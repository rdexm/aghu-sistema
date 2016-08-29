package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapUniServDependente;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorVerifDepUniMatriculaQueryBuilder extends QueryBuilder<DetachedCriteria>{

	/**
	 *#42229 cursor c_dep da procedure RAPC_VESETEM_UNIMED
	 * @author rodrigo.saraujo
	 */
	private static final String ASPAS_SIMPLES = "'";
	private static final long serialVersionUID = 5958820031058551779L;
	private DetachedCriteria criteria;
	private Integer matricula;
	private Short vinCodigo;
	private Integer depCodigo;
	private String data;
	private boolean isOracle;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(RapUniServDependente.class, "RSD");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();
	}
	private void setFiltro() {
		criteria.add(Restrictions.eq("RSD."+RapUniServDependente.Fields.SAD_SER_MATRICULA.toString(), this.matricula));
		criteria.add(Restrictions.eq("RSD."+RapUniServDependente.Fields.SAD_SER_VIN_CODIGO.toString(), this.vinCodigo));
		criteria.add(Restrictions.eq("RSD."+RapUniServDependente.Fields.DEP_CODIGO.toString(), this.depCodigo));
		StringBuilder restricao = new StringBuilder(500);
		restricao.append(ASPAS_SIMPLES).append(this.data).append("'  between {alias}.dt_inicio");
		if(this.isOracle){
			restricao.append(" and NVL(");
		}else{
			restricao.append(" and COALESCE(");
		}
		restricao.append("{alias}.dt_fim,'").append(this.data).append("')");
		criteria.add(Restrictions.sqlRestriction(restricao.toString()));
		
	}
	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		StringBuilder nroCarteira = new StringBuilder(100);
		if(this.isOracle){
			nroCarteira.append(" NVL({alias}.NRO_CARTEIRA,0) nroCarteira");
		}else{
			nroCarteira.append(" COALESCE({alias}.NRO_CARTEIRA,0) nroCarteira");
		}
		projList.add(Projections.sqlProjection(nroCarteira.toString(), new String [] {"nroCarteira"}, new Type[] {new LongType()}));
		criteria.setProjection(projList);
	}
	public DetachedCriteria build(Integer matricula, Short vinCodigo,Integer depCodigo,String data ,Boolean isOracle) {
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
		this.depCodigo = depCodigo;
		this.data = data;
		this.isOracle = isOracle;
		return super.build();
	}
	

}
