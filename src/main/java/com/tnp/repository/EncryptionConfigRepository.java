package com.tnp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tnp.model.EncryptionConfig;

@Repository
public interface EncryptionConfigRepository extends JpaRepository<EncryptionConfig, Long> {
}
