package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelGrupoExameUsualDAO;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GrupoExamesUsuaisRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoExamesUsuaisRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelGrupoExameUsualDAO aelGrupoExameUsualDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final long serialVersionUID = -8756256873027630560L;

	
	public enum GrupoExamesUsuaisRNExceptionCode implements BusinessExceptionCode {
		AEL_00343, AEL_00344, AEL_00353, AEL_00346, AEL_00369;

	}

	/**
	 * Trigger
	 * 
	 * ORADB: AELT_GRU_BRD
	 */
	public void executarBeforeDeleteGrupoExamesUsuais(Integer seq) throws ApplicationBusinessException {
		
		AelGrupoExameUsual aelGrupoExameUsual = getAelGrupoExameUsualDAO().obterPorChavePrimaria(seq);

		if (aelGrupoExameUsual == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		AghParametros paramDiasPermDelAel;
		try {
			paramDiasPermDelAel = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção ApplicationBusinessException capturada, lançada para cima.", e);
			throw new ApplicationBusinessException(GrupoExamesUsuaisRNExceptionCode.AEL_00344);
		}
		
		Integer numDias = DateUtil.calcularDiasEntreDatas(aelGrupoExameUsual.getCriadoEm(), DateUtil.truncaData(new Date()));
		
		if (numDias > paramDiasPermDelAel.getVlrNumerico().intValue()) {
			throw new ApplicationBusinessException(GrupoExamesUsuaisRNExceptionCode.AEL_00343);
		}
		
		this.getAelGrupoExameUsualDAO().remover(aelGrupoExameUsual);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_GRU_BRI
	 */
	public void executarBeforeInsertGrupoExamesUsuais(AelGrupoExameUsual aelGrupoExameUsualNew) throws ApplicationBusinessException {
		
		if (aelGrupoExameUsualNew.getServidor() == null) {
			throw new ApplicationBusinessException(GrupoExamesUsuaisRNExceptionCode.AEL_00353);
		}
		
		aelGrupoExameUsualNew.setCriadoEm(DateUtil.truncaData(new Date()));
		this.getAelGrupoExameUsualDAO().persistir(aelGrupoExameUsualNew);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_GRU_BRU
	 */
	public void executarBeforeUpdateGrupoExamesUsuais(AelGrupoExameUsual aelGrupoExameUsualNew) throws ApplicationBusinessException {
		
		AelGrupoExameUsual aelGrupoExameUsualOld = getAelGrupoExameUsualDAO().obterOriginal(aelGrupoExameUsualNew.getSeq());
		
		if(!aelGrupoExameUsualOld.getDescricao().equals(aelGrupoExameUsualNew.getDescricao())) {
			throw new ApplicationBusinessException(GrupoExamesUsuaisRNExceptionCode.AEL_00346);
		}
		
		if(!aelGrupoExameUsualOld.getCriadoEm().equals(aelGrupoExameUsualNew.getCriadoEm())
				|| !aelGrupoExameUsualOld.getServidor().equals(aelGrupoExameUsualNew.getServidor())) {
			throw new ApplicationBusinessException(GrupoExamesUsuaisRNExceptionCode.AEL_00369);
		}
		
		getAelGrupoExameUsualDAO().merge(aelGrupoExameUsualNew);
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelGrupoExameUsualDAO getAelGrupoExameUsualDAO() {
		return aelGrupoExameUsualDAO;
	}
	
}
