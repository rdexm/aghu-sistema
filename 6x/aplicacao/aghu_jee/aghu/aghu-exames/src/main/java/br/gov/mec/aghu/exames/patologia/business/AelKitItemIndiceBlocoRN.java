package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelKitItemIndiceBlocoDAO;
import br.gov.mec.aghu.exames.dao.AelKitItemIndiceBlocoJnDAO;
import br.gov.mec.aghu.model.AelKitItemIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBlocoId;
import br.gov.mec.aghu.model.AelKitItemIndiceBlocoJn;
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
public class AelKitItemIndiceBlocoRN extends BaseBusiness  {
	
	private static final Log LOG = LogFactory.getLog(AelKitItemIndiceBlocoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelKitItemIndiceBlocoDAO aelKitItemIndiceBlocoDAO;
	
	@Inject
	private AelKitItemIndiceBlocoJnDAO aelKitItemIndiceBlocoJnDAO;

	private static final long serialVersionUID = -1918185728100792621L;

	public enum AelKitItemIndiceBlocoRNExceptionCode implements BusinessExceptionCode {
		AEL_02766, // -Descrição do Item do Indice de Blocos não pode ser alterada!
		AEL_00353;
	}
	
	
	/**
	 * ORADB: AELT_LA0_ARD
	 * @param AelKitItemIndiceBloco
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLa0Ard(final AelKitItemIndiceBloco aelKitItemIndiceBloco) throws ApplicationBusinessException{
		createJournal(aelKitItemIndiceBloco, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(final AelKitItemIndiceBlocoId id) throws ApplicationBusinessException{
		AelKitItemIndiceBloco aelKitItemIndiceBloco = getAelKitItemIndiceBlocoDao().obterPorChavePrimaria(id);
		
		if (aelKitItemIndiceBloco == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		aeltLa0Ard(aelKitItemIndiceBloco);
		getAelKitItemIndiceBlocoDao().remover(aelKitItemIndiceBloco);
	}

	
	/**
	 * ORADB: AELT_LA0_BRI
	 * @param aelKitItemIndiceBloco
	 * @throws ApplicationBusinessException  
	 */
	private void aeltLa0Bri(final AelKitItemIndiceBloco aelKitItemIndiceBloco) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitItemIndiceBloco.setCriadoEm(new Date()); 

		// Migrado do Form
		Short seqp = getAelKitItemIndiceBlocoDao().obterMaxSeqPPorAelKitIndiceBloco(aelKitItemIndiceBloco.getAelKitIndiceBloco());

		// Quando é o primeiro registro a sr criado para determinadO Kit Item Indice Bloco
		if(seqp == null){
			seqp = 1;	
		} else {
			seqp++;
		}
		
		aelKitItemIndiceBloco.getId().setSeqp(seqp);
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelKitItemIndiceBlocoRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelKitItemIndiceBloco aelKitItemIndiceBloco) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitItemIndiceBloco.setRapServidores(servidorLogado);
		aeltLa0Bri(aelKitItemIndiceBloco);
		getAelKitItemIndiceBlocoDao().persistir(aelKitItemIndiceBloco);
	}
	
	private void createJournal(final AelKitItemIndiceBloco reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelKitItemIndiceBlocoJn journal = BaseJournalFactory.getBaseJournal(operacao,AelKitItemIndiceBlocoJn.class, servidorLogado.getUsuario());
		
		journal.setSeqp(reg.getId().getSeqp());
		journal.setLo9Seq(reg.getId().getLo9Seq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getRapServidores());
		
		getAelKitItemIndiceBlocoJnDAO().persistir(journal);
	}
	
	private AelKitItemIndiceBlocoJnDAO getAelKitItemIndiceBlocoJnDAO(){
		return aelKitItemIndiceBlocoJnDAO;
	}

	/**
	 * ORADB: AELT_LA0_BRU
	 * 
	 * Descrição do Item do Kit de indice de blocos não pode ser alterada!
	 * 
	 * @param aelKitItemIndiceBloco
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLa0Bru(final AelKitItemIndiceBloco alterado) throws ApplicationBusinessException{
		final AelKitItemIndiceBloco original = getAelKitItemIndiceBlocoDao().obterOriginal(alterado.getId());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLa0VerDesc();
		}
	}
	
	/**
	 * ORADB: aelk_la0_rn.rn_la0p_ver_desc
	 * @throws ApplicationBusinessException 
	 */
	private void rnLa0VerDesc() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelKitItemIndiceBlocoRNExceptionCode.AEL_02766);
	}
	
	/**
	 * ORADB: AELT_LA0_ARU
	 * @param aelKitItemIndiceBloco
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLa0Aru(final AelKitItemIndiceBloco alterado) throws ApplicationBusinessException{
		final AelKitItemIndiceBloco original = getAelKitItemIndiceBlocoDao().obterOriginal(alterado.getId());
		
		if(CoreUtil.modificados(alterado.getId(), original.getId()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getRapServidores(), original.getRapServidores() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelKitItemIndiceBloco aelKitItemIndiceBloco) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitItemIndiceBloco.setRapServidores(servidorLogado);
		aeltLa0Bru(aelKitItemIndiceBloco);
		getAelKitItemIndiceBlocoDao().merge(aelKitItemIndiceBloco);
		aeltLa0Aru(aelKitItemIndiceBloco);
	}
	
	
	protected AelKitItemIndiceBlocoDAO getAelKitItemIndiceBlocoDao(){
		return aelKitItemIndiceBlocoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
