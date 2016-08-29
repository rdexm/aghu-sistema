package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AfaGrupoComponenteNptVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @author marcelo.corati
 *
 */
public class AfaGrupoComponenteNptDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaGrupoComponenteNpt> {

	private static final long serialVersionUID = 4070084250474804889L;

	public List<AfaGrupoComponenteNptVO> obterListaGrupoComponentes(Short seq, DominioSituacao situacao, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaGrupoComponenteNpt.class,"AGC");
		
		criteria.createAlias(AfaGrupoComponenteNpt.Fields.RAP_SERVIDORES.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		if(seq != null){
			criteria.add(Restrictions.eq(AfaGrupoComponenteNpt.Fields.SEQ.toString(), seq));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(AfaGrupoComponenteNpt.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(AfaGrupoComponenteNpt.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("AGC."+AfaGrupoComponenteNpt.Fields.SEQ.toString()), AfaGrupoComponenteNptVO.Fields.SEQ.toString())
				.add(Projections.property("AGC."+AfaGrupoComponenteNpt.Fields.DESCRICAO.toString()), AfaGrupoComponenteNptVO.Fields.DESCRICAO.toString())
				.add(Projections.property("AGC."+AfaGrupoComponenteNpt.Fields.OBSERVACAO.toString()), AfaGrupoComponenteNptVO.Fields.OBSERVACAO.toString())
				.add(Projections.property("AGC."+AfaGrupoComponenteNpt.Fields.CRIADO_EM.toString()), AfaGrupoComponenteNptVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("AGC."+AfaGrupoComponenteNpt.Fields.IND_SITUACAO.toString()), AfaGrupoComponenteNptVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("SER."+RapServidores.Fields.USUARIO.toString()), AfaGrupoComponenteNptVO.Fields.USUARIO.toString())
				);
		
		criteria.addOrder(Order.asc(AfaGrupoComponenteNpt.Fields.SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AfaGrupoComponenteNptVO.class));
		return this.executeCriteria(criteria);
		
	}
	
	public List<AfaGrupoComponenteNpt> obterSuggestionGrupoComponentes(String pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaGrupoComponenteNpt.class);
		
		if(StringUtils.isNotBlank(pesquisa)){
			if(CoreUtil.isNumeroShort(pesquisa)){
				criteria.add(Restrictions.or(
						Restrictions.eq(AfaGrupoComponenteNpt.Fields.SEQ.toString(), Short.valueOf(pesquisa)),
						Restrictions.ilike(AfaGrupoComponenteNpt.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE)));
			}else{
				criteria.add(Restrictions.ilike(AfaGrupoComponenteNpt.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE));
			}
			
		}
		criteria.add(Restrictions.eq(AfaGrupoComponenteNpt.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(AfaGrupoComponenteNpt.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria,0,100,null,false);
	}

}
