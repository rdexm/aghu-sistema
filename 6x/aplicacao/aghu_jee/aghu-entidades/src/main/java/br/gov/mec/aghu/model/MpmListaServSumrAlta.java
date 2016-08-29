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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * Representa atendimento de internação que foi dada a alta médica e o sumário
 * se encontra pendente.
 * 
 * @author cvagheti
 * 
 */

@Entity
@Table(name = "MPM_LISTA_SERV_SUMR_ALTAS", schema = "AGH")
public class MpmListaServSumrAlta extends BaseEntityId<MpmListaServSumrAltaId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7727486348548469813L;

	private MpmListaServSumrAltaId id;

	private Date criadoEm;

	private RapServidores servidor;
	private AghAtendimentos atendimento;

	private Integer version;
	
	// construtores

	public MpmListaServSumrAlta() {
	}

	public MpmListaServSumrAlta(MpmListaServSumrAltaId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "SER_MATRICULA", column = @Column(name = "SER_MATRICULA", nullable = false, length = 7)),
			@AttributeOverride(name = "SER_VIN_CODIGO", column = @Column(name = "SER_VIN_CODIGO", nullable = false, length = 3)),
			@AttributeOverride(name = "ATD_SEQ", column = @Column(name = "ATD_SEQ", nullable = false, length = 7)) })
	public MpmListaServSumrAltaId getId() {
		return this.id;
	}

	public void setId(MpmListaServSumrAltaId id) {
		this.id = id;
	}

	@Column(name = "CRIADO_EM", nullable = false)
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
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne
	@JoinColumn(name = "ATD_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmListaServSumrAlta)) {
			return false;
		}
		MpmListaServSumrAlta castOther = (MpmListaServSumrAlta) other;
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
		ATENDIMENTO("atendimento"), //
		ATENDIMENTO_SEQ("atendimento.seq");

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