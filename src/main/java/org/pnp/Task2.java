package org.pnp;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class Task2 {
    public static class EduScalaHandler implements Handler {
        private final Client client;

        public EduScalaHandler(Client client) {
            this.client = client;
        }

        @Override
        public Duration timeout() {
            return Duration.ZERO;
        }

        @Override
        public void performOperation() {
            ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            CompletionService<RecipientResult> service = new ExecutorCompletionService<>(threadPool);
            Event event = client.readData();
            for (Address recipient : event.recipients) {
                service.submit(() -> new RecipientResult(recipient, client.sendData(recipient, event.payload())));
            }
            int acceptedCount = 0;
            try {
                do {
                    RecipientResult take = service.take().get();
                    if (take.result() == Result.ACCEPTED) {
                        acceptedCount++;
                    } else {
                        service.submit(() -> new RecipientResult(take.recipient, client.sendData(take.recipient, event.payload())));
                    }
                } while (acceptedCount != event.recipients.size());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            awaitTerminationAfterShutdown(threadPool);
        }
    }

    public static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public record RecipientResult(Address recipient, Result result) {}

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