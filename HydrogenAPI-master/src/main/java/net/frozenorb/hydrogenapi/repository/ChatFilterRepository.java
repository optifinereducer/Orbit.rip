package net.frozenorb.hydrogenapi.repository;

import net.frozenorb.hydrogenapi.models.ChatFilter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ChatFilterRepository extends MongoRepository<ChatFilter, String> {
}
