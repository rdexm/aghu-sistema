package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.ExameNotificacaoVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.AelExamesNotificacaoId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;


public class AelExamesNotificacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesNotificacao> {
	
	private static final long serialVersionUID = -2150183667917869597L;

	public List<ExameNotificacaoVO> pesquisarExameNotificacao(Integer firstReult, Integer maxResults, String orderProperty,
			Boolean asc, String sigla, Integer manSeq, Integer calSeq, DominioSituacao situacao){
		
		DetachedCriteria criteria = montarCriteriaExameNotificacao(sigla, manSeq, calSeq, situacao);
		criteria.addOrder(Order.asc("exame"+"."+AelExames.Fields.SIGLA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ExameNotificacaoVO.class));
		return executeCriteria(criteria, firstReult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarExameNotificacaoCount(String sigla, Integer manSeq, Integer calSeq, DominioSituacao situacao){
		
		DetachedCriteria criteria = montarCriteriaExameNotificacao(sigla, manSeq, calSeq, situacao);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaExameNotificacao(String sigla, Integer manSeq, Integer calSeq, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesNotificacao.class,"exameNotificacao");
		criteria.createAlias("exameNotificacao."+AelExamesNotificacao.Fields.EXAME_MATERIAL_ANALISE.toString(), "exameMaterialAnalise");
		criteria.createAlias("exameMaterialAnalise"+"."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "exame");
		criteria.createAlias("exameMaterialAnalise"+"."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "materialAnalise");
		criteria.createAlias("exameNotificacao."+AelExamesNotificacao.Fields.CAMPO_LAUDO.toString(), "campoLaudo");
		
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("exame"+"."+AelExames.Fields.SIGLA.toString()),ExameNotificacaoVO.Fields.SIGLA.toString())
				.add(Projections.property("exame"+"."+AelExames.Fields.DESCRICAO_USUAL.toString()),ExameNotificacaoVO.Fields.EXAME.toString())
				.add(Projections.property("materialAnalise"+"."+AelMateriaisAnalises.Fields.SEQ.toString()),ExameNotificacaoVO.Fields.MAN_SEQ.toString())
				.add(Projections.property("materialAnalise"+"."+AelMateriaisAnalises.Fields.DESCRICAO.toString()),ExameNotificacaoVO.Fields.MATERIAL_ANALISE.toString())
				.add(Projections.property("campoLaudo"+"."+AelCampoLaudo.Fields.SEQ.toString()),ExameNotificacaoVO.Fields.CODIGO.toString())
				.add(Projections.property("campoLaudo"+"."+AelCampoLaudo.Fields.NOME.toString()),ExameNotificacaoVO.Fields.CAMPO_LAUDO.toString())
				.add(Projections.property(AelExamesNotificacao.Fields.IND_SITUACAO.toString()),ExameNotificacaoVO.Fields.SITUACAO.toString()));
				
		
		if(StringUtils.isNotBlank(sigla)){
			criteria.add(Restrictions.eq("exame"+"."+AelExames.Fields.SIGLA.toString(), sigla));
		}
		if(calSeq!=null){
			criteria.add(Restrictions.eq(AelExamesNotificacao.Fields.CAL_SEQ.toString(), calSeq));
		}
		if(manSeq!=null){
			criteria.add(Restrictions.eq(AelExamesNotificacao.Fields.EMA_MAN_SEQ.toString(), manSeq));
		}
		if(situacao!=null){
			criteria.add(Restrictions.eq("exameNotificacao."+AelExamesNotificacao.Fields.IND_SITUACAO.toString(), situacao));	
		}
		
		
		
		return criteria;		
	}
		
	private DetachedCriteria montarCriteriaExameNotificacaoPorId(AelExamesNotificacaoId id){

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesNotificacao.class, "exameNotificacao");
		criteria.createAlias("exameNotificacao."+AelExamesNotificacao.Fields.CAMPO_LAUDO.toString(), "campoLaudo");
		criteria.createAlias("exameNotificacao."+AelExamesNotificacao.Fields.EXAME_MATERIAL_ANALISE.toString(), "exameMaterialAnalise");
		criteria.add(Restrictions.eq(AelExamesNotificacao.Fields.EMA_EXA_SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelExamesNotificacao.Fields.CAL_SEQ.toString(), id.getCalSeq()));
		criteria.add(Restrictions.eq(AelExamesNotificacao.Fields.EMA_MAN_SEQ.toString(), id.getEmaManSeq()));
	
		return criteria;
	}

	public AelExamesNotificacao retornaExameNotificacaoPorId(AelExamesNotificacaoId id){

		DetachedCriteria criteria = montarCriteriaExameNotificacaoPorId(id);
		return (AelExamesNotificacao) executeCriteriaUniqueResult(criteria);
	}
}