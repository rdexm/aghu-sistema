package br.gov.mec.aghu.internacao.dao;

import static org.hibernate.criterion.Projections.projectionList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 * 
 */

public class AinExtratoLeitosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinExtratoLeitos> {

	private static final long serialVersionUID = 3630225227551001864L;
	private static final String SEPARADOR = ".";
	private final static String ALIAS_TIPO_MOVIMENTO_LEITO = "tipoMovimentoLeito";
	private final static String ALIAS_LEITO = "leito";
	private static final int UM_DIA = 1;
	
	/**
	 * Método para buscar todos os registros referentes a segunda query do
	 * cursor "l_desativados"
	 * 
	 * @return
	 */
	public List<AinExtratoLeitos> pesquisarExtratosLeito(Date mesCompetencia) {
		final String aliasLeito = "leito";
		final String aliasTipoMovimentoLeito = "tipoMovimentoLeito";

		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);

		criteria.createAlias(AinExtratoLeitos.Fields.LEITO.toString(), aliasLeito);
		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(),
				aliasTipoMovimentoLeito);

		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO.toString() + SEPARADOR
				+ AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString()
				+ SEPARADOR + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				DominioMovimentoLeito.D));

		// @alterado
		// criteria.createAlias("leito.unidadeFuncional", "unidadeFuncional");
		// criteria.add(Restrictions.eq("unidadeFuncional.seq",
		// Short.valueOf("116")));

		Calendar calInicial = Calendar.getInstance();
		calInicial.setTime(mesCompetencia);
		calInicial.set(Calendar.DAY_OF_MONTH, 1);

		Calendar calFinal = Calendar.getInstance();
		calFinal.setTime(mesCompetencia);
		calFinal.set(Calendar.DAY_OF_MONTH, calFinal.getActualMaximum(Calendar.DAY_OF_MONTH));

		criteria.add(Restrictions.between(AinExtratoLeitos.Fields.DTHR_LANCAMENTO.toString(),
				calInicial.getTime(), calFinal.getTime()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Pesquisar Extratos leitos.
	 * 
	 * @param mesCompetencia
	 * @param dominioMvtsLeitos
	 * @return
	 */
	public List<Object[]> pesquisarExtratosLeitoPorTipoMovs(Date mesCompetencia, Object[] dominioMvtsLeitos) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinExtratoLeitos.class, "extrato");

		cri.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "tipoMovLeito");

		cri.createAlias(AinExtratoLeitos.Fields.LEITO.toString(), "leito");

		cri.add(Restrictions.in("tipoMovLeito" + SEPARADOR
				+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), dominioMvtsLeitos));

		Calendar cal = Calendar.getInstance();
		cal.setTime(mesCompetencia);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

		cri.add(Restrictions.between(AinExtratoLeitos.Fields.DTHR_LANCAMENTO.toString(),
				mesCompetencia, cal.getTime()));


		cri.setProjection(Projections
				.projectionList()
				.add(Projections.property("leito" + SEPARADOR
						+ AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()))

				.add(Projections.property("leito" + SEPARADOR + AinLeitos.Fields.LTO_ID.toString()))

				.add(Projections.property("extrato" + SEPARADOR
						+ AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO_CODIGO.toString()))

				.add(Projections.property("tipoMovLeito" + SEPARADOR
						+ AinTiposMovimentoLeito.Fields.DESCRICAO.toString()))

				.add(Projections.property("tipoMovLeito" + SEPARADOR
						+ AinTiposMovimentoLeito.Fields.BLOQUEIO_PACIENTE.toString()))

				.add(Projections.property("extrato" + SEPARADOR
						+ AinExtratoLeitos.Fields.DTHR_LANCAMENTO.toString()))

				.add(Projections.property("extrato" + SEPARADOR
						+ AinExtratoLeitos.Fields.LEITO_ID.toString()))

		);

		return executeCriteria(cri);
	}
	
	/**
	 * Método usado para obter os dados relativos ao último extrato de um leito.
	 * 
	 * @param leitoID
	 * @param valoresResultado
	 */
	public List<Object[]> obterDadosUltimoExtrato(String leitoID) {

		DetachedCriteria criteriaUltimoExtrato = DetachedCriteria.forClass(AinExtratoLeitos.class);

		criteriaUltimoExtrato.createAlias(AinExtratoLeitos.Fields.ORIGEM_EVENTOS.toString(),AinExtratoLeitos.Fields.ORIGEM_EVENTOS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteriaUltimoExtrato.createAlias(AinExtratoLeitos.Fields.PACIENTE.toString(), AinExtratoLeitos.Fields.PACIENTE.toString(),JoinType.LEFT_OUTER_JOIN);
		criteriaUltimoExtrato.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoID));

		DetachedCriteria subQueryMaxCriadoEm = DetachedCriteria.forClass(AinExtratoLeitos.class);
		subQueryMaxCriadoEm.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoID));
		subQueryMaxCriadoEm.setProjection(Projections.max(AinExtratoLeitos.Fields.CRIADO_EM.toString()));

		criteriaUltimoExtrato.add(Subqueries.propertyEq(AinExtratoLeitos.Fields.CRIADO_EM.toString(), subQueryMaxCriadoEm));

		criteriaUltimoExtrato.setProjection(Projections.projectionList()
				.add(Projections.property(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO // 13
						.toString()))
				.add(Projections.property(AinExtratoLeitos.Fields.JUSTIFICATIVA // 14
						.toString()))
				.add(Projections.property(AinExtratoLeitos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString()))
				.add( // 15
				Projections.property(AinExtratoLeitos.Fields.ORIGEM_EVENTOS.toString() + "."
						+ AghOrigemEventos.Fields.DESCRICAO.toString())) // 16
				.add(Projections.property(AinExtratoLeitos.Fields.CRIADO_EM // 17
						.toString())));

		return this.executeCriteria(criteriaUltimoExtrato);
	}
	
	public List<AinExtratoLeitos> pesquisarExtratoPorLeitoOrderDataHoraLanc(String codigoLeito) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), codigoLeito));
		criteria.addOrder(Order.desc(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString()));
		return executeCriteria(criteria);
	}
		
	private DetachedCriteria montarConsulta(String leito, Date data) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);
		criteria.createAlias(AinExtratoLeitos.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES");

		if (leito!= null && StringUtils.isNotBlank(leito)) {
			criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leito));
		}

		if (data != null) {
			criteria.add(Restrictions.ge(AinExtratoLeitos.Fields.CRIADO_EM.toString(), data));
		}

		return criteria;
	}
	
	public List<AinExtratoLeitos> pesquisarExtratoLeitos(String leito, Date data, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarConsulta(leito, data);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarExtratoLeitoCount(String leito, Date data) {
		DetachedCriteria criteria = montarConsulta(leito, data);
		return executeCriteriaCount(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion8(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status,
			Integer numeroDiasCenso) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer(990);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq, lt.leitoID,");
		hql.append(" 	tml.descricao, el.dthrLancamento, tml.grupoMvtoLeito, el.seq");
		hql.append(" from AinExtratoLeitos el");
		hql.append(" join el.tipoMovimentoLeito as tml");
		hql.append(" join el.leito as lt ");
		hql.append(" join lt.unidadeFuncional as unf ");
		hql.append(" left join unf.unfSeq as unfSeq ");

		hql.append(" where tml.grupoMvtoLeito <> :grupoMvtoLeito ");
		hql.append(" and el.dthrLancamento > :dthrLancamentoMaior ");
		hql.append(" and el.dthrLancamento < :dthrLancamentoMenor ");
		hql.append(" and lt.indSituacao = :situacao ");

		hql.append(" and el.seq >= (");
		hql.append(" select coalesce(max(el1.seq), 0)");
		hql.append(" from AinExtratoLeitos el1 ");
		hql.append(" join el1.tipoMovimentoLeito as tml1");
		hql.append(" where el1.leito = lt.leitoID");
		hql.append(" and el1.dthrLancamento > :dthrLancamentoMaior");
		hql.append(" and el1.dthrLancamento < :dthrLancamentoMenor");
		hql.append(" and tml1.grupoMvtoLeito = :grupoMvtoLeito ");
		hql.append(')');

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		hql.append(" and 1 <= (");
		hql.append(" select count(lt1.leitoID)");
		hql.append(" from AinLeitos lt1 ");
		hql.append(" join lt1.unidadeFuncional as unf1 ");
		hql.append(" join unf1.caracteristicas as car ");
		hql.append(" where car.id.caracteristica = :caracr");
		hql.append(" and lt1.leitoID = lt.leitoID");
		hql.append(')');
		
		Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("dthrLancamentoMaior", obterData(data, -numeroDiasCenso));
		query.setParameter("dthrLancamentoMenor", obterData(data, +UM_DIA));
		
		query.setParameter("grupoMvtoLeito", DominioMovimentoLeito.O);
		query.setParameter("situacao", DominioSituacao.A);
		query.setParameter("caracr", ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO);

		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}
	
	/**
	 * @param leito
	 * @return
	 */
	public Date recuperarDataBloqueio(String leito) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);
		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(),
				AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());

		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leito));
		criteria.add(Restrictions.le(AinExtratoLeitos.Fields.CRIADO_EM.toString(), new Date()));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + "."
				+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.BL));
		criteria.addOrder(Order.desc(AinExtratoLeitos.Fields.CRIADO_EM.toString()));

		criteria.setProjection(Projections.projectionList().add(Projections.property(AinExtratoLeitos.Fields.CRIADO_EM.toString())));

		List<Date> result = executeCriteria(criteria, 0, 1, null, true);

		if (result != null && result.size() > 0) {
			return result.get(0);
		}

		return null;
	}

	public List<AinExtratoLeitos> listarExtratosLeitosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria

		.forClass(AinExtratoLeitos.class);
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<AinExtratoLeitos> pesquisarPorSeqDataInternacao(Integer seqInternacao, Date dataHoraInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.SEQ_INTERNACAO.toString(), seqInternacao));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.DTHR_LANCAMENTO.toString(), dataHoraInternacao));
		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<AinExtratoLeitos> pesquisarPorLeitoInternacaoMovimentos(AinLeitos leito, AinInternacao internacao, List<Short> listaMovimentosLeito,
			Short codigoMovimentoLeitoDesocupado) {

		StringBuffer hql = new StringBuffer(540);

		hql.append(" select exl ");
		hql.append(" from AinExtratoLeitos exl ");
		hql.append(" where exl.leito = :leito ");
		hql.append(" 	and ( ");
		hql.append(" 			( ");
		hql.append(" 				exl.internacao = :internacao ");
		hql.append(" 				and exl.tipoMovimentoLeito.codigo in (:movimentosLeito) ");
		hql.append(" 			) or ( ");
		hql.append(" 				exl.internacao is null ");
		hql.append(" 				and exl.tipoMovimentoLeito.codigo = :movimentoDesocupado ");
		hql.append(" 				and exl.dthrLancamento = ( ");
		hql.append(" 					select max (exl2.dthrLancamento) ");
		hql.append(" 					from AinExtratoLeitos exl2 ");
		hql.append(" 					where exl2.leito = exl.leito ");
		hql.append(" 						and exl2.internacao is null ");
		hql.append(" 						and exl2.tipoMovimentoLeito.codigo = :movimentoDesocupado2 ");
		hql.append(" 				) ");
		hql.append(" 			) ");
		hql.append(" 		) ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("leito", leito);
		query.setParameter("internacao", internacao);
		query.setParameterList("movimentosLeito", listaMovimentosLeito);
		query.setParameter("movimentoDesocupado", codigoMovimentoLeitoDesocupado);
		query.setParameter("movimentoDesocupado2", codigoMovimentoLeitoDesocupado);
		return query.list();
	}
	
	public Date obtemDthrFinalLeito(String leitoId, Date dataHora) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);

		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString())));

		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "TML");

		criteria.add(Restrictions.lt("TML." + AinTiposMovimentoLeito.Fields.CODIGO.toString(), Short.valueOf("60")));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoId));
		criteria.add(Restrictions.gt(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));
		criteria.addOrder(Order.asc(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString()));

		List<Date> lista = executeCriteria(criteria, 0, 1, null, true);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	public Date buscaDthrLeito(String leitoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);
		criteria.setProjection(Projections.max(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString()));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoId));
		return (Date) executeCriteriaUniqueResult(criteria);
	}
		
	public List<AinExtratoLeitos> pesquisarDataBloqueio(String leitoId, Date dataLancamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);

		criteria.createAlias(AinExtratoLeitos.Fields.LEITO.toString(), ALIAS_LEITO);

		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoId));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString(), dataLancamento));

		return executeCriteria(criteria);
	}
	
	/**
	 * Método para criar criteria básica para a busca da data mínima da
	 * movimentação de leito de AinExtratoLeitos
	 * 
	 * @param leitoId
	 * @return
	 */
	private DetachedCriteria criarCriteriaDataHoraLancamentoMinima(String leitoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);

		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), ALIAS_TIPO_MOVIMENTO_LEITO);
		criteria.createAlias(AinExtratoLeitos.Fields.LEITO.toString(), ALIAS_LEITO);

		criteria.setProjection(projectionList().add(Projections.min(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString())));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoId));

		return criteria;
	}

	public Date obterDataMinimaMovimentoLeito(String leitoId, Date data, DominioMovimentoLeito restricao1,
			DominioMovimentoLeito restricao2) {

		DetachedCriteria criteria = this.criarCriteriaDataHoraLancamentoMinima(leitoId);

		criteria.add(Restrictions.gt(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString(),
				DateUtils.truncate(data, Calendar.SECOND)));

		List<DominioMovimentoLeito> restricao = new ArrayList<DominioMovimentoLeito>();
		restricao.add(restricao1);
		restricao.add(restricao2);

		criteria.add(Restrictions.in(
				ALIAS_TIPO_MOVIMENTO_LEITO + SEPARADOR + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), restricao));

		List<Date> lista = this.executeCriteria(criteria, 0, 1, null, true);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	/**
	 * Método para criar criteria básica para a busca da data máxima da
	 * movimentação de leito de AinExtratoLeitos
	 * 
	 * @param leitoId
	 * @return
	 */
	private DetachedCriteria criarCriteriaDataHoraLancamentoMaxima(String leitoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);

		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), ALIAS_TIPO_MOVIMENTO_LEITO);
		criteria.createAlias(AinExtratoLeitos.Fields.LEITO.toString(), ALIAS_LEITO);

		criteria.setProjection(projectionList().add(Projections.max(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString())));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoId));

		return criteria;
	}

	public Date obterDataMaximaMovimentoLeito(String leitoId, Date data) {

		DetachedCriteria criteria = this.criarCriteriaDataHoraLancamentoMaxima(leitoId);

		criteria.add(Restrictions.lt(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString(),
				DateUtils.truncate(data, Calendar.DAY_OF_MONTH)));

		List<Date> lista = this.executeCriteria(criteria, 0, 1, null, true);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	public AinLeitos obterLeitoBloqueado(String leitoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), ALIAS_TIPO_MOVIMENTO_LEITO);
		criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leitoId));

		List<DominioMovimentoLeito> restricao = new ArrayList<DominioMovimentoLeito>();
		restricao.add(DominioMovimentoLeito.B);
		restricao.add(DominioMovimentoLeito.BI);

		criteria.add(Restrictions.in(
				ALIAS_TIPO_MOVIMENTO_LEITO + SEPARADOR + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), restricao));
		return (AinLeitos) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<AinExtratoLeitos> pesquisarDataBloqueioGrupoD(String leitoId, Date data) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);

		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), ALIAS_TIPO_MOVIMENTO_LEITO);
		criteria.createAlias(AinExtratoLeitos.Fields.LEITO.toString(), ALIAS_LEITO);

		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoId));
		criteria.add(Restrictions.gt(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString(), data));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + SEPARADOR
				+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.D));

		criteria.addOrder(Order.asc(AinExtratoLeitos.Fields.DTHR_LANCAMENTO.toString()));

		return this.executeCriteria(criteria);
	}
	
	public List<AinExtratoLeitos> pesquisarDataLiberacao(String leitoId, Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);

		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), ALIAS_TIPO_MOVIMENTO_LEITO);
		criteria.createAlias(AinExtratoLeitos.Fields.LEITO.toString(), ALIAS_LEITO);

		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoId));
		criteria.add(Restrictions.gt(AinExtratoLeitos.Fields.DTHR_LANCAMENTO.toString(), data));

		List<DominioMovimentoLeito> restricao = new ArrayList<DominioMovimentoLeito>();
		restricao.add(DominioMovimentoLeito.L);
		restricao.add(DominioMovimentoLeito.BL);
		criteria.add(Restrictions.in(
				ALIAS_TIPO_MOVIMENTO_LEITO + SEPARADOR + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), restricao));

		criteria.addOrder(Order.asc(AinExtratoLeitos.Fields.DTHR_LANCAMENTO.toString()));

		return this.executeCriteria(criteria);
	}
	
	public List<AinExtratoLeitos> pesquisarMovimentacaoLeito(String leitoId, Date data) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);
		criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), ALIAS_TIPO_MOVIMENTO_LEITO);
		criteria.createAlias(AinExtratoLeitos.Fields.LEITO.toString(), ALIAS_LEITO);

		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leitoId));
		criteria.add(Restrictions.lt(AinExtratoLeitos.Fields.DATA_HORA_LANCAMENTO.toString(), data));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + SEPARADOR
				+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.D));

		criteria.addOrder(Order.asc(AinExtratoLeitos.Fields.DTHR_LANCAMENTO.toString()));

		return this.executeCriteria(criteria);
	}
	
	public Long contarMovimentacaoExtratoLeito(AinLeitosVO leito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.LEITO_ID.toString(), leito.getLeitoID()));
		
		return this.executeCriteriaCount(criteria);
	}

	public AinLeitos obterLeitoDesativado(String leitoId) {
		AinLeitos retorno = null;
		if (leitoId != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);

			criteria.createAlias(AinExtratoLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), ALIAS_TIPO_MOVIMENTO_LEITO);

			criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leitoId));
			criteria.add(Restrictions.eq(
					ALIAS_TIPO_MOVIMENTO_LEITO + SEPARADOR + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
					DominioMovimentoLeito.D));
			retorno = (AinLeitos) this.executeCriteriaUniqueResult(criteria);
		}

		return retorno;
	}

	public List<AinExtratoLeitos> listaExtratosLeitos(AipPacientes paciente, AinInternacao internacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinExtratoLeitos.class);
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.PACIENTE.toString(), paciente));
		criteria.add(Restrictions.eq(AinExtratoLeitos.Fields.INTERNACAO.toString(), internacao));
		return executeCriteria(criteria);
	}
	
	private Date obterData(final Date data, final Integer numDiasCalculo) {
		final Calendar dataCalculo = Calendar.getInstance();

		dataCalculo.setTime(data);
		dataCalculo.add(Calendar.DATE, numDiasCalculo);
		dataCalculo.set(Calendar.HOUR_OF_DAY, 0);
		dataCalculo.set(Calendar.MINUTE, 0);
		dataCalculo.set(Calendar.SECOND, 0);
		dataCalculo.set(Calendar.MILLISECOND, 0);
		
		return dataCalculo.getTime();
	}	
}