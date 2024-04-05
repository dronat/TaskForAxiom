package ru.axiomjdk;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("useDeadLock boolean argument is missed");
        }
        boolean useDeadLock = Boolean.parseBoolean(args[0]);

        Runner firstRunner = new Runner(useDeadLock);
        Runner secondRunner = new Runner(useDeadLock);

        Thread firstThread = new Thread(() -> firstRunner.firstMethod(secondRunner));
        Thread secondThread = new Thread(() -> secondRunner.secondMethod(firstRunner));

        firstThread.start();
        secondThread.start();
    }
}