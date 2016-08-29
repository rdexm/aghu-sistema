package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.dao.ScoCriterioReajusteContratoDAO;
import br.gov.mec.aghu.sicon.util.SiconUtil;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CriterioReajusteContratoON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(CriterioReajusteContratoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoCriterioReajusteContratoDAO scoCriterioReajusteContratoDAO;
	
	@EJB
	private ISiconFacade siconFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7449361955909056158L;

	public enum ManterCriterioReajusteContratoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_DESCRICAO_CRITERIO_DUPLICADA, MENSAGEM_CRITERIO_ASSOCIADO;
	}

	public List<ScoCriterioReajusteContrato> pesquisarCriterioReajusteContrato(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer seq, String descricao, DominioSituacao situacao) {
		return this.getScoCriterioReajusteContratoDAO().pesquisar(firstResult,
				maxResult, orderProperty, asc, seq, descricao, situacao);
	}

	public Long pesquisarCriterioReajusteContratoCount(Integer seq,
			String descricao, DominioSituacao situacao) {

		return this.getScoCriterioReajusteContratoDAO()
				.pesquisarCriterioReajusteContratoCount(seq, descricao,	situacao);
	}

	public void alterar(ScoCriterioReajusteContrato scoCriterioReajusteContrato)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (!verificaDescricaoUnica(scoCriterioReajusteContrato)) {
			throw new ApplicationBusinessException(
					ManterCriterioReajusteContratoONExceptionCode.MENSAGEM_DESCRICAO_CRITERIO_DUPLICADA);
		}
		
		scoCriterioReajusteContrato.setDescricao(SiconUtil.retiraEspacosConsecutivos(scoCriterioReajusteContrato.getDescricao()));
		
		scoCriterioReajusteContrato.setAlteradoEm(new Date());
		scoCriterioReajusteContrato.setServidor(servidorLogado);
		
		ScoCriterioReajusteContratoDAO scoCriterioReajusteContratoDAO = this.getScoCriterioReajusteContratoDAO();
		scoCriterioReajusteContratoDAO.atualizar(
				scoCriterioReajusteContrato);
		scoCriterioReajusteContratoDAO.flush();
	}

	public void inserir(ScoCriterioReajusteContrato scoCriterioReajusteContrato)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (!verificaDescricaoUnica(scoCriterioReajusteContrato)) {
			throw new ApplicationBusinessException(
					ManterCriterioReajusteContratoONExceptionCode.MENSAGEM_DESCRICAO_CRITERIO_DUPLICADA);
		}

		scoCriterioReajusteContrato.setDescricao(SiconUtil.retiraEspacosConsecutivos(scoCriterioReajusteContrato.getDescricao()));

		scoCriterioReajusteContrato.setCriadoEm(new Date());
		scoCriterioReajusteContrato.setServidor(servidorLogado);

		ScoCriterioReajusteContratoDAO scoCriterioReajusteContratoDAO = this.getScoCriterioReajusteContratoDAO();
		scoCriterioReajusteContratoDAO.persistir(
				scoCriterioReajusteContrato);
		scoCriterioReajusteContratoDAO.flush();
	}

	public void excluirCriterioReajusteContrato(Integer seq) throws BaseException {
		ScoCriterioReajusteContrato scoCriterioReajusteContrato = scoCriterioReajusteContratoDAO.obterPorChavePrimaria(seq);
		if (verificarAssociacaoContratoComReajuste(scoCriterioReajusteContrato)) {
			throw new ApplicationBusinessException(ManterCriterioReajusteContratoONExceptionCode.MENSAGEM_CRITERIO_ASSOCIADO);
		}

		scoCriterioReajusteContratoDAO.removerPorId(seq);
	}

	public boolean verificarAssociacaoContratoComReajuste(
			ScoCriterioReajusteContrato scoCriterioReajusteContrato) {

		List<ScoContrato> listaContrato = this
				.getSiconFacade().obterListaContratoPorCriterioReajuste(
						scoCriterioReajusteContrato);

		if (listaContrato.size() > 0) {
			return true;
		}

		return false;
	}

	public boolean verificaDescricaoUnica(
			ScoCriterioReajusteContrato scoCriterioReajusteContrato) {
		Long numDescricao = this.getScoCriterioReajusteContratoDAO()
				.validaDescricaoUnica(scoCriterioReajusteContrato.getSeq(),
						scoCriterioReajusteContrato.getDescricao());
		if (numDescricao > 0) {
			return false;
		}

		return true;
	}

	// DAOs
	protected ScoCriterioReajusteContratoDAO getScoCriterioReajusteContratoDAO() {
		return scoCriterioReajusteContratoDAO;
	}

	// Facades
	protected ISiconFacade getSiconFacade(){
		return this.siconFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
