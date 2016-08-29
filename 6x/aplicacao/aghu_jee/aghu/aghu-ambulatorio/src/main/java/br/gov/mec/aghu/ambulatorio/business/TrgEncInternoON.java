package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class TrgEncInternoON extends BaseBusiness {





private static final Log LOG = LogFactory.getLog(TrgEncInternoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5044821879121241783L;

	private MamTrgEncInternoDAO getMamTrgEncInternoDAO() {
		return mamTrgEncInternoDAO;
	}
	
	public List<MamTrgEncInterno> obterPorConsulta(AacConsultas consulta) {
		return this.getMamTrgEncInternoDAO().obterPorConsulta(consulta);
	}
	

}
