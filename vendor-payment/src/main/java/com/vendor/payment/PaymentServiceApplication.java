package com.vendor.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PaymentServiceApplication {

	public static void main(String[] args) throws InterruptedException, IOException {
		//
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
