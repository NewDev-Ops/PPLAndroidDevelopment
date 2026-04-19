package com.pocketpal.data.repository

import com.pocketpal.data.local.CategoryDao
import com.pocketpal.data.local.CategoryEntity
import com.pocketpal.data.local.PocketPalDatabase
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val database: PocketPalDatabase) {
    
    private val dao: CategoryDao = database.categoryDao()
    
    fun getAllCategories(): Flow<List<CategoryEntity>> = dao.getAllCategories()
    
    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>> = 
        dao.getCategoriesByType(type)
    
    fun getParentCategories(): Flow<List<CategoryEntity>> = dao.getParentCategories()
    
    fun getSubcategories(parentId: String): Flow<List<CategoryEntity>> = 
        dao.getSubcategories(parentId)
    
    fun getDefaultCategories(): Flow<List<CategoryEntity>> = dao.getDefaultCategories()
    
    suspend fun addCategory(
        name: String,
        icon: String,
        color: String,
        type: String,
        parentId: String? = null
    ) {
        val category = CategoryEntity(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            icon = icon,
            color = color,
            type = type,
            parentId = parentId
        )
        dao.insertCategory(category)
    }
    
    suspend fun updateCategory(category: CategoryEntity) {
        dao.updateCategory(category)
    }
    
    suspend fun deleteCategory(category: CategoryEntity) {
        dao.deleteCategory(category)
    }
    
    suspend fun deleteCategoryById(id: String) {
        dao.deleteCategoryById(id)
    }
}