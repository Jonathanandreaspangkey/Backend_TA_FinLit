package com.skripsi.api.service;
import com.skripsi.api.model.*;
import com.skripsi.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private MaterialProgressRepository materialProgressRepository;
    @Autowired
    private QuizProgressRepository quizProgressRepository;

    public List<CourseModule> getAllModulesWithProgress(Long userId) {
        // Fetch all modules
        List<CourseModule> modules = moduleRepository.findAll();

        // Fetch material progress
        List<MaterialProgress> materialProgressList = materialProgressRepository.findByUserId(userId);

        // Fetch quiz progress
        List<QuizProgress> quizProgressList = quizProgressRepository.findByUserId(userId);

        // Merge progress data with module data
        for (CourseModule module : modules) {
            for (SubModule subModule : module.getSubModules()) {
                // Find material progress related to this submodule
                Optional<MaterialProgress> materialProgress = materialProgressList.stream()
                        .filter(mp -> mp.getSubModule().getId().equals(subModule.getId()))
                        .findFirst();

                // Add material progress data to the submodule
                if (materialProgress.isPresent()) {
                    MaterialProgress userMaterialProgress = materialProgress.get();

                    // Get the last completed material's order number (if available)
                    Integer lastCompletedMaterialOrderNumber = userMaterialProgress.getLastCompletedMaterial() != null
                            ? userMaterialProgress.getLastCompletedMaterial().getOrderNumber()
                            : null;

                    // Set the count of completed materials based on the last completed material's order number
                    subModule.setCompletedMaterialsCount(lastCompletedMaterialOrderNumber);
                }

                // Find all quizzes in the submodule
                List<Quiz> quizzes = subModule.getQuizzes();

                // Find quiz progress related to this submodule
                List<QuizProgress> submoduleQuizProgress = quizProgressList.stream()
                        .filter(qp -> qp.getSubModule().getId().equals(subModule.getId()))
                        .collect(Collectors.toList());

                // Check if all quizzes are completed
                boolean allQuizzesCompleted = quizzes.stream()
                        .allMatch(quiz -> submoduleQuizProgress.stream()
                                .anyMatch(qp -> qp.getQuiz().getId().equals(quiz.getId()) && qp.isQuizCompleted()));

                // Set quiz completion status for the submodule
                subModule.setQuizCompleted(allQuizzesCompleted);
            }
        }

        return modules;
    }

    public CourseModule createModule(CourseModule module) {
        return moduleRepository.save(module);
    }

    public CourseModule getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
