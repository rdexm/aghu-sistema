package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSecaoConfiguravel;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelSecaoConfExames;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class AelSecaoConfExamesDAO extends BaseDao<AelSecaoConfExames> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8025695191269437466L;

	public List<AelSecaoConfExames> buscarPorLu2SeqEVersaoConf(Integer seq, Integer versaoConf){
		DetachedCriteria criteria = this.obterCriteria();
		criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.LU2_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.VERSAO_CONF.toString(), versaoConf));
		return this.executeCriteria(criteria);
	}

	public Integer buscarMaxVersaoConfPorLu2Seq(Integer seq){
		DetachedCriteria maxVersaoConf = this.obterCriteria();
		maxVersaoConf.add(Restrictions.eq(AelSecaoConfExames.Fields.LU2_SEQ.toString(), seq));
		maxVersaoConf.setProjection(Projections.max(AelSecaoConfExames.Fields.VERSAO_CONF.toString()));

		return (Integer)this.executeCriteriaUniqueResult(maxVersaoConf);
	}

	/**
	 * Busca o IND_OBRIGATORIO de uma seção configurável ativa, filtrando pela descrição e pela versão de configuração e pelo id da configuração de
	 * exames de laudo único.
	 * 
	 * C1 da #21585
	 * 
	 * @param secao
	 * @param versaoConf
	 * @param lu2Seq
	 * @return
	 */
	public Boolean buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel secao, Integer versaoConf, Integer lu2Seq) {
		DetachedCriteria criteria = this.criarCriteriaBuscar(DominioSituacao.A, secao, versaoConf, lu2Seq);
		criteria.setProjection(Projections.property(AelSecaoConfExames.Fields.IND_OBRIGATORIO.toString()));
		List<Boolean> result = this.executeCriteria(criteria, 0, 1, null, true);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	
	public Boolean buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel secao, Integer versaoConf, Integer lu2Seq) {
		DetachedCriteria criteria = this.criarCriteriaBuscar(DominioSituacao.A, secao, versaoConf, lu2Seq);
		criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.IND_IMPRIMIR.toString(), Boolean.TRUE));
		
		List<Boolean> result = this.executeCriteria(criteria, 0, 1, null, true);
		if (result != null && !result.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Método privado que retorna a apenas a criteria com os filtros informados, para poder ser reaproveitado.
	 * 
	 * @param situacao
	 * @param secao
	 * @param versaoConf
	 * @param lu2Seq
	 * @return
	 */
	private DetachedCriteria criarCriteriaBuscar(DominioSituacao situacao, DominioSecaoConfiguravel secao, Integer versaoConf, Integer lu2Seq) {
		DetachedCriteria criteria = this.obterCriteria();
		
		if (situacao != null) {
			criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.IND_SITUACAO.toString(), situacao));
		}
		if (secao != null) {
			criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.DESCRICAO.toString(), secao));
		}
		if (versaoConf != null) {
			criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.VERSAO_CONF.toString(), versaoConf));
		}
		if (lu2Seq != null) {
			criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.LU2_SEQ.toString(), lu2Seq));
		}
	
		return criteria;
	}

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelSecaoConfExames.class);
	}

	// C1
	public List<AelSecaoConfExames> buscaSecoesConfiguracaoObrigatorias(DominioSituacaoExamePatologia dominioSituacaoExamePatologia, Integer versaoConfig, Integer lu2Seq) {
		DetachedCriteria criteria = this.obterCriteria();
		criteria.createAlias(AelSecaoConfExames.Fields.LU2.toString(), "LU2");
		criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.IND_OBRIGATORIO.toString(), Boolean.TRUE));
		if(dominioSituacaoExamePatologia != null){
			criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.ETAPA_LAUDO.toString(), dominioSituacaoExamePatologia));
		}
		if(versaoConfig != null){
			criteria.add(Restrictions.eq(AelSecaoConfExames.Fields.VERSAO_CONF.toString(), versaoConfig));
		}
		if(lu2Seq != null){
			criteria.add(Restrictions.eq("LU2."+ AelConfigExLaudoUnico.Fields.SEQ.toString(), lu2Seq));
		}

		return this.executeCriteria(criteria);
	}

	public Boolean verificaBotoesTecnica(DominioSituacaoExamePatologia dominioSituacaoExamePatologia, Integer versaoConfig, Integer lu2Seq) {
		return buscaSecoesConfiguracaoObrigatorias(dominioSituacaoExamePatologia, versaoConfig, lu2Seq).size() > 0;
	}

}
