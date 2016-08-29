package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioAgendaUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteAgenda;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorUnidadeVO;
import br.gov.mec.aghu.exames.coleta.vo.AgendaExamesHorariosVO;
import br.gov.mec.aghu.exames.coleta.vo.GrupoExameVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AelHorarioExameDispDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelHorarioExameDisp> {

	private static final long serialVersionUID = 5879206230543134930L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelHorarioExameDisp.class);
    }

	/**
	 * Metodo para recuperar a entidade do banco para comparacao de valores antigos.<br>
	 * Objeto resultante nao atachado. NAO deve ser usado nos metodos merge, persist, etc do entitymanager.
	 * TODO Objeto populado parcialmente, setar os valores necessarios por demanda.<br>
	 * 
	 * @param id
	 * @return
	 */
	/*public AelHorarioExameDisp obterOriginal(AelHorarioExameDispId id) {
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelHorarioExameDisp.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelHorarioExameDisp.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AelHorarioExameDisp.Fields.SERVIDOR_ALTERADO.toString());
		hql.append(", o.").append(AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString());
		hql.append(", o.").append(AelHorarioExameDisp.Fields.TIPO_MARCACAO_EXAME.toString());
		hql.append(", o.").append(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString());
		
		hql.append(" from ").append(AelHorarioExameDisp.class.getSimpleName()).append(" o ");
		
		hql.append(" where o.").append(AelHorarioExameDisp.Fields.ID.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		
		AelHorarioExameDisp retorno = null;
		Object[] campos = null;
		@SuppressWarnings("rawtypes")
		List list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			campos = (Object[]) list.get(0);			
		}
		 
		if(campos != null) {
			retorno = new AelHorarioExameDisp();

			retorno.setId(id);
			retorno.setCriadoEm((Date) campos[0]);
			retorno.setServidor((RapServidores) campos[1]);
			retorno.setServidorAlterado((RapServidores) campos[2]);
			retorno.setGradeAgendaExame((AelGradeAgendaExame) campos[3]);
			retorno.setTipoMarcacaoExame((AelTipoMarcacaoExame) campos[4]);
			retorno.setSituacaoHorario((DominioSituacaoHorario) campos[5]);
			
		}		
		
		return retorno;
	}*/
	
	public AelHorarioExameDisp obterPorId(Short gaeUnfSeq, Integer gaeSeqp, Date dthrAgenda) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString(), gaeUnfSeq));
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_SEQP.toString(), gaeSeqp));
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString(), dthrAgenda));
		return (AelHorarioExameDisp) executeCriteriaUniqueResult(criteria);
	}



    public AelHorarioExameDisp obterHorarioExameDisponivel (Short gaeUnfSeq, Integer gaeSeqp, Date dthrAgenda) {
        DetachedCriteria criteria = obterCriteria();

        criteria.createAlias(AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString(), "GRADE");
        criteria.createAlias("GRADE." + AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), "SALA_EXECUTORA");

        criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString(), gaeUnfSeq));
        criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_SEQP.toString(), gaeSeqp));
        criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString(), dthrAgenda));
        return (AelHorarioExameDisp) executeCriteriaUniqueResult(criteria);
    }

    public DetachedCriteria montarCriteriaHorarioExameDisponibilidade(DominioSituacaoHorario filtroSituacao,
			Date filtroDtInicio, Date filtroDtFim, Boolean filtroHorariosFuturos, DominioDiaSemana filtroDiaSemana, 
			Date filtroHora, AelTipoMarcacaoExame filtroTipoMarcacao, Boolean filtroExtra, Boolean filtroExclusivo, 
			Short gaeUnfSeq, Integer gaeSeqp) {

		String aliasGradeAgenda = "gradeAgenda";
		String aliasTipoMarcacao = "tipoMarcacao";
		String separador = ".";
		DetachedCriteria criteria = obterCriteria();
		
		criteria.createAlias(AelHorarioExameDisp.Fields.SERVIDOR.toString(), "servidor");
		criteria.createAlias(AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString(), aliasGradeAgenda);
		criteria.createAlias(AelHorarioExameDisp.Fields.TIPO_MARCACAO_EXAME.toString(), "tipoMarcacao");
		criteria.createAlias(aliasGradeAgenda + separador + AelGradeAgendaExame.Fields.UNIDADE_FUNCIONAL.toString(), "unidFuncional");
		criteria.createAlias(aliasGradeAgenda + separador + AelGradeAgendaExame.Fields.EXAME.toString(), "unidFuncExecExame", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasGradeAgenda + separador + AelGradeAgendaExame.Fields.SERVIDOR_DIGITADO.toString(), "servidorDigitado");

		criteria.add(Restrictions.eq(aliasGradeAgenda + separador + AelGradeAgendaExame.Fields.UNF_SEQ, gaeUnfSeq));
		criteria.add(Restrictions.eq(aliasGradeAgenda + separador + AelGradeAgendaExame.Fields.SEQP.toString(), gaeSeqp));
		
		if (filtroSituacao != null) {
			criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString(), filtroSituacao));
		}
		
		if (filtroHorariosFuturos) {
			criteria.add(Restrictions.ge(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString(), new Date()));
		} else {
			if (filtroDtInicio != null && filtroDtFim != null) {
				criteria.add(Restrictions.between(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString(), 
						DateUtil.obterDataComHoraInical(filtroDtInicio), DateUtil.obterDataComHoraFinal(filtroDtFim)));
			} else if (filtroDtInicio != null && filtroDtFim == null) {
				criteria.add(Restrictions.ge(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString(), 
						DateUtil.obterDataComHoraInical(filtroDtInicio)));
			} else if (filtroDtInicio == null && filtroDtFim != null) {
				criteria.add(Restrictions.le(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString(), 
						DateUtil.obterDataComHoraFinal(filtroDtFim)));
			}
		}
		
		if (filtroTipoMarcacao != null) {
			criteria.add(Restrictions.eq(aliasTipoMarcacao + separador + AelTipoMarcacaoExame.Fields.SEQ.toString(),
					filtroTipoMarcacao.getSeq()));	
		}
		
		if (filtroExtra != null) {
			criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.IND_HORARIO_EXTRA.toString(), filtroExtra));	
		}
		
		if (filtroExclusivo != null) {
			criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.EXCLUSIVO_EXECUTOR.toString(), filtroExclusivo));	
		}
		
		return criteria;
	}

	public List<AelHorarioExameDisp> pesquisarHorarioExameDisponibilidade(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, DominioSituacaoHorario filtroSituacao, Date filtroDtInicio, 
			Date filtroDtFim, Boolean filtroHorariosFuturos, DominioDiaSemana filtroDiaSemana, Date filtroHora, 
			AelTipoMarcacaoExame filtroTipoMarcacao, Boolean filtroExtra, Boolean filtroExclusivo, Short gaeUnfSeq, 
			Integer gaeSeqp) {
		
		DetachedCriteria criteria = montarCriteriaHorarioExameDisponibilidade(filtroSituacao, filtroDtInicio, filtroDtFim,
				filtroHorariosFuturos, filtroDiaSemana, filtroHora, filtroTipoMarcacao, filtroExtra, filtroExclusivo, 
				gaeUnfSeq, gaeSeqp);
		
		List<AelHorarioExameDisp> lista = executeCriteria(criteria);
		
		// Filtros para dia e semana são realizados na lista de objetos retornada
		if (filtroDiaSemana != null || filtroHora != null) {
			lista = filtrarListaPorDiaSemanaHora(filtroDiaSemana, filtroHora, lista);
		}

		if (!lista.isEmpty()) {
			Comparator<AelHorarioExameDisp> crescente = new AelHorarioExameDispComparator();
			Collections.sort(lista, crescente);
			int indPrimeiro = firstResult;
			int indUltimo = firstResult + maxResult;
			int tamLista = lista.size();
			if (indUltimo > tamLista) {
				indUltimo = tamLista;
			}
			return lista.subList(indPrimeiro, indUltimo);
		}
		
		return lista;
	}

	public Long pesquisarHorarioExameDisponibilidadeCount(DominioSituacaoHorario filtroSituacao, Date filtroDtInicio, 
			Date filtroDtFim, Boolean filtroHorariosFuturos, DominioDiaSemana filtroDiaSemana, Date filtroHora, 
			AelTipoMarcacaoExame filtroTipoMarcacao, Boolean filtroExtra, Boolean filtroExclusivo, Short gaeUnfSeq, 
			Integer gaeSeqp) {
		
		DetachedCriteria criteria = montarCriteriaHorarioExameDisponibilidade(filtroSituacao, filtroDtInicio, filtroDtFim,
				filtroHorariosFuturos, filtroDiaSemana, filtroHora, filtroTipoMarcacao, filtroExtra, filtroExclusivo, 
				gaeUnfSeq, gaeSeqp);
		
		List<AelHorarioExameDisp> lista = executeCriteria(criteria);

		if (filtroDiaSemana != null || filtroHora != null) {
			lista = filtrarListaPorDiaSemanaHora(filtroDiaSemana, filtroHora, lista);
		}

		return (long) lista.size();
	}

	public List<AelHorarioExameDisp> pesquisarHorarioExameDisponibilidadeExcetoMarcadoExecutado(
			DominioSituacaoHorario filtroSituacao, Date filtroDtInicio, Date filtroDtFim, Boolean filtroHorariosFuturos, 
			DominioDiaSemana filtroDiaSemana, Date filtroHora, AelTipoMarcacaoExame filtroTipoMarcacao, 
			Boolean filtroExtra, Boolean filtroExclusivo, Short gaeUnfSeq, Integer gaeSeqp) {
		
		DetachedCriteria criteria = montarCriteriaHorarioExameDisponibilidade(filtroSituacao, filtroDtInicio, filtroDtFim,
				filtroHorariosFuturos, filtroDiaSemana, filtroHora, filtroTipoMarcacao, filtroExtra, filtroExclusivo, 
				gaeUnfSeq, gaeSeqp);
		
		List<DominioSituacaoHorario> listaSituacaoHorario = new ArrayList<DominioSituacaoHorario>();
		listaSituacaoHorario.add(DominioSituacaoHorario.M);
		listaSituacaoHorario.add(DominioSituacaoHorario.E);
		
		// Filtra por consultas geradas
		criteria.add(Restrictions.not(Restrictions.in(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString(), 
				listaSituacaoHorario.toArray())));

		List<AelHorarioExameDisp> lista = executeCriteria(criteria);

		if (filtroDiaSemana != null || filtroHora != null) {
			lista = filtrarListaPorDiaSemanaHora(filtroDiaSemana, filtroHora, lista);
		}
		
		return lista;
	}
	
	
	/**
	 * 
	 * @param filtroDiaExame
	 * @param filtroHoraExame
	 * @param lista
	 * @return Lista de resultados da pesquisa filtrada pelo dia da semana e
	 *         hora (hh:mm) informados
	 */
	private List<AelHorarioExameDisp> filtrarListaPorDiaSemanaHora(DominioDiaSemana filtroDiaExame, 
			Date filtroHoraExame, List<AelHorarioExameDisp> lista) {
		List<AelHorarioExameDisp> listaFiltrada = new ArrayList<AelHorarioExameDisp>();

		for (AelHorarioExameDisp horarioExame : lista) {
			Date dataHoraExame = horarioExame.getId().getDthrAgenda();
			Calendar calHoraExame = Calendar.getInstance();
			Calendar calFiltroHora = Calendar.getInstance();

			calHoraExame.setTime(dataHoraExame);

			if (filtroHoraExame != null) {
				calFiltroHora.setTime(filtroHoraExame);
			}

			int horaExame = calHoraExame.get(Calendar.HOUR_OF_DAY);
			int minutoExame = calHoraExame.get(Calendar.MINUTE);
			int filtroHora = calFiltroHora.get(Calendar.HOUR_OF_DAY);
			int filtroMinuto = calFiltroHora.get(Calendar.MINUTE);

			if (filtroDiaExame != null && filtroHoraExame != null && filtroDiaExame.equals(CoreUtil.retornaDiaSemana(dataHoraExame))
					&& horaExame == filtroHora && minutoExame == filtroMinuto) {
				listaFiltrada.add(horarioExame);
			} else if (filtroDiaExame != null && filtroHoraExame == null && filtroDiaExame.equals(CoreUtil.retornaDiaSemana(dataHoraExame))) {
				listaFiltrada.add(horarioExame);
			} else if (filtroDiaExame == null && filtroHoraExame != null && horaExame == filtroHora && minutoExame == filtroMinuto) {
				listaFiltrada.add(horarioExame);
			}
		}
		
		return listaFiltrada;
	}
	
	/**
	 * Comparator para ordenar pesquisa dos horários por atributo dthrAgenda.
	 * 
	 * @author diego.pacheco
	 * 
	 */
	class AelHorarioExameDispComparator implements Comparator<AelHorarioExameDisp> {
		@Override
		public int compare(AelHorarioExameDisp h1, AelHorarioExameDisp h2) {
			return h1.getId().getDthrAgenda().compareTo(h2.getId().getDthrAgenda());
		}
	}
	
	public Long obterCountHorarioExameDispTipoMarcacaoAtivaPorGrade(Short unfSeq, Integer seqp) {
		String aliasTipoMarcacao = "tipoMarcacao";
		String separador = ".";
		
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelHorarioExameDisp.Fields.TIPO_MARCACAO_EXAME.toString(), aliasTipoMarcacao);
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(aliasTipoMarcacao + separador 
				+ AelTipoMarcacaoExame.Fields.IND_SITUACAO, DominioSituacao.A));
		
		return executeCriteriaCount(criteria);
	}
	
	public Date obterHorarioExameDataMaisRecentePorGrade(Short unfSeq, Integer seqp) {
		DetachedCriteria criteria = obterCriteria();
		criteria.setProjection(Projections.max(AelHorarioExameDisp.Fields.ID_DTHR_AGENDA.toString()));
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_SEQP.toString(), seqp));
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelHorarioExameDisp> buscarOutrosHorariosDisponiveisNoDia(AelHorarioExameDisp horarioExameDispBase){
		Date dataLimite = DateUtil.truncaData(horarioExameDispBase.getId().getDthrAgenda());
		dataLimite = DateUtil.adicionaDias(dataLimite, 1);
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString(), horarioExameDispBase.getId().getGaeUnfSeq()));
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.GAE_SEQP.toString(), horarioExameDispBase.getId().getGaeSeqp()));
		criteria.add(Restrictions.ge(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString(), horarioExameDispBase.getId().getDthrAgenda()));
		criteria.add(Restrictions.le(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString(), dataLimite));
		
		criteria.addOrder(Order.asc(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()));
		
		return executeCriteria(criteria);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<AgendaExamesHorariosVO> pesquisarAgendaExamesHorarios(Date dtAgenda, DominioSituacaoHorario situacaoHorario, Short unfSeq, Short seqP, RapServidoresId rapServidoresId) {
		
		String aliasSoe = "soe";
		String aliasIse = "ise";
		String aliasIha = "iha";
		String aliasHed = "hed";
		String aliasAtv = "atv";
		String aliasAtd = "atd";
		String aliasGae = "gae";
		String separador = ".";
		
		StringBuffer hql = new StringBuffer(400);
		hql.append("select distinct (");
		hql.append(aliasSoe).append(separador).append(AelSolicitacaoExames.Fields.SEQ.toString()).append(')');
		hql.append(",	").append(aliasSoe).append(separador).append(AelSolicitacaoExames.Fields.ATD_SEQ.toString());
		hql.append(",	").append(aliasSoe).append(separador).append(AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO_SEQ.toString());
		hql.append(",	").append(aliasIha).append(separador).append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString());
		hql.append(",	").append(aliasIha).append(separador).append(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString());
		hql.append(",	").append(aliasIha).append(separador).append(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString());
		hql.append(",	").append(aliasHed).append(separador).append(AelHorarioExameDisp.Fields.TIPO_MARCACAO_EXAME_SEQ.toString());
		hql.append(",	").append(aliasHed).append(separador).append(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString());
		hql.append(",	").append(aliasHed).append(separador).append(AelHorarioExameDisp.Fields.IND_HORARIO_EXTRA.toString());
		hql.append(",	").append(aliasAtv).append(separador).append(AelAtendimentoDiversos.Fields.PAC_CODIGO.toString());
		hql.append(",	").append(aliasAtd).append(separador).append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		hql.append(",	").append(aliasAtv).append(separador).append(AelAtendimentoDiversos.Fields.PRONTUARIO.toString());
		hql.append(",	").append(aliasAtd).append(separador).append(AghAtendimentos.Fields.PRONTUARIO.toString());
		hql.append(",	").append(aliasAtv).append(separador).append(AelAtendimentoDiversos.Fields.NOME_PACIENTE.toString());
		hql.append(" from AelSolicitacaoExames ").append(aliasSoe);
		hql.append("	inner join ").append(aliasSoe).append(separador).append(AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString()).append(' ').append(aliasIse);
		hql.append("	inner join ").append(aliasIse).append(separador).append(AelItemSolicitacaoExames.Fields.ITEM_HORARIO_AGENDADO.toString()).append(' ').append(aliasIha);
		hql.append("	inner join ").append(aliasIha).append(separador).append(AelItemHorarioAgendado.Fields.HORARIO_EXAME_DISPONIVEL.toString()).append(' ').append(aliasHed);
		hql.append("	left join ").append(aliasSoe).append(separador).append(AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString()).append(' ').append(aliasAtv);
		hql.append("	left join ").append(aliasSoe).append(separador).append(AelSolicitacaoExames.Fields.ATENDIMENTO.toString()).append(' ').append(aliasAtd);
		hql.append(" where ");
		hql.append(aliasHed).append(separador).append(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString()).append(" in (:situacaoHorarioM, :situacaoHorarioE) ");
		hql.append("	and ").append(aliasHed).append(separador).append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()).append(" BETWEEN :dataInicial and :dataFinal ");
		if(situacaoHorario != null) {
			hql.append("	and ").append(aliasHed).append(separador).append(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString()).append(" = :situacaoHorario ");
		}
		if(unfSeq != null) {
			hql.append("	and ").append(aliasIha).append(separador).append(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()).append(" = :unfSeq ");
		}
		hql.append("	and exists (select 1 from AelGradeAgendaExame ").append(aliasGae);
		hql.append("	where ").append(aliasGae).append(separador).append(AelGradeAgendaExame.Fields.UNF_SEQ).append(" = ").append(aliasIha).append(separador).append(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ);
		hql.append("		and ").append(aliasGae).append(separador).append(AelGradeAgendaExame.Fields.SEQP).append(" = ").append(aliasIha).append(separador).append(AelItemHorarioAgendado.Fields.HED_GAE_SEQP);
		if(unfSeq != null) {
			hql.append("	and ").append(aliasGae).append(separador).append(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES_UNF_SEQ).append(" = :unfSeq ");
		}
		if(seqP != null) {
			hql.append("	and ").append(aliasGae).append(separador).append(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES_SEQ).append(" = :seqP ");
		}
		if(rapServidoresId != null && rapServidoresId.getMatricula() != null && rapServidoresId.getVinCodigo() != null) {
			hql.append("	and ").append(aliasGae).append(separador).append(AelGradeAgendaExame.Fields.SERVIDOR_ID.toString()).append(" = :rapServidoresId ");
		}
		hql.append("		)");
		hql.append(" order by ").append(aliasIha).append(separador).append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString());
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("situacaoHorarioM", DominioSituacaoHorario.M);
		query.setParameter("situacaoHorarioE", DominioSituacaoHorario.E);
		query.setParameter("dataInicial", DateUtil.obterDataComHoraInical(dtAgenda));
		query.setParameter("dataFinal", DateUtil.obterDataComHoraFinal(dtAgenda));
		if(situacaoHorario != null) {
			query.setParameter("situacaoHorario", situacaoHorario);
		}
		if(unfSeq != null) {
			query.setParameter("unfSeq", unfSeq);
		}
		if(seqP != null) {
			query.setParameter("seqP", seqP);
		}
		if(rapServidoresId != null && rapServidoresId.getMatricula() != null && rapServidoresId.getVinCodigo() != null) {
			query.setParameter("rapServidoresId", rapServidoresId);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> listaArrayObject = query.list();
		List<AgendaExamesHorariosVO> listaAgendaExamesHorariosVO = new ArrayList<AgendaExamesHorariosVO>();
		
		Short seqVO = 0;
		for (Object[] arrayObject : listaArrayObject) {
			AgendaExamesHorariosVO agendaExameHorariosVO = new AgendaExamesHorariosVO();
			agendaExameHorariosVO.setSeqVO(seqVO++);
			agendaExameHorariosVO.setSoeSeq((Integer) arrayObject[0]);
			agendaExameHorariosVO.setAtdSeq((Integer) arrayObject[1]);
			agendaExameHorariosVO.setAtvSeq((Integer) arrayObject[2]);
			agendaExameHorariosVO.setHedDthrAgenda((Date) arrayObject[3]);
			agendaExameHorariosVO.setHedGaeUnfSeq((Short) arrayObject[4]);
			agendaExameHorariosVO.setHedGaeSeqp((Integer) arrayObject[5]);
			agendaExameHorariosVO.setTmeSeq((Short) arrayObject[6]);
			agendaExameHorariosVO.setHedSituacaoHorario((DominioSituacaoHorario) arrayObject[7]);
			agendaExameHorariosVO.setHedIndHorarioExtra((Boolean) arrayObject[8]);
			if((Integer) arrayObject[1] == null) {
				agendaExameHorariosVO.setPacCodigo((Integer) arrayObject[9]);
				agendaExameHorariosVO.setProntuario((Integer) arrayObject[11]);
				agendaExameHorariosVO.setNomePaciente((String) arrayObject[13]);
			} else {
				agendaExameHorariosVO.setPacCodigo((Integer) arrayObject[10]);
				agendaExameHorariosVO.setProntuario((Integer) arrayObject[12]);
			}
			listaAgendaExamesHorariosVO.add(agendaExameHorariosVO);
		}
		return listaAgendaExamesHorariosVO;
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<AelHorarioExameDisp> pesquisarHorarioExameTransferenciaAgendamento(Date dataReativacao, Short tipo1, Short tipo2, String sigla, Integer manSeq, Short unfSeq, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor) {
		
		StringBuffer hql = new StringBuffer(600);
		hql.append("select hed from AelHorarioExameDisp hed");
		hql.append(" join hed.").append(AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString());
		hql.append(" as gae ");
		
		hql.append(" join hed.").append(AelHorarioExameDisp.Fields.SERVIDOR.toString());
		hql.append(" as ser ");
		
		hql.append(" left join gae.").append(AelGradeAgendaExame.Fields.GRUPO_EXAME.toString());
		hql.append(" as gex ");
		
		hql.append(" left join gae.").append(AelGradeAgendaExame.Fields.EXAME.toString());
		hql.append(" as ufe ");
		
		hql.append(" left join gae.").append(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString());
		hql.append(" as see ");

		hql.append(" where hed."+AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString()+" = :situacaoHorario");
		hql.append(" and hed."+AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()+" > :data");
		hql.append(" and hed."+AelHorarioExameDisp.Fields.TIPO_MARCACAO_EXAME_SEQ.toString()+" in(:tipo1,:tipo2)");
		hql.append(" and gae."+AelGradeAgendaExame.Fields.IND_SITUACAO.toString()+" =:situacaoGrade ");
		
		
		if(grade != null) {
			hql.append(" and gae.");
			hql.append(AelGradeAgendaExame.Fields.SEQP.toString());
			hql.append(" = :grade ");
		} 

        if(grupoExame!=null){
        	hql.append(" and gex.");
			hql.append(AelGrupoExames.Fields.SEQ.toString());
			hql.append(" = :seqGrupoExame ");
		}
        if(salaExecutoraExame!=null){
        	hql.append(" and see.");
			hql.append(AelSalasExecutorasExames.Fields.NUMERO.toString());
			hql.append(" = :salaNumero ");
		}
        
    	if(servidor!=null){
    		hql.append(" and ser.");
			hql.append(RapServidores.Fields.CODIGO_VINCULO.toString());
			hql.append(" = :vinCodigo ");
			hql.append(" and ser.");
			hql.append(RapServidores.Fields.MATRICULA.toString());
			hql.append(" = :matricula ");
		}
    	
    	 if(data != null) {
 			hql.append(" and hed.");
 			hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
 			hql.append(" >= :dataInicio ");
 			hql.append("and hed.");
 			hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
 			hql.append(" <= :dataFim ");
 		} 
         
         if(hora!=null){
         	hql.append(" and TO_NUMBER(TO_CHAR(hed.");
 			hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()).append(", 'HH24'))");
 			hql.append(" = :hora ");
 			hql.append(" and TO_NUMBER(TO_CHAR(hed.");
 			hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()).append(", 'MI'))");
 			hql.append(" = :minuto ");
 		}
		
		
		
		hql.append(" and ((ufe."+AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString()+" = :sigla ");
		hql.append(" and ufe."+AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString()+" = :manSeq ");
		hql.append(" and ufe."+AelUnfExecutaExames.Fields.UNF_SEQ.toString()+" = :unfSeq) ");	
		hql.append(" or exists (select 1 from AelGrupoExameUnidExame geu ");
		hql.append(" left join geu.").append(AelGrupoExameUnidExame.Fields.UNF_EXECUTA_EXAME.toString());
		hql.append(" as ufe1 ");
		hql.append(" where ufe1."+AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString()+" = :sigla ");
		hql.append(" and ufe1."+AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString()+" = :manSeq ");
		hql.append(" and ufe1."+AelUnfExecutaExames.Fields.UNF_SEQ.toString()+" = :unfSeq ");
		hql.append(" and geu."+AelGrupoExameUnidExame.Fields.GEX_SEQ.toString()+" = gex."+AelGrupoExames.Fields.SEQ.toString()+"))");
		hql.append(" order by hed.").append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
        
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("situacaoGrade", DominioSituacao.A);
		query.setParameter("situacaoHorario", DominioSituacaoHorario.L);
		if(dataReativacao==null){
			query.setTimestamp("data", new Date());	
		} else if(DateUtil.validaDataMaior(dataReativacao, new Date())){
			query.setTimestamp("data", dataReativacao);	
		} else {
			query.setTimestamp("data", new Date());
		}
		
		query.setParameter("tipo1", tipo1);
		query.setParameter("tipo2", tipo2);
		query.setParameter("manSeq", manSeq);
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("sigla", sigla);
		
		
		
		if(grade != null) {
			query.setParameter("grade", grade);
		} 

        if(grupoExame!=null){
    		query.setParameter("seqGrupoExame", grupoExame.getSeq());
		}

        if(salaExecutoraExame!=null){
        	query.setParameter("salaNumero", salaExecutoraExame.getNumero());
		}
        
    	if(servidor!=null){
    		query.setParameter("vinCodigo", servidor.getId().getVinCodigo());
    		query.setParameter("matricula", servidor.getId().getMatricula());
		}
    	
    	if(data!=null){
			query.setParameter("dataInicio", DateUtil.truncaData(data));
			query.setParameter("dataFim", DateUtil.truncaDataFim(data));
		}
		if(hora!=null){
			Calendar calHora = Calendar.getInstance();
			calHora.setTime(hora);
			int numHora = calHora.get(Calendar.HOUR_OF_DAY);
			int numMinuto = calHora.get(Calendar.MINUTE);
			query.setParameter("hora", numHora);
			query.setParameter("minuto", numMinuto);
		}
		
		
		
		query.setMaxResults(600);
		return query.list();
	}
	

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<AelHorarioExameDisp> pesquisarHorarioGrupoExameTransferenciaAgendamento(Date dataHoraReativacao, Short tipo1, Short tipo2, List<GrupoExameVO> listaGrupoExame, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor){
		StringBuffer hql = new StringBuffer(400);
		hql.append("select hed from AelHorarioExameDisp hed");
		hql.append(" join hed.").append(AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString());
		hql.append(" as gae ");
		
		hql.append(" join hed.").append(AelHorarioExameDisp.Fields.SERVIDOR.toString());
		hql.append(" as ser ");
		
		hql.append(" join gae.").append(AelGradeAgendaExame.Fields.GRUPO_EXAME.toString());
		hql.append(" as gex ");
		
		hql.append(" left join gae.").append(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString());
		hql.append(" as see ");

		hql.append(" where hed."+AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString()+" = :situacaoHorario");
		hql.append(" and hed."+AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()+" > :data");
		hql.append(" and hed."+AelHorarioExameDisp.Fields.TIPO_MARCACAO_EXAME_SEQ.toString()+" in(:tipo1,:tipo2)");
		hql.append(" and gae."+AelGradeAgendaExame.Fields.IND_SITUACAO.toString()+" =:situacaoGrade ");
		
		for(int i=0; i<listaGrupoExame.size();i++){
			if(i==0){
				hql.append(" and ( ");	
			} else {
				hql.append(" or ");
			}
			hql.append(" (gae."+AelGradeAgendaExame.Fields.GEX_SEQ.toString()+" = :gexSeq"+i);
			hql.append(" and gae."+AelGradeAgendaExame.Fields.UNF_SEQ.toString()+" = :unfSeq"+i+")");
		}
		hql.append(" ) ");
		
		if(grade != null) {
			hql.append(" and gae.");
			hql.append(AelGradeAgendaExame.Fields.SEQP.toString());
			hql.append(" = :grade ");
		} 

        if(grupoExame!=null){
        	hql.append(" and gex.");
			hql.append(AelGrupoExames.Fields.SEQ.toString());
			hql.append(" = :seqGrupoExame ");
		}
        if(salaExecutoraExame!=null){
        	hql.append(" and see.");
			hql.append(AelSalasExecutorasExames.Fields.NUMERO.toString());
			hql.append(" = :salaNumero ");
		}
        
    	if(servidor!=null){
    		hql.append(" and ser.");
			hql.append(RapServidores.Fields.CODIGO_VINCULO.toString());
			hql.append(" = :vinCodigo ");
			hql.append(" and ser.");
			hql.append(RapServidores.Fields.MATRICULA.toString());
			hql.append(" = :matricula ");
		}
    	
    	 if(data != null) {
 			hql.append(" and hed.");
 			hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
 			hql.append(" >= :dataInicio ");
 			hql.append("and hed.");
 			hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
 			hql.append(" <= :dataFim ");
 		} 
         
         if(hora!=null){
         	hql.append(" and hour(hed.");
 			hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()).append(')');
 			hql.append(" = :hora ");
 			hql.append(" and minute(hed.");
 			hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()).append(')');
 			hql.append(" = :minuto ");
 		}
		
		
		hql.append(" order by hed.").append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
        
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("situacaoGrade", DominioSituacao.A);
		query.setParameter("situacaoHorario", DominioSituacaoHorario.L);
		if(dataHoraReativacao==null){
			query.setTimestamp("data", new Date());	
		} else if(DateUtil.validaDataMaior(dataHoraReativacao, new Date())){
			query.setTimestamp("data", dataHoraReativacao);	
		} else {
			query.setTimestamp("data", new Date());
		}
		
		query.setParameter("tipo1", tipo1);
		query.setParameter("tipo2", tipo2);
		
		
		if(grade != null) {
			query.setParameter("grade", grade);
		} 

        if(grupoExame!=null){
    		query.setParameter("seqGrupoExame", grupoExame.getSeq());
		}

        if(salaExecutoraExame!=null){
        	query.setParameter("salaNumero", salaExecutoraExame.getNumero());
		}
        
    	if(servidor!=null){
    		query.setParameter("vinCodigo", servidor.getId().getVinCodigo());
    		query.setParameter("matricula", servidor.getId().getMatricula());
		}
    	
    	if(data!=null){
			query.setParameter("dataInicio", DateUtil.truncaData(data));
			query.setParameter("dataFim", DateUtil.truncaDataFim(data));
		}
		if(hora!=null){
			Calendar calHora = Calendar.getInstance();
			calHora.setTime(hora);
			int numHora = calHora.get(Calendar.HOUR_OF_DAY);
			int numMinuto = calHora.get(Calendar.MINUTE);
			query.setParameter("hora", numHora);
			query.setParameter("minuto", numMinuto);
		}
		
		for(int i=0; i<listaGrupoExame.size();i++){
			query.setParameter("gexSeq"+i, listaGrupoExame.get(i).getSeq());
			query.setParameter("unfSeq"+i, listaGrupoExame.get(i).getUnfSeq());
		}
		
		query.setMaxResults(600);
		return query.list();
	}
	
	/**
	 * Busca as Agendas Por Unidade
	 * @param {AghUnidadesFuncionais} unidadeExecutora
	 * @param {Date} dtInicio
	 * @param {Date} dtFim
	 * @param {DominioOrigemPacienteAgenda} origem
	 * @param {DominioOrdenacaoRelatorioAgendaUnidade} ordenacao
	 * @return List<RelatorioAgendaPorSalaVO>
	 */
	/*
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<RelatorioAgendaPorUnidadeVO> obterAgendaPorUnidade(AghUnidadesFuncionais unidadeExecutora, Date dtInicio, Date dtFim, 
			DominioOrigemPacienteAgenda origem, DominioOrdenacaoRelatorioAgendaUnidade ordenacao, List<Short> listUnfSeqHierarquico) {
		
		Date horarioInicial = DateUtil.obterDataComHoraInical(dtInicio);
		Date horarioFinal;
		
		if(dtFim==null) {
			horarioFinal = DateUtil.obterDataComHoraFinal(dtInicio);
		}
		else {
			horarioFinal = DateUtil.obterDataComHoraFinal(dtFim);
		}
		
		StringBuffer hql = new StringBuffer(800);
		hql.append("SELECT new br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorUnidadeVO("); 	
		hql.append("	hed.id.dthrAgenda, ");
		hql.append("	see.numero, ");
		hql.append("	pac.prontuario, ");
		hql.append("	pac.nome, ");
		hql.append("	ise.id.soeSeq, ");
		hql.append("	exa.descricao, ");
		hql.append("	ise.indUsoO2, ");
		hql.append("	ise.tipoTransporte");
		hql.append(") FROM ").append(AelItemHorarioAgendado.class.getSimpleName()).append(" iha, ");
		hql.append(AelExames.class.getSimpleName()).append(" exa, ");
		hql.append(AipPacientes.class.getSimpleName()).append(" pac, ");
		hql.append(VAelSolicAtends.class.getSimpleName()).append(" vas ");
//		hql.append(AelExames.class.getSimpleName()).append(" exa1 ");
		
		hql.append(" JOIN iha.").append(AelItemHorarioAgendado.Fields.HORARIO_EXAME_DISPONIVEL.toString());
		hql.append(" AS hed ");
		
		hql.append(" JOIN iha.").append(AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString());
		hql.append(" AS ise ");
		
		hql.append(" JOIN hed.").append(AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString());
		hql.append(" AS gae ");
		
		hql.append(" LEFT JOIN gae.").append(AelGradeAgendaExame.Fields.GRUPO_EXAME.toString());
		hql.append(" AS gex ");
		
		hql.append(" LEFT JOIN gae.").append(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString());
		hql.append(" AS see ");

		
		hql.append(" LEFT JOIN gae.").append(AelGradeAgendaExame.Fields.AELEXAMES.toString());
		hql.append(" AS exa1 ");
		
		
		hql.append(" WHERE ");
		
		hql.append(" hed.").append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
		hql.append(" BETWEEN :horarioInicial AND :horarioFinal ");
		
		hql.append(" AND ( ");
		if(!listUnfSeqHierarquico.isEmpty()) {
			hql.append(" hed.").append(AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString());
			hql.append(" IN ( SELECT uf.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString());
			hql.append(" FROM ").append(AghUnidadesFuncionais.class.getSimpleName()).append(" uf ");
			hql.append(" WHERE uf.").append(AghUnidadesFuncionais.Fields.UNF_SEQ.toString());
			hql.append(" IN ( :listaUnfHierarquico ))");
			hql.append(" OR ");
		}
		hql.append(" hed.").append(AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString());
		hql.append(" = :seqUnidadeExecutora )");
		
		hql.append(" AND iha.").append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString());
		hql.append(" IN ( SELECT MIN(ihax.").append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()).append(") ");
		hql.append(" FROM ").append(AelItemHorarioAgendado.class.getSimpleName()).append(" ihax ");
		hql.append(" WHERE ihax.").append(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = iha.").append(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString());
		hql.append(" AND ihax.").append(AelItemHorarioAgendado.Fields.ISE_SEQP.toString());
		hql.append(" = iha.").append(AelItemHorarioAgendado.Fields.ISE_SEQP.toString());
		hql.append(" ) ");
		
		hql.append(" AND ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append(" = :sitCodigoAG ");
		
		hql.append(" AND vas.").append(VAelSolicAtends.Fields.SEQ.toString());
		hql.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		
		hql.append(" AND pac.").append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = vas.").append(VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString());
		
		hql.append(" AND exa.").append(AelExames.Fields.SIGLA.toString());
		hql.append(" = ise.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString());
		
		//AND EXA1.sigla (+) = GAE.ufe_ema_exa_sigla 
//		hql.append(" AND exa1.").append(AelExames.Fields.SIGLA.toString());
//		hql.append(" = gae.").append(AelGradeAgendaExame.Fields.UFE_EMA_EXA_SIGLA.toString());
		
		hql.append(" AND ( CASE WHEN vas.").append(VAelSolicAtends.Fields.ORIGEM.toString()).append(" = 'X' THEN 'A' ");
		hql.append("            ELSE vas.").append(VAelSolicAtends.Fields.ORIGEM.toString());
		hql.append(" 	  END ");
		hql.append(" ) IN ( :origemList ) ");
		
		hql.append(" ORDER BY ");
		if(ordenacao!=null && ordenacao.equals(DominioOrdenacaoRelatorioAgendaUnidade.UNIDADE)) {
				hql.append(" vas.").append(VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString());
				hql.append(" , ");
		}
		hql.append(" iha.").append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString());
		hql.append(", pac.").append(AipPacientes.Fields.NOME.toString());
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(", exa.").append(AelExames.Fields.DESCRICAO_USUAL.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setTimestamp("horarioInicial", horarioInicial);
		query.setTimestamp("horarioFinal", horarioFinal);
		query.setShort("seqUnidadeExecutora", unidadeExecutora.getSeq());
		query.setString("sitCodigoAG",DominioSituacaoItemSolicitacaoExame.AG.toString());
		if(origem.equals(DominioOrigemPacienteAgenda.AI)) {
			query.setParameterList("origemList", new String[]{DominioOrigemPacienteAgenda.A.toString(), DominioOrigemPacienteAgenda.I.toString()});
		}
		else {
			query.setParameterList("origemList", new String[]{origem.toString()});
		}
		if(!listUnfSeqHierarquico.isEmpty()) {
			query.setParameterList("listaUnfHierarquico", listUnfSeqHierarquico);
		}
		
		return query.list();
	}
	*/
	/**
	 * Busca as Agendas Por Unidade
	 * @param {AghUnidadesFuncionais} unidadeExecutora
	 * @param {Date} dtInicio
	 * @param {Date} dtFim
	 * @param {DominioOrigemPacienteAgenda} origem
	 * @param {DominioOrdenacaoRelatorioAgendaUnidade} ordenacao
	 * @return List<RelatorioAgendaPorSalaVO>
	 */
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<RelatorioAgendaPorUnidadeVO> obterAgendaPorUnidade(AghUnidadesFuncionais unidadeExecutora, Date dtInicio, Date dtFim, 
			DominioOrigemPacienteAgenda origem, DominioOrdenacaoRelatorioAgendaUnidade ordenacao, List<Short> listUnfSeqHierarquico) {
		
		Date horarioInicial = DateUtil.obterDataComHoraInical(dtInicio);
		Date horarioFinal;
		
		if(dtFim==null) {
			horarioFinal = DateUtil.obterDataComHoraFinal(dtInicio);
		}
		else {
			horarioFinal = DateUtil.obterDataComHoraFinal(dtFim);
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class,"iha");

		//*Join com horarios agendados
		criteria.createAlias("iha."+AelItemHorarioAgendado.Fields.HORARIO_EXAME_DISPONIVEL.toString(), "hed", JoinType.INNER_JOIN);
		criteria.createAlias("hed."+AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString(), "gae", JoinType.INNER_JOIN);
		criteria.createAlias("gae."+AelGradeAgendaExame.Fields.GRUPO_EXAME.toString(), "gex", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("gae."+AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), "see", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("iha."+AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise", JoinType.INNER_JOIN);

		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(),"exa", JoinType.INNER_JOIN);

		//*Paciente*
		//*Os próximos 3 createAlias equivalem a V_AEL_SOLIC_ATENDS
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		//*Atendimento
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		//*Atendimento Diverso
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "atv", JoinType.LEFT_OUTER_JOIN);
		//*convenio
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atv."+AelAtendimentoDiversos.Fields.PACIENTE.toString(), "pacDiv", JoinType.LEFT_OUTER_JOIN);
		//*****************************************************************************************************************

		//*******Projections*******
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("hed."+AelHorarioExameDisp.Fields.DTHR_AGENDA.toString()), "dthr_agenda")
				.add(Projections.property("see."+AelSalasExecutorasExames.Fields.NUMERO.toString()), "numero_sala")
								.add(Projections.sqlProjection(	" (	case	when soe7_.atd_seq is not null then"
														+ "				case	when pac10_.PRONTUARIO is not null then"
														+ "					pac10_.prontuario"
														+ "				else"
														+ "					atd8_.prontuario"
														+ "				end"
														+ "			when soe7_.atv_seq is not null then"
														+ "				case	when pacdiv11_.PRONTUARIO is not null then"
														+ "					pacdiv11_.prontuario"
														+ "				else " 
														+ "					atv9_.prontuario"
														+ "				end" + "	end) as prontuario",
												new String[] { "prontuario" },
												new Type[] { IntegerType.INSTANCE }), "prontuario")

				.add(Projections.sqlProjection(	" (	case	when soe7_.atd_seq is not null then"
														+ "				pac10_.nome"
														+ "			when soe7_.atv_seq is not null then"
														+ "				pacdiv11_.nome"
														+ "	end) as nome_paciente",
												new String[] { "nome_paciente" },
												new Type[] { StringType.INSTANCE }), "nome_paciente")
				.add(Projections.property("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), "solicitacao")
				.add(Projections.property("exa."+AelExames.Fields.DESCRICAO.toString()), "exame")
				.add(Projections.property("ise."+AelItemSolicitacaoExames.Fields.USO_O2.toString()), "uso_o2")
				.add(Projections.property("ise."+AelItemSolicitacaoExames.Fields.TIPO_TRANSPORTE.toString()), "tipo_transporte")
				.add(Projections.property("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString()), "atendimento"));

		//*******Restrictions*******
		criteria.add(Restrictions.eq("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString() , DominioSituacaoItemSolicitacaoExame.AG.toString()));

		
		//final String sql = " case 	when atd8_.origem = 'X' then 'A' else atd8_.origem end in (?)";
		//criteria.add(Restrictions.sqlRestriction(sql, values, new Type[]{StringType.INSTANCE})) ;
		//consulta acima foi substituida pelo criteria abaixo
		
		// quando origem for diferente de A ou I, filtra todas
		if(origem != null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM.toString(), origem));
		}
		criteria.add(Restrictions.between("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), horarioInicial, horarioFinal));
		
		//equivalente a connect by prior do oracle
		//Filtro pela unidade EXECUTORA e seus pais
		Criterion crit1 = Restrictions.eq("hed."+AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString() , unidadeExecutora.getSeq());
		
		if(!listUnfSeqHierarquico.isEmpty()) {
			final String sqlUnf = "hed1_.GAE_UNF_SEQ in (select unf.seq from "+ AghUnidadesFuncionais.class.getAnnotation(javax.persistence.Table.class).schema()+"."+
																				AghUnidadesFuncionais.class.getAnnotation(javax.persistence.Table.class).name() + " unf where unf.unf_seq in(?))";
			Object[] valuesCrit2 = new Object[listUnfSeqHierarquico.size()];
			
			for (int i = 0; i < listUnfSeqHierarquico.size(); i++) {
				valuesCrit2[i]=listUnfSeqHierarquico.get(i);
			}

			Criterion crit2 = Restrictions.sqlRestriction(sqlUnf, valuesCrit2, new Type[]{ShortType.INSTANCE});
			criteria.add(Restrictions.or(crit1, crit2));
		}else{
			criteria.add(crit1);
		}
	
		StringBuilder sqlData = new StringBuilder(200);
		sqlData.append("{alias}.HED_DTHR_AGENDA in (	select 	MIN(iha2.HED_DTHR_AGENDA)")
		.append("								from	").append(AelItemHorarioAgendado.class.getAnnotation(javax.persistence.Table.class).schema()).append('.').append(
																AelItemHorarioAgendado.class.getAnnotation(javax.persistence.Table.class).name() ).append( " iha2")
		.append("								where	iha2.ISE_SOE_SEQ	=	{alias}.ISE_SOE_SEQ")
		.append("								and 	iha2.ISE_SEQP		=	{alias}.ISE_SEQP)");

		criteria.add(Restrictions.sqlRestriction(sqlData.toString()));
		
		//*******Ordenação*******
		if(ordenacao!=null && ordenacao.equals(DominioOrdenacaoRelatorioAgendaUnidade.UNIDADE)) {
			criteria.addOrder(Order.asc("atd."+AghAtendimentos.Fields.LTO_LTO_ID.toString()));
		}
		
		criteria.addOrder(Order.asc("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()));

		criteria.addOrder(Order.asc("pac."+AipPacientes.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("exa."+AelExames.Fields.DESCRICAO_USUAL.toString()));
		
		List<RelatorioAgendaPorUnidadeVO> listReturn = new ArrayList<RelatorioAgendaPorUnidadeVO>();
		List<Object[]> results = executeCriteria(criteria);
		
		for (Object[] result : results) {

			RelatorioAgendaPorUnidadeVO itemResul = new RelatorioAgendaPorUnidadeVO();
			
			itemResul.setHora((Date)result[0]);
			itemResul.setSala((String)result[1]);
			itemResul.setProntuario((Integer)result[2]);
			itemResul.setNomePaciente((String)result[3]);
			itemResul.setSolicitacao((Integer)result[4]);
			itemResul.setDescricaoExame((String)result[5]);
			itemResul.setO2((Boolean)result[6]);
			DominioTipoTransporte tipoTransporte = (DominioTipoTransporte)result[7];
			
			if(tipoTransporte != null){
				itemResul.setTransporte(tipoTransporte.getDescricao());
			}
			
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento = (AghAtendimentos) result[8];
			
			itemResul.setLocalizacao(atendimento != null ? atendimento.getDescricaoLocalizacao(true) : null);
			
			//add
			listReturn.add(itemResul);
		}
		return listReturn;
	}
	
	public List<AelHorarioExameDisp> pesquisaPorTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExame) {
		DetachedCriteria criteria = DetachedCriteria .forClass(AelHorarioExameDisp.class);
		criteria.add(Restrictions.eq(AelHorarioExameDisp.Fields.TIPO_MARCACAO_EXAME.toString(), tipoMarcacaoExame));

		return this.executeCriteria(criteria);
	}

}
