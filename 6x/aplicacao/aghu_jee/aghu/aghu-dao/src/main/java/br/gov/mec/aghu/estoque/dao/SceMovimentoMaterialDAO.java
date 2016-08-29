package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia;
import br.gov.mec.aghu.dominio.DominioEstocavelConsumoSinteticoMaterial;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioOrdenacaoConsumoSinteticoMaterial;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.ConsumoSinteticoMaterialVO;
import br.gov.mec.aghu.estoque.vo.EntradaSaidaSemLicitacaoVO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialAbcVO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.estoque.vo.RelatorioAjusteEstoqueVO;
import br.gov.mec.aghu.estoque.vo.RelatorioConsumoSinteticoMaterialVO;
import br.gov.mec.aghu.estoque.vo.RelatorioDetalhesAjusteEstoqueVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceMotivoMovimento;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceMovimentoMaterialId;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SceMovimentoMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceMovimentoMaterial> {	
	private static final long serialVersionUID = -7575470728189081831L;

	@Override
	protected void obterValorSequencialId(SceMovimentoMaterial elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		SceMovimentoMaterialId id = new SceMovimentoMaterialId();
		id.setSeq(getNextVal(SequenceID.SCE_MMT_SQ1));
		id.setDtCompetencia(elemento.getDtCompetencia());
		elemento.setId(id);
	}

	public SceMovimentoMaterial obterSceMovimentoMaterialPorTmvSeqTmvComplNroDoc(SceTipoMovimento tipoMovimento, Integer nroDoc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class);
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), tipoMovimento));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString(), nroDoc));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));

		List<SceMovimentoMaterial> retorno = executeCriteria(criteria);
		if (retorno != null && !retorno.isEmpty()) {
			return retorno.get(0);
		}
		return null;
	}

	public Double obterConsumoPassadoPorTmvEstorno(Date dataInicio, Date dataFim, Integer codigoMaterial, Integer codigoCCusto) {
		Double result = 0.0;

		if (dataInicio != null && dataFim != null && codigoMaterial != null && codigoCCusto != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MM");
			ProjectionList p = Projections.projectionList();
			String nomeCampoEstorno = CoreUtil.obterNomeCampoAtributo(SceMovimentoMaterial.class, SceMovimentoMaterial.Fields.IND_ESTORNO.toString());
			String nomeCampoTmv = "{alias}.TMV_SEQ";
			String nomeCampoQtd = CoreUtil.obterNomeCampoAtributo(SceMovimentoMaterial.class, SceMovimentoMaterial.Fields.QUANTIDADE.toString());

			// Se nao for estorno, soma as quantidades quando tmv = 5 menos de quando tmv = 3
			String sqlProjection = "SUM(CASE WHEN " + nomeCampoEstorno + "='" + DominioSimNao.N.toString() + "' then " + "CASE WHEN " + nomeCampoTmv + " = 5 then " + "coalesce("
					+ nomeCampoQtd + ",0) " + "ELSE " + "coalesce(" + nomeCampoQtd + ",0) * -1 " + "END " + "ELSE 0 " + "END ) - " +
					// Menos a soma dos estornos quando tmv=5 menos quando tmv =3
					"SUM(CASE WHEN " + nomeCampoEstorno + "='" + DominioSimNao.S.toString() + "' then " + "CASE WHEN " + nomeCampoTmv + " = 5 then " + "coalesce(" + nomeCampoQtd
					+ ",0) " + "ELSE " + "coalesce(" + nomeCampoQtd + ",0) * -1 " + "END " + "ELSE 0 " + "END ) as consumo";
			p.add(Projections.sqlProjection(sqlProjection, new String[] { "consumo" }, new Type[] { DoubleType.INSTANCE }));

			criteria.setProjection(p);
			criteria.createAlias("MM." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO, "TM", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.CENTRO_CUSTO.toString() + "." + FccCentroCustos.Fields.CODIGO.toString(), codigoCCusto));
			criteria.add(Restrictions.between(SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), dataInicio, dataFim));
			// De acordo com a procedure, só os movimentos do tipo 3 e 5
			List<Short> tiposMov = new ArrayList<Short>();
			tiposMov.add(Short.parseShort("3"));
			tiposMov.add(Short.parseShort("5"));
			criteria.add(Restrictions.in("TM." + SceTipoMovimento.Fields.SEQ, tiposMov));
			List<Double> list = this.executeCriteria(criteria);
			if (list != null && !list.isEmpty()) {
				result = list.get(0);
			}
		}
		return result;
	}

	public Date obterDataUltimaRequisicao(Integer matCodigo, Short almSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MM");
		criteria.createAlias("MM." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO, "TM", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));
		criteria.add(Restrictions.eq("TM." + SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioIndOperacaoBasica.DB));
		criteria.add(Restrictions.eq("TM." + SceTipoMovimento.Fields.IND_MOVIMENTO_CONSUMO.toString(), Boolean.TRUE));
		criteria.setProjection(Projections.max(SceMovimentoMaterial.Fields.DATA_GERACAO.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public List<RelatorioAjusteEstoqueVO> pesquisarDadosRelatorioAjusteEstoque(Date dataCompetencia, List<String> siglasTipoMovimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MOV");
		criteria.createAlias("MOV." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.INNER_JOIN);
		criteria.createAlias("MOV." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV", JoinType.INNER_JOIN);
		criteria.createAlias("MOV." + SceMovimentoMaterial.Fields.MOTIVO_MOVIMENTO.toString(), "MTM", JoinType.INNER_JOIN);
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("MOV." + SceMovimentoMaterial.Fields.DATA_GERACAO.toString()), RelatorioAjusteEstoqueVO.Fields.DATA_GERACAO.toString());
		p.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()), RelatorioAjusteEstoqueVO.Fields.CODIGO_GRUPO_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), RelatorioAjusteEstoqueVO.Fields.CODIGO_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), RelatorioAjusteEstoqueVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("TMV." + SceTipoMovimento.Fields.SIGLA.toString()), RelatorioAjusteEstoqueVO.Fields.SIGLA_TIPO_MOVIMENTO.toString());
		p.add(Projections.property("MTM." + SceMotivoMovimento.Fields.DESCRICAO.toString()), RelatorioAjusteEstoqueVO.Fields.DESCRICAO_MOTIVO_MOVIMENTO.toString());
		String nomeCampoEstorno = CoreUtil.obterNomeCampoAtributo(SceMovimentoMaterial.class, SceMovimentoMaterial.Fields.IND_ESTORNO.toString());
		String nomeCampoQtd = CoreUtil.obterNomeCampoAtributo(SceMovimentoMaterial.class, SceMovimentoMaterial.Fields.QUANTIDADE.toString());
		String nomeCampoValor = CoreUtil.obterNomeCampoAtributo(SceMovimentoMaterial.class, SceMovimentoMaterial.Fields.VALOR.toString());
		String sqlProjectionQuantidade = "(CASE WHEN " + nomeCampoEstorno + "='" + "N" + "' then " + "coalesce(" + nomeCampoQtd + ",0) " + "ELSE " + "coalesce(" + nomeCampoQtd
				+ ",0) * -1 " + "END ) as quantidadeAjuste";
		p.add(Projections.sqlProjection(sqlProjectionQuantidade, new String[] { "quantidadeAjuste" }, new Type[] { IntegerType.INSTANCE }));

		String sqlProjectionValorUnitario = "(CASE WHEN " + nomeCampoEstorno + "='" + "N" + "' then " + "coalesce(" + nomeCampoValor + ",0) " + "ELSE " + "coalesce("
				+ nomeCampoValor + ",0) * -1 " + "END )" + "/" +
				"(CASE WHEN " + nomeCampoEstorno + "='" + "N" + "' then " + "coalesce(" + nomeCampoQtd + ",0) " + "ELSE " + "coalesce(" + nomeCampoQtd + ",0) * -1 "
				+ "END ) as valorUnitario";
		p.add(Projections.sqlProjection(sqlProjectionValorUnitario, new String[] { "valorUnitario" }, new Type[] { DoubleType.INSTANCE }));
		String sqlProjectionValorAjuste = "(CASE WHEN " + nomeCampoEstorno + "='" + "N" + "' then " + "coalesce(" + nomeCampoValor + ",0) " + "ELSE " + "coalesce("
				+ nomeCampoValor + ",0) * -1 " + "END ) as valorAjuste";
		p.add(Projections.sqlProjection(sqlProjectionValorAjuste, new String[] { "valorAjuste" }, new Type[] { DoubleType.INSTANCE }));
		criteria.setProjection(p);
		criteria.add(Restrictions.eq("TMV." + SceTipoMovimento.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (dataCompetencia != null) {
			criteria.add(Restrictions.eq("MOV." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), DateUtil.obterDataInicioCompetencia(dataCompetencia)));
		}
		criteria.add(Restrictions.in("TMV." + SceTipoMovimento.Fields.SIGLA.toString(), siglasTipoMovimento));
		criteria.addOrder(Order.asc("MOV." + SceMovimentoMaterial.Fields.DATA_GERACAO.toString()));
		criteria.addOrder(Order.asc("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.CODIGO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioAjusteEstoqueVO.class));
		return executeCriteria(criteria);
	}

	public List<SceMovimentoMaterial> pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(Integer mes, Integer ano) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MOV");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("MOV." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()), SceMovimentoMaterial.Fields.DT_COMPETENCIA.toString());
		criteria.setProjection(p);
		if (mes != null) {
			criteria.add(Restrictions.sqlRestriction("extract(month from dt_competencia)= " + mes));
		}
		if (ano != null) {
			criteria.add(Restrictions.sqlRestriction("extract(year from dt_competencia)= " + ano));
		}
		criteria.add(Restrictions.sqlRestriction("extract(day from dt_competencia) = 01"));
		criteria.addOrder(Order.desc("MOV." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(SceMovimentoMaterial.class));
		return executeCriteria(criteria);
	}

	public SceMovimentoMaterial obterMovimentoMaterialPorNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		SceMovimentoMaterial retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class);
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString(), notaRecebimento.getSeq()));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), notaRecebimento.getTipoMovimento()));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));

		List<SceMovimentoMaterial> listMovMat = executeCriteria(criteria);
		if (listMovMat != null && listMovMat.size() > 0) {
			retorno = listMovMat.get(0);
		}
		return retorno;
	}

	public Double obterConsumoMediaTrimestralMovimentoMaterial(Date mesCompetencia, Integer codigoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MMT");
		String operacaoBasicaDB = DominioIndOperacaoBasica.DB.getDescricao();

		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV", JoinType.INNER_JOIN);

		String sqlProjection = "SUM(CASE WHEN {alias}.IND_ESTORNO='N' THEN " + " CASE WHEN tmv1_.IND_OPERACAO_BASICA='" + operacaoBasicaDB + "' THEN "
				+ "			 COALESCE({alias}.VALOR,0) 			" + " ELSE 											" + " 			(COALESCE({alias}.VALOR,0) * -1) END" + " 	 ELSE 0 END) - "
				+ "SUM(CASE WHEN {alias}.IND_ESTORNO='S' THEN " + " CASE WHEN tmv1_.IND_OPERACAO_BASICA='" + operacaoBasicaDB + "' THEN " + " COALESCE({alias}.VALOR, 0) ELSE 				"
				+ " (COALESCE({alias}.VALOR,0) * -1) END  		" + " ELSE 0 END) / 3 as valorTrimestre			";

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sqlProjection(sqlProjection, new String[] { "valorTrimestre" }, new Type[] { DoubleType.INSTANCE }));
		criteria.setProjection(p);

		Calendar mesCompetenciaAnterior = Calendar.getInstance();
		mesCompetenciaAnterior.setTime(mesCompetencia);
		mesCompetenciaAnterior.add(Calendar.MONTH, -2);
		criteria.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), DateUtil.obterDataInicioCompetencia(mesCompetenciaAnterior.getTime()),
				DateUtil.obterDataInicioCompetencia(mesCompetencia)));
		criteria.add(Restrictions.eq("TMV." + SceTipoMovimento.Fields.IND_MOVIMENTO_CONSUMO.toString(), Boolean.TRUE));
		if (codigoMaterial != null) {
			criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		}
		return (Double) executeCriteriaUniqueResult(criteria);
	}

	public Double obterValorMaterialNoMovimento(Integer numeroDocumento, Integer codigoMaterial, Short tmvSeq, Byte tmvComp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "mmt");
		criteria.createAlias("mmt." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "tmv", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.projectionList().add(
				Projections.sqlProjection("coalesce(sum({alias}.VALOR),0) as valor ", new String[] { "valor" }, new Type[] { DoubleType.INSTANCE })));
		criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq("tmv." + SceTipoMovimento.Fields.SEQ.toString(), tmvSeq));
		criteria.add(Restrictions.eq("tmv." + SceTipoMovimento.Fields.COMPLEMENTO.toString(), tmvComp));
		criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString(), numeroDocumento));
		criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		List<Double> valores = executeCriteria(criteria, 0, 1, null, true);
		if (valores != null && !valores.isEmpty()) {
			return valores.get(0);
		}
		return null;
	}

	public Double buscarUltimoCustoEntradaPorMaterialTipoMov(
			ScoMaterial matCodigo, Short shortValue) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				SceMovimentoMaterial.class, "mmt");
		criteria.add(Restrictions.eq("mmt."
				+ SceMovimentoMaterial.Fields.MATERIAL.toString(), matCodigo));
		criteria.add(Restrictions.ge("mmt."
				+ SceMovimentoMaterial.Fields.QUANTIDADE.toString(), 1));
		DetachedCriteria criteriaSub = DetachedCriteria.forClass(
				SceMovimentoMaterial.class, "mmtSub");
		criteriaSub.setProjection(Projections.max("mmtSub."
				+ SceMovimentoMaterial.Fields.DATA_GERACAO.toString()));
		criteriaSub.add(Restrictions.eq("mmtSub."
				+ SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(),
				shortValue));
		criteriaSub.add(Restrictions.eq("mmtSub."
				+ SceMovimentoMaterial.Fields.MATERIAL.toString(), matCodigo));
		criteria.add(Subqueries.propertyIn("mmt."
				+ SceMovimentoMaterial.Fields.DATA_GERACAO.toString(),
				criteriaSub));

		List<SceMovimentoMaterial> lista = executeCriteria(criteria);
		Double soma = 0.0;

		if (lista == null || lista.isEmpty()) {
			return 0.0;
		}
		
		if (lista != null && !lista.isEmpty()){
			SceMovimentoMaterial item = lista.get(0);
			soma = item.getValor().doubleValue() / item.getQuantidade();
		}
		return soma;
	}

	public Date obterDataCompetencia(Short tmvSeq, Integer tmvComplemento, Integer nroDocGeracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class);
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), tmvSeq));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_COMPL.toString(), tmvComplemento.byteValue()));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString(), nroDocGeracao));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.setProjection(Projections.max(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public List<SceMovimentoMaterial> obterDataUltimaCompra(Integer matCodigo, Short AlmSeq, Short tmvSeq) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class);
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), tmvSeq));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.ALMOXARIFADO_SEQ.toString(), AlmSeq));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.addOrder(Order.asc(SceMovimentoMaterial.Fields.DATA_GERACAO.toString()));
		return executeCriteria(criteria);
	}

	/** Método que realiza a pesquisa de competencias de estoque geral, por mes e ano.
	 * @param mes	 * @param ano	 * @return
	 */
	public List<MovimentoMaterialVO> pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(Integer mes, Integer ano) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MOV");
		ProjectionList p = Projections.projectionList();

		// quando se utiliza o distinct no oracle fica muito ruim, mas no postgres ele faz um hashsort antes
		// o group by teoricamente já garante o distinct, mas para não ter muitos problemas, vamos manter
		// o isOrcle...
		if (isOracle()) {
			p.add(Projections.groupProperty("MOV." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()),
					MovimentoMaterialVO.Fields.DT_COMPETENCIA.toString());
			p.add(Projections.min("MOV." + SceMovimentoMaterial.Fields.SEQ.toString()),
					MovimentoMaterialVO.Fields.SEQ.toString());
		} else {
			p.add(Projections.distinct(Projections.groupProperty("MOV." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString())),
					MovimentoMaterialVO.Fields.DT_COMPETENCIA.toString());	
			p.add(Projections.min("MOV." + SceMovimentoMaterial.Fields.SEQ.toString()),
					MovimentoMaterialVO.Fields.SEQ.toString());
		}

		criteria.setProjection(p);

		if (mes != null) {
			criteria.add(Restrictions.sqlRestriction("extract(month from dt_competencia)= " + mes));
		}
		if (ano != null) {
			criteria.add(Restrictions.sqlRestriction("extract(year from dt_competencia)= " + ano));
		}

		criteria.add(Restrictions.sqlRestriction("extract(day from dt_competencia) = 01"));
		criteria.addOrder(Order.desc("MOV." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MovimentoMaterialVO.class));
		return executeCriteria(criteria);
	}

	/** Retorna criteria.
	 * @param id	 * @param matCodigo	 * @param dtGeracao	 * @param almSeq	 * @param cctCodigo	 * @param indEstorno
	 * @param frnNumero	 * @param dtCompetencia	 * @param nroDocGeracao	 * @return
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	private DetachedCriteria obterCriteriaMovimentoMaterial(SceTipoMovimento tipo, ScoMaterial material, Date dtGeracao, SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto,
			DominioSimNao indEstorno, ScoFornecedor fornecedor, SceMovimentoMaterial movimentoMaterialDataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer nroDocGeracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class);
		if (tipo != null) {
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), tipo.getId().getSeq()));
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_COMPL.toString(), tipo.getId().getComplemento()));
		}
		if (material != null) {
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), material.getCodigo()));
		}
		if (almoxarifado != null) {
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.ALMOXARIFADO_SEQ.toString(), almoxarifado.getSeq()));
		}
		if (centroCusto != null) {
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.CENTRO_CUSTO_CODIGO.toString(), centroCusto.getCodigo()));
		}
		if (fornecedor != null) {
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString(), fornecedor.getNumero()));
		}
		if (movimentoMaterialDataCompetencia != null) {
			if (comparacaoDataCompetencia == null || DominioComparacaoDataCompetencia.IGUAL.equals(comparacaoDataCompetencia)) {
				criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), movimentoMaterialDataCompetencia.getDtCompetencia()));
			} else {
				criteria.add(Restrictions.gt(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), movimentoMaterialDataCompetencia.getDtCompetencia()));
			}
		}
		if (nroDocGeracao != null) {
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString(), nroDocGeracao));
		}
		if (dtGeracao != null) {
			Calendar amanha = Calendar.getInstance();
			amanha.setTime(dtGeracao);
			amanha.add(Calendar.DATE, 1);
			criteria.add(Restrictions.between(SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), dtGeracao, amanha.getTime()));
		} else {
			Calendar amanha = Calendar.getInstance();
			amanha.setTime(new Date());
			amanha.add(Calendar.DATE, 1);
			criteria.add(Restrictions.le(SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), amanha.getTime()));
		}
		if (indEstorno != null && indEstorno.equals(DominioSimNao.N)) {
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		} else if (indEstorno != null && indEstorno.equals(DominioSimNao.S)) {
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.TRUE));
		}
		return criteria;
	}

	/**
	 * Pesquisa de Movimentos dos Materiais
	 * 
	 * @param id	 * @param material	 * @param dtGeracao	 * @param almoxarifado	 * @param centroCusto
	 * @param indEstorno	 * @param fornecedor	 * @param movimentoMaterialDataCompetencia
	 * @param nroDocGeracao	 * @param firstResult	 * @param maxResult	 * @param orderProperty
	 * @param asc	 * @return
	 */
	public List<SceMovimentoMaterial> pesquisarMovimentosMaterial(SceTipoMovimento tipo, ScoMaterial material, Date dtGeracao, SceAlmoxarifado almoxarifado,
			FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor, SceMovimentoMaterial movimentoMaterialDataCompetencia,
			DominioComparacaoDataCompetencia comparacaoDataCompetencia, Integer nroDocGeracao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaMovimentoMaterial(tipo, material, dtGeracao, almoxarifado, centroCusto, indEstorno, fornecedor, movimentoMaterialDataCompetencia,
				comparacaoDataCompetencia, nroDocGeracao);
		criteria.addOrder(Order.desc(SceMovimentoMaterial.Fields.SEQ.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, null, true);
	}

	/** Número de movimentações do material
	 * @param id	 * @param material	 * @param dtGeracao	 * @param almoxarifado
	 * @param centroCusto	 * @param indEstorno	 * @param fornecedor
	 * @param movimentoMaterialDataCompetencia	 * @param nroDocGeracao	 * @return
	 */
	public Long pesquisarMovimentosMaterialCount(SceTipoMovimento tipo, ScoMaterial material, Date dtGeracao, SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto,
			DominioSimNao indEstorno, ScoFornecedor fornecedor, SceMovimentoMaterial movimentoMaterialDataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer nroDocGeracao) {
		DetachedCriteria criteria = obterCriteriaMovimentoMaterial(tipo, material, dtGeracao, almoxarifado, centroCusto, indEstorno, fornecedor, movimentoMaterialDataCompetencia,
				comparacaoDataCompetencia, nroDocGeracao);
		return this.executeCriteriaCount(criteria);
	}

	@SuppressWarnings("unused")
	public BigDecimal pesquisarValorOutros(Date dtCompetencia, Short tmvDocTr, Short tmvDocTrCompl, Integer gmtCodigo, Boolean indEstorno, Boolean oEntrada, Integer frnNumeroHcpa) {
		StringBuilder hql = new StringBuilder(900);
		hql.append("select SUM (MMT.").append(SceMovimentoMaterial.Fields.VALOR.toString()).append(')')
		.append(" from ").append(SceMovimentoMaterial.class.getSimpleName()).append(" MMT ")
		.append("    ,").append(ScoMaterial.class.getSimpleName()).append(" MAT ")
		.append("    ,").append(SceTipoMovimento.class.getSimpleName()).append(" TMV ")
		.append(" where MMT.").append(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()).append(" = :dtCompetencia ")
		.append("   and MMT.").append(SceMovimentoMaterial.Fields.IND_ESTORNO.toString()).append(" = :indEstorno ")
		.append("   and ((MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()).append(" not in (11,3,5,2,29,30,27,17,18,44,46) ")
		.append("   and MMT.").append(SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString()).append(" = :frnNumeroHcpa ) OR")
		.append("       ((MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()).append(" = :tmvDocTr AND ")
		.append("         MMT.").append(SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString()).append(" <> :frnNumeroHcpa  AND ")
		.append("         MMT.").append(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()).append(" < '01/09/2002') OR ") // -- Essas datas devem ficar, em função de
		// inversão de movimentos naquele mes - (implantação Consignação).
		.append("        (MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()).append(" = :tmvDocTr AND ")
		.append("         MMT.").append(SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString()).append(" <> :frnNumeroHcpa  AND ")
		.append("         MMT.").append(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()).append(" > '01/08/2002'  AND ")
		.append("         MMT.").append(SceMovimentoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" = ").append("MMT.")
				.append(SceMovimentoMaterial.Fields.ALMOXARIFADO_SEQ_COMPL.toString())
		.append("     and MMT.").append(SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString()).append(" IN  ")
		.append("         (select MMT1.").append(SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString())
		.append("            from ").append(SceMovimentoMaterial.class.getSimpleName()).append(" MMT1 ")
		.append(" 			where MMT1.").append(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()).append(" = :dtCompetencia ")
		.append(" 			and   MMT1.").append(SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString()).append(" = ")
		.append(" 			      MMT.").append(SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString())
		.append(" 			and   MMT1.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()).append(" = :tmvDocTrCompl")
		.append(" 			and   MMT1.").append(SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString()).append(" = ").append(frnNumeroHcpa)
		.append(" 			and   MMT1.").append(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()).append(" = ")
		.append(" 			      MMT.").append(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()).append("))))") // -- Mvto orig. de Documento ou TRS do Forn p/ HCPA (Entrada)
		.append(" 			and   MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = ").append(" MMT.").append(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString())
		.append(" 			and   MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()).append(" = :pCodigo")
		.append(" 			and   TMV.").append(SceTipoMovimento.Fields.SEQ.toString()).append(" = MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString())
		.append(" 			and   TMV.").append(SceTipoMovimento.Fields.COMPLEMENTO.toString()).append(" = MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_COMPL.toString())
		.append(" 			and   ((MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()).append(" <> :tmvDocTr ");
		if (oEntrada) {
			hql.append(" 					and   ((TMV.").append(SceTipoMovimento.Fields.IND_OPERACAO_BASICA.toString()).append(" = '").append(DominioIndOperacaoBasica.CR.toString()).append('\'');
		} else {
			hql.append(" 					and   ((TMV.").append(SceTipoMovimento.Fields.IND_OPERACAO_BASICA.toString()).append(" = '").append(DominioIndOperacaoBasica.DB.toString()).append('\'');
		}
		hql.append(" 					and   MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ_DOCUMENTO.toString()).append(" IS NULL ) OR");
		hql.append(" 					     (TMV.").append(SceTipoMovimento.Fields.IND_OPERACAO_BASICA.toString()).append(" = '").append(DominioIndOperacaoBasica.NN.toString()).append('\'');
		if (oEntrada) {
			hql.append(" 					       and MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ_DOCUMENTO.toString()).append(" = 43 ))) OR");
		} else {
			hql.append(" 					       and (MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ_DOCUMENTO.toString()).append(" = 39 OR ");
			hql.append(" 					            MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ_DOCUMENTO.toString()).append(" = 37) ))) OR"); // -- HOF 31/12/08
		}
		hql.append(" 			       MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()).append(" = :tmvDocTr )"); // -- Devol. de EMPC (Entrada) ou TRS de Forn

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("dtCompetencia", dtCompetencia);
		query.setParameter("indEstorno", indEstorno);
		query.setParameter("tmvDocTr", tmvDocTr);
		query.setParameter("tmvDocTrCompl", tmvDocTrCompl);
		query.setParameter("pCodigo", gmtCodigo);
		query.setParameter("frnNumeroHcpa", frnNumeroHcpa);

		BigDecimal retorno = (BigDecimal) query.uniqueResult();
		if (retorno == null) {
			retorno = BigDecimal.ZERO;
		}
		return retorno;
	}

	public BigDecimal pesquisarValorNrRelMensalMateriais(Date dtCompetencia, Short tmvSeq, Integer gmtCodigo, Boolean indEstorno, Integer frnNumeroHcpa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MMT");
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.sum("MMT." + SceMovimentoMaterial.Fields.VALOR.toString()));
		criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), indEstorno));
		criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), tmvSeq));
		if (tmvSeq != 11 && tmvSeq != 2 && tmvSeq != 27 && frnNumeroHcpa != null) {
			criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString(), frnNumeroHcpa));
		}

		criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), dtCompetencia));
		if (gmtCodigo != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), gmtCodigo));
		}
		BigDecimal retorno = (BigDecimal) executeCriteriaUniqueResult(criteria);

		if (retorno == null) {
			retorno = BigDecimal.ZERO;
		}
		return retorno;
	}

	/**
	 * Pesquisa movimentos de material para geração da classificação ABC
	 * @param dataCompetencia	 * @param quantidadeMeses	 * @return
	 */
	public List<MovimentoMaterialAbcVO> pesquisarMovimentoMaterialCurvaAbc(Date dataCompetencia, Integer quantidadeMeses) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MMT");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty("MMT." + SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()));
		p.add(Projections.sum("MMT." + SceMovimentoMaterial.Fields.VALOR.toString()));
		p.add(Projections.groupProperty("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString()));
		p.add(Projections.groupProperty("TMV." + SceTipoMovimento.Fields.IND_OPERACAO_BASICA.toString()));
		criteria.setProjection(p);
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV", JoinType.INNER_JOIN);
		Calendar c = Calendar.getInstance();
		c.setTime(dataCompetencia);
		c.add(Calendar.MONTH, (-1 * quantidadeMeses));
		Date dataMes = c.getTime();
		c.setTime(dataCompetencia);
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date dataUltimoDia = c.getTime();

		criteria.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), dataMes, dataUltimoDia));
		criteria.add(Restrictions.eq("TMV." + SceTipoMovimento.Fields.IND_MOVIMENTO_CONSUMO.toString(), Boolean.TRUE));
		List<Object[]> lista = executeCriteria(criteria);
		List<MovimentoMaterialAbcVO> listaRetorno = new LinkedList<MovimentoMaterialAbcVO>();

		if (lista != null && !lista.isEmpty()) {
			for (Object[] o : lista) {
				Integer codigoMaterial = (Integer) o[0];
				BigDecimal valor = (BigDecimal) o[1];
				Boolean estorno = (Boolean) o[2];
				DominioIndOperacaoBasica operacaoBasica = (DominioIndOperacaoBasica) o[3];
				MovimentoMaterialAbcVO vo = new MovimentoMaterialAbcVO();
				vo.setCodigoMaterial(codigoMaterial);
				vo.setValor(valor != null ? valor.doubleValue() : null);
				vo.setEstorno(estorno);
				vo.setOperacaoBasica(operacaoBasica.getDescricao());
				listaRetorno.add(vo);
			}
		}
		return listaRetorno;
	}

	/**
	 * Pesquisa movimentos de material para geração de ponto pedido
	 * @param dataCompetencia	 * @param quantidadeMeses	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovimentoMaterialAbcVO> pesquisarMovimentoMaterialAtualizarPontoPedido(Date dataCompetencia, Integer codigoMaterial) {
		// Verifica o tipo de banco
		@SuppressWarnings("deprecation")
		final boolean isOracle = isOracle();

		// Consulta principal
		StringBuilder sqlSelect = new StringBuilder(400);
		sqlSelect.append("SELECT MMT.MAT_CODIGO as codigoMaterial, MMT.IND_ESTORNO as estorno, TMV.IND_OPERACAO_BASICA as operacaoBasica, MMT.QUANTIDADE as quantidade");
		sqlSelect.append(" FROM AGH.SCE_MOVIMENTO_MATERIAIS MMT, AGH.SCE_TIPO_MOVIMENTOS TMV");
		sqlSelect.append(" WHERE " ).append( getSqlMesCompetencia(isOracle, "MMT.DT_COMPETENCIA", dataCompetencia));
		sqlSelect.append(" AND TMV.SEQ = MMT.TMV_SEQ");
		sqlSelect.append(" AND TMV.COMPLEMENTO = MMT.TMV_COMPLEMENTO");
		sqlSelect.append(" AND TMV.IND_MOVIMENTO_CONSUMO = 'S'");
		sqlSelect.append(" AND MMT.MAT_CODIGO = " ).append( codigoMaterial);

		// Cria SQL nativa
		SQLQuery query = createSQLQuery(sqlSelect.toString());
		// Faz o hibernate detectar o tipo dos aliases
		query.addScalar("codigoMaterial", IntegerType.INSTANCE);
		query.addScalar("estorno", BooleanType.INSTANCE);
		query.addScalar("operacaoBasica", StringType.INSTANCE);
		query.addScalar("quantidade", IntegerType.INSTANCE);
		// Transforma e seta aliases do resultado no VO
		query.setResultTransformer(Transformers.aliasToBean(MovimentoMaterialAbcVO.class));
		// Retorna Lista
		return query.list();
	}

	/**
	 * Obtém o mês de competência em uma SQL nativa ou de acordo com o banco
	 * @param isOracle	 * @param dataCompetencia	 * @return
	 */
	private String getSqlMesCompetencia(final boolean isOracle, String campoAlias, final Date dataCompetencia) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		final String dataFormatada = dateFormat.format(dataCompetencia);
		final String oracle = " " + campoAlias + " = TO_DATE('" + dataFormatada + "','yyyymm')";
		final String postgre = " TO_CHAR(" + campoAlias + ",'yyyymm') = '" + dataFormatada + "'";
		return isOracle ? oracle : postgre;
	}

	public RelatorioDetalhesAjusteEstoqueVO obterDetalhesAjusteEstoque(Integer mmtSeq) {
		RelatorioDetalhesAjusteEstoqueVO detalhes = new RelatorioDetalhesAjusteEstoqueVO();
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "mmt");
		criteria.createAlias("mmt." + SceMovimentoMaterial.Fields.ALMOXARIFADO.toString(), "alm", JoinType.INNER_JOIN);
		criteria.createAlias("mmt." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "mat", JoinType.INNER_JOIN);
		criteria.createAlias("mmt." + SceMovimentoMaterial.Fields.FORNECEDOR.toString(), "frn", JoinType.INNER_JOIN);
		criteria.createAlias("mmt." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "tmv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mmt." + SceMovimentoMaterial.Fields.MOTIVO_MOVIMENTO.toString(), "mtv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mmt." + SceMovimentoMaterial.Fields.SERVIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.SEQ.toString(), mmtSeq));
		List<SceMovimentoMaterial> movimentos = executeCriteria(criteria);
		SceMovimentoMaterial movimento = null;
		if (movimentos != null && movimentos.size() > 0) {
			movimento = movimentos.get(0);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
		
		if (movimento != null) {
			detalhes.setAlmoxarifadoSeq(movimento.getAlmoxarifado().getSeq());
			detalhes.setAlmoxarifadoDesc(movimento.getAlmoxarifado().getDescricao());
			detalhes.setFornecedorNumero(movimento.getFornecedor().getNumero());
			detalhes.setFornecedorDesc(movimento.getFornecedor().getNomeFantasia());
			detalhes.setMaterialCodigo(movimento.getMaterial().getCodigo());
			detalhes.setMaterialDesc(movimento.getMaterial().getNome());
			if (movimento.getMotivoMovimento() != null) {
				detalhes.setMotivoMovimentoSeq(movimento.getMotivoMovimento().getId().getNumero());
				detalhes.setMotivoMovimentoDesc(movimento.getMotivoMovimento().getDescricao());
			}

			detalhes.setTipoMovimentoSeq(movimento.getTipoMovimento().getId().getSeq());
			detalhes.setTipoMovimentoCompl(movimento.getTipoMovimento().getId().getComplemento().intValue());
			detalhes.setTipoMovimentoDesc(movimento.getTipoMovimento().getDescricao());
			detalhes.setQuantidade(movimento.getQuantidade());
			detalhes.setGeradoEm(sdf.format(movimento.getDtGeracao()));
			detalhes.setGeradoPor(movimento.getServidor().getPessoaFisica().getNome());
			detalhes.setUnidade(movimento.getUnidadeMedida().getCodigo());
		}

		return detalhes;
	}

	/**
	 * Pesquisa o consumo sintético de materiais
	 * @param cctCodigo	 * @param almSeq	 * @param estocavel	 * @param dtCompetencia	 * @param ordenacao	 * @return
	 */
	public List<RelatorioConsumoSinteticoMaterialVO> pesquisarRelatorioConsumoSinteticoMaterial(final Integer cctCodigo, final Short almSeq,
			final DominioEstocavelConsumoSinteticoMaterial estocavel, final Date dtCompetencia, final DominioOrdenacaoConsumoSinteticoMaterial ordenacao,
			Long valorClassificacaoInicial, Long valorClassificacaoFinal, ScoGrupoMaterial grupoMaterial) {
		DetachedCriteria criteria = this.obterCriteriaRelatorioConsumoSinteticoMaterial(cctCodigo, almSeq, estocavel, dtCompetencia, valorClassificacaoInicial, valorClassificacaoFinal, grupoMaterial);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("CCT." + FccCentroCustos.Fields.CODIGO.toString())),
						RelatorioConsumoSinteticoMaterialVO.Fields.CODIGO_CENTRO_CUSTO.toString())
				.add(Projections.property("CCT." + FccCentroCustos.Fields.DESCRICAO.toString()), RelatorioConsumoSinteticoMaterialVO.Fields.DESCRICAO_CENTRO_CUSTO.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), RelatorioConsumoSinteticoMaterialVO.Fields.NOME_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), RelatorioConsumoSinteticoMaterialVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA.toString()), RelatorioConsumoSinteticoMaterialVO.Fields.UNIDADE_MEDIDA.toString()));
		
		criteria.addOrder(Order.asc("CCT." + FccCentroCustos.Fields.CODIGO.toString()));
		
		// Considera a ordenação informada pelo usuário. Vide: &p_order_by
		if (ordenacao != null) {
			if (DominioOrdenacaoConsumoSinteticoMaterial.C.equals(ordenacao)) {
				criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.CODIGO.toString()));
			} else if (DominioOrdenacaoConsumoSinteticoMaterial.N.equals(ordenacao)) {
				criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.NOME.toString()));
			}
		}
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioConsumoSinteticoMaterialVO.class));
		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa o consumo sintético pro material
	 * 
	 * @param codigoMaterial	 * @param cctCodigo	 * @param almSeq	 * @param estocavel
	 * @param dtCompetencia	 * @return
	 */
	public List<ConsumoSinteticoMaterialVO> pesquisarConsumoSinteticoPorMaterial(final Integer codigoMaterial, final Integer cctCodigo, final Short almSeq,
			final DominioEstocavelConsumoSinteticoMaterial estocavel, final Date dtCompetencia, final Long valorClassificacaoInicial, final Long valorClassificacaoFinal,
			ScoGrupoMaterial grupoMaterial) {
		DetachedCriteria criteria = this.obterCriteriaRelatorioConsumoSinteticoMaterial(cctCodigo, almSeq, estocavel, dtCompetencia, valorClassificacaoInicial,
				valorClassificacaoFinal, grupoMaterial);

		// Considera o código do material
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		List<SceMovimentoMaterial> listaMovimentoMaterial = executeCriteria(criteria);
		List<ConsumoSinteticoMaterialVO> retorno = new LinkedList<ConsumoSinteticoMaterialVO>();

		for (SceMovimentoMaterial movimentoMaterial : listaMovimentoMaterial) {
			ConsumoSinteticoMaterialVO vo = new ConsumoSinteticoMaterialVO();
			vo.setIndEstorno(movimentoMaterial.getIndEstorno());
			vo.setIndOperacaoBasica(movimentoMaterial.getTipoMovimento().getIndOperacaoBasica());
			vo.setQuantidade(movimentoMaterial.getQuantidade());
			vo.setValor(movimentoMaterial.getValor());
			retorno.add(vo);
		}
		return retorno;
	}

	private DetachedCriteria obterCriteriaRelatorioConsumoSinteticoMaterial(final Integer cctCodigo, final Short almSeq, final DominioEstocavelConsumoSinteticoMaterial estocavel,
			final Date dtCompetencia, final Long valorClassificacaoInicial, final Long valorClassificacaoFinal,
			ScoGrupoMaterial grupoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MMT");
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.CENTRO_CUSTO.toString(), "CCT");
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV");
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.ALMOXARIFADO.toString(), "ALM");

		// Considera o parâmetro do código do centro de custo
		if (cctCodigo != null) {
			criteria.add(Restrictions.eq("CCT." + FccCentroCustos.Fields.CODIGO.toString(), cctCodigo));
		}
		// Considera o parâmetro do seq do almoxarifado
		if (almSeq != null) {
			criteria.add(Restrictions.eq("ALM." + SceAlmoxarifado.Fields.SEQ.toString(), almSeq));
		}
		// Considera o intervalo mensal da data de competência, ou seja, do primeiro dia do mês de competência até o final
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(dtCompetencia);
		c.set(Calendar.DATE, c.getActualMinimum(Calendar.DATE));
		Date primeiroDiaMes = c.getTime();
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		Date ultimoDiaMes = c.getTime();
		criteria.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), primeiroDiaMes, ultimoDiaMes));

		// Considera que o movimento é de consumo
		criteria.add(Restrictions.eq("TMV." + SceTipoMovimento.Fields.IND_MOVIMENTO_CONSUMO.toString(), Boolean.TRUE));
		// Considera se o material é estocável
		if (estocavel != null) {
			criteria.add(Restrictions.eq("MAT."
					+ ScoMaterial.Fields.IND_ESTOCAVEL.toString(),
					DominioEstocavelConsumoSinteticoMaterial.S.equals(estocavel)));
		}

		if (grupoMaterial != null) {
			criteria.add(Restrictions.eq("MAT."
					+ ScoMaterial.Fields.GRUPO_MATERIAL.toString(),	grupoMaterial));
		}
		// Particularidade da PROCEDURE P_DEFINE_WHERE
		if (valorClassificacaoInicial != null && valorClassificacaoFinal != null) {
			DetachedCriteria subQueryMcl = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class, "MCL");
			subQueryMcl.setProjection(Projections.property("MCL." + ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString()));
			
			subQueryMcl.add(Property.forName("MCL." + ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString()).eqProperty("MAT." + ScoMaterial.Fields.CODIGO.toString()));
			subQueryMcl.add(Restrictions.between("MCL." + ScoMateriaisClassificacoes.Fields.CN5.toString(), valorClassificacaoInicial, valorClassificacaoFinal));
			// Desconsidera a Classificação de medicamentos da COMEDI
			Criterion intervaloNaoPermitido = Restrictions.between("MCL." + ScoMateriaisClassificacoes.Fields.CN5.toString(), 20001000000L, 20099999999L);
			subQueryMcl.add(Restrictions.not(intervaloNaoPermitido));
			
			// Acrescenta consulta secundária do intervalo de classificação de materiais...
			criteria.add(Subqueries.exists(subQueryMcl));
		}
		return criteria;
	}
	
	public List<SceMovimentoMaterial> obterConsumoMensal(
			Integer codigoMaterial, Date dtCompetencia) {
		if (codigoMaterial == null || dtCompetencia == null) {
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MM");
		criteria.createAlias( "MM." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO, "TM", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("TM." + SceTipoMovimento.Fields.IND_MOVIMENTO_CONSUMO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), dtCompetencia));
		List<SceMovimentoMaterial> listaMovimentoMaterial = executeCriteria(criteria);
		return listaMovimentoMaterial;
	}
	
	public Date obterDtUltimaMovimentacao(Integer codMaterial, Short almSeq, Short tmvSeq){
		List<SceMovimentoMaterial> movimentoMaterial;
		Date dataUltimaMovimentacao = null;
		
		DetachedCriteria criteria = obterCriteriaUltimaCompraEstocavel(codMaterial, almSeq, tmvSeq);
		movimentoMaterial = executeCriteria(criteria);
		
		if(movimentoMaterial != null && !movimentoMaterial.isEmpty()){
		   dataUltimaMovimentacao = movimentoMaterial.get(0).getDtGeracao();
		}
		return dataUltimaMovimentacao;
	}
	
	public Date obterDtUltimaCompra(Integer codMaterial, Short almSeq){
		List<SceMovimentoMaterial> movimentoMaterial;
		Date dataUltimaCompra = null;
		DetachedCriteria criteria = obterCriteriaUltimaCompraEstocavel(codMaterial, almSeq, null);		
		movimentoMaterial = executeCriteria(criteria);
		
		if(movimentoMaterial != null && !movimentoMaterial.isEmpty()){
			dataUltimaCompra = movimentoMaterial.get(0).getDtGeracao();
		}		
		return dataUltimaCompra;
	}
	
	public BigDecimal obterValorUltimaCompra(Integer codMaterial, Short almSeq, Short tmvSeq){		
		BigDecimal valor = null;
		Date dataUltimoMovimento = obterDtUltimaMovimentacao(codMaterial, almSeq, tmvSeq);
		if (dataUltimoMovimento == null){
			return valor;
		}
		List<SceMovimentoMaterial> movimentoMaterial;		
		DetachedCriteria criteria = obterCriteriaUltimaCompraEstocavel(codMaterial, almSeq, tmvSeq);
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), dataUltimoMovimento));		
		criteria.addOrder(Order.desc(SceMovimentoMaterial.Fields.DATA_GERACAO.toString()));		
		movimentoMaterial = executeCriteria(criteria);
		
		if(movimentoMaterial != null && !movimentoMaterial.isEmpty()){
			if (movimentoMaterial.get(0).getValor() == null || movimentoMaterial.get(0).getQuantidade() == null){
				return valor;
			}
			if (movimentoMaterial.get(0).getQuantidade() == 0){
				movimentoMaterial.get(0).setQuantidade(1);
			}
			valor = movimentoMaterial.get(0).getValor().divide(new BigDecimal(movimentoMaterial.get(0).getQuantidade()), 4, RoundingMode.HALF_UP);
		}		
		return valor;		
	}
	
	private DetachedCriteria obterCriteriaUltimaCompraEstocavel(Integer codMaterial, Short almSeq, Short tmvSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class);		
		if (codMaterial != null){
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), codMaterial));
		}		
		if (almSeq != null){
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));
		}		
		if (tmvSeq != null){
			criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), tmvSeq));	
		}		
		criteria.addOrder(Order.desc(SceMovimentoMaterial.Fields.DATA_GERACAO.toString()));
		return criteria;		
	}
	
	public Integer consumoMedioSazonal(Date data1, Date data2, Date data3, Integer codMaterial, Short almSeq, DominioIndOperacaoBasica indOperacaoBasica){		
		StringBuilder hql = new StringBuilder(300);
		hql.append("select MMT.").append(SceMovimentoMaterial.Fields.IND_ESTORNO.toString());
		hql.append(",MMT.").append(SceMovimentoMaterial.Fields.QUANTIDADE.toString());
		hql.append(" FROM ").append(SceMovimentoMaterial.class.getSimpleName()).append(" MMT, ");
		hql.append(SceTipoMovimento.class.getSimpleName()).append(" TMV ");
		hql.append(" WHERE MMT.").append(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()).append(" = :codMaterial ");
		hql.append(" AND (:almSeq IS NULL OR (MMT.").append(SceMovimentoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" = :almSeq ))");
		hql.append(" AND MMT.").append(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()).append(" IN ( :data1, :data2, :data3)");
		hql.append(" AND MMT.").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString());
		hql.append(" = TMV.").append(SceTipoMovimento.Fields.SEQ.toString());
		hql.append(" AND TMV.").append(SceTipoMovimento.Fields.IND_OPERACAO_BASICA.toString()).append(" = :indOperacaoBasica");
		hql.append(" AND TMV.").append(SceTipoMovimento.Fields.IND_MOVIMENTO_CONSUMO.toString()).append(" = 'S'");
		
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("codMaterial", codMaterial);
		query.setParameter("almSeq", almSeq);		
		query.setParameter("data1", data1);
		query.setParameter("data2", data2);
		query.setParameter("data3", data3);
		query.setParameter("indOperacaoBasica", indOperacaoBasica);
		
		List<Object[]> resultList = query.list();
		Integer totalDebito = calculaTotalPeriodo(resultList);
		return totalDebito;
	}
	
	private Integer calculaTotalPeriodo(List<Object[]> resultList){
		Integer totalPeriodo = 0, estornoSim = 0, estornoNao = 0;
		
		for (Object[] object : resultList) {
			if ((Boolean) object[0]){
				estornoSim = estornoSim + (Integer) object[1];
			} else {
				estornoNao = estornoNao + (Integer) object[1];
			}
		}
		totalPeriodo = (estornoNao - estornoSim) / 3;
		return totalPeriodo;
	}
	
	public Double obterValorMaterialSemAF(Integer codigoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MVM");
		criteria.createAlias("MVM." + SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.projectionList().add(
				Projections.sqlProjection("sum({alias}.VALOR) / sum({alias}.QUANTIDADE) as valor ", new String[] { "valor" }, new Type[] { DoubleType.INSTANCE})));
		criteria.add(Restrictions.eq("MVM." + SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GENERICO.toString(), DominioSimNao.N));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "MVMSUB");
		subCriteria.setProjection(Projections.max("MVMSUB." + SceMovimentoMaterial.Fields.DATA_GERACAO.toString()));
		subCriteria.add(Restrictions.eq("MVMSUB." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), 11));
		subCriteria.add(Restrictions.ge("MVMSUB." + SceMovimentoMaterial.Fields.QUANTIDADE.toString(), 1));
		subCriteria.add(Restrictions.eqProperty("MVMSUB." + SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(),
				"MVM." + SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()));
		List<Double> valores = executeCriteria(criteria, 0, 1, null, true);
		if (valores != null && !valores.isEmpty()) {
			return valores.get(0);
		}
		return null;
	}
	
	public Boolean motivoMovimentoUsado(Short seq, Byte complemento, Short numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class);
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MOTIVO_MOVIMENTO_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MOTIVO_MOVIMENTO_COMPLEMENTO.toString(), complemento));
		criteria.add(Restrictions.eq(SceMovimentoMaterial.Fields.MOTIVO_MOVIMENTO_NUMERO.toString(), numero));
		return executeCriteriaExists(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public Double buscarCustoOrteseOuProteseUltimaEntrada(Integer matCodigo){
		StringBuffer sql = new StringBuffer()
		.append(" select sce.valor/sce.quantidade valor_unitario ")
		.append(" from sce_movimento_materiais sce ")
		.append(" where sce.mat_codigo = ").append(matCodigo)
		.append(" and sce.tmv_seq = 11 ")
		.append(" and sce.ind_estorno = 'N' ")
		.append(" order by sce.seq desc ");
		org.hibernate.Query query = createSQLQuery(sql.toString());
		List<Object[]> lista = query.list();
		for(Object[] campo : lista){
			return (Double) campo[0];
		}
		return null;
	}
	/*** #34163 Consulta e faz o "UNION" da listaParte1 com listaParte2 
	 * @throws ApplicationBusinessException */
    public List<EntradaSaidaSemLicitacaoVO> listarEntradaSaidaESSLComp(EntradaSaidaSemLicitacaoVO filtro) throws ApplicationBusinessException{
        GerarRelatoriosExcelEntradaSaidaSemLicitacaoQueryBuilder builderConsulta = new GerarRelatoriosExcelEntradaSaidaSemLicitacaoQueryBuilder();
        Short mmtTmvSeq = carregarValorParamentro(AghuParametrosEnum.P_TMV_MOV_INSL.toString()); 
        Short mmtTmvSeq2 = carregarValorParamentro(AghuParametrosEnum.P_TMV_DOC_TR.toString()); 
        List<EntradaSaidaSemLicitacaoVO> listaParte1 = executeCriteria(builderConsulta.montarEntradaSaidaESSLParte1(filtro));
        List<EntradaSaidaSemLicitacaoVO> listaParte2 = executeCriteria(builderConsulta.montarEntradaSaidaESSLParte2(filtro, mmtTmvSeq, mmtTmvSeq2));
        for (EntradaSaidaSemLicitacaoVO elementoVO : listaParte1) {
        	if(elementoVO != null && elementoVO.getEslSeq() != null){	
	        	EntradaSaidaSemLicitacaoVO nroNfC3 = (EntradaSaidaSemLicitacaoVO) executeCriteriaUniqueResult(builderConsulta.consultaC3(elementoVO));
	        	if(nroNfC3 != null && nroNfC3.getNroNfC3() != null){
	        		elementoVO.setNroNfC3(nroNfC3.getNroNfC3());
	        	}
        	}
        	if(elementoVO != null && elementoVO.getIafAfnNumero() != null){	
	        	EntradaSaidaSemLicitacaoVO afC4 = (EntradaSaidaSemLicitacaoVO) executeCriteriaUniqueResult(builderConsulta.consultaC4(elementoVO));
	        	if(afC4 != null && afC4.getAfC4() != null){
	        		elementoVO.setAfC4(afC4.getAfC4());
	        	}
        	}
        	if(elementoVO != null && elementoVO.getIafAfnNumero() != null && elementoVO.getIafNumero() != null){	
		    	EntradaSaidaSemLicitacaoVO itemAFC5 = (EntradaSaidaSemLicitacaoVO) executeCriteriaUniqueResult(builderConsulta.consultaC5(elementoVO));
		    	if(itemAFC5 != null && itemAFC5.getItemAFC5() != null){
		    		elementoVO.setItemAFC5(itemAFC5.getItemAFC5());
		    	}
        	}
        	carregarCNPJRazaoSocial(elementoVO);
		}
        listaParte1.addAll(listaParte2);
        return listaParte1;
    }
    /** Carregar colunas de CNPJ e Razao Social */
	private void carregarCNPJRazaoSocial(EntradaSaidaSemLicitacaoVO elementoVO) {
		if(elementoVO.getEslDfeSeq()!= null || elementoVO.getEslFrnNumero() != null || elementoVO.getFevSeq() !=null){
			EntradaSaidaSemLicitacaoVO objetoC2 = listarCnpjRazaoSocial(elementoVO);
		   if(objetoC2 != null ){
			   	if(objetoC2.getVRazaoSocialConcatenada() != null){
			   		elementoVO.setVRazaoSocial(objetoC2.getVRazaoSocialConcatenada());
			   	}
			   	if(objetoC2.getVcnpjConcatenado() != null){
			   		elementoVO.setVCnpj(Long.valueOf(objetoC2.getVcnpjConcatenado()));
			   	}
		   }
		}
	}
    /*** Consulta Secundária para preencher corretamente os campos – FORNECEDOR(V_RAZAO_SOCIAL) e CNPJ( V_CNPJ) do relatório.*/
    public EntradaSaidaSemLicitacaoVO listarCnpjRazaoSocial(EntradaSaidaSemLicitacaoVO objetoRetorno){
    	GerarRelatoriosExcelEntradaSaidaSemLicitacaoQueryBuilder builder = new GerarRelatoriosExcelEntradaSaidaSemLicitacaoQueryBuilder();
    	Long v_dfe_seq = null;
    	if(objetoRetorno != null && objetoRetorno.getEslDfeSeq() != null){
    		// Faz count da SceDocumentoFiscalEntrada passando paramentro Integer v_dfe_seq 
    		DetachedCriteria countV_Cnpj = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class,"DFE");
            countV_Cnpj.createAlias("DFE."+SceDocumentoFiscalEntrada.Fields.FORNECEDOR_EVENTUAL.toString(), "FEV", JoinType.LEFT_OUTER_JOIN);
            countV_Cnpj.createAlias("DFE."+SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
            countV_Cnpj.add(Restrictions.eq("DFE." + SceDocumentoFiscalEntrada.Fields.SEQ.toString(), objetoRetorno.getEslDfeSeq()));
    		v_dfe_seq = executeCriteriaCount(countV_Cnpj);
    	}
    	DetachedCriteria cnpjRazaoSocialCriteria = builder.listarCnpjRazaoSocial(objetoRetorno, v_dfe_seq);
    	if (cnpjRazaoSocialCriteria == null) {
             return null;
    	}
        return (EntradaSaidaSemLicitacaoVO) executeCriteriaUniqueResult(cnpjRazaoSocialCriteria);    
    }
    /*** #34163 Consulta o valor do Parametro */
    public Short carregarValorParamentro(String parametro){
    	DetachedCriteria parametroCriteria = DetachedCriteria.forClass(AghParametros.class);
    	parametroCriteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), parametro));
    	parametroCriteria.setProjection(Projections.property(AghParametros.Fields.VLR_NUMERICO.toString()));
        return ((BigDecimal) executeCriteriaUniqueResult(parametroCriteria, false)).shortValue(); 
    }
    public List<Double> buscarCustoMedicamento(Integer matCodigo, Date competencia, Short tmvSeq) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "mmt");

        criteria.setProjection(Projections.projectionList().add(
                Projections.sqlProjection("sum({alias}.VALOR) / sum({alias}.QUANTIDADE) as valor ", new String[] { "valor" }, new Type[] { DoubleType.INSTANCE})));

        criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), matCodigo));
        criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), tmvSeq));
        criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
        criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), competencia));

        criteria.addOrder(Order.desc("mmt." + SceMovimentoMaterial.Fields.SEQ.toString()));

        return executeCriteria(criteria);
    }

    public List<Double> buscarCustoMedicamentoPelaUltimaEntradaEstoque(Integer matCodigo, Date competencia, Short tmvSeq) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SceMovimentoMaterial.class, "mmt");

        criteria.setProjection(Projections.projectionList().add(
                Projections.sqlProjection("{alias}.VALOR / {alias}.QUANTIDADE as valor ", new String[] { "valor" }, new Type[] { DoubleType.INSTANCE})));

        criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString(), matCodigo));
        criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), tmvSeq));
        criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
        criteria.add(Restrictions.eq("mmt." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), competencia));

        criteria.addOrder(Order.desc("mmt." + SceMovimentoMaterial.Fields.SEQ.toString()));

        return executeCriteria(criteria);
    }
	
}