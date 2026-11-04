package com.example.demo.repository;

import com.example.demo.model.Item;
import com.example.demo.model.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> {

	@Query("""
			select i from Item i
			where (:type is null or i.type = :type)
			  and (lower(i.id) like :q or lower(i.name) like :q)
			order by i.id
			""")
	List<Item> search(@Param("type") ItemType type, @Param("q") String q);
}
