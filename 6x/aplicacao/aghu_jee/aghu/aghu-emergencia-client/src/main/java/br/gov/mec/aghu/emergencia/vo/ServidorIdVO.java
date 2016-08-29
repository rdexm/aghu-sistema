package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

public class ServidorIdVO implements Serializable {
	private static final long serialVersionUID = -5034769643492936278L;

	private Short serVinCodigo;
	private Integer matricula;

	public ServidorIdVO() {

	}

	public ServidorIdVO(Short serVinCodigo, Integer matricula) {
		this.serVinCodigo = serVinCodigo;
		this.matricula = matricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result + ((serVinCodigo == null) ? 0 : serVinCodigo.hashCode());
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
		ServidorIdVO other = (ServidorIdVO) obj;
		if (matricula == null) {
			if (other.matricula != null) {
				return false;
			}
		} else if (!matricula.equals(other.matricula)) {
			return false;
		}
		if (serVinCodigo == null) {
			if (other.serVinCodigo != null) {
				return false;
			}
		} else if (!serVinCodigo.equals(other.serVinCodigo)) {
			return false;
		}
		return true;
	}

}
