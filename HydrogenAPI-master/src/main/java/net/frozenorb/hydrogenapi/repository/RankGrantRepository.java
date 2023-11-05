package net.frozenorb.hydrogenapi.repository;

import net.frozenorb.hydrogenapi.models.RankGrant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RankGrantRepository extends MongoRepository<RankGrant, String> {

    RankGrant findById(String id);
    List<RankGrant> findByUuid(String uuid);

}
