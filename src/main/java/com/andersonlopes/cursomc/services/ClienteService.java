package com.andersonlopes.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andersonlopes.cursomc.domain.Cliente;
import com.andersonlopes.cursomc.repositories.ClienteRepository;
import com.andersonlopes.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repo;
	
	public Cliente buscar(Integer id) {
		Optional<Cliente> foundCliente = repo.findById(id);
		return foundCliente.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id 
				+ ", Tipo: " + Cliente.class.getName()));
	}

}
