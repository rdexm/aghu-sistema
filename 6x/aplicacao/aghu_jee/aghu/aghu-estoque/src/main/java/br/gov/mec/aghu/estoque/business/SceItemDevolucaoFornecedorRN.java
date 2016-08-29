package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceItemDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceItemDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemDfHistorico;
import br.gov.mec.aghu.model.SceItemDfHistoricoId;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemNotaRecebimentoDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemNotaRecebimentoDevolucaoFornecedorId;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceItemDevolucaoFornecedorRN extends BaseBusiness{

@EJB
private ConfirmacaoDevolucaoON confirmacaoDevolucaoON;
@EJB
private SceMovimentoMaterialRN sceMovimentoMaterialRN;
@EJB
private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
@EJB
private SceItemNotaRecebimentoDevolucaoFornecedorRN sceItemNotaRecebimentoDevolucaoFornecedorRN;
@EJB
private SceTipoMovimentosRN sceTipoMovimentosRN;
@EJB
private SceItemDfHistoricoRN sceItemDfHistoricoRN;

private static final Log LOG = LogFactory.getLog(SceItemDevolucaoFornecedorRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IComprasFacade comprasFacade;

@EJB
private IAutFornecimentoFacade autFornecimentoFacade;

@Inject
private SceItemDevolucaoFornecedorDAO sceItemDevolucaoFornecedorDAO;

@Inject
private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;

@Inject
private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9135311139462101679L;

	public enum SceItemDevolucaoFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DF_GERADA, MENSAGEM_DF_ESTORNADA, MENSAGEM_DF_NR_ATIVA, MENSAGEM_QTDE_INSUFICIENTE_ESTORNO, MENSAGEM_ITEM_AF_NAO_ENCONTRADO;
	}
	
	/**
	 * @ORADB SCEK_IDF_RN.RN_IDFP_VER_SIT_DF
	 * @param itemDf
	 * @throws ApplicationBusinessException
	 */
	private void verificarDfGerada(SceItemDevolucaoFornecedor itemDf) throws ApplicationBusinessException {
		if (itemDf.getSceDevolucaoFornecedor().getIndGerado().equals(DominioSimNao.S.toString())) {
			throw new ApplicationBusinessException(SceItemDevolucaoFornecedorRNExceptionCode.MENSAGEM_DF_GERADA);
		}
	}
	
	/**
	 * @ORADB SCEK_IDF_RN.RN_IDFP_VER_DFS_EST
	 * @param itemDf
	 * @throws ApplicationBusinessException
	 */
	private void verificarDfEstornada(SceItemDevolucaoFornecedor itemDf) throws ApplicationBusinessException {
		if (itemDf.getSceDevolucaoFornecedor().getIndEstorno().equals(DominioSimNao.S.toString())) {
			throw new ApplicationBusinessException(SceItemDevolucaoFornecedorRNExceptionCode.MENSAGEM_DF_ESTORNADA);
		}
	}
	
	/**
	 * @ORADB SCEK_IDF_RN.RN_IDFP_VER_NR_ATIVA
	 * @param dfsSeq
	 * @param ealSeq
	 * @throws ApplicationBusinessException
	 */
	private void verificarDfComNrAtiva(Integer dfsSeq, Integer ealSeq) throws ApplicationBusinessException {
		if (this.getSceItemNotaRecebimentoDAO().verificarDevolucaoComNrAtiva(dfsSeq, ealSeq)) {
			throw new ApplicationBusinessException(SceItemDevolucaoFornecedorRNExceptionCode.MENSAGEM_DF_NR_ATIVA);
		}
	}
	
	
	/**
	 * @ORADB SCEK_IDF_RN.RN_IDFP_ATU_MVTO
	 * @param itemDf
	 * @param nomeMicroComputador
	 * @param tmvDocDf
	 * @throws BaseException
	 */
	private void atualizarMovimentoMaterial(SceItemDevolucaoFornecedor itemDf, String nomeMicroComputador,
			Integer tmvDocDf) throws BaseException {

		SceEstoqueAlmoxarifado estoqueAlmox = this.getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoPorSeqEUmdCodigo(itemDf.getEalSeq(),
				itemDf.getUmdCodigo());
		
		SceTipoMovimento tipoMovimento = this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(tmvDocDf.shortValue());
		
		ScoUnidadeMedida unidadeMedida = this.getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(itemDf.getUmdCodigo()); 
		
		if (estoqueAlmox != null && tipoMovimento != null && unidadeMedida != null) {
			this.getSceMovimentoMaterialRN().atualizarMovimentoMaterial(estoqueAlmox.getAlmoxarifado(), estoqueAlmox.getMaterial(), 
					unidadeMedida, itemDf.getQuantidade(), null, Boolean.FALSE, tipoMovimento, 
					tipoMovimento, itemDf.getId().getDfsSeq(), itemDf.getId().getNumero().shortValue(), 
					itemDf.getSceDevolucaoFornecedor().getMotivo(), estoqueAlmox.getFornecedor(), 
					null, null, null, new BigDecimal(itemDf.getValor()), null, 
					nomeMicroComputador, false);	
		}
	}
	
	/**
	 * Insere um relacionamento itemDevolucao x historico problema
	 * @param seqHistoricoProblema
	 * @param itemDf
	 * @throws BaseException
	 */
	private void inserirItemDfHistorico(Integer seqHistoricoProblema, SceItemDevolucaoFornecedor itemDf) throws BaseException {
		SceHistoricoProblemaMaterial historicoProblema = this.getSceHistoricoProblemaMaterialDAO().obterPorChavePrimaria(seqHistoricoProblema);
		
		if (historicoProblema != null && itemDf != null) {
			SceItemDfHistoricoId idItemDfh = new SceItemDfHistoricoId();
			
			idItemDfh.setHpmSeq(seqHistoricoProblema);
			idItemDfh.setIdfDfsSeq(itemDf.getId().getDfsSeq());
			idItemDfh.setIdfNumero(itemDf.getId().getNumero().shortValue());
			SceItemDfHistorico itemDfh = new SceItemDfHistorico();
			itemDfh.setId(idItemDfh);
			itemDfh.setQuantidade(itemDf.getQuantidade());
			itemDfh.setSceHistoricoProblemaMaterial(historicoProblema);
			itemDfh.setSceItemDevolucaoFornecedor(itemDf);
			
			this.getSceItemDfHistoricoRN().inserir(itemDfh, true);
		}
	}
	
	/**
	 * Atualiza o campo qtd_df da tabela de historico de problemas de material
	 * @param itemDf
	 * @throws BaseException
	 */
	private void atualizarHistoricoProblemaMaterial(SceItemDevolucaoFornecedor itemDf) throws BaseException {
		List<SceHistoricoProblemaMaterial> listaHistoricoProblema = this.getSceHistoricoProblemaMaterialDAO().
				pesquisarHistoricoProblemaMaterialPorFornecedorQtdProblema(
						itemDf.getSceDevolucaoFornecedor().getSceDocumentoFiscalEntrada().getFornecedor().getNumero(), 
						itemDf.getEalSeq(), itemDf.getQuantidade(), null);
		
		for (SceHistoricoProblemaMaterial historico : listaHistoricoProblema) {
			// teoricamente esta fazendo isso duas vezes... 
			// o marcelo vai ver com o hoffman porque disso
			//historico.setQtdeDf(itemDf.getQuantidade());
			//this.getSceHistoricoProblemaMaterialRN().atualizar(historico, false);
			
			this.inserirItemDfHistorico(historico.getSeq(),	itemDf);
		}
	}
	
	private void inserirRelacionamentoItemNotaRecebimentoDevolucaoFornecedor(SceItemDevolucaoFornecedor itemDf, Integer numeroNr, ScoMaterial material) throws BaseException {
		SceItemNotaRecebimento itemNr = this.getSceItemNotaRecebimentoDAO().
				obterItemNotaRecebimentoPorSeqRecebimentoEMaterial(numeroNr, material.getCodigo());
		
		if (itemNr != null) {
			SceItemNotaRecebimentoDevolucaoFornecedorId idInrDfs = new SceItemNotaRecebimentoDevolucaoFornecedorId();
			idInrDfs.setDfsSeq(itemDf.getId().getDfsSeq());
			idInrDfs.setIdfNumero(itemDf.getId().getNumero());
			idInrDfs.setNrsSeq(numeroNr);
			idInrDfs.setIafAfnNumero(itemNr.getItemAutorizacaoForn().getId().getAfnNumero());
			idInrDfs.setIafNumero(itemNr.getItemAutorizacaoForn().getId().getNumero());
			
			
			SceItemNotaRecebimentoDevolucaoFornecedor item = new SceItemNotaRecebimentoDevolucaoFornecedor();
			item.setId(idInrDfs);
			item.setSceItemNr(itemNr);
			item.setQuantidade(itemDf.getQuantidade());
			item.setSceItemDf(itemDf);
			
			// aqui ele vai fazer o flush pois o item_nr_df eh utilizado na atualizacao das parcelas,
			// para chegar no item nr
			this.getSceItemNotaRecebimentoDevolucaoFornecedorRN().inserir(item, true);
		}
	}
	
	/**
	 * @ORADB SCET_IDF_BRI
	 * Processos e validacoes executados antes da insercao de um item de devolucao do fornecedor
	 * @param itemDf
	 * @param material
	 * @param nomeMicroComputador
	 * @param numeroNr
	 * @throws BaseException
	 */
	private void preInserir(SceItemDevolucaoFornecedor itemDf, ScoMaterial material,
			String nomeMicroComputador, Integer numeroNr) throws BaseException {
		this.verificarDfGerada(itemDf);
		this.verificarDfEstornada(itemDf);
		this.getSceEstoqueAlmoxarifadoRN().verificarEstoqueAlmoxarifadoAtivo(itemDf.getEalSeq());
		this.getSceEstoqueAlmoxarifadoRN().verificarUnidadeMedidaEstoqueAlmoxarifado(itemDf.getEalSeq(), itemDf.getUmdCodigo());
		this.verificarDfComNrAtiva(itemDf.getId().getDfsSeq(), itemDf.getEalSeq());
	}
		
	/**
	 * Processos e validacoes executados apos da insercao de um item de devolucao do fornecedor
	 * @param itemDf
	 * @param material
	 * @param nomeMicroComputador
	 * @param numeroNr
	 * @param tmvDocDf
	 * @param valorUnitario
	 * @throws BaseException
	 */
	private void posInserir(SceItemDevolucaoFornecedor itemDf, ScoMaterial material, 
			String nomeMicroComputador, Integer numeroNr,
			Integer tmvDocDf, BigDecimal valorUnitario) throws BaseException {
		
		// os metodos abaixo estao originalmente na BRI do item_df. porem, como usamos hibernate nem
		// sempre temos o que precisamos ja persistido na base, podem ocorrer erros de constraint nos inserts realizados que apontam para
		// o item df. aparentemente movendo o codigo para a ARI funciona sem problemas.
		
		if (!this.getConfirmacaoDevolucaoON().verificarMaterialImobilizado(material.getCodigo(), valorUnitario)) {
			this.atualizarMovimentoMaterial(itemDf, nomeMicroComputador, tmvDocDf);
			
			this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(tmvDocDf.shortValue());
			
			this.atualizarHistoricoProblemaMaterial(itemDf);
		}
		
		this.inserirRelacionamentoItemNotaRecebimentoDevolucaoFornecedor(itemDf, numeroNr, material);	
	}
	
	/**
	 * Realiza a insercao de um item de devolucao do fornecedor fazendo as validacoes necessarias
	 * @param itemDf
	 * @param material
	 * @param nomeMicroComputador
	 * @param tmvDocDf
	 * @param numeroNr
	 * @throws BaseException
	 */
	public void inserir(SceItemDevolucaoFornecedor itemDf, ScoMaterial material,
			String nomeMicroComputador,
			Integer tmvDocDf, Integer numeroNr) throws BaseException {
		
		BigDecimal valorUnitario = new BigDecimal(itemDf.getValor() / itemDf.getQuantidade());
		
		this.preInserir(itemDf, material, nomeMicroComputador, numeroNr);
		this.getSceItemDevolucaoFornecedorDAO().persistir(itemDf);
		// aqui temos que usar o flush pois precisamos relacionar o item_df em subsequentes selects para atualizacao da parcela
		this.getSceItemDevolucaoFornecedorDAO().flush();
		this.posInserir(itemDf, material, nomeMicroComputador, numeroNr, tmvDocDf, valorUnitario);
	}

	/**
	 * Processos e validacoes executados antes da remocao de um item de devolucao do fornecedor
	 * @param itemDf
	 * @throws BaseException
	 */
	private void preRemover(SceItemDevolucaoFornecedor itemDf) throws BaseException {
		//
	}
	
	/**
	 * Processos e validacoes executados apos a remocao de um item de devolucao do fornecedor
	 * @param itemDf
	 * @throws BaseException
	 */
	private void posRemover(SceItemDevolucaoFornecedor itemDf) throws BaseException {
		//
	}
	
	/**
	 * Realiza a exclusao de um item de devolucao do fornecedor fazendo as validacoes necessarias
	 * @param itemDf
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void remover(SceItemDevolucaoFornecedor itemDf) throws BaseException {
		this.preRemover(itemDf);
		this.getSceItemDevolucaoFornecedorDAO().remover(itemDf);
		this.posRemover(itemDf);
	}
	
	/**
	 * Processos e validacoes executados antes da alteracao de um item de devolucao do fornecedor
	 * @param itemDf
	 * @throws BaseException
	 */
	private void preAtualizar(SceItemDevolucaoFornecedor itemDf) throws BaseException {
		//
	}
	
	/**
	 * Processos e validacoes executados apos a alteracao de um item de devolucao do fornecedor
	 * @param itemDf
	 * @throws BaseException
	 */
	private void posAtualizar(SceItemDevolucaoFornecedor itemDf) throws BaseException {
		//
	}
	
	/**
	 * Realiza o update de um item de devolucao do fornecedor passando pelas validacoes necessarias
	 * @param itemDf
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void atualizar(SceItemDevolucaoFornecedor itemDf) throws BaseException {
		this.preAtualizar(itemDf);
		this.getSceItemDevolucaoFornecedorDAO().persistir(itemDf);
		this.posAtualizar(itemDf);
	}

	private SceItemDevolucaoFornecedorDAO getSceItemDevolucaoFornecedorDAO(){
		return sceItemDevolucaoFornecedorDAO;
	}
	
	private SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO(){
		return sceHistoricoProblemaMaterialDAO;
	}
	
	private SceItemNotaRecebimentoDAO getSceItemNotaRecebimentoDAO(){
		return sceItemNotaRecebimentoDAO;
	}
	
	private SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN() {
		return sceEstoqueAlmoxarifadoRN;
	}
	
	private ConfirmacaoDevolucaoON getConfirmacaoDevolucaoON() {
		return confirmacaoDevolucaoON;
	}
	
	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}

	private SceTipoMovimentosRN getSceTipoMovimentosRN() {
		return sceTipoMovimentosRN;
	}

	private SceMovimentoMaterialRN getSceMovimentoMaterialRN() {
		return sceMovimentoMaterialRN;
	}

	private SceItemDfHistoricoRN getSceItemDfHistoricoRN() {
		return sceItemDfHistoricoRN;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	private SceItemNotaRecebimentoDevolucaoFornecedorRN getSceItemNotaRecebimentoDevolucaoFornecedorRN() {
		return sceItemNotaRecebimentoDevolucaoFornecedorRN;
	}	
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return this.autFornecimentoFacade;
	}
}