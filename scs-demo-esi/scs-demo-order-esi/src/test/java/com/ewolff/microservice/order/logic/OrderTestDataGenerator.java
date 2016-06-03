package com.ewolff.microservice.order.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class OrderTestDataGenerator {

	@Autowired
	public OrderTestDataGenerator(OrderRepository orderRepository) {
		super();
		Order order = new Order();
		order.addLine(2, 42);
		orderRepository.save(order);
	}

}
