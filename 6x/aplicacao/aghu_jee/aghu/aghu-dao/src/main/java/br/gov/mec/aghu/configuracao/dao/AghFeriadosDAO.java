package br.gov.mec.aghu.configuracao.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioMesFeriado;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * 
 */
public class AghFeriadosDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AghFeriados> {

	
	private static final long serialVersionUID = 4127004793275675298L;


	public AghFeriados obterFeriado(Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghFeriados.class);
		criteria.add(Restrictions.eq(AghFeriados.Fields.DATA.toString(), DateUtils.truncate(
				data, Calendar.DAY_OF_MONTH)));
		return (AghFeriados) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean verificarExisteFeriado(Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghFeriados.class);
		criteria.add(Restrictions.eq(AghFeriados.Fields.DATA.toString(), data));
		return executeCriteriaExists(criteria);
	}
	
	public AghFeriados obterFeriadoSemTurno(Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghFeriados.class);
		criteria.add(Restrictions.eq(AghFeriados.Fields.DATA.toString(), data));
		criteria.add(Restrictions.isNull(AghFeriados.Fields.TURNO.toString()));
		return (AghFeriados) executeCriteriaUniqueResult(criteria);
	}
	
	protected DetachedCriteria obterCriteriaFeriadosEntreDatas(Date inicio, Date fim) {
		
		DetachedCriteria result = null;
		
		result = DetachedCriteria.forClass(AghFeriados.class);
		result.add(Restrictions.between(AghFeriados.Fields.DATA.toString(), inicio, fim));
		
		return result;
	}
	
	public List<AghFeriados> obterListaFeriadosEntreDatas(Date inicio, Date fim) {
		
		List<AghFeriados> result = null;
		DetachedCriteria criteria = null;		
		
		criteria = this.obterCriteriaFeriadosEntreDatas(inicio, fim);
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	public Long obterQtdFeriadosEntreDatasSemFindeSemTurno(Date inicio, Date fim) {
		Long result = null;
		DetachedCriteria criteria = null;		
		
		criteria = this.obterCriteriaFeriadosEntreDatas(inicio, fim);
		criteria.add(Restrictions.isNull(AghFeriados.Fields.TURNO.toString()));
		criteria.add(Restrictions.sqlRestriction(" TO_CHAR(this_.data,'DY') not in  ('SAT','SUN') "));
		result = this.executeCriteriaCount(criteria);
		
		return result;
	}
	
	public Long obterQtdFeriadosEntreDatas(Date inicio, Date fim) {
		
		Long result = null;
		DetachedCriteria criteria = null;		
		
		criteria = this.obterCriteriaFeriadosEntreDatas(inicio, fim);
		result = this.executeCriteriaCount(criteria);
		
		return result;
	}
	
	
	public List<AghFeriados> pesquisarFeriadoPaginado(Date data, DominioMesFeriado mes, 
			Integer ano, DominioTurno turno, Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc){
				
		DetachedCriteria criteria = DetachedCriteria.forClass(AghFeriados.class);
		
		criteria = this.obterCriteriaPesquisaFeriadoPaginado(data, mes, ano, turno, criteria);
			
		if (StringUtils.isBlank(orderProperty)) {
			orderProperty = AghFeriados.Fields.DATA.toString();
			asc = false;
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	
	public Long countPesquisaFeriado(Date data, DominioMesFeriado mes, Integer ano, DominioTurno turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghFeriados.class);
		
		criteria = this.obterCriteriaPesquisaFeriadoPaginado(data, mes, ano, turno, criteria);
		
		return executeCriteriaCount(criteria);
	}
	
	
	protected DetachedCriteria obterCriteriaPesquisaFeriadoPaginado(Date data, 
			DominioMesFeriado mes, Integer ano, DominioTurno turno, DetachedCriteria criteria) {
		
		if(data != null) {
			criteria.add(Restrictions.eq(AghFeriados.Fields.DATA.toString(), data));
		}
		
		if(turno != null) {
			criteria.add(Restrictions.eq(AghFeriados.Fields.TURNO.toString(), turno));
		}

		if(mes != null) {
			criteria.add(Restrictions.sqlRestriction("to_char( " + 
					AghFeriados.Fields.DATA.toString() + ",'MM') = " + 
					"'" + mes.getDescricao() + "'"));
		
		}
		
		if(ano != null) {
			criteria.add(Restrictions.sqlRestriction("to_char( " + 
					AghFeriados.Fields.DATA.toString() + ",'yyyy') = " + 
					"'" + ano + "'"));
		
		}
		
		return criteria;
	}
	
	public AghFeriados obterFeriadoSemTurnoDataTruncada(Date data) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghFeriados.class);
		criteria.add(Restrictions.eq(AghFeriados.Fields.DATA.toString(), DateUtil.truncaData(data)));
		criteria.add(Restrictions.isNull(AghFeriados.Fields.TURNO.toString()));
		return (AghFeriados) executeCriteriaUniqueResult(criteria);
	}
}