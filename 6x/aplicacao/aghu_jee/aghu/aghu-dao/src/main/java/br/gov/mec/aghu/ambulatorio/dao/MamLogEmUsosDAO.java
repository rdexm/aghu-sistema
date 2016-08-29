package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamLogEmUsos;
import br.gov.mec.aghu.model.RapServidores;

public class MamLogEmUsosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamLogEmUsos> {

	private static final long serialVersionUID = 6692910253651671321L;

	public List<MamLogEmUsos> pesquisarLogEmUsoPorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamLogEmUsos.class);
		criteria.add(Restrictions.eq(MamLogEmUsos.Fields.CON_NUMERO.toString(),
				consultaNumero));
		return executeCriteria(criteria);
	}

	public Date obterUltimaDataPorConsultaEServidor(Integer conNumero, Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLogEmUsos.class);
		criteria.createAlias(MamLogEmUsos.Fields.SERVIDOR.toString(), MamLogEmUsos.Fields.SERVIDOR.toString());
		//criteria.createAlias(MamLogEmUsos.Fields.SERVIDOR.toString()+"."+RapServidores.Fields.ID.toString(), MamLogEmUsos.Fields.SERVIDOR.toString()+"."+RapServidores.Fields.ID.toString());
		criteria.add(Restrictions.eq(MamLogEmUsos.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(MamLogEmUsos.Fields.SERVIDOR.toString()+"."+RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(MamLogEmUsos.Fields.SERVIDOR.toString()+"."+RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		criteria.setProjection(Projections.max(AacConsultas.Fields.CRIADO_EM.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public List<MamLogEmUsos> listaLogsEmUsoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLogEmUsos.class);

		criteria.createAlias(MamLogEmUsos.Fields.CONSULTA.toString(), MamLogEmUsos.Fields.CONSULTA.toString());

		criteria.add(Restrictions.eq(MamLogEmUsos.Fields.CON_NUMERO.toString(), numeroConsulta));

		return executeCriteria(criteria);
	}
	
}
