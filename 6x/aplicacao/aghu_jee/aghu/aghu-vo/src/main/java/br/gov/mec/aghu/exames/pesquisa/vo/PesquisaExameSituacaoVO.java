package br.gov.mec.aghu.exames.pesquisa.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.core.commons.BaseBean;


public class PesquisaExameSituacaoVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5816275609929062180L;
	private Date dataHoraEvento;
	private Integer solicitacao;
	private Integer prontuario;
	private String nomePaciente;
	private String localizacao;
	private String exameMaterial;
	private Date dataSolicitacao;
	private String solicitante;
	private String descricaoConvenio;
	private DominioOrigemAtendimento origem;
	private String origemRel;
	private String dataSolicRel; //Data formatada para o relatório de exames por situação
	private Integer numUnidade;
	private String dataHoraEventoRel;
	private String prontuarioRel;
	
	
	/**
	 * @return the dataHoraEvento
	 */
	public Date getDataHoraEvento() {
		return dataHoraEvento;
	}
	/**
	 * @param dataHoraEvento the dataHoraEvento to set
	 */
	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}
	/**
	 * @return the solicitacao
	 */
	public Integer getSolicitacao() {
		return solicitacao;
	}
	/**
	 * @param solicitacao the solicitacao to set
	 */
	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}
	/**
	 * @return the prontuario
	 */
	public Integer getProntuario() {
		return prontuario;
	}
	/**
	 * @param prontuario the prontuario to set
	 */
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	/**
	 * @return the nomePaciente
	 */
	public String getNomePaciente() {
		return nomePaciente;
	}
	/**
	 * @param nomePaciente the nomePaciente to set
	 */
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	/**
	 * @return the localizacao
	 */
	public String getLocalizacao() {
		return localizacao;
	}
	/**
	 * @param localizacao the localizacao to set
	 */
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	/**
	 * @return the exameMaterial
	 */
	public String getExameMaterial() {
		return exameMaterial;
	}
	/**
	 * @param exameMaterial the exameMaterial to set
	 */
	public void setExameMaterial(String exameMaterial) {
		this.exameMaterial = exameMaterial;
	}
	/**
	 * @return the dataSolicitacao
	 */
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}
	/**
	 * @param dataSolicitacao the dataSolicitacao to set
	 */
	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}
	/**
	 * @return the solicitante
	 */
	public String getSolicitante() {
		return solicitante;
	}
	/**
	 * @param solicitante the solicitante to set
	 */
	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}
	/**
	 * @return the descricaoConvenio
	 */
	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}
	/**
	 * @param descricaoConvenio the descricaoConvenio to set
	 */
	public void setDescricaoConvenio(String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}
	/**
	 * @return the origem
	 */
	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}
	/**
	 * @param origem the origem to set
	 */
	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}
	public String getDataSolicRel() {
		return dataSolicRel;
	}
	public void setDataSolicRel(String dataSolicRel) {
		this.dataSolicRel = dataSolicRel;
	}
	public Integer getNumUnidade() {
		return numUnidade;
	}
	public void setNumUnidade(Integer numUnidade) {
		this.numUnidade = numUnidade;
	}
	public String getDataHoraEventoRel() {
		return dataHoraEventoRel;
	}
	public void setDataHoraEventoRel(String dataHoraEventoRel) {
		this.dataHoraEventoRel = dataHoraEventoRel;
	}
	public String getOrigemRel() {
		return origemRel;
	}
	public void setOrigemRel(String origemRel) {
		this.origemRel = origemRel;
	}
	public String getProntuarioRel() {
		return prontuarioRel;
	}
	public void setProntuarioRel(String prontuarioRel) {
		this.prontuarioRel = prontuarioRel;
	}
	
	
}
