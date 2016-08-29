package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import javax.persistence.Table;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.faturamento.vo.CursorCTrpVO;
import br.gov.mec.aghu.faturamento.vo.CursorTipoListaPmrKitPreVO;
import br.gov.mec.aghu.model.FatEspecialidadeTratamento;
import br.gov.mec.aghu.model.FatTipoTratamentos;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;

public class FatTipoTratamentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatTipoTratamentos> {
	
	private static final long serialVersionUID = -7738715132480116292L;

	/**
	 * ORADB: FATP_ATUALIZA_PMR_KITS_PRE CURSOR: C_TIPO_LISTA 
	 */
	public List<CursorTipoListaPmrKitPreVO> obterCursorCTipoLista( final Short cpgCphCspCnvCodigo, final Byte cpgCphCspSeq1, final Byte cpgCphCspSeq2,
															 	   final Integer tptSeq, final boolean indListaCandidato){

		final StringBuilder cabecalho = new StringBuilder(150);
		
		cabecalho.append("SELECT ")
				 .append(" DISTINCT TPT.").append(FatTipoTratamentos.Fields.SEQ.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.SEQ.toString())
						
				 .append(" ,TPT.").append(FatTipoTratamentos.Fields.CODIGO.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.CODIGO.toString())
						
				 .append(" ,TPT.").append(FatTipoTratamentos.Fields.DESCRICAO.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.DESCRICAO.toString())
						
				 .append(" ,VAPR_1.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.PHI_PRIMEIRA.toString())
						
				 .append(" ,VAPR_2.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.PHI_SEGUNDA.toString())
						
				 .append(" ,TPT.").append(FatTipoTratamentos.Fields.IPH_PHO_SEQ_PRIMEIRA_COB.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.PHO_SEQ_PRIMEIRA.toString())
						
				 .append(" ,TPT.").append(FatTipoTratamentos.Fields.IPH_SEQ_PRIMEIRA_COB.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.IPH_SEQ_PRIMEIRA.toString())
						
				 .append(" ,TPT.").append(FatTipoTratamentos.Fields.IPH_PHO_SEQ_SEGUNDA_COB.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.PHO_SEQ_SEGUNDA.toString())
						
				 .append(" ,TPT.").append(FatTipoTratamentos.Fields.IPH_SEQ_SEGUNDA_COB.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.IPH_SEQ_SEGUNDA.toString())
						
				 .append(" ,TPT.").append(FatTipoTratamentos.Fields.CID_SEQ.name())
						.append(" AS ").append(CursorTipoListaPmrKitPreVO.Fields.CID_SEQ.toString());
						
		final StringBuilder vapr2 = new StringBuilder(200);
		
		vapr2.append("	 LEFT OUTER JOIN AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VAPR_2 ON ")
		   				.append("     VAPR_2.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = ")
		   						.append(" TPT.").append(FatTipoTratamentos.Fields.IPH_PHO_SEQ_SEGUNDA_COB.name())
		   				
		   				.append(" AND VAPR_2.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.name()).append(" = ")
		   						.append(" TPT.").append(FatTipoTratamentos.Fields.IPH_SEQ_SEGUNDA_COB.name())
		   
		   				.append(" AND VAPR_2.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.name()).append(" = :P_CPG_CPH_CSP_CNV_CODIGO ");

		final StringBuilder vapr1 = new StringBuilder(200);		   				
		vapr1.append("	 LEFT OUTER JOIN AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VAPR_1 ON ")
		   				.append("     VAPR_1.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = ")
		   						.append(" TPT.").append(FatTipoTratamentos.Fields.IPH_PHO_SEQ_PRIMEIRA_COB.name())
		   				
		   				.append(" AND VAPR_1.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.name()).append(" = ")
		   						.append(" TPT.").append(FatTipoTratamentos.Fields.IPH_SEQ_PRIMEIRA_COB.name())
		   
		   				.append(" AND VAPR_1.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.name()).append(" = :P_CPG_CPH_CSP_CNV_CODIGO ");

		final StringBuilder where = new StringBuilder(100);
		where.append(" WHERE 1=1")
		     .append("  AND ETR.").append(FatEspecialidadeTratamento.Fields.TPT_SEQ.name()).append(" = :P_TPT_SEQ ")
		     .append("  AND TPT.").append(FatTipoTratamentos.Fields.SEQ.name()).append(" = ETR.").append(FatEspecialidadeTratamento.Fields.TPT_SEQ.name())
		     .append("  AND ETR.").append(FatEspecialidadeTratamento.Fields.IND_LISTA_CANDIDATO.name()).append(" = :P_IND_LISTA_CANDIDATO ");
		   
		   
		final StringBuilder sql = new StringBuilder(1200);
		
		sql.append(cabecalho)
		   .append(" FROM ")
		   .append("   AGH.").append(FatTipoTratamentos.class.getAnnotation(Table.class).name()).append(" TPT ")
		   
		   .append(vapr2)
		   .append(" AND VAPR_2.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = :P_CPG_CPH_CSP_SEQ1 ")
		   
		   .append(vapr1)
		   .append(" AND VAPR_1.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = :P_CPG_CPH_CSP_SEQ1 ")
		   
		   .append(" , AGH.").append(FatEspecialidadeTratamento.class.getAnnotation(Table.class).name()).append(" ETR ")
		   .append(where)
		   
		   .append(" UNION ")
		   
		   .append(cabecalho)
		   .append(" FROM ")
		   .append("   AGH.").append(FatTipoTratamentos.class.getAnnotation(Table.class).name()).append(" TPT ")
		   
		   .append(vapr2)
		   .append(" AND VAPR_2.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = :P_CPG_CPH_CSP_SEQ2 ")
		   
		   .append(vapr1)
		   .append(" AND VAPR_1.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = :P_CPG_CPH_CSP_SEQ2 ")

		   .append(" , AGH.").append(FatEspecialidadeTratamento.class.getAnnotation(Table.class).name()).append(" ETR ")
		   .append(where)
		   
		   .append("ORDER BY ").append(CursorTipoListaPmrKitPreVO.Fields.PHI_PRIMEIRA.toString()).append(" NULLS LAST,")
		   					   .append(CursorTipoListaPmrKitPreVO.Fields.PHI_SEGUNDA.toString()).append(" NULLS LAST ");
		
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setShort("P_CPG_CPH_CSP_CNV_CODIGO", cpgCphCspCnvCodigo);
		query.setInteger("P_TPT_SEQ", tptSeq);
		query.setByte("P_CPG_CPH_CSP_SEQ1", cpgCphCspSeq1); 
		query.setByte("P_CPG_CPH_CSP_SEQ2", cpgCphCspSeq2); 
		query.setString("P_IND_LISTA_CANDIDATO", indListaCandidato ? "S" : "N");
		

		query.addScalar(CursorTipoListaPmrKitPreVO.Fields.SEQ.toString(), IntegerType.INSTANCE)
			 .addScalar(CursorTipoListaPmrKitPreVO.Fields.CODIGO.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorTipoListaPmrKitPreVO.Fields.DESCRICAO.toString(), StringType.INSTANCE)
		     .addScalar(CursorTipoListaPmrKitPreVO.Fields.PHI_PRIMEIRA.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorTipoListaPmrKitPreVO.Fields.PHI_SEGUNDA.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorTipoListaPmrKitPreVO.Fields.PHO_SEQ_PRIMEIRA.toString(), ShortType.INSTANCE)
		     .addScalar(CursorTipoListaPmrKitPreVO.Fields.IPH_SEQ_PRIMEIRA.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorTipoListaPmrKitPreVO.Fields.PHO_SEQ_SEGUNDA.toString(), ShortType.INSTANCE)
		     .addScalar(CursorTipoListaPmrKitPreVO.Fields.IPH_SEQ_SEGUNDA.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorTipoListaPmrKitPreVO.Fields.CID_SEQ.toString(), IntegerType.INSTANCE)
		     .setResultTransformer(Transformers.aliasToBean(CursorTipoListaPmrKitPreVO.class));
		
		return query.list();		
	}

	/**
	 * Método para retornar CursorCTrpVO
	 * 42229 P4 - Cursor c_trp
	 */
	public List<CursorCTrpVO> obterListaCursorCTrpVO(Integer cPacCodigo, Byte indTipoTratamento){
		CursorCTrpQueryBuilder builder = new CursorCTrpQueryBuilder();
		return executeCriteria(builder.build(cPacCodigo, indTipoTratamento));
	}
}