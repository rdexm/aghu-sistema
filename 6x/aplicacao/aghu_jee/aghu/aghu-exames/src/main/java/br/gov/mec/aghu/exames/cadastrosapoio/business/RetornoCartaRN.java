package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelRetornoCartaDAO;
import br.gov.mec.aghu.exames.dao.AelRetornoCartaJnDAO;
import br.gov.mec.aghu.model.AelRetornoCarta;
import br.gov.mec.aghu.model.AelRetornoCartaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável pelas regras de negócio migradas
 * de triggers relacionadas à retorno de carta de recoleta.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class RetornoCartaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RetornoCartaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AelRetornoCartaDAO aelRetornoCartaDAO;
	
	@Inject
	private AelRetornoCartaJnDAO aelRetornoCartaJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5894957591635122104L;
	
	public enum RetornoCartaRNExceptionCode implements BusinessExceptionCode {
		AEL_00353
	}
	
	/**
	 * Persiste (insere/atualiza) objeto AelRetornoCarta.
	 * 
	 * @param retornoCartaNew
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void persistirRetornoCarta(AelRetornoCarta retornoCartaNew) throws ApplicationBusinessException {
		if (retornoCartaNew.getSeq() == null) {
			inserirRetornoCarta(retornoCartaNew);
		} else {
			atualizarRetornoCarta(retornoCartaNew);
		}
	}
	
	/**
	 * Insere objeto AelRetornoCarta.
	 * 
	 * @param retornoCartaNew
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirRetornoCarta(AelRetornoCarta retornoCartaNew) throws ApplicationBusinessException {
		executarBeforeInsertRetornoCarta(retornoCartaNew);
		getAelRetornoCartaDAO().persistir(retornoCartaNew);
	}
	
	/**
	 * Atualiza objeto AelRetornoCarta.
	 * 
	 * @param retornoCartaNew
	 * @throws ApplicationBusinessException
	 */
	public void atualizarRetornoCarta(AelRetornoCarta retornoCartaNew) throws ApplicationBusinessException {
		AelRetornoCartaDAO aelRetornoCartaDAO = getAelRetornoCartaDAO();
		AelRetornoCarta retornoCartaOld = aelRetornoCartaDAO.obterOriginal(retornoCartaNew.getSeq());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		retornoCartaNew.setServidor(servidorLogado);
		aelRetornoCartaDAO.merge(retornoCartaNew);
		executarAfterUpdateRetornoCarta(retornoCartaNew, retornoCartaOld);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_RNO_BRI
	 * 
	 * @param retornoCartaNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarBeforeInsertRetornoCarta(AelRetornoCarta retornoCartaNew)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(RetornoCartaRNExceptionCode.AEL_00353);
		}
		
		retornoCartaNew.setServidor(servidorLogado);
		retornoCartaNew.setCriadoEm(new Date());
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_RNO_ARU
	 * 
	 * @param retornoCartaNew
	 * @throws ApplicationBusinessException 
	 */
	public void executarAfterUpdateRetornoCarta(AelRetornoCarta retornoCartaNew, AelRetornoCarta retornoCartaOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(retornoCartaNew.getSeq(), retornoCartaOld.getSeq())
				|| CoreUtil.modificados(retornoCartaNew.getDescricao(), retornoCartaOld.getDescricao())
				|| CoreUtil.modificados(retornoCartaNew.getIndSituacao(), retornoCartaOld.getIndSituacao())
				|| CoreUtil.modificados(retornoCartaNew.getCriadoEm(), retornoCartaOld.getCriadoEm())
				|| CoreUtil.modificados(retornoCartaNew.getServidor(), retornoCartaOld.getServidor())
				|| CoreUtil.modificados(retornoCartaNew.getIndCancelaExame(), retornoCartaOld.getIndCancelaExame())	
				|| CoreUtil.modificados(retornoCartaNew.getIndAvisaSolicitante(), retornoCartaOld.getIndAvisaSolicitante())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelRetornoCartaJn jn = new AelRetornoCartaJn();
			jn.setNomeUsuario(servidorLogado.getUsuario());
			jn.setOperacao(DominioOperacoesJournal.UPD);
			jn.setCriadoEm(retornoCartaOld.getCriadoEm());
			jn.setDescricao(retornoCartaOld.getDescricao());
			jn.setIndAvisaSolicitante(retornoCartaOld.getIndAvisaSolicitante());
			jn.setIndCancelaExame(retornoCartaOld.getIndCancelaExame());
			jn.setIndSituacao(retornoCartaOld.getIndSituacao());
			jn.setSeq(retornoCartaOld.getSeq());
			jn.setSerMatricula(retornoCartaOld.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(retornoCartaOld.getServidor().getId().getVinCodigo());
			getAelRetornoCartaJnDAO().persistir(jn);
		}
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}		
	
	protected AelRetornoCartaDAO getAelRetornoCartaDAO() {
		return aelRetornoCartaDAO;
	}
	
	protected AelRetornoCartaJnDAO getAelRetornoCartaJnDAO() {
		return aelRetornoCartaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
