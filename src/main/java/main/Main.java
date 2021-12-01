package main;

import command.RunCommand;
import framework.application.Application;
import framework.state.ApplicationState;
import state.LaboratoryState;

public class Main {

    public static void main(String[] args) {
        ApplicationState state = new LaboratoryState();
        Application application = new Application.ApplicationBuilder(state)
                .addCommand(new RunCommand())
                .build();
        application.start();
    }

}
