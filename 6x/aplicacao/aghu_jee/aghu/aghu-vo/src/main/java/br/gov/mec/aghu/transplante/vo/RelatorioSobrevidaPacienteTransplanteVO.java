package br.gov.mec.aghu.transplante.vo;

public class RelatorioSobrevidaPacienteTransplanteVO {
	
	private String prontuario;
	private String nome;
	private String tipo;
	private String dataTransplante;
	private String dataUltimoAtendimento;
	private String sobrevida;
	
	
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
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getDataTransplante() {
		return dataTransplante;
	}
	public void setDataTransplante(String dataTransplante) {
		this.dataTransplante = dataTransplante;
	}
	public String getDataUltimoAtendimento() {
		return dataUltimoAtendimento;
	}
	public void setDataUltimoAtendimento(String dataUltimoAtendicmento) {
		this.dataUltimoAtendimento = dataUltimoAtendicmento;
	}
	public String getSobrevida() {
		return sobrevida;
	}
	public void setSobrevida(String sobrevida) {
		this.sobrevida = sobrevida;
	}
	
}
