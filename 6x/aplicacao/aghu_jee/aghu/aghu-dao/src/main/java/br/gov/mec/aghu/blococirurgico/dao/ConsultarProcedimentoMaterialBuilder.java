package br.gov.mec.aghu.blococirurgico.dao;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoVO;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ConsultarProcedimentoMaterialBuilder  extends QueryBuilder<StringBuilder>{
  protected StringBuilder createProduct()
  {
    StringBuilder hql = new StringBuilder();
    return hql;
  }
  
  protected void doBuild(StringBuilder hql)
  {
    hql.append("SELECT ")
    .append("PCI.seq as " ).append( ItemProcedimentoVO.Fields.PCI_SEQ.toString())
    .append(",PCI.descricao as " ).append( ItemProcedimentoVO.Fields.PCI_DESCRICAO.toString())
    .append(",PHI.seq as " ).append( ItemProcedimentoVO.Fields.PHI_SEQ.toString())
    .append(",IPH.pho_seq as " ).append( ItemProcedimentoVO.Fields.IPH_PHO_SEQ.toString())
    .append(",IPH.seq as " ).append( ItemProcedimentoVO.Fields.IPH_SEQ.toString())
    .append(",IPH.cod_tabela as " ).append( ItemProcedimentoVO.Fields.IPH_COD_TABELA.toString())
    .append(",IPH.descricao as " ).append( ItemProcedimentoVO.Fields.IPH_DESCRICAO.toString())
    .append(",CEI.ind_comparacao as " ).append( ItemProcedimentoVO.Fields.CEI_IND_COMPARACAO.toString())
    .append(",CEI.ind_compat_exclus as " ).append( ItemProcedimentoVO.Fields.CEI_IND_COMPAT_EXCLUS.toString())
    .append(",CMP.pho_seq as " ).append( ItemProcedimentoVO.Fields.CMP_PHO_SEQ.toString())
    .append(",CMP.seq as " ).append( ItemProcedimentoVO.Fields.CMP_SEQ.toString())
    .append(",CMP.cod_tabela as " ).append( ItemProcedimentoVO.Fields.CMP_COD_TABELA.toString())
    .append(",CMP.descricao as " ).append( ItemProcedimentoVO.Fields.CMP_DESCRICAO.toString())
    .append(",EXC.ind_comparacao as " ).append( ItemProcedimentoVO.Fields.EXC_IND_COMPARACAO.toString())
    .append(",EXC.ind_compat_exclus as " ).append( ItemProcedimentoVO.Fields.EXC_IND_COMPAT_EXCLUS.toString())
    .append(",IPX.seq as " ).append( ItemProcedimentoVO.Fields.IPX_SEQ.toString())
    .append(",IPX.cod_tabela as " ).append( ItemProcedimentoVO.Fields.IPX_COD_TABELA.toString())
    .append(",IPX.descricao as " ).append( ItemProcedimentoVO.Fields.IPX_DESCRICAO.toString())
    .append(" FROM ")
    .append("AGH.MBC_PROCEDIMENTO_CIRURGICOS PCI ")
    .append(" INNER JOIN ")
    .append("AGH.FAT_PROCED_HOSP_INTERNOS PHI ")
    .append(" ON (").append("PCI.seq = PHI.pci_seq)")
    .append(" INNER JOIN ")
    .append("AGH.FAT_CONV_GRUPO_ITENS_PROCED CGI ")
    .append(" ON (").append("PHI.seq = CGI.phi_seq)")
    .append(" INNER JOIN ")
    .append("AGH.FAT_ITENS_PROCED_HOSPITALAR IPH ")
    .append(" ON (").append("CGI.iph_pho_seq = IPH.pho_seq ")
    .append(" AND ").append("CGI.iph_seq = IPH.seq)")
    .append(" INNER JOIN ")
    .append("AGH.FAT_COMPAT_EXCLUS_ITENS CEI ")
    .append(" ON (").append("CEI.iph_pho_seq = IPH.pho_seq")
    .append(" AND ").append("CEI.iph_seq = IPH.seq)")
    .append(" INNER JOIN ")
    .append("AGH.FAT_ITENS_PROCED_HOSPITALAR CMP ")
    .append(" ON (").append("CEI.iph_pho_seq_compatibiliza = CMP.pho_seq")
    .append(" AND ").append("CEI.iph_seq_compatibiliza = CMP.seq)")
    .append(" INNER JOIN ")
    .append("AGH.FAT_VLR_ITEM_PROCED_HOSP_COMPS VLR ")
    .append(" ON (").append("CMP.pho_seq = VLR.iph_pho_seq")
    .append(" AND ").append("CMP.seq = VLR.iph_seq)")
    .append(" LEFT JOIN ")
    .append("AGH.FAT_COMPAT_EXCLUS_ITENS EXC ")
    .append("ON (").append("CEI.iph_pho_seq_compatibiliza = EXC.iph_pho_seq")
    .append(" AND ").append("CEI.iph_seq_compatibiliza = EXC.iph_seq)")
    .append(" LEFT JOIN ")
    .append("AGH.FAT_ITENS_PROCED_HOSPITALAR IPX ")
    .append(" ON (").append("EXC.iph_pho_seq_compatibiliza = IPX.pho_seq")
    .append(" AND ").append("EXC.iph_seq_compatibiliza = IPX.seq)")
    .append(" INNER JOIN ")
    .append("AGH.FAT_COMPETENCIAS_COMPATIBILID CEC ")
    .append(" ON (").append("CEI.iph_pho_seq = CEC.iph_pho_seq")
    .append(" AND ").append("CEI.iph_seq = CEC.iph_seq")
    .append(" AND ").append("CEI.cpx_seq = CEC.seq)")
    .append(" LEFT JOIN ")
    .append("AGH.FAT_COMPETENCIAS_COMPATIBILID CEX ")
    .append(" ON (").append("EXC.iph_pho_seq = ").append("CEX.iph_pho_seq")
    .append(" AND ").append("EXC.iph_seq = ").append("CEX.iph_seq")
    .append(" AND ").append("EXC.cpx_seq = ").append("CEX.seq)")
    .append(" WHERE ")
    .append("VLR.dt_inicio_competencia <= :dataInicioCompt ")
    .append(" AND ").append("VLR.dt_fim_competencia is null ")
    .append(" AND ").append("CEX.dt_inicio_validade <= :dataInicioVal ")
    .append(" AND ").append("CEX.dt_fim_validade is null ")
    .append(" AND ").append("IPH.ind_situacao = :indSituacaoIPH")
    .append(" AND ").append("PHI.ind_situacao = :indSituacaoPHI")
    .append(" AND ").append("CMP.ind_situacao = :indSituacaoCMP")
    .append(" AND ").append("IPX.ind_situacao = :indSituacaoIPX")
    .append(" AND ").append("IPH.pho_seq = ").append(obterSubSelectParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO))
    .append(" AND ").append("CGI.cpg_cph_csp_seq = ").append(obterSubSelectParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO))
    .append(" AND ").append("CGI.cpg_cph_csp_cnv_codigo = ").append(obterSubSelectParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO))
    .append(" AND ").append("CMP.fog_sgr_grp_seq = ").append(obterSubSelectParametro(AghuParametrosEnum.P_GRUPO_OPM))
    .append(" AND ").append("IPX.fog_sgr_grp_seq = 7 ")
    .append(" AND ").append("PCI.seq = :seqProcedimento ")
    .append(" AND ").append("IPH.pho_seq = :phoSeq ")
    .append(" AND ").append("IPH.seq = :iphSeq ")
    .append(" ORDER BY PCI.SEQ, IPH.COD_TABELA, CMP.COD_TABELA ");
  }
  
  private StringBuilder obterSubSelectParametro(AghuParametrosEnum pTabelaFaturPadrao)
  {
    StringBuilder hql = new StringBuilder(100);
    hql.append("(SELECT vlr_numerico ")
    .append(" FROM ")
    .append("AGH.AGH_PARAMETROS")
    .append(" WHERE ")
    .append(" nome = '" ).append( pTabelaFaturPadrao.name() ).append( "' )");
    return hql;
  }
}