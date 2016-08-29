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
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MBC_ORGAO_TRANSPLANTADOS", schema = "AGH")
public class MbcOrgaoTransplantado extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = -672631294688190153L;
	private Short seq;
	private Integer version;
	private String descricao;
	private DominioSituacao situacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Set<MbcFichaOrgaoTransplante> mbcFichaOrgaoTransplanteses = new HashSet<MbcFichaOrgaoTransplante>(0);
	private Set<MbcFichaEvento> mbcFichaEventoses = new HashSet<MbcFichaEvento>(0);

	public MbcOrgaoTransplantado() {
	}

	public MbcOrgaoTransplantado(Short seq, Integer version, String descricao,
			DominioSituacao situacao, Date criadoEm, RapServidores servidor,
			Set<MbcFichaOrgaoTransplante> mbcFichaOrgaoTransplanteses,
			Set<MbcFichaEvento> mbcFichaEventoses) {
		super();
		this.seq = seq;
		this.version = version;
		this.descricao = descricao;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.mbcFichaOrgaoTransplanteses = mbcFichaOrgaoTransplanteses;
		this.mbcFichaEventoses = mbcFichaEventoses;
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

	@Column(name = "DESCRICAO", nullable = false, length = 120)
	@Length(max = 120)
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcOrgaoTransplantados")
	public Set<MbcFichaOrgaoTransplante> getMbcFichaOrgaoTransplanteses() {
		return this.mbcFichaOrgaoTransplanteses;
	}

	public void setMbcFichaOrgaoTransplanteses(
			Set<MbcFichaOrgaoTransplante> mbcFichaOrgaoTransplanteses) {
		this.mbcFichaOrgaoTransplanteses = mbcFichaOrgaoTransplanteses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcOrgaoTransplantados")
	public Set<MbcFichaEvento> getMbcFichaEventoses() {
		return this.mbcFichaEventoses;
	}

	public void setMbcFichaEventoses(Set<MbcFichaEvento> mbcFichaEventoses) {
		this.mbcFichaEventoses = mbcFichaEventoses;
	}
	
	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		MBC_FICHA_ORGAO_TRANSPLANTESES("mbcFichaOrgaoTransplanteses"),
		MBC_FICHA_EVENTOSES("mbcFichaEventoses"),
		SERVIDOR("servidor");

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
		if (!(obj instanceof MbcOrgaoTransplantado)) {
			return false;
		}
		MbcOrgaoTransplantado other = (MbcOrgaoTransplantado) obj;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

}
