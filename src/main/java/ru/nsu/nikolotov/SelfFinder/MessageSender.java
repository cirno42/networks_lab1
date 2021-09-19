package ru.nsu.nikolotov.SelfFinder;

import java.io.IOException;
import java.net.*;

public class MessageSender extends Thread{
    private boolean keepRunning = true;
    private DatagramPacket datagramToSend;
    private DatagramSocket udpSocket;
    private DatagramPacket disconnectDatagram;
    private final int delayBetweenMessages = 1000;
    public MessageSender(String messageToSend, String multicastAddress, int port){
        try {
            datagramToSend = new DatagramPacket(messageToSend.getBytes(),
                    messageToSend.getBytes().length,
                    InetAddress.getByName(multicastAddress),
                    port);
            var disconnectMessage = "DISCONNECT " + messageToSend;
            disconnectDatagram = new DatagramPacket(disconnectMessage.getBytes(),
                    disconnectMessage.getBytes().length,
                    InetAddress.getByName(multicastAddress),
                    port);
            udpSocket = new DatagramSocket();
        } catch (UnknownHostException | SocketException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    @Override
    public  void run() {
        try {
            while (keepRunning) {
                udpSocket.send(datagramToSend);
                Thread.sleep(delayBetweenMessages);
            }
            udpSocket.send(disconnectDatagram);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(1);
        }
        udpSocket.close();
    }
    public void stopRunning(){
        keepRunning = false;
    }
}
