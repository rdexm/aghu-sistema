package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

public class DetalharItemSolicitacaoSolicitacaoVO implements Serializable{


	//private AelItemSolicitacaoExames itemSolicitacaoExame;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8475171235022858177L;
	//private RapQualificacao qualificacaoSolicitante;
	//private RapQualificacao qualificacaoResponsavel;
	private Integer solicitacaoExameSeq;
	private Date solicitacaoExameCriadoEm;
	private String nroRegConselhoQualificacaoSolicitante;
	private String nroRegConselhoQualificacaoResponsavel;
	
	
	public void setSolicitacaoExameSeq(Integer solicitacaoExameSeq) {
		this.solicitacaoExameSeq = solicitacaoExameSeq;
	}
	public Integer getSolicitacaoExameSeq() {
		return solicitacaoExameSeq;
	}
	public void setSolicitacaoExameCriadoEm(Date solicitacaoExameCriadoEm) {
		this.solicitacaoExameCriadoEm = solicitacaoExameCriadoEm;
	}
	public Date getSolicitacaoExameCriadoEm() {
		return solicitacaoExameCriadoEm;
	}
	public void setNroRegConselhoQualificacaoSolicitante(
			String nroRegConselhoQualificacaoSolicitante) {
		this.nroRegConselhoQualificacaoSolicitante = nroRegConselhoQualificacaoSolicitante;
	}
	public String getNroRegConselhoQualificacaoSolicitante() {
		return nroRegConselhoQualificacaoSolicitante;
	}
	public void setNroRegConselhoQualificacaoResponsavel(
			String nroRegConselhoQualificacaoResponsavel) {
		this.nroRegConselhoQualificacaoResponsavel = nroRegConselhoQualificacaoResponsavel;
	}
	public String getNroRegConselhoQualificacaoResponsavel() {
		return nroRegConselhoQualificacaoResponsavel;
	}
}
