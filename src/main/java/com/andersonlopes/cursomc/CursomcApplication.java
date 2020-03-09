package com.andersonlopes.cursomc;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.andersonlopes.cursomc.domain.Categoria;
import com.andersonlopes.cursomc.domain.Cidade;
import com.andersonlopes.cursomc.domain.Cliente;
import com.andersonlopes.cursomc.domain.Endereco;
import com.andersonlopes.cursomc.domain.Estado;
import com.andersonlopes.cursomc.domain.ItemPedido;
import com.andersonlopes.cursomc.domain.Pagamento;
import com.andersonlopes.cursomc.domain.PagamentoComBoleto;
import com.andersonlopes.cursomc.domain.PagamentoComCartao;
import com.andersonlopes.cursomc.domain.Pedido;
import com.andersonlopes.cursomc.domain.Produto;
import com.andersonlopes.cursomc.domain.enums.EstadoPagamento;
import com.andersonlopes.cursomc.domain.enums.TipoCliente;
import com.andersonlopes.cursomc.repositories.CategoriaRepository;
import com.andersonlopes.cursomc.repositories.CidadeRepository;
import com.andersonlopes.cursomc.repositories.ClienteRepository;
import com.andersonlopes.cursomc.repositories.EnderecoRepository;
import com.andersonlopes.cursomc.repositories.EstadoRepository;
import com.andersonlopes.cursomc.repositories.ItemPedidoRepository;
import com.andersonlopes.cursomc.repositories.PagamentoRepository;
import com.andersonlopes.cursomc.repositories.PedidoRepository;
import com.andersonlopes.cursomc.repositories.ProdutoRepository;

@SpringBootApplication
public class CursomcApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(CursomcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}

}
