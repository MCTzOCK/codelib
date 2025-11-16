package com.bensiebert.codelib.enl.server;

import java.net.Socket;

public abstract class Command {
    public String command;

    public Command(String command) {
        this.command = command;
    }

    public abstract void execute(ClientThread client, String[] args);
}
