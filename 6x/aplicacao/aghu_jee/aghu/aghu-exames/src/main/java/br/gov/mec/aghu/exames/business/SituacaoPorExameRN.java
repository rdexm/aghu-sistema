package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.model.AelAutorizacaoAlteracaoSituacao;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SituacaoPorExameRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8295037693009205604L;
	
	private static final Log LOG = LogFactory.getLog(SituacaoPorExameRN.class);
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public AelSitItemSolicitacoes obterSituacaoItemSolicitacaoParaSituacaoExame(final String codigo) {
		AelSitItemSolicitacoes situacao = aelSitItemSolicitacoesDAO.obterPeloId(codigo);
		
		aelSitItemSolicitacoesDAO.initialize(situacao);
		if (situacao.getMatrizesSituacao() != null) {
			aelSitItemSolicitacoesDAO.initialize(situacao.getMatrizesSituacao());
			for (AelMatrizSituacao matrizSituacao : situacao.getMatrizesSituacao()) {
				aelSitItemSolicitacoesDAO.initialize(matrizSituacao);
				if (matrizSituacao.getSituacaoItemSolicitacao() != null) {
					aelSitItemSolicitacoesDAO.initialize(matrizSituacao.getSituacaoItemSolicitacao());					
				}
				if (matrizSituacao.getSituacaoItemSolicitacaoPara() != null) {
					aelSitItemSolicitacoesDAO.initialize(matrizSituacao.getSituacaoItemSolicitacaoPara());
				}
				if (matrizSituacao.getSituacaoExame() != null) {
					aelSitItemSolicitacoesDAO.initialize(matrizSituacao.getSituacaoExame());
				}
				if (matrizSituacao.getAutorizacaoAlteracaoSituacoes() != null) {
					aelSitItemSolicitacoesDAO.initialize(matrizSituacao.getAutorizacaoAlteracaoSituacoes());
					for (AelAutorizacaoAlteracaoSituacao autorizacaoAlteracao : matrizSituacao.getAutorizacaoAlteracaoSituacoes()) {
						aelSitItemSolicitacoesDAO.initialize(autorizacaoAlteracao);
					}
				}
			}
		}
		
		return situacao;
	}
	
	

}
