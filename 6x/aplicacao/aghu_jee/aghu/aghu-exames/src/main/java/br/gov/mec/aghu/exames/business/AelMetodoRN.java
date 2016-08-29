package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelMetodoDAO;
import br.gov.mec.aghu.exames.dao.AelMetodoJnDAO;
import br.gov.mec.aghu.model.AelMetodo;
import br.gov.mec.aghu.model.AelMetodoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author amalmeida
 * 
 */
@Stateless
public class AelMetodoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelMetodoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelMetodoDAO aelMetodoDAO;
	
	@Inject
	private AelMetodoJnDAO aelMetodoJnDAO;

	private static final long serialVersionUID = 5960016652904441045L;

	public enum AelMetodoRNExceptionCode implements BusinessExceptionCode {
		AEL_01730,AEL_01734;
	}

	/**
	 * ORADB TRIGGER AELT_MTD_BRI (INSERT)
	 * @param aelMetodo
	 * @throws BaseException
	 */
	private void preInserir(AelMetodo metodo) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		metodo.setCriadoEm(new Date());//RN1
		metodo.setServidor(servidorLogado);//RN2
	}
	
	/**
	 * Inserir AelaelMetodo
	 */
	public void inserir(AelMetodo aelMetodo) throws BaseException{
		this.preInserir(aelMetodo);		
		this.getAelMetodoDAO().persistir(aelMetodo);
	//	this.posInserir(aelMetodo);
	}
	
	
	/**
	 * ORADB AELT_MTD_BRU (UPDATE)
	 * @param aelMetodo
	 * @throws BaseException
	 */
	private void preAtualizar(AelMetodo metodo, AelMetodo metodoAntigo) throws BaseException{
	
		this.validaCamposAlterados(metodo, metodoAntigo);//RN1
		
		this.validaDescricaoAlterada(metodo, metodoAntigo);//RN2
		
	}

	protected void validaDescricaoAlterada(AelMetodo metodo,
			AelMetodo metodoAntigo) throws BaseException {
		/**
		 * RN2
		 */
		if(CoreUtil.modificados(metodoAntigo.getDescricao(), metodo.getDescricao())){
			//A descrição do método não pode ser alterada.
			throw new BaseException(AelMetodoRNExceptionCode.AEL_01734);
		}
	}

	protected void validaCamposAlterados(AelMetodo metodo, AelMetodo metodoAntigo)
			throws BaseException {
		/**
		 * RN1
		 */
		if(CoreUtil.modificados(metodoAntigo.getCriadoEm(),metodo.getCriadoEm()) || CoreUtil.modificados(metodoAntigo.getServidor(),metodo.getServidor())){
			//Os campos Data de Criação e Servidor não podem ser alterados.
			throw new BaseException(AelMetodoRNExceptionCode.AEL_01730);
		}
	}
	
	public void ativarInativarAleMetodo(AelMetodo metodo) throws BaseException{
		metodo.setSituacao(metodo.getSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A);
		atualizar(metodo);
	}
	
	public void atualizar(AelMetodo metodo) throws BaseException{
		AelMetodo metodoAntigo = getAelMetodoDAO().obterOriginal(metodo);
		
		this.preAtualizar(metodo,metodoAntigo);		
		this.getAelMetodoDAO().merge(metodo);
		this.posAtualizar(metodo,metodoAntigo);
	}
	
	
	/**
	 * ORADB PROCEDURE AELT_MTD_ARU (UPDATE)
	 */
	private void posAtualizar(AelMetodo aelMetodo,AelMetodo metodoAntigo) throws BaseException{
	
		/**
		 * RN1
		 */
		if(CoreUtil.modificados(aelMetodo.getSituacao(),metodoAntigo.getSituacao())){
			
			AelMetodoJn metodoJn =  criarAelMetodoJn(aelMetodo, DominioOperacoesJournal.UPD);
			getAelMetodoJnDAO().persistir(metodoJn);
			getAelMetodoJnDAO().flush();
			
		}
		
	}
	
	private AelMetodoJn criarAelMetodoJn(AelMetodo aelMetodo, DominioOperacoesJournal dominio) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelMetodoJn aelMetodoJn = BaseJournalFactory.getBaseJournal(dominio, AelMetodoJn.class, servidorLogado.getUsuario());
		
		aelMetodoJn.setSeq(aelMetodo.getSeq());
		aelMetodoJn.setDescricao(aelMetodo.getDescricao());
		aelMetodoJn.setIndSituacao(aelMetodo.getSituacao().toString());
		aelMetodoJn.setCriadoEm(aelMetodo.getCriadoEm());
		aelMetodoJn.setServidor(aelMetodo.getServidor());
		
		return aelMetodoJn;
	}

	/**
	 * Getters para RNs e DAOs
	 */
	
	protected AelMetodoDAO getAelMetodoDAO() {
		return aelMetodoDAO;
	}
	
	protected AelMetodoJnDAO getAelMetodoJnDAO() {
		return aelMetodoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
