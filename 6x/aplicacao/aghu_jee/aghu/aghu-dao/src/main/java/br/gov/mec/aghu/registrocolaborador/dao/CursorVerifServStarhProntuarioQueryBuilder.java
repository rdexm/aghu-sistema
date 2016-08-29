package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.Rhfp0283;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;


public class CursorVerifServStarhProntuarioQueryBuilder extends QueryBuilder<DetachedCriteria> {
	/**
	 * #42229 cursor c_serv2_starh Verifica se servidor tem unimed pelo prontuario 
	 */
	private static final long serialVersionUID = -3293732090919928123L;
	private Integer prontuario;
	private String data;
	private boolean isOracle;
	private DetachedCriteria criteria;
	private DetachedCriteria subConsultaSer;
	private DetachedCriteria subConsultaPes;
	private static final String ASPAS_SIMPLES = "'"; 

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(Rhfp0283.class, "TIT");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setSubConsultaPes();
		setSubConsultaSer();
		setFiltro();
		setProjecao();
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

	public DetachedCriteria build(Integer prontuario, String data, Boolean isOracle) {
		this.prontuario = prontuario;
		this.data = data;
		this.isOracle = isOracle;
		return super.build();
	}

	/**
	 * Subconsulta RapServidores as ser
	 */
	private void setSubConsultaSer() {
		this.subConsultaSer = DetachedCriteria.forClass(RapServidores.class, "SER");
		ProjectionList projList = Projections.projectionList();
		projList = Projections.projectionList();
		projList.add(Projections.property("SER." + RapServidores.Fields.CODSTARH.toString()));
		subConsultaSer.setProjection(projList);
		subConsultaSer.add(Subqueries.propertiesEq(new String[] { "SER." + RapServidores.Fields.PES_CODIGO.toString() }, this.subConsultaPes));
	}

	/**
	 * Subconsulta RapPessoasFisicas as pes
	 */
	private void setSubConsultaPes() {
		this.subConsultaPes = DetachedCriteria.forClass(RapPessoasFisicas.class, "PES");
		ProjectionList projList = Projections.projectionList();
		projList = Projections.projectionList();
		projList.add(Projections.property("PES." + RapPessoasFisicas.Fields.CODIGO.toString()));
		this.subConsultaPes.setProjection(projList);
		this.subConsultaPes.add(Restrictions.eq("PES." + RapPessoasFisicas.Fields.PRONTUARIO.toString(), this.prontuario));
	}

	private void setFiltro() {
		criteria.add(Subqueries.propertiesEq(new String[] { "TIT." + Rhfp0283.Fields.COD_CONTRATO.toString() }, this.subConsultaSer));
		criteria.add(Restrictions.in("TIT." + Rhfp0283.Fields.COD_PLANO_SAUDE.toString(), new Object[] { 1, 3 }));
		StringBuilder restricao = new StringBuilder(250);
		restricao.append(ASPAS_SIMPLES).append(this.data).append("'  between {alias}.data_inicio");
		if (this.isOracle) {
			restricao.append(" and NVL(");
		} else {
			restricao.append(" and COALESCE(");
		}
		restricao.append("{alias}.data_fim,'").append(this.data).append("')");
		criteria.add(Restrictions.sqlRestriction(restricao.toString()));

	}
}
