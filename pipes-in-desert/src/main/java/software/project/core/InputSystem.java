package software.project.core;

import software.project.models.ActiveElement;
import software.project.models.Cistern;
import software.project.models.Element;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.models.Pump;

public class InputSystem {
    public void startNewGameCommand(Game game) {
        System.out.println("[InputSystem] startNewGameCommand()");
        game.startGame();
    }

    public void initiateConfiguration() {
        System.out.println("[InputSystem] initiateConfiguration()");
    }

    public void pauseCommand(Game game) {
        System.out.println("[InputSystem] pauseCommand()");
        game.pauseGame();
    }

    public void resumeCommand(Game game) {
        System.out.println("[InputSystem] resumeCommand()");
        game.resumeGame();
    }

    public void selectTargetAdjacentElement(Element targetElement) {
        System.out.println("[InputSystem] selectTargetAdjacentElement(" + targetElement + ")");
    }

    public void selectPipe(Pipe targetPipe) {
        System.out.println("[InputSystem] selectPipe(" + targetPipe + ")");
    }

    public void selectFreePipeEnd(PipeEnd freeEnd) {
        System.out.println("[InputSystem] selectFreePipeEnd(" + freeEnd + ")");
    }

    public void selectTargetElement(ActiveElement targetElement) {
        System.out.println("[InputSystem] selectTargetElement(" + targetElement + ")");
    }

    public void selectPump(Pump targetPump) {
        System.out.println("[InputSystem] selectPump(" + targetPump + ")");
    }

    public void selectInputPipe(Pipe inputPipe) {
        System.out.println("[InputSystem] selectInputPipe(" + inputPipe + ")");
    }

    public void selectOutputPipe(Pipe outputPipe) {
        System.out.println("[InputSystem] selectOutputPipe(" + outputPipe + ")");
    }

    public void selectDamagedPipe(Pipe targetPipe) {
        System.out.println("[InputSystem] selectDamagedPipe(" + targetPipe + ")");
    }

    public void selectCistern(Cistern sourceCistern) {
        System.out.println("[InputSystem] selectCistern(" + sourceCistern + ")");
    }

    public void selectBrokenPump(Pump targetPump) {
        System.out.println("[InputSystem] selectBrokenPump(" + targetPump + ")");
    }
}
