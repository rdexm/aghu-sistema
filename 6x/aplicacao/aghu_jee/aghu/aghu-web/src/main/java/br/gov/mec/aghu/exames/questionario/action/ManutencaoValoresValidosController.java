package br.gov.mec.aghu.exames.questionario.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelValorValidoQuestao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManutencaoValoresValidosController extends ActionController{

	private static final long serialVersionUID = 7644196031587162012L;

	private static final String CADASTRO_QUESTAO_LIST = "cadastroQuestaoList";

	@EJB
	private IQuestionarioExamesFacade questionarioFacade;
	
	private Integer seqQuestao;
	private String descricaoQuestao;
	
	private String valorAbreviado;
	private String valorSignificado;
	private Boolean indSituacao;
	
	private AelQuestao questao;
	private List<AelValorValidoQuestao> listaValoresValidos;
	
	private AelValorValidoQuestao valorValidoSelecionado;
	
	private Integer qaoSeq;
	private Short seqP;

	private Boolean edicao = false;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public enum ManutencaoValoresValidosControllerExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_PROC_ESP, PROC_ESP_ATUALIZADA_SUCESSO, PROC_ESP_INSERIDA_SUCESSO, PROC_ESP_EXCLUIDA_SUCESSO,
		VALIDAR_AGENDAMENTO ;
	}

	public void iniciar() {
	 

		this.limpar();
		Enum[] fetchArgsLeftJoin = {AelQuestao.Fields.AEL_VALOR_VALIDO_QUESTAOS,};
		this.questao = this.questionarioFacade.obterAelQuestaoById(seqQuestao, null, fetchArgsLeftJoin);
		this.descricaoQuestao = questao.getDescricao();
		this.listaValoresValidos = new ArrayList<AelValorValidoQuestao>(questao.getAelValorValidoQuestaos());
	
	}
	
	private void atualizarValoresValidos() {
		this.listaValoresValidos = this.questionarioFacade.buscarValoresValidosQuestaoPorQuestao(this.questao.getSeq());
	}
	
	private void limpar() {
		this.valorAbreviado = null;
		this.valorSignificado = null;
		this.indSituacao = true;
		this.edicao = false;
		this.questao = null;
		this.listaValoresValidos = null;
		this.qaoSeq = null;
		this.seqP = null;
	}
	
	public void editar(AelValorValidoQuestao valorValido) {
		this.valorValidoSelecionado = valorValido;
		this.valorAbreviado = valorValido.getValorAbreviado();
		this.valorSignificado = valorValido.getValorSignificado();
		this.indSituacao = DominioSituacao.A.equals(valorValido.getIndSituacao()) ? true : false;
		this.edicao = true;
	}
	
	public void cancelarEdicao() {
		this.valorValidoSelecionado = null;
		this.edicao = false;
		this.indSituacao = true;
		this.valorSignificado = null;
		this.valorAbreviado = null;
		this.atualizarValoresValidos();
	}
	
	public String voltar() {
		cancelarEdicao();
		return CADASTRO_QUESTAO_LIST;
	}
	
	public void excluir() {
		try {
			this.questionarioFacade.excluirValorValidoQuestao(qaoSeq, seqP);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_VALOR_VALIDO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.atualizarValoresValidos();
		this.qaoSeq = null;
		this.seqP = null;
	}
	
	public String gravar() {
		if(this.valorValidoSelecionado == null && !this.edicao) {
			this.valorValidoSelecionado = new AelValorValidoQuestao();
			this.valorValidoSelecionado.setAelQuestao(this.questao);
			this.valorValidoSelecionado.setValorAbreviado(this.valorAbreviado);
			this.valorValidoSelecionado.setValorSignificado(this.valorSignificado);
			this.valorValidoSelecionado.setIndSituacao(this.indSituacao ? DominioSituacao.A : DominioSituacao.I);
			this.questionarioFacade.persistirValorValidoQuestao(this.valorValidoSelecionado);
		} else {
			this.valorValidoSelecionado.setValorAbreviado(this.valorAbreviado);
			this.valorValidoSelecionado.setValorSignificado(this.valorSignificado);
			this.valorValidoSelecionado.setIndSituacao(this.indSituacao ? DominioSituacao.A : DominioSituacao.I);
			this.questionarioFacade.atualizarValorValidoQuestao(this.valorValidoSelecionado);
		}
		
		this.cancelarEdicao();
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SALVAR_VALOR_VALIDO");
		return null;
	}

	public Integer getSeqQuestao() {
		return seqQuestao;
	}

	public void setSeqQuestao(Integer seqQuestao) {
		this.seqQuestao = seqQuestao;
	}

	public String getDescricaoQuestao() {
		return descricaoQuestao;
	}

	public void setDescricaoQuestao(String descricaoQuestao) {
		this.descricaoQuestao = descricaoQuestao;
	}

	public String getValorAbreviado() {
		return valorAbreviado;
	}

	public void setValorAbreviado(String valorAbreviado) {
		this.valorAbreviado = valorAbreviado;
	}

	public String getValorSignificado() {
		return valorSignificado;
	}

	public void setValorSignificado(String valorSignificado) {
		this.valorSignificado = valorSignificado;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public AelQuestao getQuestao() {
		return questao;
	}

	public void setQuestao(AelQuestao questao) {
		this.questao = questao;
	}

	public List<AelValorValidoQuestao> getListaValoresValidos() {
		return listaValoresValidos;
	}

	public void setListaValoresValidos(
			List<AelValorValidoQuestao> listaValoresValidos) {
		this.listaValoresValidos = listaValoresValidos;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public AelValorValidoQuestao getValorValidoSelecionado() {
		return valorValidoSelecionado;
	}

	public void setValorValidoSelecionado(
			AelValorValidoQuestao valorValidoSelecionado) {
		this.valorValidoSelecionado = valorValidoSelecionado;
	}

	public Integer getQaoSeq() {
		return qaoSeq;
	}

	public void setQaoSeq(Integer qaoSeq) {
		this.qaoSeq = qaoSeq;
	}

	public Short getSeqP() {
		return seqP;
	}

	public void setSeqP(Short seqP) {
		this.seqP = seqP;
	}
}