package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aghMciNtcJnSeq", sequenceName="AGH.MCI_NTC_JN_SEQ", allocationSize = 1)
@Table(name = "MCI_NOTAS_CCIH_JN", schema = "AGH")
@Immutable
public class MciNotasCCIHJn extends BaseJournal {

	private static final long serialVersionUID = -8263777452096216968L;
	
	private Integer seq;
	private AipPacientes paciente;
	private String descricao;
	private Date dtHrInicio;
	private Date dtHrEncerramento;
	private Date criadoEm;
	private RapServidores servidor;
	private Date alteradoEm;
	private RapServidores servidorMovimentado;

	public MciNotasCCIHJn() {
	}

	public MciNotasCCIHJn(Integer seq, AipPacientes paciente, String descricao, Date dtHrInicio,
			Date dtHrEncerramento, Date criadoEm, RapServidores servidor, Date alteradoEm,
			RapServidores servidorMovimentado) {
		this.seq = seq;
		this.paciente = paciente;
		this.descricao = descricao;
		this.dtHrInicio = dtHrInicio;
		this.dtHrEncerramento = dtHrEncerramento;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.alteradoEm = alteradoEm;
		this.servidorMovimentado = servidorMovimentado;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMciNtcJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PAC_CODIGO", nullable = false, referencedColumnName = "CODIGO")
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = false)
	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ENCERRAMENTO")
	public Date getDtHrEncerramento() {
		return dtHrEncerramento;
	}

	public void setDtHrEncerramento(Date dtHrEncerramento) {
		this.dtHrEncerramento = dtHrEncerramento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorMovimentado() {
		return this.servidorMovimentado;
	}

	public void setServidorMovimentado(RapServidores servidorMovimentado) {
		this.servidorMovimentado = servidorMovimentado;
	}

}
