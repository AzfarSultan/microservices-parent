package com.azsultan.inventoryservice.controller;

import com.azsultan.inventoryservice.dto.InventoryResponse;
import com.azsultan.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;


    //  http://localhost:8082/api/inventory?skuCode=Iphone 14_bLUe&skuCode=Iphone 14_Green
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }
}
