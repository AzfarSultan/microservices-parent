package com.azsultan.inventoryservice;

import com.azsultan.inventoryservice.model.Inventory;
import com.azsultan.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){

		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("Iphone 14");
			inventory.setQuantity(123);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("Iphone 14_Green");
			inventory1.setQuantity(355);

			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("Iphone 14_Blue");
			inventory2.setQuantity(0);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);
			inventoryRepository.save(inventory2);
		};
	}

}
