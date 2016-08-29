package br.gov.mec.aghu.exames.questionario.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioMascaraData;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDadoQuestionario;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroQuestaoController extends ActionController {

	private static final long serialVersionUID = 1938842027342597891L;

	private static final String CADASTRO_QUESTAO_LIST = "cadastroQuestaoList";

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	private AelQuestao questao;
	private DominioMascaraData mascaraData;
	private Boolean ativo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		if (questao != null && questao.getSeq() != null) {
			this.questao = questionarioExamesFacade.obterAelQuestaoById(questao.getSeq(), null, null);

			if(questao == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			if (DominioTipoDadoQuestionario.D.equals(this.questao.getTipoDados())) {
				this.mascaraData = DominioMascaraData.valorPorMascara(this.questao.getMascara());
			}
			
		} else {
			this.questao = new AelQuestao();
			questao.setIndSituacao(DominioSituacao.A);
		}
		
		this.ativo = DominioSituacao.A.equals(this.questao.getIndSituacao());
		
		return null;
	
	}

	public String cancelar() {
		questao = null;
		mascaraData = null;
		ativo = false;
		
		return CADASTRO_QUESTAO_LIST;
	}

	public String gravar() {
		boolean novo = this.questao.getSeq() == null;
		try {
			this.questao.setIndSituacao(this.ativo ? DominioSituacao.A : DominioSituacao.I);
			
			if (DominioTipoDadoQuestionario.D.equals(this.questao.getTipoDados())) {
				this.questao.setMascara(this.mascaraData == null ? null : this.mascaraData.getMascara());
			} else if (!DominioTipoDadoQuestionario.N.equals(this.questao.getTipoDados())) {
				this.questao.setMascara(null);
			}
			
			this.questionarioExamesFacade.gravarQuestao(this.questao);

			this.apresentarMsgNegocio(Severity.INFO, novo ? "MENSAGEM_SUCESSO_GRAVAR_QUESTAO" : "MENSAGEM_SUCESSO_ALTERAR_QUESTAO",questao.getDescricao());
			
			return cancelar();
		} catch (final BaseException e) {
			if (novo) {
				this.questao.setSeq(null);
			}
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void setQuestao(AelQuestao questao) {
		this.questao = questao;
	}

	public AelQuestao getQuestao() {
		return questao;
	} 

	public void setMascaraData(DominioMascaraData mascaraData) {
		this.mascaraData = mascaraData;
	}

	public DominioMascaraData getMascaraData() {
		return mascaraData;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}