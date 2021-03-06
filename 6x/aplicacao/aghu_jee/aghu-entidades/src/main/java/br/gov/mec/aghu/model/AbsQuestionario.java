package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * AbsQuestionario generated by hbm2java
 */
@Entity
@Table(name = "ABS_QUESTIONARIOS", schema = "AGH")
public class AbsQuestionario extends BaseEntityCodigo<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9011382227296512360L;
	private Short codigo;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private String tipoQuestionario;
	private String indObrigatorio;
	private Date criadoEm;
	private String indSituacao;

	public AbsQuestionario() {
	}

	public AbsQuestionario(Short codigo, String descricao, String tipoQuestionario, Date criadoEm) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.tipoQuestionario = tipoQuestionario;
		this.criadoEm = criadoEm;
	}

	public AbsQuestionario(Short codigo, RapServidores rapServidores, String descricao, String tipoQuestionario,
			String indObrigatorio, Date criadoEm, String indSituacao) {
		this.codigo = codigo;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.tipoQuestionario = tipoQuestionario;
		this.indObrigatorio = indObrigatorio;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false)
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "TIPO_QUESTIONARIO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTipoQuestionario() {
		return this.tipoQuestionario;
	}

	public void setTipoQuestionario(String tipoQuestionario) {
		this.tipoQuestionario = tipoQuestionario;
	}

	@Column(name = "IND_OBRIGATORIO", length = 1)
	@Length(max = 1)
	public String getIndObrigatorio() {
		return this.indObrigatorio;
	}

	public void setIndObrigatorio(String indObrigatorio) {
		this.indObrigatorio = indObrigatorio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		TIPO_QUESTIONARIO("tipoQuestionario"),
		IND_OBRIGATORIO("indObrigatorio"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao");

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
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof AbsQuestionario)) {
			return false;
		}
		AbsQuestionario other = (AbsQuestionario) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
