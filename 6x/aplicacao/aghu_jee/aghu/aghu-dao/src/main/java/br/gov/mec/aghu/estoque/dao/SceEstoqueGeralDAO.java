package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.vo.ItensAutFornUpdateSCContrVO;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.EstoqueGeralVO;
import br.gov.mec.aghu.estoque.vo.OrdenacaoPosicaoFinalEstoque;
import br.gov.mec.aghu.estoque.vo.PosicaoFinalEstoqueVO;
import br.gov.mec.aghu.estoque.vo.RelatorioMensalMateriaisClassificacaoAbcVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceEstoqueGeralId;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateMaker;
import br.gov.mec.aghu.core.utils.DateUtil;
/**
 * @modulo estoque
 * @author 
 *
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class SceEstoqueGeralDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceEstoqueGeral> {

	private static final long serialVersionUID = 4058599084503087877L;


	@Override
	protected void obterValorSequencialId(SceEstoqueGeral elemento) {

		final ScoFornecedor fornecedor = elemento.getFornecedor();
		final ScoMaterial material = elemento.getMaterial();

		if (fornecedor == null) {
			throw new IllegalArgumentException("ScoMaterial não está associado corretamente.");
		}

		if (material == null) {
			throw new IllegalArgumentException("ScoFornecedor não está associado corretamente.");
		}

		SceEstoqueGeralId id = new SceEstoqueGeralId();
		id.setMatCodigo(material.getCodigo());
		id.setFrnNumero(fornecedor.getNumero());
		id.setDtCompetencia(elemento.getId().getDtCompetencia());

		elemento.setId(id);

	}
	
	/**
	 * Retorna ScoMaterial original
	 * @param id
	 * @return
	 */
	/*public SceEstoqueGeral obterOriginal(SceEstoqueGeral elementoModificado) {
		
		final SceEstoqueGeralId id = elementoModificado.getId();
		
		StringBuilder hql = new StringBuilder();
		
		hql.append("select o.").append(SceEstoqueGeral.Fields.ID.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.FORNECEDOR.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.UNIDADE_MEDIDA.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.MATERIAL.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.CUSTO_MEDIO_PONDERADO.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.RESIDUO.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.VALOR.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.QUANTIDADE.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.VALOR_CONSIGNADO.toString());
		hql.append(", o.").append(SceEstoqueGeral.Fields.QUANTIDADE_CONSIGNADA.toString());

		hql.append(" from ").append(SceEstoqueGeral.class.getSimpleName()).append(" o ");
		
		hql.append(" left outer join o.").append(SceEstoqueGeral.Fields.FORNECEDOR.toString());
		hql.append(" left outer join o.").append(SceEstoqueGeral.Fields.UNIDADE_MEDIDA.toString());
		hql.append(" left outer join o.").append(SceEstoqueGeral.Fields.MATERIAL.toString());

		hql.append(" where o.").append(SceEstoqueGeral.Fields.ID.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		SceEstoqueGeral original = null;
		
		List<Object[]> camposLst = (List<Object[]>) query.getResultList();
		
		if(camposLst != null && camposLst.size()>0) {
			
			Object[] campos = camposLst.get(0);
			original = new SceEstoqueGeral();
			
			original.setId(id);
			original.setFornecedor((ScoFornecedor)campos[1]);
			original.setUnidadeMedida((ScoUnidadeMedida)campos[2]);
			original.setMaterial((ScoMaterial)campos[3]);
			original.setCustoMedioPonderado((Double)campos[4]);
			original.setResiduo((Double)campos[5]);
			original.setValor((Double)campos[6]);
			original.setClassificacaoAbc((DominioClassifABC)campos[7]);
			original.setSubClassificacaoAbc((DominioClassifABC)campos[8]);
			original.setQtde((Integer)campos[9]);
			original.setValorConsignado((Double)campos[10]);
			original.setQtdeConsignada((Integer)11);

			
		}		
		return original;
	}*/

	public Double getCustoMedioPonderado(Integer codigoMaterial, Date dataCompetencia, Integer frnNumero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);

		criteria.setProjection(Projections.projectionList().add(
				Projections.property(SceEstoqueGeral.Fields.CUSTO_MEDIO_PONDERADO.toString())));

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.MAT_CODIGO.toString(), codigoMaterial));

		Calendar dtCompetencia = DateUtil.getCalendarBy(dataCompetencia);

		//= DateMaker.obterData(dtCompetencia.get(dtCompetencia.YEAR), dtCompetencia.get(dtCompetencia.MONTH), 1, 0, 0);
		// DateMaker.obterData(dtCompetencia.get(dtCompetencia.YEAR), dtCompetencia.get(dtCompetencia.MONTH), 0, 0, 0);
		Date dataMesAnoInicio  = DateMaker.obterData(dtCompetencia.get(Calendar.YEAR), dtCompetencia.get(Calendar.MONTH), dtCompetencia.getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0);
		Date dataMesAnoFim = DateMaker.obterData(dtCompetencia.get(Calendar.YEAR), dtCompetencia.get(Calendar.MONTH), dtCompetencia.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0);

		/*criteria.add(Restrictions.le(SceEstoqueGeral.Fields.DT_COMPETENCIA.toString(), dataMesAnoInicio));*/
		criteria.add(Restrictions.between(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dataMesAnoInicio, dataMesAnoFim));

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.FRN_NUMERO.toString(), frnNumero));

		Object custoMedioPonderado = executeCriteriaUniqueResult(criteria);

		if(custoMedioPonderado != null){
			return ((BigDecimal) custoMedioPonderado).doubleValue();
		}

		return new Double(0);
	}
	
	public SceEstoqueGeral pesquisarEstoqueGeralPorMatDtCompFornecedor(Integer codMaterial, Date dataCompetencia, Integer frnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.MAT_CODIGO.toString(), codMaterial));
		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.FRN_NUMERO.toString(), frnNumero));
		
		Calendar data = GregorianCalendar.getInstance();
		data.setTime(dataCompetencia);
		data.set(Calendar.DAY_OF_MONTH, 1);
		data.set(Calendar.HOUR_OF_DAY, 0);
		data.set(Calendar.MINUTE, 0);
		data.set(Calendar.SECOND, 0);
		data.set(Calendar.MILLISECOND, 0);

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), data.getTime()));

		List<SceEstoqueGeral> lista = executeCriteria(criteria);

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}

		return null; 
	}

	public Long countSceEstoqueGeralMaterialFornecedorDataCompetencia(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, Date dtCompetencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);

		Calendar competencia = DateUtil.getCalendarBy(dtCompetencia);

		Date dataMesAnoInicio  = DateMaker.obterData(competencia.get(Calendar.YEAR), competencia.get(Calendar.MONTH), competencia.getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0);
		Date dataMesAnoFim = DateMaker.obterData(competencia.get(Calendar.YEAR), competencia.get(Calendar.MONTH), competencia.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0);

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.MAT_CODIGO.toString(), sceEstoqueAlmoxarifado.getMaterial().getCodigo()));

		criteria.add(Restrictions.between(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dataMesAnoInicio, dataMesAnoFim));

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.FORNECEDOR.toString(), sceEstoqueAlmoxarifado.getFornecedor()));

		return executeCriteriaCount(criteria);
	}


	public SceEstoqueGeral obterSceEstoqueGeralPorMaterialDataCompetencia(ScoMaterial material,
			Date dataCompetencia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.MAT_CODIGO.toString(), material.getCodigo()));

//		Calendar dtCompetencia = DateUtil.getCalendarBy(dataCompetencia);

		//= DateMaker.obterData(dtCompetencia.get(dtCompetencia.YEAR), dtCompetencia.get(dtCompetencia.MONTH), 1, 0, 0);
		// DateMaker.obterData(dtCompetencia.get(dtCompetencia.YEAR), dtCompetencia.get(dtCompetencia.MONTH), 0, 0, 0);
//		Date dataMesAnoInicio  = DateMaker.obterData(dtCompetencia.get(Calendar.YEAR), dtCompetencia.get(Calendar.MONTH), dtCompetencia.getActualMinimum(Calendar.DAY_OF_MONTH), 0, 0);
//		Date dataMesAnoFim = DateMaker.obterData(dtCompetencia.get(Calendar.YEAR), dtCompetencia.get(Calendar.MONTH), dtCompetencia.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0);

		/*criteria.add(Restrictions.le(SceEstoqueGeral.Fields.DT_COMPETENCIA.toString(), dataMesAnoInicio));*/
		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), DateUtil.truncaData(dataCompetencia)));

		List<SceEstoqueGeral> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return (SceEstoqueGeral) result.get(0);
		}

	}

	/**
	 *  Pesquisa número transferência
	 *   @param nroGeracao
	 *   @param dtaCompetencia
	 *   @return SceEstoqueGeral
	 */
	public SceEstoqueGeral pesquisarNumTransferencia(Integer nroGeracao, Date dtaCompetencia) {

		SceEstoqueGeral retorno = null;

		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemTransferencia.class,"ITR");
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("EAL2." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString()));
		p.add(Projections.property("ETG." + SceEstoqueGeral.Fields.CUSTO_MEDIO_PONDERADO.toString()));
		
		criteria.setProjection(p);


		criteria.createAlias("ITR." + SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO_ORIGEM, "EAL1", Criteria.INNER_JOIN);
		criteria.createAlias("ITR." + SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO, "EAL2", Criteria.INNER_JOIN);
		criteria.createAlias("EAL2." + SceEstoqueAlmoxarifado.Fields.MATERIAL, "MAT", Criteria.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_GERAL, "ETG", Criteria.INNER_JOIN);
		criteria.createAlias("EAL2." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR, "FRN", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eqProperty("ETG." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(),"EAL2."+SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()));

		criteria.add(Restrictions.eq("ETG." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dtaCompetencia));
		criteria.add(Restrictions.eq("ITR." + SceItemTransferencia.Fields.TRF_SEQ.toString(), nroGeracao));

		List<Object[]> lista = executeCriteria(criteria);
		if(lista != null && lista.size()>0) {
			for(Object[] obj : lista) {
				retorno = new SceEstoqueGeral();

				retorno.setFornecedor((ScoFornecedor)obj[0]);
				retorno.setCustoMedioPonderado(new BigDecimal(obj[1].toString()));
			}
		}
		return retorno;
	}
	
	/**
	 * Pesquisa SceEstoqueGeral através do material
	 * @param material
	 * @return
	 */
	public List<SceEstoqueGeral> pesquisarEstoqueGeralPorMaterial(ScoMaterial material) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.MAT_CODIGO.toString(), material.getCodigo()));
		
		return executeCriteria(criteria);
		
	}


	/**
	 * Efetua a pesquisa para obter os dados do relatório mensal de materiais
	 * Classificação ABC
	 * 
	 * @param mesCompetencia
	 * @param codigoFornecedor
	 * @return Lista de Instancia do VO com os dados do relatório
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<RelatorioMensalMateriaisClassificacaoAbcVO> pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(
			Date mesCompetencia, Integer codigoFornecedor){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");
		criteria.createAlias("MAT." + ScoMaterial.Fields.MOVIMENTOS_MATERIAL.toString(),"MMT", Criteria.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_GERAL.toString(), "EGR",Criteria.INNER_JOIN);
		criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV",Criteria.INNER_JOIN);
		criteria.createAlias("EGR." + SceEstoqueGeral.Fields.FORNECEDOR.toString(), "FRN",Criteria.INNER_JOIN);
		String operacaoBasicaDB = DominioIndOperacaoBasica.DB.getDescricao();
		StringBuilder sqlProjection = new StringBuilder(500);
		
		boolean isOracle = isOracle();
		
		sqlProjection.append(" (SUM(CASE WHEN MMT1_.IND_ESTORNO='N' THEN 								");
		sqlProjection.append(" 		CASE WHEN TMV3_.IND_OPERACAO_BASICA='" + operacaoBasicaDB + "' THEN ");
		if(isOracle){
			sqlProjection.append(" 			NVL(MMT1_.VALOR,0) ELSE 									");
		}else{
			sqlProjection.append(" 			COALESCE(MMT1_.VALOR,0) ELSE 									");
		}
		if(isOracle){
			sqlProjection.append(" 			(NVL(MMT1_.VALOR,0)* -1) END  								");
		}else{
			sqlProjection.append(" 			(COALESCE(MMT1_.VALOR,0)* -1) END  								");
		}
		sqlProjection.append(" 		ELSE 0 END) - 														");
		sqlProjection.append(" 	SUM(CASE WHEN MMT1_.IND_ESTORNO='S' THEN 								");
		sqlProjection.append(" 		CASE WHEN TMV3_.IND_OPERACAO_BASICA='" + operacaoBasicaDB + "' THEN ");
		if(isOracle){
			sqlProjection.append(" 		NVL(MMT1_.VALOR,0) ELSE ");
		}else{
			sqlProjection.append(" 		COALESCE(MMT1_.VALOR,0) ELSE ");
		}
		if(isOracle){
			sqlProjection.append(" 		(NVL(MMT1_.VALOR,0) * -1) END  ");	
		}else{
			sqlProjection.append(" 		(COALESCE(MMT1_.VALOR,0) * -1) END  ");
		}		
		sqlProjection.append("  	ELSE 0 END)) consumoMes");
//		 
		ProjectionList p = Projections.projectionList();

		p.add(Projections.sqlProjection(sqlProjection.toString(), 
				new String[]{RelatorioMensalMateriaisClassificacaoAbcVO.Fields.CONSUMO_MES.toString()}, 
				new Type[]{DoubleType.INSTANCE}));		

		Calendar mesCompetenciaAnterior = Calendar.getInstance();
		mesCompetenciaAnterior.setTime(mesCompetencia);
		mesCompetenciaAnterior.add(Calendar.MONTH, -2);
		Date dataAnterior = DateUtil.obterDataInicioCompetencia(mesCompetenciaAnterior.getTime());
		Date dataPosterior = DateUtil.obterDataInicioCompetencia(mesCompetencia);
		
		SimpleDateFormat in= new SimpleDateFormat("yyyy-MM-dd");    
		String dataA = in.format(dataAnterior); 
		String dataB = in.format(dataPosterior);
		
		sqlProjection = new StringBuilder();
		sqlProjection.append("round(");
		sqlProjection.append("(SELECT COALESCE(");
		if(!isOracle){ //Postgre nao tem round pra double precision 
			sqlProjection.append("cast(");
		}
		sqlProjection.append("sum(																			 ");
		sqlProjection.append(" 				case when MMT2.ind_estorno = 'N' then 								 	 ");
		sqlProjection.append(" 					case when TMV2.ind_operacao_basica = '" + operacaoBasicaDB + "' then ");
		if(isOracle){
			sqlProjection.append(" 						NVL(MMT2.valor,0) 										 ");
		}else{
			sqlProjection.append(" 						COALESCE(MMT2.valor,0) 										 ");
		}
		sqlProjection.append(" 		  			else 																 ");
		if(isOracle){
			sqlProjection.append("				 		NVL(MMT2.valor,0) *-1 									 ");
		}else{
			sqlProjection.append("				 		COALESCE(MMT2.valor,0) *-1 									 ");
		}
		sqlProjection.append(" 				end 																	 ");
		sqlProjection.append(" 				else 0 																	 ");
		sqlProjection.append(" 				end)- 																	 ");
		sqlProjection.append(" 		 sum(case when MMT2.ind_estorno = 'S' then 										 ");
		sqlProjection.append(" 				case when TMV2.ind_operacao_basica = '" + operacaoBasicaDB + "' then	 ");
		if(isOracle){
			sqlProjection.append("					NVL(MMT2.valor,0) 											 ");
		}else{
			sqlProjection.append("					COALESCE(MMT2.valor,0) 											 ");
		}
		sqlProjection.append(" 				else 																	 ");
		if(isOracle){
			sqlProjection.append(" 					NVL(MMT2.valor,0)*-1 											 ");
		}else{
			sqlProjection.append(" 					COALESCE(MMT2.valor,0)*-1 											 ");	
		}		
		sqlProjection.append(" 				end 																	 ");
		sqlProjection.append(" 			else 0 																		 ");
		sqlProjection.append(" 		end)");
		if(!isOracle){ //Postgre nao tem round pra double precision, fim do cast 
			sqlProjection.append(" as numeric)");
		}
		sqlProjection.append(",0) 																		 ");
		
		sqlProjection.append(" FROM AGH.SCE_MOVIMENTO_MATERIAIS MMT2, 												 ");     
		sqlProjection.append(" AGH.SCE_TIPO_MOVIMENTOS TMV2 														 ");
		
		if(isOracle){
			sqlProjection.append(" WHERE DT_COMPETENCIA BETWEEN to_date('" + dataA + "','yyyy-MM-dd') AND to_date('" + dataB + "','yyyy-MM-dd') ");

		}else{
			sqlProjection.append(" WHERE DT_COMPETENCIA BETWEEN date '" + dataA + "' AND date '" + dataB + "'");
		}

		sqlProjection.append(" AND TMV2.SEQ = MMT2.TMV_SEQ ");
		sqlProjection.append(" AND TMV2.COMPLEMENTO = MMT2.TMV_COMPLEMENTO ");
		sqlProjection.append(" AND TMV2.IND_MOVIMENTO_CONSUMO = 'S' ");
		sqlProjection.append(" AND MMT2.MAT_CODIGO = {alias}.CODIGO)/3,2) valorTrimestre ");
		
		p.add(Projections.sqlProjection(sqlProjection.toString(), 
				new String[]{RelatorioMensalMateriaisClassificacaoAbcVO.Fields.VALOR_TRIMESTRE.toString()}, 
				new Type[]{DoubleType.INSTANCE}));
	   	
		p.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.NOME.toString()),
				RelatorioMensalMateriaisClassificacaoAbcVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.CODIGO.toString()),
				RelatorioMensalMateriaisClassificacaoAbcVO.Fields.CODIGO.toString());
		p.add(Projections.groupProperty("MAT."+ ScoMaterial.Fields.GRUPO_MATERIAL.toString()),
				RelatorioMensalMateriaisClassificacaoAbcVO.Fields.GRUPO_MATERIAL.toString());
		p.add(Projections.groupProperty("MAT."+ ScoMaterial.Fields.IND_ESTOCAVEL.toString()),
				RelatorioMensalMateriaisClassificacaoAbcVO.Fields.ESTOCAVEL.toString());
		p.add(Projections.groupProperty("EGR."+ SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString()),
				RelatorioMensalMateriaisClassificacaoAbcVO.Fields.CLASSIFICACAO_ABC.toString());
		p.add(Projections.groupProperty("EGR."+ SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString()),
				RelatorioMensalMateriaisClassificacaoAbcVO.Fields.SUBCLASSIFICACAO_ABC.toString());
		criteria.setProjection(p);
		criteria.add(Restrictions.eq("TMV."+ SceTipoMovimento.Fields.IND_MOVIMENTO_CONSUMO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("MMT."+ SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), mesCompetencia));
		criteria.add(Restrictions.eqProperty("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), 
				"MMT." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString())); 
		criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), codigoFornecedor));
		
		criteria.addOrder(OrderBySql.sql("valorTrimestre desc"));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMensalMateriaisClassificacaoAbcVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorno campo mdAtual FUNCTION CF_CL_ATUAL
	 * 
	 * @return String
	 */
	public RelatorioMensalMateriaisClassificacaoAbcVO obterClassificacaoABCAtual(Date mesCompetencia, 
			Integer codigoMaterial, Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "EGR");		
		ProjectionList p = Projections.projectionList();
		int mesCompetenciaAdicionar = 1;
		Calendar mesCompetenciaCalendar = Calendar.getInstance(),
				 mesAtualCalendar = Calendar.getInstance();
		mesCompetenciaCalendar.setTime(mesCompetencia);
		
		if(mesAtualCalendar.get(Calendar.MONTH) == mesCompetenciaCalendar.get(Calendar.MONTH) && 
				mesAtualCalendar.get(Calendar.YEAR) == mesCompetenciaCalendar.get(Calendar.YEAR)){
			mesCompetenciaAdicionar = 0;
		}
		mesCompetenciaCalendar.add(Calendar.MONTH, mesCompetenciaAdicionar);

		p.add(Projections.property("EGR."+ SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString()), 
				RelatorioMensalMateriaisClassificacaoAbcVO.Fields.CLASSIFICACAO_ABC_CL_ATUAL.toString());
		p.add(Projections.property("EGR."+ SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString()),
				RelatorioMensalMateriaisClassificacaoAbcVO.Fields.SUBCLASSIFICACAO_ABC_CL_ATUAL.toString());
		criteria.setProjection(p);


		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(),
				DateUtil.obterDataInicioCompetencia(mesCompetenciaCalendar.getTime())));
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(),
				codigoMaterial));
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.FORNECEDOR.toString() + "." + ScoFornecedor.Fields.NUMERO.toString(), 
				numeroFornecedor));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMensalMateriaisClassificacaoAbcVO.class));
		return (RelatorioMensalMateriaisClassificacaoAbcVO) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Efetua a pesquias de Estoque Geral
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param dataCompetencia
	 * @param fornecedor
	 * @param matCodigo
	 * @param nomeMaterial
	 * @param gmtCodigo
	 * @param indEstocavel
	 * @param indGenerico
	 * @param umdCodigo
	 * @param clasABC
	 * @param subClasABC

	 * @return List<SceEstoqueGeral>
	 */
	public List<SceEstoqueGeral> pesquisarEstoqueGeral(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Date dataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia, Integer fornecedor, Integer matCodigo, String nomeMaterial, Integer gmtCodigo, 
			Boolean indEstocavel, Boolean indGenerico, String umdCodigo, DominioClassifABC clasABC, DominioClassifABC subClasABC){		
		DetachedCriteria criteria = obterCriteriaEstoqueGeral(dataCompetencia, comparacaoDataCompetencia, fornecedor, matCodigo, nomeMaterial, gmtCodigo,
				indEstocavel, indGenerico, umdCodigo, clasABC, subClasABC);
		criteria.addOrder(Order.desc("SEG." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Efetua o count da pesquisa de Estoque Geral
	 * @param dataCompetencia
	 * @param fornecedor
	 * @param matCodigo
	 * @param nomeMaterial
	 * @param gmtCodigo
	 * @param indEstocavel
	 * @param indGenerico
	 * @param umdCodigo
	 * @param clasABC
	 * @param subClasABC
	 * @return count da pesquisa
	 */
	public Long pesquisarEstoqueGeralCount(
			Date dataCompetencia,
			DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer fornecedor,
			Integer matCodigo,
			String nomeMaterial,
			Integer gmtCodigo,
			Boolean indEstocavel,
			Boolean indGenerico,
			String umdCodigo,
			DominioClassifABC clasABC,
			DominioClassifABC subClasABC) {
		DetachedCriteria criteria = obterCriteriaEstoqueGeral(
				dataCompetencia, comparacaoDataCompetencia, fornecedor, matCodigo, nomeMaterial, gmtCodigo, indEstocavel, indGenerico,
				umdCodigo, clasABC, subClasABC);
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Monta criteria base para pesquisa de estoque geral
	 * @param dtCompetencia
	 * @param frnNumero
	 * @param matCodigo
	 * @param nomeMaterial
	 * @param gmtCodigo
	 * @param indEstocavel
	 * @param indGenerico
	 * @param umdCodigo
	 * @param clasABC
	 * @param subClasABC
	 * @param qtdConsignada
	 * @param valorConsignado
	 * @return
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	private DetachedCriteria obterCriteriaEstoqueGeral(
			Date dtCompetencia,
			DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer frnNumero,
			Integer matCodigo,
			String nomeMaterial, 
			Integer gmtCodigo,
			Boolean indEstocavel,
			Boolean indGenerico,
			String umdCodigo,
			DominioClassifABC clasABC,
			DominioClassifABC subClasABC) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "SEG");
		
		criteria.createAlias("SEG." + SceEstoqueGeral.Fields.FORNECEDOR.toString(), "FRN",Criteria.INNER_JOIN);
		criteria.createAlias("SEG." + SceEstoqueGeral.Fields.MATERIAL.toString(), "MAT",Criteria.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GRP", Criteria.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UMD", Criteria.INNER_JOIN);
		
		//SCE.DT_COMPETENCIA,
		if(dtCompetencia != null){
			
			if(comparacaoDataCompetencia == null || DominioComparacaoDataCompetencia.IGUAL.equals(comparacaoDataCompetencia)){
				criteria.add(Restrictions.eq("SEG." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(),dtCompetencia));
			} else {
				criteria.add(Restrictions.gt("SEG." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(),dtCompetencia));
			}
		}
		
		//SCE.FRN_NUMERO,
		if(frnNumero != null){
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), 
					frnNumero));
		}
		//SCE.MAT_CODIGO
		if(matCodigo != null){
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		}
		if(nomeMaterial != null){
			criteria.add(Restrictions.like("MAT." + ScoMaterial.Fields.NOME.toString(),
					nomeMaterial, MatchMode.ANYWHERE).ignoreCase());
		}
		//MAT.GMT_CODIGO,
		if(gmtCodigo != null){
			criteria.add(Restrictions.eq("GRP." + ScoGrupoMaterial.Fields.CODIGO.toString(),
					gmtCodigo));
		}
		if(indEstocavel){
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), true));
		}
		if(indGenerico){
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GENERICO.toString(), 
					DominioSimNao.S));
		}
		if(umdCodigo != null){
			criteria.add(Restrictions.eq("UMD." + ScoUnidadeMedida.Fields.CODIGO.toString(), 
					umdCodigo));
		}
		if(clasABC != null && !"".equals(clasABC)){
			criteria.add(Restrictions.eq("SEG." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString(), 
					clasABC));
		}
		if(subClasABC != null && !"".equals(subClasABC)){
			criteria.add(Restrictions.eq("SEG." + SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString(), 
					subClasABC));
		}
		return criteria;
	}
	
	/**
	 * Método que realiza a pesquisa de competencias de estoque geral, por mes e ano.
	 * @param mes
	 * @param ano
	 * @return
	 */
	public List<EstoqueGeralVO> pesquisarDatasCompetenciasEstoqueGeralPorMesAno(Integer mes, Integer ano){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "EST");
		
		ProjectionList p = Projections.projectionList();
		
		p.add(Projections.min("EST."+ SceEstoqueGeral.Fields.FRN_NUMERO.toString()), 
				EstoqueGeralVO.Fields.FRN_NUMERO.toString());
		p.add(Projections.min("EST."+ SceEstoqueGeral.Fields.MAT_CODIGO.toString()), 
				EstoqueGeralVO.Fields.MAT_CODIGO.toString());
		p.add(Projections.groupProperty("EST."+ SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()), 
				EstoqueGeralVO.Fields.DT_COMPETENCIA.toString());		
		criteria.setProjection(p);
		
		if(mes!=null){
			criteria.add(Restrictions.sqlRestriction("extract(month from dt_competencia)= " + mes));
		}
		
		if(ano!=null){
			criteria.add(Restrictions.sqlRestriction("extract(year from dt_competencia)= " + ano));
		}
		criteria.addOrder(Order.desc("EST."+ SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(EstoqueGeralVO.class));
		
		return executeCriteria(criteria, 0, 100, null, false);
	}

	
	public BigDecimal pesquisarSomaValorEstocGeral(Date dataCompetencia, Integer gmtCodigo , Integer fornecedor) throws BaseException {
		
	
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class,"egr");
		
		criteria.setProjection(Projections.sum("egr."+SceEstoqueGeral.Fields.VALOR.toString()));
		
		criteria.createAlias("egr." + SceEstoqueGeral.Fields.MATERIAL, "MAT", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("egr."+SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));
		if(gmtCodigo != null){
			criteria.add(Restrictions.eq("egr."+SceEstoqueGeral.Fields.FRN_NUMERO.toString(), fornecedor));
			criteria.add(Restrictions.eq("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), gmtCodigo));
		}
		
		Double retorno = (Double)executeCriteriaUniqueResult(criteria);
		
		if(retorno == null){
			retorno = new Double(0);
		}
		
		return BigDecimal.valueOf(retorno);
	}

	public Integer obterSaldoTotalTerceiros(Integer codigoMaterial, Integer frnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setProjection(Projections.projectionList().add(
				Projections.sqlProjection("sum(coalesce({alias}.QTDE_DISPONIVEL,0) + coalesce({alias}.QTDE_BLOQUEADA,0)) saldo_geral", 
						new String[]{"saldo_geral"}, new Type[] {IntegerType.INSTANCE})));

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.ne(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), frnNumero));

		Object custoMedioPonderado = executeCriteriaUniqueResult(criteria);

		if(custoMedioPonderado != null){
			return (Integer) custoMedioPonderado;
		}

		return Integer.valueOf(0);
	}
	
	public Integer obterSaldoTotalHospital(Integer codigoMaterial, Integer frnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setProjection(Projections.projectionList().add(
				Projections.sqlProjection("sum(coalesce({alias}.QTDE_DISPONIVEL,0) + coalesce({alias}.QTDE_BLOQUEADA,0)) saldo_geral", 
						new String[]{"saldo_geral"}, new Type[] {IntegerType.INSTANCE})));

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), frnNumero));

		Object custoMedioPonderado = executeCriteriaUniqueResult(criteria);

		if(custoMedioPonderado != null){
			return (Integer) custoMedioPonderado;
		}

		return Integer.valueOf(0);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PosicaoFinalEstoqueVO> buscaDadosPosicaoFinalEstoque(Date dtCompetencia, Integer codigoGrupo, String estocavel, String orderBy, Integer fornecedor, String siglaTipoUsoMdto, Short almoxSeq) throws BaseException{

		Calendar mesCompetenciaAnterior = Calendar.getInstance();
		mesCompetenciaAnterior.setTime(dtCompetencia);
		mesCompetenciaAnterior.add(Calendar.MONTH, -1);
		String dataMesAnterio = new SimpleDateFormat("yyyy-MM-dd").format(mesCompetenciaAnterior.getTime());

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "egr");
		criteria.createAlias(SceEstoqueGeral.Fields.MATERIAL.toString(),"mat", Criteria.INNER_JOIN);
		criteria.createAlias("mat."+ScoMaterial.Fields.MEDICAMENTO.toString(),"med", Criteria.LEFT_JOIN);
		
		if(almoxSeq != null){			
			criteria.createAlias("mat."+ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(),"eal", Criteria.INNER_JOIN);
		}
		ProjectionList p = Projections.projectionList();
		
		if(almoxSeq != null){
			String sqlProjection1 = "(	coalesce(eal3_.qtde_disponivel,0)+" +
									"	coalesce(eal3_.qtde_bloqueada,0)        +" +
									"	coalesce(eal3_.qtde_bloq_dispensacao,0) +" +
									"	coalesce(eal3_.qtde_bloq_consumo,0)     +" +
									"	coalesce(eal3_.qtde_bloq_entr_transf,0)) as quantidade";
	
			p.add(Projections.sqlProjection(sqlProjection1, new String[]{"quantidade"}, new Type[]{IntegerType.INSTANCE}));
		}else{
			p.add(Projections.property(SceEstoqueGeral.Fields.QUANTIDADE.toString()),"quantidade");
		}

		p.add(Projections.property(SceEstoqueGeral.Fields.CUSTO_MEDIO_PONDERADO.toString()),"custoPonderado");
		p.add(Projections.property(SceEstoqueGeral.Fields.VALOR.toString()),"valor");

		if(almoxSeq != null){
			String sqlProjection2 = "((	coalesce(eal3_.qtde_disponivel,0)+" +
									"	coalesce(eal3_.qtde_bloqueada,0)        +" +
									"	coalesce(eal3_.qtde_bloq_dispensacao,0) +" +
									"	coalesce(eal3_.qtde_bloq_consumo,0)     +" +
									"	coalesce(eal3_.qtde_bloq_entr_transf,0)) * {alias}.custo_medio_ponderado) as VALOR_ATU";
			
			p.add(Projections.sqlProjection(sqlProjection2, new String[]{"VALOR_ATU"}, new Type[]{DoubleType.INSTANCE}));
		}else{
			p.add(Projections.sqlProjection("coalesce({alias}.valor,0) as VALOR_ATU",new String[]{"VALOR_ATU"},new Type[]{DoubleType.INSTANCE}));
		}
		p.add(Projections.property("mat."+ScoMaterial.Fields.CODIGO.toString()),"codigoMaterial");
		p.add(Projections.property("mat."+ScoMaterial.Fields.COD_CATMAT.toString()),"codigoCatmat");
		p.add(Projections.property("mat."+ScoMaterial.Fields.NOME.toString()),"nomeMaterial");
		p.add(Projections.property("mat."+ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()),"unidade");
		p.add(Projections.property("mat."+ScoMaterial.Fields.IND_ESTOCAVEL.toString()),"estocavel");
		
		String sqlProjection3 = "coalesce((select 	coalesce(valor,0) " +
							   "from 	agh.sce_estq_gerais egr1 " +
							   "where 	egr1.mat_codigo = mat1_.CODIGO " +
							   "and		egr1.dt_competencia = '" + dataMesAnterio + "' " +
							   "and 	egr1.frn_numero = "+fornecedor+"),0) as VALOR_ANT";

		p.add(Projections.sqlProjection(sqlProjection3,new String[]{"VALOR_ANT"},new Type[]{DoubleType.INSTANCE}));

		criteria.setProjection(p);

		if(almoxSeq != null){
			criteria.add(Restrictions.sqlRestriction("{alias}.mat_codigo = eal3_.mat_codigo"));
			criteria.add(Restrictions.sqlRestriction("{alias}.frn_numero = eal3_.frn_numero"));		 						
		}

		if(codigoGrupo != null){
			criteria.add(Restrictions.eq("mat."+ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), codigoGrupo));
		}

		if(!estocavel.equals("T")){
			criteria.add(Restrictions.eq("mat."+ScoMaterial.Fields.IND_ESTOCAVEL.toString(), estocavel.equals("S")));
		}

		if(almoxSeq != null){
			criteria.add(Restrictions.eq("eal."+SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), fornecedor));
		}else{
			criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.FRN_NUMERO.toString(), fornecedor));	
		}

		if(dtCompetencia!=null){
			Calendar dataCom = Calendar.getInstance();
			dataCom.setTime(dtCompetencia);
			dataCom.add(Calendar.HOUR, 0);
			dataCom.add(Calendar.MINUTE, 0);
			dataCom.add(Calendar.SECOND, 0);
			dataCom.add(Calendar.MILLISECOND, 0);

			criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dataCom.getTime()));
		}
		
		if(siglaTipoUsoMdto != null){
			criteria.add(Restrictions.eq("med." +AfaMedicamento.Fields.TUM_SIGLA.toString(), siglaTipoUsoMdto));
		}
		
		if(almoxSeq !=null){
			criteria.add(Restrictions.eq("eal." +SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeq));
		}
		
		// #39411 - Relatório de Posição Final de Estoque exibe itens inativos
		criteria.add(Restrictions.eq("mat."+ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(OrdenacaoPosicaoFinalEstoque.C.toString().equals(orderBy)){
			criteria.addOrder(Order.asc("mat."+ScoMaterial.Fields.CODIGO.toString()));	
		}else if(OrdenacaoPosicaoFinalEstoque.N.toString().equals(orderBy)){
			criteria.addOrder(Order.asc("mat."+ScoMaterial.Fields.NOME.toString()));	
		}else if(OrdenacaoPosicaoFinalEstoque.V.toString().equals(orderBy)){
			criteria.addOrder(Order.desc(SceEstoqueGeral.Fields.VALOR.toString()));	
		}
		
		List<PosicaoFinalEstoqueVO> lstPos = new ArrayList<PosicaoFinalEstoqueVO>();
		 
		List<Object[]> lista = executeCriteria(criteria);
		if(lista != null && lista.size()>0) {
			for(Object[] obj : lista) {
			
				PosicaoFinalEstoqueVO valores = new PosicaoFinalEstoqueVO();
				
				valores.setQuantidade(Integer.valueOf(obj[0].toString()));
				
				final Double valorMedioPonderado = new Double(obj[1].toString());
				valores.setValorMedioPonderado(valorMedioPonderado);
				
				NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
				format.setMaximumFractionDigits(4);
				format.setRoundingMode(RoundingMode.DOWN);
				
				valores.setValorMedioPonderadoFormatado(format.format(valorMedioPonderado));

				valores.setValorAtual(NumberUtil.truncate(new Double(obj[3].toString()), 2));
				valores.setCodMaterial(Integer.valueOf(obj[4].toString()));
				if (obj[5] != null) {
					valores.setCodCatmat(Integer.valueOf(obj[5].toString()));
				}
				valores.setNomeMaterial(obj[6].toString());
				valores.setUnidadeMedida(obj[7].toString());
				Boolean indEstocavel = Boolean.TRUE.equals(obj[8]);
				valores.setIndEstocavel(DominioSimNao.getInstance(indEstocavel).toString());
				valores.setValorAnterior(new Double(obj[9].toString()));
				valores.setVariacao(calculaPorcentagem(valores.getValorAtual(), valores.getValorAnterior()));
				lstPos.add(valores);
			}
		}

		return lstPos;
	}
	
	private Double calculaPorcentagem(Double saldoAtual, Double saldoAnterior){
		Double valorRetorno = 0.0;
		if(saldoAtual.doubleValue() > 0 && saldoAnterior.doubleValue() > 0){
			valorRetorno = ((saldoAtual / saldoAnterior) -1 )*100;
		}else{
			if(saldoAtual.doubleValue() > 0 && saldoAnterior.doubleValue() == 0){
				valorRetorno = 100.00;
			}else{
				if(saldoAtual.doubleValue() == 0 && saldoAnterior.doubleValue() > 0){
					valorRetorno = -100.00;
				}
			}
		}
		return valorRetorno;
	}
	
	/**
	 * Obtém a quantidade de registros
	 * @return
	 */
	public Long obterQuantidadeRegistrosPorDataCompetencia(Date dataCompetencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);
		criteria.setProjection(Projections.count(SceEstoqueGeral.Fields.MAT_CODIGO.toString()));
		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));
		final Long max = (Long) executeCriteriaUniqueResult(criteria);
		if (max == null) {
			return 0l;
		}
		return max;
	}
	
	/**
	 * Persiste Estoque Geral para Fechamento de Estoque Mensal (etapa 5)
	 * Obs. SQL NATIVO devido aos problemas de performance
	 * @param dataCompetencia
	 * @param servidorLogado
	 * @return quantidade de registros persistidos na operação
	 */
	public Integer persistirEstoqueGeralFechamentoEstoqueMensal(Date dataCompetencia){

		// Verifica o tipo de banco
		@SuppressWarnings("deprecation")
		final boolean isOracle = isOracle();

		// SQL Insert
		StringBuilder sqlInsert = new StringBuilder(200);
		
		sqlInsert.append("INSERT INTO AGH.SCE_ESTQ_GERAIS(");
		sqlInsert.append("mat_codigo,");
		sqlInsert.append("dt_competencia,"); // Parâmetro
		sqlInsert.append("custo_medio_ponderado,");
		sqlInsert.append("residuo,");
		sqlInsert.append("valor,");
		sqlInsert.append("umd_codigo,");
		sqlInsert.append("classif_abc,");
		sqlInsert.append("sub_classif_abc,");
		sqlInsert.append("qtde,");
		sqlInsert.append("frn_numero,");
		sqlInsert.append("qtde_consignada,");
		sqlInsert.append("valor_consignado)");
		
		// SQL Select
		StringBuilder sqlSelect = new StringBuilder(200);
		
		sqlSelect.append("SELECT mat_codigo,");
			Calendar calendarDataCompetenciaAlterada = Calendar.getInstance(); // Acrescenta 1 mês a data de competência atual
			calendarDataCompetenciaAlterada.setTime(dataCompetencia); // Seta data de competência atual
			calendarDataCompetenciaAlterada.add(Calendar.MONTH, 1); // ADD_MONTHS(dt_competencia, 1)
			sqlSelect.append(SceHistoricoEstoqueAlmoxarifadoDAO.getSqlDataCompetenciaFechamentoEstoque(isOracle, calendarDataCompetenciaAlterada.getTime()) + SceHistoricoEstoqueAlmoxarifadoDAO.VIRGULA); // V_DT_COMPETENCIA
		sqlSelect.append("custo_medio_ponderado,");
		sqlSelect.append("residuo,");
		sqlSelect.append("valor,");
		sqlSelect.append("umd_codigo,");
		sqlSelect.append("classif_abc,");
		sqlSelect.append("sub_classif_abc,");
		sqlSelect.append("qtde,");
		sqlSelect.append("frn_numero,");
		sqlSelect.append("qtde_consignada,");
		sqlSelect.append("valor_consignado");
	 	sqlSelect.append(" FROM AGH.SCE_ESTQ_GERAIS ");
	 		sqlSelect.append(" WHERE dt_competencia = " ).append( SceHistoricoEstoqueAlmoxarifadoDAO.getSqlDataCompetenciaFechamentoEstoque(isOracle, dataCompetencia)); // V_DT_COMPETENCIA
	 		
	 	// Cria SQL nativa: SQL Insert + SQL Select
		SQLQuery query = createSQLQuery(sqlInsert.toString() + sqlSelect.toString());

		// Executa a inclusão e retorna a quantidade de registros gravados
		Integer retorno = query.executeUpdate();
		this.flush();
		return retorno;
		
	}
	
	public Date obterMaxDtCompetencia(){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);
		criteria.setProjection(Projections.max(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Lista o estoque geral pelo<br>
	 * material e data competencia
	 * 
	 * @param material
	 * @param dataCompetencia
	 * @return
	 */
	public List<SceEstoqueGeral> pesquisarEstoqueGeralPorMaterialDataCompetencia(ScoMaterial material,
			Date dataCompetencia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.MAT_CODIGO.toString(), material.getCodigo()));

		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), DateUtil.truncaData(dataCompetencia)));
		
		return this.executeCriteria(criteria);
	}
	
	
	/**
	 * Atualiza geral estoque por material e data de competência do mês seguinte ao fechamento de estoque
	 * @param dataCompetencia
	 * @param codigoMaterial
	 * @param classificacaoAbc
	 * @param subClassificacaoAbc
	 * @return
	 */
	public Integer atualizarEstoqueGeralFechamentoMensalEstoqueClassificacaoAbc(final Date dataCompetencia, final Integer codigoMaterial, final DominioClassifABC classificacaoAbc, final DominioClassifABC subClassificacaoAbc){
		
		// Verifica o tipo de banco
		@SuppressWarnings("deprecation")
		final boolean isOracle = isOracle();
		
		// SQL Update
		final StringBuilder sbAtualizar = new StringBuilder(200);
		
		sbAtualizar.append(" UPDATE AGH.SCE_ESTQ_GERAIS");
		sbAtualizar.append(" SET CLASSIF_ABC = '" ).append( classificacaoAbc ).append( "', "); // p_class
		sbAtualizar.append(" SUB_CLASSIF_ABC = '" ).append( subClassificacaoAbc ).append('\''); //p_sub_class
		sbAtualizar.append(" WHERE mat_codigo = " ).append( codigoMaterial);  // p_mat
		
		// Acrescenta um mês...
		Calendar calendarDataCompetenciaAlterada = Calendar.getInstance();
		calendarDataCompetenciaAlterada.setTime(dataCompetencia);
		calendarDataCompetenciaAlterada.add(Calendar.MONTH, 1);
		
		sbAtualizar.append(" AND dt_competencia = " ).append(  SceHistoricoEstoqueAlmoxarifadoDAO.getSqlDataCompetenciaFechamentoEstoque(isOracle, calendarDataCompetenciaAlterada.getTime()));
		
	 	// Cria SQL nativa
		SQLQuery query = this.createSQLQuery(sbAtualizar.toString());
		
		// Executa a inclusão e retorna a quantidade de registros gravados
		Integer retorno = query.executeUpdate();
		this.flush();
		
		return retorno;
		
	}
	
    public List<ItensAutFornUpdateSCContrVO> pesquisarFaseSolicitacaoItemAF(ScoItemAutorizacaoFornId itemAutorizacaoFornId, Date pdataCompetencia , Integer pFornecedorPadrao) {
		
	    DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "EST_GERAL");	    		
	    
	    criteria.createAlias("EST_GERAL." + SceEstoqueGeral.Fields.MATERIAL.toString(), "MAT");
	    criteria.createAlias("MAT." + ScoMaterial.Fields.SOLICITACAO_COMPRA.toString(), "SC");
	    criteria.createAlias("SC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC");	    
	    criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(),"GRP_MAT");
	    criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(),"EST_ALMOX", Criteria.LEFT_JOIN);

	    criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), itemAutorizacaoFornId.getAfnNumero()));
	    criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), itemAutorizacaoFornId.getNumero()));
		criteria.add(Restrictions.eq("EST_GERAL." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), DateUtil.truncaData(pdataCompetencia)));
		criteria.add(Restrictions.eq("EST_ALMOX." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), pFornecedorPadrao));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SC."+ ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), ItensAutFornUpdateSCContrVO.Fields.SLC_NUMERO_SC.toString())
				.add(Projections.property("SC."+ ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()), ItensAutFornUpdateSCContrVO.Fields.SLC_CODIGO_MATERIAL.toString())
				.add(Projections.property("SC."+ ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString()), ItensAutFornUpdateSCContrVO.Fields.SLC_CCT_CODIGO.toString())
				.add(Projections.property("EST_GERAL."+ SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString()), ItensAutFornUpdateSCContrVO.Fields.EGR_CLASSIF_ABC.toString())
				.add(Projections.property("EST_ALMOX."+ SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()), ItensAutFornUpdateSCContrVO.Fields.EST_AL_ALMOX_SEQ.toString())
				.add(Projections.property("EST_ALMOX."+ SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()), ItensAutFornUpdateSCContrVO.Fields.EST_AL_ALMOX_FORNECEDOR_NUMERO.toString())
				.add(Projections.property("EST_ALMOX."+ SceEstoqueAlmoxarifado.Fields.SEQ.toString()), ItensAutFornUpdateSCContrVO.Fields.EST_AL_SEQ.toString())				
				.add(Projections.property("MAT."+ ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()), ItensAutFornUpdateSCContrVO.Fields.MAT_ALMOX_SEQ.toString())
				.add(Projections.property("GRP_MAT."+ ScoGrupoMaterial.Fields.CODIGO.toString()), ItensAutFornUpdateSCContrVO.Fields.MAT_GRUPO_CODIGO.toString()));

       criteria.setResultTransformer(Transformers.aliasToBean(ItensAutFornUpdateSCContrVO.class));
		
       return this.executeCriteria(criteria);
       
		
	}	
    
    public Integer obterQtdeConsignadaEstoqueGeralItemAF(Integer afnNumero, Integer numero) {
    	
    	 DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "EST_GERAL");	
    	 
    	 criteria.createAlias("EST_GERAL." + SceEstoqueGeral.Fields.MATERIAL.toString(), "MAT");
    	 criteria.createAlias("MAT." + ScoMaterial.Fields.SOLICITACAO_COMPRA.toString(), "SC");
    	 criteria.createAlias("SC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC");
    	 criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "ITEM_AF");
    	 criteria.createAlias("ITEM_AF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF");
    	 
    	criteria.add(Restrictions.eqProperty("EST_GERAL." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(), "AF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()));
    		
    	 criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
 	     criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), numero));
 	     
 	     criteria.addOrder(Order.desc("EST_GERAL." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()));
 	     
 	     List<SceEstoqueGeral> listaEstoqueGerais = this.executeCriteria(criteria, 0, 1, null, false);
 	     Integer qtdeConsignada = -1;
 	     
 	     if (listaEstoqueGerais != null){
 	    	 if (listaEstoqueGerais.get(0) != null){
 	    		 if (listaEstoqueGerais.get(0).getQtdeConsignada() != null){
 	    			qtdeConsignada = listaEstoqueGerais.get(0).getQtdeConsignada();
 	    		 }
 	    		 else {
 	    			qtdeConsignada = 0;
 	    		 }
 	    	 }
 	     }
 	     
 	     return qtdeConsignada;
    	
    }
    
    public Boolean isItemAFCurvaA(Integer afnNumero, Integer fornecedorPadrao) {
    	
    	DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "EGR");

		DetachedCriteria criteriaSolCompra = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		criteriaSolCompra.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC",JoinType.INNER_JOIN);
		criteriaSolCompra.add(Restrictions.eqProperty("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "EGR." + SceEstoqueGeral.Fields.MATERIAL.toString()));
		criteriaSolCompra.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteriaSolCompra.setProjection(Projections.projectionList().add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString())));
		criteria.add(Subqueries.exists(criteriaSolCompra));
	
		Calendar mesAtual = Calendar.getInstance();
		mesAtual.set(Calendar.DAY_OF_MONTH, 1);
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), DateUtil.truncaData(mesAtual.getTime())));
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(), fornecedorPadrao));
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString(), DominioClassifABC.A));
		
		return executeCriteriaCount(criteria) > 0;
    }
    
    
    /**
	 * Classificação ABC do material
	 * 
	 * C2 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param matCodigo
	 * @param numeroFornecedor
	 * @param dataCompetencia
	 * @return
	 */
	public DominioClassifABC obterClassificacaoABCMaterial(Integer matCodigo, Integer numeroFornecedor, Date dataCompetencia) {

		if (matCodigo == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "EGR");

		criteria.createAlias("EGR." + SceEstoqueGeral.Fields.MATERIAL.toString(), "MAT");
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (dataCompetencia != null) {
			criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));
		}

		if (numeroFornecedor != null) {
			criteria.createAlias("EGR." + SceEstoqueGeral.Fields.FORNECEDOR.toString(), "FRN");
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		}

		criteria.setProjection(Projections.property(("EGR." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString())));

		List<DominioClassifABC> result = executeCriteria(criteria, 0, 1, null, true);

		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}

		return null;
	}
    	
	
	public List<SceEstoqueGeral> listarEstoqueMaterial(Integer codigoMaterial){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class);
		criteria.add(Restrictions.eq(SceEstoqueGeral.Fields.MATERIAL +"."+ ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		return this.executeCriteria(criteria);
	}

    public List<BigDecimal> obterCustoMedioPonderadoDoMaterialEstoqueGeral(Integer matCodigo, Date dtCompetencia, Integer frnNumero) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "egr");

        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("egr." + SceEstoqueGeral.Fields.CUSTO_MEDIO_PONDERADO.toString())));

        criteria.add(Restrictions.eq("egr." + SceEstoqueGeral.Fields.MAT_CODIGO.toString(), matCodigo));
        criteria.add(Restrictions.eq("egr." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dtCompetencia));
        criteria.add(Restrictions.eq("egr." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(), frnNumero));

        return executeCriteria(criteria);
    }

}