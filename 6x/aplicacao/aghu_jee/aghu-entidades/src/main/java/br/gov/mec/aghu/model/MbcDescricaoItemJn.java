package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.dominio.DominioCaraterCirurgia;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MBC_DESCRICAO_ITENS_JN", schema = "AGH")
@SequenceGenerator(name = "mbcDtiJnSeq", sequenceName = "AGH.MBC_DTI_JN_SEQ", allocationSize = 1)
@Immutable
public class MbcDescricaoItemJn extends BaseJournal {

	private static final long serialVersionUID = -2645326259227146067L;
	private Integer dcgCrgSeq;
	private Short dcgSeqp;
	private DominioAsa asa;
	private String achadosOperatorios;
	private String intercorrenciaClinica;
	private String observacao;
	private DominioCaraterCirurgia carater;
	private Date dthrInicioCirg;
	private Date dthrFimCirg;
	private Date criadoEm;
	private RapServidores servidor;
	private Boolean indIntercorrencia;

	public MbcDescricaoItemJn() {
	}

	public MbcDescricaoItemJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer dcgCrgSeq, Short dcgSeqp) {
		this.dcgCrgSeq = dcgCrgSeq;
		this.dcgSeqp = dcgSeqp;
	}

	public MbcDescricaoItemJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer dcgCrgSeq, Short dcgSeqp,
			DominioAsa asa, String achadosOperatorios, String intercorrenciaClinica, String observacao, DominioCaraterCirurgia carater,
			Date dthrInicioCirg, Date dthrFimCirg, Date criadoEm, RapServidores servidor) {
		this.dcgCrgSeq = dcgCrgSeq;
		this.dcgSeqp = dcgSeqp;
		this.asa = asa;
		this.achadosOperatorios = achadosOperatorios;
		this.intercorrenciaClinica = intercorrenciaClinica;
		this.observacao = observacao;
		this.carater = carater;
		this.dthrInicioCirg = dthrInicioCirg;
		this.dthrFimCirg = dthrFimCirg;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcDtiJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "DCG_CRG_SEQ", nullable = false)
	public Integer getDcgCrgSeq() {
		return this.dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	@Column(name = "DCG_SEQP", nullable = false)
	public Short getDcgSeqp() {
		return this.dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}

	@Column(name = "ASA")
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioAsa") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioAsa getAsa() {
		return this.asa;
	}

	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}

	@Column(name = "ACHADOS_OPERATORIOS", length = 4000)
	@Length(max = 4000)
	public String getAchadosOperatorios() {
		return this.achadosOperatorios;
	}

	public void setAchadosOperatorios(String achadosOperatorios) {
		this.achadosOperatorios = achadosOperatorios;
	}

	@Column(name = "INTERCORRENCIA_CLINICA", length = 4000)
	@Length(max = 4000)
	public String getIntercorrenciaClinica() {
		return this.intercorrenciaClinica;
	}

	public void setIntercorrenciaClinica(String intercorrenciaClinica) {
		this.intercorrenciaClinica = intercorrenciaClinica;
	}

	@Column(name = "OBSERVACAO", length = 1000)
	@Length(max = 1000)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "CARATER", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioCaraterCirurgia getCarater() {
		return this.carater;
	}

	public void setCarater(DominioCaraterCirurgia carater) {
		this.carater = carater;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_CIRG", length = 29)
	public Date getDthrInicioCirg() {
		return this.dthrInicioCirg;
	}

	public void setDthrInicioCirg(Date dthrInicioCirg) {
		this.dthrInicioCirg = dthrInicioCirg;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM_CIRG", length = 29)
	public Date getDthrFimCirg() {
		return this.dthrFimCirg;
	}

	public void setDthrFimCirg(Date dthrFimCirg) {
		this.dthrFimCirg = dthrFimCirg;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false ),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	@Column(name = "IND_INTERCORRENCIA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndIntercorrencia() {
		return indIntercorrencia;
	}

	public void setIndIntercorrencia(Boolean indIntercorrencia) {
		this.indIntercorrencia = indIntercorrencia;
	}	
	
	public enum Fields {
		DCG_CRG_SEQ("dcgCrgSeq"),
		DCG_SEQP("dcgSeqp"),
		ASA("asa"),
		ACHADOS_OPERATORIOS("achadosOperatorios"),
		INTERCORRENCIA_CLINICA("intercorrenciaClinica"),
		OBSERVACAO("observacao"),
		CARATER("carater"),
		DTHR_INICIO_CIRG("dthrInicioCirg"),
		DTHR_FIM_CIRG("dthrFimCirg"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		IND_INTERCORRENCIA("indIntercorrencia"),
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
