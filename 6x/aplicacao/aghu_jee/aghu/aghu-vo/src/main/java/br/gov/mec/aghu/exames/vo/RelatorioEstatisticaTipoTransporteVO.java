package br.gov.mec.aghu.exames.vo;

import java.math.BigDecimal;

public class RelatorioEstatisticaTipoTransporteVO implements Comparable<RelatorioEstatisticaTipoTransporteVO> {

	private String tipoTransporte;
	private Integer quantidadeTurno1 = 0;
	private Integer quantidadeTurno2 = 0;
	private Integer quantidadeTurno3 = 0;
	private Integer quantidadeTurno4 = 0;
	private Integer total = 0;
	private BigDecimal percentual = BigDecimal.ZERO;

	public String getTipoTransporte() {
		return tipoTransporte;
	}

	public void setTipoTransporte(String tipoTransporte) {
		this.tipoTransporte = tipoTransporte;
	}

	public Integer getQuantidadeTurno1() {
		return quantidadeTurno1;
	}

	public void setQuantidadeTurno1(Integer quantidadeTurno1) {
		this.quantidadeTurno1 = quantidadeTurno1;
	}

	public Integer getQuantidadeTurno2() {
		return quantidadeTurno2;
	}

	public void setQuantidadeTurno2(Integer quantidadeTurno2) {
		this.quantidadeTurno2 = quantidadeTurno2;
	}

	public Integer getQuantidadeTurno3() {
		return quantidadeTurno3;
	}

	public void setQuantidadeTurno3(Integer quantidadeTurno3) {
		this.quantidadeTurno3 = quantidadeTurno3;
	}

	public Integer getQuantidadeTurno4() {
		return quantidadeTurno4;
	}

	public void setQuantidadeTurno4(Integer quantidadeTurno4) {
		this.quantidadeTurno4 = quantidadeTurno4;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	@Override
	public int compareTo(RelatorioEstatisticaTipoTransporteVO o) {
		int result = 0;
		if (this.getTipoTransporte() != null && o.getTipoTransporte() != null) {
			result = this.getTipoTransporte().compareTo(o.getTipoTransporte());
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tipoTransporte == null) ? 0 : tipoTransporte.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RelatorioEstatisticaTipoTransporteVO other = (RelatorioEstatisticaTipoTransporteVO) obj;
		if (tipoTransporte == null) {
			if (other.tipoTransporte != null) {
				return false;
			}
		} else if (!tipoTransporte.equals(other.tipoTransporte)) {
			return false;
		}
		return true;
	}

}