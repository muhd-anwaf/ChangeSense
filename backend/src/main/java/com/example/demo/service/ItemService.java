package com.example.demo.service;

import com.example.demo.model.Item;
import com.example.demo.model.ItemType;
import com.example.demo.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

	private final ItemRepository repository;

	public ItemService(ItemRepository repository) {
		this.repository = repository;
	}

	public List<Item> search(ItemType type, String q) {
		String pattern = "%" + (q == null ? "" : q.trim().toLowerCase()) + "%";
		return repository.search(type, pattern);
	}
}
