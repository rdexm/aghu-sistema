package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamTrgEncExternos;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamFluxograma
 * 
 * @author luismoura
 * 
 */
public class MamFluxogramaDAO extends BaseDao<MamFluxograma> {
	private static final long serialVersionUID = -1006818391764120786L;

	/**
	 * Pesquisa se existe MamFluxograma por descricao
	 * 
	 * @param descricao
	 * @return
	 */
	public Boolean existsFluxogramaPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFluxograma.class);
		criteria.add(Restrictions.eq(MamFluxograma.Fields.DESCRICAO.toString(), descricao));
		return super.executeCriteriaExists(criteria);
	}

	/**
	 * Monta a criteria de MamFluxograma por mamProtClassifRisco
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	private DetachedCriteria montarCriteriaFluxogramaPorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFluxograma.class);
		criteria.add(Restrictions.eq(MamFluxograma.Fields.PROT_CLASSIF_RISCO.toString(), mamProtClassifRisco));
		return criteria;
	}

	/**
	 * Pesquisa se existe MamFluxograma por mamProtClassifRisco
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	public Boolean existsFluxogramaPorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		DetachedCriteria criteria = this.montarCriteriaFluxogramaPorProtocolo(mamProtClassifRisco);
		return super.executeCriteriaExists(criteria);
	}

	/**
	 * Pesquisa MamFluxograma por mamProtClassifRisco
	 * 
	 * C2 de #32285 - Manter cadastro de fluxogramas utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	public List<MamFluxograma> pesquisarFluxogramaPorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		DetachedCriteria criteria = this.montarCriteriaFluxogramaPorProtocolo(mamProtClassifRisco);
		criteria.addOrder(Order.asc(MamFluxograma.Fields.DESCRICAO.toString()));
		return super.executeCriteria(criteria);
	}

	/**
	 * Monta a criteria de pesquisa MamFluxograma por seq e/ou descricao e/ou indSituacao e/ou seqProtocolo
	 * 
	 * @param seq
	 * @param descricao
	 * @param indSituacao
	 * @param seqProtocolo
	 * @return
	 */
	private DetachedCriteria montarPesquisaFluxogramaSeqDescSitProt(Integer seq, String descricao, DominioSituacao indSituacao, Integer seqProtocolo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamFluxograma.class);
		if (seq != null) {
			criteria.add(Restrictions.eq(MamFluxograma.Fields.SEQ.toString(), seq));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MamFluxograma.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(MamFluxograma.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		if (seqProtocolo != null) {
			criteria.createAlias(MamFluxograma.Fields.PROT_CLASSIF_RISCO.toString(), "PROT_CLASSIF_RISCO");
			criteria.add(Restrictions.eq("PROT_CLASSIF_RISCO." + MamProtClassifRisco.Fields.SEQ.toString(), seqProtocolo));
		}
		return criteria;
	}

	/**
	 * Consulta utilizada para popular a suggestionBox de fluxogramas
	 * 
	 * C1 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param seq
	 * @param descricao
	 * @param indSituacao
	 * @param seqProtocolo
	 * @return
	 */
	public List<MamFluxograma> pesquisarFluxogramaSeqDescSitProt(Integer seq, String descricao, DominioSituacao indSituacao, Integer seqProtocolo,
			Integer maxResults) {
		final DetachedCriteria criteria = this.montarPesquisaFluxogramaSeqDescSitProt(seq, descricao, indSituacao, seqProtocolo);
		return super.executeCriteria(criteria, 0, maxResults, MamFluxograma.Fields.ORDEM.toString(), true);
	}

	/**
	 * Consulta utilizada para popular a suggestionBox de fluxogramas
	 * 
	 * C1 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param seq
	 * @param descricao
	 * @param indSituacao
	 * @param seqProtocolo
	 * @return
	 */
	public Long pesquisarFluxogramaSeqDescSitProtCount(Integer seq, String descricao, DominioSituacao indSituacao, Integer seqProtocolo) {
		final DetachedCriteria criteria = this.montarPesquisaFluxogramaSeqDescSitProt(seq, descricao, indSituacao, seqProtocolo);
		return super.executeCriteriaCount(criteria);
	}
	
	public List<MamFluxograma> pesquisarFluxogramaPorTriagemEncExterno(Long seqTriagem, Short seqpTriagem) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamFluxograma.class, "FLX");
		

		criteria.createAlias("FLX." + MamFluxograma.Fields.DESCRITORES.toString(), "DCT", JoinType.INNER_JOIN);
		criteria.createAlias("DCT." + MamDescritor.Fields.MAM_TRG_GRAVIDADE.toString(), "TGG", JoinType.INNER_JOIN);
		criteria.createAlias("TGG." + MamTrgGravidade.Fields.MAM_TRIAGENS.toString(), "TRG", JoinType.INNER_JOIN);
		criteria.createAlias("TGG." + MamTrgGravidade.Fields.MAM_GRAVIDADES.toString(), "GRV", JoinType.INNER_JOIN);
		criteria.createAlias("TRG." + MamTriagens.Fields.MAM_TRG_ENC_EXTERNO.toString(), "TEE", JoinType.INNER_JOIN);
	
		criteria.add(Restrictions.eq("TEE." + MamTrgEncExternos.Fields.TRG_SEQ.toString(), seqTriagem));
		criteria.add(Restrictions.eq("TEE." + MamTrgEncExternos.Fields.SEQP.toString(), seqpTriagem));
		criteria.add(Restrictions.isNull("TEE." + MamTrgEncExternos.Fields.DTHR_ESTORNO.toString()));
		
		criteria.addOrder(Order.desc("TGG." + MamTrgGravidade.Fields.SEQP.toString()));

		return this.executeCriteria(criteria);
	}

}