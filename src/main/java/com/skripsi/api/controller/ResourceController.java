package com.skripsi.api.controller;

import com.skripsi.api.model.Resource;
import com.skripsi.api.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    // Add a new resource to a submodule
    @PostMapping("/submodule/{subModuleId}")
    public ResponseEntity<Resource> addResource(@PathVariable Long subModuleId, @RequestBody Resource resource) {
        Resource savedResource = resourceService.addResource(subModuleId, resource);
        return ResponseEntity.ok(savedResource);
    }

    // Get all resources for a submodule
    @GetMapping("/submodule/{subModuleId}")
    public ResponseEntity<List<Resource>> getResourcesBySubModuleId(@PathVariable Long subModuleId) {
        List<Resource> resources = resourceService.getResourcesBySubModuleId(subModuleId);
        return ResponseEntity.ok(resources);
    }

    // Delete a resource by ID
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }
}