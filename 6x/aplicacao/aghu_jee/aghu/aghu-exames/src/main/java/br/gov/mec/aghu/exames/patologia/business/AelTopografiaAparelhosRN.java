package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelTopografiaAparelhoJnDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaAparelhosDAO;
import br.gov.mec.aghu.model.AelTopografiaAparelhoJn;
import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaAparelhosId;
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
public class AelTopografiaAparelhosRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AelTopografiaAparelhosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTopografiaAparelhosDAO aelTopografiaAparelhosDAO;
	
	@Inject
	private AelTopografiaAparelhoJnDAO aelTopografiaAparelhoJnDAO;

	private static final long serialVersionUID = -1918185728100792621L;

	public enum AelTopografiaAparelhosRNExceptionCode implements BusinessExceptionCode {
		AEL_02604, // Descrição da Nomenclatura Específica não pode ser alterada!
		AEL_00353;
	}
	
	
	/**
	 * ORADB: AELT_LUA_ARD
	 * @param AelTopografiaAparelhos
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLuaArd(final AelTopografiaAparelhos aelTopografiaAparelhos) throws ApplicationBusinessException{
		createJournal(aelTopografiaAparelhos, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(final AelTopografiaAparelhosId id) throws ApplicationBusinessException{
		AelTopografiaAparelhos aelTopografiaAparelhos = getAelTopografiaAparelhosDao().obterPorChavePrimaria(id);
		if (aelTopografiaAparelhos == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		aeltLuaArd(aelTopografiaAparelhos);
		getAelTopografiaAparelhosDao().remover(aelTopografiaAparelhos);
	}

	
	/**
	 * ORADB: AELT_LUA_BRI
	 * @param aelTopografiaAparelhos
	 * @throws ApplicationBusinessException  
	 */
	private void aeltLuaBri(final AelTopografiaAparelhos aelTopografiaAparelhos) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTopografiaAparelhos.setCriadoEm(new Date()); 

		// Migrado do Form
		Short seqp = getAelTopografiaAparelhosDao().obterMaxSeqPPorAelTopografiaSistemas(aelTopografiaAparelhos.getAelTopografiaSistemas());

		// Quando é o primeiro registro a sr criado para determinada topografia de aparelho
		if(seqp == null){
			seqp = 1;	
		} else {
			seqp++;
		}
		
		aelTopografiaAparelhos.getId().setSeqp(seqp);
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelTopografiaAparelhosRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelTopografiaAparelhos aelTopografiaAparelhos) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTopografiaAparelhos.setServidor(servidorLogado);
		aeltLuaBri(aelTopografiaAparelhos);
		getAelTopografiaAparelhosDao().persistir(aelTopografiaAparelhos);
	}
	
	private void createJournal(final AelTopografiaAparelhos reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelTopografiaAparelhoJn journal = BaseJournalFactory.getBaseJournal(operacao,AelTopografiaAparelhoJn.class, servidorLogado.getUsuario());
		
		journal.setSeqp(reg.getId().getSeqp());
		journal.setLutSeq(reg.getId().getLutSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		
		getAelTopografiaAparelhoJnDAO().persistir(journal);
	}
	
	private AelTopografiaAparelhoJnDAO getAelTopografiaAparelhoJnDAO(){
		return aelTopografiaAparelhoJnDAO;
	}

	/**
	 * ORADB: AELT_LUA_BRU
	 * @param aelTopografiaAparelhos
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLuaBru(final AelTopografiaAparelhos alterado) throws ApplicationBusinessException{
		final AelTopografiaAparelhos original = getAelTopografiaAparelhosDao().obterOriginal(alterado.getId());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLuapVerDesc();
		}
	}
	
	/**
	 * ORADB: aelk_lua_rn.rn_luap_ver_desc
	 * @throws ApplicationBusinessException 
	 */
	private void rnLuapVerDesc() throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelTopografiaAparelhosRNExceptionCode.AEL_02604);
	}
	
	/**
	 * ORADB: AELT_LUE_ARU
	 * @param aelTopografiaAparelhos
	 * @throws ApplicationBusinessException 
	 */
	private void aeltLueAru(final AelTopografiaAparelhos alterado) throws ApplicationBusinessException{
		final AelTopografiaAparelhos original = getAelTopografiaAparelhosDao().obterOriginal(alterado.getId());
		
		if(CoreUtil.modificados(alterado.getId(), original.getId()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getServidor(), original.getServidor() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void alterar(final AelTopografiaAparelhos aelTopografiaAparelhos) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTopografiaAparelhos.setServidor(servidorLogado);
		aeltLuaBru(aelTopografiaAparelhos);
		getAelTopografiaAparelhosDao().merge(aelTopografiaAparelhos);
		aeltLueAru(aelTopografiaAparelhos);
	}
	
	
	protected AelTopografiaAparelhosDAO getAelTopografiaAparelhosDao(){
		return aelTopografiaAparelhosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}