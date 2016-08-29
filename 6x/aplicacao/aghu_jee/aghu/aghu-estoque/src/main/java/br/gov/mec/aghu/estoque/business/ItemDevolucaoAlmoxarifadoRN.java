package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceDevolucaoAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemDasDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDas;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio 
 * para Item de Devolução do Almoxarifado (DA)
 * 
 * @author diego.pacheco
 */
@Stateless
public class ItemDevolucaoAlmoxarifadoRN extends BaseBusiness {

private static final String MSG_ITEM_DA_NAO_INFORMADO = "Parametro itemDa não informado!";
@EJB
private SceMovimentoMaterialRN sceMovimentoMaterialRN;
@EJB
private SceTipoMovimentosRN sceTipoMovimentosRN;

private static final Log LOG = LogFactory.getLog(ItemDevolucaoAlmoxarifadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@Inject
private SceItemDasDAO sceItemDasDAO;

@Inject
private SceDevolucaoAlmoxarifadoDAO sceDevolucaoAlmoxarifadoDAO;

@Inject
private SceTipoMovimentosDAO sceTipoMovimentosDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6423939976598988768L;
	
	public enum ItemDevolucaoAlmoxarifadoRNExceptionCode implements BusinessExceptionCode {
		SCE_00292, SCE_00280, SCE_00573, ERRO_ESTOQUE_ALMOXARIFADO_NAO_CADASTRADO_OU_INATIVO;
	}	
	
	/**
	 * Trigger
	 * 
	 * ORADB: SCET_IDA_BRI
	 * 
	 * @param itemDaNew
	 * @throws BaseException
	 */
	public void executarBeforeInsertItemDevolucaoAlmoxarifado(SceItemDas itemDaNew, String nomeMicrocomputador) throws BaseException {
		if (itemDaNew == null) {
			throw new IllegalArgumentException("Parametro itemDaNew não informado!");
		}		
		
		/* 
		 * Verifica se estoque almoxarifado está ativo, se é igual
		 * ao almoxarifado do pai e busca ALM/MAT/FRN correspondentes
		 */
		verificarEstoqueAlmoxarifado(itemDaNew);
		
		// Verifica se unidade igual ao estoque
		verificarItemDevolucaoUnidadeMedida(itemDaNew, null);
		
		// Verifica se pai esta estornado
		verificarEstornoDevolucao(itemDaNew);
		
		// Gera movimento material para item de DA
		// -- Inicio
		AghParametros paramTipoMovimentoDocDa = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TMV_DOC_DA);
		List<SceTipoMovimento> listaTipoMovimentos = getSceTipoMovimentosDAO()
				.obterTipoMovimentoPorSeqDescricao(paramTipoMovimentoDocDa.getVlrNumerico().toString());
		
		SceTipoMovimento tipoMovimento = null;
		
		if (!listaTipoMovimentos.isEmpty()) {
			tipoMovimento = listaTipoMovimentos.get(0);
		}
		
		atualizarMovimentoMaterial(itemDaNew.getEstoqueAlmoxarifado().getAlmoxarifado(), 
				itemDaNew.getEstoqueAlmoxarifado().getMaterial(),
				itemDaNew.getEstoqueAlmoxarifado().getUnidadeMedida(), itemDaNew.getQuantidade(), null,
				Boolean.FALSE, getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(tipoMovimento), null,
				itemDaNew.getDevolucaoAlmoxarifado().getSeq(), null, null, 
				itemDaNew.getEstoqueAlmoxarifado().getFornecedor(), null, 
				itemDaNew.getDevolucaoAlmoxarifado().getCentroCusto(), null, null, null, nomeMicrocomputador);
		// -- Fim
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: SCEK_IDA_RN.RN_IDAP_VER_ALM_EAL
	 * 
	 * @param itemDa
	 * @throws ApplicationBusinessException
	 */
	public void verificarEstoqueAlmoxarifado(SceItemDas itemDa) throws ApplicationBusinessException {
		if (itemDa == null) {
			throw new IllegalArgumentException(MSG_ITEM_DA_NAO_INFORMADO);
		}	
		
		SceEstoqueAlmoxarifado estoqueAlmoxarifado = getSceEstoqueAlmoxarifadoDAO()
				.obterPorChavePrimaria(itemDa.getId().getEalSeq());
		
		if (estoqueAlmoxarifado == null || DominioSituacao.I.equals(estoqueAlmoxarifado.getIndSituacao())) {
			throw new ApplicationBusinessException(ItemDevolucaoAlmoxarifadoRNExceptionCode.SCE_00292);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: SCEK_IDA_RN.RN_IDAP_VER_UMD_ESTQ
	 * 
	 * @param itemDa
	 * @param matcodigo
	 * @throws ApplicationBusinessException
	 */
	public void verificarItemDevolucaoUnidadeMedida(SceItemDas itemDa, String matcodigo) 
			throws ApplicationBusinessException {
		
		if (itemDa == null) {
			throw new IllegalArgumentException(MSG_ITEM_DA_NAO_INFORMADO);
		}
		
		verificarDevolucaoUnidadeMedida(itemDa, matcodigo);
	}
	
	/**
	 * Procedure
	 * 
	 * @param itemDa
	 * @param matcodigo
	 */
	public void verificarDevolucaoUnidadeMedida(SceItemDas itemDa, String matcodigo) 
			throws ApplicationBusinessException {
		if (itemDa == null) {
			throw new IllegalArgumentException(MSG_ITEM_DA_NAO_INFORMADO);
		}
		SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO = getSceEstoqueAlmoxarifadoDAO();
		SceEstoqueAlmoxarifado estoqueAlmoxarifadoItemDa = itemDa.getEstoqueAlmoxarifado();
		SceEstoqueAlmoxarifado estoqueAlmoxarifadoUnidMedida = null;
		
		estoqueAlmoxarifadoUnidMedida = sceEstoqueAlmoxarifadoDAO
				.obterEstoqueAlmoxarifadoPorSeqEUmdCodigo(
						itemDa.getEstoqueAlmoxarifado().getSeq(),
						itemDa.getUnidadeMedida().getCodigo());
		
		List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = 
				sceEstoqueAlmoxarifadoDAO.buscarEstoqueAlmoxarifadoPorAlmSeqMatCodigoEFrnNumero(
						estoqueAlmoxarifadoItemDa.getAlmoxarifado().getSeq(),
						estoqueAlmoxarifadoItemDa.getMaterial().getCodigo(),
						estoqueAlmoxarifadoItemDa.getFornecedor().getNumero());
		
		if (matcodigo != null) {
			if (listaEstoqueAlmoxarifado.isEmpty()) {
				throw new ApplicationBusinessException(
						ItemDevolucaoAlmoxarifadoRNExceptionCode.ERRO_ESTOQUE_ALMOXARIFADO_NAO_CADASTRADO_OU_INATIVO,
						matcodigo,
						estoqueAlmoxarifadoItemDa.getAlmoxarifado().getSeq(),
						estoqueAlmoxarifadoItemDa.getFornecedor().getNumero());
			} else if (!listaEstoqueAlmoxarifado.get(0).getUnidadeMedida().getCodigo().equals(
					itemDa.getUnidadeMedida().getCodigo()) ) {
				 throw new ApplicationBusinessException(ItemDevolucaoAlmoxarifadoRNExceptionCode.SCE_00280);
			}
		} else if (estoqueAlmoxarifadoUnidMedida == null) {
			throw new ApplicationBusinessException(ItemDevolucaoAlmoxarifadoRNExceptionCode.SCE_00280);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: SCEK_IDA_RN.RN_IDAP_VER_ESTOR_DA
	 * 
	 * @param itemDa
	 * @throws ApplicationBusinessException
	 */
	public void verificarEstornoDevolucao(SceItemDas itemDa) throws ApplicationBusinessException {
		if (itemDa == null) {
			throw new IllegalArgumentException(MSG_ITEM_DA_NAO_INFORMADO);
		}
		SceDevolucaoAlmoxarifado devolucaoAlmoxarifado = 
				getSceDevolucaoAlmoxarifadoDAO().obterDevolucaoAlmoxarifadoEstorno(
						itemDa.getDevolucaoAlmoxarifado().getSeq());
		
		if (devolucaoAlmoxarifado != null) {
			throw new ApplicationBusinessException(ItemDevolucaoAlmoxarifadoRNExceptionCode.SCE_00573);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: SCEK_IDA_RN.RN_IDAP_ATU_MVTO
	 * 
	 * @param almoxarifado
	 * @param material
	 * @param unidadeMedida
	 * @param quantidade
	 * @param qtdeRequisitada
	 * @param indEstorno
	 * @param tipoMovimento
	 * @param tipoMovimentoDocumento
	 * @param nroDocGeracao
	 * @param itemDocGeracao
	 * @param historico
	 * @param fornecedor
	 * @param centroCustoRequisita
	 * @param centroCusto
	 * @param almoxarifadoComplemento
	 * @param valor
	 * @param nroDocRefere
	 * @throws BaseException
	 */
	public void atualizarMovimentoMaterial(SceAlmoxarifado almoxarifado, ScoMaterial material, ScoUnidadeMedida unidadeMedida, 
			Integer quantidade, Integer qtdeRequisitada, Boolean indEstorno, SceTipoMovimento tipoMovimento, 
			SceTipoMovimento tipoMovimentoDocumento, Integer nroDocGeracao, Short itemDocGeracao, String historico, 
			ScoFornecedor fornecedor, FccCentroCustos centroCustoRequisita, FccCentroCustos centroCusto, 
			SceAlmoxarifado almoxarifadoComplemento, BigDecimal valor, Integer nroDocRefere, String nomeMicrocomputador) throws BaseException {
		
		getSceMovimentoMaterialRN().atualizarMovimentoMaterial(
				almoxarifado, material, unidadeMedida, quantidade, qtdeRequisitada, indEstorno, 
				tipoMovimento, tipoMovimentoDocumento, nroDocGeracao, itemDocGeracao, historico, 
				fornecedor, centroCustoRequisita, centroCusto, almoxarifadoComplemento, valor, nroDocRefere, nomeMicrocomputador, true);
	}
	
	public void persistirItemDevolucaoAlmoxarifado(SceItemDas itemDevolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException{
		this.executarBeforeInsertItemDevolucaoAlmoxarifado(itemDevolucaoAlmoxarifado, nomeMicrocomputador);
		this.getSceItemDasDAO().persistir(itemDevolucaoAlmoxarifado);
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected SceDevolucaoAlmoxarifadoDAO getSceDevolucaoAlmoxarifadoDAO() {
		return sceDevolucaoAlmoxarifadoDAO;
	}
	
	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO() {
		return sceTipoMovimentosDAO;
	}
	
	protected SceItemDasDAO getSceItemDasDAO() {
		return sceItemDasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected SceTipoMovimentosRN getSceTipoMovimentosRN() {
		return sceTipoMovimentosRN;
	}
	
	protected SceMovimentoMaterialRN getSceMovimentoMaterialRN() {
		return sceMovimentoMaterialRN;
	}

}
