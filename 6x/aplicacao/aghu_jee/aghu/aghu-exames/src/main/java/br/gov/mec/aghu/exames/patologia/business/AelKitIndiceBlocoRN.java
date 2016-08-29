package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelKitIndiceBlocoDAO;
import br.gov.mec.aghu.exames.dao.AelKitIndiceBlocoJnDAO;
import br.gov.mec.aghu.model.AelKitIndiceBloco;
import br.gov.mec.aghu.model.AelKitIndiceBlocoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelKitIndiceBlocoRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AelKitIndiceBlocoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelKitIndiceBlocoDAO aelKitIndiceBlocoDAO;
	
	@Inject
	private AelKitIndiceBlocoJnDAO aelKitIndiceBlocoJnDAO;

	private static final long serialVersionUID = 8713470650062825453L;

	public enum AelKitIndiceBlocoRNExceptionCode implements BusinessExceptionCode {
		AEL_02765, // Descrição  do Grupo de Kit´s de índice blocos não pode ser alterada!
		AEL_00353;
	}
	
	
	/**
	 * ORADB: AELT_LO9_ARD
	 * @param aelKitIndiceBloco
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLo9Ard(final AelKitIndiceBloco aelKitIndiceBloco) throws ApplicationBusinessException{
		createJournal(aelKitIndiceBloco, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(final Integer seq) throws ApplicationBusinessException{
		AelKitIndiceBloco aelKitIndiceBloco = getAelKitIndiceBlocoDao().obterPorChavePrimaria(seq);
		
		if (aelKitIndiceBloco == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		aeltLo9Ard(aelKitIndiceBloco);
		getAelKitIndiceBlocoDao().remover(aelKitIndiceBloco);
	}

	
	/**
	 * ORADB: AELT_LO9_BRI
	 * @param AelKitIndiceBloco
	 * @throws ApplicationBusinessException  
	 */
	private void aeltLo9Bri(final AelKitIndiceBloco aelKitIndiceBloco) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitIndiceBloco.setCriadoEm(new Date()); 
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelKitIndiceBlocoRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelKitIndiceBloco aelKitIndiceBloco) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitIndiceBloco.setRapServidores(servidorLogado);
		aeltLo9Bri(aelKitIndiceBloco);
		getAelKitIndiceBlocoDao().persistir(aelKitIndiceBloco);
	}
	
	private void createJournal(final AelKitIndiceBloco reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelKitIndiceBlocoJn journal = BaseJournalFactory.getBaseJournal(operacao,AelKitIndiceBlocoJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getRapServidores());
		
		getAelKitIndiceBlocoJnDAO().persistir(journal);
	}
	
	private AelKitIndiceBlocoJnDAO getAelKitIndiceBlocoJnDAO(){
		return aelKitIndiceBlocoJnDAO;
	}

	/**
	 * ORADB: AELT_LO9_BRU
	 * @param aelKitIndiceBloco
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLo9Bru(final AelKitIndiceBloco alterado) throws ApplicationBusinessException{
		final AelKitIndiceBloco original = getAelKitIndiceBlocoDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLop9VerDesc();
		}
	}
	
	/**
	 * ORADB: aelk_lo9_rn.rn_lo9p_ver_desc
	 * Descrição do Kit de indice de blocos não pode ser alterada!
	 * @throws ApplicationBusinessException 
	 */
	private void rnLop9VerDesc() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelKitIndiceBlocoRNExceptionCode.AEL_02765);
	}
	
	/**
	 * ORADB: AELT_LO9_ARU
	 * @param aelKitIndiceBloco
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLo9Aru(final AelKitIndiceBloco alterado) throws ApplicationBusinessException{
		final AelKitIndiceBloco original = getAelKitIndiceBlocoDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getSeq(), original.getSeq()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getRapServidores(), original.getRapServidores() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelKitIndiceBloco aelKitIndiceBloco) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitIndiceBloco.setRapServidores(servidorLogado);
		aeltLo9Bru(aelKitIndiceBloco);
		getAelKitIndiceBlocoDao().merge(aelKitIndiceBloco);
		aeltLo9Aru(aelKitIndiceBloco);
	}
	
	
	protected AelKitIndiceBlocoDAO getAelKitIndiceBlocoDao(){
		return aelKitIndiceBlocoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
