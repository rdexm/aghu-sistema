package br.gov.mec.aghu.estoque.controleestoque.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.controleestoque.business.IControleEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class SaldoEstoqueController extends ActionController {
	
	private static final long serialVersionUID = 6149037945664592368L;
	
	private static final Log LOG = LogFactory.getLog(SaldoEstoqueController.class);

	@EJB
	private IControleEstoqueFacade controleEstoqueFacade;
	

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	//objeto de persistencia
	private SceMovimentoMaterial movimentoMaterial = new SceMovimentoMaterial();
	
	//suggestion almoxarifado
	private SceAlmoxarifado almoxarifado;
	
	//suggestion materiais
	private ScoMaterial material;
	
	//sugestion fornecedor
	private ScoFornecedor fornecedor;
	
	//param
	private String voltarPara;
	
	//parametros de integracao com a estoria #6617
	private Short seqAlmoxarifado;
	private Integer numeroFornecedor;
	private Integer codigoMaterial;
	
	private BigDecimal valorTotal;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	/**
	 * Metodo inicial da controller <br>
	 * que atribui um valor<br>
	 * default para o almoxarifado.
	 */
	public void iniciar() {
	 

		
		if(this.seqAlmoxarifado != null) {
			this.almoxarifado = this.controleEstoqueFacade.obterAlmoxarifado(this.seqAlmoxarifado);
			this.material = this.comprasFacade.obterMaterialPorId(this.codigoMaterial);
			this.fornecedor = this.comprasFacade.obterFornecedorPorNumero(numeroFornecedor);
		}else {
			//setar valor default para 1
			this.almoxarifado = this.controleEstoqueFacade.obterAlmoxarifado(Short.valueOf("1"));
		}
	
	}
	
		
	/**
	 * Metodo de pesquisa para <br> 
	 * suggestionbox de Almoxarifado.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String objPesquisa){
		return this.controleEstoqueFacade.pesquisarAlmoxarifadoMovimentoMaterialPorSeqDescricao((String) objPesquisa);
	}
	
	
	/**
	 * Metodo de pesquisa para <br>
	 * suggestionbox de Materiais.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ScoMaterial> pesquisarMateriais(String objPesquisa) {
		return this.comprasFacade.pesquisarMateriais(objPesquisa);
	}
	
	
	/**
	 * Metodo de pesquisa para <br>
	 * suggestionbox de fornecedores.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ScoFornecedor> pesquisarFornecedores(Object objPesquisa) {
		return this.comprasFacade.pesquisarFornecedoresSaldoEstoque(
				objPesquisa, this.almoxarifado.getSeq(),
				this.material.getCodigo(), Integer.valueOf(100));
	}
	
	
	/**
	 * Método que insere um registro na tabela SceMovimentoMaterial  <br>
	 * a partir da acao de gravar da tela.
	 * 
	 * @return
	 */
	public void gravar() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try {
			
			if (!parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO)) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_OBTER_FORNECEDOR_INCLUIR_SALDO_ESTOQUE");
				return;
			}
			
			
			AghParametros aghParamFrn = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			List<ScoFornecedor> listaFornecedores = pesquisarFornecedores(aghParamFrn.getVlrNumerico().toString());
			
			if (listaFornecedores == null || listaFornecedores.isEmpty()) {
				
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_INCLUIR_SALDO_ESTOQUE");
				return;
				
			}
			
			this.movimentoMaterial.setAlmoxarifado(this.almoxarifado);
			this.movimentoMaterial.setMaterial(this.material);
			this.movimentoMaterial.setFornecedor(listaFornecedores.get(0));
			this.controleEstoqueFacade.persistirSceMovimentoMaterial(this.movimentoMaterial, nomeMicrocomputador);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_INCLUIR_SALDO_ESTOQUE", 
					this.movimentoMaterial.getAlmoxarifado().getDescricao());
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Metodo que calcula o valor com a quantidade informada na tela.
	 */
	public void calcularValorTotal() {
		
		Integer quantidade = 0;
		Double valor = Double.valueOf(0);
		
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
	    symbols.setDecimalSeparator('.');
	    DecimalFormat format = new DecimalFormat("#.#############", symbols);
		
		if(this.movimentoMaterial != null && this.movimentoMaterial.getQuantidade() != null) {
			quantidade = this.movimentoMaterial.getQuantidade();
		}
		
		if(this.movimentoMaterial != null && this.movimentoMaterial.getValor() != null) {
			valor = this.movimentoMaterial.getValor().doubleValue();
			BigDecimal b = new BigDecimal(format.format(this.movimentoMaterial.getValor()));
			movimentoMaterial.setValor(NumberUtil.truncateFLOOR(b, 2));
		}
		
		Double custoMedioCalculado = this.calcularCustoMedioPonderado(valor, quantidade);
					
		this.valorTotal = new BigDecimal(custoMedioCalculado); 
				//NumberUtil.truncateFLOOR(custoMedio, 4);
		
	}

	
	/**
	 * Metodo que limpa os campos da tela.
	 */
	public void limpar() {
		this.movimentoMaterial = new SceMovimentoMaterial();
		this.almoxarifado = null;
		this.material = null;
		this.fornecedor = null;
		this.valorTotal = null;
	}
	
	
	public void limparFornecedor() {
		this.fornecedor = null;
	}
	
	/**
	 * Calcula custo médio ponderado
	 * @param valor
	 * @param quantidade
	 * @return
	 */
	public Double calcularCustoMedioPonderado(Double valor, Integer quantidade) {

		Double resultado = 0d;
		
		if(quantidade == null){
			quantidade = 0;
		}
		
		if(valor == null){
			valor = 0d;
		}

		if(quantidade != 0 && valor != 0) {
			resultado = valor / quantidade;
		}
		
		return resultado;
	}
	
	
	/**
	 * Metodo chamado para o botao voltar
	 * 
	 * @return
	 */
	public String voltar() {
		if(voltarPara!=null){
			if(voltarPara.equals("manterEstoqueAlmoxarifado")){
				return "estoque-manterEstoqueAlmoxarifado";
			}
		}
		return null;
	}
	
	
	//getters and setters
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public SceMovimentoMaterial getMovimentoMaterial() {
		return movimentoMaterial;
	}

	public void setMovimentoMaterial(SceMovimentoMaterial movimentoMaterial) {
		this.movimentoMaterial = movimentoMaterial;
	}


	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}


	public ScoMaterial getMaterial() {
		return material;
	}


	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}


	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}


	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}


	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}


	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}


	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}


	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}


	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}


	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}


	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
}
