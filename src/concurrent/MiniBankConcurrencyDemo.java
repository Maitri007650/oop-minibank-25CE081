
package concurrent;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.SavingsAccount;

public class MiniBankConcurrencyDemo {

    public static void main(String[] args) {
        Account accountA = new SavingsAccount("Riya", 5000, 1000);
        Account accountB = new SavingsAccount("Amit", 5000, 1000);

        // 1. Thread pool transaction processing
        System.out.println("\n1. Thread Pool Transactions");

        TransactionProcessor processor = new TransactionProcessor();

        for (int i = 1; i <= 10; i++) {
            final int taskId = i;

            processor.submit(() -> {
                synchronized (accountA) {
                    try {
                        if (taskId % 2 == 0) {
                            accountA.deposit(100);
                            System.out.println("Task " + taskId
                                    + ": Deposit 100 completed.");
                        } else {
                            accountA.withdraw(50);
                            System.out.println("Task " + taskId
                                    + ": Withdrawal 50 completed.");
                        }
                    } catch (InsufficientFundsException
                             | InvalidAmountException e) {
                        System.out.println("Task " + taskId
                                + " failed: " + e.getMessage());
                    }
                }
            });
        }

        processor.stop();

        System.out.println("Account A balance: "
                + accountA.getBalance());

        // 2. Producer-consumer using wait() and notify()
        System.out.println("\n2. Producer-Consumer");

        TransactionBuffer buffer = new TransactionBuffer();

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.put(i);
                    System.out.println("Produced transaction: " + i);
                }
                buffer.put(-1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int transaction = buffer.take();

                    if (transaction == -1) {
                        break;
                    }

                    System.out.println("Processed transaction: "
                            + transaction);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. Demonstrate deadlock using separate lock objects
        System.out.println("\n3. Deadlock Demonstration");

        Object lockA = new Object();
        Object lockB = new Object();

        Thread first = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread 1 locked A");
                pause();

                synchronized (lockB) {
                    System.out.println("Thread 1 locked B");
                }
            }
        });

        Thread second = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread 2 locked B");
                pause();

                synchronized (lockA) {
                    System.out.println("Thread 2 locked A");
                }
            }
        });

        first.setDaemon(true);
        second.setDaemon(true);

        first.start();
        second.start();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println(
                "Opposite lock order can cause deadlock.");

        // 4. Fixed transfers using consistent lock ordering
        System.out.println("\n4. Deadlock Prevention");

        Thread transfer1 = new Thread(
                () -> safeTransfer(accountA, accountB, 100));

        Thread transfer2 = new Thread(
                () -> safeTransfer(accountB, accountA, 200));

        transfer1.start();
        transfer2.start();

        try {
            transfer1.join();
            transfer2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final Account A balance: "
                + accountA.getBalance());
        System.out.println("Final Account B balance: "
                + accountB.getBalance());
        System.out.println("Safe transfers completed.");
    }

    private static void safeTransfer(
            Account from, Account to, long amount) {

        Account first;
        Account second;

        if (from.getAccountNumber().compareTo(
                to.getAccountNumber()) < 0) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {
                try {
                    from.withdraw(amount);
                    to.deposit(amount);

                    System.out.println("Transferred " + amount
                            + " from " + from.getAccountNumber()
                            + " to " + to.getAccountNumber());
                } catch (InsufficientFundsException
                         | InvalidAmountException e) {
                    System.out.println("Transfer failed: "
                            + e.getMessage());
                }
            }
        }
    }

    private static void pause() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static class TransactionBuffer {
        private int value;
        private boolean available = false;

        public synchronized void put(int number)
                throws InterruptedException {
            while (available) {
                wait();
            }

            value = number;
            available = true;
            notify();
        }

        public synchronized int take()
                throws InterruptedException {
            while (!available) {
                wait();
            }

            int number = value;
            available = false;
            notify();

            return number;
        }
    }
}
