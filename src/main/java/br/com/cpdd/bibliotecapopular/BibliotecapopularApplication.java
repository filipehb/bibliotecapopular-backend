package br.com.cpdd.bibliotecapopular;

import br.com.cpdd.bibliotecapopular.auth.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class BibliotecapopularApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecapopularApplication.class, args);
    }

}
