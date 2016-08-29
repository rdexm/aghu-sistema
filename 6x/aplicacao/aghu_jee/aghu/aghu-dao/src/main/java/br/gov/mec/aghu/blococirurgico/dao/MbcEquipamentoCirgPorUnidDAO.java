package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MbcEquipamentoCirgPorUnid;
import br.gov.mec.aghu.model.MbcEquipamentoCirgPorUnidId;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;

public class MbcEquipamentoCirgPorUnidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcEquipamentoCirgPorUnid> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5022123750226936105L;

	@Override
	protected void obterValorSequencialId(MbcEquipamentoCirgPorUnid elemento) {
		if (elemento == null || elemento.getMbcEquipamentoCirurgico() == null || elemento.getAghUnidadesFuncionais() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}

		MbcEquipamentoCirgPorUnidId id = new MbcEquipamentoCirgPorUnidId();
		id.setEuuSeq(elemento.getMbcEquipamentoCirurgico().getSeq());
		id.setUnfSeq(elemento.getAghUnidadesFuncionais().getSeq());
		elemento.setId(id);
	}

	protected DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEquipamentoCirgPorUnid.class);
		return criteria;
	}

	public List<MbcEquipamentoCirgPorUnid> listarEquipamentoCirurgicoPorUnid(MbcEquipamentoCirurgico equipamentoCirurgico) {

		DetachedCriteria criteria = this.obterCriteria();
		criteria.add(Restrictions.eq(MbcEquipamentoCirgPorUnid.Fields.MBC_EQUIPAMENTO_CIRURGICO.toString(), equipamentoCirurgico));
		criteria.createAlias(MbcEquipamentoCirgPorUnid.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UND_FUNC", JoinType.LEFT_OUTER_JOIN);

		return this.executeCriteria(criteria);
	}

	public MbcEquipamentoCirgPorUnid obterEquipamentoCirugCadastrado(Short euuSeq, Short unfSeq) {
		DetachedCriteria criteria = this.obterCriteria();

		criteria.add(Restrictions.eq(MbcEquipamentoCirgPorUnid.Fields.EUU_SEQ.toString(), euuSeq));
		criteria.add(Restrictions.eq(MbcEquipamentoCirgPorUnid.Fields.UNF_SEQ.toString(), unfSeq));

		List<MbcEquipamentoCirgPorUnid> result = this.executeCriteria(criteria);

		if (result.isEmpty()) {
			return null;
		}

		return result.get(0);
	}

	/**
	 * Obtem a quantidade de um MbcEquipamentoCirgPorUnid
	 * 
	 * @param euuSeq
	 * @param unfSeq
	 * @return
	 */
	public Short obterQuantidadePorId(Short euuSeq, Short unfSeq) {
		DetachedCriteria criteria = this.obterCriteria();

		criteria.setProjection(Projections.property(MbcEquipamentoCirgPorUnid.Fields.QUANTIDADE.toString()));

		criteria.add(Restrictions.eq(MbcEquipamentoCirgPorUnid.Fields.EUU_SEQ.toString(), euuSeq));
		criteria.add(Restrictions.eq(MbcEquipamentoCirgPorUnid.Fields.UNF_SEQ.toString(), unfSeq));

		return (Short) this.executeCriteriaUniqueResult(criteria);

	}

}
