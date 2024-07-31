/*This is a repository
interface that extends JpaRepo to
provide CRUD operations for the User entity.*/
package group.project.bookarchive.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import group.project.bookarchive.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameAndEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByUsernameAndPassword(String username, String password);

    @Override
    void deleteById(Long id);
}
