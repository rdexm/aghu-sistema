package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the ael_resultados_codificados database table.
 * 
 */
@Entity
@Table(name="AEL_RESULTADOS_CODIFICADOS", schema = "AGH")
public class AelResultadoCodificado extends BaseEntityId<AelResultadoCodificadoId> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3505275481571895064L;
	
	
	private AelResultadoCodificadoId id;
	private Integer version;
	private Date criadoEm;
	private String descricao;
	private Boolean bacteriaVirusFungo;
	private Boolean positivoCci;
	private DominioSituacao situacao;
	private RapServidores servidor;
	
	private Set<AelResultadoExame> resultadosExame;
	
	private AelGrupoResultadoCodificado grupoResulCodificado;

    public AelResultadoCodificado() {
    }


	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "gtcSeq", column = @Column(name = "GTC_SEQ", nullable = false)),
		@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AelResultadoCodificadoId getId() {
		return this.id;
	}

	public void setId(AelResultadoCodificadoId id) {
		this.id = id;
	}
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	@Column(name="IND_BACTERIA_VIRUS_FUNGO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getBacteriaVirusFungo() {
		return bacteriaVirusFungo;
	}
	
	public void setBacteriaVirusFungo(Boolean bacteriaVirusFungo) {
		this.bacteriaVirusFungo = bacteriaVirusFungo;
	}

	@Column(name="IND_POSITIVO_CCI")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPositivoCci() {
		return positivoCci;
	}
	
	public void setPositivoCci(Boolean positivoCci) {
		this.positivoCci = positivoCci;
	}

	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	//bi-directional many-to-one association to AelGrupoResultadoCodificado
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="GTC_SEQ", insertable = false, updatable = false)
	public AelGrupoResultadoCodificado getGrupoResulCodificado() {
		return grupoResulCodificado;
	}
    
    public void setGrupoResulCodificado(AelGrupoResultadoCodificado grupoResulCodificado) {
		this.grupoResulCodificado = grupoResulCodificado;
	}
	
	@Transient
	public String getCodigoDescricao() {
		StringBuffer returnValue = new StringBuffer();
		if (this.getId() != null) {
			returnValue.append(this.getId().getSeqp());
		}
		if (this.getDescricao() != null) {
			if (returnValue.length() > 0) {
				returnValue.append(" - ");
			}
			returnValue.append(this.getDescricao());
		}
		return returnValue.length() > 0 ? returnValue.toString() : null;
	}

	public enum Fields {

		ID("id"), //
		SEQ("id.seqp"),//
		GTC_SEQ("id.gtcSeq"),//
		CRIADO_EM("criadoEm"),//
		DESCRICAO("descricao"),//
		BACTERIA_VIRUS_FUNGO("bacteriaVirusFungo"),//
		POSITIVO_CCI("positivoCci"),//
		SITUACAO("situacao"),//
		SERVIDOR("servidor"),//
		RESULTADOS_EXAME("resultadosExame"),
		GRUPO_RESULTADO_CODIFICADO("grupoResulCodificado");//

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
		if (!(obj instanceof AelResultadoCodificado)) {
			return false;
		}
		AelResultadoCodificado other = (AelResultadoCodificado) obj;
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


	@OneToMany(fetch=FetchType.LAZY, mappedBy="resultadoCodificado")
	public Set<AelResultadoExame> getResultadosExame() {
		return resultadosExame;
	}


	public void setResultadosExame(Set<AelResultadoExame> resultadosExame) {
		this.resultadosExame = resultadosExame;
	}

}