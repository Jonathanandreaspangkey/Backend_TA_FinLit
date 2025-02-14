package com.skripsi.api.controller;

import com.skripsi.api.model.SubModule;
import com.skripsi.api.service.SubModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submodules")
public class SubModuleController {

    @Autowired
    private SubModuleService subModuleService;


    @GetMapping
    public ResponseEntity<List<SubModule>> getAllSubmodules() {
        List<SubModule> submodules = subModuleService.findAll();
        return ResponseEntity.ok(submodules);
    }

    @PostMapping
    public ResponseEntity<SubModule> createSubModule(@RequestBody SubModule subModule) {
        SubModule createdSubModule = subModuleService.createSubModule(subModule);
        return ResponseEntity.ok(createdSubModule);
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<SubModule>> getSubModulesByModuleId(@PathVariable Long moduleId) {
        List<SubModule> subModules = subModuleService.getSubModulesByModuleId(moduleId);
        return ResponseEntity.ok(subModules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubModule> getSubModuleById(@PathVariable Long id) {
        SubModule subModule = subModuleService.getSubModuleById(id);
        return ResponseEntity.ok(subModule);
    }

}
