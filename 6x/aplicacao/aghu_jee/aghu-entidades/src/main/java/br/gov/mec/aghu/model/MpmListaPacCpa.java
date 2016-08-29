package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * Indicação feita na configuração da lista de pacientes do servidor para
 * mostrar pacientes com cuidados pós-anestésicos(CPA).<br>
 * Os atendimentos com indicação de CPA serão mostrados na lista de pacientes
 * deste servidor.
 */

@Entity
@Table(name = "MPM_LISTA_PAC_CPAS", schema = "AGH")
public class MpmListaPacCpa extends BaseEntityId<MpmListaPacCpaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6720150126303809482L;
	private MpmListaPacCpaId id;
	private Boolean indPacCpa;
	private RapServidores servidor;
	private Date criadoEm;
	private Date alteradoEm;

	private Integer version;

	public MpmListaPacCpa() {
	}

	public MpmListaPacCpa(MpmListaPacCpaId id, Boolean indPacCpa,
			Date criadoEm) {
		this.id = id;
		this.indPacCpa = indPacCpa;
		this.criadoEm = criadoEm;
	}

	public MpmListaPacCpa(MpmListaPacCpaId id, Boolean indPacCpa,
			Date criadoEm, Date alteradoEm) {
		this.id = id;
		this.indPacCpa = indPacCpa;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)) })
	public MpmListaPacCpaId getId() {
		return this.id;
	}

	public void setId(MpmListaPacCpaId id) {
		this.id = id;
	}

	@Column(name = "IND_PAC_CPA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPacCpa() {
		return this.indPacCpa;
	}

	public void setIndPacCpa(Boolean indPacCpa) {
		this.indPacCpa = indPacCpa;
	}

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", insertable = false, updatable = false),
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", insertable = false, updatable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	// outros

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmListaPacCpa)) {
			return false;
		}
		MpmListaPacCpa castOther = (MpmListaPacCpa) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		ID("id"), //
		IND_PAC_CPA("indPacCpa"), //
		CRIADO_EM("criadoEm"), //
		ALTERADO_EM("alteradoEm"), //
		SERVIDOR("servidor");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}
