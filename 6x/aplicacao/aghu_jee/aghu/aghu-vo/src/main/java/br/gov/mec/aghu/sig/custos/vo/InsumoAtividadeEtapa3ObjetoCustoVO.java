package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class InsumoAtividadeEtapa3ObjetoCustoVO {

	private Integer cbjSeq;
	private Integer cmtSeq;
	private Integer cctCodigo;
	private Integer matCodigo;
	private Double qtdeRealizada;
	private BigDecimal vlrInsumo;

	public InsumoAtividadeEtapa3ObjetoCustoVO() {
	}

	public InsumoAtividadeEtapa3ObjetoCustoVO(Object[] obj) {

		if (obj[0] != null) {
			this.setCbjSeq(Integer.valueOf(obj[0].toString()));
		}
		if (obj[1] != null) {
			this.setCmtSeq(Integer.valueOf(obj[1].toString()));
		}
		if (obj[2] != null) {
			this.setCctCodigo(Integer.valueOf(obj[2].toString()));
		}
		if (obj[3] != null) {
			this.setMatCodigo(Integer.valueOf(obj[3].toString()));
		}
		if (obj[4] != null) {
			this.setQtdeRealizada( Double.valueOf(obj[4].toString()));
		}
		if (obj[5] != null) {
			this.setVlrInsumo(new BigDecimal(obj[5].toString()));
		}
	}

	public Integer getCbjSeq() {
		return cbjSeq;
	}

	public void setCbjSeq(Integer cbjSeq) {
		this.cbjSeq = cbjSeq;
	}

	public Integer getCmtSeq() {
		return cmtSeq;
	}

	public void setCmtSeq(Integer cmtSeq) {
		this.cmtSeq = cmtSeq;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public Double getQtdeRealizada() {
		return qtdeRealizada;
	}

	public void setQtdeRealizada(Double qtdeRealizada) {
		this.qtdeRealizada = qtdeRealizada;
	}

	public BigDecimal getVlrInsumo() {
		return vlrInsumo;
	}

	public void setVlrInsumo(BigDecimal vlrInsumo) {
		this.vlrInsumo = vlrInsumo;
	}
}
