package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmTextoPadraoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoTextoPadrao;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MpmTextoPadraoLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTextoPadraoLaudo> {

	private static final long serialVersionUID = 7391394144900842053L;

	/**
	 * Lista uma sequencia de  Textos Padrao Laudo filtrando pelo identificador do laudo.
	 * @param laudoSeq (identificador do laudo)
	 * @return retorna uma lista de MpmTextoPadraoLaudo
	 */
	public List<MpmTextoPadraoLaudo> listarTextoPadraoLaudosPorLaudo(Integer laudoSeq){
		List<MpmTextoPadraoLaudo> lista = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTextoPadraoLaudo.class);
		
		criteria.createAlias(MpmTextoPadraoLaudo.Fields.TIPOS_LAUDO_TEXTO_PADRAO.toString(), MpmTextoPadraoLaudo.Fields.TIPOS_LAUDO_TEXTO_PADRAO.toString(), Criteria.INNER_JOIN);
		criteria.createAlias(MpmTextoPadraoLaudo.Fields.TIPOS_LAUDO_TEXTO_PADRAO.toString() + "." + MpmTipoLaudoTextoPadrao.Fields.TIPO_LAUDO.toString() , MpmTipoLaudoTextoPadrao.Fields.TIPO_LAUDO.toString(), Criteria.INNER_JOIN);
		criteria.createAlias(MpmTipoLaudoTextoPadrao.Fields.TIPO_LAUDO.toString() + "." + MpmTipoLaudo.Fields.LAUDOS.toString() , MpmTipoLaudo.Fields.LAUDOS.toString(), Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(MpmTipoLaudo.Fields.LAUDOS.toString() + "." + MpmLaudo.Fields.SEQ.toString(), laudoSeq));
		criteria.add(Restrictions.eq(MpmTextoPadraoLaudo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		lista = executeCriteria(criteria);
		
		return lista;
	}

	public List<MpmTextoPadraoLaudo> listarTextoPadraoLaudo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createTextoPadraoLaudoCriteria(seq, descricao, situacao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long listarTextoPadraoLaudoCount(Integer seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createTextoPadraoLaudoCriteria(seq, descricao, situacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createTextoPadraoLaudoCriteria(Integer seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTextoPadraoLaudo.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(MpmTextoPadraoLaudo.Fields.SEQ.toString(), seq));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MpmTextoPadraoLaudo.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(MpmTextoPadraoLaudo.Fields.IND_SITUACAO.toString(), situacao));
		}

		return criteria;
	}

	public List<MpmTextoPadraoLaudo> pesquisarTextosPadrao(String filtro) {
		DetachedCriteria criteria = createTextoPadraoLaudoCriteria(filtro);
		return executeCriteria(criteria, 0, 25, MpmTextoPadraoLaudo.Fields.DESCRICAO.toString(), true);
	}

	public Long pesquisarTextosPadraoCount(String filtro) {
		DetachedCriteria criteria = createTextoPadraoLaudoCriteria(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createTextoPadraoLaudoCriteria(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTextoPadraoLaudo.class);

		if (StringUtils.isNotBlank(filtro)) {
			if (CoreUtil.isNumeroInteger(filtro)) {
				criteria.add(Restrictions.or(Restrictions.eq(MpmTextoPadraoLaudo.Fields.SEQ.toString(), Integer.valueOf(filtro)),
						Restrictions.ilike(MpmTextoPadraoLaudo.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike(MpmTextoPadraoLaudo.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}

		}
		
		criteria.add(Restrictions.eq(MpmTextoPadraoLaudo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}

}
