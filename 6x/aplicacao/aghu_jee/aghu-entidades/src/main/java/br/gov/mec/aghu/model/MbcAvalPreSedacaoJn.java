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

import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mbcApsJnSeq", sequenceName="AGH.MBC_APS_JN_SEQ", allocationSize = 1)
@Table(name = "MBC_AVAL_PRE_SEDACAO_JN", schema = "AGH")
public class MbcAvalPreSedacaoJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4899864484372115862L;

	private Integer viaAereas;
	private Boolean indParticAvalCli;
	private String avaliacaoClinica;
	private DominioAsa asa;
	private Short tempoJejum;
	private String comorbidades;
	private String exameFisico;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer dcgCrgSeq;
	private Short dcgSeqp;
	
	public MbcAvalPreSedacaoJn() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcApsJnSeq")
	@Column(name = "SEQ_JN", nullable = false, precision = 12, scale = 0)
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}	
	
	
	@Column(name = "DCG_CRG_SEQ", nullable = false)
	public Integer getDcgCrgSeq() {
		return dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	@Column(name = "DCG_SEQP", nullable = false)
	public Short getDcgSeqp() {
		return dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}

	@Column(name = "DVA_SEQ")
	public Integer getViaAereas() {
		return viaAereas;
	}
	
	public void setViaAereas(Integer viaAereas) {
		this.viaAereas = viaAereas;
	}

	@Column(name = "IND_PARTIC_AVAL_CLI")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndParticAvalCli() {
		return indParticAvalCli;
	}

	public void setIndParticAvalCli(Boolean indParticAvalCli) {
		this.indParticAvalCli = indParticAvalCli;
	}
	
	@Column(name = "AVALIACAO_CLINICA")
	public String getAvaliacaoClinica() {
		return this.avaliacaoClinica;
	}

	public void setAvaliacaoClinica(String avaliacaoClinica) {
		this.avaliacaoClinica = avaliacaoClinica;
	}
	
	@Column(name = "ASA")
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioAsa") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioAsa getAsa() {
		return this.asa;
	}
	
	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}	
	
	@Column(name = "TEMPO_JEJUM")
	public Short getTempoJejum() {
		return tempoJejum;
	}

	public void setTempoJejum(Short tempoJejum) {
		this.tempoJejum = tempoJejum;
	}
	
	@Column(name = "COMORBIDADES")
	public String getComorbidades() {
		return this.comorbidades;
	}

	public void setComorbidades(String comorbidades) {
		this.comorbidades = comorbidades;
	}
	
	@Column(name = "EXAME_FISICO")
	public String getExameFisico() {
		return this.exameFisico;
	}

	public void setExameFisico(String exameFisico) {
		this.exameFisico = exameFisico;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 4, scale = 0)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	
}
