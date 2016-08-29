package br.gov.mec.aghu.core.mail;

import java.io.Serializable;
import java.util.List;

/**
 * @author dlaks
 *
 */
public class EmailInformation implements Serializable {

	private static final long serialVersionUID = -5095753682654873698L;

	private ContatoEmail remetente;

	private List<ContatoEmail> destinatarios;

	private List<ContatoEmail> destinatariosOcultos;

	private String assunto;

	private String conteudo;
	
	private AnexoEmail[] anexos;
	
	public EmailInformation(ContatoEmail remetente, List<ContatoEmail> destinatarios,
			List<ContatoEmail> destinatariosOcultos, String assunto,
			String conteudo, AnexoEmail... anexos) {
		this.remetente = remetente;
		this.destinatarios = destinatarios;
		this.destinatariosOcultos = destinatariosOcultos;
		this.assunto = assunto;
		this.conteudo = conteudo;
		this.anexos = anexos;
	}

	public ContatoEmail getRemetente() {
		return remetente;
	}

	public void setRemetente(ContatoEmail remetente) {
		this.remetente = remetente;
	}

	public List<ContatoEmail> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(List<ContatoEmail> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public List<ContatoEmail> getDestinatariosOcultos() {
		return destinatariosOcultos;
	}

	public void setDestinatariosOcultos(List<ContatoEmail> destinatariosOcultos) {
		this.destinatariosOcultos = destinatariosOcultos;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public AnexoEmail[] getAnexos() {
		return anexos;
	}

	public void setAnexos(AnexoEmail[] anexos) {
		this.anexos = anexos;
	}

}
