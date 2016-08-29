package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.ConverterConsultasVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.VAacFormaAgendamentos;

public class AacFormaAgendamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacFormaAgendamento> {

	private static final long serialVersionUID = -5052836775768689318L;

	public AacFormaAgendamento findByID(AacFormaAgendamentoId id){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacFormaAgendamento.class);
		criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.ID.toString(), id));
		return (AacFormaAgendamento) executeCriteriaUniqueResult(criteria);
	}
	
	public AacFormaAgendamento findFormaAgendamento(AacPagador pagador, AacTipoAgendamento tipo, AacCondicaoAtendimento condicao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacFormaAgendamento.class);
		criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.PAGADOR.toString(), pagador));
		criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString(), tipo));
		criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO.toString(), condicao));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (AacFormaAgendamento) executeCriteriaUniqueResult(criteria);		
	}
	
	
	public List<AacPagador> pesquisaPagadoresComAgendamento(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPagador.class);
		criteria.add(Restrictions.eq(AacPagador.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNotEmpty(AacPagador.Fields.FORMA_AGENDAMENTO.toString()));		
		
		return executeCriteria(criteria);
	}
	
	
	public List<AacTipoAgendamento> pesquisaTipoAgendamentoComAgendamentoEPagador(AacPagador pagador){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacTipoAgendamento.class);
		criteria.add(Restrictions.eq(AacTipoAgendamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNotEmpty(AacTipoAgendamento.Fields.FORMA_AGENDAMENTO.toString()));	
		criteria.createAlias(AacTipoAgendamento.Fields.FORMA_AGENDAMENTO.toString(), "formaAgendamento");
		if(pagador!=null){
			criteria.add(Restrictions.eq(AacTipoAgendamento.Fields.PAGADOR.toString(), pagador));	
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return executeCriteria(criteria);
	}	
	
	
	public List<AacCondicaoAtendimento> pesquisaCondicaoAtendimentoComAgendamentoEPagadorETipo(AacPagador pagador,AacTipoAgendamento tipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacCondicaoAtendimento.class);
		criteria.createAlias(AacCondicaoAtendimento.Fields.FORMA_AGENDAMENTO.toString(), "formaAgendamento");
		if(pagador!=null){
			criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.PAGADOR.toString(), pagador));	
		}
		if(tipo!=null){
			criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.TIPO_AGENDAMENTO.toString(), tipo));	
		}
		criteria.add(Restrictions.eq(AacCondicaoAtendimento.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		
		return executeCriteria(criteria);
	}
	
	public List<AacFormaAgendamento> pesquisaFormaAgendamentoPorStringEFormaAgendamento(String parametro, AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento){
		DetachedCriteria criteria = obterFormaAgendamentoPorStringEFormaAgendamentoCriteria(parametro, pagador, tipoAgendamento,	condicaoAtendimento);
		criteria.addOrder(Order.asc(AacFormaAgendamento.Fields.PAGADOR.toString()));
		criteria.addOrder(Order.asc(AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString()));
		criteria.addOrder(Order.asc(AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long pesquisaFormaAgendamentoPorStringEFormaAgendamentoCount(String parametro, AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento){
		DetachedCriteria criteria = obterFormaAgendamentoPorStringEFormaAgendamentoCriteria(parametro, pagador, tipoAgendamento, condicaoAtendimento);
	
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria obterFormaAgendamentoPorStringEFormaAgendamentoCriteria(String parametro, AacPagador pagador, AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AacFormaAgendamento.class);
		criteria.createAlias(AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO.toString(), "condicaoAtendimento");
		criteria.createAlias(AacFormaAgendamento.Fields.PAGADOR.toString(), "pagador");
		criteria.createAlias(AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString(), "tipoAgendamento");

		if(StringUtils.isNotEmpty(parametro)){
			criteria.add(Restrictions.or(Restrictions.ilike("condicaoAtendimento."+AacCondicaoAtendimento.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE),
					Restrictions.or(Restrictions.ilike("pagador."+AacPagador.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE), 
							Restrictions.ilike("tipoAgendamento."+AacTipoAgendamento.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE))));
		}
		
		if (pagador != null) {
			criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.PAGADOR.toString(), pagador));
		}

		if (tipoAgendamento != null) {
			criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString(), tipoAgendamento));
		}

		if (condicaoAtendimento != null) {
			criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO.toString(), condicaoAtendimento));
		}
		
		return criteria;
	}

	public List<AacFormaAgendamento> listarFormasAgendamentos(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AacPagador pagador, AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicaoAtendimento) {
		DetachedCriteria criteria = createFormasAgendamentosCriteria(pagador, tipoAgendamento, condicaoAtendimento);

		criteria.createAlias(AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO.toString(), "CAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacFormaAgendamento.Fields.PAGADOR.toString(), "PAG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString(), "TAG", JoinType.LEFT_OUTER_JOIN);
		
		if (StringUtils.isNotEmpty(orderProperty)) {
			criteria.addOrder(asc ? Order.asc(orderProperty) : Order.desc(orderProperty));
		}
		
		criteria.addOrder(Order.asc(AacFormaAgendamento.Fields.PGD_SEQ.toString()));
		criteria.addOrder(Order.asc(AacFormaAgendamento.Fields.TAG_SEQ.toString()));
		criteria.addOrder(Order.asc(AacFormaAgendamento.Fields.CAA_SEQ.toString()));

		return executeCriteria(criteria, firstResult, maxResult, null, false);
	}

	public Long listarFormasAgendamentosCount(AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento) {
		DetachedCriteria criteria = createFormasAgendamentosCriteria(pagador, tipoAgendamento, condicaoAtendimento);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createFormasAgendamentosCriteria(AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacFormaAgendamento.class);
		
		if (pagador != null) {
			criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.PAGADOR.toString(), pagador));
		}

		if (tipoAgendamento != null) {
			criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString(), tipoAgendamento));
		}

		if (condicaoAtendimento != null) {
			criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO.toString(), condicaoAtendimento));
		}

		return criteria;
	}
	
	public Boolean existeFormaAgendamentoComCondicaoAtendimentoCount(AacCondicaoAtendimento condicaoAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacFormaAgendamento.class);
		criteria.add(Restrictions.eq(AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO.toString(), condicaoAtendimento));

		return executeCriteriaCount(criteria) > 0;
	}

	public Boolean existeFormaAgendamentoComTipoAgendamentoCount(
			AacTipoAgendamento tipoAgendamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacFormaAgendamento.class);
		criteria.add(Restrictions.eq(
				AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString(),
				tipoAgendamento));
		return executeCriteriaCount(criteria) > 0;
	}
	
	public Boolean existeFormaAgendamentoComPagadorCount(AacPagador pagador) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacFormaAgendamento.class);
		criteria.add(Restrictions.eq(
				AacFormaAgendamento.Fields.PAGADOR.toString(), pagador));
		return executeCriteriaCount(criteria) > 0;
	}
	
	//Consulta pagadores cadastrados
		public List<ConverterConsultasVO> consultaPagadoresCadastrados(){	
			DetachedCriteria criteria = DetachedCriteria.forClass(VAacFormaAgendamentos.class);
			ProjectionList projList = Projections.projectionList();
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.PGD_SEQ.toString()), ConverterConsultasVO.Fields.PGD_SEQ.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.PAGADOR.toString()), ConverterConsultasVO.Fields.PAGADOR.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.TAG_SEQ.toString()), ConverterConsultasVO.Fields.TAG_SEQ.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.TIPO.toString()), ConverterConsultasVO.Fields.TIPO.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.CAA_SEQ.toString()), ConverterConsultasVO.Fields.CAA_SEQ.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.CONDICAO.toString()), ConverterConsultasVO.Fields.CONDICAO.toString());
		    criteria.setProjection(projList);
		     
		    criteria.setResultTransformer(Transformers.aliasToBean(ConverterConsultasVO.class));
		     
		return  executeCriteria(criteria);
		}

		public List<ConverterConsultasVO> consultaPagadoresCadastrados(String sbPagadores){	
			DetachedCriteria criteria = DetachedCriteria.forClass(VAacFormaAgendamentos.class);
			
			criteria = getCriteriaVAacFormaAgendamentos(sbPagadores);
			//criteria.addOrder(Order.asc(AacFormaAgendamento.Fields.PAGADOR.toString()));
			
		    return executeCriteria(criteria, 0, 100, "pagador", true);
		}
	
		public Long consultaPagadoresCadastradosCount(String sbPagadores){	
			//DetachedCriteria criteria = getCriteriaVAacFormaAgendamentos(sbPagadores);
		     
			return  executeCriteriaCount(getCriteriaVAacFormaAgendamentos(sbPagadores));
		}
			
		/**
		 * 
		 * @param sbPagadores
		 * @return
		 */
		private DetachedCriteria getCriteriaVAacFormaAgendamentos(String sbPagadores){
			
			DetachedCriteria criteria = DetachedCriteria.forClass(VAacFormaAgendamentos.class);
			
			if (StringUtils.isNotBlank(sbPagadores)) {
				
				criteria.add(Restrictions.or(						
						Restrictions.ilike(VAacFormaAgendamentos.Fields.PAGADOR.toString(), this.replaceCaracterEspecial(sbPagadores), MatchMode.ANYWHERE),
						Restrictions.ilike(VAacFormaAgendamentos.Fields.TIPO.toString(), this.replaceCaracterEspecial(sbPagadores), MatchMode.ANYWHERE),
						Restrictions.ilike(VAacFormaAgendamentos.Fields.CONDICAO.toString(), this.replaceCaracterEspecial(sbPagadores), MatchMode.ANYWHERE)));
			}
			
			 ProjectionList projList = Projections.projectionList();
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.PGD_SEQ.toString()), ConverterConsultasVO.Fields.PGD_SEQ.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.PAGADOR.toString()), ConverterConsultasVO.Fields.PAGADOR.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.TAG_SEQ.toString()), ConverterConsultasVO.Fields.TAG_SEQ.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.TIPO.toString()), ConverterConsultasVO.Fields.TIPO.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.CAA_SEQ.toString()), ConverterConsultasVO.Fields.CAA_SEQ.toString());
		     projList.add(Projections.property(VAacFormaAgendamentos.Fields.CONDICAO.toString()), ConverterConsultasVO.Fields.CONDICAO.toString());
		     criteria.setProjection(projList);
		     
		     criteria.setResultTransformer(Transformers.aliasToBean(ConverterConsultasVO.class));
		     
		     return criteria;
			
		}
		
		/**
		 * Remove o % da string
		 * 
		 * @param descricao
		 * @return
		 */
		private String replaceCaracterEspecial(String descricao) {
	        
		       return descricao.replace("_", "\\_").replace("%", "\\%");
		}
		
}
