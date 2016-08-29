package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.Rhfp0285;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * #42229 cursor c_dep2_starh da procedure RAPC_VESETEM_UNIMED
 * @author rodrigo.saraujo
 *
 */
public class CursorVerifDepStarhProntuarioQueryBuilder extends QueryBuilder<DetachedCriteria>{
	/**
	 * #42229 Verifica se dependente tem unimed pelo prontuario no Starh
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
		return DetachedCriteria.forClass(Rhfp0285.class, "TIT");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setSubConsulta();
		setProjecao();
		setFiltro();
	}
	
	private void setSubConsulta(){
		this.subCriteria = DetachedCriteria.forClass(RapDependentes.class, "DEP");
		ProjectionList projList = Projections.projectionList();
		projList = Projections.projectionList();
		projList.add(Projections.property("DEP." + RapDependentes.Fields.CODIGO.toString()));
		subCriteria.setProjection(projList);
		subCriteria.add(Restrictions.eq("DEP."+RapDependentes.Fields.PAC_PRONTUARIO, this.prontuario));
	}
	
	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		StringBuilder nroCarteira = new StringBuilder(100);
		if(this.isOracle){
			nroCarteira.append(" NVL({alias}.NUM_CARTEIRA,'0') nroCarteira");
		}else{
			nroCarteira.append(" COALESCE({alias}.NUM_CARTEIRA,'0') nroCarteira");
		}
		projList.add(Projections.sqlProjection(nroCarteira.toString(), new String [] {"nroCarteira"}, new Type[] {new StringType()}));
		criteria.setProjection(projList);
	}
	public DetachedCriteria build(Integer prontuario, String data,Boolean isOracle) {
		this.prontuario = prontuario;
		this.data = data;
		this.isOracle = isOracle;
		return super.build();
	}
	private void setFiltro() {
		StringBuilder restricao = new StringBuilder(250);
		
		criteria.add(Restrictions.in("TIT." + Rhfp0285.Fields.COD_PLANO_SAUDE.toString(), new Object[]{1,3}));
		criteria.add(Subqueries.propertiesEq(new String[] {"TIT." + Rhfp0285.Fields.COD_PESSOA.toString()}, subCriteria));
		restricao.append(ASPAS_SIMPLES).append(this.data).append("'  between {alias}.data_inicio_dep");
		if(this.isOracle){
			restricao.append(" and NVL(");
		}else{
			restricao.append(" and COALESCE(");
		}
		restricao.append("{alias}.data_fim_dep,'").append(this.data).append("')");
		criteria.add(Restrictions.sqlRestriction(restricao.toString()));
		
	}
}
