package com.dev.boxpoint.repository;

import com.dev.boxpoint.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
