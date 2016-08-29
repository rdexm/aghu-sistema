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

@Entity
@SequenceGenerator(name = "mbcMnsSq1", sequenceName = "AGH.MBC_MNS_SQ1", allocationSize = 1)
@Table(name = "MBC_MATERIAL_IMP_NOTA_SALA_UNS", schema = "AGH")
public class MbcMaterialImpNotaSalaUn extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -1857075169850014744L;
	private Integer seq;
	private Integer version;
	private MbcUnidadeNotaSala unidadeNotaSala;
	private RapServidores servidor;
	private ScoUnidadeMedida unidadeMedida;
	private ScoMaterial material;
	private Date criadoEm;
	private Short ordemImpressao;
	private String nomeImpressao;

	public MbcMaterialImpNotaSalaUn() {
	}

	public MbcMaterialImpNotaSalaUn(Integer seq, MbcUnidadeNotaSala unidadeNotaSala, RapServidores servidor, Date criadoEm, Short ordemImpressao, String nomeImpressao) {
		this.seq = seq;
		this.unidadeNotaSala = unidadeNotaSala;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.ordemImpressao = ordemImpressao;
		this.nomeImpressao = nomeImpressao;
	}

	public MbcMaterialImpNotaSalaUn(Integer seq, MbcUnidadeNotaSala unidadeNotaSala, RapServidores servidor, ScoUnidadeMedida unidadeMedida, ScoMaterial material, Date criadoEm,
			Short ordemImpressao, String nomeImpressao) {
		this.seq = seq;
		this.unidadeNotaSala = unidadeNotaSala;
		this.servidor = servidor;
		this.unidadeMedida = unidadeMedida;
		this.material = material;
		this.criadoEm = criadoEm;
		this.ordemImpressao = ordemImpressao;
		this.nomeImpressao = nomeImpressao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcMnsSq1")
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
	@JoinColumns({ @JoinColumn(name = "NOA_UNF_SEQ", referencedColumnName = "UNF_SEQ", nullable = false),
		@JoinColumn(name = "NOA_SEQP", referencedColumnName = "SEQP", nullable = false) })
		public MbcUnidadeNotaSala getUnidadeNotaSala() {
		return unidadeNotaSala;
	}

	public void setUnidadeNotaSala(MbcUnidadeNotaSala unidadeNotaSala) {
		this.unidadeNotaSala = unidadeNotaSala;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
		public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO")
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO")
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ORDEM_IMP", nullable = false)
	public Short getOrdemImpressao() {
		return ordemImpressao;
	}

	public void setOrdemImpressao(Short ordemImpressao) {
		this.ordemImpressao = ordemImpressao;
	}

	@Column(name = "NOME_IMP", nullable = false, length = 20)
	@Length(max = 20)
	public String getNomeImpressao() {
		return nomeImpressao;
	}

	public void setNomeImpressao(String nomeImpressao) {
		this.nomeImpressao = nomeImpressao;
	}
	
	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		MBC_UNIDADE_NOTA_SALAS("unidadeNotaSala"),
		MBC_UNIDADE_NOTA_SALAS_SEQP("unidadeNotaSala.id.seqp"),
		MBC_UNIDADE_NOTA_SALAS_UNFSEQ("unidadeNotaSala.id.unfSeq"),
		RAP_SERVIDORES("servidor"),
		SCO_UNIDADE_MEDIDA("unidadeMedida"),
		SCO_MATERIAL("material"),
		CRIADO_EM("criadoEm"),
		ORDEM_IMP("ordemImpressao"),
		NOME_IMP("nomeImpressao");

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
		if (!(obj instanceof MbcMaterialImpNotaSalaUn)) {
			return false;
		}
		MbcMaterialImpNotaSalaUn other = (MbcMaterialImpNotaSalaUn) obj;
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
