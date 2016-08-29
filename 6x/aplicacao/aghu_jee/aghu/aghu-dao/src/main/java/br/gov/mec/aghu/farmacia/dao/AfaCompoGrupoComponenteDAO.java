package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AfaCompoGrupoComponenteVO;

/**
 * @author marcelo.corati
 *
 */
public class AfaCompoGrupoComponenteDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaCompoGrupoComponente> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3337925368164950607L;

	public List<AfaCompoGrupoComponenteVO> obterListaCompoGrupo(Short gcnSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaCompoGrupoComponente.class,"ACG");
		
		criteria.createAlias(AfaCompoGrupoComponente.Fields.AFA_GRUPO_COMPONENTE_NPTS.toString(), "AGC", JoinType.INNER_JOIN);
		criteria.createAlias(AfaCompoGrupoComponente.Fields.AFA_TIPO_COMPOSICOES.toString(), "ATC", JoinType.INNER_JOIN);
		criteria.createAlias(AfaCompoGrupoComponente.Fields.RAP_SERVIDORES_BY_AFA_TCG_SER_FK1.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaCompoGrupoComponente.Fields.RAP_SERVIDORES_BY_AFA_TCG_SER_FK2.toString(), "SER2", JoinType.LEFT_OUTER_JOIN);
		
		if(gcnSeq != null){
			criteria.add(Restrictions.eq(AfaCompoGrupoComponente.Fields.GCN_SEQ.toString(), gcnSeq));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ACG."+AfaCompoGrupoComponente.Fields.CRIADO_EM.toString()), AfaCompoGrupoComponenteVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("ACG."+AfaCompoGrupoComponente.Fields.ALTERADO_EM.toString()), AfaCompoGrupoComponenteVO.Fields.ALTERADO_EM.toString())
				.add(Projections.property("ACG."+AfaCompoGrupoComponente.Fields.IND_SITUACAO.toString()), AfaCompoGrupoComponenteVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("SER."+RapServidores.Fields.USUARIO.toString()), AfaCompoGrupoComponenteVO.Fields.USUARIO.toString())
				.add(Projections.property("SER2."+RapServidores.Fields.USUARIO.toString()), AfaCompoGrupoComponenteVO.Fields.USUARIO2.toString())
				.add(Projections.property("ATC."+AfaTipoComposicoes.Fields.SEQ.toString()), AfaCompoGrupoComponenteVO.Fields.TIC_SEQ.toString())
				.add(Projections.property("ATC."+AfaTipoComposicoes.Fields.DESCRICAO.toString()), AfaCompoGrupoComponenteVO.Fields.DESCRICAO.toString())
				.add(Projections.property("AGC."+AfaGrupoComponenteNpt.Fields.SEQ.toString()), AfaCompoGrupoComponenteVO.Fields.GCN_SEQ.toString())
				);
		
		criteria.addOrder(Order.asc(AfaCompoGrupoComponente.Fields.TIC_SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AfaCompoGrupoComponenteVO.class));
		return this.executeCriteria(criteria);
		
	}

}
