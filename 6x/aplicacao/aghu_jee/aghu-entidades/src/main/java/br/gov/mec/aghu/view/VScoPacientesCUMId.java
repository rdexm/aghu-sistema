package br.gov.mec.aghu.view;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class VScoPacientesCUMId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5826560617052017715L;
	private Integer rmpSeq;
	private Integer numero;

	// construtores
	public VScoPacientesCUMId() {
	}

	public VScoPacientesCUMId(Integer rmpSeq, Integer numero) {
		this.rmpSeq = rmpSeq;
		this.numero = numero;
	}

	// getters & setters

	@Column(name = "RMP_SEQ", precision = 9, scale = 0)
	public Integer getRmpSeq() {
		return rmpSeq;
	}

	public void setRmpSeq(Integer rmpSeq) {
		this.rmpSeq = rmpSeq;
	}

	@Column(name = "NUMERO",precision = 3, scale = 0)
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		result = prime * result + ((rmpSeq == null) ? 0 : rmpSeq.hashCode());
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
		if (!(obj instanceof VScoPacientesCUMId)) {
			return false;
		}
		VScoPacientesCUMId other = (VScoPacientesCUMId) obj;
		if (numero == null) {
			if (other.numero != null) {
				return false;
			}
		} else if (!numero.equals(other.numero)) {
			return false;
		}
		if (rmpSeq == null) {
			if (other.rmpSeq != null) {
				return false;
			}
		} else if (!rmpSeq.equals(other.rmpSeq)) {
			return false;
		}
		return true;
	}

	
}
