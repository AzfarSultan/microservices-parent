package com.azsultan.orderservice.service;

import com.azsultan.orderservice.dto.InventoryResponse;
import com.azsultan.orderservice.dto.OrderLineItemDto;
import com.azsultan.orderservice.dto.OrderRequest;
import com.azsultan.orderservice.event.OrderPlacedEvent;
import com.azsultan.orderservice.model.Order;
import com.azsultan.orderservice.model.OrderLineItems;
import com.azsultan.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Autowired
    private Tracer tracer;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
                .map(this::mapToDto).toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodeList = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        log.info("Calling Inventory Service");
        Span inventoryServiceLookup =tracer.nextSpan().name("InventoryServiceLookup");

        try (Tracer.SpanInScope spanInScope =tracer.withSpan(inventoryServiceLookup.start())){
            // Call Inventory Service, and place order if product is in
            // stock
            InventoryResponse[] inventoryResponseArr =webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory/",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodeList).build())
                    .retrieve().bodyToMono(InventoryResponse[].class)
                    .block();
            //Block used to call Synchronous request
            boolean allProductsInStock =false;
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()) );
            if (inventoryResponseArr != null) {
                allProductsInStock =Arrays.stream(inventoryResponseArr)
                        .allMatch(InventoryResponse::isInStock);
            }

            if (allProductsInStock) {
                orderRepository.save(order);
                log.info("Successfully created the orderNumber {}"+ order.getOrderNumber());
                return "Order Placed Successfully";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        }finally {
            inventoryServiceLookup.end();
        }



    }

    private OrderLineItems mapToDto(OrderLineItemDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }}
