package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.GrupoProcedRiscoVO;
import br.gov.mec.aghu.model.MciGrupoProcedRisco;
import br.gov.mec.aghu.model.MciGrupoProcedRiscoId;
import br.gov.mec.aghu.model.RapServidores;



public class MciGrupoProcedRiscoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciGrupoProcedRisco> {

	private static final long serialVersionUID = -2309909471166319673L;

	//# 37968 consulta para o D1
	public List<MciGrupoProcedRisco> pesquisarMciGrupoProcedRiscoPor(Short porSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciGrupoProcedRisco.class);
		if(porSeq != null) {
			criteria.add(Restrictions.eq(MciGrupoProcedRisco.Fields.POR_SEQ.toString(), porSeq));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(MciGrupoProcedRisco.class));
		return executeCriteria(criteria);
	}
	
	// #37968 
	public GrupoProcedRiscoVO obterGrupoProcedRiscoComRelacionamento(MciGrupoProcedRiscoId id) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MciGrupoProcedRisco.class,"GPR");
			criteria.add(Restrictions.eq(MciGrupoProcedRisco.Fields.POR_SEQ.toString(), id.getPorSeq()));
			criteria.add(Restrictions.eq(MciGrupoProcedRisco.Fields.TGP_SEQ.toString(), id.getTgpSeq()));
			
			criteria.createAlias("GPR." + MciGrupoProcedRisco.Fields.RAP_SERVIDORES_BY_MCI_GRS_SER_FK2.toString(), "SER", JoinType.INNER_JOIN);
			
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), GrupoProcedRiscoVO.Fields.MATRICULA.toString())
					.add(Projections.property("SER." + RapServidores.Fields.CODIGO_VINCULO.toString()), GrupoProcedRiscoVO.Fields.CODIGO_VINCULO.toString())
			);
				
			criteria.setResultTransformer(Transformers.aliasToBean(GrupoProcedRiscoVO.class));
			return (GrupoProcedRiscoVO) this.executeCriteriaUniqueResult(criteria);
	}
	
}
