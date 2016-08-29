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



/**
 * @author jccouto (Jean Couto)
 * @since 30/07/2014
 */
@Entity
@SequenceGenerator(name = "AghMcoPliJnSeq", sequenceName = "AGH.MCO_PLI_JN_SEQ", allocationSize = 1)
@Table(name = "MCO_PLANO_INICIAIS_JN", schema = "AGH")
@Immutable
public class McoPlanoIniciaisJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3242962031077090650L;

	private Integer conNumero;
	private Short seqp;
	private Integer pacCodigo;
	private Long condutaSeq;
	private String complemento;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public McoPlanoIniciaisJn() {

	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AghMcoPliJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "EFI_CON_NUMERO", nullable = false)
	@NotNull
	public Integer getConNumero() {
		return this.conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	@Column(name = "EFI_GSO_SEQP", nullable = false)
	@NotNull
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Column(name = "EFI_GSO_PAC_CODIGO", nullable = false)
	@NotNull
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "CDT_SEQ", nullable = false)
	@NotNull
	public Long getCondutaSeq() {
		return this.condutaSeq;
	}

	public void setCondutaSeq(Long condutaSeq) {
		this.condutaSeq = condutaSeq;
	}

	@Column(name = "COMPLEMENTO", length = 2000)
	@Length(max = 2000)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
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

}
