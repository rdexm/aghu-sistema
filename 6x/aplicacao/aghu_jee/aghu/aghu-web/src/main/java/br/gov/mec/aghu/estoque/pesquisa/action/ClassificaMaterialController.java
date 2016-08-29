/**
 * 
 */
package br.gov.mec.aghu.estoque.pesquisa.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioIndProducaoInterna;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ClassificacaoMaterialVO;
import br.gov.mec.aghu.estoque.vo.MateriaisParalClassificacaoVO;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoesId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @author rpanassolo
 *
 */

public class ClassificaMaterialController extends ActionController {

	

	private static final Log LOG = LogFactory.getLog(ClassificaMaterialController.class);

	private static final long serialVersionUID = -4357350475240994890L;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	
	private ScoMaterial material;
	private ScoMaterial materialSuggestion;
	private DominioSimNao matIndEstocavel;
	private DominioIndProducaoInterna indProducaoInterna;
	private List<MateriaisParalClassificacaoVO> materiaisClassificar;
	private List<ClassificacaoMaterialVO> classificaoMaterial;
	private Boolean pesquisou = Boolean.FALSE;
	private Boolean classificar = Boolean.FALSE;
	private Long idClassifMatDelecao; 
	private ClassificacaoMaterialVO classificaoMaterialSugestion;
	private Integer codMaterialSelecionado;
	private Integer codGrupoSelecionado;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Realiza operações iniciais
	 */
	public void inicio(){
	 

		
		if(getMaterial() == null){
			setMaterial(new ScoMaterial());
		}
		iniciaValoresDefault();
		//this.//setIgnoreInitPageConfig(true);
		
	
	}
	
	private void iniciaValoresDefault() {
		this.setMatIndEstocavel(null);
		this.setIndProducaoInterna(null);
		pesquisou = Boolean.FALSE;
		classificar = Boolean.FALSE; 
		idClassifMatDelecao = null;
		classificaoMaterial = new  ArrayList<ClassificacaoMaterialVO>();
	}
	
	/**
	 * Obtém uma unidade(s) de medida ativa(s) por código ou descrição
	 * @param parametro
	 * @return
	 */
	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaAtivaPorCodigoDescricao(String parametro) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorCodigoDescricao(parametro),pesquisarUnidadeMedidaAtivaPorCodigoDescricaoCount(parametro));
	}

	public Long pesquisarUnidadeMedidaAtivaPorCodigoDescricaoCount(String parametro) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorCodigoDescricaoCount(parametro);
	}
	
	/**
	 * Obtém grupo(s) de material por código ou descrição
	 * @param parametro
	 * @return
	 */
	public List<ClassificacaoMaterialVO> pesquisarClassificacoesPorDescricao(String parametro) {
		return this.returnSGWithCount(this.estoqueFacade.pesquisarClassificacoes(getCodGrupoSelecionado(), parametro),pesquisarClassificacoesPorDescricaoCount(parametro));
	}
	
	public Long pesquisarClassificacoesPorDescricaoCount(String parametro) {
		return this.estoqueFacade.pesquisarClassificacoesCount(getCodGrupoSelecionado(), parametro);
	}
	
	
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(parametro),pesquisarGrupoMaterialCount(parametro));
	}
	
	public Long pesquisarGrupoMaterialCount(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(parametro);
	}
	
	/**
	 * Executa a pesquisa
	 */
	public void pesquisar(){
		if (this.getMatIndEstocavel()!= null) {
			getMaterial().setEstocavel(this.getMatIndEstocavel().equals(DominioSimNao.S));
		}else {
			getMaterial().setEstocavel(null);
		}
		if(indProducaoInterna!=null){
			getMaterial().setIndProducaoInterna(indProducaoInterna);
		}
		if (this.getMaterialSuggestion()!= null) {
			material.setCodigo(this.getMaterialSuggestion().getCodigo());
		}
		materiaisClassificar = this.comprasFacade.pesquisarMateriaisParaClassificar(material);
		pesquisou = Boolean.TRUE;
		classificar = Boolean.FALSE; 
		idClassifMatDelecao = null;
		classificaoMaterial = new  ArrayList<ClassificacaoMaterialVO>();
	}
	
	
	public String isAtivo(DominioSituacao situacao){
		if(situacao.isAtivo()){
			return DominioSimNao.S.getDescricao();
		}
		return DominioSimNao.N.getDescricao();
	}
	
	public String isEstocavel(Boolean estocavel){
		if(estocavel){
			return DominioSimNao.S.getDescricao();
		}
		return DominioSimNao.N.getDescricao();
	}
	
	
	public void selecionarMaterial(){
		classificar = Boolean.TRUE;
		carregarListaClassificacao(getCodMaterialSelecionado());
	}
	
/*	public void classificarMaterial(){
		classificar = Boolean.TRUE;
		carregarListaClassificacao();
	}*/

	private void carregarListaClassificacao(Integer codMaterial) {
		classificaoMaterial = this.estoqueFacade.pesquisarClassificacaoDoMaterial(codMaterial);
	}
	
	public void adicionarClassificacao(){
		Long cn5Numero = null;
		Integer matCodigo = null;
		
		if(classificaoMaterialSugestion!=null){
			cn5Numero = classificaoMaterialSugestion.getNumero();
		}
		if(getCodMaterialSelecionado()!=null){
			matCodigo = getCodMaterialSelecionado();
		}
		
		if(cn5Numero!=null && matCodigo!=null ){
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			try {
				ScoMaterial material = this.estoqueFacade.obterMaterialPorId(getCodMaterialSelecionado());
				this.estoqueFacade.adicionarMaterialClassificacao(cn5Numero, matCodigo, material, nomeMicrocomputador);
			}catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			carregarListaClassificacao(getCodMaterialSelecionado());
		}
		classificaoMaterialSugestion = null;
	}
	
	public void removerClassificacao(){
		try {
			ScoMateriaisClassificacoesId id = new ScoMateriaisClassificacoesId(); 
			id.setCn5Numero(idClassifMatDelecao);
			id.setMatCodigo(getCodMaterialSelecionado());
			this.estoqueFacade.removerMateriaisClassificacao(id);
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		carregarListaClassificacao(getCodMaterialSelecionado());
		
	}
	
	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos(){
		material = new ScoMaterial();
		setMaterialSuggestion(null);
		materiaisClassificar = new ArrayList<MateriaisParalClassificacaoVO>();
		iniciaValoresDefault();
		
	}
	
	public void limparMaterialSuggestionBox(){
		materialSuggestion = null;
	}
	
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
	public List<ScoMaterial> listarMateriais(String filter){
		Integer codGrupoMaterial = null;
		if(material.getGrupoMaterial()!=null){
			codGrupoMaterial = material.getGrupoMaterial().getCodigo();
		}
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisPorGrupo(filter, null, true,codGrupoMaterial),listarMateriaisCount(filter));
	}

	public Long listarMateriaisCount(String filter){
		Integer codGrupoMaterial = null;
		if(material.getGrupoMaterial()!=null){
			codGrupoMaterial = material.getGrupoMaterial().getCodigo();
		}
		return this.comprasFacade.listarScoMateriaisPorGrupoCount(filter, null, true,codGrupoMaterial);
	}

	public DominioSimNao getMatIndEstocavel() {
		return matIndEstocavel;
	}

	public void setMatIndEstocavel(DominioSimNao matIndEstocavel) {
		this.matIndEstocavel = matIndEstocavel;
	}

	public void setMaterialSuggestion(ScoMaterial materialSuggestion) {
		this.materialSuggestion = materialSuggestion;
	}

	public ScoMaterial getMaterialSuggestion() {
		return materialSuggestion;
	}
	
	public DominioIndProducaoInterna getIndProducaoInterna() {
		return indProducaoInterna;
	}

	public void setIndProducaoInterna(DominioIndProducaoInterna indProducaoInterna) {
		this.indProducaoInterna = indProducaoInterna;
	}

	public List<MateriaisParalClassificacaoVO> getMateriaisClassificar() {
		return materiaisClassificar;
	}

	public void setMateriaisClassificar(List<MateriaisParalClassificacaoVO>materiaisClassificar) {
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

	public Long getIdClassifMatDelecao() {
		return idClassifMatDelecao;
	}

	public void setIdClassifMatDelecao(Long idClassifMatDelecao) {
		this.idClassifMatDelecao = idClassifMatDelecao;
	}

	public ClassificacaoMaterialVO getClassificaoMaterialSugestion() {
		return classificaoMaterialSugestion;
	}

	public void setClassificaoMaterialSugestion(
			ClassificacaoMaterialVO classificaoMaterialSugestion) {
		this.classificaoMaterialSugestion = classificaoMaterialSugestion;
	}

	public Integer getCodMaterialSelecionado() {
		return codMaterialSelecionado;
	}

	public void setCodMaterialSelecionado(Integer codMaterialSelecionado) {
		this.codMaterialSelecionado = codMaterialSelecionado;
	}

	public Integer getCodGrupoSelecionado() {
		return codGrupoSelecionado;
	}

	public void setCodGrupoSelecionado(Integer codGrupoSelecionado) {
		this.codGrupoSelecionado = codGrupoSelecionado;
	}
	
}
