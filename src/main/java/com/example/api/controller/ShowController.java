package com.example.api.controller;

import com.example.api.dto.CreateShowRequest;
import com.example.api.model.Show;
import com.example.api.service.ShowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public List<Show> getAll() {
        return showService.findAll();
    }

    @GetMapping("/{id}")
    public Show getById(@PathVariable @Min(1) Long id) {
        return showService.findById(id);
    }

    @PostMapping
    public Show create(@Valid @RequestBody CreateShowRequest request) {
        return showService.create(request);
    }
}
