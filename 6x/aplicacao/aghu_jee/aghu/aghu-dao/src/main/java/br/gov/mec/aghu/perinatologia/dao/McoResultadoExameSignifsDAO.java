package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;

public class McoResultadoExameSignifsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoResultadoExameSignifs> {

	private static final long serialVersionUID = -7490381426618322507L;

	public List<McoResultadoExameSignifs> listarResultadosExamesSignifs(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoResultadoExameSignifs.class);

		criteria.add(Restrictions.eq(McoResultadoExameSignifs.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoResultadoExameSignifs.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoResultadoExameSignifs> listarResultadosExamesSignifsPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoResultadoExameSignifs.class);

		criteria.add(Restrictions.eq(McoResultadoExameSignifs.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}
	
	

	public List<McoResultadoExameSignifs> listarResultadosExamesSignifsPorGestacao(Integer codigoPaciente, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoResultadoExameSignifs.class);
		criteria.createAlias(McoResultadoExameSignifs.Fields.MCO_GESTACOES.toString(), "mcoGestacoes");
		criteria.add(Restrictions.eq("mcoGestacoes" + "." + McoGestacoes.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq("mcoGestacoes" + "." + McoGestacoes.Fields.SEQUENCE.toString(), seqp));

		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta utilizada para obten��o do novo SEQP para inser��o de exame em MCO_RESULTA_EXAME_SIGNIFS
	 * 
	 * C4 de #25644
	 */
	@Override
	protected void obterValorSequencialId(McoResultadoExameSignifs elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getGsoPacCodigo() == null
				|| elemento.getId().getGsoSeqp() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(McoResultadoExameSignifs.class);
		criteria.add(Restrictions.eq(McoResultadoExameSignifs.Fields.CODIGO_PACIENTE.toString(), elemento.getId().getGsoPacCodigo()));
		criteria.add(Restrictions.eq(McoResultadoExameSignifs.Fields.SEQUENCE.toString(), elemento.getId().getGsoSeqp()));
		criteria.setProjection(Projections.max(McoResultadoExameSignifs.Fields.SEQP.toString()));
		Short seqp = (Short) super.executeCriteriaUniqueResult(criteria);
		if (seqp == null) {
			seqp = 0;
		}
		seqp++;
		elemento.getId().setSeqp(seqp);
	}
	
	public Boolean pesquisarExameExternoUtilizado(Integer seq) {

		DetachedCriteria criteria = montarCriteriaPesquisarExameExternoUtilizado(seq);
		
		return executeCriteriaExists(criteria);
	}
	
	private DetachedCriteria montarCriteriaPesquisarExameExternoUtilizado(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(McoResultadoExameSignifs.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(McoResultadoExameSignifs.Fields.EXA_EXT_SEQ.toString(), seq));
		}

		return criteria;
	}
	
	public List<McoResultadoExameSignifs> pesquisarMcoResultadoExameSignifsPorGSO(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoResultadoExameSignifs.class);
		criteria.add(Restrictions.eq(McoResultadoExameSignifs.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoResultadoExameSignifs.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.addOrder(Order.asc(McoResultadoExameSignifs.Fields.SEQP.toString()));
		criteria.addOrder(Order.desc(McoResultadoExameSignifs.Fields.DATA_REALIZACAO.toString()));
		return super.executeCriteria(criteria);
	}
}
