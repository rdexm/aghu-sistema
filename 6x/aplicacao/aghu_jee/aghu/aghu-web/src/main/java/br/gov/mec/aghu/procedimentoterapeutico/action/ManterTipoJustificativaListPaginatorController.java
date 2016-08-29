package br.gov.mec.aghu.procedimentoterapeutico.action;


import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptTipoJustificativa;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.JustificativaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTipoJustificativaListPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = -147305664868853332L;

	@Inject @Paginator
	private DynamicDataModel<JustificativaVO> dataModel;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoFacade;
	
	@Inject
	private ManterTipoJustificativaCRUDController manterTipoJustificativaCRUDController;
	
	private static final String PAGE_MANTER_TIPO_JUSTIFICATIVA_LIST = "procedimentoterapeutico-manterTipoJustificativaList";
	private static final String PAGE_MANTER_TIPO_JUSTIFICATIVA_CRUD = "procedimentoterapeutico-manterTipoJustificativaCRUD";
	

	private List<MptTipoSessao> mptTipoSessao;
	private DominioSituacao indSituacao;
	private MptTipoSessao selecionadoMptTipoSessao;
	private JustificativaVO justificativaVO;
	private String descricao;
	private MptTipoJustificativa indJustificativa;
	private MptJustificativa mptJustificativa;
	private JustificativaVO parametroSelecionado;
	private MptJustificativa selecionadoEdicao;
	private boolean edicaoOk = false;
	private boolean persistirOk = false;
	
	
	@PostConstruct
	public void init() {
		this.begin(conversation, true);
		this.mptJustificativa = new MptJustificativa();
		this.indSituacao = null;
		this.descricao = null;
		this.mptTipoSessao = null;
	}
	
	public void inicializar(){
		if(this.edicaoOk == true){
			apresentarMsgNegocio(Severity.INFO, "MSG_TIPO_JUSTIFICATIVA_EDITAR_SUCESSO");
			this.edicaoOk = false;
		}
		if(this.persistirOk == true){
			apresentarMsgNegocio(Severity.INFO, "MSG_TIPO_JUSTIFICATIVA_GRAVAR_SUCESSO");
			this.persistirOk = false;
		}
		
	}
	
	
	public List<MptTipoJustificativa> listarMptTipoJustificativa(String strPesquisa) {
		 return this.returnSGWithCount(this.procedimentoFacade.listarMptTipoJustificativa(strPesquisa),
				 					this.procedimentoFacade.listarMptTipoJustificativaCount(strPesquisa));
	}
	
	@Override
	public Long recuperarCount() {
		return this.procedimentoFacade.listarMptJustificativaCount(this.mptJustificativa);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JustificativaVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		List<JustificativaVO> result = this.procedimentoFacade.obterJustificativas(firstResult, maxResults, orderProperty, asc, this.mptJustificativa);
		return result;
	}
	
	
	public String gravar(){
		this.manterTipoJustificativaCRUDController.setEdicaoAtiva(false);
		return PAGE_MANTER_TIPO_JUSTIFICATIVA_CRUD;
	}
	
	public String editar(){
		this.manterTipoJustificativaCRUDController.setEdicaoAtiva(true);
		return PAGE_MANTER_TIPO_JUSTIFICATIVA_CRUD;
	}
	
	public void excluir(JustificativaVO justificativaVO){
		try{
			MptJustificativa mptJustificativa = this.procedimentoFacade.obterMptJustificativaVO(justificativaVO);
			this.procedimentoFacade.excluirJustificativa(mptJustificativa);
			apresentarMsgNegocio(Severity.INFO, "MSG_TIPO_JUSTIFICATIVA_REMOCAO_SUCESSO");
			this.pesquisar();
		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}	
	}
	
	public void pesquisar(){
		this.mptJustificativa = new MptJustificativa();
		
		if(this.descricao != null){
			this.mptJustificativa.setDescricao(this.descricao);
		}
		if(this.indSituacao != null){
			this.mptJustificativa.setIndSituacao(this.indSituacao);
		}
		if(this.indJustificativa != null){
			this.mptJustificativa.setMptTipoJustificativa(this.indJustificativa);
		}
		if(this.selecionadoMptTipoSessao != null){
			this.mptJustificativa.setMptTipoSessao(this.selecionadoMptTipoSessao);
		}
		this.parametroSelecionado = null;
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(true);
	}
	
	public List<MptTipoSessao> obterListaTipoSessao(){
		this.mptTipoSessao = this.procedimentoFacade.obterTipoSessaoDescricao();
		return this.mptTipoSessao;
	}
	
		
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.dataModel.limparPesquisa();
		this.indJustificativa = null;
		this.indSituacao = null;
		this.descricao = null;
		this.mptTipoSessao = null;
		this.selecionadoMptTipoSessao = null;
		this.mptJustificativa = null;
		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(false);
	}
	
	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	public DynamicDataModel<JustificativaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<JustificativaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<MptTipoSessao> getMptTipoSessao() {
		return mptTipoSessao;
	}

	public void setMptTipoSessao(List<MptTipoSessao> mptTipoSessao) {
		this.mptTipoSessao = mptTipoSessao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public MptJustificativa getMptJustificativa() {
		return mptJustificativa;
	}

	public void setMptJustificativa(MptJustificativa mptJustificativa) {
		this.mptJustificativa = mptJustificativa;
	}

	public JustificativaVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(JustificativaVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public MptJustificativa getSelecionadoEdicao() {
		return selecionadoEdicao;
	}

	public void setSelecionadoEdicao(MptJustificativa selecionadoEdicao) {
		this.selecionadoEdicao = selecionadoEdicao;
	}

	public boolean isEdicaoOk() {
		return edicaoOk;
	}

	public void setEdicaoOk(boolean edicaoOk) {
		this.edicaoOk = edicaoOk;
	}

	public boolean isPersistirOk() {
		return persistirOk;
	}

	public void setPersistirOk(boolean persistirOk) {
		this.persistirOk = persistirOk;
	}

	public static String getPageManterTipoJustificativaList() {
		return PAGE_MANTER_TIPO_JUSTIFICATIVA_LIST;
	}

	public static String getPageManterTipoJustificativaCrud() {
		return PAGE_MANTER_TIPO_JUSTIFICATIVA_CRUD;
	}


	public ManterTipoJustificativaCRUDController getManterTipoJustificativaCRUDController() {
		return manterTipoJustificativaCRUDController;
	}

	public void setManterTipoJustificativaCRUDController(
			ManterTipoJustificativaCRUDController manterTipoJustificativaCRUDController) {
		this.manterTipoJustificativaCRUDController = manterTipoJustificativaCRUDController;
	}

	public MptTipoSessao getSelecionadoMptTipoSessao() {
		return selecionadoMptTipoSessao;
	}

	public void setSelecionadoMptTipoSessao(MptTipoSessao selecionadoMptTipoSessao) {
		this.selecionadoMptTipoSessao = selecionadoMptTipoSessao;
	}

	public IProcedimentoTerapeuticoFacade getProcedimentoFacade() {
		return procedimentoFacade;
	}

	public void setProcedimentoFacade(
			IProcedimentoTerapeuticoFacade procedimentoFacade) {
		this.procedimentoFacade = procedimentoFacade;
	}

	public MptTipoJustificativa getIndJustificativa() {
		return indJustificativa;
	}

	public void setIndJustificativa(MptTipoJustificativa indJustificativa) {
		this.indJustificativa = indJustificativa;
	}

	public JustificativaVO getJustificativaVO() {
		return justificativaVO;
	}

	public void setJustificativaVO(JustificativaVO justificativaVO) {
		this.justificativaVO = justificativaVO;
	}

}
