package com.suspensive.store.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.suspensive.store.models.entities.RoleEntity;
import com.suspensive.store.models.entities.RolesEnum;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity,Long>{
   RoleEntity findRoleEntityByRolesEnum(RolesEnum role);
}
