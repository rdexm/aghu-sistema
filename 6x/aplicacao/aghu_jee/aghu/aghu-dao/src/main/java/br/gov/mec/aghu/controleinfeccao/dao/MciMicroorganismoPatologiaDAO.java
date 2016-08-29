package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MciMicroorgPatologiaExame;
import br.gov.mec.aghu.model.MciMicroorganismoPatologia;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;

public class MciMicroorganismoPatologiaDAO
extends
br.gov.mec.aghu.core.persistence.dao.BaseDao<MciMicroorganismoPatologia> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4365556101346643257L;

	// #1326 C3
	public List<MciMicroorganismoPatologia> buscarMicroorganismoAssociadoExame(
			Integer seqPai, Short seqP, Integer seqExame, Integer seqGrupoExame) {
		DetachedCriteria criteria = criarCriteriaMicroorganismo(seqPai, seqP);
		if (seqExame != null) {
			criteria.add(Restrictions.eq("MPE."
					+ MciMicroorgPatologiaExame.Fields.RCD_SEQP.toString(),
					seqExame));
		}
		if (seqGrupoExame != null) {
			criteria.add(Restrictions.eq("MPE."
					+ MciMicroorgPatologiaExame.Fields.RCD_GTC_SEQ.toString(),
					seqGrupoExame));
		}

		return executeCriteria(criteria);
	}

	// #1326 c1
	public List<MciMicroorganismoPatologia> buscarMicroorganismoPorSeqInfeccao(
			Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				MciMicroorganismoPatologia.class, "MPT");
		criteria.createAlias(
				"MPT."
						+ MciMicroorganismoPatologia.Fields.MCI_PATOLOGIA_INFECCAO
						.toString(), "PAI");
		if (seq != null) {
			criteria.add(Restrictions.eq("PAI."
					+ MciPatologiaInfeccao.Fields.SEQ.toString(), seq));
		}
		return executeCriteria(criteria);
	}

	public Short obterProximoSeqPMciMicroorganismoPatologia(Integer seqPai) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				MciMicroorganismoPatologia.class, "MPT");
		criteria.add(Restrictions.eq("MPT."
				+ MciMicroorganismoPatologia.Fields.ID_PAI_SEQ.toString(),
				seqPai));
		criteria.setProjection(Projections.max("MPT."
				+ MciMicroorganismoPatologia.Fields.ID_SEQP.toString()));

		Short sequencia = (Short) executeCriteriaUniqueResult(criteria);
		sequencia = (sequencia == null) ? (short) 0 : sequencia;
		return (short) (sequencia + Short.valueOf("1"));
	}


	public List<MciMicroorganismoPatologia> pesquisarMciMicroorganismoPatologiaPorPatologia(final Integer codigoPatologia){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMicroorganismoPatologia.class);
		criteria.createAlias(MciMicroorganismoPatologia.Fields.MCI_PATOLOGIA_INFECCAO.toString(), "MPI", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MPI." + MciPatologiaInfeccao.Fields.SEQ.toString(), codigoPatologia));
		return executeCriteria(criteria);
	}

	private DetachedCriteria criarCriteriaMicroorganismo(Integer seqPai,
			Short seqP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				MciMicroorganismoPatologia.class, "MPT");
		criteria.createAlias(
				"MPT."
						+ MciMicroorganismoPatologia.Fields.MCI_MICROORG_PATOLOGIA_EXAMEES
						.toString(), "MPE");

		if (seqPai != null) {
			criteria.add(Restrictions.eq("MPT."
					+ MciMicroorganismoPatologia.Fields.ID_PAI_SEQ.toString(),
					seqPai));
		}
		if (seqP != null) {
			criteria.add(Restrictions.eq("MPT."
					+ MciMicroorganismoPatologia.Fields.ID_SEQP.toString(),
					seqP));
		}
		return criteria;
	}
}

