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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * Representa uma unidade funcional configurada para ser apresentada na lista de
 * pacientes do servidor.<br>
 * Os atendimentos desta unidade serão mostrados na lista de pacientes deste
 * servidor.
 * 
 * @author cvagheti
 * 
 */

@Entity
@Table(name = "MPM_SERVIDOR_UNID_FUNCIONAIS", schema = "AGH")
public class MpmServidorUnidFuncional extends BaseEntityId<MpmServidorUnidFuncionalId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5885494111838345372L;

	private MpmServidorUnidFuncionalId id;

	private Date criadoEm;

	private RapServidores servidor;
	private AghUnidadesFuncionais unidadeFuncional;
	
	// TODO ADICIONAR VERSION

	// construtores

	public MpmServidorUnidFuncional() {
	}

	public MpmServidorUnidFuncional(MpmServidorUnidFuncionalId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "SER_MATRICULA", column = @Column(name = "SER_MATRICULA", nullable = false, length = 7)),
			@AttributeOverride(name = "SER_VIN_CODIGO", column = @Column(name = "SER_VIN_CODIGO", nullable = false, length = 3)),
			@AttributeOverride(name = "UNF_SEQ", column = @Column(name = "UNF_SEQ", nullable = false, length = 4))})
	public MpmServidorUnidFuncionalId getId() {
		return this.id;
	}

	public void setId(MpmServidorUnidFuncionalId id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
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
	@JoinColumn(name = "UNF_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmServidorUnidFuncional)) {
			return false;
		}
		MpmServidorUnidFuncional castOther = (MpmServidorUnidFuncional) other;
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
		UNIDADE_FUNCIONAL_SEQ("unidadeFuncional.seq"),
		UNIDADE_FUNCIONAL("unidadeFuncional");

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