package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.ShortType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoNotaSala;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MbcEquipamentoCirurgicoDAO extends	br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcEquipamentoCirurgico> {

	private static final long serialVersionUID = -344696019717544644L;
	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEquipamentoCirurgico.class);
		return criteria;
	}
	
	public List<MbcEquipamentoCirurgico> pesquisaEquipamentoCirurgicoOrderByDesc(final Short codigo, final String descricao, final DominioSituacao situacao,
			final Integer firstResult, final Integer maxResults, final String orderProperty, final Boolean asc){
		DetachedCriteria criteria = this.obterCriteria();
		
		criteria = this.montarCriteriaPesquisarEquipamentoCirurgico(criteria, codigo, descricao, situacao);
		
		criteria.addOrder(Order.asc(MbcEquipamentoCirurgico.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		
	}
	
	public List<MbcEquipamentoCirurgico> pesquisarEquipamentoCirurgico(
			final Short codigo, final String descricao, final DominioSituacao situacao,
			final Integer firstResult, final Integer maxResults, final String orderProperty, final Boolean asc) {
		
		DetachedCriteria criteria = this.obterCriteria();
		
		criteria = this.montarCriteriaPesquisarEquipamentoCirurgico(criteria, codigo, descricao, situacao);
		
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	
	public Long pesquisarEquipamentoCirurgicoCount(
			final Short codigo, final String descricao, final DominioSituacao situacao) {
	
		DetachedCriteria criteria = this.obterCriteria();
		criteria = this.montarCriteriaPesquisarEquipamentoCirurgico(criteria, codigo, descricao, situacao);
		
		return this.executeCriteriaCount(criteria);
	}
	
	public List<MbcEquipamentoCirurgico> buscaEquipamentosCirurgicos(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;

		Short seqp = (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa))?Short.parseShort(strPesquisa):null;
		String descricao = (StringUtils.isNotBlank(strPesquisa) && !CoreUtil.isNumeroShort(strPesquisa))?strPesquisa:null;

		DetachedCriteria criteria = this.obterCriteria();
		criteria = this.montarCriteriaPesquisarEquipamentoCirurgico(criteria, seqp, descricao, DominioSituacao.A);
		
		return super.executeCriteria(criteria,0, 100, MbcEquipamentoCirurgico.Fields.DESCRICAO.toString(),true);
	}
	
	public Long buscaEquipamentosCirurgicosCount(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;

		Short seqp = (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa))?Short.parseShort(strPesquisa):null;
		String descricao = (StringUtils.isNotBlank(strPesquisa) && !CoreUtil.isNumeroShort(strPesquisa))?strPesquisa:null;

		DetachedCriteria criteria = this.obterCriteria();
		criteria = this.montarCriteriaPesquisarEquipamentoCirurgico(criteria, seqp, descricao, DominioSituacao.A);
		
		return super.executeCriteriaCount(criteria);
	}
	
	protected DetachedCriteria montarCriteriaPesquisarEquipamentoCirurgico(
			DetachedCriteria criteria,
			final Short codigo, final String descricao, final DominioSituacao situacao) {
		
		if(codigo != null) {
			criteria.add(Restrictions.eq(
					MbcEquipamentoCirurgico.Fields.SEQ.toString(), codigo));
		}
		
		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					MbcEquipamentoCirurgico.Fields.DESCRICAO.toString(), descricao, 
					MatchMode.ANYWHERE));
		}
		
		if(situacao != null) {
			criteria.add(Restrictions.eq(
					MbcEquipamentoCirurgico.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
	
	public MbcEquipamentoCirurgico obterEquipamentoCirurgico(
			final String descricao, final Short codigo, final DominioSituacao situacao ) {
		DetachedCriteria criteria = this.obterCriteria();
		
		criteria = this.montarCriteriaPesquisarEquipamentoCirurgico(
				criteria, codigo, descricao, situacao);
								
		List<MbcEquipamentoCirurgico> result = this.executeCriteria(criteria);
				
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	
	public MbcEquipamentoCirurgico obterEquipamentoCirurgico(final Short codigo, final String descricao) {
		DetachedCriteria criteria = this.obterCriteria();
		
		if(codigo != null) {
			final String sql = " seq not in (?) ";
			criteria.add(Restrictions.sqlRestriction(sql, 
					new Object[] {codigo}, new Type[]{ShortType.INSTANCE}));
		}
		
		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.eq(MbcEquipamentoCirurgico
				.Fields.DESCRICAO.toString(), descricao));
		}
		List<MbcEquipamentoCirurgico> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public List<MbcEquipamentoCirurgico> pesquisarMbcEquipamentoCirurgicoComSubQueryMbcUnidadenotaSala(
			MbcCirurgias cirurgia, 
			DominioIndRespProc dominioIndRespProc,
			DominioSituacao dominioSituacao,
			String indicadorPrincipal) throws ApplicationBusinessException {
		
		//SUBQUERY
		final DetachedCriteria subQuery = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOA");
		subQuery.setProjection(Projections.property(MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subQuery.add(Property.forName("NOA." + MbcUnidadeNotaSala.Fields.SEQP.toString()).eqProperty("EPN_01." + MbcEquipamentoNotaSala.Fields.MBC_UNIDADE_NOTA_SALAS_SEQP.toString()));
		
		subQuery.createAlias("NOA.".concat(MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString()), "EPR", DetachedCriteria.INNER_JOIN);
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.SITUACAO.toString()), DominioSituacao.A));
		
		//SUBQUERY DA SUBQUERY
		final DetachedCriteria subQueryPPC = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "PPC");
		subQueryPPC.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString()), cirurgia.getSeq()));
		subQueryPPC.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()), dominioIndRespProc));
		subQueryPPC.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.SITUACAO.toString()), dominioSituacao));
		subQueryPPC.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()), "S".equals(indicadorPrincipal) ? Boolean.TRUE : Boolean.FALSE));
		subQueryPPC.add(Restrictions.eq("NOA."+MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), cirurgia.getUnidadeFuncional().getSeq()));
		
		//Relacionando MbcUnidadeNotaSala com a MbcProcEspPorCirurgias pela MbcProcedimentoCirurgicos
		subQueryPPC.setProjection(Projections.property("PPC." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString()+"." 
				+ MbcProcedimentoCirurgicos.Fields.SEQ.toString()));
		
		subQueryPPC.add(Property.forName("PPC." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO_SEQ.toString())
				.eqProperty("NOA."+ MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString()));
		
		subQuery.add(Subqueries.exists(subQueryPPC));
		
		return executeCriteria(this.getCriteriaMbcEquipamentoCirurgico(cirurgia).add(Subqueries.exists(subQuery)));
			
	}
	
	public List<MbcEquipamentoCirurgico> pesquisarMbcEquipamentoCirurgicoComSubQueryMbcUnidadenotaSala(
			MbcCirurgias cirurgia,
			DominioSituacao dominioSituacao
			) throws ApplicationBusinessException {
		
		DetachedCriteria principal = this.getCriteriaMbcEquipamentoCirurgico(cirurgia);
		
		//SUBQUERY
		final DetachedCriteria subQuery = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOA");
		subQuery.setProjection(Projections.property(MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subQuery.add(Property.forName("NOA." + MbcUnidadeNotaSala.Fields.SEQP.toString()).eqProperty("EPN_01." + MbcEquipamentoNotaSala.Fields.MBC_UNIDADE_NOTA_SALAS_SEQP.toString()));
		
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.UNFSEQ.toString()), cirurgia.getUnidadeFuncional().getSeq()));
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.SITUACAO.toString()), dominioSituacao));
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES_SEQ.toString()), cirurgia.getEspecialidade().getSeq()));
		
		principal.add(Subqueries.exists(subQuery));
		return executeCriteria(principal);
	}
	
	public List<MbcEquipamentoCirurgico> pesquisarMbcEquipamentoCirurgicoComSubQueryMbcUnidadenotaSala(MbcCirurgias cirurgia	) throws ApplicationBusinessException {
		
		//SUBQUERY
		final DetachedCriteria subQuery = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOA");
		subQuery.setProjection(Projections.property(MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subQuery.add(Property.forName("NOA." + MbcUnidadeNotaSala.Fields.SEQP.toString()).eqProperty("EPN_01." + MbcEquipamentoNotaSala.Fields.MBC_UNIDADE_NOTA_SALAS_SEQP.toString()));
				
		subQuery.add(Restrictions.isNull("NOA.".concat(MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString())));
		subQuery.add(Restrictions.isNull("NOA.".concat(MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES_SEQ.toString())));
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.UNFSEQ.toString()), cirurgia.getUnidadeFuncional().getSeq()));
		
		return executeCriteria(this.getCriteriaMbcEquipamentoCirurgico(cirurgia).add(Subqueries.exists(subQuery)));
	}
	
	public DetachedCriteria getCriteriaMbcEquipamentoCirurgico(MbcCirurgias cirurgia){
		
		//QUERY PRINCIPAL
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcEquipamentoCirurgico.class, "EUU_01");
		criteria.createAlias("EUU_01.".concat(MbcEquipamentoCirurgico.Fields.LIST_EQUIPAMENTOS_NOTA_SALA.toString()), "EPN_01", DetachedCriteria.INNER_JOIN);
		criteria.add(Restrictions.eq("EPN_01.".concat(MbcEquipamentoNotaSala.Fields.UNF_SEQ.toString()), cirurgia.getUnidadeFuncional().getSeq()));
		criteria.addOrder(Order.asc("EPN_01.".concat(MbcEquipamentoNotaSala.Fields.ORDEM_IMP.toString())));
		
		return criteria;
		
	}

}
