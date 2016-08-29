package br.gov.mec.aghu.exames.questionario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroGrupoQuestaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1938842027342597891L;

	private static final String CADASTRO_GRUPO_QUESTAO = "cadastroGrupoQuestaoCRUD";

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	private AelGrupoQuestao grupoQuestao;

	@Inject @Paginator
	private DynamicDataModel<AelGrupoQuestao> dataModel;
	
	private AelGrupoQuestao selecionado;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
			this.limparPesquisa();
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.grupoQuestao = new AelGrupoQuestao();
	}
	
	@Override
	public Long recuperarCount() {
		return questionarioExamesFacade.contarAelGrupoQuestao(this.grupoQuestao);
	}

	@Override
	public List<AelGrupoQuestao> recuperarListaPaginada(Integer arg0, Integer arg1, String arg2, boolean arg3) {
		return questionarioExamesFacade.buscarAelGrupoQuestao(this.grupoQuestao, arg0, arg1, arg2, arg3);
	}
	
	public void excluir() {
		try {
			this.questionarioExamesFacade.excluirGrupoQuestao(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_GRUPO_QUESTAO", selecionado.getDescricao());
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return CADASTRO_GRUPO_QUESTAO;
	}
	
	public String editar(){
		return CADASTRO_GRUPO_QUESTAO;
	}

	public void setGrupoQuestao(AelGrupoQuestao grupoQuestao) {
		this.grupoQuestao = grupoQuestao;
	}

	public AelGrupoQuestao getGrupoQuestao() {
		return grupoQuestao;
	}

	public DynamicDataModel<AelGrupoQuestao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelGrupoQuestao> dataModel) {
		this.dataModel = dataModel;
	}

	public AelGrupoQuestao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelGrupoQuestao selecionado) {
		this.selecionado = selecionado;
	}
}