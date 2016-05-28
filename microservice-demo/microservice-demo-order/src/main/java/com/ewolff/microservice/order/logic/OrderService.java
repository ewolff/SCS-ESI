package com.ewolff.microservice.order.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class OrderService {

	private OrderRepository orderRepository;

	@Autowired
	private OrderService(OrderRepository orderRepository) {
		super();
		this.orderRepository = orderRepository;
	}

	public Order order(Order order) {
		if (order.getNumberOfLines() == 0) {
			throw new IllegalArgumentException("No order lines!");
		}
		return orderRepository.save(order);
	}

}
