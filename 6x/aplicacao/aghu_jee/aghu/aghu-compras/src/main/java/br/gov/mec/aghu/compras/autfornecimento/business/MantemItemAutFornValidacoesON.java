/**
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.business;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.vo.ItensAutFornUpdateSCContrVO;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class MantemItemAutFornValidacoesON extends BaseBusiness{


@EJB
private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;

private static final Log LOG = LogFactory.getLog(MantemItemAutFornValidacoesON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

@EJB
private IEstoqueFacade estoqueFacade;

@Inject
private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4068119530195414335L;
	
	
	
	public enum MantemItemAutFornValidacoesONExceptionCode implements
	BusinessExceptionCode {ERRO_FATOR_CONVERSAO_MSG01, ERRO_QTDE_SOLICITADA_MSG02, ERRO_QTDE_SOLICITADA_RECEBIDA_MSG03,
		                  ERRO_VALOR_UNITARIO_MSG04, ERRO_FATOR_CONVERSAO_FORN_MSG05, ERRO_PERC_DESC_ITEM_MSG06, ERRO_PERC_DESC_MSG07,
		                  ERRO_PERC_ACRES_ITEM_MSG08, ERRO_PERC_ACRES_MSG09,ERRO_PERC_VAR_PRECO_MSG10, ERRO_PERC_IPI_MSG11,
		                  ERRO_PERC_DESCONTO_MSG13, ERRO_PERC_ACRES_MSG14,MENSAGEM_ERRO_ORCAMENTARIO_ALTERACAO_AF,
		                  ERRO_EXISTE_CONTRATO_MSG13, ERRO_NAO_EXISTE_FASE_COMPETENCIA_MSG14, ERRO_NAO_EXISTE_LOCAL_ESTOQUE_MSG16,
		                  MSG_EXISTE_SALDO_MSG17, MSG_EXISTE_SALDO_MSG18,ERRO_VLR_UNIT_ITEM_MSG18, ERRO_MATERIAL_CONSIGNADO_MSG19,
		                  ERRO_MATERIAL_CONSIG_SALDO_MSG20,ERRO_MATERIAL_NAO_CONSIG_EAL_MSG21, ERRO_MATERIAL_CONSIG_SALDO_FAT_MSG22,
		                  ERRO_FASE_NAO_EXISTE_MSG23, ERRO_EXCLUSAO_ESTORNO_MSG24, ERRO_MENSAGEM_MAT_CONTRATO, MSG_DESMARCOU_IND_CONSIGNADO,
		                  ERRO_VALIDACAO_UNIDADE_FORNECEDOR_IGUAL, ERRO_VALIDACAO_UNIDADE_FORNECEDOR_DIFERENTE,
		                  MENSAGEM_EXCLUSAO_ITEMAF_COMRP}
	
	public void validaMaiorZero(Double valor, String mensagem, Integer numeroItem) throws ApplicationBusinessException {
		if (valor != null) {
			if (valor <= 0) {
				throw new ApplicationBusinessException(
						MantemItemAutFornValidacoesONExceptionCode.valueOf(mensagem),
						numeroItem.toString());
			}
		}
	}	
	
	public void validaMaiorZeroMenorCem(Double valor, String mensagem, Integer numeroItem) throws ApplicationBusinessException {
		if (valor != null) {
			if (!(valor >= 0 && valor <= 100)) {
				throw new ApplicationBusinessException(
						MantemItemAutFornValidacoesONExceptionCode.valueOf(mensagem),
						numeroItem.toString());
			}
		}
	}	
	
	public void validaSolicitadaRecebida(Integer qtdeSolicitada, Integer qtdeRecebida, String mensagem, Integer numeroItem) throws ApplicationBusinessException {
		if (qtdeSolicitada != null && qtdeRecebida != null) {
			if (qtdeSolicitada < qtdeRecebida) {
				throw new ApplicationBusinessException(
						MantemItemAutFornValidacoesONExceptionCode.valueOf(mensagem),
						numeroItem.toString());
			}
		}
	}	
	
	public void validaPercentualAcrescimoDesconto(Double percentualItem, Double percentual, String mensagem, Integer numeroItem) throws ApplicationBusinessException {
		if (percentualItem != null && percentual != null) {
			if ((percentualItem + percentual) > 100) {
				throw new ApplicationBusinessException(
						MantemItemAutFornValidacoesONExceptionCode.valueOf(mensagem),
						numeroItem.toString());

			}
		}	
	}	
		
	public void validarParametrosOrcamentariosItensAf(List<ScoItemAutorizacaoForn> listaItens) throws ApplicationBusinessException {
		for (ScoItemAutorizacaoForn item : listaItens) {
			for (ScoFaseSolicitacao fase : item.getScoFaseSolicitacao()) {
				if (fase.getSolicitacaoDeCompra() != null) {
					if (!validarVerbaGestaoScParam(fase.getSolicitacaoDeCompra()) || !validarNaturezaScParam(fase.getSolicitacaoDeCompra())) {
						throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.MENSAGEM_ERRO_ORCAMENTARIO_ALTERACAO_AF);
					}
				} else {
					if (!validarVerbaGestaoSsParam(fase.getSolicitacaoServico()) || !validarNaturezaSsParam(fase.getSolicitacaoServico())) {
						throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.MENSAGEM_ERRO_ORCAMENTARIO_ALTERACAO_AF);
					}
				}
			}
		}
	}
	
	/*** RN 16 - PLL - VERIFICA_EXIST_CONTRATO 
	 *   RN 17 - PLL - ATUALIZA_SC_CONTRATO
	 *   RN 19 - PLL - VERIFICA_EXIST_CONTRATO ***/
	public void validarScContrato(ItensAutFornVO itemAutorizacaoForn) throws BaseException {		
		
		if (itemAutorizacaoForn.getIndContrato()) {
			AghParametros parametroGrupoMatOrtProt = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_GRP_MAT_ORT_PROT);
			
			ScoItemAutorizacaoFornId itemAutorizacaoFornId = new ScoItemAutorizacaoFornId(); 
			itemAutorizacaoFornId.setAfnNumero(itemAutorizacaoForn.getAfnNumero());
			itemAutorizacaoFornId.setNumero(itemAutorizacaoForn.getNumero());
			
						
			Object[] contrato = getScoItemAutorizacaoFornDAO().pesquisaContrato(itemAutorizacaoFornId, parametroGrupoMatOrtProt.getVlrNumerico().intValue());
			
			if (contrato != null){
				//Integer codigoGrupoMaterial = (Integer) contrato[0];
				Integer codigoMaterial = (Integer) contrato[1];
				Integer numeroSC = (Integer) contrato[2];
				//Short almoxarifadoSeq = (Short) contrato[3];
				Integer numeroAF = (Integer) contrato[4];
				Short   nroComplementoAF = (Short) contrato[5];
				Short   itlNumero = (Short) contrato[6];
				Double  ValorUnitarioItemAF = (Double) contrato[7];				
				
				
				throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_EXISTE_CONTRATO_MSG13, codigoMaterial, numeroAF, nroComplementoAF, itlNumero, ValorUnitarioItemAF, numeroSC);
				
			}
			
			AghParametros parametroDataCompetencia = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
			
			AghParametros parametroFornecedorPadrao = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			
			List<ItensAutFornUpdateSCContrVO> listaAutFornUpdateSCContrVO =  getEstoqueFacade().pesquisarFaseSolicitacaoItemAF(itemAutorizacaoFornId, parametroDataCompetencia.getVlrData(), parametroFornecedorPadrao.getVlrNumerico().intValue());
			
		    if (listaAutFornUpdateSCContrVO == null ||
		    	listaAutFornUpdateSCContrVO.size() == 0){	
		    	SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");		    	
		    	throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_NAO_EXISTE_FASE_COMPETENCIA_MSG14, formatador.format(parametroDataCompetencia.getVlrData()));		    	
		    }	
		    
		    ItensAutFornUpdateSCContrVO itemAutFornUpdateSCContrVO = listaAutFornUpdateSCContrVO.get(0);
		    
		    if (itemAutFornUpdateSCContrVO != null){
		    	if (itemAutFornUpdateSCContrVO.getEstAlseq() == null){
		    		throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_NAO_EXISTE_LOCAL_ESTOQUE_MSG16, itemAutFornUpdateSCContrVO.getMatAlmoxSeq());		
		    	}
		    }
		}
	}
	
	/***
	 * RN29 - 
	 */
	public void validaValorUnitarioItemAF(ItensAutFornVO itemAutFornVO)  throws ApplicationBusinessException{
		
		List<ScoFaseSolicitacao> listaFases = this.getScoFaseSolicitacaoDAO().pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(itemAutFornVO.getAfnNumero(), itemAutFornVO.getNumero(), null);
		if (listaFases != null && !listaFases.isEmpty()) {
			if (listaFases.get(0).getTipo().equals(DominioTipoFaseSolicitacao.S)){
				if (itemAutFornVO.getValorEfetivado() >= (itemAutFornVO.getQtdeSolicitada() * itemAutFornVO.getValorUnitario())){
					throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_VLR_UNIT_ITEM_MSG18);
				}
			}
		}
	}
	
	/***
	 * RN 31, RN 32, RN 33, RN 34, RN 35
	 * @throws BaseException 
	 */
	public void validaIndConsignadoItemAF(ItensAutFornVO itemAutFornVO) throws BaseException{
		 
		ScoItemAutorizacaoForn scoItemAutorizacaoForn = this.getScoItemAutorizacaoFornRN().obterItemAfOriginal(itemAutFornVO);
		
		if (scoItemAutorizacaoForn.getIndConsignado() &&
			!itemAutFornVO.getIndConsignado()){
			boolean existeMaterialConsignado = this.getEstoqueFacade().existeEstoqueAlmoxarifadoItemAFConsignado(itemAutFornVO.getAfnNumero(), itemAutFornVO.getNumero());
			/*** RN 31 ***/
			if (!existeMaterialConsignado){
				throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_MATERIAL_CONSIGNADO_MSG19);				
				
			}
			
			Integer qtdeSaldo = this.getEstoqueFacade().obterSaldosEstoqueAlmoxarifadoItemAFConsignado(itemAutFornVO.getAfnNumero(), itemAutFornVO.getNumero());
			/*** RN 32 ***/
			if (qtdeSaldo > 0){
				throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_MATERIAL_CONSIG_SALDO_MSG20);
			}
			/** RN33 ***/
			Integer qtdeConsignada = this.getEstoqueFacade().obterQtdeConsignadaEstoqueGeralItemAF(itemAutFornVO.getAfnNumero(), itemAutFornVO.getNumero());
			if (qtdeConsignada < 0){
				throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_MATERIAL_NAO_CONSIG_EAL_MSG21);
			}
			/*** RN34 ***/
			if (qtdeConsignada > 0){
				throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_MATERIAL_CONSIG_SALDO_FAT_MSG22);
			}
			
		}
		else if (itemAutFornVO.getIndConsignado()){
			/** RN 35 ***/
			List<ScoFaseSolicitacao> listaFases = this.getScoFaseSolicitacaoDAO().pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(itemAutFornVO.getAfnNumero(), itemAutFornVO.getNumero(), null);
			if (listaFases == null || listaFases.isEmpty()) {
					throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_FASE_NAO_EXISTE_MSG23);
			}
		}
		
	}	 
	
	/*** RN 39 ****/
	public void validarExclusaoEstornoItemAF(ItensAutFornVO itemAutFornVO) throws ApplicationBusinessException{
		if (itemAutFornVO.getQtdeRp() != null && itemAutFornVO.getQtdeRp().getQuantidade() > 0) {
			throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.MENSAGEM_EXCLUSAO_ITEMAF_COMRP);
		}

		if (itemAutFornVO.getIndExclusao() &&
			itemAutFornVO.getIndEstorno()){
			throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_EXCLUSAO_ESTORNO_MSG24);
		}
	} 
	
	public void validaIndConsignado(ItensAutFornVO itemAutorizacaoForn) throws ApplicationBusinessException{
		try {
			this.getScoItemAutorizacaoFornRN().validaIndConsignado(itemAutorizacaoForn.getIndConsignado(), itemAutorizacaoForn.getIndContrato());			
		} catch (ApplicationBusinessException e) {
			itemAutorizacaoForn.setIndConsignado(false);			
			throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.MSG_DESMARCOU_IND_CONSIGNADO);
		}
	}
	
	public void validaUnidadeFatorConversao(ItensAutFornVO itemAutorizacaoForn) throws ApplicationBusinessException{
		if (itemAutorizacaoForn.getUmdCodigoForn() != null && itemAutorizacaoForn.getUnidadeMedida() != null){ 
				if (itemAutorizacaoForn.getUmdCodigoForn().equals(itemAutorizacaoForn.getUnidadeMedida()) && itemAutorizacaoForn.getFatorConversaoForn() != 1){
					throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_VALIDACAO_UNIDADE_FORNECEDOR_IGUAL);
				}
							
				if (!itemAutorizacaoForn.getUmdCodigoForn().equals(itemAutorizacaoForn.getUnidadeMedida()) && itemAutorizacaoForn.getFatorConversaoForn() == 1){
					throw new ApplicationBusinessException(MantemItemAutFornValidacoesONExceptionCode.ERRO_VALIDACAO_UNIDADE_FORNECEDOR_DIFERENTE);
				}
			}		
	}
	
	private Boolean validarVerbaGestaoScParam(ScoSolicitacaoDeCompra sc) {
		return this.getCadastrosBasicosOrcamentoFacade().isVerbaGestaoValidScParam(sc.getMaterial(), sc.getCentroCusto(), sc.getValorTotal(),
				sc.getVerbaGestao());
	}
	
	private Boolean validarNaturezaScParam(ScoSolicitacaoDeCompra sc) {
		return this.getCadastrosBasicosOrcamentoFacade().isNaturezaValidScParam(sc.getMaterial(), sc.getCentroCusto(), sc.getValorTotal(),
				sc.getNaturezaDespesa());
	}

	private Boolean validarNaturezaSsParam(ScoSolicitacaoServico ss) {
		return this.getCadastrosBasicosOrcamentoFacade().isNaturezaValidSsParam(ss.getServico(), ss.getNaturezaDespesa());
	}

	private Boolean validarVerbaGestaoSsParam(ScoSolicitacaoServico ss) {
		return this.getCadastrosBasicosOrcamentoFacade().isVerbaGestaoValidSsParam(ss.getServico(), ss.getVerbaGestao());
	}
	
	protected ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return this.cadastrosBasicosOrcamentoFacade;
	}	
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	protected ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN() {
		return scoItemAutorizacaoFornRN;
	}
	
}
