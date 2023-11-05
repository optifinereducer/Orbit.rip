package net.frozenorb.hydrogenapi.repository;

import net.frozenorb.hydrogenapi.models.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {

    Player findByUuid(String uuid);

}
