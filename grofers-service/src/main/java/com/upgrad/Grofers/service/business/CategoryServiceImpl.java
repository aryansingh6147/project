package com.upgrad.Grofers.service.business;

import com.upgrad.Grofers.service.dao.CategoryDao;
import com.upgrad.Grofers.service.entity.CategoryEntity;
import com.upgrad.Grofers.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryDao categoryDao;

    /**
     * The method implements the business logic for getting category by its id endpoint.
     */
    @Override
    public CategoryEntity getCategoryById(String categoryId) throws CategoryNotFoundException {
        return categoryDao.getCategoryById(categoryId);
    }
    
   

    /**
     * The method implements the business logic for getting all categories ordered by their name endpoint.
     */
    @Override
    public List<CategoryEntity> getAllCategoriesOrderedByName()  {
        return categoryDao.getAllCategoriesOrderedByName();
    }

    /**
     * The method implements the business logic for getting categories for any particular store.
     */
    @Override
    public List<CategoryEntity> getCategoriesByStores(String storeId)  {
    return categoryDao.getCategoriesByStores(storeId);
    }
}
