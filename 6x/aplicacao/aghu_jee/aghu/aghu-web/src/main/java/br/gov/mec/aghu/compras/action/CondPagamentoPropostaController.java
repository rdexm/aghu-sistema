package br.gov.mec.aghu.compras.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.ScoPropostaItemFornecedorVO;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável por criar, editar e visualizar uma condição de
 * pagamento de proposta de fornecedor.
 * 
 * @author mlcruz
 */

public class CondPagamentoPropostaController extends ActionController {

	private static final long serialVersionUID = -1266623113898609131L;

	private static final Log LOG = LogFactory.getLog(CondPagamentoPropostaController.class);

	// Dependências

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasFacade comprasFacade;

	/** Proposta/Item do Fornecedor */
	private ScoPropostaItemFornecedorVO propostaItem;

	/** Condição de Pagamento da Proposta/Item */
	private ScoCondicaoPagamentoPropos condicao;

	/** Parcelas da Condição de Pagamento da Proposta/Item */
	private List<ScoParcelasPagamento> parcelas, parcelasExcluidas;

	/** Parcela em foco. */
	private ScoParcelasPagamento parcela;

	/** Estado original da parcela em foco (usado quando a edição é cancelada). */
	private ScoParcelasPagamento parcelaOriginal;

	// Parâmetros

	/** ID do PAC */
	private Integer pacId;

	/** ID do Fornecedor */
	private Integer fornecedorId;

	/** ID do Item do PAC */
	private Short pacItemId;

	/** ID da Condição de Pagamento da Proposta */
	private Integer id;

	/** Flag Somente Leitura */
	private Boolean readonly;

	/** URL de Origem */
	private String origem;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Carrega PAC da condição de pagamento da proposta.
	 */
	public void iniciar() {
		parcelasExcluidas = new ArrayList<ScoParcelasPagamento>();

		// Condição já cadastrada.
		if (id != null) {

			condicao = comprasFacade.obterCondicaoPagamentoPropos(id);
			parcelas = comprasFacade.obterParcelas(condicao);

			if (condicao == null) {
				LOG.warn("Condição de pagamento não obtida.");
			}
			// Nova condição.
		} else if (pacId != null && fornecedorId != null) {
			condicao = new ScoCondicaoPagamentoPropos();
			condicao.setIndCondEscolhida(false);
			parcelas = new ArrayList<ScoParcelasPagamento>();

			// Condição para item da proposta.
			if (pacItemId != null) {

				ScoItemPropostaFornecedorId itemId = new ScoItemPropostaFornecedorId(pacId, fornecedorId, pacItemId);

				ScoItemPropostaFornecedor item = comprasFacade.obterItemPropostaFornPorChavePrimaria(itemId);

				condicao.setItemPropostaFornecedor(item);

				// Condição para proposta.
			} else {
				ScoPropostaFornecedorId idProposta = new ScoPropostaFornecedorId(pacId, fornecedorId);

				ScoPropostaFornecedor proposta = pacFacade.obterPropostaFornecedor(idProposta);

				condicao.setPropostaFornecedor(proposta);
			}
			// Parâmetros insuficientes.
		} else {
			LOG.warn("Parâmetros insuficientes para manter condição de pagamento.");
		}

		resetParcela();
		setPropostaItem(condicao);

		if (this.parcela.getParcela().shortValue() == 1) {
			this.parcela.setPercPagamento(new BigDecimal(100));
		}
	}

	/**
	 * Reseta parcela em foco.
	 */
	public void resetParcela() {
		parcela = new ScoParcelasPagamento();
		parcela.setCondicaoPagamentoPropos(condicao);
		Integer numero = parcelas.size() + 1;
		parcela.setParcela(numero.shortValue());
		parcelaOriginal = null;
	}

	/**
	 * Define proposta/item com base na condição de pagamento.
	 * 
	 * @param condicao
	 *            Condição de pagamento.
	 */
	private void setPropostaItem(ScoCondicaoPagamentoPropos condicao) {

		propostaItem = new ScoPropostaItemFornecedorVO();
		ScoLicitacao licitacao = null;
		ScoFornecedor fornecedor = null;

		// Condição para proposta.
		if (condicao.getPropostaFornecedor() != null) {	
			ScoPropostaFornecedor proposta = pacFacade.obterPropostaFornecedor(condicao.getPropostaFornecedor().getId());
			condicao.setPropostaFornecedor(proposta);           
			licitacao = pacFacade.obterLicitacao(condicao.getPropostaFornecedor().getLicitacao().getNumero());
			fornecedor = this.comprasFacade.obterFornecedorPorChavePrimaria(condicao.getPropostaFornecedor().getFornecedor().getNumero());
			

			propostaItem.setTotal(pacFacade.obterValorTotalProposta(condicao.getPropostaFornecedor()));
			// Condição para item da proposta.
		} else if (condicao.getItemPropostaFornecedor() != null) {
			ScoItemPropostaFornecedor  scoItemPropostaFornecedor = pacFacade.obterItemPropostaFornecedorPorID(condicao.getItemPropostaFornecedor().getId());
			ScoPropostaFornecedor proposta = pacFacade.obterPropostaFornecedor(scoItemPropostaFornecedor.getPropostaFornecedor().getId());
			condicao.setItemPropostaFornecedor(scoItemPropostaFornecedor);
			condicao.getItemPropostaFornecedor().setPropostaFornecedor(proposta); 
			
			licitacao = pacFacade.obterLicitacao(scoItemPropostaFornecedor.getPropostaFornecedor().getLicitacao().getNumero());
			fornecedor = this.comprasFacade.obterFornecedorPorChavePrimaria(scoItemPropostaFornecedor.getPropostaFornecedor().getFornecedor().getNumero());
					
			
			propostaItem.setPacItemId(condicao.getItemPropostaFornecedor().getItemLicitacao().getId().getNumero());

			propostaItem.setTotal(pacFacade.obterValorTotalItemProposta(condicao.getItemPropostaFornecedor()));
		} else {
			LOG.warn("Condição não pertence a uma proposta ou item.");
		}

		propostaItem.setPacId(licitacao.getNumero());
		propostaItem.setPacDescricao(licitacao.getDescricao());
		propostaItem.setFornecedor(FornecedorStringUtils.format(fornecedor));
	}

	/**
	 * Lista formas de pagamento.
	 * 
	 * @param filter
	 *            Filtro
	 * @return Resultado
	 */
	public List<ScoFormaPagamento> listarFormasPagamento(String filter) {
		return pacFacade.listarFormasPagamento(filter);
	}

	/**
	 * Verifica se parcela em edição é nova ou já consta na grade.
	 * 
	 * @return Flag
	 */
	public boolean isNovaParcela() {
		return !parcelas.contains(parcela);
	}

	/**
	 * Adiciona parcela à condição de pagamento.
	 */
	public void adicionarParcela() {

		if (!isParcelaValida()) {
			return;
		}

		parcelas.add(parcela);
		resetParcela();
	}

	/**
	 * Altera parcela da condição de pagamento.
	 */
	public void alterarParcela() {

		if (!isParcelaValida()) {
			return;
		}
		resetParcela();
	}

	/**
	 * Cancela edição da parcela.
	 */
	public void cancelarEdicao() {
		parcela.setPrazo(parcelaOriginal.getPrazo());
		parcela.setPercPagamento(parcelaOriginal.getPercPagamento());
		parcela.setValorPagamento(parcelaOriginal.getValorPagamento());
		resetParcela();
	}

	/**
	 * Verifica se parcela é válida.
	 * 
	 * @return Flag
	 */
	private boolean isParcelaValida() {
		if (parcela.getPrazo() == null) {
			apresentarMsgNegocio(Severity.ERROR, "MESSAGE_PARCELA_PROPOSTA_FORNECEDOR_PRAZO_OBRIGATORIO");

			return false;
		} else if ((parcela.getValorPagamento() != null && parcela.getPercPagamento() != null)
				|| (parcela.getValorPagamento() == null && parcela.getPercPagamento() == null)) {
			apresentarMsgNegocio(Severity.ERROR, "MESSAGE_PARCELA_PROPOSTA_FORNECEDOR_VALOR_PERC_OBRIGATORIO");

			return false;
		} else {
			return true;
		}
	}

	/**
	 * Verifica se parcela em foco é a última.
	 * 
	 * @param parcela
	 *            Parcela em foco.
	 * @return Flag
	 */
	public boolean isLastParcela(ScoParcelasPagamento parcela) {
		if (!parcelas.isEmpty()) {
			return parcela.equals(parcelas.get(parcelas.size() - 1));
		} else {
			return false;
		}
	}

	/**
	 * Exclui parcela.
	 * 
	 * @param parcela
	 *            Parcela a ser excluída.
	 */
	public void excluir(ScoParcelasPagamento parcela) {
		parcelas.remove(parcela);

		if (parcela.getSeq() != null) {
			parcelasExcluidas.add(parcela);
		}

		resetParcela();
	}

	/**
	 * Grava condição de pagamento.
	 * 
	 * @return "voltar" - sucesso; null - falha.
	 */
	public String gravar() {
		try {
			if (id == null) {
				comprasFacade.inserir(condicao, parcelas);

				apresentarMsgNegocio(Severity.INFO, "MESSAGE_COND_PGTO_PROP_GRAVADA");
			} else {
				comprasFacade.atualizar(condicao, parcelas, parcelasExcluidas);

				apresentarMsgNegocio(Severity.INFO, "MESSAGE_COND_PGTO_PROP_ALTERADA");
			}
			
			return voltar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Volta para a interface de origem.
	 * 
	 * @return "voltar"
	 */
	public String voltar() {
		return origem;
	}

	// Getters/Setters

	public ScoPropostaItemFornecedorVO getPropostaItem() {
		return propostaItem;
	}

	public ScoCondicaoPagamentoPropos getCondicao() {
		return condicao;
	}

	public List<ScoParcelasPagamento> getParcelas() {
		return parcelas;
	}

	public ScoParcelasPagamento getParcela() {
		return parcela;
	}

	public void setParcela(ScoParcelasPagamento parcela) {
		this.parcela = parcela;
		parcelaOriginal = new ScoParcelasPagamento();
		parcelaOriginal.setPrazo(parcela.getPrazo());
		parcelaOriginal.setPercPagamento(parcela.getPercPagamento());
		parcelaOriginal.setValorPagamento(parcela.getValorPagamento());
	}

	public Integer getPacId() {
		return pacId;
	}

	public void setPacId(Integer pacId) {
		this.pacId = pacId;
	}

	public Integer getFornecedorId() {
		return fornecedorId;
	}

	public void setFornecedorId(Integer fornecedorId) {
		this.fornecedorId = fornecedorId;
	}

	public Short getPacItemId() {
		return pacItemId;
	}

	public void setPacItemId(Short pacItemId) {
		this.pacItemId = pacItemId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getReadonly() {
		return readonly;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
}