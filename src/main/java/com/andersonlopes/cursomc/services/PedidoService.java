package com.andersonlopes.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andersonlopes.cursomc.domain.Pedido;
import com.andersonlopes.cursomc.repositories.PedidoRepository;
import com.andersonlopes.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repo;
	
	public Pedido buscar(Integer id) {
		Optional<Pedido> foundPedido = repo.findById(id);
		return foundPedido.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id 
				+ ", Tipo: " + Pedido.class.getName()));
	}

}
