package br.gov.mec.aghu.exames.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.agendamento.vo.ExamesAgendadosNoHorarioVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAgendamentoMesmoHorarioVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelEtapaExame;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioExameDispId;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAelExameMatAnalise;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends"})
public class AelItemHorarioAgendadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemHorarioAgendado> {

	private static final long serialVersionUID = 3504358362533447624L;
	private static final String[] SIT_CODIGOS_JA_COLETADOS = new String[]{DominioSituacaoItemSolicitacaoExame.CA.toString(), 
																			DominioSituacaoItemSolicitacaoExame.CO.toString(),
																			DominioSituacaoItemSolicitacaoExame.LI.toString(),
																			DominioSituacaoItemSolicitacaoExame.AE.toString()};
	
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelItemHorarioAgendado.class);
    }
	
	public List<AelItemHorarioAgendado> obterPorItemSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacaoExame) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), itemSolicitacaoExame));
		return executeCriteria(criteria);
	}
	
	public AelItemHorarioAgendado buscarAelItemHorarioAgendadoComGradePorItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class,"iha");
		criteria.createAlias("iha."+AelItemHorarioAgendado.Fields.HORARIO_EXAME_DISPONIVEL.toString(), "hed");
		criteria.createAlias("hed."+AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString(), "gae");
		criteria.createAlias("gae."+AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), "see", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("iha."+AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), itemSolicitacao));
		
		List<AelItemHorarioAgendado> horariosAgendados = executeCriteria(criteria);
		
		if (horariosAgendados != null && !horariosAgendados.isEmpty()) {
			
			return horariosAgendados.get(0);
			
		}
		
		return null;
		
	}

	public Date buscarMenorHedDthrAgenda(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.ISE_SEQP.toString(), seqp));
		criteria.setProjection(Projections.min(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public List<Date> buscarListHedDthrAgenda(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.ISE_SEQP.toString(), seqp));
		criteria.setProjection(Projections.property(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()));
		criteria.addOrder(Order.asc(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AelItemHorarioAgendado> buscarPorItemSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacao) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(), itemSolicitacao.getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.ISE_SEQP.toString(), itemSolicitacao.getId().getSeqp()));
		
		return executeCriteria(criteria);
	}
	
	public Integer buscarQuantidadeHorariosNaoColetados(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		StringBuilder hql = new StringBuilder(200);
		
		hql.append(" select ")
		.append(" count (*) ")
		.append(" from ")
		.append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise, ")
		.append(AelItemHorarioAgendado.class.getSimpleName()).append(" iha ")
		.append(" where iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()).append(" = :hedGaeUnfSeq ")
		.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()).append(" = :hedGaeSeqp ")
		.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()).append(" = :hedDthrAgenda ")
		.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()).append(" = iha.").append(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString())
		.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SEQP.toString()).append(" = iha.").append(AelItemHorarioAgendado.Fields.ISE_SEQP.toString())
		.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()).append(" not in ( :sitCodigosJaColetados ) ");
		

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("hedGaeUnfSeq", hedGaeUnfSeq);
		query.setParameter("hedGaeSeqp", hedGaeSeqp);
		query.setTimestamp("hedDthrAgenda", hedDthrAgenda);
		query.setParameterList("sitCodigosJaColetados", SIT_CODIGOS_JA_COLETADOS);
		
		return Integer.valueOf(query.uniqueResult().toString());
	}
	
	/**
	 * Busca exames agendados no mesmo horario.
	 * @param {Short} hedGaeUnfSeq
	 * @param {Integer} hedGaeSeqp
	 * @param {Date} hedDthrAgenda
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExames
	 * @return List<AelItemHorarioAgendado>
	 */
	@SuppressWarnings("unchecked")
	public List<AelItemHorarioAgendado> buscarExamesAgendadosNoMesmoHorario(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, AelItemSolicitacaoExames itemSolicitacaoExames) {
		StringBuilder hql = new StringBuilder(200);
		
		List<AelItemHorarioAgendado> lista = new ArrayList<AelItemHorarioAgendado>();
		
		hql.append(" select iha ")
		.append(" from ")
		.append(AelItemHorarioAgendado.class.getSimpleName()).append(" iha ")
		.append(" where iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()).append(" = :hedGaeUnfSeq ")
		.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()).append(" = :hedGaeSeqp ")
		.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()).append(" = :hedDthrAgenda ")
		.append(" and ( iha.").append(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString()).append(" <> :hedIseSoeSeq ")
		.append(" or iha.").append(AelItemHorarioAgendado.Fields.ISE_SEQP.toString()).append(" <> :hedIseSeqp ").append(") ");
		

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("hedGaeUnfSeq", hedGaeUnfSeq);
		query.setParameter("hedGaeSeqp", hedGaeSeqp);
		query.setTimestamp("hedDthrAgenda", hedDthrAgenda);
		query.setParameter("hedIseSoeSeq", itemSolicitacaoExames.getId().getSoeSeq());
		query.setParameter("hedIseSeqp", itemSolicitacaoExames.getId().getSeqp());
		
		lista = query.list();
		
		return lista;
	}
	
	public List<ExamesAgendadosNoHorarioVO> pesquisarExamesAgendadosNoHorario(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class,"iha");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("iha." + AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()), "hedGaeUnfSeq")
				.add(Projections.property("iha." + AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()), "hedGaeSeqp")
				.add(Projections.property("iha." + AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()), "hedDthrAgenda")
				.add(Projections.property("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), "nroSolicitacao")
				.add(Projections.property("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString()), "item")
				.add(Projections.property("ise." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()), "situacao")
				.add(Projections.property("sic." + AelSitItemSolicitacoes.Fields.DESCRICAO.toString()), "descricaoSituacao")
				.add(Projections.property("vem." + VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()), "exame")
				.add(Projections.property("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString()), "amostra")
				.add(Projections.property("ete." + AelEtapaExame.Fields.NUMERO.toString()), "etapa"));
		
		criteria.createAlias("iha."+AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise", Criteria.INNER_JOIN);
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), "vem", Criteria.INNER_JOIN);
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "sic", Criteria.INNER_JOIN);
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "aie", Criteria.LEFT_JOIN);
		criteria.createAlias("iha."+AelItemHorarioAgendado.Fields.ETAPA_EXAME.toString(), "ete", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("iha."+AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString(), hedGaeUnfSeq));
		criteria.add(Restrictions.eq("iha."+AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString(), hedGaeSeqp));
		criteria.add(Restrictions.eq("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), hedDthrAgenda));
		
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ExamesAgendadosNoHorarioVO.class));
		
		return this.executeCriteria(criteria);
	}
	
	/*public List<ItemHorarioAgendadoVO> pesquisarItemHorarioAgendadoPorIseSoeSeqEListaIseSeqp(Integer iseSoeSeq,
			List<Short> listaIseSeqp) {
		String aliasIha = "iha";
		String aliasHed = "hed";
		String aliasGae = "gae";
		String aliasSee = "see";
		String aliasGex = "gex";
		String aliasSer = "ser";
		String aliasPes = "pes";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class, aliasIha);
		criteria.createAlias(aliasIha + separador + AelItemHorarioAgendado.Fields.HORARIO_EXAME_DISPONIVEL.toString(), aliasHed);
		criteria.createAlias(aliasHed + separador + AelHorarioExameDisp.Fields.GRADE_AGENDA_EXAME.toString(), aliasGae);
		criteria.createAlias(aliasGae + separador + AelGradeAgendaExame.Fields.SALAS_EXECUTORAS_EXAMES.toString(), aliasSee);
		criteria.createAlias(aliasGae + separador +	AelGradeAgendaExame.Fields.GRUPO_EXAME.toString(), aliasGex);
		criteria.createAlias(aliasIha + separador + AelItemHorarioAgendado.Fields.SERVIDOR.toString(), aliasSer);
		criteria.createAlias(aliasSer + separador + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()))
				.add(Projections.property(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()))
				.add(Projections.property(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()))
				.add(Projections.property(aliasHed + separador + AelHorarioExameDisp.Fields.SITUACAO_HORARIO.toString()))
				.add(Projections.property(aliasGex + separador + AelGrupoExames.Fields.DESCRICAO.toString()))
				.add(Projections.property(aliasSee + separador + AelSalasExecutorasExames.Fields.NUMERO.toString()))
				.add(Projections.property(aliasSer + separador + RapServidores.Fields.CODIGO_VINCULO.toString()))
				.add(Projections.property(aliasSer + separador + RapServidores.Fields.MATRICULA.toString()))
				.add(Projections.property(aliasPes + separador + RapPessoasFisicas.Fields.NOME)));
						
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.in(aliasIha + separador + AelItemHorarioAgendado.Fields.ISE_SEQP.toString(), listaIseSeqp));
		
		List<Object[]> listaArrayObject = executeCriteria(criteria);
		
		List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO = new ArrayList<ItemHorarioAgendadoVO>();
		
		for (Object[] arrayObject : listaArrayObject) {
			ItemHorarioAgendadoVO itemHorarioAgendadoVO = new ItemHorarioAgendadoVO();
			itemHorarioAgendadoVO.setHedGaeUnfSeq((Short) arrayObject[0]);
			itemHorarioAgendadoVO.setHedGaeSeqp((Integer) arrayObject[1]);
			itemHorarioAgendadoVO.setHedDthrAgenda((Date) arrayObject[2]);
			itemHorarioAgendadoVO.setSituacaoHorario((DominioSituacaoHorario) arrayObject[3]);
			itemHorarioAgendadoVO.setGradeGrupoExameDescricao((String) arrayObject[4]);
			itemHorarioAgendadoVO.setNumeroSala((String) arrayObject[5]);
			itemHorarioAgendadoVO.setServidorCodigoVinculo((Short) arrayObject[6]);
			itemHorarioAgendadoVO.setServidorMatricula((Integer) arrayObject[7]);
			itemHorarioAgendadoVO.setServidorNome((String) arrayObject[8]);
			itemHorarioAgendadoVO.setNovoHorarioSelecionado(Boolean.FALSE);
			listaItemHorarioAgendadoVO.add(itemHorarioAgendadoVO);
		}
		
		return listaItemHorarioAgendadoVO;		
	}*/
	
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoPorGradeEItemSolicitacaoExame(Short hedGaeUnfSeq, 
			Integer hedGaeSeqp, Integer soeSeq, Short seqp, String siglaExame, Integer matSeqExame, Short unfSeqExame, 
			String codigoSituacaoHorario, DominioSimNaoRestritoAreaExecutora simNaoRestritoAreaExecutora) {
		
		String aliasIha = "iha";
		String aliasIse = "ise";
		String aliasSis = "sis";
		String aliasAtd = "atd";
		String aliasSoe = "soe";
		String aliasGeu = "geu";
		String aliasUee = "uee";
		String aliasGex = "gex";
		String aliasGae = "gae";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class, aliasIha);
		criteria.createAlias(aliasIha + separador + AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), aliasIse);
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), aliasSis);
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), aliasSoe);
		criteria.createAlias(aliasSoe + separador + AelSolicitacaoExames.Fields.ATENDIMENTO, aliasAtd);
				
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOE_SEQ, soeSeq));
		criteria.add(Restrictions.ne(aliasIse + separador + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(aliasSis + separador + AelSitItemSolicitacoes.Fields.CODIGO.toString(), codigoSituacaoHorario));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelGrupoExameUnidExame.class, aliasGeu);
		subCriteria.createAlias(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UNF_EXECUTA_EXAME.toString(), aliasUee);
		subCriteria.createAlias(aliasGeu + separador + AelGrupoExameUnidExame.Fields.GRUPO_EXAME.toString(), aliasGex);
		subCriteria.createAlias(aliasGex + separador + AelGrupoExames.Fields.GRADE_AGENDA_EXAME.toString(), aliasGae);
		
		Projection p = Projections.projectionList()
				.add(Projections.property(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_EMA_EXA_SIGLA.toString()))
				.add(Projections.property(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_EMA_MAN_SEQ.toString()))
				.add(Projections.property(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_UNF_SEQ.toString()));
						
		subCriteria.setProjection(p);
		
		subCriteria.add(Restrictions.eqProperty(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_EMA_EXA_SIGLA.toString(),
				aliasIse + separador + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));
		subCriteria.add(Restrictions.eqProperty(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_EMA_MAN_SEQ.toString(),
				aliasIse + separador + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));	
		subCriteria.add(Restrictions.eqProperty(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_UNF_SEQ.toString(),
				aliasIse + separador + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		
		subCriteria.add(Restrictions.eq(aliasGae + separador + AelGradeAgendaExame.Fields.UNF_SEQ, hedGaeUnfSeq));
		subCriteria.add(Restrictions.eq(aliasGae + separador + AelGradeAgendaExame.Fields.SEQP, hedGaeSeqp));
		subCriteria.add(Restrictions.eq(aliasGex + separador + AelGrupoExames.Fields.IND_AGENDA_EX_MESMO_HOR, Boolean.TRUE));

		List<DominioOrigemAtendimento> listaOrigemAtendimento = new ArrayList<DominioOrigemAtendimento>();
		listaOrigemAtendimento.add(DominioOrigemAtendimento.I);
		listaOrigemAtendimento.add(DominioOrigemAtendimento.H);
		
		subCriteria.add(Restrictions.or(
										Restrictions.and(Restrictions.or(Restrictions.eq(aliasUee + separador + AelUnfExecutaExames.Fields.IND_AGENDAM_PREVIO_INT, DominioSimNaoRestritoAreaExecutora.S), 
																		Restrictions.eq(aliasUee + separador + AelUnfExecutaExames.Fields.IND_AGENDAM_PREVIO_INT, simNaoRestritoAreaExecutora)),
														Restrictions.in(aliasAtd + separador + AghAtendimentos.Fields.ORIGEM.toString(), listaOrigemAtendimento)),
										Restrictions.and(Restrictions.or(Restrictions.eq(aliasUee + separador + AelUnfExecutaExames.Fields.IND_AGENDAM_PREVIO_NAO_INT, DominioSimNaoRestritoAreaExecutora.S), 
																		Restrictions.eq(aliasUee + separador + AelUnfExecutaExames.Fields.IND_AGENDAM_PREVIO_NAO_INT, simNaoRestritoAreaExecutora)),
														Restrictions.in(aliasAtd + separador + AghAtendimentos.Fields.ORIGEM.toString(), listaOrigemAtendimento)))		
				);
		
		subCriteria.add(Restrictions.or(
							Restrictions.ne(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_EMA_EXA_SIGLA.toString(), siglaExame), 
							Restrictions.or(Restrictions.ne(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_EMA_MAN_SEQ.toString(), matSeqExame), 
											Restrictions.ne(aliasGeu + separador + AelGrupoExameUnidExame.Fields.UFE_UNF_SEQ.toString(), unfSeqExame))));
		
		criteria.add(Subqueries.exists(subCriteria));
		return executeCriteria(criteria);
	}
	
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoItemSolicitacaoExameMaterialColetavel(
			Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		String aliasIha = "iha";
		String aliasIse = "ise";
		String aliasMan = "man";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class, aliasIha);
		criteria.createAlias(aliasIha + separador + AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME, aliasIse);
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES, aliasMan);
		
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ, hedGaeUnfSeq));
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_SEQP, hedGaeSeqp));
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA, hedDthrAgenda));
		criteria.add(Restrictions.eq(aliasMan + separador + AelMateriaisAnalises.Fields.IND_COLETAVEL, Boolean.TRUE));
		
		return executeCriteria(criteria);
	}
	
	public List<AelItemHorarioAgendado> pesquisarAgendamentoPacientePorDatas(
			Integer pacCodigo, Date data1, Date data2) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class,"iha");
		criteria.createAlias(AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		Date data1Inicio = DateUtil.truncaData(data1);
		Date data1Fim = DateUtil.truncaDataFim(data1);
		Date data2Inicio = DateUtil.truncaData(data2);
		Date data2Fim = DateUtil.truncaDataFim(data2);
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.ge("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(),data1Inicio),Restrictions.le("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(),data1Fim)), Restrictions.and(Restrictions.ge("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(),data2Inicio),Restrictions.le("iha."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(),data2Fim))));
		criteria.add(Restrictions.gt("iha"+"."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA, new Date()));
		criteria.add(Restrictions.eq("pac"+"."+AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		return this.executeCriteria(criteria);
	}
	
	
	public List<AelItemHorarioAgendado> pesquisarExamesParaTransferencia(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class,"iha");
		criteria.createAlias(AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "ufe");
		criteria.createAlias("ufe."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "ema");
		criteria.createAlias("ema."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa");
		criteria.createAlias("ema."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "man");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "sic");
		criteria.createAlias("iha."+AelItemHorarioAgendado.Fields.ETAPA_EXAME.toString(), "ete",Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq("iha."+AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString(),hedGaeUnfSeq));
		criteria.add(Restrictions.eq("iha"+"."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA, hedDthrAgenda));
		criteria.add(Restrictions.eq("iha"+"."+AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString(), hedGaeSeqp));
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		return this.executeCriteria(criteria);
	}
	
	public List<Date> pesquisarSugestaoAgendamentoPorPaciente(
			Integer pacCodigo, Boolean isAmbulatorio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class,"iha");
		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections
						.property(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA
								.toString()))));
		criteria.createAlias(AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.gt("iha"+"."+AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), new Date()));
		criteria.add(Restrictions.eq("pac"+"."+AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		if(isAmbulatorio){
			return this.executeCriteria(criteria);
		} else{
			return this.executeCriteria(criteria, 0, 2, null, false);
		}
	}
	
	
	
	public Long obterExamesAgendamentosPaciente(Integer pacCodigo, AelUnfExecutaExamesId unfExecutaExamesId){
		StringBuilder sql = new StringBuilder(3000);
		
		sql.append("WITH solicitacoes AS                                   ")
			.append("  (SELECT soe.seq  AS soe_seq,                         ")
			.append("    ATD.PAC_CODIGO AS pac_codigo                       ")
			.append("  FROM AGH.AEL_SOLICITACAO_EXAMES SOE                  ")
			.append("  INNER JOIN AGH.AGH_ATENDIMENTOS ATD                  ")
			.append("  ON SOE.ATD_SEQ =ATD.SEQ                              ")
			.append("  UNION                                                ")
			.append("  SELECT soe.seq   AS soe_seq,                         ")
			.append("    ATV.PAC_CODIGO AS pac_codigo                       ")
			.append("  FROM AGH.AEL_SOLICITACAO_EXAMES SOE                  ")
			.append("  INNER JOIN AGH.AEL_ATENDIMENTO_DIVERSOS ATV          ")
			.append("  ON SOE.ATV_SEQ =ATV.SEQ                              ")
			.append("  )                                                    ")
			.append("SELECT count(*)                                        ")
			.append("FROM AGH.AEL_ITEM_HORARIO_AGENDADOS IHA                ")
			.append("INNER JOIN solicitacoes                                ")
			.append("ON IHA.ISE_SOE_SEQ          = solicitacoes.soe_seq     ")
			.append("AND solicitacoes.pac_codigo = :pacCodigo               ")
			.append("INNER JOIN AGH.AEL_item_SOLICITACAO_EXAMES ISE         ")
			.append("ON IHA.ISE_SEQP         =ISE.SEQP                      ")
			.append("AND IHA.ISE_SOE_SEQ     =ISE.SOE_SEQ                   ")
			.append("AND IHA.HED_DTHR_AGENDA>= :dataAtual                   ")
			.append("AND (EXISTS                                            ")
			.append("  (SELECT 1                                            ")
			.append("  FROM AGH.AEL_GRUPO_EXAME_UNID_EXAMES GEU             ")
			.append("  INNER JOIN AGH.AEL_UNF_EXECUTA_EXAMES UFE            ")
			.append("  ON GEU.UFE_EMA_EXA_SIGLA=UFE.EMA_EXA_SIGLA           ")
			.append("  AND GEU.UFE_EMA_MAN_SEQ =UFE.EMA_MAN_SEQ             ")
			.append("  AND GEU.UFE_UNF_SEQ     =UFE.UNF_SEQ                 ")
			.append("  INNER JOIN AGH.AEL_GRUPO_EXAMES GEX                  ")
			.append("  ON GEU.GEX_SEQ=GEX.SEQ                               ")
			.append("  INNER JOIN AGH.AEL_GRADE_AGENDA_EXAMES GAE           ")
			.append("  ON GEX.SEQ=GAE.GEX_SEQ                               ")
			.append("  CROSS JOIN AGH.AEL_HORARIO_EXAME_DISPS HED           ")
			.append("  WHERE HED.GAE_UNF_SEQ          =GAE.UNF_SEQ          ")
			.append("  AND HED.GAE_SEQP               =GAE.SEQP             ")
			.append("  AND HED.GAE_UNF_SEQ            =IHA.HED_GAE_UNF_SEQ  ")
			.append("  AND HED.GAE_SEQP               =IHA.HED_GAE_SEQP     ")
			.append("  AND GAE.IND_SITUACAO           ='A'                  ")
			.append("  AND GEU.IND_SITUACAO           ='A'                  ")
			.append("  AND HED.DTHR_AGENDA            =IHA.HED_DTHR_AGENDA  ")
			.append("  AND UFE.EMA_EXA_SIGLA          = :siglaExame         ")
			.append("  AND UFE.EMA_MAN_SEQ            = :matSeqExame        ")
			.append("  AND UFE.UNF_SEQ                = :unfSeqExame        ")
			.append("  AND GEX.IND_AGENDA_EX_MESMO_HOR='S'                  ")
			.append("  AND HED.SITUACAO_HORARIO       ='M'                  ")
			.append("  ))                                                   ");
			
		

		org.hibernate.Query query = createSQLQuery(sql.toString());
		query.setInteger("pacCodigo", pacCodigo);
		query.setString("siglaExame", unfExecutaExamesId.getEmaExaSigla());
		query.setInteger("matSeqExame", unfExecutaExamesId.getEmaManSeq());
		query.setShort("unfSeqExame", unfExecutaExamesId.getUnfSeq().getSeq());
		query.setTimestamp("dataAtual", new Date());
		
		BigInteger result = (BigInteger) query.uniqueResult();
		
		return result.longValue();
		
	}
	
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoPorGaeUnfSeqGaeSeqpDthrAgenda(Short hedGaeUnfSeq, Integer hedGaeSeqp, 
			Date hedDthrAgenda) {
		String aliasIha = "iha";
		String aliasIse = "ise";
		String aliasSoe = "soe";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class, aliasIha);
		criteria.createAlias(aliasIha + separador + AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME, aliasIse);		
		criteria.createAlias(aliasIse + separador + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), aliasSoe);
		criteria.createAlias(aliasSoe + separador + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ, hedGaeUnfSeq));
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_GAE_SEQP, hedGaeSeqp));
		criteria.add(Restrictions.eq(aliasIha + separador + AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA, hedDthrAgenda));
		
		return executeCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<AelItemHorarioAgendado> obterExamesAgendados(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, AelSitItemSolicitacoes situacaoItemSolicitacao) {
		StringBuilder hql = new StringBuilder(200);
		
		hql.append(" select iha ")
		.append(" from ").append(AelItemHorarioAgendado.class.getSimpleName()).append(" iha ")
		.append(" inner join iha.").append(AelItemHorarioAgendado.Fields.ITEM_SOLICITACAO_EXAME.toString()).append(" ise ")
		.append(" where iha.").append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()).append(" = :hedDthrAgenda ")
		.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()).append(" = :hedGaeUnfSeq ")
		.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()).append(" = :hedGaeSeqp ")
		.append(" and ise.").append(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString()).append(" = :situacaoItemSolicitacao ");
		
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setShort("hedGaeUnfSeq", hedGaeUnfSeq);
		query.setInteger("hedGaeSeqp", hedGaeSeqp);
		query.setTimestamp("hedDthrAgenda", hedDthrAgenda);
		query.setParameter("situacaoItemSolicitacao", situacaoItemSolicitacao);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AelItemHorarioAgendado> obterItensHorarioAgendadoComAmostras(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoAmostra sitAmostra) {
		StringBuilder hql = new StringBuilder(200);
		
		hql.append(" select iha ")
		.append(" from ").append(AelItemHorarioAgendado.class.getSimpleName()).append(" iha, ")
		.append(AelAmostraItemExames.class.getSimpleName()).append(" aie ")
		.append(" where iha.").append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()).append(" = :hedDthrAgenda ")
		.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()).append(" = :hedGaeUnfSeq ")
		.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()).append(" = :hedGaeSeqp ")
		.append(" and aie.").append(AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString()).append(" = iha.").append(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString())
		.append(" and aie.").append(AelAmostraItemExames.Fields.ISE_SEQP.toString()).append(" = iha.").append(AelItemHorarioAgendado.Fields.ISE_SEQP.toString())
		.append(" and aie.").append(AelAmostraItemExames.Fields.SITUACAO.toString()).append(" = :sitAmostra ");
		
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setShort("hedGaeUnfSeq", hedGaeUnfSeq);
		query.setInteger("hedGaeSeqp", hedGaeSeqp);
		query.setTimestamp("hedDthrAgenda", hedDthrAgenda);
		query.setParameter("sitAmostra", sitAmostra);
		
		return query.list();
	}
	
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoPorGradeESoeSeq(Short hedGaeUnfSeq, Integer hedGaeSeqp, 
			Integer soeSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class);
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString(), hedGaeUnfSeq));
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString(), hedGaeSeqp));
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		return executeCriteria(criteria);
	}
	
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoPorHorarioExameDisp(AelHorarioExameDispId horarioExameDispId) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString(), horarioExameDispId.getGaeUnfSeq()));
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString(), horarioExameDispId.getGaeSeqp()));
		criteria.add(Restrictions.eq(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), horarioExameDispId.getDthrAgenda()));
		return executeCriteria(criteria);
	}

    @SuppressWarnings("unchecked")
    public List<ExameAgendamentoMesmoHorarioVO> pesquisaHorariosAgendamentoMesmoGrupoExames(AghAtendimentos atendimento,
                                                               AelItemSolicitacaoExames itemSolicitacao,
                                                               DominioSituacaoHorario situacaoAgendaExame) {
        Date dataAtual = new Date();
        StringBuffer hql = new StringBuffer(1100);
        hql.append(" select new br.gov.mec.aghu.exames.solicitacao.vo.ExameAgendamentoMesmoHorarioVO( ")
                .append("  itemAgendamento.hedGaeSeqp, itemAgendamento.hedDthrAgenda) from ")
                .append("  AelSolicitacaoExames solicitacao ")
                .append("  AelSolicitacaoExames solicitacao ")
                .append("  join soe.atendimento atendimento ")
                .append("  join soe.itensSolicitacaoExame itemSolicitacao ")
                .append("  join itemSolicitacao.itemHorarioAgendado itemAgendamento ")
                .append("  where ")
                .append(" itemAgendamento.id.hedDthrAgenda >= :dataAtual ")
                .append(" atendimento.paciente.codigo = :codigoPaciente ")
                .append(" and exists ")
                .append(" ( ")
                .append(" select 1 from ")
                .append(" VAelHrGradeDisp gradeHorariosDisponiveis ")
                .append(" where ")
                .append(" gradeHorariosDisponiveis.id.grade = itemAgendamento.id.hedGaeUnfSeq ")
                .append(" and gradeHorariosDisponiveis.id.seqGrade =  itemAgendamento.id.hedGaeSeqp ")
                .append(" and gradeHorariosDisponiveis.id.dthrAgenda = itemAgendamento.id.hedDthrAgenda ")
                .append(" and gradeHorariosDisponiveis.id.indAgendaExMesmoHor = 'S' ")
                .append(" and gradeHorariosDisponiveis.id.situacaoHorario = :paramEnumSituacaoHorario ")
                .append(" and gradeHorariosDisponiveis.id.siglaExame = :sigla ")
                .append(" and gradeHorariosDisponiveis.id.matExame = :matExame ")
                .append(" and gradeHorariosDisponiveis.id.unfExame = :unidadeFuncional ")
                .append(" ) ");

        Query query = createHibernateQuery(hql.toString());
        query.setParameter("codigoPaciente", atendimento.getPaciente().getCodigo());
        query.setParameter("dataAtual", dataAtual);
        query.setParameter("paramEnumSituacaoHorario", situacaoAgendaExame);
        query.setParameter("sigla", itemSolicitacao.getExame().getSigla());
        query.setParameter("matExame", itemSolicitacao.getMaterialAnalise().getSeq());

        return  query.list();
    }
}