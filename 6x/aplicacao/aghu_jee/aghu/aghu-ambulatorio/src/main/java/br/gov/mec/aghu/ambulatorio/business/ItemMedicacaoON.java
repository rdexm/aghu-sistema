package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamItemMedicacaoDAO;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * @author murillo
 *
 */
@Stateless
public class ItemMedicacaoON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4376008382065843575L;
	
	private static final Log LOG = LogFactory.getLog(ItemMedicacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MamItemMedicacaoDAO mamItemMedicacaoDAO;

	private MamItemMedicacaoDAO getMamItemMedicacaoDAO(){
		return mamItemMedicacaoDAO;
	}
	
	public List<MamItemMedicacao> listarTodosItensMedicacao(String ordem) {
		return this.getMamItemMedicacaoDAO().listarTodosItensMedicacao(ordem);
	}
}
