package br.gov.mec.aghu.perinatologia.dao;

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
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.McoProcReanimacao;
import br.gov.mec.aghu.perinatologia.vo.MedicamentoRecemNascidoVO;
import br.gov.mec.aghu.vo.ProcedimentoReanimacaoVO;

public class McoProcReanimacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoProcReanimacao> {


	private static final long serialVersionUID = -2121302418251490229L;

	/**
	 * #41973 - C1 
	 * @param descricao
	 * @return
	 */
	public List<MedicamentoRecemNascidoVO> buscarMedicamentosPorDescricao(String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProcReanimacao.class);
		adicionarFiltrosMcoProcReanimacao(descricao, criteria);
		
	  	criteria.setProjection(Projections.projectionList()
	  			.add(Projections.property(McoProcReanimacao.Fields.DESCRICAO.toString()) ,MedicamentoRecemNascidoVO.Fields.DESCRICAO_PNI.toString())
	  			.add(Projections.property(McoProcReanimacao.Fields.MED_MAT_CODIGO.toString()) ,MedicamentoRecemNascidoVO.Fields.MED_MAT_CODIGO.toString())
	  			.add(Projections.property(McoProcReanimacao.Fields.IND_SITUACAO.toString()) ,MedicamentoRecemNascidoVO.Fields.IND_SITUACAO.toString())
	  			.add(Projections.property(McoProcReanimacao.Fields.CSA_CODIGO.toString()) ,MedicamentoRecemNascidoVO.Fields.CODIGO_CSA.toString())
	  			.add(Projections.property(McoProcReanimacao.Fields.SEQ.toString()) ,MedicamentoRecemNascidoVO.Fields.SEQ_PNI.toString())
	  			.add(Projections.property(McoProcReanimacao.Fields.SER_VIN_CODIGO.toString()) ,MedicamentoRecemNascidoVO.Fields.SER_VIN_CODIGO_PNI.toString())
	  			.add(Projections.property(McoProcReanimacao.Fields.SER_MATRICULA.toString()) ,MedicamentoRecemNascidoVO.Fields.SER_MATRICULA_PNI.toString())
	  			);
	  	criteria.addOrder(Order.asc(McoProcReanimacao.Fields.DESCRICAO.toString()));
	  	criteria.setResultTransformer(Transformers.aliasToBean(MedicamentoRecemNascidoVO.class));
	  	return executeCriteria(criteria);
	}

	private void adicionarFiltrosMcoProcReanimacao(String descricao,
			DetachedCriteria criteria) {
		if(StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(McoProcReanimacao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(McoProcReanimacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	}
	
	public Long buscarMedicamentosCountPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProcReanimacao.class);
		adicionarFiltrosMcoProcReanimacao(descricao, criteria);
		return executeCriteriaCount(criteria);
	}
	
	public List<ProcedimentoReanimacaoVO> listarProcReanimacao(String descricao,DominioSituacao situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProcReanimacao.class,"MPR");
		
		if(StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(McoProcReanimacao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(McoProcReanimacao.Fields.IND_SITUACAO.toString(), situacao));
		}
		criteria.createAlias("MPR." + McoProcReanimacao.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "ACS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MPR." + McoProcReanimacao.Fields.AFA_MEDICAMENTO.toString()         , "AFM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.SEQ.toString()), ProcedimentoReanimacaoVO.Fields.SEQ.toString())
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("ACS." + AbsComponenteSanguineo.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.COMPONENTE.toString())
				.add(Projections.property("AFM." + AfaMedicamento.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.MEDICAMENTO.toString())
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.IND_SITUACAO.toString()), ProcedimentoReanimacaoVO.Fields.SITUACAO.toString())
		);
		

		criteria.addOrder(Order.asc("MPR."+ McoProcReanimacao.Fields.DESCRICAO.toString()));	
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoReanimacaoVO.class));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	
	public Long listarProcReanimacaoCount(String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProcReanimacao.class,"MPR");

		if(StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(McoProcReanimacao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		/*if(StringUtils.isNotBlank(situacao)){
			criteria.add(Restrictions.eq(McoProcReanimacao.Fields.IND_SITUACAO.toString(), situacao));
		}*/
		criteria.createAlias("MPR." + McoProcReanimacao.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "ACS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MPR." + McoProcReanimacao.Fields.AFA_MEDICAMENTO.toString()         , "AFM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.SEQ.toString()), ProcedimentoReanimacaoVO.Fields.SEQ.toString())
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("ACS." + AbsComponenteSanguineo.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.COMPONENTE.toString())
				.add(Projections.property("AFM." + AfaMedicamento.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.MEDICAMENTO.toString())
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.IND_SITUACAO.toString()), ProcedimentoReanimacaoVO.Fields.SITUACAO.toString())
		);
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoReanimacaoVO.class));
		
		return executeCriteriaCount(criteria);
	}
	
	public ProcedimentoReanimacaoVO obterProcReanimacao(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoProcReanimacao.class,"MPR");
		
		
		criteria.add(Restrictions.eq(McoProcReanimacao.Fields.SEQ.toString(), seq));
		
		
		criteria.createAlias("MPR." + McoProcReanimacao.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "ACS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MPR." + McoProcReanimacao.Fields.AFA_MEDICAMENTO.toString()         , "AFM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.SEQ.toString()), ProcedimentoReanimacaoVO.Fields.SEQ.toString())
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("ACS." + AbsComponenteSanguineo.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.COMPONENTE.toString())
				.add(Projections.property("ACS." + AbsComponenteSanguineo.Fields.CODIGO.toString()), ProcedimentoReanimacaoVO.Fields.COMP_CODIGO.toString())
				.add(Projections.property("AFM." + AfaMedicamento.Fields.DESCRICAO.toString()), ProcedimentoReanimacaoVO.Fields.MEDICAMENTO.toString())
				.add(Projections.property("AFM." + AfaMedicamento.Fields.MAT_CODIGO.toString()), ProcedimentoReanimacaoVO.Fields.MAT_CODIGO.toString())
				.add(Projections.property("MPR." + McoProcReanimacao.Fields.IND_SITUACAO.toString()), ProcedimentoReanimacaoVO.Fields.SITUACAO.toString())
		);
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoReanimacaoVO.class));
		
		return (ProcedimentoReanimacaoVO) executeCriteriaUniqueResult(criteria);
	}

}
