package br.gov.mec.aghu.indicadores.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.mec.aghu.core.utils.AghuNumberFormat;


public class UnidadeIndicadoresVO {

	private Date competenciaInternacao;
	private Long tipo;
	private String andarAlaDescricao;
	private Short unfSeq;
	private String sigla;
	private Long pacientesMesAnterior;
	private Long totAltas;
	private Long totEntradasInternacoes;
	private Long totEntradasOutrasEspecialidades;
	private Long totEntradasOutrasUnidades;
	private Long totInternacoesMes;
	private Long totInternacoesAreaSatelite;
	private Long totObitosMais24hs;
	private Long totObitosMenos24hs;
	private Long totSaidas;
	private Long totSaidaOutrasEspecialidades;
	private Long totSaidaOutrasUnidades;
	private Long totSaldo;
	private Double capacReferencial;
	private Double totBloqueios;
	private Double pacHospitalDia;
	private Double mediaPacienteDia;
	private Long leitoDia;
	private Double percentualOcupacao;
	private Double mediaPermanencia;
	private Double indiceMortGeral;
	private Double indiceMortEspecialidade;
	private Double indiceIntervaloSubstituicao;
	private Double indiceRenovacao;
	private Long totEntradas;

	public Date getCompetenciaInternacao() {
		return competenciaInternacao;
	}

	public void setCompetenciaInternacao(Date competenciaInternacao) {
		this.competenciaInternacao = competenciaInternacao;
	}

	public Long getTipo() {
		return tipo;
	}

	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}

	public String getAndarAlaDescricao() {
		return andarAlaDescricao;
	}

	public void setAndarAlaDescricao(String andarAlaDescricao) {
		this.andarAlaDescricao = andarAlaDescricao;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Long getPacientesMesAnterior() {
		return pacientesMesAnterior;
	}

	public void setPacientesMesAnterior(Long pacientesMesAnterior) {
		this.pacientesMesAnterior = pacientesMesAnterior;
	}

	public Long getTotAltas() {
		return totAltas;
	}

	public void setTotAltas(Long totAltas) {
		this.totAltas = totAltas;
	}

	public Long getTotEntradasInternacoes() {
		return totEntradasInternacoes;
	}

	public void setTotEntradasInternacoes(Long totEntradasInternacoes) {
		this.totEntradasInternacoes = totEntradasInternacoes;
	}

	public Long getTotEntradasOutrasEspecialidades() {
		return totEntradasOutrasEspecialidades;
	}

	public void setTotEntradasOutrasEspecialidades(
			Long totEntradasOutrasEspecialidades) {
		this.totEntradasOutrasEspecialidades = totEntradasOutrasEspecialidades;
	}

	public Long getTotEntradasOutrasUnidades() {
		return totEntradasOutrasUnidades;
	}

	public void setTotEntradasOutrasUnidades(Long totEntradasOutrasUnidades) {
		this.totEntradasOutrasUnidades = totEntradasOutrasUnidades;
	}

	public Long getTotInternacoesMes() {
		return totInternacoesMes;
	}

	public void setTotInternacoesMes(Long totInternacoesMes) {
		this.totInternacoesMes = totInternacoesMes;
	}

	public Long getTotInternacoesAreaSatelite() {
		return totInternacoesAreaSatelite;
	}

	public void setTotInternacoesAreaSatelite(Long totInternacoesAreaSatelite) {
		this.totInternacoesAreaSatelite = totInternacoesAreaSatelite;
	}

	public Long getTotObitosMais24hs() {
		return totObitosMais24hs;
	}

	public void setTotObitosMais24hs(Long totObitosMais24hs) {
		this.totObitosMais24hs = totObitosMais24hs;
	}

	public Long getTotObitosMenos24hs() {
		return totObitosMenos24hs;
	}

	public void setTotObitosMenos24hs(Long totObitosMenos24hs) {
		this.totObitosMenos24hs = totObitosMenos24hs;
	}

	public Long getTotSaidas() {
		return totSaidas;
	}

	public void setTotSaidas(Long totSaidas) {
		this.totSaidas = totSaidas;
	}

	public Long getTotSaidaOutrasEspecialidades() {
		return totSaidaOutrasEspecialidades;
	}

	public void setTotSaidaOutrasEspecialidades(
			Long totSaidaOutrasEspecialidades) {
		this.totSaidaOutrasEspecialidades = totSaidaOutrasEspecialidades;
	}

	public Long getTotSaidaOutrasUnidades() {
		return totSaidaOutrasUnidades;
	}

	public void setTotSaidaOutrasUnidades(Long totSaidaOutrasUnidades) {
		this.totSaidaOutrasUnidades = totSaidaOutrasUnidades;
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

	public void setCapacReferencial(Double capacReferencial) {
		this.capacReferencial = capacReferencial;
	}

	public Double getTotBloqueios() {
		return totBloqueios;
	}

	public void setTotBloqueios(Double totBloqueios) {
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

	public Double getIndiceMortEspecialidade() {
		return indiceMortEspecialidade;
	}

	public void setIndiceMortEspecialidade(Double indiceMortEspecialidade) {
		this.indiceMortEspecialidade = indiceMortEspecialidade;
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

	public Long getTotEntradas() {
		return totEntradas;
	}

	public void setTotEntradas(Long totEntradas) {
		this.totEntradas = totEntradas;
	}

	/**
	 * Método para formatar a data da competencia
	 * 
	 * @return
	 */
	public String getCompetenciaInternacaoString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (this.getCompetenciaInternacao() == null) {
			return "";
		} else {
			return sdf.format(this.getCompetenciaInternacao());
		}
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getCapacReferencialString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.capacReferencial);
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getTotBloqueiosString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.totBloqueios);
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getMediaPacienteDiaString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.mediaPacienteDia);
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getPercentualOcupacaoString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.percentualOcupacao);
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getMediaPermanenciaString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.mediaPermanencia);
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getIndiceMortGeralString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.indiceMortGeral);
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getIndiceMortEspecialidadeString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.indiceMortEspecialidade);
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getIndiceIntervaloSubstituicaoString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.indiceIntervaloSubstituicao);
	}

	/**
	 * Método para formatar valor com separador decimal correto para
	 * apresentação em relatório
	 * 
	 * @return
	 */
	public String getindiceRenovacaoString() {
		return AghuNumberFormat.formatarNumeroMoeda(this.indiceRenovacao);
	}

	public String getValoresAtributos() {
		StringBuilder sb = new StringBuilder(30);

		sb.append(';').append(competenciaInternacao)
		.append(';').append(tipo)
		.append(';').append(andarAlaDescricao)
		.append(';').append(unfSeq)
		.append(';').append(sigla)
		.append(';').append(pacientesMesAnterior)
		.append(';').append(totAltas)
		.append(';').append(totEntradasInternacoes)
		.append(';').append(totEntradasOutrasEspecialidades)
		.append(';').append(totEntradasOutrasUnidades)
		.append(';').append(totInternacoesMes)
		.append(';').append(totInternacoesAreaSatelite)
		.append(';').append(totObitosMais24hs)
		.append(';').append(totObitosMenos24hs)
		.append(';').append(totSaidas)
		.append(';').append(totSaidaOutrasEspecialidades)
		.append(';').append(totSaidaOutrasUnidades)
		.append(';').append(totSaldo)
		.append(';').append(capacReferencial)
		.append(';').append(totBloqueios)
		.append(';').append(pacHospitalDia)
		.append(';').append(mediaPacienteDia)
		.append(';').append(leitoDia)
		.append(';').append(percentualOcupacao)
		.append(';').append(mediaPermanencia)
		.append(';').append(indiceMortGeral)
		.append(';').append(indiceMortEspecialidade)
		.append(';').append(indiceIntervaloSubstituicao)
		.append(';').append(indiceRenovacao)
		.append(';').append(totEntradas);

		return sb.toString();
	}

}
