package br.gov.mec.aghu.configuracao.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioPacientesUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioUnidFunc;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionais.Fields;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamEstadoPaciente;
import br.gov.mec.aghu.model.MamTipoEstadoPaciente;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AghAtendimentosVO;

@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.NcssTypeCount", "PMD.AvoidDuplicateLiteralsRule", "PMD.ConsecutiveLiteralAppendsRule"})
public class AghUnidadesFuncionaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghUnidadesFuncionais> {

	private static final long serialVersionUID = -231130858648107135L;
	
	private static final Log LOG = LogFactory.getLog(AghUnidadesFuncionaisDAO.class);
	
	private static final String SEPARADOR = ".";
	private static final String ALA = "ALA";
	
	public static final String ORDERNAR_POR_CODIGO_ALA_DESCRICAO = "ordernarPorCodigoAlaDescricao";
	public static final String ORDERNAR_POR_ANDAR = "ordernarPorAndar";

	public List<AghUnidadesFuncionais> pesquisarPorSequencialOuDescricao(final String parametro) {
		final DetachedCriteria criteria = montarCriteriaParaPesquisarPorSequencialOuDescricao(parametro, false);
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarPorSequencialOuDescricaoCount(final String parametro) {
		final DetachedCriteria criteria = montarCriteriaParaPesquisarPorSequencialOuDescricao(parametro, true);
		return executeCriteriaCount(criteria);
	}	
	
	public AghUnidadesFuncionais pesquisarUnidadeFuncionalPorSeq(Short unfSeq) {
		StringBuffer hql = new StringBuffer(300);
		hql.append("select unf from AghUnidadesFuncionais unf where unf.seq = :seq");
		Query query = (Query) createQuery(hql.toString());
		query.setParameter("seq", unfSeq);
		return (AghUnidadesFuncionais) query.uniqueResult();
	}

	
	public List<AghUnidadesFuncionais> pesquisarPorSequencialOuDescricaoOrderCodDesc(final String parametro) {
		final DetachedCriteria criteria = montarCriteriaParaPesquisarPorSequencialOuDescricao(parametro, true);
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	

	private DetachedCriteria montarCriteriaParaPesquisarPorSequencialOuDescricao(final String parametro, boolean count) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (StringUtils.isNotBlank(parametro)) {
			Criterion criterionDescCodigo = null;
			if (CoreUtil.isNumeroShort(parametro)) {
				final short codigo = Short.parseShort(parametro);
				criterionDescCodigo = Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo);
				Criterion descricao = Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(criterionDescCodigo,descricao));
			} else {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		if (!count){
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()));
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ALA.toString()));
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		}
		return criteria;
	}	

	/**
	 * Lista de Unidades Funcionais ativas e associadas às características Unid
	 * Emergencia Unid Internacao Unid Hosp Dia
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarPorDescricaoCodigoAtivaAssociada(final String parametro) {

		final DetachedCriteria criteria = montarCriteriaParaDescricaoOuCodigo(parametro);
		List<AghUnidadesFuncionais> result = null;
		DetachedCriteria caracteristicaCriteria = null;
	
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		caracteristicaCriteria = criteria.createCriteria(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
		caracteristicaCriteria.add(Restrictions.in(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), new Object[] {
				ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA,
				ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA }));
		result = montarAghUnidadesFuncionaisProjection(criteria);

		return result;
	}

	public String pegaHorarioDaUnidadeFuncional(final AghUnidadesFuncionais unidadeFuncional) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeFuncional.getSeq()));

		final AghUnidadesFuncionais unf = (AghUnidadesFuncionais) this.executeCriteriaUniqueResult(criteria);

		final Date hora = unf.getHrioValidadePme();

		return hora == null ? "" : DateFormatUtil.formataHoraMinuto(hora);
	}

	public List<AghUnidadesFuncionais> listarUnidadeFuncionalPorFuncionalSala(final Object objPesquisa, final DominioSituacao situacao) {
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(VAacSiglaUnfSala.class);
		subCriteria.setProjection(Projections.property(VAacSiglaUnfSala.Fields.UNF_SEQ.toString()));
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.add(Property.forName(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).in(subCriteria));
		final String srtPesquisa = (String) objPesquisa;

		if (situacao != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}

		if (StringUtils.isNotBlank(srtPesquisa)) {

			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));

		}
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SIGLA.toString()));
		return executeCriteria(criteria);
	}

	public List<VAacSiglaUnfSala> pesquisarSalasUnidadeFuncional(final Object objPesquisa, final AghUnidadesFuncionais unidadeFuncional,
			final DominioSituacao situacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(VAacSiglaUnfSala.class);
		final String srtPesquisa = (String) objPesquisa;

		if (StringUtils.isNotBlank(srtPesquisa) && CoreUtil.isNumeroByte(srtPesquisa)) {

			criteria.add(Restrictions.eq(VAacSiglaUnfSala.Fields.SALA.toString(), Byte.valueOf(srtPesquisa)));

		} else if (StringUtils.isNotBlank(srtPesquisa) && !CoreUtil.isNumeroByte(srtPesquisa)) {

			return null;

		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(VAacSiglaUnfSala.Fields.SIT_SALA.toString(), situacao));
		}
		criteria.add(Restrictions.eq(VAacSiglaUnfSala.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		criteria.addOrder(Order.asc(VAacSiglaUnfSala.Fields.SALA.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Lista todas as salas de uma unidade funcional
	 * 
	 * @param objPesquisa
	 * @param unidadeFuncional
	 * @return
	 */
	public List<VAacSiglaUnfSala> pesquisarSalasUnidadesFuncionais(final List<AghUnidadesFuncionais> unidadesFuncionais, DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(VAacSiglaUnfSala.class);
		if (!unidadesFuncionais.isEmpty()) {
			criteria.add(Restrictions.in(VAacSiglaUnfSala.Fields.UNIDADE_FUNCIONAL.toString(), unidadesFuncionais));
		}
		criteria.add(Restrictions.eq(VAacSiglaUnfSala.Fields.SIT_UND_FUNC.toString(), "A"));
		if(situacao != null) {
			criteria.add(Restrictions.eq(VAacSiglaUnfSala.Fields.SIT_SALA.toString(), situacao));
		}

		criteria.addOrder(Order.asc(VAacSiglaUnfSala.Fields.SIGLA.toString()));
		criteria.addOrder(Order.asc(VAacSiglaUnfSala.Fields.SALA.toString()));
		
		criteria.createAlias(VAacSiglaUnfSala.Fields.UNIDADE_FUNCIONAL.toString(), VAacSiglaUnfSala.Fields.UNIDADE_FUNCIONAL.toString());
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaParaDescricaoOuCodigo(final String parametro) {
		return montarCriteriaParaDescricaoSiglaOuCodigo(parametro, false);
	}

	private DetachedCriteria montarCriteriaParaDescricaoSiglaOuCodigo(String parametro, Boolean pesquisaSigla) {
		final String descricaoOuCodigo = StringUtils.trimToNull(parametro);
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())))
				.add(Projections.property(AghUnidadesFuncionais.Fields.SIGLA.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.ANDAR.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.ALA.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.DESCRICAO.toString())));
		
		if (StringUtils.isNotBlank(descricaoOuCodigo)) {
			String codigo = StringUtils.EMPTY;
			Criterion criterionDescCodigo = null;
			Criterion criterionAndar = null;
			if (CoreUtil.isNumeroShort(descricaoOuCodigo)) {
				codigo = descricaoOuCodigo;
			}
			if (StringUtils.isNotBlank(codigo)) {
				criterionDescCodigo = Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(codigo));
				criterionAndar = Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), codigo);
			} else {
				if (pesquisaSigla){
					criterionDescCodigo = Restrictions.or(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE), 
							Restrictions.ilike(AghUnidadesFuncionais.Fields.SIGLA.toString(), descricaoOuCodigo, MatchMode.ANYWHERE));
				}else{
					criterionDescCodigo = Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
				}
				
			}

			if (criterionAndar == null) {
				final String[] valoresAndarAla = StringUtils.split(descricaoOuCodigo);
				for (int i = 0; i < valoresAndarAla.length; i++) {
					final Criterion[] criterions = new Criterion[valoresAndarAla.length];
					String andar = StringUtils.EMPTY;
					if(CoreUtil.isNumeroShort(valoresAndarAla[i])&& Short.valueOf(valoresAndarAla[i])<= Byte.MAX_VALUE){
						andar = valoresAndarAla[i];	
					}
					if (StringUtils.isNotBlank(andar)) {
						criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), andar);
					} else {
						final AghAla ala = this.obterAghAlaPorId(valoresAndarAla[i]);
						if (ala != null) {
							criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.ALA.toString(), ala);
						}
					}
					if (criterions[i] == null) {
						criteria.add(criterionDescCodigo);
					} else {
						criterions[i] = Restrictions.or(criterionDescCodigo, criterions[i]);
						criteria.add(criterions[i]);
					}
				}
			} else {
				criteria.add(Restrictions.or(criterionDescCodigo, criterionAndar));
			}
		}

		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ALA.toString()));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return criteria;
	}

	private AghAla obterAghAlaPorId(final String codigo) {
		AghAla returnValue = null;

		if (StringUtils.isNotBlank(codigo)) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(AghAla.class);
			final Criterion codigoLowORup = Restrictions.or(Restrictions.eq(AghAla.Fields.CODIGO.toString(), codigo.toLowerCase()),
					Restrictions.eq(AghAla.Fields.CODIGO.toString(), codigo.toUpperCase()));
			criteria.add(codigoLowORup);

			final List<AghAla> list = super.executeCriteria(criteria);
			if (list != null && !list.isEmpty()) {
				returnValue = list.get(0);
			}
		}

		return returnValue;
	}

	private DetachedCriteria montarCriteriaParaDescricaoOuCodigoECaracteristicas(final String parametro, final List<Short> listaUnfSeq) {
		final String descricaoOuCodigo = StringUtils.trimToNull(parametro);
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (listaUnfSeq != null && !listaUnfSeq.isEmpty()) {
			criteria.add(Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), listaUnfSeq));
		}
		
		if (StringUtils.isNotBlank(descricaoOuCodigo)) {
			String codigo = StringUtils.EMPTY;
			Criterion criterionDescCodigo = null;
			Criterion criterionAndar = null;
			AghAla alaTemp = null;
			if (CoreUtil.isNumeroShort(descricaoOuCodigo)) {
				codigo = descricaoOuCodigo;
			}
			if (StringUtils.isNoneBlank(codigo)) {
				criterionDescCodigo = Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(codigo));
				criterionAndar = Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), codigo);
			} else {
				criterionDescCodigo = Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
			}
			if (criterionAndar == null) {
				final String[] valoresAndarAla = StringUtils.split(descricaoOuCodigo);
				for (int i = 0; i < valoresAndarAla.length; i++) {
					final Criterion[] criterions = new Criterion[valoresAndarAla.length];
					String andar = StringUtils.EMPTY;
					try {
						andar = valoresAndarAla[i];
					} catch (final Exception e) {
						LOG.warn(e.getMessage(), e);
					}
					if (StringUtils.isNoneBlank(andar)) {
						criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), andar);
					} else {
						alaTemp = obterAghAlaPorId(valoresAndarAla[i]);
						if (alaTemp != null && alaTemp.toString().equalsIgnoreCase(valoresAndarAla[i])) {
							criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.ALA.toString(), alaTemp);
						} else {
							// Se a ala for inválida adiciona uma condição que
							// invalida o filtro
							criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf("-1"));
						}
					}
					criterions[i] = Restrictions.or(criterionDescCodigo, criterions[i]);
					criteria.add(criterions[i]);
				}
			} else {
				criteria.add(Restrictions.or(criterionDescCodigo, criterionAndar));
			}
		}
		return criteria;
	}
	
	

	/**
	 */
	public List<AghUnidadesFuncionais> listarUnidadeFarmacia() {

		List<AghUnidadesFuncionais> result = null;
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "."
				+ AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA, ConstanteAghCaractUnidFuncionais.UNID_FARMACIA));
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		result = executeCriteria(criteria);

		return result;
	}

	// #5443
	public Long pesquisarCount(final AghUnidadesFuncionais elemento) {
		final DetachedCriteria criteria = criarCriteria(elemento, false);
		return executeCriteriaCount(criteria);
	}

	public AghUnidadesFuncionais obterPeloId(final Short codigo) {
		final AghUnidadesFuncionais elemento = new AghUnidadesFuncionais();
		elemento.setSeq(codigo);
		final DetachedCriteria criteria = criarCriteria(elemento, true);

		return (AghUnidadesFuncionais) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria criarCriteria(final AghUnidadesFuncionais elemento, final Boolean order) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "uf");
		// Popula criteria com dados do elemento
		if (elemento != null) {

			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
			criteria.createAlias("uf." + AghUnidadesFuncionais.Fields.ALA.toString(), ALA, JoinType.LEFT_OUTER_JOIN);
			
			final DetachedCriteria criteria2 = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "cuf");
			criteria2.add(Restrictions.eqProperty(criteria2.getAlias() + "." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), criteria.getAlias()
					+ "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
			criteria2.add(Restrictions.in(criteria2.getAlias() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),
					new ConstanteAghCaractUnidFuncionais[] { ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES,
							ConstanteAghCaractUnidFuncionais.UNID_COLETA, ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS }));
			criteria2.setProjection(Property.forName(criteria2.getAlias() + "." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()).count());
			criteria.add(Subqueries.lt(0L, criteria2));

			// Código
			if (elemento.getSeq() != null) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), elemento.getSeq()));
			}
			// Descrição
			if (elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}

			if (order) {
				criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
			}
		}
		return criteria;
	}
	
	private DetachedCriteria criarCriteriaCaractExames(final AghUnidadesFuncionais elemento, final Boolean order) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "uf");
		// Popula criteria com dados do elemento
		if (elemento != null) {

			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));

			final DetachedCriteria criteria2 = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "cuf");
			criteria2.add(Restrictions.eqProperty(criteria2.getAlias() + "." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString(), criteria.getAlias()
					+ "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
			criteria2.add(Restrictions.in(criteria2.getAlias() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),
					new ConstanteAghCaractUnidFuncionais[] { ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES }));
			criteria2.setProjection(Property.forName(criteria2.getAlias() + "." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()).count());
			criteria.add(Subqueries.lt(0L, criteria2));

			// Código
			if (elemento.getSeq() != null) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), elemento.getSeq()));
			}
			// Descrição
			if (elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}

			if (order) {
				criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
			}
		}
		return criteria;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutoras(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final AghUnidadesFuncionais elemento) {
		final DetachedCriteria criteria = criarCriteria(elemento, true);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasPorCodigoOuDescricao(final Object parametro) {
		List<AghUnidadesFuncionais> listaUnidades = new ArrayList<AghUnidadesFuncionais>();

		AghUnidadesFuncionais elemento = new AghUnidadesFuncionais();

		// Primeiro tenta buscar por código
		if (CoreUtil.isNumeroShort(parametro)) {
			elemento.setSeq(Short.parseShort(parametro.toString()));
			final DetachedCriteria criteria = criarCriteria(elemento, true);
			listaUnidades = executeCriteria(criteria);
		}
		if (listaUnidades == null || listaUnidades.isEmpty()) { 
			elemento = new AghUnidadesFuncionais();
			elemento.setDescricao((String) parametro);
			final DetachedCriteria criteria = criarCriteria(elemento, true);
			listaUnidades = executeCriteria(criteria);
		}

		return listaUnidades;
	}
	
	/**
	 * aghc_ver_caract_unf(SEQ, 'Unid Executora Exames') 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidExecPorCodDescCaractExames(final Object parametro) {
		List<AghUnidadesFuncionais> listaUnidades = new ArrayList<AghUnidadesFuncionais>();

		AghUnidadesFuncionais elemento = new AghUnidadesFuncionais();

		// Primeiro tenta buscar por código
		if (CoreUtil.isNumeroShort(parametro)) {
			elemento.setSeq(Short.parseShort(parametro.toString()));
			final DetachedCriteria criteria = criarCriteriaCaractExames(elemento, true);
			listaUnidades = executeCriteria(criteria);
		}
		if (listaUnidades == null || listaUnidades.isEmpty()) { // Se não
																// encontrou,
																// busca por
																// descrição
			elemento = new AghUnidadesFuncionais();
			elemento.setDescricao((String) parametro);

			final DetachedCriteria criteria = criarCriteriaCaractExames(elemento, true);
			listaUnidades = executeCriteria(criteria);
		}

		return listaUnidades;
	}

	/**
	 * Método de listagem utilizado pelo componente mec:suggestionBox
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionais(Object parametro) {
		final String srtPesquisa = (String) parametro;
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		if (CoreUtil.isNumeroShort(srtPesquisa)) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(StringUtils.trim(srtPesquisa))) {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa unidades funcionais com característica coletável
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionaisAtivasColetaveis(final Object parametro,
			final List<AghUnidadesFuncionais> listaAghUnidadesFuncionais) {

		final Set<Short> setSeqs = new HashSet<Short>();

		if (listaAghUnidadesFuncionais != null) {
			for (final AghUnidadesFuncionais aghUnidadesFuncionais : listaAghUnidadesFuncionais) {
				setSeqs.add(aghUnidadesFuncionais.getSeq());
			}
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		String srtPesquisa = null;
		if (parametro != null) {
			srtPesquisa = parametro.toString();
		}
		if (CoreUtil.isNumeroShort(srtPesquisa)) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(StringUtils.trim(srtPesquisa))) {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), setSeqs));

		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);

	}

	public Boolean unidadeFuncionalPossuiCaracteristica(final Short seq, final Object[] caracteristicas) {

		final DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		cri.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());

		cri.add(Restrictions.idEq(seq));
		if (caracteristicas != null && caracteristicas.length > 0) {
			cri.add(Restrictions.in(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "."
					+ AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA, caracteristicas));
		}

		return executeCriteriaCount(cri) > 0 ? true : false;
	}

	/**
	 * @ORADB Cursor capc_unid
	 * 
	 * @return
	 */
	public List<Object> obterCapacUnid() {

		final DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		cri.setProjection(Projections.projectionList().add(Projections.property(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.CAPAC_INTERNACAO.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.CLINICA.toString() + SEPARADOR + AghClinicas.Fields.CODIGO.toString())));
		cri.add(Restrictions.isNotNull(AghUnidadesFuncionais.Fields.CAPAC_INTERNACAO.toString()));
		cri.add(Restrictions.gt(AghUnidadesFuncionais.Fields.CAPAC_INTERNACAO.toString(), Short.valueOf("0")));
		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));

		return executeCriteria(cri);
	}

	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorParametro(final String valor) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		// subquery
		// --------------------------------------------------------------------------------------------------------
		final DetachedCriteria subQuery = DetachedCriteria.forClass(AghParametros.class);
		subQuery.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), valor));
		subQuery.setProjection(Projections.property(AghParametros.Fields.VLR_NUMERICO.toString()));
		// --------------------------------------------------------------------------------------------------------
		criteria.add(Property.forName(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).in(subQuery));

		return (AghUnidadesFuncionais) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Pesquisa Unidades Funcionais filtrada pela paramPesquisa e
	 * caracteristicas
	 * 
	 * @param parametroPesquisa
	 * @param situacao
	 * @param atributoOrder
	 * @param listaCaracteristicas
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(final Object parametroPesquisa,
			final DominioSituacao situacao, final AghUnidadesFuncionais.Fields atributoOrder, final List<Short> listaUnfSeq) {
		final List<AghUnidadesFuncionais.Fields> atributosOrder = new ArrayList<AghUnidadesFuncionais.Fields>();
		atributosOrder.add(atributoOrder);

		return pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(parametroPesquisa, situacao, Boolean.TRUE, Boolean.TRUE, null,
				atributosOrder, listaUnfSeq);
	}

	/**
	 * Pesquisa Unidades funcionais por código/ descrição/ Ala e Andar,
	 * filtrando também por características específicas
	 * 
	 * @param parametroPesquisa
	 * @param situacao
	 * @param List
	 *            <atributoOrder>
	 * @param listaCaracteristicas
	 * @return List<AghUnidadesFuncionais>
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(final Object parametroPesquisa,
			final DominioSituacao situacao, final List<Fields> atributoOrder, final List<Short> listaUnfSeq) {
		String paramStr = null;
		if (parametroPesquisa != null) {
			paramStr = parametroPesquisa.toString();

		}		
		DetachedCriteria criteria = this.montarCriteriaParaDescricaoOuCodigoECaracteristicas(paramStr, listaUnfSeq);
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ALA.toString()));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		if (situacao != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}
		for (final Fields fields : atributoOrder) {
			criteria.addOrder(Order.asc(fields.toString()));
		}
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(final String parametroPesquisa,
			final DominioSituacao situacao, final List<Short> listaUnfSeq) {
		final DetachedCriteria criteria = this.montarCriteriaParaDescricaoOuCodigoECaracteristicas(parametroPesquisa, listaUnfSeq);
		if (situacao != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}

		return executeCriteriaCount(criteria);
	}
	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisColetaPorCodigoDescricao(final Object parametroPesquisa) {
		String paramStr = null;
		if (parametroPesquisa != null) {
			paramStr = parametroPesquisa.toString();

		}		
		DetachedCriteria criteria = this.montarCriteriaParaDescricaoOuCodigoECaracteristicas(paramStr, null);
		criteria.createCriteria(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "cun");
		
		criteria.add(Restrictions.in("cun." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), 
					new Object[]{ConstanteAghCaractUnidFuncionais.UNID_COLETA}));

		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 * Pesquisa unidades funcionais de acordo com as caracteristicas passadas
	 * via parametro Quando este método for invocado por algum método diferente
	 * de suggestion box, enviar "parametroPesquisa", "buscarPorCodigo" e
	 * "buscarPorDescricao" nulos.
	 * 
	 * @param parametroPesquisa
	 *            , em caso de suggestionBox enviar o param de pesquisa
	 * @param situacao
	 *            , indica a situação da unidade funcional (Ativa ou Inativa)
	 *            caso nulo irá desconsiderar
	 * @param buscarPorCodigo
	 *            , indica se a pesquisa deve ser filtrada pelo código
	 * @param buscarPorDescricao
	 *            , indica se a pesquisa deve ser filtrada pela descrição
	 * @param orderAsc
	 *            , TRUE => Order.Asc, FALSE ==> Order.DESC, NULL=> não
	 *            adicionado Order
	 * @param atributoOrder
	 *            , Atributos de "AghUnidadesFuncionais.Fields" separados por
	 *            vírgula quando mais de um
	 * @param listaCaracteristicas
	 *            , as unidades funcionais serão filtradas por estas
	 *            caracteristicas.
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(final Object parametroPesquisa,
			final DominioSituacao situacao, final Boolean buscarPorCodigo, final Boolean buscarPorDescricao, final Boolean orderAsc,
			final List<AghUnidadesFuncionais.Fields> atributosOrder, final List<Short> listaUnfSeq) {

		final DetachedCriteria criteria = criteriaUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(parametroPesquisa, situacao, buscarPorCodigo,
				buscarPorDescricao, orderAsc, atributosOrder, listaUnfSeq);
		
		for (final AghUnidadesFuncionais.Fields atributoOrder : atributosOrder) {
			// Se orderAsc true e não nulo
			if (orderAsc != null && orderAsc && atributoOrder != null) {
				criteria.addOrder(Order.asc(atributoOrder.toString()));
			} else if (orderAsc != null && !orderAsc && atributoOrder != null) { // Se
																					// orderAsc
																					// false
																					// e
																					// não
																					// nulo
				criteria.addOrder(Order.desc(atributoOrder.toString()));
			}
		}

		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(final Object parametroPesquisa, final DominioSituacao situacao,
			final Boolean buscarPorCodigo, final Boolean buscarPorDescricao, final Boolean orderAsc, final List<Short> listaUnfSeq) {

			final DetachedCriteria criteria = criteriaUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(parametroPesquisa, situacao, buscarPorCodigo,
				buscarPorDescricao, orderAsc, null, listaUnfSeq);

		return executeCriteriaCount(criteria);
	}

	public DetachedCriteria criteriaUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(final Object parametroPesquisa,
			final DominioSituacao situacao, final Boolean buscarPorCodigo, final Boolean buscarPorDescricao, final Boolean orderAsc,
			final List<AghUnidadesFuncionais.Fields> atributosOrder, final List<Short> listaUnfSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (!listaUnfSeq.isEmpty()) {
			criteria.add(Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), listaUnfSeq));
		}
		// Se deve buscar por código e o parametroPesquisa é numérico
		if (buscarPorCodigo && CoreUtil.isNumeroShort(StringUtils.trimToNull(parametroPesquisa.toString()))) {
			final Short paramShort = Short.valueOf(StringUtils.trimToNull(parametroPesquisa.toString()));
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), paramShort));
		}
		if (buscarPorDescricao) { // Se deve buscar por descrição
			final String strParam = (String) parametroPesquisa;
			if (StringUtils.isNotBlank(strParam) && !CoreUtil.isNumeroShort(StringUtils.trimToNull(parametroPesquisa.toString()))) {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), strParam, MatchMode.ANYWHERE));
			}
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}

		criteria.createAlias(AghUnidadesFuncionais.Fields.ALA.toString(), "indAla", JoinType.LEFT_OUTER_JOIN);
		
		return criteria;

	}

	// #3559 - INICIO

	/**
	 * Retorna criteria para pesquisa de unidades funcionais disponiveis para
	 * dispensacao de medicamento
	 * 
	 * @param unfsSolicitantesMdto
	 * @return criteria
	 */
	private DetachedCriteria montaPesquisaUnidadesFuncionaisDisponivelLocalDispensacao(final Short[] unfsSolicitantesMdto) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		final DetachedCriteria caracteristicaCriteria = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);

		caracteristicaCriteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString())));

		caracteristicaCriteria.add(Restrictions.in(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), new Object[] {
				ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA,
				ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA, ConstanteAghCaractUnidFuncionais.CO,
				ConstanteAghCaractUnidFuncionais.ZONA_AMBULATORIO }));

		criteria.add(Subqueries.propertyIn(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), caracteristicaCriteria));
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		// criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		if (unfsSolicitantesMdto != null && unfsSolicitantesMdto.length > 0) {
			final Criterion in = Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfsSolicitantesMdto);
			criteria.add(Restrictions.not(in));
		}

		return criteria;
	}

	/**
	 * Retorna count de pesquisa de unidades funcionais disponiveis para
	 * dispensacao de medicamento
	 * 
	 * @param unfsDispensaMdtos
	 * @return count
	 */
	public Long buscarCountUnidadesFuncionaisDisponivelLocalDispensacao(final Short[] unfsDispensaMdtos) {

		final DetachedCriteria criteria = montaPesquisaUnidadesFuncionaisDisponivelLocalDispensacao(unfsDispensaMdtos);

		return executeCriteriaCount(criteria);
	}

	/**
	 * Retorna lista paginada de pesquisa de unidades funcionais disponiveis
	 * para dispensacao de medicamento
	 * 
	 * @param unfsDispensaMdtos
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return unfsList
	 */
	public List<AghUnidadesFuncionais> buscarUnidadesFuncionaisDisponivelLocalDispensacao(final Short[] unfsDispensaMdtos, final Integer firstResult,
			final Integer maxResults, final String orderProperty, final boolean asc) {

		final DetachedCriteria criteria = montaPesquisaUnidadesFuncionaisDisponivelLocalDispensacao(unfsDispensaMdtos);
		final List<AghUnidadesFuncionais> unfsList = executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);

		return unfsList;
	}

	/**
	 * Retorna lista de pesquisa de unidades funcionais disponiveis para
	 * dispensacao de medicamento
	 * 
	 * @param unfsSolicitantesMdto
	 * @return
	 */
	public List<AghUnidadesFuncionais> buscarSeqsUnidadesFuncionaisDisponivelLocalDispensacao(final Short... unfsSolicitantesMdto) {

		final DetachedCriteria criteria = montaPesquisaUnidadesFuncionaisDisponivelLocalDispensacao(unfsSolicitantesMdto);
		final List<AghUnidadesFuncionais> unfsSeqList = executeCriteria(criteria);

		return unfsSeqList;
	}

	public List<AghUnidadesFuncionais> listaUnidadeFuncionalComSiglaNaoNulla(final Object param, final DominioSituacao situacao,
			final Integer maxResults, final String order) {

		final DetachedCriteria criteria = montarCriteriaUnidadeFuncionalComSiglaNaoNull(param, situacao);
		criteria.addOrder(Order.asc(order));
		return executeCriteria(criteria, 0, maxResults, order, true);
	}

	public Long listaUnidadeFuncionalComSiglaNaoNullaCount(final Object param, final DominioSituacao situacao) {
		final DetachedCriteria criteria = montarCriteriaUnidadeFuncionalComSiglaNaoNull(param, situacao);
		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria montarCriteriaUnidadeFuncionalComSiglaNaoNull(final Object param, final DominioSituacao situacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (param != null && !param.toString().equals("")) {
			final Criterion sigla = Restrictions.ilike(AghUnidadesFuncionais.Fields.SIGLA.toString(), param.toString(), MatchMode.ANYWHERE);
			final Criterion descricao = Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE);
			final LogicalExpression orExp = Restrictions.or(sigla, descricao);
			criteria.add(orExp);
		}

		criteria.add(Restrictions.isNotNull(AghUnidadesFuncionais.Fields.SIGLA.toString()));

		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));

		return criteria;
	}

	// #3559 - FIM

	public String obterDescricaoUnidadeFuncional(final Short codigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		if (codigo != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo));
		}
		criteria.setProjection(Projections.projectionList().add(// 0
				Projections.property(AghUnidadesFuncionais.Fields.ANDAR.toString())).add(// 1
				Projections.property(AghUnidadesFuncionais.Fields.ALA.toString())).add(// 2
				Projections.property(AghUnidadesFuncionais.Fields.DESCRICAO.toString())));

		final StringBuffer descricao = new StringBuffer();

		final List<Object[]> res = executeCriteria(criteria, 0, 1, null, true);
		if (res != null && !res.isEmpty()) {
			final Object[] obj = res.get(0);

			if (obj[0] != null) {
				descricao.append(obj[0]);
			}

			if (obj[1] != null) {
				final String ala = ((AghAla) obj[1]).toString();
				if (ala.length() == 1) {
					//descricao.append('0').append(ala);
					descricao.append(' ').append(ala);
				} else {
					descricao.append(ala);
				}
			}

			if (obj[2] != null) {
				descricao.append(" - ").append((String) obj[2]);
			}
		}
		return descricao.toString();
	}

	@SuppressWarnings("unchecked")
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String strPesquisa) {
		// Utilizado HQL, pois não é possível fazer pesquisar com LIKE para
		// tipos numéricos (Integer, Byte, Short)
		// através de Criteria (ocorre ClassCastException).
		final StringBuilder hql = new StringBuilder(100);
		hql.append("select q from ").append(AghUnidadesFuncionais.class.getSimpleName()).append(" q ");
		hql.append("left join fetch q.").append(AghUnidadesFuncionais.Fields.ALA.toString()).append(" indAla ");

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			strPesquisa = CoreUtil.retirarCaracteresInvalidos(strPesquisa);
			strPesquisa = strPesquisa.toUpperCase();

			String strPesquisaNumerica = strPesquisa.replaceFirst("0*", "");
			if ("".equals(strPesquisaNumerica)) {
				strPesquisaNumerica = "0";
			}

			hql.append("where str(q.").append(AghUnidadesFuncionais.Fields.ANDAR.toString()).append(") like '%").append(strPesquisaNumerica).append("%' ")
					.append("or str(q.").append(AghUnidadesFuncionais.Fields.ALA_CODIGO.toString()).append(") like '%").append(strPesquisaNumerica)
					.append("%' ").append("or q.").append(AghUnidadesFuncionais.Fields.DESCRICAO.toString()).append(" like '%").append(strPesquisa)
					.append("%' ");
		}

		hql.append("order by ").append(AghUnidadesFuncionais.Fields.ANDAR.toString());
		return createHibernateQuery(hql.toString()).list();
	}

	public AghUnidadesFuncionais obterUnidadeFuncionalPorUnidEmergencia(final Short codigo) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		cri.add(Restrictions.idEq(codigo));
		cri.add(Restrictions.or(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_EMERGENCIA.toString(), DominioSimNao.S),
				Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_INTERNACAO.toString(), DominioSimNao.S)));

		return (AghUnidadesFuncionais) executeCriteriaUniqueResult(cri);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(final String srtPesquisa) {
		final DetachedCriteria cri = this.obterCriteriaPesquisarUnidadeFuncionalVO(srtPesquisa);

		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()));
		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ALA_CODIGO.toString()));
		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

		return executeCriteria(cri);
	}
	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(final String srtPesquisa, Integer maxResults) {
		final DetachedCriteria cri = this.obterCriteriaPesquisarUnidadeFuncionalVO(srtPesquisa);

		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()));
		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ALA_CODIGO.toString()));
		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

		return executeCriteria(cri, 0, maxResults, null, false);
	}
	
	
	public Long pesquisarUnidadeFuncionalVOPorCodigoEDescricaoCount(final String srtPesquisa) {
		
		final DetachedCriteria cri = this.obterCriteriaPesquisarUnidadeFuncionalVO(srtPesquisa);
		
		return executeCriteriaCount(cri); 
	}
	
	public DetachedCriteria obterCriteriaPesquisarUnidadeFuncionalVO(final String srtPesquisa){
		final DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroShort(srtPesquisa)) {
				cri.add(Restrictions.or(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE),
						Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(srtPesquisa))));
			} else {
				cri.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		cri.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioUnidFunc.A));
		
		return cri;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoAndar(String srtPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		
		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroByte(srtPesquisa)){
				cri.add(Restrictions.or(
							Restrictions.or(
									Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(),srtPesquisa, MatchMode.ANYWHERE),
									Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(srtPesquisa))),
							Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), srtPesquisa)));
			}else if (CoreUtil.isNumeroShort(srtPesquisa)) {
				cri.add(Restrictions.or(Restrictions.ilike(
						AghUnidadesFuncionais.Fields.DESCRICAO.toString(),
						srtPesquisa, MatchMode.ANYWHERE),
						Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL
								.toString(), Short.valueOf(srtPesquisa))));
			} else {
				cri.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), srtPesquisa,
						MatchMode.ANYWHERE));
			}
		}
		
		cri.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioUnidFunc.A));
		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()));
		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ALA_CODIGO.toString()));
		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		
		return executeCriteria(cri);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(final Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			if (objPesquisa instanceof Short) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), objPesquisa));
			} else {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf((String) objPesquisa)));
			}
		} else {
			if (!StringUtils.isEmpty((String) objPesquisa)) {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), (String) objPesquisa, MatchMode.ANYWHERE));
			}
		}
		criteria.createAlias(AghUnidadesFuncionais.Fields.ALA.toString(), "indAla", JoinType.LEFT_OUTER_JOIN);
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacao(final Object objPesquisa) {
		final DetachedCriteria criteria = obterCriteriaPesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacao(objPesquisa);

		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacaoCount(final Object objPesquisa) {
		final DetachedCriteria criteria = obterCriteriaPesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacao(objPesquisa);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacao(final Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		final String descricaoOuCodigo = StringUtils.trimToNull((String) objPesquisa);
		if (StringUtils.isNotBlank(descricaoOuCodigo)) {
			short codigo = -1;
			Criterion criterionDescCodigo = null;
			Criterion criterionAndar = null;
			if (CoreUtil.isNumeroShort(descricaoOuCodigo)) {
				codigo = Short.parseShort(descricaoOuCodigo);
			}
			if (codigo != -1) {
				criterionDescCodigo = Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo);
				if (codigo <= Byte.MAX_VALUE) {
					criterionAndar = Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), (byte) codigo);
				}
			} else {
				criterionDescCodigo = Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
			}
			if (criterionAndar == null) {
				final String[] valoresAndarAla = StringUtils.split(descricaoOuCodigo);
				for (int i = 0; i < valoresAndarAla.length; i++) {
					final Criterion[] criterions = new Criterion[valoresAndarAla.length];
					String andar = StringUtils.EMPTY;
					try {
						andar = valoresAndarAla[i];
					} catch (final Exception e) {
						LOG.warn(e.getMessage(), e);
					}
					if (StringUtils.isNoneBlank(andar)) {
						criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), andar);
					} else {
						final AghAla ala = this.obterAghAlaPorId(valoresAndarAla[i]);
						if (ala != null) {
							criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.ALA.toString(), ala);
						}
					}
					if (criterions[i] == null) {
						criteria.add(criterionDescCodigo);
					} else {
						criterions[i] = Restrictions.or(criterionDescCodigo, criterions[i]);
						criteria.add(criterions[i]);
					}
				}
			} else {
				criteria.add(Restrictions.or(criterionDescCodigo, criterionAndar));
			}
		}

		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_INTERNACAO.toString(), DominioSimNao.S));
		criteria.createAlias(AghUnidadesFuncionais.Fields.ALA.toString(), ALA, JoinType.LEFT_OUTER_JOIN);
		
		return criteria;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(final Object objPesquisa,
			final boolean apenasAtivos) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			if (objPesquisa instanceof Short) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), objPesquisa));
			} else {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf((String) objPesquisa)));
			}
		} else {
			if (!StringUtils.isEmpty((String) objPesquisa)) {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), (String) objPesquisa, MatchMode.ANYWHERE));
			}
		}

		if (apenasAtivos) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		}
		return executeCriteria(criteria, 0, 100, AghUnidadesFuncionais.Fields.DESCRICAO.toString(), true);
	}

	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(final Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			if (objPesquisa instanceof Short) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), objPesquisa));
			} else {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf((String) objPesquisa)));
			}
		} else {
			if (!StringUtils.isEmpty((String) objPesquisa)) {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), (String) objPesquisa, MatchMode.ANYWHERE));
			}
		}

		return executeCriteriaCount(criteria);
	}

	/**
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param codigo
	 * @param descricao
	 * @param sigla
	 * @param clinica
	 * @param centroCusto
	 * @param unidadeFuncionalPai
	 * @param situacao
	 * @return
	 */
	private DetachedCriteria createPesquisaCriteria(Short codigo, String descricao, String sigla, AghClinicas clinica,
			FccCentroCustos centroCusto, AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, AghAla ala) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		
		criteria.createAlias("UNF."+AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), "PAI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UNF."+AghUnidadesFuncionais.Fields.CLINICA.toString(), "CLI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UNF."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), "CC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UNF."+AghUnidadesFuncionais.Fields.ALA.toString(), ALA, JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CARAC", JoinType.LEFT_OUTER_JOIN);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotBlank(sigla)) {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.SIGLA.toString(), sigla, MatchMode.ANYWHERE));
		}
		if (clinica != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.CLINICA.toString(), clinica));
		}
		if (centroCusto != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), centroCusto));
		}
		if (unidadeFuncionalPai != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), unidadeFuncionalPai));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}
		if (StringUtils.isNoneBlank(andar)) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), andar));
		}
		if (ala != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.ALA.toString(), ala));
		}		
		
		return criteria;
	}

	/**
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param unidadeFuncionalPaiSeq
	 * @param situacao
	 * @return
	 */
	private DetachedCriteria createPesquisaUnidadesVinculadasCriteria(final Short unidadeFuncionalPaiSeq, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (unidadeFuncionalPaiSeq != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString(), unidadeFuncionalPaiSeq));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}

		return criteria;
	}
	
	/**
	 * Pesquisa as Filhas da Unidade Funcional Informada
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @params unfSeq,situacao
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisaUnidadesFilhasVinculadas(final Short unidadeFuncionalPaiSeq, final DominioSituacao situacao) {
		return executeCriteria(createPesquisaUnidadesVinculadasCriteria(unidadeFuncionalPaiSeq, situacao));
	}

	public List<AghUnidadesFuncionais> pesquisa(Integer firstResult, Integer maxResults, String orderProperty, Boolean asc,
			Short codigo, String descricao, String sigla, AghClinicas clinica, FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, AghAla ala) {
		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao, sigla, clinica, centroCusto, unidadeFuncionalPai, situacao, andar, ala);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisaCount(Short codigo, String descricao, String sigla, AghClinicas clinica,
			FccCentroCustos centroCusto, AghUnidadesFuncionais unidadeFuncionalPai, DominioSituacao situacao, String andar, AghAla ala) {
		final DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao, sigla, clinica, centroCusto, unidadeFuncionalPai, situacao, andar, ala);
		return executeCriteriaCount(criteria);
	}

	/**
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaUnidCodigoOuDescricao(final Object parametro) {
		String descricao = null;
		Short codigo = null;

		if (CoreUtil.isNumeroInteger(parametro)) {
			if (parametro instanceof String) {
				codigo = Short.valueOf((String) parametro);
			} else {
				codigo = (Short) parametro;
			}
		}

		else if (parametro instanceof String) {
			descricao = (String) parametro;
		}

		final DetachedCriteria criteria = obterCriteriaUnidades();

		if (descricao != null && StringUtils.isBlank(descricao.trim())) {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo));
		}

		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));

		return criteria;

	}

	/**
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @return
	 */
	private DetachedCriteria obterCriteriaUnidades() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		return criteria;
	}

	/**
	 * Metódo de consulta de Unidade Pai por Codigo ou Descrição
	 * 
	 * @dbtables AghUnidadesFuncionais select
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaPorCodigoOuDescricao(final Object parametro, final boolean orderDescricao) {
		final String descricao = (String) parametro;

		final DetachedCriteria criteria = obterCriteriaUnidades();

		if (descricao != null && !"".equals(descricao)) {

			if (StringUtils.isNumeric(descricao)) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.parseShort((descricao))));
			} else {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (orderDescricao) {
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		} else {
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		}

		return criteria;

	}

	/**
	 * Verifica pacientes em atendimento de urgência para unidade funcional a
	 * ser desativada para desativar ou não a Unidade Funcional
	 * 
	 * @dbtables AinInternacao select
	 * 
	 * @params unfSeq,situacao
	 * @return
	 */
	private DetachedCriteria createPesquisaPacienteInternado(final Short unfSeq, final Boolean situacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(AinInternacao.Fields.UNF_SEQ.toString(), unfSeq));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), situacao));
		}

		return criteria;

	}

	/**
	 * Verifica pacientes em atendimento de urgência para unidade funcional a
	 * ser desativada
	 * 
	 * @dbtables AinAtendimentosUrgencia select
	 * 
	 * @params unfSeq,situacao
	 * @return
	 */
	private DetachedCriteria createPesquisaAtendimentoUrgencia(final Short unfSeq, final Boolean situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);

		if (unfSeq != null) {
			criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString(), situacao));
		}

		return criteria;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesPaiPorDescricao(final Object parametro) {
		final DetachedCriteria criteria = obterCriteriaPesquisaUnidCodigoOuDescricao(parametro);
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria);
	}
	
	public Long pesquisarUnidadesPaiPorDescricaoCount(final Object parametro) {
		final DetachedCriteria criteria = obterCriteriaPesquisaUnidCodigoOuDescricao(parametro);
		return this.executeCriteriaCount(criteria);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesPorCodigoDescricao(final Object parametro, final boolean orderDescricao) {
		final DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(parametro, orderDescricao);
		return this.executeCriteria(criteria);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesPorCodigoDescricaoFilha(final Object parametro, final boolean orderDescricao, final Short unidadeFuncionalFilha) {
		final DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricaoFilha(parametro, orderDescricao, unidadeFuncionalFilha);
		return this.executeCriteria(criteria);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		return executeCriteria(criteria);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivas() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<AghUnidadesFuncionais> pesquisaPacienteInternado(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Short codigo, final boolean situacao) {
		final DetachedCriteria criteria = createPesquisaPacienteInternado(codigo, situacao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<AghUnidadesFuncionais> pesquisaAtendimentoUrgencia(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Short codigo, final boolean situacao) {
		final DetachedCriteria criteria = createPesquisaAtendimentoUrgencia(codigo, situacao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionaisPorCodigoDescricao(final Object parametro, final boolean orderDescricao) {
		final DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(parametro, orderDescricao);
		return executeCriteria(criteria, 0, 100, null, true);
	}

	@SuppressWarnings("unchecked")
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorUnidEmergencia(String strPesquisa, final boolean ordemPorDescricao) {
		// Utilizado HQL, pois não é possível fazer pesquisar com LIKE para
		// tipos numéricos (Integer, Byte, Short)
		// através de Criteria (ocorre ClassCastException).
		final StringBuilder hql = new StringBuilder(100);
		hql.append("from ").append(AghUnidadesFuncionais.class.getSimpleName()).append(" q ");
		hql.append(" where (q.").append(AghUnidadesFuncionais.Fields.IND_UNID_INTERNACAO.toString()).append(" = 'S' or q.")
				.append(AghUnidadesFuncionais.Fields.IND_UNID_EMERGENCIA.toString()).append(" = 'S')");

		if (strPesquisa != null && !"".equals(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
			hql.append("and str(q.").append(AghUnidadesFuncionais.Fields.ANDAR.toString()).append(") like '%").append(strPesquisa).append("%' ")
					.append("or str(q.").append(AghUnidadesFuncionais.Fields.ALA_CODIGO.toString()).append(") like '%").append(strPesquisa)
					.append("%' ").append("or q.").append(AghUnidadesFuncionais.Fields.DESCRICAO.toString()).append(" like '%").append(strPesquisa)
					.append("%' ");
			if (StringUtils.isNumeric(strPesquisa)) {
				hql.append(" or seq = ").append(Long.valueOf(strPesquisa)).append(' ');
			}
		}
		
		hql.append(" and (q.").append(AghUnidadesFuncionais.Fields.SITUACAO.toString()).append(" = 'A') ");
		
		if (ordemPorDescricao) {
			hql.append("order by q.").append(AghUnidadesFuncionais.Fields.DESCRICAO.toString()).append(" asc");
		} else {
			hql.append("order by q.").append(AghUnidadesFuncionais.Fields.ANDAR.toString()).append(" asc, q.")
					.append(AghUnidadesFuncionais.Fields.ALA_CODIGO.toString()).append(" asc, q.")
					.append(AghUnidadesFuncionais.Fields.DESCRICAO.toString()).append(" asc");
		}

		return createHibernateQuery(hql.toString()).list();
	}

	@SuppressWarnings("unchecked")
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final Short seq, final Boolean ordernarPorCodigoAlaDescricao,
			final boolean apenasAtivos, final boolean caracteristicasInternacaoOuEmergencia, final boolean caracteristicaUnidadeExecutora) {

		final StringBuilder sql = new StringBuilder(400);
		sql.append(" select distinct uf from AghUnidadesFuncionais uf");
		if (caracteristicasInternacaoOuEmergencia) {
			sql.append(" join uf.caracteristicas carac ");
		}
		sql.append(" where uf.seq = :seq ");

		if (apenasAtivos) {
			sql.append(" and uf.indSitUnidFunc = :situacao ");
		}

		if (caracteristicasInternacaoOuEmergencia) {
			sql.append(" and (carac.id.caracteristica = :unidadeInternacao or carac.id.caracteristica = :unidadeEmergencia) ");
		}

		if (caracteristicaUnidadeExecutora) {
			sql.append(" and uf.seq in (select cuf.id.unfSeq from AghCaractUnidFuncionais cuf where cuf.id.caracteristica = :unidadeExecutoraExames )  ");
		}

		final Query query = createHibernateQuery(sql.toString());
		if (apenasAtivos) {
			query.setParameter("situacao", DominioUnidFunc.A);
		}

		query.setParameter("seq", seq);

		if (caracteristicasInternacaoOuEmergencia) {
			query.setParameter("unidadeInternacao", ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO);
			query.setParameter("unidadeEmergencia", ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
		}

		if (caracteristicaUnidadeExecutora) {
			query.setParameter("unidadeExecutoraExames", ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES);
		}

		// Reduzido a quantidade de elementos a serem exibidas na suggestion box
		query.setFirstResult(0);
		query.setMaxResults(25);

		return query.list();
	}
	
	
	public Integer pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(String strPesquisa) {
		List<AghUnidadesFuncionais> list = null;
		
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			list = this.pesquisarUnidadeFuncionalPorCodigoEDescricao(
					Short.valueOf(strPesquisa),false, true, false, false);
		} else {
			list = this.pesquisarUnidadeFuncionalPorCodigoEDescricao(strPesquisa, 
					false, true, false, false);
		}
		
		if(list.isEmpty()) {
			return Integer.valueOf(0);
		}
		return list.size();
	}
	

	@SuppressWarnings({"unchecked", "PMD.NPathComplexity"})
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(final String strPesquisa,
			final Boolean ordernarPorCodigoAlaDescricao, final boolean apenasAtivos, final boolean caracteristicasInternacaoOuEmergencia,
			final boolean caracteristicaUnidadeExecutora) {
		final StringBuilder sql = new StringBuilder(600);
		sql.append(" select distinct uf from AghUnidadesFuncionais uf");
		if (caracteristicasInternacaoOuEmergencia) {
			sql.append(" join uf.caracteristicas carac ");
		}
		sql.append(" where 1 = 1 ");

		if (StringUtils.isNotBlank(strPesquisa)) {
			sql.append(" 	and upper('0' || uf.andar || ' ' || coalesce(uf.indAla, '') || ' - ' || uf.descricao) like upper('%' || :strPesquisa || '%') ");
		}

		if (apenasAtivos) {
			sql.append(" and uf.indSitUnidFunc = :situacao ");
		}

		if (caracteristicasInternacaoOuEmergencia) {
			sql.append(" and (carac.id.caracteristica = :unidadeInternacao or carac.id.caracteristica = :unidadeEmergencia) ");
		}

		if (caracteristicaUnidadeExecutora) {
			sql.append(" and uf.seq in (select cuf.id.unfSeq from AghCaractUnidFuncionais cuf where cuf.id.caracteristica = :unidadeExecutoraExames )  ");
		}

		// se for null, temos a opcao de nao usar ordenacao
		if (ordernarPorCodigoAlaDescricao != null && ordernarPorCodigoAlaDescricao) {
			sql.append(" order by uf.andar,  uf.indAla , uf.descricao ");
		} else if (ordernarPorCodigoAlaDescricao != null && !ordernarPorCodigoAlaDescricao) {
			sql.append(" order by uf.descricao ");
		}

		final Query query = createHibernateQuery(sql.toString());

		if (apenasAtivos) {
			query.setParameter("situacao", DominioUnidFunc.A);
		}

		if (caracteristicasInternacaoOuEmergencia) {
			query.setParameter("unidadeInternacao", ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO);
			query.setParameter("unidadeEmergencia", ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
		}

		if (caracteristicaUnidadeExecutora) {
			query.setParameter("unidadeExecutoraExames", ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES);
		}

		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("strPesquisa", strPesquisa);
		}

		// Reduzido a quantidade de elementos a serem exibidas na suggestion box
		query.setFirstResult(0);
		//query.setMaxResults(25);

		return query.list();
	}

	@SuppressWarnings("rawtypes")
	public List listarLocalDataUltExamePorHistoricoPacienteVO(final HistoricoPacienteVO historicoVO) {
		// FIXME Alterar para DetachedCriteria com Subqueries
		final javax.persistence.Query q = this.createQuery(
				"SELECT " + "unf.andar, unf.indAla, unf.descricao, ise.dthrProgramada " + "FROM " + "AelItemSolicitacaoExames ise, "
						+ "AelSolicitacaoExames soe, " + "AghAtendimentos atd, " + "AghUnidadesFuncionais unf " + "WHERE "
						+ "atd.paciente.codigo = :codigo " + "AND " + "soe.atendimento.seq = atd.seq " + "AND "
						+ "ise.solicitacaoExame.seq = soe.seq " + "AND " + "ise.dthrProgramada <= current_date " + "AND "
						+ "ise.unidadeFuncional.seq in ( "
						+ "SELECT cuf.id.unfSeq FROM AghCaractUnidFuncionais cuf WHERE cuf.id.caracteristica = 'Area Fechada' " + ") " + "AND "
						+ "ise.unidadeFuncional.seq in ( "
						+ "SELECT cuf.id.unfSeq FROM AghCaractUnidFuncionais cuf WHERE cuf.id.caracteristica = 'Unid Executora Exames' " + ") "
						+ "AND " + "ise.situacaoItemSolicitacao.codigo in ('AE', 'AX', 'LI', 'EX') " + "AND " + "unf.seq = ise.unidadeFuncional.seq "
						+ "ORDER BY ise.dthrProgramada DESC");
		q.setParameter("codigo", historicoVO.getAipPaciente().getCodigo());

		return q.getResultList();
	}

	public AghUnidadesFuncionais obterUnidadeFuncionalQuarto(final Short numero) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AinQuartos.class);
		cri.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("unidadeFuncional"))));
		cri.add(Restrictions.eq("numero", numero));

		final Object resultadoPesquisa = this.executeCriteriaUniqueResult(cri);

		return (AghUnidadesFuncionais) resultadoPesquisa;
	}

	public List<AghUnidadesFuncionais> pesquisaUnidadesFuncionais(final AghUnidadesFuncionais unidadeFuncional) {
		final DetachedCriteria criteria = createPesquisaUnidadesCriteria(unidadeFuncional, true);
		return executeCriteria(criteria);
	}

	private DetachedCriteria createPesquisaAtendimentosCriteria(final AghUnidadesFuncionais unidadeFuncional, final boolean ordemPorNome) {
		final DetachedCriteria criteria = createCriteriaSemOrdenacao(unidadeFuncional);

		criteria.addOrder(Order.asc("uf." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE));
		criteria.addOrder(Order.asc("aat." + AghAtendimentos.Fields.UNF_SEQ));
		criteria.addOrder(Order.asc("aat." + AghAtendimentos.Fields.LTO_LTO_ID));
		if (ordemPorNome) {
			criteria.createAlias("paciente", "pac");
			criteria.addOrder(Order.asc("pac." + AipPacientes.Fields.NOME));
		}

		return criteria;
	}

	private DetachedCriteria createPesquisaAtendimentosCriteria(final AghUnidadesFuncionais unidadeFuncional, final DominioOrdenacaoRelatorioPacientesUnidade ordenacao) {
		final DetachedCriteria criteria = createCriteriaSemOrdenacao(unidadeFuncional);

		criteria.addOrder(Order.asc("uf." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE));
		criteria.addOrder(Order.asc("aat." + AghAtendimentos.Fields.UNF_SEQ));
		criteria.createAlias("paciente", "pac");
		
		if(ordenacao != null){
			if (ordenacao == DominioOrdenacaoRelatorioPacientesUnidade.NOME) {
				criteria.addOrder(Order.asc("pac." + AipPacientes.Fields.NOME));
			} else if (ordenacao == DominioOrdenacaoRelatorioPacientesUnidade.LEITO) {
				criteria.addOrder(Order.asc("aat." + AghAtendimentos.Fields.LTO_LTO_ID));
			}
		}

		return criteria;
	}

	private DetachedCriteria createCriteriaSemOrdenacao(final AghUnidadesFuncionais unidadeFuncional) {
		final DetachedCriteria subquery = createPesquisaUnidadesCriteria(unidadeFuncional, false);
		subquery.setProjection(Projections.property("auf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "aat");
		criteria.createAlias("unidadeFuncional", "uf");
		criteria.add(Restrictions.eq("aat." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.or(Restrictions.isNotNull("aat." + AghAtendimentos.Fields.ATU_SEQ.toString()),
				Restrictions.isNotNull("aat." + AghAtendimentos.Fields.INT_SEQ.toString())));
		criteria.add(Property.forName("aat." + AghAtendimentos.Fields.UNF_SEQ.toString()).in(subquery));

		return criteria;
	}

	private DetachedCriteria createPesquisaUnidadesCriteria(final AghUnidadesFuncionais unidadeFuncional, final boolean order) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "auf");
		if (unidadeFuncional != null && unidadeFuncional.getSeq() != null) {
			criteria.add(Restrictions.or(Restrictions.eq("auf." + AghUnidadesFuncionais.Fields.SEQUENCIAL, unidadeFuncional.getSeq()),
					Restrictions.eq("auf." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE, unidadeFuncional.getSeq())));
		}
		criteria.add(Restrictions.or(Restrictions.eq("auf." + AghUnidadesFuncionais.Fields.IND_UNID_EMERGENCIA.toString(), DominioSimNao.S),
				Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_INTERNACAO.toString(), DominioSimNao.S)));

		if (order) {
			criteria.addOrder(Order.asc("auf." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE));
			criteria.addOrder(Order.asc("auf." + AghUnidadesFuncionais.Fields.SEQUENCIAL));
		}

		return criteria;
	}

	public Long pesquisaAtendimentosCount(final AghUnidadesFuncionais unidadeFuncional) {
		final DetachedCriteria criteria = createCriteriaSemOrdenacao(unidadeFuncional);
		return executeCriteriaCount(criteria);
	}
	
	public List<AghAtendimentosVO> pesquisaAtendimentos(final AghUnidadesFuncionais unidadeFuncional, final DominioOrdenacaoRelatorioPacientesUnidade ordenacao) {
		final DetachedCriteria criteria = createPesquisaAtendimentosCriteria(unidadeFuncional, ordenacao);

		criteria.createAlias(AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), "CON", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("uf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()),AghAtendimentosVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("LTO."+AinLeitos.Fields.LTO_ID.toString()),AghAtendimentosVO.Fields.LTO_ID.toString())
				
				.add(Projections.property("pac."+AipPacientes.Fields.CODIGO.toString()),AghAtendimentosVO.Fields.CODIGO_PACIENTE.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.NOME.toString()),AghAtendimentosVO.Fields.NOME_PACIENTE.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.DATA_NASCIMENTO.toString()),AghAtendimentosVO.Fields.DT_NASCIMENTO_PACIENTE.toString())
				
				.add(Projections.property("CON."+AacConsultas.Fields.NUMERO.toString()),AghAtendimentosVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("aat."+AghAtendimentos.Fields.PRONTUARIO.toString()),AghAtendimentosVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("aat."+AghAtendimentos.Fields.DATA_HORA_INICIO.toString()),AghAtendimentosVO.Fields.DATA_HORA_INICIO.toString())
				.add(Projections.property("aat."+AghAtendimentos.Fields.INT_SEQ.toString()),AghAtendimentosVO.Fields.INT_SEQ.toString())
				
				.add(Projections.property("ESP."+AghEspecialidades.Fields.SIGLA.toString()),AghAtendimentosVO.Fields.SIGLA_ESPECIALIDADE.toString())
				
				
				.add(Projections.sqlProjection(montaSQLColunaEstadoSaude(), new String[]{AghAtendimentosVO.Fields.ESTADO_SAUDE.toString()}, new Type[]{StringType.INSTANCE}))
				.add(Projections.sqlProjection(montaSQLProjectionUltimaSenhaAutorizada() , new String[]{AghAtendimentosVO.Fields.SENHA_AUTORIZADA.toString()}, new Type[]{StringType.INSTANCE}))
				);

		criteria.setResultTransformer(Transformers.aliasToBean(AghAtendimentosVO.class));
		
		return executeCriteria(criteria);
	}

	public List<AghAtendimentos> pesquisaAtendimentos(final AghUnidadesFuncionais unidadeFuncional, final boolean ordemPorNome) {
		final DetachedCriteria criteria = createPesquisaAtendimentosCriteria(unidadeFuncional, ordemPorNome);
		return executeCriteria(criteria);
	}
	
	private String montaSQLProjectionUltimaSenhaAutorizada() {
		StringBuilder sql = new StringBuilder(200);
		
		sql.append(" (select ADA2.").append(AinDiariasAutorizadas.Fields.SENHA.name()).append(" from ")
			.append(AinDiariasAutorizadas.class.getAnnotation(Table.class).schema() ).append('.').append( AinDiariasAutorizadas.class.getAnnotation(Table.class).name() ).append(" ADA2, ")
			.append(AinInternacao.class.getAnnotation(Table.class).schema()).append('.').append(AinInternacao.class.getAnnotation(Table.class).name()).append(" INT2 ")
			.append(" where ADA2.").append(AinDiariasAutorizadas.Fields.SEQ.name()).append(" = ")
			.append(" (select max(ADA.").append(AinDiariasAutorizadas.Fields.SEQ.name()).append(") from ")
			.append(AinDiariasAutorizadas.class.getAnnotation(Table.class).schema() ).append('.').append( AinDiariasAutorizadas.class.getAnnotation(Table.class).name() ).append(" ADA, ")
			.append(AinInternacao.class.getAnnotation(Table.class).schema()).append('.').append(AinInternacao.class.getAnnotation(Table.class).name()).append(" INT ")
		    .append(" where ADA.").append(AinDiariasAutorizadas.Fields.INT_SEQ.name()).append(" = INT.").append(AinInternacao.Fields.SEQ.name())
		    .append(" AND INT.").append(AinInternacao.Fields.SEQ.name() ).append(" = {alias}.").append(AghAtendimentos.Fields.INT_SEQ.name()).append(") ")
		    .append(" AND ADA2.").append(AinDiariasAutorizadas.Fields.INT_SEQ.name()).append(" = INT2.").append(AinDiariasAutorizadas.Fields.SEQ.name())
		    .append(" AND INT2.").append(AinDiariasAutorizadas.Fields.SEQ.name()).append(" = {alias}.").append(AghAtendimentos.Fields.INT_SEQ.name());
		
		sql.append(") AS ").append(AghAtendimentosVO.Fields.SENHA_AUTORIZADA.toString());
		   
		return sql.toString();
	}

	private String montaSQLColunaEstadoSaude() {
		StringBuilder sql = new StringBuilder(100);
		sql.append(" (select MTEP.").append(MamTipoEstadoPaciente.Fields.DESCRICAO.toString())
				.append(" from ")
						   .append(MamTipoEstadoPaciente.class.getAnnotation(Table.class).schema() ).append('.').append(MamTipoEstadoPaciente.class.getAnnotation(Table.class).name() ).append( " MTEP, ")
						   .append(MamEstadoPaciente.class.getAnnotation(Table.class).schema()).append('.').append(MamEstadoPaciente.class.getAnnotation(Table.class).name()).append(" MEP ")
				   .append(" where MEP.").append(MamEstadoPaciente.Fields.TSA_SEQ.name() ).append( " = MTEP.").append( MamTipoEstadoPaciente.Fields.SEQ.name())
				   .append(" AND MEP.").append(MamEstadoPaciente.Fields.ATD_SEQ.name() ).append( " = {alias}.").append(AghAtendimentos.Fields.SEQ.name());
		
		if(isOracle()){
			sql.append(" and rownum <=1 ");
		} else {
			sql.append(" limit 1 ");
		}
		
		sql.append(") AS ").append(AghAtendimentosVO.Fields.ESTADO_SAUDE.toString());

		return sql.toString();
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final Object parametro, final Integer maxResults) {
		String descricao = null;
		Integer codigo = null;

		if (parametro instanceof String) {
			descricao = (String) parametro;
		}

		if (parametro instanceof Integer) {
			codigo = (Integer) parametro;
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (descricao != null) {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo));
		}

		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, 0, maxResults, null, false);
	}

	public List<Object[]> pesquisaInformacoesUnidadesFuncionaisEscalaCirurgias(final Short seq) {
		final DetachedCriteria unf = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		unf.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seq));
		unf.setProjection(Projections.projectionList().add(Projections.property(AghUnidadesFuncionais.Fields.ANDAR.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.ALA.toString())));
		return executeCriteria(unf);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(final Object objPesquisa,
			final boolean ordernarPorCodigoAlaDescricao, final boolean apenasAtivos, final Object[] caracteristicas) {
		return pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(objPesquisa,
				ordernarPorCodigoAlaDescricao ? AghUnidadesFuncionaisDAO.ORDERNAR_POR_CODIGO_ALA_DESCRICAO : null, apenasAtivos, caracteristicas);
	}

	@SuppressWarnings({ "unchecked", "PMD.NPathComplexity" })
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(final Object objPesquisa, final String ordernar,
			final boolean apenasAtivos, final Object[] caracteristicas) {
		final String strPesquisa = (String) objPesquisa;
		boolean flag = false;
		boolean ordernarPorCodigoAlaDescricao = AghUnidadesFuncionaisDAO.ORDERNAR_POR_CODIGO_ALA_DESCRICAO.equals(ordernar);
		boolean ordernarPorAndar = AghUnidadesFuncionaisDAO.ORDERNAR_POR_ANDAR.equals(ordernar);

		final StringBuilder sql = new StringBuilder(500);
		sql.append(" select distinct uf ");
		if (ordernarPorCodigoAlaDescricao) {
			sql.append(", uf.andar || ' ' || ala.codigo || ' - ' || uf.descricao as ordenador ");
		}

		sql.append(" from AghUnidadesFuncionais uf left join fetch uf.indAla ala WHERE 1=1 ");
		if (StringUtils.isNotBlank(strPesquisa)) {
			sql.append(" AND lower('0' || uf.andar || ' ' || ala.codigo || ' - ' || uf.descricao) like lower('%' || :strPesquisa || '%') ");
			flag = true;
		}
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa) && flag) {
			sql.append(" OR ");
		} else {
			if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa) && !flag) {
				sql.append(" AND ");
			}
		}
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			sql.append(" uf.seq = :seq");
		}
		sql.append(" and uf.seq in ( ");
		sql.append(" select cuf.id.unfSeq from AghCaractUnidFuncionais cuf");
		sql.append(" where 1 = 1 ");
		if (caracteristicas != null && caracteristicas.length > 0) {
			sql.append(" and cuf.id.caracteristica in ( :caract )");
		}
		sql.append(" )");

		if (apenasAtivos) {
			sql.append(" and uf.indSitUnidFunc = :situacao ");
		}

		if (ordernarPorCodigoAlaDescricao) {
			sql.append(" order by uf.andar || ' ' || ala.codigo || ' - ' || uf.descricao ");
		} else if (ordernarPorAndar) {
			sql.append(" order by uf.andar ");
		} else {
			sql.append(" order by trim(uf.descricao) ");
		}

		final javax.persistence.Query query = this.createQuery(sql.toString());

		if (apenasAtivos) {
			query.setParameter("situacao", DominioSituacao.A);
		}

		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			query.setParameter("seq", Short.valueOf(strPesquisa));
		}

		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("strPesquisa", strPesquisa);
		}

		if (caracteristicas != null && caracteristicas.length > 0) {
			query.setParameter("caract", Arrays.asList(caracteristicas));
		}

		final List<AghUnidadesFuncionais> objetos = new ArrayList<AghUnidadesFuncionais>();

		if (ordernarPorCodigoAlaDescricao) {
			for (final Object[] objeto : (List<Object[]>) query.getResultList()) {
				objetos.add((AghUnidadesFuncionais) objeto[0]);
			}
		}

		if (!ordernarPorCodigoAlaDescricao) {
			return query.getResultList();
		} else {
			return objetos;
		}
	}

	/**
	 * Método para retornar Unidades Funcionais cadastradas que possuam unidades
	 * funcionais, que a característica da unidade seja igual a Unid Internacao
	 * ou Unid Emergencia e que o parametro passado seja igual ao id da unidade
	 * funcional ou seja encontrado na string formada pelo andar + ala +
	 * descricao da unidade.
	 * 
	 * @return Lista com Unidades Funcionais
	 */
	@SuppressWarnings({"unchecked", "PMD.NPathComplexity"})
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(final Object objPesquisa,
			final boolean ordernarPorCodigoAlaDescricao, final boolean apenasAtivos, final Object[] caracteristicas) {
		final String strPesquisa = (String) objPesquisa;
		boolean flag = false;

		final StringBuilder sql = new StringBuilder(600);
		sql.append(" select distinct uf ");
		if (ordernarPorCodigoAlaDescricao) {
			sql.append(", uf.andar || ' ' || uf.indAla.codigo || ' - ' || uf.descricao as ordenador ");
		}

		sql.append(" from AghUnidadesFuncionais uf WHERE 1=1 ");

		if (StringUtils.isNotBlank(strPesquisa)) {
			sql.append(" AND ( lower('0' || uf.andar || ' ' || uf.indAla.codigo || ' - ' || uf.descricao) like lower('%' || :strPesquisa || '%') ");
			flag = true;
		}
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa) && flag) {
			sql.append(" OR ");
		} else {
			if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa) && !flag) {
				sql.append(" AND ( ");
			}
		}
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			sql.append(" uf.seq = :seq )");
		} else {
			if (flag) {
				sql.append(" )");
			}
		}
		sql.append(" and uf.seq in ( ")
		.append(" select uf2.unfSeq.seq from AghUnidadesFuncionais uf2")
		.append(" where uf2.unfSeq != null")
		.append(" and uf.seq in (")
		.append(" select cuf.id.unfSeq from AghCaractUnidFuncionais cuf")
		.append(" where 1 = 1 ");
		if (caracteristicas != null && caracteristicas.length > 0) {
			sql.append(" and cuf.id.caracteristica in ( :caract )");
		}
		sql.append(" )")
		.append(" and uf2.unfSeq.seq = uf.seq ")
		.append(" )");

		if (apenasAtivos) {
			sql.append(" and uf.indSitUnidFunc = :situacao ");
		}

		if (ordernarPorCodigoAlaDescricao) {
			sql.append(" order by uf.andar || ' ' || uf.indAla.codigo || ' - ' || uf.descricao ");
		} else {
			sql.append(" order by trim(uf.descricao) ");
		}

		final javax.persistence.Query query = this.createQuery(sql.toString());
		if (apenasAtivos) {
			query.setParameter("situacao", DominioUnidFunc.A);
		}
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			query.setParameter("seq", Short.valueOf(strPesquisa));
		}
		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("strPesquisa", strPesquisa);
		}
		if (caracteristicas != null && caracteristicas.length > 0) {
			query.setParameter("caract", Arrays.asList(caracteristicas));
		}

		query.setFirstResult(0);
		query.setMaxResults(25);

		final List<AghUnidadesFuncionais> objetos = new ArrayList<AghUnidadesFuncionais>();

		if (ordernarPorCodigoAlaDescricao) {
			for (final Object[] objeto : (List<Object[]>) query.getResultList()) {
				objetos.add((AghUnidadesFuncionais) objeto[0]);
			}
		}

		if (!ordernarPorCodigoAlaDescricao) {
			return query.getResultList();
		} else {
			return objetos;
		}
	}

	/**
	 * Método que pesquisa por código ou descrição de Unidade Funcional.
	 * 
	 * @param filtro
	 * @return Lista de <code>AghUnidadesFuncionais</code>.
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricao(final String filtro) {

		final DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		cri.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());

		// Se for número pesquida por código = chave primária. Caso contrário
		// pesquisa por descrição.
		if (CoreUtil.isNumeroShort(filtro)) {
			cri.add(Restrictions.idEq(Short.valueOf(filtro)));
		} else {
			cri.add(Restrictions.ilike("descricao", filtro, MatchMode.ANYWHERE));
		}

		cri.add(Restrictions.eq(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "."
				+ AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));

		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));

		return executeCriteria(cri);
	}
	
	/**
	 * Método que pesquisa por código ou descrição Unidades Funcionais Ativas dos tipos Executora ou de Exames
	 * 
	 * @param filtro
	 * @return Lista de <code>AghUnidadesFuncionais</code>.
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasExecutoraColeta(final Object filtro) {

		final DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		cri.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));

		cri.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());

		// Se for número pesquida por código = chave primária. Caso contrário pesquisa por descrição.
		if (CoreUtil.isNumeroShort(filtro)) {
			cri.add(Restrictions.idEq(Short.valueOf(filtro.toString())));
		} else {
			cri.add(Restrictions.ilike("descricao", filtro.toString(), MatchMode.ANYWHERE));
		}

		Criterion lhs = Restrictions.eq(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,
				ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES);
		Criterion rhs = Restrictions.eq(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,
				ConstanteAghCaractUnidFuncionais.UNID_COLETA);

		cri.add(Restrictions.or(lhs, rhs));

		cri.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

		return executeCriteria(cri);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(Object filtro,
			final Object[] caracteristicas) {

		final DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		cri.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
		cri.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));

		// Se for número pesquida por código = chave primária. Caso contrário
		// pesquisa por descrição.
		if (CoreUtil.isNumeroShort(filtro) && CoreUtil.isNumeroByte(filtro)) {
			cri.add(Restrictions.or(Restrictions.idEq(Short.valueOf(filtro.toString())), Restrictions.or(
					Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), filtro.toString(), MatchMode.ANYWHERE),
					Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), filtro.toString()))));
		} else if (CoreUtil.isNumeroShort(filtro)) {
			cri.add(Restrictions.or(Restrictions.idEq(Short.valueOf(filtro.toString())),
					Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), filtro.toString(), MatchMode.ANYWHERE)));

		} else if (!StringUtils.isEmpty((String) filtro)) {
			cri.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), filtro.toString(), MatchMode.ANYWHERE));
		}

		if (caracteristicas != null && caracteristicas.length > 0) {
			cri.add(Restrictions.in(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "."
					+ AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA, caracteristicas));
		}

		return executeCriteria(cri);
	}

	public Object[] obterAndarAlaPorSeq(final Short seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.setProjection(Projections.projectionList().add(Projections.property(AghUnidadesFuncionais.Fields.ANDAR.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.ALA.toString())));
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seq));
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}

	public AghUnidadesFuncionais obterUnidadeFuncionalPorSigla(final String sigla) {

		final DetachedCriteria cri = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		AghUnidadesFuncionais unidadeFuncional = null;

		if (sigla != null && !StringUtils.isBlank(sigla)) {
			cri.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SIGLA.toString(), sigla));
			unidadeFuncional = (AghUnidadesFuncionais) executeCriteriaUniqueResult(cri);
		}

		return unidadeFuncional;
	}

	/**
	 * Lista de Unidades Funcionais ativas e associadas às Unid
	 * executora Unid coleta e Central Recebimento Materiais
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasPorSeqDescricao(String parametro) {
		final DetachedCriteria criteria = montarCriteriaSuggestionUnidExecutora(parametro);
		List<AghUnidadesFuncionais> result = montarAghUnidadesFuncionaisProjection(criteria);

		return result;
	}

    public List<AghUnidadesFuncionais> obterUnidadesFuncionaisListaUnidadesSolicitacao(String parametro, List<Short> seqUnidades){
        final DetachedCriteria criteria = montarCriteriaSuggestionUnidExecutora(parametro);

        criteria.add(Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnidades));

        List<AghUnidadesFuncionais> result = montarAghUnidadesFuncionaisProjection(criteria);

        return result;
    }

    private DetachedCriteria montarCriteriaSuggestionUnidExecutora(String parametro){
        final DetachedCriteria criteria = montarCriteriaParaDescricaoSiglaOuCodigo(parametro, true);

        DetachedCriteria caracteristicaCriteria = null;

        criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
        caracteristicaCriteria = criteria.createCriteria(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
        caracteristicaCriteria.add(Restrictions.in(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), new Object[] {
                ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES, ConstanteAghCaractUnidFuncionais.UNID_COLETA,
                ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS }));
        criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

        return criteria;
    }

	private List<AghUnidadesFuncionais> montarAghUnidadesFuncionaisProjection(DetachedCriteria criteria) {
		List<Object[]> listaArrayObject = executeCriteria(criteria); 

		//Busca lista de ids e depois busca por criteria para manter os objetos atachados
		if(listaArrayObject != null && !listaArrayObject.isEmpty()) {
			List<Short> listaIds = new ArrayList<Short>();
			for (Object[] diagArrayObject : listaArrayObject) {
				listaIds.add((Short) diagArrayObject[0]);
			}
			DetachedCriteria criteriaIn = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
			criteriaIn.createAlias(AghUnidadesFuncionais.Fields.ALA.toString(), ALA, JoinType.LEFT_OUTER_JOIN);
			criteriaIn.add(Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), listaIds));
			
			return executeCriteria(criteriaIn);
			
		}
		return new ArrayList<AghUnidadesFuncionais>();
	}

	public List<AghUnidadesFuncionais> obterUnidadesFuncionais(final Object param) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		final String strPesquisa = (String) param;
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		validarFiltroSeqDescricao(criteria, strPesquisa);
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public List<AghUnidadesFuncionais> obterUnidadesExecutora(final Object param) {

		final DetachedCriteria criteria = getCriteriaObterUnidadesExecutora(param);
		return executeCriteria(criteria, 0, 100, "seq", true);
	}

	private DetachedCriteria getCriteriaObterUnidadesExecutora(
			final Object param) {
		final DetachedCriteria criteriaIn = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);

		criteriaIn.setProjection(Projections.property(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		criteriaIn.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),
				ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA));
		final String strPesquisa = (String) param;

		List<Short> unfSeqsPME = new ArrayList<Short>();

		unfSeqsPME = executeCriteria(criteriaIn);

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		criteria.add(Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeqsPME));
		validarFiltroSeqDescricao(criteria, strPesquisa);
		return criteria;
	}
	
	public Long obterUnidadesExecutoraCount(final Object param) {
		final DetachedCriteria criteria = getCriteriaObterUnidadesExecutora(param);
		return executeCriteriaCount(criteria);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(final Object parametro,
			final AghTiposUnidadeFuncional aghTiposUnidadeFuncional, final Integer maxResult) {
		final DetachedCriteria criteria = this.obterCriteriaUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(parametro, aghTiposUnidadeFuncional);
		
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, maxResult, null, false);
	}

	private DetachedCriteria obterCriteriaUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(final Object parametro,
			final AghTiposUnidadeFuncional aghTiposUnidadeFuncional) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (CoreUtil.isNumeroShort(parametro)) {
			if (parametro instanceof Short) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), parametro));
			} else {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf((String) parametro)));
			}
		} else {
			if (!StringUtils.isEmpty((String) parametro)) {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), (String) parametro, MatchMode.ANYWHERE));
			}
		}

		if (aghTiposUnidadeFuncional != null) {
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.TIPO_UNIDADE_FUNCIONAL_SEQ.toString(), aghTiposUnidadeFuncional));
		}
		return criteria;
	}
	
	public List<Short> obterUnidadesFuncionaisHierarquicasPorCaract2(Short seq, ConstanteAghCaractUnidFuncionais cacuf) {

		final StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT UNF.UNF_SEQ")
		.append(" FROM ")
		.append(" agh. ").append(AghUnidadesFuncionais.class.getAnnotation(Table.class).name()).append(" UNF ")
		.append(" WHERE 1=1 ");
		
		
		sql.append(" AND 'S' = ( ")
		   			   .append(" CASE WHEN  ( ")
						   			   .append(" SELECT UF_IN.UNF_SEQ ")
						   			   .append("  FROM agh.").append(AghCaractUnidFuncionais.class.getAnnotation(Table.class).name()).append(" UF_IN ")
						   			   .append("  WHERE 1=1 ")
						   			   .append("    AND UF_IN.UNF_SEQ = UNF.UNF_SEQ " )
						   			   .append("    AND UF_IN.CARACTERISTICA =  :PRM_CARACTERISTICA " )
		   			   				.append(") IS NOT NULL THEN 'S' ")
		   			   				.append(" ELSE 'N' END ")
		   			 .append(" ) ");
		   			   
		String restricao;
		
		if(isOracle()){
			restricao = " connect by prior UNF."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = UNF.unf_seq" +
				" start with UNF."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = "+ seq;
		}
		else {
			restricao = " AND UNF.unf_seq in ( WITH RECURSIVE n("+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , unf_seq) AS"+
				" (select u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , u.unf_seq"+
				" from agh.AGH_UNIDADES_FUNCIONAIS u"+
				" where u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = " + seq +
				" UNION ALL " +
				" select n1." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , n1.unf_seq" +
				" from agh.AGH_UNIDADES_FUNCIONAIS n1, n"+
				" where n."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() + 
				" = n1.unf_seq" + 
				" )" +
				" SELECT unf_seq FROM n )";
		}
		
		sql.append(restricao);
		
		javax.persistence.Query query = this.createNativeQuery(sql.toString());
		query.setParameter("PRM_CARACTERISTICA", cacuf.getCodigo());	

		List<Short> unfList = new LinkedList<Short>();
		
		if(isOracle()){
			@SuppressWarnings("unchecked")
			List<BigDecimal> result = (List<BigDecimal>) query.getResultList();
			for (BigDecimal obj : result) {
				unfList.add(obj.shortValue());
			}
			
		} else {
			@SuppressWarnings("unchecked")
			List<Short> result = (List<Short>) query.getResultList();
			for (Short obj : result) {
				unfList.add(obj);
			}
		}
		
		return unfList;
	}
	

	/**
	 * Obtem as Unidades Funcionais Hierarquicas Por Característica
	 * 
	 * @param seq - código da unidade funcional
	 * @param cacuf - Constante Agh Característica Unidade Funcional
	 */
	public List<Short> obterUnidadesFuncionaisHierarquicasPorCaract(Short seq, ConstanteAghCaractUnidFuncionais cacuf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
	
		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CARACTERISTICAS");
		criteria.setProjection(Projections.distinct(Projections.property(AghUnidadesFuncionais.Fields.UNF_SEQ.toString())));
		criteria.add(Restrictions.eq("CARACTERISTICAS"
				+ "."
				+ AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), cacuf));
		
		String restricao;
		
		if(isOracle()){
			restricao = " 1=1 connect by prior {alias}."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = {alias}.unf_seq" +
				" start with {alias}."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = "+ seq;
		}
		else {
			restricao = " {alias}.unf_seq in ( WITH RECURSIVE n("+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , unf_seq) AS"+
				" (select u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , u.unf_seq"+
				" from agh.AGH_UNIDADES_FUNCIONAIS u"+
				" where u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = " + seq +
				" UNION ALL " +
				" select n1." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , n1.unf_seq" +
				" from agh.AGH_UNIDADES_FUNCIONAIS n1, n"+
				" where n."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() + 
				" = n1.unf_seq" + 
				" )" +
				" SELECT unf_seq FROM n )";
		}
		
		criteria.add(Restrictions.sqlRestriction(restricao));
		
		List<Short> listReturn = new ArrayList<Short>();
		
		List<AghUnidadesFuncionais> listResult = executeCriteria(criteria);

		for (AghUnidadesFuncionais aghUnidadesFuncionais : listResult) {
			listReturn.add(aghUnidadesFuncionais.getSeq());
		}				

		return listReturn;
	}

	public List<Short> listarUnidadesFuncionaisPorUnfSeq(List<Short> listUnfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class,"UNF");
		
		criteria.setProjection(Projections.projectionList().add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())));
		criteria.add(Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString(), listUnfSeq));
		
		List<Short> seqs =  this.executeCriteria(criteria);
		
		return seqs;
	}
	
	/**
	 * Q_INTERNADO  : indpacAtendimento = S, nroDias = NULL
	 * Q_ALTA :indpacAtendimento = N && nroDias = NULL
	 * Q_ALTA_RESTRICAO  :indpacAtendimento = N && nroDias != NULL
	 * 
	 * @param indPacAtendimento
	 * @param nroDias
	 * @param pacCodigo
	 * 
	 * @return List AghUnidadesFuncionais 
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPaciente(DominioPacAtendimento indpacAtendimento, Integer nroDias, Integer pacCodigo, Boolean centroDeCustoNotNull){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "unf");
		
		criteria.createAlias("unf.".concat(AghUnidadesFuncionais.Fields.ATENDIMENTO.toString()), "atd");
		
		criteria.add(Restrictions.eq("atd.".concat(AghAtendimentos.Fields.ORIGEM.toString()), DominioOrigemAtendimento.I));
		criteria.add(Restrictions.eq("atd.".concat(AghAtendimentos.Fields.PAC_CODIGO.toString()), pacCodigo));
		
		if(indpacAtendimento != null){
			criteria.add(Restrictions.eq("atd.".concat(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString()), indpacAtendimento));
		}
				
		//atende Q_ALTA
		if(indpacAtendimento.name().equals("N")  && nroDias == null) {
			criteria.add(Restrictions.ge("atd.".concat(AghAtendimentos.Fields.DTHR_FIM.toString()), DateUtil.obterDataComHoraInical(new Date())));
			criteria.add(Restrictions.le("atd.".concat(AghAtendimentos.Fields.DTHR_FIM.toString()), DateUtil.obterDataComHoraFinal(new Date())));
		}
		
		//atende Q_ALTA_RESTRICAO
		if(indpacAtendimento.name().equals("N") && nroDias != null) {
			criteria.add(Restrictions.ge("atd.".concat(AghAtendimentos.Fields.DTHR_FIM.toString()), DateUtil.obterDataComHoraInical(DateUtil.adicionaDias(DateUtil.truncaData(new Date()), (-1)*nroDias))));
			criteria.add(Restrictions.le("atd.".concat(AghAtendimentos.Fields.DTHR_FIM.toString()), DateUtil.obterDataComHoraFinal(new Date())));
		}
		
		if(centroDeCustoNotNull){
			criteria.add(Restrictions.isNotNull("unf.".concat(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())));
		}
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Q_CIRURGIA_AMB
	 * Q_CIRURGIA_AMB_RESTRICAO
	 * 
	 * @param pacCodigo
	 * @param nroDiasFuturo
	 * @param nroDiasPassado
	 * 
	 * @return List AghUnidadesFuncionais 
	 */
	public List<AghUnidadesFuncionais> pesquisarCirurgiasUnidadesFuncionais(Integer pacCodigo, Integer nroDiasFuturo, Integer nroDiasPassado) {
		
		if(nroDiasPassado == null) {
			nroDiasPassado = 0;
		}
		if(nroDiasFuturo == null) {
			nroDiasFuturo = 0;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "unf");
		criteria.createAlias("unf.".concat(AghUnidadesFuncionais.Fields.MBC_CIRURGIAS.toString()), "crg");
		
		criteria.add(Restrictions.eq("crg.".concat(MbcCirurgias.Fields.PAC_CODIGO.toString()), pacCodigo));
		criteria.add(Restrictions.ne("crg.".concat(MbcCirurgias.Fields.SITUACAO.toString()), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.ge("crg.".concat(MbcCirurgias.Fields.DATA.toString()), DateUtil.adicionaDias(DateUtil.obterDataComHoraInical(new Date()), (-1)*nroDiasPassado)));
		criteria.add(Restrictions.le("crg.".concat(MbcCirurgias.Fields.DATA.toString()), DateUtil.adicionaDias(DateUtil.obterDataComHoraFinal(new Date()), nroDiasFuturo)));
		criteria.add(Restrictions.isNotNull("unf.".concat(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Lista todas as unidades funcionais que contenham salas
	 * 
	 * @param parametro
	 * @return lista de unidades funcionais
	 */
	public List<AghUnidadesFuncionais> listarUnidadeFuncionalComSala(final String parametro) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		if (StringUtils.isNotBlank(parametro)) {
			Criterion criterionDescCodigo = null;
			if (CoreUtil.isNumeroShort(parametro)) {
				final short codigo = Short.parseShort(parametro);
				criterionDescCodigo = Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo);
			} else {
				criterionDescCodigo = Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE);
			}
			criteria.add(criterionDescCodigo);
		}

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(AelSalasExecutorasExames.class);
		subCriteria.setProjection(Projections.property(AelSalasExecutorasExames.Fields.UNF_SEQ.toString()));
		criteria.add(Property.forName(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).in(subCriteria));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	

	
	/**
	 * @see Q_CONSULTA utilizar sem as datas de passado e futuro
	 * @see Q_CONSULTA_RESTRICAO utilizar passando as datas de passado e futuro
	 * 
	 * @param pacCodigo, nroDiasPassado, nroDiasFuturo
	 * @return Lista Unidades Funcionais
	 */
	public List<AghUnidadesFuncionais> pesquisarConsultasPaciente(Integer pacCodigo, Integer nroDiasFuturo, 
			Integer nroDiasPassado, Integer paramReteronoConsAgendada) {
		
		if(nroDiasFuturo == null) {
			nroDiasFuturo = 0;
		}
		if(nroDiasPassado == null) {
			nroDiasPassado = 0;
		} 
		if (paramReteronoConsAgendada == null){
			paramReteronoConsAgendada = -1;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "unf");
		
		criteria.createAlias("unf.".concat(AghUnidadesFuncionais.Fields.AAC_GRADE_AGENDAMEN_CONSULTAS.toString()), "grd");
		criteria.createAlias("grd.".concat(AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString()), "con");
		criteria.createAlias("con.".concat(AacConsultas.Fields.RETORNO.toString()), "ret");
		
		criteria.add(Restrictions.eq("con.".concat(AacConsultas.Fields.PAC_CODIGO.toString()), pacCodigo));
		criteria.add(Restrictions.or(Restrictions.eq("ret.".concat(AacRetornos.Fields.IND_ABSENTEISMO.toString()), DominioIndAbsenteismo.R),
									Restrictions.eq("ret.".concat(AacRetornos.Fields.SEQ.toString()), paramReteronoConsAgendada)));
		criteria.add(Restrictions.ge("con.".concat(AacConsultas.Fields.DATA_CONSULTA.toString()), DateUtil.adicionaDias(DateUtil.obterDataComHoraInical(new Date()), (-1)*nroDiasPassado)));
		criteria.add(Restrictions.le("con.".concat(AacConsultas.Fields.DATA_CONSULTA.toString()), DateUtil.adicionaDias(DateUtil.obterDataComHoraFinal(new Date()), nroDiasFuturo)));		
		criteria.add(Restrictions.isNotNull("unf.".concat(AghUnidadesFuncionais.Fields.CENTRO_CUSTO_CODIGO.toString())));
		
		return executeCriteria(criteria);
	}
	
	

	public List<Short> obterUnidadesFuncionaisHierarquicasPorCaractCentralRecebimento(Short seq) {
		
		final StringBuilder hql = new StringBuilder(200);
		hql.append("SELECT UF.unf_Seq ");
		hql.append(" from ");
		hql.append(" AGH.AGH_UNIDADES_FUNCIONAIS UF ");
		hql.append(", AGH.AGH_CARACT_UNID_FUNCIONAIS CA ");
		hql.append(" WHERE CA.CARACTERISTICA ");
		hql.append(" like :descCaract ");
		hql.append(" and UF.SEQ=CA.UNF_SEQ ");
				
		String restricao;
		
		if(isOracle()){
			restricao = " connect by prior UF."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = UF.unf_seq" +
				" start with UF."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = "+ seq;
		}
		else {
			restricao = " AND UF.unf_seq in ( WITH RECURSIVE n("+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , unf_seq) AS"+
				" (select u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , u.unf_seq"+
				" from agh.AGH_UNIDADES_FUNCIONAIS u"+
				" where u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = " + seq +
				" UNION ALL " +
				" select n1." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , n1.unf_seq" +
				" from agh.AGH_UNIDADES_FUNCIONAIS n1, n"+
				" where n."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() + 
				" = n1.unf_seq" + 
				" )" +
				" SELECT unf_seq FROM n )";
		}
		
		hql.append(restricao);
		
		javax.persistence.Query query = this.createNativeQuery(hql.toString());
		query.setParameter("descCaract", ConstanteAghCaractUnidFuncionais.CENTRAL_RECEBIMENTO_MATERIAIS.getCodigo());	

		@SuppressWarnings("unchecked")
		List<Short> result = query.getResultList();
		List<Short> unfList = new LinkedList<Short>();
		for (Short obj : result) {
			unfList.add(obj);
		}
		
		return unfList;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqSigla(Object parametro) {		
		final DetachedCriteria criteria = criarCriteriaPesquisarUnidadeFuncionalPorSeqSigla(parametro);			
		return executeCriteria(criteria);
	}
	
	public Long pesquisarUnidadeFuncionalPorSeqSiglaCount(Object parametro) {
		final DetachedCriteria criteria = criarCriteriaPesquisarUnidadeFuncionalPorSeqSigla(parametro);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria criarCriteriaPesquisarUnidadeFuncionalPorSeqSigla(Object parametro) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		
		final String strPesquisa = (String) parametro;		
		if (StringUtils.isNotBlank(strPesquisa)) {				
			if (CoreUtil.isNumeroShort(StringUtils.trimToNull(parametro.toString()))) {
				final Short paramShort = Short.valueOf(StringUtils.trimToNull(parametro.toString()));
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), paramShort));
			} else {				
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SIGLA.toString(), strPesquisa));		
			}
		}
		return criteria;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorDescricao(Object parametro) {
		final DetachedCriteria criteria = criarCriteriaPesquisarUnidadeFuncionalPorDescricao(parametro);
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarUnidadeFuncionalPorDescricaoCount(Object parametro) {
		final DetachedCriteria criteria = criarCriteriaPesquisarUnidadeFuncionalPorDescricao(parametro);	
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaPesquisarUnidadeFuncionalPorDescricao(Object parametro) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);	
		
		final String strPesquisa = (String) parametro;		
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));	
		}
		return criteria;
	}

	/**
	 * Metodo utilizado para alimentar Suggestion Box da #5969.
	 * @param param
	 * @return
	 */
	
    public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqDescricao(Object param, Boolean somenteAtivos) {
    	DetachedCriteria dc = montarCriteriaUnidadeFuncionalPorSeqDescricao(param, somenteAtivos);
        dc.addOrder(Order.asc("UNF.".concat(AghUnidadesFuncionais.Fields.DESCRICAO.toString())));
        return executeCriteria(dc);        
    }

    public Long pesquisarUnidadeFuncionalPorSeqDescricaoCount(Object param, Boolean somenteAtivos) {
        DetachedCriteria dc = montarCriteriaUnidadeFuncionalPorSeqDescricao(param, somenteAtivos);
        return executeCriteriaCount(dc);
    }
	
	private DetachedCriteria montarCriteriaUnidadeFuncionalPorSeqDescricao(Object param, Boolean somenteAtivos) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "UNF");
		
		dc.createAlias("UNF.".concat(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString()), "CUF");
		
		dc.add(Restrictions.eq("CUF.".concat(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString()), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES));
		
		if (somenteAtivos) {
			
			dc.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));	
			
		}

		
		if (CoreUtil.isNumeroShort(param)) {
			dc.add(Restrictions.eq("UNF.".concat(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), Short.parseShort(param.toString())));
		} else if (param != null && StringUtils.isNotBlank(param.toString())) {
			dc.add(Restrictions.ilike("UNF.".concat(AghUnidadesFuncionais.Fields.DESCRICAO.toString()), param.toString(), MatchMode.ANYWHERE));
		}
		return dc;	
	}
	
	public Long pesquisarUnidadesExecutorasPorCodigoOuDescricaoCount(
			Object parametro) {

		AghUnidadesFuncionais elemento = new AghUnidadesFuncionais();
		Long total = null;
		// Primeiro tenta buscar por código
		if (CoreUtil.isNumeroShort(parametro)) {
			elemento.setSeq(Short.parseShort(parametro.toString()));
			DetachedCriteria criteria = criarCriteria(elemento, false);
			total = executeCriteriaCount(criteria);
		}
		if (total == null) {

			elemento = new AghUnidadesFuncionais();
			elemento.setDescricao((String) parametro);

			DetachedCriteria criteria = criarCriteria(elemento, false);
			total = executeCriteriaCount(criteria);
		}
		return total;
	}
	
	public AghUnidadesFuncionais obterUnidadeFuncionalComCaracteristica(final Short seq) {
		DetachedCriteria criteria = this.obterCriteriaUnidades();
		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), 
				AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seq));
		AghUnidadesFuncionais unidadeFuncional = (AghUnidadesFuncionais) executeCriteriaUniqueResult(criteria);
		for (AghCaractUnidFuncionais caractUnidFuncionais : unidadeFuncional.getCaracteristicas()) {
			Hibernate.initialize(caractUnidFuncionais);
		}
		return unidadeFuncional;
	}

	/**
	 * Pesquisa Unidades funcionais através do código, descrição e característica da unidade funcional
	 * @param filtro
	 * @param caracteristicaUnideFuncional
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristica(final String filtro, final ConstanteAghCaractUnidFuncionais[] caracteristicasUnideFuncional, final DominioSituacao situacao, Boolean order) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
		
		if (situacao != null){
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}

		// Se for número pesquida por código = chave primária. Caso contrário pesquisa por descrição.
		if (CoreUtil.isNumeroShort(filtro)) {
			criteria.add(Restrictions.idEq(Short.valueOf(filtro)));
		} else {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		}

		// 
		criteria.add(Restrictions.in(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,
				caracteristicasUnideFuncional));

		if(order){
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		}

		return executeCriteria(criteria);
	}
	
	
	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristicaCount(final String filtro, final ConstanteAghCaractUnidFuncionais[] caracteristicasUnideFuncional, final DominioSituacao situacao, Boolean order) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
		
		if (situacao != null){
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}

		// Se for número pesquida por código = chave primária. Caso contrário pesquisa por descrição.
		if (CoreUtil.isNumeroShort(filtro)) {
			criteria.add(Restrictions.idEq(Short.valueOf(filtro)));
		} else {
			criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.in(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,
				caracteristicasUnideFuncional));

		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * Pesquisa Unidades funcionais da unidade executora de cirurgias
	 * @param filtro código ou descrição
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(final Object filtro) {
		return pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgias(filtro, DominioSituacao.A);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgias(final Object filtro, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		
		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
		criteria = this.montarCriterioPesquisaUnidadesFuncionaisUnidadeExecutoraCirurgias(criteria, filtro, situacao);
		// Característica da unidade funcional deve ser a 
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,
				ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));

		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
	
	public Long pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgiasCount(final Object filtro, DominioSituacao situacao) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
        criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
        criteria = this.montarCriterioPesquisaUnidadesFuncionaisUnidadeExecutoraCirurgias(criteria, filtro, situacao);
        criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,
                        ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));
        return executeCriteriaCount(criteria);
    }
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasPorCaracteristica(final ConstanteAghCaractUnidFuncionais caractUnidFuncional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,
				caractUnidFuncional));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
    public Long pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(final Object filtro) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
        criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());
        criteria = this.montarCriterioPesquisaUnidadesFuncionaisUnidadeExecutoraCirurgias(criteria, filtro, DominioSituacao.A);
        criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString() + "." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,
                        ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));
        return executeCriteriaCount(criteria);
    }

	
	private DetachedCriteria montarCriterioPesquisaUnidadesFuncionaisUnidadeExecutoraCirurgias(DetachedCriteria criteria, Object filtro, DominioSituacao situacao) {
		// Se for número pesquida por código = chave primária. Caso contrário pesquisa por descrição.
		String strPesquisa = (String) filtro;
		if (CoreUtil.isNumeroShort(filtro)) {
			criteria.add(Restrictions.idEq(Short.valueOf(strPesquisa)));
		} else {
			//criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			criteria.add(Restrictions.or(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE), 
					Restrictions.ilike(AghUnidadesFuncionais.Fields.SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE))
					);
		}
		
		// Situação ativa
		if(situacao != null){
			criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisExecutoraCirurgias(final Object filtro, Boolean asc, String order) {
		DetachedCriteria criteria = getCriteriaPesquisarUnidadesFuncionaisExecutoraCirurgias(filtro);
		
		if(asc != null && order != null){
			if(asc){
				criteria.addOrder(Order.asc(order));
			}else{
				criteria.addOrder(Order.desc(order));
			}
		}
		
		return this.executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarUnidadesFuncionaisExecutoraCirurgias(
			final Object filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria = this.montarCriterioPesquisaUnidadesFuncionaisUnidadeExecutoraCirurgias(
				criteria, filtro, DominioSituacao.A);
		
		final DetachedCriteria subQuery = DetachedCriteria.forClass(AghCaractUnidFuncionais.class);
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName(AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		subQuery.setProjection(projection);
		subQuery.add(Restrictions.eq(AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), 
				ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));
		criteria.add(Property.forName(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).in(subQuery));
		return criteria;
	}
	
	public Long pesquisarUnidadesFuncionaisExecutoraCirurgiasCount(Object filtro){
		return executeCriteriaCount(getCriteriaPesquisarUnidadesFuncionaisExecutoraCirurgias(filtro));
	}
	
	public Short obterMenorTempoMinimoUnidadeFuncionalCirurgia(ConstanteAghCaractUnidFuncionais caractUnidFuncional) {
		String aliasUnf = "unf";
		String aliasCuf = "cuf";
		String separador = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, aliasUnf);
		criteria.createAlias(aliasUnf + separador + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), aliasCuf);
		criteria.add(Restrictions.eq(aliasCuf + separador + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caractUnidFuncional));
		criteria.setProjection(Projections.min(aliasUnf + separador + AghUnidadesFuncionais.Fields.TEMPO_MINIMO_CIRURGIA.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria criarCriteriaUnidadesFuncionaisPorUnidadeExecutora(
			Object filtro,
			ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais, Boolean somenteAtivos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "unf");

		criteria.createAlias("unf." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "unfCarac");
		if (constanteAghCaractUnidFuncionais != null) {
			criteria.add(Restrictions.eq("unfCarac." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA, constanteAghCaractUnidFuncionais));
		}
		if (somenteAtivos.equals(Boolean.TRUE)) {
			criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		}
		
		if (CoreUtil.isNumeroShort(filtro.toString())) {
			criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(filtro.toString())));
		} else if (StringUtils.isNotBlank(StringUtils.trim(filtro.toString()))) {
			criteria.add(Restrictions.ilike("unf." + AghUnidadesFuncionais.Fields.DESCRICAO.toString(), filtro.toString(), MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorCaracteristica(String filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais) {
		DetachedCriteria criteria = criarCriteriaUnidadesFuncionaisPorUnidadeExecutora(
				filtro, constanteAghCaractUnidFuncionais, true);
		
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())))
				.add(Projections.property(AghUnidadesFuncionais.Fields.SIGLA.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.ANDAR.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.ALA.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.DESCRICAO.toString())));
		
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()))
			.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ALA.toString()))
			.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		
		List<Object[]> listaArrayObject = executeCriteria(criteria, 0, 100, null, true);

		//Busca lista de ids e depois busca por criteria para manter os objetos atachados
		if(listaArrayObject != null && !listaArrayObject.isEmpty()) {
			List<Short> listaIds = new ArrayList<Short>();
			for (Object[] diagArrayObject : listaArrayObject) {
				listaIds.add((Short) diagArrayObject[0]);
			}
			DetachedCriteria criteriaIn = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
			criteriaIn.createAlias(AghUnidadesFuncionais.Fields.ALA.toString(), ALA, JoinType.LEFT_OUTER_JOIN);
			criteriaIn.add(Restrictions.in(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), listaIds));
			
			criteriaIn.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()))
				.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ALA.toString()))
				.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
			
			return executeCriteria(criteriaIn);
		}
		return new ArrayList<AghUnidadesFuncionais>();
	}
	
	public Long listarUnidadesFuncionaisPorCaracteristicaCount(String filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais) {
		DetachedCriteria criteria = criarCriteriaUnidadesFuncionaisPorUnidadeExecutora(
				filtro, constanteAghCaractUnidFuncionais, true);
		
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())))
				.add(Projections.property(AghUnidadesFuncionais.Fields.SIGLA.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.ANDAR.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.ALA.toString()))
				.add(Projections.property(AghUnidadesFuncionais.Fields.DESCRICAO.toString())));
		
		return (long) executeCriteria(criteria).size();
	}
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(Object filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais,
			Boolean somenteAtivos) {
		DetachedCriteria criteria = criarCriteriaUnidadesFuncionaisPorUnidadeExecutora(
				filtro, constanteAghCaractUnidFuncionais, somenteAtivos);
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		return executeCriteria(criteria);
	}
	
	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(Object filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais,
			Boolean somenteAtivos) {
		return executeCriteriaCount(criarCriteriaUnidadesFuncionaisPorUnidadeExecutora(filtro, constanteAghCaractUnidFuncionais, somenteAtivos));
	}
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorSeqDescricaoAndarAla(Object filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais, Boolean ativo) {
		DetachedCriteria criteria = montarCriteriaListarUnidadesFuncionaisPorSeqDescricaoAndarAla((String)filtro);
		restricaoListarUnidadesFuncionaisPorSeqDescricaoAndarAla(constanteAghCaractUnidFuncionais, ativo, criteria);
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		return executeCriteria(criteria);
	}
	
	public Long listarUnidadesFuncionaisPorSeqDescricaoAndarAlaCount(Object filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais, Boolean ativo) {
		DetachedCriteria criteria = montarCriteriaListarUnidadesFuncionaisPorSeqDescricaoAndarAla((String) filtro);
		restricaoListarUnidadesFuncionaisPorSeqDescricaoAndarAla(constanteAghCaractUnidFuncionais, ativo, criteria);
		return executeCriteriaCount(criteria);
	}

	private void restricaoListarUnidadesFuncionaisPorSeqDescricaoAndarAla(ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais, Boolean ativo, DetachedCriteria criteria) {
		if(ativo != null){ 
			criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.getInstance(ativo)));
		}
		if(constanteAghCaractUnidFuncionais != null){
			criteria.createAlias("unf." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "unfCarac");
			criteria.add(Restrictions.eq("unfCarac." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), constanteAghCaractUnidFuncionais));
		}
	}
	
	private DetachedCriteria montarCriteriaListarUnidadesFuncionaisPorSeqDescricaoAndarAla(final String parametro) {
		String descricaoOuCodigo = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "unf");
		
		if (StringUtils.isNotBlank(descricaoOuCodigo)) {
			String codigo = StringUtils.EMPTY;
			Criterion criterionDescCodigo = null;
			Criterion criterionAndar = null;
			if (CoreUtil.isNumeroShort(descricaoOuCodigo)) {
				codigo = descricaoOuCodigo;
			}
			if (StringUtils.isNotBlank(codigo)) {
				criterionDescCodigo = Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo);
				criterionAndar = Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), codigo);
			} else {
				criterionDescCodigo = Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricaoOuCodigo, MatchMode.ANYWHERE);
			}

			if (criterionAndar == null) {
				final String[] valoresAndarAla = StringUtils.split(descricaoOuCodigo);
				for (int i = 0; i < valoresAndarAla.length; i++) {
					final Criterion[] criterions = new Criterion[valoresAndarAla.length];
					String andar = StringUtils.EMPTY;
					if(CoreUtil.isNumeroShort(valoresAndarAla[i])&& Short.valueOf(valoresAndarAla[i])<= Byte.MAX_VALUE){
						andar = valoresAndarAla[i];	
					}
					if (StringUtils.isNotBlank(andar)) {
						criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.ANDAR.toString(), andar);
					} else {
						final AghAla ala = this.obterAghAlaPorId(valoresAndarAla[i]);
						if (ala != null) {
							criterions[i] = Restrictions.eq(AghUnidadesFuncionais.Fields.ALA.toString(), ala);
						}
					}
					if (criterions[i] == null) {
						criteria.add(criterionDescCodigo);
					} else {
						criterions[i] = Restrictions.or(criterionDescCodigo, criterions[i]);
						criteria.add(criterions[i]);
					}
				}
			} else {
				criteria.add(Restrictions.or(criterionDescCodigo, criterionAndar));
			}
		}
		return criteria;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasCirurgias(String objPesquisa) {
		DetachedCriteria criteria = montarCriteriaParaPesquisarUnidadesExecutorasCirurgias(objPesquisa);
		
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarCriteriaParaPesquisarUnidadesExecutorasCirurgias(String objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CUF");

		if (StringUtils.isNotBlank(objPesquisa)) {
			Criterion criterionDescCodigo = null;
			if (CoreUtil.isNumeroShort(objPesquisa)) {
				short codigo = Short.parseShort(objPesquisa);
				criterionDescCodigo = Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo);
			} else {
				criterionDescCodigo = Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE);
			}
			criteria.add(criterionDescCodigo);
		}

		criteria.add(Restrictions.eq("CUF." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));
		return criteria;
	}

	public Long pesquisarUnidadesExecutorasCirurgiasCount(String objPesquisa) {
		DetachedCriteria criteria = montarCriteriaParaPesquisarUnidadesExecutorasCirurgias(objPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	
	public DetachedCriteria montarCriteriaPesquisarUnidadeExecutoraMonitorCirurgia(final Object parametro){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		
		criteria = this.validaParametro(parametro, criteria);

		DetachedCriteria cun1 = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "CUN1");
		cun1.setProjection(Projections.property("CUN1." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		cun1.add(Property.forName("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).eqProperty("CUN1." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		ConstanteAghCaractUnidFuncionais[] caracteristicasCun1 = { ConstanteAghCaractUnidFuncionais.BLOCO, ConstanteAghCaractUnidFuncionais.CCA,
				ConstanteAghCaractUnidFuncionais.HEMODINAMICA };
		cun1.add(Restrictions.in("CUN1." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), caracteristicasCun1));

		DetachedCriteria cun2 = DetachedCriteria.forClass(AghCaractUnidFuncionais.class, "CUN2");
		cun2.setProjection(Projections.property("CUN2." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		cun2.add(Property.forName("CUN1." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()).eqProperty("CUN2." + AghCaractUnidFuncionais.Fields.UNF_SEQ.toString()));
		cun2.add(Restrictions.eq("CUN2." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS));

		cun1.add(Subqueries.exists(cun2));
		criteria.add(Subqueries.exists(cun1));
		return criteria;
	}
	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutoraMonitorCirurgia(final Object parametro) {
		return this.executeCriteria(this.montarCriteriaPesquisarUnidadeExecutoraMonitorCirurgia(parametro));
	}
	
	public Integer pesquisarUnidadeExecutoraMonitorCirurgiaCount(final Object parametro) {
		return this.pesquisarUnidadeExecutoraMonitorCirurgia(parametro).size();
	}

	// #27200 - Impressão das etiquetas de identificação relacionada ao paciente
	// C1
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqAndarAlaDescricao(String seqOuAndarAlaDescricao) {
		DetachedCriteria criteria = montarCriteriaParaDescricaoSiglaOuCodigo((String) seqOuAndarAlaDescricao, true);

		criteria.add(Restrictions.or(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_INTERNACAO.toString(), DominioSimNao.S),
				Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_EMERGENCIA.toString(), DominioSimNao.S)));
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));

		List<AghUnidadesFuncionais> lista = montarAghUnidadesFuncionaisProjection(criteria);
		
		class OrdenacaoAndarAlaDescricao implements Comparator<AghUnidadesFuncionais> {
			@Override
			public int compare(AghUnidadesFuncionais o1, AghUnidadesFuncionais o2) {
				return o1.getLPADAndarAlaDescricao().compareToIgnoreCase(o2.getLPADAndarAlaDescricao());
			}
		}
		Collections.sort(lista, new OrdenacaoAndarAlaDescricao());
		
		return lista;
	}

	// #27200 - Impressão das etiquetas de identificação relacionada ao paciente
	// C1 Count
	public Integer pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoCount(String seqOuAndarAlaDescricao) {
		return pesquisarUnidadeFuncionalPorSeqAndarAlaDescricao(seqOuAndarAlaDescricao).size();
	}

	// #27200 - Impressão das etiquetas de identificação relacionada ao paciente
	// C2
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMae(String seqOuAndarAlaDescricao) {
		DetachedCriteria criteria = montarCriteriaParaDescricaoSiglaOuCodigo((String) seqOuAndarAlaDescricao, true);

		criteria.createAlias(AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), "unf");
		criteria.add(Restrictions.or(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_INTERNACAO.toString(), DominioSimNao.S),
				Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_EMERGENCIA.toString(), DominioSimNao.S)));
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<AghUnidadesFuncionais> lista = montarAghUnidadesFuncionaisProjection(criteria);
		
		class OrdenacaoAndarAlaDescricao implements Comparator<AghUnidadesFuncionais> {
			@Override
			public int compare(AghUnidadesFuncionais o1, AghUnidadesFuncionais o2) {
				return o1.getLPADAndarAlaDescricao().compareToIgnoreCase(o2.getLPADAndarAlaDescricao());
			}
		}
		Collections.sort(lista, new OrdenacaoAndarAlaDescricao());

		return lista;
	}

	// #27200 - Impressão das etiquetas de identificação relacionada ao paciente
	// C2 Count
	public Integer pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMaeCount(String seqOuAndarAlaDescricao) {
		return pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMae(seqOuAndarAlaDescricao).size();
	}
	
	public List<Short> pesquisarUnidadeFuncionalTriagemRecepcao(
			List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) {
		
		final StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT UNF.SEQ")
		.append(" FROM ")
		.append(" agh.").append(AghUnidadesFuncionais.class.getAnnotation(Table.class).name()).append(" UNF ")
		.append(" WHERE 1=1 ");
		
		if(listaUnfSeqTriagemRecepcao != null && !listaUnfSeqTriagemRecepcao.isEmpty()) {
			sql.append(" AND UNF.SEQ IN ( :PRM_LISTA_UNF_SEQ )");
		}
		   			   
		String restricao;
		
		if (isOracle()) {
			restricao = "connect by prior UNF.UNF_SEQ" +
				" = UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" start with UNF."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = "+ unfSeqMicroComputador;
		}
		else {
			restricao = " AND UNF.unf_seq in ( WITH RECURSIVE n("+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , unf_seq) AS"+
				" (select u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , u.unf_seq"+
				" from agh.AGH_UNIDADES_FUNCIONAIS u"+
				" where u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = " + unfSeqMicroComputador +
				" UNION ALL " +
				" select n1." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , n1.unf_seq" +
				" from agh.AGH_UNIDADES_FUNCIONAIS n1, n"+
				" where n."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() + 
				" = n1.unf_seq" + 
				" )" +
				" SELECT unf_seq FROM n )";
		}
		sql.append(restricao);
		
		javax.persistence.Query query = this.createNativeQuery(sql.toString());
		if(listaUnfSeqTriagemRecepcao != null && !listaUnfSeqTriagemRecepcao.isEmpty()) {
			query.setParameter("PRM_LISTA_UNF_SEQ", listaUnfSeqTriagemRecepcao);
		}
		
		List<Short> unfList = new LinkedList<Short>();
		
		if(isOracle()){
			@SuppressWarnings("unchecked")
			List<BigDecimal> result = (List<BigDecimal>) query.getResultList();
			for (BigDecimal obj : result) {
				unfList.add(obj.shortValue());
			}
			
		} else {
			@SuppressWarnings("unchecked")
			List<Short> result = (List<Short>) query.getResultList();
			for (Short obj : result) {
				unfList.add(obj);
			}
		}
		
		return unfList;
	}
	
	public AghUnidadesFuncionais buscarUnidadeInternacaoAtiva(Short unfSeqAgendada){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		criteria.createAlias("UNF."+AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CUF");
		criteria.add(Restrictions.eq("UNF."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeqAgendada));
		criteria.add(Restrictions.eq("UNF."+AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("CUF."+AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO));
		return (AghUnidadesFuncionais) this.executeCriteriaUniqueResult(criteria, true);
	}
	/**
	 * Criteria de unidades funcionais ativas por descrição e código
	 * 
	 * Web Service #36153
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterCriteriaAtivasPorSeqOuDescricao(final String parametro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);

		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioUnidFunc.A));

		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	/**
	 * Buscar unidades funcionais ativas por descrição e código
	 * 
	 * Web Service #36153
	 * 
	 * @param parametro
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarAtivasPorSeqOuDescricao(final String parametro, int firstResult, int maxResults, String orderProperty,
			boolean asc) {
		final DetachedCriteria criteria = this.obterCriteriaAtivasPorSeqOuDescricao(parametro);
		return super.executeCriteria(criteria, firstResult, 100, orderProperty, asc);
	}

	/**
	 * Buscar count de unidades funcionais ativas por descrição e código
	 * 
	 * Web Service #36153
	 * 
	 * @param parametro
	 * @return
	 */
	public Long pesquisarAtivasPorSeqOuDescricaoCount(final String parametro) {
		final DetachedCriteria criteria = this.obterCriteriaAtivasPorSeqOuDescricao(parametro);
		return super.executeCriteriaCount(criteria);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesAtivas(final String strPesquisa, final boolean semEtiologia, final String tipo) {
		javax.persistence.Query query = montarQueryUnidadesAtivas(strPesquisa, semEtiologia, tipo);
		query.setFirstResult(0);
		query.setMaxResults(100);

		final List<AghUnidadesFuncionais> objetos = new ArrayList<AghUnidadesFuncionais>();

		for (final Object[] objeto : (List<Object[]>) query.getResultList()) {
			objetos.add((AghUnidadesFuncionais) objeto[0]);
		}
		return objetos;
	}
	
	public Long pesquisarUnidadesAtivasCount(final String strPesquisa, final boolean semEtiologia, final String tipo) {
		return Long.valueOf(montarQueryUnidadesAtivas(strPesquisa, semEtiologia, tipo).getResultList().size());
	}

	@SuppressWarnings({"unchecked", "PMD.NPathComplexity"})
	private javax.persistence.Query montarQueryUnidadesAtivas(
			String strPesquisa, boolean semEtiologia, String tipo) {
		boolean flag = false;

		final StringBuilder sql = new StringBuilder(500);
		sql.append(" select distinct uf ");
		sql.append(", uf.andar || ' ' || ala.codigo || ' - ' || uf.descricao as ordenador ");

		sql.append(" from AghUnidadesFuncionais uf left join uf.indAla ala WHERE 1=1 ");

		if (StringUtils.isNotBlank(strPesquisa)) {
			sql.append(" AND ( lower('0' || uf.andar || ' ' || ala.codigo || ' - ' || uf.descricao) like lower('%' || :strPesquisa || '%') ");
			flag = true;
		}
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa) && flag) {
			sql.append(" OR ");
		} else {
			if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa) && !flag) {
				sql.append(" AND ( ");
			}
		}
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			sql.append(" uf.seq = :seq )");
		} else {
			if (flag) {
				sql.append(" )");
			}
		}
		sql.append(" and uf.indSitUnidFunc = :situacao ");
		
		if (semEtiologia) {
			sql.append(" and uf.seq not in ( ");
			sql.append(" select mle.id.unfSeq ");
			sql.append(" from MciLocalEtiologia mle  WHERE 1=1 ");
			sql.append(" and mle.id.einTipo = :tipo) ");
		}

		sql.append(" order by uf.andar || ' ' || ala.codigo || ' - ' || uf.descricao ");

		final javax.persistence.Query query = this.createQuery(sql.toString());
		query.setParameter("situacao", DominioSituacao.A);
		if (semEtiologia) {
			query.setParameter("tipo", tipo);
		}
		
		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			query.setParameter("seq", Short.valueOf(strPesquisa));
		}
		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("strPesquisa", strPesquisa);
		}
		return query;
	}

	//#41079 - C1
	public List<AghUnidadesFuncionais> obterUnidadesFuncionaisSB(final Object param) {
		final DetachedCriteria criteria = criarCriteriaUnidadesFuncionaisSB(param);
		
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.ANDAR.toString()));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.IND_ALA.toString()));
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	private DetachedCriteria criarCriteriaUnidadesFuncionaisSB(
			final Object param) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		final String strPesquisa = (String) param;
		
		validarFiltroSeqDescricao(criteria, strPesquisa);
		return criteria;
	}

	private void validarFiltroSeqDescricao(final DetachedCriteria criteria,
			final String strPesquisa) {
		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.parseShort(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
	}

	public Long obterUnidadesFuncionaisSBCount(Object param) {
		final DetachedCriteria criteria = criarCriteriaUnidadesFuncionaisSB(param);
		return executeCriteriaCount(criteria);
	}
		
	/**
	 * Consulta as unidades funcionais por sigla ou descrição e filtra por característica zona_ambulatorio.
	 * @param parametro Valor para sigla ou descrição.
	 * @return Lista com unidades funcionais filtradas.
	 */
	public List<AghUnidadesFuncionais> obterCriteriaAtivaPorSiglaOuDescricaoECaracteristica(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
				
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioUnidFunc.A));
		String strParametro = StringUtils.trimToNull((String) parametro);
		if (StringUtils.isNotBlank(strParametro)) {
			criteria.add(Restrictions.or(Restrictions.ilike(AghUnidadesFuncionais.Fields.SIGLA.toString(), parametro.toString(), MatchMode.ANYWHERE),
					Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), parametro.toString(), MatchMode.ANYWHERE)));
		}	
		
		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CAR");
		criteria.add(Restrictions.in("CAR." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA, new Object[] {
				ConstanteAghCaractUnidFuncionais.ZONA_AMBULATORIO}));
		
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
				
		return this.executeCriteria(criteria, 0, 100, null);
	}

	public AghUnidadesFuncionais obterPorUnfSeq(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		criteria.add(Restrictions.eq("UNF."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		return (AghUnidadesFuncionais) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #5799
	 * C3 - Consulta para retornar a descrição da unidade funcional
	 */
	public AghUnidadesFuncionais obterValoresPrescricaoMedica(Short seqUnf){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		criteria.add(Restrictions.eq("UNF."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnf));
		return (AghUnidadesFuncionais) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #44179 - CURSOR - c_unf_cti_filhos
	 * @author marcelo.deus
	 */
	public List<Short> listarCursorUnfCtiFilhos(Integer pUnfPai){

		final StringBuilder sql = new StringBuilder(300);
		sql.append(" SELECT UNF.SEQ")
		.append(" FROM ")
		.append(" agh. ").append(AghUnidadesFuncionais.class.getAnnotation(Table.class).name()).append(" UNF ")
		.append(" WHERE ")
		.append(" UNF.IND_SIT_UNID_FUNC = 'A' ")
		.append(" AND UNF.UNF_SEQ IS NOT NULL");
		   			   
		String restricao;
			
		if(isOracle()){
			restricao = " connect by prior UNF."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = UNF.unf_seq" +
				" start with UNF."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
				" = " + pUnfPai;
		} else {
			restricao = " AND UNF.unf_seq in ( WITH RECURSIVE n("+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , unf_seq) AS"+
					" (select u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , u.unf_seq"+
					" from agh.AGH_UNIDADES_FUNCIONAIS u"+
					" where u."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +
					" = " + pUnfPai +
					" UNION ALL " +
					" select n1." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() +" , n1.unf_seq" +
					" from agh.AGH_UNIDADES_FUNCIONAIS n1, n"+
					" where n."+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString() + 
					" = n1.unf_seq" + 
					" )" +
					" SELECT unf_seq FROM n )";
		}
		
		sql.append(restricao);
		
		javax.persistence.Query query = this.createNativeQuery(sql.toString());

		List<Short> unfList = new LinkedList<Short>();
		
		if(isOracle()){
			@SuppressWarnings("unchecked")
			List<BigDecimal> result = (List<BigDecimal>) query.getResultList();
			for (BigDecimal obj : result) {
				unfList.add(obj.shortValue());
			}
			
		} else {
			@SuppressWarnings("unchecked")
			List<Short> result = (List<Short>) query.getResultList();
			for (Short obj : result) {
				unfList.add(obj);
			}
		}
		return unfList;
	}
	
	public AghAtendimentos obterAghUnidadesFuncionaisPorAtendimentoSeq(Integer codAtendimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "U");
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), codAtendimento));
		return (AghAtendimentos) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AghUnidadesFuncionais> obterUnidadesFuncionaisComLeitosEPodemSolicitarExames(Object parametro){
		return this.executeCriteria(this.obterCriteriaUnidadeFuncionaisComLeitosEPodemSolicitarExames(parametro));
	}
	
	public Long obterUnidadesFuncionaisComLeitosEPodemSolicitarExamesCount(Object parametro){
		return this.executeCriteriaCount(this.obterCriteriaUnidadeFuncionaisComLeitosEPodemSolicitarExames(parametro));
	}
	
	private DetachedCriteria obterCriteriaUnidadeFuncionaisComLeitosEPodemSolicitarExames(Object parametro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		criteria = this.validaParametro(parametro, criteria);
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioUnidFunc.A));
		criteria.createAlias(AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CAR");
		criteria.add(Restrictions.in("CAR." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA, 
				new Object[] {
						ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES,
						ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO,
						ConstanteAghCaractUnidFuncionais.ZONA_AMBULATORIO,
						ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA,
						ConstanteAghCaractUnidFuncionais.UNID_COLETA}));
		
		criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);  
	}
	
	private DetachedCriteria validaParametro(Object parametro, DetachedCriteria criteria){
		String parametroStr = parametro != null ? parametro.toString().trim() : null;
		Criterion criterionDescCodigo = null;
		if (StringUtils.isNotBlank(parametroStr)) {
			if (CoreUtil.isNumeroShort(parametroStr)) {
				final short codigo = Short.parseShort(parametroStr);
				criterionDescCodigo = Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), codigo);
			} else {
				criterionDescCodigo = Restrictions.ilike("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString(), parametroStr, MatchMode.ANYWHERE);
			}
			return criteria.add(criterionDescCodigo);
		}
		return criteria;
	}
	
	public Short obtemUnfSeqPorAlmoxarifado(Short almSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.setProjection(Projections.property(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.ALM_SEQ.toString(), almSeq));
		List<Short> retorno = executeCriteria(criteria);
		if (retorno == null || retorno.isEmpty()) {
			return null;
		}
		return retorno.get(0);
	}
	
	/***  Buscar unidade funcional pai retirando a unidade funcional filha */
	private DetachedCriteria obterCriteriaPesquisaPorCodigoOuDescricaoFilha(final Object parametro, final boolean orderDescricao, final Short unidadeFuncionalFilha) {
		final String descricao = (String) parametro;

		final DetachedCriteria criteria = obterCriteriaUnidades();

		if (descricao != null && !"".equals(descricao)) {

			if (StringUtils.isNumeric(descricao)) {
				criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), Short.parseShort((descricao))));
			} else {
				criteria.add(Restrictions.ilike(AghUnidadesFuncionais.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
			}
		}
		
		if (unidadeFuncionalFilha != null) {
			criteria.add(Restrictions.ne(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeFuncionalFilha));
		}

		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (orderDescricao) {
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
		} else {
			criteria.addOrder(Order.asc(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		}

		return criteria;

	}
}
