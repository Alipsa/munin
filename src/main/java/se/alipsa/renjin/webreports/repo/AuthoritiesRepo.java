package se.alipsa.renjin.webreports.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.alipsa.renjin.webreports.model.Authorities;
import se.alipsa.renjin.webreports.model.AuthoritiesPk;
import se.alipsa.renjin.webreports.model.User;

@Repository
public interface AuthoritiesRepo extends CrudRepository<Authorities, AuthoritiesPk> {

  @Modifying
  @Query("DELETE from Authorities a where a.pk.user = ?1")
  void deleteByUser(User u);

}
