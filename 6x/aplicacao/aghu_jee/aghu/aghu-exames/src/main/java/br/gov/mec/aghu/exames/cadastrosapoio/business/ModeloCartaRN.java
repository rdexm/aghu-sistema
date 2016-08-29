package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelModeloCartaJnDAO;
import br.gov.mec.aghu.exames.dao.AelModeloCartasDAO;
import br.gov.mec.aghu.model.AelModeloCartaJn;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio migradas
 * de triggers relacionadas à modelo de carta de recoleta.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class ModeloCartaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ModeloCartaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelModeloCartaJnDAO aelModeloCartaJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AelModeloCartasDAO aelModeloCartasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1158286368068381192L;
	
	public enum ModeloCartaRNExceptionCode implements BusinessExceptionCode {
		AEL_00353
	}
	
	public void persistirModeloCarta(AelModeloCartas modeloCartaNew) throws ApplicationBusinessException {
		if (modeloCartaNew.getSeq() == null) {
			inserirModeloCarta(modeloCartaNew);
		} else {
			atualizarModeloCarta(modeloCartaNew);
		}
	}
	
	/**
	 * Insere objeto AelModeloCartas.
	 * 
	 * @param modeloCartaNew
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirModeloCarta(AelModeloCartas modeloCartaNew) throws ApplicationBusinessException {
		executarBeforeInsertModeloCarta(modeloCartaNew);
		getAelModeloCartasDAO().persistir(modeloCartaNew);
	}
	
	/**
	 * Atualiza objeto AelModeloCartas.
	 * 
	 * @param modeloCartaNew
	 * @throws ApplicationBusinessException
	 */	
	public void atualizarModeloCarta(AelModeloCartas modeloCartaNew) throws ApplicationBusinessException {
		// Trigger AELT_MRT_BRU não foi migrada pois não
		// executa nenhuma regra de negócio.
		AelModeloCartasDAO modeloCartaDAO = getAelModeloCartasDAO();
		AelModeloCartas modeloCartaOld = modeloCartaDAO.obterOriginal(modeloCartaNew.getSeq());
		modeloCartaDAO.atualizar(modeloCartaNew);
		executarAfterUpdateModeloCarta(modeloCartaNew, modeloCartaOld);
	}
	
	/**
	 * Exclui objeto AelModeloCartas.
	 * 
	 * @param modeloCartaOld
	 * @throws ApplicationBusinessException 
	 */
	public void excluirModeloCarta(AelModeloCartas modeloCartaOld) throws ApplicationBusinessException {
		getAelModeloCartasDAO().remover(modeloCartaOld);
		executarAfterDeleteModeloCarta(modeloCartaOld);
	}	
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_MRT_BRI
	 * 
	 * @param modeloCartaNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarBeforeInsertModeloCarta(AelModeloCartas modeloCartaNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Servidor que está criando um modelo de carta.
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(ModeloCartaRNExceptionCode.AEL_00353);
		}
		
		modeloCartaNew.setServidor(servidorLogado);
		modeloCartaNew.setCriadoEm(new Date());
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_MRT_ARU
	 * 
	 * @param modeloCartaNew
	 * @param modeloCartaOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAfterUpdateModeloCarta(AelModeloCartas modeloCartaNew, AelModeloCartas modeloCartaOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(modeloCartaNew.getCriadoEm(), modeloCartaOld.getCriadoEm())
				|| CoreUtil.modificados(modeloCartaNew.getServidor(), modeloCartaOld.getServidor())
				|| CoreUtil.modificados(modeloCartaNew.getIndSituacao(), modeloCartaOld.getIndSituacao())
				|| CoreUtil.modificados(modeloCartaNew.getSeq(), modeloCartaOld.getSeq())
				|| CoreUtil.modificados(modeloCartaNew.getNome(), modeloCartaOld.getNome())
				|| CoreUtil.modificados(modeloCartaNew.getTexto(), modeloCartaOld.getTexto())) {
			
			inserirJournal(modeloCartaOld, DominioOperacoesJournal.UPD);
		}
	}	
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_MRT_ARD
	 * 
	 * @param modeloCartaNew
	 * @throws ApplicationBusinessException 
	 */
	public void executarAfterDeleteModeloCarta(AelModeloCartas modeloCartaOld) throws ApplicationBusinessException {
		inserirJournal(modeloCartaOld, DominioOperacoesJournal.DEL);
	}
	
	private void inserirJournal(final AelModeloCartas modeloCartaOld, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelModeloCartaJn jn = new AelModeloCartaJn();
		jn.setNomeUsuario(servidorLogado.getUsuario());
		jn.setOperacao(operacao);
		jn.setCriadoEm(modeloCartaOld.getCriadoEm());
		jn.setNome(modeloCartaOld.getNome());
		jn.setSeq(modeloCartaOld.getSeq());
		jn.setSerMatricula(modeloCartaOld.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(modeloCartaOld.getServidor().getId().getVinCodigo());
		jn.setSituacao(modeloCartaOld.getIndSituacao());
		jn.setTexto(modeloCartaOld.getTexto());
		getAelModeloCartaJnDAO().persistir(jn);
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}	
	
	protected AelModeloCartasDAO getAelModeloCartasDAO() {
		return aelModeloCartasDAO;
	}
	
	protected AelModeloCartaJnDAO getAelModeloCartaJnDAO() {
		return aelModeloCartaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
