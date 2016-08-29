package br.gov.mec.aghu.indicadores.vo;

import java.math.BigDecimal;

public class LeitoIndicadoresVO {

	private short seq;
	private Integer capacidade;
	private BigDecimal bloqueio;

	public LeitoIndicadoresVO(short seq, Integer capacidade, BigDecimal bloqueio) {
		super();
		this.seq = seq;
		this.capacidade = capacidade;
		this.bloqueio = bloqueio;
	}

	public short getSeq() {
		return seq;
	}

	public void setSeq(short seq) {
		this.seq = seq;
	}

	public Integer getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Integer capacidade) {
		this.capacidade = capacidade;
	}

	public BigDecimal getBloqueio() {
		return bloqueio;
	}

	public void setBloqueio(BigDecimal bloqueio) {
		this.bloqueio = bloqueio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bloqueio == null) ? 0 : bloqueio.hashCode());
		result = prime * result
				+ ((capacidade == null) ? 0 : capacidade.hashCode());
		result = prime * result + seq;
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
		LeitoIndicadoresVO other = (LeitoIndicadoresVO) obj;
		if (bloqueio == null) {
			if (other.bloqueio != null) {
				return false;
			}
		} else if (!bloqueio.equals(other.bloqueio)) {
			return false;
		}
		if (capacidade == null) {
			if (other.capacidade != null) {
				return false;
			}
		} else if (!capacidade.equals(other.capacidade)) {
			return false;
		}
		if (seq != other.seq) {
			return false;
		}
		return true;
	}

}
