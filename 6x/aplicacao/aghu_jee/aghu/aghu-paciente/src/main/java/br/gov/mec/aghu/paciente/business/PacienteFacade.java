package br.gov.mec.aghu.paciente.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.bancosangue.dao.AbsComponenteSanguineoDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.compras.contaspagar.vo.ListaPacientesCCIHVO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.vo.AtendimentoPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.FiltroListaPacienteCCIHVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioOcorrenciaPOL;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTipoEnvioProntuario;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosVO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.vo.PacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.internacao.vo.ProntuarioCirurgiaVO;
import br.gov.mec.aghu.internacao.vo.ProntuarioInternacaoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipAvisoAgendamentoCirurgia;
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipControleEscalaCirurgia;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipEndPacientesHist;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemas;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipLocalizaPaciente;
import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.AipPacientesHistJn;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipProntuariosImpressos;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAipPacientesExcluidos;
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipAvisoAgendamentoCirurgiaDAO;
import br.gov.mec.aghu.paciente.dao.AipBairrosDAO;
import br.gov.mec.aghu.paciente.dao.AipCepLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipCidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipControleEscalaCirurgiaDAO;
import br.gov.mec.aghu.paciente.dao.AipConveniosSaudePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipEndPacientesHistDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipEtniaDAO;
import br.gov.mec.aghu.paciente.dao.AipGrupoFamiliarPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipLocalizaPacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipLogProntOnlinesDAO;
import br.gov.mec.aghu.paciente.dao.AipLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipNacionalidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteDadoClinicosDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDadosCnsDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesHistDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesHistJnDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipProntuarioLiberadosDAO;
import br.gov.mec.aghu.paciente.dao.AipRegSanguineosDAO;
import br.gov.mec.aghu.paciente.dao.AipSolicitantesProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipUfsDAO;
import br.gov.mec.aghu.paciente.dao.PacIntdConvDAO;
import br.gov.mec.aghu.paciente.prontuarioonline.business.RelatorioSumarioTransferenciaON;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistoricoPacientePolVO;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuariosVO;
import br.gov.mec.aghu.paciente.vo.AtualizarLocalAtendimentoVO;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.paciente.vo.DesarquivamentoProntuarioVO;
import br.gov.mec.aghu.paciente.vo.InclusaoAtendimentoVO;
import br.gov.mec.aghu.paciente.vo.PacienteZplVo;
import br.gov.mec.aghu.paciente.vo.ReImpressaoEtiquetasVO;
import br.gov.mec.aghu.paciente.vo.RelatorioMovimentacaoVO;
import br.gov.mec.aghu.paciente.vo.RelatorioPacienteVO;
import br.gov.mec.aghu.paciente.vo.VAipSolicitantesVO;
import br.gov.mec.aghu.paciente.vo.ZplVo;
import br.gov.mec.aghu.perinatologia.dao.McoProcReanimacaoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AtendimentoJustificativaUsoMedicamentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesDAO;
import br.gov.mec.aghu.vo.ProcedimentoReanimacaoVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

/**
 * Porta de entrada do módulo de pacientes.
 */
@Stateless
@Modulo(ModuloEnum.PACIENTES)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
public class PacienteFacade extends BaseFacade implements IPacienteFacade {

	@Inject
	private AipProntuarioLiberadosDAO aipProntuarioLiberadosDAO;
	
	@Inject
	private AipPacientesDadosCnsDAO aipPacientesDadosCnsDAO;
	
	@Inject
	private AipGrupoFamiliarPacientesDAO aipGrupoFamiliarPacientesDAO;

	@Inject
	private AipLogradourosDAO aipLogradourosDAO;

	@Inject
	private AipBairrosDAO aipBairrosDAO;
	
	@EJB
	private DesarquivamentoProntuarioON desarquivamentoProntuarioON;

	@EJB
	private RelatorioHistoricoPacienteON relatorioHistoricoPacienteON;

	@EJB
	private MovimentacaoProntuarioRN movimentacaoProntuarioRN;

	@EJB
	private MovimentacaoON movimentacaoON;
	
	@EJB
	private RecemNascidosRN recemNascidosRN;

	@EJB
	private PacienteON pacienteON;

	@EJB
	private EtiquetasON etiquetasON;

	@EJB
	private AtendimentosON atendimentosON;

	@EJB
	private EtiquetasMovimentacaoProntuarioON etiquetasMovimentacaoProntuarioON;

	@EJB
	private MigracaoPacientesON migracaoPacientesON;

	@EJB
	private AtendimentosRN atendimentosRN;

	@EJB
	private PacienteHistoricoON pacienteHistoricoON;

	@EJB
	private PacientesExcluidosON pacientesExcluidosON;

	@EJB
	private ProntuarioRN prontuarioRN;

	@EJB
	private AtendimentoPacienteRN atendimentoPacienteRN;
	
	@Inject
	private MtxTransplantesDAO mtxTransplantesDAO;

	@EJB
	private FichaApacheJournalRN fichaApacheJournalRN;

	@EJB
	private AtendimentoPacienteON atendimentoPacienteON;

	@EJB
	private UnidadeFuncionaisON unidadeFuncionaisON;

	@EJB
	private FonemasPacienteRN fonemasPacienteRN;

	@EJB
	private PacienteRN pacienteRN;

	@EJB
	private PassivarProntuarioSchedulerRN passivarProntuarioSchedulerRN;

	@EJB
	private ProntuarioON prontuarioON;

	@EJB
	private AtendimentoJournalRN atendimentoJournalRN;
	
	@EJB
	private FonemasPacienteReindexacoesRN fonemasPacienteReindexacoesRN;
	
	@EJB
	private ValidaProntuarioON validaProntuarioON;
	
	@EJB
	private RelatorioSumarioTransferenciaON relatorioSumarioTransferenciaON;
	
	@EJB
	private MovimentacaoProntuarioJournalON movimentacaoProntuarioJournalON;
	
	@Inject
	private AipUfsDAO aipUfsDAO;

	@Inject
	private AipAvisoAgendamentoCirurgiaDAO aipAvisoAgendamentoCirurgiaDAO;

	@Inject
	private PacIntdConvDAO pacIntdConvDAO;

	@Inject
	private AipEndPacientesHistDAO aipEndPacientesHistDAO;

	@Inject
	private AipNacionalidadesDAO aipNacionalidadesDAO;

	@Inject
	private AipLogProntOnlinesDAO aipLogProntOnlinesDAO;

	@Inject
	private AipCepLogradourosDAO aipCepLogradourosDAO;

	@Inject
	private AipEnderecosPacientesDAO aipEnderecosPacientesDAO;

	@Inject
	private AipControleEscalaCirurgiaDAO aipControleEscalaCirurgiaDAO;

	@Inject
	private AipPacientesHistJnDAO aipPacientesHistJnDAO;

	@Inject
	private AipSolicitantesProntuarioDAO aipSolicitantesProntuarioDAO;

	@Inject
	private AipCidadesDAO aipCidadesDAO;

	@Inject
	private AipPesoPacientesDAO aipPesoPacientesDAO;

	@Inject
	private AipPacientesDAO aipPacientesDAO;

	@Inject
	private AipPacienteDadoClinicosDAO aipPacienteDadoClinicosDAO;

	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;

	@Inject
	private AipMovimentacaoProntuarioDAO aipMovimentacaoProntuarioDAO;

	@Inject
	private AipConveniosSaudePacienteDAO aipConveniosSaudePacienteDAO;

	@Inject
	private AipAlturaPacientesDAO aipAlturaPacientesDAO;

	@Inject
	private AipEtniaDAO aipEtniaDAO;

	@Inject
	private AipPacientesHistDAO aipPacientesHistDAO;

	@Inject
	private AipLocalizaPacienteDAO aipLocalizaPacienteDAO;
	
	@Inject
	private AipRegSanguineosDAO aipRegSanguineosDAO;

	@Inject
	private McoProcReanimacaoDAO mcoProcReanimacaoDAO;
	
	@Inject
	private AbsComponenteSanguineoDAO absComponenteSanguineoDAO;
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private PesquisarPacienteON pesquisarPacienteON;

	@EJB
	private MovimentacaoProntuarioON movimentacaoProntuarioON;

	private static final long serialVersionUID = -6607935764400536342L;


	@Override
	public List<AipEnderecosPacientes> obterEnderecosPacientes(Integer pacCodigo){
		return aipEnderecosPacientesDAO.obterEnderecosCompletosPaciente(pacCodigo);
	}
	
	
	protected UnidadeFuncionaisON getUnidadeFuncionaisON() {
		return unidadeFuncionaisON;
	}

	protected PacienteON getPacienteON() {
		return pacienteON;
	}

	protected PacienteRN getPacienteRN() {
		return pacienteRN;
	}

	protected ProntuarioON getProntuarioON() {
		return prontuarioON;
	}

	protected ProntuarioRN getProntuarioRN() {
		return prontuarioRN;
	}

	protected MovimentacaoProntuarioRN getMovimentacaoProntuarioRN() {
		return movimentacaoProntuarioRN;
	}

	protected AtendimentosON getAtendimentosON() {
		return atendimentosON;
	}

	protected AtendimentoJournalRN getAtendimentoJournalRN() {
		return atendimentoJournalRN;
	}

	protected AtendimentoPacienteON getAtendimentoPacienteON() {
		return atendimentoPacienteON;
	}

	protected AtendimentoPacienteRN getAtendimentoPacienteRN() {
		return atendimentoPacienteRN;
	}

	protected EtiquetasON getEtiquetasON() {
		return etiquetasON;
	}

	protected EtiquetasMovimentacaoProntuarioON getEtiquetasMovimentacaoProntuarioON() {
		return etiquetasMovimentacaoProntuarioON;
	}

	protected PassivarProntuarioSchedulerRN getPassivarProntuarioSchedulerRN() {
		return passivarProntuarioSchedulerRN;
	}

	protected DesarquivamentoProntuarioON getDesarquivamentoProntuarioON() {
		return desarquivamentoProntuarioON;
	}

	protected FonemasPacienteRN getFonemasPacienteRN() {
		return fonemasPacienteRN;
	}

	protected RecemNascidosRN getRecemNascidosRN() {
		return recemNascidosRN;
	}

	@Override
	public AipPacientes obterPacientePorProntuario(Integer nroProntuario) {
		return getPacienteON().pesquisarPacientePorProntuario(nroProntuario);
	}
	
	@Override
	public AipPacientes obterPacientePorProntuarioLeito(Integer nroProntuario) {
		return getPacienteON().pesquisarPacientePorProntuarioLeito(nroProntuario);
	}
	
	@Override
	public AipNacionalidades obterNacionalidade(Integer codigoNacionalidade) {
		return getAipNacionalidadesDAO().obterPorChavePrimaria(
				codigoNacionalidade);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoPorSeqHospitalDia(
			Integer seqHospitalDia) {
		return getAtendimentosON().obterAtendimentoPorSeqHospitalDia(
				seqHospitalDia);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoPorSeqInternacao(
			Integer seqInternacao) {
		return getAtendimentosON().obterAtendimentoPorSeqInternacao(
				seqInternacao);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoPorSeqAtendimentoUrgencia(
			Integer seqAtendimentoUrgencia) {
		return getAtendimentosON().obterAtendimentoPorSeqAtendimentoUrgencia(
				seqAtendimentoUrgencia);
	}

	@Override
	public void atualizarPacPediatrico(Integer seq, Boolean indPacPediatrico,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		this.getAtendimentosON().atualizarPacPediatrico(seq, indPacPediatrico,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void atualizarAtendimento(AghAtendimentos atendimento,
			AghAtendimentos atendimentoOld, String nomeMicrocomputador, RapServidores servidorLogado,
			final Date dataFimVinculoServidor) throws BaseException {

		this.getAtendimentosRN().atualizarAtendimento(atendimento,
				atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
	}

	@Override
	public Date obterMaxDataCriadoEmPesoPaciente(Integer codigoPaciente) {
		return getPacienteON().obterMaxDataCriadoEmPesoPaciente(codigoPaciente);
	}

	@Override
	public Integer verificarAtendimentoPaciente(Integer altanAtdSeq,
			Integer altanApaSeq, MpmAltaSumario altaSumario)
			throws ApplicationBusinessException {
		return this.getAtendimentoPacienteON().verificarAtendimentoPaciente(
				altanAtdSeq, altanApaSeq, altaSumario);
	}

	@Override
	public Integer recuperarCodigoPaciente(Integer altanAtdSeq)
			throws ApplicationBusinessException {
		return this.getAtendimentosON().recuperarCodigoPaciente(altanAtdSeq);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoPorProntuarioPacienteAtendimento(
			Integer prontuario) {
		return getAtendimentosON()
				.obterAtendimentoPorProntuarioPacienteAtendimento(prontuario);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentosRecemNascidosPorProntuarioMae(
			AghAtendimentos aghAtendimentos) {
		return getAtendimentosON()
				.obterAtendimentosRecemNascidosPorProntuarioMae(aghAtendimentos);
	}

	@Override
	public ConvenioExamesLaudosVO buscarConvenioExamesLaudos(
			Integer seqAtendimentoParam) {
		return getAtendimentosRN().buscarConvenioExamesLaudos(
				seqAtendimentoParam);
	}

	@Override
	public AipPacientes obterPacientePorCodigo(Integer aipPacientesCodigo) {
		return getPacienteON().obterPaciente(aipPacientesCodigo);
	}
	
	@Override
	public AipPacientes pesquisarPacienteComponente(Object numero, String tipo) throws ApplicationBusinessException {
		return getPacienteON().pesquisarPacienteComponente(numero, tipo);
	}
	
	@Override	
	public AipPacientes obterPacientePorCodigo(Integer aipPacientesCodigo, Enum [] innerArgs, Enum [] leftArgs) {
		return getAipPacientesDAO().obterPorChavePrimaria(aipPacientesCodigo, innerArgs, leftArgs);
	}

	@Override
	public AipPacientes obterPaciente(Integer aipPacientesCodigo) {
		return getPacienteON().obterPaciente(aipPacientesCodigo);
	}

	@Override
	public AipPesoPacientes obterPesoPacienteAtual(AipPacientes aipPaciente) {
		return this.getPacienteON().obterPesoPacienteAtual(aipPaciente);
	}

	@Override
	public List<AipPacientes> pesquisarPacientesPorProntuarioOuCodigo(
			String strPesquisa) {
		return this.getPacienteON().pesquisarPacientesPorProntuarioOuCodigo(
				strPesquisa);
	}

	@Override
	public List<AipPacientes> obterPacientesPorProntuarioOuCodigo(
			String strPesquisa) {
		return this.getPacienteON().obterPacientesPorProntuarioOuCodigo(
				strPesquisa);
	}

	@Override
	public void atualizarPacienteAtendimento(
			AinAtendimentosUrgencia atendimentoUrgencia,
			AinInternacao internacao, AhdHospitaisDia hospitalDia,
			AacConsultas consulta, AipPacientes paciente,
			String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean substituirProntuario)
			throws BaseException {

		this.getAtendimentosRN().atualizarPacienteAtendimento(
				atendimentoUrgencia, internacao, hospitalDia, consulta,
				paciente, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
	}

	@Override
	public Boolean verificarAcaoQualificacaoMatricula(String descricao)
			throws ApplicationBusinessException {
		return this.getAtendimentosRN().verificarAcaoQualificacaoMatricula(
				descricao);
	}

	@Override
	public AipPacientes obterAipPacientesPorChavePrimaria(Integer chavePrimaria) {
		return this.getAipPacientesDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public AipPacientes atualizarAipPacientes(AipPacientes aipPacientes,
			boolean flush) {
		AipPacientes retorno = this.getAipPacientesDAO()
				.atualizar(aipPacientes);
		if (flush) {
			this.getAipPacientesDAO().flush();
		}
		return retorno;
	}

	@Override
	public String obterNomePacientePorProntuario(Integer nroProntuario) {
		return this.getAipPacientesDAO().obterNomePacientePorProntuario(
				nroProntuario);
	}

	@Override
	public AipPacienteDadoClinicos obterAipPacienteDadoClinicosPorChavePrimaria(
			Integer chavePrimaria) {
		return this.getAipPacienteDadoClinicosDAO().obterPorChavePrimaria(
				chavePrimaria);
	}

	@Override
	public AipPesoPacientes buscarPrimeiroPesosPacienteOrdenadoCriadoEm(
			Integer pacCodigo) {
		return getAipPesoPacientesDAO()
				.buscarPrimeiroPesosPacienteOrdenadoCriadoEm(pacCodigo);
	}

	@Override
	public InclusaoAtendimentoVO incluirAtendimento(Integer codigoPaciente,
			Date dataHoraInicio, Integer seqHospitalDia, Integer seqInternacao,
			Integer seqAtendimentoUrgencia, Date dataHoraFim, String leitoId,
			Short numeroQuarto, Short seqUnidadeFuncional,
			Short seqEspecialidade, RapServidores servidor,
			Integer codigoClinica, RapServidores digitador,
			Integer dcaBolNumero, Short dcaBolBsaCodigo, Date dcaBolData,
			Integer apeSeq, Integer numeroConsulta,
			Integer seqGradeAgendamenConsultas, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		return getAtendimentosRN()
				.inclusaoAtendimento(codigoPaciente, dataHoraInicio,
						seqHospitalDia, seqInternacao, seqAtendimentoUrgencia,
						dataHoraFim, leitoId, numeroQuarto,
						seqUnidadeFuncional, seqEspecialidade, servidor,
						codigoClinica, digitador, dcaBolNumero,
						dcaBolBsaCodigo, dcaBolData, apeSeq, numeroConsulta,
						seqGradeAgendamenConsultas, nomeMicrocomputador);
	}

	@Override
	public void inserirAtendimento(final AghAtendimentos atendimento,
			String nomeMicrocomputador) throws BaseException {
		this.getAtendimentosRN().inserirAtendimento(atendimento,
				nomeMicrocomputador);
	}

	private AtendimentosRN getAtendimentosRN() {
		return atendimentosRN;
	}

	@Override
	public String buscaUfPaciente(Integer pacCodigo) {
		return getPacienteRN().buscaUfPaciente(pacCodigo);
	}

	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	protected AipPacienteDadoClinicosDAO getAipPacienteDadoClinicosDAO() {
		return aipPacienteDadoClinicosDAO;
	}

	protected AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}

	protected AipEnderecosPacientesDAO getAipEnderecosPacientesDAO() {
		return aipEnderecosPacientesDAO;
	}

	protected AipAlturaPacientes getAipAlturaPacientes() {
		return new AipAlturaPacientes();
	}

	@Override
	public List<AipConveniosSaudePaciente> pesquisarConveniosPaciente(
			AipPacientes paciente) {
		return getPacienteON().pesquisarConveniosPaciente(paciente);
	}

	@Override
	public void gerarMovimentacaoProntuario(Integer conNumero,
			RapServidores servidorLogado)
			throws ApplicationBusinessException {
		this.getMovimentacaoProntuarioRN().aippGeraMvolEvent(conNumero);
	}

	@Override
	public void atualizarMovimentacaoProntuario(
			AipMovimentacaoProntuarios newMovimentacaoProntuario,
			AipMovimentacaoProntuarios oldMovimentacaoProntuario) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getMovimentacaoProntuarioRN().atualizarMovimentacaoProntuario(
				newMovimentacaoProntuario, oldMovimentacaoProntuario, servidorLogado);
	}

	@Override
	public List<AipSolicitantesProntuario> pesquisarSolicitantesProntuarioPorUnidadeFuncional(
			Short unfSeq) {
		return getAipSolicitantesProntuarioDAO()
				.pesquisarSolicitantesProntuarioPorUnidadeFuncional(unfSeq);
	}

	protected AipSolicitantesProntuarioDAO getAipSolicitantesProntuarioDAO() {
		return aipSolicitantesProntuarioDAO;
	}

	protected AipNacionalidadesDAO getAipNacionalidadesDAO() {
		return aipNacionalidadesDAO;
	}

	@Override
	public AipPacientes obterPacienteComAtendimentoPorProntuario(
			Integer nroProntuario) {
		return getAipPacientesDAO().obterPacienteComAtendimentoPorProntuario(
				nroProntuario);
	}

	@Override
	public List<AipPacientes> obterPacienteComAtendimentoPorProntuarioOUCodigo(
			Integer nroProntuario, Integer codigoPaciente,
			List<DominioOrigemAtendimento> origemAtendimentos) {
		return getAipPacientesDAO()
				.obterPacienteComAtendimentoPorProntuarioOUCodigo(
						nroProntuario, codigoPaciente, origemAtendimentos);
	}

	@Override
	public RelatorioPacienteVO obterPacienteComEnderecoPadrao(
			Integer nroProntuario, Long cpf, Integer nroCodigo)
			throws ApplicationBusinessException {
		return this.getPacienteON().obterPacienteComEnderecoPadrao(
				nroProntuario, cpf, nroCodigo);
	}

	@Override
	public Boolean verEnvioPront(Short unfSeq, Integer pacCod, Short espSeq,
			Short grdSeq, Short vUnf17, Short vUnf19, Short vUnfCaps)
			throws ApplicationBusinessException {
		return this.getEtiquetasMovimentacaoProntuarioON().verEnvioPront(
				unfSeq, pacCod, espSeq, grdSeq, vUnf17, vUnf19, vUnfCaps);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','etiquetasMovimentacaoProntuario')}")
	public void atualizaEtiquetasImpressas(List<PacienteZplVo> pacZplVos,
			Boolean isReprint, Date dtReferencia,
			Map<Integer, PacConsultaVo> hashProntPacConsultaVo,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getEtiquetasMovimentacaoProntuarioON().atualizaEtiquetasImpressas(
				pacZplVos, isReprint, dtReferencia, hashProntPacConsultaVo,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','etiquetasMovimentacaoProntuario')}")
	public Map<Integer, PacConsultaVo> obterPacConsultas(Date dtReferencia,
			Boolean isReprint) throws ApplicationBusinessException {
		return this.getEtiquetasMovimentacaoProntuarioON().obterPacConsultas(
				dtReferencia, isReprint);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','etiquetasMovimentacaoProntuario')}")
	public Map<Integer, AipProntuariosImpressos> obterProntImpressos(
			Date dtReferencia) {
		return this.getEtiquetasMovimentacaoProntuarioON().obterProntImpressos(
				dtReferencia);
	}
	
	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','etiquetasMovimentacaoProntuario') and s:hasPermission('relatorioPaciente','etiquetas')}")
	public ZplVo obterDadosEtiquetas(Date dtReferencia, Boolean isReprint,
			Integer turnoDe, Integer turnoAte, String zonaDe, String zonaAte,
			Integer salaDe, Integer salaAte,
			Map<Integer, PacConsultaVo> hashProntPacConsultaVo,
			Map<Integer, AipProntuariosImpressos> hashProntImpressos)
			throws ApplicationBusinessException {
		return this.getEtiquetasMovimentacaoProntuarioON().obterDadosEtiquetas(
				dtReferencia, isReprint, turnoDe, turnoAte, zonaDe, zonaAte,
				salaDe, salaAte, hashProntPacConsultaVo, hashProntImpressos);
	}
	
	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','etiquetas')}")
	public String gerarZpl(Integer prontuario, Short volume, String nome) {
		return this.getEtiquetasON().gerarZpl(prontuario, volume, nome);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','etiquetas')}")
	public List<ReImpressaoEtiquetasVO> pesquisa(List<Integer> prontuarios,
			Date dataInicial, Date dataFinal) {
		return this.getEtiquetasON().pesquisa(prontuarios, dataInicial,
				dataFinal);
	}

	@Override
	public void validaDatas(Date dtInicial, Date dtFinal)
			throws ApplicationBusinessException {
		this.getEtiquetasON().validaDatas(dtInicial, dtFinal);
	}

	@Override
	@Secure("#{s:hasPermission('paciente','alterar')}")
	public void atualizaVolume(Integer codPaciente, Short volume) {
		this.getPacienteON().atualizaVolume(codPaciente, volume);
	}

	@Override
	public void validaDadosRelProntuarioIdent(Date dtInicial, Date dtFinal)
			throws ApplicationBusinessException {
		this.getProntuarioON()
				.validaDadosRelProntuarioIdent(dtInicial, dtFinal);
	}

	@Override
	public List<Object> obterProntuariosIdentificados(Date dtInicial,
			Date dtFinal, Integer codigoAreaConsiderar,
			Integer codigoAreaDesconsiderar, boolean infAdicionais) {
		return this.getProntuarioON().obterProntuariosIdentificados(dtInicial,
				dtFinal, codigoAreaConsiderar, codigoAreaDesconsiderar,
				infAdicionais);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','movimentacao') and s:hasPermission('movimentacaoProntuario','excluir')}")
	public void validaRemocaoMovimentacaoProntuario(
			Integer codigoSolicitacaoProntuario, Integer codigoPaciente)
			throws ApplicationBusinessException {
		this.getMovimentacaoON().validaRemocaoMovimentacaoProntuario(
				codigoSolicitacaoProntuario, codigoPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','movimentacao')}")
	public List<VAipSolicitantesVO> obterViewAipSolicitantes(Short seq) {
		return this.getMovimentacaoON().obterViewAipSolicitantes(seq);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','movimentacao')}")
	public List<RelatorioMovimentacaoVO> obterMovimentacoes(Date dtInicial,
			Date dtFinal, DominioSituacaoMovimentoProntuario csSituacao,
			Boolean csExibirArea, VAipSolicitantesVO vAipSolicitantes,
			Map<Short, VAipSolicitantesVO> hashSeqVAipSolicitantes) {
		return this.getMovimentacaoON().obterMovimentacoes(dtInicial, dtFinal,
				csSituacao, csExibirArea, vAipSolicitantes,
				hashSeqVAipSolicitantes);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','movimentacao')}")
	public List<VAipSolicitantesVO> pesquisarAreaSolicitante(
			List<VAipSolicitantesVO> lista, String filtro) {
		return this.getMovimentacaoON().pesquisarAreaSolicitante(lista, filtro);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','prontuarioExcluido')}")
	public List<VAipPacientesExcluidos> pesquisaPacientesExcluidos(
			Date dataInicial, Date dataFinal) {
		return this.getPacientesExcluidosON().pesquisa(dataInicial, dataFinal);
	}

	@Override
	@Secure("#{s:hasPermission('historicoPaciente','pesquisar')}")
	public List<AipPacientesHist> pesquisaPacientesHistorico(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String nome) throws ApplicationBusinessException {
		return this.getPacienteHistoricoON().pesquisaPacientesHistorico(
				firstResult, maxResults, orderProperty, asc, nome);
	}

	@Override
	public Long pesquisaPacientesHistoricoCount(String nome)
			throws ApplicationBusinessException {
		return this.getPacienteHistoricoON().pesquisaPacientesHistoricoCount(
				nome);
	}

	protected MovimentacaoON getMovimentacaoON() {
		return movimentacaoON;
	}

	protected PacientesExcluidosON getPacientesExcluidosON() {
		return pacientesExcluidosON;
	}

	protected PacienteHistoricoON getPacienteHistoricoON() {
		return pacienteHistoricoON;
	}

	@Override
	public Long pesquisarPorFonemasCount(String nome, String nomeMae,
			Boolean respeitarOrdem, Date dtNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException {
		return this.getPacienteON().pesquisarPorFonemasCount(nome, nomeMae,
				respeitarOrdem, dtNascimento, listaOrigensAtendimentos);
	}

	@Override
	public Long pesquisarPacienteCount(Integer nroProntuario, Integer nroCodigo, Long cpf,  BigInteger nroCartaoSaude)
			throws ApplicationBusinessException {
		return this.getPacienteON().pesquisarPacienteCount(nroProntuario, nroCodigo, cpf, nroCartaoSaude);
	}

	@Override
	public List<AipPacientes> pesquisarPorFonemas(Integer firstResult,
			Integer maxResults, String nome, String nomeMae,
			boolean respeitarOrdem, Date dataNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException {
		return this.getPacienteON().pesquisarPorFonemas(firstResult,
				maxResults, nome, nomeMae, respeitarOrdem, dataNascimento,
				listaOrigensAtendimentos);
	}

	@Override
	@BypassInactiveModule
	public AipPacientes obterPacientePorCodigoOuProntuario(
			Integer nroProntuario, Integer nroCodigo, Long cpf,
			BigInteger... nroCartaoSaude) throws ApplicationBusinessException {
		if(nroCartaoSaude == null || nroCartaoSaude.length == 0){
			return this.getPacienteON().obterPacientePorCodigoOuProntuario(
					nroProntuario , nroCodigo , cpf);
		}else{
			if(nroCartaoSaude != null && nroCartaoSaude.length > 0) {
				return this.getPacienteON().obterPacientePorCodigoOuProntuario(
						nroProntuario, nroCodigo, cpf, nroCartaoSaude[0]);	
			}else{
				return this.getPacienteON().obterPacientePorCodigoOuProntuario(
						nroProntuario, nroCodigo, cpf, nroCartaoSaude[0]);
			}
		}			

	}
	
	@Override
	public List<AipPacientes> obterPacientePorCodigoOuProntuarioFamiliar(
			Integer nroProntuario, Integer nroCodigo, Integer nroProntuarioFamiliar)
			throws ApplicationBusinessException {
		return this.getPacienteON().obterPacientePorCodigoOuProntuarioFamiliar(
				nroProntuario, nroCodigo, nroProntuarioFamiliar);
	}

	@Override
	public AipPacientes pesquisarPacientePorProntuario(Integer nroProntuario) {
		return this.getPacienteON().pesquisarPacientePorProntuario(
				nroProntuario);
	}

	@Override
	public List<AipPacientes> pesquisarPacientesPorListaProntuario(
			Collection<Integer> nroProntuarios)
			throws ApplicationBusinessException {
		return this.getPacienteON().pesquisarPacientesPorListaProntuario(
				nroProntuarios);
	}

	@Override
	public void validaDadosPesquisaPacientes(Integer numeroProntuario,
			Date periodoAltaInicio, Date periodoAltaFim,
			Date periodoConsultaInicio, Date periodoConsultaFim,
			Date periodoCirurgiaInicio, Date periodoCirurgiaFim,
			String nomePaciente, AghEquipes equipeMedica,
			AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException {
		this.getPacienteON().validaDadosPesquisaPacientes(numeroProntuario,
				periodoAltaInicio, periodoAltaFim, periodoConsultaInicio,
				periodoConsultaFim, periodoCirurgiaInicio, periodoCirurgiaFim,
				nomePaciente, equipeMedica, especialidade, servico,
				unidadeFuncional, procedimentoCirurgico, leito);
	}

	@Override
	public Long pesquisaPacientesCount(Integer numeroProntuario,
			Date periodoAltaInicio, Date periodoAltaFim,
			Date periodoConsultaInicio, Date periodoConsultaFim,
			Date periodoCirurgiaInicio, Date periodoCirurgiaFim,
			String nomePaciente, AghEquipes equipeMedica,
			AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException {
		return this.getPacienteON().pesquisaPacientesCount(numeroProntuario,
				periodoAltaInicio, periodoAltaFim, periodoConsultaInicio,
				periodoConsultaFim, periodoCirurgiaInicio, periodoCirurgiaFim,
				nomePaciente, equipeMedica, especialidade, servico,
				unidadeFuncional, procedimentoCirurgico, leito);
	}

	@Override
	public List<AipPacientes> pesquisaPacientes(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer numeroProntuario, Date periodoAltaInicio,
			Date periodoAltaFim, Date periodoConsultaInicio,
			Date periodoConsultaFim, Date periodoCirurgiaInicio,
			Date periodoCirurgiaFim, String nomePaciente,
			AghEquipes equipeMedica, AghEspecialidades especialidade,
			FccCentroCustos servico, AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException {
		return this.getPacienteON().pesquisaPacientes(firstResult, maxResults,
				orderProperty, asc, numeroProntuario, periodoAltaInicio,
				periodoAltaFim, periodoConsultaInicio, periodoConsultaFim,
				periodoCirurgiaInicio, periodoCirurgiaFim, nomePaciente,
				equipeMedica, especialidade, servico, unidadeFuncional,
				procedimentoCirurgico, leito);
	}
	@Override
	public String pesquisarNomeProfissional(Integer matricula, Short vinculo) {
		return this.getPacienteON().pesquisarNomeProfissional(matricula,
				vinculo);
	}

	@Override
	public List<RapServidoresVO> obterNomeProfissionalServidores(
			List<RapServidoresId> servidores) {
		return this.getPacienteON().obterNomeProfissionalServidores(servidores);
	}

	@Override
	@Secure("#{s:hasPermission('prontuario','excluir')}")
	public void excluirProntuario(AipPacientes paciente, String usuarioLogado)
			throws ApplicationBusinessException {
		this.getPacienteON().excluirProntuario(paciente, usuarioLogado);
	}

	@Override
	public List<AipPacientes> pesquisaProntuarioPaciente(Integer firstResult,
			Integer maxResults, Integer codigo, String nome, Integer prontuario) {
		return this.getPacienteON().pesquisaProntuarioPaciente(firstResult,
				maxResults, codigo, nome, prontuario);
	}

	@Override
	public Long pesquisaProntuarioPacienteCount(Integer codigo, String nome,
			Integer prontuario) {
		return this.getPacienteON().pesquisaProntuarioPacienteCount(codigo,
				nome, prontuario);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.paciente.business.IPacienteFacade#passivarProntuario(
	 * java.lang.String, java.util.Date, boolean)
	 */
	@Override
	@Secure("#{s:hasPermission('prontuario','passivar')}")
	public synchronized void passivarProntuario(String secao, Date dthr,
			boolean incluiPassivos, Date ultimaExecucaoPassivarProntuario)
			throws ApplicationBusinessException {
		this.getProntuarioON().passivarProntuario(secao, dthr, incluiPassivos,
				ultimaExecucaoPassivarProntuario);
	}

	 //@Asynchronous
	 //@Begin(flushMode = FlushModeType.MANUAL) 
//	 public void agendarPassivarProntuario(String secao, Date dthr, boolean
//	 incluiPassivos, @Expiration Date dataInicial, String username) throws
//	 ApplicationBusinessException { 
	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void agendarPassivarProntuario(String secao, Date dthr, boolean incluiPassivos, RapServidores servidorLogado) throws ApplicationBusinessException {
		this.getPassivarProntuarioSchedulerRN().agendarPassivarProntuario(secao, dthr, incluiPassivos, servidorLogado); 
	}

	@Override
	public List<AipPacientes> obterPacientesParaPassivarProntuarioScrollableResults(
			String secao, Calendar cDthr, Integer prontuario,
			boolean incluiPassivos) throws ApplicationBusinessException {
		return this.getAipPacientesDAO()
				.obterPacientesParaPassivarProntuarioScrollableResults(secao,
						cDthr, prontuario, incluiPassivos);
	}

	@Override
	public AipPacientes obterPacientePorCodigoEProntuario(
			Integer nroProntuario, Integer nroCodigo, Long cpf)
			throws ApplicationBusinessException {
		return this.getPacienteON().obterPacientePorCodigoEProntuario(
				nroProntuario, nroCodigo, cpf);
	}

	@Override
	@Secure("#{s:hasPermission('prontuario','substituir')}")
	public void substituirProntuario(Integer prontuarioOrigem,
			Integer prontuarioDestino, Date dtIdentificacaoOrigem,
			Date dtIdentificacaoDestino, Integer codigoOrigem,
			Integer codigoDestino, String nomeMicrocomputador,
			RapServidores servidorLogado,
			final Date dataFimVinculoServidor) throws BaseException {
		this.getProntuarioON().substituirProntuario(prontuarioOrigem,
				prontuarioDestino, dtIdentificacaoOrigem,
				dtIdentificacaoDestino, codigoOrigem, codigoDestino,
				nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
	}

	@Override
	public List<AipPacientes> pesquisarSituacaoProntuario(Integer firstResult,
			Integer maxResults, Integer codigo, String nome,
			Integer prontuario, DominioSimNao indPacienteVip,
			DominioSimNao indPacProtegido, Boolean consideraDigito) {
		return this.getPacienteON().pesquisarSituacaoProntuario(firstResult,
				maxResults, codigo, nome, prontuario, indPacienteVip,
				indPacProtegido, consideraDigito);
	}

	@Override
	public Long pesquisarSituacaoProntuarioCount(Integer codigo, String nome,
			Integer prontuario, DominioSimNao indPacienteVip,
			DominioSimNao indPacProtegido, Boolean consideraDigito) {
		return this.getPacienteON().pesquisarSituacaoProntuarioCount(codigo,
				nome, prontuario, indPacienteVip, indPacProtegido,
				consideraDigito);
	}

	@Override
	public AipPacientes obterPacientePorCodigoOuProntuarioSemDigito(
			Integer nroProntuario, Integer nroCodigo)
			throws ApplicationBusinessException {
		return this.getPacienteON()
				.obterPacientePorCodigoOuProntuarioSemDigito(nroProntuario,
						nroCodigo);
	} 

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.paciente.business.IPacienteFacade#obterPacienteHistAnterior
	 * (java.lang.Integer)
	 */
	@Override
	public Object[] obterPacienteHistAnterior(Integer codigo) {
		return this.getAipPacientesHistDAO().obterPacienteHistAnterior(codigo);
	}
	
	protected AipPacientesHistDAO getAipPacientesHistDAO() {
		return aipPacientesHistDAO;
	}

	@Override
	public void inserirPacienteHist(AipPacientesHist pacienteHist, boolean flush) {
		if(pacienteHist.getCodigo() == null){
			this.getAipPacientesHistDAO().persistir(pacienteHist);
		} else {
			this.getAipPacientesHistDAO().merge(pacienteHist);
		}
		if (flush) {
			this.getAipPacientesHistDAO().flush();
		}
	}

	@Override
	public void removerPacienteHist(AipPacientesHist pacienteHist, boolean flush) {
		this.getAipPacientesHistDAO().remover(pacienteHist);
		if (flush) {
			this.getAipPacientesHistDAO().flush();
		}
	}

	@Override
	public void atualizarPacienteHist(AipPacientesHist pacienteHist,
			boolean flush) {
		this.getAipPacientesHistDAO().atualizar(pacienteHist);
		if (flush) {
			this.getAipPacientesHistDAO().flush();
		}
	}

	@Override
	public void inserirAipPacientesHistJn(
			AipPacientesHistJn aipPacientesHistJn, boolean flush) {
		this.getAipPacientesHistJnDAO().persistir(aipPacientesHistJn);
		if (flush) {
			this.getAipPacientesHistJnDAO().flush();
		}
	}

	@Override
	public List<AipEndPacientesHist> pesquisarHistoricoEnderecoPaciente(
			Integer codigo) {
		return this.getAipEndPacientesHistDAO()
				.pesquisarHistoricoEnderecoPaciente(codigo);
	}

	@Override
	public void inserirEnderecoPacienteHist(
			AipEndPacientesHist endPacienteHist, boolean flush) {
		this.getAipEndPacientesHistDAO().persistir(endPacienteHist);
		if (flush) {
			this.getAipEndPacientesHistDAO().flush();
		}
	}

	@Override
	public void removerEnderecoPacienteHist(
			AipEndPacientesHist endPacienteHist, boolean flush) {
		this.getAipEndPacientesHistDAO().remover(endPacienteHist);
		if (flush) {
			this.getAipEndPacientesHistDAO().flush();
		}
	}

	@Override
	public List<AipSolicitacaoProntuarios> pesquisarSolicitacaoProntuario(
			Integer firstResult, Integer maxResults, String solicitante,
			String responsavel, String observacao) {
		return this.getPacienteON().pesquisarSolicitacaoProntuario(firstResult,
				maxResults, solicitante, responsavel, observacao);
	}

	@Override
	public Long pesquisarSolicitacaoProntuarioCount(String solicitante,
			String responsavel, String observacao) {
		return this.getPacienteON().pesquisarSolicitacaoProntuarioCount(
				solicitante, responsavel, observacao);
	}

	@Override
	public AipSolicitacaoProntuarios obterSolicitacaoProntuario(Integer codigo) {
		return this.getPacienteON().obterSolicitacaoProntuario(codigo);
	}

	@Override
	public List<AipPacientes> pesquisarPacientesPorProntuario(String strPesquisa) {
		return this.getPacienteON()
				.pesquisarPacientesPorProntuario(strPesquisa);
	}

	@Override
	public List<AipPacientes> pesquisarPacientesComProntuarioPorProntuarioOuCodigo(
			String strPesquisa) {
		return this.getPacienteON()
				.pesquisarPacientesComProntuarioPorProntuarioOuCodigo(
						strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','desarquivamentoProntuario')}")
	public List<DesarquivamentoProntuarioVO> pesquisaDesarquivamentoProntuario(
			Integer slpCodigo, Date dataMovimento) {
		return this.getDesarquivamentoProntuarioON().pesquisa(slpCodigo,
				dataMovimento);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','desarquivamentoProntuario')}")
	public List<AipMovimentacaoProntuarios> pesquisarDesarquivamentoProntuarios(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.getDesarquivamentoProntuarioON()
				.pesquisarDesarquivamentoProntuarios(firstResult, maxResult,
						orderProperty, asc);
	}

	@Override
	public Long obterCountPesquisaDesarquivamentoProntuarios() {
		return this.getDesarquivamentoProntuarioON()
				.obterCountPesquisaDesarquivamentoProntuarios();
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','desarquivamentoProntuario')}")
	public List<ProntuarioInternacaoVO> pesquisarAvisoInternacaoSAMES() {
		return this.getDesarquivamentoProntuarioON().pesquisarAvisoInternacaoSAMES();
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','desarquivamentoProntuario')}")
	public Long pesquisarAvisoInternacaoSAMESCount() {
		return this.getDesarquivamentoProntuarioON().pesquisarAvisoInternacaoSAMESCount();
	}
	
	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','desarquivamentoProntuario')}")
	public void buscarProntuariosInternacaoDesarquivamentoProntuario(
			List<ProntuarioInternacaoVO> listaProntuariosSelecionados,
			String nomeMicrocomputador, final Date dataFimVinculoServidor) {
		this.getDesarquivamentoProntuarioON().buscarProntuariosInternacao(
				listaProntuariosSelecionados, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','desarquivamentoProntuario')}")
	public AipSolicitacaoProntuarios buscarProntuariosDesarquivamentoProntuario(
			List<AipMovimentacaoProntuarios> movimentacoes)
			throws ApplicationBusinessException {
		return this.getDesarquivamentoProntuarioON().buscarProntuarios(
				movimentacoes);
	}
	@Override
	public Integer gerarAtendimentoPaciente(Integer atdSeq)
			throws ApplicationBusinessException {
		return this.getAtendimentoPacienteRN().gerarAtendimentoPaciente(atdSeq);
	}

	@Override
	public void gerarJournalFichaApache(
			DominioOperacoesJournal operacaoJournal,
			MpmFichaApache fichaApache, MpmFichaApache fichaApacheOld)
			throws ApplicationBusinessException {
		this.getFichaApacheJournalRN().gerarJournalFichaApache(operacaoJournal,
				fichaApache, fichaApacheOld);
	}

	@Override
	@Secure("#{s:hasPermission('atendimento','alterar')}")
	public void atualizarProntuarioNoAtendimento(Integer codigoPaciente,
			Integer prontuario, String nomeMicrocomputador,
			final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {

		this.getAtendimentosON().atualizarProntuarioNoAtendimento(
				codigoPaciente, prontuario, nomeMicrocomputador,
				dataFimVinculoServidor);
	}
	
	@Override
	public List<AipPacientes> pesquisarPorFonemas(PesquisaFoneticaPaciente parametrosPequisa) throws ApplicationBusinessException {
		return this.getPacienteON().pesquisarPorFonemas(parametrosPequisa);
	}
	
	@Override
	public Long pesquisarPorFonemasCount(PesquisaFoneticaPaciente paramPesquisa) throws ApplicationBusinessException {
		return this.getPacienteON().pesquisarPorFonemasCount(paramPesquisa);
	}
	
	@Override
	public List<AipFonemaPacientes> pesquisarFonemasPaciente(
			Integer codigoPaciente) {
		return this.getFonemasPacienteRN().pesquisarFonemasPaciente(
				codigoPaciente);
	}

	@Override
	public AipFonemas obterAipFonemaPorFonema(String fonema) {
		return this.getFonemasPacienteRN().obterAipFonemaPorFonema(fonema);
	}

	@Override
	public AipFonemaPacientes obterAipFonemaPaciente(Integer codigoPaciente,
			AipFonemas aipFonema) {
		return this.getFonemasPacienteRN().obterAipFonemaPaciente(
				codigoPaciente, aipFonema);
	}

	@Override
	public void removerAipFonemaPaciente(AipFonemaPacientes aipFonemaPaciente) {
		this.getFonemasPacienteRN().removerAipFonemaPaciente(aipFonemaPaciente);
	}

	@Override
	public void removerAipFonema(AipFonemas aipFonema) {
		this.getFonemasPacienteRN().removerAipFonema(aipFonema);
	}

	@Override
	public void persistirAipFonema(AipFonemas aipFonema) {
		this.getFonemasPacienteRN().persistirAipFonema(aipFonema);
	}

	@Override
	public List<AipFonemasMaePaciente> pesquisarFonemasMaePaciente(
			Integer codigoPaciente) {
		return this.getFonemasPacienteRN().pesquisarFonemasMaePaciente(
				codigoPaciente);
	}

	@Override
	public AipFonemasMaePaciente obterAipFonemaMaePaciente(
			Integer codigoPaciente, AipFonemas aipFonema) {
		return this.getFonemasPacienteRN().obterAipFonemaMaePaciente(
				codigoPaciente, aipFonema);
	}

	@Override
	public void removerAipFonemaMaePaciente(
			AipFonemasMaePaciente aipFonemaMaePaciente) {
		this.getFonemasPacienteRN().removerAipFonemaMaePaciente(
				aipFonemaMaePaciente);
	}

	@Override
	public List<String> obterFonemasNaOrdem(String nome)
			throws ApplicationBusinessException {
		return this.getFonemasPacienteRN().obterFonemasNaOrdem(nome);
	}

	@Override
	public Date obterDataNascimentoRecemNascidos(Integer codigoPaciente) {
		return this.getRecemNascidosRN().obterDataNascimentoRecemNascidos(
				codigoPaciente);
	}

	@Override
	public McoRecemNascidos obterRecemNascidosPorCodigo(Integer codigoPaciente) {
		return this.getRecemNascidosRN().obterRecemNascidosPorCodigo(
				codigoPaciente);
	}

	@Override
	public void persistirRecemNascido(McoRecemNascidos recemNascido) {
		this.getRecemNascidosRN().persistirRecemNascido(recemNascido);
	}

	@Override
	@Secure("#{s:hasPermission('prontuario','alterar')}")
	public String verificarLiberacaoProntuario(Integer prontuario)
			throws ApplicationBusinessException {
		return this.getProntuarioON().verificarLiberacaoProntuario(prontuario);
	}

	// /**
	// * @deprecated usar {@link AghuFacade#getMatricula()}
	// * @return
	// */
	// public Integer getMatricula() {
	// return this.getAghuFacade().getMatricula();
	// }

	// /**
	// * @deprecated usar {@link AghuFacade#getVinculo()}
	// * @return
	// */
	// public Short getVinculo() {
	// return this.getAghuFacade().getVinculo();
	// }

	// /**
	// * @deprecated {@link AghuFacade#getCcustAtuacao()}
	// * @return
	// */
	// public Integer getCcustAtuacao() {
	// return this.getAghuFacade().getCcustAtuacao();
	// }

	protected AipPacientesHistJnDAO getAipPacientesHistJnDAO() {
		return aipPacientesHistJnDAO;
	}

	protected AipEndPacientesHistDAO getAipEndPacientesHistDAO() {
		return aipEndPacientesHistDAO;
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(
			Object objPesquisa, boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos, Object[] caracteristicas) {
		return getUnidadeFuncionaisON()
				.pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(
						objPesquisa, ordernarPorCodigoAlaDescricao,
						apenasAtivos, caracteristicas);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(
			Object objPesquisa, boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos, Object[] caracteristicas) {
		return getUnidadeFuncionaisON()
				.pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(
						objPesquisa, ordernarPorCodigoAlaDescricao,
						apenasAtivos, caracteristicas);
	}

	protected FichaApacheJournalRN getFichaApacheJournalRN() {
		return fichaApacheJournalRN;
	}

	@Override
	public List<AipPacientes> pesquisaPacientesPorSexoDataObito(
			Date dtInicialReferencia, Date dtFinalReferencia, DominioSexo sexo) {
		return getAipPacientesDAO().pesquisaPacientesPorSexoDataObito(
				dtInicialReferencia, dtFinalReferencia, sexo);
	}

	@Override
	public Short obterSeqEnderecoPadraoPaciente(Integer pacCodigo) {
		return getAipEnderecosPacientesDAO().obterSeqEnderecoPadraoPaciente(
				pacCodigo);
	}

	@Override
	public Short obterSeqEnderecoResidencialPaciente(Integer pacCodigo) {
		return getAipEnderecosPacientesDAO()
				.obterSeqEnderecoResidencialPaciente(pacCodigo);
	}

	@Override
	public String pesquisarCidadeCursor1(Integer pacCodigo, Short seqp) {
		return getAipEnderecosPacientesDAO().pesquisarCidadeCursor1(pacCodigo,
				seqp);
	}

	@Override
	public String pesquisarCidadeCursor2(Integer pacCodigo, Short seqp) {
		return getAipEnderecosPacientesDAO().pesquisarCidadeCursor2(pacCodigo,
				seqp);
	}

	@Override
	public String pesquisarCidadeCursor3(Integer pacCodigo, Short seqp) {
		return getAipEnderecosPacientesDAO().pesquisarCidadeCursor3(pacCodigo,
				seqp);
	}
	
	@Override
	public Long obterQuantidadeEnderecosPaciente(final AipPacientes pac){
		return aipEnderecosPacientesDAO.buscaQuantidadeEnderecosPaciente(pac);
	}

	@Override
	public void persistirAtendimento(AghAtendimentos atendimento,
			AghAtendimentos atendimentoOld, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		this.getAtendimentosON().persistirAtendimento(atendimento,
				atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public AghAtendimentos clonarAtendimento(AghAtendimentos atendimento)
			throws ApplicationBusinessException {
		return this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	@Secure("#{s:hasPermission('paciente','reindexarFonemas')}")
	public void reindexarPacientes() throws BaseException {
		this.getFonemasPacienteReindexacoesRN().reindexarPosicaoFonemas();
	}

	@Override
	public void setTimeout(Integer timeout) throws ApplicationBusinessException {
		// UserTransaction userTx = this.getUserTransaction();
		// try {
		// EntityManager em = this.getEntityManager();
		// if (userTx.isNoTransaction() || !userTx.isActive()) {
		// userTx.begin();
		// }
		// if (timeout != null) {
		// userTx.setTransactionTimeout(timeout);
		// }
		// em.joinTransaction();
		// } catch (Exception e) {
		// logError(e.getMessage(), e);
		// }
	}

	@Override
	public void sessionClear() {
		this.clear();
	}

	@Override
	public void commit(Integer timeout) throws ApplicationBusinessException {
		// UserTransaction userTx = this.getUserTransaction();
		//
		// try {
		// if (userTx.isNoTransaction() || !userTx.isActive()) {
		// userTx.begin();
		// }
		// EntityManager em = this.getEntityManager();
		// em.joinTransaction();
		// em.flush();
		// userTx.commit();
		// if (timeout != null) {
		// userTx.setTransactionTimeout(timeout);
		// }
		// if (userTx.isNoTransaction() || !userTx.isActive()) {
		// userTx.begin();
		// }
		// em.joinTransaction();
		// } catch (Exception e) {
		// logError(e.getMessage(), e);
		// PacienteFacadeExceptionCode.ERRO_AO_CONFIRMAR_TRANSACAO
		// .throwException();
		// }
	}

	@Override
	public void commit() throws ApplicationBusinessException {
		this.commit(null);
	}

	@Override
	@Secure("#{s:hasPermission('relatorioPaciente','historicoPaciente')}")
	public List<HistoricoPacientePolVO> pesquisaHistoricoPaciente(
			Integer prontuario) throws ApplicationBusinessException {
		return this.getRelatorioHistoricoPacienteON().pesquisa(prontuario);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(
			Object filtro, Object[] caracteristicas) {
		return this
				.getUnidadeFuncionaisON()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(
						filtro, caracteristicas);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricao(
			String filtro) {
		return this.getUnidadeFuncionaisON()
				.pesquisarUnidadesFuncionaisPorCodigoDescricao(filtro);
	}

	@Override
	public void removerAtendimento(AghAtendimentos atendimentoOld,
			String nomeMicrocomputador) throws BaseException {
		this.getAtendimentosRN().removerAtendimento(atendimentoOld,
				nomeMicrocomputador);
	}

	@Override
	public void atualizarFimAtendimento(AinInternacao internacao,
			AhdHospitaisDia hospitalDia,
			AinAtendimentosUrgencia atendimentoUrgencia, Date dthrSaida,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		this.getAtendimentosRN().atualizarFimAtendimento(internacao,
				hospitalDia, atendimentoUrgencia, dthrSaida,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorConsultaEOrigem(
			DominioOrigemAtendimento dominioOrigemAtendimento,
			Integer consultaNumero) {
		return this.getAtendimentosON().listarAtendimentosPorConsultaEOrigem(
				dominioOrigemAtendimento, consultaNumero);
	}

	@Override
	@Secure("#{s:hasPermission('atendimento','alterar')}")
	public InclusaoAtendimentoVO inclusaoAtendimento(Integer codigoPaciente,
			Date dataHoraInicio, Integer seqHospitalDia, Integer seqInternacao,
			Integer seqAtendimentoUrgencia, Date dataHoraFim, String leitoId,
			Short numeroQuarto, Short seqUnidadeFuncional,
			Short seqEspecialidade, RapServidores servidor,
			Integer codigoClinica, RapServidores digitador,
			Integer dcaBolNumero, Short dcaBolBsaCodigo, Date dcaBolData,
			Integer apeSeq, Integer numeroConsulta,
			Integer seqGradeAgendamenConsultas, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		return this.getAtendimentosON()
				.inclusaoAtendimento(codigoPaciente, dataHoraInicio,
						seqHospitalDia, seqInternacao, seqAtendimentoUrgencia,
						dataHoraFim, leitoId, numeroQuarto,
						seqUnidadeFuncional, seqEspecialidade, servidor,
						codigoClinica, digitador, dcaBolNumero,
						dcaBolBsaCodigo, dcaBolData, apeSeq, numeroConsulta,
						seqGradeAgendamenConsultas, nomeMicrocomputador);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoNacimentoEmAndamento(
			AipPacientes paciente) {
		return this.getAtendimentosON().obterAtendimentoNacimentoEmAndamento(
				paciente);
	}

	@Override
	@Secure("#{s:hasPermission('atendimento','alterar')}")
	public void atualizarEspecialidadeNoAtendimento(Integer seqInternacao,
			Integer seqHospitalDia, Short seqEspecialidade,
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		this.getAtendimentosON().atualizarEspecialidadeNoAtendimento(
				seqInternacao, seqHospitalDia, seqEspecialidade,
				nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	@Secure("#{s:hasPermission('atendimento','alterar')}")
	public AtualizarLocalAtendimentoVO atualizarLocalAtendimento(
			Integer seqInternacao, Integer seqHospitalDia,
			Integer seqAtendimentoUrgencia, String leitoId, Short numeroQuarto,
			Short seqUnidadeFuncional, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		return this.getAtendimentosON().atualizarLocalAtendimento(
				seqInternacao, seqHospitalDia, seqAtendimentoUrgencia, leitoId,
				numeroQuarto, seqUnidadeFuncional, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	@Override
	@Secure("#{s:hasPermission('atendimento','alterar')}")
	public void atualizarProfissionalResponsavelPeloAtendimento(
			Integer seqInternacao, Integer seqHospitalDia,
			RapServidores servidor, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		this.getAtendimentosON()
				.atualizarProfissionalResponsavelPeloAtendimento(seqInternacao,
						seqHospitalDia, servidor, nomeMicrocomputador,
						dataFimVinculoServidor);
	}
	
	
	@Override
	public AghAtendimentos obterAtendimento(Integer atdSeq){
		return this.getAghAtendimentoDAO().obterDetalhadoPorChavePrimaria(atdSeq);
	}

	public AghAtendimentoDAO getAghAtendimentoDAO(){
		return this.aghAtendimentoDAO;
	}
	
	@Override
	public String pesquisarNomePaciente(Integer prontuario) {
		return getPacienteON().pesquisarNomePaciente(prontuario);
	}

	@Override
	public <T> Boolean modificados(T newValue, T oldValue) {
		return this.getProntuarioRN().modificados(newValue, oldValue);
	}

	@Override
	public void aippSubstProntConv(Integer prontuarioOrigem,
			Integer prontuarioDestino) throws ApplicationBusinessException {
		this.getProntuarioRN().aippSubstProntConv(prontuarioOrigem,
				prontuarioDestino);
	}

	@Override
	public List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPorPacienteEDataConsulta(
			Integer pacCodigo, Date dtConsulta) {
		return getAipMovimentacaoProntuarioDAO()
				.pesquisarMovimentacaoPorPacienteEDataConsulta(pacCodigo,
						dtConsulta);
	}

	protected AipMovimentacaoProntuarioDAO getAipMovimentacaoProntuarioDAO() {
		return aipMovimentacaoProntuarioDAO;
	}

	protected RelatorioHistoricoPacienteON getRelatorioHistoricoPacienteON() {
		return relatorioHistoricoPacienteON;
	}

	// /**
	// * @deprecated usar {@link
	// CadastroPacienteFacade#inserirPaciente(AipPacientes)}
	// * @param paciente
	// * @throws ApplicationBusinessException
	// */
	// public void inserirPaciente(AipPacientes paciente) throws
	// ApplicationBusinessException{
	// this.getCadastroPacienteFacade().inserirPaciente(paciente);
	// }

	// /**
	// * @deprecated usar {@link
	// CadastroPacienteFacade#verificarCamposInclusaoEndereco(Integer, String,
	// String, Integer, Integer)}
	// * @param cloCep
	// * @param logradouro
	// * @param cidade
	// * @param codigoCidade
	// * @param pacCodigo
	// * @throws ApplicationBusinessException
	// */
	// public void verificarCamposInclusaoEndereco(Integer cloCep, String
	// logradouro, String cidade, Integer codigoCidade, Integer pacCodigo)
	// throws ApplicationBusinessException{
	// this.getCadastroPacienteFacade().verificarCamposInclusaoEndereco(cloCep,
	// logradouro, cidade, codigoCidade, pacCodigo);
	// }

	protected PacIntdConvDAO getPacIntdConvDAO() {
		return pacIntdConvDAO;
	}

	@Override
	public List<PacIntdConv> pesquisarPorCodPrntDataInt(Integer codPrnt,
			Date dataInt) {
		return getPacIntdConvDAO().pesquisarPorCodPrntDataInt(codPrnt, dataInt);
	}

	@Override
	public void removerPacIntdConv(PacIntdConv pacIntdConv, boolean flush) {
		getPacIntdConvDAO().remover(pacIntdConv);
		if (flush) {
			getPacIntdConvDAO().flush();
		}
	}

	@Override
	public void atualizarPacIntdConv(PacIntdConv pacIntdConv, boolean flush) {
		getPacIntdConvDAO().atualizar(pacIntdConv);
		if (flush) {
			getPacIntdConvDAO().flush();
		}
	}

	@Override
	public List<PacIntdConv> obterListaPacIntdConvPorAtendimento(Integer atdSeq) {
		return getPacIntdConvDAO().obterListaPacIntdConvPorAtendimento(atdSeq);
	}

	@Override
	public DominioSexo obterSexoPaciente(Integer pacCodigo) {
		return this.getAipPacientesDAO().obterSexoPaciente(pacCodigo);
	}

	@Override
	public List<AipConveniosSaudePaciente> pesquisarAtivosPorPacienteCodigoSeqConvenio(
			Integer codigoPaciente, Short codigoConvenioSaudePlano,
			Byte seqConvenioSaudePlano) {
		return getAipConveniosSaudePacienteDAO()
				.pesquisarAtivosPorPacienteCodigoSeqConvenio(codigoPaciente,
						codigoConvenioSaudePlano, seqConvenioSaudePlano);
	}

	protected AipConveniosSaudePacienteDAO getAipConveniosSaudePacienteDAO() {
		return aipConveniosSaudePacienteDAO;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public File refonetizarPacientes(Boolean somentePacientesSemFonemas) {
		return this.getFonemasPacienteReindexacoesRN().refonetizarPacientes(somentePacientesSemFonemas);
	}

	@Override
	public Boolean isUnidadeFuncionalBombaInfusao(Short seq) {
		return seq == null ? false
				: !(this.pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(
						seq,
						new Object[] { ConstanteAghCaractUnidFuncionais.PERMITE_PRESCRICAO_BI })
						.isEmpty());
	}

	@Override
	public List<AipPacientes> pesquisarPacientePorNomeDtNacimentoComHoraNomeMae(
			String nome, Date dataNacimento, String nomeMae) {
		return this.getAipPacientesDAO()
				.pesquisarPacientePorNomeDtNacimentoComHoraNomeMae(nome,
						dataNacimento, nomeMae);
	}

	@Override
	public List<AipPacientes> pesquisarPacientePorNomeENumeroCartaoSaude(
			String nome, BigInteger numCartaoSaude) {
		return this.getAipPacientesDAO()
				.pesquisarPacientePorNomeENumeroCartaoSaude(nome,
						numCartaoSaude);
	}

	@Override
	public String gerarEtiquetaPulseira(AipPacientes paciente, Integer atdSeq)
			throws ApplicationBusinessException {
		return getEtiquetasON().gerarEtiquetaPulseira(paciente, atdSeq);
	}

	@Override
	public AipPacientes buscaPaciente(final Integer pacCodigo) {
		return this.getAipPacientesDAO().obterNomePacientePorCodigo(pacCodigo);
	}

	@Override
    public Short obterSeqUnidadeFuncionalPaciente(Integer pacCodigo) {
        return this.getAipPacientesDAO().obterSeqUnidadeFuncionalPaciente(pacCodigo);
    }

	@Override
	public AipPesoPacientes obterPesoPaciente(Integer codPaciente,
			DominioMomento dominioMomento) {
		return getAipPesoPacientesDAO().obterPesoPaciente(codPaciente,
				dominioMomento);
	}

	@Override
	public List<AipCepLogradouros> listarCepLogradourosPorCEP(Integer cep) {
		return this.getAipCepLogradourosDAO().listarCepLogradourosPorCEP(cep);
	}

	@Override
	public List<AipEtnia> obterTodasEtnias() {
		return this.getAipEtniaDAO().obterTodasEtnias();
	}

	@Override
	public AipEtnia obterAipEtniaPorChavePrimaria(Integer id) {
		return this.getAipEtniaDAO().obterPorChavePrimaria(id);
	}

	@Override
	public List<AipLocalizaPaciente> listarPacientesPorAtendimento(
			Integer numeroConsulta, Integer pacCodigo) {
		return this.getAipLocalizaPacienteDAO().listarPacientesPorAtendimento(
				numeroConsulta, pacCodigo);
	}

	@Override
	public void removerAipLocalizaPaciente(
			AipLocalizaPaciente aipLocalizaPaciente) {
		this.getAipLocalizaPacienteDAO().remover(aipLocalizaPaciente);
		this.getAipLocalizaPacienteDAO().flush();
	}

	@Override
	public List<AipLogProntOnlines> listarLogProntOnlines(Integer pacCodigo,
			Date dthrInicio) {
		return this.getAipLogProntOnlinesDAO().listarLogProntOnlines(pacCodigo,
				dthrInicio);
	}

	@Override
	public List<AipLogProntOnlines> pesquisarLogPorServidorProntuarioDatasOcorrencia(
			RapServidores servidor, Integer prontuario, Date dataInicio,
			Date dataFim, DominioOcorrenciaPOL ocorrencia, Integer firstResult,
			Integer maxResults, String orderProperty, Boolean asc) {
		return this.getAipLogProntOnlinesDAO()
				.pesquisarLogPorServidorProntuarioDatasOcorrencia(servidor,
						prontuario, dataInicio, dataFim, ocorrencia,
						firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long pesquisarLogPorServidorProntuarioDatasOcorrenciaCount(
			RapServidores servidor, Integer prontuario, Date dataInicio,
			Date dataFim, DominioOcorrenciaPOL ocorrencia) {
		return this.getAipLogProntOnlinesDAO()
				.pesquisarLogPorServidorProntuarioDatasOcorrenciaCount(
						servidor, prontuario, dataInicio, dataFim, ocorrencia);
	}

	@Override
	public AipCidades obterCidadePorChavePrimaria(Integer chavePrimaria) {
		return getAipCidadesDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public List<AipCidades> pesquisarCidadesPorNomeSiglaUf(String nome,
			String siglaUf) {
		return getAipCidadesDAO().pesquisarCidadesPorNomeSiglaUf(nome, siglaUf);
	}

	@Override
	public AipNacionalidades obterNacionalidadePorCodigoAtiva(Integer codigo) {
		return this.getAipNacionalidadesDAO().obterNacionalidadePorCodigoAtiva(
				codigo);
	}

	@Override
	public Short obterValorSeqPlanoCodPaciente(Integer codigoPaciente) {
		return this.getAipConveniosSaudePacienteDAO()
				.obterValorSeqPlanoCodPaciente(codigoPaciente);
	}

	@Override
	@BypassInactiveModule
	public BigDecimal buscaMatriculaConvenio(final Integer prontuario,
			final Short convenio, final Byte plano) {
		return getAipConveniosSaudePacienteDAO().buscaMatriculaConvenio(
				prontuario, convenio, plano);
	}

	@Override
	public List<AipMovimentacaoProntuarios> listarMovimentacoesProntuarios(
			AipPacientes paciente, Date dthrInicio) {
		return this.getAipMovimentacaoProntuarioDAO()
				.listarMovimentacoesProntuarios(paciente, dthrInicio);
	}

	@Override
	public AipMovimentacaoProntuarios obterMovimentacaoPorPacienteSituacaoTipoEnvioDtMovimento(
			Integer pacCodigo, DominioSituacaoMovimentoProntuario situacao,
			DominioTipoEnvioProntuario tipoEnvio, Date dataMovimento) {
		return this.getAipMovimentacaoProntuarioDAO()
				.obterMovimentacaoPorPacienteSituacaoTipoEnvioDtMovimento(
						pacCodigo, situacao, tipoEnvio, dataMovimento);
	}

	@Override
	public List<AipMovimentacaoProntuarios> pesquisarMovimentacaoPacienteProntRequerido(
			Integer pacCodigo, Integer numConsulta) {
		return this.getAipMovimentacaoProntuarioDAO()
				.pesquisarMovimentacaoPacienteProntRequerido(pacCodigo,
						numConsulta);
	}

	@Override
	public void removerAipMovimentacaoProntuariosSemFlush(
			AipMovimentacaoProntuarios aipMovimentacaoProntuarios) {
		this.getAipMovimentacaoProntuarioDAO().remover(
				aipMovimentacaoProntuarios);
	}

	@Override
	public List<Integer> executarCursorPaciente(Integer prontuario) {
		return this.getAipPacientesDAO().executarCursorPaciente(prontuario);
	}

	@Override
	public List<Integer> pesquisarCPac(final Integer pacCodigo) {
		return this.getAipPacientesDAO().pesquisarCPac(pacCodigo);
	}

	@Override
	public List<AipSolicitantesProntuario> pesquisarUnidadesSolicitantesPorCodigoOuSigla(
			String pesquisa) {
		return movimentacaoProntuarioON.pesquisarUnidadesSolicitantesPorCodigoOuSigla(pesquisa);
	}	
	
	@Override
	public Integer obterProntuarioPorPacCodigo(Integer pacCodigo) {
		return this.getAipPacientesDAO().obterProntuarioPorPacCodigo(pacCodigo);
	}

	@Override
	public List<AipPacientes> executarCursorPac(Integer codigo) {
		return this.getAipPacientesDAO().executarCursorPac(codigo);
	}

	@Override
	public AipPacientes obterNomePacientePorCodigo(Integer pacCodigo) {
		return this.getAipPacientesDAO().obterNomePacientePorCodigo(pacCodigo);
	}

	@Override
	public String obterNomeDoPacientePorCodigo(Integer pacCodigo) {
		return this.getAipPacientesDAO()
				.obterNomeDoPacientePorCodigo(pacCodigo);
	}

	@Override
	public List<Integer> obtemCodPacienteComInternacaoNaoNulaEOutrasDatas(
			Integer pacCodigo) {
		return this.getAipPacientesDAO()
				.obtemCodPacienteComInternacaoNaoNulaEOutrasDatas(pacCodigo);
	}

	@Override
	public List<AipPacientes> pesquisarAipPacientesPorCodigoOuProntuario(
			Object filtro) {
		return this.getAipPacientesDAO()
				.pesquisarAipPacientesPorCodigoOuProntuario(filtro);
	}

	@Override
	public Long pesquisarAipPacientesPorCodigoOuProntuarioCount(Object filtro) {
		return this.getAipPacientesDAO()
				.pesquisarAipPacientesPorCodigoOuProntuarioCount(filtro);
	}

	@Override
	public List<Integer> pesquisarCodigoPacientesPorNumeroQuarto(
			Short numeroQuarto) {
		return this.getAipPacientesDAO()
				.pesquisarCodigoPacientesPorNumeroQuarto(numeroQuarto);
	}

	@Override
	public List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorEspecialidadeEPaciente(
			Short espec, Integer prontuario, List<String> situacoesIn,
			boolean trazHistoricos) {
		List<FluxogramaLaborarorialDadosVO> list = this.getAipPacientesDAO()
				.buscaFluxogramaPorEspecialidadeEPaciente(espec, prontuario,
						situacoesIn, trazHistoricos);
		return list;
	}

	@Override
	public List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorServidorEPaciente(
			RapServidores servidor, Integer prontuario,
			List<String> situacoesIn, boolean trazHistoricos) {

		List<FluxogramaLaborarorialDadosVO> list = this.getAipPacientesDAO()
				.buscaFluxogramaPorServidorEPaciente(servidor, prontuario,
						situacoesIn, trazHistoricos);
		return list;
	}

	@Override
	public List<FluxogramaLaborarorialDadosVO> buscaFluxogramaPorPaciente(
			Integer prontuario, List<String> situacoesIn, boolean trazHistoricos) {
		List<FluxogramaLaborarorialDadosVO> list = this.getAipPacientesDAO()
				.buscaFluxogramaPorPaciente(prontuario, situacoesIn,
						trazHistoricos);
		return list;
	}

	@Override
	public List<Object[]> pesquisaInformacoesPacienteEscalaCirurgias(
			Integer pacCodigo) {
		return this.getAipPacientesDAO()
				.pesquisaInformacoesPacienteEscalaCirurgias(pacCodigo);
	}
	
	@Override
	public void importarArquivo(List<String> listaLinhas, String nomeArquivo,
			String separador, Boolean anularProntuarios,
			Boolean nomeMaeNaoInformado, Boolean gerarFonemas, Boolean migrarEnderecos, Boolean migrarCodigos, Boolean tratarProntuario, String loginUsuarioLogado)
			throws ApplicationBusinessException {

		getMigracaoPacientesON().importarArquivo(listaLinhas, nomeArquivo,
				separador, anularProntuarios, nomeMaeNaoInformado, gerarFonemas, migrarEnderecos, tratarProntuario, loginUsuarioLogado);
	}

	@Override
	public List<AipPacientes> pesquisarPacientePorNumeroCartaoSaude(
			BigInteger numCartaoSaude) {
		return this.getAipPacientesDAO().pesquisarPacientePorNumeroCartaoSaude(
				numCartaoSaude);
	}

	@Override
	public AipPacientes obterPacientePorCodigoDtUltAltaEProntuarioNotNull(
			Integer codigoPaciente, Date data) {
		return this.getAipPacientesDAO()
				.obterPacientePorCodigoDtUltAltaEProntuarioNotNull(
						codigoPaciente, data);
	}

	@Override
	public List<AipPacientes> recuperarPacientes(int firstResult,
			int maxResults, String orderProperty, boolean asc) {
		return this.getAipPacientesDAO().recuperarPacientes(firstResult,
				maxResults, orderProperty, asc);
	}

	@Override
	public Long recuperarPacientesCount() {
		return this.getAipPacientesDAO().recuperarPacientesCount();
	}

	@Override
	public List<AipPacientes> pesquisarPacientePorNomeDtNacimentoNomeMae(
			String nome, Date dataNacimento, String nomeMae) {
		return this.getAipPacientesDAO()
				.pesquisarPacientePorNomeDtNacimentoNomeMae(nome,
						dataNacimento, nomeMae);
	}

	@Override
	public Boolean verificarUnidadeChecagem(Integer atdSeq) {
		return getAtendimentosRN().verificarUnidadeChecagem(atdSeq);
	}

	@Override
	public AipUfs obterAipUfsPorChavePrimaria(String siglaUf) {
		return this.getAipUfsDAO().obterPorChavePrimaria(siglaUf);
	}

	protected AipEtniaDAO getAipEtniaDAO() {
		return aipEtniaDAO;
	}

	protected AipCepLogradourosDAO getAipCepLogradourosDAO() {
		return aipCepLogradourosDAO;
	}

	protected AipLocalizaPacienteDAO getAipLocalizaPacienteDAO() {
		return aipLocalizaPacienteDAO;
	}

	protected AipLogProntOnlinesDAO getAipLogProntOnlinesDAO() {
		return aipLogProntOnlinesDAO;
	}

	protected AipCidadesDAO getAipCidadesDAO() {
		return aipCidadesDAO;
	}

	protected AipUfsDAO getAipUfsDAO() {
		return aipUfsDAO;
	}

	protected AipControleEscalaCirurgiaDAO getAipControleEscalaCirurgiaDAO() {
		return aipControleEscalaCirurgiaDAO;
	}

	public AipPacientesHist pesquisarPacientePorProntuarioHist(
			Integer nroProntuario) {
		return getAipPacientesHistDAO().pesquisarPacientePorProntuario(
				nroProntuario);
	}

	@Override
	public AipAlturaPacientes obterAlturaPorNumeroConsulta(Integer conNumero) {
		return getAipAlturaPacientesDAO().obterAlturaPorNumeroConsulta(
				conNumero);
	}

	protected AipAlturaPacientesDAO getAipAlturaPacientesDAO() {
		return aipAlturaPacientesDAO;
	}

	protected AipProntuarioLiberadosDAO getAipProntuarioLiberadosDAO() {
		return aipProntuarioLiberadosDAO;
	}

	@Override
	public AipPacientes obterPacientePelaConsulta(Integer numeroConsulta) {
		return pacienteON.obterPacientePelaConsulta(numeroConsulta);
	}
	
	@Override
	public BigDecimal obterPesoPacienteUltimaHoraPelaConsulta(Integer numeroConsulta) {
		return getPacienteON().obterPesoPacientePelaConsulta(numeroConsulta);
	}
	
	@Override
	public BigDecimal obterAlturaPacienteUltimaHoraPelaConsulta(Integer numeroConsulta) {
		return getPacienteON().obterAlturaPacientePelaConsulta(numeroConsulta);
	}
	
	@Override
	public AipPesoPacientes obterPesoPacientesPorNumeroConsulta(
			Integer conNumero) {
		return getAipPesoPacientesDAO().obterPesoPacientesPorNumeroConsulta(
				conNumero);
	}

	@Override
	public AipAlturaPacientes obterAlturasPaciente(Integer pacCodigo,
			DominioMomento n) {
		return getAipAlturaPacientesDAO().obterAlturasPaciente(pacCodigo, n);
	}

	@Override
	public Boolean existeEscalaDefinitiva(Short unidade, Date dataCirurgia) {
		return getAipControleEscalaCirurgiaDAO().existeEscalaDefinitiva(
				unidade, dataCirurgia);
	}

	@Override
	public Date recuperarMaxDataFim(Short unidade, Date dataCirurgia) {
		return getAipControleEscalaCirurgiaDAO().recuperarMaxDataFim(unidade,
				dataCirurgia);
	}

	public void persistirAipControleEscalaCirurgia(
			AipControleEscalaCirurgia aipControleEscalaCirurgia) {
		getAipControleEscalaCirurgiaDAO().persistir(aipControleEscalaCirurgia);
	}

	@Override
	public void validaProntuarioScheduler(Date date, String cron) throws ApplicationBusinessException { 
		validaProntuarioON.validaProntuario(); 
	}

	public Long countSolicitantesPorUnidadeFuncional(Short unfSeq) {
		return getAipSolicitantesProntuarioDAO()
				.countSolicitantesPorUnidadeFuncional(unfSeq);
	}

	@Override
	public void limpaProntuariosLiberados() {
		getAipProntuarioLiberadosDAO().limpaProntuariosLiberados();
	}

	@Override
	public List<AipMovimentacaoProntuarios> listarMovimentacoesProntuariosPorCodigoPacienteESituacao(
			Integer pacCodigo, Integer codEvento, Integer origemEmergencia) {
		return getAipMovimentacaoProntuarioDAO()
				.listarMovimentacoesProntuariosPorCodigoPacienteESituacao(
						pacCodigo, codEvento, origemEmergencia);
	}

	protected AipAvisoAgendamentoCirurgiaDAO getAipAvisoAgendamentoCirurgiaDAO() {
		return aipAvisoAgendamentoCirurgiaDAO;
	}

	@Override
	public void persistirAvisoAgendamentoCirurgia(
			AipAvisoAgendamentoCirurgia avisoAgendamentoCirurgia) {
		this.getAipAvisoAgendamentoCirurgiaDAO().persistir(
				avisoAgendamentoCirurgia);
	}

	@Override
	public List<ProntuarioCirurgiaVO> pesquisarDesarquivamentoProntuariosCirurgia(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.getDesarquivamentoProntuarioON()
				.pesquisarDesarquivamentoProntuariosCirurgia(firstResult,
						maxResult, orderProperty, asc);
	}

	@Override
	public Long obterCountPesquisaDesarquivamentoProntuariosCirurgia() {
		return this.getDesarquivamentoProntuarioON()
				.obterCountPesquisaDesarquivamentoProntuariosCirurgia();
	}

	public void atualizarRegistrosDesarquivamentoProntuariosCirurgia(
			List<ProntuarioCirurgiaVO> listaProntuariosSelecionados) {
		this.getDesarquivamentoProntuarioON()
				.atualizarRegistrosDesarquivamentoProntuariosCirurgia(
						listaProntuariosSelecionados);
	}

	@Override
	public void verificarExtensaoArquivoCSV(final String arquivo)
			throws ApplicationBusinessException {
		getMigracaoPacientesON().verificarExtensaoArquivoCSV(arquivo);
	}
	
	@Override
	public List<String> carregarArquivoPacientes(String caminhoAbsolutoTxt)
			throws FileNotFoundException {
		return getMigracaoPacientesON().carregarArquivoPacientes(
				caminhoAbsolutoTxt);
	}

	protected MigracaoPacientesON getMigracaoPacientesON() {
		return migracaoPacientesON;
	}

	public boolean verificarUtilizacaoDigitoVerificadorProntuario() {
		return getPacienteON().verificarUtilizacaoDigitoVerificadorProntuario();
	}

	@Override
	public AipPacientes carregarInformacoesEdicao(
			AipPacientes paciente) {		
		return pacienteON.carregarInformacoesEdicao(paciente);
	}
	
	@Override
	public List<AipEtnia> pesquisarEtniaPorIDouDescricao(Object etnia){
		return getAipEtniaDAO().pesquisarEtniaPorIDouDescricao(etnia);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarPacienteNeonatalPossuiExamePCT(Integer atdSeq,
			String exaSigla, Integer manSeq, Short ufeUnfSeq,
			String situacaoCancelado, Integer meses) {
		return getAipPacientesDAO().verificarPacienteNeonatalPossuiExamePCT(
				atdSeq, exaSigla, manSeq, ufeUnfSeq, situacaoCancelado, meses);
	}
	
	public FatConvenioSaudePlano obterConvenioSaudePlanoAtendimento(Integer seq) {
		return getAtendimentosRN().obterConvenioSaudePlanoAtendimento(seq);	
	}
	
	@Override
	public AipPacientes obterDadosGestantePorProntuario(Integer nroProntuario) {
		return getAipPacientesDAO().obterDadosGestantePorProntuario(nroProntuario);
	}
	
	@Override
	public Boolean existsRegSanguineosPorCodigoPaciente(Integer pacCodigo) {
		return getAipRegSanguineosDAO().existsRegSanguineosPorCodigoPaciente(pacCodigo);
	}
	
	@Override
	public AipRegSanguineos obterRegSanguineosPorCodigoPaciente(Integer pacCodigo, Byte seqp) {
		return getAipRegSanguineosDAO().obterRegSanguineosPorCodigoPaciente(pacCodigo, seqp);
	}
	
	@Override
	public List<AipRegSanguineos> listarRegSanguineosSemDadosPorPaciente(Integer pacCodigo) {
		return getAipRegSanguineosDAO().listarRegSanguineosSemDadosPorPaciente(pacCodigo);		
	}

	@Override
	public void removerAipRegSanguineos(AipRegSanguineos aipRegSanguineos) {
		//remove		
		getAipRegSanguineosDAO().removerPorId(aipRegSanguineos.getId());
		//flush
		getAipRegSanguineosDAO().flush();
	}
	
	@Override
	public void atualizarAipRegSanguineos(AipRegSanguineos aipRegSanguineos) {
		getAipRegSanguineosDAO().atualizar(aipRegSanguineos);
		getAipRegSanguineosDAO().flush();
	}
	
	@Override
	public Byte buscaMaxSeqpAipRegSanguineos(Integer pacCodigo) {
		return getAipRegSanguineosDAO().buscaMaxSeqp(pacCodigo);
	}
	
	@Override
	public void inserirAipRegSanguineos(AipRegSanguineos aipRegSanguineos) {
		getAipRegSanguineosDAO().persistir(aipRegSanguineos);
		getAipRegSanguineosDAO().flush();
	}
	
	@Override
	public Boolean existsRegSanguineosSemDadosPorPaciente(Integer pacCodigo) {
		return getAipRegSanguineosDAO().existsRegSanguineosSemDadosPorPaciente(pacCodigo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.paciente.business.IPacienteFacade#
	 * obterAghAtendimentosOriginal(br.gov.mec.aghu.model.AghAtendimentos)
	 */
	@Override
	public AghAtendimentos obterAghAtendimentosOriginal(AghAtendimentos aghAtendimentos) {

		return getAghAtendimentoDAO().obterOriginal(aghAtendimentos);
	}

	protected AipRegSanguineosDAO getAipRegSanguineosDAO() {
		return aipRegSanguineosDAO;
	}

	public FonemasPacienteReindexacoesRN getFonemasPacienteReindexacoesRN() {
		return fonemasPacienteReindexacoesRN;
	}
	
	@Override
	public AipPacientesDadosCns obterDadosCNSPaciente(Integer codPaciente){
		return getAipPacientesDadosCnsDAO().obterPacientesDadosCns(codPaciente);
	}
	
	protected AipPacientesDadosCnsDAO getAipPacientesDadosCnsDAO(){
		return aipPacientesDadosCnsDAO;
	}

	@Override
	public AinAtendimentosUrgencia ingressarPacienteEmergenciaObstetricaSalaObservacao(Integer numeroConsulta, Integer codigoPaciente, String nomeMicroComputador) throws NumberFormatException, BaseException {
		
		return pacienteON.ingressarPacienteEmergenciaObstetricaSalaObservacao(numeroConsulta, codigoPaciente, nomeMicroComputador);
		
	}
	
	@Override
	public void atualizarPacienteDthrNascimento(Integer pacCodigo, Date dthrNascimento) {
		getPacienteON().atualizarPacienteDthrNascimento(pacCodigo, dthrNascimento);		
	}
	
	@Override
	public List<ListaPacientesCCIHVO> pesquisarPacientesCCIH(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FiltroListaPacienteCCIHVO filtro) {
		return this.aipPacientesDAO.pesquisarPacientesCCIH(firstResult, maxResult, orderProperty, asc, filtro);
	}
	
	@Override
	public Long pesquisarPacientesCCIHCount(FiltroListaPacienteCCIHVO filtro) {
		return this.aipPacientesDAO.pesquisarPacientesCCIHCount(filtro);
	}
	@Override
	public AipPacientes obterPacientesComUnidadeFuncional (Integer pacCodigo){
		return this.aipPacientesDAO.obterPacientesComUnidadeFuncional(pacCodigo);
	}
	
	@Override
	public String obterLocalizacaoPaciente(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unf) {
		return relatorioSumarioTransferenciaON.obterLocalizacaoPacienteParaRelatorio(leito, quarto, unf);
	}
	
	@Override
	public BigDecimal obterPesoPacientesPorCodigoPaciente(Integer pacCodigo){
		return aipPesoPacientesDAO.obterPesoPacientesPorCodigoPaciente(pacCodigo);
	}
	@Override
	public List<ProcedimentoReanimacaoVO> listarProcReanimacao(String descricao,
			DominioSituacao situacao, 
			Integer firstResult, 
			Integer maxResult, 
			String orderProperty, 
			boolean asc) {
		return mcoProcReanimacaoDAO.listarProcReanimacao(descricao,situacao,firstResult,
				maxResult, orderProperty, asc);
	}
	
	@Override
	public Long listarProcReanimacaoCount(String descricao, DominioSituacao situacao) {
		return mcoProcReanimacaoDAO.listarProcReanimacaoCount(descricao,situacao);
	}
	
	@Override
	public List<AbsComponenteSanguineo> listarComponentesSuggestion(Object objPesquisa){
		return absComponenteSanguineoDAO.listarComponentesSuggestion(objPesquisa);
	}
	
	@Override
	public Long listarComponentesSuggestionCount(Object objPesquisa){
		return absComponenteSanguineoDAO.listarComponentesSuggestionCount(objPesquisa);
	}
	
	@Override
	public void persistirProcedimentoReanimacao(Integer seq,
			String componenteSeq,
			Integer matCodigo,
			String descricao,
			DominioSituacao situacao) throws ApplicationBusinessException{
		pacienteON.persistirProcedimentoReanimacao(seq, componenteSeq, matCodigo, descricao, situacao);
	}
	
	@Override
	public List<AfaMedicamento> listarMedicamentosSuggestion(Object objPesquisa){
		return afaMedicamentoDAO.listarMedicamentosSuggestion(objPesquisa);
	}
	
	@Override
	public Long listarMedicamentosSuggestionCount(Object objPesquisa){
		return afaMedicamentoDAO.listarMedicamentosSuggestionCount(objPesquisa);
	}
	
	@Override
	public AfaMedicamento obterMedicamentoPorId(Integer matCodigo){
		return afaMedicamentoDAO.obterPorChavePrimaria(matCodigo);
	}
	
	@Override
	public AbsComponenteSanguineo obterComponentePorId(String codigo){
		return absComponenteSanguineoDAO.obterPorChavePrimaria(codigo);
	}
	@Override
	public ProcedimentoReanimacaoVO obterProcReanimacao(Integer seq) {
		return mcoProcReanimacaoDAO.obterProcReanimacao(seq);
	}

	@Override
	public void persistirSindrome(String descricao, String situacao) {
		pacienteON.persistirSindrome(descricao,situacao);
	}
	
	@Override
	public void ativarInativarSindrome(Integer seq){
		pacienteON.ativarInativarSindrome(seq);
	}
	
	@Override
	public void persistirBallard(Short seq,Short escore, Short igSemanas) {
		pacienteON.persistirBallard(seq,escore, igSemanas);
	}

	@Override
	public void atualizarAtendimentoGestante(Integer gsoPacCodigo, Short gsoSeqp, 
			String nomeMicroComputador, Integer atdSeq) throws BaseException {
		this.getPacienteON().atualizarAtendimentoGestante(gsoPacCodigo, gsoSeqp, nomeMicroComputador, atdSeq);
	}
	@Override
	public AipRegSanguineos obterRegSanguineoPorCodigoPaciente(Integer pacCodigo) {
		return this.aipRegSanguineosDAO.obterRegSanguineoPorCodigoPaciente(pacCodigo);
	}


	@Override
	public void observarPersistenciaMovimentacaoProntuario(
			AipMovimentacaoProntuarios aipMovimentacaoProntuario,
			DominioOperacoesJournal dominio) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		getMovimentacaoProntuarioJournalON().observarPersistenciaMovimentacaoProntuario(aipMovimentacaoProntuario, dominio, servidorLogado.getUsuario());
	}


	protected MovimentacaoProntuarioJournalON getMovimentacaoProntuarioJournalON() {
		return movimentacaoProntuarioJournalON;
	}

	@Override
	public List<AtendimentoJustificativaUsoMedicamentoVO> obterPacientePorAtendimentoComEndereco(Integer atdSeq) {
		return aipPacientesDAO.obterPacientePorAtendimentoComEndereco(atdSeq);
	}

	@Override
	public Character verificarPacienteImplante(Integer pacCodigo, Date dtRealizado, CirurgiaVO cirurgiaVO) {
		return pacienteON.verificarPacienteImplante(pacCodigo, dtRealizado,cirurgiaVO);
	}

	@Override
	public List<AtendimentoPacientesCCIHVO> pesquisarPacientesBuscaAtivaCCIH(FiltroListaPacienteCCIHVO filtro){
		return this.aipPacientesDAO.pesquisarPacientesBuscaAtivaCCIH(filtro);
	}
	@Override
	public List<AfaMedicamento> obterListaMedicamentosTubercolostaticos() {
		return afaMedicamentoDAO.obterListaMedicamentoTubercolostaticos();
	}
	
	@Override
	public String verificarAtualizacaoPaciente(String nomePaciente, Integer numeroConsulta) throws ApplicationBusinessException {
		return pacienteON.verificarAtualizacaoPaciente(nomePaciente, numeroConsulta);
	}
	
	@Override
	public void atualizarPacienteConsulta(AipPacientes paciente, Integer numeroConsulta) {
		pacienteON.atualizarPacienteConsulta(paciente, numeroConsulta);
	}

	
	@Override
	public PacienteAgendamentoPrescribenteVO obterCodigoAtendimentoInformacoesPaciente(Integer codigo, Integer prontuario) {
		return this.aipPacientesDAO.obterCodigoAtendimentoInformacoesPaciente(codigo, prontuario);
	}
	
	/***
	 * Verifica se existe alguma alta registrada para o paciente
	 * #47350
	 *
	 * @param codPaciente Código do paciente
	 * @return Se existe alta registrada
	 */
	@Override
	public Boolean verificarExisteAlta(Integer codPaciente) {
		return aipPacientesDAO.verificarExisteAltaPaciente(codPaciente);
	}
	
	/***
	 * Obtém a lista de altas de um paciente
	 * #47350
	 * 
	 * @param codPaciente Código do paciente
	 * @return List de {@link MamAltaSumario} 
	 */
	@Override
	public List<MamAltaSumario> obterDadosAltaPaciente(Integer codPaciente) {
		return aipPacientesDAO.obterDadosAltaPaciente(codPaciente);
	}

	@Override
	public AipGrupoFamiliarPacientes obterDadosGrupoFamiliarPaciente(AipPacientes paciente){
		return aipGrupoFamiliarPacientesDAO.obterDadosGrupoFamiliarPaciente(paciente);
	}
	
	@Override
	public MtxTransplantes pesquisarTransplantePaciente(Integer pacCodigo2, Integer seqTransplante) {		 
		return mtxTransplantesDAO.pesquisarTransplantePaciente(pacCodigo2, seqTransplante);
	}
	@Override
	public void enviarEmailPacienteGMR(AghJobDetail job) throws ApplicationBusinessException {
		pacienteRN.enviarEmailPacienteGMR(job);
	}
	
	@Override
	public List<AipCidades> obterAipCidadesPorNomeAtivo(Object filtro) {
		return aipCidadesDAO.obterAipCidadesPorNomeAtivo(filtro);
	}
	
	@Override
	public Long obterAipCidadesPorNomeAtivoCount(Object filtro) {
		return aipCidadesDAO.obterAipCidadesPorNomeAtivoCount(filtro);
	}

	@Override
	public List<AipLogradouros> obterAipLogradourosPorNome(Object filtro) {
		return aipLogradourosDAO.obterAipLogradourosPorNome(filtro);
	}

	@Override
	public Long obterAipLogradourosPorNomeCount(Object filtro) {
		return aipLogradourosDAO.obterAipLogradourosPorNomeCount(filtro);
	}

	@Override
	public List<AipCepLogradouros> obterAipCepLogradourosPorCEP(Object filtro) {
		return aipCepLogradourosDAO.obterAipCepLogradourosPorCEP(filtro);
	}

	@Override
	public Long obterAipCepLogradourosPorCEPCount(Object filtro) {
		return aipCepLogradourosDAO.obterAipCepLogradourosPorCEPCount(filtro);
	}
	
	@Override
	public List<AipBairros> obterAipBairrosPorDescricao(Object filtro) {
		return aipBairrosDAO.obterAipBairrosPorDescricao(filtro);
	}
	
	@Override
	public Long obterAipBairrosPorDescricaoCount(Object filtro) {
		return aipBairrosDAO.obterAipBairrosPorDescricaoCount(filtro);
	}

	@Override
	public List<AipUfs> obterAipUfsPorSiglaNome(Object filtro) {
		return aipUfsDAO.obterAipUfsPorSiglaNome(filtro);
	}

	@Override
	public Long obterAipUfsPorSiglaNomeCount(Object filtro) {
		return aipUfsDAO.obterAipUfsPorSiglaNomeCount(filtro);
	}
	
	/**
	 * C1#41790
	 */
	
	@Override
	public List<AipPacientes> pesquisarPacientePorNomeOuProntuario(String strPesquisa) {
		return aipPacientesDAO.pesquisarPacientePorNomeOuProntuario(strPesquisa);
	}
	
	@Override
	public Long pesquisarPacientePorNomeOuProntuarioCount(String strPesquisa) {
		return aipPacientesDAO.pesquisarPacientePorNomeOuProntuarioCount(strPesquisa);
	}
	
	/**
	 * C1#50814 e 50816
	  */
		
	@Override
	public List<AipPacientes> consultarDadosPacientePorNomeOuProntuarioOuCodigo(AipPacientes paciente, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return aipPacientesDAO.pesquisarPacientePorNomeOuProntuarioOuCodigo(paciente, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long consultarDadosPacientePorNomeOuProntuarioOuCodigoCount(AipPacientes paciente){
		return this.aipPacientesDAO.pesquisarPacientePorNomeOuProntuarioOuCodigoCount(paciente);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoPacienteInternadoPorProntuario(Integer nroProntuario) {
		return aghAtendimentoDAO.obterAtendimentoPacienteInternadoPorProntuario(nroProntuario);
	}
	
	@Override
	public List<AipPacientes> obterPacienteInternadoPorLeito(String leitoId) {
		return aipPacientesDAO.obterPacienteInternadoPorLeito(leitoId);
	}
	
	@Override
	public List<AipPacientes> obterPacientePorCartaoCpfCodigoPronturario(Integer nroProntuario, Integer nroCodigo, Long cpf,
			BigInteger... nroCartaoSaude) throws ApplicationBusinessException {
		return this.getPacienteON().obterPacientePorCartaoCpfCodigoPronturario(nroProntuario, nroCodigo, cpf, nroCartaoSaude[0]);
	}
	
	@Override
	public Long pesquisaPacienteCount(
			Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente) {
		return pesquisarPacienteON.pesquisaPacienteCount(codigoPaciente, prontuario, nomePesquisaPaciente);
	}
	
	@Override
	public List<AipPacientes> pesquisaPacientePorCodigoOuProntuario(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente) {
		return pesquisarPacienteON.pesquisaPacientesMovimentacaoProntuario(firstResult, maxResults, orderProperty, asc, codigoPaciente, prontuario, nomePesquisaPaciente);
	}
	
	@Override
	public AipPacientes refreshAipPaciente(AipPacientes aipPacientes) {
		try {
			this.refresh(aipPacientes);
		} catch (IllegalArgumentException ex) {
			aipPacientes = getAipPacientesDAO().obterPorChavePrimaria(
					aipPacientes.getCodigo());
		}
		return aipPacientes;
	}

	@Override
	public void persistirCadastroOrigemProntuario(List<AipMovimentacaoProntuariosVO> listaItensSelecionados, AghSamis origensProntuarios,
			RapServidores servidorLogado) throws ApplicationBusinessException{
		this.getMovimentacaoProntuarioON().persistirVinculoMovimentacaoOrigemProntuario(listaItensSelecionados, origensProntuarios, 
				servidorLogado);
		
	}

	@Override
	public List<AipMovimentacaoProntuariosVO> pesquisaMovimentacoesDeProntuarios(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AipPacientes paciente, AghSamis origemProntuariosPesquisa, 
			AghUnidadesFuncionais unidadeSolicitantePesquisa,  DominioSituacaoMovimentoProntuario situacao, 
			Date dataMovimentacaoConsulta) throws ApplicationBusinessException {
		return getMovimentacaoProntuarioON().pesquisaMovimentacoesDeProntuarios(firstResult, maxResult, orderProperty, asc, paciente,
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
	}

	@Override
	public AipMovimentacaoProntuarios refreshAipMovimentacaoProntuarios(
			AipMovimentacaoProntuarios aipMovimentacaoProntuarios) {
		try {
			this.refresh(aipMovimentacaoProntuarios);
		} catch (IllegalArgumentException ex) {
			aipMovimentacaoProntuarios = getAipMovimentacaoProntuarioDAO().obterPorChavePrimaria(
					aipMovimentacaoProntuarios.getSeq());
		}
		return aipMovimentacaoProntuarios;
	}

	

	@Override
	public Long pesquisaMovimentacoesDeProntuariosCount(
			AipPacientes paciente, AghSamis origemProntuariosPesquisa, 
			AghUnidadesFuncionais unidadeSolicitantePesquisa, DominioSituacaoMovimentoProntuario situacao, 
			Date dataMovimentacaoConsulta) {
		return this.getMovimentacaoProntuarioON().pesquisaMovimentacoesDeProntuariosCount(paciente, 
				origemProntuariosPesquisa, unidadeSolicitantePesquisa, situacao, dataMovimentacaoConsulta);
	}

	@Override
	public void persistirMovimentacaoParaUnidadeSolicitante(List<AipMovimentacaoProntuariosVO> listaItensSelecionados, AghUnidadesFuncionais unidadeSolicitanteAlteracao,
			RapServidores servidorLogado, DominioSituacaoMovimentoProntuario situacao) throws ApplicationBusinessException {
		this.getMovimentacaoProntuarioON().persistirVinculoMovimentacaoUnidadeSolicitante(listaItensSelecionados, unidadeSolicitanteAlteracao, servidorLogado, situacao);
		
	}

	@Override
	public void persistirDevolucaoDeProntuario(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados,
			RapServidores servidorLogado) throws ApplicationBusinessException {
		this.getMovimentacaoProntuarioON().persistirDevolucaoDeProntuario(listaItensSelecionados, servidorLogado);
		
	}

	@Override
	public List<AipSolicitantesProntuario> verificaLocalParaMovimentacao(
			List<AipMovimentacaoProntuariosVO> listaItensSelecionados, String param) throws ApplicationBusinessException {
		return this.getMovimentacaoProntuarioON().verificaLocalParaMovimentacao(listaItensSelecionados, param);
		
	}
	
	protected MovimentacaoProntuarioON getMovimentacaoProntuarioON() {
		return movimentacaoProntuarioON;
	}
	
	@Override
	public void persistirAipMovimentacaoProntuario(AipMovimentacaoProntuarios aipMovimentacaoProntuarios){
		getAipMovimentacaoProntuarioDAO().persistir(aipMovimentacaoProntuarios);
		getAipMovimentacaoProntuarioDAO().flush();
	}
	
}