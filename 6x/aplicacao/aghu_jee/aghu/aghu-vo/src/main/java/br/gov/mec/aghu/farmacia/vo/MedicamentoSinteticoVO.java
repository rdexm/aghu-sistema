package br.gov.mec.aghu.farmacia.vo;

import java.util.List;

import br.gov.mec.aghu.internacao.vo.MedicamentoVO;

public class MedicamentoSinteticoVO {
	
	private List<TipoUsoMedicamentoVO> tipoUsoMedicamentoList;
	private List<MedicamentoVO> medicamentoList;
	
	
	// Métodos construtores
	public MedicamentoSinteticoVO() {
		
	}
	
	public MedicamentoSinteticoVO(List<TipoUsoMedicamentoVO> tipoUsoMedicamentoList,List<MedicamentoVO> medicamentoList) {
		this.medicamentoList = medicamentoList;
		this.tipoUsoMedicamentoList = tipoUsoMedicamentoList;
	}
	
	
	//Getters & Setters 

	public List<TipoUsoMedicamentoVO> getTipoUsoMedicamentoList() {
		return tipoUsoMedicamentoList;
	}

	public void setTipoUsoMedicamentoList(
			List<TipoUsoMedicamentoVO> tipoUsoMedicamentoList) {
		this.tipoUsoMedicamentoList = tipoUsoMedicamentoList;
	}

	public List<MedicamentoVO> getMedicamentoList() {
		return medicamentoList;
	}

	public void setMedicamentoList(List<MedicamentoVO> medicamentoList) {
		this.medicamentoList = medicamentoList;
	}
	
}
