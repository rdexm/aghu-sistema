package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe ON responsável pelas regras de negócio dos serviços.
 * 
 * @author mlcruz
 */
@Stateless
public class ScoServicoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoServicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoServicoDAO scoServicoDAO;

@EJB
private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	private static final long serialVersionUID = 7221646527909958525L;

	/**
	 * Inclui serviço.
	 * 
	 * @param servico Serviço.
	 */
	public void incluir(ScoServico servico) {
		getScoServicoDAO().persistir(servico);
	}

	/**
	 * Altera serviço.
	 * 
	 * @param servico Serviço.
	 */
	public void alterar(ScoServico servico) {
		getScoServicoDAO().atualizar(servico);
	}
	
	protected ScoServicoDAO getScoServicoDAO() {
		return scoServicoDAO;
	}
	
	protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return solicitacaoServicoFacade;
	}
}