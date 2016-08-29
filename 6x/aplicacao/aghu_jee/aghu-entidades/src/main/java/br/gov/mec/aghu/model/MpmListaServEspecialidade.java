package br.gov.mec.aghu.model;

import java.io.Serializable;
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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * Representa uma especialidade configurada para ser apresentada na lista de
 * pacientes do servidor.<br>
 * Os atendimentos desta especialidade serão mostrados na lista de pacientes
 * deste servidor.
 * 
 * @author cvagheti
 * 
 */

@Entity
@Table(name = "MPM_LISTA_SERV_ESPECIALIDADES", schema = "AGH")
public class MpmListaServEspecialidade extends BaseEntityId<MpmListaServEspecialidadeId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8048322518337906221L;

	private MpmListaServEspecialidadeId id;

	private Date criadoEm;

	private RapServidores servidor;
	private AghEspecialidades especialidade;
	
	// TODO ADICIONAR VERSION

	// construtores
	public MpmListaServEspecialidade() {

	}

	public MpmListaServEspecialidade(MpmListaServEspecialidadeId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "SER_MATRICULA", column = @Column(name = "SER_MATRICULA", nullable = false, length = 7)),
			@AttributeOverride(name = "SER_VIN_CODIGO", column = @Column(name = "SER_VIN_CODIGO", nullable = false, length = 3)),
			@AttributeOverride(name = "ESP_SEQ", column = @Column(name = "ESP_SEQ", nullable = false, length = 4)) })
	public MpmListaServEspecialidadeId getId() {
		return this.id;
	}

	public void setId(MpmListaServEspecialidadeId id) {
		this.id = id;
	}

	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

	@ManyToOne
	@JoinColumn(name = "ESP_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmListaServEspecialidade)) {
			return false;
		}
		MpmListaServEspecialidade castOther = (MpmListaServEspecialidade) other;
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
		ESPECIALIDADE_SEQ("especialidade.seq"),
		ESPECIALIDADE("especialidade"), ;

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