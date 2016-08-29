package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import br.gov.mec.aghu.core.commons.NumberUtil;


public class SubRelatorioRegistroDaNotaDeSalaMateriaisVO implements Serializable {

	private static final long serialVersionUID = -3128743944875476782L;
	
	private Integer matCodigo;				//21
	private String matNome;					//22
	private Double quantidade;				//23
	private String unidade;					//24
	private BigDecimal custoMedioPonderado;	//25
	private BigDecimal custoTotal;			//26
	
	private BigDecimal custoTotalStr;
	
	private String equipeNome;
	private String equipeNomeUsual;
	
	public SubRelatorioRegistroDaNotaDeSalaMateriaisVO() {
		super();
	}
	
	public SubRelatorioRegistroDaNotaDeSalaMateriaisVO(Integer matCodigo, 
													   String matNome, 
													   Double quantidade, 
													   String unidade,
													   BigDecimal custoMedioPonderado,
													   BigDecimal custoTotal,
													   String equipeNome,
													   String equipeNomeUsual) {
		super();
		this.matCodigo = matCodigo;
		this.matNome = matNome;
		this.quantidade = quantidade;
		this.unidade = unidade;
		this.custoMedioPonderado = custoMedioPonderado;
		this.custoTotal = custoTotal;
		this.equipeNome = equipeNome;
		this.equipeNomeUsual = equipeNomeUsual;
	}
	
	public enum Fields {
		
		MATERIAL_CODIGO("matCodigo"),
		MATERIAL_DESCRICAO("matNome"),
		QUANTIDADE("quantidade"),
		UNIDADE("unidade"),
		CUSTO_MEDIO_PONDERADO("custoMedioPonderado"),
		CUSTO_TOTAL("custoTotal"),
		EQUIPE_NOME("equipeNome"),
		EQUIPE_NOME_USUAL("equipeNomeUsual");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}
	
	//Getters and Setters

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getMatNome() {
		return matNome;
	}

	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}

	public String getQuantidade() {
		DecimalFormat df = new DecimalFormat( "#,##0.##" );
		return df.format(quantidade);
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}
	
	public Double getQuantidadeDouble(){
		return quantidade;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getCustoMedioPonderado() {
		DecimalFormat df = new DecimalFormat( "#,##0.00" );
		custoMedioPonderado =  NumberUtil.truncateHALFEVEN(custoMedioPonderado, 2);
		return df.format(custoMedioPonderado);
	}

	public void setCustoMedioPonderado(BigDecimal custoMedioPonderado) {
		this.custoMedioPonderado = custoMedioPonderado;
	}
	
	public BigDecimal getCustoMedioPonderadoBigDecimal() {
		return custoMedioPonderado;
	}

	public BigDecimal getCustoTotal() {
		return NumberUtil.truncateHALFEVEN(custoTotal, 2);
	}

	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
		setCustoTotalStr(this.custoTotal);
	}

	public String getCustoTotalStr() {
		DecimalFormat df = new DecimalFormat( "#,##0.00" );
		return df.format(custoTotalStr);
	}

	public void setCustoTotalStr(BigDecimal custoTotalStr) {
		this.custoTotalStr = custoTotalStr;
	}

	public String getEquipeNome() {
		return equipeNome;
	}

	public void setEquipeNome(String equipeNome) {
		this.equipeNome = equipeNome;
	}

	public String getEquipeNomeUsual() {
		return equipeNomeUsual;
	}

	public void setEquipeNomeUsual(String equipeNomeUsual) {
		this.equipeNomeUsual = equipeNomeUsual;
	}
}
