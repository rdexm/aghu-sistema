package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelNomenclaturaEspecJnDAO;
import br.gov.mec.aghu.exames.dao.AelNomenclaturaEspecsDAO;
import br.gov.mec.aghu.model.AelNomenclaturaEspecJn;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelNomenclaturaEspecsRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AelNomenclaturaEspecsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelNomenclaturaEspecsDAO aelNomenclaturaEspecsDAO;
	
	@Inject
	private AelNomenclaturaEspecJnDAO aelNomenclaturaEspecJnDAO;

	private static final long serialVersionUID = -2727530457706583473L;

	public enum AelNomenclaturaEspecsRNExceptionCode implements BusinessExceptionCode {
		AEL_02602, // Descrição da Nomenclatura Específica não pode ser alterada!
		AEL_00353;
	}
	
	
	/**
	 * ORADB: AELT_LUE_ARD
	 * @param AelNomenclaturaEspecs
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLueArd(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws ApplicationBusinessException{
		createJournal(aelNomenclaturaEspecs, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(AelNomenclaturaEspecs aelNomenclaturaEspecs) throws ApplicationBusinessException{
		aelNomenclaturaEspecs = getAelNomenclaturaEspecsDao().obterPorChavePrimaria(aelNomenclaturaEspecs.getId());
		aeltLueArd(aelNomenclaturaEspecs);
		getAelNomenclaturaEspecsDao().remover(aelNomenclaturaEspecs);
	}

	
	/**
	 * ORADB: AELT_LUE_BRI
	 * @param aelNomenclaturaEspecs
	 * @throws ApplicationBusinessException  
	 */
	private void aeltLueBri(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelNomenclaturaEspecs.setCriadoEm(new Date()); 

		// Migrado do Form
		Short seqp = getAelNomenclaturaEspecsDao().obterMaxSeqPPorAelNomenclaturaGenerics(aelNomenclaturaEspecs.getAelNomenclaturaGenerics());

		// Quando é o primeiro registro a sr criado para determinada nomenclatura genérica
		if(seqp == null){
			seqp = 1;	
		} else {
			seqp++;
		}
		
		aelNomenclaturaEspecs.getId().setSeqp(seqp);
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelNomenclaturaEspecsRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelNomenclaturaEspecs.setServidor(servidorLogado);
		aeltLueBri(aelNomenclaturaEspecs);
		getAelNomenclaturaEspecsDao().persistir(aelNomenclaturaEspecs);
	}
	
	private void createJournal(final AelNomenclaturaEspecs reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelNomenclaturaEspecJn journal = BaseJournalFactory.getBaseJournal(operacao,AelNomenclaturaEspecJn.class, servidorLogado.getUsuario());
		
		journal.setSeqp(reg.getId().getSeqp());
		journal.setLugSeq(reg.getId().getLugSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		
		getAelNomenclaturaEspecJnDAO().persistir(journal);
	}
	
	private AelNomenclaturaEspecJnDAO getAelNomenclaturaEspecJnDAO(){
		return aelNomenclaturaEspecJnDAO;
	}

	/**
	 * ORADB: AELT_LUE_ARU
	 * @param aelNomenclaturaEspecs
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLueBru(final AelNomenclaturaEspecs alterado) throws ApplicationBusinessException{
		final AelNomenclaturaEspecs original = getAelNomenclaturaEspecsDao().obterOriginal(alterado.getId());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLuepVerDesc();
		}
	}
	
	/**
	 * ORADB: aelk_lug_rn.rn_lugp_ver_desc
	 * @throws ApplicationBusinessException 
	 */
	private void rnLuepVerDesc() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelNomenclaturaEspecsRNExceptionCode.AEL_02602);
	}
	
	/**
	 * ORADB: AELT_LUE_ARU
	 */
	private void aeltLueAru(final AelNomenclaturaEspecs alterado) throws ApplicationBusinessException{
		final AelNomenclaturaEspecs original = getAelNomenclaturaEspecsDao().obterOriginal(alterado.getId());
		
		if(CoreUtil.modificados(alterado.getId(), original.getId()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getServidor(), original.getServidor() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelNomenclaturaEspecs.setServidor(servidorLogado);
		aeltLueBru(aelNomenclaturaEspecs);
		getAelNomenclaturaEspecsDao().merge(aelNomenclaturaEspecs);
		aeltLueAru(aelNomenclaturaEspecs);
	}
	
	
	protected AelNomenclaturaEspecsDAO getAelNomenclaturaEspecsDao(){
		return aelNomenclaturaEspecsDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
