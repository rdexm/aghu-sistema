package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import javax.persistence.Table;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaCnesVO;
import br.gov.mec.aghu.model.FatCnesUf;
import br.gov.mec.aghu.model.FatProcedServicos;
import br.gov.mec.aghu.model.FatServClassificacoes;
import br.gov.mec.aghu.model.FatServicos;

public class FatServicosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatServicos> {

	private static final long serialVersionUID = 1999470682101015947L;
	
	public List<CursorBuscaCnesVO> buscarCursorCNES(final Integer iphSeq, Short phoSeq, DominioSituacao situacao, short unfSeq){
		final StringBuilder sql = new StringBuilder(500);
		
		sql.append("SELECT ")
		   .append("    FSE.").append(FatServicos.Fields.CODIGO.name())
 		 					  .append(" AS ").append(CursorBuscaCnesVO.Fields.CODIGO.toString())
 		 					  
		   .append("   ,FSE.").append(FatServicos.Fields.DESCRICAO.name())
		 					  .append(" AS ").append(CursorBuscaCnesVO.Fields.SERVICO.toString())

		   .append("   ,FCS.").append(FatServClassificacoes.Fields.CODIGO.name())
 		 					  .append(" AS ").append(CursorBuscaCnesVO.Fields.CODIGO_CLASS.toString())
		 
		   .append("   ,FCS.").append(FatServClassificacoes.Fields.DESCRICAO.name())
		 					  .append(" AS ").append(CursorBuscaCnesVO.Fields.CLASSIFICACAO.toString())
		 					
		   .append("   ,LPAD(FSE.").append(FatServicos.Fields.CODIGO.name()).append(",3,'0') || ")
		   .append("    LPAD(FCS.").append(FatServClassificacoes.Fields.CODIGO.name()).append(",3,'0') ")
 		 					 .append(" AS ").append(CursorBuscaCnesVO.Fields.SERV_CLASS.toString())
		 
		   .append("   ,COALESCE(FCU.").append(FatCnesUf.Fields.UNF_SEQ.name()).append(",0) ")
		 					.append(" AS ").append(CursorBuscaCnesVO.Fields.UNF_SEQ.toString())
		
		   .append(" FROM ")
		   .append("   AGH.").append(FatServicos.class.getAnnotation(Table.class).name()).append(" FSE ")
		   .append(" , AGH.").append(FatServClassificacoes.class.getAnnotation(Table.class).name()).append(" FCS ")
		   .append(" , AGH.").append(FatProcedServicos.class.getAnnotation(Table.class).name()).append(" PSC")
		   .append("    LEFT JOIN AGH.").append(FatCnesUf.class.getAnnotation(Table.class).name()).append(" FCU ")
		   								.append(" ON FCU.").append(FatCnesUf.Fields.FCS_SEQ.name()).append(" = PSC.").append(FatProcedServicos.Fields.FCS_SEQ.name())
		   								.append("    AND FCU.").append(FatCnesUf.Fields.UNF_SEQ.name()).append(" = :P_UNF_SEQ ")
		  
		   .append(" WHERE 1=1")
		   .append("  AND PSC.").append(FatProcedServicos.Fields.IPH_PHO_SEQ.name()).append(" = :P_IPH_PHO_SEQ")
		   .append("  AND PSC.").append(FatProcedServicos.Fields.IPH_SEQ.name()).append(" = :P_IPH_SEQ")
		   .append("  AND PSC.").append(FatProcedServicos.Fields.IND_SITUACAO.name()).append(" = :P_SITUACAO ")
		   .append("  AND FCS.").append(FatServClassificacoes.Fields.SEQ.name()).append(" = PSC.").append(FatProcedServicos.Fields.FCS_SEQ.name())
		   .append("  AND FCS.").append(FatServClassificacoes.Fields.IND_SITUACAO.name()).append(" = :P_SITUACAO")
		   .append("  AND FSE.").append(FatServicos.Fields.SEQ.name()).append(" = FCS.").append(FatServClassificacoes.Fields.FSE_SEQ.name())
		   .append("  AND FSE.").append(FatServicos.Fields.IND_SITUACAO.name()).append(" = :P_SITUACAO ")
		   
		   .append(" ORDER BY COALESCE(FCU.").append(FatCnesUf.Fields.UNF_SEQ.name()).append(",0) DESC");
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setString("P_SITUACAO", situacao.toString());
		query.setInteger("P_IPH_SEQ", iphSeq);
		query.setShort("P_IPH_PHO_SEQ", phoSeq);
		query.setShort("P_UNF_SEQ", unfSeq); 

		query.addScalar(CursorBuscaCnesVO.Fields.CODIGO.toString(), StringType.INSTANCE)
		     .addScalar(CursorBuscaCnesVO.Fields.SERVICO.toString(), StringType.INSTANCE)
		     .addScalar(CursorBuscaCnesVO.Fields.CODIGO_CLASS.toString(), StringType.INSTANCE)
		     .addScalar(CursorBuscaCnesVO.Fields.CLASSIFICACAO.toString(), StringType.INSTANCE)
		     .addScalar(CursorBuscaCnesVO.Fields.SERV_CLASS.toString(), StringType.INSTANCE)
		     .addScalar(CursorBuscaCnesVO.Fields.UNF_SEQ.toString(), ShortType.INSTANCE)
		     .setResultTransformer(Transformers.aliasToBean(CursorBuscaCnesVO.class));

		return query.list();
	}
	
	public List<FatServicos> buscaListaServicosAtivos() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatServicos.class);
		criteria.add(Restrictions.eq(FatServicos.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
}