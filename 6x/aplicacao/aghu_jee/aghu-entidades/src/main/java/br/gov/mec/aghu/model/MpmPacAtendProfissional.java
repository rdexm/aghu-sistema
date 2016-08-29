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
 * Representa atendimento em acompanhamento configurado para ser apresentada na
 * lista de pacientes do servidor.
 */

@Entity
@Table(name = "MPM_PAC_ATEND_PROFISSIONAIS", schema = "AGH")
public class MpmPacAtendProfissional extends BaseEntityId<MpmPacAtendProfissionalId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 101003424609803457L;
	private MpmPacAtendProfissionalId id;
	private Date criadoEm;
	private RapServidores servidor;
	private AghAtendimentos atendimento;

	// TODO ADICIONAR VERSION

	// construtores

	public MpmPacAtendProfissional() {
	}

	public MpmPacAtendProfissional(MpmPacAtendProfissionalId id) {
		this.id = id;
	}

	public MpmPacAtendProfissional(MpmPacAtendProfissionalId id, Date criadoEm) {
		this.id = id;
		this.criadoEm = criadoEm;
	}

	// getters & setters

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)) })
	public MpmPacAtendProfissionalId getId() {
		return this.id;
	}

	public void setId(MpmPacAtendProfissionalId id) {
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

	@ManyToOne()
	@JoinColumn(name = "ATD_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmPacAtendProfissional)) {
			return false;
		}
		MpmPacAtendProfissional castOther = (MpmPacAtendProfissional) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		CRIADO_EM("criadoEm"), //
		ATENDIMENTO("atendimento"), //
		ATENDIMENTO_SEQ("atendimento.seq"), //
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
