package br.gov.mec.aghu.internacao.dao;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.VMciMvtoPacsVO;
import br.gov.mec.aghu.dominio.DominioFormaContabilizacao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosAtendUrgencia;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposMvtoInternacao;
import br.gov.mec.aghu.model.MciLocalEtiologia;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AinMovimentosAtendUrgenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinMovimentosAtendUrgencia> {

	private static final long serialVersionUID = -4606780612646877106L;

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion9(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status,
			Integer numeroDiasCenso, Integer codigoLeitoBloqueioLimpeza) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer(850);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	atd.dtAtendimento,  esp.sigla, tam.codigo, mv.dthrLancamento,");
		hql.append(" 	atd.seq, mv.id.criadoEm, pac.dtNascimento, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AinMovimentosAtendUrgencia mv");
		hql.append(" join mv.atendimentoUrgencia as atd ");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" left join atd.especialidade as esp ");
		hql.append(" join atd.paciente as pac ");
		hql.append(" join mv.tipoMovimentoInternacao as tmv ");
		hql.append(" left join atd.tipoAltaMedica as tam ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.quarto as qrt");
		hql.append(" left join mv.leito as lto");

		hql.append(" where tmv.codigo <> :tipoMovInt ");
		hql.append(" and mv.dthrLancamento > :dthrLancamento ");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Calendar dataAtualMenos32 = Calendar.getInstance();
		dataAtualMenos32.add(Calendar.DATE, -numeroDiasCenso);
		dataAtualMenos32.set(Calendar.HOUR_OF_DAY, 0);
		dataAtualMenos32.set(Calendar.MINUTE, 0);
		dataAtualMenos32.set(Calendar.SECOND, 0);
		dataAtualMenos32.set(Calendar.MILLISECOND, 0);

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrLancamento", dataAtualMenos32.getTime());

		query.setParameter("tipoMovInt", codigoLeitoBloqueioLimpeza);
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion10(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status,
			Integer numeroDiasCenso) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer(900);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	atd.dtAtendimento,  esp.sigla, tam.codigo, mv.dthrLancamento,");
		hql.append(" 	atd.seq, mv.id.criadoEm, pac.dtNascimento, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AinMovimentosAtendUrgencia mv");
		hql.append(" join mv.atendimentoUrgencia as atd ");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" left join atd.especialidade as esp ");
		hql.append(" join atd.paciente as pac ");
		hql.append(" join mv.tipoMovimentoInternacao as tmv ");
		hql.append(" left join atd.tipoAltaMedica as tam ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.quarto as qrt");
		hql.append(" left join mv.leito as lto");

		hql.append(" where tmv.codigo = :tipoMovInt ");
		hql.append(" and mv.dthrLancamento > :dthrLancamento ");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Calendar dataAtualMenos32 = Calendar.getInstance();
		dataAtualMenos32.add(Calendar.DATE, -numeroDiasCenso);
		dataAtualMenos32.set(Calendar.HOUR_OF_DAY, 0);
		dataAtualMenos32.set(Calendar.MINUTE, 0);
		dataAtualMenos32.set(Calendar.SECOND, 0);
		dataAtualMenos32.set(Calendar.MILLISECOND, 0);

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrLancamento", dataAtualMenos32.getTime());
		query.setParameter("tipoMovInt", Integer.valueOf("5"));
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion11(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status,
			Integer numeroDiasCenso, Integer codigoLeitoBloqueioLimpeza) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer(900);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	atd.dtAtendimento,  esp.sigla, tam.codigo, mv.dthrLancamento,");
		hql.append(" 	atd.seq, mv.id.criadoEm, pac.dtNascimento, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AinMovimentosAtendUrgencia mv");
		hql.append(" join mv.atendimentoUrgencia as atd ");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" left join atd.especialidade as esp ");
		hql.append(" join atd.paciente as pac ");
		hql.append(" join mv.tipoMovimentoInternacao as tmv ");
		hql.append(" left join atd.tipoAltaMedica as tam ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.quarto as qrt");
		hql.append(" left join mv.leito as lto");

		hql.append(" where tmv.codigo = :tipoMovInt ");
		hql.append(" and mv.dthrLancamento > :dthrLancamento ");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Calendar dataAtualMenos32 = Calendar.getInstance();
		dataAtualMenos32.add(Calendar.DATE, -numeroDiasCenso);
		dataAtualMenos32.set(Calendar.HOUR_OF_DAY, 0);
		dataAtualMenos32.set(Calendar.MINUTE, 0);
		dataAtualMenos32.set(Calendar.SECOND, 0);
		dataAtualMenos32.set(Calendar.MILLISECOND, 0);

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrLancamento", dataAtualMenos32.getTime());

		query.setParameter("tipoMovInt", codigoLeitoBloqueioLimpeza);
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion12(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status,
			Integer numeroDiasCenso) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer(1200);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	atd.dtAtendimento,  esp.sigla, tam.codigo, mv.dthrLancamento,");
		hql.append(" 	atd.seq, mv.id.criadoEm, pac.dtNascimento, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AinMovimentosAtendUrgencia mv");
		hql.append(" join mv.atendimentoUrgencia as atd ");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" left join atd.especialidade as esp ");
		hql.append(" join atd.paciente as pac ");
		hql.append(" join mv.tipoMovimentoInternacao as tmv ");
		hql.append(" left join atd.tipoAltaMedica as tam ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.quarto as qrt");
		hql.append(" left join mv.leito as lto");

		hql.append(" where tmv.codigo <> :tipoMovInt ");
		hql.append(" and mv.dthrLancamento > :dthrLancamento ");

		hql.append(" and unf.seq <> (");
		hql.append(" select a.unidadeFuncional.seq");
		hql.append(" from AinMovimentosAtendUrgencia a ");
		hql.append(" join a.unidadeFuncional as b ");
		hql.append(" where a.dthrLancamento = (");
		hql.append(" select max(a1.dthrLancamento) ");
		hql.append(" from AinMovimentosAtendUrgencia a1 ");
		hql.append(" where a1.id.seqAtendimentoUrgencia = atd.seq");
		hql.append(" and a1.dthrLancamento < mv.dthrLancamento)");
		hql.append(" and a.id.seqAtendimentoUrgencia = atd.seq");
		hql.append(')');

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Calendar dataAtualMenos32 = Calendar.getInstance();
		dataAtualMenos32.add(Calendar.DATE, -numeroDiasCenso);
		dataAtualMenos32.set(Calendar.HOUR_OF_DAY, 0);
		dataAtualMenos32.set(Calendar.MINUTE, 0);
		dataAtualMenos32.set(Calendar.SECOND, 0);
		dataAtualMenos32.set(Calendar.MILLISECOND, 0);

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrLancamento", dataAtualMenos32.getTime());
		query.setParameter("tipoMovInt", Integer.valueOf("21"));
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion13(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status,
			Integer numeroDiasCenso, Integer codigoLeitoBloqueioLimpeza) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer(1200);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	atd.dtAtendimento,  esp.sigla, tam.codigo, mv.dthrLancamento,");
		hql.append(" 	atd.seq, mv.id.criadoEm, pac.dtNascimento, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AinMovimentosAtendUrgencia mv");
		hql.append(" join mv.atendimentoUrgencia as atd ");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" left join atd.especialidade as esp ");
		hql.append(" join atd.paciente as pac ");
		hql.append(" join mv.tipoMovimentoInternacao as tmv ");
		hql.append(" left join atd.tipoAltaMedica as tam ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.quarto as qrt");
		hql.append(" left join mv.leito as lto");

		hql.append(" where tmv.codigo <> :tipoMovInt ");
		hql.append(" and mv.dthrLancamento > :dthrLancamento ");

		hql.append(" and unf.seq <> (");
		hql.append(" select a.unidadeFuncional.seq");
		hql.append(" from AinMovimentosAtendUrgencia a ");
		hql.append(" join a.unidadeFuncional as b ");
		hql.append(" where a.dthrLancamento = (");
		hql.append(" select min(a1.dthrLancamento) ");
		hql.append(" from AinMovimentosAtendUrgencia a1 ");
		hql.append(" where a1.id.seqAtendimentoUrgencia = atd.seq");
		hql.append(" and a1.dthrLancamento > mv.dthrLancamento)");
		hql.append(" and a.id.seqAtendimentoUrgencia = atd.seq");
		hql.append(')');

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Calendar dataAtualMenos32 = Calendar.getInstance();
		dataAtualMenos32.add(Calendar.DATE, -numeroDiasCenso);
		dataAtualMenos32.set(Calendar.HOUR_OF_DAY, 0);
		dataAtualMenos32.set(Calendar.MINUTE, 0);
		dataAtualMenos32.set(Calendar.SECOND, 0);
		dataAtualMenos32.set(Calendar.MILLISECOND, 0);

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrLancamento", dataAtualMenos32.getTime());

		query.setParameter("tipoMovInt", codigoLeitoBloqueioLimpeza);
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	public AinMovimentosAtendUrgencia obterAinMovimentosAtendUrgenciaSeqAteDtHrLancamento(Integer atendimentoUrgSeq, Date dataHora) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		criteria.createAlias(AinMovimentosAtendUrgencia.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		DetachedCriteria subquery = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		subquery.setProjection(Projections.max(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));
		subquery.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA_SEQ.toString(), atendimentoUrgSeq));
		subquery.add(Restrictions.lt(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));

		criteria.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA_SEQ.toString(), atendimentoUrgSeq));
		criteria.add(Subqueries.propertyEq(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), subquery));
		return (AinMovimentosAtendUrgencia) executeCriteriaUniqueResult(criteria);
	}

	public AinMovimentosAtendUrgencia obterAinMovimentosAtendUrgenciaSeqPosDtHrLancamento(Integer atendimentoUrgenciaSeq, Date dataHora) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		DetachedCriteria subquery = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		subquery.setProjection(Projections.min(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));
		subquery.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA_SEQ.toString(), atendimentoUrgenciaSeq));
		subquery.add(Restrictions.gt(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));

		criteria.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA_SEQ.toString(), atendimentoUrgenciaSeq));
		criteria.add(Subqueries.propertyEq(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), subquery));

		return (AinMovimentosAtendUrgencia) executeCriteriaUniqueResult(criteria);
	}

	public Date obtemDthrFinalUrgencia(Integer atendimentoUrgenciaSeq, Date dataHora) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		criteria.createAlias(AinMovimentosAtendUrgencia.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		DetachedCriteria subquery = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		subquery.setProjection(Projections.min(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));
		subquery.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA_SEQ.toString(), atendimentoUrgenciaSeq));
		subquery.add(Restrictions.gt(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));

		criteria.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA_SEQ.toString(), atendimentoUrgenciaSeq));
		criteria.add(Subqueries.propertyEq(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), subquery));

		criteria.setProjection(Projections.property(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));

		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public Date obtemDthrFinalNova(Integer atendimentoUrgenciaSeq, Date dataHora, Integer movimentoSeq, Date criadoEm) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		criteria.createAlias(AinMovimentosAtendUrgencia.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		criteria.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA_SEQ.toString(), atendimentoUrgenciaSeq));
		criteria.add(Restrictions.ge(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));

		criteria.add(Restrictions.or(
				Restrictions.ne(AinMovimentosAtendUrgencia.Fields.ID_SEQ_ATENDIMENTO_URGENCIA.toString(), movimentoSeq),
				Restrictions.ne(AinMovimentosAtendUrgencia.Fields.CRIADO_EM.toString(), criadoEm)));

		criteria.addOrder(Order.asc(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));

		criteria.setProjection(Projections.property(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));

		List<Date> dates = executeCriteria(criteria, 0, 1, null, true);
		if (dates != null && !dates.isEmpty()) {
			return dates.get(0);
		}
		return null;
	}

	public List<Object[]> pesquisarOrigemPorAtendimentoUrgencia(Integer atendimentoUrgenciaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);

		criteria.setProjection(Projections.projectionList().add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString()))
				.add(Projections.property("QRT." + AinQuartos.Fields.NUMERO.toString()))
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())));

		criteria.createAlias(AinMovimentosAtendUrgencia.Fields.LEITO.toString(), "LTO", Criteria.LEFT_JOIN);
		criteria.createAlias(AinMovimentosAtendUrgencia.Fields.QUARTO.toString(), "QRT", Criteria.LEFT_JOIN);
		criteria.createAlias(AinMovimentosAtendUrgencia.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", Criteria.LEFT_JOIN);
		criteria.createAlias(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA.toString(), "ATU");

		criteria.add(Restrictions.eq("ATU." + AinAtendimentosUrgencia.Fields.SEQ.toString(), atendimentoUrgenciaSeq));
		criteria.addOrder(Order.desc(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));

		return executeCriteria(criteria);
	}

	public Date buscaDthrAtendimentoUrgencia(Integer atendimentoUrgenciaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		criteria.setProjection(Projections.max(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));
		criteria.createAlias(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA.toString(), "ATU");
		criteria.add(Restrictions.eq("ATU." + AinAtendimentosUrgencia.Fields.SEQ.toString(), atendimentoUrgenciaSeq));
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public List<VMciMvtoPacsVO> buscarMovimentoPaciente(Integer pacCodigo, Date dtInicio, String einTipo) {
		// CRITERIA 1
		DetachedCriteria criteria1 = DetachedCriteria.forClass(AinMovimentosInternacao.class, "MVI");
		criteria1.createAlias("MVI." + AinMovimentosInternacao.Fields.INTERNACAO.toString(), "INT", JoinType.INNER_JOIN);
		criteria1.createAlias("MVI." + AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO_OBJ.toString(), "TMI", JoinType.INNER_JOIN);
		criteria1.add(Restrictions.eq("TMI." + AinTiposMvtoInternacao.Fields.IND_CCIH_MVTO_LOCAL_REGRAS.toString(), Boolean.TRUE));
		criteria1.add(Restrictions.eq("INT." + AinInternacao.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria1.add(Restrictions.le("MVI." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dtInicio));
		
		//subQuery mciLocalEtiologia
		if (einTipo != null) {
			DetachedCriteria subQuery1 = DetachedCriteria.forClass(MciLocalEtiologia.class, "LET");
			subQuery1.setProjection(Projections.property("LET." + MciLocalEtiologia.Fields.UNF_SEQ.toString()));
			subQuery1.add(Restrictions.eqProperty("LET." + MciLocalEtiologia.Fields.UNF_SEQ.toString(), 
					"MVI." + AinMovimentosInternacao.Fields.UNF_SEQ.toString()));
			subQuery1.add(Restrictions.eq("LET." + MciLocalEtiologia.Fields.EIN_TIPO.toString(), einTipo));
			subQuery1.add(Restrictions.eq("LET." + MciLocalEtiologia.Fields.IND_FORMA_CONTABILIZACAO.toString(), DominioFormaContabilizacao.P));
			criteria1.add(Subqueries.exists(subQuery1));
		}

		criteria1.setProjection(Projections.projectionList()
				.add(Projections.property("MVI." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()), VMciMvtoPacsVO.Fields.DTHR_LANCAMENTO.toString())
				.add(Projections.property("MVI." + AinMovimentosInternacao.Fields.LTO_LTO_ID.toString()), VMciMvtoPacsVO.Fields.LEITO_ID.toString())
				.add(Projections.property("MVI." + AinMovimentosInternacao.Fields.QRT_NUMERO.toString()), VMciMvtoPacsVO.Fields.QUARTO_NUMERO.toString())
				.add(Projections.property("MVI." + AinMovimentosInternacao.Fields.UNF_SEQ.toString()), VMciMvtoPacsVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("INT." + AinInternacao.Fields.PAC_CODIGO.toString()), VMciMvtoPacsVO.Fields.PAC_CODIGO.toString()));
		
		criteria1.setResultTransformer(Transformers.aliasToBean(VMciMvtoPacsVO.class));
		
		List<VMciMvtoPacsVO> result1 = executeCriteria(criteria1);
		
		// CRITERIA 2
		DetachedCriteria criteria2 = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class, "MVA");
		criteria2.createAlias("MVA." + AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA.toString(), "ATU", JoinType.INNER_JOIN);
		criteria2.createAlias("MVA." + AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO_OBJ.toString(), "TMI", JoinType.INNER_JOIN);
		criteria2.add(Restrictions.eq("TMI." + AinTiposMvtoInternacao.Fields.IND_CCIH_MVTO_LOCAL_REGRAS.toString(), Boolean.TRUE));
		criteria2.add(Restrictions.eq("ATU." + AinAtendimentosUrgencia.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria2.add(Restrictions.le("MVA." + AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), dtInicio));
		
		//subQuery mciLocalEtiologia
		if (einTipo != null) {
			DetachedCriteria subQuery2 = DetachedCriteria.forClass(MciLocalEtiologia.class, "LET");
			subQuery2.setProjection(Projections.property("LET." + MciLocalEtiologia.Fields.UNF_SEQ.toString()));
			subQuery2.add(Restrictions.eqProperty("LET." + MciLocalEtiologia.Fields.UNF_SEQ.toString(), 
					"MVA." + AinMovimentosAtendUrgencia.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));
			subQuery2.add(Restrictions.eq("LET." + MciLocalEtiologia.Fields.EIN_TIPO.toString(), einTipo));
			subQuery2.add(Restrictions.eq("LET." + MciLocalEtiologia.Fields.IND_FORMA_CONTABILIZACAO.toString(), DominioFormaContabilizacao.P));
			criteria2.add(Subqueries.exists(subQuery2));
		}
		
		criteria2.setProjection(Projections.projectionList()
				.add(Projections.property("MVA." + AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()), VMciMvtoPacsVO.Fields.DTHR_LANCAMENTO.toString())
				.add(Projections.property("MVA." + AinMovimentosAtendUrgencia.Fields.LEITO_ID.toString()), VMciMvtoPacsVO.Fields.LEITO_ID.toString())
				.add(Projections.property("MVA." + AinMovimentosAtendUrgencia.Fields.QUARTO_NUMERO.toString()), VMciMvtoPacsVO.Fields.QUARTO_NUMERO.toString())
				.add(Projections.property("MVA." + AinMovimentosAtendUrgencia.Fields.UNIDADE_FUNCIONAL_SEQ.toString()), VMciMvtoPacsVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("ATU." + AinAtendimentosUrgencia.Fields.PACIENTE_CODIGO.toString()), VMciMvtoPacsVO.Fields.PAC_CODIGO.toString()));
		
		criteria2.setResultTransformer(Transformers.aliasToBean(VMciMvtoPacsVO.class));
		
		List<VMciMvtoPacsVO> result2 = executeCriteria(criteria2);
				
		// UNION
		result1.addAll(result2);
		
		Collections.sort(result1, Collections.reverseOrder(new Comparator<VMciMvtoPacsVO>() {
			@Override
			public int compare(VMciMvtoPacsVO arg0, VMciMvtoPacsVO arg1) {
				return arg0.getDthrLancamento().compareTo(arg1.getDthrLancamento());
			}
		}));
		
		return result1;
	}

	public List<AinMovimentosAtendUrgencia> pesquisarMovimentosAtendimentosUrgencia(Integer seq, Integer codigoMovimentacao, Date dtAltaAtendimentoAntiga) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		criteria.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ID_SEQ_ATENDIMENTO_URGENCIA.toString(), seq));
		criteria.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.TIPO_MOVIMENTO_INTERNACAO_CODIGO.toString(), codigoMovimentacao));
		criteria.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString(), dtAltaAtendimentoAntiga));
		
		return executeCriteria(criteria);
	}
}
