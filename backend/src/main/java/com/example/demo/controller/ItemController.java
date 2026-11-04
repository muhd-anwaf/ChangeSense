package com.example.demo.controller;

import com.example.demo.dto.BomResponse;
import com.example.demo.model.Item;
import com.example.demo.model.ItemType;
import com.example.demo.service.BomService;
import com.example.demo.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

	private final ItemService service;
	private final BomService bomService;

	public ItemController(ItemService service, BomService bomService) {
		this.service = service;
		this.bomService = bomService;
	}

	@GetMapping
	public List<Item> search(@RequestParam(required = false) ItemType type,
							 @RequestParam(required = false) String q) {
		return service.search(type, q);
	}

	@GetMapping("/{id}/bom")
	public BomResponse bom(@PathVariable String id) {
		return bomService.getBom(id);
	}
}
