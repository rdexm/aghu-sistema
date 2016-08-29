package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class PacienteContaHospitalarVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 365915175267145354L;

	private Integer pacCodigo;

	private Integer pacProntuario;

	private Integer intSeq;

	public PacienteContaHospitalarVO() {
	}

	public PacienteContaHospitalarVO(Integer pacCodigo, Integer pacProntuario, Integer intSeq) {
		this.pacCodigo = pacCodigo;
		this.pacProntuario = pacProntuario;
		this.intSeq = intSeq;
	}

	public PacienteContaHospitalarVO(Integer pacCodigo, Integer pacProntuario) {
		this.pacCodigo = pacCodigo;
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((intSeq == null) ? 0 : intSeq.hashCode());
		result = prime * result + ((pacCodigo == null) ? 0 : pacCodigo.hashCode());
		result = prime * result + ((pacProntuario == null) ? 0 : pacProntuario.hashCode());
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
		PacienteContaHospitalarVO other = (PacienteContaHospitalarVO) obj;
		if (intSeq == null) {
			if (other.intSeq != null) {
				return false;
			}
		} else if (!intSeq.equals(other.intSeq)) {
			return false;
		}
		if (pacCodigo == null) {
			if (other.pacCodigo != null) {
				return false;
			}
		} else if (!pacCodigo.equals(other.pacCodigo)) {
			return false;
		}
		if (pacProntuario == null) {
			if (other.pacProntuario != null) {
				return false;
			}
		} else if (!pacProntuario.equals(other.pacProntuario)) {
			return false;
		}
		return true;
	}

}
