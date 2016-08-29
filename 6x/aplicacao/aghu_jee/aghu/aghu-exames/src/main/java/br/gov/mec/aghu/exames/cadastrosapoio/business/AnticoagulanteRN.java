package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelAnticoagulanteDAO;
import br.gov.mec.aghu.exames.dao.AelAnticoagulanteJNDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelAnticoagulanteJn;
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
public class AnticoagulanteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AnticoagulanteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelAnticoagulanteDAO aelAnticoagulanteDAO;
	
	@Inject
	private AelAnticoagulanteJNDAO aelAnticoagulanteJNDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6203188470967562632L;

	public enum ManterAnticoagulanteRNExceptionCode implements BusinessExceptionCode {
		AEL_01196,
		AEL_00353,
		AEL_00346,
		AEL_00369,
		AEL_00344,
		AEL_00343,
		ERRO_PERSISTIR_ANTICOAGULANTE,
		ERRO_UK_DESCRICAO_ANTICOAGULANTE;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	public AelAnticoagulante inserir(AelAnticoagulante elemento)
			throws ApplicationBusinessException {

		preInsertAnticoagulante(elemento);

		getAelAnticoagulanteDAO().persistir(elemento);
		return elemento;
	}

	/**
	 * @ORADB Trigger AELT_ATC_BRI
	 * 
	 * @param AelAnticoagulante
	 *  
	 */
	protected void preInsertAnticoagulante(AelAnticoagulante aelAnticoagulante) throws ApplicationBusinessException {
		if (aelAnticoagulante.getServidor() == null || StringUtils.isBlank(aelAnticoagulante.getServidor().getMatriculaVinculo())) {
			throw new ApplicationBusinessException(ManterAnticoagulanteRNExceptionCode.AEL_00353);
		}
		
		aelAnticoagulante.setCriadoEm(new Date());	
		
		if (descricaoExiste(aelAnticoagulante)) {
			ManterAnticoagulanteRNExceptionCode.ERRO_UK_DESCRICAO_ANTICOAGULANTE.throwException();
		}
	}

	public AelAnticoagulante atualizar(AelAnticoagulante elemento) throws ApplicationBusinessException {
		AelAnticoagulante retorno = null;

		AelAnticoagulante aelAnticoagulanteOld = getAelAnticoagulanteDAO().obterAntigoPeloId(elemento.getSeq());

		//		//Regras pré-update
		preUpdateAnticoagulante(elemento, aelAnticoagulanteOld);
		aelAnticoagulanteDAO.merge(elemento);
		updateAnticoagulanteJN(elemento, aelAnticoagulanteOld);

		return retorno;
	}

	
	/**
	 * @ORADB Trigger AELT_ATC_ARU
	 */
	private void updateAnticoagulanteJN(AelAnticoagulante aelAnticoagulante, AelAnticoagulante aelAnticoagulanteOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelAnticoagulante.getCriadoEm(), aelAnticoagulanteOld.getCriadoEm())
				|| CoreUtil.modificados(aelAnticoagulante.getServidor().getMatriculaVinculo(), aelAnticoagulanteOld.getServidor().getMatriculaVinculo())
				|| CoreUtil.modificados(aelAnticoagulante.getServidor().getVinculo().getCodigo(), aelAnticoagulanteOld.getServidor().getVinculo().getCodigo())
				|| CoreUtil.modificados(aelAnticoagulante.getIndSituacao(), aelAnticoagulanteOld.getIndSituacao())
				|| CoreUtil.modificados(aelAnticoagulante.getDescricao(), aelAnticoagulanteOld.getDescricao())
				|| CoreUtil.modificados(aelAnticoagulante.getSeq(), aelAnticoagulanteOld.getSeq())) {

			AelAnticoagulanteJn AelAnticoagulanteJn =  criarAelAnticoagulanteJn(aelAnticoagulanteOld, DominioOperacoesJournal.UPD);
			getAelAnticoagulanteJNDAO().persistir(AelAnticoagulanteJn);
		}
	}
	
	/**
	 * @ORADB Procedure AELT_ATC_ARD
	 */
	private void posDeleteAnticoagulante(AelAnticoagulante aelAnticoagulante) throws ApplicationBusinessException {
		AelAnticoagulanteJn AelAnticoagulanteJn =  criarAelAnticoagulanteJn(aelAnticoagulante, DominioOperacoesJournal.DEL);
		getAelAnticoagulanteJNDAO().persistir(AelAnticoagulanteJn);
	}


	private AelAnticoagulanteJn criarAelAnticoagulanteJn(AelAnticoagulante aelAnticoagulante, DominioOperacoesJournal dominio) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelAnticoagulanteJn AelAnticoagulanteJn = BaseJournalFactory.getBaseJournal(dominio, AelAnticoagulanteJn.class, servidorLogado.getUsuario());
		
		AelAnticoagulanteJn.setSeq(aelAnticoagulante.getSeq());
		AelAnticoagulanteJn.setDescricao(aelAnticoagulante.getDescricao());
		AelAnticoagulanteJn.setIndSituacao(aelAnticoagulante.getIndSituacao().toString());
		AelAnticoagulanteJn.setCriadoEm(aelAnticoagulante.getCriadoEm());
		AelAnticoagulanteJn.setServidor(aelAnticoagulante.getServidor());
		return AelAnticoagulanteJn;
	}


	/**
	 * @ORADB Trigger AELT_UMV_BRU
	 */
	protected void preUpdateAnticoagulante(AelAnticoagulante aelAnticoagulante, AelAnticoagulante aelAnticoagulanteOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelAnticoagulante.getDescricao(), aelAnticoagulanteOld.getDescricao())) {
			throw new ApplicationBusinessException(ManterAnticoagulanteRNExceptionCode.AEL_00346);
		}
		
		if (CoreUtil.modificados(aelAnticoagulante.getServidor(), aelAnticoagulanteOld.getServidor())
				|| CoreUtil.modificados(aelAnticoagulante.getCriadoEm(), aelAnticoagulanteOld.getCriadoEm())) {
			throw new ApplicationBusinessException(ManterAnticoagulanteRNExceptionCode.AEL_00369);
		}
		
		if (getAelTipoAmostraExameDAO().pesquisarPorAnticoagulante(aelAnticoagulante.getSeq())) {
			throw new ApplicationBusinessException(ManterAnticoagulanteRNExceptionCode.AEL_01196);
		}
		
		if (descricaoExiste(aelAnticoagulante)) {
			ManterAnticoagulanteRNExceptionCode.ERRO_UK_DESCRICAO_ANTICOAGULANTE.throwException();
		}
	}

	public void remover(Integer seqAnticoagulante) throws ApplicationBusinessException {
		final AelAnticoagulante aelAnticoagulante = aelAnticoagulanteDAO.obterPorChavePrimaria(seqAnticoagulante);
		
		if(aelAnticoagulante == null){
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		preDeleteAnticoagulante(aelAnticoagulante);
		getAelAnticoagulanteDAO().remover(aelAnticoagulante);
		posDeleteAnticoagulante(aelAnticoagulante);
	}

	/**
	 * @ORADB Procedure AELT_ATC_BRD
	 */
	private void preDeleteAnticoagulante(AelAnticoagulante anticoagulante) throws ApplicationBusinessException {
		verificarParametroDelecao(anticoagulante.getCriadoEm());
	}
	
	/**
	 * Verifica se a data está entre em uma data possível de excluir (se já foi feito o backup) 
	 * @param dataCriacao Data de criação do objeto.
	 * @throws ApplicationBusinessException
	 * 	*Lança exceção AEL_00344 se não existir o parâmetro P_DIAS_PERM_DEL_AEL cadastrado no sistema
	 *  *Lança exceção AEL_00343 se não puder ser ecluído o dado (Se não foi feito backup)
	 */
	protected boolean verificarParametroDelecao(Date dataCriacao) throws ApplicationBusinessException {
		//Verifica e obtém o parâmetro
		AghParametros parametro = null;
		try {
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		} catch(ApplicationBusinessException e) {
			ManterAnticoagulanteRNExceptionCode.AEL_00344.throwException();
		}
		
		int limite = parametro.getVlrNumerico().intValue();
		
		//Verifica se o período para deletar é válido
		int diasDesdeCriacao = DateUtil.obterQtdDiasEntreDuasDatasTruncadas(dataCriacao, new Date());
		if(diasDesdeCriacao > limite) {
			ManterAnticoagulanteRNExceptionCode.AEL_00343.throwException();
		}
		//Não lançou exceção, então está dentro da data permitida para deleção.
		return true;
	}
	
	/**
	 * Indica se a descrição do anticoagulante passado por parâmetro ja existe no sistema.
	 * @param antNew
	 * @return
	 */
	
	private boolean descricaoExiste(AelAnticoagulante antNew) {
		AelAnticoagulante anticoagulante = new AelAnticoagulante();
		//Seta a descrição em um novo objeto para pesquisar
		anticoagulante.setDescricao(antNew.getDescricao());
		//Verifica se não existe um anticoagulante com esta descrição
		List<AelAnticoagulante> anticoagulantes = getAelAnticoagulanteDAO().pesquisarDescricao(antNew);
		//Se a lista de materiais de retorno tiver dados itera a lista
		if (anticoagulantes != null && !anticoagulantes.isEmpty()) {
			for(AelAnticoagulante aelAnticoagulantes : anticoagulantes) {
				//Se o anticoagulante não for igual ao atual retorna que a descrição ja existe. 
				if (!aelAnticoagulantes.equals(antNew)) {
					return true;
				}
			}
		}
		return false;
	}

	protected AelAnticoagulanteDAO getAelAnticoagulanteDAO() {
		return aelAnticoagulanteDAO;
	}
	
	protected AelAnticoagulanteJNDAO getAelAnticoagulanteJNDAO() {
		return aelAnticoagulanteJNDAO;
	}
	
	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
