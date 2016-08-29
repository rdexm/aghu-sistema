package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.FcpCalendarioVencimentoTributosVO;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.faturamento.cadastrosapoio.action.ImprimirVencimentoTributosController;
import br.gov.mec.aghu.model.FcpCalendarioVencimentoTributos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CalendarioVencimentoTributosController extends ActionController  {

	private static final long serialVersionUID = 3552849422505061611L;

	private static final class Pages {
		public static final String MANTER_CALENDARIO_VENCIMENTO_TRIBUTO = "manterCalendarioVencimentoTributos";
		public static final String PESQUISAR_CALENDARIO_VENCIMENTO_TRIBUTO = "pesquisarCalendarioVencimentoTributos";
	}
	
	private static final String MENSAGEM_REGRA_CALENDARIO_VENCIMENTOS_TRIBUTOS_GRAVADA = "MENSAGEM_REGRA_CALENDARIO_VENCIMENTOS_TRIBUTOS_GRAVADA";
	private static final String MENSAGEM_REGRA_CALENDARIO_VENCIMENTOS_TRIBUTOS_EXCLUIDA = "MENSAGEM_REGRA_CALENDARIO_VENCIMENTOS_TRIBUTOS_EXCLUIDA";
	
	@EJB
	private IComprasFacade compraFacade;

	private List<FcpCalendarioVencimentoTributosVO> fcpCalendariosVencimentosTributosVOs;
	
	private FcpCalendarioVencimentoTributosVO fcpCalendarioVencimentoTributosVO;
	
	private FcpCalendarioVencimentoTributos fcpCalendarioVencimentoTributos;

	private Date mesApuracao;
	
	private DominioTipoTributo dominioTipoTributo;
	
	private boolean desabilitaCampos = false;
	
	private Boolean exibirBotaoNovo;
	
	private Boolean exibirBotaoCancelar;
	
	@Inject
	private ImprimirVencimentoTributosController imprimirVencimentoTributosController;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.fcpCalendarioVencimentoTributos = new FcpCalendarioVencimentoTributos();
		this.fcpCalendarioVencimentoTributosVO = new FcpCalendarioVencimentoTributosVO();
		this.fcpCalendariosVencimentosTributosVOs = new ArrayList<FcpCalendarioVencimentoTributosVO>();
		
		this.exibirBotaoNovo = false;
	}
	
	
	
	public void imprimir(){
		imprimirVencimentoTributosController.setDataApuracao(mesApuracao);
		imprimirVencimentoTributosController.setTipoTributo(dominioTipoTributo);
		imprimirVencimentoTributosController.dispararDownloadPDF();
	}
	
	
	/**
	 * Consulta de Calendario de Vencimento de Tributos
	 * @throws BaseException
	 */
	public void pesquisar() throws BaseException {
		this.setFcpCalendariosVencimentosTributosVOs(compraFacade.listarCalendariosVencimentos(mesApuracao, dominioTipoTributo));
		
		this.exibirBotaoNovo = true;
	}

	/**
	 * Visualização do calendario de vencimento selecionado
	 * @return
	 */
	 public String visualizar(Integer numeroCalendarioVencimento){
		this.exibirBotaoCancelar = false;
		this.setDesabilitaCampos(true);
		this.fcpCalendarioVencimentoTributos = compraFacade.pesquisarFcpCalendarioVencimentoTributoPorCodigo(numeroCalendarioVencimento);
		return Pages.MANTER_CALENDARIO_VENCIMENTO_TRIBUTO;
	}

	/**
	 * Pesquisa para apresentação dos dados na tela de inserir
	 */
	public void pesquisarFcpCalendarioVencimentoTributoPorCodigo(){
		this.setFcpCalendarioVencimentoTributos(compraFacade.pesquisarFcpCalendarioVencimentoTributoPorCodigo(fcpCalendarioVencimentoTributosVO.getSeq()));
	}

	/**
	 * Limpa campos da tela
	 */
	public String limpar() {
		this.setMesApuracao(null);
		this.setDominioTipoTributo(null);
		this.fcpCalendariosVencimentosTributosVOs.clear();
		this.exibirBotaoNovo = false;
		
		return Pages.PESQUISAR_CALENDARIO_VENCIMENTO_TRIBUTO;
	}

	/**
	 * Remove o registro
	 * 
	 * @throws BaseException
	 */
	public void remover() throws BaseException {
		FcpCalendarioVencimentoTributos calendario = new FcpCalendarioVencimentoTributos();
		calendario.setSeq(this.fcpCalendarioVencimentoTributosVO.getSeq());
		this.compraFacade.removerCalendarioVencimentoTributo(calendario);
		//Realizo o refresh da lista de pesquisa
		pesquisar();

		//Exibo a mensagem de retorno para o usuário
		apresentarMsgNegocio(Severity.INFO, MENSAGEM_REGRA_CALENDARIO_VENCIMENTOS_TRIBUTOS_EXCLUIDA);
	}
	
	public String inserir() {
		this.exibirBotaoCancelar = true;
		this.desabilitaCampos = false;
		this.fcpCalendarioVencimentoTributos = new FcpCalendarioVencimentoTributos();
		return Pages.MANTER_CALENDARIO_VENCIMENTO_TRIBUTO;
	}
	
	/**
	 * Grava registro de Calendário de Vencimento de Tributos
	 * @return
	 * @throws BaseException 
	 */
	public String gravar() throws BaseException{
		try {
			compraFacade.persistirCalendarioVencimento(this.fcpCalendarioVencimentoTributos);
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_REGRA_CALENDARIO_VENCIMENTOS_TRIBUTOS_GRAVADA);			
		} catch (BaseException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			return null;
		}
		//Efetua a pesquisa novamente somente se a data foi selecionada anteriormente
		if(mesApuracao != null){
			pesquisar();
		}
		resetVariaveis();
		return Pages.PESQUISAR_CALENDARIO_VENCIMENTO_TRIBUTO;
	}
	
	/**
	 * Reseta variaveis ao clicar no botão cancelar e gravar
	 */
	private void resetVariaveis(){
		this.fcpCalendarioVencimentoTributos = new FcpCalendarioVencimentoTributos();
		this.setDesabilitaCampos(false);
	}

	/**
	 * Cancela a operação de inserir
	 * 
	 * @return
	 */
	public String cancelar() {
		resetVariaveis();
		this.exibirBotaoNovo = false;
		return Pages.PESQUISAR_CALENDARIO_VENCIMENTO_TRIBUTO;
	}
	
	public String voltar() {
		return Pages.PESQUISAR_CALENDARIO_VENCIMENTO_TRIBUTO;
	}

	/**
	 * Retorna os VOs recuperados da consulta da tela
	 * @return
	 */
	public List<FcpCalendarioVencimentoTributosVO> getFcpCalendariosVencimentosTributosVOs() {
		return fcpCalendariosVencimentosTributosVOs;
	}

	/**
	 * Seta os VOs da listagem de pesquisa
	 * 
	 * @param fcpCalendariosVencimentos
	 */
	public void setFcpCalendariosVencimentosTributosVOs(
			List<FcpCalendarioVencimentoTributosVO> fcpCalendariosVencimentos) {
		this.fcpCalendariosVencimentosTributosVOs = fcpCalendariosVencimentos;
	}
	
	public DominioTipoTributo getDominioTipoTributo() {
		return dominioTipoTributo;
	}
	
	public void setDominioTipoTributo(DominioTipoTributo dominioTipoTributo) {
		this.dominioTipoTributo = dominioTipoTributo;
	}
	
	public Date  getMesApuracao() {
		return mesApuracao;
	}
	
	public void setMesApuracao(Date mesApuracao) {
		this.mesApuracao = mesApuracao;
	}
	
	public FcpCalendarioVencimentoTributosVO getFcpCalendarioVencimentoTributosVO() {
		return fcpCalendarioVencimentoTributosVO;
	}
	
	public void setFcpCalendarioVencimentoTributosVO(FcpCalendarioVencimentoTributosVO fcpCalendarioVencimentoTributosVO) {
		this.fcpCalendarioVencimentoTributosVO = fcpCalendarioVencimentoTributosVO;
	}
	
	public FcpCalendarioVencimentoTributos getFcpCalendarioVencimentoTributos() {
		return fcpCalendarioVencimentoTributos;
	}
	
	public void setFcpCalendarioVencimentoTributos(FcpCalendarioVencimentoTributos fcpCalendarioVencimentoTributos) {
		this.fcpCalendarioVencimentoTributos = fcpCalendarioVencimentoTributos;
	}

	/**
	 * Verifica se é para desabilitar os campos
	 * true - desabilita
	 * false - habilita
	 * 
	 * @return
	 */
	public boolean isDesabilitaCampos() {
		return desabilitaCampos;
	}

	/**
	 * Seta o valor na flag para verificar se desabilita ou habilita os campos
	 * 
	 * @param desabilitaCampos
	 */
	public void setDesabilitaCampos(boolean desabilitaCampos) {
		this.desabilitaCampos = desabilitaCampos;
	}
	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}
	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
	public Boolean getExibirBotaoCancelar() {
		return exibirBotaoCancelar;
	}
	public void setExibirBotaoCancelar(Boolean exibirBotaoCancelar) {
		this.exibirBotaoCancelar = exibirBotaoCancelar;
	}
}

