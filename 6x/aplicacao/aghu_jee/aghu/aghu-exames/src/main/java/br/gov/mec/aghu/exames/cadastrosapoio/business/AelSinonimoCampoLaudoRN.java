package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSinonimoCampoLaudoDAO;
import br.gov.mec.aghu.model.AelSinonimoCampoLaudo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelSinonimoCampoLaudoRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(AelSinonimoCampoLaudoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelSinonimoCampoLaudoDAO aelSinonimoCampoLaudoDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2937553725387875184L;

	
	public enum AelSinonimoCampoLaudoRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_SINONIMO_CAMPO_LAUDO;
	}
	
	/**
	 * Persiste AelSinonimoCampoLaudo
	 * @param sinonimoCampoLaudo
	 * @throws BaseException
	 */
	public void persistirSinonimoCampoLaudo(AelSinonimoCampoLaudo sinonimoCampoLaudo) throws BaseException{
		
		if (sinonimoCampoLaudo.getId() == null) {
			this.inserir(sinonimoCampoLaudo);
		} else {
			this.atualizar(sinonimoCampoLaudo);
		}

		try {
			// Caso nenhum erro ocorra faz o flush das alterações
			this.getAelSinonimoCampoLaudoDAO().flush();
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(AelSinonimoCampoLaudoRNExceptionCode.ERRO_PERSISTIR_SINONIMO_CAMPO_LAUDO);
		}
	}
	
	/**
	 * ORADB TRIGGER AELT_SCL_BRI (INSERT)
	 * @param sinonimoCampoLaudo
	 * @throws BaseException
	 */
	private void preInserir(AelSinonimoCampoLaudo sinonimoCampoLaudo) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		sinonimoCampoLaudo.setServidor(servidorLogado);
	}

	/**
	 * Inserir AelSinonimoCampoLaudo
	 * @param sinonimoCampoLaudo
	 * @throws BaseException
	 */
	public void inserir(AelSinonimoCampoLaudo sinonimoCampoLaudo) throws BaseException{
		this.preInserir(sinonimoCampoLaudo);
		this.getAelSinonimoCampoLaudoDAO().persistir(sinonimoCampoLaudo);
	}
	
	/**
	 * Atualizar AelSinonimoCampoLaudo
	 * @param sinonimoCampoLaudo
	 * @throws BaseException
	 */
	public void atualizar(AelSinonimoCampoLaudo sinonimoCampoLaudo) throws BaseException{
		this.getAelSinonimoCampoLaudoDAO().merge(sinonimoCampoLaudo);
	}
	
	protected AelSinonimoCampoLaudoDAO getAelSinonimoCampoLaudoDAO() {
		return aelSinonimoCampoLaudoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
			
}
