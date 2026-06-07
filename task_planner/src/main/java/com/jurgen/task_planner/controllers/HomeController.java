package com.jurgen.task_planner.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "public")
public class HomeController {

    @GetMapping("/home")
    public String home() {
        String name = "Test";
        name = "new";
        return name;
    }
}
