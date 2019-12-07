package com.brewery.repository;

import com.brewery.model.MeasureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasureTypeRepository extends JpaRepository<MeasureType, String> {
}
