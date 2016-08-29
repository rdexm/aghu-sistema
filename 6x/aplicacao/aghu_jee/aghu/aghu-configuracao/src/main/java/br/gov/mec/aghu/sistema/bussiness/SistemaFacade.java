package br.gov.mec.aghu.sistema.bussiness;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.sistema.bussiness.UserSessions.UserSession;

/**
 * Porta de entrada do módulo Sistema.
 * 
 * @author lcmoura
 * 
 */

@Modulo(ModuloEnum.CONFIGURACAO)
@Stateless
public class SistemaFacade extends BaseFacade implements ISistemaFacade {

	private static final long serialVersionUID = 9137097015902027175L;

	@EJB
	private SistemaON sistemaON;
	
	@Inject
	private AppSessionsBean appSessionsBean;
	
	@Override
	@SuppressWarnings("rawtypes")
//	@TransactionTimeout(6000000)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void indexar(Class clazz) throws InterruptedException {
		sistemaON.indexar(clazz);
	}
	
//	@Override
//	@SuppressWarnings("rawtypes")
//	public Directory getLuceneDirectory(Class clazz) {
//		return sistemaON.getLuceneDirectory(clazz);
//	}

	@Override
	public Map<String, UserSession> listarUsuariosLogados(){
		return appSessionsBean.listarUsuariosLogados();
	}
}
