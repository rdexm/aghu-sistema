package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.VAelGradeExame;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VAelGradeExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelGradeExame> {
	
	private static final long serialVersionUID = 4772355869390809357L;


	public Boolean existeGradeExamePorSiglaMatUnf(String exameSigla, Integer materialSeq, Short unfSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelGradeExame.class);
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Property.forName(VAelGradeExame.Fields.GRADE.toString()));
		criteria.setProjection(projectionList);
		
		criteria.add(Restrictions.eq(VAelGradeExame.Fields.SIGLA_EXAME.toString(), exameSigla));
		criteria.add(Restrictions.eq(VAelGradeExame.Fields.MAT_EXAME.toString(), materialSeq));
		criteria.add(Restrictions.eq(VAelGradeExame.Fields.UNF_EXAME.toString(), unfSeq));
		
		criteria.add(Restrictions.eq(VAelGradeExame.Fields.SIT_GRUPO.toString(), DominioSituacao.A.toString()));
		criteria.add(Restrictions.eq(VAelGradeExame.Fields.SIT_GRUPO_EX.toString(), DominioSituacao.A.toString()));
		
		List<Short> resultadoPesquisa =  executeCriteria(criteria);
		
		return resultadoPesquisa != null;
	}
	
	public List<VAelGradeExame> pesquisarGradeExame(Object parametro, Short grade, String sigla, Integer matExame, Short unfExame) {
		
		StringBuilder stbQuery  = new StringBuilder(400).append("	select  vge.*");
		stbQuery.append("	from    AGH.V_AEL_GRADE_EXAME vge left join AGH.V_AEL_EXAME_MAT_ANALISE vem on (vem.sigla=vge.sigla_exame and vem.man_seq=vge.mat_exame)");
		stbQuery.append(" where ");
		stbQuery.append("vge.sit_grade = 'A' and ");
		stbQuery.append("vge.grade = "+grade+" and ");
		stbQuery.append("vge.sigla_exame = '"+sigla+"' and ");
		stbQuery.append("vge.mat_exame = "+matExame+" and ");
		stbQuery.append("vge.unf_exame = "+unfExame+" ");
		if (StringUtils.isNotBlank((String)parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				Integer seqGrade = Integer.parseInt((String)parametro);
				stbQuery.append("and vge.seq_grade = "+seqGrade+" ");
			} else {
				String descricao = parametro.toString();
				stbQuery.append("and (vge.descr_grupo_ex like '%"+StringUtils.upperCase(descricao)+"%'");
				stbQuery.append(" or vge.nome_func like '%"+StringUtils.upperCase(descricao)+"%')");
			}		
		}
		stbQuery.append(" order by vge.num_sala, vge.nome_func ");
		
		javax.persistence.Query query = this.createNativeQuery(stbQuery.toString(), VAelGradeExame.class);

		return (List<VAelGradeExame>)query.getResultList();
	}

}
