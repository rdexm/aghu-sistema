package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AGH_UNIDADES_FUNCIONAIS", schema = "AGH")
@SequenceGenerator(name = "aghUnfSeq", sequenceName = "AGH.AGH_UNF_SQ1", allocationSize = 1)
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity", "PMD.ExcessiveClassLength"})
public class AghUnidadesFuncionais extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = -9111710628571250792L;
	
	private Short seq;
	private String descricao;
	private String sigla;
	private String andar;
	private DominioSituacao indSitUnidFunc;
	private DominioSimNao indPermPacienteExtra;
	private AghAla indAla;
	private Date dthrConfCenso;
	private DominioSimNao indVerfEscalaProfInt;
	private DominioSimNao indBloqLtoIsolamento;
	private DominioSimNao indUnidEmergencia;
	private Short capacInternacao;
	private DominioSimNao indConsClin;
	private DominioSimNao indUnidCti;
	private DominioSimNao indUnidHospDia;
	private DominioSimNao indUnidInternacao;
	private String rotinaFuncionamento;
	private Date hrioInicioAtendimento;
	private Date hrioFimAtendimento;
	private Short nroUnidTempoPmeAdiantadas;
	private Short nroUnidTempoPenAdiantadas;
	private DominioUnidTempo indUnidTempoPmeAdiantada;
	private DominioUnidTempo indUnidTempoPenAdiantada;
	private Date hrioValidadePme;
	private Date hrioValidadePen;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer nroUltimoProtocolo;
	private Byte nroViasPme;
	private Byte nroViasPen;
	private Byte indTipoTratamento;
	private Integer preSerMatricula;
	private Short preSerVinCodigo;
	private Short preEspSeq;
	private Short qtdDiasLimiteCirg;
	private Short qtdDiasLimiteCirgConvenio;
	private Short tempoMinimoCirurgia;
	private Short tempoMaximoCirurgia;
	private DominioSimNao indVisualizaIg;
	private DominioSimNao indVisualizaIap;
	private Byte intervaloEscalaCirurgia;
	private Byte intervaloEscalaProced;
	private DominioSimNao indAnexaDocAutomatico;
	private String localDocAnexo;
	private AghTiposUnidadeFuncional tiposUnidadeFuncional;
	private AghClinicas clinica;
	private FccCentroCustos centroCusto;
	private RapServidores rapServidor;
	private RapServidores rapServidorChefia;
	private Set<AghUnidadesFuncionais> aghUnidadesFuncionais;
	private AghUnidadesFuncionais unfSeq;
	private Set<AinQuartos> ainQuartos = new HashSet<AinQuartos>(0);
	private Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
	private Set<AinLeitos> leitos;
	private Integer version;// Adicionado Campo Version do Controle de
	private SceAlmoxarifado almoxarifado;
	private Boolean controleEstoque;
	private String setor;
	

	private Set<AghAtendimentos> aghAtendimentos;
	private Set<MbcCirurgias> mbcCirurgias;
	private Set<AacGradeAgendamenConsultas> aacGradeAgendamenConsultas;
	
	private Set<AelQuestionariosConvUnid> questionariosConvenioUnid = new HashSet<AelQuestionariosConvUnid>();
	
	private String descricaoIg; // Campo transient.

	private Set<MptAgendaPrescricao> mptAgendaPrescricoes;
	
	private Set<AelExameInternetGrupoArea> exameInternetGrupoArea;

	private Integer tufSeq;
	
	// concorrência

	private enum AghUnidadesFuncionaisExceptionCode implements
			BusinessExceptionCode {
		AGH_UNF_CK13, AGH_UNF_CK14, AGH_UNF_CK15, AGH_UNF_CK18, AGH_UNF_CK10, AGH_UNF_CK19, AGH_UNF_CK20, AGH_UNF_CK21, AGH_UNF_CK22, AGH_UNF_CK9, AGH_UNF_CK17, AGH_UNF_CK16, VALOR_INCORRETO_TEMPO_ADIANTADA_PME, VALOR_INCORRETO_TEMPO_ADIANTADA_PEN
	}

	public AghUnidadesFuncionais(){
		
	}
	
	public AghUnidadesFuncionais(Short seq) {
		this.seq = seq;
	}

	public AghUnidadesFuncionais(Short seq, String descricao, String andar,
			DominioSituacao indSitUnidFunc, DominioSimNao indPermPacienteExtra,
			DominioSimNao indVerfEscalaProfInt,
			DominioSimNao indBloqLtoIsolamento,
			DominioSimNao indUnidEmergencia, DominioSimNao indConsClin,
			AghTiposUnidadeFuncional tiposUnidadeFuncional,
			DominioSimNao indUnidCti, DominioSimNao indUnidHospDia,
			DominioSimNao indUnidInternacao, FccCentroCustos centroCusto,
			DominioSimNao indAnexaDocAutomatico, Integer version) {
		this.seq = seq;
		this.descricao = descricao;
		this.andar = andar;
		this.indSitUnidFunc = indSitUnidFunc;
		this.indPermPacienteExtra = indPermPacienteExtra;
		this.indVerfEscalaProfInt = indVerfEscalaProfInt;
		this.indBloqLtoIsolamento = indBloqLtoIsolamento;
		this.indUnidEmergencia = indUnidEmergencia;
		this.indConsClin = indConsClin;
		this.tiposUnidadeFuncional = tiposUnidadeFuncional;
		this.indUnidCti = indUnidCti;
		this.indUnidHospDia = indUnidHospDia;
		this.indUnidInternacao = indUnidInternacao;
		this.centroCusto = centroCusto;
		this.indAnexaDocAutomatico = indAnexaDocAutomatico;
		this.version = version;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public AghUnidadesFuncionais(Short seq, String descricao, String sigla,
			String andar, DominioSituacao indSitUnidFunc,
			DominioSimNao indPermPacienteExtra, AghAla indAla,
			Date dthrConfCenso, DominioSimNao indVerfEscalaProfInt,
			DominioSimNao indBloqLtoIsolamento,
			DominioSimNao indUnidEmergencia, Short capacInternacao,
			DominioSimNao indConsClin,
			AghTiposUnidadeFuncional tiposUnidadeFuncional,
			AghClinicas clinica, Byte tufSeq, DominioSimNao indUnidCti,
			DominioSimNao indUnidHospDia, DominioSimNao indUnidInternacao,
			String rotinaFuncionamento, Date hrioInicioAtendimento,
			Date hrioFimAtendimento, Short nroUnidTempoPmeAdiantadas,
			Short nroUnidTempoPenAdiantadas,
			DominioUnidTempo indUnidTempoPmeAdiantada,
			DominioUnidTempo indUnidTempoPenAdiantada, Date hrioValidadePme,
			Date hrioValidadePen, FccCentroCustos centroCusto,
			RapServidores rapServidor, Integer nroUltimoProtocolo,
			Byte nroViasPme, Byte nroViasPen, Byte indTipoTratamento,
			Integer preSerMatricula, Short preSerVinCodigo, Short preEspSeq,
			Short qtdDiasLimiteCirg, Short qtdDiasLimiteCirgConv, Short tempoMinimoCirurgia,
			Short tempoMaximoCirurgia, RapServidores rapServidorChefia,
			DominioSimNao indVisualizaIg, DominioSimNao indVisualizaIap,
			Byte intervaloEscalaCirurgia, Byte intervaloEscalaProced,
			DominioSimNao indAnexaDocAutomatico, String localDocAnexo,
			Integer version) {
		this.seq = seq;
		this.descricao = descricao;
		this.sigla = sigla;
		this.andar = andar;
		this.indSitUnidFunc = indSitUnidFunc;
		this.indPermPacienteExtra = indPermPacienteExtra;
		this.indAla = indAla;
		this.dthrConfCenso = dthrConfCenso;
		this.indVerfEscalaProfInt = indVerfEscalaProfInt;
		this.indBloqLtoIsolamento = indBloqLtoIsolamento;
		this.indUnidEmergencia = indUnidEmergencia;
		this.capacInternacao = capacInternacao;
		this.indConsClin = indConsClin;
		this.clinica = clinica;
		this.tiposUnidadeFuncional = tiposUnidadeFuncional;
		this.indUnidCti = indUnidCti;
		this.indUnidHospDia = indUnidHospDia;
		this.indUnidInternacao = indUnidInternacao;
		this.rotinaFuncionamento = rotinaFuncionamento;
		this.hrioInicioAtendimento = hrioInicioAtendimento;
		this.hrioFimAtendimento = hrioFimAtendimento;
		this.nroUnidTempoPmeAdiantadas = nroUnidTempoPmeAdiantadas;
		this.nroUnidTempoPenAdiantadas = nroUnidTempoPenAdiantadas;
		this.indUnidTempoPmeAdiantada = indUnidTempoPmeAdiantada;
		this.indUnidTempoPenAdiantada = indUnidTempoPenAdiantada;
		this.hrioValidadePme = hrioValidadePme;
		this.hrioValidadePen = hrioValidadePen;
		this.centroCusto = centroCusto;
		this.rapServidor = rapServidor;
		this.nroUltimoProtocolo = nroUltimoProtocolo;
		this.nroViasPme = nroViasPme;
		this.nroViasPen = nroViasPen;
		this.indTipoTratamento = indTipoTratamento;
		this.preSerMatricula = preSerMatricula;
		this.preSerVinCodigo = preSerVinCodigo;
		this.preEspSeq = preEspSeq;
		this.qtdDiasLimiteCirg = qtdDiasLimiteCirg;
		this.qtdDiasLimiteCirgConvenio = qtdDiasLimiteCirgConv;
		this.tempoMinimoCirurgia = tempoMinimoCirurgia;
		this.tempoMaximoCirurgia = tempoMaximoCirurgia;
		this.rapServidorChefia = rapServidorChefia;
		this.indVisualizaIg = indVisualizaIg;
		this.indVisualizaIap = indVisualizaIap;
		this.intervaloEscalaCirurgia = intervaloEscalaCirurgia;
		this.intervaloEscalaProced = intervaloEscalaProced;
		this.indAnexaDocAutomatico = indAnexaDocAutomatico;
		this.localDocAnexo = localDocAnexo;
		this.version = version;
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghUnfSeq")
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "SIGLA", unique = true, length = 10)
	@Length(max = 10)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "ANDAR", nullable = false, precision = 2, scale = 0)
	public String getAndar() {
		return this.andar;
	}

	public void setAndar(String andar) {
		this.andar = andar;
	}
	
	@Column(name = "SETOR",precision = 2, scale = 0)
	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	@Column(name = "IND_SIT_UNID_FUNC", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSitUnidFunc() {
		return this.indSitUnidFunc;
	}

	public void setIndSitUnidFunc(DominioSituacao indSitUnidFunc) {
		this.indSitUnidFunc = indSitUnidFunc;
	}

	@Transient
	public boolean isSlcIndSitUnidFunc() {
		if (getIndSitUnidFunc().equals("A")) {
			return getIndSitUnidFunc() == DominioSituacao.A;
		} else {
			return getIndSitUnidFunc() == DominioSituacao.I;
		}
	}

	public void setIndSitUnidFunc(boolean valor) {
		setIndSitUnidFunc(DominioSituacao.getInstance(valor));
	}

	@Column(name = "IND_PERM_PACIENTE_EXTRA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndPermPacienteExtra() {
		return this.indPermPacienteExtra;
	}

	public void setIndPermPacienteExtra(DominioSimNao indPermPacienteExtra) {
		this.indPermPacienteExtra = indPermPacienteExtra;
	}

	@Transient
	public boolean isSlcIndPermPacienteExtra() {
		if (getIndPermPacienteExtra() != null) {
			return getIndPermPacienteExtra() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndPermPacienteExtra(boolean valor) {
		setIndPermPacienteExtra(DominioSimNao.getInstance(valor));
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "IND_ALA")
	public AghAla getIndAla() {
		return this.indAla;
	}

	public void setIndAla(AghAla ala) {
		this.indAla = ala;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CONF_CENSO", length = 7)
	public Date getDthrConfCenso() {
		return this.dthrConfCenso;
	}

	public void setDthrConfCenso(Date dthrConfCenso) {
		this.dthrConfCenso = dthrConfCenso;
	}

	@Column(name = "IND_VERF_ESCALA_PROF_INT", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndVerfEscalaProfInt() {
		return this.indVerfEscalaProfInt;
	}

	public void setIndVerfEscalaProfInt(DominioSimNao indVerfEscalaProfInt) {
		this.indVerfEscalaProfInt = indVerfEscalaProfInt;
	}

	@Transient
	public boolean isSlcIndVerfEscalaProfInt() {
		if (getIndVerfEscalaProfInt() != null) {
			return getIndVerfEscalaProfInt() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndVerfEscalaProfInt(boolean valor) {
		setIndVerfEscalaProfInt(DominioSimNao.getInstance(valor));
	}

	@Column(name = "IND_BLOQ_LTO_ISOLAMENTO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndBloqLtoIsolamento() {
		return this.indBloqLtoIsolamento;
	}

	public void setIndBloqLtoIsolamento(DominioSimNao indBloqLtoIsolamento) {
		this.indBloqLtoIsolamento = indBloqLtoIsolamento;
	}

	@Transient
	public boolean isSlcIndBloqLtoIsolamento() {
		if (getIndBloqLtoIsolamento() != null) {
			return getIndBloqLtoIsolamento() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndBloqLtoIsolamento(boolean valor) {
		setIndBloqLtoIsolamento(DominioSimNao.getInstance(valor));
	}

	@Column(name = "IND_UNID_EMERGENCIA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndUnidEmergencia() {
		return this.indUnidEmergencia;
	}

	public void setIndUnidEmergencia(DominioSimNao indUnidEmergencia) {
		this.indUnidEmergencia = indUnidEmergencia;
	}

	@Transient
	public boolean isSlcIndUnidEmergencia() {
		if (getIndUnidEmergencia() != null) {
			return getIndUnidEmergencia() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndUnidEmergencia(boolean valor) {
		setIndUnidEmergencia(DominioSimNao.getInstance(valor));
	}

	@Column(name = "CAPAC_INTERNACAO", precision = 3, scale = 0)
	public Short getCapacInternacao() {
		return this.capacInternacao;
	}

	public void setCapacInternacao(Short capacInternacao) {
		this.capacInternacao = capacInternacao;
	}

	@Column(name = "IND_CONS_CLIN", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndConsClin() {
		return this.indConsClin;
	}

	public void setIndConsClin(DominioSimNao indConsClin) {
		this.indConsClin = indConsClin;
	}

	@Transient
	public boolean isSlcIndConsClin() {
		if (getIndConsClin() != null) {
			return getIndConsClin() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndConsClin(boolean valor) {
		setIndConsClin(DominioSimNao.getInstance(valor));
	}

	@Column(name = "IND_UNID_CTI", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndUnidCti() {
		return this.indUnidCti;
	}

	@Transient
	public boolean isSlcIndUnidCti() {
		if (getIndUnidCti() != null) {
			return getIndUnidCti() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndUnidCti(boolean valor) {
		setIndUnidCti(DominioSimNao.getInstance(valor));
	}

	public void setIndUnidCti(DominioSimNao indUnidCti) {
		this.indUnidCti = indUnidCti;
	}

	@Column(name = "IND_UNID_HOSP_DIA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndUnidHospDia() {
		return this.indUnidHospDia;
	}

	public void setIndUnidHospDia(DominioSimNao indUnidHospDia) {
		this.indUnidHospDia = indUnidHospDia;
	}

	@Transient
	public boolean isSlcIndUnidHospDia() {
		if (getIndUnidHospDia() != null) {
			return getIndUnidHospDia() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndUnidHospDia(boolean valor) {
		setIndUnidHospDia(DominioSimNao.getInstance(valor));
	}

	@Column(name = "IND_UNID_INTERNACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndUnidInternacao() {
		return this.indUnidInternacao;
	}

	public void setIndUnidInternacao(DominioSimNao indUnidInternacao) {
		this.indUnidInternacao = indUnidInternacao;
	}

	@Transient
	public boolean isSlcIndUnidInternacao() {
		if (getIndUnidInternacao() != null) {
			return getIndUnidInternacao() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndUnidInternacao(boolean valor) {
		setIndUnidInternacao(DominioSimNao.getInstance(valor));
	}

	@Column(name = "ROTINA_FUNCIONAMENTO", length = 2000)
	@Length(max = 2000)
	public String getRotinaFuncionamento() {
		return this.rotinaFuncionamento;
	}

	public void setRotinaFuncionamento(String rotinaFuncionamento) {
		this.rotinaFuncionamento = rotinaFuncionamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HRIO_INICIO_ATENDIMENTO", length = 7)
	public Date getHrioInicioAtendimento() {
		return this.hrioInicioAtendimento;
	}

	public void setHrioInicioAtendimento(Date hrioInicioAtendimento) {
		this.hrioInicioAtendimento = hrioInicioAtendimento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HRIO_FIM_ATENDIMENTO", length = 7)
	public Date getHrioFimAtendimento() {
		return this.hrioFimAtendimento;
	}

	public void setHrioFimAtendimento(Date hrioFimAtendimento) {
		this.hrioFimAtendimento = hrioFimAtendimento;
	}

	@Column(name = "NRO_UNID_TEMPO_PME_ADIANTADAS", precision = 3, scale = 0)
	public Short getNroUnidTempoPmeAdiantadas() {
		return this.nroUnidTempoPmeAdiantadas;
	}

	public void setNroUnidTempoPmeAdiantadas(Short nroUnidTempoPmeAdiantadas) {
		this.nroUnidTempoPmeAdiantadas = nroUnidTempoPmeAdiantadas;
	}

	@Column(name = "NRO_UNID_TEMPO_PEN_ADIANTADAS", precision = 3, scale = 0)
	public Short getNroUnidTempoPenAdiantadas() {
		return this.nroUnidTempoPenAdiantadas;
	}

	public void setNroUnidTempoPenAdiantadas(Short nroUnidTempoPenAdiantadas) {
		this.nroUnidTempoPenAdiantadas = nroUnidTempoPenAdiantadas;
	}

	@Column(name = "IND_UNID_TEMPO_PME_ADIANTADA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioUnidTempo getIndUnidTempoPmeAdiantada() {
		return this.indUnidTempoPmeAdiantada;
	}

	public void setIndUnidTempoPmeAdiantada(
			DominioUnidTempo indUnidTempoPmeAdiantada) {
		this.indUnidTempoPmeAdiantada = indUnidTempoPmeAdiantada;
	}

	@Column(name = "IND_UNID_TEMPO_PEN_ADIANTADA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioUnidTempo getIndUnidTempoPenAdiantada() {
		return this.indUnidTempoPenAdiantada;
	}

	public void setIndUnidTempoPenAdiantada(
			DominioUnidTempo indUnidTempoPenAdiantada) {
		this.indUnidTempoPenAdiantada = indUnidTempoPenAdiantada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HRIO_VALIDADE_PME", length = 7)
	public Date getHrioValidadePme() {
		return this.hrioValidadePme;
	}

	public void setHrioValidadePme(Date hrioValidadePme) {
		this.hrioValidadePme = hrioValidadePme;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HRIO_VALIDADE_PEN", length = 7)
	public Date getHrioValidadePen() {
		return this.hrioValidadePen;
	}

	public void setHrioValidadePen(Date hrioValidadePen) {
		this.hrioValidadePen = hrioValidadePen;
	}

	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	@ManyToOne(fetch = FetchType.LAZY)
	public FccCentroCustos getCentroCusto() {
		return this.centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public RapServidores getRapServidor() {
		return this.rapServidor;
	}

	public void setRapServidor(RapServidores rapServidor) {
		this.rapServidor = rapServidor;
	}

	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_CHEFIA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CHEFIA", referencedColumnName = "VIN_CODIGO") })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getRapServidorChefia() {
		return this.rapServidorChefia;
	}

	public void setRapServidorChefia(RapServidores rapServidorChefia) {
		this.rapServidorChefia = rapServidorChefia;
	}

	@Column(name = "NRO_ULTIMO_PROTOCOLO", precision = 9, scale = 0)
	public Integer getNroUltimoProtocolo() {
		return this.nroUltimoProtocolo;
	}

	public void setNroUltimoProtocolo(Integer nroUltimoProtocolo) {
		this.nroUltimoProtocolo = nroUltimoProtocolo;
	}

	@Column(name = "NRO_VIAS_PME", precision = 2, scale = 0)
	public Byte getNroViasPme() {
		return this.nroViasPme;
	}

	public void setNroViasPme(Byte nroViasPme) {
		this.nroViasPme = nroViasPme;
	}

	@Column(name = "NRO_VIAS_PEN", precision = 2, scale = 0)
	public Byte getNroViasPen() {
		return this.nroViasPen;
	}

	public void setNroViasPen(Byte nroViasPen) {
		this.nroViasPen = nroViasPen;
	}

	@Column(name = "IND_TIPO_TRATAMENTO", precision = 2, scale = 0)
	public Byte getIndTipoTratamento() {
		return this.indTipoTratamento;
	}

	public void setIndTipoTratamento(Byte indTipoTratamento) {
		this.indTipoTratamento = indTipoTratamento;
	}

	@Column(name = "PRE_SER_MATRICULA", precision = 7, scale = 0)
	public Integer getPreSerMatricula() {
		return this.preSerMatricula;
	}

	public void setPreSerMatricula(Integer preSerMatricula) {
		this.preSerMatricula = preSerMatricula;
	}

	@Column(name = "PRE_SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getPreSerVinCodigo() {
		return this.preSerVinCodigo;
	}

	public void setPreSerVinCodigo(Short preSerVinCodigo) {
		this.preSerVinCodigo = preSerVinCodigo;
	}

	@Column(name = "PRE_ESP_SEQ", precision = 4, scale = 0)
	public Short getPreEspSeq() {
		return this.preEspSeq;
	}

	public void setPreEspSeq(Short preEspSeq) {
		this.preEspSeq = preEspSeq;
	}

	@Column(name = "QTD_DIAS_LIMITE_CIRG", precision = 4, scale = 0)
	public Short getQtdDiasLimiteCirg() {
		return this.qtdDiasLimiteCirg;
	}

	public void setQtdDiasLimiteCirg(Short qtdDiasLimiteCirg) {
		this.qtdDiasLimiteCirg = qtdDiasLimiteCirg;
	}

	@Column(name = "QTD_DIAS_LIMITE_CONV", precision = 4, scale = 0)
	public Short getQtdDiasLimiteCirgConvenio() {
		return qtdDiasLimiteCirgConvenio;
	}

	public void setQtdDiasLimiteCirgConvenio(Short qtdDiasLimiteCirgConvenio) {
		this.qtdDiasLimiteCirgConvenio = qtdDiasLimiteCirgConvenio;
	}
	
	@Column(name = "TEMPO_MINIMO_CIRURGIA", precision = 4, scale = 0)
	public Short getTempoMinimoCirurgia() {
		return this.tempoMinimoCirurgia;
	}

	public void setTempoMinimoCirurgia(Short tempoMinimoCirurgia) {
		this.tempoMinimoCirurgia = tempoMinimoCirurgia;
	}

	@Column(name = "TEMPO_MAXIMO_CIRURGIA", precision = 4, scale = 0)
	public Short getTempoMaximoCirurgia() {
		return this.tempoMaximoCirurgia;
	}

	public void setTempoMaximoCirurgia(Short tempoMaximoCirurgia) {
		this.tempoMaximoCirurgia = tempoMaximoCirurgia;
	}

	@Column(name = "IND_VISUALIZA_IG", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndVisualizaIg() {
		return this.indVisualizaIg;
	}

	public void setIndVisualizaIg(DominioSimNao indVisualizaIg) {
		this.indVisualizaIg = indVisualizaIg;
	}

	@Column(name = "IND_VISUALIZA_IAP", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndVisualizaIap() {
		return this.indVisualizaIap;
	}

	public void setIndVisualizaIap(DominioSimNao indVisualizaIap) {
		this.indVisualizaIap = indVisualizaIap;
	}

	@Column(name = "INTERVALO_ESCALA_CIRURGIA", precision = 2, scale = 0)
	public Byte getIntervaloEscalaCirurgia() {
		return this.intervaloEscalaCirurgia;
	}

	public void setIntervaloEscalaCirurgia(Byte intervaloEscalaCirurgia) {
		this.intervaloEscalaCirurgia = intervaloEscalaCirurgia;
	}

	@Column(name = "INTERVALO_ESCALA_PROCED", precision = 2, scale = 0)
	public Byte getIntervaloEscalaProced() {
		return this.intervaloEscalaProced;
	}

	public void setIntervaloEscalaProced(Byte intervaloEscalaProced) {
		this.intervaloEscalaProced = intervaloEscalaProced;
	}

	@Column(name = "IND_ANEXA_DOC_AUTOMATICO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndAnexaDocAutomatico() {
		return this.indAnexaDocAutomatico;
	}

	public void setIndAnexaDocAutomatico(DominioSimNao indAnexaDocAutomatico) {
		this.indAnexaDocAutomatico = indAnexaDocAutomatico;
	}

	@Transient
	public boolean isSlcIndAnexaDocAutomatico() {
		if (getIndAnexaDocAutomatico() != null) {
			return getIndAnexaDocAutomatico() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setSlcIndAnexaDocAutomatico(boolean valor) {
		setIndAnexaDocAutomatico(DominioSimNao.getInstance(valor));
	}

	@Column(name = "LOCAL_DOC_ANEXO", length = 60)
	@Length(max = 60)
	public String getLocalDocAnexo() {
		return this.localDocAnexo;
	}

	public void setLocalDocAnexo(String localDocAnexo) {
		this.localDocAnexo = localDocAnexo;
	}

	@OneToMany(mappedBy = "unfSeq")
	public Set<AghUnidadesFuncionais> getAghUnidadesFuncionais() {
		return this.aghUnidadesFuncionais;
	}

	public void setAghUnidadesFuncionais(
			Set<AghUnidadesFuncionais> aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}

	@JoinColumn(name = "UNF_SEQ", referencedColumnName = "SEQ")
	@ManyToOne(fetch = FetchType.LAZY)
	public AghUnidadesFuncionais getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(AghUnidadesFuncionais unfSeq) {
		this.unfSeq = unfSeq;
	}
	
	@OneToMany(mappedBy = "unidadeFuncional")
	public Set<AelExameInternetGrupoArea> getExameInternetGrupoArea() {
		return exameInternetGrupoArea;
	}

	public void setExameInternetGrupoArea(Set<AelExameInternetGrupoArea> exameInternetGrupoArea) {
		this.exameInternetGrupoArea = exameInternetGrupoArea;
	}

	@Column(name = "SER_MATRICULA", precision = 7, scale = 0, updatable = false, insertable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0, updatable = false, insertable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@JoinColumn(name = "TUF_SEQ", referencedColumnName = "SEQ", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	public AghTiposUnidadeFuncional getTiposUnidadeFuncional() {
		return this.tiposUnidadeFuncional;
	}

	public void setTiposUnidadeFuncional(
			AghTiposUnidadeFuncional tiposUnidadeFuncional) {
		this.tiposUnidadeFuncional = tiposUnidadeFuncional;
	}

	@JoinColumn(name = "CLC_CODIGO", referencedColumnName = "CODIGO")
	@ManyToOne(fetch = FetchType.LAZY)
	public AghClinicas getClinica() {
		return this.clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unidadeFuncional")
	public Set<AinQuartos> getAinQuartos() {
		return this.ainQuartos;
	}

	public void setAinQuartos(Set<AinQuartos> ainQuartos) {
		this.ainQuartos = ainQuartos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unidadeFuncional")
	public Set<AinLeitos> getLeitos() {
		return this.leitos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unidadeFuncional")
	public Set<AghAtendimentos> getAghAtendimentos() {
		return aghAtendimentos;
	}

	public void setAghAtendimentos(Set<AghAtendimentos> aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unidadeFuncional")
	public Set<MbcCirurgias> getMbcCirurgias() {
		return mbcCirurgias;
	}

	public void setMbcCirurgias(Set<MbcCirurgias> mbcCirurgias) {
		this.mbcCirurgias = mbcCirurgias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy="unidadeFuncional" )
	public Set<AacGradeAgendamenConsultas> getAacGradeAgendamenConsultas() {
		return aacGradeAgendamenConsultas;
	}

	public void setAacGradeAgendamenConsultas(Set<AacGradeAgendamenConsultas> aacGradeAgendamenConsultas) {
		this.aacGradeAgendamenConsultas = aacGradeAgendamenConsultas;
	}
	
	
	public void setLeitos(Set<AinLeitos> leitos) {
		this.leitos = leitos;
	}
	
	@JoinColumn(name="ALM_SEQ", referencedColumnName = "SEQ")
	@ManyToOne(fetch = FetchType.LAZY)
	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	@Column(name = "IND_CONTROLE_ESTOQUE", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getControleEstoque() {
		return controleEstoque;
	}

	public void setControleEstoque(Boolean controleEstoque) {
		this.controleEstoque = controleEstoque;
	}

	@SuppressWarnings({"unused", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.NPathComplexity"})
	@PrePersist
	@PreUpdate
	private void validarUnidadeFuncional() {
		
		if (this.capacInternacao != null && this.capacInternacao < 0) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK9);
		}

		if (!((this.nroUnidTempoPenAdiantadas != null
				&& this.indUnidTempoPenAdiantada != null
				&& this.hrioValidadePen != null && this.nroViasPen != null) || (this.nroUnidTempoPenAdiantadas == null
				&& this.indUnidTempoPenAdiantada == null
				&& this.hrioValidadePen == null && this.nroViasPen == null))) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK17);
		}

		if ((this.hrioValidadePme != null
				&& this.nroUnidTempoPmeAdiantadas != null
				&& this.indUnidTempoPmeAdiantada == null && this.nroViasPme == null)
				|| (this.hrioValidadePme != null
						&& this.nroUnidTempoPmeAdiantadas != null
						&& this.indUnidTempoPmeAdiantada != null && this.nroViasPme == null)
				|| (this.hrioValidadePme == null
						&& this.nroUnidTempoPmeAdiantadas != null
						&& this.indUnidTempoPmeAdiantada != null && this.nroViasPme == null)
				|| (this.hrioValidadePme != null
						&& this.nroUnidTempoPmeAdiantadas == null
						&& this.indUnidTempoPmeAdiantada == null && this.nroViasPme == null)
				|| (this.hrioValidadePme == null
						&& this.nroUnidTempoPmeAdiantadas == null
						&& this.indUnidTempoPmeAdiantada == null && this.nroViasPme != null)
				|| (this.hrioValidadePme != null
						&& this.nroUnidTempoPmeAdiantadas != null
						&& this.indUnidTempoPmeAdiantada == null && this.nroViasPme != null)) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK16);

		}
		if ((this.hrioValidadePen != null
				&& this.nroUnidTempoPenAdiantadas != null
				&& this.indUnidTempoPenAdiantada == null && this.nroViasPen == null)
				|| (this.hrioValidadePen != null
						&& this.nroUnidTempoPenAdiantadas != null
						&& this.indUnidTempoPenAdiantada != null && this.nroViasPen == null)
				|| (this.hrioValidadePen == null
						&& this.nroUnidTempoPenAdiantadas != null
						&& this.indUnidTempoPenAdiantada != null && this.nroViasPen == null)
				|| (this.hrioValidadePen != null
						&& this.nroUnidTempoPenAdiantadas == null
						&& this.indUnidTempoPenAdiantada == null && this.nroViasPen == null)
				|| (this.hrioValidadePen == null
						&& this.nroUnidTempoPenAdiantadas == null
						&& this.indUnidTempoPenAdiantada == null && this.nroViasPen != null)
				|| (this.hrioValidadePen != null
						&& this.nroUnidTempoPenAdiantadas != null
						&& this.indUnidTempoPenAdiantada == null && this.nroViasPen != null)) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK17);

		}
		// AGH_UNF_CK16
		if (!(this.nroViasPme == null || (this.nroViasPme != null && this.nroViasPme > 0))) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK19);
		}
		if (!(this.nroViasPen == null || (this.nroViasPen != null && this.nroViasPen > 0))) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK20);
		}
		if (!(this.nroUnidTempoPmeAdiantadas == null || (this.nroUnidTempoPmeAdiantadas != null && this.nroUnidTempoPmeAdiantadas > 0))) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK21);
		}
		if (!(this.nroUnidTempoPenAdiantadas == null || (this.nroUnidTempoPenAdiantadas != null && this.nroUnidTempoPenAdiantadas > 0))) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK22);
		}
		if (this.nroUnidTempoPmeAdiantadas != null
				&& this.nroUnidTempoPmeAdiantadas < -999
				&& this.nroUnidTempoPmeAdiantadas < 999) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.VALOR_INCORRETO_TEMPO_ADIANTADA_PME);
		}
		if (this.nroUnidTempoPenAdiantadas != null
				&& this.nroUnidTempoPenAdiantadas < -999
				&& this.nroUnidTempoPenAdiantadas > 999) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.VALOR_INCORRETO_TEMPO_ADIANTADA_PEN);
		}

		if (!((this.hrioInicioAtendimento == null && this.hrioFimAtendimento == null) || (this.hrioInicioAtendimento != null
				&& this.hrioFimAtendimento != null && this.hrioFimAtendimento
				.after(this.hrioInicioAtendimento)))) {
			throw new BaseRuntimeException(
					AghUnidadesFuncionaisExceptionCode.AGH_UNF_CK13);
		}
	}

	public enum Fields {
		SEQUENCIAL("seq")
		, DESCRICAO("descricao")
		, ANDAR("andar")
		, ALA("indAla")
		, ALA_CODIGO("indAla.codigo")
		, UNF_SEQ("unfSeq")
		, CARACTERISTICAS("caracteristicas")
		, SITUACAO("indSitUnidFunc")
		, SEQUENCIAL_MAE("unfSeq.seq")
		, IND_UNID_EMERGENCIA("indUnidEmergencia")
		, IND_UNID_INTERNACAO("indUnidInternacao")
		, IND_UNID_CTI("indUnidCti")
		, IND_UNID_HOSP_DIA("indUnidHospDia")
		, IND_VISUALIZA_IG("indVisualizaIg")
		, CLINICA("clinica")
		, CENTRO_CUSTO("centroCusto")
		, CENTRO_CUSTO_CODIGO("centroCusto.codigo")
		, SIGLA("sigla")
		, CAPAC_INTERNACAO("capacInternacao")
		, TIPO_UNIDADE_FUNCIONAL_SEQ("tiposUnidadeFuncional")
		, CONTROLE_ESTOQUE("controleEstoque")
		, ATENDIMENTO("aghAtendimentos")
		, ATENDIMENTO_SEQ("aghAtendimentos.seq") 
		, MBC_CIRURGIAS("mbcCirurgias")
		, AAC_GRADE_AGENDAMEN_CONSULTAS("aacGradeAgendamenConsultas")
		, IND_ANEXA_DOC_AUTOMATICO("indAnexaDocAutomatico")
		, LOCAL_DOC_ANEXO("localDocAnexo")
		, TEMPO_MINIMO_CIRURGIA("tempoMinimoCirurgia")
		, ALMOXARIFADO("almoxarifado")
		, RAP_SERVIDOR_CHEFIA("rapServidorChefia")
		, RAP_SERVIDOR_CHEFIA_PESSOA_FISICA("rapServidorChefia.pessoaFisica")
		, TIPOS_UNIDADE_FUNCIONAL("tiposUnidadeFuncional")
		, HRIO_VALIDADE_PME("hrioValidadePme")
		, IND_ALA("indAla")
		, QTD_DIAS_LIMITE_CIRURGIA("qtdDiasLimiteCirg")
		, TUFSEQ("tufSeq")
		, EXAME_INTERNET_GRUPO_AREA("exameInternetGrupoArea")
		, ALM_SEQ("almoxarifado.seq")
		, Setor("setor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	/**
	 * @return the caracteristicas
	 */
	@OneToMany(mappedBy = "unidadeFuncional", fetch = FetchType.LAZY)
	public Set<AghCaractUnidFuncionais> getCaracteristicas() {
		return this.caracteristicas;
	}

	/**
	 * @param caracteristicas
	 *            the caracteristicas to set
	 */
	public void setCaracteristicas(Set<AghCaractUnidFuncionais> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	
	@Transient
	public String getAndarAlaDescricao() {
		return (this.getAndar() != null ? this.getAndar() : "") + " "
				+ (this.getIndAla() != null ? this.getIndAla().toString() : "")
				+ " - "
				+ (this.getDescricao() != null ? this.getDescricao() : "");
	}
	
	@Transient
	public String getSeqAndarAlaDescricao() {
		return this.seq + " - " 
				+ (this.getAndar() != null ? this.getAndar() : "") + " "
				+ (this.getIndAla() != null ? this.getIndAla().toString() : "")
				+ " - "
				+ (this.getDescricao() != null ? this.getDescricao() : "");
	}

	@Transient
	public String getLPADAndarAlaDescricao() {
		return (this.getAndar() != null ? StringUtils.leftPad(this.getAndar()
				.toString(), 2, "0") : "")
				+ " "
				+ (this.getIndAla() != null ? this.getIndAla().toString() : "")
				+ " - "
				+ (this.getDescricao() != null ? this.getDescricao() : "");
	}
	
	@Transient
	public String getLPADAndarAla() {
		return (this.getAndar() != null ? StringUtils.leftPad(this.getAndar()
				.toString(), 2, "0") : "")
				+ " "
				+ (this.getIndAla() != null ? this.getIndAla().toString() : "");
	}

	@Transient
	public String getDescricaoSituacao() {
		return getIndSitUnidFunc().getDescricao();
	}

	@Transient
	public String getDescricaoIg() {

		// TODO: O IG verifica se a unidade é uma unidade de internação por meio
		// de código fixo em procedimento de banco, porém a flag
		// 'indUnidInternacao' não relete esse comportamento.
		// Após conversa com Rejane - CGTI, decidiu-se usar o mesmo padrão
		// (ANDAR + ALA) para todos.

		// if (this.indUnidInternacao != null && this.indUnidInternacao.isSim())
		// {
		descricaoIg = (this.getDescricao() != null ? this.getDescricao() : "")
				+ (this.getAndar() != null ? " - " + this.getAndar() : "")
				+ (this.getIndAla() != null ? " " + this.getIndAla().toString()
						: "");
		// }
		
		return descricaoIg;

		// return (this.getDescricao() != null ? this.getDescricao() : "");
	}
	
	@Transient
	public String getUnidadeDescricao() {
		return (this.getSeq() + " "
				+ " - "
				+ this.getDescricao() );
	}

	@Version
	@Column(name = "VERSION", nullable = false, length = 9)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Transient
	public boolean isAtivo() {
		if (this.indSitUnidFunc != null && this.indSitUnidFunc.isAtivo()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Transient
	public void setAtivo(boolean ativo) {
		if (ativo) {
			this.indSitUnidFunc = DominioSituacao.A;
		} else {
			this.indSitUnidFunc = DominioSituacao.I;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AghUnidadesFuncionais)) {
			return false;
		}
		AghUnidadesFuncionais castOther = (AghUnidadesFuncionais) other;
		return new EqualsBuilder().append(this.getSeq(), castOther.getSeq())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}
	
	@Transient
	public String getSeqEDescricao(){
		return getSeq() + "   " + getDescricao();
	}
	
	@Transient
	public String getDescricaoTrunc(Long size){
		if(size != null && getDescricao() != null && getDescricao().length() > size) {
			return getDescricao().substring(0,size.intValue()-2) + "...";
		} else {
			return getDescricao();
		}
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unidadeFuncional")
	public Set<AelQuestionariosConvUnid> getQuestionariosConvenioUnid() {
		return questionariosConvenioUnid;
	}

	public void setQuestionariosConvenioUnid(Set<AelQuestionariosConvUnid> questionariosConvenioUnid) {
		this.questionariosConvenioUnid = questionariosConvenioUnid;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "unidadeFuncional")
	public Set<MptAgendaPrescricao> getMptAgendaPrescricoes() {
		return mptAgendaPrescricoes;
	}

	public void setMptAgendaPrescricoes(
			Set<MptAgendaPrescricao> mptAgendaPrescricoes) {
		this.mptAgendaPrescricoes = mptAgendaPrescricoes;
	}
	
	@Transient
	public boolean possuiCaracteristica(
			ConstanteAghCaractUnidFuncionais caracteristica) {
		if (this.getCaracteristicas() != null) {
			for (AghCaractUnidFuncionais acuf : this.getCaracteristicas()) {
				if(acuf!=null && acuf.getId()!=null && acuf.getId().getCaracteristica()!=null){
					if (acuf.getId().getCaracteristica().equals(caracteristica)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Column(name = "TUF_SEQ", insertable=false, updatable=false)
	public Integer getTufSeq() {
		return tufSeq;
	}

	public void setTufSeq(Integer tufSeq) {
		this.tufSeq = tufSeq;
	}	
	
}
