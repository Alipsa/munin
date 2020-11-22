package se.alipsa.munin.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.munin.model.User;

@Repository
public interface UserRepo extends CrudRepository<User, String> {
}
