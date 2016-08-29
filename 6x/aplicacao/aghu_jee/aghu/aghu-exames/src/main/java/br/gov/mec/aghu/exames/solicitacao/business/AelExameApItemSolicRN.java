package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Responsavel pelas regras de negocio da entidade AelExameApItemSolic.<br>
 * 
 * 
 * Tabela: AEL_EXAME_AP_ITEM_SOLICS.
 * 
 */
@Stateless
public class AelExameApItemSolicRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelExameApItemSolicRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AelExameApItemSolicDAO aelExameApItemSolicDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8002962673564504381L;

	/**
	 * Insere objeto AelExameApItemSolic.
	 * 
	 * @param {AelExameApItemSolic} solicitacaoExame
	 * @throws BaseException
	 */
	public void inserir(final AelExameApItemSolic aelExameApItemSolic) throws BaseException {
		if (aelExameApItemSolic == null) {
			throw new IllegalArgumentException("AelExameApItemSolic é obrigatório.");
		}
		aelExameApItemSolic.setCriadoEm(new Date());
		aelExameApItemSolic.setAlteradoEm(new Date());
		this.getAelExameApItemSolicDAO().persistir(aelExameApItemSolic);
		this.getAelExameApItemSolicDAO().flush();
	}

	protected AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}

}
