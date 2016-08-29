package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoJustificativaDAO;
import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterScoJustificativaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterScoJustificativaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private ScoJustificativaDAO scoJustificativaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1066215596800961383L;

	public enum ManterJustificativaExceptionCode implements
			BusinessExceptionCode {

		MENSAGEM_PARAM_OBRIG, MENSAGEM_JUSTIFICATIVA_DUP_ERRO;
	}

	public void alterar(ScoJustificativa scoJustificativa)
			throws ApplicationBusinessException {

		if (scoJustificativa == null) {
			throw new ApplicationBusinessException(
					ManterJustificativaExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		validaJustificativa(scoJustificativa);

		this.getScoJustificativaDAO().merge(scoJustificativa);
	}

	public void inserir(ScoJustificativa scoJustificativa)
			throws ApplicationBusinessException {

		if (scoJustificativa == null) {
			throw new ApplicationBusinessException(
					ManterJustificativaExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		validaJustificativa(scoJustificativa);
		this.getScoJustificativaDAO().persistir(scoJustificativa);
	}

	public List<ScoJustificativa> pesquisarJustificativas(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			final ScoJustificativa comprasJustificativa) {

		return this.getScoJustificativaDAO().pesquisarJustificativas(
				firstResult, maxResult, orderProperty, asc,
				comprasJustificativa);
	}

	public Long pesquisarJustificativaCount(
			final ScoJustificativa comprasJustificativa) {

		return this.getScoJustificativaDAO().pesquisarJustificativaCount(
				comprasJustificativa);
	}

	public ScoJustificativa obterJustificativa(final Short codigo) {

		return this.getScoJustificativaDAO().obterPorChavePrimaria(codigo);
	}

		
	public void validaJustificativa(ScoJustificativa scoJustificativa) throws ApplicationBusinessException {

		ScoJustificativa searchJustificativa = this.getScoJustificativaDAO()
				.pesquisarJustificativasPorDescricao(
						scoJustificativa.getCodigo(),
						scoJustificativa.getDescricao());

		if (searchJustificativa != null) {
			throw new ApplicationBusinessException(
					ManterJustificativaExceptionCode.MENSAGEM_JUSTIFICATIVA_DUP_ERRO);	
		}

		
	}

	// instâncias
	protected ScoJustificativaDAO getScoJustificativaDAO() {
		return scoJustificativaDAO;
	}
	

}
