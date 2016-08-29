package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.List;

public class RelatorioRegistroDaNotaDeSalaVO implements Serializable{

	private static final long serialVersionUID = 7719598420426832643L;
	
	private List<SubRelatorioRegistroDaNotaDeSalaProcedimentosVO> subRelatorioProcedimentos;
	private List<SubRelatorioRegistroDaNotaDeSalaMateriaisVO> subRelatorioMateriais;
	
	private double total;
	
	private String equipe;
	
	public RelatorioRegistroDaNotaDeSalaVO() {
		super();
	}
	
	//Getters and Setters
	
	public List<SubRelatorioRegistroDaNotaDeSalaProcedimentosVO> getSubRelatorioProcedimentos() {
		return subRelatorioProcedimentos;
	}

	public void setSubRelatorioProcedimentos(
			List<SubRelatorioRegistroDaNotaDeSalaProcedimentosVO> subRelatorioProcedimentos) {
		this.subRelatorioProcedimentos = subRelatorioProcedimentos;
	}

	public List<SubRelatorioRegistroDaNotaDeSalaMateriaisVO> getSubRelatorioMateriais() {
		return subRelatorioMateriais;
	}

	public void setSubRelatorioMateriais(List<SubRelatorioRegistroDaNotaDeSalaMateriaisVO> subRelatorioMateriais) {
		this.subRelatorioMateriais = subRelatorioMateriais;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	
}
