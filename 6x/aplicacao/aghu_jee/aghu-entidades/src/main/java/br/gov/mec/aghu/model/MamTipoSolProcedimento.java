package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioJustificativa;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="mamTlpSq1", sequenceName="AGH.MAM_TLP_SQ1", allocationSize = 1)
@Table(name = "MAM_TIPO_SOL_PROCEDIMENTOS", schema = "AGH")
public class MamTipoSolProcedimento extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1074933201137826622L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private DominioJustificativa indDigitaJustif;
	private Set<MamSolicProcedimento> mamSolicProcedimentoes = new HashSet<MamSolicProcedimento>(0);

	public MamTipoSolProcedimento() {
	}

	public MamTipoSolProcedimento(Short seq, RapServidores rapServidores, String descricao, Date criadoEm, DominioSituacao indSituacao,
			DominioJustificativa indDigitaJustif) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indDigitaJustif = indDigitaJustif;
	}

	public MamTipoSolProcedimento(Short seq, RapServidores rapServidores, String descricao, Date criadoEm, DominioSituacao indSituacao,
			DominioJustificativa indDigitaJustif, Set<MamSolicProcedimento> mamSolicProcedimentoes) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indDigitaJustif = indDigitaJustif;
		this.mamSolicProcedimentoes = mamSolicProcedimentoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamTlpSq1")
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_DIGITA_JUSTIF", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioJustificativa getIndDigitaJustif() {
		return this.indDigitaJustif;
	}

	public void setIndDigitaJustif(DominioJustificativa indDigitaJustif) {
		this.indDigitaJustif = indDigitaJustif;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTipoSolProcedimento")
	public Set<MamSolicProcedimento> getMamSolicProcedimentoes() {
		return this.mamSolicProcedimentoes;
	}

	public void setMamSolicProcedimentoes(Set<MamSolicProcedimento> mamSolicProcedimentoes) {
		this.mamSolicProcedimentoes = mamSolicProcedimentoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		IND_DIGITA_JUSTIF("indDigitaJustif"),
		MAM_SOLIC_PROCEDIMENTOES("mamSolicProcedimentoes");

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
		if (!(obj instanceof MamTipoSolProcedimento)) {
			return false;
		}
		MamTipoSolProcedimento other = (MamTipoSolProcedimento) obj;
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
