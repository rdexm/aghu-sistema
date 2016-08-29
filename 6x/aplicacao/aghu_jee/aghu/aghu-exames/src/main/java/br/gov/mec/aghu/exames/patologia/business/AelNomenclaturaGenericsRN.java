package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelNomenclaturaGenericJnDAO;
import br.gov.mec.aghu.exames.dao.AelNomenclaturaGenericsDAO;
import br.gov.mec.aghu.model.AelNomenclaturaGenericJn;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelNomenclaturaGenericsRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AelNomenclaturaGenericsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelNomenclaturaGenericJnDAO aelNomenclaturaGenericJnDAO;
	
	@Inject
	private AelNomenclaturaGenericsDAO aelNomenclaturaGenericsDAO;

	private static final long serialVersionUID = 8186879957451533959L;

	public enum AelNomenclaturaGenericsRNExceptionCode implements BusinessExceptionCode {
		AEL_02601, // Descrição da Nomenclatura Genérica não pode ser alterada!
		AEL_00353
		;
	}
	
	
	/**
	 * ORADB: AELT_LUG_ARD
	 */
	private void aeltLugArd(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws ApplicationBusinessException{
		createJournal(aelNomenclaturaGenerics, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(AelNomenclaturaGenerics aelNomenclaturaGenerics) throws ApplicationBusinessException{
		aelNomenclaturaGenerics = getAelNomenclaturaGenericsDao().obterPorChavePrimaria(aelNomenclaturaGenerics.getSeq());
		aeltLugArd(aelNomenclaturaGenerics);
		getAelNomenclaturaGenericsDao().remover(aelNomenclaturaGenerics);
		getAelNomenclaturaGenericsDao().flush();
	}

	
	/**
	 * ORADB: AELT_LUG_BRI
	 */
	private void aeltLugBri(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelNomenclaturaGenerics.setCriadoEm(new Date()); 
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelNomenclaturaGenericsRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelNomenclaturaGenerics.setServidor(servidorLogado);
		aeltLugBri(aelNomenclaturaGenerics);
		getAelNomenclaturaGenericsDao().persistir(aelNomenclaturaGenerics);
		getAelNomenclaturaGenericsDao().flush();
	}
	
	private void createJournal(final AelNomenclaturaGenerics reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelNomenclaturaGenericJn journal = BaseJournalFactory.getBaseJournal(operacao,AelNomenclaturaGenericJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		
		getAelNomenclaturaGenericJnDAO().persistir(journal);
	}
	
	private AelNomenclaturaGenericJnDAO getAelNomenclaturaGenericJnDAO(){
		return aelNomenclaturaGenericJnDAO;
	}

	/**
	 * ORADB: AELT_LUG_BRU
	 */
	private void aeltLugBru(final AelNomenclaturaGenerics alterado) throws ApplicationBusinessException{
		final AelNomenclaturaGenerics original = getAelNomenclaturaGenericsDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLu1pVerDesc();
		}
	}
	
	/**
	 * ORADB: aelk_lug_rn.rn_lugp_ver_desc
	 */
	private void rnLu1pVerDesc() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelNomenclaturaGenericsRNExceptionCode.AEL_02601);
	}
	
	/**
	 * ORADB: AELT_LUG_ARU
	 */
	private void aeltLugAru(final AelNomenclaturaGenerics alterado) throws ApplicationBusinessException{
		final AelNomenclaturaGenerics original = getAelNomenclaturaGenericsDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getSeq(), original.getSeq()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getServidor(), original.getServidor() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelNomenclaturaGenerics.setServidor(servidorLogado);
		aeltLugBru(aelNomenclaturaGenerics);
		getAelNomenclaturaGenericsDao().merge(aelNomenclaturaGenerics);
		aeltLugAru(aelNomenclaturaGenerics);
	}
	
	
	protected AelNomenclaturaGenericsDAO getAelNomenclaturaGenericsDao(){
		return aelNomenclaturaGenericsDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
