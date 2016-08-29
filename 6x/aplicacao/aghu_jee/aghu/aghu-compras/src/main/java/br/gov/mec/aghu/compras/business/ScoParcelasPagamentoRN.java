package br.gov.mec.aghu.compras.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ScoParcelasPagamentoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ScoParcelasPagamentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8067774134360772224L;

	private void preInserirParcelasPagamento(ScoParcelasPagamento parcela) throws ApplicationBusinessException {
		//
	}

	private void posInserirParcelasPagamento(ScoParcelasPagamento parcela) throws ApplicationBusinessException {
		//
	}
	
	@SuppressWarnings("ucd")
	/**
	 * Persiste item de parcela de pagamento.
	 * 
	 * @param itemParcela Item de parcela de pagamento.
	 * @throws ApplicationBusinessException
	 */
	public void persistir(ScoParcelasPagamento itemParcela) throws ApplicationBusinessException {
		if (itemParcela.getSeq() == null) {
			inserirParcelasPagamento(itemParcela);
		} else {
			alterarParcelasPagamento(itemParcela);
		}
	}
	
	private void inserirParcelasPagamento(ScoParcelasPagamento parcela) throws ApplicationBusinessException {
		this.preInserirParcelasPagamento(parcela);
		this.getScoParcelasPagamentoDAO().persistir(parcela);
		this.posInserirParcelasPagamento(parcela);
	}
	
	private void preAlterarParcelasPagamento(ScoParcelasPagamento parcela) throws ApplicationBusinessException {
		//
	}

	private void posAlterarParcelasPagamento(ScoParcelasPagamento parcela) throws ApplicationBusinessException {
		//
	}
	
	private void alterarParcelasPagamento(ScoParcelasPagamento parcela) throws ApplicationBusinessException {
		this.preAlterarParcelasPagamento(parcela);
		this.getScoParcelasPagamentoDAO().atualizar(parcela);
		this.posAlterarParcelasPagamento(parcela);
	}

	private void preExcluirParcelasPagamento(ScoParcelasPagamento proposta) throws ApplicationBusinessException {
		//
	}

	private void posExcluirParcelasPagamento(ScoParcelasPagamento proposta) throws ApplicationBusinessException {
	//	
	}
	
	@SuppressWarnings("ucd")
	public void remover(ScoParcelasPagamento parcela) throws ApplicationBusinessException {
		this.preExcluirParcelasPagamento(parcela);
		this.getScoParcelasPagamentoDAO().remover(parcela);
		this.posExcluirParcelasPagamento(parcela);
	}

	protected ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}
}
