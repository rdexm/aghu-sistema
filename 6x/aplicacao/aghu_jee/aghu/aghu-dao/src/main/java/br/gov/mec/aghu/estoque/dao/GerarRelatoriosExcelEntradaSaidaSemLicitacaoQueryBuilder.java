package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.EntradaSaidaSemLicitacaoVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Query Builder para o Relatório ESSL - Entrada e Saida Sem Licitação.
 * 
 * @author romario.caldeira
 *
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class GerarRelatoriosExcelEntradaSaidaSemLicitacaoQueryBuilder extends QueryBuilder<DetachedCriteria> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6260703503809186649L;

	@Override
	protected DetachedCriteria createProduct() {
		return null;
	}

	@Override
	protected void doBuild(DetachedCriteria aProduct) {
		super.build();
	}

	
	/**
     * #34163 Monta primeira parte da consulta C1 
     * @param filtro     * @return
     */
    public DetachedCriteria montarEntradaSaidaESSLParte1(EntradaSaidaSemLicitacaoVO filtro){

	  DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class,"MAT");
	  
	  criteria.createAlias("MAT." + ScoMaterial.Fields.ITEM_ESL.toString(), "ISL", JoinType.INNER_JOIN);
	  criteria.createAlias("ISL." + SceItemEntrSaidSemLicitacao.Fields.ISL.toString(), "ISL1", JoinType.LEFT_OUTER_JOIN);
	  criteria.createAlias("ISL."+ SceItemEntrSaidSemLicitacao.Fields.SCE_ENTR_SAID_SEM_LIC_ORIGEM.toString(), "ESL1", JoinType.LEFT_OUTER_JOIN);
	  criteria.createAlias("ESL1."+ SceEntrSaidSemLicitacao.Fields.TIPO_MOVIMENTO.toString(), "TMV1", JoinType.LEFT_OUTER_JOIN);
	  criteria.createAlias("ISL." + SceItemEntrSaidSemLicitacao.Fields.MMT.toString(), "MMT", JoinType.INNER_JOIN);
	  criteria.createAlias("MMT." + SceMovimentoMaterial.Fields.ESL.toString(), "ESL", JoinType.INNER_JOIN);
	  criteria.createAlias("ESL." + SceEntrSaidSemLicitacao.Fields.TIPO_MOVIMENTO.toString(), "TMV", JoinType.INNER_JOIN);
	  	  
	  criteria.add(Restrictions.eqProperty("ESL." + SceEntrSaidSemLicitacao.Fields.SEQ.toString(), "ISL." + SceItemEntrSaidSemLicitacao.Fields.ESL_SEQ.toString()));
	  criteria.add(Restrictions.eq("ESL." + SceEntrSaidSemLicitacao.Fields.IND_ESTORNO.toString(), DominioSimNao.N.isSim()));
	  criteria.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), DominioSimNao.N.isSim()));
	  criteria.add(Restrictions.eq("TMV." + SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	  criteria.add(Restrictions.eq("ESL1." + SceEntrSaidSemLicitacao.Fields.IND_ESTORNO.toString(), DominioSimNao.N.isSim()));
	  criteria.add(Restrictions.eq("TMV1."+ SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	  
	  ProjectionList projList = Projections.projectionList();
	     projList.add(Projections.property("MAT." + ScoMaterial.Fields.GMT_CODIGO.toString()), EntradaSaidaSemLicitacaoVO.Fields.MATERIAL_CODIGO_GMT.toString());
	     projList.add(Projections.property("TMV." + SceTipoMovimento.Fields.SIGLA.toString()), EntradaSaidaSemLicitacaoVO.Fields.SIGLA_TMV.toString());
	     projList.add(Projections.property("TMV1."+ SceTipoMovimento.Fields.SIGLA.toString()), EntradaSaidaSemLicitacaoVO.Fields.SIGLA_TMV1.toString());
	     projList.add(Projections.property("ESL." + SceEntrSaidSemLicitacao.Fields.SEQ.toString()), EntradaSaidaSemLicitacaoVO.Fields.ESL_SEQ.toString());
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.ESL_SEQ.toString()), EntradaSaidaSemLicitacaoVO.Fields.ISL_ESL_SEQ.toString());
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.SLC_NUMERO.toString()), EntradaSaidaSemLicitacaoVO.Fields.ISL_SLC_NUMERO.toString());
	     projList.add(Projections.property("ESL." + SceEntrSaidSemLicitacao.Fields.DATA_GERACAO.toString()), EntradaSaidaSemLicitacaoVO.Fields.ESL_DATA_GERACAO.toString());
	     projList.add(Projections.property("ESL." + SceEntrSaidSemLicitacao.Fields.IND_ENCERRADO.toString()), EntradaSaidaSemLicitacaoVO.Fields.ESL_IND_ENCERRADO.toString());
	     projList.add(Projections.property("ESL." + SceEntrSaidSemLicitacao.Fields.IND_ESTORNO.toString()), EntradaSaidaSemLicitacaoVO.Fields.ESL_IND_ESTORNO.toString());
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.ALM_SEQ.toString()), EntradaSaidaSemLicitacaoVO.Fields.ISL_SEQ_ALMOXARIFADO.toString());
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.CODIGO_MATERIAL.toString()), EntradaSaidaSemLicitacaoVO.Fields.ISL_CODIGO_MATERIAL.toString());
	     projList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), EntradaSaidaSemLicitacaoVO.Fields.MATERIAL_NOME.toString());
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.CODIGO_UNIDADE_MEDIDA.toString()), EntradaSaidaSemLicitacaoVO.Fields.ISL_CODIGO_UNIDADE_MEDIDA.toString());
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.QUANTIDADE.toString()), EntradaSaidaSemLicitacaoVO.Fields.ISL_QUANTIDADE.toString());
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.QTDE_DEVOLVIDA.toString()), EntradaSaidaSemLicitacaoVO.Fields.ISL_QUANTIDADE_DEVOLVIDA.toString());
	     projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.VALOR.toString()), EntradaSaidaSemLicitacaoVO.Fields.MOVIMENTO_MATERIAIS_VALOR.toString());
	     projList.add(Projections.property("ESL." + SceEntrSaidSemLicitacao.Fields.DFE_SEQ.toString()), EntradaSaidaSemLicitacaoVO.Fields.ESL_DFE_SEQ.toString());
	     projList.add(Projections.property("ESL." + SceEntrSaidSemLicitacao.Fields.FRN_NUMERO.toString()), EntradaSaidaSemLicitacaoVO.Fields.ESL_FRN_NUMERO.toString());
	     projList.add(Projections.sqlProjection("null as " + EntradaSaidaSemLicitacaoVO.Fields.FEV_SEQ.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.FEV_SEQ.toString()}, new Type[]{IntegerType.INSTANCE}));
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.IAF_AFN_NUMERO.toString()), EntradaSaidaSemLicitacaoVO.Fields.IAF_AFN_NUMERO.toString());
	     projList.add(Projections.property("ISL." + SceItemEntrSaidSemLicitacao.Fields.IAF_NUMERO.toString()), EntradaSaidaSemLicitacaoVO.Fields.IAF_NUMERO.toString());
	     criteria.setProjection(projList);
         
	     if(filtro  != null){
	          if(filtro.getMmtDataComp() != null){
	 			  criteria.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), DateUtil.obterDataInicioCompetencia(filtro.getMmtDataComp()), DateUtil.obterDataFimCompetencia(filtro.getMmtDataComp())));
	          }
	          
	          if(filtro.getDataInicial() != null && filtro.getDataFinal() != null){
	 			  criteria.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), DateUtil.truncaData(filtro.getDataInicial()), DateUtil.truncaDataFim(filtro.getDataFinal())));
	          }
	          
	          if(filtro.getAdiantamentoAF()){
	 			  criteria.add(Restrictions.eq("ESL." + SceEntrSaidSemLicitacao.Fields.IND_ADIANTAMENTO_AF.toString(), "S"));
	          }
	          
	          if(filtro.getListaTipoMovimento() != null && filtro.getListaTipoMovimento().length != 0){
	 			  criteria.add(Restrictions.in("TMV." + SceTipoMovimento.Fields.SEQ.toString(), filtro.getListaTipoMovimento()));
	          }
	          
	          if(filtro.getListaTipoMovimentoOrigem() != null && filtro.getListaTipoMovimentoOrigem().length != 0){
	 			  criteria.add(Restrictions.in("TMV1." + SceTipoMovimento.Fields.SEQ.toString(), filtro.getListaTipoMovimentoOrigem()));
	          }
	          
	     }
	     criteria.setResultTransformer(Transformers.aliasToBean(EntradaSaidaSemLicitacaoVO.class));
	     criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.GMT_CODIGO.toString())).addOrder(Order.asc("TMV." + SceTipoMovimento.Fields.SIGLA.toString()));  
      return criteria;
    }
    
    /**
     * #34163 Monta segnuda parte da consulta C1 
     * @param filtro     * @return
     * @throws ApplicationBusinessException 
     */
    public DetachedCriteria montarEntradaSaidaESSLParte2(EntradaSaidaSemLicitacaoVO filtro, Short mmtTmvSeq, Short mmtTmvSeq2) throws ApplicationBusinessException{

    	DetachedCriteria criteria2 = DetachedCriteria.forClass(SceMovimentoMaterial.class,"MMT");
        
        criteria2.createAlias("MMT."+SceMovimentoMaterial.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
        criteria2.createAlias("MMT."+SceMovimentoMaterial.Fields.TIPO_MOVIMENTO.toString(), "TMV", JoinType.INNER_JOIN);
        
        criteria2.add(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString(), DominioSimNao.N.isSim()));
        criteria2.add(Restrictions.eq("TMV." + SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
        
        criteria2.add(Restrictions.or(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), mmtTmvSeq),
                      Restrictions.and(Restrictions.eq("MMT." + SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString(), mmtTmvSeq2), 
                                                 Restrictions.gt("MMT." + SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString(), 1))));
        
        ProjectionList projList = Projections.projectionList();
        projList.add(Projections.property("MAT." + ScoMaterial.Fields.GMT_CODIGO.toString()), EntradaSaidaSemLicitacaoVO.Fields.MATERIAL_CODIGO_GMT.toString());
        projList.add(Projections.property("TMV." + SceTipoMovimento.Fields.SIGLA.toString()), EntradaSaidaSemLicitacaoVO.Fields.SIGLA_TMV.toString());
        projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.NRO_DOC_GERACAO.toString()), EntradaSaidaSemLicitacaoVO.Fields.MMT_DOC_GERACAO.toString());
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.ESL_SEQ.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.ESL_SEQ.toString()}, new Type[]{IntegerType.INSTANCE}));
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.SIGLA_TMV1.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.SIGLA_TMV1.toString()}, new Type[]{IntegerType.INSTANCE}));
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.ISL_SLC_NUMERO.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.ISL_SLC_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
        projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.DATA_GERACAO.toString()), EntradaSaidaSemLicitacaoVO.Fields.MMT_DT_GERACAO.toString());
        projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.IND_ESTORNO.toString()), EntradaSaidaSemLicitacaoVO.Fields.MMT_IND_ESTORNO.toString());
        projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.ALMOXARIFADO_SEQ.toString()), EntradaSaidaSemLicitacaoVO.Fields.MMT_ALMOXARIFADO.toString());
        projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()), EntradaSaidaSemLicitacaoVO.Fields.MMT_MAT_CODIGO.toString());
        projList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), EntradaSaidaSemLicitacaoVO.Fields.MATERIAL_NOME.toString());
        projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.UNIDADE_MEDIDA.toString()), EntradaSaidaSemLicitacaoVO.Fields.MMT_UMD.toString());
        projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.QUANTIDADE.toString()), EntradaSaidaSemLicitacaoVO.Fields.MMT_QUANTIDADE.toString());
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.ISL_QUANTIDADE_DEVOLVIDA.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.ISL_QUANTIDADE_DEVOLVIDA.toString()}, new Type[]{StringType.INSTANCE}));
        projList.add(Projections.property("MMT." + SceMovimentoMaterial.Fields.VALOR.toString()), EntradaSaidaSemLicitacaoVO.Fields.MOVIMENTO_MATERIAIS_VALOR.toString());
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.ESL_DFE_SEQ.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.ESL_DFE_SEQ.toString()}, new Type[]{IntegerType.INSTANCE}));
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.ESL_FRN_NUMERO.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.ESL_FRN_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.FEV_SEQ.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.FEV_SEQ.toString()}, new Type[]{IntegerType.INSTANCE}));
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.IAF_AFN_NUMERO.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.IAF_AFN_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
        projList.add(Projections.sqlProjection(" null as " + EntradaSaidaSemLicitacaoVO.Fields.IAF_NUMERO.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.IAF_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
        criteria2.setProjection(projList);
        
        if(filtro  != null){
	          if(filtro.getMmtDataComp() != null){
	 			 criteria2.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString(), DateUtil.obterDataInicioCompetencia(filtro.getMmtDataComp()), DateUtil.obterDataFimCompetencia(filtro.getMmtDataComp())));
	          }
	          
	          if(filtro.getDataInicial() != null && filtro.getDataFinal() != null){
	        	  criteria2.add(Restrictions.between("MMT." + SceMovimentoMaterial.Fields.DATA_GERACAO.toString(), DateUtil.truncaData(filtro.getDataInicial()), DateUtil.truncaDataFim(filtro.getDataFinal())));
	          }
	     }
        try {
        	criteria2.setResultTransformer(new AliasToBeanConstructorResultTransformer(EntradaSaidaSemLicitacaoVO.class.getConstructor(Integer.class,
        			String.class, Integer.class, Integer.class, String.class, Integer.class, Date.class, Boolean.class, Short.class, Integer.class, String.class, 
        			String.class, Integer.class, Integer.class, BigDecimal.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class)));
        } catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}        
        criteria2.addOrder(Order.asc("MAT." + ScoMaterial.Fields.GMT_CODIGO.toString())).addOrder(Order.asc("TMV." + SceTipoMovimento.Fields.SIGLA.toString()));       
       return criteria2;
    }
    
    /**
     * Consulta Secundária para preencher corretamente os campos – FORNECEDOR(V_RAZAO_SOCIAL) e CNPJ( V_CNPJ) do relatório.
     * @param filtro     * @return
     */
    public DetachedCriteria listarCnpjRazaoSocial(EntradaSaidaSemLicitacaoVO objetoRetorno, Long retornoCount){
          
    	DetachedCriteria cnpjRazaoSocialCriteria = null;
          if(objetoRetorno != null){
                 if(objetoRetorno.getEslDfeSeq() != null){      
                        String sigla = objetoRetorno.getTmvSigla() != null ? objetoRetorno.getTmv1Sigla() != null ? objetoRetorno.getTmvSigla() + objetoRetorno.getTmv1Sigla() : ""  : "";  
                        if (!sigla.equals("CONSS")){
                               Integer v_dfe_seq = objetoRetorno.getEslDfeSeq();
                               
                               objetoRetorno.setVCnpj(retornoCount);
                               
                               if (objetoRetorno.getVcnpj() == 0) {
                                     objetoRetorno.setVCnpj(null);
                                     objetoRetorno.setVRazaoSocial(null);
                               } else{
                            	   cnpjRazaoSocialCriteria = consultaCnpjRazaoSocialDfeSeq(v_dfe_seq);
                               }
                       }
                        else if(objetoRetorno.getEslFrnNumero() != null){
                               
                               Integer v_frn_numero = objetoRetorno.getEslFrnNumero();  
                               cnpjRazaoSocialCriteria = consultaCnpjRazaoSocialFrnNumero(v_frn_numero);
                        }
                        else{
                               Integer v_dfe_seq = objetoRetorno.getEslDfeSeq();
                               cnpjRazaoSocialCriteria = consultaCnpjRazaoSocialDfeSeq(v_dfe_seq);
                        }
                 }
                 else if(objetoRetorno.getEslFrnNumero() != null){
                        
                	    Integer v_frn_numero = objetoRetorno.getEslFrnNumero();
                	    cnpjRazaoSocialCriteria = consultaCnpjRazaoSocialFrnNumero(v_frn_numero);

                 }else if(objetoRetorno.getFevSeq() != null){
                        
                        Integer v_fev_seq = objetoRetorno.getFevSeq();
                        cnpjRazaoSocialCriteria = consultaCnpjRazaoSocialFevSeq(v_fev_seq);

                 }else {
                        objetoRetorno.setVCnpj(null);
                        objetoRetorno.setVRazaoSocial(null);
                 }
                 
          }
          return cnpjRazaoSocialCriteria;    
    }
    
    private DetachedCriteria consultaCnpjRazaoSocialDfeSeq(Integer v_dfe_seq){
    	
       DetachedCriteria cnpj_RazaoSocial = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class,"DFE");
       cnpj_RazaoSocial.createAlias("DFE."+SceDocumentoFiscalEntrada.Fields.FORNECEDOR_EVENTUAL.toString(), "FEV", JoinType.LEFT_OUTER_JOIN);
       cnpj_RazaoSocial.createAlias("DFE."+SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
       cnpj_RazaoSocial.add(Restrictions.eq("DFE." + SceDocumentoFiscalEntrada.Fields.SEQ.toString(), v_dfe_seq));
       
       ProjectionList projListC2 = Projections.projectionList();
       projListC2.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FRN_CGC.toString());
       projListC2.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FRN_CPF.toString());
       projListC2.add(Projections.property("FEV." + SceFornecedorEventual.Fields.SEQ.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FEV_SEQ.toString());
       projListC2.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FRN_RAZAO.toString());
       projListC2.add(Projections.property("FEV." + SceFornecedorEventual.Fields.RAZAO_SOCIAL.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FEV_RAZAO.toString());
       cnpj_RazaoSocial.setProjection(projListC2);
       cnpj_RazaoSocial.setResultTransformer(Transformers.aliasToBean(EntradaSaidaSemLicitacaoVO.class));
       return cnpj_RazaoSocial;
    }
    
    private DetachedCriteria consultaCnpjRazaoSocialFrnNumero(Integer v_frn_numero){
    	
    	DetachedCriteria cnpj_RazaoSocial = DetachedCriteria.forClass(ScoFornecedor.class,"FRN");
        cnpj_RazaoSocial.add(Restrictions.eq("FRN." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), v_frn_numero));
        
        ProjectionList projListC2 = Projections.projectionList();
        projListC2.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FRN_CGC.toString());
        projListC2.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FRN_CPF.toString());
        projListC2.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FRN_RAZAO.toString());
        cnpj_RazaoSocial.setProjection(projListC2);
        cnpj_RazaoSocial.setResultTransformer(Transformers.aliasToBean(EntradaSaidaSemLicitacaoVO.class));
        return cnpj_RazaoSocial;
     }
    
    private DetachedCriteria consultaCnpjRazaoSocialFevSeq(Integer v_fev_seq){
    	
    	DetachedCriteria cnpjRazaoSocialCriteria = DetachedCriteria.forClass(SceFornecedorEventual.class,"FEV.");
        cnpjRazaoSocialCriteria.add(Restrictions.eq("FEV." + SceFornecedorEventual.Fields.SEQ.toString(), v_fev_seq));
        
        ProjectionList projListC2 = Projections.projectionList();
        projListC2.add(Projections.property("FEV." + SceFornecedorEventual.Fields.SEQ.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FEV_SEQ.toString());
        projListC2.add(Projections.property("FEV." + SceFornecedorEventual.Fields.RAZAO_SOCIAL.toString()), EntradaSaidaSemLicitacaoVO.Fields.C2_FEV_RAZAO.toString());
        cnpjRazaoSocialCriteria.setProjection(projListC2);
        cnpjRazaoSocialCriteria.setResultTransformer(Transformers.aliasToBean(EntradaSaidaSemLicitacaoVO.class));

        return cnpjRazaoSocialCriteria;
     }
    
    /**
     * Consulta Secundária para preencher corretamente o campo NRO_NF
     * 
     * @param objetoRetorno
     * @return
     */
    public DetachedCriteria consultaC3(EntradaSaidaSemLicitacaoVO objetoRetorno){

    	DetachedCriteria campoNRO_NF = DetachedCriteria.forClass(SceEntrSaidSemLicitacao.class,"ESL");
    	campoNRO_NF.createAlias("ESL." + SceEntrSaidSemLicitacao.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.INNER_JOIN);
    	
    	campoNRO_NF.add(Restrictions.eq("ESL." + SceEntrSaidSemLicitacao.Fields.SEQ.toString(), objetoRetorno.getEslSeq()));
        
        ProjectionList projListC3 = Projections.projectionList();
        projListC3.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), EntradaSaidaSemLicitacaoVO.Fields.C3_DFE_NUMERO.toString());
        campoNRO_NF.setProjection(projListC3);
        campoNRO_NF.setResultTransformer(Transformers.aliasToBean(EntradaSaidaSemLicitacaoVO.class));

        return campoNRO_NF;
    }
    
    /**
     * Consulta Secundária para preencher corretamente o campo AF
     * 
     * @param objetoRetorno
     * @return
     */
    public DetachedCriteria consultaC4(EntradaSaidaSemLicitacaoVO objetoRetorno){

    	DetachedCriteria campo_AF = DetachedCriteria.forClass(ScoAutorizacaoForn.class,"AFN");
	    	
    	campo_AF.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), objetoRetorno.getIafAfnNumero()));
        
        ProjectionList projListC4 = Projections.projectionList();
        projListC4.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), EntradaSaidaSemLicitacaoVO.Fields.C4_AFN_PFR_LCT_NUMERO.toString());
        projListC4.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), EntradaSaidaSemLicitacaoVO.Fields.C4_AFN_NRO_COMPLEMENTO.toString());
        campo_AF.setProjection(projListC4);
        campo_AF.setResultTransformer(Transformers.aliasToBean(EntradaSaidaSemLicitacaoVO.class));

        return campo_AF;
    }
    
    /**
     * Consulta Secundária para preencher corretamente o campo ITEM AF
     * 
     * @param objetoRetorno
     * @return
     */
    public DetachedCriteria consultaC5(EntradaSaidaSemLicitacaoVO objetoRetorno){

		DetachedCriteria campoITEM_AF = DetachedCriteria.forClass(ScoFaseSolicitacao.class,"FSL");
		
		campoITEM_AF.add(Restrictions.eq("FSL." + ScoFaseSolicitacao.Fields.IAFAFNNUMERO.toString(), objetoRetorno.getIafAfnNumero()));
		campoITEM_AF.add(Restrictions.eq("FSL." + ScoFaseSolicitacao.Fields.IAFNUMERO.toString(), objetoRetorno.getIafNumero()));
		campoITEM_AF.add(Restrictions.eq("FSL." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
	    
	    ProjectionList projListC5 = Projections.projectionList();
	    projListC5.add(Projections.sqlProjection("( select fsl1.ITL_NUMERO from agh.SCO_FASES_SOLICITACOES fsl1 where fsl1.SLC_NUMERO = this_.SLC_NUMERO and fsl1.ITL_LCT_NUMERO is not null and fsl1.ind_exclusao = 'N' ) as " 
	            + EntradaSaidaSemLicitacaoVO.Fields.C5_FSL1_ITL_NUMERO.toString(), new String[]{EntradaSaidaSemLicitacaoVO.Fields.C5_FSL1_ITL_NUMERO.toString()}, new Type[]{ShortType.INSTANCE}));
	    campoITEM_AF.setProjection(projListC5);
	    campoITEM_AF.setResultTransformer(Transformers.aliasToBean(EntradaSaidaSemLicitacaoVO.class));
	
        return campoITEM_AF;
    }
}
