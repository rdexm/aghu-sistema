package br.gov.mec.aghu.core.business;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.moduleintegration.ModuleChecked;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;

/**
 * SuperClasse de todas as Fachadas do sistema. declara o interceptor para
 * verificar se o módulo está ativo. declara os métodos de log para
 * compatibilidade com código na arquitetura antiga
 * 
 */
@SuppressWarnings("PMD.HierarquiaFachadaIncorreta")
@ModuleChecked
public abstract class BaseFacade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 77392629292707665L;
	
	@Resource
	protected SessionContext sessionContext;
	
	@Inject
	private DataAccessService dataAcess;
	
	
	protected void flush(){
		this.dataAcess.flush();
	}
	
	protected <E extends BaseEntity>  void  refresh(E entidade){
		this.dataAcess.refresh(entidade);
	}
	
    protected <E extends BaseEntity>  void  evict(E entidade){
		this.dataAcess.evict(entidade);
	}
    
    protected void clear(){
    	this.dataAcess.entityManagerClear();
    }
	
	protected SessionContext getSessionContext() {
		return sessionContext;
	}
}