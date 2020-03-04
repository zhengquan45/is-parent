package org.zhq.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class IsPriceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IsPriceApiApplication.class, args);
    }

}
