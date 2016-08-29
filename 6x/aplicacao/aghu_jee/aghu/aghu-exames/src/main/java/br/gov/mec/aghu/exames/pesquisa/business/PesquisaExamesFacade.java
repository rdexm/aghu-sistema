package br.gov.mec.aghu.exames.pesquisa.business;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelProtocoloEntregaExamesRN;
import br.gov.mec.aghu.exames.dao.AelEquipamentosDAO;
import br.gov.mec.aghu.exames.dao.AelProtocoloEntregaExamesDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.ItensProtocoloVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExameSituacaoVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.pesquisa.vo.ResultadoPesquisaProtocoloVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosVO;
import br.gov.mec.aghu.exames.questionario.vo.QuestionarioVO;
import br.gov.mec.aghu.exames.questionario.vo.RespostaQuestaoVO;
import br.gov.mec.aghu.exames.vo.RelatorioCaracteristicasResultadosPorExameVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelProtocoloEntregaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.IAelSolicitacaoExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;

/**
 * Porta de entrada do sub módulo de Pesquisa do módulo Exames.
 * 
 * @author lcmoura
 * 
 */

@Modulo(ModuloEnum.EXAMES_LAUDOS)
@Stateless
public class PesquisaExamesFacade extends BaseFacade implements IPesquisaExamesFacade {

	@EJB
	private ExamesCancelarRN examesCancelarRN;

	@EJB
	private MonitorarColetasEmergenciaRN monitorarColetasEmergenciaRN;

	@EJB
	private RelatorioCaracteristicasResultadosPorExameON relatorioCaracteristicasResultadosPorExameON;

	@EJB
	private ExameSituacaoON exameSituacaoON;

	@EJB
	private PesquisaExamesON pesquisaExamesON;

	@EJB
	private PesquisaExameRN pesquisaExameRN;
	
	@EJB
	private ExamesCancelarON examesCancelarON;

	@Inject
	private AelEquipamentosDAO aelEquipamentosDAO;
	
	@EJB
	private AelProtocoloEntregaExamesRN aelProtocoloEntregaExamesRN;
	
	@Inject
	private AelProtocoloEntregaExamesDAO aelProtocoloEntregaExamesDAO;

	private static final long serialVersionUID = -3312888572685676736L;

	// private static final String ENTITY_MANAGER = "entityManager";
	//
	// private static final String TRANSACTION =
	// "org.jboss.seam.transaction.transaction";

	private static final int TRANSACTION_TIMEOUT_1_HORA = 60 * 60 * 1; // 1 hora

	/* Métodos de pesquisa dos exames para cancelar */

	@Override
	public PesquisaExamesPacientesResultsVO buscaDadosSolicitacaoPorSoeSeq(Integer soeSeq) throws BaseException {
		return getExamesCancelarRN().buscaDadosSolicitacaoPorSoeSeq(soeSeq);
	}

	@Override
	public List<PesquisaExamesPacientesResultsVO> buscaDadosItensSolicitacaoPorSoeSeq(Integer soeSeq, Short unfSeq) throws BaseException {
		return getExamesCancelarRN().buscaDadosItensSolicitacaoPorSoeSeq(soeSeq, unfSeq);
	}

	@Override
	public List<PesquisaExamesPacientesResultsVO> buscaDadosItensSolicitacaoCancelarColetaPorSoeSeq(Integer soeSeq, Short unfSeq)
			throws BaseException {
		return getExamesCancelarRN().buscaDadosItensSolicitacaoCancelarColetaPorSoeSeq(soeSeq, unfSeq);
	}

	@Override
	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExames(Object param) {
		return getExamesCancelarRN().pesquisarMotivoCancelaExames(param);
	}

	@Override
	public Long pesquisarMotivoCancelaExamesCount(Object param) {
		return getExamesCancelarRN().pesquisarMotivoCancelaExamesCount(param);
	}

	@Override
	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExamesColeta(Object param) {
		return getExamesCancelarRN().pesquisarMotivoCancelaExamesColeta(param);
	}

	@Override
	public void cancelarExamesNaAlta(AghAtendimentos atendimento, AinTiposAltaMedica tipoAltaMedica, String nomeMicrocomputador)
			throws BaseException {
		getExamesCancelarRN().cancelarExamesNaAlta(atendimento, tipoAltaMedica, nomeMicrocomputador);
	}

	@Override
	@BypassInactiveModule
	public List<Short> buscaExamesSolicitadosOrdenados(Integer solicitacao, List<Short> seqps, Boolean isHist) {
		return getPesquisaExameRN().buscaExamesSolicitadosOrdenados(solicitacao, seqps, isHist);
	}

	@Override
	public Boolean verGeraCarta(AelItemSolicitacaoExames aelItemSolicitacaoExames) {
		return getExamesCancelarRN().verGeraCarta(aelItemSolicitacaoExames);
	}

	@Override
	public void geraCartaCanc(AelItemSolicitacaoExames aelItemSolicitacaoExames) throws ApplicationBusinessException {
		getExamesCancelarRN().geraCartaCanc(aelItemSolicitacaoExames);
	}

	protected ExamesCancelarRN getExamesCancelarRN() {
		return examesCancelarRN;
	}

	@Override
	public void cancelarExames(AelItemSolicitacaoExames aelItemSolicitacaoExames, final AelMotivoCancelaExames motivoCancelar,
			String nomeMicrocomputador) throws BaseException {
		getExamesCancelarON().cancelarExames(aelItemSolicitacaoExames, motivoCancelar, nomeMicrocomputador);
	}

	protected ExamesCancelarON getExamesCancelarON() {
		return examesCancelarON;
	}

	@BypassInactiveModule
	public boolean permiteVisualizarLaudoMedico() {
		return getPesquisaExamesON().permiteVisualizarLaudoMedico();
	}

	@BypassInactiveModule
	public boolean permitevisualizarLaudoAtdExt() {
		return getPesquisaExamesON().permitevisualizarLaudoAtdExt();
	}

	@BypassInactiveModule
	public boolean permitevisualizarLaudoSamis() {
		return getPesquisaExamesON().permitevisualizarLaudoSamis();
	}

	@BypassInactiveModule
	public void validaSituacaoExamesSelecionados(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist, Boolean validarSitExecutando)
			throws ApplicationBusinessException {
		getPesquisaExamesON().validaSituacaoExamesSelecionados(solicitacoes, isHist, validarSitExecutando);
	}

	@Override
	@BypassInactiveModule
	public boolean validarSituacaoExameSelecionado(final String situacaoCodigo) {
		return getPesquisaExamesON().validarSituacaoExameSelecionado(situacaoCodigo);
	}
	
	@BypassInactiveModule
	public Map<Integer, Vector<Short>> obterListaSolicitacoesImpressaoLaudo(final Map<Integer, Vector<Short>> solicitacoes,
			final List<PesquisaExamesPacientesResultsVO> listaResultados, final Integer prontuario,
			final DominioTipoImpressaoLaudo tipoImpressaoLaudo) throws ApplicationBusinessException {
		return getPesquisaExamesON().obterListaSolicitacoesImpressaoLaudo(solicitacoes, listaResultados, prontuario, tipoImpressaoLaudo);
	}

	@BypassInactiveModule
	public Boolean validaQuantidadeExamesSelecionados(final Map<Integer, Vector<Short>> solicitacoes) {
		return getPesquisaExamesON().validaQuantidadeExamesSelecionados(solicitacoes);
	}

	@BypassInactiveModule
	public void validarExamesComResposta(final Integer soeSeq, final Short seqp) throws ApplicationBusinessException {
		this.getPesquisaExamesON().validarExamesComResposta(soeSeq, seqp);
	}

	protected PesquisaExamesON getPesquisaExamesON() {
		return pesquisaExamesON;
	}

	/** #5870 **/
	@Override
	@Secure("#{s:hasPermission('pesquisarExamesPorSituacao','pesquisar')}")
	public List<VAelExamesSolicitacao> pesquisaExameSolicitacao(String descricao, AghUnidadesFuncionais unidadeExecutora)
			throws ApplicationBusinessException {
		return getExameSituacaoON().pesquisaExameSolicitacao(descricao, unidadeExecutora);
	}

	@Override
	@Secure("#{s:hasPermission('pesquisarExamesPorSituacao','pesquisar')}")
	public List<PesquisaExameSituacaoVO> pesquisaExameSolicitacaoPacAtend(AghUnidadesFuncionais unidadeExecutora, Date dtHrInicial,
			Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio, AelSitItemSolicitacoes situacao,
			VAelExamesSolicitacao nomeExame, DominioOrigemAtendimento origemAtendimento,
			DominoOrigemMapaAmostraItemExame origemMapaTrabalho, Integer firstResult, Integer maxResult, String orderProperty, boolean asc)
			throws BaseException {
		return getExameSituacaoON().pesquisaExameSolicitacaoPacAtend(unidadeExecutora, dtHrInicial, dtHrFinal, dtHrProgramado, convenio,
				situacao, nomeExame, origemAtendimento, origemMapaTrabalho, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('pesquisarExamesPorSituacao','pesquisar')}")
	public List<PesquisaExameSituacaoVO> pesquisaExameSolicitacaoPacAtendRel(AghUnidadesFuncionais unidadeExecutora, Date dtHrInicial,
			Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio, AelSitItemSolicitacoes situacao,
			VAelExamesSolicitacao nomeExame, DominioOrigemAtendimento origemAtendimento, DominoOrigemMapaAmostraItemExame origemMapaTrabalho)
			throws ApplicationBusinessException {
		return getExameSituacaoON().pesquisaExameSolicitacaoPacAtendRel(unidadeExecutora, dtHrInicial, dtHrFinal, dtHrProgramado, convenio,
				situacao, nomeExame, origemAtendimento, origemMapaTrabalho);
	}

	@Override
	@Secure("#{s:hasPermission('pesquisarExamesPorSituacao','pesquisar')}")
	public void validarFiltroPesquisaExameSolicitacaoPacAtend(Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado,
			AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame) throws BaseException {

		getExameSituacaoON().validarFiltroPesquisaExameSolicitacaoPacAtend(dtHrInicial, dtHrFinal, dtHrProgramado, situacao, nomeExame);
	}

	@Override
	@Secure("#{s:hasPermission('pesquisarExamesPorSituacao','pesquisar')}")
	public Long countExameSolicitacaoPacAtend(AghUnidadesFuncionais unidadeExecutora, Date dtHrInicial, Date dtHrFinal,
			Date dtHrProgramado, DominioConvenioExameSituacao convenio, AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame,
			DominioOrigemAtendimento origemAtendimento, DominoOrigemMapaAmostraItemExame origemMapaTrabalho) {

		return getExameSituacaoON().countExameSolicitacaoPacAtend(unidadeExecutora, dtHrInicial, dtHrFinal, dtHrProgramado, convenio,
				situacao, nomeExame, origemAtendimento, origemMapaTrabalho);
	}

	protected ExameSituacaoON getExameSituacaoON() {
		return exameSituacaoON;
	}

	/** FIM - #5870 **/

	/** #5431 **/
	@Override
	public List<VAelSolicAtendsVO> pesquisaMonitoramentoColetasEmergencia(AghUnidadesFuncionais unidadeExecutora) throws BaseException {
		this.setTimeout(TRANSACTION_TIMEOUT_1_HORA);
		this.commit(TRANSACTION_TIMEOUT_1_HORA);
//		return getMonitorarColetasEmergenciaRN().pesquisaMonitoramentoColetasEmergencia(unidadeExecutora);

		//Chamado #79045
		return getMonitorarColetasEmergenciaRN().pesquisaMonitoramentoColetasEmergenciaAgregado(unidadeExecutora);
	}

	@Override
	public List<AelItemSolicitacaoExames> pesquisaMonitoramentoColetasEmergenciaItensProgramados(AghUnidadesFuncionais unidadeExecutora,
			VAelSolicAtendsVO vo) throws BaseException {
		return getMonitorarColetasEmergenciaRN().pesquisaMonitoramentoColetasEmergenciaItensProgramados(unidadeExecutora, vo);
	}

	@Override
	@BypassInactiveModule
	public DominioOrigemAtendimento validaLaudoOrigemPaciente(AelSolicitacaoExames solicitacaoExame) {
		return getMonitorarColetasEmergenciaRN().validaLaudoOrigemPaciente(solicitacaoExame);
	}
	
	@Override
	@BypassInactiveModule
	public DominioOrigemAtendimento validaLaudoOrigemPaciente(IAelSolicitacaoExames solicitacaoExame) {
		return getMonitorarColetasEmergenciaRN().validaLaudoOrigemPaciente(solicitacaoExame);
	}

	@Override
	@BypassInactiveModule
	public DominioOrigemAtendimento validaLaudoOrigemPacienteHist(AelSolicitacaoExamesHist solicitacaoExame) {
		return getMonitorarColetasEmergenciaRN().validaLaudoOrigemPacienteHist(solicitacaoExame);
	}

	protected MonitorarColetasEmergenciaRN getMonitorarColetasEmergenciaRN() {
		return monitorarColetasEmergenciaRN;
	}

	@Override
	public List<AelAgrpPesquisas> buscaAgrupamentosPesquisa(Object pesquisa) {
		return getPesquisaExameRN().buscaAgrupamentosPesquisa(pesquisa);
	}

	@Override
	@BypassInactiveModule
	public List<AinLeitos> obterLeitosAtivosPorUnf(Object pesquisa, Short unfSeq) {
		return getPesquisaExameRN().obterLeitosAtivosPorUnf(pesquisa, unfSeq);
	}

	@Override
	@BypassInactiveModule
	public Long obterLeitosAtivosPorUnfCount(Object pesquisa, Short unfSeq) {
		return getPesquisaExameRN().obterLeitosAtivosPorUnfCount(pesquisa, unfSeq);
	}

	@Override
	public List<VAelExamesSolicitacao> obterNomeExames(Object objPesquisa) {
		return getPesquisaExameRN().obterNomeExames(objPesquisa);
	}

	/* Métodos de pesquisa dos exames */
	@Override
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorSolicExa(Integer seq_solicitacao) {
		return getPesquisaExameRN().buscarAipPacientesPorSolicExa(seq_solicitacao);
	}
	
	@Override
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorNumProtocolo(Long seq_protocolo) {
		return aelProtocoloEntregaExamesRN.buscarAipPacientesPorNumProtocolo(seq_protocolo);
	}

	@Override
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorNumeroAp(Long numeroAp, AelConfigExLaudoUnico configExame) {
		return getPesquisaExameRN().buscarAipPacientesPorNumeroAp(numeroAp, configExame);
	}

	@Override
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorConsulta(Integer seq_consulta) {
		return getPesquisaExameRN().buscarAipPacientesPorConsulta(seq_consulta);
	}

	@Override
	@BypassInactiveModule
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorParametros(Integer prontuarioPac, String nomePaciente, AinLeitos leitoPac,
			AghUnidadesFuncionais unidadeFuncionalPac) {
		return getPesquisaExameRN().buscarAipPacientesPorParametros(prontuarioPac, nomePaciente, leitoPac, unidadeFuncionalPac);
	}

	@Override
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorServidor(Integer matricula, Short vinCodigo) {
		return getPesquisaExameRN().buscarAipPacientesPorServidor(matricula, vinCodigo);
	}

	@Override
	@BypassInactiveModule
	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorPacienteHist(AipPacientes paciente)
			throws ApplicationBusinessException {

		return getPesquisaExameRN().buscaExamesSolicitadosPorPacienteHist(paciente);
	}

	@Override
	@BypassInactiveModule
	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorPaciente(Integer codigo, Integer seq_consulta,
			PesquisaExamesFiltroVO filtro) throws ApplicationBusinessException {

		return getPesquisaExameRN().buscaExamesSolicitadosPorPaciente(codigo, seq_consulta, filtro);
	}

	@Override
	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorSolicitante(PesquisaExamesFiltroVO filtro)
			throws ApplicationBusinessException {
		return getPesquisaExameRN().buscaExamesSolicitadosPorSolicitante(filtro);
	}

	/* fim dos métodos efetivos e pesquisa */

	protected PesquisaExameRN getPesquisaExameRN() {
		return pesquisaExameRN;
	}

	/**
	 * 2211 - Manter Cadastro de Equipamentos
	 */
	@Override
	@Secure("#{s:hasPermission('manterEquipamentos','pesquisar')}")
	public List<AelEquipamentos> pesquisaListaEquipamentos(AghUnidadesFuncionais unidadeExecutora) throws BaseException {
		return getAelEquipamentosDAO().pesquisaEquipamentosPorUnidadeExecutora(unidadeExecutora);
	}

	protected AelEquipamentosDAO getAelEquipamentosDAO() {
		return aelEquipamentosDAO;
	}

	/**
	 * 2376 - Imprimir lista de caracteristicas de resultados por exame
	 * 
	 * @throws BaseException
	 */
	@Override
	public List<RelatorioCaracteristicasResultadosPorExameVO> pesquisarRelatorioCaracteristicasResultadosExame(String siglaExame,
			Integer manSeq) throws ApplicationBusinessException {
		return getRelatorioCaracteristicasResultadosPorExameON().pesquisaCaracteristicasResultadosPorExame(siglaExame, manSeq);
	}

	protected RelatorioCaracteristicasResultadosPorExameON getRelatorioCaracteristicasResultadosPorExameON() {
		return relatorioCaracteristicasResultadosPorExameON;
	}

	@Override
	public List<RapServidores> obterServidorSolic(String objPesquisa) {
		return getPesquisaExameRN().obterServidorSolic(objPesquisa);
	}

	@Override
	public Integer obterServidorSolicCount(String objPesquisa) {
		return getPesquisaExameRN().obterServidorSolicCount(objPesquisa);
	}

	@Override
	public Long obterNomeExamesCount(Object objPesquisa) {
		return getPesquisaExameRN().obterNomeExamesCount(objPesquisa);
	}

	@Override
	public List<VAelExamesAtdDiversosVO> buscarVAelExamesAtdDiversos(final VAelExamesAtdDiversosFiltroVO filtro) {
		return this.getPesquisaExameRN().buscarVAelExamesAtdDiversos(filtro);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.faturamento.business.IFaturamentoFacade#setTimeout(java
	 * .lang.Integer)
	 */
	@Override
	public void setTimeout(final Integer timeout) throws ApplicationBusinessException {
		// final UserTransaction userTx = this.getUserTransaction();
		// try {
		// final EntityManager em = this.getEntityManager();
		// if (userTx.isNoTransaction() || !userTx.isActive()) {
		// userTx.begin();
		// }
		// if (timeout != null) {
		// userTx.setTransactionTimeout(timeout);
		// }
		// em.joinTransaction();
		// } catch (final Exception e) {
		// logError(e.getMessage(), e);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#commit(java.lang.Integer)
	 */
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
		// EstoqueFacadeExceptionCode.ERRO_AO_CONFIRMAR_TRANSACAO.throwException();
		// }
	}

	@Override
	public String getUrlImpax(Map<Integer, Vector<Short>> solicitacoes) throws BaseException {
		return getPesquisaExamesON().getUrlImpax(solicitacoes);
	}

	@Override
	@BypassInactiveModule
	public void validarExamesComRespostaHistorico(final Integer soeSeq, final Short seqp) throws ApplicationBusinessException {
		this.getPesquisaExamesON().validarExamesComRespostaHistorico(soeSeq, seqp);
	}

	@Override
	@BypassInactiveModule
	public List<QuestionarioVO> pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(Integer soeSeq, Short seqp, Boolean isHist) {
		return getPesquisaExamesON().pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(soeSeq, seqp, isHist);
	}

	@Override
	@BypassInactiveModule
	public List<RespostaQuestaoVO> pesquisarRespostasPorQuestionarioEItemSolicitacaoExame(Integer qtnSeq, Integer soeSeq, Short seqp,
			Boolean isHist) {
		return getPesquisaExamesON().pesquisarRespostasPorQuestionarioEItemSolicitacaoExame(qtnSeq, soeSeq, seqp, isHist);
	}
	
	@Override
	public AelItemSolicitacaoExames obterDadoItensSolicitacaoPorSoeSeq(Integer soeSeq, Short seqp) {
		return getPesquisaExamesON().obterDadoItensSolicitacaoPorSoeSeq(soeSeq , seqp);
	}
	
	@Override
	public List<AelItemSolicitacaoExames> obterDadosItensSolicitacaoPorSoeSeq(Integer soeSeq, Short seqp) {
		return getPesquisaExamesON().obterDadosItensSolicitacaoPorSoeSeq(soeSeq , seqp);
	}
	
	@Override
	public List<ResultadoPesquisaProtocoloVO> buscarProtocolo(Long protocolo) {
		return aelProtocoloEntregaExamesRN.buscarProtocolo(protocolo);
	}
	
	@Override
	public List<ResultadoPesquisaProtocoloVO> buscarProtocoloPorProntuario(Integer prontuario) {
		return aelProtocoloEntregaExamesRN.buscarProtocoloPorProntuario(prontuario);
	}
	
	@Override
	public List<ResultadoPesquisaProtocoloVO> buscarProtocoloPorSolicitacao(Integer solicitacao) {
		return aelProtocoloEntregaExamesRN.buscarProtocoloPorSolicitacao(solicitacao);
	}
	
	@Override
	public List<ItensProtocoloVO> buscarItensProtocolo(Long protocolo) {
		return aelProtocoloEntregaExamesRN.buscarItensProtocolo(protocolo);
	}
	
	@Override
	public AelProtocoloEntregaExames recuperarNovoProtocolo(Long protocolo) {
		return aelProtocoloEntregaExamesDAO.buscarProtocolo(protocolo);
	}

}
