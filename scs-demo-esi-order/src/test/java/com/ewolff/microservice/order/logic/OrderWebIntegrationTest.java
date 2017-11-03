package com.ewolff.microservice.order.logic;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.ewolff.microservice.order.OrderApp;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OrderApp.class)
@ActiveProfiles("test")
public class OrderWebIntegrationTest {

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${local.server.port}")
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
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(orderURL() + "/form", String.class);
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
		URI uri = restTemplate.postForLocation(orderURL(), map, String.class);
		UriTemplate uriTemplate = new UriTemplate(orderURL() + "/{id}");
		assertEquals(before + 1, orderRepository.count());
	}
}
