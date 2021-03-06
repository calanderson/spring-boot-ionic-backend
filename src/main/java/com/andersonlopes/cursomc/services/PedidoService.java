package com.andersonlopes.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.andersonlopes.cursomc.domain.Cliente;
import com.andersonlopes.cursomc.domain.ItemPedido;
import com.andersonlopes.cursomc.domain.PagamentoComBoleto;
import com.andersonlopes.cursomc.domain.Pedido;
import com.andersonlopes.cursomc.domain.Produto;
import com.andersonlopes.cursomc.domain.enums.EstadoPagamento;
import com.andersonlopes.cursomc.repositories.ClienteRepository;
import com.andersonlopes.cursomc.repositories.ItemPedidoRepository;
import com.andersonlopes.cursomc.repositories.PagamentoRepository;
import com.andersonlopes.cursomc.repositories.PedidoRepository;
import com.andersonlopes.cursomc.repositories.ProdutoRepository;
import com.andersonlopes.cursomc.security.UserSS;
import com.andersonlopes.cursomc.services.exceptions.AuthorizationException;
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
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EmailService emailService;
	
	public Pedido find(Integer id) {
		Optional<Pedido> foundPedido = repo.findById(id);
		return foundPedido.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id 
				+ ", Tipo: " + Pedido.class.getName()));
	}

	public Pedido insert(Pedido pedido) {
		pedido.setId(null);
		pedido.setInstante(new Date());
		Cliente cliente = clienteRepository.findById(pedido.getCliente().getId()).get();
		if (cliente != null) {
			pedido.setCliente(cliente);
		}
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
				ip.setProduto(produto.get());
				ip.setPreco(produto.get().getPreco());
			}
			ip.setPedido(pedido);
		}
		itemPedidoRepository.saveAll(pedido.getItens());
		emailService.sendOrderConfirmationHtmlEmail(pedido);
		return pedido;
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
		UserSS user = UserService.authenticated();
		if(user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageRequest = PageRequest.of(page, linesPerPage,Direction.valueOf(direction), orderBy); 
		Cliente cliente = clienteRepository.findById(user.getId()).get();
		return repo.findByCliente(cliente, pageRequest);
	}

}
