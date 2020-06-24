package org.example.server;

import io.grpc.ServerBuilder;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;
import io.opentracing.contrib.grpc.TracingServerInterceptor;
import io.opentracing.util.GlobalTracer;
import java.io.IOException;

public class gRPCServer {

  public static void main(String[] args) throws IOException, InterruptedException {
    ProductService productService = new ProductService();
    TracingServerInterceptor serverInterceptor = getOpentracingInterceptor();

    System.out.println("Server Started");
    ServerBuilder
        .forPort(8080)
        .addService(serverInterceptor.intercept(productService))
        .build()
        .start()
        .awaitTermination();
  }

  private static TracingServerInterceptor getOpentracingInterceptor() {
    Tracer tracer = getTracer();
    return TracingServerInterceptor.newBuilder()
        .withStreaming()
        .withVerbosity()
        .withTracer(tracer)
        .build();
  }

  private static Tracer getTracer() {
    Tracer tracer = new JaegerTracer.Builder(gRPCServer.class.getSimpleName()).build();
    GlobalTracer.registerIfAbsent(tracer);
    return tracer;
  }

}
