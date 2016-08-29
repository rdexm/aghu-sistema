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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * Representa uma equipe configurada para ser apresentada na lista de pacientes
 * do servidor.<br>
 * Os atendimentos desta equipe serão mostrados na lista de pacientes deste
 * servidor.
 */

@Entity
@Table(name = "MPM_LISTA_SERV_EQUIPES", schema = "AGH")
public class MpmListaServEquipe extends BaseEntityId<MpmListaServEquipeId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -576793947040593589L;

	private MpmListaServEquipeId id;

	private RapServidores servidor;
	private AghEquipes equipe;

	private Date criadoEm;
	
	// TODO ADICIONAR VERSION

	// construtores

	public MpmListaServEquipe() {
	}

	public MpmListaServEquipe(MpmListaServEquipeId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "eqpSeq", column = @Column(name = "EQP_SEQ", nullable = false, precision = 4, scale = 0)) })
	public MpmListaServEquipeId getId() {
		return this.id;
	}

	public void setId(MpmListaServEquipeId id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne()
	@JoinColumns({
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", insertable = false, updatable = false),
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", insertable = false, updatable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne
	@JoinColumn(name = "EQP_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmListaServEquipe)) {
			return false;
		}
		MpmListaServEquipe castOther = (MpmListaServEquipe) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		CRIADO_EM("criadoEm"), //
		SERVIDOR("servidor"), //
		EQUIPE("equipe"), 
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"), EQP_SEQ("equipe.seq");

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
