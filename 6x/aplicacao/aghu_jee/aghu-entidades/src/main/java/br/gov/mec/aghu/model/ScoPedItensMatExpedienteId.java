package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_ped_itens_mat_expediente database table.
 * 
 */
@Embeddable
public class ScoPedItensMatExpedienteId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4259649006865606196L;
	private Integer pmxSeq;
	private Integer itemNumero;
	

	@Column(name = "PMX_SEQ", nullable = false)
	public Integer getPmxSeq() {
		return pmxSeq;
	}
	
	@Column(name = "ITEM_NUMERO", nullable = false)
	public Integer getItemNumero() {
		return itemNumero;
	}

	public void setItemNumero(Integer itemNumero) {
		this.itemNumero = itemNumero;
	}
	
	public void setPmxSeq(Integer pmxSeq) {
		this.pmxSeq = pmxSeq;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itemNumero == null) ? 0 : itemNumero.hashCode());
		result = prime * result + ((pmxSeq == null) ? 0 : pmxSeq.hashCode());
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
		ScoPedItensMatExpedienteId other = (ScoPedItensMatExpedienteId) obj;
		if (itemNumero == null) {
			if (other.itemNumero != null) {
				return false;
			}
		} else if (!itemNumero.equals(other.itemNumero)) {
			return false;
		}
		if (pmxSeq == null) {
			if (other.pmxSeq != null) {
				return false;
			}
		} else if (!pmxSeq.equals(other.pmxSeq)) {
			return false;
		}
		return true;
	}
	
}