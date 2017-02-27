package com.ewolff.microservice.order.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemInitializer {

	@Autowired
	public ItemInitializer(ItemRepository itemRepository) {
		itemRepository.save(new Item("iPod", "Just an MP3 player", 42.0));
		itemRepository.save(new Item("iPod touch", "A tiny MP3 player", 21.0));
		itemRepository.save(new Item("iPod nano", "A small MP3 player", 1.0));
		itemRepository.save(new Item("Apple TV", "A set top box for your TV", 100.0));
	}

}
