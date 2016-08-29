package br.gov.mec.aghu.blococirurgico.vo;

public class RelatorioResumoCirurgiasRealizadasPorPeriodoListVO {

	private String sigla;
	private String nomeEspecialidade;
	private Integer emergencia;
	private Integer urgencia;
	private Integer eletiva;
	private Integer canceladas;
	private Integer realizadas;

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Integer getEmergencia() {
		return emergencia;
	}

	public void setEmergencia(Integer emergencia) {
		this.emergencia = emergencia;
	}

	public Integer getUrgencia() {
		return urgencia;
	}

	public void setUrgencia(Integer urgencia) {
		this.urgencia = urgencia;
	}

	public Integer getEletiva() {
		return eletiva;
	}

	public void setEletiva(Integer eletiva) {
		this.eletiva = eletiva;
	}

	public Integer getCanceladas() {
		return canceladas;
	}

	public void setCanceladas(Integer canceladas) {
		this.canceladas = canceladas;
	}

	public Integer getRealizadas() {
		return realizadas;
	}

	public void setRealizadas(Integer realizadas) {
		this.realizadas = realizadas;
	}

}
