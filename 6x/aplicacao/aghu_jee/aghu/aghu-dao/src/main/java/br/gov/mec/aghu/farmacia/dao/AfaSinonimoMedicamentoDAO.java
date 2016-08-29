package br.gov.mec.aghu.farmacia.dao;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoId;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AfaSinonimoMedicamentoDAO extends
		AbstractMedicamentoDAO<AfaSinonimoMedicamento> {

	private static final long serialVersionUID = 7903015805686112450L;

	@Override
	public void obterValorSequencialId(AfaSinonimoMedicamento elemento) {
		if (elemento == null || elemento.getId() == null
				|| elemento.getId().getMedMatCodigo() == null) {
			throw new IllegalArgumentException("medicamento não pode ser nulo");
		}

		AfaSinonimoMedicamentoId id = new AfaSinonimoMedicamentoId();
		id.setMedMatCodigo(elemento.getMedicamento().getMatCodigo());
		Integer seqp = buscaMaxSeqAfaSinonimoMedicamento(id).intValue() + 1;
		id.setSeqp(seqp);

		elemento.setId(id);
	}
	
	public Long pesquisaSinonimoMedicamentoCount(AfaSinonimoMedicamento sinonimoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaSinonimoMedicamento.class);

		if (sinonimoMedicamento.getDescricao() != null) {
			criteria.add(Restrictions.eq(AfaSinonimoMedicamento.Fields.DESCRICAO.toString(), sinonimoMedicamento.getDescricao()));
		}

		if (sinonimoMedicamento.getId() != null && sinonimoMedicamento.getId().getMedMatCodigo() != null) {
			criteria.add(Restrictions.eq(AfaSinonimoMedicamento.Fields.ID_MED_MAT_CODIGO.toString(), sinonimoMedicamento.getId().getMedMatCodigo()));
		}
		return executeCriteriaCount(criteria);
	}


	/**
	 * Busca o maior sequencial associado a AfaSinonimoMedicamento
	 * 
	 * @param id
	 * @return
	 */
	private Integer buscaMaxSeqAfaSinonimoMedicamento(
			AfaSinonimoMedicamentoId id) {
		StringBuilder sql = new StringBuilder(150);
		sql.append("select max(sinonimoMedicamento.");
		sql.append(AfaSinonimoMedicamento.Fields.ID_SEQP);
		sql.append(") as maxSeq ");
		sql.append("from ").append(AfaSinonimoMedicamento.class.getName())
				.append(" sinonimoMedicamento");
		sql.append(" where sinonimoMedicamento.").append(
				AfaSinonimoMedicamento.Fields.ID_MED_MAT_CODIGO.toString())
				.append(" = :medMatCodigo ");

		Query query = createQuery(sql.toString());
		query.setParameter("medMatCodigo", id.getMedMatCodigo());

		Object maxSeq = query.getSingleResult();

		if (maxSeq == null) {
			return 0;
		}
		return (Integer) maxSeq;
	}

	@Override
	protected DetachedCriteria pesquisarCriteria(AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaSinonimoMedicamento.class);

		if (medicamento != null) {
			criteria.add(Restrictions.eq(
					AfaSinonimoMedicamento.Fields.MEDICAMENTO.toString(),
					medicamento));
		}

		return criteria;
	}
}
