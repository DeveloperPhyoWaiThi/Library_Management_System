package com.sample.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sample.entity.Book;

import jakarta.transaction.Transactional;

public interface BookRepository extends JpaRepository<Book, Long>{
	
	Page<Book> findByStatusAndDelFlag(Boolean status,Boolean delFlag, Pageable pageable);
	
	Book findByBookIdAndDelFlag(Long id, boolean delFlag);
	
	
	@Query("SELECT b FROM Book b WHERE b.category.categoryId = :categoryId and b.delFlag= :delFlag and b.status= false")
	Page<Book> findByCategoryIdAndDelFlag(@Param("categoryId") Long categoryId,@Param("delFlag") Boolean delFlag,Pageable pageable);
	
	@Query("SELECT b FROM Book b JOIN b.authors a WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND b.status = false and b.delFlag=:delFlag")
	Page<Book> searchBooks(@Param("keyword") String keyword,@Param("delFlag") boolean delFlag, Pageable pageable);

	@Query("SELECT b FROM Book b JOIN b.authors a JOIN b.category c " +
	           "WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "OR LOWER(a.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
	           "AND c.categoryId = :categoryId and b.delFlag=:delFlag and b.status= false")
	Page<Book> findBooksByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("delFlag") Boolean delFlag,Pageable page);
	
	long countByStatusFalse();

    long countByStatusTrue();
    
    Long countByDelFlagFalse();
    
    @Modifying
    @Query("UPDATE Book b SET b.delFlag = true WHERE b.category.categoryId = :categoryId")
    void softDeleteBooksByCategoryId(@Param("categoryId") Long categoryId);
    
    @Modifying
    @Query(value = "UPDATE Book b SET b.del_flag = true " +
                   "WHERE b.book_id IN (SELECT ba.book_id FROM book_author ba " +
                                       "WHERE ba.author_id = :authorId)", nativeQuery = true)
    void softDeleteBooksByAuthorId(@Param("authorId") Long authorId);

    // Count borrowed books (where status is true) and delFlag is false
    @Query("SELECT COUNT(b) FROM Book b WHERE b.status = true AND b.delFlag = false")
    Long countByStatusTrueAndDelFlagFalse();

    // Count remaining books (where status is false) and delFlag is false
    @Query("SELECT COUNT(b) FROM Book b WHERE b.status = false AND b.delFlag = false")
    Long countByStatusFalseAndDelFlagFalse();
    
    @Query("SELECT b FROM Book b WHERE b.delFlag = false and b.status= false")
    Page<Book> findActiveBooks(Pageable pageable);
    
    @Query(value = "SELECT book_author.author_id FROM book_author WHERE book_author.book_id = :bookId",
		       nativeQuery = true)
    List<Long> findAuthorByBookId(@Param("bookId") Long bookId);
    
    @Modifying
    @Transactional
	@Query(value = "DELETE FROM book_author WHERE book_id = :bookId AND author_id = :authorId", nativeQuery = true)
	void deleteAuthor(@Param("bookId") Long bookId, @Param("authorId") Long authorId);

}
