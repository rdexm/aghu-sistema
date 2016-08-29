package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoPesos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;

/**
 * Classe para acesso a dados do model {@code SigAtividades}.
 * 
 */
public class SigAtividadeEquipamentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigAtividadeEquipamentos> {

	private static final long serialVersionUID = 3762475986877712718L;

	public List<SigAtividadeEquipamentos> pesquisarEquipamentosAtividade(SigAtividades atividades) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividadeEquipamentos.class);
		criteria.setFetchMode(SigAtividadeEquipamentos.Fields.DIRECIONADOR.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(SigAtividadeEquipamentos.Fields.ATIVIDADE.toString(), atividades));
		criteria.addOrder(Order.asc(SigAtividadeEquipamentos.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc(SigAtividadeEquipamentos.Fields.CODIGO_PATRIMONIO.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * SQL Busca equipamentos alocados atividades com peso por rateio
	 * 
	 * Busca todos os equipamentos associados em atividades de objetos de custos com produção no mês de competência do processamento. 
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamentoCusto			Seq do processamento Atual.		
	 * @return								Retorna todos os equipamentos ordenados por centro de custo e código do bem (equipamento).
	 */
	public ScrollableResults buscaEquipamentosAlocadosAtividadesApoio(Integer seqProcessamentoCusto) {
		StringBuilder sql = new StringBuilder(500);

		sql.append("SELECT ")
		.append("tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " as tvd_seq, ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.SEQ.toString() ).append( " as ave_seq, ")
		.append("cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " as cmt_seq, ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " as cct_codigo, ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.CODIGO_PATRIMONIO.toString() ).append( " as cod_patrimonio, ")
		.append("cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( " as dir_seq_atividade, ")
		.append("coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ", 1) as peso_oc ")

		.append("FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append("left join obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope, ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName() ).append( " cbt, ")
		.append(SigAtividades.class.getSimpleName() ).append( " tvd, ")
		.append(SigAtividadeEquipamentos.class.getSimpleName() ).append( " ave ")

		.append("WHERE ")
		.append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamentoCusto ")
		.append("AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString()
				).append( ' ')
		.append("AND cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()
				).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ' ')
		.append("AND cmt." ).append( SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString() ).append( '.' ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString()
				).append( " = cbt." ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString() ).append( ' ')
		.append("AND cbt." ).append( SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString() ).append( '.' ).append( SigAtividades.Fields.SEQ.toString() ).append( " = tvd."
				).append( SigAtividades.Fields.SEQ.toString() ).append( ' ')
		.append("AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv."
				).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( ' ')
		.append("AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString() ).append( ' ')
		.append("AND tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " = ave." ).append( SigAtividadeEquipamentos.Fields.ATIVIDADE.toString() ).append( '.'
				).append( SigAtividades.Fields.SEQ.toString() ).append( ' ')
		.append("AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :indTipo ")

		.append("ORDER BY  ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " , ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.CODIGO_PATRIMONIO.toString() ).append( ' ');

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setInteger("seqProcessamentoCusto", seqProcessamentoCusto);
		createQuery.setString("indTipo", DominioTipoObjetoCusto.AP.toString());

		return createQuery.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * SQL Busca equipamentos alocados atividades com peso por rateio
	 * 
	 * Busca todos os equipamentos associados em atividades de objetos de custos com produção no mês de competência do processamento. 
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamentoCusto		Seq do processamento Atual.		
	 * @return							Retorna todos os equipamentos ordenados por centro de custo e código do bem (equipamento).
	 */
	public ScrollableResults buscaEquipamentosAlocadosAtividadesAssistencial(Integer seqProcessamentoCusto) {
		StringBuilder sql = new StringBuilder(600);

		sql.append("SELECT ")
		.append("tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " as tvd_seq, ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.SEQ.toString() ).append( " as ave_seq, ")
		.append("cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " as cmt_seq, ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " as cct_codigo, ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.CODIGO_PATRIMONIO.toString() ).append( " as cod_patrimonio, ")
		.append("cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( " as dir_seq_atividade, ")
		.append("coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ", 1)*sum(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString()
				).append( ") as peso_oc ")

		.append("FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigDetalheProducao.class.getSimpleName() ).append( " dhp, ")
		.append(SigProducaoObjetoCusto.class.getSimpleName() ).append( " pjc, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append("left join obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope, ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName() ).append( " cbt, ")
		.append(SigAtividades.class.getSimpleName() ).append( " tvd, ")
		.append(SigAtividadeEquipamentos.class.getSimpleName() ).append( " ave ")

		.append("WHERE ")
		.append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamentoCusto ")
		.append("and pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() )
			.append( " = dhp." ).append( SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO_SEQ.toString() ).append( ' ')
		.append("and pjc." ).append( SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString() ).append( '.' ).append( SigDetalheProducao.Fields.SEQ.toString() )
			.append( " = dhp.").append( SigDetalheProducao.Fields.SEQ.toString() ).append( ' ')
		.append("and pjc." ).append( SigProducaoObjetoCusto.Fields.CALCULO_OBJETO_CUSTOS.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() )
			.append( " = cbj.").append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ' ')
		.append("and cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
			.append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ' ')
		.append("and cmt." ).append( SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString() ).append( '.' ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString())
			.append( " = cbt." ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString() ).append( ' ')
		.append("and cbt." ).append( SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString() ).append( '.' ).append( SigAtividades.Fields.SEQ.toString() )
			.append( " = tvd.").append( SigAtividades.Fields.SEQ.toString() ).append( ' ')
		.append("and cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() )
			.append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( ' ')
		.append("and ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() )
			.append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString() ).append( ' ')
		.append("and tvd." ).append( SigAtividades.Fields.SEQ.toString() )
			.append( " = ave." ).append( SigAtividadeEquipamentos.Fields.ATIVIDADE.toString() ).append( '.').append( SigAtividades.Fields.SEQ.toString() ).append( ' ')
		.append("and dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " in (:grupos) ")
		.append("and obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :indTipo ")

		.append("GROUP BY ")
		.append("tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( ", ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.SEQ.toString() ).append( ", ")
		.append("cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( ", ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.CODIGO_PATRIMONIO.toString() ).append( ", ")
		.append("cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( ", ")
		.append("ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ' ')

		.append("ORDER BY  ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " , ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.CODIGO_PATRIMONIO.toString() ).append( ' ');

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});
		createQuery.setInteger("seqProcessamentoCusto", seqProcessamentoCusto);
		createQuery.setParameter("indTipo", DominioTipoObjetoCusto.AS);

		return createQuery.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}
}