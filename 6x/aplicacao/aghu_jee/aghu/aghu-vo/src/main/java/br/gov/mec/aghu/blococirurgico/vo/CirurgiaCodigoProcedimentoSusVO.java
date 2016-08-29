package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * VO da listagem de código SUS
 * 
 * @author aghu
 * 
 */
public class CirurgiaCodigoProcedimentoSusVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 203480330301828708L;
	// Seq da consulta inicial
	private Integer phiSeq;
	// Colunas da consulta C1
	private Long codTabela;
	private String iphDescricao;
	private Short iphPhoSeq;
	private DominioSituacao iphIndSituacao;
	private Integer iphSeq;
	private Short cpgGrcSeq;
	private BigDecimal valorTotal;
	private BigDecimal valorPerm;
	
	public CirurgiaCodigoProcedimentoSusVO() {
		super();
	}
	
	public CirurgiaCodigoProcedimentoSusVO(Integer phiSeq, Long codTabela, String iphDescricao, Short iphPhoSeq,
			DominioSituacao iphIndSituacao, Integer iphSeq, Short cpgGrcSeq) {
		super();
		this.phiSeq = phiSeq;
		this.codTabela = codTabela;
		this.iphDescricao = iphDescricao;
		this.iphPhoSeq = iphPhoSeq;
		this.iphIndSituacao = iphIndSituacao;
		this.iphSeq = iphSeq;
		this.cpgGrcSeq = cpgGrcSeq;
	}
	
	public enum Fields {

		PHI_SEQ("phiSeq"),
		COD_TABELA("codTabela"),
		IPH_DESCRICAO("iphDescricao"),
		IPH_PHO_SEQ("iphPhoSeq"),
		IPH_IND_SITUACAO("iphIndSituacao"),
		IPH_SEQ("iphSeq"),
		CPG_GRC_SEQ("cpgGrcSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	//Getters and Setters
	
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Long getCodTabela() {
		return codTabela;
	}
	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}
	public String getIphDescricao() {
		return iphDescricao;
	}
	public void setIphDescricao(String iphDescricao) {
		this.iphDescricao = iphDescricao;
	}
	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}
	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}
	public DominioSituacao getIphIndSituacao() {
		return iphIndSituacao;
	}
	public void setIphIndSituacao(DominioSituacao iphIndSituacao) {
		this.iphIndSituacao = iphIndSituacao;
	}
	public Integer getIphSeq() {
		return iphSeq;
	}
	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	public Short getCpgGrcSeq() {
		return cpgGrcSeq;
	}
	public void setCpgGrcSeq(Short cpgGrcSeq) {
		this.cpgGrcSeq = cpgGrcSeq;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getValorPerm() {
		return valorPerm;
	}

	public void setValorPerm(BigDecimal valorPerm) {
		this.valorPerm = valorPerm;
	}

}