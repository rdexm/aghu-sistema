package br.gov.mec.aghu.estoque.cadastrosbasicos.materiais.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.estoque.vo.FiltroMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceRelacionamentoMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceSuggestionBoxMaterialFornecedorVO;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedor;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedorJn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class SceRelacionamentoMaterialFornecedorON extends BaseBusiness{

	@Inject
	private SceRelacionamentoMaterialFornecedorRN sceRelacionamentoMaterialFornecedorRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8498570131302960552L;
	
	private enum SceRelacionamentoMaterialFornecedorONException implements BusinessExceptionCode {
		MENSAGEM_VALOR_OBRIGATORIO_PARA_CAMPO;
	}

	/**
	 * Pesquisa material por código e nome
	 * @return
	 */
	public List<ScoMaterial> pesquisarMaterial(Object value){
		return this.getSceRelacionamentoMaterialFornecedorRN().pesquisarMaterial(value);
	}

	/**
	 * Pesquisa material de acordo com a consulta gerada
	 * @return
	 */
	public Long pesquisarMaterialCount(Object value){
		return this.getSceRelacionamentoMaterialFornecedorRN().pesquisarMaterialCount(value);
	}

	/**
	 * Pesquisa a lista paginada de materiais dos fornecedores
	 * 
	 * @param filtroMaterialFornecedorVO
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarListagemPaginadaMaterialFornecedor(
			FiltroMaterialFornecedorVO filtroMaterialFornecedorVO) throws ApplicationBusinessException{
		return this.getSceRelacionamentoMaterialFornecedorRN().pesquisarListagemPaginadaMaterialFornecedor(filtroMaterialFornecedorVO);
	}
	
	/**
	 * Count da lista paginada de materiais dos fornecedores
	 * 
	 * @param filtroMaterialFornecedorVO
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarListagemPaginadaMaterialFornecedorCount(
			FiltroMaterialFornecedorVO filtroMaterialFornecedorVO) throws ApplicationBusinessException{
		return this.getSceRelacionamentoMaterialFornecedorRN().pesquisarListagemPaginadaMaterialFornecedorCount(filtroMaterialFornecedorVO);
	}
	
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarMaterialFornecedor(Integer numeroFornecedor)
			throws ApplicationBusinessException {
		return this.getSceRelacionamentoMaterialFornecedorRN().pesquisarMaterialFornecedor(numeroFornecedor);
	}
	
	/**
	 * Pesquisa informações do material em edição
	 * 
	 * @param codigoMaterialFornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public SceRelacionamentoMaterialFornecedor carregarMaterialFornecedor(
			Long codigoMaterialFornecedor) throws ApplicationBusinessException {
		return this.getSceRelacionamentoMaterialFornecedorRN().carregarMaterialFornecedor(codigoMaterialFornecedor);
	}
	
	/**
	 * Pesquisa o histórico de materiais do fornecedor
	 * 
	 * @param codigoMaterialFornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedorJn> pesquisarHistoricoMaterialFornecedor(
			Long codigoMaterialFornecedor) throws ApplicationBusinessException {
		return this.getSceRelacionamentoMaterialFornecedorRN().pesquisarHistoricoMaterialFornecedor(codigoMaterialFornecedor);
	}

	public SceRelacionamentoMaterialFornecedorRN getSceRelacionamentoMaterialFornecedorRN() {
		return sceRelacionamentoMaterialFornecedorRN;
	}

	public void setSceRelacionamentoMaterialFornecedorRN(
			SceRelacionamentoMaterialFornecedorRN sceRelacionamentoMaterialFornecedorRN) {
		this.sceRelacionamentoMaterialFornecedorRN = sceRelacionamentoMaterialFornecedorRN;
	}
	
	/**
	 * Método responsável por persistir um material de fornecedor fazendo as validações das regras de negócio referente a tela
	 * @param sceRelacionamentoMaterialFornecedor
	 * @throws ApplicationBusinessException
	 */
	public void persistirMaterialFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException {
		String codigoMaterialFornecedor = sceRelacionamentoMaterialFornecedor.getCodigoMaterialFornecedor();
		String descricaoMaterialFornecedor = sceRelacionamentoMaterialFornecedor.getDescricaoMaterialFornecedor();
		if ((codigoMaterialFornecedor != null && !codigoMaterialFornecedor.isEmpty()) || (descricaoMaterialFornecedor != null && !descricaoMaterialFornecedor.isEmpty())) {
			sceRelacionamentoMaterialFornecedor.setCodigoMaterialFornecedor(codigoMaterialFornecedor != null ? codigoMaterialFornecedor.toUpperCase(): StringUtils.EMPTY);
			sceRelacionamentoMaterialFornecedor.setDescricaoMaterialFornecedor(descricaoMaterialFornecedor != null ? descricaoMaterialFornecedor.toUpperCase() : StringUtils.EMPTY);
			this.getSceRelacionamentoMaterialFornecedorRN().persistirMaterialFornecedor(sceRelacionamentoMaterialFornecedor);
		} else {
			String campoObrigatorio = this.getResourceBundleValue("LABEL_CODIGO_MATERIAL_FORNEC");
			throw new ApplicationBusinessException(SceRelacionamentoMaterialFornecedorONException.MENSAGEM_VALOR_OBRIGATORIO_PARA_CAMPO, Severity.WARN, campoObrigatorio);
		}	
	}	
	
	/**
	 * Consulta para obter o(s) Material(is) do Fornecedor associados a um Material x Fornecedor do Hospital.
	 * @param SceRelacionamentoMaterialFornecedor
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedor> pesquisarListaMateriaHospitalFornecedor(
			SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException{
		return this.getSceRelacionamentoMaterialFornecedorRN().pesquisarListaMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}

	
	/**
	 * Pesquisar se já existe um Material do Hospital relacionado ao Material do Fornecedor.
	 * @param SceRelacionamentoMaterialFornecedor
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException{
		return this.getSceRelacionamentoMaterialFornecedorRN().verificarMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	/** 
	 * Ativação do relacionamento do código do material do hospital e do fornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 */
	public void alterarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		 this.getSceRelacionamentoMaterialFornecedorRN().alterarMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	/**
	 * Inclusão do relacionamento do código do material do hospital e do fornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 */
	public void incluirMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		this.getSceRelacionamentoMaterialFornecedorRN().incluirMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	/**
	 *  Pesquisa materiais por fornecedor para suggestion box
	 * @param numeroFornecedor
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceSuggestionBoxMaterialFornecedorVO> pesquisarMaterialPorFornecedor(Integer numeroFornecedor, Object parametro) throws BaseException {
		return this.getSceRelacionamentoMaterialFornecedorRN().pesquisarMaterialPorFornecedor(numeroFornecedor, parametro);
	}	

	
	@Override
	protected Log getLogger() {
		return null;
	}

}
