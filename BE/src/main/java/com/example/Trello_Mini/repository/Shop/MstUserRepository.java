package com.example.Trello_Mini.repository.Shop;

import com.example.Trello_Mini.entity.Shop.MstUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MstUserRepository extends JpaRepository<MstUserEntity, Integer> {
    Optional<MstUserEntity> findByUsername(String username);
}
