package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.core.commons.BaseBean;


public class PesquisaReferencialClinicaEspecialidadeVO implements BaseBean, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1423535673785823034L;

	private Number codigoClinica;

	private Number seqEspecialidade;

	private String siglaEspecialidade;

	private Number capacidadeReferencial;

	private BigDecimal mediaPermanencia;

	private BigDecimal percentualOcupacao;

	private Long pac;
	
	private Long elet;

	private Long urg;

	private Long bloqueios;
	
	private Number capacidadeReferencialCalculado;
	
	private Long diferenca;

	private Long cti;

	private Long aptos;

	private Long outrasUnidades;

	private Long outrasClinicas;

	private Long total;
	
	private BigDecimal mediaPacienteDia;

	public PesquisaReferencialClinicaEspecialidadeVO(Number codigoClinica, Number seqEspecialidade,
			String siglaEspecialidade, Number capacidadeReferencial,
			BigDecimal mediaPermanencia, BigDecimal percentualOcupacao) {
		this.codigoClinica = codigoClinica;
		this.seqEspecialidade = seqEspecialidade;
		this.siglaEspecialidade = siglaEspecialidade;
		this.capacidadeReferencial = capacidadeReferencial;
		this.mediaPermanencia = mediaPermanencia;
		this.percentualOcupacao = percentualOcupacao;
	}

	public Number getCodigoClinica() {
		return codigoClinica;
	}

	public void setCodigoClinica(Number codigoClinica) {
		this.codigoClinica = codigoClinica;
	}

	public Number getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Number seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public Number getCapacidadeReferencial() {
		return capacidadeReferencial;
	}

	public void setCapacidadeReferencial(Number capacidadeReferencial) {
		this.capacidadeReferencial = capacidadeReferencial;
	}

	public BigDecimal getMediaPermanencia() {
		return mediaPermanencia;
	}

	public void setMediaPermanencia(BigDecimal mediaPermanencia) {
		this.mediaPermanencia = mediaPermanencia;
	}

	public BigDecimal getPercentualOcupacao() {
		return percentualOcupacao;
	}

	public void setPercentualOcupacao(BigDecimal percentualOcupacao) {
		this.percentualOcupacao = percentualOcupacao;
	}

	public Long getPac() {
		return pac;
	}

	public void setPac(Long pac) {
		this.pac = pac;
	}

	public Long getElet() {
		return elet;
	}

	public void setElet(Long elet) {
		this.elet = elet;
	}

	public Long getUrg() {
		return urg;
	}

	public void setUrg(Long urg) {
		this.urg = urg;
	}

	public Long getBloqueios() {
		return bloqueios;
	}

	public void setBloqueios(Long bloqueios) {
		this.bloqueios = bloqueios;
	}

	public Number getCapacidadeReferencialCalculado() {
		return capacidadeReferencialCalculado;
	}

	public void setCapacidadeReferencialCalculado(
			Number capacidadeReferencialCalculado) {
		this.capacidadeReferencialCalculado = capacidadeReferencialCalculado;
	}

	public Long getDiferenca() {
		return diferenca;
	}

	public void setDiferenca(Long diferenca) {
		this.diferenca = diferenca;
	}

	public Long getCti() {
		return cti;
	}

	public void setCti(Long cti) {
		this.cti = cti;
	}

	public Long getAptos() {
		return aptos;
	}

	public void setAptos(Long aptos) {
		this.aptos = aptos;
	}

	public Long getOutrasUnidades() {
		return outrasUnidades;
	}

	public void setOutrasUnidades(Long outrasUnidades) {
		this.outrasUnidades = outrasUnidades;
	}

	public Long getOutrasClinicas() {
		return outrasClinicas;
	}

	public void setOutrasClinicas(Long outrasClinicas) {
		this.outrasClinicas = outrasClinicas;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public BigDecimal getMediaPacienteDia() {
		return mediaPacienteDia;
	}

	public void setMediaPacienteDia(BigDecimal mediaPacienteDia) {
		this.mediaPacienteDia = mediaPacienteDia;
	}
	
}
