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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@SequenceGenerator(name="eceAmcSq1", sequenceName="AGH.ECE_AMC_SQ1", allocationSize = 1)
@Table(name = "ECE_ADM_MDTO_CB_SPS", schema = "AGH")
public class EceAdmMdtoCbSp extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3853176647743188014L;
	private Long seq;
	private Integer version;
	private EceHorarioAdministrado eceHorarioAdministrado;
	private String nrEtiquetaMdtoAdm;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVincCodigo;

	public EceAdmMdtoCbSp() {
	}

	public EceAdmMdtoCbSp(Long seq, String nrEtiquetaMdtoAdm, Date criadoEm, Integer serMatricula, Short serVincCodigo) {
		this.seq = seq;
		this.nrEtiquetaMdtoAdm = nrEtiquetaMdtoAdm;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVincCodigo = serVincCodigo;
	}

	public EceAdmMdtoCbSp(Long seq, EceHorarioAdministrado eceHorarioAdministrado, String nrEtiquetaMdtoAdm, Date criadoEm,
			Integer serMatricula, Short serVincCodigo) {
		this.seq = seq;
		this.eceHorarioAdministrado = eceHorarioAdministrado;
		this.nrEtiquetaMdtoAdm = nrEtiquetaMdtoAdm;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVincCodigo = serVincCodigo;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "eceAmcSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
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
	@JoinColumn(name = "EHA_SEQ")
	public EceHorarioAdministrado getEceHorarioAdministrado() {
		return this.eceHorarioAdministrado;
	}

	public void setEceHorarioAdministrado(EceHorarioAdministrado eceHorarioAdministrado) {
		this.eceHorarioAdministrado = eceHorarioAdministrado;
	}

	@Column(name = "NR_ETIQUETA_MDTO_ADM", nullable = false, length = 15)
	@Length(max = 15)
	public String getNrEtiquetaMdtoAdm() {
		return this.nrEtiquetaMdtoAdm;
	}

	public void setNrEtiquetaMdtoAdm(String nrEtiquetaMdtoAdm) {
		this.nrEtiquetaMdtoAdm = nrEtiquetaMdtoAdm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VINC_CODIGO", nullable = false)
	public Short getSerVincCodigo() {
		return this.serVincCodigo;
	}

	public void setSerVincCodigo(Short serVincCodigo) {
		this.serVincCodigo = serVincCodigo;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		ECE_HORARIO_ADMINISTRADOS("eceHorarioAdministrado"),
		NR_ETIQUETA_MDTO_ADM("nrEtiquetaMdtoAdm"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VINC_CODIGO("serVincCodigo");

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
		if (!(obj instanceof EceAdmMdtoCbSp)) {
			return false;
		}
		EceAdmMdtoCbSp other = (EceAdmMdtoCbSp) obj;
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

}
