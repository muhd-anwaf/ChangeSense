package com.example.demo.dto;

import com.example.demo.model.ItemType;

import java.math.BigDecimal;
import java.util.List;

public record BomNode(String id, String name, ItemType type, String revision, String supplier,
					  BigDecimal quantity, boolean cyclic, List<BomNode> children) {
}
