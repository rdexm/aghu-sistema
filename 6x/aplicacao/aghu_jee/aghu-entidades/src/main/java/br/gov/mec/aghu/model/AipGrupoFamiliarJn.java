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

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aipGrupoFamiliarJnSequence", sequenceName="AGH.AIP_GFM_JN_SEQ", allocationSize = 1)
@Table(name = "AIP_GRUPO_FAMILIAR_JN", schema = "AGH")
@Immutable
public class AipGrupoFamiliarJn extends BaseJournal{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5131117694899236018L;


	private Integer seq;


	private Date criadoEm;
	private Date alteradoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipGrupoFamiliarJnSequence")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	  
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}


	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO	", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}


	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public AipGrupoFamiliarJn() {
	}
	
	public enum Fields {
		SEQ("seq");
		
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
