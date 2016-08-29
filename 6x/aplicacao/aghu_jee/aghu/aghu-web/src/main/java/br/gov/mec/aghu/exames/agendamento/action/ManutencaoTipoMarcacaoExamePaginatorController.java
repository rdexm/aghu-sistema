package br.gov.mec.aghu.exames.agendamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para Manter Tipo de Marcação de Exames.
 * 
 * @author diego.pacheco
 *
 */

public class ManutencaoTipoMarcacaoExamePaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2616916558062018276L;
	
	private static final Log LOG = LogFactory.getLog(ManutencaoTipoMarcacaoExamePaginatorController.class);

	private static final String REDIRECT_TIPO_MARCACAO_EXAME_CRUD = "manterTipoMarcacaoExameCRUD";
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AelTipoMarcacaoExame> dataModel;
	
	private Short tipoMarcacaoExameSeq;
	private String tipoMarcacaoExameDescricao;
	private DominioSituacao tipoMarcacaoExameSituacao;
	private Boolean exibeBotaoNovo;
	private AelTipoMarcacaoExame tipoMarcacaoExameSelecionado;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Metodo que realiza a acao de pesquisar.
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();
		exibeBotaoNovo = true;
	}
	
	public String editarTipoMarcacaoExame() {
		return REDIRECT_TIPO_MARCACAO_EXAME_CRUD;
	}
	
	public String iniciarInclusao() {
		return REDIRECT_TIPO_MARCACAO_EXAME_CRUD;
	}
	
	/**
	 * Metodo que limpa os campos de filtro.
	 */
	public void limparPesquisa() {
		this.setTipoMarcacaoExameSeq(null);
		this.setTipoMarcacaoExameDescricao(null);
		this.setTipoMarcacaoExameSituacao(null);
		
		// Apaga resultados da pesquisa
		dataModel.setPesquisaAtiva(false);
		setExibeBotaoNovo(false);
	}

	@Override
	public Long recuperarCount() {
		return agendamentoExamesFacade.pesquisarTipoMarcacaoExameCount(tipoMarcacaoExameSeq, 
				tipoMarcacaoExameDescricao, tipoMarcacaoExameSituacao);
	}

	@Override
	public List<AelTipoMarcacaoExame> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		return agendamentoExamesFacade.pesquisarTipoMarcacaoExame(firstResult, maxResults, orderProperty, asc, 
				tipoMarcacaoExameSeq, tipoMarcacaoExameDescricao, tipoMarcacaoExameSituacao);
	}
	
	public void selecionarTipoMarcacaoExclusao(AelTipoMarcacaoExame tipoMarcacaoExame) {
		tipoMarcacaoExameSelecionado = tipoMarcacaoExame;
	}
	
	/**
	 * Método responsavel por excluir
	 * um tipo de marcação.
	 */
	public void excluir() {
		try {
			String descricao = tipoMarcacaoExameSelecionado.getDescricao();
			this.agendamentoExamesFacade.excluirTipoMarcacaoExame(tipoMarcacaoExameSelecionado.getSeq());
			dataModel.reiniciarPaginator();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_TIPO_MARCACAO_EXAME", descricao);
			tipoMarcacaoExameSelecionado = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	public Short getTipoMarcacaoExameSeq() {
		return tipoMarcacaoExameSeq;
	}

	public void setTipoMarcacaoExameSeq(Short tipoMarcacaoExameSeq) {
		this.tipoMarcacaoExameSeq = tipoMarcacaoExameSeq;
	}

	public String getTipoMarcacaoExameDescricao() {
		return tipoMarcacaoExameDescricao;
	}

	public void setTipoMarcacaoExameDescricao(String tipoMarcacaoExameDescricao) {
		this.tipoMarcacaoExameDescricao = tipoMarcacaoExameDescricao;
	}

	public DominioSituacao getTipoMarcacaoExameSituacao() {
		return tipoMarcacaoExameSituacao;
	}

	public void setTipoMarcacaoExameSituacao(
			DominioSituacao tipoMarcacaoExameSituacao) {
		this.tipoMarcacaoExameSituacao = tipoMarcacaoExameSituacao;
	}

	public Boolean getExibeBotaoNovo() {
		return exibeBotaoNovo;
	}

	public void setExibeBotaoNovo(Boolean exibeBotaoNovo) {
		this.exibeBotaoNovo = exibeBotaoNovo;
	}

	public DynamicDataModel<AelTipoMarcacaoExame> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelTipoMarcacaoExame> dataModel) {
		this.dataModel = dataModel;
	}

	public AelTipoMarcacaoExame getTipoMarcacaoExameSelecionado() {
		return tipoMarcacaoExameSelecionado;
	}

	public void setTipoMarcacaoExameSelecionado(
			AelTipoMarcacaoExame tipoMarcacaoExameSelecionado) {
		this.tipoMarcacaoExameSelecionado = tipoMarcacaoExameSelecionado;
	}

}
