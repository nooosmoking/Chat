package edu.school21.app;

import edu.school21.config.SocketsApplicationConfig;
import edu.school21.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 3002; i++) {
            System.out.print("s");
        }
        ApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
        Server server = context.getBean(Server.class);
        server.setPort(getPort(args));
        server.run();
    }

    private static int getPort(String[] args){
        if(args.length != 1 || !args[0].startsWith("--port=")){
            System.err.println("Please write port in arguments.");
            System.exit(-1);
        }
        return Integer.parseInt(args[0].split("=")[1]);
    }
}
