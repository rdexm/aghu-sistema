package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseJnDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMaterialAnaliseJn;
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
public class ManterMaterialAnaliseRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterMaterialAnaliseRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;
	
	@Inject
	private AelMaterialAnaliseJnDAO aelMaterialAnaliseJnDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7050120586853221858L;

	public enum ManterMaterialAnaliseRNExceptionCode implements BusinessExceptionCode {
		AEL_00346,
		AEL_00369,
		AEL_01197, 
		AEL_00344,
		AEL_00343,
		ERRO_UK_DESCRICAO_MATERIAL_ANALISE;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	public AelMateriaisAnalises inserir(AelMateriaisAnalises material) throws ApplicationBusinessException {
	

		//Regras pré-insert
		preInsert(material);

		//Insert
		getAelMaterialAnaliseDAO().persistir(material);

		return material;
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AELT_MAN_BRI
	 * 
	 */
	protected void preInsert(AelMateriaisAnalises material) throws ApplicationBusinessException {
		//Atribui ao material a data de criação como o dia corrente
		material.setCriadoEm(new Date());

		if (descricaoExiste(material)) {
			ManterMaterialAnaliseRNExceptionCode.ERRO_UK_DESCRICAO_MATERIAL_ANALISE.throwException();
		}
	}

	public AelMateriaisAnalises atualizar(AelMateriaisAnalises material) throws ApplicationBusinessException {
		//Recupera o objeto antigo
		AelMateriaisAnalises materialOld = getAelMaterialAnaliseDAO().obterOriginal(material.getSeq());

		//Regras pré-update
		preUpdate(materialOld, material);
		
		aelMaterialAnaliseDAO.merge(material);

		//Regras pós-update
		posUpdate(materialOld, material);

		return material;
	}

	/**
	 * @ORADB Trigger AELT_MAN_BRU
	 * 
	 */
	protected void preUpdate(AelMateriaisAnalises materialOld, AelMateriaisAnalises materialNew) throws ApplicationBusinessException {
		//Verifica se a descrição é diferente da já existente
		if(!materialNew.getDescricao().trim().equalsIgnoreCase(materialOld.getDescricao().trim())) {
			ManterMaterialAnaliseRNExceptionCode.AEL_00346.throwException();
		}

		//Verifica se a data de criação é diferente da já existente
		if(!materialNew.getCriadoEm().equals(materialOld.getCriadoEm())) {
			ManterMaterialAnaliseRNExceptionCode.AEL_00369.throwException();
		}

		//Se o material está inativo
		if(materialNew.getIndSituacao().equals(DominioSituacao.I)) {
			//Verifica se o material está associado a um exame
			if(getAelTipoAmostraExameDAO().pesquisarPorMaterialAnalise(materialNew.getSeq())) {
				ManterMaterialAnaliseRNExceptionCode.AEL_01197.throwException();
			}
		}

		if (descricaoExiste(materialNew)) {
			ManterMaterialAnaliseRNExceptionCode.ERRO_UK_DESCRICAO_MATERIAL_ANALISE.throwException();
		}
	}

	/**
	 * Indica se a descrição do material passado por parâmetro ja existe no sistema.
	 * @param materialNew
	 * @return
	 */

	private boolean descricaoExiste(AelMateriaisAnalises materialNew) {
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		//Seta a descrição em um novo objeto para pesquisar
		material.setDescricao(materialNew.getDescricao());
		//Verifica se não existe um material com esta descrição
		List<AelMateriaisAnalises> materiais = getAelMaterialAnaliseDAO().pesquisarDescricao(materialNew);
		//Se a lista de materiais de retorno tiver dados itera a lista
		if (materiais != null && !materiais.isEmpty()) {
			for(AelMateriaisAnalises aelMateriaisAnalises : materiais) {
				//Se o material não for igual ao atual retorna que a descrição ja existe. 
				if (!aelMateriaisAnalises.equals(materialNew)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AELT_MAN_ARU
	 * 
	 */
	protected void posUpdate(AelMateriaisAnalises materialOld, AelMateriaisAnalises materialNew) throws ApplicationBusinessException {
		//Verifica se algum campo foi alterado
		if(CoreUtil.modificados(materialNew.getSeq(), materialOld.getSeq())
				|| CoreUtil.modificados(materialNew.getDescricao(), materialOld.getDescricao())
				|| CoreUtil.modificados(materialNew.getIndSituacao(), materialOld.getIndSituacao())
				|| CoreUtil.modificados(materialNew.getIndColetavel(), materialOld.getIndColetavel())
				|| CoreUtil.modificados(materialNew.getIndExigeDescMatAnls(), materialOld.getIndExigeDescMatAnls())
				|| CoreUtil.modificados(materialNew.getIndUrina(), materialOld.getIndUrina())) {

			//Cria uma entrada na journal
			criarJournal(materialOld, DominioOperacoesJournal.UPD);
		}
	}

	protected void criarJournal(AelMateriaisAnalises material, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelMaterialAnaliseJn materialJournal = BaseJournalFactory.getBaseJournal(operacao, AelMaterialAnaliseJn.class, servidorLogado.getUsuario());

		materialJournal.setSeq(material.getSeq());
		materialJournal.setDescricao(material.getDescricao());
		materialJournal.setIndSituacao(material.getIndSituacao());
		materialJournal.setIndColetavel(material.getIndColetavel());
		materialJournal.setCriadoEm(material.getCriadoEm());
		materialJournal.setIndExigeDescMatAnls(material.getIndExigeDescMatAnls());
		materialJournal.setIndUrina(material.getIndUrina());

		getAelMaterialAnaliseJnDAO().persistir(materialJournal);
	}

	public void remover(Integer codigo) throws ApplicationBusinessException {
		AelMateriaisAnalises material = getAelMaterialAnaliseDAO().obterPeloId(codigo);

		if(material != null) {
			//Regras pré-delete
			preDelete(material);

			//Delete
			getAelMaterialAnaliseDAO().remover(material);
			//Regras pós-delete
			posDelete(material);
			
		} else {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
	}

	/**
	 * @ORADB Trigger AELT_MAN_BRD
	 * 
	 */
	protected void preDelete(AelMateriaisAnalises material) throws ApplicationBusinessException {
		//Verifica e obtém o parâmetro
		AghParametros parametro = null;
		try {
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		} catch(ApplicationBusinessException e) {
			ManterMaterialAnaliseRNExceptionCode.AEL_00344.throwException();
		}

		int limite = parametro.getVlrNumerico().intValue();

		//Verifica se o período para deletar é válido
		int diasDesdeCriacao = DateUtil.diffInDaysInteger(new Date(), material.getCriadoEm());
		if(diasDesdeCriacao > limite) {
			ManterMaterialAnaliseRNExceptionCode.AEL_00343.throwException();
		}
	}

	/**
	 * @ORADB Trigger AELT_MAN_ARD
	 * 
	 */
	protected void posDelete(AelMateriaisAnalises material) throws ApplicationBusinessException {
		//Cria uma entrada na journal
		criarJournal(material, DominioOperacoesJournal.DEL);
	}

	//--------------------------------------------------
	//Getters

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO() {
		return aelMaterialAnaliseDAO;
	}

	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	protected AelMaterialAnaliseJnDAO getAelMaterialAnaliseJnDAO() {
		return aelMaterialAnaliseJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
