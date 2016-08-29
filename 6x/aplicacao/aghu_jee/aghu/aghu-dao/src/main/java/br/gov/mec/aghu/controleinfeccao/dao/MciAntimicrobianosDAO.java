package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciAntimicrobianos;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MciAntimicrobianosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciAntimicrobianos> {

	private static final long serialVersionUID = 5057201176700267504L;
	
	public MciAntimicrobianos obterMciAntimicrobianosComRelacionamento(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciAntimicrobianos.class,"AMB");
		criteria.add(Restrictions.eq(MciAntimicrobianos.Fields.SEQ.toString(), seq));
		
		criteria.createAlias("AMB." + MciAntimicrobianos.Fields.RAP_SERVIDORES.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("AMB." + MciAntimicrobianos.Fields.SERVIDOR_MOVIMENTADO.toString(), "SER_MOVI", JoinType.LEFT_OUTER_JOIN);
		
		return (MciAntimicrobianos) this.executeCriteriaUniqueResult(criteria);
	}
	
	public DetachedCriteria obterCriteriaAntimicrobianosPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciAntimicrobianos.class);
		if(seq != null){
			criteria.add(Restrictions.eq(MciAntimicrobianos.Fields.SEQ.toString(), seq));
		}
		if(descricao != null){
			criteria.add(Restrictions.like(MciAntimicrobianos.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indSituacao != null){
			criteria.add(Restrictions.eq(MciAntimicrobianos.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		return criteria;
	}
	
	public List<MciAntimicrobianos> pesquisarAntimicrobianosPorSeqDescricaoSituacao(Integer seq, String descricao, DominioSituacao indSituacao, 
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.obterCriteriaAntimicrobianosPorSeqDescricaoSituacao(seq, descricao, indSituacao);
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarAntimicrobianosPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = this.obterCriteriaAntimicrobianosPorSeqDescricaoSituacao(seq, descricao, indSituacao);
		return this.executeCriteriaCount(criteria);
	}
	
	public List<MciAntimicrobianos> pesquisarAntiMicrobianosAtivosPorSeqDescricao(Object param){
		return executeCriteria(this.montarCriteriaAntiMicrobianosAtivosPorSeqDescricao(param));
	}
	
	public Long pesquisarAntiMicrobianosAtivosPorSeqDescricaoCount(Object param){
		return executeCriteriaCount(this.montarCriteriaAntiMicrobianosAtivosPorSeqDescricao(param));
	}
	
	public DetachedCriteria montarCriteriaAntiMicrobianosAtivosPorSeqDescricao(Object param){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciAntimicrobianos.class);

		final String srtPesquisa = (String) param;

		if (CoreUtil.isNumeroInteger(param)) {
			criteria.add(Restrictions.eq(MciAntimicrobianos.Fields.SEQ.toString(), Integer.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotEmpty(srtPesquisa)) {
			criteria.add(Restrictions.ilike(MciAntimicrobianos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(MciAntimicrobianos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	public Boolean verificarDescricaoExiste(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciAntimicrobianos.class);

		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MciAntimicrobianos.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));
		}
		
		return (this.executeCriteriaCount(criteria) > 0 ? true : false);
	}
}