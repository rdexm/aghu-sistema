package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.compras.cadastrosapoio.business.FcpAgenciaON;
import br.gov.mec.aghu.compras.cadastrosapoio.business.FcpBancoON;
import br.gov.mec.aghu.compras.cadastrosapoio.business.FcpRetencaoTributoON;
import br.gov.mec.aghu.compras.cadastrosapoio.business.FcpTributoON;
import br.gov.mec.aghu.compras.dao.FcpBancoDAO;
import br.gov.mec.aghu.compras.dao.ScoCaracteristicaUsuarioCentroCustoDAO;
import br.gov.mec.aghu.compras.dao.ScoCriterioEscolhaPropostaDAO;
import br.gov.mec.aghu.compras.dao.ScoDireitoAutorizacaoTempDAO;
import br.gov.mec.aghu.compras.dao.ScoEtapaModPacDAO;
import br.gov.mec.aghu.compras.dao.ScoFornRamoComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoJustificativaPrecoDAO;
import br.gov.mec.aghu.compras.dao.ScoLocalizacaoProcessoDAO;
import br.gov.mec.aghu.compras.dao.ScoModalidadeLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMotivoCancelamentoItemDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaServidorDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoTempoAndtPacDAO;
import br.gov.mec.aghu.compras.dao.ScoUnidadeMedidaDAO;
import br.gov.mec.aghu.compras.vo.ScoRamoComercialCriteriaVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioObjetoDoPac;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoEtapaModPac;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoFornRamoComercialId;
import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.model.ScoJustificativaPreco;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.model.ScoMotivoCancelamentoItem;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoTemposAndtPacsId;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


/**
 * Facade criada para as estorias 5602 - Cadastro Ramo Comercial 5604 - Cadastro
 * Forma Pagamento 5605 - Cadastro de Motivo Alteracao AF 5607 - Cadastro de
 * Unidade de Medida 5609 - Cadastro de Justificativas
 */

@Modulo(ModuloEnum.COMPRAS)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class ComprasCadastrosBasicosFacade extends BaseFacade implements IComprasCadastrosBasicosFacade {

	private static final long serialVersionUID = -3483287839307251476L;
	
	@EJB
	private ScoModalidadeLicitacaoON scoModalidadeLicitacaoON;
	@EJB
	private ScoPontoParadaServidorON scoPontoParadaServidorON;
	@EJB
	private ScoLocalizacaoProcessoON scoLocalizacaoProcessoON;
	@EJB
	private ManterScoJustificativaON manterScoJustificativaON;
	@EJB
	private ScoUnidadeMedidaON scoUnidadeMedidaON;
	@EJB
	private ScoTempoAndtPacON scoTempoAndtPacON;
	@EJB
	private MotivoAlteracaoAfON motivoAlteracaoAfON;
	@EJB
	private ScoCriterioEscolhaPropostaON scoCriterioEscolhaPropostaON;
	@EJB
	private ScoParamAutorizacaoScON scoParamAutorizacaoScON;
	@EJB
	private ScoRamoComercialON scoRamoComercialON;
	@EJB
	private ScoFormaPagamentoON scoFormaPagamentoON;
	@EJB
	private ScoCaracteristicaUsuarioCentroCustoON scoCaracteristicaUsuarioCentroCustoON;
	@EJB
	private ScoPontoParadaSolicitacaoON scoPontoParadaSolicitacaoON;

	@EJB
	private ScoJustificativaPrecoON scoJustificativaPrecoON;

	@EJB
	private ScoMotivoCancelamentoItemCRUD scoMotivoCancelamentoItemCRUD;

	@Inject
	private ScoDireitoAutorizacaoTempDAO scoDireitoAutorizacaoTempDAO;
	
	@Inject
	private ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO;

	@Inject
	private ScoJustificativaPrecoDAO scoJustificativaPrecoDAO;

	@Inject
	private ScoMotivoCancelamentoItemDAO scoMotivoCancelamentoItemDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private FcpAgenciaON fcpAgenciaON;
	
	@EJB
	private FcpBancoON fcpBancoON;
	
	@EJB
	private FcpRetencaoTributoON fcpRetencaoTributoON;
	
	@EJB
	private FcpTributoON fcpTributoON;
	
	@Inject
	private ScoModalidadeLicitacaoDAO scoModalidadeLicitacaoDAO;

	@Inject
	private ScoTempoAndtPacDAO scoTempoAndtPacDAO;

	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;

	@Inject
	private ScoUnidadeMedidaDAO scoUnidadeMedidaDAO;

	@Inject
	private ScoPontoParadaServidorDAO scoPontoParadaServidorDAO;

	@Inject
	private ScoCriterioEscolhaPropostaDAO scoCriterioEscolhaPropostaDAO;

	@Inject
	private ScoLocalizacaoProcessoDAO scoLocalizacaoProcessoDAO;
	
	@Inject
	private ScoFornRamoComercialDAO scoFornRamoComercialDAO;
	
	@EJB
	private ScoFornRamoComercialRN scoFornRamoComercialRN;
	
	@Inject
	private ScoEtapaModPacDAO scoEtapaModPacDAO;
	
	@Inject
	private FcpBancoDAO fcpBancoDAO;
	
	@EJB
	private ScoEtapaModPacON scoEtapaModPacON;	

	@Override
	public List<ScoFormaPagamento> pesquisarFormasPagamento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			final ScoFormaPagamento scoFormaPagamento) {

		return this.getScoFormaPagamentoON().pesquisarFormasPagamento(firstResult, maxResult, orderProperty, asc, scoFormaPagamento);
	}

	@Override
	public ScoRamoComercial obterScoRamoComercial(Short codigo) {
		return getScoRamoComercialON().obterScoRamoComercial(codigo);
	}

	@Override
	public List<ScoRamoComercial> pesquisarScoRamosComerciais(ScoRamoComercialCriteriaVO criteria, int firstResult, int maxResults,
			String orderProperty, boolean asc) {
		return getScoRamoComercialON().pesquisarScoRamosComerciais(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Boolean verificarProtocoloPac(RapServidores servidorLogado) {
		return scoCaracteristicaUsuarioCentroCustoDAO.verificarProtocoloPac(servidorLogado);
	}

	@Override
	public Long contarScoRamosComerciais(ScoRamoComercialCriteriaVO criteria) {
		return getScoRamoComercialON().contarScoRamosComerciais(criteria);
	}

	@Override
	public void persistir(ScoRamoComercial ramo) throws ApplicationBusinessException {
		getScoRamoComercialON().persistir(ramo);
	}

	private ScoRamoComercialON getScoRamoComercialON() {
		return scoRamoComercialON;
	}

	@Override
	public List<ScoJustificativa> pesquisarJustificativas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			final ScoJustificativa comprasJustificativa) {

		return this.getScoJustificativaON().pesquisarJustificativas(firstResult, maxResult, orderProperty, asc, comprasJustificativa);
	}

	@Override
	public Long pesquisarFormasPagamentoCount(final ScoFormaPagamento scoFormaPagamento) {

		return this.getScoFormaPagamentoON().pesquisarFormasPagamentoCount(scoFormaPagamento);
	}

	@Override
	public Long pesquisarJustificativaCount(final ScoJustificativa comprasJustificativa) {

		return this.getScoJustificativaON().pesquisarJustificativaCount(comprasJustificativa);
	}

	@Override	
	public ScoFormaPagamento obterFormaPagamento(Short codigo) {
		return this.getScoFormaPagamentoON().obterFormaPagamento(codigo);
	}

	@Override
	public ScoJustificativa obterJustificativa(final Short codigo) {
		return this.getScoJustificativaON().obterJustificativa(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioCompras','gravar')}")
	public void inserirJustificativa(final ScoJustificativa comprasJustificativa) throws ApplicationBusinessException {
		this.getScoJustificativaON().inserir(comprasJustificativa);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarApoioCompras','gravar')}")
	public void alterarJustificativa(final ScoJustificativa comprasJustificativa) throws ApplicationBusinessException {
		this.getScoJustificativaON().alterar(comprasJustificativa);
	}

	protected ManterScoJustificativaON getScoJustificativaON() {
		return manterScoJustificativaON;
	}

	protected ScoUnidadeMedidaDAO getScoUnidadeMedidaDAO() {
		return scoUnidadeMedidaDAO;
	}

	protected ScoUnidadeMedidaON getScoUnidadeMedidaON() {
		return scoUnidadeMedidaON;
	}

	/**
	 * Obtém uma unidade de medida por código ou descrição
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(Object parametro) {
		return getScoUnidadeMedidaDAO().pesquisarUnidadeMedidaPorCodigoDescricao(parametro);
	}

	@Override
	public Long pesquisarUnidadeMedidaPorCodigoDescricaoCount(Object param) {
		return getScoUnidadeMedidaDAO().pesquisarUnidadeMedidaPorCodigoDescricaoCount(param);
	}

	/**
	 * Obtém uma unidade(s) de medida ativa(s) por código ou descrição
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaAtivaPorCodigoDescricao(Object parametro) {
		return getScoUnidadeMedidaDAO().pesquisarUnidadeMedidaAtivaPorCodigoDescricao(parametro);
	}

	/**
	 * Pesquisa geral de registros na tabela {@code ScoUnidadeMedida}. Pode ser
	 * filtrada por parametros de entrada.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unidadeMedida
	 * @return Listagem contendo os registros encontrados.
	 */
	@Override
	public List<ScoUnidadeMedida> pesquisarUnidadeMedida(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoUnidadeMedida unidadeMedida) {

		return this.getScoUnidadeMedidaDAO().pesquisarUnidadeMedida(firstResult, maxResult, orderProperty, asc, unidadeMedida);
	}

	/**
	 * Faz a contagem de registros na tabela {@code Unidade Medida}.
	 * 
	 * @param unidadeMedida
	 * @return Inteiro indicando o numero de registros encontrados.
	 */
	@Override
	public Long listarUnidadeMedidaCount(ScoUnidadeMedida scoUnidadeMedida) {

		return this.getScoUnidadeMedidaDAO().listarUnidadeMedidaCount(scoUnidadeMedida);
	}

	/**
	 * Obtem o registro de {@code ScoUnidadeMedida} a partir do {@code codigo}.
	 * 
	 * @param codigo
	 * @return Registro unico encontrado.
	 */
	@Override
	public ScoUnidadeMedida obterUnidadeMedida(String codigo) {
		return this.getScoUnidadeMedidaDAO().obterPorChavePrimaria(codigo);
	}

	/**
	 * Altera ou Insere um novo registro .
	 * 
	 * @param unidadeMedida
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void cadastrarUnidadeMedida(ScoUnidadeMedida unidadeMedida) throws ApplicationBusinessException {
		this.getScoUnidadeMedidaON().cadastrar(unidadeMedida);
	}

	@Override
	public void inserirFormaPagamento(ScoFormaPagamento scoFormaPagamento) throws ApplicationBusinessException {
		this.getScoFormaPagamentoON().inserirFormaPagamento(scoFormaPagamento);
	}

	@Override
	public void alterarFormaPagamento(ScoFormaPagamento scoFormaPagamento) throws ApplicationBusinessException {
		this.getScoFormaPagamentoON().alterarFormaPagamento(scoFormaPagamento);
	}

	private ScoFormaPagamentoON getScoFormaPagamentoON() {
		return scoFormaPagamentoON;
	}

	// #5605 - Cadastro de Motivo Alteração AF
	@Override
	public List<ScoMotivoAlteracaoAf> pesquisarScoMotivoAlteracaoAf(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoMotivoAlteracaoAf motivoAlteracao) {
		return this.getScoMotivoAlteracaoAfON().pesquisarScoMotivoAlteracaoAf(firstResult, maxResult, orderProperty, asc, motivoAlteracao);
	}

	@Override
	public Long pesquisarScoMotivoAlteracaoAfCount(ScoMotivoAlteracaoAf motivoAlteracao) {
		return this.getScoMotivoAlteracaoAfON().pesquisarScoMotivoAlteracaoAfCount(motivoAlteracao);
	}

	@Override
	public ScoMotivoAlteracaoAf obterScoMotivoAlteracaoAf(Short codigo) {
		return this.getScoMotivoAlteracaoAfON().obterScoMotivoAlteracaoAf(codigo);
	}

	@Override
	public void persistirScoMotivoAlteracaoAf(ScoMotivoAlteracaoAf motivoAlteracao) throws ApplicationBusinessException {
		this.getScoMotivoAlteracaoAfON().persistirScoMotivoAlteracaoAf(motivoAlteracao);
	}

	public MotivoAlteracaoAfON getScoMotivoAlteracaoAfON() {
		return motivoAlteracaoAfON;
	}

	// #5608 - Cadastro das Características do usuário X centro de custo
	@Override
	public List<ScoCaracteristicaUsuarioCentroCusto> pesquisarCaracteristicaUserCC(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) {

		return this.getScoCaracteristicaUsuarioCentroCustoON().pesquisarCaracteristicaUserCC(firstResult, maxResult, orderProperty, asc,
				caracteristicaUserCC);
	}

	@Override
	public Long pesquisarCaracteristicaUserCCCount(final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) {

		return this.getScoCaracteristicaUsuarioCentroCustoON().pesquisarCaracteristicaUserCCCount(caracteristicaUserCC);
	}

	@Override
	public ScoCaracteristicaUsuarioCentroCusto obterCaracteristicaUserCC(final Integer seq) {

		return this.getScoCaracteristicaUsuarioCentroCustoON().obterCaracteristicaUserCC(seq);
	}

	@Override
	public void inserirCaracteristicaUserCC(final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC)
			throws ApplicationBusinessException {
		this.getScoCaracteristicaUsuarioCentroCustoON().inserir(caracteristicaUserCC);
	}

	@Override
	public void alterarCaracteristicaUserCC(final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC)
			throws ApplicationBusinessException {
		this.getScoCaracteristicaUsuarioCentroCustoON().alterar(caracteristicaUserCC);
	}

	@Override	
	public void excluirCaracteristicaUserCC(final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) throws ApplicationBusinessException
	{
		this.getScoCaracteristicaUsuarioCentroCustoON().excluir(caracteristicaUserCC);
	}

	@Override
	public List<ScoCaracteristica> pesquisarCaracteristicasPorCodigoOuDescricao(Object objPesquisa) {
		return this.getScoCaracteristicaUsuarioCentroCustoON().pesquisarCaracteristicasPorCodigoOuDescricao(objPesquisa);
	}

	@Override
	public Long pesquisarCaracteristicasPorCodigoOuDescricaoCount(Object objPesquisa) {
		return this.getScoCaracteristicaUsuarioCentroCustoON().pesquisarCaracteristicasPorCodigoOuDescricaoCount(objPesquisa);
	}

	public ScoCaracteristicaUsuarioCentroCustoON getScoCaracteristicaUsuarioCentroCustoON() {
		return scoCaracteristicaUsuarioCentroCustoON;
	}

	@Override
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao(String filtro,
			Boolean fromLiberacao) throws ApplicationBusinessException {
		return getScoPontoParadaSolicitacaoON().pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao(filtro, fromLiberacao);
	}

	@Override
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(String filtro, Short pontoOrigem) {
		return getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(filtro, pontoOrigem);
	}

	/**
	 * Ponto Parada Servidor
	 * 
	 * @author flavio rutkowski
	 * @param codigo
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	@Override
	public ScoPontoServidor obterPontoParadaServidorCodigoMatriculaVinculo(Short codigo, Short vinculo, Integer matricula) {
		return getManterPontoParadaServidorON().obterPontoParadaServidorCodigoMatriculaVinculo(codigo, vinculo, matricula);
	}

	@Override
	public Boolean verificarPontoParadaComprador(ScoPontoParadaSolicitacao pontoParada) throws ApplicationBusinessException {
		return this.getScoPontoParadaSolicitacaoON().verificarPontoParadaComprador(pontoParada);
	}

	@Override
	public ScoPontoParadaSolicitacao obterPontoParadaAutorizacao() throws ApplicationBusinessException {
		return this.getScoPontoParadaSolicitacaoON().obterPontoParadaChefia();
	}

	@Override
	public ScoPontoParadaSolicitacao obterPontoParadaPorTipo(DominioTipoPontoParada tipoPontoParada) {
		return this.getScoPontoParadaSolicitacaoON().obterPontoParadaPorTipo(tipoPontoParada);
	}

	private ScoPontoParadaServidorON getManterPontoParadaServidorON() {
		return scoPontoParadaServidorON;
	}

	// #5227 - Cadastro de Pontos de Parada da Solicitação
	@Override
	public ScoPontoParadaSolicitacao obterPontoParada(final Short codigo) {
		return getScoPontoParadaSolicitacaoON().obterPontoParadaSolicitacao(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('consultarApoioCompras', 'visualizar')}")
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {

		return this.getScoPontoParadaSolicitacaoON().pesquisarPontoParadaSolicitacao(firstResult, maxResult, orderProperty, asc,
				scoPontoParadaSolicitacao);
	}

	@Override
	public Long pesquisarPontoParadaSolicitacaoCount(final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {

		return this.getScoPontoParadaSolicitacaoON().pesquisarPontoParadaSolicitacaoCount(scoPontoParadaSolicitacao);
	}

	@Override
	public Boolean verificarPontoParadaPermitido(ScoPontoParadaSolicitacao pontoParada) {
		return getManterPontoParadaServidorON().verificarPontoParadaPermitido(pontoParada);
	}

	@Override
	public void inserirPontoParadaSolicitacao(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) throws ApplicationBusinessException {
		this.getScoPontoParadaSolicitacaoON().inserirPontoParadaSolicitacao(scoPontoParadaSolicitacao);
	}

	@Override
	public void alterarPontoParadaSolicitacao(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao,
			ScoPontoParadaSolicitacao scoPontoParadaSolAnterior) throws ApplicationBusinessException {
		this.getScoPontoParadaSolicitacaoON().alterarPontoParadaSolicitacao(scoPontoParadaSolicitacao, scoPontoParadaSolAnterior);
	}

	@Override
	public void excluirPontoParadaSolicitacao(final Short scoPontoParadaSolicitacao) throws ApplicationBusinessException {
		this.getScoPontoParadaSolicitacaoON().excluirPontoParadaSolicitacao(scoPontoParadaSolicitacao);
	}

	@Override
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorEnviadoPara(Short ppsEnviadoPara) {
		return getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaPorPontoParadaSolicitacaoEnviadoPara(ppsEnviadoPara);
	}

	@Override
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao(String filtro) {
		return getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao(filtro);
	}

	@Override
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoAtivos(String filtro) {
		return getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoAtivos(filtro);
	}

	@Override
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivos(String filtro,
			Boolean isPerfilGeral) {
		return getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivos(filtro, isPerfilGeral);
	}

	@Override
	public Long pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivosCount(String filtro, Boolean isPerfilGeral) {
		return getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivosCount(filtro,
				isPerfilGeral);
	}

	@Override
	public Long pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount(String filtro) {
		return getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount(filtro);
	}

	private ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return scoPontoParadaSolicitacaoDAO;
	}

	private ScoPontoParadaSolicitacaoON getScoPontoParadaSolicitacaoON() {
		return scoPontoParadaSolicitacaoON;
	}

	/**
	 * lista ScoPontoParadaSolicitacao por codigo ou descrição para SB
	 * 
	 * @param pesquisa
	 * @return
	 */
	public List<ScoPontoParadaSolicitacao> listarPontoParadaSolicitacao(Object pesquisa) {
		return getScoPontoParadaSolicitacaoDAO().listarPontoParadaSolicitacao(pesquisa);

	}

	public List<ScoPontoParadaSolicitacao> listarPontoParadaPontoServidor(Object pesquisa) {
		return getScoPontoParadaSolicitacaoDAO().listarPontoParadaSolicitacao(pesquisa);

	}

	// #22310 - Parâmetros de Autorização da Solicitação de Compra
	@Override
	public ScoParamAutorizacaoSc obterParamAutorizacaoSc(final Integer seq) {
		return getScoParamAutorizacaoScON().obterParamAutorizacaoSc(seq);
	}

	@Override
	public ScoParamAutorizacaoSc obterParametrosAutorizacaoSCPrioridade(FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicacao,
			RapServidores servidor) {
		return getScoParamAutorizacaoScON().obterParametrosAutorizacaoSCPrioridade(centroCusto, centroCustoAplicacao, servidor);
	}

	@Override
	public List<ScoParamAutorizacaoSc> pesquisarParamAutorizacaoSc(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		return this.getScoParamAutorizacaoScON().pesquisarParamAutorizacaoSc(firstResult, maxResult, orderProperty, asc,
				scoParamAutorizacaoSc);
	}

	@Override
	public Long pesquisarParamAutorizacaoScCount(final ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		return this.getScoParamAutorizacaoScON().pesquisarParamAutorizacaoScCount(scoParamAutorizacaoSc);
	}

	@Override
	public void inserirParamAutorizacaoSc(ScoParamAutorizacaoSc scoParamAutorizacaoSc) throws ApplicationBusinessException {
		this.getScoParamAutorizacaoScON().inserirParamAutorizacaoSc(scoParamAutorizacaoSc);
	}

	@Override
	public void alterarParamAutorizacaoSc(ScoParamAutorizacaoSc scoParamAutorizacaoSc) throws ApplicationBusinessException {
		this.getScoParamAutorizacaoScON().alterarParamAutorizacaoSc(scoParamAutorizacaoSc);
	}

	private ScoParamAutorizacaoScON getScoParamAutorizacaoScON() {
		return scoParamAutorizacaoScON;
	}

	/**
	 * Estoria 5232 - RN4
	 * 
	 * @throws BaseException
	 */
	@Override
	public void enviarSolicitacaoCompras(ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoSolicitacaoDeCompra solicitacaoCompraOld,
			ScoPontoParadaSolicitacao pontoParadaDestino) throws BaseException {

		getScoPontoParadaSolicitacaoON().enviarSolicitacaoCompra(solicitacaoDeCompra, pontoParadaDestino);

	}

	// #5600 - Locais dos Processos
	@Override
	public ScoLocalizacaoProcesso obterLocalizacaoProcesso(final Short codigo) {
		return getScoLocalizacaoProcessoDAO().obterLocalizacaoProcesso(codigo);
	}

	@Override
	public List<ScoLocalizacaoProcesso> listarLocalizacaoProcesso(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final ScoLocalizacaoProcesso scoLocalizacaoProcesso) {

		return this.getScoLocalizacaoProcessoDAO().listarLocalizacaoProcesso(firstResult, maxResult, orderProperty, asc,
				scoLocalizacaoProcesso);
	}

	@Override
	public Long listarLocalizacaoProcessoCount(final ScoLocalizacaoProcesso scoLocalizacaoProcesso) {
		return this.getScoLocalizacaoProcessoDAO().listarLocalizacaoProcessoCount(scoLocalizacaoProcesso);
	}

	@Override
	public void inserirLocalizacaoProcesso(ScoLocalizacaoProcesso scoLocalizacaoProcesso) throws ApplicationBusinessException {
		this.getScoLocalizacaoProcessoON().inserirLocalizacaoProcesso(scoLocalizacaoProcesso);
	}

	@Override
	public void alterarLocalizacaoProcesso(ScoLocalizacaoProcesso scoLocalizacaoProcesso) throws ApplicationBusinessException {
		this.getScoLocalizacaoProcessoON().alterarLocalizacaoProcesso(scoLocalizacaoProcesso);
	}

	@Override
	public void excluirLocalizacaoProcesso(Short codigo) throws ApplicationBusinessException {
		this.getScoLocalizacaoProcessoON().excluirLocalizacaoProcesso(codigo);
	}

	@Override
	public List<ScoUnidadeMedida> obterUnidadesMedida(String objPesquisa) {
		return this.getScoUnidadeMedidaDAO().pesquisarUnidadeMedidaPorCodigoDescricao(objPesquisa);
	}

	@Override
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(Object objPesquisa, boolean apenasAtivos) {
		return this.getScoUnidadeMedidaDAO().pesquisarUnidadeMedidaPorCodigoDescricao(objPesquisa, apenasAtivos);
	}

	@Override
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorSigla(Object objPesquisa, boolean apenasAtivos) {
		return this.getScoUnidadeMedidaDAO().pesquisarUnidadeMedidaPorSigla(objPesquisa, apenasAtivos);
	}

	@Override
	public Long pesquisarUnidadeMedidaPorSiglaCount(Object objPesquisa, boolean apenasAtivos) {
		return this.getScoUnidadeMedidaDAO().pesquisarUnidadeMedidaPorSiglaCount(objPesquisa, apenasAtivos);
	}

	/**
	 * Estoria 5232 - RN12 Verifica se a quantidade do ponto de parada do
	 * servidor é maior que 1
	 * 
	 * @param vinculo
	 * @param matricula
	 * @return Verdadeiro caso o resultado seja maior que 1.
	 */
	@Override
	public Boolean isQuantidadePontoParadaServidorMaiorQueUm(Integer matricula, Short vinculo) {
		return pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(null, matricula, vinculo) > 1;
	}

	/**
	 * @author clayton.bras
	 * @param codigo
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	@Override
	public Long pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(final Short codigo, final Integer matricula, final Short vinculo) {
		return getScoPontoParadaServidorDAO().pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(codigo, matricula, vinculo);
	}

	private ScoLocalizacaoProcessoON getScoLocalizacaoProcessoON() {
		return scoLocalizacaoProcessoON;
	}

	private ScoLocalizacaoProcessoDAO getScoLocalizacaoProcessoDAO() {
		return scoLocalizacaoProcessoDAO;
	}

	private ScoPontoParadaServidorDAO getScoPontoParadaServidorDAO() {
		return scoPontoParadaServidorDAO;
	}

	@Override
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcessoPorCodigoOuDescricao(Object parametro, Boolean indAtivo) {
		return this.getScoLocalizacaoProcessoDAO().pesquisarLocalizacaoProcessoPorCodigoOuDescricao(parametro, indAtivo);
	}

	// #5601 - Tempos Localização PAC
	@Override
	public ScoTempoAndtPac obterTempoAndtPac(ScoTemposAndtPacsId chavePrimaria) {
		return getScoTempoAndtPacDAO().obterPorChavePrimaria(chavePrimaria);
	}

	public ScoTempoAndtPac obterPorChavePrimariaSemLazy(ScoTemposAndtPacsId chavePrimaria) {
		return getScoTempoAndtPacDAO().obterPorChavePrimariaSemLazy(chavePrimaria);
	}

	@Override
	public List<ScoTempoAndtPac> listarTempoAndtPac(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoModalidadeLicitacao modalidadeLicitacao, ScoLocalizacaoProcesso localizacaoProcesso, ScoTempoAndtPac tempoLocalizacaoPac) {

		return this.getScoTempoAndtPacDAO().listarTempoAndtPac(firstResult, maxResult, orderProperty, asc, modalidadeLicitacao,
				localizacaoProcesso, tempoLocalizacaoPac);
	}

	@Override
	public Long listarTempoAndtPacCount(final ScoModalidadeLicitacao modalidadeLicitacao, final ScoLocalizacaoProcesso localizacaoProcesso,
			final ScoTempoAndtPac tempoLocalizacaoPac) {

		return this.getScoTempoAndtPacDAO().listarTempoAndtPacCount(modalidadeLicitacao, localizacaoProcesso, tempoLocalizacaoPac);
	}

	@Override
	public void inserirTempoAndtPac(ScoTempoAndtPac tempoLocalizacaoPac) throws ApplicationBusinessException {
		this.getScoTempoAndtPacON().inserirTempoAndtPac(tempoLocalizacaoPac);
	}

	@Override
	public void alterarTempoAndtPac(ScoTempoAndtPac tempoLocalizacaoPac) throws ApplicationBusinessException {
		this.getScoTempoAndtPacON().alterarTempoAndtPac(tempoLocalizacaoPac);
	}

	@Override
	public void excluirTempoAndtPac(ScoTemposAndtPacsId id) throws ApplicationBusinessException {
		this.getScoTempoAndtPacON().excluirTempoAndtPac(id);
	}

	protected ScoTempoAndtPacDAO getScoTempoAndtPacDAO() {
		return scoTempoAndtPacDAO;
	}

	public ScoTempoAndtPacON getScoTempoAndtPacON() {
		return scoTempoAndtPacON;
	}

	public ScoDireitoAutorizacaoTempDAO getScoDireitoAutorizacaoTempDAO() {
		return scoDireitoAutorizacaoTempDAO;
	}
	
	@Override
	public List<ScoModalidadeLicitacao> listarModalidadeLicitacaoAtivas(Object pesquisa) {
		return getScoModalidadeLicitacaoDAO().listarModalidadeLicitacaoAtivas(pesquisa);
	}

	@Override
	public ScoDireitoAutorizacaoTemp obterDireitoAutorizacaoTemporarioPorId(ScoDireitoAutorizacaoTemp direito) {
		return getScoDireitoAutorizacaoTempDAO().obterDireitoAutorizacaoTemporarioPorId(direito);
	}
	
	/**
	 * Retorna lista de ScoModalidadeLicitacao de acordo com o parametro
	 * informado
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacaoPorCodigoDescricao(Object parametro) {
		return this.pesquisarModalidadeLicitacaoPorCodigoDescricao(parametro, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.compras.cadastrosbasicos.business.
	 * IComprasCadastrosBasicosFacade
	 * #pesquisarModalidadeLicitacaoPorCodigoDescricao(java.lang.Object,
	 * br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacaoPorCodigoDescricao(final Object parametro,
			final DominioSituacao situacao) {
		return getScoModalidadeLicitacaoDAO().pesquisarModalidadeLicitacaoPorCodigoDescricao(parametro, situacao);
	}

	@Override
	public ScoModalidadeLicitacao obterScoModalidadeLicitacaoPorChavePrimaria(String codigo) {
		return this.getScoModalidadeLicitacaoDAO().obterPorChavePrimaria(codigo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.suprimentos.business.IComprasFacade#
	 * obterModalidadesLicitacaoAprovadasPorCodigo(java.lang.String)
	 */
	@Override
	public List<ScoModalidadeLicitacao> obterModalidadesLicitacaoAprovadasPorCodigo(final String codigo) {
		return getScoModalidadeLicitacaoDAO().obterModalidadesLicitacaoAprovadasPorCodigo(codigo);
	}

	// #5599 - Modalidade Pac
	@Override
	public List<ScoModalidadeLicitacao> listarModalidadeLicitacao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final ScoModalidadeLicitacao scoModalidadePac) {

		return this.getScoModalidadeLicitacaoDAO().listarModalidadeLicitacao(firstResult, maxResult, orderProperty, asc, scoModalidadePac);
	}

	@Override
	public Long listarModalidadeCount(String filter) {
		return this.getScoModalidadeLicitacaoDAO().listarModalidadeCount(filter);
	}

	@Override
	public Long listarModalidadeLicitacaoCount(final ScoModalidadeLicitacao scoModalidadePac) {
		return this.getScoModalidadeLicitacaoDAO().listarModalidadeLicitacaoCount(scoModalidadePac);
	}

	@Override
	public ScoModalidadeLicitacao obterModalidadeLicitacao(final String codigo) {
		return getScoModalidadeLicitacaoDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public void inserirModalidadeLicitacao(ScoModalidadeLicitacao scoModalidadePac) throws ApplicationBusinessException {
		this.getScoModalidadeLicitacaoON().inserirModalidadeLicitacao(scoModalidadePac);
	}

	@Override
	public void alterarModalidadeLicitacao(ScoModalidadeLicitacao scoModalidadePac) throws ApplicationBusinessException {
		this.getScoModalidadeLicitacaoON().alterarModalidadeLicitacao(scoModalidadePac);
	}

	private ScoModalidadeLicitacaoDAO getScoModalidadeLicitacaoDAO() {
		return scoModalidadeLicitacaoDAO;
	}

	private ScoCriterioEscolhaPropostaDAO getScoCriterioEscolhaPropostaDAO() {
		return scoCriterioEscolhaPropostaDAO;
	}

	private ScoModalidadeLicitacaoON getScoModalidadeLicitacaoON() {
		return scoModalidadeLicitacaoON;
	}

	@Override
	public List<ScoCriterioEscolhaProposta> pesquisarCriterioEscolhaProposta(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio) {
		return this.getScoCriterioEscolhaPropostaON().pesquisarCriterioEscolhaProposta(firstResult, maxResult, orderProperty, asc,
				codigoCriterio, descricaoCriterio, situacaoCriterio);
	}

	@Override
	public Long pesquisarCriterioEscolhaPropostaCount(Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio) {
		return this.getScoCriterioEscolhaPropostaON().pesquisarCriterioEscolhaPropostaCount(codigoCriterio, descricaoCriterio,
				situacaoCriterio);
	}

	@Override
	public ScoCriterioEscolhaProposta obterCriterioEscolhaProposta(Short codigoCriterio) {
		return this.getScoCriterioEscolhaPropostaON().obterCriterioEscolhaProposta(codigoCriterio);
	}

	@Override
	public List<ScoCriterioEscolhaProposta> pesquisarCriterioEscolhaProposta() {
		return getScoCriterioEscolhaPropostaDAO().pesquisarCriterioEscolhaAtivos();
	}

	@Override
	public void persistirCriterioEscolhaProposta(ScoCriterioEscolhaProposta criterioEscolhaProposta) throws ApplicationBusinessException {
		this.getScoCriterioEscolhaPropostaON().persistirCriterioEscolha(criterioEscolhaProposta);
	}

	@Override
	public void excluirCriterioEscolhaProposta(Short criterioEscolha) throws ApplicationBusinessException {
		this.getScoCriterioEscolhaPropostaON().excluirCriterioEscolha(criterioEscolha);
	}

	private ScoCriterioEscolhaPropostaON getScoCriterioEscolhaPropostaON() {
		return scoCriterioEscolhaPropostaON;
	}

	@Override
	public Long listarJustificativasPrecoContratadoCount(ScoJustificativaPreco justificativa) {
		return this.scoJustificativaPrecoDAO.pesquisarJustificativasCount(justificativa);
	}

	@Override
	public List<ScoJustificativaPreco> pesquisarJustificativasPrecoContratado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoJustificativaPreco justificativa) {
		return this.scoJustificativaPrecoDAO.pesquisarJustificativas(firstResult, maxResult, orderProperty, asc, justificativa);
	}

	@Override
	public void persistirNivelJustificativaPreco(ScoJustificativaPreco justificativa) {
		this.scoJustificativaPrecoON.persistirJustificativa(justificativa);
	}

	@Override
	public ScoJustificativaPreco obterJustificativaPorCodigo(Short codigo) {
		return this.scoJustificativaPrecoDAO.obterPorChavePrimaria(codigo);
	}

	//#24709 - Cadastro de Motivo de Cancelamento de Itens PAC

	@Override
	public List<ScoMotivoCancelamentoItem> pesquisarScoMotivoCancelamentoItem(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoMotivoCancelamentoItem motivoCancel) {
		return this.scoMotivoCancelamentoItemDAO.pesquisaMotivoCancelamentoItem(firstResult, maxResult, orderProperty, asc,
				motivoCancel.getCodigo(), motivoCancel.getDescricao(), motivoCancel.getIndAtivo());
	}

	@Override
	public Long pesquisarScoMotivoCancelamentoItemCount(ScoMotivoCancelamentoItem motivoCancel) {
		return this.scoMotivoCancelamentoItemDAO.pesquisaMotivoCancelamentoItemCount(motivoCancel.getCodigo(), motivoCancel.getDescricao(),
				motivoCancel.getIndAtivo());
	}

	@Override
	public ScoMotivoCancelamentoItem obterScoMotivoCancelamentoItem(String codigo) {
		return this.scoMotivoCancelamentoItemDAO.obterMotivoCancelamentoItemPorCodigo(codigo);
	}
		   
	@Override
	public void persistirScoMotivoCancelamentoItem(ScoMotivoCancelamentoItem motivoCancel) throws ApplicationBusinessException{
		this.scoMotivoCancelamentoItemCRUD.incluirMotivoCancelamentoItem(motivoCancel);
	}

	@Override
	public Long listarModalidadeLicitacaoAtivasCount(Object pesquisa) {
		return this.scoModalidadeLicitacaoDAO.listarModalidadeLicitacaoAtivasCount(pesquisa);
	}
	
	@Override
	public List<ScoModalidadeLicitacao> listarModalidadeLicitacao(Object pesquisa) {
		return getScoModalidadeLicitacaoDAO().listarModalidadeLicitacao(pesquisa);
	}
	@Override
	public ScoPontoParadaSolicitacao obterScoPontoParadaSolicitacaoPorChavePrimaria(Short codigo) {
		return this.scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(codigo);
	}

	@Override
	public FccCentroCustos obterCcAplicacaoAlteracaoRmGppg(RapServidores servidorLogado) {
		return this.scoCaracteristicaUsuarioCentroCustoDAO.obterCcAplicacaoAlteracaoRmGppg(servidorLogado);
	}


	@Override
	public FccCentroCustos obterCcAplicacaoAlteracaoRmGppg() {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		return this.scoCaracteristicaUsuarioCentroCustoDAO
				.obterCcAplicacaoAlteracaoRmGppg(servidorLogado);
	}

	@Override
	public ScoCaracteristicaUsuarioCentroCusto montarScoCaracUsuario(DominioCaracteristicaCentroCusto preencherCcSolicRm) {
		return this.getScoCaracteristicaUsuarioCentroCustoON().montarScoCaracUsuario(preencherCcSolicRm);
	}

	@Override
	public ScoCaracteristicaUsuarioCentroCusto obterCaracteristica(DominioCaracteristicaCentroCusto preencherCcSolicRm) {
		return this.scoCaracteristicaUsuarioCentroCustoDAO.obterCaracteristica(servidorLogadoFacade.obterServidorLogado(), preencherCcSolicRm);
	}
	
	// Implementações das interfaces do CRUD de agencia
	
	/**
	 * Obter lista agência com paginação.
	 */
	public List<FcpAgenciaBanco> pesquisarListaAgencia(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Short codBanco, Integer codigoAgencia) throws BaseException {
		return fcpAgenciaON.pesquisarListaAgencia(firstResult, maxResult, orderProperty, asc, codBanco, codigoAgencia);
	}

	/**
	 * Obter count lista de agências.
	 */
	public Long pesquisarCountListaAgencia(Short codBanco, Integer codigoAgencia) throws BaseException {
		return fcpAgenciaON.pesquisarCountListaAgencia(codBanco, codigoAgencia);
	}

	/**
	 * Inserir agência.
	 */
	public void persistirAgencia(FcpAgenciaBanco fcpAgenciaBanco) throws BaseException {
		fcpAgenciaON.persistirAgencia(fcpAgenciaBanco);		
	}
	
	public void verificarAgenciaBancariaComMesmoCodigo(FcpAgenciaBanco fcpAgenciaBanco) throws BaseException {
		fcpAgenciaON.verificarAgenciaBancariaComMesmoCodigo(fcpAgenciaBanco);
	}

	/**
	 * Excluir agência.
	 */
	public void excluirAgencia(FcpAgenciaBanco fcpAgenciaBanco) throws BaseException {
		fcpAgenciaON.excluirAgencia(fcpAgenciaBanco);
	}
	
	// Implementacao da FACADE de BANCO
	
	/**
	 * Obter lista banco com paginação.
	 */
	public List pesquisarListaBanco(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpBanco fcpBanco) throws BaseException {
		return getFcpBancoON().pesquisarListaBanco(firstResult, maxResult, orderProperty, asc, fcpBanco);
	}

	/**
	 * Obter count lista de bancos.
	 */
	public Long pesquisarCountListaBanco(FcpBanco fcpBanco) throws BaseException {
		return getFcpBancoON().pesquisarCountListaBanco(fcpBanco);
	}
	
	/**
	 * Pesquisar banco.
	 */
	public FcpBanco pesquisarBanco(Short codigoBanco) throws BaseException {
		return getFcpBancoON().pesquisarBanco(codigoBanco);
	}

	/**
	 * Inserir banco.
	 */
	public boolean inserirBanco(FcpBanco fcpBanco) throws BaseException {
		return getFcpBancoON().inserirBanco(fcpBanco);		
	}

	/**
	 * Atualizar banco.
	 */
	public void atualizarBanco(FcpBanco fcpBanco) throws BaseException {
		getFcpBancoON().atualizarBanco(fcpBanco);
	}

	/**
	 * Excluir banco.
	 */
	public boolean excluirBanco(FcpBanco fcpBanco) throws BaseException {
		return getFcpBancoON().excluirBanco(fcpBanco);
	}
	
	/**
	 * Pesquisar por código ou descrição.
	 */
	@Override
	public List<FcpBanco> pesquisarSuggestionBox(String parametro) throws BaseException {
		return fcpBancoDAO.pesquisarCodigoDescricao(parametro);
	}
	
	/**
	 * Pesquisar por código ou descrição.
	 */
	@Override
	public Long pesquisarSuggestionBoxCount(String parametro) throws BaseException {
		return fcpBancoDAO.pesquisarCodigoDescricaoCount(parametro);
	}

	/**
	 * @return the fcpBancoON
	 */
	public FcpBancoON getFcpBancoON() {
		return fcpBancoON;
	}

	/**
	 * @param fcpBancoON the fcpBancoON to set
	 */
	public void setFcpBancoON(FcpBancoON fcpBancoON) {
		this.fcpBancoON = fcpBancoON;
	}
	
	//Implementacao da FACADE para Retencao Tributo
	
	
	@SuppressWarnings("rawtypes")
	public List pesquisarListaCodigoRecolhimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
		return getFcpRetencaoTributoON().obterListaCodigoRecolhimento(firstResult, maxResult, orderProperty, asc, fcpRetencaoTributo);
	}
	
	public Long pesquisarCountCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
		return getFcpRetencaoTributoON().obterCountCodigoRecolhimento(fcpRetencaoTributo); 
	}

	public void inserirCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
		getFcpRetencaoTributoON().inserirCodigoRecolhimento(fcpRetencaoTributo);
	}

	public void atualizarCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
		getFcpRetencaoTributoON().atualizarCodigoRecolhimento(fcpRetencaoTributo);
	}

	public void excluirCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws ApplicationBusinessException {
		getFcpRetencaoTributoON().excluirCodigoRecolhimento(fcpRetencaoTributo);
	}
	
	public List<FcpRetencaoTributo> pesquisarRecolhimentoPorCodigoOuDescricao(Object parametro) {
		return getFcpRetencaoTributoON().pesquisarRecolhimentoPorCodigoOuDescricao(parametro);
	}
	
	public Long pesquisarRecolhimentoPorCodigoOuDescricaoCount(final String parametro) {
		  return getFcpRetencaoTributoON().pesquisarRecolhimentoPorCodigoOuDescricaoCount(parametro);
	}

	/**
	 * @return the fcpRetencaoTributoON
	 */
	public FcpRetencaoTributoON getFcpRetencaoTributoON() {
		return fcpRetencaoTributoON;
	}

	/**
	 * @param fcpRetencaoTributoON the fcpRetencaoTributoON to set
	 */
	public void setFcpRetencaoTributoON(FcpRetencaoTributoON fcpRetencaoTributoON) {
		this.fcpRetencaoTributoON = fcpRetencaoTributoON;
	}
	
	// Implementacao da Facade para Tributos
	
public List<FcpRetencaoAliquota> pesquisarListaTributo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
		
		return getFcpTributoON().obterListaTributo(firstResult, maxResult, orderProperty, asc, fcpRetencaoTributo);
	}
		 
	 public Long pesquisarCountTributo(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException {
		 
		 return getFcpTributoON().obterCountTributo(fcpRetencaoTributo);
	 }

	public void inserirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException {
		
		getFcpTributoON().inserirRetencaoAliquota(fcpRetencaoAliquota);
	}

	public void atualizarRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException {
		getFcpTributoON().atualizarRetencaoAliquota(fcpRetencaoAliquota);
	}

	public void excluirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws ApplicationBusinessException {
		fcpTributoON.excluirRetencaoAliquota(fcpRetencaoAliquota);
	}
	
	public RapServidores obterRapServidor(Usuario usuario) throws BaseException {
		
		return fcpTributoON.obterRapServidor(usuario);
	}

	/**
	 * @return the fcpTributoON
	 */
	public FcpTributoON getFcpTributoON() {
		return fcpTributoON;
	}

	/**
	 * @param fcpTributoON the fcpTributoON to set
	 */
	public void setFcpTributoON(FcpTributoON fcpTributoON) {
		this.fcpTributoON = fcpTributoON;
	}
	
	@Override
	public List<ScoEtapaModPac> listarEtapasPac(
			ScoTempoAndtPac tempoLocalizacaoPac,
			DominioObjetoDoPac objetoPacPesquisa) {
		return getScoEtapaModPacDAO().listarEtapasModPac(tempoLocalizacaoPac, objetoPacPesquisa);		
	}

	@Override
	public void validaEtapasComTempoPrevistoExecucao(
			List<ScoEtapaModPac> etapasModPac) throws ApplicationBusinessException {
		this.getScoEtapaModPacON().validaEtapasComTempoPrevistoExecucao(etapasModPac);	
	}

	@Override
	public void inserirEtapaPac(ScoEtapaModPac etapaModPac) throws ApplicationBusinessException {
		this.getScoEtapaModPacON().inserirEtapaModPac(etapaModPac);
	}

	@Override
	public void alterarEtapaModPac(ScoEtapaModPac etapaModPac)
			throws BaseException {
		this.getScoEtapaModPacON().alterarEtapaModPac(etapaModPac);
	}

	@Override
	public void excluirEtapaModPac(ScoEtapaModPac etapaModPac)
			throws BaseException {
		this.getScoEtapaModPacDAO().removerPorId(etapaModPac.getCodigo());
	}
	
	public ScoEtapaModPacDAO getScoEtapaModPacDAO() {
		return scoEtapaModPacDAO;
	}
	
	public ScoEtapaModPacON getScoEtapaModPacON() {
		return scoEtapaModPacON;
	}
	
	@Override
	public  List<ScoFornRamoComercial> pesquisarScoRamosComerciaisPorFornecedor(VScoFornecedor fornRamo, int firstResult, int maxResults,
			String orderProperty, boolean asc) {
		return getScoFornRamoComercialDAO().pesquisarScoRamosComerciaisPorFornecedor(fornRamo, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public  ScoFornRamoComercial obterFornRamoComercia(ScoFornRamoComercialId id)  {
		return getScoFornRamoComercialDAO().obterOriginal(id);
	}
	
	@Override
	public  ScoFornRamoComercial obterFornRamoComerciaNumeroCodigo(Integer frnNumero,Short rcmCodigo)  {
		return getScoFornRamoComercialDAO().pesquisarScoFornRamoComerciailPorForneCodigo(frnNumero, rcmCodigo);
	}
	
	@Override	
	public void excluirScoFornRamoComercial(final ScoFornRamoComercial fornRamoComercial) throws ApplicationBusinessException
	{
		this.getScoFornRamoComercialRN().excluir(fornRamoComercial);
	}
	
	@Override	
	public void cadastrarScoFornRamoComercial(final ScoFornRamoComercial fornRamoComercial) throws BaseException
	{
		this.getScoFornRamoComercialRN().inserir(fornRamoComercial);
	}
	
	@Override
	public  Long pesquisarScoRamosComerciaisPorFornecedorCount(VScoFornecedor fornRamo) {
		return getScoFornRamoComercialDAO().pesquisarScoRamosComerciaisPorFornecedorCount(fornRamo);
	}
	
	private ScoFornRamoComercialRN getScoFornRamoComercialRN() {
		return scoFornRamoComercialRN;
	}
	
	private ScoFornRamoComercialDAO getScoFornRamoComercialDAO() {
		return scoFornRamoComercialDAO;
	}
	
	
	public ScoEtapaModPac obterEtapaModPac(Integer chavePrimaria){
		return this.getScoEtapaModPacDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcessoPorCodigoOuDescricaoOrderByDescricao(
			Object filter, Boolean indAtivo) {
		return  this.getScoLocalizacaoProcessoDAO().pesquisarLocalizacaoProcessoPorCodigoOuDescricaoOrderDescricao(filter, indAtivo);
	}

	@Override
	public Long pesquisarLocalizacaoProcessoPorCodigoOuDescricaoCount(
			Object filter, Boolean indAtivo) {
        return this.getScoLocalizacaoProcessoDAO().pesquisarLocalizacaoProcessoPorCodigoOuDescricaoCount(filter, indAtivo);
	}

	@Override
	public Long pesquisarModalidadesCount(String pesquisa) {
		return getScoModalidadeLicitacaoDAO().listarModalidadeLicitacaoAtivasCount(pesquisa);
	}
	
	@Override
	public ScoCaracteristicaUsuarioCentroCusto obterCaracteristica(RapServidores servidor, DominioCaracteristicaCentroCusto carac) {
		return scoCaracteristicaUsuarioCentroCustoDAO.obterCaracteristica(servidor, carac);
		
	}
}