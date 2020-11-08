package se.alipsa.renjin.webreports.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.renjin.webreports.model.User;

@Repository
public interface UserRepo extends CrudRepository<User, String> {
}
