package br.gov.mec.aghu.internacao.business.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ProfissionaisEscalaIntenacaoVO implements BaseBean{

	private static final long serialVersionUID = -9103908319000409110L;
	private Short vinculoServidor;
	private Integer matriculaServidor;
	private String nomeServidor;
	private String numeroRegistroConselho;
	private String siglaEspecialidade;
	private Short codigoConvenio;
	private String descricaoConvenio;
	private Date dataInicio;
	private Date dataFim;

	private Integer codigoPessoa;
	private Short codigoQualificacao;
	private Short seqEspecialidade;

	// getters and setters
	public Short getVinculoServidor() {
		return vinculoServidor;
	}

	public void setVinculoServidor(Short vinculoServidor) {
		this.vinculoServidor = vinculoServidor;
	}

	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}

	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public String getNumeroRegistroConselho() {
		return numeroRegistroConselho;
	}

	public void setNumeroRegistroConselho(String numeroRegistroConselho) {
		this.numeroRegistroConselho = numeroRegistroConselho;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public Short getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(Short codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}

	public void setDescricaoConvenio(String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}

	public Integer getCodigoPessoa() {
		return codigoPessoa;
	}

	public void setCodigoPessoa(Integer codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public Short getCodigoQualificacao() {
		return codigoQualificacao;
	}

	public void setCodigoQualificacao(Short codigoQualificacao) {
		this.codigoQualificacao = codigoQualificacao;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

}
