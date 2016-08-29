package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Os dados armazenados nesse objeto representam os Medicamentos Prescritos por Unidade
 * 
 * @author Heliz
 */

public class MedicamentoPrescritoUnidadeVO implements Serializable {
	
	//private String seqUnidadeFuncional;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2451424533891085389L;

	private String andarAla;
	
	private String dataReferenciaInicio;
	
	private String dataReferenciaFim;
	
	private String medicamento;
	
	private String quantidade;
	
	private BigDecimal valorUnitario;
	
	private BigDecimal valorTotal;
	
	
	// GETs and SETs

	public String getAndarAla() {
		return andarAla;
	}

	public void setAndarAla(String andarAla) {
		this.andarAla = andarAla;
	}

	public String getDataReferenciaInicio() {
		return dataReferenciaInicio;
	}

	public void setDataReferenciaInicio(String dataReferenciaInicio) {
		this.dataReferenciaInicio = dataReferenciaInicio;
	}

	public String getDataReferenciaFim() {
		return dataReferenciaFim;
	}

	public void setDataReferenciaFim(String dataReferenciaFim) {
		this.dataReferenciaFim = dataReferenciaFim;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	public String getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

}
