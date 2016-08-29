package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatPeriodosEmissao;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @author rafael
 * 
 */

public class FatConvenioSaudePlanoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatConvenioSaudePlano> {

	private static final long serialVersionUID = 1996140928915798337L;

	private DetachedCriteria obterDetachedCriteriaFatConvenioSaudePlano() {
		return DetachedCriteria.forClass(FatConvenioSaudePlano.class);
	}

	/**
	 * Obtem um convenio saude plano atraves do atdSeq de uma consulta. Também
	 * filtra pelo indTipoPlano igual a "A".
	 * 
	 * @param atdSeq
	 * @return FatConvenioSaudePlano
	 */
	public FatConvenioSaudePlano obterConvenioSaudePlanoPeloAtendimentoDaConsulta(final Integer atdSeq) {
		final DetachedCriteria criteria = obterDetachedCriteriaFatConvenioSaudePlano();

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONSULTAS.toString(), FatConvenioSaudePlano.Fields.CONSULTAS.toString(),
				JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.A));

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONSULTAS.toString() + "." + AacConsultas.Fields.ATD_SEQ.toString(),
				atdSeq));

		final List<FatConvenioSaudePlano> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}

		return null;

	}

	/**
	 * Obtem um convenio saude plano atraves do numero de um
	 * aac_atendimento_apacs. Também filtra pelo indTipoPlano igual a "A", da
	 * entidade FatConvenioSaudePlano.
	 * 
	 * @param atdSeq
	 * @return FatConvenioSaudePlano
	 */
	public FatConvenioSaudePlano obterConvenioSaudePlanoPeloNumeroDoAtendimentoPaciente(final Long atmNumero) {
		final DetachedCriteria criteria = obterDetachedCriteriaFatConvenioSaudePlano();

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONSULTAS.toString(), FatConvenioSaudePlano.Fields.CONSULTAS.toString(),
				JoinType.INNER_JOIN);
		criteria.createAlias(FatConvenioSaudePlano.Fields.CONSULTAS.toString() + "." + AacConsultas.Fields.ATENDIMENTO.toString(),
				AacConsultas.Fields.ATENDIMENTO.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AacConsultas.Fields.ATENDIMENTO.toString() + "." + AghAtendimentos.Fields.ATENDIMENTOS_APAC.toString(),
				AghAtendimentos.Fields.ATENDIMENTOS_APAC.toString(), JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.A));

		criteria.add(Restrictions.eq(
				AghAtendimentos.Fields.ATENDIMENTOS_APAC.toString() + "." + AacAtendimentoApacs.Fields.NUMERO.toString(), atmNumero));

		final List<FatConvenioSaudePlano> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Obtem um convenio saude plano atraves do seq do
	 * fat_proced_amb_realizados.
	 * 
	 * @param atdSeq
	 * @return FatConvenioSaudePlano
	 */
	public FatConvenioSaudePlano obterConvenioSaudePlanoPeloProcedimentoAmbRealizado(final Long pmrSeq) {
		final DetachedCriteria criteria = obterDetachedCriteriaFatConvenioSaudePlano();

		criteria.createAlias(FatConvenioSaudePlano.Fields.PROCEDIMENTOS_AMB_REALIZADOS.toString(),
				FatConvenioSaudePlano.Fields.PROCEDIMENTOS_AMB_REALIZADOS.toString(), JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.PROCEDIMENTOS_AMB_REALIZADOS.toString() + "."
				+ FatProcedAmbRealizado.Fields.SEQ.toString(), pmrSeq));

		final List<FatConvenioSaudePlano> list = executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	private DetachedCriteria criarCriteriaBaseConvenios(final String filtro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());

		if (filtro!=null && !"".equalsIgnoreCase(filtro)){
			final Criterion criSeq = Restrictions.sqlRestriction("cast(SEQ as varchar(50)) like '%" + filtro + "%'");
			final Criterion criCodigo = Restrictions.sqlRestriction("cast(CODIGO as varchar(50)) like '%" + filtro + "%'");
			final Criterion criDescricao = Restrictions.like(FatConvenioSaudePlano.Fields.DESCRICAO.toString(), filtro == null ? "" : filtro.toUpperCase(),
					MatchMode.ANYWHERE);
			final Criterion criDescricaoConvenio = Restrictions.like(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
					+ FatConvenioSaude.Fields.DESCRICAO.toString(), filtro == null ? "" : filtro.toUpperCase(), MatchMode.ANYWHERE);
			
			// criteria2.add(criCodigo);
			criteria.add(Restrictions.or(criDescricao, Restrictions.or(criSeq, Restrictions.or(criCodigo, criDescricaoConvenio))));
		}
		return criteria;
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(final String filtro) {

		final DetachedCriteria criteria = criarCriteriaBaseConvenios(filtro);

		criteria.addOrder(Order.asc(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
				+ FatConvenioSaude.Fields.DESCRICAO.toString()));

		criteria.addOrder(Order.asc(FatConvenioSaudePlano.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria, 0, 100, "descricao", true);
	}

	/**
	 * Pesquisa por grupo de convenio
	 * 
	 * @param filtro
	 * @return
	 */
	public List<FatConvenioSaudePlano> pesquisarConvenioPorGrupoConvenio(final String filtro, DominioGrupoConvenio grupoConvenioSUS) {

		final DetachedCriteria criteria = criarCriteriaBaseConvenios(filtro);

		if (grupoConvenioSUS != null) {
			criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
					+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), grupoConvenioSUS));
		} 
		
		criteria.addOrder(Order.asc(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."+ FatConvenioSaude.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(FatConvenioSaudePlano.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, 0, 100, "descricao", true);
	}

	/**
	 * Pesquisa por grupo de convenio
	 * 
	 * @param seqConvenioSaudePlano
	 * @param codConvenioSaude
	 * @param grupoConvenioSUS
	 * @return
	 */
	public FatConvenioSaudePlano obterConvenioPorGrupoConvenio(final Byte seqConvenioSaudePlano, final Short codConvenioSaude,
			DominioGrupoConvenio grupoConvenioSUS) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SEQ.toString(), seqConvenioSaudePlano));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), codConvenioSaude));
		if (grupoConvenioSUS != null) {
			criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
					+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), grupoConvenioSUS));
		} else {
			criteria.add(Restrictions.ne(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
					+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), grupoConvenioSUS));
		}
		return (FatConvenioSaudePlano) this.executeCriteriaUniqueResult(criteria);
	}

	public Long pesquisarConvenioSaudePlanosCount(final String filtro) {
		final DetachedCriteria criteria = criarCriteriaBaseConvenios(filtro);
		return this.executeCriteriaCount(criteria);
	}

	public Long pesquisarConvenioPorGrupoConvenioCount(final String filtro, DominioGrupoConvenio grupoConvenioSUS) {
		final DetachedCriteria criteria = criarCriteriaBaseConvenios(filtro);
		if (grupoConvenioSUS != null) {
			criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
					+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), grupoConvenioSUS));
		} else {
			criteria.add(Restrictions.ne(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
					+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), grupoConvenioSUS));
		}
		return this.executeCriteriaCount(criteria);
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosSolicitacaoInternacao(final String filtro) {

		final String filtroStr = filtro;
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());

		final Criterion criSeq = Restrictions.sqlRestriction("cast(SEQ as varchar(50)) like '%" + filtroStr + "%'");

		final Criterion criCodigo = Restrictions.sqlRestriction("cast(CODIGO as varchar(50)) like '%" + filtroStr + "%'");

		final Criterion criDescricao = Restrictions.like(FatConvenioSaudePlano.Fields.DESCRICAO.toString(), filtroStr.toUpperCase(),
				MatchMode.ANYWHERE);

		final Criterion criDescricaoConvenio = Restrictions.like(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
				+ FatConvenioSaude.Fields.DESCRICAO.toString(), filtroStr.toUpperCase(), MatchMode.ANYWHERE);

		criteria.add(Restrictions.or(criDescricao, Restrictions.or(criSeq, Restrictions.or(criCodigo, criDescricaoConvenio))));

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
				+ FatConvenioSaude.Fields.PERMISSAO_INTERNACAO.toString(), true));

		return this.executeCriteria(criteria, 0, 100, null, true);
	}

	public FatConvenioSaudePlano obterPlanoPorIdConvenioInternacao(final Byte seqConvenioSaudePlano, final Short codConvenioSaude) {
		final DetachedCriteria cri = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		cri.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());

		cri.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SEQ.toString(), seqConvenioSaudePlano));
		cri.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), codConvenioSaude));

		cri.add(Restrictions.eq(
				FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "." + FatConvenioSaude.Fields.PERMISSAO_INTERNACAO.toString(),
				true));
		cri.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		cri.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SITUACAO.toString(), DominioSituacao.A));
		cri.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "." + FatConvenioSaude.Fields.SITUACAO.toString(),
				DominioSituacao.A));

		return (FatConvenioSaudePlano) executeCriteriaUniqueResult(cri);
	}

	public FatConvenioSaudePlano obterConvenioSaudePlanoParaInternacao(final Short cnvCodigo, final Byte seq) {
		final DetachedCriteria criteria = this.criarCriteriaConvenioSaudePlano(cnvCodigo, seq);
		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE_PERMISSAO_INTERNACAO.toString(), true));

		return (FatConvenioSaudePlano) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método para criar criteria de ConvenioSaudePlano.
	 * 
	 * @param cnvCodigo
	 * @param seq
	 * @return
	 */
	private DetachedCriteria criarCriteriaConvenioSaudePlano(final Short cnvCodigo, final Byte seq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CNV");
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), cnvCodigo));

		return criteria;
	}

	public FatConvenioSaudePlano obterConvenioSaudePlano(final Short cnvCodigo, final Byte seq) {
		final DetachedCriteria criteria = criarCriteriaConvenioSaudePlano(cnvCodigo, seq);
		return (FatConvenioSaudePlano) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * @param filtro
	 * @return
	 */
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanosInternacao(final String filtro) {
		final List<FatConvenioSaudePlano> listaPlanoConveniosBanco = this.pesquisarConvenioSaudePlanosSolicitacaoInternacao(filtro);

		final List<FatConvenioSaudePlano> listaPlanoConveniosInternacao = new ArrayList<FatConvenioSaudePlano>();
		for (final FatConvenioSaudePlano convenioPlano : listaPlanoConveniosBanco) {
			if (convenioPlano.getConvenioSaude().getPermissaoInternacao() && DominioTipoPlano.I.equals(convenioPlano.getIndTipoPlano())) {
				if (DominioSituacao.A.equals(convenioPlano.getConvenioSaude().getSituacao())
						&& DominioSituacao.A.equals(convenioPlano.getIndSituacao())) {
					listaPlanoConveniosInternacao.add(convenioPlano);
				}
			}
		}

		return listaPlanoConveniosInternacao;
	}

	/**
	 * @param seqConvenioSaudePlano
	 * @param codConvenioSaude
	 * @return
	 */
	public FatConvenioSaudePlano obterPlanoPorIdSolicitacaoInternacao(final Byte seqConvenioSaudePlano, final Short codConvenioSaude) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SEQ.toString(), seqConvenioSaudePlano));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), codConvenioSaude));

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
				+ FatConvenioSaude.Fields.PERMISSAO_INTERNACAO.toString(), true));

		return (FatConvenioSaudePlano) executeCriteriaUniqueResult(criteria);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void obterValorSequencialId(final FatConvenioSaudePlano fatConvenioSaudePlano) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		if (fatConvenioSaudePlano.getId() != null && fatConvenioSaudePlano.getId().getCnvCodigo() != null) {
			criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), fatConvenioSaudePlano.getId().getCnvCodigo()));

			criteria.setProjection(Projections.max(FatConvenioSaudePlano.Fields.SEQ.toString()));
		} else {
			fatConvenioSaudePlano.getId().setSeq(Byte.valueOf("1"));
		}

		final Byte obj = (Byte) executeCriteriaUniqueResult(criteria);

		if (obj == null) {
			fatConvenioSaudePlano.getId().setSeq(Byte.valueOf("1"));
		}else {
			fatConvenioSaudePlano.getId().setSeq(Byte.valueOf((byte) (obj.intValue() + 1))); 
		}

	}

	public Short obterValorSequencialPeriodoEmissaoId(final FatConvenioSaudePlano fatConvenioSaudePlano) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatPeriodosEmissao.class);

		if (fatConvenioSaudePlano.getId() != null && fatConvenioSaudePlano.getId().getCnvCodigo() != null) {
			criteria.add(Restrictions.eq(FatPeriodosEmissao.Fields.CNV_CODIGO.toString(), fatConvenioSaudePlano.getId().getCnvCodigo()));

			criteria.setProjection(Projections.max(FatPeriodosEmissao.Fields.SEQ.toString()));

		} else {
			return (short) 1;
		}

		final Short obj = (Short) executeCriteriaUniqueResult(criteria);

		if (obj == null) {
			return (short) 1;
		}

		return (short) (obj.intValue() + 1);
	}

	public FatConvenioSaudePlano obterConvenioSaudePlanoPeloId(final Short cnvCodigo, final Byte seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SEQ.toString(), seq));

		return (FatConvenioSaudePlano) executeCriteriaUniqueResult(criteria);
	}

	public List<FatConvenioSaudePlano> listarConvenioSaudePlanos(final String cspDescricao) {
		final DetachedCriteria criteria = obterDetachedCriteriaFatConvenioSaudePlano();

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(),
				FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.A));
		criteria.add(Restrictions.eq(
				FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "." + FatConvenioSaude.Fields.SITUACAO.toString(),
				DominioTipoPlano.A));

		if (StringUtils.isNotBlank(cspDescricao)) {
			if (CoreUtil.isNumeroShort(cspDescricao)) {
				criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), Short.valueOf(cspDescricao)));
			} else {
				final Criterion DESCRICAO_CONVENIO_SAUDE = Restrictions.ilike(
						FatConvenioSaudePlano.Fields.DESCRICAO_CONVENIO_SAUDE.toString(), cspDescricao, MatchMode.ANYWHERE);
				final Criterion DESCRICAO = Restrictions.ilike(FatConvenioSaudePlano.Fields.DESCRICAO.toString(), cspDescricao,
						MatchMode.ANYWHERE);

				criteria.add(Restrictions.or(DESCRICAO_CONVENIO_SAUDE, DESCRICAO));

				// criteria.add(Restrictions.ilike(FatConvenioSaudePlano.Fields.DESCRICAO_CONVENIO_SAUDE.toString(),
				// cspDescricao, MatchMode.ANYWHERE));
			}

		}

		// desc_conv - convenio.descricao
		criteria.addOrder(Order.asc(FatConvenioSaudePlano.Fields.DESCRICAO_CONVENIO_SAUDE.toString()));
		// desc_plan - descricao
		criteria.addOrder(Order.asc(FatConvenioSaudePlano.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Obtém o convênio e o plano que permite internação
	 * 
	 * @param cnvCodigo
	 * @return
	 */
	public FatConvenioSaudePlano obterConvenioPlanoInternacao(final Short cnvCodigo) {
		FatConvenioSaudePlano retorno = null;
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString()
				.toString());

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.I));

		final List<FatConvenioSaudePlano> listaConvenioPlanos = executeCriteria(criteria, 0, 1, null, true);
		if (listaConvenioPlanos != null && !listaConvenioPlanos.isEmpty()) {
			retorno = listaConvenioPlanos.get(0);
		}
		return retorno;
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioPlano(final Integer firstResult, final Integer maxResult, final String strPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);
		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString()
				.toString());

		if (StringUtils.isNotBlank(strPesquisa)) {

			if (StringUtils.isNumeric(strPesquisa)) {
				final Short shortPesquisa = Short.valueOf(strPesquisa);
				criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), shortPesquisa));
			} else {
				final Criterion cNome = Restrictions.or(Restrictions.ilike(FatConvenioSaudePlano.Fields.DESCRICAO.toString(), strPesquisa,
						MatchMode.ANYWHERE), Restrictions.ilike(FatConvenioSaudePlano.Fields.DESCRICAO_CONVENIO_SAUDE.toString(),
						strPesquisa, MatchMode.ANYWHERE));
				criteria.add(cNome);
			}
		}

		List<FatConvenioSaudePlano> resultList = null;

		if (firstResult == 0 && maxResult == 0) {
			resultList = executeCriteria(criteria);
		} else {
			resultList = executeCriteria(criteria, firstResult, maxResult, null, true);
		}
		return resultList;
	}

	public List<ConvenioPlanoVO> pesquisarConvenioPlanoVOPorCodigoDescricao(final Object strPesquisa) {
		final DetachedCriteria cri = DetachedCriteria.forClass(FatConvenioSaudePlano.class, "PS");

		cri.setProjection(Projections.projectionList().add(Projections.property("PS.id.cnvCodigo"), "cnvCodigo")
				.add(Projections.property("PS.id.seq"), "plano").add(Projections.property("CS.descricao"), "descConv")
				.add(Projections.property("PS.descricao"), "descPlan")
				.add(Projections.property("CS.verificaEscalaProfInt"), "indVerfEscalaProfInt")
				.add(Projections.property("CS.exigeNumeroMatricula"), "indExigeNumMatr")
				.add(Projections.property("CS.selecaoAutomaticaProf"), "indSelAutomProf").add(

				Projections.property("CS.situacao"), "indSituacao").add(Projections.property("CS.restringeProf"), "indRestringeProf")
				.add(Projections.property("CS.grupoConvenio"), "grupoConvenio")
				.add(Projections.property("PS.indTipoPlano"), "indTipoPlano")
				.add(Projections.property("PS.indSituacao"), "planoIndSituacao").add(Projections.property("CS.pagador.seq"), "pgdSeq").add(

				Projections.property("CS.permissaoInternacao"), "indPermissaoInternacao"));

		cri.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CS", JoinType.INNER_JOIN);

		if (strPesquisa != null && !"".equalsIgnoreCase(strPesquisa.toString())) {

			if (CoreUtil.isNumeroShort(strPesquisa)) {
				cri.add(Restrictions.or(
						Restrictions.or(Restrictions.ilike("PS." + FatConvenioSaudePlano.Fields.DESCRICAO.toString(),
								strPesquisa.toString(), MatchMode.ANYWHERE), Restrictions.ilike(
								"CS." + FatConvenioSaude.Fields.DESCRICAO.toString(), strPesquisa)),
						Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), Short.valueOf(strPesquisa.toString()))));
			} else {
				cri.add(Restrictions.or(Restrictions.ilike("PS." + FatConvenioSaudePlano.Fields.DESCRICAO.toString(),
						strPesquisa.toString(), MatchMode.ANYWHERE), Restrictions.ilike(
						"CS." + FatConvenioSaude.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE)));
			}
		}

		cri.add(Restrictions.eq("PS.indTipoPlano", DominioTipoPlano.I));
		cri.add(Restrictions.eq("CS.permissaoInternacao", true));
		cri.setResultTransformer(Transformers.aliasToBean(ConvenioPlanoVO.class));

		return executeCriteria(cri, 0, 25, null, false);
	}

	public FatConvenioSaudePlano obterConvenioSaudePlanoAtivo(final Short cnvCodigo, final Byte seq) {
		final DetachedCriteria criteria = this.criarCriteriaConvenioSaudePlano(cnvCodigo, seq);
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SITUACAO.toString(), DominioSituacao.A));
		return (FatConvenioSaudePlano) executeCriteriaUniqueResult(criteria);
	}

	public List<FatConvenioSaudePlano> sbObterConvenio(final Object parametro) {
		return executeCriteria(this.obterCriteriaConvenio(parametro), 0, 100, null, false);
	}

	public Long sbObterConvenioCount(final Object parametro) {
		return executeCriteriaCount(this.obterCriteriaConvenio(parametro));
	}

	public DetachedCriteria obterCriteriaConvenio(final Object parametro) {

		/*
		 * select cnv.descricao, pla.descricao from fat_convenios_saude cnv,
		 * fat_conv_saude_planos pla where cnv.codigo = pla.cnv_codigo and
		 * cnv.ind_situacao = 'A' and pla.ind_situacao = 'A'
		 */

		final DetachedCriteria criteria = this.obterDetachedCriteriaFatConvenioSaudePlano();

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());

		if (StringUtils.isNotBlank(parametro.toString())) {

			if (CoreUtil.isNumeroShort(parametro.toString())) {
				criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), Short.valueOf(parametro.toString())));
			} else {
				final Criterion DESCRICAO_CONVENIO_SAUDE = Restrictions.ilike(
						FatConvenioSaudePlano.Fields.DESCRICAO_CONVENIO_SAUDE.toString(), parametro.toString(), MatchMode.ANYWHERE);
				final Criterion DESCRICAO = Restrictions.ilike(FatConvenioSaudePlano.Fields.DESCRICAO.toString(), parametro.toString(),
						MatchMode.ANYWHERE);

				criteria.add(Restrictions.or(DESCRICAO_CONVENIO_SAUDE, DESCRICAO));
			}
		}

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SITUACAO_CONVENIO_SAUDE.toString(), DominioSituacao.A));

		return criteria;
	}

	public FatConvenioSaudePlano obterPorCspCnvCodigoECnvCodigo(Short codigo, Byte seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class, "CSP");
		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SEQ.toString(), seq));
		return (FatConvenioSaudePlano) this.executeCriteriaUniqueResult(criteria);
	}

	public List<FatConvenioSaudePlano> pesquisarPlanoPorConvenioSaude(final Short cnvCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class, "CSP");
		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), cnvCodigo));
		return this.executeCriteria(criteria);
	}
}