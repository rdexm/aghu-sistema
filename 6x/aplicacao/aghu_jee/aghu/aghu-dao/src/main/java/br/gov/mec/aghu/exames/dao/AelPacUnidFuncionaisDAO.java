package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelPacUnidFuncionaisId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;

public class AelPacUnidFuncionaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelPacUnidFuncionais> {

	private static final long serialVersionUID = 5413290575666351898L;


	@Override
	protected void obterValorSequencialId(AelPacUnidFuncionais elemento) {
		if (elemento == null || elemento.getAelProtocoloInternoUnids() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		AelPacUnidFuncionaisId id = new AelPacUnidFuncionaisId();
		id.setPiuPacCodigo(elemento.getAelProtocoloInternoUnids().getId().getPacCodigo());
		id.setUnidadeFuncional(elemento.getAelProtocoloInternoUnids().getId().getUnidadeFuncional());
		
		elemento.setId(id);
		
		final Integer maxSeqp = this.obterSeqpMax(id);
		if (maxSeqp != null) {
			elemento.getId().setSeqp(maxSeqp+1);
		}else{
			elemento.getId().setSeqp(1);
		}
	}
	
	public Integer obterSeqpMax(AelPacUnidFuncionaisId param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPacUnidFuncionais.class);

		criteria.setProjection(Projections.max(AelPacUnidFuncionais.Fields.NRO_CONTROLE_EXAME_PACIENTE.toString()));
		
		criteria.add(Restrictions.eq(AelPacUnidFuncionais.Fields.PIU_PAC_CODIGO.toString(), 
				param.getPiuPacCodigo()));
		criteria.add(Restrictions.eq(AelPacUnidFuncionais.Fields.SEQUENCIA_UNIDADE_FUNCIONAL.toString(), 
				param.getUnidadeFuncional().getSeq()));

		final Object objMax = this.executeCriteriaUniqueResult(criteria);
		return (Integer) objMax;
	}
	
	public List<AelPacUnidFuncionais> pesquisarUnidadesFuncionaisPaciente(
			Integer codigoPaciente, Short sequenciaUnidadeFuncional) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelPacUnidFuncionais.class);

		criteria = this.obterCriterioConsulta(criteria, codigoPaciente, sequenciaUnidadeFuncional);
		
		return executeCriteria(criteria);
	}
	
	public List<AelPacUnidFuncionais> listarUnidadesFuncionaisPaciente(
			Integer codigoPaciente, Short unfSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelPacUnidFuncionais.class);
		
		criteria.createCriteria(AelPacUnidFuncionais.Fields.EQUIPAMENTO.toString(), Criteria.LEFT_JOIN);
		criteria.createCriteria(AelPacUnidFuncionais.Fields.ITEM_SOLICITACAO_EXAME.toString(), 
				"ISE", Criteria.LEFT_JOIN);
		criteria.createCriteria("ISE.".concat(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()), 
				"SOE", Criteria.LEFT_JOIN);
		criteria.createCriteria("SOE.".concat(AelSolicitacaoExames.Fields.FAT_CONVENIO_SAUDE.toString()), 
				"CNV", Criteria.LEFT_JOIN);
		criteria.createCriteria("SOE.".concat(AelSolicitacaoExames.Fields.ATENDIMENTO.toString()), 
				"ATD", Criteria.LEFT_JOIN);
		criteria.createCriteria("SOE.".concat(AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString()), 
				"ATV", Criteria.LEFT_JOIN);
		
		criteria = this.obterCriterioConsulta(criteria, codigoPaciente, unfSeq);
		
		criteria.addOrder(Order.asc(AelPacUnidFuncionais.Fields.NRO_CONTROLE_EXAME_PACIENTE.toString()));

		return executeCriteria(criteria);
	}
	
	
	private DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria, Integer codigoPaciente, Short unfSeq) {
		
		if (codigoPaciente != null) {
			criteria.add(Restrictions.eq(
					AelPacUnidFuncionais.Fields.PIU_PAC_CODIGO.toString(),
					codigoPaciente));
		}

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(
					AelPacUnidFuncionais.Fields.SEQUENCIA_UNIDADE_FUNCIONAL
							.toString(), unfSeq));
		}
		
		return criteria;
	}
	
	
	public List<AelPacUnidFuncionais> pesquisarAelPacUnidFuncionais(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPacUnidFuncionais.class);
		criteria.add(Restrictions.eq(AelPacUnidFuncionais.Fields.PIU_PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public Integer listarMaxNumeroControleExamePaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPacUnidFuncionais.class);
		
		criteria.add(Restrictions.eq(AelPacUnidFuncionais.Fields.PIU_PAC_CODIGO.toString(), pacCodigo));
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.max(AelPacUnidFuncionais.Fields.NRO_CONTROLE_EXAME_PACIENTE.toString()));
		criteria.setProjection(pList);

		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
}
