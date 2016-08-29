package br.gov.mec.aghu.blococirurgico.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.blococirurgico.vo.MaterialPorCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioNotasDeConsumoDaSalaMateriaisVO;
import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioRegistroDaNotaDeSalaMateriaisVO;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcMaterialImpNotaSalaUn;
import br.gov.mec.aghu.model.MbcMaterialPorCirurgia;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;



public class MbcMaterialPorCirurgiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMaterialPorCirurgia> {
	
	private static final Log LOG = LogFactory.getLog(MbcMaterialPorCirurgiaDAO.class);

	@SuppressWarnings("ucd")
	public enum TipoOperacao {
		I,
		A,
		E
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1299830086065977385L;

	
	/**
	 * Pesquisa materiais consumidos por cirurgia
	 * @param crgSeq
	 * @return
	 */
	public List<MaterialPorCirurgiaVO> pesquisarMaterialConsumidoPorCirurgia(Integer crgSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMaterialPorCirurgia.class, "MPC");
		
		criteria.createAlias("MPC.".concat(MbcMaterialPorCirurgia.Fields.SCO_MATERIAL.toString()), "MAT");		
		criteria.createAlias("MPC.".concat(MbcMaterialPorCirurgia.Fields.MBC_CIRURGIAS.toString()), "CRG");
		criteria.createAlias("MPC.".concat(MbcMaterialPorCirurgia.Fields.SCO_UNIDADE_MEDIDA.toString()), "UND");
		criteria.createAlias("MAT.".concat(ScoMaterial.Fields.UNIDADE_MEDIDA.toString()), "MAT_UND");
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("MPC." + MbcMaterialPorCirurgia.Fields.CRG_SEQ.toString())
						, MaterialPorCirurgiaVO.Fields.CRG_SEQ_ID.toString())
				.add(Projections.property("MPC." + MbcMaterialPorCirurgia.Fields.CRG_SEQ.toString())
						, MaterialPorCirurgiaVO.Fields.CRG_SEQ.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString())
						, MaterialPorCirurgiaVO.Fields.MAT_CODIGO.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString())
						, MaterialPorCirurgiaVO.Fields.MAT_NOME.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())
						, MaterialPorCirurgiaVO.Fields.GMT_CODIGO.toString())
				.add(Projections.property("MAT_UND." + ScoUnidadeMedida.Fields.DESCRICAO.toString())
						, MaterialPorCirurgiaVO.Fields.UMD_MAT.toString())
				.add(Projections.property("UND." + ScoUnidadeMedida.Fields.CODIGO.toString())
						, MaterialPorCirurgiaVO.Fields.UMD_CODIGO.toString())
				.add(Projections.property("UND." + ScoUnidadeMedida.Fields.DESCRICAO.toString())
						, MaterialPorCirurgiaVO.Fields.UMD_CONS.toString())
				.add(Projections.property("MPC." + MbcMaterialPorCirurgia.Fields.QUANTIDADE.toString())
						, MaterialPorCirurgiaVO.Fields.QUANTIDADE.toString())));
		
		criteria.add(Restrictions.eq("CRG.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));
		
		criteria.addOrder(Order.asc("MAT.".concat(ScoMaterial.Fields.CODIGO.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MaterialPorCirurgiaVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * ORADB: MBCK_MCI_RN.RN_MCIP_VER_UNID_MAT
	 * 
	 * @param crgSeq
	 * @param matCodigo
	 * @param qtd
	 * @param tipoOperacao
	 * @throws ApplicationBusinessException
	 */
	public void atualizarMateriaisPorCirurgiaCallableStatement(final Integer crgSeq, final Integer matCodigo,
			final Double qtd, final TipoOperacao tipoOperacao) throws ApplicationBusinessException {
		
		if (isOracle()) {
			final String nomeObjeto = ObjetosBancoOracleEnum.FFC_INTERFACE_MBC.toString();
			try {
				this.doWork(new Work() {
					@Override
					public void execute(Connection connection) throws SQLException {
						CallableStatement cs = null;
						
						try {
							cs = connection.prepareCall("{call " + nomeObjeto + "(?,?,?,?)}");
							CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, crgSeq);
							CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, matCodigo);
							CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, new BigDecimal(qtd));
							CoreUtil.configurarParametroCallableStatement(cs, 4, Types.VARCHAR, tipoOperacao.toString());
							
							cs.execute();
						} catch (Exception e) {
							LOG.error(e.getMessage(),e);
						}finally {
							if (cs != null) {
								cs.close();
							}
						}
					}
				});
			}  catch (Exception e) {
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(crgSeq, 
						matCodigo, qtd, tipoOperacao.toString());
				this.LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores));
				throw new ApplicationBusinessException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, e, false, valores));
			}
		}
	}
	
	public List<MbcMaterialImpNotaSalaUn> pesquisarMateriaisComSubQueryMbcUnidadenotaSala(
			MbcCirurgias cirurgia, DominioIndRespProc dominioIndRespProc,
			DominioSituacao dominioSituacao, DominioSimNao indicadorPrincipal, AghParametros pGrMatOrtProt)
			throws ApplicationBusinessException {
		//SUBQUERY
		final DetachedCriteria subQuery = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOA");
		subQuery.setProjection(Projections.property(MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subQuery.add(Property.forName("NOA." + MbcUnidadeNotaSala.Fields.SEQP.toString()).eqProperty("MNS_01." + MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS.toString().concat("."+MbcUnidadeNotaSala.Fields.SEQP.toString())));
		subQuery.createAlias("NOA.".concat(MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString()), "EPR", DetachedCriteria.INNER_JOIN);	
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.SITUACAO.toString()), DominioSituacao.A));
		
		//SUBQUERY DA SUBQUERY
		final DetachedCriteria subQueryPPC = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "PPC");
		subQueryPPC.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString()), cirurgia.getSeq()));
		subQueryPPC.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()), dominioIndRespProc));
		subQueryPPC.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.SITUACAO.toString()), dominioSituacao));
		subQueryPPC.add(Restrictions.eq("PPC.".concat(MbcProcEspPorCirurgias.Fields.IND_PRINCIPAL.toString()), "S".equals(indicadorPrincipal.toString()) ? Boolean.TRUE : Boolean.FALSE));
		//Relacionando MbcUnidadeNotaSala com a MbcProcEspPorCirurgias pela MbcProcedimentoCirurgicos
		subQueryPPC.setProjection(Projections.property("PPC." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO.toString()+"." 
				+ MbcProcedimentoCirurgicos.Fields.SEQ.toString()));
		subQueryPPC.add(Property.forName("PPC." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO_SEQ.toString())
				.eqProperty("NOA."+ MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString()));

		subQuery.add(Subqueries.exists(subQueryPPC));
		return executeCriteria(this.getCriteriaScoMaterial(cirurgia, pGrMatOrtProt).add(Subqueries.exists(subQuery)));
	}
	
	public List<MbcMaterialImpNotaSalaUn> pesquisarMateriaisComSubQueryMbcUnidadenotaSala(
			MbcCirurgias cirurgia, DominioSituacao dominioSituacao, AghParametros pGrMatOrtProt)
			throws ApplicationBusinessException {
		
		DetachedCriteria principal = this.getCriteriaScoMaterial(cirurgia, pGrMatOrtProt); 
		
		//SUBQUERY
		final DetachedCriteria subQuery = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOA");
		
		subQuery.setProjection(Projections.property(MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subQuery.add(Property.forName("NOA." + MbcUnidadeNotaSala.Fields.SEQP.toString()).eqProperty("MNS_01." + MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS.toString().concat("."+MbcUnidadeNotaSala.Fields.SEQP.toString())));

		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.UNFSEQ.toString()), cirurgia.getUnidadeFuncional().getSeq()));
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.SITUACAO.toString()), dominioSituacao));
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES.toString()+"."
		.concat(AghEspecialidades.Fields.SEQ.toString())), cirurgia.getEspecialidade().getSeq()));
		
		principal.add(Subqueries.exists(subQuery));
		return executeCriteria(principal);
	}

	public List<MbcMaterialImpNotaSalaUn> pesquisarMateriaisComSubQueryMbcUnidadenotaSala(
			MbcCirurgias cirurgia, AghParametros pGrMatOrtProt) throws ApplicationBusinessException {
		//SUBQUERY
		final DetachedCriteria subQuery = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOA");
		subQuery.setProjection(Projections.property(MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subQuery.add(Property.forName("NOA." + MbcUnidadeNotaSala.Fields.SEQP.toString()).eqProperty("MNS_01." + MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS.toString().concat("."+MbcUnidadeNotaSala.Fields.SEQP.toString())));
		
		subQuery.add(Restrictions.isNull("NOA.".concat(MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString())));
		subQuery.add(Restrictions.isNull("NOA.".concat(MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES.toString())));
		subQuery.add(Restrictions.eq("NOA.".concat(MbcUnidadeNotaSala.Fields.UNFSEQ.toString()), cirurgia.getUnidadeFuncional().getSeq()));
		
		return executeCriteria(this.getCriteriaScoMaterial(cirurgia, pGrMatOrtProt).add(Subqueries.exists(subQuery)));
					
	}
	
	private DetachedCriteria getCriteriaScoMaterial(MbcCirurgias cirurgia, AghParametros pGrMatOrtProt){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMaterialImpNotaSalaUn.class, "MNS_01");
		
		criteria.createAlias("MNS_01.".concat(MbcMaterialImpNotaSalaUn.Fields.SCO_MATERIAL.toString()), "MAT");
		
		criteria.createAlias("MNS_01.".concat(MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS.toString()), "UNS");		
		
		criteria.add(Restrictions.eq("UNS.".concat(MbcUnidadeNotaSala.Fields.UNFSEQ.toString()), cirurgia.getUnidadeFuncional().getSeq()));
		
		criteria.add(Restrictions.not(Restrictions.eq("MAT.".concat(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()),
				pGrMatOrtProt.getVlrNumerico().intValue())));
		
		criteria.addOrder(Order.asc("MNS_01.".concat(MbcMaterialImpNotaSalaUn.Fields.ORDEM_IMP.toString())));
		
		return criteria;
	}
	
public List<SubRelatorioRegistroDaNotaDeSalaMateriaisVO> pesquisarMateriaisPorCirurgiaNotaDeSala(final Integer crgSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMaterialPorCirurgia.class, "MCI");
		
		criteria.createAlias("MCI.".concat(MbcMaterialPorCirurgia.Fields.SCO_MATERIAL.toString()), "MAT", DetachedCriteria.LEFT_JOIN);
		criteria.createAlias("MCI.".concat(MbcMaterialPorCirurgia.Fields.MBC_CIRURGIAS.toString()), "CRG");
		criteria.createAlias("MAT.".concat(ScoMaterial.Fields.ESTOQUE_GERAL.toString()), "SCE");
		criteria.createAlias("CRG.".concat(MbcCirurgias.Fields.PROF_CIRURGIAS.toString()), "PCG");
		criteria.createAlias("PCG.".concat(MbcProfCirurgias.Fields.SERVIDOR_PUC.toString()), "SER");
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES");
		
		criteria.setProjection(Projections.projectionList()
				
				.add(Projections.distinct(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString())), 
						SubRelatorioRegistroDaNotaDeSalaMateriaisVO.Fields.MATERIAL_CODIGO.toString())
						
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), 
						SubRelatorioRegistroDaNotaDeSalaMateriaisVO.Fields.MATERIAL_DESCRICAO.toString())
								
				.add(Projections.property("MCI." + MbcMaterialPorCirurgia.Fields.QUANTIDADE.toString()), 
						SubRelatorioRegistroDaNotaDeSalaMateriaisVO.Fields.QUANTIDADE.toString())
										
				.add(Projections.property("MCI." + MbcMaterialPorCirurgia.Fields.SCO_UNIDADE_MEDIDA.toString()
						.concat("."+ScoUnidadeMedida.Fields.CODIGO.toString())), 
						SubRelatorioRegistroDaNotaDeSalaMateriaisVO.Fields.UNIDADE.toString())
												
				.add(Projections.property("SCE." + SceEstoqueGeral.Fields.CUSTO_MEDIO_PONDERADO.toString()), 
						SubRelatorioRegistroDaNotaDeSalaMateriaisVO.Fields.CUSTO_MEDIO_PONDERADO.toString())
				
				
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()),
						SubRelatorioRegistroDaNotaDeSalaMateriaisVO.Fields.EQUIPE_NOME.toString())
				
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME_USUAL.toString()),
						SubRelatorioRegistroDaNotaDeSalaMateriaisVO.Fields.EQUIPE_NOME_USUAL.toString())
				
				.add(Projections.property("MCI.".concat(MbcMaterialPorCirurgia.Fields.CRIADO_EM.toString()))));
				
				
		criteria.add(Restrictions.eq("CRG.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));
		criteria.add(Restrictions.eq("PCG.".concat(MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString()), Boolean.TRUE));
		criteria.add(Restrictions.eq("SCE.".concat(SceEstoqueGeral.Fields.FRN_NUMERO.toString()), 1));
		criteria.add(Restrictions.sqlRestriction("SCE3_.DT_COMPETENCIA = to_date(to_char(CRG2_.DATA,'mmyyyy'),'mmyyyy')"));
		
		criteria.addOrder(Order.asc("MAT.".concat(ScoMaterial.Fields.NOME.toString())));
				
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioRegistroDaNotaDeSalaMateriaisVO.class));
		List<SubRelatorioRegistroDaNotaDeSalaMateriaisVO>  listMateriaisCirurgia = executeCriteria(criteria);
		
		
		for (SubRelatorioRegistroDaNotaDeSalaMateriaisVO vo : listMateriaisCirurgia) {
			SceConversaoUnidadeConsumos conversao = obterConversaoUnidadePorMaterialUnidadeMedida(vo.getMatCodigo(), vo.getUnidade());
			BigDecimal total = BigDecimal.ZERO;
			if(conversao != null){
				BigDecimal quantidade = new BigDecimal(vo.getQuantidadeDouble());
				total =  quantidade.multiply(
								new BigDecimal(((vo.getCustoMedioPonderadoBigDecimal().doubleValue()) / (conversao.getFatorConversao().doubleValue()))));
				vo.setCustoTotal(total);
			}else{
				BigDecimal quantidade = new BigDecimal(vo.getQuantidadeDouble());
				total =  quantidade.multiply(vo.getCustoMedioPonderadoBigDecimal());
				vo.setCustoTotal(total);
			}
		}
		
		return listMateriaisCirurgia;
	}

	public SceConversaoUnidadeConsumos obterConversaoUnidadePorMaterialUnidadeMedida(Integer material, String unidadeMedida) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConversaoUnidadeConsumos.class);
		criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.MATERIAL.toString(), material));
		criteria.add(Restrictions.eq(SceConversaoUnidadeConsumos.Fields.UNIDADE_MEDIDA.toString(), unidadeMedida));
		return (SceConversaoUnidadeConsumos) executeCriteriaUniqueResult(criteria);
	}

	
	public List<String> obterListaOrteseProteseUtilizadosPorCrgSeq(Integer crgSeq) {
		
		StringBuilder sql = new StringBuilder(1700);
		
		sql.append(" SELECT subtab.MATERIAL_QTDE ||' Unid    '||subtab.MATERIAL_DESCRICAO OPMES FROM (SELECT CASE iro.IND_REQUERIDO ");
		sql.append("             when 'NOV' then '(Nova Solicitação de Material) '|| ");
		sql.append("                   case when COALESCE(mat.CODIGO, -1) = -1 ");
		sql.append("                        then COALESCE(iro.ESPEC_NOVO_MAT, iro.SOLC_NOVO_MAT) ");
		sql.append("                        else mat.CODIGO||' - '||mat.NOME ");
		sql.append("                   end ");
		sql.append("             when 'ADC' then '(Material Adicionado pelo Usuário) '||mat.CODIGO||' - '||mat.NOME ");
		sql.append("             else  '(ROMP '||iph.COD_TABELA||')'|| ");
		sql.append("                   case when COALESCE(mat.CODIGO,-1) = -1 ");
		sql.append("                        then ' '||iph.DESCRICAO ");
		sql.append("                        else ' | '||mat.CODIGO ||' - '||mat.NOME ");
		sql.append("                   end ");
		sql.append("        END MATERIAL_DESCRICAO ");
		sql.append("       ,COALESCE(mio.QTD_SOLC, iro.QTD_SOLC) MATERIAL_QTDE ");
		sql.append("       ,case iro.IND_REQUERIDO  ");
		sql.append("          when 'NOV' then 1  ");
		sql.append("          when 'ADC' then 2  ");
		sql.append("          else 3 ");
		sql.append("        end as reqOrder "); 
		sql.append(" FROM   agh.MBC_AGENDAS                agd ");
		sql.append(" JOIN   agh.MBC_REQUISICAO_OPMES       rop ON rop.AGD_SEQ = agd.SEQ ");
		sql.append(" JOIN   agh.MBC_ITENS_REQUISICAO_OPMES iro ON iro.ROP_SEQ = rop.SEQ ");
		sql.append(" JOIN   agh.MBC_CIRURGIAS crg ON crg.agd_seq = agd.seq ");
		sql.append(" LEFT JOIN agh.MBC_MATERIAIS_ITEM_OPMES   mio ON iro.SEQ = mio.IRO_SEQ AND mio.QTD_SOLC > 0 ");
		sql.append(" LEFT JOIN agh.SCO_MATERIAIS              mat ON mio.MAT_CODIGO = mat.CODIGO ");
		sql.append(" LEFT JOIN agh.FAT_ITENS_PROCED_HOSPITALAR iph on iro.IPH_PHO_SEQ = iph.PHO_SEQ and iro.IPH_SEQ = iph.SEQ ");
		sql.append(" WHERE  crg.SEQ = :crgSeq ");
		sql.append(" AND    iro.QTD_SOLC       > 0 ");
		sql.append(" AND    iro.IND_AUTORIZADO = 'S' ");
		sql.append(" ORDER BY reqOrder ,iph.COD_TABELA   ,mat.CODIGO) subtab");


		SQLQuery query = createSQLQuery(sql.toString());
		
		query.setInteger("crgSeq", crgSeq);

		return (List<String>) query.list();
	}
	
	public List<SubRelatorioNotasDeConsumoDaSalaMateriaisVO> obterListaMateriaisUtilizadosPorCrgSeq(Integer crgSeq, Integer grupoMatOrtProt) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMaterialPorCirurgia.class, "MPC");
		criteria.createAlias("MPC.".concat(MbcMaterialPorCirurgia.Fields.SCO_MATERIAL.toString()), "MAT");
		
		StringBuilder sqlProjection = new StringBuilder(200);
		sqlProjection.append("   RPAD(mat1_.NOME,30,'. ') ||': '||                          ")
		.append("   LPAD(mat1_.UMD_CODIGO,3,' ') || '   ' || {alias}.QUANTIDADE    "
			).append( SubRelatorioNotasDeConsumoDaSalaMateriaisVO.Fields.DESCRICAO_MATERIAL.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaMateriaisVO.Fields.DESCRICAO_MATERIAL.toString()},
						new Type[] { StringType.INSTANCE })));
		
		criteria.add(Restrictions.eq("MPC." + MbcMaterialPorCirurgia.Fields.MBC_CIRURGIAS_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.ne("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), grupoMatOrtProt));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaMateriaisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Object[] obterDadosDispensarioPorMaterialCirurgico(Integer matCodigo, Integer crgSeq) {
		StringBuilder sql = new StringBuilder(350);
		sql.append(" SELECT BIL.QTY, BIL.STATUS ")
			.append(" FROM TBL_EXP_BILLING BIL ")
			.append(" WHERE BIL.FACILITY = 2 ")
			.append(" AND BIL.MSG_TYPE = 'EVM' ")
			.append(" AND BIL.STATUS IN (1,3) ")
			.append(" AND BIL.ITEM_ID = ").append(matCodigo.toString())
			.append(" AND BIL.PAT_ID_ALT =  ").append(crgSeq.toString());
		
		Query query = this.createNativeQuery(sql.toString());
		
		List<Object[]> listResult = query.getResultList();
		
		if (!listResult.isEmpty()) {
			return listResult.get(0);
		}
		return null;
	}
	
	public void atualizarDispensarioPorMaterialCirurgico(Short status, Integer matCodigo, Integer crgSeq) {
		StringBuilder sql = new StringBuilder(350);
		sql.append(" UPDATE TBL_EXP_BILLING ")
			.append(" SET STATUS = ").append(status.toString())
			.append(" WHERE FACILITY = 2 ")
			.append(" AND MSG_TYPE = 'EVM' ")
			.append(" AND STATUS IN (1,3) ")
			.append(" AND ITEM_ID = ").append(matCodigo.toString())
			.append(" AND PAT_ID_ALT =  ").append(crgSeq.toString());
		
		Query query = this.createNativeQuery(sql.toString());
		query.executeUpdate();
		this.flush();
	}

}
