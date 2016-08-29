package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Max;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.core.validation.CPF;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity", "PMD.ExcessiveClassLength"})
@Entity
@SequenceGenerator(name="aipPacSq1", sequenceName="AGH.AIP_PAC_SQ1", allocationSize = 1)
@Table(name = "AIP_PACIENTES", schema = "AGH")
@org.hibernate.annotations.Cache(usage =org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
public class AipPacientes extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -79858385251026096L;
	
	// Maior valor que um prontuário real de paciente pode ter é 90 milhões
	// e consequentemente o menor valor do prontuário virtual é 90.000.001
	public static final int VALOR_MAXIMO_PRONTUARIO = 90000000;
	
	private Integer codigo;
	private AipNacionalidades aipNacionalidades;
	private RapServidores rapServidoresRecadastro;
	private AipUfs aipUfs;
	private AipOcupacoes aipOcupacoes;
	private AipCidades aipCidades;
	private RapServidores rapServidoresCadastro;
	private AipPacientes maePaciente;
	private FccCentroCustos fccCentroCustosCadastro;
	private FccCentroCustos fccCentroCustosRecadastro;
	private String nome;
	private String nomeMae;
	private Date dtNascimento;
	private Date dtIdentificacao;
	private String ltoLtoId;
	private Short unfSeq;
	private Short qrtNumero;
	private Boolean insereProntuario;
	private AipGrupoFamiliarPacientes grupoFamiliarPaciente;

	private Set<MtxComorbidade> comorbidade;

	// FIXME Verificar a necessidade deste existir este atributo
	@SuppressWarnings("unused")
	private String dsSuggestion;

	private DominioSexo sexoBiologico;

	private DominioTipoDataObito tipoDataObito;

	private Integer version;

	private Set<AinInternacao> internacoes = new HashSet<AinInternacao>(0);
	private Set<AipEnderecosPacientes> enderecos = new HashSet<AipEnderecosPacientes>(
			0);

	private Set<AipSolicitacaoProntuarios> aipSolicitacaoProntuarios = new HashSet<AipSolicitacaoProntuarios>(
			0);

	private Set<FatPacienteTransplantes> pacienteTransplantes;
	private Set<FatListaPacApac> listaPacApacs;

	/**
	 * Cor do Paciente
	 */
	private DominioCor cor;
	
	/**
	 * Etnia do Paciente
	 */
	private AipEtnia etnia;

	/**
	 * Sexo do Paciente
	 */
	private DominioSexo sexo;
	
	
	/**
	 * Id do sistema legado (utilizado em migrações de pacientes)
	 */
	private Long idSistemaLegado;

	/**
	 * Grau de InstruÃ§Ã£o do Paciente (1Â° grau, 2Â° grau, etc)
	 */
	private DominioGrauInstrucao grauInstrucao;

	private String nomePai;
	private String naturalidade;
	private Short dddFoneResidencial;
	private Long foneResidencial;
	private Short dddFoneRecado;
	private String foneRecado;

	/**
	 * Estado Civil do paciente (solteiro, casado, etc)
	 */
	private DominioEstadoCivil estadoCivil;

	private Long cpf;
	private Integer prontuario;
	private Integer prontuarioEditado;
	private Date dtObito;
	private String rg;
	private String orgaoEmisRg;
	private String observacao;
	private DominioTipoProntuario prntAtivo;
	private Long numeroCNH;
	private Date dataValidadeCNH;

	/**
	 * Indica se o cadastro foi confirmado
	 */
	private DominioSimNao cadConfirmado;

	/**
	 * Indica se deve gerar prontuÃ¡rio
	 */
	private DominioSimNao indGeraProntuario;

	private Date dtUltInternacao;
	private Date dtUltAlta;
	private Date dtUltConsulta;
	private Date dtUltProcedimento;
	private Date dtUltAtendHospDia;
	private Date dtUltAltaHospDia;

	private AinQuartos quarto;
	private AghUnidadesFuncionais unidadeFuncional; // private Short unfSeq;
	private AinLeitos leito; // private String ltoLtoId;
	private String regNascimento;
	private BigInteger nroCartaoSaude;
	private Date dtRecadastro;

	/**
	 * Indica se o paciente Ã© vip
	 */
	private DominioSimNao indPacienteVip;

	private Date dtObitoExterno;
	private McoRecemNascidos recemNascido; 
	private Long numeroPis;
	private Short volumes;

	private DominioSimNao indPacProtegido;

	private Date criadoEm;

	private DominioSimNao indPacAgfa;

	private Integer idade;

	private AipPacientesDadosCns aipPacientesDadosCns;
	private AipPacienteDadoClinicos aipPacienteDadoClinicos;
	private Set<RapPessoasFisicas> rapPessoasFisicas = new HashSet<RapPessoasFisicas>(
			0);
	private Set<AipAlturaPacientes> aipAlturaPacienteses = new HashSet<AipAlturaPacientes>(
			0);
	private Set<AipPacientes> aipPacienteses = new HashSet<AipPacientes>(0);
	private Set<AipPesoPacientes> aipPesoPacienteses = new HashSet<AipPesoPacientes>(
			0);
	
	private Set<AipFonemaPacientes> fonemas = new HashSet<AipFonemaPacientes>(0);

	private Set<AipFonemasMaePaciente> fonemasMae = new HashSet<AipFonemasMaePaciente>(
			0);

	private Set<MbcCirurgias> mbcCirurgias;

	private Set<AacConsultas> aacConsultas;
	
	private Set<MciNotificacaoGmr> mciNotificacaoGmr;

	private Set<AghAtendimentos> aghAtendimentos;

	private Set<AipConveniosSaudePaciente> aipConveniosSaudePaciente;

	private Set<AinAtendimentosUrgencia> atendimentosUrgencia;
	
	private Boolean geraProntuarioVirtual;
	
	private String nomeSocial;
	
	private Boolean pacienteNotifGMR;
	
	private Set<FatCandidatosApacOtorrino> listaCandidatosApacOtorrino;
		
	private List<MtxTransplantes> transplanteDoador;

	private List<MtxTransplantes> transplanteReceptor;
	
	private List<MptControleFrequencia> mptControleFrequenciaPaciente;
	
		
	private enum PacienteExceptionCode implements BusinessExceptionCode {
		PACIENTE_VIP_DEVE_TER_PRONTUARIO, INFO_QUARTO_UNINF_LEITO_INCORRETAS,
		PACIENTE_COM_PRONTUARIO_SEM_COR, PACIENTE_COM_PRONTUARIO_SEM_SEXO, 
		PACIENTE_COM_PRONTUARIO_SEM_INSTRUCAO, PACIENTE_COM_PRONTUARIO_SEM_PAI,
		PACIENTE_COM_PRONTUARIO_SEM_ESTADO_CIVIL
	}

	public AipPacientes() {
	}

	public AipPacientes(Integer codigo, Date dtNascimento) {
		super();
		this.codigo = codigo;
		this.dtNascimento = dtNascimento;
	}

	public AipPacientes(Integer codigo, RapServidores rapServidoresCadastro,
			FccCentroCustos fccCentroCustosCadastro, String nome,
			String nomeMae, Date dtNascimento, Date dtIdentificacao,
			DominioSimNao cadConfirmado, DominioSimNao indGeraProntuario,
			DominioSimNao indPacienteVip) {
		this.codigo = codigo;
		this.rapServidoresCadastro = rapServidoresCadastro;
		this.fccCentroCustosCadastro = fccCentroCustosCadastro;
		this.nome = nome;
		this.nomeMae = nomeMae;
		this.dtNascimento = dtNascimento;
		this.dtIdentificacao = dtIdentificacao;
		this.cadConfirmado = cadConfirmado;
		this.indGeraProntuario = indGeraProntuario;
		this.indPacienteVip = indPacienteVip;
	}
	
	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public AipPacientes(Integer codigo, AipNacionalidades aipNacionalidades,
			RapServidores rapServidoresRecadastro, AipUfs aipUfs,
			AipOcupacoes aipOcupacoes, AipCidades aipCidades,
			RapServidores rapServidoresCadastro, AipPacientes maePaciente,
			FccCentroCustos fccCentroCustosCadastro, String nome,
			String nomeMae, Date dtNascimento, Date dtIdentificacao,
			DominioSexo sexoBiologico,
			FccCentroCustos fccCentroCustosRecadastro, DominioCor cor,
			DominioSexo sexo, DominioGrauInstrucao grauInstrucao,
			String nomePai, String naturalidade, Short dddFoneResidencial,
			Long foneResidencial, Short dddFoneRecado, String foneRecado,
			DominioEstadoCivil estadoCivil, Long cpf, Long numeroCNH, Date dataValidadeCNH,
			Integer prontuario,	Date dtObito, String rg, String orgaoEmisRg, String observacao,
			DominioTipoProntuario prntAtivo, DominioSimNao cadConfirmado,
			DominioSimNao indGeraProntuario, Date dtUltInternacao,
			Date dtUltAlta, Date dtUltConsulta, Date dtUltProcedimento,
			Date dtUltAtendHospDia, Date dtUltAltaHospDia, AinQuartos quarto,
			AghUnidadesFuncionais unidadeFuncional, AinLeitos leito,
			String regNascimento, BigInteger nroCartaoSaude,
			Date dtRecadastro, DominioSimNao indPacienteVip,
			DominioTipoDataObito tipoDataObito, Date dtObitoExterno,
			McoRecemNascidos recemNascido, Short rnaGsoSeqp, Byte rnaSeqp,
			Long numeroPis, Short volumes, DominioSimNao indPacProtegido,
			Date criadoEm, DominioSimNao indPacAgfa,
			AipPacientesDadosCns aipPacientesDadosCns,
			AipPacienteDadoClinicos aipPacienteDadoClinicos,
			Set<RapPessoasFisicas> rapPessoasFisicas,
			Set<AipAlturaPacientes> aipAlturaPacienteses,
			Set<AipPacientes> aipPacienteses,
			Set<AipPesoPacientes> aipPesoPacienteses,
			Set<MbcCirurgias> mbcCirurgias, Set<AacConsultas> aacConsultas,
			Set<AghAtendimentos> aghAtendimentos) {
		this.codigo = codigo;
		this.aipNacionalidades = aipNacionalidades;
		this.rapServidoresRecadastro = rapServidoresRecadastro;
		this.aipUfs = aipUfs;
		this.aipOcupacoes = aipOcupacoes;
		this.aipCidades = aipCidades;
		this.rapServidoresCadastro = rapServidoresCadastro;
		this.maePaciente = maePaciente;
		this.fccCentroCustosCadastro = fccCentroCustosCadastro;
		this.nome = nome;
		this.nomeMae = nomeMae;
		this.dtNascimento = dtNascimento;
		this.dtIdentificacao = dtIdentificacao;
		this.sexoBiologico = sexoBiologico;
		this.fccCentroCustosRecadastro = fccCentroCustosRecadastro;
		this.cor = cor;
		this.sexo = sexo;
		this.grauInstrucao = grauInstrucao;
		this.nomePai = nomePai;
		this.naturalidade = naturalidade;
		this.dddFoneResidencial = dddFoneResidencial;
		this.foneResidencial = foneResidencial;
		this.dddFoneRecado = dddFoneRecado;
		this.foneRecado = foneRecado;
		this.estadoCivil = estadoCivil;
		this.cpf = cpf;
		this.numeroCNH = numeroCNH;
		this.dataValidadeCNH = dataValidadeCNH;
		this.prontuario = prontuario;
		this.dtObito = dtObito;
		this.rg = rg;
		this.orgaoEmisRg = orgaoEmisRg;
		this.observacao = observacao;
		this.prntAtivo = prntAtivo;
		this.cadConfirmado = cadConfirmado;
		this.indGeraProntuario = indGeraProntuario;
		this.dtUltInternacao = dtUltInternacao;
		this.dtUltAlta = dtUltAlta;
		this.dtUltConsulta = dtUltConsulta;
		this.dtUltProcedimento = dtUltProcedimento;
		this.dtUltAtendHospDia = dtUltAtendHospDia;
		this.dtUltAltaHospDia = dtUltAltaHospDia;
		this.quarto = quarto;
		this.unidadeFuncional = unidadeFuncional;
		this.leito = leito;
		this.regNascimento = regNascimento;
		this.nroCartaoSaude = nroCartaoSaude;
		this.dtRecadastro = dtRecadastro;
		this.indPacienteVip = indPacienteVip;
		this.tipoDataObito = tipoDataObito;
		this.dtObitoExterno = dtObitoExterno;
		this.recemNascido = recemNascido;
		this.numeroPis = numeroPis;
		this.volumes = volumes;
		this.indPacProtegido = indPacProtegido;
		this.criadoEm = criadoEm;
		this.indPacAgfa = indPacAgfa;
		this.aipPacientesDadosCns = aipPacientesDadosCns;
		this.aipPacienteDadoClinicos = aipPacienteDadoClinicos;
		this.rapPessoasFisicas = rapPessoasFisicas;
		this.aipAlturaPacienteses = aipAlturaPacienteses;
		this.aipPacienteses = aipPacienteses;
		this.aipPesoPacienteses = aipPesoPacienteses;
		this.mbcCirurgias = mbcCirurgias;
		this.aacConsultas = aacConsultas;
		this.aghAtendimentos = aghAtendimentos;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipPacSq1")
	@Column(name = "CODIGO", updatable = false, nullable = false, precision = 8, scale = 0)
	// @DocumentId
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NAC_CODIGO")
	public AipNacionalidades getAipNacionalidades() {
		return this.aipNacionalidades;
	}

	public void setAipNacionalidades(AipNacionalidades aipNacionalidades) {
		this.aipNacionalidades = aipNacionalidades;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<FatPacienteTransplantes> getPacienteTransplantes() {
		return pacienteTransplantes;
	}

	public void setPacienteTransplantes(Set<FatPacienteTransplantes> pacienteTransplantes) {
		this.pacienteTransplantes = pacienteTransplantes;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<FatListaPacApac> getListaPacApacs() {
		return listaPacApacs;
	}

	public void setListaPacApacs(Set<FatListaPacApac> listaPacApacs) {
		this.listaPacApacs = listaPacApacs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<FatCandidatosApacOtorrino> getListaCandidatosApacOtorrino() {
		return listaCandidatosApacOtorrino;
	}

	public void setListaCandidatosApacOtorrino(Set<FatCandidatosApacOtorrino> listaCandidatosApacOtorrino) {
		this.listaCandidatosApacOtorrino = listaCandidatosApacOtorrino;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_RECADASTRO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_RECADASTRO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresRecadastro() {
		return this.rapServidoresRecadastro;
	}

	public void setRapServidoresRecadastro(RapServidores rapServidoresRecadastro) {
		this.rapServidoresRecadastro = rapServidoresRecadastro;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UF_SIGLA")
	public AipUfs getAipUfs() {
		return this.aipUfs;
	}

	public void setAipUfs(AipUfs aipUfs) {
		this.aipUfs = aipUfs;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCP_CODIGO")
	public AipOcupacoes getAipOcupacoes() {
		return this.aipOcupacoes;
	}

	public void setAipOcupacoes(AipOcupacoes aipOcupacoes) {
		this.aipOcupacoes = aipOcupacoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDD_CODIGO")
	public AipCidades getAipCidades() {
		return this.aipCidades;
	}

	public void setAipCidades(AipCidades aipCidades) {
		this.aipCidades = aipCidades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_CADASTRO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_CADASTRO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresCadastro() {
		return this.rapServidoresCadastro;
	}

	public void setRapServidoresCadastro(RapServidores rapServidoresCadastro) {
		this.rapServidoresCadastro = rapServidoresCadastro;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO_MAE")
	public AipPacientes getMaePaciente() {
		return this.maePaciente;
	}

	public void setMaePaciente(AipPacientes maePaciente) {
		this.maePaciente = maePaciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_CADASTRO", nullable = false)
	public FccCentroCustos getFccCentroCustosCadastro() {
		return this.fccCentroCustosCadastro;
	}

	public void setFccCentroCustosCadastro(
			FccCentroCustos fccCentroCustosCadastro) {
		this.fccCentroCustosCadastro = fccCentroCustosCadastro;
	}

	@Column(name = "NOME", nullable = false, length = 100)
	@Length(max = 100, message = "Nome do paciente pode ter no mÃ¡ximo 100 caracteres.")
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = (nome == null ? null : nome.toUpperCase());
	}
	
	@Column(name = "NOME_MAE", nullable = false, length = 100)
	@Length(max = 100, message = "Nome da mÃ£e do paciente pode ter no mÃ¡ximo 100 caracteres.")
	public String getNomeMae() {
		return this.nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = (nomeMae == null ? null : nomeMae.toUpperCase());
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_NASCIMENTO", nullable = false)
	public Date getDtNascimento() {
		return this.dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_IDENTIFICACAO", nullable = false)
	public Date getDtIdentificacao() {
		return this.dtIdentificacao;
	}

	public void setDtIdentificacao(Date dtIdentificacao) {
		this.dtIdentificacao = dtIdentificacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_RECADASTRO")
	public FccCentroCustos getFccCentroCustosRecadastro() {
		return this.fccCentroCustosRecadastro;
	}

	public void setFccCentroCustosRecadastro(
			FccCentroCustos fccCentroCustosRecadastro) {
		this.fccCentroCustosRecadastro = fccCentroCustosRecadastro;
	}

	@Column(name = "COR", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioCor getCor() {
		return this.cor;
	}

	public void setCor(DominioCor cor) {
		this.cor = cor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ETN_ID", nullable = true)
	public AipEtnia getEtnia() {
		return etnia;
	}
	
	public void setEtnia(AipEtnia etnia) {
		this.etnia = etnia;
	}

	@Column(name = "SEXO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSexo getSexo() {
		return this.sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	@Column(name = "GRAU_INSTRUCAO", precision = 2, scale = 0)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioGrauInstrucao") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioGrauInstrucao getGrauInstrucao() {
		return this.grauInstrucao;
	}

	public void setGrauInstrucao(DominioGrauInstrucao grauInstrucao) {
		this.grauInstrucao = grauInstrucao;
	}

	@Column(name = "NOME_PAI", length = 100)
	@Length(max = 100)
	public String getNomePai() {
		return this.nomePai;
	}

	public void setNomePai(String nomePai) {
		if (!"".equalsIgnoreCase(nomePai)) {
			this.nomePai = (nomePai == null ? null : nomePai.toUpperCase());
		} else {
			this.nomePai = null;
		}
	}

	@Column(name = "NATURALIDADE", length = 25)
	@Length(max = 25)
	public String getNaturalidade() {
		return this.naturalidade;
	}

	public void setNaturalidade(String naturalidade) {
		if (!"".equalsIgnoreCase(naturalidade)) {
			this.naturalidade = naturalidade;
		} else {
			this.naturalidade = null;
		}
	}

	@Column(name = "DDD_FONE_RESIDENCIAL", precision = 4, scale = 0)
	public Short getDddFoneResidencial() {
		return this.dddFoneResidencial;
	}

	public void setDddFoneResidencial(Short dddFoneResidencial) {
		this.dddFoneResidencial = dddFoneResidencial;
	}

	@Column(name = "FONE_RESIDENCIAL", precision = 10, scale = 0)
	@Max(value = 9999999999L, message = "Valor mÃ¡ximo permitido: 999 999 9999")
	public Long getFoneResidencial() {
		return this.foneResidencial;
	}

	public void setFoneResidencial(Long foneResidencial) {
		this.foneResidencial = foneResidencial;
	}

	@Column(name = "DDD_FONE_RECADO", precision = 4, scale = 0)
	public Short getDddFoneRecado() {
		return this.dddFoneRecado;
	}

	public void setDddFoneRecado(Short dddFoneRecado) {
		this.dddFoneRecado = dddFoneRecado;
	}

	@Column(name = "FONE_RECADO", length = 15)
	@Length(max = 15)
	public String getFoneRecado() {
		return this.foneRecado;
	}

	public void setFoneRecado(String foneRecado) {

		if (!"".equalsIgnoreCase(foneRecado)) {
			this.foneRecado = foneRecado;
		} else {
			this.foneRecado = null;
		}
	}

	@Column(name = "ESTADO_CIVIL", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioEstadoCivil getEstadoCivil() {
		return this.estadoCivil;
	}

	public void setEstadoCivil(DominioEstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	@Column(name = "CPF", precision = 11, scale = 0)
	@CPF
	public Long getCpf() {
		return this.cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	
	@Column(name = "CNH", precision = 11, scale = 0)
	public Long getNumeroCNH() {
		return numeroCNH;
	}

	public void setNumeroCNH(Long numeroCNH) {
		this.numeroCNH = numeroCNH;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_VALIDADE_CNH")
	public Date getDataValidadeCNH() {
		return dataValidadeCNH;
	}

	public void setDataValidadeCNH(Date dataValidadeCNH) {

		this.dataValidadeCNH = dataValidadeCNH;

	}

	@Column(name = "PRONTUARIO", unique = true, precision = 8, scale = 0)
	// TODO encontrar contra forma de fazer esta validacao @Prontuario. NAO criar dependencia externa a model. 
	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	@Column(name = "ID_SISTEMA_LEGADO", updatable = true, nullable = true, scale = 0)
	public Long getIdSistemaLegado() {
		return idSistemaLegado;
	}

	public void setIdSistemaLegado(Long idSistemaLegado) {
		this.idSistemaLegado = idSistemaLegado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_OBITO")
	public Date getDtObito() {
		return this.dtObito;
	}

	public void setDtObito(Date dtObito) {
		this.dtObito = dtObito;
	}

	@Column(name = "RG", length = 20)
	public String getRg() {
		return this.rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	@Column(name = "ORGAO_EMIS_RG", length = 10)
	@Length(max = 10)
	public String getOrgaoEmisRg() {
		return this.orgaoEmisRg;
	}

	public void setOrgaoEmisRg(String orgaoEmisRg) {
		if (!"".equalsIgnoreCase(orgaoEmisRg)) {
			this.orgaoEmisRg = orgaoEmisRg;
		} else {
			this.orgaoEmisRg = null;
		}
	}

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		if (!"".equalsIgnoreCase(observacao)) {
			this.observacao = observacao;
		} else {
			this.observacao = null;
		}
	}

	@Column(name = "PRNT_ATIVO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoProntuario getPrntAtivo() {
		return this.prntAtivo;
	}

	public void setPrntAtivo(DominioTipoProntuario prntAtivo) {
		this.prntAtivo = prntAtivo;
	}

	@Column(name = "CAD_CONFIRMADO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getCadConfirmado() {
		return this.cadConfirmado;
	}

	public void setCadConfirmado(DominioSimNao cadConfirmado) {
		this.cadConfirmado = cadConfirmado;
	}

	/**
	 * Se estiver marcado com S indica que um prontuÃ¡rio foi gerado. Com N ou
	 * nulo pode indicar que nÃ£o tem prontuÃ¡rio.
	 * 
	 * @return
	 */
	@Column(name = "IND_GERA_PRONTUARIO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndGeraProntuario() {
		return this.indGeraProntuario;
	}

	public void setIndGeraProntuario(DominioSimNao indGeraProntuario) {
		this.indGeraProntuario = indGeraProntuario;
	}

	/**
	 * Campo sintÃ©tico criado para mapear diretamente este dominio booleano em
	 * um componente selectOneCheckBox
	 * 
	 * @return
	 */
	@Transient
	public boolean isGerarProntuario() {
		if (getIndGeraProntuario() != null) {
			return getIndGeraProntuario().isSim();
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Campo sintÃ©tico criado para informar quando um nÃºmero de prontuÃ¡rio Ã©
	 * superior a 90.000.000 (prontuÃ¡rio virtual)
	 * 
	 * @return
	 */
	@Transient
	public boolean isProntuarioVirtual() {
		if (prontuario != null && prontuario > VALOR_MAXIMO_PRONTUARIO) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}

	}

	public void setGerarProntuario(boolean gerarProntuario) {
		
		setIndGeraProntuario(DominioSimNao.getInstance(gerarProntuario));
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_INTERNACAO")
	public Date getDtUltInternacao() {
		return this.dtUltInternacao;
	}

	public void setDtUltInternacao(Date dtUltInternacao) {
		this.dtUltInternacao = dtUltInternacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_ALTA")
	public Date getDtUltAlta() {
		return this.dtUltAlta;
	}

	public void setDtUltAlta(Date dtUltAlta) {
		this.dtUltAlta = dtUltAlta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_CONSULTA")
	public Date getDtUltConsulta() {
		return this.dtUltConsulta;
	}

	public void setDtUltConsulta(Date dtUltConsulta) {
		this.dtUltConsulta = dtUltConsulta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_PROCEDIMENTO")
	public Date getDtUltProcedimento() {
		return this.dtUltProcedimento;
	}

	public void setDtUltProcedimento(Date dtUltProcedimento) {
		this.dtUltProcedimento = dtUltProcedimento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_ATEND_HOSP_DIA")
	public Date getDtUltAtendHospDia() {
		return this.dtUltAtendHospDia;
	}

	public void setDtUltAtendHospDia(Date dtUltAtendHospDia) {
		this.dtUltAtendHospDia = dtUltAtendHospDia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_ALTA_HOSP_DIA")
	public Date getDtUltAltaHospDia() {
		return this.dtUltAltaHospDia;
	}

	public void setDtUltAltaHospDia(Date dtUltAltaHospDia) {
		this.dtUltAltaHospDia = dtUltAltaHospDia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QRT_NUMERO")
	public AinQuartos getQuarto() {
		return this.quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ")
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LTO_LTO_ID")
	public AinLeitos getLeito() {
		return this.leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	@Column(name = "REG_NASCIMENTO")
	public String getRegNascimento() {
		return this.regNascimento;
	}

	public void setRegNascimento(String regNascimento) {
		this.regNascimento = regNascimento;
	}

	@Column(name = "NRO_CARTAO_SAUDE")
	public BigInteger getNroCartaoSaude() {
		return this.nroCartaoSaude;
	}

	public void setNroCartaoSaude(BigInteger nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_RECADASTRO")
	public Date getDtRecadastro() {
		return this.dtRecadastro;
	}

	public void setDtRecadastro(Date dtRecadastro) {
		this.dtRecadastro = dtRecadastro;
	}

	@Column(name = "IND_PACIENTE_VIP", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndPacienteVip() {
		return this.indPacienteVip;
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings({"unused", "PMD.NPathComplexity"})
	private void validacoes() {

		if (this.indPacienteVip == null) {
			this.indPacienteVip = DominioSimNao.N;
		}

		if (this.indPacAgfa == null) {
			this.indPacAgfa = DominioSimNao.N;
		}

		// CONSTRAINT AIP_PAC_CK15
		if (this.prontuario == null
				&& this.indPacienteVip.equals(DominioSimNao.S)) {
			throw new BaseRuntimeException(
					PacienteExceptionCode.PACIENTE_VIP_DEVE_TER_PRONTUARIO);
		}
		
		// CONSTRAINT AIP_PAC_CK6
		//Essa constraint nÃ£o existe mais no Oracle, foi comentada pq ao passivar prontuario 
		//nÃ£o deve executar essa validaÃ§Ã£o.
//		if (this.prontuario != null
//				&& this.prontuario <= 90000000) {
//			if (this.cor == null) {
//				throw new BaseRuntimeException(PacienteExceptionCode.PACIENTE_COM_PRONTUARIO_SEM_COR);
//			}
//			
//			if (this.sexo == null) {
//				throw new BaseRuntimeException(PacienteExceptionCode.PACIENTE_COM_PRONTUARIO_SEM_SEXO);
//			}
//			
//			if (this.grauInstrucao == null) {
//				throw new BaseRuntimeException(PacienteExceptionCode.PACIENTE_COM_PRONTUARIO_SEM_INSTRUCAO);
//			}
//			
//			if (this.nomePai == null || this.nomePai.isEmpty()) {
//				throw new BaseRuntimeException(PacienteExceptionCode.PACIENTE_COM_PRONTUARIO_SEM_PAI);
//			}
//			
//			if (this.estadoCivil == null) {
//				throw new BaseRuntimeException(PacienteExceptionCode.PACIENTE_COM_PRONTUARIO_SEM_ESTADO_CIVIL);
//			}			
//		}		

		// CONSTRAINT AIP_PAC_CK9
		if (!(this.quarto != null && this.unidadeFuncional == null && this.leito == null)
				&& !(this.quarto == null && this.unidadeFuncional != null && this.leito == null)
				&& !(this.quarto == null && this.unidadeFuncional == null && this.leito != null)
				&& !(this.quarto == null && this.unidadeFuncional == null && this.leito == null)) {
			throw new BaseRuntimeException(
					PacienteExceptionCode.INFO_QUARTO_UNINF_LEITO_INCORRETAS);
		}

		if (!(this.quarto != null && this.unidadeFuncional == null && this.leito == null)
				&& !(this.quarto == null && this.unidadeFuncional != null && this.leito == null)
				&& !(this.quarto == null && this.unidadeFuncional == null && this.leito != null)
				&& !(this.quarto == null && this.unidadeFuncional == null && this.leito == null)) {
			throw new BaseRuntimeException(
					PacienteExceptionCode.INFO_QUARTO_UNINF_LEITO_INCORRETAS);
		}		
	}

	public void setIndPacienteVip(DominioSimNao indPacienteVip) {
		this.indPacienteVip = indPacienteVip;
	}

	@Column(name = "TIPO_DATA_OBITO", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioTipoDataObito getTipoDataObito() {
		return this.tipoDataObito;
	}

	public void setTipoDataObito(DominioTipoDataObito tipoDataObito) {
		this.tipoDataObito = tipoDataObito;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_OBITO_EXTERNO")
	public Date getDtObitoExterno() {
		return this.dtObitoExterno;
	}

	public void setDtObitoExterno(Date dtObitoExterno) {
		this.dtObitoExterno = dtObitoExterno;
	}

	
	@Column(name = "NUMERO_PIS", precision = 15, scale = 0)
	public Long getNumeroPis() {
		return this.numeroPis;
	}

	public void setNumeroPis(Long numeroPis) {
		this.numeroPis = numeroPis;
	}

	@Column(name = "VOLUMES", precision = 3, scale = 0)
	public Short getVolumes() {
		return this.volumes;
	}

	public void setVolumes(Short volumes) {
		this.volumes = volumes;
	}

	@Column(name = "IND_PAC_PROTEGIDO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndPacProtegido() {
		return this.indPacProtegido;
	}

	public void setIndPacProtegido(DominioSimNao indPacProtegido) {
		this.indPacProtegido = indPacProtegido;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_PAC_AGFA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndPacAgfa() {
		return this.indPacAgfa;
	}

	public void setIndPacAgfa(DominioSimNao indPacAgfa) {
		this.indPacAgfa = indPacAgfa;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "aipPaciente")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	@BatchSize(size = 20)
	public AipPacientesDadosCns getAipPacientesDadosCns() {
		return this.aipPacientesDadosCns;
	}

	public void setAipPacientesDadosCns(
			AipPacientesDadosCns aipPacientesDadosCns) {
		this.aipPacientesDadosCns = aipPacientesDadosCns;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPacientes")
	public Set<RapPessoasFisicas> getRapPessoasFisicas() {
		return this.rapPessoasFisicas;
	}

	public void setRapPessoasFisicas(Set<RapPessoasFisicas> rapPessoasFisicas) {
		this.rapPessoasFisicas = rapPessoasFisicas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	public Set<AipAlturaPacientes> getAipAlturaPacienteses() {
		return this.aipAlturaPacienteses;
	}

	public void setAipAlturaPacienteses(
			Set<AipAlturaPacientes> aipAlturaPacienteses) {
		this.aipAlturaPacienteses = aipAlturaPacienteses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "maePaciente")
	public Set<AipPacientes> getAipPacienteses() {
		return this.aipPacienteses;
	}

	public void setAipPacienteses(Set<AipPacientes> aipPacienteses) {
		this.aipPacienteses = aipPacienteses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	public Set<AipPesoPacientes> getAipPesoPacienteses() {
		return this.aipPesoPacienteses;
	}

	public void setAipPesoPacienteses(Set<AipPesoPacientes> aipPesoPacienteses) {
		this.aipPesoPacienteses = aipPesoPacienteses;
	}

	// Manter cascateamento DELETE ORPHAN pois ele influencia no comportamento
	// do merge na persistencia do paciente. TODO Verificar este comportamento
	// posteriormente.
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	@Cascade(org.hibernate.annotations.CascadeType.MERGE)
	public Set<AipEnderecosPacientes> getEnderecos() {
		return this.enderecos;
	}

	public void setEnderecos(Set<AipEnderecosPacientes> enderecos) {
		this.enderecos = enderecos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<AinInternacao> getInternacoes() {
		return this.internacoes;
	}

	public void setInternacoes(Set<AinInternacao> internacoes) {
		this.internacoes = internacoes;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "aipPaciente")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	@BatchSize(size = 20)
	public AipPacienteDadoClinicos getAipPacienteDadoClinicos() {
		return this.aipPacienteDadoClinicos;
	}

	public void setAipPacienteDadoClinicos(
			AipPacienteDadoClinicos aipPacienteDadoClinicos) {
		this.aipPacienteDadoClinicos = aipPacienteDadoClinicos;
	}

	@Column(name = "LTO_LTO_ID", length = 14, updatable = false, insertable = false)
	@Length(max = 14)
	public String getLtoLtoId() {
		return this.ltoLtoId;
	}

	@Column(name = "UNF_SEQ", precision = 4, scale = 0, updatable = false, insertable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	@Column(name = "QRT_NUMERO", precision = 4, scale = 0, updatable = false, insertable = false)
	public Short getQrtNumero() {
		return this.qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	/**
	 * Campo sintÃ©tico criado para calcular e exibir em tela a idade atual do
	 * paciente conforme sua data de nascimento
	 * 
	 * @return
	 */
	@Transient
	public Integer getIdade() {
		// Cria um objeto calendar com a data atual
		Calendar today = Calendar.getInstance();
		return this.getIdade(today.getTime());
	}

	/**
	 * Campo sintético criado para calcular a idade do paciente em uma
	 * determinada data, conforme sua data de nascimento
	 * 
	 * @return
	 */
	@Transient
	public Integer getIdade(Date data) {
		if (this.getDtNascimento() != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(this.getDtNascimento());
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(data);
			// ObtÃ©m a idade baseado no ano
			this.idade = dataCalendario.get(Calendar.YEAR)
					- dataNascimento.get(Calendar.YEAR);
			dataNascimento.add(Calendar.YEAR, this.idade);
			// se a data de hoje Ã© antes da data de Nascimento, entÃ£o diminui
			// 1(um)
			if (dataCalendario.before(dataNascimento)) {
				this.idade--;
			}
		}

		return this.idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	/**
	 * Retorna a diferença entre data corrente a data de nascimento do 
	 * paciente, sob o formato de uma String por extenso como, por exemplo:
	 * 5 anos 1 mês 2 dias
	 * 
	 * ORADB MPMC_IDA_ANO_MES_DIA
	 * 
	 * @return Diferença entre datas por extenso
	 */
	@Transient
	public String getIdadeFormat() {
		String tempo = "anos";
		String idadeFormat = null;
		if (this.getDtNascimento() != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(this.getDtNascimento());
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(new Date());
			
			// Obtém a idade baseado no ano
			Integer idadeNum = dataCalendario.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);

			if (dataCalendario.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
				idadeNum--;
			} else if (dataCalendario.get(Calendar.MONTH) == dataNascimento.get(Calendar.MONTH)
					&& dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
				idadeNum--;
			}

			if (idadeNum == 1) {
				tempo = "ano";
			}
			if (idadeNum < 1) {
				if (dataCalendario.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
					idadeNum = dataCalendario.get(Calendar.MONTH) + (12 - dataNascimento.get(Calendar.MONTH));
				} 
				else if ((dataCalendario.get(Calendar.YEAR) != dataNascimento.get(Calendar.YEAR)) 
						&& (dataCalendario.get(Calendar.MONTH) == dataNascimento.get(Calendar.MONTH)) 
						&& (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH))) { 
					idadeNum = dataCalendario.get(Calendar.MONTH) + (11 - dataNascimento.get(Calendar.MONTH));
				} else {
					idadeNum = dataCalendario.get(Calendar.MONTH) - dataNascimento.get(Calendar.MONTH);
					if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
						idadeNum--;
					}
				}
				
				tempo = "meses";
				if (idadeNum == 1) {
					tempo = "mês";
				}

				if (idadeNum < 1) {
					if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento
							.get(Calendar.DAY_OF_MONTH)) {
						Integer lastDayMonth = dataNascimento
								.getActualMaximum(Calendar.DAY_OF_MONTH);
						idadeNum = (lastDayMonth - dataNascimento
								.get(Calendar.DAY_OF_MONTH))
								+ dataCalendario.get(Calendar.DAY_OF_MONTH);
					} else {
						idadeNum = dataCalendario.get(Calendar.DAY_OF_MONTH)
								- dataNascimento.get(Calendar.DAY_OF_MONTH);
					}

					// Soma 1 para dias(de acordo com ORADB AIPC_IDADE_ANO_MES)
					idadeNum++;
					
					tempo = "dias";
					if (idadeNum == 1) {
						tempo = "dia";
					}
				}
			}
			idadeFormat = idadeNum + " " + tempo;
		}

		return idadeFormat;
		
		/*
		 String tempo = "anos";
		String idadeFormat = null;
		Integer idadeNum = 0;
		Calendar dataCalendario = new GregorianCalendar();
		Calendar dataNascimento = new GregorianCalendar();
		
		dataCalendario.setTime(new Date());
		dataNascimento.setTime(this.getDtNascimento());
		
		if (this.getDtNascimento() != null) {
			idadeNum = dataCalendario.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);
			
			//Se estiver no mesmo ano
			if(idadeNum == 0) {
				//se estiver no mesmo mês
				if (dataCalendario.get(Calendar.MONTH) - dataNascimento.get(Calendar.MONTH) == 0) {
					idadeNum = dataCalendario.get(Calendar.DAY_OF_MONTH) - dataNascimento.get(Calendar.DAY_OF_MONTH);
					tempo = "dias";
				}
				//se os meses forem diferentes
				else {
					idadeNum = dataCalendario.get(Calendar.MONTH) - dataNascimento.get(Calendar.MONTH);
					
					//se tem 1 mês completo
					if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
						idadeNum--;
					}
					
					if (idadeNum == 0) {
						idadeNum = dataCalendario.get(Calendar.DAY_OF_YEAR) - dataNascimento.get(Calendar.DAY_OF_YEAR);
						tempo = "dias";
					}
					else if (idadeNum == 1) {
						tempo = "mês";
					}
					else {
						tempo = "meses";
					}
					
				}
			}
			
			//se anos forem diferentes
			else if (idadeNum == 1) {
				idadeNum = (dataCalendario.get(Calendar.MONTH) + 12) - dataNascimento.get(Calendar.MONTH);
			}
			
		}
		
		idadeFormat = idadeNum + " " + tempo;	
		
		return idadeFormat;
		
		 */
	}
	
	@Transient
	public String getIdadeAnoMesFormat() {
		String tempo = "anos";
		String tempoMes = "meses";
		Integer idadeMes = null;
		String idadeFormat = null;
		if (this.getDtNascimento() != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(this.getDtNascimento());
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(new Date());
			// ObtÃ©m a idade baseado no ano
			Integer idadeNum = dataCalendario.get(Calendar.YEAR)
					- dataNascimento.get(Calendar.YEAR);
			// dataNascimento.add(Calendar.YEAR, idadeNum);

			if (dataCalendario.get(Calendar.MONTH) < dataNascimento
					.get(Calendar.MONTH)) {
				idadeNum--;
			} else if (dataCalendario.get(Calendar.MONTH) == dataNascimento
					.get(Calendar.MONTH)
					&& dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento
							.get(Calendar.DAY_OF_MONTH)) {
				idadeNum--;
			}

			if (idadeNum == 1) {
				tempo = "ano";
			}
			
			if (dataCalendario.get(Calendar.MONTH) < dataNascimento
					.get(Calendar.MONTH)) {
				idadeMes = dataCalendario.get(Calendar.MONTH)
						+ (12 - dataNascimento.get(Calendar.MONTH));
			} else {
				idadeMes = dataCalendario.get(Calendar.MONTH)
						- dataNascimento.get(Calendar.MONTH);
				if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento
						.get(Calendar.DAY_OF_MONTH)) {
					idadeMes--;
				}
			}
			
			if(idadeMes<0){
				if (12+(idadeMes) == 1) {
					tempoMes = 12+(idadeMes) + " mês";
				} else {
					tempoMes = 12+(idadeMes) + " meses";
				}	
			}else{
				if (idadeMes == 1) {
					tempoMes = idadeMes + " mês";
				} else {
					tempoMes = idadeMes + " meses";
				}
			}

			
			idadeFormat = idadeNum + " " + tempo + " " + tempoMes;
		}

		return idadeFormat;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	public Set<AipFonemaPacientes> getFonemas() {
		return this.fonemas;
	}

	public void setFonemas(Set<AipFonemaPacientes> fonemas) {
		this.fonemas = fonemas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	public Set<AipFonemasMaePaciente> getFonemasMae() {
		return this.fonemasMae;
	}

	public void setFonemasMae(Set<AipFonemasMaePaciente> fonemasMae) {
		this.fonemasMae = fonemasMae;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "AIP_PACIENTES_SOLICITACAO_PRON", schema = "AGH", joinColumns = { @JoinColumn(name = "PAC_CODIGO", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "SLP_CODIGO", nullable = false, updatable = false) })
	public Set<AipSolicitacaoProntuarios> getAipSolicitacaoProntuarios() {
		return this.aipSolicitacaoProntuarios;
	}

	public void setAipSolicitacaoProntuarios(
			Set<AipSolicitacaoProntuarios> aipSolicitacaoProntuarios) {
		this.aipSolicitacaoProntuarios = aipSolicitacaoProntuarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<MbcCirurgias> getMbcCirurgias() {
		return this.mbcCirurgias;
	}

	public void setMbcCirurgias(Set<MbcCirurgias> mbcCirurgias) {
		this.mbcCirurgias = mbcCirurgias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<AacConsultas> getAacConsultas() {
		return this.aacConsultas;
	}

	public void setAacConsultas(Set<AacConsultas> aacConsultas) {
		this.aacConsultas = aacConsultas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<MciNotificacaoGmr> getMciNotificacaoGmr() {
		return mciNotificacaoGmr;
	}

	public void setMciNotificacaoGmr(Set<MciNotificacaoGmr> mciNotificacaoGmr) {
		this.mciNotificacaoGmr = mciNotificacaoGmr;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<AghAtendimentos> getAghAtendimentos() {
		return this.aghAtendimentos;
	}

	public void setAghAtendimentos(Set<AghAtendimentos> aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	public Set<AipConveniosSaudePaciente> getAipConveniosSaudePaciente() {
		return aipConveniosSaudePaciente;
	}

	public void setAipConveniosSaudePaciente(
			Set<AipConveniosSaudePaciente> aipConveniosSaudePaciente) {
		this.aipConveniosSaudePaciente = aipConveniosSaudePaciente;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "paciente")
	public Set<AinAtendimentosUrgencia> getAtendimentosUrgencia() {
		return atendimentosUrgencia;
	}

	public void setAtendimentosUrgencia(
			Set<AinAtendimentosUrgencia> atendimentosUrgencia) {
		this.atendimentosUrgencia = atendimentosUrgencia;
	}
	
	@Column(name = "NOME_SOCIAL", length = 50)
	public String getNomeSocial() {
		return this.nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		NOMEMAE("nomeMae"), NOME("nome"), NOMEPAI("nomePai"), CODIGO("codigo"), DATA_NASCIMENTO(
				"dtNascimento"), PRONTUARIO("prontuario"), PRN_ATIVO(
				"prntAtivo"), VOLUMES("volumes"), DATA_IDENTIFICACAO(
				"dtIdentificacao"), SEXO("sexo"), SEXO_BIOLOGICO(
				"sexoBiologico"), DT_ULT_INTERNACAO("dtUltInternacao"), DT_ULT_PROCEDIMENTO(
				"dtUltProcedimento"), CAD_CONFIRMADO("cadConfirmado"), IND_PACIENTE_VIP(
				"indPacienteVip"), IND_PAC_PROTEGIDO("indPacProtegido"), IND_GERA_PRONTUARIO(
				"indGeraProntuario"), IND_PACIENTE_AGFA("indPacAgfa"), SERVIDOR_CADASTRO(
				"rapServidoresCadastro"), SERVIDOR_CADASTRO_MATRICULA(
				"rapServidoresCadastro.id.matricula"), SERVIDOR_CADASTRO_VINCULO(
				"rapServidoresCadastro.id.vinCodigo"), SERVIDOR_RECADASTRO(
				"rapServidoresRecadastro"), SERVIDOR_RECADASTRO_MATRICULA(
				"rapServidoresRecadastro.id.matricula"), SERVIDOR_RECADASTRO_VINCULO(
				"rapServidoresRecadastro.id.vinCodigo"), CENTRO_CUSTO_CADASTRO(
				"fccCentroCustosCadastro"), CENTRO_CUSTO_CADASTRO_CODIGO(
				"fccCentroCustosCadastro.codigo"), CENTRO_CUSTO_RECADASTRO(
				"fccCentroCustosRecadastro"), CENTRO_CUSTO_RECADASTRO_CODIGO(
				"fccCentroCustosRecadastro.codigo"), LEITO("leito"), 
				RECEM_NASCIDO("recemNascido"), FONEMAS(
				"fonemas"), FONEMAS_MAE("fonemasMae"), UNIDADE_FUNCIONAL_SEQ(
				"unidadeFuncional.seq"), CIDADE_CODIGO("aipCidades.codigo"), NACIONALIDADE_CODIGO(
				"aipNacionalidades.codigo"), OCUPACAO_CODIGO(
				"aipOcupacoes.codigo"), UF_SIGLA("aipUfs.sigla"), COR("cor"), GRAU_INSTRUCAO(
				"grauInstrucao"), NATURALIDADE("naturalidade"), DDD_FONE_RESIDENCIAL(
				"dddFoneResidencial"), FONE_RESIDENCIAL("foneResidencial"), DDD_FONE_RECADO(
				"dddFoneRecado"), FONE_RECADO("foneRecado"), ESTADO_CIVIL(
				"estadoCivil"), CPF("cpf"), RG("rg"), ORGAO_EMISSOR_RG(
				"orgaoEmisRg"), OBSERVACAO("observacao"), REG_NASCIMENTO(
				"regNascimento"), NUMERO_CARTAO_SAUDE("nroCartaoSaude"), NUMERO_PIS(
				"numeroPis"), DT_ULT_ALTA("dtUltAlta"), LTO_LTO_ID("ltoLtoId"), UNF_SEQ(
				"unfSeq"), QRT_NUMERO("qrtNumero"), QUARTO("quarto"), CODIGO_MAE(
				"maePaciente.codigo"), DT_OBITO("dtObito"), TIPO_DATA_OBITO(
				"tipoDataObito"), DT_OBITO_EXTERNO("dtObitoExterno"), ATENDIMENTOS("aghAtendimentos"),
				INTERNACOES("internacoes"),
				ENDERECOS("enderecos"), PACIENTE_DADO_CLINICOS("aipPacienteDadoClinicos"), 
				NRO_CARTAO_SAUDE("nroCartaoSaude"),
				RECEM_NASCIDO_GSO_CODIGO_PACIENTE("recemNascido.id.gsoPacCodigo"),
				RECEM_NASCIDO_GSO_SEQUENCE("recemNascido.id.gsoSeqp"),
				AAC_CONSULTAS("aacConsultas"),
				MCI_NOTIFICACAO_GMR("mciNotificacaoGmr"),
				MBC_CIRURGIAS("mbcCirurgias"),
				ETNIA("etnia"),
				NOME_SOCIAL("nomeSocial"),
				ID_SISTEMA_LEGADO("idSistemaLegado"),
				NACIONALIDADE("aipNacionalidades"),
				CIDADE("aipCidades"), OCUPACOES("aipOcupacoes"),
				MAE_PACIENTE("maePaciente"),
				UNIDADE_FUNCIONAL("unidadeFuncional"),
				DT_RECADASTRO("dtRecadastro"),
				AIP_SOLICITACAO_PRONTUARIOS("aipSolicitacaoProntuarios"),
				DT_NASCIMENTO("dtNascimento"), 
				PAC_TRANSPLANTE("pacienteTransplantes"),
				LISTA_PAC_APAC("listaPacApacs"),
				FAT_CANDIDATOS_APAC_OTORRINO("listaCandidatosApacOtorrino"),
				COD_GRUPO_FAMILIAR_PACIENTE("grupoFamiliarPaciente.agfSeq"),
				GRUPO_FAMILIAR_PACIENTE("grupoFamiliarPaciente"),
				TRANSPLANTE_DOADOR("transplanteDoador"),
				TRANSPLANTE_RECEPTOR("transplanteReceptor"),
				AIP_ALTURA_PACIENTESES("aipAlturaPacienteses"),
				MPT_CONTROLE_FRQUENCIA_PACIENTE("mptControleFrequenciaPaciente"),
				COMORBIDADE("comorbidade"),
				RAP_PESSOA_FISICA("rapPessoasFisicas");

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
	 * @return the sexoBiologico
	 */
	@Column(name = "SEXO_BIOLOGICO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSexo getSexoBiologico() {
		return this.sexoBiologico;
	}

	/**
	 * @param sexoBiologico
	 *            the sexoBiologico to set
	 */
	public void setSexoBiologico(DominioSexo sexoBiologico) {
		this.sexoBiologico = sexoBiologico;
	}

	/**
	 * Campo sintÃ©tico criado para mapear diretamente este dominio booleano em
	 * um componente selectOneCheckBox
	 * 
	 * @author jvaranda
	 * @return
	 */
	@Transient
	public boolean isVip() {
		if (this.indPacienteVip != null && this.indPacienteVip.isSim()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Transient
	public void setVip(boolean vip) {
		if (vip) {
			this.indPacienteVip = DominioSimNao.S;
		} else {
			this.indPacienteVip = DominioSimNao.N;
		}
	}

	@Transient
	public boolean isProtegido() {
		if (this.indPacProtegido != null && this.indPacProtegido.isSim()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Transient
	public void setProtegido(boolean protegido) {
		if (protegido) {
			this.indPacProtegido = DominioSimNao.S;
		} else {
			this.indPacProtegido = DominioSimNao.N;
		}
	}
	
	@Transient
	public boolean isApresentarCadConfirmado() {
		if (getCadConfirmado() != null) {
			return getCadConfirmado() == DominioSimNao.S;
		} else {
			return Boolean.FALSE;
		}

	}

	public void setApresentarCadConfirmado(boolean apresentarCadConfirmado) {
		setCadConfirmado(DominioSimNao.getInstance(apresentarCadConfirmado));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.codigo == null) ? 0 : this.codigo.hashCode());
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
		if (!(obj instanceof AipPacientes)) {
			return false;
		}
		AipPacientes other = (AipPacientes) obj;
		if (this.codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!this.codigo.equals(other.getCodigo())) {
			return false;
		}
		return true;
	}

	@Transient
	public Integer getProntuarioEditado() {
		return prontuarioEditado;
	}

	@Transient
	public void setProntuarioEditado(Integer prontuarioEditado) {
		this.prontuarioEditado = prontuarioEditado;
	}
	
	@Transient
	public Boolean getInsereProntuario() {
		return insereProntuario;
	}
	@Transient
	public void setInsereProntuario(Boolean insereProntuario) {	
		this.insereProntuario = insereProntuario;
	}
	@Transient
	public void setInsereProntuarioCadastro(Boolean insereProntuario) {
		if(codigo == null || (codigo != null && prontuario == null) 
				|| (prontuario != null && prontuario > VALOR_MAXIMO_PRONTUARIO)) {
			setGerarProntuario(insereProntuario);
		}
		
		this.insereProntuario = insereProntuario;
	}
	@Transient
	public Boolean getInsereProntuarioCadastro() {
		return insereProntuario;
	}
	@Transient
	public void setInsereProntuarioEdicao(Boolean insereProntuario) {	
		if(codigo == null || (codigo != null && prontuario == null) 
				|| (prontuario != null && prontuario > VALOR_MAXIMO_PRONTUARIO)) {
			this.insereProntuario = insereProntuario;			
		}
		else{
			this.insereProntuario = false;
		}
	}
	
	@Transient
	public AipEnderecosPacientes getEnderecoPadrao(){
		if (enderecos!=null && !enderecos.isEmpty()){
			for (AipEnderecosPacientes e : enderecos){
				if (e.isPadrao()){
					return e;
				}
			}
		}
		return null;		
	}
	
	/**
	 * CÃ³digo Forms AFAP_ULTIMO_PESO
	 * 
	 * @return
	 */
    @Transient
    public BigDecimal getUltimoPeso(){
        if(getAipPesoPacienteses()!= null && !getAipPesoPacienteses().isEmpty()){
            List<AipPesoPacientes> pesos = new ArrayList<AipPesoPacientes>(getAipPesoPacienteses());
            AGHUUtil.ordenarLista(pesos, "id.criadoEm", Boolean.FALSE);
            return pesos.get(0).getPeso();
        }
        return BigDecimal.ZERO;
    }
    
    @Transient
    public String getUltimoPesoFormatado(){
  
    	if(getAipPesoPacienteses()!= null && !getAipPesoPacienteses().isEmpty()){
    		String ultimoPesoComVirgula = getUltimoPeso().setScale(10, RoundingMode.HALF_DOWN).toString().replace(".", ",").replaceAll("(,0+|0+)$" , "");
 	
    		return ultimoPesoComVirgula + " Kg";
    	}
    			
    	return "";
    }
    
    // FIXME - Remover esse mÃ©todo transiente do POJO
    // Os mÃ©todos transientes tem que ser evitados e nÃ£o podem ser
    // criados com dependencia de classes que nÃ£o estejam no pacote das
    // models (br.gov.mec.aghu.model)
    @Transient
    public AghAtendimentos getUltimoAtendimento(){
    	AghAtendimentos ultimoAtendimento = null;
    	for (AghAtendimentos att:aghAtendimentos){
    		if (ultimoAtendimento == null || DateUtil.validaDataMaior(att.getDthrInicio(), ultimoAtendimento.getDthrInicio())){
    			ultimoAtendimento = att;
    		}
    	}
    	return ultimoAtendimento;
    }
    
    /**
	 * 5465 - Ultima Internacao do Paciente
	 * 
	 * @return
	 */
	// FIXME - Remover esse mÃ©todo transiente do POJO
    // Os mÃ©todos transientes tem que ser evitados e nÃ£o podem ser
    // criados com dependencia de classes que nÃ£o estejam no pacote das
    // models (br.gov.mec.aghu.model)
    @Transient
    public AinInternacao getUltimaInternacao(){
  	 if(getInternacoes()!= null && !getInternacoes().isEmpty()){
           List<AinInternacao> internacoes = new ArrayList<AinInternacao>(getInternacoes());
           AGHUUtil.ordenarLista(internacoes, "dthrInternacao", Boolean.FALSE);
           return internacoes.get(0);
       }
       return new AinInternacao();
  }

	// FIXME - Remover esse mÃ©todo transiente do POJO, pois os mesmos devem ser evitados em POJOs.
	// Uma alternativa Ã© colocÃ¡-lo em um VO.
    @Transient
	public String getDsSuggestion() {
		return (prontuario != null ? prontuario + " - ": " ")+(nome != null ? nome : " ");
	}

	public void setDsSuggestion(String dsSuggestion) {
		this.dsSuggestion = dsSuggestion;
	}
	
	@Transient
	public String getCodigoNome() {
		return (codigo != null ? codigo + " - ": " ")+(nome != null ? nome : " ");
	}
	
	@Transient
	public String getNomeTrunc(Long size){
		if(size != null && getNome() != null && getNome().length() > size) {
			return getNome().substring(0,size.intValue()-2) + "...";
		} else {
			return getNome();
		}
	}
	
	@Transient
	public String getCodigoNomeTrunc(Long size){
		return StringUtil.trunc(getCodigoNome(), Boolean.TRUE, size);
	}
	
	@Transient
	public void setGeraProntuarioVirtual(Boolean geraProntuarioVirtual) {
		this.geraProntuarioVirtual = geraProntuarioVirtual;
	}
	
	@Transient
	public Boolean getGeraProntuarioVirtual() {
		return geraProntuarioVirtual;
	}
	
	@Transient
	public Boolean getPacienteNotifGMR() {
		return pacienteNotifGMR;
	}
	
	@Transient
	public void setPacienteNotifGMR(Boolean pacienteNotifGMR) {
		this.pacienteNotifGMR = pacienteNotifGMR;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "RNA_GSO_PAC_CODIGO", referencedColumnName = "GSO_PAC_CODIGO"),
			@JoinColumn(name = "RNA_GSO_SEQP", referencedColumnName = "GSO_SEQP"),
			@JoinColumn(name = "RNA_SEQP", referencedColumnName = "SEQP") })	
	public McoRecemNascidos getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(McoRecemNascidos recemNascido) {
		this.recemNascido = recemNascido;
	}

	@OneToMany(mappedBy = "doador")
	public List<MtxTransplantes> getTransplanteDoador() {
		return transplanteDoador;
	}

	public void setTransplanteDoador(List<MtxTransplantes> transplanteDoador) {
		this.transplanteDoador = transplanteDoador;
	}

	@OneToMany(mappedBy = "receptor")
	public List<MtxTransplantes> getTransplanteReceptor() {
		return transplanteReceptor;
	}

	public void setTransplanteReceptor(List<MtxTransplantes> transplanteReceptor) {
		this.transplanteReceptor = transplanteReceptor;
	}
	
	@OneToMany(mappedBy = "pacCodigo")
	public List<MptControleFrequencia> getMptControleFrequencia() {
		return mptControleFrequenciaPaciente;
	}

	public void setMptControleFrequencia(List<MptControleFrequencia> mptControleFrequenciaPaciente) {
		this.mptControleFrequenciaPaciente = mptControleFrequenciaPaciente;
	}

	@OneToOne(fetch=FetchType.LAZY, mappedBy="paciente")
	public AipGrupoFamiliarPacientes getGrupoFamiliarPaciente() {
		return grupoFamiliarPaciente;
	}

	public void setGrupoFamiliarPaciente(AipGrupoFamiliarPacientes grupoFamiliarPaciente) {
		this.grupoFamiliarPaciente = grupoFamiliarPaciente;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "MTX_COMORBIDADE_PACIENTES", schema = "AGH", joinColumns = { @JoinColumn(name = "PAC_CODIGO", nullable = false, updatable = false) }, 
	inverseJoinColumns = { @JoinColumn(name = "CMB_SEQ", nullable = false, updatable = false) })
	public Set<MtxComorbidade> getComorbidade() {
		return comorbidade;
	}

	public void setComorbidade(Set<MtxComorbidade> comorbidade) {
		this.comorbidade = comorbidade;
	}
}