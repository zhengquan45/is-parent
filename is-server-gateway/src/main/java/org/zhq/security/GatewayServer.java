package org.zhq.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class GatewayServer {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServer.class,args);
    }
}
