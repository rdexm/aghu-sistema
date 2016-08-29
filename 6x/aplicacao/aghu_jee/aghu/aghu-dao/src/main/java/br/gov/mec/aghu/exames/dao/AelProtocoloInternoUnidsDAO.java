package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelProtocoloInternoUnids;
import br.gov.mec.aghu.model.AelProtocoloInternoUnidsId;
import br.gov.mec.aghu.model.AipPacientes;

public class AelProtocoloInternoUnidsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelProtocoloInternoUnids> {

	private static final long serialVersionUID = 1404763737228180925L;


	@Override
	protected void obterValorSequencialId(AelProtocoloInternoUnids elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		AelProtocoloInternoUnidsId id = new AelProtocoloInternoUnidsId();
		id.setPacCodigo(elemento.getPaciente().getCodigo());
		id.setUnidadeFuncional(elemento.getUnidadeFuncional());
		
		elemento.setId(id);
		final Integer maxNroProtocolo = this.obterProtocoloMax(id);
		if (maxNroProtocolo != null) {
			elemento.setNroProtocolo(maxNroProtocolo + 1);
		}else{
			elemento.setNroProtocolo(1);
		}
	}
	
	
	public Integer obterProtocoloMax(final AelProtocoloInternoUnidsId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProtocoloInternoUnids.class);

		criteria.setProjection(Projections.max(AelProtocoloInternoUnids.Fields.NUMERO_PROTOCOLO.toString()));
		
		//criteria.add(Restrictions.eq(AelProtocoloInternoUnids.Fields.PACIENTE_CODIGO.toString(), 
			//	id.getPacCodigo()));
		criteria.add(Restrictions.eq(AelProtocoloInternoUnids.Fields.SEQUENCIA_UNIDADE_FUNCIONAL.toString(), 
				id.getUnidadeFuncional().getSeq()));
		
		final Object objMax = this.executeCriteriaUniqueResult(criteria);
		return (Integer) objMax;
	}
	
	
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelProtocoloInternoUnids.class);
	}
	
	public AelProtocoloInternoUnids obterProtocoloInterno(Integer codigoPaciente, Short unfSeq) {
		DetachedCriteria criteria = this.obterCriteria();
		criteria.createCriteria(AelProtocoloInternoUnids.Fields.PACIENTE.toString());
		criteria.createAlias(AelProtocoloInternoUnids.Fields.UNIDADE_FUNCIONAL.toString(), "UN_FUNC");
		
		criteria = this.montarCriteriaPesquisarProtocoloInterno(criteria, unfSeq, null, codigoPaciente);
		
		return  (AelProtocoloInternoUnids) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelProtocoloInternoUnids> pesquisarProtocolosPorPaciente(
			Integer codigoPaciente) {

		if (codigoPaciente == null) {
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelProtocoloInternoUnids.class);
		criteria.add(Restrictions.eq(
				AelProtocoloInternoUnids.Fields.PACIENTE_CODIGO.toString(),
				codigoPaciente));

		return executeCriteria(criteria);
	}
	
	public List<AelProtocoloInternoUnids> pesquisarAelProtocoloInternoUnids(
			Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelProtocoloInternoUnids.class);
		criteria.add(Restrictions.eq(
				AelProtocoloInternoUnids.Fields.PACIENTE_CODIGO.toString(),
				pacCodigo));

		return executeCriteria(criteria);
	}
	
	
	public List<AelProtocoloInternoUnids> listarProtocolosInternosUnids(AipPacientes paciente, Date criadoEm) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProtocoloInternoUnids.class);
		criteria.add(Restrictions.eq(AelProtocoloInternoUnids.Fields.PACIENTE_CODIGO.toString(),
				paciente == null ? null : paciente.getCodigo()));
		criteria.add(Restrictions.ge(AelProtocoloInternoUnids.Fields.CRIADO_EM.toString(), criadoEm));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Retorna uma lista de <br>
	 * registros da tabela <AEL_PROTOCOLO_INTERNO_UNIDS>
	 * conforme filtros de pesquisa.
	 * 
	 * @param unfSeq
	 * @param numeroProtocolo
	 * @param pacCodigo
	 * @return
	 */
	public List<AelProtocoloInternoUnids> pesquisarProtocoloInterno(Short unfSeq, Integer numeroProtocolo, 
			Integer codigoPaciente, Integer prontuario, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.obterCriteria();
		
		criteria.createCriteria(AelProtocoloInternoUnids.Fields.PACIENTE.toString(), "PAC");
				
		criteria.createAlias(AelProtocoloInternoUnids.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		
		criteria = this.montarCriteriaPesquisarProtocoloInterno(
				criteria, unfSeq, numeroProtocolo, codigoPaciente);

		if(prontuario != null) {
			criteria.add(Restrictions.eq(
					"PAC.".concat(AipPacientes.Fields.PRONTUARIO.toString()), prontuario));
		}
		criteria.addOrder(Order.asc(AelProtocoloInternoUnids.Fields.NUMERO_PROTOCOLO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	
	
	public Long pesquisarProtocoloInternoCount(Short unfSeq, Integer numeroProtocolo, 
			Integer codigoPaciente, Integer prontuario) {
		
		DetachedCriteria criteria = this.obterCriteria();
		criteria.createAlias(AelProtocoloInternoUnids.Fields.PACIENTE.toString(), "PAC");
		
		criteria.createAlias(AelProtocoloInternoUnids.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);

		criteria = this.montarCriteriaPesquisarProtocoloInterno(
				criteria, unfSeq, numeroProtocolo, codigoPaciente);
		
		if(prontuario != null) {
			criteria.add(Restrictions.eq(
					"PAC.".concat(AipPacientes.Fields.PRONTUARIO.toString()), prontuario));
		}
		
		return this.executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria montarCriteriaPesquisarProtocoloInterno(DetachedCriteria criteria, Short unfSeq, Integer numeroProtocolo, 
			Integer codigoPaciente){
				
		if(unfSeq != null) {
			criteria.add(Restrictions.eq(
					AelProtocoloInternoUnids.Fields.SEQUENCIA_UNIDADE_FUNCIONAL.toString(), unfSeq));
		}
		
		if(numeroProtocolo != null) {
			criteria.add(Restrictions.eq(
					AelProtocoloInternoUnids.Fields.NUMERO_PROTOCOLO.toString(), numeroProtocolo));
		}
		
		if(codigoPaciente != null) {
			criteria.add(Restrictions.eq(
					AelProtocoloInternoUnids.Fields.PACIENTE_CODIGO.toString(),	codigoPaciente));
		}
				
		return criteria;
	}

}
