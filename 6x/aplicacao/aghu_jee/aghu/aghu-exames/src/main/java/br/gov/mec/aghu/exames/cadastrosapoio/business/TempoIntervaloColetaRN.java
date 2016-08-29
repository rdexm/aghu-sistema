package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelTmpIntervaloColetaDAO;
import br.gov.mec.aghu.exames.dao.AelTmpIntervaloColetaJnDAO;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AelTmpIntervaloColetaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class TempoIntervaloColetaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TempoIntervaloColetaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTmpIntervaloColetaDAO aelTmpIntervaloColetaDAO;
	
	@Inject
	private AelTmpIntervaloColetaJnDAO aelTmpIntervaloColetaJnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8946289995304058682L;

	public enum TempoIntervaloColetaRNExceptionCode implements BusinessExceptionCode {
		AEL_00353;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	protected void criarJournal(AelTmpIntervaloColeta tempo, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelTmpIntervaloColetaJn tempoJournal = BaseJournalFactory.getBaseJournal(operacao, AelTmpIntervaloColetaJn.class, servidorLogado.getUsuario());
		
		tempoJournal.setIcoSeq(tempo.getId().getIcoSeq());
		tempoJournal.setSeqp(tempo.getId().getSeqp());
		tempoJournal.setTempo(tempo.getTempo());
		tempoJournal.setCriadoEm(tempo.getCriadoEm());
		tempoJournal.setServidor(tempo.getServidor());
		
		getAelTmpIntervaloColetaJnDAO().persistir(tempoJournal);
	}
	
	//--------------------------------------------------
	//Insert
	
	public AelTmpIntervaloColeta inserir(AelTmpIntervaloColeta tempo) throws ApplicationBusinessException {
				
		//Regras pré-insert intervalo
		preInsert(tempo);
		
		//Insert intervalo
		getAelTmpIntervaloColetaDAO().persistir(tempo);
		
		return tempo;
	}
	
	/**
	 * @throws ApplicationBusinessException  
	 * @ORADB Trigger AELT_TIL_BRI
	 * 
	 */
	protected void preInsert(AelTmpIntervaloColeta tempo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Atualiza data de criação
		tempo.setCriadoEm(new Date());
		
		//Verifica e atribui usuário logado
		if(servidorLogado == null) {
			TempoIntervaloColetaRNExceptionCode.AEL_00353.throwException();
		} else {
			tempo.setServidor(servidorLogado);
		}
	}
	
	//--------------------------------------------------
	//Update
	
	public AelTmpIntervaloColeta atualizar(AelTmpIntervaloColeta tempo) throws ApplicationBusinessException {
		//Recupera o objeto antigo
		AelTmpIntervaloColeta tempoOld = getAelTmpIntervaloColetaDAO().obterOriginal(tempo.getId().getIcoSeq(), tempo.getId().getSeqp());
		
		//Regras pré-update
		preUpdate(tempo);
		
		//Regras pós-update
		posUpdate(tempoOld, tempo);
		
		return tempo;
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AELT_TIL_BRU
	 * 
	 */
	protected void preUpdate(AelTmpIntervaloColeta tempo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Verifica usuário logado
		if(servidorLogado == null) {
			TempoIntervaloColetaRNExceptionCode.AEL_00353.throwException();
		}
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AELT_TIL_ARU
	 * 
	 */
	protected void posUpdate(AelTmpIntervaloColeta tempoOld, AelTmpIntervaloColeta tempoNew) throws ApplicationBusinessException {
		//Verifica se algum campo foi alterado
		if(CoreUtil.modificados(tempoNew.getId(), tempoOld.getId())
			|| CoreUtil.modificados(tempoNew.getTempo(), tempoOld.getTempo())
			|| CoreUtil.modificados(tempoNew.getCriadoEm(), tempoOld.getCriadoEm())
			|| CoreUtil.modificados(tempoNew.getServidor(), tempoOld.getServidor())) {
			
			//Cria uma entrada na journal
			criarJournal(tempoOld, DominioOperacoesJournal.UPD);
		}
	}
	
	//--------------------------------------------------
	//Delete
	
	public void remover(Short codigoIntervaloColeta, Short codigoTempo) throws ApplicationBusinessException {
		AelTmpIntervaloColeta tempo = getAelTmpIntervaloColetaDAO().obterPeloId(codigoIntervaloColeta, codigoTempo);
		
		if(tempo != null) {
			//Delete
			getAelTmpIntervaloColetaDAO().remover(tempo);
			
			//Regras pós-delete
			posDelete(tempo);
		}
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AELT_TIL_ARD
	 * 
	 */
	protected void posDelete(AelTmpIntervaloColeta tempo) throws ApplicationBusinessException {
		//Cria uma entrada na journal
		criarJournal(tempo, DominioOperacoesJournal.DEL);
	}
	
	//--------------------------------------------------
	//Getters
	
	protected AelTmpIntervaloColetaDAO getAelTmpIntervaloColetaDAO() {
		return aelTmpIntervaloColetaDAO;
	}
	
	protected AelTmpIntervaloColetaJnDAO getAelTmpIntervaloColetaJnDAO() {
		return aelTmpIntervaloColetaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
