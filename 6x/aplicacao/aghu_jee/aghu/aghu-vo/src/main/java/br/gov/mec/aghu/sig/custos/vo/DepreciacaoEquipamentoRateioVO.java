package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class DepreciacaoEquipamentoRateioVO {

	private Integer cctCodigo;
	private String codPatrimonio;
	private BigDecimal vlrDepreciacao;
	
	public DepreciacaoEquipamentoRateioVO() {

	}

	public DepreciacaoEquipamentoRateioVO(Object[] object) {
		if (object[0] != null) {
			this.setCctCodigo((Integer) object[0]);
		}
		if (object[1] != null) {
			this.setCodPatrimonio((String) object[1]);
		}
		if (object[2] != null) {
			this.setVlrDepreciacao((BigDecimal) object[2]);
		}
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public String getCodPatrimonio() {
		return codPatrimonio;
	}

	public void setCodPatrimonio(String codPatrimonio) {
		this.codPatrimonio = codPatrimonio;
	}

	public BigDecimal getVlrDepreciacao() {
		return vlrDepreciacao;
	}

	public void setVlrDepreciacao(BigDecimal vlrDepreciacao) {
		this.vlrDepreciacao = vlrDepreciacao;
	}

}
