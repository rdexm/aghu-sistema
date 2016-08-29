package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacEspecialidadePmpaDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.model.AacEspecialidadePmpa;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ManterEspecialidadePmpaRN extends BaseBusiness {
	
	
	private static final Log LOG = LogFactory.getLog(ManterEspecialidadePmpaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AacEspecialidadePmpaDAO aacEspecialidadePmpaDAO;

	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2092050085161100892L;

	public void remover(AacEspecialidadePmpa aacEspecialidadePmpa)
			throws ApplicationBusinessException {
		
		if(aacEspecialidadePmpa == null || aacEspecialidadePmpa.getAghEspecialidades() == null){
			throw new IllegalArgumentException();
		}
		
		AghEspecialidades aghEspecialidades = this.getAghEspecialidadesDAO()
				.obterPorChavePrimaria(
						aacEspecialidadePmpa.getAghEspecialidades().getSeq());
		aacEspecialidadePmpa.setAghEspecialidades(aghEspecialidades);
		
		this.aacEspecialidadePmpaDAO.removerPorId(aacEspecialidadePmpa.getId());
	}

	public void persistir(final AacEspecialidadePmpa aacEspecialidadePmpa)
			throws BaseException {
		
		if(aacEspecialidadePmpa == null || aacEspecialidadePmpa.getAghEspecialidades() == null){
			throw new IllegalArgumentException();
		}
		
		AghEspecialidades aghEspecialidades = this.getAghEspecialidadesDAO()
				.obterPorChavePrimaria(
						aacEspecialidadePmpa.getAghEspecialidades().getSeq());
		aacEspecialidadePmpa.setAghEspecialidades(aghEspecialidades);
		
		this.aacEspecialidadePmpaDAO.persistir(aacEspecialidadePmpa);
	}
	
	public AacEspecialidadePmpaDAO getAacEspecialidadePmpaDAO() {
		return aacEspecialidadePmpaDAO;
	}

	public void setAacEspecialidadePmpaDAO(
			AacEspecialidadePmpaDAO aacEspecialidadePmpaDAO) {
		this.aacEspecialidadePmpaDAO = aacEspecialidadePmpaDAO;
	}

	public AghEspecialidadesDAO getAghEspecialidadesDAO() {
		return aghEspecialidadesDAO;
	}

	public void setAghEspecialidadesDAO(AghEspecialidadesDAO aghEspecialidadesDAO) {
		this.aghEspecialidadesDAO = aghEspecialidadesDAO;
	}

}
