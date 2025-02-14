package com.skripsi.api.controller;
import com.skripsi.api.model.CourseModule;
import com.skripsi.api.model.User;
import com.skripsi.api.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @PostMapping
    public ResponseEntity<CourseModule> createModule(@RequestBody CourseModule module) {
        CourseModule createdModule = moduleService.createModule(module);
        return ResponseEntity.ok(createdModule);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseModule>> getAllModulesWithProgress( @AuthenticationPrincipal User user) {
        List<CourseModule> modules = moduleService.getAllModulesWithProgress(user.getId());
        return ResponseEntity.ok(modules);
    }

    @GetMapping
    public ResponseEntity<List<CourseModule>> getModules(@RequestParam Long userId) {
        List<CourseModule> modules = moduleService.getAllModulesWithProgress(userId);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseModule> getModuleById(@PathVariable Long id) {
        CourseModule module = moduleService.getModuleById(id);
        return ResponseEntity.ok(module);
    }

}
