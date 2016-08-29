package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDevolucaoFornecedorDAO;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemNotaRecebimentoDevolucaoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceItemNotaRecebimentoDevolucaoFornecedorRN extends BaseBusiness{

@EJB
private SceItemNotaRecebimentoRN sceItemNotaRecebimentoRN;

private static final Log LOG = LogFactory.getLog(SceItemNotaRecebimentoDevolucaoFornecedorRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IComprasFacade comprasFacade;

@Inject
private SceItemNotaRecebimentoDevolucaoFornecedorDAO sceItemNotaRecebimentoDevolucaoFornecedorDAO;

@EJB
private IAutFornecimentoFacade autFornecimentoFacade;

@Inject
private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

@Inject
private SceDevolucaoFornecedorDAO sceDevolucaoFornecedorDAO;

@EJB
private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6445495537570282196L;
	
	public enum SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ITEM_DF_NAO_ENCONTRADO, MENSAGEM_ITEM_NR_NAO_ENCONTRADO,
		MENSAGEM_ITEM_NR_DF_NAO_ENCONTRADO, MENSAGEM_QTDE_ITEM_NR_X_ITEM_DF_SUPERIOR_ITEM_DF,
		MENSAGEM_DF_NAO_ENCONTRADO, MENSAGEM_ERRO_INCLUSAO_EM_DF_ESTORNADA, MENSAGEM_ERRO_INCLUSAO_EM_DF_GERADA,
		MENSAGEM_AF_NAO_ENCONTRADA, MENSAGEM_ITEM_AF_NAO_ENCONTRADO, MENSAGEM_QTD_SUPERIOR_ITEM_NR;
	}

	/**
	 * @ORADB SCEK_NRD_RN.RN_NRDP_VER_QTDE_INR
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void verificarQuantidadeDevolucaoMaiorNr(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		
		// na package original (oracle) ele verifica se existe item_df na base. como ainda estamos dentro do pre-insert
		// do item_df, ele nao existe neste ponto no java.
		// outro detalhe eh o teste se existe o item_nrdfs, que tambem nao eh possivel fazer neste ponto pelo simples motivo
		// que estamos no pre-insert do mesmo.
		
		SceItemNotaRecebimento itemNr = this.getSceItemNotaRecebimentoDAO().obterItemNotaRecebimentoPorNrEAf(itemNrDfs.getSceItemNr().getId().getNrsSeq(), 
				itemNrDfs.getSceItemNr().getItemAutorizacaoForn().getId().getAfnNumero(), itemNrDfs.getSceItemNr().getItemAutorizacaoForn().getId().getNumero());
		
		if (itemNr == null) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_ITEM_NR_NAO_ENCONTRADO,
					itemNrDfs.getSceItemNr().getId().getNrsSeq(), 
					itemNrDfs.getSceItemNr().getItemAutorizacaoForn().getId().getAfnNumero(), 
					itemNrDfs.getSceItemNr().getItemAutorizacaoForn().getId().getNumero());
		}
		
		ScoItemAutorizacaoForn itemAf = this.getComprasFacade().obterItemAutorizacaoFornPorId(itemNrDfs.getId().getIafAfnNumero(), itemNrDfs.getId().getIafNumero());
		
		if (itemAf == null) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_ITEM_AF_NAO_ENCONTRADO);
		}
		
		List<ScoSolicitacaoDeCompra> listaSolCompra = this.getSolicitacaoComprasFacade().pesquisarSolicitacaoComprasPorItemAf(itemAf.getId());
		
		if (listaSolCompra != null) {
			for (ScoSolicitacaoDeCompra solCompra : listaSolCompra) {
				Integer qtdConvertida = this.getSceItemNotaRecebimentoRN().buscarConversaoUnidade(
						solCompra.getMaterial().getCodigo(), itemNrDfs.getQuantidade(), solCompra.getDtSolicitacao(), 
						solCompra.getUnidadeMedida().getCodigo(), itemAf.getUnidadeMedida().getCodigo());
				
				if (itemNrDfs.getQuantidade() > qtdConvertida) {
					throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_QTD_SUPERIOR_ITEM_NR);
				}
			}
		}
	}

	/**
	 * @ORADB SCEK_NRD_RN.RN_NRDP_VER_QTDE_IDF 
	 * VERIFICA SE QUANTIDADE SUPERIOR A QTDE DO ITEM DF
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void verificarQuantidadeNrdSuperiorQtdeItemDevolucao(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		
		if (itemNrDfs.getSceItemDf() == null || itemNrDfs.getSceItemDf().getQuantidade() == null) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_ITEM_DF_NAO_ENCONTRADO);
		}
		
		// na package original (oracle) ele verifica se existe item_df na base. como ainda estamos dentro do pre-insert
		// do item_df, ele nao existe neste ponto no java.
		// outro detalhe eh o teste se existe o item_nrdfs, que tambem nao eh possivel fazer neste ponto pelo simples motivo
		// que estamos no pre-insert do mesmo.
		
		Integer qtdeIdf = itemNrDfs.getSceItemDf().getQuantidade();
		Integer qtdeNrd = this.getSceItemNotaRecebimentoDevolucaoFornecedorDAO().calculaQtdeTotalNrdPorItemDevolucaoFornecedor(
				itemNrDfs.getSceItemDf().getId().getDfsSeq(), itemNrDfs.getSceItemDf().getId().getNumero().shortValue()) + itemNrDfs.getQuantidade();
		
		if (qtdeNrd > qtdeIdf) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_QTDE_ITEM_NR_X_ITEM_DF_SUPERIOR_ITEM_DF,
					qtdeNrd, qtdeIdf);
		}
	}
	
	
	/**
	 * VERIFICA SE DF ESTORNADA OU GERADA
	 * @ORADB SCEK_NRD_RN.RN_NRDP_VER_SIT_DF
	 * @param dfsSeq
	 * @throws BaseException
	 */
	private void verificarSituacaoDf(Integer dfsSeq) throws BaseException {
		SceDevolucaoFornecedor devolucaoFornecedor = this.getSceDevolucaoFornecedorDAO().obterPorChavePrimaria(dfsSeq);
		
		if (devolucaoFornecedor == null) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_DF_NAO_ENCONTRADO);
		}
		if (devolucaoFornecedor.getIndEstorno()) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_ERRO_INCLUSAO_EM_DF_ESTORNADA);
		} else if (devolucaoFornecedor.getIndGerado().equals(DominioSimNao.S)) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_ERRO_INCLUSAO_EM_DF_GERADA);
		}
	}
	
	/**
	 * Atualiza quantidade da AF
	 * @ORADB SCEK_NRD_RN.RN_NRDP_ATU_AF
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void atualizarQuantidadeItemAutorizacaoForn(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		String umdCodigo = itemNrDfs.getSceItemDf().getUmdCodigo();
		Integer matCodigo = this.getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria(itemNrDfs.getSceItemDf().getEalSeq()).getCodigoMaterial();
		DominioIndOperacaoBasica indOperacaoBasica = this.getSceDevolucaoFornecedorDAO().obterOperacaoBasicaNumeroDevolucaoFornecedor(itemNrDfs.getId().getDfsSeq());
		Double idfValor = itemNrDfs.getSceItemDf().getValor();
		
		ScoItemAutorizacaoForn itemAf = this.getComprasFacade().obterItemAutorizacaoFornPorId(itemNrDfs.getId().getIafAfnNumero(), itemNrDfs.getId().getIafNumero());
		
		if (itemAf == null) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_AF_NAO_ENCONTRADA);
		}
		
		Integer vNovaQtde = itemNrDfs.getQuantidade();
		
		if (!umdCodigo.equals(itemAf.getUmdCodigoForn())) {
			this.converterQtdeNotaRecebimentoScAf(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), 
					matCodigo, umdCodigo, itemNrDfs.getQuantidade());
		}
		
		if (indOperacaoBasica.equals(DominioIndOperacaoBasica.DB)) {
			itemAf.setQtdeRecebida((Integer) CoreUtil.nvl(itemAf.getQtdeRecebida(), 0) - vNovaQtde);
			itemAf.setValorEfetivado((Double) CoreUtil.nvl(itemAf.getValorEfetivado(), 0.00) - idfValor);
		} else if (indOperacaoBasica.equals(DominioIndOperacaoBasica.CR)) {
			itemAf.setQtdeRecebida((Integer) CoreUtil.nvl(itemAf.getQtdeRecebida(), 0) + vNovaQtde);
			itemAf.setValorEfetivado((Double) CoreUtil.nvl(itemAf.getValorEfetivado(), 0.00) + idfValor);
		}
			
		this.getAutFornecimentoFacade().atualizarItemAutorizacaoFornecimento(itemAf);
		
	}
	
	/**
	 * @ORADB SCEP_MMT_CONV_QT_AF
	 * CONVERTE A QTDE RECEBIDA DA NR PARA QTDE SC PARA DEPOIS CONVERTER PARA AF
	 * @param iafAfnNumero
	 * @param iafNumero
	 * @param matCodigo
	 * @param String
	 * @param qtdeRecebida
	 * @return Integer
	 * @throws BaseException 
	 */
	public Integer converterQtdeNotaRecebimentoScAf(Integer iafAfnNumero, Integer iafNumero, Integer matCodigo, String umdCodigo, 
			Integer qtdeRecebida) throws BaseException {
		
		ScoItemAutorizacaoForn itemAf = this.getComprasFacade().obterItemAutorizacaoFornPorId(iafAfnNumero, iafNumero);
		
		if (itemAf == null) {
			throw new BaseException(SceItemNotaRecebimentoDevolucaoFornecedorRNExceptionCode.MENSAGEM_ITEM_AF_NAO_ENCONTRADO);
		}
		
		ScoSolicitacaoDeCompra solCompra = this.getSolicitacaoComprasFacade().obterSolicitacaoDeCompra(itemAf.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra().getNumero());
		
		Integer qtdRetorno = Integer.valueOf(qtdeRecebida);
		
		if (!umdCodigo.equals(solCompra.getUnidadeMedida().getCodigo())) {
			qtdRetorno = this.getSceItemNotaRecebimentoRN().buscarConversaoUnidade(matCodigo, qtdeRecebida, 
					solCompra.getDtSolicitacao(), solCompra.getUnidadeMedida().getCodigo(), itemAf.getUnidadeMedida().getCodigo());
		}
		
		qtdRetorno = qtdRetorno / itemAf.getFatorConversao();
		
		return qtdRetorno;
	}
	
	/**
	 * @ORADB SCET_NRD_BRI
	 * Processos e validacoes executados antes da insercao de um relacionamento itemNr x devolucao fornecedor
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void preInserir(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		this.verificarQuantidadeDevolucaoMaiorNr(itemNrDfs);
		this.verificarQuantidadeNrdSuperiorQtdeItemDevolucao(itemNrDfs);
		this.verificarSituacaoDf(itemNrDfs.getId().getDfsSeq());
		this.atualizarQuantidadeItemAutorizacaoForn(itemNrDfs);
	}
	
	/**
	 * Processos e validacoes executados apos da insercao de um relacionamento itemNr x devolucao fornecedor
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void posInserir(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		//
	}
	
	/**
	 * Realiza a insercao de um relacionamento itemNr x devolucao fornecedor fazendo as validacoes necessarias
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	public void inserir(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs, Boolean flush) throws BaseException {
		this.preInserir(itemNrDfs);
		this.getSceItemNotaRecebimentoDevolucaoFornecedorDAO().persistir(itemNrDfs);
		if (flush) {
			this.getSceItemNotaRecebimentoDevolucaoFornecedorDAO().flush();
		}
		this.posInserir(itemNrDfs);
	}

	/**
	 * Processos e validacoes executados antes da remocao de um relacionamento itemNr x devolucao fornecedor
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void preRemover(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		//
	}
	
	/**
	 * Processos e validacoes executados apos a remocao de um relacionamento itemNr x devolucao fornecedor
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void posRemover(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		//
	}
	
	/**
	 * Realiza a exclusao de um relacionamento itemNr x devolucao fornecedor fazendo as validacoes necessarias
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void remover(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		this.preRemover(itemNrDfs);
		this.getSceItemNotaRecebimentoDevolucaoFornecedorDAO().remover(itemNrDfs);
		this.posRemover(itemNrDfs);
	}
	
	/**
	 * Processos e validacoes executados antes da alteracao de um relacionamento itemNr x devolucao fornecedor
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void preAtualizar(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		//
	}
	
	/**
	 * Processos e validacoes executados apos a alteracao de um relacionamento itemNr x devolucao fornecedor
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	private void posAtualizar(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		//
	}
	
	/**
	 * Realiza o update de um relacionamento itemNr x devolucao fornecedor passando pelas validacoes necessarias
	 * @param itemNrDfs
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void atualizar(SceItemNotaRecebimentoDevolucaoFornecedor itemNrDfs) throws BaseException {
		this.preAtualizar(itemNrDfs);
		this.getSceItemNotaRecebimentoDevolucaoFornecedorDAO().persistir(itemNrDfs);
		this.posAtualizar(itemNrDfs);
	}

	private SceItemNotaRecebimentoDevolucaoFornecedorDAO getSceItemNotaRecebimentoDevolucaoFornecedorDAO() {
		return sceItemNotaRecebimentoDevolucaoFornecedorDAO;
	}
	
	private SceItemNotaRecebimentoDAO getSceItemNotaRecebimentoDAO() {
		return sceItemNotaRecebimentoDAO;
	}

	private SceDevolucaoFornecedorDAO getSceDevolucaoFornecedorDAO() {
		return sceDevolucaoFornecedorDAO;
	}

	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	private SceItemNotaRecebimentoRN getSceItemNotaRecebimentoRN() {
		return sceItemNotaRecebimentoRN;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return this.autFornecimentoFacade;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return this.solicitacaoComprasFacade;
	}
}
