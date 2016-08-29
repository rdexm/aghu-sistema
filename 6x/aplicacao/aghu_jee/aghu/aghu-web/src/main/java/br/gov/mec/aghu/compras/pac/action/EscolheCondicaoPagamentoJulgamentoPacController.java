package br.gov.mec.aghu.compras.pac.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class EscolheCondicaoPagamentoJulgamentoPacController extends ActionController {

	private static final long serialVersionUID = 6311943144533755902L;
	
	private static final Log LOG = LogFactory.getLog(EscolheCondicaoPagamentoJulgamentoPacController.class);

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasFacade comprasFacade;

	// parametros
	private Integer numeroPac;
	private Integer numeroFornecedor;
	private Short numeroItemProposta;
	private Short numeroItemPac;
	private Short numeroComplemento;

	// cabecalho
	private String descricaoPac;
	private String descricaoMaterialServico;
	private String razaoSocial;

	// URL e demais atributos para botão voltar
	private String voltarParaUrl;

	// grid
	private List<ScoCondicaoPagamentoPropos> listaFormasPagamento;
	private List<ScoParcelasPagamento> listaParcelas;
	private ScoCondicaoPagamentoPropos condicaoSelecionada;
	private ScoCondicaoPagamentoPropos condicaoPagamentoPropostaSelecionada;

	private Integer numeroCondicaoPagamentoSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String confirmarEscolha() {
		String ret = null;
		if (this.condicaoSelecionada == null) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JULGAMENTO_MSG004");
		} else {
			ret = this.voltar(false);
		}
		return ret;
	}

	public void alterarListaParcelas(ScoCondicaoPagamentoPropos item) {
		this.listaParcelas.clear();

		Integer index = this.listaFormasPagamento.indexOf(item);
		if (index >= 0) {
			this.popularParcelasPagamento(item);
		}
	}

	public void selecionarCondicao(ScoCondicaoPagamentoPropos item) {
		try {
			this.condicaoSelecionada = (ScoCondicaoPagamentoPropos) BeanUtils.cloneBean(item);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		if (this.listaParcelas != null) {
			this.listaParcelas.clear();
		} else {
			this.listaParcelas = new ArrayList<ScoParcelasPagamento>();
		}
		this.popularParcelasPagamento(item);
		this.numeroCondicaoPagamentoSelecionado = Integer.valueOf(this.condicaoSelecionada.getNumero());
	}

	public String voltar(Boolean limparEscolha) {
		if (limparEscolha) {
			this.numeroCondicaoPagamentoSelecionado = null;
		}
		return voltarParaUrl;
	}

	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}

	public void obterCondicaoPagamento() {
		this.listaFormasPagamento = this.pacFacade.pesquisarCondicaoPagamentoProposta(this.numeroFornecedor, this.numeroPac,
				this.numeroItemProposta);

		if (this.listaFormasPagamento == null) {
			this.listaFormasPagamento = new ArrayList<ScoCondicaoPagamentoPropos>();
			this.listaParcelas = new ArrayList<ScoParcelasPagamento>();
		} else {
			if (this.listaFormasPagamento != null && !this.listaFormasPagamento.isEmpty()) {
				this.selecionarCondicao(this.listaFormasPagamento.get(0));
				this.popularParcelasPagamento(this.condicaoSelecionada);
				this.numeroCondicaoPagamentoSelecionado = Integer.valueOf(this.condicaoSelecionada.getNumero());
			}
		}
	}

	public void obterDadosProposta() {
		ScoLicitacao licitacao = this.pacFacade.obterLicitacao(this.numeroPac);

		if (licitacao != null) {
			this.descricaoPac = licitacao.getNumeroDescricao();
		} else {
			this.descricaoPac = null;
		}

		if (this.numeroFornecedor != null) {
			ScoFornecedor fornecedor = this.comprasFacade.obterFornecedorPorChavePrimaria(this.numeroFornecedor);

			if (fornecedor != null) {
				this.razaoSocial = this.comprasFacade.montarRazaoSocialFornecedor(fornecedor);
			} else {
				this.razaoSocial = null;
			}
		}

		if (this.numeroItemPac != null) {
			ScoItemLicitacao itemLicitacao = this.pacFacade.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(this.numeroPac,
					this.numeroItemPac);

			if (itemLicitacao != null) {
				this.descricaoMaterialServico = this.pacFacade.obterNomeMaterialServico(itemLicitacao, true);
			} else {
				this.descricaoMaterialServico = null;
			}
		}
	}

	public void iniciar() {
	 

	 

		if (this.numeroPac != null) {
			this.obterDadosProposta();
			this.obterCondicaoPagamento();
		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARAMETROS_INVALIDOS");
		}
	
	}
	

	private void popularParcelasPagamento(ScoCondicaoPagamentoPropos condPag) {
		this.listaParcelas = this.pacFacade.obterParcelasPgtoProposta(condPag.getNumero());
		if (this.listaParcelas == null) {
			this.listaParcelas = new ArrayList<ScoParcelasPagamento>();
		}
	}

	// gets and sets

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getDescricaoPac() {
		return descricaoPac;
	}

	public void setDescricaoPac(String descricaoPac) {
		this.descricaoPac = descricaoPac;
	}	

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getDescricaoMaterialServico() {
		return descricaoMaterialServico;
	}

	public void setDescricaoMaterialServico(String descricaoMaterialServico) {
		this.descricaoMaterialServico = descricaoMaterialServico;
	}

	public List<ScoCondicaoPagamentoPropos> getListaFormasPagamento() {
		return listaFormasPagamento;
	}

	public void setListaFormasPagamento(List<ScoCondicaoPagamentoPropos> listaFormasPagamento) {
		this.listaFormasPagamento = listaFormasPagamento;
	}

	public List<ScoParcelasPagamento> getListaParcelas() {
		return listaParcelas;
	}

	public void setListaParcelas(List<ScoParcelasPagamento> listaParcelas) {
		this.listaParcelas = listaParcelas;
	}

	public ScoCondicaoPagamentoPropos getCondicaoSelecionada() {
		return condicaoSelecionada;
	}

	public void setCondicaoSelecionada(ScoCondicaoPagamentoPropos condicaoSelecionada) {
		this.condicaoSelecionada = condicaoSelecionada;
	}

	public Short getNumeroItemPac() {
		return numeroItemPac;
	}

	public void setNumeroItemPac(Short numeroItemPac) {
		this.numeroItemPac = numeroItemPac;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Short getNumeroItemProposta() {
		return numeroItemProposta;
	}

	public void setNumeroItemProposta(Short numeroItemProposta) {
		this.numeroItemProposta = numeroItemProposta;
	}

	public ScoCondicaoPagamentoPropos getCondicaoPagamentoPropostaSelecionada() {
		return condicaoPagamentoPropostaSelecionada;
	}

	public void setCondicaoPagamentoPropostaSelecionada(ScoCondicaoPagamentoPropos condicaoPagamentoPropostaSelecionada) {
		this.condicaoPagamentoPropostaSelecionada = condicaoPagamentoPropostaSelecionada;
	}

	public Integer getNumeroCondPag() {
		return numeroCondicaoPagamentoSelecionado;
	}

	public void setNumeroCondPag(Integer numeroCondPag) {
		this.numeroCondicaoPagamentoSelecionado = numeroCondPag;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}
}
