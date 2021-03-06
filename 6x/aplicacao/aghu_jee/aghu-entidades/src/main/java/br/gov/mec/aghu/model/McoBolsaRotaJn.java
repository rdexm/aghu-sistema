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

/**
 * McoBolsaRotaJn generated by hbm2java
 */
@Entity
@Table(name = "MCO_BOLSA_ROTAS_JN", schema = "AGH")
@Immutable
public class McoBolsaRotaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 5074996267873173192L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Date dthrRompimento;
	private String formaRuptura;
	private String indAmnioscopia;
	private String liquidoAmniotico;
	private String indOdorFetido;
	private Date criadoEm;
	private Short gsoSeqp;
	private Integer gsoPacCodigo;
	private Integer serMatricula;
	private Short serVinCodigo;

	public McoBolsaRotaJn() {
	}

	public McoBolsaRotaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short gsoSeqp, Integer gsoPacCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.gsoSeqp = gsoSeqp;
		this.gsoPacCodigo = gsoPacCodigo;
	}

	public McoBolsaRotaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Date dthrRompimento, String formaRuptura,
			String indAmnioscopia, String liquidoAmniotico, String indOdorFetido, Date criadoEm, Short gsoSeqp, Integer gsoPacCodigo,
			Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.dthrRompimento = dthrRompimento;
		this.formaRuptura = formaRuptura;
		this.indAmnioscopia = indAmnioscopia;
		this.liquidoAmniotico = liquidoAmniotico;
		this.indOdorFetido = indOdorFetido;
		this.criadoEm = criadoEm;
		this.gsoSeqp = gsoSeqp;
		this.gsoPacCodigo = gsoPacCodigo;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ROMPIMENTO", length = 29)
	public Date getDthrRompimento() {
		return this.dthrRompimento;
	}

	public void setDthrRompimento(Date dthrRompimento) {
		this.dthrRompimento = dthrRompimento;
	}

	@Column(name = "FORMA_RUPTURA", length = 15)
	@Length(max = 15)
	public String getFormaRuptura() {
		return this.formaRuptura;
	}

	public void setFormaRuptura(String formaRuptura) {
		this.formaRuptura = formaRuptura;
	}

	@Column(name = "IND_AMNIOSCOPIA", length = 1)
	@Length(max = 1)
	public String getIndAmnioscopia() {
		return this.indAmnioscopia;
	}

	public void setIndAmnioscopia(String indAmnioscopia) {
		this.indAmnioscopia = indAmnioscopia;
	}

	@Column(name = "LIQUIDO_AMNIOTICO", length = 2)
	@Length(max = 2)
	public String getLiquidoAmniotico() {
		return this.liquidoAmniotico;
	}

	public void setLiquidoAmniotico(String liquidoAmniotico) {
		this.liquidoAmniotico = liquidoAmniotico;
	}

	@Column(name = "IND_ODOR_FETIDO", length = 1)
	@Length(max = 1)
	public String getIndOdorFetido() {
		return this.indOdorFetido;
	}

	public void setIndOdorFetido(String indOdorFetido) {
		this.indOdorFetido = indOdorFetido;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "GSO_SEQP", nullable = false)
	public Short getGsoSeqp() {
		return this.gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	@Column(name = "GSO_PAC_CODIGO", nullable = false)
	public Integer getGsoPacCodigo() {
		return this.gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
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
		DTHR_ROMPIMENTO("dthrRompimento"),
		FORMA_RUPTURA("formaRuptura"),
		IND_AMNIOSCOPIA("indAmnioscopia"),
		LIQUIDO_AMNIOTICO("liquidoAmniotico"),
		IND_ODOR_FETIDO("indOdorFetido"),
		CRIADO_EM("criadoEm"),
		GSO_SEQP("gsoSeqp"),
		GSO_PAC_CODIGO("gsoPacCodigo"),
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
