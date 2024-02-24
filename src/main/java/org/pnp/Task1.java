package org.pnp;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Task1 {
    public static class EduScalaHandler implements Handler {
        private final Client client;

        public EduScalaHandler(Client client) {
            this.client = client;
        }

        @Override
        public ApplicationStatusResponse performOperation(String id) {
            AtomicInteger retry = new AtomicInteger(0);
            AtomicInteger failed = new AtomicInteger(0);
            long startTime = System.nanoTime();
            CompletionService<ClientResponse> service = new ExecutorCompletionService<>(ForkJoinPool.commonPool());

            try {
                return CompletableFuture.supplyAsync(() -> {
                    service.submit(() -> new ClientResponse(client::getApplicationStatus1, client.getApplicationStatus1(id)));
                    service.submit(() -> new ClientResponse(client::getApplicationStatus2, client.getApplicationStatus2(id)));
                    try {
                        do {
                            ClientResponse take = service.take().get();
                            if (take.response() instanceof Response.Success s) {
                                return new ApplicationStatusResponse.Success(s.applicationStatus(), s.applicationId());
                            } else if (take.response() instanceof Response.RetryAfter rf) {
                                retry.incrementAndGet();
                                service.submit(() -> {
                                    Thread.sleep(rf.delay.toMillis());
                                    return new ClientResponse(take.method, take.method.apply(id));
                                });
                            } else if (take.response() instanceof Response.Failure f) {
                                int failedMethod = failed.addAndGet(1);
                                if (failedMethod == 2) {
                                    return new ApplicationStatusResponse
                                            .Failure(Duration.ofNanos(System.nanoTime() - startTime), retry.get());
                                }
                            }
                        } while (true);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                        .orTimeout(15, TimeUnit.SECONDS)
                        .handle((o, th) -> {
                            if (th != null) {
                                return new ApplicationStatusResponse
                                        .Failure(Duration.ofNanos(System.nanoTime() - startTime), retry.get());
                            } else {
                                return o;
                            }
                        }).get();
            } catch (InterruptedException | ExecutionException e) {
                return new ApplicationStatusResponse
                        .Failure(Duration.ofNanos(System.nanoTime() - startTime), retry.get());
            }
        }
    }

    public record ClientResponse(Function<String, Response> method, Response response) {}

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