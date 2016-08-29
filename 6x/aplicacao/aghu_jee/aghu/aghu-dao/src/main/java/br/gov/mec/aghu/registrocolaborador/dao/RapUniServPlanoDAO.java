package br.gov.mec.aghu.registrocolaborador.dao;

import br.gov.mec.aghu.model.RapUniServPlano;

public class RapUniServPlanoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapUniServPlano>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 219643963719862086L;
	
	/**
	 * #42229 cursor c_ser
	 * @param matricula
	 * @param vinCodigo
	 * @param data
	 * @return
	 */
	public Long obterNroCarteiraPorMatricula(Integer matricula,Short vinCodigo,String data){
		CursorVerifServUniMatriculaQueryBuilder builder = new CursorVerifServUniMatriculaQueryBuilder();
		return (Long) executeCriteriaUniqueResult(builder.build(matricula, vinCodigo,data, isOracle()));
	}
	/**
	 * #42229 cursor c_ser2
	 * @param prontuario
	 * @param data
	 * @return
	 */
	public Long obterNroCarteiraPorProntuario(Integer prontuario,String data){
		CursorVerifServUniProntuarioQueryBuilder builder = new CursorVerifServUniProntuarioQueryBuilder();
		return (Long) executeCriteriaUniqueResult(builder.build(prontuario,data, isOracle()));
	}
}
