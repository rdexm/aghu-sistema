package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.PriorizaEntregaVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceItemRecbXProgrEntregaDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.model.SceItemRecbXProgrEntrega;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceItemRecebProvisorioId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class SceItemRecbXProgrEntregaON extends BaseBusiness {

@EJB
private SceItemRecbXProgrEntregaRN sceItemRecbXProgrEntregaRN;

private static final Log LOG = LogFactory.getLog(SceItemRecbXProgrEntregaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAutFornecimentoFacade autFornecimentoFacade;

@Inject
private SceItemRecbXProgrEntregaDAO sceItemRecbXProgrEntregaDAO;

@Inject
private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6207171292876196620L;

	/**
	 * Realiza as operacoes de persistencia das listas de priorizacao de entrega quando
	 * utilizado diretamente pela tela de confirmacao de recebimento ou desbloqueio de material,
	 * realizando as validacoes necessarias
	 * @param listaPriorizacao
	 */
	public void persistirSceItemRecbXProgrEntrega(List<PriorizaEntregaVO> listaPriorizacao) throws ApplicationBusinessException {
		for (PriorizaEntregaVO item : listaPriorizacao) {
			SceItemRecbXProgrEntrega itemReceb = null;
			
			if (item.getSeqItemRecbXProgrEntrega() != null) {
				itemReceb = this.getSceItemRecbXProgrEntregaDAO().obterPorChavePrimaria(item.getSeqItemRecbXProgrEntrega());
			}
			
			if (itemReceb != null) {
				if (item.getSolicitacaoCompra() != null) {
					itemReceb.setQtdeEntregue(item.getQtdeRecebidaSolicitacaoCompra().longValue());
				} else {
					itemReceb.setValorEfetivado(item.getValorRecebidoSolicitacaoServico().doubleValue());
				}
			} else {
				itemReceb = new SceItemRecbXProgrEntrega();
				
				itemReceb.setIndEntregaImediata(DominioSimNao.N.toString());
				itemReceb.setIndTramiteInterno(DominioSimNao.N.toString());
				itemReceb.setIndEstornado(false);
				
				if (item.getSolicitacaoCompra() != null) {
					itemReceb.setQtdeEntregue(item.getQtdeRecebidaSolicitacaoCompra().longValue());
				} else {
					itemReceb.setValorEfetivado(item.getValorRecebidoSolicitacaoServico().doubleValue());
				}

				SceItemRecebProvisorioId idItemRecebProv = new SceItemRecebProvisorioId();
				idItemRecebProv.setNrpSeq(item.getSeqRecebimento());
				idItemRecebProv.setNroItem(item.getItemRecebimento());
				SceItemRecebProvisorio sceItemRecebProvisorio = this.getSceItemRecebProvisorioDAO().obterPorChavePrimaria(idItemRecebProv);
				if (sceItemRecebProvisorio != null) {
					itemReceb.setSceItemRecebProvisorio(sceItemRecebProvisorio);
				}
				
				ScoProgEntregaItemAutorizacaoFornecimentoId idProgEntrega = new ScoProgEntregaItemAutorizacaoFornecimentoId(item.getNumeroAf(), item.getNumeroItemAf().hashCode(), item.getSeqProgEntrega(), item.getNumeroParcela()); 
				ScoProgEntregaItemAutorizacaoFornecimento scoProgEntregaItemAutorizacaoFornecimento = 
						this.getAutFornecimentoFacade().obterProgEntregaPorChavePrimaria(idProgEntrega);

				if (scoProgEntregaItemAutorizacaoFornecimento != null) {
					itemReceb.setScoProgEntregaItemAutorizacaoFornecimento(scoProgEntregaItemAutorizacaoFornecimento);
				}
			}
			
			this.getSceItemRecbXProgrEntregaRN().persistir(itemReceb);
		}
	}
	
	protected SceItemRecbXProgrEntregaRN getSceItemRecbXProgrEntregaRN() {
		return sceItemRecbXProgrEntregaRN;
	}
	
	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}
	
	protected SceItemRecbXProgrEntregaDAO getSceItemRecbXProgrEntregaDAO() {
		return sceItemRecbXProgrEntregaDAO;
	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

}
