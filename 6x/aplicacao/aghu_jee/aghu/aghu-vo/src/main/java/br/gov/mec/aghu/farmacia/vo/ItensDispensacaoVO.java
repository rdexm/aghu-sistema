package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



public class ItensDispensacaoVO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6900226788917714456L;

	private Integer unfSeqSol;
	
	private String andarAlaDescricaoSol;
	
	private Integer prontuario;
	
	private String nomePaciente;
	
	private Date dtHrInclusaoItem;
	
	private Integer medCodigo;
	
	private String medDescricao;
	
	private String siglaApr;
	
	//Variaveis do Hint	
	private BigDecimal qtdeDispensadaDispensacao;	
	
	private String situacaoDispensacao;
	
	private Short unfSeqFar;
	
	private String descricaoFar;
	
	//Obter da tabela AfaTipoOcorDispensacoes pela chave da AfaDispensacaoMdtos
	private String descricaoTipoOcorrencia;
	
	private String nomeResponsavel;

	
	
	//Getters and Setters
	
	public Integer getUnfSeqSol() {
		return unfSeqSol;
	}

	public void setUnfSeqSol(Integer unfSeqSol) {
		this.unfSeqSol = unfSeqSol;
	}

	public String getAlaDescricaoSol() {
		return andarAlaDescricaoSol;
	}

	public void setAlaDescricaoSol(String alaDescricaoSol) {
		this.andarAlaDescricaoSol = alaDescricaoSol;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getDtHrInclusaoItem() {
		return dtHrInclusaoItem;
	}

	public void setDtHrInclusaoItem(Date dtHrInclusaoItem) {
		this.dtHrInclusaoItem = dtHrInclusaoItem;
	}

	public Integer getMatCodigo() {
		return medCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.medCodigo = matCodigo;
	}

	public String getDescricaoMed() {
		return medDescricao;
	}

	public void setDescricaoMed(String descricaoMed) {
		this.medDescricao = descricaoMed;
	}

	public String getSiglaApr() {
		return siglaApr;
	}

	public void setSiglaApr(String siglaApr) {
		this.siglaApr = siglaApr;
	}

	public BigDecimal getQtdeDispensadaDispensacao() {
		return qtdeDispensadaDispensacao;
	}

	public void setQtdeDispensadaDispensacao(BigDecimal qtdeDispensadaDispensacao) {
		this.qtdeDispensadaDispensacao = qtdeDispensadaDispensacao;
	}
	
	public Short getUnfSeqFar() {
		return unfSeqFar;
	}

	public void setUnfSeqFar(Short unfSeqFar) {
		this.unfSeqFar = unfSeqFar;
	}

	public String getDescricaoFar() {
		return descricaoFar;
	}

	public void setDescricaoFar(String descricaoFar) {
		this.descricaoFar = descricaoFar;
	}

	public String getDescricaoTipoOcorrencia() {
		return descricaoTipoOcorrencia;
	}

	public void setDescricaoTipoOcorrencia(String descricaoTipoOcorrencia) {
		this.descricaoTipoOcorrencia = descricaoTipoOcorrencia;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public String getSituacaoDispensacao() {
		return situacaoDispensacao;
	}

	public void setSituacaoDispensacao(String situacaoDispensacao) {
		this.situacaoDispensacao = situacaoDispensacao;
	}
	
	
	
}
