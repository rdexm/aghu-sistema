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

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AIP_PACIENTE_PRONTUARIO_JN", schema = "AGH")
@SequenceGenerator(name = "aipPacProntSeq", sequenceName = "AGH.AIP_PAC_PRONT_JN_SQ1", allocationSize = 1)
@Immutable
public class AipPacienteProntuarioJn extends BaseJournal implements java.io.Serializable{

	private static final long serialVersionUID = -1450862668561275984L;
	
	
	private Integer prontuario;
	private Short seqSamis;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	
	
	
	public AipPacienteProntuarioJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipPacProntSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "PAC_PRONTUARIO", precision = 8, scale = 0)
	public Integer getProntuario() {
		return prontuario;
	}
	
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	@Column(name = "SEQ_SAMIS", precision = 8, scale = 0)
	public Short getSeqSamis() {
		return seqSamis;
	}
	
	public void setSeqSamis(Short seqSamis) {
		this.seqSamis = seqSamis;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return serMatricula;
	}
	
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	public enum Fields {
		PAC_PRONTUARIO("prontuario"),
		SEQ_SAMIS("seqSamis")
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
	
	
}
