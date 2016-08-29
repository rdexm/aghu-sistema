/**
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ItemAFGerarSCON extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(ItemAFGerarSCON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4168119530195414334L;
	
	
   /** RN 26 - PLL PROCESSA_SOLIC_COMPRA 
 * @throws BaseException ***/	
   public void processarSolicitacaoCompra(ItensAutFornVO  itemAutorizacaoForn) throws BaseException{
		
         List<ScoFaseSolicitacao> listaFaseSolicitacao = itemAutorizacaoForn.getScoFaseSolicitacao();
         Integer qtdeSolicitacao = itemAutorizacaoForn.getQtdeSolicitada() - itemAutorizacaoForn.getQtdeRecebida();
         
        /* this.logInfo("processaSolicitacaoCompra 1");
         
         ScoSolicitacaoDeCompra solCompra = this.getSolicitacaoComprasFacade().obterSolicitacaoDeCompra(179778);
         this.getScoSolicitacoesDeComprasDAO().desatachar(solCompra);
         gerarNovaSC(solCompra, new Long(2), null);
         this.logInfo("processaSolicitacaoCompra 2 ");*/         
         
         this.logInfo("processaSolicitacaoCompra");
		
		if (listaFaseSolicitacao != null &&
			!listaFaseSolicitacao.isEmpty()){
			if (listaFaseSolicitacao.size() == 1){				
				gerarNovaSC(listaFaseSolicitacao.get(0).getSolicitacaoDeCompra(), qtdeSolicitacao.longValue(), null);
			}
			else {				
				AghParametros parametroAlmoxarifadoCentral = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL);				
				for (ScoFaseSolicitacao faseSolicitacao: listaFaseSolicitacao){
					
					// RN 26 - PLL DISP_BLOQ_PROBL INICIO 
				      Integer codigoMaterial =  faseSolicitacao.getSolicitacaoDeCompra().getMaterial().getCodigo();
				      Integer qtdeDisponivel = 0;
				      Integer qtdeBloqueada = 0;
				      Integer qtdeProblema = 0;
				      FccCentroCustos centroCustos = null;
				      
				      SceEstoqueAlmoxarifado estoqueAlmoxarifado = this.getEstoqueFacade().obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(parametroAlmoxarifadoCentral.getVlrNumerico().shortValue(), codigoMaterial, null);
                     
				      if (estoqueAlmoxarifado != null) {
				    	  qtdeDisponivel = estoqueAlmoxarifado.getQtdeDisponivel();
				    	  qtdeBloqueada  = estoqueAlmoxarifado.getQtdeBloqueada();
				    	  
				          SceHistoricoProblemaMaterial histProblemaMaterial = this.getEstoqueFacade().pesquisarQtdeBloqueadaPorProblema(estoqueAlmoxarifado.getSeq());
				          
				          if (histProblemaMaterial != null){
				        	  qtdeProblema = (histProblemaMaterial.getQtdeProblema() - histProblemaMaterial.getQtdeDf() - histProblemaMaterial.getQtdeDesbloqueada());				        	  
				          }
				      }
				      
				      qtdeDisponivel = qtdeDisponivel + qtdeBloqueada + qtdeProblema;
				      
				      if (qtdeDisponivel != 0){
				    	  centroCustos = this.getEstoqueFacade().obterAlmoxarifadoPorId(parametroAlmoxarifadoCentral.getVlrNumerico().shortValue()).getCentroCusto();
				      }
				      // RN 26 - PLL DISP_BLOQ_PROBL FIM 				      
				      gerarNovaSC(faseSolicitacao.getSolicitacaoDeCompra(), qtdeSolicitacao.longValue(), centroCustos);
                      
				      
				}
			}
		}
		
	}
	
   /** RN 26 - PLL GERA_NOVA_SC 
 * @throws BaseException ***/
	public void gerarNovaSC (ScoSolicitacaoDeCompra scoSolicitacaoCompra, Long qtdeSolicitacao, FccCentroCustos fccCentroCustos) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
		    ScoSolicitacaoDeCompra solCompra = this.getSolicitacaoComprasFacade().obterSolicitacaoDeCompra(scoSolicitacaoCompra.getNumero());		   
            this.getScoSolicitacoesDeComprasDAO().desatachar(solCompra);
         				
		    AghParametros pontoParadaComprador = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_COMPRADOR);			
			
			ScoPontoParadaSolicitacao scoPontoParadaSolicitacao =  this.getComprasCadastrosBasicosFacade().obterPontoParada(pontoParadaComprador.getVlrNumerico().shortValue());

			solCompra.setNumero(null);
			solCompra.setPontoParada(scoPontoParadaSolicitacao);
			solCompra.setPontoParadaProxima(scoPontoParadaSolicitacao);
			solCompra.setDtSolicitacao(new Date());
			solCompra.setDtDigitacao(new Date());
			solCompra.setQtdeSolicitada(qtdeSolicitacao);
			solCompra.setQtdeAprovada(qtdeSolicitacao);
			solCompra.setExclusao(false);
			solCompra.setUrgente(false);
			solCompra.setDevolucao(false);
			solCompra.setOrcamentoPrevio("N");
		    solCompra.setQtdeReforco(null);
		    solCompra.setMotivoExclusao(null);
		    solCompra.setMotivoUrgencia(null);		    
		    solCompra.setDtExclusao(null);
		    solCompra.setDtAnalise(null);
		    solCompra.setJustificativaDevolucao(null);
		    solCompra.setServidorExclusao(null);
		    solCompra.setServidorAlteracao(null);
		    solCompra.setQtdeEntregue(null);
		    solCompra.setEfetivada(false);
		    solCompra.setRecebimento(false);
		    solCompra.setNroProjeto(null);		    
		    solCompra.setServidor(servidorLogado);
		    solCompra.setServidorAutorizacao(null);
		    solCompra.setServidorCompra(null);			
		    solCompra.setDtAlteracao(null);
		    solCompra.setPaciente(null);
		    solCompra.setSlcNumeroVinculado(null);
		    solCompra.setFases(new HashSet<ScoFaseSolicitacao>());
		    
		    
		    if (fccCentroCustos != null) {
				solCompra.setCentroCusto(fccCentroCustos);
				solCompra.setCentroCustoAplicada(fccCentroCustos);
			}		
		    
		    this.getSolicitacaoComprasFacade().inserirScoSolicitacaoDeCompra(solCompra);
		    
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}
		
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}
	
	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}	
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
