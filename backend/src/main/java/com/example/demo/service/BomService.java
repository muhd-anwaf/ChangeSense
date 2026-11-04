package com.example.demo.service;

import com.example.demo.dto.BomNode;
import com.example.demo.dto.BomResponse;
import com.example.demo.exception.ItemNotFoundException;
import com.example.demo.model.BomLine;
import com.example.demo.model.Item;
import com.example.demo.repository.BomLineRepository;
import com.example.demo.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BomService {

	private final ItemRepository itemRepository;
	private final BomLineRepository bomLineRepository;

	public BomService(ItemRepository itemRepository, BomLineRepository bomLineRepository) {
		this.itemRepository = itemRepository;
		this.bomLineRepository = bomLineRepository;
	}

	@Transactional(readOnly = true)
	public BomResponse getBom(String id) {
		Item root = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
		Set<List<String>> cycles = new LinkedHashSet<>();
		BomNode tree = build(root, null, new ArrayList<>(), cycles, new HashMap<>());
		return new BomResponse(tree, !cycles.isEmpty(), List.copyOf(cycles));
	}

	private BomNode build(Item item, BigDecimal quantity, List<String> path, Set<List<String>> cycles,
						  Map<String, List<BomLine>> lineCache) {
		path.add(item.getId());
		List<BomNode> children = new ArrayList<>();
		for (BomLine line : lineCache.computeIfAbsent(item.getId(), bomLineRepository::findChildren)) {
			Item child = line.getChild();
			int at = path.indexOf(child.getId());
			if (at >= 0) {
				List<String> cycle = new ArrayList<>(path.subList(at, path.size()));
				cycle.add(child.getId());
				cycles.add(cycle);
				children.add(node(child, line.getQuantity(), true, List.of()));
			} else {
				children.add(build(child, line.getQuantity(), path, cycles, lineCache));
			}
		}
		path.removeLast();
		return node(item, quantity, false, children);
	}

	private BomNode node(Item item, BigDecimal quantity, boolean cyclic, List<BomNode> children) {
		return new BomNode(item.getId(), item.getName(), item.getType(), item.getRevision(),
				item.getSupplier(), quantity, cyclic, children);
	}
}
