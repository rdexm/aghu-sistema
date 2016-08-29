package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoRecemNascidos;

public class McoExameFisicoRnsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoExameFisicoRns> {

	private static final long serialVersionUID = 3013124836234243863L;

	public List<McoExameFisicoRns> listarExamesFisicosRns(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoExameFisicoRns.class);


		criteria.add(Restrictions.eq(McoExameFisicoRns.Fields.GESTACOES_CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoExameFisicoRns.Fields.GESTACOES_SEQUENCE.toString(), gsoSeqp));

		return executeCriteria(criteria);
	}

	public List<McoExameFisicoRns> listarExamesFisicosRnsPorGestacoesCodigoPaciente(Integer gsoPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoExameFisicoRns.class);


		criteria.add(Restrictions.eq(McoExameFisicoRns.Fields.GESTACOES_CODIGO_PACIENTE.toString(), gsoPacCodigo));

		return executeCriteria(criteria);
	}

	/**
	 * #27482 - C2
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	public McoExameFisicoRns obterMcoExameFisicoRnPorId(Integer gsoPacCodigo, Short gsoSeqp, Byte seqp) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoExameFisicoRns.class);
		criteria.add(Restrictions.eq(McoExameFisicoRns.Fields.GESTACOES_CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoExameFisicoRns.Fields.GESTACOES_SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoExameFisicoRns.Fields.SEQP.toString(), seqp));
		return (McoExameFisicoRns) executeCriteriaUniqueResult(criteria);
	}

	

	/**
	 * #27482 - C10
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	public Byte obterMaxSeqRecemNascido(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoExameFisicoRns.class);
		criteria.add(Restrictions.eq(McoExameFisicoRns.Fields.GESTACOES_CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoExameFisicoRns.Fields.GESTACOES_SEQUENCE.toString(), gsoSeqp));
	 	criteria.setProjection(Projections.projectionList().add(Projections.max(McoExameFisicoRns.Fields.SEQP.toString())));
		return (Byte) executeCriteriaUniqueResult(criteria);
	}
	public Boolean isExameFisicoRealizado(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoExameFisicoRns.class);
		criteria.createAlias(McoExameFisicoRns.Fields.RECEM_NASCIDO.toString(), "RNSC");
		criteria.createAlias("RNSC." + McoRecemNascidos.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));

		return executeCriteriaCount(criteria) > 0 ? true : false;
	}

}
