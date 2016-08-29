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
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "mcoRxsJnSeq", sequenceName = "AGH.MCO_RXS_JN_SEQ", allocationSize = 1)
@Table(name = "MCO_RESULTADO_EXAME_SIGNIFS_JN", schema = "AGH")
@Immutable
public class McoResultadoExameSignifsJn extends BaseJournal {
	private static final long serialVersionUID = 8963839345222594455L;

	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Short seqp;
	private Date dataRealizacao;
	private String resultado;
	private Date criadoEm;
	private String emaExaSigla;
	private Integer emaManSeq;
	private Short iseSeqp;
	private Integer iseSoeSeq;
	private Integer eexSeq;
	private Integer serMatricula;
	private Short serVinCodigo;

	public McoResultadoExameSignifsJn() {
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mcoRxsJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	@NotNull
	public Integer getGsoPacCodigo() {
		return this.gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	@Column(name = "GSO_SEQP", nullable = false, precision = 3, scale = 0)
	@NotNull
	public Short getGsoSeqp() {
		return this.gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 3, scale = 0)
	@NotNull
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_REALIZACAO", length = 7)
	public Date getDataRealizacao() {
		return this.dataRealizacao;
	}

	public void setDataRealizacao(Date dataRealizacao) {
		this.dataRealizacao = dataRealizacao;
	}

	@Column(name = "RESULTADO", length = 55)
	@Length(max = 55)
	public String getResultado() {
		return this.resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "EMA_EXA_SIGLA", length = 5)
	@Length(max = 5)
	public String getEmaExaSigla() {
		return this.emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	@Column(name = "EMA_MAN_SEQ", precision = 5, scale = 0)
	public Integer getEmaManSeq() {
		return this.emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	@Column(name = "ISE_SEQP", precision = 3, scale = 0)
	public Short getIseSeqp() {
		return this.iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	@Column(name = "ISE_SOE_SEQ", precision = 8, scale = 0)
	public Integer getIseSoeSeq() {
		return this.iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	@Column(name = "EEX_SEQ")
	public Integer getEexSeq() {
		return eexSeq;
	}

	public void setEexSeq(Integer eexSeq) {
		this.eexSeq = eexSeq;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	@NotNull
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
}