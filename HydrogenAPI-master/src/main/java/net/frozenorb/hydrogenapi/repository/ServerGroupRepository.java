package net.frozenorb.hydrogenapi.repository;

import net.frozenorb.hydrogenapi.models.ServerGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ServerGroupRepository extends MongoRepository<ServerGroup, String> {
}
