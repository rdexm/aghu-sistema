/**
 * 
 */
package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.math.BigDecimal;


public class VerbaGrupoSolicitacaoVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -54779967049984255L;
	public enum Fields{
		VALOR_UNITARIO("valorUnitario"),
		VBG_SEQ("vbgSeq"),
		VBG_DESCRICAO("vbgDescricao"),
		GND_CODIGO("gndCodigo"),
		GND_DESCRICAO("gndDescricao"),
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
	
	private BigDecimal valorUnitario;
	private Integer vbgSeq;
	private String vbgDescricao;
	private Integer gndCodigo;
	private String gndDescricao;
	private Long qtdAprovada;
	private Long qtdReforco;
	
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public Integer getVbgSeq() {
		return vbgSeq;
	}
	public void setVbgSeq(Integer vbgSeq) {
		this.vbgSeq = vbgSeq;
	}
	public String getVbgDescricao() {
		return vbgDescricao;
	}
	public void setVbgDescricao(String vbgDescricao) {
		this.vbgDescricao = vbgDescricao;
	}
	public Integer getGndCodigo() {
		return gndCodigo;
	}
	public void setGndCodigo(Integer gndCodigo) {
		this.gndCodigo = gndCodigo;
	}
	public String getGndDescricao() {
		return gndDescricao;
	}
	public void setGndDescricao(String gndDescricao) {
		this.gndDescricao = gndDescricao;
	}
	public Long getQtdAprovada() {
		return qtdAprovada;
	}
	public void setQtdAprovada(Long qtdAprovada) {
		this.qtdAprovada = qtdAprovada;
	}
	public Long getQtdReforco() {
		return qtdReforco;
	}
	public void setQtdReforco(Long qtdReforco) {
		this.qtdReforco = qtdReforco;
	}
	
	
}
