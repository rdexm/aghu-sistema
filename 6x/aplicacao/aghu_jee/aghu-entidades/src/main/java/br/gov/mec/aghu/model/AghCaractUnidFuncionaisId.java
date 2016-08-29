package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class AghCaractUnidFuncionaisId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -419494901313249104L;
	private Short unfSeq;
	private ConstanteAghCaractUnidFuncionais caracteristica;

	public AghCaractUnidFuncionaisId() {
	}

	public AghCaractUnidFuncionaisId(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		this.unfSeq = unfSeq;
		this.caracteristica = caracteristica;
	}

	@Column(name = "UNF_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType" , parameters = @Parameter(name="enumClassName", value="br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais"))
	@Column(name = "CARACTERISTICA", nullable = false)
	public ConstanteAghCaractUnidFuncionais getCaracteristica() {
		return this.caracteristica;
	}

	public void setCaracteristica(ConstanteAghCaractUnidFuncionais caracteristica) {
		this.caracteristica = caracteristica;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((caracteristica == null) ? 0 : caracteristica.hashCode());
		result = prime * result + ((unfSeq == null) ? 0 : unfSeq.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AghCaractUnidFuncionaisId)) {
			return false;
		}
		AghCaractUnidFuncionaisId other = (AghCaractUnidFuncionaisId) obj;
		if (caracteristica != other.caracteristica) {
			return false;
		}
		if (unfSeq == null) {
			if (other.unfSeq != null) {
				return false;
			}
		} else if (!unfSeq.equals(other.unfSeq)) {
			return false;
		}
		return true;
	}

	

}