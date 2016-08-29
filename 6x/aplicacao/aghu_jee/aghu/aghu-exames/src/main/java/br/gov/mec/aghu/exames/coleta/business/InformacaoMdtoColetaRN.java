package br.gov.mec.aghu.exames.coleta.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelInformacaoMdtoColetaDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoMdtoColetaJnDAO;
import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;
import br.gov.mec.aghu.model.AelInformacaoMdtoColetaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio migradas de triggers
 * e procedures relacionadas com a entidade AelInformacaoMdtoColeta.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class InformacaoMdtoColetaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(InformacaoMdtoColetaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelInformacaoMdtoColetaJnDAO aelInformacaoMdtoColetaJnDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelInformacaoMdtoColetaDAO aelInformacaoMdtoColetaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3161814618381992435L;
	
	public enum InformacaoMdtoColetaRNExceptionCode implements BusinessExceptionCode {
		AEL_00353;
	}

	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_MDL_BRI
	 * 
	 * @param newInformacaoMdtoColeta
	 * @throws ApplicationBusinessException  
	 */
	public void executarBeforeInsertInformacaoMdtoColeta(AelInformacaoMdtoColeta newInformacaoMdtoColeta) 
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
 		//COMENTADO, TAREFA #24988
//		verificarInformacaoMedicamento(newInformacaoMdtoColeta);

		newInformacaoMdtoColeta.setCriadoEm(new Date());
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(InformacaoMdtoColetaRNExceptionCode.AEL_00353);
		}
		
		newInformacaoMdtoColeta.setServidor(servidorLogado);
	}
	
	//COMENTADO, TAREFA #24988
//	/**
//	 * Procedure
//	 * 
//	 * ORADB: AELK_MDL_RN.RN_MDLP_VER_INF_MDTO
//	 * 
//	 * Verifica se a opção "Paciente não soube Informar" esta desmarcada.
//	 * 
//	 * @param newInformacaoMdtoColeta
//	 * @throws ApplicationBusinessException 
//	 */
//	public void verificarInformacaoMedicamento(AelInformacaoMdtoColeta newInformacaoMdtoColeta) throws ApplicationBusinessException {
//		if (Boolean.TRUE.equals(newInformacaoMdtoColeta.getInformacaoColeta().getInfMedicacao())) {
//			throw new ApplicationBusinessException(InformacaoMdtoColetaRNExceptionCode.AEL_02251);
//		}
//	}
	
	/**
	 * Insere objeto AelInformacaoMdtoColeta.
	 * 
	 * @param newInformacaoMdtoColeta
	 * @throws ApplicationBusinessException  
	 */
	public void inserirInformacaoMdtoColeta(AelInformacaoMdtoColeta newInformacaoMdtoColeta) throws ApplicationBusinessException {
		executarBeforeInsertInformacaoMdtoColeta(newInformacaoMdtoColeta);
		getAelInformacaoMdtoColetaDAO().persistir(newInformacaoMdtoColeta);
	}	
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_MDL_ARU
	 * 
	 * @param oldInformacaoMdtoColeta
	 * @param newInformacaoMdtoColeta
	 * @throws ApplicationBusinessException 
	 */
	public void executarAfterUpdateInformacaoMdtoColeta(AelInformacaoMdtoColeta oldInformacaoMdtoColeta, 
			AelInformacaoMdtoColeta newInformacaoMdtoColeta) throws ApplicationBusinessException {
		if (CoreUtil.modificados(oldInformacaoMdtoColeta.getId(), newInformacaoMdtoColeta.getId()) 
				|| CoreUtil.modificados(oldInformacaoMdtoColeta.getServidor(), newInformacaoMdtoColeta.getServidor())
				|| CoreUtil.modificados(oldInformacaoMdtoColeta.getDthrIngeriu(), newInformacaoMdtoColeta.getDthrIngeriu()) 
				|| CoreUtil.modificados(oldInformacaoMdtoColeta.getDthrColetou(), newInformacaoMdtoColeta.getDthrColetou()) 
				|| CoreUtil.modificados(oldInformacaoMdtoColeta.getMedicamento(), newInformacaoMdtoColeta.getMedicamento()) 
				|| CoreUtil.modificados(oldInformacaoMdtoColeta.getCriadoEm(), newInformacaoMdtoColeta.getCriadoEm())
				) {
			
			inserirJournal(oldInformacaoMdtoColeta, DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * Atualiza objeto AelInformacaoMdtoColeta.
	 * 
	 * @param oldInformacaoMdtoColeta
	 * @param newInformacaoMdtoColeta
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarInformacaoMdtoColeta(AelInformacaoMdtoColeta newInformacaoMdtoColeta) throws ApplicationBusinessException {
		AelInformacaoMdtoColetaDAO informacaoMdtoColetaDAO = getAelInformacaoMdtoColetaDAO();
		//informacaoMdtoColetaDAO.atualizar(newInformacaoMdtoColeta);
		informacaoMdtoColetaDAO.merge(newInformacaoMdtoColeta);
		newInformacaoMdtoColeta = informacaoMdtoColetaDAO.obterPorChavePrimaria(newInformacaoMdtoColeta.getId());
		AelInformacaoMdtoColeta oldInformacaoMdtoColeta = informacaoMdtoColetaDAO.obterOriginal(newInformacaoMdtoColeta.getId());
		executarAfterUpdateInformacaoMdtoColeta(oldInformacaoMdtoColeta, newInformacaoMdtoColeta);
		informacaoMdtoColetaDAO.flush();
	}
	
	private void inserirJournal(final AelInformacaoMdtoColeta oldInformacaoMdtoColeta, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelInformacaoMdtoColetaJn informacaoMdtoColetaJn = new AelInformacaoMdtoColetaJn();
		informacaoMdtoColetaJn.setNomeUsuario(servidorLogado.getUsuario());
		informacaoMdtoColetaJn.setOperacao(operacao);
		informacaoMdtoColetaJn.setDthrColetou(oldInformacaoMdtoColeta.getDthrColetou());
		informacaoMdtoColetaJn.setIclSoeSeq(oldInformacaoMdtoColeta.getInformacaoColeta().getId().getSoeSeq());
		informacaoMdtoColetaJn.setIclSeqp(oldInformacaoMdtoColeta.getInformacaoColeta().getId().getSeqp());
		informacaoMdtoColetaJn.setSerMatricula(oldInformacaoMdtoColeta.getServidor().getId().getMatricula());
		informacaoMdtoColetaJn.setSerVinCodigo(oldInformacaoMdtoColeta.getServidor().getId().getVinCodigo());
		informacaoMdtoColetaJn.setSeqp(oldInformacaoMdtoColeta.getId().getSeqp());
		informacaoMdtoColetaJn.setDthrIngeriu(oldInformacaoMdtoColeta.getDthrIngeriu());
		informacaoMdtoColetaJn.setMedicamento(oldInformacaoMdtoColeta.getMedicamento());
		informacaoMdtoColetaJn.setCriadoEm(oldInformacaoMdtoColeta.getCriadoEm());
		getAelInformacaoMdtoColetaJnDAO().persistir(informacaoMdtoColetaJn);
	}

	/**
	 * Trigger 
	 *
	 * ORADB: AELT_MDL_ARD
	 * 
	 * @param oldInformacaoMdtoColeta
	 * @throws ApplicationBusinessException 
	 */
	public void executarAfterDeleteInformacaoMdtoColeta(AelInformacaoMdtoColeta oldInformacaoMdtoColeta) throws ApplicationBusinessException {
		inserirJournal(oldInformacaoMdtoColeta, DominioOperacoesJournal.DEL);
	}
	
	/**
	 * Remove objeto AelInformacaoMdtoColeta.
	 * 
	 * @param oldInformacaoMdtoColeta
	 * @throws ApplicationBusinessException 
	 */
	public void removerInformacaoMdtoColeta(AelInformacaoMdtoColeta oldInformacaoMdtoColeta) throws ApplicationBusinessException {
		getAelInformacaoMdtoColetaDAO().remover(oldInformacaoMdtoColeta);
		executarAfterDeleteInformacaoMdtoColeta(oldInformacaoMdtoColeta);
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected AelInformacaoMdtoColetaDAO getAelInformacaoMdtoColetaDAO() {
		return aelInformacaoMdtoColetaDAO;
	}
	
	protected AelInformacaoMdtoColetaJnDAO getAelInformacaoMdtoColetaJnDAO() {
		return aelInformacaoMdtoColetaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
