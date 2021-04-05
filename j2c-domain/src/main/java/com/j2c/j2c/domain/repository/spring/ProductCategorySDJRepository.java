package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductCategorySDJRepository
        extends JpaRepository<ProductCategory, Long> {

    String FIND_SUBCATEGORY_IDS = "SELECT C.id FROM ProductCategory C WHERE C.root.id = :rootId AND C.left > :left AND C.right < :right";

    @Query("SELECT T FROM ProductCategory T WHERE T.left > :left AND T.right < :right")
    Page<ProductCategory> findSubCategories(Integer left, Integer right, Pageable pageable);

    @Query("SELECT T.imageFilename FROM ProductCategory T WHERE T.id IN " +
            "(" + FIND_SUBCATEGORY_IDS + ")")
    List<String> findSubCategoryFilenames(Long rootId, Integer left, Integer right);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ProductCategory T SET T.right = T.right+2 WHERE (T.root.id = :rootId OR T.id = :rootId) AND T.id != :parentId AND T.right > :right")
    void incrementHierarchyRight(Long rootId, Long parentId, Integer right);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ProductCategory T SET T.left = T.left+2 WHERE (T.root.id = :rootId OR T.id = :rootId) AND T.id != :parentId AND T.left > :left")
    void incrementHierarchyLeft(Long rootId, Long parentId, Integer left);

}
