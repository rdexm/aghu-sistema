package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailAtrasoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ConsultaProgramacaoEntregaItemVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroPesquisaAssinarAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItemAutFornecimentoJnVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PersistenciaProgEntregaItemAfVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItemAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItensPendentesPacVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PriorizaEntregaVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentoMaterialServicoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFJnVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ResponsavelAfVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAutorizacaoFornJnVO;
import br.gov.mec.aghu.compras.business.ScoFaseSolicitacaoRN;
import br.gov.mec.aghu.compras.dao.ScoAfEmpenhoDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizFornContFuturosDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoMotivoAlteracaoAfDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.compras.dao.VScoPacientesCUMDAO;
import br.gov.mec.aghu.compras.pac.vo.GeracaoAfVO;
import br.gov.mec.aghu.compras.vo.AFPFornecedoresVO;
import br.gov.mec.aghu.compras.vo.AcessoFornProgEntregaFiltrosVO;
import br.gov.mec.aghu.compras.vo.AfFiltroVO;
import br.gov.mec.aghu.compras.vo.AfsContratosFuturosVO;
import br.gov.mec.aghu.compras.vo.AlteracaoEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.AutorizacaoFornPedidosVO;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoEntregaVO;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoManualVO;
import br.gov.mec.aghu.compras.vo.ConsultarParcelasEntregaMateriaisVO;
import br.gov.mec.aghu.compras.vo.ExcluirProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.compras.vo.FiltroParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.FiltroPesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.FiltroProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.compras.vo.MaterialServicoVO;
import br.gov.mec.aghu.compras.vo.ModalAlertaGerarVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.ParcelasAutFornPedidoVO;
import br.gov.mec.aghu.compras.vo.PesquisaAutFornPedidosVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfFiltroVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalFornecedoresVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoManualParcelasEntregaFiltroVO;
import br.gov.mec.aghu.compras.vo.ValidacaoFiltrosVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioAndamentoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoAfEmpenho;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.suprimentos.vo.CalculoValorTotalAFVO;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO;
import br.gov.mec.aghu.view.VScoPacientesCUM;

@Modulo(ModuloEnum.COMPRAS)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class AutFornecimentoFacade extends BaseFacade implements IAutFornecimentoFacade {

	@EJB
	private ScoAutorizacaoFornJnON scoAutorizacaoFornJnON;
	@EJB
	private ScoAutorizacaoFornRN scoAutorizacaoFornRN;
	@EJB
	private RecebeMaterialServicoON recebeMaterialServicoON;
	@EJB
	private AFProgramacaoManualON aFProgramacaoManualON;
	@EJB
	private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;
	@EJB
	private AutorizacaoFornecimentoEmailAtrasoRN autorizacaoFornecimentoEmailAtrasoRN;
	@EJB
	private ItemAFGerarSLCSaldoON itemAFGerarSLCSaldoON;
	@EJB
	private AFProgramacaoEntregaON aFProgramacaoEntregaON;
	@EJB
	private AutFornecimentoRN autFornecimentoRN;
	@EJB
	private ScoAutorizacaoFornON scoAutorizacaoFornON;
	@EJB
	private AutorizacaoFornecimentoEmailAtrasoON autorizacaoFornecimentoEmailAtrasoON;
	@EJB
	private ScoSolicitacaoProgramacaoEntregaON scoSolicitacaoProgramacaoEntregaON;
	@EJB
	private ScoProgEntregaItemAutorizacaoFornecimentoRN scoProgEntregaItemAutorizacaoFornecimentoRN;
	@EJB
	private ScoItemAutorizacaoFornJnON scoItemAutorizacaoFornJnON;
	@EJB
	private PesquisarVersoesItensAutfornecimentoON pesquisarVersoesItensAutfornecimentoON;
	@EJB
	private AutFornecimentoON autFornecimentoON;
	@EJB
	private ParcelaEntregaMaterialRN parcelaEntregaMaterialRN;
	@EJB
	private ProgramacaoGeralEntregaAFON programacaoGeralEntregaAFON;
	@EJB
	private ProgramacaoEntregaItensAFON programacaoEntregaItensAFON;
	@EJB
	private ParcelasEntregaMatDiretoON parcelasEntregaMatDiretoON;
	@EJB
	private ExcluirProgramacaoEntregaItemAFRN excluirProgramacaoEntregaItemAFRN;
	@EJB
	private ProgramacaoEntregaGlobalFornecedoresON programacaoEntregaGlobalFornecedoresON;
	@EJB
	private ParcelaEntregaMaterialON parcelaEntregaMaterialON;
	@EJB
	private ScoAutorizacaoFornecedorPedidoON scoAutorizacaoFornecedorPedidoON;
	@EJB
	private ScoProgEntregaItemAutorizacaoFornecimentoON scoProgEntregaItemAutorizacaoFornecimentoON;
	@EJB
	private ItemAFAtualizaSCContratoON itemAFAtualizaSCContratoON;
	@EJB
	private ScoSolicitacaoProgramacaoEntregaRN scoSolicitacaoProgramacaoEntregaRN;
	@EJB
	private ItemAFGerarSCON itemAFGerarSCON;
	@EJB
	private ProgrEntregaItensAfON progrEntregaItensAfON;
	@EJB
	private PesquisarVersaoAnteriorAutorizacaoFornON pesquisarVersaoAnteriorAutorizacaoFornON;
	@EJB
	private MantemItemAutFornValidacoesON mantemItemAutFornValidacoesON;
	@EJB
	private MantemItemAutFornecimentoON mantemItemAutFornecimentoON;
	@EJB
	private ItemPendentePacON itemPendentePacON;
	@EJB
	private ValidacaoProgEntregaItemAfON validacaoProgEntregaItemAfON;
	@EJB
	private ScoFaseSolicitacaoRN scoFaseSolicitacaoRN;
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	@EJB
	private ParcelasAFPendEntON parcelasAFPendEntON;
	
	@EJB
	private PublicaAFPRN publicaAFPRN;

	@EJB
	private ProgramacaoEntregaItensAFRN programacaoEntregaItensAFRN;

	@Inject
	private ScoContatoFornecedorDAO scoContatoFornecedorDAO;
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	@Inject
	private ScoAfEmpenhoDAO scoAfEmpenhoDAO;
	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;	
	@Inject
	private ScoAutorizFornContFuturosDAO scoAutorizFornContFuturosDAO;
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	@Inject
	private ScoMotivoAlteracaoAfDAO scoMotivoAlteracaoAfDAO;
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	@Inject
	private ScoAutorizacaoFornJnDAO scoAutorizacaoFornJnDAO;
	@Inject
	private ScoAutorizacaoFornecedorPedidoDAO scoAutorizacaoFornecedorPedidoDAO;
	@Inject
	private VScoPacientesCUMDAO vScoPacientesCUMDAO;

	private static final long serialVersionUID = 2261955322391131859L;

	@Secure("#{s:hasPermission('imprimirAF','visualizar')}")
	public RelatorioAFPVO pesquisarAFsPorLicitacaoComplSeqAlteracao(Integer numPac, Short nroComplemento)
			throws ApplicationBusinessException {
		return this.getAutFornecimentoON().pesquisarAFsPorLicitacaoComplSeqAlteracao(numPac, nroComplemento);
	}

	@Secure("#{s:hasPermission('imprimirAF','visualizar')}")
	public RelatorioAFJnVO pesquisarJnAFsPorLicitacaoComplSeqAlteracao(Integer pacNumero, Short nroComplemento, Short sequenciaAlteracao)
			throws ApplicationBusinessException {
		return this.getAutFornecimentoON().pesquisarJnAFsPorLicitacaoComplSeqAlteracao(pacNumero, nroComplemento, sequenciaAlteracao);
	}

	public String buscarPrazosAtendimento(Integer cdpNumero) throws ApplicationBusinessException {
		return this.getAutFornecimentoON().cfParcelasPgto(cdpNumero);
	}

	private AutFornecimentoON getAutFornecimentoON() {
		return autFornecimentoON;
	}

	private ProgramacaoGeralEntregaAFON getProgramacaoGeralEntregaAFON() {
		return programacaoGeralEntregaAFON;
	}

	private ProgramacaoEntregaGlobalFornecedoresON getProgramacaoEntregaGlobalFornecedoresON() {
		return programacaoEntregaGlobalFornecedoresON;
	}

	private ProgramacaoEntregaItensAFON getProgramacaoEntregaItensAFON() {
		return programacaoEntregaItensAFON;
	}

	private ParcelasEntregaMatDiretoON getParcelasEntregaMatDiretoON() {
		return parcelasEntregaMatDiretoON;
	}

	private ExcluirProgramacaoEntregaItemAFRN getExcluirProgramacaoEntregaItemAFRN() {
		return excluirProgramacaoEntregaItemAFRN;
	}

	private ProgrEntregaItensAfON getProgrEntregaItensAfON() {
		return progrEntregaItensAfON;
	}

	private AutFornecimentoRN getAutFornecimentoRN() {
		return autFornecimentoRN;
	}

	private ScoAutorizacaoFornON getScoAutorizacaoFornON() {
		return scoAutorizacaoFornON;
	}

	@Override
	public List<ScoAutorizacaoForn> pesquisarAutorizacaoFornecimento(Integer first, Integer max, String order, boolean asc,
			PesquisaAutorizacaoFornecimentoVO filtro) {
		return this.getScoAutorizacaoFornDAO().pesquisarAutorizacaoFornecimento(first, max, order, asc, filtro);
	}

	@Override
	public Long contarAutorizacaoFornecimento(PesquisaAutorizacaoFornecimentoVO filtro) {
		return this.getScoAutorizacaoFornDAO().contarAutorizacaoFornecimento(filtro);
	}

	protected ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {
		return scoSolicitacaoProgramacaoEntregaDAO;
	}

	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}

	protected ScoAutorizFornContFuturosDAO getScoAutorizFornContFuturosDAO() {
		return scoAutorizFornContFuturosDAO;
	}

	public void excluirSequenciaAlteracao(ScoAutorizacaoForn autorizacaoFornecimento, Short ultimaSequenciaAlteracao) throws BaseException {
		this.getScoAutorizacaoFornON().excluirSequenciaAlteracao(autorizacaoFornecimento, ultimaSequenciaAlteracao);
	}

	@Override
	public DominioAndamentoAutorizacaoFornecimento obterAndamentoAutorizacaoFornecimento(Integer afnNumero, Short numeroComplemento) {
		return this.getScoAutorizacaoFornDAO().obterAndamentoAutorizacaoFornecimento(afnNumero, numeroComplemento);
	}

	@Override
	public ScoAutorizacaoForn obterAfByNumero(Integer numeroAf) {
		return this.getAutFornecimentoON().obterAfByNumero(numeroAf);
	}
	
	@Override
	public ScoAutorizacaoForn obterAfByNumeroComPropostaFornecedor(Integer numeroAf) {
		return this.getAutFornecimentoON().obterAfByNumeroComPropostaFornecedor(numeroAf);
	}

	@Override
	public List<PesquisaGeralAFVO> listarAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc,
				filtro);
	}

	@Override
	public Long listarAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarAutorizacoesFornecimentoFiltradoCount(filtro);
	}

	@Override
	public List<PesquisaGeralIAFVO> listarItensAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarItensAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty,
				asc, filtro);
	}

	@Override
	public Long listarItensAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarItensAutorizacoesFornecimentoFiltradoCount(filtro);
	}

	@Override
	public Long listarPedidosAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarPedidosAutorizacoesFornecimentoFiltradoCount(filtro);
	}

	@Override
	public Long listarParcelasItensAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarParcelasItensAutorizacoesFornecimentoFiltradoCount(filtro);
	}

	@Override
	public List<PesquisaGeralPAFVO> listarPedidosAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarPedidosAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty,
				asc, filtro);
	}

	@Override
	public List<PesquisaGeralPIAFVO> listarParcelasItensAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarParcelasItensAutorizacoesFornecimentoFiltrado(firstResult, maxResults,
				orderProperty, asc, filtro);
	}

	protected ScoAutorizacaoFornecedorPedidoDAO getScoAutorizacaoFornecedorPedidoDAO() {
		return scoAutorizacaoFornecedorPedidoDAO;
	}

	@Override
	public ScoAutorizacaoForn obterAfDetalhadaByNumero(Integer numeroAf) {
		return this.getScoAutorizacaoFornDAO().obterAfDetalhadaByNumero(numeroAf);
	}

	@Override
	public List<ScoAutorizacaoForn> listarAfByFornAndLic(ScoLicitacao licitacao, ScoFornecedor fornecedor) {
		return getAutFornecimentoON().listarAfByFornAndLic(licitacao, fornecedor);
	}

	@Override
	public void atualizarAutorizacaoForn(ScoAutorizacaoForn e, boolean flush) {

		autFornecimentoRN.atualizarAutorizacaoForn(e, flush);
	}

	public void persistirAutorizacaoForn(ScoAutorizacaoForn autForn, boolean flush) {
		ScoAutorizacaoFornDAO scoAutorizacaoFornDAO = this.getScoAutorizacaoFornDAO();
		scoAutorizacaoFornDAO.persistir(autForn);
		if (flush) {
			scoAutorizacaoFornDAO.flush();
		}
	}

	@Override
	public void excluirProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento item) throws BaseException {
		this.getScoProgEntregaItemAutorizacaoFornecimentoON().excluirProgEntregaItemAf(item);
	}

	@Override
	public void liberarAssinaturaAf(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		this.getScoAutorizacaoFornON().liberarAssinaturaAf(autorizacaoFornecimento);
	}

	@Override
	public List<ScoSolicitacaoProgramacaoEntrega> pesquisarSolicitacaoProgEntregaPorItemProgEntrega(Integer iafAfnNumero,
			Integer iafNumero, Integer seq, Integer parcela) {
		return this.getScoSolicitacaoProgramacaoEntregaDAO().pesquisarSolicitacaoProgEntregaPorItemProgEntrega(iafAfnNumero, iafNumero,
				seq, parcela);
	}

	@Override
	public Integer converterUnidadeAf(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		return this.getScoAutorizacaoFornON().converterUnidadeAf(autorizacaoFornecimento);
	}

	@Override
	public void desatacharAutorizacaoForn(ScoAutorizacaoForn scoAutorizacaoForn) {
		getScoAutorizacaoFornDAO().desatachar(scoAutorizacaoForn);
	}

	@Override
	public List<ScoItemAutorizacaoForn> pesquisarAutorizacoesFornecimentoPorSeqDescricao(Object param) {
		return this.getScoAutorizacaoFornDAO().pesquisarAutorizacoesFornecimentoPorSeqDescricao(param);
	}

	@Override
	public Integer validarDataPrevisaoEntregaDuplicada(Integer afnNumero, Integer numeroItem, Integer parcelaAtual, Date previsaoEntrega) {
		return this.validacaoProgEntregaItemAfON.validarDataPrevisaoEntregaDuplicada(afnNumero, numeroItem, parcelaAtual, previsaoEntrega);
	}

	@Override
	@Secure("#{s:hasPermission('gerarAF','gravar')}")
	public void gravarAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		this.getScoAutorizacaoFornON().gravarAutorizacaoFornecimento(autorizacaoFornecimento);
	}

	@Override
	@Secure("#{s:hasPermission('gerarAF','gravar')}")
	public void atualizarAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(autorizacaoFornecimento);
	}

	private ScoAutorizacaoFornRN getScoAutorizacaoFornRN() {
		return scoAutorizacaoFornRN;
	}

	@Override
	public Boolean existeAutorizacaoFornecimentoNotaImportacao(Integer numeroAutorizacaoFornecimento, Short codigoFormaPagamentoImportacao) {
		return this.getScoAutorizacaoFornDAO().existeAutorizacaoFornecimentoNotaImportacao(numeroAutorizacaoFornecimento,
				codigoFormaPagamentoImportacao);
	}

	@Override
	public Double obterValorBrutoAf(Integer afnNumero, Short numeroComplemento, List<ScoItemAutorizacaoForn> listaItens) {
		return getAutFornecimentoON().obterValorBrutoAf(afnNumero, numeroComplemento, listaItens);
	}

	@Override
	public Double obterValorAcrescimoAf(Integer afnNumero, Short numeroComplemento, Double valorBruto,
			List<ScoItemAutorizacaoForn> listaItens) {
		return getAutFornecimentoON().obterValorAcrescimoAf(afnNumero, numeroComplemento, valorBruto, listaItens);
	}

	@Override
	public Double obterValorDescontoAf(Integer afnNumero, Short numeroComplemento, Double valorBruto,
			List<ScoItemAutorizacaoForn> listaItens) {
		return getAutFornecimentoON().obterValorDescontoAf(afnNumero, numeroComplemento, valorBruto, listaItens);
	}

	@Override
	public Integer obterMaxSequenciaAlteracaoAF(Integer numAf) {
		return getAutFornecimentoON().obterMaxSequenciaAlteracaoAF(numAf);
	}

	@Override
	public String obterCorFundoAndamentoAutorizacaoFornecimento(DominioAndamentoAutorizacaoFornecimento andamento) {
		return this.getAutFornecimentoON().obterCorFundoAndamentoAutorizacaoFornecimento(andamento);
	}

	@Override
	public Short obterMaxSequenciaAlteracaoAnteriorAfJn(Integer numAf, Short sequenciaAlteracao) {
		return this.getScoAutorizacaoFornJnDAO().obterMaxSequenciaAlteracaoAnteriorAfJn(numAf, sequenciaAlteracao);
	}

	@Override
	public void calcularPrioridadeEntrega(List<ParcelaItemAutFornecimentoVO> listaPea, DominioTipoSolicitacao tipoSolicitacao) {
		this.getScoProgEntregaItemAutorizacaoFornecimentoON().calcularPrioridadeEntrega(listaPea, tipoSolicitacao);
	}

	@Override
	public Double obterValorIpiAf(Integer afnNumero, Short numeroComplemento, Double valorBruto, Double valorDesconto,
			Double valorAcrescimo, List<ScoItemAutorizacaoForn> listaItens) {
		return getAutFornecimentoON().obterValorIpiAf(afnNumero, numeroComplemento, valorBruto, valorDesconto, valorAcrescimo, listaItens);
	}

	@Override
	public PersistenciaProgEntregaItemAfVO persistirProgEntregaItemAutorizacaoFornecimento(List<ParcelaItemAutFornecimentoVO> listaPea,
			List<ParcelaItemAutFornecimentoVO> listaPeaExclusao, ScoProgEntregaItemAutorizacaoFornecimentoId id, Date previsaoEntrega,
			Double valorParcelaAf, Integer qtdParcelaAf, DominioSimNao indPlanejada, DominioTipoSolicitacao tipoSolicitacao,
			Boolean novoRegistro, Double valorLiberar, Integer qtdLiberar) throws BaseException {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoON().persistirProgEntregaItemAutorizacaoFornecimento(listaPea,
				listaPeaExclusao, id, previsaoEntrega, valorParcelaAf, qtdParcelaAf, indPlanejada, tipoSolicitacao, novoRegistro,
				valorLiberar, qtdLiberar);

	}

	@Override
	public ScoAutorizacaoForn montarSolicitacaoProgEntrega(ParcelaItemAutFornecimentoVO parcela, DominioTipoSolicitacao tipoSolicitacao,
			ScoProgEntregaItemAutorizacaoFornecimento progEntrega, Date prevEntrega, Double valorParcela, Integer qtdeParcelaAf,
			DominioSimNao planejada, ScoProgEntregaItemAutorizacaoFornecimentoId id) throws BaseException {
		return this.getScoSolicitacaoProgramacaoEntregaON().montarSolicitacaoProgEntrega(parcela, tipoSolicitacao, progEntrega,
				prevEntrega, valorParcela, qtdeParcelaAf, planejada, id);
	}

	@Override
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(Integer afnNumero, Integer numero, Boolean indExclusao) {
		return scoFaseSolicitacaoDAO.pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(afnNumero, numero, false);
	}
	
	@Override
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoServicoPorAutorizacaoFornecimento(Integer afnNumero, Integer numero, Boolean indExclusao){ 
		return scoFaseSolicitacaoDAO.pesquisarFaseSolicitacaoServicoPorAutorizacaoFornecimento(afnNumero, numero, false);
	}
	
	public void excluirListaSolicitacaoProgramacao(List<ParcelaItemAutFornecimentoVO> listaPeaExclusao) throws BaseException {
		this.getScoSolicitacaoProgramacaoEntregaON().excluirListaSolicitacaoProgramacao(listaPeaExclusao);
	}

	@Override
	public ScoItemAutorizacaoForn obterDadosItensAutorizacaoFornecimento(Integer afNumero, Integer numero) {
		return this.getScoItemAutorizacaoFornDAO().obterDadosItensAutorizacaoFornecimento(afNumero, numero);
	}

	@Override
	public Double obterValorEfetivadoAf(Integer afnNumero, Short numeroComplemento, List<ScoItemAutorizacaoForn> listaItens) {
		return getAutFornecimentoON().obterValorEfetivadoAf(afnNumero, numeroComplemento, listaItens);
	}

	@Override
	public ScoProgEntregaItemAutorizacaoFornecimento obterProgEntregaPorChavePrimaria(ScoProgEntregaItemAutorizacaoFornecimentoId id) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public ScoAutorizacaoFornJn buscarUltimaScoAutorizacaoFornJnPorNroAF(Integer afnNumero, Short sequenciaAlteracao) {
		return this.getScoAutorizacaoFornJnDAO().buscarUltimaScoAutorizacaoFornJnPorNroAF(afnNumero, sequenciaAlteracao);
	}

	@Override
	public Double obterValorLiquidoAf(Double valorBruto, Double valorIpi, Double valorAcrescimo, Double valorDesconto) {
		return getAutFornecimentoON().getValorLiquidoAf(valorBruto, valorIpi, valorAcrescimo, valorDesconto);
	}

	@Override
	public Double obterValorTotalAf(Double valorLiquido, Double valorEfetivado) {
		return getAutFornecimentoON().getValorTotalAf(valorLiquido, valorEfetivado);
	}

	@Override
	public List<PriorizaEntregaVO> pesquisarSolicitacaoProgEntregaItemAf(Integer peaIafAfnNumero, Short peaIafNumero, Integer peaSeq,
			Integer peaParcela, Integer nrpSeq, Integer nroItem, Integer qtdLimite, Double valorLimite,
			DominioTipoSolicitacao tipoSolicitacao) {
		return this.getScoSolicitacaoProgramacaoEntregaON().pesquisarSolicitacaoProgEntregaItemAf(peaIafAfnNumero, peaIafNumero, peaSeq,
				peaParcela, nrpSeq, nroItem, qtdLimite, valorLimite, tipoSolicitacao);
	}

	@Override
	public List<ScoAutorizacaoForn> pesquisarAutorizacaoFornecimentoValidasInsercao(Integer numeroAutorizacaoFornecimento) {
		return this.getScoAutorizacaoFornDAO().pesquisarAutorizacaoFornecimentoValidasInsercao(numeroAutorizacaoFornecimento);
	}

	@Override
	public String obterNomeImagemAutorizacaoFornecimento(DominioAndamentoAutorizacaoFornecimento andamento) {
		return this.getAutFornecimentoON().obterNomeImagemAutorizacaoFornecimento(andamento);
	}

	@Override
	public List<ScoItemAutorizacaoForn> pesquisarItemAfAtivosPorNumeroAf(Integer afnNumero, Boolean filtraTipos) {
		return this.getScoItemAutorizacaoFornDAO().pesquisarItemAfAtivosPorNumeroAf(afnNumero, filtraTipos);
	}
	
	@Override
	public List<ScoItemAutorizacaoForn> pesquisarItemAfPorNumeroAf(Integer afnNumero){
		return this.getScoItemAutorizacaoFornDAO().pesquisarItemAfPorNumeroAf(afnNumero);
	}

	@Override
	public String obterDescricaoQtdItemAF(ScoItemAutorizacaoForn itemAutForn, Integer qtd) {
		return getAutFornecimentoON().obterDescricaoQtdItemAF(itemAutForn, qtd);
	}

	@Override
	@Secure("#{s:hasPermission('consultarPAC','visualizar')}")
	public boolean isEmAf(ScoPropostaFornecedor proposta) {
		return getScoAutorizacaoFornDAO().isEmAf(proposta);
	}

	public List<ScoAfEmpenho> buscarEmpenhoPorAfNum(Integer numeroAf) throws ApplicationBusinessException {
		return this.getScoAfEmpenhoDAO().buscarEmpenhoPorAfNum(numeroAf);
	}

	public void gerarAf(Integer numPac, DominioModalidadeEmpenho modalidadeEmpenhoSelecionada) throws BaseException {
		this.getAutFornecimentoRN().gerarAutorizacaoFornecimento(numPac, modalidadeEmpenhoSelecionada);
	}

	private ScoAfEmpenhoDAO getScoAfEmpenhoDAO() {
		return scoAfEmpenhoDAO;
	}

	@Override
	public ScoItemAutorizacaoForn atualizarItemAutorizacaoFornecimento(ScoItemAutorizacaoForn itemAutorizacaoForn)
			throws ApplicationBusinessException {
		return this.getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(itemAutorizacaoForn);
	}

	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	private ScoSolicitacaoProgramacaoEntregaON getScoSolicitacaoProgramacaoEntregaON() {
		return scoSolicitacaoProgramacaoEntregaON;
	}

	public List<ScoItemAutorizacaoFornJnVO> buscarListaItensJn(ScoAutorizacaoFornJn autForn) throws ApplicationBusinessException {
		return this.getScoItemAutorizacaoFornJnON().obterScoItemAutorizacaoFornJnPorNumPacSeq(autForn.getScoAutorizacaoForn().getNumero(),
				autForn.getSequenciaAlteracao().intValue());
	}

	private ScoItemAutorizacaoFornJnON getScoItemAutorizacaoFornJnON() {
		return scoItemAutorizacaoFornJnON;
	}

	private ScoMotivoAlteracaoAfDAO getScoMotivoAlteracaoAfDAO() {
		return scoMotivoAlteracaoAfDAO;
	}

	@Override
	public ScoAutorizacaoForn buscarAutFornPorNumPac(Integer numPac, Short numComplemento) {
		return this.getAutFornecimentoON().buscarAutFornPorNumPac(numPac, numComplemento);
	}

	@Override
	public Integer obterMaxNumEmpenhoPorAfeEspecie(Integer numAf, int espEmpenho) throws BaseException {
		return this.getScoAfEmpenhoDAO().buscarMaxEmpenhoPorAfNumEEspecie(numAf, espEmpenho);
	}

	@Override
	public ScoAfEmpenho obterEmpenhoPorChavePrimaria(Integer seqAf) throws BaseException {
		return this.getScoAfEmpenhoDAO().obterPorChavePrimaria(seqAf);
	}

	@Override
	public List<ScoMotivoAlteracaoAf> listarScoMotivoAlteracaoAf(Object objPesquisa) {
		return this.getScoMotivoAlteracaoAfDAO().listarScoMotivoAlteracaoAf(objPesquisa);
	}

	@Override
	public void liberarAssinaturaProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento item) throws BaseException {
		this.getScoProgEntregaItemAutorizacaoFornecimentoON().liberarAssinaturaProgEntregaItemAf(item);
	}

	@Override
	public String verificarCaracteristicaAssinarAf() {
		return this.getAutFornecimentoON().verificarCaracteristicaAssinarAf();
	}

	@Override
	public Integer obterMaxNumeroParcela(Integer iafAfnNum, Integer iafNumero) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoON().obterMaxNumeroParcela(iafAfnNum, iafNumero);
	}

	@Override
	public ScoItemAutorizacaoForn obterItemAfPorSolicitacaoCompra(Integer slcNumero, Boolean filtraContrato, Boolean filtraSituacao) {
		return this.getScoItemAutorizacaoFornDAO().obterItemAfPorSolicitacaoCompra(slcNumero, filtraContrato, filtraSituacao);
	}

	@Override
	public ScoItemAutorizacaoForn obterItemAfPorSolicitacaoServico(Integer slsNumero, Boolean filtraContrato, Boolean filtraSituacao) {
		return this.getScoItemAutorizacaoFornDAO().obterItemAfPorSolicitacaoServico(slsNumero, filtraContrato, filtraSituacao);
	}

	@Override
	public Boolean verificarProgEntregaItemAfPlanejamento(Integer iafAfnNum, Integer iafNumero) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().verificarProgEntregaItemAfPlanejamento(iafAfnNum, iafNumero);
	}

	@Override
	@Secure("#{s:hasPermission('consultarAF','visualizar')}")
	public List<ScoAutorizacaoFornJn> buscarAutFornJNPorNumPacNumCompl(Integer numPac, Short nroComplemento, int first, int max) {
		return this.getScoAutorizacaoFornJnDAO().buscarAutFornJNPorNumPacNumCompl(numPac, nroComplemento, first, max);
	}

	protected ScoAutorizacaoFornJnDAO getScoAutorizacaoFornJnDAO() {
		return scoAutorizacaoFornJnDAO;
	}

	@Override
	public Integer obterQtdeSaldoItemAF(Integer qtdeSolicitada, Integer qtdeRecebida) {
		return this.getManterItemAutFornecimentoON().obterQtdeSaldoItemAF(qtdeSolicitada, qtdeRecebida);
	}

	@Override
	public Double obterValorBrutoItemAF(Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, BigDecimal valorEfetivado,
			Boolean isSc) {
		return this.getManterItemAutFornecimentoON().obterValorBrutoItemAF(valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado,
				isSc);
	}

	@Override
	public Double obterValorDescontoItemAF(Double percDesconto, Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada,
			Integer qtdeRecebida, BigDecimal valorEfetivado, Boolean isSc) {
		return this.getManterItemAutFornecimentoON().obterValorDescontoItemAF(percDesconto, percDescontoItem, valorUnitario,
				qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
	}

	@Override
	public Double obterValorAcrescimoItemAF(Double percAcrescimo, Double percAcrescimoItem, Double valorUnitario, Integer qtdeSolicitada,
			Integer qtdeRecebida, BigDecimal valorEfetivado, Boolean isSc) {
		return this.getManterItemAutFornecimentoON().obterValorAcrescimoItemAF(percAcrescimo, percAcrescimoItem, valorUnitario,
				qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
	}

	@Override
	public List<PriorizaEntregaVO> gravarPriorizacaoEntrega(Boolean processoRecebimento, DominioTipoSolicitacao tipoSolicitacao,
			List<PriorizaEntregaVO> listaPriorizacao, Integer qtdeLimite, Double valorLimite) throws ApplicationBusinessException {
		return this.getScoSolicitacaoProgramacaoEntregaON().gravarPriorizacaoEntrega(processoRecebimento, tipoSolicitacao,
				listaPriorizacao, qtdeLimite, valorLimite);
	}

	@Override
	public Double obterValorIpiItemAF(Double percIPI, Double percAcrescimo, Double percAcrescimoItem, Double percDesconto,
			Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, BigDecimal valorEfetivado,
			Boolean isSc) {
		return this.getManterItemAutFornecimentoON().obterValorIpiItemAF(percIPI, percAcrescimo, percAcrescimoItem, percDesconto,
				percDescontoItem, valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
	}

	@Override
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarProgEntregaItemAfPlanejamento(Integer iafAfnNum, Integer iafNumero) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisarProgEntregaItemAfPlanejamento(iafAfnNum, iafNumero);
	}

	@Override
	public Double obterValorSaldoItemAF(Double percIPI, Double percAcrescimo, Double percAcrescimoItem, Double percDesconto,
			Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, BigDecimal valorEfetivado,
			Boolean isSc) {
		return this.getManterItemAutFornecimentoON().obterValorSaldoItemAF(percIPI, percAcrescimo, percAcrescimoItem, percDesconto,
				percDescontoItem, valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
	}

	@Override
	public Double obterValorTotalItemAF(Double valorEfetivado, Double percIPI, Double percAcrescimo, Double percAcrescimoItem,
			Double percDesconto, Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, Boolean isSc) {
		return this.getManterItemAutFornecimentoON().obterValorTotalItemAF(valorEfetivado, percIPI, percAcrescimo, percAcrescimoItem,
				percDesconto, percDescontoItem, valorUnitario, qtdeSolicitada, qtdeRecebida, isSc);
	}

	@Override
	public Integer obterQtdeProgramadaProgEntregaItemAf(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoON().obterQtdeProgramadaProgEntregaItemAf(listaParcelas);
	}

	@Override
	public Integer obterQtdeEfetivadaProgEntregaItemAf(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoON().obterQtdeEfetivadaProgEntregaItemAf(listaParcelas);
	}

	@Override
	public BigDecimal obterValorProgramadoProgEntregaItemAf(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoON().obterValorProgramadoProgEntregaItemAf(listaParcelas);
	}

	@Override
	public BigDecimal obterValorTotalProgEntregaItemAf(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoON().obterValorEfetivadoProgEntregaItemAf(listaParcelas);
	}

	private ScoProgEntregaItemAutorizacaoFornecimentoON getScoProgEntregaItemAutorizacaoFornecimentoON() {
		return scoProgEntregaItemAutorizacaoFornecimentoON;
	}

	@Override
	public String obterDescricaoSolicitacao(List<ScoFaseSolicitacao> fases) {
		return this.getManterItemAutFornecimentoON().obterDescricaoSolicitacao(fases);
	}

	@Override
	public Integer obterNumeroSolicitacao(List<ScoFaseSolicitacao> fases) {
		return this.getManterItemAutFornecimentoON().obterNumeroSolicitacao(fases);
	}

	@Override
	public Integer obterMaxNumeroSeqParcelaItemAf(Integer iafAfnNum, Integer iafNumero, Integer parcela) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterMaxNumeroSeqParcelaItemAf(iafAfnNum, iafNumero, parcela, null);
	}

	@Override
	public MaterialServicoVO obterDadosMaterialServico(List<ScoFaseSolicitacao> fases) {
		return this.getManterItemAutFornecimentoON().obterDadosMaterialServico(fases);
	}

	@Override
	public Boolean verificarMaterialFase(List<ScoFaseSolicitacao> fases) {
		return this.getManterItemAutFornecimentoON().verificarMaterialFase(fases);
	}

	@Override
	public void alterarItemAF(ItensAutFornVO item) throws BaseException {
		this.getManterItemAutFornecimentoON().alterarItemAF(item);
	}

	@Override
	public boolean desabilitarPermisaoSituacao(String login, String componente, String metodo,
			DominioSituacaoAutorizacaoFornecedor situacaoAf) {
		return this.getManterItemAutFornecimentoON().desabilitarPermisaoSituacao(login, componente, metodo, situacaoAf);
	}

	private MantemItemAutFornecimentoON getManterItemAutFornecimentoON() {
		return mantemItemAutFornecimentoON;
	}

	@Override
	@Secure("#{s:hasPermission('consultarAF','visualizar')}")
	public Long contarAutFornJNPorNumPacNumCompl(Integer numPac, Short nroComplemento) {
		return this.getScoAutorizacaoFornJnDAO().contarAutFornJNPorNumPacNumCompl(numPac, nroComplemento);
	}

	@Override
	public List<PesquisaAutorizacaoFornecimentoVO> pesquisarListaAfsAssinar(Integer first, Integer max,
			String order, boolean asc, FiltroPesquisaAssinarAFVO filtro) {
		return this.getAutFornecimentoON().pesquisarListaAfsAssinar(first, max, order, asc, filtro);
	}

	@Override
	public Long pesquisarListaAfsAssinarCount(FiltroPesquisaAssinarAFVO filtro) { 
		return this.getAutFornecimentoON().pesquisarListaAfsAssinarCount(filtro);
	}
	
	@Override
	@Secure("#{s:hasPermission('consultarAF','visualizar')}")
	public ScoAutorizacaoFornJn obterScoAutorizacaoFornJn(Integer numeroAf, Short complementoAf, Short sequenciaAlteracao) {
		return this.getScoAutorizacaoFornJnDAO().obterScoAutorizacaoFornJn(numeroAf, complementoAf, sequenciaAlteracao);
	}

	@Override
	@Secure("#{s:hasPermission('consultarAF','visualizar')}")
	public List<ItemAutFornecimentoJnVO> obterListaItemAutFornecimentoJnVO(Integer seqAF, Integer numeroAF, Integer sequenciaAlteracao) {
		return getPesquisarVersoesItensAutfornecimentoON().obterListaItemAutFornecimentoJnVO(seqAF, numeroAF, sequenciaAlteracao);
	}

	private PesquisarVersoesItensAutfornecimentoON getPesquisarVersoesItensAutfornecimentoON() {
		return pesquisarVersoesItensAutfornecimentoON;
	}

	@Secure("#{s:hasPermission('imprimirAF','visualizar') or s:hasPermission('assinarAF','assinar')}")
	public RelatorioAFPVO pesquisarAFPedidoPorNumEPac(Integer afpNumero, Integer numeroPac, Short nroComplemento, int espEmpenho)
			throws ApplicationBusinessException {
		return this.getScoAutorizacaoFornecedorPedidoON().listarAfsPorPedidoENumAf(afpNumero, numeroPac, nroComplemento, espEmpenho);
	}

	@Override
	@Secure("#{s:hasPermission('consultarAF','visualizar')}")
	public CalculoValorTotalAFVO getValorTotalAf(Integer numero, Integer seq) {
		return getPesquisarVersaoAnteriorAutorizacaoFornON().getCalculoValorTotalAF(numero, seq);
	}

	public void validarComplementoAssinarAf(final FiltroPesquisaAssinarAFVO filtro) throws ApplicationBusinessException {
		this.getAutFornecimentoON().validarComplementoAssinarAf(filtro);
	}

	@Override
	public Integer calcularTotalEntreguePorItemAf(Integer iafAfnNumero, Integer iafNumero, Integer parcela) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().calcularTotalEntreguePorItemAf(iafAfnNumero, iafNumero, parcela);
	}

	protected PesquisarVersaoAnteriorAutorizacaoFornON getPesquisarVersaoAnteriorAutorizacaoFornON() {
		return pesquisarVersaoAnteriorAutorizacaoFornON;
	}

	@Override
	@Secure("#{s:hasPermission('consultarAF','visualizar')}")
	public List<ResponsavelAfVO> obterResponsaveisAutorizacaoFornJn(Integer numeroAf, Short complementoAf, Short sequenciaAlteracao) {
		return getScoAutorizacaoFornJnON().obterResponsaveisAutorizacaoFornJn(numeroAf, complementoAf, sequenciaAlteracao);
	}

	protected ScoAutorizacaoFornJnON getScoAutorizacaoFornJnON() {
		return scoAutorizacaoFornJnON;
	}

	@Override
	@Secure("#{s:hasPermission('consultarAF','visualizar')}")
	public ScoCondicaoPagamentoPropos obterCondPgtoAutorizacaoFornJn(Integer numeroAf, Short complementoAf, Short sequenciaAlteracao) {
		return this.getScoAutorizacaoFornJnDAO().obterCondPgtoAutorizacaoFornJn(numeroAf, complementoAf, sequenciaAlteracao);
	}

	public ScoMotivoAlteracaoAf buscarUltimoMotivoAlteracao(Integer numeroAf, Integer numeroPac, Short complementoAf)
			throws ApplicationBusinessException {
		return this.getScoAutorizacaoFornJnON().buscarUltimoMotivoAlteracao(numeroAf, numeroPac, complementoAf);
	}

	@Override
	public void validarScContrato(ItensAutFornVO itemAutorizacaoForn) throws BaseException {
		this.getManterItemAutFornValidacoesON().validarScContrato(itemAutorizacaoForn);
	}

	@Override
	public Boolean verificarExisteSaldo(ItensAutFornVO itemAutorizacaoForn) {
		return this.getItemAFGerarSLCSaldoON().verificarExisteSaldo(itemAutorizacaoForn);
	}

	@Override
	public void processarSolicitacaoCompra(ItensAutFornVO itemAutorizacaoForn) throws BaseException {
		getItemAFGerarSCON().processarSolicitacaoCompra(itemAutorizacaoForn);
	}

	@Override
	public void alterarSituacaoItem(ItensAutFornVO itemAutorizacaoFornVo) throws BaseException {
		this.getItemAFGerarSLCSaldoON().alterarSituacaoItem(itemAutorizacaoFornVo);
	}

	@Override
	public List<ScoAutorizacaoForn> pesquisarVerbaGestaoAssociadaAf(FsoVerbaGestao verbaGestao) {
		return this.getScoAutorizacaoFornDAO().pesquisarVerbaGestaoAssociadaAf(verbaGestao);
	}

	@Override
	public void validarValorUnitarioItemAF(ItensAutFornVO itemAutFornVO) throws ApplicationBusinessException {
		this.getManterItemAutFornValidacoesON().validaValorUnitarioItemAF(itemAutFornVO);
	}

	@Override
	public boolean desabilitarCheckExclusao(DominioSituacaoAutorizacaoFornecedor situacaoAf) {
		return this.getManterItemAutFornecimentoON().desabilitarCheckExclusao(situacaoAf);
	}

	@Override
	public void validarItemEntregue(List<ScoFaseSolicitacao> fasesSolicitacao, Integer qtdeRecebida, Integer qtdeSolicitada,
			Double valorUnitario, Double valorEfetivado) throws ApplicationBusinessException {
		this.getScoItemAutorizacaoFornRN().validarItemEntregue(fasesSolicitacao, qtdeRecebida, qtdeSolicitada, valorUnitario,
				valorEfetivado);
	}

	@Override
	public void validarExclusaoEstornoItemAF(ItensAutFornVO itemAutFornVO) throws ApplicationBusinessException {
		this.getManterItemAutFornValidacoesON().validarExclusaoEstornoItemAF(itemAutFornVO);
	}

	@Override
	public void validaIndConsignadoItemAF(ItensAutFornVO itemAutFornVO) throws BaseException {
		this.getManterItemAutFornValidacoesON().validaIndConsignadoItemAF(itemAutFornVO);
	}

	@Override
	public void validaIndConsignado(ItensAutFornVO itemAutorizacaoForn) throws ApplicationBusinessException {
		this.getManterItemAutFornValidacoesON().validaIndConsignado(itemAutorizacaoForn);
	}

	@Override
	public void persistirProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento)
			throws ApplicationBusinessException {
		this.getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(progEntregaItemAutorizacaoFornecimento);
	}

	protected ItemAFAtualizaSCContratoON getItemAFAtualizaSCContratoON() {
		return itemAFAtualizaSCContratoON;
	}

	protected ItemAFGerarSCON getItemAFGerarSCON() {
		return itemAFGerarSCON;
	}

	protected ItemAFGerarSLCSaldoON getItemAFGerarSLCSaldoON() {
		return itemAFGerarSLCSaldoON;
	}

	protected MantemItemAutFornValidacoesON getManterItemAutFornValidacoesON() {
		return mantemItemAutFornValidacoesON;
	}

	protected ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN() {
		return scoItemAutorizacaoFornRN;
	}

	/**
	 * Retorna a lista de contatos por email do fornecedor.
	 * 
	 * @param numero
	 * @return lista de contatos de fornecedor que permitem receber emails
	 */
	public List<String> obterContatosPorEmailFornecedor(final ScoFornecedor fornecedor) throws BaseException {
		return this.getScoContatoFornecedorDAO().obterContatosPorEmailFornecedor(fornecedor);
	}

	/**
	 * Verifica se existe Parcela NÃO Enviada ainda ao Fornecedor e com
	 * (DT_PREV_ENTREGA + Prazo Entrega) > Data Atual. -- Nesse caso deverá
	 * atualizar a DT_PREV_ENTREGA das respectivas Parcelas com a Data Atual
	 * (Hoje) + o Prazo de Entrega indicado pelo Fornecedor na Proposta, -- para
	 * que o Fornecedor tenha tempo hábil para entrega do material.
	 * 
	 * @param numeroAf
	 *            , numeroAfp, prazo de Entrega, data Prevista para entrega
	 * @return
	 */
	public void updateParcelasAFP(Integer numAf, Integer numAfp, Short prazoEntrega) throws ApplicationBusinessException {
		this.getScoProgEntregaItemAutorizacaoFornecimentoON().updateParcelasAFP(numAf, numAfp, prazoEntrega);
	}

	/**
	 * Grava Data envio da AFP ao Fornecedor somente se ainda não gravada
	 * (Preserva sempre a primeira Dt Envio)
	 * 
	 * @param numeroAf
	 *            , numeroAfp
	 * @return
	 */
	public void updateAFP(Integer numAf, Integer numAfp) throws ApplicationBusinessException {

		this.getScoAutorizacaoFornecedorPedidoON().updateAFP(numAf, numAfp);
	}

	private ScoContatoFornecedorDAO getScoContatoFornecedorDAO() {
		return scoContatoFornecedorDAO;
	}

	@Override
	public List<PesquisaAutorizacaoFornecimentoVO> confirmarAssinaturaAf(Set<PesquisaAutorizacaoFornecimentoVO> lista) throws BaseException {
		return this.getAutFornecimentoON().confirmarAssinaturaAf(lista);
	}

	@Override
	public void cancelarAssinaturaAf(PesquisaAutorizacaoFornecimentoVO item) throws ApplicationBusinessException {
		this.getAutFornecimentoON().cancelarAssinaturaAf(item);
	}

	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	private ScoAutorizacaoFornecedorPedidoON getScoAutorizacaoFornecedorPedidoON() {
		return scoAutorizacaoFornecedorPedidoON;
	}

	/**
	 * Lista todas as AFP's que estão com entrega liberada a serem
	 * enviadas/reenviadas/não enviadas ao fornecedor. Estória #5566
	 * 
	 * @param numeroAF
	 *            (PAC), complementoAF, numeroAFP, fornecedor, dataEnvioIni,
	 *            dataEnvioFinal, ind_impressas, ind_enviadas, gestor,
	 *            grupoMaterial, material, grupoServico, servico
	 * @return lista (AutorizacaoFornPedidosVO)
	 */
	@Override
	@Secure("#{s:hasPermission('AFsLiberadasFornecedor', 'gravar')}")
	public List<AutorizacaoFornPedidosVO> pesquisarAutFornPedidosPorFiltro(Integer first, Integer max, PesquisaAutFornPedidosVO filtroVO)
			throws ApplicationBusinessException {
		return this.getScoAutorizacaoFornecedorPedidoON().pesquisarAutFornPedidosPorFiltro(first, max, filtroVO);
	}
	
	@Override
	@Secure("#{s:hasPermission('AFsLiberadasFornecedor', 'gravar')}")
	public Long pesquisarAutFornPedidosPorFiltroCount(PesquisaAutFornPedidosVO filtroVO){
		return this.getScoAutorizacaoFornecedorPedidoON().pesquisarAutFornPedidosPorFiltroCount(filtroVO);
	}

	/**
	 * Altera as tabelas SCO_AF_PEDIDOS e SCO_PROGR_ENTREGA_ITENS_AF quando a
	 * AFP não deve ser enviada ao fornecedor Estória #5566
	 * 
	 * @param listaAfpsSelecionadas
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('AFsLiberadasFornecedor', 'gravar')}")
	public void alterarAFP(List<AutorizacaoFornPedidosVO> listaSel) throws ApplicationBusinessException {
		this.getScoAutorizacaoFornecedorPedidoON().alterarAFP(listaSel);
	}

	@Override
	@Secure("#{s:hasPermission('consultarAFaAssinar','visualizar')}")
	public List<PesquisaItemAFPVO> pesquisarItensAFPedido(final FiltroAFPVO filtro) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoRN().pesquisarItensAFPedido(filtro);
	}

	@Override
	public Double obterValorTotalProgEntregaItemAf(Integer iafAfnNum, Integer iafNumero, Integer qtd, Integer qtdEntregue,
			Double valorEfetivado) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoON()
				.obterValorTotal(iafAfnNum, iafNumero, qtd, qtdEntregue, valorEfetivado);
	}

	protected ScoProgEntregaItemAutorizacaoFornecimentoRN getScoProgEntregaItemAutorizacaoFornecimentoRN() {
		return scoProgEntregaItemAutorizacaoFornecimentoRN;
	}

	@Override
	public SceNotaRecebProvisorio receberParcelaItensAF(List<RecebimentoMaterialServicoVO> listaItemAF,
			SceDocumentoFiscalEntrada documentoFiscalEntrada, RapServidores servidorLogado, ScoFornecedor fornecedor, boolean force)
			throws BaseException, ItemRecebimentoValorExcedido {
		return getRecebeMaterialServicoON().receberParcelaItensAF(listaItemAF, documentoFiscalEntrada, servidorLogado, fornecedor, force);
	}

	@Override
	public boolean verificarConfirmacaoImediataRecebimento() throws ApplicationBusinessException {
		return getRecebeMaterialServicoON().verificarConfirmacaoImediataRecebimento();
	}

	@Override
	public void enviarEmailSolicitanteCompras(List<RecebimentoMaterialServicoVO> listaItemAF) throws ApplicationBusinessException {
		getRecebeMaterialServicoON().enviarEmailSolicitanteCompras(listaItemAF);
	}

	@Override
	public List<RecebimentoMaterialServicoVO> pesquisarProgEntregaItensComSaldoPositivo(Integer numeroAF, Short complementoAf,
			DominioTipoFaseSolicitacao tipoSolicitacao) {
		return getRecebeMaterialServicoON().pesquisarProgEntregaItensComSaldoPositivo(numeroAF, complementoAf, tipoSolicitacao);
	}

	@Override
	public List<ScoAutorizacaoForn> pesquisarAFNumComplementoFornecedor(Object numeroAf, Short numComplementoAf, Integer numFornecedor,
			ScoMaterial material, ScoServico servico) {
		return getRecebeMaterialServicoON().pesquisarAFNumComplementoFornecedor(numeroAf, numComplementoAf, numFornecedor, material,
				servico);
	}

	@Override
	public List<ScoAutorizacaoForn> pesquisarComplementoNumAFNumComplementoFornecedor(Integer numeroAf, Object numComplementoAf,
			Integer numFornecedor, ScoMaterial material, ScoServico servico) {
		return getRecebeMaterialServicoON().pesquisarComplementoNumAFNumComplementoFornecedor(numeroAf, numComplementoAf, numFornecedor,
				material, servico);
	}

	@Override
	public List<ScoFornecedor> pesquisarFornecedorNumAfNumComplementoFornecedor(Integer numeroAf, Short numComplementoAf,
			Object fornFilter, ScoMaterial material, ScoServico servico, SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		return getRecebeMaterialServicoON().pesquisarFornecedorNumAfNumComplementoFornecedor(numeroAf, numComplementoAf, fornFilter,
				material, servico, documentoFiscalEntrada);
	}

	@Override
	public List<ScoMaterial> pesquisarMaterialaReceber(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, String matFilter) {
		return getRecebeMaterialServicoON().pesquisarMaterialaReceber(numeroAf, numComplementoAf, numFornecedor, matFilter);
	}

	@Override
	public List<ScoServico> pesquisarServicoaReceber(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, String param) {
		return getRecebeMaterialServicoON().pesquisarServicoaReceber(numeroAf, numComplementoAf, numFornecedor, param);
	}

	@Override
	public boolean permiteNotaFiscalEntrada() throws ApplicationBusinessException {
		return getRecebeMaterialServicoON().permiteNotaFiscalEntrada();
	}

	private RecebeMaterialServicoON getRecebeMaterialServicoON() {
		return recebeMaterialServicoON;
	}

	@Override
	@Secure("#{s:hasPermission('AFsLiberadasFornecedor', 'gravar')}")
	public List<ParcelasAutFornPedidoVO> pesquisarParcelasAfpPorFiltro(Integer numeroAF, Integer numeroAFP, DominioTipoFaseSolicitacao tipo) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisarParcelasAfpPorFiltro(numeroAF, numeroAFP, tipo);
	}

	@Override
	public ScoSolicitacaoProgramacaoEntrega obterSolicitacaoProgEntregaPorId(Long speSeq) {
		return this.getScoSolicitacaoProgramacaoEntregaDAO().obterPorChavePrimaria(speSeq);
	}

	@Override
	public void persistir(ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega) throws BaseException {
		this.getScoSolicitacaoProgramacaoEntregaRN().persistir(solicitacaoProgramacaoEntrega);
	}

	@Override
	public List<PesquisaItensPendentesPacVO> pesquisarItemLicitacaoPorNumeroLicitacao(final Integer numeroLicitacao) {
		return getItemPendentePacON().pesquisarItemLicitacaoPorNumeroLicitacao(numeroLicitacao);
	}

	private ItemPendentePacON getItemPendentePacON() {
		return itemPendentePacON;
	}

	private ScoSolicitacaoProgramacaoEntregaRN getScoSolicitacaoProgramacaoEntregaRN() {
		return scoSolicitacaoProgramacaoEntregaRN;
	}

	@Override
	public List<AfsContratosFuturosVO> montarListaItemAutorizacaoForn(AfFiltroVO filtro, DominioModalidadeEmpenho param) {
		return getScoAutorizFornContFuturosDAO().montarListaItemAutorizacaoFornVO(filtro, param);
	}

	@Override
	public List<GeracaoAfVO> listarPacsParaAF(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial, Boolean indProcNaoAptoGer) throws ApplicationBusinessException {
		return getAutFornecimentoRN().listarPacsParaAF(firstResult, maxResults, orderProperty, asc, licitacao, grupoMaterial,
				indProcNaoAptoGer);
	}

	@Override
	public Integer listarPacsParaAFCount(ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial, Boolean indProcNaoAptoGer)
			throws ApplicationBusinessException {
		return getAutFornecimentoRN().listarPacsParaAFCount(licitacao, grupoMaterial, indProcNaoAptoGer);
	}

	@Override
	public void excluirParcelasPendentes(ScoSolicitacaoDeCompra sc) throws BaseException {
		getScoProgEntregaItemAutorizacaoFornecimentoON().excluirParcelasPendentes(sc);
	}

	@Override
	public DominioTipoFaseSolicitacao getItemAutorizacaoFornecedorFaseSolicitacaoTipoPorAutorizacaoFornecimento(
			ScoAutorizacaoForn autorizacaoForn) {
		return scoFaseSolicitacaoRN.getItemAutorizacaoFornecedorFaseSolicitacaoTipoPorAutorizacaoFornecimento(autorizacaoForn);
	}

	@Override
	public Integer obterQtdeEntregaPendente(Integer codMat) {
		return this.getScoItemAutorizacaoFornDAO().obterQtdeEntregaPendente(codMat);
	}

	@Override
	public List<AFPFornecedoresVO> listProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro, Boolean isCount, Integer firstResult,
			Integer maxResult) {
		return getScoAutorizacaoFornecedorPedidoON().listProgEntregaFornecedor(filtro, isCount, firstResult, maxResult);
	}

	@Override
	public Long listProgEntregaFornecedorCount(AcessoFornProgEntregaFiltrosVO filtro) {
		return getScoAutorizacaoFornecedorPedidoON().listProgEntregaFornecedorCount(filtro);
	}

	@Override
	public void validaFiltrosProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro) throws ApplicationBusinessException {
		getScoAutorizacaoFornecedorPedidoON().validaPesquisaProgEntregaFornecedor(filtro);
	}

	@Override
	public List<ParcelasAFPendEntVO> listarParcelasAFsPendentes(FiltroParcelasAFPendEntVO filtro, Integer firstResult, Integer maxResult,
			String order, boolean asc) throws ApplicationBusinessException {
		return this.parcelasAFPendEntON.listarParcelasAFsPendentes(filtro, firstResult, maxResult, order, asc);
	}

	@Override
	public Long listarParcelasAFsPendentesCount(FiltroParcelasAFPendEntVO filtro) {
		return getScoAutorizacaoFornDAO().listarParcelasAFsPendentesCount(filtro);
	}

	//#24965
	@Override
	public AutorizacaoFornecimentoEmailAtrasoVO carregarLabelsParaTelaAutFornEmailAtraso(Integer numeroAutorizacaoFornecedor) {
		return this.autorizacaoFornecimentoEmailAtrasoON.carregarLabelsParaTelaAutFornEmailAtraso(numeroAutorizacaoFornecedor);
	}

	//#24965
	@Override
	public AutorizacaoFornecimentoEmailVO montarCorpoEmail(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
		return this.autorizacaoFornecimentoEmailAtrasoON.montarCorpoEmail(afsSelecionadas);
	}

	@Override
	public void enviarEmailFornecedor(AutorizacaoFornecimentoEmailVO dadosEmail) throws ApplicationBusinessException {
		this.autorizacaoFornecimentoEmailAtrasoRN.enviarEmailFornecedor(dadosEmail);
	}

	//#24898
	@Override
	public List<ConsultaProgramacaoEntregaItemVO> obterProgramacaoGeral(Integer numeroAf, Integer numeroComplemento)
			throws ApplicationBusinessException {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoRN().obterProgramacaoGeral(numeroAf, numeroComplemento);
	}

	//#24898
	@Override
	public List<ConsultaProgramacaoEntregaItemVO> obterProgramacaoPendente(Integer numeroAf, Integer numeroComplemento)
			throws ApplicationBusinessException {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoRN().obterProgramacaoPendente(numeroAf, numeroComplemento);
	}

	//#24898
	@Override
	public ScoAutorizacaoFornecedorPedido obterScoAutorizacaoFornecedorPedidoPorChavePrimaria(ScoAutorizacaoFornecedorPedidoId id) {
		return this.getScoAutorizacaoFornecedorPedidoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void validarDatas(Date dtInicial, Date dtFinal) throws ApplicationBusinessException {
		this.parcelasAFPendEntON.validarDatas(dtInicial, dtFinal);
	}

	@Override
	public void enviarEmailMultiplosFornecedores(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
		this.autorizacaoFornecimentoEmailAtrasoON.enviarEmailMultiplosFornecedores(afsSelecionadas);
	}

	@Override
	public String determinarProcessamentoAFs(AutorizacaoFornecimentoEmailAtrasoVO autorizacaoFornecimentoEmailAtrasoVO,
			List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
		return this.autorizacaoFornecimentoEmailAtrasoON.determinarProcessamentoAFs(autorizacaoFornecimentoEmailAtrasoVO, afsSelecionadas);
	}

	@Override
	public void selecionarAFs(ParcelasAFPendEntVO parcela, List<ParcelasAFPendEntVO> afsPendentes, List<ParcelasAFPendEntVO> afsSelecionados) {
		this.parcelasAFPendEntON.selecionarAFs(parcela, afsPendentes, afsSelecionados);
	}

	@Override
	public Boolean desativarAtivarPeriodoEntrega(FiltroParcelasAFPendEntVO filtro) {
		return this.parcelasAFPendEntON.desativarAtivarPeriodoEntrega(filtro);
	}

	@Override
	public ScoContatoFornecedor obterPrimeiroContatoPorFornecedor(ScoFornecedor fornecedor) {
		return this.parcelasAFPendEntON.obterPrimeiroContatoPorFornecedor(fornecedor);
	}

	@Override
	public void selecionarTodasAFs(boolean todasAFsSelecionadas, List<ParcelasAFPendEntVO> listaAFs,
			List<ParcelasAFPendEntVO> listaAFsSelecionadas) {
		this.parcelasAFPendEntON.selecionarTodasAFs(todasAFsSelecionadas, listaAFs, listaAFsSelecionadas);
	}

	@Override
	public void desfazerSelecaoTodasAFs(List<ParcelasAFPendEntVO> listaAFs, List<ParcelasAFPendEntVO> listaAFsSelecionadas) {
		this.parcelasAFPendEntON.desfazerSelecaoTodasAFs(listaAFs, listaAFsSelecionadas);
	}

	@Override
	public List<PesquisarPlanjProgrEntregaItensAfVO> pesquisarProgrEntregaItensAf(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		return getProgrEntregaItensAfON().pesquisarProgrEntregaItensAf(filtro, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long pesquisarProgrEntregaItensAfCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro) throws ApplicationBusinessException {
		return getProgrEntregaItensAfON().pesquisarProgrEntregaItensAfCount(filtro);
	}

	@Override
	@Secure("#{s:hasPermission('liberarParcelaAF','liberarParcelaAF')}")
	public void liberarEntrega(List<PesquisarPlanjProgrEntregaItensAfVO> lista, Date dataPrevisaoEntrega, RapServidores usuarioLogado)
			throws ApplicationBusinessException {
		this.getProgrEntregaItensAfON().liberarEntrega(lista, dataPrevisaoEntrega, usuarioLogado);
	}

	@Override
	public Date obterDataLiberacaoEntrega(Date dataPrevisaoEntrega) throws ApplicationBusinessException {
		return getProgrEntregaItensAfON().obterDataLiberacaoEntrega(dataPrevisaoEntrega);
	}

	@Override
	public List<ProgrGeralEntregaAFVO> pesquisarItensProgGeralEntregaAF(FiltroProgrGeralEntregaAFVO filtro,	Integer firstResult, Integer maxResult)
			throws ApplicationBusinessException {
		return getProgramacaoGeralEntregaAFON().pesquisarItensProgGeralEntregaAF(filtro,firstResult,maxResult);
	}

	@Override
	public ValidacaoFiltrosVO validarVisualizacaoFiltros(FiltroProgrGeralEntregaAFVO filtro, ValidacaoFiltrosVO camposVO) {
		return getProgramacaoGeralEntregaAFON().validarVisualizacaoFiltros(filtro, camposVO);
	}

	@Override
	public Long countItensProgGeralEntregaAF(FiltroProgrGeralEntregaAFVO filtro) throws ApplicationBusinessException {
		return getProgramacaoGeralEntregaAFON().countItensProgGeralEntregaAF(filtro);
	}	
	
	@Override
	public void pesquisarInfoComplementares(ProgrGeralEntregaAFVO item) {
		getProgramacaoGeralEntregaAFON().pesquisarInfoComplementares(item);
	}

	//#5565 - C1
	@Override
	public List<ProgramacaoEntregaItemAFVO> listarProgramacaoEntregaItensAF(Integer numeroAF) {
		return getProgramacaoEntregaItensAFON().listarProgramacaoEntregaItensAF(numeroAF);
	}

	//#5565 - C2
	@Override
	public String buscarQuantidadeAFsProgramadas(Integer codigoMaterial, Integer afnNumero) {
		return getProgramacaoEntregaItensAFON().buscarQuantidadeAFsProgramadas(codigoMaterial, afnNumero);
	}

	//#5565
	@Override
	public void gravarProgramacaoEntregaItemAF(List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAF) throws ApplicationBusinessException {
		this.getProgramacaoEntregaItemAFRN().gravarProgramacaoEntregaItemAF(listaProgramacaoEntregaItemAF);
	}

	//#5565
	@Override
	public void verificaAtualizacaoRegistro(ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVO,
			List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAFAlterados) {
		this.getProgramacaoEntregaItemAFRN().verificaAtualizacaoRegistro(programacaoEntregaItemAFVO, listaProgramacaoEntregaItemAFAlterados);
	}

	@Override
	public AlteracaoEntregaProgramadaVO gerarParcelasParaMatDiretos(DominioSimNao entregaProgramada, Integer afNumero)
			throws ApplicationBusinessException {
		return getParcelasEntregaMatDiretoON().gerarParcelas(entregaProgramada, afNumero);
	}

	@Override
	public List<ExcluirProgramacaoEntregaItemAFVO> pesquisarListaProgrEntregaItensAfExclusao(Integer numeroAF) {
		return this.getExcluirProgramacaoEntregaItemAFRN().pesquisarListaProgrEntregaItensAfExclusao(numeroAF);
	}

	@Override
	public void excluirListaProgrEntregaItensAf(Integer numeroAF, List<ExcluirProgramacaoEntregaItemAFVO> listaItensProgramacao) {
		this.getExcluirProgramacaoEntregaItemAFRN().excluirListaProgrEntregaItensAfExclusao(numeroAF, listaItensProgramacao);
	}

	@Override
	public void excluirProgrEntregaItensAf(Integer numeroAF) throws ApplicationBusinessException {
		this.getExcluirProgramacaoEntregaItemAFRN().excluirProgrEntregaItensAf(numeroAF);
	}

	@Override
	public List<ProgramacaoEntregaGlobalFornecedoresVO> listarProgramacaoEntregaGlobalFornecedores(Integer codigoGrupoMaterial,
			Date dataInicial, Date dataFinal, String tipoValor,
			ProgramacaoEntregaGlobalTotalizadorVO programacaoEntregaGlobalTotalizadorVO, ScoFornecedor fornecedor) {
		return getProgramacaoEntregaGlobalFornecedoresON().listarProgramacaoEntregaGlobalFornecedores(codigoGrupoMaterial, dataInicial,
				dataFinal, tipoValor, programacaoEntregaGlobalTotalizadorVO, fornecedor);
	}

	@Override
	public AlteracaoEntregaProgramadaVO gerarProgramacaoParcela(Integer afNumero, Boolean gerarProgramacao) {
		return getParcelasEntregaMatDiretoON().geraProgramacaoParcela(afNumero, gerarProgramacao);
	}

	@Override
	public boolean verificarGeracaoParcelas(AlteracaoEntregaProgramadaVO alteracaoVO) {
		return false;
	}

	@Override
	public List<ConsultaItensAFProgramacaoEntregaVO> consultarItensAFProgramacaoEntrega(final Integer numeroAF) throws BaseException {
		return this.aFProgramacaoEntregaON.consultarItensAFProgramacaoEntrega(numeroAF);
	}

	@Override
	public void manterAutorizacaoFornecimento(final Integer numeroAF, final List<ConsultaItensAFProgramacaoEntregaVO> listVO)
			throws BaseException {
		this.aFProgramacaoEntregaON.manterAutorizacaoFornecimento(numeroAF, listVO);
	}

	@Override
	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItemNumeroAfNumeroComplemento(Integer numeroAf,
			Short numeroComplemento) throws ApplicationBusinessException {
		return this.parcelaEntregaMaterialON.buscarEntregasPorItemNumeroAfNumeroComplemento(numeroAf, numeroComplemento);
	}

	@Override
	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItemNumeroLctNumeroComplemento(Integer numeroAf,
			Short numeroComplemento) {
		return this.parcelaEntregaMaterialON.buscarEntregasPorItemNumeroLctNumeroComplemento(numeroAf, numeroComplemento);
	}

	@Override
	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPendentesMaterial(Integer numeroAf, Short numeroComplemento) {
		return this.parcelaEntregaMaterialON.buscarEntregasPendentesMaterial(numeroAf, numeroComplemento);
	}

	@Override
	public void gravarParcelaEntregaMaterial(List<ConsultarParcelasEntregaMateriaisVO> listaParcelas, ConsultarParcelasEntregaMateriaisVO vo) throws ApplicationBusinessException {
		this.parcelaEntregaMaterialRN.gravar(listaParcelas, vo);
	}

	@Override
	public List<ConsultaItensAFProgramacaoManualVO> consultarItensAFProgramacaoManual(final Integer numeroItem, final Integer numeroAF,
			final Short numeroComplemento, final Integer numeroFornecedor, final Integer codigoMaterial, final Integer codigoGrupoMaterial,
			final Boolean isIndProgramado) throws ApplicationBusinessException {

		return this.aFProgramacaoManualON.consultarItensAFProgramacaoManual(numeroItem, numeroAF, numeroComplemento, numeroFornecedor,
				codigoMaterial, codigoGrupoMaterial, isIndProgramado);
	}

	@Override
	public ModalAlertaGerarVO preGerarProgramacao(ProgramacaoManualParcelasEntregaFiltroVO filtro,
			List<ConsultaItensAFProgramacaoManualVO> listaItensAF, ModalAlertaGerarVO modalAlertaGerarVO)
			throws ApplicationBusinessException {
		return this.aFProgramacaoManualON.preGerarProgramacao(filtro, listaItensAF, modalAlertaGerarVO);
	}

	@Override
	public void validarPesquisa(ProgramacaoManualParcelasEntregaFiltroVO filtro) throws ApplicationBusinessException {
		this.aFProgramacaoManualON.validarPesquisa(filtro);
	}

	@Override
	public void gerarProgramacao(ProgramacaoManualParcelasEntregaFiltroVO filtro, List<ConsultaItensAFProgramacaoManualVO> listaItensAF)
			throws BaseException {
		this.aFProgramacaoManualON.gerarProgramacao(filtro, listaItensAF);
	}
	

	@Override
	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItem() throws ApplicationBusinessException {
		return parcelaEntregaMaterialON.buscarEntregasPorItem();
	}

	@Override
	public String verificaCorFundoPrevEntregas(Date dtPrevEntrega) throws ApplicationBusinessException{
		return parcelaEntregaMaterialRN.verificaCorFundoPrevEntregas(dtPrevEntrega);
	}

	protected ProgramacaoEntregaItensAFRN getProgramacaoEntregaItemAFRN() {
		return programacaoEntregaItensAFRN;
	}
	
	@Override
	public List<VScoPacientesCUM> pesquisarPacientesCUMPorAFeAFP(Integer afeAfnNumero, Integer afeNumero){
		return this.vScoPacientesCUMDAO.pesquisarPacientesCUMPorAFeAFP(afeAfnNumero, afeNumero);
	}
	
	@Override
	public Boolean publicaAfpFornecedorEntrega(Integer afnNumero, Integer afeNumero) throws ApplicationBusinessException{
		return publicaAFPRN.publicaAfpFornecedorEntrega(afnNumero, afeNumero);
	}

}
