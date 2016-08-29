package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AghEspecialidadesParametroVOQueryBuilder;
import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghEspecialidadesAtestadoVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioEspecialidadeInterna;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultas;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.faturamento.vo.ConsultaRateioProfissionalVO;
import br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AelEspecialidadeCampoLaudo;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAinServInterna;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;

@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.NcssTypeCount"})
public class AghEspecialidadesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghEspecialidades> {

	private static final long serialVersionUID = -5941358180926402822L;

	private static final String SIGLA_ESPECIALIDADE_PEDIATRIA = "PED";

	private static final String NOME_ESPECIALIDADE_PEDIATRIA = "PEDIATRIA";

	@Inject
	private IParametroFacade aIParametroFacade;

	public List<AghEspecialidades> pesquisarPorSiglaOuNomeOrdSigla(String parametro) {
		String nomeOuSigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			criteria.add(Restrictions.or(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), nomeOuSigla, MatchMode.ANYWHERE),
										 Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE)));
		}
		return this.executeCriteria(criteria, 0, 100, AghEspecialidades.Fields.SIGLA.toString(), true);
	}
	
	public List<AghEspecialidades> pesquisarPorNomeOuSigla(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		return this.executeCriteria(criteria, 0, 100, AghEspecialidades.Fields.NOME.toString(), true);
	}

	public Long pesquisarPorNomeOuSiglaCount(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		return this.executeCriteriaCount(criteria);
	}

	public List<AghEspecialidades> pesquisarPorNomeOuSiglaEspSeqNulo(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESP_SEQ.toString()));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		return this.executeCriteria(criteria, 0, 100, AghEspecialidades.Fields.NOME.toString(), true);
	}
	
	public AghEspecialidades buscarEspecialidadesPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), seq));
		
		return (AghEspecialidades) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AghEspecialidades> pesquisarPorNomeOuSiglaEspSeqNuloAtivos(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESP_SEQ.toString()));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria, 0, 100, AghEspecialidades.Fields.NOME.toString(), true);
	}

	public Long pesquisarPorNomeOuSiglaEspSeqNuloCount(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESP_SEQ.toString()));
		return this.executeCriteriaCount(criteria);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadeFluxogramaPorNomeOuSigla(String parametro) {

		DetachedCriteria criteria = createPesquisarEspecialidadeFluxogramaPorNomeOuSigla(parametro);
		return executeCriteria(criteria, 0, 100, null, false);
	}
	

	public DetachedCriteria  createPesquisarEspecialidadeFluxogramaPorNomeOuSigla(String parametro) {

		String nomeOuSigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			Criterion lhs = Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), nomeOuSigla, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));
		}

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelEspecialidadeCampoLaudo.class, "ccl");
		subCriteria
				.setProjection(Projections.distinct(Projections.property("ccl." + AelEspecialidadeCampoLaudo.Fields.ESP_SEQ.toString())));

		criteria.add(Subqueries.propertyIn(AghEspecialidades.Fields.SEQ.toString(), subCriteria));

		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));

		return criteria;
	}
	
	public Long pesquisarEspecialidadeFluxogramaPorNomeOuSiglaCount(String parametro) {
		
		String nomeOuSigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			Criterion lhs = Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), nomeOuSigla, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));
		}

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelEspecialidadeCampoLaudo.class, "ccl");
		subCriteria
				.setProjection(Projections.distinct(Projections.property("ccl." + AelEspecialidadeCampoLaudo.Fields.ESP_SEQ.toString())));

		criteria.add(Subqueries.propertyIn(AghEspecialidades.Fields.SEQ.toString(), subCriteria));	
		return this.executeCriteriaCount(criteria);
	}
	

	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSigla(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaAtivaParaSigla(parametro);
		return this.executeCriteria(criteria);
	}

	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorNome(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaAtivaParaNome(parametro);
		return this.executeCriteria(criteria);
	}

	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSiglaOrigemSumario(String parametro, Integer atdSeq, RapServidores servidorLogado) {
		
		DetachedCriteria criteriaAtendimento = this.montarCriteriaAtivaParaSiglaPorAtendimentoOrigemSumario(parametro, atdSeq);
		DetachedCriteria criteriaServidor = this.montarCriteriaAtivaParaSiglaPorServidorOrigemSumario(parametro, servidorLogado);
		List<AghEspecialidades> resultAtendimento = this.executeCriteria(criteriaAtendimento);
		List<AghEspecialidades> resultServidor = this.executeCriteria(criteriaServidor);
		if(resultAtendimento != null && resultServidor != null){
			for (AghEspecialidades aghEspecialidades : resultServidor) {
				if(!resultAtendimento.contains(aghEspecialidades)){
					resultAtendimento.add(aghEspecialidades);
				}
			}
		}
		return resultAtendimento != null ? resultAtendimento: resultServidor;
	}

	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorNomeOrigemSumario(String parametro, Integer atdSeq, RapServidores servidorLogado) {
		DetachedCriteria criteriaAtendimento = this.montarCriteriaAtivaParaNomePorAtendimentoOrigemSumario(parametro, atdSeq);
		DetachedCriteria criteriaServidor = this.montarCriteriaAtivaParaNomePorServidorOrigemSumario(parametro, servidorLogado);
		List<AghEspecialidades> resultAtendimento = this.executeCriteria(criteriaAtendimento);
		List<AghEspecialidades> resultServidor = this.executeCriteria(criteriaServidor);
		if(resultAtendimento != null && resultServidor != null){
			for (AghEspecialidades aghEspecialidades : resultServidor) {
				if(!resultAtendimento.contains(aghEspecialidades)){
					resultAtendimento.add(aghEspecialidades);
				}
			}
		}
		return resultAtendimento != null ? resultAtendimento: resultServidor;
	}

	public RapPessoasFisicas obterEspecialidadeInternacaoServidorChefePessoaFisica(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "esp");
		criteria.createAlias("esp." + AghEspecialidades.Fields.SERVIDOR_CHEFIA.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		// criteria.setProjection(Projections.property("ser." +
		// RapServidores.Fields.PESSOA_FISICA.toString()));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), seq));
		return (RapPessoasFisicas) executeCriteriaUniqueResult(criteria);
	}

	public List<AghEspecialidades> pesquisarPorNomeSiglaInternaUnidade(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDINTERNA.toString(), new Object[] { DominioEspecialidadeInterna.I }));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		return this.executeCriteria(criteria);
	}

	public Long pesquisarPorNomeSiglaInternaUnidadeCount(String parametro) {
		String nomeOuSigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			Criterion lhs = Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), nomeOuSigla, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));
		}
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDINTERNA.toString(), new Object[] { DominioEspecialidadeInterna.I }));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteriaCount(criteria);
	}

	public List<AghEspecialidades> pesquisarPorSiglaIndices(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDINTERNA.toString(), new Object[] { DominioEspecialidadeInterna.I,
				DominioEspecialidadeInterna.U }));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria);
	}	

	//#1288
	private DetachedCriteria montarCriteriaEspecialidadesPorServidor(final RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "esp");
		criteria.createAlias("esp." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "pre");
		criteria.createAlias("pre." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "rap");
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");

		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_PROF_REALIZA_CONSULTORIA.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.SER_MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
		
		return criteria;
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadesPorServidor(final RapServidores servidor) {
		DetachedCriteria criteria = this.montarCriteriaEspecialidadesPorServidor(servidor);
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AghEspecialidades> pesquisarPorSiglaIndicesAmbulatorio(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaSigla(parametro);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria, 0, 100, null);
	}

	public List<SuggestionListaCirurgiaVO> pesquisarEspecialidadesCirurgicas(final String filtro, final AghUnidadesFuncionais unidade,
			final Date data, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.ESPECIALIDADE.toString(), "esp");

		if (unidade != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), unidade));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.INDSITUACAO.toString(), indSituacao));
		}

		if (data != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), data));
		}

		if (StringUtils.isNotEmpty(filtro)) {
			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(filtro)));

			} else {
				criteria.add(Restrictions.or(
						Restrictions.ilike("esp." + AghEspecialidades.Fields.SIGLA.toString(), filtro, MatchMode.EXACT),
						Restrictions.ilike("esp." + AghEspecialidades.Fields.NOME.toString(), filtro, MatchMode.ANYWHERE)));

			}
		}

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.distinct(Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString())),
						SuggestionListaCirurgiaVO.Fields.SIGLA.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.NOME.toString()),
						SuggestionListaCirurgiaVO.Fields.NOME.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.SEQ.toString()),
						SuggestionListaCirurgiaVO.Fields.SEQ.toString()));

		criteria.addOrder(Order.asc("esp." + AghEspecialidades.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(SuggestionListaCirurgiaVO.class));

		List<SuggestionListaCirurgiaVO> result = executeCriteria(criteria);

		return result;
	}
	
	
	public Long  pesquisarEspecialidadesCirurgicasCount(final String filtro, final AghUnidadesFuncionais unidade,
			final Date data, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCirurgias.class);
		criteria.createAlias(MbcCirurgias.Fields.ESPECIALIDADE.toString(), "esp");

		if (unidade != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), unidade));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.INDSITUACAO.toString(), indSituacao));
		}

		if (data != null) {
			criteria.add(Restrictions.eq(MbcCirurgias.Fields.DATA.toString(), data));
		}

		if (StringUtils.isNotEmpty(filtro)) {
			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(filtro)));

			} else {
				criteria.add(Restrictions.or(
						Restrictions.ilike("esp." + AghEspecialidades.Fields.SIGLA.toString(), filtro, MatchMode.EXACT),
						Restrictions.ilike("esp." + AghEspecialidades.Fields.NOME.toString(), filtro, MatchMode.ANYWHERE)));

			}
		}

		return executeCriteriaCountDistinct(criteria,"esp." + AghEspecialidades.Fields.NOME.toString() , true);
	
	}
	
	
	public List<AghEspecialidades> pesquisarPorSiglaIndicesAmbulatorioTodasSituacoes(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaSigla(parametro);
		return this.executeCriteria(criteria);
	}

	public List<AghEspecialidades> pesquisarEspecialidadePorNomeOuSigla(
			String parametro) {
		
		DetachedCriteria criteria = this.montarCriteriaParaSigla(parametro);
		List<AghEspecialidades> result = this.executeCriteria(criteria, 0, 100,
				null, false);
		
		if (result.isEmpty()) {
			DetachedCriteria criteria2 = this.montarCriteriaParaNome(parametro);
			result = this.executeCriteria(criteria2, 0, 100, null, false);
		}
		return result;
	}
	
	private DetachedCriteria montarCriteriaParaNomeOuSiglaOuNomeReduzido(String parametro) {
		String nomeOuSigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			if (CoreUtil.isNumeroShort(nomeOuSigla)) {
				criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.parseShort(nomeOuSigla)));
			} else {
				Criterion lhs = Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), nomeOuSigla, MatchMode.EXACT);
				Criterion rhs = Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE);
				Criterion rnr = Restrictions.ilike(AghEspecialidades.Fields.NOME_REDUZIDO.toString(), nomeOuSigla, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(lhs, rhs, rnr));
			}
		}
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public List<AghEspecialidades> pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSiglaOuNomeReduzido(parametro);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		return this.executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEspCount(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSiglaOuNomeReduzido(parametro);
		return this.executeCriteriaCount(criteria);
	}		
	
	public List<AghEspecialidades> pesquisarEspecialidadePorNomeOuSiglaAtivo(
			String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaSigla(parametro);
		List<AghEspecialidades> result = this.executeCriteria(criteria, 0, 100, null, false);
		if (result.isEmpty()) {
			DetachedCriteria criteria2 = this.obterCriteriaParaNome(parametro);
			criteria2.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
			result = this.executeCriteria(criteria2, 0, 100, null, false);
		}
		return result;
	}
	
	public Long pesquisarEspecialidadePorNomeOuSiglaAtivoCount(String parametro) {
		
		Long retorno = Long.valueOf(0);
		DetachedCriteria criteria = this.montarCriteriaSigla(parametro);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		retorno = this.executeCriteriaCount(criteria);
		if (retorno == 0) {
			DetachedCriteria criteria2 = this.obterCriteriaParaNome(parametro);
			criteria2.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
			retorno = this.executeCriteriaCount(criteria2);
		}
		return retorno;
	}
	
	

	private DetachedCriteria montarCriteriaListarEspecialidadePorNomeOuSigla(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(parametro)) {
			Criterion lhs = Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), parametro, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), parametro, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));
		}
		return criteria;
	}

	public List<AghEspecialidades> listarEspecialidadePorNomeOuSigla(String parametro) {
		DetachedCriteria criteria = montarCriteriaListarEspecialidadePorNomeOuSigla(parametro);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		return this.executeCriteria(criteria, 0, 100, null, false);
	}

	public Long listarEspecialidadePorNomeOuSiglaCount(String parametro) {
		DetachedCriteria criteria = montarCriteriaListarEspecialidadePorNomeOuSigla(parametro);
		return executeCriteriaCount(criteria);
	}

	public List<AghEspecialidades> pesquisarEspecialidadePorNomeOuSigla(String parametro, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaParaSiglaSemOrder(parametro);
		List<AghEspecialidades> result = this.executeCriteria(criteria, 0, maxResults, null, false);
		if (result.isEmpty()) {
			DetachedCriteria criteria2 = this.montarCriteriaParaNomeSemOrder(parametro);
			result = this.executeCriteria(criteria2, 0, maxResults, null, false);
		}
		return result;
	}

	public List<AghEspecialidades> pesquisarEspecialidadeAtivaPorNomeOuSigla(String parametro, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaAtivaParaSigla(parametro);
		List<AghEspecialidades> result = this.executeCriteria(criteria, 0, maxResults, null, false);
		if (result.isEmpty()) {
			DetachedCriteria criteria2 = this.montarCriteriaAtivaParaNome(parametro);
			result = this.executeCriteria(criteria2, 0, maxResults, null, false);
		}
		return result;
	}

	public Integer pesquisarEspecialidadeAtivaPorNomeOuSiglaCount(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaAtivaParaSigla(parametro);
		List<AghEspecialidades> result = this.executeCriteria(criteria);
		if (result.isEmpty()) {
			DetachedCriteria criteria2 = this.montarCriteriaAtivaParaNome(parametro);
			result = this.executeCriteria(criteria2);
		}
		return result.size();
	}

	public Integer pesquisarEspecialidadePrincipalAtivaPorNomeOuSiglaCount(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaPrincipalAtivaParaSigla(parametro);
		List<AghEspecialidades> result = this.executeCriteria(criteria);
		if (result.isEmpty()) {
			DetachedCriteria criteria2 = this.montarCriteriaPrincipalAtivaParaNome(parametro);
			result = this.executeCriteria(criteria2);
		}
		return result.size();
	}

	public List<AghEspecialidades> pesquisarEspecialidadePrincipalAtivaPorNomeOuSigla(String parametro, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaPrincipalAtivaParaSigla(parametro);
		List<AghEspecialidades> result = this.executeCriteria(criteria, 0, maxResults, null, false);
		if (result.isEmpty()) {
			DetachedCriteria criteria2 = this.montarCriteriaPrincipalAtivaParaNome(parametro);
			result = this.executeCriteria(criteria2, 0, maxResults, null, false);
		}
		return result;
	}

	private DetachedCriteria montarCriteriaPrincipalAtivaParaSigla(String parametro) {
		String sigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(sigla)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), sigla, MatchMode.EXACT));
		}
		criteria.add(Restrictions.or(
				Restrictions.isNull(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
				Restrictions.eqProperty(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString(),
						AghEspecialidades.Fields.SEQ.toString())));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	private DetachedCriteria montarCriteriaPrincipalAtivaParaNome(String parametro) {
		String nome = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(nome)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nome, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.or(
				Restrictions.isNull(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
				Restrictions.eqProperty(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString(),
						AghEspecialidades.Fields.SEQ.toString())));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	public List<AghEspecialidades> pesquisarPorNomeOuSiglaAmbulatorio(String parametro, FccCentroCustos servico) {
		DetachedCriteria criteria = this.montarCriteriaParaSigla(parametro);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		if (servico != null) {
			criteria.add(Restrictions.eq(AghEspecialidades.Fields.CENTRO_CUSTO.toString(), servico));
		}
		List<AghEspecialidades> result = this.executeCriteria(criteria);
		if (result.isEmpty()) {
			DetachedCriteria criteria2 = this.montarCriteriaParaNome(parametro);
			criteria2.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
			if (servico != null) {
				criteria2.add(Restrictions.eq(AghEspecialidades.Fields.CENTRO_CUSTO.toString(), servico));
			}
			result = this.executeCriteria(criteria2);
		}
		return result;
	}

	public List<AghEspecialidades> pesquisarPorNomeIndicesAmbulatorio(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNome(parametro);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria, 0, 100, null);
	}

	public List<AghEspecialidades> pesquisarPorNomeAmbulatorio(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNome(parametro);
		return this.executeCriteria(criteria);
	}

	public List<AghEspecialidades> pesquisarPorNomeIndicesAmbulatorioTodasSituacoes(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNome(parametro);
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDINTERNA.toString(), new Object[] { DominioEspecialidadeInterna.I,
				DominioEspecialidadeInterna.U }));
		return this.executeCriteria(criteria);
	}

	public List<AghEspecialidades> pesquisarEspecialidades(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaEspecialidades(objPesquisa);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));

		return this.executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarEspecialidadesCount(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaEspecialidades(objPesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaEspecialidades(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		pesquisarPorNomeIdSiglaIndices(objPesquisa, criteria);
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDINTERNA.toString(), new Object[] {
				DominioEspecialidadeInterna.I, DominioEspecialidadeInterna.U }));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(),
				DominioSituacao.A));
		return criteria;
	}

	private DetachedCriteria montarCriteriaParaSigla(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaSigla(parametro);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	private DetachedCriteria montarCriteriaParaSiglaSemOrder(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaSigla(parametro);
		return criteria;
	}

	protected DetachedCriteria montarCriteriaSigla(String parametro) {
		String sigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(sigla)) {
			criteria.add(Restrictions.ilike(
					AghEspecialidades.Fields.SIGLA.toString(), sigla,
					MatchMode.EXACT));
		}
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(),DominioSituacao.A));
		return criteria;
	}

	private DetachedCriteria montarCriteriaParaNome(String parametro) {
		DetachedCriteria criteria = this.obterCriteriaParaNome(parametro);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		return criteria;
	}

	private DetachedCriteria montarCriteriaParaNomeSemOrder(String parametro) {
		DetachedCriteria criteria = this.obterCriteriaParaNome(parametro);
		return criteria;
	}

	protected DetachedCriteria obterCriteriaParaNome(String parametro) {
		String nomeOuSigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE));
		}
		return criteria;
	}

	private DetachedCriteria montarCriteriaParaNomeOuSigla(String parametro) {
		String nomeOuSigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			if (CoreUtil.isNumeroShort(nomeOuSigla)) {
				criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.parseShort(nomeOuSigla)));
			} else {
				Criterion lhs = Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), nomeOuSigla, MatchMode.EXACT);
				Criterion rhs = Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(lhs, rhs));
			}
		}
		return criteria;
	}

	public List<AghEspecialidades> pesquisarAghEspecialidadeAtivasEspSeqNull(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty((String) parametro)) {
			criteria.add(Restrictions.like(AghEspecialidades.Fields.SIGLA.toString(), (String) parametro.toUpperCase(), MatchMode.EXACT));
		}
		List<AghEspecialidades> lista = executeCriteria(criteria, 0, 2, null, false);
		if (lista != null && lista.size() == 1) {
			return lista;
		} else {
			final DetachedCriteria cri = obterCriteriaAghEspecialidadeAtivaEspSeqNull(parametro);
			cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
			return executeCriteria(cri, 0, 100, null, true);
		}
	}

	public Long pesquisarAghEspecialidadeAtivasEspSeqNullCount(String parametro) {
		DetachedCriteria criteria = obterCriteriaAghEspecialidadeAtivaEspSeqNull(parametro);
		if (StringUtils.isNotEmpty((String) parametro)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), (String) parametro, MatchMode.EXACT));
		}
		Long count = executeCriteriaCount(criteria);
		if (count == 1) {
			return count;
		}else {
			return this.executeCriteriaCount(obterCriteriaAghEspecialidadeAtivaEspSeqNull(parametro));
		}
	}

	public DetachedCriteria obterCriteriaAghEspecialidadeAtivaEspSeqNull(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), (String) parametro.toUpperCase(), MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESP_SEQ.toString()));
		return criteria;
	}

	
	private DetachedCriteria montarCriteriaAtivaParaSigla(String parametro) {
		String sigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(sigla)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), sigla, MatchMode.EXACT));
		}
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	private DetachedCriteria montarCriteriaAtivaParaNome(String parametro) {
		String nome = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(nome)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nome, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	private DetachedCriteria montarCriteriaAtivaParaSiglaPorAtendimentoOrigemSumario(String parametro, Integer atdSeq) {
		String sigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");
		if (StringUtils.isNotEmpty(sigla)) {
			criteria.add(Restrictions.ilike("ESP." + AghEspecialidades.Fields.SIGLA.toString(), sigla, MatchMode.EXACT));
		}
		DetachedCriteria subQueryAtd = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		subQueryAtd.setProjection(Projections.property("ATD." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()));
		subQueryAtd.add(Restrictions.eqProperty("ATD." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString(), "ESP." + AghEspecialidades.Fields.SEQ.toString()));
		subQueryAtd.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));

		DetachedCriteria subQueryAtdGen = DetachedCriteria.forClass(AghAtendimentos.class, "ATD_GEN");
		subQueryAtdGen.setProjection(Projections.property("ATD_GEN." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()));
		subQueryAtdGen.add(Restrictions.eqProperty("ATD_GEN." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString(), "ESP." + AghEspecialidades.Fields.ESP_SEQ.toString()));
		subQueryAtdGen.add(Restrictions.eq("ATD_GEN." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		
		criteria.add(Restrictions.or(Subqueries.exists(subQueryAtd),Subqueries.exists(subQueryAtdGen)));
		criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	private DetachedCriteria montarCriteriaAtivaParaSiglaPorServidorOrigemSumario(String parametro, RapServidores servidor) {
		String sigla = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(sigla)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), sigla, MatchMode.EXACT));
		}
		criteria.createAlias(AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "PROF", JoinType.INNER_JOIN);
		if(servidor != null && servidor.getId() != null){
			criteria.add(Restrictions.eq("PROF." + AghProfEspecialidades.Fields.SER_MATRICULA.toString(), servidor.getId().getMatricula()));
			criteria.add(Restrictions.eq("PROF." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
		}
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	private DetachedCriteria montarCriteriaAtivaParaNomePorAtendimentoOrigemSumario(String parametro, Integer atdSeq) {
		String nome = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");
		if (StringUtils.isNotEmpty(nome)) {
			criteria.add(Restrictions.ilike("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nome, MatchMode.ANYWHERE));
		}
		DetachedCriteria subQueryAtd = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		subQueryAtd.setProjection(Projections.property("ATD." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()));
		subQueryAtd.add(Restrictions.eqProperty("ATD." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString(), "ESP." + AghEspecialidades.Fields.SEQ.toString()));
		subQueryAtd.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));

		DetachedCriteria subQueryAtdGen = DetachedCriteria.forClass(AghAtendimentos.class, "ATD_GEN");
		subQueryAtdGen.setProjection(Projections.property("ATD_GEN." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()));
		subQueryAtdGen.add(Restrictions.eqProperty("ATD_GEN." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString(), "ESP." + AghEspecialidades.Fields.ESP_SEQ.toString()));
		subQueryAtdGen.add(Restrictions.eq("ATD_GEN." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		
		criteria.add(Restrictions.or(Subqueries.exists(subQueryAtd),Subqueries.exists(subQueryAtdGen)));
		criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	private DetachedCriteria montarCriteriaAtivaParaNomePorServidorOrigemSumario(String parametro, RapServidores servidor) {
		String nome = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty(nome)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nome, MatchMode.ANYWHERE));
		}
		criteria.createAlias(AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "PROF", JoinType.INNER_JOIN);
		if(servidor != null && servidor.getId() != null){
			criteria.add(Restrictions.eq("PROF." + AghProfEspecialidades.Fields.SER_MATRICULA.toString(), servidor.getId().getMatricula()));
			criteria.add(Restrictions.eq("PROF." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
		}
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		return criteria;
	}

	/**
	 * Lista utilizada no sumário de alta
	 * 
	 * @param atdSeq
	 * @param espSeq
	 * @param intSeq
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesPorInternacao(Integer atdSeq, Short espSeq, Integer intSeq)
			throws ApplicationBusinessException {

		List<AghEspecialidades> listaEspecialidades = new ArrayList<AghEspecialidades>();
		List<AghEspecialidades> listaRestricao = new ArrayList<AghEspecialidades>();
		List<AghEspecialidades> listaUnion = new ArrayList<AghEspecialidades>();

		listaEspecialidades = this.executeCriteria(this.obterEspecialidadesPorInternacao(espSeq, intSeq));
		listaUnion = this.executeCriteria(this.obterEspecialidadesPorInternacao2(espSeq, intSeq));
		listaRestricao = this.executeCriteria(this.obterCriteriaSolicitacaoConsultoria(atdSeq));

		for (AghEspecialidades esp : listaUnion) {

			if (!listaEspecialidades.contains(esp)) {

				listaEspecialidades.add(esp);

			}

		}

		for (AghEspecialidades esp : listaRestricao) {

			if (listaEspecialidades.contains(esp)) {

				listaEspecialidades.remove(esp);

			}

		}

		return listaEspecialidades;
	}

	/**
	 * Lista utilizada no sumário de alta
	 * 
	 * @param atdSeq
	 * @param espSeq
	 * @param atuSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesPorAtendUrgencia(Integer atdSeq, Short espSeq, Integer atuSeq)
			throws ApplicationBusinessException {

		List<AghEspecialidades> listaEspecialidades = new ArrayList<AghEspecialidades>();
		List<AghEspecialidades> listaRestricao = new ArrayList<AghEspecialidades>();

		listaEspecialidades = this.executeCriteria(this.obterEspecialidadesPorAtendUrgencia(espSeq, atuSeq));
		listaRestricao = this.executeCriteria(this.obterCriteriaSolicitacaoConsultoria(atdSeq));

		for (AghEspecialidades esp : listaRestricao) {

			if (listaEspecialidades.contains(esp)) {

				listaEspecialidades.remove(esp);

			}

		}

		return listaEspecialidades;

	}

	private DetachedCriteria obterEspecialidadesPorInternacao(Short espSeq, Integer intSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), AinInternacao.Fields.ESPECIALIDADE.toString());
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), intSeq));
		criteria.add(Restrictions.ne(AinInternacao.Fields.ESP_SEQ.toString(), espSeq));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString())));

		return criteria;
	}

	private DetachedCriteria obterEspecialidadesPorInternacao2(Short espSeq, Integer intSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(),
				AinMovimentosInternacao.Fields.ESPECIALIDADE.toString());
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), intSeq));
		criteria.add(Restrictions.ne(AinMovimentosInternacao.Fields.ESP_SEQ.toString(), espSeq));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString())));

		return criteria;
	}

	private DetachedCriteria obterEspecialidadesPorAtendUrgencia(Short espSeq, Integer atuSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);

		criteria.createAlias(AinAtendimentosUrgencia.Fields.ESPECIALIDADE.toString(),
				AinAtendimentosUrgencia.Fields.ESPECIALIDADE.toString());
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.SEQ.toString(), atuSeq));
		criteria.add(Restrictions.ne(AinAtendimentosUrgencia.Fields.ESP_SEQ.toString(), espSeq));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AinAtendimentosUrgencia.Fields.ESPECIALIDADE.toString())));

		return criteria;
	}

	private DetachedCriteria obterCriteriaSolicitacaoConsultoria(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);

		criteria.createAlias(MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(),
				MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString());
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PME_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.or(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString()),
				Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.M)));
		criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString())));

		return criteria;
	}

	/**
	 * Reutilização de código...
	 * 
	 * @param parametro
	 * @param criteria
	 */
	private void pesquisarPorNomeIdSiglaIndices(final Object parametro, final DetachedCriteria criteria) {

		final String srtPesquisa = (String) parametro;

		if (CoreUtil.isNumeroInteger(parametro)) {

			criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(srtPesquisa)));

		} else if (StringUtils.isNotEmpty(srtPesquisa)) {

			Criterion lhs = Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), srtPesquisa, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), srtPesquisa, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));

		}

		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));

	}

	public List<AghEspecialidades> pesquisarPorNomeIdSiglaIndices(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		pesquisarPorNomeIdSiglaIndices(parametro, criteria); // Reutilização de
																// código
		// #1094
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDCONSULTORIA.toString(), new Object[] { DominioSimNao.S }));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		return this.executeCriteria(criteria);
	}
	
	public Long pesquisarPorNomeIdSiglaIndicesCount(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		pesquisarPorNomeIdSiglaIndices(parametro, criteria); // Reutilização de
																// código
		// #1094
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDCONSULTORIA.toString(), new Object[] { DominioSimNao.S }));
		return this.executeCriteriaCount(criteria);
	}

	public List<AghEspecialidades> pesquisarPorIdSigla(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDCONSULTORIA.toString(), new Object[] { DominioSimNao.S }));
		criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), parametro.toString(), MatchMode.EXACT));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));

		return this.executeCriteria(criteria);
	}
	
	public Long pesquisarPorIdSiglaCount(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.in(AghEspecialidades.Fields.INDCONSULTORIA.toString(), new Object[] { DominioSimNao.S }));
		criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), parametro.toString(), MatchMode.EXACT));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));

		return this.executeCriteriaCount(criteria);
	}

	public List<AghEspecialidades> pesquisarPorSiglaAtivasIndices(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		pesquisarPorNomeIdSiglaIndices(parametro, criteria);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		return this.executeCriteria(criteria);
	}

	public List<AghEspecialidades> pesquisarPorSiglaAtivas(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), parametro.toString(), MatchMode.EXACT));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		return this.executeCriteria(criteria);
	}

	/**
	 * @ORADB carga_capacidade_esp
	 */
	public List<AghEspecialidades> carregarCapacidadeEspecialidade() {

		// Query referente ao cursor "capc_esp"
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);
		cri.createAlias(AghEspecialidades.Fields.CLINICA.toString(), AghEspecialidades.Fields.CLINICA.toString());
		cri.add(Restrictions.ne(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.N));
		cri.addOrder(Order.asc(AghEspecialidades.Fields.SEQ.toString()));

		return executeCriteria(cri);
	}

	public List<AghEspecialidades> pesquisarEspecialidadesAgendas(String filtro) {
		DetachedCriteria criteria = createPesquisaEspecialidadesAgendasCriteria(filtro);
		return executeCriteria(criteria, 0, 25, AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), true);
	}

	public Long pesquisarEspecialidadesAgendasCount(String filtro) {
		DetachedCriteria criteria = createPesquisaEspecialidadesAgendasCriteria(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createPesquisaEspecialidadesAgendasCriteria(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		if (StringUtils.isNotBlank(filtro)) {
			Criterion siglaNomeEspecialidadeCriterion = Restrictions.or(
					Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), filtro, MatchMode.ANYWHERE),
					Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), filtro, MatchMode.ANYWHERE));

			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.or(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(filtro)),
						siglaNomeEspecialidadeCriterion));
			} else {
				criteria.add(siglaNomeEspecialidadeCriterion);
			}
		}

		return criteria;
	}

	public List<AghEspecialidades> pesquisarEspecialidadesPorClinica(AghClinicas clinica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.CLINICA_CODIGO.toString(), clinica.getCodigo()));
		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * Busca especialidades por Nome ou Sigla
	 * 
	 * @return Lista de especialidades
	 */
	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty((String) strPesquisa)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), (String) strPesquisa, MatchMode.EXACT));
		}

		List<AghEspecialidades> lista = executeCriteria(criteria, 0, 2, null, false);

		if (lista != null && lista.size() == 1) {
			return lista;
		} else {
			final DetachedCriteria cri = obterCriteriaPesquisarEspecialidadePorSiglaNome(strPesquisa);
			cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));

			return executeCriteria(cri, 0, 100, AghEspecialidades.Fields.NOME.toString(), true);
		}
	}

	public Long pesquisarEspecialidadePorSiglaNomeCount(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty((String) strPesquisa)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), (String) strPesquisa, MatchMode.EXACT));
		}

		Long count = executeCriteriaCount(criteria);

		if (count == 1) {
			return count;
		} else {
			return executeCriteriaCount(obterCriteriaPesquisarEspecialidadePorSiglaNome(strPesquisa));
		}
	}

	private DetachedCriteria obterCriteriaPesquisarEspecialidadePorSiglaNome(Object strPesquisa) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);

		if (strPesquisa != null) {
			cri.add(Restrictions.or(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), (String) strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), (String) strPesquisa, MatchMode.ANYWHERE)));
		}

		return cri;
	}

	private Query clinicaEspecialidade(AghClinicas clinica, boolean count) throws ApplicationBusinessException {
		StringBuffer sql1 = new StringBuffer(420);
		if (count) {
			sql1.append(" select count (*) ");
		} else {
			sql1.append(" select new br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO (");
			sql1.append(" 	esp.clinica.codigo, esp.seq, esp.sigla, esp.capacReferencial, ih.mediaPermanencia, ih.percentualOcupacao ");
			sql1.append(" ) ");
		}
		sql1.append(" from AghEspecialidades esp ");
		sql1.append(" join esp.indicadoresHospitalares as ih ");
		sql1.append(" where esp.clinica = :clinica ");
		sql1.append(" and esp.indEspInterna = :indEspInterna ");
		sql1.append(" and ih.tipoUnidade = :tipoUnidade ");
		sql1.append(" and ih.serVinCodigo is null ");
		sql1.append(" and ih.serMatricula is null ");
		sql1.append(" and ih.unidadeFuncional.seq is null ");
		sql1.append(" and ih.competenciaInternacao = ( ");
		sql1.append(" 	select max(ih2.competenciaInternacao) ");
		sql1.append(" 	from AinIndicadoresHospitalares ih2 ");
		sql1.append(" 	where ih2.especialidade = esp ");
		sql1.append(" ) ");

		Query query = createHibernateQuery(sql1.toString());
		query.setParameter("clinica", clinica);
		query.setParameter("indEspInterna", DominioEspecialidadeInterna.I);
		query.setParameter("tipoUnidade", DominioTipoUnidade.U);

		return query;
	}

	public Long countEspecialidadeReferencial(AghClinicas clinica) throws ApplicationBusinessException {
		// Count do SQL #1 da view
		Query query = this.clinicaEspecialidade(clinica, true);
		return (Long) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<PesquisaReferencialClinicaEspecialidadeVO> listaEspecialidadeReferencial(AghClinicas clinica)
			throws ApplicationBusinessException {
		// SQL #1 da view
		Query query = this.clinicaEspecialidade(clinica, false);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<AghEspecialidades> pesquisarSolicitarProntuarioEspecialidade(String strPesquisa) {
		List<AghEspecialidades> li = null;

		// Utilizado HQL, pois não é possível fazer pesquisar com LIKE para
		// tipos numéricos (Integer, Byte, Short)
		// através de Criteria (ocorre ClassCastException).
		StringBuilder hql = new StringBuilder(100);
		hql.append("from AghEspecialidades e ");

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
		}

		hql.append("order by e.nomeEspecialidade");

		Query q = createHibernateQuery(hql.toString());
		q.setMaxResults(100);

		li = q.list();
		return li;
	}

	/**
	 * 
	 * Lista as Especialidades pela sigla
	 * 
	 * @dbtables AghEspecialidades select
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSigla(Object paramPesquisa) {

		final DetachedCriteria criteria = createaCriteriaPesquisarEspecialidadesInternasPorSigla(paramPesquisa);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}

	private DetachedCriteria createaCriteriaPesquisarEspecialidadesInternasPorSigla(Object paramPesquisa) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.ne(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.N));

		String strPesquisa = (String) paramPesquisa;
		if (StringUtils.isNotBlank(strPesquisa)) {
			strPesquisa = strPesquisa.trim();
			strPesquisa = strPesquisa.replace('%', '#');
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.START));
		}

		return criteria;
	}

	/**
	 * 
	 * Lista as Especialidades pela sigla ou nome
	 * 
	 * @dbtables AghEspecialidades select
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSiglaENome(Object paramPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty((String) paramPesquisa)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), (String) paramPesquisa, MatchMode.EXACT));
		}

		List<AghEspecialidades> lista = executeCriteria(criteria, 0, 2, null, false);

		if (lista != null && lista.size() == 1) {
			return lista;
		} else {
			criteria = createaCriteriaPesquisarEspecialidadesInternasPorSiglaENome(paramPesquisa);
			criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));

			return executeCriteria(criteria, 0, 100, null, false);
		}
	}

	public Long pesquisarEspecialidadesInternasPorSiglaENomeCount(Object paramPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotEmpty((String) paramPesquisa)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), (String) paramPesquisa, MatchMode.EXACT));
		}

		Long count = executeCriteriaCount(criteria);

		if (count == 1) {
			return count;
		} else {
			return executeCriteriaCount(createaCriteriaPesquisarEspecialidadesInternasPorSiglaENome(paramPesquisa));
		}
	}

	private DetachedCriteria createaCriteriaPesquisarEspecialidadesInternasPorSiglaENome(Object paramPesquisa) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.ne(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.N));

		String strPesquisa = (String) paramPesquisa;
		if (StringUtils.isNotBlank(strPesquisa)) {
			strPesquisa = strPesquisa.trim();
			strPesquisa = strPesquisa.replace('%', '#');
			criteria.add(Restrictions.or(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.START),
					Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}

		return criteria;
	}

	public List<AghEspecialidades> pesquisarEspecialidades(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short codigoEspecialidade, String nomeEspecialidade, String siglaEspecialidade, Short codigoEspGenerica, Integer centroCusto,
			Integer clinica, DominioSituacao situacao) {
		DetachedCriteria criteria = createPesquisaCriteria(codigoEspecialidade, nomeEspecialidade, siglaEspecialidade, codigoEspGenerica,
				centroCusto, clinica, situacao);
		criteria.createAlias(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), // associationPath
				AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), // alias
				CriteriaSpecification.LEFT_JOIN);
		if (orderProperty == null) {
			criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE_GENERICA.toString()));
			criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		}
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarEspecialidadesCount(Short codigo, String nomeEspecialidade, String siglaEspecialidade, Short codigoEspGenerica,
			Integer centroCusto, Integer clinica, DominioSituacao situacao) {
		return executeCriteriaCount(createPesquisaCriteria(codigo, nomeEspecialidade, siglaEspecialidade, codigoEspGenerica, centroCusto,
				clinica, situacao));
	}

	private DetachedCriteria createPesquisaCriteria(Short codigo, String nomeEspecialidade, String siglaEspecialidade,
			Short codigoEspGenerica, Integer centroCusto, Integer clinica, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.createAlias(AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "centroCusto");
		criteria.createAlias("centroCusto." + FccCentroCustos.Fields.SERVIDOR.toString(), "centroCustoServidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("centroCustoServidor." + RapServidores.Fields.PESSOA_FISICA.toString(), "centroCustoPesFisica",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghEspecialidades.Fields.CLINICA.toString(), "clinica");
		criteria.createAlias(AghEspecialidades.Fields.SERVIDOR_CHEFIA.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor." + RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica",JoinType.LEFT_OUTER_JOIN);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), codigo));
		}

		if (!StringUtils.isBlank(siglaEspecialidade)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), siglaEspecialidade, MatchMode.EXACT));
		}

		if (codigoEspGenerica != null) {
			criteria.add(Restrictions.eq(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString(), codigoEspGenerica));
		}

		if (centroCusto != null) {
			criteria.add(Restrictions.eq(AghEspecialidades.Fields.CENTRO_CUSTO_CODIGO.toString(), centroCusto));
		}

		if (clinica != null) {
			criteria.add(Restrictions.eq(AghEspecialidades.Fields.CLINICA_CODIGO.toString(), clinica));
		}

		if (!StringUtils.isBlank(nomeEspecialidade)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), nomeEspecialidade, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), situacao));
		}

		return criteria;
	}

	public List<AghEspecialidades> pesquisarTodasEspecialidades(String strPesquisa){
		return executeCriteria(montaPesquisaTodasEspecialidades(strPesquisa));
	}

	public Long pesquisarTodasEspecialidadesCount(String strPesquisa){
		return executeCriteriaCount(montaPesquisaTodasEspecialidades(strPesquisa));
	}
	
	
	private DetachedCriteria montaPesquisaTodasEspecialidades(String strPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion cNome = Restrictions.or(
					Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE));
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.or(cNome, Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(strPesquisa))));
			} else {
				criteria.add(cNome);
			}
		}
		return criteria;
	}

	/**
	 * Pesquisa de especialidade genérica por nome ou código
	 * 
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 */
	public List<AghEspecialidades> pesquisarEspecialidadeGenerica(String strPesquisa, Integer maxResults) {
		DetachedCriteria criteria = montarCriteriaEspecialidadeGenerica(strPesquisa);
		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()));

		return executeCriteria(criteria, 0, maxResults, AghEspecialidades.Fields.NOME.toString(), true);
	}

	public Long pesquisarEspecialidadeGenericaCount(String strPesquisa) {
		final DetachedCriteria criteria = montarCriteriaEspecialidadeGenerica(strPesquisa);
		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()));
		return executeCriteriaCount(criteria);
	}

	public List<AghEspecialidades> pesquisarEspecialidadesSemEspSeq(String strPesquisa) {
		DetachedCriteria criteria = montarCriteriaEspecialidadesSemEspSeq(strPesquisa);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		return executeCriteria(criteria);
	}

	public Long pesquisarEspecialidadesSemEspSeqCount(String strPesquisa) {
		final DetachedCriteria criteria = montarCriteriaEspecialidadesSemEspSeq(strPesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaEspecialidadesSemEspSeq(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESP_SEQ.toString()));

		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion cNome = Restrictions.or(
					Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE));
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.or(cNome, Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(strPesquisa))));
			} else {
				criteria.add(cNome);
			}
		}

		return criteria;
	}

	/**
	 * Pesquisa de especialidade genérica por nome ou código levando em conta a
	 * idade do paciente internado e o índice de especialidade interna
	 * 
	 * Pesquisa utilizada na tela de internação.
	 * 
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 *            , idadePaciente
	 */
	public List<AghEspecialidades> pesquisarEspecialidadeInternacao(String strPesquisa, Short idadePaciente,
			DominioSimNao indUnidadeEmergencia, Integer maxResults) {

		DetachedCriteria criteria = montarCriteriaEspecialidadeInternacao(idadePaciente, indUnidadeEmergencia);
		List<AghEspecialidades> lista = new ArrayList<AghEspecialidades>();

		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.EXACT));
			}
			lista = executeCriteria(criteria, 0, maxResults, AghEspecialidades.Fields.SIGLA.toString(), true);
			if (lista.isEmpty()) {
				DetachedCriteria criteria2 = montarCriteriaEspecialidadeInternacao(idadePaciente, indUnidadeEmergencia);
				criteria2.add(Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), strPesquisa, MatchMode.ANYWHERE));
				lista = executeCriteria(criteria2, 0, maxResults, AghEspecialidades.Fields.SIGLA.toString(), true);
			}
		} else {
			lista = executeCriteria(criteria, 0, maxResults, AghEspecialidades.Fields.SIGLA.toString(), true);
		}

		return lista;
	}

	public List<AghEspecialidades> obterEspecialidadePorSiglas(List<String> siglas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (siglas != null && !siglas.isEmpty()) {
			criteria.add(Restrictions.in(AghEspecialidades.Fields.SIGLA.toString(), siglas));
			return executeCriteria(criteria);
		}
		return null;
	}	
	
	private DetachedCriteria montarCriteriaEspecialidadeInternacao(Short idadePaciente, DominioSimNao indUnidadeEmergencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.ge(AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idadePaciente));
		criteria.add(Restrictions.le(AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idadePaciente));

		if (DominioSimNao.N.equals(indUnidadeEmergencia)) {
			criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.I));
		}

		criteria.add(Restrictions.or(Restrictions.eq(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.I),
				Restrictions.eq(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.U)));
		
		criteria.createAlias(AghEspecialidades.Fields.CLINICA.toString(), AghEspecialidades.Fields.CLINICA.toString());

		return criteria;
	}

	/**
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 * @param idadePaciente
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadeSolicitacaoInternacao(String strPesquisa, Short idadePaciente, Integer maxResults) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), (String) strPesquisa, MatchMode.EXACT));

		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.ge(AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idadePaciente));

		criteria.add(Restrictions.le(AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idadePaciente));

		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.I));

		List<AghEspecialidades> lista = executeCriteria(criteria, 0, 2, null, false);

		if (lista != null && lista.size() == 1) {
			return lista;
		} else {
			criteria = montarCriteriaEspecialidadeGenerica(strPesquisa);
			criteria.add(Restrictions.ge(AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idadePaciente));
			criteria.add(Restrictions.le(AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idadePaciente));

			criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.I));

			criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));

			return executeCriteria(criteria, 0, maxResults, AghEspecialidades.Fields.NOME.toString(), true);
		}
	}

	/**
	 * Método que monta a criteria para retornar uma especialidade genérica
	 * 
	 * @param strPesquisa
	 */
	private DetachedCriteria montarCriteriaEspecialidadeGenerica(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));

		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion cNome = Restrictions.or(
					Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE));
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.or(cNome, Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(strPesquisa))));
			} else {
				criteria.add(cNome);
			}
		}

		return criteria;
	}

	/**
	 * Obtem uma lista de especialidades de pediatria sem à ocorrência de
	 * especialidade genérica
	 * 
	 * @return lista de especialidades
	 */
	public List<AghEspecialidades> obterEspecialidadePediatria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), SIGLA_ESPECIALIDADE_PEDIATRIA, MatchMode.ANYWHERE));
		criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), NOME_ESPECIALIDADE_PEDIATRIA, MatchMode.ANYWHERE));
		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString()));
		return executeCriteria(criteria);
	}

	public AghEspecialidades obterEspecialidadePorSigla(String sigla) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);

		AghEspecialidades especialidade = null;

		if (sigla != null && !StringUtils.isBlank(sigla)) {
			cri.add(Restrictions.eq(AghEspecialidades.Fields.SIGLA.toString(), sigla));

			especialidade = (AghEspecialidades) executeCriteriaUniqueResult(cri);
		}

		return especialidade;
	}
	
	public Long pesquisarEspecialidadePorSeqOuSiglaOuNomeCount(Object objPesquisa) {
		Long retorno;
		String strCriterio = (String) objPesquisa;
		if (StringUtils.isNotBlank(strCriterio) && !StringUtils.isEmpty(strCriterio)) {
			retorno = executeCriteriaCount(montarCriteriaPesquisarEspecialidadeSigla(strCriterio));
			if (retorno == 0) {
				retorno = executeCriteriaCount(montarCriteriaPesquisarEspecialidadeDescricao(strCriterio, true));
			}
		}
		else {
			retorno = executeCriteriaCount(DetachedCriteria.forClass(AghEspecialidades.class));
		}

		return retorno;
	}
	

	/**
	 * 
	 * Lista as Especialidades pela sigla ou nome
	 * 
	 * @dbtables AghEspecialidades select
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadePorSeqOuSiglaOuNome(Object strPesquisa) {
		List<AghEspecialidades> retorno = new ArrayList<AghEspecialidades>();
		String strCriterio = (String) strPesquisa;
		
		if (StringUtils.isNotBlank(strCriterio) && StringUtils.isNumeric(strCriterio)) {
			retorno = executeCriteria(montarCriteriaPesquisarEspecialidadePorSeq(strCriterio), 0, 100, null, false);
		}else {
			retorno = executeCriteria(montarCriteriaPesquisarEspecialidadeSigla(strCriterio), 0, 100, null, false);
			if (retorno.size() == 0) {
				retorno = executeCriteria(montarCriteriaPesquisarEspecialidadeDescricao(strCriterio, false), 0, 100, null, false);
			}
		}
		return retorno;
	}
	
	private DetachedCriteria montarCriteriaPesquisarEspecialidadeSigla(String strCriterio) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);
		cri.add(Restrictions.eq(AghEspecialidades.Fields.SIGLA.toString(), strCriterio.toUpperCase()));
		return cri;
	}
	
	private DetachedCriteria montarCriteriaPesquisarEspecialidadeDescricao(String strCriterio, boolean pesquisaContador) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);
		cri.add(Restrictions.like(AghEspecialidades.Fields.NOME.toString(), strCriterio.toUpperCase(), MatchMode.ANYWHERE));
		if (!pesquisaContador) {
			cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		}
		return cri;
	}
	
	private DetachedCriteria montarCriteriaPesquisarEspecialidadePorSeq(String strCriterio) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);
		cri.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(strCriterio)));
		return cri;
	}
	
	
	public AghEspecialidades obterEspecialidade(Short seq, LockOptions lockMode) {
		return (AghEspecialidades) this.getAndLock(seq, lockMode);
	}

	/* ******** */

	private DetachedCriteria criarCriteriaEspecialidades() {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);
		cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		return cri;
	}

	private DetachedCriteria criarCriteriaEspecialidadesAtivas() {
		DetachedCriteria cri = criarCriteriaEspecialidades();
		cri.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return cri;
	}

	private DetachedCriteria criarCriteriaEspecialidadesInternasUrgenciaeAtivas() {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);

		cri.add(Restrictions.in(AghEspecialidades.Fields.INDINTERNA.toString(), new DominioEspecialidadeInterna[] {
				DominioEspecialidadeInterna.I, DominioEspecialidadeInterna.U }));
		cri.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return cri;
	}

	private DetachedCriteria criarCriteriaEspecialidades(boolean verificarIndConsultoria, boolean apenasAtivas) {
		// Primeiro pesquisa apenas por sigla, caso tenham sido informados
		// exatamente 3 caracteres
		DetachedCriteria cri = null;

		if (apenasAtivas) {
			cri = criarCriteriaEspecialidadesAtivas();
		} else {
			cri = criarCriteriaEspecialidades();
		}

		if (verificarIndConsultoria) {
			adicionarRestrictionIndConsultoria(cri);
		}
		return cri;
	}

	private DetachedCriteria criarCriteriaEspecialidadesInternas() {
		// Primeiro pesquisa apenas por sigla, caso tenham sido informados
		// exatamente 3 caracteres
		DetachedCriteria cri = criarCriteriaEspecialidadesAtivas();
		cri.add(Restrictions.eq("indEspInterna", DominioEspecialidadeInterna.I));
		return cri;
	}

	private DetachedCriteria criarCriteriaEspecialidadesAtivasNaoInternas() {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);
		cri.add(Restrictions.ne(AghEspecialidades.Fields.INDINTERNA.toString(), DominioEspecialidadeInterna.N));
		cri.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		return cri;
	}

	private DetachedCriteria adicionarRestrictionIndConsultoria(DetachedCriteria cri) {
		cri.add(Restrictions.eq(AghEspecialidades.Fields.INDCONSULTORIA.toString(), DominioSimNao.S));
		return cri;
	}

	/* ************** */

	public List<AghEspecialidades> pesquisaEspecialidadesPorSiglaFaixaIdade(String strPesquisa, Integer idade) {
		List<AghEspecialidades> lista = null;
		DetachedCriteria cri = criarCriteriaEspecialidadesInternasUrgenciaeAtivas();
		// Caso exista uma string de pesquisa, busca pela sigla
		if (StringUtils.isNotBlank(strPesquisa)) {
			cri.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.START));
			if (idade != null) {
				cri.add(Restrictions.le(AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idade.shortValue()));
				cri.add(Restrictions.ge(AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idade.shortValue()));
			}
			cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
			lista = executeCriteria(cri);
		}
		return lista;
	}

	public List<AghEspecialidades> pesquisaEspecialidadesPorNomeFaixaIdade(String strPesquisa, Integer idade) {
		List<AghEspecialidades> lista = null;
		DetachedCriteria cri = criarCriteriaEspecialidadesInternasUrgenciaeAtivas();
		// Caso exista uma string de pesquisa, busca pela sigla
		if (StringUtils.isNotBlank(strPesquisa)) {
			cri = criarCriteriaEspecialidadesInternasUrgenciaeAtivas();

			cri.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			if (idade != null) {
				cri.add(Restrictions.le(AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idade.shortValue()));
				cri.add(Restrictions.ge(AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idade.shortValue()));
			}

			cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));

			lista = executeCriteria(cri);
		}
		return lista;
	}

	public List<AghEspecialidades> pesquisaEspecialidadesPorFaixaIdade(Short idade) {
		List<AghEspecialidades> lista = null;
		DetachedCriteria cri = criarCriteriaEspecialidadesInternasUrgenciaeAtivas();
		if (idade != null) {
			cri.add(Restrictions.le(AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idade));
			cri.add(Restrictions.ge(AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idade));
		}
		cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		lista = executeCriteria(cri);

		return lista;
	}

	public List<AghEspecialidades> pesquisaEspecialidadesAtivasNaoInternasPorSigla(String strPesquisa, Integer maxResults) {
		DetachedCriteria cri = criarCriteriaEspecialidadesAtivasNaoInternas();
		if (StringUtils.isNotBlank(strPesquisa)) {
			strPesquisa = strPesquisa.trim();
			strPesquisa = strPesquisa.replace('%', '#');
			cri.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.START));
			cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		}
		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> pesquisaEspecialidadesAtivasNaoInternasPorNome(String strPesquisa, Integer maxResults) {
		DetachedCriteria cri = criarCriteriaEspecialidadesAtivasNaoInternas();
		cri.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> pesquisaEspecialidadesInternasPorSiglaFaixaIdade(String strPesquisa, Short idade, Integer maxResults) {
		DetachedCriteria cri = criarCriteriaEspecialidadesInternas();
		if (StringUtils.isNotBlank(strPesquisa)) {
			strPesquisa = strPesquisa.trim();
			strPesquisa = strPesquisa.replace('%', '#');
			cri.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.START));
			cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		}
		if (idade != null) {
			cri.add(Restrictions.le(AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idade));
			cri.add(Restrictions.ge(AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idade));
		}
		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> pesquisaEspecialidadesInternasPorNomeFaixaIdade(String strPesquisa, Short idade, Integer maxResults) {
		DetachedCriteria cri = criarCriteriaEspecialidadesInternas();
		cri.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		if (idade != null) {
			cri.add(Restrictions.le(AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idade));
			cri.add(Restrictions.ge(AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idade));
		}
		cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> pesquisaEspecialidadesPorSeq(Short seq, boolean verificarIndConsultoria, boolean apenasSiglaSePossivel,
			boolean apenasAtivas, Integer maxResults) {
		DetachedCriteria cri = criarCriteriaEspecialidades(verificarIndConsultoria, apenasAtivas);
		cri.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), seq));
		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> pesquisaEspecialidadesPorSigla(String sigla, boolean verificarIndConsultoria,
			boolean apenasSiglaSePossivel, boolean apenasAtivas, Integer maxResults) {
		DetachedCriteria cri = criarCriteriaEspecialidades(verificarIndConsultoria, apenasAtivas);
		cri.add(Restrictions.eq(AghEspecialidades.Fields.SIGLA.toString(), sigla));
		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> pesquisaEspecialidadesPorNome(String nome, boolean verificarIndConsultoria,
			boolean apenasSiglaSePossivel, boolean apenasAtivas, Integer maxResults) {
		DetachedCriteria cri = criarCriteriaEspecialidades(verificarIndConsultoria, apenasAtivas);
		cri.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> pesquisaEspecialidades(boolean verificarIndConsultoria, boolean apenasSiglaSePossivel,
			boolean apenasAtivas, Integer maxResults) {
		DetachedCriteria cri = criarCriteriaEspecialidades(verificarIndConsultoria, apenasAtivas);
		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> listarEspecialidadesSolicitacaoProntuario(String strPesquisa, boolean ordemPorSigla,
			boolean apenasAtivas, Integer maxResults) {
		DetachedCriteria cri = obterCriteriaListarEspecialidadesSolicitacaoProntuario(strPesquisa, apenasAtivas);

		if (ordemPorSigla) {
			cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		} else {
			cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		}

		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public Long listarEspecialidadesSolicitacaoProntuarioCount(String strPesquisa, boolean apenasAtivas) {
		final DetachedCriteria cri = obterCriteriaListarEspecialidadesSolicitacaoProntuario(strPesquisa, apenasAtivas);
		return executeCriteriaCount(cri);
	}

	private DetachedCriteria obterCriteriaListarEspecialidadesSolicitacaoProntuario(String strPesquisa, final boolean apenasAtivas) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);

		if (apenasAtivas) {
			cri.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		}

		if (StringUtils.isNotBlank(strPesquisa)) {
			strPesquisa = strPesquisa.trim();
			strPesquisa = strPesquisa.replace('%', '#');
			cri.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.START));
		}

		return cri;
	}

	public List<AghEspecialidades> listarEspecialidadesPorSigla(String strPesquisa, boolean ordemPorSigla, boolean apenasAtivas,
			Integer maxResults) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotBlank(strPesquisa)) {
			strPesquisa = strPesquisa.trim();
			strPesquisa = strPesquisa.replace('%', '#');
			cri.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.START));
			if (apenasAtivas) {
				cri.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
			}
			if (ordemPorSigla) {
				cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
			} else {
				cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
			}
		} else {
			if (apenasAtivas) {
				cri.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
			}
			if (ordemPorSigla) {
				cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
			} else {
				cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
			}
		}

		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<AghEspecialidades> listarEspecialidadesPorNome(String strPesquisa, boolean ordemPorSigla, boolean apenasAtivas,
			Integer maxResults) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEspecialidades.class);
		cri.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));

		if (apenasAtivas) {
			cri.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		}

		if (ordemPorSigla) {
			cri.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		} else {
			cri.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		}

		if (maxResults != null) {
			return executeCriteria(cri, 0, maxResults, null, false);
		}
		return executeCriteria(cri);
	}

	public List<ConsultaRateioProfissionalVO> listarConsultaRateioServicosProfissionais(FatCompetencia competencia)
			throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");
		criteria.createAlias(AghEspecialidades.Fields.PROCEDIMENTO_AMB_REALIZADO.toString(), "FP");
		criteria.createAlias("FP." + FatProcedAmbRealizado.Fields.FAT_ESPELHOS_PROCED_AMB.toString(), "FE");
		criteria.createAlias("FE." + FatEspelhoProcedAmb.Fields.ITENS_PROCED_HOSPITALAR.toString(), "FI");

		// projeções
		// projeções
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.sum("FE." + FatEspelhoProcedAmb.Fields.QUANTIDADE.toString()),
				ConsultaRateioProfissionalVO.Fields.QUANTIDADE.toString());
		pList.add(Projections.sum("FE." + FatEspelhoProcedAmb.Fields.VLR_PROC.toString()),
				ConsultaRateioProfissionalVO.Fields.VALOR_PROCEDIMENTO.toString());
		pList.add(Projections.sum("FE." + FatEspelhoProcedAmb.Fields.VLR_SERV_PROF.toString()),
				ConsultaRateioProfissionalVO.Fields.VLR_SERV_PROF_REPORT.toString());
		pList.add(Projections.count("FI." + FatItensProcedHospitalar.Fields.COD_PROCEDIMENTO.toString()),
				ConsultaRateioProfissionalVO.Fields.COUNT_COD_PROCED.toString());
		pList.add(Projections.groupProperty("FE." + FatEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString()),
				ConsultaRateioProfissionalVO.Fields.PROCEDIMENTO_HOSP.toString());
		pList.add(Projections.groupProperty("FI." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()),
				ConsultaRateioProfissionalVO.Fields.DESCRICAO.toString());
		pList.add(Projections.groupProperty("FI." + FatItensProcedHospitalar.Fields.COD_PROCEDIMENTO.toString()),
				ConsultaRateioProfissionalVO.Fields.CODIGO_PROCEDIMENTO.toString());
		criteria.setProjection(pList);

		// WHERE
		StringBuilder clausulaCCTCodigo = new StringBuilder("{alias}.CCT_CODIGO = ")
				.append(" CASE WHEN {alias}.CCT_CODIGO = ")
				.append(this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_CENTRO_CUSTOS_SERV_MED_OCUPAC)
						.getVlrNumerico().intValue())
				.append(" THEN ")
				.append(" CASE WHEN {alias}.SEQ = ")
				.append(this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_ESPECIALIDADE_DOENCAS_TRABALHO)
						.getVlrNumerico().shortValue())
				.append(" THEN ")
				.append(" {alias}.CCT_CODIGO ")
				.append(" ELSE ")
				.append(" -1 ")
				.append(" END ")
				.append(" ELSE CASE WHEN {alias}.CCT_CODIGO = ")
				.append(this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_CENTRO_CUSTOS_UNID_MEDICA_FUNC)
						.getVlrNumerico().intValue()).append(" THEN ").append(" -1 ").append("  ELSE ").append(" {alias}.CCT_CODIGO ")
				.append("  END ").append(" END ");

		Criterion criCctCodigo = Restrictions.sqlRestriction(clausulaCCTCodigo.toString());

		criteria.add(Restrictions.eq("FE." + FatEspelhoProcedAmb.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.AMB));
		criteria.add(Restrictions.eq("FE." + FatEspelhoProcedAmb.Fields.IND_CONSISTENTE.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("FE." + FatEspelhoProcedAmb.Fields.IND_CONSULTA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("FE." + FatEspelhoProcedAmb.Fields.CPE_MES.toString(), competencia.getId().getMes()));
		criteria.add(Restrictions.eq("FE." + FatEspelhoProcedAmb.Fields.CPE_ANO.toString(), competencia.getId().getAno()));

		criteria.add(criCctCodigo);

		criteria.addOrder(Order.asc("FE." + FatEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaRateioProfissionalVO.class));

		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaProfEspecialidade(String propriedade, AghEspecialidades especialidade) {
		DetachedCriteria criteriaAghProfEspecialidades = DetachedCriteria.forClass(AghProfEspecialidades.class, "prof");
		criteriaAghProfEspecialidades.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "servidor");

		criteriaAghProfEspecialidades.setProjection(Projections.property(AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString()));

		criteriaAghProfEspecialidades.add(Restrictions.eq(AghProfEspecialidades.Fields.ID_ESPECIALIDADE_SEQ.toString(),
				(especialidade != null ? especialidade.getSeq() : null)));

		criteriaAghProfEspecialidades.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true));

		Criterion cond1 = Restrictions.or(Restrictions.eq("servidor.indSituacao", DominioSituacaoVinculo.A),
				Restrictions.eq("servidor.indSituacao", DominioSituacaoVinculo.P));
		Criterion cond2 = Restrictions.or(Restrictions.isNull("servidor.dtFimVinculo"),
				Restrictions.ge("servidor.dtFimVinculo", Calendar.getInstance().getTime()));

		criteriaAghProfEspecialidades.add(Restrictions.and(cond1, cond2));

		criteriaAghProfEspecialidades.add(Restrictions.eqProperty("servidor.id", propriedade + ".id"));
		return criteriaAghProfEspecialidades;
	}

	public List<AghEspecialidades> listarEspecialidadesPorServidor(Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");
		criteria.createAlias("ESP." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "PRE");
		criteria.createAlias("PRE." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "RSE");
		criteria.add(Restrictions.eq("ESP."+ AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		if(matricula != null && vinCodigo != null) {
			criteria.add(Restrictions.eq("RSE."+ RapServidores.Fields.MATRICULA.toString(), matricula));
			criteria.add(Restrictions.eq("RSE."+ RapServidores.Fields.VIN_CODIGO.toString(), vinCodigo));
		}
		return executeCriteria(criteria);
	}	
	
	public List<AghEquipes> pesquisarEquipesPorEspecialidadeServidores(Object objPesquisa, AghEspecialidades especialidade,
			DominioSituacao situacao) {

		DetachedCriteria criteriaServidorEquipe = obterCriteriaProfEspecialidade("servidor_equipe", especialidade);
		DetachedCriteria criteriaProfissionalResponsavel = obterCriteriaProfEspecialidade("profissional_responsavel", especialidade);

		// Consulta todas as equipes de uma especialidade e separa todos os
		// profissionais
		DetachedCriteria criteriaAghEquipes = DetachedCriteria.forClass(AghEquipes.class, "eqp");

		// ALIAS PARA UTILIZAR NAS DUAS QUERYS ACIMA
		criteriaAghEquipes.createAlias(AghEquipes.Fields.RAP_SERVIDORES.toString(), "servidor_equipe", Criteria.LEFT_JOIN);
		criteriaAghEquipes.createAlias(AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "profissional_responsavel",
				Criteria.LEFT_JOIN);

		// OU ENTRE AS DUAS QUERYS ACIMA.
		criteriaAghEquipes.add(Restrictions.or(Subqueries.exists(criteriaServidorEquipe),
				Subqueries.exists(criteriaProfissionalResponsavel)));

		// Considera os parâmetros informados para pesquisa
		String srtPesquisa = (String) objPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteriaAghEquipes.add(Restrictions.eq(AghEquipes.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteriaAghEquipes.add(Restrictions.ilike(AghEquipes.Fields.NOME.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}
		if (situacao != null) {
			criteriaAghEquipes.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(), situacao));
		}

		// Ordena profissionais por nome
		criteriaAghEquipes.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));

		// Realiza uma distinção no resultados da consulta de equipes
		criteriaAghEquipes.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);

		return executeCriteria(criteriaAghEquipes);
	}

	public List<AghEspecialidades> listarEspecialidadesPorSiglaUsandoLike(String strPesquisa, Integer maxResults) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), strPesquisa, MatchMode.EXACT));
		}
		return executeCriteria(criteria, 0, maxResults, null, false);
	}

	public AghEspecialidades obterEspecialidadePorServidor(Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);

		criteria.createAlias(AghEspecialidades.Fields.CENTRO_CUSTO.toString(), AghEspecialidades.Fields.CENTRO_CUSTO.toString());
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SER_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SER_VIN_CODIGO.toString(), vinCodigo));

		List<AghEspecialidades> result = this.executeCriteria(criteria);

		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public AghEspecialidades obterEspecialidadePorEquipe(Integer eqSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "esp");
		criteria.createAlias("esp." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "pre");
		criteria.createAlias("pre." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "rap");
		criteria.createAlias("rap." + RapServidores.Fields.EQUIPES.toString(), "eqp");

		if (eqSeq != null) {
			criteria.add(Restrictions.eq("eqp." + AghEquipes.Fields.SEQ.toString(), eqSeq));
		}

		criteria.add(Restrictions.or(Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true),
				Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(), DominioSimNao.S)));

		criteria.add(Restrictions.isNull(AghEspecialidades.Fields.ESP_SEQ.toString()));

		List<AghEspecialidades> result = this.executeCriteria(criteria);

		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public List<AghEspecialidades> obterEspecialidadePorProfissionalAmbInt(String param, Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = obterCriteriaEspPorProfAmbInt(param, matricula, vinCodigo, false);
		criteria.addOrder(Order.asc("esp." + AghEspecialidades.Fields.NOME.toString()));
		return this.executeCriteria(criteria);
	}
	
	public Long obterEspecialidadePorProfissionalAmbIntCount(String param, Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = obterCriteriaEspPorProfAmbInt(param, matricula, vinCodigo, false);
		return this.executeCriteriaCount(criteria);
	}
	
	public List<AghEspecialidades> obterEspecialidadeProfCirurgiaoPorServidor(Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = obterCriteriaEspPorProfAmbInt(null, matricula, vinCodigo, true);
		return this.executeCriteria(criteria);
	}
	
	
	public Map<String, String> obterMapEspecialidadeProfCirurgiaoPorServidor(List<Integer> listmatricula, List<Short> listvinCodigo) {
		Map<String, String> returnMap = new HashMap<String, String>();
		
		List<Object[]> lista = obterEspecialidadeProfCirurgiaoPorServidor(listmatricula, listvinCodigo);
		for (Object[] objects : lista) {
			String sigla = (String)objects[1];
			Integer matricula = (Integer)objects[2];
			Short vinculo = (Short)objects[3];
			
			String especialidadeConcat = null;
			String key = matricula + "-" + vinculo;
			if (returnMap.containsKey(key)) {
				especialidadeConcat = returnMap.get(key);
				especialidadeConcat = especialidadeConcat.concat("/").concat(sigla);
			} else {
				especialidadeConcat = sigla;
			}
			returnMap.put(key, especialidadeConcat);
			
		}
		
		return returnMap;
	}
	
	private List<Object[]> obterEspecialidadeProfCirurgiaoPorServidor(List<Integer> listmatricula, List<Short> listvinCodigo) {
		DetachedCriteria criteria = obterCriteriaEspPorProfAmbInt(listmatricula, listvinCodigo);		
		return this.executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaEspPorProfAmbInt(List<Integer> listmatricula, List<Short> listvinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "esp");
		criteria.createAlias("esp." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "pre");
		
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("esp."+AghEspecialidades.Fields.SEQ.toString()), AghEspecialidades.Fields.SEQ.toString())
			.add(Projections.property("esp."+AghEspecialidades.Fields.SIGLA.toString()), AghEspecialidades.Fields.NOME.toString())
			.add(Projections.property("pre." + AghProfEspecialidades.Fields.SER_MATRICULA.toString()), AghEspecialidades.Fields.SEQ.toString())
			.add(Projections.property("pre." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString()), AghEspecialidades.Fields.SEQ.toString())
		);
		
		criteria.add(Restrictions.in("pre." + AghProfEspecialidades.Fields.SER_MATRICULA.toString(), listmatricula));
		criteria.add(Restrictions.in("pre." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), listvinCodigo));
		
		criteria.add(Restrictions.eq("pre."+ AghProfEspecialidades.Fields.IND_CIRURGIAO_BLOCO.toString(), DominioSimNao.S));
		
		return criteria;
	}


	private DetachedCriteria obterCriteriaEspPorProfAmbInt(String param,
			Integer matricula, Short vinCodigo, boolean apenasProfCirurgiaoBloco) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "esp");
		criteria.createAlias("esp." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "pre");
					
		if(StringUtils.isNotBlank(param)) {
			Criterion cNome = Restrictions.or(Restrictions.ilike(
					AghEspecialidades.Fields.NOME.toString(), param,
					MatchMode.ANYWHERE), Restrictions.ilike(
					AghEspecialidades.Fields.SIGLA.toString(), param,
					MatchMode.ANYWHERE));
			if(CoreUtil.isNumeroShort(param)) {
				criteria.add(Restrictions.or(cNome, Restrictions.eq(
						AghEspecialidades.Fields.SEQ.toString(), Short
								.valueOf(param))));
			} else {
				criteria.add(cNome);
			}
		}
		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.SER_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), vinCodigo));
		if (apenasProfCirurgiaoBloco) {
			criteria.add(Restrictions.eq("pre."+ AghProfEspecialidades.Fields.IND_CIRURGIAO_BLOCO.toString(), DominioSimNao.S));
		} else {
			criteria.add(Restrictions.or(Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true),
					Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(), DominioSimNao.S)));
			criteria.add(Restrictions.isNull("esp."+ AghEspecialidades.Fields.ESP_SEQ.toString()));
			criteria.add(Restrictions.eq("esp."+ AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		}
		return criteria;
	}

	public List<AghEspecialidades> pesquisarEspPorNomeOuSiglaListaSeq(List<Short> listEspId, String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		if (listEspId != null && !listEspId.isEmpty()) {
			criteria.add(Restrictions.in(AghEspecialidades.Fields.SEQ.toString(), listEspId));
		}
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		return this.executeCriteria(criteria, 0, 100, AghEspecialidades.Fields.NOME.toString(), true);
	}
	
	public Long pesquisarEspPorNomeOuSiglaListaSeqCount(List<Short> listEspId, String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		if (listEspId != null && !listEspId.isEmpty()) {
			criteria.add(Restrictions.in(AghEspecialidades.Fields.SEQ.toString(), listEspId));
		}
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Monta criteria de especialidades ativas
	 * 
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivas() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	/**
	 * Monta criteria de especialidades ativas por seqs
	 * 
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivasPorSeqs(List<Short> listSeqs) {
		DetachedCriteria criteria = this.montarCriteriaAtivas();
		criteria.add(Restrictions.in(AghEspecialidades.Fields.SEQ.toString(), listSeqs));
		return criteria;
	}
	
	/**
	 * Retorna as especialidades ativas de acordo com os seqs.
	 * 
	 * @param listSeqs
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs) {
		return this.pesquisarEspecialidadesAtivasPorSeqs(listSeqs, null, null, null, true);
	}
	
	/**
	 * Retorna as especialidades ativas de acordo com os seqs.
	 * 
	 * @param listSeqs
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.montarCriteriaAtivasPorSeqs(listSeqs);
		if(firstResult != null && maxResults != null){
			return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);	
		}
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retorna as especialidades ativas de acordo com o param e seqs especificados.
	 * 
	 * @param listSeqs
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqs(String param, List<Short> listSeqs) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(param);
		criteria.add(Restrictions.in(AghEspecialidades.Fields.SEQ.toString(), listSeqs));	
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retorna o número de especialidades ativas de acordo com o param e seqs especificados.
	 * 
	 * @param listSeqs
	 * @return
	 */
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqsCount(String param, List<Short> listSeqs) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(param);
		criteria.add(Restrictions.in(AghEspecialidades.Fields.SEQ.toString(), listSeqs));	
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna o número de especialidades ativas de acordo com os seqs
	 * 
	 * @param listSeqs
	 * @return
	 */
	public Long pesquisarEspecialidadesAtivasPorSeqsCount(List<Short> listSeqs) {
		DetachedCriteria criteria = this.montarCriteriaAtivasPorSeqs(listSeqs);
		return this.executeCriteriaCount(criteria);
	}
		
	/**
	 * Retorna as especialidades ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 */
	@Deprecated
	/**
	 * Utilizar pesquisarEspecialidadePorNomeOuSiglaAtivo, pois este método não pesquisa primeiramente por sigla e depois por descrição. Ele faz um "or"
	 * fazendo com que o padrão AGHU não seja respeitado. Se for pesquisado "CAR" deverá trazer somente Cardiologia, e não CARDIO.
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param, Integer maxResults) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuSigla(param);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		
		if (maxResults != null) {
			return this.executeCriteria(criteria, 0, maxResults, null, true);
		}
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retorna o número de especialidades ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 */
	@Deprecated
	/**
	 * Utilizar pesquisarEspecialidadePorNomeOuSiglaAtivoCount, pois este método não pesquisa primeiramente por sigla e depois por descrição. Ele faz um "or"
	 * fazendo com que o padrão AGHU não seja respeitado. Se for pesquisado "CAR" deverá trazer somente Cardiologia, e não CARDIO.
	 */
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param){
		DetachedCriteria criteria = montarCriteriaParaNomeOuSigla(param);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteriaCount(criteria);
	}
	
	public String pesquisarNomeEspecialidadePorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.setProjection(Projections.property(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), seq));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadeLaudoAih(Object pesquisa, Integer conNumero, Integer idadePaciente) {
		
		final String nomeOuSigla = (String) pesquisa;
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");
		criteria.createAlias("ESP." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "PRE");
		criteria.createAlias("PRE." + AghProfEspecialidades.Fields.PROFISSIONAIS_ESP_CONVENIO.toString(), "PEC");
		criteria.createAlias("PRE." + AghProfEspecialidades.Fields.ESCALAS_PROFISSIONAIS_INT.toString(), "EPI");
		
		if (StringUtils.isNotBlank(nomeOuSigla)) {
			Criterion lhs = Restrictions.ilike("ESP." + AghEspecialidades.Fields.SIGLA.toString(),nomeOuSigla, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("ESP."+AghEspecialidades.Fields.SIGLA.toString())),AghEspecialidades.Fields.SIGLA.toString())
				.add(Projections.property("ESP."+AghEspecialidades.Fields.NOME.toString()),AghEspecialidades.Fields.NOME.toString())
				.add(Projections.property("ESP."+AghEspecialidades.Fields.SEQ.toString()),AghEspecialidades.Fields.SEQ.toString())
		  );
		
		//SUBQUERY VAinServInterna
		final DetachedCriteria subQueryVSC = DetachedCriteria.forClass(VAinServInterna.class, "VSC");
		subQueryVSC.setProjection(Projections.property("VSC." + VAinServInterna.Fields.MATRICULA.toString()));
		subQueryVSC.setProjection(Projections.property("VSC." + VAinServInterna.Fields.VIN_CODIGO.toString()));
		
		subQueryVSC.add(Property.forName("VSC." + VAinServInterna.Fields.MATRICULA.toString()).eqProperty("PRE." + AghProfEspecialidades.Fields.SER_MATRICULA.toString()));
		subQueryVSC.add(Property.forName("VSC." + VAinServInterna.Fields.VIN_CODIGO.toString()).eqProperty("PRE." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString()));
		
		criteria.add(Subqueries.exists(subQueryVSC));
		
		//SUBQUERY FatConvenioSaude
		final DetachedCriteria subQueryCNV = DetachedCriteria.forClass(FatConvenioSaude.class, "CNV");
		subQueryCNV.setProjection(Projections.property("CNV." + FatConvenioSaude.Fields.CODIGO.toString()));
		subQueryCNV.add(Property.forName("CNV." + FatConvenioSaude.Fields.CODIGO.toString()).eqProperty("PEC." + AghProfissionaisEspConvenio.Fields.CONVENIO.toString()));
		subQueryCNV.add(Restrictions.eq("CNV." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		criteria.add(Subqueries.exists(subQueryCNV));
		
		//SUBQUERY AghEspecialidades
		final DetachedCriteria subQueryEsp = DetachedCriteria.forClass(AghEspecialidades.class, "ESM");
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append(" COALESCE({alias}.ESP_SEQ, {alias}.SEQ) as ESP_SEQ");
		subQueryEsp.setProjection(Projections.sqlProjection(sqlProjection.toString(), 
				new String[]{AghEspecialidades.Fields.ESP_SEQ.toString()}, 
				new Type[] { StringType.INSTANCE }));
		subQueryEsp.add(Restrictions.sqlRestriction(" COALESCE({alias}.ESP_SEQ, {alias}.SEQ) = this_.SEQ"));
		
		//SUBQUERY AacGradeAgendamenConsultas
		final DetachedCriteria subQueryConsultas = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "GRD");
		subQueryConsultas.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "CON");
		subQueryConsultas.setProjection(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));
		subQueryConsultas.add(Property.forName("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString())
				.eqProperty("ESM." + AghEspecialidades.Fields.SEQ.toString()));
		subQueryConsultas.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		
		criteria.add(Restrictions.eq("PRE." + AghProfEspecialidades.Fields.IND_INTERNA.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq("PEC." + AghProfissionaisEspConvenio.Fields.IND_INTERNA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.in("ESP." + AghEspecialidades.Fields.INDINTERNA.toString(), 
				new Object[] { DominioEspecialidadeInterna.I, DominioEspecialidadeInterna.U }));
		criteria.add(Property.forName("EPI." + AinEscalasProfissionalInt.Fields.PROF_ESPECIALIDADE_CONVENIO.toString()
				.concat("."+AghProfissionaisEspConvenio.Fields.CONVENIO.toString())).eqProperty("PEC." + AghProfissionaisEspConvenio.Fields.CONVENIO.toString()));
		
		criteria.add(Restrictions.and(Restrictions.le("EPI." + AinEscalasProfissionalInt.Fields.DATA_INICIO.toString(), new Date()), 
				Restrictions.or(Restrictions.isNull("EPI." + AinEscalasProfissionalInt.Fields.DATA_FIM.toString()), 
						Restrictions.ge("EPI." + AinEscalasProfissionalInt.Fields.DATA_FIM.toString(), DateUtil.truncaData(new Date())))));
		
		criteria.add(Restrictions.and(Restrictions.le("ESP." + AghEspecialidades.Fields.IDADE_MIN_PAC_INTERNACAO.toString(), idadePaciente.shortValue()), 
				Restrictions.ge("ESP." + AghEspecialidades.Fields.IDADE_MAX_PAC_INTERNACAO.toString(), idadePaciente.shortValue())));
		
		if (conNumero != null && conNumero > 0) {
			subQueryEsp.add(Subqueries.exists(subQueryConsultas));
			criteria.add(Subqueries.exists(subQueryEsp));
		}
		
		
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.NOME.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		
		return this.executeCriteria(criteria, 0, 100, null, false);
	}
	
	/** GET **/
	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}

	
	//#31988 - C_SB_01
	public List<AghEspecialidades> listarEspecialidadesAtivasPorNomeOuSigla(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SEQ.toString()));
		return this.executeCriteria(criteria, 0, 100, null, true);
	}

	//#31988 - C_SB_01
	public Long listarEspecialidadesAtivasPorNomeOuSiglaCount(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteriaCount(criteria);
	}
	
	public AghEspecialidades buscarEspecialidadePorConNumero(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.createAlias(AghEspecialidades.Fields.ATENDIMENTOS.toString(), "atd");
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.CON_NUMERO.toString(), conNumero));
		return (AghEspecialidades) executeCriteriaUniqueResult(criteria);
	}
	
	//#2280 C2
	public List<AghEspecialidades> pesquisarPorNomeOuSiglaOrderByNomeReduzido(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(parametro);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_REDUZIDO.toString()));
		return this.executeCriteria(criteria, 0, 100, AghEspecialidades.Fields.NOME_REDUZIDO.toString(), true);
	}

	public Long pesquisarPorNomeOuSiglaOrderByNomeReduzidoCount(String param) {
		DetachedCriteria criteria = this.montarCriteriaParaNomeOuSigla(param);
		return executeCriteriaCount(criteria);
	}
	
	
	public List<AghEspecialidades> pesquisarPorSiglaOuNomeEspecialidade(Object parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaSiglaOuNomeEspecialidade(parametro);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		return this.executeCriteria(criteria, 0, 100, null);
	}

	public List<AghEspecialidades> pesquisarPorSiglaOuNomeGestaoInterconsultas(Object parametro) {
		String nomeOuSigla = StringUtils.trimToNull(parametro.toString());
		List <AghEspecialidades> lista = null;
		if (nomeOuSigla != null){
			DetachedCriteria criteria = montarCriteriaSiglaEspecialidade(nomeOuSigla);
			lista = this.executeCriteria(criteria);			
		}
		if (lista == null || lista.isEmpty()){
			DetachedCriteria criteriaNome = montarCriteriaNomeEspecialidade(nomeOuSigla);
			criteriaNome.addOrder(Order.asc(AghEspecialidades.Fields.SIGLA.toString()));
			criteriaNome.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
			lista = this.executeCriteria(criteriaNome, 0, 100, null);
		}
		return lista;
	}
	
	private DetachedCriteria montarCriteriaSiglaEspecialidade(String nomeOuSigla){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);	
		criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), nomeOuSigla, MatchMode.EXACT));
		return criteria;
	}
	
	private DetachedCriteria montarCriteriaNomeEspecialidade(String nomeOuSigla){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);	
		if (nomeOuSigla != null){
			criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), nomeOuSigla, MatchMode.ANYWHERE));			
		}
		return criteria;
	}
	
	public Long pesquisarPorSiglaOuNomeGestaoInterconsultasCount(Object parametro) {
		String nomeOuSigla = StringUtils.trimToNull(parametro.toString());
		Long count = null;
		if (nomeOuSigla != null){
			DetachedCriteria criteria = montarCriteriaSiglaEspecialidade(nomeOuSigla);
			count = this.executeCriteriaCount(criteria);		
		}
		if (count == null || count == 0){
			DetachedCriteria criteriaNome = montarCriteriaNomeEspecialidade(nomeOuSigla);
			count = this.executeCriteriaCount(criteriaNome);
		}
		return count;
	}
	

	public Long pesquisarPorSiglaOuNomeEspecialidadeCount(Object parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaSiglaOuNomeEspecialidade(parametro);
		return this.executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria montarCriteriaParaSiglaOuNomeEspecialidade(Object parametro) {
		String nomeOuSigla = StringUtils.trimToNull(parametro.toString());
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");	
		criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(AacConsultas.class, "ACO");
		
		subQuery.setProjection(Projections.property("ES2." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));

		subQuery.createAlias("ACO." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GAC", JoinType.INNER_JOIN);
		subQuery.createAlias("GAC." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ES2", JoinType.INNER_JOIN);
		
		subQuery.add(Restrictions.eqProperty("ES2." + AghEspecialidades.Fields.SEQ.toString(), "ESP." + AghEspecialidades.Fields.SEQ.toString()));
		
		DetachedCriteria subQueryParam = DetachedCriteria.forClass(AghParametros.class, "AGP");
		subQueryParam.setProjection(Projections.property("AGP." + AghParametros.Fields.VLR_NUMERICO.toString()));
		subQueryParam.add(Property.forName("AGP." + AghParametros.Fields.NOME.toString()).eq("P_CAA_INTERCONSULTA"));
		
		subQuery.add(Property.forName("ACO." + AacConsultas.Fields.FAG_CAA_SEQ.toString()).eq(subQueryParam));
		
		subQuery.add(Property.forName("ACO." + AacConsultas.Fields.DATA_CONSULTA).gt(new Date())); 
		subQuery.add(Property.forName("ACO." + AacConsultas.Fields.SITUACAO_CONSULTA.toString() + "." + 
				AacSituacaoConsultas.Fields.SITUACAO.toString()).ne(DominioSituacaoInterconsultas.M.toString()));
		
		criteria.add(Subqueries.exists(subQuery));
		
		if (StringUtils.isNotEmpty(nomeOuSigla)) {
			if (CoreUtil.isNumeroShort(nomeOuSigla)) {
				criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.parseShort(nomeOuSigla)));
			} else {
				Criterion lhs = Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), nomeOuSigla, MatchMode.EXACT);
				Criterion rhs = Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), nomeOuSigla, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(lhs, rhs));
			}
		}
		return criteria;
	}
	
	/**
	 * Criado novo método que retorna um VO e faz filtro somente por servidor
	 * @param servidor
	 * @return
	 */
	public List<AghEspecialidadeVO> pesquisarAghEspecialidadePorServidor(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "esp");
		criteria.createAlias("esp." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "pre");
		criteria.createAlias("pre." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "rap");
		criteria.createAlias("rap." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("pre." + AghProfEspecialidades.Fields.IND_PROF_REALIZA_CONSULTORIA.toString()), AghEspecialidadeVO.Fields.IND_PROF_REALIZA_CONSULTORIA.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.SIGLA.toString()), AghEspecialidadeVO.Fields.SIGLA.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), AghEspecialidadeVO.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property("esp." + AghEspecialidades.Fields.SEQ.toString()), AghEspecialidadeVO.Fields.SEQ.toString())
				);
		
		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.SER_MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
	    
		criteria.addOrder(Order.asc("esp." + AghEspecialidades.Fields.SIGLA.toString()));
		
    	criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidadeVO.class));
	    
    	return executeCriteria(criteria);
	}
	
	public List<AghEspecialidadeVO> pesquisarEspecialidadesConsultoria(final String pesquisa, final Boolean indPesquisaConsultoriaAmbulatorio, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		List<AghEspecialidadeVO> lista = null;
		if (pesquisa != null) {
			DetachedCriteria criteria = montarCriteriaEspecialidadesConsultoria(pesquisa, true, indPesquisaConsultoriaAmbulatorio);
			criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
			lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		if (lista == null || lista.isEmpty()) {
			DetachedCriteria criteria = montarCriteriaEspecialidadesConsultoria(pesquisa, false, indPesquisaConsultoriaAmbulatorio);
			criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
			lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		return lista; 
	}
	
	public Long pesquisarEspecialidadesConsultoriaCount(final String pesquisa, final Boolean indPesquisaConsultoriaAmbulatorio) {
		Long retorno = null;
		if (pesquisa != null) {
			DetachedCriteria criteria = montarCriteriaEspecialidadesConsultoria(pesquisa, true, indPesquisaConsultoriaAmbulatorio);
			retorno = executeCriteriaCount(criteria);
		}
		if (retorno == null || retorno.longValue() == 0) {
			DetachedCriteria criteria = montarCriteriaEspecialidadesConsultoria(pesquisa, false, indPesquisaConsultoriaAmbulatorio);
			retorno = executeCriteriaCount(criteria);
		}
		return retorno;
	}
	
	private DetachedCriteria montarCriteriaEspecialidadesConsultoria(final String pesquisa, boolean isSigla, final Boolean indPesquisaConsultoriaAmbulatorio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghEspecialidades.Fields.SIGLA.toString()), AghEspecialidadeVO.Fields.SIGLA.toString())
				.add(Projections.property(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), AghEspecialidadeVO.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property(AghEspecialidades.Fields.SEQ.toString()), AghEspecialidadeVO.Fields.SEQ.toString())
				);
		
		if (pesquisa != null && !pesquisa.isEmpty()) {
			if (isSigla) {
				criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), pesquisa, MatchMode.EXACT));
			} else {
				criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), pesquisa, MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		
		if (indPesquisaConsultoriaAmbulatorio) {
			if(isOracle()) {	
				criteria.add(Restrictions.sqlRestriction("seq in (select esp_seq from agh.mam_consultoria_amb_permissoes where  dt_fim is null or dt_fim > sysdate )"));
			} else {
				criteria.add(Restrictions.sqlRestriction("seq in (select esp_seq from agh.mam_consultoria_amb_permissoes where  dt_fim is null or dt_fim > now() )"));
			}
		} else {
			criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDCONSULTORIA.toString(), DominioSimNao.S));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidadeVO.class));
		
		return criteria;
	}

	/**
	 * 
	 * @param esp_seq
	 * @return
	 * Estoria: 40229:  Consulta c17
	 */
	public List<AghEspecialidades> obterNomeEspecialidadePorEspSeq(Short esp_seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		
		criteria.setProjection(Projections.property(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.ESP_SEQ.toString(), esp_seq));
		
		return executeCriteria(criteria);
	}	
	
	private DetachedCriteria obterCriteriaPesquisarSelecaoEspecialidades(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.INDCONSULTORIA.toString(), DominioSimNao.S));
		if (StringUtils.isNotBlank(parametro)) {
			if (parametro.length() == 3) {
				criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), parametro, MatchMode.EXACT));
			} else {
				criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	public List<AghEspecialidades> pesquisarSelecaoEspecialidades(String parametro) {
		DetachedCriteria criteria = obterCriteriaPesquisarSelecaoEspecialidades(parametro);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisarSelecaoEspecialidadesCount(String parametro) {
		DetachedCriteria criteria = obterCriteriaPesquisarSelecaoEspecialidades(parametro);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * 42011
	 * Listar Especialidades pelo SEQ onde Imprime Agenda está 'S'
	 * @param seq
	 * @return
	 */
	public Short listarEspecialidades(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.IND_IMPRIME_AGENDA.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), seq));
		AghEspecialidades result = (AghEspecialidades) executeCriteriaUniqueResult(criteria); 
		if (result == null) {
			return null;
		} else {
			if (result.getEspecialidade() != null) {
				return result.getEspecialidade().getSeq();
			} else {
				return result.getSeq();
			}
		}
	}
	
	/**#45341 C1 - Obtem a sigla e o nome da especialidade pela sequencia da especialidade**/
	public AghEspecialidades obterSiglaNomeEspecialidadePorEspSeq(Short pEspSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghEspecialidades.Fields.SIGLA.toString()).as(AghEspecialidades.Fields.SIGLA.toString()))
				.add(Projections.property(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()).as(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString())));
		
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), pEspSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));		
		return (AghEspecialidades)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * C2 - 6807
	 * @param pesquisa
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<AghEspecialidadeVO> pesquisarEspecialidadesPorSiglaNomeCodigo(String pesquisa) throws ApplicationBusinessException{
		DetachedCriteria criteria = filtroPesquisarEspecialidadesPorSiglaNomeCodigo(pesquisa);
		criteria.addOrder(Order.asc(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}	
	
	/**
	 * #6807
	 * C2
	 * @param pesquisa
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public Long pesquisarEspecialidadesPorSiglaNomeCodigoCount(String pesquisa) throws ApplicationBusinessException{
		DetachedCriteria criteria = filtroPesquisarEspecialidadesPorSiglaNomeCodigo(pesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria filtroPesquisarEspecialidadesPorSiglaNomeCodigo(
			String pesquisa) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghEspecialidades.Fields.SEQ.toString()),AghEspecialidadeVO.Fields.SEQ.toString())
				.add(Projections.property(AghEspecialidades.Fields.SIGLA.toString()),AghEspecialidadeVO.Fields.SIGLA.toString())
				.add(Projections.property(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()),AghEspecialidadeVO.Fields.NOME_ESPECIALIDADE.toString())
				);
		if(StringUtils.isNotBlank(pesquisa)){
			if(CoreUtil.isNumeroShort(pesquisa)){
				criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), Short.valueOf(pesquisa)));
			}
			else{
				criteria.add(Restrictions.or(
						Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), pesquisa,MatchMode.ANYWHERE),
						Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), pesquisa,MatchMode.ANYWHERE))
						);
			}
		}
		
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(
					AghEspecialidadeVO.class.getConstructor(Short.class, String.class, String.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		
		return criteria;
	}
	
	
	/**
	 * @ORADB P_CHAMA_ATESTADOS - CURSOR c_esp
	 */
	public List<ParametrosAghEspecialidadesAtestadoVO> obterParametroProcedure(Integer codConsulta){
		AghEspecialidadesParametroVOQueryBuilder builder = new AghEspecialidadesParametroVOQueryBuilder();
		DetachedCriteria criteria = builder.build(codConsulta);
		
		if (criteria == null) {
            return null;
		}
		
		return executeCriteria(criteria);
	}
	
	public AghEspecialidades obterEspecialidadePorNumeroConsulta(Integer conNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class,"ESP");
		criteria.createAlias("ESP." + AghEspecialidades.Fields.SERVIDOR.toString(), "SRV",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ESP." + AghEspecialidades.Fields.GRADE_AGENDAMENTO.toString(), "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), "AAC");
		criteria.createAlias("ESP." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "CEN",JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("AAC."+AacConsultas.Fields.NUMERO.toString(), conNumero));
		return (AghEspecialidades) executeCriteriaUniqueResult(criteria);
	}
	
	
	private DetachedCriteria createPesquisaCriteriaInterconsulta(String pesquisa, AacConsultas consulta, Integer idadePaciente) {
		
		Date data =  DateUtil.truncaData(new Date());
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(AacConsultas.class, "ACO");
		subQuery.setProjection(Projections.projectionList()
				.add(Projections.property("ACO."+ AacConsultas.Fields.NUMERO.toString()).as(AacConsultas.Fields.NUMERO.toString()))
				);
		subQuery.createAlias("ACO." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GAC", JoinType.INNER_JOIN);
		subQuery.createAlias("GAC." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP2", JoinType.LEFT_OUTER_JOIN);
		subQuery.add(Restrictions.eqProperty("ESP2." + AghEspecialidades.Fields.SEQ.toString(),"ESP." + AghEspecialidades.Fields.SEQ.toString()));
		subQuery.add(Restrictions.eq("ACO." + AacConsultas.Fields.CAA_SEQ.toString(), consulta.getCaaSeq()));
		
		if(isOracle()){
			subQuery.add(Restrictions.sqlRestriction("SUBSTR({alias}.IND_SIT_CONSULTA, 1, 1) <> 'M'"));
		}else{
			subQuery.add(Restrictions.sqlRestriction("SUBSTRING({alias}.IND_SIT_CONSULTA, 1, 1) <> 'M'"));
		}
		
		subQuery.add(Restrictions.sqlRestriction("COALESCE({alias}.IND_EXCEDE_PROGRAMACAO, 'N') = 'N'"));
		subQuery.add(Restrictions.gt("ACO." + AacConsultas.Fields.DATA_CONSULTA.toString(), data));
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");
			
		criteria.add(Restrictions.eq("ESP."+ AghEspecialidades.Fields.INDSITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.not(Restrictions.in("ESP."+ AghEspecialidades.Fields.SIGLA.toString(), new Object[] {"UAD", "UCI", "UPE", "UGI", "UOB", "PMI", "PAP"})));
		criteria.add(Subqueries.exists(subQuery));
		if(idadePaciente != null){
			criteria.add(Restrictions.sqlRestriction(idadePaciente +" between {alias}.IDADE_MIN_PAC_INTERNACAO and {alias}.IDADE_MAX_PAC_INTERNACAO"));
		}
		
			if (StringUtils.isNotBlank(pesquisa)) {
				if (CoreUtil.isNumeroInteger(pesquisa)) {
					criteria.add(Restrictions.eq("ESP."+ AghEspecialidades.Fields.SEQ.toString(),Short.valueOf(pesquisa)));
				} else {
					criteria.add(Restrictions.or(
							Restrictions.ilike("ESP."+ AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), pesquisa, MatchMode.ANYWHERE),
							Restrictions.ilike("ESP."+ AghEspecialidades.Fields.SIGLA.toString(), pesquisa, MatchMode.ANYWHERE)
						));
					}
				}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ESP."+ AghEspecialidades.Fields.SEQ.toString()).as(AghEspecialidades.Fields.SEQ.toString()))
				.add(Projections.property("ESP."+ AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()).as(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()))
				.add(Projections.property("ESP."+ AghEspecialidades.Fields.SIGLA.toString()).as(AghEspecialidades.Fields.SIGLA.toString()))
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		
		return criteria;
	}
	
	public List<AghEspecialidades> pesquisarAgendaInterconsulta(String pesquisa, AacConsultas consulta, Integer idadePaciente) {
		DetachedCriteria criteria = createPesquisaCriteriaInterconsulta(pesquisa, consulta, idadePaciente);
		criteria.addOrder(Order.asc("ESP."+ AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		return executeCriteria(criteria, 0, 100, null, false);

	}
	public Long pesquisarAgendaInterconsultaCount(String pesquisa, AacConsultas consulta, Integer idadePaciente) {
		DetachedCriteria criteria = createPesquisaCriteriaInterconsulta(pesquisa, consulta, idadePaciente);
		return executeCriteriaCount(criteria);
		
	}
	
	public AghEspecialidadeVO obterEspecialidadePorEspSeq(Short espSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class,"ESP");

		criteria.createAlias("ESP." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), "CEN",JoinType.LEFT_OUTER_JOIN);
	
		criteria.add(Restrictions.eq("ESP."+AghEspecialidades.Fields.SEQ.toString(), espSeq));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ESP."+ AghEspecialidades.Fields.IND_IMP_SO_SERVICO.toString()), AghEspecialidadeVO.Fields.IND_IMP_SO_SERVICO.toString())
				.add(Projections.property("ESP."+ AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), AghEspecialidadeVO.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property("CEN."+ FccCentroCustos.Fields.CODIGO.toString()), AghEspecialidadeVO.Fields.CCT_CODIGO.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidadeVO.class));
		return (AghEspecialidadeVO) executeCriteriaUniqueResult(criteria);
	}

	//#49925
	public List<AghEspecialidades> obterEspecialidadesAtivas() {
		DetachedCriteria criteria = criarCriteriaEspecialidadesAtivas();
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghEspecialidades.Fields.SEQ.toString()), AghEspecialidades.Fields.SEQ.toString())
				.add(Projections.property(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property(AghEspecialidades.Fields.SIGLA.toString()), AghEspecialidades.Fields.SIGLA.toString())
				);

		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		
		return executeCriteria(criteria);
	}	
	//#51886 - CUR_ESP
	public DominioSituacao obterSituacaoEspecialidadePorSeq(Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), espSeq));
		criteria.setProjection(Projections.property(AghEspecialidades.Fields.INDSITUACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		return ((AghEspecialidades) executeCriteriaUniqueResult(criteria)).getIndSituacao();
	}
	
	/**
	 * #6810 SB1 List
	 * @param parametro
	 */
	public List<AghEspecialidades> obterEspecialidadesPorSiglaOUNomeEspecialidade(String parametro, boolean porSigla){
		DetachedCriteria criteria = obterCriteriaEspecialidadesPorSiglaOUNomeEspecialidade(parametro, porSigla);
		return executeCriteria(criteria, 0, 100, AghEspecialidades.Fields.SIGLA.toString(), true);
	}
	
	/**
	 * #6810  SB1 Count
	 * @param parametro
	 */
	public Long obterEspecialidadesPorSiglaOUNomeEspecialidadeCount(String parametro, boolean porSigla){
		DetachedCriteria criteria = obterCriteriaEspecialidadesPorSiglaOUNomeEspecialidade(parametro, porSigla);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #6810 SB1
	 * @param parametro
	 */
	private DetachedCriteria obterCriteriaEspecialidadesPorSiglaOUNomeEspecialidade(String parametro, boolean porSigla) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		if(parametro != null && !parametro.isEmpty()){
			if(porSigla){
				criteria.add(Restrictions.ilike(AghEspecialidades.Fields.SIGLA.toString(), parametro, MatchMode.EXACT));
			}else{
				criteria.add(Restrictions.ilike(AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	
	/**
	 * #52053
	 * @param espSeq
	 * @return DominioSituacao
	 */
	public DominioSituacao obterSituacaoEspecialidade(Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEspecialidades.class);
		criteria.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), espSeq));
		criteria.setProjection(Projections.property(AghEspecialidades.Fields.INDSITUACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		return ((AghEspecialidades) executeCriteriaUniqueResult(criteria)).getIndSituacao();
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadePorVbmcProfServidor(String pesquisa, Integer ser_matricula, Short ser_vin_codigo, Short ufseq){
		Query query = getHqlpesquisarEspecialidadePorVbmcProfServidorCount(pesquisa, ser_matricula,
				ser_vin_codigo, ufseq, false);
		List<AghEspecialidades> lista = query.list();
		return lista;
	}
	
	public Long pesquisarEspecialidadePorVbmcProfServidorCount(String pesquisa, Integer ser_matricula, Short ser_vin_codigo, Short ufseq){
		Query query = getHqlpesquisarEspecialidadePorVbmcProfServidorCount(pesquisa, ser_matricula,
				ser_vin_codigo, ufseq, true);
		return Long.parseLong(String.valueOf(query.list().size()));
	}

	/**
	 * @param ser_matricula
	 * @param ser_vin_codigo
	 * @return
	 */
	private Query getHqlpesquisarEspecialidadePorVbmcProfServidorCount(String pesquisa, Integer ser_matricula,
			Short ser_vin_codigo, Short ufseq, boolean count) {
		StringBuffer hql = new StringBuffer(522);
		hql.append(" select ")
				.append(" e.sigla as sigla, e.nomeEspecialidade as nomeEspecialidade, e.seq as seq")
				.append(" from ")
				.append(" VMbcProfServidor this , AghProfEspecialidades prof ,")
				.append(" AghEspecialidades e ")
				.append(" where ")
				.append(" this.id.serMatricula = prof.id.serMatricula")
				.append(" and this.id.serVinCodigo = prof.id.serVinCodigo ")
				.append(" and e.seq = prof.id.espSeq ")
				.append(" and this.id.unfSeq = :ufseq ")
				.append(" and this.indFuncaoProf in ('MPF', 'MCO') ")
				.append(" and this.situacao ='A' ")
				.append(" and this.id.serMatricula = :sermatricula ")
				.append(" and this.id.serVinCodigo = :servincodigo ");
		if(pesquisa != null && !StringUtils.isEmpty(pesquisa)){
			if(pesquisa.length() <= 3){
				hql.append(" and e.sigla like :pesquisa ");
			} else {
				hql.append(" and e.nomeEspecialidade like :pesquisa ");
			}
		}
		if (!count) {
			if (isOracle()) {
				hql.append(" and rownum < 101");
			} else {
				hql.append(" and limit 100");
			}
		}
		Query query = createHibernateQuery(hql.toString());
		query.setInteger("sermatricula", ser_matricula);
		query.setShort("servincodigo", ser_vin_codigo);
		query.setShort("ufseq", ufseq);
		if(pesquisa != null && !pesquisa.isEmpty()){
			query.setString("pesquisa","%" + pesquisa + "%");
		}
		query.setResultTransformer(Transformers
				.aliasToBean(AghEspecialidades.class));
		return query;
	}
	
}