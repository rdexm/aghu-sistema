package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamCaractSitEmerg;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamCaractSitEmerg
 * 
 * @author luismoura
 * 
 */
public class MamCaractSitEmergDAO extends BaseDao<MamCaractSitEmerg> {

	private static final long serialVersionUID = 4658896388566632834L;
	
	private static final String MAM_CARACT_SIT_EMERG= "MamCaractSitEmerg.";

	/**
	 * Executa a pesquisa de característica da situação de emergência pelo código da situação
	 * 
	 * C2 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigoSit
	 * @return
	 */
	public List<MamCaractSitEmerg> pesquisarCaracteristicaSituacaoEmergencia(Short codigoSit) {

		final DetachedCriteria criteria = this.montarPesquisaCaracteristicaSituacaoEmergencia(codigoSit);
		criteria.addOrder(Order.asc(MamCaractSitEmerg.Fields.SEG_SEQ.toString()));
		criteria.addOrder(Order.asc(MamCaractSitEmerg.Fields.CARACTERISTICA.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Executa o count da pesquisa de característica da situação de emergência pelo código da situação
	 * 
	 * C3 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigoSit
	 * @return
	 */
	public Long pesquisarCaracteristicaSituacaoEmergenciaCount(Short codigoSit) {

		final DetachedCriteria criteria = this.montarPesquisaCaracteristicaSituacaoEmergencia(codigoSit);

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Monta a pesquisa de característica da situação de emergência pelo código da situação
	 * 
	 * C3 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigoSit
	 * @return
	 */
	private DetachedCriteria montarPesquisaCaracteristicaSituacaoEmergencia(Short codigoSit) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamCaractSitEmerg.class, "MamCaractSitEmerg");

		if (codigoSit != null) {
			criteria.add(Restrictions.eq(MamCaractSitEmerg.Fields.SEG_SEQ.toString(), codigoSit));
		}

		return criteria;
	}

	/***
	 * @ORADB MAMK_SITUACAO_EMERG.MAMC_GET_SIT_EMERG
	 * @param caracteristica
	 * C7
	 */
	public List<Short> obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia caracteristica) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamCaractSitEmerg.class, "MamCaractSitEmerg");

		criteria.setProjection(Projections.property(MAM_CARACT_SIT_EMERG + MamCaractSitEmerg.Fields.SEG_SEQ.toString()));

		criteria.add(Restrictions.eq(MAM_CARACT_SIT_EMERG + MamCaractSitEmerg.Fields.CARACTERISTICA.toString(), caracteristica));
		criteria.add(Restrictions.eq(MAM_CARACT_SIT_EMERG + MamCaractSitEmerg.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return this.executeCriteria(criteria);
	}

	/***
	 * C9
	 */
	public Boolean isExisteSituacaoEmerg(Short segSeq, DominioCaracteristicaEmergencia caracEmergencia) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamCaractSitEmerg.class, "MamCaractSitEmerg");

		criteria.add(Restrictions.eq(MAM_CARACT_SIT_EMERG + MamCaractSitEmerg.Fields.SEG_SEQ.toString(), segSeq));
		criteria.add(Restrictions.eq(MAM_CARACT_SIT_EMERG + MamCaractSitEmerg.Fields.CARACTERISTICA.toString(), caracEmergencia));

		return (this.executeCriteriaCount(criteria) > 0);

	}
}