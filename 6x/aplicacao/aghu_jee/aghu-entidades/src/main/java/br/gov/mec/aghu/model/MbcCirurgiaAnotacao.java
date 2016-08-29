package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_CIRURGIA_ANOTACOES", schema = "AGH")
public class MbcCirurgiaAnotacao extends BaseEntityId<MbcCirurgiaAnotacaoId> implements java.io.Serializable {

	private static final long serialVersionUID = 7894350135477774290L;
	private MbcCirurgiaAnotacaoId id;
	private Integer version;
	private MbcCirurgias mbcCirurgias;
	private RapServidores rapServidores;
	private FccCentroCustos fccCentroCustos;
	private String descricao;
	private Date criadoEm;

	public MbcCirurgiaAnotacao() {
	}

	public MbcCirurgiaAnotacao(MbcCirurgiaAnotacaoId id, MbcCirurgias mbcCirurgias, RapServidores rapServidores, String descricao,
			Date criadoEm) {
		this.id = id;
		this.mbcCirurgias = mbcCirurgias;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
	}

	public MbcCirurgiaAnotacao(MbcCirurgiaAnotacaoId id, MbcCirurgias mbcCirurgias, RapServidores rapServidores,
			FccCentroCustos fccCentroCustos, String descricao, Date criadoEm) {
		this.id = id;
		this.mbcCirurgias = mbcCirurgias;
		this.rapServidores = rapServidores;
		this.fccCentroCustos = fccCentroCustos;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "crgSeq", column = @Column(name = "CRG_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 17, scale = 17)) })
	public MbcCirurgiaAnotacaoId getId() {
		return this.id;
	}

	public void setId(MbcCirurgiaAnotacaoId id) {
		this.id = id;
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
	@JoinColumn(name = "CRG_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcCirurgias getMbcCirurgias() {
		return this.mbcCirurgias;
	}

	public void setMbcCirurgias(MbcCirurgias mbcCirurgias) {
		this.mbcCirurgias = mbcCirurgias;
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
	@JoinColumn(name = "CCT_CODIGO")
	public FccCentroCustos getFccCentroCustos() {
		return this.fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 2000)
	@Length(max = 2000)
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

	public enum Fields {

		ID("id"),
		ID_CRG_SEQ("id.crgSeq"),
		ID_SEQP("id.seqp"),
		VERSION("version"),
		MBC_CIRURGIAS("mbcCirurgias"),
		MBC_CIRURGIAS_SEQ("mbcCirurgias.seq"),
		RAP_SERVIDORES("rapServidores"),
		FCC_CENTRO_CUSTOS("fccCentroCustos"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm");

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
		if (!(obj instanceof MbcCirurgiaAnotacao)) {
			return false;
		}
		MbcCirurgiaAnotacao other = (MbcCirurgiaAnotacao) obj;
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

}