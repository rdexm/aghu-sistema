package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcHorarioTurnoCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcHorarioTurnoCirg> {

	private static final long	serialVersionUID	= -5021887473668569700L;

	public List<MbcHorarioTurnoCirg> buscarMbcHorarioTurnoCirg(MbcHorarioTurnoCirg mbcHorarioTurnoCirg) {
		final DetachedCriteria criteria = this.criarCriteriaBuscarMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
//		criteria.addOrder(Order.asc(MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString()));
		//ordenação no Java pois é só por horário
		List<MbcHorarioTurnoCirg> listaMbcHorarioTurnoCirg = executeCriteria(criteria);
		if (listaMbcHorarioTurnoCirg != null && listaMbcHorarioTurnoCirg.size() > 0) {
			listaMbcHorarioTurnoCirg = ordenarMbcHorarioTurnoCirgPorHorario(listaMbcHorarioTurnoCirg);
		}
		return listaMbcHorarioTurnoCirg;
	}
	
	public List<MbcHorarioTurnoCirg> buscarTurnosPorUnidadeFuncional(Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class);
		criteria.createAlias(MbcHorarioTurnoCirg.Fields.TURNO.toString(), "TUR", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcHorarioTurnoCirg> buscarTurnosPorUnidadeFuncionalSb(Object param, Short unfSeq) {
		List<MbcHorarioTurnoCirg> retorno = new ArrayList<MbcHorarioTurnoCirg>();
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class);
		
		criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
		if (param != null) {
			criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.TURNO_ID.toString(), param.toString().toUpperCase()));
		}
		retorno =  executeCriteria(criteria);
		
		if (retorno == null || retorno.isEmpty()) {
			criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class);
			criteria.createAlias(MbcHorarioTurnoCirg.Fields.TURNO.toString(), "TUR");
			
			criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
			if (param != null) {
				criteria.add(Restrictions.ilike("TUR."+MbcTurnos.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
			}
			retorno =  executeCriteria(criteria);
		}
		return executeCriteria(criteria);
	}
	
	public Long buscarTurnosPorUnidadeFuncionalSbCount(Object param, Short unfSeq) {
		List<MbcHorarioTurnoCirg> retorno = buscarTurnosPorUnidadeFuncionalSb(param, unfSeq);
		Long count = Long.valueOf(0);
		if (retorno != null) {
			count = Long.valueOf(retorno.size());
		}
		return count;
	}

	public List<MbcHorarioTurnoCirg> ordenarMbcHorarioTurnoCirgPorHorario(List<MbcHorarioTurnoCirg> listaMbcHorarioTurnoCirg) {
		Collections.sort(listaMbcHorarioTurnoCirg, new Comparator<MbcHorarioTurnoCirg>() {
			@Override
			public int compare(MbcHorarioTurnoCirg o1, MbcHorarioTurnoCirg o2) {
				Calendar dateO1 = Calendar.getInstance();
				Calendar dateO2 = Calendar.getInstance();
				dateO1.setTime(o1.getHorarioInicial());
				dateO2.setTime(o2.getHorarioInicial());
				zerarData(dateO1);
				zerarData(dateO2);
				
				return dateO1.compareTo(dateO2);
			}
			private void zerarData(Calendar date) {
				Calendar dateAux = Calendar.getInstance();
				date.set(Calendar.DAY_OF_MONTH, dateAux.get(Calendar.DAY_OF_MONTH));
				date.set(Calendar.MONTH, dateAux.get(Calendar.MONTH));
				date.set(Calendar.YEAR, dateAux.get(Calendar.YEAR));
			}
			
		});
		
		return listaMbcHorarioTurnoCirg;
	}

	private DetachedCriteria criarCriteriaBuscarMbcHorarioTurnoCirg(final MbcHorarioTurnoCirg mbcHorarioTurnoCirg) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class);
		criteria.createAlias(MbcHorarioTurnoCirg.Fields.TURNO.toString(), "TURNO", JoinType.LEFT_OUTER_JOIN);
		
		if (mbcHorarioTurnoCirg != null) {
			if (mbcHorarioTurnoCirg.getId() != null) {
				if (mbcHorarioTurnoCirg.getId().getUnfSeq() != null) {
					criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), mbcHorarioTurnoCirg.getId().getUnfSeq()));
				}
				if (mbcHorarioTurnoCirg.getId().getTurno() != null) {
					criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.TURNO_ID.toString(), mbcHorarioTurnoCirg.getId().getTurno()));
				}
			}
			if (mbcHorarioTurnoCirg.getAghUnidadesFuncionais() != null) {
				criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), mbcHorarioTurnoCirg.getAghUnidadesFuncionais().getSeq()));
			}

		}
		return criteria;
	}

	public List<AghUnidadesFuncionais> buscarUnidadesFuncionaisCirurgia(Object objPesquisa) {
		List<AghUnidadesFuncionais> retorno = null;

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			retorno = executeCriteria(this.criarCriteriaBuscarMbcHorarioTurnoCirgPorCodigo(Short.valueOf(objPesquisa.toString())));
		}
		if (retorno == null || retorno.isEmpty()) {
			final DetachedCriteria criteria = this.criarCriteriaBuscarMbcHorarioTurnoCirgPorDescricao(objPesquisa);
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
			retorno = executeCriteria(criteria, 0, 100, null, false);
		}
		return retorno;
	}
	
	public List<AghUnidadesFuncionais> buscarUnidadesFuncionaisCirurgiaSB(Object objPesquisa) {
		List<AghUnidadesFuncionais> retorno = null;
		if (CoreUtil.isNumeroShort(objPesquisa)) {
			DetachedCriteria criteria = this.criarCriteriaBuscarMbcHorarioTurnoCirgPorCodigo(Short.valueOf(objPesquisa.toString()));
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
			retorno = executeCriteria(criteria);
		}
		if (retorno == null || retorno.isEmpty()) {
			final DetachedCriteria criteria = this.criarCriteriaBuscarMbcHorarioTurnoCirgPorDescricao(objPesquisa);
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
			retorno = executeCriteria(criteria, 0, 100, null, false);
		}
		return retorno;
	}

	public Long contarUnidadesFuncionaisCirurgia(Object objPesquisa) {
		Long retorno = Long.valueOf(0);

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			retorno = executeCriteriaCount(this.criarCriteriaBuscarMbcHorarioTurnoCirgPorCodigo(Short.valueOf(objPesquisa.toString())));
		}
		if (retorno == 0) {
			retorno = executeCriteriaCount(this.criarCriteriaBuscarMbcHorarioTurnoCirgPorDescricao(objPesquisa));
		}
		return retorno;
	}

	private DetachedCriteria criarCriteriaBuscarMbcHorarioTurnoCirgPorDescricao(final Object objPesquisa) {
		final DetachedCriteria criteria = this.criarCriteriaBaseAghUnidadesFuncionaisCirurgia();
		criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), objPesquisa == null ? "" : objPesquisa.toString(), MatchMode.ANYWHERE));

		return criteria;
	}

	private DetachedCriteria criarCriteriaBuscarMbcHorarioTurnoCirgPorCodigo(Short objPesquisa) {
		final DetachedCriteria criteria = this.criarCriteriaBaseAghUnidadesFuncionaisCirurgia();
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), objPesquisa));

		return criteria;
	}

	private DetachedCriteria criarCriteriaBaseAghUnidadesFuncionaisCirurgia() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CUF");
		criteria.add(Restrictions.eq("CUF." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),
				ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public boolean existeCaracteristicaSalaCirgs(final MbcHorarioTurnoCirgId id) {
		if(id == null) {
			return false;
		}
		return executeCriteriaCount(criarCriteriaCaracteristicaSalaCirgs(id)) > 0;
	}

	private DetachedCriteria criarCriteriaCaracteristicaSalaCirgs(final MbcHorarioTurnoCirgId id) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class);
		criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_TURNO.toString(), id.getTurno()));
		criteria.add(Restrictions.eq(MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_UNF_SEQ.toString(), id.getUnfSeq()));
		
		return criteria;
	}

	public List<MbcHorarioTurnoCirg> buscarHorariosCirgOutrosTurnos(final Short unfSeq, final String turno) {
		return executeCriteria(criarCriteriaHorariosCirgOutrosTurnos(unfSeq, turno));
	}
	
	private DetachedCriteria criarCriteriaHorariosCirgOutrosTurnos(final Short unfSeq, final String turno) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class);
		criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.ne(MbcHorarioTurnoCirg.Fields.TURNO_ID.toString(), turno));
		
		return criteria;
	}
	
	public List<MbcHorarioTurnoCirg> buscarHorariosCirgPorUnfSeqTurno(final Short unfSeq, final String turno) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class);
		criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(MbcHorarioTurnoCirg.Fields.TURNO_ID.toString(), turno));
		
		return executeCriteria(criteria);
	}
	
	
	public MbcHorarioTurnoCirg obterHorarioInicioTurnoCirurg(MbcCirurgias cirurgia) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class, "HTC");

		criteria.add(Restrictions.eq("HTC."+MbcHorarioTurnoCirg.Fields.AGH_UNIDADES_FUNCIONAIS.toString(),cirurgia.getUnidadeFuncional()));
			
//		Calendar horaFimDia = Calendar.getInstance();
//		horaFimDia.set(Calendar.HOUR_OF_DAY, 24);   
//		horaFimDia.set(Calendar.MINUTE, 00);   
//		Calendar horaInicioDia = Calendar.getInstance();
//		horaInicioDia.set(Calendar.HOUR_OF_DAY, 00);   
//		horaInicioDia.set(Calendar.MINUTE, 00);   

		criteria.add(Restrictions.or(Restrictions.ltProperty("HTC."+MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString(),"HTC."+MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString()), Restrictions.gtProperty("HTC."+MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString(),"HTC."+MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString())));
		
		MbcHorarioTurnoCirg retorno = null;
		List<MbcHorarioTurnoCirg> lista =  this.executeCriteria(criteria);
		
		if(!lista.isEmpty()){
			
			final String formatoHHmm = "HHmm"; // Pattern para conversão no formato Short
			
			Short cDthrPrevinicio = 0;
			if(cirurgia.getDataPrevisaoInicio() != null){
				cDthrPrevinicio = Short.valueOf(DateUtil.obterDataFormatada(cirurgia.getDataPrevisaoInicio(), formatoHHmm));	
			}
			
			for (MbcHorarioTurnoCirg item : lista) {
				
				final Short horarioInicial = Short.valueOf(DateUtil.obterDataFormatada(item.getHorarioInicial(), formatoHHmm));
				final Short horarioFinal = Short.valueOf(DateUtil.obterDataFormatada(item.getHorarioFinal(), formatoHHmm));
			
				boolean validacaoHrIniMenorHrFim = horarioInicial < horarioFinal;
				boolean validacaoIntervaloPrevIni = cDthrPrevinicio >= horarioInicial && cDthrPrevinicio <= (horarioFinal - 1);
				
				if(validacaoHrIniMenorHrFim && validacaoIntervaloPrevIni){
					retorno = item; // Seta retorno
					break;
				}
				
				boolean validacaoHrIniMaiorHrFim = horarioInicial > horarioFinal;
				boolean validacaoIntervaloPrevIni2400 = cDthrPrevinicio >= horarioInicial && cDthrPrevinicio <= 2400;
				boolean validacaoIntervaloPrevIni0000 = cDthrPrevinicio >= 0 && cDthrPrevinicio <= (horarioFinal - 1);
				
				if(validacaoHrIniMaiorHrFim && (validacaoIntervaloPrevIni2400 || validacaoIntervaloPrevIni0000)){
					retorno = item; // Seta retorno
					break;
				}
				
			}

		}

		return retorno;
	
	}

	public MbcHorarioTurnoCirg obterHorarioNoiteTurnoCirurg(MbcCirurgias cirurgia) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class, "HTC");

		criteria.add(Restrictions.eq("HTC."+MbcHorarioTurnoCirg.Fields.AGH_UNIDADES_FUNCIONAIS.toString(),cirurgia.getUnidadeFuncional()));
		criteria.add(Restrictions.eq("HTC."+MbcHorarioTurnoCirg.Fields.TURNO_ID.toString(),"N"));
		
		return (MbcHorarioTurnoCirg) this.executeCriteriaUniqueResult(criteria);
	
	}

	public List<MbcHorarioTurnoCirg> pesquisarHorarioTurnoCirgPorHorarioInicial(Date dthrPrevInicio, AghUnidadesFuncionais unidadeFuncional){
		StringBuilder hql = new StringBuilder(400);
		hql.append(" SELECT htc FROM MbcHorarioTurnoCirg htc ");
		hql.append(" WHERE htc.id.unfSeq = :unfSeq AND ");
        
		if(isOracle()){
			hql.append(" (to_number(to_char(htc.horarioInicial,'hh24mi')) < (to_number(to_char(htc.horarioFinal,'hh24mi'))) ) AND ");
	        hql.append(" to_number(to_char(:dthrPrevInicio,'hh24mi')) BETWEEN to_number(to_char(htc.horarioInicial,'hh24mi')) AND (to_number(to_char(htc.horarioFinal,'hh24mi')) -100)  ");
	        hql.append(" OR ");
	        hql.append(" to_number(to_char(htc.horarioInicial,'hh24mi')) > to_number(to_char(htc.horarioFinal,'hh24mi')) AND ");
	        hql.append(" ( to_number(to_char(:dthrPrevInicio,'hh24mi')) BETWEEN to_number(to_char(htc.horarioInicial,'hh24mi')) AND to_number(to_char(:valor1,'hh24mi')) OR  ");
	        hql.append(" to_number(to_char(:dthrPrevInicio,'hh24mi')) BETWEEN to_number(to_char(:valor2,'hh24mi')) AND ( to_number(to_char(htc.horarioFinal,'hh24mi')) -100 ) )   ");
		}else{
			hql.append(" (to_number(to_char(htc.horarioInicial,'hh24mi'),'9999') < (to_number(to_char(htc.horarioFinal,'hh24mi'),'9999')) ) AND ");
	        hql.append(" to_number(to_char(to_date(:dthrPrevInicio,'DD/MM/YYYY HH24:MI'),'hh24mi'),'9999') BETWEEN to_number(to_char(htc.horarioInicial,'hh24mi'),'9999') AND (to_number(to_char(htc.horarioFinal,'hh24mi'),'9999') -100)  ");
	        hql.append(" OR ");
	        hql.append(" to_number(to_char(htc.horarioInicial,'hh24mi'),'9999') > to_number(to_char(htc.horarioFinal,'hh24mi'),'9999') AND ");
	        hql.append(" ( to_number(to_char(to_date(:dthrPrevInicio,'DD/MM/YYYY HH24:MI'),'hh24mi'),'9999') BETWEEN to_number(to_char(htc.horarioInicial,'hh24mi'),'9999') AND to_number(to_char(to_date(:valor1,'DD/MM/YYYY HH24:MI'),'hh24mi'),'9999') OR  ");
	        hql.append(" to_number(to_char(to_date(:dthrPrevInicio,'DD/MM/YYYY HH24:MI'),'hh24mi'),'9999') BETWEEN to_number(to_char(to_date(:valor2,'DD/MM/YYYY HH24:MI'),'hh24mi'),'9999') AND ( to_number(to_char(htc.horarioFinal,'hh24mi'),'9999') -100 ) )   ");
		}
		
        hql.append(" ) ");
		
        Query query = createHibernateQuery(hql.toString());
        query.setParameter("unfSeq", unidadeFuncional.getSeq());
        query.setParameter("dthrPrevInicio", dthrPrevInicio);
        Calendar dateO1 = Calendar.getInstance();
        dateO1.setTime(new Date());
        Calendar dateAux = Calendar.getInstance();
        dateO1.set(Calendar.DAY_OF_MONTH, dateAux.get(Calendar.DAY_OF_MONTH));
		dateO1.set(Calendar.MONTH, dateAux.get(Calendar.MONTH));
		dateO1.set(Calendar.YEAR, dateAux.get(Calendar.YEAR));
		dateO1.set(Calendar.HOUR, 24);
		dateO1.set(Calendar.MINUTE, 0);
		
        query.setParameter("valor1", dateO1.getTime());
        
        dateO1.set(Calendar.HOUR, 0);
		dateO1.set(Calendar.MINUTE, 0);
        query.setParameter("valor2", dateO1.getTime());
        
        List<MbcHorarioTurnoCirg> resultado = query.list();  
        
        return resultado;
		
	}
	
	public List<Object[]> buscarSalasTurnosHorariosDisponiveisUnion1(MbcAgendas agenda) {
				
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");
		criteria.createAlias("htc." + MbcHorarioTurnoCirg.Fields.TURNO.toString(), "tur");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString()), MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString())
				.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString()), MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString())
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString()), MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString())
				.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString()), MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString())
				.add(Projections.property("tur." + MbcTurnos.Fields.TURNO.toString()), MbcTurnos.Fields.TURNO.toString()));
		   
		if(agenda.getProfAtuaUnidCirgs() != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), agenda.getProfAtuaUnidCirgs()));		
		}
		
		if(agenda.getEspecialidade() != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), agenda.getEspecialidade().getSeq()));
		}
		
		if (agenda.getSalaCirurgica() != null && agenda.getSalaCirurgica().getId() != null){
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), agenda.getSalaCirurgica().getId().getUnfSeq()));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_UNF_SEQ.toString(), agenda.getSalaCirurgica().getId().getUnfSeq()));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), agenda.getSalaCirurgica().getId().getSeqp()));
		}
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.FALSE));		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));				
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(agenda.getDtAgenda()))));
		
		// tirando as que foram cedidas para outras equipes @ORADB mbcc_ver_subst_bloq
		DetachedCriteria subCriteriaEscalaSalas = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ssl");
		subCriteriaEscalaSalas.setProjection(Projections.property(MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(agenda.getDtAgenda())));
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
		subCriteriaBloq.add(Restrictions.le("bsc." + MbcBloqSalaCirurgica.Fields.DT_INICIO.toString(), DateUtil.truncaData(agenda.getDtAgenda())));
		subCriteriaBloq.add(Restrictions.ge("bsc." + MbcBloqSalaCirurgica.Fields.DT_FIM.toString(), DateUtil.truncaData(agenda.getDtAgenda())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.DIA_SEMANA.toString(), "cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.DIA_SEMANA.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.TURNO_TURNO.toString(), "cas." + MbcCaracteristicaSalaCirg.Fields.TURNO_ID.toString()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.TURNO_TURNO.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), agenda.getProfAtuaUnidCirgs()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString())));
		subCriteriaBloq.add(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Subqueries.notExists(subCriteriaBloq));
		
		// tirando feriados
		DetachedCriteria subCriteriaFrd = DetachedCriteria.forClass(AghFeriados.class, "frd");
		subCriteriaFrd.setProjection(Projections.property(AghFeriados.Fields.DATA.toString()));
		
		// Restrição feita com sql nativo pois não é possível comparar Dominio com pojo MbcTurnos utilizando criteria
		subCriteriaFrd.add(Restrictions.or(Restrictions.sqlRestriction(" TURNO = HTC_TURNO "),
				Restrictions.isNull("frd." + AghFeriados.Fields.TURNO.toString())));
		subCriteriaFrd.add(Restrictions.eq("frd." + AghFeriados.Fields.DATA.toString(), DateUtil.truncaData(agenda.getDtAgenda())));
		criteria.add(Subqueries.notExists(subCriteriaFrd));
		
		return executeCriteria(criteria);
	}
	
	
	public List<Object[]> buscarSalasTurnosHorariosDisponiveisUnion2(MbcAgendas agenda) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaractSalaEsp.class, "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");
		criteria.createAlias("htc." + MbcHorarioTurnoCirg.Fields.TURNO.toString(), "tur");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_SUBST_ESCALA_SALAES.toString(), "ssl");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString()), MbcCaractSalaEsp.Fields.HORA_INICIO_EQUIPE.toString())
				.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString()), MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString())
				.add(Projections.property("cse." + MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString()), MbcCaractSalaEsp.Fields.HORA_FIM_EQUIPE.toString())
				.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString()), MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString())
				.add(Projections.property("tur." + MbcTurnos.Fields.TURNO.toString()), MbcTurnos.Fields.TURNO.toString()));
		
		if(agenda.getProfAtuaUnidCirgs() != null) {
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), agenda.getProfAtuaUnidCirgs()));
		}
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(agenda.getDtAgenda())));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.FALSE));
		
		if(agenda.getSalaCirurgica() != null && agenda.getSalaCirurgica().getId() != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), agenda.getSalaCirurgica().getId().getUnfSeq()));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_UNF_SEQ.toString(), agenda.getSalaCirurgica().getId().getUnfSeq()));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), agenda.getSalaCirurgica().getId().getSeqp()));
		}
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<Object[]> buscarSalasTurnosHorariosDisponiveisUnion3(MbcAgendas agenda) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), "sci");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS.toString(), "htc");
		criteria.createAlias("htc." + MbcHorarioTurnoCirg.Fields.TURNO.toString(), "tur");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString()), MbcHorarioTurnoCirg.Fields.HORARIO_FINAL.toString())
				.add(Projections.property("htc." + MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString()), MbcHorarioTurnoCirg.Fields.HORARIO_INICIAL.toString())
				.add(Projections.property("tur." + MbcTurnos.Fields.TURNO.toString()), MbcTurnos.Fields.TURNO.toString()));
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(agenda.getDtAgenda())));
		
		if(agenda.getProfAtuaUnidCirgs() != null) {
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), agenda.getProfAtuaUnidCirgs()));
		}
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		
		if(agenda.getSalaCirurgica() != null && agenda.getSalaCirurgica().getId() != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), agenda.getSalaCirurgica().getId().getUnfSeq()));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_UNF_SEQ.toString(), agenda.getSalaCirurgica().getId().getUnfSeq()));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), agenda.getSalaCirurgica().getId().getSeqp()));
		}
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}

	public MbcHorarioTurnoCirg buscarUltimoTurnoQueEquipePossuiCedenciaInstitucional(
			Short unfSeq, Date data, MbcSalaCirurgica sala,
			MbcProfAtuaUnidCirgs equipe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class, "htc");
		criteria.createAlias("htc." + MbcHorarioTurnoCirg.Fields.MBC_CARACTERISTICA_SALA_CIRGES.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		
		criteria.add(Restrictions.eq("htc." + MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), equipe));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), sala));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(data))));
		
		criteria.addOrder(OrderBySql.sql(" to_char(HORARIO_INICIAL, 'hh24:mi') desc "));
		
		List<MbcHorarioTurnoCirg> listaHorarios = executeCriteria(criteria); 
		if(!listaHorarios.isEmpty()) {
			return listaHorarios.get(0);
		} else {
			return null;
		}
	}

	public MbcHorarioTurnoCirg buscarUltimoTurnoQueEquipePossuiCedencia(
			Short unfSeq, Date data, MbcSalaCirurgica sala,
			MbcProfAtuaUnidCirgs equipe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class, "htc");
		criteria.createAlias("htc." + MbcHorarioTurnoCirg.Fields.MBC_CARACTERISTICA_SALA_CIRGES.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_SUBST_ESCALA_SALAES.toString(), "ssl");
		
		criteria.add(Restrictions.eq("htc." + MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(data))));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), sala));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), equipe));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(data)));
		
		criteria.addOrder(OrderBySql.sql(" to_char(HORARIO_INICIAL, 'hh24:mi') desc "));
		
		List<MbcHorarioTurnoCirg> listaHorarios = executeCriteria(criteria); 
		if(!listaHorarios.isEmpty()) {
			return listaHorarios.get(0);
		} else {
			return null;
		}
	}

	public MbcHorarioTurnoCirg buscarUltimoTurnoQueEquipePossuiReserva(
			Short unfSeq, Date data, MbcSalaCirurgica sala, MbcProfAtuaUnidCirgs equipe, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcHorarioTurnoCirg.class, "htc");
		criteria.createAlias("htc." + MbcHorarioTurnoCirg.Fields.MBC_CARACTERISTICA_SALA_CIRGES.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		
		criteria.add(Restrictions.eq("htc." + MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(data))));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_SALA_CIRURGICA.toString(), sala));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), equipe));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), espSeq));
		
		criteria.addOrder(OrderBySql.sql(" to_char(HORARIO_INICIAL, 'hh24:mi') desc "));
		
		List<MbcHorarioTurnoCirg> listaHorarios = executeCriteria(criteria); 
		if(!listaHorarios.isEmpty()) {
			return listaHorarios.get(0);
		} else {
			return null;
		}
	}
}