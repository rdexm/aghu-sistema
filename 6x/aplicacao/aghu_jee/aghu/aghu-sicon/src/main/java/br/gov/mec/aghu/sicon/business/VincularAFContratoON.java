package br.gov.mec.aghu.sicon.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class VincularAFContratoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(VincularAFContratoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPacFacade pacFacade;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7978328954400034463L;

	/**
	 * Retorna as licitacoes com modalidade de empenho que geram contratos.
	 * 
	 * @param pesquisa
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ScoLicitacao> listarLicitacoesAtivas(Object pesquisa)
			throws ApplicationBusinessException {
		AghParametros param = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_MODL_EMP_CTR_SICON);
		return getPacFacade().listarLicitacoesAtivas(
				pesquisa,
				DominioModalidadeEmpenho.forValue(param.getVlrNumerico()
						.intValue()));
	}

	protected IParametroFacade getParametroFacade() {
		//return this.IparametroFacade;
		return this.parametroFacade;
	}
	
	protected IPacFacade getPacFacade(){
		return pacFacade;
	}

}
