package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class AacTipoProcedSisregId implements EntityCompositeId {

	private static final long serialVersionUID = 2145525218827390858L;
	private Long codInternoProced;
	private Long codUnificadoProced;

	public AacTipoProcedSisregId() {
	}

	public AacTipoProcedSisregId(Long codInternoProced, Long codUnificadoProced) {
		this.codInternoProced = codInternoProced;
		this.codUnificadoProced = codUnificadoProced;
	}

	@Column(name = "cod_interno_proced", nullable = false)
	public Long getCodInternoProced() {
		return codInternoProced;
	}
	
	public void setCodInternoProced(Long codInternoProced) {
		this.codInternoProced = codInternoProced;
	}
	
	@Column(name = "cod_unificado_proced", nullable = false)
	public Long getCodUnificadoProced() {
		return codUnificadoProced;
	}
	
	public void setCodUnificadoProced(Long codUnificadoProced) {
		this.codUnificadoProced = codUnificadoProced;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AacTipoProcedSisregId)) {
			return false;
		}
		AacTipoProcedSisregId other = (AacTipoProcedSisregId) obj;
		if (codInternoProced == null) {
			if (other.codInternoProced != null) {
				return false;
			}
		} else if (!codInternoProced.equals(other.codInternoProced)) {
			return false;
		}
		if (codUnificadoProced == null) {
			if (other.codUnificadoProced != null) {
				return false;
			}
		} else if (!codUnificadoProced.equals(other.codUnificadoProced)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((codInternoProced == null) ? 0 : codInternoProced.hashCode());
		result = prime
				* result
				+ ((codUnificadoProced == null) ? 0 : codUnificadoProced
						.hashCode());
		return result;
	}
}
