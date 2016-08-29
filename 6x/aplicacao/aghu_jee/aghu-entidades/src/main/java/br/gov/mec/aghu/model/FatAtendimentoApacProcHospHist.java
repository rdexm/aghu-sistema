package br.gov.mec.aghu.model;

// Generated 15/03/2011 13:46:58 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * #42229 Incluido para migrar procedure P4 - RN_ATMP_VER_APAC_AUT
 * FatAtendimentoApacProcHospHist 
 * @author rodrigo.saraujo
 */
@Entity
@Table(name = "FAT_ATENDIMENTO_APAC_PROC_HOSP", schema = "HIST")
public class FatAtendimentoApacProcHospHist extends BaseEntityId<FatAtendimentoApacProcHospId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2950305430407652407L;
	/**
	 * 
	 */
	private FatAtendimentoApacProcHospId id;
	private FatProcedHospInternos procedimentoHospitalarInterno;
	private DominioPrioridadeCid indPrioridade;
	private AacAtendimentoApacs atendimentoApac;

	public FatAtendimentoApacProcHospHist() {
	}

	public FatAtendimentoApacProcHospHist(FatAtendimentoApacProcHospId id, FatProcedHospInternos procedimentoHospitalarInterno) {
		this.id = id;
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
	}

	public FatAtendimentoApacProcHospHist(FatAtendimentoApacProcHospId id,
			DominioPrioridadeCid indPrioridade) {
		this.id = id;
		this.indPrioridade = indPrioridade;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "atmNumero", column = @Column(name = "ATM_NUMERO", nullable = false, precision = 13, scale = 0)),
			@AttributeOverride(name = "phiSeq", column = @Column(name = "PHI_SEQ", nullable = false, precision = 6, scale = 0)) })
	public FatAtendimentoApacProcHospId getId() {
		return this.id;
	}

	public void setId(FatAtendimentoApacProcHospId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ", nullable = false, insertable = false, updatable = false)
	public FatProcedHospInternos getProcedimentoHospitalarInterno() {
		return procedimentoHospitalarInterno;
	}

	public void setProcedimentoHospitalarInterno(
			FatProcedHospInternos procedimentoHospitalarInterno) {
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
	}

	@Column(name = "IND_PRIORIDADE", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioPrioridadeCid getIndPrioridade() {
		return this.indPrioridade;
	}

	public void setIndPrioridade(DominioPrioridadeCid indPrioridade) {
		this.indPrioridade = indPrioridade;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATM_NUMERO", nullable = false, insertable = false, updatable = false)
	public AacAtendimentoApacs getAtendimentoApac() {
		return this.atendimentoApac;
	}

	public void setAtendimentoApac(AacAtendimentoApacs atendimentoApac) {
		this.atendimentoApac = atendimentoApac;
	}
	
	public enum Fields {
		ATENDIMENTO_APAC("atendimentoApac"),PHI_SEQ("id.phiSeq"),
		ATM_NUMERO("id.atmNumero"),
		IND_PRIORIDADE("indPrioridade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof FatAtendimentoApacProcHospHist)) {
			return false;
		}
		FatAtendimentoApacProcHospHist other = (FatAtendimentoApacProcHospHist) obj;
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
