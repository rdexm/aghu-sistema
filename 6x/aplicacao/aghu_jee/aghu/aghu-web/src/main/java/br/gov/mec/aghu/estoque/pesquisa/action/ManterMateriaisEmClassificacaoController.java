package br.gov.mec.aghu.estoque.pesquisa.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ClassificacaoMaterialVO;
import br.gov.mec.aghu.estoque.vo.MaterialClassificacaoVO;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoesId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @author rpanassolo
 *
 */
public class ManterMateriaisEmClassificacaoController extends ActionController{

	private static final long serialVersionUID = -5534521641224829801L;
	private static final Log LOG = LogFactory.getLog(ManterMateriaisEmClassificacaoController.class);

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	private ScoGrupoMaterial grupoMaterialSuggestion;
	private ClassificacaoMaterialVO classificaoMaterialSugestion;
	private ScoMaterial materialSuggestion;
	private ClassificacaoMaterialVO classificacaoSelecionada;
	private List<ScoMaterial> materiaisClassificar;
	private List<ClassificacaoMaterialVO> classificaoMaterial;
	private List<MaterialClassificacaoVO> materialClassificacao;
	private Boolean pesquisou = Boolean.FALSE;
	private Boolean classificar = Boolean.FALSE;
	private Integer idClassifMatDelecao; 
	private Integer codGrupoMaterial;
	private Long numeroClassificacao;
	private String voltarParaUrl;
	
	private final String PAGE_MANTER_MATERIAIS_EM_CLASSIFICACAO = "manterMateriaisEmClassificacao";
	
	/**
	 * Obtém grupo(s) de material por código ou descrição
	 * @param parametro
	 * @return
	 */
	public List<ClassificacaoMaterialVO> pesquisarClassificacoesPorDescricao(String parametro) {
		Integer codGrupo= null;
		if(getGrupoMaterialSuggestion()!=null && getGrupoMaterialSuggestion().getCodigo()!=null){
			codGrupo = getGrupoMaterialSuggestion().getCodigo();
		}
		return this.returnSGWithCount(this.estoqueFacade.pesquisarClassificacoes(codGrupo, parametro),pesquisarClassificacoesPorDescricaoCount(parametro));
	}
	
	public Long pesquisarClassificacoesPorDescricaoCount(String parametro) {
		Integer codGrupo= null;
		if(getGrupoMaterialSuggestion()!=null && getGrupoMaterialSuggestion().getCodigo()!=null){
			codGrupo = getGrupoMaterialSuggestion().getCodigo();
		}
		return this.estoqueFacade.pesquisarClassificacoesCount(codGrupo, parametro);
	}
	
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(parametro),pesquisarGrupoMaterialCount(parametro));
	}
	
	public Long pesquisarGrupoMaterialCount(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(parametro);
	}
	
	/**
	 * Realiza operações iniciais
	 */
	public void inicio(){
		
		iniciaValoresDefault();
		
		if(this.codGrupoMaterial != null && this.numeroClassificacao != null){
			ClassificacaoMaterialVO aux = new ClassificacaoMaterialVO();
			aux.setCodGrupo(this.codGrupoMaterial);
			aux.setNumero(this.numeroClassificacao);			
			this.classificaoMaterialSugestion = aux; 
			pesquisar();
		}			
	}
	
	private void iniciaValoresDefault() {
		grupoMaterialSuggestion = null;
		pesquisou = Boolean.FALSE;
		classificar = Boolean.FALSE; 
		idClassifMatDelecao = null;
		classificaoMaterial = new  ArrayList<ClassificacaoMaterialVO>();
		materialClassificacao = new ArrayList<MaterialClassificacaoVO>();
	}
	
	/**
	 * Executa a pesquisa
	 */
	public void pesquisar(){
		Integer codGrupo = null;
		classificaoMaterial = new ArrayList<ClassificacaoMaterialVO>();
		if(this.classificaoMaterialSugestion!=null){
			classificaoMaterialSugestion.setSelecionado(true);
			classificaoMaterial.add(classificaoMaterialSugestion);
			classificarMaterial(classificaoMaterialSugestion);
		}else{
			if(grupoMaterialSuggestion!=null){
				codGrupo = grupoMaterialSuggestion.getCodigo();
			}
			classificaoMaterial = this.estoqueFacade.pesquisarClassificacoes(codGrupo, null);
			classificar = Boolean.FALSE; 
			materiaisClassificar = new ArrayList<ScoMaterial>();
		}
		pesquisou = Boolean.TRUE;
		idClassifMatDelecao = null;
	}
	
	
	public void selecionarMateriaisClassificacao(){
		classificar = Boolean.TRUE;
		carregarListaMateriais(getNumeroClassificacao(), getCodGrupoMaterial());
	}
	
	private void classificarMaterial(ClassificacaoMaterialVO item){
		setNumeroClassificacao(item.getNumero());
		setCodGrupoMaterial(item.getCodGrupo());
		selecionarMateriaisClassificacao();
	}

	private void carregarListaMateriais(Long cn5, Integer codGrupo) {
		materialClassificacao = this.estoqueFacade.listarMateriasPorClassificacao(cn5, codGrupo);
	}
	
	public List<ScoMaterial> listarMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(filter, null, true),listarMateriaisCount(filter));
	}

	public Long listarMateriaisCount(String filter){
		return this.comprasFacade.listarScoMateriaisCount(filter, null, true);
	}
	
	public void adicionarClassificacao(){
		Long cn5Numero = null;
		Integer matCodigo = null;
		
		if(getNumeroClassificacao()!=null){
			cn5Numero = getNumeroClassificacao();
		}
		if(getMaterialSuggestion()!=null){
			matCodigo = getMaterialSuggestion().getCodigo();
		}
		
		if(matCodigo!=null &&  cn5Numero!=null){
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				this.estoqueFacade.adicionarMaterialClassificacao(cn5Numero, matCodigo, getMaterialSuggestion(), nomeMicrocomputador);
				carregarListaMateriais(cn5Numero,getCodGrupoMaterial());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
		}
		materialSuggestion = null;
	}
	
	public void removerClassificacao(){
		try {
			ScoMateriaisClassificacoesId id = new ScoMateriaisClassificacoesId(); 
			id.setCn5Numero(getNumeroClassificacao());
			id.setMatCodigo(idClassifMatDelecao);
			this.estoqueFacade.removerMateriaisClassificacao(id);
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		carregarListaMateriais(getNumeroClassificacao(), getCodGrupoMaterial());
	}
	
	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos(){
		setMaterialSuggestion(null);
		classificaoMaterialSugestion = null;
		materiaisClassificar = new ArrayList<ScoMaterial>();
		iniciaValoresDefault();
	}
	
	public String voltar() {
		return PAGE_MANTER_MATERIAIS_EM_CLASSIFICACAO;
	}
	
	public boolean exibirVoltar(){
		return StringUtils.isNotBlank(this.voltarParaUrl);
	}

	public void limparClassificacaoMaterialSuggestionBox(){
		classificaoMaterialSugestion = null;
	}
	
	public void setMaterialSuggestion(ScoMaterial materialSuggestion) {
		this.materialSuggestion = materialSuggestion;
	}

	public ScoMaterial getMaterialSuggestion() {
		return materialSuggestion;
	}
	
	public List<ScoMaterial> getMateriaisClassificar() {
		return materiaisClassificar;
	}

	public void setMateriaisClassificar(List<ScoMaterial> materiaisClassificar) {
		this.materiaisClassificar = materiaisClassificar;
	}

	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public Boolean getClassificar() {
		return classificar;
	}

	public void setClassificar(Boolean classificar) {
		this.classificar = classificar;
	}

	public List<ClassificacaoMaterialVO> getClassificaoMaterial() {
		return classificaoMaterial;
	}

	public void setClassificaoMaterial(
			List<ClassificacaoMaterialVO> classificaoMaterial) {
		this.classificaoMaterial = classificaoMaterial;
	}

	public Integer getIdClassifMatDelecao() {
		return idClassifMatDelecao;
	}

	public void setIdClassifMatDelecao(Integer idClassifMatDelecao) {
		this.idClassifMatDelecao = idClassifMatDelecao;
	}

	public ClassificacaoMaterialVO getClassificaoMaterialSugestion() {
		return classificaoMaterialSugestion;
	}

	public void setClassificaoMaterialSugestion(
			ClassificacaoMaterialVO classificaoMaterialSugestion) {
		this.classificaoMaterialSugestion = classificaoMaterialSugestion;
	}

	public ScoGrupoMaterial getGrupoMaterialSuggestion() {
		return grupoMaterialSuggestion;
	}

	public void setGrupoMaterialSuggestion(ScoGrupoMaterial grupoMaterialSuggestion) {
		this.grupoMaterialSuggestion = grupoMaterialSuggestion;
	}

	public ClassificacaoMaterialVO getClassificacaoSelecionada() {
		return classificacaoSelecionada;
	}

	public void setClassificacaoSelecionada(
			ClassificacaoMaterialVO classificacaoSelecionada) {
		this.classificacaoSelecionada = classificacaoSelecionada;
	}

	public List<MaterialClassificacaoVO> getMaterialClassificacao() {
		return materialClassificacao;
	}

	public void setMaterialClassificacao(
			List<MaterialClassificacaoVO> materialClassificacao) {
		this.materialClassificacao = materialClassificacao;
	}

	public Integer getCodGrupoMaterial() {
		return codGrupoMaterial;
	}

	public void setCodGrupoMaterial(Integer codGrupoMaterial) {
		this.codGrupoMaterial = codGrupoMaterial;
	}

	public Long getNumeroClassificacao() {
		return numeroClassificacao;
	}

	public void setNumeroClassificacao(Long numeroClassificacao) {
		this.numeroClassificacao = numeroClassificacao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	
}
