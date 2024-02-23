package edu.school21.app;

import edu.school21.client.Client;

public class Main {
    public static void main(String[] args) {
        Client client = new Client("localhost", getPort(args));
        client.run();
    }

    private static int getPort(String[] args){
        if(args.length != 1 || !args[0].startsWith("--server-port=")){
            System.err.println("Please write port in arguments.");
            System.exit(-1);
        }
        return Integer.parseInt(args[0].split("=")[1]);
    }
}