package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelTextoPadraoColoracsDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoColoracsJnDAO;
import br.gov.mec.aghu.model.AelTextoPadraoColoracs;
import br.gov.mec.aghu.model.AelTextoPadraoColoracsJn;
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
public class AelTextoPadraoColoracsRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AelTextoPadraoColoracsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTextoPadraoColoracsJnDAO aelTextoPadraoColoracsJnDAO;
	
	@Inject
	private AelTextoPadraoColoracsDAO aelTextoPadraoColoracsDAO;

	private static final long serialVersionUID = -3193559951844403842L;

	public enum AelTextoPadraoColoracsRNExceptionCode implements BusinessExceptionCode {
		AEL_02616, // Descrição Texto Padrão Coloração não pode ser alterada!
		AEL_00353;
	}
	
	/**
	 * ORADB: AELT_LU1_ARD
	 */
	private void aeltLu1Ard(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws ApplicationBusinessException{
		createJournal(aelTextoPadraoColoracs, DominioOperacoesJournal.DEL);
	}
	
	public void excluir(final Integer seq) throws ApplicationBusinessException{

		final AelTextoPadraoColoracs aelTextoPadraoColoracs = getAelTextoPadraoColoracsDao().obterPorChavePrimaria(seq);
		
		if (aelTextoPadraoColoracs == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		aeltLu1Ard(aelTextoPadraoColoracs);
		getAelTextoPadraoColoracsDao().remover(aelTextoPadraoColoracs);
	}

	
	/**
	 * ORADB: AELT_LU1_BRI
	 */
	private void aeltLu1Bri(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTextoPadraoColoracs.setCriadoEm(new Date()); 
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelTextoPadraoColoracsRNExceptionCode.AEL_00353);
		}
	}
	
	public void inserir(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTextoPadraoColoracs.setServidor(servidorLogado);
		aeltLu1Bri(aelTextoPadraoColoracs);
		getAelTextoPadraoColoracsDao().persistir(aelTextoPadraoColoracs);
	}
	
	/**
	 * ORADB: AELT_LU1_ARU
	 */
	private void aeltLu1Aru(final AelTextoPadraoColoracs alterado) throws ApplicationBusinessException{
		final AelTextoPadraoColoracs original = getAelTextoPadraoColoracsDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getSeq(), original.getSeq()) ||
				CoreUtil.modificados(alterado.getDescricao(), original.getDescricao()) ||
					CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao()) ||
						CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm()) ||
							CoreUtil.modificados(alterado.getServidor(), original.getServidor() )
				){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	private void createJournal(final AelTextoPadraoColoracs reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelTextoPadraoColoracsJn journal = BaseJournalFactory.getBaseJournal(operacao,AelTextoPadraoColoracsJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		
		getAelTextoPadraoColoracsJnDAO().persistir(journal);
	}
	
	private AelTextoPadraoColoracsJnDAO getAelTextoPadraoColoracsJnDAO(){
		return aelTextoPadraoColoracsJnDAO;
	}

	/**
	 * ORADB: AELT_LU1_BRU
	 */
	private void aeltLu1Bru(final AelTextoPadraoColoracs alterado) throws ApplicationBusinessException{
		final AelTextoPadraoColoracs original = getAelTextoPadraoColoracsDao().obterOriginal(alterado.getSeq());
		
		if(CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())){
			rnLu1pVerDesc(alterado);
		}
	}
	
	/**
	 * ORADB: aelk_lu1_rn.rn_lu1p_ver_desc
	 */
	private void rnLu1pVerDesc(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws ApplicationBusinessException{
		throw new ApplicationBusinessException(AelTextoPadraoColoracsRNExceptionCode.AEL_02616);
	}
	
	public void alterar(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTextoPadraoColoracs.setServidor(servidorLogado);
		aeltLu1Bru(aelTextoPadraoColoracs);
		getAelTextoPadraoColoracsDao().merge(aelTextoPadraoColoracs);
		aeltLu1Aru(aelTextoPadraoColoracs);
	}
	
	
	protected AelTextoPadraoColoracsDAO getAelTextoPadraoColoracsDao(){
		return aelTextoPadraoColoracsDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}