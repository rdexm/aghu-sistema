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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "afa_prescricao_medicamentos", schema = "AGH")
@SequenceGenerator(name = "afaPmdSeq", sequenceName = "AGH.AFA_PMM_SQ1", allocationSize = 1)
public class AfaPrescricaoMedicamento  extends BaseEntitySeq<Long> implements java.io.Serializable {

	private static final long serialVersionUID = 6159441666480485955L;
	
	private Long seq;
	private AghAtendimentos atendimento;
	private RapServidores servidor;
	private Date dtReferencia;
	private Date criadoEm;
	private Date dthrInicio;
	private Date dthrFim;
	private Integer version;
	private RapServidores servidorResponsavel;
	private RapConselhosProfissionais conselhoProfissional;
	
	private String nomeResponsavel;
	private String numeroExterno;
	
	private String nroConselhoResponsavel;
	
	
	public enum Fields {
		ATENDIMENTO("atendimento"),
		ATD_SEQ("atendimento.seq"),
		SEQ("seq"),
		SERVIDOR("servidor"),
		DT_REFERENCIA("dtReferencia"),
		CRIADO_EM("criadoEm"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		SITUACAO("situacao"),
		SERVIDOR_RESPONSAVEL("servidorResponsavel"),
		CRM_RESPONSAVEL("crmResponsavel"),
		NOME_RESPONSAVEL("nomeResponsavel"),
		NUMERO_EXTERNO("numeroExterno"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		CONSELHO_PROFISSIONAL("conselhoProfissional"),
		ATENDIMENTO_PACIENTE("atendimento.paciente");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public AfaPrescricaoMedicamento(){}
	
	public AfaPrescricaoMedicamento(AghAtendimentos atendimento, Long seq,
			RapServidores servidor, Date dtReferencia, Date criadoEm,
			Date dthrInicio, Date dthrFim, DominioSituacaoPrescricao situacao,
			Integer version, RapServidores servidorResponsavel,
			String nomeResponsavel,	String numeroExterno) {
		super();
		this.atendimento = atendimento;
		this.servidor = servidor;
		this.dtReferencia = dtReferencia;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.version = version;
		this.servidorResponsavel = servidorResponsavel;
		this.nomeResponsavel = nomeResponsavel;
		this.numeroExterno = numeroExterno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", nullable = false)
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "afaPmdSeq")
	public Long getSeq() {
		return seq;
	}
	/*@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false, precision = 8, scale = 0)) })
	public AfaPrescricaoMedicamentoId getId() {
		return id;
	}*/

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false, insertable = true, updatable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false, insertable = true, updatable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_REFERENCIA", nullable = false, updatable = false)
	public Date getDtReferencia() {
		return dtReferencia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, updatable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = true, updatable = false)
	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", nullable = true, updatable = false)
	public Date getDthrFim() {
		return dthrFim;
	}

	@Version
	@Column(nullable = false)
	public Integer getVersion() {
		return version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "ser_matricula_responsavel", referencedColumnName = "MATRICULA", nullable = true, insertable = true, updatable = false),
			@JoinColumn(name = "ser_vin_codigo_responsavel", referencedColumnName = "VIN_CODIGO", nullable = true, insertable = true, updatable = false) })
	public RapServidores getServidorResponsavel() {
		return servidorResponsavel;
	}

	@Column(name = "nome_responsavel", length = 100)
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	@Column(name = "numero_externo")
	public String getNumeroExterno() {
		return numeroExterno;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPR_CODIGO")
	public RapConselhosProfissionais getConselhoProfissional() {
		return conselhoProfissional;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setServidorResponsavel(RapServidores servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public void setNumeroExterno(String numeroExterno) {
		this.numeroExterno = numeroExterno;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public void setConselhoProfissional(
			RapConselhosProfissionais conselhoProfissional) {
		this.conselhoProfissional = conselhoProfissional;
	}
	
	@PrePersist
	@PreUpdate
	public void validarAfaPrescricaoMedicamento() {
		if(servidorResponsavel == null && (conselhoProfissional == null && nomeResponsavel == null)){
			throw new BaseRuntimeException(
					AfaPrescricaoMedicamentoExceptionCode.AFA_PMM_CK1);
		}
		if(dthrFim != null && dthrInicio!= null && (dthrFim.equals(dthrInicio) || dthrFim.before(dthrInicio))){
			throw new BaseRuntimeException(
					AfaPrescricaoMedicamentoExceptionCode.AFA_PMM_CK2);
		}
	}
	
	private enum AfaPrescricaoMedicamentoExceptionCode implements BusinessExceptionCode {
		AFA_PMM_CK1, AFA_PMM_CK2
	}

	@Column(name = "NRO_CONSELHO_RESPONSAVEL", nullable = true)
	public String getNroConselhoResponsavel() {
		return nroConselhoResponsavel;
	}

	public void setNroConselhoResponsavel(String nroConselhoResponsavel) {
		this.nroConselhoResponsavel = nroConselhoResponsavel;
	}
	
}
