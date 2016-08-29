package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mamTrgSq1", sequenceName="AGH.MAM_TRG_SQ1", allocationSize = 1)
@Table(name = "MAM_TRIAGENS", schema = "AGH")
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
public class MamTriagens extends BaseEntitySeq<Long> implements java.io.Serializable {

	private static final long serialVersionUID = 90433502065359545L;
	private Long seq;
	private String queixaPrincipal;
	private String informacoesComplementares;
	private AipPacientes paciente;
	private AghUnidadesFuncionais unidadeFuncional;
	private Date criadoEm;
	private DominioTipoMovimento ultTipoMvto;
	private DominioPacAtendimento indPacAtendimento;
	private Boolean indPacEmergencia;
	private Date dthrInicio;
	private Date dthrFim;
	private Date dthrUltMvto;
	private Date dthrUltSituacao;
	private String micNome;
	private String nomeResponsavelConta;
	private MamOrigemPaciente origemPaciente;
	private Boolean houveContato;
	private String contato;
	private Date dataQueixa;
	private Date horaQueixa;
	private Boolean internado;
	private MamOrigemPaciente hospitalInternado;
	private MamSituacaoEmergencia situacaoEmergencia;
	private RapServidores servidor;
	private RapServidores servidorUltimoMovimento;
	private RapServidores servidorSituacao;
	private MamUnidAtendem unidadeAtendimento;
	
	private Set<MamAnamneses> mamAnamneseses = new HashSet<MamAnamneses>(0);
	private Set<MamAtestados> mamAtestadoses = new HashSet<MamAtestados>(0);
	private Set<MamTrgAlergias> mamTrgAlergiases = new HashSet<MamTrgAlergias>(0);
	private Set<MamReceituarios> mamReceituarioses = new HashSet<MamReceituarios>(0);
	private Set<MamAlergias> mamAlergiases = new HashSet<MamAlergias>(0);
	private Set<MamEvolucoes> mamEvolucoeses = new HashSet<MamEvolucoes>(0);
	private Set<MamMotivoAtendimento> motivoAtendimentos = new HashSet<MamMotivoAtendimento>(0);
	private Set<MamNotaAdicionalEvolucoes> mamNotaAdicionalEvolucoeses = new HashSet<MamNotaAdicionalEvolucoes>(0);
	private List<MamTrgGravidade> mamTrgGravidade;

	private List<MamRegistro> mamRegistros;
	private List<MamTrgEncInterno> mamTrgEncInterno;
	private List<MamTrgEncExternos> mamTrgEncExterno;
	private List<MamTrgPrevAtend> mamTrgPrevAtend;
	private List<MamDestinos> mamDestinos;

	public MamTriagens() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamTrgSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false)
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", nullable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", insertable= false, updatable= false)
	@NotNull
	public MamUnidAtendem getUnidadeAtendimento() {
		return unidadeAtendimento;
	}

	public void setUnidadeAtendimento(MamUnidAtendem unidadeAtendimento) {
		this.unidadeAtendimento = unidadeAtendimento;
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
	
	@Column(name = "NOME_RESP_CONTA", length = 60)
	@Length(max = 60)
	public String getNomeResponsavelConta() {
		return nomeResponsavelConta;
	}

	public void setNomeResponsavelConta(String nomeResponsavelConta) {
		this.nomeResponsavelConta = nomeResponsavelConta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORP_SEQ_HOSP")
	public MamOrigemPaciente getHospitalInternado() {
		return hospitalInternado;
	}

	public void setHospitalInternado(MamOrigemPaciente hospitalInternado) {
		this.hospitalInternado = hospitalInternado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORP_SEQ")
	public MamOrigemPaciente getOrigemPaciente() {
		return origemPaciente;
	}

	public void setOrigemPaciente(MamOrigemPaciente origemPaciente) {
		this.origemPaciente = origemPaciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEG_SEQ")
	public MamSituacaoEmergencia getSituacaoEmergencia() {
		return situacaoEmergencia;
	}

	public void setSituacaoEmergencia(MamSituacaoEmergencia situacaoEmergencia) {
		this.situacaoEmergencia = situacaoEmergencia;
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
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_ULT_MVTO", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO_ULT_MVTO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public RapServidores getServidorUltimoMovimento() {
		return servidorUltimoMovimento;
	}
	
	public void setServidorUltimoMovimento(RapServidores servidorUltimoMovimento) {
		this.servidorUltimoMovimento = servidorUltimoMovimento;
	}

	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_SITUACAO", referencedColumnName = "MATRICULA", nullable = false), 
		@JoinColumn(name = "SER_VIN_CODIGO_SITUACAO", referencedColumnName = "VIN_CODIGO", nullable = false)})
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public RapServidores getServidorSituacao() {
		return servidorSituacao;
	}

	public void setServidorSituacao(RapServidores servidorSituacao) {
		this.servidorSituacao = servidorSituacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTriagens")
	public Set<MamAnamneses> getMamAnamneseses() {
		return this.mamAnamneseses;
	}

	public void setMamAnamneseses(Set<MamAnamneses> mamAnamneseses) {
		this.mamAnamneseses = mamAnamneseses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTriagens")
	public Set<MamAtestados> getMamAtestadoses() {
		return this.mamAtestadoses;
	}

	public void setMamAtestadoses(Set<MamAtestados> mamAtestadoses) {
		this.mamAtestadoses = mamAtestadoses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTriagens")
	public Set<MamTrgAlergias> getMamTrgAlergiases() {
		return this.mamTrgAlergiases;
	}

	public void setMamTrgAlergiases(Set<MamTrgAlergias> mamTrgAlergiases) {
		this.mamTrgAlergiases = mamTrgAlergiases;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTriagens")
	public Set<MamReceituarios> getMamReceituarioses() {
		return this.mamReceituarioses;
	}

	public void setMamReceituarioses(Set<MamReceituarios> mamReceituarioses) {
		this.mamReceituarioses = mamReceituarioses;
	}


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTriagens")
	public Set<MamAlergias> getMamAlergiases() {
		return this.mamAlergiases;
	}

	public void setMamAlergiases(Set<MamAlergias> mamAlergiases) {
		this.mamAlergiases = mamAlergiases;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTriagens")
	public Set<MamEvolucoes> getMamEvolucoeses() {
		return this.mamEvolucoeses;
	}

	public void setMamEvolucoeses(Set<MamEvolucoes> mamEvolucoeses) {
		this.mamEvolucoeses = mamEvolucoeses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "triagem")
	public Set<MamMotivoAtendimento> getMotivoAtendimentos() {
		return this.motivoAtendimentos;
	}

	public void setMotivoAtendimentos(Set<MamMotivoAtendimento> motivoAtendimentos) {
		this.motivoAtendimentos = motivoAtendimentos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTriagens")
	public Set<MamNotaAdicionalEvolucoes> getMamNotaAdicionalEvolucoeses() {
		return this.mamNotaAdicionalEvolucoeses;
	}

	public void setMamNotaAdicionalEvolucoeses(
			Set<MamNotaAdicionalEvolucoes> mamNotaAdicionalEvolucoeses) {
		this.mamNotaAdicionalEvolucoeses = mamNotaAdicionalEvolucoeses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTriagens")
	public List<MamTrgGravidade> getMamTrgGravidade() {
		return mamTrgGravidade;
	}

	public void setMamTrgGravidade(List<MamTrgGravidade> mamTrgGravidade) {
		this.mamTrgGravidade = mamTrgGravidade;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "triagem")
	public List<MamRegistro> getMamRegistros() {
		return mamRegistros;
	}

	public void setMamRegistros(List<MamRegistro> mamRegistros) {
		this.mamRegistros = mamRegistros;
	}


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "triagem")
	public List<MamTrgEncInterno> getMamTrgEncInterno() {
		return mamTrgEncInterno;
	}

	public void setMamTrgEncInterno(List<MamTrgEncInterno> mamTrgEncInterno) {
		this.mamTrgEncInterno = mamTrgEncInterno;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "triagem")
	public List<MamTrgEncExternos> getMamTrgEncExterno() {
		return mamTrgEncExterno;
	}

	public void setMamTrgEncExterno(List<MamTrgEncExternos> mamTrgEncExterno) {
		this.mamTrgEncExterno = mamTrgEncExterno;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "triagem")
	public List<MamTrgPrevAtend> getMamTrgPrevAtend() {
		return mamTrgPrevAtend;
	}

	public void setMamTrgPrevAtend(List<MamTrgPrevAtend> mamTrgPrevAtend) {
		this.mamTrgPrevAtend = mamTrgPrevAtend;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "triagem")
	public List<MamDestinos> getMamDestinos() {
		return mamDestinos;
	}

	public void setMamDestinos(List<MamDestinos> mamDestinos) {
		this.mamDestinos = mamDestinos;
	}

	public enum Fields {
		SEQ("seq"),
		PAC_CODIGO("paciente.codigo"),
		SEG_SEQ("situacaoEmergencia.seq"),
		UNF_SEQ("unidadeFuncional.seq"),
		DTHR_ULT_MVTO("dthrUltMvto"),
		IND_PAC_ATENDIMENTO("indPacAtendimento"),
		ULT_TIPO_MVTO("ultTipoMvto"),
		DTHR_FIM("dthrFim"),
		DTHR_INICIO("dthrInicio"),
		PACIENTE("paciente"),
		HOSPITAL_INTERNADO_SEQ("hospitalInternado.seq"),
		IND_PAC_EMERGENCIA("indPacEmergencia"),
		SITUACAO_EMERGENCIA("situacaoEmergencia"),
		MAM_REGISTROS("mamRegistros"),
		MAM_DESTINOS("mamDestinos"),
		MAM_TRG_ENC_INTERNO("mamTrgEncInterno"),
		MAM_TRG_ENC_EXTERNO("mamTrgEncExterno"),
		MAM_TRG_PREV_ATEND("mamTrgPrevAtend"),
		MAM_TRG_GRAVIDADE("mamTrgGravidade"),
		MAM_TRG_ALERGIAS("mamTrgAlergiases"),
		MAM_UNID_ATENDIMENTO("unidadeAtendimento"),
		ORIGEM_PACIENTE("origemPaciente"),
		QUEIXA_PRINCIPAL("queixaPrincipal"),
		DATA_QUEIXA("dataQueixa"),
		HORA_QUEIXA("horaQueixa"),
		IND_INTERNADO("internado"),
		INFORMACOES_COMPLEMENTARES("informacoesComplementares"),
		HOUVE_CONTATO("houveContato"),
		CONTATO("contato"),
		NOME_RESPONSAVEL_CONTA("nomeResponsavelConta");

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
