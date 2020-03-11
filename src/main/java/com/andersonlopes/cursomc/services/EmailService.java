package com.andersonlopes.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.andersonlopes.cursomc.domain.Pedido;

public interface EmailService {
	
	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage msg);

}
