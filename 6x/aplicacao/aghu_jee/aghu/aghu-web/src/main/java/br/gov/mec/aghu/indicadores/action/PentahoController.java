package br.gov.mec.aghu.indicadores.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class PentahoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4951673346506119131L;

	@EJB
	private IParametroFacade parametroFacade;

	public String carregaURLPentaho() throws ApplicationBusinessException {
		AghParametros paramURL = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PENTAHO_URL);

		if (StringUtils.isEmpty(paramURL.getVlrTexto())) {
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE, AghuParametrosEnum.P_PENTAHO_URL);
		}

		return paramURL.getVlrTexto();
	}

}
