package com.upgrad.Grofers.service.business;



import com.upgrad.Grofers.service.dao.CategoryDao;
import com.upgrad.Grofers.service.dao.StoreDao;
import com.upgrad.Grofers.service.entity.StoreEntity;
import com.upgrad.Grofers.service.exception.CategoryNotFoundException;
import com.upgrad.Grofers.service.exception.StoreNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreDao StoreDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * The method implements the business logic for getting Store details by Store uuid.
     */
    @Override
    public StoreEntity StoreByUUID(String StoreId) throws StoreNotFoundException {
        return StoreDao.storeByUUID(StoreId);
    }

    /**
     * The method implements the business logic for getting Stores by Store name.
     */
    @Override
    public List<StoreEntity> StoresByName(String StoreName) throws StoreNotFoundException {
        return StoreDao.storeByName(StoreName);
    }

    /**
     * The method implements the business logic for getting all Stores ordered by their rating.
     */
    @Override
    public List<StoreEntity> StoresByRating() {
        return StoreDao.storeByRating();
    }

    /**
     * The method implements the business logic for getting Stores by their category.
     */
    @Override
    public List<StoreEntity> StoreByCategory(String categoryId) throws CategoryNotFoundException {
        return StoreDao.storeByCategory(categoryId);

    }

}
