package br.gov.mec.aghu.model;

import javax.persistence.Column;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

public class AinMedicosAssistentesId implements EntityCompositeId {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8786946133710246417L;
	private Integer intSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	
	public AinMedicosAssistentesId() {
	}

	public AinMedicosAssistentesId(Integer intSeq, Integer serMatricula, Short serVinCodigo) {
		this.intSeq = intSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}
	
	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "INT_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getIntSeq() {
		return this.intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((intSeq == null) ? 0 : intSeq.hashCode());
		result = prime * result
				+ ((serMatricula == null) ? 0 : serMatricula.hashCode());
		result = prime * result
				+ ((serVinCodigo == null) ? 0 : serVinCodigo.hashCode());
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
		AinMedicosAssistentesId other = (AinMedicosAssistentesId) obj;
		if (intSeq == null) {
			if (other.intSeq != null) {
				return false;
			}
		} else if (!intSeq.equals(other.intSeq)) {
			return false;
		}
		if (serMatricula == null) {
			if (other.serMatricula != null) {
				return false;
			}
		} else if (!serMatricula.equals(other.serMatricula)) {
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
