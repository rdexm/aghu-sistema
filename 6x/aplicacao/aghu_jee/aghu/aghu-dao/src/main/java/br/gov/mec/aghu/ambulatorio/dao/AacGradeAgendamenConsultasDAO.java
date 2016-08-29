package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.AacGradeAgendamenConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaEspecialidadeAlteradaVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.CursorCTicketDadosVO;
import br.gov.mec.aghu.ambulatorio.vo.CursorConDadosVO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.ambulatorio.vo.EncaminhamentosRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroGradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.CurTeiVO;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacConsultasJn;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentoAmbulatorioVO;

@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" })
public class AacGradeAgendamenConsultasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacGradeAgendamenConsultas> {

	private static final String ALIAS_G_PONTO = "G.";
	private static final String ALIAS_GRD = "GRD";
	private static final String ALIAS_GRD_PONTO = "GRD.";
	private static final String ALIAS_CON = "CON";
	private static final String ALIAS_CON_PONTO = "CON.";
	private static final String ALIAS_ESP = "ESP";
	private static final String ALIAS_ESP_PONTO = "ESP.";
    private static final long serialVersionUID = -7540998545482113216L;
    
    @Inject
    private IParametroFacade parametroFacade;
    
	@Inject @New(AacGradeAgendamenConsultasObterOriginalQueryBuilder.class) 
	private Instance<AacGradeAgendamenConsultasObterOriginalQueryBuilder> obterOriginalQueryBuilder;

	@Inject @New(PesquisaDisponibilidadeHorariosQueryBuilder.class) 
	private Instance<PesquisaDisponibilidadeHorariosQueryBuilder> obterOriginalQueryBuilderPesquisaDisponibilidade;
    
    /**
     * Devido a problema na migracao de dados/estrutura o metodo generico nao pode ser usado.
     */
    @Override
	public AacGradeAgendamenConsultas obterOriginal(AacGradeAgendamenConsultas elemento) {
    	AacGradeAgendamenConsultasObterOriginalQueryBuilder builder = obterOriginalQueryBuilder.get();
    	
    	builder.setGradeSeq(elemento.getSeq());
    	Query query = builder.build();
    	@SuppressWarnings("unchecked")
    	List<Object[]> camposLst = (List<Object[]>) query.list();
    	
    	AacGradeAgendamenConsultas gradeAgendamentoConsultas = builder.materialize(camposLst);
    	return gradeAgendamentoConsultas;
	}
    
    private Query montarPesquisaDisponibilidadeHorarios(Integer filtroSeq, Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade,
			AghEquipes filtroEquipe, RapServidores filtroProfissional, boolean count, AacPagador filtroPagador,
			AacTipoAgendamento filtroTipoAgendamento, AacCondicaoAtendimento filtroCondicao, Date dtConsulta, Date horaConsulta,
			Date mesInicio, Date mesFim, DominioDiaSemana dia, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,
			DataInicioFimVO turno, List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {
    	
    	Short seqTipoAgendamentoSMS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TAG_SMS).getVlrNumerico().shortValue();
		Short seqCondPrimeiraConsulta = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CAA_CONSULTA).getVlrNumerico().shortValue();
		
		PesquisaDisponibilidadeHorariosQueryBuilder builder =  obterOriginalQueryBuilderPesquisaDisponibilidade.get();
		
		return builder.montarPesquisaDisponibilidadeHorarios(filtroSeq, filtroUslUnfSeq, filtroEspecialidade
    					, filtroEquipe, filtroProfissional, count, filtroPagador
    					, filtroTipoAgendamento, filtroCondicao, dtConsulta, horaConsulta
    					, mesInicio, mesFim, dia, zona, zonaSala
    					, turno, listEspecialidade, visualizarPrimeirasConsultasSMS, seqTipoAgendamentoSMS, seqCondPrimeiraConsulta);
    }
    
	@SuppressWarnings("PMD.NPathComplexity")
    private DetachedCriteria criarCriteriaPesquisaAacGradeAgendamenConsultasSituacao(AacGradeAgendamenConsultas grade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);

		if (grade.getAacUnidFuncionalSala() != null && grade.getAacUnidFuncionalSala().getId() != null
			&& grade.getAacUnidFuncionalSala().getId().getUnfSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString(), grade
				.getAacUnidFuncionalSala().getId().getUnfSeq()));
		}
		if (grade.getAacUnidFuncionalSala() != null && grade.getAacUnidFuncionalSala().getId() != null
			&& grade.getAacUnidFuncionalSala().getId().getSala() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_ID_SALA.toString(), grade.getAacUnidFuncionalSala()
				.getId().getSala()));
		}
		if (grade.getEquipe() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), grade.getEquipe().getSeq()));
		}
		if (grade.getEspecialidade() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), grade.getEspecialidade().getSeq()));
		}
		if (grade.getProfEspecialidade() != null && grade.getProfEspecialidade().getId() != null
			&& grade.getProfEspecialidade().getId().getSerMatricula() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString(), grade.getProfEspecialidade()
				.getId().getSerMatricula()));
		}
		if (grade.getProfEspecialidade() != null && grade.getProfEspecialidade().getId() != null
			&& grade.getProfEspecialidade().getId().getSerVinCodigo() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString(), grade.getProfEspecialidade()
				.getId().getSerVinCodigo()));
		}
		if (grade.getProfEspecialidade() != null && grade.getProfEspecialidade().getId() != null
			&& grade.getProfEspecialidade().getId().getEspSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.PRE_ESP_SEQ.toString(), grade.getProfEspecialidade().getId()
				.getEspSeq()));
		}
		if (grade.getProcedimento() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString(), grade.getProcedimento()));
		}

		return criteria;
    }

    /**
     * 
     * @return A query em HQL conforme os filtros que foram informados
     */
    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NcssMethodCount", "PMD.NPathComplexity" })
    private Query montarPesquisaAacGradeAgendamenConsultas(Integer filtroSeq, Short filtroUslUnfSeq, Boolean filtroProcedimento,
			Boolean filtroEnviaSamis, DominioSituacao filtroSituacao, AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe,
			RapServidores profissional, AelProjetoPesquisas filtroProjeto, Date filtroDtEm, Date filtroDtInicio, Date filtroDtFim,
			Date filtroDtInicioUltGeracao, Date filtroDtFimUltGeracao, boolean count) {

		Short espSeq = null;
		Integer eqpSeq = null;
		Integer preSerMatricula = null;
		Short preSerVinCodigo = null;
		Integer pjqSeq = null;
		Date dtEm = null;
		Date dataAtual = null;
		Date dtInicio = null;
		Date dtFim = null;
		Date dtAtual = null;
		Date dtInicioUltGeracao = null;
		Date dtFimUltGeracao = null;
		Date dtInicioUltGeracaoDiaSeguinte = null;
		Date dtFimUltGeracaoDiaSeguinte = null;

		StringBuffer hql = new StringBuffer(500);
		if (count) {
			hql.append("select count(distinct agac.seq)");
		} else {
			hql.append("select distinct new br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoVO(");
			hql.append("	agac.seq, ");
			hql.append("	agac.criadoEm, ");
			hql.append("	agac.alteradoEm, ");
			hql.append("	agac.dtUltimaGeracao, ");
			hql.append("	agac.procedimento, ");
			hql.append("	pf.nome, ");
			hql.append("	pfAlt.nome, ");
			hql.append("	sglSl.sigla, ");
			hql.append("	sglSl.id.sala, ");
			hql.append("	esp.sigla, ");
			hql.append("	esp.nomeEspecialidade, ");
			hql.append("	eqp.nome, ");
			hql.append("	pfProfEsp.nome, ");
			hql.append("	pgd.descricao ");
			hql.append(')');
		}

		hql.append(" from AacGradeAgendamenConsultas agac ");
		hql.append(" 	left join agac.pagador pgd ");
		hql.append(" 	left join agac.servidor.pessoaFisica pf ");
		hql.append(" 	left join agac.servidorAlterado.pessoaFisica pfAlt ");
		hql.append(" 	left join agac.siglaUnfSala sglSl ");
		hql.append(" 	left join agac.especialidade esp ");
		hql.append(" 	left join agac.equipe eqp ");
		hql.append(" 	left join agac.profEspecialidade.rapServidor.pessoaFisica pfProfEsp ");

		if (filtroSituacao != null) {
			hql.append("inner join agac.gradeSituacao gs ");
		}
		if (filtroDtInicio != null || filtroDtFim != null) {
			hql.append("inner join agac.aacConsultas ac ");
		}
		
		if (filtroSeq != null || filtroUslUnfSeq != null || filtroProcedimento != null || filtroEnviaSamis != null
				|| filtroSituacao != null || filtroEspecialidade != null || filtroEquipe != null || profissional != null
				|| filtroProjeto != null || filtroDtEm != null || filtroDtInicio != null || filtroDtFim != null
				|| dtInicioUltGeracaoDiaSeguinte != null || dtFimUltGeracaoDiaSeguinte != null) {

			hql.append(" where ");

			boolean concatenouPrimeiraCondicao = false;
			if (filtroSituacao != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				// Inner Join com AacGradeSituacao
				hql.append("agac." + AacGradeAgendamenConsultas.Fields.SEQ.toString() + " = gs."
					+ AacGradeSituacao.Fields.GRD_SEQ.toString() + " ");
			}

			if (filtroDtInicio != null || filtroDtFim != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				// Inner Join com AacConsultas
				hql.append("agac." + AacGradeAgendamenConsultas.Fields.SEQ.toString() + " = ac."
					+ AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + PONTO + AacGradeAgendamenConsultas.Fields.SEQ.toString());
			}

			if (filtroSeq != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.SEQ.toString());
				hql.append(" = :filtroSeq ");
			}

			if (filtroUslUnfSeq != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString());
				hql.append(" = :filtroUslUnfSeq ");
			}

			if (filtroProcedimento != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString());
				hql.append(" = :filtroProcedimento ");
			}

			if (filtroEnviaSamis != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.IND_ENVIA_SAMIS.toString());
				hql.append(" = :filtroEnviaSamis ");
			}

			if (filtroEspecialidade != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				espSeq = filtroEspecialidade.getSeq();
				hql.append("agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString());
				hql.append(" = :espSeq ");
			}

			if (filtroEquipe != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				eqpSeq = filtroEquipe.getSeq();
				hql.append("agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString());
				hql.append(" = :eqpSeq ");
			}

			if (profissional != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				preSerMatricula = profissional.getId().getMatricula();
				preSerVinCodigo = profissional.getId().getVinCodigo();
				hql.append("agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString());
				hql.append(" = :preSerMatricula ");
				hql.append("and agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString());
				hql.append(" = :preSerVinCodigo ");
			}

			if (filtroProjeto != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				pjqSeq = filtroProjeto.getSeq();
				hql.append("agac.");
				hql.append(AacGradeAgendamenConsultas.Fields.PJQ_SEQ.toString());
				hql.append(" = :pjqSeq ");
			}

			// Filtros conforme a function AACC_VER_SIT_GRADE
			if (filtroSituacao != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("gs.");
				hql.append(AacGradeSituacao.Fields.IND_SITUACAO.toString());
				hql.append(" = :filtroSituacao ");

				if (filtroDtEm != null) {
					dtEm = DateUtil.truncaData(filtroDtEm);
					hql.append("and :dtEm >= ");
					hql.append("gs.");
					hql.append(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString());
					hql.append(" and (gs.");
					hql.append(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString());
					hql.append(" is null ");
					hql.append("or :dtEm <= ");
					hql.append("gs.");
					hql.append(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString());
					hql.append(") ");
				} else {
					dataAtual = DateUtil.truncaData(new Date());
					hql.append("and :dataAtual >= ");
					hql.append("gs.");
					hql.append(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString());
					hql.append(" and (gs.");
					hql.append(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString());
					hql.append(" is null ");
					hql.append("or :dataAtual <= ");
					hql.append("gs.");
					hql.append(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString());
					hql.append(") ");
				}
			}

			if (filtroDtInicio != null || filtroDtFim != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}

				if (filtroDtInicio != null && filtroDtFim != null) {
					dtInicio = DateUtil.truncaData(filtroDtInicio);
					dtFim = DateUtil.obterDataComHoraFinal(filtroDtFim);
					hql.append("ac.");
					hql.append(AacConsultas.Fields.DATA_CONSULTA.toString());
					hql.append(" between :dtInicio and :dtFim ");
				} else if (filtroDtInicio != null && filtroDtFim == null) { 
					// Considera somente a data de inicio
					dtInicio = DateUtil.truncaData(filtroDtInicio);
					dtAtual = DateUtil.obterDataComHoraFinal(Calendar.getInstance().getTime());
					hql.append("ac.");
					hql.append(AacConsultas.Fields.DATA_CONSULTA.toString());
					hql.append(" between :dtInicio and :dtAtual ");
				} else if (filtroDtInicio == null && filtroDtFim != null) { 
					// Considera somente a data de fim
					dtFim = DateUtil.obterDataComHoraFinal(filtroDtFim);
					hql.append("ac.");
					hql.append(AacConsultas.Fields.DATA_CONSULTA.toString());
					hql.append(" <= :dtFim ");
				}
			}

			if (filtroDtInicioUltGeracao != null || filtroDtFimUltGeracao != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}

				if (filtroDtInicioUltGeracao != null && filtroDtFimUltGeracao != null) {
					dtInicioUltGeracao = DateUtil.truncaData(filtroDtInicioUltGeracao);
					dtFimUltGeracao = DateUtil.truncaData(filtroDtFimUltGeracao);
					hql.append("agac.").append(AacGradeAgendamenConsultas.Fields.DT_ULTIMA_GERACAO);
					hql.append(" between :dtInicioUltGeracao and :dtFimUltGeracao");
				} else if (filtroDtInicioUltGeracao != null && filtroDtFimUltGeracao == null) {
					// CondiÃ§Ã£o adaptada devido ao uso do trunc(<data>)
					dtInicioUltGeracao = DateUtil.truncaData(filtroDtInicioUltGeracao);
					dtInicioUltGeracaoDiaSeguinte = DateUtil.adicionaDias(dtInicioUltGeracao, 1);
					hql.append("agac.").append(AacGradeAgendamenConsultas.Fields.DT_ULTIMA_GERACAO);
					hql.append(" >= :dtInicioUltGeracao");
					hql.append(" and agac.").append(AacGradeAgendamenConsultas.Fields.DT_ULTIMA_GERACAO);
					hql.append(" < :dtInicioUltGeracaoDiaSeguinte");
				} else if (filtroDtInicioUltGeracao == null && filtroDtFimUltGeracao != null) {
					// CondiÃ§Ã£o adaptada devido ao uso do trunc(<data>)
					dtFimUltGeracao = DateUtil.truncaData(filtroDtFimUltGeracao);
					dtFimUltGeracaoDiaSeguinte = DateUtil.adicionaDias(dtFimUltGeracao, 1);
					hql.append("agac.").append(AacGradeAgendamenConsultas.Fields.DT_ULTIMA_GERACAO);
					hql.append(" >= :dtFimUltGeracao");
					hql.append(" and agac.").append(AacGradeAgendamenConsultas.Fields.DT_ULTIMA_GERACAO);
					hql.append(" < :dtFimUltGeracaoDiaSeguinte");
				}
			}
		}

		if (!count) {
			hql.append(" order by sglSl.sigla, sglSl.id.sala ");
		}

		Query query = createHibernateQuery(hql.toString());
		if (filtroSeq != null) {
			query.setParameter("filtroSeq", filtroSeq);
		}
		if (filtroUslUnfSeq != null) {
			query.setParameter("filtroUslUnfSeq", filtroUslUnfSeq);
		}
		if (filtroProcedimento != null) {
			query.setParameter("filtroProcedimento", filtroProcedimento);
		}
		if (filtroEnviaSamis != null) {
			query.setParameter("filtroEnviaSamis", filtroEnviaSamis);
		}
		if (filtroSituacao != null) {
			query.setParameter("filtroSituacao", filtroSituacao);
		}
		if (espSeq != null) {
			query.setParameter("espSeq", espSeq);
		}
		if (eqpSeq != null) {
			query.setParameter("eqpSeq", eqpSeq);
		}
		if (preSerMatricula != null) {
			query.setParameter("preSerMatricula", preSerMatricula);
		}
		if (preSerVinCodigo != null) {
			query.setParameter("preSerVinCodigo", preSerVinCodigo);
		}
		if (pjqSeq != null) {
			query.setParameter("pjqSeq", pjqSeq);
		}
		if (dtEm != null) {
			query.setParameter("dtEm", dtEm);
		}
		if (dataAtual != null) {
			query.setParameter("dataAtual", dataAtual);
		}
		if (dtInicio != null) {
			query.setParameter("dtInicio", dtInicio);
		}
		if (dtFim != null) {
			query.setParameter("dtFim", dtFim);
		}
		if (dtAtual != null) {
			query.setParameter("dtAtual", dtAtual);
		}
		if (dtInicioUltGeracao != null) {
			query.setParameter("dtInicioUltGeracao", dtInicioUltGeracao);
		}
		if (dtFimUltGeracao != null) {
			query.setParameter("dtFimUltGeracao", dtFimUltGeracao);
		}
		if (dtInicioUltGeracaoDiaSeguinte != null) {
			query.setParameter("dtInicioUltGeracaoDiaSeguinte", dtInicioUltGeracaoDiaSeguinte);
		}
		if (dtFimUltGeracaoDiaSeguinte != null) {
			query.setParameter("dtFimUltGeracaoDiaSeguinte", dtFimUltGeracaoDiaSeguinte);
		}

		return query;
    }
    
    public Long listarAgendamentoConsultasCount(Integer filtroSeq, Short filtroUslUnfSeq, Boolean filtroProcedimento,
			Boolean filtroEnviaSamis, DominioSituacao filtroSituacao, AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe,
			RapServidores profissional, AelProjetoPesquisas filtroProjeto, Date filtroDtEm, Date filtroDtInicio, Date filtroDtFim,
			Date filtroDtInicioUltGeracao, Date filtroDtFimUltGeracao) {

		Long count = 0L;
		Query query = montarPesquisaAacGradeAgendamenConsultas(filtroSeq, filtroUslUnfSeq, filtroProcedimento, filtroEnviaSamis,
			filtroSituacao, filtroEspecialidade, filtroEquipe, profissional, filtroProjeto, filtroDtEm, filtroDtInicio, filtroDtFim,
			filtroDtInicioUltGeracao, filtroDtFimUltGeracao, true);

		count = (Long) query.uniqueResult();
		return count;
    }

    /**
     * 
     * @return Lista contendo os resultados da pesquisa conforme os filtros
     *         informados
     */
    @SuppressWarnings({ "unchecked" })
    public List<GradeAgendamentoVO> listarAgendamentoConsultas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroSeq, Short filtroUslUnfSeq, Boolean filtroProcedimento, Boolean filtroEnviaSamis, DominioSituacao filtroSituacao,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, RapServidores profissional, AelProjetoPesquisas filtroProjeto,
			Date filtroDtEm, Date filtroDtInicio, Date filtroDtFim, Date filtroDtInicioUltGeracao, Date filtroDtFimUltGeracao) {

		Query query = montarPesquisaAacGradeAgendamenConsultas(filtroSeq, filtroUslUnfSeq, filtroProcedimento, filtroEnviaSamis,
			filtroSituacao, filtroEspecialidade, filtroEquipe, profissional, filtroProjeto, filtroDtEm, filtroDtInicio, filtroDtFim,
			filtroDtInicioUltGeracao, filtroDtFimUltGeracao, false);

		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (maxResult != null) {
			query.setMaxResults(maxResult);
		}

		return query.list();
    }

    public List<AacGradeAgendamenConsultas> listarDisponibilidadeHorariosEmergencia(String orderProperty, boolean asc) {
		Query query = montarPesquisaDisponibilidadeHorariosEmergencia();

		List<AacGradeAgendamenConsultas> lista = query.list();
		return lista;
    }

    private Query montarPesquisaDisponibilidadeHorariosEmergencia() {
		StringBuffer hql = new StringBuffer(300);
		hql.append("select distinct agac");

		hql.append(" from AacGradeAgendamenConsultas agac ");
		hql.append("inner join agac.gradeSituacao gs ");
		hql.append("inner join agac.especialidade esp ");
		hql.append("inner join esp.caracteristicas ecr");

		hql.append(" where ");
		hql.append("agac.").append(AacGradeAgendamenConsultas.Fields.SEQ.toString()).append(" = gs.")
			.append(AacGradeSituacao.Fields.GRD_SEQ.toString()).append(' ');
		hql.append(" and ");
		hql.append("gs.").append(AacGradeSituacao.Fields.IND_SITUACAO.toString());
		hql.append(" = :filtroSituacao ");
		hql.append(" and ");
		hql.append("(gs.dtFimSituacao is null or gs.dtFimSituacao >= :data1)  ");
		hql.append(" and ");
		hql.append(" ecr.").append(AghCaractEspecialidades.Fields.CARACTERISTICA.toString()).append(" = :emergencia");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("filtroSituacao", DominioSituacao.A);
		query.setParameter("emergencia", DominioCaracEspecialidade.EMERGENCIA);
		query.setParameter("data1", DateUtil.truncaData(new Date()));

		return query;
    }

    public List<AacGradeAgendamenConsultas> listarDisponibilidadeHorarios(Integer filtroSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador filtroPagador,
			AacTipoAgendamento filtroTipoAgendamento, AacCondicaoAtendimento filtroCondicao, Date dtConsulta, Date horaConsulta,
			Date mesInicio, Date mesFim, DominioDiaSemana dia, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala, DataInicioFimVO turno,
			List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {

		Query query = montarPesquisaDisponibilidadeHorarios(filtroSeq, filtroUslUnfSeq, filtroEspecialidade, filtroEquipe,
			filtroProfissional, false, filtroPagador, filtroTipoAgendamento, filtroCondicao, dtConsulta, horaConsulta, mesInicio,
			mesFim, dia, zona, zonaSala, turno, listEspecialidade, visualizarPrimeirasConsultasSMS);
		
		List<AacGradeAgendamenConsultas> lista = query.list();

		return lista;
    }

    public List<AacGradeAgendamenConsultas> listarDisponibilidadeHorarios(Integer filtroSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador filtroPagador,
			AacTipoAgendamento filtroTipoAgendamento, AacCondicaoAtendimento filtroCondicao, Date dtConsulta, Date horaConsulta,
			Date mesInicio, Date mesFim, DominioDiaSemana dia, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {
    	
    	return listarDisponibilidadeHorarios(filtroSeq, filtroUslUnfSeq, filtroEspecialidade,
        		filtroEquipe, filtroProfissional, filtroPagador, filtroTipoAgendamento, filtroCondicao, dtConsulta, horaConsulta,
        		mesInicio, mesFim, dia, null, null, null, null, visualizarPrimeirasConsultasSMS);
    }
    
    public Long listarDisponibilidadeHorariosCount(Integer filtroSeq, Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade,
			AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento, Date dtConsulta, Date horaConsulta, Date mesInicio, Date mesFim,
			DominioDiaSemana dia, Boolean disponibilidade, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,	DataInicioFimVO turno,
			List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {

		Query query = montarPesquisaDisponibilidadeHorarios(filtroSeq, filtroUslUnfSeq, filtroEspecialidade, filtroEquipe,
			filtroProfissional, true, pagador, tipoAgendamento, condicaoAtendimento, dtConsulta, horaConsulta, mesInicio,
			mesFim, dia, zona, zonaSala, turno, listEspecialidade, visualizarPrimeirasConsultasSMS);

		return (Long) query.uniqueResult();
    }

    public List<AacGradeAgendamenConsultas> listarAgendamentoConsultasSituacao(AacGradeAgendamenConsultas grade) {
		DetachedCriteria criteria = criarCriteriaPesquisaAacGradeAgendamenConsultasSituacao(grade);

		return executeCriteria(criteria);
    }

    public List<AacGradeAgendamenConsultas> listarConsultasProgramadas(Short UnfSeq, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString(), UnfSeq));
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.IND_PROGRAMADA.toString(), true));

		return executeCriteria(criteria);
    }

    private static final String PONTO = ".";

   	public AacGradeAgendamenConsultas obterGradeAgendamento(Integer seq) {
		StringBuilder hql = new StringBuilder(700);
		hql.append("select grd from AacGradeAgendamenConsultas grd ")
			.append("left outer join fetch grd.profEspecialidade prof ")
			.append("     left outer join fetch prof.rapServidor rap ")
			.append("          left outer join fetch rap.pessoaFisica pf ")
			.append("left outer join fetch grd.condicaoAtendimento cond ")
			.append("left outer join fetch grd.pagador pag ")
			.append("left outer join fetch grd.tipoAgendamento tipo ")
			.append("left outer join fetch grd.aacConsultas aac ")
			.append("inner join fetch grd.servidor ser ")
			.append("      inner join fetch ser.pessoaFisica pesfis ")
			.append("inner join fetch grd.especialidade esp ")
			.append("inner join fetch grd.aacUnidFuncionalSala sala ")
			.append("inner join fetch grd.unidadeFuncional unf ")
			.append("inner join fetch grd.equipe eqp ")
			.append("inner join fetch grd.").append(AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString()).append(" siglaUnfSala ")
			.append("where grd.seq = :seq ");
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("seq", seq);
		List<AacGradeAgendamenConsultas> list = query.getResultList();

		return list != null && !list.isEmpty() ? list.get(0) : null;
    }
    
    public List<AacGradeAgendamenConsultas> listarAgendamentoConsultasEquipe(AacGradeAgendamenConsultas grade) {
		DetachedCriteria criteria = criarCriteriaPesquisaAacGradeAgendamenConsultasEquipes(grade);

		return executeCriteria(criteria);
    }

    private DetachedCriteria criarCriteriaPesquisaAacGradeAgendamenConsultasEquipes(AacGradeAgendamenConsultas grade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);

		if (grade.getAacUnidFuncionalSala().getId().getUnfSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString(), grade
				.getAacUnidFuncionalSala().getId().getUnfSeq()));
		}

		if (grade.getAacUnidFuncionalSala().getId().getSala() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_ID_SALA.toString(), grade.getAacUnidFuncionalSala()
				.getId().getSala()));
		}

		if (grade.getEquipe().getSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), grade.getEquipe().getSeq()));
		}

		if (grade.getEspecialidade().getSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), grade.getEspecialidade().getSeq()));
		}

		if (grade.getProcedimento() != null) {
			criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString(), grade.getProcedimento()));
		}

		criteria.add(Restrictions.isNull(AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString()));
		criteria.add(Restrictions.isNull(AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString()));

		return criteria;
    }

    public Long listarGradeAgendamenConsultasCount(AacFormaAgendamento formaAgendamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.FORMA_AGENDAMENTO.toString(), formaAgendamento));
		return executeCriteriaCount(criteria);
    }

    public List<AacGradeAgendamenConsultas> quantidadeGradeAgendamentosConsultaPorEspSeq(Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString(), false));
		return executeCriteria(criteria);
    }

    /**
     * Pesquisa registros por Zona/Sala
     * 
     * @param unfSeq
     * @param sala
     * @return
     */
    public List<AacGradeAgendamenConsultas> pesquisarAacGradeAgendamenConsultasPorZonaESala(Short unfSeq, Byte sala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.USL_SALA.toString(), sala));
		return executeCriteria(criteria);
    }

    public List<AacGradeAgendamenConsultas> pesquisarGradeAgendamenConsultasPorSeqEFormaAgendamento(Integer seq, Short caaSeq,
	    Short tagSeq, Short pgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);

		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.SEQ.toString(), seq));

		criteria.add(Restrictions.or(Restrictions.eq(AacGradeAgendamenConsultas.Fields.FAG_CAA_SEQ.toString(), caaSeq),
			Restrictions.isNull(AacGradeAgendamenConsultas.Fields.FAG_CAA_SEQ.toString())));

		criteria.add(Restrictions.or(Restrictions.eq(AacGradeAgendamenConsultas.Fields.FAG_TAG_SEQ.toString(), tagSeq),
			Restrictions.isNull(AacGradeAgendamenConsultas.Fields.FAG_TAG_SEQ.toString())));

		criteria.add(Restrictions.or(Restrictions.eq(AacGradeAgendamenConsultas.Fields.FAG_PGD_SEQ.toString(), pgdSeq),
			Restrictions.isNull(AacGradeAgendamenConsultas.Fields.FAG_PGD_SEQ.toString())));

		return executeCriteria(criteria);
    }

    /**
     * Implementa o cursor <code>c_get_cbo_cons</code>
     * 
     * @param conNumero
     */
    public List<AacGradeAgendamenConsultas> executaCursorGetCboExame(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "grd");
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "con");
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "eqp");
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "sera", Criteria.LEFT_JOIN);
		criteria.createAlias("eqp." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "serb");
		criteria.add(Restrictions.eq("con." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		return executeCriteria(criteria);
    }

    public List<AacGradeAgendamenConsultas> listarGradesAgendamentoConsultaPorEspecialidade(AghEspecialidades especialidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		return executeCriteria(criteria);
    }

    /**
     * Listar grades angendamento consultas
     * 
     * @param conNumero
     * @param indProcedimento
     * @return
     */
    public Integer obterGradeAgendamentoConsultaSeqPorConNumeroEIndProcedimento(Integer conNumero, Boolean indProcedimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "grd");
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "con");
		criteria.setProjection(Projections.property("con." + AacConsultas.Fields.GRD_SEQ.toString()));
		criteria.add(Restrictions.eq("con." + AacConsultas.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq("grd." + AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString(), indProcedimento));
		return (Integer) executeCriteriaUniqueResult(criteria);
    }

    /**
     * Listar grades angendamento consultas por aquipe
     * 
     * @param equipe
     * @return
     */
    public Boolean listarGradesAgendamentoConsultaPorEquipe(AghEquipes equipe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), equipe.getSeq()));
		return executeCriteriaCount(criteria) > 0 ? true : false;
    }

    public Short obterUnidadeAssociadaAgendaPorNumeroConsulta(Integer conNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.distinct(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString())));
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public AacGradeAgendamenConsultas obterGradesAgendaPorEspecialidadeDataLimiteParametros(Short espSeq, Short pPagadorSUS,
			Short pTagDemanda, Short pCondATEmerg, Date dataLimite) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "AAC_GRADE");
		criteria.createAlias("AAC_GRADE." + AacGradeAgendamenConsultas.Fields.AAC_GRADE_SITUACAO.toString(), "AAC_GRADE_SITUACAO");
		criteria.add(Restrictions.eq("AAC_GRADE." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		if (pPagadorSUS != null) {
			criteria.add(Restrictions.eq("AAC_GRADE." + AacGradeAgendamenConsultas.Fields.FAG_PGD_SEQ.toString(), pPagadorSUS));
		}
		if (pTagDemanda != null) {
			criteria.add(Restrictions.eq("AAC_GRADE." + AacGradeAgendamenConsultas.Fields.FAG_TAG_SEQ.toString(), pTagDemanda));
		}
		if (pCondATEmerg != null) {
			criteria.add(Restrictions.eq("AAC_GRADE." + AacGradeAgendamenConsultas.Fields.FAG_CAA_SEQ.toString(), pCondATEmerg));
		}
		criteria.add(Restrictions.eq("AAC_GRADE_SITUACAO." + AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.le("AAC_GRADE_SITUACAO." + AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString(), dataLimite));
		criteria.add(Restrictions.or(Restrictions.isNull("AAC_GRADE_SITUACAO." + AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()),
			Restrictions.ge("AAC_GRADE_SITUACAO." + AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString(), dataLimite)));
		List<AacGradeAgendamenConsultas> listGradeAgendConsulta = executeCriteria(criteria);
		return (listGradeAgendConsulta != null && !listGradeAgendConsulta.isEmpty()) ? listGradeAgendConsulta.get(0) : null;
    }

    /**
     * Obter lista de consultas teve especialidade alterada
     * 
     * @param espSeq
     * @param grdSeq
     * @param conNumero
     * @return
     */
    public List<ConsultaEspecialidadeAlteradaVO> obterConsultasEspecialidadeAlterada(Short espSeq, Integer grdSeq, Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultasJn.class, "JN");
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "AAC");
		criteria.add(Restrictions.eq("JN." + AacConsultasJn.Fields.NUMERO.toString(), conNumero));
		if (grdSeq != null) {
			criteria.add(Restrictions.eq("JN." + AacConsultasJn.Fields.GRD_SEQ.toString(), grdSeq));
		}
		if (espSeq != null) {
			subCriteria.add(Restrictions.eq("AAC." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		}
		subCriteria.setProjection(Projections.projectionList().add(
			Projections.property("AAC." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString())));
		subCriteria.add(Restrictions.eqProperty("AAC." + AacGradeAgendamenConsultas.Fields.SEQ.toString(), "JN."
			+ AacConsultasJn.Fields.GRD_SEQ.toString()));
		criteria.add(Subqueries.exists(subCriteria));

		ProjectionList projection = Projections
			.projectionList()
			.add(Projections.property("JN." + AacConsultasJn.Fields.DATA_ALTERACAO.toString()),
				ConsultaEspecialidadeAlteradaVO.Fields.JN_DATE_TIME.toString())
			.add(Projections.property("JN." + AacConsultasJn.Fields.GRD_SEQ), ConsultaEspecialidadeAlteradaVO.Fields.GRD_SEQ.toString());
		criteria.setProjection(Projections.distinct(projection));
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaEspecialidadeAlteradaVO.class));
		return executeCriteria(criteria);
    }

    public List<GradeAgendamentoAmbulatorioVO> listarQuantidadeConsultasAmbulatorio(List<String> sitConsultas, Short espSeq,
			String situacaoMarcado, Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg, boolean isDtConsultaMaiorDtAtual) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.setProjection(Projections
			.projectionList()
			.add(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString()),
				GradeAgendamentoAmbulatorioVO.Fields.USL_UNF_SEQ.toString())
			.add(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()),
				GradeAgendamentoAmbulatorioVO.Fields.ESP_SEQ.toString())
			.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()),
				GradeAgendamentoAmbulatorioVO.Fields.DT_CONSULTA.toString())
			.add(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString()),
				GradeAgendamentoAmbulatorioVO.Fields.NUMERO.toString())
			.add(Projections.property("CON." + AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString()),
				GradeAgendamentoAmbulatorioVO.Fields.IND_SIT_CONSULTA.toString())
			.add(Projections.property("CON." + AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString()),
				GradeAgendamentoAmbulatorioVO.Fields.IND_EXCEDE_PROGRAMACAO.toString())
			.add(Projections.property("CON." + AacConsultas.Fields.PAC_CODIGO.toString()),
				GradeAgendamentoAmbulatorioVO.Fields.PAC_CODIGO.toString())
			.add(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()),
				GradeAgendamentoAmbulatorioVO.Fields.GRD_SEQ.toString()));
		criteria.add(Restrictions.in("CON." + AacConsultas.Fields.SITUACAO_CONSULTA_SIT, sitConsultas));
		criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));

		StringBuffer sql = new StringBuffer(40);
		sql.append(" '" + situacaoMarcado + "' " + " IN ('S', 'N') ");
		criteria.add(Restrictions.sqlRestriction(sql.toString()));

		if (pPagadorSUS != null) {
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.FAG_PGD_SEQ.toString(), pPagadorSUS));
		}
		if (pTagDemanda != null) {
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.FAG_TAG_SEQ.toString(), pTagDemanda));
		}
		if (pCondATEmerg != null) {
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.FAG_CAA_SEQ.toString(), pCondATEmerg));
		}

		if (isDtConsultaMaiorDtAtual) {
			criteria.add(Restrictions.gt("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), new Date()));

		} else {
			// #39983
			criteria.add(Restrictions.ge("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.obterDataComHoraInical(new Date())));
			criteria.add(Restrictions.le("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.obterDataComHoraFinal(new Date())));
			criteria.addOrder(Order.asc("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		}

		criteria.setResultTransformer(Transformers.aliasToBean(GradeAgendamentoAmbulatorioVO.class));
		return executeCriteria(criteria);
    }

    public List<AacGradeAgendamenConsultas> listarGradeUnidFuncionalECaracteristicas(Integer numeroConsulta) {
		String ponto = ".";
		String aliasGrd = "GRD";
		String aliasCon = "CON";
		String aliasUnf = "UNF";
		String aliasCar = "CAR";
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, aliasGrd);

		criteria.createAlias(aliasGrd + ponto + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), aliasCon, JoinType.INNER_JOIN);
		criteria.createAlias(aliasGrd + ponto + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), aliasUnf, JoinType.INNER_JOIN);
		criteria.createAlias(aliasUnf + ponto + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), aliasCar, JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(aliasCon + ponto + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));

		return executeCriteria(criteria);
	}
	
	public Long buscarGradeAgendamentoExistentePorSetorEspecialidadeEquipe(AacGradeAgendamenConsultas grade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), grade.getUslSala()));
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), grade.getEspecialidade()));
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), grade.getEquipe()));
		return executeCriteriaCount(criteria);
	}    
	
	/**
	 * C2 estoria 40230
	 * @param grd_seq
	 * @return
	 */
	public AacGradeAgendamenConsultas obterPorChavePrimariaEspecialidadeUnidFuncionalSala(Integer grdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.property(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));
		proList.add(Projections.property(AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString())); 
		criteria.setProjection(proList);
		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.SEQ.toString(), grdSeq));
		AacGradeAgendamenConsultas AacGradeAgendamenConsultas = (AacGradeAgendamenConsultas) executeCriteriaUniqueResult(criteria);
		
		if (AacGradeAgendamenConsultas != null) {
			return AacGradeAgendamenConsultas;
		}
		
		return null;
	}

    public List<Integer> obterGradesComHorariosDisponiveis(Short espSeq, Date dataInicio, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "CON", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.property("CON." + AacConsultas.Fields.GRD_SEQ.toString())));
		
		criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(), "L"));
		criteria.add(Restrictions.ge("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), dataInicio));
		criteria.add(Restrictions.le("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), dataFim));
		
		criteria.addOrder(Order.asc("CON." + AacConsultas.Fields.GRD_SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AgendamentoAmbulatorioVO> obterHorariosDisponiveisAmbulatorio(Short espSeq, Integer grdSeq, Date dataInicio, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "CON", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CON." + AacConsultas.Fields.GRD_SEQ.toString())
						, AgendamentoAmbulatorioVO.Fields.GRD_SEQ.toString())
				.add(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString())
						, AgendamentoAmbulatorioVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString())
						, AgendamentoAmbulatorioVO.Fields.DATA_CONSULTA.toString()));
		
		criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(), "L"));
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.ge("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), dataInicio));
		criteria.add(Restrictions.le("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), dataFim));
		
		criteria.addOrder(Order.asc("CON." + AacConsultas.Fields.GRD_SEQ.toString()))
			.addOrder(Order.asc("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AgendamentoAmbulatorioVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * #27521 #8236 #8233 C2
	 * Query para obter consultas da grade de agendamento para o relatório de Agenda de Consultas
	 * @param dataConsulta
	 * @param grade
	 * @param turno
	 * @param especialidade
	 * @param unidadeFuncional
	 * @return Lista com as consultas de acordo com o filtro passado para o relatório de Agenda de Consultas
	 */
	public List<ConsultasRelatorioAgendaConsultasVO> obterConsultasGradeAgendamentoRelatorioAgenda(Date dataConsulta, Integer grade, Integer turno, 
			Short especialidade, Short unidadeFuncional, Byte seqSala){
		String[] ind_situacao_consulta_valores = {"M","L"};
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "GRD");
	
		//JOIN
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "CON", JoinType.INNER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.IND_SIT_CONSULTA.toString(), "SIT", JoinType.INNER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CAA", JoinType.INNER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections
				.projectionList()
					.add(Projections
						.property("CON." + AacConsultas.Fields.NUMERO.toString()),
						"numeroConsulta")
					.add(Projections
						.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()),
						"prontuario")
					.add(Projections
						.property("PAC." + AipPacientes.Fields.NOME.toString()),
						"nomePaciente")
					.add(Projections
						.property("PAC." + AipPacientes.Fields.CAD_CONFIRMADO.toString()),
						"pacConfirmado")
					.add(Projections
						.property("CAA." + AacCondicaoAtendimento.Fields.DESCRICAO.toString()),
						"condicaoAtendimentoDescricao")
					.add(Projections
						.property("PAC." + AipPacientes.Fields.CODIGO.toString()),
						"codigoPaciente")
					.add(Projections
						.property("CON." + AacConsultas.Fields.RETORNO_SEQ.toString()),
						"seqRetorno")
					.add(Projections
							.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()),
							"dataConsulta")
					.add(Projections
						.property("PAC." + AipPacientes.Fields.DT_OBITO.toString()),
						"pacienteDataObito")
					.add(Projections
						.property("PAC." + AipPacientes.Fields.TIPO_DATA_OBITO.toString()),
						"pacienteTipoDataObito")
					.add(Projections
						.property("GRD." + AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString()),
						"seqUnidadeFuncional")
					.add(Projections
						.property("ESP." + AghEspecialidades.Fields.SEQ.toString()),
						"seqEspecialidade")
					.add(Projections
						.property("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()),
						"seqGrade")
					.add(Projections
						.property("PAC." + AipPacientes.Fields.ID_SISTEMA_LEGADO.toString()),
						"prontuarioFamilia")
				);
				
		//WHERE
		criteria.add(Restrictions.in("SIT." + AacSituacaoConsultas.Fields.SITUACAO.toString(), ind_situacao_consulta_valores));

		if (seqSala != null) {
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_ID_SALA.toString(), seqSala));
		}
		
		if(grade != null){
			criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grade));
		}
		if(especialidade != null){
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), especialidade));
		}
		if(unidadeFuncional != null){
			criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeFuncional));
		}
		Date dataInicio = new Date();
		Date dataFim = new Date();
		
		if(dataConsulta != null){
			if(turno != null){
				Calendar dataPeriodoInicial = Calendar.getInstance();
				dataPeriodoInicial.setTime(dataConsulta);
				Calendar dataPeriodoFinal = Calendar.getInstance();
				dataPeriodoFinal.setTime(dataConsulta);
				
				if(turno == DominioTurno.M.getCodigo()){
					dataPeriodoInicial.set(Calendar.HOUR, 00);
					dataPeriodoInicial.set(Calendar.HOUR_OF_DAY, 00);
					dataPeriodoInicial.set(Calendar.MINUTE, 00);
					dataPeriodoInicial.set(Calendar.SECOND, 00);
					
					dataPeriodoFinal.set(Calendar.HOUR, 11);
					dataPeriodoFinal.set(Calendar.HOUR_OF_DAY, 11);
					dataPeriodoFinal.set(Calendar.MINUTE, 59);
					dataPeriodoFinal.set(Calendar.SECOND, 59);
				}else if (turno == DominioTurno.T.getCodigo()){
					dataPeriodoInicial.set(Calendar.HOUR, 12);
					dataPeriodoInicial.set(Calendar.HOUR_OF_DAY, 12);
					dataPeriodoInicial.set(Calendar.MINUTE, 00);
					dataPeriodoInicial.set(Calendar.SECOND, 00);
					
					dataPeriodoFinal.set(Calendar.HOUR, 15);
					dataPeriodoFinal.set(Calendar.HOUR_OF_DAY, 15);
					dataPeriodoFinal.set(Calendar.MINUTE, 59);
					dataPeriodoFinal.set(Calendar.SECOND, 59);
				}else if (turno == DominioTurno.N.getCodigo()){
					dataPeriodoInicial.set(Calendar.HOUR, 16);
					dataPeriodoInicial.set(Calendar.HOUR_OF_DAY, 16);
					dataPeriodoInicial.set(Calendar.MINUTE, 00);
					dataPeriodoInicial.set(Calendar.SECOND, 00);					
					dataPeriodoFinal.set(Calendar.HOUR, 23);
					dataPeriodoFinal.set(Calendar.HOUR_OF_DAY, 23);
					dataPeriodoFinal.set(Calendar.MINUTE, 59);
					dataPeriodoFinal.set(Calendar.SECOND, 59);
				}
				dataInicio = dataPeriodoInicial.getTime();
				dataFim = dataPeriodoFinal.getTime();
			} else{
				dataInicio = DateUtil.obterDataComHoraInical(dataConsulta);
				dataFim = DateUtil.obterDataComHoraFinal(dataConsulta);
			}
			criteria.add(Restrictions.between("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), 
					dataInicio, dataFim));
		}		
		//ORDER BY
		criteria.addOrder(Order.asc("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.addOrder(Order.asc("CON." + AacConsultas.Fields.NUMERO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultasRelatorioAgendaConsultasVO.class));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * #27521 #8236 #8233 C3
	 * Query para obter os encaminhamentos associados às consultas do relatório de Agenda de Consultas
	 * @param grade
	 * @param codigoPaciente
	 * @param dataConsulta
	 * @return Lista com as consultas que são encaminhamentos para o relatório de Agenda de Consultas
	 */
	public List<EncaminhamentosRelatorioAgendaConsultasVO> obterEncaminhamentosRelatorioAgenda(Integer grade, Integer codigoPaciente, Date dataConsulta){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "GRD");
		//JOIN
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "CON", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA, "UND", JoinType.INNER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList().add(Projections
						.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()),
						"dataConsulta").add(Projections
						.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()),
						"prontuario").add(Projections
						.property("UND." + VAacSiglaUnfSala.Fields.DESCRICAO.toString()),
						"descricaoSetor").add(Projections
						.property("UND." + VAacSiglaUnfSala.Fields.SALA.toString()),
						"sala").add(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()),
						"codigoPaciente").add(Projections
						.property("GRD." + AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString()),
						"seqUnidadeFuncional").add(Projections
						.property("ESP." + AghEspecialidades.Fields.SEQ.toString()),
						"seqEspecialidade").add(Projections
						.property("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()),
						"seqGrade")
		);		
		//WHERE
		if(dataConsulta != null){
			criteria.add(Restrictions.between("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), 
					DateUtil.obterDataComHoraInical(dataConsulta), DateUtil.obterDataComHoraFinal(dataConsulta)));
		}
		if(grade != null){
			criteria.add(Restrictions.ne("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString(), grade));
		}
		if(codigoPaciente != null){
			criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		}
		criteria.add(Restrictions.isNotNull("PAC." + AipPacientes.Fields.PRONTUARIO.toString()));
		//filtro por OBTER_ENVIO_PRONT = S realizado em RelatorioAgendaConsultasON		
		criteria.setResultTransformer(Transformers.aliasToBean(EncaminhamentosRelatorioAgendaConsultasVO.class));
		return this.executeCriteria(criteria); 
	}
	//Querys da estória 40299------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param c_pac_codigo
	 * @param c_esp_seq
	 * @return
	 * Estoria: 40229 consulta c7
	 */
	public boolean verificaInterconsultaMarcadaPorEspecialidadeSeqPacienteCodigo(Integer cPacCodigo,Short cEspSeq){
		Date currentDate = new Date();
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "CON", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("CON."+AacConsultas.Fields.PAC_CODIGO.toString(), cPacCodigo));
		criteria.add(Restrictions.gt("CON."+AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.adicionaDias(currentDate, 1)));		
		criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), cEspSeq));
		if(!executeCriteria(criteria).isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * #44179 F05 MAMC_EMG_GET_ESP.cur_tei
	 * @param mam_triagens.seq%type
	 * @return espSeq
	 */
	public CurTeiVO obterEspPorTriagem(Long seq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class,"GRD");
		criteria.createAlias("GRD."+AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "CON");
		criteria.createAlias("CON."+AacConsultas.Fields.TRG_ENC_INTERNO.toString(), "TEI");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("TEI."+MamTrgEncInterno.Fields.SEQP.toString()),CurTeiVO.Fields.SEQ_P.toString())
				.add(Projections.property("TEI."+MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()),CurTeiVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("GRD."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()),CurTeiVO.Fields.ESP_SEQ.toString()));
		criteria.add(Restrictions.eq("TEI."+MamTrgEncInterno.Fields.TRG_SEQ.toString(),seq));
		criteria.add(Restrictions.isNull("TEI."+MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		criteria.add(Restrictions.eqProperty("TEI."+MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString(),"CON."+AacConsultas.Fields.CON_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("GRD."+AacGradeAgendamenConsultas.Fields.SEQ.toString(),"CON."+AacConsultas.Fields.GRD_SEQ.toString()));
		
		criteria.addOrder(Order.desc("TEI."+MamTrgEncInterno.Fields.SEQP.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CurTeiVO.class));
		
		List<CurTeiVO> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()){
			return  lista.get(0);
		} else {
			return null;
		}		
	}
	
	public AacGradeAgendamenConsultas obterGradeAgendamentoParaMarcacaoConsultaEmergencia(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class);
		// Profissional especialidade
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString(), "PROF_ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROF_ESP.".concat(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString()), "PROF_ESP_SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROF_ESP_SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PROF_ESP_PES", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(AacGradeAgendamenConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "COND_ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.PAGADOR.toString(), "PAGADOR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.TIPO_AGENDAMENTO.toString(), "TPO_AGD", JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias(AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "AAC_CON", JoinType.LEFT_OUTER_JOIN);

		// Servidor
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "SER_PES", JoinType.LEFT_OUTER_JOIN);

		// Servidor alterado
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.SERVIDOR_ALTERADO.toString(), "SER_ALT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_ALT.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "SER_ALT_PES", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), "V_ACC_SIGLA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(AacGradeAgendamenConsultas.Fields.PROJETO_PESQUISA.toString(), "PROJETO", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AacGradeAgendamenConsultas.Fields.SEQ.toString(), seq));
		return (AacGradeAgendamenConsultas) executeCriteriaUniqueResult(criteria);
    }
	/**
	 * #43033 P1 
	 * Consultas para obter CURSOR CURSOR c_con_dados
	 * Substitui CURSOR c_con_dados em P1
	 * @param cSorSeq
	 * @return
	 */
	public List<CursorConDadosVO> obterCursorCConDadosVO(Long cSorSeq){
		CursorCConDadosVOQueryBuilder builder = new CursorCConDadosVOQueryBuilder();
		return executeCriteria(builder.build(cSorSeq, isOracle()));		
}
	
	/**
	 * #43033 P1 
	 * Consultas para obter CURSOR CURSOR c_con_dados
	 * Substitui CURSOR c_TICKET_DADOS em P1
	 * @param cSorSeq
	 * @return
	 */
	public List<CursorCTicketDadosVO> obterCursorCTicketDadosVO(Integer cConNumeroRetorno){
		CursorCTicketDadosVOQueryBuilder builder = new CursorCTicketDadosVOQueryBuilder();
		return executeCriteria(builder.build(cConNumeroRetorno));
	}
	
	/**
	 * #43033 C2 
	 * Consultas para obter AacGradeAgendamenConsultasVO
	 * @param cSorSeq
	 * @return
	 */
	public AacGradeAgendamenConsultasVO obterAacGradeAgendamenConsultasVOPorSeq(Integer seq){
		AacGradeAgendamenConsultaVOQueryBuilder builder = new AacGradeAgendamenConsultaVOQueryBuilder();
		return (AacGradeAgendamenConsultasVO) executeCriteriaUniqueResult(builder.build(seq));
	}
	
	/**#6807
	 * C6
	 * Consulta para popular a listagem de Grades.
	 */
	public List<GradeVO> obterAacGradeAgendamentoConsultas(FiltroGradeConsultasVO filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class,"G");
		
		criteria.createAlias(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(),"E",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(),"SS",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(),"ES",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.PESSOA_SERVIDOR.toString(),"P",JoinType.LEFT_OUTER_JOIN);
			
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.SEQ.toString()),GradeVO.Fields.SEQ.toString())
				.add(Projections.property("SS." + VAacSiglaUnfSala.Fields.SIGLA.toString()), GradeVO.Fields.SIGLA.toString())
				.add(Projections.property("SS." + VAacSiglaUnfSala.Fields.SALA.toString()), GradeVO.Fields.SALA.toString())
				.add(Projections.property(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString()),GradeVO.Fields.IND_PROCEDIMENTO.toString())
				.add(Projections.property("ES." + AghEspecialidades.Fields.SEQ.toString()),GradeVO.Fields.ESP_SEQ.toString())
				.add(Projections.property("ES." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()),GradeVO.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property("E." + AghEquipes.Fields.SEQ.toString()),GradeVO.Fields.EQP_SEQ.toString())
				.add(Projections.property("P." + VRapPessoaServidor.Fields.NOME.toString()),GradeVO.Fields.NOME_PROFISSIONAL.toString())
				.add(Projections.property("P." + VRapPessoaServidor.Fields.MATRICULA.toString()),GradeVO.Fields.MATRICULA_PROFISSIONAL.toString())
				.add(Projections.property("P." + VRapPessoaServidor.Fields.VINCODIGO.toString()),GradeVO.Fields.VINCULO_PROFISSIONAL.toString())
				.add(Projections.property("E." + AghEquipes.Fields.NOME.toString()),GradeVO.Fields.NOME_EQUIPE.toString()));
		
		criteria.add(Restrictions.eq(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		filtroObterAacGradeAgendamentoConsultas(filtro, criteria);
		
		criteria.addOrder(Order.asc(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(GradeVO.class));
		
		return executeCriteria (criteria);
}

	private void filtroObterAacGradeAgendamentoConsultas(
			FiltroGradeConsultasVO filtro, DetachedCriteria criteria) {
		if(filtro.getGrade() != null){
			criteria.add(Restrictions.eq(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.SEQ.toString(),filtro.getGrade()));
		}
		if(filtro.getSetorSalaSeq() != null){
			criteria.add(Restrictions.eq("SS." + VAacSiglaUnfSala.Fields.UNF_SEQ.toString(),filtro.getSetorSalaSeq()));
		}
		if(filtro.getIndProcedimento() != null){
			criteria.add(Restrictions.eq(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString(),filtro.getIndProcedimento()));
		}
		if(filtro.getEspSeq() != null){
			criteria.add(Restrictions.eq(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(),filtro.getEspSeq()));
		}
		if(filtro.getEqpSeq() != null){
			criteria.add(Restrictions.eq(ALIAS_G_PONTO + AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(),filtro.getEqpSeq()));
		}
		if(filtro.getMatricula() != null){
			criteria.add(Restrictions.eq("P." + VRapPessoaServidor.Fields.SER_MATRICULA.toString(), filtro.getMatricula()));
		}
		if(filtro.getVinculo()!= null){
			criteria.add(Restrictions.eq("P." + VRapPessoaServidor.Fields.SER_VIN_CODIGO.toString(), filtro.getVinculo()));
		}
	}
	
	public List<Object[]> obterGradeAgendamentoConsultas(Date dataInicio, Date dataFim, Integer grade, String sigla, Integer seqEquipe, 
			Integer servico, Integer matricula, Short vinculo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, ALIAS_GRD);
		criteria.createAlias(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), ALIAS_CON, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESP, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), "VUSL", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.PAGADOR.toString(), "PGD", JoinType.LEFT_OUTER_JOIN);
				
		criteria.add(Restrictions.between(ALIAS_CON_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString(), dataInicio, dataFim));
		criteria.add(Restrictions.eq(ALIAS_CON_PONTO + AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString(), DominioSimNao.N.isSim()));
		
		if(grade != null){			
			criteria.add(Restrictions.eq(ALIAS_CON_PONTO + AacConsultas.Fields.GRD_SEQ.toString(), grade));
		}
		if(sigla != null){			
			criteria.add(Restrictions.eq(ALIAS_ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString(), sigla));
		}
		if(seqEquipe != null){
			criteria.add(Restrictions.eq("EQP." + AghEquipes.Fields.SEQ.toString(), seqEquipe));
		}
		if(servico != null){
			criteria.add(Restrictions.eq(ALIAS_ESP_PONTO + AghEspecialidades.Fields.CENTRO_CUSTO_CODIGO.toString(), servico));
		}
		if(matricula != null){
			criteria.add(Restrictions.eq(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA_COD.toString(), matricula));
		}
		if(vinculo != null){
			criteria.add(Restrictions.eq(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_COD.toString(), vinculo));
		}
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.groupProperty(ALIAS_ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString()), AghEspecialidades.Fields.SIGLA.toString());
		projection.add(Projections.groupProperty(ALIAS_ESP_PONTO + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString());
		projection.add(Projections.groupProperty(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.SEQ.toString()), AacGradeAgendamenConsultas.Fields.SEQ.toString());
		projection.add(Projections.groupProperty("PGD." + AacPagador.Fields.SEQ.toString()), AacPagador.Fields.SEQ.toString());
		projection.add(Projections.groupProperty("PGD." + AacPagador.Fields.DESCRICAO.toString()), AacPagador.Fields.DESCRICAO.toString());
		projection.add(Projections.groupProperty("VUSL." + VAacSiglaUnfSala.Fields.SALA.toString()), VAacSiglaUnfSala.Fields.SALA.toString());
		projection.add(Projections.groupProperty("VUSL." + VAacSiglaUnfSala.Fields.SIGLA.toString()), VAacSiglaUnfSala.Fields.SIGLA.toString());
		projection.add(Projections.groupProperty("EQP." + AghEquipes.Fields.SEQ.toString()), AghEquipes.Fields.SEQ.toString());
		projection.add(Projections.groupProperty("EQP." + AghEquipes.Fields.NOME.toString()), AghEquipes.Fields.NOME.toString());
		projection.add(Projections.groupProperty(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA_COD.toString()), AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA_COD.toString());
		projection.add(Projections.groupProperty(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_COD.toString()), AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_COD.toString());
		
		criteria.setProjection(Projections.distinct(projection));
		
		criteria.addOrder(Order.asc(ALIAS_ESP_PONTO + AghEspecialidades.Fields.SIGLA.toString()));
		criteria.addOrder(Order.asc(ALIAS_GRD_PONTO + AacGradeAgendamenConsultas.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
}
