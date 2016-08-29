package br.gov.mec.aghu.model;

// Generated 28/03/2012 15:17:44 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCondicaoPsicoSensorial;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MBC_COND_PSICO_SENSORIAIS", schema = "AGH")
public class MbcCondPsicoSensorial extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = -2057232393711385516L;
	private Short seq;
	private Integer version;
	private String descricao;
	private DominioSituacao situacao;
	private DominioTipoCondicaoPsicoSensorial tipo;
	private RapServidores servidor;
	private Date criadoEm;
	private Set<MbcFichaFinal> mbcFichaFinaises = new HashSet<MbcFichaFinal>(0);
	private Set<MbcFichaPaciente> mbcFichaPacienteses = new HashSet<MbcFichaPaciente>(0);

	public MbcCondPsicoSensorial() {
	}

	public MbcCondPsicoSensorial(Short seq, String descricao, DominioSituacao situacao,
			DominioTipoCondicaoPsicoSensorial tipo, RapServidores servidor, Date criadoEm) {
		this.seq = seq;
		this.descricao = descricao;
		this.situacao = situacao;
		this.tipo = tipo;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
	}

	public MbcCondPsicoSensorial(Short seq, String descricao, DominioSituacao situacao,
			DominioTipoCondicaoPsicoSensorial tipo, RapServidores servidor, Date criadoEm,
			Set<MbcFichaFinal> mbcFichaFinaises,
			Set<MbcFichaPaciente> mbcFichaPacienteses) {
		this.seq = seq;
		this.descricao = descricao;
		this.situacao = situacao;
		this.tipo = tipo;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.mbcFichaFinaises = mbcFichaFinaises;
		this.mbcFichaPacienteses = mbcFichaPacienteses;
	}

	@Id
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

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcCondPsicoSensoriais")
	public Set<MbcFichaFinal> getMbcFichaFinaises() {
		return this.mbcFichaFinaises;
	}

	public void setMbcFichaFinaises(Set<MbcFichaFinal> mbcFichaFinaises) {
		this.mbcFichaFinaises = mbcFichaFinaises;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcCondPsicoSensoriais")
	public Set<MbcFichaPaciente> getMbcFichaPacienteses() {
		return this.mbcFichaPacienteses;
	}

	public void setMbcFichaPacienteses(
			Set<MbcFichaPaciente> mbcFichaPacienteses) {
		this.mbcFichaPacienteses = mbcFichaPacienteses;
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		SITUACAO("situacao"),
		TIPO("tipo"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		MBC_FICHA_FINAISES("mbcFichaFinaises"),
		MBC_FICHA_PACIENTESES("mbcFichaPacienteses");

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
		if (!(obj instanceof MbcCondPsicoSensorial)) {
			return false;
		}
		MbcCondPsicoSensorial other = (MbcCondPsicoSensorial) obj;
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

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "TIPO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoCondicaoPsicoSensorial getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoCondicaoPsicoSensorial tipo) {
		this.tipo = tipo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

}
