package it.polimi.ingsw.Server.Model.Characters;
import it.polimi.ingsw.Enum.Errors;
import it.polimi.ingsw.Message.ModelMessage.CharacterSerializable;
import it.polimi.ingsw.Server.Model.GameInitializer;

final public class Knight extends Character {

    Knight(GameInitializer gameInitializer) {
        super (7, 2, gameInitializer, "Knight");
    }

    public Knight(GameInitializer gameInitializer, CharacterSerializable character) {
        super(gameInitializer, character);
    }

    @Override
    protected void activateEffect(int[] object) {
        // calcInfluence() method already implemented in GameBoard
    }

    @Override
    public Errors canActivateEffect(int[] obj) {
        // no further checks needed
        return Errors.NO_ERROR;
    }
}
