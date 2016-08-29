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
@Table(name = "AGH_NOTIFICACAO_DESTINOS_JN", schema = "AGH")
@SequenceGenerator(name="aghNtdSeqJn", sequenceName="AGH.agh_ntd_seq_jn", allocationSize = 1)
@Immutable
public class AghNotificacaoDestinosJn extends BaseJournal {
	
	private static final long serialVersionUID = 149695375629510864L;
	
	private Integer seq;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaContato;
	private Short serVinCodigoContato;
	private Short dddCelular;
	private Long celular;
	private Integer ntsSeq;
	
	public AghNotificacaoDestinosJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghNtdSeqJn")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SER_MATRICULA_CONTATO")
	public Integer getSerMatriculaContato() {
		return serMatriculaContato;
	}

	public void setSerMatriculaContato(Integer serMatriculaContato) {
		this.serMatriculaContato = serMatriculaContato;
	}

	@Column(name = "SER_VIN_CODIGO_CONTATO")
	public Short getSerVinCodigoContato() {
		return serVinCodigoContato;
	}

	public void setSerVinCodigoContato(Short serVinCodigoContato) {
		this.serVinCodigoContato = serVinCodigoContato;
	}

	@Column(name = "NTS_SEQ")
	public Integer getNtsSeq() {
		return ntsSeq;
	}

	public void setNtsSeq(Integer ntsSeq) {
		this.ntsSeq = ntsSeq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@Column(name = "DDD_CELULAR", length=3)
	public Short getDddCelular() {
		return dddCelular;
	}

	public void setDddCelular(Short dddCelular) {
		this.dddCelular = dddCelular;
	}

	@Column(name = "CELULAR", length=15)
	public Long getCelular() {
		return celular;
	}

	public void setCelular(Long celular) {
		this.celular = celular;
	}

	public enum Fields {
		SEQ("seq"),
		SERVIDOR_CONTATO("servidorContato"),
		CELULAR("celular"),
		NTS_SEQ("notificacao.seq");
		
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
