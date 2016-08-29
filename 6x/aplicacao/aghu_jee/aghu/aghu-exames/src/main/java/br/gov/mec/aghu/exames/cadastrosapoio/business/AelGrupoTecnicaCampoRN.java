package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoTecnicaCampoDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoTecnicaCampoJnDAO;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampo;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampoId;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelGrupoTecnicaCampoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelGrupoTecnicaCampoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrupoTecnicaCampoJnDAO aelGrupoTecnicaCampoJnDAO;
	
	@Inject
	private AelGrupoTecnicaCampoDAO aelGrupoTecnicaCampoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6247568375339828910L;

	public enum AelGrupoTecnicaCampoRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR,
		ERRO_POS_ATUALIZAR,
		ERRO_POS_REMOVER,
		;
	}
	
	
	public void persistir(AelGrupoTecnicaCampo elemento) throws BaseException {
		if (elemento.getId() == null) {
			this.inserir(elemento);
		} else {
			this.atualizar(elemento);
		}
	}
	
	
	/**
	 * Insere um registro na <br>
	 * tabela AEL_GRP_TECNICA_CAMPOS
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void inserir(AelGrupoTecnicaCampo elemento) throws BaseException {
		this.verificarRestricoes(elemento);
		this.getAelGrupoTecnicaCampoDAO().persistir(elemento);
	}
	
	/**
	 * Atualiza um registro na <br>
	 * tabela AEL_GRP_TECNICA_CAMPOS
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void atualizar(AelGrupoTecnicaCampo elemento) throws BaseException {
		AelGrupoTecnicaCampo oldElemento = getAelGrupoTecnicaCampoDAO().obterOriginal(elemento);
		
		if(oldElemento == null){
			throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
		}
		
		this.getAelGrupoTecnicaCampoDAO().merge(elemento);
		this.posAtualizar(elemento, oldElemento);
	}
	
	
	/**
	 * Remove um registro <br>
	 * na tabela AEL_GRP_TECNICA_CAMPOS
	 */
	public void remover(AelGrupoTecnicaCampoId id) throws BaseException {
		AelGrupoTecnicaCampo elemento = this.getAelGrupoTecnicaCampoDAO().obterPorChavePrimaria(id); 
		this.getAelGrupoTecnicaCampoDAO().remover(elemento);
		this.posRemover(elemento);
	}
	
	
	/**
	 * ORADB TRIGGER AELT_TCC_ARU
	 * 
	 * @param elemento
	 * @param oldElemento
	 * @throws BaseException
	 */
	protected void posAtualizar(AelGrupoTecnicaCampo elemento, AelGrupoTecnicaCampo oldElemento) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {
			if (this.verificarModificacao(elemento, oldElemento)) {
				AelGrupoTecnicaCampoJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelGrupoTecnicaCampoJn.class, servidorLogado.getUsuario());
				this.setarJournal(journal, oldElemento);
				this.getAelGrupoTecnicaCampoJnDAO().persistir(journal);
			}
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(AelGrupoTecnicaCampoRNExceptionCode.ERRO_POS_ATUALIZAR, AelGrupoRecomendacaoExame.class.getSimpleName());
		}
	}
	
	
	/**
	 * ORADB TRIGGER AELT_TCC_ARD
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void posRemover(AelGrupoTecnicaCampo elemento) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {
			AelGrupoTecnicaCampoJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelGrupoTecnicaCampoJn.class, servidorLogado.getUsuario());
			this.setarJournal(journal, elemento);
			this.getAelGrupoTecnicaCampoJnDAO().persistir(journal);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(AelGrupoTecnicaCampoRNExceptionCode.ERRO_POS_REMOVER, AelGrupoTecnicaCampo.class.getSimpleName());
		}
	}
	
	
	protected void setarJournal(AelGrupoTecnicaCampoJn journal, AelGrupoTecnicaCampo oldElemento) {
		journal.setTceGrtSeq(oldElemento.getId().getTceGrtSeq());
		journal.setTceUfeEmaExaSigla(oldElemento.getId().getTceUfeEmaExaSigla());
		journal.setTceUfeEmaManSeq(oldElemento.getId().getTceUfeEmaManSeq());
		journal.setTceUfeUnfSeq(oldElemento.getId().getTceUfeUnfSeq());
		journal.setCalSeq(oldElemento.getCampoLaudo().getSeq());
		journal.setTitulo(oldElemento.getTitulo());
	}
	
	
	protected Boolean verificarModificacao(AelGrupoTecnicaCampo elemento, AelGrupoTecnicaCampo oldElemento) {
		if (elemento == null || oldElemento == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		
		return (CoreUtil.modificados(elemento.getCampoLaudo().getSeq(), oldElemento.getCampoLaudo().getSeq()) 
				|| CoreUtil.modificados(elemento.getGrupoTecnicaUnfExames().getId().getGrtSeq(), 
						oldElemento.getGrupoTecnicaUnfExames().getId().getGrtSeq())
				|| CoreUtil.modificados(elemento.getGrupoTecnicaUnfExames().getId().getUfeEmaExaSigla(), 
						oldElemento.getGrupoTecnicaUnfExames().getId().getUfeEmaExaSigla())
				|| CoreUtil.modificados(elemento.getGrupoTecnicaUnfExames().getId().getUfeEmaManSeq(), 
						oldElemento.getGrupoTecnicaUnfExames().getId().getUfeEmaManSeq())
				|| CoreUtil.modificados(elemento.getGrupoTecnicaUnfExames().getId().getUfeUnfSeq(), 
						oldElemento.getGrupoTecnicaUnfExames().getId().getUfeUnfSeq())
				|| CoreUtil.modificados(elemento.getTitulo(), oldElemento.getTitulo()));
	}
	
	
	protected void verificarRestricoes(AelGrupoTecnicaCampo elemento) throws BaseException {
		AelGrupoTecnicaCampo elementoDuplicado = 
			this.getAelGrupoTecnicaCampoDAO().verificarUnicoAelGrupoTecnicaCampo(
					elemento.getGrupoTecnicaUnfExames().getId().getGrtSeq(), 
					elemento.getGrupoTecnicaUnfExames().getId().getUfeEmaExaSigla(), 
					elemento.getGrupoTecnicaUnfExames().getId().getUfeEmaManSeq(), 
					elemento.getGrupoTecnicaUnfExames().getId().getUfeUnfSeq(),
					elemento.getCampoLaudo().getSeq());
		
		if(elementoDuplicado != null) {
			throw new ApplicationBusinessException(AelGrupoTecnicaCampoRNExceptionCode.ERRO_INSERIR);
		}
	}
	
	
	
	
	/** GET **/
	protected AelGrupoTecnicaCampoDAO getAelGrupoTecnicaCampoDAO() {
		return aelGrupoTecnicaCampoDAO;
	}
	
	protected AelGrupoTecnicaCampoJnDAO getAelGrupoTecnicaCampoJnDAO() {
		return aelGrupoTecnicaCampoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
