package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSigTipoAlerta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtividadeInsumo;
import br.gov.mec.aghu.model.SigCalculoAtividadePessoa;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoAlertas;
import br.gov.mec.aghu.model.SigProcessamentoAnalises;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAlertasProcessamentoVO;

public class SigProcessamentoAlertasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigProcessamentoAlertas> {

	private static final long serialVersionUID = 7762139326955620318L;
	
	public void removerPorProcessamento(Integer idProcessamentoCusto){
		StringBuilder sql = new StringBuilder(50);
		sql.append(" DELETE ").append(SigProcessamentoAlertas.class.getSimpleName().toString()).append(" a ");
		sql.append(" WHERE a.").append(SigProcessamentoAlertas.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}
	
	/**
	 * Método executa o sql e já cria a lista de VOs para retorno
	 * 
	 * @author rmalvezzi
	 * @param createQuery
	 * @return 				Lista com os alertas
	 */
	@SuppressWarnings("unchecked")
	private List<VisualizarAlertasProcessamentoVO> montarExecutaHQL(Query createQuery) {
		List<VisualizarAlertasProcessamentoVO> alertasProcessamentoVOs = new ArrayList<VisualizarAlertasProcessamentoVO>();
		List<Object[]> list = createQuery.getResultList();
		for (Object[] objects : list) {
			alertasProcessamentoVOs.add(VisualizarAlertasProcessamentoVO.createCentroCusto(objects));
		}
		return alertasProcessamentoVOs;
	}
	
	/**
	 * Cria um Objeto {@link Query} com o sql e filtros passados por parametro 
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamento
	 * @param codigoCentroCusto
	 * @param sql
	 * @return  				{@link Query}
	 */
	private Query criarSetaQuery(Integer seqProcessamento, Integer codigoCentroCusto, StringBuilder sql) {
		Query createQuery = this.createQuery(sql.toString());
		createQuery.setParameter("seqProcessamento", seqProcessamento);
		if (codigoCentroCusto != null) {
			createQuery.setParameter("codigoCentroCusto", codigoCentroCusto);
		}
		return createQuery;
	}

	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCCSemParecer(Integer seqProcessamento, Integer codigoCentroCusto) {
		StringBuilder sql = new StringBuilder(300);

		sql.append("SELECT DISTINCT 'Centro de custo sem análise do processamento' as TIPO, ")
		.append(" '' as obj_custo, '' as atividade, fcc.codigo, fcc.descricao ")
		.append(" FROM " ).append( SigProcessamentoAnalises.class.getSimpleName() ).append( " pa1, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " fcc ")
		.append(" WHERE pa1." ).append( SigProcessamentoAnalises.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()
				).append( " = :seqProcessamento")
		.append(" AND pa1." ).append( SigProcessamentoAnalises.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = fcc."
				).append( FccCentroCustos.Fields.CODIGO)
		.append(" AND pa1." ).append( SigProcessamentoAnalises.Fields.PARECER.toString() ).append( " is null ");

		if (codigoCentroCusto != null) {
			sql.append(" AND pa1." ).append( SigProcessamentoAnalises.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()
					).append( " = :codigoCentroCusto");
		}

		return this.montarExecutaHQL(this.criarSetaQuery(seqProcessamento, codigoCentroCusto, sql));
	}

	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCCSemObjCustoAtivo(Integer seqProcessamento, Integer codigoCentroCusto) {
		StringBuilder sql = new StringBuilder(300);

		sql.append("SELECT DISTINCT 'Centro de custo sem objetos de custos ativos' as TIPO, ")
		.append(" '' as obj_custo, '' as atividade, fcc.codigo, fcc.descricao ")
		.append(" FROM " ).append( SigProcessamentoAlertas.class.getSimpleName() ).append( " pa1, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " fcc ")
		.append(" WHERE pa1." ).append( SigProcessamentoAlertas.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()
				).append( " = :seqProcessamento")
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.TIPO_ALERTA.toString() ).append( " = :tipoAlertaNC")
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = fcc."
				).append( FccCentroCustos.Fields.CODIGO);

		if (codigoCentroCusto != null) {
			sql.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()
					).append( " = :codigoCentroCusto");
		}

		Query createQuery = this.criarSetaQuery(seqProcessamento, codigoCentroCusto, sql);
		createQuery.setParameter("tipoAlertaNC", DominioSigTipoAlerta.NC);

		return this.montarExecutaHQL(createQuery);
	}

	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCP(Integer seqProcessamento, Integer codigoCentroCusto) {
		StringBuilder sql = new StringBuilder(350);

		sql.append("SELECT DISTINCT 'Clientes sem peso/valor' AS TIPO, ")
		.append("obj." ).append( SigObjetoCustos.Fields.NOME.toString() ).append( ", ")
		.append(" '' as atividade, ")
		.append("fcc." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("fcc." ).append( FccCentroCustos.Fields.DESCRICAO.toString())
		.append(" FROM " ).append( SigProcessamentoAlertas.class.getSimpleName() ).append( " pa1, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " fcc, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(SigObjetoCustoClientes.class.getSimpleName() ).append( " occ ")

		.append(" WHERE pa1." ).append( SigProcessamentoAlertas.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()
				).append( " = :seqProcessamento")
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.TIPO_ALERTA.toString() ).append( " = :tipoAlertaCR")
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = occ."
				).append( SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO)
		.append(" AND occ." ).append( SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " = fcc."
				).append( FccCentroCustos.Fields.CODIGO)
		.append(" AND occ." ).append( SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString() ).append( " = ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ)
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString());

		if (codigoCentroCusto != null) {
			sql.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()
					).append( " = :codigoCentroCusto");
		}

		Query createQuery = this.criarSetaQuery(seqProcessamento, codigoCentroCusto, sql);
		createQuery.setParameter("tipoAlertaCR", DominioSigTipoAlerta.CP);

		return this.montarExecutaHQL(createQuery);
	}

	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCCSomenteRateio(Integer seqProcessamento, Integer codigoCentroCusto) {
		StringBuilder sql = new StringBuilder(400);

		sql.append("SELECT DISTINCT 'Cálculo somente por rateio' AS TIPO, ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME ).append( " || ' - Versão: ' || ocv." ).append( SigObjetoCustoVersoes.Fields.NRO_VERSAO
				).append( " as obj_custo, '' as atividade, ")
		.append("fcc." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("fcc." ).append( FccCentroCustos.Fields.DESCRICAO.toString())
		.append(" FROM " ).append( SigProcessamentoAlertas.class.getSimpleName() ).append( " pa1, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " fcc ")
		.append(" WHERE pa1." ).append( SigProcessamentoAlertas.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()
				).append( " = :seqProcessamento")
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.TIPO_ALERTA.toString() ).append( " = :tipoAlertaCR")
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj."
				).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ)
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv."
				).append( SigObjetoCustoVersoes.Fields.SEQ)
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString())
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = fcc."
				).append( FccCentroCustos.Fields.CODIGO)
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_INSUMOS.toString() ).append( " = 0")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_PESSOAL.toString() ).append( " = 0")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_EQUIPAMENTO.toString() ).append( " = 0")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_SERVICO.toString() ).append( " = 0");

		if (codigoCentroCusto != null) {
			sql.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()
				).append( " = :codigoCentroCusto");
		}

		Query createQuery = this.criarSetaQuery(seqProcessamento, codigoCentroCusto, sql);
		createQuery.setParameter("tipoAlertaCR", DominioSigTipoAlerta.CR);

		return this.montarExecutaHQL(createQuery);
	}
	@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCCQtAjustadaPessoas(Integer seqProcessamento, Integer codigoCentroCusto) {
		StringBuilder sql = this.buscarAlertasPorProcessamentoCCQt("Quantidade ajustada de alocação de pessoas", codigoCentroCusto);
		sql.append(" AND cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " in ( SELECT cmt." ).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" FROM " ).append( SigCalculoAtividadePessoa.class.getSimpleName() ).append( " cap WHERE cap.")
		.append(SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE.toString() ).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cmt."
				).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" AND cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_PREVISTA ).append( " > cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_REALIZADA ).append( ')');

		Query createQuery = this.criarSetaQuery(seqProcessamento, codigoCentroCusto, sql);
		createQuery.setParameter("tipoAlertaQA", DominioSigTipoAlerta.QA);

		return this.montarExecutaHQL(createQuery);
	}
	@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCCQtAjustadaInsumos(Integer seqProcessamento, Integer codigoCentroCusto) {
		StringBuilder sql = this.buscarAlertasPorProcessamentoCCQt("Quantidade ajustada de alocação de insumos", codigoCentroCusto);
		sql.append(" AND cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " in ( SELECT cmt." ).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" FROM " ).append( SigCalculoAtividadeInsumo.class.getSimpleName() ).append( " cvn WHERE cvn.")
		.append(SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString() ).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cvn."
				).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" AND cvn." ).append( SigCalculoAtividadeInsumo.Fields.QUANTIDADE_PREVISTA ).append( " > cvn." ).append( SigCalculoAtividadeInsumo.Fields.QUANTIDADE_REALIZADA ).append( ')');

		Query createQuery = this.criarSetaQuery(seqProcessamento, codigoCentroCusto, sql);
		createQuery.setParameter("tipoAlertaQA", DominioSigTipoAlerta.QA);

		return this.montarExecutaHQL(createQuery);
	}

	private StringBuilder buscarAlertasPorProcessamentoCCQt(String tipo, Integer codigoCentroCusto) {
		StringBuilder sql = new StringBuilder(400);
		sql.append("SELECT DISTINCT '" ).append( tipo ).append( "' AS TIPO, ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME ).append( " || ' - Versão: ' || ocv." ).append( SigObjetoCustoVersoes.Fields.NRO_VERSAO ).append( " as obj_custo, ")
		.append(" tvd." ).append( SigAtividades.Fields.NOME.toString() ).append( " as atividade, ")
		.append(" fcc." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" fcc." ).append( FccCentroCustos.Fields.DESCRICAO)
		.append(" FROM " ).append( SigProcessamentoAlertas.class.getSimpleName() ).append( " pa1, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName() ).append( " cbt, ")
		.append(SigAtividades.class.getSimpleName() ).append( " tvd, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " fcc ")
		.append(" WHERE pa1." ).append( SigProcessamentoAlertas.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()
				).append( " = :seqProcessamento")
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.TIPO_ALERTA.toString() ).append( " = :tipoAlertaQA")
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj."
				).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ)
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv."
				).append( SigObjetoCustoVersoes.Fields.SEQ)
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString())
		.append(" AND cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString()
				).append( " = cbj." ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString())
		.append(" AND cmt." ).append( SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString() ).append( '.' ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString()
				).append( " = cbt." ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString())
		.append(" AND cbt." ).append( SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString() ).append( '.' ).append( SigAtividades.Fields.SEQ.toString() ).append( " = tvd."
				).append( SigAtividades.Fields.SEQ.toString())
		.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = fcc."
				).append( FccCentroCustos.Fields.CODIGO);

		if (codigoCentroCusto != null) {
			sql.append(" AND pa1." ).append( SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString() ).append('.').append( FccCentroCustos.Fields.CODIGO.toString()
				).append( " = :codigoCentroCusto");
		}
		return sql;
	}

	public Long buscarTotalAlertasPorProcessamentoTipoAlerta(SigProcessamentoCusto processamentoCusto, DominioSigTipoAlerta tipoAlerta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoAlertas.class);
		criteria.setProjection(Projections.distinct(Projections.property(SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString())));
		criteria.add(Restrictions.eq(SigProcessamentoAlertas.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		criteria.add(Restrictions.eq(SigProcessamentoAlertas.Fields.TIPO_ALERTA.toString(), tipoAlerta));
		List<Object> list = this.executeCriteria(criteria);
		return (list != null) ? (long)list.size() : 0L;
	}
	
}
