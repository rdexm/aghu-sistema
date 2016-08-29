package br.gov.mec.aghu.model;

import javax.persistence.Column;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;



public class CsePerfisUsuariosId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5436662866630369922L;
	
	private String usrId;
	private String perNome;
	
	public CsePerfisUsuariosId() {
	}

	public CsePerfisUsuariosId(String usrId, String perNome) {
		this.usrId = usrId;
		this.perNome = perNome;
	}
	
	
	@Column(name = "USR_ID", nullable = false, length = 30)
	public String getUsrId() {
		return usrId;
	}
	
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
	
	
	@Column(name = "PER_NOME", nullable = false, length = 30)
	public String getPerNome() {
		return perNome;
	}
	
	public void setPerNome(String perNome) {
		this.perNome = perNome;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("usrId", this.usrId)
				.append("perNome", this.perNome).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CsePerfisUsuariosId)) {
			return false;
		}
		CsePerfisUsuariosId castOther = (CsePerfisUsuariosId) other;
		return new EqualsBuilder().append(this.usrId, castOther.getUsrId())
				.append(this.perNome, castOther.getPerNome()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.usrId).append(this.perNome)
				.toHashCode();
	}

}
