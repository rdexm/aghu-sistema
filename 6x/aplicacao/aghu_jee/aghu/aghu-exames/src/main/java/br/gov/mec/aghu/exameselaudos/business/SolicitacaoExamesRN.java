package br.gov.mec.aghu.exameselaudos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * usar br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN.
 * 
 * @author rcorvalao
 *
 */
@Deprecated
@Stateless
public class SolicitacaoExamesRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SolicitacaoExamesRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2168398280283924267L;

	/**
	 * 
	 */
	public void atualizarSolicitacaoExame(AelSolicitacaoExames solicitacaoExame) {
		// TODO implementar triggers de AEL_SOLICITACAO_EXAME
		this.getSolicitacaoExameDAO().atualizar(solicitacaoExame);
		this.getSolicitacaoExameDAO().flush();

	}
	
	private AelSolicitacaoExameDAO getSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

}
