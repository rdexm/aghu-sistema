package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.Date;

public class GeracaoMovimentoEstoqueVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3273476907082795361L;

	private Boolean indGeraMovimentoEstoque;
	private String umdCodigo;
	private Short almSeq;
	private String umdSlcCodigo;
	private Date dtSoicitacaoCompra;
	public Boolean getIndGeraMovimentoEstoque() {
		return indGeraMovimentoEstoque;
	}
	public void setIndGeraMovimentoEstoque(Boolean indGeraMovimentoEstoque) {
		this.indGeraMovimentoEstoque = indGeraMovimentoEstoque;
	}
	public String getUmdCodigo() {
		return umdCodigo;
	}
	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}
	public Short getAlmSeq() {
		return almSeq;
	}
	public void setAlmSeq(Short almSeq) {
		this.almSeq = almSeq;
	}
	public String getUmdSlcCodigo() {
		return umdSlcCodigo;
	}
	public void setUmdSlcCodigo(String umdSlcCodigo) {
		this.umdSlcCodigo = umdSlcCodigo;
	}
	public Date getDtSoicitacaoCompra() {
		return dtSoicitacaoCompra;
	}
	public void setDtSoicitacaoCompra(Date dtSoicitacaoCompra) {
		this.dtSoicitacaoCompra = dtSoicitacaoCompra;
	}
	
	public enum Fields {
		IND_GERA_MOVIMENTO_ESTOQUE("indGeraMovimentoEstoque"), 
		UMD_CODIGO("umCodigo"), 
		ALM_SEQ("almSeq"),
		UMD_SLC_CODIGO("umdSlcCodigo"),
		DT_SOLICITACAO_COMPRA("dtSolicitacaoCompra");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	
	
}
