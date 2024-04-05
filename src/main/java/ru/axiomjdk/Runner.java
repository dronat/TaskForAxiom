package ru.axiomjdk;

public class Runner {
    private final Boolean useDeadLock;
    private static final Object locker = new Object();
    private ThreadLocal<Integer> i = ThreadLocal.withInitial(() -> 1);

    public Runner(boolean useDeadLock) {
        this.useDeadLock = useDeadLock;
    }

    public void firstMethod(Runner runner) {
        if (useDeadLock) {
            thirdMethod(runner);
        } else {
            synchronized (locker) {
                System.out.println(Thread.currentThread().getName() + " blocked monitor for locker");
                thirdMethod(runner);
                System.out.println(Thread.currentThread().getName() + " unblocked monitor for locker");
            }
        }
    }

    public void secondMethod(Runner runner) {
        if (useDeadLock) {
            fourthMethod(runner);
        } else {
            synchronized (locker) {
                System.out.println(Thread.currentThread().getName() + " blocked monitor for locker");
                fourthMethod(runner);
                System.out.println(Thread.currentThread().getName() + " unblocked monitor for locker");
            }
        }
    }

    private synchronized void thirdMethod(Runner runner) {
        System.out.println(Thread.currentThread().getName() + " entered in third method");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (i.get() >= 10) {
            return;
        }
        i.set(i.get() + 1);
        runner.fourthMethod(this);
        System.out.println(Thread.currentThread().getName() + " exited from third method");
    }

    private synchronized void fourthMethod(Runner runner) {
        System.out.println(Thread.currentThread().getName() + " entered in fourth method");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (i.get() >= 10) {
            return;
        }
        i.set(i.get() + 1);
        runner.thirdMethod(this);
        System.out.println(Thread.currentThread().getName() + " exited from fourth method");
    }
}
