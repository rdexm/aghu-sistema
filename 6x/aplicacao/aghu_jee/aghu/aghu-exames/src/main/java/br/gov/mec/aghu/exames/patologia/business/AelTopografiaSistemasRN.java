package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelTopografiaSistemaJnDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaSistemasDAO;
import br.gov.mec.aghu.model.AelTopografiaSistemaJn;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
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
public class AelTopografiaSistemasRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AelTopografiaSistemasRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTopografiaSistemaJnDAO aelTopografiaSistemaJnDAO;
	
	@Inject
	private AelTopografiaSistemasDAO aelTopografiaSistemasDAO;

	private static final long serialVersionUID = -7540209626146268131L;

	public enum AelTopografiaSistemasRNExceptionCode implements BusinessExceptionCode {
		AEL_02603, // Descrição da Nomenclatura Genérica não pode ser alterada!
		AEL_00353;
	}
	
	
	/**
	 * ORADB: AELT_LUT_ARD
	 * @param aelTopografiaSistemas
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLutArd(final AelTopografiaSistemas aelTopografiaSistemas) throws ApplicationBusinessException{
		createJournal(aelTopografiaSistemas, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(final Integer seq) throws ApplicationBusinessException{
		AelTopografiaSistemas aelTopografiaSistemas = getAelTopografiaSistemasDao().obterPorChavePrimaria(seq);
		
		if (aelTopografiaSistemas == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		aeltLutArd(aelTopografiaSistemas);
		getAelTopografiaSistemasDao().remover(aelTopografiaSistemas);
	}

	/**
	 * ORADB: AELT_LUT_BRI
	 * @param AelTopografiaSistemas
	 * @throws ApplicationBusinessException  
	 */
	private void aeltLutBri(final AelTopografiaSistemas aelTopografiaSistemas) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTopografiaSistemas.setCriadoEm(new Date()); 
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelTopografiaSistemasRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelTopografiaSistemas aelTopografiaSistemas) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		aelTopografiaSistemas.setServidor(servidorLogado);
		aeltLutBri(aelTopografiaSistemas);
		getAelTopografiaSistemasDao().persistir(aelTopografiaSistemas);
	}
	
	private void createJournal(final AelTopografiaSistemas reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelTopografiaSistemaJn journal = BaseJournalFactory.getBaseJournal(operacao,AelTopografiaSistemaJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		
		getAelTopografiaSistemaJnDAO().persistir(journal);
	}
	
	private AelTopografiaSistemaJnDAO getAelTopografiaSistemaJnDAO(){
		return aelTopografiaSistemaJnDAO;
	}

	/**
	 * ORADB: AELT_LUT_BRU
	 * @param aelTopografiaSistemas
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLutBru(final AelTopografiaSistemas alterado) throws ApplicationBusinessException{
		final AelTopografiaSistemas original = getAelTopografiaSistemasDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLutpVerDesc();
		}
	}
	
	/**
	 * ORADB: aelk_lut_rn.rn_lutp_ver_desc
	 * @throws ApplicationBusinessException 
	 */
	private void rnLutpVerDesc() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelTopografiaSistemasRNExceptionCode.AEL_02603);
	}
	
	/**
	 * ORADB: AELT_LUT_ARU
	 * @param aelTopografiaSistemas
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLutAru(final AelTopografiaSistemas alterado) throws ApplicationBusinessException{
		final AelTopografiaSistemas original = getAelTopografiaSistemasDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getSeq(), original.getSeq()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getServidor(), original.getServidor() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelTopografiaSistemas aelTopografiaSistemas) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTopografiaSistemas.setServidor(servidorLogado);
		aeltLutBru(aelTopografiaSistemas);
		getAelTopografiaSistemasDao().merge(aelTopografiaSistemas);
		aeltLutAru(aelTopografiaSistemas);
	}
	
	
	protected AelTopografiaSistemasDAO getAelTopografiaSistemasDao(){
		return aelTopografiaSistemasDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}