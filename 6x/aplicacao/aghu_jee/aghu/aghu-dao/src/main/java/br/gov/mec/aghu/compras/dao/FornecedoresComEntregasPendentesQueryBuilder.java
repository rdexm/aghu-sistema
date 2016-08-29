package br.gov.mec.aghu.compras.dao;

import java.math.BigDecimal;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.vo.EntregasPendentesVO;
import br.gov.mec.aghu.compras.vo.FornecedoresComEntregasPendentesVO;
import br.gov.mec.aghu.compras.vo.ItensPendentesEntregaVO;
import br.gov.mec.aghu.dominio.DominioItemNotaPreRecebimento;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceE660IncForn;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemNotaPreRecebimento;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoCumXProgrEntrega;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;


public class FornecedoresComEntregasPendentesQueryBuilder {

	private static final String PEAPONTO = "PEA.";
	private static final String IPRPONTO = "IPR.";
	private static final String SRVPONTO = "SRV.";
	private static final String MATPONTO = "MAT.";
	private static final String MATSEMPONTO = "MAT";
	private static final String SLCSEMPONTO = "SLC";
	private static final String ISLSEMPONTO = "ISL";
	private static final String SRVSEMPONTO = "SRV";
	private static final String SLSSEMPONTO = "SLS";
	private static final String LCTSEMPONTO = "LCT";
	private static final String ESLSEMPONTO = "ESL";
	private static final String FRNSEMPONTO = "FRN";
	private static final String SLCPONTO = "SLC.";
	private static final String IAFPONTO = "IAF.";
	private static final String FSC2SEMPONTO = "FSC2";
	private static final String FSC1SEMPONTO = "FSC1";
	private static final String IAFSEMPONTO = "IAF";
	private static final String AFNSEMPONTO = "AFN";
	private static final String ESLPONTO = "ESL.";
	private static final String FSC1PONTO = "FSC1.";
	private static final String FSC2PONTO = "FSC2.";
	private static final String FRNPONTO = "FRN.";
	private static final String AFNPONTO = "AFN.";

	public DetachedCriteria montarCriteriaListarFornecedoresComEntregasPendentes(Integer arg0, Integer arg1, String arg2, boolean arg3,
				FornecedoresComEntregasPendentesVO filtro, boolean order)  throws ApplicationBusinessException{
			
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, AFNSEMPONTO);
			
			criteria.createAlias(AFNPONTO + ScoAutorizacaoForn.Fields.SCO_FORNECEDOR.toString(), FRNSEMPONTO)
					.createAlias(AFNPONTO + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), IAFSEMPONTO, JoinType.LEFT_OUTER_JOIN)
					.createAlias(IAFPONTO + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), FSC1SEMPONTO, JoinType.LEFT_OUTER_JOIN)
					.createAlias(FRNPONTO + ScoFornecedor.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", JoinType.LEFT_OUTER_JOIN)
					.createAlias(FSC1PONTO + ScoFaseSolicitacao.Fields.FSL2, FSC2SEMPONTO, JoinType.LEFT_OUTER_JOIN)
					.createAlias(FSC2PONTO + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS, SLCSEMPONTO, JoinType.LEFT_OUTER_JOIN)
					.createAlias(SLCPONTO + ScoSolicitacaoDeCompra.Fields.MATERIAL, MATSEMPONTO, JoinType.LEFT_OUTER_JOIN)
					.createAlias(IAFPONTO + ScoItemAutorizacaoForn.Fields.SCO_CUM_X_PROG.toString(), "CPE", JoinType.LEFT_OUTER_JOIN);
			
			if(filtro.getMaterial() != null && filtro.getMaterial().getCodigo() != null ){
				criteria.add(Restrictions.eq(MATPONTO+ScoMaterial.Fields.CODIGO.toString(), filtro.getMaterial().getCodigo()));
			}
			
			criteria.add(Restrictions.eq(FSC2PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
			criteria.add(Restrictions.isNotNull(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()));
			
			criteria.add(Restrictions.or(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
										 Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
			criteria.add(Restrictions.or(Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
					 					 Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
			criteria.add(Restrictions.eq(FSC1PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		
			criteria.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.NUMERO.toString()), FornecedoresComEntregasPendentesVO.Fields.FORNECEDOR_NUMERO.toString())
					.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), FornecedoresComEntregasPendentesVO.Fields.FORNECEDOR_RAZAO_SOCIAL.toString())
					.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.CGC.toString()), FornecedoresComEntregasPendentesVO.Fields.CNPJ.toString())
					.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.CPF.toString()), FornecedoresComEntregasPendentesVO.Fields.CPF.toString())));
			
			try {
				criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(FornecedoresComEntregasPendentesVO.class.getConstructor(
						Integer.class, String.class, Long.class, Long.class)));
			} catch (NoSuchMethodException e) {
				throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
			} catch (SecurityException e) {
				throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
			}
			
			filtrosConsultaFornecedores(filtro, criteria);
			if(filtro.getFornecedor() != null && filtro.getFornecedor().getNumero() != null){
				criteria.add(Restrictions.eq(FRNPONTO+ScoFornecedor.Fields.NUMERO.toString(), filtro.getFornecedor().getNumero()));
			}
			if(filtro.getNotaFiscal() != null && filtro.getNotaFiscal().getSeq() != null ){
				criteria.add(Restrictions.eq("DFE."+SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), filtro.getNotaFiscal().getNumero()));
			}
			if(!order){
				criteria.addOrder(Order.asc(FRNPONTO+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
			}
			
		return criteria;
	}

	private void filtrosConsultaFornecedores(
			FornecedoresComEntregasPendentesVO filtro, DetachedCriteria criteria) {
		if(filtro.getCodigoBarra() != null ){
			criteria.add(Restrictions.eq("DFE."+SceDocumentoFiscalEntrada.Fields.CHAVE_CODIGO_BARRAS.toString(), filtro.getCodigoBarra()));
		} 
		if(filtro.getAf() != null){
			criteria.add(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getAf()));
		}
		if(filtro.getCp() != null){
			criteria.add(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getCp()));
		}
		if(filtro.getCum() != null){
			criteria.add(Restrictions.eq("CPE."+ScoCumXProgrEntrega.Fields.SEQ.toString(), filtro.getCum()));
		}
	}
	
	public DetachedCriteria unionParaEslEMaterial(FornecedoresComEntregasPendentesVO filtro) throws ApplicationBusinessException {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedor.class, FRNSEMPONTO);
		
		criteria.createAlias(FRNPONTO+ScoFornecedor.Fields.ENTRADA_SAIDA_SEM_LICIT.toString(), ESLSEMPONTO);
		criteria.createAlias(ESLPONTO+SceEntrSaidSemLicitacao.Fields.ITEM_ENT_SAIDA_SEM_LIC.toString(), ISLSEMPONTO, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ESLPONTO+SceEntrSaidSemLicitacao.Fields.IND_EFETIVADO.toString(), DominioSimNao.N.toString()));
		criteria.add(Restrictions.eq(ESLPONTO+SceEntrSaidSemLicitacao.Fields.IND_ENCERRADO.toString(), DominioSimNao.N.isSim()));
		if(filtro.getEsl() != null){
			criteria.add(Restrictions.eq(ESLPONTO+SceEntrSaidSemLicitacao.Fields.SEQ.toString(), filtro.getEsl()));
		}
		if(filtro.getMaterial() != null && filtro.getMaterial().getCodigo() != null){
			criteria.add(Restrictions.eq("ISL."+SceItemEntrSaidSemLicitacao.Fields.CODIGO_MATERIAL.toString(), filtro.getMaterial().getCodigo()));
		}

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.NUMERO.toString()), FornecedoresComEntregasPendentesVO.Fields.FORNECEDOR_NUMERO.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), FornecedoresComEntregasPendentesVO.Fields.FORNECEDOR_RAZAO_SOCIAL.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.CGC.toString()), FornecedoresComEntregasPendentesVO.Fields.CNPJ.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.CPF.toString()), FornecedoresComEntregasPendentesVO.Fields.CPF.toString())));

		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(FornecedoresComEntregasPendentesVO.class.getConstructor(
					Integer.class, String.class, Long.class, Long.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		
		return criteria;
	}
	
	public DetachedCriteria unionParaServico(FornecedoresComEntregasPendentesVO filtro) throws ApplicationBusinessException{
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, AFNSEMPONTO);
		
		criteria.createAlias(AFNPONTO+ScoAutorizacaoForn.Fields.SCO_FORNECEDOR.toString(), FRNSEMPONTO)
				.createAlias(AFNPONTO+ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), IAFSEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(IAFPONTO+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), FSC1SEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(FSC1PONTO+ScoFaseSolicitacao.Fields.FSL2.toString(), FSC2SEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(FSC2PONTO + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO, SLSSEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), SRVSEMPONTO, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(FSC2PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.isNotNull(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()));
		
		if(filtro.getServico() != null && filtro.getServico().getCodigo() != null){
			criteria.add(Restrictions.eq(SRVPONTO+ScoServico.Fields.CODIGO.toString(), filtro.getServico().getCodigo()));
		}

		criteria.add(Restrictions.or(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
				 Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		criteria.add(Restrictions.or(Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
				 Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		criteria.add(Restrictions.eq(FSC1PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.NUMERO.toString()), FornecedoresComEntregasPendentesVO.Fields.FORNECEDOR_NUMERO.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), FornecedoresComEntregasPendentesVO.Fields.FORNECEDOR_RAZAO_SOCIAL.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.CGC.toString()), FornecedoresComEntregasPendentesVO.Fields.CNPJ.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.CPF.toString()), FornecedoresComEntregasPendentesVO.Fields.CPF.toString())));
		
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(FornecedoresComEntregasPendentesVO.class.getConstructor(
					Integer.class, String.class, Long.class, Long.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		
		return criteria;
	}
	
	
	public DetachedCriteria listarEntregasPendentes(EntregasPendentesVO filtro) throws ApplicationBusinessException{
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, AFNSEMPONTO);

		criteria.createAlias(AFNPONTO + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), IAFSEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(IAFPONTO + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), FSC1SEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(AFNPONTO + ScoAutorizacaoForn.Fields.SCO_LICITACAO.toString(), LCTSEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(IAFPONTO +ScoItemAutorizacaoForn.Fields.SCO_CUM_X_PROG.toString(), "CPE", JoinType.LEFT_OUTER_JOIN)
		.createAlias(FSC1PONTO + ScoFaseSolicitacao.Fields.FSL2.toString(), FSC2SEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(FSC2PONTO + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS, SLCSEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(SLCPONTO + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), MATSEMPONTO, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(FSC2PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.isNotNull(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()));
		
		criteria.add(Restrictions.or(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
				Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		criteria.add(Restrictions.or(Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
				Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		
		criteria.add(Restrictions.eq(FSC1PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		if(filtro.getFornecedor() != null && filtro.getFornecedor().getNumero() != null){
			criteria.add(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), filtro.getFornecedor().getNumero()));
		}
		if(filtro.getAf() != null){
			criteria.add(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getAf()));
		}
		if(filtro.getCp() != null){
			criteria.add(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getCp()));
		}
		
		if(filtro.getMaterial() != null && filtro.getMaterial().getCodigo() != null){
			criteria.add(Restrictions.eq(MATPONTO+ScoMaterial.Fields.CODIGO.toString(), filtro.getMaterial().getCodigo()));
		}
		if(filtro.getCum() != null){
			criteria.add(Restrictions.eq("CPE."+ScoCumXProgrEntrega.Fields.SEQ.toString(), filtro.getCum()));
		}

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.NUMERO.toString()), EntregasPendentesVO.Fields.AFN_NUMERO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), EntregasPendentesVO.Fields.PFR_LCT_NUMERO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), EntregasPendentesVO.Fields.NUMERO_COMPLEMENTO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString()), EntregasPendentesVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.MLCCODIGO.toString()), EntregasPendentesVO.Fields.MLC_CODIGO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString()), EntregasPendentesVO.Fields.MODALIDADE_EMPENHO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString()), EntregasPendentesVO.Fields.DATA_VENC_CONTRATO.toString())
				.add(Projections.sqlProjection("'Material' as " + EntregasPendentesVO.Fields.TIPO_MATERIAL,
						new String[] {EntregasPendentesVO.Fields.TIPO_MATERIAL.toString()}, new Type[] { StringType.INSTANCE }))
				));

		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(EntregasPendentesVO.class.getConstructor(
					Integer.class, Integer.class, Short.class,  DominioSituacaoAutorizacaoFornecimento.class, String.class, 
					DominioModalidadeEmpenho.class, java.util.Date.class, String.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		
		return criteria;
	}
	
	public DetachedCriteria unionEntregasPendentesServico(EntregasPendentesVO filtro) throws ApplicationBusinessException{
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, AFNSEMPONTO);
		
		criteria.createAlias(AFNPONTO + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), IAFSEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(IAFPONTO + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), FSC1SEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(AFNPONTO + ScoAutorizacaoForn.Fields.SCO_LICITACAO.toString(), LCTSEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(FSC1PONTO + ScoFaseSolicitacao.Fields.FSL2_SERVICO.toString(), FSC2SEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias(FSC2PONTO + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO, SLSSEMPONTO, JoinType.LEFT_OUTER_JOIN)
		.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), SRVSEMPONTO, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(FSC2PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.isNotNull(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()));
		
		criteria.add(Restrictions.or(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
				Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		criteria.add(Restrictions.or(Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
				Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		
		criteria.add(Restrictions.eq(FSC1PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		if(filtro.getFornecedor() != null && filtro.getFornecedor().getNumero() != null){
			criteria.add(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), filtro.getFornecedor().getNumero()));
		}
		if(filtro.getAf() != null){
			criteria.add(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getAf()));
		}
		if(filtro.getCp() != null){
			criteria.add(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getCp()));
		}
		
		if(filtro.getServico() != null && filtro.getServico().getCodigo() != null){
			criteria.add(Restrictions.eq(SRVPONTO+ScoServico.Fields.CODIGO.toString(), filtro.getServico().getCodigo()));
		}
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.NUMERO.toString()), EntregasPendentesVO.Fields.AFN_NUMERO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), EntregasPendentesVO.Fields.PFR_LCT_NUMERO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), EntregasPendentesVO.Fields.NUMERO_COMPLEMENTO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString()), EntregasPendentesVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.MLCCODIGO.toString()), EntregasPendentesVO.Fields.MLC_CODIGO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString()), EntregasPendentesVO.Fields.MODALIDADE_EMPENHO.toString())
				.add(Projections.property(AFNPONTO+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString()), EntregasPendentesVO.Fields.DATA_VENC_CONTRATO.toString())
				.add(Projections.sqlProjection("'Serviço' as " + EntregasPendentesVO.Fields.TIPO_MATERIAL,
						new String[] {EntregasPendentesVO.Fields.TIPO_MATERIAL.toString()}, new Type[] { StringType.INSTANCE }))
				));
		
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(EntregasPendentesVO.class.getConstructor(
					Integer.class, Integer.class, Short.class,  DominioSituacaoAutorizacaoFornecimento.class, String.class, 
					DominioModalidadeEmpenho.class, java.util.Date.class, String.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		
		return criteria;
	}
	
	public DetachedCriteria unionParaEslETipoMovimento(EntregasPendentesVO filtro) throws ApplicationBusinessException{
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEntrSaidSemLicitacao.class, ESLSEMPONTO);
		
		criteria.createAlias(ESLPONTO+ SceEntrSaidSemLicitacao.Fields.ITEM_ESL.toString(), ISLSEMPONTO, JoinType.INNER_JOIN)
				.createAlias(ESLPONTO+ SceEntrSaidSemLicitacao.Fields.TIPO_MOVIMENTO.toString(), "TMV", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(ESLPONTO+ SceEntrSaidSemLicitacao.Fields.IND_EFETIVADO.toString(), "N"));
		criteria.add(Restrictions.eq(ESLPONTO+ SceEntrSaidSemLicitacao.Fields.IND_ENCERRADO.toString(), DominioSimNao.N.isSim()));
		if(filtro.getFornecedor() != null && filtro.getFornecedor().getNumero() != null){
			criteria.add(Restrictions.eq(ESLPONTO+ SceEntrSaidSemLicitacao.Fields.FRN_NUMERO.toString(), filtro.getFornecedor().getNumero()));
		}
		if(filtro.getEsl() != null){
			criteria.add(Restrictions.eq(ESLPONTO+ SceEntrSaidSemLicitacao.Fields.SEQ.toString(), filtro.getEsl()));
		}
		if(filtro.getMaterial() != null && filtro.getMaterial().getCodigo() != null){
			criteria.add(Restrictions.eq("ISL."+ SceItemEntrSaidSemLicitacao.Fields.CODIGO_MATERIAL.toString(), filtro.getMaterial().getCodigo()));
		}
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(ESLPONTO+SceEntrSaidSemLicitacao.Fields.SEQ.toString()), 
												EntregasPendentesVO.Fields.ESL.toString())
				.add(Projections.property("TMV."+SceTipoMovimento.Fields.DESCRICAO.toString()), 
						EntregasPendentesVO.Fields.TIPO_MOVIMENTO.toString())
				));
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(EntregasPendentesVO.class.getConstructor(Integer.class, String.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		
		return criteria;
	}		   
	
	
	public DetachedCriteria listarItensPendentesEntrega (ItensPendentesEntregaVO filtro, Integer cum) throws ApplicationBusinessException{
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, IAFSEMPONTO);
		
		criteria.createAlias(IAFPONTO+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), FSC1SEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(FSC1PONTO+ScoFaseSolicitacao.Fields.FSL2.toString(), FSC2SEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(FSC2PONTO+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), SLCSEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(SLCPONTO+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), MATSEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(IAFPONTO+ScoItemAutorizacaoForn.Fields.ITEM_NOTA_PRE_RECEBIMENTO.toString(), "IPR", JoinType.LEFT_OUTER_JOIN)
				.createAlias(IPRPONTO+SceItemNotaPreRecebimento.Fields.SCE_INC_FORN.toString(), "INC", JoinType.LEFT_OUTER_JOIN);
		
		//#29178
		if(cum != null){
			criteria.createAlias(IAFPONTO+ScoItemAutorizacaoForn.Fields.SCO_CUM_X_PROG.toString(),"CPE",JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("CPE."+ScoCumXProgrEntrega.Fields.SEQ.toString(),cum));
		}
		criteria.add(Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO, filtro.getScoAutForn().getNumero()));
		criteria.add(Restrictions.eq(FSC1PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO, DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq(FSC2PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO, DominioSimNao.N.isSim()));
		
		
		criteria.add(Restrictions.or(Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE),
				Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		
		criteria.add(Restrictions.or(Restrictions.eq(IPRPONTO+SceItemNotaPreRecebimento.Fields.IND_SITUACAO.toString(), DominioItemNotaPreRecebimento.G),
				Restrictions.isNull(IPRPONTO+SceItemNotaPreRecebimento.Fields.IND_SITUACAO.toString())));

		criteria.add(Restrictions.isNotNull(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));

		criteria.addOrder(Order.asc(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		
		StringBuilder somatorio = new StringBuilder(500); 
		somatorio.append(" (select sum (pea.qtde - coalesce(pea.qtde_entregue,0))   from agh.sco_progr_entrega_itens_af PEA  where pea.iaf_afn_numero = this_.afn_numero   and pea.iaf_numero = this_.numero  and pea.ind_cancelada = 'N'  and coalesce(pea.qtde_entregue,0) < pea.qtde) as somatorioQtdEQtdEntregue ");

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), ItensPendentesEntregaVO.Fields.ITL_NUMERO.toString())
				.add(Projections.sqlProjection(somatorio.toString(), 
						new String[] {ItensPendentesEntregaVO.Fields.SOMATORIO_QTDE_QTDE_ENTREGUE.toString()},
						new Type[] { new BigDecimalType() }))
				.add(Projections.property(IPRPONTO+SceItemNotaRecebimento.Fields.QUANTIDADE.toString()), 
						ItensPendentesEntregaVO.Fields.QTDE_ITEM_NOTA_RECEBIMENTO.toString())
				.add(Projections.property(IPRPONTO+SceItemNotaRecebimento.Fields.VALOR.toString()), 
						ItensPendentesEntregaVO.Fields.VALOR_ITEM_NOTA_RECEBIMENTO.toString())
				.add(Projections.property(IAFPONTO+ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString()), 
						ItensPendentesEntregaVO.Fields.VALOR_UNITARIO.toString())
				.add(Projections.property(IAFPONTO+ScoItemAutorizacaoForn.Fields.UNIDADE.toString()), 
						ItensPendentesEntregaVO.Fields.UNIDADE_MEDIDA.toString())
				.add(Projections.property(MATPONTO+ScoMaterial.Fields.CODIGO.toString()), 
						ItensPendentesEntregaVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.property(MATPONTO+ScoMaterial.Fields.NOME.toString()), 
						ItensPendentesEntregaVO.Fields.NOME_MATERIAL.toString())
				.add(Projections.property("INC."+SceE660IncForn.Fields.CODPRO.toString()), 
						ItensPendentesEntregaVO.Fields.CODPRO.toString())
				.add(Projections.property("INC."+SceE660IncForn.Fields.CPLPRO.toString()),
						ItensPendentesEntregaVO.Fields.CPLPRO.toString())
				.add(Projections.property(IAFPONTO+ScoItemAutorizacaoForn.Fields.NUMERO.toString()),
						ItensPendentesEntregaVO.Fields.IAF_NUMERO.toString())
				.add(Projections.property(IAFPONTO+ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO_FORM.toString()),
						ItensPendentesEntregaVO.Fields.FATOR_CONVERSAO_FORN.toString())
				.add(Projections.property(MATPONTO+ScoMaterial.Fields.IND_TERMOLABIL.toString()),
						ItensPendentesEntregaVO.Fields.IND_TERMOLABIL.toString())
				.add(Projections.property(MATPONTO+ScoMaterial.Fields.IND_CORROSIVO.toString()),
						ItensPendentesEntregaVO.Fields.IND_CORROSIVO.toString())
				.add(Projections.property(MATPONTO+ScoMaterial.Fields.IND_INFLAMAVEL.toString()),
						ItensPendentesEntregaVO.Fields.IND_INFLAMAVEL.toString())
				.add(Projections.property(MATPONTO+ScoMaterial.Fields.IND_RADIOATIVO.toString()),
						ItensPendentesEntregaVO.Fields.IND_RADIOATIVO.toString())
				.add(Projections.property(MATPONTO+ScoMaterial.Fields.IND_TOXICO.toString()),
						ItensPendentesEntregaVO.Fields.IND_TOXICO.toString())
				);
		
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(ItensPendentesEntregaVO.class.getConstructor(
					Short.class, BigDecimal.class, Long.class, BigDecimal.class, Double.class, ScoUnidadeMedida.class, Integer.class, 
					String.class, String.class, String.class, Integer.class, Integer.class,	Boolean.class, Boolean.class, Boolean.class, 
					Boolean.class, Boolean.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		return criteria;
	}
	
	public DetachedCriteria listarParcelasItemPendente(ItensPendentesEntregaVO filtro) throws ApplicationBusinessException{
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		
		criteria.add(Restrictions.eq(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), filtro.getScoAutForn().getNumero()));
		criteria.add(Restrictions.eq(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString(), filtro.getIafNumero()));
		if(filtro.getQtdeEntreguePea() != null){
			criteria.add(Restrictions.lt(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), filtro.getQtdePea()));
		} else {
			criteria.add(Restrictions.le(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString(), 0));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()),
						ItensPendentesEntregaVO.Fields.PARCELA.toString())
				.add(Projections.property(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE.toString()),
						ItensPendentesEntregaVO.Fields.QTDE_PEA.toString())
				.add(Projections.property(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.toString()),
						ItensPendentesEntregaVO.Fields.QTDE_ENTREGUE_PEA.toString())
				.add(Projections.property(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()),
						ItensPendentesEntregaVO.Fields.PEA_SEQ.toString()));
		
		criteria.addOrder(Order.asc(PEAPONTO + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));		
		
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(ItensPendentesEntregaVO.class.getConstructor(
					Integer.class, Integer.class, Integer.class, Integer.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
			
		return criteria;
	}

	public DetachedCriteria listarItensPendentesEntregaServico (ItensPendentesEntregaVO filtro) throws ApplicationBusinessException{
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, IAFSEMPONTO);
		
		criteria.createAlias(IAFPONTO+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), FSC1SEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(FSC1PONTO+ScoFaseSolicitacao.Fields.FSL2_SERVICO.toString(), FSC2SEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias(FSC2PONTO+ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), SLSSEMPONTO, JoinType.LEFT_OUTER_JOIN)
				.createAlias("SLS."+ScoSolicitacaoServico.Fields.SERVICO.toString(), SRVSEMPONTO, JoinType.LEFT_OUTER_JOIN);
	
		criteria.add(Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO, filtro.getScoAutForn().getNumero()));
		criteria.add(Restrictions.eq(FSC1PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO, DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq(FSC2PONTO+ScoFaseSolicitacao.Fields.IND_EXCLUSAO, DominioSimNao.N.isSim()));
		
		criteria.add(Restrictions.or(Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE),
				Restrictions.eq(IAFPONTO+ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		
		criteria.add(Restrictions.isNotNull(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));

		criteria.addOrder(Order.asc(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()));
		
		StringBuilder somatorio = new StringBuilder(500); 
		somatorio.append(" (select sum ( pea.VALOR_TOTAL - coalesce(pea.VALOR_EFETIVADO,0))  from agh.sco_progr_entrega_itens_af pea  where pea.iaf_afn_numero = this_.afn_numero  and pea.iaf_numero = this_.numero and pea.ind_cancelada = 'N'  and coalesce(pea.VALOR_EFETIVADO,0) < pea.VALOR_TOTAL) as valorSaldo "); 

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(FSC2PONTO+ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()), ItensPendentesEntregaVO.Fields.ITL_NUMERO.toString())
				.add(Projections.sqlProjection(somatorio.toString(), 
						new String[] {ItensPendentesEntregaVO.Fields.VALOR_SALDO.toString()},
						new Type[] { new BigDecimalType() }))
				.add(Projections.property(SRVPONTO+ScoServico.Fields.CODIGO.toString()), 
						ItensPendentesEntregaVO.Fields.CODIGO_SERVICO.toString())
				.add(Projections.property(SRVPONTO+ScoServico.Fields.NOME.toString()), 
						ItensPendentesEntregaVO.Fields.NOME_SERVICO.toString())
				.add(Projections.property(IAFPONTO+ScoItemAutorizacaoForn.Fields.NUMERO.toString()),
						ItensPendentesEntregaVO.Fields.IAF_NUMERO.toString())
				);
		
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(ItensPendentesEntregaVO.class.getConstructor(
					Short.class, BigDecimal.class, Integer.class, String.class, Integer.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		return criteria;
	}
	
	public DetachedCriteria montarCriteriaListarFornecedoresComEntregasPendentesSemFiltro()  throws ApplicationBusinessException{
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, AFNSEMPONTO);
		
		criteria.createAlias(AFNPONTO + ScoAutorizacaoForn.Fields.SCO_FORNECEDOR.toString(), FRNSEMPONTO, JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.or(Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), 
				 Restrictions.eq(AFNPONTO+ScoAutorizacaoForn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.NUMERO.toString()), FornecedoresComEntregasPendentesVO.Fields.FORNECEDOR_NUMERO.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), FornecedoresComEntregasPendentesVO.Fields.FORNECEDOR_RAZAO_SOCIAL.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.CGC.toString()), FornecedoresComEntregasPendentesVO.Fields.CNPJ.toString())
				.add(Projections.property(FRNPONTO+ScoFornecedor.Fields.CPF.toString()), FornecedoresComEntregasPendentesVO.Fields.CPF.toString())));
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(FornecedoresComEntregasPendentesVO.class.getConstructor(
					Integer.class, String.class, Long.class, Long.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
		criteria.addOrder(Order.asc(FRNPONTO+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));

		return criteria;
	}

}