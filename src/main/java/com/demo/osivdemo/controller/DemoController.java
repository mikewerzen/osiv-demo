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

    @PostMapping("/singleparent")
    public void createSingleParent() {
        demoService.saveSingleParent();
    }

    @GetMapping("/getSingleParentInOSIV")
    public ParentEntity getSingleParent() {
        return  demoService.getSingleParentInOSIV();
    }

    @GetMapping("/getSingleParentWithSeparateSession")
    public ParentEntity getSingleParentWithSeparateSession() {
        return  demoService.getSingleParentWithSeparateSession();
    }

    @GetMapping("/getSingleParentInAsyncSession")
    public ParentEntity getSingleParentInAsyncSession() {
        return  demoService.getSingleParentInAsyncSession();
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

    @GetMapping("/familyNoOSIV")
    public ParentEntity getFamilyNoOSIV() {
        return demoService.getFamilyNoOSIV();
    }
}
