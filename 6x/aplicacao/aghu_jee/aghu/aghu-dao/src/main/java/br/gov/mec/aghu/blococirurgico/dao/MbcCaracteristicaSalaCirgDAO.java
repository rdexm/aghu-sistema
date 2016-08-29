package br.gov.mec.aghu.blococirurgico.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcCaracteristicaSalaCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcCaracteristicaSalaCirg> {

	private static final long serialVersionUID = -1587808275461963142L;

	// Formata hora em 24Hmm
	private static final SimpleDateFormat SDF_24HMM = new SimpleDateFormat("kmm");

	public List<MbcCaracteristicaSalaCirg> listarCaracteristicaSalaCirgPorUnidade(Short unfSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteriaCaracteristicaSalaCirgPorUnidade(unfSeq, situacao, null);
		
		criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString()), "htc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("htc.".concat(MbcHorarioTurnoCirg.Fields.TURNO.toString()), "tur", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc("sci.".concat(MbcSalaCirurgica.Fields.ID_SEQP.toString())));
		criteria.addOrder(Order.asc("tur.".concat(MbcTurnos.Fields.ORDEM.toString())));
		
		return executeCriteria(criteria);
	}

	public List<LinhaReportVO> listarCaracteristicaSalaCirgPorUnidade(String pesquisa, Short unfSeq) {
		DetachedCriteria criteria = montarCriteriaCaracteristicaSalaCirgPorUnidade(unfSeq, null, pesquisa);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("sci." + MbcSalaCirurgica.Fields.ID_SEQP.toString())), LinhaReportVO.Fields.NUMERO4.toString())
				.add(Projections.property("sci." + MbcSalaCirurgica.Fields.NOME.toString()), LinhaReportVO.Fields.TEXTO1.toString())
				.add(Projections.property("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString()), LinhaReportVO.Fields.OBJECT.toString())
		);

		criteria.addOrder(Order.asc("sci." + MbcSalaCirurgica.Fields.ID_SEQP.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));

		return executeCriteria(criteria);
	}
	
	public Long listarCaracteristicaSalaCirgPorUnidadeCount(String pesquisa, Short unfSeq) {
		DetachedCriteria criteria = montarCriteriaCaracteristicaSalaCirgPorUnidade(unfSeq, null, pesquisa);		
		return executeCriteriaCountDistinct(criteria, "sci." + MbcSalaCirurgica.Fields.ID_SEQP.toString(), true);
	}

	private DetachedCriteria montarCriteriaCaracteristicaSalaCirgPorUnidade(Short unfSeq, DominioSituacao situacao, String pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createAlias(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.add(Restrictions.eq("sci.".concat(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString()), unfSeq));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString()), Boolean.TRUE));

		if(situacao != null){
			criteria.add(Restrictions.eq("sci.".concat(MbcSalaCirurgica.Fields.SITUACAO.toString()), situacao));
		}
		
		if (StringUtils.isNotBlank(pesquisa)) {
			Criterion criterionDescCodigo = null;
			if (CoreUtil.isNumeroShort(pesquisa)) {
				short codigo = Short.parseShort(pesquisa);
				criterionDescCodigo = Restrictions.eq("sci."+MbcSalaCirurgica.Fields.ID_SEQP.toString(), codigo);
			} else {
				criterionDescCodigo = Restrictions.ilike("sci."+MbcSalaCirurgica.Fields.NOME.toString(), pesquisa, MatchMode.ANYWHERE);
			}
			criteria.add(criterionDescCodigo);
		}
		
		return criteria;
	}

	public List<MbcCaracteristicaSalaCirg> buscarHorariosCaractPorSalaCirurgica(Short unfSeq, Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createCriteria("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("htc."+MbcHorarioTurnoCirg.Fields.TURNO.toString(), "tur", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA_ID_UNF_SEQ.toString()), unfSeq));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA_ID_SEQP.toString()), seqp));

		StringBuilder orderBy = new StringBuilder(200);
		orderBy.append(" case DIA_SEMANA ");
		orderBy.append("	when 'SEG' then 1 ");
		orderBy.append("	when 'TER' then 2 ");
		orderBy.append("	when 'QUA' then 3 ");
		orderBy.append("	when 'QUI' then 4 ");
		orderBy.append("	when 'SEX' then 5 ");
		orderBy.append("	when 'SAB' then 6 ");
		orderBy.append("	when 'DOM' then 7 ");
		orderBy.append(" end ");

		criteria.addOrder(OrderBySql.sql(orderBy.toString()));
		criteria.addOrder(Order.asc("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_UNF_SEQ.toString()));
		criteria.addOrder(Order.asc("tur." + MbcTurnos.Fields.ORDEM.toString()));

		return executeCriteria(criteria);
	}

	public List<Short> pesquisarSciSeqpDistinctByUnfSeq(Short unfSeq,
			Boolean indDisponivel, DominioSituacao situacao, DominioDiaSemana diaSemana) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class);
		criteria.createAlias(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "msc");//Join que não muda resultado porque colunas notNull

		setCriteriaMbcCaracteristica(criteria, unfSeq, indDisponivel, situacao, diaSemana, null, null);

		criteria.setProjection(Projections.distinct(Projections.property(MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString())));

		return executeCriteria(criteria);
	}

	private void setCriteriaMbcCaracteristica(DetachedCriteria criteria, Short unfSeq, Boolean indDisponivel,
			DominioSituacao situacao, DominioDiaSemana diaSemana,
			DominioSituacao situacaoTurno, Short sciSeqp) {
		if(unfSeq != null){
			criteria.add(Restrictions.eq("msc." + MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		}
		if(indDisponivel!= null){
			criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), indDisponivel));
		}
		if(diaSemana != null){
			criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), diaSemana));
		}

		if(situacao != null){
			criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), situacao));
		}
		if(sciSeqp != null){
			criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sciSeqp));
		}
		
		if(situacaoTurno != null){
			criteria.add(Restrictions.eq("tur." + MbcTurnos.Fields.SITUACAO.toString(), situacaoTurno));
		}
	}

	public MbcCaracteristicaSalaCirg buscarCaracteristicaComMesmaConfiguracao(MbcCaracteristicaSalaCirg caract) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");

		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()), caract.getDiaSemana()));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString()), caract.getMbcHorarioTurnoCirg()));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), caract.getMbcSalaCirurgica()));

		if(caract.getSeq() != null) {
			criteria.add(Restrictions.ne("cas.".concat(MbcCaracteristicaSalaCirg.Fields.SEQ.toString()), caract.getSeq()));
		}

		return (MbcCaracteristicaSalaCirg) executeCriteriaUniqueResult(criteria);
	}



	/**
	 * Obtem a urgência da característica da sala cirurgica através da sala
	 * 
	 * @param mbcSalaCirurgica
	 * @return
	 */
	public Boolean obterUrgenciaCaracteristicaSalaCirgPorSalaCirurgica(Short unfSeq, Short seqp) {

		if (unfSeq == null || seqp == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório não informado");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class);

		criteria.createCriteria(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sal", DetachedCriteria.INNER_JOIN);

		criteria.setProjection(Projections.property(MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString()));

		criteria.add(Restrictions.eq("sal.".concat(MbcSalaCirurgica.Fields.ID_SEQP.toString()), seqp));
		criteria.add(Restrictions.eq("sal.".concat(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString()), unfSeq));

		Boolean indUrgencia = (Boolean) executeCriteriaUniqueResult(criteria);
		return Boolean.TRUE.equals(indUrgencia);
	}

	/**
	 * Busca característica sala cirúrgica por turno do dia da semana e sala cirúrgica
	 * 
	 * @param dataHoraInicio
	 * @param sciSeqp
	 * @param unfSeq
	 * @return
	 */
	public MbcCaracteristicaSalaCirg buscarCaracteristicaPorTurnoDiaSemanSalaCirurgica(Date dataHoraInicio, Short sciSeqp, Short unfSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");

		criteria.createCriteria("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString()), "htc", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), "sal", DetachedCriteria.INNER_JOIN);

		// Critério da unidade funcional
		criteria.add(Restrictions.eq("htc.".concat(MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString()), unfSeq));
		// Critério da sala cirúrgica
		criteria.add(Restrictions.eq("sal.".concat(MbcSalaCirurgica.Fields.ID_SEQP.toString()), sciSeqp));

		/*
		 * Restrições do DIA DA SEMANA
		 */
		Calendar calendarDiaSemana = Calendar.getInstance();
		calendarDiaSemana.setTime(dataHoraInicio);
		DominioDiaSemana diaSemana = DominioDiaSemana.getDiaDaSemana(calendarDiaSemana.get(Calendar.DAY_OF_WEEK));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()), diaSemana));

		/*
		 * Segunda parte da pesquisa no intervalo de horas
		 */
		List<MbcCaracteristicaSalaCirg> listaCaracteristicaSalaCirg = this.executeCriteria(criteria);
		MbcCaracteristicaSalaCirg retorno = null; // Instancia retorno do método

		// Verifica se a lista possuí resultados
		if (!listaCaracteristicaSalaCirg.isEmpty()) {

			// Formata a data no formato 24HMM
			Integer horaInicioFormatada = Integer.valueOf(SDF_24HMM.format(dataHoraInicio));

			// Realiza a pesquisa de intervalos de hora através dos turnos. Obs. Parte externalizada do SQL original
			for (MbcCaracteristicaSalaCirg caract : listaCaracteristicaSalaCirg) {

				// Obtem o turno da característica da sala
				MbcHorarioTurnoCirg turnoCirg = caract.getMbcHorarioTurnoCirg();

				// Formata horário inicial e final do turno
				final Integer horarioInicial = Integer.valueOf(SDF_24HMM.format(turnoCirg.getHorarioInicial()));
				final Integer horarioFinal = Integer.valueOf(SDF_24HMM.format(turnoCirg.getHorarioFinal()));

				// Verifica o intervalo de horas entre a hora inicial e final do turno (menos 1 minuto)
				if (horarioInicial < horarioFinal && (horaInicioFormatada >= horarioInicial && horaInicioFormatada <= (horarioFinal - 1))) {
					retorno = caract;
					break; // Encontrou resultado
				}

				/*
				 * Verifica os seguintes intervalos: 1. Entre a hora inicial e às 24:00. 2. Entre a 0000 e a hora final (menos 1 minuto)
				 */
				if (horarioInicial > horarioFinal
						&& (horaInicioFormatada >= horarioInicial && horaInicioFormatada <= 2400 || horaInicioFormatada >= 0 && horaInicioFormatada <= (horarioFinal - 1))) {
					retorno = caract;
					break; // Encontrou resultado
				}

			}

		}

		return retorno;
	}


	public MbcCaracteristicaSalaCirg buscarCaracteristicaSalaEspera(MbcCirurgias cirurgia) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");

		criteria.createCriteria("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString()), "htc", DetachedCriteria.INNER_JOIN);
		// ((TO_CHAR(htc.horario_inicial,'hh24mi') <  TO_CHAR(htc.horario_final,'hh24mi') 
		criteria.add(Restrictions.lt("htc."+MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString(),"htc."+MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString()));
		// AND       TO_CHAR(c_dthr_prev_inicio,'hh24mi')BETWEEN TO_CHAR(htc.horario_inicial,'hh24mi') AND  ( TO_CHAR(htc.horario_final,'hh24mi')-1))
		//      OR  (TO_CHAR(htc.horario_inicial,'hh24mi') >  TO_CHAR(htc.horario_final,'hh24mi') 
		criteria.add(Restrictions.or(
				Restrictions.between(cirurgia.getDataPrevisaoInicio().toString(), "htc."+MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString(),"htc."+MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString()),
				Restrictions.gt("htc."+MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString(),"htc."+MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString())));
		Calendar horaFimDia = Calendar.getInstance();
		horaFimDia.set(Calendar.HOUR_OF_DAY, 24);   
		horaFimDia.set(Calendar.MINUTE, 00);   
		Calendar horaInicioDia = Calendar.getInstance();
		horaInicioDia.set(Calendar.HOUR_OF_DAY, 00);   
		horaInicioDia.set(Calendar.MINUTE, 00);   
		//	AND    ( TO_CHAR(c_dthr_prev_inicio,'hh24mi') BETWEEN TO_CHAR(htc.horario_inicial,'hh24mi') AND '2400'
		//     OR    TO_CHAR(c_dthr_prev_inicio,'hh24mi') BETWEEN '0000' AND (TO_CHAR(htc.horario_final,'hh24mi')-1))))
		criteria.add(Restrictions.or(
				Restrictions.between(cirurgia.getDataPrevisaoInicio().toString(), "htc."+MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString(),horaFimDia.getTime()),
				Restrictions.between(cirurgia.getDataPrevisaoInicio().toString(), horaInicioDia.getTime(),"htc."+MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString())));
		//AND  htc.unf_seq =  c_unf_seq
		//AND  cas.htc_turno = htc.turno
		//AND  cas.sci_unf_seq = htc.unf_seq
		//AND  cas.sci_seqp  = c_sci_seqp
		criteria.add(Restrictions.eq( MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), cirurgia.getSalaCirurgica()));
		criteria.add(Restrictions.eq( "htc."+MbcHorarioTurnoCirg.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), cirurgia.getUnidadeFuncional()));
		//AND  cas.dia_semana = DECODE(TO_CHAR(c_dthr_prev_inicio,'d'),1,'DOM', 2,'SEG',3,'TER',4,'QUA',5,'QUI',6,'SEX',7,'SAB');
		Calendar calendarDiaSemana = Calendar.getInstance();
		calendarDiaSemana.setTime(cirurgia.getDataPrevisaoInicio());
		DominioDiaSemana diaSemana = DominioDiaSemana.getDiaDaSemana(calendarDiaSemana.get(Calendar.DAY_OF_WEEK));
		
		criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), diaSemana));
		return (MbcCaracteristicaSalaCirg) this.executeCriteriaUniqueResult(criteria);
	
	}



	public List<MbcCaracteristicaSalaCirg> pesquisarTurnoCirurgiaParticular(MbcCirurgias cirurgia, Short htcUnfSeq, String  htcTurno) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createCriteria("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "SCI", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), cirurgia.getSalaCirurgica()));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_UNF_SEQ.toString()), htcUnfSeq));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_TURNO.toString()), htcTurno));
		
		Calendar calendarDiaSemana = Calendar.getInstance();
		calendarDiaSemana.setTime(cirurgia.getData());
		DominioDiaSemana diaSemana = DominioDiaSemana.getDiaDaSemana(calendarDiaSemana.get(Calendar.DAY_OF_WEEK));
		
		criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), diaSemana));
		
		return executeCriteria(criteria);
	}
	
	public Boolean existeTurnosValidos(Date data, MbcSalaCirurgica cirurgia, MbcHorarioTurnoCirg turnos) {
		final DetachedCriteria criteria = this.montaCriteriaPesquisarTurnosValidos(data, cirurgia, turnos);
		
		Long count = this.executeCriteriaCount(criteria);
		return count != null && count > 0;
	}
	
	private DetachedCriteria montaCriteriaPesquisarTurnosValidos(Date data, MbcSalaCirurgica cirurgia, MbcHorarioTurnoCirg turnos) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createCriteria("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "SCI", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString()), cirurgia));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString()), turnos));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString()), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString()), DominioSituacao.A));
		
		Calendar calendarDiaSemana = Calendar.getInstance();
		calendarDiaSemana.setTime(data);
		DominioDiaSemana diaSemana = DominioDiaSemana.getDiaDaSemana(calendarDiaSemana.get(Calendar.DAY_OF_WEEK));
		
		criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), diaSemana));
		
		return criteria;
	}
	
	public List<MbcCaracteristicaSalaCirg> pesquisarMbcCaracteristicaSalaCirgComCaracSalaEsp(
			MbcCaracteristicaSalaCirg caractSalaFiltro,
			DominioSituacao sciSituacao, DominioSituacao casSituacao,
			DominioSituacao cseIndSituacao, Boolean casIndDisponivel,
			Integer firstResult, Integer maxResult, boolean asc, String order) {
		DetachedCriteria criteria = getCriteriaPesquisarMbcCaracteristicaSalaCirgComCaracSalaEsp(
				caractSalaFiltro, sciSituacao, casSituacao, cseIndSituacao,
				casIndDisponivel);
		
		criteria.addOrder(Order.asc("unf."+AghUnidadesFuncionais.Fields.SIGLA.toString()));
		criteria.addOrder(Order.asc("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA_ID_SEQP.toString()));
		criteria.addOrder(Order.asc("cas."+MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()));
		criteria.addOrder(Order.asc("cas."+MbcCaracteristicaSalaCirg.Fields.TURNO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, order, asc);
	}

	public Long pesquisarMbcCaracteristicaSalaCirgComCaracSalaEspCount(
			MbcCaracteristicaSalaCirg caractSalaFiltro,
			DominioSituacao sciSituacao, DominioSituacao casSituacao,
			DominioSituacao cseIndSituacao, Boolean casIndDisponivel) {
		DetachedCriteria criteria = getCriteriaPesquisarMbcCaracteristicaSalaCirgComCaracSalaEsp(
				caractSalaFiltro, sciSituacao, casSituacao, cseIndSituacao,
				casIndDisponivel);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria getCriteriaPesquisarMbcCaracteristicaSalaCirgComCaracSalaEsp(
			MbcCaracteristicaSalaCirg caractSalaFiltro,
			DominioSituacao sciSituacao, DominioSituacao casSituacao,
			DominioSituacao cseIndSituacao, Boolean casIndDisponivel) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		
		addFiltroPesquisarMbcCaracteristicaSalaCirg(caractSalaFiltro, criteria);
		
		if(casSituacao != null){
			criteria.add(Restrictions.eq("cas."+MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), casSituacao));
		}
		if(sciSituacao != null){
			criteria.add(Restrictions.eq("sci."+MbcSalaCirurgica.Fields.SITUACAO.toString(), sciSituacao));
		}
		if(casIndDisponivel != null){
			criteria.add(Restrictions.eq("cas."+MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), casIndDisponivel));
		}
		criteria.add(Restrictions.not(Subqueries.exists(getSubCriteriaMbcCaractSalaEsps(cseIndSituacao))));
		return criteria;
	}

	private DetachedCriteria getSubCriteriaMbcCaractSalaEsps(DominioSituacao cseIndSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		
		criteria.setProjection(Projections.property(MbcCaractSalaEsp.Fields.SEQP.toString()));
		
		criteria.add(Restrictions.eqProperty("cse."+MbcCaractSalaEsp.Fields.CAS_SEQ.toString(), "cas." + MbcCaracteristicaSalaCirg.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq("cse."+MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), cseIndSituacao));
		criteria.add(Restrictions.isNotNull("cse."+MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString()));
		
		return criteria;
	}

	private void addFiltroPesquisarMbcCaracteristicaSalaCirg(
			MbcCaracteristicaSalaCirg caractSalaFiltro,
			DetachedCriteria criteria) {
		//Não interfere pra quem não precisa deste join pq notNull
		criteria.createCriteria("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createCriteria("sci."+MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		
		if(caractSalaFiltro.getDiaSemana() != null){
			criteria.add(Restrictions.eq("cas."+MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), caractSalaFiltro.getDiaSemana()));
		}
		
		if(caractSalaFiltro.getMbcTurnos() != null){
			criteria.add(Restrictions.eq("cas."+MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), caractSalaFiltro.getMbcTurnos()));
		}
		
		if(caractSalaFiltro.getUnidadeSalaCirurgica() !=null){
			criteria.add(Restrictions.eq("cas."+MbcCaracteristicaSalaCirg.Fields.UNIDADE_SALA_CIRURGICA.toString(), caractSalaFiltro.getUnidadeSalaCirurgica()));
		}
		
		if(caractSalaFiltro.getMbcSalaCirurgica() != null){
			criteria.add(Restrictions.eq("cas."+MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), caractSalaFiltro.getMbcSalaCirurgica()));
		}
	}

	public List<DominioDiaSemana> pesquisarDiasSemana(MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short unfSeq, AghEspecialidades especialidade, Short sciSeqp, Boolean reverse){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		
		criteria.setProjection(Projections.property("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()));
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(especialidade!=null){
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.HTC_UNF_SEQ.toString(), unfSeq));
		
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		if(profAtuaUnidCirg != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), profAtuaUnidCirg));
		}
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		StringBuilder orderBy = new StringBuilder();
		if(reverse) {
			orderBy.append(" case DIA_SEMANA ");
			orderBy.append("	when 'DOM' then 7 ");
			orderBy.append("	when 'SEG' then 6 ");
			orderBy.append("	when 'TER' then 5 ");
			orderBy.append("	when 'QUA' then 4 ");
			orderBy.append("	when 'QUI' then 3 ");
			orderBy.append("	when 'SEX' then 2 ");
			orderBy.append("	when 'SAB' then 1 ");
			orderBy.append(" end ");
		} else {
			orderBy.append(" case DIA_SEMANA ");
			orderBy.append("	when 'DOM' then 1 ");
			orderBy.append("	when 'SEG' then 2 ");
			orderBy.append("	when 'TER' then 3 ");
			orderBy.append("	when 'QUA' then 4 ");
			orderBy.append("	when 'QUI' then 5 ");
			orderBy.append("	when 'SEX' then 6 ");
			orderBy.append("	when 'SAB' then 7 ");
			orderBy.append(" end ");
		}

		criteria.addOrder(OrderBySql.sql(orderBy.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<DominioDiaSemana> pesquisarDiasSemana(Short unfSeq, Short sciSeqp, Boolean reverse){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		
		criteria.setProjection(Projections.property("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()));
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.HTC_UNF_SEQ.toString(), unfSeq));
		
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		StringBuilder orderBy = new StringBuilder();
		if(reverse) {
			orderBy.append(" case DIA_SEMANA ");
			orderBy.append("	when 'DOM' then 7 ");
			orderBy.append("	when 'SEG' then 6 ");
			orderBy.append("	when 'TER' then 5 ");
			orderBy.append("	when 'QUA' then 4 ");
			orderBy.append("	when 'QUI' then 3 ");
			orderBy.append("	when 'SEX' then 2 ");
			orderBy.append("	when 'SAB' then 1 ");
			orderBy.append(" end ");
		} else {
			orderBy.append(" case DIA_SEMANA ");
			orderBy.append("	when 'DOM' then 1 ");
			orderBy.append("	when 'SEG' then 2 ");
			orderBy.append("	when 'TER' then 3 ");
			orderBy.append("	when 'QUA' then 4 ");
			orderBy.append("	when 'QUI' then 5 ");
			orderBy.append("	when 'SEX' then 6 ");
			orderBy.append("	when 'SAB' then 7 ");
			orderBy.append(" end ");
		}

		criteria.addOrder(OrderBySql.sql(orderBy.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCaracteristicaSalaCirg> pesquisarDiasSemanaPorTurno(Date dtBase, MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short unfSeq,
			AghEspecialidades especialidade, Short sciSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		
		criteria.setProjection(Projections.property("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString()));
		
		if(profAtuaUnidCirg != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), profAtuaUnidCirg));
		}
		if(especialidade != null){
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		
		// substitui decode dia_semana to_char(data,'D')
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(dtBase))));
		
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.HTC_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));

		//consulta de htc_turno em AghFeriados foi implementada na ON, pois 
		// AghFeriados.turno é um dominio e MbcCaracteristicaSalaCirg.mbcTurno.turno é String.

		
		StringBuilder orderBy = new StringBuilder(200);
		orderBy.append(" case DIA_SEMANA ");
		orderBy.append("	when 'DOM' then 1 ");
		orderBy.append("	when 'SEG' then 2 ");
		orderBy.append("	when 'TER' then 3 ");
		orderBy.append("	when 'QUA' then 4 ");
		orderBy.append("	when 'QUI' then 5 ");
		orderBy.append("	when 'SEX' then 6 ");
		orderBy.append("	when 'SAB' then 7 ");
		orderBy.append(" end ");

		criteria.addOrder(OrderBySql.sql(orderBy.toString()));
		
		return executeCriteria(criteria);
	}

	public Date buscarMaiorHorarioInicioFimTurno(Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Date dtAgendaParam, Short sciSeqpCombo, MbcAgendas agenda, Boolean fimTurno,
			Boolean retornaHorarioOrd, Date hrInicioEscala) {
		List<Date> listaHorarios = new ArrayList<Date>();
		
		listaHorarios.addAll(buscarMaiorHorarioInicioFimTurnoUnion1(pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, dtAgendaParam, sciSeqpCombo, agenda, fimTurno));
		listaHorarios.addAll(buscarMaiorHorarioInicioFimTurnoUnion2(dtAgendaParam, sciSeqpCombo, agenda, fimTurno));
		listaHorarios.addAll(buscarMaiorHorarioInicioFimTurnoUnion3(dtAgendaParam, sciSeqpCombo, agenda, fimTurno));
		
		List<Date> listaHorariosTruncados = new ArrayList<Date>();
		for(Date data : listaHorarios) {
			listaHorariosTruncados.add(DateUtil.truncaHorario(data));
		}
		
		if(fimTurno) {
			Date ultimoHorario = null;
			Collections.reverse(listaHorariosTruncados);
			
			if (!listaHorariosTruncados.isEmpty()) {
				ultimoHorario = listaHorariosTruncados.get(0);
			}
			
			if(ultimoHorario != null && DateUtil.getHoras(ultimoHorario) == 0 && DateUtil.getMinutos(ultimoHorario) == 0) {
				if(!retornaHorarioOrd) {
					return ultimoHorario;
				} else {
					return DateUtil.obterDataComHoraFinal(ultimoHorario);
				}
			}
		} else {
			Collections.sort(listaHorariosTruncados);
			if(hrInicioEscala != null) {
				for(Date dt : listaHorariosTruncados) {
					if(dt.compareTo(DateUtil.truncaHorario(hrInicioEscala)) > 0) {
						return dt;
					}
				}
				return null;
			}
		}
		
		return (!listaHorariosTruncados.isEmpty() ? listaHorariosTruncados.get(0) : null);
	}
	
	private List<Date> buscarMaiorHorarioInicioFimTurnoUnion1(Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Date dtAgendaParam, Short sciSeqpCombo, MbcAgendas agenda, Boolean fimTurno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");
		
		if (fimTurno) {
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString()))
					.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString())));
		} else {
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString()))
					.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString())));
		}
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_SER_MATRICULA.toString(), agenda.getProfAtuaUnidCirgs().getId().getSerMatricula()));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_SER_VIN_CODIGO.toString(), agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo()));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_UNF_SEQ.toString(), agenda.getProfAtuaUnidCirgs().getId().getUnfSeq()));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_IND_FUNCAO_PROF.toString(), agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf()));
		if(agenda.getEspecialidade() != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), agenda.getEspecialidade().getSeq()));
		}
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), agenda.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_UNF_SEQ.toString(), agenda.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sciSeqpCombo));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(dtAgendaParam))));
		
		// tirando as que foram cedidas para outras equipes @ORADB mbcc_ver_subst_bloq
		DetachedCriteria subCriteriaEscalaSalas = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ssl");
		subCriteriaEscalaSalas.setProjection(Projections.property(MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(dtAgendaParam)));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString(), "cse." + MbcCaractSalaEsp.Fields.CAS_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_ESP_SEQ.toString(), "cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_SEQP.toString(), "cse." + MbcCaractSalaEsp.Fields.SEQP.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.notExists(subCriteriaEscalaSalas));
		
		// tirando as que estao bloqueadas
		DetachedCriteria subCriteriaBloq = DetachedCriteria.forClass(MbcBloqSalaCirurgica.class, "bsc");
		subCriteriaBloq.setProjection(Projections.property(MbcBloqSalaCirurgica.Fields.SEQ.toString()));
		subCriteriaBloq.add(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.SCI_UNF_SEQ.toString(), "sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString()));
		subCriteriaBloq.add(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.SCI_SEQP.toString(), "sci." + MbcSalaCirurgica.Fields.SEQP.toString()));
		
		subCriteriaBloq.add(Restrictions.le("bsc." + MbcBloqSalaCirurgica.Fields.DT_INICIO.toString(), DateUtil.truncaData(dtAgendaParam)));
		subCriteriaBloq.add(Restrictions.ge("bsc." + MbcBloqSalaCirurgica.Fields.DT_FIM.toString(), DateUtil.truncaData(dtAgendaParam)));
		
		subCriteriaBloq.add(Restrictions.or(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.DIA_SEMANA.toString(), "cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.DIA_SEMANA.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.TURNO_TURNO.toString(), "cas." + MbcCaracteristicaSalaCirg.Fields.TURNO_ID.toString()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.TURNO_TURNO.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_MATRICULA.toString(), pucSerMatriculaParam),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_MATRICULA.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_VINCODIGO.toString(), pucSerVinCodigoParam),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_VINCODIGO.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_UNF_SEQ.toString(), pucUnfSeqParam),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_UNF_SEQ.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_IND_FUNCAO_PROF.toString(), pucFuncProfParam),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_IND_FUNCAO_PROF.toString())));
		subCriteriaBloq.add(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.notExists(subCriteriaBloq));
		
		// tirando feriados
		DetachedCriteria subCriteriaFrd = DetachedCriteria.forClass(AghFeriados.class, "frd");
		subCriteriaFrd.setProjection(Projections.property(AghFeriados.Fields.DATA.toString()));
		// Restrição feita com sql nativo pois não é possível comparar Dominio com pojo MbcTurnos utilizando criteria
		subCriteriaFrd.add(Restrictions.or(Restrictions.sqlRestriction(" TURNO = HTC_TURNO "),
				Restrictions.isNull("frd." + AghFeriados.Fields.TURNO.toString())));
		subCriteriaFrd.add(Restrictions.eq("frd." + AghFeriados.Fields.DATA.toString(), DateUtil.truncaData(dtAgendaParam)));
		criteria.add(Subqueries.notExists(subCriteriaFrd));
		
		List<Date> listaRetorno = new ArrayList<Date>();
		List<Object[]> valores = executeCriteria(criteria);

		if(valores != null && valores.size() > 0) {
			for (Object[] object : valores) {
				if(object[0] != null){
					listaRetorno.add((Date) object[0]);
				} else {
					listaRetorno.add((Date) object[1]);
				}
			}
		}
		
		return listaRetorno;
	}

	private List<Date> buscarMaiorHorarioInicioFimTurnoUnion2(Date dtAgendaParam, Short sciSeqpCombo, MbcAgendas agenda, Boolean fimTurno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ssl");
		criteria.createAlias("ssl." + MbcSubstEscalaSala.Fields.MBC_CARACT_SALA_ESPS.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");
		
		if (fimTurno) {
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString()))
					.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString())));
		} else {
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString()))
					.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString())));
		}
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(dtAgendaParam)));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), agenda.getProfAtuaUnidCirgs().getId().getSerMatricula()));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo()));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), agenda.getProfAtuaUnidCirgs().getId().getUnfSeq()));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf()));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), agenda.getUnidadeFuncional().getSeq()));
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_UNF_SEQ.toString(), agenda.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sciSeqpCombo));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<Date> listaRetorno = new ArrayList<Date>();
		List<Object[]> valores = executeCriteria(criteria);

		if(valores != null && valores.size() > 0) {
			for (Object[] object : valores) {
				if(object[0] != null){
					listaRetorno.add((Date) object[0]);
				} else {
					listaRetorno.add((Date) object[1]);
				}
			}
		}
		
		return listaRetorno;
	}

	private List<Date> buscarMaiorHorarioInicioFimTurnoUnion3(Date dtAgendaParam, Short sciSeqpCombo, MbcAgendas agenda, Boolean fimTurno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createCriteria("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");
		criteria.createCriteria("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createCriteria("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		
		if (fimTurno) {
			criteria.setProjection(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString()));
		} else {
			criteria.setProjection(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString()));
		}
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(dtAgendaParam)));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_MATRICULA.toString(), agenda.getProfAtuaUnidCirgs().getId().getSerMatricula()));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_VIN_CODIGO.toString(), agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo()));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_UNF_SEQ.toString(), agenda.getProfAtuaUnidCirgs().getId().getUnfSeq()));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_IND_FUNCAO_PROF.toString(), agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf()));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), agenda.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_UNF_SEQ.toString(), agenda.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sciSeqpCombo));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public Long pesquisarSalaDiaSemanaTurnoCount(Object objSalaDiaSemanaTurno, AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = gerarCriteriaPesquisarSalaDiaSemanaTurno(objSalaDiaSemanaTurno, unidadeFuncional);
		return executeCriteriaCount(criteria);
	}
	
	public List<MbcCaracteristicaSalaCirg> pesquisarSalaDiaSemanaTurno(Object objSalaDiaSemanaTurno, AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = gerarCriteriaPesquisarSalaDiaSemanaTurno(objSalaDiaSemanaTurno, unidadeFuncional);
		criteria.addOrder(Order.asc("SLC." + MbcSalaCirurgica.Fields.ID_SEQP.toString()));
		criteria.addOrder(OrderBySql.sql("case this_.DIA_SEMANA when 'DOM' then '1'"
				+ " when 'SEG' then '2'" + " when 'TER' then '3'" + " when 'QUA' then '4'" + " when 'QUI' then '5'" + " when 'SEX' then '6'" + " when 'SAB' then '7' end"));
		criteria.addOrder(Order.asc("TUR." + MbcTurnos.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria gerarCriteriaPesquisarSalaDiaSemanaTurno(Object objSalaDiaSemanaTurno, AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "CSC");
		criteria.createAlias("CSC." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(), "TUR");
		criteria.createAlias("CSC." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "SLC");
		
		if(unidadeFuncional != null){
			criteria.add(Restrictions.eq("CSC." + MbcCaracteristicaSalaCirg.Fields.SCI_UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		}
		
		if(!objSalaDiaSemanaTurno.toString().isEmpty()){
			if (CoreUtil.isNumeroByte(objSalaDiaSemanaTurno)) {
				criteria.add(Restrictions.eq("CSC." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), Short.valueOf(objSalaDiaSemanaTurno.toString())));
			} else {
				if (objSalaDiaSemanaTurno.toString().equalsIgnoreCase(DominioDiaSemana.DOM.getCodigo()) ||
					objSalaDiaSemanaTurno.toString().equalsIgnoreCase(DominioDiaSemana.SEG.getCodigo()) ||
					objSalaDiaSemanaTurno.toString().equalsIgnoreCase(DominioDiaSemana.TER.getCodigo()) ||	
					objSalaDiaSemanaTurno.toString().equalsIgnoreCase(DominioDiaSemana.QUA.getCodigo()) ||
					objSalaDiaSemanaTurno.toString().equalsIgnoreCase(DominioDiaSemana.QUI.getCodigo()) ||
					objSalaDiaSemanaTurno.toString().equalsIgnoreCase(DominioDiaSemana.SEX.getCodigo()) ||
					objSalaDiaSemanaTurno.toString().equalsIgnoreCase(DominioDiaSemana.SAB.getCodigo())){
						criteria.add(Restrictions.eq("CSC." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.valueOf(objSalaDiaSemanaTurno.toString().toUpperCase())));
				} else {
					criteria.add(Restrictions.ilike("TUR."+MbcTurnos.Fields.DESCRICAO.toString(), objSalaDiaSemanaTurno.toString(), MatchMode.ANYWHERE));
				}
			}
		}
		
	
		
		return criteria;

	}

	public List<MbcCaracteristicaSalaCirg> pesquisarMbcSalaCirurgica(Short sciUnfSeq,
			Boolean indDisponivel, DominioSituacao situacaoCaracSalaCirg,
			DominioDiaSemana diaSemana, DominioSituacao situacaoTurno, Short sciSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class);
		criteria.createAlias(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "msc");//Join que não muda resultado porque colunas notNull
		criteria.createAlias(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");//Join que não muda resultado porque colunas notNull
		criteria.createAlias("htc." + MbcHorarioTurnoCirg.Fields.TURNO.toString(), "tur");//Join que não muda resultado porque colunas notNull

		setCriteriaMbcCaracteristica(criteria, sciUnfSeq, indDisponivel, situacaoCaracSalaCirg, diaSemana, situacaoTurno, sciSeqp);

		return executeCriteria(criteria);
	}

	public List<MbcCaracteristicaSalaCirg> listarCaracteristicaSalaCirgPorDiaSemana(String pesquisa, DominioDiaSemana diaSemana) {
        DetachedCriteria criteria = montaCriteriaCaracteristicaSalaCirgPorDiaSemana(pesquisa, diaSemana, Boolean.FALSE);
		return executeCriteria(criteria);
	}

    private DetachedCriteria montaCriteriaCaracteristicaSalaCirgPorDiaSemana(String pesquisa, DominioDiaSemana diaSemana, Boolean isCount) {
        DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
        criteria.createAlias(MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
        criteria.createAlias("sci.".concat(MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString()), "unf");
        criteria.createAlias("cas.".concat(MbcCaracteristicaSalaCirg.Fields.TURNO.toString()), "TURNO", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString()), DominioSituacao.A));
        criteria.add(Restrictions.eq("sci.".concat(MbcSalaCirurgica.Fields.SITUACAO.toString()), DominioSituacao.A));
        criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString()), Boolean.TRUE));
        criteria.add(Restrictions.eq("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()), diaSemana));

        DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
        subCriteria.setProjection(Projections.property("cse." + MbcCaractSalaEsp.Fields.CAS_SEQ.toString()));
        subCriteria.add(Restrictions.eqProperty(MbcCaractSalaEsp.Fields.CAS_SEQ.toString(), "cas.".concat(MbcCaracteristicaSalaCirg.Fields.SEQ.toString())));
        subCriteria.add(Restrictions.eq("cse.".concat(MbcCaractSalaEsp.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
        subCriteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.PUC_SER_MATRICULA.toString())));
        subCriteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.PUC_SER_VIN_CODIGO.toString())));
        subCriteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.PUC_UNF_SEQ.toString())));
        subCriteria.add(Restrictions.isNotNull("cse.".concat(MbcCaractSalaEsp.Fields.PUC_IND_FUNCAO_PROF.toString())));

        criteria.add(Subqueries.notExists(subCriteria));

        if(!isCount) {
        	criteria.addOrder(Order.asc("unf.".concat(AghUnidadesFuncionais.Fields.SIGLA.toString())));
        	criteria.addOrder(Order.asc("sci.".concat(MbcSalaCirurgica.Fields.SEQP.toString())));
        	criteria.addOrder(Order.asc("cas.".concat(MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString())));
        	criteria.addOrder(Order.asc("cas.".concat(MbcCaracteristicaSalaCirg.Fields.TURNO.toString())));
        }

        if (StringUtils.isNotBlank(pesquisa)) {
            if (CoreUtil.isNumeroShort(pesquisa)) {
                short codigo = Short.parseShort(pesquisa);
                criteria.add(Restrictions.or(Restrictions.eq("sci."+MbcSalaCirurgica.Fields.ID_SEQP.toString(), codigo),
                        Restrictions.eq("sci."+MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), codigo)));
            } else {
                criteria.add(Restrictions.or(Restrictions.ilike("sci."+MbcSalaCirurgica.Fields.NOME.toString(), pesquisa, MatchMode.ANYWHERE),
                        Restrictions.ilike("unf."+AghUnidadesFuncionais.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE)));
            }
        }

        return criteria;
    }

    public Long pesquisaCaracteristicaSalaCirgPorDiaSemanaCount(String pesquisa, DominioDiaSemana diaSemana) {
        DetachedCriteria criteria = montaCriteriaCaracteristicaSalaCirgPorDiaSemana(pesquisa, diaSemana, Boolean.TRUE);
        return executeCriteriaCount(criteria);
    }
}