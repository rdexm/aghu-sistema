package br.gov.mec.aghu.compras.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.compras.vo.ScoPedidoMatExpedienteVO;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPedItensMatExpediente;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.sig.custos.vo.PedidoPapelariaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;


public class ScoPedidoMatExpedienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoPedidoMatExpediente> {
	
	private static final Log LOG = LogFactory.getLog(ScoPedidoMatExpedienteDAO.class);
	
	private static final long serialVersionUID = -5977820662737048600L;

	public List<ScoPedidoMatExpedienteVO> pesquisarNotasFiscais(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoPedidoMatExpedienteVO scoPedidoMatExpedienteVO) {
		
		StringBuilder hql = obterQueryNativa(scoPedidoMatExpedienteVO);

		final SQLQuery query = createSQLQuery(hql.toString());

		if (scoPedidoMatExpedienteVO.getNumeroNotaFiscal() != null) {
			query.setInteger("numeroNotaFiscal",
					scoPedidoMatExpedienteVO.getNumeroNotaFiscal());
		}
		//TODO datafim validar

		if (scoPedidoMatExpedienteVO.getDataInicioEmissao() != null) {
			query.setDate("dataEmissaoInicio",
					scoPedidoMatExpedienteVO.getDataInicioEmissao());
		}

		if (scoPedidoMatExpedienteVO.getDataFimEmissao() != null) {
			query.setDate("dataEmissaoFim",
					scoPedidoMatExpedienteVO.getDataFimEmissao());
		}

		if (scoPedidoMatExpedienteVO.getDataInicioPedido() != null) {
			query.setDate("dataPedidoInicio",
					scoPedidoMatExpedienteVO.getDataInicioPedido());
		}

		if (scoPedidoMatExpedienteVO.getDataFimPedido() != null) {
			query.setDate("dataPedidoFim",
					scoPedidoMatExpedienteVO.getDataFimPedido());
		}

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		query.setResultTransformer(Transformers
				.aliasToBean(ScoPedidoMatExpedienteVO.class));
		
		query.addScalar("numeroNotaFiscal", IntegerType.INSTANCE);
		query.addScalar("dataNotaFiscal", DateType.INSTANCE);
		query.addScalar("valorTotal", BigDecimalType.INSTANCE);
		query.addScalar("indValidacaoNF", StringType.INSTANCE);
		query.addScalar("indIntegracaoEstoque", StringType.INSTANCE);
		query.addScalar("indGeracaoRM", StringType.INSTANCE);

		return query.list();
	}

	private StringBuilder obterQueryNativa(
			ScoPedidoMatExpedienteVO scoPedidoMatExpedienteVO) {
		StringBuilder hql = new StringBuilder(800);

		hql.append("SELECT PMX.NUMERO_NOTA_FISCAL AS numeroNotaFiscal, ");
		hql.append("	   PMX.DT_NOTA_FISCAL AS dataNotaFiscal, ");
		hql.append("       SUM(IMX.QUANTIDADE * IMX.VALOR_UNITARIO) AS valorTotal, ");
		hql.append("       PMX.INDICADOR_VALIDACAO_NF AS indValidacaoNF, ");
		hql.append("       PMX.INDICADOR_INTEGRACAO_ESTOQUE AS indIntegracaoEstoque, ");
		hql.append("       PMX.INDICADOR_GERACAO_RM AS indGeracaoRM ");
		hql.append("FROM AGH.SCO_PEDIDOS_MAT_EXPEDIENTE PMX, AGH.SCO_PED_ITENS_MAT_EXPEDIENTE IMX ");
		hql.append("WHERE PMX.SEQ = IMX.PMX_SEQ ");

		if (scoPedidoMatExpedienteVO.getNumeroNotaFiscal() != null) {
			hql.append("AND PMX.NUMERO_NOTA_FISCAL = :numeroNotaFiscal ");
		} else {
			hql.append("AND PMX.NUMERO_NOTA_FISCAL is not null ");
		}

		if (scoPedidoMatExpedienteVO.getDataInicioEmissao() != null) {
			hql.append("AND PMX.DT_NOTA_FISCAL >= :dataEmissaoInicio ");
		}

		if (scoPedidoMatExpedienteVO.getDataFimEmissao() != null) {
			hql.append("AND PMX.DT_NOTA_FISCAL <= :dataEmissaoFim ");
		}

		if (scoPedidoMatExpedienteVO.getDataInicioPedido() != null) {
			hql.append("AND PMX.DT_PEDIDO >= :dataPedidoInicio ");
		}

		if (scoPedidoMatExpedienteVO.getDataFimPedido() != null) {
			hql.append("AND PMX.DT_PEDIDO <= :dataPedidoFim ");
		}

		hql.append("GROUP BY ");
		hql.append("PMX.NUMERO_NOTA_FISCAL,");
		hql.append("PMX.DT_NOTA_FISCAL,");
		hql.append("PMX.INDICADOR_VALIDACAO_NF,");
		hql.append("PMX.INDICADOR_INTEGRACAO_ESTOQUE,");
		hql.append("PMX.INDICADOR_GERACAO_RM");
		return hql;
	}

	public Long pesquisarNotasFiscaisCount(ScoPedidoMatExpedienteVO scoPedidoMatExpedienteVO) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoPedidoMatExpediente.class);
		criteria.createAlias(ScoPedidoMatExpediente.Fields.LISTA_ITENS.toString(), "li");
		
		if (scoPedidoMatExpedienteVO.getNumeroNotaFiscal() != null) {
			criteria.add(Restrictions.eq(ScoPedidoMatExpediente.Fields.NUMERO_NOTA_FISCAL.toString(), scoPedidoMatExpedienteVO.getNumeroNotaFiscal()));
		} else {
			criteria.add(Restrictions.isNotNull(ScoPedidoMatExpediente.Fields.NUMERO_NOTA_FISCAL.toString()));
		}

		if (scoPedidoMatExpedienteVO.getDataInicioEmissao() != null) {
			criteria.add(Restrictions.gt(ScoPedidoMatExpediente.Fields.DATA_NOTA_FISCAL.toString(), scoPedidoMatExpedienteVO.getDataInicioEmissao()));
		}

		if (scoPedidoMatExpedienteVO.getDataFimEmissao() != null) {
			criteria.add(Restrictions.lt(ScoPedidoMatExpediente.Fields.DATA_NOTA_FISCAL.toString(), scoPedidoMatExpedienteVO.getDataFimEmissao()));
		}

		if (scoPedidoMatExpedienteVO.getDataInicioPedido() != null) {
			criteria.add(Restrictions.ge(ScoPedidoMatExpediente.Fields.DATA_PEDIDO.toString(), scoPedidoMatExpedienteVO.getDataInicioPedido()));
		}

		if (scoPedidoMatExpedienteVO.getDataFimPedido() != null) {
			criteria.add(Restrictions.le(ScoPedidoMatExpediente.Fields.DATA_PEDIDO.toString(), scoPedidoMatExpedienteVO.getDataFimPedido()));
		}

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.groupProperty(ScoPedidoMatExpediente.Fields.NUMERO_NOTA_FISCAL.toString()))
				.add(Projections.groupProperty(ScoPedidoMatExpediente.Fields.DATA_NOTA_FISCAL.toString()))
				.add(Projections.groupProperty(ScoPedidoMatExpediente.Fields.IND_VALIDACAO_NF.toString()))
				.add(Projections.groupProperty(ScoPedidoMatExpediente.Fields.IND_INTEGRACAO_ESTOQUE.toString()))
				.add(Projections.groupProperty(ScoPedidoMatExpediente.Fields.IND_GERACAO_RM.toString()))
		);
		
		List<ScoPedidoMatExpediente> listaResultado = executeCriteria(criteria);
		
		Integer result = 0;
		
		if (listaResultado != null && !listaResultado.isEmpty()) {
			result = listaResultado.size();
		}

		return result.longValue();
	}

	private DetachedCriteria obterCriteriaBasicaPedidoMatExpediente(
			final ScoPedidoMatExpedienteVO filtro) {
		final DetachedCriteria criteria = DetachedCriteria
		.forClass(ScoPedidoMatExpediente.class);
		if (filtro == null) {
			return criteria;
		} else {
			if (filtro.getNumeroPedido() != null) {
				criteria.add(Restrictions.eq(
						ScoPedidoMatExpediente.Fields.NUMERO_PEDIDO.toString(),
						filtro.getNumeroPedido()));
			}
			this.adicionarFiltroCentroCusto(criteria,
					filtro.getCodigoCentroCusto());
			this.adicionarFiltroSolicitante(criteria,
					filtro.getMatriculaSolicitante(),
					filtro.getVinculoSolicitante());
			this.adicionarFiltroData(criteria,
					ScoPedidoMatExpediente.Fields.DATA_PEDIDO.toString(),
					filtro.getDataInicioPedido(), filtro.getDataFimPedido());
			this.adicionarFiltroData(criteria,
					ScoPedidoMatExpediente.Fields.DATA_RECEBIMENTO.toString(),
					filtro.getDataPedRecebInicial(),
					filtro.getDataPedRecebFinal());

		}
		return criteria;

	}

	public List<ScoPedidoMatExpediente> pesquisarPedidoMatExp(
			final ScoPedidoMatExpedienteVO filtro, final Integer firstResult,
			final Integer maxResults, final String orderProperty,
			final boolean asc) {
		DetachedCriteria criteria = obterCriteriaBasicaPedidoMatExpediente(filtro);
		
		criteria.createAlias(ScoPedidoMatExpediente.Fields.SERVIDOR_SOLICITANTE.toString(), "rap", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoPedidoMatExpediente.Fields.CENTRO_CUSTO.toString(), "cct", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.desc(ScoPedidoMatExpediente.Fields.NUMERO_PEDIDO.toString()));
				
		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Long pesquisarPedidoMatExpCount(
			final ScoPedidoMatExpedienteVO filtro) {
		DetachedCriteria criteria = obterCriteriaBasicaPedidoMatExpediente(filtro);
		return executeCriteriaCount(criteria);
	}

	public ScoPedidoMatExpediente obterPedidoMatExpPorChavePrimaria(
			final Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria
		.forClass(ScoPedidoMatExpediente.class);
		criteria.add(Restrictions.eq(
				ScoPedidoMatExpediente.Fields.SEQ.toString(), seq));
		criteria.createAlias(ScoPedidoMatExpediente.Fields.CENTRO_CUSTO.toString(), "cct", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoPedidoMatExpediente.Fields.SERVIDOR_SOLICITANTE.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoPedidoMatExpediente.Fields.SERVIDOR_CONFERENTE.toString(), "src", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("src." + RapServidores.Fields.PESSOA_FISICA.toString(), "psc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoPedidoMatExpediente.Fields.LISTA_ITENS.toString(), "li", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("li." + ScoPedItensMatExpediente.Fields.MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mat." + ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "umd", JoinType.LEFT_OUTER_JOIN);
		
		return (ScoPedidoMatExpediente) executeCriteriaUniqueResult(criteria);
	}

	public List<ScoPedidoMatExpediente> pesquisarPedidosByNumeroNotaFiscal(final Integer numeroNotaFiscal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPedidoMatExpediente.class);

		criteria.add(Restrictions.eq(ScoPedidoMatExpediente.Fields.NUMERO_NOTA_FISCAL.toString(), numeroNotaFiscal));

		return executeCriteria(criteria);
	}

	private void adicionarFiltroCentroCusto(final DetachedCriteria criteria,
			final Integer codCentroCusto) {
		if (codCentroCusto != null) {
			criteria.add(Restrictions.eq(
					ScoPedidoMatExpediente.Fields.CENTRO_CUSTO_CODIGO
					.toString(), codCentroCusto));
		}
	}

	private void adicionarFiltroSolicitante(final DetachedCriteria criteria,
			final Integer matricula, final Short vinculo) {
		if (matricula != null && vinculo != null) {
			criteria.add(Restrictions
					.eq(ScoPedidoMatExpediente.Fields.SERVIDOR_SOLICITANTE_MATRICULA
							.toString(), matricula));
			criteria.add(Restrictions.eq(
					ScoPedidoMatExpediente.Fields.SERVIDOR_SOLICITANTE_VINCULO
					.toString(), vinculo));
		}
	}

	private void adicionarFiltroData(final DetachedCriteria criteria,
			final String campo, final Date dataInicial, final Date dataFinal) {
		if (dataInicial != null) {
			criteria.add(Restrictions.ge(campo,
					DateUtil.obterDataComHoraInical(dataInicial)));
		}
		if (dataFinal != null) {
			criteria.add(Restrictions.le(campo,
					DateUtil.obterDataComHoraFinal(dataFinal)));
		}
	}

	public ScoPedidoMatExpediente obterScoPedidoMatExpedienteByNumeroPedido(PedidoPapelariaVO pedidoPapelaria) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoPedidoMatExpediente.class);

		if(pedidoPapelaria.getNumPedido() != null) {
			criteria.add(Restrictions.eq(ScoPedidoMatExpediente.Fields.NUMERO_PEDIDO.toString(), pedidoPapelaria.getNumPedido()));
		}

		return (ScoPedidoMatExpediente)executeCriteriaUniqueResult(criteria);
	}


	/**
	 * 	ORADB SCOP_GERA_AFP_AUT
	 * @param numeroPedido
	 * @throws AGHUNegocioException
	 */
	public void procedureGeraAfpAut(final Integer numeroPedido) throws ApplicationBusinessException{
		
		if (isOracle()) {			

			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.SCOP_GERA_AFP_AUT;
			try {
				
				this.doWork(new Work() {
					
					@Override
					public void execute(Connection connection) throws SQLException {
						CallableStatement cs = null;
						try {
							cs = connection.prepareCall("{call " + nomeObjeto + "(?)}");
							CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, numeroPedido == null ? null : numeroPedido);
							cs.execute();

						} finally {
							if(cs != null){
								cs.close();
							}
						}
					}

					
				});

			} catch (Exception e) {
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(numeroPedido == null ? null : numeroPedido);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}		
		}
	}
	

	public List<ScoPedidoMatExpediente> buscarPedidosPorNumeroNotaFiscal(Integer numeroNotaFiscal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPedidoMatExpediente.class);
		criteria.add(Restrictions.eq(ScoPedidoMatExpediente.Fields.NUMERO_NOTA_FISCAL.toString(), numeroNotaFiscal));
		
		return executeCriteria(criteria);
	}

    /**
     * ORADB SCOP_GERA_AFP_AUT_AUTOMATICA
     * @param numeroPedido
     * @throws AGHUNegocioException
     */
    public void procedureGeraAfpAutAutomatica(final RapServidores servidorLogado) throws ApplicationBusinessException {
	if (isOracle()) {
	    final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.SCOP_GERA_AFP_AUT_AUTOMATICA;
	    try {
		this.doWork(new Work() {
		    public void execute(Connection connection) throws SQLException {
			CallableStatement cs = null;
			try {
			    cs = connection.prepareCall("{call " + nomeObjeto + "(?,?)}");
			    CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER,
				    servidorLogado.getId().getMatricula() == null ? null : servidorLogado.getId().getMatricula());
			    CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER,
				    servidorLogado.getId().getVinCodigo() == null ? null : servidorLogado.getId().getVinCodigo());
			    cs.execute();
			} finally {
			    if (cs != null) {
				cs.close();
			    }
			}
		    }
		});
	    } catch (Exception e) {
		String valores = CoreUtil.configurarValoresParametrosCallableStatement(servidorLogado.getId().getMatricula() == null ? null
			: servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo() == null ? null : servidorLogado
			.getId().getVinCodigo());
		LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
		throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
		CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
	    }
	}
    }
}
