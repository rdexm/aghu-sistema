package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "mamEscJnSeq", sequenceName = "AGH.MAM_ESC_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_EMG_SERV_ESP_COOPS_JN", schema = "AGH")
@Immutable
public class MamEmgServEspCoopJn extends BaseJournal {
	private static final long serialVersionUID = -4230915953109826073L;

	private Integer seq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer esdEseSeq;
	private Short esdEepEspSeq;
	private Short tcoSeq;
	private String criadoEm;

	public MamEmgServEspCoopJn() {
	}
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamEscJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "ESD_ESE_SEQ", nullable = false)
	public Integer getEsdEseSeq() {
		return esdEseSeq;
	}

	public void setEsdEseSeq(Integer esdEseSeq) {
		this.esdEseSeq = esdEseSeq;
	}

	@Column(name = "ESD_EEP_ESP_SEQ", nullable = false)
	public Short getEsdEepEspSeq() {
		return esdEepEspSeq;
	}

	public void setEsdEepEspSeq(Short esdEepEspSeq) {
		this.esdEepEspSeq = esdEepEspSeq;
	}

	@Column(name = "TCO_SEQ", nullable = false)
	public Short getTcoSeq() {
		return tcoSeq;
	}

	public void setTcoSeq(Short tcoSeq) {
		this.tcoSeq = tcoSeq;
	}

	@Column(name = "CRIADO_EM", nullable = false, length = 240)
	@NotNull
	@Length(max = 240)
	public String getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getSeqJn() == null) ? 0 : getSeqJn().hashCode());
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
		MamEmgServEspCoopJn other = (MamEmgServEspCoopJn) obj;
		if (getSeqJn() == null) {
			if (other.getSeqJn() != null) {
				return false;
			}
		} else if (!getSeqJn().equals(other.getSeqJn())) {
			return false;
		}
		return true;
	}

	
}
