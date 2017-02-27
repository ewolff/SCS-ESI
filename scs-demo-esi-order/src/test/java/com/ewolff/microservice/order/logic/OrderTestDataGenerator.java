package com.ewolff.microservice.order.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@DependsOn("itemInitializer")
public class OrderTestDataGenerator {

	@Autowired
	public OrderTestDataGenerator(OrderRepository orderRepository, ItemRepository itemRepository) {
		super();
		Order order = new Order();
		order.addLine(2, itemRepository.findByName("iPod").get(0).getItemId());
		orderRepository.save(order);
	}

}
