package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;

public class ItemProcedHospVO implements Serializable {
	
	private static final long serialVersionUID = -732847051529688047L;

	private Short iphPhoSeq;
	private Integer iphSeq;
	private Long codTabela;
	private String descricao;
	
	public enum Fields{
		IPH_PHO_SEQ ("iphPhoSeq"),
		IPH_SEQ ("iphSeq"),
		COD_TABELA ("codTabela"),
		DESCRICAO ("descricao")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((iphPhoSeq == null) ? 0 : iphPhoSeq.hashCode());
		result = prime * result + ((iphSeq == null) ? 0 : iphSeq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ItemProcedHospVO other = (ItemProcedHospVO) obj;
		if (iphPhoSeq == null) {
			if (other.iphPhoSeq != null){
				return false;
			}
		} else if (!iphPhoSeq.equals(other.iphPhoSeq)){
			return false;
		}
		if (iphSeq == null) {
			if (other.iphSeq != null){
				return false;
			}
		} else if (!iphSeq.equals(other.iphSeq)){
			return false;
		}
		return true;
	}	
	
	
}
