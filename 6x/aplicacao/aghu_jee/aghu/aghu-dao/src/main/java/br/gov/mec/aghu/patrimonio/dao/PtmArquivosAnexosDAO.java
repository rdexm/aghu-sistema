package br.gov.mec.aghu.patrimonio.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.model.PtmArquivosAnexos;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmBemProcesso;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmNotaItemReceb;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaFiltroVO;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaGridVO;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoArquivosAnexosVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmArquivosAnexosDAO extends BaseDao<PtmArquivosAnexos> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8630870138662371319L;
	private static final String ESPACO_AA =" AA ";
    private static final String AA ="AA";
	private static final String PIRP="PIRP";
	private static final String IRP="IRP";
	private static final String PONTO=".";
	private static final String SER="SER";
	private static final String LEFTJOINAGH = " LEFT JOIN  AGH";
	private static final String FROMAGH=" FROM AGH";
	private static final String PIRPBP = "  PIRP ON (BP.IRP_SEQ = PIRP.SEQ) ";
	private static final String SELECAO = " AND AA.SEQ IN ( ( SELECT aa.seq  FROM   AGH.ptm_arquivos_anexos aa, AGH.ptm_nota_item_receb nir  WHERE  (aa.not_seq = nir.not_seq  AND    nir.irp_seq = :irpSeq  OR  aa.irp_seq = :irpSeq ) )";
	private static final String UNION = "union (select aa.seq from AGH.ptm_arquivos_anexos aa where aa.IRP_SEQ = :irpSeq)) ";
	
	public List<ArquivosAnexosPesquisaGridVO> pesquisarArquivoAnexosPorNotificacaoTecnica(ArquivosAnexosPesquisaFiltroVO filtro,final Integer firstResult, final Integer maxResults){
		StringBuilder nativeSQL = new StringBuilder(2000);
		
		projectionsArquivosAnexos(nativeSQL);
		 

		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (AA.BPE_SEQ = BP.SEQ) ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(" PIRP ON  (BP.IRP_SEQ = PIRP.SEQ) OR (AA.irp_seq = pirp.seq) ");
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		nativeSQL.append(" AND AA.SEQ IN ( SELECT aa.seq  FROM   AGH.ptm_arquivos_anexos aa, AGH.ptm_nota_item_receb nir  WHERE  (  aa.not_seq = nir.not_seq  AND    nir.not_seq = :notSeq)  OR  ( aa.irp_seq = nir.irp_seq   AND    nir.not_seq = :notSeq) )");
		
		final SQLQuery query = createSQLQuery(nativeSQL.toString());
		query.setParameter("notSeq", filtro.getNotificacaoTecnica().getSeq());

		restrictionsParameters(filtro, query);
	
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		final List<ArquivosAnexosPesquisaGridVO> vos = resultTransformArquivosAnexos(query);
		return vos;
	}
	
	
	/**
	 * #44713 C5 Pesquisa geral Imagem 2 (COUNT) - se o campo notificação técnica preenchido
	 */
	public Long pesquisarArquivoAnexosPorNotificacaoTecnicaCount(ArquivosAnexosPesquisaFiltroVO filtro) {

		StringBuilder nativeSQL = new StringBuilder(2000);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (AA.BPE_SEQ = BP.SEQ) ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(" PIRP ON  (BP.IRP_SEQ = PIRP.SEQ) OR (AA.irp_seq = pirp.seq) ");
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		nativeSQL.append(" AND AA.SEQ IN ( SELECT aa.seq  FROM   AGH.ptm_arquivos_anexos aa, AGH.ptm_nota_item_receb nir  WHERE  (  aa.not_seq = nir.not_seq  AND    nir.not_seq = :notSeq)  OR  ( aa.irp_seq = nir.irp_seq   AND    nir.not_seq = :notSeq) )");
		final SQLQuery query = createSQLQuery(nativeSQL.toString());
		query.setParameter("notSeq", filtro.getNotificacaoTecnica().getSeq());
		restrictionsParameters(filtro, query);

		final List<ArquivosAnexosPesquisaGridVO> vos = resultTransformArquivosAnexos(query);

		if (vos != null && !vos.isEmpty()) {
			return (long) vos.size();
		} else {
			return 0L;
		}
	}

	/**
	 * #44713 C11 Pesquisa geral Imagem 2  -  se o campo patrmonio for preenchido
	 */
	public List<ArquivosAnexosPesquisaGridVO> pesquisarArquivoAnexosPorPatrimonio(ArquivosAnexosPesquisaFiltroVO filtro,final Integer firstResult, final Integer maxResults){
		StringBuilder nativeSQL = new StringBuilder(2000);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmNotaItemReceb.class.getAnnotation(Table.class).name()).append("  NIR ON (AA.NOT_SEQ = NIR.NOT_SEQ )  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (NIR.IRP_SEQ = BP.IRP_SEQ)  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(PIRPBP);
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		sqlUnionPesquisa(nativeSQL);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemProcesso.class.getAnnotation(Table.class).name()).append("  BPR ON (bpr.PRO_SEQ = aa.PRO_SEQ  )  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (bp.SEQ = bpr.BPE_SEQ  )  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(PIRPBP);
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		sqlUnionPesquisa(nativeSQL);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (bp.SEQ = aa.BPE_SEQ or bp.irp_seq = aa.irp_seq   )  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(PIRPBP);
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		nativeSQL.append(" and bp.NR_BEM = :nrBem ");
		final SQLQuery query = createSQLQuery(nativeSQL.toString());
		query.setParameter("nrBem", filtro.getPatrimonio().getNumeroBem());
		restrictionsParameters(filtro, query);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		final List<ArquivosAnexosPesquisaGridVO> vos = resultTransformArquivosAnexos(query);
		return vos;
	}


	private void sqlUnionPesquisa(StringBuilder nativeSQL) {
		nativeSQL.append(" and bp.NR_BEM = :nrBem union ");
	}
	
	/**
	 * #44713 C11 Pesquisa geral Imagem 2 (Count) - se o campo patrmonio for preenchido
	 */
	public Long pesquisarArquivoAnexosPorPatrimonioCount(ArquivosAnexosPesquisaFiltroVO filtro){
		StringBuilder nativeSQL = new StringBuilder(2000);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmNotaItemReceb.class.getAnnotation(Table.class).name()).append("  NIR ON (AA.NOT_SEQ = NIR.NOT_SEQ )  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (NIR.IRP_SEQ = BP.IRP_SEQ)  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(PIRPBP);
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		sqlUnionPesquisa(nativeSQL);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemProcesso.class.getAnnotation(Table.class).name()).append("  BPR ON (bpr.PRO_SEQ = aa.PRO_SEQ  )  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (bp.SEQ = bpr.BPE_SEQ  )  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(PIRPBP);
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		sqlUnionPesquisa(nativeSQL);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (bp.SEQ = aa.BPE_SEQ or bp.irp_seq = aa.irp_seq   )  ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(PIRPBP);
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		nativeSQL.append(" and bp.NR_BEM = :nrBem ");
		final SQLQuery query = createSQLQuery(nativeSQL.toString());
		query.setParameter("nrBem", filtro.getPatrimonio().getNumeroBem());
		restrictionsParameters(filtro, query);
		final List<ArquivosAnexosPesquisaGridVO> vos = resultTransformArquivosAnexos(query);
		
		if (vos != null && !vos.isEmpty()) {
			return (long) vos.size();
		} else {
			return 0L;
		}
	}
	/**
	 * #44713 C12 Pesquisa geral Imagem 2  o campo recebimento for preenchido
	 */
	public List<ArquivosAnexosPesquisaGridVO> pesquisarArquivosAnexosPorRecebimento(ArquivosAnexosPesquisaFiltroVO filtro,Long irpSeq,final Integer firstResult, final Integer maxResults, String orderProperty, boolean asc){
		StringBuilder nativeSQL = new StringBuilder(2000);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append(" BP ON (AA.BPE_SEQ = BP.SEQ) ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append("  PIRP ON  (BP.IRP_SEQ = PIRP.SEQ) OR (AA.irp_seq = pirp.seq) ");
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		nativeSQL.append(SELECAO)
				 .append(UNION);
		checarOrdenacao(orderProperty, asc, nativeSQL);
		final SQLQuery query = createSQLQuery(nativeSQL.toString());
		query.setParameter("irpSeq", irpSeq);
		restrictionsParameters(filtro, query);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		query.setMaxResults(maxResults);

		final List<ArquivosAnexosPesquisaGridVO> vos = resultTransformArquivosAnexos(query);
		return vos;
	}


	private void checarOrdenacao(String orderProperty, boolean asc,
			StringBuilder nativeSQL) {
		if (StringUtils.isNotEmpty(orderProperty)) {
			String valorColuna = StringUtils.EMPTY;
			switch (Integer.valueOf(orderProperty)) {
			case 1:
				valorColuna = "SIA.IPF_PFR_LCT_NUMERO";
				break;
			case 2:
				valorColuna = "SLC.NUMERO";
				break;
			case 3:
				valorColuna = "DFE.NUMERO";
				break;
			case 4:
				valorColuna = "BP.NR_BEM";
				break;			
			case 6:
				valorColuna = "AA.TIPO_PROCESSO";
				break;
			case 7:
				valorColuna = "AA.TIPO_DOCUMENTO";
				break;
			case 8:
				valorColuna = "AA.DT_CRIADO_EM";
				break;
			case 9:
				valorColuna = "AA.DT_ALTERACAO";
				break;
			case 10:
				valorColuna = "AA.SER_MATRICULA";
				break;
			case 11:
				valorColuna = "AA.SER_MATRICULA_ALTERACAO";		
				break;
			default:
				break;
			}
			nativeSQL.append(" ORDER BY ").append(valorColuna);
			if (!asc) {
				nativeSQL.append(" DESC ");
			}else{
				nativeSQL.append(" ASC ");
			}
		}
	}
	/**
	 * #44713 C12 Pesquisa geral Imagem 2  o campo recebimento for preenchido
	 */
	public Long pesquisarArquivoAnexosPorRecebimentoCount(ArquivosAnexosPesquisaFiltroVO filtro, Long irpSeq){

		StringBuilder nativeSQL = new StringBuilder(2000);
		projectionsArquivosAnexos(nativeSQL);
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append(" BP ON (AA.BPE_SEQ = BP.SEQ) ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append("  PIRP ON  (BP.IRP_SEQ = PIRP.SEQ) OR (AA.irp_seq = pirp.seq) ");
		joinsArquivosAnexos(nativeSQL);
		restrictionsArquivosAnexos(nativeSQL, filtro);
		nativeSQL.append(SELECAO)
		         .append(UNION);
		final SQLQuery query = createSQLQuery(nativeSQL.toString());
		query.setParameter("irpSeq", irpSeq);
		restrictionsParameters(filtro, query);
		
		final List<ArquivosAnexosPesquisaGridVO> vos = resultTransformArquivosAnexos(query);

		if (vos != null && !vos.isEmpty()) {
			return (long) vos.size();
		} else {
			return 0L;
		}
	}
	

	/**
	 * OBS: restrição compartilhada pelas consultas C5,C12,C13 
	 * @param filtro
	 * @param query
	 */
	private void restrictionsParameters(ArquivosAnexosPesquisaFiltroVO filtro, final SQLQuery query) {
		parametersParteUm(query, filtro);
		parametersParteDois(query, filtro);
		//INI
		parametersDatas(query, filtro, filtro.getDtIniInclusao(),filtro.getDtFimInclusao(),true);
		//FIM
		parametersDatas(query, filtro, filtro.getDtIniUltAlt(),filtro.getDtFimUltAlt(),false);
	}
	
	/**
	 * OBS* Join compartilhado pelas consultas C5,C12,C13 
	 * @param nativeSQL
	 * @return
	 */
	private StringBuilder joinsArquivosAnexos(StringBuilder nativeSQL){          
		nativeSQL.append(LEFTJOINAGH).append(PONTO).append(SceItemRecebProvisorio.class.getAnnotation(Table.class).name()).append("  IRP ON (PIRP.SCE_NRP_SEQ = IRP.NRP_SEQ AND PIRP.SCE_NRO_ITEM = IRP.NRO_ITEM)   ")
		.append(LEFTJOINAGH).append(PONTO).append(ScoFaseSolicitacao.class.getAnnotation(Table.class).name()).append("  FSC ON (FSC.IAF_AFN_NUMERO = IRP.PEA_IAF_AFN_NUMERO AND FSC.IND_EXCLUSAO = 'N' AND  FSC.IAF_NUMERO = IRP.PEA_IAF_NUMERO)  ")
		.append(LEFTJOINAGH).append(PONTO).append(ScoItemAutorizacaoForn.class.getAnnotation(Table.class).name()).append("  SIA ON (IRP.PEA_IAF_AFN_NUMERO = SIA.AFN_NUMERO AND FSC.IAF_NUMERO = SIA.NUMERO)  ")
		.append(LEFTJOINAGH).append(PONTO).append(SceNotaRecebProvisorio.class.getAnnotation(Table.class).name()).append("  NRP ON (NRP.SEQ = PIRP.SCE_NRP_SEQ)  ")
		.append(LEFTJOINAGH).append(PONTO).append(SceDocumentoFiscalEntrada.class.getAnnotation(Table.class).name()).append("  DFE ON (NRP.DFE_SEQ = DFE.SEQ ) ")
		.append(LEFTJOINAGH).append(PONTO).append(ScoSolicitacaoDeCompra.class.getAnnotation(Table.class).name()).append("   SLC ON (SLC.NUMERO = FSC.SLC_NUMERO ) ")
		.append(LEFTJOINAGH).append(PONTO).append(ScoMaterial.class.getAnnotation(Table.class).name()).append("   MAT ON (MAT.CODIGO = SLC.MAT_CODIGO)  ")
		.append(LEFTJOINAGH).append(PONTO).append(RapServidores.class.getAnnotation(Table.class).name()).append("  UINC ON (AA.SER_MATRICULA = UINC.MATRICULA AND AA.SER_VIN_CODIGO = UINC.VIN_CODIGO) ")
		.append(LEFTJOINAGH).append(PONTO).append(RapPessoasFisicas.class.getAnnotation(Table.class).name()).append("   PESINC ON (UINC.PES_CODIGO = PESINC.CODIGO) ")
		.append(LEFTJOINAGH).append(PONTO).append(RapServidores.class.getAnnotation(Table.class).name()).append("  UALT ON (AA.SER_MATRICULA_ALTERACAO = UALT.MATRICULA AND AA.SER_VIN_CODIGO_ALTERACAO = UALT.VIN_CODIGO)  ")
		.append(LEFTJOINAGH).append(PONTO).append(RapPessoasFisicas.class.getAnnotation(Table.class).name()).append("   PESALT ON (UALT.PES_CODIGO = PESALT.CODIGO) ");
		return nativeSQL;
	}
	

	/**
	 * 
	 * #44713 Filtro comun para consultas da tela arquivosAnexosList 
	 * OBS* Restrição compartilhada pelas consultas C5,C12,C13 
	 * @param criteria
	 * @param filtro
	 */
	private void restrictionsArquivosAnexos(StringBuilder nativeSQL,ArquivosAnexosPesquisaFiltroVO filtro){
		nativeSQL.append(" WHERE 1 = 1 ");	
		restrictionsParteUm(nativeSQL, filtro);
		//DT_INCLUSAO
		restrictionsDatas(nativeSQL, filtro.getDtIniInclusao(),filtro.getDtFimInclusao(),true);
		//DT ALTTERACAO
		restrictionsDatas(nativeSQL, filtro.getDtIniUltAlt(),filtro.getDtFimUltAlt(),false);
		restrictionsParteDois(nativeSQL, filtro);
	}

	private void restrictionsParteDois(StringBuilder nativeSQL, ArquivosAnexosPesquisaFiltroVO filtro) {
		if(filtro.getNotaFiscal() != null){
			nativeSQL.append("  and dfe.numero = :dfeSeq ");
		}
		
		if(filtro.getDescricaoTipoDocumento()!=null && !StringUtils.isEmpty(filtro.getDescricaoTipoDocumento()) ){
			if(isOracle()){
				nativeSQL.append("  and lower(aa.Tipo_documento_outros) like '%").append(StringUtils.lowerCase(filtro.getDescricaoTipoDocumento())).append("%'");
			}else{
				nativeSQL.append("  and aa.Tipo_documento_outros ilike '%").append(StringUtils.lowerCase(filtro.getDescricaoTipoDocumento())).append("%'");
			}
		}
		if(filtro.getArquivo()!=null && !StringUtils.isEmpty(filtro.getArquivo())){
			if(isOracle()){
				nativeSQL.append("  and lower(aa.arquivo) like '%").append(StringUtils.lowerCase(filtro.getArquivo())).append("%'");
			}else{
				nativeSQL.append("  and aa.arquivo ilike '%").append(StringUtils.lowerCase(filtro.getArquivo())).append("%'");
			}
			
		}
		if(filtro.getUsuarioInclusao()!=null){
			nativeSQL.append("  and aa.SER_MATRICULA = :ser_matricula_inc ");
			nativeSQL.append("  and aa.SER_VIN_CODIGO = :ser_vin_codigo_inc ");
		}
		if(filtro.getUsuarioUltimaAlteracao()!=null){
			nativeSQL.append("  and aa.ser_matricula_alteracao  = :ser_matricula_alt ");
			nativeSQL.append("  and aa.ser_vin_codigo_alteracao  = :ser_vin_codigo_alt ");
		}
		if(filtro.getEsl()!=null){
			nativeSQL.append("  and irp.esl_seq = :esl ");
		}
	}

	private void restrictionsParteUm(StringBuilder nativeSQL, ArquivosAnexosPesquisaFiltroVO filtro) {
		if(filtro.getNotaFiscal()!=null){
			nativeSQL.append("  and	dfe.numero = :dfeSeq ");	
		}
		if(filtro.getSc()!=null){
			nativeSQL.append("	and slc.numero = :sc ");
		}
		if(filtro.getNroAf()!=null){
			nativeSQL.append("	and sia.IPF_PFR_LCT_NUMERO = :nroAf ");
		}
		if(filtro.getTipoDocumento()!= null){
			nativeSQL.append("	and aa.TIPO_DOCUMENTO = :tipoDocumento ");
		}
		if(filtro.getTipoProcesso()!=null){
			nativeSQL.append("	and aa.tipo_processo = :tipoProcesso ");
		}
		if(filtro.getDescricaoArquivoAnexo()!=null && !StringUtils.isEmpty(filtro.getDescricaoArquivoAnexo())){
			
			if(isOracle()){
				nativeSQL.append("	and lower(aa.DESCRICAO) like '%").append(StringUtils.lowerCase(filtro.getDescricaoArquivoAnexo())).append("%'");
			}else{
				nativeSQL.append("	and aa.DESCRICAO ilike '%").append(StringUtils.lowerCase(filtro.getDescricaoArquivoAnexo())).append("%'");
			}
			
		}
		if(filtro.getMaterial()!=null){
			nativeSQL.append("	and mat.codigo = :material");
		}
		
		if(filtro.getAceiteTecnico() != null){
			nativeSQL.append(" AND AA.AVT_SEQ = :aceiteTecnico  ");
		}
	}
	
	private void parametersParteUm(SQLQuery query ,  ArquivosAnexosPesquisaFiltroVO filtro){
		if(filtro.getNotaFiscal() != null){
			query.setParameter("dfeSeq", filtro.getNotaFiscal());
		}
		if(filtro.getUsuarioInclusao()!=null){
			query.setParameter("ser_matricula_inc", filtro.getUsuarioInclusao().getId().getMatricula());
			query.setParameter("ser_vin_codigo_inc", filtro.getUsuarioInclusao().getId().getVinCodigo());
		}
		if(filtro.getUsuarioUltimaAlteracao()!=null){
			query.setParameter("ser_matricula_alt", filtro.getUsuarioUltimaAlteracao().getId().getMatricula());
			query.setParameter("ser_vin_codigo_alt", filtro.getUsuarioUltimaAlteracao().getId().getVinCodigo());
		}
		if(filtro.getEsl()!=null){
			query.setParameter("esl", filtro.getEsl());
		}
		if(filtro.getAceiteTecnico() != null){
			query.setParameter("aceiteTecnico", filtro.getAceiteTecnico());
		}
	}
	private void parametersParteDois(SQLQuery query ,  ArquivosAnexosPesquisaFiltroVO filtro){
		
		if(filtro.getNotaFiscal()!=null){
			query.setParameter("dfeSeq", filtro.getNotaFiscal());
		}
		if(filtro.getSc()!=null){
			query.setParameter("sc", filtro.getSc());
		}
		if(filtro.getNroAf()!=null){
			query.setParameter("nroAf", filtro.getNroAf());
		}
		if(filtro.getTipoDocumento()!= null){
			query.setParameter("tipoDocumento", filtro.getTipoDocumento().getCodigo());
		}
		if(filtro.getTipoProcesso()!=null){
			query.setParameter("tipoProcesso", filtro.getTipoProcesso().getCodigo());
		}
		if(filtro.getMaterial()!=null){
			query.setParameter("material", filtro.getMaterial());
		}
	}
	
	private void parametersDatas(SQLQuery query,ArquivosAnexosPesquisaFiltroVO filtro,Date inicio, Date fim,boolean isInicio){
		if(inicio != null && fim ==null){
			if(isInicio){
				query.setParameter("dtInicio", inicio);
			}else{
				query.setParameter("dtInicioAlteracao", inicio);
			}
			
		}else if(inicio == null && fim != null){
			Calendar calendarData = Calendar.getInstance();
			calendarData.setTime(fim);
			calendarData.set(Calendar.SECOND, 59);
			fim = calendarData.getTime();
			if(isInicio){
				query.setParameter("dtFim", fim);
			}else{
				query.setParameter("dtFimAlteracao", fim);
			}
			
		}else if(inicio != null && fim != null){
			if(isInicio){
				Calendar calendarData = Calendar.getInstance();
				calendarData.setTime(fim);
				calendarData.set(Calendar.SECOND, 59);
				fim = calendarData.getTime();
				query.setParameter("dtInicio", inicio);
				query.setParameter("dtFim", fim);
			}else{
				Calendar calendarData = Calendar.getInstance();
				calendarData.setTime(fim);
				calendarData.set(Calendar.SECOND, 59);
				fim = calendarData.getTime();
				query.setParameter("dtInicioAlteracao", inicio);
				query.setParameter("dtFimAlteracao", fim);
			}
		}
	}
	
	/**
	 * #44713 Restrição de intervalos de datas da tela arquivosAnexosList
	 * @param criteria
	 * @param inicio
	 * @param fim
	 * @param isInicio
	 */
	
	private void restrictionsDatas(StringBuilder nativeSQL,Date inicio, Date fim,boolean isInicio){
		if(inicio != null && fim ==null){
			if(isInicio){
				nativeSQL.append("	and aa.dt_criado_em  >= :dtInicio");
			}else{
				nativeSQL.append("	and aa.dt_alteracao >= :dtInicioAlteracao");
			}
			
		}else if(inicio == null && fim != null){
			if(isInicio){
				nativeSQL.append("	and aa.dt_criado_em  <= :dtFim");
			}else{
				nativeSQL.append("	and aa.dt_alteracao <= :dtFimAlteracao");
			}
			
		}else if(inicio != null && fim != null){
			if(isInicio){
				nativeSQL.append("	and aa.dt_criado_em  between  :dtInicio and :dtFim");
			}else{
				nativeSQL.append("	and aa.dt_alteracao between  :dtInicioAlteracao and :dtFimAlteracao");
			}
		}
	}
	/**
	 * OBS* Result Transform Compartilhado pelas consultas C5 , C12, C13
	 * @param query
	 * @return
	 */
	private List<ArquivosAnexosPesquisaGridVO> resultTransformArquivosAnexos(final SQLQuery query) {
		
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.AA_SEQ.toString(), LongType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.NRO_BEM.toString(), LongType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.TIPO_PROCESSO.toString(), IntegerType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.TIPO_DOCUMENTO.toString(),IntegerType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.ARQUIVO.toString(), StringType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.DESCRICAO.toString(), StringType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.CRIADO_EM.toString(), DateType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.MATRICULA.toString(), IntegerType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.VIN_CODIGO.toString(), ShortType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.NRO_AF.toString(), IntegerType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.DFE_SEQ.toString(), LongType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.SC.toString(), IntegerType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.DT_ULT_ALTERA.toString(), DateType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.VIN_CODIGO_ALTERACAO.toString(), ShortType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.NOME.toString(), StringType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.NOME_ALTERACAO.toString(), StringType.INSTANCE);
		query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.MATRICULA_ALTERACAO.toString(), IntegerType.INSTANCE);
		if(isOracle()){
			query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.HORA_INC.toString(), StringType.INSTANCE);
			query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.HORA_ALT.toString(), StringType.INSTANCE);
		}
		List<ArquivosAnexosPesquisaGridVO> vos = query.setResultTransformer(Transformers.aliasToBean(ArquivosAnexosPesquisaGridVO.class)).list();
		return vos;
	}
	
	
	/**
	 * #44713 Projeção das consultas da tela de anexosArquivosList
	 * OBS* Projeção compartilhada pelas consultas C5,C12,C13 
	 * @param criteria
	 */
	private void projectionsArquivosAnexos(StringBuilder nativeSQL){
		 nativeSQL.append(" SELECT aa.seq  AS ").append(ArquivosAnexosPesquisaGridVO.Fields.AA_SEQ.toString())
		.append(" ,bp.nr_bem AS ").append(ArquivosAnexosPesquisaGridVO.Fields.NRO_BEM.toString())
		.append(" ,aa.TIPO_PROCESSO AS ").append(ArquivosAnexosPesquisaGridVO.Fields.TIPO_PROCESSO.toString())
		.append(" ,aa.TIPO_DOCUMENTO AS ").append(ArquivosAnexosPesquisaGridVO.Fields.TIPO_DOCUMENTO.toString())
		.append(" ,aa.ARQUIVO as ").append(ArquivosAnexosPesquisaGridVO.Fields.ARQUIVO.toString())
		.append(" ,aa.DESCRICAO AS ").append(ArquivosAnexosPesquisaGridVO.Fields.DESCRICAO.toString())
		.append(" ,aa.dt_criado_em AS ").append(ArquivosAnexosPesquisaGridVO.Fields.CRIADO_EM.toString())
		.append(" ,aa.SER_MATRICULA AS ").append(ArquivosAnexosPesquisaGridVO.Fields.MATRICULA.toString())
		.append(" ,aa.SER_VIN_CODIGO AS ").append(ArquivosAnexosPesquisaGridVO.Fields.VIN_CODIGO.toString())
		.append(" ,sia.IPF_PFR_LCT_NUMERO AS ").append(ArquivosAnexosPesquisaGridVO.Fields.NRO_AF.toString())
		.append(" ,dfe.numero AS ").append(ArquivosAnexosPesquisaGridVO.Fields.DFE_SEQ.toString())
		.append(" ,slc.numero AS ").append(ArquivosAnexosPesquisaGridVO.Fields.SC.toString())
		.append(" ,aa.dt_alteracao AS ").append(ArquivosAnexosPesquisaGridVO.Fields.DT_ULT_ALTERA.toString())
		.append(" ,aa.ser_vin_codigo_alteracao AS ").append(ArquivosAnexosPesquisaGridVO.Fields.VIN_CODIGO_ALTERACAO.toString())
		.append(" ,pesinc.nome AS ").append(ArquivosAnexosPesquisaGridVO.Fields.NOME.toString())
		.append(" ,pesalt.nome AS ").append(ArquivosAnexosPesquisaGridVO.Fields.NOME_ALTERACAO.toString())
		.append(" ,aa.ser_matricula_alteracao AS ").append(ArquivosAnexosPesquisaGridVO.Fields.MATRICULA_ALTERACAO.toString());
		if(isOracle()){
			nativeSQL.append(",to_char(aa.dt_criado_em,'DD/MM/YYYY HH24:MI:SS') AS ").append(ArquivosAnexosPesquisaGridVO.Fields.HORA_INC.toString());
			nativeSQL.append(",to_char(aa.dt_alteracao,'DD/MM/YYY HH24:MI:SS') AS ").append(ArquivosAnexosPesquisaGridVO.Fields.HORA_ALT.toString());
		}
	}
	
	
	
	/**
	 * #50677
	 * C7 - Select usado para atualização  e visualização dos detalhes do anexo 
	 * (desatualizado) * utilizado na tela de edição
	 */
	public PtmArquivosAnexos obterVisualizacaoDetalhesAnexo(Long seqAnexo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmArquivosAnexos.class, AA);
		
		criteria.createAlias(AA + PONTO + PtmArquivosAnexos.Fields.PTM_ITEM_RECEB_PROVISORIOS.toString(), PIRP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AA + PONTO + PtmArquivosAnexos.Fields.PTM_BEM_PERMANENTES.toString(), "BP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AA + PONTO + PtmArquivosAnexos.Fields.PTM_NOTIFICACAO_TECNICA.toString(), "NT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP + PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), IRP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(IRP + PONTO + SceItemRecebProvisorio.Fields.SCO_FASE_SOLICITACAO.toString(), "FSC", JoinType.LEFT_OUTER_JOIN, 
									  Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.createAlias(IRP + PONTO + SceItemRecebProvisorio.Fields.ITEM_AUTORIZACAO_FORNECIMENTO.toString(), "SIA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP + PONTO + PtmItemRecebProvisorios.Fields.SCE_NOTA_RECEB_PROVISORIOS.toString(), "NRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AA + PONTO + PtmArquivosAnexos.Fields.SERVIDOR.toString(), SER, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AA + PONTO + PtmArquivosAnexos.Fields.SEQ.toString(), seqAnexo));
		
		criteria.addOrder(Order.asc(AA + PONTO + PtmArquivosAnexos.Fields.SEQ.toString()));
		

		return (PtmArquivosAnexos)executeCriteriaUniqueResult(criteria);
	}

	
	
	/**
	 * #50677
	 * C7 - Select usado para o detalhamento do arquivo
	 * 
	 */
	public DetalhamentoArquivosAnexosVO obterVisualizacaoDetalhamento(Long seqAnexo,String tipo) {
		StringBuilder nativeSQL = new StringBuilder(14126);			 
		nativeSQL.append(" SELECT aa.seq  AS ").append(DetalhamentoArquivosAnexosVO.Fields.SEQ.toString())
			.append(" ,bp.nr_bem AS ").append(DetalhamentoArquivosAnexosVO.Fields.PATRIMONIO.toString())
			.append(" ,aa.TIPO_PROCESSO AS ").append(DetalhamentoArquivosAnexosVO.Fields.TIPO_PROCESSO.toString())
			.append(" ,aa.TIPO_DOCUMENTO AS ").append(DetalhamentoArquivosAnexosVO.Fields.TIPO_DOCUMENTO.toString())
			.append(" ,aa.tipo_documento_outros  AS ").append(DetalhamentoArquivosAnexosVO.Fields.DESCRICAO_OUTROS.toString())
			.append(" ,aa.ARQUIVO as ").append(DetalhamentoArquivosAnexosVO.Fields.ARQUIVO.toString())
			.append(" ,aa.DESCRICAO AS ").append(DetalhamentoArquivosAnexosVO.Fields.DESCRICAO.toString())
			.append(" ,aa.dt_criado_em AS ").append(DetalhamentoArquivosAnexosVO.Fields.DT_INCLUSAO.toString())
			.append(" ,aa.SER_MATRICULA AS ").append(DetalhamentoArquivosAnexosVO.Fields.SER_MATRICULA.toString())
			.append(" ,aa.SER_VIN_CODIGO AS ").append(DetalhamentoArquivosAnexosVO.Fields.SER_VIN_CODIGO.toString())
			.append(" ,sia.IPF_PFR_LCT_NUMERO AS ").append(DetalhamentoArquivosAnexosVO.Fields.NRO_AF.toString())
			.append(" ,dfe.numero AS ").append(DetalhamentoArquivosAnexosVO.Fields.NOTA_FISCAL.toString())
			.append(" ,slc.numero AS ").append(DetalhamentoArquivosAnexosVO.Fields.SC.toString())
			.append(" ,aa.dt_alteracao AS ").append(DetalhamentoArquivosAnexosVO.Fields.DT_ALTERACAO.toString())
			.append(" ,aa.ser_vin_codigo_alteracao AS ").append(DetalhamentoArquivosAnexosVO.Fields.SER_VIN_CODIGO_ALTERACAO.toString())
			.append(" ,pesinc.nome AS ").append(DetalhamentoArquivosAnexosVO.Fields.NOME_INCLUSAO.toString())
			.append(" ,pesalt.nome AS ").append(DetalhamentoArquivosAnexosVO.Fields.NOME_ALTERACAO.toString())
			.append(" ,nt.seq AS ").append(DetalhamentoArquivosAnexosVO.Fields.SEQ_NOTA_TECNICA.toString())
			.append(" ,nt.DESCRICAO  AS ").append(DetalhamentoArquivosAnexosVO.Fields.DESC_NOTA_TECNICA.toString())
			.append(" ,mat.codigo  AS ").append(DetalhamentoArquivosAnexosVO.Fields.COD_MATERIAL.toString())
			.append(" ,pirp.SCE_NRP_SEQ   AS ").append(DetalhamentoArquivosAnexosVO.Fields.RECEB.toString())
			.append(" ,pirp.SCE_NRO_ITEM   AS ").append(DetalhamentoArquivosAnexosVO.Fields.ITEM.toString())
			.append(" ,irp.esl_seq    AS ").append(DetalhamentoArquivosAnexosVO.Fields.ESL.toString())
			.append(" ,aa.ser_matricula_alteracao AS ").append(DetalhamentoArquivosAnexosVO.Fields.SER_MATRICULA_ALTERACAO.toString())
			.append(" ,aa.AVT_SEQ    AS ").append(DetalhamentoArquivosAnexosVO.Fields.ACEITE_TECNICO.toString());
		
		if(isOracle()){
				nativeSQL.append(",to_char(aa.dt_criado_em,'DD/MM/YYYY HH24:MI:SS') AS ").append(ArquivosAnexosPesquisaGridVO.Fields.HORA_INC.toString())
				.append(",to_char(aa.dt_alteracao,'DD/MM/YYY HH24:MI:SS') AS ").append(ArquivosAnexosPesquisaGridVO.Fields.HORA_ALT.toString());
		}
		
		nativeSQL.append(FROMAGH).append(PONTO).append(PtmArquivosAnexos.class.getAnnotation(Table.class).name()).append(ESPACO_AA)
		.append(LEFTJOINAGH).append(PONTO).append(PtmBemPermanentes.class.getAnnotation(Table.class).name()).append("  BP ON (AA.BPE_SEQ = BP.SEQ) ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmNotificacaoTecnica.class.getAnnotation(Table.class).name()).append(" NT ON  (NT.SEQ = AA.NOT_SEQ) ")
		.append(LEFTJOINAGH).append(PONTO).append(PtmItemRecebProvisorios.class.getAnnotation(Table.class).name()).append(" PIRP ON (aa.IRP_seq = pirp.seq or bp.irp_seq = pirp.seq or nt.IRP_SEQ = pirp.seq) ")
		.append(LEFTJOINAGH).append(PONTO).append(SceItemRecebProvisorio.class.getAnnotation(Table.class).name()).append(" IRP ON  (pirp.SCE_NRP_SEQ = irp.NRP_SEQ and pirp.SCE_NRO_ITEM = irp.NRO_ITEM ) ")
		.append(LEFTJOINAGH).append(PONTO).append(ScoFaseSolicitacao.class.getAnnotation(Table.class).name()).append("  FSC ON (FSC.IAF_AFN_NUMERO = IRP.PEA_IAF_AFN_NUMERO AND FSC.IND_EXCLUSAO = 'N' AND  FSC.IAF_NUMERO = IRP.PEA_IAF_NUMERO)  ")
		.append(LEFTJOINAGH).append(PONTO).append(ScoItemAutorizacaoForn.class.getAnnotation(Table.class).name()).append("   SIA ON (IRP.PEA_IAF_AFN_NUMERO = SIA.AFN_NUMERO AND FSC.IAF_NUMERO = SIA.NUMERO)  ")
		.append(LEFTJOINAGH).append(PONTO).append(SceNotaRecebProvisorio.class.getAnnotation(Table.class).name()).append("   NRP ON (NRP.SEQ = PIRP.SCE_NRP_SEQ)  ")
		.append(LEFTJOINAGH).append(PONTO).append(SceDocumentoFiscalEntrada.class.getAnnotation(Table.class).name()).append("   DFE ON (NRP.DFE_SEQ = DFE.SEQ ) ")
		.append(LEFTJOINAGH).append(PONTO).append(ScoSolicitacaoDeCompra.class.getAnnotation(Table.class).name()).append("    SLC ON (SLC.NUMERO = FSC.SLC_NUMERO ) ")
		.append(LEFTJOINAGH).append(PONTO).append(ScoMaterial.class.getAnnotation(Table.class).name()).append("    MAT ON (MAT.CODIGO = SLC.MAT_CODIGO)  ")
		.append(LEFTJOINAGH).append(PONTO).append(RapServidores.class.getAnnotation(Table.class).name()).append("   UINC ON (AA.SER_MATRICULA = UINC.MATRICULA AND AA.SER_VIN_CODIGO = UINC.VIN_CODIGO) ")
		.append(LEFTJOINAGH).append(PONTO).append(RapPessoasFisicas.class.getAnnotation(Table.class).name()).append("    PESINC ON (UINC.PES_CODIGO = PESINC.CODIGO) ")
		.append(LEFTJOINAGH).append(PONTO).append(RapServidores.class.getAnnotation(Table.class).name()).append("   UALT ON (AA.SER_MATRICULA_ALTERACAO = UALT.MATRICULA AND AA.SER_VIN_CODIGO_ALTERACAO = UALT.VIN_CODIGO)  ")
		.append(LEFTJOINAGH).append(PONTO).append(RapPessoasFisicas.class.getAnnotation(Table.class).name()).append("    PESALT ON (UALT.PES_CODIGO = PESALT.CODIGO) ")
		.append(" WHERE aa.seq = :seq  order by AA.seq");
		final SQLQuery query = createSQLQuery(nativeSQL.toString());
		query.setParameter("seq", seqAnexo);
		final List<DetalhamentoArquivosAnexosVO> vos = resultTransformArquivosAnexosDetalhamento(query);
		if(!vos.isEmpty()){
			return vos.get(0);
		}
		return null;
	}

	private List<DetalhamentoArquivosAnexosVO> resultTransformArquivosAnexosDetalhamento(final SQLQuery query){
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.SEQ.toString(), LongType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.PATRIMONIO.toString(), LongType.INSTANCE);
		//
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.TIPO_PROCESSO.toString(), IntegerType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.TIPO_DOCUMENTO.toString(),IntegerType.INSTANCE);
		//
		
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.ARQUIVO.toString(), StringType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.DESCRICAO.toString(), StringType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.DT_INCLUSAO.toString(), DateType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.DT_ALTERACAO.toString(), DateType.INSTANCE);
		
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.NOME_INCLUSAO.toString(), StringType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.NOME_ALTERACAO.toString(), StringType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.SER_MATRICULA.toString(), IntegerType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.SER_VIN_CODIGO.toString(), ShortType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.SER_MATRICULA_ALTERACAO.toString(), IntegerType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.SER_VIN_CODIGO_ALTERACAO.toString(), ShortType.INSTANCE);
		
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.NRO_AF.toString(), IntegerType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.NOTA_FISCAL.toString(), LongType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.ESL.toString(), IntegerType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.NOTA_FISCAL.toString(), LongType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.COD_MATERIAL.toString(), IntegerType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.DESCRICAO_OUTROS.toString(), StringType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.SEQ_NOTA_TECNICA.toString(), LongType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.DESC_NOTA_TECNICA.toString(), StringType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.RECEB.toString(), IntegerType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.ITEM.toString(), IntegerType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.DESC_NOTA_TECNICA.toString(), StringType.INSTANCE);
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.SC.toString(), IntegerType.INSTANCE);	
		query.addScalar(DetalhamentoArquivosAnexosVO.Fields.ACEITE_TECNICO.toString(), IntegerType.INSTANCE);	
		if(isOracle()){
			query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.HORA_INC.toString(), StringType.INSTANCE);
			query.addScalar(ArquivosAnexosPesquisaGridVO.Fields.HORA_ALT.toString(), StringType.INSTANCE);
		}
		List<DetalhamentoArquivosAnexosVO> vos = query.setResultTransformer(Transformers.aliasToBean(DetalhamentoArquivosAnexosVO.class)).list();
		return vos;
	}
	
	
	/*
	 * Consulta utilizada para obter um anexo arquivo, a partir de um aceite tecnico
	 */
	public List<PtmArquivosAnexos> obterAnexoArquivoAceiteTecnico(Integer aceiteTecnico) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmArquivosAnexos.class, AA);
		
		criteria.createAlias(AA + PONTO + PtmArquivosAnexos.Fields.PTM_ITEM_RECEB_PROVISORIOS.toString(), PIRP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AA + PONTO + PtmArquivosAnexos.Fields.PTM_BEM_PERMANENTES.toString(), "BP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AA + PONTO + PtmArquivosAnexos.Fields.PTM_NOTIFICACAO_TECNICA.toString(), "NT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP + PONTO + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), IRP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(IRP + PONTO + SceItemRecebProvisorio.Fields.SCO_FASE_SOLICITACAO.toString(), "FSC", JoinType.LEFT_OUTER_JOIN, 
									  Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.createAlias(IRP + PONTO + SceItemRecebProvisorio.Fields.ITEM_AUTORIZACAO_FORNECIMENTO.toString(), "SIA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PIRP + PONTO + PtmItemRecebProvisorios.Fields.SCE_NOTA_RECEB_PROVISORIOS.toString(), "NRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NRP." + SceNotaRecebProvisorio.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AA + PONTO + PtmArquivosAnexos.Fields.SERVIDOR.toString(), SER, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AA + PONTO + PtmArquivosAnexos.Fields.AVT_SEQ.toString(), aceiteTecnico));
		
		criteria.addOrder(Order.asc(AA + PONTO + PtmArquivosAnexos.Fields.SEQ.toString()));
		

		return executeCriteria(criteria);
	}
	
}
