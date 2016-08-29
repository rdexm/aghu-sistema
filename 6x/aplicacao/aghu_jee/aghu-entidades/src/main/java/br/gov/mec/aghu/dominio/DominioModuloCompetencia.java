package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * <p>
 * Obtido com a seguinte consulta:<br/>
 * <code>
 * SELECT RV_LOW_VALUE, RV_MEANING FROM fat_REF_CODES WHERE RV_DOMAIN = 'MODULO COMPETENCIA'
 * </code>
 * </p>
 * @author gandriotti
 *
 */
public enum DominioModuloCompetencia implements Dominio {

	/**
	 * 
	 */
	AMB("Ambulatório"),
	/**
	 * 
	 */
	APAC("Apac Quimioterapia"),
	/**
	 * 
	 */
	APAF("Apac Fotocoagulação"),
	/**
	 * 
	 */
	APAN("Apac Nefrologia"),
	/**
	 * 
	 */
	APAP("Apac Acompanhamento"),
	/**
	 * 
	 */
	APAR("Apac Radioterapia"),
	/**
	 * 
	 */
	APAT("Apac Otorrino"),
	/**
	 * 
	 */
	APEX("Apac Exames"),
	/**
	 * 
	 */
	APRE("Apac Pre-Transplante"),
	/**
	 * 
	 */
	INT("Internação"),
	/**
	 * 
	 */
	MAMA("SISMAMA"),
	/**
	 * 
	 */
	SIS("SISCOLO"),
	;
	
	private final String descricao;
	
	private DominioModuloCompetencia(String descricao) {
					
		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {

		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		return this.descricao;
	}
}