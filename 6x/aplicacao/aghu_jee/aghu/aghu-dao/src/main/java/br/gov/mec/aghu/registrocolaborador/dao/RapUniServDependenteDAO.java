package br.gov.mec.aghu.registrocolaborador.dao;

import br.gov.mec.aghu.model.RapUniServDependente;



/**
 * #42229 Incluido 
 * @author rodrigo.saraujo
 *
 */
public class RapUniServDependenteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapUniServDependente> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6703726845099369373L;
	
	/**
	 * Verifica se dependente tem unimed pela matricula
	 * #42229 cursor c_dep
	 * @param matricula
	 * @param vinCodigo
	 * @param depCodigo
	 * @param date
	 * @return
	 */
	public Long obterNroCarteiraDependenteMatricula(Integer matricula,Short vinCodigo,Integer depCodigo,String data){
		CursorVerifDepUniMatriculaQueryBuilder builder = new CursorVerifDepUniMatriculaQueryBuilder();
		return (Long) executeCriteriaUniqueResult(builder.build(matricula, vinCodigo,depCodigo,data, isOracle()));
	}

	/**
	 * #42229
	 * Verifica se dependente tem unimed pelo prontuario 
	 * @param prontuario
	 * @param data
	 * @return
	 */
	public Long obterNroCarteiraDependenteProntuario(Integer prontuario,String data){
		CursorVerifDepUniProntuarioQueryBuilder builder = new CursorVerifDepUniProntuarioQueryBuilder();
		return (Long) executeCriteriaUniqueResult(builder.build(prontuario,data, isOracle()));
	}
}
