package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SceItemRecebProvisorioRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SceItemRecebProvisorioRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6150764788274774028L;

	public void atualizar(SceItemRecebProvisorio itemRecebProvisorio){
		this.getSceItemRecebProvisorioDAO().atualizar(itemRecebProvisorio);
	}
	
	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO(){
		return sceItemRecebProvisorioDAO;
	}
}
