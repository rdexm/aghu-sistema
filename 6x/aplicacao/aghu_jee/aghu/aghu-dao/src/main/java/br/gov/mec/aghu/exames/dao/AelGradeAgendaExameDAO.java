package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorGradeVO;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorSalaVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGradeAgendaExameId;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;

public class AelGradeAgendaExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGradeAgendaExame> {

	private static final long serialVersionUID = -5661595264975981488L;

	@Override
	public void obterValorSequencialId(AelGradeAgendaExame elemento){
		if (elemento==null || elemento.getUnidadeFuncional()==null){
			throw new IllegalArgumentException("unidade funcinonal não pode ser nulo");
		}
		AelGradeAgendaExameId idGrade = new AelGradeAgendaExameId();
		idGrade.setUnfSeq(elemento.getUnidadeFuncional().getSeq());
		idGrade.setSeqp(this.nextSeqp(elemento.getUnidadeFuncional()));
		elemento.setId(idGrade);
	}

	public Integer nextSeqp(AghUnidadesFuncionais unidadeFuncional){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class);
		criteria.setProjection(Projections.max(AelGradeAgendaExame.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		Integer value = (Integer) executeCriteriaUniqueResult(criteria);
		if (value==null) {
			value=Integer.valueOf("0");
		}
		return ++value;
	}

	public Long pesquisarGradeExameCount(Integer seq, AghUnidadesFuncionais unidadeExecutora, DominioSituacao situacao, 
			AelSalasExecutorasExames sala, AelGrupoExames grupoExame, VAelUnfExecutaExames exame, RapServidores responsavel){

		DetachedCriteria criteria1 = montarCriteriaGradeExame1(seq, unidadeExecutora, situacao, sala, grupoExame, exame, responsavel);
		DetachedCriteria criteria2 = montarCriteriaGradeExame2(seq, unidadeExecutora, situacao, sala, grupoExame, exame, responsavel);
		
		Set<AelGradeAgendaExame> listaDistinct = new HashSet<AelGradeAgendaExame>();

		List<AelGradeAgendaExame> lista1 = executeCriteria(criteria1);
		List<AelGradeAgendaExame> lista2 = executeCriteria(criteria2);
		listaDistinct.addAll(lista1);
		listaDistinct.addAll(lista2);
		
		List<AelGradeAgendaExame> listaRetorno = new ArrayList<AelGradeAgendaExame>();
	
		listaRetorno.addAll(listaDistinct);
		
		return (long) listaRetorno.size();

	}

	public List<AelGradeAgendaExame> pesquisarGradeExame(String orderProperty, boolean asc,
			Integer seq, AghUnidadesFuncionais unidadeExecutora, DominioSituacao situacao, 
			AelSalasExecutorasExames sala, AelGrupoExames grupoExame, VAelUnfExecutaExames exame, 
			RapServidores responsavel) {

		DetachedCriteria criteria1 = montarCriteriaGradeExame1(seq, unidadeExecutora, situacao, sala, grupoExame, exame, responsavel);
		DetachedCriteria criteria2 = montarCriteriaGradeExame2(seq, unidadeExecutora, situacao, sala, grupoExame, exame, responsavel);
		
		Set<AelGradeAgendaExame> listaDistinct = new HashSet<AelGradeAgendaExame>();

		List<AelGradeAgendaExame> lista1 = executeCriteria(criteria1);
		List<AelGradeAgendaExame> lista2 = executeCriteria(criteria2);
		listaDistinct.addAll(lista1);
		listaDistinct.addAll(lista2);
		
		List<AelGradeAgendaExame> listaRetorno = new ArrayList<AelGradeAgendaExame>();
	
		listaRetorno.addAll(listaDistinct);
		
		Collections.sort(listaRetorno, new AelGradeAgendaExameComparator());
		
		return listaRetorno;
	}
	
	/**
	 * Comparator para ordenar pesquisa de grades de agenda exames pelo seqp
	 * 
	 * @author okamchen
	 * 
	 */
	private class AelGradeAgendaExameComparator implements Comparator<AelGradeAgendaExame> {
		@Override
		public int compare(AelGradeAgendaExame arg1, AelGradeAgendaExame arg2) {
			return arg1.getId().getSeqp().compareTo(arg2.getId().getSeqp());
		}
	}
	
	public List<AelGradeAgendaExame> pesquisarGradeExamePorGrupoExame(AelGrupoExames grupoExame) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class);
		criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.GRUPO_EXAME.toString()+"."+AelGrupoExames.Fields.SEQ.toString(), grupoExame.getSeq()));

		return executeCriteria(criteria);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria montarCriteriaGradeExame2(Integer seq, AghUnidadesFuncionais unidadeExecutora, DominioSituacao situacao, 
			AelSalasExecutorasExames sala, AelGrupoExames grupoExame, VAelUnfExecutaExames exame, RapServidores responsavel){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class, "GRADE_AGENDA");
		
		//Alias
		if(unidadeExecutora!=null){
			criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.UNIDADE_FUNCIONAL.toString(), AelGradeAgendaExame.Fields.UNIDADE_FUNCIONAL.toString());
		}
		if(sala!=null){
			criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString());
		}

		criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.GRUPO_EXAME.toString(), "GRUPO_EXAME");
		criteria.createAlias("GRUPO_EXAME"+"."+AelGrupoExames.Fields.GRUPO_EXAME_UNID_EXAME.toString(), "GRUPO_EXAME_UNIDADE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRUPO_EXAME_UNIDADE"+"."+ AelGrupoExameUnidExame.Fields.UNF_EXECUTA_EXAME.toString(), "UNF_EXAME_UNIDADE", JoinType.INNER_JOIN);
		
		if(exame!=null){
			criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.EXAME.toString(), AelGradeAgendaExame.Fields.EXAME.toString());
		}
		if(responsavel!=null){
			criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.SERVIDOR.toString(), "servidor");
			criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
		}
		
		//Restrictions
		criteria.add(Restrictions.eq("GRUPO_EXAME_UNIDADE"+"."+AelGrupoExameUnidExame.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(seq!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.SEQP.toString(), seq));
		}
		if(unidadeExecutora!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.UNF_SEQ.toString(), unidadeExecutora.getSeq()));
		}
		if(situacao!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(sala!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES_SEQ.toString(), sala.getId().getSeqp()));
		}
		if(grupoExame!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.GRUPO_EXAME.toString()+"."+AelGrupoExames.Fields.SEQ.toString(), grupoExame.getSeq()));
		}
		if(exame!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.UFE_EMA_EXA_SIGLA.toString(), exame.getId().getSigla()));
		}
		if(responsavel!=null){
			criteria.add(Restrictions.eq("servidor."+RapServidores.Fields.ID.toString(), responsavel.getId()));
		}
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.ID.toString()), AelGradeAgendaExame.Fields.ID.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.SERVIDOR_DIGITADO.toString()), AelGradeAgendaExame.Fields.SERVIDOR_DIGITADO.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.CRIADO_EM.toString()), AelGradeAgendaExame.Fields.CRIADO_EM.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.IND_SITUACAO.toString()), AelGradeAgendaExame.Fields.IND_SITUACAO.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.GRUPO_EXAME.toString()), AelGradeAgendaExame.Fields.GRUPO_EXAME.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.SERVIDOR.toString()), AelGradeAgendaExame.Fields.SERVIDOR.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString()), AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.DATA_ULTIMA_GERACAO.toString()), AelGradeAgendaExame.Fields.DATA_ULTIMA_GERACAO.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.UNIDADE_FUNCIONAL.toString()), AelGradeAgendaExame.Fields.UNIDADE_FUNCIONAL.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.AELEXAMES.toString()), AelGradeAgendaExame.Fields.AELEXAMES.toString())
				.add(Projections.property("GRADE_AGENDA." + AelGradeAgendaExame.Fields.EXAME.toString()), AelGradeAgendaExame.Fields.EXAME.toString());

		criteria.setProjection(Projections.distinct(projection));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelGradeAgendaExame.class));
		
		return criteria;		
	}
	
	private DetachedCriteria montarCriteriaGradeExame1(Integer seq, AghUnidadesFuncionais unidadeExecutora, DominioSituacao situacao, 
			AelSalasExecutorasExames sala, AelGrupoExames grupoExame, VAelUnfExecutaExames exame, RapServidores responsavel){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class, "GRADE_AGENDA");
		
		//Alias
		criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.UNIDADE_FUNCIONAL.toString(), AelGradeAgendaExame.Fields.UNIDADE_FUNCIONAL.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.EXAME.toString(), AelGradeAgendaExame.Fields.EXAME.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica", JoinType.LEFT_OUTER_JOIN);
		
		//Restrictions
		if(seq!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.SEQP.toString(), seq));
		}
		if(unidadeExecutora!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.UNF_SEQ.toString(), unidadeExecutora.getSeq()));
		}
		if(situacao!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(sala!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES_SEQ.toString(), sala.getId().getSeqp()));
		}
		if(grupoExame!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.GRUPO_EXAME.toString()+"."+AelGrupoExames.Fields.SEQ.toString(), grupoExame.getSeq()));
		}
		if(exame!=null){
			criteria.add(Restrictions.eq("GRADE_AGENDA"+"."+AelGradeAgendaExame.Fields.UFE_EMA_EXA_SIGLA.toString(), exame.getId().getSigla()));
		}
		if(responsavel!=null){
			criteria.add(Restrictions.eq("servidor."+RapServidores.Fields.ID.toString(), responsavel.getId()));
		}
		return criteria;		
	}

	public List<AelGradeAgendaExame> pesquisarGradeExameGrupoExame(Short unfSeq, Integer seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class);
		criteria.createAlias(AelGradeAgendaExame.Fields.GRUPO_EXAME.toString(), "grupoExame");
		criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.SEQP.toString(), seqp));
		return executeCriteria(criteria);
	}


	/**
	 * Busca as Agendas Por Sala
	 * @param {AghUnidadesFuncionais} unidadeExecutora
	 * @param {Date} dtAgenda
	 * @param {AelSalasExecutorasExames} sala
	 * @param {Boolean} impHorariosLivres
	 * @return List<RelatorioAgendaPorSalaVO>
	 */
	@SuppressWarnings("unchecked")
	public List<RelatorioAgendaPorSalaVO> obterAgendas(AghUnidadesFuncionais unidadeExecutora, Date dtAgenda, AelSalasExecutorasExames sala, Boolean impHorariosLivres) {
		Date horarioInicial = DateUtil.obterDataComHoraInical(dtAgenda);
		Date horarioFinal = DateUtil.obterDataComHoraFinal(dtAgenda);

		StringBuffer hql = new StringBuffer(650);
		hql.append("select new br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorSalaVO("); 	
		hql.append("	see.numero, ");
		hql.append("	see.localizacao, ");
		hql.append("	hed.id.dthrAgenda, ");
		hql.append("	see.numero, ");
		hql.append("	pac.prontuario, ");
		hql.append("	pac.nome, ");
		hql.append("	pac.dtNascimento, ");
		hql.append("	atd.seq, ");
		hql.append("	soe.seq, ");
		hql.append("	exa.descricao, ");
		hql.append("	hed.indHorarioExtra");
		hql.append(") from AelGradeAgendaExame gae");

		hql.append(" left join gae.");
		hql.append(AelGradeAgendaExame.Fields.HORARIOS_EXAME_DISP.toString());
		hql.append(" as hed ");

		hql.append(" inner join gae.");
		hql.append(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString());
		hql.append(" as see ");

		hql.append(" left join hed.");
		hql.append(AelHorarioExameDisp.Fields.ITEM_HORARIO_AGENDADOS.toString());
		hql.append(" as iha ");

		hql.append(" left join iha.");
		hql.append(AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString());
		hql.append(" as ise ");

		hql.append(" left join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString());
		hql.append(" as exa ");

		hql.append(" left join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString());
		hql.append(" as soe ");

		hql.append(" left join soe.");
		hql.append(AelSolicitacaoExames.Fields.ATENDIMENTO.toString());
		hql.append(" as atd ");

		hql.append(" left join atd.");
		hql.append(AghAtendimentos.Fields.PACIENTE.toString());
		hql.append(" as pac ");

		hql.append(" where ");

		hql.append("gae.");
		hql.append(AelGradeAgendaExame.Fields.UNF_SEQ.toString());
		hql.append(" = :codigoUnidadeExecutora and ");

		hql.append("hed.");
		hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
		hql.append(" between :horarioInicial and :horarioFinal");

		hql.append(" and ");

		if(sala != null) {
			hql.append("see.");
			hql.append(AelSalasExecutorasExames.Fields.NUMERO.toString());
			hql.append(" = :numeroSala and ");
		}

		hql.append("hed.");
		hql.append(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString());
		if(impHorariosLivres) {
			hql.append(" in (:situacaoHorarioM, :situacaoHorarioL)");
		}
		else {
			hql.append(" = :situacaoHorarioM");
		}
		
		hql.append(" order by hed.id.dthrAgenda, see.numero");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("horarioInicial", horarioInicial);
		query.setParameter("horarioFinal", horarioFinal);
		query.setParameter("situacaoHorarioM", DominioSituacaoHorario.M);
		if(impHorariosLivres) {
			query.setParameter("situacaoHorarioL", DominioSituacaoHorario.L);
		}
		query.setParameter("codigoUnidadeExecutora", unidadeExecutora.getSeq());
		if(sala!=null) {
			query.setParameter("numeroSala", sala.getNumero());
		}

		return query.list();
	}

	/**
	 * Busca as Agendas Por Grade
	 * @param {Short} gaeUnfSeq
	 * @param {Integer} gaeSeqp
	 * @param {Date} dthrAgendaInicial
	 * @param {Date} dthrAgendaFinal
	 * @return List<RelatorioAgendaPorGradeVO>
	 */
	@SuppressWarnings("unchecked")
	public List<RelatorioAgendaPorGradeVO> obterAgendasPorGrade(Short gaeUnfSeq, Integer gaeSeqp,
			Date dthrAgendaInicial, Date dthrAgendaFinal, Boolean impHorariosLivres) {

		StringBuffer hql = new StringBuffer(650);
		hql.append("select new br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorGradeVO("); 	
		hql.append("	 gae.").append(AelGradeAgendaExame.Fields.UNF_SEQ.toString());
		hql.append("	,see.").append(AelSalasExecutorasExames.Fields.NUMERO.toString());
		hql.append("	,hed.").append(AelHorarioExameDisp.Fields.GAE_SEQP.toString());
		hql.append("	,gex.").append(AelGrupoExames.Fields.DESCRICAO.toString());
		hql.append("	,hed.").append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
		hql.append("	,hed.").append(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString());
		hql.append("	,soe.").append(AelSolicitacaoExames.Fields.ATD_SEQ.toString());
		hql.append("	,soe.").append(AelSolicitacaoExames.Fields.SEQ.toString());
		hql.append("	,exa.").append(AelExames.Fields.DESCRICAO.toString());
		hql.append("	,pac.").append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append("	,pac.").append(AipPacientes.Fields.NOME.toString());
		hql.append(") from AelGradeAgendaExame gae");

		hql.append(" left join gae.");
		hql.append(AelGradeAgendaExame.Fields.HORARIOS_EXAME_DISP.toString());
		hql.append(" as hed ");

		hql.append(" inner join gae.");
		hql.append(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString());
		hql.append(" as see ");

		hql.append(" left join hed.");
		hql.append(AelHorarioExameDisp.Fields.ITEM_HORARIO_AGENDADOS.toString());
		hql.append(" as iha ");

		hql.append(" left join iha.");
		hql.append(AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString());
		hql.append(" as ise ");

		hql.append(" left join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString());
		hql.append(" as exa ");

		hql.append(" left join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString());
		hql.append(" as soe ");

		hql.append(" left join soe.");
		hql.append(AelSolicitacaoExames.Fields.ATENDIMENTO.toString());
		hql.append(" as atd ");

		hql.append(" left join atd.");
		hql.append(AghAtendimentos.Fields.PACIENTE.toString());
		hql.append(" as pac ");

		hql.append(" left join gae.");
		hql.append(AelGradeAgendaExame.Fields.GRUPO_EXAME.toString());
		hql.append(" as gex ");

		hql.append(" where ");
		hql.append("hed.");
		hql.append(AelHorarioExameDisp.Fields.GAE_UNF_SEQ.toString());
		hql.append(" = :gaeUnfSeq ");

		hql.append(" and hed.");
		hql.append(AelHorarioExameDisp.Fields.DTHR_AGENDA.toString());
		hql.append(" between :horarioInicial and :horarioFinal ");

		if (gaeSeqp != null) {
			hql.append(" and hed.");
			hql.append(AelHorarioExameDisp.Fields.GAE_SEQP.toString());
			hql.append(" = :gaeSeqp ");
		}

		if(impHorariosLivres != null) {
			hql.append(" and hed.");
			hql.append(AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString());
			if(impHorariosLivres) {
				hql.append(" in (:situacaoHorarioM, :situacaoHorarioL)");
			}
			else {
				hql.append(" = :situacaoHorarioM");
			}
		}

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("horarioInicial", dthrAgendaInicial);
		query.setParameter("horarioFinal", dthrAgendaFinal);
		query.setParameter("gaeUnfSeq", gaeUnfSeq);
		if (gaeSeqp != null) {
			query.setParameter("gaeSeqp", gaeSeqp);
		}
		if(impHorariosLivres != null) {
			if(impHorariosLivres) {
				query.setParameter("situacaoHorarioL", DominioSituacaoHorario.L);
			}
			query.setParameter("situacaoHorarioM", DominioSituacaoHorario.M);
		}

		return query.list();
	}	

	public List<AelGradeAgendaExame> pesquisarGradeAgendaExamePorSeqpUnfSeq(Object parametro, Short unfSeq) {
		String aliasUee = "uee";
		String aliasEma = "ema";
		String separador = ".";
		String strParametro = (String) parametro;
		Integer seqp = null;
		if (CoreUtil.isNumeroInteger(strParametro)) {
			seqp = Integer.parseInt(strParametro);
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class);
		
		criteria.createAlias(AelGradeAgendaExame.Fields.GRUPO_EXAME.toString(), "gex", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelGradeAgendaExame.Fields.EXAME.toString(), aliasUee, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), "see", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasUee + separador + AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), aliasEma,  JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasEma + separador + AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasEma + separador + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AelGradeAgendaExame.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PES", JoinType.LEFT_OUTER_JOIN);

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.UNF_SEQ.toString(), unfSeq));
		}

		if (seqp != null) {
			criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.SEQP.toString(), seqp));	
		}

		return executeCriteria(criteria);
	}

	public List<AelGradeAgendaExame> pesquisarGradeExamePorUnidadeExec(Object parametro ,Short unfSeq){

		Integer seqp = null;
		String strParametro = (String) parametro;
		if (CoreUtil.isNumeroInteger(strParametro)) {
			seqp = Integer.parseInt(strParametro);
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class);
		criteria.createAlias(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), "SEE");
		if (unfSeq != null) {
			criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if (seqp != null) {
			criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.SEQP.toString(), seqp));	
		}else if(!strParametro.isEmpty()){
			criteria.add(Restrictions.eq("SEE."+AelSalasExecutorasExames.Fields.NUMERO.toString(), strParametro));
		}

		criteria.addOrder(Order.asc(AelGradeAgendaExame.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}

	public Long contarAelGradeAgendaExamePorAelSalasExecutorasExames(final AelSalasExecutorasExames salaOriginal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class);
		criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), salaOriginal));
		return executeCriteriaCount(criteria);
	}

	public AelGradeAgendaExame obterPorChavePrimariaFull(AelGradeAgendaExameId id){

		DetachedCriteria criteria = DetachedCriteria.forClass(AelGradeAgendaExame.class);
		criteria.createAlias(AelGradeAgendaExame.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelGradeAgendaExame.Fields.GRUPO_EXAME.toString(), "GEX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), "SEE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelGradeAgendaExame.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelGradeAgendaExame.Fields.ID.toString(), id));
		
		return (AelGradeAgendaExame)executeCriteriaUniqueResult(criteria);

	}
}
