package br.gov.mec.aghu.model;

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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptPclSq1", sequenceName="AGH.MPT_PCL_SQ1", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_CICLO", schema = "AGH")

public class MptProtocoloCiclo extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = 3934601518964037741L;
	
	private Integer seq;
	private MptPrescricaoCiclo mptPrescricaoCiclo;
	private MpaVersaoProtAssistencial mpaVersaoProtAssistencial;	
	private String descricao;
	private Short vpaPtaSeq;
	private Short vpaSeqP;
	private Integer cloSeq;
	private MpaProtocoloAssistencial protocoloAssistencial;
	public MptProtocoloCiclo() {
		
	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPclSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLO_SEQ", nullable = false)
	public MptPrescricaoCiclo getMptPrescricaoCiclo() {
		return mptPrescricaoCiclo;
	}


	public void setMptPrescricaoCiclo(MptPrescricaoCiclo mptPrescricaoCiclo) {
		this.mptPrescricaoCiclo = mptPrescricaoCiclo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "VPA_PTA_SEQ", referencedColumnName = "PTA_SEQ"),
		@JoinColumn(name = "VPA_SEQP", referencedColumnName = "SEQP")})
	public MpaVersaoProtAssistencial getMpaVersaoProtAssistencial() {
		return mpaVersaoProtAssistencial;
	}


	public void setMpaVersaoProtAssistencial(
			MpaVersaoProtAssistencial mpaVersaoProtAssistencial) {
		this.mpaVersaoProtAssistencial = mpaVersaoProtAssistencial;
	}

	@Column(name = "DESCRICAO" , length = 100)
	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		MPT_PRESCRICAO_CICLO("mptPrescricaoCiclo"),
		MPA_VERSAO_PROT_ASSISTENCIAL_ID("mpaVersaoProtAssistencial.id"),
		MPA_VERSAO_PROT_ASSISTENCIAL("mpaVersaoProtAssistencial"),
		DESCRICAO("descricao"),
		VPASEQP("vpaSeqP"),
		VPATPASEQP("vpaPtaSeq"),
			CICLO("mptPrescricaoCiclo.seq"),
		PROTOCOLO_ASSISTENCIAL("protocoloAssistencial"),
		CLO_SEQ("cloSeq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

//	// ##### GeradorEqualsHashCodeMain #####
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
		if (!(obj instanceof MptSessao)) {
			return false;
		}
		MptSessao other = (MptSessao) obj;
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


	@Column(name = "VPA_PTA_SEQ", insertable=false, updatable=false)
	public Short getVpaPtaSeq() {
		return vpaPtaSeq;
	}


	public void setVpaPtaSeq(Short vpaPtaSeq) {
		this.vpaPtaSeq = vpaPtaSeq;
	}

	@Column(name = "VPA_SEQP", insertable=false, updatable=false)
	public Short getVpaSeqP() {
		return vpaSeqP;
	}


	public void setVpaSeqP(Short vpaSeqP) {
		this.vpaSeqP = vpaSeqP;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VPA_PTA_SEQ", insertable=false, updatable=false)
	public MpaProtocoloAssistencial getProtocoloAssistencial() {
		return protocoloAssistencial;
	}

	public void setProtocoloAssistencial(
			MpaProtocoloAssistencial protocoloAssistencial) {
		this.protocoloAssistencial = protocoloAssistencial;
	}
	
	@Column(name = "CLO_SEQ", insertable=false, updatable=false)
	public Integer getCloSeq() {
		return cloSeq;
	}

	public void setCloSeq(Integer cloSeq) {
		this.cloSeq = cloSeq;
	}
	
}
