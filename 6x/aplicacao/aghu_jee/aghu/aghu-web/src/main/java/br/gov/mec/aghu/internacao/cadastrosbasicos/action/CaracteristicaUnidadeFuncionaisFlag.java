package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;

/*
 * Classe de apoio, utilizada pelo UnidadeFuncionalController
 * para exibir checkbox dinamicos de catacterísticas 
 */
public class CaracteristicaUnidadeFuncionaisFlag {
	
	private ConstanteAghCaractUnidFuncionais caracteristica;
	private Boolean valor;
	
	public CaracteristicaUnidadeFuncionaisFlag(
			ConstanteAghCaractUnidFuncionais caracteristica, 
		Boolean valor
	) {
		this.caracteristica = caracteristica;
		this.valor = valor;
	}
	
	public ConstanteAghCaractUnidFuncionais getCaracteristica() {
		return caracteristica;
	}
	public void setCaracteristica(ConstanteAghCaractUnidFuncionais caracteristica) {
		this.caracteristica = caracteristica;
	}
	public Boolean getValor() {
		return valor;
	}
	public void setValor(Boolean valor) {
		this.valor = valor;
	}
	
}
