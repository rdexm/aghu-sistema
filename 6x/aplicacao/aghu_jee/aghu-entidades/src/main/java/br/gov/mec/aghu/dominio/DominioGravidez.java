package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a confirmação de uma gravidez.
 * 
 * @author mtocchetto
 * 
 */
public enum DominioGravidez implements Dominio {
	/**
	 * Gravidez Confirmada
	 */
	GCO, 
	/**
	 * Gravidez Não Confirmada
	 */
	GNC,
	/**
	 * 
	 */
	PNG;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case GCO:
			return "Gravidez confirmada";
		case GNC:
			return "Gravidez não confirmada";
		case PNG:
			return "Paciente não grávida";
		default:
			return "";
		}
	}
	
	// TODO Tocchetto Confirmar com a equipe de BSB se a descrição dos domínios 
	// deve ser obtida de acordo com o trecho de código comentado abaixo. 
	// Obs.: Modelo retirado da classe DominioTipoSolicitacaoConsultoria.
//	@Override
//	public String getDescricao() {
//		
//		//Apresenta a descrição deste dominio de forma localizada
//		ResourceBundle bundle = super.getResourceBundle();
//		
//		switch (this) {
//		case GCO:
//			return getResourceBundleValue("GRAVIDEZ.GCO");
//		case GNC:
//			return getResourceBundleValue("GRAVIDEZ.GNC");
//		case PNG:
//			return getResourceBundleValue("GRAVIDEZ.PNG");
//		default:
//			return "";
//		}
//	}	

}
