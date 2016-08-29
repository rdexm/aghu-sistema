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

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AEL_NOMENC_TOPO_CIDS_JN", schema = "AGH")
@Immutable
public class AelNomencTopoCidJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 7894361521307892169L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer lueLugSeq;
	private Short lueSeqp;
	private Integer luaLutSeq;
	private Short luaSeqp;
	private Integer cidSeq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public AelNomencTopoCidJn() {
	}

	public AelNomencTopoCidJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer lueLugSeq, Short lueSeqp,
			Integer luaLutSeq, Short luaSeqp, Integer cidSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.lueLugSeq = lueLugSeq;
		this.lueSeqp = lueSeqp;
		this.luaLutSeq = luaLutSeq;
		this.luaSeqp = luaSeqp;
		this.cidSeq = cidSeq;
	}

	public AelNomencTopoCidJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer lueLugSeq, Short lueSeqp,
			Integer luaLutSeq, Short luaSeqp, Integer cidSeq, Date criadoEm, Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.lueLugSeq = lueLugSeq;
		this.lueSeqp = lueSeqp;
		this.luaLutSeq = luaLutSeq;
		this.luaSeqp = luaSeqp;
		this.cidSeq = cidSeq;
		this.criadoEm = criadoEm;
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

	@Column(name = "LUE_LUG_SEQ", nullable = false)
	public Integer getLueLugSeq() {
		return this.lueLugSeq;
	}

	public void setLueLugSeq(Integer lueLugSeq) {
		this.lueLugSeq = lueLugSeq;
	}

	@Column(name = "LUE_SEQP", nullable = false)
	public Short getLueSeqp() {
		return this.lueSeqp;
	}

	public void setLueSeqp(Short lueSeqp) {
		this.lueSeqp = lueSeqp;
	}

	@Column(name = "LUA_LUT_SEQ", nullable = false)
	public Integer getLuaLutSeq() {
		return this.luaLutSeq;
	}

	public void setLuaLutSeq(Integer luaLutSeq) {
		this.luaLutSeq = luaLutSeq;
	}

	@Column(name = "LUA_SEQP", nullable = false)
	public Short getLuaSeqp() {
		return this.luaSeqp;
	}

	public void setLuaSeqp(Short luaSeqp) {
		this.luaSeqp = luaSeqp;
	}

	@Column(name = "CID_SEQ", nullable = false)
	public Integer getCidSeq() {
		return this.cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
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

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		LUE_LUG_SEQ("lueLugSeq"),
		LUE_SEQP("lueSeqp"),
		LUA_LUT_SEQ("luaLutSeq"),
		LUA_SEQP("luaSeqp"),
		CID_SEQ("cidSeq"),
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
