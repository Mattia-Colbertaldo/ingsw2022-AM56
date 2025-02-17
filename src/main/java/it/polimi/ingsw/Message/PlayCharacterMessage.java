package it.polimi.ingsw.Message;

import it.polimi.ingsw.Enum.Errors;
import it.polimi.ingsw.Server.Model.Game;

/**
 * message that can execute a charcter play
 */
public final class PlayCharacterMessage extends ClientMessage{

    private final int characterId;
    private final int[] attributes;

    public PlayCharacterMessage(Errors er, String message, int characterId, int[] attributes) {
        super(er, message, 5);
        this.characterId = characterId;
        this.attributes = attributes;
    }

    @Override
    public int executeMove(Game game, int playerId) {
        return game.playCharacter(playerId, characterId, attributes);
    }
}
