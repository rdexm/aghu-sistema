package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamItemExameDAO;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * @author murillo
 *
 */
@Stateless
public class ItemExameON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ItemExameON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	
	@Inject
	private MamItemExameDAO mamItemExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8054663478387013093L;

	private MamItemExameDAO getMamItemExameDAO(){
		return mamItemExameDAO;
	}
	

	/**
	 * 
	 * @param param
	 * @param ordem
	 * @param maxResults
	 * @return
	 */
	public List<MamItemExame> pesquisarItemExamePorDescricaoOuSeq(Object param,
			String ordem, Integer maxResults) {
		return this.getMamItemExameDAO().pesquisarItemExamePorDescricaoOuSeq(param, ordem, maxResults);
	}

	/**
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarItemExamePorDescricaoOuSeq(Object param) {
		return this.getMamItemExameDAO().pesquisarItemExamePorDescricaoOuSeq(param);
	}
	
}
