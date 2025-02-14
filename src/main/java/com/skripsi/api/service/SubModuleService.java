package com.skripsi.api.service;
import com.skripsi.api.model.CourseModule;
import com.skripsi.api.model.SubModule;
import com.skripsi.api.repository.ModuleRepository;
import com.skripsi.api.repository.SubModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class SubModuleService {
    @Autowired
    private SubModuleRepository subModuleRepository;
    @Autowired
    private ModuleRepository moduleRepository;


    public List<SubModule> findAll() {
        return subModuleRepository.findAll();
    }

    public SubModule getSubModuleById(Long id) {
        return subModuleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<SubModule> getSubModulesByModuleId(Long moduleId) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        return subModuleRepository.findByModule(module);
    }

    public SubModule createSubModule(SubModule subModule) {
        Long moduleId = subModule.getModule().getId(); // Extract moduleId from SubModule
        Optional<CourseModule> module = moduleRepository.findById(moduleId); // Check extracted id if present in module table
        if (module.isPresent()) {
            subModule.setModule(module.get()); // Set the Module entity
            return subModuleRepository.save(subModule);
        } else {
            throw new IllegalArgumentException("Module with id " + moduleId + " not found");
        }
    }

}
