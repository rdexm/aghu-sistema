package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.vo.AutFornEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class PesquisaAutFornecimentoEntregaProgramadaController extends ActionController {

	private static final long serialVersionUID = 128015135703844211L;
	private static final String CONSULTAR_ITENS_AF_ENTREGA_PROGRAMADA = "consultarItensAFEntregaProgramada";
	private static final String PESQUISAR_PROG_ENTREGA_ITENS_AF_PARCELAS = "pesquisarProgEntregaItensAFParcelas";
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	protected IComprasFacade comprasFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;
	
	//filtros recebidos da estoria 24884
	private Integer grupoMaterialCodigo;
	private Integer fornecedorNumero;
	private String tipoValorSelecionado;
	
	private String tipoValorOut;
	
	private Date dataInicial;
	private Date dataFinal;
	
	private Integer iafNumero;
	
	private EntregasGlobaisAcesso entregasGlobaisAcesso;
	
	private String voltarParaInicio = "";
	private String voltarParaUrl = "";
	
	private ScoGrupoMaterial grupoMaterial;
	private ScoFornecedor fornecedor;
	
	private List<AutFornEntregaProgramadaVO> listagem;
	
	private ProgramacaoEntregaGlobalTotalizadorVO totalizador;
	
	private Integer afnNumero;
	
	@Inject
	private ProgramacaoEntregaGlobalFornecedoresController entregaGlobalFornecedoresController;
	
	private Integer lctNumero;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 

	 

		if(grupoMaterialCodigo != null && fornecedorNumero != null){
			grupoMaterial = comprasFacade.obterGrupoMaterialPorId(grupoMaterialCodigo);
			fornecedor = comprasFacade.obterFornecedorPorNumero(fornecedorNumero);
			
			try {
				listagem = comprasFacade.obtemAutFornecimentosEntregasProgramadas(grupoMaterial, fornecedor, dataInicial, dataFinal, EntregasGlobaisAcesso.getValue(tipoValorSelecionado));
			
				totalizador = comprasFacade.totalizaValoresEntregaProgramada(listagem);
				
			}  catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	
	}

	public String redirecionaProgramacao(AutFornEntregaProgramadaVO item) {
		iafNumero = item.getIafNumero();
		afnNumero = item.getAfnNumero();
		return PESQUISAR_PROG_ENTREGA_ITENS_AF_PARCELAS;
	}
	
	public String redirecionaItensAF(AutFornEntregaProgramadaVO item) {
		//????????????????? abre vazio 1º vez
		return redirecionaItensAF(item, null);
	}
	
	public String voltar() {
		entregaGlobalFornecedoresController.setExecutarInicio(false);
		this.setTipoValorSelecionado(null);
		return voltarParaUrl;
	}
	
	public String voltarParaInicio() {
		return voltarParaInicio;
	}
	
	public String redirecionaItensAF(AutFornEntregaProgramadaVO item, EntregasGlobaisAcesso tipoValor) {
		afnNumero = item.getAfnNumero();
		this.tipoValorOut = tipoValor == null ? null : tipoValor.toString();
		return CONSULTAR_ITENS_AF_ENTREGA_PROGRAMADA;
	}
	public String redirecionaItensAFSaldoProgramado(AutFornEntregaProgramadaVO item) {
		return redirecionaItensAF(item, EntregasGlobaisAcesso.SALDO_PROGRAMADO);
	}
	public String redirecionaItensAFValorLiberar(AutFornEntregaProgramadaVO item) {
		return redirecionaItensAF(item, EntregasGlobaisAcesso.VALOR_LIBERAR);
	}
	public String redirecionaItensAFValorLiberado(AutFornEntregaProgramadaVO item) {
		return redirecionaItensAF(item, EntregasGlobaisAcesso.VALOR_LIBERADO);
	}
	public String redirecionaItensAFValorAtraso(AutFornEntregaProgramadaVO item) {
		return redirecionaItensAF(item, EntregasGlobaisAcesso.VALOR_ATRASO);
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
	
	public List<AutFornEntregaProgramadaVO> getListagem() {
		return listagem;
	}

	public ProgramacaoEntregaGlobalTotalizadorVO getTotalizador() {
		return totalizador;
	}

	public void setTotalizador(ProgramacaoEntregaGlobalTotalizadorVO totalizador) {
		this.totalizador = totalizador;
	}

	public void setListagem(List<AutFornEntregaProgramadaVO> listagem) {
		this.listagem = listagem;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
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

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public String getVoltarParaInicio() {
		return voltarParaInicio;
	}

	public void setVoltarParaInicio(String voltarParaInicio) {
		this.voltarParaInicio = voltarParaInicio;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Integer getGrupoMaterialCodigo() {
		return grupoMaterialCodigo;
	}

	public void setGrupoMaterialCodigo(Integer grupoMaterialCodigo) {
		this.grupoMaterialCodigo = grupoMaterialCodigo;
	}

	public Integer getFornecedorNumero() {
		return fornecedorNumero;
	}

	public void setFornecedorNumero(Integer fornecedorNumero) {
		this.fornecedorNumero = fornecedorNumero;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public String getTipoValorSelecionado() {
		return tipoValorSelecionado;
	}

	public void setTipoValorSelecionado(String tipoValorSelecionado) {
		this.tipoValorSelecionado = tipoValorSelecionado;
	}

	public String getTipoValorOut() {
		return tipoValorOut;
	}

	public void setTipoValorOut(String tipoValorOut) {
		this.tipoValorOut = tipoValorOut;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
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
