package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelKitItemMatPatologiaDAO;
import br.gov.mec.aghu.exames.dao.AelKitItemMatPatologiaJnDAO;
import br.gov.mec.aghu.model.AelKitItemMatPatologia;
import br.gov.mec.aghu.model.AelKitItemMatPatologiaId;
import br.gov.mec.aghu.model.AelKitItemMatPatologiaJn;
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
public class AelKitItemMatPatologiaRN extends BaseBusiness  {
	
	private static final Log LOG = LogFactory.getLog(AelKitItemMatPatologiaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelKitItemMatPatologiaJnDAO aelKitItemMatPatologiaJnDAO;
	
	@Inject
	private AelKitItemMatPatologiaDAO aelKitItemMatPatologiaDAO;

	private static final long serialVersionUID = -2132186591749933549L;

	public enum AelKitItemMatPatologiaRNExceptionCode implements BusinessExceptionCode {
		AEL_02608, // Descrição do Item do Kit de Materiais não pode ser alterada!
		AEL_00353;
	}
	
	
	/**
	 * ORADB: AELT_LUQ_ARD
	 * @param AelKitItemMatPatologia
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLuqArd(final AelKitItemMatPatologia aelKitItemMatPatologia) throws ApplicationBusinessException{
		createJournal(aelKitItemMatPatologia, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(AelKitItemMatPatologiaId id) throws ApplicationBusinessException{
		final AelKitItemMatPatologia aelKitItemMatPatologia = getAelKitItemMatPatologiaDao().obterPorChavePrimaria(id); 
		
		if (aelKitItemMatPatologia == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		aeltLuqArd(aelKitItemMatPatologia);
		getAelKitItemMatPatologiaDao().remover(aelKitItemMatPatologia);
	}

	
	/**
	 * ORADB: AELT_LUQ_BRI
	 * @param aelKitItemMatPatologia
	 * @throws ApplicationBusinessException  
	 */
	private void aeltLuqBri(final AelKitItemMatPatologia aelKitItemMatPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitItemMatPatologia.setCriadoEm(new Date()); 

		// Migrado do Form
		Short seqp = getAelKitItemMatPatologiaDao().obterMaxSeqPPorAelKitMatPatologia(aelKitItemMatPatologia.getAelKitMatPatologia());

		// Quando é o primeiro registro a sr criado para determinado Kit Item Material
		if(seqp == null){
			seqp = 1;	
		} else {
			seqp++;
		}
		
		aelKitItemMatPatologia.getId().setSeqp(seqp);
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelKitItemMatPatologiaRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelKitItemMatPatologia aelKitItemMatPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitItemMatPatologia.setRapServidores(servidorLogado);
		aeltLuqBri(aelKitItemMatPatologia);
		getAelKitItemMatPatologiaDao().persistir(aelKitItemMatPatologia);
	}
	
	private void createJournal(final AelKitItemMatPatologia reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelKitItemMatPatologiaJn journal = BaseJournalFactory.getBaseJournal(operacao,AelKitItemMatPatologiaJn.class, servidorLogado.getUsuario());
		
		journal.setSeqp(reg.getId().getSeqp());
		journal.setLukSeq(reg.getId().getLukSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getRapServidores());
		
		getAelKitItemMatPatologiaJnDAO().persistir(journal);
	}
	
	private AelKitItemMatPatologiaJnDAO getAelKitItemMatPatologiaJnDAO(){
		return aelKitItemMatPatologiaJnDAO;
	}

	/**
	 * ORADB: AELT_LUQ_BRU
	 * 
	 * Descrição do Item do Kit de Materiais não pode ser alterada!
	 * 
	 * @param aelKitItemMatPatologia
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLuqBru(final AelKitItemMatPatologia alterado) throws ApplicationBusinessException{
		final AelKitItemMatPatologia original = getAelKitItemMatPatologiaDao().obterOriginal(alterado.getId());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLuqPVerDesc();
		}
	}
	
	/**
	 * ORADB: aelk_luq_rn.rn_luqp_ver_desc
	 * @throws ApplicationBusinessException 
	 */
	private void rnLuqPVerDesc() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelKitItemMatPatologiaRNExceptionCode.AEL_02608);
	}
	
	/**
	 * ORADB: AELT_LUQ_ARU
	 * @param aelKitItemMatPatologia
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLuqAru(final AelKitItemMatPatologia alterado) throws ApplicationBusinessException{
		final AelKitItemMatPatologia original = getAelKitItemMatPatologiaDao().obterOriginal(alterado.getId());
		
		if(CoreUtil.modificados(alterado.getId(), original.getId()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getRapServidores(), original.getRapServidores() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelKitItemMatPatologia aelKitItemMatPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitItemMatPatologia.setRapServidores(servidorLogado);
		aeltLuqBru(aelKitItemMatPatologia);
		getAelKitItemMatPatologiaDao().merge(aelKitItemMatPatologia);
		aeltLuqAru(aelKitItemMatPatologia);
	}
	
	
	protected AelKitItemMatPatologiaDAO getAelKitItemMatPatologiaDao(){
		return aelKitItemMatPatologiaDAO;
	} 

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
