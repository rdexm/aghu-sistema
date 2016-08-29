package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_UNIDADE_NOTA_SALAS", schema = "AGH")
public class MbcUnidadeNotaSala extends BaseEntityId<MbcUnidadeNotaSalaId> implements java.io.Serializable {

	private static final long serialVersionUID = -8036376966073534924L;
	private MbcUnidadeNotaSalaId id;
	private Integer version;
	private AghEspecialidades aghEspecialidades;
	private RapServidores rapServidores;
	private AghUnidadesFuncionais aghUnidadesFuncionais;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos;
	private Date criadoEm;
	private DominioSituacao situacao;
	private Set<MbcEquipamentoNotaSala> mbcEquipamentoNotaSalaes = new HashSet<MbcEquipamentoNotaSala>(0);
	private Set<MbcMaterialImpNotaSalaUn> mbcMaterialImpNotaSalaUnes = new HashSet<MbcMaterialImpNotaSalaUn>(0);

	public MbcUnidadeNotaSala() {
	}

	public MbcUnidadeNotaSala(MbcUnidadeNotaSalaId id, RapServidores rapServidores, AghUnidadesFuncionais aghUnidadesFuncionais,
			Date criadoEm, DominioSituacao situacao) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
		this.criadoEm = criadoEm;
		this.situacao = situacao;
	}

	public MbcUnidadeNotaSala(MbcUnidadeNotaSalaId id, AghEspecialidades aghEspecialidades, RapServidores rapServidores,
			AghUnidadesFuncionais aghUnidadesFuncionais, MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos, Date criadoEm,
			DominioSituacao situacao, Set<MbcEquipamentoNotaSala> mbcEquipamentoNotaSalaes,
			Set<MbcMaterialImpNotaSalaUn> mbcMaterialImpNotaSalaUnes) {
		this.id = id;
		this.aghEspecialidades = aghEspecialidades;
		this.rapServidores = rapServidores;
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
		this.criadoEm = criadoEm;
		this.situacao = situacao;
		this.mbcEquipamentoNotaSalaes = mbcEquipamentoNotaSalaes;
		this.mbcMaterialImpNotaSalaUnes = mbcMaterialImpNotaSalaUnes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "unfSeq", column = @Column(name = "UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MbcUnidadeNotaSalaId getId() {
		return this.id;
	}

	public void setId(MbcUnidadeNotaSalaId id) {
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
	@JoinColumn(name = "ESP_SEQ")
	public AghEspecialidades getAghEspecialidades() {
		return this.aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
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
	@JoinColumn(name = "UNF_SEQ", insertable = false, updatable = false)
	public AghUnidadesFuncionais getAghUnidadesFuncionais() {
		return this.aghUnidadesFuncionais;
	}

	public void setAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ")
	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		return this.mbcProcedimentoCirurgicos;
	}

	public void setMbcProcedimentoCirurgicos(MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcUnidadeNotaSala")
	public Set<MbcEquipamentoNotaSala> getMbcEquipamentoNotaSalaes() {
		return this.mbcEquipamentoNotaSalaes;
	}

	public void setMbcEquipamentoNotaSalaes(Set<MbcEquipamentoNotaSala> mbcEquipamentoNotaSalaes) {
		this.mbcEquipamentoNotaSalaes = mbcEquipamentoNotaSalaes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unidadeNotaSala")
	public Set<MbcMaterialImpNotaSalaUn> getMbcMaterialImpNotaSalaUnes() {
		return this.mbcMaterialImpNotaSalaUnes;
	}

	public void setMbcMaterialImpNotaSalaUnes(Set<MbcMaterialImpNotaSalaUn> mbcMaterialImpNotaSalaUnes) {
		this.mbcMaterialImpNotaSalaUnes = mbcMaterialImpNotaSalaUnes;
	}

	public enum Fields {

		ID("id"),
		UNFSEQ("id.unfSeq"),
		SEQP("id.seqp"),
		VERSION("version"),
		AGH_ESPECIALIDADES("aghEspecialidades"),
		AGH_ESPECIALIDADES_SEQ("aghEspecialidades.seq"),
		RAP_SERVIDORES("rapServidores"),
		AGH_UNIDADES_FUNCIONAIS("aghUnidadesFuncionais"),
		MBC_PROCEDIMENTO_CIRURGICOS("mbcProcedimentoCirurgicos"),
		MBC_PROCEDIMENTO_CIRURGICOS_SEQ("mbcProcedimentoCirurgicos.seq"),
		CRIADO_EM("criadoEm"),
		SITUACAO("situacao"),
		MBC_EQUIPAMENTO_NOTA_SALAES("mbcEquipamentoNotaSalaes"),
		MBC_MATERIAL_IMP_NOTA_SALA_UNES("mbcMaterialImpNotaSalaUnes");

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
		if (!(obj instanceof MbcUnidadeNotaSala)) {
			return false;
		}
		MbcUnidadeNotaSala other = (MbcUnidadeNotaSala) obj;
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

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	private enum MbcUnidadeNotaSalaExceptionCode implements BusinessExceptionCode {
		MBC_NOA_CK1
	}
	
	@SuppressWarnings("unused")
	@PrePersist
	@PreUpdate
	private void validarMbcUnidadeNotaSala() {
		if (aghEspecialidades != null && mbcProcedimentoCirurgicos != null) {
			throw new BaseRuntimeException(MbcUnidadeNotaSalaExceptionCode.MBC_NOA_CK1);
		}
	}

}
