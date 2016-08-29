package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCaminhoSolicitacaoDAO;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacao;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacaoID;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoCaminhoSolicitacaoON extends BaseBusiness {

@EJB
private ScoCaminhoSolicitacaoRN scoCaminhoSolicitacaoRN;

private static final Log LOG = LogFactory.getLog(ScoCaminhoSolicitacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoCaminhoSolicitacaoDAO scoCaminhoSolicitacaoDAO;

	private static final long serialVersionUID = -7130595457653656617L;

	public enum ScoCaminhoSolicitacaoONExceptionCode implements
			BusinessExceptionCode { MENSAGEM_PARAM_OBRIG; }

	public List<ScoCaminhoSolicitacao> pesquisarCaminhoSolicitacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada) {

		return this.getScoCaminhoSolicitacaoDAO().pesquisarCaminhoSolicitacao(
				firstResult, maxResult, orderProperty, asc, origemParada, destinoParada);
	}

	public Long pesquisarCaminhoSolicitacaoCount(
			ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada) {

		return this.getScoCaminhoSolicitacaoDAO().pesquisarCaminhoSolicitacaoCount(
				origemParada, destinoParada);
	}

	public void inserirCaminhoSolicitacao(ScoCaminhoSolicitacao caminhoSolicitacao)
			throws ApplicationBusinessException {

		if (caminhoSolicitacao == null) {
			throw new ApplicationBusinessException(ScoCaminhoSolicitacaoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		this.getScoCaminhoSolicitacaoRN().persistir(caminhoSolicitacao);
	}
	
	public void excluirCaminhoSolicitacao(ScoCaminhoSolicitacaoID id) throws ApplicationBusinessException {
		this.getScoCaminhoSolicitacaoRN().remover(id);
	}
	
	private ScoCaminhoSolicitacaoDAO getScoCaminhoSolicitacaoDAO() {
		return scoCaminhoSolicitacaoDAO;
	}

	protected ScoCaminhoSolicitacaoRN getScoCaminhoSolicitacaoRN(){
		return scoCaminhoSolicitacaoRN;
	}
}