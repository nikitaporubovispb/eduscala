package org.pnp;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Task2 {
    public static class EduScalaHandler implements Handler {
        private final Client client;
        private final BlockingQueue<Event> eventsQueue = new LinkedBlockingQueue<>();
        private final Executor executor = ForkJoinPool.commonPool();

        public EduScalaHandler(Client client) {
            this.client = client;
        }

        @Override
        public Duration timeout() {
            return Duration.ZERO;
        }

        @Override
        public void performOperation() {
            AtomicBoolean canceled = new AtomicBoolean(false);
            executor.execute(() -> {
                while (!canceled.get()) {
                    try {
                        Event event = eventsQueue.take();
                        for (Address recipient : event.recipients) {
                            executor.execute(() -> {
                                Result sendResult = client.sendData(recipient, event.payload());
                                if (sendResult == Result.REJECTED) {
                                    try {
                                        Thread.sleep(timeout().toMillis());
                                        eventsQueue.put(new Event(List.of(recipient), event.payload()));
                                    } catch (InterruptedException e) {
                                        canceled.set(true);
                                    }
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        canceled.set(true);
                    }
                }
            });
            executor.execute(() -> {
                while (!canceled.get()) {
                    eventsQueue.add(client.readData());
                }
            });
        }
    }

    public record Payload(String origin, byte[] data) {}
    public record Address(String datacenter, String nodeId) {}
    public record Event(List<Address> recipients, Payload payload) {}

    public enum Result { ACCEPTED, REJECTED }

    public interface Client {
        //блокирующий метод для чтения данных
        Event readData();

        //блокирующий метод отправки данных
        Result sendData(Address dest, Payload payload);
    }

    public interface Handler {
        Duration timeout();

        void performOperation();
    }
}