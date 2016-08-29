package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.Rhfp0283;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorVerifServStarhMatriculaQueryBuilder extends QueryBuilder<DetachedCriteria> {
	/**
	 * #42229 cursor c_starh da procedure RAPC_VESETEM_UNIMED
	 * 
	 * @author rodrigo.saraujo
	 */
	private static final String ASPAS_SIMPLES = "'"; 
	private static final long serialVersionUID = 5958820031058551779L;
	private DetachedCriteria criteria;
	private DetachedCriteria subCriteria;
	private Integer matricula;
	private Short vinCodigo;
	private String data;
	private boolean isOracle;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(Rhfp0283.class, "TIT");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setSubConsulta();
		setFiltro();
		setProjecao();
	}

	private void setSubConsulta() {
		this.subCriteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		ProjectionList projList = Projections.projectionList();
		projList = Projections.projectionList();
		projList.add(Projections.property("SER." + RapServidores.Fields.CODSTARH.toString()));
		subCriteria.setProjection(projList);
		subCriteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), this.matricula));
		subCriteria.add(Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), this.vinCodigo));
	}

	private void setFiltro() {
		criteria.add(Restrictions.in("TIT." + Rhfp0283.Fields.COD_PLANO_SAUDE.toString(), new Object[] { 1, 3 }));
		StringBuilder restricao = new StringBuilder(200);
		criteria.add(Subqueries.propertiesEq(new String[] { "TIT." + Rhfp0283.Fields.COD_CONTRATO.toString() }, subCriteria));
		restricao.append(ASPAS_SIMPLES).append(this.data).append("'  between {alias}.data_inicio");
		if (this.isOracle) {
			restricao.append(" and NVL(");
		} else {
			restricao.append(" and COALESCE(");
		}
		restricao.append("{alias}.data_fim,'").append(this.data).append("')");
		criteria.add(Restrictions.sqlRestriction(restricao.toString()));
	}

	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		StringBuilder nroCarteira = new StringBuilder(100);
		if (this.isOracle) {
			nroCarteira.append(" NVL({alias}.NUM_CARTEIRA,'0') nroCarteira");
		} else {
			nroCarteira.append(" COALESCE({alias}.NUM_CARTEIRA,'0') nroCarteira");
		}
		projList.add(Projections.sqlProjection(nroCarteira.toString(), new String[] { "nroCarteira" }, new Type[] { new StringType() }));
		criteria.setProjection(projList);
	}

	public DetachedCriteria build(Integer matricula, Short vinCodigo, String data, Boolean isOracle) {
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
		this.data = data;
		this.isOracle = isOracle;
		return super.build();
	}

}
