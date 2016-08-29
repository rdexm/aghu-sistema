package br.gov.mec.aghu.faturamento.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.RapServidores;

public class FatAihDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatAih> {

	private static final long serialVersionUID = -4747850052646106921L;

	public FatAih buscarMinAih(DominioSituacaoAih situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAih.class);
		criteria.add(Restrictions.eq(FatAih.Fields.IND_SITUACAO.toString(), situacao));
		criteria.setProjection(Projections.min(FatAih.Fields.NRO_AIH.toString()));
		Long nrAih = (Long) this.executeCriteriaUniqueResult(criteria);
		if (nrAih != null) {
			return obterPorChavePrimaria(nrAih);
		}
		return null;
	}

	public Long pesquisarAihsLiberadasCount(final Long nroAih, final Integer cthSeq, final List<DominioSituacaoAih> listaDominioSituacaoAih, final Date dtEmissao,
			final Short serVinCodigo, final Integer serMatricula) {
		return executeCriteriaCount(obterCriteriaPorPropriedades(nroAih, cthSeq, listaDominioSituacaoAih, dtEmissao, serVinCodigo, serMatricula));
	}

	public List<FatAih> pesquisarAihsLiberadas(final Integer firstResult, final Integer maxResult, final String order, final boolean asc, final Long nroAih,
			final Integer cthSeq, final List<DominioSituacaoAih> listaDominioSituacaoAih, final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula) {
		DetachedCriteria criteria = obterCriteriaPorPropriedades(nroAih, cthSeq, listaDominioSituacaoAih, dtEmissao, serVinCodigo, serMatricula);
		criteria.addOrder(Order.desc(FatAih.Fields.NRO_AIH.toString()));
		return executeCriteria(criteria, firstResult, maxResult, order, asc);
	}

	public Long pesquisarAihsCount(final Long nroAih, final Integer cthSeq, final List<DominioSituacaoAih> listaDominioSituacaoAih, final Date dtEmissao,
			final Short serVinCodigo, final Integer serMatricula, final Date dtCriadoEm) {
		return executeCriteriaCount(obterCriteriaAihs(nroAih, cthSeq, listaDominioSituacaoAih, dtEmissao, serVinCodigo, serMatricula, dtCriadoEm));
	}

	public List<FatAih> pesquisarAihs(final Integer firstResult, final Integer maxResult, final String order, final boolean asc, final Long nroAih,
			final Integer cthSeq, final List<DominioSituacaoAih> listaDominioSituacaoAih, final Date dtEmissao, final Short serVinCodigo,
			final Integer serMatricula, final Date dtCriadoEm) {
		DetachedCriteria criteria = obterCriteriaAihs(nroAih, cthSeq, listaDominioSituacaoAih, dtEmissao, serVinCodigo, serMatricula, dtCriadoEm);
		criteria.addOrder(Order.desc(FatAih.Fields.NRO_AIH.toString()));
		return executeCriteria(criteria, firstResult, maxResult, order, asc);
	}

	private DetachedCriteria obterCriteriaAihs(final Long nroAih, final Integer cthSeq, final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo, final Integer serMatricula, final Date dtCriadoEm) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAih.class);
		
		criteria.createAlias(FatAih.Fields.SERVIDOR.toString(), FatAih.Fields.SERVIDOR.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(FatAih.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString(), JoinType.INNER_JOIN);
		
		if (nroAih != null) {
			criteria.add(Restrictions.eq(FatAih.Fields.NRO_AIH.toString(), nroAih));
		}
		if (cthSeq != null) {
			criteria.add(Restrictions.eq(FatAih.Fields.SEQ_CTH.toString(), cthSeq));
		}
		if (listaDominioSituacaoAih != null && !listaDominioSituacaoAih.isEmpty()) {
			criteria.add(Restrictions.in(FatAih.Fields.IND_SITUACAO.toString(), listaDominioSituacaoAih));
		}
		if (dtEmissao != null) {

			criteria.add(Restrictions.between(FatAih.Fields.DT_EMISSAO.toString(), dtInicial(dtEmissao), dtFinal(dtEmissao)));
			// criteria.add(Restrictions.eq(FatAih.Fields.DT_EMISSAO.toString(),dtEmissao
			// ));
		}
		if (serVinCodigo != null) {
			criteria.add(Restrictions.eq(FatAih.Fields.VINCULO.toString(), serVinCodigo));
		}
		if (serMatricula != null) {
			criteria.add(Restrictions.eq(FatAih.Fields.MATRICULA.toString(), serMatricula));
		}
		if (dtCriadoEm != null) {
			criteria.add(Restrictions.between(FatAih.Fields.CRIADO_EM.toString(), dtInicial(dtCriadoEm), dtFinal(dtCriadoEm)));
		}
		return criteria;
	}

	private Date dtInicial(Date dt) {
		Calendar inicio = Calendar.getInstance();
		inicio.setTime(dt);
		inicio.set(Calendar.HOUR, 0);
		inicio.set(Calendar.MINUTE, 0);
		inicio.set(Calendar.SECOND, 0);
		inicio.set(Calendar.MILLISECOND, 0);
		return inicio.getTime();
	}

	private Date dtFinal(Date dt) {
		Calendar fim = Calendar.getInstance();
		fim.setTime(dt);
		fim.set(Calendar.HOUR, 23);
		fim.set(Calendar.MINUTE, 59);
		fim.set(Calendar.SECOND, 59);
		fim.set(Calendar.MILLISECOND, 999);
		return fim.getTime();
	}

	private DetachedCriteria obterCriteriaPorPropriedades(final Long nroAih, final Integer cthSeq, final List<DominioSituacaoAih> listaDominioSituacaoAih,
			final Date dtEmissao, final Short serVinCodigo, final Integer serMatricula) {
		DetachedCriteria criteria = obterCriteriaAihs(nroAih, cthSeq, listaDominioSituacaoAih, dtEmissao, serVinCodigo, serMatricula, null);
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatContasHospitalares.class, "CTH2");
		subCriteria.setProjection(Projections.property(FatContasHospitalares.Fields.AIH.toString()));
		subCriteria.add(Restrictions.eqProperty(subCriteria.getAlias() + "." + FatContasHospitalares.Fields.AIH.toString(), criteria.getAlias() + "."
				+ FatAih.Fields.NRO_AIH.toString()));
		criteria.add(Subqueries.notExists(subCriteria));
		return criteria;
	}

	public List<FatAih> obterFatAihsParaReaproveitamentoEstorno(final Long nroAih) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAih.class);
		criteria.add(Restrictions.eq(FatAih.Fields.NRO_AIH.toString(), nroAih));

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		subCriteria.setProjection(Projections.property(FatContasHospitalares.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(FatContasHospitalares.Fields.NRO_AIH.toString(), nroAih));

		// ETB 24/03/04, não bloqueia ou libera aih após uma conta já ter sido
		// rejeitada
		subCriteria.add(Restrictions.ne(FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.R));
		criteria.add(Subqueries.notExists(subCriteria));

		return executeCriteria(criteria);

	}

	private DetachedCriteria obterCriteriaIntervaloAihs(Long nroAihInicial, Long nroAihFinal) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAih.class);
		criteria.add(Restrictions.ge(FatAih.Fields.NRO_AIH.toString(), nroAihInicial));
		criteria.add(Restrictions.le(FatAih.Fields.NRO_AIH.toString(), nroAihFinal));
		return criteria;
	}

	public List<FatAih> pesquisarAihsIntervalo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Long nroAihInicial, Long nroAihFinal) {
		final DetachedCriteria criteria = obterCriteriaIntervaloAihs(nroAihInicial, nroAihFinal);
		criteria.addOrder(Order.desc(FatAih.Fields.NRO_AIH.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarAihsIntervaloCount(Long nroAihInicial, Long nroAihFinal) {
		return executeCriteriaCount(obterCriteriaIntervaloAihs(nroAihInicial, nroAihFinal));
	}
}
