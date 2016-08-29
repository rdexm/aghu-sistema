package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamTrgEncExternos;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamProtClassifRisco
 * 
 * @author loleksinski
 * 
 */
public class MamProtClassifRiscoDAO extends BaseDao<MamProtClassifRisco> {
	private static final long serialVersionUID = 3734703115837214143L;

	public List<MamProtClassifRisco> pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricao(Object objPesquisa, Integer maxResults) {
		final DetachedCriteria criteria = montarCriteriaPesquisarProtocolosAtivosPorCodigoDescricao(objPesquisa);
		if(maxResults != null){
			return this.executeCriteria(criteria, 0, maxResults, MamProtClassifRisco.Fields.DESCRICAO.toString(), true);
		}
		criteria.addOrder(Order.asc(MamProtClassifRisco.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria);
	}

	public Long pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricaoCount(Object objPesquisa) {
		final DetachedCriteria criteria = montarCriteriaPesquisarProtocolosAtivosPorCodigoDescricao(objPesquisa);

		return this.executeCriteriaCount(criteria);
	}

	public DetachedCriteria montarCriteriaPesquisarProtocolosAtivosPorCodigoDescricao(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProtClassifRisco.class);

		if (objPesquisa != null) {
			if (CoreUtil.isNumeroInteger(objPesquisa.toString())) {
				criteria.add(Restrictions.eq(MamProtClassifRisco.Fields.SEQ.toString(), Integer.valueOf(objPesquisa.toString())));
			} else {
				criteria.add(Restrictions.ilike(MamProtClassifRisco.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(MamProtClassifRisco.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	/**
	 * Cria uma criteria genérica para filtrar MamProtClassifRisco por descricao e/ou indSituacao
	 * 
	 * C1 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	private DetachedCriteria montarCriteriaPesquisarProtClassifRisco(String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProtClassifRisco.class);
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MamProtClassifRisco.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(MamProtClassifRisco.Fields.IND_SITUACAO.toString(), indSituacao));
		}

		return criteria;
	}

	/**
	 * Pesquisa MamProtClassifRisco por descricao e/ou indSituacao
	 * 
	 * C1 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param descricao
	 * @param indSituacao
	 * @return
	 */
	public List<MamProtClassifRisco> pesquisarProtClassifRisco(String descricao, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = this.montarCriteriaPesquisarProtClassifRisco(descricao, indSituacao);
		criteria.addOrder(Order.asc(MamProtClassifRisco.Fields.SEQ.toString()));
		return super.executeCriteria(criteria);
	}

	/**
	 * Pesquisa se existe MamProtClassifRisco por descricao
	 * 
	 * C3 de #32283 - manter cadastro de protocolos de classificação de risco
	 * 
	 * @param descricao
	 * @return
	 */
	public Boolean existsProtClassifRiscoPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProtClassifRisco.class);
		criteria.add(Restrictions.eq(MamProtClassifRisco.Fields.DESCRICAO.toString(), descricao));
		return super.executeCriteriaExists(criteria);
	}
	
	public List<MamProtClassifRisco> pesquisarProtClassifRiscoPorTriagemEncExterno(Long seqTriagem, Short seqpTriagem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProtClassifRisco.class, "PCR");
		criteria.createAlias("PCR." + MamProtClassifRisco.Fields.MAM_UNID_ATEND.toString(), "UAN", JoinType.INNER_JOIN);
		
		DetachedCriteria subQueryTriagem = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		subQueryTriagem.createAlias("TRG." + MamTriagens.Fields.MAM_TRG_ENC_EXTERNO.toString(), "TEE", JoinType.INNER_JOIN);
		subQueryTriagem.add(Restrictions.eq("TEE." + MamTrgEncExternos.Fields.TRG_SEQ.toString(), seqTriagem));
		subQueryTriagem.add(Restrictions.eq("TEE." + MamTrgEncExternos.Fields.SEQP.toString(), seqpTriagem));
		subQueryTriagem.add(Restrictions.isNull("TEE." + MamTrgEncExternos.Fields.DTHR_ESTORNO.toString()));
		subQueryTriagem.setProjection(Projections.property("TRG."+ MamTriagens.Fields.UNF_SEQ.toString()));
		criteria.add(Subqueries.propertyEq("UAN." + MamUnidAtendem.Fields.UNF_SEQ.toString(), subQueryTriagem));
		
		return executeCriteria(criteria);
	}

}