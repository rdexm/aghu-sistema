/**ala
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.AlteracaoEntregaProgramadaVO;
import br.gov.mec.aghu.dominio.DominioAndamentoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class AutorizacaoFornecimentoController extends ActionController {

	private static final String IMPRIMIR_AUTORIZACAO_FORNECIMENTO = "compras-imprimirAutorizacaoFornecimento";

	private static final String PESQUISAR_VERSOES_AUT_FORNECIMENTO = "compras-pesquisarVersoesAutFornecimento";

	private static final String PESQUISAR_ITEM_AUT_FORNECIMENTO = "compras-pesquisarItemAutFornecimento";

	private static final String ESCOLHE_CONDICAO_PAGAMENTO_JULGAMENTO_PAC = "compras-escolheCondicaoPagamentoJulgamentoPac";
	
	private static final String PESQUISA_AUTORIZACAO_FORNECIMENTO = "pesquisaAutorizacaoFornecimentoList";

	private static final long serialVersionUID = 4497825107167582869L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private RelatorioAutorizacaoFornecimentoController relatorioAutorizacaoFornecimentoController;

	//TODO MIGRAÇÃO @In(required=false, value="numeroCondicaoPagamentoSelecionado")
	private Integer numeroCondicaoPagamentoSelecionado;
	
	// campos tela
	private String voltarParaUrl;
	private Integer numeroAf;
	private Short numeroComplemento;
	private DominioSituacaoAutorizacaoFornecimento situacaoAf;
	private ScoModalidadeLicitacao modalidadeCompra;
	private DominioAndamentoAutorizacaoFornecimento andamentoAf;	
	private RapServidores servidorGestor;
	private Date previsaoEntrega;
	private ScoFornecedor fornecedor;
	private String documentoFornecedor;
	private DominioSimNao excluida;
	private Date dataExclusao;
	private Date dataGeracao;
	private RapServidores servidorGerador;
	private Date dataAlteracao;
	private Short seqAf;
	private ScoMotivoAlteracaoAf motivoAlteracao;
	private ScoMotivoAlteracaoAf motivoAnterior;
	private DominioSimNao entregaProgramada;
	private DominioSimNao entregaProgramadaAnterior;
	private Double valorBruto;
	private Double valorIpi;
	private Double valorAcrescimo;
	private Double valorDesconto;
	private Double valorLiquido;
	private BigDecimal valorFrete;
	private Double valorEfetivado;
	private Double valorTotal;
	private String observacao;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private FsoVerbaGestao verbaGestao;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private FsoNaturezaDespesa naturezaDespesa;
	private ScoCondicaoPagamentoPropos condicaoPagamento;
	private FcpMoeda moeda;
	private Integer contrato;
	private Date vencimentoContrato;
	
	// controles
	private Integer maxSequenciaAlteracao;
	private Short ultimaSequenciaAlteracao;
	private Boolean mostraModalConversaoUnidade;
	//private Boolean mostraModalExclusaoSeq;
	private Boolean ultimaVersaoAfJnAssinadaCoord;
	//private Boolean mostraModalExclusaoSeqCoord;
	private String corFundoAndamento;
	
	//auxiliares af.numero
	private Integer afNumero;
	private boolean gerarProgramacao;
	private AlteracaoEntregaProgramadaVO alteracaoVO = new AlteracaoEntregaProgramadaVO();
	private boolean mostrarModal;
	
	
	private String banco;
	private String UrlBaseWebForms;

	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		this.mostraModalConversaoUnidade = Boolean.FALSE;		
		if (this.numeroCondicaoPagamentoSelecionado == null) {
			if (this.numeroAf != null && this.numeroComplemento != null) {
				this.carregarAutorizacaoFornecimento();
			}
		} else {
			this.condicaoPagamento = this.pacFacade.obterCondicaoPagamentoPropostaPorNumero(this.numeroCondicaoPagamentoSelecionado);
		}
	
	}
	
	
	// sugestions
	public List<RapServidores> listarServidores(String objPesquisa) {
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return this.registroColaboradorFacade.pesquisarServidor(objPesquisa);
		} else 
		{
			return this.registroColaboradorFacade.pesquisarRapServidores();
		}
	}

	public List<ScoMotivoAlteracaoAf> listarMotivoAlteracao(String objPesquisa) {
		return this.autFornecimentoFacade.listarScoMotivoAlteracaoAf(objPesquisa);
	}
	
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(String objParam) {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(objParam);
	}
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaPorCodigoEDescricao(String parametro) {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(parametro);
	}
	
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesaAtivas(String filter) {
		return this.cadastrosBasicosOrcamentoFacade.pesquisarNaturezasDespesaAtivas(this.grupoNaturezaDespesa, filter);
	}

	// botoes
	public void gravar() {
		try {
			this.autFornecimentoFacade.gravarAutorizacaoFornecimento(this.montarObjetoAf());	
			this.carregarAutorizacaoFornecimento();
			
			apresentarMsgNegocio(
					Severity.INFO, "MENSAGEM_GRAVAR_AF_OK");
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void converterUnidadeAf() {
		try {		
			Integer qtdConversao = this.autFornecimentoFacade.converterUnidadeAf(this.montarObjetoAf());
			
			this.carregarAutorizacaoFornecimento();
			if (qtdConversao > 0) {
				apresentarMsgNegocio(
						Severity.INFO, "MENSAGEM_CONVERSAO_AF_SUCESSO", qtdConversao);
			} else {
				apresentarMsgNegocio(
						Severity.INFO, "MENSAGEM_CONVERSAO_AF_SEM_SUCESSO");
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		this.mostraModalConversaoUnidade = Boolean.FALSE;
			
	}
	
	public void excluirSequenciaAlteracao() {
		try {
			Integer seq = Integer.valueOf(this.seqAf);			
			this.autFornecimentoFacade.excluirSequenciaAlteracao(this.montarObjetoAf(), this.ultimaSequenciaAlteracao);
			
			this.carregarAutorizacaoFornecimento();
			
			apresentarMsgNegocio(
					Severity.INFO, "MENSAGEM_EXCLUSAO_SEQ_OK", seq);
			
			
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}		
		
	}
	
	public void liberarAssinaturaAf(Boolean mostrarMensagem) {
		try {			
			this.autFornecimentoFacade.liberarAssinaturaAf(this.montarObjetoAf());
			this.carregarAutorizacaoFornecimento();
			
			if (mostrarMensagem) {
				apresentarMsgNegocio(
						Severity.INFO, "MENSAGEM_UPDATE_AF_OK");
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}		
	}
	
	public String imprimirAf() throws BaseException, JRException, SystemException, IOException, DocumentException{
		relatorioAutorizacaoFornecimentoController.setNumPac(this.numeroAf);
		relatorioAutorizacaoFornecimentoController.setNroComplemento(this.numeroComplemento);
		relatorioAutorizacaoFornecimentoController.iniciar();
		return IMPRIMIR_AUTORIZACAO_FORNECIMENTO;
	}
	
	public String liberarAssinaturaAfEImprimir() throws BaseException, JRException, SystemException, IOException, DocumentException {
		relatorioAutorizacaoFornecimentoController.setNumPac(this.numeroAf);
		relatorioAutorizacaoFornecimentoController.setNroComplemento(this.numeroComplemento);
		relatorioAutorizacaoFornecimentoController.iniciar();
		this.liberarAssinaturaAf(false);
		return IMPRIMIR_AUTORIZACAO_FORNECIMENTO;
	}
	
	// metodos de auxilio

	public void limpar(Boolean limparParam) {
		if (limparParam) {
			this.numeroAf = null;
			this.numeroComplemento= null;
		}
		this.situacaoAf= null;
		this.modalidadeCompra= null;
		this.andamentoAf= null;	
		this.servidorGestor= null;
		this.previsaoEntrega= null;
		this.fornecedor= null;
		this.documentoFornecedor= null;
		this.excluida= null;
		this.dataExclusao= null;
		this.dataGeracao= null;
		this.servidorGerador= null;
		this.dataAlteracao= null;
		this.seqAf= null;
		this.motivoAlteracao= null;
		this.motivoAnterior= null;
		this.entregaProgramada= null;
		this.valorBruto= null;
		this.valorIpi= null;
		this.valorAcrescimo= null;
		this.valorDesconto= null;
		this.valorLiquido= null;
		this.valorFrete= null;
		this.valorEfetivado= null;
		this.valorTotal= null;
		this.observacao= null;
		this.modalidadeEmpenho= null;
		this.verbaGestao= null;
		this.grupoNaturezaDespesa= null;
		this.naturezaDespesa= null;
		this.condicaoPagamento= null;
		this.moeda= null;
		this.contrato= null;
		this.vencimentoContrato= null;
	}
	
	public String voltar() {
		this.numeroCondicaoPagamentoSelecionado = null;
		return voltarParaUrl;
	}
	
	public String redirecionarEscolheCondicaoPagamentoJulgamentoPac(){
		return ESCOLHE_CONDICAO_PAGAMENTO_JULGAMENTO_PAC;
	}
	
	public String redirecionarItemAutorizacaoFornecimento(){
		return PESQUISAR_ITEM_AUT_FORNECIMENTO;
	}
	
	public String pesquisarVersoesAutFornecimento(){
		return PESQUISAR_VERSOES_AUT_FORNECIMENTO;
	}
	
	private ScoAutorizacaoForn montarObjetoAf() {
		ScoAutorizacaoForn af = this.autFornecimentoFacade.buscarAutFornPorNumPac(this.numeroAf, this.numeroComplemento);
		if (this.previsaoEntrega != null) {
			af.setDtPrevEntrega(this.previsaoEntrega);
		}
		if (this.excluida != null) {
			af.setExclusao(this.excluida.isSim());
		}
		if (this.servidorGestor != null) {
			af.setServidorGestor(this.servidorGestor);
		}
		if (this.motivoAlteracao != null) {
			af.setMotivoAlteracaoAf(this.motivoAlteracao);
		}
		if (this.entregaProgramada != null) {
			af.setEntregaProgramada(this.entregaProgramada.isSim());
		} else {
			af.setEntregaProgramada(null);
		}
		if (this.valorFrete != null) {
			af.setValorFrete(this.valorFrete);
		}
		if (this.contrato != null) {
			af.setNroContrato(this.contrato);
		}
		if (this.vencimentoContrato != null) {
			af.setDtVenctoContrato(this.vencimentoContrato);
		}
		if (this.observacao != null) {
			af.setObservacao(this.observacao);
		}
		
		af.setCondicaoPagamentoPropos(this.condicaoPagamento);
		af.setModalidadeEmpenho(this.modalidadeEmpenho);
		af.setVerbaGestao(this.verbaGestao);
		af.setGrupoNaturezaDespesa(this.grupoNaturezaDespesa);
		af.setNaturezaDespesa(this.naturezaDespesa);
		
		return af;
	}
	
	private void carregarAutorizacaoFornecimento() {
		this.limpar(false);
		ScoAutorizacaoForn af = this.autFornecimentoFacade.buscarAutFornPorNumPac(this.numeroAf, this.numeroComplemento);
		if (af != null) {
			this.preencherCamposBasicos(af);
			this.preencherAbaCondicaoPagamento(af);
			this.preencherAbaOrcamentaria(af);
			this.preencherAbaValores(af);
			this.preencherCamposCalculados(af);
			this.corFundoAndamento = this.autFornecimentoFacade.obterCorFundoAndamentoAutorizacaoFornecimento(this.andamentoAf);
			this.maxSequenciaAlteracao = this.autFornecimentoFacade.obterMaxSequenciaAlteracaoAF(af.getNumero());						
			this.ultimaSequenciaAlteracao = this.autFornecimentoFacade.obterMaxSequenciaAlteracaoAnteriorAfJn(af.getNumero(), af.getSequenciaAlteracao());
			this.afNumero = af.getNumero();
			
			if (this.seqAf > 0) {
				ScoAutorizacaoFornJn autJn = this.autFornecimentoFacade.buscarUltimaScoAutorizacaoFornJnPorNroAF(af.getNumero(), this.ultimaSequenciaAlteracao);
				
				if (autJn != null) {
					this.motivoAnterior = autJn.getMotivoAlteracaoAf();
				} else {
					this.motivoAnterior = null;
				}
				
				if (autJn != null && autJn.getServidorAssinaCoord() != null) {
					this.ultimaVersaoAfJnAssinadaCoord = Boolean.TRUE;
				} else {
					this.ultimaVersaoAfJnAssinadaCoord = Boolean.FALSE;
				}
				
			} else {
				this.motivoAnterior = null;
				this.ultimaVersaoAfJnAssinadaCoord = Boolean.FALSE;
			}
			
		}
	}
	
	private void preencherCamposBasicos(ScoAutorizacaoForn af) {
		if (af.getSituacao() != null) {
			this.situacaoAf = af.getSituacao();
		}		
		if (af.getServidorGestor() != null) {
			this.servidorGestor = af.getServidorGestor();
		}
		if (af.getDtPrevEntrega() != null) {
			this.previsaoEntrega = af.getDtPrevEntrega();
		}	
		if(af.getEntregaProgramada() != null){
			if(af.getEntregaProgramada().equals(Boolean.TRUE)){
				this.entregaProgramada = DominioSimNao.S;
			} else if(af.getEntregaProgramada().equals(Boolean.FALSE)){
				this.entregaProgramada = DominioSimNao.N;
			}
		} else {
			this.entregaProgramada = null;
		}
		if (af.getDtExclusao() != null) {
			this.dataExclusao = af.getDtExclusao();
		}
		if (af.getDtGeracao() != null) {
			this.dataGeracao = af.getDtGeracao();
		}
		if (af.getServidor() != null) {
			this.servidorGerador = af.getServidor();
		}
		if (af.getDtAlteracao() != null) {
			this.dataAlteracao = af.getDtAlteracao();
		}		
	}
	
	private void preencherAbaCondicaoPagamento(ScoAutorizacaoForn af) {
		if (af.getCondicaoPagamentoPropos() != null) {
			this.condicaoPagamento = af.getCondicaoPagamentoPropos();
		}
		if (af.getMoeda() != null) {
			this.moeda = af.getMoeda();
		}
		if (af.getNroContrato() != null) {
			this.contrato = af.getNroContrato();
		}
		if (af.getDtVenctoContrato() != null) {
			this.vencimentoContrato = af.getDtVenctoContrato();
		}
		if (af.getSequenciaAlteracao() != null) {
			this.seqAf = af.getSequenciaAlteracao();
		}
		if (af.getMotivoAlteracaoAf() != null) {
			this.motivoAlteracao = af.getMotivoAlteracaoAf();
		} else {
			this.motivoAlteracao = null;
		}
	}
	
	private void preencherAbaOrcamentaria(ScoAutorizacaoForn af) {
		if (af.getModalidadeEmpenho() != null) {
			this.modalidadeEmpenho = af.getModalidadeEmpenho();
		}	
		if (af.getVerbaGestao() != null) {	
			this.verbaGestao = af.getVerbaGestao();
		}
		if (af.getGrupoNaturezaDespesa() != null) {
			this.grupoNaturezaDespesa = af.getGrupoNaturezaDespesa();
		}
		if (af.getNaturezaDespesa() != null) {
			this.naturezaDespesa = af.getNaturezaDespesa();
		}
	}
	
	private void preencherAbaValores(ScoAutorizacaoForn af) {
		List<ScoItemAutorizacaoForn> listaItens = this.autFornecimentoFacade.pesquisarItemAfAtivosPorNumeroAf(af.getNumero(), true);
		
		if (listaItens != null && !listaItens.isEmpty()) {
			this.valorBruto = this.autFornecimentoFacade.obterValorBrutoAf(af.getNumero(), af.getNroComplemento(), listaItens);
			this.valorAcrescimo = this.autFornecimentoFacade.obterValorAcrescimoAf(af.getNumero(), af.getNroComplemento(), this.valorBruto, listaItens);
			this.valorDesconto = this.autFornecimentoFacade.obterValorDescontoAf(af.getNumero(), af.getNroComplemento(), this.valorBruto, listaItens);
			this.valorIpi = this.autFornecimentoFacade.obterValorIpiAf(af.getNumero(), af.getNroComplemento(), this.valorBruto, this.valorDesconto, this.valorAcrescimo, listaItens);		
			this.valorLiquido = this.autFornecimentoFacade.obterValorLiquidoAf(this.valorBruto, this.valorIpi, this.valorAcrescimo, this.valorDesconto);
			this.valorEfetivado = this.autFornecimentoFacade.obterValorEfetivadoAf(af.getNumero(), af.getNroComplemento(), listaItens);
			this.valorTotal = this.autFornecimentoFacade.obterValorTotalAf(this.valorLiquido, this.valorEfetivado);
		}
		if (af.getValorFrete() != null) {
			this.valorFrete = af.getValorFrete();
		} else {
			this.valorFrete = BigDecimal.ZERO;
		}
	}
	
	private void preencherCamposCalculados(ScoAutorizacaoForn af) {
		this.andamentoAf = this.autFornecimentoFacade.obterAndamentoAutorizacaoFornecimento(this.numeroAf, this.numeroComplemento);
		
		if (af.getPropostaFornecedor() != null) {
			if (af.getPropostaFornecedor().getFornecedor() != null) {
				this.fornecedor = af.getPropostaFornecedor().getFornecedor();
				this.documentoFornecedor = this.comprasFacade.obterCnpjCpfFornecedorFormatado(fornecedor);
			}
			if (af.getPropostaFornecedor().getLicitacao() != null && 
					af.getPropostaFornecedor().getLicitacao().getModalidadeLicitacao() != null) {
				this.modalidadeCompra = af.getPropostaFornecedor().getLicitacao().getModalidadeLicitacao();
			}
		}
		 
		this.excluida = af.getExclusao() ? DominioSimNao.S : DominioSimNao.N;
		
		if (af.getEntregaProgramada() != null && af.getEntregaProgramada()) {
			this.entregaProgramada = DominioSimNao.S;
		} else {
			this.entregaProgramada = DominioSimNao.N;
		}
		
		this.entregaProgramadaAnterior = this.entregaProgramada;
		
		this.observacao = af.getObservacao();
	}
	
	public Boolean desabilitarCampos(Boolean testaSituacao, Boolean testaAndamento, Boolean testaAndamentoExclusao) {
		Boolean camposDesabilitados = Boolean.FALSE;
		
		if (testaSituacao) {
			if (this.situacaoAf == DominioSituacaoAutorizacaoFornecimento.EF || this.situacaoAf == DominioSituacaoAutorizacaoFornecimento.EP ||
					this.situacaoAf == DominioSituacaoAutorizacaoFornecimento.EX || this.situacaoAf == DominioSituacaoAutorizacaoFornecimento.ES) {
				camposDesabilitados = Boolean.TRUE;
			}
		}
		if (testaAndamento) {
			if (this.andamentoAf == DominioAndamentoAutorizacaoFornecimento.LIBERAR_AF_ASSINATURA) {
				camposDesabilitados = Boolean.TRUE;
			}
		}
		if (testaAndamentoExclusao) {
			if (this.situacaoAf != DominioSituacaoAutorizacaoFornecimento.EX && this.situacaoAf != DominioSituacaoAutorizacaoFornecimento.AE) {
				camposDesabilitados = Boolean.TRUE;
			}
		}
		return camposDesabilitados;
	}
	
	public void verificarSequenciaAssinada() {
		if (!this.ultimaVersaoAfJnAssinadaCoord)  {
			this.excluirSequenciaAlteracao();			
		} else {
			this.openDialog("modalConfirmacaoExclusaoSeqCoordWG");
		}
		
		
	}
	
	public void limparNaturezaDespesa() {
		this.setNaturezaDespesa(null);
	}
	
	public void alterarEntregaProgramada() {
		
		try {
			alteracaoVO = this.autFornecimentoFacade.gerarParcelasParaMatDiretos(entregaProgramada, afNumero);
			if(alteracaoVO != null ) {
				if(!alteracaoVO.isAlteracaoPermitida() &&  !alteracaoVO.isMensagemConfirmacao()) {
					entregaProgramada = entregaProgramadaAnterior;
					this.apresentarMsgNegocio(Severity.INFO,  alteracaoVO.getMessagem());	
				} else {
					mostrarModal = alteracaoVO.isMensagemConfirmacao();
				}
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gerarProgramacaoParcela(Boolean gerarProgramacao) {		
		alteracaoVO.setMensagemConfirmacao(false);
		alteracaoVO = this.autFornecimentoFacade.gerarProgramacaoParcela(afNumero, gerarProgramacao);
		mostrarModal = false;
		if(alteracaoVO!=null && !alteracaoVO.isAlteracaoPermitida()) {
			entregaProgramada = entregaProgramadaAnterior;
			this.apresentarMsgNegocio(Severity.INFO,  alteracaoVO.getMessagem());
		}
	}	
	
	
	// getters and setters
	
	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacaoAf() {
		return situacaoAf;
	}

	public void setSituacaoAf(DominioSituacaoAutorizacaoFornecimento situacaoAf) {
		this.situacaoAf = situacaoAf;
	}

	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public DominioAndamentoAutorizacaoFornecimento getAndamentoAf() {
		return andamentoAf;
	}

	public void setAndamentoAf(DominioAndamentoAutorizacaoFornecimento andamentoAf) {
		this.andamentoAf = andamentoAf;
	}

	public Date getPrevisaoEntrega() {
		return previsaoEntrega;
	}

	public void setPrevisaoEntrega(Date previsaoEntrega) {
		this.previsaoEntrega = previsaoEntrega;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getDocumentoFornecedor() {
		return documentoFornecedor;
	}

	public void setDocumentoFornecedor(String documentoFornecedor) {
		this.documentoFornecedor = documentoFornecedor;
	}

	public DominioSimNao getExcluida() {
		return excluida;
	}

	public void setExcluida(DominioSimNao excluida) {
		this.excluida = excluida;
	}

	public Date getDataExclusao() {
		return dataExclusao;
	}

	public void setDataExclusao(Date dataExclusao) {
		this.dataExclusao = dataExclusao;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public RapServidores getServidorGerador() {
		return servidorGerador;
	}

	public void setServidorGerador(RapServidores servidorGerador) {
		this.servidorGerador = servidorGerador;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public Short getSeqAf() {
		return seqAf;
	}

	public void setSeqAf(Short seqAf) {
		this.seqAf = seqAf;
	}

	public ScoMotivoAlteracaoAf getMotivoAlteracao() {
		return motivoAlteracao;
	}

	public void setMotivoAlteracao(ScoMotivoAlteracaoAf motivoAlteracao) {
		this.motivoAlteracao = motivoAlteracao;
	}

	public ScoMotivoAlteracaoAf getMotivoAnterior() {
		return motivoAnterior;
	}

	public void setMotivoAnterior(ScoMotivoAlteracaoAf motivoAnterior) {
		this.motivoAnterior = motivoAnterior;
	}

	public DominioSimNao getEntregaProgramada() {
		return entregaProgramada;
	}

	public void setEntregaProgramada(DominioSimNao entregaProgramada) {
		this.entregaProgramada = entregaProgramada;
	}

	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public void setAutFornecimentoFacade(IAutFornecimentoFacade autFornecimentoFacade) {
		this.autFornecimentoFacade = autFornecimentoFacade;
	}

	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	public Double getValorBruto() {
		return valorBruto;
	}

	public void setValorBruto(Double valorBruto) {
		this.valorBruto = valorBruto;
	}

	public Double getValorIpi() {
		return valorIpi;
	}

	public void setValorIpi(Double valorIpi) {
		this.valorIpi = valorIpi;
	}

	public Double getValorAcrescimo() {
		return valorAcrescimo;
	}

	public void setValorAcrescimo(Double valorAcrescimo) {
		this.valorAcrescimo = valorAcrescimo;
	}

	public Double getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(Double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public Double getValorLiquido() {
		return valorLiquido;
	}

	public void setValorLiquido(Double valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public Double getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public ScoCondicaoPagamentoPropos getCondicaoPagamento() {
		return condicaoPagamento;
	}

	public void setCondicaoPagamento(ScoCondicaoPagamentoPropos condicaoPagamento) {
		this.condicaoPagamento = condicaoPagamento;
	}

	public FcpMoeda getMoeda() {
		return moeda;
	}

	public void setMoeda(FcpMoeda moeda) {
		this.moeda = moeda;
	}

	public Integer getContrato() {
		return contrato;
	}

	public void setContrato(Integer contrato) {
		this.contrato = contrato;
	}

	public Date getVencimentoContrato() {
		return vencimentoContrato;
	}

	public void setVencimentoContrato(Date vencimentoContrato) {
		this.vencimentoContrato = vencimentoContrato;
	}

	public IPacFacade getPacFacade() {
		return pacFacade;
	}

	public void setPacFacade(IPacFacade pacFacade) {
		this.pacFacade = pacFacade;
	}

	public ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return cadastrosBasicosOrcamentoFacade;
	}

	public void setCadastrosBasicosOrcamentoFacade(
			ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade) {
		this.cadastrosBasicosOrcamentoFacade = cadastrosBasicosOrcamentoFacade;
	}

	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}

	public void setGrupoNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}

	public Integer getNumeroCondicaoPagamentoSelecionado() {
		return numeroCondicaoPagamentoSelecionado;
	}

	public void setNumeroCondicaoPagamentoSelecionado(
			Integer numeroCondicaoPagamentoSelecionado) {
		this.numeroCondicaoPagamentoSelecionado = numeroCondicaoPagamentoSelecionado;
	}

	public Integer getMaxSequenciaAlteracao() {
		return maxSequenciaAlteracao;
	}

	public void setMaxSequenciaAlteracao(Integer maxSequenciaAlteracao) {
		this.maxSequenciaAlteracao = maxSequenciaAlteracao;
	}

	public Boolean getMostraModalConversaoUnidade() {
		return mostraModalConversaoUnidade;
	}

	public void setMostraModalConversaoUnidade(
			Boolean mostraModalConversaoUnidade) {
		this.mostraModalConversaoUnidade = mostraModalConversaoUnidade;
	}

	public Short getUltimaSequenciaalteracao() {
		return ultimaSequenciaAlteracao;
	}

	public void setUltimaSequenciaalteracao(Short ultimaSequenciaalteracao) {
		this.ultimaSequenciaAlteracao = ultimaSequenciaalteracao;
	}	

	public Boolean getUltimaVersaoAfJnAssinadaCoord() {
		return ultimaVersaoAfJnAssinadaCoord;
	}

	public void setUltimaVersaoAfJnAssinadaCoord(
			Boolean ultimaVersaoAfJnAssinadaCoord) {
		this.ultimaVersaoAfJnAssinadaCoord = ultimaVersaoAfJnAssinadaCoord;
	}

	public String getCorFundoAndamento() {
		return corFundoAndamento;
	}

	public void setCorFundoAndamento(String corFundoAndamento) {
		this.corFundoAndamento = corFundoAndamento;
	}
	
	public void setGerarProgramacao(boolean gerarProgramacao) {
		this.gerarProgramacao = gerarProgramacao;
	}

	public boolean isGerarProgramacao() {
		return gerarProgramacao;
	}

	public void setAlteracaoVO(AlteracaoEntregaProgramadaVO alteracaoVO) {
		this.alteracaoVO = alteracaoVO;
	}

	public AlteracaoEntregaProgramadaVO getAlteracaoVO() {
		return alteracaoVO;
	}

	public void setEntregaProgramadaAnterior(DominioSimNao entregaProgramadaAnterior) {
		this.entregaProgramadaAnterior = entregaProgramadaAnterior;
	}

	public DominioSimNao getEntregaProgramadaAnterior() {
		return entregaProgramadaAnterior;
	}

	public void setMostrarModal(boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public boolean isMostrarModal() {
		return mostrarModal;
	}	
	
	public boolean refazPesquisa(){
		if(this.voltarParaUrl != null && this.voltarParaUrl.equalsIgnoreCase(PESQUISA_AUTORIZACAO_FORNECIMENTO)){
			return true;
		} else {
			return false;
		}
	}
	
	public String getUrlBaseWebForms() {
		if (StringUtils.isBlank(UrlBaseWebForms)) {
			try {
				AghParametros aghParametros = this.parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);

				if (aghParametros != null) {
					UrlBaseWebForms = aghParametros.getVlrTexto();
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return UrlBaseWebForms;
	}

	public String getBanco() {
		if (StringUtils.isBlank(banco)) {
			try {
				AghParametros aghParametros = this.parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);

				if (aghParametros != null) {
					banco = aghParametros.getVlrTexto();
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}

		}
		return banco;
	}

	public Integer getAfNumero() {
		return afNumero;
	}

	public void setAfNumero(Integer afNumero) {
		this.afNumero = afNumero;
	}
}
