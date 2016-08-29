package br.gov.mec.aghu.paciente.vo;

/**
 * Os dados armazenados nesse objeto representam uma etiqueta.
 * 
 * @author Ricardo Costa
 */
public class EtiquetaVO {

	private String nome;
	private String ltoLtoId;
	private String prontuario;

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

}
