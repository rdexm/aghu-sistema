package br.gov.mec.aghu.model;

// Generated 22/03/2011 09:37:20 by Hibernate Tools 3.2.5.Beta

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

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "aacCgaJnSeq", sequenceName = "AGH.AAC_CGA_JN_SEQ", allocationSize = 1)
@Table(name = "AAC_CARACTERISTICA_GRADES_JN", schema = "AGH")

@Immutable
public class AacCaracteristicaGradeJn extends BaseJournal implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4200885963730569695L;
	private Integer grdSeq;
	private String caracteristica;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public AacCaracteristicaGradeJn() {
	}

	public AacCaracteristicaGradeJn(Integer grdSeq,
			String caracteristica) {
		this.grdSeq = grdSeq;
		this.caracteristica = caracteristica;
	}

	public AacCaracteristicaGradeJn(Integer grdSeq,
			String caracteristica, Date criadoEm, Integer serMatricula,
			Short serVinCodigo) {
		this.grdSeq = grdSeq;
		this.caracteristica = caracteristica;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aacCgaJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "GRD_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getGrdSeq() {
		return this.grdSeq;
	}

	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}

	@Column(name = "CARACTERISTICA", nullable = false, length = 30)
	public String getCaracteristica() {
		return this.caracteristica;
	}

	public void setCaracteristica(String caracteristica) {
		this.caracteristica = caracteristica;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

	public enum Fields {

		GRD_SEQ("grdSeq"),
		CARACTERISTICA("caracteristica"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

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
