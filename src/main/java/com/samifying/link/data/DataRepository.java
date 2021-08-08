package com.samifying.link.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataRepository extends CrudRepository<Data, Integer> {
    Optional<Data> findByUuid(String uuid);

    Optional<Data> findByDiscordId(String discordId);
}
