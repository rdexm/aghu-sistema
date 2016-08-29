package br.gov.mec.aghu.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghEquipesDAO;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EquipeCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EquipeCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IAghuFacade iAghuFacade;

	@Inject
	private AghEquipesDAO aghEquipesDAO;

	@EJB
	private AghProfissionaisEquipeON aghProfissionaisEquipeON;

	private static final long serialVersionUID = 6164134786381656768L;

	private enum EquipeCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_EQUIPE, NOME_EQUIPE_OBRIGATORIO, SITUACAO_EQUIPE_OBRIGATORIO, PLACAR_RISCO_NEONATAL_EQUIPE_OBRIGATORIO, RESPONSAVEL_EQUIPE_OBRIGATORIO
	}

	public void persistirEquipe(AghEquipes equipe, List<RapServidoresVO> profissionaisEquipe) throws ApplicationBusinessException {
		validarDadosEquipe(equipe);

		if(equipe.getSeq() != null){
			equipe = this.aghEquipesDAO.merge(equipe);
			
		} else {
			 this.aghEquipesDAO.persistir(equipe);
		}

		getAghProfissionaisEquipeON().removerAghProfissionaisEquipePorAGHEquipe(equipe.getSeq());
		getAghProfissionaisEquipeON().inserirAghProfissionaisEquipePorRapServidoresVO(profissionaisEquipe, equipe);
	}

	public void removerEquipe(AghEquipes equipe) {
		getAghProfissionaisEquipeON().removerAghProfissionaisEquipePorAGHEquipe(equipe.getSeq());
		this.aghEquipesDAO.remover(aghEquipesDAO.obterPorChavePrimaria(equipe.getSeq()));
	}

	/**
	 * Método responsável por validar campos.
	 * 
	 * @param acomodacao
	 */
	private void validarDadosEquipe(AghEquipes equipe)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(equipe.getNome())) {
			throw new ApplicationBusinessException(
					EquipeCRUDExceptionCode.NOME_EQUIPE_OBRIGATORIO);
		}

		if (equipe.getIndSituacao() == null) {
			throw new ApplicationBusinessException(
					EquipeCRUDExceptionCode.SITUACAO_EQUIPE_OBRIGATORIO);
		}

		if (equipe.getIndPlacarCo() == null) {
			throw new ApplicationBusinessException(
					EquipeCRUDExceptionCode.PLACAR_RISCO_NEONATAL_EQUIPE_OBRIGATORIO);
		}

		if (equipe.getProfissionalResponsavel() == null) {
			throw new ApplicationBusinessException(
					EquipeCRUDExceptionCode.RESPONSAVEL_EQUIPE_OBRIGATORIO);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	public AghProfissionaisEquipeON getAghProfissionaisEquipeON() {
		return aghProfissionaisEquipeON;
	} 
}
