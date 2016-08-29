package br.gov.mec.aghu.prescricaomedica.modelobasico.vo;

import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;

public class ModeloBasicoMedicamentoVO {
	
	private MpmModeloBasicoMedicamento modeloBasicoMedicamento;
	
	private Boolean excluir;
	
	private ItensModeloBasicoVO.Tipo tipo;
	
	/**
	 * Por padrao um ModeloBasicoMedicamentoVO serah uma Solucao<br>
	 * Caso necessite ser um Medicamento usar o metodo setTipo para altera o valor.
	 */
	public ModeloBasicoMedicamentoVO() {
		super();
		this.setTipo(ItensModeloBasicoVO.Tipo.SOLUCAO);
	}
	
	public ModeloBasicoMedicamentoVO(MpmModeloBasicoMedicamento modBasMedicamento) {
		this();
		this.setModeloBasicoMedicamento(modBasMedicamento);
	}

	public void setModeloBasicoMedicamento(MpmModeloBasicoMedicamento modBasMedicamento) {
		this.modeloBasicoMedicamento = modBasMedicamento;
	}

	public MpmModeloBasicoMedicamento getModeloBasicoMedicamento() {
		return modeloBasicoMedicamento;
	}

	public void setExcluir(Boolean excluir) {
		this.excluir = excluir;
	}

	public Boolean getExcluir() {
		return excluir;
	}

	public void setTipo(ItensModeloBasicoVO.Tipo tipo) {
		this.tipo = tipo;
	}

	public ItensModeloBasicoVO.Tipo getTipo() {
		return tipo;
	}
	

}
