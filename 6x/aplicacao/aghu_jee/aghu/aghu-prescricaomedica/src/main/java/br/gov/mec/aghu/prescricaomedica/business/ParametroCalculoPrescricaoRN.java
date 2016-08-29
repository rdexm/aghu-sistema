package br.gov.mec.aghu.prescricaomedica.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParamCalculoPrescricaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ParametroCalculoPrescricaoRN extends BaseBusiness implements Serializable {

	private static final Log LOG = LogFactory.getLog(ParametroCalculoPrescricaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmParamCalculoPrescricaoDAO mpmParamCalculoPrescricaoDAO;

	private static final long serialVersionUID = 6483787609437950032L;
	
	public void persistir(MpmParamCalculoPrescricao paramCalculoPrescricao) throws ApplicationBusinessException {
		if(paramCalculoPrescricao.getId() == null || paramCalculoPrescricao.getId().getCriadoEm() == null) {
			this.preInserir(paramCalculoPrescricao);
			getMpmParamCalculoPrescricaoDAO().persistir(paramCalculoPrescricao);
		}
		else {
			this.preAtualizar(paramCalculoPrescricao);
		}
	}
	
	/**
	 * @throws ApplicationBusinessException  
	 * @ORADB Trigger MPMT_PCA_BRU
	 */
	public void preAtualizar(MpmParamCalculoPrescricao paramCalculoPrescricao) throws ApplicationBusinessException {
		MpmParamCalculoPrescricao old = getMpmParamCalculoPrescricaoDAO().obterOriginal(paramCalculoPrescricao);
		if((old.getPepPacCodigo() != null && paramCalculoPrescricao.getPepPacCodigo() != null && !CoreUtil.igual(old.getPepPacCodigo(), paramCalculoPrescricao.getPepPacCodigo()))
		|| (old.getAtpPacCodigo() != null && paramCalculoPrescricao.getAtpPacCodigo() != null && !CoreUtil.igual(old.getAtpPacCodigo(), paramCalculoPrescricao.getAtpPacCodigo()))) {
			return;
		}
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		paramCalculoPrescricao.setServidor(servidorLogado);
	}
	
	/**
	 * @throws ApplicationBusinessException  
	 * @ORADB Trigger MPMT_PCA_BRI
	 */
	public void preInserir(MpmParamCalculoPrescricao paramCalculoPrescricao) throws ApplicationBusinessException {
		if(paramCalculoPrescricao.getId() != null) {
			paramCalculoPrescricao.getId().setCriadoEm(new Date());
		}
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		paramCalculoPrescricao.setServidor(servidorLogado);
	}

	private MpmParamCalculoPrescricaoDAO getMpmParamCalculoPrescricaoDAO() {
		return mpmParamCalculoPrescricaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
