package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CursorTipoListaPmrKitPreVO implements Serializable {

	private static final long serialVersionUID = -7500013611055760329L;
	
	private Integer seq;
	private Integer codigo;
	private String descricao;
	private Integer phiPrimeira;
	private Integer phiSegunda;

	private Short phoSeqPrimeira;
	private Integer iphSeqPrimeira;
	
	private Short phoSeqSegunda;
	private Integer iphSeqSegunda;
	
	private Integer cidSeq;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getPhiPrimeira() {
		return phiPrimeira;
	}

	public void setPhiPrimeira(Integer phiPrimeira) {
		this.phiPrimeira = phiPrimeira;
	}

	public Integer getPhiSegunda() {
		return phiSegunda;
	}

	public void setPhiSegunda(Integer phiSegunda) {
		this.phiSegunda = phiSegunda;
	}

	public Short getPhoSeqPrimeira() {
		return phoSeqPrimeira;
	}

	public void setPhoSeqPrimeira(Short phoSeqPrimeira) {
		this.phoSeqPrimeira = phoSeqPrimeira;
	}

	public Integer getIphSeqPrimeira() {
		return iphSeqPrimeira;
	}

	public void setIphSeqPrimeira(Integer iphSeqPrimeira) {
		this.iphSeqPrimeira = iphSeqPrimeira;
	}

	public Short getPhoSeqSegunda() {
		return phoSeqSegunda;
	}

	public void setPhoSeqSegunda(Short phoSeqSegunda) {
		this.phoSeqSegunda = phoSeqSegunda;
	}

	public Integer getIphSeqSegunda() {
		return iphSeqSegunda;
	}

	public void setIphSeqSegunda(Integer iphSeqSegunda) {
		this.iphSeqSegunda = iphSeqSegunda;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public enum Fields {
		SEQ("seq"),
		CODIGO("codigo"),
		DESCRICAO("descricao"),
		PHI_PRIMEIRA("phiPrimeira"),
		PHI_SEGUNDA("phiSegunda"),
		PHO_SEQ_PRIMEIRA("phoSeqPrimeira"),
		IPH_SEQ_PRIMEIRA("iphSeqPrimeira"),
		PHO_SEQ_SEGUNDA("phoSeqSegunda"),
		IPH_SEQ_SEGUNDA("iphSeqSegunda"),
		CID_SEQ("cidSeq")
		
		;
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}