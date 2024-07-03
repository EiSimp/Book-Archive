package group.project.bookarchive.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import group.project.bookarchive.models.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT r FROM UserRole r WHERE r.name = ?1")
    public UserRole findByName(String name);

}
