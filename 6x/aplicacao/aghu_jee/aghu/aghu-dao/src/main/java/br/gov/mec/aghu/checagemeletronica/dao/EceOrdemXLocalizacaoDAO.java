package br.gov.mec.aghu.checagemeletronica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoOrdemDeAdministracao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.EceOrdemDeAdministracao;
import br.gov.mec.aghu.model.EceOrdemXLocalizacao;

public class EceOrdemXLocalizacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EceOrdemXLocalizacao> {

	private static final long serialVersionUID = -7929199705764893491L;

	public boolean existeLocalizacao1(final Integer newSeq, final Date truncaData, final Short newUnfSeq) {
		return executeCriteriaCount(createCriteriaLocalizacao1(newSeq, truncaData, newUnfSeq)) > 0;
	}

	protected DetachedCriteria createCriteriaLocalizacao1(final Integer newSeq, final Date truncaData, final Short newUnfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EceOrdemXLocalizacao.class);
		criteria.createAlias(EceOrdemXLocalizacao.Fields.ORDEM_ADMINISTRACAO.toString(), "oda");
		criteria.add(Restrictions.eq("oda." + EceOrdemDeAdministracao.Fields.PME_ATD_SEQ.toString(), newSeq));
		criteria.add(Restrictions.ge("oda." + EceOrdemDeAdministracao.Fields.DATA_REFERENCIA.toString(), truncaData));

		criteria.add(Restrictions.eq(EceOrdemXLocalizacao.Fields.UNF_SEQ.toString(), newUnfSeq));
		criteria.add(Restrictions.eq(EceOrdemXLocalizacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public boolean existeLocalizacao2(Integer newSeq, Date truncaData, Short newUnfSeq) {
		return executeCriteriaCount(createCriteriaLocalizacao2(newSeq, truncaData, newUnfSeq)) > 0;
	}

	protected DetachedCriteria createCriteriaLocalizacao2(final Integer newSeq, final Date truncaData, final Short newUnfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EceOrdemXLocalizacao.class);
		criteria.createAlias(EceOrdemXLocalizacao.Fields.ORDEM_ADMINISTRACAO.toString(), "oda");
		criteria.add(Restrictions.eq("oda." + EceOrdemDeAdministracao.Fields.PEN_ATD_SEQ.toString(), newSeq));

		criteria.add(Restrictions.eq(EceOrdemXLocalizacao.Fields.UNF_SEQ.toString(), newUnfSeq));
		criteria.add(Restrictions.eq(EceOrdemXLocalizacao.Fields.SITUACAO.toString(), DominioSituacao.A));

		return criteria;
	}

	public boolean existeLocalizacao3(final Integer newSeq, final Date truncaData, final Short newUnfSeq) {
		Object result = createQueryLocalizacao3(newSeq, truncaData, newUnfSeq, "count(*)").getSingleResult();
		if (result == null) {
			return false;
		}
		return ((Long) result) > 0;
	}

	protected Query createQueryLocalizacao3(final Integer newSeq, final Date truncaData, final Short newUnfSeq, final String projecao) {
		/*
		 * FROM ece_ordem_de_administracoes oda, agh_atendimentos atd_quimio,
		 * agh_atendimentos atd , ece_ordem_x_localizacao oxl WHERE atd.seq =
		 * c_atd_seq AND atd_quimio.pac_codigo = atd.pac_codigo AND
		 * atd_quimio.tpt_seq+0 IN (6,4,19) -- quimio ou hemodialise AND
		 * oda.pte_atd_seq = atd_quimio.seq AND oda.situacao <> 'O' AND
		 * oda.data_referencia+0 >= c_dt_referencia AND oxl.unf_seq = c_unf_seq
		 * AND oxl.situacao = 'A' AND oxl.oda_seq = oda.seq ;
		 */
		
		StringBuffer hql = new StringBuffer("select ")
				.append(projecao)
				.append(" from ")
				.append(EceOrdemXLocalizacao.class.getName())
				.append(" as oxl,")
				.append(EceOrdemDeAdministracao.class.getName())
				.append(" as oda,")
				.append(AghAtendimentos.class.getName())
				.append(" as atd,")
				.append(AghAtendimentos.class.getName())
				.append(" as atd_quimio")
				
				.append(" where atd.")
				.append(AghAtendimentos.Fields.SEQ.toString())
				.append(" = :atdSeq")
				
				.append(" and atd_quimio.")
				.append(AghAtendimentos.Fields.PAC_CODIGO.toString())
				.append(" = atd.")
				.append(AghAtendimentos.Fields.PAC_CODIGO.toString())
				
				.append(" and atd_quimio.")
				.append(AghAtendimentos.Fields.TIPO_TRATAMENTO.toString())
				.append(" in (6,4,19)")  // TODO rubens.souza: parametrizar
				
				.append(" and oda.")
				.append(EceOrdemDeAdministracao.Fields.PTE_ATD_SEQ.toString())
				.append(" = atd_quimio.seq")
				
				.append(" and oda.")
				.append(EceOrdemDeAdministracao.Fields.SITUACAO.toString())
				.append(" <> '" + DominioSituacaoOrdemDeAdministracao.O.toString() + "'")
				
				.append(" and oda.")
				.append(EceOrdemDeAdministracao.Fields.DATA_REFERENCIA
						.toString()).append(" >= :dtReferencia ")

				.append(" and oxl.")
				.append(EceOrdemXLocalizacao.Fields.UNF_SEQ.toString())
				.append(" = :unfSeq")

				.append(" and oxl.")
				.append(EceOrdemXLocalizacao.Fields.SITUACAO.toString())
				.append(" = :situacao")
				
				.append(" and oxl.")
				.append(EceOrdemXLocalizacao.Fields.ORDEM_ADMINISTRACAO_ID.toString())
				.append(" = oda.seq");
		

		Query query = createQuery(hql.toString());

		query.setParameter("atdSeq", newSeq);
		query.setParameter("dtReferencia", truncaData);
		query.setParameter("unfSeq", newUnfSeq);
		query.setParameter("situacao", DominioSituacao.A);
		return query;
	}

	public List<EceOrdemXLocalizacao> buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao(Integer newSeq, Date dataReferencia, Short newUnfSeq) {
		final List<EceOrdemXLocalizacao> result = new ArrayList<EceOrdemXLocalizacao>(0);
		
		List<EceOrdemXLocalizacao> resultPar = this.buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao1(newSeq, dataReferencia, newUnfSeq);
		if(resultPar != null && !resultPar.isEmpty()){
			result.addAll(resultPar);
		}
		resultPar =  this.buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao2(newSeq, dataReferencia, newUnfSeq);
		if(resultPar != null && !resultPar.isEmpty()){
			result.addAll(resultPar);
		}
		resultPar =  this.buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao3(newSeq, dataReferencia, newUnfSeq);
		if(resultPar != null && !resultPar.isEmpty()){
			result.addAll(resultPar);
		}
		
		return result;
	}

	private List<EceOrdemXLocalizacao> buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao3(final Integer newSeq, final Date dataReferencia, final Short newUnfSeq) {
		DetachedCriteria criteria = this.createCriteriaLocalizacao1(newSeq, dataReferencia, newUnfSeq);
		return executeCriteria(criteria);
	}

	private List<EceOrdemXLocalizacao> buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao2(final Integer newSeq, final Date dataReferencia, final Short newUnfSeq) {
		DetachedCriteria criteria = this.createCriteriaLocalizacao2(newSeq, dataReferencia, newUnfSeq);
		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	private List<EceOrdemXLocalizacao> buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao1(final Integer newSeq, final Date dataReferencia, final Short newUnfSeq) {
		final Query query = this.createQueryLocalizacao3(newSeq, dataReferencia, newUnfSeq, " oxl ");
		return query.getResultList();
	}

	public List<EceOrdemXLocalizacao> buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao(Integer odaSeq, Short unfSeq, Short numeroQuarto, String leitoID) {
		return executeCriteria(createCriteriaOrdemLocalizacao(odaSeq, unfSeq, numeroQuarto, leitoID));
	}

	protected DetachedCriteria createCriteriaOrdemLocalizacao(final Integer odaSeq, final Short unfSeq, final Short numeroQuarto, final String leitoID) {
		/*
        WHERE oda_seq    =  r.oda_seq
        AND unf_seq    =  r.unf_seq
        AND NVL(qrt_numero,1) =  NVL(r.qrt_numero,1)
        AND NVL(lto_lto_id,'1') =  NVL(r.lto_lto_id,'1');
		 */
		DetachedCriteria criteria = DetachedCriteria.forClass(EceOrdemXLocalizacao.class);
		criteria.add(Restrictions.eq(EceOrdemXLocalizacao.Fields.ORDEM_ADMINISTRACAO_ID.toString(), odaSeq));
		criteria.add(Restrictions.eq(EceOrdemXLocalizacao.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.sqlRestriction(" CASE WHEN {alias}.qrt_numero is null THEN 1 ELSE {alias}.qrt_numero end = ?", numeroQuarto == null ? (short)1
				: numeroQuarto, ShortType.INSTANCE));
		criteria.add(Restrictions.sqlRestriction(" CASE WHEN {alias}.lto_lto_id is null THEN '1' ELSE {alias}.lto_lto_id end = ? ",
				StringUtils.isEmpty(leitoID) ? "1" : leitoID, StringType.INSTANCE));

		return criteria;
	}

}
