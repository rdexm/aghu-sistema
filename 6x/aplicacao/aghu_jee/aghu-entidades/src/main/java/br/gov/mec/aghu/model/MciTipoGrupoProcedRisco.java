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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="mciTgpSq1", sequenceName="AGH.MCI_TGP_SQ1", allocationSize = 1)
@Table(name = "MCI_TIPO_GRUPO_PROCED_RISCOS", schema = "AGH")
public class MciTipoGrupoProcedRisco extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -742849796272725081L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidoresByMciTgpSerFk1;
	private RapServidores rapServidoresByMciTgpSerFk2;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Date alteradoEm;
	private Set<MciGrupoProcedRisco> mciGrupoProcedRiscoes = new HashSet<MciGrupoProcedRisco>(0);

	public MciTipoGrupoProcedRisco() {
	}

	public MciTipoGrupoProcedRisco(Short seq, RapServidores rapServidoresByMciTgpSerFk1, String descricao, DominioSituacao indSituacao,
			Date criadoEm) {
		this.seq = seq;
		this.rapServidoresByMciTgpSerFk1 = rapServidoresByMciTgpSerFk1;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MciTipoGrupoProcedRisco(Short seq, RapServidores rapServidoresByMciTgpSerFk1, RapServidores rapServidoresByMciTgpSerFk2,
			String descricao, DominioSituacao indSituacao, Date criadoEm, Date alteradoEm, Set<MciGrupoProcedRisco> mciGrupoProcedRiscoes) {
		this.seq = seq;
		this.rapServidoresByMciTgpSerFk1 = rapServidoresByMciTgpSerFk1;
		this.rapServidoresByMciTgpSerFk2 = rapServidoresByMciTgpSerFk2;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.mciGrupoProcedRiscoes = mciGrupoProcedRiscoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciTgpSq1")
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
	public RapServidores getRapServidoresByMciTgpSerFk1() {
		return this.rapServidoresByMciTgpSerFk1;
	}

	public void setRapServidoresByMciTgpSerFk1(RapServidores rapServidoresByMciTgpSerFk1) {
		this.rapServidoresByMciTgpSerFk1 = rapServidoresByMciTgpSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByMciTgpSerFk2() {
		return this.rapServidoresByMciTgpSerFk2;
	}

	public void setRapServidoresByMciTgpSerFk2(RapServidores rapServidoresByMciTgpSerFk2) {
		this.rapServidoresByMciTgpSerFk2 = rapServidoresByMciTgpSerFk2;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciTipoGrupoProcedRisco")
	public Set<MciGrupoProcedRisco> getMciGrupoProcedRiscoes() {
		return this.mciGrupoProcedRiscoes;
	}

	public void setMciGrupoProcedRiscoes(Set<MciGrupoProcedRisco> mciGrupoProcedRiscoes) {
		this.mciGrupoProcedRiscoes = mciGrupoProcedRiscoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES_BY_MCI_TGP_SER_FK1("rapServidoresByMciTgpSerFk1"),
		RAP_SERVIDORES_BY_MCI_TGP_SER_FK2("rapServidoresByMciTgpSerFk2"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		MCI_GRUPO_PROCED_RISCOES("mciGrupoProcedRiscoes");

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
		if (!(obj instanceof MciTipoGrupoProcedRisco)) {
			return false;
		}
		MciTipoGrupoProcedRisco other = (MciTipoGrupoProcedRisco) obj;
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
