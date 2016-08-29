package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapCodStarhLivres;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class VinculoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(VinculoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4602781601980647382L;

	/**
	 * ORADB: Procedure RAPP_GERA_COD_STARH
	 * 
	 * @param vinculo
	 * @throws ApplicationBusinessException
	 */
	public void gerarCodStarh(Integer parametroInicial, Integer parametroFinal)
			throws ApplicationBusinessException {

		RapCodStarhLivres rapCodStarhLivres = new RapCodStarhLivres();
		RapServidores servidor = new RapServidores();

		Integer parametro = parametroInicial;

		while (parametro <= parametroFinal) {
			rapCodStarhLivres = new RapCodStarhLivres();
			rapCodStarhLivres.setCodStarh(parametro);

			servidor = getRegistroColaboradorFacade().obterServidor(rapCodStarhLivres);

			if (servidor != null){
				continue;
			}

			getRegistroColaboradorFacade().insert(rapCodStarhLivres);
			parametro += 1;
		}
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
}
