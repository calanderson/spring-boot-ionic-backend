package com.andersonlopes.cursomc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.andersonlopes.cursomc.domain.Cidade;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Integer>{
	
	@Transactional
	@Query("SELECT cidade from Cidade cidade WHERE cidade.estado.id = :estadoId ORDER BY cidade.nome")
	public List<Cidade> findCidades(@Param("estadoId") Integer estado_id);

}
