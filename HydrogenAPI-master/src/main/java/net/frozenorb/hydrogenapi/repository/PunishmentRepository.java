package net.frozenorb.hydrogenapi.repository;

import net.frozenorb.hydrogenapi.models.Punishment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PunishmentRepository extends MongoRepository<Punishment, String> {

    List<Punishment> findByUuid(String uuid);
    List<Punishment> findByUserIp(String userIp);

}
