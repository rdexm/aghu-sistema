package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoRecomendacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoRecomendacaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4755086452411990046L;

	private static final String GRUPO_RECOMENDACAO_CRUD = "grupoRecomendacaoCRUD";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AelGrupoRecomendacao grupoRecomendacao = new AelGrupoRecomendacao();
	

	@Inject @Paginator
	private DynamicDataModel<AelGrupoRecomendacao> dataModel;
	
	private AelGrupoRecomendacao selecionado;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	@Override
	public List<AelGrupoRecomendacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(orderProperty == null){
			orderProperty = AelGrupoRecomendacao.Fields.DESCRICAO.toString();
			asc = true;
		}
		
		return  cadastrosApoioExamesFacade.pesquisaGrupoRecomendacaoPaginada(firstResult, maxResult, orderProperty, asc, grupoRecomendacao);
	}

	@Override
	public Long recuperarCount() {
		return cadastrosApoioExamesFacade.pesquisaGrupoRecomendacaoPaginadaCount(grupoRecomendacao);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		setGrupoRecomendacao(new AelGrupoRecomendacao());
	}
	
	public void excluir() {
		try {
			cadastrosApoioExamesFacade.removerAelGrupoRecomendacao(selecionado.getSeq());
			
			String paramMsgNome = "";
			if (selecionado != null && StringUtils.isNotBlank(selecionado.getDescricao())) {
				if (selecionado.getDescricao().length() > 21) {
					paramMsgNome = selecionado.getDescricao().substring(0, 20) + "...";
				} else {
					paramMsgNome = selecionado.getDescricao();
				}
			}
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_GRUPO_RECOMENDACAO", paramMsgNome);
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return GRUPO_RECOMENDACAO_CRUD;
	}
	
	public String editar(){
		return GRUPO_RECOMENDACAO_CRUD;
	}

	public void setGrupoRecomendacao(AelGrupoRecomendacao grupoRecomendacao) {
		this.grupoRecomendacao = grupoRecomendacao;
	}

	public AelGrupoRecomendacao getGrupoRecomendacao() {
		return grupoRecomendacao;
	}

	public DynamicDataModel<AelGrupoRecomendacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelGrupoRecomendacao> dataModel) {
		this.dataModel = dataModel;
	}

	public AelGrupoRecomendacao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelGrupoRecomendacao selecionado) {
		this.selecionado = selecionado;
	}
}
