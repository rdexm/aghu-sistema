package br.gov.mec.aghu.model;

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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aghMamTrgJnSeq", sequenceName="AGH.MAM_TRG_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_TRIAGENS_JN", schema = "AGH")
@Immutable
public class MamTriagensJn extends BaseJournal {

	
	
	private static final long serialVersionUID = 90433502065359545L;
	private Long seq;
	private String queixaPrincipal;
	private String informacoesComplementares;
	private Integer pacCodigo;
	private Short unfSeq;
	private Date criadoEm;
	private DominioTipoMovimento ultTipoMvto;
	private DominioPacAtendimento indPacAtendimento;
	private Boolean indPacEmergencia;
	private Date dthrInicio;
	private Date dthrFim;
	private Date dthrUltMvto;
	private Date dthrUltSituacao;
	private String micNome;
	private MamOrigemPaciente origemPaciente;
	private Boolean houveContato;
	private String contato;
	private Date dataQueixa;
	private Date horaQueixa;
	private Boolean internado;
	private MamOrigemPaciente hospitalInternado;
	
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaSituacao;
	private Short serVinCodigoSituacao;
	private Integer serMatriculaUltimoMovimento;
	private Short serVinCodigoUltimoMovimento;
	
	private MamSituacaoEmergencia situacaoEmergencia;

	
	// ATUALIZADOR JOURNALS - ID
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMamTrgJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}	
	
	@Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "QUEIXA_PRINCIPAL", nullable = false, length = 2000)
	@NotNull
	@Length(max = 2000)
	public String getQueixaPrincipal() {
		return this.queixaPrincipal;
	}

	public void setQueixaPrincipal(String queixaPrincipal) {
		this.queixaPrincipal = queixaPrincipal;
	}

	@Column(name = "INFORMACOES_COMPLEMENTARES", length = 2000)
	@Length(max = 2000)
	public String getInformacoesComplementares() {
		return this.informacoesComplementares;
	}

	public void setInformacoesComplementares(String informacoesComplementares) {
		this.informacoesComplementares = informacoesComplementares;
	}

	@Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "UNF_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getUnfSeq() {
		return this.unfSeq;
	}
	
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
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

	@Column(name = "ULT_TIPO_MVTO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoMovimento getUltTipoMvto() {
		return this.ultTipoMvto;
	}

	public void setUltTipoMvto(DominioTipoMovimento ultTipoMvto) {
		this.ultTipoMvto = ultTipoMvto;
	}

	@Column(name = "IND_PAC_ATENDIMENTO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioPacAtendimento getIndPacAtendimento() {
		return this.indPacAtendimento;
	}

	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}

	@Column(name = "IND_PAC_EMERGENCIA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPacEmergencia() {
		return this.indPacEmergencia;
	}

	public void setIndPacEmergencia(Boolean indPacEmergencia) {
		this.indPacEmergencia = indPacEmergencia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = false, length = 7)
	@NotNull
	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_FIM", length = 7)
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ULT_MVTO", nullable = false, length = 7)
	@NotNull
	public Date getDthrUltMvto() {
		return this.dthrUltMvto;
	}

	public void setDthrUltMvto(Date dthrUltMvto) {
		this.dthrUltMvto = dthrUltMvto;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_ULT_SITUACAO", nullable = false, length = 7)
	@NotNull
	public Date getDthrUltSituacao() {
		return this.dthrUltSituacao;
	}

	public void setDthrUltSituacao(Date dthrUltSituacao) {
		this.dthrUltSituacao = dthrUltSituacao;
	}

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORP_SEQ")
	public MamOrigemPaciente getOrigemPaciente() {
		return origemPaciente;
	}

	public void setOrigemPaciente(MamOrigemPaciente origemPaciente) {
		this.origemPaciente = origemPaciente;
	}

	@Column(name = "HOUVE_CONTATO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getHouveContato() {
		return houveContato;
	}

	public void setHouveContato(Boolean houveContato) {
		this.houveContato = houveContato;
	}

	@Column(name = "CONTATO", length = 50)
	@Length(max = 50)
	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_QUEIXA", length = 7)
	public Date getDataQueixa() {
		return dataQueixa;
	}

	public void setDataQueixa(Date dataQueixa) {
		this.dataQueixa = dataQueixa;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HORA_QUEIXA", length = 7)
	public Date getHoraQueixa() {
		return horaQueixa;
	}

	public void setHoraQueixa(Date horaQueixa) {
		this.horaQueixa = horaQueixa;
	}

	@Column(name = "IND_INTERNADO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getInternado() {
		return internado;
	}

	public void setInternado(Boolean internado) {
		this.internado = internado;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SER_MATRICULA_SITUACAO", nullable = false)
	@NotNull
	public Integer getSerMatriculaSituacao() {
		return serMatriculaSituacao;
	}

	public void setSerMatriculaSituacao(Integer serMatriculaSituacao) {
		this.serMatriculaSituacao = serMatriculaSituacao;
	}

	@Column(name = "SER_VIN_CODIGO_SITUACAO", nullable = false)
	@NotNull
	public Short getSerVinCodigoSituacao() {
		return serVinCodigoSituacao;
	}

	public void setSerVinCodigoSituacao(Short serVinCodigoSituacao) {
		this.serVinCodigoSituacao = serVinCodigoSituacao;
	}

	@Column(name = "SER_MATRICULA_ULT_MVTO", nullable = false)
	@NotNull
	public Integer getSerMatriculaUltimoMovimento() {
		return serMatriculaUltimoMovimento;
	}

	public void setSerMatriculaUltimoMovimento(Integer serMatriculaUltimoMovimento) {
		this.serMatriculaUltimoMovimento = serMatriculaUltimoMovimento;
	}

	@Column(name = "SER_VIN_CODIGO_ULT_MVTO", nullable = false)
	@NotNull
	public Short getSerVinCodigoUltimoMovimento() {
		return serVinCodigoUltimoMovimento;
	}

	public void setSerVinCodigoUltimoMovimento(Short serVinCodigoUltimoMovimento) {
		this.serVinCodigoUltimoMovimento = serVinCodigoUltimoMovimento;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEG_SEQ")
	public MamSituacaoEmergencia getSituacaoEmergencia() {
		return situacaoEmergencia;
	}

	public void setSituacaoEmergencia(MamSituacaoEmergencia situacaoEmergencia) {
		this.situacaoEmergencia = situacaoEmergencia;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORP_SEQ_HOSP")
	public MamOrigemPaciente getHospitalInternado() {
		return hospitalInternado;
	}

	public void setHospitalInternado(MamOrigemPaciente hospitalInternado) {
		this.hospitalInternado = hospitalInternado;
	}



	public enum Fields {
		SEQ("seq"),
		PAC_CODIGO("pacCodigo"),		
		UNF_SEQ("unfSeq"),
		DTHR_ULT_MVTO("dthrUltMvto"),
		IND_PAC_ATENDIMENTO("indPacAtendimento"),
		ULT_TIPO_MVTO("ultTipoMvto"),
		DTHR_FIM("dthrFim"),
		DTHR_INICIO("dthrInicio"),
		SITUACAO_EMERGENCIA("situacaoEmergencia"),
		JN_DATE_TIME("dataAlteracao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MamTriagens)) {
			return false;
		}
		MamTriagens other = (MamTriagens) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
