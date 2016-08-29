package br.gov.mec.aghu.indicadores.vo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.mec.aghu.core.utils.AghuNumberFormat;

/**
 * Classe que representa o resultado da query realizada para o report
 * ainr_indicador_clin
 * 
 * @oradb ainr_indicador_clin
 * 
 * @author lsamberg
 * 
 */

public class IndHospClinicaEspVO {
	private Long tipo;
	private Date competencia;
	private Long pacientesMesAnteriror;
	private Long totAltas;
	private Long totEntrInternacoes;
	private Long totEntrOutrasEspecialidades;
	private Long totEntrOutrasUnidades;
	private Long totInternacoesMes;
	private Long totIntAreaSatelite;
	private Long totObitosMais24h;
	private Long totObitosMenos24h;
	private Long totSaidas;
	private Long totSaidOutrasEspecialidades;
	private Long totSaidOutrasUnidades;
	private Long totSaldo;
	private Double capacReferencial;
	private BigDecimal totBloqueios;
	private Double pacHospitalDia;
	private Double mediaPacienteDia;
	private Long leitoDia;
	private Double percentualOcupacao;
	private Double mediaPermanencia;
	private Double indiceMortGeral;
	private Double indiceMortEspecialidade;
	private Double indiceIntervaloSubstituicao;
	private Double indiceRenovacao;
	private String sigla;
	private Long clcCodigo;
	private Long tipoUnidade;

	public Long getTipo() {
		return tipo;
	}

	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}

	public Date getCompetencia() {
		return competencia;
	}
	
	public String getCompetenciaString(){
		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

		if (this.getCompetencia() == null) {
			return "";
		} else {
			return sdf.format(this.getCompetencia());
		}
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Long getPacientesMesAnteriror() {
		return pacientesMesAnteriror;
	}

	public void setPacientesMesAnteriror(Long pacientesMesAnteriror) {
		this.pacientesMesAnteriror = pacientesMesAnteriror;
	}

	public Long getTotAltas() {
		return totAltas;
	}

	public void setTotAltas(Long totAltas) {
		this.totAltas = totAltas;
	}

	public Long getTotEntrInternacoes() {
		return totEntrInternacoes;
	}

	public void setTotEntrInternacoes(Long totEntrInternacoes) {
		this.totEntrInternacoes = totEntrInternacoes;
	}

	public Long getTotEntrOutrasEspecialidades() {
		return totEntrOutrasEspecialidades;
	}

	public void setTotEntrOutrasEspecialidades(Long totEntrOutrasEspecialidades) {
		this.totEntrOutrasEspecialidades = totEntrOutrasEspecialidades;
	}

	public Long getTotEntrOutrasUnidades() {
		return totEntrOutrasUnidades;
	}

	public void setTotEntrOutrasUnidades(Long totEntrOutrasUnidades) {
		this.totEntrOutrasUnidades = totEntrOutrasUnidades;
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

	public Long getTotObitosMais24h() {
		return totObitosMais24h;
	}

	public void setTotObitosMais24h(Long totObitosMais24h) {
		this.totObitosMais24h = totObitosMais24h;
	}

	public Long getTotObitosMenos24h() {
		return totObitosMenos24h;
	}

	public void setTotObitosMenos24h(Long totObitosMenos24h) {
		this.totObitosMenos24h = totObitosMenos24h;
	}

	public Long getTotSaidas() {
		return totSaidas;
	}

	public void setTotSaidas(Long totSaidas) {
		this.totSaidas = totSaidas;
	}

	public Long getTotSaidOutrasEspecialidades() {
		return totSaidOutrasEspecialidades;
	}

	public void setTotSaidOutrasEspecialidades(Long totSaidOutrasEspecialidades) {
		this.totSaidOutrasEspecialidades = totSaidOutrasEspecialidades;
	}

	public Long getTotSaidOutrasUnidades() {
		return totSaidOutrasUnidades;
	}

	public void setTotSaidOutrasUnidades(Long totSaidOutrasUnidades) {
		this.totSaidOutrasUnidades = totSaidOutrasUnidades;
	}

	public Long getTotSaldo() {
		return totSaldo;
	}

	public void setTotSaldo(Long totSaldo) {
		this.totSaldo = totSaldo;
	}

	public Double getCapacReferencial() {
		return capacReferencial;
	}
	
	public String getCapacReferencialString() {
		return AghuNumberFormat.formatarNumeroMoeda(capacReferencial);
	}

	public void setCapacReferencial(Double capacReferencial) {
		this.capacReferencial = capacReferencial;
	}

	public BigDecimal getTotBloqueios() {
		return totBloqueios;
	}
	
	public String getTotBloqueiosString() {
		return AghuNumberFormat.formatarNumeroMoeda(totBloqueios.doubleValue());
	}

	public void setTotBloqueios(BigDecimal totBloqueios) {
		this.totBloqueios = totBloqueios;
	}

	public Double getPacHospitalDia() {
		return pacHospitalDia;
	}

	public void setPacHospitalDia(Double pacHospitalDia) {
		this.pacHospitalDia = pacHospitalDia;
	}

	public Double getMediaPacienteDia() {
		return mediaPacienteDia;
	}
	
	public String getMediaPacienteDiaString() {
		return AghuNumberFormat.formatarNumeroMoeda(mediaPacienteDia);
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
	
	public String getPercentualOcupacaoString() {
		return AghuNumberFormat.formatarNumeroMoeda(percentualOcupacao);
	}

	public void setPercentualOcupacao(Double percentualOcupacao) {
		this.percentualOcupacao = percentualOcupacao;
	}

	public Double getMediaPermanencia() {
		return mediaPermanencia;
	}
	
	public String getMediaPermanenciaString() {
		return AghuNumberFormat.formatarNumeroMoeda(mediaPermanencia);
	}

	public void setMediaPermanencia(Double mediaPermanencia) {
		this.mediaPermanencia = mediaPermanencia;
	}

	public Double getIndiceMortGeral() {
		return indiceMortGeral;
	}
	
	public String getIndiceMortGeralString() {
		return AghuNumberFormat.formatarNumeroMoeda(indiceMortGeral);
	}

	public void setIndiceMortGeral(Double indiceMortGeral) {
		this.indiceMortGeral = indiceMortGeral;
	}

	public Double getIndiceMortEspecialidade() {
		return indiceMortEspecialidade;
	}
	
	public String getIndiceMortEspecialidadeString() {
		return AghuNumberFormat.formatarNumeroMoeda(indiceMortEspecialidade);
	}

	public void setIndiceMortEspecialidade(Double indiceMortEspecialidade) {
		this.indiceMortEspecialidade = indiceMortEspecialidade;
	}

	public Double getIndiceIntervaloSubstituicao() {
		return indiceIntervaloSubstituicao;
	}
	
	public String getIndiceIntervaloSubstituicaoString() {
		return AghuNumberFormat.formatarNumeroMoeda(indiceIntervaloSubstituicao);
	}

	public void setIndiceIntervaloSubstituicao(
			Double indiceIntervaloSubstituicao) {
		this.indiceIntervaloSubstituicao = indiceIntervaloSubstituicao;
	}

	public Double getIndiceRenovacao() {
		return indiceRenovacao;
	}
	
	public String getIndiceRenovacaoString() {
		return AghuNumberFormat.formatarNumeroMoeda(indiceRenovacao);
	}

	public void setIndiceRenovacao(Double indiceRenovacao) {
		this.indiceRenovacao = indiceRenovacao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Long getClcCodigo() {
		return clcCodigo;
	}

	public void setClcCodigo(Long clcCodigo) {
		this.clcCodigo = clcCodigo;
	}

	public Long getTipoUnidade() {
		return tipoUnidade;
	}

	public void setTipoUnidade(Long tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}

}
