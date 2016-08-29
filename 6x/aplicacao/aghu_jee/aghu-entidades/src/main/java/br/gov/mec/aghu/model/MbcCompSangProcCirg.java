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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mbcCpcSq1", sequenceName="AGH.MBC_CPC_SQ1", allocationSize = 1)
@Table(name = "MBC_COMP_SANG_PROC_CIRGS", schema = "AGH")
public class MbcCompSangProcCirg extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = -1330106751439416465L;
	private Short seq;
	private Integer version;
	private AghEspecialidades aghEspecialidades;
	private RapServidores rapServidores;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos;
	private AbsComponenteSanguineo absComponenteSanguineo;
	private Short qtdeUnidade;
	private Short qtdeMl;
	private Date criadoEm;

	public MbcCompSangProcCirg() {
	}

	public MbcCompSangProcCirg(Short seq, MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos,
			AbsComponenteSanguineo absComponenteSanguineo) {
		this.seq = seq;
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
		this.absComponenteSanguineo = absComponenteSanguineo;
	}

	public MbcCompSangProcCirg(Short seq, AghEspecialidades aghEspecialidades, RapServidores rapServidores,
			MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos, AbsComponenteSanguineo absComponenteSanguineo, Short qtdeUnidade,
			Short qtdeMl, Date criadoEm) {
		this.seq = seq;
		this.aghEspecialidades = aghEspecialidades;
		this.rapServidores = rapServidores;
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
		this.absComponenteSanguineo = absComponenteSanguineo;
		this.qtdeUnidade = qtdeUnidade;
		this.qtdeMl = qtdeMl;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcCpcSq1")
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
	@JoinColumn(name = "ESP_SEQ")
	public AghEspecialidades getAghEspecialidades() {
		return this.aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ", nullable = false)
	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		return this.mbcProcedimentoCirurgicos;
	}

	public void setMbcProcedimentoCirurgicos(MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CSA_CODIGO", nullable = false)
	public AbsComponenteSanguineo getAbsComponenteSanguineo() {
		return this.absComponenteSanguineo;
	}

	public void setAbsComponenteSanguineo(AbsComponenteSanguineo absComponenteSanguineo) {
		this.absComponenteSanguineo = absComponenteSanguineo;
	}

	@Column(name = "QTDE_UNIDADE")
	public Short getQtdeUnidade() {
		return this.qtdeUnidade;
	}

	public void setQtdeUnidade(Short qtdeUnidade) {
		this.qtdeUnidade = qtdeUnidade;
	}

	@Column(name = "QTDE_ML")
	public Short getQtdeMl() {
		return this.qtdeMl;
	}

	public void setQtdeMl(Short qtdeMl) {
		this.qtdeMl = qtdeMl;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		AGH_ESPECIALIDADES("aghEspecialidades"),
		RAP_SERVIDORES("rapServidores"),
		MBC_PROCEDIMENTO_CIRURGICOS("mbcProcedimentoCirurgicos"),
		ABS_COMPONENTE_SANGUINEO("absComponenteSanguineo"),
		QTDE_UNIDADE("qtdeUnidade"),
		QTDE_ML("qtdeMl"),
		CRIADO_EM("criadoEm"),
		PCI_SEQ("mbcProcedimentoCirurgicos.seq"), 
		ESP_SEQ("aghEspecialidades.seq"), 
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
		if (!(obj instanceof MbcCompSangProcCirg)) {
			return false;
		}
		MbcCompSangProcCirg other = (MbcCompSangProcCirg) obj;
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

	private enum MbcCompSangProcCirgExceptionCode implements BusinessExceptionCode {
		MBC_CPC_CK10
	}
	
	@SuppressWarnings("unused")
	@PrePersist
	@PreUpdate
	private void validarMbcCompSangProcCirg() {
		if ((this.qtdeMl == null && this.qtdeUnidade == null) || (this.qtdeMl != null && this.qtdeUnidade != null)) {
			throw new BaseRuntimeException(MbcCompSangProcCirgExceptionCode.MBC_CPC_CK10);
		}
	}

}
