/**
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.compras.vo.MaterialServicoVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;


@Stateless
public class MantemItemAutFornecimentoON extends BaseBusiness{

@EJB
private ItemAFAtualizaConsignadoON itemAFAtualizaConsignadoON;
@EJB
private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;
@EJB
private ItemAFAtualizaSCContratoON itemAFAtualizaSCContratoON;
@EJB
private MantemItemAutFornValidacoesON mantemItemAutFornValidacoesON;

	private static final Log LOG = LogFactory.getLog(MantemItemAutFornecimentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 4068119530195412334L;	
	
	public Integer obterQtdeSaldoItemAF(Integer qtdeSolicitada, Integer qtdeRecebida) {
		if (qtdeSolicitada != null &&
			qtdeRecebida != null) {
		    return qtdeSolicitada - qtdeRecebida;
		}
		else {
			return 0;
		}
			
	}
	
	public Double obterValorBrutoItemAF(Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, BigDecimal valorEfetivado, Boolean isSc) {
		if (valorUnitario != null) {
			if (isSc) {
				return  valorUnitario * this.obterQtdeSaldoItemAF(qtdeSolicitada, qtdeRecebida);	
			} else {
				return valorUnitario - valorEfetivado.doubleValue();
			}
		}
		else {
			return new Double(0);
		}
	}
	
	public Double obterValorDescontoItemAF(Double percDesconto, Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida,
			BigDecimal valorEfetivado, Boolean isSc) {

		Double valorBruto = this.obterValorBrutoItemAF(valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
		
		if (percDesconto != null &&
		    percDescontoItem != null &&
		    valorBruto != null) {
		    return ((valorBruto * (percDesconto/100)) + (valorBruto * (percDescontoItem/100)));
		}
		else {
			return new Double(0);
		}
	}
	
	public Double obterValorAcrescimoItemAF(Double percAcrescimo, Double percAcrescimoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida,
			BigDecimal valorEfetivado, Boolean isSc) {
		Double valorBruto = this.obterValorBrutoItemAF(valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);

		if (percAcrescimo != null &&
			percAcrescimoItem != null &&
		    valorBruto != null) {
		    return ((valorBruto * (percAcrescimo/100)) + (valorBruto * (percAcrescimoItem/100)));
		}
		else {
			return new Double(0);
		}
	}
	
	public Double obterValorIpiItemAF(Double percIPI, Double percAcrescimo, Double percAcrescimoItem, Double percDesconto, 
			                          Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida,
			                          BigDecimal valorEfetivado, Boolean isSc) {
		
		Double valorBruto = this.obterValorBrutoItemAF(valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
		Double valorDesconto = this.obterValorDescontoItemAF(percDesconto, percDescontoItem, valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
		Double valorAcrescimo = this.obterValorAcrescimoItemAF(percAcrescimo, percAcrescimoItem, valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
		
		if (valorBruto != null &&
			valorDesconto != null &&
			valorAcrescimo != null &&
			percIPI !=null) {
		    return (((valorBruto - valorDesconto) + valorAcrescimo) * (percIPI/100));
		}
		else {
			return new Double(0);
		}
	}
	
		
	public void alterarItemAF(ItensAutFornVO item) throws BaseException{			
		
		
		this.getManterItemAutFornValidacoesON().validaMaiorZero(item.getFatorConversao().doubleValue(), "ERRO_FATOR_CONVERSAO_MSG01", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaMaiorZero(item.getQtdeSolicitada().doubleValue(), "ERRO_QTDE_SOLICITADA_MSG02", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaSolicitadaRecebida(item.getQtdeSolicitada(), item.getQtdeRecebida() , "ERRO_QTDE_SOLICITADA_RECEBIDA_MSG03", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaMaiorZero(item.getValorUnitario(), "ERRO_VALOR_UNITARIO_MSG04", item.getNumero());
		
		List<ScoFaseSolicitacao> listFases = item.getScoFaseSolicitacao();
		
		if (listFases != null && listFases.size() > 0){
			ScoFaseSolicitacao fase = listFases.get(0);
			
		    if (fase.getSolicitacaoDeCompra() != null && item.getFatorConversaoForn().doubleValue() != 0) { 	
		        this.getManterItemAutFornValidacoesON().validaMaiorZero(item.getFatorConversaoForn().doubleValue(), "ERRO_FATOR_CONVERSAO_FORN_MSG05", item.getNumero());
		   }
		}
		
		this.getManterItemAutFornValidacoesON().validaMaiorZeroMenorCem(item.getPercDescontoItem(), "ERRO_PERC_DESC_ITEM_MSG06", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaMaiorZeroMenorCem(item.getPercDesconto(), "ERRO_PERC_DESC_MSG07", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaMaiorZeroMenorCem(item.getPercAcrescimoItem(), "ERRO_PERC_ACRES_ITEM_MSG08", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaMaiorZeroMenorCem(item.getPercAcrescimo(), "ERRO_PERC_ACRES_MSG09", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaMaiorZeroMenorCem(item.getPercVarPreco().doubleValue(), "ERRO_PERC_VAR_PRECO_MSG10", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaMaiorZeroMenorCem(item.getPercIpi(), "ERRO_PERC_IPI_MSG11", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaPercentualAcrescimoDesconto(item.getPercDescontoItem(), item.getPercDesconto(), "ERRO_PERC_DESCONTO_MSG13", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaPercentualAcrescimoDesconto(item.getPercAcrescimoItem(), item.getPercAcrescimo(), "ERRO_PERC_ACRES_MSG14", item.getNumero());
		this.getManterItemAutFornValidacoesON().validaUnidadeFatorConversao(item);		
		
		ScoItemAutorizacaoForn itemOriginal = this.getScoItemAutorizacaoFornRN().obterItemAfOriginal(item);
		
		// RN 20 21
		if (!item.getIndContrato().equals(itemOriginal.getIndContrato())){
			this.getItemAFAtualizaSCContratoON().atualizarSCContrato(item);
		}
		
		// RN 36 e RN 37
		if (!item.getIndConsignado().equals(itemOriginal.getIndConsignado())){
			this.getItemAFAtualizaConsignadoON().atualizaIndConsignadoItemAF(item);
		}		
		
		DominioSituacaoAutorizacaoFornecedor indSituacaoAntiga = itemOriginal.getIndSituacao();
		 
				//this.getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(item.getAfnNumero(), item.getNumero());
		
		itemOriginal.setMarcaComercial(item.getMarcaComercial());
		
		itemOriginal.setUnidadeMedida(item.getUnidadeMedida());
		itemOriginal.setFatorConversao(item.getFatorConversao());
		itemOriginal.setQtdeSolicitada(item.getQtdeSolicitada());
		itemOriginal.setValorUnitario(item.getValorUnitario());		
		itemOriginal.setDtExclusao(item.getDtExclusao());	
		
		if (itemOriginal.getIndExclusao() && !Boolean.TRUE.equals(item.getIndExclusao())) {
			itemOriginal.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
			item.setIndSituacao(itemOriginal.getIndSituacao());
		} else if (Boolean.TRUE.equals(item.getIndExclusao())) {
			itemOriginal.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.EX);
		} else {
			itemOriginal.setIndSituacao(item.getIndSituacao());
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		item.setDesabilitarEdicaoItem(desabilitarPermisaoSituacao(
				servidorLogado.getUsuario(), "gerarAF", "gravar",
				itemOriginal.getIndSituacao()));
		
		itemOriginal.setIndAnaliseProgrPlanej(item.getIndAnaliseProgrPlanej());
		itemOriginal.setIndConsignado(item.getIndConsignado());
		itemOriginal.setIndContrato(item.getIndContrato());
		itemOriginal.setIndEstorno(item.getIndEstorno());
		itemOriginal.setIndExclusao(item.getIndExclusao());
		itemOriginal.setIndPreferencialCum(item.getIndPreferencialCum());
		itemOriginal.setIndProgrEntgAuto(item.getIndProgrEntgAuto());
		
		itemOriginal.setMarcaComercial(item.getMarcaComercial());
		itemOriginal.setModeloComercial(item.getModeloComercial());
		itemOriginal.setUmdCodigoForn(item.getUmdCodigoForn());
		itemOriginal.setFatorConversaoForn(item.getFatorConversaoForn());
		itemOriginal.setPercDescontoItem(item.getPercDescontoItem());
		itemOriginal.setPercDesconto(item.getPercDesconto());
		itemOriginal.setPercAcrescimoItem(item.getPercAcrescimoItem());
		itemOriginal.setPercAcrescimo(item.getPercAcrescimo());
		itemOriginal.setPercVarPreco(item.getPercVarPreco());
		itemOriginal.setPercIpi(item.getPercIpi());
		
		// RN 23 e RN 24
		this.getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(itemOriginal);
		
				
		/**
		 * RN 28 Comentada a pedido no analista Paulo Ricardo issue ##29145
		 */
		//this.getScoAutorizacaoFornON().atualizarServidorAF(scoItemAutorizacaoForn.getAutorizacoesForn(), servidorLogado);
		
		/****
		 * RN 30 
		 * ***/
		if (itemOriginal.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EX) &&
			!indSituacaoAntiga.equals(itemOriginal.getIndSituacao())){
			ScoItemPropostaFornecedor itemPropostaFornecedor = itemOriginal.getItemPropostaFornecedor();
			
			if (itemPropostaFornecedor != null){
				ScoItemLicitacao itemLicitacao = scoItemLicitacaoDAO.obterPorChavePrimaria(itemPropostaFornecedor.getItemLicitacao().getId());
				if (itemLicitacao !=null){
					ScoLicitacao licitacao = this.getScoLicitacaoDAO().obterPorChavePrimaria(itemLicitacao.getLicitacao().getNumero());
					ScoLicitacao licitacaoOld = this.getScoLicitacaoDAO().obterOriginal(itemLicitacao.getLicitacao().getNumero());
					licitacao.setSituacao(DominioSituacaoLicitacao.JU);
					this.getPacFacade().alterarLicitacao(licitacao, licitacaoOld);
				}
			}			
		}
		
		item.setIndSituacao(itemOriginal.getIndSituacao());
		
	}
	
	/*public void gravarItemAF(ItensAutFornVO item) throws BaseException{
		this.getItemAFAtualizaSCContratoON().atualizarSCContrato(item);
		this.alterarItemAF(item);
	}*/
	
	/**** RN 36 
	 * @throws BaseException ***/
	public void atualizarFaseSolicitacao(ScoFaseSolicitacao fase) throws BaseException{		
		fase.setExclusao(false);
		fase.setDtExclusao(null);
		this.getComprasFacade().atualizarScoFaseSolicitacao(fase, fase);	
		ScoSolicitacaoDeCompra  solicitacaoCompra = fase.getSolicitacaoDeCompra();
		solicitacaoCompra.setExclusao(false);
		solicitacaoCompra.setDtExclusao(null);
		solicitacaoCompra.setMotivoExclusao(null);
		this.getScoSolicitacoesDeComprasDAO().atualizar(solicitacaoCompra);
	}
	
	public Double obterValorSaldoItemAF(Double percIPI, Double percAcrescimo, Double percAcrescimoItem, Double percDesconto,
										Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida,
										BigDecimal valorEfetivado, Boolean isSc) {

		Double valorBruto = this.obterValorBrutoItemAF(valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
		Double valorDesconto = this.obterValorDescontoItemAF(percDesconto, percDescontoItem, valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
		Double valorAcrescimo = this.obterValorAcrescimoItemAF(percAcrescimo, percAcrescimoItem, valorUnitario, qtdeSolicitada, qtdeRecebida, valorEfetivado, isSc);
		Double valorIPI = this.obterValorIpiItemAF(percIPI, percAcrescimo,	percAcrescimoItem, percDesconto, percDescontoItem,
												   valorUnitario, qtdeSolicitada, qtdeRecebida,valorEfetivado, isSc);

		if (valorBruto != null && valorDesconto != null
				&& valorAcrescimo != null && valorIPI != null) {
			return (((valorBruto - valorDesconto) + valorAcrescimo) - (valorIPI));
		} else {
			return new Double(0);
		}
	}
	
	public Double obterValorTotalItemAF(Double valorEfetivado, Double percIPI,
										Double percAcrescimo, Double percAcrescimoItem,
										Double percDesconto, Double percDescontoItem, Double valorUnitario,
										Integer qtdeSolicitada, Integer qtdeRecebida, Boolean isSc) {

		Double valorSaldo = this.obterValorSaldoItemAF(percIPI, percAcrescimo,
				percAcrescimoItem, percDesconto, percDescontoItem,
				valorUnitario, qtdeSolicitada, qtdeRecebida, new BigDecimal(valorEfetivado.toString()), isSc);

		if (valorEfetivado != null && valorSaldo != null) {
			return valorEfetivado + valorSaldo;
		} else {
			return new Double(0);
		}
	}    
    
	/*public Integer obterCodigoMaterialServico(List<ScoFaseSolicitacao> fases) {

		Integer codigoMatServ = null;

		if (fases != null && !fases.isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fases).get(0);
			if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {
				codigoMatServ = fase.getSolicitacaoDeCompra().getMaterial().getCodigo();
			} else {
				codigoMatServ = fase.getSolicitacaoServico().getServico().getCodigo();
			}
		}
		return codigoMatServ;
	}

	public String obterNomeMaterialServico(List<ScoFaseSolicitacao> fases) {

		String nomeMatServ = null;

		if (fases != null && !fases.isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fases).get(0);
			if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {
				nomeMatServ = fase.getSolicitacaoDeCompra().getMaterial().getNome();
			} else {
				nomeMatServ = fase.getSolicitacaoServico().getServico().getNome();
			}
		}
		return nomeMatServ;
	}*/
	
	public Boolean verificarMaterialFase(List<ScoFaseSolicitacao> fases) {
		
		if (fases != null && !fases.isEmpty()) {
			ScoFaseSolicitacao fase = this.scoFaseSolicitacaoDAO.obterPorChavePrimaria(new ArrayList<ScoFaseSolicitacao>(fases).get(0).getNumero());			
			return (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null);
		}
		return null;
	}


	/*public String obterDescricaoMaterialServico(List<ScoFaseSolicitacao> fases) {

		String descricaoMatServ = null;

		if (fases != null && !fases.isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fases).get(0);
			if (fase.getSolicitacaoDeCompra() != null
					&& fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {
				descricaoMatServ = fase.getSolicitacaoDeCompra().getMaterial().getDescricao();
			} else {
				descricaoMatServ = fase.getSolicitacaoServico().getServico().getDescricao();
			}
		}
		return descricaoMatServ;
	}*/

	public String obterDescricaoSolicitacao(List<ScoFaseSolicitacao> fases) {
		String descricaoSolicitacao = null;

		if (fases != null && !fases.isEmpty()) {
			ScoFaseSolicitacao fase = this.scoFaseSolicitacaoDAO.obterPorChavePrimaria(new ArrayList<ScoFaseSolicitacao>(fases).get(0).getNumero());			
			if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {
				descricaoSolicitacao = fase.getSolicitacaoDeCompra().getDescricao();
			} else {
				descricaoSolicitacao = fase.getSolicitacaoServico().getDescricao();
			}

		}
		return descricaoSolicitacao;
	}
	
	public Integer obterNumeroSolicitacao(List<ScoFaseSolicitacao> fases){		
		Integer nroSolicitacao= null;
		
		if(fases != null && !fases.isEmpty()){
			ScoFaseSolicitacao fase = this.scoFaseSolicitacaoDAO.obterPorChavePrimaria(new ArrayList<ScoFaseSolicitacao>(fases).get(0).getNumero());			 
			if(fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null){
				nroSolicitacao = fase.getSolicitacaoDeCompra().getNumero();
			}
			else{
				nroSolicitacao = fase.getSolicitacaoServico().getNumero();
			}
				
		}
		return nroSolicitacao;
	}
	

	/*public String obterUnidadeMaterial(List<ScoFaseSolicitacao> fases) {
		String unidadeMaterial = null;

		if (fases != null && !fases.isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fases).get(0);
			if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {
				unidadeMaterial = fase.getSolicitacaoDeCompra().getMaterial().getUmdCodigo();
			}
			if (fase.getSolicitacaoDeCompra() != null) {
				unidadeMaterial = "UN";
			}
		}
		return unidadeMaterial;
	}*/
	
	public MaterialServicoVO obterDadosMaterialServico(List<ScoFaseSolicitacao> fases){		

		Integer codigoMatServ = null;
		String nomeMatServ = null;
		String descricaoMatServ = null;
		String unidadeMaterial = null;
		Boolean estocavelMat = null;



		if (fases != null && !fases.isEmpty()) {

			ScoFaseSolicitacao fase = this.scoFaseSolicitacaoDAO.obterPorChavePrimaria(new ArrayList<ScoFaseSolicitacao>(fases).get(0).getNumero());
			
			if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {
  
				codigoMatServ = fase.getSolicitacaoDeCompra().getMaterial().getCodigo();
				nomeMatServ = fase.getSolicitacaoDeCompra().getMaterial().getNome();
				descricaoMatServ = fase.getSolicitacaoDeCompra().getMaterial().getDescricao();
				unidadeMaterial = fase.getSolicitacaoDeCompra().getMaterial().getUmdCodigo();
				estocavelMat = fase.getSolicitacaoDeCompra().getMaterial().getEstocavel();

			} else {
				codigoMatServ = fase.getSolicitacaoServico().getServico().getCodigo();
				nomeMatServ = fase.getSolicitacaoServico().getServico().getNome();
				descricaoMatServ = fase.getSolicitacaoServico().getServico().getDescricao();
				unidadeMaterial = "UN";				
			}

		}

		MaterialServicoVO materialServicoVO = new MaterialServicoVO();
		materialServicoVO.setCodigoMatServ(codigoMatServ);
		materialServicoVO.setNomeMatServ(nomeMatServ);
		materialServicoVO.setDescricaoMatServ(descricaoMatServ);
		materialServicoVO.setUnidadeMaterial(unidadeMaterial);
		materialServicoVO.setEstocavelMat(estocavelMat);

		return materialServicoVO;
	}

	
    public boolean desabilitarPermisaoSituacao(String login, String componente, String metodo, DominioSituacaoAutorizacaoFornecedor situacaoAf){	 
    	if (situacaoAf != null) {
	        return ((!getICascaFacade().usuarioTemPermissao(login, componente, metodo)) || (situacaoAf.equals(DominioSituacaoAutorizacaoFornecedor.EF))
	        		|| (situacaoAf.equals(DominioSituacaoAutorizacaoFornecedor.EX)) || (situacaoAf.equals(DominioSituacaoAutorizacaoFornecedor.EP)));
    	}
    	else {
    		return (!getICascaFacade().usuarioTemPermissao(login, componente, metodo)); 
    	}
	}	

    /*** RN 38 ***/
    public boolean desabilitarCheckExclusao(DominioSituacaoAutorizacaoFornecedor situacaoAf){
    	return !(situacaoAf.equals(DominioSituacaoAutorizacaoFornecedor.AE) ||
    			 situacaoAf.equals(DominioSituacaoAutorizacaoFornecedor.EX));
	}
    
	protected ICascaFacade getICascaFacade() {
		return this.cascaFacade;
	}
	
	
	protected IPacFacade getPacFacade() {
		return pacFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected MantemItemAutFornValidacoesON getManterItemAutFornValidacoesON() {
		return mantemItemAutFornValidacoesON;		
	}
	
	protected ItemAFAtualizaSCContratoON getItemAFAtualizaSCContratoON() {
		return itemAFAtualizaSCContratoON;
	}	
	
	
	private ItemAFAtualizaConsignadoON getItemAFAtualizaConsignadoON() {
		return itemAFAtualizaConsignadoON;
	}
	
	private ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN() {
		return scoItemAutorizacaoFornRN;
	}
	
	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}

	
	public ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO(){
		return scoFaseSolicitacaoDAO;
	}
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
