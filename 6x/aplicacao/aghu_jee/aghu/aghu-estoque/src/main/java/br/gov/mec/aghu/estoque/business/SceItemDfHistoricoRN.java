package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceItemDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceItemDfHistoricoDAO;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceItemDfHistorico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceItemDfHistoricoRN extends BaseBusiness{

@EJB
private SceHistoricoProblemaMaterialRN sceHistoricoProblemaMaterialRN;

private static final Log LOG = LogFactory.getLog(SceItemDfHistoricoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemDevolucaoFornecedorDAO sceItemDevolucaoFornecedorDAO;

@Inject
private SceItemDfHistoricoDAO sceItemDfHistoricoDAO;

@Inject
private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;

@Inject
private SceDevolucaoFornecedorDAO sceDevolucaoFornecedorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8910932375236591327L;

	public enum SceItemDfHistoricoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_QTDE_IDFH_MAIOR_IDF,
		MENSAGEM_HISTORICO_PROBLEMA_NAO_ENCONTRADO, MENSAGEM_HISTORICO_MATERIAL_BLOQUEADO_INSUFICIENTE,
		MENSAGEM_DEVOLUCAO_NAO_ENCONTRADO, SCE_00764, SCE_00765, MENSAGEM_TIPO_MOVIMENTO_NAO_ENCONTRADO;
	}

	/**
	 * @ORADB SCEK_IDH_RN.RN_IDHP_VER_QTDE_IDF 
	 * VERIFICA SE SOMATORIO DA QTDE DOS HISTÓRICOS DO MESMO ITEM DF NÃO É SUPERIOR QUE A QUANTIDADE DESTE ULTIMO, BUSCA EAL_SEQ DO ITEM DF
	 * @param seqDfs
	 * @param nroItem
	 * @param qtde
	 * @param itemDfHistorico
	 * @throws ApplicationBusinessException
	 */
	private void validarSomatorioHistoricosIgualItemDf(Integer seqDfs, Integer nroItem, Integer qtde, SceItemDfHistorico itemDfHistorico) throws ApplicationBusinessException {
		
		// na package original (oracle) ele verifica se existe item_df na base. como ainda estamos dentro do pre-insert
		// do item_df, ele nao existe neste ponto no java.
		// outro detalhe eh o teste se existe o item_dfh, que tambem nao eh possivel fazer neste ponto pelo simples motivo
		// que estamos no pre-insert do mesmo.
		
		Integer qtdIdf = (Integer) CoreUtil.nvl(this.getSceItemDevolucaoFornecedorDAO().somarQuantidadePorItemDevolucaoFornecedor(seqDfs, nroItem), 0);
		Integer qtdIdh = (Integer) CoreUtil.nvl(this.getSceItemDfHistoricoDAO().somarQuantidadePorHistoricoItemDevolucaoFornecedor(seqDfs, nroItem), 0);
	
		if ((qtdIdh + qtde) > qtdIdf) {
			throw new ApplicationBusinessException(SceItemDfHistoricoRNExceptionCode.MENSAGEM_QTDE_IDFH_MAIOR_IDF);
		}
	}
	
	/**
	 * VERIFICA SE A QUANTIDADE DO HISTÓRICO PROBLEMA NÃO É SUPERIOR AO SOMATÓRIO DOS HISTÓRICOS PROBLEMAS DO REFERIDO MATERIAL PROBLEMA E EAL_SEQ DO ITEM
	 * @ORADB SCEK_IDH_RN.RN_IDHP_VER_QTDE_HPM
	 * @param itemDfHistorico
	 * @param qtde
	 * @throws ApplicationBusinessException
	 */
	private void validarSomatorioHistoricoMenorItens(SceItemDfHistorico itemDfHistorico, Integer qtde) throws ApplicationBusinessException {
		SceHistoricoProblemaMaterial histProblema = this.getSceHistoricoProblemaMaterialDAO().obterPorChavePrimaria(itemDfHistorico.getId().getHpmSeq());
		
		if (histProblema == null) {
			throw new ApplicationBusinessException(SceItemDfHistoricoRNExceptionCode.MENSAGEM_HISTORICO_PROBLEMA_NAO_ENCONTRADO);
		}
		
		Integer qtdProbTot = this.getSceHistoricoProblemaMaterialDAO().obterQtdeProblemaPorCodMaterial(null, itemDfHistorico.getSceItemDevolucaoFornecedor().getEalSeq(), itemDfHistorico.getId().getHpmSeq());
		
		if (qtde > qtdProbTot) {
			throw new ApplicationBusinessException(SceItemDfHistoricoRNExceptionCode.MENSAGEM_HISTORICO_MATERIAL_BLOQUEADO_INSUFICIENTE);
		}
	}
	
	/**
	 * VERIFICA SE DF ESTORNADA OU GERADA
	 * @ORADB SCEK_IDH_RN.RN_IDHP_VER_SIT_DF
	 * @param itemDfHistorico
	 * @throws ApplicationBusinessException
	 */
	private void validarDevolucaoFornecedorEstornada(SceItemDfHistorico itemDfHistorico) throws ApplicationBusinessException {
		SceDevolucaoFornecedor devolucaoFornecedor = this.getSceDevolucaoFornecedorDAO().obterPorChavePrimaria(itemDfHistorico.getId().getIdfDfsSeq());
		
		if (devolucaoFornecedor == null) {
			throw new ApplicationBusinessException(SceItemDfHistoricoRNExceptionCode.MENSAGEM_DEVOLUCAO_NAO_ENCONTRADO);
		}
		
		if (devolucaoFornecedor.getIndEstorno()) {
			throw new ApplicationBusinessException(SceItemDfHistoricoRNExceptionCode.SCE_00764);
		}
		
		if (devolucaoFornecedor.getIndGerado().equals(DominioSimNao.S)) {
			throw new ApplicationBusinessException(SceItemDfHistoricoRNExceptionCode.SCE_00765);
		}
	}
	
	/**
	 * ATUALIZA QUANTIDADE DF NA HISTORICO PROBL MATERIAL
	 * @ORADB SCEK_IDH_RN.RN_IDHP_ATU_HPM
	 * @param itemDfHistorico
	 * @param qtde
	 * @throws BaseException 
	 */
	private void atualizarQuantidadeDf(SceItemDfHistorico itemDfHistorico, Integer qtde) throws BaseException {
		DominioIndOperacaoBasica indOperacaoBasica = this.getSceDevolucaoFornecedorDAO().obterOperacaoBasicaNumeroDevolucaoFornecedor(itemDfHistorico.getId().getIdfDfsSeq());
		
		if (indOperacaoBasica == null) {
			throw new ApplicationBusinessException(SceItemDfHistoricoRNExceptionCode.MENSAGEM_TIPO_MOVIMENTO_NAO_ENCONTRADO);
		}
		
		SceHistoricoProblemaMaterial historicoProblema = this.getSceHistoricoProblemaMaterialDAO().obterPorChavePrimaria(itemDfHistorico.getSceHistoricoProblemaMaterial().getSeq());
		
		if (indOperacaoBasica.equals(DominioIndOperacaoBasica.DB)) {
			historicoProblema.setQtdeDf(historicoProblema.getQtdeDf()+qtde);
		} else if (indOperacaoBasica.equals(DominioIndOperacaoBasica.CR)) {
			historicoProblema.setQtdeDf(historicoProblema.getQtdeDf()-qtde);
		}
		
		this.getSceHistoricoProblemaMaterialRN().atualizar(historicoProblema, true);
	}
	
	/**
	 * @ORADB SCET_IDH_BRI
	 * Processos e validacoes executados antes da insercao de um relacionamento itemDf x historico
	 * @param itemDfHistorico
	 * @throws BaseException
	 */
	private void preInserir(SceItemDfHistorico itemDfHistorico) throws BaseException {
		this.validarSomatorioHistoricosIgualItemDf(itemDfHistorico.getSceItemDevolucaoFornecedor().getId().getDfsSeq(), 
				itemDfHistorico.getSceItemDevolucaoFornecedor().getId().getNumero(), itemDfHistorico.getQuantidade(), itemDfHistorico);
	
		this.validarSomatorioHistoricoMenorItens(itemDfHistorico, itemDfHistorico.getQuantidade());
		this.validarDevolucaoFornecedorEstornada(itemDfHistorico);
		this.atualizarQuantidadeDf(itemDfHistorico, itemDfHistorico.getQuantidade());
	}
	
	/**
	 * Processos e validacoes executados apos da insercao de um relacionamento itemDf x historico
	 * @param itemDfHistorico
	 * @throws BaseException
	 */
	private void posInserir(SceItemDfHistorico itemDfHistorico) throws BaseException {
		//
	}
	
	/**
	 * Realiza a insercao de um relacionamento itemDf x historico fazendo as validacoes necessarias
	 * @param itemDfHistorico
	 * @param flush
	 * @throws BaseException
	 */
	public void inserir(SceItemDfHistorico itemDfHistorico, Boolean flush) throws BaseException {
		this.preInserir(itemDfHistorico);
		this.getSceItemDfHistoricoDAO().persistir(itemDfHistorico);
		
		if (flush) {
			this.getSceItemDfHistoricoDAO().flush();
		}
		this.posInserir(itemDfHistorico);
	}

	/**
	 * Processos e validacoes executados antes da remocao de um relacionamento itemDf x historico
	 * @param itemDfHistorico
	 * @throws BaseException
	 */
	private void preRemover(SceItemDfHistorico itemDfHistorico) throws BaseException {
		//
	}
	
	/**
	 * Processos e validacoes executados apos a remocao de um relacionamento itemDf x historico
	 * @param itemDfHistorico
	 * @throws BaseException
	 */
	private void posRemover(SceItemDfHistorico itemDfHistorico) throws BaseException {
		//
	}
	
	/**
	 * Realiza a exclusao de um relacionamento itemDf x historico fazendo as validacoes necessarias
	 * @param itemDfHistorico
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void remover(SceItemDfHistorico itemDfHistorico) throws BaseException {
		this.preRemover(itemDfHistorico);
		this.getSceItemDfHistoricoDAO().remover(itemDfHistorico);
		this.posRemover(itemDfHistorico);
	}
	
	/**
	 * Processos e validacoes executados antes da alteracao de um relacionamento itemDf x historico
	 * @param itemDfHistorico
	 * @throws BaseException
	 */
	private void preAtualizar(SceItemDfHistorico itemDfHistorico) throws BaseException {
		//
	}
	
	/**
	 * Processos e validacoes executados apos a alteracao de um relacionamento itemDf x historico
	 * @param itemDfHistorico
	 * @throws BaseException
	 */
	private void posAtualizar(SceItemDfHistorico itemDfHistorico) throws BaseException {
		//
	}
	
	/**
	 * Realiza o update de um relacionamento itemDf x historico passando pelas validacoes necessarias
	 * @param itemDfHistorico
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void atualizar(SceItemDfHistorico itemDfHistorico) throws BaseException {
		this.preAtualizar(itemDfHistorico);
		this.getSceItemDfHistoricoDAO().persistir(itemDfHistorico);
		this.posAtualizar(itemDfHistorico);
	}

	private SceItemDfHistoricoDAO getSceItemDfHistoricoDAO() {
		return sceItemDfHistoricoDAO;
	}
	
	private SceItemDevolucaoFornecedorDAO getSceItemDevolucaoFornecedorDAO() {
		return sceItemDevolucaoFornecedorDAO;
	}
	
	private SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO() {
		return sceHistoricoProblemaMaterialDAO;
	}
	
	private SceDevolucaoFornecedorDAO getSceDevolucaoFornecedorDAO() {
		return sceDevolucaoFornecedorDAO;
	}
	
	private SceHistoricoProblemaMaterialRN getSceHistoricoProblemaMaterialRN() {
		return sceHistoricoProblemaMaterialRN;
	}
}

