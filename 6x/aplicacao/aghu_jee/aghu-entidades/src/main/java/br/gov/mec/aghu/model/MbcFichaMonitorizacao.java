package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MBC_FICHA_MONITORIZACOES", schema = "AGH")
public class MbcFichaMonitorizacao extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 3428992910113825939L;
	private Integer seq;
	private Integer version;
	private MbcFichaAnestesias mbcFichaAnestesias;
	private RapServidores rapServidores;
	private MbcItemMonitorizacao mbcItemMonitorizacao;
	private Date criadoEm;
	private Set<MbcFichaMedMonitorizacao> mbcFichaMedMonitorizacaoes = new HashSet<MbcFichaMedMonitorizacao>(0);

	public MbcFichaMonitorizacao() {
	}

	public MbcFichaMonitorizacao(Integer seq, MbcFichaAnestesias mbcFichaAnestesias, RapServidores rapServidores,
			MbcItemMonitorizacao mbcItemMonitorizacao, Date criadoEm) {
		this.seq = seq;
		this.mbcFichaAnestesias = mbcFichaAnestesias;
		this.rapServidores = rapServidores;
		this.mbcItemMonitorizacao = mbcItemMonitorizacao;
		this.criadoEm = criadoEm;
	}

	public MbcFichaMonitorizacao(Integer seq, MbcFichaAnestesias mbcFichaAnestesias, RapServidores rapServidores,
			MbcItemMonitorizacao mbcItemMonitorizacao, Date criadoEm, Set<MbcFichaMedMonitorizacao> mbcFichaMedMonitorizacaoes) {
		this.seq = seq;
		this.mbcFichaAnestesias = mbcFichaAnestesias;
		this.rapServidores = rapServidores;
		this.mbcItemMonitorizacao = mbcItemMonitorizacao;
		this.criadoEm = criadoEm;
		this.mbcFichaMedMonitorizacaoes = mbcFichaMedMonitorizacaoes;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
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
	@JoinColumn(name = "FIC_SEQ", nullable = false)
	public MbcFichaAnestesias getMbcFichaAnestesias() {
		return this.mbcFichaAnestesias;
	}

	public void setMbcFichaAnestesias(MbcFichaAnestesias mbcFichaAnestesias) {
		this.mbcFichaAnestesias = mbcFichaAnestesias;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IMZ_SEQ", nullable = false)
	public MbcItemMonitorizacao getMbcItemMonitorizacao() {
		return this.mbcItemMonitorizacao;
	}

	public void setMbcItemMonitorizacao(MbcItemMonitorizacao mbcItemMonitorizacao) {
		this.mbcItemMonitorizacao = mbcItemMonitorizacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcFichaMonitorizacao")
	public Set<MbcFichaMedMonitorizacao> getMbcFichaMedMonitorizacaoes() {
		return this.mbcFichaMedMonitorizacaoes;
	}

	public void setMbcFichaMedMonitorizacaoes(Set<MbcFichaMedMonitorizacao> mbcFichaMedMonitorizacaoes) {
		this.mbcFichaMedMonitorizacaoes = mbcFichaMedMonitorizacaoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		MBC_FICHA_ANESTESIAS("mbcFichaAnestesias"),
		RAP_SERVIDORES("rapServidores"),
		MBC_ITEM_MONITORIZACOES("mbcItemMonitorizacao"),
		CRIADO_EM("criadoEm"),
		MBC_FICHA_MED_MONITORIZACAOES("mbcFichaMedMonitorizacaoes");

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
		if (!(obj instanceof MbcFichaMonitorizacao)) {
			return false;
		}
		MbcFichaMonitorizacao other = (MbcFichaMonitorizacao) obj;
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
