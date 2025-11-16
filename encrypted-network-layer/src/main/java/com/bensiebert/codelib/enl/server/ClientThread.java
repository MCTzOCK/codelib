package com.bensiebert.codelib.enl.server;

import com.bensiebert.codelib.enl.Server;

import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.Function;

public class ClientThread {

    private Socket socket;
    private Server server;
    private PublicKey clientPubKey;

    public ClientThread(Socket s, Server server) {
        this.socket = s;
        this.server = server;

        try {
            this.handle();
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void handle() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        PrintStream out = new PrintStream(this.socket.getOutputStream());

        out.println(Base64.getEncoder().encodeToString(server.publicKey.getEncoded()));
        out.flush();

        try {
            String l = in.readLine().replaceAll("\n", "");
            byte[] clientPubKeyBytes = Base64.getDecoder().decode(l);
            clientPubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(clientPubKeyBytes));
        } catch (Exception e) {
            System.out.println("Error getting client public key: " + e.getMessage());
            out.println("Error getting your public key: " + e.getMessage());
            out.flush();
            this.socket.close();
            return;
        }

        while(!this.socket.isClosed()) {
            String raw = in.readLine().replaceAll("\n", "");
            if(raw.trim().isEmpty() || raw.equals("\n")) {
                continue;
            }
            String decrypted = this.server.decryptMessage(raw);
            String[] parts = decrypted.split(" ");
            String command = parts[0].toLowerCase();
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, parts.length - 1);

            System.out.println("Received command: " + command);

            if(command.equals("exit")) {
                this.socket.close();
                return;
            } else {
                Command c = this.server.commands.get(command);
                if(c != null) {
                    c.execute(this, args);
                } else {
                    out.println("Unknown command: " + command);
                    out.flush();
                }
            }

        }
    }

    public void send(String message) {
        try {
            PrintStream out = new PrintStream(this.socket.getOutputStream());
            out.println(this.server.encryptMessage(message, clientPubKey).toString());
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

}

