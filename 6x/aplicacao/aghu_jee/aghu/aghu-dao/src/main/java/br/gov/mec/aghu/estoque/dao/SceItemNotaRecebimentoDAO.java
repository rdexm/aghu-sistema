package br.gov.mec.aghu.estoque.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.vo.DadosEntradaMateriasDiaVO;
import br.gov.mec.aghu.estoque.vo.DestinoMaterialRecebidoVO;
import br.gov.mec.aghu.estoque.vo.NotaRecebimentoItensVO;
import br.gov.mec.aghu.estoque.vo.PendenciasDevolucaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemNotaRecebimentoId;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @modulo estoque
 *
 */

@SuppressWarnings("PMD.CyclomaticComplexity")
public class SceItemNotaRecebimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemNotaRecebimento> {
	
	private static final long serialVersionUID = -5320628529445349822L;


	@Override
	protected void obterValorSequencialId(SceItemNotaRecebimento elemento) {

		if (elemento.getNotaRecebimento() == null) {
			throw new IllegalArgumentException("SceNotaRecebimento não está associado corretamente.");
		} 

		if (elemento.getItemAutorizacaoForn() == null) {
			throw new IllegalArgumentException("ScoItemAutorizacaoForn não está associado corretamente.");
		}

		SceItemNotaRecebimentoId id = new SceItemNotaRecebimentoId();
		
		id.setNrsSeq(elemento.getNotaRecebimento().getSeq());
		id.setAfnNumero(elemento.getItemAutorizacaoForn().getId().getAfnNumero());
		id.setItemNumero(elemento.getItemAutorizacaoForn().getId().getNumero());

		elemento.setId(id);
	}
	
	public List<SceItemNotaRecebimento> pesquisarItensNotaRecebimentoPorNotaRecebimento(Integer seqNotaRecebimento) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class);
		
		criteria.add(Restrictions.eq(SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), seqNotaRecebimento));
		criteria.createAlias(SceItemNotaRecebimento.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceItemNotaRecebimento.Fields.UNIDADE_MEDIDA.toString(), "UM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceItemNotaRecebimento.Fields.SERVICO.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "IAF_MC", JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria);
	}

	public List<SceItemNotaRecebimento> pesquisarItensNotaRecebimentoPorNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		return pesquisarItensNotaRecebimentoPorNotaRecebimento(notaRecebimento.getSeq());
	}

	public Double pesquisarValorEmNrPorItemAutorizacaoForn(
			ScoItemAutorizacaoForn scoItensAutorizacaoForn) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class,"inr");
		criteria.setProjection(Projections.sum("inr."+SceItemNotaRecebimento.Fields.VALOR.toString()));
		criteria.createAlias("inr."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), "nrs");
		
		criteria.add(Restrictions.eq("nrs."+SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), scoItensAutorizacaoForn.getAutorizacoesForn()));
		criteria.add(Restrictions.eq("nrs."+SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("inr."+SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), scoItensAutorizacaoForn));
		
		return (Double) executeCriteriaUniqueResult(criteria);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<NotaRecebimentoItensVO> buscaItensNotaRecebimentoImpressaoUnion(Integer numNotaRec, Integer fornHU)throws BaseException{

		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "inr");
		criteria.createAlias("inr."+SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "iaf", Criteria.INNER_JOIN);
		criteria.createAlias("inr."+SceItemNotaRecebimento.Fields.MATERIAL.toString(), "mat", Criteria.INNER_JOIN);

		criteria.createAlias("iaf."+ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "ipf", Criteria.INNER_JOIN);
		criteria.createAlias("ipf."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "itl", Criteria.INNER_JOIN);
		criteria.createAlias("iaf."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "fsc", Criteria.INNER_JOIN);
		criteria.createAlias("iaf."+ScoItemAutorizacaoForn.Fields.NOME_COMERCIAL.toString(), "nc", Criteria.LEFT_JOIN);
		criteria.createAlias("iaf."+ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "mcm1", Criteria.LEFT_JOIN);
		criteria.createAlias("fsc."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "slc", Criteria.INNER_JOIN);
		criteria.createAlias("nc."+ScoNomeComercial.Fields.MARCA_COMERCIAL.toString(), "mcm", Criteria.LEFT_JOIN);
		criteria.createAlias("mat."+ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "eal", Criteria.LEFT_JOIN);
		
		/*Projections*/
		ProjectionList projection = Projections.projectionList();

		projection.add(Projections.distinct(Projections.property("inr." + SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString())), "nota_recebimento");
		projection.add(Projections.property("inr." + SceItemNotaRecebimento.Fields.ITEM_NUMERO.toString()), "item_numero");
		projection.add(Projections.property("iaf." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()), "afn_numero");
		projection.add(Projections.property("itl." + ScoItemLicitacao.Fields.NUMERO.toString()), "numero");
		projection.add(Projections.property("inr." + SceItemNotaRecebimento.Fields.QUANTIDADE.toString()), "qtde_recebida");
		projection.add(Projections.property("iaf." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), "qtde_solicitada");
		projection.add(Projections.property("inr." + SceItemNotaRecebimento.Fields.VALOR.toString()), "valorItem");
		projection.add(Projections.property("eal." + SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString()), "umd_codigo");
		projection.add(Projections.sqlProjection("coalesce(MCM1x7_.DESCRICAO, MCM9_.DESCRICAO) as marca", new String[]{"marca"}, new Type[] { StringType.INSTANCE }),"marca");
		projection.add(Projections.property("nc." + ScoNomeComercial.Fields.NOME.toString()), "nome_comercial");
		projection.add(Projections.property("mat." + ScoMaterial.Fields.CODIGO.toString()), "codigo_mat");
		projection.add(Projections.property("mat." + ScoMaterial.Fields.NOME.toString()), "nome_mat");
		projection.add(Projections.property("mat." + ScoMaterial.Fields.DESCRICAO.toString()), "descricao_mat");
		projection.add(Projections.property("slc." + ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString()), "cct");

		projection.add(Projections.sqlProjection("	case	when mat2_.IND_ESTOCAVEL = 'S' then" +
												"				'E'" +
												"			else" +
												"				'D'" +
												"	end as ind_estoc ", new String[]{"ind_estoc"}, new Type[] { StringType.INSTANCE }),"ind_estoc");

		projection.add(Projections.property("slc." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), "solicitacao_compra");
		projection.add(Projections.property("eal." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()), "almoxarifado");
		projection.add(Projections.property("eal." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()), "endereco");

		criteria.setProjection(projection);

		/*Restrictions*/
		criteria.add(Restrictions.eq(SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), numNotaRec));
		criteria.add(Restrictions.eq("eal." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), fornHU));
		criteria.add(Restrictions.eqProperty("eal."+SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "mat."+ScoMaterial.Fields.ALMOXARIFADO.toString()));

		List<NotaRecebimentoItensVO> lstitens = new ArrayList<NotaRecebimentoItensVO>();
		List<Object[]> lstObj = executeCriteria(criteria);

		List<Integer> itensJaInclusos = new ArrayList<Integer>();

		for (Object[] object : lstObj) {
			NotaRecebimentoItensVO itemNota = new NotaRecebimentoItensVO();

			int index=0;

			if(object[index++]!=null){
				itemNota.setNumeroNota(Integer.parseInt(object[0].toString()));
			}
			if(object[index++]!=null){
				itensJaInclusos.add(Integer.parseInt(object[1].toString()));
				itemNota.setItemNumero(Integer.parseInt(object[1].toString()));
			}
			if(object[index++]!=null){
				itemNota.setAfnNumero(Integer.parseInt(object[2].toString()));
			}
			if(object[index++]!=null){
				itemNota.setItemLicitacaoNumero(Integer.parseInt(object[3].toString()));
			}
			if(object[index++]!=null){
				itemNota.setQtdeRecebida(Integer.parseInt(object[4].toString()));
			}
			if(object[index++]!=null){
				itemNota.setQtdeSolicitada(Integer.parseInt(object[5].toString()));
			}
			if(object[index++]!=null){
				itemNota.setValorItem(Double.parseDouble(object[6].toString()));
			}
			if(object[index++]!=null){
				itemNota.setCodUniMedida(object[7].toString());
			}
			if(object[index++]!=null){
				itemNota.setMarca(object[8].toString());
			}
			if(object[index++]!=null){
				itemNota.setNomeComercial(object[9].toString());
			}
			if(object[index++]!=null){
				itemNota.setCodMaterial(Integer.parseInt(object[10].toString()));
			}
			if(object[index++]!=null){
				itemNota.setNomeMaterial(object[11].toString());
			}
			if(object[index++]!=null){
				itemNota.setDescricaoMaterial(object[12].toString());
			}
			if(object[index++]!=null){
				itemNota.setCcAplicaCodigo(Integer.parseInt(object[13].toString()));
			}
			if(object[index++]!=null){
				itemNota.setIndEstocavel(object[14].toString());
			}
			if(object[index++]!=null){
				itemNota.setSolicitacaoCompra(Integer.parseInt(object[15].toString()));
			}
			if(object[index++]!=null){
				itemNota.setAlmoSeq(Short.parseShort(object[16].toString()));
			}
			if(object[index++]!=null){
				itemNota.setEnderecoAlm(object[17].toString());
			}
			
			lstitens.add(itemNota);
		}
		
		/*Union
		 * Query de Serviços
		 * */

		DetachedCriteria criteriaUni = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "inr");
		criteriaUni.createAlias("inr."+SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "iaf", Criteria.INNER_JOIN);
		criteriaUni.createAlias("inr."+SceItemNotaRecebimento.Fields.SERVICO.toString(), "srv", Criteria.INNER_JOIN);

		criteriaUni.createAlias("iaf."+ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "ipf", Criteria.INNER_JOIN);
		criteriaUni.createAlias("ipf."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "itl", Criteria.INNER_JOIN);
		criteriaUni.createAlias("iaf."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "fsc", Criteria.INNER_JOIN);
		criteriaUni.createAlias("iaf."+ScoItemAutorizacaoForn.Fields.NOME_COMERCIAL.toString(), "nc", Criteria.LEFT_JOIN);
		criteriaUni.createAlias("iaf."+ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "mcm1", Criteria.LEFT_JOIN);
		criteriaUni.createAlias("fsc."+ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "sls", Criteria.INNER_JOIN);
		criteriaUni.createAlias("nc."+ScoNomeComercial.Fields.MARCA_COMERCIAL.toString(), "mcm", Criteria.LEFT_JOIN);

		/*Projections*/
		ProjectionList projectionUni = Projections.projectionList();

		projectionUni.add(Projections.distinct(Projections.property("inr." + SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString())), "nota_recebimento");
		projectionUni.add(Projections.property("inr." + SceItemNotaRecebimento.Fields.ITEM_NUMERO.toString()), "item_numero");
		projectionUni.add(Projections.property("iaf." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()), "afn_numero");
		projectionUni.add(Projections.property("itl." + ScoItemLicitacao.Fields.NUMERO.toString()), "numero");
		projectionUni.add(Projections.property("inr." + SceItemNotaRecebimento.Fields.QUANTIDADE.toString()), "qtde_recebida");
		projectionUni.add(Projections.property("iaf." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), "qtde_solicitada");
		projectionUni.add(Projections.property("inr." + SceItemNotaRecebimento.Fields.VALOR.toString()), "valorItem");
		projectionUni.add(Projections.sqlProjection("'' as umd_codigo", new String[]{"umd_codigo"}, new Type[] { StringType.INSTANCE }),"umd_codigo");
		projectionUni.add(Projections.sqlProjection("coalesce(MCM1x7_.DESCRICAO, MCM9_.DESCRICAO) as marca", new String[]{"marca"}, new Type[] { StringType.INSTANCE }),"marca");
		projectionUni.add(Projections.property("nc." + ScoNomeComercial.Fields.NOME.toString()), "nome_comercial");

		projectionUni.add(Projections.property("srv." + ScoServico.Fields.CODIGO.toString()), "codigo_serv");
		projectionUni.add(Projections.property("srv." + ScoServico.Fields.NOME.toString()), "nome_serv");
		projectionUni.add(Projections.property("srv." + ScoServico.Fields.DESCRICAO.toString()), "descricao_serv");

		projectionUni.add(Projections.property("sls." + ScoSolicitacaoServico.Fields.CC_APLICADA_CODIGO.toString()), "cct");

		projectionUni.add(Projections.sqlProjection("''  as ind_estoc ", new String[]{"ind_estoc"}, new Type[] { StringType.INSTANCE }),"ind_estoc");

		projectionUni.add(Projections.property("sls." + ScoSolicitacaoServico.Fields.NUMERO.toString()), "solicitacao_servico");
		projectionUni.add(Projections.sqlProjection("0 as almoxarifado", new String[]{"almoxarifado"}, new Type[] { IntegerType.INSTANCE }),"almoxarifado");
		projectionUni.add(Projections.sqlProjection("'' as endereco", new String[]{"endereco"}, new Type[] { StringType.INSTANCE}),"endereco");

		criteriaUni.setProjection(projectionUni);

		/*Restrictions*/
		criteriaUni.add(Restrictions.eq(SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), numNotaRec));

		/*Limpa a lista anterior*/
		lstObj.clear();
		
		lstObj = executeCriteria(criteriaUni);

		for (Object[] object : lstObj) {
			NotaRecebimentoItensVO itemNota = new NotaRecebimentoItensVO();
			
			if(!itensJaInclusos.contains(Integer.parseInt(object[1].toString()))){

				int index=0;

				if(object[index++]!=null){
					itemNota.setNumeroNota(Integer.parseInt(object[0].toString()));
				}
				if(object[index++]!=null){
					itensJaInclusos.add(Integer.parseInt(object[1].toString()));
					itemNota.setItemNumero(Integer.parseInt(object[1].toString()));
				}
				if(object[index++]!=null){
					itemNota.setAfnNumero(Integer.parseInt(object[2].toString()));
				}
				if(object[index++]!=null){
					itemNota.setItemLicitacaoNumero(Integer.parseInt(object[3].toString()));
				}
				if(object[index++]!=null){
					itemNota.setQtdeRecebida(Integer.parseInt(object[4].toString()));
				}
				if(object[index++]!=null){
					itemNota.setQtdeSolicitada(Integer.parseInt(object[5].toString()));
				}
				if(object[index++]!=null){
					itemNota.setValorItem(Double.parseDouble(object[6].toString()));
				}
				if(object[index++]!=null){
					itemNota.setCodUniMedida(object[7].toString());
				}
				if(object[index++]!=null){
					itemNota.setMarca(object[8].toString());
				}
				if(object[index++]!=null){
					itemNota.setNomeComercial(object[9].toString());
				}
				if(object[index++]!=null){
					itemNota.setCodMaterial(Integer.parseInt(object[10].toString()));
				}
				if(object[index++]!=null){
					itemNota.setNomeMaterial(object[11].toString());
				}
				if(object[index++]!=null){
					itemNota.setDescricaoMaterial(object[12].toString());
				}
				if(object[index++]!=null){
					itemNota.setCcAplicaCodigo(Integer.parseInt(object[13].toString()));
				}
				if(object[index++]!=null){
					itemNota.setIndEstocavel(object[14].toString());
				}
				if(object[index++]!=null){
					itemNota.setSolicitacaoCompra(Integer.parseInt(object[15].toString()));
				}
				if(object[index++]!=null){
					itemNota.setAlmoSeq(Short.parseShort(object[16].toString()));
				}
				if(object[index++]!=null){
					itemNota.setEnderecoAlm(object[17].toString());
				}
				lstitens.add(itemNota);
			}
		}

		return lstitens;
	}

	public List<SceItemNotaRecebimento> pesquisaItensNotaRecebimentoUltimos60DiasPormaterial(Integer codMaterial, Object param){

		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "inr");
		criteria.createCriteria("inr."+SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "iaf", Criteria.INNER_JOIN);
		criteria.createCriteria("iaf."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "afn", Criteria.INNER_JOIN);
		criteria.createCriteria("iaf."+ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "mcm", Criteria.LEFT_JOIN);
		criteria.createCriteria("afn."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "pfn", Criteria.INNER_JOIN);
		criteria.createCriteria("pfn."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "frn", Criteria.INNER_JOIN);
		criteria.createCriteria("inr."+SceItemNotaRecebimento.Fields.MATERIAL, "mat", Criteria.INNER_JOIN);
		

		if(param != null && !"".equals(param)) {
			if(CoreUtil.isNumeroInteger(param)) {
				criteria.add(Restrictions.eq("inr."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ, Integer.parseInt(param.toString())));
			} else {
				criteria.add(Restrictions.ilike("mcm."+ScoMarcaComercial.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq("mat."+ScoMaterial.Fields.CODIGO.toString(), codMaterial));

		/*Sub query do in*/
		DetachedCriteria criteriaSub = DetachedCriteria.forClass(SceNotaRecebimento.class);
		criteriaSub.add(Restrictions.eq(SceNotaRecebimento.Fields.IND_ESTORNO.toString(), false));

		String subQuery = " {alias}.dt_geracao  > ?";
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, -60);//60 dias antes

		Object[] values = {c.getTime()};
		Type[] types = {TimestampType.INSTANCE};

		criteriaSub.add(Restrictions.sqlRestriction(subQuery, values, types));
		criteriaSub.setProjection(Projections.distinct(Property.forName(SceNotaRecebimento.Fields.NUMERO_NR.toString())));

		criteria.add(Subqueries.propertyIn("inr."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), criteriaSub));
		
		criteria.addOrder(Order.asc("inr."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString()));
		criteria.addOrder(Order.asc("mcm."+ScoMarcaComercial.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
		
	}
	
	public Long quantidadeEfetivadaItensAF(Integer numeroAF, Integer numeroItem){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class);
		
		criteria.setProjection(Projections.sum(SceItemNotaRecebimento.Fields.QUANTIDADE.toString()));		
		criteria.add(Restrictions.eq(SceItemNotaRecebimento.Fields.IAF_AFN_NUMERO.toString(), numeroAF));
		criteria.add(Restrictions.eq(SceItemNotaRecebimento.Fields.IAF_NUMERO.toString(), numeroItem));
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	public List<PendenciasDevolucaoVO> pesquisarGeracaoPendenciasDevolucao(Integer numeroNr){
		 
		 DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "INR");
		 criteria.createCriteria("INR."+ SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), "NR", Criteria.INNER_JOIN);
		 criteria.createCriteria("INR."+ SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF", Criteria.INNER_JOIN);		
		 criteria.createCriteria("IAF."+ ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPR", Criteria.INNER_JOIN);
		 criteria.createCriteria("IPR."+ ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ITL", Criteria.INNER_JOIN);
		 criteria.createCriteria("INR."+ SceItemNotaRecebimento.Fields.MATERIAL.toString(), "MAT", Criteria.INNER_JOIN);
		 criteria.createCriteria("MAT."+ ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UMD", Criteria.INNER_JOIN);
		 /*criteria.createCriteria("NR." + SceNotaRecebimento.Fields.BOLETIM_OCORRENCIAS.toString(),"BO", Criteria.LEFT_JOIN);		 		 
		 criteria.createCriteria("BO." + SceBoletimOcorrencias.Fields.ITENS_BOLETIM_OCORRENCIA.toString(),"IBO", Hibernate.)
		 .add(Restrictions.eqProperty("IBO." + SceItemBoc.Fields.MAT_CODIGO.toString(), "MAT." + ScoMaterial.Fields.CODIGO.toString()));
		 */
		 criteria.add(Restrictions.eq("NR." + SceNotaRecebimento.Fields.IND_ESTORNO.toString(), false));
		 criteria.add(Restrictions.eq("NR." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), numeroNr));		 		 
		 	 
		
		 
		 
		 criteria.setProjection(Projections
					.projectionList()
					.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), PendenciasDevolucaoVO.Fields.NUMERO_ITEM.toString())
					.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.CODIGO.toString()), PendenciasDevolucaoVO.Fields.MAT_CODIGO.toString())
					.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.NOME.toString()), PendenciasDevolucaoVO.Fields.MAT_NOME.toString())
					.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), PendenciasDevolucaoVO.Fields.MAT_DESCRICAO.toString())
					.add(Projections.groupProperty("UMD." + ScoUnidadeMedida.Fields.CODIGO.toString()), PendenciasDevolucaoVO.Fields.MAT_UNIDADE_MEDIDA.toString())					
					.add(Projections.max("INR." + SceItemNotaRecebimento.Fields.QUANTIDADE.toString()), PendenciasDevolucaoVO.Fields.QTDE_ENTRADA.toString())					
					.add(Projections.max("INR." + SceItemNotaRecebimento.Fields.QUANTIDADE.toString()), PendenciasDevolucaoVO.Fields.QTDE_NR.toString())
					.add(Projections.max("INR." + SceItemNotaRecebimento.Fields.VALOR.toString()), PendenciasDevolucaoVO.Fields.VLR_TOTAL_ITEM_NR_ORIGINAL.toString())
					.add(Projections.max("IAF." + ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO.toString()), PendenciasDevolucaoVO.Fields.FATOR_CONVERSAO.toString()));		 
		
		
		 
		 criteria.setResultTransformer(Transformers.aliasToBean(PendenciasDevolucaoVO.class));
		 return this.executeCriteria(criteria);
		 
	}
	
	/**
	 * Retorna uma lista de itens de NR pesquisando pela PK da NR, pelo codigo do material e pelo numero do item da licitacao
	 * @param seqRecebimento
	 * @param matCodigo
	 * @param itlNumero
	 * @return List
	 */
	public List<SceItemNotaRecebimento> pesquisarItensNrPorSeqRecebimentoEMaterialEItemLicitacao(Integer seqRecebimento, Integer matCodigo, Short itlNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "INR");
		criteria.createAlias("INR."+SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL");
			
		criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), seqRecebimento));		
		criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.MAT_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq("ITL."+ScoItemLicitacao.Fields.NUMERO_ITEM_LICITACAO.toString(), itlNumero));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Verifica se a devolucao possui NR ativa
	 * @param dfsSeq
	 * @param ealSeq
	 * @return Boolean
	 */
	public Boolean verificarDevolucaoComNrAtiva(Integer dfsSeq, Integer ealSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "INR");
		
		criteria.createAlias("INR."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias("NRS."+SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("DFE."+SceDocumentoFiscalEntrada.Fields.DEVOLUCAO_FORNECEDORES.toString(), "DFS");
		
		criteria.add(Restrictions.eq("DFS."+SceDevolucaoFornecedor.Fields.SEQ.toString(), dfsSeq));
		criteria.add(Restrictions.eq("NRS."+SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		
		DetachedCriteria subQueryEal = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
		subQueryEal.setProjection(Projections.property("EAL."+SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString()));
		subQueryEal.add(Restrictions.eq("EAL."+SceEstoqueAlmoxarifado.Fields.SEQ.toString(), ealSeq));
		
    	criteria.add(Subqueries.propertyIn("INR."+SceItemNotaRecebimento.Fields.MAT_CODIGO.toString(), subQueryEal));
			
		return executeCriteriaCount(criteria) == 0;
	}
	
	/**
	 * Obtem o item de NR pelo numero da NR e pelo codigo de material
	 * @param seqRecebimento
	 * @param matCodigo
	 * @return SceItemNotaRecebimento
	 */
	public SceItemNotaRecebimento obterItemNotaRecebimentoPorSeqRecebimentoEMaterial(Integer seqRecebimento, Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "INR");
		
		criteria.createAlias("INR."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.add(Restrictions.eq("NRS."+SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), seqRecebimento));
		criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.MAT_CODIGO.toString(), matCodigo));
		
		return (SceItemNotaRecebimento) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Pesquisa um item de NR pelo seu SEQ e pela PK da IAF
	 * @param numeroNr
	 * @param numeroAf
	 * @param numeroItemAf
	 * @return SceItemNotaRecebimento
	 */
	public SceItemNotaRecebimento obterItemNotaRecebimentoPorNrEAf(Integer numeroNr, Integer numeroAf, Integer numeroItemAf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "INR");
		
		criteria.createAlias("INR."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias("INR."+SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		
		criteria.add(Restrictions.eq("NRS."+SceNotaRecebimento.Fields.IND_DEBITO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("NRS."+SceNotaRecebimento.Fields.NUMERO_NR.toString(), numeroNr));
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numeroItemAf));
		criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.IND_DEBITO_NF.toString(), Boolean.FALSE));
		
		return (SceItemNotaRecebimento) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria pesquisarListaDestinoMaterialRecebidoCriteria(String numeroNr) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "INR");
		
		StringBuilder projectionNumber = new StringBuilder(100);
		projectionNumber.append("TO_CHAR(afn2_.PFR_LCT_NUMERO)||'/'||TO_CHAR(afn2_.NRO_COMPLEMENTO) NRO_AF");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("NRS." + SceNotaRecebimento.Fields.NUMERO_NR), DestinoMaterialRecebidoVO.Fields.NRO_NR.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO), DestinoMaterialRecebidoVO.Fields.NOTA_FISCAL.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.DT_ENTRADA), DestinoMaterialRecebidoVO.Fields.DATA_ENTR.toString())			
				.add(Projections.sqlProjection(projectionNumber.toString(), new String[] { "NRO_AF" }, new Type[] { StringType.INSTANCE }))			
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME), DestinoMaterialRecebidoVO.Fields.COMPRADOR.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.CVF_CODIGO), DestinoMaterialRecebidoVO.Fields.CV.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO), DestinoMaterialRecebidoVO.Fields.NRO_SC.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO), DestinoMaterialRecebidoVO.Fields.DESCRICAO_SC.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO), DestinoMaterialRecebidoVO.Fields.CC_REQ.toString())			
				.add(Projections.property("CCR." + FccCentroCustos.Fields.DESCRICAO), DestinoMaterialRecebidoVO.Fields.CC_REQ_DESC.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO), DestinoMaterialRecebidoVO.Fields.CC_APL.toString())	
				.add(Projections.property("CCA." + FccCentroCustos.Fields.DESCRICAO), DestinoMaterialRecebidoVO.Fields.CC_APL_DESC.toString())			
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO), DestinoMaterialRecebidoVO.Fields.MAT_CODIGO.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME), DestinoMaterialRecebidoVO.Fields.MAT_NOME.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO), DestinoMaterialRecebidoVO.Fields.MAT_DESCRICAO.toString())			
				.add(Projections.property("INR." + SceItemNotaRecebimento.Fields.QUANTIDADE), DestinoMaterialRecebidoVO.Fields.QTDE.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.UMD_CODIGO), DestinoMaterialRecebidoVO.Fields.UMD_CODIGO.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.APLICACAO), DestinoMaterialRecebidoVO.Fields.APLICACAO.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.JUSTIFICATIVA_USO), DestinoMaterialRecebidoVO.Fields.JUSTIFICATIVA_USO.toString())			
				);
		
		criteria.createAlias("INR." + SceItemNotaRecebimento.Fields.MAT_CODIGO.toString(), "MAT");  
		criteria.createAlias("INR." + SceItemNotaRecebimento.Fields.IAF_NUMERO.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), "FSC"); 
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO.toString(), "SLC");	
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), "CCR"); 
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), "CCA");  	
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString(), "AFN");  	
		criteria.createAlias("INR." + SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), "NRS"); 		
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.SERVIDOR.toString(), "SER"); 
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES"); 
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE"); 
		
		if(numeroNr != null) { 
			criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), numeroNr));
		}
		criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.IND_ESTOCAVEL.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString(), "INR."+SceItemNotaRecebimento.Fields.IAF_AFN_NUMERO));
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "IAF."+ScoItemAutorizacaoForn.Fields.AFN_NUMERO));
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(),  DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString(), "IAF."+ScoItemAutorizacaoForn.Fields.AFN_NUMERO));
		criteria.add(Restrictions.eq("NRS."+SceNotaRecebimento.Fields.NUMERO_NR.toString(), "INR."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO));
		criteria.add(Restrictions.eq("SER."+RapServidores.Fields.VIN_CODIGO.toString(), "SLC."+ScoSolicitacaoDeCompra.Fields.SOLICITANTE));
		
		criteria.addOrder(Order.asc("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(DestinoMaterialRecebidoVO.class));
		
		return criteria;
	}
	
	/**
	 * 
	 * @param numeroNr
	 * @return
	 */
	public List<DestinoMaterialRecebidoVO> pesquisarListaDestinoMaterialRecebido(String numeroNr) {
		DetachedCriteria criteria = pesquisarListaDestinoMaterialRecebidoCriteria(numeroNr);
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria pesquisarNotasRecebimentoCriteria(String numeroNr) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "INR");
		
		StringBuilder projectionNumer = new StringBuilder(100);
		projectionNumer.append("TO_CHAR(afn2_.PFR_LCT_NUMERO)||'/'||TO_CHAR(afn2_.NRO_COMPLEMENTO) NRO_AF");	
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("NRS." + SceNotaRecebimento.Fields.NUMERO_NR), DestinoMaterialRecebidoVO.Fields.NRO_NR.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO), DestinoMaterialRecebidoVO.Fields.NOTA_FISCAL.toString())
				.add(Projections.sqlProjection(projectionNumer.toString(), new String[] { "NRO_AF" }, new Type[] { StringType.INSTANCE }))
				);
		
		criteria.createAlias("INR." + SceItemNotaRecebimento.Fields.MAT_CODIGO.toString(), "MAT");  
		criteria.createAlias("INR." + SceItemNotaRecebimento.Fields.IAF_NUMERO.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), "FSC"); 
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString(), "AFN");  	
		criteria.createAlias("INR." + SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), "NRS"); 		
		
		if(numeroNr != null){
			criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), numeroNr));
		}
		criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.IND_ESTOCAVEL.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.AFN_NUMERO.toString(), "INR."+SceItemNotaRecebimento.Fields.IAF_AFN_NUMERO));
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "IAF."+ScoItemAutorizacaoForn.Fields.AFN_NUMERO));
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(),  DominioSimNao.N.isSim()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(DestinoMaterialRecebidoVO.class));
		
		return criteria;
	}

	/**
	 * 
	 * @param numeroNr
	 * @return
	 */
	public List<DestinoMaterialRecebidoVO> pesquisarNotasRecebimento(String numeroNr) {
		DetachedCriteria criteria = pesquisarNotasRecebimentoCriteria(numeroNr);
		
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param numeroNr
	 * @return
	 */
	public Long pesquisarNotasRecebimentoCount(String numeroNr) {
		DetachedCriteria criteria = pesquisarNotasRecebimentoCriteria(numeroNr);
		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Consulta que atende a Function F5 e F8 da estória #6635.
	 * @param dataGeracao
	 * @param mes para verificar entrada por Dia ou Mês
	 * @return criteria
	 */
	public Double pesquisarEntradaServicos(Date dataGeracao, boolean mes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				SceItemNotaRecebimento.class, "inr");
		criteria.createAlias("inr."
				+ SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(),
				"nrs", JoinType.INNER_JOIN);
		if (dataGeracao != null) {
			if (mes) {
				criteria.add(Restrictions.between("nrs."
						+ SceNotaRecebimento.Fields.DATA_GERACAO.toString(),
						DateUtil.obterDataInicioCompetencia(dataGeracao),
						DateUtil.obterDataComHoraFinal(dataGeracao)));
			} else {
				criteria.add(Restrictions.between("nrs."
						+ SceNotaRecebimento.Fields.DATA_GERACAO.toString(),
						DateUtil.truncaData(dataGeracao),
						DateUtil.obterDataComHoraFinal(dataGeracao)));
			}
		}
		criteria.add(Restrictions.or(Restrictions.isNull("nrs."
				+ SceNotaRecebimento.Fields.IND_ESTORNO), Restrictions.eq(
				"nrs." + SceNotaRecebimento.Fields.IND_ESTORNO,
				DominioSimNao.N.isSim())));
		criteria.add(Restrictions.isNotNull("inr."
				+ SceItemNotaRecebimento.Fields.SERVICO.toString()));
		criteria.setProjection(Projections.sum("inr."
				+ SceItemNotaRecebimento.Fields.VALOR.toString()));
		return (Double) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Consulta que atende a C1 primeira query da estória #6635.
	 * @param dataGeracao
	 * @return List<DadosEntradaMateriasDiaVO>
	 */
	public List<DadosEntradaMateriasDiaVO> entradaMateriasPrimeiraParte(Date dataGeracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MMT");
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString()), DadosEntradaMateriasDiaVO.Fields.DOCUMENTO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()), DadosEntradaMateriasDiaVO.Fields.GRUPO.toString());
		p.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), DadosEntradaMateriasDiaVO.Fields.DESCRICAO_GRUPO.toString());
		p.add(Projections.property("TMV." + SceTipoMovimento.Fields.SIGLA.toString()), DadosEntradaMateriasDiaVO.Fields.SIGLA.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), DadosEntradaMateriasDiaVO.Fields.CODIGO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), DadosEntradaMateriasDiaVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString()), DadosEntradaMateriasDiaVO.Fields.ESTOCAVEL.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString()), DadosEntradaMateriasDiaVO.Fields.IND_ESTORNO.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), DadosEntradaMateriasDiaVO.Fields.UNIDADE.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.QUANTIDADE.toString()), DadosEntradaMateriasDiaVO.Fields.QT_RECEBIMENTO.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.VALOR.toString()), DadosEntradaMateriasDiaVO.Fields.VALOR.toString());
		p.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), DadosEntradaMateriasDiaVO.Fields.FORNECEDOR.toString());

		criteria.setProjection(p);
		
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");	
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV");
		
		Calendar dataInicio = GregorianCalendar.getInstance();
		Calendar dataFim = GregorianCalendar.getInstance();
		
		dataInicio.setTime(dataGeracao);
		
		setarhoraMinutoSegundo(dataGeracao, dataInicio, dataFim);
		
		criteria.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), dataInicio.getTime(), dataFim.getTime()));
				
		DetachedCriteria subQueryParam = DetachedCriteria.forClass(AghParametros.class, "AGP");
		subQueryParam.setProjection(Projections.property("AGP." + AghParametros.Fields.VLR_NUMERICO.toString()));
		subQueryParam.add(Property.forName("AGP." + AghParametros.Fields.NOME.toString()).eq("P_TMV_DOC_NR"));

		criteria.add(Property.forName("TMV." + SceTipoMovimento.Fields.SEQ.toString()).eq(subQueryParam));
		criteria.setResultTransformer(Transformers.aliasToBean(DadosEntradaMateriasDiaVO.class));
		List<DadosEntradaMateriasDiaVO> valores = executeCriteria(criteria);
		if (valores != null && !valores.isEmpty()) {
			return valores;
		}
		return null;
	}


	/**
	 * Consulta que atende a C1 segunda query da estória #6635.
	 * 
	 * @param dataGeracao
	 * @return List<DadosEntradaMateriasDiaVO>
	 */
	public List<DadosEntradaMateriasDiaVO> entradaMateriasSegundaParte(Date dataGeracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MMT");
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString()), DadosEntradaMateriasDiaVO.Fields.DOCUMENTO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()), DadosEntradaMateriasDiaVO.Fields.GRUPO.toString());
		p.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), DadosEntradaMateriasDiaVO.Fields.DESCRICAO_GRUPO.toString());
		p.add(Projections.property("TMV." + SceTipoMovimento.Fields.SIGLA.toString()), DadosEntradaMateriasDiaVO.Fields.SIGLA.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), DadosEntradaMateriasDiaVO.Fields.CODIGO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), DadosEntradaMateriasDiaVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString()), DadosEntradaMateriasDiaVO.Fields.ESTOCAVEL.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString()), DadosEntradaMateriasDiaVO.Fields.IND_ESTORNO.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()), DadosEntradaMateriasDiaVO.Fields.UNIDADE.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.QUANTIDADE.toString()), DadosEntradaMateriasDiaVO.Fields.QT_RECEBIMENTO.toString());
		p.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.VALOR.toString()), DadosEntradaMateriasDiaVO.Fields.VALOR.toString());
		p.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), DadosEntradaMateriasDiaVO.Fields.FORNECEDOR.toString());

		criteria.setProjection(p);
		
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT");		
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL, "GMT");
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.NOTA_RECEBIMENTO.toString(), "DFS");
		criteria.createAlias("DFS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV");
		
		Calendar dataInicio = GregorianCalendar.getInstance();
		Calendar dataFim = GregorianCalendar.getInstance();
		
		dataInicio.setTime(dataGeracao);
		
		setarhoraMinutoSegundo(dataGeracao, dataInicio, dataFim);
		
		criteria.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), dataInicio.getTime(), dataFim.getTime()));
		
		DetachedCriteria subQueryParam = DetachedCriteria.forClass(AghParametros.class, "AGP");
		subQueryParam.setProjection(Projections.property("AGP." + AghParametros.Fields.VLR_NUMERICO.toString()));
		subQueryParam.add(Property.forName("AGP." + AghParametros.Fields.NOME.toString()).eq("P_TMV_DOC_DF"));
		
		criteria.add(Property.forName("TMV." + SceTipoMovimento.Fields.SEQ.toString()).eq(subQueryParam));
		
		criteria.setResultTransformer(Transformers.aliasToBean(DadosEntradaMateriasDiaVO.class));
		List<DadosEntradaMateriasDiaVO> valores = executeCriteria(criteria);
		if (valores != null && !valores.isEmpty()) {
			return valores;
		}
		return null;
	}
	
	/*
	 * Seta a hora, os minutos e os segundos na data geracao.
	 */
	private void setarhoraMinutoSegundo(Date dataGeracao, Calendar dataInicio,
			Calendar dataFim) {
		dataInicio.set(Calendar.HOUR, 0);
		dataInicio.set(Calendar.MINUTE, 0);
		dataInicio.set(Calendar.SECOND, 0);
		dataInicio.set(Calendar.MILLISECOND, 0);
		
		dataFim.setTime(dataGeracao);
		
		dataFim.set(Calendar.HOUR, 23);
		dataFim.set(Calendar.MINUTE, 59);
		dataFim.set(Calendar.SECOND, 59);
		dataFim.set(Calendar.MILLISECOND, 999);
	}
}