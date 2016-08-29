package br.gov.mec.aghu.farmacia.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


//Estória # 5714
public class MedicamentoDispensadoPorBoxVO {
	
	
	private Date dataEmissaoInicio;	
	private Date dataEmissaoFim;
	private String dataEmissaoInicioEditada;	
	private String dataEmissaoFimEditada;
	private String estacaoDispensadora;
	private Integer codMedicamento;
	private String descricaoMedicamento;
	private String descricaoMedicamentoEditada;
	private BigDecimal concentracao;
	private String concentracaoEditada;
	private String unidadeMedidaMedica;	
	private String apresentacao;
	private BigDecimal quantidade;	
	private String quantidadeEditada;	
	private BigDecimal custoUnitario;
	private BigDecimal custoTotal;
	private String tipousoMedicamento;
	private String tipoApresentacaoMedicamento;
	

	public void setMedicamentoDispensadoPorBoxList(
			List<MedicamentoDispensadoPorBoxVO> listaMedicamentomedicamentoDispensadoPorBox) {
		// TODO Auto-generated method stub
		
	}
	
	
	public enum Fields {
		DATA_EMISSAO_INICIO("dataEmissaoInicio"), 
		DATA_EMISSAO_FIM("dataEmissaoFim"), 
		ESTACAO_DISPENSADORA("estacaoDispensadora"),
		CODIGO_MEDICAMENTO("codMedicamento"),	
		DESCRICAO_MEDICAMENTO("descricaoMedicamento"),
		DESCRICAO_MEDICAMENTO_EDITADA("descricaoMedicamentoEditada"),
		CONCENTRACAO("concentracao"),
		CONCENTRACAO_EDITADA("concentracaoEditada"),
		UNIDADE_MEDIDA_MEDICA("unidadeMedidaMedica"),
		APRESENTACAO("apresentacao"),		
		QUANTIDADE("quantidade"),
		QUANTIDADE_EDITADA("quantidadeEditada"),
		CUSTO_UNITARIO("custoUnitario"),
		CUSTO_TOTAL("custoTotal"),
		TIPO_USO_MEDICAMENTO("tipousoMedicamento"),
		TIPO_APRESENTACAO_MEDICAMENTO("tipoApresentacaoMedicamento");
				

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

		public String getFields() {
			return fields;
		}

		public void setFields(String fields) {
			this.fields = fields;
		}
	}
		
		
	//Getters & Setters
	
	public Date getDataEmissaoInicio() {
		return dataEmissaoInicio;
	}

	public void setDataEmissaoInicio(Date dataEmissaoInicio) {
		this.dataEmissaoInicio = dataEmissaoInicio;
	}

	public Date getDataEmissaoFim() {
		return dataEmissaoFim;
	}

	public void setDataEmissaoFim(Date dataEmissaoFim) {
		this.dataEmissaoFim = dataEmissaoFim;
	}
	
	public String getDataEmissaoInicioEditada() {
		return dataEmissaoInicioEditada;
	}

	public void setDataEmissaoInicioEditada(String dataEmissaoInicioEditada) {
		this.dataEmissaoInicioEditada = dataEmissaoInicioEditada;
	}

	public String getDataEmissaoFimEditada() {
		return dataEmissaoFimEditada;
	}

	public void setDataEmissaoFimEditada(String dataEmissaoFimEditada) {
		this.dataEmissaoFimEditada = dataEmissaoFimEditada;
	}

	public String getEstacaoDispensadora() {
		return estacaoDispensadora;
	}

	public void setEstacaoDispensadora(String estacaoDispensadora) {
		this.estacaoDispensadora = estacaoDispensadora;
	}

	public Integer getCodMedicamento() {
		return codMedicamento;
	}

	public void setCodMedicamento(Integer codMedicamento) {
		this.codMedicamento = codMedicamento;
	}

	public String getDescricaoMedicamento() {
		return descricaoMedicamento;
	}

	public void setDescricaoMedicamento(String descricaoMedicamento) {
		this.descricaoMedicamento = descricaoMedicamento;
	}

	public BigDecimal getConcentracao() {
		return concentracao;
	}

	public void setConcentracao(BigDecimal concentracao) {
		this.concentracao = concentracao;
	}

	public String getConcentracaoEditada() {
		return concentracaoEditada;
	}

	public void setConcentracaoEditada(String concentracaoEditada) {
		this.concentracaoEditada = concentracaoEditada;
	}

	public String getUnidadeMedidaMedica() {
		return unidadeMedidaMedica;
	}

	public void setUnidadeMedidaMedica(String unidadeMedidaMedica) {
		this.unidadeMedidaMedica = unidadeMedidaMedica;
	}

	public String getDescricaoMedicamentoEditada() {
		return descricaoMedicamentoEditada;
	}

	public void setDescricaoMedicamentoEditada(String descricaoMedicamentoEditada) {
		this.descricaoMedicamentoEditada = descricaoMedicamentoEditada;
	}

	public String getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(String apresentacao) {
		this.apresentacao = apresentacao;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public String getQuantidadeEditada() {
		return quantidadeEditada;
	}

	public void setQuantidadeEditada(String quantidadeEditada) {
		this.quantidadeEditada = quantidadeEditada;
	}

	public BigDecimal getCustoUnitario() {
		return custoUnitario;
	}

	public void setCustoUnitario(BigDecimal custoUnitario) {
		this.custoUnitario = custoUnitario;
	}

	public BigDecimal getCustoTotal() {
		return custoTotal;
	}

	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
	}

	public String getTipousoMedicamento() {
		return tipousoMedicamento;
	}

	public void setTipousoMedicamento(String tipousoMedicamento) {
		this.tipousoMedicamento = tipousoMedicamento;
	}

	public String getTipoApresentacaoMedicamento() {
		return tipoApresentacaoMedicamento;
	}

	public void setTipoApresentacaoMedicamento(String tipoApresentacaoMedicamento) {
		this.tipoApresentacaoMedicamento = tipoApresentacaoMedicamento;
	}
	
	
}