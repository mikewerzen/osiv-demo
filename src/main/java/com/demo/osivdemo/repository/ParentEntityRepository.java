package com.demo.osivdemo.repository;

import com.demo.osivdemo.domain.ParentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentEntityRepository extends JpaRepository<ParentEntity, Long> {
}
