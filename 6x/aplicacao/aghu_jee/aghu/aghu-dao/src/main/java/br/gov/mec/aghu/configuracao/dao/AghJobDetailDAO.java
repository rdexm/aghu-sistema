package br.gov.mec.aghu.configuracao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.RapServidores;

public class AghJobDetailDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghJobDetail> {

	private static final long serialVersionUID = 7591133034062616232L;

	public Long countAghJobDetailPaginator(Map<Object, Object> filtersMap) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghJobDetail.class);
		montarCriteria(criteria, filtersMap);
		return executeCriteriaCount(criteria);
	}

	public AghJobDetail obterAghJobDetailPorNome(String nomeProcesso) {
		AghJobDetail returnValeu = null;

		List<AghJobDetail> list = pesquisarAghJobDetailPorNome(nomeProcesso);

		if (list != null && !list.isEmpty()) {
			returnValeu = list.get(0);
		}

		if (list != null && list.size() > 1) {
			throw new IllegalStateException("Existe mais que um AghJobDetail com o mesmo NomeProcesso.");
		}

		return returnValeu;
	}

	public List<AghJobDetail> pesquisarAghJobDetailPorNome(String nomeProcesso) {
		if (nomeProcesso == null || "".equals(nomeProcesso.trim())) {
			throw new IllegalArgumentException("parametro obrigatorio nao informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(AghJobDetail.class);

		criteria.add(Restrictions.eq(AghJobDetail.Fields.NOME_PROCESSO.toString(), nomeProcesso));

		return this.executeCriteria(criteria);
	}

	public List<AghJobDetail> buscarAghJobDetail(final String nomeJobEncerramento, final DominioSituacaoJobDetail[] indSituacao) {
		return executeCriteria(obterCriteriaBuscarAghJobDetail(nomeJobEncerramento, indSituacao));
	}

	private DetachedCriteria obterCriteriaBuscarAghJobDetail(final String nomeJobEncerramento, final DominioSituacaoJobDetail[] indSituacao) {
		// basedo em FUNCTION F_EXISTE_JOBS
		/*
		 * select to_number(nvl(count(*),0)) from dba_jobs dbaj , v_jobs_plus vw
		 * where vw.what like '%fatk_amb_rn.RN_FATP_EXEC_FAT_NEW%' and
		 * upper(vw.situacao) in ('AGENDADO','EXECUTANDO') and dbaj.job = vw.job
		 * and dbaj.next_date between p_dt_prog - 0.125 -- soma 3 horas and
		 * p_dt_prog + 0.125 ; -- subtrai 3 hor
		 */

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghJobDetail.class);
		criteria.add(Restrictions.ilike(AghJobDetail.Fields.NOME_PROCESSO.toString(), nomeJobEncerramento, MatchMode.START));
		criteria.add(Restrictions.in(AghJobDetail.Fields.IND_SITUACAO.toString(), indSituacao));
		return criteria;
	}

	public List<AghJobDetail> pesquisarAghJobDetailPaginator(Map<Object, Object> filtersMap, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghJobDetail.class);
		
		criteria.createAlias(AghJobDetail.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica", JoinType.LEFT_OUTER_JOIN);
		
		montarCriteria(criteria, filtersMap);

		return executeCriteria(criteria, firstResult, maxResult, AghJobDetail.Fields.SEQ.toString(), false);
	}

	private void montarCriteria(DetachedCriteria criteria, Map<Object, Object> filtersMap) {
		final Integer numero = (Integer) filtersMap.get("numero");
		final String nomeProcesso = (String) filtersMap.get("nomeProcesso");
		final Date dataInicial = (Date) filtersMap.get("dataInicial");
		final Date dataFinal = (Date) filtersMap.get("dataFinal");
		final DominioSituacaoJobDetail situacao = (DominioSituacaoJobDetail) filtersMap.get("situacao");

		if (numero != null) {
			criteria.add(Restrictions.eq(AghJobDetail.Fields.SEQ.toString(), numero));
		}

		if (StringUtils.isNotBlank(nomeProcesso)) {
			criteria.add(Restrictions.ilike(AghJobDetail.Fields.NOME_PROCESSO.toString(), nomeProcesso, MatchMode.ANYWHERE));
		}

		if (dataInicial != null && dataFinal != null) {
			criteria.add(Restrictions.between(AghJobDetail.Fields.AGENDADO_EM.toString(), dataInicial, dataFinal));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(AghJobDetail.Fields.IND_SITUACAO.toString(), situacao));
		}
	}

	public AghJobDetail obterAghJobDetailPorId(Integer seq) {
		if (seq == null || seq.intValue() <= 0) {
			throw new IllegalArgumentException("parametro obritatorio nao informado!!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghJobDetail.class);
		
		criteria.createAlias(AghJobDetail.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica", JoinType.LEFT_OUTER_JOIN);		
		
		criteria.add(Restrictions.eq(AghJobDetail.Fields.SEQ.toString(), seq));
		
		return (AghJobDetail) executeCriteriaUniqueResult(criteria);
	}

}
