package org.zhq.security.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

//    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public OrderInfo create(@RequestBody OrderInfo orderInfo, @RequestHeader String username){
        log.info("username is "+username);
//        PriceInfo priceInfo = restTemplate.getForObject("http://localhost:9080/prices/"+orderInfo.getProductId(),PriceInfo.class);
        return orderInfo;
    }

    @GetMapping("/{id}")
    public OrderInfo getInfo(@PathVariable Long id){
        log.info("orderId is "+id);
        return new OrderInfo();
    }
}
