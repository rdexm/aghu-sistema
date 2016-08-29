package br.gov.mec.aghu.exames.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.exames.coleta.vo.RelatorioExameColetaPorUnidadeVO;
import br.gov.mec.aghu.exames.coleta.vo.SubRelatorioExameColetaPorUnidadeVO;
import br.gov.mec.aghu.exames.vo.AelExtratoItemSolicitacaoVO;
import br.gov.mec.aghu.exames.vo.RelatorioEstatisticaTipoTransporteExtratoVO;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacaoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.model.VAelSolicAtends;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.TooManyMethods")
public class AelExtratoItemSolicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExtratoItemSolicitacao> {
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = -4991552067205273490L;

	@Override
	protected void obterValorSequencialId(final AelExtratoItemSolicitacao elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		if (elemento.getItemSolicitacaoExame() == null) {
			throw new IllegalArgumentException("Associacao com AelItemSolicitacaoExame nao foi informada corretamente!!!");
		}
		// para gerar id
		Short seqp = this.buscarMaiorSeqp(elemento.getItemSolicitacaoExame().getId().getSoeSeq(), elemento.getItemSolicitacaoExame().getId().getSeqp());
		if (seqp == null) {
			seqp = 1;
		} else {
			seqp++;
		}
		final AelExtratoItemSolicitacaoId id = new AelExtratoItemSolicitacaoId(elemento.getItemSolicitacaoExame().getId().getSoeSeq(), elemento.getItemSolicitacaoExame().getId()
				.getSeqp(), seqp);

		elemento.setId(id);
	}

	public Date buscaMaiorDataRecebimento(final Integer itemSolicitacaoExameSoeSeq, final Short itemSolicitacaoExameSeqp, final String situacaoItemSolicitacaoCodigo) {
		final StringBuilder hql = new StringBuilder(200);
		hql.append("select max(eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO);
		hql.append(") ");
		hql.append("from ");
		hql.append(AelExtratoItemSolicitacao.class.getSimpleName());
		hql.append(" eis ");
		hql.append(" where eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = :itemSolicitacaoExameSoeSeq ");
		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString());
		hql.append("  = :itemSolicitacaoExameSeqp ");
		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString());
		hql.append(" = :situacaoItemSolicitacaoCodigo ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("itemSolicitacaoExameSoeSeq", itemSolicitacaoExameSoeSeq);
		query.setParameter("itemSolicitacaoExameSeqp", itemSolicitacaoExameSeqp);
		query.setParameter("situacaoItemSolicitacaoCodigo", situacaoItemSolicitacaoCodigo);

		return (Date) query.uniqueResult();
	}

	/*
	 * select trunc( ( SYSDATE - eis.dthr_evento ) * 24 ) from ael_extrato_item_solics eis where eis.ise_soe_seq = c_ise_soe_seq and eis.ise_seqp = c_ise_seqp and eis.sit_codigo =
	 * c_sit_codigo and eis.seqp in ( select max( seqp ) from ael_extrato_item_solics ews where ews.ise_soe_seq = eis.ise_soe_seq and ews.ise_seqp = eis.ise_seqp and eis.sit_codigo
	 * = c_sit_codigo and ews.moc_seq > 0 );
	 */

	public Date buscaDataEvento(final Integer itemSolicitacaoExameSoeSeq, final Short itemSolicitacaoExameSeqp, final String situacaoItemSolicitacaoCodigo) {

		final StringBuilder hql = new StringBuilder(300);
		hql.append("select eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO);

		hql.append(" from ");
		hql.append(AelExtratoItemSolicitacao.class.getSimpleName());
		hql.append(" eis ");
		hql.append(" where eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = :itemSolicitacaoExameSoeSeq ");
		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString());
		hql.append("  = :itemSolicitacaoExameSeqp ");
		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString());
		hql.append(" = :situacaoItemSolicitacaoCodigo ");

		// in
		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.SEQP.toString());
		hql.append(" in ( ");

		// subquery
		hql.append("select max(");
		hql.append(AelExtratoItemSolicitacao.Fields.SEQP.toString());
		hql.append(')');

		// from
		hql.append(" from ");
		hql.append(AelExtratoItemSolicitacao.class.getSimpleName());
		hql.append(" ews ");

		// where
		hql.append(" where eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString());
		hql.append(" = ews.");
		hql.append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString());

		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString());
		hql.append(" = ews.");
		hql.append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString());

		hql.append(" and eis.");
		hql.append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString());
		hql.append(" = :situacaoItemSolicitacaoCodigo ");

		hql.append(" and ews.");
		hql.append(AelExtratoItemSolicitacao.Fields.MOC_SEQ.toString());
		hql.append(" > 0");

		hql.append(" ) ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("itemSolicitacaoExameSoeSeq", itemSolicitacaoExameSoeSeq);
		query.setParameter("itemSolicitacaoExameSeqp", itemSolicitacaoExameSeqp);
		query.setParameter("situacaoItemSolicitacaoCodigo", situacaoItemSolicitacaoCodigo);

		return (Date) query.uniqueResult();
	}

	/**
	 * @HIST AelExtratoItemSolicitacaoDAO.obterCriteraiPorItemSolicitacaoSitCodigoHist
	 * @param soeSeq
	 * @param seqp
	 * @param sitCodigo
	 * @return
	 */
	protected DetachedCriteria obterCriteraiPorItemSolicitacaoSitCodigo(final Integer soeSeq, final Short seqp, final String sitCodigo) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		result.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		result.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), seqp));
		if (sitCodigo != null) {
			result.createAlias(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
			result.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigo));
		}
		return result;
	}

	/**
	 * @HIST AelExtratoItemSolicitacaoDAO.obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntradaHist
	 * @param soeSeq
	 * @param seqp
	 * @param sitCodigo
	 * @return
	 */
	public AelExtratoItemSolicitacao obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntrada(final Integer soeSeq, final Short seqp, final String sitCodigo) {

		AelExtratoItemSolicitacao result = null;
		List<AelExtratoItemSolicitacao> partial = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteraiPorItemSolicitacaoSitCodigo(soeSeq, seqp, sitCodigo);
		partial = this.executeCriteria(criteria, 0, 1, AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), false);
		if ((partial != null) && !partial.isEmpty()) {
			result = partial.get(0);
		}

		return result;
	}

	/**
	 * @HIST AelExtratoItemSolicitacaoDAO.obterListaPorItemSolicitacaoSitCodigoOrdenadoPorDataPrimeiraEntradaHist
	 * @param soeSeq
	 * @param seqp
	 * @param sitCodigo
	 * @return
	 */
	public AelExtratoItemSolicitacao obterUltimoItemSolicitacaoSitCodigo(final Integer soeSeq, final Short seqp, final String sitCodigo) {

		AelExtratoItemSolicitacao result = null;
		List<AelExtratoItemSolicitacao> partial = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteraiPorItemSolicitacaoSitCodigo(soeSeq, seqp, sitCodigo);

		criteria.addOrder(Order.desc(AelExtratoItemSolicitacao.Fields.SEQP.toString()));

		partial = this.executeCriteria(criteria, 0, 1, null, false);
		if ((partial != null) && !partial.isEmpty()) {
			result = partial.get(0);
		}

		return result;
	}

	public AelExtratoItemSolicitacao obterPenultimoExtratoPorItemSolicitacao(final Integer soeSeq, final Short seqp) {

		AelExtratoItemSolicitacao result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteraiPorItemSolicitacaoSitCodigo(soeSeq, seqp, null);
		criteria.addOrder(Order.desc(AelExtratoItemSolicitacao.Fields.SEQP.toString()));

		final List<AelExtratoItemSolicitacao> retorno = executeCriteria(criteria);

		if (retorno != null && !retorno.isEmpty()) {

			result = retorno.get(1); // Obtém o penúltimo extrato

		}

		return result;
	}

	public Short buscarMaiorSeqp(final Integer iseSoeSeq, final Short iseSeqp) {
		return buscarMaiorSeqp(iseSoeSeq, iseSeqp, false);
	}

	public Short buscarMaiorSeqp(final Integer iseSoeSeq, final Short iseSeqp, final Boolean mocSeqNotNull) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		if (mocSeqNotNull) {
			criteria.add(Restrictions.isNotNull(AelExtratoItemSolicitacao.Fields.AEL_MOTIVO_CANCELAR_EXAMES.toString()));
		}
		criteria.setProjection(Projections.max(AelExtratoItemSolicitacao.Fields.SEQP.toString()));

		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public Short buscarMaiorSeqpCancelado(final Integer iseSoeSeq, final Short iseSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.ne(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "CA"));
		criteria.setProjection(Projections.max(AelExtratoItemSolicitacao.Fields.SEQP.toString()));

		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public AelExtratoItemSolicitacao obterPorId(final Integer iseSoeSeq, final Short iseSeqp, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.SEQP.toString(), seqp));

		return (AelExtratoItemSolicitacao) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Busca todos os AelExtratoItemSolicitacao que tenha seqp menor que o seqp informado.<br>
	 * E sejam do mesmo itemSolicitacaoExame (iseSoeSeq, iseSeqp)<br>
	 * Ordenado decrescentemente.<br>
	 * Todos os parametros sao obrigatorios.<br>
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param seqp
	 * @return list of AelExtratoItemSolicitacao
	 * 
	 * @throws IllegalArgumentException
	 */
	public List<AelExtratoItemSolicitacao> buscaAelExtratoItemSolicitacaoAnteriores(final Integer iseSoeSeq, final Short iseSeqp, final Short seqp) {
		if (iseSoeSeq == null || iseSeqp == null || seqp == null) {
			throw new IllegalArgumentException("buscaAelExtratoItemSolicitacaoAnteriores: Parametro obrigatorio nao informado!!");
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.lt(AelExtratoItemSolicitacao.Fields.SEQP.toString(), seqp));

		criteria.addOrder(Order.desc(AelExtratoItemSolicitacao.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}

	public AelExtratoItemSolicitacao buscarPorItemSolicitacaoSituacao(final Integer iseSoeSeq, final Short iseSeqp, final String sitCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigo));
		criteria.addOrder(Order.desc(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()));

		final List<Object> resultado = executeCriteria(criteria);
		if (resultado.size() == 0) {
			return null;
		}
		return (AelExtratoItemSolicitacao) resultado.get(0);
	}

	public List<AelExtratoItemSolicitacao> pesquisarExtratoItemSolicitacaoConclusao(final Integer iseSoeSeq, final Short iseSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.addOrder(Order.desc(AelExtratoItemSolicitacao.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}

	public List<AelExtratoItemSolicitacao> pesquisarAelExtratoItemSolicitacaoPorItemSolicitacao(final Integer iseSoeSeq, final Short iseSeqp) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.addOrder(Order.asc(AelExtratoItemSolicitacao.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}

	public List<AelExtratoItemSolicitacao> buscarExtratoPorMotivoCancelamento(final AelMotivoCancelaExames motivoCancelamento) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.MOC_SEQ.toString(), motivoCancelamento.getSeq()));

		return executeCriteria(criteria);

	}

	public Long contarExtratoItemSolicitacaoPorAreaExecutora(final AelItemSolicitacaoExamesId aelItemSolicitacaoExamesId, final String areaExecutora) {
		/*
		 * CURSOR c_eis (c_ise_soe_seq ael_exame_ap_item_solics.ise_soe_seq%type, c_ise_seqp ael_exame_ap_item_solics.ise_seqp%type, c_sit_na_area_executora
		 * ael_item_solicitacao_exames.sit_codigo%type) IS SELECT COUNT(*) FROM ael_extrato_item_solics WHERE ise_soe_seq = c_ise_soe_seq AND ise_seqp = c_ise_seqp AND
		 * SUBSTR(sit_codigo,1,2) = c_sit_na_area_executora;
		 */
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), aelItemSolicitacaoExamesId.getSoeSeq()));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), aelItemSolicitacaoExamesId.getSeqp()));

		criteria.add(Restrictions.sqlRestriction(" SUBSTR(sit_codigo,1,2) = ? ", areaExecutora, StringType.INSTANCE));

		return executeCriteriaCount(criteria);
	}

	public List<AelExtratoItemSolicitacao> pesquisarExamesPorAtendimento(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class, "eis");

		DetachedCriteria ise = criteria.createCriteria("itemSolicitacaoExame", "ise");
		DetachedCriteria soe = ise.createCriteria("solicitacaoExame", "soe");
		soe.add(Restrictions.eq("atendimento.seq", atdSeq));
		//
		ise.add(Restrictions.sqlRestriction(" SUBSTR(this_.sit_codigo,1,2) = {alias}.sit_codigo "));
		//
		// subquery
		DetachedCriteria subcriteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class, "subeis");
		subcriteria.setProjection(Projections.max("criadoEm"));
		subcriteria.add(Restrictions.eqProperty("eis.id.iseSoeSeq", "subeis.id.iseSoeSeq"));
		subcriteria.add(Restrictions.eqProperty("eis.id.iseSeqp", "subeis.id.iseSeqp"));

		criteria.add(Subqueries.propertyIn("eis.criadoEm", subcriteria));

		return executeCriteria(criteria);

	}

	/**
	 * Busca os Exames em Coleta Por Unidade Funcional
	 * 
	 * @param {AghUnidadesFuncionais} unidadeExecutora, AelSitItemSolicitacoes situacao
	 * @return List<RelatorioAgendaPorSalaVO>
	 */
	@SuppressWarnings("unchecked")
	public List<RelatorioExameColetaPorUnidadeVO> obterAgendaPorUnidade(AghUnidadesFuncionais unidadeExecutora, AelSitItemSolicitacoes situacao) {

		StringBuffer hql = new StringBuffer(600);
		hql.append("SELECT new br.gov.mec.aghu.exames.coleta.vo.RelatorioExameColetaPorUnidadeVO(");
		hql.append("  vas.").append(VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString());
		hql.append(", vas.").append(VAelSolicAtends.Fields.ATD_PRONTUARIO.toString());
		hql.append(", pac.").append(AipPacientes.Fields.NOME.toString());

		hql.append(") FROM ").append(VAelSolicAtends.class.getSimpleName()).append(" vas, ");
		hql.append(AipPacientes.class.getSimpleName()).append(" pac, ");
		hql.append(AelExtratoItemSolicitacao.class.getSimpleName()).append(" eis ");
		hql.append(" INNER JOIN eis.").append(AelExtratoItemSolicitacao.Fields.ITEM_SOLICITACAO_EXAME.toString());
		hql.append(" AS ise ");
		hql.append(" INNER JOIN ise.").append(AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString());
		hql.append(" AS vem ");

		hql.append(" WHERE ");
		hql.append(" vas.").append(VAelSolicAtends.Fields.SEQ.toString());
		hql.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(" AND pac.").append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = vas.").append(VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString());
		hql.append(" AND vas.").append(VAelSolicAtends.Fields.UNF_SEQ.toString());
		hql.append(" = :unfSeq ");
		hql.append(" AND eis.").append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
		hql.append(" = :sitCodigo ");
		hql.append(" AND ise.").append(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
		hql.append(" = :sitCodigo ");

		hql.append(" GROUP BY ");
		hql.append("  vas.").append(VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString());
		hql.append(", vas.").append(VAelSolicAtends.Fields.ATD_PRONTUARIO.toString());
		hql.append(", pac.").append(AipPacientes.Fields.NOME.toString());
		
		hql.append(" ORDER BY ");
		hql.append("  vas.").append(VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString());
		hql.append(", vas.").append(VAelSolicAtends.Fields.ATD_PRONTUARIO.toString());
		hql.append(", pac.").append(AipPacientes.Fields.NOME.toString());

		// TODO Implementar o UNION com a Amostra para banco de sangue.

		Query query = createHibernateQuery(hql.toString());
		query.setShort("unfSeq", unidadeExecutora.getSeq());
		query.setString("sitCodigo", situacao.getCodigo());

		return query.list();
	}
	
	/**
	 * Busca os Exames em Coleta Por Unidade Funcional
	 * 
	 * @param {AghUnidadesFuncionais} unidadeExecutora, AelSitItemSolicitacoes situacao
	 * @return List<RelatorioAgendaPorSalaVO>
	 */
	@SuppressWarnings("unchecked")
	public List<SubRelatorioExameColetaPorUnidadeVO> obterSubAgendaPorUnidade(AghUnidadesFuncionais unidadeExecutora,
			AelSitItemSolicitacoes situacao, Integer prontuario) {

		StringBuffer hql = new StringBuffer(600);
		hql.append("SELECT new br.gov.mec.aghu.exames.coleta.vo.SubRelatorioExameColetaPorUnidadeVO(");
		hql.append("  vas.").append(VAelSolicAtends.Fields.ORIGEM.toString());
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(", vem.").append(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString());
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString());
		hql.append(", max(eis.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()).append(") ");

		hql.append(") FROM ").append(VAelSolicAtends.class.getSimpleName()).append(" vas, ");
		hql.append(AipPacientes.class.getSimpleName()).append(" pac, ");
		hql.append(AelExtratoItemSolicitacao.class.getSimpleName()).append(" eis ");
		hql.append(" INNER JOIN eis.").append(AelExtratoItemSolicitacao.Fields.ITEM_SOLICITACAO_EXAME.toString());
		hql.append(" AS ise ");
		hql.append(" INNER JOIN ise.").append(AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString());
		hql.append(" AS vem ");

		hql.append(" WHERE ");
		hql.append(" vas.").append(VAelSolicAtends.Fields.SEQ.toString());
		hql.append(" = ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(" AND pac.").append(AipPacientes.Fields.CODIGO.toString());
		hql.append(" = vas.").append(VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString());
		hql.append(" AND vas.").append(VAelSolicAtends.Fields.UNF_SEQ.toString());
		hql.append(" = :unfSeq ");
		hql.append(" AND eis.").append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
		hql.append(" = :sitCodigo ");
		hql.append(" AND ise.").append(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO.toString());
		hql.append(" = :sitCodigo ");
		hql.append(" AND vas.").append(VAelSolicAtends.Fields.ATD_PRONTUARIO.toString());
		hql.append(" = :prontuario ");

		hql.append(" GROUP BY ");
		hql.append("  vas.").append(VAelSolicAtends.Fields.ORIGEM.toString());
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(", vem.").append(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString());
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString());
		
		hql.append(" ORDER BY ");
		hql.append("  vas.").append(VAelSolicAtends.Fields.ORIGEM.toString());
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString());
		hql.append(", vem.").append(VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString());
		hql.append(", ise.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString());

		// TODO Implementar o UNION com a Amostra para banco de sangue.

		Query query = createHibernateQuery(hql.toString());
		query.setShort("unfSeq", unidadeExecutora.getSeq());
		query.setString("sitCodigo", situacao.getCodigo());
		query.setInteger("prontuario", prontuario);

		return query.list();
	}

	/**
	 * Pesquisa do relatório de estatística do tipo de transporte para uma unidade executora
	 * 
	 * @param unidadeExecutora
	 * @param origem
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public List<RelatorioEstatisticaTipoTransporteExtratoVO> pesquisarRelatorioEstatisticaTipoTransporte(AghUnidadesFuncionais unidadeExecutora, DominioOrigemAtendimento origem,
			Date dataInicial, Date dataFinal) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class, "EIS");

		criteria.createAlias("EIS." + AelExtratoItemSolicitacao.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UFE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		
		// Projections com distinct considerando os diferentes tipos de transporte e a data e hora do evento
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("EIS." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()), "dthr_evento")
				.add(Property.forName("ISE." + AelItemSolicitacaoExames.Fields.TIPO_TRANSPORTE_UNIDADE.toString()), "tipo_transporte1")
				.add(Property.forName("ISE." + AelItemSolicitacaoExames.Fields.TIPO_TRANSPORTE.toString()), "tipo_transporte2")));

		// Consulta secundária da unidade funcional
		DetachedCriteria subQuery = DetachedCriteria.forClass(AghUnidadesFuncionais.class, "UNF");
		subQuery.setProjection(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		subQuery.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeExecutora.getSeq()));
		subQuery.createAlias("UNF." + AghUnidadesFuncionais.Fields.UNF_SEQ.toString(), "UNF2");
		subQuery.add(Property.forName("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).eqProperty("ISE." + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		criteria.add(Subqueries.exists(subQuery));

		criteria.add(Restrictions.ne("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), DominioSituacaoItemSolicitacaoExame.CA.toString()));
		criteria.add(Restrictions.eq("EIS." + AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), DominioSituacaoItemSolicitacaoExame.AE.toString()));

		criteria.add(Restrictions.ge("EIS." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), dataInicial));

		Calendar calendarDataFinalDiaPosterior = Calendar.getInstance();
		calendarDataFinalDiaPosterior.setTime(dataFinal);
		calendarDataFinalDiaPosterior.add(Calendar.DATE, 1); // Acrescenta 1 dia a data final conforme AGH
		Date dataFinalDiaPosterior = calendarDataFinalDiaPosterior.getTime();
		criteria.add(Restrictions.lt("EIS." + AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), dataFinalDiaPosterior));

		// Comparação de origem
		if (origem != null) {
			criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), origem));
		} else {
			// Todo ajustar o ALIAS
			criteria.add(Restrictions.sqlRestriction(" CASE WHEN atd4_.ORIGEM = 'H' THEN 'I' ELSE atd4_.ORIGEM END = atd4_.ORIGEM"));
		}

		// Executa consulta
		List<Object[]> listaResultados = executeCriteria(criteria);

		// Instancia lista contendo o resultado final. Nesta lista é realizada a decodificação do tipo de transporte
		List<RelatorioEstatisticaTipoTransporteExtratoVO> retorno = new LinkedList<RelatorioEstatisticaTipoTransporteExtratoVO>();

		if (listaResultados != null && listaResultados.size() > 0) {

			for (Object[] resultado : listaResultados) {

				Date dataHoraEvento = resultado[0] != null ? (Date) resultado[0] : null;
				DominioTipoTransporteUnidade tipoTransporteUnidade = resultado[1] != null ? (DominioTipoTransporteUnidade) resultado[1] : null;
				DominioTipoTransporte tipoTransporteIse = resultado[2] != null ? (DominioTipoTransporte) resultado[2] : null;

				// Decodificação do tipo de transporte
				DominioTipoTransporte tipoTransporte = null;
				if (tipoTransporteUnidade != null) {
					// Quando o tipo de transporte da unidade do item de solicitação é VÁLIDO
					tipoTransporte = DominioTipoTransporte.valueOf(tipoTransporteUnidade.toString());
				} else {
					// Quando o tipo de transporte da unidade é INVÁLIDO considera o do item de solicitação
					tipoTransporte = tipoTransporteIse;
				}

				RelatorioEstatisticaTipoTransporteExtratoVO vo = new RelatorioEstatisticaTipoTransporteExtratoVO();

				vo.setTipoTransporte(tipoTransporte);
				vo.setDataHoraEvento(dataHoraEvento);

				retorno.add(vo);

			}

		}

		return retorno;
	}

	public List<AelExtratoItemSolicitacaoVO> listarExtatisticaPorResultadoExame(Date dtHrInicial, Date dtHoraFinal, Short unfSeq) throws ApplicationBusinessException {

		AghParametros pSitCodigo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);

		StringBuilder hqlIn = new StringBuilder(" SELECT MAX(a.").append(AelExtratoItemSolicitacao.Fields.SEQP.toString()).append(") FROM ")
		.append(AelExtratoItemSolicitacao.class.getName()).append(" a ").append(" WHERE a.").append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ).append(" = ")
		.append(" EIS.").append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString()).append(" AND ").append(" a.")
		.append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString()).append(" = ").append(" EIS.").append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString());

		StringBuilder hql = new StringBuilder(" SELECT ")
			.append(" EIS.").append(AelExtratoItemSolicitacao.Fields.SER_VIN_CODIGO.toString()).append(" AS  ")
				.append(AelExtratoItemSolicitacaoVO.Fields.SER_VIN_CODIGO.toString())
			.append(",EIS.").append(AelExtratoItemSolicitacao.Fields.MATRICULA.toString()).append(" AS  ")
				.append(AelExtratoItemSolicitacaoVO.Fields.SER_MATRICULA.toString())
			.append(",PES.").append(RapPessoasFisicas.Fields.NOME.toString()).append(" AS  ")
				.append(AelExtratoItemSolicitacaoVO.Fields.PESSOA_FISICA_NOME.toString())
			.append(", to_date(to_char(EIS.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO).append(",'dd/mm/yy'),'dd/mm/yy')").append(" AS  ")
				.append(AelExtratoItemSolicitacaoVO.Fields.DATA_HORA_EVENTO.toString())
			.append(", to_date(to_char(EIS.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO).append(",'yyyymmdd'),'yyyymmdd')").append(" AS  ")
				.append(AelExtratoItemSolicitacaoVO.Fields.CRIADO_EM.toString())
			.append(",COUNT(EIS.").append(AelExtratoItemSolicitacao.Fields.SEQP.toString()).append(')').append(" AS  ")
				.append(AelExtratoItemSolicitacaoVO.Fields.COUNT_SEQP.toString())

		.append(" from ")
		.append(AelExtratoItemSolicitacao.class.getName())
		.append(" EIS ")
		.append(" INNER JOIN  EIS.")
		.append(AelExtratoItemSolicitacao.Fields.ITEM_SOLICITACAO_EXAME.toString())
		.append(" ISE ")
		.append(" INNER JOIN  EIS.")
		.append(AelExtratoItemSolicitacao.Fields.SERVIDOR.toString())
		.append(" SER ")
		.append(" INNER JOIN  SER.")
		.append(RapServidores.Fields.PESSOA_FISICA.toString())
		.append(" PES ")

		// where
		.append(" WHERE ")

		.append(" ISE.").append(AelItemSolicitacaoExames.Fields.DTHR_LIBERADA).append(" BETWEEN :dtHoraIni and :dtHoraFim").append(" AND ").append(" ISE.")
		.append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()).append(" = :unfSeq ").append(" AND ").append(" ISE.")
		.append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString()).append(" = :sitCodigo ").append(" AND ").append(" EIS.")
		.append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString()).append(" = :sitCodigo ").append(" AND ").append(" EIS.")
		.append(AelExtratoItemSolicitacao.Fields.SEQP.toString()).append(" IN (").append(hqlIn.toString()).append(')')

		.append(" GROUP BY ")
			.append(" EIS.").append(AelExtratoItemSolicitacao.Fields.SER_VIN_CODIGO.toString()).append(',')
			.append(" EIS.").append(AelExtratoItemSolicitacao.Fields.MATRICULA.toString()).append(',')
			.append(" PES.").append(RapPessoasFisicas.Fields.NOME.toString()).append(',')
			.append(" to_date(to_char(EIS.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO)
				.append(",'dd/mm/yy'),'dd/mm/yy')")
			.append(",to_date(to_char(EIS.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO).append(",'yyyymmdd'),'yyyymmdd')")

		.append(" ORDER BY ")
		//.append(" PES.").append(RapPessoasFisicas.Fields.NOME.toString().concat(","))
		.append("EIS.").append(AelExtratoItemSolicitacao.Fields.SER_VIN_CODIGO.toString().concat(","))
		.append("EIS.").append(AelExtratoItemSolicitacao.Fields.MATRICULA.toString().concat(","))
		.append("to_date(to_char(EIS.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO).append(",'yyyymmdd'),'yyyymmdd')");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("dtHoraIni", DateUtil.obterDataComHoraInical(dtHrInicial));
		query.setParameter("dtHoraFim", DateUtil.obterDataComHoraFinal(dtHoraFinal));
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("sitCodigo", pSitCodigo.getVlrTexto());

		query.setResultTransformer(Transformers.aliasToBean(AelExtratoItemSolicitacaoVO.class));

		return query.list();
	}

	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}
	
	public List<AelExtratoItemSolicitacao> pesquisarExtratoItemSolicitacaoResponsabilideNotNull(final Integer iseSoeSeq, final Short iseSeqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.isNotNull(AelExtratoItemSolicitacao.Fields.SERVIDOR_EH_RESPONSABILIDE_MATRICULA.toString()));
		criteria.add(Restrictions.isNotNull(AelExtratoItemSolicitacao.Fields.SERVIDOR_EH_RESPONSABILIDE_VIN_CODIGO.toString()));
		criteria.addOrder(Order.desc(AelExtratoItemSolicitacao.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna a menor data de entrada na área executora por solicitação de exame
	 * @param iseSoeSeq
	 * @param situacao
	 * @return
	 */
	public Date obterMenorDataAreaExecutoraPorSolicitacaoExame(final Integer iseSoeSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "AE"));
		
		criteria.addOrder(Order.asc(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()));
		List<AelExtratoItemSolicitacao> list = executeCriteria(criteria);
		
		if(list != null && !list.isEmpty()){
			return list.get(0).getDataHoraEvento();
		}else{
			return null;
		}
		
	}
	
}