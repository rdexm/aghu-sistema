package br.gov.mec.aghu.estoque.almoxarifado.action;

import java.io.Serializable;

public class RecebimentoDetalhaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2377754536105471523L;
	
	private Integer notaRecebimentoProvisorioSeq;
	private Boolean indFornecedor;
	
	
	public Integer getNotaRecebimentoProvisorioSeq() {
		return notaRecebimentoProvisorioSeq;
	}
	
	public void setNotaRecebimentoProvisorioSeq(Integer notaRecebimentoProvisorioSeq) {
		this.notaRecebimentoProvisorioSeq = notaRecebimentoProvisorioSeq;
	}
	
	public Boolean getIndFornecedor() {
		return indFornecedor;
	}
	
	public void setIndFornecedor(Boolean indFornecedor) {
		this.indFornecedor = indFornecedor;
	}
	
	/**
	 * Verifica se os atributos são diferentes de null.
	 * 
	 * @return
	 */
	public boolean isPreenchido() {
		return (this.getNotaRecebimentoProvisorioSeq() != null && this.getIndFornecedor() != null);
	}

}
