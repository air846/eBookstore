package com.bookstore.service;

import com.bookstore.dto.CategorySaveRequest;
import com.bookstore.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> tree();

    void create(CategorySaveRequest request);

    void update(Long id, CategorySaveRequest request);

    void delete(Long id);
}
