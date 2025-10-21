package com.minji.hi_erp.security.repository;

import com.minji.hi_erp.security.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
