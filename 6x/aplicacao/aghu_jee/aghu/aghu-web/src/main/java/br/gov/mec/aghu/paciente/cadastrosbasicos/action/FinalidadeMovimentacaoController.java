package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de finalidade
 * de movimentação.
 * 
 * @author david.laks
 */


public class FinalidadeMovimentacaoController extends ActionController{

	private static final long serialVersionUID = -5815836372917338418L;

	private static final String REDIRECT_FINALIDADE_MOVIMENTACAO_LIST = "finalidadeMovimentacaoList";
	
	private static final Log LOG = LogFactory.getLog(FinalidadeMovimentacaoController.class);
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	@Inject
	private FinalidadeMovimentacaoPaginatorController finalidadeMovimentacaoPaginatorController;
	
	/**
	 * Finalidade de movimentação a ser criada/editada
	 */
	private AipFinalidadesMovimentacao aipFinalidadeMovimentacao;

	public void iniciarInclusao() {
		this.aipFinalidadeMovimentacao = new AipFinalidadesMovimentacao();
		this.aipFinalidadeMovimentacao.setAtivo(true);
	}

	public void iniciarEdicao(AipFinalidadesMovimentacao aipFinalidadeMovimentacao) {
		this.aipFinalidadeMovimentacao = aipFinalidadeMovimentacao;
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * Finalidade de movimentação
	 */
	public String confirmar() {
		finalidadeMovimentacaoPaginatorController.reiniciarPaginator();

		try {
			// Tarefa 659 - deixar todos textos das entidades em caixa alta via
			// toUpperCase()
			transformarTextosCaixaAlta();

			boolean create = this.aipFinalidadeMovimentacao.getCodigo() == null;

			this.cadastrosBasicosPacienteFacade.persistirFinalidadeMovimentacao(this.aipFinalidadeMovimentacao);

			if (create) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_FINALIDADE_MOVIMENTACAO",
						this.aipFinalidadeMovimentacao.getDescricao());
				
				
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_FINALIDADE_MOVIMENTACAO",
						this.aipFinalidadeMovimentacao.getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);

			return null;
		}

		return REDIRECT_FINALIDADE_MOVIMENTACAO_LIST;
	}

	private void transformarTextosCaixaAlta() {
		this.aipFinalidadeMovimentacao.setDescricao(this.aipFinalidadeMovimentacao.getDescricao() == null ? null
				: this.aipFinalidadeMovimentacao.getDescricao().toUpperCase());
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Finalidades de Movimentação
	 */
	public String cancelar() {
		LOG.info("Cancelado");
		finalidadeMovimentacaoPaginatorController.reiniciarPaginator();
		return REDIRECT_FINALIDADE_MOVIMENTACAO_LIST;
	}

	// ### GETs e SETs ###

	public AipFinalidadesMovimentacao getAipFinalidadeMovimentacao() {
		return aipFinalidadeMovimentacao;
	}

	public void setAipFinalidadeMovimentacao(AipFinalidadesMovimentacao aipFinalidadeMovimentacao) {
		this.aipFinalidadeMovimentacao = aipFinalidadeMovimentacao;
	}
}
