package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoJustificativaPrecoDAO;
import br.gov.mec.aghu.model.ScoJustificativaPreco;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ScoJustificativaPrecoON extends BaseBusiness {
	
	private static final long serialVersionUID = -4315028891741261842L;

	private static final Log LOG = LogFactory.getLog(ScoJustificativaPrecoON.class);
	
	@Inject
	private ScoJustificativaPrecoDAO scoJustificativaPrecoDAO;
	
	public void persistirJustificativa(ScoJustificativaPreco justificativa) {
		if(justificativa.getCodigo() == null) {
			getScoJustificativaPrecoDAO().persistir(justificativa);
		}
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected ScoJustificativaPrecoDAO getScoJustificativaPrecoDAO() {
		return scoJustificativaPrecoDAO;
	}

	protected void setScoJustificativaPrecoDAO(ScoJustificativaPrecoDAO scoJustificativaPrecoDAO) {
		this.scoJustificativaPrecoDAO = scoJustificativaPrecoDAO;
	}
	
	

}
