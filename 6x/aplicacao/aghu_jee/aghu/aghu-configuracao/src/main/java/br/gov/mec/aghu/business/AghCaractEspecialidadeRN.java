package br.gov.mec.aghu.business;


import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghCaractEspecialidadesDAO;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghCaractEspecialidadesId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

class AghCaractEspecialidadeRN extends BaseBusiness {

	
	@Inject
	private AghCaractEspecialidadesDAO aghCaractEspecialidadesDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1617534462919675182L;
	
	private static final Log LOG = LogFactory.getLog(AghCaractEspecialidadeRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}	

	public void atualizarAghCaractEspecialidade(AghCaractEspecialidades aghCaractEspecialidade, AghCaractEspecialidades aghCaractEspecialidadeOld)  throws BaseException{
		this.getAghCaractEspecialidadesDAO().remover(getAghCaractEspecialidadesDAO().obterPorChavePrimaria(aghCaractEspecialidadeOld.getId()));
		this.getAghCaractEspecialidadesDAO().flush();
		AghCaractEspecialidades aghCaractEspecialidadeNova = new AghCaractEspecialidades();
		aghCaractEspecialidadeNova.setEspecialidade(aghCaractEspecialidade.getEspecialidade());
		aghCaractEspecialidadeNova.setId(new AghCaractEspecialidadesId());
		aghCaractEspecialidadeNova.getId().setEspSeq(aghCaractEspecialidade.getId().getEspSeq());
		aghCaractEspecialidadeNova.getId().setCaracteristica(aghCaractEspecialidade.getId().getCaracteristica());
		this.getAghCaractEspecialidadesDAO().persistir(aghCaractEspecialidadeNova);
		this.getAghCaractEspecialidadesDAO().flush();
		
	}

	protected AghCaractEspecialidadesDAO getAghCaractEspecialidadesDAO() {
		return aghCaractEspecialidadesDAO;
	}

}