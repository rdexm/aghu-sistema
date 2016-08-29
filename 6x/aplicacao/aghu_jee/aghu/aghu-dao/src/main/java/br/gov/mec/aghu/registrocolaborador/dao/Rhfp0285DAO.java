package br.gov.mec.aghu.registrocolaborador.dao;

import br.gov.mec.aghu.model.Rhfp0285;

public class Rhfp0285DAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Rhfp0285>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6240469511978500683L;

	/**
	 * Verifica se dependente tem unimed pela matricula no Starh
	 * #42229 cursor c_dep_starh
	 * @param matricula
	 * @param vinCodigo
	 * @param depCodigo
	 * @param date
	 * @return
	 */
	public String obterNroCarteiraDependenteMatricula(Integer matricula,Short vinCodigo,Integer depCodigo,String data){
		CursorVerifDepStarhMatriculaQueryBuilder builder = new CursorVerifDepStarhMatriculaQueryBuilder();
		return (String) executeCriteriaUniqueResult(builder.build(matricula, vinCodigo,depCodigo,data, isOracle()));
	}

	/**
	 * #42229 cursor c_dep2_starh
	 * Verifica se dependente tem unimed pelo prontuario no Starh
	 * @param prontuario
	 * @param data
	 * @return
	 */
	public String obterNroCarteiraDependenteProntuario(Integer prontuario,String data){
		CursorVerifDepStarhProntuarioQueryBuilder builder = new CursorVerifDepStarhProntuarioQueryBuilder();
		return (String) executeCriteriaUniqueResult(builder.build(prontuario,data, isOracle()));
	}
	
}
