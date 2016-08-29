package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioSituacaoAelExameAp;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaLaminasVO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelLaminaAps;
import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;

public class AelLaminaApsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelLaminaAps> {
	
	private static final long serialVersionUID = -653468706566127654L;

	public List<RelatorioMapaLaminasVO> pesquisarRelatorioMapaLaminasVO(final Date dtRelatorio, final AelCestoPatologia cesto){
		final StringBuilder sql = new StringBuilder(600);
		
		sql.append(" select ")
		   .append("       LAM.").append(AelLaminaAps.Fields.DTHR_LAMINA.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.DATA.toString())
		   .append("     , CES.").append(AelCestoPatologia.Fields.SIGLA.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.SIGLA_CESTO.toString())
		   .append("     , CES.").append(AelCestoPatologia.Fields.DESCRICAO.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.DESCRICAO_CESTO.toString())
		   .append("     , ANA.").append(AelAnatomoPatologico.Fields.NUMERO_AP.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.NUMERO_AP.toString())
		   .append("     , ANA.").append(AelAnatomoPatologico.Fields.SEQ.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.LUM_SEQ.toString())
		   .append("     , LAM.").append(AelLaminaAps.Fields.NUMERO_CAPSULA.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.NUMERO_CAPSULA.toString())
		   .append("     , LAM.").append(AelLaminaAps.Fields.NUMERO_FRAGMENTOS.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.NUMERO_FRAGMENTO.toString())
		   .append("     , LAM.").append(AelLaminaAps.Fields.COLORACAO.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.COLORACAO.toString())
		   .append("     , LAM.").append(AelLaminaAps.Fields.DESCRICAO.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.DESCRICAO.toString())
		   .append("     , LAM.").append(AelLaminaAps.Fields.OBSERVACAO.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.OBSERVACAO.toString())
		   .append("     , EXA.").append(AelExameAp.Fields.MATERIAIS.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.MATERIAIS.toString())
		   .append("     , PES.").append(RapPessoasFisicas.Fields.NOME.name()).append(" as ").append(RelatorioMapaLaminasVO.Fields.RESIDENTE.toString())

		   .append(" FROM ")
		   
		   .append("       AGH.").append(AelLaminaAps.class.getAnnotation(Table.class).name()).append(" LAM ")
		   .append("     , AGH.").append(AelExameAp.class.getAnnotation(Table.class).name()).append(" EXA ")
		   .append("     , AGH.").append(AelAnatomoPatologico.class.getAnnotation(Table.class).name()).append(" ANA ")
		   .append("     , AGH.").append(AelCestoPatologia.class.getAnnotation(Table.class).name()).append(" CES ")
		   
		   .append("     , AGH.").append(AelMacroscopiaAps.class.getAnnotation(Table.class).name()).append(" MAC ")
		   .append("     , AGH.").append(RapServidores.class.getAnnotation(Table.class).name()).append(" RAP ")
		   .append("     , AGH.").append(RapPessoasFisicas.class.getAnnotation(Table.class).name()).append(" PES ")
		   
		   .append(" WHERE 1=1   ")
		   .append("   AND LAM.").append(AelLaminaAps.Fields.LUX_SEQ.name()).append(" = EXA.").append(AelExameAp.Fields.SEQ.name())
		   .append("   AND EXA.").append(AelExameAp.Fields.LUM_SEQ.name()).append(" = ANA.").append(AelAnatomoPatologico.Fields.SEQ.name())
		   .append("   AND LAM.").append(AelLaminaAps.Fields.CST_SEQ.name()).append(" = CES.").append(AelCestoPatologia.Fields.SEQ.name())
		   .append("   AND EXA.").append(AelExameAp.Fields.SITUACAO.name()).append(" IN (:PRM_SITUACAO) ")
		   .append("   AND LAM.").append(AelLaminaAps.Fields.DTHR_LAMINA.name()).append(" = :PRM_DTHR_LAMINA ")

		   .append("   AND EXA.").append(AelExameAp.Fields.SEQ.name()).append(" = MAC.").append(AelMacroscopiaAps.Fields.LUX_SEQ.name())
		   .append("   AND RAP.").append(RapServidores.Fields.MATRICULA.name()).append(" = MAC.").append(AelMacroscopiaAps.Fields.SER_MATRICULA.name())
		   .append("   AND RAP.").append(RapServidores.Fields.VIN_CODIGO.name()).append(" = MAC.").append(AelMacroscopiaAps.Fields.SER_VIN_CODIGO.name())
   		   .append("   AND RAP.").append(RapServidores.Fields.PES_CODIGO.name()).append(" = PES.").append(RapPessoasFisicas.Fields.CODIGO.name())
		   ;
		
		if(cesto != null){
			sql.append(" AND CES.").append(AelCestoPatologia.Fields.SEQ.name()).append(" = :PRM_CESTO");
		}
		
		sql.append(" ORDER BY CES.").append(AelCestoPatologia.Fields.SEQ.name());

		final SQLQuery query = createSQLQuery(sql.toString());
		
		final String []situacoes = {DominioSituacaoAelExameAp.I.toString(), DominioSituacaoAelExameAp.R.toString()};
		query.setParameterList("PRM_SITUACAO", situacoes);

		if(dtRelatorio != null){
			query.setDate("PRM_DTHR_LAMINA", dtRelatorio);
		}
		
		if(cesto != null){
			query.setInteger("PRM_CESTO", cesto.getSeq());
		}

		final List<RelatorioMapaLaminasVO> vos = query.addScalar(RelatorioMapaLaminasVO.Fields.DATA.toString(), DateType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.SIGLA_CESTO.toString(),StringType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.DESCRICAO_CESTO.toString(),StringType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.NUMERO_AP.toString(),LongType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.LUM_SEQ.toString(),LongType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.NUMERO_CAPSULA.toString(),StringType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.NUMERO_FRAGMENTO.toString(),StringType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.COLORACAO.toString(),StringType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.DESCRICAO.toString(),StringType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.OBSERVACAO.toString(),StringType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.MATERIAIS.toString(),StringType.INSTANCE)
											          .addScalar(RelatorioMapaLaminasVO.Fields.RESIDENTE.toString(),StringType.INSTANCE)
											 
											          .setResultTransformer(Transformers.aliasToBean(RelatorioMapaLaminasVO.class)).list();
		return vos;
	}
	
	public List<AelLaminaAps> obterListaLaminasPeloExameApSeq(Long luxSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelLaminaAps.class);
		criteria.createAlias(AelLaminaAps.Fields.CESTO_PATOLOGIA.toString(), AelLaminaAps.Fields.CESTO_PATOLOGIA.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelLaminaAps.Fields.AEL_EXAME_APS.toString(), AelLaminaAps.Fields.AEL_EXAME_APS.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AelLaminaAps.Fields.SERVIDOR.toString(), AelLaminaAps.Fields.SERVIDOR.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AelLaminaAps.Fields.MATERIAL.toString(), AelLaminaAps.Fields.MATERIAL.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelLaminaAps.Fields.TEXTO_PADRAO_COLORACAO.toString(), AelLaminaAps.Fields.TEXTO_PADRAO_COLORACAO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelLaminaAps.Fields.AEL_EXAME_APS.toString() + "." + AelExameAp.Fields.SEQ.toString(), luxSeq));
		criteria.addOrder(Order.asc(AelLaminaAps.Fields.NUMERO_CAPSULA.toString()));
		
		return executeCriteria(criteria);
    }

	public Long listarIndiceBlocoCount(TelaLaudoUnicoVO telaLaudoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelLaminaAps.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq(AelLaminaAps.Fields.LUX_SEQ.toString(), telaLaudoVO.getAelExameAp().getSeq()));
		return this.executeCriteriaCount(criteria);
	}
	
	public List<AelLaminaAps> listarAelLaminaApsPorLuxSeq(Long luxSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelLaminaAps.class);
		criteria.add(Restrictions.eq(AelLaminaAps.Fields.LUX_SEQ.toString(), luxSeq));
		return executeCriteria(criteria);
		
	}
	
}