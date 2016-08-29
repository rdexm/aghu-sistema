package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.AelAgrpPesquisaXExameVO;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelAgrpPesquisaXExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAgrpPesquisaXExame> {
	
	private static final long serialVersionUID = 8132982365437909757L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelAgrpPesquisaXExame.class);
    }

	public AelAgrpPesquisaXExame buscarAtivoPorUnfExecutaExame(String emaExaSigla, Integer emaManSeq, Short unfSeq, final DominioSituacao situacao) {
		DetachedCriteria criteria = criarCriteriabuscarAtivoPorUnfExecutaExame(emaExaSigla, emaManSeq, unfSeq, situacao);
		// Alterado para suprir bug do AGH.
		// TODO implementar solução de negócio
		final List<AelAgrpPesquisaXExame> result = executeCriteria(criteria);
		if(result == null || result.isEmpty()){
			return null;
		}
		return result.get(0);
	}

	private DetachedCriteria criarCriteriabuscarAtivoPorUnfExecutaExame(String emaExaSigla,	Integer emaManSeq, Short unfSeq, final DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.UFE_EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.UFE_EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		
		if(situacao != null){
			criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.IND_SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
	public AelAgrpPesquisaXExame buscarAtivoPorUnfExecutaExameEAelAgrpPesquisas(final AelAgrpPesquisas aelAgrpPesquisas, final AelUnfExecutaExames unfExecutaExame) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.AGRP_PESQUISA.toString(), aelAgrpPesquisas));
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.UNF_EXECUTA_EXAME.toString(), unfExecutaExame));
		return (AelAgrpPesquisaXExame) executeCriteriaUniqueResult(criteria);
	}

	public List<AelAgrpPesquisaXExame> buscarAelAgrpPesquisaXExame(AelExames exame, AelMateriaisAnalises materialAnalise,
			AghUnidadesFuncionais unidadeFuncional, AelAgrpPesquisas agrpPesquisa, DominioSituacao situacao) {
		if (exame == null || exame.getSigla() == null 
				|| materialAnalise == null || materialAnalise.getSeq() == null
				|| unidadeFuncional == null || unidadeFuncional.getSeq() == null
				|| agrpPesquisa == null || agrpPesquisa.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.UFE_EMA_EXA_SIGLA.toString(), exame.getSigla()));
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.UFE_EMA_MAN_SEQ.toString(), materialAnalise.getSeq()));
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.UFE_UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.AGRP_PESQUISA.toString(), agrpPesquisa));
		if(situacao != null) {
			criteria.add(Restrictions.eq(AelAgrpPesquisaXExame.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		return super.executeCriteria(criteria);
	}
	
	public List<AelAgrpPesquisaXExameVO> obterAelAgrpPesquisaXExamePorAelAgrpPesquisas(final AelAgrpPesquisas aelAgrpPesquisas, final String filtro, final boolean limitarRegsPai){
		final StringBuffer hql = new StringBuffer(300);
		
		hql.append("select ");
		
		if(limitarRegsPai){
			hql.append("  AEE.").append(AelAgrpPesquisaXExame.Fields.SEQ.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.SEQ.toString()).append(", ");
			hql.append("  AEE.").append(AelAgrpPesquisaXExame.Fields.IND_SITUACAO.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.IND_SITUACAO.toString()).append(", ");
			//hql.append("  AEE.").append(AelAgrpPesquisaXExame.Fields.VERSION.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.VERSION.toString()).append(", ");
		}
		
	   hql.append("   LVE.").append(VAelUnfExecutaExames.Fields.SIGLA.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.EMA_EXA_SIGLA.toString())
  		   .append(" ,LVE.").append(VAelUnfExecutaExames.Fields.MAN_SEQ.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.EMA_MAN_SEQ.toString())
		   .append(" ,LVE.").append(VAelUnfExecutaExames.Fields.UNF_SEQ.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.UNF_SEQ.toString())
		   
		   .append(" ,LVE.").append(VAelUnfExecutaExames.Fields.DESCRICAO_USUAL_EXAME.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.DESCRICAO_USUAL_EXAME.toString())
		   .append(" ,LVE.").append(VAelUnfExecutaExames.Fields.DESCRICAO_MATERIAL.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.DESCRICAO_MATERIAL.toString())
		   .append(" ,LVE.").append(VAelUnfExecutaExames.Fields.DESCRICAO_UNIDADE.toString()).append(" as ").append(AelAgrpPesquisaXExameVO.Fields.DESCRICAO_UNIDADE.toString())
		   
		   .append(" from ");
	   
	   if(limitarRegsPai){
		   hql.append(AelAgrpPesquisaXExame.class.getName()).append(" AS AEE, ");
	   }
	   
	   hql.append(VAelUnfExecutaExames.class.getName()).append(" AS LVE ")
		   .append(" WHERE 1=1 ") ;
		
	   if(limitarRegsPai){
		  hql.append("  AND LVE.").append(VAelUnfExecutaExames.Fields.MAN_SEQ.toString())
		   		.append(" = AEE.").append(AelAgrpPesquisaXExame.Fields.UFE_EMA_MAN_SEQ.toString())
   		       
		  .append(" AND LVE.").append(VAelUnfExecutaExames.Fields.UNF_SEQ.toString())
		  		.append(" = AEE.").append(AelAgrpPesquisaXExame.Fields.UFE_UNF_SEQ.toString())
		   		       
		  .append(" AND LVE.").append(VAelUnfExecutaExames.Fields.SIGLA.toString())
		  		.append(" = AEE.").append(AelAgrpPesquisaXExame.Fields.UFE_EMA_EXA_SIGLA.toString());
	   }
		   		       
		// Entrará quando da pesquisa da suggestion
		if(StringUtils.isNotEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro) || CoreUtil.isNumeroShort(filtro)){
				hql.append(" AND (LVE.").append(VAelUnfExecutaExames.Fields.MAN_SEQ.toString()).append(" = :PRM_FILTRO OR ")
				   .append("      LVE.").append(VAelUnfExecutaExames.Fields.UNF_SEQ.toString()).append(" = :PRM_FILTRO )");
				
			} else {
				hql.append(" AND (LVE.").append(VAelUnfExecutaExames.Fields.DESCRICAO_USUAL_EXAME.toString()).append(" like(:PRM_FILTRO) OR  ")
				   .append("      LVE.").append(VAelUnfExecutaExames.Fields.DESCRICAO_MATERIAL.toString()).append(" like(:PRM_FILTRO) OR ")
				   .append("      LVE.").append(VAelUnfExecutaExames.Fields.SIGLA.toString()).append(" like(:PRM_FILTRO) OR ")
				   .append("      LVE.").append(VAelUnfExecutaExames.Fields.DESCRICAO_UNIDADE.toString()).append(" like(:PRM_FILTRO) )");
			}
			
		}
		
		if(limitarRegsPai){
			hql.append(" AND AEE.").append(AelAgrpPesquisaXExame.Fields.AGRP_PESQUISA.toString()).append(" = :PRM_AGRP_PESQUISA" );
		}
		
		hql.append(" order by LVE.").append(VAelUnfExecutaExames.Fields.DESCRICAO_USUAL_EXAME.toString());

		final Query query = createHibernateQuery(hql.toString());

		// Entrará quando da pesquisa da suggestion
		if(StringUtils.isNotEmpty(filtro)){
			query.setParameter("PRM_FILTRO", "%"+filtro.toUpperCase()+"%");
		} 
		
		if(limitarRegsPai){
			query.setParameter("PRM_AGRP_PESQUISA", aelAgrpPesquisas);
		}
		
		return query.setResultTransformer(Transformers.aliasToBean(AelAgrpPesquisaXExameVO.class)).list();
	}
	
	
	public List<AelAgrpPesquisaXExame> pesquisarPorDescricaoAtivoPorUnfExecutaExame(String descricao, String emaExaSigla, Integer emaManSeq, Short unfSeq, DominioSituacao situacao) {
		DetachedCriteria dc = criarCriteriabuscarAtivoPorUnfExecutaExame(emaExaSigla, emaManSeq, unfSeq, situacao);
		
		dc.createAlias(AelAgrpPesquisaXExame.Fields.AGRP_PESQUISA.toString(), "APS");
		dc.add(Restrictions.eq("APS.".concat(AelAgrpPesquisas.Fields.DESCRICAO.toString()), descricao));
		
		return executeCriteria(dc);
	}	
	
	
	private DetachedCriteria obterCriteriaPesquisarAelAgrpPesquisaXExamePorNumeroApDescricao(Long numeroAp, String descricao, Integer lu2Seq) {
		String aliasAps = "aps";
		String aliasUee = "uee";
		String aliasIse = "ise";
		String separador = ".";
		
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelAgrpPesquisaXExame.Fields.AGRP_PESQUISA.toString(), aliasAps);
		criteria.createAlias(AelAgrpPesquisaXExame.Fields.UNF_EXECUTA_EXAME.toString(), aliasUee);
		criteria.createAlias(aliasUee + separador + AelUnfExecutaExames.Fields.ITEM_SOLICITACAO_EXAMES, aliasIse);

		//joins necessarios pq agora filtra na tabela LUM o nro_ap e lu2_seq
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");

		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp.longValue()));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		
		criteria.add(Restrictions.eq(aliasAps + separador + AelAgrpPesquisas.Fields.DESCRICAO.toString(), descricao));
		return criteria;
	}
	
	public List<AelAgrpPesquisaXExame> pesquisarAelAgrpPesquisaXExamePorNumeroApDescricao(Long numeroAp, String descricao, Integer lu2Seq) {
		DetachedCriteria criteria = obterCriteriaPesquisarAelAgrpPesquisaXExamePorNumeroApDescricao(numeroAp, descricao, lu2Seq);
		return executeCriteria(criteria);
	}
	
}
