/**
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ItemAFAtualizaConsignadoON extends BaseBusiness{

	@EJB
	private MantemItemAutFornecimentoON mantemItemAutFornecimentoON;

	private static final Log LOG = LogFactory.getLog(ItemAFAtualizaConsignadoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4068119530112314334L;

	public void atualizaIndConsignadoItemAF(ItensAutFornVO itemAutFornVO) throws BaseException {
		
			List<ScoFaseSolicitacao> listaFaseSolicitacao = this.getScoFaseSolicitacaoDAO().pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(itemAutFornVO.getAfnNumero(), itemAutFornVO.getNumero(), null);
					
			ScoAutorizacaoForn autFornecimento = this.getScoAutorizacaoFornDAO().obterPorChavePrimaria(itemAutFornVO.getAfnNumero());
				
			AghParametros parametroFornecedorPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			String nomeMicrocomputador = itemAutFornVO.getNomeMicrocomputador();
		
			if (listaFaseSolicitacao != null && !listaFaseSolicitacao.isEmpty()) {				
				for (ScoFaseSolicitacao fase: listaFaseSolicitacao){					
					if (fase.getSolicitacaoDeCompra() != null){						
						if (itemAutFornVO.getIndConsignado()){
							// RN 36 Atualiza FASE 
							this.getManterItemAutFornecimentoON().atualizarFaseSolicitacao(fase);	
							// RN 37 
							Short seqAlmoxarifado = fase.getSolicitacaoDeCompra().getMaterial().getAlmoxarifado().getSeq();
							Integer codigoMaterial = fase.getSolicitacaoDeCompra().getMaterial().getCodigo();
							Integer numeroFornecedor = autFornecimento.getPropostaFornecedor().getFornecedor().getNumero();
							
							List<SceEstoqueAlmoxarifado> listaEstoquesAlmoxarifadosForn =  this.getEstoqueFacade().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(seqAlmoxarifado, codigoMaterial, numeroFornecedor);
							
							if (listaEstoquesAlmoxarifadosForn != null && !listaEstoquesAlmoxarifadosForn.isEmpty()) {
								
								
								listaEstoquesAlmoxarifadosForn.addAll(this.getEstoqueFacade().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(seqAlmoxarifado, codigoMaterial, parametroFornecedorPadrao.getVlrNumerico().intValue()));
								
								for (SceEstoqueAlmoxarifado estoqueAlmoxFornecedor:listaEstoquesAlmoxarifadosForn){
									estoqueAlmoxFornecedor.setIndConsignado(true);
									this.getEstoqueFacade().atualizarEstoqueAlmoxarifado(estoqueAlmoxFornecedor, nomeMicrocomputador);
								}
								
							}
							else {
								listaEstoquesAlmoxarifadosForn = this.getEstoqueFacade().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(seqAlmoxarifado, codigoMaterial, parametroFornecedorPadrao.getVlrNumerico().intValue());
								
								if (listaEstoquesAlmoxarifadosForn != null && !listaEstoquesAlmoxarifadosForn.isEmpty()) {
									for (SceEstoqueAlmoxarifado estoqueAlmoxFornecedor:listaEstoquesAlmoxarifadosForn){
										
										estoqueAlmoxFornecedor.setIndConsignado(true);
										this.getEstoqueFacade().atualizarEstoqueAlmoxarifado(estoqueAlmoxFornecedor, nomeMicrocomputador);
										
										SceAlmoxarifado almox = this.getEstoqueFacade().obterAlmoxarifadoPorId(seqAlmoxarifado);
										ScoMaterial     mat = this.getComprasFacade().obterMaterialPorId(codigoMaterial);
										ScoUnidadeMedida unidadeMedida = null;
										
										if (estoqueAlmoxFornecedor.getUnidadeMedida() != null){
										   unidadeMedida = this.getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(estoqueAlmoxFornecedor.getUnidadeMedida().getCodigo());
										}
										
										ScoFornecedor fornecedor = this.getComprasFacade().obterFornecedorPorChavePrimaria(numeroFornecedor);
										
										this.criarEstoqueAlmoxarifado(almox, mat, unidadeMedida, fornecedor, estoqueAlmoxFornecedor);
									}
									
									
								}
								else {
									
									AghParametros parametroAlmoxarifadoCentral = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL);
									
									listaEstoquesAlmoxarifadosForn = this.getEstoqueFacade().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(parametroAlmoxarifadoCentral.getVlrNumerico().shortValue(), codigoMaterial, parametroFornecedorPadrao.getVlrNumerico().intValue());
									
									if (listaEstoquesAlmoxarifadosForn != null && !listaEstoquesAlmoxarifadosForn.isEmpty()) {
										for (SceEstoqueAlmoxarifado estoqueAlmoxFornecedor:listaEstoquesAlmoxarifadosForn){
											
											estoqueAlmoxFornecedor.setIndConsignado(true);
											this.getEstoqueFacade().atualizarEstoqueAlmoxarifado(estoqueAlmoxFornecedor , nomeMicrocomputador);
																				
											SceAlmoxarifado almox = this.getEstoqueFacade().obterAlmoxarifadoPorId(parametroAlmoxarifadoCentral.getVlrNumerico().shortValue());
											ScoMaterial     mat = this.getComprasFacade().obterMaterialPorId(codigoMaterial);
											ScoUnidadeMedida unidadeMedida = null;
											
											if (estoqueAlmoxFornecedor.getUnidadeMedida() != null){
											   unidadeMedida = this.getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(estoqueAlmoxFornecedor.getUnidadeMedida().getCodigo());
											}
											
											ScoFornecedor fornecedor = this.getComprasFacade().obterFornecedorPorChavePrimaria(numeroFornecedor);										
											this.criarEstoqueAlmoxarifado(almox, mat, unidadeMedida, fornecedor, estoqueAlmoxFornecedor);
											
										}
											
									}
								}
							}
						}
						else {
							// RN 37 						
							Short seqAlmoxarifado = fase.getSolicitacaoDeCompra().getMaterial().getAlmoxarifado().getSeq();
							Integer codigoMaterial = fase.getSolicitacaoDeCompra().getMaterial().getCodigo();
							Integer numeroFornecedor = autFornecimento.getPropostaFornecedor().getFornecedor().getNumero();
							
							List<SceEstoqueAlmoxarifado> listaEstoquesAlmoxarifadosForn =  this.getEstoqueFacade().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(seqAlmoxarifado, codigoMaterial, numeroFornecedor);
							
							if (listaEstoquesAlmoxarifadosForn != null && !listaEstoquesAlmoxarifadosForn.isEmpty()) {
								
								
								listaEstoquesAlmoxarifadosForn.addAll(this.getEstoqueFacade().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(seqAlmoxarifado, codigoMaterial, parametroFornecedorPadrao.getVlrNumerico().intValue()));
								
								for (SceEstoqueAlmoxarifado estoqueAlmoxFornecedor:listaEstoquesAlmoxarifadosForn){
									estoqueAlmoxFornecedor.setIndConsignado(false);
									this.getEstoqueFacade().atualizarEstoqueAlmoxarifado(estoqueAlmoxFornecedor, nomeMicrocomputador);
								}							
							}
						}
					}
				}
			
		}
	}

	private void criarEstoqueAlmoxarifado(SceAlmoxarifado almox, ScoMaterial mat, ScoUnidadeMedida unidadeMedida, ScoFornecedor fornecedor, SceEstoqueAlmoxarifado estoqueAlmoxFornecedor) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
	
		SceEstoqueAlmoxarifado sceEstoqueAlmForn = new SceEstoqueAlmoxarifado();
		
		sceEstoqueAlmForn.setAlmoxarifado(almox);
		sceEstoqueAlmForn.setMaterial(mat);
		sceEstoqueAlmForn.setUnidadeMedida(unidadeMedida);
		sceEstoqueAlmForn.setDtGeracao(new Date());
		sceEstoqueAlmForn.setIndEstocavel(estoqueAlmoxFornecedor.getIndEstocavel());
		sceEstoqueAlmForn.setIndEstqMinCalc(estoqueAlmoxFornecedor.getIndEstqMinCalc());
		sceEstoqueAlmForn.setIndPontoPedidoCalc(estoqueAlmoxFornecedor.getIndPontoPedidoCalc());
		sceEstoqueAlmForn.setIndSituacao(estoqueAlmoxFornecedor.getIndSituacao());
		sceEstoqueAlmForn.setServidor(servidorLogado);
		sceEstoqueAlmForn.setFornecedor(fornecedor);
		sceEstoqueAlmForn.setQtdeBloqueada(0);
		sceEstoqueAlmForn.setQtdeDisponivel(0);
		sceEstoqueAlmForn.setQtdeEmUso(0);
		sceEstoqueAlmForn.setQtdePontoPedido(estoqueAlmoxFornecedor.getQtdePontoPedido());
		sceEstoqueAlmForn.setQtdeEstqMin(estoqueAlmoxFornecedor.getQtdeEstqMin());
		sceEstoqueAlmForn.setQtdeEstqMax(estoqueAlmoxFornecedor.getQtdeEstqMax());
		sceEstoqueAlmForn.setTempoReposicao(estoqueAlmoxFornecedor.getTempoReposicao());
		sceEstoqueAlmForn.setSolicitacaoCompra(null);
		sceEstoqueAlmForn.setEndereco(estoqueAlmoxFornecedor.getEndereco());
		sceEstoqueAlmForn.setDtAlteracao(null);
		sceEstoqueAlmForn.setServidorAlterado(null);
		sceEstoqueAlmForn.setDtDesativacao(null);
		sceEstoqueAlmForn.setServidorDesativado(null);
		sceEstoqueAlmForn.setIndControleValidade(estoqueAlmoxFornecedor.getIndControleValidade());
		sceEstoqueAlmForn.setDtUltimoConsumo(null);
		sceEstoqueAlmForn.setDtUltimaCompra(null);
		sceEstoqueAlmForn.setDtUltimaCompraFf(null);
		sceEstoqueAlmForn.setIndConsignado(estoqueAlmoxFornecedor.getIndConsignado());
		sceEstoqueAlmForn.setQtdeBloqEntrTransf(0);
		sceEstoqueAlmForn.setQtdeBloqConsumo(0);
		
		this.getEstoqueFacade().inserirSceEstoqueAlmoxarifado(sceEstoqueAlmForn);
	
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected MantemItemAutFornecimentoON getManterItemAutFornecimentoON() {
		return mantemItemAutFornecimentoON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
