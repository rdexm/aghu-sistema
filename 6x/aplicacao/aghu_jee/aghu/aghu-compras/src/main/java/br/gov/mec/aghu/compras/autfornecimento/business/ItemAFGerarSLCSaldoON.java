/**
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ItemAFGerarSLCSaldoON extends BaseBusiness{
	
	@EJB
	private ScoAutorizacaoFornRN scoAutorizacaoFornRN;
	
	private static final Log LOG = LogFactory.getLog(ItemAFGerarSLCSaldoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4069119530195414334L;
	
	
	/*** RN 25 - PLL GERA_SLC_SALDO	 ***/
	public Boolean verificarExisteSaldo(ItensAutFornVO itemAutorizacaoForn) {

		List<ScoFaseSolicitacao> listaFaseSolicitacao = itemAutorizacaoForn
				.getScoFaseSolicitacao();

		if (listaFaseSolicitacao != null && !listaFaseSolicitacao.isEmpty()) {

			ScoFaseSolicitacao faseSolicitacao = listaFaseSolicitacao.get(0);
			faseSolicitacao = this.getScoFaseSolicitacaoDAO().merge(faseSolicitacao);
			
			ScoMaterial material = null;

			if (faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.C)) {
				material = faseSolicitacao.getSolicitacaoDeCompra()
						.getMaterial();
			}

			return (material != null ? (faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.C) && 
						!material.getIndEstocavel().isSim()
						&& (itemAutorizacaoForn.getQtdeSolicitada()	- itemAutorizacaoForn.getQtdeRecebida() > 0) &&
						(itemAutorizacaoForn.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.AE) ||
						itemAutorizacaoForn.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.PA))) : false);
			
		} else {
			return null;
		}		

	}
	/*** RN 27 - PLL GERA_SLC_SALDO	 */
	public void alterarSituacaoItem(ItensAutFornVO itemAutorizacaoFornVo) throws BaseException{
		List<ScoFaseSolicitacao> listaFaseSolicitacao = itemAutorizacaoFornVo
				.getScoFaseSolicitacao();

		this.logInfo("alterarSituacaoItem");
		
		if (listaFaseSolicitacao != null && !listaFaseSolicitacao.isEmpty()) {
			
			ScoFaseSolicitacao faseSolicitacao = listaFaseSolicitacao.get(0);
			
			if (faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.C)) {				
				
				if (itemAutorizacaoFornVo.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.AE) ||
					itemAutorizacaoFornVo.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.PA)){
					
					itemAutorizacaoFornVo.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.EP);
					itemAutorizacaoFornVo.setIndExclusao(false);					
					if (this.getScoItemAutorizacaoFornDAO().verificaItensSituacaoAEPA(itemAutorizacaoFornVo.getAfnNumero(), itemAutorizacaoFornVo.getNumero()) == 0){
						ScoAutorizacaoForn scoAutorizacaoForn = this.getScoAutorizacaoFornDAO().obterPorChavePrimaria(itemAutorizacaoFornVo.getAfnNumero());
						scoAutorizacaoForn.setExclusao(false);
						scoAutorizacaoForn.setSituacao(DominioSituacaoAutorizacaoFornecimento.EF);
						this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(scoAutorizacaoForn);	
					}
					
				}
				else if (itemAutorizacaoFornVo.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EP)) {
					itemAutorizacaoFornVo.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.PA);
					itemAutorizacaoFornVo.setIndExclusao(false);
					ScoAutorizacaoForn scoAutorizacaoForn = this.getScoAutorizacaoFornDAO().obterPorChavePrimaria(itemAutorizacaoFornVo.getAfnNumero());
					scoAutorizacaoForn.setSituacao(DominioSituacaoAutorizacaoFornecimento.PA);
					scoAutorizacaoForn.setExclusao(false);		
					this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(scoAutorizacaoForn);	
				}
				else if (itemAutorizacaoFornVo.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EF)) {
					itemAutorizacaoFornVo.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.PA);
					itemAutorizacaoFornVo.setQtdeSolicitada(itemAutorizacaoFornVo.getQtdeSolicitada() + 1);					
				}
				//this.getManterItemAutFornecimentoON().alterarItemAF(itemAutorizacaoFornVo);				
				
			}
			else if (faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.S)){
				itemAutorizacaoFornVo.setValorUnitario(itemAutorizacaoFornVo.getValorUnitario() + 1);
				ScoAutorizacaoForn scoAutorizacaoForn = this.getScoAutorizacaoFornDAO().obterPorChavePrimaria(itemAutorizacaoFornVo.getAfnNumero());
				scoAutorizacaoForn.setSituacao(DominioSituacaoAutorizacaoFornecimento.PA);
				scoAutorizacaoForn.setExclusao(false);
				this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(scoAutorizacaoForn);				
				
				if (itemAutorizacaoFornVo.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EF)){
					itemAutorizacaoFornVo.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.PA);
					itemAutorizacaoFornVo.setQtdeSolicitada(itemAutorizacaoFornVo.getQtdeSolicitada() + 1);
				}
				//this.getManterItemAutFornecimentoON().alterarItemAF(itemAutorizacaoFornVo);
			}			
		}		
	}

	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}

	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	protected ScoAutorizacaoFornRN getScoAutorizacaoFornRN() {
		return scoAutorizacaoFornRN;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
}
