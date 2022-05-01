package kr.giljabi.jwt.repository;

import kr.giljabi.jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByusername(String username);
}
