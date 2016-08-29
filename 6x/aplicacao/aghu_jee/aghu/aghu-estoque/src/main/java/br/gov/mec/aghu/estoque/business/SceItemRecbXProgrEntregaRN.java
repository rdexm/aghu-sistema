package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceItemRecbXProgrEntregaDAO;
import br.gov.mec.aghu.model.SceItemRecbXProgrEntrega;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SceItemRecbXProgrEntregaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SceItemRecbXProgrEntregaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemRecbXProgrEntregaDAO sceItemRecbXProgrEntregaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6207171292876196620L;

	/**
	 * Persiste um SceItemRecbXProgrEntrega
	 * @param itemRecb
	 */
	public void persistir(SceItemRecbXProgrEntrega itemReceb) {
		
		if (itemReceb.getSeq() == null){
			Long seq = this.getSceItemRecbXProgrEntregaDAO().obterMaxItemRecbXProgrEntrega();
			seq = seq + 1;
			itemReceb.setSeq(seq);
			
			itemReceb.setVersion(0);
		} else{
			itemReceb.setVersion(itemReceb.getVersion() + 1);
		}
			
		
		this.getSceItemRecbXProgrEntregaDAO().persistir(itemReceb);
	}
	
	public void atualizar(SceItemRecbXProgrEntrega itemRecbXProgrEntrega){
		this.getSceItemRecbXProgrEntregaDAO().atualizar(itemRecbXProgrEntrega);
	}
	
	
	protected SceItemRecbXProgrEntregaDAO getSceItemRecbXProgrEntregaDAO() {
		return sceItemRecbXProgrEntregaDAO;
	}
}
