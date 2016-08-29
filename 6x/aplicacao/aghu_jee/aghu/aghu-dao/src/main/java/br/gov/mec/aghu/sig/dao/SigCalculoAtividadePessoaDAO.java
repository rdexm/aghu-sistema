package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtividadePessoa;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigCalculoAtividadePessoaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtividadePessoa> {

	private static final long serialVersionUID = -1694501276140997031L;

	public void removerPorProcessamento(Integer idProcessamento) {

		StringBuilder sql = new StringBuilder(100);

		sql.append(" DELETE " ).append( SigCalculoAtividadePessoa.class.getSimpleName().toString() ).append( " ca ")
		.append(" WHERE ca." ).append( SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE.toString() ).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" IN ( ")
		.append(" SELECT c." ).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" FROM " ).append( SigCalculoComponente.class.getSimpleName() ).append( " c ")
		.append(" WHERE c." ).append( SigCalculoComponente.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq")
		.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}

	/**
	 * Busca grupos de ocupação com alocação excedente
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamento identificador do processamento atual
	 * @param tipoObjetoCusto tipo do objeto de custo
	 * @return  retorna todos os grupos de ocupação de um centro de custo cujos valores previstos de alocação de pessoas nas atividades excederam a 
	 * quantidade disponível na competência do processamento
	 */
	public ScrollableResults buscarGruposOcupacaoAlocacaoExcedente(Integer seqProcessamento, DominioTipoObjetoCusto tipoObjetoCusto) {

		StringBuilder hql = new StringBuilder(500);

		hql.append("SELECT DISTINCT ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " as cct_codigo, ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.GRUPO_OCUPACOES ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( " as goc_seq, ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.QTDE ).append( " as qtde_disponivel, ")
		.append("sum(cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_PREVISTA ).append( ") as qtde_prevista ")
		.append("FROM ")
		.append(SigCalculoAtividadePessoa.class.getSimpleName() ).append( " cap, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append("WHERE ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ ).append( " = :seqProcessamento ")
		.append("and cap." ).append( SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE ).append( '.' ).append( SigCalculoComponente.Fields.SEQ ).append( " = cmt."
				).append( SigCalculoComponente.Fields.SEQ ).append( ' ')
		.append("and cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( " = cbj."
				).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ' ')
		.append("and msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " = cbj."
				).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ' ')
		.append("and msl." ).append( SigMvtoContaMensal.Fields.GRUPO_OCUPACAO ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( " = cap."
				).append( SigCalculoAtividadePessoa.Fields.GRUPO_OCUPACOES ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( ' ')
		.append("AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv."
				).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( ' ')
		.append("AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString() ).append( ' ')
		.append("AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoObjetoCusto ")
		.append("and msl." ).append( SigMvtoContaMensal.Fields.VALOR ).append( " > 0 ")
		.append("and msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO ).append( " in ( :tipoSIP, :tipoSIT ) ")
		.append("and msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ ).append( " = :seqProcessamento ")
		.append("GROUP BY ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.GRUPO_OCUPACOES ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.QTDE ).append( ' ')
		.append("HAVING sum(cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_PREVISTA ).append( ") > msl." ).append( SigMvtoContaMensal.Fields.QTDE ).append( ' ');

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqProcessamento", seqProcessamento);
		query.setParameter("tipoObjetoCusto", tipoObjetoCusto);
		query.setParameter("tipoSIP", DominioTipoMovimentoConta.SIP);
		query.setParameter("tipoSIT", DominioTipoMovimentoConta.SIT);
		
		return query.setFetchSize(10).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Busca atividades com alocação excedente
	 * 
	 * @author rogeriovieira
	 * @param seqGrupoOcupacao identificador do grupo de ocupação
	 * @param seqProcessamento indentificador do processamento atual
	 * @param codigoCentroCusto identificador do centro de custo
	 * @param tipoObjetoCusto tipo do objeto de custo
	 * @return retorna todas as atividades cuja soma dos tempos previstos excede a disponibilidade no centro de custo no grupo de ocupação
	 */
	public ScrollableResults buscarAtividadesAlocacaoExcedente(Integer seqProcessamento, Integer seqGrupoOcupacao, Integer codigoCentroCusto, DominioTipoObjetoCusto tipoObjetoCusto) {

		StringBuilder hql = new StringBuilder(300);

		hql.append("SELECT ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.SEQ ).append( ", ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_PREVISTA ).append( " as qtde_prevista_atividade, ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.VALOR_GRUPO_OCUPACAO ).append( ' ')
		.append("FROM ")
		.append(SigCalculoAtividadePessoa.class.getSimpleName() ).append( " cap, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append("WHERE ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.GRUPO_OCUPACOES ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( " = :seqGrupoOcupacao ")
		.append("and cap." ).append( SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE ).append( '.' ).append( SigCalculoComponente.Fields.SEQ ).append( " = cmt."
				).append( SigCalculoComponente.Fields.SEQ ).append( ' ')
		.append("and cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( " = cbj."
				).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ' ')
		.append("AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv."
				).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( ' ')
		.append("AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString() ).append( ' ')
		.append("AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoObjetoCusto  ")
		.append("and cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ ).append( " = :seqProcessamento ")
		.append("and cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " = :codigoCentroCusto ");

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqGrupoOcupacao", seqGrupoOcupacao);
		query.setParameter("seqProcessamento", seqProcessamento);
		query.setParameter("codigoCentroCusto", codigoCentroCusto);
		query.setParameter("tipoObjetoCusto", tipoObjetoCusto);
		
		return query.setFetchSize(10).scroll(ScrollMode.FORWARD_ONLY);
		
	}

	/**
	 * Busca Atividades com Valores Realizados Calculados
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamento identificador do processamento atual
	 * @param tipoObjetoCusto tipo do objeto de custo
	 * @return todas as atividades que tiveram quantidades realizadas e valores calculados nas etapas anteriores
	 */
	public ScrollableResults buscarAtividadesComValoresRealizadosCalculados(Integer seqProcessamento, DominioTipoObjetoCusto tipoObjetoCusto) {

		StringBuilder hql = new StringBuilder(320);

		hql.append("SELECT ")
		.append("ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ).append( ", ")
		.append("cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ", ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE ).append( '.' ).append( SigCalculoComponente.Fields.SEQ ).append( ", ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.GRUPO_OCUPACOES ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( ", ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_REALIZADA ).append( ", ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.VALOR_GRUPO_OCUPACAO ).append( ", ")
		.append("tvd." ).append( SigAtividades.Fields.SEQ).append(", ")
		.append("tvd." ).append(SigAtividades.Fields.NOME).append(", ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.PAGADOR).append( '.').append(AacPagador.Fields.SEQ).append(' ')
		.append("FROM ")
		.append(SigCalculoAtividadePessoa.class.getSimpleName() ).append( " cap, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(SigAtividades.class.getSimpleName() ).append( " tvd ")
		.append("WHERE ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ ).append( " = :seqProcessamento ")
		.append("and cap." ).append( SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE ).append( '.' ).append( SigCalculoComponente.Fields.SEQ )
			.append( " = cmt.").append( SigCalculoComponente.Fields.SEQ ).append( ' ')
		.append("and cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ )
		.append( " = cbj.").append( SigCalculoObjetoCusto.Fields.SEQ ).append( ' ')
		.append("and cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_REALIZADA ).append( " > 0 ")
		.append("AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() )
			.append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( ' ')
		.append("AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() )
			.append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString() ).append( ' ')
		.append("AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoObjetoCusto ")
		.append("AND cap." ).append( SigCalculoAtividadePessoa.Fields.ATIVIDADES ).append( '.' ).append( SigAtividades.Fields.SEQ ).append( " = tvd."
				).append( SigAtividades.Fields.SEQ ).append( ' ')
		.append("ORDER BY ")
		.append("ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ).append( " asc, ")
		.append("tvd." ).append( SigAtividades.Fields.SEQ).append(" asc, ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.GRUPO_OCUPACOES ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( " asc, ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.PAGADOR).append( '.').append(AacPagador.Fields.SEQ).append(" desc ")
		;


		org.hibernate.Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("seqProcessamento", seqProcessamento);
		query.setParameter("tipoObjetoCusto", tipoObjetoCusto);
		
		return query.setFetchSize(10).scroll(ScrollMode.FORWARD_ONLY);
	}
}
