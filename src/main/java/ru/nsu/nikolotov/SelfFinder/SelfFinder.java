package ru.nsu.nikolotov.SelfFinder;

import java.util.Scanner;
import java.util.UUID;

public class SelfFinder {
    private final String multicastAddress;
    private final int port = 8081;
    String uniqueID;
    public SelfFinder(String multicastAddress){
        this.multicastAddress = multicastAddress;
        uniqueID =  UUID.randomUUID().toString();

    }
    public void findSelfCopies(){
        MessageSender sender = new MessageSender(uniqueID, multicastAddress, port);
        MessageReceiver receiver = new MessageReceiver(multicastAddress, port);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press any key to stop application");
        sender.start();
        receiver.start();
        scanner.nextLine();
        System.out.println("Stopping application...");
        sender.stopRunning();
        receiver.stopRunning();
    }
}

