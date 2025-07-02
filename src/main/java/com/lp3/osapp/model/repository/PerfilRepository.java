package com.lp3.osapp.model.repository;

import com.lp3.osapp.model.entity.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {


    Optional<Perfil> findByCpf(String cpf);
}