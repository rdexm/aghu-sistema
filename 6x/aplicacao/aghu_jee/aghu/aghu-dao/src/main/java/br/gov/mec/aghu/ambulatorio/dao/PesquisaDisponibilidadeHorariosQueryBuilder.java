package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;


@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity"
	, "PMD.InsufficientStringBufferDeclaration", "PMD.AvoidDuplicateLiterals"
	, "PMD.ConsecutiveLiteralAppends", "PMD.CyclomaticComplexity"})
public class PesquisaDisponibilidadeHorariosQueryBuilder extends QueryBuilder<Query> {

	private static final long serialVersionUID = 4454490573874613174L;

	@Override
	protected Query createProduct() {
		
		return null;
	}

	@Override
	protected void doBuild(Query aProduct) {
		// TODO Auto-generated method stub
		
	}
	
    public Query montarPesquisaDisponibilidadeHorarios(Integer filtroSeq, Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade,
			AghEquipes filtroEquipe, RapServidores filtroProfissional, boolean count, AacPagador filtroPagador,
			AacTipoAgendamento filtroTipoAgendamento, AacCondicaoAtendimento filtroCondicao, Date dtConsulta, Date horaConsulta,
			Date mesInicio, Date mesFim, DominioDiaSemana dia, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,
			DataInicioFimVO turno, List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS
			, Short seqTipoAgendamentoSMS, Short seqCondPrimeiraConsulta) throws ApplicationBusinessException {

		Short espSeq = null;
		Integer eqpSeq = null;
		Integer preSerMatricula = null;
		Short preSerVinCodigo = null;
		Date dtInicioUltGeracaoDiaSeguinte = null;
		Date dtFimUltGeracaoDiaSeguinte = null;

		StringBuffer hql = new StringBuffer(150);
		if (count) {
			hql.append("select count(distinct agac.seq)");
		} else {
			hql.append("select distinct agac");
		}

		hql.append(" from AacGradeAgendamenConsultas agac ")
		// if (filtroDtInicio != null && filtroDtFim != null) {
		.append("left join agac.aacConsultas ac ")
		.append("left join agac.siglaUnfSala sg ")
		.append("inner join agac.gradeSituacao gs ");
		// }
		
		if (filtroSeq != null || filtroUslUnfSeq != null || filtroEspecialidade != null || filtroEquipe != null
				|| filtroProfissional != null || dtInicioUltGeracaoDiaSeguinte != null || dtFimUltGeracaoDiaSeguinte != null
				|| filtroPagador != null || filtroTipoAgendamento != null || filtroCondicao != null || dtConsulta != null
				|| horaConsulta != null || mesInicio != null || mesFim != null || dia != null || (zona != null && zona.getUnfSeq() != null)
				|| zonaSala != null || turno != null || (listEspecialidade != null && !listEspecialidade.isEmpty())) {

			hql.append(" where ");

			boolean concatenouPrimeiraCondicao = false;

			hql.append("agac.").append(AacGradeAgendamenConsultas.Fields.SEQ.toString()).append(" = gs.")
			.append(AacGradeSituacao.Fields.GRD_SEQ.toString()).append(' ')
			.append(" and ")
			.append("gs.").append(AacGradeSituacao.Fields.IND_SITUACAO.toString())
			.append(" = :filtroSituacao ")

			.append(" and ")
			.append("(gs.dtFimSituacao is null or gs.dtFimSituacao >= :data1)  ");
			concatenouPrimeiraCondicao = true;

			if (filtroSeq != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("agac.")
				.append(AacGradeAgendamenConsultas.Fields.SEQ.toString())
				.append(" = :filtroSeq ");
			}

			if (filtroUslUnfSeq != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("agac.")
				.append(AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString())
				.append(" = :filtroUslUnfSeq ");
			}

			if (filtroEspecialidade != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				espSeq = filtroEspecialidade.getSeq();
				hql.append("agac.")
				.append(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString())
				.append(" = :espSeq ");
			} else if (listEspecialidade != null && !listEspecialidade.isEmpty()){
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("agac.")
				.append(AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString())
				.append(" in (:listEspecialidade) ");
			}

			if (filtroEquipe != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				eqpSeq = filtroEquipe.getSeq();
				hql.append("agac.")
				.append(AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString())
				.append(" = :eqpSeq ");

			}

			if (filtroProfissional != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				preSerMatricula = filtroProfissional.getId().getMatricula();
				preSerVinCodigo = filtroProfissional.getId().getVinCodigo();
				hql.append("agac.")
				.append(AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString())
				.append(" = :preSerMatricula ")
				.append("and agac.")
				.append(AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString())
				.append(" = :preSerVinCodigo ");
			}

			if (filtroPagador != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("ac.")
				.append(AacConsultas.Fields.FAG_PGD_SEQ.toString())
				.append(" = :pagador ");
			}

			if (filtroTipoAgendamento != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("ac.")
				.append(AacConsultas.Fields.FAG_TAG_SEQ.toString())
				.append(" = :tipoAgendamento ");
			}

			if (filtroCondicao != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("ac.")
				.append(AacConsultas.Fields.FAG_CAA_SEQ.toString())
				.append(" = :condicaoAtendimento ");
			}

			if (dtConsulta != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("ac.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" >= :data1 ")
				.append("and ac.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" <= :data2 ");
			}

			if (mesInicio != null) {
				hql.append("and ac.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" >= :mesInicio ");
			}
			if (mesFim != null) {
				hql.append("and ac.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" <= :mesFim ");
			}

			if (dia != null) {
				hql.append("and ac.").append(AacConsultas.Fields.DIA_SEMANA.toString()).append(" = :diaSemana ");
			}
			
			if (zonaSala != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("sg.").append(VAacSiglaUnfSala.Fields.SALA.toString()).append(" = :zonaSala ")
				.append(" and ")
				.append("sg.").append(VAacSiglaUnfSala.Fields.UNF_SEQ.toString()).append(" = :zonaSalaUnfSeq ");
			} else if (zona != null && zona.getUnfSeq() != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("sg.").append(VAacSiglaUnfSala.Fields.UNF_SEQ.toString()).append(" = :zonaUnfSeq ");
			}

			if (turno != null) {
				if (concatenouPrimeiraCondicao) {
					hql.append(" and ");
				} else {
					concatenouPrimeiraCondicao = true;
				}
				hql.append("to_char(ac.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(",'HH24:MI:SS') between (:dataTurnoInicio) ")
				.append(" and (:dataTurnoFim)");
			}

		}
		//#52061 - Caso não possua permissão específica não lista Primeiras Consultas da Secretaria Municipal da Saúde
		if (!visualizarPrimeirasConsultasSMS){
			hql.append("and (ac.").append(AacConsultas.Fields.CAA_SEQ.toString()).append(" <> :seqCondPrimeiraConsulta ")
			.append("or ac.").append(AacConsultas.Fields.TAG_SEQ.toString()).append(" <> :seqTipoAgendamentoSMS)");
		}

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("filtroSituacao", DominioSituacao.A);
		if (filtroSeq != null) {
			query.setParameter("filtroSeq", filtroSeq);
		}
		if (filtroUslUnfSeq != null) {
			query.setParameter("filtroUslUnfSeq", filtroUslUnfSeq);
		}
		if (espSeq != null) {
			query.setParameter("espSeq", espSeq);
		}else if (listEspecialidade != null && !listEspecialidade.isEmpty()){
			query.setParameterList("listEspecialidade", listEspecialidade);
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

		if (dtInicioUltGeracaoDiaSeguinte != null) {
			query.setParameter("dtInicioUltGeracaoDiaSeguinte", dtInicioUltGeracaoDiaSeguinte);
		}
		if (dtFimUltGeracaoDiaSeguinte != null) {
			query.setParameter("dtFimUltGeracaoDiaSeguinte", dtFimUltGeracaoDiaSeguinte);
		}
		if (filtroPagador != null) {
			query.setParameter("pagador", filtroPagador.getSeq());
		}
		if (filtroCondicao != null) {
			query.setParameter("condicaoAtendimento", filtroCondicao.getSeq());
		}
		if (filtroTipoAgendamento != null) {
			query.setParameter("tipoAgendamento", filtroTipoAgendamento.getSeq());
		}
		if (dtConsulta != null) {
			query.setParameter("data1", DateUtil.truncaData(dtConsulta));
			query.setParameter("data2", DateUtil.truncaDataFim(dtConsulta));
		} else {
			query.setParameter("data1", DateUtil.truncaData(new Date()));
		}
		
		if (mesInicio != null) {
			query.setParameter("mesInicio", mesInicio);
		}
		if (mesFim != null) {
			query.setParameter("mesFim", mesFim);
		}

		if (dia != null) {
			query.setParameter("diaSemana", dia);
		}
		
		if(zonaSala != null){
			query.setParameter("zonaSala", zonaSala.getId().getSala());
			query.setParameter("zonaSalaUnfSeq", zonaSala.getId().getUnfSeq());
		} else if (zona != null && zona.getUnfSeq() != null){
			query.setParameter("zonaUnfSeq", zona.getUnfSeq());
		}

		if (turno != null) {
			String dtConsulta1 = DateUtil.obterDataFormatada(turno.getDataInicial(), "HH:mm:ss");
			String dtConsulta2 = DateUtil.obterDataFormatada(turno.getDataFinal(), "HH:mm:ss");
			query.setParameter("dataTurnoInicio", dtConsulta1);
			query.setParameter("dataTurnoFim", dtConsulta2);
		}
		//#52061
		if (!visualizarPrimeirasConsultasSMS) {
			//Short seqTipoAgendamentoSMS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TAG_SMS).getVlrNumerico().shortValue();
			//Short seqCondPrimeiraConsulta = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CAA_CONSULTA).getVlrNumerico().shortValue();
			query.setParameter("seqCondPrimeiraConsulta", seqCondPrimeiraConsulta);
			query.setParameter("seqTipoAgendamentoSMS", seqTipoAgendamentoSMS);
		}

		return query;
    }

	

}
