package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "AghMcoApgarJnSeq", sequenceName = "AGH.MCO_APG_JN_SEQ", allocationSize = 1)
@Table(name = "MCO_APGARS_JN", schema = "AGH")
public class McoApgarsJn extends BaseJournal {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7992361627454624151L;
	
	private Integer rnaGsoPacCodigo;
	private Short rnaGsoSeqp;
	private Short rnaSeqp;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String freqCardiaca1;
	private String esforcoResp1;
	private String tonoMuscular1;
	private String irritabilidade1;
	private String cor1;
	private Short apgar1;
	private String freqCardiaca5;
	private String esforcoResp5;
	private String tonoMuscular5;
	private String irritabilidade5;
	private String cor5;
	private Short apgar5;
	private String freqCardiaca10;
	private String esforcoResp10;
	private String tonoMuscular10;
	private String irritabilidade10;
	private String cor10;
	private Short apgar10;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer pacCodigo;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AghMcoApgarJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "RNA_GSO_PAC_CODIGO", nullable = false)
	public Integer getRnaGsoPacCodigo() {
		return this.rnaGsoPacCodigo;
	}

	public void setRnaGsoPacCodigo(Integer rnaGsoPacCodigo) {
		this.rnaGsoPacCodigo = rnaGsoPacCodigo;
	}

	@Column(name = "RNA_GSO_SEQP", nullable = false)
	public Short getRnaGsoSeqp() {
		return this.rnaGsoSeqp;
	}

	public void setRnaGsoSeqp(Short rnaGsoSeqp) {
		this.rnaGsoSeqp = rnaGsoSeqp;
	}

	@Column(name = "RNA_SEQP", nullable = false)
	public Short getRnaSeqp() {
		return this.rnaSeqp;
	}

	public void setRnaSeqp(Short rnaSeqp) {
		this.rnaSeqp = rnaSeqp;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "FREQ_CARDIACA_1", length = 1)
	@Length(max = 1)
	public String getFreqCardiaca1() {
		return this.freqCardiaca1;
	}

	public void setFreqCardiaca1(String freqCardiaca1) {
		this.freqCardiaca1 = freqCardiaca1;
	}

	@Column(name = "ESFORCO_RESP_1", length = 1)
	@Length(max = 1)
	public String getEsforcoResp1() {
		return this.esforcoResp1;
	}

	public void setEsforcoResp1(String esforcoResp1) {
		this.esforcoResp1 = esforcoResp1;
	}

	@Column(name = "TONO_MUSCULAR_1", length = 1)
	@Length(max = 1)
	public String getTonoMuscular1() {
		return this.tonoMuscular1;
	}

	public void setTonoMuscular1(String tonoMuscular1) {
		this.tonoMuscular1 = tonoMuscular1;
	}

	@Column(name = "IRRITABILIDADE_1", length = 1)
	@Length(max = 1)
	public String getIrritabilidade1() {
		return this.irritabilidade1;
	}

	public void setIrritabilidade1(String irritabilidade1) {
		this.irritabilidade1 = irritabilidade1;
	}

	@Column(name = "COR_1", length = 1)
	@Length(max = 1)
	public String getCor1() {
		return this.cor1;
	}

	public void setCor1(String cor1) {
		this.cor1 = cor1;
	}

	@Column(name = "APGAR_1")
	public Short getApgar1() {
		return this.apgar1;
	}

	public void setApgar1(Short apgar1) {
		this.apgar1 = apgar1;
	}

	@Column(name = "FREQ_CARDIACA_5", length = 1)
	@Length(max = 1)
	public String getFreqCardiaca5() {
		return this.freqCardiaca5;
	}

	public void setFreqCardiaca5(String freqCardiaca5) {
		this.freqCardiaca5 = freqCardiaca5;
	}

	@Column(name = "ESFORCO_RESP_5", length = 1)
	@Length(max = 1)
	public String getEsforcoResp5() {
		return this.esforcoResp5;
	}

	public void setEsforcoResp5(String esforcoResp5) {
		this.esforcoResp5 = esforcoResp5;
	}

	@Column(name = "TONO_MUSCULAR_5", length = 1)
	@Length(max = 1)
	public String getTonoMuscular5() {
		return this.tonoMuscular5;
	}

	public void setTonoMuscular5(String tonoMuscular5) {
		this.tonoMuscular5 = tonoMuscular5;
	}

	@Column(name = "IRRITABILIDADE_5", length = 1)
	@Length(max = 1)
	public String getIrritabilidade5() {
		return this.irritabilidade5;
	}

	public void setIrritabilidade5(String irritabilidade5) {
		this.irritabilidade5 = irritabilidade5;
	}

	@Column(name = "COR_5", length = 1)
	@Length(max = 1)
	public String getCor5() {
		return this.cor5;
	}

	public void setCor5(String cor5) {
		this.cor5 = cor5;
	}

	@Column(name = "APGAR_5")
	public Short getApgar5() {
		return this.apgar5;
	}

	public void setApgar5(Short apgar5) {
		this.apgar5 = apgar5;
	}

	@Column(name = "FREQ_CARDIACA_10", length = 1)
	@Length(max = 1)
	public String getFreqCardiaca10() {
		return this.freqCardiaca10;
	}

	public void setFreqCardiaca10(String freqCardiaca10) {
		this.freqCardiaca10 = freqCardiaca10;
	}

	@Column(name = "ESFORCO_RESP_10", length = 1)
	@Length(max = 1)
	public String getEsforcoResp10() {
		return this.esforcoResp10;
	}

	public void setEsforcoResp10(String esforcoResp10) {
		this.esforcoResp10 = esforcoResp10;
	}

	@Column(name = "TONO_MUSCULAR_10", length = 1)
	@Length(max = 1)
	public String getTonoMuscular10() {
		return this.tonoMuscular10;
	}

	public void setTonoMuscular10(String tonoMuscular10) {
		this.tonoMuscular10 = tonoMuscular10;
	}

	@Column(name = "IRRITABILIDADE_10", length = 1)
	@Length(max = 1)
	public String getIrritabilidade10() {
		return this.irritabilidade10;
	}

	public void setIrritabilidade10(String irritabilidade10) {
		this.irritabilidade10 = irritabilidade10;
	}

	@Column(name = "COR_10", length = 1)
	@Length(max = 1)
	public String getCor10() {
		return this.cor10;
	}

	public void setCor10(String cor10) {
		this.cor10 = cor10;
	}

	@Column(name = "APGAR_10")
	public Short getApgar10() {
		return this.apgar10;
	}

	public void setApgar10(Short apgar10) {
		this.apgar10 = apgar10;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", nullable = false, length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@Column(name = "PAC_CODIGO")
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
}