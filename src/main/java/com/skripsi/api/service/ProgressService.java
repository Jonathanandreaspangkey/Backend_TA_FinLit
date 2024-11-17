package com.skripsi.api.service;

import com.skripsi.api.model.*;
import com.skripsi.api.model.dto.UserProgressDto;
import com.skripsi.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressService {
    @Autowired
    private MaterialProgressRepository materialProgressRepository;
    @Autowired
    private QuizProgressRepository quizProgressRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private SubModuleRepository subModuleRepository;
    @Autowired
    private ExamProgressRepository examProgressRepository;
    @Autowired
    private PreTestProgressRepository preTestProgressRepository;
    @Autowired
    private QuizRepository quizRepository;

    public List<UserProgressDto> getAllUsersProgress() {
        List<User> users = userRepository.findAll();
        List<UserProgressDto> userProgressList = new ArrayList<>();

        // Calculate the total number of items (materials and quizzes) across all modules
        int totalItems = 0;
        List<SubModule> subModules = subModuleRepository.findAll();
        for (SubModule subModule : subModules) {
            totalItems += subModule.getMaterials().size();
            totalItems += subModule.getQuizzes().size();
        }

        totalItems += 1;

        for (User user : users) {
            List<MaterialProgress> materialProgressList = materialProgressRepository.findByUserId(user.getId());
            List<QuizProgress> quizProgressList = quizProgressRepository.findByUserId(user.getId());

            // Retrieve the user's exam progress
            Optional<ExamProgress> examProgressOpt = examProgressRepository.findByUserId(user.getId());
            boolean examCompleted = examProgressOpt.map(ExamProgress::isExamCompleted).orElse(false);
            System.out.println("ExamProgress for user " + user.getId() + ": " + examProgressOpt);
            UserProgressDto userProgressDto = new UserProgressDto(user.getId(), user.getFirstname());

            int completedItems = 0;

            // Iterate over each submodule's material progress for the user
            for (MaterialProgress materialProgress : materialProgressList) {
                int completedMaterialsCount = materialProgress.getLastCompletedMaterial() != null
                        ? materialProgress.getLastCompletedMaterial().getOrderNumber()
                        : 0;
                completedItems += completedMaterialsCount;
                userProgressDto.addMaterialProgress(materialProgress.getSubModule().getId(), completedMaterialsCount);
            }

            // Iterate over each submodule's quiz progress for the user
            for (QuizProgress quizProgress : quizProgressList) {
                if (quizProgress.isQuizCompleted()) {
                    completedItems += quizProgress.getSubModule().getQuizzes().size();
                }
                userProgressDto.addQuizProgress(quizProgress.getSubModule().getId(), quizProgress.isQuizCompleted());
            }

            // Include exam progress in the calculation
            if (examCompleted) {
                completedItems += 1;
            }

            // Calculate and set the progress percentage
            double progressPercentage = totalItems == 0 ? 0 : Math.round(((double) completedItems / totalItems) * 100);
            userProgressDto.setOverallProgressPercentage(progressPercentage);
            userProgressDto.setExamLastScore(examProgressOpt.map(ExamProgress::getLastScore).orElse(null));
            userProgressList.add(userProgressDto);
        }

        return userProgressList;
    }

    public UserProgressDto getUserProgressById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculate the total number of items (materials and quizzes) across all modules
        int totalItems = 0;
        List<SubModule> subModules = subModuleRepository.findAll();
        for (SubModule subModule : subModules) {
            totalItems += subModule.getMaterials().size();
            totalItems += subModule.getQuizzes().size();
        }

        totalItems += 1;

        List<MaterialProgress> materialProgressList = materialProgressRepository.findByUserId(user.getId());
        List<QuizProgress> quizProgressList = quizProgressRepository.findByUserId(user.getId());

        // Retrieve the user's exam progress
        Optional<ExamProgress> examProgressOpt = examProgressRepository.findByUserId(user.getId());
        boolean examCompleted = examProgressOpt.map(ExamProgress::isExamCompleted).orElse(false);
        System.out.println("ExamProgress for user " + user.getId() + ": " + examProgressOpt);
        UserProgressDto userProgressDto = new UserProgressDto(user.getId(), user.getFirstname());

        int completedItems = 0;

        // Iterate over each submodule's material progress for the user
        for (MaterialProgress materialProgress : materialProgressList) {
            int completedMaterialsCount = materialProgress.getLastCompletedMaterial() != null
                    ? materialProgress.getLastCompletedMaterial().getOrderNumber()
                    : 0;
            completedItems += completedMaterialsCount;
            userProgressDto.addMaterialProgress(materialProgress.getSubModule().getId(), completedMaterialsCount);
        }

        // Iterate over each submodule's quiz progress for the user
        for (QuizProgress quizProgress : quizProgressList) {
            if (quizProgress.isQuizCompleted()) {
                completedItems += quizProgress.getSubModule().getQuizzes().size();
            }
            userProgressDto.addQuizProgress(quizProgress.getSubModule().getId(), quizProgress.isQuizCompleted());
        }

        // Include exam progress in the calculation
        if (examCompleted) {
            completedItems += 1;
        }

        // Calculate and set the progress percentage
        double progressPercentage = totalItems == 0 ? 0 : Math.round(((double) completedItems / totalItems) * 100);
        userProgressDto.setOverallProgressPercentage(progressPercentage);
        userProgressDto.setExamLastScore(examProgressOpt.map(ExamProgress::getLastScore).orElse(null));

        return userProgressDto;
    }


    public void updateMaterialProgress(User user, CourseModule module, SubModule subModule, Material material) {
        Optional<MaterialProgress> existingProgressOpt = materialProgressRepository.findByUserAndSubModule(user, subModule);
        MaterialProgress progress;

        if (existingProgressOpt.isPresent()) {
            progress = existingProgressOpt.get();

            // Check if the new material's order number is greater than the current one
            if (progress.getLastCompletedMaterial() == null ||
                    material.getOrderNumber() > progress.getLastCompletedMaterial().getOrderNumber()) {
                progress.setLastCompletedMaterial(material);
            }

        } else {
            progress = new MaterialProgress();
            progress.setUser(user);
            progress.setSubModule(subModule);
            progress.setLastCompletedMaterial(material);
        }

        materialProgressRepository.save(progress);
    }



    // Mark quiz as completed
    public void markQuizCompleted(Long userId, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        SubModule subModule = quiz.getSubModule();

        QuizProgress progress = quizProgressRepository.findByUserIdAndQuizId(userId, quizId)
                .orElse(new QuizProgress(
                        userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")),
                        subModule,
                        quiz
                ));

        progress.setQuizCompleted(true);
        quizProgressRepository.save(progress);

        if (isAllQuizzesCompleted(userId, subModule.getId())) {
            subModule.setQuizCompleted(true);
            subModuleRepository.save(subModule);
        }
    }


    private boolean isAllQuizzesCompleted(Long userId, Long subModuleId) {
        return quizProgressRepository.countCompletedQuizzesByUserIdAndSubModuleId(userId, subModuleId) ==
                quizRepository.countBySubModuleId(subModuleId);
    }

    // In ProgressService.java
    public List<LearningObjective> getLearningObjectivesWithQuizCompletion(Long userId, Long subModuleId) {
        List<LearningObjective> objectives = subModuleRepository.findById(subModuleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submodule not found"))
                .getLearningObjectives();

        for (LearningObjective objective : objectives) {
            boolean allQuizzesCompleted = objective.getQuizzes().stream()
                    .allMatch(quiz -> quizProgressRepository.isQuizCompletedForUser(userId, quiz.getId()));
            objective.setQuizCompleted(allQuizzesCompleted);
        }

        return objectives;
    }

    public void updateExamProgress(User user, boolean examCompleted, Integer lastScore) {
        Optional<ExamProgress> existingProgressOpt = examProgressRepository.findByUserId(user.getId());
        ExamProgress progress;

        if (existingProgressOpt.isPresent()) {
            progress = existingProgressOpt.get();
        } else {
            progress = new ExamProgress();
            progress.setUser(user);
        }

        // Mark examCompleted as true only if lastScore > 75
        if (lastScore != null && lastScore >= 75) {
            progress.setExamCompleted(true);
        } else {
            progress.setExamCompleted(false);
        }
        progress.setLastScore(lastScore);
        examProgressRepository.save(progress);
    }

    public Optional<ExamProgress> getExamProgressByUserId(Long userId) {
        return examProgressRepository.findByUserId(userId);
    }

public void updatePreTestProgress(User user, boolean preTestCompleted, Integer lastScore) {
    Optional<PreTestProgress> existingProgressOpt = preTestProgressRepository.findByUserId(user.getId());
    PreTestProgress progress;

    if (existingProgressOpt.isPresent()) {
        progress = existingProgressOpt.get();
    } else {
        progress = new PreTestProgress();
        progress.setUser(user);
    }

    // Mark preTestCompleted as true only if lastScore > 75
    if (lastScore != null && lastScore >= 75) {
        progress.setPreTestCompleted(true);
    } else {
        progress.setPreTestCompleted(false);
    }
    progress.setLastScore(lastScore);
    preTestProgressRepository.save(progress);
}

    public Optional<PreTestProgress> getPreTestProgressByUserId(Long userId) {
        return preTestProgressRepository.findByUserId(userId);
    }
}

