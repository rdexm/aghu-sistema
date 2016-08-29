package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;

/*
 * Classe de apoio, utilizada pelo EspecialidadeController
 * para exibir checkbox dinamicos de catacterísticas 
 */
public class CaracteristicaEspecialidadeFlag {
	
	private DominioCaracEspecialidade caracteristica;
	private Boolean valor;
	
	public CaracteristicaEspecialidadeFlag(
		DominioCaracEspecialidade caracteristica, 
		Boolean valor
	) {
		this.caracteristica = caracteristica;
		this.valor = valor;
	}
	
	public DominioCaracEspecialidade getCaracteristica() {
		return caracteristica;
	}
	public void setCaracteristica(DominioCaracEspecialidade caracteristica) {
		this.caracteristica = caracteristica;
	}
	public Boolean getValor() {
		return valor;
	}
	public void setValor(Boolean valor) {
		this.valor = valor;
	}
	
}
