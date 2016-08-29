package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.MciBacteriasAssociadasVO;
import br.gov.mec.aghu.controleinfeccao.vo.BacteriaMultirresistenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.BacteriaPacienteVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciBacteriaMultir;

public class MciBacteriaMultirDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciBacteriaMultir> {

	/**
	 * 
	 */
private static final long serialVersionUID = 7165231194179138380L;
	
	public List<BacteriaMultirresistenteVO> listarBacteriasMultir(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteria(codigo, descricao, situacao);
		criteria.addOrder(Order.asc(MciBacteriaMultir.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(BacteriaMultirresistenteVO.class));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	private DetachedCriteria montarCriteria(String codigo, String descricao,
			DominioSituacao situacao) {
		DetachedCriteria criteria  = DetachedCriteria.forClass(MciBacteriaMultir.class);
		
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(MciBacteriaMultir.Fields.SEQ.toString())
					, BacteriaMultirresistenteVO.Fields.CODIGO.toString())
				.add(Projections.property(MciBacteriaMultir.Fields.DESCRICAO.toString())
						, BacteriaMultirresistenteVO.Fields.DESCRICAO.toString())
				.add(Projections.property(MciBacteriaMultir.Fields.SITUACAO.toString())
						, BacteriaMultirresistenteVO.Fields.SITUACAO.toString()));

		if (codigo != null && !codigo.isEmpty()) {
			criteria.add(Restrictions.eq(MciBacteriaMultir.Fields.SEQ.toString(), Integer.valueOf(codigo)));
		}

		if (descricao != null && !descricao.isEmpty()) {
			criteria.add(Restrictions.ilike(MciBacteriaMultir.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(MciBacteriaMultir.Fields.SITUACAO.toString(), situacao));
		}
		
		return criteria;
	}

	public Long listarBacteriasMultirCount(String codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteria(codigo, descricao, situacao);
		return executeCriteriaCount(criteria);
	}

	// #37923 C1
	private DetachedCriteria montaCriteriaMciBacteriaMultir(Integer seq, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciBacteriaMultir.class);
		
		criteria.add(Restrictions.isNull(MciBacteriaMultir.Fields.MCI_BACTERIA_MULTIR.toString()));
		
		if (seq != null) {
			criteria.add(Restrictions.eq(MciBacteriaMultir.Fields.SEQ.toString(), seq));
		}
		if(descricao != null){
			criteria.add(Restrictions.like(MciBacteriaMultir.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indSituacao != null){
			criteria.add(Restrictions.eq(MciBacteriaMultir.Fields.SITUACAO.toString(), indSituacao));
		}

		return criteria;
	}
	
	// RN
	public List<MciBacteriaMultir> pesquisarBacteriaMultirAssociadaPorBrmSeq(Integer bmrSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciBacteriaMultir.class, "BMR");
		criteria.createAlias("BMR" + "." + MciBacteriaMultir.Fields.MCI_BACTERIA_MULTIR.toString(), "TIP", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("TIP" + "." + MciBacteriaMultir.Fields.SEQ.toString(), bmrSeq));
		
		criteria.addOrder(Order.asc(MciBacteriaMultir.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);	
	}	

	public List<MciBacteriaMultir> obterBacteriaMultirPorDescricaoSituacao(Integer seq, String descricao, DominioSituacao indSituacao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.montaCriteriaMciBacteriaMultir(seq, descricao, indSituacao);
		criteria.addOrder(Order.asc(MciBacteriaMultir.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long obterBacteriaMultirPorSeqDescricaoSituacaoCount(Integer seq, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = this.montaCriteriaMciBacteriaMultir(seq, descricao, indSituacao);
		return this.executeCriteriaCount(criteria);
	}

	// #37923 C2
	public List<MciBacteriasAssociadasVO> listarBacteriasAssociadas(Integer seq) {	
		DetachedCriteria criteria = DetachedCriteria.forClass(MciBacteriaMultir.class, "BMR");
		criteria.createAlias("BMR" + "." + MciBacteriaMultir.Fields.MCI_BACTERIA_MULTIR.toString(), "TIPO", JoinType.INNER_JOIN);
		
		
		criteria.setProjection(Projections.projectionList().add(
				Projections.property("BMR." + MciBacteriaMultir.Fields.SEQ.toString())
					, MciBacteriasAssociadasVO.Fields.SEQ.toString())
				.add(Projections.property("BMR." + MciBacteriaMultir.Fields.DESCRICAO.toString())
						, MciBacteriasAssociadasVO.Fields.DESCRICAO.toString())
				.add(Projections.property("BMR." + MciBacteriaMultir.Fields.SITUACAO.toString())
						, MciBacteriasAssociadasVO.Fields.IND_SITUACAO.toString()));
		
		criteria.add(Restrictions.eq("TIPO." + MciBacteriaMultir.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.isNotNull("TIPO." + MciBacteriaMultir.Fields.SEQ.toString()));
		
		criteria.addOrder(Order.asc("BMR." + MciBacteriaMultir.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MciBacteriasAssociadasVO.class));
		
		return executeCriteria(criteria);
	}

	public Boolean validarDescricaoJaExistente(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciBacteriaMultir.class);

		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MciBacteriaMultir.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));
		}
		
		return (this.executeCriteriaCount(criteria) > 0 ? true : false);
	}
	
	public List<BacteriaPacienteVO> pesquisarGermesDisponiveisListaGMRPaciente(final String parametro) {
		/*
		 * Uso de SQL nativo, pois o builder de Criteria com withClause não aplica a condicional OR 
		 * e separar condicionais para simular Join com OR aumenta a complexidade e manutenção da Criteria.
		 */
		StringBuilder sql = new StringBuilder(350);
		
		String condicao = "";
		if (StringUtils.isNotBlank(parametro)) {
			if (StringUtils.isNumeric(parametro)) {
				condicao = " AND (BMR.SEQ = " + parametro + " OR BMN.SEQ = " + parametro + ")";
			} else {
				condicao = " AND (UPPER(BMR.DESCRICAO) LIKE '%" + parametro.toUpperCase() + "%' OR UPPER(BMN.DESCRICAO) LIKE '%" + parametro.toUpperCase() + "%')";
			}
		}
		
		sql.append("SELECT BMR.SEQ, BMR.DESCRICAO, BMN.SEQ AS BMN_SEQ, BMN.DESCRICAO AS BMN_DESCRICAO " // PROJECTIONS
				).append(" FROM AGH.MCI_BACTERIA_MULTIR BMR "
				).append("INNER JOIN AGH.MCI_BACTERIA_MULTIR BMN ON (BMN.SEQ = BMR.BMR_SEQ OR (BMN.SEQ = BMR.SEQ AND BMR.BMR_SEQ IS NULL)) "
				).append(condicao		
				).append("WHERE BMR.IND_SITUACAO = 'A' AND BMN.IND_SITUACAO = 'A' "
		);
		
		sql.append("ORDER BY BMR.DESCRICAO, BMN.DESCRICAO");

		List<BacteriaPacienteVO> resultado = new ArrayList<BacteriaPacienteVO>();
		javax.persistence.Query query = this.createNativeQuery(sql.toString());

		List<Object[]> resultadoConsulta = query.getResultList();
		for (Object[] object : resultadoConsulta) {
			BacteriaPacienteVO vo = new BacteriaPacienteVO();
			if (object[0] != null) { // BMR.SEQ
				vo.setSeq(((Number) object[0]).intValue());
			}
			if (object[1] != null) { // BMR.DESCRICAO
				vo.setDescricao(String.valueOf(object[1]));
			}
			if (object[2] != null) { // BMN.SEQ
				vo.setBmnSeq(((Number) object[2]).intValue());
			}
			if (object[3] != null) { // BMN.DESCRICAO
				vo.setBmnDescricao(String.valueOf(object[3]));
			}
			resultado.add(vo);
		}
		return resultado;
	}

	public Long pesquisarGermesDisponiveisListaGMRPacienteCount(final String parametro) {
		return (long) this.pesquisarGermesDisponiveisListaGMRPaciente(parametro).size();
	}
	
	public List<MciBacteriaMultir> listarSuggestionBoxBacteriaMultir(String filtro, boolean count){
		/*
		 * Uso de SQL nativo, pois o builder de Criteria com withClause não aplica a condicional OR 
		 * e separar condicionais para simular Join com OR aumenta a complexidade e manutenção da Criteria.
		 */
		StringBuilder sql = new StringBuilder(350);
		
		String condicao = "";
		if (StringUtils.isNotBlank(filtro)) {
			if (StringUtils.isNumeric(filtro)) {
				condicao = " AND (BMR.SEQ = " + filtro + ")";
			} else {
				condicao = " AND (UPPER(BMR.DESCRICAO) LIKE '%" + filtro.toUpperCase() + "%' OR UPPER(BMN.DESCRICAO) LIKE '%" + filtro.toUpperCase() + "%')";
			}
		}
		
		sql.append("SELECT BMR.SEQ, BMR.DESCRICAO, BMN.SEQ AS BMN_SEQ, BMN.DESCRICAO AS BMN_DESCRICAO " // PROJECTIONS
				).append(" FROM AGH.MCI_BACTERIA_MULTIR BMR "
				).append("INNER JOIN AGH.MCI_BACTERIA_MULTIR BMN ON (BMN.SEQ = BMR.BMR_SEQ OR (BMN.SEQ = BMR.SEQ AND BMR.BMR_SEQ IS NULL)) "
				).append(condicao		
				).append("WHERE BMR.IND_SITUACAO = 'A' AND BMN.IND_SITUACAO = 'A' "						
		);
		
		if (!count) {
			if (isOracle()) {
				sql.append("AND ROWNUM <= 100");
			} else {
				sql.append("LIMIT 100");
			}
			sql.append("ORDER BY BMR.DESCRICAO, BMN.DESCRICAO");
		}

		List<MciBacteriaMultir> resultado = new ArrayList<MciBacteriaMultir>();
		javax.persistence.Query query = this.createNativeQuery(sql.toString());

		List<Object[]> resultadoConsulta = query.getResultList();
		for (Object[] object : resultadoConsulta) {
			MciBacteriaMultir vo = new MciBacteriaMultir();
			MciBacteriaMultir voFilho = new MciBacteriaMultir();
			if (object[0] != null) { // BMR.SEQ
				vo.setSeq(((Number) object[0]).intValue());
			}
			if (object[1] != null) { // BMR.DESCRICAO
				vo.setDescricao(String.valueOf(object[1]));
			}
			if (object[2] != null) { // BMN.SEQ
				voFilho.setSeq(((Number) object[2]).intValue());
			}
			if (object[3] != null) { // BMN.DESCRICAO
				voFilho.setDescricao(String.valueOf(object[3]));
			}
			
			vo.setMciBacteriaMultir(voFilho);
			
			resultado.add(vo);
		}
		return resultado;
	}
	
	public Long listarSuggestionBoxBacteriaMultirCount(String filtro){
		return (long) listarSuggestionBoxBacteriaMultir(filtro, true).size();
	}
}
