package br.gov.mec.aghu.compras.solicitacaocompra.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCaminhoSolicitacaoDAO;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacao;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacaoID;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoCaminhoSolicitacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoCaminhoSolicitacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoCaminhoSolicitacaoDAO scoCaminhoSolicitacaoDAO;

	private static final long serialVersionUID = -5780209686447701050L;

	public enum ScoCaminhoSolicitacaoRNExceptionCode implements
			BusinessExceptionCode { MENSAGEM_PARAM_OBRIG, MENSAGEM_CAMINHO_SOLIC_DUPLICADO; }

	public void persistir(ScoCaminhoSolicitacao caminhoSolicitacao)
			throws ApplicationBusinessException {

		if (caminhoSolicitacao == null) {
			throw new ApplicationBusinessException(ScoCaminhoSolicitacaoRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		if (this.getScoCaminhoSolicitacaoDAO().obterPorChavePrimaria(caminhoSolicitacao.getId()) != null){
			 throw new ApplicationBusinessException(ScoCaminhoSolicitacaoRNExceptionCode.MENSAGEM_CAMINHO_SOLIC_DUPLICADO);
		}
		
		this.getScoCaminhoSolicitacaoDAO().persistir(caminhoSolicitacao);
	}
	
	public void remover(ScoCaminhoSolicitacaoID id) throws ApplicationBusinessException {
		this.getScoCaminhoSolicitacaoDAO().removerPorId(id);
	}

	private ScoCaminhoSolicitacaoDAO getScoCaminhoSolicitacaoDAO() {
		return scoCaminhoSolicitacaoDAO;
	}
}
