package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.vo.ItemAutFornEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ConsultarItensAutFornecimentoEntregaProgramadaController extends ActionController {

	private static final long serialVersionUID = 128015135703844211L;
	private static final String PESQUISAR_PROG_ENTREGA_ITENS_AF_PARCELAS = "pesquisarProgEntregaItensAFParcelas";
	
	@EJB
	protected IComprasFacade comprasFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;

	//filtros recebidos da estoria 25388
	private Integer afnNumero;
	private Date dataInicial;
	private Date dataFinal;
	private String tipoValor;
	private EntregasGlobaisAcesso entregasGlobaisAcesso;
	
	private List<ItemAutFornEntregaProgramadaVO> listagem;
	
	private String voltarUrl;
	private String voltarParaInicio;
	
	private ProgramacaoEntregaGlobalTotalizadorVO totalizador;
	
	private Integer numeroFornecedor;
	private Integer iafNumero;
	private Integer lctNumero;
	
	
	public String irProgramacao(ItemAutFornEntregaProgramadaVO vo) {
		iafNumero = vo.getIafNumero();
		lctNumero = vo.getLctNumero();
		return PESQUISAR_PROG_ENTREGA_ITENS_AF_PARCELAS;
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 

	 

		try {
			
			listagem = comprasFacade.obtemItemAutFornecimentosEntregasProgramadas(afnNumero, dataInicial, dataFinal, EntregasGlobaisAcesso.getValue(tipoValor));
			totalizador = comprasFacade.totalizaValoresItensEntregaProgramada(listagem);
			
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.INFO, "ERRO_AO_RECUPERAR_LISTA");
		}
	
	}
	

	
	public String corSaldoProgramado(BigDecimal valor) {
		if (valor.doubleValue() > 0) {
			return "background-color: blue;";
		} else {
			return "";
		}
	}
	public String corValorLiberar(BigDecimal valor) {
		if (valor.doubleValue() > 0) {
			return "background-color: yellow;";
		} else {
			return "";
		}
	}
	public String corValorLiberado(BigDecimal valor) {
		if (valor.doubleValue() > 0) {
			return "background-color: green;";
		} else {
			return "";
		}
	}
	public String corValorAtraso(BigDecimal valor) {
		if (valor.doubleValue() > 0) {
			return "background-color: red;";
		} else {
			return "";
		}
	}
	
	public String voltar() {
		this.setTipoValor(null);
		return voltarUrl;
	}
	
	public String voltarParaInicio() {
		return voltarParaInicio;
	}
	
	public ProgramacaoEntregaGlobalTotalizadorVO getTotalizador() {
		return totalizador;
	}

	public void setTotalizador(ProgramacaoEntregaGlobalTotalizadorVO totalizador) {
		this.totalizador = totalizador;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public EntregasGlobaisAcesso getEntregasGlobaisAcesso() {
		return entregasGlobaisAcesso;
	}

	public void setEntregasGlobaisAcesso(EntregasGlobaisAcesso entregasGlobaisAcesso) {
		this.entregasGlobaisAcesso = entregasGlobaisAcesso;
	}

	public List<ItemAutFornEntregaProgramadaVO> getListagem() {
		return listagem;
	}

	public void setListagem(List<ItemAutFornEntregaProgramadaVO> listagem) {
		this.listagem = listagem;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public String getVoltarUrl() {
		return voltarUrl;
	}

	public void setVoltarUrl(String voltarUrl) {
		this.voltarUrl = voltarUrl;
	}

	public String getVoltarParaInicio() {
		return voltarParaInicio;
	}

	public void setVoltarParaInicio(String voltarParaInicio) {
		this.voltarParaInicio = voltarParaInicio;
	}


	public String getTipoValor() {
		return tipoValor;
	}


	public void setTipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}	
}
