package br.gov.mec.aghu.procedimentoterapeutico.business;

import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class MptCaracteristicaON extends BaseBusiness {

	private static final long serialVersionUID = -360244603883837883L;
	
	@Inject
	MptCaracteristicaDAO mptCaracteristicaDAO;
	
	@Override
	protected Log getLogger() {
		return null;
	}

	public void inserirMptCaracteristica(MptCaracteristica mptCaracteristica){
		this.mptCaracteristicaDAO.persistir(mptCaracteristica);
	}



}
