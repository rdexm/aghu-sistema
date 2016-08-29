package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * 
 * @author diego.pacheco
 *
 */

public class ProcedimentoAtendimentoConsultaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9222398527547969573L;
	private Integer seq;
	private String descricao;
	private Byte quantidade;
	private DominioSituacao situacao;  // Ativo indica procedimento realizado
	private Boolean padraoConsulta;
	private Integer phiSeq;
	private Integer cidSeq;	
	private String cidCodigo;
	private Short pedSeq;
	private Boolean nenhumProcedimentoRealizado;
	private Boolean realizado;
	private String cidDescricao;
	
	
	// Atributo utiizado para renderização customizada do procedimento que estiver em edição 
	private Boolean procedimentoEmEdicao;  
	
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Byte getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(Byte quantidade) {
		this.quantidade = quantidade;
	}
	
	/**
	 * Ativo indica um procedimento realizado.
	 * 
	 * @return
	 */
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	public Boolean getPadraoConsulta() {
		return padraoConsulta;
	}
	
	public void setPadraoConsulta(Boolean padraoConsulta) {
		this.padraoConsulta = padraoConsulta;
	}
	
	public Integer getPhiSeq() {
		return phiSeq;
	}
	
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	
	public Integer getCidSeq() {
		return cidSeq;
	}
	
	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}
	
	public String getCidCodigo() {
		return cidCodigo;
	}
	
	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}
	
	public Short getPedSeq() {
		return pedSeq;
	}
	
	public void setPedSeq(Short pedSeq) {
		this.pedSeq = pedSeq;
	}
	
	public Boolean getNenhumProcedimentoRealizado() {
		return nenhumProcedimentoRealizado;
	}

	public void setNenhumProcedimentoRealizado(Boolean nenhumProcedimentoRealizado) {
		this.nenhumProcedimentoRealizado = nenhumProcedimentoRealizado;
	}

	public Boolean getRealizado() {
		return realizado;
	}

	public void setRealizado(Boolean realizado) {
		this.realizado = realizado;
		if (realizado) {
			situacao = DominioSituacao.A;	
		}
		else {
			situacao = DominioSituacao.I;
		}
	}

	public Boolean getProcedimentoEmEdicao() {
		return procedimentoEmEdicao;
	}

	public void setProcedimentoEmEdicao(Boolean procedimentoEmEdicao) {
		this.procedimentoEmEdicao = procedimentoEmEdicao;
	}

	public String getCidDescricao() {
		return cidDescricao;
	}

	public void setCidDescricao(String cidDescricao) {
		this.cidDescricao = cidDescricao;
	}
	
}
