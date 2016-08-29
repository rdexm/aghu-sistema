package br.gov.mec.aghu.estoque.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.FiltroMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceRelacionamentoMaterialFornecedorVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para Manter Disponibilidade de Horários
 * 
 * @author diego.pacheco
 *
 */


/**
 * @author mesias
 *
 */
public class PesquisarMaterialFonecedorPaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3293004984482182914L;
	
	private static final String LINE_SEPARATOR = System.lineSeparator();
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
 	private static final Log LOG = LogFactory.getLog(PesquisarMaterialFonecedorPaginatorController.class);

    private FiltroMaterialFornecedorVO filtroMaterialFornecedorVO;
    

	// Lista de consulta
    @Inject @Paginator
	private DynamicDataModel<SceRelacionamentoMaterialFornecedorVO> dataModel;
	
	//exibe o botao Novo
	private boolean exibirBotaoNovo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);	
		filtroMaterialFornecedorVO = new FiltroMaterialFornecedorVO();
		exibirBotaoNovo = false;
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
		//dataModel = new DynamicDataModel<SceRelacionamentoMaterialFornecedorVO>(this);
	}
	
	public void limpar() {
		this.filtroMaterialFornecedorVO = new FiltroMaterialFornecedorVO();
		this.dataModel.limparPesquisa();
		exibirBotaoNovo = false;
	}
	
	@Override
	public Long recuperarCount() {
		Long count = null;
		try {
			count = this.estoqueFacade.pesquisarListagemPaginadaMaterialFornecedorCount(this.filtroMaterialFornecedorVO);
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar count da lista: ", e);
		}
		return count;
	}

	@Override
	public List<SceRelacionamentoMaterialFornecedorVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<SceRelacionamentoMaterialFornecedorVO> retornoVO = new ArrayList<SceRelacionamentoMaterialFornecedorVO>();
		this.filtroMaterialFornecedorVO.setPaginacao(new FiltroMaterialFornecedorVO.Paginacao(firstResult, maxResult, orderProperty, asc));
		
		try {
			retornoVO = this.estoqueFacade.pesquisarListagemPaginadaMaterialFornecedor(this.filtroMaterialFornecedorVO);	
		} catch (BaseException e) {
			LOG.error("Exceção capturada ao recuperar lista paginada: ", e);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_REGISTRO_NAO_LOCALIZADO");
		}		
		return retornoVO;
	}
	
	// *** Hint ***
	
	public String getHintCriacao(SceRelacionamentoMaterialFornecedorVO item) {
		StringBuilder builder = new StringBuilder(64);
		
		builder.append("Criado por: ").append(
				transformarNuloVazio(item.getCriadoPor())).append(LINE_SEPARATOR)
		.append("Origem: ").append(
				transformarNuloVazio(item.getOrigem().getDescricao())).append(LINE_SEPARATOR)
		.append("Alterado em: ").append(
				transformarNuloVazio(item.getDataAlteracao())).append(LINE_SEPARATOR)
		.append("Alterado por: ").append(
				transformarNuloVazio(item.getAlteradoPor())).append(LINE_SEPARATOR);
		
		return builder.toString();
	}
	
	/**
	 * Transforma nulo em vazio
	 * 
	 * @param o
	 * @return
	 */
	private String transformarNuloVazio(Object o) {
		return o == null ? StringUtils.EMPTY : o.toString().trim();
	}
	
	// *** Siggestion Box ***
	
	/**
	 * Suggestion Box
	 * Método responsável por pesquisar a lista de materiais
	 * @return lista 
	 */
	public List<ScoMaterial> pesquisarMaterial(String objPesquisa) throws BaseException {	
		return  this.returnSGWithCount(this.estoqueFacade.pesquisarMaterial(objPesquisa),pesquisarMaterialCount(objPesquisa));
	}
	
	/**
	 * Suggestion Box
	 * Método responsável por recuperar a quantidade de materiais
	 * @return quantidade de bancos
	 */	
	public Long pesquisarMaterialCount(String objPesquisa) throws BaseException {
		  return this.estoqueFacade.pesquisarMaterialCount(objPesquisa);  
	}
	
	/**
	 * Suggestion Box 
	 * Método responsável por pesquisar a lista de fornecedores
	 * @return lista ScoFornecedor
	 */
	public List<ScoFornecedor> pesquisarFornecedor(String pesquisa) throws BaseException {	
		return  this.returnSGWithCount(comprasFacade.listarFornecedoresAtivos(pesquisa, 0, 100, null, true),pesquisarFornecedorCount(pesquisa));
	}
	
	/**
	 * Suggestion Box 
	 * Método responsável por recuperar a quantidade de fornecedores
	 * @return quantidade de fornecedores 
	 */	
	public Long pesquisarFornecedorCount(String strPesquisa) throws BaseException {
		  return comprasFacade.listarFornecedoresAtivosCount(strPesquisa);	  
	}

	public FiltroMaterialFornecedorVO getFiltroMaterialFornecedorVO() {
		return filtroMaterialFornecedorVO;
	}

	public void setFiltroMaterialFornecedorVO(
			FiltroMaterialFornecedorVO filtroMaterialFornecedorVO) {
		this.filtroMaterialFornecedorVO = filtroMaterialFornecedorVO;
	}

	public DynamicDataModel<SceRelacionamentoMaterialFornecedorVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<SceRelacionamentoMaterialFornecedorVO> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
	
	public ScoMaterial getScoMaterial() {
		return this.filtroMaterialFornecedorVO.getMaterial();
	}
	
	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.filtroMaterialFornecedorVO.setMaterial(scoMaterial);	
	}

	public ScoFornecedor getScoFornecedor() {
		return this.filtroMaterialFornecedorVO.getFornecedor();
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.filtroMaterialFornecedorVO.setFornecedor(scoFornecedor);
	}
	
	public String getHintCnpjCpfRazaoSocial(SceRelacionamentoMaterialFornecedorVO sceRelacionamentoMaterialFornecedorVO){
		StringBuilder hint = new StringBuilder(200);
		Long cpfCnpj = (sceRelacionamentoMaterialFornecedorVO.getCgcFornecedor() != null ? sceRelacionamentoMaterialFornecedorVO.getCgcFornecedor() : sceRelacionamentoMaterialFornecedorVO.getCpfFornecedor());
		
		if (cpfCnpj == null) {
			return StringUtils.EMPTY;
		}
		hint.append(CoreUtil.formatarCNPJ(cpfCnpj))
		.append(" - ")
		.append(sceRelacionamentoMaterialFornecedorVO.getRazaoSocialFornecedor());
		return hint.toString();
	}
	
}
