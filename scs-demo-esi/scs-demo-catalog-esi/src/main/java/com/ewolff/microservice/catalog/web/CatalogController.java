package com.ewolff.microservice.catalog.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ewolff.microservice.catalog.Item;
import com.ewolff.microservice.catalog.ItemRepository;

@Controller
public class CatalogController {

	private final ItemRepository itemRepository;

	@Autowired
	public CatalogController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView Item(@PathVariable("id") long id) {
		return new ModelAndView("item", "item", itemRepository.findOne(id));
	}

	@RequestMapping(value = "/{id}.esi", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String ItemESI(@PathVariable("id") long id) {
		Item item = itemRepository.findOne(id);
		return item.getName();
	}

	@RequestMapping({ "/list.html", "/" })
	public ModelAndView ItemList() {
		return new ModelAndView("itemlist", "items", itemRepository.findAll());
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.GET)
	public ModelAndView add() {
		return new ModelAndView("item", "item", new Item());
	}

	@RequestMapping(value = "/form.html", method = RequestMethod.POST)
	public ModelAndView post(Item Item) {
		Item = itemRepository.save(Item);
		return new ModelAndView("success");
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.PUT)
	public ModelAndView put(@PathVariable("id") long id, Item item) {
		item.setId(id);
		itemRepository.save(item);
		return new ModelAndView("success");
	}

	@RequestMapping({ "/item-choice.esi" })
	public ModelAndView ItemChoice(@RequestParam(name = "selected", required = false) Long selected,
			@RequestParam("name") String name, @RequestParam("id") String id) {
		Map model = new HashMap<String, Object>();
		model.put("items", itemRepository.findAll());
		if (selected != null) {
			model.put("selected", selected);
		}
		model.put("id", id);
		model.put("name", name);
		return new ModelAndView("item-choice", model);
	}

	@RequestMapping(value = "/searchForm.html", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView searchForm() {
		return new ModelAndView("searchForm");
	}

	@RequestMapping(value = "/searchByName.html", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView search(@RequestParam("query") String query) {
		return new ModelAndView("itemlist", "items", itemRepository.findByNameContaining(query));
	}

	@RequestMapping(value = "/{id}.html", method = RequestMethod.DELETE)
	public ModelAndView delete(@PathVariable("id") long id) {
		itemRepository.delete(id);
		return new ModelAndView("success");
	}

}
