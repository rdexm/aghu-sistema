package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelExameDeptConvenioDAO;
import br.gov.mec.aghu.exames.dao.AelExameDeptConvenioJnDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDependentesJnDao;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialDependenteDAO;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.model.AelExameDeptConvenio;
import br.gov.mec.aghu.model.AelExameDeptConvenioId;
import br.gov.mec.aghu.model.AelExameDeptConvenioJn;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesDependentesId;
import br.gov.mec.aghu.model.AelExamesDependentesJn;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
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
 * @author fwinck
 *
 */
@Stateless
public class ManterExamesMaterialDependenteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterExamesMaterialDependenteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExamesMaterialDependenteDAO aelExamesMaterialDependenteDAO;
	
	@Inject
	private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;
	
	@Inject
	private AelExameDeptConvenioJnDAO aelExameDeptConvenioJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelExameDeptConvenioDAO aelExameDeptConvenioDAO;
	
	@Inject
	private AelExamesDependentesJnDao aelExamesDependentesJnDao;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7183128024449954038L;

	public enum ManterExamesMaterialDependenteRNExceptionCode implements BusinessExceptionCode {
		AEL_00834, AEL_00425, AEL_CONV_JA_INSERIDO, AEL_EXAME_DEPENDENTE_DUPLICADO;
	}

	/**
	 * Inserção em AelExamesDependentes
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void inserirAelExamesDependente(AelExamesDependentes exameDependente, List<AelExameDeptConvenio> listaConveniosPlanosDependentes) throws ApplicationBusinessException {
		
		preInserirAelExamesDependentes(exameDependente);
		verificarDuplicidade(exameDependente);
		getAelExamesMaterialDependenteDAO().persistir(exameDependente);
		getAelExamesMaterialDependenteDAO().flush();
		getAelExamesMaterialDependenteDAO().refresh(exameDependente);
		
		ajustaNovaLista(exameDependente, listaConveniosPlanosDependentes);

		for (AelExameDeptConvenio aelExameDeptConvenio : listaConveniosPlanosDependentes) {
			getAelExameDeptConvenioDAO().persistir(aelExameDeptConvenio);
			getAelExameDeptConvenioDAO().flush();
		}

		//posInserirAelExamesMaterialAnalise(aelExamesMaterialAnalise);
	}

	public AelExamesDependentes buscarAelExamesDependenteById(AelExamesDependentesId id) throws ApplicationBusinessException {
		return getAelExamesMaterialDependenteDAO().buscarAelExamesDependenteById(id);
	}
	
	public void validaConvenioPlanoJaInserido(List<AelExameDeptConvenio> listaConveniosPlanosDependentes, AelExameDeptConvenioId exaConvIdToCompare) throws ApplicationBusinessException {

		for (AelExameDeptConvenio aelExameDeptConvenio : listaConveniosPlanosDependentes) {
			AelExameDeptConvenioId exaConvId = aelExameDeptConvenio.getId();
			if(exaConvId.getCspCnvCodigo() == exaConvIdToCompare.getCspCnvCodigo()
				&& exaConvId.getCspSeq().shortValue() == exaConvIdToCompare.getCspSeq().shortValue()){
				throw new ApplicationBusinessException(ManterExamesMaterialDependenteRNExceptionCode.AEL_CONV_JA_INSERIDO);
			}
		}
	}

	public List<AelExamesDependentes> obterExamesDependentes(String sigla, Integer manSeq) {
		return getAelExamesMaterialDependenteDAO().obterExamesDependentes(sigla, manSeq);
	}

	public List<AelExameDeptConvenio> obterConvenioPlanoDependentes(AelExamesDependentesId id) {
		return getAelExameDeptConvenioDAO().obterConvenioPlanoDependentes(id);
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return getAelExameDeptConvenioDAO().pesquisarConvenioSaudePlanos(filtro);
	}
	
	public Long pesquisarConvenioSaudePlanosCount(String filtro) {
		return getAelExameDeptConvenioDAO().pesquisarConvenioSaudePlanosCount(filtro);
	}
	
	public FatConvenioSaudePlano obterPlanoPorId(Byte seqConvenioSaudePlano, Short codConvenioSaude) {
		return getAelExameDeptConvenioDAO().obterPlanoPorId(seqConvenioSaudePlano, codConvenioSaude);
	}

	/**
	 * Inserção em AelExamesDependentes
	 * @param aelExamesMaterialAnalise
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void atualizarAelExamesDependente(AelExamesDependentes exameDependente, AelExamesDependentes exaDepAux) throws ApplicationBusinessException {

		preInserirAelExamesDependentes(exameDependente);
		
		AelExamesDependentes antigo = getAelExamesMaterialDependenteDAO().obterOriginal(exameDependente);

		getAelExamesMaterialDependenteDAO().merge(exameDependente);
		getAelExamesMaterialDependenteDAO().flush();
		//getAelExamesMaterialDependenteDAO().refresh(exameDependente);

		posAtualizarAelExamesDependentes(exameDependente, antigo);
	}

	public void inserirExameDeptConvenioEmLote(AelExamesDependentes exameDependente, List<AelExameDeptConvenio> listaConveniosPlanosDependentes) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		/*Ajusta os ids lista, já que provavelmente mudou o exame dependente*/
		ajustaNovaLista(exameDependente, listaConveniosPlanosDependentes);

		/*Insere os novos*/
		for (AelExameDeptConvenio aelExameDeptConvenio : listaConveniosPlanosDependentes) {
			aelExameDeptConvenio.setRapServidor(servidorLogado);
			aelExameDeptConvenio.setCriadoEm(new Date());
			getAelExameDeptConvenioDAO().persistir(aelExameDeptConvenio);	
			getAelExameDeptConvenioDAO().flush();
		}
	}

	public void removerExameDependente(AelExamesDependentes exameDependente) throws BaseException{
		exameDependente =  getAelExamesMaterialDependenteDAO().obterPorChavePrimaria(exameDependente.getId());
		if(exameDependente.getIdAux()!=null){

			List<AelExameDeptConvenio> exaDepList = getAelExameDeptConvenioDAO().obterConvenioPlanoDependentes(exameDependente.getIdAux());
			for (AelExameDeptConvenio exaDep : exaDepList) {

				AelExameDeptConvenio exaDepAux = new AelExameDeptConvenio();
				try {
					BeanUtils.copyProperties(exaDepAux, exaDep);
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					this.logError(e.getMessage());
				}
				getAelExameDeptConvenioDAO().remover(exaDep);
				getAelExameDeptConvenioDAO().flush();
				posRemoverAelExameDeptConvenio(exaDepAux);
			}

			getAelExamesMaterialDependenteDAO().remover(exameDependente);
			getAelExamesMaterialDependenteDAO().flush();
			posRemoverAelExamesDependentes(exameDependente);
		}
	}
	
	
	public void removerListaDependentes(AelExamesDependentes exameDependente) throws BaseException{
		List<AelExameDeptConvenio> exaDepList = getAelExameDeptConvenioDAO().obterConvenioPlanoDependentes(exameDependente.getIdAux());
		for (AelExameDeptConvenio exaDep : exaDepList) {

			AelExameDeptConvenio exaDepAux = new AelExameDeptConvenio();
			try {
				BeanUtils.copyProperties(exaDepAux, exaDep);
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				this.logError(e.getMessage());
			}
			getAelExameDeptConvenioDAO().remover(exaDep);
			getAelExameDeptConvenioDAO().flush();
			posRemoverAelExameDeptConvenio(exaDepAux);
		}
	}

	private void ajustaNovaLista(AelExamesDependentes exameDependente, List<AelExameDeptConvenio> listaConveniosPlanosDependentes) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(listaConveniosPlanosDependentes!=null){
			for (AelExameDeptConvenio aelExameDeptConvenio : listaConveniosPlanosDependentes) {
				aelExameDeptConvenio.getId().setExdEmaExaSigla(exameDependente.getId().getEmaExaSigla());
				aelExameDeptConvenio.getId().setExdEmaManSeq(exameDependente.getId().getEmaManSeq());
				aelExameDeptConvenio.getId().setExdEmaExaSiglaEhDependent(exameDependente.getId().getEmaExaSiglaEhDependente());
				aelExameDeptConvenio.getId().setExdEmaManSeqEhDependente(exameDependente.getId().getEmaManSeqEhDependente());
				
				aelExameDeptConvenio.setRapServidor(servidorLogado);
				aelExameDeptConvenio.setCriadoEm(new Date());
			}
		}
	}

	/**
	 * ORADB aelk_exd_rn.rn_exdp_ver_ind_dept
	 * @param exaDependente
	 * @throws ApplicationBusinessException  
	 */
	public void preInserirAelExamesDependentes(AelExamesDependentes exaDependente) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		exaDependente.setServidor(servidorLogado);
		validaExameDependente(exaDependente);
		validaEhDependente(exaDependente);
	}

	protected void validaExameDependente(AelExamesDependentes exaDependente) throws ApplicationBusinessException {
		AelExamesDependentesId exaDepId = exaDependente.getId();
		if(exaDepId.getEmaExaSigla().equals(exaDepId.getEmaExaSiglaEhDependente()) && exaDepId.getEmaManSeq()==exaDepId.getEmaManSeqEhDependente()){
			throw new ApplicationBusinessException (ManterExamesMaterialDependenteRNExceptionCode.AEL_00425);	
		}
	}

	protected void validaEhDependente(AelExamesDependentes exaDependente) throws ApplicationBusinessException {

		VAelExameMatAnalise exaDep = getVAelExameMatAnaliseDAO().buscarVAelExameMatAnalisePelaSiglaESeq(exaDependente.getId().getEmaExaSiglaEhDependente(), exaDependente.getId().getEmaManSeqEhDependente());

		if(!exaDep.getIndDependente().equals("S")){
			throw new ApplicationBusinessException (ManterExamesMaterialDependenteRNExceptionCode.AEL_00834);			
		}
	}

	/**
	 * ORADB TRIGGERS AELT_EXD_ARU
	 * @param exaDepNovo
	 * @param exaDepAntigo
	 *  
	 */
	protected void posAtualizarAelExamesDependentes(AelExamesDependentes exaDepNovo, AelExamesDependentes exaDepAntigo) throws ApplicationBusinessException {
		
		if(	CoreUtil.modificados(exaDepNovo.getIndCancelaAutomatico(), exaDepAntigo.getIndCancelaAutomatico())
			|| CoreUtil.modificados(exaDepNovo.getIndCancLaudoUnico(), exaDepAntigo.getIndCancLaudoUnico())
			|| CoreUtil.modificados(exaDepNovo.getIndOpcional(), exaDepAntigo.getIndOpcional())
			|| CoreUtil.modificados(exaDepNovo.getIndSituacao(), exaDepAntigo.getIndSituacao())
			|| CoreUtil.modificados(exaDepNovo.getId().getEmaExaSiglaEhDependente(), exaDepAntigo.getId().getEmaExaSiglaEhDependente())
			|| CoreUtil.modificados(exaDepNovo.getId().getEmaManSeqEhDependente(), exaDepAntigo.getId().getEmaManSeqEhDependente())
		){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelExamesDependentesJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelExamesDependentesJn.class, servidorLogado.getUsuario());
			jn.setEmaExaSigla(exaDepNovo.getId().getEmaExaSigla());
			jn.setEmaManSeq(exaDepNovo.getId().getEmaManSeq());
			jn.setEmaExaSiglaEhDependente(exaDepNovo.getId().getEmaExaSiglaEhDependente());
			jn.setEmaManSeqEhDependente(exaDepNovo.getId().getEmaManSeqEhDependente());
			jn.setIndCancelaAutomatico(exaDepNovo.getIndCancelaAutomatico().toString());
			jn.setIndCancLaudoUnico(exaDepNovo.getIndCancLaudoUnico().toString());
			jn.setIndOpcional(exaDepNovo.getIndOpcional().toString());
			jn.setIndSituacao(exaDepNovo.getIndSituacao().toString());
			jn.setSerMatricula(exaDepNovo.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(exaDepNovo.getServidor().getId().getVinCodigo());
			getAelExamesDependenteJnDAO().persistir(jn);
			getAelExamesDependenteJnDAO().flush();
		}
	}
	
	/**
	 * ORADB TRIGGERS AAELT_EXD_ARD
	 * @param exaDepNovo
	 *  
	 */
	protected void posRemoverAelExamesDependentes(AelExamesDependentes exaDepNovo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelExamesDependentesJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelExamesDependentesJn.class, servidorLogado.getUsuario());
		jn.setEmaExaSigla(exaDepNovo.getId().getEmaExaSigla());
		jn.setEmaManSeq(exaDepNovo.getId().getEmaManSeq());
		jn.setEmaExaSiglaEhDependente(exaDepNovo.getId().getEmaExaSiglaEhDependente());
		jn.setEmaManSeqEhDependente(exaDepNovo.getId().getEmaManSeqEhDependente());
		jn.setIndCancelaAutomatico(exaDepNovo.getIndCancelaAutomatico().toString());
		jn.setIndCancLaudoUnico(exaDepNovo.getIndCancLaudoUnico().toString());
		jn.setIndOpcional(exaDepNovo.getIndOpcional().toString());
		jn.setIndSituacao(exaDepNovo.getIndSituacao().toString());
		jn.setSerMatricula(exaDepNovo.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(exaDepNovo.getServidor().getId().getVinCodigo());
		getAelExamesDependenteJnDAO().persistir(jn);
		getAelExamesDependenteJnDAO().flush();
	}

	/**
	 * ORADB TRIGGERS AELT_EDC_ARU 
	 * @param exaDepNovo
	 * @param exaDepAntigo
	 *  
	 */
	protected void posRemoverAelExameDeptConvenio(AelExameDeptConvenio exaDepNovo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelExameDeptConvenioJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelExameDeptConvenioJn.class, servidorLogado.getUsuario());

		jn.setExdEmaExaSigla(exaDepNovo.getId().getExdEmaExaSigla());
		jn.setExdEmaManSeq(exaDepNovo.getId().getExdEmaManSeq());
		jn.setExdEmaExaSiglaEhDependent(exaDepNovo.getId().getExdEmaExaSiglaEhDependent());
		jn.setExdEmaManSeqEhDependente(exaDepNovo.getId().getExdEmaManSeqEhDependente());
		jn.setIndSituacao(exaDepNovo.getIndSituacao().toString());
		jn.setSerMatricula(exaDepNovo.getRapServidor().getId().getMatricula());
		jn.setSerVinCodigo(exaDepNovo.getRapServidor().getId().getVinCodigo());
		jn.setCspCnvCodigo(exaDepNovo.getId().getCspCnvCodigo());
		jn.setCspSeq(exaDepNovo.getId().getCspSeq());
		getAelExameDeptConvenioJnDAO().persistir(jn);
		getAelExameDeptConvenioJnDAO().flush();
	}
	
	/**
	 * Verifica duplicidade
	 * @param examesDependentes
	 *  
	 * @throws ApplicationBusinessException 
	 */
	private void verificarDuplicidade(AelExamesDependentes examesDependentes) throws ApplicationBusinessException {
		
		if (getAelExamesMaterialDependenteDAO().buscarAelExamesDependenteById(examesDependentes.getId()) != null) {
			
			throw new ApplicationBusinessException (ManterExamesMaterialDependenteRNExceptionCode.AEL_EXAME_DEPENDENTE_DUPLICADO);			
			
		}
		
	}

	protected AelExamesMaterialDependenteDAO getAelExamesMaterialDependenteDAO() {
		return aelExamesMaterialDependenteDAO;
	}

	protected AelExamesDependentesJnDao getAelExamesDependenteJnDAO() {
		return aelExamesDependentesJnDao;
	}

	protected AelExameDeptConvenioJnDAO getAelExameDeptConvenioJnDAO() {
		return aelExameDeptConvenioJnDAO;
	}

	protected AelExameDeptConvenioDAO getAelExameDeptConvenioDAO() {
		return aelExameDeptConvenioDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO() {
		return vAelExameMatAnaliseDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}