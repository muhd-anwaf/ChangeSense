package com.example.demo.exception;

public class ItemNotFoundException extends RuntimeException {

	public ItemNotFoundException(String id) {
		super("Item not found: " + id);
	}
}
