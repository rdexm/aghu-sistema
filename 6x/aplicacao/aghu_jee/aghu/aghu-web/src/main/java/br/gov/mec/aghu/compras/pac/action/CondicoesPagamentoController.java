package br.gov.mec.aghu.compras.pac.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CondicoesPagamentoController extends ActionController {

	private static final long serialVersionUID = -4984001566203253834L;

	private static final String PAGE_CONDICOES_PAGAMENTO_PAC_LIST = "condicoesPagamentoPacList";

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	private ScoLicitacao pac;
	private ScoItemLicitacao itemPac;
	private ScoCondicaoPgtoLicitacao condicaoPagamento;
	private Short numeroItemPac;
	private Integer numeroPac;
	private String descricaoPac;
	private Integer numeroItem;
	private String solicitacao;
	private String nomeItem;
	private String descricaoItem;
	private Integer seqCondicaoPgto;
	private BigDecimal valorTotal;
	private ScoCondicaoPgtoLicitacao condicaoPgtoLicitacao;
	private List<ScoParcelasPagamento> listaParcelas;
	private List<ScoParcelasPagamento> listaParcelasExcluidas;
	private Integer numeroFormaPagamento;
	private ScoFormaPagamento formaPagamento;
	private BigDecimal desconto;
	private BigDecimal acrescimo;
	private Short parcela;
	private Short prazo;
	private BigDecimal percentual;
	private BigDecimal valor;
	private boolean visualizar;
	private boolean edicao;
	private boolean edicaoParcela;
	private boolean adicionando;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		this.limpar();
		
		if (!adicionando) {
			buscaPac();
			buscaItemPac();
			buscaCondicaoPgto();
			buscaListaParcelas();
		}

		if (this.parcela.shortValue() == 1) {
			this.percentual = new BigDecimal(100);
		}

		if (seqCondicaoPgto == null) {
			edicao = false;
		} else {
			edicao = true;
		}
	}
	
	private void limpar(){
		formaPagamento = null;
		desconto = null;
		acrescimo = null;
		prazo = null;
		valor = null;
		listaParcelas = null;
		listaParcelasExcluidas = null;
		edicaoParcela = false;
		adicionando = false;
	}
	
	
	public String adicionarParcela() {

		if ((percentual != null && valor != null) || (percentual == null && valor == null)) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALOR_OU_PERCENTUAL");
			return null;
		}

		if (valor != null && valor.compareTo(BigDecimal.ZERO) == 0) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALOR_OU_PERCENTUAL");
			return null;
		}

		if (percentual != null && percentual.compareTo(BigDecimal.ZERO) == 0) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALOR_OU_PERCENTUAL");
			return null;
		}

		if (prazo == null) {
			this.apresentarMsgNegocio(Severity.INFO, "PARCELA_SEM_PRAZO");
			return null;
		}

		if (edicaoParcela) {
			for (ScoParcelasPagamento parcela : listaParcelas) {
				if (parcela.getParcela() == this.parcela) {
					parcela.setPrazo(this.prazo);
					parcela.setPercPagamento(this.percentual);
					parcela.setValorPagamento(this.valor);

					parcela.setEmEdicao(false);
					this.edicaoParcela = false;
				}
			}
		} else {
			ScoParcelasPagamento parcela = new ScoParcelasPagamento();

			parcela.setParcela(this.parcela);
			parcela.setPrazo(this.prazo);
			parcela.setPercPagamento(this.percentual);
			parcela.setValorPagamento(this.valor);

			listaParcelas.add(parcela);
		}
		this.prazo = null;
		this.percentual = null;
		this.valor = null;

		setNumProximaParcela();
		setEdicaoParcela(false);

		return null;
	}

	public void editarParcela(ScoParcelasPagamento parcela) {
		for (ScoParcelasPagamento parc : listaParcelas) {
			parc.setEmEdicao(false);
		}

		this.parcela = parcela.getParcela();
		this.prazo = parcela.getPrazo();
		this.percentual = parcela.getPercPagamento();
		this.valor = parcela.getValorPagamento();

		parcela.setEmEdicao(true);
		setEdicaoParcela(true);
	}

	public void cancEdicao() {
		this.prazo = null;
		this.percentual = null;
		this.valor = null;

		for (ScoParcelasPagamento parcela : listaParcelas) {
			parcela.setEmEdicao(false);
		}

		setEdicaoParcela(false);
	}

	public void excluirParcela(ScoParcelasPagamento parcela) {
		listaParcelasExcluidas.add(parcela);

		listaParcelas.remove(listaParcelas.size() - 1);

		setNumProximaParcela();
	}

	public String gravar() {
		if (seqCondicaoPgto == null) {
			// Persiste Condição de Pagamento
			condicaoPagamento = new ScoCondicaoPgtoLicitacao();

			condicaoPagamento.setNumero(this.numeroFormaPagamento);
			if (itemPac == null) {
				condicaoPagamento.setLicitacao(pac);
			} else {
				condicaoPagamento.setItemLicitacao(itemPac);
			}
		}

		condicaoPagamento.setFormaPagamento(formaPagamento);
		condicaoPagamento.setPercDesconto(desconto);
		condicaoPagamento.setPercAcrescimo(acrescimo);

		// Persiste Condição de Pagamento e Parcelas
		try {
			pacFacade.gravarCondicaoPagtoParcelas(condicaoPagamento, listaParcelas, listaParcelasExcluidas);

			if (isEdicao()) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CONDICAO_PGTO");

			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_CONDICAO_PGTO");
			}

			return voltar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	private void buscaPac() {
		if (numeroPac != null) {
			pac = pacFacade.obterLicitacao(this.numeroPac);
			descricaoPac = pac.getDescricao();
		}
	}

	private void buscaItemPac() {
		if (numeroItemPac != null) {
			itemPac = pacFacade.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(this.numeroPac, this.numeroItemPac);

			ScoFaseSolicitacao fase = itemPac.getFasesSolicitacao().iterator().next();
			DominioTipoFaseSolicitacao tipoSolicitacao = fase.getTipo();
			List<ScoFaseSolicitacao> fases = new ArrayList<ScoFaseSolicitacao>();
			fases.add(fase);
			solicitacao = autFornecimentoFacade.obterNumeroSolicitacao(fases).toString();
			solicitacao = solicitacao + "/" + tipoSolicitacao.getDescricao();

			nomeItem = getNomeMaterialTruncado(this.obterNomeMaterialServico(itemPac), 40);
			descricaoItem = pacFacade.obterDescricaoMaterialServico(itemPac);

			valorTotal = pacFacade.obterValorTotalItemPac(this.numeroPac, this.numeroItemPac);
		} else {
			valorTotal = pacFacade.obterValorTotalPorNumeroLicitacao(this.numeroPac);
		}
	}

	private void buscaCondicaoPgto() {
		if (seqCondicaoPgto != null) {
			condicaoPagamento = pacFacade.buscarCondicaoPagamentoPK(seqCondicaoPgto);

			numeroFormaPagamento = condicaoPagamento.getNumero();
			formaPagamento = condicaoPagamento.getFormaPagamento();
			desconto = condicaoPagamento.getPercDesconto();
			acrescimo = condicaoPagamento.getPercAcrescimo();
		} else {
			numeroFormaPagamento = pacFacade.obterProxNumCondicaoPagamento(numeroPac, numeroItemPac);
		}
	}

	private void buscaListaParcelas() {
		listaParcelasExcluidas = new ArrayList<ScoParcelasPagamento>();

		if (seqCondicaoPgto == null) {
			listaParcelas = new ArrayList<ScoParcelasPagamento>();
		} else {
			listaParcelas = pacFacade.obterParcelasPagamento(seqCondicaoPgto);
		}
		setNumProximaParcela();
	}

	public List<ScoFormaPagamento> listarFormasPagamento(String pesquisa) {
		List<ScoFormaPagamento> listaFormaPagamento = pacFacade.listarFormasPagamento(pesquisa);

		return listaFormaPagamento;
	}

	public String obterNomeMaterialServico(ScoItemLicitacao item) {
		return pacFacade.obterNomeMaterialServico(item, true);
	}

	private String getNomeMaterialTruncado(String codNomeMaterial, Integer tamanhoMaximo) {
		if (codNomeMaterial.length() > tamanhoMaximo) {
			codNomeMaterial = codNomeMaterial.substring(0, tamanhoMaximo) + "...";
		}
		return codNomeMaterial;
	}

	private void setNumProximaParcela() {
		Integer proximaParcela = listaParcelas.size() + 1;
		setParcela(proximaParcela.shortValue());

		for (ScoParcelasPagamento parcela : listaParcelas) {
			parcela.setUltimaParcela(false);
		}

		if (proximaParcela > 1) {
			listaParcelas.get(listaParcelas.size() - 1).setUltimaParcela(true);
		}
	}

	public String voltar() {
		seqCondicaoPgto = null;
		return PAGE_CONDICOES_PAGAMENTO_PAC_LIST;
	}

	public Short getNumeroItemPac() {
		return numeroItemPac;
	}

	public void setNumeroItemPac(Short numeroItemPac) {
		this.numeroItemPac = numeroItemPac;
	}

	public String getDescricaoPac() {
		return descricaoPac;
	}

	public void setDescricaoPac(String descricaoPac) {
		this.descricaoPac = descricaoPac;
	}

	public Integer getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}

	public String getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getNomeItem() {
		return nomeItem;
	}

	public void setNomeItem(String nomeItem) {
		this.nomeItem = nomeItem;
	}

	public String getDescricaoItem() {
		return descricaoItem;
	}

	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	public ScoLicitacao getPac() {
		return pac;
	}

	public void setPac(ScoLicitacao pac) {
		this.pac = pac;
	}

	public ScoItemLicitacao getItemPac() {
		return itemPac;
	}

	public void setItemPac(ScoItemLicitacao itemPac) {
		this.itemPac = itemPac;
	}

	public ScoCondicaoPgtoLicitacao getCondicaoPagamento() {
		return condicaoPagamento;
	}

	public void setCondicaoPagamento(ScoCondicaoPgtoLicitacao condicaoPagamento) {
		this.condicaoPagamento = condicaoPagamento;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public Integer getSeqCondicaoPgto() {
		return seqCondicaoPgto;
	}

	public void setSeqCondicaoPgto(Integer seqCondicaoPgto) {
		this.seqCondicaoPgto = seqCondicaoPgto;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public ScoCondicaoPgtoLicitacao getCondicaoPgtoLicitacao() {
		return condicaoPgtoLicitacao;
	}

	public void setCondicaoPgtoLicitacao(ScoCondicaoPgtoLicitacao condicaoPgtoLicitacao) {
		this.condicaoPgtoLicitacao = condicaoPgtoLicitacao;
	}

	public List<ScoParcelasPagamento> getListaParcelas() {
		return listaParcelas;
	}

	public void setListaParcelas(List<ScoParcelasPagamento> listaParcelas) {
		this.listaParcelas = listaParcelas;
	}

	public List<ScoParcelasPagamento> getListaParcelasExcluidas() {
		return listaParcelasExcluidas;
	}

	public void setListaParcelasExcluidas(List<ScoParcelasPagamento> listaParcelasExcluidas) {
		this.listaParcelasExcluidas = listaParcelasExcluidas;
	}

	public Integer getNumeroFormaPagamento() {
		return numeroFormaPagamento;
	}

	public void setNumeroFormaPagamento(Integer numeroFormaPagamento) {
		this.numeroFormaPagamento = numeroFormaPagamento;
	}

	public ScoFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(ScoFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public BigDecimal getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(BigDecimal acrescimo) {
		this.acrescimo = acrescimo;
	}

	public Short getParcela() {
		return parcela;
	}

	public void setParcela(Short parcela) {
		this.parcela = parcela;
	}

	public Short getPrazo() {
		return prazo;
	}

	public void setPrazo(Short prazo) {
		this.prazo = prazo;
	}

	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public boolean isEdicaoParcela() {
		return edicaoParcela;
	}

	public void setEdicaoParcela(boolean edicaoParcela) {
		this.edicaoParcela = edicaoParcela;
	}

	public boolean isAdicionando() {
		return adicionando;
	}

	public void setAdicionando(boolean adicionando) {
		this.adicionando = adicionando;
	}

}
