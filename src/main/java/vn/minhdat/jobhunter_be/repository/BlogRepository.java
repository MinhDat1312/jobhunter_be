package vn.minhdat.jobhunter_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.minhdat.jobhunter_be.entity.Blog;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {
    @Query(value = """
        SELECT DISTINCT t, COUNT(t) 
        FROM Blog b JOIN b.tags t
        WHERE (:keyword IS NULL OR LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%'))) 
        GROUP BY t 
        ORDER BY COUNT(t) DESC
        """
    )
    List<Object[]> findAllTags(@Param("keyword") String keyword);
}
