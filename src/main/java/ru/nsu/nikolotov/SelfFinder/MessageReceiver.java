package ru.nsu.nikolotov.SelfFinder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class MessageReceiver extends Thread{
    private final HashMap<String, Integer> delayBetweenMessages = new HashMap<String, Integer>();
    private final String multicastAddress;
    private final int multicastPort;
    private  int timePiecesBeforeDelete = 10;
    private final int timeToSleepInMillis = 100;
    private final int bufSize = 256;
    private boolean keepRunning = true;
    MessageReceiver(String multicastAddress, int multicastPort){
        this.multicastPort = multicastPort;
        this.multicastAddress = multicastAddress;
    }
    private void deleteWhoTimeouted(){
        delayBetweenMessages.entrySet().removeIf(entry -> entry.getValue() > timePiecesBeforeDelete);
    }

    private void processMessage(String message){
        if (message.split(" ")[0].equals("DISCONNECT")){
            String id = message.split(" ")[1];
            delayBetweenMessages.entrySet().removeIf(entry -> entry.getKey().equals(id.trim()));
            //trim нужен, чтобы убрать лидирующие нули у строки и сделать их равными по длине.
            //Иначе у нас будут две одинаковые строки, но с разными по длине байтовыми массивами,
            //и поэтому equals() вернет false
        } else {
            delayBetweenMessages.forEach((k, v) -> delayBetweenMessages.put(k, v + 1));
            delayBetweenMessages.put(message.trim(), 0);
            timePiecesBeforeDelete = Math.max(timePiecesBeforeDelete, delayBetweenMessages.size());
            //timePiecesBeforeDelete не должен быть меньше размеров мапы
        }
    }

    @Override
    public void run() {
        try {
            var multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.joinGroup(InetAddress.getByName(multicastAddress));
            while(keepRunning) {
                var receivedDatagram = new DatagramPacket(new byte[bufSize], bufSize);
                multicastSocket.receive(receivedDatagram);
                String message = new String(receivedDatagram.getData());
                processMessage(message);
                deleteWhoTimeouted();
                System.out.println("Current count of applications: " + delayBetweenMessages.size());
                Thread.sleep(timeToSleepInMillis);
            }
            multicastSocket.leaveGroup(InetAddress.getByName(multicastAddress));
            multicastSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void stopRunning(){
        keepRunning = false;
    }
}
