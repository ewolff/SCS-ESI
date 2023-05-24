package com.ewolff.microservice.order.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ewolff.microservice.order.OrderApp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OrderApp.class)
@ActiveProfiles("test")
public class OrderWebIntegrationTest {

	private RestTemplate restTemplate = new RestTemplate();

	@LocalServerPort
	private long serverPort;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	public void IsOrderListReturned() {
		try {
			ResponseEntity<String> resultEntity = restTemplate.getForEntity(orderURL(), String.class);
			assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
			String orderList = resultEntity.getBody();
			assertFalse(orderList.contains("Eberhard"));
			Order order = new Order();
			order.addLine(2, 42);
			orderRepository.save(order);
			orderList = restTemplate.getForObject(orderURL(), String.class);
			assertTrue(orderList.contains("href")); // if the order is
													// processed, a link and a
													// form for delete should be
													// rendered
			assertTrue(orderList.contains("form"));
		} finally {
			orderRepository.deleteAll();
		}
	}

	private String orderURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void IsOrderFormDisplayed() {
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(orderURL() + "/form.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<form"));
	}

	@Test
	@Transactional
	public void IsSubmittedOrderSaved() {
		long before = orderRepository.count();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("submit", "");
		map.add("orderLine[0].itemId", "42");
		map.add("orderLine[0].count", "2");
		restTemplate.postForLocation(orderURL(), map, String.class);
		assertEquals(before + 1, orderRepository.count());
	}
}
