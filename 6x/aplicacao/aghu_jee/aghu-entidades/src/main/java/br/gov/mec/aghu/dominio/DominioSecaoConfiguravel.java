package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSecaoConfiguravel implements Dominio {
	

	/**
	 * Informações Clínicas
	 */
	
	LIC(DominioAba.LAU),
	
	/**
	 * Macroscopia
	 */
	
	LMA(DominioAba.LAU),
	
	/**
	 * Avaliação de Margem Cirúrgica
	 */
	LDI(DominioAba.LAU),
	
	/**
	 * Diagnóstico
	 */
	LDE(DominioAba.LAU),
	
	/**
	 * Descrição do Material
	 */
	LDM(DominioAba.LAU),
	
	/**
	 * Topografia
	 */
	CTO(DominioAba.CAD),
	
	/**
	 * Diagnóstico
	 */
	CDI(DominioAba.CAD),
	
	/**
	 * Índice de Blocos
	 */
	IBL(DominioAba.IND)	
	
	;
	
	private DominioAba aba;

	DominioSecaoConfiguravel(DominioAba aba){
		this.aba = aba;
	}
	
	public DominioAba getAba(){
		return aba;
	}
		
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case LIC:
			return "Informações Clínicas";
		case LMA:
			return "Macroscopia";
		case LDI:
			return "Avaliação de Margem Cirúrgica";
		case LDE:
			return "Diagnóstico";
		case LDM:
			return "Descrição do Material";
		case CTO:
			return "Topografia";
		case CDI:
			return "Diagnóstico";
		case IBL:
			return "Índice de Blocos";
			
		default:
			return "";
		}
	}
}
