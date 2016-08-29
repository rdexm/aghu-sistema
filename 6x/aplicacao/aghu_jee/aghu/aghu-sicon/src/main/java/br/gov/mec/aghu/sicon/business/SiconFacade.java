package br.gov.mec.aghu.sicon.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAditContratoId;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoConvItensContrato;
import br.gov.mec.aghu.model.ScoConvItensContratoId;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLogEnvioSicon;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.dao.ScoAditContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoAfContratosDAO;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoConvItensContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoItensContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoLogEnvioSiconDAO;
import br.gov.mec.aghu.sicon.vo.AutorizacaoFornecimentoVO;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.LicitacaoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ListaDetalhesItensLicVO;
import br.gov.mec.aghu.sicon.vo.ListaLicitacaoVO;
import br.gov.mec.aghu.sicon.vo.SiconResponseVO;

/**
 * Porta de entrada para os serviÃ§os do mÃ³dulo SICON
 * 
 * @author agerling
 * 
 */


@Modulo(ModuloEnum.SICON)
@Stateless
public class SiconFacade extends BaseFacade implements ISiconFacade {


	@EJB
	private GerarXmlEnvioAditivoON gerarXmlEnvioAditivoON;

	@EJB
	private ManterContratosON manterContratosON;

	@EJB
	private GerarXmlEnvioRescisaoContratoON gerarXmlEnvioRescisaoContratoON;

	@EJB
	private EnvioContratoSiconValidacaoON envioContratoSiconValidacaoON;

	@EJB
	private GerarXmlEnvioContratoON gerarXmlEnvioContratoON;

	@EJB
	private ManterRescicaoContratoON manterRescicaoContratoON;

	@EJB
	private GerenciarContratosON gerenciarContratosON;

	@EJB
	private ManterAditivoContratualON manterAditivoContratualON;

	@EJB
	private GerenciarIntegracaoSiconON gerenciarIntegracaoSiconON;

	@EJB
	private EnvioContratoSiasgSiconON envioContratoSiasgSiconON;

	@EJB
	private ManterConvenioItemContratoON manterConvenioItemContratoON;

	@EJB
	private ManterItensContratoON manterItensContratoON;

	@EJB
	private ManterAfContratoON manterAfContratoON;

	@EJB
	private VincularAFContratoON vincularAFContratoON;

	@EJB
	private PesquisarContratosFuturosON pesquisarContratosFuturosON;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	@Inject
	private ScoAditContratoDAO scoAditContratoDAO;

	@Inject
	private ScoConvItensContratoDAO scoConvItensContratoDAO;

	@Inject
	private ScoContratoDAO scoContratoDAO;

	@Inject
	private ScoLogEnvioSiconDAO scoLogEnvioSiconDAO;

	@Inject
	private ScoItensContratoDAO scoItensContratoDAO;

	@Inject
	private ScoAfContratosDAO scoAfContratosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 899337776116095511L;

	@Override
	public void inserirContratoManual(ScoContrato contratoManual)
			throws ApplicationBusinessException {
		getManterContratosON().inserirContratoManual(contratoManual);
	}

	@Override
	public void inserirContratoAutomatico(ScoContrato contratoManual)
			throws ApplicationBusinessException {
		getManterContratosON().inserirContratoAutomatico(contratoManual);
	}

	@Override
	public void alterarContratoManual(ScoContrato contratoManual)
			throws ApplicationBusinessException {
		getManterContratosON().alterarContratoManual(contratoManual);
	}

	@Override
	public void alterarContratoAutomatico(ScoContrato contratoAutomatico)
			throws ApplicationBusinessException {
		getManterContratosON().alterarContratoAutomatico(contratoAutomatico);
	}

	@Override
	public void deletarContrato(ScoContrato input) throws ApplicationBusinessException {
		getGerenciarContratosON().deletarContrato(input);
	}

	@Override
	public List<ScoConvItensContrato> listarConvItensContratos(
			ScoItensContrato input) throws BaseException {
		return getConvItensContratoDAO().getItensContratoConvenioByContrato(
				input);
	}

	@Override
	public List<ScoItensContrato> listarItensContratos(ScoContrato input)
			throws BaseException {
		List<ScoItensContrato> itensContrato = getScoItensContratoDAO().getItensContratoByContrato(input);
		
		/*
		 * inicializa manualmente as bags que não podem ser inicializadas na
		 * consulta dos itens de contrato devido ao erro cannot initialize
		 * multiple bags
		 */
		for(ScoItensContrato item : itensContrato){
			item.setConvItensContrato(scoConvItensContratoDAO.getItensContratoConvenioByContrato(item));
		}
		
		return itensContrato;
	}

	@Override
	public boolean possuiItensContrato(ScoContrato input) {
		return getScoItensContratoDAO().possuiItensContrato(input);
	}

	@Override
	public ScoResContrato getRescicaoContrato(ScoContrato input)
			throws BaseException {
		return getManterRescicaoContratoManualON().getRescicaoContrato(input);
	}

	@Override
	public ScoContrato getContrato(Integer seq) throws BaseException {
		Enum[] fetch = {ScoContrato.Fields.FORNECEDOR, ScoContrato.Fields.LICITACAO, ScoContrato.Fields.TIPO_CONTRATO_SICON};
		return scoContratoDAO.obterPorChavePrimaria(seq, null, fetch);
	}

	@Override
	public ScoContrato obterContratoPorNumeroContrato(Long nrContrato) {
		return getScoContratoDAO().obterContratoPorNumeroContrato(nrContrato);
	}

	@Override
	public void desatacharContrato(ScoContrato contrato) {
		getScoContratoDAO().desatachar(contrato);
	}

	@Override
	public void desatacharItemContrato(ScoItensContrato itemContrato) {
		getScoItensContratoDAO().desatachar(itemContrato); 
	}
	
	@Override
	public List<ScoLicitacao> listarLicitacoesAtivas(Object pesquisa)
			throws ApplicationBusinessException {
		return getVincularAFContratoON().listarLicitacoesAtivas(pesquisa);
	}

	@Override
	public void inserirAtualizarRescicao(ScoResContrato resContrato)
			throws BaseException {
		getManterRescicaoContratoManualON().persisteRescicaoContrato(
				resContrato);
	}

	@Override
	public List<ScoAditContrato> listarAditivosByContrato(ScoContrato input) {
		return getScoAditContratoDAO().obterAditivosByContrato(input);
	}

	@Override
	public ScoAditContrato getAditContratoBySeq(ScoAditContratoId id) {
		return getScoAditContratoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void excluirAditContrato(ScoAditContratoId id)
			throws ApplicationBusinessException {
		getManterAditivoContratualON().excluir(id);
	}

	@Override
	public List<RapServidores> pesquisarServidorAtivoGestorEFiscalContrato(
			Object paramPesquisa) throws BaseException {
		return getManterContratosON().pesquisarServidorAtivoGestorEFiscalContrato(
				paramPesquisa);
	}

	@Override
	public ScoItensContrato getItemContratoBySeq(Integer seq)
			throws BaseException {
		return getScoItensContratoDAO().obterPorChavePrimaria(seq, true, ScoItensContrato.Fields.CONTRATO);
	}

	@Override
	public void excluirItemContrato(Integer itemSeq)
			throws BaseException {
		getManterItensContratoON().excluir(itemSeq);
	}

	@Override
	public ScoItensContrato gravarOrAtualizarItemContrato(
			ScoItensContrato input,
			FsoConveniosFinanceiro fsoConveniosFinanceiro)
			throws BaseException {
		return getManterItensContratoON().gravarAtualizar(input,
				fsoConveniosFinanceiro);
	}

	@Override
	public ScoAditContrato gravarOrAtualizarAditivo(ScoAditContrato input)
			throws ApplicationBusinessException {
		return getManterAditivoContratualON().gravarAtualizar(input);
	}

	@Override
	public void gravarConvenioItemContrato(
			List<ScoConvItensContrato> _listaConvenios,
			List<ScoConvItensContratoId> _listaIdConveniosExcluidos,
			BigDecimal _valorTotal) throws BaseException {
		this.getManterConvenioItemContratoON().gravar(_listaConvenios,
				_listaIdConveniosExcluidos, _valorTotal);
	}

	@Override
	public List<AutorizacaoFornecimentoVO> listarAfTheFornLicContrato(
			ScoContrato contrato) {
		return getManterAfContratoON().listarAfTheFornLicContrato(contrato);
	}

	@Override
	public void gravarAfContratos(
			List<AutorizacaoFornecimentoVO> listaAutorizacaoFornecimentoVO,
			ScoContrato contrato) throws BaseException {
		this.getManterAfContratoON().gravar(listaAutorizacaoFornecimentoVO,
				contrato);
	}
	
	@Override
	public List<ScoContrato> obterContratoByFiltro(ContratoFiltroVO input) {
		return gerenciarContratosON.obterContratoByFiltro(input);
	}
	
	@Override
	public List<ScoAfContrato> obterAfByContrato(ScoContrato input) {
		if(input != null){
			return getScoAfContratosDAO().obterAfByContrato(input);
		}
		return new ArrayList<ScoAfContrato>();
	}
	
	@Override
	public List<ScoItemAutorizacaoForn> obterItemAfAtivoPorAf(ScoAfContrato afContrato) {
		if (afContrato != null && afContrato.getScoAutorizacoesForn() != null) {
			return scoItemAutorizacaoFornDAO.pesquisarItemAfAtivosPorNumeroAf(afContrato.getScoAutorizacoesForn().getNumero(),
					Boolean.FALSE);
		}
		return new ArrayList<ScoItemAutorizacaoForn>();
	}
	
	@Override
	public List<ScoFaseSolicitacao> obterFaseSolicitacaoPorItemAf(ScoItemAutorizacaoForn item) {
		if (item != null) {
			return scoFaseSolicitacaoDAO.pesquisaFasePorItensAutForn(item.getId());
		}
		return new ArrayList<ScoFaseSolicitacao>();
	}
	
	@Override
	public List<ScoItensContrato> obterItensContratoPorContrato(ScoContrato contrato){
		if (contrato != null) {
			return scoItensContratoDAO.getItensContratoByContrato(contrato);
		}
		return new ArrayList<ScoItensContrato>();
	}	
	

	@Override
	public List<ScoContrato> listarContratosFiltro(
			ContratoFiltroVO filtroContratoIntegracao) {
		return gerenciarIntegracaoSiconON.listarContratosFiltro(
				filtroContratoIntegracao);
	}

	@Override
	public List<ScoResContrato> listarRescisoesContrato(
			List<ScoContrato> listContratos) {
		return gerenciarIntegracaoSiconON.listarRescisoesContrato(
				listContratos);
	}

	@Override
	public List<ScoAditContrato> pesquisarAditivos(
			ContratoFiltroVO filtroContratoIntegracao) {
		return gerenciarIntegracaoSiconON.pesquisarAditivos(
				filtroContratoIntegracao);
	}

	@Override
	public List<ScoResContrato> listarRescisoesContratoFiltro(
			List<ScoContrato> listContratos,
			ContratoFiltroVO filtroContratoIntegracao) {
		return gerenciarIntegracaoSiconON.listarRescisoesContratoFiltro(
				listContratos, filtroContratoIntegracao);
	}

	@Override
	public List<ScoResContrato> pesquisarRescisoes(
			ContratoFiltroVO filtroContratoIntegracao) {
		return gerenciarIntegracaoSiconON.pesquisarRescisoes(
				filtroContratoIntegracao);
	}

	@Override
	public List<ScoResContrato> listarRescisoesSituacaoEnvio(
			DominioSituacaoEnvioContrato situacao) {
		return gerenciarIntegracaoSiconON.listarRescisoesSituacaoEnvio(
				situacao);
	}

	protected ManterContratosON getManterContratosON() {
		return manterContratosON;
	}

	protected VincularAFContratoON getVincularAFContratoON() {
		return vincularAFContratoON;
	}

	protected ManterConvenioItemContratoON getManterConvenioItemContratoON() {
		return manterConvenioItemContratoON;
	}

	protected ManterItensContratoON getManterItensContratoON() {
		return manterItensContratoON;
	}

	protected ManterRescicaoContratoON getManterRescicaoContratoManualON() {
		return manterRescicaoContratoON;
	}

	protected ManterAditivoContratualON getManterAditivoContratualON() {
		return manterAditivoContratualON;
	}

	protected ScoContratoDAO getScoContratoDAO() {
		return scoContratoDAO;
	}

	protected ScoItensContratoDAO getScoItensContratoDAO() {
		return scoItensContratoDAO;
	}

	protected ScoConvItensContratoDAO getConvItensContratoDAO() {
		return scoConvItensContratoDAO;
	}

	protected ScoAditContratoDAO getScoAditContratoDAO() {
		return scoAditContratoDAO;
	}

	protected ScoAfContratosDAO getScoAfContratosDAO() {
		return scoAfContratosDAO;
	}

	protected ManterAfContratoON getManterAfContratoON() {
		return manterAfContratoON;
	}

	protected GerenciarContratosON getGerenciarContratosON() {
		return gerenciarContratosON;
	}

	protected GerenciarIntegracaoSiconON getGerenciarIntegracaoSiconON() {
		return gerenciarIntegracaoSiconON;
	}

	// OperaÃ§Ãµes XML - IntegraÃ§Ã£o SICON
	@Override
	public void validarEnvioContrato(Long nrContrato)
			throws ApplicationBusinessException {
		getEnvioContratoSiconValidacaoON()
				.validaGeracaoContratoXML(nrContrato);
	}

	@Override
	public void validarEnvioAditivo(ScoAditContrato aditContrato)
			throws ApplicationBusinessException {
		getEnvioContratoSiconValidacaoON()
				.validaGeracaoAditivoXML(aditContrato);
	}

	@Override
	public void validarEnvioRescisao(ScoResContrato rescisao)
			throws ApplicationBusinessException {
		getEnvioContratoSiconValidacaoON().validaGeracaoRescisaoXML(rescisao);
	}

	@Override
	public DadosEnvioVO gerarXml(Integer seq, String autenticacaoSicon)
			throws ApplicationBusinessException {
		return getGerarXmlEnvioContratoON().gerarXml(seq, autenticacaoSicon);
	}

	@Override
	public DadosEnvioVO gerarXmlAditivo(ScoAditContrato aditContrato,
			String autenticacaoSicon) throws ApplicationBusinessException {
		return getGerarXmlEnvioAditivoON().gerarXml(aditContrato,
				autenticacaoSicon);
	}

	@Override
	public SiconResponseVO integrarSIASG(DadosEnvioVO dadosEnvioVO,
			int contSeq, Boolean usaVlrAfs) throws ApplicationBusinessException {
		return getEnvioContratoSiasgSiconON().integrarSIASG(dadosEnvioVO,
				contSeq, usaVlrAfs);
	}

	@Override
	public SiconResponseVO integrarRescisaoSIASG(DadosEnvioVO dadosEnvioVO,
			ScoResContrato rescisaoContrato) throws ApplicationBusinessException {
		return getEnvioContratoSiasgSiconON().integrarRescisaoSIASG(
				dadosEnvioVO, rescisaoContrato);
	}

	protected EnvioContratoSiasgSiconON getEnvioContratoSiasgSiconON()
			throws ApplicationBusinessException {
		return envioContratoSiasgSiconON;
	}

	protected EnvioContratoSiconValidacaoON getEnvioContratoSiconValidacaoON() {
		return envioContratoSiconValidacaoON;
	}

	protected GerarXmlEnvioContratoON getGerarXmlEnvioContratoON() {
		return gerarXmlEnvioContratoON;
	}

	protected GerarXmlEnvioAditivoON getGerarXmlEnvioAditivoON() {
		return gerarXmlEnvioAditivoON;
	}

	protected GerarXmlEnvioRescisaoContratoON getGerarXmlEnvioRescisaoContratoON() {
		return gerarXmlEnvioRescisaoContratoON;
	}

	@Override
	public SiconResponseVO enviarAditivo(DadosEnvioVO dadosEnvioVO,
			ScoAditContrato aditContrato) throws ApplicationBusinessException {
		return getEnvioContratoSiasgSiconON().integrarAditivoSIASG(
				dadosEnvioVO, aditContrato);
	}

	@Override
	public DadosEnvioVO gerarXmlRescisaoContrato(Integer seq,
			String autenticacaoSicon)
			throws ApplicationBusinessException {
		return getGerarXmlEnvioRescisaoContratoON().gerarXml(seq,
				autenticacaoSicon);
	}

	@Override
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoContratos(
			ScoContrato contrato) {
		return getScoLogEnvioSiconDAO().pesquisarRetornoIntegracaoContratos(
				contrato);
	}

	@Override
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoAditivos(
			ScoContrato contrato) {
		return getScoLogEnvioSiconDAO().pesquisarRetornoIntegracaoAditivos(
				contrato);

	}

	@Override
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoRescicao(
			ScoContrato contrato) {
		return getScoLogEnvioSiconDAO().pesquisarRetornoIntegracaoRescicao(
				contrato);
	}

	@Override
	public void atualizaLogEnvioSicon(ScoLogEnvioSicon log) {
		getScoLogEnvioSiconDAO().atualizar(log);
		getScoLogEnvioSiconDAO().flush();
	}

	protected ScoLogEnvioSiconDAO getScoLogEnvioSiconDAO() {
		return scoLogEnvioSiconDAO;
	}

	@Override
	public ScoLogEnvioSicon obterLogEnvioSicon(Integer seqScoLogEnvioSicon)
			throws ApplicationBusinessException {
		return getScoLogEnvioSiconDAO().obterPorChavePrimaria(
				seqScoLogEnvioSicon);
	}

	@Override
	public List<ScoContrato> obterListaContratoAssociado(ScoTipoContratoSicon _tipoContrato) {
		return this.getScoContratoDAO().obterListaContratoAssociado(_tipoContrato);
	}
	
	@Override
	public List<ScoAditContrato> obterListaAditContratoAssociado(
			ScoTipoContratoSicon _tipoContrato) {
		return this.getScoAditContratoDAO().obterListaAditContratoAssociado(_tipoContrato);
	}
	
	@Override
	public List<ScoContrato> obterListaContratoPorCriterioReajuste(ScoCriterioReajusteContrato scoCriterioReajusteContrato) {
		return this.getScoContratoDAO().obterListaContratoPorCriterioReajuste(scoCriterioReajusteContrato);
	}
	
	@Override
	public List<ScoContrato> listarContratoByNroOuDescricao(Object paramPesquisa) {
		return getScoContratoDAO().listarContratoByNroOuDescricao(paramPesquisa);
	}
	@Override
	public List<ScoItensContrato> getItensContratoByContratoManual(ScoContrato input) {
		return this.getScoItensContratoDAO().getItensContratoByContratoManual(input);
	}
	
	@Override
	public ScoAfContrato obterAfContratosById(Integer seq){
		return this.getScoAfContratosDAO().obterAfContratosById(seq);
	}
	
	@Override
	public ScoAditContrato obterAfContratoComContrato(ScoAditContratoId id){
		return this.getScoAditContratoDAO().obterContratoPorIdComContrato(id);
	}

	
	@Override
	public Boolean indicarExclusaoAditivo(ScoAditContrato aditivo) {
		return getManterAditivoContratualON().indicarExclusaoAditivo(aditivo); 
	}
	
	@Override
	public ScoAfContrato buscaItemContratoAF(Integer iafAfnNumero) {
		return this.getScoAfContratosDAO().buscaItemContratoAF(iafAfnNumero);
	}
	
	private PesquisarContratosFuturosON getPesquisarContratosFuturosON() {
		return pesquisarContratosFuturosON;
	}

	/**
	 * Monta a lista de licitações 
	 * @param licitacoes
	 * @param filtro
	 * @param param
	 * @return
	 * @throws BaseException
	 */
	@Override
	public List<ListaLicitacaoVO> montarListaLicitacoes(LicitacaoFiltroVO filtro, DominioModalidadeEmpenho param) throws BaseException {
		return getPesquisarContratosFuturosON().montarListaLicitacoes(filtro, param);
	}
	
	/**
	 * Monta a lista de detalhes da licitação
	 * @param listaDetalhesItensLic
	 * @return
	 * @throws BaseException
	 */
	@Override
	public List<ListaDetalhesItensLicVO> montarlistaDetalhesItensLic(List<Object[]> listaDetalhesItensLic) throws BaseException {
		return getPesquisarContratosFuturosON().montarlistaDetalhesItensLic(listaDetalhesItensLic);
	}
	/**
	 * Param: Licitação Verifica a inexistência de registros Ativos(INATIVOS),
	 * em Compras/Serviço, SE PELO MENOS UM não for, retorna FALSE, caso todos
	 * forem ativos retorna TRUE. FALSE - pinta de vermelho - TRUE - pinta de
	 * verde
	 **/
	public Boolean possuiCodSiasg(Integer numeroLicitacao)	throws ApplicationBusinessException {
		return getPesquisarContratosFuturosON().possuiCodSiasg(numeroLicitacao);
	}

	/**
	 * Retorna quantidades de materiais e/ou serviços pendentes de codigo sicon.
	 * 
	 * @param contrato
	 * @return
	 */
	public Long siconPendentesCount(ScoContrato contrato) {
		return getGerenciarContratosON().siconPendentesCount(contrato);
	}
	
}
