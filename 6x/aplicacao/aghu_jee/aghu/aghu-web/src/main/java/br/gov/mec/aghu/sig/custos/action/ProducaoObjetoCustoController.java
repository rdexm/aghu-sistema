package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ProducaoObjetoCustoController extends ActionController {

	private static final String PRODUCAO_OBJETO_CUSTO_LIST = "producaoObjetoCustoList";

	private static final Log LOG = LogFactory.getLog(ProducaoObjetoCustoController.class);

	private static final long serialVersionUID = -32489324982349L;
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private Integer seqDetalheProducao;
	private Integer codigoCentroCusto;
	
	private FccCentroCustos centroCusto;
	private SigObjetoCustoVersoes objetoCustoVersao;
	private SigDirecionadores direcionador;
	private SigProcessamentoCusto competencia;
	private BigDecimal valorTotal;
	
	private List<SigProcessamentoCusto> listaCompetencias;
	private List<SigDetalheProducao> listaClientes;
	private Boolean modoEdicao;
	private Boolean exibirTabela;
	private String mensagemConfirmacaoGravacao;
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
		this.carregarInformacoesPagina(true);	
	}
	
	private void carregarInformacoesPagina(boolean limparDadosInsercao){
		//Edição
		if(this.getSeqDetalheProducao() != null){
			this.setModoEdicao(true);
			this.setExibirTabela(true);
			//Busca o detalhe de produção para obter os valores dos campos de filtro
			SigDetalheProducao detalheProducao = this.custosSigCadastrosBasicosFacade.obterDetalheProducao(this.getSeqDetalheProducao());
			this.setObjetoCustoVersao(detalheProducao.getSigObjetoCustoVersoes());
			this.setDirecionador(detalheProducao.getSigDirecionadores());
			this.setCompetencia(detalheProducao.getSigProcessamentoCustos());
			this.setListaCompetencias(this.custosSigProcessamentoFacade.pesquisarCompetencia());//Lista todas as competências pra poder mostrar a selecionada
		}
		//Inserção
		else if(limparDadosInsercao){
			this.setModoEdicao(false);
			this.setExibirTabela(false);
			this.setObjetoCustoVersao(null);
			this.setDirecionador(null);
			this.setCompetencia(this.custosSigProcessamentoFacade.obterProcessamentoCusto(this.obterDataCompetenciaDefault()));
			this.setCentroCusto(this.centroCustoFacade.pesquisarCentroCustoAtivoPorCodigo(this.getCodigoCentroCusto()));
			this.carregarListaCompetencias();//Carrega somente as que não possuem
		}
		this.carregarTabelaClientes();
	}
	
	
	private void carregarValorPadraoCompetencia(){
		SigProcessamentoCusto competencia = this.custosSigProcessamentoFacade.obterProcessamentoCusto(this.obterDataCompetenciaDefault());
		if(!this.getListaCompetencias().contains(competencia)){
			competencia = null;
		}
		this.setCompetencia(competencia);
	}
	
	private void carregarTabelaClientes(){
		this.setListaClientes(new ArrayList<SigDetalheProducao>());
		if(this.getExibirTabela()){
			this.setListaClientes(this.custosSigCadastrosBasicosFacade.listarClientesObjetoCustoVersao(this.getObjetoCustoVersao(), this.getDirecionador(),this.getCompetencia()));
		}
		this.calcularValorTotal();
	}
	
	public List<SigDirecionadores> listarDirecionadores(){
		if(this.getObjetoCustoVersao() != null){
			return this.custosSigCadastrosBasicosFacade.pesquisaDirecionadoresDoObjetoCusto(this.getObjetoCustoVersao(), DominioSituacao.A, DominioTipoDirecionadorCustos.RT, DominioTipoCalculoObjeto.PM);
		}
		return null;
	}
	
	public List<SigProcessamentoCusto> listarCompetencias(){
		return this.listaCompetencias;
	}
	
	private void carregarListaCompetencias(){
		this.setListaCompetencias(this.custosSigProcessamentoFacade.pesquisarCompetenciaSemProducao(this.getObjetoCustoVersao(), this.getDirecionador()));
	}
	
	private Date obterDataCompetenciaDefault(){
		
		Calendar dtCompetencia = Calendar.getInstance();
		
		dtCompetencia.setTime(new Date());
		dtCompetencia.set(Calendar.DAY_OF_MONTH, 1);
		dtCompetencia.set(Calendar.HOUR_OF_DAY, 0);
		dtCompetencia.set(Calendar.MINUTE, 0);
		dtCompetencia.set(Calendar.SECOND, 0);
		dtCompetencia.set(Calendar.MILLISECOND, 0);
		//Mês anterior
		dtCompetencia.add(Calendar.MONTH, -1);
		
		return dtCompetencia.getTime();
	}
	
	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersao(String paramPesquisa){
		return custosSigFacade.buscaObjetoCustoPrincipalAtivoPeloCentroCusto(this.getCentroCusto(), paramPesquisa);
	}
	
	public void selecionarObjetoCustoVersao(){
		this.setDirecionador(null);
		this.carregarListaCompetencias();
		this.carregarValorPadraoCompetencia();
		
		this.setExibirTabela(false);
	}
	
	public void selecionarDirecionador(){
		if(this.getDirecionador() == null){
			this.setExibirTabela(false);
		}
		else{
			this.setExibirTabela(true);
		}
		this.carregarListaCompetencias();
		this.carregarValorPadraoCompetencia();
		this.carregarTabelaClientes();
	}

	public void calcularValorTotal(){
		this.setValorTotal(this.custosSigCadastrosBasicosFacade.calcularValorTotal(this.getListaClientes()));
	}
	
	public void verificarAntesGravar(){
		this.setMensagemConfirmacaoGravacao(null);
		try {
			if(this.custosSigCadastrosBasicosFacade.verificarPreenchimentoValoresClientes(this.getListaClientes())){
				this.setMensagemConfirmacaoGravacao(this.buscarMensagem("MENSAGEM_CONFIRMACAO_GRAVACAO_PARCIAL_PRODUCAO_OBJETO_CUSTO", this.getValorTotal().toString().replace('.', ',')));
			}
			else{
				this.setMensagemConfirmacaoGravacao(this.buscarMensagem("MENSAGEM_CONFIRMACAO_GRAVACAO_PRODUCAO_OBJETO_CUSTO", this.getValorTotal().toString().replace('.', ',')));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private String buscarMensagem(String nomeMensagem, Object...parametros) {
		return WebUtil.initLocalizedMessage(nomeMensagem, null, parametros);
	}
	
	public void gravar(){
		
		try {
			this.custosSigCadastrosBasicosFacade.persistirProducaoObjetoCusto(this.getObjetoCustoVersao(), this.getDirecionador(), this.getCompetencia(), this.getListaClientes());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO_PRODUCAO_OBJETO_CUSTO", this.getObjetoCustoVersao().getSigObjetoCustos().getNome());
			this.setMensagemConfirmacaoGravacao(null);
			
			if(!this.getListaClientes().isEmpty()){
				this.setSeqDetalheProducao(this.getListaClientes().get(0).getSeq());
				this.carregarInformacoesPagina(true);
			}
			else{
				this.setSeqDetalheProducao(null);
				this.carregarInformacoesPagina(false);
			}			
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar(){
		return PRODUCAO_OBJETO_CUSTO_LIST;
	}

	public Integer getSeqDetalheProducao() {
		return seqDetalheProducao;
	}

	public void setSeqDetalheProducao(Integer seqDetalheProducao) {
		this.seqDetalheProducao = seqDetalheProducao;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public List<SigDetalheProducao> getListaClientes() {
		return listaClientes;
	}

	public void setListaClientes(List<SigDetalheProducao> listaClientes) {
		this.listaClientes = listaClientes;
	}

	public List<SigProcessamentoCusto> getListaCompetencias() {
		return listaCompetencias;
	}

	public void setListaCompetencias(List<SigProcessamentoCusto> listaCompetencias) {
		this.listaCompetencias = listaCompetencias;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getExibirTabela() {
		return exibirTabela;
	}

	public void setExibirTabela(Boolean exibirTabela) {
		this.exibirTabela = exibirTabela;
	}

	public SigObjetoCustoVersoes getObjetoCustoVersao() {
		return objetoCustoVersao;
	}

	public void setObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersao) {
		this.objetoCustoVersao = objetoCustoVersao;
	}

	public void setMensagemConfirmacaoGravacao(String mensagemConfirmacaoGravacao) {
		this.mensagemConfirmacaoGravacao = mensagemConfirmacaoGravacao;
	}

	public String getMensagemConfirmacaoGravacao() {
		return mensagemConfirmacaoGravacao;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;
	}

	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}
}
