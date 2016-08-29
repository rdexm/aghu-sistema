package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItensPendentesPacVO;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialVinculo;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPareceresMateriais;
import br.gov.mec.aghu.model.ScoPareceresTecnicos;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;

/**
 * @author bruno.mourao
 * 
 */
public class ScoItemLicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoItemLicitacao> {

	private static final long serialVersionUID = 2302976587833963561L;

	/**
	 * Obtém uma lista de itens da licitação informada e, se informado, somente com os itens indicados.
	 * @param numLicitacao
	 * @param itens
	 * @return
	 * @author bruno.mourao
	 * @since 07/06/2011
	 */
	public List<ScoItemLicitacao> pesquisarItemLicitacaoPorNumLicitacaoNumItens(Integer numLicitacao, List<Integer> itens) {
		// Cria a criteria
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class);

		// Busca onde o num licitação é igual ao informado
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),numLicitacao));
		
		//se existir itens informados, busca somente estes itens
		if(itens != null && !itens.isEmpty()){
			criteria.add(Restrictions.in(ScoItemLicitacao.Fields.CLASSIF_ITEM.toString(), itens));
		}

		return this.executeCriteria(criteria);
	}

	/**
	 * Retorna o próximo número da ordem dos itens a compor a licitação
	 * @param numeroLicitacao
	 * @return
	 * @author clayton.bras
	 * 
	 */
	public Integer proximosItensLicitacao(Integer numeroLicitacao) {

		final Short SOMA_MAIS_UM_PROX_ITEM = 1;
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class);

		criteria.setProjection(Projections.projectionList()	.add(Projections.max(ScoItemLicitacao.Fields.CLASSIF_ITEM
						.toString())));
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numeroLicitacao));

		Short numeroClassifItem = (Short) executeCriteriaUniqueResult(criteria);
		
		if (numeroClassifItem!=null) {
			return numeroClassifItem.intValue() + SOMA_MAIS_UM_PROX_ITEM.intValue();
		}
		else {
			return SOMA_MAIS_UM_PROX_ITEM.intValue();
		}

	}
	
	/**
	 * Obtém uma lista de itens que possuem o número da licitação e indicativo de exclusão iguais aos
	 * informados.
	 * 
	 * @param numLicitacao, indExclusao
	 */
	public List<ScoItemLicitacao> pesquisarItemLicitacaoPorNumLicitacaoEIndExclusao(
			Integer numLicitacao, Boolean indExclusao) {
		// Cria a criteria
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class);

		// Busca onde o num licitação é igual ao informado
		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),
				numLicitacao));

		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(),
				indExclusao));

		criteria.addOrder(Order.asc(ScoItemLicitacao.Fields.NUMERO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	public List<ScoItemLicitacao> pesquisarItemLicitacaoPorNumLicitacaoEIndExclusao(
			Integer numLicitacao, Boolean indExclusao, Integer numItemLicitacaoInicial, Integer numItemLicitacaoFinal) {
		// Cria a criteria
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class);

		// Busca onde o num licitação é igual ao informado
		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),
				numLicitacao));

		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(),
				indExclusao));
		
		criteria.add(Restrictions.between(ScoItemLicitacao.Fields.NUMERO.toString(), numItemLicitacaoInicial.shortValue(), numItemLicitacaoFinal.shortValue()));

		criteria.addOrder(Order.asc(ScoItemLicitacao.Fields.NUMERO.toString()));
		
		return this.executeCriteria(criteria);
	}

	/**
	 * Obtém o número de itens que possuem o número da licitação, indicativo de exclusão e indicativo de proposta escolhida iguais aos
	 * informados.
	 * 
	 * @param numLicitacao, indExclusao
	 */
	public Long pesquisarItemLicitacaoPorNumLicitacaoEIndExclusaoEIndPropostaEscolhidaCount(
			Integer numLicitacao, Boolean indExclusao, Boolean indPropostaEscolhida) {
		// Cria a criteria
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class);

		// Busca onde o num licitação é igual ao informado
		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),
				numLicitacao));

		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(),
				indExclusao));

		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString(),
				indPropostaEscolhida));
		
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Obtém o número de itens que possuem o número da licitação e indicativo de exclusão iguais aos
	 * informados.
	 * 
	 * @param numLicitacao, indExclusao
	 */
	public Long pesquisarItemLicitacaoPorNumLicitacaoEIndExclusaoCount(
			Integer numLicitacao, Boolean indExclusao) {
		// Cria a criteria
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class);

		// Busca onde o num licitação é igual ao informado
		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),
				numLicitacao));

		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(),
				indExclusao));

		
		return this.executeCriteriaCount(criteria);
	}

	public List<ItensPACVO> pesquisarRelatorioItensLicitacao(Integer numero, DominioTipoFaseSolicitacao tipoFaseSolicitacao, boolean flagNaoExcluidas) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class);
		
		//Joins com tabela ScoLicitacao, ScoModalidadeLicitacao, ScoFaseSolicitação, ScoSolicitacaoDeCompra, ScoMaterial, ScoUnidadeMedida
		
		criteria.createAlias(ScoItemLicitacao.Fields.LICITACAO.toString(), "licitacao"); 
		criteria.createAlias("licitacao" + "." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "modalidadeLicitacao");	
		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "fasesSolicitacao"); 
		criteria.createAlias("fasesSolicitacao" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "solicitacaoCompra");	
		criteria.createAlias("solicitacaoCompra" + "." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "material");	
		criteria.createAlias("material" + "." + ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "unidadeMedida");	
		criteria.createAlias(ScoItemLicitacao.Fields.LOTE_LICITACAO.toString(), "LOTE",	JoinType.LEFT_OUTER_JOIN);
		
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NRO_LICITACAO.toString())
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.DESCRICAO.toString()),ItensPACVO.Fields.DESCRICAO_LICITACAO.toString())
		.add(Projections.groupProperty("modalidadeLicitacao"+"."+ScoModalidadeLicitacao.Fields.DESCRICAO.toString()),ItensPACVO.Fields.DESCRICAO_MODALIDADE.toString())
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.DT_DIGITACAO.toString()),ItensPACVO.Fields.DATA_DIGITACAO.toString())
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.DT_LIMITE_ACEITE_PROPOSTA.toString()),ItensPACVO.Fields.DATA_LIMITE_ACEITE_PROPOSTA.toString())
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.DT_ABERTURA_PROPOSTA.toString()),ItensPACVO.Fields.DATA_HR_ABERTURA_PROPOSTA.toString())
		.add(Projections.groupProperty(ScoItemLicitacao.Fields.NUMERO.toString()),ItensPACVO.Fields.NUMERO_ITEM.toString())
		.add(Projections.groupProperty("material"+"."+ScoMaterial.Fields.CODIGO.toString()),ItensPACVO.Fields.CODIGO_MATERIAL.toString())
		.add(Projections.groupProperty("unidadeMedida"+"."+ScoUnidadeMedida.Fields.CODIGO.toString()),ItensPACVO.Fields.CODIGO_UNIDADE_MEDIDA.toString())
		.add(Projections.groupProperty("unidadeMedida"+"."+ScoUnidadeMedida.Fields.DESCRICAO.toString()), ItensPACVO.Fields.DESCRICAO_UNIDADE_MEDIDA.toString())
		.add(Projections.groupProperty("material"+"."+ScoMaterial.Fields.NOME.toString()),ItensPACVO.Fields.NOME_MATERIAL.toString())
		.add(Projections.groupProperty("material"+"."+ScoMaterial.Fields.DESCRICAO.toString()),ItensPACVO.Fields.DESCRICAO_MATERIAL.toString())
		.add(Projections.groupProperty("material"+"."+ScoMaterial.Fields.CODIGO.toString()),ItensPACVO.Fields.CODIGO_MATERIAL.toString())
		.add(Projections.groupProperty("solicitacaoCompra"+"."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()),ItensPACVO.Fields.NRO_SOLICITACAO_COMPRA.toString())		
		.add(Projections.groupProperty("fasesSolicitacao" + "." + ScoFaseSolicitacao.Fields.TIPO.toString()),ItensPACVO.Fields.TIPO_FASE_SOLICITACAO.toString())
		.add(Projections.groupProperty(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()),ItensPACVO.Fields.EXCLUSAO.toString())
		.add(Projections.groupProperty("LOTE." + ScoLoteLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NUMERO_LOTE.toString())
		.add(Projections.groupProperty(ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()),ItensPACVO.Fields.VALOR_UNITARIO.toString())
		.add(Projections.sum("solicitacaoCompra"+"."+ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()),ItensPACVO.Fields.SOMA_QTDE_APROVADA.toString())
		.add(Projections.max("solicitacaoCompra"+"."+ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()),ItensPACVO.Fields.DESCRICAO_SOLICITACAO_COMPRA.toString());		

		criteria.setProjection(projection);
		
		
		//Restrictions
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numero));
		
		if (flagNaoExcluidas == true){
		    criteria.add(Restrictions.eq("licitacao" + "." + ScoLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		    criteria.add(Restrictions.eq("fasesSolicitacao" + "." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		}
		
		criteria.add(Restrictions.isNotNull("solicitacaoCompra" + "." + ScoSolicitacaoDeCompra .Fields.NUMERO.toString()));		
		criteria.add(Restrictions.eq("fasesSolicitacao" + "." + ScoFaseSolicitacao.Fields.TIPO.toString(), tipoFaseSolicitacao));		
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItensPACVO.class));
		
//		criteria.addOrder(Order.asc(ItensLicitacaoVO.Fields.NUMERO_ITEM.toString()));
		
		return executeCriteria(criteria);
	}

	public List<ItensPACVO> pesquisarRelatorioItensLicitacaoUnion(Integer numero, DominioTipoFaseSolicitacao tipoFaseSolicitacao,boolean flagNaoExcluidas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class);

		//Joins com tabela ScoLicitacao, ScoModalidadeLicitacao, ScoFaseSolicitação, ScoSolicitacaoDeServico, ScoServico 

		criteria.createAlias(ScoItemLicitacao.Fields.LICITACAO.toString(), "licitacao"); 
		criteria.createAlias("licitacao" + "." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "modalidadeLicitacao");	
		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "fasesSolicitacao"); 
		criteria.createAlias("fasesSolicitacao" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "solicitacaoServico");	
		criteria.createAlias("solicitacaoServico" + "." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "servico");	
		criteria.createAlias(ScoItemLicitacao.Fields.LOTE_LICITACAO.toString(), "LOTE",	JoinType.LEFT_OUTER_JOIN);
		

		ProjectionList projection = Projections.projectionList()
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NRO_LICITACAO.toString())
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.DESCRICAO.toString()),ItensPACVO.Fields.DESCRICAO_LICITACAO.toString())
		.add(Projections.groupProperty("modalidadeLicitacao"+"."+ScoModalidadeLicitacao.Fields.DESCRICAO.toString()),ItensPACVO.Fields.DESCRICAO_MODALIDADE.toString())
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.DT_DIGITACAO.toString()),ItensPACVO.Fields.DATA_DIGITACAO.toString())
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.DT_LIMITE_ACEITE_PROPOSTA.toString()),ItensPACVO.Fields.DATA_LIMITE_ACEITE_PROPOSTA.toString())
		.add(Projections.groupProperty("licitacao"+"."+ScoLicitacao.Fields.DT_ABERTURA_PROPOSTA.toString()),ItensPACVO.Fields.DATA_HR_ABERTURA_PROPOSTA.toString())
		.add(Projections.groupProperty(ScoItemLicitacao.Fields.NUMERO.toString()),ItensPACVO.Fields.NUMERO_ITEM.toString())
		.add(Projections.groupProperty("servico"+"."+ScoServico.Fields.CODIGO.toString()),ItensPACVO.Fields.CODIGO_MATERIAL.toString())				
		.add(Projections.groupProperty("servico"+"."+ScoMaterial.Fields.NOME.toString()),ItensPACVO.Fields.NOME_MATERIAL.toString())
		.add(Projections.groupProperty("servico"+"."+ScoMaterial.Fields.DESCRICAO.toString()),ItensPACVO.Fields.DESCRICAO_MATERIAL.toString())
		.add(Projections.groupProperty("servico"+"."+ScoMaterial.Fields.CODIGO.toString()),ItensPACVO.Fields.CODIGO_MATERIAL.toString())
		.add(Projections.groupProperty("solicitacaoServico"+"."+ScoSolicitacaoServico.Fields.NUMERO.toString()),ItensPACVO.Fields.NRO_SOLICITACAO_COMPRA.toString())		
        .add(Projections.groupProperty("fasesSolicitacao" + "." + ScoFaseSolicitacao.Fields.TIPO.toString()),ItensPACVO.Fields.TIPO_FASE_SOLICITACAO.toString())
        .add(Projections.groupProperty(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()),ItensPACVO.Fields.EXCLUSAO.toString())
        .add(Projections.groupProperty("LOTE." + ScoLoteLicitacao.Fields.NUMERO.toString()),ItensPACVO.Fields.NUMERO_LOTE.toString())
        .add(Projections.groupProperty(ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()),ItensPACVO.Fields.VALOR_UNITARIO.toString())
        .add(Projections.sqlProjection("SUM(solicitaca4_.QTDE_SOLICITADA) as somaQtdeAprovada" ,new String[]{"somaQtdeAprovada"} , new Type[]{LongType.INSTANCE}), ItensPACVO.Fields.SOMA_QTDE_APROVADA.toString())		
		.add(Projections.max("solicitacaoServico"+"."+ScoSolicitacaoServico.Fields.DESCRICAO.toString()),ItensPACVO.Fields.DESCRICAO_SOLICITACAO_COMPRA.toString());		

		
		criteria.setProjection(projection);		
	


		//Restrictions
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numero));
		
		if (flagNaoExcluidas == true){
		   criteria.add(Restrictions.eq("licitacao" + "." + ScoLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		   criteria.add(Restrictions.eq("fasesSolicitacao" + "." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		}

		criteria.add(Restrictions.isNotNull("solicitacaoServico" + "." + ScoSolicitacaoServico.Fields.NUMERO.toString()));		
		criteria.add(Restrictions.eq("fasesSolicitacao" + "." + ScoFaseSolicitacao.Fields.TIPO.toString(), tipoFaseSolicitacao));		

		criteria.setResultTransformer(Transformers.aliasToBean(ItensPACVO.class));
		
//		criteria.addOrder(Order.asc(ItensLicitacaoVO.Fields.NUMERO_ITEM.toString()));
		
		return executeCriteria(criteria);
	}
	
	public ScoItemLicitacao obterItemLicitacaoPorNumeroLicitacaoENumeroItem(
			Integer numeroLCT, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria
		.forClass(ScoItemLicitacao.class);

		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FASES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FASES." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FASES." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MATERIAL", JoinType.LEFT_OUTER_JOIN);
				
		criteria.setFetchMode(ScoItemLicitacao.Fields.LICITACAO.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),
				numeroLCT));
		
		criteria.add(Restrictions.eq(
				ScoItemLicitacao.Fields.NUMERO.toString(),
				numeroItem));
		
		return (ScoItemLicitacao) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<ScoItemLicitacao> obterItensPorLote(
			Integer numeroLCT, Short numeroItem) {
		DetachedCriteria criteria = DetachedCriteria
		.forClass(ScoItemLicitacao.class);

		criteria.createAlias(ScoItemLicitacao.Fields.LOTE_LICITACAO.toString(), "LOTE");
		criteria.add(Restrictions.eq(
				"LOTE."+ScoLoteLicitacao.Fields.NUMERO_LCT.toString(),
				numeroLCT));
		
		criteria.add(Restrictions.eq(
				"LOTE."+ScoLoteLicitacao.Fields.NUMERO.toString(),
				numeroItem));
		
		criteria.addOrder(Order.asc(ScoItemLicitacao.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<ItensPACVO> listarItensPorNroPacUnion1(Integer pac){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class,"ITL");
		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC" + "." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()), ItensPACVO.Fields.NRO_LICITACAO.toString())
				.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NUMERO_ITEM.toString())
				.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.NOME.toString()), ItensPACVO.Fields.NOME_MATERIAL.toString());
	
		criteria.setProjection(projection);		
		criteria.setResultTransformer(Transformers.aliasToBean(ItensPACVO.class));
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), pac));
		criteria.add(Restrictions.ne(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		return executeCriteria(criteria);
	}

	
	public List<ItensPACVO> listarItensPorNroPacUnion2(Integer pac){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class,"ITL");
		
		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS");
		criteria.createAlias("SLS" + "." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
		
		
		//Restrictions
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), pac));
		criteria.add(Restrictions.ne(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()), ItensPACVO.Fields.NRO_LICITACAO.toString())
				.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NUMERO_ITEM.toString())
				.add(Projections.groupProperty("SRV." + ScoServico.Fields.NOME.toString()), ItensPACVO.Fields.NOME_MATERIAL.toString());
		criteria.setProjection(projection);		
		criteria.setResultTransformer(Transformers.aliasToBean(ItensPACVO.class));
		return executeCriteria(criteria);
	}

	public List<ItensPACVO> listarItensMateriaisPorNroPacUnion1(Integer pac){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class,"ITL");
		
		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC" + "." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias(ScoItemLicitacao.Fields.LOTE_LICITACAO.toString(), "LOTE",	JoinType.LEFT_OUTER_JOIN);

		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.groupProperty("LOTE." + ScoLoteLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NUMERO_LOTE.toString())
				.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NUMERO_ITEM.toString())
				.add(Projections.groupProperty("FSC." + ScoFaseSolicitacao.Fields.TIPO.toString()), ItensPACVO.Fields.TIPO_FASE_SOLICITACAO.toString())
				.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItensPACVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.NOME.toString()), ItensPACVO.Fields.NOME_MATERIAL.toString())
				.add(Projections.groupProperty("SLC." + ScoSolicitacaoDeCompra.Fields.UMD_CODIGO.toString()), ItensPACVO.Fields.CODIGO_UNIDADE_MEDIDA.toString())
				.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()), ItensPACVO.Fields.VALOR_UNITARIO.toString())
				.add(Projections.sqlProjection("SUM(slc2_.QTDE_SOLICITADA) as somaQtdeAprovada" ,new String[]{"somaQtdeAprovada"} , new Type[]{LongType.INSTANCE}), ItensPACVO.Fields.SOMA_QTDE_APROVADA.toString());
	
		criteria.setProjection(projection);		
		criteria.setResultTransformer(Transformers.aliasToBean(ItensPACVO.class));
		//Restrictions
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), pac));
		criteria.add(Restrictions.ne(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		
		//criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	public List<ItensPACVO> listarItensMateriaisPorNroPacUnion2(Integer pac){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class,"ITL");
		
		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS");
		criteria.createAlias("SLS" + "." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
		criteria.createAlias(ScoItemLicitacao.Fields.LOTE_LICITACAO.toString(), "LOTE",	JoinType.LEFT_OUTER_JOIN);
		
		//Restrictions
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), pac));
		criteria.add(Restrictions.ne(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.groupProperty("LOTE." + ScoLoteLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NUMERO_LOTE.toString())
				.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), ItensPACVO.Fields.NUMERO_ITEM.toString())
				.add(Projections.groupProperty("FSC." + ScoFaseSolicitacao.Fields.TIPO.toString()), ItensPACVO.Fields.TIPO_FASE_SOLICITACAO.toString())
				.add(Projections.groupProperty("SLS." + ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString()), ItensPACVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.groupProperty("SRV." + ScoServico.Fields.NOME.toString()), ItensPACVO.Fields.NOME_MATERIAL.toString())
				//.adgroupProperty("SLS." + ScoSolicitacaoServico.Fields.UNIDADE_MEDIDA.toString()), ItensPACVO.Fields.CODIGO_UNIDADE_MEDIDA.toString())
				.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()), ItensPACVO.Fields.VALOR_UNITARIO.toString())
				.add(Projections.sqlProjection("SUM(sls2_.QTDE_SOLICITADA) as somaQtdeAprovada" ,new String[]{"somaQtdeAprovada"} , new Type[]{LongType.INSTANCE}), ItensPACVO.Fields.SOMA_QTDE_APROVADA.toString());
		
		
		criteria.setProjection(projection);		
		criteria.setResultTransformer(Transformers.aliasToBean(ItensPACVO.class));
		
		
		//criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<ItensPACVO> listarItensMateriaisPorNroPac(Integer pac) {
		List<ItensPACVO> retorno = new ArrayList<ItensPACVO>();
		retorno.addAll(listarItensMateriaisPorNroPacUnion1(pac));
		List<ItensPACVO> retornoUnion2 = listarItensMateriaisPorNroPacUnion2(pac);
		if(retornoUnion2!=null && !retornoUnion2.isEmpty()){
			for (ItensPACVO itensPACVO : retornoUnion2) {
				itensPACVO.setCodigoUnidadeMedida("UN");
			}
		}
		retorno.addAll(retornoUnion2);
		
		final BeanComparator pacCodigoComparator = new BeanComparator(
				ItensPACVO.Fields.NUMERO_ITEM.toString(), new NullComparator(false));
		Collections.sort(retorno, pacCodigoComparator);
		return retorno;
	}
	
	public List<ItensPACVO> listarItensPorNroPac(Integer pac) {
		List<ItensPACVO> retorno = new ArrayList<ItensPACVO>();
		retorno.addAll(listarItensPorNroPacUnion1(pac));
		List<ItensPACVO> retornoUnion2 = listarItensPorNroPacUnion2(pac);
		retorno.addAll(retornoUnion2);
		return retorno;
	}
	

	public Boolean verificarExisteItemPropForn(Integer itlLctNumero, Short itlNumero, 
			ScoSolicitacaoServico scoBuscaSolLicitServico, 
			 ScoSolicitacaoDeCompra scoBuscaSolLicitCompra){
		
		StringBuffer hql = new StringBuffer(850);
		hql.append(" select 1 ");
		hql.append(" 	   from ScoPareceresMateriais pm, ");
		hql.append(" 	  		ScoPareceresTecnicos pt, ");		
		hql.append(" 	  		ScoItemPropostaFornecedor ip, ");
		hql.append(" 	  		ScoSolicitacaoDeCompra slc, ");
		hql.append(" 	  		ScoSolicitacaoServico sls ");
		hql.append(" 		where ip.id.pfrLctNumero 									 = :itlLctNumero ");
		hql.append(" 			and ip.itemLicitacao.id.numero 							 = :itlNumero ");
		hql.append(" 			and ip.indExclusao 										 = 'N' ");
		if(scoBuscaSolLicitCompra != null ){
			hql.append(" 			and slc.numero = :scoBuscaSolLicitCompra ");	
		}
		if(scoBuscaSolLicitServico != null){
			hql.append(" 			and sls.numero = :scoBuscaSolLicitServico ");	
		}
		hql.append(" 			and pt.id.mcmCodigo in (ip.marcaComercial.codigo, ip.nomeComercial.id.mcmCodigo) ");	
		hql.append(" 			and ((pm.id.matCodigo = sls.servico.codigo ) or (pm.id.matCodigo = slc.material.codigo)) ");
		hql.append(" 			and pm.id.ptcMcmCodigo 									 = pt.id.mcmCodigo ");
		hql.append(" 			and pm.id.ptcNroSubPasta 								 = pt.id.nroSubPasta ");
		hql.append(" 			and pm.id.ptcOptCodigo 								 = pt.id.optCodigo ");
		hql.append(" 			and pm.indExcluido 										 = 'N' ");
		hql.append(" 			and pm.parecer 											 = 'F' ");
		hql.append("     	) ");	
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("itlLctNumero", itlLctNumero);	
		query.setParameter("itlNumero", itlNumero);
		if(scoBuscaSolLicitCompra != null ){query.setParameter("scoBuscaSolLicitCompra", scoBuscaSolLicitCompra.getNumero());}
		if(scoBuscaSolLicitServico != null){query.setParameter("scoBuscaSolLicitServico", scoBuscaSolLicitServico.getNumero());}
		Boolean existe = false;
		if(query.list() != null){
			existe = !query.list().isEmpty();
		}
		return existe;
	}

	public Long countItemLicitacaoPorNumeroLicitacao (Integer numeroLicitacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class, "ITL");

		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FS");
		
		criteria.add(Restrictions.eq(
				"ITL."+ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),
				numeroLicitacao));
		
		return this.executeCriteriaCount(criteria);
	}
	
	public List<ScoItemLicitacao> pesquisarItemLicitacaoPorNumeroLicitacao(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer numeroLicitacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class, "ITL");

		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "FS_COMPRA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "FS_SERVICO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("FS_COMPRA." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MATERIAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FS_SERVICO." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SERVICO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("ITL." +
				ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(),
				numeroLicitacao));

		criteria.addOrder(Order.asc("ITL."+ScoItemLicitacao.Fields.NUMERO.toString()));
		// tem que ser sem paginação para o sortBy do richfaces funcionar...
		//return this.executeCriteria(criteria, firstResult, maxResults, orderProperty,  asc);
		
		return this.executeCriteria(criteria);
	}

	/**
	 * Pesquisa os itens de solicitação com proposta lançada, escolhida, mas
	 * pendente de geração de af.
	 * 
	 * @param numeroLicitacao
	 * @return
	 */
	public List<PesquisaItensPendentesPacVO> pesquisarItemLicitacaoPendenteAFPorNumeroLicitacao(final Integer numeroLicitacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF");
		criteria.createAlias("IPF." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PRF");
		criteria.createAlias("PRF." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("IPF." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ITL");
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSL");
		criteria.createAlias("IPF." + ScoItemPropostaFornecedor.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CDP." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPG", JoinType.LEFT_OUTER_JOIN);
		

		criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		if (numeroLicitacao != null) {
			criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numeroLicitacao));
		}
		criteria.add(Restrictions.eq("IPF." + ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("IPF." + ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.TRUE));

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		subCriteria.setProjection(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), "IPF."
				+ ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString()));
		subCriteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString(), "IPF."
				+ ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString()));
		subCriteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO_PROPOSTA.toString(),
				"IPF." + ScoItemPropostaFornecedor.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Subqueries.notExists(subCriteria));

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()),
						PesquisaItensPendentesPacVO.Fields.ITL_NUMERO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.DT_EXCLUSAO.toString()), PesquisaItensPendentesPacVO.Fields.DT_EXCLUSAO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.MOTIVO_EXCLUSAO.toString()),
						PesquisaItensPendentesPacVO.Fields.MOTIVO_EXCLUSAO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()),
						PesquisaItensPendentesPacVO.Fields.VALOR_UNITARIO.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.TIPO.toString()), PesquisaItensPendentesPacVO.Fields.TIPO.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()),
						PesquisaItensPendentesPacVO.Fields.SOLICITACAO_DE_COMPRA.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()),
						PesquisaItensPendentesPacVO.Fields.SOLICITACAO_SERVICO.toString())
				.add(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()),
						PesquisaItensPendentesPacVO.Fields.VALOR_UNITARIO.toString())
				.add(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.DT_ESCOLHA.toString()),
						PesquisaItensPendentesPacVO.Fields.DT_JULGADA.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), PesquisaItensPendentesPacVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), PesquisaItensPendentesPacVO.Fields.CGC.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), PesquisaItensPendentesPacVO.Fields.CPF.toString())
				.add(Projections.property("FPG." + ScoFormaPagamento.Fields.DESCRICAO.toString()), PesquisaItensPendentesPacVO.Fields.FPG_DESCRICAO.toString()));
		// order
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaItensPendentesPacVO.class));
		return this.executeCriteria(criteria);
	}

	/**
	 * Pesquisa os itens de solicitação sem escolha de proposta (tem propostas
	 * lançadas, mas nenhuma escolhida, pois ainda não houve julgamento).
	 * 
	 * @param numeroLici542tacao
	 * @return
	 */
	public List<PesquisaItensPendentesPacVO> pesquisarItemLicitacaoSemEscolhaPorNumeroLicitacao(final Integer numeroLicitacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class, "ITL");

		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSL");

		criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		if (numeroLicitacao != null) {
			criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numeroLicitacao));
		}

		final DetachedCriteria subCriteria1 = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF");
		subCriteria1.setProjection(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString()));
		subCriteria1.add(Restrictions.eqProperty("IPF." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), "ITL."
				+ ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));
		subCriteria1.add(Restrictions.eqProperty("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(),
				"ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		subCriteria1.add(Restrictions.eq("IPF." + ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Subqueries.exists(subCriteria1));

		final DetachedCriteria subCriteria2 = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF");
		subCriteria2.setProjection(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString()));
		subCriteria2.add(Restrictions.eqProperty("IPF." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), "ITL."
				+ ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));
		subCriteria2.add(Restrictions.eqProperty("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(),
				"ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		subCriteria2.add(Restrictions.eq("IPF." + ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subCriteria2.add(Restrictions.eq("IPF." + ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString(), Boolean.TRUE));
		criteria.add(Subqueries.notExists(subCriteria2));

		// projection
		criteria.setProjection(this.getProjectionInicialPesquisaItensPendentesPacVO());

		// order
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaItensPendentesPacVO.class));
		return this.executeCriteria(criteria);
	}

	/**
	 * Pesquisa os itens de solicitação sem proposta.
	 * 
	 * @param numeroLicitacao
	 * @return
	 */
	public List<PesquisaItensPendentesPacVO> pesquisarItemLicitacaoSemPropostaPorNumeroLicitacao(final Integer numeroLicitacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class, "ITL");

		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSL");
		// criteria.createAlias("FSL." +
		// ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC",
		// DetachedJoinType.LEFT_OUTER_JOIN);
		// criteria.createAlias("FSL." +
		// ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS",
		// DetachedJoinType.LEFT_OUTER_JOIN);
		// criteria.createAlias("SLC." +
		// ScoSolicitacaoDeCompra.Fields.GRUPO_NATUREZA_DESPESA.toString(),
		// "GND", DetachedJoinType.LEFT_OUTER_JOIN);
		// criteria.createAlias("SLS." +
		// ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "NTD",
		// DetachedJoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoItemPropostaFornecedor.class, "IPF");
		subCriteria.setProjection(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString()));
		subCriteria.add(Restrictions.eqProperty("IPF." + ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), "ITL."
				+ ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));
		subCriteria.add(Restrictions.eqProperty("IPF." + ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString(),
				"ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eq("IPF." + ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Subqueries.propertyNotIn("ITL." + ScoItemLicitacao.Fields.NUMERO.toString(), subCriteria));

		if (numeroLicitacao != null) {
			criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numeroLicitacao));
		}
		// projection
		criteria.setProjection(this.getProjectionInicialPesquisaItensPendentesPacVO());

		// order
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaItensPendentesPacVO.class));
		return this.executeCriteria(criteria);
	}

	/**
	 * Pesquisa os itens de solicitação excluídos.
	 * 
	 * @param numeroLicitacao
	 * @return
	 */
	public List<PesquisaItensPendentesPacVO> pesquisarItemLicitacaoExcluidoPorNumeroLicitacao(final Integer numeroLicitacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class, "ITL");

		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSL");

		criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		if (numeroLicitacao != null) {
			criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numeroLicitacao));
		}
		// projection
		criteria.setProjection(this.getProjectionInicialPesquisaItensPendentesPacVO());
		
		// order
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaItensPendentesPacVO.class));
		return this.executeCriteria(criteria);
	}

	private Projection getProjectionInicialPesquisaItensPendentesPacVO() {
		return Projections
				.projectionList()
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()),
						PesquisaItensPendentesPacVO.Fields.ITL_NUMERO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.DT_EXCLUSAO.toString()), PesquisaItensPendentesPacVO.Fields.DT_EXCLUSAO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.MOTIVO_EXCLUSAO.toString()),
						PesquisaItensPendentesPacVO.Fields.MOTIVO_EXCLUSAO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()),
						PesquisaItensPendentesPacVO.Fields.VALOR_UNITARIO.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.TIPO.toString()), PesquisaItensPendentesPacVO.Fields.TIPO.toString())
				// .add(Projections.property("ITL." +
				// ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()),
				// PesquisaItensPendentesPacVO.Fields.IND_EXCLUSAO.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()),
						PesquisaItensPendentesPacVO.Fields.SOLICITACAO_DE_COMPRA.toString())
				.add(Projections.property("FSL." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()),
						PesquisaItensPendentesPacVO.Fields.SOLICITACAO_SERVICO.toString());
	}

		
	public Boolean verificarParecerMaterialDesfavoravelPorNumeroEItemLicitacao(ScoItemPropostaVO itemPropostaVO) {
		
		final String parecerDesfavoravel = "D";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPareceresMateriais.class, "PAM");
		criteria.createAlias(ScoPareceresMateriais.Fields.PARECER_TECNICO.toString(), "PTC"); 
		
		DetachedCriteria subQueryFases = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		subQueryFases.createAlias(ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
		subQueryFases.createAlias(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);					
		subQueryFases.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), itemPropostaVO.getNumeroPac()));
		subQueryFases.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), Short.valueOf(itemPropostaVO.getNumeroItemPac())));
		subQueryFases.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));	
		ProjectionList projectionListSubQueryFases = Projections.projectionList()
				.add(Projections.property("SLC."+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
		subQueryFases.setProjection(projectionListSubQueryFases);
		
		criteria.add(Subqueries.propertyIn("PAM."+ScoPareceresMateriais.Fields.MAT_CODIGO.toString(), subQueryFases));

		if (itemPropostaVO.getMarcaComercial() != null && itemPropostaVO.getNomeComercial() != null) {
			criteria.add(Restrictions.or(
					Restrictions.eq(
							"PTC."+ScoPareceresTecnicos.Fields.MCM_CODIGO.toString(), itemPropostaVO.getMarcaComercial().getCodigo()), 
					Restrictions.eq(
							"PTC."+ScoPareceresTecnicos.Fields.MCM_CODIGO.toString(), itemPropostaVO.getNomeComercial().getId().getMcmCodigo())));	
		} else if (itemPropostaVO.getMarcaComercial() != null && itemPropostaVO.getNomeComercial() == null) {
			criteria.add(Restrictions.eq(
					"PTC."+ScoPareceresTecnicos.Fields.MCM_CODIGO.toString(), itemPropostaVO.getMarcaComercial().getCodigo()));
		} else if (itemPropostaVO.getMarcaComercial() == null && itemPropostaVO.getNomeComercial() != null) {
			criteria.add(Restrictions.eq(
					"PTC."+ScoPareceresTecnicos.Fields.MCM_CODIGO.toString(), itemPropostaVO.getNomeComercial().getId().getMcmCodigo()));
		}
	
		criteria.add(Restrictions.eq("PAM."+ScoPareceresMateriais.Fields.PARECER.toString(), parecerDesfavoravel));
		
		return executeCriteriaCount(criteria) > 0;
		
	}
	
	private void acrescentarHqlParametrosEOrdem(StringBuffer hql, Short numeroInicial, Short numeroFinal, String listaItens) {
		if (numeroInicial != null && numeroFinal != null) {
			hql.append(" AND IT.").append(ScoItemLicitacao.Fields.NUMERO.toString()).append(" BETWEEN :numInicial AND :numFinal ");
		}
		
		if (StringUtils.isNotBlank(listaItens)) {
			hql.append(" AND IT.").append(ScoItemLicitacao.Fields.NUMERO.toString()).append(" IN ("+listaItens+") ");
		}
		
		hql.append(" ORDER BY IT.").append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()).append(", IT.").append(ScoItemLicitacao.Fields.NUMERO.toString());
	}
	
	public List<Object[]> pesquisarItemQuadroPropostas(Integer numPac, Short numeroInicial, 
			Short numeroFinal, String listaItens){
		
		// Itens da Solicitação de Compra
		StringBuffer hqlSC = new StringBuffer(1300);

		hqlSC.append(" SELECT IT.").append(ScoItemLicitacao.Fields.NUMERO.toString());
		hqlSC.append(", FS.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString());		
		hqlSC.append(", SC.").append(ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString());		
		hqlSC.append(", SC.").append(ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString());		
		hqlSC.append(", SC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString());
		hqlSC.append(", MAT.").append(ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString());
		hqlSC.append(", MAT.").append(ScoMaterial.Fields.NOME.toString());
		hqlSC.append(", MAT.").append(ScoMaterial.Fields.DESCRICAO.toString());
		hqlSC.append(", CASE WHEN MAT.").append(ScoMaterial.Fields.IND_MENOR_PRECO.toString()).append(" = :indMenorPreco ");
		hqlSC.append(" THEN '*** Menor Preço ***' ELSE '' END");
		hqlSC.append(", 'SC'");
		
		hqlSC.append(" FROM ").append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SC, ");
		hqlSC.append(ScoMaterial.class.getSimpleName()).append(" MAT, ");
		hqlSC.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FS, ");
		hqlSC.append(ScoItemLicitacao.class.getSimpleName()).append(" IT, ");
		hqlSC.append(ScoLicitacao.class.getSimpleName()).append(" LI ");
		
		hqlSC.append("WHERE FS.").append(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()).append(" = IT.")
		.append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()).append(' ');	
		hqlSC.append(" AND   FS.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()).append(" = IT.")
		.append(ScoItemLicitacao.Fields.NUMERO.toString()).append(' ');		
		hqlSC.append(" AND FS.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");	
		hqlSC.append(" AND   SC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = FS.")
		.append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(' ');				
		hqlSC.append(" AND   MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = SC.")
		.append(ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString()).append(' ');		
		hqlSC.append(" AND   IT.").append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()).append(" = LI.")
		.append(ScoLicitacao.Fields.NUMERO.toString()).append(' ');		
		hqlSC.append(" AND IT.").append(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");		
		hqlSC.append(" AND IT.").append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()).append(" = :numPac ");		
				
		acrescentarHqlParametrosEOrdem(hqlSC, numeroInicial, numeroFinal, listaItens);
		
		Query querySC = createHibernateQuery(hqlSC.toString());
		querySC.setParameter("numPac", numPac);
		querySC.setParameter("indMenorPreco", DominioSimNao.S);
		querySC.setParameter("indExclusao", Boolean.FALSE);
		
		if (numeroInicial != null && numeroFinal != null) {
			querySC.setParameter("numInicial", numeroInicial);	
			querySC.setParameter("numFinal", numeroFinal);
		}
				
		List<Object[]> itemSC = querySC.list();

		// Itens da Solicitação de Serviço
		StringBuffer hqlSS = new StringBuffer(1300);
		
		hqlSS.append(" SELECT IT.").append(ScoItemLicitacao.Fields.NUMERO.toString());
		hqlSS.append(", FS.").append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString());		
		hqlSS.append(", SS.").append(ScoSolicitacaoServico.Fields.DESCRICAO.toString());		
		hqlSS.append(", SS.").append(ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString());		
		hqlSS.append(", SS.").append(ScoSolicitacaoServico.Fields.QUANTIDADE_SOLICITADA.toString());
		hqlSS.append(", ''");
		hqlSS.append(", SERV.").append(ScoServico.Fields.NOME.toString());
		hqlSS.append(", SERV.").append(ScoServico.Fields.DESCRICAO.toString());
		hqlSS.append(", ''");
		hqlSS.append(", 'SS'");

		hqlSS.append(" FROM ").append(ScoSolicitacaoServico.class.getSimpleName()).append(" SS, ");
		hqlSS.append(ScoServico.class.getSimpleName()).append(" SERV, ");
		hqlSS.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FS, ");
		hqlSS.append(ScoItemLicitacao.class.getSimpleName()).append(" IT, ");
		hqlSS.append(ScoLicitacao.class.getSimpleName()).append(" LI ");

		hqlSS.append("WHERE FS.").append(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()).append(" = IT.")
		.append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()).append(' ');	
		hqlSS.append(" AND   FS.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()).append(" = IT.")
		.append(ScoItemLicitacao.Fields.NUMERO.toString()).append(' ');		
		hqlSS.append(" AND FS.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");	
		hqlSS.append(" AND   SS.").append(ScoSolicitacaoServico.Fields.NUMERO.toString()).append(" = FS.")
		.append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()).append(' ');	
		hqlSS.append(" AND   SERV.").append(ScoServico.Fields.CODIGO.toString()).append(" = SS.")
		.append(ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString()).append(' ');		
		hqlSS.append(" AND   IT.").append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()).append(" = LI.")
		.append(ScoLicitacao.Fields.NUMERO.toString()).append(' ');		
		hqlSS.append(" AND IT.").append(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");		
		hqlSS.append(" AND IT.").append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()).append(" = :numPac ");

		acrescentarHqlParametrosEOrdem(hqlSS, numeroInicial, numeroFinal, listaItens);
		
		Query querySS = createHibernateQuery(hqlSS.toString());
		querySS.setParameter("numPac", numPac);
		querySS.setParameter("indExclusao", Boolean.FALSE);
		if (numeroInicial != null && numeroFinal != null) {
			querySS.setParameter("numInicial", numeroInicial);	
			querySS.setParameter("numFinal", numeroFinal);
		}

		List<Object[]> itemSS = querySS.list();
		List<Object[]> itensSCeSS = itemSC;
		itensSCeSS.addAll(itemSS);
		
		return itensSCeSS;
	}

	public Long contarItensLicitacao(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class);		
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numero));		
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), false));		
		return executeCriteriaCount(criteria);
	}

	public Long contarItensLicitacaoJulgados(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class);
		
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), numero));
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), false));		
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString(), true));		
		return executeCriteriaCount(criteria);
	}
	
	public List<ScoItemLicitacao>  verificarDependenciasDoItemUnion1(Integer nroPac, Integer materialCod){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class,"ITL");
		
		criteria.createAlias(ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC" + "." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MT");
		criteria.createAlias("MT" + "." + ScoMaterial.Fields.MATERIAL_VINCULO.toString(), "MVC");
		criteria.createAlias("MVC" + "." + ScoMaterialVinculo.Fields.MATERIAL.toString(), "MAT");
		
		criteria.add(Restrictions.eq(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), nroPac));
		criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.CODIGO.toString(), materialCod));
		criteria.add(Restrictions.ne(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		
		return executeCriteria(criteria);
	}

	public List<ScoItemLicitacao>  verificarDependenciasDoItemUnion2(Integer nroPac, Integer materialCod){
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItemLicitacao.class,"ITL");
		criteria.createAlias("ITL."+ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC" + "." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoMaterialVinculo.class,"MVC");
		subCriteria.createAlias("MVC" + "." + ScoMaterialVinculo.Fields.MATERIAL.toString(), "MAT");
		subCriteria.setProjection(Projections.property("MAT." + ScoMaterialVinculo.Fields.CODIGO.toString()));
		subCriteria.add(Property.forName("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString())
				.eqProperty("MAT." + ScoMaterialVinculo.Fields.CODIGO.toString()));
		subCriteria.add(Restrictions.eq("MVC."+ScoMaterialVinculo.Fields.MATERIAL_VINCULO.toString(), materialCod));
		criteria.add(Subqueries.exists(subCriteria));
		criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), nroPac));
		criteria.add(Restrictions.ne("ITL."+ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		return executeCriteria(criteria);
	}
	
	public List<ScoItemLicitacao> listarItensLicitacao(Integer numeroLicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class, "ITL");
		criteria.createAlias("ITL."+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT");
		criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.NUMERO.toString(), numeroLicitacao));
		criteria.addOrder(Order.asc(ScoItemLicitacao.Fields.NUMERO.toString()));
		return executeCriteria(criteria);
	}

	// C1 - #22068
	@SuppressWarnings("unchecked")
	public List<Object[]> obterModaldiadePacSocilitacao(Integer numeroLicitacao) {
	
		StringBuilder hql = new StringBuilder(200);		
		hql.append("SELECT ")
		.append("MDL." ).append( ScoModalidadeLicitacao.Fields.DESCRICAO.toString())
		.append(", FSL." ).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		.append(", FSL." ).append( ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
		.append(", MDL." ).append( ScoModalidadeLicitacao.Fields.CODIGO.toString())		
		.append(" FROM ")
		.append(ScoLicitacao.class.getSimpleName() ).append( " PAC, ")
		.append(ScoItemLicitacao.class.getSimpleName() ).append( " ITL, ")
		.append(ScoFaseSolicitacao.class.getSimpleName() ).append( " FSL, ")
		.append(ScoModalidadeLicitacao.class.getSimpleName() ).append( " MDL ")		
		.append(" WHERE ")
		.append("PAC." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( " = :numeroLicitacao")
		.append(" AND PAC." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( " = ITL." ).append( ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString())
		.append(" AND PAC." ).append( ScoLicitacao.Fields.MLC_CODIGO.toString() ).append( " = MDL." ).append( ScoModalidadeLicitacao.Fields.CODIGO.toString())
		.append(" AND ITL." ).append( ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString() ).append( " = FSL." ).append( ScoFaseSolicitacao.Fields.LCT_NUMERO.toString())
		.append(" AND ITL." ).append( ScoItemLicitacao.Fields.NUMERO.toString() ).append( " = FSL." ).append( ScoFaseSolicitacao.Fields.ITL_NUMERO.toString());		
		Query query = createHibernateQuery(hql.toString());
		query.setInteger("numeroLicitacao", numeroLicitacao);

		return query.list();
	}
	
	public List<ScoItemLicitacao> listarModalidadeItensLicitacaoPorNumeroLicitacao(Integer numeroLicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class, "ITL");
		criteria.createAlias("ITL."+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT");
		criteria.createAlias("LCT."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MOD");
		
		criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.NUMERO.toString(), numeroLicitacao));
		
		return executeCriteria(criteria);
	}
}