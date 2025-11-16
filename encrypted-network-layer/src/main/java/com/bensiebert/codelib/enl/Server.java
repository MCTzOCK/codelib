package com.bensiebert.codelib.enl;

import com.bensiebert.codelib.enl.crypto.EncryptedComponent;
import com.bensiebert.codelib.enl.server.ClientThread;
import com.bensiebert.codelib.enl.server.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server extends EncryptedComponent {

    public ServerSocket server;
    public HashMap<String, Command> commands = new HashMap<>();

    public Server(int port, Command[] commands) {
        super();
        for (Command c : commands) {
            this.commands.put(c.command, c);
        }

        try {
            server = new ServerSocket(port);
            Thread t = new Thread(this::listen);
            t.setName("Server Listener");
            t.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server started on port " + port);
    }

    public void listen() {
        while (!server.isClosed()) {
            try {
                Socket s = server.accept();
                System.out.println("Accepted connection from " + s.getInetAddress().getHostAddress());

                Thread t = new Thread(() -> new ClientThread(s, this));
                t.setName("Client Thread " + s.getInetAddress().getHostAddress());
                t.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}


