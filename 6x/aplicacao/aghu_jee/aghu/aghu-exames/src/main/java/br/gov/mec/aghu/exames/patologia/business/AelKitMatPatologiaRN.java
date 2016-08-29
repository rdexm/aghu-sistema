package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelKitMatPatologiaDAO;
import br.gov.mec.aghu.exames.dao.AelKitMatPatologiaJnDAO;
import br.gov.mec.aghu.model.AelKitMatPatologia;
import br.gov.mec.aghu.model.AelKitMatPatologiaJn;
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
public class AelKitMatPatologiaRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AelKitMatPatologiaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelKitMatPatologiaJnDAO aelKitMatPatologiaJnDAO;
	
	@Inject
	private AelKitMatPatologiaDAO aelKitMatPatologiaDAO;

	private static final long serialVersionUID = -6588530376472068321L;

	public enum AelKitMatPatologiaRNExceptionCode implements BusinessExceptionCode {
		AEL_02607, // Descrição  do Grupo de Kit´s não pode ser alterada!
		AEL_00353;
	}
	
	/**
	 * ORADB: AELT_LUK_ARD
	 */
	private void aeltLukArd(final AelKitMatPatologia aelKitMatPatologia) throws ApplicationBusinessException{
		createJournal(aelKitMatPatologia, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(Integer seq) throws ApplicationBusinessException{
		final AelKitMatPatologia aelKitMatPatologia = getAelKitMatPatologiaDao().obterPorChavePrimaria(seq); 
		
		if (aelKitMatPatologia == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		aeltLukArd(aelKitMatPatologia);
		getAelKitMatPatologiaDao().remover(aelKitMatPatologia);
	}

	
	/**
	 * ORADB: AELT_LUK_BRI
	 */
	private void aeltLukBri(final AelKitMatPatologia aelKitMatPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitMatPatologia.setCriadoEm(new Date());
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelKitMatPatologiaRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelKitMatPatologia aelKitMatPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelKitMatPatologia.setRapServidores(servidorLogado);
		aeltLukBri(aelKitMatPatologia);
		getAelKitMatPatologiaDao().persistir(aelKitMatPatologia);
	}
	
	private void createJournal(final AelKitMatPatologia reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelKitMatPatologiaJn journal = BaseJournalFactory.getBaseJournal(operacao,AelKitMatPatologiaJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getRapServidores());
		
		getAelKitMatPatologiaJnDAO().persistir(journal);
	}
	
	private AelKitMatPatologiaJnDAO getAelKitMatPatologiaJnDAO(){
		return aelKitMatPatologiaJnDAO;
	}

	/**
	 * ORADB: AELT_LUK_BRU
	 * Descrição  do Grupo de Kit´s não pode ser alterada!
	 */
	private void aeltLukBru(final AelKitMatPatologia alterado) throws ApplicationBusinessException{
		final AelKitMatPatologia original = getAelKitMatPatologiaDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLoKpverDesc();
		}
	}
	
	/**
	 * ORADB: aelk_luk_rn.rn_lukp_ver_desc
	 * Descrição do Kit de indice de blocos não pode ser alterada!
	 */
	private void rnLoKpverDesc() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelKitMatPatologiaRNExceptionCode.AEL_02607);
	}
	
	/**
	 * ORADB: AELT_LUK_ARU
	 */
	private void aeltLukAru(final AelKitMatPatologia alterado) throws ApplicationBusinessException{
		final AelKitMatPatologia original = getAelKitMatPatologiaDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getSeq(), original.getSeq()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getRapServidores(), original.getRapServidores() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelKitMatPatologia aelKitMatPatologia) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		aelKitMatPatologia.setRapServidores(servidorLogado);
		aeltLukBru(aelKitMatPatologia);
		getAelKitMatPatologiaDao().merge(aelKitMatPatologia);
		aeltLukAru(aelKitMatPatologia);
	}
	
	protected AelKitMatPatologiaDAO getAelKitMatPatologiaDao(){
		return aelKitMatPatologiaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}