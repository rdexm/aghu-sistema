package br.gov.mec.aghu.compras.autfornecimento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * RN responsável por persistir parcelamento de entrega.
 * 
 * @author matheus
 */
@Stateless
public class ScoSolicitacaoProgramacaoEntregaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoSolicitacaoProgramacaoEntregaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;
	private static final long serialVersionUID = 539646983386494922L;
	
	/**
	 * Insere ou atualiza parcelamento de entrega.
	 * 
	 * @param solicitacaoProgramacaoEntrega Parcelamento
	 * @throws ApplicationBusinessException
	 */
	public void persistir(ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega)
			throws ApplicationBusinessException {
		if (solicitacaoProgramacaoEntrega.getSeq() == null) {
			inserir(solicitacaoProgramacaoEntrega);
		} else {
			atualizar(solicitacaoProgramacaoEntrega);
		}
	}
	
	/**
	 * Insere parcelamento de entrega.
	 * 
	 * @param solicitacaoProgramacaoEntrega Parcelamento
	 * @throws ApplicationBusinessException
	 */
	private void inserir(ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega)
			throws ApplicationBusinessException {
		this.getScoSolicitacaoProgramacaoEntregaDAO().persistir(solicitacaoProgramacaoEntrega);
	}
	
	/**
	 * Atualiza parcelamento de entrega.
	 * 
	 * @param solicitacaoProgramacaoEntrega Parcelamento
	 * @throws ApplicationBusinessException
	 */
	private void atualizar(ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega)
			throws ApplicationBusinessException {
		this.getScoSolicitacaoProgramacaoEntregaDAO().atualizar(solicitacaoProgramacaoEntrega);
	}
	
	/**
	 * Remove parcelamento de entrega.
	 * 
	 * @param solicitacaoProgramacaoEntrega Parcelamento
	 * @throws ApplicationBusinessException
	 */
	public void remover(ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega)
			throws ApplicationBusinessException {
		this.getScoSolicitacaoProgramacaoEntregaDAO().remover(solicitacaoProgramacaoEntrega);
	}
	
	protected ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {
		return scoSolicitacaoProgramacaoEntregaDAO;
	}

}
