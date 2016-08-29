package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.hibernate.validator.constraints.Length;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "AEL_CAMPANHAS", schema = "AGH")
public class AelCampanha extends BaseEntityCodigo<BigDecimal> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -521571546949679024L;
	private BigDecimal codigo;
	private Integer version;
	private String descricao;
	private Date dtInicio;
	private Date dtFim;
	private String situacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Set<AelDoadorRedome> aelDoadorRedomees = new HashSet<AelDoadorRedome>(0);

	public AelCampanha() {
	}

	public AelCampanha(BigDecimal codigo, String descricao, Date dtInicio, String situacao, Date criadoEm, Integer serMatricula,
			Short serVinCodigo) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.dtInicio = dtInicio;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	public AelCampanha(BigDecimal codigo, String descricao, Date dtInicio, Date dtFim, String situacao, Date criadoEm,
			Integer serMatricula, Short serVinCodigo, Set<AelDoadorRedome> aelDoadorRedomees) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.dtInicio = dtInicio;
		this.dtFim = dtFim;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.aelDoadorRedomees = aelDoadorRedomees;
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 131089, scale = 0)
	public BigDecimal getCodigo() {
		return this.codigo;
	}

	public void setCodigo(BigDecimal codigo) {
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

	@Column(name = "DESCRICAO", nullable = false, length = 45)
	@Length(max = 45)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO", nullable = false, length = 29)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getSituacao() {
		return this.situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelCampanha")
	public Set<AelDoadorRedome> getAelDoadorRedomees() {
		return this.aelDoadorRedomees;
	}

	public void setAelDoadorRedomees(Set<AelDoadorRedome> aelDoadorRedomees) {
		this.aelDoadorRedomees = aelDoadorRedomees;
	}

	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		DESCRICAO("descricao"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		AEL_DOADOR_REDOMEES("aelDoadorRedomees");

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
		if (!(obj instanceof AelCampanha)) {
			return false;
		}
		AelCampanha other = (AelCampanha) obj;
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
