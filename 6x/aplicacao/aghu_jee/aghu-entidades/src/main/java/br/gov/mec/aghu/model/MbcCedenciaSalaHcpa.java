package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_CEDENCIA_SALAS_HCPA", schema = "AGH")
public class MbcCedenciaSalaHcpa extends BaseEntityId<MbcCedenciaSalaHcpaId> implements java.io.Serializable {

	private static final long serialVersionUID = 4462276067774272124L;
	private MbcCedenciaSalaHcpaId id;
	private Integer version;
	private RapServidores servidor;
	private MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg;
	private MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private RapServidores pucServidor;
	private AghUnidadesFuncionais unidade;
	private AghEspecialidades especialidade;

	public MbcCedenciaSalaHcpa() {
	}

	public MbcCedenciaSalaHcpa(MbcCedenciaSalaHcpaId id, RapServidores rapServidores,
			MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg, MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs, Date criadoEm,
			DominioSituacao indSituacao) {
		this.id = id;
		this.servidor = rapServidores;
		this.mbcCaracteristicaSalaCirg = mbcCaracteristicaSalaCirg;
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "casSeq", column = @Column(name = "CAS_SEQ", nullable = false)),
			@AttributeOverride(name = "pucSerMatricula", column = @Column(name = "PUC_SER_MATRICULA", nullable = false)),
			@AttributeOverride(name = "pucSerVinCodigo", column = @Column(name = "PUC_SER_VIN_CODIGO", nullable = false)),
			@AttributeOverride(name = "pucUnfSeq", column = @Column(name = "PUC_UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "pucIndFuncaoProf", column = @Column(name = "PUC_IND_FUNCAO_PROF", nullable = false)),
			@AttributeOverride(name = "data", column = @Column(name = "DATA", nullable = false)) })
	public MbcCedenciaSalaHcpaId getId() {
		return this.id;
	}

	public void setId(MbcCedenciaSalaHcpaId id) {
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAS_SEQ", insertable = false, updatable = false)
	public MbcCaracteristicaSalaCirg getMbcCaracteristicaSalaCirg() {
		return this.mbcCaracteristicaSalaCirg;
	}

	public void setMbcCaracteristicaSalaCirg(MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg) {
		this.mbcCaracteristicaSalaCirg = mbcCaracteristicaSalaCirg;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PUC_SER_MATRICULA", referencedColumnName = "SER_MATRICULA", insertable = false, updatable = false),
			@JoinColumn(name = "PUC_SER_VIN_CODIGO", referencedColumnName = "SER_VIN_CODIGO", insertable = false, updatable = false),
			@JoinColumn(name = "PUC_UNF_SEQ", referencedColumnName = "UNF_SEQ", insertable = false, updatable = false),
			@JoinColumn(name = "PUC_IND_FUNCAO_PROF", referencedColumnName = "IND_FUNCAO_PROF", insertable = false, updatable = false) })
	public MbcProfAtuaUnidCirgs getMbcProfAtuaUnidCirgs() {
		return this.mbcProfAtuaUnidCirgs;
	}

	public void setMbcProfAtuaUnidCirgs(MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs) {
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
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
		VERSION("version"),
		SERVIDOR("servidor"),
		MBC_CARACTERISTICA_SALA_CIRGS("mbcCaracteristicaSalaCirg"),
		MBC_PROF_ATUA_UNID_CIRGS("mbcProfAtuaUnidCirgs"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		PUC_SERVIDOR("pucServidor"),
		PUC_SER_MATRICULA("id.pucSerMatricula"),
		PUC_SER_VIN_CODIGO("id.pucSerVinCodigo"),
		PUC_UNF_SEQ("id.pucUnfSeq"),
		PUC_IND_FUNCAO_PROF("id.pucIndFuncaoProf"),
		DATA("id.data"),
		CAS_SEQ("id.casSeq"),
		ESPECIALIDADE("especialidade")
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
		if (!(obj instanceof MbcCedenciaSalaHcpa)) {
			return false;
		}
		MbcCedenciaSalaHcpa other = (MbcCedenciaSalaHcpa) obj;
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

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PUC_UNF_SEQ", insertable = false, updatable = false)
	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}
	
	public void setPucServidor(RapServidores pucServidor) {
		this.pucServidor = pucServidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "PUC_SER_MATRICULA" , referencedColumnName = "MATRICULA"	, insertable = false, updatable = false),
		@JoinColumn(name = "PUC_SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", insertable = false, updatable = false)})
	public RapServidores getPucServidor() {
		return pucServidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRE_ESP_SEQ")
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidadeEquipe) {
		this.especialidade = especialidadeEquipe;
	}

}
