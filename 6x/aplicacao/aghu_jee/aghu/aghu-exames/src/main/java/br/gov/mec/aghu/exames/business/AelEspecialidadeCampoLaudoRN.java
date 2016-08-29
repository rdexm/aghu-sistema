package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelEspecialidadeCampoLaudoDAO;
import br.gov.mec.aghu.model.AelEspecialidadeCampoLaudo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelEspecialidadeCampoLaudoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(AelEspecialidadeCampoLaudoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelEspecialidadeCampoLaudoDAO aelEspecialidadeCampoLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8563788618595490664L;
	
	public enum AelEspecialidadeCampoLaudoRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_ESPECIALIDADE_CAMPO_LAUDO;
	}

	/**
	 * Persiste AelEspecialidadeCampoLaudo
	 * @param especialidadeCampoLaudo
	 * @throws BaseException
	 */
	public void persistirEspecialidadeCampoLaudo(AelEspecialidadeCampoLaudo especialidadeCampoLaudo) throws BaseException{
		
		if (especialidadeCampoLaudo.getId() == null) {
			this.inserir(especialidadeCampoLaudo);
		} else {
			this.atualizar(especialidadeCampoLaudo);
		}

		try {
			// Caso nenhum erro ocorra faz o flush das alterações
			this.getAelEspecialidadeCampoLaudoDAO().flush();
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(AelEspecialidadeCampoLaudoRNExceptionCode.ERRO_PERSISTIR_ESPECIALIDADE_CAMPO_LAUDO);
		}
	}
	

	/**
	 * Inserir AelEspecialidadeCampoLaudo
	 * @param especialidadeCampoLaudo
	 * @throws BaseException
	 */
	public void inserir(AelEspecialidadeCampoLaudo especialidadeCampoLaudo) throws BaseException{
		this.getAelEspecialidadeCampoLaudoDAO().persistir(especialidadeCampoLaudo);

	}
	
	/**
	 * Atualizar AelEspecialidadeCampoLaudo
	 * @param especialidadeCampoLaudo
	 * @throws BaseException
	 */
	public void atualizar(AelEspecialidadeCampoLaudo especialidadeCampoLaudo) throws BaseException{
		this.getAelEspecialidadeCampoLaudoDAO().atualizar(especialidadeCampoLaudo);
	}
	
	/**
	 * Remover AelEspecialidadeCampoLaudo
	 * @param especialidadeCampoLaudo
	 * @throws BaseException
	 */
	public void remover(AelEspecialidadeCampoLaudo especialidadeCampoLaudo) throws BaseException{
		this.getAelEspecialidadeCampoLaudoDAO().remover(especialidadeCampoLaudo);

	}
	
	/*
	 * Getters para RNs e DAOs
	 */	
	
	protected AelEspecialidadeCampoLaudoDAO getAelEspecialidadeCampoLaudoDAO() {
		return aelEspecialidadeCampoLaudoDAO;
	}

}
