package br.gov.mec.aghu.orcamento.dao;

import java.math.BigDecimal;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FsoExercicioOrcamentario;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoPrevisaoOrcamentaria;

public class FsoPrevisaoOrcamentariaDAO extends	br.gov.mec.aghu.core.persistence.dao.BaseDao<FsoPrevisaoOrcamentaria> {

	private static final long serialVersionUID = -374646229964275828L;

	public BigDecimal obterSaldoOrcamento(final Integer codigo) {
		DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.createAlias(FsoPrevisaoOrcamentaria.Fields.EXEC_ORCAMENTARIO.toString(), "TAB1");
		criteria.add(Restrictions.eq("TAB1." + FsoExercicioOrcamentario.Fields.EXEC_CORRENTE.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(FsoPrevisaoOrcamentaria.Fields.GRUPO_NAT_DESPZ.toString() + "." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), codigo));
		criteria.setProjection(Projections.projectionList().add((Projections.sum(FsoPrevisaoOrcamentaria.Fields.VALOR_ORCADO.toString())))
				.add(Projections.sum(FsoPrevisaoOrcamentaria.Fields.VALOR_COMPROMETIDO.toString())));
		Object retorno = executeCriteriaUniqueResult(criteria);
		if (retorno == null) {
			return null;
		}
		Object[] vals = (Object[]) retorno;
		if (vals[0] == null || vals[1] == null) {
			return null;
		}
		return ((BigDecimal) vals[0]).subtract((BigDecimal) vals[1]);
	}
}
