package com.andersonlopes.cursomc.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andersonlopes.cursomc.domain.Cidade;
import com.andersonlopes.cursomc.domain.Cliente;
import com.andersonlopes.cursomc.domain.Endereco;
import com.andersonlopes.cursomc.domain.enums.TipoCliente;
import com.andersonlopes.cursomc.dto.ClienteDTO;
import com.andersonlopes.cursomc.dto.ClienteNewDTO;
import com.andersonlopes.cursomc.repositories.ClienteRepository;
import com.andersonlopes.cursomc.repositories.EnderecoRepository;
import com.andersonlopes.cursomc.services.exceptions.DataIntegrityException;
import com.andersonlopes.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	public Cliente find(Integer id) {
		Optional<Cliente> foundCliente = clienteRepository.findById(id);
		return foundCliente.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id 
				+ ", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente newCliente) {
		newCliente.setId(null);
		newCliente = clienteRepository.save(newCliente);
		enderecoRepository.saveAll(newCliente.getEnderecos());
		return newCliente;
	}

	public Cliente update(Cliente newCliente) {
		Cliente persistedCliente = find(newCliente.getId());
		updateData(persistedCliente, newCliente);
		return clienteRepository.save(persistedCliente);
	}

	public void delete(Integer id) {
		find(id);
		try {
			clienteRepository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir um cliente que há pedidos relacionadas");
		}
	}

	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage,Direction.valueOf(direction), orderBy); 
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}
	
	public Cliente fromDTO(@Valid ClienteNewDTO objDto) {
		Cliente cliente = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()));
		Cidade cidade = new Cidade(objDto.getCidadeId(), null, null);
		Endereco endereco = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cliente, cidade);
		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(objDto.getTelefone1());
		if(objDto.getTelefone2() != null) {
			cliente.getTelefones().add(objDto.getTelefone2());
		}
		if(objDto.getTelefone3() != null) {
			cliente.getTelefones().add(objDto.getTelefone3());
		}
		return cliente;
	}

	private void updateData(Cliente persistedCliente, Cliente newCliente) {
		persistedCliente.setNome(newCliente.getNome());
		persistedCliente.setEmail(newCliente.getEmail());
	}

}
