package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MpmMotivoReinternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmMotivoReinternacao> {

	private static final long serialVersionUID = 6277780865416421838L;

	/**
	 * Busca na base uma lista de MpmMotivoReinternacao pelo filtro
	 */
	public List<MpmMotivoReinternacao> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			MpmMotivoReinternacao motivoReinternacao) {
		DetachedCriteria criteria = this.createCriteria(motivoReinternacao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	/**
	 * Busca na base o número de elementos da lista de MpmMotivoReinternacao pelo filtro
	 */
	public Long pesquisarCount(MpmMotivoReinternacao motivoReinternacao) {
		DetachedCriteria criteria = this.createCriteria(motivoReinternacao);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Cria a criteria do filtro
	 */
	private DetachedCriteria createCriteria(MpmMotivoReinternacao motivoReinternacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmMotivoReinternacao.class);

		if (motivoReinternacao != null) {
			if (motivoReinternacao.getSeq() != null) {
				criteria.add(Restrictions.eq(MpmMotivoReinternacao.Fields.SEQ.toString(), motivoReinternacao.getSeq()));
			}
			if (motivoReinternacao.getDescricao() != null && !motivoReinternacao.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(MpmMotivoReinternacao.Fields.DESCRICAO.toString(), motivoReinternacao.getDescricao(), MatchMode.ANYWHERE));
			}
			if (motivoReinternacao.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(MpmMotivoReinternacao.Fields.IND_SITUACAO.toString(),motivoReinternacao.getIndSituacao()));
			}
		}

		criteria.createAlias(MpmMotivoReinternacao.Fields.SERVIDOR.toString(), "SERV");
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES");

		return criteria;
	}

	/**
	 * Obtém um motivo reinternação pelo seu ID.
	 * 
	 * bsoliveira - 02/11/2010
	 * 
	 * @param {Integer} seq
	 * 
	 * @return {MpmMotivoReinternacao}
	 */
	public MpmMotivoReinternacao obterMotivoReinternacaoPeloId(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmMotivoReinternacao.class);
		criteria.add(Restrictions.eq(MpmMotivoReinternacao.Fields.SEQ
				.toString(), seq));
		MpmMotivoReinternacao retorno = (MpmMotivoReinternacao) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;

	}

	/**
	 * Lista MpmMotivoReinternacao
	 * 
	 * @param parametro
	 * @return
	 */
	public List<MpmMotivoReinternacao> obterMpmMotivoReinternacao(
			Object parametro) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmMotivoReinternacao.class);
		final String srtPesquisa = (String) parametro;

		if (StringUtils.isNotEmpty(srtPesquisa)) {

			criteria.add(Restrictions.ilike(
					MpmMotivoReinternacao.Fields.DESCRICAO.toString(),
					srtPesquisa, MatchMode.ANYWHERE));

		}

		if (CoreUtil.isNumeroInteger(parametro)) {

			criteria.add(Restrictions.eq(MpmMotivoReinternacao.Fields.SEQ
					.toString(), Integer.valueOf(srtPesquisa)));

		}

		criteria.add(Restrictions.eq(MpmMotivoReinternacao.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));

		return executeCriteria(criteria);

	}

}
