package com.example.demo.repository;

import com.example.demo.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    Page findBySlug(String slug);

    boolean existsBySlug(String slug);
}
