package com.example.demo.dto;

import java.util.List;

public record BomResponse(BomNode root, boolean hasCycle, List<List<String>> cycles) {
}
