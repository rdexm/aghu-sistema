package br.gov.mec.aghu.compras.pac.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.ComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPgtoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFormaPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.compras.vo.DupItensPACVO;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoDuplicacaoPAC;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class DuplicaPACON extends BaseBusiness implements Serializable{

	@EJB
	private ScoItemLicitacaoON scoItemLicitacaoON;
	
	@EJB
	private ScoLicitacaoON scoLicitacaoON;
	
	@EJB
	private RelatorioItensPACON relatorioItensPACON;
	
	@EJB
	private ScoLicitacaoRN scoLicitacaoRN;
	
	private static final Log LOG = LogFactory.getLog(DuplicaPACON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;
	
	@Inject
	private ScoFormaPagamentoDAO scoFormaPagamentoDAO;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@Inject
	private ScoCondicaoPgtoLicitacaoDAO scoCondicaoPgtoLicitacaoDAO;
	
	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;
	
	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4216766104835407921L;

		
	public enum DuplicaPACONExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_CLONAR_PAC;
	}
	
	public ScoPontoParadaSolicitacao buscaParametroPontoParadaPlanejamento() throws ApplicationBusinessException{
		AghParametros pontoParadaPlanejamentoParametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.PPS_PLANEJAMENTO);
		
		Short   pontoParadaPlanejamento = pontoParadaPlanejamentoParametro.getVlrNumerico().shortValue();				
		ScoPontoParadaSolicitacao pontoParadaSolicitacaoPlanejamento = getComprasCadastrosBasicosFacade().obterPontoParada(pontoParadaPlanejamento);		
		return pontoParadaSolicitacaoPlanejamento;
		
	}
	
	public ScoPontoParadaSolicitacao buscaParametroPontoParadaComprador() throws ApplicationBusinessException{
		AghParametros pontoParadaCompradorParametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_PPS_COMPRADOR);
		
		Short   pontoParadaComprador = pontoParadaCompradorParametro.getVlrNumerico().shortValue();				
		ScoPontoParadaSolicitacao pontoParadaSolicitacaoPlanejamento = getComprasCadastrosBasicosFacade().obterPontoParada(pontoParadaComprador);		
		return pontoParadaSolicitacaoPlanejamento;
		
	}	
	
	public List<DupItensPACVO> pesquisarItensPAC(Integer numero, DominioTipoDuplicacaoPAC tipoDuplicacaoPAC)
			throws ApplicationBusinessException {
		List<ItensPACVO> listaItensPACVOs = getRelatorioItensPACON().pesquisarRelatorioItensPAC(numero, false);
		List<DupItensPACVO> listaDuplicarItensPAC = new ArrayList<DupItensPACVO>();

		for (ItensPACVO item : listaItensPACVOs) {
			DupItensPACVO dupItemPACVO = new DupItensPACVO();

			dupItemPACVO.setNaoDuplicar(Boolean.FALSE);
			dupItemPACVO.setLoteNumero(item.getNumeroLote());
			dupItemPACVO.setNumeroLicitacao(item.getNumeroLicitacao());
			dupItemPACVO.setNumeroItem(item.getNumeroItem());
			dupItemPACVO.setCodigoMaterialServico(item.getCodigoMaterial());
			dupItemPACVO.setNomeMaterialServico(item.getNomeMaterial());
			dupItemPACVO.setQtdeSolicitada(item.getSomaQtdeAprovada());
			dupItemPACVO.setNumeroSolicitacao(item.getNumeroSolicitacaoCompra());
			dupItemPACVO.setExclusao(item.getExclusao());

			if (item.getTipoFaseSolicitacao().equals(
					DominioTipoFaseSolicitacao.C)) {
				dupItemPACVO.setTipoSolicitacao(DominioTipoSolicitacao.SC);
				dupItemPACVO.setUnidadeMedidaCodigo(item
						.getCodigoUnidadeMedida());
				if (DominioTipoDuplicacaoPAC.ABERTURA_PAC.equals(tipoDuplicacaoPAC)) {
					if (item.getCodigoMaterial() != null) {
						ScoMaterial scoMaterial = this.getScoMaterialDAO().obterPorChavePrimaria(item.getCodigoMaterial());
						BigDecimal ultimoValorCompra;
						if (this.getEstoqueFacade().getUltimoValorCompra(scoMaterial) == null
								|| this.getEstoqueFacade().getUltimoValorCompra(scoMaterial).doubleValue() == 0) {
							ScoSolicitacaoDeCompra solicitacaoCompra = getSolicitacaoComprasFacade().obterSolicitacaoDeCompra(item.getNumeroSolicitacaoCompra());
							ultimoValorCompra = solicitacaoCompra.getValorUnitPrevisto();
						} else {
							ultimoValorCompra = new BigDecimal(this.getEstoqueFacade().getUltimoValorCompra(scoMaterial));
						}
						dupItemPACVO.setValorUnitPrevisto(ultimoValorCompra);
					}
				} else {
					dupItemPACVO.setValorUnitPrevisto(item.getValorUnitario());
				}

			} else {
				dupItemPACVO.setTipoSolicitacao(DominioTipoSolicitacao.SS);
				dupItemPACVO.setValorUnitPrevisto(item.getValorUnitario());				
			}

			listaDuplicarItensPAC.add(dupItemPACVO);

		}

		return listaDuplicarItensPAC;
	}			
		
	public void inserirLicitacao(ScoLicitacao scoLicitacao, ScoLicitacao scoLicitacaoClone, boolean flagIncluirParcelaPagamento)
			throws BaseException {				
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		scoLicitacaoClone.setNumero(null);	
		scoLicitacaoClone.setServidorDigitado(servidorLogado);
		scoLicitacaoClone.setDtDigitacao(new Date());
		
		scoLicitacaoClone.setDtLimiteAceiteProposta(null);
		scoLicitacaoClone.setDthrAberturaProposta(null);
		scoLicitacaoClone.setHrAberturaProposta(null);
		scoLicitacaoClone.setItensLicitacao(null);
		scoLicitacaoClone.setExclusao(false);
		scoLicitacaoClone.setDtExclusao(null);
		scoLicitacaoClone.setMotivoExclusao(null);
		scoLicitacaoClone.setDtDivulgacaoPublicacao(null);
	//	scoLicitacaoClone.setCritJulgOrcamento(null);
	//	scoLicitacaoClone.setDtInicioFornecimento(null);
		//scoLicitacaoClone.setModalidadeEmpenho(null);
		scoLicitacaoClone.setTempoPrevExecucao(null);
		//scoLicitacaoClone.setTipo(null);
	//	scoLicitacaoClone.setTempoAtendimento(null);
		//scoLicitacaoClone.setTipoPregao(null);
		scoLicitacaoClone.setIncisoArtigoLicitacaoBkp(null);
		scoLicitacaoClone.setServidorExcluido(null);
		//scoLicitacaoClone.setServidorGestor(null);		
		scoLicitacaoClone.setDtHrAberturaPropostaCompleta(null);
		scoLicitacaoClone.setVarMaxima(null);
		scoLicitacaoClone.setDtPublicacao(null);
		scoLicitacaoClone.setIdEletronico(null);
			
		
		scoLicitacaoClone.setServidorDigitado(servidorLogado);
		scoLicitacaoClone.setDtDigitacao(new Date());
		scoLicitacaoClone.setSituacao(DominioSituacaoLicitacao.GR);
		
		if (scoLicitacaoClone.getDthrAberturaProposta()!=null){
			scoLicitacaoClone.setHrAberturaProposta(scoLicitacaoClone.getDthrAberturaProposta().getHours());		
		}
		
		this.getScoLicitacaoON().validaFrequenciaCompras(scoLicitacaoClone);		
		
		this.getScoLicitacaoRN().inserirLicitacao(scoLicitacaoClone);
	
	}	
	
	public ScoSolicitacaoDeCompra criarSC(DupItensPACVO itemPACVO, DominioTipoFaseSolicitacao tipoFaseSolicitacao,ScoSolicitacaoDeCompra solicitacaoCompra) throws BaseException{
		ScoSolicitacaoDeCompra solicitacaoCompraClone = new ScoSolicitacaoDeCompra(); 
		solicitacaoCompraClone = getSolicitacaoComprasFacade().clonarSolicitacaoDeCompra(solicitacaoCompra);	
		solicitacaoCompraClone.setValorUnitPrevisto(itemPACVO.getValorUnitPrevisto());
		solicitacaoCompraClone.setPontoParada(solicitacaoCompra.getPontoParada());
		solicitacaoCompraClone.setPontoParadaProxima(solicitacaoCompra.getPontoParadaProxima());		
		solicitacaoCompraClone.setValorUnitPrevisto(itemPACVO.getValorUnitPrevisto());
		return getSolicitacaoComprasFacade().duplicarSCPorPAC(solicitacaoCompraClone);
		
	}
	
	public ScoSolicitacaoServico criarSS(DupItensPACVO itemPACVO, RapServidores servidorLogado, DominioTipoFaseSolicitacao tipoFaseSolicitacao, ScoSolicitacaoServico solicitacaoServico) throws BaseException{
		ScoSolicitacaoServico solicitacaoServicoClone = new ScoSolicitacaoServico();
		solicitacaoServicoClone = getSolicitacaoServicoFacade().clonarSolicitacaoServico(solicitacaoServico);
		solicitacaoServicoClone.setPontoParada(solicitacaoServico.getPontoParada());
		solicitacaoServicoClone.setPontoParadaLocAtual(solicitacaoServico.getPontoParadaLocAtual());
//		solicitacaoServicoClone.setPontoParada(this.buscaParametroPontoParadaPlanejamento());
//		solicitacaoServicoClone.setPontoParadaLocAtual(this.buscaParametroPontoParadaComprador());
		solicitacaoServicoClone.setValorUnitPrevisto(itemPACVO.getValorUnitPrevisto());
		return getSolicitacaoServicoFacade().duplicarSS(solicitacaoServicoClone, false, true, false);
		
	}
	
	public void criarItensLicitacao(DupItensPACVO itemPACVO, ScoLicitacao scoLicitacaoClone, 
			ScoItemLicitacao scoItemLicitacao, ScoItemLicitacaoId scoItemLicitacaoId, 
			ScoItemLicitacao scoItemLicitacaoClone, boolean flagAteAF){  
			
			scoItemLicitacaoClone.setId(scoItemLicitacaoId);
			scoItemLicitacaoClone.setLicitacao(scoLicitacaoClone);
			scoItemLicitacaoClone.setEmAf(false);
			scoItemLicitacaoClone.setMotivoCancel(null);
			scoItemLicitacaoClone.setExclusao(false);
			scoItemLicitacaoClone.setDtExclusao(null);
			scoItemLicitacaoClone.setMotivoExclusao(null);
			scoItemLicitacaoClone.setValorUnitario(itemPACVO.getValorUnitPrevisto());
			scoItemLicitacaoClone.setFasesSolicitacao(null);
			scoItemLicitacaoClone.setValorOriginalItem(null);
			scoItemLicitacaoClone.setJulgParcial(false);
			scoItemLicitacaoClone.setDtJulgParcial(null);
			scoItemLicitacaoClone.setServidorJulgParcial(null);
			scoItemLicitacaoClone.setCondicoesPagamento(null);
			scoItemLicitacaoClone.setLoteLicitacao(null);
			
			
			if(flagAteAF){
			scoItemLicitacaoClone.setPropostaEscolhida(scoItemLicitacao.getPropostaEscolhida());
			} else {
				scoItemLicitacaoClone.setPropostaEscolhida(false);	
			}
			scoItemLicitacaoClone.setClassifItem(scoItemLicitacao.getId().getNumero());
					
			getScoItemLicitacaoDAO().persistir(scoItemLicitacaoClone);
			getScoItemLicitacaoDAO().flush();
			
	 }
	
	public void criarFases(DominioTipoFaseSolicitacao tipoFaseSolicitacao, ScoSolicitacaoDeCompra solicitacaoCompraClone, ScoSolicitacaoServico solicitacaoServicoClone, ScoItemLicitacao scoItemLicitacaoClone){
		ScoFaseSolicitacao scoFaseSolicitacao = new ScoFaseSolicitacao();
		scoFaseSolicitacao.setTipo(tipoFaseSolicitacao);
		scoFaseSolicitacao.setSolicitacaoDeCompra(solicitacaoCompraClone);
		scoFaseSolicitacao.setSolicitacaoServico(solicitacaoServicoClone);
		scoFaseSolicitacao.setItemLicitacao(scoItemLicitacaoClone);
		scoFaseSolicitacao.setItemAutorizacaoForn(null);
		scoFaseSolicitacao.setExclusao(false);
		scoFaseSolicitacao.setDtExclusao(null);	   
		getScoFaseSolicitacaoDAO().persistir(scoFaseSolicitacao);
	} 
	public void criarPropostaFornecedor(ScoLicitacao scoLicitacao, ScoLicitacao scoLicitacaoClone){
		
		List<ScoPropostaFornecedor> listaPropostaFornecedor = this.getScoPropostaFornecedorDAO().listarPropostaFornPorLicitacao(scoLicitacao);
		
		for(ScoPropostaFornecedor itemPropostaFornecedor: listaPropostaFornecedor){
			
			ScoPropostaFornecedor propostaFornecedor = new ScoPropostaFornecedor();
			propostaFornecedor.setLicitacao(scoLicitacaoClone);
			propostaFornecedor.setFornecedor(itemPropostaFornecedor.getFornecedor());
			propostaFornecedor.setServidor(itemPropostaFornecedor.getServidor());			
			propostaFornecedor.setDtDigitacao(new Date());
			propostaFornecedor.setDtApresentacao(new Date());
			propostaFornecedor.setValorTotalFrete(itemPropostaFornecedor.getValorTotalFrete());
			propostaFornecedor.setPrazoEntrega(itemPropostaFornecedor.getPrazoEntrega());
			propostaFornecedor.setIndExclusao(false);
			propostaFornecedor.setDtExclusao(null);
			this.getScoPropostaFornecedorDAO().persistir(propostaFornecedor);
			
			List<ScoCondicaoPagamentoPropos> listaScoCondicaoPagamentoPropos = getScoCondicaoPagamentoProposDAO().obterCondicaoPagamentoPropos(itemPropostaFornecedor.getFornecedor().getNumero(), itemPropostaFornecedor.getLicitacao().getNumero(), null, 0, 1000, null, false);
			
			for (ScoCondicaoPagamentoPropos itemScoCondicaoPagamentoPropos:listaScoCondicaoPagamentoPropos){
				ScoCondicaoPagamentoPropos condicaoPagamentoPropos = new ScoCondicaoPagamentoPropos();
				condicaoPagamentoPropos.setFormaPagamento(itemScoCondicaoPagamentoPropos.getFormaPagamento());
				condicaoPagamentoPropos.setIndCondEscolhida(itemScoCondicaoPagamentoPropos.getIndCondEscolhida());
				condicaoPagamentoPropos.setPropostaFornecedor(propostaFornecedor);
				condicaoPagamentoPropos.setItemPropostaFornecedor(null);
				condicaoPagamentoPropos.setPercAcrescimo(itemScoCondicaoPagamentoPropos.getPercAcrescimo());
				condicaoPagamentoPropos.setPercDesconto(itemScoCondicaoPagamentoPropos.getPercDesconto());
				condicaoPagamentoPropos.setParcelas(null);
				getScoCondicaoPagamentoProposDAO().persistir(condicaoPagamentoPropos);
				
				for (ScoParcelasPagamento itemParcelaPagamentoProposForn: itemScoCondicaoPagamentoPropos.getParcelas()){
					ScoParcelasPagamento parcelaPagamentoProposForn = new ScoParcelasPagamento();
					parcelaPagamentoProposForn.setCondicaoPagamentoPropos(condicaoPagamentoPropos);
					parcelaPagamentoProposForn.setParcela(itemParcelaPagamentoProposForn.getParcela());
					parcelaPagamentoProposForn.setPrazo(itemParcelaPagamentoProposForn.getPrazo());
					parcelaPagamentoProposForn.setPercPagamento(itemParcelaPagamentoProposForn.getPercPagamento());
					parcelaPagamentoProposForn.setValorPagamento(itemParcelaPagamentoProposForn.getValorPagamento());
					this.getScoParcelasPagamentoDAO().persistir(parcelaPagamentoProposForn);			
					
				}								
				
			}			
			
			
		}
		
	}
	
	public void criarItensPropostaFornecedor(DupItensPACVO itemPACVO, ScoItemLicitacao scoItemLicitacaoClone){
		List<ScoItemPropostaFornecedor> listaItensPorpostaForn = getItemPropostaFornecedorDAO().obterItemPropostaFornecedorPeloNumLicitacaoENumeroItem(itemPACVO.getNumeroLicitacao(), itemPACVO.getNumeroItem());
		   
    	for (ScoItemPropostaFornecedor itemItemPropostaForn : listaItensPorpostaForn){
    		ScoItemPropostaFornecedorId propostaFornID = new ScoItemPropostaFornecedorId();
    		
    		propostaFornID.setPfrLctNumero(scoItemLicitacaoClone.getLicitacao().getNumero());			    		
    		propostaFornID.setPfrFrnNumero(itemItemPropostaForn.getPropostaFornecedor().getFornecedor().getNumero());
    		propostaFornID.setNumero(itemItemPropostaForn.getId().getNumero());	    		
    		
    		ScoItemPropostaFornecedor scoItemPropostaFornecedor = new ScoItemPropostaFornecedor();
    		scoItemPropostaFornecedor.setItemLicitacao(scoItemLicitacaoClone);			    		
    		scoItemPropostaFornecedor.setId(propostaFornID);
    		
    		//ScoPropostaFornecedorId propFornID = new ScoPropostaFornecedorId(scoItemLicitacaoClone.getLicitacao().getNumero(), itemItemPropostaForn.getPropostaFornecedor().getFornecedor().getNumero());
    		
    		//scoItemPropostaFornecedor.setPropostaFornecedor(new ScoPropostaFornecedor(propFornID));
    		   		
    		
    		//scoItemPropostaFornecedor.setPropostaFornecedor(new ScoPropostaFornecedor(new ScoPropostaFornecedorId(scoItemLicitacaoClone.getLicitacao().getNumero(), itemItemPropostaForn.getPropostaFornecedor().getFornecedor().getNumero())));
    		scoItemPropostaFornecedor.setUnidadeMedida(itemItemPropostaForn.getUnidadeMedida());
    		scoItemPropostaFornecedor.setMoeda(itemItemPropostaForn.getMoeda());
    		scoItemPropostaFornecedor.setQuantidade(itemItemPropostaForn.getQuantidade());
    		scoItemPropostaFornecedor.setIndEscolhido(itemItemPropostaForn.getIndEscolhido());
    		scoItemPropostaFornecedor.setIndComDesconto(itemItemPropostaForn.getIndComDesconto());
    		scoItemPropostaFornecedor.setIndNacional(itemItemPropostaForn.getIndNacional());
    		scoItemPropostaFornecedor.setIndDesclassificado(itemItemPropostaForn.getIndDesclassificado());
    		scoItemPropostaFornecedor.setFatorConversao(itemItemPropostaForn.getFatorConversao());
    		scoItemPropostaFornecedor.setMarcaComercial(itemItemPropostaForn.getMarcaComercial());
    		scoItemPropostaFornecedor.setNomeComercial(itemItemPropostaForn.getNomeComercial());
    		scoItemPropostaFornecedor.setCriterioEscolhaProposta(itemItemPropostaForn.getCriterioEscolhaProposta());
    		scoItemPropostaFornecedor.setPercAcrescimo(itemItemPropostaForn.getPercAcrescimo());
    		scoItemPropostaFornecedor.setPercIpi(itemItemPropostaForn.getPercIpi());
    		scoItemPropostaFornecedor.setPercDesconto(itemItemPropostaForn.getPercDesconto());
    		scoItemPropostaFornecedor.setValorUnitario(itemItemPropostaForn.getValorUnitario());
    		scoItemPropostaFornecedor.setObservacao(itemItemPropostaForn.getObservacao());
    		scoItemPropostaFornecedor.setMotDesclassif(itemItemPropostaForn.getMotDesclassif());
    		scoItemPropostaFornecedor.setIndExclusao(false);
    		scoItemPropostaFornecedor.setDtExclusao(null);
    		scoItemPropostaFornecedor.setIndAnalisadoPt(itemItemPropostaForn.getIndAnalisadoPt());
    		scoItemPropostaFornecedor.setCondicaoPagamentoPropos(itemItemPropostaForn.getCondicaoPagamentoPropos());
    		scoItemPropostaFornecedor.setIndAutorizUsr(itemItemPropostaForn.getIndAutorizUsr());
    		scoItemPropostaFornecedor.setJustifAutorizUsr(itemItemPropostaForn.getJustifAutorizUsr());
    		scoItemPropostaFornecedor.setApresentacao(itemItemPropostaForn.getApresentacao());			    		
    		getItemPropostaFornecedorDAO().persistir(scoItemPropostaFornecedor);
    		
    	}
		
		
	}
	
	public boolean criarItensLicitacaoPACVO(ScoLicitacao scoLicitacaoClone, RapServidores servidorLogado, List<DupItensPACVO> itensPACVO, boolean flagAteAF) throws CloneNotSupportedException, BaseException{
		
		boolean flagCriouItens = false;
		Integer numero = 1;
		for (DupItensPACVO itemPACVO: itensPACVO){
			DominioTipoFaseSolicitacao tipoFaseSolicitacao = null;
			if (itemPACVO.getNaoDuplicar().equals(Boolean.FALSE)){			
				
				ScoSolicitacaoDeCompra solicitacaoCompraClone = null;
				ScoSolicitacaoServico solicitacaoServicoClone = null;
				
				if (itemPACVO.getTipoSolicitacao().equals(DominioTipoSolicitacao.SC)){
					ScoSolicitacaoDeCompra solicitacaoCompra = getSolicitacaoComprasFacade().obterSolicitacaoDeCompraOriginal(itemPACVO.getNumeroSolicitacao());
					solicitacaoCompraClone = this.criarSC(itemPACVO, tipoFaseSolicitacao, solicitacaoCompra);	
					tipoFaseSolicitacao = DominioTipoFaseSolicitacao.C;			    
				}
				else {						
					ScoSolicitacaoServico solicitacaoServico = getSolicitacaoServicoFacade().obterSolicitacaoServicoOriginal(itemPACVO.getNumeroSolicitacao());
					solicitacaoServicoClone = this.criarSS(itemPACVO, servidorLogado, tipoFaseSolicitacao, solicitacaoServico);	
					tipoFaseSolicitacao = DominioTipoFaseSolicitacao.S;	
				}			
				
			    
				ScoItemLicitacao scoItemLicitacao = getScoItemLicitacaoDAO().obterItemLicitacaoPorNumeroLicitacaoENumeroItem(itemPACVO.getNumeroLicitacao(), itemPACVO.getNumeroItem());
				ScoItemLicitacao scoItemLicitacaoClone = getScoItemLicitacaoON().clonarItemLicitacao(scoItemLicitacao);
				ScoItemLicitacaoId scoItemLicitacaoId = new ScoItemLicitacaoId(scoLicitacaoClone.getNumero(), numero.shortValue());
				this.criarItensLicitacao(itemPACVO, scoLicitacaoClone, scoItemLicitacao, scoItemLicitacaoId, scoItemLicitacaoClone, flagAteAF);
				numero++;
			    this.criarFases(tipoFaseSolicitacao, solicitacaoCompraClone, solicitacaoServicoClone, scoItemLicitacaoClone);
			    
			    if (flagAteAF){
			    	this.criarItensPropostaFornecedor(itemPACVO, scoItemLicitacaoClone);			    
			    }
			    
			    flagCriouItens = true;
			}
		}
		
		return flagCriouItens;
	}
	
	
	
	public ScoLicitacao duplicarPAC(Integer numeroPAC, DominioTipoDuplicacaoPAC tipoDuplicacaoPAC,  RapServidores servidorLogado, List<DupItensPACVO> itensPACVO) throws BaseException{
		
		ScoLicitacao scoLicitacao = getScoLicitacaoDAO().obterPorChavePrimaria(numeroPAC);
		
		ScoLicitacao scoLicitacaoClone = new ScoLicitacao();
		
		try {
			scoLicitacaoClone = (ScoLicitacao) scoLicitacao.clone();
			//BeanUtils.copyProperties(scoLicitacaoClone, scoLicitacao);

			if (DominioTipoDuplicacaoPAC.ABERTURA_PAC.equals(tipoDuplicacaoPAC)) {
                this.getScoLicitacaoDAO().desatachar(scoLicitacao);
				this.inserirLicitacao(scoLicitacao, scoLicitacaoClone, false);
				if (this.criarItensLicitacaoPACVO(scoLicitacaoClone, servidorLogado, itensPACVO, false)) {

					this.getScoLicitacaoDAO().flush();
				}
			} else {
				if (DominioTipoDuplicacaoPAC.AUTORIZACAO_FORN.equals(tipoDuplicacaoPAC)) {
					this.getScoLicitacaoDAO().desatachar(scoLicitacao); 
					this.inserirLicitacao(scoLicitacao, scoLicitacaoClone, true);					
					
					this.criarPropostaFornecedor(scoLicitacao, scoLicitacaoClone);
					//this.getScoLicitacaoDAO().flush();    
					if (this.criarItensLicitacaoPACVO(scoLicitacaoClone, servidorLogado, itensPACVO, true)) {
						this.getScoLicitacaoDAO().flush();
					}
				}

			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			throw new ApplicationBusinessException(DuplicaPACONExceptionCode.MSG_ERRO_CLONAR_PAC);
		} 
		return scoLicitacaoClone;		
		
	}

	public Boolean validarEdicaoAcoes(ScoAndamentoProcessoCompra andamento, ScoAndamentoProcessoCompra primeiroAndamento) {
		return this.getServidorLogadoFacade().obterServidorLogado().equals(andamento.getServidor()) && andamento.getSeq().equals(primeiroAndamento.getSeq());
	}
	
	private RelatorioItensPACON getRelatorioItensPACON(){
		return relatorioItensPACON;
	}
	
	private ScoLicitacaoON getScoLicitacaoON(){
		return scoLicitacaoON;
	}
	private ScoLicitacaoRN getScoLicitacaoRN(){
		return scoLicitacaoRN;
	}
	
	private ScoItemLicitacaoON getScoItemLicitacaoON(){
		return scoItemLicitacaoON;
	}
	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO(){
		return scoItemLicitacaoDAO;
	}
	
	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO(){
		return scoFaseSolicitacaoDAO;
	}
	
	private ScoMaterialDAO getScoMaterialDAO(){
		return scoMaterialDAO;
	}
	
	private ScoLicitacaoDAO getScoLicitacaoDAO(){
		return scoLicitacaoDAO;
	}
	
	private ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO(){
		return scoParcelasPagamentoDAO;
	}
	
	private ScoPropostaFornecedorDAO  getScoPropostaFornecedorDAO(){
		return scoPropostaFornecedorDAO;
	}
	
	private ScoItemPropostaFornecedorDAO  getItemPropostaFornecedorDAO(){
		return scoItemPropostaFornecedorDAO;
	}
	
	private ScoCondicaoPagamentoProposDAO getScoCondicaoPagamentoProposDAO(){
		return scoCondicaoPagamentoProposDAO;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return this.solicitacaoComprasFacade;
	}
	protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return this.solicitacaoServicoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return new ComprasCadastrosBasicosFacade();
	}
	
	protected ScoCondicaoPgtoLicitacaoDAO getScoCondicaoPgtoLicitacaoDAO() {
		return scoCondicaoPgtoLicitacaoDAO;
	}
	
	protected ScoFormaPagamentoDAO getScoFormaPagamentoDAO() {
		return scoFormaPagamentoDAO;
	} 

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
