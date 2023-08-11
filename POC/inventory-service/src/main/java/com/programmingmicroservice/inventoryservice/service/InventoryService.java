package com.programmingmicroservice.inventoryservice.service;

import com.programmingmicroservice.inventoryservice.dtos.InventoryResponse;
import com.programmingmicroservice.inventoryservice.model.Inventory;
import com.programmingmicroservice.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

//    @SneakyThrows
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodeList){
//        log.info("wait started");
//        Thread.sleep(10000);
//        log.info("wait ended");

        List<InventoryResponse> inventoryResponses = inventoryRepository.findBySkuCodeIn(skuCodeList)
                .stream()
                .map(inventory -> {
                    return InventoryResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build();
                }).collect(Collectors.toList());
        log.info("Inventory Responses {}",inventoryResponses);
        return inventoryResponses;
    }
}
