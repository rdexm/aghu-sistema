package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.vo.CirurgiaCodigoProcedimentoSusVO;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.faturamento.vo.CursorBuscaUnidadeVO;
import br.gov.mec.aghu.faturamento.vo.FatcVerCarPhiCnvVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;

public class VFatAssociacaoProcedimento2DAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VFatAssociacaoProcedimento>{

	private static final long serialVersionUID = -2181678688614478052L;
	private static final String ALIAS_VAPR = "VAPR";
	private static final String PONTO = ".";

	/**
	 * ORADB: FATC_BUSCA_SERV_CLASS.C_BUSCA_UNIDADE
	 * @param iphSe 
	 * @param phoSeq 
	 */
	public List<CursorBuscaUnidadeVO> obterCursorCBuscaUnidade(final Integer cthSeq, Integer iphSeq, Short phoSeq) {
		final StringBuilder sql = new StringBuilder(400);
		
		sql.append("SELECT ISE.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.name())
								 .append(" AS ").append(CursorBuscaUnidadeVO.Fields.UFE_UNF_SEQ.toString())
								 
		   .append(" 	  ,ICH.").append(FatItemContaHospitalar.Fields.IND_ORIGEM.name())
			 					 .append(" AS ").append(CursorBuscaUnidadeVO.Fields.IND_ORIGEM.toString())
		   
		   .append(" 	  ,ICH.").append(FatItemContaHospitalar.Fields.IPS_RMP_SEQ.name())
			 					 .append(" AS ").append(CursorBuscaUnidadeVO.Fields.IPS_RMP_SEQ.toString())
			 
		   .append(" 	  ,ICH.").append(FatItemContaHospitalar.Fields.UNF_SEQ.name())
			 					 .append(" AS ").append(CursorBuscaUnidadeVO.Fields.UNF_SEQ.toString())
		   
		   .append(" FROM  ")
		   .append(" 	   AGH.").append(FatItemContaHospitalar.class.getAnnotation(Table.class).name()).append(" ICH ")
		   .append("	   LEFT JOIN AGH.").append(AelItemSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" ISE ON ")
		   								   .append("     ISE.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.name()).append(" = ICH.").append(FatItemContaHospitalar.Fields.ISE_SOE_SEQ.name())
		   								   .append(" AND ISE.").append(AelItemSolicitacaoExames.Fields.SEQP.name()).append(" = ICH.").append(FatItemContaHospitalar.Fields.ISE_SEQP.name())
		   								   
		   .append("  	   INNER JOIN AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VFAT ON ")
										    .append(" VFAT.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name()).append(" = ICH.").append(FatItemContaHospitalar.Fields.PHI_SEQ.name())
		   
		   .append(" WHERE 1=1 ")
		   .append(" 	   AND ICH.").append(FatItemContaHospitalar.Fields.CTH_SEQ.name()).append(" = :P_CTH_SEQ ")
		   .append(" 	   AND ICH.").append(FatItemContaHospitalar.Fields.IND_SITUACAO.name()).append(" NOT IN (:P_SITUACOES) ")
		   .append(" 	   AND VFAT.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.name()).append(" = :P_IPH_SEQ ")
		   .append(" 	   AND VFAT.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = :P_IPH_PHO_SEQ ");
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setInteger("P_CTH_SEQ", cthSeq);
		
		final String[] situacoes = {DominioSituacaoItenConta.C.toString(),DominioSituacaoItenConta.N.toString()};
		query.setParameterList("P_SITUACOES", situacoes);
		
		query.setInteger("P_IPH_SEQ", iphSeq);
		query.setShort("P_IPH_PHO_SEQ", phoSeq);

		query.addScalar(CursorBuscaUnidadeVO.Fields.UFE_UNF_SEQ.toString(), ShortType.INSTANCE)
		     .addScalar(CursorBuscaUnidadeVO.Fields.IND_ORIGEM.toString(), StringType.INSTANCE)
		     .addScalar(CursorBuscaUnidadeVO.Fields.IPS_RMP_SEQ.toString(), IntegerType.INSTANCE)
		     .addScalar(CursorBuscaUnidadeVO.Fields.UNF_SEQ.toString(), ShortType.INSTANCE)
		     .setResultTransformer(Transformers.aliasToBean(CursorBuscaUnidadeVO.class));

		return query.list();
	}

	public List<FatcVerCarPhiCnvVO> obterFatcVerCarPhiCnvVO(final Short cpgGrcSeq, final Integer phiSeq, final Byte cspSeq, final Short cnvCodigo){
		final StringBuilder hql = new StringBuilder(380);
		
		hql.append("SELECT ")
		   .append("  VAPR.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString()).append(" as ").append(FatcVerCarPhiCnvVO.Fields.IPH_PHO_SEQ.toString())
		   .append(" ,VAPR.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString()).append(" as ").append(FatcVerCarPhiCnvVO.Fields.IPH_SEQ.toString())
		   
		   .append(" FROM ")
		   .append(VFatAssociacaoProcedimento.class.getSimpleName()).append(" VAPR, ")
		   .append(FatProcedHospInternos.class.getSimpleName()).append(" PHI, ")
		   .append(FatConvenioSaudePlano.class.getSimpleName()).append(" CSP, ")
		   .append(FatConvenioSaude.class.getSimpleName()).append(" CNV ")
		   
		   .append(" WHERE 1=1 ")
		   .append("  AND CNV.").append(FatConvenioSaude.Fields.CODIGO.toString()).append(" = :PRM_CNV_CODIGO ")
		   .append("  AND CNV.").append(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()).append(" = :PRM_GRUPO_CONVENIO ")
		   
		   .append("  AND CSP.").append(FatConvenioSaudePlano.Fields.CNV_CODIGO.toString())
		   					.append(" = CNV.").append(FatConvenioSaude.Fields.CODIGO.toString())
		   
		   .append("  AND CSP.").append(FatConvenioSaudePlano.Fields.SEQ.toString()).append(" = :PRM_CSP_SEQ ")
		   					
		   .append("  AND PHI.").append(FatProcedHospInternos.Fields.SEQ.toString()).append(" = :PRM_PHI_SEQ ")
		   
		   .append("  AND VAPR.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString())
		   					.append(" = PHI.").append(FatProcedHospInternos.Fields.SEQ.toString())
		   					
		   .append("  AND VAPR.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString())
		   					.append(" = CNV.").append(FatConvenioSaude.Fields.CODIGO.toString())
		   					
		   .append("  AND VAPR.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString())
		   					.append(" = CSP.").append(FatConvenioSaudePlano.Fields.SEQ.toString())
		   					
		   .append("  AND VAPR.").append(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString()).append(" = :PRM_CPG_GRC_SEQ ")
		;

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("PRM_CPG_GRC_SEQ", cpgGrcSeq);
		query.setParameter("PRM_PHI_SEQ", phiSeq);
		query.setParameter("PRM_CSP_SEQ", cspSeq);
		query.setParameter("PRM_GRUPO_CONVENIO", DominioGrupoConvenio.S);
		query.setParameter("PRM_CNV_CODIGO", cnvCodigo);
		
		return query.setResultTransformer(Transformers.aliasToBean(FatcVerCarPhiCnvVO.class)).list();
	}
	
	public List<CirurgiaCodigoProcedimentoSusVO> obterListaCirurgiaCodigoProcedimentoPorPhiSeqOrigemGrupo(Integer phiSeq, 
			Short grupoSUS, Byte origemCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, "VAPR");
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.PHI_SEQ.toString())
				.add(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.COD_TABELA.toString())
				.add(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_DESCRICAO),
						CirurgiaCodigoProcedimentoSusVO.Fields.IPH_DESCRICAO.toString())
				.add(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.IPH_PHO_SEQ.toString())
				.add(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_IND_SITUACAO.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.IPH_IND_SITUACAO.toString())
				.add(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.IPH_SEQ.toString())
				.add(Projections.property("VAPR." + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.CPG_GRC_SEQ.toString())));
		
		criteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), grupoSUS));
		criteria.add(Restrictions.eq("VAPR." + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), origemCodigo));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiaCodigoProcedimentoSusVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	public List<CirurgiaCodigoProcedimentoSusVO> getCursorFatAssocProcdporPhiSeqOrigemGrupo(
			final Integer phiSeqList, final Short grupoSUS, final Byte origemCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class, ALIAS_VAPR);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.IPH_SEQ.toString())
				.add(Projections.property(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.COD_TABELA.toString())
				.add(Projections.property(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.IPH_DESCRICAO),
						CirurgiaCodigoProcedimentoSusVO.Fields.IPH_DESCRICAO.toString())
				.add(Projections.property(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.IPH_PHO_SEQ.toString())
				.add(Projections.property(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.IPH_IND_SITUACAO.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.IPH_IND_SITUACAO.toString())
				.add(Projections.property(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString()),
						CirurgiaCodigoProcedimentoSusVO.Fields.CPG_GRC_SEQ.toString()));
		
		criteria.add(Restrictions.eq(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeqList));
		criteria.add(Restrictions.eq(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.toString(), grupoSUS));
		criteria.add(Restrictions.eq(ALIAS_VAPR + PONTO + VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), origemCodigo));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CirurgiaCodigoProcedimentoSusVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	public Long obterCodTabelaPorPhi(Integer phiSeq, Short convenioSus,
			Byte planoSusAmbulatorio,
			Short tabelaFaturamentoUnificada){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);
		criteria.setProjection(Projections.property(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), convenioSus));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), planoSusAmbulatorio));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), tabelaFaturamentoUnificada));
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	public List<VFatAssociacaoProcedimento> pesquisarAssociacaoProcedimentos(Integer phiSeq, Short convenioSus,
			Byte plano,
			Long codTabela){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatAssociacaoProcedimento.class);
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), convenioSus));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString(), plano));
		criteria.add(Restrictions.eq(VFatAssociacaoProcedimento.Fields.COD_TABELA.toString(), codTabela));
		return executeCriteria(criteria);
	}
}
