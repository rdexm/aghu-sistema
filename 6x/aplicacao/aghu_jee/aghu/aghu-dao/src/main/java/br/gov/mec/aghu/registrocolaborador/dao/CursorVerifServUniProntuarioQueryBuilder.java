package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.RapUniServPlano;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;


public class CursorVerifServUniProntuarioQueryBuilder extends QueryBuilder<DetachedCriteria> {
	/**
	 * cursor c_ser2 da procedure RAPC_VESETEM_UNIMED Incluido pela estória #42229
	 * 
	 * @author rodrigo.saraujo
	 */
	private static final String ASPAS_SIMPLES = "'";
	private static final long serialVersionUID = -3293732090919928123L;
	private Integer prontuario;
	private String data;
	private boolean isOracle;
	private DetachedCriteria criteria;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(RapUniServPlano.class, "RSP");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();
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
		StringBuilder restricao = new StringBuilder(1000);
		restricao.append("exists (select 1 from agh.RAP_SERVIDORES ser,agh.RAP_PESSOAS_FISICAS rpf2  where {alias}.sad_ser_matricula=ser.MATRICULA and {alias}.sad_ser_vin_codigo=ser.VIN_CODIGO and ser.PES_CODIGO=rpf2.CODIGO and rpf2.PAC_PRONTUARIO=")
		.append(this.prontuario).append(" )");
		criteria.add(Restrictions.sqlRestriction(restricao.toString()));
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
