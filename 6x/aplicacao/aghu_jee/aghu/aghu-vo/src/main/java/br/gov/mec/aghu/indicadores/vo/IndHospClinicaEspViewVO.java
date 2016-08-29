package br.gov.mec.aghu.indicadores.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * Classe que representa a view V_AIN_INDICADORES_TOT
 * 
 * @oradb V_AIN_INDICADORES_TOT
 * 
 * @author lsamberg
 * 
 */

public class IndHospClinicaEspViewVO {
	private Date competencia;
	private Long tipoUnidade;
	private Long clcCodigo;
	private String clinica;
	private Long totInternacoesMes;
	private Long totIntAreaSatelite;
	private Long totEntrOutrasClinicas;
	private Long totEntradas;
	private Long totAltas;
	private Long totObitosMenos48h;
	private Long totObitosMais48h;
	private Long totSaidOutrasClinicas;
	private Long totSaidOutrasUnidades;
	private Long totSaidas;
	private Long pacientesMesAnterior;
	private Long totSaldo;
	private Double capacidade;
	private BigDecimal totBloqueios;
	private Double pacienteDia;
	private Double mediaPacienteDia;
	private Long leitoDia;
	private Double percentualOcupacao;
	private Double mediaPermanencia;
	private Double indiceMortGeral;
	private Double indiceMortEspecifico;
	private Double indiceIntervaloSubstituicao;
	private Double indiceRenovacao;
	private String descricao;
	
	public IndHospClinicaEspViewVO(){
		
	}
	
	public IndHospClinicaEspViewVO(String zeros){
		tipoUnidade = Long.valueOf(0);
		clcCodigo = Long.valueOf(0);
		totInternacoesMes = Long.valueOf(0);
		totIntAreaSatelite = Long.valueOf(0);
		totEntrOutrasClinicas = Long.valueOf(0);
		totEntradas = Long.valueOf(0);
		totAltas = Long.valueOf(0);
		totObitosMenos48h = Long.valueOf(0);
		totObitosMais48h = Long.valueOf(0);
		totSaidOutrasClinicas = Long.valueOf(0);
		totSaidas = Long.valueOf(0);
		pacientesMesAnterior = Long.valueOf(0);
		totSaldo = Long.valueOf(0);
		capacidade = new Double(0);
		totBloqueios = BigDecimal.ZERO;
		pacienteDia = new Double(0);
		mediaPacienteDia = new Double(0);
		leitoDia = Long.valueOf(0);
		percentualOcupacao = new Double(0);
		mediaPermanencia = new Double(0);
		indiceMortGeral = new Double(0);
		indiceMortEspecifico = new Double(0);
		indiceIntervaloSubstituicao = new Double(0);
		indiceRenovacao = new Double(0);
		totSaidOutrasUnidades = Long.valueOf(0);
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Long getTipoUnidade() {
		return tipoUnidade;
	}

	public void setTipoUnidade(Long tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}

	public Long getClcCodigo() {
		return clcCodigo;
	}

	public void setClcCodigo(Long clcCodigo) {
		this.clcCodigo = clcCodigo;
	}

	public String getClinica() {
		return clinica;
	}

	public void setClinica(String clinica) {
		this.clinica = clinica;
	}

	public Long getTotInternacoesMes() {
		return totInternacoesMes;
	}

	public void setTotInternacoesMes(Long totInternacoesMes) {
		this.totInternacoesMes = totInternacoesMes;
	}

	public Long getTotIntAreaSatelite() {
		return totIntAreaSatelite;
	}

	public void setTotIntAreaSatelite(Long totIntAreaSatelite) {
		this.totIntAreaSatelite = totIntAreaSatelite;
	}

	public Long getTotEntrOutrasClinicas() {
		return totEntrOutrasClinicas;
	}

	public void setTotEntrOutrasClinicas(Long totEntrOutrasClinicas) {
		this.totEntrOutrasClinicas = totEntrOutrasClinicas;
	}

	public Long getTotEntradas() {
		return totEntradas;
	}

	public void setTotEntradas(Long totEntradas) {
		this.totEntradas = totEntradas;
	}

	public Long getTotAltas() {
		return totAltas;
	}

	public void setTotAltas(Long totAltas) {
		this.totAltas = totAltas;
	}

	public Long getTotObitosMenos48h() {
		return totObitosMenos48h;
	}

	public void setTotObitosMenos48h(Long totObitosMenos48h) {
		this.totObitosMenos48h = totObitosMenos48h;
	}

	public Long getTotObitosMais48h() {
		return totObitosMais48h;
	}

	public void setTotObitosMais48h(Long totObitosMais48h) {
		this.totObitosMais48h = totObitosMais48h;
	}

	public Long getTotSaidOutrasClinicas() {
		return totSaidOutrasClinicas;
	}

	public void setTotSaidOutrasClinicas(Long totSaidOutrasClinicas) {
		this.totSaidOutrasClinicas = totSaidOutrasClinicas;
	}

	public Long getTotSaidas() {
		return totSaidas;
	}

	public void setTotSaidas(Long totSaidas) {
		this.totSaidas = totSaidas;
	}

	public Long getPacientesMesAnterior() {
		return pacientesMesAnterior;
	}

	public void setPacientesMesAnterior(Long pacientesMesAnterior) {
		this.pacientesMesAnterior = pacientesMesAnterior;
	}

	public Long getTotSaldo() {
		return totSaldo;
	}

	public void setTotSaldo(Long totSaldo) {
		this.totSaldo = totSaldo;
	}

	public Double getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Double capacidade) {
		this.capacidade = capacidade;
	}

	public BigDecimal getTotBloqueios() {
		return totBloqueios;
	}

	public void setTotBloqueios(BigDecimal totBloqueios) {
		this.totBloqueios = totBloqueios;
	}

	public Double getPacienteDia() {
		return pacienteDia;
	}

	public void setPacienteDia(Double pacienteDia) {
		this.pacienteDia = pacienteDia;
	}

	public Double getMediaPacienteDia() {
		return mediaPacienteDia;
	}

	public void setMediaPacienteDia(Double mediaPacienteDia) {
		this.mediaPacienteDia = mediaPacienteDia;
	}

	public Long getLeitoDia() {
		return leitoDia;
	}

	public void setLeitoDia(Long leitoDia) {
		this.leitoDia = leitoDia;
	}

	public Double getPercentualOcupacao() {
		return percentualOcupacao;
	}

	public void setPercentualOcupacao(Double percentualOcupacao) {
		this.percentualOcupacao = percentualOcupacao;
	}

	public Double getMediaPermanencia() {
		return mediaPermanencia;
	}

	public void setMediaPermanencia(Double mediaPermanencia) {
		this.mediaPermanencia = mediaPermanencia;
	}

	public Double getIndiceMortGeral() {
		return indiceMortGeral;
	}

	public void setIndiceMortGeral(Double indiceMortGeral) {
		this.indiceMortGeral = indiceMortGeral;
	}

	public Double getIndiceMortEspecifico() {
		return indiceMortEspecifico;
	}

	public void setIndiceMortEspecifico(Double indiceMortEspecifico) {
		this.indiceMortEspecifico = indiceMortEspecifico;
	}

	public Double getIndiceIntervaloSubstituicao() {
		return indiceIntervaloSubstituicao;
	}

	public void setIndiceIntervaloSubstituicao(
			Double indiceIntervaloSubstituicao) {
		this.indiceIntervaloSubstituicao = indiceIntervaloSubstituicao;
	}

	public Double getIndiceRenovacao() {
		return indiceRenovacao;
	}

	public void setIndiceRenovacao(Double indiceRenovacao) {
		this.indiceRenovacao = indiceRenovacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getTotSaidOutrasUnidades() {
		return totSaidOutrasUnidades;
	}

	public void setTotSaidOutrasUnidades(Long totSaidOutrasUnidades) {
		this.totSaidOutrasUnidades = totSaidOutrasUnidades;
	}
	
	public String getValoresAtributos() {
		StringBuilder sb = new StringBuilder(30);
		
		sb.append(';').append(competencia)
		.append(';').append(tipoUnidade)
		.append(';').append(clcCodigo)
		.append(';').append(clinica)
		.append(';').append(totInternacoesMes)
		.append(';').append(totIntAreaSatelite)
		.append(';').append(totEntrOutrasClinicas)
		.append(';').append(totEntradas)
		.append(';').append(totAltas)
		.append(';').append(totObitosMenos48h)
		.append(';').append(totObitosMais48h)
		.append(';').append(totSaidOutrasClinicas)
		.append(';').append(totSaidas)
		.append(';').append(pacientesMesAnterior)
		.append(';').append(totSaldo)
		.append(';').append(capacidade)
		.append(';').append(totBloqueios)
		.append(';').append(pacienteDia)
		.append(';').append(mediaPacienteDia)
		.append(';').append(leitoDia)
		.append(';').append(percentualOcupacao)
		.append(';').append(mediaPermanencia)
		.append(';').append(indiceMortGeral)
		.append(';').append(indiceMortEspecifico)
		.append(';').append(indiceIntervaloSubstituicao)
		.append(';').append(indiceRenovacao);
		
		return sb.toString();
	}

}
