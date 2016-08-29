package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatProcedimentoCboDAO;
import br.gov.mec.aghu.model.FatProcedimentoCbo;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class ProcedimentoCboON extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(ProcedimentoCboON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@Inject
	private FatProcedimentoCboDAO fatProcedimentoCboDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5594215618641803125L;

	/**
	 * Metodo para persistir um ProcedimentoCbo.
	 * @param ProcedimentoCbo
	 */
	public void inserirProcedimentoCbo(FatProcedimentoCbo procCbo){
		procCbo.setDtInicio(procCbo.getCbo().getDtInicio());
		procCbo.setDtFim(procCbo.getCbo().getDtFim());
		
		this.getFatProcedimentoCboDAO().persistir(procCbo);
		this.getFatProcedimentoCboDAO().flush();
	}

	/**
	 * Metodo para remover um FatProcedimentoCbo.
	 * @param FatProcedimentoCbo
	 */
	public void removerProcedimentoCbo(FatProcedimentoCbo procCbo){
		this.getFatProcedimentoCboDAO().remover(procCbo);
	}

	public String obterMensagemResourceBundle(String key) {
		return getResourceBundleValue(key);
	}
	
	protected FatProcedimentoCboDAO getFatProcedimentoCboDAO() {
		return fatProcedimentoCboDAO;
	}
}
