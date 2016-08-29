package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioProgramacaoExecExames;
import br.gov.mec.aghu.exames.dao.AelExecExamesMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelExecExamesMatAnaliseJnDAO;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelExecExamesMatAnalise;
import br.gov.mec.aghu.model.AelExecExamesMatAnaliseJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author amalmeida
 * 
 */
@Stateless
public class AelExecExamesMatAnaliseRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelExecExamesMatAnaliseRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExecExamesMatAnaliseDAO aelExecExamesMatAnaliseDAO;
	
	@Inject
	private AelExecExamesMatAnaliseJnDAO aelExecExamesMatAnaliseJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5960016652904441045L;

	public enum AelExecExamesMatAnaliseRNExceptionCode implements BusinessExceptionCode {

		AEL_00578,AEL_00369,MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, MENSAGEM_ERRO_AEL_EXEC_EXAMES_MAT_ANALISE_DUPLICADO;

	}

	/**
	 * ORADB AELT_EEM_BRI (INSERT)
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	private void preInserir(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		this.validarExecExamesMatAnaliseDuplicado(execExamesMatAnalise);

		execExamesMatAnalise.setCriadoEm(new Date());//RN1
		execExamesMatAnalise.setServidor(servidorLogado);//RN2
		this.validarOrdemExame(execExamesMatAnalise);//RN3

	}

	/**
	 * ORADB aelk_eem_rn.rn_eemp_ver_ordem (AELP_ENFORCE_EEM_RULES)
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	private void validarOrdemExame(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException {

		final AelExamesMaterialAnaliseId examesMaterialAnaliseId = new AelExamesMaterialAnaliseId(execExamesMatAnalise.getId().getEmaExaSigla(), execExamesMatAnalise.getId().getEmaManSeq());

		List<AelExecExamesMatAnalise> listExecExamesMatAnalise = this.getAelExecExamesMatAnaliseDAO().pesquisarEquipamentosExecutamExamesPorExameMaterialAnalise(examesMaterialAnaliseId,
				execExamesMatAnalise.getOrdem());

		final String emaExaSigla = execExamesMatAnalise.getId().getEmaExaSigla();
		final Integer emaManSeq = execExamesMatAnalise.getId().getEmaManSeq();
		final Short equSeq = execExamesMatAnalise.getId().getEquSeq();
		final DominioProgramacaoExecExames programacao = execExamesMatAnalise.getId().getProgramacao();

		for (AelExecExamesMatAnalise item : listExecExamesMatAnalise) {

			if (emaExaSigla.equals(item.getId().getEmaExaSigla()) && emaManSeq.equals(item.getId().getEmaManSeq()) && equSeq.equals(item.getId().getEquSeq())
					&& programacao.equals(item.getId().getProgramacao())) {
				continue;
			}
			if (execExamesMatAnalise.getOrdem().equals(item.getOrdem())) {
				// AEL-00578: “Não pode repetir a mesma ordem para o mesmo exame.”
				throw new ApplicationBusinessException(AelExecExamesMatAnaliseRNExceptionCode.AEL_00578);
			}
		}
	}

	/**
	 * Inserir AelexecExamesMatAnalise
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	public void inserir(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException{
		this.preInserir(execExamesMatAnalise);		
		this.getAelExecExamesMatAnaliseDAO().merge(execExamesMatAnalise);
		this.getAelExecExamesMatAnaliseDAO().flush();

	}



	/**
	 * ORADB AELT_EEM_BRU (UPDATE) 
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	private void preAtualizar(AelExecExamesMatAnalise execExamesMatAnalise,AelExecExamesMatAnalise old) throws BaseException{


		this.validarAlteracao(execExamesMatAnalise, old);
		/**
		 * RN2: AELP_ENFORCE_EEM_RULES (Regra retirada da enforce)
		 */
		this.validarOrdemExame(execExamesMatAnalise);

	}


	/**
	 * Valida dados alterados
	 * @param aelCopiaResultados
	 * @param novoAelCopiaResultados
	 * @throws ApplicationBusinessException 
	 */
	protected void validarAlteracao(AelExecExamesMatAnalise execExamesMatAnalise, AelExecExamesMatAnalise old) throws ApplicationBusinessException {

		if (execExamesMatAnalise != null && old != null) {

			if (CoreUtil.modificados(execExamesMatAnalise.getCriadoEm(), old.getCriadoEm()) || CoreUtil.modificados(execExamesMatAnalise.getServidor().getId().getMatricula(), old.getServidor().getId().getMatricula()) || CoreUtil.modificados(execExamesMatAnalise.getServidor().getId().getVinCodigo(), old.getServidor().getId().getVinCodigo())) {

				/*Tentativa de alterar campos que não podem ser alterados.*/
				throw new ApplicationBusinessException(AelExecExamesMatAnaliseRNExceptionCode.AEL_00369);

			}

		}

	}


	/**
	 * Atuliza AelexecExamesMatAnalise
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	public void atualizar(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException{

		AelExecExamesMatAnalise old = getAelExecExamesMatAnaliseDAO().obterOriginal(execExamesMatAnalise);

		this.preAtualizar(execExamesMatAnalise,old);		
		this.getAelExecExamesMatAnaliseDAO().merge(execExamesMatAnalise);
		this.posAtualizar(execExamesMatAnalise,old);
		this.getAelExecExamesMatAnaliseDAO().flush();
	}


	/**
	 * ORADB AELT_EEM_ARU (UPDATE) 
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	private void posAtualizar(AelExecExamesMatAnalise execExamesMatAnalise,AelExecExamesMatAnalise old) throws BaseException{

		/**
		 * RN1
		 */
		if(this.verificaModificados(execExamesMatAnalise, old)){

			AelExecExamesMatAnaliseJn execExamesMatAnaliseJn =  criarAelExecExamesMatAnaliseJn(execExamesMatAnalise, DominioOperacoesJournal.UPD);

			getAelExecExamesMatAnaliseJnDAO().persistir(execExamesMatAnaliseJn);


		}

	}


	private Boolean verificaModificados(AelExecExamesMatAnalise execExamesMatAnalise,AelExecExamesMatAnalise old) {

		Boolean alterado = false;

		if(CoreUtil.modificados(execExamesMatAnalise.getAelEquipamentos(), old.getAelEquipamentos())){

			alterado = true;

		}
		if(CoreUtil.modificados(execExamesMatAnalise.getOrdem(), old.getOrdem())){

			alterado = true;

		}
		if(CoreUtil.modificados(execExamesMatAnalise.getSituacao(), old.getSituacao())){

			alterado = true;

		}

		return alterado;
	}


	private AelExecExamesMatAnaliseJn criarAelExecExamesMatAnaliseJn(AelExecExamesMatAnalise execExamesMatAnalise, DominioOperacoesJournal dominio) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelExecExamesMatAnaliseJn execExamesMatAnaliseJn = BaseJournalFactory.getBaseJournal(dominio, AelExecExamesMatAnaliseJn.class, servidorLogado.getUsuario());

		//AelExecExamesMatAnaliseJnId id = new AelExecExamesMatAnaliseJnId();
		execExamesMatAnaliseJn.setEmaExaSigla(execExamesMatAnalise.getId().getEmaExaSigla());
		execExamesMatAnaliseJn.setEmaManSeq(execExamesMatAnalise.getId().getEmaManSeq());
		execExamesMatAnaliseJn.setEquSeq(execExamesMatAnalise.getId().getEquSeq());
		execExamesMatAnaliseJn.setProgramacao(execExamesMatAnalise.getId().getProgramacao());

		//execExamesMatAnaliseJn.setId(id);
		execExamesMatAnaliseJn.setAelEquipamentos(execExamesMatAnalise.getAelEquipamentos());
		execExamesMatAnaliseJn.setCriadoEm(execExamesMatAnalise.getCriadoEm());
		execExamesMatAnaliseJn.setOrdem(execExamesMatAnalise.getOrdem());
		execExamesMatAnaliseJn.setServidor(execExamesMatAnalise.getServidor());
		execExamesMatAnaliseJn.setSituacao(execExamesMatAnalise.getSituacao());


		return execExamesMatAnaliseJn;
	}

	/**
	 * 
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	private void preRemover(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException{


		if(this.getAelExecExamesMatAnaliseDAO().pesquisarDependenciaAelExameEquipamento(execExamesMatAnalise.getId())){

			throw new ApplicationBusinessException(AelExecExamesMatAnaliseRNExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS,  this.getResourceBundleValue("LABEL_AEL_EXEC_EXAMES_MAT_ANALISE"));


		}


	}

	/**
	 * ORADB AELT_EEM_ARD (DELETE)
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	private void posRemover(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException{

		AelExecExamesMatAnaliseJn execExamesMatAnaliseJn =  criarAelExecExamesMatAnaliseJn(execExamesMatAnalise, DominioOperacoesJournal.DEL);

		getAelExecExamesMatAnaliseJnDAO().persistir(execExamesMatAnaliseJn);

	}

	/**
	 * Atualiza AelExecExamesMatAnalise
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	public void remover(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException{
		execExamesMatAnalise = this.getAelExecExamesMatAnaliseDAO().obterPorChavePrimaria(execExamesMatAnalise.getId());
		this.preRemover(execExamesMatAnalise);		
		this.getAelExecExamesMatAnaliseDAO().remover(execExamesMatAnalise);
		this.posRemover(execExamesMatAnalise);		
		this.getAelExecExamesMatAnaliseDAO().flush();
	}
	
	/**
	 * Valida de existe equipamento duplicado para o mesmo exame
	 * @param execExamesMatAnalise
	 * @throws BaseException
	 */
	private void validarExecExamesMatAnaliseDuplicado(AelExecExamesMatAnalise execExamesMatAnalise) throws BaseException {
		
		if (getAelExecExamesMatAnaliseDAO().verificaAelExecExamesMatAnaliseCadastrado(execExamesMatAnalise.getId())) {
			
			throw new ApplicationBusinessException(AelExecExamesMatAnaliseRNExceptionCode.MENSAGEM_ERRO_AEL_EXEC_EXAMES_MAT_ANALISE_DUPLICADO);
			
		}
		
	}

	/**
	 * Getters para RNs e DAOs
	 */
	protected AelExecExamesMatAnaliseDAO getAelExecExamesMatAnaliseDAO() {
		return aelExecExamesMatAnaliseDAO;
	}

	protected AelExecExamesMatAnaliseJnDAO getAelExecExamesMatAnaliseJnDAO() {
		return aelExecExamesMatAnaliseJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}