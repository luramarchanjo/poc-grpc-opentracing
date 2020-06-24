package org.example.client;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;
import io.opentracing.contrib.grpc.TracingClientInterceptor;
import io.opentracing.util.GlobalTracer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.example.grpc.CreateProduct;
import org.example.grpc.CreateProductResponse;
import org.example.grpc.ProductServiceGrpc;
import org.example.grpc.ProductServiceGrpc.ProductServiceBlockingStub;
import org.example.server.gRPCServer;

public class gRPCClient {

  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder
        .forAddress("localhost", 8080)
        .usePlaintext()
        .build();

    TracingClientInterceptor opentracingInterceptor = getOpentracingInterceptor();
    Channel opentracingChannel = opentracingInterceptor.intercept(channel);
    ProductServiceBlockingStub productService = ProductServiceGrpc.newBlockingStub(
        opentracingChannel);

    do {
      CreateProductResponse productResponse = productService.create(CreateProduct.newBuilder()
          .setName(UUID.randomUUID().toString())
          .setPrice(Math.random())
          .build());

      System.out.println("Created product " + productResponse.toString());
      TimeUnit.MILLISECONDS.sleep(1000);
    } while (true);
  }

  private static TracingClientInterceptor getOpentracingInterceptor() {
    Tracer tracer = getTracer();
    return TracingClientInterceptor.newBuilder()
        .withStreaming()
        .withVerbosity()
        .withTracer(tracer)
        .build();
  }

  private static Tracer getTracer() {
    Tracer tracer = new JaegerTracer.Builder(gRPCClient.class.getSimpleName()).build();
    GlobalTracer.registerIfAbsent(tracer);
    return tracer;
  }

}
