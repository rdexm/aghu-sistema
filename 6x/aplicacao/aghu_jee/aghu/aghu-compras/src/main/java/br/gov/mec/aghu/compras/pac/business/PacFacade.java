package br.gov.mec.aghu.compras.pac.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoAcoesPontoParadaDAO;
import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPgtoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFormaPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoParecerDAO;
import br.gov.mec.aghu.compras.dao.ScoLocalizacaoProcessoDAO;
import br.gov.mec.aghu.compras.dao.ScoLoteLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoCompraServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoDeCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoTempoAndtPacDAO;
import br.gov.mec.aghu.compras.dao.VScoItensLicitacaoDAO;
import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoQuadroAprovacaoVO;
import br.gov.mec.aghu.compras.pac.vo.ItemPropostaAFVO;
import br.gov.mec.aghu.compras.pac.vo.LicitacoesLiberarCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoVO;
import br.gov.mec.aghu.compras.pac.vo.PreItemPacVO;
import br.gov.mec.aghu.compras.pac.vo.RelatorioQuadroPropostasLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.VisualizarExtratoJulgamentoLicitacaoVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraDataVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraVO;
import br.gov.mec.aghu.compras.vo.DupItensPACVO;
import br.gov.mec.aghu.compras.vo.EstatisticaPacVO;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.compras.vo.PACsPendetesVO;
import br.gov.mec.aghu.compras.vo.PropFornecAvalParecerVO;
import br.gov.mec.aghu.compras.vo.RelatorioEspelhoPACVO;
import br.gov.mec.aghu.compras.vo.RelatorioResumoVerbaGrupoVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioAgrupadorItemFornecedorMarca;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioSimNaoTodos;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.dominio.DominioTipoDuplicacaoPAC;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.dominio.DominioVisaoExtratoJulgamento;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacaoId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVO;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVOPai;
import br.gov.mec.aghu.suprimentos.vo.ScoCondicaoPgtoLicitacaoVO;
import br.gov.mec.aghu.suprimentos.vo.ScoFaseSolicitacaoVO;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPacVO;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;
import br.gov.mec.aghu.suprimentos.vo.ScoLocalizacaoProcessoComprasVO;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;

@Modulo(ModuloEnum.COMPRAS)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class PacFacade extends BaseFacade implements IPacFacade {

	@EJB
	private RelatorioResumoVerbaGrupoON relatorioResumoVerbaGrupoON;
	@EJB
	private ScoItemLicitacaoRN scoItemLicitacaoRN;
	@EJB
	private DadosItemLicitacaoON dadosItemLicitacaoON;
	@EJB
	private PreItemPacVoON preItemPacVoON;
	@EJB
	private ScoItemLicitacaoON scoItemLicitacaoON;
	@EJB
	private RelatorioItensPACON relatorioItensPACON;
	@EJB
	private ScoLoteLicitacaoON scoLoteLicitacaoON;
	@EJB
	private ScoLoteLicitacaoRN scoLoteLicitacaoRN;
	@EJB
	private ScoPropostaFornecedorON scoPropostaFornecedorON;
	@EJB
	private DuplicaPACON duplicaPACON;
	@EJB
	private ScoCondicaoPgtoLicitacaoRN scoCondicaoPgtoLicitacaoRN;
	@EJB
	private ScoLicitacaoRN scoLicitacaoRN;
	@EJB
	private ScoLocalizacaoProcessoCompraRN scoLocalizacaoProcessoCompraRN;
	@EJB
	private RelatorioQuadroPropostasProvisisorioON relatorioQuadroPropostasProvisisorioON;
	@EJB
	private RelatorioEspelhoPACON relatorioEspelhoPACON;
	@EJB
	private ScoCondicaoPgtoLicitacaoON scoCondicaoPgtoLicitacaoON;
	@EJB
	private ScoLicitacaoON scoLicitacaoON;
	@EJB
	private RegistraJulgamentoPacON registraJulgamentoPacON;
	@EJB
	private VisualizarExtratoJulgamentoLicitacaoRN visualizarExtratoJulgamentoLicitacaoRN;
	@EJB
	private ScoAndamentoProcessoCompraRN scoAndamentoProcessoCompraRN;
	@EJB
	private RelatorioUltimasComprasPACON relatorioUltimasComprasPACON;
	@EJB
	private RelatorioQuadroJulgamentoPropostasON relatorioQuadroJulgamentoPropostasON;
	@EJB
	private ScoItemPropostaFornecedorON scoItemPropostaFornecedorON;
	@EJB
	private ScoItemPropostaFornecedorRN scoItemPropostaFornecedorRN;
	@EJB
	private PregaoBancoBrasilRN pregaobancoBrasilRN;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private AtualizarSituacaoEtapasPACRN atualizarSituacaoEtapasPACRN;

	@Inject
	private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;

	@Inject
	private ScoFormaPagamentoDAO scoFormaPagamentoDAO;

	@Inject
	private ScoLoteLicitacaoDAO scoLoteLicitacaoDAO;

	@Inject
	private ScoSolicitacaoCompraServicoDAO scoSolicitacaoCompraServicoDAO;

	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;

	@Inject
	private ScoTempoAndtPacDAO scoTempoAndtPacDAO;

	@Inject
	private ScoAndamentoProcessoCompraDAO scoAndamentoProcessoCompraDAO;

	@Inject
	private ScoServicoDAO scoServicoDAO;

	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

	@Inject
	private ScoAcoesPontoParadaDAO scoAcoesPontoParadaDAO;

	@Inject
	private ScoCondicaoPgtoLicitacaoDAO scoCondicaoPgtoLicitacaoDAO;

	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;

	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;

	@Inject
	private ScoLocalizacaoProcessoDAO scoLocalizacaoProcessoDAO;

	@Inject
	private ScoLicitacaoParecerDAO scoLicitacaoParecerDAO;

	@Inject
	private ScoMaterialDAO scoMaterialDAO;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	@Inject
	private VScoItensLicitacaoDAO vScoItensLicitacaoDAO;

	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@EJB
	private UltimasComprasRN ultimasComprasRN;
	
	@EJB
	private CotarPrecoRN cotarPrecoRN;
	
	@EJB
	private ConsultarAndamentoProcessoCompraRN consultarAndamentoProcessoCompraRN;
	
	@Inject
	private ScoSolicitacaoDeCompraDAO scoSolicitacaoDeCompraDAO;
	
	
	private static final long serialVersionUID = -5875255349708260627L;

	@Override
	public Long countItemLicitacaoPorNumeroPac(Integer numeroPac) {
		return this.getScoItemLicitacaoDAO().countItemLicitacaoPorNumeroLicitacao(numeroPac);
	}

	@Override
	public List<ScoItemLicitacao> pesquisarItemLicitacaoPorNumeroPac(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer numeroPac) {
		return scoItemLicitacaoDAO.pesquisarItemLicitacaoPorNumeroLicitacao(firstResult, maxResults, orderProperty, asc, numeroPac);
	}

	@Override
	public ScoItemLicitacao obterItemLicitacaoPorNumeroLicitacaoENumeroItem(Integer numeroLCT, Short numeroItem) {
		return this.getScoItemLicitacaoDAO().obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLCT, numeroItem);
	}

	@Override
	public Long contarPacLicitacao(Integer numero) {
		return getScoAndamentoProcessoCompraDAO().contarPacLicitacao(numero);
	}

	@Override
	public List<ScoAndamentoProcessoCompra> pesquisarPacLicitacao(Integer numero, Integer first, Integer max, String order, boolean asc) {
		return getScoAndamentoProcessoCompraDAO().pesquisarPacLicitacao(numero, first, max, order, asc);
	}

	@Override
	public List<ScoAndamentoProcessoCompra> pesquisarPacLicitacao(Integer numeroPAC) {
		return getScoAndamentoProcessoCompraDAO().pesquisarPacLicitacao(numeroPAC);
	}	
	
	@Override	
	public ScoAndamentoProcessoCompra obterAndamentoPac(Integer seq) {
		return getScoAndamentoProcessoCompraDAO().obterAndamentoPac(seq);
	}

	@Override
	public void excluir(Integer seq) throws ApplicationBusinessException {
		getScoAndamentoProcessoCompraRN().excluir(seq);
	}

	@Override	
	public void alterar(ScoAndamentoProcessoCompra andamento) {
		getScoAndamentoProcessoCompraDAO().atualizar(andamento);
	}

	@Override
	public void incluir(ScoAndamentoProcessoCompra andamento) throws ApplicationBusinessException {
		scoAndamentoProcessoCompraRN.incluir(andamento);
	}

	@Override	
	public List<ScoLocalizacaoProcesso> pesquisarScoLocalizacaoProcesso(Object filtro, Integer max) {
		return getScoLocalizacaoProcessoDAO().pesquisarScoLocalizacaoProcesso(filtro, max);
	}

	@Override
	public Short obterMaxDiasPermAndamentoPac(String modalidade, Short localizacao) {
		return getScoTempoAndtPacDAO().obterMaxDiasPermAndamentoPac(modalidade, localizacao);
	}

	@Override
	public List<ScoCondicaoPgtoLicitacaoVO> obterCondicaoPgtoPac(Integer numeroPac, Short numeroItem, Integer first, Integer max,
			String order, boolean asc) {
		return getScoCondicaoPgtoLicitacaoON().obterCondicaoPgtoPac(numeroPac, numeroItem, first, max, order, asc);
	}

	@Override
	public void copiarCondicaoPagamentoLicitacao(Integer pfrLctNumero, Integer pfrFrnNumero, Short numeroItem, Short numeroItemLicitacao)
			throws ApplicationBusinessException {
		this.getScoItemPropostaFornecedorON().copiarCondicaoPagamentoLicitacao(pfrLctNumero, pfrFrnNumero, numeroItem, numeroItemLicitacao);
	}

	@Override
	public ScoCondicaoPgtoLicitacao obterCondicaoPagamentoPorChavePrimaria(Integer seqCondicao) {
		return getScoCondicaoPgtoLicitacaoDAO().obterPorChavePrimaria(seqCondicao);
	}
	
	@Override
	public ScoCondicaoPgtoLicitacao buscarCondicaoPagamentoPK(Integer seqCondicaoPgto){
		return getScoCondicaoPgtoLicitacaoDAO().buscarCondicaoPagamentoPK(seqCondicaoPgto);
	}

	@Override
	public void registrarJulgamentoPac(ScoItemPropostaFornecedor itemProposta, ScoFaseSolicitacaoVO faseSolicitacao,
			ScoCondicaoPagamentoPropos condicaoPagamentoEscolhida, DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento,
			DominioSituacaoJulgamento pendentePor, ScoCriterioEscolhaProposta criterioEscolha,
			DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao) throws BaseException {
		this.getScoLicitacaoON().registrarJulgamentoPac(itemProposta, faseSolicitacao, condicaoPagamentoEscolhida, motivoCancelamento,
				pendentePor, criterioEscolha, motivoDesclassificacao, servidorLogadoFacade.obterServidorLogado());
	}

	@Override
	public Integer obterProxNumCondicaoPagamento(Integer numeroLicitacao, Short numeroItem) {
		return getScoCondicaoPgtoLicitacaoDAO().obterProxNumCondicaoPagamento(numeroLicitacao, numeroItem);
	}

	@Override
	public List<ScoFormaPagamento> listarFormasPagamento(Object pesquisa) {
		return getScoFormaPagamentoDAO().listarFormasPagamento(pesquisa);
	}

	@Override
	public List<ScoParcelasPagamento> obterParcelasPagamento(Integer seqCondicaoPgto) {
		return getScoParcelasPagamentoDAO().obterParcelasPagamento(seqCondicaoPgto);
	}

	@Override
	public List<ScoParcelasPagamento> obterParcelasPgtoProposta(Integer numeroProposta) {
		return this.getScoParcelasPagamentoDAO().obterParcelasPgtoProposta(numeroProposta);
	}

	@Override
	public Boolean verificarItemPacPossuiPropostaEscolhida(Integer numLicitacao, Short numero) {
		return this.getScoItemPropostaFornecedorDAO().verificarItemPacPossuiPropostaEscolhida(numLicitacao, numero);
	}

	@Override
	public Long obterCondicaoPgtoPacCount(Integer numeroLicitacao, Short numeroItem) {
		return getScoCondicaoPgtoLicitacaoON().obterCondicaoPgtoPacCount(numeroLicitacao, numeroItem);
	}

	@Override
	public boolean permitirNovaCondicaoPgto(Integer numeroLicitacao, Short numeroItem) {
		return getScoCondicaoPgtoLicitacaoON().permitirNovaCondicaoPgto(numeroLicitacao, numeroItem);
	}

	@Override
	public boolean verificarParamUnicaCondicaoPgto() {
		return getScoCondicaoPgtoLicitacaoON().verificarParamUnicaCondicaoPgto();
	}

	@Override
	public void gravarCondicaoPagtoParcelas(ScoCondicaoPgtoLicitacao condicaoPagamento, List<ScoParcelasPagamento> listaParcelas,
			List<ScoParcelasPagamento> listaParcelasExcluidas) throws ApplicationBusinessException {
		getScoCondicaoPgtoLicitacaoON().gravarCondicaoPagtoParcelas(condicaoPagamento, listaParcelas, listaParcelasExcluidas);
	}

	@Override
	public ScoItemPacVO montarItemObjetoVO(ScoItemLicitacao item) {
		return this.getDadosItemLicitacaoON().montarItemObjetoVO(item);
	}

	@Override
	public Boolean validarEmbalagemProposta(String unidadeSolicitada, ScoUnidadeMedida embalagem) {
		return getDadosItemLicitacaoON().validarEmbalagemProposta(unidadeSolicitada, embalagem);
	}

	@Override
	public void excluirCondicaoPgto(Integer seqCondicaoPgto) {
		getScoCondicaoPgtoLicitacaoON().excluirCondicaoPgto(seqCondicaoPgto);
	}

	@Override
	public Boolean verificarPermissoesPac(String login, Boolean gravar) {
		return this.getScoLicitacaoRN().verificarPermissoesPac(login, gravar);
	}

	private ScoCondicaoPgtoLicitacaoDAO getScoCondicaoPgtoLicitacaoDAO() {
		return scoCondicaoPgtoLicitacaoDAO;
	}

	private ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}

	private ScoCondicaoPgtoLicitacaoON getScoCondicaoPgtoLicitacaoON() {
		return scoCondicaoPgtoLicitacaoON;
	}

	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}

	private ScoLicitacaoParecerDAO getScoLicitacaoParecerDAO() {
		return scoLicitacaoParecerDAO;
	}

	@Override
	public List<ScoLicitacao> pesquisarLicitacaoParecerTecnico(Integer firstResult, Integer maxResult, String order, boolean asc,
			Integer numeroPac, String descricaoPac, ScoModalidadeLicitacao modalidade, Boolean vencida) {
		return this.getScoLicitacaoParecerDAO().pesquisarLicitacaoParecerTecnico(firstResult, maxResult, order, asc, numeroPac,
				descricaoPac, modalidade, vencida);
	}

	@Override
	public Long contarLicitacaoParecerTecnico(Integer numeroPac, String descricaoPac, ScoModalidadeLicitacao modalidade, Boolean vencida) {
		return this.getScoLicitacaoParecerDAO().contarLicitacaoParecerTecnico(numeroPac, descricaoPac, modalidade, vencida);
	}

	@Override
	public String obterParecerTecnicoItemProposta(ScoItemPropostaFornecedor item) {
		return this.getScoItemPropostaFornecedorON().obterParecerTecnicoItemProposta(item);
	}

	/**
	 * Obtém licitação pelo Id
	 * 
	 * @param numero
	 * @return
	 */
	@Override
	public ScoLicitacao obterLicitacao(Integer numero) {
		return getScoLicitacaoDAO().obterLicitacaoModalidadePorNumeroPac(numero);
	}

	@Override
	public List<ScoLicitacao> listarLicitacoesAtivas(Object pesquisa, DominioModalidadeEmpenho modemp) {
		return this.getScoLicitacaoDAO().listarLicitacoesAtivas(pesquisa, modemp);
	}

	@Override
	public Integer obterNumeroSolicitacao(ScoItemLicitacao item) {
		return this.getDadosItemLicitacaoON().obterNumeroSolicitacao(item);
	}

	@Override
	public ScoUnidadeMedida obterUnidadeMedidaSs() {
		return this.getScoItemPropostaFornecedorON().obterUnidadeMedidaSs();
	}

	@Override
	public List<ScoItemPropostaFornecedor> pesquisarItemPropostaPorNumeroLicitacaoENumeroItem(Integer numeroPac, Short numeroItem) {
		return this.getScoItemPropostaFornecedorDAO().pesquisarItemPropostaPorNumeroLicitacaoENumeroItem(numeroPac, numeroItem);
	}

	@Override
	/**
	 * Remove o item passado como paramaetro da lista tambem passada por parametro
	 * @param item
	 * @param list
	 */
	public void removerItemLista(ScoItemPacVO item, List<ScoItemPacVO> list) {
		this.getDadosItemLicitacaoON().removerItemLista(item, list);
	}

	@Override
	public ScoCondicaoPagamentoPropos obterCondicaoPagamentoPropostaPorNumero(Integer numeroCondicaoPagamento) {
		return this.getScoCondicaoPagamentoProposDAO().obterCondicaoPagamentoPropostaPorNumero(numeroCondicaoPagamento);
	}

	/**
	 * Retorna lista de ScoLicitacao de acordo com o parametro informado
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	public List<ScoLicitacao> pesquisarLicitacoesPorNumeroDescricao(Object parametro) {
		return getScoLicitacaoDAO().pesquisarLicitacoesPorNumeroDescricao(parametro);
	}

	/**
	 * Altera uma licitação passando pelas regras de trigger de alteração.
	 * 
	 * @param licitacao
	 *            , licitacaoClone.
	 * @return
	 */
	public void alterarLicitacao(ScoLicitacao licitacao, ScoLicitacao licitacaoClone) throws ApplicationBusinessException {
		this.getScoLicitacaoRN().alterarLicitacao(licitacao, licitacaoClone);

	}

	/**
	 * Insere uma licitação passando pelas regras de trigger de inserção.
	 * 
	 * @param licitacao
	 *            .
	 * @return
	 */
	public void inserirLicitacao(ScoLicitacao licitacao) throws BaseException {
		this.getScoLicitacaoRN().inserirLicitacao(licitacao);

	}

	@Override
	public void atualizarItemLicitacao(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException {
		this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);
	}

	protected ScoLocalizacaoProcessoDAO getScoLocalizacaoProcessoDAO() {
		return scoLocalizacaoProcessoDAO;
	}

	protected ScoAndamentoProcessoCompraRN getScoAndamentoProcessoCompraRN() {
		return scoAndamentoProcessoCompraRN;
	}

	protected ScoItemLicitacaoRN getScoItemLicitacaoRN() {
		return scoItemLicitacaoRN;
	}

	protected ScoAndamentoProcessoCompraDAO getScoAndamentoProcessoCompraDAO() {
		return scoAndamentoProcessoCompraDAO;
	}

	protected ScoCondicaoPagamentoProposDAO getScoCondicaoPagamentoProposDAO() {
		return scoCondicaoPagamentoProposDAO;
	}

	@Override
	public List<ScoCondicaoPagamentoPropos> pesquisarCondicaoPagamentoProposta(Integer numeroFornecedor, Integer numeroPac, Short numeroItem) {
		return this.getScoCondicaoPagamentoProposDAO().pesquisarCondicaoPagamentoProposta(numeroFornecedor, numeroPac, numeroItem);
	}

	@Override
	public Long pesquisarLicitacoesLiberarCount(LicitacoesLiberarCriteriaVO criteria) {
		return getScoLicitacaoDAO().pesquisarLicitacoesLiberarCount(criteria);
	}

	@Override
	public List<ScoLicitacao> pesquisarLicitacoesLiberar(LicitacoesLiberarCriteriaVO criteria, Integer firstResult, Integer maxResult,
			String order, boolean asc) {
		return getScoLicitacaoDAO().pesquisarLicitacoesLiberar(criteria, firstResult, maxResult, order, asc);
	}

	@Secure("#{s:hasPermission('consultarPAC','visualizar')}")
	public Long listarProcessosAdmCompraCount(ScoLicitacao licitacao, Date dataInicioGer, Date dataFimGer)
			throws ApplicationBusinessException {

		return getScoLicitacaoDAO().listarProcessosAdmCompraCount(licitacao, dataInicioGer, dataFimGer);

	}

	@Secure("#{s:hasPermission('consultarPAC','visualizar')}")
	public List<ScoLicitacao> listarProcessosAdmCompra(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			ScoLicitacao licitacao, Date dataInicioGer, Date dataFimGer) throws ApplicationBusinessException {

		return scoLicitacaoDAO.listarProcessosAdmCompra(firstResult, maxResults, orderProperty, asc, licitacao, dataInicioGer, dataFimGer);

	}

	@Override
	@Secure("#{s:hasPermission('cadastrarProposta','gravar')}")
	public void gravarItemProposta(List<ScoItemPropostaVO> listaItensPropostas, List<ScoItemPropostaVO> listaItensPropostasExclusao,
			Boolean propostaEmEdicao) throws ApplicationBusinessException {
		this.getScoItemPropostaFornecedorON().gravarItemProposta(listaItensPropostas, listaItensPropostasExclusao, propostaEmEdicao);
	}

	@Override
	public void registrarJulgamentoPacLote(List<ScoItemPropostaVO> listaItensProposta,
			ScoCondicaoPagamentoPropos condicaoPagamentoEscolhida, ScoCriterioEscolhaProposta criterioEscolha)
			throws ApplicationBusinessException {
		this.getRegistraJulgamentoPacON().registrarJulgamentoPacLote(listaItensProposta, condicaoPagamentoEscolhida, criterioEscolha);
	}

	@Override
	public Short obterProximoNumeroItemPropostaFornecedor(List<ScoItemPropostaVO> listaItensPropostas,
			List<ScoItemPropostaVO> listaItensPropostasExclusao, Integer numeroPac, Integer numeroFornecedor) {
		return this.getScoItemPropostaFornecedorON().obterProximoNumeroItemPropostaFornecedor(listaItensPropostas,
				listaItensPropostasExclusao, numeroPac, numeroFornecedor);
	}

	@Override
	public Boolean validarQuantidadePropostaDiferenteSolicitacao(Long qtdItemProposta, Integer fatorConversao, Integer qtdItemSolicitacao) {
		return this.getScoItemPropostaFornecedorON().validarQuantidadePropostaDiferenteSolicitacao(qtdItemProposta, fatorConversao,
				qtdItemSolicitacao);
	}

	@Override
	public void validarInsercaoItemPropostaValorUnitario(BigDecimal valorUnitarioItemProposta) throws ApplicationBusinessException {
		this.getScoItemPropostaFornecedorON().validarInsercaoItemPropostaValorUnitario(valorUnitarioItemProposta);
	}

	@Override
	public Boolean validarFatorConversao(ScoFaseSolicitacaoVO faseSolicitacao, Integer fatorConversao, ScoUnidadeMedida embalagem) {
		return this.getScoItemPropostaFornecedorON().validarFatorConversao(faseSolicitacao, fatorConversao, embalagem);
	}

	@Override
	public void validarInsercaoItemPropostaDuplicado(List<ScoItemPropostaVO> listaItensPropostas, ScoFaseSolicitacaoVO faseSolicitacao,
			ScoMarcaComercial marcaComercial, ScoFornecedor scoFornecedor) throws ApplicationBusinessException {
		this.getScoItemPropostaFornecedorON().validarInsercaoItemPropostaDuplicado(listaItensPropostas, faseSolicitacao, marcaComercial,
				scoFornecedor);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarProposta','gravar')}")
	public void encaminharParecerTecnico(ScoLicitacao licitacao) {
		getScoLicitacaoON().encaminharParecerTecnico(licitacao);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarProposta','gravar')}")
	public void encaminharComissao(ScoLicitacao pac) {
		getScoLicitacaoON().encaminharComissao(pac);
	}

	@Override
	@Secure("#{s:hasPermission('consultarProposta','visualizar')}")
	public Long contarPropostas(ScoLicitacao licitacao, String fornecedor) {
		return getScoPropostaFornecedorDAO().contarPropostas(licitacao, fornecedor);
	}

	private ScoItemPropostaFornecedorRN getScoItemPropostaFornecedorRN() {
		return scoItemPropostaFornecedorRN;
	}

	@Override
	public void inserirItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		this.getScoItemPropostaFornecedorRN().inserirItemPropostaFornecedor(itemProposta);
	}

	@Override
	public void atualizarItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itemProposta);
	}

	@Override
	@Secure("#{s:hasPermission('consultarProposta','visualizar')}")
	public List<ScoPropostaFornecedor> pesquisarPropostas(ScoLicitacao licitacao, String fornecedor, Integer first, Integer max,
			String order, boolean asc) {
		return getScoPropostaFornecedorDAO().pesquisarPropostas(licitacao, fornecedor, first, max, order, asc);
	}

	@Override
	public Boolean verificarItemPropostaFornecedorEmAf(Integer numeroPac, Short numeroItem, ScoFornecedor fornecedor) {
		return getScoItemAutorizacaoFornDAO().verificarItemPropostaFornecedorEmAf(numeroPac, numeroItem, fornecedor);
	}

	/**
	 * Exclui proposta.
	 */
	@Override
	@Secure("#{s:hasPermission('cadastrarProposta','gravar')}")
	public void excluir(ScoPropostaFornecedorId id) {
		getScoPropostaFornecedorDAO().removerPorId(id);
	}

	/**
	 * Altera ou insere uma licitação passando pelas regras da estória de #5195
	 * Gerar Pac
	 * 
	 * @param licitacao
	 *            , licitacaoClone.
	 * @return
	 */
	@Secure("#{s:hasPermission('cadastrarPAC','gravar')}")
	public void persistirPac(ScoLicitacao licitacao, ScoLicitacao licitacaoClone) throws BaseException {
		getScoLicitacaoON().persistirLicitacao(licitacao, licitacaoClone);
	}

	public void validaFrequenciaCompras(ScoLicitacao licitacao) throws ApplicationBusinessException {

		getScoLicitacaoON().validaFrequenciaCompras(licitacao);

	}

	private ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}

	private ScoPropostaFornecedorDAO getScoPropostaFornecedorDAO() {
		return scoPropostaFornecedorDAO;
	}

	private ScoFormaPagamentoDAO getScoFormaPagamentoDAO() {
		return scoFormaPagamentoDAO;
	}

	protected ScoLicitacaoON getScoLicitacaoON() {
		return scoLicitacaoON;
	}

	protected ScoLicitacaoRN getScoLicitacaoRN() {
		return scoLicitacaoRN;
	}

	private ScoItemLicitacaoON getScoItemLicitacaoON() {
		return scoItemLicitacaoON;
	}

	private PreItemPacVoON getPreItemPacVoON() {
		return preItemPacVoON;
	}

	private DadosItemLicitacaoON getDadosItemLicitacaoON() {
		return dadosItemLicitacaoON;
	}

	@Override
	public String obterUnidadeMaterial(ScoItemLicitacao item) {
		return this.getDadosItemLicitacaoON().obterUnidadeMaterial(item);
	}

	@Override
	public String obterComplementoAutorizacaoFornecimento(ScoItemLicitacao item) {
		return this.getDadosItemLicitacaoON().obterComplementoAutorizacaoFornecimento(item);
	}

	@Override
	public DominioTipoSolicitacao obterTipoSolicitacao(ScoItemLicitacao item) {
		return this.getDadosItemLicitacaoON().obterTipoSolicitacao(item);
	}

	@Override
	public String obterDescricaoSolicitacao(ScoItemLicitacao item) {
		return this.getDadosItemLicitacaoON().obterDescricaoSolicitacao(item);
	}

	@Override
	public String obterNomeMaterialServico(ScoItemLicitacao item, Boolean concatenarCodigo) {
		return this.getDadosItemLicitacaoON().obterNomeMaterialServico(item, concatenarCodigo);
	}

	@Override
	public Integer obterQuantidadeMaterialServico(ScoItemLicitacao item) {
		return this.getDadosItemLicitacaoON().obterQuantidadeMaterialServico(item);
	}

	@Override
	public String obterDescricaoMaterialServico(ScoItemLicitacao item) {
		return this.getDadosItemLicitacaoON().obterDescricaoMaterialServico(item);
	}

	@Override
	public BigDecimal obterValorTotalPorNumeroLicitacao(Integer numeroLicitacao) {
		return this.getDadosItemLicitacaoON().obterValorTotalPorNumeroLicitacao(numeroLicitacao);
	}

	@Override
	public BigDecimal obterValorTotalItemPac(Integer numeroLicitacao, Short numeroItem) {
		return this.getDadosItemLicitacaoON().obterValorTotalItemPac(numeroLicitacao, numeroItem);
	}

	@Override
	public List<ScoItemPropostaVO> pesquisarPropostaFornecedorParaJulgamentoLote(Integer numeroPac, ScoFornecedor fornecedor,
			DominioSimNaoTodos julgados) {
		return registraJulgamentoPacON.pesquisarPropostaFornecedorParaJulgamentoLote(numeroPac, fornecedor, julgados);
	}

	private RegistraJulgamentoPacON getRegistraJulgamentoPacON() {
		return registraJulgamentoPacON;
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarPAC','gravar')}")
	public void excluirItemPac(Integer numeroLicitacao, Short numeroItem, String motivoExclusao, Boolean indExcluido) throws BaseException {
		this.getScoItemLicitacaoON()
				.excluirItemPac(numeroLicitacao, numeroItem, motivoExclusao,indExcluido);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarPAC','gravar')}")
	public void reativarItemPac(Integer numeroLicitacao, Short numeroItem) throws BaseException {
		this.getScoItemLicitacaoON().reativarItemPac(numeroLicitacao, numeroItem);
	}

	@Override
	public Boolean verificarLicitacaoProposta(Integer numLicitacao, Short numero, Boolean propostaEscolhida) {
		return this.getScoItemLicitacaoON().verificarLicitacaoProposta(numLicitacao, numero, propostaEscolhida);
	}

	@Override
	public Boolean verificarEdicaoItensPac(Integer numeroLicitacao, Boolean validarProposta, Boolean validarPublicacao) {
		return this.getScoItemLicitacaoON().verificarEdicaoItensPac(numeroLicitacao, validarProposta, validarPublicacao);
	}
	
	@Override
	public Boolean verificarEdicaoItensPacPropostaLote(Integer numeroLicitacao, Short numero) {
		return this.getScoItemLicitacaoON().verificarEdicaoItensPacPropostaLote(numeroLicitacao,numero);
	}

	@Override
	public ScoPontoParadaSolicitacao obterPontoParadaAnteriorItemLicitacao(ScoItemLicitacao itemLicitacao) {
		return this.getScoItemLicitacaoON().obterPontoParadaAnteriorItemLicitacao(itemLicitacao);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarPAC','gravar')}")
	public void gravarAlteracoesItensPac(Integer numeroLicitacao, List<ScoItemPacVO> listaOriginal, List<ScoItemPacVO> listAlteracoes)
			throws BaseException {
		this.getScoItemLicitacaoON().gravarAlteracoesItensPac(numeroLicitacao, listaOriginal, listAlteracoes);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarPAC','gravar')}")
	public void reordenarItensPac(Integer numeroLicitacao, List<ScoItemPacVO> listaGradeAtual) throws BaseException {
		this.getScoItemLicitacaoON().reordenarItensPac(numeroLicitacao, listaGradeAtual);
	}

	@Override	
	public Long contarItensEmAf(ScoPropostaFornecedor proposta) {
		return getScoItemPropostaFornecedorDAO().contarItensEmAf(proposta);
	}

	@Secure("#{s:hasPermission('cadastrarPAC','gravar')}")
	public List<PreItemPacVO> preSelecionarItensPac(ScoPontoParadaSolicitacao caixa, RapServidores comprador, ScoServico servico,
			ScoMaterial material) throws ApplicationBusinessException, ApplicationBusinessException {
		return this.getPreItemPacVoON().preSelecionarItensPac(caixa, comprador, servico, material);
	}

	//@Secure("#{s:hasPermission('cadastrarPac','gravar')}")
	public List<PreItemPacVO> pesquisarPreItensPac(DominioTipoSolicitacao tipoSolicitacao, Integer numeroIni, Integer numeroFim)
			throws ApplicationBusinessException, ApplicationBusinessException {
		return this.getPreItemPacVoON().adicionarItensPacPorNumero(tipoSolicitacao, numeroIni, numeroFim);
	}

	@Secure("#{s:hasPermission('cadastrarPAC','gravar')}")
	// Retorna com a lista das SC associadas
	public List<PreItemPacVO> listaAssociadasSSItensPac(Integer numeroIni, Integer numeroFim) throws ApplicationBusinessException,
			ApplicationBusinessException {
		return this.getScoSolicitacaoCompraServicoDAO().listaAssociadasSSItensPac(numeroIni, numeroFim);
	}

	@Secure("#{s:hasPermission('cadastrarPAC','gravar')}")
	// Retorna com a lista das SS associadas
	public List<PreItemPacVO> listaAssociadasSCItensPac(Integer numeroIni, Integer numeroFim) throws ApplicationBusinessException,
			ApplicationBusinessException {
		return this.getScoSolicitacaoCompraServicoDAO().listaAssociadasSCItensPac(numeroIni, numeroFim);
	}

	@Override
	public ScoPropostaFornecedor obterPropostaFornecedor(ScoPropostaFornecedorId idProposta) {
		Enum[] fetch = { ScoPropostaFornecedor.Fields.LICITACAO };
		return getScoPropostaFornecedorDAO().obterPorChavePrimaria(idProposta, null, fetch);
	}

	@Override
	public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(Object param, Integer numeroPac) {
		return this.getScoItemPropostaFornecedorON().pesquisarItemLicitacao(param, numeroPac);
	}

	@Override
	public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(Object param, Integer numeroPac, List<ScoItemPropostaVO> listaItensPropostas)
			throws ApplicationBusinessException {
		return this.getScoItemPropostaFornecedorON().pesquisarItemLicitacao(param, numeroPac, listaItensPropostas);
	}

	@Override
	public BigDecimal obterValorTotalProposta(ScoPropostaFornecedor propostaFornecedor) {
		return this.getScoItemPropostaFornecedorON().obterValorTotalProposta(propostaFornecedor);
	}

	@Override
	public BigDecimal obterValorTotalItemProposta(ScoItemPropostaFornecedor itemProposta) {
		return this.getScoItemPropostaFornecedorON().obterValorTotalItemProposta(itemProposta);
	}

	@Override
	public List<ScoItemPropostaVO> pesquisarItemPropostaPorNumeroLicitacao(Integer numeroPac, ScoFornecedor fornecedor) {
		return this.getScoItemPropostaFornecedorON().pesquisarItemPropostaPorNumeroLicitacao(numeroPac, fornecedor);
	}

	private ScoItemPropostaFornecedorON getScoItemPropostaFornecedorON() {
		return scoItemPropostaFornecedorON;
	}

	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	// # 5473 - RELATÓRIO ITENS DA LICITAÇÃO
	private RelatorioItensPACON getRelatorioItensPACON() {
		return relatorioItensPACON;
	}

	@Override
	public List<ItensPACVO> pesquisarRelatorioItensPAC(Integer numero, boolean flagNaoExcluidas) throws ApplicationBusinessException,
			ApplicationBusinessException {
		return getRelatorioItensPACON().pesquisarRelatorioItensPAC(numero, flagNaoExcluidas);
	}

	/**
	 * Método que retorna dados para o relatório Espelho PAC
	 * 
	 * @param numLicitacao
	 * @param itemInicial
	 * @param itemFinal
	 * @return
	 */
	private RelatorioEspelhoPACON getRelatorioEspelhoPACON() {
		return relatorioEspelhoPACON;
	}

	@Override
	public Set<RelatorioEspelhoPACVO> gerarDadosRelatorioEspelhoPAC(final Integer numLicitacao) {
		return getRelatorioEspelhoPACON().gerarDadosRelatorioEspelhoPAC(numLicitacao);
	}

	private RelatorioUltimasComprasPACON getRelatorioUltimasComprasPACON() {
		return relatorioUltimasComprasPACON;
	}

	@Override
	public List<RelUltimasComprasPACVOPai> gerarRelatorioUltimasComprasPAC(final Integer numLicitacao, final List<Integer> itens,
			final List<String> itensModalidade, final Integer qtdeUltimasCompras, DominioAgrupadorItemFornecedorMarca agrupador) {
		return getRelatorioUltimasComprasPACON().gerarRelatorioUltimasComprasPAC(numLicitacao, itens, itensModalidade, qtdeUltimasCompras, agrupador);
	}

	private DuplicaPACON getDuplicaPACON() {
		return duplicaPACON;
	}

	private ScoSolicitacaoCompraServicoDAO getScoSolicitacaoCompraServicoDAO() {
		return scoSolicitacaoCompraServicoDAO;
	}

	@Override
	public List<DupItensPACVO> pesquisarItensPAC(Integer numero, DominioTipoDuplicacaoPAC tipoDuplicacaoPAC)
			throws ApplicationBusinessException {
		return getDuplicaPACON().pesquisarItensPAC(numero, tipoDuplicacaoPAC);
	}

	@Override
	public ScoLicitacao duplicarPAC(Integer numeroPAC, DominioTipoDuplicacaoPAC tipoDuplicacaoPAC, RapServidores servidorLogado,
			List<DupItensPACVO> itensPACVO) throws BaseException {
		return getDuplicaPACON().duplicarPAC(numeroPAC, tipoDuplicacaoPAC, servidorLogado, itensPACVO);
	}

	@Override
	public ScoFaseSolicitacaoVO obterFaseVOPorNumeroLicitacaoENumeroItemLicitacao(Integer numeroLCT, Short numeroItem) {
		return this.getScoFaseSolicitacaoDAO().obterFasesVOPorNumeroLicitacaoENumeroItemLicitacao(numeroLCT, numeroItem);
	}

	@Override
	public FcpMoeda obterMoedaPadraoItemProposta() throws ApplicationBusinessException {
		return this.getScoItemPropostaFornecedorON().obterMoedaPadraoItemProposta();
	}

	@Override
	public void persistirPropostaFornecedor(ScoPropostaFornecedor proposta, Boolean propostaEmEdicao) throws ApplicationBusinessException {
		this.getScoPropostaFornecedorON().persistirPropostaFornecedor(proposta, propostaEmEdicao);
	}

	@Override
	public List<RelatorioQuadroPropostasLicitacaoVO> pesquisarQuadroProvisorioItensPropostas(Set<Integer> listaNumPac, Short numeroInicial,
			Short numeroFinal, String listaItens) {
		return getRelatorioQuadroPropostasProvisisorioON().pesquisarQuadroProvisorioItensPropostas(listaNumPac, numeroInicial, numeroFinal,
				listaItens);
	}

	@Override
	public List<RelatorioQuadroPropostasLicitacaoVO> pesquisarQuadroJulgamentoPropostas(Set<Integer> listaNumPac, Short numeroItemInicial,
			Short numeroItemFinal, String listaItens) throws ApplicationBusinessException {
		return getRelatorioQuadroJulgamentoPropostasON().pesquisarQuadroJulgamentoItensPropostas(listaNumPac, numeroItemInicial,
				numeroItemFinal, listaItens);
	}

	public void persistirItemPac(ScoLicitacao pac, List<PreItemPacVO> listaItensPac) throws BaseException {

		this.getPreItemPacVoON().gravarItens(pac, listaItensPac);

	}

	protected RelatorioQuadroPropostasProvisisorioON getRelatorioQuadroPropostasProvisisorioON() {
		return relatorioQuadroPropostasProvisisorioON;
	}

	protected RelatorioQuadroJulgamentoPropostasON getRelatorioQuadroJulgamentoPropostasON() {
		return relatorioQuadroJulgamentoPropostasON;
	}

	protected ScoPropostaFornecedorON getScoPropostaFornecedorON() {
		return scoPropostaFornecedorON;
	}

	protected RelatorioResumoVerbaGrupoON getRelatorioResumoVerbaGrupoON() {
		return relatorioResumoVerbaGrupoON;
	}

	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}

	@Override
	@Secure("#{s:hasPermission('consultarPAC','visualizar')}")
	public RelatorioResumoVerbaGrupoVO obterDadosRelatorioVerbaGrupo(Integer numLicitacao) {
		return this.getRelatorioResumoVerbaGrupoON().obterRelatorioResumoVerbaGrupo(numLicitacao);
	}

	/** {@inheritDoc} */
	@Override
	@Secure("#{s:hasPermission('consultarPAC','visualizar')}")
	public Long contarPacsParaJulgamento(PacParaJulgamentoCriteriaVO criteria) {
		return getScoLicitacaoDAO().contarPacsParaJulgamento(criteria);
	}

	/** {@inheritDoc} */
	@Override
	@Secure("#{s:hasPermission('consultarPAC','visualizar')}")
	public List<PacParaJulgamentoVO> pesquisarPacsParaJulgamento(PacParaJulgamentoCriteriaVO criteria, Integer first, Integer max,
			String order, boolean asc) {
		return getScoLicitacaoON().pesquisarPacsParaJulgamento(criteria, first, max, order, asc);
	}

	/** {@inheritDoc} */
	@Override
	@Secure("#{s:hasPermission('encaminharComprador','gravar')}")
	public void encaminharComprador(Set<Integer> pacIds, Boolean limpaParecer) throws ApplicationBusinessException {
		getScoLicitacaoON().encaminharComprador(pacIds, limpaParecer);
	}

	@Override
	@Secure("#{s:hasPermission('consultarProposta','visualizar')}")
	public List<ItemLicitacaoQuadroAprovacaoVO> pesquisarPacsQuadroAprovacao(Set<Integer> pacs, Boolean assinaturas) {
		return getScoLicitacaoON().pesquisarPacsQuadroAprovacao(pacs, assinaturas);
	}

	@Override
	public List<Integer> retornarListaNumeroSolicicaoCompraPorPAC(ScoLicitacao scoLicitacao) {
		return getScoLicitacaoON().retornarListaNumeroSolicicaoCompraPorPAC(scoLicitacao);
	}

	@Override
	public List<Integer> retornarListaNumeroSolicicaoServicoPorPAC(ScoLicitacao scoLicitacao) {
		return getScoLicitacaoON().retornarListaNumeroSolicicaoServicoPorPAC(scoLicitacao);
	}

	@Override
	public boolean validarUnidadeMedida(ScoSolicitacaoDeCompra sc) {
		return !sc.getUnidadeMedida().equals(sc.getMaterial().getUnidadeMedida());
	}

	@Override
	public boolean validarJulgadoCancelado(ScoItemLicitacao item) {
		return getScoItemLicitacaoON().validarJulgadoCancelado(item);
	}

	@Override
	public PropFornecAvalParecerVO obterParecerMaterialSituacaoItemProposta(ScoItemPropostaFornecedor item) {
		return this.getScoItemPropostaFornecedorON().obterParecerMaterialSituacaoItemProposta(item);
	}

	@Override
	public Boolean verificarItemProposta(ScoLicitacao licitacao) {
		return getScoPropostaFornecedorON().verificarItemProposta(licitacao);
	}

	@Override
	public List<ItemPropostaAFVO> pesquisarItensPac(Integer numeroPac) throws ApplicationBusinessException {
		return this.getScoItemPropostaFornecedorDAO().pesquisarItensPac(numeroPac);
	}

	public ScoMaterial obterMaterialPorChavePrimaria(Integer codigoMaterial) {
		return getScoMaterialDAO().obterPorChavePrimaria(codigoMaterial);
	}

	public ScoServico obterServicoPorChavePrimaria(Integer codigoMaterial) {
		return getScoServicoDAO().obterPorChavePrimaria(codigoMaterial);
	}

	private ScoMaterialDAO getScoMaterialDAO() {
		return scoMaterialDAO;
	}

	private ScoServicoDAO getScoServicoDAO() {
		return scoServicoDAO;
	}

	protected ScoTempoAndtPacDAO getScoTempoAndtPacDAO() {
		return scoTempoAndtPacDAO;
	}

	@Override
	public void inserir(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException {
		getScoItemLicitacaoRN().inserirItemLicitacao(itemLicitacao);
	}

	@Override
	public void persistir(ScoCondicaoPgtoLicitacao cond) throws ApplicationBusinessException {
		getScoCondicaoPgtoLicitacaoRN().persistir(cond);
	}

	protected ScoCondicaoPgtoLicitacaoRN getScoCondicaoPgtoLicitacaoRN() {
		return scoCondicaoPgtoLicitacaoRN;
	}

	protected VScoItensLicitacaoDAO getVscoItensLicitacaoDAO() {
		return vScoItensLicitacaoDAO;
	}

	@Override
	public List<Object[]> montarListaDetalhesLicitacao(Integer nroLicitacao) {
		return this.getVscoItensLicitacaoDAO().obterDetalhesItensLicitacao(nroLicitacao);
	}

	@Override
	public Boolean verificarUtilizaParecerTecnico() throws ApplicationBusinessException {
		return this.getScoItemPropostaFornecedorON().verificarUtilizaParecerTecnico();
	}

	@Override
	public Boolean verificarAcoesPontoParadaPac(Integer seqAndamento) {
		return this.scoAcoesPontoParadaDAO.verificarAcoesPontoParadaPac(seqAndamento);
	}
	
	@Override
	public Boolean validarEdicaoAcoes(ScoAndamentoProcessoCompra andamento, ScoAndamentoProcessoCompra primeiroAndamento) {
		return this.duplicaPACON.validarEdicaoAcoes(andamento, primeiroAndamento);
	}

	@Override
	public List<ScoAcoesPontoParada> listarAcoesPontoParadaPac(Integer seqAndamento) {
		return this.scoAcoesPontoParadaDAO.listarAcoesPontoParadaPac(seqAndamento);
	}

	@Override
	public List<ScoLocalizacaoProcessoComprasVO> pesquisarLocalizacaoProcessoCompra(Integer first, Integer max, String order, boolean asc,
			Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento, ScoModalidadeLicitacao modalidadeCompra,
			Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel) {
		return getScoAndamentoProcessoCompraDAO().pesquisarLocalizacaoProcessoCompra(first, max, order, asc, protocolo, local, nroPac,
				complemento, modalidadeCompra, nroAF, dtEntrada, servidorResponsavel);
	}

	@Override
	public Long pesquisarLocalizacaoProcessoCompraCount(Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento,
			ScoModalidadeLicitacao modalidadeCompra, Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel) {
		return getScoAndamentoProcessoCompraDAO().pesquisarLocalizacaoProcessoCompraCount(protocolo, local, nroPac, complemento,
				modalidadeCompra, nroAF, dtEntrada, servidorResponsavel);
	}

	@Override
	public Boolean validarCamposObrigatorioLocalizacao(Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento,
			ScoModalidadeLicitacao modalidadeCompra, Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel)
			throws ApplicationBusinessException {
		return this.scoLocalizacaoProcessoCompraRN.validarCamposObrigatorioLocalizacao(protocolo, local, nroPac, complemento,
				modalidadeCompra, nroAF, dtEntrada, servidorResponsavel);
	}

	@Override
	public ScoLicitacao buscarLicitacaoPorNumero(Integer numeroPac) {
		return this.visualizarExtratoJulgamentoLicitacaoRN.buscarLicitacaoPorNumero(numeroPac);
	}

	@Override
	public List<VisualizarExtratoJulgamentoLicitacaoVO> buscarPropostasFornecedor(Integer numeroPac) {
		return this.visualizarExtratoJulgamentoLicitacaoRN.buscarPropostasFornecedor(numeroPac);
	}

	@Override
	public ScoLicitacao obterLicitacaoPorNroPAC(Integer numeroPAC) {
		return getScoLicitacaoDAO().obterLicitacaoModalidadePorNumeroPac(numeroPAC);
	}

	@Override
	public List<ScoLoteLicitacao> listarLotesPorPac(Integer nroPac) {
		return getScoLoteLicitacaoDAO().listarLotesPorPac(nroPac);
	}

	protected ScoLoteLicitacaoDAO getScoLoteLicitacaoDAO() {
		return this.scoLoteLicitacaoDAO;
	}

	protected ScoLoteLicitacaoON getScoLoteLicitacaoON() {
		return this.scoLoteLicitacaoON;
	}

	protected ScoLoteLicitacaoRN getScoLoteLicitacaoRN() {
		return this.scoLoteLicitacaoRN;
	}

	@Override
	public void gravarLoteSolicitacao(ScoLoteLicitacao loteLicitacao) throws ApplicationBusinessException {
		this.getScoLoteLicitacaoON().gravarLoteSolicitacao(loteLicitacao);
	}

	@Override
	public void excluirLoteSolicitacao(ScoLoteLicitacaoId idLoteDelecao) throws ApplicationBusinessException {
		this.getScoLoteLicitacaoON().excluirLoteSolicitacao(idLoteDelecao);
	}
	
	@Override
	public List<ItensPACVO> listarItensLictacaoPorPac(Integer nroPac) {
		return getScoItemLicitacaoDAO().listarItensMateriaisPorNroPac(nroPac);
	}

	@Override
	public List<VisualizarExtratoJulgamentoLicitacaoVO> verificaVisaoExtratoJulgamento(DominioVisaoExtratoJulgamento visao,
			List<VisualizarExtratoJulgamentoLicitacaoVO> list, List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoAux,
			VisualizarExtratoJulgamentoLicitacaoVO item) {
		return this.visualizarExtratoJulgamentoLicitacaoRN.verificaVisao(visao, list, listExtratoAux, item);
	}

	@Override
	public void validarSeExisteLote(Integer nroPac, Short nroLote) throws ApplicationBusinessException {
		getScoLoteLicitacaoON().verificarExisteLote(nroPac, nroLote);
	}

	@Override
	public Boolean verificarExisteItensAssociados(Integer lctNumero, Short numero)throws ApplicationBusinessException{
		return getScoLoteLicitacaoON().verificarExisteItensAssociados(lctNumero,numero);
	}
	
	@Override
	public void associarItensLote(List<ItensPACVO> itensLicitacao) throws ApplicationBusinessException {
		getScoLoteLicitacaoON().associarItensLote(itensLicitacao);
	}

	@Override
	public String obterParecer(Integer codigo) {
		return getScoLoteLicitacaoRN().obterParecerTecnico(codigo);
	}
	
	@Override
	public String obterParecerAtivo(Integer codigo){
		return getScoLoteLicitacaoRN().obterParecerTecnicoAtivo(codigo);
	}

	@Override
	public Boolean verificarDependenciasDoItem(Integer nroPac, Integer materialCod, Short nroLote) {
		return getScoLoteLicitacaoRN().verificarDependenciasDoItem(nroPac, materialCod, nroLote);
	}

	@Override
	@Secure("#{s:hasPermission('consultarPAC','visualizar')}")
	public Long pesquisarScoLocalizacaoProcessoCount(Object filtro) {
		return getScoLocalizacaoProcessoDAO().pesquisarScoLocalizacaoProcessoCount(filtro);
	}

	@Override
	public void enviarEmail(Integer pac, VisualizarExtratoJulgamentoLicitacaoVO vo, byte[] pdf, RapServidores usuarioLogado,
			String nomeArquivoPDF) throws ApplicationBusinessException {
		this.visualizarExtratoJulgamentoLicitacaoRN.enviarEmail(pac, vo, pdf, usuarioLogado, nomeArquivoPDF);
	}

	@Override
	public void validarContatosFornecedores(List<VisualizarExtratoJulgamentoLicitacaoVO> vos) throws ApplicationBusinessException {
		this.visualizarExtratoJulgamentoLicitacaoRN.validarContatosFornecedores(vos);
	}

	@Override
	public ScoLoteLicitacao obterLote(ScoLoteLicitacao lote) {
		return getScoLoteLicitacaoDAO().obterOriginal(lote);
	}

	@Override
	public void desfazerExcluirLotes(List<ScoLoteLicitacao> lotesLicitacao, Integer nroPac) throws ApplicationBusinessException {
		getScoLoteLicitacaoON().desfazerExcluirLotes(lotesLicitacao, nroPac);
	}

	@Override
	public void gerarLotesByPacItens(Integer nroPac) throws ApplicationBusinessException {
		getScoLoteLicitacaoON().gerarLotesByPacItens(nroPac);
	}
	
	@Override
	public String obterRamalCotacao() throws ApplicationBusinessException {
		return this.getCotarPrecoRN().obterRamalCotacao();
	}
	
	private CotarPrecoRN getCotarPrecoRN(){
		return cotarPrecoRN;
	}
	
	
	private ScoSolicitacaoDeCompraDAO getScoSolicitacaoDeCompraDAO() {
		return scoSolicitacaoDeCompraDAO;
	}	
	
    @Override
	public String geraArquivoCSV(List<RelUltimasComprasPACVO> dados,Integer numeroPac) throws IOException {
		return visualizarExtratoJulgamentoLicitacaoRN.geraArquivoCSV(dados, numeroPac);
	}

	@Override
	public void downloaded(String fileName) throws IOException {
		visualizarExtratoJulgamentoLicitacaoRN.downloaded(fileName);
	}

	@Override
	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMaterias(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String modl, Date DataNrInicial, Integer matCodigo, boolean historico) {
		return getUltimasComprasRN().pesquisarUltimasComprasMaterias(firstResult, maxResult, orderProperty, asc, modl, DataNrInicial, matCodigo, historico);
		
	}

	@Override
	public Long pesquisarUltimasComprasMateriasCount(String modl, Date DataNrInicial, Integer matCodigo, boolean historico) {
		return ultimasComprasRN.pesquisarUltimasComprasMateriasCount(modl, DataNrInicial, matCodigo,historico);
	}
	
	
	private ConsultarAndamentoProcessoCompraRN getConsultarAndamentoProcessoCompraRN(){
		return consultarAndamentoProcessoCompraRN;
	}
	
	@Override
	public List<ConsultarAndamentoProcessoCompraDataVO> consultarAndamentoProcessoCompra(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, ConsultarAndamentoProcessoCompraVO filtro) {
		return getConsultarAndamentoProcessoCompraRN().consultarAndamentoProcessoCompra(firstResult, maxResults, orderProperty, asc, filtro);
	}

	@Override
	public Long consultarAndamentoProcessoCompraCount(ConsultarAndamentoProcessoCompraVO filtro) {
		return getConsultarAndamentoProcessoCompraRN().consultarAndamentoProcessoCompraCount(filtro);
	}

	@Override
	public List<RelUltimasComprasPACVO> gerarUltimasComprasPAC(
			Integer numeroPAC, 
			List<Short> listaItens,
			List<String> listaCodigosModalidades) {
		
		return this.getScoMaterialDAO().obterItensRelatorioUltimasComprasMaterial(numeroPAC, listaItens, listaCodigosModalidades, DominioAgrupadorItemFornecedorMarca.ITEM);
	}

	@Override
	public List<Short> converterItensToShort(List<Integer> itens) {
		return this.getRelatorioUltimasComprasPACON().converterItensToShort(itens);
	}

	@Override
	public List<RelUltimasComprasPACVO> buscarUltimasComprasPAC(
			Integer numLicitacao, List<Integer> itens,
			List<String> itensModalidade, Integer qtdeUltimasCompras, DominioAgrupadorItemFornecedorMarca tipoAgrupamento) {
		return this.getRelatorioUltimasComprasPACON().buscarUltimasComprasPAC(numLicitacao, itens, itensModalidade, qtdeUltimasCompras, tipoAgrupamento);
	}
	
	public UltimasComprasRN getUltimasComprasRN(){
		return ultimasComprasRN;
	}

	@Override
	public List<String> obterEmailsFornecedor(Integer numeroFornecedor) {
		return getUltimasComprasRN().obterEmailsFornecedor(numeroFornecedor);
	}

	@Override
	public String geraArquivoCSVItem(List<RelUltimasComprasPACVOPai> dados,
			Integer numeroPAC) throws IOException {
		return visualizarExtratoJulgamentoLicitacaoRN.geraArquivoCSVItem(dados, numeroPAC);
	}
	
	@Override
	public String geraArquivoAndamentoProcessoCompra(List<ConsultarAndamentoProcessoCompraDataVO> dados) throws IOException {
		return getConsultarAndamentoProcessoCompraRN().consultarAndamentoProcessoCompra(dados);
	}

	@Override
	public List<ConsultarAndamentoProcessoCompraDataVO> consultarAndamentoProcessoCompraForCSV(ConsultarAndamentoProcessoCompraVO filtro) {
		return getConsultarAndamentoProcessoCompraRN().consultarAndamentoProcessoCompraForCSV(filtro);
	}

	@Override
	public boolean isValidForSearch(ConsultarAndamentoProcessoCompraVO filtro) {
		return getConsultarAndamentoProcessoCompraRN().isValidForSearch(filtro);
	}
	
	@Override
	public List<EstatisticaPacVO> gerarEstatisticas(){
		return this.getConsultarAndamentoProcessoCompraRN().gerarEstatisticas();
	}
	
	@Override
	public List<PACsPendetesVO> obterQtdPACsPendentes(){
		return this.getConsultarAndamentoProcessoCompraRN().obterQtdPACsPendentes();
	}
	
	@Override
	public List<ScoSolicitacaoDeCompra> obterSolicitacoesCompraPorNumero(Object filter){
		return this.getScoSolicitacaoDeCompraDAO().obterSolicitacoesCompraPorNumero(filter);
	}
	@Override
	public String gerarIdentificacaoCotacao(){
		return this.getCotarPrecoRN().gerarIdentificacaoCotacao();
	}
	@Override
	public void validarSelecaoPregaoEletronico(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException {
		this.pregaobancoBrasilRN.validarSelecaoPregaoEletronico(licitacoesSelecionadas);
	}
	@Override
	public void gerarPropostaPregaoBB(Integer numPac, String nomeArquivoProcessado) throws ApplicationBusinessException {
		this.pregaobancoBrasilRN.gerarPropostaPregaoBB(numPac, nomeArquivoProcessado);
	}
	@Override
	public ScoItemPropostaFornecedor obterItemPropostaFornecedorPorID(ScoItemPropostaFornecedorId id){
		return this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(id);
		
	}
	
	@Override
	public List<ScoFaseSolicitacao> obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(Integer numeroLCT, Short numeroItem){
		return this.getScoFaseSolicitacaoDAO().obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(numeroLCT, numeroItem); 
	}
		
	
	@Override
	public Long pesquisarLicitacoesPorNumeroDescricaoCount(String parametro) {
		return getScoLicitacaoDAO().pesquisarLicitacoesPorNumeroDescricaoCount(parametro);
	}
	
	@Override
	public String obterLocalidadeAtualPACPorNumLicitacao(Integer numeroLicitacao){
		return atualizarSituacaoEtapasPACRN.obterLocalidadeAtualPacPorNumLicitacao(numeroLicitacao);
	}
	
	@Override
	public List<String> listarHistoricoEtapas(Integer numeroLicitacao) {
		return atualizarSituacaoEtapasPACRN.listarHistoricoEtapas(numeroLicitacao);
	}
	@Override
	public String obterTempoTotal(Integer numeroLicitacao) {
		return atualizarSituacaoEtapasPACRN.obterTempoTotal(numeroLicitacao);
	}
	@Override
	public List<LocalPACVO> pesquisarLocalPACPorNumeroDescricao(Object param, Integer numeroLicitacao, String codigoModalidade) {
		return atualizarSituacaoEtapasPACRN.pesquisarLocalPACPorNumeroDescricao(param, numeroLicitacao, codigoModalidade);
	}

	@Override
	public Long pesquisarLocalPACPorNumeroDescricaoCount(Object param, Integer numeroLicitacao, String codigoModalidade) {
		return atualizarSituacaoEtapasPACRN.pesquisarLocalPACPorNumeroDescricaoCount(param, numeroLicitacao, codigoModalidade);
	}

	@Override
	public List<EtapasRelacionadasPacVO> listarEtapasRelacionadasPAC(Integer numeroLicitacao, Short codigoLocalizacao, String codigoModalidade) {
		return atualizarSituacaoEtapasPACRN.listarEtapasRelacionadasPAC(numeroLicitacao, codigoLocalizacao, codigoModalidade);
	}

	@Override
	public EtapasRelacionadasPacVO verificaNecessidadeSalvarEtapaPAC(EtapasRelacionadasPacVO codigoEtapa, Integer numeroLicitacao) {
		return atualizarSituacaoEtapasPACRN.verificaNecessidadeSalvarEtapaPAC(codigoEtapa, numeroLicitacao);  
	}

	@Override
	public void atualizarSituacaoEtapaPAC(EtapasRelacionadasPacVO vo, RapServidores servidor) {
		atualizarSituacaoEtapasPACRN.atualizarSituacaoEtapaPAC(vo, servidor);
	}

	@Override
	public String alterarTempoTotal(String dataInicioFim){
		return atualizarSituacaoEtapasPACRN.alterarTempoTotal(dataInicioFim);
	}
	
	@Override
	public String geraArquivoPAC(List<ScoLicitacao> listaExcel) throws IOException {
		return this.getScoLicitacaoON().geraArquivoPAC(listaExcel);
	}

	@Override
	public String geraArquivoItensPAC(List<ScoItemPacVO> listaExcel, Integer pac) throws IOException {
		return this.getScoLicitacaoON().geraArquivoItensPAC(listaExcel, pac);
	}
	
	@Override
	public void excluirTodosLoteSolicitacao(List<ScoLoteLicitacao> lotesSolicitacao, Integer nroPac) throws ApplicationBusinessException{
		this.getScoLoteLicitacaoON().excluirTodosLoteSolicitacao(lotesSolicitacao, nroPac);
	}

	@Override
	public void atualizarItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta, boolean novo) throws ApplicationBusinessException {
		this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itemProposta, novo);	
	}
	
	@Override
	public Integer obterCodMatServ(ScoItemLicitacao item){
		return this.dadosItemLicitacaoON.obterCodMatServ(item);
	}	
	
	@Override
	public ScoLicitacao obterLicitacaoPorModalidadeEditalAno(ScoModalidadeLicitacao modalidade, Integer edital, Integer ano) {
		return getScoLicitacaoDAO().obterLicitacaoModalidadePorModalidadeEditalAno(modalidade, edital, ano);
	}
	
	@Override
	public void validaValoresRegrasOrcamentarias(List<PreItemPacVO> listaItensVOPac) throws ApplicationBusinessException {
		this.getPreItemPacVoON().validaValoresRegrasOrcamentarias(listaItensVOPac);
	}
	
}