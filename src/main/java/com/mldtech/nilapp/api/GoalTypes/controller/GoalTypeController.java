package com.mldtech.nilapp.api.GoalTypes.controller;


import com.mldtech.nilapp.api.GoalTypes.model.GoalType;
import com.mldtech.nilapp.api.GoalTypes.service.GoalTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/goal-types")
@RequiredArgsConstructor
public class GoalTypeController {

    private final GoalTypeService service;

    @GetMapping
    public Object getAll() {
        return service.getAll();
    }

    @PostMapping
    public Object create(@RequestBody GoalType type) {
        return service.create(type);
    }
}
