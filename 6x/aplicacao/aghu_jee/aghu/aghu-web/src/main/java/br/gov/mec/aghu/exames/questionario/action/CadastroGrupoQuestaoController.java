package br.gov.mec.aghu.exames.questionario.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroGrupoQuestaoController extends ActionController {

	private static final long serialVersionUID = 619893954362522945L;

	private static final String CADASTRO_GRUPO_QUESTAO_LIST = "cadastroGrupoQuestaoList";

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	private AelGrupoQuestao grupoQuestao;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if (grupoQuestao != null && grupoQuestao.getSeq() != null) {
			grupoQuestao = questionarioExamesFacade.obterAelGrupoQuestaoById(grupoQuestao.getSeq());

			if(grupoQuestao == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			grupoQuestao = new AelGrupoQuestao();
		}
		return null;
	
	}

	public String cancelar() {
		this.grupoQuestao = null;
		return CADASTRO_GRUPO_QUESTAO_LIST;
	}

	public String gravar() {
		boolean novo = this.grupoQuestao.getSeq() == null;
		try {
			this.questionarioExamesFacade.gravarGrupoQuestao(grupoQuestao);

			this.apresentarMsgNegocio(Severity.INFO, novo ? "MENSAGEM_SUCESSO_GRAVAR_GRUPO_QUESTAO" : "MENSAGEM_SUCESSO_ALTERAR_GRUPO_QUESTAO",
					grupoQuestao.getDescricao());
			
			return cancelar();
		} catch (final BaseException e) {
			if (novo) {
				this.grupoQuestao.setSeq(null);
			}
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void setGrupoQuestao(AelGrupoQuestao grupoQuestao) {
		this.grupoQuestao = grupoQuestao;
	}

	public AelGrupoQuestao getGrupoQuestao() {
		return grupoQuestao;
	}
}