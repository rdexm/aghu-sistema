package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.ProcedimentosXCompatibilidadesVO;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompetenciaCompatibilid;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;

public class FatCompetenciaCompatibilidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCompetenciaCompatibilid> {

	private static final long serialVersionUID = -3278002556073442891L;
 

	private static final String IPH = " IPH.";
	private static final String IPH1 = " IPH1.";
	private static final String ICT = " ICT.";
	private static final String CPX = " CPX.";
	private static final String AS = " AS ";
	private static final String COALESCE = " COALESCE(";
	private static final String AGH = " AGH.";
	private static final String AND = " AND ";
	
	/**
	 * ORADB: Cursor: ATUALIZA_COMPATIBILIDADE.CARREGA_TAB_PROC_X_COMP_AGH.C_PROC_X_COMP
	 */
	public List<ProcedimentosXCompatibilidadesVO> obterCursorCProcXCcomp(final Short phoSeq){

		final StringBuilder sql = new StringBuilder(500);
		
		sql.append("SELECT ")
		   .append(IPH).append(FatItensProcedHospitalar.Fields.SEQ.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.ITEM.toString())
		   .append(',').append(IPH).append(FatItensProcedHospitalar.Fields.COD_TABELA.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.COD_SUS.toString())
		   .append(',').append(IPH).append(FatItensProcedHospitalar.Fields.DESCRICAO.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.DESCRICAO.toString())

		   .append(',').append(ICT).append(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.TIPO.toString())
		   .append(',').append(ICT).append(FatCompatExclusItem.Fields.IND_COMPARACAO.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.COMPARACAO.toString())
		   
		   .append(',').append(IPH1).append(FatItensProcedHospitalar.Fields.SEQ.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.SEQ_SUS_COMP.toString())
		   .append(',').append(IPH1).append(FatItensProcedHospitalar.Fields.COD_TABELA.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.COD_SUS_COMP.toString())
		   .append(',').append(IPH1).append(FatItensProcedHospitalar.Fields.DESCRICAO.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.ITEM_COMPATIVEL.toString())
		
		   .append(',').append(COALESCE).append(ICT).append(FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.name()).append(",0) ").append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.QUANTIDADE.toString())
		   .append(',').append(COALESCE).append(ICT).append(FatCompatExclusItem.Fields.IND_AMBULATORIO.name()).append(",'N') ").append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.IND_AMBULATORIO.toString())
		   .append(',').append(COALESCE).append(ICT).append(FatCompatExclusItem.Fields.IND_INTERNACAO.name()).append(",'N') ").append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.IND_INTERNACAO.toString())

		   .append(',').append(ICT).append(FatCompatExclusItem.Fields.CPX_SEQ.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.CPX_SEQ.toString())
		   .append(',').append(ICT).append(FatCompatExclusItem.Fields.TTR_CODIGO.name()).append(AS).append(ProcedimentosXCompatibilidadesVO.Fields.TTR_CODIGO.toString())
		   
		   .append(" FROM ")
	   		   .append(AGH).append(FatCompetenciaCompatibilid.class.getAnnotation(Table.class).name()).append(" CPX, ")
	   		   .append(AGH).append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH, ")
	   		   .append(AGH).append(FatCompatExclusItem.class.getAnnotation(Table.class).name()).append(" ICT, ")
	   		   .append(AGH).append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH1 ")
	   		   
   		   .append(" WHERE 1=1 ")
   		   .append(AND).append(ICT).append(FatCompatExclusItem.Fields.IPH_PHO_SEQ.name()).append("= :PRM_IPH_PHO_SEQ ")
   		   
   		   .append(AND).append(ICT).append(FatCompatExclusItem.Fields.IPH_SEQ.name()).append('=')
   		   			.append(IPH).append(FatItensProcedHospitalar.Fields.SEQ.name())
   		   
   		   .append(AND).append(ICT).append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name()).append('=')
   		   			.append(IPH).append(FatItensProcedHospitalar.Fields.PHO_SEQ.name())
   		   			
   		   .append(AND).append(IPH).append(FatItensProcedHospitalar.Fields.IND_SITUACAO.name()).append("=:PRM_IND_SITUACAO")
   		   

   		   .append(AND).append(IPH1).append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append('=')
   		   			.append(ICT).append(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.name())
   		   			
   		   .append(AND).append(IPH1).append(FatItensProcedHospitalar.Fields.SEQ.name()).append('=')
   		   			.append(ICT).append(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.name())
   		   			
   		   .append(AND).append(CPX).append(FatCompetenciaCompatibilid.Fields.SEQ.name()).append('=')
   		   			.append(ICT).append(FatCompatExclusItem.Fields.CPX_SEQ.name())

   		   .append(AND).append(CPX).append(FatCompetenciaCompatibilid.Fields.SEQ.name()).append(" IS NULL ")
   		   
   		   .append(" ORDER BY ")
   		   		.append(IPH).append(FatItensProcedHospitalar.Fields.COD_TABELA.name())
   		   		.append(',').append(IPH1).append(FatItensProcedHospitalar.Fields.COD_TABELA.name());
		
		
		final SQLQuery query = createSQLQuery(sql.toString());
		query.setShort("PRM_IPH_PHO_SEQ", phoSeq);
		query.setString("PRM_IND_SITUACAO", DominioSituacao.A.toString());
		
		final List<ProcedimentosXCompatibilidadesVO> result =  
						query.addScalar(ProcedimentosXCompatibilidadesVO.Fields.ITEM.toString(), IntegerType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.COD_SUS.toString(),LongType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.DESCRICAO.toString(),ShortType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.TIPO.toString(),StringType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.COMPARACAO.toString(),StringType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.SEQ_SUS_COMP.toString(),IntegerType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.COD_SUS_COMP.toString(),LongType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.ITEM_COMPATIVEL.toString(),StringType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.QUANTIDADE.toString(),ShortType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.IND_AMBULATORIO.toString(),StringType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.IND_INTERNACAO.toString(),StringType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.CPX_SEQ.toString(),LongType.INSTANCE)
							 .addScalar(ProcedimentosXCompatibilidadesVO.Fields.TTR_CODIGO.toString(),StringType.INSTANCE)
							 
	     .setResultTransformer(Transformers.aliasToBean(ProcedimentosXCompatibilidadesVO.class)).list();

		return result;
	}
	
	
	public Date obterMaiorCompetenciaSemDataFim() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompetenciaCompatibilid.class);
		
		criteria.setProjection(Projections.projectionList()
								.add(Projections.max(FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.toString()))
							  );
		
		criteria.add(Restrictions.isNotNull(FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.toString()));
		Object obj = executeCriteriaUniqueResult(criteria);
		
		if (obj != null) {
			return (Date) obj;
		}
		
		return null;
	}
}
