package net.frozenorb.hydrogenapi.repository;

import net.frozenorb.hydrogenapi.models.Prefix;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrefixRepository extends MongoRepository<Prefix, String> {

}
