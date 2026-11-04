package com.example.demo.repository;

import com.example.demo.model.BomLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BomLineRepository extends JpaRepository<BomLine, Integer> {

	@Query("select l from BomLine l join fetch l.child where l.parent.id = :parentId order by l.id")
	List<BomLine> findChildren(@Param("parentId") String parentId);
}
