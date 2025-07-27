package com.kp.moneyManager.controller;

import com.kp.moneyManager.dto.CategoryDTO;
import com.kp.moneyManager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){

        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);

        return  ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
}
