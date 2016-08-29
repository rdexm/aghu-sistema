package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

/**
 * VO utilizado na function VERIFICA_ATO_OBRIGATORIO
 */
public class VerificaAtoObrigatorioVO implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8582487524160030018L;
	private String descricao;
	private Date dthrRealizado;
	private Boolean retorno;
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Date getDthrRealizado() {
		return dthrRealizado;
	}
	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}
	public Boolean getRetorno() {
		return retorno;
	}
	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
	
}