package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelRegiaoAnatomicaDAO;
import br.gov.mec.aghu.exames.dao.AelRegiaoAnatomicaJnDAO;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelRegiaoAnatomicaJn;
import br.gov.mec.aghu.model.AghParametros;
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
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterRegiaoAnatomicaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterRegiaoAnatomicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelRegiaoAnatomicaDAO aelRegiaoAnatomicaDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelRegiaoAnatomicaJnDAO aelRegiaoAnatomicaJnDAO;
	
	private static final long serialVersionUID = 7178927649490628227L;

	public enum ManterRegiaoAnatomicaRNExceptionCode implements BusinessExceptionCode {
		AEL_00346,
		AEL_00369,
		AEL_00344,
		AEL_00343,
		ERRO_UK_DESCRICAO_REGIAO_ANATOMICA,
		ERRO_GENERICO_REMOCAO_REGIAO_ANATOMICA;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	public AelRegiaoAnatomica inserir(AelRegiaoAnatomica regiao) throws ApplicationBusinessException {
		//Regras pré-insert
		preInsert(regiao);
		
		//Insert
		getAelRegiaoAnatomicaDAO().persistir(regiao);
		
		return regiao;
	}

	/**
	 * @ORADB Trigger AELT_RAN_BRI
	 */
	protected void preInsert(AelRegiaoAnatomica regiao) throws ApplicationBusinessException {
		//Atribui aa região a data de criação como o dia corrente
		regiao.setCriadoEm(new Date());
		
		if (descricaoExiste(regiao)) {
			ManterRegiaoAnatomicaRNExceptionCode.ERRO_UK_DESCRICAO_REGIAO_ANATOMICA.throwException();
		}
	}

	public AelRegiaoAnatomica atualizar(AelRegiaoAnatomica regiao) throws ApplicationBusinessException {
		//Recupera o objeto antigo
		AelRegiaoAnatomica regiaoOld = aelRegiaoAnatomicaDAO.obterOriginal(regiao.getSeq());
		
		//Regras pré-update
		preUpdate(regiaoOld, regiao);
		
		aelRegiaoAnatomicaDAO.merge(regiao);
		
		//Regras pós-update
		posUpdate(regiaoOld, regiao);
		
		return regiao;
	}
	
	/**
	 * @ORADB Trigger AELT_RAN_BRU
	 */
	protected void preUpdate(AelRegiaoAnatomica regiaoOld, AelRegiaoAnatomica regiaoNew) throws ApplicationBusinessException {
		//Verifica se a descrição é diferente da já existente
		if(!regiaoNew.getDescricao().trim().equalsIgnoreCase(regiaoOld.getDescricao().trim())) {
			ManterRegiaoAnatomicaRNExceptionCode.AEL_00346.throwException();
		}
		
		//Verifica se a data de criação é diferente da já existente
		if(!regiaoNew.getCriadoEm().equals(regiaoOld.getCriadoEm())) {
			ManterRegiaoAnatomicaRNExceptionCode.AEL_00369.throwException();
		}
		
		if (descricaoExiste(regiaoNew)) {
			ManterRegiaoAnatomicaRNExceptionCode.ERRO_UK_DESCRICAO_REGIAO_ANATOMICA.throwException();
		}
	}
	
	/**
	 * @ORADB Trigger AELT_RAN_ARU
	 */
	protected void posUpdate(AelRegiaoAnatomica regiaoOld, AelRegiaoAnatomica regiaoNew) throws ApplicationBusinessException {
		//Verifica se algum campo foi alterado
		if(CoreUtil.modificados(regiaoNew.getSeq(), regiaoOld.getSeq())
			|| CoreUtil.modificados(regiaoNew.getDescricao(), regiaoOld.getDescricao())
			|| CoreUtil.modificados(regiaoNew.getIndSituacao(), regiaoOld.getIndSituacao())
			|| CoreUtil.modificados(regiaoNew.getCriadoEm(), regiaoOld.getCriadoEm())) {
			
			//Cria uma entrada na journal
			criarJournal(regiaoOld, DominioOperacoesJournal.UPD);
		}
	}
	
	protected void criarJournal(AelRegiaoAnatomica regiao, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelRegiaoAnatomicaJn regiaoJournal = BaseJournalFactory.getBaseJournal(operacao, AelRegiaoAnatomicaJn.class, servidorLogado.getUsuario());
		
		regiaoJournal.setSeq(regiao.getSeq());
		regiaoJournal.setDescricao(regiao.getDescricao());
		regiaoJournal.setIndSituacao(regiao.getIndSituacao());
		regiaoJournal.setCriadoEm(regiao.getCriadoEm());
		
		getAelRegiaoAnatomicaDAOJnDAO().persistir(regiaoJournal);
	}

	public void remover(Integer seqRegiao) throws ApplicationBusinessException {
		AelRegiaoAnatomica regiao = aelRegiaoAnatomicaDAO.obterPorChavePrimaria(seqRegiao);
		
		if(regiao != null) {
			//Regras pré-delete
			preDelete(regiao);
			
			//Delete
			String descricao = regiao.getDescricao();
			try {
				aelRegiaoAnatomicaDAO.remover(regiao);
			} catch (PersistenceException e) {
				LOG.error("Exceção capturada: ", e);
				ManterRegiaoAnatomicaRNExceptionCode.ERRO_GENERICO_REMOCAO_REGIAO_ANATOMICA.throwException(descricao);
			}
			
			//Regras pós-delete
			posDelete(regiao);
			
		} else {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
	}
	
	/**
	 * @ORADB Trigger AELT_RAN_BRD
	 * 
	 */
	protected void preDelete(AelRegiaoAnatomica regiao) throws ApplicationBusinessException {
		//Verifica e obtém o parâmetro
		AghParametros parametro = null;
		try {
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		} catch(ApplicationBusinessException e) {
			ManterRegiaoAnatomicaRNExceptionCode.AEL_00344.throwException();
		}
		
		int limite = parametro.getVlrNumerico().intValue();
		
		//Verifica se o período para deletar é válido
		int diasDesdeCriacao = DateUtil.diffInDaysInteger(new Date(), regiao.getCriadoEm());
		if(diasDesdeCriacao > limite) {
			ManterRegiaoAnatomicaRNExceptionCode.AEL_00343.throwException();
		}
	}
	
	/**
	 * Indica se a descrição da região anatômica passado por parâmetro ja existe no sistema.
	 */
	
	private boolean descricaoExiste(AelRegiaoAnatomica regiaoAnatNew) {
		AelRegiaoAnatomica regiaoAnatomica = new AelRegiaoAnatomica();
		//Seta a descrição em um novo objeto para pesquisar
		regiaoAnatomica.setDescricao(regiaoAnatNew.getDescricao());
		//Verifica se não existe um material com esta descrição
		List<AelRegiaoAnatomica> regioes = getAelRegiaoAnatomicaDAO().pesquisarDescricao(regiaoAnatNew);
		//Se a lista de materiais de retorno tiver dados itera a lista
		if (regioes != null && !regioes.isEmpty()) {
			for(AelRegiaoAnatomica aelRegioes : regioes) {
				//Se a região não for igual ao atual retorna que a descrição ja existe. 
				if (!aelRegioes.equals(regiaoAnatNew)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @ORADB Trigger AELT_RAN_ARD
	 */
	protected void posDelete(AelRegiaoAnatomica regiao) throws ApplicationBusinessException {
		//Cria uma entrada na journal
		criarJournal(regiao, DominioOperacoesJournal.DEL);
	}
	
	//--------------------------------------------------
	//Getters
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelRegiaoAnatomicaDAO getAelRegiaoAnatomicaDAO() {
		return aelRegiaoAnatomicaDAO;
	}
	
	protected AelRegiaoAnatomicaJnDAO getAelRegiaoAnatomicaDAOJnDAO() {
		return aelRegiaoAnatomicaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
