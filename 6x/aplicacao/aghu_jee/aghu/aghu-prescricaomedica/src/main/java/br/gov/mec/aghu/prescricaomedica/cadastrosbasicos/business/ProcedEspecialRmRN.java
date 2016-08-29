package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialRmDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ProcedEspecialRmRN extends BaseBusiness {

	@EJB
	private ProcedEspecialDiversoRN procedEspecialDiversoRN;

	@Inject
	private MpmProcedEspecialRmDAO mpmProcedEspecialRmDAO;
	
	private static final Log LOG = LogFactory.getLog(ProcedEspecialRmRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	} 
	
	private static final long serialVersionUID = -8249176923646739867L;


	public void inserir(MpmProcedEspecialRm material, RapServidores servidorLogado) throws BaseException{
		preInsertProcedEspecialRm(material, servidorLogado);
		getMpmProcedEspecialRmDAO().persistir(material);
	}
	
	private void preInsertProcedEspecialRm(MpmProcedEspecialRm material, RapServidores servidorLogado) throws BaseException {
		material.setServidor(servidorLogado);
		material.setCriadoEm(new Date());
	}
	
	public void atualizar(MpmProcedEspecialRm material, RapServidores servidorLogado) throws BaseException {
		preUpdateProcedEspecialRm(material, servidorLogado);
		getMpmProcedEspecialRmDAO().merge(material);
	}
	
	/**
	 * @ORADB Trigger MPMT_PRR_BRU
	 * 
	 * @param material
	 */
	private void preUpdateProcedEspecialRm(MpmProcedEspecialRm material, RapServidores servidorLogado) throws BaseException {
		//Verifica e atribui o usuário logado
		material.setServidor(servidorLogado);
	}
	
	public void remover(MpmProcedEspecialRm material) throws BaseException {
		getMpmProcedEspecialRmDAO().remover(material);
	}

	public ProcedEspecialDiversoRN getProcedEspecialDiversoRN() {
		return procedEspecialDiversoRN;
	}

	
	protected MpmProcedEspecialRmDAO getMpmProcedEspecialRmDAO() {
		return mpmProcedEspecialRmDAO;
	}
		
}
