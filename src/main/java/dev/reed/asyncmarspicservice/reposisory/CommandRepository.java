package dev.reed.asyncmarspicservice.reposisory;

import dev.reed.asyncmarspicservice.entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRepository extends JpaRepository<Command, Long> {
}
