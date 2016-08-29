package br.gov.mec.aghu.exames.solicitacao.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSeqCodbarraRedomeDAO;
import br.gov.mec.aghu.model.AelSeqCodbarraRedome;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelSeqCodbarraRedomeRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelSeqCodbarraRedomeRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSeqCodbarraRedomeDAO aelSeqCodbarraRedomeDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4339235654275954072L;

	public AelSeqCodbarraRedome inserir(AelSeqCodbarraRedome entidade) throws BaseException {

		this.getAelSeqCodbarraRedomeDAO().persistir(entidade);
		this.getAelSeqCodbarraRedomeDAO().flush();

		return entidade;
	}
	
	protected AelSeqCodbarraRedomeDAO getAelSeqCodbarraRedomeDAO() {
		return aelSeqCodbarraRedomeDAO;
	}

}
