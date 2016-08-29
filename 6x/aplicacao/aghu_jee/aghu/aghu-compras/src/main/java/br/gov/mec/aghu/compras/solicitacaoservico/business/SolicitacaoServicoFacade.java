package br.gov.mec.aghu.compras.solicitacaoservico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoAcoesPontoParadaDAO;
import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoCompraServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSsJnDAO;
import br.gov.mec.aghu.compras.vo.LoteSolicitacaoServicoVO;
import br.gov.mec.aghu.compras.vo.SolServicoVO;
import br.gov.mec.aghu.compras.vo.SolicitacaoServicoVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoSsJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Porta de entrada do módulo Solicitação de Serviços.
 * 
 */
@Modulo(ModuloEnum.COMPRAS)
@Stateless
public class SolicitacaoServicoFacade extends BaseFacade implements ISolicitacaoServicoFacade {

	private static final long serialVersionUID = 8908737372048060889L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ScoSolicitacaoServicoON scoSolicitacaoServicoON;

	@EJB
	private FasesSolicitacaoServicoON fasesSolicitacaoServicoON;

	@EJB
	private ScoSolicitacaoServicoRN scoSolicitacaoServicoRN;

	@EJB
	private ScoSolicitacaoCompraServicoON scoSolicitacaoCompraServicoON;

	@Inject
	private ScoSolicitacaoCompraServicoDAO scoSolicitacaoCompraServicoDAO;

	@Inject
	private ScoAcoesPontoParadaDAO scoAcoesPontoParadaDAO;

	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;

	@Inject
	private ScoServicoDAO scoServicoDAO;
	
	@Inject
	private ScoSsJnDAO scoSsJnDAO;

	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoPorNumeroOuDescricao(String filtro) {
		return getScoSolicitacaoServicoDAO().pesquisarSolicitacaoServicoPorNumeroOuDescricao(filtro);
	}

	public List<ScoSolicitacaoCompraServico> pesquisarSolServicosPorSolCompra(ScoSolicitacaoDeCompra solCompra) {
		return getScoSolicitacaoServicoDAO().pesquisarSolServicosPorSolCompra(solCompra);
	}

	private ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}

	private ScoServicoDAO getScoServicoDAO() {
		return scoServicoDAO;
	}

	public void mensagemErroDuplicadoScoSolServicoCompras() throws ApplicationBusinessException {

		getScoSolicitacaoCompraServicoON().mensagemErroDuplicadoScoSolServicoCompras();

	}

	public void mensagemErroDuplicadoScoSolServicoComprasSolCompras() throws ApplicationBusinessException {
		getScoSolicitacaoCompraServicoON().mensagemErroDuplicadoScoSolServicoComprasSolCompras();
	}

	@Override
	public Long countSolicitacaoServicoAutorizarSs(LoteSolicitacaoServicoVO filtroPesquisa) {
		return this.getScoSolicitacaoServicoON().countSolicitacaoServicoAutorizarSs(filtroPesquisa);
	}

	@Override
	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoAutorizarSs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, LoteSolicitacaoServicoVO filtroPesquisa) {
		return this.getScoSolicitacaoServicoON().pesquisarSolicitacaoServicoAutorizarSs(firstResult, maxResults, orderProperty, asc,
				filtroPesquisa);
	}

	@Override
	public void resgatarSs(Integer slsNumero) throws BaseException {
		this.getFasesSolicitacaoServicoON().resgatarSs(slsNumero);
	}

	@Override
	public Long countSolicitacaoServicoEncaminharSs(LoteSolicitacaoServicoVO filtroPesquisa) {
		return this.getScoSolicitacaoServicoON().countSolicitacaoServicoEncaminharSs(filtroPesquisa);
	}

	@Override
	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoEncaminharSs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, LoteSolicitacaoServicoVO filtroPesquisa) {
		return this.getScoSolicitacaoServicoON().pesquisarSolicitacaoServicoEncaminharSs(firstResult, maxResults, orderProperty, asc,
				filtroPesquisa);
	}

	protected ScoSolicitacaoCompraServicoON getScoSolicitacaoCompraServicoON() {
		return scoSolicitacaoCompraServicoON;
	}

	@Secure("#{s:hasPermission('cadastrarSolicitacaoCompras','gravar')}")
	public void inserirScoSolCompraServico(ScoSolicitacaoCompraServico solCompraServico) throws ApplicationBusinessException {
		getScoSolicitacaoCompraServicoON().inserirSolicitacaoCompraServico(solCompraServico);
	}

	@Secure("#{s:hasPermission('cadastrarSolicitacaoCompras','gravar')}")
	public void excluirScoSolCompraServico(ScoSolicitacaoCompraServico solCompraServico) throws ApplicationBusinessException {
		getScoSolicitacaoCompraServicoON().excluirSolicitacaoCompraServico(solCompraServico);

	}

	@Secure("#{s:hasPermission('cadastrarSolicitacaoCompras','gravar')}")
	public void excluirScoSolCompraServico(ScoSolicitacaoDeCompra solCompra) throws ApplicationBusinessException {
		getScoSolicitacaoServicoDAO().excluirScoSolCompraServico(solCompra);

	}

	@Override
	public Boolean verificarPermissoesSolicitacaoServico(String login, Boolean gravar) {
		return this.getScoSolicitacaoServicoON().verificarPermissoesSolicitacaoServico(login, gravar);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarSolicitacaoServico', 'gravar') or s:hasPermission('cadastrarSSPlanejamento', 'gravar') or s:hasPermission('cadastrarSSComprador', 'gravar') or s:hasPermission('cadastrarSSEngenharia', 'gravar') or s:hasPermission('cadastrarSSChefias', 'gravar')}")
	public void inativarListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacoes, String motivoInativacao) throws BaseException {
		this.getScoSolicitacaoServicoON().inativarListaSolicitacaoServico(listaSolicitacoes, motivoInativacao);
	}

	@Override
	@Secure("#{s:hasPermission('encaminharSolicitacaoServico', 'gravar')}")
	public void encaminharListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacaoServico, ScoPontoParadaSolicitacao pontoParadaAtual,
			ScoPontoParadaSolicitacao proximoPontoParada, RapServidores funcionarioComprador, Boolean autorizacaoChefia)
			throws BaseException {
		this.getScoSolicitacaoServicoON().encaminharListaSolicitacaoServico(listaSolicitacaoServico, pontoParadaAtual, proximoPontoParada,
				funcionarioComprador, autorizacaoChefia);
	}

	@Override
	public List<ScoSolicitacaoServico> pesquisarSolicServicoCodigoDescricao(Object filter, ScoServico servico,
			FsoNaturezaDespesa naturezaDespesa) {
		return getScoSolicitacaoServicoDAO().pesquisarSolicServicoCodigoDescricao(filter, servico, naturezaDespesa);
	}

	@Override
	public ScoSolicitacaoServico obterSolicitacaoServico(Integer numero) {

		return scoSolicitacaoServicoDAO.obterScoSolicitacaoServicoPeloId(numero);
	}

	@Override
	public ScoSolicitacaoServico obterSolicitacaoServicoOriginal(Integer numero) {
		return getScoSolicitacaoServicoDAO().obterOriginal(numero);
	}

	@Override
	public List<ScoSolicitacaoCompraServico> pesquisarSolicitacaoDeCompraPorServico(ScoSolicitacaoServico solicitacaoServico) {
		return getScoSolicitacaoCompraServicoON().pesquisarSolicitacaoDeCompraPorServico(solicitacaoServico);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarSolicitacaoServico', 'gravar') or s:hasPermission('cadastrarSSPlanejamento', 'gravar') or s:hasPermission('cadastrarSSComprador', 'gravar') or s:hasPermission('cadastrarSSEngenharia', 'gravar') or s:hasPermission('cadastrarSSChefias', 'gravar')}")
	public void autorizarListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacoes) throws BaseException {
		this.getScoSolicitacaoServicoON().autorizarListaSolicitacaoServico(listaSolicitacoes);
	}

	@Override
	public List<SolServicoVO> listarSolicitacoesDeServicos(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada, ScoServico servico,
			Date dtSolInicial, Date dtSolFinal, DominioSimNao pendente, ScoSolicitacaoServico solicitacaoServico,
			ScoPontoParadaSolicitacao pontoParada) throws ApplicationBusinessException {
		return getScoSolicitacaoServicoDAO().listarSolicitacoesDeServicos(firstResult, maxResult, order, asc, listaCentroCustos,
				centroCusto, centroCustoAplicada, servico, dtSolInicial, dtSolFinal, pendente,
				this.getScoSolicitacaoServicoRN().getPpsSolicitante(), this.getScoSolicitacaoServicoRN().getPpsAutorizacao(),
				solicitacaoServico, this.getServidorLogadoFacade().obterServidorLogado(), pontoParada);
	}

	@Override
	public List<Integer> obterListaNumeroSs(List<ScoSolicitacaoServico> listaSs) { 
		return getScoSolicitacaoServicoON().obterListaNumeroSs(listaSs);
	}
	
	@Override
	public List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaNaturezaDespesa(FsoNaturezaDespesaId id, Boolean isExclusao) {
		return this.getScoSolicitacaoServicoDAO().buscarSolicitacaoServicoAssociadaNaturezaDespesa(id, isExclusao);
	}

	@Override
	public List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaVerbaGestao(FsoVerbaGestao verbaGestao, Boolean filtraEfetivada) {
		return this.getScoSolicitacaoServicoDAO().buscarSolicitacaoServicoAssociadaVerbaGestao(verbaGestao, filtraEfetivada);
	}

	@Override
	public Long listarSolicitacoesDeServicosCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto,
			FccCentroCustos centroCustoAplicada, ScoServico servico, Date dtSolInicial, Date dtSolFinal, DominioSimNao pendente,
			ScoSolicitacaoServico solicitacaoServico, ScoPontoParadaSolicitacao pontoParada) throws ApplicationBusinessException {
		return getScoSolicitacaoServicoDAO().listarSolicitacoesDeServicosCount(listaCentroCustos, centroCusto, centroCustoAplicada,
				servico, dtSolInicial, dtSolFinal, pendente, this.getScoSolicitacaoServicoRN().getPpsSolicitante(),
				this.getScoSolicitacaoServicoRN().getPpsAutorizacao(), solicitacaoServico,
				this.getServidorLogadoFacade().obterServidorLogado(), pontoParada);
	}

	@Override
	public List<SolServicoVO> listarSolicitacoesDeServicosSemParametros(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos) throws ApplicationBusinessException {
		return getScoSolicitacaoServicoDAO().listarSolicitacoesDeServicosSemParametros(firstResult, maxResult, order, asc,
				listaCentroCustos, this.getScoSolicitacaoServicoRN().getPpsSolicitante(),
				this.getScoSolicitacaoServicoRN().getPpsAutorizacao(), this.getScoSolicitacaoServicoRN().getPGrpoServEng(),
				this.getServidorLogadoFacade().obterServidorLogado());
	}

	@Override
	public Long listarSolicitacoesDeServicosCountSemParametros(List<FccCentroCustos> listaCentroCustos) throws ApplicationBusinessException {

		return getScoSolicitacaoServicoDAO().listarSolicitacoesDeServicosCountSemParametros(listaCentroCustos,
				this.getScoSolicitacaoServicoRN().getPpsSolicitante(), this.getScoSolicitacaoServicoRN().getPpsAutorizacao(),
				this.getScoSolicitacaoServicoRN().getPGrpoServEng(), this.getServidorLogadoFacade().obterServidorLogado());
	}

	@Override
	public List<ScoServico> listarServicosAtivos(Object param) {

		return this.getScoSolicitacaoServicoON().listarServicosAtivos(param);

	}

	@Override
	public void atualizarSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoServicoClone)
			throws BaseException {
		this.getScoSolicitacaoServicoRN().atualizarSolicitacaoServico(solicitacaoServico, solicitacaoServicoClone);
		//this.atualizarSolicitacaoServico(solicitacaoServico, solicitacaoServicoClone);
	}

	@Override
	public Long listarServicosAtivosCount(Object param) {
		return this.getScoSolicitacaoServicoON().listarServicosCount(param);
	}

	public List<ScoServico> listarServicos(Object param) {

		return this.getScoSolicitacaoServicoON().listarServicos(param);

	}

	public List<ScoServico> listarServicosEngenharia(Object param, String indEngenharia) {

		return this.getScoServicoDAO().listarServicosEngenharia(param, indEngenharia);

	}

	public List<ScoServico> listarServicosAtivosEngenharia(Object param, String indEngenharia) {

		return this.getScoServicoDAO().listarServicosEngenhariaAtivos(param, indEngenharia);

	}

	public Long listarServicosEngenhariaCount(Object param, String indEngenharia) {

		return this.getScoServicoDAO().listarServicosEngenhariaCount(param, indEngenharia);

	}

	public Long listarServicosAtivosEngenhariaCount(Object param, String indEngenharia) {

		return this.getScoServicoDAO().listarServicosEngenhariaAtivosCount(param, indEngenharia);

	}

	public void persistirSolicitacaoDeServico(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoServicoClone)
			throws BaseException {
		getScoSolicitacaoServicoON().persistirSolicitacaoDeServico(solicitacaoServico, solicitacaoServicoClone);
	}

	@Override
	public ScoSolicitacaoServico clonarSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {
		return getScoSolicitacaoServicoON().clonarSolicitacaoServico(solicitacaoServico);

	}

	@Override
	public List<ScoSsJn> listarPesquisaFasesSolicitacaoServico(Integer numero) {

		return this.getFasesSolicitacaoServicoON().listarPesquisaFasesSolicitacaoServico(numero);
	}

	@Override
	public Long countPesquisaFasesSolicitacaoServico(Integer numero) {

		return this.getFasesSolicitacaoServicoON().countPesquisaFasesSolicitacaoServico(numero);
	}

	@Override
	public List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaAutorizacaoFornEfetivada(FsoNaturezaDespesaId id) {
		return this.getScoSolicitacaoServicoDAO().buscarSolicitacaoServicoAssociadaAutorizacaoFornEfetivada(id);
	}

	@Override
	public ScoSsJn obterFaseSolicitacaoServico(Integer numero, Short codigoPontoParada, Integer seq) {

		return this.getFasesSolicitacaoServicoON().obterFaseSolicitacaoServico(numero, codigoPontoParada, seq);
	}

	@Override
	public List<ScoFaseSolicitacao> listarDadosLicitacaoSs(Integer numero) {
		return this.getFasesSolicitacaoServicoON().listarDadosLicitacaoSs(numero);
	}

	@Override
	public ScoFaseSolicitacao obterDadosAutorizacaoFornecimentoSs(Integer numero) {
		return this.getFasesSolicitacaoServicoON().obterDadosAutorizacaoFornecimentoSs(numero);
	}

	/*********************************
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validaUrgentePrioritario(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {

		getScoSolicitacaoServicoON().validaUrgentePrioritario(solicitacaoServico);

	}

	@Override
	public Boolean verificarAcoesPontoParadaSs(Integer slsNumero, Date data) {
		return this.getScoAcoesPontoParadaDAO().verificarAcoesPontoParadaSs(slsNumero, data);
	}

	@Override
	public List<SolicitacaoServicoVO> obterRelatorioSolicitacaoServico(List<Integer> listaCodSS) throws ApplicationBusinessException {
		return getScoSolicitacaoServicoON().buscarSolicitacaoServico(listaCodSS);
	}

	@Override
	public boolean isReadonlyEdicao(ScoSolicitacaoServico solicitacaoServico) {
		return getScoSolicitacaoServicoRN().isReadonlyEdicao(solicitacaoServico);
	}

	public boolean verificaPemissaoUsuario(String permissao, String login) throws ApplicationBusinessException {
		return getScoSolicitacaoServicoON().verificaPemissaoUsuario(permissao, login);
	}

	public List<SolServicoVO> listarSolicitacoesDeServicoCompradoreEngenharia(Integer firstResult, Integer maxResult, String order,
			Boolean asc, List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada,
			ScoServico servico, Date dtSolInicial, Date dtSolFinal, ScoSolicitacaoServico solicitacaoServico,
			ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra, ScoModalidadeLicitacao modalidadeLicitacao)
			throws ApplicationBusinessException {

		return scoSolicitacaoServicoDAO.listarSolicitacoesDeServicosCompradoreEngenharia(firstResult, maxResult, order, asc,
				listaCentroCustos, centroCusto, centroCustoAplicada, servico, dtSolInicial, dtSolFinal, this.getScoSolicitacaoServicoRN()
						.getPpsSolicitante(), this.getScoSolicitacaoServicoRN().getPpsAutorizacao(), solicitacaoServico, pontoParadaAtual,
				servidorCompra, modalidadeLicitacao);
	}

	public Long listarSolicitacoesDeServicoCompradoreEngenhariaCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto,
			FccCentroCustos centroCustoAplicada, ScoServico servico, Date dtSolInicial, Date dtSolFinal,
			ScoSolicitacaoServico solicitacaoServico, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra,
			ScoModalidadeLicitacao modalidadeLicitacao) throws ApplicationBusinessException {

		return getScoSolicitacaoServicoDAO().listarSolicitacoesDeServicosCompradoreEngenhariaCount(listaCentroCustos, centroCusto,
				centroCustoAplicada, servico, dtSolInicial, dtSolFinal, this.getScoSolicitacaoServicoRN().getPpsSolicitante(),
				this.getScoSolicitacaoServicoRN().getPpsAutorizacao(), solicitacaoServico, pontoParadaAtual, servidorCompra,
				modalidadeLicitacao);

	}

	protected ScoSolicitacaoServicoON getScoSolicitacaoServicoON() {
		return scoSolicitacaoServicoON;
	}

	protected FasesSolicitacaoServicoON getFasesSolicitacaoServicoON() {
		return fasesSolicitacaoServicoON;
	}

	private ScoSolicitacaoServicoRN getScoSolicitacaoServicoRN() {
		return scoSolicitacaoServicoRN;
	}
	
	

	protected ScoSsJnDAO getScoSsJnDAO() {
		return scoSsJnDAO;
	}

	protected void setScoSsJnDAO(ScoSsJnDAO scoSsJnDAO) {
		this.scoSsJnDAO = scoSsJnDAO;
	}

	@Override
	public ScoPontoParadaSolicitacao getPpsAutorizacao() {
		return this.getScoSolicitacaoServicoRN().getPpsAutorizacao();
	}

	@Override
	public ScoSolicitacaoServico duplicarSS(ScoSolicitacaoServico solicitacaoServico, boolean flagFlush, boolean isDuplicarPac, boolean manterCcOriginal)
			throws BaseException {
		return getScoSolicitacaoServicoRN().duplicarSS(solicitacaoServico, flagFlush, isDuplicarPac, manterCcOriginal);
	}

	private ScoAcoesPontoParadaDAO getScoAcoesPontoParadaDAO() {
		return scoAcoesPontoParadaDAO;
	}

	private ScoSolicitacaoCompraServicoDAO getScoSolicitacaoCompraServicoDAO() {
		return scoSolicitacaoCompraServicoDAO;
	}

	@Secure("#{s:hasPermission('cadastrarSolicitacaoCompras','gravar')}")
	public void excluirScoSolCompraServico(List<ScoSolicitacaoCompraServico> solCompraServicoList) throws ApplicationBusinessException {
		getScoSolicitacaoCompraServicoON().excluirSolicitacaoCompraServico(solCompraServicoList);
		this.getScoSolicitacaoCompraServicoDAO().flush();
	}

	public List<ScoSolicitacaoCompraServico> listarSolicitacaoCompraServico(ScoSolicitacaoDeCompra numeroSC, ScoSolicitacaoServico numeroSS) {
		return getScoSolicitacaoCompraServicoON().listarSolicitacaoCompraServico(numeroSC, numeroSS);

	}

	/*************
	 * Verifica se a Solicitação de servico está habilitada para ser encaminhada
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 *
	 */
	@Override
	public Boolean habilitarEncaminharSS(ScoSolicitacaoServico solicitacaoServico, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoEncaminhar, List<FccCentroCustos> listaCentroCustosUsuario) {
		return getScoSolicitacaoServicoON().habilitarEncaminharSS(solicitacaoServico, temPermissaoComprador, temPermissaoPlanejamento,
				temPermissaoEncaminhar, listaCentroCustosUsuario);
	}

	/*************
	 * Verifica se a Solicitação de servico está habilitada para ser autorizada
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 *
	 */
	public Boolean habilitarAutorizarSS(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {
		return getScoSolicitacaoServicoON().habilitarAutorizarSS(solicitacaoServico);
	}

	/****
	 * verifica se as solicitacoes estao no ponto de parada chefia
	 * 
	 * @param solicitacoes
	 * @return
	 */
	@Override
	public Boolean verificarPontoParadaChefia(List<ScoSolicitacaoServico> solicitacoes) {
		return this.getScoSolicitacaoServicoON().verificarPontoParadaChefia(solicitacoes);
	}

	/***
	 * Efetua a devolucao das solicitacoes de serviço
	 * 
	 * @param nroSsos
	 * @param justificativa
	 * @throws BaseException
	 */
	@Override
	public void devolverSolicitacoesServico(List<ScoSolicitacaoServico> listaSolicitacaoServico, String justificativa) throws BaseException {
		this.getScoSolicitacaoServicoON().devolverSolicitacoesServico(listaSolicitacaoServico, justificativa);
	}

	/**
	 * Insere na tabela de Journal da solicitação de servico.
	 * 
	 * @param ScoSsJn
	 * @throws ApplicationBusinessException
	 */
	public void inserirJournalSS(ScoSsJn scoSsJn) throws ApplicationBusinessException {
		this.getScoSolicitacaoServicoRN().inserirSSJournal(scoSsJn);
	}

	public void inserirSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico, RapServidores servidorLogado, boolean flagFlush,
			boolean duplicacao) throws BaseException {

		this.getScoSolicitacaoServicoRN().inserirSolicitacaoServico(solicitacaoServico, flagFlush, duplicacao);
	}

	@Override
	public void validarRegrasOrcamentarias(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoServicoClone)
			throws BaseException {
		getScoSolicitacaoServicoRN().validarRegrasOrcamentarias(solicitacaoServico, solicitacaoServicoClone);
	}

	@Override
	public void desatacharSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		getScoSolicitacaoServicoDAO().desatachar(solicitacaoServico);
	}
	
	@Override
	public Boolean verificarSolicitacaoDevolvidaAutorizacao(Integer numero) {
		return getScoSsJnDAO().verificarSolicitacaoDevolvidaAutorizacao(numero);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	

	@Override
	public List<ScoServico> listarSuggestionServicos(Object objPesquisa) {
		return scoServicoDAO.listarSuggestionServicos(objPesquisa);
	}

	@Override
	public Long listarSuggestionServicosCount(Object pesquisa) {
		return scoServicoDAO.listarSuggestionServicosCount(pesquisa);
	}
}
