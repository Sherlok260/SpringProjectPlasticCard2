package com.example.springcardprojectdemo.repository;

import com.example.springcardprojectdemo.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriesRepository extends JpaRepository<History, Long> {
}
