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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class ManterTipoJustificativaCRUDController extends ActionController {

	
	private static final long serialVersionUID = -4813909530328283367L;

	private static final String PAGE_MANTER_TIPO_JUSTIFICATIVA_LIST = "procedimentoterapeutico-manterTipoJustificativaList";
	
	@Inject
	private ManterTipoJustificativaListPaginatorController manterTipoJustificativaListPaginatorController;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoFacade;
	
	private List<MptTipoSessao> mptTipoSessao;
	private MptTipoSessao selecionadoMptTipoSessao;
	private MptTipoSessao tipoSessao;
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}


	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	private DominioSituacao indSituacao;
	private String descricao;
	private MptTipoJustificativa indJustificativa;
	private MptJustificativa mptJustificativa;
	private JustificativaVO justificativaVO;
	private MptJustificativa selecionadoEdicao;
	private boolean edicaoAtiva = false;
	private boolean bloqueioDescricao = false;
	private boolean habDesSituacao;
	
	
	@PostConstruct
	public void init() {
		this.begin(conversation, true);
	}
	
	
	public void inicializar(){
		if(this.edicaoAtiva == false){
			this.descricao = null;
			this.indJustificativa = null;
			this.indSituacao = null;
			this.mptTipoSessao = null;
			this.selecionadoMptTipoSessao = null;
			this.bloqueioDescricao = false;
			this.habDesSituacao = true;
			this.indSituacao = DominioSituacao.A; 
		}else{
			this.selecionadoEdicao = this.procedimentoFacade.obterMptJustificativaVO(this.justificativaVO);
			this.mptJustificativa = this.selecionadoEdicao;
			this.descricao = this.selecionadoEdicao.getDescricao();
			this.indJustificativa = this.selecionadoEdicao.getMptTipoJustificativa();
			this.indSituacao = this.selecionadoEdicao.getIndSituacao();
			this.selecionadoMptTipoSessao = this.selecionadoEdicao.getMptTipoSessao();
			this.habDesSituacao = false;
		}
	}
	
	public String gravar() {
		try {
				if (this.edicaoAtiva == true) {
					if(this.descricao != null && !this.descricao.isEmpty()){
						this.selecionadoEdicao.setDescricao(this.descricao.trim());
					}else{
						this.selecionadoEdicao.setDescricao(null);
					}
					
					this.selecionadoEdicao.setIndSituacao(this.indSituacao);
					this.selecionadoEdicao
							.setMptTipoJustificativa(this.indJustificativa);
					this.tipoSessao = this.procedimentoFacade.obterTipoSessaoOriginal(this.selecionadoMptTipoSessao.getSeq());
					this.selecionadoEdicao
							.setMptTipoSessao(this.tipoSessao);
					this.procedimentoFacade
							.editarJustificativa(this.selecionadoEdicao);
					this.manterTipoJustificativaListPaginatorController.setEdicaoOk(true);
				} else {
					this.selecionadoEdicao = new MptJustificativa();
					if(this.descricao != null && !this.descricao.isEmpty()){
						this.selecionadoEdicao.setDescricao(this.descricao.trim());
					}					
					
					this.selecionadoEdicao.setIndSituacao(this.indSituacao);
					this.selecionadoEdicao
							.setMptTipoJustificativa(this.indJustificativa);
					if(this.selecionadoMptTipoSessao != null){
						this.tipoSessao = this.procedimentoFacade.obterTipoSessaoOriginal(this.selecionadoMptTipoSessao.getSeq());
					}
					this.selecionadoEdicao
							.setMptTipoSessao(this.tipoSessao);
					this.procedimentoFacade
							.adicionarJustificativa(this.selecionadoEdicao);
					this.manterTipoJustificativaListPaginatorController.setPersistirOk(true);
				}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.selecionadoEdicao = this.mptJustificativa;
			return null;
		}	
		
		this.edicaoAtiva = false;
		manterTipoJustificativaListPaginatorController.pesquisar();
		return PAGE_MANTER_TIPO_JUSTIFICATIVA_LIST;
	}

	public void verificaBloqueio(){
		if(this.indJustificativa != null){
			if(this.indJustificativa.getSigla().equals("BLQ")){
				this.descricao = null;
				this.bloqueioDescricao = true;
			}else{
				this.bloqueioDescricao = false;
			}
		}
	}
	
	public List<MptTipoSessao> obterListaTipoSessao(){
		this.mptTipoSessao = this.procedimentoFacade.obterTipoSessaoDescricao();
		return this.mptTipoSessao;
	}
	
	public String voltar(){
		this.limpar();
		return PAGE_MANTER_TIPO_JUSTIFICATIVA_LIST;
	}
	
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.indJustificativa = null;
		this.indSituacao = null;
		this.descricao = null;
		this.mptTipoSessao = null;
		this.selecionadoEdicao = null;
		this.bloqueioDescricao = false;
	}
	
	public List<MptTipoJustificativa> listarMptTipoJustificativa(String strPesquisa) {
		return this.returnSGWithCount(this.procedimentoFacade.listarMptTipoJustificativa(strPesquisa),
				 					this.procedimentoFacade.listarMptTipoJustificativaCount(strPesquisa));
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
	public MptJustificativa getSelecionadoEdicao() {
		return selecionadoEdicao;
	}
	public void setSelecionadoEdicao(MptJustificativa selecionadoEdicao) {
		this.selecionadoEdicao = selecionadoEdicao;
	}



	public ManterTipoJustificativaListPaginatorController getManterTipoJustificativaListPaginatorController() {
		return manterTipoJustificativaListPaginatorController;
	}



	public void setManterTipoJustificativaListPaginatorController(
			ManterTipoJustificativaListPaginatorController manterTipoJustificativaListPaginatorController) {
		this.manterTipoJustificativaListPaginatorController = manterTipoJustificativaListPaginatorController;
	}



	public boolean isEdicaoAtiva() {
		return edicaoAtiva;
	}



	public void setEdicaoAtiva(boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}

	public MptTipoSessao getSelecionadoMptTipoSessao() {
		return selecionadoMptTipoSessao;
	}

	public void setSelecionadoMptTipoSessao(MptTipoSessao selecionadoMptTipoSessao) {
		this.selecionadoMptTipoSessao = selecionadoMptTipoSessao;
	}

	public static String getPageManterTipoJustificativaList() {
		return PAGE_MANTER_TIPO_JUSTIFICATIVA_LIST;
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

	public boolean isBloqueioDescricao() {
		return bloqueioDescricao;
	}

	public void setBloqueioDescricao(boolean bloqueioDescricao) {
		this.bloqueioDescricao = bloqueioDescricao;
	}


	public JustificativaVO getJustificativaVO() {
		return justificativaVO;
	}


	public void setJustificativaVO(JustificativaVO justificativaVO) {
		this.justificativaVO = justificativaVO;
	}


	public boolean isHabDesSituacao() {
		return habDesSituacao;
	}


	public void setHabDesSituacao(boolean habDesSituacao) {
		this.habDesSituacao = habDesSituacao;
	}


	
	

}
