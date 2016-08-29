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
	@Table(name = "AEL_DIAGNOSTICO_LAUDOS_JN", schema = "AGH")
	@SequenceGenerator(name="aelDluJnSq1", sequenceName="AGH.AEL_DLU_JN_SQ1", allocationSize = 1)
	@Immutable
	public class AelDiagnosticoLaudosJn extends BaseJournal {

	private static final long serialVersionUID = 943837523859993708L;
	
	private Long seq;
	private Integer aghCid;
	private Long aelCidos;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	
	public AelDiagnosticoLaudosJn() {
	}
	
	// ATUALIZADOR JOURNALS - ID
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelDluJnSq1")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "CIO_SEQ")
	public Long getAelCidos() {
		return this.aelCidos;
	}

	public void setAelCidos(Long aelCidos) {
		this.aelCidos = aelCidos;
	}
	
	@Column(name = "CID_SEQ")
	public Integer getAghCid() {
		return aghCid;
	}

	public void setAghCid(Integer aghCid) {
		this.aghCid = aghCid;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	public enum Fields {		
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SEQ_CID("aghCid"),
		SEQ_CIDO("aelCidos"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
