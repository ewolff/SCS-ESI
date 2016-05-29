package com.ewolff.microservice.catalog;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CatalogApp.class)
@IntegrationTest("server.port=0")
@WebAppConfiguration
@ActiveProfiles("test")
public class CatalogWebIntegrationTest {

	@Autowired
	private ItemRepository itemRepository;

	@Value("${local.server.port}")
	private int serverPort;

	private Item iPodNano;

	private RestTemplate restTemplate;

	@Before
	public void setup() {
		iPodNano = itemRepository.findByName("iPod nano").get(0);
		restTemplate = new RestTemplate();
	}

	@Test
	public void IsItemReturned() {
		String url = catalogURL() + "/" + iPodNano.getId() + ".html";
		String body = getForMediaType(String.class, MediaType.TEXT_HTML, url);

		assertThat(body, containsString("iPod nano"));
		assertThat(body, containsString("<div"));
		assertThat(body, containsString("<!DOCTYPE"));
	}

	@Test
	public void IsItemESIReturned() {
		String url = catalogURL() + "/" + iPodNano.getId() + ".esi";
		String body = getForMediaType(String.class, MediaType.TEXT_HTML, url);

		assertThat(body, containsString("iPod nano"));
		assertThat(body, not(containsString("<!DOCTYPE")));
	}

	private String catalogURL() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void FormReturned() {
		String url = catalogURL() + "/searchForm.html";
		String body = getForMediaType(String.class, MediaType.TEXT_HTML, url);

		assertThat(body, containsString("<form"));
		assertThat(body, containsString("<div>"));
	}

	@Test
	public void SearchWorks() {
		String url = catalogURL() + "/searchByName.html?query=iPod";
		String body = restTemplate.getForObject(url, String.class);

		assertThat(body, containsString("iPod nano"));
		assertThat(body, containsString("<div"));
	}

	@Test
	public void IsChoiceReturnedAndSelectedWorks() {
		String url = catalogURL() + "/item-choice.esi?selected=2&id=myId&name=myName";
		String body = getForMediaType(String.class, MediaType.TEXT_HTML, url);

		assertThat(body, containsString("iPod nano"));
		assertThat(body, containsString("id=\"myId\""));
		assertThat(body, containsString("name=\"myName\""));
		assertThat(body, containsString("selected"));
		assertThat(body, containsString("<option"));
	}

	private <T> T getForMediaType(Class<T> value, MediaType mediaType, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(mediaType));

		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		ResponseEntity<T> resultEntity = restTemplate.exchange(url, HttpMethod.GET, entity, value);

		return resultEntity.getBody();
	}

}
