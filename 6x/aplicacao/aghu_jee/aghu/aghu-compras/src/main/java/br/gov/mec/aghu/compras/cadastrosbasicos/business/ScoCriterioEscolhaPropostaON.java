package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCriterioEscolhaPropostaDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoCriterioEscolhaPropostaON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ScoCriterioEscolhaPropostaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private ScoCriterioEscolhaPropostaDAO scoCriterioEscolhaPropostaDAO;

	private static final long serialVersionUID = -4315028891741261842L;

	public enum ScoCriterioEscolhaPropostaONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG;
	}
	
	public List<ScoCriterioEscolhaProposta> pesquisarCriterioEscolhaProposta (
		    Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
		    Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio) {
		return this.getScoCriterioEscolhaPropostaDAO().pesquisarCriterioEscolhaProposta(firstResult, maxResult, 
				orderProperty, asc, codigoCriterio, descricaoCriterio, situacaoCriterio);
	}
	
	public Long pesquisarCriterioEscolhaPropostaCount(Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio) {
		return this.getScoCriterioEscolhaPropostaDAO().pesquisarCriterioEscolhaPropostaCount(codigoCriterio, descricaoCriterio, situacaoCriterio);
	}
	
	public void persistirCriterioEscolha(ScoCriterioEscolhaProposta criterioEscolha) throws ApplicationBusinessException {

		if (criterioEscolha == null) {
			throw new ApplicationBusinessException( ScoCriterioEscolhaPropostaONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		if (criterioEscolha.getCodigo() != null) {
			getScoCriterioEscolhaPropostaDAO().merge(criterioEscolha);			
		} else {
			this.getScoCriterioEscolhaPropostaDAO().persistir(criterioEscolha);
		}
		
	}
	
	public ScoCriterioEscolhaProposta obterCriterioEscolhaProposta(Short codigoCriterio) {
		return this.getScoCriterioEscolhaPropostaDAO().obterPorChavePrimaria(codigoCriterio);
	}
	
	public void excluirCriterioEscolha(Short criterioEscolha) throws ApplicationBusinessException {
		this.getScoCriterioEscolhaPropostaDAO().removerPorId(criterioEscolha);
	}

	private ScoCriterioEscolhaPropostaDAO getScoCriterioEscolhaPropostaDAO() {
		return scoCriterioEscolhaPropostaDAO;
	}
}