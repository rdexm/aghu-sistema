package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AEL_IMAGEM_APS_JN", schema = "AGH")
@Immutable
public class AelImagemApJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 1532964395476922320L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Long seq;
	private String legenda;
	private String selecionada;
	private byte[] imagem;
	private Date criadoEm;
	private String indFigura;
	private Long luxSeq;
	private Integer serMatricula;
	private Short serVinCodigo;

	public AelImagemApJn() {
	}

	public AelImagemApJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Long seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public AelImagemApJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Long seq, String legenda,
			String selecionada, byte[] imagem, Date criadoEm, String indFigura, Long luxSeq, Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.legenda = legenda;
		this.selecionada = selecionada;
		this.imagem = imagem;
		this.criadoEm = criadoEm;
		this.indFigura = indFigura;
		this.luxSeq = luxSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	//@GeneratedValue(strategy = GenerationType.AUTO, generator = "")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	// ATUALIZADOR JOURNALS - ID
	
/* ATUALIZADOR JOURNALS - Get / Set	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Long getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Long seqJn) {
		this.seqJn = seqJn;
	}

	@Column(name = "JN_USER", nullable = false, length = 30)
	@Length(max = 30)
	public String getJnUser() {
		return this.jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 29)
	public Date getJnDateTime() {
		return this.jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@Length(max = 3)
	public String getJnOperation() {
		return this.jnOperation;
	}

	public void setJnOperation(String jnOperation) {
		this.jnOperation = jnOperation;
	}*/

	@Column(name = "SEQ", nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "LEGENDA", length = 4000)
	@Length(max = 4000)
	public String getLegenda() {
		return this.legenda;
	}

	public void setLegenda(String legenda) {
		this.legenda = legenda;
	}

	@Column(name = "SELECIONADA", length = 1)
	@Length(max = 1)
	public String getSelecionada() {
		return this.selecionada;
	}

	public void setSelecionada(String selecionada) {
		this.selecionada = selecionada;
	}

	@Column(name = "IMAGEM")
	public byte[] getImagem() {
		return this.imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_FIGURA", length = 1)
	@Length(max = 1)
	public String getIndFigura() {
		return this.indFigura;
	}

	public void setIndFigura(String indFigura) {
		this.indFigura = indFigura;
	}

	@Column(name = "LUX_SEQ")
	public Long getLuxSeq() {
		return this.luxSeq;
	}

	public void setLuxSeq(Long luxSeq) {
		this.luxSeq = luxSeq;
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

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		LEGENDA("legenda"),
		SELECIONADA("selecionada"),
		IMAGEM("imagem"),
		CRIADO_EM("criadoEm"),
		IND_FIGURA("indFigura"),
		LUX_SEQ("luxSeq"),
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
