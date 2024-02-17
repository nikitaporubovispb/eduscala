package org.pnp;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Task1 {
    public static void main(String[] args) {
        System.out.println("EduScalaHandler");
    }

    public static class EduScalaHandler implements Task1.Handler {
        private final Task1.Client client;

        public EduScalaHandler(Task1.Client client) {
            this.client = client;
        }

        @Override
        public Task1.ApplicationStatusResponse performOperation(String id) {
            AtomicInteger retry = new AtomicInteger(1);
            long startTime = System.nanoTime();
            CompletableFuture<Task1.Response> request1 = CompletableFuture.supplyAsync(() -> client.getApplicationStatus1(id));
            CompletableFuture<Task1.Response> request2 = CompletableFuture.supplyAsync(() -> client.getApplicationStatus1(id));

            CompletableFuture<Task1.ApplicationStatusResponse> handle = CompletableFuture.anyOf(request1, request2)
                    .orTimeout(15, TimeUnit.SECONDS)
                    .handle((o, throwable) -> handle(o, throwable, new Task1.ApplicationStatusResponse.Failure(
                            Duration.of(System.nanoTime() - startTime, ChronoUnit.NANOS), retry.get())));
            try {
                return handle.get();
            } catch (InterruptedException | ExecutionException e) {
                return new Task1.ApplicationStatusResponse.Failure(null, retry.get());
            }
        }

        private static Task1.ApplicationStatusResponse handle(Object o, Throwable throwable, Task1.ApplicationStatusResponse.Failure failure) {
            if (throwable != null) {
                return failure;
            } else {
                return switch (o) {
                    case Task1.Response.Success s ->
                            new Task1.ApplicationStatusResponse.Success(s.applicationStatus, s.applicationId);
                    case Task1.Response.RetryAfter ra -> failure;
                    case Task1.Response.Failure f -> failure;
                    default -> failure;
                };
            }
        }
    }

    public sealed interface Response {
        record Success(String applicationStatus, String applicationId) implements Response {}
        record RetryAfter(Duration delay) implements Response {}
        record Failure(Throwable ex) implements Response {}
    }

    public sealed interface ApplicationStatusResponse {
        record Failure(@Nullable Duration lastRequestTime, int retriesCount) implements ApplicationStatusResponse {}
        record Success(String id, String status) implements ApplicationStatusResponse {
        }
    }

    public interface Handler {
        ApplicationStatusResponse performOperation(String id);
    }

    public interface Client {
        //блокирующий вызов сервиса 1 для получения статуса заявки
        Response getApplicationStatus1(String id);

        //блокирующий вызов сервиса 2 для получения статуса заявки
        Response getApplicationStatus2(String id);

    }
}