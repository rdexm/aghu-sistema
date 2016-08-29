package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class ConsumoInsumoVO implements java.io.Serializable {

	private static final long serialVersionUID = 3844840805812023432L;

	private Integer materialCodigo;
	private Integer centroCustoCodigo;
	private Integer centroCustoRequisitaCodigo;
	private String unidadeMediaMaterial;
	private BigDecimal custoMedioMaterial;
	private Long qtdConsumo;
	private BigDecimal vlrConsumo;

	public ConsumoInsumoVO() {

	}

	public ConsumoInsumoVO(Object[] object) {

		if (object[0] != null) {
			this.setMaterialCodigo((Integer) object[0]);
		}
		if (object[1] != null) {
			this.setCentroCustoCodigo((Integer) object[1]);
		}
		if (object[2] != null) {
			this.setCentroCustoRequisitaCodigo((Integer) object[2]);
		}
		if (object[3] != null) {
			this.setUnidadeMediaMaterial((String) object[3]);
		}
		if (object[4] != null) {
			this.setCustoMedioMaterial((BigDecimal) object[4]);
		}
		if (object[5] != null) {
			this.setQtdConsumo((Long) object[5]);
		}
		if (object[6] != null) {
			this.setVlrConsumo((BigDecimal) object[6]);
		}
	}

	public Integer getMaterialCodigo() {
		return materialCodigo;
	}

	public void setMaterialCodigo(Integer materialCodigo) {
		this.materialCodigo = materialCodigo;
	}

	public Integer getCentroCustoCodigo() {
		return centroCustoCodigo;
	}

	public void setCentroCustoCodigo(Integer centroCustoCodigo) {
		this.centroCustoCodigo = centroCustoCodigo;
	}

	public Integer getCentroCustoRequisitaCodigo() {
		return centroCustoRequisitaCodigo;
	}

	public void setCentroCustoRequisitaCodigo(Integer centroCustoRequisitaCodigo) {
		this.centroCustoRequisitaCodigo = centroCustoRequisitaCodigo;
	}

	public String getUnidadeMediaMaterial() {
		return unidadeMediaMaterial;
	}

	public void setUnidadeMediaMaterial(String unidadeMediaMaterial) {
		this.unidadeMediaMaterial = unidadeMediaMaterial;
	}

	public BigDecimal getCustoMedioMaterial() {
		return custoMedioMaterial;
	}

	public void setCustoMedioMaterial(BigDecimal custoMedioMaterial) {
		this.custoMedioMaterial = custoMedioMaterial;
	}

	public Long getQtdConsumo() {
		return qtdConsumo;
	}

	public void setQtdConsumo(Long qtdConsumo) {
		this.qtdConsumo = qtdConsumo;
	}

	public BigDecimal getVlrConsumo() {
		return vlrConsumo;
	}

	public void setVlrConsumo(BigDecimal vlrConsumo) {
		this.vlrConsumo = vlrConsumo;
	}

}
