package br.gov.mec.aghu.internacao.pesquisa.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ReferencialEspecialidadeProfissonalViewVO implements BaseBean{
	
	// Campos da view V_AIN_PES_REF_ESP_PRO 
	private Integer ordem; 
	private Short espSeq; 
	private Short espVinculo; 
	private Integer espMatricula;
	private Integer capacReferencial; // Quantidade de internações
	private Number inhMediaPermanencia; // Tempo médio de permanência geral
	private Number inhPercentualOcupacao; // Percentual de ocupação
	private Number inhMediaPacienteDia; // Media de paciente dia do mês
	
	public Integer getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getEspVinculo() {
		return this.espVinculo;
	}

	public void setEspVinculo(Short espVinculo) {
		this.espVinculo = espVinculo;
	}

	public Integer getEspMatricula() {
		return this.espMatricula;
	}

	public void setEspMatricula(Integer espMatricula) {
		this.espMatricula = espMatricula;
	}

	public Integer getCapacReferencial() {
		return this.capacReferencial;
	}

	public void setCapacReferencial(Integer capacReferencial) {
		this.capacReferencial = capacReferencial;
	}

	public Number getInhMediaPermanencia() {
		return this.inhMediaPermanencia;
	}

	public void setInhMediaPermanencia(Number inhMediaPermanencia) {
		this.inhMediaPermanencia = inhMediaPermanencia;
	}

	public Number getInhPercentualOcupacao() {
		return this.inhPercentualOcupacao;
	}

	public void setInhPercentualOcupacao(Number inhPercentualOcupacao) {
		this.inhPercentualOcupacao = inhPercentualOcupacao;
	}
	
	public Number getInhMediaPacienteDia() {
		return this.inhMediaPacienteDia;
	}

	public void setInhMediaPacienteDia(Number inhMediaPacienteDia) {
		this.inhMediaPacienteDia = inhMediaPacienteDia;
	}

}