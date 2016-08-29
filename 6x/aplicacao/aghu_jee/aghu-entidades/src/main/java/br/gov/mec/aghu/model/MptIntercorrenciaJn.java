package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MPT_INTERCORRENCIA_JN", schema = "AGH")
@SequenceGenerator(name = "mptItcjSq1", sequenceName = "AGH.MPT_ITC_JN_SEQ", allocationSize = 1)
@Immutable
public class MptIntercorrenciaJn extends BaseJournal implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8046162968112269855L;

	private Short seq;
	private String descricao;
	private Short tpiSeq;
	private Integer sesSeq;
	private Integer version;
	private DominioSituacao indSituacao;
	private Integer serMatricula;
	private Short serVinculoCodigo;

	public MptIntercorrenciaJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptItcjSq1")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Version
	@Column(name = "version")	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	@Column(name="SEQ", nullable=false)
	public Short getSeq() {
		return seq;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	@Column(name="DESCRICAO")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column (name="TPI_SEQ", nullable=false)
	public Short getTpiSeq() {
		return tpiSeq;
	}
	
	public void setTpiSeq(Short tpiSeq) {
		this.tpiSeq = tpiSeq;
	}
	
	@Column(name="SES_SEQ")
	public Integer getSesSeq() {
		return sesSeq;
	}

	public void setSesSeq(Integer sesSeq) {
		this.sesSeq = sesSeq;
	}
	@Column(name="IND_SITUACAO", length= 1)	
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name= "SER_MATRICULA")
	public Integer getSerMatricula() {
		return serMatricula;
	}
	
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	@Column(name="SER_VIN_CODIGO")
	public Short getSerVinculoCodigo() {
		return serVinculoCodigo;
	}
	
	public void setSerVinculoCodigo(Short serVinculoCodigo) {
		this.serVinculoCodigo = serVinculoCodigo;
	}
	public enum Fields {
		JN_USER("jnUser");
		
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
