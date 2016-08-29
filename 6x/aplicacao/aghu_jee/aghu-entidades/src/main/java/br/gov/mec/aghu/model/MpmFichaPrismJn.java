package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MPM_FICHAS_PRISM_JN", schema = "AGH")
@SequenceGenerator(name = "mpmFprjJnSeq", sequenceName = "AGH.MPM_FPR_jn_seq", allocationSize = 1)
@Immutable
public class MpmFichaPrismJn extends BaseJournal implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6393931995655659951L;
	private Integer atdSeq;
	private Short seqp;
	private Integer egwSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;
	private Byte pontPaDiastolica;
	private Byte pontOxigPao2Fio2;
	private Byte pontOxigPacO2;
	private Byte pontReacPupila;
	private Byte pontCoagulacao;
	private Byte pontBilirrubina;
	private Byte pontPotassio;
	private Byte pontCalcio;
	private Byte pontGlicemia;
	private Byte pontBicarbonato;
	private Byte pontPaSistolLactente;
	private Byte pontPaSistolCrMax;
	private Byte pontFreqCardLactente;
	private Byte pontFreqCardCrMax;
	private Byte pontFreqRespLactente;
	private Byte pontFreqRespCrMax;
	private Date dthrRealizacao;
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmFprjJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "ATD_SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getAtdSeq() {
		return this.atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 3, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Column(name = "EGW_SEQ", precision = 8, scale = 0)
	public Integer getEgwSeq() {
		return this.egwSeq;
	}

	public void setEgwSeq(Integer egwSeq) {
		this.egwSeq = egwSeq;
	}

	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "PONT_PA_DIASTOLICA", precision = 2, scale = 0)
	public Byte getPontPaDiastolica() {
		return this.pontPaDiastolica;
	}

	public void setPontPaDiastolica(Byte pontPaDiastolica) {
		this.pontPaDiastolica = pontPaDiastolica;
	}

	@Column(name = "PONT_OXIG_PAO2_FIO2", precision = 2, scale = 0)
	public Byte getPontOxigPao2Fio2() {
		return this.pontOxigPao2Fio2;
	}

	public void setPontOxigPao2Fio2(Byte pontOxigPao2Fio2) {
		this.pontOxigPao2Fio2 = pontOxigPao2Fio2;
	}

	@Column(name = "PONT_OXIG_PAC_O2", precision = 2, scale = 0)
	public Byte getPontOxigPacO2() {
		return this.pontOxigPacO2;
	}

	public void setPontOxigPacO2(Byte pontOxigPacO2) {
		this.pontOxigPacO2 = pontOxigPacO2;
	}

	@Column(name = "PONT_REAC_PUPILA", precision = 2, scale = 0)
	public Byte getPontReacPupila() {
		return this.pontReacPupila;
	}

	public void setPontReacPupila(Byte pontReacPupila) {
		this.pontReacPupila = pontReacPupila;
	}

	@Column(name = "PONT_COAGULACAO", precision = 2, scale = 0)
	public Byte getPontCoagulacao() {
		return this.pontCoagulacao;
	}

	public void setPontCoagulacao(Byte pontCoagulacao) {
		this.pontCoagulacao = pontCoagulacao;
	}

	@Column(name = "PONT_BILIRRUBINA", precision = 2, scale = 0)
	public Byte getPontBilirrubina() {
		return this.pontBilirrubina;
	}

	public void setPontBilirrubina(Byte pontBilirrubina) {
		this.pontBilirrubina = pontBilirrubina;
	}

	@Column(name = "PONT_POTASSIO", precision = 2, scale = 0)
	public Byte getPontPotassio() {
		return this.pontPotassio;
	}

	public void setPontPotassio(Byte pontPotassio) {
		this.pontPotassio = pontPotassio;
	}

	@Column(name = "PONT_CALCIO", precision = 2, scale = 0)
	public Byte getPontCalcio() {
		return this.pontCalcio;
	}

	public void setPontCalcio(Byte pontCalcio) {
		this.pontCalcio = pontCalcio;
	}

	@Column(name = "PONT_GLICEMIA", precision = 2, scale = 0)
	public Byte getPontGlicemia() {
		return this.pontGlicemia;
	}

	public void setPontGlicemia(Byte pontGlicemia) {
		this.pontGlicemia = pontGlicemia;
	}

	@Column(name = "PONT_BICARBONATO", precision = 2, scale = 0)
	public Byte getPontBicarbonato() {
		return this.pontBicarbonato;
	}

	public void setPontBicarbonato(Byte pontBicarbonato) {
		this.pontBicarbonato = pontBicarbonato;
	}

	@Column(name = "PONT_PA_SISTOL_LACTENTE", precision = 2, scale = 0)
	public Byte getPontPaSistolLactente() {
		return this.pontPaSistolLactente;
	}

	public void setPontPaSistolLactente(Byte pontPaSistolLactente) {
		this.pontPaSistolLactente = pontPaSistolLactente;
	}

	@Column(name = "PONT_PA_SISTOL_CR_MAX", precision = 2, scale = 0)
	public Byte getPontPaSistolCrMax() {
		return this.pontPaSistolCrMax;
	}

	public void setPontPaSistolCrMax(Byte pontPaSistolCrMax) {
		this.pontPaSistolCrMax = pontPaSistolCrMax;
	}

	@Column(name = "PONT_FREQ_CARD_LACTENTE", precision = 2, scale = 0)
	public Byte getPontFreqCardLactente() {
		return this.pontFreqCardLactente;
	}

	public void setPontFreqCardLactente(Byte pontFreqCardLactente) {
		this.pontFreqCardLactente = pontFreqCardLactente;
	}

	@Column(name = "PONT_FREQ_CARD_CR_MAX", precision = 2, scale = 0)
	public Byte getPontFreqCardCrMax() {
		return this.pontFreqCardCrMax;
	}

	public void setPontFreqCardCrMax(Byte pontFreqCardCrMax) {
		this.pontFreqCardCrMax = pontFreqCardCrMax;
	}

	@Column(name = "PONT_FREQ_RESP_LACTENTE", precision = 2, scale = 0)
	public Byte getPontFreqRespLactente() {
		return this.pontFreqRespLactente;
	}

	public void setPontFreqRespLactente(Byte pontFreqRespLactente) {
		this.pontFreqRespLactente = pontFreqRespLactente;
	}

	@Column(name = "PONT_FREQ_RESP_CR_MAX", precision = 2, scale = 0)
	public Byte getPontFreqRespCrMax() {
		return this.pontFreqRespCrMax;
	}

	public void setPontFreqRespCrMax(Byte pontFreqRespCrMax) {
		this.pontFreqRespCrMax = pontFreqRespCrMax;
	}

	@Column(name = "DTHR_REALIZACAO", length = 7)
	public Date getDthrRealizacao() {
		return this.dthrRealizacao;
	}

	public void setDthrRealizacao(Date dthrRealizacao) {
		this.dthrRealizacao = dthrRealizacao;
	}

	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer result = super.hashCode();
		result = prime * result + getSeqJn();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MpmFichaPrismJn other = (MpmFichaPrismJn) obj;
		if (getSeqJn() != other.getSeqJn()) {
			return false;
		}
		return true;
	}

	public enum Fields {

		ATD_SEQ("atdSeq"),
		SEQP("seqp"),
		EGW_SEQ("egwSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		CRIADO_EM("criadoEm"),
		PONT_PA_DIASTOLICA("pontPaDiastolica"),
		PONT_OXIG_PAO2_FIO2("pontOxigPao2Fio2"),
		PONT_OXIG_PAC_O2("pontOxigPacO2"),
		PONT_REAC_PUPILA("pontReacPupila"),
		PONT_COAGULACAO("pontCoagulacao"),
		PONT_BILIRRUBINA("pontBilirrubina"),
		PONT_POTASSIO("pontPotassio"),
		PONT_CALCIO("pontCalcio"),
		PONT_GLICEMIA("pontGlicemia"),
		PONT_BICARBONATO("pontBicarbonato"),
		PONT_PA_SISTOL_LACTENTE("pontPaSistolLactente"),
		PONT_PA_SISTOL_CR_MAX("pontPaSistolCrMax"),
		PONT_FREQ_CARD_LACTENTE("pontFreqCardLactente"),
		PONT_FREQ_CARD_CR_MAX("pontFreqCardCrMax"),
		PONT_FREQ_RESP_LACTENTE("pontFreqRespLactente"),
		PONT_FREQ_RESP_CR_MAX("pontFreqRespCrMax"),
		DTHR_REALIZACAO("dthrRealizacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
