package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;

public class SigAtividadePessoasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigAtividadePessoas> {

	private static final long serialVersionUID = 1788724807011316336L;

	public List<SigAtividadePessoas> pesquisarPessoasPorSeqAtividade(Integer seqAtividade) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividadePessoas.class, "atvPessoas");
		criteria.createCriteria("atvPessoas."+SigAtividadePessoas.Fields.GRUPO_OCUPACAO, "grOcup", JoinType.INNER_JOIN);
		criteria.createCriteria("atvPessoas."+SigAtividadePessoas.Fields.DIRECIONADOR, "dir", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(SigAtividadePessoas.Fields.ATIVIDADE_SEQ.toString(), seqAtividade));
		//#24619 - Pessoas: situação, descrição do grupo de ocupação
		criteria.addOrder(Order.asc("atvPessoas."+SigAtividadePessoas.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc("grOcup."+SigGrupoOcupacoes.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	/**
	 * Busca grupos de ocupação alocados em atividades de objetos de custo assistenciais
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamento identificador do processamento atual
	 * @param grupo grupo de ocupação
	 * @return retorna todos os grupos de ocupação associados em atividades de objetos de custos com produção no mês de competência do processamento
	 */
	public ScrollableResults buscarGruposOcupacaoAlocadosAtividade(Integer seqProcessamento) {
		StringBuilder hql = new StringBuilder(800);

		hql.append("SELECT ")
		.append("tvd.").append(SigAtividades.Fields.SEQ.toString()).append(" as tvd_seq, ")
		.append("avp.").append(SigAtividadePessoas.Fields.SEQ.toString()).append(" as ais_seq, ")
		.append("cmt.").append(SigCalculoComponente.Fields.SEQ.toString()).append(" as cmt_seq, ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()).append('.').append(FccCentroCustos.Fields.CODIGO.toString()).append(" as cct_codigo, ")
		.append("avp.").append(SigAtividadePessoas.Fields.GRUPO_OCUPACAO.toString()).append('.').append(SigGrupoOcupacoes.Fields.SEQ.toString()).append(" as goc_seq, ")
		.append("avp.").append(SigAtividadePessoas.Fields.QUANTIDADE.toString()).append(" as qtde_profissionais, ")
		.append("(avp.").append(SigAtividadePessoas.Fields.TEMPO.toString()).append(" * dirtempo.").append(SigDirecionadores.Fields.FAT_CONV_HORAS.toString()).append(") as qtde_tempo, ")
		.append("avp.").append(SigAtividadePessoas.Fields.DIRECIONADOR.toString()).append('.').append(SigDirecionadores.Fields.SEQ.toString()).append(" as dir_seq_tempo, ")
		.append("cmt.").append(SigCalculoComponente.Fields.DIRECIONADOR.toString()).append('.').append(SigDirecionadores.Fields.SEQ.toString()).append(" as dir_seq_atividade, ")
		.append("cbt.").append(SigObjetoCustoComposicoes.Fields.NRO_EXECUCAO.toString()).append(" as nro_execucoes, ")
		.append("pmu.").append(SigProcessamentoCusto.Fields.DATA_INICIO.toString()).append(" as proc_data_inicio, ")
		.append("pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM.toString()).append(" as proc_data_fim, ")
		.append("sum(dhp.").append(SigDetalheProducao.Fields.NRO_DIAS_PRODUCAO.toString()).append(") as nro_dias_producao, ")
		.append("sum(dhp.").append(SigDetalheProducao.Fields.QTDE.toString()).append(") as qtde ")
		.append("FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName()).append(" pmu, ")
		.append(SigDetalheProducao.class.getSimpleName()).append(" dhp, ")
		.append(SigProducaoObjetoCusto.class.getSimpleName()).append(" pjc, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName()).append(" cbj, ")		
		.append(SigObjetoCustoVersoes.class.getSimpleName()).append(" ocv, ")
		.append(SigObjetoCustos.class.getSimpleName()).append(" obj, ")		
		.append(SigCalculoComponente.class.getSimpleName()).append(" cmt, ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName()).append(" cbt, ")
		.append(SigAtividades.class.getSimpleName()).append(" tvd, ")
		.append(SigAtividadePessoas.class.getSimpleName()).append(" avp ")
		.append("LEFT JOIN avp.").append(SigAtividadePessoas.Fields.DIRECIONADOR.toString()).append(" as dirtempo, ")
		.append(SigGrupoOcupacoes.class.getSimpleName()).append(" goc ")
		.append("WHERE ")
		.append("pmu.").append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :seqProcessamento ")
		.append("AND pmu.").append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = dhp.").append(SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO_SEQ.toString()).append(' ')
		.append("AND pjc.").append(SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString()).append('.').append(SigDetalheProducao.Fields.SEQ.toString()).append(" = dhp.").append(SigDetalheProducao.Fields.SEQ.toString()).append(' ')
		.append("AND pjc.").append(SigProducaoObjetoCusto.Fields.CALCULO_OBJETO_CUSTOS.toString()).append('.').append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(" = cbj.").append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(' ')
		.append("AND cmt.").append(SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString()).append('.').append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(" = cbj.").append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(' ')
		.append("AND cmt.").append(SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString()).append('.').append(SigObjetoCustoComposicoes.Fields.SEQ.toString()).append(" = cbt.").append(SigObjetoCustoComposicoes.Fields.SEQ.toString()).append(' ')
		.append("AND cbt.").append(SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString()).append('.').append(SigAtividades.Fields.SEQ.toString()).append(" = tvd.").append(SigAtividades.Fields.SEQ.toString()).append(' ')
		.append("AND tvd.").append(SigAtividades.Fields.SEQ.toString()).append(" = avp.").append(SigAtividadePessoas.Fields.ATIVIDADE_SEQ.toString()).append(' ')
		.append("AND avp.").append(SigAtividadePessoas.Fields.GRUPO_OCUPACAO.toString()).append('.').append(SigGrupoOcupacoes.Fields.SEQ.toString()).append(" = goc.").append(SigGrupoOcupacoes.Fields.SEQ.toString()).append(' ')
		.append("AND cbj.").append(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString()).append('.').append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(" = ocv.").append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(' ')
		.append("AND ocv.").append(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString()).append('.').append(SigObjetoCustos.Fields.SEQ.toString()).append(" = obj.").append(SigObjetoCustos.Fields.SEQ.toString()).append(' ')
		.append("AND obj.").append(SigObjetoCustos.Fields.IND_TIPO.toString()).append(" = :tipoObjetoCusto ")
		.append("AND dhp.").append(SigDetalheProducao.Fields.GRUPO.toString()).append(" in (:grupos) ")
		.append("GROUP BY ")
		.append("tvd.").append(SigAtividades.Fields.SEQ.toString()).append(", ")
		.append("avp.").append(SigAtividadePessoas.Fields.SEQ.toString()).append(", ")
		.append("cmt.").append(SigCalculoComponente.Fields.SEQ.toString()).append(", ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()).append('.').append(FccCentroCustos.Fields.CODIGO.toString()).append(", ")
		.append("avp.").append(SigAtividadePessoas.Fields.GRUPO_OCUPACAO.toString()).append('.').append(SigGrupoOcupacoes.Fields.SEQ.toString()).append(", ")
		.append("avp.").append(SigAtividadePessoas.Fields.QUANTIDADE.toString()).append(", ")
		.append("avp.").append(SigAtividadePessoas.Fields.TEMPO.toString()).append(", ")
		.append("avp.").append(SigAtividadePessoas.Fields.DIRECIONADOR.toString()).append('.').append(SigDirecionadores.Fields.SEQ.toString()).append(", ")
		.append("dirtempo.").append(SigDirecionadores.Fields.FAT_CONV_HORAS.toString()).append(", ")
		.append("cmt.").append(SigCalculoComponente.Fields.DIRECIONADOR.toString()).append('.').append(SigDirecionadores.Fields.SEQ.toString()).append(", ")
		.append("cbt.").append(SigObjetoCustoComposicoes.Fields.NRO_EXECUCAO.toString()).append(", ")
		.append("pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM.toString()).append(", ")
		.append("pmu.").append(SigProcessamentoCusto.Fields.DATA_INICIO.toString()).append(' ');
		
		
		Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("seqProcessamento", seqProcessamento);
		query.setParameter("tipoObjetoCusto", DominioTipoObjetoCusto.AS);
		query.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});
		
		return query.setFetchSize(10).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Busca grupos de ocupação alocados em atividades com objeto de custo de apoio
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamento  identificador do processamento atual
	 * @return retorna todos os grupos de ocupação associados em atividades de objetos de custos com produção no mês de competência do processamento
	 */
	public ScrollableResults buscarGruposOcupacaoAlocadosAtividadeApoio(Integer seqProcessamento) {
		StringBuilder hql = new StringBuilder(600);

		hql.append("SELECT ")
		.append("tvd.").append(SigAtividades.Fields.SEQ.toString()).append(" as tvd_seq, ")
		.append("avp.").append(SigAtividadePessoas.Fields.SEQ.toString()).append(" as ais_seq, ")
		.append("cmt.").append(SigCalculoComponente.Fields.SEQ.toString()).append(" as cmt_seq, ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()).append('.').append(FccCentroCustos.Fields.CODIGO.toString()).append(" as cct_codigo, ")
		.append("avp.").append(SigAtividadePessoas.Fields.GRUPO_OCUPACAO.toString()).append('.').append(SigGrupoOcupacoes.Fields.SEQ.toString()).append(" as goc_seq, ")
		.append("avp.").append(SigAtividadePessoas.Fields.QUANTIDADE.toString()).append(" as qtde_profissionais, ")
		.append("(avp.").append(SigAtividadePessoas.Fields.TEMPO.toString()).append(" * dirtempo.").append(SigDirecionadores.Fields.FAT_CONV_HORAS.toString()).append(") as qtde_tempo, ")
		.append("avp.").append(SigAtividadePessoas.Fields.DIRECIONADOR.toString()).append('.').append(SigDirecionadores.Fields.SEQ.toString()).append(" as dir_seq_tempo, ")
		.append("cmt.").append(SigCalculoComponente.Fields.DIRECIONADOR.toString()).append('.').append(SigDirecionadores.Fields.SEQ.toString()).append(" as dir_seq_atividade, ")
		.append("cbt.").append(SigObjetoCustoComposicoes.Fields.NRO_EXECUCAO.toString()).append(" as nro_execucoes, ")
		.append("pmu.").append(SigProcessamentoCusto.Fields.DATA_INICIO.toString()).append(" as proc_data_inicio, ")
		.append("pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM.toString()).append(" as proc_data_fim ")
		.append("FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName()).append(" pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName()).append(" cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName()).append(" ocv, ")
		.append(SigObjetoCustos.class.getSimpleName()).append(" obj, ")
		.append(SigCalculoComponente.class.getSimpleName()).append(" cmt, ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName()).append(" cbt, ")
		.append(SigAtividades.class.getSimpleName()).append(" tvd, ")
		.append(SigAtividadePessoas.class.getSimpleName()).append(" avp ")
		.append("LEFT JOIN avp.").append(SigAtividadePessoas.Fields.DIRECIONADOR.toString()).append(" as dirtempo, ")
		.append(SigGrupoOcupacoes.class.getSimpleName()).append(" goc ")
		.append("WHERE ")
		.append("pmu.").append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :seqProcessamento ")
		.append("AND pmu.").append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = cbj.").append(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString()).append(' ')
		.append("AND cbj.").append(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString()).append('.').append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(" = ocv.").append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(' ')
		.append("AND ocv.").append(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString()).append('.').append(SigObjetoCustos.Fields.SEQ.toString()).append(" = obj.").append(SigObjetoCustos.Fields.SEQ.toString()).append(' ')
		.append("AND cmt.").append(SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString()).append('.').append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(" = cbj.").append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(' ')
		.append("AND cmt.").append(SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString()).append('.').append(SigObjetoCustoComposicoes.Fields.SEQ.toString()).append(" = cbt.").append(SigObjetoCustoComposicoes.Fields.SEQ.toString()).append(' ')
		.append("AND cbt.").append(SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString()).append('.').append(SigAtividades.Fields.SEQ.toString()).append(" = tvd.").append(SigAtividades.Fields.SEQ.toString()).append(' ')
		.append("AND tvd.").append(SigAtividades.Fields.SEQ.toString()).append(" = avp.").append(SigAtividadePessoas.Fields.ATIVIDADE_SEQ.toString()).append(' ')
		.append("AND avp.").append(SigAtividadePessoas.Fields.GRUPO_OCUPACAO.toString()).append('.').append(SigGrupoOcupacoes.Fields.SEQ.toString()).append(" = goc.").append(SigGrupoOcupacoes.Fields.SEQ.toString()).append(' ')
		.append("AND obj.").append(SigObjetoCustos.Fields.IND_TIPO.toString()).append(" = :tipoObjetoCusto ");
		
		Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("seqProcessamento", seqProcessamento);
		query.setParameter("tipoObjetoCusto", DominioTipoObjetoCusto.AP);
		
		return query.setFetchSize(10).scroll(ScrollMode.FORWARD_ONLY);
	}
}
