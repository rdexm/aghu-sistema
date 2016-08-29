package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class SceConversaoUnidMateriaisId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3994841052694385460L;
	private Integer matCodigo;
	private Short numero;

	public SceConversaoUnidMateriaisId() {
	}

	public SceConversaoUnidMateriaisId(Integer matCodigo, Short numero) {
		this.matCodigo = matCodigo;
		this.numero =  numero;
	}

	@Column(name = "MAT_CODIGO", nullable = false)
	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	@Column(name = "NUMERO", nullable = false)
	public Short getNumero() {
		return numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}
	
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matCodigo == null) ? 0 : matCodigo.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
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
		SceConversaoUnidMateriaisId other = (SceConversaoUnidMateriaisId) obj;
		if (matCodigo == null) {
			if (other.matCodigo != null) {
				return false;
			}
		} else if (!matCodigo.equals(other.matCodigo)) {
			return false;
		}
		if (numero == null) {
			if (other.numero != null) {
				return false;
			}
		} else if (!numero.equals(other.numero)) {
			return false;
		}
		return true;
	}
}
