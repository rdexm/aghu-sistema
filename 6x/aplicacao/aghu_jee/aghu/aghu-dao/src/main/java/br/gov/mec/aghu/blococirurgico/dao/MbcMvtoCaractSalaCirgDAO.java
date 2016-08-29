package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcMvtoCaractSalaCirg;
import br.gov.mec.aghu.model.MbcSalaCirurgica;

public class MbcMvtoCaractSalaCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMvtoCaractSalaCirg> {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4503841204953718109L;

	public List<MbcMvtoCaractSalaCirg> pesquisarHistoricoAlteracoesCaractSalas(
			MbcSalaCirurgica salaCirurgicaSelecionada,
			MbcCaracteristicaSalaCirg caracteristicaSalaCirgSelecionada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMvtoCaractSalaCirg.class);
		criteria.add(Restrictions.eq(MbcMvtoCaractSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), salaCirurgicaSelecionada));
		criteria.add(Restrictions.eq(MbcMvtoCaractSalaCirg.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), caracteristicaSalaCirgSelecionada));
		criteria.addOrder(Order.desc(MbcMvtoCaractSalaCirg.Fields.DT_INICIO_MVTO.toString()));
		criteria.addOrder(Order.desc(MbcMvtoCaractSalaCirg.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}
	
	public MbcMvtoCaractSalaCirg pesquisarUltimoMovimentoDaCaractSalaCirg(Short casSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMvtoCaractSalaCirg.class, "mvto");
		criteria.add(Restrictions.eq("mvto." + MbcMvtoCaractSalaCirg.Fields.SEQ_CARACTERISTICA_SALA_CIRGS.toString(), casSeq));
		criteria.add(Restrictions.isNull("mvto." + MbcMvtoCaractSalaCirg.Fields.DT_FIM_MVTO.toString()));
		return (MbcMvtoCaractSalaCirg) executeCriteriaUniqueResult(criteria);
	}

}
