package br.gov.mec.aghu.model;

// Generated 02/08/2011 11:48:45 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "AEL_MOVIMENTO_GUICHES", schema = "AGH")
public class AelMovimentoGuiche extends BaseEntityId<AelMovimentoGuicheId> implements java.io.Serializable {

	private static final long serialVersionUID = 449757426552852759L;
	
	private AelMovimentoGuicheId id;
	private RapServidores servidor;
	private String machine;
	private Integer version;
	private AelCadGuiche aelCadGuiche;


	public AelMovimentoGuiche() {
	}

	public AelMovimentoGuiche(final AelMovimentoGuicheId id, final RapServidores servidor, final String machine, final Integer version) {
		super();
		this.setId(id);
		this.servidor = servidor;
		this.machine = machine;
		this.version = version;
	}


	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cguSeq", column = @Column(name = "CGU_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "dthrMovimento", column = @Column(name = "DTHR_MOVIMENTO", nullable = false, length = 7)) })
	public AelMovimentoGuicheId getId() {
		return id;
	}

	public void setId(final AelMovimentoGuicheId id) {
		this.id = id;
	}


	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(final Integer version) {
		this.version = version;
	}


	/**
	 * responsável.
	 * 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	@Column(name = "MACHINE", length = 64)
	public String getMachine() {
		return this.machine;
	}

	public void setMachine(final String machine) {
		this.machine = machine;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CGU_SEQ", referencedColumnName= "SEQ", insertable = false, updatable = false)
	public AelCadGuiche getAelCadGuiche() {
		return aelCadGuiche;
	}
	
	public void setAelCadGuiche(final AelCadGuiche aelCadGuiche) {
		this.aelCadGuiche = aelCadGuiche;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}
	public enum Fields {

		ID("id"),
		SERVIDOR("servidor"),
		MACHINE("machine"),
		AEL_CAD_GUICHE("aelCadGuiche");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof AelMovimentoGuiche)) {
			return false;
		}
		AelMovimentoGuiche other = (AelMovimentoGuiche) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
