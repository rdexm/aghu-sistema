package br.gov.mec.controller;

public class EventoLoginMalSucedido {
	
	private String username;
	
	public EventoLoginMalSucedido(String username){
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
