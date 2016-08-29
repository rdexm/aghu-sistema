package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacMotivosDAO;
import br.gov.mec.aghu.model.AacMotivos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterMotivoConsultaRN extends BaseBusiness {

	/**
     *
     */
	private static final long serialVersionUID = -1431334637452812962L;

	private static final Log LOG = LogFactory.getLog(ManterMotivoConsultaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	@Inject
	private AacMotivosDAO aacMotivosDAO;

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	public enum ManterManterMotivoConsultaRNExceptionCode implements BusinessExceptionCode {
		MSG_DEPENDENCIAS_MOTIVO_CONSULTA
	}
	
	public void remover(Short accMotivoConsultaCodigo)
			throws ApplicationBusinessException {
		
		AacMotivos motivo = this.getAacMotivosDAO().obterMotivoConsulta(accMotivoConsultaCodigo);
		preDeleteMotivoConsulta(motivo);
		this.getAacMotivosDAO().remover(motivo);
		this.getAacMotivosDAO().flush();

	}

	public void preDeleteMotivoConsulta(AacMotivos motivo) throws ApplicationBusinessException {
		Long count = this.getAacConsultasDAO().listarConsultasComMotivoCount(motivo);
		if (count != null && count > 0) {
			throw new ApplicationBusinessException(ManterManterMotivoConsultaRNExceptionCode.MSG_DEPENDENCIAS_MOTIVO_CONSULTA,
					"AAC_CONSULTAS");
		}
	}
	
	/**
	 * 
	 * @throws MECBaseException
	 */
	public void persistirMotivoConsulta(AacMotivos motivo) throws ApplicationBusinessException {
		this.getAacMotivosDAO().persistir(motivo);
		this.getAacMotivosDAO().flush();
	}

	public void atualizarMotivoConsulta(AacMotivos motivo)
			throws ApplicationBusinessException {
		this.getAacMotivosDAO().atualizar(motivo);
		this.getAacMotivosDAO().flush();
	}

	/**
	 * GET/SET *
	 */
	private AacMotivosDAO getAacMotivosDAO() {
		return aacMotivosDAO;
	}

	private AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

}
