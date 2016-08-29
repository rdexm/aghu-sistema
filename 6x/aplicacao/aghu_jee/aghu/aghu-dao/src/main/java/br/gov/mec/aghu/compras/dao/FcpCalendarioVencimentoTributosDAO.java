package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioDiasMes;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.model.FcpCalendarioVencimentoTributos;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class FcpCalendarioVencimentoTributosDAO extends BaseDao<FcpCalendarioVencimentoTributos> {

	private static final long serialVersionUID = -3217015915443627898L;
	
	
	public List<FcpCalendarioVencimentoTributos> pesquisarCalendarioVencimentoPorApuracao(Date inicioApuracao, Date fimApuracao,
			DominioTipoTributo tipoTributo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpCalendarioVencimentoTributos.class, "CV1");
		
		if(tipoTributo != null){
			criteria.add(Restrictions.eq("CV1." + FcpCalendarioVencimentoTributos.Fields.TIPO_TRIBUTO.toString(), tipoTributo));
		}
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FcpCalendarioVencimentoTributos.class, "CV2");
		subCriteria.setProjection(Projections.max("CV2." + FcpCalendarioVencimentoTributos.Fields.INICIO_VIGENCIA.toString()));
		subCriteria.add(Restrictions.eqProperty("CV2." + FcpCalendarioVencimentoTributos.Fields.TIPO_TRIBUTO.toString(), 
				"CV1." + FcpCalendarioVencimentoTributos.Fields.TIPO_TRIBUTO.toString()));
		subCriteria.add(Restrictions.le("CV2." + FcpCalendarioVencimentoTributos.Fields.INICIO_VIGENCIA.toString(), inicioApuracao));
		
		Criterion subSelect1 = Subqueries.propertyEq("CV1." + FcpCalendarioVencimentoTributos.Fields.INICIO_VIGENCIA.toString(), subCriteria);
		
		Criterion vigencia1 = Restrictions.ge("CV1." + FcpCalendarioVencimentoTributos.Fields.INICIO_VIGENCIA.toString(), inicioApuracao);
		Criterion vigencia2 = Restrictions.le("CV1." + FcpCalendarioVencimentoTributos.Fields.INICIO_VIGENCIA.toString(), fimApuracao);
		
		criteria.add(Restrictions.or(subSelect1, Restrictions.and(vigencia1, vigencia2)));
		
		criteria.addOrder(Order.asc("CV1." + FcpCalendarioVencimentoTributos.Fields.TIPO_TRIBUTO.toString()))
			.addOrder(Order.asc("CV1." + FcpCalendarioVencimentoTributos.Fields.INICIO_VIGENCIA.toString()))
			.addOrder(Order.asc("CV1." + FcpCalendarioVencimentoTributos.Fields.INICIO_PERIODO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Boolean verificarSobreposicaoPeriodos(DominioDiasMes inicioPeriodo, DominioDiasMes fimPeriodo, Date inicioVigencia, DominioTipoTributo tipoTributo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpCalendarioVencimentoTributos.class);
		
		criteria.add(Restrictions.eq(FcpCalendarioVencimentoTributos.Fields.TIPO_TRIBUTO.toString(), tipoTributo));
		criteria.add(Restrictions.eq(FcpCalendarioVencimentoTributos.Fields.INICIO_VIGENCIA.toString(), inicioVigencia));
		
		StringBuilder sbInicio = new StringBuilder(200);
		sbInicio.append("( ? BETWEEN {alias}.").append("INICIO_PERIODO")
		.append(" and {alias}.").append("FIM_PERIODO")
		.append(" )");
		
		Criterion btwInicioSql = Restrictions.sqlRestriction(sbInicio.toString(), new Object[]{inicioPeriodo.ordinal()}, new Type[]{IntegerType.INSTANCE});
		Criterion btwFimSql = Restrictions.sqlRestriction(sbInicio.toString(), new Object[]{fimPeriodo.ordinal()}, new Type[]{IntegerType.INSTANCE});
		
		Criterion gtInicio = Restrictions.gt(FcpCalendarioVencimentoTributos.Fields.INICIO_PERIODO.toString(), inicioPeriodo);
		Criterion ltFim = Restrictions.lt(FcpCalendarioVencimentoTributos.Fields.FIM_PERIODO.toString(), fimPeriodo);
		
		criteria.add(Restrictions.or(
				btwInicioSql, Restrictions.or(
						btwFimSql, Restrictions.and(gtInicio, ltFim))));
		
		return executeCriteriaExists(criteria);
	}

}
