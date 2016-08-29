package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.exames.vo.LiberacaoLimitacaoExameVO;
import br.gov.mec.aghu.model.AelExamesLimitadoAtend;
import br.gov.mec.aghu.model.AelExamesLimitadoAtendId;
import br.gov.mec.aghu.model.VAelExameMatAnalise;

public class AelExamesLimitadoAtendDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesLimitadoAtend> {


	private static final long serialVersionUID = -4519809666001931959L;

	public AelExamesLimitadoAtend obterPeloId(AelExamesLimitadoAtendId id) {
		if (id == null) {
			return null;
		}
		return super.obterPorChavePrimaria(id);
	}

	public List<LiberacaoLimitacaoExameVO> pesquisarLiberacaoLimitacaoExames(Integer atdSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesLimitadoAtend.class,"ELA");
		criteria.add(Restrictions.eq(AelExamesLimitadoAtend.Fields.ATD_SEQ.toString(), atdSeq));	
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(VAelExameMatAnalise.class, "VEM");
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property(VAelExameMatAnalise.Fields.SIGLA.toString()));
		projection.add(Projections.property(VAelExameMatAnalise.Fields.MAN_SEQ.toString()));
		subCriteria.setProjection(projection);
		subCriteria.add(Restrictions.eqProperty("ELA."+AelExamesLimitadoAtend.Fields.EMA_EXA_SIGLA.toString(), "VEM."+VAelExameMatAnalise.Fields.SIGLA.toString()));
		subCriteria.add(Restrictions.eqProperty("ELA."+AelExamesLimitadoAtend.Fields.EMA_MAN_SEQ.toString(), "VEM."+VAelExameMatAnalise.Fields.MAN_SEQ.toString()));
		criteria.add(Subqueries.exists(subCriteria));
		
		ProjectionList projectionPrincipal = Projections.projectionList();
		projectionPrincipal.add(Property.forName("ELA."+AelExamesLimitadoAtend.Fields.EMA_EXA_SIGLA.toString()),"sigla");
		projectionPrincipal.add(Property.forName("ELA."+AelExamesLimitadoAtend.Fields.EMA_MAN_SEQ.toString()), "manSeq");
		projectionPrincipal.add(Property.forName("ELA."+AelExamesLimitadoAtend.Fields.DTHR_LIMITE.toString()),"dthrLimite");
		projectionPrincipal.add(Property.forName("ELA."+AelExamesLimitadoAtend.Fields.ATD_SEQ.toString()),"atdSeq");
		criteria.setProjection(projectionPrincipal);
		
		criteria.setResultTransformer(Transformers.aliasToBean(LiberacaoLimitacaoExameVO.class));
			
		return executeCriteria(criteria);	
	}
}
