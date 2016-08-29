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


/**
 *   Alias: AEO_JN
 */
@Entity
@Table(name = "AEL_TOPOGRAFIA_LAUDOS_JN", schema = "AGH")
@SequenceGenerator(name="aelAeoJnSq1", sequenceName="AEL_AEO_JN_SQ1", allocationSize = 1)
@Immutable
public class AelTopografiaLaudosJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 8925443388389072587L;
	
	private Long seq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Long exameAp;
	private Long topografiaCidOs;
		
	public AelTopografiaLaudosJn() {
	}

	public AelTopografiaLaudosJn(Integer seqJn, String jnUser, Date jnDateTime, String jnOperation, Long seq) {
		this.seq = seq;
	}

	public AelTopografiaLaudosJn(Integer seqJn, String jnUser, Date jnDateTime, String jnOperation, Long seq, Date criadoEm, Long seqCido,
			Long seqExame, Integer serMatricula, Short serVinCodigo) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.topografiaCidOs= seqCido;
		this.exameAp = seqExame;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}
	
	// ATUALIZADOR JOURNALS - ID
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelAeoJnSq1")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = false)
	public long getSeq() {
		return this.seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "LUX_SEQ")
	public Long getExameAp() {
		return this.exameAp;
	}

	public void setExameAp(Long exameAp) {
		this.exameAp = exameAp;
	}

	@Column(name = "CIO_SEQ")
	public Long getTopografiaCidOs() {
		return this.topografiaCidOs;
	}

	public void setTopografiaCidOs(Long topografiaCidOs) {
		this.topografiaCidOs = topografiaCidOs;
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
		SEQ_EXAME("seqExame"),
		SEQ_CIDO("topografiaCidOs"),
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
