package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * IbmListaPac generated by hbm2java
 */
@Entity
@Table(name = "IBM_LISTA_PAC", schema = "AGH")
public class IbmListaPac extends BaseEntityId<IbmListaPacId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6248360323603587750L;
	private IbmListaPacId id;
	private Integer version;
	private Short serVinCodigo;
	private Integer serMatricula;
	private Integer pacCodigo;
	private Integer prontuario;
	private String nome;
	private String leito;
	private String nota;
	private Short notaMod;

	public IbmListaPac() {
	}

	public IbmListaPac(IbmListaPacId id) {
		this.id = id;
	}

	public IbmListaPac(IbmListaPacId id, Short serVinCodigo, Integer serMatricula, Integer pacCodigo, Integer prontuario, String nome,
			String leito, String nota, Short notaMod) {
		this.id = id;
		this.serVinCodigo = serVinCodigo;
		this.serMatricula = serMatricula;
		this.pacCodigo = pacCodigo;
		this.prontuario = prontuario;
		this.nome = nome;
		this.leito = leito;
		this.nota = nota;
		this.notaMod = notaMod;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "usuario", column = @Column(name = "USUARIO", nullable = false, length = 30)) })
	public IbmListaPacId getId() {
		return this.id;
	}

	public void setId(IbmListaPacId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "PAC_CODIGO")
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "PRONTUARIO")
	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	@Column(name = "NOME", length = 50)
	@Length(max = 50)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "LEITO", length = 5)
	@Length(max = 5)
	public String getLeito() {
		return this.leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	@Column(name = "NOTA", length = 2000)
	@Length(max = 2000)
	public String getNota() {
		return this.nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	@Column(name = "NOTA_MOD")
	public Short getNotaMod() {
		return this.notaMod;
	}

	public void setNotaMod(Short notaMod) {
		this.notaMod = notaMod;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA("serMatricula"),
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		NOME("nome"),
		LEITO("leito"),
		NOTA("nota"),
		NOTA_MOD("notaMod");

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
		if (!(obj instanceof IbmListaPac)) {
			return false;
		}
		IbmListaPac other = (IbmListaPac) obj;
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
