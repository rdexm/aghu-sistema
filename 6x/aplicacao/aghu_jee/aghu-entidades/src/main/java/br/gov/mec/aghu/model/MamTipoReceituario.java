package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MamTipoReceituario generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mamTerSq1", sequenceName="AGH.MAM_TER_SQ1", allocationSize = 1)
@Table(name = "MAM_TIPO_RECEITUARIOS", schema = "AGH")
public class MamTipoReceituario extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6871886684597162147L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private String indSituacao;
	private Date criadoEm;
	
	// FIXME Implementar este relacionamento
//	private Set<MamTipoReceitProcessos> mamTipoReceitProcessoses = new HashSet<MamTipoReceitProcessos>(0);

	public MamTipoReceituario() {
	}

	public MamTipoReceituario(Short seq, RapServidores rapServidores, String descricao, String indSituacao, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

//	public MamTipoReceituario(Short seq, RapServidores rapServidores, String descricao, String indSituacao, Date criadoEm,
//			Set<MamTipoReceitProcessos> mamTipoReceitProcessoses) {
//		this.seq = seq;
//		this.rapServidores = rapServidores;
//		this.descricao = descricao;
//		this.indSituacao = indSituacao;
//		this.criadoEm = criadoEm;
//		this.mamTipoReceitProcessoses = mamTipoReceitProcessoses;
//	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamTerSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 240)
	@Length(max = 240)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTipoReceituario")
//	public Set<MamTipoReceitProcessos> getMamTipoReceitProcessoses() {
//		return this.mamTipoReceitProcessoses;
//	}
//
//	public void setMamTipoReceitProcessoses(Set<MamTipoReceitProcessos> mamTipoReceitProcessoses) {
//		this.mamTipoReceitProcessoses = mamTipoReceitProcessoses;
//	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
//		MAM_TIPO_RECEIT_PROCESSOSES("mamTipoReceitProcessoses")
		;

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MamTipoReceituario)) {
			return false;
		}
		MamTipoReceituario other = (MamTipoReceituario) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
