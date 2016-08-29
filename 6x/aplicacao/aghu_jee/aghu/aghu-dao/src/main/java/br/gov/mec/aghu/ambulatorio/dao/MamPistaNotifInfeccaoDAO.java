package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.MamPistaNotifInfeccao;
import br.gov.mec.aghu.model.MamRespostaNotifInfeccao;

public class MamPistaNotifInfeccaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamPistaNotifInfeccao> {

	private static final long serialVersionUID = -5670796868498506969L;
	
	/**
	 * #42360 - Usada na Procedure 1
	 * Obtem o prontuario de um determinado paciente a partir do código do paciente.
	 * @param pacCodigo
	 * @return prontuario
	 */
	public MamPistaNotifInfeccao obterMamPistaNotifInfeccao(Integer pConNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamPistaNotifInfeccao.class,"PNN");
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamRespostaNotifInfeccao.class,"RNI");
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection(" 1 as existe", new String[]{"existe"}, new Type[] { StringType.INSTANCE }), "existe");		
		subCriteria.setProjection(projection);
		subCriteria.add(Restrictions.eqProperty("RNI." + MamRespostaNotifInfeccao.Fields.PNN_SEQ.toString(), 
				"PNN." + MamPistaNotifInfeccao.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq("RNI." + MamRespostaNotifInfeccao.Fields.CONSULTA_NUMERO.toString(), pConNumero));
		
		Disjunction dis = Restrictions.disjunction();
		dis.add(Restrictions.eq("PNN." + MamPistaNotifInfeccao.Fields.CONSULTA_CON_NUMERO.toString(), pConNumero));
		dis.add(Subqueries.exists(subCriteria));
		
		criteria.add(dis);

		return (MamPistaNotifInfeccao)this.executeCriteriaUniqueResult(criteria);
	}
	
	
	
	
}
