package com.andersonlopes.cursomc.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andersonlopes.cursomc.domain.Cidade;
import com.andersonlopes.cursomc.domain.Cliente;
import com.andersonlopes.cursomc.domain.Endereco;
import com.andersonlopes.cursomc.domain.enums.Perfil;
import com.andersonlopes.cursomc.domain.enums.TipoCliente;
import com.andersonlopes.cursomc.dto.ClienteDTO;
import com.andersonlopes.cursomc.dto.ClienteNewDTO;
import com.andersonlopes.cursomc.repositories.ClienteRepository;
import com.andersonlopes.cursomc.repositories.EnderecoRepository;
import com.andersonlopes.cursomc.security.UserSS;
import com.andersonlopes.cursomc.services.exceptions.AuthorizationException;
import com.andersonlopes.cursomc.services.exceptions.DataIntegrityException;
import com.andersonlopes.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private BCryptPasswordEncoder bpe;
	
	public Cliente find(Integer id) {
		UserSS user = UserService.authenticated();
		if(user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso Negado");
		}
		
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
	
	public Cliente findByEmail(String email) {
		UserSS user = UserService.authenticated();
		if(user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
			throw new AuthorizationException("Acesso negado!");
		}
		
		Cliente cliente = clienteRepository.findByEmail(email);
		if(cliente == null) {
			throw new ObjectNotFoundException("Objeto não encontrado! Id: " + user.getId() 
					+ ", Tipo: " + Cliente.class.getName());
		}
		return cliente;
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage,Direction.valueOf(direction), orderBy); 
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(@Valid ClienteNewDTO objDto) {
		Cliente cliente = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()), bpe.encode(objDto.getSenha()));
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
