package br.gov.mec.aghu.registrocolaborador.dao;

import br.gov.mec.aghu.model.Rhfp0283;

/**
 * #42229
 * @author rodrigo.saraujo
 *
 */
public class Rhfp0283DAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Rhfp0283>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7190773503777186643L;

	/**
	 * Verifica se servidor tem unimed pela matricula no Starh
	 * #42229 cursor c_ser_starh
	 * @param matricula
	 * @param vinCodigo
	 * @param data
	 * @return
	 */
	public String obterNroCarteiraPorMatricula(Integer matricula,Short vinCodigo,String data){
		CursorVerifServStarhMatriculaQueryBuilder builder = new CursorVerifServStarhMatriculaQueryBuilder();
		return (String) executeCriteriaUniqueResult(builder.build(matricula, vinCodigo,data, isOracle()));
	}
	/**Verifica se servidor tem unimed pelo prontuário no Starth
	 * #42229 cursor c_ser2_starh
	 * @param prontuario
	 * @param data
	 * @return
	 */
	public String obterNroCarteiraPorProntuario(Integer prontuario,String data){
		CursorVerifServStarhProntuarioQueryBuilder builder = new CursorVerifServStarhProntuarioQueryBuilder();
		return (String) executeCriteriaUniqueResult(builder.build(prontuario,data, isOracle()));
	}
	
}
