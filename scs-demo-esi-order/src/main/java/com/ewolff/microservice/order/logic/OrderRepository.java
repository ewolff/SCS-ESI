package com.ewolff.microservice.order.logic;

import org.springframework.data.repository.PagingAndSortingRepository;

interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

}
