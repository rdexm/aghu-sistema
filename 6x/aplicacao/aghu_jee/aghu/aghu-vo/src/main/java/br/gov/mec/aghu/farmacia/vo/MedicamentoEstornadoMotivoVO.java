package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Os dados armazenados nesse objeto representam os Medicamentos Estornados por Motivo
 * 
 * @author Ademir
 */

public class MedicamentoEstornadoMotivoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3862831471386599616L;

	private String andarAla;

	private String dataDeReferenciaInicio;
	
	private String dataDeReferenciaFim;
	
	private String unidadeFuncional;
	
	private String tipoOcorDispensacao;
	
	private Integer codigoMedicamento;
	
	private String medicamento;
		
	private String quantidadeDispensada;
	
	private String quantidadeEstornada;
	
	private BigDecimal custoUnitario;
	
	private BigDecimal custoTotal;
	
	
	// GETs and SETs

	
	public Integer getCodigoMedicamento() {
		return codigoMedicamento;
	}

	public void setCodigoMedicamento(Integer codigoMedicamento) {
		this.codigoMedicamento = codigoMedicamento;
	}

	public String getQuantidadeDispensada() {
		return quantidadeDispensada;
	}

	public void setQuantidadeDispensada(String quantidadeDispensada) {
		this.quantidadeDispensada = quantidadeDispensada;
	}

	public String getQuantidadeEstornada() {
		return quantidadeEstornada;
	}

	public void setQuantidadeEstornada(String quantidadeEstornada) {
		this.quantidadeEstornada = quantidadeEstornada;
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

	public String getAndarAla() {
		return andarAla;
	}

	public void setAndarAla(String andarAla) {
		this.andarAla = andarAla;
	}
	
	public String getDataDeReferenciaInicio() {
		return dataDeReferenciaInicio;
	}

	public void setDataDeReferenciaInicio(String dataDeReferenciaInicio) {
		this.dataDeReferenciaInicio = dataDeReferenciaInicio;
	}

	public String getDataDeReferenciaFim() {
		return dataDeReferenciaFim;
	}

	public void setDataDeReferenciaFim(String dataDeReferenciaFim) {
		this.dataDeReferenciaFim = dataDeReferenciaFim;
	}

	public String getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(String unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public String getTipoOcorDispensacao() {
		return tipoOcorDispensacao;
	}

	public void setTipoOcorDispensacao(String tipoOcorDispensacao) {
		this.tipoOcorDispensacao = tipoOcorDispensacao;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	
}