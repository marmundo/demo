package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação.
 *
 * @SpringBootApplication combina três anotações:
 *  - @Configuration: a classe pode declarar beans;
 *  - @EnableAutoConfiguration: o Spring Boot configura sozinho o que encontra no classpath
 *    (servidor web, JPA, banco H2 etc.);
 *  - @ComponentScan: procura classes anotadas (@RestController, @Service, @Repository...)
 *    neste pacote e nos subpacotes, e as registra no contêiner do Spring.
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // Sobe o contêiner do Spring e o servidor web embutido (Tomcat, porta 8080 por padrão)
        SpringApplication.run(DemoApplication.class, args);
    }

}
