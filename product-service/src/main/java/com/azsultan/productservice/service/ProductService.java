package com.azsultan.productservice.service;

import com.azsultan.productservice.dto.ProductRequest;
import com.azsultan.productservice.dto.ProductResponse;
import com.azsultan.productservice.model.Product;
import com.azsultan.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){

        Product product = Product.builder().name(productRequest.getName()).
                descriptions(productRequest.getDescriptions())
                .price(productRequest.getPrice()).build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product>  products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }
    public ProductResponse mapToProductResponse(Product product){
        return ProductResponse.builder().id(product.getId()).name(product.getName())
                .descriptions(product.getDescriptions()).price(product.getPrice()).build();
    }

}
