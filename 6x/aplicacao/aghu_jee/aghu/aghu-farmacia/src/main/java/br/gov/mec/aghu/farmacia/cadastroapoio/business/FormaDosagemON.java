package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.dao.AbstractMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * 
 * @author fgka
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FormaDosagemON extends AbstractCrudMedicamento<AfaFormaDosagem> {


@EJB
private FormaDosagemRN formaDosagemRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7854176629257604956L;

	private static final Log LOG = LogFactory.getLog(FormaDosagemON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;
	
	@Override
	public AbstractMedicamentoDAO<AfaFormaDosagem> getEntidadeDAO() {
		
		return afaFormaDosagemDAO;
	}
	
	@Override
	public AbstractAGHUCrudRn<AfaFormaDosagem> getRegraNegocio() {
		
		return formaDosagemRN;
	}

	@Override
	public Object getChavePrimariaEntidade(AfaFormaDosagem entidade) {

		return (entidade != null ? entidade.getSeq() : null);
	}
	
	@Override
	public void atualizar(AfaFormaDosagem entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		entidade = this.getEntidadeDAO().merge(entidade);
		super.atualizar(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
}
