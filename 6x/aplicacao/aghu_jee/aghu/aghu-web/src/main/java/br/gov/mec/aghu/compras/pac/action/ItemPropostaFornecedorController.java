package br.gov.mec.aghu.compras.pac.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.MaterialServicoVO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.suprimentos.vo.ScoFaseSolicitacaoVO;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
public class ItemPropostaFornecedorController extends ActionController {

	private static final long serialVersionUID = 6311943154533755902L;
	
	private static final String PAGE_MANTER_MARCA_COMERCIAL = "compras-manterMarcaComercial";
	private static final String PAGE_COND_PAGAMENTO_PROP_FORN_LIST = "condPagamentoPropFornList";

	@EJB
	protected IPacFacade pacFacade;

	@EJB
	protected IComprasFacade comprasFacade;

	@EJB
	protected IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	protected IEstoqueFacade estoqueFacade;

	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;

	private ScoMarcaComercial marcaComercialInserida;

	private ScoMarcaModelo marcaModeloInserida;
	
	@Inject
	private SecurityController securityController;

	private Integer codigoFornecedor;
	private Integer numeroPac;
	private Boolean novaProposta;

	private String descricaoPac;
	private Date dataApresentacao;
	private Date dataCadastro;
	private ScoFornecedor fornecedorProposta;
	private BigDecimal valorTotalFrete;
	private Short prazoEntrega;
	private ScoPropostaFornecedor propostaFornecedor;

	private ScoFaseSolicitacaoVO faseSolicitacao;
	private String nomeSolicitacao;
	private Integer qtdItemSolicitacao;
	private String unidadeSolicitacao;
	private DominioTipoSolicitacao tipoSolicitacao;

	private Long qtdItemProposta;
	private ScoUnidadeMedida embalagemProposta;
	private Integer fatorConversao;
	private Boolean indNacional;
	private FcpMoeda moedaItemProposta;
	private String apresentacao;
	private BigDecimal valorUnitarioItemProposta;
	private String numeroOrcamento;
	private String observacao;
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo modeloComercial;
	private String codigoMaterialFornecedor;

	private String voltarParaUrl;

	private List<ScoItemPropostaVO> listaItensPropostas;
	private List<ScoItemPropostaVO> listaItensPropostasExclusao;
	private ScoItemPropostaVO itemEmEdicao;
	private Boolean propostaEmEdicao;
	private Short numeroItemProposta;

	private Boolean mostraModalMarcaComercial;
	private Boolean mostraModalModelo;
	private Boolean mostraModalExclusao;
	private Boolean mostraModalQuantidade;
	private Boolean mostraModalVoltar;
	private Boolean mostraModalCondProposta;
	private Boolean mostraModalItCondProposta;
	private ScoItemPropostaVO itemPropostaExclusao;
	private ScoItemPropostaVO itemPropostaItemCondPag;

	private Boolean bloqueiaFatorConversao = Boolean.FALSE;
	private Boolean bloqueiaEmbalagem = Boolean.FALSE;
	private Boolean propostaPersistida;
	private Boolean alteracoesPersistidas;
	private Boolean itemPropostaEscolhido;
	private Boolean direcionaNovaProposta = Boolean.FALSE;
	private Boolean bloqueiaCodMaterialFornec = Boolean.FALSE;
	
	public enum ItemPropostaFornecedorControllerExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_ITEMPROPOSTA_MSG001_INCLUSAO,MENSAGEM_ITEMPROPOSTA_MSG001_ALTERACAO}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void gravar() {
		try {
			
			if (listaItensPropostas == null || listaItensPropostas.size() == 0) {
				if (propostaEmEdicao == Boolean.FALSE) {
					throw new ApplicationBusinessException(
							ItemPropostaFornecedorControllerExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG001_INCLUSAO);								
				} else {
					throw new ApplicationBusinessException(
							ItemPropostaFornecedorControllerExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG001_ALTERACAO);
				}
			}
			
			this.pacFacade.persistirPropostaFornecedor(this.montarPropostaFornecedor(), this.propostaEmEdicao);
			this.pacFacade.gravarItemProposta(this.listaItensPropostas, this.listaItensPropostasExclusao, this.propostaEmEdicao);

			if (this.listaItensPropostasExclusao != null && this.listaItensPropostasExclusao.size() > 0) {
				this.listaItensPropostasExclusao.clear();
			}

			for (ScoItemPropostaVO item : this.listaItensPropostas) {
				item.setTipoOperacao(DominioOperacaoBanco.UPD);
			}

			this.inicializarControles();
			this.codigoFornecedor = this.fornecedorProposta.getNumero();
			this.novaProposta = Boolean.FALSE;

			if (this.propostaEmEdicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEMPROPOSTA_MSG008");
			} else {
				this.propostaPersistida = Boolean.TRUE;
				this.propostaEmEdicao = Boolean.TRUE;
				//apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEMPROPOSTA_MSG007");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao(Boolean limparCabecalho, Boolean limparEdicao, Boolean limparPac, Boolean limparForn) {
		if (limparEdicao) {
			this.itemEmEdicao = null;
			this.qtdItemProposta = null;
			this.embalagemProposta = null;
			this.fatorConversao = 1;
			this.indNacional = Boolean.TRUE;
			try {
				this.moedaItemProposta = this.pacFacade.obterMoedaPadraoItemProposta();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			this.apresentacao = null;
			this.valorUnitarioItemProposta = null;
			this.numeroOrcamento = null;
			this.observacao = null;
			this.marcaComercial = null;
			this.modeloComercial = null;
			this.codigoMaterialFornecedor = null;
		}

		if (limparCabecalho) {
			this.faseSolicitacao = null;
			this.nomeSolicitacao = null;
			this.qtdItemSolicitacao = null;
			this.unidadeSolicitacao = null;
			this.tipoSolicitacao = null;
		}
		if (limparPac) {
			this.numeroPac = null;
			this.descricaoPac = null;
		}
		if (limparForn) {
			this.dataApresentacao = null;
			this.dataCadastro = null;
			this.fornecedorProposta = null;
			this.valorTotalFrete = null;
			this.prazoEntrega = null;
		}
	}

	public void editarItemProposta(ScoItemPropostaVO item) {
		this.itemEmEdicao = item;
		this.faseSolicitacao = this.pacFacade.obterFaseVOPorNumeroLicitacaoENumeroItemLicitacao(item.getNumeroPac(),
				Short.valueOf(item.getNumeroItemPac()));		
		this.qtdItemProposta = item.getQtdItemProposta();
		this.embalagemProposta = item.getUnidadeProposta();
		this.fatorConversao = item.getFatorConversao();
		this.indNacional = item.getIndNacional();
		this.moedaItemProposta = item.getMoedaItemProposta();
		this.apresentacao = item.getApresentacao();
		this.valorUnitarioItemProposta = item.getValorUnitarioItemProposta();
		this.numeroOrcamento = item.getNumeroOrcamento();
		this.observacao = item.getObservacao();
		this.marcaComercial = item.getMarcaComercial();
		this.modeloComercial = item.getModeloComercial();
		this.codigoMaterialFornecedor = item.getCodigoMaterialFornecedor();
		this.atualizarDadosCabecalhoItem();
		this.atualizarFatorConversao();
	}

	public void excluirItemProposta() {
		Integer index = listaItensPropostas.indexOf(this.itemPropostaExclusao);
		if (index >= 0) {
			if (listaItensPropostas.remove(this.itemPropostaExclusao)) {
				if (listaItensPropostasExclusao == null) {
					listaItensPropostasExclusao = new ArrayList<ScoItemPropostaVO>();
				}
				listaItensPropostasExclusao.add(this.itemPropostaExclusao);
			}
		}
		this.alteracoesPersistidas = Boolean.FALSE;
		this.mostraModalExclusao = Boolean.FALSE;
		this.gravar();
	}

	public String adicionarMarcaComercial() {
		return PAGE_MANTER_MARCA_COMERCIAL;
	}

	public String adicionarModelo() {
		return PAGE_MANTER_MARCA_COMERCIAL;
	}
	
	public String visualizarCondicoesPagamentoProposta(){
		return PAGE_COND_PAGAMENTO_PROP_FORN_LIST;
	}

	public String voltar(Boolean retornar) {
		String ret = null;
		this.marcaComercialInserida = null;
		this.marcaModeloInserida = null;
		//this.numeroPac = null;
		this.faseSolicitacao = null;

		if (retornar) {
			this.cancelarEdicao(true, true, true, true);
			ret = voltarParaUrl;
		} else {
			this.cancelarEdicao(true, true, false, false);
		}

		return ret;
	}
	
	public void inicializarListaItensPropostas(){
		if (this.listaItensPropostas == null) {
			this.listaItensPropostas = new ArrayList<ScoItemPropostaVO>();
		}
	} 

	public void atualizarItemProposta(Boolean forceUpdate) {
		Boolean err = Boolean.FALSE;

		inicializarListaItensPropostas();

		if (!forceUpdate) {
			if (this.fornecedorProposta == null) {
				err = Boolean.TRUE;
				apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_ITEMPROPOSTA_MSG012");
			}
			ScoItemLicitacao item = faseSolicitacao.getItemLicitacao();
			item.setFasesSolicitacao(new HashSet<ScoFaseSolicitacao>(this.pacFacade.obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(item.getId().getLctNumero(), item.getId().getNumero())));
			if (item.getMotivoCancel() != null && !securityController.usuarioTemPermissao("permiteAlterCriticasSupr", "alterar")) {
				err = Boolean.TRUE;

				List<ScoFaseSolicitacao> listaFases = new ArrayList<ScoFaseSolicitacao>();
				listaFases.add(item.getFasesSolicitacao().iterator().next());
				MaterialServicoVO materialServicoVO = this.autFornecimentoFacade.obterDadosMaterialServico(listaFases);
				apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_ITEMPROPOSTA_CANCELADO_JULGADO", item.getId().getNumero(),
						materialServicoVO.getNomeMatServ());
			}
			if (getValidacaoUnidade()) {
				err = Boolean.TRUE;
			}
			if (this.itemEmEdicao == null && !err) {

				try {
					this.pacFacade.validarInsercaoItemPropostaDuplicado(this.listaItensPropostas, this.faseSolicitacao,
							this.marcaComercial, this.fornecedorProposta);
					this.pacFacade.validarInsercaoItemPropostaValorUnitario(this.valorUnitarioItemProposta);
				} catch (ApplicationBusinessException e) {
					err = Boolean.TRUE;
					apresentarExcecaoNegocio(e);
				}

			}
			if (this.pacFacade.validarQuantidadePropostaDiferenteSolicitacao(this.qtdItemProposta, this.fatorConversao,
					this.qtdItemSolicitacao) && !err) {				
				this.openDialog("modalConfirmacaoQuantidadeWG");
				err = Boolean.TRUE;
			}
		}
		if (!err) {
			ScoItemPropostaVO obj = new ScoItemPropostaVO();
			obj.setNumeroPac(this.numeroPac);
			obj.setNumeroItemPac(this.faseSolicitacao.getItemLicitacao().getId().getNumero());
			obj.setDescricaoItem(this.pacFacade.obterDescricaoMaterialServico(this.faseSolicitacao.getItemLicitacao()));
			obj.setQtdItemProposta(this.qtdItemProposta);
			obj.setUnidadeProposta(this.embalagemProposta);
			obj.setFatorConversao(this.fatorConversao);
			obj.setIndNacional(this.indNacional);
			obj.setMoedaItemProposta(this.moedaItemProposta);
			obj.setApresentacao(this.apresentacao);
			obj.setValorUnitarioItemProposta(this.valorUnitarioItemProposta);
			obj.setNumeroOrcamento(this.numeroOrcamento);
			obj.setObservacao(this.observacao);
			obj.setMarcaComercial(this.marcaComercial);
			obj.setModeloComercial(this.modeloComercial);
			obj.setIndEscolhido(Boolean.FALSE);
			obj.setCriterioEscolha(null);
			obj.setIndDesclassificado(Boolean.FALSE);
			obj.setMotivoDesclassificacao(null);
			obj.setIndAnalisadoParecerTecnico(Boolean.FALSE);
			obj.setParecerTecnicoMarca(null);
			obj.setFornecedorProposta(this.fornecedorProposta);
			if (this.itemEmEdicao == null) {
				obj.setNumeroItemProposta(this.pacFacade.obterProximoNumeroItemPropostaFornecedor(this.listaItensPropostas,
						this.listaItensPropostasExclusao, this.numeroPac, this.fornecedorProposta.getNumero()));
				obj.setItemAutorizacaoFornececimento(null);
				obj.setTipoOperacao(DominioOperacaoBanco.INS);
				listaItensPropostas.add(obj);
			} else {
				Integer index = listaItensPropostas.indexOf(this.itemEmEdicao);
				if (index >= 0) {
					obj.setNumeroItemProposta(this.listaItensPropostas.get(index).getNumeroItemProposta());
					obj.setTipoOperacao(this.listaItensPropostas.get(index).getTipoOperacao());
					if (this.listaItensPropostas.get(index).getItemAutorizacaoFornececimento() != null) {
						obj.setItemAutorizacaoFornececimento(this.listaItensPropostas.get(index)
								.getItemAutorizacaoFornececimento());
					}
					listaItensPropostas.set(index, obj);
				}
			}
			this.cancelarEdicao(true, true, false, false);
			this.alteracoesPersistidas = Boolean.TRUE;
			this.gravar();
		}
	}

	private Boolean getValidacaoUnidade() {
		Boolean err = false;

		if (!this.bloqueiaFatorConversao) {
			ScoSolicitacaoDeCompra sc = this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(faseSolicitacao.getSolicitacaoCompra().getNumero());
			sc.setMaterial(this.pacFacade.obterMaterialPorChavePrimaria(sc.getMaterial().getCodigo()));
			sc.getMaterial().setUnidadeMedida(this.comprasFacade.obterUnidadeMedidaPorId(sc.getMaterial().getUnidadeMedida().getCodigo()));
			sc.setUnidadeMedida(this.comprasFacade.obterUnidadeMedidaPorId(sc.getUnidadeMedida().getCodigo()));
			
			if (this.pacFacade.validarUnidadeMedida(sc)) {
				
				ScoMaterial material = sc.getMaterial();
				String materialId = material.getCodigo().toString();
				String scId = sc.getNumero().toString();
				String scUnidadeId = sc.getUnidadeMedida().getCodigo();
				String materialUnidadeId = material.getUnidadeMedida().getCodigo();
				apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_ITEMPROPOSTA_MSG009", materialId, scId, scUnidadeId,
						materialUnidadeId);
				err = true;
			}
			if (this.pacFacade.validarFatorConversao(this.faseSolicitacao, this.fatorConversao, this.embalagemProposta)) {
				apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_ITEMPROPOSTA_MSG002");
				err = true;
			}
		}
		return err;
	}

	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		return this.comprasFacade.listarFornecedoresAtivos(param, 0, 100, null, false);
	}

	public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(String param) {

		List<ScoFaseSolicitacaoVO> listaScoFaseSolicitacaoVO = null;
		try {
			listaScoFaseSolicitacaoVO = this.pacFacade.pesquisarItemLicitacao(param, this.numeroPac, this.listaItensPropostas);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return listaScoFaseSolicitacaoVO;

	}

	public List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(String parametro) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorCodigoDescricao(parametro);
	}

	public List<FcpMoeda> pesquisarMoeda(String parametro) {
		return this.estoqueFacade.pesquisarMoeda(parametro);
	}

	public List<ScoMarcaComercial> pesquisarMarcaComercial(String param) {
		return this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoSemLucene(param);
	}

	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricao(String param) {
		return this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricaoSemLucene(param, this.marcaComercial, true);
	}

	public void atualizarFornecedoresLista() {
		if (this.listaItensPropostas == null) {
			this.listaItensPropostas = new ArrayList<ScoItemPropostaVO>();
		}
		for (ScoItemPropostaVO item : this.listaItensPropostas) {
			item.setFornecedorProposta(this.fornecedorProposta);
		}
	}

	public Boolean verificarItemPropostaFornecedorEmAf(ScoItemPropostaVO item) {
		return this.pacFacade.verificarItemPropostaFornecedorEmAf(this.numeroPac, item.getNumeroItemProposta(),
				item.getFornecedorProposta());
	}

	public void atualizarPrazoEntrega(Boolean alteracoes) {
		ScoLicitacao licitacao = this.pacFacade.obterLicitacao(this.numeroPac);
		if (licitacao.getModalidadeLicitacao() != null && licitacao.getModalidadeLicitacao().getPrazoEntrega() != null) {
			this.prazoEntrega = licitacao.getModalidadeLicitacao().getPrazoEntrega();
		}
		if (alteracoes) {
			this.alteracoesPersistidas = Boolean.FALSE;
		}
	}

	private ScoPropostaFornecedor montarPropostaFornecedor() {
		RapServidores servidor = null;
		ScoPropostaFornecedor proposta = null;
		try {
			servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException esr) {
			apresentarExcecaoNegocio(esr);
		}
		if (!propostaPersistida) {
			proposta = new ScoPropostaFornecedor();
		} else {
			proposta = this.pacFacade.obterPropostaFornecedor(new ScoPropostaFornecedorId(this.numeroPac, this.fornecedorProposta
					.getNumero()));
		}

		if (this.fornecedorProposta != null) {
			proposta.setId(new ScoPropostaFornecedorId(this.numeroPac, this.fornecedorProposta.getNumero()));
		}

		proposta.setDtApresentacao(this.dataApresentacao);
		proposta.setDtDigitacao(this.dataCadastro);
		proposta.setDtExclusao(null);
		proposta.setFornecedor(this.fornecedorProposta);
		proposta.setServidor(servidor);
		proposta.setIndExclusao(Boolean.FALSE);
		proposta.setPrazoEntrega(this.prazoEntrega);
		proposta.setValorTotalFrete(this.valorTotalFrete);
		return proposta;
	}

	public void prepararExclusaoItemProposta(ScoItemPropostaVO item) {
		this.itemPropostaExclusao = item;
		this.setMostraModalExclusao(Boolean.TRUE);
	}

	public void atualizarDadosCabecalhoItem() {
		if (this.getFaseSolicitacao() != null) {
			this.setNomeSolicitacao(this.pacFacade.obterNomeMaterialServico(this.getFaseSolicitacao().getItemLicitacao(), true));
			this.setQtdItemSolicitacao(this.pacFacade
					.obterQuantidadeMaterialServico(this.getFaseSolicitacao().getItemLicitacao()));
			this.setUnidadeSolicitacao(this.pacFacade.obterUnidadeMaterial(this.getFaseSolicitacao().getItemLicitacao()));
			this.setTipoSolicitacao(this.pacFacade.obterTipoSolicitacao(this.getFaseSolicitacao().getItemLicitacao()));
			if (this.itemEmEdicao == null) {
				this.setQtdItemProposta(this.getQtdItemSolicitacao().longValue());
				if (this.tipoSolicitacao == DominioTipoSolicitacao.SS) {
					this.fatorConversao = 1;
					this.bloqueiaCodMaterialFornec = Boolean.TRUE;
					this.bloqueiaFatorConversao = Boolean.TRUE;
					this.bloqueiaEmbalagem = Boolean.TRUE;
					this.embalagemProposta = this.pacFacade.obterUnidadeMedidaSs();
					this.setUnidadeSolicitacao(this.pacFacade.obterUnidadeMedidaSs().getCodigo());
				} else {
					this.bloqueiaFatorConversao = Boolean.FALSE;
					this.bloqueiaEmbalagem = Boolean.FALSE;
					ScoUnidadeMedida scoUnidadeMedida = this.comprasFacade.obterScoUnidadeMedidaPorChavePrimaria(this
							.getUnidadeSolicitacao());
					this.setEmbalagemProposta(scoUnidadeMedida);
				}
				atualizarFatorConversao();
			}
		} else {
			this.setNomeSolicitacao(null);
			this.setQtdItemSolicitacao(null);
			this.setUnidadeSolicitacao(null);
			this.setTipoSolicitacao(null);
			this.setQtdItemProposta(null);
			this.setEmbalagemProposta(null);
		}
	}

	public void limparModeloComercial() {
		this.modeloComercial = null;
	}

	public void atualizarFatorConversao() {
		if (this.tipoSolicitacao.equals(DominioTipoSolicitacao.SC)
				&& this.pacFacade.validarEmbalagemProposta(this.unidadeSolicitacao, this.embalagemProposta)) {
			this.fatorConversao = 1;
			this.bloqueiaFatorConversao = Boolean.TRUE;
		} else {
			this.bloqueiaFatorConversao = (this.tipoSolicitacao == DominioTipoSolicitacao.SS);
		}
	}

	public String getHelpValorUnitario() {
		String helpMsg = "";
		if (this.tipoSolicitacao != null) {
			if (this.tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
				helpMsg = getBundle().getString("TITLE_VALOR_UNITARIO_ITEM_PROPOSTA_SC");
			} else {
				helpMsg = getBundle().getString("TITLE_VALOR_UNITARIO_ITEM_PROPOSTA_SS");
			}
		} else {
			helpMsg = getBundle().getString("TITLE_VALOR_UNITARIO_ITEM_PROPOSTA_SC");
		}

		return helpMsg;
	}

	public Boolean obterDadosProposta() {
		ScoLicitacao licitacao = null;
		ScoPropostaFornecedor proposta = null;

		if ((this.marcaComercialInserida == null && this.marcaModeloInserida == null && this.faseSolicitacao == null)
				|| (this.novaProposta && StringUtils.isBlank(this.descricaoPac))) {
			this.cancelarEdicao(true, true, false, false);
		}
		if (StringUtils.isBlank(this.descricaoPac)) {
			if (!this.novaProposta) {
				this.fornecedorProposta = this.comprasFacade.obterFornecedorPorChavePrimaria(this.codigoFornecedor);
				proposta = this.pacFacade.obterPropostaFornecedor(new ScoPropostaFornecedorId(this.numeroPac,
						this.fornecedorProposta.getNumero()));
				if (proposta != null) {
					this.setDataApresentacao(proposta.getDtApresentacao());
					this.setDataCadastro(proposta.getDtDigitacao());
					this.setValorTotalFrete(proposta.getValorTotalFrete());
					this.setPrazoEntrega(proposta.getPrazoEntrega());
					this.setDescricaoPac(proposta.getLicitacao().getDescricao());
					this.propostaPersistida = Boolean.TRUE;
				}
				if (this.marcaComercialInserida == null && this.marcaModeloInserida == null && this.faseSolicitacao == null) {
					this.listaItensPropostas = this.pacFacade.pesquisarItemPropostaPorNumeroLicitacao(this.numeroPac,
							this.fornecedorProposta);

					if (this.listaItensPropostas == null) {
						this.listaItensPropostas = new ArrayList<ScoItemPropostaVO>();
					}
				}
				this.propostaEmEdicao = Boolean.TRUE;
			} else {
				if (this.numeroPac != null) {
					licitacao = this.pacFacade.obterLicitacao(this.numeroPac);
					if (licitacao != null) {
						this.setDescricaoPac(licitacao.getDescricao());
					}
					this.fornecedorProposta = null;
					this.setDataCadastro(new Date());
					this.setDataApresentacao(new Date());
					this.valorTotalFrete = null;
					if (this.listaItensPropostas == null) {
						this.listaItensPropostas = new ArrayList<ScoItemPropostaVO>();
					} else {
						this.listaItensPropostas.clear();
					}
					this.atualizarPrazoEntrega(false);
				}
			}
		}
		if (this.marcaComercialInserida != null) {
			this.marcaComercial = this.marcaComercialInserida;
			this.alteracoesPersistidas = Boolean.FALSE;
		}
		if (this.marcaModeloInserida != null) {
			this.modeloComercial = this.marcaModeloInserida;
			this.alteracoesPersistidas = Boolean.FALSE;
		}

		this.fatorConversao = 1;

		try {
			this.moedaItemProposta = this.pacFacade.obterMoedaPadraoItemProposta();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return (licitacao != null);
	}

	private void inicializarControles() {
		this.propostaPersistida = Boolean.FALSE;
		this.propostaEmEdicao = Boolean.FALSE;
		this.mostraModalMarcaComercial = Boolean.FALSE;
		this.mostraModalModelo = Boolean.FALSE;
		this.mostraModalExclusao = Boolean.FALSE;
		this.mostraModalQuantidade = Boolean.FALSE;
		this.mostraModalVoltar = Boolean.FALSE;
		this.direcionaNovaProposta = Boolean.FALSE;
		this.mostraModalItCondProposta = Boolean.FALSE;
		this.mostraModalCondProposta = Boolean.FALSE;
		this.alteracoesPersistidas = Boolean.TRUE;
		this.itemPropostaExclusao = null;
		this.itemPropostaItemCondPag = null;
		this.itemPropostaEscolhido = Boolean.FALSE;
		this.valorUnitarioItemProposta= null;
		this.qtdItemSolicitacao = null;
		this.qtdItemProposta = null;
	}

	public void iniciar() {
		this.inicializarControles();

		if (numeroPac != null) {
			this.obterDadosProposta();
		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARAMETROS_INVALIDOS");
		}
	}

	public String visualizarCondicoesPagamentoItemProposta() {
		numeroItemProposta = this.itemPropostaItemCondPag.getNumeroItemProposta();
		this.itemPropostaEscolhido = this.itemPropostaItemCondPag.getIndEscolhido();
		return PAGE_COND_PAGAMENTO_PROP_FORN_LIST;
	}

	public void setIniciarNovaProposta() {
		if (this.listaItensPropostasExclusao != null && this.listaItensPropostasExclusao.size() > 0) {
			this.listaItensPropostasExclusao.clear();
		}
		if (this.listaItensPropostas != null && this.listaItensPropostas.size() > 0) {
			this.listaItensPropostas.clear();
		}
		this.voltar(false);
		this.novaProposta = Boolean.TRUE;
		this.direcionaNovaProposta = Boolean.FALSE;
		this.setDescricaoPac(null);
		this.iniciar();
	}
	
	public String verificarAtualizacaoPendentes(int src, Boolean testaMarca) {
		return verificarAtualizacaoPendentes(src, null, testaMarca);
	}

	public String verificarAtualizacaoPendentes(int src, ScoItemPropostaVO item, Boolean testaMarca) {
		String ret = null;
		if (!testaMarca) {
			if (this.alteracoesPersistidas) {
				switch (src) {
				case 0:
					cancelarEdicao(true, true, true, true);
					ret = voltarParaUrl;
					break;
				case 1:
					ret = PAGE_COND_PAGAMENTO_PROP_FORN_LIST;
					this.propostaPersistida = Boolean.TRUE;
					this.setDescricaoPac(null);
					break;
				case 2:
					this.itemPropostaItemCondPag = item;
					ret = this.visualizarCondicoesPagamentoItemProposta();
					break;
				case 3:
					this.setIniciarNovaProposta();
					break;
				}
			} else {
				switch (src) {
				case 0: // modal voltar					
					this.openDialog("modalConfirmacaoVoltarWG");
					this.direcionaNovaProposta = Boolean.FALSE;
					break;
				case 1: // modal condicao pagamento proposta					
					this.openDialog("modalConfirmacaoCondPropostaWG");
					break;
				case 2: // modal item condicao pagamento proposta
					this.itemPropostaItemCondPag = item;					
					this.openDialog("modalConfirmacaoItCondPropostaWG");
					break;
				case 3:					
					this.openDialog("modalConfirmacaoVoltarWG");
					this.direcionaNovaProposta = Boolean.TRUE;
					break;
				}
			}
		} else {
			if (this.marcaComercial == null) {
				this.closeDialog("modalConfirmacaoModeloWG");
				apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_VALIDA_MARCAMODELO");
			} else {
			    this.openDialog("modalConfirmacaoModeloWG");
			}
		}
		return ret;
	}

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

	public Date getDataApresentacao() {
		return dataApresentacao;
	}

	public void setDataApresentacao(Date dataApresentacao) {
		this.dataApresentacao = dataApresentacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public ScoFornecedor getFornecedorProposta() {
		return fornecedorProposta;
	}

	public void setFornecedorProposta(ScoFornecedor fornecedorProposta) {
		this.fornecedorProposta = fornecedorProposta;
	}

	public BigDecimal getValorTotalFrete() {
		return valorTotalFrete;
	}

	public void setValorTotalFrete(BigDecimal valorTotalFrete) {
		this.valorTotalFrete = valorTotalFrete;
	}

	public Short getPrazoEntrega() {
		return prazoEntrega;
	}

	public void setPrazoEntrega(Short prazoEntrega) {
		this.prazoEntrega = prazoEntrega;
	}

	public Long getQtdItemProposta() {
		return qtdItemProposta;
	}

	public void setQtdItemProposta(Long qtdItemProposta) {
		this.qtdItemProposta = qtdItemProposta;
	}

	public ScoFaseSolicitacaoVO getFaseSolicitacao() {
		return faseSolicitacao;
	}

	public void setFaseSolicitacao(ScoFaseSolicitacaoVO faseSolicitacao) {
		this.faseSolicitacao = faseSolicitacao;
	}

	public Integer getQtdItemSolicitacao() {
		return qtdItemSolicitacao;
	}

	public void setQtdItemSolicitacao(Integer qtdItemSolicitacao) {
		this.qtdItemSolicitacao = qtdItemSolicitacao;
	}

	public String getNomeSolicitacao() {
		return nomeSolicitacao;
	}

	public void setNomeSolicitacao(String nomeSolicitacao) {
		this.nomeSolicitacao = nomeSolicitacao;
	}

	public String getUnidadeSolicitacao() {
		return unidadeSolicitacao;
	}

	public void setUnidadeSolicitacao(String unidadeSolicitacao) {
		this.unidadeSolicitacao = unidadeSolicitacao;
	}

	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public ScoUnidadeMedida getEmbalagemProposta() {
		return embalagemProposta;
	}

	public void setEmbalagemProposta(ScoUnidadeMedida embalagemProposta) {
		this.embalagemProposta = embalagemProposta;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Boolean getIndNacional() {
		return indNacional;
	}

	public void setIndNacional(Boolean indNacional) {
		this.indNacional = indNacional;
	}

	public FcpMoeda getMoedaItemProposta() {
		return moedaItemProposta;
	}

	public void setMoedaItemProposta(FcpMoeda moedaItemProposta) {
		this.moedaItemProposta = moedaItemProposta;
	}

	public BigDecimal getValorUnitarioItemProposta() {
		return valorUnitarioItemProposta;
	}

	public void setValorUnitarioItemProposta(BigDecimal valorUnitarioItemProposta) {
		this.valorUnitarioItemProposta = valorUnitarioItemProposta;
	}

	public String getNumeroOrcamento() {
		return numeroOrcamento;
	}

	public void setNumeroOrcamento(String numeroOrcamento) {
		this.numeroOrcamento = numeroOrcamento;
	}

	public String getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(String apresentacao) {
		this.apresentacao = apresentacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<ScoItemPropostaVO> getListaItensPropostas() {
		return listaItensPropostas;
	}

	public void setListaItensPropostas(List<ScoItemPropostaVO> listaItensPropostas) {
		this.listaItensPropostas = listaItensPropostas;
	}

	public Integer getCodigoFornecedor() {
		return codigoFornecedor;
	}

	public void setCodigoFornecedor(Integer codigoFornecedor) {
		this.codigoFornecedor = codigoFornecedor;
	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public ScoMarcaComercial getMarcaComercialInserida() {
		return marcaComercialInserida;
	}

	public void setMarcaComercialInserida(ScoMarcaComercial marcaComercialInserida) {
		this.marcaComercialInserida = marcaComercialInserida;
	}

	public String getCodigoMaterialFornecedor() {
		return codigoMaterialFornecedor;
	}

	public void setCodigoMaterialFornecedor(String codigoMaterialFornecedor) {
		this.codigoMaterialFornecedor = codigoMaterialFornecedor;
	}

	public List<ScoItemPropostaVO> getListaItensPropostasExclusao() {
		return listaItensPropostasExclusao;
	}

	public void setListaItensPropostasExclusao(List<ScoItemPropostaVO> listaItensPropostasExclusao) {
		this.listaItensPropostasExclusao = listaItensPropostasExclusao;
	}

	public ScoItemPropostaVO getItemEmEdicao() {
		return itemEmEdicao;
	}

	public void setItemEmEdicao(ScoItemPropostaVO itemEmEdicao) {
		this.itemEmEdicao = itemEmEdicao;
	}

	public Boolean getPropostaEmEdicao() {
		return propostaEmEdicao;
	}

	public void setPropostaEmEdicao(Boolean propostaEmEdicao) {
		this.propostaEmEdicao = propostaEmEdicao;
	}

	public Boolean getMostraModalMarcaComercial() {
		return mostraModalMarcaComercial;
	}

	public void setMostraModalMarcaComercial(Boolean mostraModalMarcaComercial) {
		this.mostraModalMarcaComercial = mostraModalMarcaComercial;
	}

	public Boolean getMostraModalExclusao() {
		return mostraModalExclusao;
	}

	public void setMostraModalExclusao(Boolean mostraModalExclusao) {
		this.mostraModalExclusao = mostraModalExclusao;
	}

	public ScoItemPropostaVO getItemPropostaExclusao() {
		return itemPropostaExclusao;
	}

	public void setItemPropostaExclusao(ScoItemPropostaVO itemPropostaExclusao) {
		this.itemPropostaExclusao = itemPropostaExclusao;
	}

	public ScoPropostaFornecedor getPropostaFornecedor() {
		return propostaFornecedor;
	}

	public void setPropostaFornecedor(ScoPropostaFornecedor propostaFornecedor) {
		this.propostaFornecedor = propostaFornecedor;
	}

	public Boolean getMostraModalQuantidade() {
		return mostraModalQuantidade;
	}

	public void setMostraModalQuantidade(Boolean mostraModalQuantidade) {
		this.mostraModalQuantidade = mostraModalQuantidade;
	}

	public Boolean getBloqueiaFatorConversao() {
		return bloqueiaFatorConversao;
	}

	public void setBloqueiaFatorConversao(Boolean bloqueiaFatorConversao) {
		this.bloqueiaFatorConversao = bloqueiaFatorConversao;
	}

	public Short getNumeroItemProposta() {
		return numeroItemProposta;
	}

	public void setNumeroItemProposta(Short numeroItemProposta) {
		this.numeroItemProposta = numeroItemProposta;
	}

	public Boolean getPropostaPersistida() {
		return propostaPersistida;
	}

	public void setPropostaPersistida(Boolean propostaPersistida) {
		this.propostaPersistida = propostaPersistida;
	}

	public Boolean getMostraModalVoltar() {
		return mostraModalVoltar;
	}

	public void setMostraModalVoltar(Boolean mostraModalVoltar) {
		this.mostraModalVoltar = mostraModalVoltar;
	}

	public Boolean getAlteracoesPersistidas() {
		return alteracoesPersistidas;
	}

	public void setAlteracoesPersistidas(Boolean alteracoesPersistidas) {
		this.alteracoesPersistidas = alteracoesPersistidas;
	}

	public Boolean getNovaProposta() {
		return novaProposta;
	}

	public void setNovaProposta(Boolean novaProposta) {
		this.novaProposta = novaProposta;
	}

	public ScoMarcaModelo getModeloComercial() {
		return modeloComercial;
	}

	public void setModeloComercial(ScoMarcaModelo modeloComercial) {
		this.modeloComercial = modeloComercial;
	}

	public ScoMarcaModelo getMarcaModeloInserida() {
		return marcaModeloInserida;
	}

	public void setMarcaModeloInserida(ScoMarcaModelo marcaModeloInserida) {
		this.marcaModeloInserida = marcaModeloInserida;
	}

	public Boolean getMostraModalCondProposta() {
		return mostraModalCondProposta;
	}

	public void setMostraModalCondProposta(Boolean mostraModalCondProposta) {
		this.mostraModalCondProposta = mostraModalCondProposta;
	}

	public Boolean getMostraModalItCondProposta() {
		return mostraModalItCondProposta;
	}

	public void setMostraModalItCondProposta(Boolean mostraModalItCondProposta) {
		this.mostraModalItCondProposta = mostraModalItCondProposta;
	}

	public ScoItemPropostaVO getItemPropostaItemCondPag() {
		return itemPropostaItemCondPag;
	}

	public void setItemPropostaItemCondPag(ScoItemPropostaVO itemPropostaItemCondPag) {
		this.itemPropostaItemCondPag = itemPropostaItemCondPag;
	}

	public Boolean getMostraModalModelo() {
		return mostraModalModelo;
	}

	public void setMostraModalModelo(Boolean mostraModalModelo) {
		this.mostraModalModelo = mostraModalModelo;
	}

	public Boolean getItemPropostaEscolhido() {
		return itemPropostaEscolhido;
	}

	public void setItemPropostaEscolhido(Boolean itemPropostaEscolhido) {
		this.itemPropostaEscolhido = itemPropostaEscolhido;
	}

	public Boolean getBloqueiaEmbalagem() {
		return bloqueiaEmbalagem;
	}

	public void setBloqueiaEmbalagem(Boolean bloqueiaEmbalagem) {
		this.bloqueiaEmbalagem = bloqueiaEmbalagem;
	}

	public Boolean getDirecionaNovaProposta() {
		return direcionaNovaProposta;
	}

	public void setDirecionaNovaProposta(Boolean direcionaNovaProposta) {
		this.direcionaNovaProposta = direcionaNovaProposta;
	}

	public Boolean getBloqueiaCodMaterialFornec() {
		return bloqueiaCodMaterialFornec;
	}

	public void setBloqueiaCodMaterialFornec(Boolean bloqueiaCodMaterialFornec) {
		this.bloqueiaCodMaterialFornec = bloqueiaCodMaterialFornec;
	}
}