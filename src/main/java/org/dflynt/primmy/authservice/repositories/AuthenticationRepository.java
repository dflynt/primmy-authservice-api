package org.dflynt.primmy.authservice.repositories;

import org.dflynt.primmy.authservice.models.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;


@Repository
public interface AuthenticationRepository extends CrudRepository<User, Integer> {

    User findByemail(String email);

}
