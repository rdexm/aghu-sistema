package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoEntregaVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ConsultaItensAFProgramacaoEntregaController extends ActionController {

	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";

	private static final String PESQUISA_AUTORIZACAO_FORNECIMENTO_LIST = "compras-pesquisaAutorizacaoFornecimentoList";

	private static final String PROGRAMACAO_MANUAL_PARCELAS_ENTREGA_AF = "programacaoManualParcelasEntregaAF";

	private static final Log LOG = LogFactory.getLog(ConsultaItensAFProgramacaoEntregaController.class);

	private static final long serialVersionUID = 7020954410359662290L;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	PesquisaAutorizacaoFornecimentoPaginatorController pesquisaAutorizacaoFornecimentoPaginatorController;


	private Integer  numeroLicitacao;
	private Integer afnNumero;
	private Short complemento;
	private String nroAF;
	private String voltarParaUrl;
	private Boolean isBotaoVoltar = Boolean.TRUE;
	private Boolean indContrato;
	private Boolean indProgrEntgBloq;
	private Boolean isEntregaProgramada;
	private ScoAutorizacaoForn autorizacaoForn  = new ScoAutorizacaoForn();
	private Integer lctNumero;
	private Integer codMaterial;

	private ConsultaItensAFProgramacaoEntregaVO itemSelecionado = new ConsultaItensAFProgramacaoEntregaVO();


	List<ConsultaItensAFProgramacaoEntregaVO> listVO = new ArrayList<ConsultaItensAFProgramacaoEntregaVO>(0);

	public void inicio() {




		autorizacaoForn  = new ScoAutorizacaoForn();
		nroAF = formatarNumeroDaAF();
		pesquisar();
		getIsEntregaProgramadaLista();

	}


	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public String consultarAF() {
		autorizacaoForn = autFornecimentoFacade.obterAfByNumero(afnNumero);
		if(autorizacaoForn!=null && autorizacaoForn.getPropostaFornecedor()!=null){
			lctNumero = autorizacaoForn.getPropostaFornecedor().getId().getLctNumero();
			complemento = autorizacaoForn.getNroComplemento();

			// seta os valores para a consulta de AF
			pesquisaAutorizacaoFornecimentoPaginatorController.setNumeroAf(lctNumero);
			pesquisaAutorizacaoFornecimentoPaginatorController.setNumeroComplemento(complemento);
			pesquisaAutorizacaoFornecimentoPaginatorController.setVoltarParaUrl("compras-consultaItensAFProgramacaoEntrega");
		}
		return PESQUISA_AUTORIZACAO_FORNECIMENTO_LIST;
	}

	public String estatisticaConsumo(ConsultaItensAFProgramacaoEntregaVO vo){
		codMaterial = vo.getCodigo();
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}

	public String gravar() {
		try {
			setIsEntregaProgramadaLista();
			autFornecimentoFacade.manterAutorizacaoFornecimento(afnNumero, listVO);
			this.apresentarMsgNegocio(Severity.INFO, "MESSAGE_ITENS_AF_PROG_ENTREGA");
		} catch (BaseException e) {
			LOG.error("Erro",e);
			apresentarExcecaoNegocio(e);
		}

		return voltarParaUrl;
	}

	public String voltarCancelar() {
		return voltarParaUrl;
	}

	public String redirecionarProgramacaoManualParcelasEntregaAF(){
		return PROGRAMACAO_MANUAL_PARCELAS_ENTREGA_AF;
	}

	private Boolean getIsEntregaProgramadaLista(){

		if(listVO != null && !listVO.isEmpty()){
			isEntregaProgramada = listVO.get(0).getEntregaProgramada();
		}

		return Boolean.FALSE;
	}

	private void setIsEntregaProgramadaLista(){
		for (ConsultaItensAFProgramacaoEntregaVO vo : listVO) {
			vo.setEntregaProgramada(isEntregaProgramada);
		}
	}

	private String formatarNumeroDaAF() {
		return numeroLicitacao + "/" + complemento;
	}

	private void pesquisar() {
		try {
			listVO = autFornecimentoFacade.consultarItensAFProgramacaoEntrega(afnNumero);
		} catch (BaseException e) {
			LOG.error("Erro",e);
			apresentarExcecaoNegocio(e);
		}
	}

	public Boolean excluiProgramacaoExistente(ConsultaItensAFProgramacaoEntregaVO item){
		if (Boolean.TRUE.equals(item.getIndContrato()) && Boolean.FALSE.equals(item.getIndProgrEntgBloq())){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public void alterarMessagesLabelBotaoVoltarCancelar(){
		isBotaoVoltar = Boolean.FALSE;
	}

	/**
	 * Ação do botão PROGRAMAR AUTOMÁTICA
	 */
	public void programacaoAutomaticaItem() {
		try {

			String retorno = comprasFacade.programacaoAutomaticaParcelasAF(afnNumero, itemSelecionado.getNumeroDoItem());

			if (retorno == null) {
				this.apresentarMsgNegocio(Severity.INFO, "PROGRAMACAO_REALIZADA_SUCESSO");
			} else {
				if (retorno.equals("ITEM_COM_ESTOQUE_MAXIMO_OU_POUCO_MOVIMENTADO")) {
					this.apresentarMsgNegocio(Severity.INFO, retorno, itemSelecionado.getNumero());
				} else {
					this.apresentarMsgNegocio(Severity.INFO, retorno);
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}

	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public String getNroAF() {
		return nroAF;
	}

	public void setNroAF(String nroAF) {
		this.nroAF = nroAF;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public List<ConsultaItensAFProgramacaoEntregaVO> getListVO() {
		return listVO;
	}

	public void setListVO(List<ConsultaItensAFProgramacaoEntregaVO> listVO) {
		this.listVO = listVO;
	}

	public Boolean getIsBotaoVoltar() {
		return isBotaoVoltar;
	}

	public void setIsBotaoVoltar(Boolean isBotaoVoltar) {
		this.isBotaoVoltar = isBotaoVoltar;
	}

	public Boolean getIndContrato() {
		return indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	public Boolean getIndProgrEntgBloq() {
		return indProgrEntgBloq;
	}

	public void setIndProgrEntgBloq(Boolean indProgrEntgBloq) {
		this.indProgrEntgBloq = indProgrEntgBloq;
	}

	public Boolean getIsEntregaProgramada() {
		return isEntregaProgramada;
	}

	public void setIsEntregaProgramada(Boolean isEntregaProgramada) {
		this.isEntregaProgramada = isEntregaProgramada;
	}

	public ConsultaItensAFProgramacaoEntregaVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(
			ConsultaItensAFProgramacaoEntregaVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Integer getCodMaterial() {
		return codMaterial;
	}

	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}



}
