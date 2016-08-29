package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.estoque.vo.RelatorioDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDas;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SceDevolucaoAlmoxarifadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceDevolucaoAlmoxarifado> {
	
	private static final long serialVersionUID = 3270806845792338140L;

	private DetachedCriteria montarCriteriaPesquisarDevolucaoAlmoxarifado(Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo, Boolean estorno, Boolean pesquisaEstorno, Date dtInicio, Date dtFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDevolucaoAlmoxarifado.class);
		criteria.setFetchMode(SceDevolucaoAlmoxarifado.Fields.SERVIDOR.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceDevolucaoAlmoxarifado.Fields.SERVIDOR.toString()+"."+RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceDevolucaoAlmoxarifado.Fields.ALMOXARIFADO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceDevolucaoAlmoxarifado.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceDevolucaoAlmoxarifado.Fields.TIPO_MOVIMENTO.toString(), FetchMode.JOIN);
		if (numeroDa != null){
			criteria.add(Restrictions.eq(SceDevolucaoAlmoxarifado.Fields.SEQ.toString(), numeroDa));
			if (pesquisaEstorno){
				criteria.add(Restrictions.eq(SceDevolucaoAlmoxarifado.Fields.ESTORNO.toString(), estorno));	
			}
			return criteria;
		}
		if (almoxarifadoSeq != null){
			criteria.createAlias(SceDevolucaoAlmoxarifado.Fields.ALMOXARIFADO.toString(), SceDevolucaoAlmoxarifado.Fields.ALMOXARIFADO.toString());
			criteria.add(Restrictions.eq(SceDevolucaoAlmoxarifado.Fields.ALMOXARIFADO.toString()+"."+SceAlmoxarifado.Fields.SEQ.toString(), almoxarifadoSeq));
		}
		if (cctCodigo != null){
			criteria.createAlias(SceDevolucaoAlmoxarifado.Fields.CENTRO_CUSTO.toString(), SceDevolucaoAlmoxarifado.Fields.CENTRO_CUSTO.toString());
			criteria.add(Restrictions.eq(SceDevolucaoAlmoxarifado.Fields.CENTRO_CUSTO.toString()+"."+FccCentroCustos.Fields.CODIGO.toString(), cctCodigo));
		} 	
		if (pesquisaEstorno){
			criteria.add(Restrictions.eq(SceDevolucaoAlmoxarifado.Fields.ESTORNO.toString(), estorno));	
		}
		if (dtInicio != null) {
			Date dataInicioTruncada = DateUtil.truncaData(dtInicio); 
			criteria.add(Restrictions.ge(SceDevolucaoAlmoxarifado.Fields.DT_GERACAO.toString(), dataInicioTruncada));			
		}
		if (dtFim != null){
			Date dataFimTruncada = DateUtil.truncaDataFim(dtFim);
			criteria.add(Restrictions.le(SceDevolucaoAlmoxarifado.Fields.DT_GERACAO.toString(), dataFimTruncada));	
		}
		
		
		
		
		
		return criteria;
	}
	
	public List<SceDevolucaoAlmoxarifado> pesquisarDevolucaoAlmoxarifado(Integer numeroDa, Short almoxarifadoSeq, 
			Integer cctCodigo, Boolean estorno, Boolean pesquisaEstorno, Date dtInicio, Date dtFim){
		return executeCriteria(montarCriteriaPesquisarDevolucaoAlmoxarifado(
				numeroDa, almoxarifadoSeq, cctCodigo, estorno, pesquisaEstorno, dtInicio, dtFim));
	}
	
	public List<SceDevolucaoAlmoxarifado> pesquisarDevolucaoAlmoxarifado(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo, 
			Boolean estorno, Boolean pesquisaEstorno, Date dtInicio, Date dtFim) {
		return executeCriteria(montarCriteriaPesquisarDevolucaoAlmoxarifado(
				numeroDa, almoxarifadoSeq, cctCodigo, estorno, pesquisaEstorno, dtInicio, dtFim),
				firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarDevolucaoAlmoxarifadoCount(Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo, 
			Boolean estorno, Boolean pesquisaEstorno, Date dtInicio, Date dtFim) {
		return executeCriteriaCount(montarCriteriaPesquisarDevolucaoAlmoxarifado(
				numeroDa, almoxarifadoSeq, cctCodigo, estorno, pesquisaEstorno, dtInicio, dtFim));
	}
	
	public SceDevolucaoAlmoxarifado obterDevolucaoAlmoxarifadoEstorno(Integer seq) {
		if (seq == null) {
			throw new IllegalArgumentException("Parametro seq não informado!");
		}		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDevolucaoAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceDevolucaoAlmoxarifado.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceDevolucaoAlmoxarifado.Fields.ESTORNO.toString(), Boolean.TRUE));
		return (SceDevolucaoAlmoxarifado) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * 
	 * @param numeroDevAlmox
	 * @return
	 */
	public List<RelatorioDevolucaoAlmoxarifadoVO> pesquisarDadosRelatorioDevolucaoAlmoxarifado(Integer numeroDevAlmox) {
		
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(),"DAL");
		
		//Projections
		ProjectionList p = Projections.projectionList();		
		p.add(Projections.groupProperty("DAL.".concat(SceDevolucaoAlmoxarifado.Fields.SEQ.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.SEQ.toString());	
		p.add(Projections.groupProperty("EAL.".concat(SceDevolucaoAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.ALM_SEQ.toString());
		p.add(Projections.groupProperty("DAL.".concat(SceDevolucaoAlmoxarifado.Fields.DT_GERACAO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.DT_GERACAO.toString());
		p.add(Projections.groupProperty("CCT.".concat(FccCentroCustos.Fields.CODIGO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.CCT_CODIGO.toString());
		p.add(Projections.groupProperty("CCT.".concat(FccCentroCustos.Fields.DESCRICAO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.CCT_DESCRICAO.toString());
		p.add(Projections.groupProperty("DAL.".concat(SceDevolucaoAlmoxarifado.Fields.OBSERVACAO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.OBSERVACAO.toString());
		p.add(Projections.groupProperty("TMV.".concat(SceTipoMovimento.Fields.SEQ.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.TMV_SEQ.toString());
		p.add(Projections.groupProperty("TMV.".concat(SceTipoMovimento.Fields.COMPLEMENTO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.TMV_COMPLEMENTO.toString());		
		p.add(Projections.groupProperty("PEF.".concat(RapPessoasFisicas.Fields.NOME.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.NOME_PESSOA_SERVIDOR.toString());
		p.add(Projections.min("SER.".concat(RapServidores.Fields.NRO_RAMAL.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.RCC_RAMAL.toString());
		p.add(Projections.groupProperty("MAT.".concat(ScoMaterial.Fields.CODIGO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.MAT_CODIGO.toString());
		p.add(Projections.groupProperty("MAT.".concat(ScoMaterial.Fields.NOME.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.MAT_NOME.toString());
		p.add(Projections.groupProperty("EAL.".concat(SceEstoqueAlmoxarifado.Fields.ENDERECO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.ENDERECO.toString());
		p.add(Projections.groupProperty("FRN.".concat(ScoFornecedor.Fields.RAZAO_SOCIAL.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.FORNECEDOR.toString());
		p.add(Projections.groupProperty("FRN.".concat(ScoFornecedor.Fields.NUMERO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.NUMERO_FORNECEDOR.toString());
		p.add(Projections.groupProperty("UMD.".concat(ScoUnidadeMedida.Fields.CODIGO.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.UMD_CODIGO.toString());
		p.add(Projections.groupProperty("IDA.".concat(SceItemDas.Fields.QUANTIDADE.toString())), RelatorioDevolucaoAlmoxarifadoVO.Fields.QUANTIDADE.toString());
		
		//Alias
		dc.createAlias("DAL.".concat(SceDevolucaoAlmoxarifado.Fields.SERVIDOR.toString()), "SER", Criteria.INNER_JOIN);
		dc.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PEF", Criteria.INNER_JOIN);
		dc.createAlias("DAL.".concat(SceDevolucaoAlmoxarifado.Fields.CENTRO_CUSTO.toString()), "CCT", Criteria.INNER_JOIN);
		dc.createAlias("DAL.".concat(SceDevolucaoAlmoxarifado.Fields.TIPO_MOVIMENTO.toString()), "TMV", Criteria.INNER_JOIN);
		dc.createAlias("DAL.".concat(SceDevolucaoAlmoxarifado.Fields.SCE_ITEM_DAS.toString()), "IDA", Criteria.INNER_JOIN);
		dc.createAlias("IDA.".concat(SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString()), "EAL", Criteria.INNER_JOIN);
		dc.createAlias("IDA.".concat(SceItemDas.Fields.UNIDADE_MEDIDA.toString()), "UMD", Criteria.INNER_JOIN);
		dc.createAlias("EAL.".concat(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString()), "MAT", Criteria.INNER_JOIN);
		dc.createAlias("EAL.".concat(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString()), "FRN", Criteria.LEFT_JOIN);
		
		//Restrictions
		dc.add(Restrictions.eq("DAL.".concat(SceDevolucaoAlmoxarifado.Fields.SEQ.toString()), numeroDevAlmox));

		//Order by
		dc.addOrder(Order.asc("MAT.".concat(ScoMaterial.Fields.CODIGO.toString())));

		dc.setProjection(p);
		dc.setResultTransformer(Transformers.aliasToBean(RelatorioDevolucaoAlmoxarifadoVO.class));
		
		return executeCriteria(dc);
	}
}