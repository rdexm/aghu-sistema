/**
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.ItensAutFornUpdateSCContrVO;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ItemAFAtualizaSCContratoON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(ItemAFAtualizaSCContratoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4068145530195414334L;
	
	 
	private Integer getTempoReposicaoContrato(ItensAutFornUpdateSCContrVO itemAutFornecedorVO,	SceAlmoxarifado sceAlmoxarifadoCentral) {
		Integer tempoReposicao = 0;

		if (itemAutFornecedorVO.getEgrClassificacaoAbc() != null) {
			if (itemAutFornecedorVO.getEgrClassificacaoAbc().equals(DominioClassifABC.A)) {
				tempoReposicao = sceAlmoxarifadoCentral.getTempoReposicaoContrClassA();
			} else if (itemAutFornecedorVO.getEgrClassificacaoAbc().equals(	DominioClassifABC.B)) {
				tempoReposicao = sceAlmoxarifadoCentral.getTempoReposicaoContrClassB();
			} else if (itemAutFornecedorVO.getEgrClassificacaoAbc().equals(	DominioClassifABC.C)) {
				tempoReposicao = sceAlmoxarifadoCentral.getTempoReposicaoContrClassC();
			}
		} else {
			tempoReposicao = sceAlmoxarifadoCentral.getTempoReposicaoContrClassC();
		}
		return tempoReposicao;
	}

	private Integer getVoltarTempoReposicaoContratoClassC(ItensAutFornUpdateSCContrVO itemAutFornecedorVO,  SceAlmoxarifado sceAlmoxarifadoCentral) throws ApplicationBusinessException{
		
		Integer tempoReposicao = 0;
		AghParametros parametroGrupoMaterialMedicamento = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_GR_MAT_MEDIC);

		AghParametros parametroTempoReposicaoPadrao = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TEMPO_REPOSICAO);
		
		if (itemAutFornecedorVO.getMatGrupoCodigo().equals(parametroGrupoMaterialMedicamento.getVlrNumerico().intValue())) {
			tempoReposicao = parametroTempoReposicaoPadrao.getVlrNumerico().intValue();
		} else {
			tempoReposicao = sceAlmoxarifadoCentral.getTempoReposicaoClassC();
		}
		return tempoReposicao;
	}
	
	private Integer getVoltarTempoReposicaoContrato(ItensAutFornUpdateSCContrVO itemAutFornecedorVO, SceAlmoxarifado sceAlmoxarifadoCentral) throws ApplicationBusinessException {

		Integer tempoReposicao = 0;		

		if (itemAutFornecedorVO.getEgrClassificacaoAbc() != null) {
			if (itemAutFornecedorVO.getEgrClassificacaoAbc().equals(DominioClassifABC.A)) {
				tempoReposicao = sceAlmoxarifadoCentral.getTempoReposicaoClassA();
			} else if (itemAutFornecedorVO.getEgrClassificacaoAbc().equals(DominioClassifABC.B)) {
				tempoReposicao = sceAlmoxarifadoCentral.getTempoReposicaoClassB();
			} else if (itemAutFornecedorVO.getEgrClassificacaoAbc().equals(DominioClassifABC.C)) {
              
				getVoltarTempoReposicaoContratoClassC(itemAutFornecedorVO, sceAlmoxarifadoCentral);
			}
		} else {
			 getVoltarTempoReposicaoContratoClassC(itemAutFornecedorVO, sceAlmoxarifadoCentral);
		}
		return tempoReposicao;
	}
	
	private Integer calculoTempoReposicao(Boolean marcadoContrato, ItensAutFornUpdateSCContrVO itemAutFornecedorVO, SceAlmoxarifado sceAlmoxarifadoCentral) throws ApplicationBusinessException{
		
		Integer tempoReposicao = 0;
		marcadoContrato = (marcadoContrato != null) ? marcadoContrato : false;
		
		if (marcadoContrato) {
			tempoReposicao = getTempoReposicaoContrato(itemAutFornecedorVO, sceAlmoxarifadoCentral);
		}
		else if (!marcadoContrato) {
			tempoReposicao = getVoltarTempoReposicaoContrato(itemAutFornecedorVO, sceAlmoxarifadoCentral);
		}
		
		return tempoReposicao;
	}
	
	/*** RN20 - PLL - ATUALIZA_SC_CONTRATO
	 *   RN21 - PLL - ATUALIZA_SC_CONTRATO
	 * @param itemAutorizacaoForn
	 * @throws BaseException
	 */
	public void atualizarSCContrato(ItensAutFornVO item ) throws BaseException{
		    
		AghParametros parametroDataCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		
		AghParametros parametroFornecedorPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		
		ScoItemAutorizacaoForn itemAutorizacaoForn = this.getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(item.getAfnNumero(), item.getNumero());
		
		String nomeMicrocomputador = item.getNomeMicrocomputador();
		 
		this.logInfo("atualizarSCContrato");		 
		
		List<ItensAutFornUpdateSCContrVO> listaAutFornUpdateSCContrVO =  getEstoqueFacade().pesquisarFaseSolicitacaoItemAF(itemAutorizacaoForn.getId(), parametroDataCompetencia.getVlrData(), parametroFornecedorPadrao.getVlrNumerico().intValue());
	    for (ItensAutFornUpdateSCContrVO itemAutFormUpdate : listaAutFornUpdateSCContrVO) {
	    	
	    	SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = getEstoqueFacade().obterSceEstoqueAlmoxarifadoPorChavePrimaria(itemAutFormUpdate.getEstAlseq());
	    			    	
			if (item.getIndContrato()) {
				ScoSolicitacaoDeCompra solicitacaoCompra;

				if (itemAutFormUpdate.getMatAlmoxSeq().equals(sceEstoqueAlmoxarifado.getAlmoxarifado().getSeq())) {
					solicitacaoCompra = this.getSolicitacaoComprasFacade().obterSolicitacaoDeCompra(itemAutFormUpdate.getSlcNumeroSC());

				} else {
					solicitacaoCompra = null;
				}
                
				
				sceEstoqueAlmoxarifado.setSolicitacaoCompra(solicitacaoCompra);

				ScoSolicitacaoDeCompra solicitacaoCompraUP = this.getSolicitacaoComprasFacade().obterSolicitacaoDeCompra(itemAutFormUpdate.getSlcNumeroSC());
				ScoSolicitacaoDeCompra solicitacaoCompraUPClone = this.getSolicitacaoComprasFacade().clonarSolicitacaoDeCompra(solicitacaoCompraUP);
				
				solicitacaoCompraUP.setExclusao(false);
				solicitacaoCompraUP.setDtExclusao(null);
				solicitacaoCompraUP.setMotivoExclusao(null);
				solicitacaoCompraUP.setServidorExclusao(null);					
				this.getSolicitacaoComprasFacade().atualizarScoSolicitacaoDeCompra(solicitacaoCompraUP, solicitacaoCompraUPClone);
			}
			else {
				sceEstoqueAlmoxarifado.setSolicitacaoCompra(null);
			}
	    	
	    	AghParametros parametroAlmoxarifadoCentral = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL);		    	
	    	
	    	SceAlmoxarifado sceAlmoxarifadoCentral = getEstoqueFacade().obterAlmoxarifadoPorSeq(parametroAlmoxarifadoCentral.getVlrNumerico().shortValue());		    	
	    	
	    	Integer tempoReposicao = calculoTempoReposicao(item.getIndContrato(), itemAutFormUpdate, sceAlmoxarifadoCentral);
	    	
	    	if (itemAutFormUpdate.getMatAlmoxSeq().equals(sceEstoqueAlmoxarifado.getAlmoxarifado().getSeq())){		    		
	    		sceEstoqueAlmoxarifado.setTempoReposicao(tempoReposicao);		    			    		
	    	}
	    	
	    	if (! itemAutFormUpdate.getMatAlmoxSeq().equals(parametroAlmoxarifadoCentral.getVlrNumerico().shortValue())){		    	
	    		SceEstoqueAlmoxarifado sceEstoqueAlmoxarifadoCentral = getEstoqueFacade().obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(parametroAlmoxarifadoCentral.getVlrNumerico().shortValue(), itemAutFormUpdate.getSlcCodigoMaterial(), parametroFornecedorPadrao.getVlrNumerico().intValue());
	    		sceEstoqueAlmoxarifadoCentral.setTempoReposicao(tempoReposicao);
	    		this.getEstoqueFacade().atualizarEstoqueAlmoxarifado(sceEstoqueAlmoxarifadoCentral, nomeMicrocomputador);
	    	}
	    	
	    	this.getEstoqueFacade().atualizarEstoqueAlmoxarifado(sceEstoqueAlmoxarifado, nomeMicrocomputador);	
	    			    	
	    	Calendar calendar = Calendar.getInstance();
			calendar.setTime(parametroDataCompetencia.getVlrData());
			calendar.add(Calendar.MONTH, - 1);
			
			Date dataCompetencia = calendar.getTime();
	    	
	    	this.getEstoqueFacade().atualizarPontoPedido(dataCompetencia, itemAutFormUpdate.getSlcCodigoMaterial());				
		}				
	}
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}	
	
}
