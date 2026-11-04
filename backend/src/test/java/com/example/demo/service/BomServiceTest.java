package com.example.demo.service;

import com.example.demo.dto.BomResponse;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.model.BomLine;
import com.example.demo.model.Item;
import com.example.demo.repository.BomLineRepository;
import com.example.demo.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BomServiceTest {

	private final ItemRepository items = mock(ItemRepository.class);
	private final BomLineRepository lines = mock(BomLineRepository.class);
	private final BomService service = new BomService(items, lines);

	@BeforeEach
	void noChildrenByDefault() {
		when(lines.findChildren(anyString())).thenReturn(List.of());
	}

	@Test
	void buildsTreeWithoutCycle() {
		Item a = item("A");
		Item b = item("B");
		Item c = item("C");
		when(items.findById("A")).thenReturn(Optional.of(a));
		when(lines.findChildren("A")).thenReturn(List.of(line(a, b, "2"), line(a, c, "1")));
		when(lines.findChildren("B")).thenReturn(List.of(line(b, c, "3")));

		BomResponse response = service.getBom("A");

		assertFalse(response.hasCycle());
		assertEquals(2, response.root().children().size());
		assertEquals("C", response.root().children().get(0).children().get(0).id());
	}

	@Test
	void detectsCycle() {
		Item a = item("A");
		Item b = item("B");
		Item c = item("C");
		when(items.findById("A")).thenReturn(Optional.of(a));
		when(lines.findChildren("A")).thenReturn(List.of(line(a, b, "1")));
		when(lines.findChildren("B")).thenReturn(List.of(line(b, c, "1")));
		when(lines.findChildren("C")).thenReturn(List.of(line(c, a, "1")));

		BomResponse response = service.getBom("A");

		assertTrue(response.hasCycle());
		assertEquals(List.of(List.of("A", "B", "C", "A")), response.cycles());
	}

	@Test
	void unknownItemThrows() {
		when(items.findById("X")).thenReturn(Optional.empty());
		assertThrows(ItemNotFoundException.class, () -> service.getBom("X"));
	}

	private Item item(String id) {
		Item item = new Item();
		item.setId(id);
		item.setName(id);
		return item;
	}

	private BomLine line(Item parent, Item child, String quantity) {
		BomLine line = new BomLine();
		line.setParent(parent);
		line.setChild(child);
		line.setQuantity(new BigDecimal(quantity));
		return line;
	}
}
