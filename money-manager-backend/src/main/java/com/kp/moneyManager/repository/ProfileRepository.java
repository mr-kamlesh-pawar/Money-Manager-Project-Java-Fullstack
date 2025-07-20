package com.kp.moneyManager.repository;

import com.kp.moneyManager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long>
{
    Optional<ProfileEntity> findByEmail(String email);

}
