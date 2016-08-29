package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria #5630 - Estornar uma requisição de material
 * @author aghu
 *
 */
@Stateless
public class EstornarNotaRecebimentoON extends BaseBusiness {

	@EJB
	private SceNotaRecebimentoRN sceNotaRecebimentoRN;
	
	@Inject
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;
	
	private static final Log LOG = LogFactory.getLog(EstornarNotaRecebimentoON.class);
	
	private static final long serialVersionUID = 5959174924562415031L;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public void estornarNotaRecebimento(Integer seqNotaRecebimento, String nomeMicrocomputador) throws BaseException{
		SceNotaRecebimento notaRecebimento = sceNotaRecebimentoDAO.obterPorChavePrimaria(seqNotaRecebimento);
		SceNotaRecebimento oldNotaRecebimento = sceNotaRecebimentoDAO.obterOriginal(seqNotaRecebimento);
		
		notaRecebimento.setEstorno(true);
		getNotaRecebimentoRN().atualizar(notaRecebimento, oldNotaRecebimento, nomeMicrocomputador, true);
	}
	
	public void estornarNotaRecebimento(SceNotaRecebimento notaRecebimento, SceNotaRecebimento notaRecebimentoProvisoria,
			String nomeMicrocomputador, Boolean flush) throws BaseException{
		notaRecebimento.setEstorno(true);
		getNotaRecebimentoRN().atualizar(notaRecebimento, notaRecebimentoProvisoria, nomeMicrocomputador, flush);
	}
	
	/**
	 * get de RNs e DAOs
	 */
	protected SceNotaRecebimentoRN getNotaRecebimentoRN() {
		return sceNotaRecebimentoRN;
	}

	public SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}

	public void setSceNotaRecebimentoDAO(
			SceNotaRecebimentoDAO sceNotaRecebimentoDAO) {
		this.sceNotaRecebimentoDAO = sceNotaRecebimentoDAO;
	}

}
