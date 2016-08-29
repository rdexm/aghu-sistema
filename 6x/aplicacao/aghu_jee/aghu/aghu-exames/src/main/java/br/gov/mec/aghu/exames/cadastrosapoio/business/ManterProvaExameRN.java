package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExamesProvaDAO;
import br.gov.mec.aghu.exames.dao.AelExamesProvaJnDAO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesProva;
import br.gov.mec.aghu.model.AelExamesProvaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterProvaExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterProvaExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExamesProvaJnDAO aelExamesProvaJnDAO;
	
	@Inject
	private AelExamesProvaDAO aelExamesProvaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5999205210549119947L;

	public enum ManterProvaExameRNExceptionCode implements BusinessExceptionCode {

		AEL_00428, AEL_00369, EXAME_PROVA_DUPLICADO;

	}

	/**
	 * ORADB AELT_EXP_BRI
	 * @param examesProva
	 * @throws ApplicationBusinessException  
	 */
	public void inserirExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		examesProva.setServidor(servidorLogado);
		preInserirExamesProva(examesProva);
		getAelExamesProvaDAO().persistir(examesProva);
		getAelExamesProvaDAO().flush();

	}

	/**
	 * ORADB AELT_EXP_BRU, AELT_EXP_ARU
	 * @param examesProva
	 * @throws ApplicationBusinessException
	 */
	public void atualizarExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException {

		AelExamesProva oldExamesProva = getAelExamesProvaDAO().obterAelExamesProvaPorId(examesProva.getId());
		AelExamesProva auxExamesProva = new AelExamesProva();

		try {

			BeanUtils.copyProperties(auxExamesProva, oldExamesProva);

		} catch (IllegalAccessException e) {

			this.logError(e.getMessage());

		} catch (InvocationTargetException e) {

			this.logError(e.getMessage());

		}

		preAtualizarExamesProva(examesProva, oldExamesProva);
		getAelExamesProvaDAO().merge(examesProva);
		getAelExamesProvaDAO().flush();
		posAtualizarExamesProva(examesProva, auxExamesProva);

	}
	
	/**
	 * ORADB AELT_EXP_ARD
	 * @param examesProva
	 * @throws ApplicationBusinessException 
	 */
	public void removerExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException {
		examesProva = getAelExamesProvaDAO().obterPorChavePrimaria(examesProva.getId());
		getAelExamesProvaDAO().remover(examesProva);
		getAelExamesProvaDAO().flush();
		posRemoverExamesProva(examesProva);
		
	}

	/**
	 * ORADB AELT_EXP_BRI
	 * @param examesProva
	 * @throws ApplicationBusinessException
	 */
	private void preInserirExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException {

		verificarDuplicidade(examesProva.getExamesMaterialAnalise(), examesProva.getExamesMaterialAnaliseEhProva());
		verificarProva(examesProva.getExamesMaterialAnalise(), examesProva.getExamesMaterialAnaliseEhProva());

	}

	/**
	 * ORADB AELT_EXP_BRU
	 * @param examesProva
	 * @throws ApplicationBusinessException
	 */
	private void preAtualizarExamesProva(AelExamesProva examesProva, AelExamesProva oldExamesProva) throws ApplicationBusinessException {

		validarServidor(examesProva.getServidor(), oldExamesProva.getServidor());
		verificarProva(examesProva.getExamesMaterialAnalise(), examesProva.getExamesMaterialAnaliseEhProva());

	}

	/**
	 * ORADB AELT_EXP_ARU
	 * @param examesProva
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizarExamesProva(AelExamesProva examesProva, AelExamesProva oldExamesProva) throws ApplicationBusinessException {

		if (CoreUtil.modificados(examesProva.getId().getEmaExaSigla(), oldExamesProva.getId().getEmaExaSigla())
				|| CoreUtil.modificados(examesProva.getId().getEmaManSeq(), oldExamesProva.getId().getEmaManSeq())
				|| CoreUtil.modificados(examesProva.getId().getEmaExaSiglaEhProva(), oldExamesProva.getId().getEmaExaSiglaEhProva())
				|| CoreUtil.modificados(examesProva.getId().getEmaManSeqEhProva(), oldExamesProva.getId().getEmaManSeqEhProva())
				|| CoreUtil.modificados(examesProva.getIndSituacao(), oldExamesProva.getIndSituacao())
				|| CoreUtil.modificados(examesProva.getServidor().getId().getMatricula(), oldExamesProva.getServidor().getId().getMatricula())
				|| CoreUtil.modificados(examesProva.getServidor().getId().getVinCodigo(), oldExamesProva.getServidor().getId().getVinCodigo())
				|| CoreUtil.modificados(examesProva.getIndConsiste(), oldExamesProva.getIndConsiste())) {

			AelExamesProvaJn jn = populaJournal(examesProva, DominioOperacoesJournal.UPD);	
			getAelExamesProvaJnDAO().persistir(jn);
			getAelExamesProvaJnDAO().flush();

		}

	}
	
	/**
	 * Cria o journal de deleção
	 * @param examesProva
	 * @throws ApplicationBusinessException 
	 */
	private void posRemoverExamesProva(AelExamesProva examesProva) throws ApplicationBusinessException {
		
		AelExamesProvaJn jn = populaJournal(examesProva, DominioOperacoesJournal.DEL);	
		getAelExamesProvaJnDAO().persistir(jn);
		getAelExamesProvaJnDAO().flush();
		
	}

	/**
	 * ORADB aelk_exp_rn.rn_expp_ver_prova 
	 * Exame não pode ser prova dele mesmo.
	 * @param examesMaterialAnalise
	 * @param examesMaterialAnaliseEhProva
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarProva(AelExamesMaterialAnalise examesMaterialAnalise, AelExamesMaterialAnalise examesMaterialAnaliseEhProva) throws ApplicationBusinessException {

		if (examesMaterialAnalise.getId().getExaSigla().equals(examesMaterialAnaliseEhProva.getId().getExaSigla()) && examesMaterialAnalise.getId().getManSeq().equals(examesMaterialAnaliseEhProva.getId().getManSeq())) {

			throw new ApplicationBusinessException(ManterProvaExameRNExceptionCode.AEL_00428);	

		}

	}

	/**
	 * Verifica se já existe item cadastrado com o mesmo Id
	 * @param examesProva
	 * @throws ApplicationBusinessException
	 */
	protected void verificarDuplicidade(AelExamesMaterialAnalise examesMaterialAnalise, AelExamesMaterialAnalise examesMaterialAnaliseEhProva) throws ApplicationBusinessException {
		
		if (getAelExamesProvaDAO().obterCountExamesProva(examesMaterialAnalise.getId().getExaSigla(), examesMaterialAnaliseEhProva.getId().getExaSigla(), examesMaterialAnalise.getId().getManSeq(), examesMaterialAnaliseEhProva.getId().getManSeq()) > 0) {

			throw new ApplicationBusinessException(ManterProvaExameRNExceptionCode.EXAME_PROVA_DUPLICADO);	

		}

	}

	/**
	 * Valida se foi alterado o servidor
	 * @param servidor
	 * @param oldServidor
	 * @throws ApplicationBusinessException 
	 */
	protected void validarServidor(RapServidores servidor, RapServidores oldServidor) throws ApplicationBusinessException {

		if (CoreUtil.modificados(servidor.getId().getMatricula(), oldServidor.getId().getMatricula()) || CoreUtil.modificados(servidor.getId().getVinCodigo(), oldServidor.getId().getVinCodigo())) {

			throw new ApplicationBusinessException(ManterProvaExameRNExceptionCode.AEL_00369);	

		}

	}

	/**
	 * Retorna o journal já populado
	 * @param operacao
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private AelExamesProvaJn populaJournal(AelExamesProva examesProva, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelExamesProvaJn jn = BaseJournalFactory.getBaseJournal(operacao, AelExamesProvaJn.class, servidorLogado.getUsuario());
		jn.setEmaExaSigla(examesProva.getId().getEmaExaSigla());
		jn.setEmaExaSiglaEhProva(examesProva.getId().getEmaExaSiglaEhProva());
		jn.setEmaManSeq(examesProva.getId().getEmaManSeq());
		jn.setEmaManSeqEhProva(examesProva.getId().getEmaManSeqEhProva());
		jn.setIndConsiste(examesProva.getIndConsiste());
		jn.setIndSituacao(examesProva.getIndSituacao());
		jn.setSerMatricula(examesProva.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(examesProva.getServidor().getId().getVinCodigo());			
		return jn;

	}

	private AelExamesProvaDAO getAelExamesProvaDAO() {
		return aelExamesProvaDAO;
	}

	protected AelExamesProvaJnDAO getAelExamesProvaJnDAO() {
		return aelExamesProvaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
