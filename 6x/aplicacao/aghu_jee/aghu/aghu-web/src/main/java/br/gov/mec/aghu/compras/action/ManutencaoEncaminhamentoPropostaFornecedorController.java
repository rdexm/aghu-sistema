package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável pela manutenção e encaminhamento de propostas de
 * fornecedores.
 * 
 * @author mlcruz
 */
public class ManutencaoEncaminhamentoPropostaFornecedorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<ScoPropostaFornecedor> dataModel;

	private static final long serialVersionUID = -1222041819697284835L;

	// Navigation
	private static final String PAGE_ITEM_PROPOSTA_FORNECEDOR_CRUD = "itemPropostaFornecedorCRUD";

	// Dependências

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private SecurityController securityController;

	/** Número da Licitação */
	private Integer numeroLicitacao = null;

	/** Licitação */
	private ScoLicitacao licitacao = null;

	/** Fornecedor */
	private String fornecedor;

	/** Tela de Origem */
	private String origem;

	/** Proposta Selecionada */
	private ScoPropostaFornecedor proposta;
	
	private Boolean usuarioPermissaoCadastrarProposta;	
	
	private final String strCadastrarProposta = "cadastrarProposta";
	private final String strGravar = "gravar";
	
	
	private boolean adjudicado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Inicializa tela.
	 */
	public void iniciar() {
		
		usuarioPermissaoCadastrarProposta = securityController.usuarioTemPermissao(strCadastrarProposta, strGravar);
		
		dataModel.setUserEditPermission(usuarioPermissaoCadastrarProposta);
		dataModel.setUserRemovePermission(usuarioPermissaoCadastrarProposta);
		
		
		
		licitacao = this.pacFacade.obterLicitacao(numeroLicitacao);
		dataModel.reiniciarPaginator();
	
	}
	

	public String editarItemProposta() {
		return PAGE_ITEM_PROPOSTA_FORNECEDOR_CRUD;
	}

	/**
	 * Pesquisa.
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	/**
	 * Limpa.
	 */
	public void limpar() {
		fornecedor = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		dataModel.reiniciarPaginator();
	}

	public String voltar() {
		return origem;
	}

	@Override
	public Long recuperarCount() {
		return pacFacade.contarPropostas(licitacao, fornecedor);
	}

	@Override
	public List<ScoPropostaFornecedor> recuperarListaPaginada(Integer first, Integer max, String order, boolean asc) {
		return pacFacade.pesquisarPropostas(licitacao, fornecedor, first, max, null, true);
	}

	/**
	 * Verifica se possui permissão para excluir.
	 * 
	 * @param proposta
	 *            Proposta
	 * @return Permissão
	 */
	public boolean hasPermissionToExcluir(ScoPropostaFornecedor proposta) {
		if (proposta == null){
			return false;
		}
		if (usuarioPermissaoCadastrarProposta) {
			if (!this.autFornecimentoFacade.isEmAf(proposta)) {
				return pacFacade.contarItensEmAf(proposta) == 0;
			}
		}

		return false;
	}

	/**
	 * Edita proposta.
	 * 
	 * @param proposta
	 *            Proposta
	 * @return Redirect
	 */
	public String novo() {
		return PAGE_ITEM_PROPOSTA_FORNECEDOR_CRUD;
	}

	/**
	 * Exclui proposta de fornecedor.
	 */
	public void excluir() {
		if (hasPermissionToExcluir(proposta)) {
			pacFacade.excluir(proposta.getId());

			apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUIR_PROPOSTA_FORNECEDOR", proposta.getFornecedor().getRazaoSocial(),
					licitacao.getNumero().toString());

			dataModel.reiniciarPaginator();
		} else {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_EXCLUIR_PROPOSTA_FORNECEDOR", proposta.getFornecedor().getRazaoSocial(),
					licitacao.getNumero().toString());
		}
	}

	/**
	 * Trunca string.
	 * 
	 * @param str
	 *            String
	 * @param length
	 * @return Truncada
	 */
	public String abreviar(String str, int length) {
		return StringUtils.abbreviate(str, length);
	}
	
	
	public boolean consultarFornecedorAdjudicado(Integer numLicitacao, Integer numFornecedor){
		return comprasFacade.consultarFornecedorAdjudicado(numLicitacao, numFornecedor);
	}

	// Getters/Setters

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getOrigem() {
		return origem;
	}

	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}

	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setProposta(ScoPropostaFornecedor proposta) {
		this.proposta = proposta;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		fornecedor = StringUtils.trimToNull(fornecedor);

		if (fornecedor != null) {
			fornecedor = fornecedor.replaceAll("\\-", "").replaceAll("\\.", "").replaceAll("\\/", "");
		}

		this.fornecedor = fornecedor;
	}

	public DynamicDataModel<ScoPropostaFornecedor> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoPropostaFornecedor> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isAdjudicado() {
		return adjudicado;
	}

	public void setAdjudicado(boolean adjudicado) {
		this.adjudicado = adjudicado;
	}
}
