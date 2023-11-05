package net.frozenorb.hydrogenapi.repository;

import net.frozenorb.hydrogenapi.models.Rank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RankRepository extends MongoRepository<Rank, String> {

    Rank findByRankid(String rankid);

}
