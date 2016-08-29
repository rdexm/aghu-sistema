package br.gov.mec.aghu.estoque.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.SceRelacionamentoMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceSuggestionBoxMaterialFornecedorVO;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedor;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedorJn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class MaterialFornecedorController extends ActionController {
	
	private static final class Pages {
		public static final String HISTORICO_MATERIAL_FORNECEDOR = "historicoMaterialFornecedor";
		public static final String PESQUISAR_MATERIAL_FORNECEDOR = "pesquisarMaterialFornecedor";
		public static final String MANTER_MATERIAL_FORNECEDOR = "manterMaterialFornecedor";
	}
	
	/**
	 * Exceptions do controller
	 */
	private enum MaterialFornecedorMessages implements BusinessExceptionCode {
		MENSAGEM_MATERIAL_FORNECEDOR_GRAVADO_SUCESSO,
		MENSAGEM_MATERIAL_FORNECEDOR_ALTERADO_COM_SUCESSO,
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9164457623155901835L;
	
	private static final String LINE_SEPARATOR = System.lineSeparator();
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	/**
	 *  Fornecedor selecionado
	 */
	ScoFornecedor scoFornecedor;
	
	/**
	 * Identifica se o material inclusao ou edição está ativo
	 */
	private boolean materialAtivo;

	/**
	 *   Grid inferior da lista de materiais
	 */
	private List<SceRelacionamentoMaterialFornecedorVO> sceRelacionamentoMaterialFornecedorVOList;
	
	/**
	 *   Material Fornecedor usado no cadastro
	 */
	private SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor;
	
	private List<SceRelacionamentoMaterialFornecedorJn> listaHistoricoMaterialFornecedor;
	
	private SceSuggestionBoxMaterialFornecedorVO sceSuggestionBoxMateriaFornecedorVO;
	
	private ScoMaterial scoMaterialHistorico;
	
	
	//Indica se a pagina está em modo de edição ou inserção
	private boolean edicao;

	private String paginaAnterior;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		novoMaterialFornecedor();
		this.sceRelacionamentoMaterialFornecedorVOList = new ArrayList<SceRelacionamentoMaterialFornecedorVO>();
		setMaterialAtivo(true);
		setEdicao(false);
	}
	
	public void limpar(){
		this.sceRelacionamentoMaterialFornecedor = new SceRelacionamentoMaterialFornecedor();
		this.sceRelacionamentoMaterialFornecedorVOList = new ArrayList<SceRelacionamentoMaterialFornecedorVO>();
		this.sceSuggestionBoxMateriaFornecedorVO =  null;
		setMaterialAtivo(true);
	}
	
	private void limparDefault(){
		this.sceRelacionamentoMaterialFornecedor.setCodigoMaterialFornecedor(null);
		this.sceRelacionamentoMaterialFornecedor.setDataAlteracao(null);
		this.sceRelacionamentoMaterialFornecedor.setDataCriacao(null);
		this.sceRelacionamentoMaterialFornecedor.setDescricaoMaterialFornecedor(null);
		this.sceRelacionamentoMaterialFornecedor.setMatriculaVinCodigo(null);
		this.sceRelacionamentoMaterialFornecedor.setOrigem(null);
		this.sceRelacionamentoMaterialFornecedor.setScoMaterial(null);
		this.sceRelacionamentoMaterialFornecedor.setSituacao(null);
		this.sceSuggestionBoxMateriaFornecedorVO = null;		
		setMaterialAtivo(true);
	}
	
	/**
	 * Limpa campos ao mudar fornecedor
	 */
	public void limparSelecaoFornecedor(){
		limparDefault();
		this.sceRelacionamentoMaterialFornecedorVOList = new ArrayList<SceRelacionamentoMaterialFornecedorVO>();
	}
	/**
	 * Limpa campos ao cancelar edição
	 */
	public void limparEdicao(){
		limparDefault();
		this.sceRelacionamentoMaterialFornecedor.setSeq(null);
	}
	// TODO Migrar
	public void cancelarEdicao(){
		this.setEdicao(false);
		limparEdicao();
		
	}
	
	/**
	 *   Metodo a ser chamado ao inserir novo relacionamento de material e fornecedor 
	 *   (o que limpa os campos do cadastro tambem)
	 */
	
	public void novoMaterialFornecedor(){
		this.sceRelacionamentoMaterialFornecedor = new SceRelacionamentoMaterialFornecedor();
	}
	
	/**
	 *  Ao editar a lista de materiais de um fornecedor, traz a lista dos materiais
	 *  relacionados ao fornecedor selecionado
	 *  
	 * @throws ApplicationBusinessException
	 */
	
	public void atualizarGridMaterial() {
		if(this.scoFornecedor != null){
			try {
				this.sceRelacionamentoMaterialFornecedorVOList = this.estoqueFacade.pesquisarMaterialFornecedor(this.scoFornecedor.getNumero());
			} catch (ApplicationBusinessException e) {
				apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			}
		} else {
			this.sceRelacionamentoMaterialFornecedorVOList = new ArrayList<>();
		}
	}
	
	/**
	 *  Adiciona o material ao pressionar o botao inserir, serve na edicao e na nova insercao
	 */
	public String gravar(){
		try{

			// Inserir fornecedor no material
			this.sceRelacionamentoMaterialFornecedor.setScoFornecedor(this.scoFornecedor);

			// Verificar se o material ja esta vinculado ao fornecedor na RN
			this.estoqueFacade.persistirMaterialFornecedor(this.sceRelacionamentoMaterialFornecedor);
			
			if(edicao){
				apresentarMsgNegocio(Severity.INFO, MaterialFornecedorMessages.MENSAGEM_MATERIAL_FORNECEDOR_ALTERADO_COM_SUCESSO.toString());
			}else{
				apresentarMsgNegocio(Severity.INFO, MaterialFornecedorMessages.MENSAGEM_MATERIAL_FORNECEDOR_GRAVADO_SUCESSO.toString());
			}
		} catch(ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage(), e.getParameters());
			return Pages.MANTER_MATERIAL_FORNECEDOR;
		}
		return voltar();
	}
	
	public String voltar(){
		setEdicao(false);
		limpar();
		return Pages.PESQUISAR_MATERIAL_FORNECEDOR;
	}

	public String voltarPaginaAnterior(){
		return this.getPaginaAnterior();
	}

	public String mostrarHistorico(){
		return Pages.HISTORICO_MATERIAL_FORNECEDOR;
	}
	
	/**
	 * Usado ao inserir a partir da tela de pesquisa
	 * passar o fornecedor selecionado na tela de pesquisa
	 * @return
	 */
	public String inserir() {
		limpar();		
		if(this.scoFornecedor != null){
			atualizarGridMaterial();
		}

		novoMaterialFornecedor();
		return Pages.MANTER_MATERIAL_FORNECEDOR;
	}
	
	
	/**
	 * Editar material selecionado na tela de pesquisa
	 * @param codigoMaterialFornecedor
	 * @return
	 */
	public String editar(Long codigoMaterialFornecedor)  throws BaseException{
		//Seta pagina modo edição
		setEdicao(true);
		
		//Obtém Relacionamento Material Fornecedor
		carregarMaterialFornecedor(codigoMaterialFornecedor);
		
		if(this.sceRelacionamentoMaterialFornecedor != null){
			List<ScoFornecedor> fornecedorList = this.pesquisarFornecedor(this.sceRelacionamentoMaterialFornecedor.getScoFornecedor().getNumero());
			if(fornecedorList != null && fornecedorList.size() > 0){
				this.scoFornecedor = fornecedorList.get(0);
			}
		}
		if(this.sceRelacionamentoMaterialFornecedor != null && this.sceRelacionamentoMaterialFornecedor.getScoMaterial() != null){
			List<SceSuggestionBoxMaterialFornecedorVO> materialList = this.pesquisarMaterialPorFornecedor(this.sceRelacionamentoMaterialFornecedor.getScoMaterial().getCodigo());
			if(materialList != null && materialList.size() > 0){
				this.sceSuggestionBoxMateriaFornecedorVO = materialList.get(0);
			}
		}
		
		atualizarGridMaterial();
		
		return Pages.MANTER_MATERIAL_FORNECEDOR;
	}
	
	/**
	 * Mostrar historico de Relacionamento Material Fornecedor
	 * @param codigoMaterialFornecedor
	 * @return
	 */
	public String abrirHistorico(Long codigoMaterialFornecedor, String paginaAnterior) throws BaseException{
		this.setPaginaAnterior(paginaAnterior);
		carregarHistoricoMaterial(codigoMaterialFornecedor);
		//Obtém Relacionamento Material Fornecedor
		carregarMaterialFornecedor(codigoMaterialFornecedor);		
		this.scoFornecedor = this.sceRelacionamentoMaterialFornecedor.getScoFornecedor();
		this.scoMaterialHistorico = this.sceRelacionamentoMaterialFornecedor.getScoMaterial();

		return Pages.HISTORICO_MATERIAL_FORNECEDOR;
	}
	
	public void carregarHistoricoMaterial(Long codigoMaterialFornecedor) throws BaseException{
		this.listaHistoricoMaterialFornecedor = this.estoqueFacade.pesquisarHistoricoMaterialFornecedor(codigoMaterialFornecedor); //estoqueBeanFacade.pesquisarHistoricoMaterialFornecedor(materialFornecedor); 
	}
	
	/**
	 *  No caso de editar um relacionamento de material fornecedor, passa o codigo de material fornecedor
	 *  para o metodo e carrega o material fornecedor para edicao
	 *  
	 * @param codigoMaterialFornecedor
	 * @throws BaseException
	 */
	public void carregarMaterialFornecedor(Long codigoMaterialFornecedor) {
		try {
			this.sceRelacionamentoMaterialFornecedor = this.estoqueFacade.carregarMaterialFornecedor(codigoMaterialFornecedor);
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
	}
	
	// **** Suggestion Boxes *****
	
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
	public List<ScoFornecedor> pesquisarFornecedor(Object pesquisa) throws BaseException {
		return comprasFacade.listarFornecedoresAtivos(pesquisa, 0, 100, null, true);
	}
	
	/**
	 * Suggestion Box 
	 * Método responsável por recuperar a quantidade de fornecedores
	 * @return quantidade de fornecedores 
	 */	
	public Long pesquisarFornecedorCount(Object strPesquisa) throws BaseException {
		  return comprasFacade.listarFornecedoresAtivosCount(strPesquisa);	  
	}
	
	/**
	 * Suggestion Box 
	 * Método responsável por pesquisar a material para suggestionBox
	 * da tela de manterMaterialFornecedor
	 * @return lista 
	 */
	public List<SceSuggestionBoxMaterialFornecedorVO> pesquisarMaterialPorFornecedor(Object parametro) throws BaseException {	
		return estoqueFacade.pesquisarMaterialPorFornecedor(this.scoFornecedor.getNumeroFornecedor(), parametro);
	}

	/**
	 * Suggestion Box 
	 * Método responsável por recuperar a quantidade
	 * @return quantidade
	 */	
	public Long pesquisarMaterialPorFornecedorCount(Object value) throws BaseException {
		Long count = (long) this.pesquisarMaterialPorFornecedor(value).size();
		return count;
	}
	
	
	// ***** Hints ******
	
	public String getHintUsuario(SceRelacionamentoMaterialFornecedorJn item) {
		StringBuilder builder = new StringBuilder(64);
		
		builder.append("Usuário: ").append(transformarNuloVazio(item.getUsuarioAlteracao())).append(LINE_SEPARATOR)
		.append("Matrícula: ").append(transformarNuloVazio(item.getSerMatriculaAlteracao())).append(LINE_SEPARATOR)
		.append("Vínculo: ").append(transformarNuloVazio(item.getSerVinCodigoAlteracao())).append(LINE_SEPARATOR);
		
		return builder.toString();
	}
	
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
	
	//***  GETTERS e SETTERS *****
	
	public List<SceRelacionamentoMaterialFornecedorVO> getSceRelacionamentoMaterialFornecedorVOList() {
		return sceRelacionamentoMaterialFornecedorVOList;
	}

	public SceRelacionamentoMaterialFornecedor getSceRelacionamentoMaterialFornecedor() {
		return sceRelacionamentoMaterialFornecedor;
	}

	public void setSceRelacionamentoMaterialFornecedor(
			SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		this.sceRelacionamentoMaterialFornecedor = sceRelacionamentoMaterialFornecedor;
	}

	public void setSceRelacionamentoMaterialFornecedorVOList(
			List<SceRelacionamentoMaterialFornecedorVO> sceRelacionamentoMaterialFornecedorVOList) {
		this.sceRelacionamentoMaterialFornecedorVOList = sceRelacionamentoMaterialFornecedorVOList;
	}

	public List<SceRelacionamentoMaterialFornecedorJn> getListaHistoricoMaterialFornecedor() {
		return listaHistoricoMaterialFornecedor;
	}

	public void setListaHistoricoMaterialFornecedor(
			List<SceRelacionamentoMaterialFornecedorJn> listaHistoricoMaterialFornecedor) {
		this.listaHistoricoMaterialFornecedor = listaHistoricoMaterialFornecedor;
	}

	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
		this.sceRelacionamentoMaterialFornecedor.setScoFornecedor(scoFornecedor);
	}

	public boolean isMaterialAtivo() {
		return materialAtivo;
	}

	public void setMaterialAtivo(boolean materialAtivo) {
		this.materialAtivo = materialAtivo;
		this.sceRelacionamentoMaterialFornecedor.setSituacao(DominioSituacao.getInstance(materialAtivo));
	}

	public SceSuggestionBoxMaterialFornecedorVO getSceSuggestionBoxMateriaFornecedorVO() {
		return sceSuggestionBoxMateriaFornecedorVO;
	}

	public void setSceSuggestionBoxMateriaFornecedorVO(
			SceSuggestionBoxMaterialFornecedorVO sceSuggestionBoxMateriaFornecedorVO) {
		this.sceSuggestionBoxMateriaFornecedorVO = sceSuggestionBoxMateriaFornecedorVO;
		
		if(sceRelacionamentoMaterialFornecedor != null){
			this.sceRelacionamentoMaterialFornecedor.setScoMaterial(null);
		}
		if(sceSuggestionBoxMateriaFornecedorVO != null){
			ScoMaterial scoMaterial = new ScoMaterial();
			scoMaterial.setCodigo(sceSuggestionBoxMateriaFornecedorVO.getCodigoMaterial());
			scoMaterial = this.comprasFacade.obterMaterialPorCodigoSituacao(scoMaterial);
			this.sceRelacionamentoMaterialFornecedor.setScoMaterial(scoMaterial);
		}
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public boolean bloquearCamposEdicao() {
		if(scoFornecedor != null){
			return false;
		}
		return true;
	}

	public ScoMaterial getScoMaterialHistorico() {
		return scoMaterialHistorico;
	}

	public void setScoMaterialHistorico(ScoMaterial scoMaterialHistorico) {
		this.scoMaterialHistorico = scoMaterialHistorico;
	}

	public String getPaginaAnterior() {
		return paginaAnterior;
	}

	public void setPaginaAnterior(String paginaAnterior) {
		this.paginaAnterior = paginaAnterior;
	}
}
