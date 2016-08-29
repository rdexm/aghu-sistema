package br.gov.mec.aghu.compras.pac.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCondicaoPgtoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * RN responsável por manter condição de pagamento de licitação.
 * 
 * @author matheus
 */
@Stateless
public class ScoCondicaoPgtoLicitacaoRN extends BaseBusiness {

@EJB
private ScoCondicaoPgtoLicitacaoValidacaoON scoCondicaoPgtoLicitacaoValidacaoON;

private static final Log LOG = LogFactory.getLog(ScoCondicaoPgtoLicitacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoCondicaoPgtoLicitacaoDAO scoCondicaoPgtoLicitacaoDAO;

@Inject
private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;
	private static final long serialVersionUID = -5180375676145620896L;

	public void persistir(ScoCondicaoPgtoLicitacao condicaoPagamento)
			throws ApplicationBusinessException {
		getScoCondicaoPgtoLicitacaoValidacaoON().validaCondicaoPgto(condicaoPagamento);

		// Grava Condição de Pagamento
		if (condicaoPagamento.getSeq() == null) {
			getScoCondicaoPgtoLicitacaoDAO().persistir(condicaoPagamento);
		} else {
			getScoCondicaoPgtoLicitacaoDAO().atualizar(condicaoPagamento);
		}
	}

	public void remover(Integer seqCondicaoPgto) {		
		List<ScoParcelasPagamento> listaParcelas = getScoParcelasPagamentoDAO().obterParcelasPagamento(seqCondicaoPgto);
		
		for (ScoParcelasPagamento parcela : listaParcelas) {
			getScoParcelasPagamentoDAO().remover(parcela);
		}

		getScoCondicaoPgtoLicitacaoDAO().remover(getScoCondicaoPgtoLicitacaoDAO().obterPorChavePrimaria(seqCondicaoPgto));		
	}

	private ScoCondicaoPgtoLicitacaoDAO getScoCondicaoPgtoLicitacaoDAO() {
		return scoCondicaoPgtoLicitacaoDAO;
	}

	private ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}
	
	private ScoCondicaoPgtoLicitacaoValidacaoON getScoCondicaoPgtoLicitacaoValidacaoON(){
		return scoCondicaoPgtoLicitacaoValidacaoON;
	}

}
