package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioPontuacaoPrismBicarbonato;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismBilirrubina;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismCalcio;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismCoagulacao;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismFreqCardiacaCriancaMax;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismFreqCardiacaLactente;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismFreqRespiratoriaCriancaMax;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismFreqRespiratoriaLactente;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismGlicemia;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismOxigPacO2;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismOxigPao2Fio2;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismPotassio;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismPressaoArterialDiastolica;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismPressaoArterialSistolicaCriancaMax;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismPressaoArterialSistolicaLactente;
import br.gov.mec.aghu.dominio.DominioPontuacaoPrismReacaoPupila;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "MPM_FICHAS_PRISM", schema = "AGH")
public class MpmFichaPrism extends BaseEntityId<MpmFichaPrismId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6229323913208679638L;
	private MpmFichaPrismId id;
	private MpmEscalaGlasgow escalaGlasgow;
	private RapServidores servidor;
	private Date criadoEm;
	private DominioPontuacaoPrismPressaoArterialDiastolica pontPaDiastolica;
	private DominioPontuacaoPrismOxigPao2Fio2 pontOxigPao2Fio2;
	private DominioPontuacaoPrismOxigPacO2 pontOxigPacO2;
	private DominioPontuacaoPrismReacaoPupila pontReacPupila;
	private DominioPontuacaoPrismCoagulacao pontCoagulacao;
	private DominioPontuacaoPrismBilirrubina pontBilirrubina;
	private DominioPontuacaoPrismPotassio pontPotassio;
	private DominioPontuacaoPrismCalcio pontCalcio;
	private DominioPontuacaoPrismGlicemia pontGlicemia;
	private DominioPontuacaoPrismBicarbonato pontBicarbonato;
	private DominioPontuacaoPrismPressaoArterialSistolicaLactente pontPaSistolLactente;
	private DominioPontuacaoPrismPressaoArterialSistolicaCriancaMax pontPaSistolCrMax;
	private DominioPontuacaoPrismFreqCardiacaLactente pontFreqCardLactente;
	private DominioPontuacaoPrismFreqCardiacaCriancaMax pontFreqCardCrMax;
	private DominioPontuacaoPrismFreqRespiratoriaLactente pontFreqRespLactente;
	private DominioPontuacaoPrismFreqRespiratoriaCriancaMax pontFreqRespCrMax;
	private Date dthrRealizacao;

	private enum FichasPrismExceptionCode implements BusinessExceptionCode {
		MPM_FPR_CK17, MPM_FPR_CK18, MPM_FPR_CK19
	}
	
	
	public MpmFichaPrism() {
	}
	
	public MpmFichaPrism(
			MpmFichaPrismId id,
			MpmEscalaGlasgow escalaGlasgow,
			RapServidores servidor,
			Date criadoEm,
			DominioPontuacaoPrismPressaoArterialDiastolica pontPaDiastolica,
			DominioPontuacaoPrismOxigPao2Fio2 pontOxigPao2Fio2,
			DominioPontuacaoPrismOxigPacO2 pontOxigPacO2,
			DominioPontuacaoPrismReacaoPupila pontReacPupila,
			DominioPontuacaoPrismCoagulacao pontCoagulacao,
			DominioPontuacaoPrismBilirrubina pontBilirrubina,
			DominioPontuacaoPrismPotassio pontPotassio,
			DominioPontuacaoPrismCalcio pontCalcio,
			DominioPontuacaoPrismGlicemia pontGlicemia,
			DominioPontuacaoPrismBicarbonato pontBicarbonato,
			DominioPontuacaoPrismPressaoArterialSistolicaLactente pontPaSistolLactente,
			DominioPontuacaoPrismPressaoArterialSistolicaCriancaMax pontPaSistolCrMax,
			DominioPontuacaoPrismFreqCardiacaLactente pontFreqCardLactente,
			DominioPontuacaoPrismFreqCardiacaCriancaMax pontFreqCardCrMax,
			DominioPontuacaoPrismFreqRespiratoriaLactente pontFreqRespLactente,
			DominioPontuacaoPrismFreqRespiratoriaCriancaMax pontFreqRespCrMax,
			Date dthrRealizacao) {
		super();
		this.id = id;
		this.escalaGlasgow = escalaGlasgow;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.pontPaDiastolica = pontPaDiastolica;
		this.pontOxigPao2Fio2 = pontOxigPao2Fio2;
		this.pontOxigPacO2 = pontOxigPacO2;
		this.pontReacPupila = pontReacPupila;
		this.pontCoagulacao = pontCoagulacao;
		this.pontBilirrubina = pontBilirrubina;
		this.pontPotassio = pontPotassio;
		this.pontCalcio = pontCalcio;
		this.pontGlicemia = pontGlicemia;
		this.pontBicarbonato = pontBicarbonato;
		this.pontPaSistolLactente = pontPaSistolLactente;
		this.pontPaSistolCrMax = pontPaSistolCrMax;
		this.pontFreqCardLactente = pontFreqCardLactente;
		this.pontFreqCardCrMax = pontFreqCardCrMax;
		this.pontFreqRespLactente = pontFreqRespLactente;
		this.pontFreqRespCrMax = pontFreqRespCrMax;
		this.dthrRealizacao = dthrRealizacao;
	}


	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
	public MpmFichaPrismId getId() {
		return this.id;
	}

	public void setId(MpmFichaPrismId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EGW_SEQ", nullable = false)
	public MpmEscalaGlasgow getEscalaGlasgow() {
		return this.escalaGlasgow;
	}

	public void setEscalaGlasgow(MpmEscalaGlasgow escalaGlasgow) {
		this.escalaGlasgow = escalaGlasgow;
	}

	/**
	 * @return the servidor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "PONT_PA_DIASTOLICA", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismPressaoArterialDiastolica") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismPressaoArterialDiastolica getPontPaDiastolica() {
		return this.pontPaDiastolica;
	}

	public void setPontPaDiastolica(DominioPontuacaoPrismPressaoArterialDiastolica pontPaDiastolica) {
		this.pontPaDiastolica = pontPaDiastolica;
	}

	@Column(name = "PONT_OXIG_PAO2_FIO2", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismOxigPao2Fio2") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismOxigPao2Fio2 getPontOxigPao2Fio2() {
		return this.pontOxigPao2Fio2;
	}

	public void setPontOxigPao2Fio2(DominioPontuacaoPrismOxigPao2Fio2 pontOxigPao2Fio2) {
		this.pontOxigPao2Fio2 = pontOxigPao2Fio2;
	}

	@Column(name = "PONT_OXIG_PAC_O2", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismOxigPacO2") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismOxigPacO2 getPontOxigPacO2() {
		return this.pontOxigPacO2;
	}

	public void setPontOxigPacO2(DominioPontuacaoPrismOxigPacO2 pontOxigPacO2) {
		this.pontOxigPacO2 = pontOxigPacO2;
	}

	@Column(name = "PONT_REAC_PUPILA", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismReacaoPupila") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismReacaoPupila getPontReacPupila() {
		return this.pontReacPupila;
	}

	public void setPontReacPupila(DominioPontuacaoPrismReacaoPupila pontReacPupila) {
		this.pontReacPupila = pontReacPupila;
	}

	@Column(name = "PONT_COAGULACAO", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismCoagulacao") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismCoagulacao getPontCoagulacao() {
		return this.pontCoagulacao;
	}

	public void setPontCoagulacao(DominioPontuacaoPrismCoagulacao pontCoagulacao) {
		this.pontCoagulacao = pontCoagulacao;
	}

	@Column(name = "PONT_BILIRRUBINA", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismBilirrubina") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismBilirrubina getPontBilirrubina() {
		return this.pontBilirrubina;
	}

	public void setPontBilirrubina(DominioPontuacaoPrismBilirrubina pontBilirrubina) {
		this.pontBilirrubina = pontBilirrubina;
	}

	@Column(name = "PONT_POTASSIO", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismPotassio") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismPotassio getPontPotassio() {
		return this.pontPotassio;
	}

	public void setPontPotassio(DominioPontuacaoPrismPotassio pontPotassio) {
		this.pontPotassio = pontPotassio;
	}

	@Column(name = "PONT_CALCIO", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismCalcio") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismCalcio getPontCalcio() {
		return this.pontCalcio;
	}

	public void setPontCalcio(DominioPontuacaoPrismCalcio pontCalcio) {
		this.pontCalcio = pontCalcio;
	}

	@Column(name = "PONT_GLICEMIA", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismGlicemia") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismGlicemia getPontGlicemia() {
		return this.pontGlicemia;
	}

	public void setPontGlicemia(DominioPontuacaoPrismGlicemia pontGlicemia) {
		this.pontGlicemia = pontGlicemia;
	}

	@Column(name = "PONT_BICARBONATO", nullable = false, precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismBicarbonato") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismBicarbonato getPontBicarbonato() {
		return this.pontBicarbonato;
	}

	public void setPontBicarbonato(DominioPontuacaoPrismBicarbonato pontBicarbonato) {
		this.pontBicarbonato = pontBicarbonato;
	}

	@Column(name = "PONT_PA_SISTOL_LACTENTE", precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismPressaoArterialSistolicaLactente") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismPressaoArterialSistolicaLactente getPontPaSistolLactente() {
		return this.pontPaSistolLactente;
	}

	public void setPontPaSistolLactente(DominioPontuacaoPrismPressaoArterialSistolicaLactente pontPaSistolLactente) {
		this.pontPaSistolLactente = pontPaSistolLactente;
	}

	@Column(name = "PONT_PA_SISTOL_CR_MAX", precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismPressaoArterialSistolicaCriancaMax") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismPressaoArterialSistolicaCriancaMax getPontPaSistolCrMax() {
		return this.pontPaSistolCrMax;
	}

	public void setPontPaSistolCrMax(DominioPontuacaoPrismPressaoArterialSistolicaCriancaMax pontPaSistolCrMax) {
		this.pontPaSistolCrMax = pontPaSistolCrMax;
	}

	@Column(name = "PONT_FREQ_CARD_LACTENTE", precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismFreqCardiacaLactente") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismFreqCardiacaLactente getPontFreqCardLactente() {
		return this.pontFreqCardLactente;
	}

	public void setPontFreqCardLactente(DominioPontuacaoPrismFreqCardiacaLactente pontFreqCardLactente) {
		this.pontFreqCardLactente = pontFreqCardLactente;
	}

	@Column(name = "PONT_FREQ_CARD_CR_MAX", precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismFreqCardiacaCriancaMax") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismFreqCardiacaCriancaMax getPontFreqCardCrMax() {
		return this.pontFreqCardCrMax;
	}

	public void setPontFreqCardCrMax(DominioPontuacaoPrismFreqCardiacaCriancaMax pontFreqCardCrMax) {
		this.pontFreqCardCrMax = pontFreqCardCrMax;
	}

	@Column(name = "PONT_FREQ_RESP_LACTENTE", precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismFreqRespiratoriaLactente") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismFreqRespiratoriaLactente getPontFreqRespLactente() {
		return this.pontFreqRespLactente;
	}

	public void setPontFreqRespLactente(DominioPontuacaoPrismFreqRespiratoriaLactente pontFreqRespLactente) {
		this.pontFreqRespLactente = pontFreqRespLactente;
	}

	@Column(name = "PONT_FREQ_RESP_CR_MAX", precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioPontuacaoPrismFreqRespiratoriaCriancaMax") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioPontuacaoPrismFreqRespiratoriaCriancaMax getPontFreqRespCrMax() {
		return this.pontFreqRespCrMax;
	}

	public void setPontFreqRespCrMax(DominioPontuacaoPrismFreqRespiratoriaCriancaMax pontFreqRespCrMax) {
		this.pontFreqRespCrMax = pontFreqRespCrMax;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_REALIZACAO", nullable = false, length = 7)
	public Date getDthrRealizacao() {
		return this.dthrRealizacao;
	}

	public void setDthrRealizacao(Date dthrRealizacao) {
		this.dthrRealizacao = dthrRealizacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MpmFichaPrism other = (MpmFichaPrism) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {
		ID("id"),
		ESCALA_GLASGOW("escalaGlasgow"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		PONT_PA_DIASTOLICA("pontPaDiastolica"),
		PONT_OXIG_PAO_2_FIO_2("pontOxigPao2Fio2"),
		PONT_OXIG_PAC_O_2("pontOxigPacO2"),
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

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarDados(){
		//(pont_pa_sistol_lactente is null and pont_pa_sistol_cr_max is not null)
		//or (pont_pa_sistol_lactente is not null and pont_pa_sistol_cr_max is null)       
		if( !((this.pontPaSistolLactente == null && this.pontPaSistolCrMax != null)
			||(this.pontPaSistolLactente != null && this.pontPaSistolCrMax == null))){
				throw new BaseRuntimeException(FichasPrismExceptionCode.MPM_FPR_CK17);
		}
	       //(pont_freq_card_lactente is null and pont_freq_card_cr_max is not null )
		   //or (pont_freq_card_lactente is not null and pont_freq_card_cr_max is null )       
		if( !((this.pontFreqCardLactente == null && this.pontFreqCardCrMax != null)
			||(this.pontFreqCardLactente != null && this.pontFreqCardCrMax == null))){
				throw new BaseRuntimeException(FichasPrismExceptionCode.MPM_FPR_CK18);
		}

		//(pont_freq_resp_lactente is null and pont_freq_resp_cr_max is not null)
		//or (pont_freq_resp_lactente is not null and pont_freq_resp_cr_max is null)       
		if( !((this.pontFreqRespLactente == null && this.pontFreqRespCrMax != null)
				||(this.pontFreqRespLactente != null && this.pontFreqRespCrMax == null))){
			throw new BaseRuntimeException(FichasPrismExceptionCode.MPM_FPR_CK19);
		}
		
	}
}