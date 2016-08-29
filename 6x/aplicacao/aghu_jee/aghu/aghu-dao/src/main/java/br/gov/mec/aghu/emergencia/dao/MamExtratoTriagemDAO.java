package br.gov.mec.aghu.emergencia.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamExtratoTriagem;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamExtratoTriagem
 * 
 * @author luismoura
 * 
 */
public class MamExtratoTriagemDAO extends BaseDao<MamExtratoTriagem> {
	private static final long serialVersionUID = -3798652483347817019L;

	/**
	 * Executa o count da pesquisa de ExtratoTriagem da situação de emergência pelo código da situação
	 * 
	 * C6 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigoSit
	 * @return
	 */
	public Long pesquisarExtratoTriagemSituacaoEmergenciaCount(Short codigoSit) {

		final DetachedCriteria criteria = this.montarPesquisaExtratoTriagemSituacaoEmergencia(codigoSit);

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Monta a pesquisa de ExtratoTriagem da situação de emergência pelo código da situação
	 * 
	 * C6 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigoSit
	 * @return
	 */
	private DetachedCriteria montarPesquisaExtratoTriagemSituacaoEmergencia(Short codigoSit) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoTriagem.class, "MamExtratoTriagem");

		if (codigoSit != null) {
			criteria.createAlias("MamExtratoTriagem." + MamExtratoTriagem.Fields.MAM_SITUACAO_EMERGENCIAS.toString(), "MamSituacaoEmergencia");
			criteria.add(Restrictions.eq("MamSituacaoEmergencia." + MamSituacaoEmergencia.Fields.SEQ.toString(), codigoSit));
		}

		return criteria;
	}

	/**
	 * C10
	 * 
	 * @param seqTriagem
	 * @return
	 */
	public Short obterMaxSeqPExtratoTriagem(Long seqTriagem) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoTriagem.class, "MamExtratoTriagem");

		criteria.add(Restrictions.eq("MamExtratoTriagem." + MamExtratoTriagem.Fields.TRG_SEQ.toString(), seqTriagem));

		criteria.setProjection(Projections.max("MamExtratoTriagem." + MamExtratoTriagem.Fields.SEQP.toString()));

		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
	}
	
	public List<Date> obterDtHrSituacaoExtratoTriagem(Long trgSeq, Short segSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoTriagem.class);
		
		criteria.setProjection(Projections.property(MamExtratoTriagem.Fields.DTHR_SITUACAO.toString()));
		
		criteria.add(Restrictions.eq(MamExtratoTriagem.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq(MamExtratoTriagem.Fields.SEG_SEQ.toString(), segSeq));
		
		criteria.addOrder(Order.asc(MamExtratoTriagem.Fields.SEQP.toString()));
		
		return executeCriteria(criteria);
	}

}