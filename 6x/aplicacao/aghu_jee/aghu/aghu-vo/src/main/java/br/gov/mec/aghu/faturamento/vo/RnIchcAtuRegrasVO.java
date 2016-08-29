package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioModoCobranca;

public class RnIchcAtuRegrasVO {

	private DominioModoCobranca modoCobranca;

	private BigDecimal vlrSh;

	private BigDecimal vlrSadt;

	private BigDecimal vlrProc;

	private BigDecimal vlrAnest;

	private BigDecimal vlrSp;

	private Integer codExclusaoCritica;

	private Short retorno;

	public DominioModoCobranca getModoCobranca() {
		return modoCobranca;
	}

	public void setModoCobranca(DominioModoCobranca modoCobranca) {
		this.modoCobranca = modoCobranca;
	}

	public BigDecimal getVlrSh() {
		return vlrSh;
	}

	public void setVlrSh(BigDecimal vlrSh) {
		this.vlrSh = vlrSh;
	}

	public BigDecimal getVlrSadt() {
		return vlrSadt;
	}

	public void setVlrSadt(BigDecimal vlrSadt) {
		this.vlrSadt = vlrSadt;
	}

	public BigDecimal getVlrProc() {
		return vlrProc;
	}

	public void setVlrProc(BigDecimal vlrProc) {
		this.vlrProc = vlrProc;
	}

	public BigDecimal getVlrAnest() {
		return vlrAnest;
	}

	public void setVlrAnest(BigDecimal vlrAnest) {
		this.vlrAnest = vlrAnest;
	}

	public BigDecimal getVlrSp() {
		return vlrSp;
	}

	public void setVlrSp(BigDecimal vlrSp) {
		this.vlrSp = vlrSp;
	}

	public Integer getCodExclusaoCritica() {
		return codExclusaoCritica;
	}

	public void setCodExclusaoCritica(Integer codExclusaoCritica) {
		this.codExclusaoCritica = codExclusaoCritica;
	}

	public Short getRetorno() {
		return retorno;
	}

	public void setRetorno(Short retorno) {
		this.retorno = retorno;
	}

}
