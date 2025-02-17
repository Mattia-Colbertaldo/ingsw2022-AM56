package it.polimi.ingsw.Server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import it.polimi.ingsw.Enum.Errors;
import it.polimi.ingsw.Message.ClientMessage;
import it.polimi.ingsw.Message.ClientMessageDecorator;
import it.polimi.ingsw.Message.Message;
import it.polimi.ingsw.Message.ModelMessage.ModelMessage;
import it.polimi.ingsw.Message.ModelMessage.ModelMessageBuilder;
import it.polimi.ingsw.Server.Model.Game;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The creator and manager of the model server side
 */
public class ModelHandler implements Runnable{

    private final Server server;
    private final Game model;
    private final QueueOrganizer queues;
    private final PersistenceAssistant persistenceAssistant;
    private final int[] ids;
    private final Gson gson = new Gson();

    private Thread thread = null;

    /**
     * Constructor of the class
     *
     * @param ids       Ids of the players
     * @param gameMode  game mode to that the right type of game
     * @param server    Server main Thread
     * @param q         the Queue Organizer
     * @param usernames usernames used for create the sign of this game and could save it on the disk
     */
    public ModelHandler(int[] ids, int gameMode, Server server, QueueOrganizer q, Map<Integer, String> usernames) {
        this.server = server;
        this.queues = q;
        this.ids = ids;

        this.persistenceAssistant = new PersistenceAssistant(usernames, gameMode);

        if(this.persistenceAssistant.modelAvailable()){
            this.model = this.persistenceAssistant.getResumedModel();
            Message temp = this.persistenceAssistant.getResumedModelMessage();
            Instant time = temp.getTime();
            SimpleDateFormat d = new SimpleDateFormat("dd MMMM yyyy - HH:mm:ss - z");
            temp = temp.setError(Errors.MODEL_RESUMED).setMessage("Model resumed from: " + d.format(java.util.Date.from(time)));
            sendMessageToPlayers(this.gson.toJsonTree(temp));
        }
        else {
            this.model = Game.getGameModel(ids, gameMode);
        }
    }

    /**
     * stop the model in 500 millisecond and take at least one other message and send one other message to the players
     */
    public void stopModel () {
        this.thread.interrupt();
    }

    /**
     * Thread of the Model
     *
     * First it send to all the player the first model message.
     * Then it waits for some move from the players and execute them, resending to player the result.
     *
     * Finally start again to wait for some moves from players until another thread set to stop, or the game finish.
     */
    @Override
    public void run() {

        this.thread = Thread.currentThread();

        //first send to player the first model message
        ModelMessage m = ModelMessageBuilder.getModelMessageBuilder().buildModelMessage(Errors.NO_ERROR);
        JsonElement message = this.gson.toJsonTree(m);

        if (!this.thread.isInterrupted()){
            sendMessageToPlayers(message);
        }
        //then wait for player move

        while (!this.thread.isInterrupted()){

            ClientMessageDecorator move;
            try {
                move = queues.getModelQueue().poll(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for some move from clients ModelHandlers");
                this.thread.interrupt();
                break;
            }
            if (move == null)
                continue;

            int playerId = move.playerId();
            ClientMessage moveReceived = move.message();

            if (!this.thread.isInterrupted()) {
                int error = moveReceived.executeMove(this.model, playerId);
                updateClients(error, playerId);
            }
        }
    }
    private void updateClients(int errorCode, int playerId) {

        JsonElement message;
        Errors er = Errors.getErrorsByCode(errorCode);



        if (Errors.NO_ERROR.equals(er)) {
            //if no error the model is updated
            ModelMessage m = ModelMessageBuilder.getModelMessageBuilder().buildModelMessage(er);

            //if the game is over communicates it to the main server thread
            if (!this.thread.isInterrupted()) {
                if (m.gameIsOver()){
                    this.persistenceAssistant.gameOver();
                    server.setCode(Errors.GAME_OVER);
                    stopModel();
                }
                else{
                    this.persistenceAssistant.updateModelFile(m);
                }
            }

            message = gson.toJsonTree(m);
        }
        else {
            //if error return the message

            Message m = new Message(er, "The player " + playerId + " commit an error: " + er.getDescription());

            message = gson.toJsonTree(m);
        }

        sendMessageToPlayers(message);
    }

    private void sendMessageToPlayers (JsonElement m){
        //send message to all players

        boolean interrupted = false;
        for (int id: this.ids){
            boolean done = false;
            while (!done) {
                try {
                    this.queues.getPlayerQueue(id).put(m);
                    done = true;
                } catch (InterruptedException e){
                    interrupted = true;
                } //this action need to be done for all player in any case if it is started
            }
        }

        if (interrupted)
            stopModel();
    }
}
