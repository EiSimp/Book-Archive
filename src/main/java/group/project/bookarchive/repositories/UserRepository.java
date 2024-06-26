/*This is a repository
interface that extends JpaRepo to
provide CRUD operations for the User entity.*/
package group.project.bookarchive.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import group.project.bookarchive.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    Optional<User> findById(Long id);

    List<User> findByUsername(String username);

    List<User> findByUsernameAndPassword(String username, String password);

}
