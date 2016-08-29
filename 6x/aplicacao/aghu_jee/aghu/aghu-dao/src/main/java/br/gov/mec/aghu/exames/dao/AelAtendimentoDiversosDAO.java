package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelAtendimentoDiversos;

public class AelAtendimentoDiversosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAtendimentoDiversos> {

	private static final long serialVersionUID = -8466035167220115295L;

	public List<AelAtendimentoDiversos> pesquisarAtendimentosDiversorPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAtendimentoDiversos.class);

		criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public Long pesquisarAelAtendimentoDiversosCount(final AelAtendimentoDiversos filtros){
		return executeCriteriaCount(obterCriteria(filtros));
	}
	
	public List<AelAtendimentoDiversos> pesquisarAelAtendimentoDiversos(final AelAtendimentoDiversos filtros, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		final DetachedCriteria criteria = obterCriteria(filtros);
		
		criteria.createAlias(AelAtendimentoDiversos.Fields.PROJETO_PESQUISA.toString(),"prj", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelAtendimentoDiversos.Fields.LABORATORIO_EXTERNO.toString(), "lab", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelAtendimentoDiversos.Fields.CAD_CTRL_QUALIDADES.toString(), "ctrl", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelAtendimentoDiversos.Fields.DADOS_CADAVERES.toString(), "cad", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelAtendimentoDiversos.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		
		//Joins para evitar erros de lazy 
		criteria.createAlias(AelAtendimentoDiversos.Fields.CENTRO_CUSTOS.toString(),"fcc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelAtendimentoDiversos.Fields.ESPECIALIDADES.toString(),"espc", JoinType.LEFT_OUTER_JOIN);
		 
		if(StringUtils.isEmpty(orderProperty)){ 
			orderProperty =  AelAtendimentoDiversos.Fields.PROJETO_PESQUISA.toString();
			Order orderSeqAtendDiv = Order.asc(AelAtendimentoDiversos.Fields.SEQUENCE.toString());
			criteria.addOrder(orderSeqAtendDiv);
			asc = true;
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria obterCriteria(final AelAtendimentoDiversos filtros) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAtendimentoDiversos.class);

		criteria.add(Restrictions.isNull(AelAtendimentoDiversos.Fields.ABS_AMOSTRA_DOACAO.toString()));
		
		if(filtros != null){
			if(filtros.getSeq() != null){
				criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.SEQUENCE.toString(), filtros.getSeq()));
			}
			
			if(filtros.getAelProjetoPesquisas() != null){
				criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.PROJETO_PESQUISA.toString(), filtros.getAelProjetoPesquisas()));
			}
			
			if(filtros.getAelLaboratorioExternos() != null){
				criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.LABORATORIO_EXTERNO.toString(), filtros.getAelLaboratorioExternos()));
			}
			
			if(filtros.getAelCadCtrlQualidades() != null){
				criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.CAD_CTRL_QUALIDADES.toString(), filtros.getAelCadCtrlQualidades()));
			}
			
			if(filtros.getAelDadosCadaveres() != null){
				criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.DADOS_CADAVERES.toString(), filtros.getAelDadosCadaveres()));
			}
			
			if(filtros.getAipPaciente() != null){
				criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.PACIENTE.toString(), filtros.getAipPaciente()));
			}
			
			if(StringUtils.isNotEmpty(filtros.getNomePaciente())){
				criteria.add(Restrictions.ilike(AelAtendimentoDiversos.Fields.NOME_PACIENTE.toString(), filtros.getNomePaciente(), MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	public List<AelAtendimentoDiversos> pesquisarAtendimentoDiversoPorCadaver(Integer ddvSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAtendimentoDiversos.class);
		criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.DDV_SEQ.toString(), ddvSeq));
		return executeCriteria(criteria);
	}

	public AelAtendimentoDiversos obterAtendimentoDiversoComPaciente(Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAtendimentoDiversos.class);
		criteria.add(Restrictions.eq(AelAtendimentoDiversos.Fields.SEQUENCE.toString(), seq));
		criteria.createAlias(AelAtendimentoDiversos.Fields.PACIENTE.toString(), "pac");
		return (AelAtendimentoDiversos) executeCriteriaUniqueResult(criteria);
	}
	
	public AelAtendimentoDiversos obterAtendimentoDiversoPorSolicitacaoExame(Integer soeSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAtendimentoDiversos.class);
		criteria.createAlias(AelAtendimentoDiversos.Fields.SOLICITACAO_EXAMES.toString(), "soe");
		
		criteria.add(Restrictions.eq("soe.seq", soeSeq));
		return (AelAtendimentoDiversos) executeCriteriaUniqueResult(criteria);
	}
	
}
