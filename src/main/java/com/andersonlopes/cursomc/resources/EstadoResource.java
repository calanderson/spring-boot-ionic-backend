package com.andersonlopes.cursomc.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.andersonlopes.cursomc.domain.Cidade;
import com.andersonlopes.cursomc.domain.Estado;
import com.andersonlopes.cursomc.dto.CidadeDTO;
import com.andersonlopes.cursomc.dto.EstadoDTO;
import com.andersonlopes.cursomc.services.CidadeService;
import com.andersonlopes.cursomc.services.EstadoService;

@RestController
@RequestMapping(value="/estados")
public class EstadoResource {

	@Autowired
	private EstadoService service;
	
	@Autowired
	private CidadeService cidadeService;
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<EstadoDTO>> findAll() {
		List<Estado> lista = service.findAll();
		List<EstadoDTO> listaDTO = lista.stream().map(estado -> new EstadoDTO(estado)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listaDTO);
	}
	
	@RequestMapping(value = "/{estado_id}/cidades", method=RequestMethod.GET)
	public ResponseEntity<List<CidadeDTO>> findCidadesByEstado(@PathVariable Integer estado_id) {
		List<Cidade> lista = cidadeService.findByEstado(estado_id);
		List<CidadeDTO> listaDTO = lista.stream().map(cidade -> new CidadeDTO(cidade)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listaDTO);
	}
}
