package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.dto.CategorySaveRequest;
import com.bookstore.entity.Category;
import com.bookstore.mapper.CategoryMapper;
import com.bookstore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> tree() {
        List<Category> list = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSortOrder)
                .orderByAsc(Category::getId));
        Map<Long, Category> map = new HashMap<>();
        for (Category item : list) {
            item.setChildren(new ArrayList<>());
            map.put(item.getId(), item);
        }

        List<Category> roots = new ArrayList<>();
        for (Category item : list) {
            Long parentId = item.getParentId();
            if (parentId == null || parentId == 0) {
                roots.add(item);
            } else {
                Category parent = map.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(item);
                } else {
                    roots.add(item);
                }
            }
        }
        return roots;
    }

    @Override
    public void create(CategorySaveRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder());
        categoryMapper.insert(category);
    }

    @Override
    public void update(Long id, CategorySaveRequest request) {
        Category category = new Category();
        category.setId(id);
        category.setName(request.getName());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder());
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
