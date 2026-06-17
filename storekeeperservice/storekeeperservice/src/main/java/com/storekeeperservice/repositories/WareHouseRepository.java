package com.storekeeperservice.repositories;

import com.storekeeperservice.entities.WareHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WareHouseRepository extends JpaRepository<WareHouse, UUID> {

	Optional<WareHouse> findByWareHouseName(String wareHouseName);

}
