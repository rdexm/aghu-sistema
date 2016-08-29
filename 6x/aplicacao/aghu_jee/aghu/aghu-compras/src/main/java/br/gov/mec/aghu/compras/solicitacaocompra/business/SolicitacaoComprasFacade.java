package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoAcoesPontoParadaDAO;
import br.gov.mec.aghu.compras.dao.ScoAutTempSolicitaDAO;
import br.gov.mec.aghu.compras.dao.ScoCaminhoSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLogGeracaoScMatEstocavelDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoScJnDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.FiltroReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.ItensScoDireitoAutorizacaoTempVO;
import br.gov.mec.aghu.compras.vo.PlanejamentoCompraVO;
import br.gov.mec.aghu.compras.vo.ProcessoGeracaoAutomaticaVO;
import br.gov.mec.aghu.compras.vo.RelatorioSolicitacaoCompraEstocavelVO;
import br.gov.mec.aghu.compras.vo.SolCompraVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;
import br.gov.mec.aghu.model.ScoArquivoAnexo;
import br.gov.mec.aghu.model.ScoAutTempSolicita;
import br.gov.mec.aghu.model.ScoAutTempSolicitaId;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacao;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacaoID;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLogGeracaoScMatEstocavel;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParamProgEntgAf;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoScJn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.PesqLoteSolCompVO;
import br.gov.mec.aghu.suprimentos.vo.ScoPlanejamentoVO;

@Modulo(ModuloEnum.COMPRAS)
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
@Stateless
public class SolicitacaoComprasFacade extends BaseFacade implements ISolicitacaoComprasFacade {

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private PesquisasSolicitacaoCompraON pesquisasSolicitacaoCompraON;
	@EJB
	private SolicitacaoCompraRN solicitacaoCompraRN;
	@EJB
	private ScoAutTempSolicitaON scoAutTempSolicitaON;
	@EJB
	private ScoCaminhoSolicitacaoON scoCaminhoSolicitacaoON;
	@EJB
	private SolicitacaoCompraON solicitacaoCompraON;
	@EJB
	private GerarSolicitacaoCompraAlmoxarifadoRN gerarSolicitacaoCompraAlmoxarifadoRN;
	@EJB
	private FasesSolicitacaoCompraON fasesSolicitacaoCompraON;
	@EJB
	private PlanejamentoScON planejamentoScON;

	@EJB
	private ScoParamProgEntgAfRN scoParamProgEntgAfRN;
	@EJB
	private AutorizacoesTemporariasParaGeracaoScON autorizacoesTemporariasParaGeracaoScON;
	@EJB
	private AnexarDocumentosSolicitacaoCompraON anexarDocumentosSolicitacaoCompraON;
	@EJB
	private ScoAcoesPontoParadaON scoAcoesPontoParadaON;
	@Inject
	private ScoScJnDAO scoScJnDAO;

	@Inject
	private ScoMaterialDAO scoMaterialDAO;

	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;

	@Inject
	private ScoLogGeracaoScMatEstocavelDAO scoLogGeracaoScMatEstocavelDAO;

	@Inject
	private ScoCaminhoSolicitacaoDAO scoCaminhoSolicitacaoDAO;

	@Inject
	private ScoAutTempSolicitaDAO scoAutTempSolicitaDAO;

	@Inject
	private ScoAcoesPontoParadaDAO scoAcoesPontoParadaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5875255349708260627L;

	/**
	 * Pesquisa geral de registros na tabela {@code ScoAutTempSolicita}. Pode
	 * ser filtrada por parametros de entrada.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param ScoAutTempSolicita
	 * @return Listagem contendo os registros encontrados.
	 */

	@Override
	@Secure("#{s:hasPermission('consultarPermissoesCompras', 'visualizar')}")
	public List<ScoAutTempSolicita> pesquisarAutSolicitacaoTemp(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, RapServidores servidor, Date dtInicio) {

		return scoAutTempSolicitaDAO.pesquisarAutSolicitacaoTemp(firstResult, maxResult, orderProperty, asc, centroCusto,
				servidor, dtInicio);
	}

	/**
	 * Faz a contagem de registros na tabela {@code ScoAutTempSolicita}.
	 * 
	 * @param ScoAutTempSolicita
	 * @return Inteiro indicando o numero de registros encontrados.
	 */
	@Override
	@Secure("#{s:hasPermission('consultarPermissoesCompras', 'visualizar')}")
	public Long pesquisarAutSolicitacaoTempCount(FccCentroCustos centroCusto, RapServidores servidor, Date dtInicio) {

		return scoAutTempSolicitaDAO.pesquisarAutSolicitacaoTempCount(centroCusto, servidor, dtInicio);
	}

	/**
	 * Obtem o registro de {@code ScoAutTempSolicita} a partir do {@code codigo}
	 * .
	 * 
	 * @param codigo
	 * @return Registro unico encontrado.
	 */
	@Override
	public ScoAutTempSolicita obterAutTempSolicitacao(ScoAutTempSolicitaId chavePrimaria) {
		return scoAutTempSolicitaDAO.obterPorChavePrimaria(chavePrimaria);
	}

	public ScoAutTempSolicita obterScoAutTempSolicitaFull(ScoAutTempSolicitaId chavePrimaria) {
		return scoAutTempSolicitaDAO.obterScoAutTempSolicitaFull(chavePrimaria);
	}

	/**
	 * Altera ou Insere um novo registro .
	 * 
	 * @param autTempSol
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void cadastrarAutTempSolicitacao(ScoAutTempSolicita autTempSol) throws ApplicationBusinessException {
		scoAutTempSolicitaON.cadastrar(autTempSol);
	}

	@Override
	public void excluirAutTempSolicitacao(ScoAutTempSolicitaId id) throws ApplicationBusinessException {
		scoAutTempSolicitaDAO.removerPorId(id);
	}

	/**
	 * Pesquisa paginação de ScoPontoServidor
	 */
	public List<ScoPontoServidor> pesquisarScoPontoServidor(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, RapServidores servidor,
			RapServidores servidorAutorizado, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {

		return autorizacoesTemporariasParaGeracaoScON.pesquisarScoPontoServidor(scoPontoParadaSolicitacao, servidor,
				servidorAutorizado, firstResult, maxResults, orderProperty, asc);

	}

	/**
	 * Conta paginação de ScoPontoServidor
	 * 
	 * @param scoPontoParadaSolicitacao
	 * @param servidor
	 * @return
	 */
	public Long pesquisarScoPontoServidorCount(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, RapServidores servidor,
			RapServidores servidorAutorizado) {

		return autorizacoesTemporariasParaGeracaoScON.pesquisarScoPontoServidorCount(scoPontoParadaSolicitacao, servidor,
				servidorAutorizado);

	}

	/**
	 * Pesquisa paginação de ScoDireitoAutorizacaoTemp
	 * 
	 * @param pontoServidor
	 * @param servidor
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<ScoDireitoAutorizacaoTemp> pesquisarScoDireitoAutorizacaoTemp(ScoPontoServidor pontoServidor, RapServidores servidor,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {

		return autorizacoesTemporariasParaGeracaoScON.pesquisarScoDireitoAutorizacaoTemp(pontoServidor, servidor, firstResult,
				maxResults, orderProperty, asc);
	}

	/**
	 * conta paginação de ScoDireitoAutorizacaoTemp
	 * 
	 * @param pontoServidor
	 * @param servidor
	 * @return
	 */
	@Secure("#{s:hasPermission('consultarPermissoesCompras','visualizar')}")
	public Long pesquisarScoDireitoAutorizacaoTempCount(ScoPontoServidor pontoServidor, RapServidores servidor) {

		return autorizacoesTemporariasParaGeracaoScON.pesquisarScoDireitoAutorizacaoTempCount(pontoServidor, servidor);

	}

	@Override
	public void persistirScoAcoesPontoParada(ScoAcoesPontoParada acao) {
		this.scoAcoesPontoParadaON.persistirScoAcoesPontoParada(acao);
	}

	@Override
	public void removerScoAcoesPontoParada(Long seq) {
		this.scoAcoesPontoParadaON.removerScoAcoesPontoParada(seq);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> buscarSolicitacaoCompraAssociadaNaturezaDespesa(FsoNaturezaDespesaId id, Boolean isExclusao) {
		return this.scoSolicitacoesDeComprasDAO.buscarSolicitacaoCompraAssociadaNaturezaDespesa(id, isExclusao);
	}

	@Override
	@Secure("#{s:hasPermission('consultarPermissoesCompras','visualizar')}")
	public List<ScoDireitoAutorizacaoTemp> listarScoDireitoAutorizacaoTemp(ScoPontoServidor pontoServidor, RapServidores servidor) {

		return autorizacoesTemporariasParaGeracaoScON.listarScoDireitoAutorizacaoTemp(pontoServidor, servidor);

	}

	@Override
	public void validaDataInicioFimScoDireitoAutorizacaoTemp(Date dataInicio, Date dataFim) throws ApplicationBusinessException {

		autorizacoesTemporariasParaGeracaoScON.validaDataInicioFimScoDireitoAutorizacaoTemp(dataInicio, dataFim);

	}

	@Override
	public void resgatarSc(Integer slcNumero) throws BaseException {
		fasesSolicitacaoCompraON.resgatarSc(slcNumero);
	}

	@Override
	public void validarConflitoPeriodoDatas(List<ItensScoDireitoAutorizacaoTempVO> listaGrade,
			ScoDireitoAutorizacaoTemp direitoAutorizacao, Integer idLista) throws ApplicationBusinessException {
		this.autorizacoesTemporariasParaGeracaoScON.validarConflitoPeriodoDatas(listaGrade, direitoAutorizacao, idLista);
	}

	@Override
	public void mensagemErroDuplicadoScoDireitoAutorizacaoTemp() throws ApplicationBusinessException {

		autorizacoesTemporariasParaGeracaoScON.mensagemErroDuplicadoScoDireitoAutorizacaoTemp();

	}

	@Override
	@Secure("#{s:hasPermission('cadastrarPermissoesCompras','gravar')}")
	public void inserirScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException {
		autorizacoesTemporariasParaGeracaoScON.inserirScoDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarPermissoesCompras','gravar')}")
	public void alterarScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException {

		autorizacoesTemporariasParaGeracaoScON.alterarScoDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> pesquisarSolicCompraCodigoDescricao(Object filter, ScoMaterial material,
			FsoNaturezaDespesa naturezaDespesa) {
		return this.scoSolicitacoesDeComprasDAO.pesquisarSolicCompraCodigoDescricao(filter, material, naturezaDespesa);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarPermissoesCompras','gravar')}")
	public void excluirScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException {
		autorizacoesTemporariasParaGeracaoScON.excluirScoDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp);
	}

	// #5228 - Cadastro de Caminhos da Solicitação
	@Override
	@Secure("#{s:hasPermission('consultarApoioCompras', 'visualizar')}")
	public List<ScoCaminhoSolicitacao> pesquisarCaminhoSolicitacao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada) {
		return scoCaminhoSolicitacaoON.pesquisarCaminhoSolicitacao(firstResult, maxResult, orderProperty, asc, origemParada,
				destinoParada);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioCompras', 'visualizar')}")
	public Long pesquisarCaminhoSolicitacaoCount(ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada) {
		return scoCaminhoSolicitacaoON.pesquisarCaminhoSolicitacaoCount(origemParada, destinoParada);
	}

	@Override
	public ScoCaminhoSolicitacao obterCaminhoSolicitacao(ScoCaminhoSolicitacaoID chavePrimaria) {
		return scoCaminhoSolicitacaoDAO.obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	@Secure("#{s:hasPermission('consultarSolicitacaoCompras', 'visualizar')}")
	public void inserirCaminhoSolicitacao(ScoCaminhoSolicitacao caminhoSolicitacao) throws ApplicationBusinessException {
		scoCaminhoSolicitacaoON.inserirCaminhoSolicitacao(caminhoSolicitacao);
	}

	@Override
	@Secure("#{s:hasPermission('consultarSolicitacaoCompras', 'visualizar')}")
	public void excluirCaminhoSolicitacao(ScoCaminhoSolicitacaoID id) throws ApplicationBusinessException {
		scoCaminhoSolicitacaoON.excluirCaminhoSolicitacao(id);
	}

	@Override
	public List<SolCompraVO> listarSolicitacoesDeCompras(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada,
			ScoMaterial material, Date dtSolInicial, Date dtSolFinal, DominioSimNao pendente, ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoPontoParadaSolicitacao pontoParadaAtual) throws ApplicationBusinessException {
		return scoSolicitacoesDeComprasDAO.listarSolicitacoesDeCompras(firstResult, maxResult, order, asc, listaCentroCustos,
				centroCusto, centroCustoAplicada, material, dtSolInicial, dtSolFinal, pendente,
				solicitacaoCompraRN.getPpsSolicitante(), solicitacaoCompraRN.getPpsAutorizacao(), solicitacaoDeCompra,
				pontoParadaAtual, servidorLogadoFacade.obterServidorLogado());
	}

	@Override
	public Long listarSolicitacoesDeComprasCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto,
			FccCentroCustos centroCustoAplicada, ScoMaterial material, Date dtSolInicial, Date dtSolFinal, DominioSimNao pendente,
			ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParadaAtual) throws ApplicationBusinessException {
		return scoSolicitacoesDeComprasDAO.listarSolicitacoesDeComprasCount(listaCentroCustos, centroCusto, centroCustoAplicada,
				material, dtSolInicial, dtSolFinal, pendente, solicitacaoCompraRN.getPpsSolicitante(),
				solicitacaoCompraRN.getPpsAutorizacao(), solicitacaoDeCompra, pontoParadaAtual,
				servidorLogadoFacade.obterServidorLogado());
	}

	@Override
	public List<SolCompraVO> listarSolicitacoesDeComprasSemParametros(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos) throws ApplicationBusinessException {
		return scoSolicitacoesDeComprasDAO.listarSolicitacoesDeComprasSemParametros(firstResult, maxResult, order, asc,
				listaCentroCustos, solicitacaoCompraRN.getPpsSolicitante(), solicitacaoCompraRN.getPpsAutorizacao(),
				servidorLogadoFacade.obterServidorLogado());
	}

	@Override
	public Long listarSolicitacoesDeComprasCountSemParametros(List<FccCentroCustos> listaCentroCustos) throws ApplicationBusinessException {

		return scoSolicitacoesDeComprasDAO.listarSolicitacoesDeComprasCountSemParametros(listaCentroCustos,
				solicitacaoCompraRN.getPpsSolicitante(), solicitacaoCompraRN.getPpsAutorizacao(),
				servidorLogadoFacade.obterServidorLogado());
	}

	@Override
	public Boolean verificarAcoesPontoParadaSc(Integer slcNumero, Date data) {
		return this.scoAcoesPontoParadaDAO.verificarAcoesPontoParadaSc(slcNumero, data);
	}

	@Override
	public ScoPontoParadaSolicitacao getPpsAutorizacao() {
		return solicitacaoCompraRN.getPpsAutorizacao();
	}

	@Override
	public Boolean verificarPermissoesSolicitacaoCompras(String login, Boolean gravar) {
		return solicitacaoCompraRN.verificarPermissoesSolicitacaoCompras(login, gravar);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarSolicitacaoCompras', 'gravar') or s:hasPermission('cadastrarSCPlanejamento', 'gravar') or s:hasPermission('cadastrarSCComprador', 'gravar') or s:hasPermission('cadastrarSCAreasEspecificas', 'gravar') or s:hasPermission('cadastrarSCChefias', 'gravar')}")
	public void inativarListaSolicitacaoCompras(List<Integer> listaSolicitacoes, String motivoInativacao) throws BaseException {
		solicitacaoCompraRN.inativarListaSolicitacaoCompras(listaSolicitacoes, motivoInativacao);
	}

	@Override
	public void atualizarPlanejamentoSco(List<ScoPlanejamentoVO> listaAlteracoes, List<Integer> nroLibRefs, List<Integer> nroLibAss)
			throws BaseException {
		planejamentoScON.atualizarPlanejamentoSco(listaAlteracoes, nroLibRefs, nroLibAss);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> pesquisarLoteSolicitacaoCompras(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa) {
		return pesquisasSolicitacaoCompraON.pesquisarLoteSolicitacaoCompras(firstResult, maxResults, orderProperty, asc,
				filtroPesquisa);
	}

	@Override
	public Long countLoteSolicitacaoCompras(PesqLoteSolCompVO filtroPesquisa) {
		return solicitacaoCompraON.countLoteSolicitacaoCompras(filtroPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('encaminharSolicitacaoCompras', 'gravar')}")
	public void encaminharListaSolicitacaoCompras(List<Integer> listaSolicitacoes, ScoPontoParadaSolicitacao pontoParadaAtual,
			ScoPontoParadaSolicitacao proximoPontoParada, RapServidores funcionarioComprador, Boolean autorizacaoChefia)
			throws BaseException {
		solicitacaoCompraRN.encaminharListaSolicitacaoCompras(listaSolicitacoes, pontoParadaAtual, proximoPontoParada,
				funcionarioComprador, autorizacaoChefia);
	}

	@Override
	public List<Integer> obterListaNumeroSc(List<ScoSolicitacaoDeCompra> listaSc) {
		return solicitacaoCompraON.obterListaNumeroSc(listaSc);
	}
	
	@Override
	@Secure("#{s:hasPermission('cadastrarSolicitacaoCompras', 'gravar') or s:hasPermission('cadastrarSCPlanejamento', 'gravar') or s:hasPermission('cadastrarSCComprador', 'gravar') or s:hasPermission('cadastrarSCAreasEspecificas', 'gravar') or s:hasPermission('cadastrarSCChefias', 'gravar')}")
	public void autorizarListaSolicitacaoCompras(List<Integer> listaSolicitacoes) throws BaseException {
		solicitacaoCompraRN.autorizarListaSolicitacaoCompras(listaSolicitacoes);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasAutorizarSc(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, PesqLoteSolCompVO filtroPesquisa) {
		return pesquisasSolicitacaoCompraON.pesquisarSolicitacaoComprasAutorizarSc(firstResult, maxResults, orderProperty, asc,
				filtroPesquisa);
	}

	@Override
	public List<ScoLogGeracaoScMatEstocavel> pesquisarLogGeracaoScMaterialEstocavel(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, ProcessoGeracaoAutomaticaVO processo, ScoMaterial material, DominioSimNao indContrato) {
		return scoLogGeracaoScMatEstocavelDAO.pesquisarLogGeracaoScMaterialEstocavel(firstResult, maxResults, orderProperty, asc,
				processo, material, indContrato);
	}

	@Override
	public Long contarLogGeracaoScMaterialEstocavel(ProcessoGeracaoAutomaticaVO processo, ScoMaterial material, DominioSimNao indContrato) {
		return scoLogGeracaoScMatEstocavelDAO.contarLogGeracaoScMaterialEstocavel(processo, material, indContrato);
	}

	@Override
	public Boolean verificarPontoParadaChefia(List<ScoSolicitacaoDeCompra> solicitacoes){
		return pesquisasSolicitacaoCompraON.verificarPontoParadaChefia(solicitacoes);
	}

	@Override
	public List<ProcessoGeracaoAutomaticaVO> pesquisarProcessoGeracaoCodigoData(Object param) {
		return scoLogGeracaoScMatEstocavelDAO.pesquisarProcessoGeracaoCodigoData(param);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasLiberacaoSc(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, PesqLoteSolCompVO filtroPesquisa) {
		return pesquisasSolicitacaoCompraON.pesquisarSolicitacaoComprasLiberacaoSc(firstResult, maxResults, orderProperty, asc,
				filtroPesquisa);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasPlanejamentoSc(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, PesqLoteSolCompVO filtroPesquisa, Boolean pesquisarScMaterialEstocavel) {
		return pesquisasSolicitacaoCompraON.pesquisarSolicitacaoComprasPlanejamentoSc(firstResult, maxResults, orderProperty,
				asc, filtroPesquisa, pesquisarScMaterialEstocavel);
	}

	@Override
	public Integer obterIndiceListaControle(ScoSolicitacaoDeCompra item, List<PlanejamentoCompraVO> listaControle) {
		return planejamentoScON.obterIndiceListaControle(item, listaControle);
	}

	@Override
	public Integer obterIndiceLista(ScoSolicitacaoDeCompra item, List<ScoPlanejamentoVO> listAlteracoes) {
		return planejamentoScON.obterIndiceLista(item, listAlteracoes);
	}

	@Override
	public PlanejamentoCompraVO preencherControleVO(ScoSolicitacaoDeCompra scoItem) {
		return planejamentoScON.preencherControleVO(scoItem);
	}

	@Override
	public Integer obterQtdSaldoParcelas(ScoSolicitacaoDeCompra scoItem, List<PlanejamentoCompraVO> listaControle) {
		return planejamentoScON.obterQtdSaldoParcelas(scoItem, listaControle);
	}

	@Override
	public String obterDescricaoPontoParada(ScoSolicitacaoDeCompra item, Boolean proximo) throws ApplicationBusinessException {
		return planejamentoScON.obterDescricaoPontoParada(item, proximo);
	}

	@Override
	public ScoPlanejamentoVO montarItemObjetoVO(ScoSolicitacaoDeCompra item) {
		return planejamentoScON.montarItemObjetoVO(item);
	}

	@Override
	public Boolean verificarHabilitacaoCamposAf(List<PlanejamentoCompraVO> listaControle, ScoSolicitacaoDeCompra item,
			Boolean verificarParcela, Boolean verificarLibRef, Boolean verificarAf, Boolean verificarLibAss, Boolean protegerQtde) {
		return planejamentoScON.verificarHabilitacaoCamposAf(listaControle, item, verificarParcela, verificarLibRef, verificarAf,
				verificarLibAss, protegerQtde);
	}

	@Override
	public Long countSolicitacaoComprasAutorizarSc(PesqLoteSolCompVO filtroPesquisa) {
		return pesquisasSolicitacaoCompraON.countSolicitacaoComprasAutorizarSc(filtroPesquisa);
	}

	@Override
	public Long countSolicitacaoComprasLiberacaoSc(PesqLoteSolCompVO filtroPesquisa) {
		return pesquisasSolicitacaoCompraON.countSolicitacaoComprasLiberacaoSc(filtroPesquisa);
	}

	@Override
	public Long countSolicitacaoComprasPlanejamentoSc(PesqLoteSolCompVO filtroPesquisa, Boolean pesquisarScMaterialEstocavel) {
		return pesquisasSolicitacaoCompraON.countSolicitacaoComprasPlanejamentoSc(filtroPesquisa, pesquisarScMaterialEstocavel);
	}

	@Override
	public List<ScoMaterial> listarMateriaisAtivos(String param, String login) {
		return solicitacaoCompraON.listarMateriaisAtivos(param, login);
	}

	@Override
	public Long listarMateriaisAtivosCount(String param, String servidor) {
		return solicitacaoCompraON.listarMateriaisAtivosCount(param, servidor);
	}

	@Override
	public List<ScoMaterial> listarMateriaisSC(String param) {
		return solicitacaoCompraON.listarMaterialSC(param, null);
	}
	
	@Override
	public List<ScoMaterial> listarMateriaisSC(Object param, Integer gmtCodigo, Short almCodigo) {
		return solicitacaoCompraON.listarMaterialSC(param, gmtCodigo, almCodigo);
	}


	@Override
	public Integer listarMateriaisSCCount(String param, Integer gmtCodigo) {
		return solicitacaoCompraON.listarMaterialSCCount(param, gmtCodigo);
	}


	@Override
	public void persistirSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoCompra, ScoSolicitacaoDeCompra solicitacaoCompraClone)
			throws BaseException {
		solicitacaoCompraON.persistirSolicitacaoDeCompra(solicitacaoCompra, solicitacaoCompraClone);
	}

	@Override
	public void inserirScoSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws BaseException {
		solicitacaoCompraRN.inserirSolicitacaoCompra(solicitacaoDeCompra, true);
	}

	@Override
	public void atualizarScoSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws BaseException {
		solicitacaoCompraRN.atualizarSolicitacaoCompra(solicitacaoDeCompra, solicitacaoDeCompraOld);

	}

	@Override
	public ScoSolicitacaoDeCompra obterSolicitacaoDeCompra(Integer numero) {
		return solicitacaoCompraON.obterSolicitacaoDeCompra(numero);
	}

	@Override
	public ScoSolicitacaoDeCompra obterSolicitacaoDeCompraOriginal(Integer numero) {
		return scoSolicitacoesDeComprasDAO.obterOriginal(numero);

	}

	@Override
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasPorItemAf(ScoItemAutorizacaoFornId itemAfId) {
		return this.scoSolicitacoesDeComprasDAO.pesquisarSolicitacaoComprasPorItemAf(itemAfId);
	}

	@Override
	public ScoSolicitacaoDeCompra clonarSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoCompra) throws ApplicationBusinessException {
		return solicitacaoCompraON.clonarSolicitacaoDeCompra(solicitacaoCompra);

	}

	@Override
	public void duplicarSC(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean mantemCcOriginal) throws BaseException{
		solicitacaoCompraON.duplicarSC(solicitacaoDeCompra, servidorLogadoFacade.obterServidorLogado(), mantemCcOriginal);
	}
	@Override
	public ScoSolicitacaoDeCompra duplicarSCPorPAC(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		return solicitacaoCompraRN.duplicarSCPorPAC(solicitacaoDeCompra);

	}

	@Override
	public boolean isReadonlyEdicao(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoGeral) {
		return solicitacaoCompraRN.isReadonlyEdicao(solicitacaoDeCompra, temPermissaoComprador, temPermissaoPlanejamento,
				temPermissaoGeral);
	}

	@Override
	public boolean isReadonlyDevolucao(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoGeral) {
		return solicitacaoCompraRN.isReadonlyDevolucao(solicitacaoDeCompra, temPermissaoComprador, temPermissaoPlanejamento,
				temPermissaoGeral);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> buscarSolicitacaoCompraAssociadaAutorizacaoFornEfetivada(FsoNaturezaDespesaId id) {
		return this.scoSolicitacoesDeComprasDAO.buscarSolicitacaoCompraAssociadaAutorizacaoFornEfetivada(id);
	}

	/**
	 * Verifica se usuário logado tem permissao cadastrarSCComprador
	 */
	@Override
	public Boolean isPerfilComprador(ScoSolicitacaoDeCompra solicitacaoDeCompra, String login) {
		return solicitacaoCompraRN.isPerfilComprador(solicitacaoDeCompra, login);
	}

	/**
	 * Verifica se usuário logado tem permissao cadastrarSCPlanejamento
	 */
	@Override
	public Boolean isPerfilPlanejamento(ScoSolicitacaoDeCompra solicitacaoDeCompra, String login) {
		return solicitacaoCompraRN.isPerfilPlanejamento(solicitacaoDeCompra, login);
	}

	/**
	 * Verifica se usuário logado tem permissao cadastrarSCGeral
	 */
	@Override
	public Boolean isPerfilGeral(ScoSolicitacaoDeCompra solicitacaoDeCompra, String login) {
		return solicitacaoCompraRN.isPerfilGeral(solicitacaoDeCompra, login);
	}

	@Override
	public boolean isRequeridDescricaoCompra(ScoMaterial scoMaterial) throws ApplicationBusinessException {
		return solicitacaoCompraON.isRequeridDescricaoCompra(scoMaterial);
	}

	@Override
	public Double getUltimoValorCompra(ScoMaterial scoMaterial) throws ApplicationBusinessException {
		return solicitacaoCompraON.getUltimoValorCompra(scoMaterial);
	}

	/*************
	 * Valida Quantidade Solicitada e Aprovada que deve ser maior que zero
	 * chamado na inserção
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validaQtdeSolicitadaAprovada(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException {

		solicitacaoCompraON.validaQtdeSolicitadaAprovada(solicitacaoDeCompra);

	}

	/****
	 * RN 11 - Metodo Acionado na acao gravar caso a quantidade solicitada Não
	 * esteja em branca seta o valor dela para o da quantidade aprovada *
	 * 
	 * @param solicitacaoCompra
	 */
	@Override
	public void alteraQuantidadeAprovada(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		solicitacaoCompraON.alteraQuantidadeAprovada(solicitacaoDeCompra);
	}

	@Override
	public List<ScoArquivoAnexo> pesquisarArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento origemSolicitacao,
			Integer numeroSolicitacao) {
		return anexarDocumentosSolicitacaoCompraON.pesquisarArquivosPorNumeroOrigem(origemSolicitacao, numeroSolicitacao);
	}

	@Override
	public Long pesquisarArquivosPorNumeroOrigemCount(DominioOrigemSolicitacaoSuprimento origem, Integer numero) {
		return anexarDocumentosSolicitacaoCompraON.pesquisarArquivosPorNumeroOrigemCount(origem, numero);
	}

	@Override
	public Boolean verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento origem, Integer numero) {
		return anexarDocumentosSolicitacaoCompraON.verificarExistenciaArquivosPorNumeroOrigem(origem, numero);
	}

	@Override
	public void incluirArquivoAnexo(ScoArquivoAnexo arquivoAnexo) throws ApplicationBusinessException {
		anexarDocumentosSolicitacaoCompraON.incluirArquivoAnexo(arquivoAnexo);
	}

	@Override
	public void excluirArquivoAnexo(Long seqArquivo) throws ApplicationBusinessException {
		anexarDocumentosSolicitacaoCompraON.excluirArquivoAnexo(seqArquivo);
	}

	@Override
	public void alterarArquivoAnexo(ScoArquivoAnexo arquivoAnexo) throws ApplicationBusinessException {
		anexarDocumentosSolicitacaoCompraON.alterarArquivoAnexo(arquivoAnexo);
	}

	@Override
	public BigDecimal tamanhoMaximoPermitido() throws ApplicationBusinessException {
		return anexarDocumentosSolicitacaoCompraON.tamanhoMaximoPermitido();
	}

	// #5262 - Andamento da Solicitação de Compra
	@Override
	public List<ScoScJn> listarPesquisaFasesSolicitacaoCompra(Integer numero) {

		return fasesSolicitacaoCompraON.listaPesquisaFasesSolicitacaoCompra(numero);
	}

	@Override
	public Long countPesquisaFasesSolicitacaoCompra(Integer numero) {

		return fasesSolicitacaoCompraON.countPesquisaFasesSolicitacaoCompra(numero);
	}

	@Override
	public ScoScJn obterFaseSolicitacaoCompra(Integer numero, Short codigoPontoParada, Integer seq) {

		return fasesSolicitacaoCompraON.obterFaseSolicitacaoCompra(numero, codigoPontoParada, seq);
	}

	@Override
	public List<ScoFaseSolicitacao> listarDadosLicitacao(Integer numero) {

		return fasesSolicitacaoCompraON.listaDadosLicitacao(numero);
	}

	@Override
	public ScoFaseSolicitacao obterDadosAutorizacaoFornecimento(Integer numero) {

		return fasesSolicitacaoCompraON.obterDadosAutorizacaoFornecimento(numero);
	}

	@Override
	public List<ScoPropostaFornecedor> listarDataDigitacaoPropostaForn(Integer numero) {

		return fasesSolicitacaoCompraON.listaDataDigitacaoPropostaForn(numero);
	}

	@Override
	public ScoItemAutorizacaoForn obterDadosItensAutorizacaoFornecimento(Integer afNumero, Integer numero) {

		return fasesSolicitacaoCompraON.obterDadosItensAutorizacaoFornecimento(afNumero, numero);
	}

	@Override
	public ScoItemLicitacao obterDataDigitacaoPublicacaoLicitacao(Integer lctNumero, Short numero) {

		return fasesSolicitacaoCompraON.obterDataDigitacaoPublicacaoLicitacao(lctNumero, numero);
	}

	@Override
	public List<ScoAcoesPontoParada> listarAcoesPontoParada(Integer numero, Short codigoPontoParada, DominioTipoSolicitacao tipoSolicitacao) {

		return fasesSolicitacaoCompraON.listaAcoesPontoParada(numero, codigoPontoParada, tipoSolicitacao);
	}

	@Override
	public Boolean verificarFiltroPlanejamentoVazio(PesqLoteSolCompVO filtroPesquisa) {
		return pesquisasSolicitacaoCompraON.verificarFiltroPlanejamentoVazio(filtroPesquisa);
	}

	/*********************************
	 * RN23
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validaUrgentePrioritario(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException {

		solicitacaoCompraON.validaUrgentePrioritario(solicitacaoDeCompra);

	}

	@Override
	public List<String> gerarSolicitacaoCompraAlmox(Date dtAnalise) throws BaseException {
		return gerarSolicitacaoCompraAlmoxarifadoRN.gerarSolicitacaoCompraAlmox(dtAnalise);
	}

	@Override
	public List<String> gerarSolicitacaoCompraAlmox(Date dtAnalise, SceAlmoxarifado almox) throws BaseException {
		return gerarSolicitacaoCompraAlmoxarifadoRN.gerarSolicitacaoCompraAlmox(dtAnalise, almox);
	}

	@Override
	public List<String> gerarScRepAutomatica(Date dtAnalise, SceAlmoxarifado almox) throws BaseException {
		return gerarSolicitacaoCompraAlmoxarifadoRN.gerarScRepAutomatica(dtAnalise, almox);
	}

	@Override
	public void atualizarHorarioAgendamentoGeracaoAutomaticaSolCompras(Date horaAgendamento) throws BaseException {
		gerarSolicitacaoCompraAlmoxarifadoRN.atualizarHorarioAgendamentoGeracaoAutomaticaSolCompras(horaAgendamento);
	}

	@Override
	public Date carregarHorarioAgendamentoGeracaoAutomaticaSolCompras() throws ApplicationBusinessException, ParseException {
		return gerarSolicitacaoCompraAlmoxarifadoRN.carregarHorarioAgendamentoGeracaoAutomaticaSolCompras();
	}

	@Override
	public void atualizarUltimaExecucaoGeracaoAutomaticaSolCompras(String msg) throws ApplicationBusinessException {
		gerarSolicitacaoCompraAlmoxarifadoRN.atualizarUltimaExecucaoGeracaoAutomaticaSolCompras(msg);
	}

	@Override
	public Boolean verificarAutorizacaoFornecimentoVinculada(ScoSolicitacaoDeCompra solicitacao) {
		return scoSolicitacoesDeComprasDAO.verificarAutorizacaoFornecimentoVinculada(solicitacao);
	}

	@Override
	public List<RelatorioSolicitacaoCompraEstocavelVO> pesquisarSolicitacaoMaterialEstocavel(Date dtInicial, Date dtFinal,
			Integer numSolicitacao, Date dataCompetencia) throws ApplicationBusinessException {

		return pesquisasSolicitacaoCompraON.pesquisarSolicitacaoMaterialEstocavel(dtInicial, dtFinal, numSolicitacao, dataCompetencia);
	}

	@Override
	public List<SolCompraVO> listarSolicitacoesDeComprasComprador(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada,
			ScoMaterial material, Date dtSolInicial, Date dtSolFinal, ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra, Boolean isPerfilGeral)
			throws ApplicationBusinessException {
		return scoSolicitacoesDeComprasDAO.listarSolicitacoesDeComprasComprador(firstResult, maxResult, order, asc, listaCentroCustos,
				centroCusto, centroCustoAplicada, material, dtSolInicial, dtSolFinal, solicitacaoCompraRN.getPpsSolicitante(),
				solicitacaoCompraRN.getPpsAutorizacao(), solicitacaoDeCompra, pontoParadaAtual, servidorCompra, isPerfilGeral);
	}

	@Override
	public Long listarSolicitacoesDeComprasCompradorCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto,
			FccCentroCustos centroCustoAplicada, ScoMaterial material, Date dtSolInicial, Date dtSolFinal,
			ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra,
			Boolean isPerfilGeral) throws ApplicationBusinessException {
		return scoSolicitacoesDeComprasDAO.listarSolicitacoesDeComprasCompradorCount(listaCentroCustos, centroCusto,
				centroCustoAplicada, material, dtSolInicial, dtSolFinal, solicitacaoCompraRN.getPpsSolicitante(),
				solicitacaoCompraRN.getPpsAutorizacao(), solicitacaoDeCompra, pontoParadaAtual, servidorCompra, isPerfilGeral);
	}

	@Override
	public boolean verificaPemissaoUsuario(String permissao, String login) throws ApplicationBusinessException {
		return solicitacaoCompraON.verificaPemissaoUsuario(permissao, login);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoCompraPorNumeroOuDescricao(Object pesquisa, Boolean filtrarAssociadas) {
		return scoSolicitacoesDeComprasDAO.pesquisarSolicitacaoCompraPorNumeroOuDescricao(pesquisa, filtrarAssociadas);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> obterScoSolicitacoesDeCompras(Object param) {
		return scoSolicitacoesDeComprasDAO.listarSolicitacoesDeComprasPorNumeroVinculado(param);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> buscarSolicitacaoComprasAssociadaVerbaGestao(FsoVerbaGestao verbaGestao, Boolean filtraEfetivada) {
		return this.scoSolicitacoesDeComprasDAO.buscarSolicitacaoComprasAssociadaVerbaGestao(verbaGestao, filtraEfetivada);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioFinanceiro', 'visualizar')}")
	public FsoNaturezaDespesa obterNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNatureza, ScoMaterial material) {
		return scoMaterialDAO.obterNaturezaDespesa(grupoNatureza, material);
	}

	@Override
	public void validarRegrasOrcamentarias(ScoSolicitacaoDeCompra sc) throws BaseException {
		solicitacaoCompraRN.validarRegrasOrcamentarias(sc, null);
	}

	/*************
	 * Verifica se a Solicitação de compra está habilitada para ser autorizada
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 *
	 */
	public Boolean habilitarAutorizarSC(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException {
		return solicitacaoCompraON.habilitarAutorizarSC(solicitacaoDeCompra);
	}

	/*************
	 * Verifica se a Solicitação de compra está habilitada para ser encaminhada
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 *
	 */
	@Override
	public Boolean habilitarEncaminharSC(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoEncaminhar, List<FccCentroCustos> listaCentroCustosUsuario) {
		return solicitacaoCompraON.habilitarEncaminharSC(solicitacaoDeCompra, temPermissaoComprador, temPermissaoPlanejamento,
				temPermissaoEncaminhar, listaCentroCustosUsuario);
	}

	@Override
	public void verificarMaterialSelecionado(ScoMaterial material, Boolean temPermissaoCadastrar, Boolean temPermissaoChefia,
			Boolean temPermissaoAreasEspecificas, Boolean temPermissaoGeral, Boolean temPermissaoPlanejamento)
			throws ApplicationBusinessException {
		solicitacaoCompraON.verificarMaterialSelecionado(material, temPermissaoCadastrar, temPermissaoChefia,
				temPermissaoAreasEspecificas, temPermissaoGeral, temPermissaoPlanejamento);
	}

	@Override
	public Boolean bloqueiaSolicitacaoComprasMatEstocavel(ScoMaterial material,
			Boolean temPermissaoCadastrar, Boolean temPermissaoChefia, Boolean temPermissaoAreasEspecificas,
			Boolean temPermissaoGeral, Boolean temPermissaoPlanejamento) {
		return solicitacaoCompraON.bloqueiaSolicitacaoComprasMatEstocavel(material, temPermissaoCadastrar, temPermissaoChefia, temPermissaoAreasEspecificas, temPermissaoGeral, temPermissaoPlanejamento);
	}
	
	@Override
	public void devolverSolicitacoesCompras(List<Integer> nroScos, String justificativa) throws BaseException {
		solicitacaoCompraON.devolverSolicitacoesCompras(nroScos, justificativa);
	}

	public void inserirJournalSC(ScoSolicitacaoDeCompra solicitacaoDeCompra, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		solicitacaoCompraRN.inserirSolicitacaoCompraJournal(solicitacaoDeCompra, operacao);
	}

	/**
	 * Obtem data da ultima execucao do processo
	 * 
	 * @return Date
	 */
	@Override
	public Date obterDataUltimaExecucao() {
		return scoLogGeracaoScMatEstocavelDAO.obterDataUltimaExecucao();
	}

	@Override
	public ProcessoGeracaoAutomaticaVO obterUltimoProcessoGeracao() {
		return scoLogGeracaoScMatEstocavelDAO.obterUltimoProcessoGeracao();
	}

	@Override
	public Integer obterSolicitacaoDeCompra(ScoLogGeracaoScMatEstocavel item) {
		return scoSolicitacoesDeComprasDAO.obterSolicitacaoDeCompra(item);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> pesquisarScsNaoAf(ScoMaterial material) {
		return scoSolicitacoesDeComprasDAO.pesquisarScsNaoAf(material);
	}

	@Override
	public boolean isRequiredCcProjeto(FccCentroCustos centroCusto) throws ApplicationBusinessException {
		return solicitacaoCompraON.isRequiredCcProjeto(centroCusto);
	}

	@Override
	public void desatacharSolicitacaoCompras(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		scoSolicitacoesDeComprasDAO.desatachar(solicitacaoDeCompra);
	}

	@Override
	public Long pesquisarMaterialReposicaoCount(FiltroReposicaoMaterialVO filtro) {
		return scoMaterialDAO.pesquisarMaterialReposicaoCount(filtro);
	}

	@Override
	public void gerarParcelasPorScsMateriaisDiretos(List<Integer> listaSolicitacoes) throws ApplicationBusinessException {
		solicitacaoCompraRN.gerarParcelasPorScsMateriaisDiretos(listaSolicitacoes);
	}

	@Override
	public ScoParamProgEntgAf obterScoParamProgEntgAfPorSolicitacaoDeCompra(Integer numero) {
		return scoParamProgEntgAfRN.obterScoParamProgEntgAfPorSolicitacaoDeCompra(numero);
	}

	@Override
	public void excluirScoParamProgEntgAf(ScoParamProgEntgAf programacaoEntrega) {
		scoParamProgEntgAfRN.excluirScoParamProgEntgAf(programacaoEntrega);
	}

	@Override
	public void inserirSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws BaseException {
		solicitacaoCompraRN.inserirSolicitacaoCompra(solicitacaoDeCompra, false);
	}
	
	@Override
	public void persistirScoParamProgEntgAf(ScoParamProgEntgAf programacaoEntrega, Boolean inserir) throws ApplicationBusinessException {
		scoParamProgEntgAfRN.persistirScoParamProgEntgAf(programacaoEntrega, inserir);
	}
	
	@Override	
	public Boolean verificarSolicitacaoDevolvidaAutorizacao(Integer numero) {
		return scoScJnDAO.verificarSolicitacaoDevolvidaAutorizacao(numero);
	}

	@Override
	public Boolean verificarComprasWeb(AghParametros param, ScoMaterial material) {
		return solicitacaoCompraON.verificarComprasWeb(param, material);
	}	

}