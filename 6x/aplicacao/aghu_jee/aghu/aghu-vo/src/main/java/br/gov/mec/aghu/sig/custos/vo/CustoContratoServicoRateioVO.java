package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class CustoContratoServicoRateioVO {

	private Integer cctCodigo;
	private Integer afcoSeq;
	private Integer afNumero;
	private Integer srvCodigo;
	private Integer slsNumero;
	private BigDecimal vlrItemContrato;

	
	public CustoContratoServicoRateioVO() {
		
	}

	public CustoContratoServicoRateioVO(Object[] object) {
		if (object[0] != null) {
			this.setCctCodigo((Integer) object[0]);
		}
		if (object[1] != null) {
			this.setAfcoSeq((Integer) object[1]);
		}
		if (object[2] != null) {
			this.setAfNumero((Integer) object[2]);
		}
		if (object[3] != null) {
			this.setSrvCodigo((Integer) object[3]);
		}
		
		if (object[4] != null) {
			this.setSlsNumero((Integer) object[4]);
		}
		
		if (object[5] != null) {
			this.setVlrItemContrato((BigDecimal) object[5]);
		}
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getSrvCodigo() {
		return srvCodigo;
	}

	public void setSrvCodigo(Integer srvCodigo) {
		this.srvCodigo = srvCodigo;
	}

	public Integer getAfcoSeq() {
		return afcoSeq;
	}

	public void setAfcoSeq(Integer afcoSeq) {
		this.afcoSeq = afcoSeq;
	}

	public BigDecimal getVlrItemContrato() {
		return vlrItemContrato;
	}

	public void setVlrItemContrato(BigDecimal vlrItemContrato) {
		this.vlrItemContrato = vlrItemContrato;
	}

	public Integer getAfNumero() {
		return afNumero;
	}

	public void setAfNumero(Integer afNumero) {
		this.afNumero = afNumero;
	}

	public Integer getSlsNumero() {
		return slsNumero;
	}

	public void setSlsNumero(Integer slsNumero) {
		this.slsNumero = slsNumero;
	}

}
