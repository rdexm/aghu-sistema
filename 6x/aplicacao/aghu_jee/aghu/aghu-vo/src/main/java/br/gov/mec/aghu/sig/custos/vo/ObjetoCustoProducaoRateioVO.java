package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class ObjetoCustoProducaoRateioVO {

	private Integer cctCodigo;
	private Integer cbjSeq;
	private BigDecimal somaPesos;
	private BigDecimal pesoOc;

	public ObjetoCustoProducaoRateioVO(){
		
	}
	
	
	public ObjetoCustoProducaoRateioVO(Object[] object) {
		if (object[0] != null) {
			this.setCctCodigo((Integer) object[0]);
		}
		if (object[1] != null) {
			this.setCbjSeq((Integer) object[1]);
		}
		if (object[2] != null) {
			this.setSomaPesos((BigDecimal) object[2]);
		}
		if (object[3] != null) {
			this.setPesoOc((BigDecimal) object[3]);
		}
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getCbjSeq() {
		return cbjSeq;
	}

	public void setCbjSeq(Integer cbjSeq) {
		this.cbjSeq = cbjSeq;
	}

	public BigDecimal getSomaPesos() {
		return somaPesos;
	}

	public void setSomaPesos(BigDecimal somaPesos) {
		this.somaPesos = somaPesos;
	}

	public BigDecimal getPesoOc() {
		return pesoOc;
	}

	public void setPesoOc(BigDecimal pesoOc) {
		this.pesoOc = pesoOc;
	}
}
