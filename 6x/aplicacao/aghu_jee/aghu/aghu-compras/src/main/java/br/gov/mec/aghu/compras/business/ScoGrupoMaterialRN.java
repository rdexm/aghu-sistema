package br.gov.mec.aghu.compras.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv1DAO;
import br.gov.mec.aghu.compras.dao.ScoGrupoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoTributoGrpMatServicoDAO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.model.ScoClassifMatNiv1Id;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * RN de {@link ScoGrupoMaterial}
 * 
 * @author luismoura
 * 
 */
@Stateless
public class ScoGrupoMaterialRN extends BaseBusiness {
	private static final long serialVersionUID = 4855353816484169453L;
	private static final Log LOG = LogFactory.getLog(ScoGrupoMaterialRN.class);
	
	public enum ScoGrupoMaterialRNExceptionCode implements BusinessExceptionCode {
		M1_GRUPO_MATERIAL, //
		M2_GRUPO_MATERIAL, //
		M3_GRUPO_MATERIAL, //
		M4_GRUPO_MATERIAL, //
		M5_GRUPO_MATERIAL, //
		M6_GRUPO_MATERIAL, //
		M7_GRUPO_MATERIAL, //
		M8_GRUPO_MATERIAL, //
		M9_GRUPO_MATERIAL, //
		M10_GRUPO_MATERIAL, //
		;
	}

	@Inject
	private ScoGrupoMaterialDAO scoGrupoMaterialDAO;
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	
	@Inject
	private ScoTributoGrpMatServicoDAO scoTributoGrpMatServicoDAO;
	
	@Inject
	private ScoClassifMatNiv1DAO scoClassifMatNiv1DAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * RN02 de #31584
	 * 
	 * @param scoGrupoMaterial
	 * @throws ApplicationBusinessException
	 */
	public void excluirScoGrupoMaterial(Integer gmtCodigo) throws ApplicationBusinessException {

		ScoGrupoMaterialDAO scoGrupoMaterialDAO = getScoGrupoMaterialDAO();
		ScoGrupoMaterial scoGrupoMaterial = scoGrupoMaterialDAO.obterPorChavePrimaria(gmtCodigo);
		if (scoGrupoMaterial != null) {

			// Executar consulta C7,
			boolean existsReq = getEstoqueFacade().verificarExistenciaSceReqMateriaisPorScoGrupoMaterial(scoGrupoMaterial.getCodigo());
			// se retornar registro apresentar exceção com mensagem M5.
			if (existsReq) {
				throw new ApplicationBusinessException(ScoGrupoMaterialRNExceptionCode.M5_GRUPO_MATERIAL);
			}

			// Executar consulta C8,
			boolean existsMat = getScoMaterialDAO().verificarExistenciaScoMaterialPorScoGrupoMaterial(scoGrupoMaterial.getCodigo());
			// se retornar registro apresentar exceção com mensagem M6.
			if (existsMat) {
				throw new ApplicationBusinessException(ScoGrupoMaterialRNExceptionCode.M6_GRUPO_MATERIAL);
			}

			// Executar consulta C9,
			boolean existsTrib = getScoTributoGrpMatServicoDAO().verificarExistenciaScoTributoGrpMatServicoPorScoGrupoMaterial(scoGrupoMaterial.getCodigo());
			// se retornar registro apresentar exceção com mensagem M7.
			if (existsTrib) {
				throw new ApplicationBusinessException(ScoGrupoMaterialRNExceptionCode.M7_GRUPO_MATERIAL);
			}

			scoGrupoMaterialDAO.remover(scoGrupoMaterial);
		}
	}

	/**
	 * RN01 de #31584
	 * 
	 * @param scoGrupoMaterial
	 * @throws ApplicationBusinessException
	 * @throws MECModelException
	 */
	public void persistirScoGrupoMaterial(ScoGrupoMaterial scoGrupoMaterial) throws ApplicationBusinessException {
		if (scoGrupoMaterial.getCodigo() != null) {
			getScoGrupoMaterialDAO().atualizar(scoGrupoMaterial);
		} else {
			scoGrupoMaterial.setGeraMvtoCondVlr(Boolean.FALSE);
			getScoGrupoMaterialDAO().persistir(scoGrupoMaterial);
			this.posInsert(scoGrupoMaterial);
		}
	}

	/**
	 * I2 de #31584
	 * 
	 * ORADB: SCOT_GMT_ASI
	 * 
	 * @param scoGrupoMaterial
	 */
	private void posInsert(ScoGrupoMaterial scoGrupoMaterial) {
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv1Id scoClassifMatNiv1Id = new ScoClassifMatNiv1Id();
		scoClassifMatNiv1Id.setGmtCodigo(scoGrupoMaterial.getCodigo());
		ScoClassifMatNiv1 scoClassifMatNiv1 = new ScoClassifMatNiv1();
		scoClassifMatNiv1.setId(scoClassifMatNiv1Id);
		scoClassifMatNiv1.setDescricao(null);
		scoClassifMatNiv1.setSerMatricula(servidorLogado.getId().getMatricula());
		scoClassifMatNiv1.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		scoClassifMatNiv1.setSerMatriculaAlterado(null);
		scoClassifMatNiv1.setSerVinCodigoAlterado(null);
		scoClassifMatNiv1.setCriadoEm(new Date());
		getScoClassifMatNiv1DAO().persistir(scoClassifMatNiv1);
	}

	// ------------------------------ DAO
	protected ScoGrupoMaterialDAO getScoGrupoMaterialDAO() {
		return scoGrupoMaterialDAO;
	}

	protected ScoMaterialDAO getScoMaterialDAO() {
		return scoMaterialDAO;
	}

	protected ScoTributoGrpMatServicoDAO getScoTributoGrpMatServicoDAO() {
		return scoTributoGrpMatServicoDAO;
	}

	protected ScoClassifMatNiv1DAO getScoClassifMatNiv1DAO() {
		return scoClassifMatNiv1DAO;
	}

	// ------------------------------ FACADE
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}
