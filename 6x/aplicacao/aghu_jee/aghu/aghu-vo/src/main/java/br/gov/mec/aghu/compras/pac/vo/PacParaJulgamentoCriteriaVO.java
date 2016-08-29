package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;

/**
 * Critério de Consulta de PAC's para Julgamento
 * 
 * @author mlcruz
 */
public class PacParaJulgamentoCriteriaVO {	
	/** Nro. do PAC */
	private Integer nroPac;
	
	/** Descrição do PAC */
	private String descricao;
	
	/** Modalidade do PAC */
	private ScoModalidadeLicitacao modalidade;
	
	/** Situação do PAC */
	private DominioSituacaoLicitacao situacao = DominioSituacaoLicitacao.GR;

	/** Pac com Propostas */
	private Boolean pacPossuiProposta = Boolean.TRUE;
	
	// Getters/Setters

	public Integer getNroPac() {
		return nroPac;
	}

	public void setNroPac(Integer nroPac) {
		this.nroPac = nroPac;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}

	public DominioSituacaoLicitacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoLicitacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getPacPossuiProposta() {
		return pacPossuiProposta;
	}

	public void setPacPossuiProposta(Boolean pacPossuiProposta) {
		this.pacPossuiProposta = pacPossuiProposta;
	}
}