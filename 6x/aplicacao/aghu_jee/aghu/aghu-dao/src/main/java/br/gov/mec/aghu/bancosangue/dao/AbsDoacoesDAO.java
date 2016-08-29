package br.gov.mec.aghu.bancosangue.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.faturamento.vo.DoacaoColetaSangueVO;
import br.gov.mec.aghu.faturamento.vo.DoadorSangueTriagemClinicaVO;
import br.gov.mec.aghu.model.AbsDoacoes;

public class AbsDoacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsDoacoes> {

	private static final long serialVersionUID = -8337956828595106046L;

	@SuppressWarnings("unchecked")
	public List<DoadorSangueTriagemClinicaVO> listarDoadorSangueTriagemClinica(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo) {

		StringBuffer sql = new StringBuffer(50);
		if(isOracle()) {
			//QUERY NATIVA - ORACLE
			sql.append("SELECT ");
			sql.append("to_char(BOL_DATA, 'DD/MM/YYYY') as boldata, "); // --- DTHR_REALIZADO
			sql.append("COUNT(*) as quantidade, "); // --- QUANTIDADE
			sql.append("NVL(SER_MATRICULA," + matricula + ") as sermatricula, ");// ---SER_MATRICULA 9999999
			sql.append("NVL(SER_VIN_CODIGO," + vinCodigo + ") as servincodigo "); // ---SER_VIN_CODIGO 955
			sql.append("FROM   agh.ABS_DOACOES ");
			sql.append("WHERE  BOL_DATA BETWEEN :vDtHrInicio AND :vDtHrFim ");
			sql.append("GROUP BY to_char(BOL_DATA, 'DD/MM/YYYY'),NVL(SER_MATRICULA," + matricula + "),NVL(SER_VIN_CODIGO," + vinCodigo + ") ");
  
			sql.append("UNION ALL ");
  
			sql.append("SELECT ");
			sql.append("to_char(RCA.DT_RESPOSTA, 'DD/MM/YYYY') as boldata, ");
			sql.append("COUNT(DISTINCT( RCA.CAD_SEQ)) as quantidade, "); //--- QUANTIDADE
			sql.append("NVL(rca.SER_MATRICULA," + matricula + ") as sermatricula, ");// ---SER_MATRICULA
			sql.append("NVL(rca.SER_VIN_CODIGO," + vinCodigo + ") as servincodigo "); // ---SER_VIN_CODIGO
			sql.append("FROM    agh.ABS_RESPOSTAS_CANDIDATOS RCA"); 
			sql.append(",agh.ABS_CANDIDATOS_DOADORES CAD1 ");
			sql.append(",agh.ABS_QUESTOES QST ");
			sql.append(",agh.ABS_QUESTIONARIOS QUE ");
			sql.append("WHERE QST.CODIGO = RCA.QST_CODIGO ");
			sql.append("AND   QST.QUE_CODIGO = RCA.QST_QUE_CODIGO ");
			sql.append("AND   QUE.CODIGO = QST.QUE_CODIGO ");
			sql.append("AND   CAD1.SEQ = RCA.CAD_SEQ ");
			sql.append("AND   (RCA.DT_RESPOSTA BETWEEN :vDtHrInicio AND :vDtHrFim ");
			sql.append("AND   QUE.TIPO_QUESTIONARIO = 'R') ");
			sql.append("GROUP BY to_char(RCA.DT_RESPOSTA, 'DD/MM/YYYY'), NVL(rca.SER_MATRICULA," + matricula + "), NVL(rca.SER_VIN_CODIGO," + vinCodigo + ")");
		}
		else {
			//QUERY NATIVA - POSTGRESQL
			sql.append("SELECT ");
			sql.append("to_char(BOL_DATA,'DD/MM/YYYY') as boldata, ");// --- DTHR_REALIZADO
			sql.append("COUNT(*) as quantidade, ");// --- QUANTIDADE

			sql.append("CASE WHEN SER_MATRICULA is null THEN " + matricula + " ELSE SER_MATRICULA END as sermatricula, ");
			//--NVL(SER_MATRICULA,9999999), ---SER_MATRICULA 

			sql.append("CASE WHEN SER_VIN_CODIGO is null THEN " + vinCodigo + " ELSE SER_VIN_CODIGO END as servincodigo ");
			//--NVL(SER_VIN_CODIGO,955), ---SER_VIN_CODIGO
      
			//from
			sql.append("FROM   agh.ABS_DOACOES ");
			
			//where
			sql.append("WHERE  BOL_DATA BETWEEN :vDtHrInicio AND :vDtHrFim ");
			sql.append("GROUP BY bol_data,sermatricula,servincodigo ");
  
			//union all
			sql.append(" UNION ALL ");
  
			sql.append("SELECT ");
			sql.append("to_char(RCA.DT_RESPOSTA,'DD/MM/YYYY') as boldata, ");
			sql.append("COUNT(DISTINCT( RCA.CAD_SEQ)) as quantidade, ");// --- QUANTIDADE

			sql.append("CASE WHEN rca.SER_MATRICULA is null THEN " + matricula + " ELSE rca.SER_MATRICULA END as sermatricula, ");

			sql.append("CASE WHEN rca.SER_VIN_CODIGO is null THEN " + vinCodigo + " ELSE rca.SER_VIN_CODIGO END as servincodigo ");

			//from
			sql.append("FROM    agh.ABS_RESPOSTAS_CANDIDATOS RCA");
			sql.append(",agh.ABS_CANDIDATOS_DOADORES CAD1");
			sql.append(",agh.ABS_QUESTOES QST");
			sql.append(",agh.ABS_QUESTIONARIOS QUE");
			
			//where
			sql.append(" WHERE QST.CODIGO = RCA.QST_CODIGO");
			sql.append(" AND   QST.QUE_CODIGO = RCA.QST_QUE_CODIGO");
			sql.append(" AND   QUE.CODIGO = QST.QUE_CODIGO");
			sql.append(" AND   CAD1.SEQ = RCA.CAD_SEQ");
			sql.append(" AND   (RCA.DT_RESPOSTA BETWEEN :vDtHrInicio AND :vDtHrFim");
			sql.append(" AND   QUE.TIPO_QUESTIONARIO = 'R')");
			sql.append(" GROUP by boldata, sermatricula, servincodigo");
		}
			
		SQLQuery q = createSQLQuery(sql.toString());
		q.setDate("vDtHrInicio", vDtHrInicio);
		q.setDate("vDtHrFim", vDtHrFim);
		
		List<DoadorSangueTriagemClinicaVO> listaVO = q.
				addScalar("boldata",StringType.INSTANCE).
				addScalar("quantidade",ShortType.INSTANCE).
				addScalar("sermatricula",IntegerType.INSTANCE).
				addScalar("servincodigo",ShortType.INSTANCE).
				setResultTransformer(Transformers.aliasToBean(DoadorSangueTriagemClinicaVO.class)).list();
		
		return listaVO;
	}
	
	/**
	 * Lista os tipos de doações ignorando o tipo passado como parametro e filtra pelo intervalo de datas
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param tipo
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarDoacaoColetaSangue(Date vDtHrInicio, Date vDtHrFim, 
			String tipo, Integer matricula, Short vinCodigo) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select trunc(ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(") as bolData");
		
		hql.append(", count(*)");
		hql.append(" as quantidade");

		hql.append(", case when ").append(AbsDoacoes.Fields.SER_MATRICULA.toString());
		hql.append(" is null then ").append(matricula).append(" else ");
		hql.append(AbsDoacoes.Fields.SER_MATRICULA.toString()).append(" end as serMatricula");
		
		hql.append(", case when ").append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString());
		hql.append(" is null then ").append(vinCodigo).append(" else ");
		hql.append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString()).append(" end as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AbsDoacoes.class.getName());
		hql.append(" as ado");
		
		//where
		hql.append(" where ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and ado.");
		hql.append(AbsDoacoes.Fields.BOL_NUMERO.toString());
		hql.append(" > 0");

		hql.append(" and ado.");
		hql.append(AbsDoacoes.Fields.TDO_CODIGO.toString());
		hql.append(" <> :tipo");
		
		// group by
		hql.append(" group by");
		hql.append(" ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(", ado.");
		hql.append(AbsDoacoes.Fields.SER_MATRICULA.toString());
		hql.append(", ado.");
		hql.append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString());
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameter("tipo", tipo);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}	
	
	/**
	 * Lista os tipos de doações filtrando por tipo no intervalo de datas
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param tipo
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarDoacaoPorTipo(Date vDtHrInicio, Date vDtHrFim,
			String tipo, Integer matricula, Short vinCodigo) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select trunc(ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(") as bolData");
		
		hql.append(", count(*)");
		hql.append(" as quantidade");

		hql.append(", case when ").append(AbsDoacoes.Fields.SER_MATRICULA.toString());
		hql.append(" is null then ").append(matricula).append(" else ");
		hql.append(AbsDoacoes.Fields.SER_MATRICULA.toString()).append(" end as serMatricula");
		
		hql.append(", case when ").append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString());
		hql.append(" is null then ").append(vinCodigo).append(" else ");
		hql.append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString()).append(" end as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AbsDoacoes.class.getName());
		hql.append(" as ado");
		
		//where
		hql.append(" where ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and ado.");
		hql.append(AbsDoacoes.Fields.BOL_NUMERO.toString());
		hql.append(" > 0");

		hql.append(" and ado.");
		hql.append(AbsDoacoes.Fields.TDO_CODIGO.toString());
		hql.append(" = :tipo");
		
		// group by
		hql.append(" group by");
		hql.append(" ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(", ado.");
		hql.append(AbsDoacoes.Fields.SER_MATRICULA.toString());
		hql.append(", ado.");
		hql.append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString());
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameter("tipo", tipo);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}
	
	/**
	 * Lista as doações por intervalo de datas
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarDoacaoPorPeriodo(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select trunc(ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(") as bolData");
		
		hql.append(", count(*)");
		hql.append(" as quantidade");

		hql.append(", case when ").append(AbsDoacoes.Fields.SER_MATRICULA.toString());
		hql.append(" is null then ").append(matricula).append(" else ");
		hql.append(AbsDoacoes.Fields.SER_MATRICULA.toString()).append(" end as serMatricula");
		
		hql.append(", case when ").append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString());
		hql.append(" is null then ").append(vinCodigo).append(" else ");
		hql.append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString()).append(" end as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AbsDoacoes.class.getName());
		hql.append(" as ado");
		
		//where
		hql.append(" where ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		hql.append(" and ado.");
		hql.append(AbsDoacoes.Fields.BOL_NUMERO.toString());
		hql.append(" > 0");

		// group by
		hql.append(" group by");
		hql.append(" ado.");
		hql.append(AbsDoacoes.Fields.BOL_DATA.toString());
		hql.append(", ado.");
		hql.append(AbsDoacoes.Fields.SER_MATRICULA.toString());
		hql.append(", ado.");
		hql.append(AbsDoacoes.Fields.SER_VIN_CODIGO.toString());
		
		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}	
	
}