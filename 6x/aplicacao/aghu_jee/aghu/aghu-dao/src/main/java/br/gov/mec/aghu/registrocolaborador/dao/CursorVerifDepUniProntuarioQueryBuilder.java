package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapUniServDependente;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 *
 *
 */
public class CursorVerifDepUniProntuarioQueryBuilder extends QueryBuilder<DetachedCriteria> {
	/**
	 * #42229 cursor c_dep2 da procedure RAPC_VESETEM_UNIMED
	 * 
	 * @author rodrigo.saraujo
	 */
	private static final String ASPAS_SIMPLES = "'";
	private static final long serialVersionUID = -802445744895370859L;
	private Integer prontuario;
	private String data;
	private boolean isOracle;
	private DetachedCriteria criteria;
	private DetachedCriteria subCriteria;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(RapUniServDependente.class, "DUN");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setSubConsulta();
		setProjecao();
		setFiltro();
	}

	private void setSubConsulta() {
		this.subCriteria = DetachedCriteria.forClass(RapDependentes.class, "DEP");
		ProjectionList projList = Projections.projectionList();
		projList = Projections.projectionList();
		projList.add(Projections.property("DEP." + RapDependentes.Fields.PES_CODIGO.toString()));
		projList.add(Projections.property("DEP." + RapDependentes.Fields.CODIGO.toString()));
		subCriteria.setProjection(projList);
		subCriteria.add(Restrictions.eq("DEP." + RapDependentes.Fields.PAC_PRONTUARIO, this.prontuario));
	}

	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		StringBuilder nroCarteira = new StringBuilder(100);
		if (this.isOracle) {
			nroCarteira.append(" NVL({alias}.NRO_CARTEIRA,0) nroCarteira");
		} else {
			nroCarteira.append(" COALESCE({alias}.NRO_CARTEIRA,0) nroCarteira");
		}
		projList.add(Projections.sqlProjection(nroCarteira.toString(), new String[] { "nroCarteira" }, new Type[] { new LongType() }));
		criteria.setProjection(projList);
	}

	public DetachedCriteria build(Integer prontuario, String data, Boolean isOracle) {
		this.prontuario = prontuario;
		this.data = data;
		this.isOracle = isOracle;
		return super.build();
	}

	private void setFiltro() {
		StringBuilder restricao = new StringBuilder(200);
		criteria.add(Subqueries.propertiesEq(new String[] { "DUN." + RapUniServDependente.Fields.DEP_PES_CODIGO.toString(), "DUN." + RapUniServDependente.Fields.DEP_CODIGO.toString() }, subCriteria));
		restricao = new StringBuilder(500);
		restricao.append(ASPAS_SIMPLES).append(this.data).append("'  between {alias}.dt_inicio");
		if (this.isOracle) {
			restricao.append(" and NVL(");
		} else {
			restricao.append(" and COALESCE(");
		}
		restricao.append("{alias}.dt_fim,'").append(this.data).append("')");
		criteria.add(Restrictions.sqlRestriction(restricao.toString()));

	}
}
