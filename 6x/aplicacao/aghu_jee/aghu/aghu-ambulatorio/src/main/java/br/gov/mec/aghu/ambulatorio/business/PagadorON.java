package br.gov.mec.aghu.ambulatorio.business;



import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacPagadorDAO;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;

@Stateless
public class PagadorON extends BaseBusiness {





private static final Log LOG = LogFactory.getLog(PagadorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AacPagadorDAO aacPagadorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -102899110051621482L;

	/**
	 * Método para obter <code>AacPagador</code> a partir da descrição.
	 * 
	 * @param codigo
	 * @param descricao
	 * @return List de <code>AacPagador</code>
	 */
	@Secure("#{s:hasPermission('pagador','pesquisar')}")
	public AacPagador obterPagador(Short codigo) {
		return getAacPagadorDAO().obterPagador(codigo);
	}

	AacPagadorDAO getAacPagadorDAO() {
		return aacPagadorDAO;
	}
}
