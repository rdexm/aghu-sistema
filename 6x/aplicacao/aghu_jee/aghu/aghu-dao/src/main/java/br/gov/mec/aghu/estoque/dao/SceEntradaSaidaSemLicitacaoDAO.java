package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceTipoMovimento;

public class SceEntradaSaidaSemLicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceEntradaSaidaSemLicitacao> {
	
	
	private static final long serialVersionUID = -8826848115422077277L;

	public SceEntradaSaidaSemLicitacao obterEntradaSaidaSemLicitacaoPorSeqMatCodigo(Integer ealSeq, Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEntradaSaidaSemLicitacao.class);
		
		criteria.add(Restrictions.eq(SceEntradaSaidaSemLicitacao.Fields.SEQ.toString(), ealSeq));
		criteria.add(Restrictions.eq(SceEntradaSaidaSemLicitacao.Fields.CODIGO_MATERIAL.toString(), matCodigo));
		
		return (SceEntradaSaidaSemLicitacao) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Pesquisa documentos com unidade antiga não encerrados através do material e unidade de medida
	 * @param codigoMaterial
	 * @param codigoUnidadeMedida
	 * @return
	 */
	public List<SceEntradaSaidaSemLicitacao> pesquisarDocumentoUnidadeAntigaNaoEncerrado(Integer codigoMaterial, String codigoUnidadeMedida) {
		
		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEntradaSaidaSemLicitacao.class, "ESL");
		
		criteria.createAlias("ESL." + SceEntradaSaidaSemLicitacao.Fields.TIPO_MOVIMENTO.toString(), "TMV", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("ESL." + SceEntradaSaidaSemLicitacao.Fields.CODIGO_MATERIAL.toString(), codigoMaterial));
		criteria.add(Restrictions.eq("ESL." + SceEntradaSaidaSemLicitacao.Fields.IND_ENCERRADO.toString(), Boolean.FALSE));
		
		if (codigoUnidadeMedida != null) {
			criteria.add(Restrictions.eq("ESL." + SceEntradaSaidaSemLicitacao.Fields.CODIGO_UNIDADE_MEDIDA.toString(), codigoUnidadeMedida));
		}
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisa a data de geração de entrada e saída sem licitação através do seq do documento fiscal de entrada
	 * @param documentoFiscalEntrada
	 * @return
	 */
	public List<Date> pesquisarDataGeracaoEntradaSaidaSemLicitacaoPorDocumentoFiscalEntrada(Integer seqDocumentoFiscalEntradada) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEntradaSaidaSemLicitacao.class);
		criteria.createAlias(SceEntradaSaidaSemLicitacao.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE", Criteria.INNER_JOIN);
		
		criteria.setProjection(Projections.property(SceEntradaSaidaSemLicitacao.Fields.DATA_GERACAO.toString()));

		criteria.add(Restrictions.eq("DFE." + SceDocumentoFiscalEntrada.Fields.SEQ.toString(), seqDocumentoFiscalEntradada));
		criteria.add(Restrictions.eq(SceEntradaSaidaSemLicitacao.Fields.IND_ESTORNO.toString(), Boolean.TRUE));
		
		return  executeCriteria(criteria);
	}

	/**
	 * Pesquisa entradas ou saídas sem licitação por material
	 * @param documentoFiscalEntrada
	 * @return
	 */
	public List<SceEntradaSaidaSemLicitacao> pesquisarEntradaSaidaPorMaterial(Integer codMaterial, Object param) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEntradaSaidaSemLicitacao.class);
		criteria.createAlias(SceEntradaSaidaSemLicitacao.Fields.TIPO_MOVIMENTO.toString(), "tvm", Criteria.INNER_JOIN);
		criteria.createAlias(SceEntradaSaidaSemLicitacao.Fields.FORNECEDOR.toString(), "frn", Criteria.LEFT_JOIN);
		criteria.createAlias(SceEntradaSaidaSemLicitacao.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "dfe", Criteria.LEFT_JOIN);
		criteria.createAlias(SceEntradaSaidaSemLicitacao.Fields.MARCA_COMERCIAL.toString(), "mcm", Criteria.LEFT_JOIN);
		criteria.createAlias("dfe."+SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "frn2", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("tvm." + SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(SceEntradaSaidaSemLicitacao.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(SceEntradaSaidaSemLicitacao.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(SceEntradaSaidaSemLicitacao.Fields.CODIGO_MATERIAL.toString(), codMaterial));
		/*
		and 	(esl.mcm_codigo is not null   or esl.nc_mcm_codigo is not null)
		and 	(esl.frn_numero is not null or esl.fev_seq is not null or esl.dfe_seq is not null)
		*/
		criteria.add(Restrictions.sqlRestriction("({alias}.mcm_codigo is not null   or {alias}.nc_mcm_codigo is not null)"));
		criteria.add(Restrictions.sqlRestriction("({alias}.frn_numero is not null or {alias}.fev_seq is not null or {alias}.dfe_seq is not null)"));
		
		return  executeCriteria(criteria);
	}
	
	
}