package ru.nsu.nikolotov;

import ru.nsu.nikolotov.SelfFinder.SelfFinder;

public class Main {
    static public void main(String[] args){
        if (args.length < 1) {
            System.out.println("Use multicast address as an arg");
            System.exit(1);
        }
        String multicastAddress = args[0];
        SelfFinder finder = new SelfFinder(multicastAddress);
        finder.findSelfCopies();
    }
}

