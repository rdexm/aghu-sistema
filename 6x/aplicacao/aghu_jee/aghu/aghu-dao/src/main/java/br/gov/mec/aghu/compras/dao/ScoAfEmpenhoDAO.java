package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DoubleType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoAfEmpenho;



public class ScoAfEmpenhoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAfEmpenho>{
	
	private static final long serialVersionUID = 7560943552026609158L;
	
	
	public List<ScoAfEmpenho>  buscarEmpenhoPorAfNum(Integer numeroAf){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAfEmpenho.class);
		criteria.add(Restrictions.eq(ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq(ScoAfEmpenho.Fields.IND_ENVIADO.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq(ScoAfEmpenho.Fields.IND_CONFIRMADO_SIAFI.toString(), DominioSimNao.S));
		
		return executeCriteria(criteria);
	}
	
	
	public Integer  buscarMaxEmpenhoPorAfNumEEspecie(Integer numeroAf , int espEmpenho){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAfEmpenho.class, "AFO");
		criteria.setProjection(Projections.max("AFO."+ScoAfEmpenho.Fields.SEQ));  
		criteria.add(Restrictions.eq("AFO."+ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq("AFO."+ScoAfEmpenho.Fields.ESPECIE.toString(), espEmpenho));
			
			return (Integer) executeCriteriaUniqueResult(criteria);
		}
	
	/**
	 * #27143
	 * @author marcelo.deus <br/>
	 * Consulta que busca o total empenhado
	 */
	public Double totalEmpenhadoRelatorioEntradasSemEmpenhoSemAssinaturaAF(Integer afnNumero){
		Double result = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAfEmpenho.class);
		
		criteria.add(Restrictions.eq(ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));
		//criteria.setProjection(Projections.projectionList().add(Projections.sum(ScoAfEmpenho.Fields.VALOR.toString())));
		criteria.setProjection(Projections.projectionList().add(Projections.sqlProjection("sum({alias}.VALOR) as VALOR", new String[] { "VALOR"}, new Type[] { DoubleType.INSTANCE })));
		
		result = (Double) executeCriteriaUniqueResult(criteria);
		if(result != null && result > 0){
			return result;
		} else {
			result = new Double("0");
		}
		return result;
	}
}
