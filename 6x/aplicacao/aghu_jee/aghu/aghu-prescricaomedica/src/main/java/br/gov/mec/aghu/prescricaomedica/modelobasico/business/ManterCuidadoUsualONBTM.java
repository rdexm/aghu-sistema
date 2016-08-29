package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualUnfDAO;

/**
 * @author mgoulart
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ManterCuidadoUsualONBTM extends BaseBMTBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8064403242774335464L;

	private static final int TEMPO_12_HORAS = (60 * 60 * 12); // 12 horas

	private static final Log LOG = LogFactory.getLog(ManterCuidadoUsualONBTM.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MpmCuidadoUsualUnfDAO mpmCuidadoUsualUnfDAO;

	public void incluirTodosCuidadosUnf(RapServidores rapServidores) throws ApplicationBusinessException {
		beginTransaction(TEMPO_12_HORAS);
		try {
			this.getMpmCuidadoUsualUnfDAO().inserirTodosCuidadosUnf(rapServidores);
		} catch (ApplicationBusinessException e) {
			rollbackTransaction();
			throw new ApplicationBusinessException(e);
		}
		
		commitTransaction();
	}
	
	protected MpmCuidadoUsualUnfDAO getMpmCuidadoUsualUnfDAO() {
		return mpmCuidadoUsualUnfDAO;
	}
	
}