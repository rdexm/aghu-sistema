package br.gov.mec.aghu.indicadores.vo;

public class CapacidadeEspecialidadeIndicadoresVO {
	private Short esp;
	private Short capacidade;
	private Integer bloqueios;
	private Integer clinica;

	public Short getEsp() {
		return esp;
	}

	public void setEsp(Short esp) {
		this.esp = esp;
	}

	public Short getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Short capacidade) {
		this.capacidade = capacidade;
	}

	public Integer getBloqueios() {
		return bloqueios;
	}

	public void setBloqueios(Integer bloqueios) {
		this.bloqueios = bloqueios;
	}

	public Integer getClinica() {
		return clinica;
	}

	public void setClinica(Integer clinica) {
		this.clinica = clinica;
	}
}
