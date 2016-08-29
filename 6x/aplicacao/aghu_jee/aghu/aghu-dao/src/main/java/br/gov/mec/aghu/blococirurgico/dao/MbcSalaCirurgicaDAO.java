package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ShortType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasBuscaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasProfissionalEspecialidadeVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioEscalaDeSalasRetornoHqlVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSala;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;

public class MbcSalaCirurgicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcSalaCirurgica> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6829168811133873518L;

	private DetachedCriteria createCriteria(MbcSalaCirurgica salaCirurgica,
			Boolean asc, MbcSalaCirurgica.Fields... orders) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MbcSalaCirurgica.class);

		criteria.createAlias(MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		
		if (salaCirurgica.getId().getSeqp() != null) {
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_SEQP
					.toString(), salaCirurgica.getId().getSeqp()));
		}

		if (salaCirurgica.getId().getUnfSeq() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(),
					salaCirurgica.getId().getUnfSeq()));
		}

		if (salaCirurgica.getNome() != null) {
			criteria.add(Restrictions.ilike(
					MbcSalaCirurgica.Fields.NOME.toString(),
					salaCirurgica.getNome()));
		}

		if (salaCirurgica.getTipoSala() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.TIPO_SALA.toString(),
					salaCirurgica.getTipoSala()));
		}
		
		if (salaCirurgica.getMbcTipoSala() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.MBC_TIPO_SALA.toString(),
					salaCirurgica.getMbcTipoSala()));
		}

		if (salaCirurgica.getVisivelMonitor() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.VISIVEL_MONITOR.toString(),
					salaCirurgica.getVisivelMonitor()));
		}

		if (salaCirurgica.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.SITUACAO.toString(),
					salaCirurgica.getSituacao()));
		}

		if (orders != null && orders != null) {
			if (asc) {
				for (MbcSalaCirurgica.Fields order : orders) {
					criteria.addOrder(Order.asc(order.toString()));
				}
			} else {
				for (MbcSalaCirurgica.Fields order : orders) {
					criteria.addOrder(Order.desc(order.toString()));
				}
			}
		}

		return criteria;
	}

	public MbcSalaCirurgica buscarSalaCirurgicaById(Short seqp, Short unfSeq) {

		MbcSalaCirurgica salaCirurgica = new MbcSalaCirurgica();
		MbcSalaCirurgicaId id = new MbcSalaCirurgicaId(unfSeq, seqp);
		salaCirurgica.setId(id);
		DetachedCriteria criteria = createCriteria(salaCirurgica, null, null);
		return (MbcSalaCirurgica) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MbcSalaCirurgica> buscarSalasCirurgicas(final String filtro,
			final Short unfSeq, final DominioSituacao situacao) {
				DetachedCriteria criteria = createBuscarSalasCirurgicas(filtro,unfSeq,  situacao);
				criteria.addOrder(Order.asc(MbcSalaCirurgica.Fields.ID_SEQP.toString()));
		return executeCriteria(criteria, 0, 100, null,false);
	}
	
	public DetachedCriteria createBuscarSalasCirurgicas(final String filtro,
			final Short unfSeq, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(MbcSalaCirurgica.class);

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.SITUACAO.toString(), situacao));
		}

		if (org.apache.commons.lang3.StringUtils.isNotEmpty(filtro)) {
			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.eq(
						MbcSalaCirurgica.Fields.ID_SEQP.toString(),
						Short.valueOf(filtro)));

			} else {
				criteria.add(Restrictions.ilike(
						MbcSalaCirurgica.Fields.NOME.toString(), filtro,
						MatchMode.ANYWHERE));
				criteria.addOrder(Order.asc(MbcSalaCirurgica.Fields.NOME
						.toString()));
			}
		}
		return criteria;
	}
	
	public Long buscarSalasCirurgicasCount(final String filtro,
			final Short unfSeq, final DominioSituacao situacao) {
		DetachedCriteria criteria = createBuscarSalasCirurgicas(filtro,unfSeq,  situacao);		
		return executeCriteriaCount(criteria);
	}
	
	public List<MbcSalaCirurgica> buscarSalaCirurgica(Short seqp, Short unfSeq,
			String nome, DominioTipoSala tipoSala, Boolean visivelMonitor,
			DominioSituacao situacao, Boolean asc,
			MbcSalaCirurgica.Fields... orders) {

		MbcSalaCirurgica salaCirurgica = processarMbcSalaCirurgica(seqp,
				unfSeq, nome, tipoSala, visivelMonitor, situacao);
		DetachedCriteria criteria = createCriteria(salaCirurgica, asc, orders);
		return executeCriteria(criteria);

	}

	public List<MbcSalaCirurgica> buscarSalasCirurgicasPorUnfSeq(final Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class);
		criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if (unfSeq != null) {
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		}
		
		criteria.addOrder(Order.asc(MbcSalaCirurgica.Fields.ID_SEQP.toString()));
		
		return executeCriteria(criteria);
	}
	
	private MbcSalaCirurgica processarMbcSalaCirurgica(Short seqp,
			Short unfSeq, String nome, DominioTipoSala tipoSala,
			Boolean visivelMonitor, DominioSituacao situacao) {
		MbcSalaCirurgica salaCirurgica = new MbcSalaCirurgica();
		MbcSalaCirurgicaId id = new MbcSalaCirurgicaId(unfSeq, seqp);
		salaCirurgica.setId(id);
		if (StringUtils.isEmpty(nome)) {
			salaCirurgica.setNome(null);
		} else {
			salaCirurgica.setNome(nome);
		}

		salaCirurgica.setTipoSala(tipoSala);
		salaCirurgica.setVisivelMonitor(visivelMonitor);
		salaCirurgica.setSituacao(situacao);
		return salaCirurgica;
	}

	public List<MbcSalaCirurgica> buscarSalaCirurgica(
			MbcSalaCirurgica salaCirurgica) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MbcSalaCirurgica.class);
		
		criteria.createAlias(MbcSalaCirurgica.Fields.MBC_TIPO_SALA.toString(), "TIPO_SALA",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString(), "UND_FUNC",JoinType.LEFT_OUTER_JOIN);


		if (salaCirurgica.getId() != null
				&& salaCirurgica.getId().getSeqp() != null) {
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_SEQP
					.toString(), salaCirurgica.getId().getSeqp()));
		}
		if (salaCirurgica.getUnidadeFuncional() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL.toString(),
					salaCirurgica.getUnidadeFuncional()));
		}
		if (salaCirurgica.getNome() != null) {
			criteria.add(Restrictions.ilike(
					MbcSalaCirurgica.Fields.NOME.toString(),
					salaCirurgica.getNome(), MatchMode.ANYWHERE));
		}
		if (salaCirurgica.getMbcTipoSala() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.MBC_TIPO_SALA.toString(),
					salaCirurgica.getMbcTipoSala()));
		}
		if (salaCirurgica.getVisivelMonitor() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.VISIVEL_MONITOR.toString(),
					salaCirurgica.getVisivelMonitor()));
		}
		if (salaCirurgica.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					MbcSalaCirurgica.Fields.SITUACAO.toString(),
					salaCirurgica.getSituacao()));
		}

		criteria.addOrder(Order.asc(MbcSalaCirurgica.Fields.ID_SEQP.toString()));

		return executeCriteria(criteria);
	}

	public Long buscarSalaCirurgicaCount(Short seqp, Short unfSeq,
			String nome, DominioTipoSala tipoSala, Boolean visivelMonitor,
			DominioSituacao situacao) {
		MbcSalaCirurgica salaCirurgica = processarMbcSalaCirurgica(seqp,
				unfSeq, nome, tipoSala, visivelMonitor, situacao);
		DetachedCriteria criteria = createCriteria(salaCirurgica, null, null);
		return executeCriteriaCount(criteria);
	}

	public Short obterSequenceSalaCirurgica(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MbcSalaCirurgica.class);
		criteria.setProjection(Projections.max(MbcSalaCirurgica.Fields.ID_SEQP
				.toString()));
		criteria.add(Restrictions.eq(
				MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public List<EscalaSalasBuscaVO> buscarEscalaSalasPorUnidadeCirurgica(
			final Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(
				MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci."
				+ MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(),
				"cas");
		criteria.createAlias(
				"cas." + MbcCaracteristicaSalaCirg.Fields.TURNO.toString(),
				"tur");

		criteria.add(Restrictions.eq("sci."
				+ MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(
				"sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(),
				DominioSituacao.A));
		criteria.add(Restrictions.eq("cas."
				+ MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(),
				true));

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("sci."
						+ MbcSalaCirurgica.Fields.ID_SEQP.toString()), "seqp")
				.add(Projections.property("tur."
						+ MbcTurnos.Fields.TURNO.toString()), "turno")
				.add(Projections.property("cas."
						+ MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA
								.toString()), "diasemana")
				.add(Projections.property("cas."
						+ MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA
								.toString()), "urgencia")
				.add(Projections.property("cas."
						+ MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR
								.toString()), "particular")
				.add(Projections.property("cas."
						+ MbcCaracteristicaSalaCirg.Fields.SEQ.toString()),
						"casseq")
				.add(Projections
						.sqlProjection(
								" case WHEN cas1_.dia_semana = 'DOM' THEN 6 WHEN cas1_.dia_semana = 'SEG' THEN 0 WHEN cas1_.dia_semana = 'TER' THEN 1 WHEN cas1_.dia_semana = 'QUA' THEN 2 WHEN cas1_.dia_semana = 'QUI' THEN 3 WHEN cas1_.dia_semana = 'SEX' THEN 4 WHEN cas1_.dia_semana = 'SAB' THEN 5 else NULL end as ordem",
								new String[] { "ordem" },
								new Type[] { ShortType.INSTANCE })));

		criteria.addOrder(Order.asc("sci."
				+ MbcSalaCirurgica.Fields.ID_SEQP.toString()));
		criteria.addOrder(Order.asc("tur." + MbcTurnos.Fields.ORDEM.toString()));
		criteria.addOrder(OrderBySql
				.sql(" case WHEN cas1_.dia_semana = 'DOM' THEN 6 WHEN cas1_.dia_semana = 'SEG' THEN 0 WHEN cas1_.dia_semana = 'TER' THEN 1 WHEN cas1_.dia_semana = 'QUA' THEN 2 WHEN cas1_.dia_semana = 'QUI' THEN 3 WHEN cas1_.dia_semana = 'SEX' THEN 4 WHEN cas1_.dia_semana = 'SAB' THEN 5 else NULL end "));

		criteria.setResultTransformer(Transformers
				.aliasToBean(EscalaSalasBuscaVO.class));
		return executeCriteria(criteria);
	}

	public List<EscalaSalasProfissionalEspecialidadeVO> buscarProfissionalEscala(
			final Short unfSeq, final Short sala, final String turno,
			final DominioDiaSemana diasemana) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(
				MbcCaracteristicaSalaCirg.class, "cas");
		criteria.createAlias(
				"cas."
						+ MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES
								.toString(), "cse", DetachedCriteria.LEFT_JOIN);
		criteria.createAlias("cse."
				+ MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString(), "esp",
				DetachedCriteria.LEFT_JOIN);
		criteria.createAlias("cse."
				+ MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(),
				"puc", DetachedCriteria.LEFT_JOIN);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections
						.property("puc."
								+ MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO
										.toString()),
						"codigo")
				.add(Projections.property("puc."
						+ MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString()),
						"matricula")
				.add(Projections.property("esp."
						+ AghEspecialidades.Fields.SIGLA.toString()), "sigla"));

		criteria.add(Restrictions.eq("cas."
				+ MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sala));
		criteria.add(Restrictions.eq("cas."
				+ MbcCaracteristicaSalaCirg.Fields.TURNO_ID.toString(), turno));
		criteria.add(Restrictions.eq("cas."
				+ MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(),
				diasemana));
		criteria.add(Restrictions
				.eq("cas."
						+ MbcCaracteristicaSalaCirg.Fields.MBC_HORARIO_TURNO_CIRGS_ID_UNF_SEQ
								.toString(), unfSeq));
		criteria.add(Restrictions.eq("cas."
				+ MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(),
				true));
		criteria.add(Restrictions.or(
				Restrictions.eq(
						"cse."
								+ MbcCaractSalaEsp.Fields.IND_SITUACAO
										.toString(), DominioSituacao.A),
				Restrictions.isNull("cse."
						+ MbcCaractSalaEsp.Fields.IND_SITUACAO.toString())));

		criteria.setResultTransformer(Transformers
				.aliasToBean(EscalaSalasProfissionalEspecialidadeVO.class));
		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<MbcSalaCirurgica> buscarSalasDisponiveisParaTrocaNaAgenda(Date data, Short unfSeq, Short espSeq, 
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf) {
		List<MbcSalaCirurgica> retorno = new ArrayList<MbcSalaCirurgica>();
		//UNION
		retorno.addAll(buscarSalasDisponiveisParaTrocaNaAgendaParte1(data, unfSeq, espSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, indFuncaoProf));
		retorno.addAll(buscarSalasDisponiveisParaTrocaNaAgendaParte2(data, unfSeq, null, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, indFuncaoProf));
		retorno.addAll(buscarSalasDisponiveisParaTrocaNaAgendaParte3(data, unfSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, indFuncaoProf));
		
		final ComparatorChain ordenacao = new ComparatorChain();
		final BeanComparator ordenarNome = new BeanComparator(
				MbcSalaCirurgica.Fields.NOME.toString(), new NullComparator(
						false));
		ordenacao.addComparator(ordenarNome);

		Collections.sort(retorno, ordenacao);
		
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	public List<MbcSalaCirurgica> buscarSalasDisponiveisParaEscala(Date data, Short unfSeq, Short espSeq, 
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf) {
		List<MbcSalaCirurgica> retorno = new ArrayList<MbcSalaCirurgica>();
		
		//Utilizou-se as consultas da estoria #22351
		retorno.addAll(buscarSalasDisponiveisParaTrocaNaAgendaParte1(data, unfSeq, espSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, indFuncaoProf));
		retorno.addAll(buscarSalasDisponiveisParaTrocaNaAgendaParte2(data, unfSeq, espSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, indFuncaoProf));
		retorno.addAll(buscarSalasDisponiveisCedidasHCPA(data, unfSeq, espSeq, matriculaEquipe, vinCodigoEquipe, 
				unfSeqEquipe, indFuncaoProf));
		
		final ComparatorChain ordenacao = new ComparatorChain();
		final BeanComparator ordenarNome = new BeanComparator(
				MbcSalaCirurgica.Fields.NOME.toString(), new NullComparator(
						false));
		ordenacao.addComparator(ordenarNome);

		Collections.sort(retorno, ordenacao);
		
		return retorno;
	}
	
	private List<MbcSalaCirurgica> buscarSalasDisponiveisParaTrocaNaAgendaParte1(Date dataAgenda, Short unfSeq, Short espSeq, 
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		
		if(espSeq != null){
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), espSeq));
		}
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_SER_MATRICULA.toString(), matriculaEquipe));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_SER_VIN_CODIGO.toString(), vinCodigoEquipe));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_UNF_SEQ.toString(), unfSeqEquipe));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.PUC_IND_FUNCAO_PROF.toString(), indFuncaoProf));
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(dataAgenda))));
		
		DetachedCriteria subCriteriaEscalaSalas = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ssl");
		subCriteriaEscalaSalas.setProjection(Projections.property(MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(dataAgenda)));
		subCriteriaEscalaSalas.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString(), "cse." + MbcCaractSalaEsp.Fields.CAS_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_ESP_SEQ.toString(), "cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_SEQP.toString(), "cse." + MbcCaractSalaEsp.Fields.SEQP.toString()));
		
		criteria.add(Subqueries.notExists(subCriteriaEscalaSalas));
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}
	
	private List<MbcSalaCirurgica> buscarSalasDisponiveisParaTrocaNaAgendaParte2(Date dataAgenda, Short unfSeq, Short espSeq, 
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_SUBST_ESCALA_SALAES.toString(), "ssl");
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(dataAgenda)));
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), matriculaEquipe));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), vinCodigoEquipe));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), unfSeqEquipe));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), indFuncaoProf));
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}
	
	private List<MbcSalaCirurgica> buscarSalasDisponiveisParaTrocaNaAgendaParte3(Date dataAgenda, Short unfSeq, Integer matriculaEquipe, 
			Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "cse");
	
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cse." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("cse." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(dataAgenda)));
		criteria.add(Restrictions.eq("cse." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), matriculaEquipe));
		criteria.add(Restrictions.eq("cse." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), vinCodigoEquipe));
		criteria.add(Restrictions.eq("cse." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), unfSeqEquipe));
		criteria.add(Restrictions.eq("cse." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), indFuncaoProf));
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}
	
	public List<MbcSalaCirurgica> buscarSalasDisponiveisCedidasHCPA(Date data, Short unfSeq, Short espSeq, Integer 
			matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf) {
		//salas extras cedidas pelo hcpa		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_MATRICULA.toString(), matriculaEquipe));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_VIN_CODIGO.toString(), vinCodigoEquipe));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_UNF_SEQ.toString(), unfSeqEquipe));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_IND_FUNCAO_PROF.toString(), indFuncaoProf));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));		
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}
	
	public List<MbcSalaCirurgica> validarDataRemarcacaoAgendaEquipe(MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short espSeq, Short unfSeq,
			Date dtReagendamento) {
		List<MbcSalaCirurgica> retorno = new ArrayList<MbcSalaCirurgica>();
		retorno.addAll(validarDataRemarcacaoAgendaEquipeUnion1(profAtuaUnidCirg, espSeq, unfSeq, dtReagendamento, null));
		// mais as salas extras (que outras equipes cederam)
		retorno.addAll(validarDataRemarcacaoAgendaEquipeUnion2(profAtuaUnidCirg, unfSeq, dtReagendamento, null));
		// mais as salas extras cedidas pelo HCPA
		retorno.addAll(validarDataRemarcacaoAgendaEquipeUnion3(profAtuaUnidCirg, unfSeq, dtReagendamento, null));
		
		return retorno;
	}
	
	public List<MbcSalaCirurgica> validarDataRemarcacaoAgendaEquipeCaracteristica(MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short espSeq, Short unfSeq,
			Date dtReagendamento, Short sciSeqp) {
		List<MbcSalaCirurgica> retorno = new ArrayList<MbcSalaCirurgica>();
		retorno.addAll(validarDataRemarcacaoAgendaEquipeUnion1(profAtuaUnidCirg, espSeq, unfSeq, dtReagendamento, sciSeqp));
		// mais as salas extras (que outras equipes cederam)
		retorno.addAll(validarDataRemarcacaoAgendaEquipeUnion2(profAtuaUnidCirg, unfSeq, dtReagendamento, sciSeqp));
		
		return retorno;
	}
		
	protected List<MbcSalaCirurgica> validarDataRemarcacaoAgendaEquipeUnion1(
			MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short espSeq, Short unfSeq,
			Date dtReagendamento, Short sciSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), profAtuaUnidCirg));
		
		if(espSeq != null){
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString(), espSeq));
		}
		
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(dtReagendamento))));
		
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(sciSeqp == null){
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.FALSE));
		}else{
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sciSeqp));
		}
		
		//retirando as cedidas
		criteria.add(Subqueries.notExists(montarSubCriteriaCedidas(dtReagendamento, profAtuaUnidCirg)));
		//retirando as que estao bloqueadas
		criteria.add(Subqueries.notExists(montarSubCriteriaBloqueadas(dtReagendamento, profAtuaUnidCirg)));
		//tirando feriados
		criteria.add(Subqueries.notExists(montarSubCriteriaFeriados(dtReagendamento)));
		
		return executeCriteria(criteria);
	}
	
	private List<MbcSalaCirurgica> validarDataRemarcacaoAgendaEquipeUnion2(
			MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short unfSeq,
			Date dtReagendamento, Short sciSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_SUBST_ESCALA_SALAES.toString(), "ssl");
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(dtReagendamento)));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), profAtuaUnidCirg));

		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(sciSeqp == null){
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString(), Boolean.FALSE));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString(), Boolean.FALSE));
		}else{
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sciSeqp));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(dtReagendamento))));
		}
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	private List<MbcSalaCirurgica> validarDataRemarcacaoAgendaEquipeUnion3(
			MbcProfAtuaUnidCirgs profAtuaUnidCirg, Short unfSeq,
			Date dtReagendamento,  Short sciSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(dtReagendamento)));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), profAtuaUnidCirg));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(sciSeqp!=null){
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SCI_SEQP.toString(), sciSeqp));
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemana.valueOf(DateFormatUtil.diaDaSemana(dtReagendamento))));
		}
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarSubCriteriaCedidas(Date dtReagendamento, MbcProfAtuaUnidCirgs profAtuaUnidCirgs){
		// tirando as que foram cedidas para outras equipes
		DetachedCriteria subCriteriaEscalaSalas = DetachedCriteria.forClass(MbcSubstEscalaSala.class, "ssl");
		subCriteriaEscalaSalas.setProjection(Projections.property(MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(dtReagendamento)));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_CAS_SEQ.toString(), "cse." + MbcCaractSalaEsp.Fields.CAS_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_ESP_SEQ.toString(), "cse." + MbcCaractSalaEsp.Fields.ESP_SEQ.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eqProperty("ssl." + MbcSubstEscalaSala.Fields.CSE_SEQP.toString(), "cse." + MbcCaractSalaEsp.Fields.SEQP.toString()));
		subCriteriaEscalaSalas.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return subCriteriaEscalaSalas;
	}
	
	private DetachedCriteria montarSubCriteriaBloqueadas(Date dtReagendamento, MbcProfAtuaUnidCirgs profAtuaUnidCirg){
		// tirando as que estao bloqueadas
		DetachedCriteria subCriteriaBloq = DetachedCriteria.forClass(MbcBloqSalaCirurgica.class, "bsc");
		subCriteriaBloq.setProjection(Projections.property(MbcBloqSalaCirurgica.Fields.SEQ.toString()));
		subCriteriaBloq.add(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.SCI_UNF_SEQ.toString(), "sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString()));
		subCriteriaBloq.add(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.SCI_SEQP.toString(), "sci." + MbcSalaCirurgica.Fields.SEQP.toString()));
		
		subCriteriaBloq.add(Restrictions.le("bsc." + MbcBloqSalaCirurgica.Fields.DT_INICIO.toString(), dtReagendamento));
		subCriteriaBloq.add(Restrictions.ge("bsc." + MbcBloqSalaCirurgica.Fields.DT_FIM.toString(), dtReagendamento));
		
		subCriteriaBloq.add(Restrictions.or(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.DIA_SEMANA.toString(), "cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.DIA_SEMANA.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eqProperty("bsc." + MbcBloqSalaCirurgica.Fields.TURNO_TURNO.toString(), "cas." + MbcCaracteristicaSalaCirg.Fields.TURNO_ID.toString()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.TURNO_TURNO.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_MATRICULA.toString(), profAtuaUnidCirg.getId().getSerMatricula()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_MATRICULA.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_VINCODIGO.toString(), profAtuaUnidCirg.getId().getSerVinCodigo()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_SER_VINCODIGO.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_UNF_SEQ.toString(), profAtuaUnidCirg.getId().getUnfSeq()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_UNF_SEQ.toString())));
		subCriteriaBloq.add(Restrictions.or(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_IND_FUNCAO_PROF.toString(), profAtuaUnidCirg.getId().getIndFuncaoProf()),
				Restrictions.isNull("bsc." + MbcBloqSalaCirurgica.Fields.MBC_PROF_ATUA_UNID_CIRGS_ID_IND_FUNCAO_PROF.toString())));
		subCriteriaBloq.add(Restrictions.eq("bsc." + MbcBloqSalaCirurgica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return subCriteriaBloq;
	}
	
	private DetachedCriteria montarSubCriteriaFeriados(Date dtReagendamento){
		DetachedCriteria subCriteriaFrd = DetachedCriteria.forClass(AghFeriados.class, "frd");
		subCriteriaFrd.setProjection(Projections.property(AghFeriados.Fields.DATA.toString()));
		subCriteriaFrd.add(Restrictions.or(Restrictions.sqlRestriction(" TURNO = HTC_TURNO "),
				Restrictions.isNull("frd." + AghFeriados.Fields.TURNO.toString())));
		subCriteriaFrd.add(Restrictions.eq("frd." + AghFeriados.Fields.DATA.toString(), DateUtil.truncaData(dtReagendamento)));
		
		return subCriteriaFrd;
	}
	
	public List<Date> buscarHorariosExtras(Date data, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, Short sciSeqp, Boolean reverse) {
		List<Date> retorno = new ArrayList<Date>();
		// limpa o entity manager para nao realizar consultas do portal de agendamento pela cache
		entityManagerClear();
		
		retorno.addAll(buscarHorariosExtrasUnion1(data, atuaUnidCirgs, unfSeq, sciSeqp));
		retorno.addAll(buscarHorariosExtrasUnion2(data, atuaUnidCirgs, unfSeq, sciSeqp));
		//comentado a pedido do Filipe pois não faz sentido buscar anotações para montar agenda
		//retorno.addAll(buscarHorariosExtrasUnion3(data, atuaUnidCirgs, reverse));
		
		for(Date item : retorno) {
			item = DateUtil.truncaData(item);
		}
		
		if(reverse) {
			Collections.sort(retorno, Collections.reverseOrder());
		} else {
			Collections.sort(retorno);
		}
		return retorno;
	}
	
	private List<Date> buscarHorariosExtrasUnion1(Date data, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, Short sciSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		criteria.createAlias("cse." + MbcCaractSalaEsp.Fields.MBC_SUBST_ESCALA_SALAES.toString(), "ssl");
		
		criteria.setProjection(Projections.property("ssl." + MbcSubstEscalaSala.Fields.DATA.toString()));
		
//		if(reverse) {
//			criteria.add(Restrictions.le("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(data)));
//		} else {
//			criteria.add(Restrictions.ge("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(data)));
//		}
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.DATA.toString(), DateUtil.truncaData(data)));
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		
		if(atuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
		}
		
		criteria.add(Restrictions.eq("ssl." + MbcSubstEscalaSala.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	private List<Date> buscarHorariosExtrasUnion2(Date data, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, Short sciSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CEDENCIA_SALA_HCPAS.toString(), "csh");
		
		criteria.setProjection(Projections.property("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString()));
		
//		if(reverse) {
//			criteria.add(Restrictions.le("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
//		} else {
//			criteria.add(Restrictions.ge("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
//		}
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.DATA.toString(), DateUtil.truncaData(data)));
		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		if(sciSeqp != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sciSeqp));
		}
		
		if(atuaUnidCirgs != null) {
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_MATRICULA.toString(), atuaUnidCirgs.getId().getSerMatricula()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_SER_VIN_CODIGO.toString(), atuaUnidCirgs.getId().getSerVinCodigo()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_UNF_SEQ.toString(), atuaUnidCirgs.getId().getUnfSeq()));
			criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.PUC_IND_FUNCAO_PROF.toString(), atuaUnidCirgs.getId().getIndFuncaoProf()));
		}
		
		criteria.add(Restrictions.eq("csh." + MbcCedenciaSalaHcpa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria obterCriteriaPesquisarEspecialidadeEquipe(MbcSalaCirurgica sala, 
			MbcProfAtuaUnidCirgs profAtuaUnidcirg, Date diaSemana) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");
		criteria.createAlias("sci." + MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString(), "cas");
		criteria.createAlias("cas." + MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString(), "cse");
		
		if (sala != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), sala.getId().getSeqp()));
		}
		
		if (profAtuaUnidcirg != null) {
			criteria.add(Restrictions.eq("cse." + MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString(), profAtuaUnidcirg));
		}
		
		if (diaSemana != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(diaSemana);
			Integer d = cal.get(Calendar.DAY_OF_WEEK);
		
			criteria.add(Restrictions.eq("cas." + MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString(), DominioDiaSemanaSigla.getDiaSemanaSigla(d)));			
		}		
		
		return criteria;
	}	
	
	public Long pesquisarEspecialidadeEquipeCount(MbcSalaCirurgica sala, 
			MbcProfAtuaUnidCirgs profAtuaUnidcirgs, Date diaSemana) {
		return executeCriteriaCount(obterCriteriaPesquisarEspecialidadeEquipe(sala, profAtuaUnidcirgs, diaSemana));
	}

	public List<MbcSalaCirurgica> pesquisarSalasCirurgicasPorDatasEquipeEspecUnidSala(
			Short unidadeFiltro,
			Short salaFiltro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class, "sci");


		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.UNF_SEQ.toString(), unidadeFiltro));
		
		if(salaFiltro != null) {
			criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SEQP.toString(), salaFiltro));
		}		
		criteria.add(Restrictions.eq("sci." + MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("sci." + MbcSalaCirurgica.Fields.ID_SEQP));
		
		return executeCriteria(criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
	}

	
	public List<MbcSalaCirurgica> pesquisarSalasCirurgicasParaReagendamentoPaciente(Date dataAgenda, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short espSeq, Short unfSeq) {
		List<MbcSalaCirurgica> retorno = new ArrayList<MbcSalaCirurgica>();
		//reutilizada consulta que busca salas para equipe por data para validar data reagendamento de mbc_agendas
		retorno.addAll(validarDataRemarcacaoAgendaEquipeUnion1(atuaUnidCirgs, espSeq, unfSeq, dataAgenda, null));
		// mais as salas extras (que outras equipes cederam)
		retorno.addAll(validarDataRemarcacaoAgendaEquipeUnion2(atuaUnidCirgs, unfSeq, dataAgenda, null));
		// mais as salas extras cedidas pelo HCPA
		retorno.addAll(validarDataRemarcacaoAgendaEquipeUnion3(atuaUnidCirgs, unfSeq, dataAgenda, null));

		//remove salas duplicadas
		Set<MbcSalaCirurgica> set = new HashSet<MbcSalaCirurgica>();
		set.addAll(retorno);
		retorno.clear();
		retorno.addAll(set);
		
		Collections.sort(retorno, new Comparator<MbcSalaCirurgica>(){
			@Override
			public int compare(MbcSalaCirurgica o1, MbcSalaCirurgica o2) {
				return o1.getId().getSeqp().compareTo(o2.getId().getSeqp());
			}
		});
		
		return retorno;
	}
	
	
	public Long pesquisarSalasCirurgicasPorUnfSeqSeqpOuNomeCount(Object objSalaCirurgica, AghUnidadesFuncionais unidadeFuncional){
		DetachedCriteria criteria = pesquisarSalasCirurgicasPorUnfSeqSeqpOuNomeCriteria(objSalaCirurgica, unidadeFuncional);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<MbcSalaCirurgica> pesquisarSalasCirurgicasPorUnfSeqSeqpOuNome(Object objSalaCirurgica, AghUnidadesFuncionais unidadeFuncional){
		DetachedCriteria criteria = pesquisarSalasCirurgicasPorUnfSeqSeqpOuNomeCriteria(objSalaCirurgica, unidadeFuncional);
		criteria.addOrder(Order.asc(MbcSalaCirurgica.Fields.ID_SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria pesquisarSalasCirurgicasPorUnfSeqSeqpOuNomeCriteria(Object objSalaCirurgica, AghUnidadesFuncionais unidadeFuncional){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class);
		
		if(unidadeFuncional != null){
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		}
		
		if(!objSalaCirurgica.toString().isEmpty()){
			
			if (CoreUtil.isNumeroByte(objSalaCirurgica)) {
				criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_SEQP.toString(), Short.valueOf(objSalaCirurgica.toString())));
			}else{
				criteria.add(Restrictions.ilike(MbcSalaCirurgica.Fields.NOME.toString(), objSalaCirurgica.toString(), MatchMode.ANYWHERE));
			}
		}
				
		return criteria;

	}
	
	public List<RelatorioEscalaDeSalasRetornoHqlVO> pesquisarEscalaDeSalas(Short unidade, Short sala, MbcTurnos turno, DominioDiaSemana diaSemana) {
		// Utilizado HQL, pois não é possível fazer restrição no left join
		StringBuilder hql = new StringBuilder(400);
		hql.append("select 	esp.").append( AghEspecialidades.Fields.SIGLA.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.SIGLA.toString() ).append(", ")
		.append("puc.").append( MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.MATRICULA.toString()	).append(", ")
		.append("puc.").append( MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.VIN_CODIGO.toString()	).append(", ")
		.append("cas.").append( MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.CIRURGIA_PARTICULAR.toString() ).append(", ")
		.append("cas.").append( MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.IND_URGENCIA.toString()	).append(' ')
		.append("from ").append( MbcSalaCirurgica.class.getSimpleName() ).append(" sci ")
		.append("inner join sci.").append( MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString() ).append(" cas ")
		.append("left join cas.").append( MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString() ).append(" cse ")
		.append("with cse.").append( MbcCaractSalaEsp.Fields.IND_SITUACAO.toString() ).append(" = :cseIndSituacao ")
		.append("left join cse.").append( MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString() ).append(" esp ")
		.append("left join cse.").append( MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString() ).append(" puc ")
		.append("where sci.").append( MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString() ).append(" = :unfSeq ")
		.append("and sci.").append( MbcSalaCirurgica.Fields.ID_SEQP.toString() ).append(" = :sciSeqp ")
		.append("and cas.").append( MbcCaracteristicaSalaCirg.Fields.TURNO.toString() ).append(" = :casMbcTurnos ")
		.append("and cas.").append( MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString() ).append(" = :casDiaSemana ")
		.append("and sci.").append( MbcSalaCirurgica.Fields.SITUACAO.toString() ).append(" = :sciSituacao ")
		.append("and cas.").append( MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString() ).append(" = :casIndDisponivel ");

		Query query = createHibernateQuery(hql.toString());
		query.setString("cseIndSituacao", DominioSituacao.A.toString());
		query.setShort("unfSeq", unidade);
		query.setShort("sciSeqp", sala);
		query.setString("casMbcTurnos", turno.getTurno().toString());
		query.setString("casDiaSemana", diaSemana.getDescricao());
		query.setString("sciSituacao", DominioSituacao.A.toString());
		query.setBoolean("casIndDisponivel", Boolean.TRUE);
		
		query.setResultTransformer(Transformers.aliasToBean(RelatorioEscalaDeSalasRetornoHqlVO.class));
		
		return query.list();
	}
		
	
	public List<RelatorioEscalaDeSalasRetornoHqlVO> pesquisarEscalaDeSalasRelatorio(Short unidade, Short sala, MbcTurnos turno, DominioDiaSemana diaSemana) {
		// Utilizado HQL, pois não é possível fazer restrição no left join
		StringBuilder hql = new StringBuilder(414);
		hql.append("select 	esp.").append( AghEspecialidades.Fields.SIGLA.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.SIGLA.toString() ).append(", ")
		.append("puc.").append( MbcProfAtuaUnidCirgs.Fields.SER_MATRICULA.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.MATRICULA.toString()	).append(", ")
		.append("puc.").append( MbcProfAtuaUnidCirgs.Fields.SER_VIN_CODIGO.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.VIN_CODIGO.toString()	).append(", ")
		.append("cas.").append( MbcCaracteristicaSalaCirg.Fields.CIRURGIA_PARTICULAR.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.CIRURGIA_PARTICULAR.toString() ).append(", ")
		.append("cas.").append( MbcCaracteristicaSalaCirg.Fields.IND_URGENCIA.toString() ).append(" as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.IND_URGENCIA.toString()	).append(' ')
		.append(" ,  CASE WHEN rapf.").append(RapPessoasFisicas.Fields.NOME_USUAL.toString() ).append(" = NULL THEN  rapf.").append(RapPessoasFisicas.Fields.NOME.toString()).append(" ELSE rapf.").append(RapPessoasFisicas.Fields.NOME.toString()).append(" end as ").append( RelatorioEscalaDeSalasRetornoHqlVO.Fields.NOME.toString()	).append(' ')
		.append("from ").append( MbcSalaCirurgica.class.getSimpleName() ).append(" sci ")
		.append("inner join sci.").append( MbcSalaCirurgica.Fields.CARACTERISTICA_SALA_CIRGS.toString() ).append(" cas ")
		.append("left join cas.").append( MbcCaracteristicaSalaCirg.Fields.MBC_CARACT_SALA_ESPES.toString() ).append(" cse ")
		.append("with cse.").append( MbcCaractSalaEsp.Fields.IND_SITUACAO.toString() ).append(" = :cseIndSituacao ")
		.append("left join cse.").append( MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString() ).append(" esp ")
		.append("left join cse.").append( MbcCaractSalaEsp.Fields.MBC_PROF_ATUA_UNID_CIRGS.toString() ).append(" puc ")
		.append("left join puc.").append(MbcProfAtuaUnidCirgs.Fields.RAP_SERVIDOR.toString()).append( " rap ")
		.append("left join rap.").append(RapServidores.Fields.PESSOA_FISICA.toString()).append( " rapf ")
		.append("where sci.").append( MbcSalaCirurgica.Fields.UNIDADE_FUNCIONAL_SEQ.toString() ).append(" = :unfSeq ")
		.append("and sci.").append( MbcSalaCirurgica.Fields.ID_SEQP.toString() ).append(" = :sciSeqp ")
		.append("and cas.").append( MbcCaracteristicaSalaCirg.Fields.TURNO.toString() ).append(" = :casMbcTurnos ")
		.append("and cas.").append( MbcCaracteristicaSalaCirg.Fields.DIA_SEMANA.toString() ).append(" = :casDiaSemana ")
		.append("and sci.").append( MbcSalaCirurgica.Fields.SITUACAO.toString() ).append(" = :sciSituacao ")
		.append("and cas.").append( MbcCaracteristicaSalaCirg.Fields.IND_DISPONIVEL.toString() ).append(" = :casIndDisponivel ");

		Query query = createHibernateQuery(hql.toString());
		query.setString("cseIndSituacao", DominioSituacao.A.toString());
		query.setShort("unfSeq", unidade);
		query.setShort("sciSeqp", sala);
		query.setString("casMbcTurnos", turno.getTurno().toString());
		query.setString("casDiaSemana", diaSemana.getDescricao());
		query.setString("sciSituacao", DominioSituacao.A.toString());
		query.setBoolean("casIndDisponivel", Boolean.TRUE);
		
		query.setResultTransformer(Transformers.aliasToBean(RelatorioEscalaDeSalasRetornoHqlVO.class));
		
		return query.list();
	}
		
	public List<MbcSalaCirurgica> buscarSalasCirurgicasAtivasPorUnfSeqSeqp(final Short unfSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class);
		criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if (unfSeq != null) {
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		}
		
		if(seqp != null){
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.SEQP.toString(), seqp));
		}
		
		criteria.addOrder(Order.asc(MbcSalaCirurgica.Fields.ID_SEQP.toString()));
		return executeCriteria(criteria);
	}

	public Long buscarSalasCirurgicasAtivasPorUnfSeqSeqpCount(final Short unfSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class);
		criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(unfSeq != null) {
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		}
		
		if(seqp != null){
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.SEQP.toString(), seqp));
		}
		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Monta a criteria de salas cirurgicas ativas por SEQ e/ou NOME 
	 * Web Service #38100
	 */
	public DetachedCriteria montarCriteriaObterSalasCirurgicasAtivasPorUnfSeqNome(final Short unfSeq, final Short seqp, final String nome) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcSalaCirurgica.class);
		criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_UNF_SEQ.toString(), unfSeq));
		if (seqp != null) {
			criteria.add(Restrictions.eq(MbcSalaCirurgica.Fields.ID_SEQP.toString(), seqp));
		}
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(MbcSalaCirurgica.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		return criteria;
	}

	/**
	 * Pesqusia salas cirurgicas ativas por SEQ e/ou NOME 
	 * Web Service #38100
	 */
	public List<MbcSalaCirurgica> obterSalasCirurgicasAtivasPorUnfSeqNome(final Short unfSeq, final Short seqp, final String nome) {
		final DetachedCriteria criteria = this.montarCriteriaObterSalasCirurgicasAtivasPorUnfSeqNome(unfSeq, seqp, nome);
		return executeCriteria(criteria, 0, 100, MbcSalaCirurgica.Fields.NOME.toString(), true);
	}
	
	/**
	 * Count de salas cirurgicas ativas por SEQ e/ou NOME 
	 * Web Service #38100
	 */
	public Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(final Short unfSeq, final Short seqp, final String nome) {
		final DetachedCriteria criteria = this.montarCriteriaObterSalasCirurgicasAtivasPorUnfSeqNome(unfSeq, seqp, nome);
		return executeCriteriaCount(criteria);
	}
}
