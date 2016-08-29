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
@Table(name = "MBC_CHECK_LISTS", schema = "AGH")
public class MbcCheckList extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = -383821521638595458L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private Short ordem;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Set<MbcFichaCheckList> mbcFichaCheckListes = new HashSet<MbcFichaCheckList>(0);

	public MbcCheckList() {
	}

	public MbcCheckList(Short seq, RapServidores rapServidores, String descricao, Short ordem, DominioSituacao situacao, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.ordem = ordem;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
	}

	public MbcCheckList(Short seq, RapServidores rapServidores, String descricao, Short ordem, DominioSituacao situacao, Date criadoEm,
			Set<MbcFichaCheckList> mbcFichaCheckListes) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.ordem = ordem;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.mbcFichaCheckListes = mbcFichaCheckListes;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORDEM", nullable = false)
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcCheckList")
	public Set<MbcFichaCheckList> getMbcFichaCheckListes() {
		return this.mbcFichaCheckListes;
	}

	public void setMbcFichaCheckListes(Set<MbcFichaCheckList> mbcFichaCheckListes) {
		this.mbcFichaCheckListes = mbcFichaCheckListes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		ORDEM("ordem"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		MBC_FICHA_CHECK_LISTES("mbcFichaCheckListes");

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
		if (!(obj instanceof MbcCheckList)) {
			return false;
		}
		MbcCheckList other = (MbcCheckList) obj;
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

}
