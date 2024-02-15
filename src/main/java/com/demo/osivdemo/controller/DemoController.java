package com.demo.osivdemo.controller;

import com.demo.osivdemo.domain.ParentEntity;
import com.demo.osivdemo.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class DemoController {

    private DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/health")
    public String getHealth() {
        return "OK";
    }

    @GetMapping("/checkNontransactional")
    public void checkNontransactional() {
        demoService.doSomethingNontransactional();
    }

    @GetMapping("/checkTransactional")
    public void checkTransactional() {
        demoService.doSomethingTransactional();
    }

    @PostMapping("/family")
    public void createFamily() {
        demoService.saveFamily();
    }

    @PostMapping("/megafamily")
    public void createMegaFamily() {
        demoService.saveMegaFamily();
    }

    @GetMapping("/family")
    public Optional<ParentEntity> getFamily() {
        return demoService.getFamily();
    }
}
