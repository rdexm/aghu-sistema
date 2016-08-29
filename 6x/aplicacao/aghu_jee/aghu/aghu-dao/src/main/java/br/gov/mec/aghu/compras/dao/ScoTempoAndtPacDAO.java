package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoTemposAndtPacsId;

public class ScoTempoAndtPacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoTempoAndtPac> {

	private static final long serialVersionUID = -594518991954904231L;

	public List<ScoTempoAndtPac> listarTempoAndtPac(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			ScoModalidadeLicitacao modalidadeLicitacao, ScoLocalizacaoProcesso localizacaoProcesso, 
			ScoTempoAndtPac tempoLocalizacaoPac) {

		DetachedCriteria criteria = this.obterCriteriaBasica(modalidadeLicitacao, localizacaoProcesso, tempoLocalizacaoPac);
		criteria.createAlias(ScoTempoAndtPac.Fields.MODALIDADE_LICITACAO.toString(), "ML", JoinType.INNER_JOIN);
		criteria.createAlias(ScoTempoAndtPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LP", JoinType.INNER_JOIN);
		
		criteria.createAlias(ScoTempoAndtPac.Fields.SERVIDOR.toString(), "SE", JoinType.INNER_JOIN);
		criteria.createAlias("SE."+RapServidores.Fields.PESSOA_FISICA.toString(), "SE_PF", JoinType.LEFT_OUTER_JOIN);

		criteria.addOrder(Order.asc(ScoTempoAndtPac.Fields.MLC_CODIGO.toString()));
		criteria.addOrder(Order.asc(ScoTempoAndtPac.Fields.LCP_CODIGO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public ScoTempoAndtPac obterPorChavePrimariaSemLazy(ScoTemposAndtPacsId id){
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoTempoAndtPac.class);
		criteria.add(Restrictions.eq(ScoTempoAndtPac.Fields.ID.toString(), id));
		criteria.createAlias(ScoTempoAndtPac.Fields.MODALIDADE_LICITACAO.toString(), "ML", JoinType.INNER_JOIN);
		criteria.createAlias(ScoTempoAndtPac.Fields.LOCALIZACAO_PROCESSO.toString(), "LP", JoinType.INNER_JOIN);
		
		criteria.createAlias(ScoTempoAndtPac.Fields.SERVIDOR.toString(), "SE", JoinType.INNER_JOIN);
		criteria.createAlias("SE."+RapServidores.Fields.PESSOA_FISICA.toString(), "SE_PF", JoinType.LEFT_OUTER_JOIN);
		
		return (ScoTempoAndtPac) executeCriteriaUniqueResult(criteria);
	}

	public Long listarTempoAndtPacCount(
			ScoModalidadeLicitacao modalidadeLicitacao, ScoLocalizacaoProcesso localizacaoProcesso,
			ScoTempoAndtPac tempoLocalizacaoPac) {

		final DetachedCriteria criteria = this
				.obterCriteriaBasica(modalidadeLicitacao, localizacaoProcesso, tempoLocalizacaoPac);

		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaBasica(
			ScoModalidadeLicitacao modalidadeLicitacao, ScoLocalizacaoProcesso localizacaoProcesso,
			ScoTempoAndtPac tempoLocalizacaoPac) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoTempoAndtPac.class);

		if (modalidadeLicitacao != null) {
			if (modalidadeLicitacao.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoTempoAndtPac.Fields.MLC_CODIGO.toString(), modalidadeLicitacao.getCodigo()));
			}
		}

		if (localizacaoProcesso != null) {
			if (localizacaoProcesso.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoTempoAndtPac.Fields.LCP_CODIGO.toString(), localizacaoProcesso.getCodigo()));
			}
		}

		if (tempoLocalizacaoPac != null) {
			if (tempoLocalizacaoPac.getMaxDiasPermanencia() != null) {
				criteria.add(Restrictions.eq(
						ScoTempoAndtPac.Fields.MAX_DIAS_PERMANENCIA.toString(), tempoLocalizacaoPac.getMaxDiasPermanencia()));
			}
		}
		
		return criteria;
	}
	
	/**
	 * Obtem dias de permanência do andamento do PAC.
	 * 
	 * @param modalidade ID da Modalidade.
	 * @param localizacao ID da Localização.
	 */
	public Short obterMaxDiasPermAndamentoPac(
			String modalidadeId, Short localizacaoId) {
		ScoTempoAndtPac entidade = obterPorChavePrimaria(
				new ScoTemposAndtPacsId(modalidadeId, localizacaoId));
		
		if (entidade != null) {
			return entidade.getMaxDiasPermanencia();
		} else {
			return null;
		}
	}
}
