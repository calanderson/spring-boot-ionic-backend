package com.andersonlopes.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andersonlopes.cursomc.domain.ItemPedido;
import com.andersonlopes.cursomc.domain.PagamentoComBoleto;
import com.andersonlopes.cursomc.domain.Pedido;
import com.andersonlopes.cursomc.domain.Produto;
import com.andersonlopes.cursomc.domain.enums.EstadoPagamento;
import com.andersonlopes.cursomc.repositories.ItemPedidoRepository;
import com.andersonlopes.cursomc.repositories.PagamentoRepository;
import com.andersonlopes.cursomc.repositories.PedidoRepository;
import com.andersonlopes.cursomc.repositories.ProdutoRepository;
import com.andersonlopes.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repo;
	@Autowired
	private BoletoService boletoService;
	@Autowired
	private PagamentoRepository pagamentoRepository;
	@Autowired
	private ProdutoRepository produtoRepository;
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	public Pedido find(Integer id) {
		Optional<Pedido> foundPedido = repo.findById(id);
		return foundPedido.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id 
				+ ", Tipo: " + Pedido.class.getName()));
	}

	public Pedido insert(Pedido pedido) {
		pedido.setId(null);
		pedido.setInstante(new Date());
		pedido.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		pedido.getPagamento().setPedido(pedido);
		
		if(pedido.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) pedido.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, pedido.getInstante());
		}
		
		pedido = repo.save(pedido);
		pagamentoRepository.save(pedido.getPagamento());
		for(ItemPedido ip : pedido.getItens()) {
			ip.setDesconto(0.0);
			Optional<Produto> produto = produtoRepository.findById(ip.getProduto().getId());
			if(null != produto.get()) {
				ip.setPreco(produto.get().getPreco());
			}
			ip.setPedido(pedido);
		}
		itemPedidoRepository.saveAll(pedido.getItens());
		return pedido;
	}

}
