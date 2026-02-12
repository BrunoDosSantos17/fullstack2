package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<TaskEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND t.listName = :listName ORDER BY t.createdAt DESC")
    List<TaskEntity> findByUserIdAndListName(@Param("userId") UUID userId, @Param("listName") String listName);

    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id AND t.user.id = :userId")
    Optional<TaskEntity> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT DISTINCT t.listName FROM TaskEntity t WHERE t.user.id = :userId ORDER BY t.listName")
    List<String> findDistinctListNamesByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND t.completed = true ORDER BY t.updatedAt DESC")
    List<TaskEntity> findCompletedByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND t.completed = false ORDER BY t.createdAt DESC")
    List<TaskEntity> findPendingByUserId(@Param("userId") UUID userId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TaskEntity t " +
            "WHERE t.user.id = :userId AND t.listName = :listName AND LOWER(t.title) = LOWER(:title)")
    boolean existsByUserIdAndListNameAndTitle(@Param("userId") UUID userId,
                                              @Param("listName") String listName,
                                              @Param("title") String title);
}
