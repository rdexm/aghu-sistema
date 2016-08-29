package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameEquipCampoLaudDAO;
import br.gov.mec.aghu.model.AelExameEquipCampoLaud;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AelExameEquipCampoLaudRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelExameEquipCampoLaudRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExameEquipCampoLaudDAO aelExameEquipCampoLaudDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1987650131949914390L;

	/**
	 * Insere um registro na <br>
	 * tabela AEL_EXAME_EQUIP_CAMPOS_LAUD
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void inserir(AelExameEquipCampoLaud elemento) throws BaseException {
		this.getAelExameEquipCampoLaudDAO().persistir(elemento);
	}
	
	
	/** GET **/
	protected AelExameEquipCampoLaudDAO getAelExameEquipCampoLaudDAO() {
		return aelExameEquipCampoLaudDAO;
	}
	
}
