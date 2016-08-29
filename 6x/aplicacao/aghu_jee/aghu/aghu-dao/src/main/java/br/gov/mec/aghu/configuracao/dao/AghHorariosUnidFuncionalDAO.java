package br.gov.mec.aghu.configuracao.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioTipoDia;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

public class AghHorariosUnidFuncionalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghHorariosUnidFuncional> {
	
	
	private static final long serialVersionUID = 3028978058729675656L;

	public AghHorariosUnidFuncional obterHorarioUnidadeFuncionalPor(AghUnidadesFuncionais unidadeFuncional, DominioTipoDia tipoDia, Date dthrProgramada) {
		
		if (unidadeFuncional == null || tipoDia == null || dthrProgramada == null) {
		
			throw new IllegalArgumentException("Algum parametro obrigatorio nao foi informado!!!");
		
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghHorariosUnidFuncional.class);
		
		criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));
		criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.TIPO_DIA.toString(), tipoDia));
		
		String horaProgramada = DateFormatUtil.formataHoraMinuto(dthrProgramada);
		String query = "? between to_char(hr_inicial,'hh24:mi') and to_char(hr_final,'hh24:mi')";
		criteria.add(Restrictions.sqlRestriction(query, horaProgramada, StringType.INSTANCE));
		
		return (AghHorariosUnidFuncional) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * Retorna lista de AghHorariosUnidFuncional
	 * @param unfSeq
	 * @param tipoDia
	 * @param hrInicial
	 * @param hrFinal
	 * @param indPlantao
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<AghHorariosUnidFuncional> pesquisarAghHorariosUnidFuncional(Short unfSeq, DominioTipoDia tipoDia, Date hrInicial, Date hrFinal, Boolean indPlantao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghHorariosUnidFuncional.class);
		criteria.createCriteria(AghHorariosUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString(), "unf", Criteria.INNER_JOIN);
		
		if (unfSeq != null) {
			
			criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.UNF_SEQ.toString(), unfSeq));
			
		}
		
		if (tipoDia != null) {
			
			criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.TIPO_DIA.toString(), tipoDia));
			
		}
		
		if (indPlantao != null) {
			
			criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.IND_PLANTAO.toString(), indPlantao));
			
		}
		
		if (hrInicial != null) {
			
			SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
			criteria.add(Restrictions.sqlRestriction("To_char(hr_inicial, 'hh24:mi') = ? ",formatador.format(hrInicial), StringType.INSTANCE));
			
		}
		
		if (hrFinal != null) {
			
			SimpleDateFormat formatador2 = new SimpleDateFormat("HH:mm");
			criteria.add(Restrictions.sqlRestriction("To_char(hr_final, 'hh24:mi') = ? ",formatador2.format(hrFinal), StringType.INSTANCE));
			
		}
		
		criteria.addOrder(Order.asc("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(AghHorariosUnidFuncional.Fields.HR_INICIAL.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
	}
	
	/**
	 * Retorna o total de registros
	 * @param unfSeq
	 * @param tipoDia
	 * @param hrInicial
	 * @param hrFinal
	 * @param indPlantao
	 * @return
	 */
	public Long pesquisarAghHorariosUnidFuncionalCount(Short unfSeq, DominioTipoDia tipoDia, Date hrInicial, Date hrFinal, Boolean indPlantao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghHorariosUnidFuncional.class);
		
		if (unfSeq != null) {
			
			criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.UNF_SEQ.toString(), unfSeq));
			
		}
		
		if (tipoDia != null) {
			
			criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.TIPO_DIA.toString(), tipoDia));
			
		}
		
		if (indPlantao != null) {
			
			criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.IND_PLANTAO.toString(), indPlantao));
			
		}
		
		if (hrInicial != null) {
			
			SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
			criteria.add(Restrictions.sqlRestriction("To_char(hr_inicial, 'hh24:mi') = ? ",formatador.format(hrInicial), StringType.INSTANCE));
			
		}
		
		if (hrFinal != null) {
			
			SimpleDateFormat formatador2 = new SimpleDateFormat("HH:mm");
			criteria.add(Restrictions.sqlRestriction("To_char(hr_final, 'hh24:mi') = ? ",formatador2.format(hrFinal), StringType.INSTANCE));
			
		}
		
		return executeCriteriaCount(criteria);
		
	}
	
	/**
	 * Retorna AghHorariosUnidFuncional por Id
	 * @param id
	 * @return
	 */
	public AghHorariosUnidFuncional obterHorarioUnidadeFuncionalPorId(AghUnidadesFuncionais unidadeFuncional, 
			DominioTipoDia tipoDia, String horaProgramada) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghHorariosUnidFuncional.class);
		
		criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		criteria.add(Restrictions.eq(AghHorariosUnidFuncional.Fields.TIPO_DIA.toString(), tipoDia));
		criteria.add(Restrictions.sqlRestriction("To_char(hr_inicial, 'hh24:mi') = ? ",
				horaProgramada, StringType.INSTANCE));
		
		List<AghHorariosUnidFuncional> retorno = executeCriteria(criteria);
		
		if (!retorno.isEmpty()) {
			return retorno.get(0);
		}
		
		return null;
	}
	
}
