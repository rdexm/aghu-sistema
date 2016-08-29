package br.gov.mec.aghu.exames.solicitacao.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.dominio.DominioTipoPesquisaExame;
import br.gov.mec.aghu.dominio.DominioTipoTransporteQuestionario;
import br.gov.mec.aghu.exames.business.IExameInternetStatusBean;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMotivoCancelaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioMateriaisColetarInternacaoFiltroVO;
import br.gov.mec.aghu.exames.solicitacao.vo.AbasIndicadorApresentacaoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ConfirmacaoImpressaoEtiquetaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DataProgramadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameSuggestionVO;
import br.gov.mec.aghu.exames.solicitacao.vo.HistoricoNumeroUnicoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameItemVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameResultadoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.TipoLoteVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.exames.vo.ExamesCriteriosSelecionadosVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.exames.vo.ImprimirExamesRealizadosAtendimentosDiversosVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoColetarVO;
import br.gov.mec.aghu.exames.vo.SolicitacoesAgendaColetaAmbulatorioVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExameInternetStatus;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.view.VAelArcoSolicitacaoAghu;

@Modulo(ModuloEnum.EXAMES_LAUDOS)
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects"})
@Stateless
public class SolicitacaoExameFacade extends BaseFacade implements ISolicitacaoExameFacade {

	private static final long serialVersionUID = 3761809856583509274L;

	@EJB
	private SolicitacaoExameRN solicitacaoExameRN;

	@EJB
	private ImprimirExamesRealizadosAtendimentosDiversosON imprimirExamesRealizadosAtendimentosDiversosON;

	@EJB
	private EtiquetasRN etiquetasRN;

	@EJB
	private ItemSolicitacaoExameON itemSolicitacaoExameON;

	@EJB
	private SolicitacaoExameListaON solicitacaoExameListaON;

	@EJB
	private HistoricoNumeroUnicoON historicoNumeroUnicoON;

	@EJB
	private ImprimirAgendaColetarAmbulatorioON imprimirAgendaColetarAmbulatorioON;

	@EJB
	private ExameInternetStatusON exameInternetStatusON;

	@EJB
	private EtiquetasON etiquetasON;

	@EJB
	private LoteExameUsualON loteExameUsualON;

	@EJB
	private SolicitacaoExameON solicitacaoExameON;

	@EJB
	private RelatorioMateriaisColetarRN relatorioMateriaisColetarRN;

	@EJB
	private AelSismamaMamoResON aelSismamaMamoResON;

	@EJB
	private ItemSolicitacaoExameRN itemSolicitacaoExameRN;

	@EJB
	private ExameProvaCruzadaRN exameProvaCruzadaRN;

	@EJB
	private ItemSolicitacaoExameEnforceRN itemSolicitacaoExameEnforceRN;

	@EJB
	private EtiquetasRedomeON etiquetasRedomeON;

	@EJB
	private ProcessarExameInternetStatusON processarExamesInternetStatusON;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

	@Inject
	private AelMotivoCancelaExamesDAO aelMotivoCancelaExamesDAO;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@Inject
	private AelPermissaoUnidSolicDAO aelPermissaoUnidSolicDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	/**
	 * @param valor
	 */
	@Override
	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorParametro(final String valor) {
		return this.getRelatorioMateriaisColetarRN().buscarAghUnidadesFuncionaisPorParametro(valor);
	}

	/**
	 * @param filtro
	 * @throws BaseException
	 */
	@Override
	@Secure("#{s:hasPermission('relatorioMateriaisColetarInternacao','pesquisar')}")
	public List<SolicitacaoColetarVO> buscaMateriaisColetarInternacao(final RelatorioMateriaisColetarInternacaoFiltroVO filtro,
			String nomeMicrocomputador) throws BaseException {
		return this.getRelatorioMateriaisColetarRN().buscaMateriaisColetarInternacao(filtro, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('imprimirAgendaColetaAmbulatorio','executar')}")
	public List<SolicitacoesAgendaColetaAmbulatorioVO> pesquisarAgendaColetaAmbulatorio(
			final RelatorioMateriaisColetarInternacaoFiltroVO filtro) throws BaseException {
		return getImprimirAgendaColetarAmbulatorioON().pesquisarAgendaColetaAmbulatorio(filtro);
	}

	/**
	 * @param solicExame
	 * @param itemSolicExameExcluidos
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN#atualizar(br.gov.mec.aghu.model.AelSolicitacaoExames,
	 *      java.util.List)
	 */
	@Override
	public AelSolicitacaoExames atualizar(final AelSolicitacaoExames solicExame,
			final List<AelItemSolicitacaoExames> itemSolicExameExcluidos, String nomeMicrocomputador, RapServidores servidorLogado) throws BaseException {
		return this.getSolicitacaoExameRN().atualizar(solicExame, itemSolicExameExcluidos, nomeMicrocomputador, servidorLogado,new Date());
	}

	/**
	 * @param atendimento
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN#recuperarLocalPaciente(br.gov.mec.aghu.model.AghAtendimentos)
	 */
	@Override
	@BypassInactiveModule
	public String recuperarLocalPaciente(final AghAtendimentos atendimento) {
		return this.getSolicitacaoExameRN().recuperarLocalPaciente(atendimento);
	}

	@Override
	public RapServidores buscarConsulta(SolicitacaoExameVO solicitacaoExameVO) throws BaseException {
		return this.getSolicitacaoExameRN().buscarConsulta(solicitacaoExameVO);
	}

	/**
	 * @param atendimento
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN#excluirSolicitacaoExamesPorAtendimento(br.gov.mec.aghu.model.AghAtendimentos)
	 */
	@Override
	@BypassInactiveModule
	public void excluirSolicitacaoExamesPorAtendimento(final AghAtendimentos atendimento) {
		this.getSolicitacaoExameRN().excluirSolicitacaoExamesPorAtendimento(atendimento);
	}

	/**
	 * @param soeSeq
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN#atualizarIndImpressaoSolicitacaoExames(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public void atualizarIndImpressaoSolicitacaoExames(final Integer soeSeq, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getSolicitacaoExameRN().atualizarIndImpressaoSolicitacaoExames(soeSeq, nomeMicrocomputador, new Date(), servidorLogado);
	}

	@Override
	public void gravar(AelSolicitacaoExames solicitacaoExame, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getSolicitacaoExameON().gravar(solicitacaoExame, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * @param solicitacaoExameVO
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#gravar(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO)
	 */
	@Override
	public SolicitacaoExameVO gravar(final SolicitacaoExameVO solicitacaoExameVO, String nomeMicrocomputador) throws BaseException {
		return this.getSolicitacaoExameON().gravar(solicitacaoExameVO, nomeMicrocomputador, new Date());
	}
	
	@Override
	public void finalizarGeracaoSolicExamePendente(Integer seqSolicExame) throws BaseException {
		this.getSolicitacaoExameON().finalizarGeracaoSolicExamePendente(seqSolicExame);		
	}

	/**
	 * @param solicitacaoExameVO
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#gravar(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO)
	 */
	@Override
	public List<ItemContratualizacaoVO> gravarContratualizacao(final AelSolicitacaoExames aelSolicitacaoExames,
			final List<ItemContratualizacaoVO> listaItensVO, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return this.getSolicitacaoExameRN().inserirContratualizacao(aelSolicitacaoExames, listaItensVO, nomeMicrocomputador, new Date(), servidorLogado);
	}

	/**
	 * @param solicitacaoExameVO
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#executarValidacoesPosGravacaoSolicitacaoExame(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO)
	 */
	@Override
	public List<String> executarValidacoesPosGravacaoSolicitacaoExame(SolicitacaoExameVO solicitacaoExameVO,
			String nomeMicrocomputador, final Date dataFimVinculoServidor,
			List<ConfirmacaoImpressaoEtiquetaVO> listaConfirmaImpressaoEtiquetas, AghUnidadesFuncionais unidadeExecutora)
			throws BaseException {
		return this.getSolicitacaoExameON().executarValidacoesPosGravacaoSolicitacaoExame(solicitacaoExameVO,
				nomeMicrocomputador, dataFimVinculoServidor, listaConfirmaImpressaoEtiquetas, unidadeExecutora);
	}

	@Override
	public void atualizarItensPendentesAmbulatorio(SolicitacaoExameVO solicitacaoExameVO, AelSolicitacaoExames solicEx,
			String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.getSolicitacaoExameON().atualizarItensPendentesAmbulatorio(solicitacaoExameVO, solicEx, nomeMicrocomputador,
				dataFimVinculoServidor);
	}

	/**
	 * @param atendimentoSeq
	 * @param atendimentoDiversoSeq
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscaSolicitacaoExameVO(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public SolicitacaoExameVO buscaSolicitacaoExameVO(final Integer atendimentoSeq, final Integer atendimentoDiversoSeq)
			throws ApplicationBusinessException {
		return this.getSolicitacaoExameON().buscaSolicitacaoExameVO(atendimentoSeq, atendimentoDiversoSeq);
	}

	/**
	 * @param seq
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscarItensExames(java.lang.Integer)
	 */
	@Override
	public List<AelItemSolicitacaoExames> buscarItensExames(final Integer seq) throws ApplicationBusinessException {
		return this.getSolicitacaoExameON().buscarItensExames(seq);
	}

	/**
	 * @param seq
	 * @param unfSeq
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscarItensExamesAExecutar(java.lang.Integer,
	 *      java.lang.Short)
	 */
	@Override
	public List<AelItemSolicitacaoExames> buscarItensExamesAExecutar(final Integer seq, final Short unfSeq)
			throws ApplicationBusinessException {
		return this.getSolicitacaoExameON().buscarItensExamesAExecutar(seq, unfSeq);
	}

	/**
	 * @param soeSeq
	 * @param itemSeq
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscarAmostrasItemExame(java.lang.Integer,
	 *      java.lang.Short)
	 */
	@Override
	public List<AelAmostraItemExames> buscarAmostrasItemExame(final Integer soeSeq, final Short itemSeq)
			throws ApplicationBusinessException {
		return this.getSolicitacaoExameON().buscarAmostrasItemExame(soeSeq, itemSeq);
	}

	/**
	 * @param soeSeq
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscaExameCancelamentoSolicRespons(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public VAelSolicAtendsVO buscaExameCancelamentoSolicRespons(final Integer soeSeq) throws BaseException {
		return this.getSolicitacaoExameON().buscaExameCancelamentoSolicRespons(soeSeq);
	}

	/**
	 * @param seq
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscaDetalhesSolicitacaoExameVO(java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('detalharSolicitacaoExames','pesquisar')}")
	public SolicitacaoExameVO buscaDetalhesSolicitacaoExameVO(final Integer seq) throws ApplicationBusinessException {
		return this.getSolicitacaoExameON().buscaDetalhesSolicitacaoExameVO(seq);
	}

	/**
	 * @param unidadeExecutora
	 * @param dtSolicitacao
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#listarSolicitacaoExamesUnExecutora(br.gov.mec.aghu.model.AghUnidadesFuncionais,
	 *      java.util.Date)
	 */
	@Override
	@Secure("#{s:hasPermission('consultarSolicitacaoInternacaoUnidadesFechadas','pesquisar')}")
	public List<VAelSolicAtendsVO> listarSolicitacaoExamesUnExecutora(final AghUnidadesFuncionais unidadeExecutora, final Date dtSolicitacao)
			throws BaseException {
		return this.getSolicitacaoExameON().listarSolicitacaoExamesUnExecutora(unidadeExecutora, dtSolicitacao);
	}

	/**
	 * @param atendimentoSeq
	 * @param origemSumarioAlta
	 * @return 
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#verificarPermissoesParaSolicitarExame(java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')  or s:hasPermission('solicitarExamesLote','executar')}")
	public AghAtendimentos verificarPermissoesParaSolicitarExame(final Integer atendimentoSeq, boolean origemSumarioAlta) throws BaseException {
		return this.getSolicitacaoExameON().verificarPermissoesParaSolicitarExame(atendimentoSeq, origemSumarioAlta);
	}

	/**
	 * @param atendimentoSeq
	 * @return 
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#verificarPermissoesParaSolicitarExame(java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')  or s:hasPermission('solicitarExamesLote','executar')}")
	public AghAtendimentos verificarPermissoesParaSolicitarExame(final Integer atendimentoSeq) throws BaseException {
		return this.getSolicitacaoExameON().verificarPermissoesParaSolicitarExame(atendimentoSeq, false);
	}

	/**
	 * @param solicitacoes
	 * @param unidadeExecutora
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#imprimirSolicitacoesColetar(java.util.List,
	 *      br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	@Override
	@Secure("#{s:hasPermission('monitorarColetasEmergencia','imprimirSolicitacoesColeta')}")
	public List<SolicitacaoColetarVO> imprimirSolicitacoesColetar(final List<Integer> solicitacoes,
			final AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador) throws BaseException {
		return this.getSolicitacaoExameON().imprimirSolicitacoesColetar(solicitacoes, unidadeExecutora, nomeMicrocomputador, new Date());
	}

	/**
	 * @param vinculo
	 * @param matricula
	 * @param diasServidorFimVinculoPermitidoSolicitarExame
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#pesquisarQualificacoesSolicitacaoExameSemPermissao(java.lang.Short,
	 *      java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public List<RapQualificacao> pesquisarQualificacoesSolicitacaoExameSemPermissao(final Short vinculo, final Integer matricula,
			final Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		return this.getSolicitacaoExameON().pesquisarQualificacoesSolicitacaoExameSemPermissao(vinculo, matricula,
				diasServidorFimVinculoPermitidoSolicitarExame);
	}

	/**
	 * @param filter
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#verificarFiltrosPesquisaSolicitacaoExame(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public void verificarFiltrosPesquisaSolicitacaoExame(final SolicitacaoExameFilter filter) throws ApplicationBusinessException {
		this.getSolicitacaoExameListaON().verificarFiltrosPesquisaSolicitacaoExame(filter);
	}

	/**
	 * @param filter
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPacienteTotalRegistros(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public Long pesquisarAtendimentosPacienteTotalRegistros(final SolicitacaoExameFilter filter) throws BaseException {
		return this.getSolicitacaoExameListaON().pesquisarAtendimentosPacienteTotalRegistros(filter);
	}

	/**
	 * @param filter
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPacienteInternadoCount(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public Long pesquisarAtendimentosPacienteInternadoCount(final SolicitacaoExameFilter filter) throws BaseException {
		return this.getSolicitacaoExameListaON().pesquisarAtendimentosPacienteInternadoCount(filter);
	}

	/**
	 * @param filter
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPacienteUnico(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public SolicitacaoExameItemVO pesquisarAtendimentosPacienteUnico(final SolicitacaoExameFilter filter) throws BaseException {
		return this.getSolicitacaoExameListaON().pesquisarAtendimentosPacienteUnico(filter);
	}

	/**
	 * @param filter
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPacienteInternadoUnico(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public SolicitacaoExameItemVO pesquisarAtendimentosPacienteInternadoUnico(final SolicitacaoExameFilter filter) throws BaseException {
		return this.getSolicitacaoExameListaON().pesquisarAtendimentosPacienteInternadoUnico(filter);
	}

	/**
	 * @param filter
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#pesquisarAtendimentosPaciente(br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter,
	 *      java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public SolicitacaoExameResultadoVO pesquisarAtendimentosPaciente(final SolicitacaoExameFilter filter, final Integer firstResult,
			final Integer maxResult, final String orderProperty, final boolean asc) throws BaseException {
		return this.getSolicitacaoExameListaON().pesquisarAtendimentosPaciente(filter, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * @param filtro
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#listarExamesCancelamentoSolicitante(br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO)
	 */
	@Override
	public SolicitacaoExameResultadoVO listarExamesCancelamentoSolicitante(final PesquisaExamesFiltroVO filtro) throws BaseException {
		return this.getSolicitacaoExameListaON().listarExamesCancelamentoSolicitante(filtro);
	}

	/**
	 * @param parametro
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#buscarUnidadeFuncionais(java.lang.String)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public List<AghUnidadesFuncionais> buscarUnidadeFuncionais(final String parametro) {
		return this.getSolicitacaoExameListaON().buscarUnidadeFuncionais(parametro);
	}

	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public Long buscarUnidadeFuncionaisCount(final String parametro) {
		return this.getSolicitacaoExameListaON().buscarUnidadeFuncionaisCount(parametro);
	}

	/**
	 * @param seq
	 * @param exame
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#listarPesquisaIntervaloColeta(java.lang.String,
	 *      br.gov.mec.aghu.model.AelUnfExecutaExames)
	 */
	@Override
	public List<AelTmpIntervaloColeta> listarPesquisaIntervaloColeta(final String seq, final AelUnfExecutaExames exame) {
		return this.getSolicitacaoExameListaON().listarPesquisaIntervaloColeta(seq, exame);
	}

	/**
	 * @param objPesquisa
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameListaON#listarRegiaoAnatomica(java.lang.String)
	 */
	@Override
	public List<AelRegiaoAnatomica> listarRegiaoAnatomica(final String objPesquisa, final List<Integer> regioesMama) {
		return this.getSolicitacaoExameListaON().listarRegiaoAnatomica(objPesquisa, regioesMama);
	}

	/**
	 * @param dominio
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.LoteExameUsualON#getDadosLote(br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote)
	 */
	@Override
	public List<TipoLoteVO> getDadosLote(final DominioSolicitacaoExameLote dominio) {
		return this.getLoteExameUsualON().getDadosLote(dominio);
	}

	/**
	 * @param loteExameUsual
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.LoteExameUsualON#inserirAelLoteExameUsual(br.gov.mec.aghu.model.AelLoteExameUsual)
	 */
	@Override
	public void inserirAelLoteExameUsual(final AelLoteExameUsual loteExameUsual) throws BaseException {
		this.getLoteExameUsualON().inserirAelLoteExameUsual(loteExameUsual);
	}

	/**
	 * @param loteExameUsual
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.LoteExameUsualON#atualizarAelLoteExameUsual(br.gov.mec.aghu.model.AelLoteExameUsual)
	 */
	@Override
	public void atualizarAelLoteExameUsual(final AelLoteExameUsual loteExameUsual) throws BaseException {
		this.getLoteExameUsualON().atualizarAelLoteExameUsual(loteExameUsual);
	}

	/**
	 * @param loteSeq
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.LoteExameUsualON#removerAelLoteExameUsual(java.lang.Short)
	 */
	@Override
	public void removerAelLoteExameUsual(final Short loteSeq) throws BaseException {
		this.getLoteExameUsualON().removerAelLoteExameUsual(loteSeq);
	}

	/**
	 * @param itens
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#cancelarItensResponsabilidadeSolicitante(java.util.List)
	 */
	@Override
	@BypassInactiveModule
	public void cancelarItensResponsabilidadeSolicitante(final List<ItemSolicitacaoExameCancelamentoVO> itens, String nomeMicrocomputador)
			throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getItemSolicitacaoExameRN().cancelarItensResponsabilidadeSolicitante(itens, nomeMicrocomputador, new Date(), servidorLogado);
	}

	/**
	 * @param item
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#atualizar(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames atualizar(final AelItemSolicitacaoExames item, String nomeMicrocomputador, final AelItemSolicitacaoExames itemOriginal) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return this.getItemSolicitacaoExameRN().atualizar(item, itemOriginal, nomeMicrocomputador, new Date(), servidorLogado);
	}
	
	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames atualizar(final AelItemSolicitacaoExames item, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return this.getItemSolicitacaoExameRN().atualizar(item, null, nomeMicrocomputador, new Date(), servidorLogado);
	}	

	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames atualizar(final AelItemSolicitacaoExames item, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return this.getItemSolicitacaoExameRN().atualizar(item, itemSolicitacaoExameOriginal, false, nomeMicrocomputador, new Date(), servidorLogado);
	}

	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames atualizar(final AelItemSolicitacaoExames item, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, boolean atualizarItemAmostra, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return this.getItemSolicitacaoExameRN().atualizar(item, itemSolicitacaoExameOriginal, atualizarItemAmostra, nomeMicrocomputador, new Date(), servidorLogado);
	}

	
	/**
	 * @param item
	 * @param atualizarItemAmostra
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#atualizar(br.gov.mec.aghu.model.AelItemSolicitacaoExames,
	 *      boolean)
	 */
	@Override
	public AelItemSolicitacaoExames atualizar(final AelItemSolicitacaoExames item, final boolean atualizarItemAmostra,
			String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return this.getItemSolicitacaoExameRN().atualizar(item, atualizarItemAmostra, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
	}

	/**
	 * @param item
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#atualizarSemFlush(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	@Override
	public AelItemSolicitacaoExames atualizarSemFlush(final AelItemSolicitacaoExames item, String nomeMicrocomputador, final Boolean flush)
			throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return this.getItemSolicitacaoExameRN().atualizarSemFlush(item, nomeMicrocomputador, new Date(), flush, servidorLogado);
	}

	/**
	 * @param aelItemSolicitacaoExames
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#receberItemSolicitacaoExame(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	@Override
	public void receberItemSolicitacaoExame(final AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador)
			throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getItemSolicitacaoExameRN().receberItemSolicitacaoExame(aelItemSolicitacaoExames, nomeMicrocomputador, new Date(), servidorLogado);
	}

	/**
	 * @param aelItemSolicitacaoExames
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#voltarItemSolicitacaoExame(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	@Override
	public void voltarItemSolicitacaoExame(final AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getItemSolicitacaoExameRN().voltarItemSolicitacaoExame(aelItemSolicitacaoExames, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
	}

	@Override
	public Boolean verificaDiaPlantao(){
		return this.getRelatorioMateriaisColetarRN().verificaDiaPlantao();
	}
	
	@Override
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorId(Integer soeSeq, Short seqp) {
		return getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(soeSeq, seqp);
	}	
	
	@Override
	public List<AelItemSolicitacaoExames> buscarItensPorAmostra(Integer soeSeq, Integer amoSeqp) {
		return getAelItemSolicitacaoExameDAO().buscarItensPorAmostra(soeSeq, amoSeqp);
	}	
	
	
	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorAtendimentoCirurgico(
			Integer atdSeq, Integer crgSeq, String exaSigla, String[] situacao, Boolean geradoAutomatico){
		return getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorAtendimentoCirurgico(atdSeq, crgSeq, exaSigla, situacao, geradoAutomatico);
	}

	
	/**
	 * Se impressa etiqueta de amostra, altera a situação do item de 'a coletar' para 'em coleta' 
	 * 
	 * @param solicitacao
	 * @param item
	 * @param nomeMicrocomputador
	 * @param dataFimVinculoServidor
	 * @throws BaseException 
	 * 
	 */
	
	@Override
	@BypassInactiveModule
	public void atualizarItemSolicitacaEmColeta(Integer soeSeq, Short seqp, String nomeMicrocomputador) throws BaseException{
		 getEtiquetasON().atualizarItemSolicitacaEmColeta(soeSeq, seqp, nomeMicrocomputador, new Date());
	}
	
	/**
	 * Se impressa etiqueta de amostra, altera a situação da amostra de 'a coletar' para 'em coleta' 
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @param nomeMicrocomputador
	 * @throws BaseException 
	 * 
	 */
	@Override
	@BypassInactiveModule
	public void atualizarAmostraSolicitacaEmColeta(Integer iseSoeSeq, Short iseSeqp,Integer amoSoeSeq, Integer amoSeqp, String nomeMicrocomputador) throws BaseException{
		 getEtiquetasON().atualizarAmostraSolicitacaEmColeta(iseSoeSeq, iseSeqp, amoSoeSeq, amoSeqp, nomeMicrocomputador);
	}
	
	/**
	 * Obter descricao usual de exame
	 * 
	 * @param solicitacao
	 * @param item
	 * @throws BaseException 
	 * 
	 */
	
	@Override
	@BypassInactiveModule
	public String obterDescricaoUsualExame(Integer soeSeq, Short seqp){
		 return getEtiquetasON().obterDescricaoUsualExame(soeSeq, seqp);
	}
	
	
	/**
	 * Verifica se usuário tem permissão para coletar amostra
	 * 
	 * @param login
	 * @throws BaseException 
	 * 
	 */
	
	@Override
	@BypassInactiveModule
	public Boolean verificarUsuarioLogadoColetador(String login){
		 return getEtiquetasON().verificarUsuarioLogadoColetador(login);
	}
	
	/**
	 * Verifica se usuario solicitador tem permissao para coletar amostra de exames
	 * @param solicitacao
	 * @param item
	 * 
	 */
	
	@Override
	@BypassInactiveModule
	public Boolean verificarUsuarioSolicitanteColetador(Integer soeSeq, Short seqp){
		return getEtiquetasON().verificarUsuarioSolicitanteColetador(soeSeq, seqp);
	}
	

	/**
	 * @param itemEstorno
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN#estornarItemSolicitacaoExame(br.gov.mec.aghu.model.AelItemSolicitacaoExames)
	 */
	@Override
	public void estornarItemSolicitacaoExame(final AelItemSolicitacaoExames itemEstorno, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getItemSolicitacaoExameRN().estornarItemSolicitacaoExame(itemEstorno, nomeMicrocomputador, new Date(), servidorLogado);
	}

	/**
	 * @param itemSolicitacaoExameVO
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#validarItemSolicitacaoExameErros(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO)
	 */
	@Override
	public void validarItemSolicitacaoExameErros(final ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			final Map<String, Object> questionarioSismama) throws BaseException {
		this.getItemSolicitacaoExameON().validarItemSolicitacaoExameErros(itemSolicitacaoExameVO, questionarioSismama);
	}

	@Override
	public void gerarDependentes(final ItemSolicitacaoExameVO itemSolicitacaoExameVO, final AghUnidadesFuncionais unfTrabalho)
			throws BaseException {
		this.getItemSolicitacaoExameON().gerarDependentes(itemSolicitacaoExameVO, unfTrabalho);
	}

	/**
	 * @param itemSolicitacaoExameVO
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#validarItemSolicitacaoExameMensagens(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO)
	 */
	@Override
	public BaseListException validarItemSolicitacaoExameMensagens(final ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws BaseException {
		return this.getItemSolicitacaoExameON().validarItemSolicitacaoExameMensagens(itemSolicitacaoExameVO);
	}

	/**
	 * @param unfSolicitante
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#verificarUrgenciaItemSolicitacaoExame(br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	@Override
	public Boolean verificarUrgenciaItemSolicitacaoExame(final AghUnidadesFuncionais unfSolicitante) {
		return this.getItemSolicitacaoExameON().verificarUrgenciaItemSolicitacaoExame(unfSolicitante);
	}

	/**
	 * @param unfSolicitante
	 * @param atendimento
	 * @param atendimentoDiverso
	 * @param unfExecutaExame
	 * @param unfTrabalho
	 * @param itemSolicEx
	 * @param solicitacaoExameVo
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#obterSituacaoExameSugestao(br.gov.mec.aghu.model.AghUnidadesFuncionais,
	 *      br.gov.mec.aghu.model.AghAtendimentos,
	 *      br.gov.mec.aghu.model.AelAtendimentoDiversos,
	 *      br.gov.mec.aghu.model.AelUnfExecutaExames,
	 *      br.gov.mec.aghu.model.AghUnidadesFuncionais,
	 *      br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO,
	 *      br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO)
	 */
	@Override
	public AelSitItemSolicitacoes obterSituacaoExameSugestao(final AghUnidadesFuncionais unfSolicitante, final AghAtendimentos atendimento,
			final AelAtendimentoDiversos atendimentoDiverso, final AelUnfExecutaExames unfExecutaExame,
			final AghUnidadesFuncionais unfTrabalho, final ItemSolicitacaoExameVO itemSolicEx, final SolicitacaoExameVO solicitacaoExameVo)
			throws BaseException {
		return this.getItemSolicitacaoExameON().obterSituacaoExameSugestao(unfSolicitante, atendimento, atendimentoDiverso,
				unfExecutaExame, unfTrabalho, itemSolicEx, solicitacaoExameVo);
	}

	/**
	 * @param unfExecutaExame
	 * @param unfSolicitante
	 * @param origem
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#obterIndicadorApresentacaoAbas(br.gov.mec.aghu.model.AelUnfExecutaExames,
	 *      br.gov.mec.aghu.model.AghUnidadesFuncionais,
	 *      br.gov.mec.aghu.dominio.DominioOrigemAtendimento)
	 */
	@Override
	public AbasIndicadorApresentacaoVO obterIndicadorApresentacaoAbas(final AelUnfExecutaExames unfExecutaExame,
			final AghUnidadesFuncionais unfSolicitante, final AghUnidadesFuncionais unfTrabalho, final DominioOrigemAtendimento origem,
			final AelSolicitacaoExames solicitacao, final Integer atdSeq, final Integer atdDivSeq, final Boolean indGeradoAutomatico,
			final Boolean includeUnidadeTrabalho, final DominioTipoTransporteQuestionario tipoTransporte)
			throws BaseException {
		return this.getItemSolicitacaoExameON().obterIndicadorApresentacaoAbas(unfExecutaExame, unfSolicitante, unfTrabalho, origem,
				solicitacao, atdSeq, atdDivSeq, indGeradoAutomatico, includeUnidadeTrabalho, tipoTransporte);
	}

	/**
	 * @param SolicitacaoExameVO
	 *            Vo de solicitacao de exame
	 * @return AghUnidadesFuncionais
	 */
	@Override
	public AghUnidadesFuncionais obterUnidadeTrabalhoSolicitacaoExame(final SolicitacaoExameVO solicitacaoExame) throws BaseException {
		return this.getSolicitacaoExameON().obterUnidadeTrabalhoSolicitacaoExame(solicitacaoExame);
	}

	/**
	 *  @param usuarioLogado
	 * @param SolicitacaoExameVO
	 *            Vo de solicitacao de exame
	 * @return AghUnidadesFuncionais
	 */
	@Override
	public Boolean mostrarUnidadeTrabalhoSolicitacaoExame(final SolicitacaoExameVO solicitacaoExame) throws BaseException {
		return this.getSolicitacaoExameON().mostrarUnidadeTrabalhoSolicitacaoExame(solicitacaoExame);
	}
	
	@Override
	public RapServidores buscarResponsavelConsultaOuEquipe(Integer numeroConsulta){
		return this.getSolicitacaoExameON().buscarResponsavelConsultaOuEquipe(numeroConsulta);
	}
	
	/**
	 * @param nomeExame
	 * @param seqUnidadeFuncional
	 * @param isSus 
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisaUnidadeExecutaSinonimoExame(java.lang.String)
	 */
	@Override
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(final String nomeExame, Short seqUnidade, Boolean isSus, Boolean isOrigemInternacao, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, DominioTipoPesquisaExame tipoPesquisa) {
		return this.getItemSolicitacaoExameON().pesquisaUnidadeExecutaSinonimoExame(nomeExame,seqUnidade,isSus, isOrigemInternacao, seqAtendimento, filtrarExamesProcEnfermagem, false, tipoPesquisa);
	}

	@Override
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(final String nomeExame, Short seqUnidade, Boolean isSus, Boolean isOrigemInternacao, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean buscaCompleta, DominioTipoPesquisaExame tipoPesquisa) {
		return this.getItemSolicitacaoExameON().pesquisaUnidadeExecutaSinonimoExame(nomeExame,seqUnidade, isSus, isOrigemInternacao, seqAtendimento, filtrarExamesProcEnfermagem, buscaCompleta, tipoPesquisa);
	}
	/**
	 * @param nomeExame
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisaUnidadeExecutaSinonimoExame(java.lang.String)
	 */
	@Override
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(final String nomeExame, DominioTipoPesquisaExame tipoPesquisa) {
		return this.getItemSolicitacaoExameON().pesquisaUnidadeExecutaSinonimoExame(nomeExame, tipoPesquisa);
	}
	
	@Override
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameAntigo(final String nomeExame) {
		return this.getItemSolicitacaoExameON().pesquisaUnidadeExecutaSinonimoExameAntigo(nomeExame);
	}

//	@Override
//	public Long pesquisaUnidadeExecutaSinonimoExameCount(final String nomeExame, Short seqUnidade, Boolean isSus, Boolean isOrigemInternacao, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem) {
//		return this.getItemSolicitacaoExameON().pesquisaUnidadeExecutaSinonimoExameCount(nomeExame,seqUnidade,isSus, isOrigemInternacao, seqAtendimento, filtrarExamesProcEnfermagem);
//	}

	/**
	 * @param leuSeq
	 * @param isOrigemInternacao 
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisaUnidadeExecutaSinonimoExameLote(java.lang.Short)
	 */
	@Override
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLote(final Short leuSeq, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean isOrigemInternacao) {
		return this.getItemSolicitacaoExameON().pesquisaUnidadeExecutaSinonimoExameLote(leuSeq,null,false, seqAtendimento, filtrarExamesProcEnfermagem, isOrigemInternacao);
	}
	
	@Override
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLote(final Short leuSeq, short seqUnidade, Boolean isSus, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem,boolean isOrigemInternacao) {
		return this.getItemSolicitacaoExameON().pesquisaUnidadeExecutaSinonimoExameLote(leuSeq,seqUnidade,isSus, seqAtendimento, filtrarExamesProcEnfermagem,isOrigemInternacao);
	}

	@Override
	public List<UnfExecutaSinonimoExameVO> pesquisaUnidadeExecutaSinonimoExameLoteSemPermissoes(Short leuSeq, List<UnfExecutaSinonimoExameVO> listaExames, Integer seqAtendimento, boolean filtrarExamesProcEnfermagem, boolean isOrigemInternacao){
		return this.getItemSolicitacaoExameON().pesquisaUnidadeExecutaSinonimoExameLoteSemPermissoes(leuSeq, listaExames, seqAtendimento, filtrarExamesProcEnfermagem,isOrigemInternacao);
	}
	
	
	/**
	 * @param itemSolicitacaoExameVO
	 * @param unidadeSolicitante
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#verificarCampoDataHora(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO,
	 *      br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	@Override
	public TipoCampoDataHoraISE verificarCampoDataHora(final ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			final AghUnidadesFuncionais unidadeSolicitante) throws ApplicationBusinessException {
		return this.getItemSolicitacaoExameON().verificarCampoDataHora(itemSolicitacaoExameVO, unidadeSolicitante);
	}

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param ordem
	 * @param b
	 * @param aelUnfExecutaExames
	 * @param aelSolicitacaoExames
	 * @param aelSitItemSolicitacoes
	 * @param fatConvenioSaude
	 * @param codigoPaciente
	 * @param prontuario2
	 * @param nomePaciente2
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String, boolean,
	 *      br.gov.mec.aghu.model.AghUnidadesFuncionais,
	 *      br.gov.mec.aghu.model.AelSolicitacaoExames,
	 *      br.gov.mec.aghu.model.AelSitItemSolicitacoes,
	 *      br.gov.mec.aghu.model.FatConvenioSaude, java.lang.String,
	 *      java.lang.Integer, java.lang.String)
	 */
	@Override
	public List<ExamesCriteriosSelecionadosVO> pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(final Integer firstResult,
			final Integer maxResult, final String ordem, final boolean b, final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSolicitacaoExames aelSolicitacaoExames, final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude, final String codigoPaciente, final Integer prontuario2, final String nomePaciente2) {
		return this.getItemSolicitacaoExameON().pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionados(firstResult, maxResult,
				ordem, b, aelUnfExecutaExames, aelSolicitacaoExames, aelSitItemSolicitacoes, fatConvenioSaude, codigoPaciente, prontuario2,
				nomePaciente2);
	}

	/**
	 * @param aelUnfExecutaExames
	 * @param aelSolicitacaoExames
	 * @param aelSitItemSolicitacoes
	 * @param fatConvenioSaude
	 * @param codigoPaciente
	 * @param prontuario
	 * @param nomePaciente
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(br.gov.mec.aghu.model.AghUnidadesFuncionais,
	 *      br.gov.mec.aghu.model.AelSolicitacaoExames,
	 *      br.gov.mec.aghu.model.AelSitItemSolicitacoes,
	 *      br.gov.mec.aghu.model.FatConvenioSaude, java.lang.String,
	 *      java.lang.Integer, java.lang.String)
	 */
	@Override
	public Long pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(final AghUnidadesFuncionais aelUnfExecutaExames,
			final AelSolicitacaoExames aelSolicitacaoExames, final AelSitItemSolicitacoes aelSitItemSolicitacoes,
			final FatConvenioSaude fatConvenioSaude, final String codigoPaciente, final Integer prontuario, final String nomePaciente) {
		return this.getItemSolicitacaoExameON().pesquisarListaAelItemSolicitacaoExamesPorCriteriosSelecionadosCount(aelUnfExecutaExames,
				aelSolicitacaoExames, aelSitItemSolicitacoes, fatConvenioSaude, codigoPaciente, prontuario, nomePaciente);
	}

	/**
	 * @param itemSolicitacaoExameVO
	 * @param unidadeSolicitante
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#getHorariosRotina(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO,
	 *      br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	@Override
	public List<DataProgramadaVO> getHorariosRotina(final ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			final AghUnidadesFuncionais unidadeSolicitante) throws BaseException {
		return this.getItemSolicitacaoExameON().getHorariosRotina(itemSolicitacaoExameVO, unidadeSolicitante);
	}

	/**
	 * @param solicitacaoExameSeq
	 * @return
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#listarItensExameCancelamentoSolicitante(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public List<ItemSolicitacaoExameCancelamentoVO> listarItensExameCancelamentoSolicitante(final Integer solicitacaoExameSeq)
			throws BaseException {
		return this.getItemSolicitacaoExameON().listarItensExameCancelamentoSolicitante(solicitacaoExameSeq);
	}

	/**
	 * @param itemSolicitacaoExamePai
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#obterDependentesOpcionais(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO)
	 */
	@Override
	public List<ItemSolicitacaoExameVO> obterDependentesOpcionais(final ItemSolicitacaoExameVO itemSolicitacaoExamePai) {
		return this.getItemSolicitacaoExameON().obterDependentesOpcionais(itemSolicitacaoExamePai);
	}

	/**
	 * @param itemSolicitacaoExameVo
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameON#obterDependentesOpcionaisSelecionados(br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO)
	 */
	@Override
	public List<ItemSolicitacaoExameVO> obterDependentesOpcionaisSelecionados(final ItemSolicitacaoExameVO itemSolicitacaoExameVo) {
		return this.getItemSolicitacaoExameON().obterDependentesOpcionaisSelecionados(itemSolicitacaoExameVo);
	}

	/**
	 * @param solicitacaoExame
	 * @param amoSeqp
	 * @param unidadeTrabalho
	 * @param impressoraCups
	 * @throws BaseException
	 * @see br.gov.mec.aghu.exames.solicitacao.business.EtiquetasON#gerarEtiquetas(br.gov.mec.aghu.model.AelSolicitacaoExames,
	 *      java.lang.Short, br.gov.mec.aghu.model.AghUnidadesFuncionais,
	 *      br.gov.mec.aghu.model.cups.ImpImpressora)
	 */
	@Override
	@Secure("#{s:hasPermission('reimprimirEtiquetasAmostras','executar')}")
	public Integer gerarEtiquetas(final AelSolicitacaoExames solicitacaoExame, final Short amoSeqp,
			final AghUnidadesFuncionais unidadeTrabalho, final String impressoraCups, String situacaoItemExame) throws BaseException {
		return this.getEtiquetasON().gerarEtiquetas(solicitacaoExame, amoSeqp, unidadeTrabalho, impressoraCups, situacaoItemExame,false);
	}

	@Override
	@BypassInactiveModule
	public String obterNomeImpressoraEtiquetas(String nomeMicro) throws BaseException {
		return this.getEtiquetasON().obterNomeImpressoraEtiquetas(nomeMicro);
	}

	public VAelArcoSolicitacaoAghu obterVAelArcoSolicitacaoAghuPeloId(final Integer seq) {
		return this.getEtiquetasRN().obterVAelArcoSolicitacaoAghuPeloId(seq);
	}

	@Override
	public AelSolicitacaoExames obterSolicitacaoExame(Integer soeSeq) {
		return this.getAelSolicitacaoExameDAO().obterPorChavePrimaria(soeSeq);
	}

	@Override
	public String gerarEtiquetaEnvelopePaciente(String nome, Integer solicitacao, String unidadeExecutora, String data, Integer prontuario) {
		return this.getEtiquetasON().gerarEtiquetaEnvelopePaciente(nome, solicitacao, unidadeExecutora, data, prontuario);
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected RelatorioMateriaisColetarRN getRelatorioMateriaisColetarRN() {
		return relatorioMateriaisColetarRN;
	}

	protected ImprimirAgendaColetarAmbulatorioON getImprimirAgendaColetarAmbulatorioON() {
		return imprimirAgendaColetarAmbulatorioON;
	}

	protected SolicitacaoExameRN getSolicitacaoExameRN() {
		return solicitacaoExameRN;
	}

	protected SolicitacaoExameON getSolicitacaoExameON() {
		return solicitacaoExameON;
	}

	protected SolicitacaoExameListaON getSolicitacaoExameListaON() {
		return solicitacaoExameListaON;
	}

	protected LoteExameUsualON getLoteExameUsualON() {
		return loteExameUsualON;
	}

	protected ItemSolicitacaoExameRN getItemSolicitacaoExameRN() {
		return itemSolicitacaoExameRN;
	}

	protected ItemSolicitacaoExameON getItemSolicitacaoExameON() {
		return itemSolicitacaoExameON;
	}

	protected EtiquetasON getEtiquetasON() {
		return etiquetasON;
	}

	@Override
	public Integer calcularHorasUteisEntreDatas(final Date d1, final Date d2) {
		return getItemSolicitacaoExameRN().calcularHorasUteisEntreDatas(d1, d2);
	}

	@Override
	public Integer calcularDiasUteisEntreDatas(final Date d1, final Date d2) {
		return getItemSolicitacaoExameRN().calcularDiasUteisEntreDatas(d1, d2);
	}

	protected HistoricoNumeroUnicoON getHistoricoNumeroUnicoON() {
		return historicoNumeroUnicoON;
	}

	@Override
	@Secure("#{s:hasPermission('consultarHistoricoNroUnico','pesquisar')}")
	@BypassInactiveModule
	public List<HistoricoNumeroUnicoVO> listarHistoricoNroUnico(final Integer soeSeq, final Short seqp, Boolean isHist) {
		return getHistoricoNumeroUnicoON().listarHistoricoNroUnico(soeSeq, seqp, isHist);
	}

	protected EtiquetasRN getEtiquetasRN() {
		return etiquetasRN;
	}

	@Override
	@BypassInactiveModule
	public String reimprimirAmostra(final AghUnidadesFuncionais unidadeExecutora,
			final Integer amostraSoeSeqSelecionada, final Short amostraSeqpSelecionada, final String nomeMicro,
			final String nomeImpressora) throws BaseException {
		return this.getEtiquetasON().reimprimirAmostra(unidadeExecutora, amostraSoeSeqSelecionada,
				amostraSeqpSelecionada, nomeMicro, nomeImpressora);
	}
	
	@Override
	public ImpImpressora obterImpressoraEtiquetas(String nomeMicro) {
		return this.getEtiquetasON().obterImpressoraEtiquetas(nomeMicro);
	}

	@Override
	public String gerarZPLNumeroAp(final String nroAp) {
		return this.getEtiquetasON().gerarZPLNumeroAp(nroAp);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> buscarItensSolicitacaoExamePorAtendimentoParamentro(Integer atdSeq, List<String> situacao) {
		return getAelItemSolicitacaoExameDAO().buscarItensSolicitacaoExamePorAtendimentoParamentro(atdSeq, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> pesquisarItensSolicitacoesExamesPorSolicitacao(final Integer soeSeq) {
		return getAelItemSolicitacaoExameDAO().pesquisarItensSolicitacoesExamesPorSolicitacao(soeSeq);
	}
	
	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorAtendimento(
			Integer atdSeq, String exaSigla, String[] situacao){
		return getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorAtendimento(atdSeq,exaSigla, situacao);
	}

	@Override
	@BypassInactiveModule
	public AelSolicitacaoExames obterAelSolicitacaoExamePorAtdSeq(Integer atdSeq) {
		return getAelSolicitacaoExameDAO().obterAelSolicitacaoExamePorAtdSeq(atdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> obterAelItemSolicitacaoExamesPorUfeEmaManSeqSoeSeq(Integer seq, Integer soeSeq) {
		return getAelItemSolicitacaoExameDAO().obterAelItemSolicitacaoExamesPorUfeEmaManSeqSoeSeq(seq, soeSeq);
	}

	@Override
	public AelSitItemSolicitacoes obterAelSitItemSolicitacoes(String codigo) {
		return getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public AelMotivoCancelaExames obterAelMotivoCancelaExames(Short seq) {
		return getAelMotivoCancelaExamesDAO().obterPorChavePrimaria(seq);
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	protected AelMotivoCancelaExamesDAO getAelMotivoCancelaExamesDAO() {
		return aelMotivoCancelaExamesDAO;
	}

	protected AelSismamaMamoResON getAelSismamaMamoResRN() {
		return aelSismamaMamoResON;
	}

	@Override
	public List<ImprimirExamesRealizadosAtendimentosDiversosVO> imprimirExamesRealizadosAtendimentosDiversos(Date dataInicial,
			Date dataFinal, DominioSimNao grupoSus, FatConvenioSaude convenioSaude) throws ApplicationBusinessException {
		return getImprimirExamesRealizadosAtendimentosDiversosON().imprimirExamesRealizadosAtendimentosDiversos(dataInicial, dataFinal,
				grupoSus, convenioSaude);
	}

	public ImprimirExamesRealizadosAtendimentosDiversosON getImprimirExamesRealizadosAtendimentosDiversosON() {
		return imprimirExamesRealizadosAtendimentosDiversosON;
	}

	@Override
	public Map<String, Object> inicializarMapSismama() {
		return this.getAelSismamaMamoResRN().inicializarMapSismama();
	}

	public ExameInternetStatusON getExamesInternetStatusON() {
		return exameInternetStatusON;
	}

	@Override
	@BypassInactiveModule
	public boolean inserirFilaExamesLiberados(final AghJobDetail job) {
		return this.getExamesInternetStatusON().inserirFilaExamesLiberados(job);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> buscarItemExamesLiberadosPorGrupo(Integer soeSeq, Integer grupo) {
		return this.getAelItemSolicitacaoExameDAO().buscarItemExamesLiberadosPorGrupo(soeSeq, grupo);
	}

	@Override
	@BypassInactiveModule
	public String gerarXmlEnvioExameInternet(List<AelItemSolicitacaoExames> listaItensAgrupados, Integer seqExameInternetGrupo)
			throws BaseException {
		return this.getExamesInternetStatusON().gerarXmlEnvioExameInternet(listaItensAgrupados, seqExameInternetGrupo);
	}

	@Override
	@BypassInactiveModule
	public void inserirFilaLaudoExames(AelSolicitacaoExames solicitacaoExame, List<AelItemSolicitacaoExames> listItemSolicitacao,
			Integer seqGrupo, byte[] arquivoLaudo, String arquivoXml) {
		this.getExamesInternetStatusON().inserirFilaLaudoExames(solicitacaoExame, listItemSolicitacao, seqGrupo, arquivoLaudo, arquivoXml);
	}

	@Override
	@BypassInactiveModule
	public String gerarToken() throws BaseException {
		return this.getExamesInternetStatusON().gerarToken();
	}

	@Override
	@BypassInactiveModule
	public ByteArrayOutputStream buildResultadoPDF(final AelSolicitacaoExames solicitacaoExame, final Integer seqGrupoExame,
			final String token) throws IOException, BaseException {
		return this.getExamesInternetStatusON().buildResultadoPDF(solicitacaoExame, seqGrupoExame, token);
	}

	@Override
	@BypassInactiveModule
	public String obterSolicitanteExame(AelExameInternetStatus exameInternetStatus) {
		return this.getExamesInternetStatusON().obterSolicitanteExame(exameInternetStatus);
	}

	@Override
	@BypassInactiveModule
	public boolean inserirFilaExamesLiberados(MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO, boolean isReenvio) {
		return this.getExamesInternetStatusON().inserirFilaExamesLiberados(mensagemSolicitacaoExameGrupoVO, isReenvio);
	}

	@Override
	@BypassInactiveModule
	public void inserirStatusInternet(AelSolicitacaoExames solicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExame,
			Date dataStatus, DominioSituacaoExameInternet situacao, DominioStatusExameInternet status, String mensagem,
			RapServidores servidor) {
		ServiceLocator.getBean(IExameInternetStatusBean.class, "aghu-exames").inserirStatusInternetMesmaTransacao(solicitacaoExame, itemSolicitacaoExame,
				dataStatus, situacao, status, mensagem, servidor);
	}

	@Override
	@BypassInactiveModule
	public void atualizarStatusInternet(Integer soeSeq, Integer grupoSeq, DominioStatusExameInternet statusAtualiza,
			DominioSituacaoExameInternet situacao, DominioStatusExameInternet statusNovo, String mensagemErro) {
		ServiceLocator.getBean(IExameInternetStatusBean.class, "aghu-exames").atualizarStatusInternet(soeSeq, grupoSeq, statusAtualiza,
				situacao, statusNovo, mensagemErro);
	}

	@Override
	public Boolean gerarExameProvaCruzadaTransfusional(AghAtendimentos atendimento, MbcCirurgias cirurgia, String nomeMicrocomputador,
			RapServidores responsavel, Boolean validaHemocomponente) throws BaseException {
		return getExameProvaCruzadaRN().gerarExamePCT(atendimento, cirurgia, nomeMicrocomputador, responsavel, validaHemocomponente);
	}

	@Override
	@BypassInactiveModule
	public void cancelarExameProvaCruzadaTransfusional(AghAtendimentos atendimento, MbcCirurgias cirurgia, String nomeMicrocomputador)
			throws BaseException {
		getExameProvaCruzadaRN().cancelarItemSolicitacao(atendimento, cirurgia, nomeMicrocomputador);
	}

	public ExameProvaCruzadaRN getExameProvaCruzadaRN() {
		return exameProvaCruzadaRN;
	}

	@Override
	public boolean verificarCodigoSituacaoNumeroAP(AelItemSolicitacaoExames itemSolicitacaoExame,
			AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws ApplicationBusinessException {
		return getItemSolicitacaoExameEnforceRN().verificarCodigoSituacaoNumeroAP(itemSolicitacaoExame, itemSolicitacaoExameOriginal);
	}

	protected ItemSolicitacaoExameEnforceRN getItemSolicitacaoExameEnforceRN() {
		return this.itemSolicitacaoExameEnforceRN;
	}

	@Override
	public String gerarZPLEtiquetaNumeroExame(final ImprimeEtiquetaVO imprimeEtiquetaVO) throws BaseException {
		return getEtiquetasON().gerarZPLEtiquetaNumeroExame(imprimeEtiquetaVO);
	}

	@Override
	@BypassInactiveModule
	public boolean verificaPctPendente(Integer atdSeqSelecionado, Integer crgSeq) {
		return this.getAelSolicitacaoExameDAO().verificaPctPendente(atdSeqSelecionado, crgSeq);
	}

	@Override
	@BypassInactiveModule
	public boolean verificaPctRealizado(Integer pacCodigoSelecionado) {
		return this.getAelSolicitacaoExameDAO().verificaPctRealizado(pacCodigoSelecionado);
	}
	
	@Override
	public Boolean verificaConvenioSus(AghAtendimentos atendimento,
			AelAtendimentoDiversos atendimentoDiverso) {
		return getItemSolicitacaoExameON().verificaConvenioSus(atendimento, atendimentoDiverso);
		 
	}
	
	@Override
	public ConfirmacaoImpressaoEtiquetaVO verificarImpressaoEtiqueta(
			final SolicitacaoExameVO solicitacaoExameVO,
			final AghMicrocomputador microcomputador) throws ApplicationBusinessException {
		return getEtiquetasON().verificarImpressaoEtiqueta(solicitacaoExameVO, microcomputador);
	}
	
	@Override
	public List<AelAmostraItemExames> buscarAelAmostraItemExamesAelAmostrasPorItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacao) {
		return aelAmostraItemExamesDAO.buscarAelAmostraItemExamesAelAmostrasPorItemSolicitacaoExame(itemSolicitacao);
	}
	
	@Override
	public String obterNomeImpressoraEtiquetasRedome(final String nomeMicro) throws ApplicationBusinessException,
			UnknownHostException {
		return etiquetasRedomeON.obterNomeImpressoraEtiquetasRedome(nomeMicro);
	}
	
	/**
	 * Chamada para Web Service #38474
	 * Utilizado nas estórias #864 e #27542 
	 * @param atdSeq
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Boolean verificarExameVDRLnaoSolicitado(Integer atdSeq) throws ApplicationBusinessException{
		return getItemSolicitacaoExameRN().verificarExameVDRLnaoSolicitado(atdSeq);
	}
	
	/**
	 * #39003 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public AelSolicitacaoExames buscarUltimaSolicitacaoExames(Integer atdSeq) throws ApplicationBusinessException{
		return getSolicitacaoExameRN().buscarUltimaSolicitacaoExames(atdSeq);
	}

	@Override
	public List<AelSolicitacaoExames> listarSolicitacaoExamesPorSeqAtdSituacoes(Integer atdSeq, String... situacoes) {
		return aelItemSolicitacaoExameDAO.listarSolicitacaoExamesPorSeqAtdSituacoes(atdSeq, situacoes);
	}

	@Override
	public List<AelRecomendacaoExame> verificarRecomendacaoExameQueSeraoExibidas(
			List<AelRecomendacaoExame> aelRecomendacaoExame,
			ItemSolicitacaoExameVO itemSolicitacaoExameVO) {
		return  getItemSolicitacaoExameON().verificarRecomendacaoExameQueSeraoExibidas(aelRecomendacaoExame, itemSolicitacaoExameVO);
	}

	/**
	 * @param objPesquisa
	 * @return
	 * @see br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON#buscarServidoresSolicitacaoExame(java.lang.String)
	 */
	@Override
	@Secure("#{s:hasPermission('elaborarSolicitacaoExame','elaborar')}")
	public List<RapServidores> buscarServidoresSolicitacaoExame(final String objPesquisa) {
		return this.getSolicitacaoExameON().buscarServidoresSolicitacaoExame(objPesquisa);
	}
	
	@Override
	public Long buscarServidoresSolicitacaoExameCount(String objPesquisa) {
		return this.getSolicitacaoExameON().buscarServidoresSolicitacaoExameCount(objPesquisa);
	}
	
	@Override
	public Boolean verificarSeExameSendoSolicitadoRedome(final AelItemSolicitacaoExames itemSolicitacaoExame,
			AghUnidadesFuncionais unidadeExecutora) throws BaseException {
		return getItemSolicitacaoExameON()
				.verificarSeExameSendoSolicitadoRedome(itemSolicitacaoExame, unidadeExecutora);
	}
	
	@Override
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(final AelItemSolicitacaoExames itemSolicitacaoExame)
			throws BaseException {
		return getItemSolicitacaoExameON().buscarAmostrasPorSolicitacaoExame(itemSolicitacaoExame);	
	}

    @Override
    public void gravarExtratoDoadorRedome(Integer soeSeq) throws BaseException {
        this.getSolicitacaoExameON().preparaDadosinsercaoExtratoDoadores(soeSeq);
    }

    @Override
    public Boolean validaUnidadeSolicitanteSus(Short unfSeq) {
	return aelUnfExecutaExamesDAO.buscaAelUnfExecutaExamesComPermissaoSus(unfSeq);
    }

	@Override
	@BypassInactiveModule
	public void processarExameInternet(AghJobDetail job) {
		processarExamesInternetStatusON.processarExameInternet(job);
}
	@Override
	@BypassInactiveModule
	public void reenviarExameParaPortal(MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO) {
		processarExamesInternetStatusON.reenviarExameParaPortal(mensagemSolicitacaoExameGrupoVO);
	}

	@Override
	public Boolean verificarExamePodeSolicitarUrgente(String emaExaSigla, Integer emaManSeq, Short unfSeq, Short unfSeqSolicitante) {
		AelPermissaoUnidSolic permissao = aelPermissaoUnidSolicDAO.buscarAelPermissaoUnidSolicPorEmaExaSiglaEmaManSeqUnfSeqUnfSeqSolicitante(emaExaSigla, emaManSeq, unfSeq, unfSeqSolicitante);
		if (permissao!= null){
			return permissao.getIndPermiteColetarUrgente();
		}
		return false;
	}
}
