/*This is a repository
interface that extends JpaRepo to
provide CRUD operations for the User entity.*/
package group.project.bookarchive.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import group.project.bookarchive.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByUsernameAndPassword(String username, String password);
}
