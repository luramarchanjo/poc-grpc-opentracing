package org.example.server;

import io.grpc.stub.StreamObserver;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.example.Product;
import org.example.grpc.CreateProduct;
import org.example.grpc.CreateProductResponse;
import org.example.grpc.ProductServiceGrpc.ProductServiceImplBase;

public class ProductService extends ProductServiceImplBase {

  private Map<UUID, Product> products = new HashMap<>();

  @Override
  public void create(CreateProduct request,
      StreamObserver<CreateProductResponse> responseObserver) {

    UUID id = UUID.randomUUID();
    String name = request.getName();
    BigDecimal price = BigDecimal.valueOf(request.getPrice());
    final Product product = new Product(id, name, price);

    System.out.println("Creating product " + product.toString());
    products.putIfAbsent(id, product);

    responseObserver.onNext(CreateProductResponse.newBuilder()
        .setId(id.toString())
        .setName(name)
        .setPrice(price.doubleValue())
        .build());

    responseObserver.onCompleted();
  }

}
