package com.skripsi.api.service;

import com.skripsi.api.model.Resource;
import com.skripsi.api.model.SubModule;
import com.skripsi.api.repository.ResourceRepository;
import com.skripsi.api.repository.SubModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SubModuleRepository subModuleRepository;

    // Add a new resource to a submodule
    public Resource addResource(Long subModuleId, Resource resource) {
        SubModule subModule = subModuleRepository.findById(subModuleId)
                .orElseThrow(() -> new RuntimeException("Submodule not found"));
        resource.setSubModule(subModule);
        return resourceRepository.save(resource);
    }

    // Get all resources for a submodule
    public List<Resource> getResourcesBySubModuleId(Long subModuleId) {
        return resourceRepository.findBySubModuleId(subModuleId);
    }

    // Delete a resource by ID
    public void deleteResource(Long resourceId) {
        resourceRepository.deleteById(resourceId);
    }
}
