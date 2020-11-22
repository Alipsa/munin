package se.alipsa.munin.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.munin.model.Authorities;
import se.alipsa.munin.model.AuthoritiesPk;
import se.alipsa.munin.model.User;

@Repository
public interface AuthoritiesRepo extends CrudRepository<Authorities, AuthoritiesPk> {

  @Modifying
  @Query("DELETE from Authorities a where a.pk.user = ?1")
  void deleteByUser(User u);

}
