package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.Rhfp0285;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
/**
 * Verifica se dependente tem unimed pela matricula no Starh
 * #42229 cursor c_dep_starh
  */
public class CursorVerifDepStarhMatriculaQueryBuilder extends QueryBuilder<DetachedCriteria>{

	/**
	 * 
	 */
	private static final String ASPAS_SIMPLES = "'";
	private static final long serialVersionUID = 5958820031058551779L;
	private DetachedCriteria criteria;
	private Integer matricula;
	private Short vinCodigo;
	private Integer depCodigo;
	private String data;
	private boolean isOracle;
	private DetachedCriteria subCriteria;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(Rhfp0285.class, "TIT");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setSubConsulta();
		setFiltro();
		setProjecao();
	}
	
	private void setSubConsulta(){
		this.subCriteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		ProjectionList projList = Projections.projectionList();
		projList = Projections.projectionList();
		projList.add(Projections.property("SER." + RapServidores.Fields.CODSTARH.toString()));
		subCriteria.setProjection(projList);
		subCriteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), this.matricula));
		subCriteria.add(Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), this.vinCodigo));
	}
	
	private void setFiltro() {
		StringBuilder restricao = new StringBuilder(250);
		
		criteria.add(Restrictions.in("TIT." + Rhfp0285.Fields.COD_PLANO_SAUDE.toString(), new Object[]{1,3}));
		criteria.add(Restrictions.eq("TIT."+Rhfp0285.Fields.COD_PESSOA.toString(), this.depCodigo));
		restricao.append(ASPAS_SIMPLES).append(this.data).append("'  between {alias}.data_inicio_dep");
		if(this.isOracle){
			restricao.append(" and NVL(");
		}else{
			restricao.append(" and COALESCE(");
		}
		restricao.append("{alias}.data_fim_dep,'").append(this.data).append("')");
		criteria.add(Restrictions.sqlRestriction(restricao.toString()));
		
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
	public DetachedCriteria build(Integer matricula, Short vinCodigo,Integer depCodigo,String data ,Boolean isOracle) {
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
		this.depCodigo = depCodigo;
		this.data = data;
		this.isOracle = isOracle;
		return super.build();
	}
}
