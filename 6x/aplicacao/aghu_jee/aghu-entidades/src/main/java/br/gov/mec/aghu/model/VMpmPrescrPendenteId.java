package br.gov.mec.aghu.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class VMpmPrescrPendenteId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8694754748802839667L;
	
	private Integer pmdAtdSeq;
	private Long pmdSeq;
	private Integer medMatCodigo;
	private Short seqp;
	private String indAntimicrobiano;
	private Integer codigo;
	private BigDecimal dose;
	private Short frequencia;
	private String vadSigla;
	private Short duracaoTratSolicitado;
	private String indUsoMdtoAntimicrob;
	private String indFaturavel;
	private Short gupSeq;
	private String descricao;
	private Integer jumSeq;
	private Integer cspCnvCodigo;
	private Integer cspSeq;
	private String descricaoEdit;
	private String doseEdit;
	private String freqEdit;
	private String indQuimioterapico;

	public VMpmPrescrPendenteId() {
	}

	public VMpmPrescrPendenteId(Integer pmdAtdSeq, Long pmdSeq,
			Integer medMatCodigo, Short seqp, String indAntimicrobiano,
			Integer codigo, BigDecimal dose, Short frequencia, String vadSigla,
			Short duracaoTratSolicitado, String indUsoMdtoAntimicrob,
			String indFaturavel, Short gupSeq, String descricao,
			Integer jumSeq, Integer cspCnvCodigo, Integer cspSeq,
			String descricaoEdit, String doseEdit, String freqEdit,
			String indQuimioterapico) {
		super();
		this.pmdAtdSeq = pmdAtdSeq;
		this.pmdSeq = pmdSeq;
		this.medMatCodigo = medMatCodigo;
		this.seqp = seqp;
		this.indAntimicrobiano = indAntimicrobiano;
		this.codigo = codigo;
		this.dose = dose;
		this.frequencia = frequencia;
		this.vadSigla = vadSigla;
		this.duracaoTratSolicitado = duracaoTratSolicitado;
		this.indUsoMdtoAntimicrob = indUsoMdtoAntimicrob;
		this.indFaturavel = indFaturavel;
		this.gupSeq = gupSeq;
		this.descricao = descricao;
		this.jumSeq = jumSeq;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.descricaoEdit = descricaoEdit;
		this.doseEdit = doseEdit;
		this.freqEdit = freqEdit;
		this.indQuimioterapico = indQuimioterapico;
	}

		
	@Column(name = "PMD_ATD_SEQ", nullable = false, precision = 9, scale = 0)
	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}

	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}

	@Column(name = "PMD_SEQ", nullable = false, precision = 14, scale = 0)
	public Long getPmdSeq() {
		return pmdSeq;
	}

	public void setPmdSeq(Long pmdSeq) {
		this.pmdSeq = pmdSeq;
	}

	@Column(name = "MED_MAT_CODIGO", nullable = false, precision = 6, scale = 0)
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	@Column(name = "SEQP", nullable = false, precision = 4, scale = 0)
	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Column(name = "IND_ANTIMICROBIANO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndAntimicrobiano() {
		return indAntimicrobiano;
	}

	public void setIndAntimicrobiano(String indAntimicrobiano) {
		this.indAntimicrobiano = indAntimicrobiano;
	}

	@Column(name = "CODIGO")
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DOSE", nullable = false, precision = 14, scale = 4)
	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	@Column(name = "FREQUENCIA", precision = 3, scale = 0)
	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@Column(name = "VAD_SIGLA", nullable = false, length = 2)
	@Length(max = 2)
	public String getVadSigla() {
		return vadSigla;
	}

	public void setVadSigla(String vadSigla) {
		this.vadSigla = vadSigla;
	}

	@Column(name = "DURACAO_TRAT_SOLICITADO", precision = 3, scale = 0)
	public Short getDuracaoTratSolicitado() {
		return duracaoTratSolicitado;
	}

	public void setDuracaoTratSolicitado(Short duracaoTratSolicitado) {
		this.duracaoTratSolicitado = duracaoTratSolicitado;
	}

	@Column(name = "IND_USO_MDTO_ANTIMICROB", length = 1)
	@Length(max = 1)
	public String getIndUsoMdtoAntimicrob() {
		return indUsoMdtoAntimicrob;
	}

	public void setIndUsoMdtoAntimicrob(String indUsoMdtoAntimicrob) {
		this.indUsoMdtoAntimicrob = indUsoMdtoAntimicrob;
	}

	@Column(name = "IND_FATURAVEL")
	public String getIndFaturavel() {
		return indFaturavel;
	}

	public void setIndFaturavel(String indFaturavel) {
		this.indFaturavel = indFaturavel;
	}

	@Column(name = "GUP_SEQ", nullable = false, precision = 3, scale = 0)
	public Short getGupSeq() {
		return gupSeq;
	}

	public void setGupSeq(Short gupSeq) {
		this.gupSeq = gupSeq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "JUM_SEQ", precision = 8, scale = 0)
	public Integer getJumSeq() {
		return jumSeq;
	}

	public void setJumSeq(Integer jumSeq) {
		this.jumSeq = jumSeq;
	}

	@Column(name = "CSP_CNV_CODIGO")
	public Integer getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Integer cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	@Column(name = "CSP_SEQ")
	public Integer getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Integer cspSeq) {
		this.cspSeq = cspSeq;
	}

	@Column(name = "DESCRICAO_EDIT", length = 117)
	@Length(max = 117)
	public String getDescricaoEdit() {
		return descricaoEdit;
	}

	public void setDescricaoEdit(String descricaoEdit) {
		this.descricaoEdit = descricaoEdit;
	}

	@Column(name = "DOSE_EDIT", length = 56)
	@Length(max = 56)
	public String getDoseEdit() {
		return doseEdit;
	}

	public void setDoseEdit(String doseEdit) {
		this.doseEdit = doseEdit;
	}

	@Column(name = "FREQ_EDIT", length = 1000)
	@Length(max = 1000)
	public String getFreqEdit() {
		return freqEdit;
	}

	public void setFreqEdit(String freqEdit) {
		this.freqEdit = freqEdit;
	}

	@Column(name = "IND_QUIMIOTERAPICO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndQuimioterapico() {
		return indQuimioterapico;
	}

	public void setIndQuimioterapico(String indQuimioterapico) {
		this.indQuimioterapico = indQuimioterapico;
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getPmdAtdSeq());
		umHashCodeBuilder.append(this.getPmdSeq());
		umHashCodeBuilder.append(this.getMedMatCodigo());
		umHashCodeBuilder.append(this.getSeqp());
		umHashCodeBuilder.append(this.getIndAntimicrobiano());
		umHashCodeBuilder.append(this.getCodigo());
		umHashCodeBuilder.append(this.getDose());
		umHashCodeBuilder.append(this.getFrequencia());
		umHashCodeBuilder.append(this.getVadSigla());
		umHashCodeBuilder.append(this.getDuracaoTratSolicitado());
		umHashCodeBuilder.append(this.getIndUsoMdtoAntimicrob());
		umHashCodeBuilder.append(this.getIndFaturavel());
		umHashCodeBuilder.append(this.getGupSeq());
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getJumSeq());
		umHashCodeBuilder.append(this.getCspCnvCodigo());
		umHashCodeBuilder.append(this.getCspSeq());
		umHashCodeBuilder.append(this.getDescricaoEdit());
		umHashCodeBuilder.append(this.getDoseEdit());
		umHashCodeBuilder.append(this.getFreqEdit());
		umHashCodeBuilder.append(this.getIndQuimioterapico());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VMpmPrescrPendenteId)) {
			return false;
		}
		VMpmPrescrPendenteId other = (VMpmPrescrPendenteId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getPmdAtdSeq(), other.getPmdAtdSeq());
		umEqualsBuilder.append(this.getPmdSeq(), other.getPmdSeq());
		umEqualsBuilder.append(this.getMedMatCodigo(), other.getMedMatCodigo());
		umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
		umEqualsBuilder.append(this.getIndAntimicrobiano(), other.getIndAntimicrobiano());
		umEqualsBuilder.append(this.getCodigo(), other.getCodigo());
		umEqualsBuilder.append(this.getDose(), other.getDose());
		umEqualsBuilder.append(this.getFrequencia(), other.getFrequencia());
		umEqualsBuilder.append(this.getVadSigla(), other.getVadSigla());
		umEqualsBuilder.append(this.getDuracaoTratSolicitado(), other.getDuracaoTratSolicitado());
		umEqualsBuilder.append(this.getIndUsoMdtoAntimicrob(), other.getIndUsoMdtoAntimicrob());
		umEqualsBuilder.append(this.getIndFaturavel(), other.getIndFaturavel());
		umEqualsBuilder.append(this.getGupSeq(), other.getGupSeq());
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getJumSeq(), other.getJumSeq());
		umEqualsBuilder.append(this.getCspCnvCodigo(), other.getCspCnvCodigo());
		umEqualsBuilder.append(this.getCspSeq(), other.getCspSeq());
		umEqualsBuilder.append(this.getDescricaoEdit(), other.getDescricaoEdit());
		umEqualsBuilder.append(this.getDoseEdit(), other.getDoseEdit());
		umEqualsBuilder.append(this.getFreqEdit(), other.getFreqEdit());
		umEqualsBuilder.append(this.getIndQuimioterapico(), other.getIndQuimioterapico());
		
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}