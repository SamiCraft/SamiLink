package com.samifying.link.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataRepository extends JpaRepository<Data, Integer> {
    Optional<Data> findByUuid(String uuid);

    Optional<Data> findByDiscordId(String discordId);
}
