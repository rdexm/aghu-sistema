package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaViaAdmUnfDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdmUnfsJnDAO;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdmUnfsJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AfaViaAdmUnfRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(AfaViaAdmUnfRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AfaViaAdmUnfsJnDAO afaViaAdmUnfsJnDAO;
	
	@Inject
	private AfaViaAdmUnfDAO afaViaAdmUnfDAO;
	
	private static final long serialVersionUID = -621333380857703249L;

	public enum AfaViaAdmUnfRNExceptionCode implements BusinessExceptionCode {
		CHAVE_UNICA_VIOLADA_VIA_ADM_UNIDADE;
	}
	
	public void insert(AfaViaAdmUnf afaViaAdmUnf) throws ApplicationBusinessException{
		preInsert(afaViaAdmUnf);
		getAfaViaAdmUnfDAO().persistir(afaViaAdmUnf);
		getAfaViaAdmUnfDAO().flush();
	}
	
	public void update(AfaViaAdmUnf afaViaAdmUnf) throws ApplicationBusinessException {
		AfaViaAdmUnf original = getAfaViaAdmUnfDAO().obterOriginal(afaViaAdmUnf);
		preUpdate(afaViaAdmUnf);
		getAfaViaAdmUnfDAO().atualizar(afaViaAdmUnf);
		getAfaViaAdmUnfDAO().flush();
		posUpdate(original, afaViaAdmUnf);
	}
	
	/**
	 * ORADB : AFAT_VAU_BRI
	 * 
	 * @param afaViaAdmUnf
	 * @throws ApplicationBusinessException  
	 */
	public void preInsert(AfaViaAdmUnf afaViaAdmUnf) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		verificaChave(afaViaAdmUnf);
		afaViaAdmUnf.setCriadoEm(new Date());
		afaViaAdmUnf.setServidor(servidorLogado);	
	}
	
	
	/**
	 * ORADB : AFAT_VAU_BRU
	 * 
	 * @param afaViaAdmUnf
	 * @throws ApplicationBusinessException  
	 */
	public void preUpdate(AfaViaAdmUnf afaViaAdmUnf) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		afaViaAdmUnf.setServidor(servidorLogado);
	}
	
	
	/**
	 * ORADB : AFAT_VAU_ARU
	 * 
	 * @param afaViaAdmUnf
	 */
	public void posUpdate(AfaViaAdmUnf original, AfaViaAdmUnf afaViaAdmUnf){
		AfaViaAdmUnfsJn jn = new AfaViaAdmUnfsJn();
		
		if(!original.equals(afaViaAdmUnf)){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AfaViaAdmUnfsJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			//jn.setNomeUsuario(servidorLogado != null ? servidorLogado.getUsuario() : null);
			jn.setVadSigla(original.getId().getVadSigla());
			jn.setUnfSeq(original.getId().getUnfSeq());
			jn.setCriadoEm(original.getCriadoEm());
			jn.setIndSituacao(original.getIndSituacao());
			jn.setServidor(original.getServidor());
			
			getAfaViaAdmUnfsJnDAO().persistir(jn);
			getAfaViaAdmUnfsJnDAO().flush();
		}
	}
	
	public void verificaChave(AfaViaAdmUnf afaViaAdmUnf) throws ApplicationBusinessException{
		if(getAfaViaAdmUnfDAO().verificaChaveExistente(afaViaAdmUnf.getId().getVadSigla(), afaViaAdmUnf.getId().getUnfSeq())){
			throw new ApplicationBusinessException(AfaViaAdmUnfRNExceptionCode.CHAVE_UNICA_VIOLADA_VIA_ADM_UNIDADE);
		}
	}
	
	protected AfaViaAdmUnfsJnDAO getAfaViaAdmUnfsJnDAO(){
		return afaViaAdmUnfsJnDAO;
	}
	
	protected AfaViaAdmUnfDAO getAfaViaAdmUnfDAO(){
		return afaViaAdmUnfDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
