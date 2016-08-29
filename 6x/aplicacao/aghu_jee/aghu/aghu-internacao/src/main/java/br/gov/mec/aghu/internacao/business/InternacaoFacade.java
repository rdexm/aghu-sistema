package br.gov.mec.aghu.internacao.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.HibernateException;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioLocalizacaoPaciente;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProcedenciaPacientes;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioPacientesUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioTipoDadosAltaPaciente;
import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.dominio.DominioTransacaoAltaPaciente;
import br.gov.mec.aghu.faturamento.vo.AinMovimentosInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.ReInternacoesVO;
import br.gov.mec.aghu.indicadores.vo.ReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.internacao.business.vo.EscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.business.vo.FlagsValidacaoDadosAltaPacienteVO;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ManterEquipesPorProfissionalRN;
import br.gov.mec.aghu.internacao.dao.AhdHospitaisDiaDAO;
import br.gov.mec.aghu.internacao.dao.AinAcompanhantesInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinCaracteristicaLeitoDAO;
import br.gov.mec.aghu.internacao.dao.AinDiariasAutorizadasDAO;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinIndicadorHospitalarResumidoDAO;
import br.gov.mec.aghu.internacao.dao.AinIndicadoresHospitalaresDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.dao.AinResponsaveisPacienteDAO;
import br.gov.mec.aghu.internacao.dao.AinSolicitacoesInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposCaraterInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMovimentoLeitoDAO;
import br.gov.mec.aghu.internacao.dao.ProcEfetDAO;
import br.gov.mec.aghu.internacao.dao.ReferencialClinicaEspecialidadeVODAO;
import br.gov.mec.aghu.internacao.dao.VAinConvenioPlanoDAO;
import br.gov.mec.aghu.internacao.dao.VAinInternacoesExcedentesDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.PesquisaInternacaoON;
import br.gov.mec.aghu.internacao.pesquisa.vo.PacientesInternadosUltrapassadoVO;
import br.gov.mec.aghu.internacao.vo.AltasPorPeriodoVO;
import br.gov.mec.aghu.internacao.vo.AltasPorUnidadeVO;
import br.gov.mec.aghu.internacao.vo.BaixasDiaPorEtniaVO;
import br.gov.mec.aghu.internacao.vo.BaixasDiaVO;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.DadosAltaSumarioVO;
import br.gov.mec.aghu.internacao.vo.PacienteIdadeCIDVO;
import br.gov.mec.aghu.internacao.vo.PacienteUnidadeFuncionalVO;
import br.gov.mec.aghu.internacao.vo.PacientesAniversariantesVO;
import br.gov.mec.aghu.internacao.vo.PacientesComObitoVO;
import br.gov.mec.aghu.internacao.vo.PesquisaSituacoesLeitosVO;
import br.gov.mec.aghu.internacao.vo.ProcedenciaPacientesInternadosVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.internacao.vo.RelatorioAltasDiaVO;
import br.gov.mec.aghu.internacao.vo.RelatorioBoletimInternacaoVO;
import br.gov.mec.aghu.internacao.vo.RelatorioSolicitacaoInternacaoVO;
import br.gov.mec.aghu.internacao.vo.ResponsaveisPacienteVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.internacao.vo.SituacaoLeitosVO;
import br.gov.mec.aghu.internacao.vo.ValidaContaTrocaConvenioVO;
import br.gov.mec.aghu.internacao.vo.VerificaPermissaoVO;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.AghProfissionaisEquipeId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinCrachaAcompanhantes;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinIndicadorHospitalarResumido;
import br.gov.mec.aghu.model.AinIndicadoresHospitalares;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinQuartos.Fields;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.ProcEfet;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAinConvenioPlano;

/**
 * Porta de entrada do módulo de internação.
 * 
 * @author lalegre
 * 
 */


@Modulo(ModuloEnum.INTERNACAO)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.JUnit4TestShouldUseTestAnnotation",
		"PMD.CouplingBetweenObjects" })
@Stateless
public class InternacaoFacade extends BaseFacade implements	IInternacaoFacade {

	@EJB
	private ManterEquipesPorProfissionalRN manterEquipesPorProfissionalRN;
	
	@EJB
	private RelatorioInternacaoRN relatorioInternacaoRN;
	
	@EJB
	private ValidaInternacaoRN validaInternacaoRN;
	
	@EJB
	private PacientesComObitoON pacientesComObitoON;
	
	@EJB
	private AltasPorUnidadeRN altasPorUnidadeRN;
	
	@EJB
	private RelatorioLocalizadorPacienteON relatorioLocalizadorPacienteON;
	
	@EJB
	private DarAltaPacienteON darAltaPacienteON;
	
	@EJB
	private CadastroInternacaoON cadastroInternacaoON;
	
	@EJB
	private PacientePorUnidadeON pacientePorUnidadeON;
	
	@EJB
	private ProcedenciaPacientesInternadosON procedenciaPacientesInternadosON;
	
	@EJB
	private LeitoDesativadoRN leitoDesativadoRN;
	
	@EJB
	private SituacaoLeitosON situacaoLeitosON;
	
	@EJB
	private RelatorioSolicitacaoInternacaoON relatorioSolicitacaoInternacaoON;
	
	@EJB
	private RelatorioInternacaoON relatorioInternacaoON;
	
	@EJB
	private AtualizaAcompanhantesInternacaoON atualizaAcompanhantesInternacaoON;
	
	@EJB
	private SubstituirProntuarioAtendimentoRN substituirProntuarioAtendimentoRN;
	
	@EJB
	private InternacaoON internacaoON;
	
	@EJB
	private RelatorioResponsaveisPacienteON relatorioResponsaveisPacienteON;
	
	@EJB
	private AltasPorUnidadeON altasPorUnidadeON;
	
	@EJB
	private AltaPacienteObservacaoON altaPacienteObservacaoON;
	
	@EJB
	private PacientesAniversariantesON pacientesAniversariantesON;
	
	@EJB
	private RelatorioAltasDiaON relatorioAltasDiaON;
	
	@EJB
	private AtualizaInternacaoRN atualizaInternacaoRN;
	
	@EJB
	private EscalaProfissionaisInternacaoON escalaProfissionaisInternacaoON;
	
	@EJB
	private VerificaEscalaProfissionalSchedulerRN verificaEscalaProfissionalSchedulerRN;
	
	@EJB
	private MvtoFatorPredisponentesRN mvtoFatorPredisponentesRN;
	
	@EJB
	private InternacaoRN internacaoRN;
	
	@EJB
	private LeitoBloqueioRN leitoBloqueioRN;
	
	@EJB
	private EstornarAltaPacienteON estornarAltaPacienteON;
	
	@EJB
	private ProcedenciaPacientesInternadosRN procedenciaPacientesInternadosRN;
	
	@EJB
	private CensoRN censoRN;
	
	@EJB
	private AtendimentoUrgenciaRN atendimentoUrgenciaRN;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@Inject
	private AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO;
	
	@Inject
	private ReferencialClinicaEspecialidadeVODAO referencialClinicaEspecialidadeVODAO;
	
	@Inject
	private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;
	
	@Inject
	private AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO;
	
	@Inject
	private AhdHospitaisDiaDAO ahdHospitaisDiaDAO;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@Inject
	private AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO;
	
	@Inject
	private VAinInternacoesExcedentesDAO vAinInternacoesExcedentesDAO;
	
	@Inject
	private AinIndicadoresHospitalaresDAO ainIndicadoresHospitalaresDAO;
	
	@Inject
	private AinSolicitacoesInternacaoDAO ainSolicitacoesInternacaoDAO;
	
	@Inject
	private AinCaracteristicaLeitoDAO ainCaracteristicaLeitoDAO;
	
	@Inject
	private AinIndicadorHospitalarResumidoDAO ainIndicadorHospitalarResumidoDAO;
	
	@Inject
	private AinExtratoLeitosDAO ainExtratoLeitosDAO;
	
	@Inject
	private AinQuartosDAO ainQuartosDAO;
	
	@Inject
	private VAinConvenioPlanoDAO vAinConvenioPlanoDAO;
	
	@Inject
	private AinTiposCaraterInternacaoDAO ainTiposCaraterInternacaoDAO;
	
	@Inject
	private ProcEfetDAO procEfetDAO;
	
	@Inject
	private AinDiariasAutorizadasDAO ainDiariasAutorizadasDAO;
	
	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;
	
	@EJB
	private RelatorioBaixasDiaON relatorioBaixasDiaON;

	@EJB
	PesquisaInternacaoON pesquisaInternacaoON;
	
	@EJB
	private RelatorioIdadeCIDRN relatorioIdadeCIDRN;

	@EJB
	private RelatorioAltasPorPeriodoRN relatorioAltasPorPeriodoRN;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 532713374719760690L;
	
//	private static final String ENTITY_MANAGER = "entityManager";

	@Override
	public void atualizaPrevisaoAltaInternacao(AinInternacao internacao,
			Date dtPrevAlta, Boolean confirmacaoPelaModalPrevPlanoAlta,
			Boolean considerarValorModalPlanoPrevAlta, Boolean bloquearData) throws BaseException {
		this.getInternacaoON().atualizaPrevisaoAltaInternacao(internacao,
				dtPrevAlta, confirmacaoPelaModalPrevPlanoAlta, considerarValorModalPlanoPrevAlta, bloquearData);
	}
	
	@Override
	public Boolean exibirModalPlanoPrevAlta(AinInternacao internacao) throws ApplicationBusinessException{
		return this.getInternacaoON().exibirModalPlanoPrevAlta(internacao);
	}

	@Override
	public Boolean verificarCaracteristicaUnidadeFuncional(Short seq,
			ConstanteAghCaractUnidFuncionais caracteristica) {
		return getRelatorioInternacaoRN()
				.verificarCaracteristicaUnidadeFuncional(seq, caracteristica);
	}

	@Override
	public List<ProfessorCrmInternacaoVO> pesquisarProfessoresCrm(
			Object strParam, Integer matriculaProfessor,
			Short vinCodigoProfessor) {
		return getCadastroInternacaoON().pesquisarProfessoresCrm(strParam,
				matriculaProfessor, vinCodigoProfessor);
	}

	
	@Override	
	public AinLeitos obterLeitoComAla(String leitoID){
		return ainLeitosDAO.obterLeitoComAla(leitoID);
	}
	
	
	/**
	 * Este método realiza a pesquisa de CRM Professores para a tela de
	 * internação
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @dbtables AghEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfissionaisEspConvenio select
	 * @dbtables AinEscalasProfissionalInt select
	 * 
	 * @param especialidade
	 *            , convenioPlano, strParam
	 * @return listaLOV
	 */
	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<ProfessorCrmInternacaoVO> pesquisarProfessoresCrm(Short espSeq,
			String espSigla, Short cnvCodigo, Boolean indVerfEscalaProfInt,
			String strParam, Integer matriculaProfessor,
			Short vinCodigoProfessor) {
		return getCadastroInternacaoON().pesquisarProfessoresCrm(espSeq,
				espSigla, cnvCodigo, indVerfEscalaProfInt, strParam,
				matriculaProfessor, vinCodigoProfessor);
	}


	/**
	 * Este método monta e retorna apenas um objeto ProfessorCrmInternacaoVO
	 * 
	 * @param professorEspecialidade
	 *            , cnvCodigo
	 * @return ProfessorCrmInternacaoVO
	 */
	@Override
	public ProfessorCrmInternacaoVO obterProfessorCrmInternacaoVO(
			RapServidores servidorProfessor, AghEspecialidades especialidade,
			Short cnvCodigo) {
		return getCadastroInternacaoON().obterProfessorCrmInternacaoVO(
				servidorProfessor, especialidade, cnvCodigo);
	}

	protected CadastroInternacaoON getCadastroInternacaoON() {
		return cadastroInternacaoON;
	}

	@Override
	public AinInternacao obterInternacao(Integer seqInternacao) {
		return getInternacaoON().obterInternacaoPorSeq(seqInternacao);
	}
	
	@Override
	public AinInternacao obterInternacaoPorChavePrimaria(Integer seqInternacao, Enum[] innerArgs, Enum[] leftArgs) {
		return getAinInternacaoDAO().obterPorChavePrimaria(seqInternacao, innerArgs, leftArgs);
	}

	@Override
	public Integer obterCodigoCaraterInternacaoPorAinInternacao(Integer seq){
		return getAinInternacaoDAO().obterCodigoCaraterInternacaoPorAinInternacao(seq);
	}
	
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	@BypassInactiveModule
	public AinInternacao obterAinInternacaoPorChavePrimaria(
			Integer chavePrimaria, Enum... fields) {
		return getAinInternacaoDAO().obterPorChavePrimaria(chavePrimaria, fields);
	}
	
	@Override
	@BypassInactiveModule
	public AinInternacao obterAinInternacaoPorChavePrimariaProcessamentoCusto(Integer chavePrimaria) {
		return getAinInternacaoDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<Object[]> pesquisarDatas(Integer prontuario, Date dataInternacao) {
		return getAinInternacaoDAO().pesquisarDatas(prontuario, dataInternacao);
	}

	/**
	 * Primeiro MovimentosInternacao filtrando pelo maior seq de Internacao de
	 * uma ContaInternacao e também pela DthrLancamento.
	 * 
	 * @param cthSeq
	 * @param dthrLancamento
	 * @return List<AinMovimentosInternacao>
	 */
	@Override
	public AinMovimentosInternacao buscarPrimeiroMovimentosInternacaoPorMaiorInternacaoDeContaInternacaoEDthrLancamento(
			Integer cthSeq, Date dthrLancamento) {
		return getAinMovimentoInternacaoDAO()
				.buscarPrimeiroMovimentosInternacaoPorMaiorInternacaoDeContaInternacaoEDthrLancamento(
						cthSeq, dthrLancamento);
	}

	@Override
	public List<AinMovimentosInternacao> listarMovimentosInternacao(
			Integer intSeq, Date dtIniAtd, Date dtFimCta,
			Integer[] codigosTipoMovimentoInternacao) {
		return getAinMovimentoInternacaoDAO().listarMovimentosInternacao(
				intSeq, dtIniAtd, dtFimCta, codigosTipoMovimentoInternacao);
	}

	@Override
	public List<AinDiariasAutorizadas> pesquisarDiariasAutorizadasOrdenadoCriadoEmDesc(
			Integer seqInternacao) {
		return getAinDiariasAutorizadasDAO()
				.pesquisarDiariasAutorizadasOrdenadoCriadoEmDesc(seqInternacao);
	}

	@Override
	public void atualizarInternacao(AinInternacao internacao,
			String nomeMicrocomputador, RapServidores servidorLogado,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario
			, boolean umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL)
			throws BaseException {
		getInternacaoRN().atualizarInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidorLogado, umFATK_CTH4_RN_UN_V_ATU_CTH_SSM_SOL);
	}

	@Override
	public void atualizarInternacaoComFlush(AinInternacao internacao,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario)
			throws BaseException {
		getInternacaoRN().atualizarInternacaoComEstorno(internacao, null, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
		this.flush();
	}

	@Override
	public void estornarAltaPaciente(final Integer seqInternacao, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final Boolean substituirProntuario, RapServidores servidor) throws BaseException{
		getInternacaoRN().estornarAltaPaciente(seqInternacao, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidor);
	}

	/**
	 * ORADB: AINT_INT_BRU, AINT_INT_ARU
	 * 
	 * @param ainInternacao
	 * @throws ApplicationBusinessException
	 * 
	 *             Invocar este método caso tenha algum flush antes do final da
	 *             transacao
	 */
	@Override
	public void atualizarInternacao(AinInternacao internacao,
			AinInternacao internacaoOld, String nomeMicrocomputador, RapServidores servidor,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws BaseException {
		getInternacaoRN().atualizarInternacao(internacao, internacaoOld, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidor);
	}

	/**
	 * ORADB: AINT_INT_BRU, AINT_INT_ARU
	 * 
	 * @param ainInternacao
	 * @throws ApplicationBusinessException
	 * 
	 *             Invocar este método caso tenha algum flush antes do final da
	 *             transacao
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','alterar')}")
	public void atualizarInternacao(AinInternacao internacao,
			AinInternacao internacaoOld, boolean flush,
			String nomeMicrocomputador, RapServidores servidor,
			final Date dataFimVinculoServidor,
			final Boolean substituirProntuario) throws BaseException {
		getInternacaoRN().atualizarInternacao(internacao, internacaoOld,
				nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario, servidor);
		this.flush();
	}

	@Override
	@Secure("#{s:hasPermission('internacao','alterar')}")
	public void inserirInternacao(AinInternacao internacao,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor, Boolean substituirProntuario)
			throws BaseException {
		getInternacaoRN().inserirInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
	}

	@Override
	public boolean gravarDataAltaPaciente(
			FlagsValidacaoDadosAltaPacienteVO flagsValidacaoDadosAltaPacienteVO, VerificaPermissaoVO verificaPermissaoVO,
			DominioTipoDadosAltaPaciente tipoDadoAltaPaciente,
			AinInternacao internacao, AinTiposAltaMedica tipoAltaMedica,
			AghInstituicoesHospitalares instituicaoHospitalar,
			Date dthrAltaMedica, Date dtSaidaPaciente, Integer docObito,
			boolean dtSaidaPacBD, boolean dthrAltaMedicaBD,
			boolean tipoAltaMedicaBD, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		return getDarAltaPacienteON().gravarDataAltaPaciente(
				flagsValidacaoDadosAltaPacienteVO, verificaPermissaoVO, tipoDadoAltaPaciente,
				internacao, tipoAltaMedica, instituicaoHospitalar,
				dthrAltaMedica, dtSaidaPaciente, docObito, dtSaidaPacBD,
				dthrAltaMedicaBD, tipoAltaMedicaBD, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * traducao da function ainc_alta_medica
	 * 
	 * @param internacaoSeq
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	public DadosAltaSumarioVO buscarDadosAltaMedica(Integer internacaoSeq)
			throws ApplicationBusinessException {
		return getDarAltaPacienteON().buscarDadosAltaMedica(internacaoSeq);
	}

	@Override
	public Long pesquisaInternacoesParaDarAltaPacienteCount(
			Integer prontuario) {
		return getDarAltaPacienteON()
				.pesquisaInternacoesParaDarAltaPacienteCount(prontuario);
	}

	@Override
	public List<AinInternacao> pesquisaInternacoesParaDarAltaPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer prontuario) {
		return getDarAltaPacienteON().pesquisaInternacoesParaDarAltaPaciente(
				firstResult, maxResult, orderProperty, asc, prontuario);
	}

	@Override
	public List<AinMovimentosInternacaoVO> listarMotivosInternacaoVOOrdenadoPorDthrLancamento(
			Integer intSeq, Date dtInicio, Date dtFim,
			Integer[] tiposMovimentoIgnorados) {
		return this.getAinMovimentoInternacaoDAO()
				.listarMotivosInternacaoVOOrdenadoPorDthrLancamento(intSeq,
						dtInicio, dtFim, tiposMovimentoIgnorados);
	}

	@Override
	public List<AinMovimentosInternacao> listarMotivosInternacaoPorInternacaoECriadoem(
			Integer intSeq, Date criadoEm, String colunaOrder, Boolean order) {
		return this.getAinMovimentoInternacaoDAO()
				.listarMotivosInternacaoPorInternacaoECriadoem(intSeq,
						criadoEm, colunaOrder, order);
	}

	@Override
	public AinDiariasAutorizadas buscarPrimeiraDiariaAutorizada(Integer cthSeq) {
		return getAinDiariasAutorizadasDAO().buscarPrimeiraDiariaAutorizada(
				cthSeq);
	}

	@Override
	public AinResponsaveisPaciente buscaResponsaveisPaciente(Integer intSeq) {
		return getAinResponsaveisPacienteDAO()
				.buscaResponsaveisPaciente(intSeq);
	}
	
	@Override
	public AinResponsaveisPaciente obterResponsaveisPaciente(Integer intSeq) {
		return getAinResponsaveisPacienteDAO().obterPorChavePrimaria(intSeq);
	}
	
	@Override
	public void atualizaResponsaveisPaciente(AinResponsaveisPaciente responsavelPaciente) {
		 getAinResponsaveisPacienteDAO().atualizar(responsavelPaciente);
				
	}

	// ======================================================

	protected RelatorioInternacaoRN getRelatorioInternacaoRN() {
		return relatorioInternacaoRN;
	}

	protected RelatorioInternacaoON getRelatorioInternacaoON() {
		return relatorioInternacaoON;
	}

	protected InternacaoON getInternacaoON() {
		return internacaoON;
	}

	protected InternacaoRN getInternacaoRN() {
		return internacaoRN;
	}

	protected ValidaInternacaoRN getValidaInternacaoRN() {
		return validaInternacaoRN;
	}

	protected SubstituirProntuarioAtendimentoRN getSubstituirProntuarioAtendimentoRN() {
		return substituirProntuarioAtendimentoRN;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}

	protected AinDiariasAutorizadasDAO getAinDiariasAutorizadasDAO() {
		return ainDiariasAutorizadasDAO;
	}

	protected AinResponsaveisPacienteDAO getAinResponsaveisPacienteDAO() {
		return ainResponsaveisPacienteDAO;
	}

	@Override
	public List<AinLeitos> obterLeitosAtivosPorUnf(Object strPesquisa,
			Short unfSeq) {
		return getAinLeitosDAO().obterLeitosAtivosPorUnf(strPesquisa, unfSeq);
	}

	@Override
	public Long obterLeitosAtivosPorUnfCount(Object strPesquisa, Short unfSeq) {
		return getAinLeitosDAO().obterLeitosAtivosPorUnfCount(strPesquisa,
				unfSeq);
	}
	
	public RelatorioAltasPorPeriodoRN getRelatorioAltasPorPeriodoRN() {
		return relatorioAltasPorPeriodoRN;
	}

	public void setRelatorioAltasPorPeriodoRN(RelatorioAltasPorPeriodoRN relatorioAltasPorPeriodoRN) {
		this.relatorioAltasPorPeriodoRN = relatorioAltasPorPeriodoRN;
	}

	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}

	@Override
	public List<AinQuartos> listarQuartosPorUnidadeFuncionalEDescricao(
			String descricao, Short unfSeq, Boolean orderAsc,
			Fields[] atributosOrder) {
		return getAinQuartosDAO().listarQuartosPorUnidadeFuncionalEDescricao(
				descricao, unfSeq, orderAsc, atributosOrder);
	}

	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO() {
		return ainAtendimentosUrgenciaDAO;
	}

	@Override
	public AinAtendimentosUrgencia pesquisaAtendimentoUrgencia(
			Integer sequence, Integer prontuario) {
		return getAinAtendimentosUrgenciaDAO().pesquisaAtendimentoUrgencia(
				sequence, prontuario);
	}

	@Override
	public AinInternacao pesquisaInternacao(Integer sequence, Integer prontuario) {
		return this.getAinInternacaoDAO().pesquisaInternacao(sequence,
				prontuario);
	}

	@Override
	public Long pesquisaAtendimentoUrgenciaCount(Integer sequence,
			Integer prontuario) {
		return getAinAtendimentosUrgenciaDAO()
				.pesquisaAtendimentoUrgenciaCount(sequence, prontuario);
	}

	@Override
	public Long pesquisaInternacaoCount(Integer sequence, Integer prontuario) {
		return this.getAinInternacaoDAO().pesquisaInternacaoCount(sequence,
				prontuario);
	}

	@Override
	public List<AinAtendimentosUrgencia> listarAtendimentosUrgencia(
			Integer sequence, Integer prontuario) {
		return this.getAinAtendimentosUrgenciaDAO().listarAtendimentosUrgencia(
				sequence, prontuario);
	}

	@Override
	public List<AinInternacao> listarInternacoes(Integer sequence,
			Integer prontuario) {
		return this.getAinInternacaoDAO().listarInternacoes(sequence,
				prontuario);
	}

	@Override
	public PesquisaSituacoesLeitosVO pesquisaSituacoesLeitos(AghClinicas clinica) {
		return this.getSituacaoLeitosON().pesquisaSituacoesLeitos(clinica);
	}

	@Override
	public SituacaoLeitosVO pesquisaSituacaoSemLeitos() {
		return this.getSituacaoLeitosON().pesquisaSituacaoSemLeitos();
	}

	@Override
	public SituacaoLeitosVO pesquisaCapacidadeInstaladaLeitos(
			AghClinicas clinica) {
		return this.getSituacaoLeitosON().pesquisaCapacidadeInstaladaLeitos(
				clinica);
	}

	protected SituacaoLeitosON getSituacaoLeitosON() {
		return situacaoLeitosON;
	}

	@Override
	public AinInternacao obterUltimaInternacaoPaciente(Integer codigo) {
		return this.getInternacaoON().obterUltimaInternacaoPaciente(codigo);
	}

	@Override
	public AinQuartos pesquisaQuartoPorNumero(Short qrtNumero) {
		return this.getAinQuartosDAO().pesquisaQuartoPorNumero(qrtNumero);
	}

	@Override
	public AinQuartos pesquisaQuartoPorLeitoID(String ltoLtoId) {
		return this.getAinQuartosDAO().pesquisaQuartoPorLeitoID(ltoLtoId);
	}

	/**
	 * 
	 * @param seqInternacao
	 * @param codigoObservacaoPacAlta
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('alta','alterar')}")
	public void associarObservacaoPacAlta(Integer seqInternacao, Integer codigoObservacaoPacAlta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		getInternacaoON().associarObservacaoPacAlta(seqInternacao,
				codigoObservacaoPacAlta, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeInternacao() {
		return this.getAinInternacaoDAO().pesquisarInternacoesAvisoSamisUnidadeInternacao();
	}

	@Override
	public Long pesquisarInternacoesAvisoSamisUnidadeInternacaoCount() {
		return this.getAinInternacaoDAO().pesquisarInternacoesAvisoSamisUnidadeInternacaoCount();
	}
	
	@Override
	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeLeito() {
		return this.getAinInternacaoDAO().pesquisarInternacoesAvisoSamisUnidadeLeito();
	}

	@Override
	public Long pesquisarInternacoesAvisoSamisUnidadeLeitoCount() {
		return this.getAinInternacaoDAO().pesquisarInternacoesAvisoSamisUnidadeLeitoCount();
	}
	
	@Override
	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeQuarto() {
		return this.getAinInternacaoDAO().pesquisarInternacoesAvisoSamisUnidadeQuarto();
	}
	
	@Override
	public Long pesquisarInternacoesAvisoSamisUnidadeQuartoCount() {
		return this.getAinInternacaoDAO().pesquisarInternacoesAvisoSamisUnidadeQuartoCount();
	}

	@Override
	public Short recuperarEnderecoCorrespondencia(Integer pacCodigo) {
		return this.getProcedenciaPacientesInternadosRN()
				.recuperarEnderecoCorrespondencia(pacCodigo);
	}

	@Override
	public void inserirMvtoFatorPredisponente(MciMvtoFatorPredisponentes mvto) {
		this.getMvtoFatorPredisponentesRN().inserirMvtoFatorPredisponente(mvto);
	}

	@Override
	public void removerMvtoFatorPredisponente(MciMvtoFatorPredisponentes mvto)
			throws ApplicationBusinessException {
		this.getMvtoFatorPredisponentesRN().removerMvtoFatorPredisponente(mvto);
	}

	@Override
	public DominioSexoDeterminante obterSexoDeterminanteQuarto(Short numero) {
		return this.getAinQuartosDAO().obterSexoDeterminanteQuarto(numero);
	}

	@Override
	public DominioSimNao obterIndConSexoQuarto(Short numero) {
		return this.getAinQuartosDAO().obterIndConSexoQuarto(numero);
	}

	@Override
	public Short obterNumeroQuartoPorLeito(String leitoID) {
		return this.getAinLeitosDAO().obterNumeroQuartoPorLeito(leitoID);
	}

	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}

	protected DarAltaPacienteON getDarAltaPacienteON() {
		return darAltaPacienteON;
	}

	@Override
	public void verificarPermissaoAltaPaciente(Integer intSeq,
			Date intDthrAltaMedica, VerificaPermissaoVO vo) {
		getDarAltaPacienteON()
				.verificarPermissao(intSeq, intDthrAltaMedica, vo);
	}

	protected ProcedenciaPacientesInternadosRN getProcedenciaPacientesInternadosRN() {
		return procedenciaPacientesInternadosRN;
	}

	protected MvtoFatorPredisponentesRN getMvtoFatorPredisponentesRN() {
		return mvtoFatorPredisponentesRN;
	}

	@Override
	@Secure("#{s:hasPermission('relatorio','altasDia')}")
	public List<RelatorioAltasDiaVO> pesquisaAltasDia(Date dataAlta) {
		return getRelatorioAltasDiaON().pesquisa(dataAlta);
	}
	
	@Override
	public List<AltasPorPeriodoVO> pesquisarAltasNoPeriodo(Date dataAltaInicio, Date dataAltaFinal) throws ApplicationBusinessException {
		return getRelatorioAltasPorPeriodoRN().pesquisarAltasPorPeriodo(dataAltaInicio, dataAltaFinal);
	}
	
	protected RelatorioAltasDiaON getRelatorioAltasDiaON() {
		return relatorioAltasDiaON;
	}

	protected RelatorioBaixasDiaON getRelatorioBaixasDiaON() {
		return relatorioBaixasDiaON;
	}

	@Override
	@Secure("#{s:hasPermission('relatorio','pacientesUnidadeFuncional')}")
	public List<PacienteUnidadeFuncionalVO> pesquisaPacientesPorUnidadesFuncionais(
			AghUnidadesFuncionais unidadeFuncional,
			DominioOrdenacaoRelatorioPacientesUnidade ordenacao) {
		return getPacientePorUnidadeON().pesquisa(unidadeFuncional, ordenacao);
	}

	@Override
	@Secure("#{s:hasPermission('relatorio','pacientesUnidadeFuncional')}")
	public List<PacienteUnidadeFuncionalVO> pesquisaPacientesPorUnidadesFuncionaisComAcompanhantes(AghUnidadesFuncionais unidadeFuncional, DominioOrdenacaoRelatorioPacientesUnidade ordenacao)
			throws ApplicationBusinessException {
		return getPacientePorUnidadeON().pesquisaAcompanhantes(unidadeFuncional, ordenacao);
	}

	@Override
	@Secure("#{s:hasPermission('relatorio','pacientesUnidadeFuncional')}")
	public Long pesquisaAtendimentosCount(final AghUnidadesFuncionais unidadeFuncional) {
		return getPacientePorUnidadeON().pesquisaAtendimentosCount(unidadeFuncional);
	}
	
	protected PacientePorUnidadeON getPacientePorUnidadeON() {
		return pacientePorUnidadeON;
	}

	@Override
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	public List<AltasPorUnidadeVO> pesquisaAltasPorUnidade(
			Date dataDeReferencia, DominioGrupoConvenio grupoConvenio,
			Integer codigoUnidadesFuncionais) throws ApplicationBusinessException {
		return getAltasPorUnidadeON().pesquisa(dataDeReferencia, grupoConvenio,
				codigoUnidadesFuncionais);
	}

	protected AltasPorUnidadeON getAltasPorUnidadeON() {
		return altasPorUnidadeON;
	}

	/**
	 * Realiza a pesquisa de pacientes com obito.
	 * 
	 * @param dtInicialReferencia
	 * @param dtFinalReferencia
	 * @param idadeInicial
	 * @param idadeFinal
	 * @param sexo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('relatorio','pacientesComObito')}")
	public List<PacientesComObitoVO> pesquisaPacientesComObito(
			Date dtInicialReferencia, Date dtFinalReferencia,
			Integer idadeInicial, Integer idadeFinal, DominioSexo sexo) {
		return getPacientesComObitoON().pesquisaPacientesComObito(
				dtInicialReferencia, dtFinalReferencia, idadeInicial,
				idadeFinal, sexo);
	}

	protected PacientesComObitoON getPacientesComObitoON() {
		return pacientesComObitoON;
	}

	/**
	 * Realiza a pesquisa de procedencia de pacientes internados
	 * 
	 * @param localizacaoPaciente
	 * @param ordenacaoProcedenciaPacientes
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('relatorio','procedenciaPacientesInternados')}")
	public List<ProcedenciaPacientesInternadosVO> pesquisaProcedenciaPacientesInternados(
			DominioLocalizacaoPaciente localizacaoPaciente,
			DominioOrdenacaoProcedenciaPacientes ordenacaoProcedenciaPacientes) {
		return getProcedenciaPacientesInternadosON().pesquisa(
				localizacaoPaciente, ordenacaoProcedenciaPacientes);
	}

	protected ProcedenciaPacientesInternadosON getProcedenciaPacientesInternadosON() {
		return procedenciaPacientesInternadosON;
	}

	/**
	 * Realiza a pesquisa de pacientes aniversáriantes.
	 * 
	 * @param {Date} dtReferencia
	 * @return {List} PacientesAniversariantesVO
	 */
	@Override
	@Secure("#{s:hasPermission('relatorio','pacientesAniversariantes')}")
	public List<PacientesAniversariantesVO> pesquisaPacientesAniversariantes(
			Date dtReferencia) {
		return getPacientesAniversariantesON()
				.pesquisaPacientesAniversariantes(dtReferencia);
	}

	/**
	 * 
	 * @dbtables AinInternacao select
	 * 
	 * @param intSeq
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoPorSeq(Integer intSeq, Enum... inners) {
		return getInternacaoON().obterInternacaoPorSeq(intSeq, inners);
	}

	protected PacientesAniversariantesON getPacientesAniversariantesON() {
		return pacientesAniversariantesON;
	}

	/**
	 * Método resposável por buscar Instituicoes Hospitalares, conforme a string
	 * passada como parametro, que é comparada com o codigo e a nome da
	 * Instituicao Hospitalar É utilizado pelo converter
	 * AghInstituicoesHospitalaresConverter.
	 * 
	 * @param nome
	 *            ou codigo
	 * @return Lista de AghInstituicoesHospitalares
	 */
	@Override
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(
			Object objPesquisa) {
		return getDarAltaPacienteON()
				.pesquisarInstituicaoHospitalarPorCodigoENome(objPesquisa);
	}

	@Override
	public void insereExtratoLeito(AinLeitos leito, RapServidores digita,
			RapServidores professor, Date dataHoraInternacao,
			Date dataHoraUltimoEvento, AinInternacao internacao, boolean ocupa)
			throws ApplicationBusinessException {
		this.getAtualizaInternacaoRN().insereExtratoLeito(leito, digita,
				professor, dataHoraInternacao, dataHoraUltimoEvento,
				internacao, ocupa);
	}

	@Override
	public void registraExtratoLeito(String leitoID, Integer seqInternacao,
			Date dataHoraAltaMedica, Date dataHoraUltimoEvento,
			DominioTransacaoAltaPaciente transacao) throws ApplicationBusinessException {
		this.getAtualizaInternacaoRN().registraExtratoLeito(leitoID,
				seqInternacao, dataHoraAltaMedica, dataHoraUltimoEvento,
				transacao);
	}

	protected AtualizaInternacaoRN getAtualizaInternacaoRN() {
		return atualizaInternacaoRN;
	}

	@Override
	public Long pesquisarProfissionaisEscalaCount(Short vinculo,
			Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio)
			throws ApplicationBusinessException {
		return getEscalaProfissionaisInternacaoON()
				.pesquisarProfissionaisEscalaCount(vinculo, matricula,
						conselhoProfissional, nomeServidor, siglaEspecialidade,
						codigoConvenio, descricaoConvenio);
	}

	@Override
	@Secure("#{s:hasPermission('escalaProfissionais','pesquisar')}")
	public List<ProfissionaisEscalaIntenacaoVO> pesquisarProfissionaisEscala(
			Short vinculo, Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws ApplicationBusinessException {
		return getEscalaProfissionaisInternacaoON()
				.pesquisarProfissionaisEscala(vinculo, matricula,
						conselhoProfissional, nomeServidor, siglaEspecialidade,
						codigoConvenio, descricaoConvenio, firstResult,
						maxResult, orderProperty, asc);
	}

	/**
	 * Método utilizado para obter uma instância de AinEscalasProfissionalInt
	 * pela chave primária composta.
	 * 
	 * @param pecPreSerMatricula
	 * @param pecPreSerVinCodigo
	 * @param pecCnvCodigo
	 * @param pecPreEspSeq
	 * @param dtInicio
	 * @return
	 */
	@Override
	public AinEscalasProfissionalInt obterProfissionalEscala(
			Integer pecPreSerMatricula, Integer pecPreSerVinCodigo,
			Integer pecCnvCodigo, Integer pecPreEspSeq, Date dtInicio) {
		return getEscalaProfissionaisInternacaoON().obterProfissionalEscala(
				pecPreSerMatricula, pecPreSerVinCodigo, pecCnvCodigo,
				pecPreEspSeq, dtInicio);
	}
	
	@Override
	public List<AinEscalasProfissionalInt> pesquisarProfissionalEscala(
			Integer pecPreSerMatricula, Short pecPreSerVinCodigo,
			Short pecCnvCodigo, Short pecPreEspSeq) {
		return getEscalaProfissionaisInternacaoON().pesquisarProfissionalEscala(pecPreSerMatricula, pecPreSerVinCodigo, pecCnvCodigo, pecPreEspSeq);
	}

	/**
	 * Procura profissional substituto para os parâmetros fornecidos.<br>
	 * Substitutos são os profissionais que tem escala para a especialidade e
	 * convênio no dia fornecido.
	 * 
	 * @param especialidadeId
	 *            id da especialidade
	 * @param convenioId
	 *            id do convênio
	 * @param data
	 * @return matriculas(RapServidores) dos profissionais substitutos
	 * @throws ApplicationBusinessException
	 */
	@Override
	public List<RapServidores> pesquisarProfissionaisSubstitutos(
			Short especialidadeId, Short convenioId, Date data,
			Object substitutoPesquisaLOV) throws ApplicationBusinessException {
		return getEscalaProfissionaisInternacaoON()
				.pesquisarProfissionaisSubstitutos(especialidadeId, convenioId,
						data, substitutoPesquisaLOV);
	}

	/**
	 * Método utilizado para adicionar uma nova escala de um profissional da
	 * internação.
	 * 
	 * @param ainEscalasProfissionalInt
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('escalaProfissionais','alterar')}")
	public void incluirEscala(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws BaseException {
		getEscalaProfissionaisInternacaoON().incluirEscala(
				ainEscalasProfissionalInt);
	}

	/**
	 * Método utilizado para alterar uma escala de um profissional da
	 * internação.
	 * 
	 * @param ainEscalasProfissionalInt
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('escalaProfissionais','alterar')}")
	public void alterarEscala(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws BaseException {
		getEscalaProfissionaisInternacaoON().alterarEscala(
				ainEscalasProfissionalInt);
	}

	@Override
	public Long pesquisarEscalaCount(Short vinculo, Integer matricula,
			Integer seqEspecialidade, Short codigoConvenio, Date dataInicio,
			Date dataFim) {
		return getEscalaProfissionaisInternacaoON().pesquisarEscalaCount(
				vinculo, matricula, seqEspecialidade, codigoConvenio,
				dataInicio, dataFim);
	}

	@Override
	@Secure("#{s:hasPermission('escalaProfissionais','pesquisar')}")
	public List<EscalaIntenacaoVO> montarEscalaVO(Short vinculo,
			Integer matricula, Integer seqEspecialidade, Short codigoConvenio,
			Date dataInicio, Date dataFim, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc)
			throws ApplicationBusinessException {
		return getEscalaProfissionaisInternacaoON().montarEscalaVO(vinculo,
				matricula, seqEspecialidade, codigoConvenio, dataInicio,
				dataFim, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public void verificaPacienteInternado(Integer intSeq)
			throws ApplicationBusinessException {
		getEstornarAltaPacienteON().verificaPacienteInternado(intSeq);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisaInternacoesParaEstornarAltaPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer prontuario) {

		return getEstornarAltaPacienteON()
				.pesquisaInternacoesParaEstornarAltaPaciente(firstResult,
						maxResult, orderProperty, asc, prontuario);
	}

	@Override
	public Long pesquisaInternacoesParaEstornarAltaPacienteCount(
			Integer prontuario) {
		return getEstornarAltaPacienteON()
				.pesquisaInternacoesParaEstornarAltaPacienteCount(prontuario);
	}

	protected EscalaProfissionaisInternacaoON getEscalaProfissionaisInternacaoON() {
		return escalaProfissionaisInternacaoON;
	}

	protected EstornarAltaPacienteON getEstornarAltaPacienteON() {
		return estornarAltaPacienteON;
	}

	/**
	 * Método usado para remover os acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('acompanhante','excluir')}")
	public void removerAcompanhantes(
			List<AinAcompanhantesInternacao> acompanhantesInternacao)
			throws ApplicationBusinessException {
		getAtualizaAcompanhantesInternacaoON().removerAcompanhantes(
				acompanhantesInternacao);
	}

	protected AtualizaAcompanhantesInternacaoON getAtualizaAcompanhantesInternacaoON() {
		return atualizaAcompanhantesInternacaoON;
	}

	/**
	 * ORADB: procedure AINP_ENFORCE_INT_RULES (p_event = 'DELETE')
	 * 
	 * @param intSaved
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void enforceDelete(AinInternacao intSaved, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		getInternacaoRN().enforceDelete(intSaved, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	@Override
	public void validarAtendimentoUrgenciaAntesAtualizar(final AinAtendimentosUrgencia atendimentoUrgencia, 
			final AinAtendimentosUrgencia atendimentoUrgenciaAntigo, String usuario) throws ApplicationBusinessException{
		getInternacaoRN().validarAtendimentoUrgenciaAntesAtualizar(atendimentoUrgencia, atendimentoUrgenciaAntigo, usuario);
	}
	
	@Override
	public void geraMovimentoAtendimento(final AinAtendimentosUrgencia atendimentoUrgencia, final Integer codigoMovimentacao,
			final Date dataHoraLancamento) throws ApplicationBusinessException{
		getInternacaoRN().geraMovimentoAtendimento(atendimentoUrgencia, codigoMovimentacao, dataHoraLancamento);
	}
	
	@Override
	public void desatacharObjetoInternacao(AinInternacao internacao) {
		getAinInternacaoDAO().desatachar(internacao);
	}

	/**
	 * ORADB: Trigger AINT_INT_ARD
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void posDelete(AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		getInternacaoRN().posDelete(internacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * Obtém o convênio e o plano que permite internação
	 * 
	 * @param cnvCodigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public FatConvenioSaudePlano obterConvenioPlanoInternacao(Short cnvCodigo) {
		return getCadastroInternacaoON()
				.obterConvenioPlanoInternacao(cnvCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AinAtendimentosUrgencia obterAtendimentoUrgencia(
			Integer ainAtendimentoUrgenciaSeq) {
		return getCadastroInternacaoON().obterAtendimentoUrgencia(
				ainAtendimentoUrgenciaSeq);
	}

	/**
	 * Método que obtém a Origem de internação para quando o paciente já se
	 * encontra em atendimento de emergência.
	 * 
	 * @param unfSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AghOrigemEventos obterOrigemInternacaoAtendimentoEmergencia(
			Short unfSeq) throws ApplicationBusinessException {
		return getCadastroInternacaoON()
				.obterOrigemInternacaoAtendimentoEmergencia(unfSeq);
	}

	/**
	 * Método que obtem um procedimentoHospitalar pelo seu id
	 * 
	 * @param phoSeq
	 *            , seq
	 * @return FatItensProcedHospitalar
	 */
	@Override
	public FatItensProcedHospitalar obterItemProcedimentoHospitalar(
			Short phoSeq, Integer seq) {
		return getCadastroInternacaoON().obterItemProcedimentoHospitalar(
				phoSeq, seq);
	}

	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public FatConvenioSaudePlano obterConvenioSaudePlano(
			FatConvenioSaudePlanoId id) {
		return getCadastroInternacaoON().obterConvenioSaudePlano(id);
	}

	/**
	 * Método responsável pela persistência de uma internação.
	 * 
	 * @param internacao
	 *            , listaResponsaveis, listaResponsaveisExcluidos
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','alterar')}")
	public AinInternacao persistirInternacao(AinInternacao internacao,
			List<AinCidsInternacao> cidsInternacao,
			List<AinCidsInternacao> cidsInternacaoExcluidos,
			List<AinResponsaveisPaciente> listaResponsaveis,
			List<AinResponsaveisPaciente> listaResponsaveisExcluidos, 
			String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario)
			throws BaseException {
		return getCadastroInternacaoON().persistirInternacao(internacao,
				cidsInternacao, cidsInternacaoExcluidos, listaResponsaveis,
				listaResponsaveisExcluidos, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
	}

	/**
	 * Método que valida quando o CNRAC deve ser informado para paciente com
	 * endereço em outro estado
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean validarPacienteOutroEstado(AipPacientes paciente,
			Integer iphSeq, Short iphPhoSeq) throws ApplicationBusinessException {
		return getCadastroInternacaoON().validarPacienteOutroEstado(paciente,
				iphSeq, iphPhoSeq);
	}

	/**
	 * Este método realiza a pesquisa de Convênios para a tela de internação
	 * 
	 * @param strParam
	 * @return listaLOV
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoInternacao(
			Object strParam) {
		return getCadastroInternacaoON().pesquisarConvenioPlanoInternacao(
				strParam);
	}

	/**
	 * Pesquisa de projeto de pesquisa por descrição ou código
	 * 
	 * Pesquisa utilizada na tela de internação.
	 * 
	 * @param strPesquisa
	 *            , idadePaciente
	 */
	@Override
	@Secure("#{s:hasPermission('projetoPesquisa','pesquisar')}")
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(
			String strPesquisa, Integer codigoPaciente) {
		return getCadastroInternacaoON().pesquisarProjetosPesquisaInternacao(
				strPesquisa, codigoPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('caraterInternacao','pesquisar')}")
	public List<AinTiposCaraterInternacao> pesquisarCaraterInternacao(
			String strPesquisa) {
		return getCadastroInternacaoON()
				.pesquisarCaraterInternacao(strPesquisa);
	}

	@Override
	public AghProfEspecialidades sugereProfessorEspecialidade(Short espSeq,
			Short cnvCodigo) {
		return getCadastroInternacaoON().sugereProfessorEspecialidade(espSeq,
				cnvCodigo);
	}

	/**
	 * ORADB ainp_verifica_cirurgia Método que obtém o procedimento cirúrgico de
	 * um paciente, caso haja algum no período de até 7 dias antes da data de
	 * internação.
	 * 
	 * @param pacCodigo
	 * @param dtInternacao
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('procedimentoCirurgico','pesquisar')}")
	public MbcCirurgias obterProcedimentoCirurgicoInternacao(Integer pacCodigo,
			Date dtInternacao) {
		return getCadastroInternacaoON().obterProcedimentoCirurgicoInternacao(
				pacCodigo, dtInternacao);
	}

	/**
	 * Método delegado para a RN de Solicitação Internação
	 * 
	 * @param cidSeq
	 * @return listaCodTabelas
	 */
	@Override
	public List<Long> pesquisarFatAssociacaoProcedimentos(Integer cidSeq) {
		return getCadastroInternacaoON().pesquisarFatAssociacaoProcedimentos(
				cidSeq);
	}

	/**
	 * Método que obtém a UF correspondente ao HU
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public String obterUfSedeHU() throws ApplicationBusinessException {
		return getCadastroInternacaoON().obterUfSedeHU();
	}

	/**
	 * Este método é chamado diretamente da Controller para verificar se a
	 * mensagem de confirmação deve ser exibida ao usuário
	 * 
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @param especialidade
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean consistirClinicaMensagemConfirmacao(AinLeitos leito,
			AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional,
			AghEspecialidades especialidade) throws ApplicationBusinessException {
		return getCadastroInternacaoON().consistirClinicaMensagemConfirmacao(
				leito, quarto, unidadeFuncional, especialidade);
	}

	/**
	 * Este método é chamado diretamente da Controller para verificar se a
	 * mensagem de confirmação deve ser exibida ao usuário (somente para
	 * especialidade este)
	 * 
	 * @param internacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean consistirEspecialidadeMensagemConfirmacao(AinLeitos leito,
			AghEspecialidades especialidade) throws ApplicationBusinessException {
		return getCadastroInternacaoON()
				.consistirEspecialidadeMensagemConfirmacao(leito, especialidade);
	}

	/**
	 * Regra que verifica se deve ser informado o número CERIH para a internação
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean verificarInformarNumeroCERIH(Integer iphSeq,
			Short iphPhoSeq, Short cnvCodigo, Integer seqInternacao)
			throws BaseException {
		return getCadastroInternacaoON().verificarInformarNumeroCERIH(iphSeq,
				iphPhoSeq, cnvCodigo, seqInternacao);
	}

	/**
	 * Consiste para que seja informada a acomodação autorizada para internações
	 * de convênio (AGUARDANDO MELHOR LOCAL PARA CHAMADA DESTE MÉTODO)
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean verificarAcomodacaoInternacaoConvenio(
			AinInternacao internacao) {
		return getCadastroInternacaoON().verificarAcomodacaoInternacaoConvenio(
				internacao);
	}

	/**
	 * ORADB ainp_consiste_matr_conv Verifica se deve ser informada a matrícula
	 * do convênio do paciente
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public boolean verificarConvenioSaudePaciente(AipPacientes paciente,
			Short cnvCodigo) {
		return getCadastroInternacaoON().verificarConvenioSaudePaciente(
				paciente, cnvCodigo);
	}

	/**
	 * Método para verificar se o convenio é um convenio SUS. Esse método é a
	 * implementação da procedura AINC_CONVENIO_SUS do form AINF_INTERNAR_PAC.
	 * 
	 * @param codigo
	 *            do convenio
	 * @return true/false
	 */
	@Override
	public Boolean verificarConvenioSus(Short codigoConvenio) {
		return getRelatorioInternacaoON().verificarConvenioSus(codigoConvenio);
	}

	/**
	 * Método para executar as regras de triggers de AIN_CIDS_INTERNACAO.
	 * 
	 * ORADB Trigger AINT_CDI_ASI
	 * 
	 * @param cidInternacao
	 * @param operacao
	 * @throws BaseException
	 */
	@Override
	public void executarTriggersCid(AinCidsInternacao cidInternacao,
			DominioOperacoesJournal operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		getInternacaoRN().executarTriggersCid(cidInternacao, operacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * ORADB ainp_sugere_prof_esp
	 */
	@Override
	public AghProfEspecialidades verificarEscalaProfessor(Short espSeq,
			Short cnvCodigo) {
		return getInternacaoRN().verificarEscalaProfessor(espSeq, cnvCodigo);
	}

	/**
	 * Método para chamar function do BD oracle.
	 * 
	 * ORADB Procedure FATP_CTH_PERM_TRC_CNV_AGHU
	 */
	@Override
	public ValidaContaTrocaConvenioVO validarContaTrocaConvenio(
			final Integer internacaoSeq, final Date dataInternacao,
			final Short cnvCodigo, final Integer phiSeq)
			throws ApplicationBusinessException {
		return getInternacaoRN().validarContaTrocaConvenio(internacaoSeq,
				dataInternacao, cnvCodigo, phiSeq);
	}

	@Override
	public AinInternacao obterInternacaoAnterior(Integer internacao){
		return getInternacaoRN().obterInternacaoAnterior(internacao);
	}

	/**
	 * Método para chamar function do BD oracle.
	 * 
	 * ORADB Procedure CONV.FFC_CALCULO_CONTA
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Integer calcularConta(final Integer conta, final Short cnvCodigo,
			final String desconto) throws ApplicationBusinessException {
		return getInternacaoRN().calcularConta(conta, cnvCodigo, desconto);
	}

	/**
	 * Método para atualizar o convênio/plano dos exames
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_CNV_EXME
	 * 
	 * @param seqAtendimento
	 * @param codigoConvenio
	 * @param seqConvenioSaudePlano
	 * @param data
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void atualizarConvenioPlanoExames(Integer seqAtendimento,
			Short codigoConvenio, Byte seqConvenioSaudePlano, Date data, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		getValidaInternacaoRN().atualizarConvenioPlanoExames(seqAtendimento,
				codigoConvenio, seqConvenioSaudePlano, data, nomeMicrocomputador);
	}

	/**
	 * Método para atualizar o convênio/plano das cirurgias
	 * 
	 * ORADB: Procedure AINK_INT_RN.RN_INTP_ATU_CNV_CIRG
	 * 
	 * @param seqAtendimento
	 * @param codigoConvenio
	 * @param seqConvenioSaudePlano
	 * @param data
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void atualizarConvenioPlanoCirurgias(Integer seqAtendimento,
			Short codigoConvenio, Byte seqConvenioSaudePlano, Date data)
			throws ApplicationBusinessException {
		getValidaInternacaoRN().atualizarConvenioPlanoCirurgias(seqAtendimento,
				codigoConvenio, seqConvenioSaudePlano, data);
	}

	@Override
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(
			String strPesquisa) {
		return getCadastroInternacaoON().pesquisarProjetosPesquisaInternacao(
				strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public ConvenioPlanoVO obterConvenioPlanoVO(Short cnvCodigo, Byte seq) {
		return getCadastroInternacaoON().obterConvenioPlanoVO(cnvCodigo, seq);
	}

	/**
	 * Método que atualiza a conta hospitalar da internação quando a acomodação
	 * for informada
	 * 
	 * @param contaHospitalar
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public void atualizarContaHospitalar(FatContasHospitalares contaHospitalar) {
		getCadastroInternacaoON().atualizarContaHospitalar(contaHospitalar);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','alterar') && s:hasPermission('contaHospitalar','alterar')}")
	public AinInternacao alterarConvenioInternacao(AinInternacao internacao,
			FatContasHospitalares contaHospitalar,
			final Integer seqContaHospitalarOld, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean encerraConta) throws BaseException {
		return getCadastroInternacaoON().alterarConvenioInternacao(internacao,
				contaHospitalar, seqContaHospitalarOld, nomeMicrocomputador, dataFimVinculoServidor, encerraConta);
	}

	@Override
	public void substituirPacienteAtendimentoUrgencia(
			AipPacientes pacienteOrigem, AipPacientes pacienteDestino,
			AinAtendimentosUrgencia atendimentoUrgencia, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getSubstituirProntuarioAtendimentoRN()
				.substituirPacienteAtendimentoUrgencia(pacienteOrigem,
						pacienteDestino, atendimentoUrgencia, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
	}

	/**
	 * Obtem o atendimento de urgencia mais recente do paciente solicitado.
	 * 
	 * @param pacCodigo
	 * @return AinAtendimentosUrgencia
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AinAtendimentosUrgencia obterAtendUrgencia(AipPacientes paciente)
			throws ApplicationBusinessException {
		return getAltaPacienteObservacaoON().obterAtendUrgencia(paciente);
	}

	/**
	 * Obtem o atendimento de urgencia mais recente do paciente solicitado.
	 * 
	 * @param pacCodigo
	 * @return AinAtendimentosUrgencia
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validaDataAlta(Date dtAlta, Date dtAtendimento)
			throws ApplicationBusinessException {
		getAltaPacienteObservacaoON().validaDataAlta(dtAlta, dtAtendimento);
	}

	protected AltaPacienteObservacaoON getAltaPacienteObservacaoON() {
		return altaPacienteObservacaoON;
	}

	/**
	 * Obtem lista de crachás por acompanhante.
	 * 
	 * @param acompanhanteInternacao
	 * @return Lista de crachás.
	 */
	@Override
	@Secure("#{s:hasPermission('acompanhante','pesquisar')}")
	public List<AinCrachaAcompanhantes> obterCrachasAcompanhantes(
			AinAcompanhantesInternacao acompanhanteInternacao) {

		return getAtualizaAcompanhantesInternacaoON()
				.obterCrachasAcompanhantes(acompanhanteInternacao);
	}

	/**
	 * Sugere número do crachá para acompanhantes, que não tenham crédito refeitório.
	 * 
	 * @param acompanhanteInternacao
	 * @return Número do crachá.
	 * @throws ApplicationBusinessException 
	 */
	@Override
	public Long sugereNumeroDoCracha(final String usuario) throws ApplicationBusinessException {
		return getAtualizaAcompanhantesInternacaoON().sugereNumeroDoCracha(usuario);
	}
	
	/**
	 * Valida datas.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validaCamposAtualizaAcompanhantes(Date dtInicio, Date dtFim,
			String nomeAcomp) throws ApplicationBusinessException {
		getAtualizaAcompanhantesInternacaoON().validaCampos(dtInicio, dtFim,
				nomeAcomp);
	}

	/**
	 * Método usado para persistir os cracha dos acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('acompanhante','alterar')}")
	public void persistirCrachaAcompanhantes(
			List<AinCrachaAcompanhantes> crachasAcompanhantes,
			AinAcompanhantesInternacao acompanhanteInternacao,
			List<AinAcompanhantesInternacao> acompanhantesInternacaos,
			AinInternacao internacao) throws ApplicationBusinessException {
		getAtualizaAcompanhantesInternacaoON().persistirCrachaAcompanhantes(
				crachasAcompanhantes, acompanhanteInternacao,
				acompanhantesInternacaos, internacao);
	}

	/**
	 * Método usado para persistir os acompanhantes.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('acompanhante','alterar')}")
	public void persistirAcompanhantes(
			List<AinAcompanhantesInternacao> acompanhantesInternacaos,
			AinInternacao internacao) throws ApplicationBusinessException {
		getAtualizaAcompanhantesInternacaoON().persistirAcompanhantes(
				acompanhantesInternacaos, internacao);
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_SEXO_QUARTO
	 * 
	 * Atualiza Sexo Quarto
	 * 
	 * Coloca null no sexo de ocupação do quarto quando este se tornar liberado
	 * após a saída de um paciente.
	 */
	@Override
	public void atualizarSexoQuarto(String paramLeitoId, Short paramNumeroQuarto)
			throws ApplicationBusinessException {
		this.getAtualizaInternacaoRN().atualizarSexoQuarto(paramLeitoId,
				paramNumeroQuarto);
	}

	/**
	 * ORADB Procedure AINK_CENSO.LOCAL_ORIGEM
	 * 
	 * Obtem local de origem.
	 */
	@Override
	public String obtemLocalOrigem(Integer internacaoSeq, Date dataHora) {
		return this.getCensoRN().obtemLocalOrigem(internacaoSeq, dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL .
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date obtemDthrFinal(Integer internacaoSeq, Date dataHora) {
		return this.getCensoRN().obtemDthrFinal(internacaoSeq, dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO.UNF_SEQ_ORIGEM
	 * 
	 * Obtem local de origem (sequence).
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Short obtemLocalOrigemSeq(Integer internacaoSeq, Date dataHora) {
		return this.getCensoRN().obtemLocalOrigemSeq(internacaoSeq, dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO.LOCAL_DESTINO
	 * 
	 * Obtem local de destino.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public String obtemLocalDestino(Integer internacaoSeq, Date dataHora) {
		return this.getCensoRN().obtemLocalDestino(internacaoSeq, dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL_T .
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date obtemDthrFinalT(Integer internacaoSeq, Date dataHora) {
		return this.getCensoRN().obtemDthrFinalT(internacaoSeq, dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO.DTHR_FINAL_LTO
	 * 
	 * Obtem data final leito.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date obtemDthrFinalLeito(String leitoId, Date dataHora) {
		return this.getCensoRN().obtemDthrFinalLeito(leitoId, dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL_NOVA .
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date obtemDthrFinalNova(Integer atendimentoUrgenciaSeq,
			Date dataHora, Integer movimentoSeq, Date criadoEm) {
		return this.getCensoRN().obtemDthrFinalNova(atendimentoUrgenciaSeq,
				dataHora, movimentoSeq, criadoEm);
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.LOCAL_ORIGEM
	 * 
	 * Obtem local de origem.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public String obtemLocalOrigemUrgencia(Integer atendimentoUrgSeq,
			Date dataHora) {
		return this.getCensoRN().obtemLocalOrigemUrgencia(atendimentoUrgSeq,
				dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL .
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date obtemDthrFinalUrgencia(Integer atendimentoUrgenciaSeq,
			Date dataHora) {
		return this.getCensoRN().obtemDthrFinalUrgencia(atendimentoUrgenciaSeq,
				dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.DTHR_FINAL_T .
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date obtemDthrFinalTUrgencia(Integer atendimentoUrgenciaSeq,
			Date dataHora) {
		return this.getCensoRN().obtemDthrFinalTUrgencia(
				atendimentoUrgenciaSeq, dataHora);
	}

	/**
	 * ORADB Procedure AINK_CENSO_ATU.LOCAL_DESTINO
	 * 
	 * Obtem local de destino.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public String obtemLocalDestinoUrgencia(Integer atendimentoUrgenciaSeq,
			Date dataHora) {
		return this.getCensoRN().obtemLocalDestinoUrgencia(
				atendimentoUrgenciaSeq, dataHora);
	}

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_FIN
	 * 
	 * Obtem data final.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date buscaDthrFinalInternacao(Integer intSeq) {
		return this.getCensoRN().buscaDthrFinal(intSeq);
	}

	/**
	 * ORADB Procedure AINC_BUSCA_ORIG_MOV
	 * 
	 * Obtem local de origem.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public String obtemOrigemMovimentacao(Integer intSeq) {
		return this.getCensoRN().obtemOrigemMovimentacao(intSeq);
	}

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_LTO
	 * 
	 * Obtem data final leito.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date buscaDthrLeito(String leitoId) {
		return this.getCensoRN().buscaDthrLeito(leitoId);
	}

	/**
	 * ORADB Procedure AINC_BUSCA_DTHR_ATU
	 * 
	 * Obtem data atendimento de urgencia.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date buscaDthrAtendimentoUrgencia(Integer atendimentoUrgenciaSeq) {
		return this.getCensoRN().buscaDthrAtendimentoUrgencia(
				atendimentoUrgenciaSeq);
	}

	/**
	 * ORADB Procedure AINC_BUSCA_ORIG_ATU
	 * 
	 * Obtem local de origem do atendimento de urgencia.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Override
	public String obtemOrigemAtendimentoUrgencia(Integer atendimentoUrgenciaSeq) {
		return this.getCensoRN().obtemOrigemAtendimentoUrgencia(
				atendimentoUrgenciaSeq);
	}

	protected CensoRN getCensoRN() {
		return censoRN;
	}

	/**
	 * Método para retornar o código do tipo de movimento do leito
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.COD_BLOQUEIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	@Override
	public Short obterCodigoBloqueioLB(String leitoId, Date data) {
		return this.getLeitoBloqueioRN().obterCodigoBloqueio(leitoId, data);
	}

	/**
	 * Método para retornar a situação do leito do paciente
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.IND_BLOQUEIO_PACIENTE
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	@Override
	public DominioSimNao obterSituacaoPacienteLB(String leitoId, Date data) {
		return this.getLeitoBloqueioRN().obterSituacaoPaciente(leitoId, data);
	}

	/**
	 * Método para retornar a data de início
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.DTHR_INICIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	@Override
	public Date obterDataInicialLB(String leitoId, Date data) {
		return this.getLeitoBloqueioRN().obterDataInicial(leitoId, data);
	}

	/**
	 * Método para retornar a data final do lançamento
	 * 
	 * ORADB Function AINK_LEITOS_BLQ.DTHR_FINAL_L
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	@Override
	public Date obterDataFinalLancamentoLB(String leitoId, Date data) {
		return this.getLeitoBloqueioRN()
				.obterDataFinalLancamento(leitoId, data);
	}

	protected LeitoBloqueioRN getLeitoBloqueioRN() {
		return leitoBloqueioRN;
	}

	/**
	 * Método para retornar a situação do leito do paciente
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.IND_BLOQUEIO_PACIENTE
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	@Override
	public DominioSimNao obterSituacaoPacienteLD(String leitoId, Date data) {
		return this.getLeitoDesativadoRN().obterSituacaoPaciente(leitoId, data);
	}

	/**
	 * Método para retornar a data de início
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.DTHR_INICIO
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	@Override
	public Date obterDataInicialLD(String leitoId, Date data) {
		return this.getLeitoDesativadoRN().obterDataInicial(leitoId, data);
	}

	/**
	 * Método para retornar a data final do lançamento
	 * 
	 * ORADB Function AINK_LEITOS_DESAT.DTHR_FINAL_L
	 * 
	 * @param leitoId
	 * @param data
	 * @return
	 */
	@Override
	public Date obterDataFinalLancamentoLD(String leitoId, Date data) {
		return this.getLeitoDesativadoRN().obterDataFinalLancamento(leitoId,
				data);
	}
	
	public void verificarEscalaProfissional(String nomeMicrocomputador, final RapServidores servidorLogado, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		this.getVerificaEscalaProfissionalSchedulerRN().verificarEscalaProfissional(nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);		
	}

	

	@Override
	public void testVerificarEscalaProfissional(Date date, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getVerificaEscalaProfissionalSchedulerRN()
				.testVerificarEscalaProfissional(date, nomeMicrocomputador, dataFimVinculoServidor);
	}

	protected VerificaEscalaProfissionalSchedulerRN getVerificaEscalaProfissionalSchedulerRN() {
		return verificaEscalaProfissionalSchedulerRN;
	}

	protected LeitoDesativadoRN getLeitoDesativadoRN() {
		return leitoDesativadoRN;
	}

	/**
	 * Método para chamada da impressão do Boletim de Internação. Este método é
	 * a implementação do procedure AINP_CHAMA_BOLETIM do form
	 * AINF_INTERNAR_PAC.
	 * 
	 * @param codigo
	 *            da internação
	 * @return RelatorioBoletimInternacaoVO com todos campos setados de acordo
	 *         com busca para geração do relatório
	 */
	@Override
	@Secure("#{s:hasPermission('relatorio','internacao')}")
	public RelatorioBoletimInternacaoVO imprimirRelatorioBoletimInternacao(
			Integer seqInternacao) throws ApplicationBusinessException {
		return this.getRelatorioInternacaoON()
				.imprimirRelatorioBoletimInternacao(seqInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('relatorio','solicitacaoInternacao')}")
	public List<RelatorioSolicitacaoInternacaoVO> obterSolicitacoesInternacao(
			Date criadoEm,
			DominioSituacaoSolicitacaoInternacao indSitSolicInternacao,
			AghClinicas clinica, Date dtPrevistaInternacao,
			AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio)
			throws ApplicationBusinessException {
		return this.getRelatorioSolicitacaoInternacaoON()
				.obterSolicitacoesInternacao(criadoEm, indSitSolicInternacao,
						clinica, dtPrevistaInternacao, paciente, crm,
						especialidade, convenio);
	}

	@Override
	@Secure("#{s:hasPermission('relatorio','responsaveisPaciente')}")
	public List<ResponsaveisPacienteVO> obterRelatorioResponsaveisPaciente(
			Integer intSeq) throws ApplicationBusinessException {
		return this.getRelatorioResponsaveisPacienteON()
				.obterRelatorioResponsaveisPaciente(intSeq);
	}

	protected RelatorioSolicitacaoInternacaoON getRelatorioSolicitacaoInternacaoON() {
		return relatorioSolicitacaoInternacaoON;
	}

	protected RelatorioResponsaveisPacienteON getRelatorioResponsaveisPacienteON() {
		return relatorioResponsaveisPacienteON;
	}

	@Override
	@Secure("#{s:hasPermission('relatorio','localizadorPaciente')}")
	public Map<String, Object> getParematrosRelatorioLocalizadorPaciente(
			Integer codPaciente) {
		return getRelatorioLocalizadorPacienteON().getParematros(codPaciente);
	}

	protected RelatorioLocalizadorPacienteON getRelatorioLocalizadorPacienteON() {
		return relatorioLocalizadorPacienteON;
	}

	@Override
	public AinLeitos obterLeitoPorId(String leitoID) {
		return this.getAinLeitosDAO().obterPorChavePrimaria(leitoID);
	}

	@Override
	public AinQuartos obterQuartoPorId(Short numero) {
		return this.getAinQuartosDAO().obterPorChavePrimaria(numero);
	}

	@Override
	public Boolean habilitarDadosControle(final Integer cPacCodigo,
			final Integer atdSeq) {
		return this.getAinInternacaoDAO().habilitarDadosControle(cPacCodigo,
				atdSeq);
	}

	@Override
	public void evict(BaseEntity entity) throws HibernateException {
		this.flush();
		super.evict(entity);
	}

	@Override
	public List<AinQuartos> pesquisaQuartos(String strPesquisa) {
		return this.getAinQuartosDAO().pesquisarQuartos(strPesquisa);
	}

	@Override
	public Long listarQuartosPorUnidadeFuncionalEDescricaoCount(
			String descricao, Short unfSeq, Boolean orderAsc) {
		return getAinQuartosDAO()
				.listarQuartosPorUnidadeFuncionalEDescricaoCount(descricao,
						unfSeq, orderAsc);
	}

	@Override
	public AinInternacao obrterInternacaoPorPacienteInternado(
			final Integer pacCodigo) {
		return getAinInternacaoDAO().obrterInternacaoPorPacienteInternado(
				pacCodigo);
	}

	@Override
	public List<AinInternacao> pesquisarInternacaoIndependenteEspecialidade(
			Integer pacCodigo, Date dtConsulta, Date dataLimite, Short pgdSeq) {
		return getAinInternacaoDAO()
				.pesquisarInternacaoIndependenteEspecialidade(pacCodigo,
						dtConsulta, dataLimite, pgdSeq);
	}

	@Override
	public List<AinInternacao> pesquisarInternacaoMesmaEspecialidade(
			Integer codigo, Date dtConsulta, Date dataLimite, Short pgdSeq) {
		return getAinInternacaoDAO()
				.pesquisarInternacaoIndependenteEspecialidade(codigo,
						dtConsulta, dataLimite, pgdSeq);
	}

	@Override
	public List<AinInternacao> pesquisarInternacaoOutrasEspecialidades(
			Integer pacCodigo, Date dtConsulta, Short espSeqGen,
			Date dataLimite, Short pgdSeq) {
		return getAinInternacaoDAO().pesquisarInternacaoOutrasEspecialidades(
				pacCodigo, dtConsulta, espSeqGen, dataLimite, pgdSeq);
	}

	@Override
	public List<AinInternacao> pesquisarInternacaoEspecialidadeDiferente(
			Integer pacCodigo, Date dtConsulta, Short espSeqGen,
			Date dataLimite, Short pgdSeq) {
		return getAinInternacaoDAO().pesquisarInternacaoEspecialidadeDiferente(
				pacCodigo, dtConsulta, espSeqGen, dataLimite, pgdSeq);
	}



	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}
	
	protected AinSolicitacoesInternacaoDAO getAinSolicitacoesInternacaoDAO() {
		return ainSolicitacoesInternacaoDAO;
	}

	@Override
	public AinAtendimentosUrgencia obterUltimoAtendimentosUrgencia(
			Integer aipPacientesCodigo) {
		return this.getAinAtendimentosUrgenciaDAO()
				.obterUltimoAtendimentosUrgencia(aipPacientesCodigo);
	}

	@Override
	public List<AinAtendimentosUrgencia> pesquisarAinAtendimentosUrgencia(
			Integer pacCodigo) {
		return this.getAinAtendimentosUrgenciaDAO()
				.pesquisarAinAtendimentosUrgencia(pacCodigo);
	}

	@Override
	public List executarCursorAtu(Integer atuSeq) {
		return this.getAinAtendimentosUrgenciaDAO().executarCursorAtu(atuSeq);
	}

	@Override
	public List<Date> executarCursorAtu2(Integer atuSeq) {
		return this.getAinAtendimentosUrgenciaDAO().executarCursorAtu2(atuSeq);
	}

	@Override
	public List executarCursorUrgencia(Integer atuSeq) {
		return this.getAinAtendimentosUrgenciaDAO().executarCursorUrgencia(
				atuSeq);
	}

	@Override
	@BypassInactiveModule
	public AinAtendimentosUrgencia obterAinAtendimentosUrgenciaPorChavePrimaria(
			Integer seq) {
		return this.getAinAtendimentosUrgenciaDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void persistirAinExtratoLeitos(AinExtratoLeitos ainExtratoLeitos) {
		this.getAinExtratoLeitosDAO().persistir(ainExtratoLeitos);
	}

	@Override
	public List<AinExtratoLeitos> listarExtratosLeitosPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAinExtratoLeitosDAO()
				.listarExtratosLeitosPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List executarCursorInt(Integer intSeq) {
		return this.getAinInternacaoDAO().executarCursorInt(intSeq);
	}

	@Override
	public List<Date> executarCursorInt2(Integer intSeq) {
		return this.getAinInternacaoDAO().executarCursorInt2(intSeq);
	}

	@Override
	public List<AinInternacao> pesquisarInternacoesPorPaciente(
			AipPacientes paciente) {
		return this.getAinInternacaoDAO().pesquisarInternacoesPorPaciente(
				paciente);
	}
	
	@Override
	public List<AinInternacao> pesquisarInternacoesPorPacientePOL(
			AipPacientes paciente) {
		return this.getAinInternacaoDAO().pesquisarInternacoesPorPacientePOL(
				paciente);
	}
	
	@Override
	public Long pesquisarInternacoesPorPacienteCount(
			AipPacientes paciente) {
		return this.getAinInternacaoDAO().pesquisarInternacoesPorPacienteCount(
				paciente);
	}

	@Override
	public List<AinInternacao> pesquisarAinInternacao(Integer pacCodigo) {
		return this.getAinInternacaoDAO().pesquisarAinInternacao(pacCodigo);
	}

	@Override
	public AinQuartos obterAinQuartosPorChavePrimaria(Short numeroQuarto) {
		return this.getAinQuartosDAO().obterPorChavePrimaria(numeroQuarto);
	}

	@Override
	public AinLeitos obterAinLeitosPorChavePrimaria(String leitoID) {
		return this.getAinLeitosDAO().obterPorChavePrimaria(leitoID);
	}

	@Override
	public List<AinSolicitacoesInternacao> listarSolicitacoesInternacaoPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAinSolicitacoesInternacaoDAO()
				.listarSolicitacoesInternacaoPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<AhdHospitaisDia> listarHospitaisDiaPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAghAhdHospitaisDiaDAO()
				.listarHospitaisDiaPorCodigoPaciente(pacCodigo);
	}

	@Override
	public AhdHospitaisDia obterUltimoAtendimentoHospitaisDia(
			Integer aipPacientesCodigo) {
		return this.getAghAhdHospitaisDiaDAO().obterUltimoAtendimentoHospitaisDia(
				aipPacientesCodigo);
	}

	@Override
	@BypassInactiveModule
	public AhdHospitaisDia obterAhdHospitaisDiaPorChavePrimaria(Integer seq) {
		return this.getAghAhdHospitaisDiaDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List executarCursorHod(Integer hodSeq) {
		return this.getAghAhdHospitaisDiaDAO().executarCursorHod(hodSeq);
	}

	@Override
	public List<Date> executarCursorHod2(Integer atuSeq) {
		return this.getAghAhdHospitaisDiaDAO().executarCursorHod2(atuSeq);
	}

	@Override
	public void persistirAhdHospitaisDia(AhdHospitaisDia ahdHospitaisDia) {
		this.getAghAhdHospitaisDiaDAO().persistir(ahdHospitaisDia);
	}
	
	@Override
	public List<ProcEfet> listarProcEfetPorCtacvNro(Integer ctacvNro) {
		return this.getProcEfetDAO().listarProcEfetPorCtacvNro(ctacvNro);
	}

	@Override
	public void removerProcEfet(ProcEfet procEfet, boolean flush) {
		ProcEfetDAO procEfetDAO = this.getProcEfetDAO();
		procEfetDAO.remover(procEfet);
		if (flush) {
			procEfetDAO.flush();
		}
	}

	@Override
	public void desatacharProcEfet(ProcEfet procEfet) {
		this.getProcEfetDAO().desatachar(procEfet);
	}

	@Override
	public void inserirProcEfet(ProcEfet procEfet, boolean flush) {
		ProcEfetDAO procEfetDAO = this.getProcEfetDAO();
		procEfetDAO.persistir(procEfet);
		procEfetDAO.flush();
	}

	protected ProcEfetDAO getProcEfetDAO() {
		return procEfetDAO;
	}

	private VAinConvenioPlanoDAO getVAinConvenioPlanoDAO() {
		return vAinConvenioPlanoDAO;
	}
	
	@Override
	public List<VAinConvenioPlano> pesquisarConveniosAtivos(final Object objPesquisa) {
		return getVAinConvenioPlanoDAO().pesquisarConveniosAtivos(objPesquisa);
	}

	@Override
	public VAinConvenioPlano obterVAinConvenioPlanoAtivoPeloId(final byte plano,
			final short cnvCodigo) {
		return getVAinConvenioPlanoDAO().obterVAinConvenioPlanoAtivoPeloId(
				plano, cnvCodigo);
	}

	@Override
	public VAinConvenioPlano obterVAinConvenioPlanoSus(final AghParametros paramPlano, final AghParametros paramConvenio)
			throws BaseException {
		return getVAinConvenioPlanoDAO().obterVAinConvenioPlanoAtivoPeloId(
				paramPlano.getVlrNumerico().byteValue(),
				paramConvenio.getVlrNumerico().shortValue());
	}

	@Override
	public String obterCaracteristicaLeito(String pLtoiD, String pTipo) {
		return this.getAinCaracteristicaLeitoDAO().obterCaracteristicaLeito(pLtoiD, pTipo);
	}

	@Override
	public List<AinTiposCaraterInternacao> pesquisarAinTiposCaraterInternacaoPorTodosOsCampos(
			Object filtros) {
		return this.getAinTiposCaraterInternacaoDAO()
				.pesquisarAinTiposCaraterInternacaoPorTodosOsCampos(filtros);
	}

	@Override
	public Long pesquisarAinTiposCaraterInternacaoPorTodosOsCamposCount(
			Object filtros) {
		return this.getAinTiposCaraterInternacaoDAO()
				.pesquisarAinTiposCaraterInternacaoPorTodosOsCamposCount(
						filtros);
	}

	@Override
	public Date obterUltimoIndicadorHospitalarGerado() {
		return this.getAinIndicadorHospitalarResumidoDAO()
				.obterUltimoIndicadorHospitalarGerado();
	}

	@Override
	public List<AinIndicadorHospitalarResumido> obterTotaisIndicadoresUnidade(
			Date mesCompetencia, Date mesCompetenciaFim,
			DominioTipoIndicador tipoIndicador,
			AghUnidadesFuncionais unidadeFuncional) {
		return this.getAinIndicadorHospitalarResumidoDAO()
				.obterTotaisIndicadoresUnidade(mesCompetencia,
						mesCompetenciaFim, tipoIndicador, unidadeFuncional);
	}

	@Override
	public boolean existeIndicadorParaCompetencia(Date anoMesCompetencia,
			DominioTipoIndicador tipoIndicador) {
		return this.getAinIndicadorHospitalarResumidoDAO().existeIndicadorParaCompetencia(anoMesCompetencia, tipoIndicador);
	}

	@Override
	public void removerIndicadorPorCompetenciaTipoIndicador(
			Date anoMesCompetencia, DominioTipoIndicador tipoIndicador) {
		this.getAinIndicadorHospitalarResumidoDAO()
				.removerIndicadorPorCompetenciaTipoIndicador(anoMesCompetencia,
						tipoIndicador);
	}

	@Override
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorAreaFuncional(
			Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {
		return this.getAinIndicadorHospitalarResumidoDAO()
				.obterTransferenciasPorAreaFuncional(mesCompetencia, map);
	}

	@Override
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorClinica(
			Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {
		return this.getAinIndicadorHospitalarResumidoDAO()
				.obterTransferenciasPorClinica(mesCompetencia, map);
	}

	@Override
	public Map<Integer, AinIndicadorHospitalarResumido> obterTransferenciasPorEspecialidade(
			Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {
		return this.getAinIndicadorHospitalarResumidoDAO()
				.obterTransferenciasPorEspecialidade(mesCompetencia, map);
	}

	@Override
	public Integer obterIndicadoresResumidosSeq() {
		return this.getAinIndicadorHospitalarResumidoDAO()
				.obterIndicadoresResumidosSeq();
	}

	@Override
	public Map<Integer, AinIndicadorHospitalarResumido> obterCapacidadeInstaladaGeral(
			Integer quantidadeDiasMes) {
		return this.getAinIndicadorHospitalarResumidoDAO()
				.obterCapacidadeInstaladaGeral(quantidadeDiasMes);
	}

	@Override
	public Map<Integer, AinIndicadorHospitalarResumido> obterCapacidadeInstaladaPorUnidadeFuncional(
			Integer quantidadeDiasMes) {
		return this.getAinIndicadorHospitalarResumidoDAO()
				.obterCapacidadeInstaladaPorUnidadeFuncional(quantidadeDiasMes);
	}

	@Override
	public void obterSaidasGeral(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {
		this.getAinIndicadorHospitalarResumidoDAO().obterSaidasGeral(
				mesCompetencia, map);
	}

	@Override
	public void obterSaidasPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {
		this.getAinIndicadorHospitalarResumidoDAO()
				.obterSaidasPorAreaFuncional(mesCompetencia, map);
	}

	@Override
	public void obterSaidasPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {
		this.getAinIndicadorHospitalarResumidoDAO().obterSaidasPorClinica(
				mesCompetencia, map);
	}

	@Override
	public void obterSaidasPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map) {
		this.getAinIndicadorHospitalarResumidoDAO()
				.obterSaidasPorEspecialidade(mesCompetencia, map);
	}

	@Override
	public void obterPacientesDiaGeral(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException {
		this.getAinIndicadorHospitalarResumidoDAO().obterPacientesDiaGeral(
				mesCompetencia, map, parametroAltaC, parametroAltaD);
	}

	@Override
	public void obterPacientesDiaPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException {
		this.getAinIndicadorHospitalarResumidoDAO()
				.obterPacientesDiaPorAreaFuncional(mesCompetencia, map,
						parametroAltaC, parametroAltaD);
	}

	@Override
	public void obterPacientesDiaPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException {
		this.getAinIndicadorHospitalarResumidoDAO()
				.obterPacientesDiaPorClinica(mesCompetencia, map,
						parametroAltaC, parametroAltaD);
	}

	@Override
	public void obterPacientesDiaPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException {
		this.getAinIndicadorHospitalarResumidoDAO()
				.obterPacientesDiaPorEspecialidade(mesCompetencia, map,
						parametroAltaC, parametroAltaD);
	}

	@Override
	public void obterObitosGeral(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException {
		this.getAinIndicadorHospitalarResumidoDAO().obterObitosGeral(
				mesCompetencia, map, parametroAltaC, parametroAltaD);
	}

	@Override
	public void obterObitosPorAreaFuncional(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException {
		this.getAinIndicadorHospitalarResumidoDAO()
				.obterObitosPorAreaFuncional(mesCompetencia, map,
						parametroAltaC, parametroAltaD);
	}

	@Override
	public void obterObitosPorClinica(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException {
		this.getAinIndicadorHospitalarResumidoDAO().obterObitosPorClinica(
				mesCompetencia, map, parametroAltaC, parametroAltaD);
	}

	@Override
	public void obterObitosPorEspecialidade(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			AghParametros parametroAltaC, AghParametros parametroAltaD)
			throws ApplicationBusinessException {
		this.getAinIndicadorHospitalarResumidoDAO()
				.obterObitosPorEspecialidade(mesCompetencia, map,
						parametroAltaC, parametroAltaD);
	}

	@Override
	public void inserirAinIndicadorHospitalarResumido(
			AinIndicadorHospitalarResumido ainIndicadorHospitalarResumido) {
		AinIndicadorHospitalarResumidoDAO ainIndicadorHospitalarResumidoDAO = this
				.getAinIndicadorHospitalarResumidoDAO();
		ainIndicadorHospitalarResumidoDAO
				.persistir(ainIndicadorHospitalarResumido);
		ainIndicadorHospitalarResumidoDAO.flush();
	}

	@Override
	public String obterDescTipoMvtoPorCodigo(Short codBloq) {
		return this.getAinTiposMovimentoLeitoDAO().obterDescTipoMvtoPorCodigo(
				codBloq);
	}

	@Override
	public List<AinIndicadoresHospitalares> pesquisarQuery5IndicadoresClinica(
			Date mes) {
		return this.getAinIndicadoresHospitalaresDAO()
				.pesquisarQuery5IndicadoresClinica(mes);
	}

	@Override
	public List<AinIndicadoresHospitalares> pesquisarQuery4IndicadoresClinica(
			Date mes) {
		return this.getAinIndicadoresHospitalaresDAO()
				.pesquisarQuery4IndicadoresClinica(mes);
	}

	@Override
	public List<AinIndicadoresHospitalares> pesquisarQuery3IndicadoresClinica(
			Date mes) {
		return this.getAinIndicadoresHospitalaresDAO()
				.pesquisarQuery3IndicadoresClinica(mes);
	}

	@Override
	public List<AinIndicadoresHospitalares> pesquisarQuery2IndicadoresClinica(
			Date mes) {
		return this.getAinIndicadoresHospitalaresDAO()
				.pesquisarQuery2IndicadoresClinica(mes);
	}

	@Override
	public List<AinIndicadoresHospitalares> pesquisarQuery1IndicadoresClinica(
			Date mes) {
		return this.getAinIndicadoresHospitalaresDAO()
				.pesquisarQuery1IndicadoresClinica(mes);
	}

	@Override
	public List<AinIndicadoresHospitalares> pesquisarIndicadoresGeraisUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia) {
		return this.getAinIndicadoresHospitalaresDAO()
				.pesquisarIndicadoresGeraisUnidade(tipoUnidade, mesCompetencia);
	}

	@Override
	public List<AinIndicadoresHospitalares> pesquisarIndicadoresTotaisUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia) {
		return this.getAinIndicadoresHospitalaresDAO()
				.pesquisarIndicadoresTotaisUnidade(tipoUnidade, mesCompetencia);
	}

	@Override
	public Integer obterNumeroOcorrenciasIndicadoresGerais(Date mesCompetencia) {
		return this.getAinIndicadoresHospitalaresDAO()
				.obterNumeroOcorrenciasIndicadoresGerais(mesCompetencia);
	}

	@Override
	public Integer obterNumeroOcorrenciasIndicadoresUnidade(
			DominioTipoUnidade tipoUnidade, Date mesCompetencia) {
		return this.getAinIndicadoresHospitalaresDAO()
				.obterNumeroOcorrenciasIndicadoresUnidade(tipoUnidade,
						mesCompetencia);
	}

	@Override
	public void removerPorCompetencia(Date anoMesComp) {
		this.getAinIndicadoresHospitalaresDAO().removerPorCompetencia(
				anoMesComp);
	}

	@Override
	public List<AinLeitos> obterCapacUnidPriv() {
		return this.getAinIndicadoresHospitalaresDAO().obterCapacUnidPriv();
	}

	@Override
	public void inserirAinIndicadoresHospitalares(
			AinIndicadoresHospitalares ainIndicadoresHospitalares) {
		AinIndicadoresHospitalaresDAO ainIndicadoresHospitalaresDAO = this
				.getAinIndicadoresHospitalaresDAO();
		ainIndicadoresHospitalaresDAO.persistir(ainIndicadoresHospitalares);
		ainIndicadoresHospitalaresDAO.flush();
	}

	@Override
	public List<AinExtratoLeitos> pesquisarExtratosLeito(Date mesCompetencia) {
		return this.getAinExtratoLeitosDAO().pesquisarExtratosLeito(
				mesCompetencia);
	}

	@Override
	public List<Object[]> pesquisarExtratosLeitoPorTipoMovs(
			Date mesCompetencia, Object[] dominioMvtsLeitos) {
		return this.getAinExtratoLeitosDAO().pesquisarExtratosLeitoPorTipoMovs(
				mesCompetencia, dominioMvtsLeitos);
	}

	@Override
	public List<ReInternacoesVO> obterReInternacoesVO() {
		return getAinInternacaoDAO().obterReInternacoesVO();
	}

	@Override
	public String obterLeitoContaHospitalar(final Integer cthSeq) {
		return getAinInternacaoDAO().obterLeitoContaHospitalar(cthSeq);
	}

	@Override
	public Date obterDataInternacao(Integer intSeq) {
		return this.getAinInternacaoDAO().obterDataInternacao(intSeq);
	}

	@Override
	public List<Integer> obterListNumeroQuartoPorCodigoClinica(
			Integer codigoClinica) {
		return this.getAinQuartosDAO().obterListNumeroQuartoPorCodigoClinica(
				codigoClinica);
	}

	@Override
	public AinQuartos obterQuartoDescricao(String descricao) {
		return this.getAinQuartosDAO().obterQuartoDescricao(descricao);
	}

	@Override
	public List<AinLeitos> pesquisarLeitosAtivos() {
		return this.getAinLeitosDAO().pesquisarLeitosAtivos();
	}

	@Override
	public int contarLeitosAtivosPorListaQuartos(List<Integer> listNroQuarto) {
		return this.getAinLeitosDAO().contarLeitosAtivosPorListaQuartos(
				listNroQuarto);
	}

	@Override
	public List<Object[]> obterLeitosTipoMovsLeitos() {
		return this.getAinLeitosDAO().obterLeitosTipoMovsLeitos();
	}

	@Override
	public List<AinMovimentosInternacao> pesquisarMovimentoInternacao(
			Integer seqInternacao, Date dataLancamento, Date dataAlta) {
		return this.getAinMovimentoInternacaoDAO()
				.pesquisarMovimentoInternacao(seqInternacao, dataLancamento,
						dataAlta);
	}

	@Override
	public List<Object> pesquisarMovtsInternacao(
			List<Integer> tipoMovimentoInternacaoList, Date mesCompetencia,
			Date ultimoDiaMes, Date diaMaxMesMaisUm) {
		return this.getAinMovimentoInternacaoDAO().pesquisarMovtsInternacao(
				tipoMovimentoInternacaoList, mesCompetencia, ultimoDiaMes,
				diaMaxMesMaisUm);
	}

	@Override
	public AinMovimentosInternacao obterMovimentoInternacao(Integer seq) {
		return this.getAinMovimentoInternacaoDAO()
				.obterMovimentoInternacao(seq);
	}

	@Override
	public Date obterDataAtendimento(Integer atuSeq) {
		return this.getAinAtendimentosUrgenciaDAO()
				.obterDataAtendimento(atuSeq);
	}

	protected AinTiposCaraterInternacaoDAO getAinTiposCaraterInternacaoDAO() {
		return ainTiposCaraterInternacaoDAO;
	}
	
	protected VAinInternacoesExcedentesDAO getVAinInternacoesExcedentesDAO() {
		return vAinInternacoesExcedentesDAO;
	}

	protected AinTiposMovimentoLeitoDAO getAinTiposMovimentoLeitoDAO() {
		return ainTiposMovimentoLeitoDAO;
	}

	protected AinIndicadorHospitalarResumidoDAO getAinIndicadorHospitalarResumidoDAO() {
		return ainIndicadorHospitalarResumidoDAO;
	}

	protected AinIndicadoresHospitalaresDAO getAinIndicadoresHospitalaresDAO() {
		return ainIndicadoresHospitalaresDAO;
	}

	protected AinCaracteristicaLeitoDAO getAinCaracteristicaLeitoDAO() {
		return ainCaracteristicaLeitoDAO;
	}

	protected ReferencialClinicaEspecialidadeVODAO getReferencialClinicaEspecialidadeVODAO() {
		return referencialClinicaEspecialidadeVODAO;
	}	

	protected AhdHospitaisDiaDAO getAghAhdHospitaisDiaDAO() {
		return ahdHospitaisDiaDAO;
	}
	
	public List<ReferencialClinicaEspecialidadeVO> pesquisarReferencialClinicaEspecialidade(
			Integer codigoClinica, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc){
		return getReferencialClinicaEspecialidadeVODAO().pesquisarReferencialClinicaEspecialidade(codigoClinica, firstResult, maxResult, orderProperty, asc);
	}
	
	public AinAcompanhantesInternacaoDAO getAinAcompanhantesInternacaoDAO(){
		return ainAcompanhantesInternacaoDAO;
	}
	
	public List<AinAcompanhantesInternacao> pesquisarAcompanhantesInternacao(Integer intSeq){
		return getAinAcompanhantesInternacaoDAO().obterAcompanhantesInternacao(intSeq);
	}
	
	@Override
	public String obterTelefonePaciente(Integer pacCodigo) {
		return getRelatorioInternacaoRN().obterTelefonePaciente(pacCodigo);
	}
	
	@Override
	public AinInternacao obterInternacaoConvenio(final Integer intSeq) {
		return getAinInternacaoDAO().obterInternacaoConvenio(intSeq);
	}
	
	@Override
	public AinAtendimentosUrgencia obterAtendimentoUrgenciaConvenio(final Integer atuSeq){
		return getAinAtendimentosUrgenciaDAO().obterAtendimentoUrgenciaConvenio(atuSeq);
	}
	
	@Override
	public AhdHospitaisDia obterHospitalDiaConvenio(final Integer hodSeq){
		return getAghAhdHospitaisDiaDAO().obterHospitalDiaConvenio(hodSeq);
	}

	@Override
	public String obterDescricaoConvenioPlano(Byte cspSeq,Short cspCnvCodigo) {
		return getVAinConvenioPlanoDAO().obterDescricaoConvenioPlano(cspSeq, cspCnvCodigo);
	}
	
	@Override
	public String obterConvenioPlano(Byte cspSeq,Short cspCnvCodigo) {
		return getVAinConvenioPlanoDAO().obterDescricaoConvenioPlano(cspSeq, cspCnvCodigo);
	}
	
	@Override
	public AinInternacao obterInternacaoPorAtendimentoPacCodigo(Integer codigo){
		return getAinInternacaoDAO().obterInternacaoPorAtendimentoPacCodigo(codigo);
	}
	
	@Override
	public Boolean verificarCaracteristicaEspecialidade(Short seq, DominioCaracEspecialidade caracteristica) {
		return getAltasPorUnidadeRN().verificarCaracteristicaEspecialidade(seq, caracteristica);
	}
	
	protected AltasPorUnidadeRN getAltasPorUnidadeRN() {
		return altasPorUnidadeRN;
	}
	
	public AinInternacao obterInternacaoPorSequencialPaciente(final Integer seq, final Integer pacCodigo) {
		return getAinInternacaoDAO().obterInternacaoPorSequencialPaciente(seq, pacCodigo);
	}
	
	@Override
	public AinAtendimentosUrgencia obterAtendimentoUrgenciaPorSequencialPaciente(Integer seq, Integer pacCodigo) {
		return getAinAtendimentosUrgenciaDAO().obterAtendimentoUrgenciaPorSequencialPaciente(seq, pacCodigo);
	}
	
	@Override
	public VAinConvenioPlano obterVAinConvenioPlanoPeloId(byte plano, short cnvCodigo) {
		return getVAinConvenioPlanoDAO().obterVAinConvenioPlanoPeloId(plano, cnvCodigo);
	}
	
	@Override
	public List<AinMovimentosInternacao> buscarMovimentosInternacao(Integer seqInternacao, Date dtInicioProcessamento, Date dtFimProcessamento, Date dtHoraLancamento){
		return getAinMovimentoInternacaoDAO().buscarMovimentosInternacao(seqInternacao, dtInicioProcessamento, dtFimProcessamento, dtHoraLancamento);
	}	
	
	@Override
	public List<AinMovimentosInternacao> buscarMovimentosInternacao(Integer seqInternacao, Date dtInicioProcessamento){
		return getAinMovimentoInternacaoDAO().buscarMovimentosInternacao(seqInternacao, dtInicioProcessamento);
	}
	
	@Override
	public void verificarNecessidadeInformarDiaria(Integer seqInternacao, Short codigoConvenio) throws ApplicationBusinessException{
		this.getRelatorioInternacaoON().verificarNecessidadeInformarDiaria(seqInternacao, codigoConvenio);
	}

	@Override
	public AinInternacao obterAinInternacaoPorOrigemEAtdSeq(DominioOrigemAtendimento i, Integer seq) {
		return this.getAinInternacaoDAO().obterAinInternacaoPorOrigemEAtdSeq(i, seq);
	}
	
	@Override
	public AinInternacao obterAinInternacaoPorAtdSeq(Integer atdSeq) {
		return this.getAinInternacaoDAO().obterAinInternacaoPorAtdSeq(atdSeq);
	}
	
	@Override
	public String obterPostoSaudePaciente(Integer pacCodigo) {
		return getRelatorioInternacaoRN().obterPostoSaudePaciente(pacCodigo);
	}
	
	@Override
	public List<BaixasDiaVO> pesquisaBaixasDia(Date dataReferencia,	DominioGrupoConvenio grupoConvenio, AghOrigemEventos origemEvento, AipEtnia etniaPaciente, boolean exibeEtnia) throws ApplicationBusinessException {
		return getRelatorioBaixasDiaON().pesquisa(dataReferencia, grupoConvenio, origemEvento, etniaPaciente, exibeEtnia);
	}
	
	@Override
	public List<BaixasDiaPorEtniaVO> pesquisaBaixasDiaPorEtnia(Date dataReferencia,	DominioGrupoConvenio grupoConvenio, AghOrigemEventos origemEvento, AipEtnia etniaPaciente, boolean exibeEtnia) throws ApplicationBusinessException {
		return getRelatorioBaixasDiaON().pesquisaPorEtnia(dataReferencia, grupoConvenio, origemEvento, etniaPaciente, exibeEtnia);
	}

	@Override
	public Integer obterSeqAtendimentoUrgenciaPorConsulta(Integer conNumero) {
		return getAinAtendimentosUrgenciaDAO().obterSeqAtendimentoUrgenciaPorConsulta(conNumero);
	}

	@Override
	public AinInternacao obterInternacaoPorAtendimentoUrgencia(Integer atuSeq) {
		return getAinInternacaoDAO().obterInternacaoPorAtendimentoUrgencia(atuSeq);
	}

	@Override
	public String atualizarServidorProfessorInternacao(Integer seqInternacao, Integer matriculaProfessor, Short vinCodigoProfessor, String nomeMicrocomputador,
			Integer matriculaServidorLogado, Short vinCodigoServidorLogado) {
		return getInternacaoRN().atualizarServidorProfessorInternacao(seqInternacao, matriculaProfessor, vinCodigoProfessor, nomeMicrocomputador, matriculaServidorLogado, vinCodigoServidorLogado);
	}
	
	@Override
	public List<AinInternacao> pesquisarInternacoesInstituicaoHospitalarOrigem(Integer ihoSeqOrigem){
		return getAinInternacaoDAO().pesquisarInternacoesInstituicaoHospitalarOrigem(ihoSeqOrigem);
	}
	
	@Override
	public List<AinInternacao> pesquisarInternacoesInstituicaoHospitalarTransferencia(Integer ihoSeqOrigem){
		return getAinInternacaoDAO().pesquisarInternacoesInstituicaoHospitalarTransferencia(ihoSeqOrigem);
	}

	@Override
	public Integer obtemCodigoMovimento(String parametro) throws ApplicationBusinessException {
		return getInternacaoRN().obtemCodigoMovimento(parametro);
	}
	
	@Override
	public void finalizarConsulta(Integer numeroConsulta, Integer codigoPaciente, Integer matricula, Short vinCodigo, String hostName) throws ApplicationBusinessException, BaseException{
		getAtendimentoUrgenciaRN().finalizarConsulta(numeroConsulta, codigoPaciente, matricula, vinCodigo, hostName);
	}	

	protected AtendimentoUrgenciaRN getAtendimentoUrgenciaRN() {
		return atendimentoUrgenciaRN;
	}

	@Override
	public void processarEnforceAtendimentoUrgencia(AinAtendimentosUrgencia atendimentoUrgencia, AinAtendimentosUrgencia atendimentoUrgenciaAntigo,
			TipoOperacaoEnum tipoOperacao, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		this.internacaoRN.processarEnforceAtendimentoUrgencia(atendimentoUrgencia, atendimentoUrgenciaAntigo, tipoOperacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void executaRotinasInsercao(AinAtendimentosUrgencia atendimentoUrgencia, AinAtendimentosUrgencia atendimentoUrgenciaAntigo)
			throws BaseException {
		this.internacaoRN.executaRotinasInsercao(atendimentoUrgencia, atendimentoUrgenciaAntigo);
	}

	@Override
	public Boolean verificarPacienteInternadoPorConsulta(Integer numeroConsulta) {
		return pesquisaInternacaoON.verificarPacienteInternadoPorConsulta(numeroConsulta);
	}

	@Override
	public Boolean verificarPacienteIngressoSOPorConsulta(Integer numeroConsulta) throws ApplicationBusinessException {
		return pesquisaInternacaoON.verificaPacienteIngressoSOPorConsulta(numeroConsulta);
	}

	@Override
	public void atualizaMovimentoInternacao(AinAtendimentosUrgencia atendimentoUrgencia,
			AinAtendimentosUrgencia atendimentoUrgenciaAntigo,
			Integer codigoMovimentacao, Date dtAltaAtendimento)
			throws ApplicationBusinessException {
		internacaoRN.atualizaMovimentoInternacao(atendimentoUrgencia, atendimentoUrgenciaAntigo, codigoMovimentacao, dtAltaAtendimento);
	}
	
	@Override
	public List<PacientesInternadosUltrapassadoVO> obterUnidadesQtdPacientesInternadosExcedentes() {
		return getVAinInternacoesExcedentesDAO().obterUnidadesQtdPacientesInternadosExcedentes();
	}
	
	// #8424 - ManterEquipesPorProfissional
		@Override
		public List<AghProfissionaisEquipe> pesquisarListaEquipesPorProfissionalPorServidorPaginado(Integer firstResult, Integer maxResult,
				String orderProperty, boolean asc, RapServidores servidor) {
			return manterEquipesPorProfissionalRN.pesquisarListaEquipesPorProfissionalPorServidor(firstResult, maxResult, orderProperty, asc, servidor);
		}
		
		@Override
		public Long pesquisarListaEquipesPorProfissionalPorServidorCount(RapServidores servidor) {
			return manterEquipesPorProfissionalRN.pesquisarListaEquipesPorProfissionalPorServidorCount(servidor);
		}
		
		@Override
		public void excluirEquipePorProfissional(AghProfissionaisEquipeId equipePorProfissionalId) {
			manterEquipesPorProfissionalRN.excluirEquipePorProfissional(equipePorProfissionalId);
		}

		@Override
		public void persistirEquipePorProfissional(RapServidores servidorSelecionado, AghEquipes equipe) {
			manterEquipesPorProfissionalRN.persistirEquipePorProfissional(servidorSelecionado, equipe);
		}

		@Override
		public List<AghEquipes> pesquisarListaAghEquipes(String filtro) {
			return manterEquipesPorProfissionalRN.pesquisarListaAghEquipes(filtro);
		}

		@Override
		public Long pesquisarListaAghEquipesCount(String filtro) {
			return manterEquipesPorProfissionalRN.pesquisarListaAghEquipesCount(filtro);
		}
		
		public ManterEquipesPorProfissionalRN getManterEquipesPorProfissionalRN(){
			return new ManterEquipesPorProfissionalRN();
		}

		@Override
		public void realizarInternacaoPacienteAutomaticamente(Integer matricula, Short vinCodigo, Integer pacCodigo, Short seqp, Integer numeroConsulta, String hostName, Long trgSeq) throws ApplicationBusinessException, BaseException {
			this.internacaoRN.realizarInternacaoPacienteAutomaticamente(matricula, vinCodigo, pacCodigo, seqp, numeroConsulta, hostName, trgSeq);
		}
		
		public AinTiposCaraterInternacao obterAinTiposCaraterInternacao(Integer seq){
			return this.getAinTiposCaraterInternacaoDAO().obterPorChavePrimaria(seq);
		}
		
		@Override
		public List<AinLeitos> pesquisarLeitosPorUnidadeFuncional(String param,
				Short unfSeq) {		
			return this.ainLeitosDAO.pesquisarLeitosPorUnidadeFuncional(param, unfSeq);
		}
		
		@Override
		public Long pesquisarLeitosPorUnidadeFuncionalCount(Object param, Short unfSeq) {		
			return this.ainLeitosDAO.pesquisarLeitosPorUnidadeFuncionalCount(param, unfSeq);
		}

		@Override
		public Boolean verificarLeitoExiste(String idLeito) {
			return ainLeitosDAO.verificarLeitoExiste(idLeito);
		}

		@Override
		public List<AinLeitos> pesquisarLeitosPorSeqUnf(List<Short> unfs, String leito) {
			return ainLeitosDAO.pesquisarLeitosPorSeqUnf(unfs,leito);
		}
		
		@Override
		public AinAtendimentosUrgencia persistirAtendimentoUrgente(
				Integer numeroConsulta, Integer codigoPaciente,
				String microComputador) throws ApplicationBusinessException,
				NumberFormatException, BaseException {
			return getAtendimentoUrgenciaRN().persistirAtendimentoUrgente(numeroConsulta, codigoPaciente, microComputador);
		}

		@Override
		public Long pesquisarLeitosPorSeqUnfCount(List<Short> unfs, String leito) {
			return ainLeitosDAO.pesquisarLeitosPorSeqUnfCount(unfs,leito);
		}

		@Override
		public void inserirCirurgiaDoCentroObstetrico(Integer pacCodigo,
				Short gestacaoSeqp, String nivelContaminacao,
				Date dataInicioProcedimento, Short salaCirurgicaSeqp,
				Short tempoDuracaoProcedimento, Short anestesiaSeqp,
				Short equipeSeqp, String tipoNascimento) throws BaseException, ApplicationBusinessException {
			
			this.getCadastroInternacaoON().inserirCirurgiaDoCentroObstetrico(pacCodigo, gestacaoSeqp, nivelContaminacao, dataInicioProcedimento, salaCirurgicaSeqp, tempoDuracaoProcedimento, anestesiaSeqp, equipeSeqp, tipoNascimento);
			
		}

		@Override
		public List<AinLeitos> pesquisarLeitosPorUnidadeFuncional(Object param,
				Short unfSeq) {
			return ainLeitosDAO.pesquisarLeitosPorUnidadeFuncional(param, unfSeq);
		}
		
		@Override
		public AinInternacao obterInternacaoPaciente(Integer seqInternacao){
			return this.getAinInternacaoDAO().obterInternacaoPaciente(seqInternacao);
		}
		
		
		@Override
		public AinInternacao obterInternacaoPacientePorSeq(Integer seqInternacao){
			return this.getAinInternacaoDAO().obterInternacaoPacientePorSeq(seqInternacao);
		}
		
		
		@Override
		public AinResponsaveisPaciente obterResponsaveisPacientePorNome(String nome) {
			return this.ainResponsaveisPacienteDAO.obterResponsaveisPacientePorNome(nome);
		}

		@Override
		public List<PacienteIdadeCIDVO> pesquisarPacientesPorIdadeECID(Integer idadeInicial, Integer idadeFinal, AghCid cid)
				throws ApplicationBusinessException {
			return relatorioIdadeCIDRN.pesquisarPacientesPorIdadeECID(idadeInicial, idadeFinal, cid);
		}
}