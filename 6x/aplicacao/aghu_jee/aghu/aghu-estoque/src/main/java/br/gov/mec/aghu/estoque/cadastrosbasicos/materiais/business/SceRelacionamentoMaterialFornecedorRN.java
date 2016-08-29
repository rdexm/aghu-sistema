package br.gov.mec.aghu.estoque.cadastrosbasicos.materiais.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioOrigemMaterialFornecedor;
import br.gov.mec.aghu.estoque.dao.SceRelacionamentoMaterialFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceRelacionamentoMaterialFornecedorJnDAO;
import br.gov.mec.aghu.estoque.vo.FiltroMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceRelacionamentoMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceSuggestionBoxMaterialFornecedorVO;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedor;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedorJn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class SceRelacionamentoMaterialFornecedorRN extends BaseBusiness{

	private static final long serialVersionUID = -498570131302960550L;

	/**
	 * Exceptions para regra de negócio
	 * 
	 * @author julianosena
	 *
	 */
	private enum SceRelacionamentoMaterialFornecedorRNException implements BusinessExceptionCode {
		MENSAGEM_MATERIAL_ESTA_VINCULADO_AO_CODIGO
	}

	@Inject
	private SceRelacionamentoMaterialFornecedorDAO sceRelacionamentoMaterialFornecedorDAO;

	@Inject
	private SceRelacionamentoMaterialFornecedorJnDAO sceRelacionamentoMaterialFornecedorJnDAO;

	@EJB
	private IServidorLogadoFacade iServidorLogadoFacade;

	/**
	 * Pesquisa material por código e nome
	 * @return
	 */
	public List<ScoMaterial> pesquisarMaterial(Object value){
		return this.getSceRelacionamentoMaterialFornecedorDAO().pesquisarMaterial(value);
	}

	/**
	 * Pesquisa material de acordo com a consulta gerada
	 * @return
	 */
	public Long pesquisarMaterialCount(Object value){
		return this.getSceRelacionamentoMaterialFornecedorDAO().pesquisarMaterialCount(value);
	}

	/**
	 * Chama a DAO para realizar a pesquisa de registros para a listagem da página
	 * de pesquisa da estória
	 * 
	 * @param filtroMaterialFornecedorVO
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarListagemPaginadaMaterialFornecedor(FiltroMaterialFornecedorVO filtroMaterialFornecedorVO)
			throws ApplicationBusinessException{
		return this.getSceRelacionamentoMaterialFornecedorDAO().pesquisarListagemPaginadaMaterialFornecedor(filtroMaterialFornecedorVO);
	}

	/**
	 * Count da lista paginada de materiais dos fornecedores
	 * 
	 * @param filtroMaterialFornecedorVO
	 * @return
	 */
	public Long pesquisarListagemPaginadaMaterialFornecedorCount(FiltroMaterialFornecedorVO filtroMaterialFornecedorVO){
		return this.getSceRelacionamentoMaterialFornecedorDAO().pesquisarListagemPaginadaMaterialFornecedorCount(filtroMaterialFornecedorVO);
	}

	/**
	 * Realiza a pesquisa de material por fornecedor chamando o método da DAO
	 * 
	 * @param numeroFornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarMaterialFornecedor(Integer numeroFornecedor)
			throws ApplicationBusinessException {
		return this.getSceRelacionamentoMaterialFornecedorDAO().pesquisarMaterialPorFornecedor(numeroFornecedor);
	}

	/**
	 * Método de persistência de material de fornecedor que valida
	 * se já existe um material para um determinado fornecedor
	 * buscando pelo código ou descrição
	 * 
	 * @author julianosena
	 * @param sceRelacionamentoMaterialFornecedor
	 * @throws ApplicationBusinessException
	 */
	public void persistirMaterialFornecedor( SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor )
		throws ApplicationBusinessException {

		boolean exist = this.getSceRelacionamentoMaterialFornecedorDAO().materialFornecedorExists(sceRelacionamentoMaterialFornecedor);

		//Lanço exceção se existir um material com o código ou descrição recebida como parâmetro
		if( exist ) {
			String material = "";

			if( sceRelacionamentoMaterialFornecedor.getScoMaterial().getCodigo() != null){
				material = sceRelacionamentoMaterialFornecedor.getScoMaterial().getCodigo().toString();
			} else {
				material = sceRelacionamentoMaterialFornecedor.getScoMaterial().getDescricao();
			}

			throw new ApplicationBusinessException(
					SceRelacionamentoMaterialFornecedorRNException.MENSAGEM_MATERIAL_ESTA_VINCULADO_AO_CODIGO,
					Severity.WARN,
					material
			);

		} else {
			//Realizo a persistência da data de alteração se existir este relacionamento na base de dados
			if(sceRelacionamentoMaterialFornecedor.getSeq() != null){
				sceRelacionamentoMaterialFornecedor.setDataAlteracao(new Date());
				sceRelacionamentoMaterialFornecedor.setMatriculaVinCodigoAlteracao(this.getIServidorLogadoFacade().obterServidorLogado());
			} else {
				sceRelacionamentoMaterialFornecedor.setDataCriacao(new Date());
				sceRelacionamentoMaterialFornecedor.setOrigem(DominioOrigemMaterialFornecedor.C);
				sceRelacionamentoMaterialFornecedor.setMatriculaVinCodigo(this.getIServidorLogadoFacade().obterServidorLogado());
			}
			SceRelacionamentoMaterialFornecedor persistido = this.getSceRelacionamentoMaterialFornecedorDAO().merge(sceRelacionamentoMaterialFornecedor);

			//Realizo a inserção na journal
			SceRelacionamentoMaterialFornecedorJn sceRelacionamentoMaterialFornecedorJn = new SceRelacionamentoMaterialFornecedorJn();
			sceRelacionamentoMaterialFornecedorJn.setCodigoMaterialFornecedor(persistido.getCodigoMaterialFornecedor());
			sceRelacionamentoMaterialFornecedorJn.setDtAlteracao(persistido.getDataAlteracao());
			sceRelacionamentoMaterialFornecedorJn.setDtCriacao(persistido.getDataAlteracao());
			sceRelacionamentoMaterialFornecedorJn.setDescricaoMaterialFornecedor(persistido.getDescricaoMaterialFornecedor());
			sceRelacionamentoMaterialFornecedorJn.setMatriculaVinCodigo(persistido.getMatriculaVinCodigo());
			sceRelacionamentoMaterialFornecedorJn.setMatriculaVinCodigoAlteracao(persistido.getMatriculaVinCodigoAlteracao());
			sceRelacionamentoMaterialFornecedorJn.setNomeUsuario(this.getIServidorLogadoFacade().obterServidorLogadoSemCache().getPessoaFisica().getNome());
			sceRelacionamentoMaterialFornecedorJn.setOperacao(DominioOperacoesJournal.INS);
			sceRelacionamentoMaterialFornecedorJn.setOrigem(persistido.getOrigem());
			sceRelacionamentoMaterialFornecedorJn.setScoFornecedor(persistido.getScoFornecedor());
			sceRelacionamentoMaterialFornecedorJn.setScoMaterial(persistido.getScoMaterial());
			sceRelacionamentoMaterialFornecedorJn.setSituacao(persistido.getSituacao());
			sceRelacionamentoMaterialFornecedorJn.setSeq(persistido.getSeq());
			sceRelacionamentoMaterialFornecedorJn.setDtCriacao(persistido.getDataCriacao());
			sceRelacionamentoMaterialFornecedorJn.setDtAlteracao(persistido.getDataAlteracao());
			this.getSceRelacionamentoMaterialFornecedorJnDAO().persistir(sceRelacionamentoMaterialFornecedorJn);
		}
	}

	/**
	 * Obtem o material pela sua primary key para a tela de
	 * edição
	 * 
	 * @param codigoMaterialFornecedor
	 * @return
	 */
	public SceRelacionamentoMaterialFornecedor carregarMaterialFornecedor(Long codigoMaterialFornecedor){
		return this.getSceRelacionamentoMaterialFornecedorDAO().obterPorChavePrimaria(codigoMaterialFornecedor);
	}

	/**
	 * Obtém a listagem para os registros da listagem de histórico material fornecedor
	 * 
	 * @param codigoMaterialFornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedorJn> pesquisarHistoricoMaterialFornecedor(Long codigoMaterialFornecedor) 
			throws ApplicationBusinessException {
		return sceRelacionamentoMaterialFornecedorJnDAO.pesquisarHistoricoMaterialFornecedor(codigoMaterialFornecedor);
	}

	public SceRelacionamentoMaterialFornecedorDAO getSceRelacionamentoMaterialFornecedorDAO() {
		return sceRelacionamentoMaterialFornecedorDAO;
	}

	public void setSceRelacionamentoMaterialFornecedorDAO(
			SceRelacionamentoMaterialFornecedorDAO sceRelacionamentoMaterialFornecedor) {
		this.sceRelacionamentoMaterialFornecedorDAO = sceRelacionamentoMaterialFornecedor;
	}
	
	/**
	 * Consulta para obter o(s) Material(is) do Fornecedor associados a um Material x Fornecedor do Hospital.
	 * @param SceRelacionamentoMaterialFornecedor
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedor> pesquisarListaMateriaHospitalFornecedor(
			SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException{
		return this.getSceRelacionamentoMaterialFornecedorDAO().pesquisarListaMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}

	/**
	 * Pesquisar se já existe um Material do Hospital relacionado ao Material do Fornecedor.
	 * @param SceRelacionamentoMaterialFornecedor
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException{
	
		List<SceRelacionamentoMaterialFornecedor> listMaterialFornecedor = 
				this.getSceRelacionamentoMaterialFornecedorDAO().verificarMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
		
		if(listMaterialFornecedor != null && listMaterialFornecedor.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/** 
	 * Ativação do relacionamento do código do material do hospital e do fornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 */
	public void alterarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		this.getSceRelacionamentoMaterialFornecedorDAO().alterarMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	
	/**
	 * Inclusão do relacionamento do código do material do hospital e do fornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 */
	public void incluirMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		this.getSceRelacionamentoMaterialFornecedorDAO().incluirMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	/**
	 *  Pesquisa materiais por fornecedor para suggestion box
	 * @param numeroFornecedor
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceSuggestionBoxMaterialFornecedorVO> pesquisarMaterialPorFornecedor(Integer numeroFornecedor, Object parametro) throws BaseException {
		return this.getSceRelacionamentoMaterialFornecedorDAO().pesquisarMaterialPorFornecedor(numeroFornecedor, parametro);
	}
	

	@Override
	protected Log getLogger() {
		return null;
	}

	public SceRelacionamentoMaterialFornecedorJnDAO getSceRelacionamentoMaterialFornecedorJnDAO() {
		return sceRelacionamentoMaterialFornecedorJnDAO;
	}

	public IServidorLogadoFacade getIServidorLogadoFacade() {
		return iServidorLogadoFacade;
	}

}
