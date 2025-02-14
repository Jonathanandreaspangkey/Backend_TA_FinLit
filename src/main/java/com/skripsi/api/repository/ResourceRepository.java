package com.skripsi.api.repository;

import com.skripsi.api.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findBySubModuleId(Long subModuleId); // Find resources by submodule ID
}
