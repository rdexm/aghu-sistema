package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelServidorCampoLaudoDAO;
import br.gov.mec.aghu.model.AelServidorCampoLaudo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelServidorCampoLaudoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(AelServidorCampoLaudoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelServidorCampoLaudoDAO aelServidorCampoLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8563788618595490664L;
	
	public enum AelServidorCampoLaudoRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_SERVIDOR_CAMPO_LAUDO;
	}

	/**
	 * Persiste AelServidorCampoLaudo
	 * @param servidorCampoLaudo
	 * @throws BaseException
	 */
	public AelServidorCampoLaudo persistirCampoLaudo(AelServidorCampoLaudo servidorCampoLaudo) throws BaseException{
		
	
		
		if (servidorCampoLaudo.getId() == null) {
			this.inserir(servidorCampoLaudo);
		} else {
			servidorCampoLaudo = this.atualizar(servidorCampoLaudo);
		}

		try {
			// Caso nenhum erro ocorra faz o flush das alterações
			this.getAelServidorCampoLaudoDAO().flush();
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(AelServidorCampoLaudoRNExceptionCode.ERRO_PERSISTIR_SERVIDOR_CAMPO_LAUDO);
		}
		
		return servidorCampoLaudo;
	}
	

	/**
	 * Inserir AelServidorCampoLaudo
	 * @param servidorCampoLaudo
	 * @throws BaseException
	 */
	public void inserir(AelServidorCampoLaudo servidorCampoLaudo) throws BaseException{
		this.getAelServidorCampoLaudoDAO().persistir(servidorCampoLaudo);

	}
	
	/**
	 * Atualizar AelServidorCampoLaudo
	 * @param servidorCampoLaudo
	 * @throws BaseException
	 */
	public AelServidorCampoLaudo atualizar(AelServidorCampoLaudo servidorCampoLaudo) throws BaseException{
		return this.getAelServidorCampoLaudoDAO().merge(servidorCampoLaudo);
	}
	
	/**
	 * Remover AelServidorCampoLaudo
	 * @param servidorCampoLaudo
	 * @throws BaseException
	 */
	public void remover(AelServidorCampoLaudo servidorCampoLaudo) throws BaseException{
		this.getAelServidorCampoLaudoDAO().remover(servidorCampoLaudo);

	}
	
	/*
	 * Getters para RNs e DAOs
	 */	
	
	protected AelServidorCampoLaudoDAO getAelServidorCampoLaudoDAO() {
		return aelServidorCampoLaudoDAO;
	}

}
