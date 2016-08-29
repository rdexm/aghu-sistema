package br.gov.mec.aghu.checagemeletronica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoOrdemDeAdministracao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.EceOrdemDeAdministracao;

public class EceOrdemDeAdministracaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EceOrdemDeAdministracao> {

	private static final long serialVersionUID = 4576788511282156969L;

	public List<EceOrdemDeAdministracao> buscarEceOrdemDeAdministracaoInserirOrdemLocalizacao(final Integer newSeq, final Date dataReferencia) {
		final List<EceOrdemDeAdministracao> result = new ArrayList<EceOrdemDeAdministracao>(0);

		List<EceOrdemDeAdministracao> resultPar = this.buscarEceOrdemDeAdministracaoInserirOrdemLocalizacao1(newSeq, dataReferencia);
		if (resultPar != null && !resultPar.isEmpty()) {
			result.addAll(resultPar);
		}
		resultPar = this.buscarEceOrdemDeAdministracaoInserirOrdemLocalizacao2(newSeq, dataReferencia);
		if (resultPar != null && !resultPar.isEmpty()) {
			result.addAll(resultPar);
		}
		resultPar = this.buscarEceOrdemDeAdministracaoInserirOrdemLocalizacao3(newSeq, dataReferencia);
		if (resultPar != null && !resultPar.isEmpty()) {
			result.addAll(resultPar);
		}

		return result;
	}

	private List<EceOrdemDeAdministracao> buscarEceOrdemDeAdministracaoInserirOrdemLocalizacao1(final Integer newSeq, final Date dataReferencia) {
		/*
	SELECT oda.seq   oda_seq
	  FROM ece_ordem_de_administracoes oda
	 WHERE oda.pme_atd_seq       = c_atd_seq
	   AND oda.data_referencia >= c_dt_referencia
		 */
		DetachedCriteria criteria = DetachedCriteria.forClass(EceOrdemDeAdministracao.class);
		criteria.add(Restrictions.eq(EceOrdemDeAdministracao.Fields.PME_ATD_SEQ.toString(), newSeq));
		criteria.add(Restrictions.ge(EceOrdemDeAdministracao.Fields.DATA_REFERENCIA.toString(), dataReferencia));
		return executeCriteria(criteria);
	}
	
	private List<EceOrdemDeAdministracao> buscarEceOrdemDeAdministracaoInserirOrdemLocalizacao2(final Integer newSeq, final Date dataReferencia) {
		/*
 SELECT oda.seq   oda_seq
  FROM ece_ordem_de_administracoes oda
 WHERE oda.pen_atd_seq       = c_atd_seq
   AND oda.data_referencia >= c_dt_referencia
		 */
		DetachedCriteria criteria = DetachedCriteria.forClass(EceOrdemDeAdministracao.class);
		criteria.add(Restrictions.eq(EceOrdemDeAdministracao.Fields.PEN_ATD_SEQ.toString(), newSeq));
		criteria.add(Restrictions.ge(EceOrdemDeAdministracao.Fields.DATA_REFERENCIA.toString(), dataReferencia));
		return executeCriteria(criteria);
	}
	
	
	@SuppressWarnings("unchecked")
	private List<EceOrdemDeAdministracao> buscarEceOrdemDeAdministracaoInserirOrdemLocalizacao3(final Integer newSeq, final Date dataReferencia) {
		/*
 SELECT oda.seq  oda_seq
  FROM ece_ordem_de_administracoes oda,
       agh_atendimentos atd_quimio,
       agh_atendimentos atd
  WHERE atd.seq                =  c_atd_seq
   AND atd_quimio.pac_codigo   =  atd.pac_codigo
   AND atd_quimio.tpt_seq+0    IN (6,4,19) -- quimio ou hemodialise
   AND oda.pte_atd_seq         =  atd_quimio.seq
   AND oda.situacao            <> 'O'
   AND oda.data_referencia+0   >= c_dt_referencia;
		 */
		StringBuffer hql = new StringBuffer("select oda")
				.append(" from ")
				.append(EceOrdemDeAdministracao.class.getName())
				.append(" as oda, ")
				.append(AghAtendimentos.class.getName())
				.append(" as atd_quimio,")
				.append(AghAtendimentos.class.getName())
				.append(" as atd")
				
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
				.append(" <> :situacao")
				
				.append(" and oda.")
				.append(EceOrdemDeAdministracao.Fields.DATA_REFERENCIA
						.toString()).append(" >= :dtReferencia ");

		Query query = createQuery(hql.toString());

		query.setParameter("atdSeq", newSeq);
		query.setParameter("dtReferencia", dataReferencia);
		query.setParameter("situacao", DominioSituacaoOrdemDeAdministracao.O);

		return query.getResultList();
	}
	
	public List<EceOrdemDeAdministracao> buscarOrdemAdmin(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(EceOrdemDeAdministracao.class);
		criteria.add(Restrictions.eq(EceOrdemDeAdministracao.Fields.PME_ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(EceOrdemDeAdministracao.Fields.DATA_REFERENCIA.toString()));
		return executeCriteria(criteria);
	}
}
