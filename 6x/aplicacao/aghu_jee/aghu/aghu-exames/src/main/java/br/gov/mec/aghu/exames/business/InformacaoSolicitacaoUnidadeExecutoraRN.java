package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelInformacaoSolicitacaoUnidadeExecutoraDAO;
import br.gov.mec.aghu.model.AelInformacaoSolicitacaoUnidadeExecutora;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Regras de Insert, update e delete da entidade AelInformacaoSolicitacaoUnidadeExecutora.
 * Tabela: ael_inf_solic_un_execs
 * 
 * @author rcorvalao
 */
@Stateless
public class InformacaoSolicitacaoUnidadeExecutoraRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(InformacaoSolicitacaoUnidadeExecutoraRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelInformacaoSolicitacaoUnidadeExecutoraDAO aelInformacaoSolicitacaoUnidadeExecutoraDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1773084063124535494L;

	/**
	 * 
	 * ORADB TRIGGER AELT_AIX_BRI
	 * 
	 * @param entity
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public AelInformacaoSolicitacaoUnidadeExecutora inserir(AelInformacaoSolicitacaoUnidadeExecutora entity) throws ApplicationBusinessException {
		this.preInserir(entity);
		
		this.getInformacaoSolicitacaoUnidadeExecutoraDAO().persistir(entity);
		
		return entity;
	}


	protected void preInserir(AelInformacaoSolicitacaoUnidadeExecutora entity) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// SEQ eh preenchido automaticamento pelo Hibernate.
		entity.setCriadoEm(new Date());
		
		entity.setServidor(servidorLogado);
	}


	protected AelInformacaoSolicitacaoUnidadeExecutoraDAO getInformacaoSolicitacaoUnidadeExecutoraDAO() {
		return aelInformacaoSolicitacaoUnidadeExecutoraDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
