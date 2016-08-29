package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_listas_siafi_fonte_rec database table.
 * 
 */
@Embeddable
public class ScoListasSiafiFonteRecId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2400596000163657784L;
	private Long frfCodigo;
	private java.util.Date dtInicialEmpenho;

    public ScoListasSiafiFonteRecId() {
    }

	@Column(name="FRF_CODIGO")
	public Long getFrfCodigo() {
		return this.frfCodigo;
	}
	public void setFrfCodigo(Long frfCodigo) {
		this.frfCodigo = frfCodigo;
	}

    @Temporal( TemporalType.TIMESTAMP)
	@Column(name="DT_INICIAL_EMPENHO")
	public java.util.Date getDtInicialEmpenho() {
		return this.dtInicialEmpenho;
	}
	public void setDtInicialEmpenho(java.util.Date dtInicialEmpenho) {
		this.dtInicialEmpenho = dtInicialEmpenho;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScoListasSiafiFonteRecId)) {
			return false;
		}
		ScoListasSiafiFonteRecId castOther = (ScoListasSiafiFonteRecId)other;
		return 
			this.frfCodigo.equals(castOther.frfCodigo)
			&& this.dtInicialEmpenho.equals(castOther.dtInicialEmpenho);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.frfCodigo.hashCode();
		hash = hash * prime + this.dtInicialEmpenho.hashCode();
		
		return hash;
    }
}