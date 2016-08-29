package br.gov.mec.aghu.compras.solicitacaocompra.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoLogGeracaoScMatEstocavelDAO;
import br.gov.mec.aghu.model.ScoLogGeracaoScMatEstocavel;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * RN responsável por persistir log de geração de materiais estocáveis.
 * 
 * @author matheus
 */
@Stateless
public class ScoLogGeracaoScMatEstocavelRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoLogGeracaoScMatEstocavelRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoLogGeracaoScMatEstocavelDAO scoLogGeracaoScMatEstocavelDAO;
	private static final long serialVersionUID = 2985068067606622707L;

	/**
	 * Persiste log de geração.
	 * 
	 * @param log
	 */
	public void persistir(ScoLogGeracaoScMatEstocavel log) {
		getScoLogGeracaoScMatEstocavelDAO().persistir(log);
	}

	protected ScoLogGeracaoScMatEstocavelDAO getScoLogGeracaoScMatEstocavelDAO() {
		return scoLogGeracaoScMatEstocavelDAO;
	}
}