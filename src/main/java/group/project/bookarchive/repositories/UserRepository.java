/*This is a repository
interface that extends JpaRepo to
provide CRUD operations for the User entity.*/
package group.project.bookarchive.repositories;

import group.project.bookarchive.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
