package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelExameReflexo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelExameReflexoON extends BaseBusiness {


@EJB
private AelExameReflexoRN aelExameReflexoRN;

private static final Log LOG = LogFactory.getLog(AelExameReflexoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = -2876524483275806974L;

	public enum AelExameReflexoONExceptionCode implements BusinessExceptionCode {
		AEL_02886, AEL_02887, AEL_02890, AEL_02896, AEL_02888, AEL_02897, AEL_02889, AEL_02898, ;
	}

	public void persistirAelExameReflexo(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		this.verificarTipoCampo(aelExameReflexo);
		this.getAelExameReflexoRN().persitirAelExameReflexo(aelExameReflexo);
	}

	public void excluirAelExameReflexo(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		this.getAelExameReflexoRN().excluirAelExameReflexo(aelExameReflexo);
	}

	private void verificarTipoCampo(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		switch (aelExameReflexo.getAelCampoLaudo().getTipoCampo()) {
		case N:
			if (aelExameReflexo.getResulNumIni() == null) {
				throw new ApplicationBusinessException(AelExameReflexoONExceptionCode.AEL_02886);
			}
			if (aelExameReflexo.getResulNumFim() == null) {
				throw new ApplicationBusinessException(AelExameReflexoONExceptionCode.AEL_02887);
			}
			if (aelExameReflexo.getResulNumIni() > aelExameReflexo.getResulNumFim()) {
				throw new ApplicationBusinessException(AelExameReflexoONExceptionCode.AEL_02890);
			}
			if (StringUtils.isNotEmpty(aelExameReflexo.getResulAlfa()) || aelExameReflexo.getAelResultadoCodificado() != null) {
				throw new ApplicationBusinessException(AelExameReflexoONExceptionCode.AEL_02896);
			}
			break;

		case A:
			if (StringUtils.isEmpty(aelExameReflexo.getResulAlfa())) {
				throw new ApplicationBusinessException(AelExameReflexoONExceptionCode.AEL_02888);
			}
			if (aelExameReflexo.getResulNumIni() != null || aelExameReflexo.getResulNumFim() != null || aelExameReflexo.getAelResultadoCodificado() != null) {
				throw new ApplicationBusinessException(AelExameReflexoONExceptionCode.AEL_02897);
			}
			break;
			
		case C:
			if (aelExameReflexo.getAelResultadoCodificado() == null) {
				throw new ApplicationBusinessException(AelExameReflexoONExceptionCode.AEL_02889);
			}
			if (aelExameReflexo.getResulNumIni() != null || aelExameReflexo.getResulNumFim() != null || StringUtils.isNotEmpty(aelExameReflexo.getResulAlfa())) {
				throw new ApplicationBusinessException(AelExameReflexoONExceptionCode.AEL_02898);
			}
			break;
			
		default:
			break;
		}
	}

	public AelExameReflexoRN getAelExameReflexoRN() {
		return aelExameReflexoRN;
	}

}