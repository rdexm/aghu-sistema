package br.gov.mec.aghu.business.bancosangue;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.vo.AtualizaCartaoPontoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class BancoDeSangueON extends BaseBusiness {

	@EJB
	private BancoDeSangueRN bancoDeSangueRN;
	
	private static final Log LOG = LogFactory.getLog(BancoDeSangueON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7879723688747767214L;

	public String buscaJustificativaLaudoCsa(Integer atdSeq, Integer phiSeq) {
		return getBancoDeSangueRN().buscaJustificativaLaudoCsa(atdSeq, phiSeq);
	}

	public String buscaJustificativaLaudoPhe(Integer atdSeq, Integer phiSeq) {
		return getBancoDeSangueRN().buscaJustificativaLaudoPhe(atdSeq, phiSeq);
	}

	public AtualizaCartaoPontoVO atualizaCartaoPontoServidor()
			throws ApplicationBusinessException {
		return getBancoDeSangueRN().atualizaCartaoPontoServidor();
	}
	
	protected BancoDeSangueRN getBancoDeSangueRN() {
		return bancoDeSangueRN;
	}

}
