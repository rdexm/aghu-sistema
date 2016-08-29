package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 */
public class GrupoComponenteNPTVO implements BaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7279419778019466204L;
	private Short seq;
	private Date movimentacao;
	private String operacao;
	private String usuario;
	private String responsavel;
	private String descricao;
	private String observacao;
	private Boolean situacaoBoolean;
	private Date criadoEm;
	private String criadoPor;

	

	public Boolean getSituacaoBoolean() {
		return situacaoBoolean;
	}

	public void setSituacaoBoolean(Boolean situacaoBoolean) {
		this.situacaoBoolean = situacaoBoolean;
	}

	public Date getMovimentacao() {
		return movimentacao;
	}
	
	public void setMovimentacao(Date movimentacao) {
		this.movimentacao = movimentacao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getObservacao() {
		return observacao;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	public String getCriadoPor() {
		return criadoPor;
	}
	
	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

}