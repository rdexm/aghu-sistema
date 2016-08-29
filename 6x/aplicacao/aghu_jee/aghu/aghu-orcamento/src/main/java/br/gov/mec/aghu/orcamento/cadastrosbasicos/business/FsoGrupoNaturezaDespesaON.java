package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoGrupoNaturezaDespesaCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoGrupoNaturezaDespesaDAO;
import br.gov.mec.aghu.orcamento.dao.FsoNaturezaDespesaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FsoGrupoNaturezaDespesaON extends BaseBusiness {
	
	@EJB
	private FsoNaturezaDespesaON fsoNaturezaDespesaON;
	
	private static final Log LOG = LogFactory.getLog(FsoGrupoNaturezaDespesaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private FsoGrupoNaturezaDespesaDAO fsoGrupoNaturezaDespesaDAO;
	
	@Inject
	private FsoNaturezaDespesaDAO fsoNaturezaDespesaDAO;
	
	private static final long serialVersionUID = 6866099887944861051L;
	
	/**
	 * Obter grupo de natureza de despesa.
	 */
	public FsoGrupoNaturezaDespesa obterGrupoNaturezaDespesa(Integer codigo) {
		return getDAO().obterPorChavePrimaria(codigo);
	}
	
	/**
	 * Pesquisa por grupos de natureza de despesa.
	 */
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(FsoGrupoNaturezaDespesaCriteriaVO criteria, int first, int max, String orderField, Boolean orderAsc) {
		return getDAO().pesquisarGruposNaturezaDespesa(criteria, first, max,orderField, orderAsc);
	}

	/**
	 * Conta grupos de natureza de despesa.
	 */
	public Long contarGruposNaturezaDespesa(
			FsoGrupoNaturezaDespesaCriteriaVO criteria) {
		return getDAO().contarGruposNaturezaDespesa(criteria);
	}
	
	/**
	 * Inclui um grupo de natureza de despesa.
	 */
	public void incluir(FsoGrupoNaturezaDespesa model) throws ApplicationBusinessException {
		assertChaveUnica(model);
		validar(model);
		getDAO().persistir(model);
	}
	
	/**
	 * Altera um grupo de natureza de despesa.
	 */
	public void alterar(FsoGrupoNaturezaDespesa model) throws ApplicationBusinessException {
		validar(model);
		desativarNaturezasDespesa(model);
		getDAO().merge(model);
	}
	
	/**
	 * Desativa naturezas por grupo quando grupo inativo.
	 */
	private void desativarNaturezasDespesa(FsoGrupoNaturezaDespesa model) {
		if (!DominioSituacao.I.equals(model.getIndSituacao())) {
			return;
		}
		
		getNaturezaDespesaON().desativarPorGrupo(model.getCodigo());
	}

	/**
	 * Valida grupo antes de salva-lo.
	 */
	private void validar(FsoGrupoNaturezaDespesa model) throws ApplicationBusinessException {
		if (model.getCodigo().toString().length() != 6) {
			throw new ApplicationBusinessException(FsoGrupoNaturezaDespesaExceptionCode.GRUPO_NATUREZA_DESPESA_CODIGO_SEIS_CARACTERES);
		}
		assertDescricaoUnica(model);
	}

	/**
	 * Garante descrição para grupo.
	 */
	private void assertDescricaoUnica(FsoGrupoNaturezaDespesa model) throws ApplicationBusinessException {
		Boolean existe = getDAO().existeDescricaoDuplicada(model);
		
		if (existe) {
			throw new ApplicationBusinessException(FsoGrupoNaturezaDespesaExceptionCode.GRUPO_NATUREZA_DESPESA_DESCRICAO_DUPLICADA_ERROR);
		}
	}

	/**
	 * Garante chave única para grupo.
	 */
	private void assertChaveUnica(FsoGrupoNaturezaDespesa model) throws ApplicationBusinessException {
		FsoGrupoNaturezaDespesaCriteriaVO criteria = new FsoGrupoNaturezaDespesaCriteriaVO();

		criteria.setCodigo(model.getCodigo());
		
		Long count = getDAO().contarGruposNaturezaDespesa(criteria);
		
		if (count > 0) {
			throw new ApplicationBusinessException(FsoGrupoNaturezaDespesaExceptionCode.GRUPO_NATUREZA_DESPESA_CHAVE_DUPLICADA_ERROR);
		}
	}

	/**
	 * Exclui um grupo de natureza de despesa.
	 */
	public void excluir(Integer codigo) throws ApplicationBusinessException {
		FsoGrupoNaturezaDespesa model = fsoGrupoNaturezaDespesaDAO.obterPorChavePrimaria(codigo);

		if (model == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}

		List<FsoNaturezaDespesa> listaNtd = fsoNaturezaDespesaDAO.listarNaturezaDespesaPorGrupoNatureza(model); 
		
		if (listaNtd != null && !listaNtd.isEmpty()) {
			StringBuilder msg = new StringBuilder();
			
			for (FsoNaturezaDespesa ntd : listaNtd) {
				msg.append(ntd.getId().getCodigo()).append(',');	
			}
			
			throw new ApplicationBusinessException( FsoGrupoNaturezaDespesaExceptionCode.GRUPO_POSSUI_NATUREZA_DESPESA_VINCULADA_ERROR, msg.toString().substring(0, msg.toString().length()-1));
		}
	
		getDAO().remover(model);
	}

	protected FsoNaturezaDespesaON getNaturezaDespesaON() {
		return fsoNaturezaDespesaON;
	}

	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(final Object strPesquisa) {
		return this.getDAO().pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(strPesquisa);
	}
	
	protected FsoGrupoNaturezaDespesaDAO getDAO() {
		return fsoGrupoNaturezaDespesaDAO;
	}
	
	protected FsoNaturezaDespesaDAO getNaturezaDespesaDAO() {
		return fsoNaturezaDespesaDAO;
	}
	
	public enum FsoGrupoNaturezaDespesaExceptionCode  implements BusinessExceptionCode {
		GRUPO_POSSUI_NATUREZA_DESPESA_VINCULADA_ERROR, 
		GRUPO_NATUREZA_DESPESA_CHAVE_DUPLICADA_ERROR, 
		GRUPO_NATUREZA_DESPESA_DESCRICAO_DUPLICADA_ERROR,
		GRUPO_NATUREZA_DESPESA_CODIGO_SEIS_CARACTERES
	}
}