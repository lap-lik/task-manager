package com.sber.finalsberproject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class FinalSberProjectApplication implements CommandLineRunner {

    @Value("${server.port}")
    private String serverPort;


    public static void main(String[] args) {
        SpringApplication.run(FinalSberProjectApplication.class, args);
    }

    public void run(String... args) throws Exception{
        System.out.println("Application path: http://localhost:" + serverPort);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode("user1");
        System.out.println(hashedPassword);
    }
}
