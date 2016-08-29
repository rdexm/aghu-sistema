package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.VMpmMdtosDescr;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;

public class PesquisaComponentesNPTPaginatorController extends ActionController implements ActionPaginator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2790793137829143167L;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject @Paginator
	private DynamicDataModel<ComponenteNPTVO> dataModel;
	
	private static final String REDIRECIONA_HISTORICO_COMPONENTES = "pesquisaHistoricoComponentesNPT";
	
	private static final String REDIRECIONA_CRUD_COMPONENTES = "cadastroComponentesNPT";
	
	private List<DominioSimNao> situacoes = new ArrayList<DominioSimNao>();
	
	private List<DominioIdentificacaoComponenteNPT> identificacoes  = new ArrayList<DominioIdentificacaoComponenteNPT>();
	
	// FORM PESQUISA
	private VMpmMdtosDescr medicamento;
	private AfaGrupoComponenteNpt grupo;
	private String descricao;
	private DominioSimNao situacao;
	private DominioSimNao adulto;
	private DominioSimNao pediatria;
	private Short ordem;
	private DominioIdentificacaoComponenteNPT identificacao;
	
	private ComponenteNPTVO selecionado;
	
	private Boolean permissao;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
		situacoes.add(DominioSimNao.S);
		situacoes.add(DominioSimNao.N);
		
		identificacoes.addAll(Arrays.asList(DominioIdentificacaoComponenteNPT.values()));
		
		permissao = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "manterCadastrosNPT", "manter");
	}
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return farmaciaFacade.pesquisarComponentesNPTCount(medicamento,grupo,descricao,situacao,adulto,pediatria,ordem,identificacao);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ComponenteNPTVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,boolean asc) {
		List<ComponenteNPTVO> listaVo = new ArrayList<ComponenteNPTVO>();
		listaVo	= farmaciaFacade.pesquisarComponentesNPT(medicamento,grupo,descricao,situacao,adulto,pediatria,ordem,identificacao,firstResult, maxResults, orderProperty, asc);
		for (ComponenteNPTVO componenteNPTVO : listaVo) {
			Long size = 10l;
			Long size2 = 8l;
			if(StringUtils.isNotBlank(componenteNPTVO.getDescricao())){
				componenteNPTVO.setDescricaoTrunc(StringUtil.trunc(componenteNPTVO.getDescricao(), true, size));
			}
			if(componenteNPTVO.getIdentificacao() != null){
				componenteNPTVO.setIdentificacaoTrunc(StringUtil.trunc(componenteNPTVO.getIdentificacao().getDescricao(), true, size2));
			}
		}
		return listaVo;
	}
	
	public void limpar(){
		medicamento = null;
		grupo = null;
		descricao = null;
		situacao = null;
		adulto = null;
		pediatria = null;
		dataModel.setPesquisaAtiva(false);
		identificacao = null;
		ordem = null;
	}
	
	public void excluir(){
		try {
			prescricaoMedicaFacade.removerComponenteNpt(selecionado);
			this.dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUIR_COMPONENTE");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<VMpmMdtosDescr> obterSuggestionMed(String strPesquisa){
		return prescricaoMedicaFacade.obterSuggestionMedicamento(strPesquisa);
	}
	
	public List<AfaGrupoComponenteNpt> obterSuggestionGrupoComponentes(String strPesquisa){
		return prescricaoMedicaFacade.obterSuggestionGrupoComponentes(strPesquisa);
	}
	
	public String historico(){
		return REDIRECIONA_HISTORICO_COMPONENTES;
	}
	
	public String novo(){
		return REDIRECIONA_CRUD_COMPONENTES;
	}
	
	public String editar(){
		return REDIRECIONA_CRUD_COMPONENTES;
	}
	
	public DynamicDataModel<ComponenteNPTVO> getDataModel() {
		return dataModel;
	}
	
	public void setDataModel(DynamicDataModel<ComponenteNPTVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public VMpmMdtosDescr getMedicamento() {
		return medicamento;
	}
	
	public void setMedicamento(VMpmMdtosDescr medicamento) {
		this.medicamento = medicamento;
	}
	
	public AfaGrupoComponenteNpt getGrupo() {
		return grupo;
	}
	
	public void setGrupo(AfaGrupoComponenteNpt grupo) {
		this.grupo = grupo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public List<DominioSimNao> getSituacoes() {
		return situacoes;
	}
	public void setSituacoes(List<DominioSimNao> situacoes) {
		this.situacoes = situacoes;
	}
	public DominioSimNao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSimNao situacao) {
		this.situacao = situacao;
	}
	public DominioSimNao getAdulto() {
		return adulto;
	}
	public void setAdulto(DominioSimNao adulto) {
		this.adulto = adulto;
	}
	public DominioSimNao getPediatria() {
		return pediatria;
	}
	public void setPediatria(DominioSimNao pediatria) {
		this.pediatria = pediatria;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public List<DominioIdentificacaoComponenteNPT> getIdentificacoes() {
		return identificacoes;
	}
	public void setIdentificacoes(List<DominioIdentificacaoComponenteNPT> identificacoes) {
		this.identificacoes = identificacoes;
	}
	public DominioIdentificacaoComponenteNPT getIdentificacao() {
		return identificacao;
	}
	public void setIdentificacao(DominioIdentificacaoComponenteNPT identificacao) {
		this.identificacao = identificacao;
	}
	public ComponenteNPTVO getSelecionado() {
		return selecionado;
	}
	public void setSelecionado(ComponenteNPTVO selecionado) {
		this.selecionado = selecionado;
	}
	public Boolean getPermissao() {
		return permissao;
	}
	public void setPermissao(Boolean permissao) {
		this.permissao = permissao;
	}
	
}
