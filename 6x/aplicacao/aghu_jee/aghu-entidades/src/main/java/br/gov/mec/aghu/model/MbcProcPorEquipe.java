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


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_PROC_POR_EQUIPES", schema = "AGH")
public class MbcProcPorEquipe extends BaseEntityId<MbcProcPorEquipeId> implements java.io.Serializable {

	private static final long serialVersionUID = 8877044352482343279L;
	private MbcProcPorEquipeId id;
	private Integer version;
	private RapServidores rapServidoresByMbcPxqSerFk2;
	private AghUnidadesFuncionais aghUnidadesFuncionais;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos;
	private RapServidores rapServidoresByMbcPxqSerFk1;
	private Date criadoEm;

	public MbcProcPorEquipe() {
	}

	public MbcProcPorEquipe(MbcProcPorEquipeId id, RapServidores rapServidoresByMbcPxqSerFk2,
			AghUnidadesFuncionais aghUnidadesFuncionais, MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos,
			RapServidores rapServidoresByMbcPxqSerFk1, Date criadoEm) {
		this.id = id;
		this.rapServidoresByMbcPxqSerFk2 = rapServidoresByMbcPxqSerFk2;
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
		this.rapServidoresByMbcPxqSerFk1 = rapServidoresByMbcPxqSerFk1;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "serMatriculaPrf", column = @Column(name = "SER_MATRICULA_PRF", nullable = false)),
			@AttributeOverride(name = "serVinCodigoPrf", column = @Column(name = "SER_VIN_CODIGO_PRF", nullable = false)),
			@AttributeOverride(name = "unfSeq", column = @Column(name = "UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "pciSeq", column = @Column(name = "PCI_SEQ", nullable = false)) })
	public MbcProcPorEquipeId getId() {
		return this.id;
	}

	public void setId(MbcProcPorEquipeId id) {
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
	public RapServidores getRapServidoresByMbcPxqSerFk2() {
		return this.rapServidoresByMbcPxqSerFk2;
	}

	public void setRapServidoresByMbcPxqSerFk2(RapServidores rapServidoresByMbcPxqSerFk2) {
		this.rapServidoresByMbcPxqSerFk2 = rapServidoresByMbcPxqSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", nullable = false, insertable = false, updatable = false)
	public AghUnidadesFuncionais getAghUnidadesFuncionais() {
		return this.aghUnidadesFuncionais;
	}

	public void setAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		return this.mbcProcedimentoCirurgicos;
	}

	public void setMbcProcedimentoCirurgicos(MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_PRF", referencedColumnName = "MATRICULA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_PRF", referencedColumnName = "VIN_CODIGO", nullable = false, insertable = false, updatable = false) })
	public RapServidores getRapServidoresByMbcPxqSerFk1() {
		return this.rapServidoresByMbcPxqSerFk1;
	}

	public void setRapServidoresByMbcPxqSerFk1(RapServidores rapServidoresByMbcPxqSerFk1) {
		this.rapServidoresByMbcPxqSerFk1 = rapServidoresByMbcPxqSerFk1;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@javax.persistence.Transient
	public String getDescricaoProcedimento(){
		String descricaoCapitalizada = mbcProcedimentoCirurgicos.getDescricao();
		if (StringUtils.isNotBlank(descricaoCapitalizada)) {
			descricaoCapitalizada = WordUtils.capitalize(descricaoCapitalizada.toLowerCase(), new char[1]);
		}
		return descricaoCapitalizada;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_MBC_PXQ_SER_FK2("rapServidoresByMbcPxqSerFk2"),
		AGH_UNIDADES_FUNCIONAIS("aghUnidadesFuncionais"),
		AGH_UNIDADES_FUNCIONAIS_ID("aghUnidadesFuncionais.seq"),
		MBC_PROCEDIMENTO_CIRURGICOS("mbcProcedimentoCirurgicos"),
		RAP_SERVIDORES_BY_MBC_PXQ_SER_FK1("rapServidoresByMbcPxqSerFk1"),
		RAP_SERVIDORES_BY_MBC_PXQ_SER_FK1_ID("rapServidoresByMbcPxqSerFk1.id"),
		CRIADO_EM("criadoEm"), 
		SER_MATRICULA_PRF("id.serMatriculaPrf"), 
		SER_VIN_CODIGO_PRF("id.serVinCodigoPrf"), 
		UNF_SEQ("aghUnidadesFuncionais.seq"), 
		PCI_SEQ("mbcProcedimentoCirurgicos.seq");

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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MbcProcPorEquipe)) {
			return false;
		}
		MbcProcPorEquipe other = (MbcProcPorEquipe) obj;
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
