package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.PersistenciaProgEntregaItemAfVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ProgrEntregaItensAfVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO.TipoProgramacaoEntrega;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ProgrEntregaItensAfController extends ActionController {

	private static final long serialVersionUID = 4491825107167582869L;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SolicProgramacaoEntregaController solicProgramacaoEntregaController;
	
	
	// parametros
	private Integer numeroAf;
	private Short numeroItem;
	private Short numeroSeq;
	private Short numeroParcela;
	private String voltarParaUrl;
	
	// campos tela
	private ProgrEntregaItensAfVO vo;
		
	// listas
	private List<ParcelaItemAutFornecimentoVO> listaPea;		
	private List<ParcelaItemAutFornecimentoVO> listaPeaExclusao;	
	
	// controles de tela
	private Boolean ativo;
	private ParcelaItemAutFornecimentoVO itemEmEdicao;
	private ParcelaItemAutFornecimentoVO itemExclusao;
	private Boolean alteracoesPersistidas;
	private Integer maxRowId;
	private ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutForn;
	private ScoAutorizacaoForn af;
	private ScoItemAutorizacaoForn itemAf;
	private Boolean novoRegistro;
	private Integer numParcelaDuplicada;
	private Boolean indPrioridadeAlterado;
	private Boolean vincularNovasSolicitacoes;
	
	// atributos privados
	private FccCentroCustos	centroCustoOriginal;
	private FsoVerbaGestao verbaGestaoOriginal;

	/** Itens da grade que devem ser removidos por geraram novo complemento da AF. */
	private List<ParcelaItemAutFornecimentoVO> listaPeaGerados;

	private boolean isItemAfGeradoAutomaticamente = false;

	private boolean progEntregaItemAfPersistida;
	

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public List<ScoSolicitacaoServico> pesquisarSolicServicoCodigoDescricao(String filter) {
		return this.solicitacaoServicoFacade.pesquisarSolicServicoCodigoDescricao(filter, 
				vo.getServico(), this.af.getNaturezaDespesa());
	}
	
	public List<ScoSolicitacaoDeCompra> pesquisarSolicCompraCodigoDescricao(
			String filter) {
		return this.solicitacaoComprasFacade.pesquisarSolicCompraCodigoDescricao(filter, 
				vo.getMaterial(), this.af.getNaturezaDespesa());
	}
	
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(String objParam) {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(objParam);
	}
		
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(String parametro) {			
		return this.centroCustoFacade.pesquisarCentroCustoUsuarioGerarSCSuggestion(parametro);
	}

	// botoes
	public void atualizarLista(){
		// Realiza verificações com Verba de Gestão e Centro de Custo
		// somente na inclusão de novos registros à grade
		if (this.itemEmEdicao == null){
			if (vo.getSolicitacaoCompra() != null
					|| vo.getSolicitacaoServico() != null) {
				if (vo.getVerbaGestao() != null
						&& !vo.getVerbaGestao().equals(
								this.af.getVerbaGestao())) {
					solicProgramacaoEntregaController
							.setMostraModalVerbaGestaoSolicitacao(Boolean.TRUE);
					return;
				}
				
				if (vo.getCentroCusto() != null
						&& !vo.getCentroCusto().equals(
								getCentroCustoOriginal())) {
					solicProgramacaoEntregaController
							.setMostraModalCentroCustoSolicitacao(Boolean.TRUE);
					return;
				}
			} else {
				if (vo.getVerbaGestao() != null
						&& !vo.getVerbaGestao().equals(
								this.af.getVerbaGestao())) {
					solicProgramacaoEntregaController
							.setMostraModalVerbaGestao(Boolean.TRUE);
					return;
				}
				
				if (vo.getCentroCusto() != null
						&& !vo.getCentroCusto().equals(
								getCentroCustoOriginal())) {
					solicProgramacaoEntregaController.setMostraModalCentroCusto(Boolean.TRUE);
					return;
				}
			}
			
			if (this.isSc()){
			  if (vo.getQtdeDetalhada() == null){
			      this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_QTDE_LIBERADA_CRUD"));
			      return;
			  }			  
			}
			else {
			    if (vo.getValorDetalhado() == null){
			      this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_VALOR_LIBERADO_CRUD"));
			      return;
			  }	
			}
		}
		
		// Caso não houver necessidade de alteração no Centro de Custo e/ou Verba de Gestão insere na grade sem modificações
		adicionarNaGrade(null);
	}
		
	public void adicionarNaGrade(TipoProgramacaoEntrega tipoProgramacaoEntrega){
		if (this.listaPea == null) {
			this.listaPea = new ArrayList<ParcelaItemAutFornecimentoVO>();
		}

		ParcelaItemAutFornecimentoVO obj = this.montarObj(tipoProgramacaoEntrega);
		
		if (this.itemEmEdicao == null) {
			listaPea.add(obj);
		} else {
			Integer index = listaPea.indexOf(this.itemEmEdicao);
			if (index >= 0) {					
				listaPea.set(index, obj);
			}
		}
		this.autFornecimentoFacade.calcularPrioridadeEntrega(listaPea, vo.getTipoSolicitacao());
		this.limpar(false, true, false);
		this.setAlteracoesPersistidas(Boolean.FALSE);
	}
	
	public void gravar(Boolean forcePersist) throws BaseException {
		Double vlLiberar = 0.00;
		Integer qtLiberar = 0; 
		
		if (vo.getValorLiberar() != null) {
			vlLiberar = vo.getValorLiberar().doubleValue();
		}
		
		if (vo.getQtdeLiberar() != null) {
			qtLiberar = vo.getQtdeLiberar();
		}
		
		ParcelaItemAutFornecimentoVO obj = this.montarObj(null);
		
		this.numParcelaDuplicada = this.autFornecimentoFacade
				.validarDataPrevisaoEntregaDuplicada(this.numeroAf,
						Integer.valueOf(this.numeroItem),
						Integer.valueOf(this.numeroParcela),
						vo.getPrevisaoEntrega());
				
		if (this.numParcelaDuplicada == null || forcePersist) {		
			try {		

				ScoProgEntregaItemAutorizacaoFornecimentoId id = this.obterProgEntregaItemId(obj.getIafAfnNumero(), obj.getIafNumero(), 
						obj.getParcela(), obj.getSeq());
				
				listaPeaGerados = new ArrayList<ParcelaItemAutFornecimentoVO>();
				
				PersistenciaProgEntregaItemAfVO retorno = this.autFornecimentoFacade.persistirProgEntregaItemAutorizacaoFornecimento(
								this.listaPea, this.listaPeaExclusao,
								id, vo.getPrevisaoEntrega(), vo.getValorParcelaAf().doubleValue(), 
								vo.getQtdeParcelaAf(), vo.getIndPlanejada(), vo.getTipoSolicitacao(),
								this.novoRegistro, vlLiberar, qtLiberar);
				
				if(!retorno.getExisteEntregaProgramada()){

					List<ParcelaItemAutFornecimentoVO> lista = new ArrayList<ParcelaItemAutFornecimentoVO>(listaPea);
					if (lista.isEmpty()) {
						ParcelaItemAutFornecimentoVO parcela = new ParcelaItemAutFornecimentoVO();
						parcela.setSeqDetalhe(null);
						parcela.setIafAfnNumero(retorno.getProgEntrega().getId().getIafAfnNumero());
						parcela.setIafNumero(Integer.valueOf(retorno.getProgEntrega().getId().getIafNumero()));
						parcela.setIndPrioridade((short)1);
						parcela.setSeq(Integer.valueOf(retorno.getProgEntrega().getId().getSeq()));
						parcela.setParcela(Integer.valueOf(retorno.getProgEntrega().getId().getParcela()));					
						if (vo.getTipoSolicitacao() == DominioTipoSolicitacao.SC) {
							parcela.setQtdeDetalhada(retorno.getProgEntrega().getQtde());
							parcela.setQtdeEntregue(retorno.getProgEntrega().getQtdeEntregue());
						} else {
							parcela.setValorDetalhado(retorno.getProgEntrega().getValorTotal());
							parcela.setValorEfetivado(retorno.getProgEntrega().getValorEfetivado());
						}
						lista.add(parcela);
					} 		
					
					for (ParcelaItemAutFornecimentoVO parcela : listaPea) {
						ScoAutorizacaoForn novaAf = this.autFornecimentoFacade.montarSolicitacaoProgEntrega(parcela, vo.getTipoSolicitacao(),
								retorno.getProgEntrega(), vo.getPrevisaoEntrega(), vo.getValorParcelaAf().doubleValue(),
								vo.getQtdeParcelaAf(),  vo.getIndPlanejada(), id);
						if(novaAf != null){
							this.addComplementoAfStatusMessage(parcela, novaAf);
						}
					}
					
					this.autFornecimentoFacade.excluirListaSolicitacaoProgramacao(listaPeaExclusao);
				}
				
				this.setProgEntregaItemAfPersistida(retorno.getPersistirParcela());
				
				for (ParcelaItemAutFornecimentoVO pea : listaPeaGerados) {
					listaPea.remove(pea);
				}
				
				listaPeaGerados = null;
				
				if (this.novoRegistro) {
					this.progEntregaItemAutForn = this.comprasFacade.obterProgEntregaAutorizacaoFornecimentoPorId(
							this.obterProgEntregaItemId(this.numeroAf, Integer.valueOf(this.numeroItem), 
									Integer.valueOf(this.numeroParcela), Integer.valueOf(this.numeroSeq)));
					if (this.listaPea.isEmpty()) {
						this.popularGrade();
					}
					apresentarMsgNegocio(
							Severity.INFO, "MENSAGEM_INCLUSAO_PROG_ENTREGA_AF_OK");
				} else {
					apresentarMsgNegocio(
							Severity.INFO, "MENSAGEM_ALTERACAO_PROG_ENTREGA_AF_OK");
				}
				
				this.listaPeaExclusao = new ArrayList<ParcelaItemAutFornecimentoVO>();				
				this.alteracoesPersistidas = Boolean.TRUE;
				
				if (!Boolean.TRUE.equals(progEntregaItemAfPersistida) || 
						(Boolean.TRUE.equals(novoRegistro) && !Boolean.TRUE.equals(vincularNovasSolicitacoes))) {
					numeroSeq = null;
					numeroParcela = null;
					vincularNovasSolicitacoes = null;
				}

				iniciar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} 
		} else {
			super.openDialog("modalConfirmacaoPrevisaoDuplicadaWG");			
		}
	}
	
	/**
	 * Exibe mensagem de complemento de AF gerado com sucesso.
	 * 
	 * @param novaAf
	 *            Complemento de AF gerado.
	 */
	public void addComplementoAfStatusMessage(
			ParcelaItemAutFornecimentoVO parcela, ScoAutorizacaoForn novaAf) {
		listaPeaGerados.add(parcela);

		apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_GERACAO_COMPLEMENTO_AF_OK",
				novaAf.getNroComplemento().toString(),
				novaAf.getPropostaFornecedor().getId().getLctNumero().toString());
	}
	
	/**
	 * Define se parcelamento de entrega foi gravado ou se foram apenas gerados outros parcelamentos.
	 * 
	 * @param flag Flag
	 */
	public void setProgEntregaItemAfPersistida(boolean flag) {
		progEntregaItemAfPersistida = flag;
	}
	
	public void editar(ParcelaItemAutFornecimentoVO item) {
		this.itemEmEdicao = item;			
		vo.setIndPrioridade(Integer.valueOf(item.getIndPrioridade()));	
		vo.setSeqDetalhe(item.getSeqDetalhe());		
		this.indPrioridadeAlterado = Boolean.FALSE;
		if (item.getSolicitacaoServico() != null) {
			vo.setSolicitacaoServico(item.getSolicitacaoServico());
			vo.setServico(item.getSolicitacaoServico().getServico());			
		}
		if (item.getSolicitacaoCompra() != null) {
			vo.setSolicitacaoCompra(item.getSolicitacaoCompra());
			vo.setMaterial(item.getSolicitacaoCompra().getMaterial());			
		}
		if (item.getQtdeDetalhada() != null) {
			vo.setQtdeDetalhada(item.getQtdeDetalhada());
		}
		if (item.getValorDetalhado() != null) {
			vo.setValorDetalhado(new BigDecimal(item.getValorDetalhado()));
		}		
		if (item.getCentroCusto() != null) {
			vo.setCentroCusto(item.getCentroCusto());
		}		
		if (item.getVerbaGestao() != null) {
			vo.setVerbaGestao(item.getVerbaGestao());
		}
	}
	
	public void excluir() {
		Integer index = listaPea.indexOf(this.itemExclusao);
		if (index >= 0) {
			if (listaPea.remove(this.itemExclusao)) {
				if (this.itemExclusao.getSeqDetalhe() != null) {
					if (listaPeaExclusao == null) {
						listaPeaExclusao = new ArrayList<ParcelaItemAutFornecimentoVO>();			
					}
					listaPeaExclusao.add(this.itemExclusao);
				}
			}
		}
		this.autFornecimentoFacade.calcularPrioridadeEntrega(this.listaPea, vo.getTipoSolicitacao());
		this.setAlteracoesPersistidas(Boolean.FALSE);
	}
	
	// metodos de auxilio
	private void popularObjValores(ParcelaItemAutFornecimentoVO obj) {
		obj.setQtdeDetalhada(getQtdeDetalhadaOrDefault());
		obj.setValorDetalhado(getValorDetalhadoOrDefault());
		if (this.novoRegistro) {
			obj.setQtdeEntregue(0);
			obj.setValorEfetivado(0.00);			
		} else {
			if (this.progEntregaItemAutForn.getQtdeEntregue() != null) {
				obj.setQtdeEntregue(this.progEntregaItemAutForn.getQtdeEntregue());
			}
			if (this.progEntregaItemAutForn.getValorEfetivado() != null) {
				obj.setValorEfetivado(this.progEntregaItemAutForn.getValorEfetivado());
			}		
		}
	}

	/** Obtem quantidade detalhada ou zero (padrão). */
	private int getQtdeDetalhadaOrDefault() {
		if (vo.getQtdeDetalhada() != null) {
			return vo.getQtdeDetalhada();
		} else if (DominioTipoSolicitacao.SS.equals(vo.getTipoSolicitacao())) {
			return 1;
		} else {
			return 0;
		}
	}

	/** Obtem valor detalhado ou zero (padrão). */
	private Double getValorDetalhadoOrDefault() {
		return vo.getValorDetalhado() != null ? vo
				.getValorDetalhado().doubleValue() : 0.00;
	}

	public ParcelaItemAutFornecimentoVO montarObj(TipoProgramacaoEntrega tipoProgramacaoEntrega) {
		ParcelaItemAutFornecimentoVO obj = new ParcelaItemAutFornecimentoVO();
		
		if (itemEmEdicao != null) {
			obj.setSeqDetalhe(vo.getSeqDetalhe());
		}
		
		if (TipoProgramacaoEntrega.ALTERAR_SOLICITACAO.equals(tipoProgramacaoEntrega)) {
			obj.setVerbaGestao(cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(this.af.getVerbaGestao().getSeq()));
			obj.setCentroCusto(getCentroCustoOriginal());
		} else {
			if (vo.getCentroCusto() == null) {
				obj.setCentroCusto(getCentroCustoOriginal());
			} else {
				obj.setCentroCusto(vo.getCentroCusto());
			}

			if (vo.getVerbaGestao() == null) {
				obj.setVerbaGestao(getVerbaGestaoOriginal());
			} else {				
				obj.setVerbaGestao(cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(vo.getVerbaGestao().getSeq()));
			}
		}
		
		if (tipoProgramacaoEntrega != null){
			obj.setTipoProgramacaoEntrega(tipoProgramacaoEntrega);
		} else {
			if (isSc()){  
				obj.setSolicitacaoCompra(autFornecimentoFacade.pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), false).get(0).getSolicitacaoDeCompra());				
			} else {
				obj.setSolicitacaoServico(autFornecimentoFacade.pesquisarFaseSolicitacaoServicoPorAutorizacaoFornecimento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), false).get(0).getSolicitacaoServico());
			}
		}
		
		obj.setRowId(this.maxRowId);
		this.maxRowId++;
		obj.setIafAfnNumero(this.numeroAf);
		obj.setIafNumero(Integer.valueOf(this.numeroItem));
		obj.setSeq(Integer.valueOf(this.numeroSeq));
		obj.setParcela(Integer.valueOf(this.numeroParcela));		
		obj.setIndPrioridade(vo.getIndPrioridade().shortValue());
		obj.setIndPrioridadeAlterada(this.indPrioridadeAlterado);
		
		if (vo.getSolicitacaoCompra() != null) {
			obj.setSolicitacaoCompra(vo.getSolicitacaoCompra());
			obj.setMaterial(vo.getSolicitacaoCompra().getMaterial());
		}
		
		if (vo.getSolicitacaoServico() != null) {
			obj.setSolicitacaoServico(vo.getSolicitacaoServico());
			obj.setServico(vo.getSolicitacaoServico().getServico());
		}
		this.popularObjValores(obj);
		return obj;
	}

	private void popularCamposAf() {
		if (Boolean.TRUE.equals(novoRegistro)) {
			vo.setIndPlanejada(DominioSimNao.N);
		}
		
		this.af = this.autFornecimentoFacade.obterAfByNumero(this.numeroAf);
		if (this.af != null) {
			vo.setNumeroComplemento(this.af.getNroComplemento());			
			vo.setNumeroProposta(this.af.getPropostaFornecedor().getId().getLctNumero());
			vo.setNaturezaDespesa(this.af.getNaturezaDespesa());
		}
		this.itemAf = this.solicitacaoComprasFacade.obterDadosItensAutorizacaoFornecimento(this.numeroAf, Integer.valueOf(this.numeroItem));
		if (this.itemAf != null) {
			if (Objects.equals(this.comprasFacade.obterTipoFaseSolicitacaoPorNumeroAF(itemAf.getId().getAfnNumero()), DominioTipoFaseSolicitacao.C)) {
				vo.setTipoSolicitacao(DominioTipoSolicitacao.SC);
				vo.setMaterial(autFornecimentoFacade.pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), false).get(0).getSolicitacaoDeCompra().getMaterial());					
			} else {
				vo.setTipoSolicitacao(DominioTipoSolicitacao.SS);
				vo.setServico(autFornecimentoFacade.pesquisarFaseSolicitacaoServicoPorAutorizacaoFornecimento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), false).get(0).getSolicitacaoServico().getServico());
			}
			vo.setNumeroItemLicitacao(itemAf.getItemPropostaFornecedor().getItemLicitacao().getId().getNumero());
			vo.setDescricaoSolicitacao(this.pacFacade.obterNomeMaterialServico(this.itemAf.getItemPropostaFornecedor().getItemLicitacao(), true));					
		}
	}		
	
	private ScoProgEntregaItemAutorizacaoFornecimentoId obterProgEntregaItemId(Integer afnNumero, Integer numeroItem, Integer numeroParcela, Integer numeroSeq) {
		ScoProgEntregaItemAutorizacaoFornecimentoId id = new ScoProgEntregaItemAutorizacaoFornecimentoId();
		id.setIafAfnNumero(afnNumero);
		id.setIafNumero(Integer.valueOf(numeroItem));
		id.setParcela(Integer.valueOf(numeroParcela));
		id.setSeq(Integer.valueOf(numeroSeq));
		return id;
	}
	
	private void obterListaProgEntrega() {
		
		List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas;
		this.ativo = Boolean.TRUE;
		listaParcelas = this.comprasFacade.pesquisaProgEntregaItemAf(this.numeroAf, Integer.valueOf(this.numeroItem), false, false);
		if (listaParcelas != null) {
			if (vo.getTipoSolicitacao() == DominioTipoSolicitacao.SC) {
				Integer qtdeProgramada = this.autFornecimentoFacade.obterQtdeProgramadaProgEntregaItemAf(listaParcelas);
				vo.setQtdeLiberar(this.itemAf.getQtdeSolicitada() - qtdeProgramada);
			} else {
				BigDecimal valorProgramado = this.autFornecimentoFacade.obterValorProgramadoProgEntregaItemAf(listaParcelas);
				vo.setValorLiberar(new BigDecimal(this.itemAf.getValorUnitario()).subtract(valorProgramado));
			}
		}
		if (!this.novoRegistro) {			
			this.progEntregaItemAutForn = this.comprasFacade.obterProgEntregaAutorizacaoFornecimentoPorId(
					this.obterProgEntregaItemId(this.numeroAf, Integer.valueOf(this.numeroItem), Integer.valueOf(this.numeroParcela), Integer.valueOf(this.numeroSeq)));
			if (this.progEntregaItemAutForn.getQtde() != null) {
				vo.setQtdeParcelaAf(this.progEntregaItemAutForn.getQtde());
			}
			if (this.progEntregaItemAutForn.getValorTotal() != null) {
				vo.setValorParcelaAf(new BigDecimal(this.progEntregaItemAutForn.getValorTotal()));
			}
			if (this.progEntregaItemAutForn.getDtPrevEntrega() != null) {
				vo.setPrevisaoEntrega(this.progEntregaItemAutForn.getDtPrevEntrega()); 
			}
			if (this.progEntregaItemAutForn.getIndPlanejamento() != null) {
				vo.setIndPlanejada(this.progEntregaItemAutForn.getIndPlanejamento() ? DominioSimNao.S : DominioSimNao.N);
			}
		} else {
			if (vo.getTipoSolicitacao() == DominioTipoSolicitacao.SC) {
				vo.setValorParcelaAf(BigDecimal.ZERO);
			} else {
				vo.setQtdeParcelaAf(0);
			}
		}
		
		PARCELA_LOOP: for (ScoProgEntregaItemAutorizacaoFornecimento parcela : listaParcelas) {			
			if (parcela.getSolicitacoesProgEntrega() != null) {
				parcela.setSolicitacoesProgEntrega(new HashSet<ScoSolicitacaoProgramacaoEntrega>(this.autFornecimentoFacade.pesquisarSolicitacaoProgEntregaPorItemProgEntrega(parcela.getId().getIafAfnNumero(), 
                        parcela.getId().getIafNumero(), 
                        parcela.getId().getSeq(), 
                        parcela.getId().getParcela())));

				for (ScoSolicitacaoProgramacaoEntrega entrega : parcela.getSolicitacoesProgEntrega()) {
					if (entrega.getItemAfOrigem() != null) {
						isItemAfGeradoAutomaticamente = true;
						break PARCELA_LOOP;
					}
				}
			}
		}
	}

	public void limpar(Boolean limparParam, Boolean limparEdicao, Boolean limparCabecalho) {
		if (limparParam) {
			this.numeroAf = null;
			this.numeroItem= null;
			this.numeroSeq= null;
			this.numeroParcela= null;			
		}	
		if (limparEdicao) {
			this.numParcelaDuplicada = null;
			vo.setSeqDetalhe(null); 
			this.itemEmEdicao = null;			
			vo.setQtdeDetalhada(null);
			vo.setValorDetalhado(null);			
			vo.setVerbaGestao(null);
			vo.setIndPrioridade(1);
			vo.setCentroCusto(null);
			vo.setSolicitacaoServico(null);
			vo.setSolicitacaoCompra(null);			
		}
		if (limparCabecalho) {
			vo.setNumeroComplemento(null);
			vo.setNumeroProposta(null);
			vo.setTipoSolicitacao(null);
			vo.setDescricaoSolicitacao(null);
			vo.setQtdeParcelaAf(null);
			vo.setValorParcelaAf(null);
			vo.setIndPlanejada(null);
			vo.setPrevisaoEntrega(null);
			vo.setQtdeLiberar(null);	
			vo.setValorLiberar(null);
			this.listaPea = new ArrayList<ParcelaItemAutFornecimentoVO>();
			this.listaPeaExclusao = new ArrayList<ParcelaItemAutFornecimentoVO>();
			this.alteracoesPersistidas = Boolean.TRUE;			
			this.ativo = Boolean.FALSE;
		}
	}	
	
	public void iniciar() {
		vo = new ProgrEntregaItensAfVO();
		this.maxRowId = 32000;
		this.indPrioridadeAlterado = Boolean.FALSE;
		this.limpar(false,true,true);
		if (this.numeroSeq == null && this.numeroParcela == null) {
			this.novoRegistro = Boolean.TRUE;
			this.numeroSeq = 1;
			this.numeroParcela = (short) (this.autFornecimentoFacade.obterMaxNumeroParcela(this.numeroAf, Integer.valueOf(this.numeroItem)) + 1);
			this.vincularNovasSolicitacoes = null;
			this.listaPea = new ArrayList<ParcelaItemAutFornecimentoVO>();
			this.popularCamposAf();
			this.obterListaProgEntrega();
		} else {
			this.novoRegistro = Boolean.FALSE;
			if (numeroAf != null && numeroItem != null && numeroSeq != null && numeroParcela != null) {
				this.popularCamposAf();
				this.obterListaProgEntrega();
				this.popularGrade();			
			}
		}				
	}
	
	public Boolean isSc() {
		return DominioTipoSolicitacao.SC.equals(vo.getTipoSolicitacao());
	}
		
	public String verificarAtualizacaoPendentes() {
		if (this.alteracoesPersistidas) {
			this.limpar(true, true, true);
			return voltarParaUrl;		
		} else {
			super.openDialog("modalConfirmacaoVoltarWG");
			return null;
		}
	}

	public void atualizarParametrosOrcamentariosSc() {
		if (vo.getSolicitacaoCompra() != null) {	
			vo.setVerbaGestao(cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(vo.getSolicitacaoCompra().getVerbaGestao().getSeq()));
			vo.setCentroCusto(centroCustoFacade.obterCentroCusto(vo.getSolicitacaoCompra().getCentroCustoAplicada().getCodigo()));
			vo.setQtdeDetalhada(vo.getSolicitacaoCompra().getQtdeSolicitada().intValue());
		} else {
			vo.setVerbaGestao(null);
			vo.setCentroCusto(null);
			vo.setQtdeDetalhada(null);
		}
	}
	
	public void atualizarParametrosOrcamentariosSs() {
		if (vo.getSolicitacaoServico() != null) {
			vo.setVerbaGestao(cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(vo.getSolicitacaoServico().getVerbaGestao().getSeq()));
			vo.setCentroCusto(centroCustoFacade.obterCentroCusto(vo.getSolicitacaoServico().getCentroCustoAplicada().getCodigo()));
			vo.setValorDetalhado(vo.getSolicitacaoServico().getValorTotal());
		} else {
			vo.setVerbaGestao(null);
			vo.setCentroCusto(null);
			vo.setValorDetalhado(null);
		}
	}
	
	/**
	 * Quando não vincular novas solicitações, a lista de solicitações deve ser
	 * limpa.
	 */
	public void atualizarVinculoNovasSolicitacoes() {
		if (!Boolean.TRUE.equals(vincularNovasSolicitacoes)) {
			listaPea = new ArrayList<ParcelaItemAutFornecimentoVO>();
			listaPeaExclusao = new ArrayList<ParcelaItemAutFornecimentoVO>();
		}
	}
	
	private void popularGrade() {
		this.listaPea = new ArrayList<ParcelaItemAutFornecimentoVO>();
		List<ScoSolicitacaoProgramacaoEntrega> listaProgEntrega = this.autFornecimentoFacade.pesquisarSolicitacaoProgEntregaPorItemProgEntrega(this.numeroAf, Integer.valueOf(this.numeroItem), Integer.valueOf(this.numeroSeq), Integer.valueOf(this.numeroParcela));
		if (listaProgEntrega != null) {
			for (ScoSolicitacaoProgramacaoEntrega item : listaProgEntrega) {
				ParcelaItemAutFornecimentoVO obj = new ParcelaItemAutFornecimentoVO();
				obj.setRowId(this.maxRowId);
				obj.setIafAfnNumero(this.numeroAf);
				obj.setIafNumero(Integer.valueOf(this.numeroItem));
				obj.setSeq(item.getProgEntregaItemAf().getId().getSeq());
				obj.setParcela(item.getProgEntregaItemAf().getId().getParcela());
				obj.setSeqDetalhe(item.getSeq());
				obj.setIndPrioridadeAlterada(Boolean.FALSE);
				if (item.getSolicitacaoCompra() != null) {		
					item.setSolicitacaoCompra(solicitacaoComprasFacade.obterSolicitacaoDeCompra(item.getSolicitacaoCompra().getNumero()));
					item.getSolicitacaoCompra().setMaterial(comprasFacade.obterMaterialPorId(item.getSolicitacaoCompra().getMaterial().getCodigo()));
					
					if(item.getSolicitacaoCompra().getNaturezaDespesa() != null){
					   item.getSolicitacaoCompra().setNaturezaDespesa(cadastrosBasicosOrcamentoFacade.obterFsoNaturezaDespesa(item.getSolicitacaoCompra().getNaturezaDespesa().getId().getGndCodigo(), item.getSolicitacaoCompra().getNaturezaDespesa().getId().getCodigo()));
					}
					item.getSolicitacaoCompra().setCentroCustoAplicada(centroCustoFacade.obterCentroCustoPorChavePrimaria(item.getSolicitacaoCompra().getCentroCustoAplicada().getCodigo()));
					
					if (item.getSolicitacaoCompra().getVerbaGestao() != null){
					    item.getSolicitacaoCompra().setVerbaGestao(cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(item.getSolicitacaoCompra().getVerbaGestao().getSeq()));
					}
					
					obj.setSolicitacaoCompra(item.getSolicitacaoCompra());
					obj.setQtdeDetalhada(item.getQtde());
					obj.setQtdeEntregue(item.getQtdeEntregue());										
					obj.setMaterial(item.getSolicitacaoCompra().getMaterial());
					obj.setNaturezaDespesa(item.getSolicitacaoCompra().getNaturezaDespesa());
					obj.setCentroCusto(item.getSolicitacaoCompra().getCentroCustoAplicada());
					obj.setVerbaGestao(item.getSolicitacaoCompra().getVerbaGestao());				
				}
				if (item.getSolicitacaoServico() != null) {		
					item.setSolicitacaoServico(solicitacaoServicoFacade.obterSolicitacaoServico(item.getSolicitacaoServico().getNumero()));
					item.getSolicitacaoServico().setServico(comprasFacade.obterServicoPorId(item.getSolicitacaoServico().getServico().getCodigo()));
					if(item.getSolicitacaoServico().getNaturezaDespesa() != null){
					  FsoNaturezaDespesa natDespesa = cadastrosBasicosOrcamentoFacade.obterFsoNaturezaDespesa(item.getSolicitacaoServico().getNaturezaDespesa().getId().getGndCodigo(), item.getSolicitacaoServico().getNaturezaDespesa().getId().getCodigo());
					  item.getSolicitacaoServico().setNaturezaDespesa(natDespesa);
					}
					item.getSolicitacaoServico().setCentroCustoAplicada(centroCustoFacade.obterCentroCustoPorChavePrimaria(item.getSolicitacaoServico().getCentroCustoAplicada().getCodigo()));
					if(item.getSolicitacaoServico().getVerbaGestao() != null){
					   item.getSolicitacaoServico().setVerbaGestao(cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(item.getSolicitacaoServico().getVerbaGestao().getSeq()));
					}
							
					obj.setSolicitacaoServico(item.getSolicitacaoServico());					
					obj.setNaturezaDespesa(item.getSolicitacaoServico().getNaturezaDespesa());
					obj.setCentroCusto(item.getSolicitacaoServico().getCentroCustoAplicada());
					obj.setVerbaGestao(item.getSolicitacaoServico().getVerbaGestao());
					obj.setServico(item.getSolicitacaoServico().getServico());					
					obj.setValorDetalhado(item.getValor());
					obj.setValorEfetivado(item.getValorEfetivado());
				}
				obj.setIndPrioridade(item.getIndPrioridade());
				obj.setGeradoAutomaticamente(item.getItemAfOrigem() != null);
				this.maxRowId++;
				listaPea.add(obj);
			}
		}
	}
	
	public String getMsgAdvertenciaCentroCusto() {
		String descCentroCustoOrinal = getCentroCustoOriginal().getDescricao();
		String msg = this.getBundle().getString("MENSAGEM_CENTRO_CUSTO_DIFERENTE");
		return MessageFormat.format(msg, vo.getCentroCusto().getDescricao(),
				descCentroCustoOrinal, vo.getTipoSolicitacao().getDescricao());
	}

	public String getMsgAdvertenciaVerbaGestao() {
		String descVerbaGestao = "";		
		if (this.af.getVerbaGestao() != null) {
			this.af.setVerbaGestao(cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(this.af.getVerbaGestao().getSeq()));
			descVerbaGestao = this.af.getVerbaGestao().getDescricao();
		}
		String msg = this.getBundle().getString("MENSAGEM_VERBA_GESTAO_DIFERENTE");
		return MessageFormat.format(msg, vo.getVerbaGestao().getDescricao(),
				descVerbaGestao, vo.getTipoSolicitacao().getDescricao());
	}

	public String getMsgAdvertenciaCentroCustoSolicitacao() {
		String descCentroCustoOrinal = getCentroCustoOriginal().getDescricao();
		String msg = this.getBundle().getString("MENSAGEM_CENTRO_CUSTO_SOLICITACAO_DIFERENTE");
		return MessageFormat.format(msg, vo.getCentroCusto().getDescricao(),
				descCentroCustoOrinal, vo.getTipoSolicitacao().getDescricao());
	}

	public String getMsgAdvertenciaVerbaGestaoSolicitacao() {
		String descVerbaGestao = "";
		if (this.af.getVerbaGestao() != null) {
			this.af.setVerbaGestao(cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(this.af.getVerbaGestao().getSeq()));			
			descVerbaGestao = this.af.getVerbaGestao().getDescricao();
		}
		String msg = this.getBundle().getString("MENSAGEM_VERBA_GESTAO_SOLICITACAO_DIFERENTE");
		return MessageFormat.format(msg, vo.getVerbaGestao().getDescricao(),
				descVerbaGestao, vo.getTipoSolicitacao().getDescricao());
	}
	
	public String getDescricaoTruncada() {
		final Integer tam = 40;
		if (!this.isSc()) {			
			if (vo.getSolicitacaoServico() != null && vo.getSolicitacaoServico().getDescricao() != null && StringUtils.isNotBlank(vo.getSolicitacaoServico().getDescricao())){
				return (vo.getSolicitacaoServico().getDescricao().length() <= tam) ? vo.getSolicitacaoServico().getDescricao() : vo.getSolicitacaoServico().getDescricao().substring(0, tam -1) + "...";
			}
		}
		else {
			if (vo.getSolicitacaoCompra() != null && vo.getSolicitacaoCompra().getDescricao() != null && StringUtils.isNotBlank(vo.getSolicitacaoCompra().getDescricao())){
			    return (vo.getSolicitacaoCompra().getDescricao().length() <= tam) ? vo.getSolicitacaoCompra().getDescricao() : vo.getSolicitacaoCompra().getDescricao().substring(0, tam -1) + "...";
			}
		}
		return null;
	}
	public String getSplited(final String descricao, final Integer tam) {
		final String spliter =  "<br />";
		
		if (descricao == null) {
			return null;
		}
		if (tam == null || tam <= 0 || descricao.length() <= tam) {
			return descricao;
		}
		final StringBuffer ret = new StringBuffer(descricao.length());
		int i = 0;
		while ((i + tam) < descricao.length()) {
			final String tmp = descricao.substring(i, (i + tam));
			int fim = tmp.lastIndexOf(' ');
			if (fim <= 0) {
				fim = tam;
			}
			if (i > 0) {
				ret.append(spliter);
			}
			ret.append(tmp.substring(0, fim));
			i += fim;
		}
		ret.append(spliter).append(descricao.substring(i));
		return ret.toString();
	}
	
	public String voltar(){
		this.limpar(true, true, true);
		return voltarParaUrl;
	}
	
	// Getters/Setters
	
	public ProgrEntregaItensAfVO getVo() {
		return vo;
	}
	
	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}

	public Short getNumeroSeq() {
		return numeroSeq;
	}

	public void setNumeroSeq(Short numeroSeq) {
		this.numeroSeq = numeroSeq;
	}

	public Short getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(Short numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public List<ParcelaItemAutFornecimentoVO> getListaPea() {
		return listaPea;
	}

	public void setListaPea(List<ParcelaItemAutFornecimentoVO> listaPea) {
		this.listaPea = listaPea;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public ParcelaItemAutFornecimentoVO getItemEmEdicao() {
		return itemEmEdicao;
	}

	public void setItemEmEdicao(ParcelaItemAutFornecimentoVO itemEmEdicao) {
		this.itemEmEdicao = itemEmEdicao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public List<ParcelaItemAutFornecimentoVO> getListaPeaExclusao() {
		return listaPeaExclusao;
	}

	public void setListaPeaExclusao(List<ParcelaItemAutFornecimentoVO> listaPeaExclusao) {
		this.listaPeaExclusao = listaPeaExclusao;
	}

	public Boolean getAlteracoesPersistidas() {
		return alteracoesPersistidas;
	}

	public void setAlteracoesPersistidas(Boolean alteracoesPersistidas) {
		this.alteracoesPersistidas = alteracoesPersistidas;
	}

	public Integer getMaxRowId() {
		return maxRowId;
	}

	public void setMaxRowId(Integer maxRowId) {
		this.maxRowId = maxRowId;
	}

	public ScoProgEntregaItemAutorizacaoFornecimento getProgEntregaItemAutForn() {
		return progEntregaItemAutForn;
	}

	public void setProgEntregaItemAutForn(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutForn) {
		this.progEntregaItemAutForn = progEntregaItemAutForn;
	}

	public Boolean getNovoRegistro() {
		return novoRegistro;
	}

	public void setNovoRegistro(Boolean novoRegistro) {
		this.novoRegistro = novoRegistro;
	}

	public Integer getNumParcelaDuplicada() {
		return numParcelaDuplicada;
	}

	public void setNumParcelaDuplicada(Integer numParcelaDuplicada) {
		this.numParcelaDuplicada = numParcelaDuplicada;
	}

	public SolicProgramacaoEntregaController getSolicProgramacaoEntregaController() {
		return solicProgramacaoEntregaController;
	}

	public ScoItemAutorizacaoForn getItemAf() {
		return itemAf;
	}

	public void setItemAf(ScoItemAutorizacaoForn itemAf) {
		this.itemAf = itemAf;
	}

	public ParcelaItemAutFornecimentoVO getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(ParcelaItemAutFornecimentoVO itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public ScoAutorizacaoForn getAf() {
		return af;
	}

	public void setAf(ScoAutorizacaoForn af) {
		this.af = af;
	}

	private FccCentroCustos getCentroCustoOriginal() {
		if (centroCustoOriginal == null){
			if (Objects.equals(this.comprasFacade.obterTipoFaseSolicitacaoPorNumeroAF(itemAf.getId().getAfnNumero()), DominioTipoFaseSolicitacao.C)) {
				FccCentroCustos cc = autFornecimentoFacade.pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), false).get(0).getSolicitacaoDeCompra().getCentroCustoAplicada();
			    if (cc != null){	
				    centroCustoOriginal = centroCustoFacade.obterCentroCustoPorChavePrimaria(cc.getCodigo());
			    }
			} else {
				FccCentroCustos cc = autFornecimentoFacade.pesquisarFaseSolicitacaoServicoPorAutorizacaoFornecimento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), false).get(0).getSolicitacaoServico().getCentroCustoAplicada();
			    if (cc != null){	
				    centroCustoOriginal = centroCustoFacade.obterCentroCustoPorChavePrimaria(cc.getCodigo());
			    }
			}
		}
		return centroCustoOriginal;
	}
	
	private FsoVerbaGestao getVerbaGestaoOriginal() {
		if (verbaGestaoOriginal == null){
			if (Objects.equals(this.comprasFacade.obterTipoFaseSolicitacaoPorNumeroAF(itemAf.getId().getAfnNumero()), DominioTipoFaseSolicitacao.C)) {
				FsoVerbaGestao vg = autFornecimentoFacade.pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), false).get(0).getSolicitacaoDeCompra().getVerbaGestao();
				if (vg != null){ 
				    verbaGestaoOriginal = cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(vg.getSeq());
				}
			} else {
				FsoVerbaGestao vg = autFornecimentoFacade.pesquisarFaseSolicitacaoServicoPorAutorizacaoFornecimento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), false).get(0).getSolicitacaoServico().getVerbaGestao();
				if (vg != null){ 
				    verbaGestaoOriginal = cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(vg.getSeq());
				}
			}
		}
		return verbaGestaoOriginal;
	}
	
	public Boolean mostrarVincSolicProg(){
	  try { 
			AghParametros paramSolicProg = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_VINC_SOLIC_PROG);
			return paramSolicProg.getVlrTexto().equalsIgnoreCase("S");
		} catch (ApplicationBusinessException e) {
			return false;
		}
	}
	public Boolean getIndPrioridadeAlterado() {
		return indPrioridadeAlterado;
	}

	public void setIndPrioridadeAlterado(Boolean indPrioridadeAlterado) {
		this.indPrioridadeAlterado = indPrioridadeAlterado;
	}

	public Boolean getVincularNovasSolicitacoes() {
		return vincularNovasSolicitacoes;
	}

	public void setVincularNovasSolicitacoes(Boolean vincularNovasSolicitacoes) {
		this.vincularNovasSolicitacoes = vincularNovasSolicitacoes;
	}

	public boolean isItemAfGeradoAutomaticamente() {
		return isItemAfGeradoAutomaticamente;
	}	
}