package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class OrigemEventoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(OrigemEventoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8425239672647211976L;

	/**
	 * 
	 * @dbtables AghOrigemEventos select
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AghOrigemEventos> pesquisarOrigemEventoPorCodigoEDescricao(Object objPesquisa) {
		return this.getAghuFacade().pesquisarOrigemEventoPorCodigoEDescricao(objPesquisa);
	}

	/**
	 * 
	 * @dbtables AghOrigemEventos select
	 * 
	 * @param codigo
	 * @return
	 */
	public AghOrigemEventos obterOrigemInternacao(Short codigo) {
		AghOrigemEventos retorno = this.getAghuFacade().obterAghOrigemEventosPorChavePrimaria(codigo);
		return retorno;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
}
