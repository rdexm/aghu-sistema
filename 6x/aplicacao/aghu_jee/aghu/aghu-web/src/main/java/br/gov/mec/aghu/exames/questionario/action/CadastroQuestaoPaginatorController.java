package br.gov.mec.aghu.exames.questionario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioMascaraData;
import br.gov.mec.aghu.dominio.DominioTipoDadoQuestionario;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroQuestaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1938842027342597891L;

	private static final String CADASTRO_QUESTAO_CRUD = "cadastroQuestaoCRUD";

	private static final String MANTER_VALORES_VALIDOS = "manterValoresValidos";

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;

	private AelQuestao questao;
	private DominioMascaraData mascaraData;

	@Inject @Paginator
	private DynamicDataModel<AelQuestao> dataModel;
	
	private AelQuestao selecionado;
	
	private Integer seq;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		limparPesquisa();
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.questao = new AelQuestao();
		this.setMascaraData(null);
	}
	
	@Override
	public Long recuperarCount() {
		this.prepararPesquisa();
		return questionarioExamesFacade.contarAelQuestao(this.questao);
	}

	private void prepararPesquisa() {
		if(this.questao == null){
			this.questao = this.questionarioExamesFacade.obterAelQuestaoById(this.seq, null, null);
		}
		if (DominioTipoDadoQuestionario.D.equals(this.questao.getTipoDados())) {
			this.questao.setMascara(mascaraData == null ? null : mascaraData.getMascara());
			
		} else if (!DominioTipoDadoQuestionario.N.equals(this.questao.getTipoDados())) {
			this.questao.setMascara(null);
		}		
	}

	@Override
	public List<AelQuestao> recuperarListaPaginada(Integer arg0, Integer arg1, String arg2, boolean arg3) {
		this.prepararPesquisa();
		return questionarioExamesFacade.buscarAelQuestao(this.questao, arg0, arg1, arg2, arg3);
	}
	
	public void excluir() {
		try {
			this.questionarioExamesFacade.excluirQuestao(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_QUESTAO", selecionado.getSeq());
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return CADASTRO_QUESTAO_CRUD;
	}
	
	public String editar(){
		return CADASTRO_QUESTAO_CRUD;
	}
	
	public String manterValoresValidos(){
		return MANTER_VALORES_VALIDOS;
	}

	public void setQuestao(AelQuestao questao) {
		this.questao = questao;
	}

	public AelQuestao getQuestao() {
		return questao;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setMascaraData(DominioMascaraData mascaraData) {
		this.mascaraData = mascaraData;
	}

	public DominioMascaraData getMascaraData() {
		return mascaraData;
	}

	public DynamicDataModel<AelQuestao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelQuestao> dataModel) {
		this.dataModel = dataModel;
	}

	public AelQuestao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelQuestao selecionado) {
		this.selecionado = selecionado;
	}
}