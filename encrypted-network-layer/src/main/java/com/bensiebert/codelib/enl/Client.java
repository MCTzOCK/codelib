package com.bensiebert.codelib.enl;

import com.bensiebert.codelib.enl.crypto.EncryptedComponent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.Function;

public class Client extends EncryptedComponent {

    public Socket socket;
    public BufferedReader in;
    public PrintStream out;
    public PublicKey serverPubKey;
    public Function<String, Void> onMessage;
    public Function<Client, Void> onLoad;

    public Client(String host, int port, Function<Client, Void> onLoad, Function<String, Void> onMessage) {
        super();

        this.onLoad = onLoad;
        this.onMessage = onMessage;

        try {
            this.socket = new Socket(host, port);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintStream(this.socket.getOutputStream());

            this.handle();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handle() {
        Thread t = new Thread(() -> {
            try {
                // unencrypted key exchange
                String l = in.readLine().replaceAll("\n", "");
                byte[] serverPubKeyBytes = Base64.getDecoder().decode(l.getBytes(StandardCharsets.UTF_8));
                serverPubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(serverPubKeyBytes));

                out.println(Base64.getEncoder().encodeToString(this.publicKey.getEncoded()));
                out.flush();

                this.onLoad.apply(this);
                // Encrypted from here on out
                while (!this.socket.isClosed()) {
                    /*String raw = this.in.readLine().replaceAll("\n", "");
                    String decrypted = this.decryptMessage(raw);
                    this.onMessage.apply(decrypted);*/
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.setName("Client Process");
        t.start();
    }

    public void close() {
        try {
            this.socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendUnencrypted(String message) {
        try {
            this.out.println(message);
            this.out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String message) {
        this.sendUnencrypted(this.encryptMessage(message, serverPubKey).toString());
    }

    public String getResponse() {
        try {
            return this.decryptMessage(this.in.readLine().replaceAll("\n", ""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
