package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Domínio que indica o tipo de uma solicitação de consultoria.
 * 
 * @author gmneto
 * 
 */
public enum DominioTipoSolicitacaoConsultoria implements Dominio {
	/**
	 * Consultoria
	 */
	C,

	/**
	 * Avaliação Pré-Cirúrgica
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal(); 
	}

	@Override
	public String getDescricao() {
		
		//Apresenta a descrição deste dominio de forma localizada
//		ResourceBundle bundle = super.getResourceBundle();
//		
//		switch (this) {
//		case C:
//			return getResourceBundleValue("TIPO_SOLICITACAO_CONSULTORIA.C");
//		case A:
//			return getResourceBundleValue("TIPO_SOLICITACAO_CONSULTORIA.A");
//		default:
//			return "";
//		}
		switch (this) {
		case C:
			return "Consultoria";
		case A:
			return "Avaliação Pré-Cirúrgica";
		default:
			return "";
		}
	}

}
