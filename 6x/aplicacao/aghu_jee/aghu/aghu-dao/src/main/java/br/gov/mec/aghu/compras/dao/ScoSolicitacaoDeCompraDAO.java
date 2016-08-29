package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.contaspagar.vo.FiltroSolicitacaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.SolicitacaoTituloVO;
import br.gov.mec.aghu.compras.vo.AfPendenteCompradorVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcpTituloSolicitacoes;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoSolicitacaoDeCompraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSolicitacaoDeCompra> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3330264333796523598L;
	
	public List<AfPendenteCompradorVO> buscarListaAFPendentesPorComprador(ScoGrupoMaterial grupoMaterial, ScoMaterial material) {
		StringBuilder sb = new StringBuilder(700);
		
		/* Projeções */
		sb.append("SELECT MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())
		.append(", VPS1.").append(VRapPessoaServidor.Fields.NOME_USUAL.toString())
		.append(", VPS1.").append(VRapPessoaServidor.Fields.NOME.toString())
		.append(", VPS2.").append(VRapPessoaServidor.Fields.NOME_USUAL.toString())
		.append(", VPS2.").append(VRapPessoaServidor.Fields.NOME.toString())
		.append(", AFN.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
		.append(", AFN.").append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
		.append(", AFN.").append(ScoAutorizacaoForn.Fields.DT_GERACAO.toString())
		.append(", AFN.").append(ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString())
		.append(", AFN.").append(ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString())
		.append(", LCT.").append(ScoLicitacao.Fields.MLC_CODIGO.toString())
		.append(", AFN.").append(ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString())
		.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString())
		.append(", FSC1.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())
		.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString())
		.append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString())
		.append(", MAT.").append(ScoMaterial.Fields.NOME.toString())
		.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString())
		.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString())
		.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString())
		.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString())
		.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.VALOR_EFETIVADO.toString())
		.append(", FRN.").append(ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
		
		/* FROM */
		.append(" FROM ").append(VRapPessoaServidor.class.getSimpleName()).append(" VPS2, ")
		.append(VRapPessoaServidor.class.getSimpleName()).append(" VPS1, ")
		.append(ScoFornecedor.class.getSimpleName()).append(" FRN, ")
		.append(ScoLicitacao.class.getSimpleName()).append(" LCT, ")
		.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(" IAF, ")
		.append(ScoAutorizacaoForn.class.getSimpleName()).append(" AFN, ")
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC2, ")
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC1, ")
		.append(ScoMaterial.class.getSimpleName()).append(" MAT, ")
		.append(FccCentroCustos.class.getSimpleName()).append(" CCT, ")
		.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ")
		
		/* Condições JOINS */
		.append(" WHERE ")
		.append(" MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = ").append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString())
		.append(" AND CCT.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" = ").append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString())
		.append(" AND FSC1.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = ").append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())
		.append(" AND FSC2.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = ").append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())
		.append(" AND AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(" = ").append(" FSC2.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())
		.append(" AND IAF.").append(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()).append(" = ").append(" AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString())
		.append(" AND IAF.").append(ScoItemAutorizacaoForn.Fields.NUMERO.toString()).append(" = ").append(" FSC2.").append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString())
		.append(" AND LCT.").append(ScoLicitacao.Fields.NUMERO.toString()).append(" = ").append(" AFN.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
		.append(" AND FRN.").append(ScoFornecedor.Fields.NUMERO.toString()).append(" = ").append(" AFN.").append(ScoAutorizacaoForn.Fields.FORNECEDOR_NUMERO.toString())
		.append(" AND VPS1.").append(VRapPessoaServidor.Fields.SER_MATRICULA.toString()).append(" = ").append(" AFN.").append(ScoAutorizacaoForn.Fields.SER_MATRICULA.toString())
		.append(" AND VPS1.").append(VRapPessoaServidor.Fields.SER_VIN_CODIGO.toString()).append(" = ").append(" AFN.").append(ScoAutorizacaoForn.Fields.SER_VIN_CODIGO.toString())
		.append(" AND VPS2.").append(VRapPessoaServidor.Fields.SER_MATRICULA.toString()).append(" = ").append(" AFN.").append(ScoAutorizacaoForn.Fields.SER_MATRICULA_GESTOR.toString())
		.append(" AND VPS2.").append(VRapPessoaServidor.Fields.SER_VIN_CODIGO.toString()).append(" = ").append(" AFN.").append(ScoAutorizacaoForn.Fields.SER_VIN_CODIGO_GESTOR.toString())
		
		
		
		/* Restrições */
		.append(" AND FSC1.").append(ScoFaseSolicitacao.Fields.ITL_LCT_NUMERO.toString()).append(" is not null ")
		.append(" AND FSC1.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ")
		.append(" AND FSC2.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ")
		.append(" AND IAF.").append(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString()).append(" in ('AE', 'PA') ");
		
		
		if (material != null) {
			sb.append(" AND MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = ").append(" :codigoMaterial ");
		}
		
		if (grupoMaterial != null) {
			sb.append(" AND MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()).append(" = ").append(" :codigoGrupoMaterial ");
		}
		
		sb.append(" ORDER BY MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())
		.append(", VPS1.").append(VRapPessoaServidor.Fields.NOME.toString())
		.append(", VPS2.").append(VRapPessoaServidor.Fields.NOME.toString());

		Query query = this.createHibernateQuery(sb.toString());
		
		query.setParameter("indExclusao", Boolean.FALSE);
		
		if (material != null) {
			query.setParameter("codigoMaterial", material.getCodigo());
		}
		
		if (grupoMaterial != null) {
			query.setParameter("codigoGrupoMaterial", grupoMaterial.getCodigo());
		}
		
		List<Object[]> resultList = query.list();
		
		return criarListaAfPendenteCompradorVO(resultList);
	}
	
	private List<AfPendenteCompradorVO> criarListaAfPendenteCompradorVO(List<Object[]> resultList) {
		List<AfPendenteCompradorVO> listaVo = new ArrayList<AfPendenteCompradorVO>();
		
		for (Object[] ob : resultList) {
			
			AfPendenteCompradorVO vo = new AfPendenteCompradorVO();
			
			vo.setCodigoGrupoMaterial((Integer) ob[0]);
			
			vo.setCompradorU((String) ob[1]);
			vo.setComprador((String) ob[2]);
			
			vo.setGestorU((String) ob[3]);
			vo.setGestor((String) ob[4]);
			
			vo.setAfNumero((Integer) ob[5]);
			vo.setAfComplemento((Short) ob[6]);
			
			vo.setGeradaEm((Date) ob[7]);
			vo.setDtPrevEntrega((Date) ob[8]);
			vo.setDtVencContrato((Date) ob[9]);
			
			vo.setModlLict((String) ob[10]);
			vo.setModlEmp((DominioModalidadeEmpenho) ob[11]);
			
			vo.setItemContrato((Boolean) ob[12]);
			vo.setItem((Short) ob[13]);
			vo.setSit(((DominioSituacaoAutorizacaoFornecedor) ob[14]).name());
			
			vo.setCodigoMaterial((Integer) ob[15]);
			vo.setNomeMaterial((String) ob[16]);
			vo.setUnid((String) ob[17]);
			
			vo.setQtdeSolic((Integer) ob[18]);
			vo.setQtdeRecb((Integer) ob[19]);
			
			vo.setCustoUnit((Double) ob[20]);
			vo.setValorEfet((Double) ob[21]);
			
			Integer qtdeSol = vo.getQtdeSolic() != null ? vo.getQtdeSolic() : 0;
			Integer qtdeRec = vo.getQtdeRecb() != null ? vo.getQtdeRecb() : 0;
			Double vlrUnit = vo.getCustoUnit() != null ? vo.getCustoUnit() : 0;
			Double vlrEfet = vo.getValorEfet() != null ? vo.getValorEfet() : 0;
			// ((IAF.QTDE_SOLICITADA - IAF.QTDE_RECEBIDA) * IAF.VALOR_UNITARIO)			
			vo.setValorSaldo((qtdeSol - qtdeRec) * vlrUnit);
			//(IAF.VALOR_EFETIVADO + ((IAF.QTDE_SOLICITADA - IAF.QTDE_RECEBIDA) * IAF.VALOR_UNITARIO))
			vo.setValorItem(vlrEfet + ((qtdeSol - qtdeRec) * vlrUnit));
			
			vo.setFornecedor((String) ob[22]);
			
			listaVo.add(vo);
		}
		
		return listaVo;
	}

	public DetachedCriteria obterCriteriaFSC2() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC2");
		criteria.add(Restrictions.eqProperty("FSC2."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("FSC2."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("FSC2."+ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), "IAF."+ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq("FSC2."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		
		return criteria;
	}
	
	/**
	 * C1 - #22176
	 * @param numero
	 * @return
	 */
	public List<ScoSolicitacaoDeCompra> obterSolicitacoesCompraPorNumero(Object filter) {
		String strPesquisa = (String) filter;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class);
		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "mat",  JoinType.INNER_JOIN);
		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.UNIDADE_MEDIDA.toString(), "und",  JoinType.INNER_JOIN);
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), Integer.valueOf(strPesquisa)));
		}
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * #46298 - C1 - Buscar Solicitações de Compra
	 * 
	 * @param filtro
	 * @param pontoParada
	 * @return
	 */
	public List<SolicitacaoTituloVO> obterListaSolicitacaoDeCompra(FiltroSolicitacaoTituloVO filtro, Short pontoParada) {
		DetachedCriteria criteria = obterCriteriaListaSolicitacaoDeCompra(filtro);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).as(SolicitacaoTituloVO.Fields.SOLICITACAO.toString()))
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.DESCRICAO.toString()))
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.APLICACAO.toString()).as(SolicitacaoTituloVO.Fields.APLICACAO.toString()))
				.add(Projections.property("VBG." + FsoVerbaGestao.Fields.SEQ.toString()).as(SolicitacaoTituloVO.Fields.VBG_SEQ.toString()))
				.add(Projections.property("VBG." + FsoVerbaGestao.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.VBG_DESCRICAO.toString()))
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()).as(SolicitacaoTituloVO.Fields.CODIGO.toString()))
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()).as(SolicitacaoTituloVO.Fields.NOME.toString()))
				.add(Projections.property("GND." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()).as(SolicitacaoTituloVO.Fields.GRUPO_NATUREZA_DESPESA.toString()))
				.add(Projections.property("GND." + FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.DESCRICAO_GRUPO_NATUREZA_DESPESA.toString()))
				.add(Projections.property("NTD." + FsoNaturezaDespesa.Fields.CODIGO.toString()).as(SolicitacaoTituloVO.Fields.NTD_CODIGO.toString()))
				.add(Projections.property("NTD." + FsoNaturezaDespesa.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.NTD_DESCRICAO.toString()))
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.DATA_DIGITACAO.toString()).as(SolicitacaoTituloVO.Fields.DATA_GERACAO.toString()))
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.QUANTIDADE_SOLICITADA.toString()).as(SolicitacaoTituloVO.Fields.QTDE_SOLICITADA.toString()))
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.VALOR_UNIT_PREVISTO.toString()).as(SolicitacaoTituloVO.Fields.VALOR_UNIT_PREVISTO.toString()))
		);
		criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.PPS_CODIGO_LOC_PROXIMA.toString(), pontoParada));
		criteria.add(Subqueries.notExists(obterSubCriteriaTituloSolicitacao()));
		criteria.add(Subqueries.notExists(obterSubCriteriaFaseSolicitacao()));
		obterRestrictionListaSolicitacaoDeCompra(filtro, criteria);
		obterRestrictionDatasListaSolicitacaoDeCompra(filtro, criteria);
		criteria.setResultTransformer(Transformers.aliasToBean(SolicitacaoTituloVO.class));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaListaSolicitacaoDeCompra(FiltroSolicitacaoTituloVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		if (filtro.getVerbaGestao() != null) {
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.INNER_JOIN);
		} else {
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.LEFT_OUTER_JOIN);
		}
		if (filtro.getGrupoNaturezaDespesa() != null || filtro.getNaturezaDespesa() != null) {
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.INNER_JOIN);
			criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", JoinType.INNER_JOIN);
		} else {
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", JoinType.LEFT_OUTER_JOIN);
		}
		return criteria;
	}

	private void obterRestrictionListaSolicitacaoDeCompra(FiltroSolicitacaoTituloVO filtro, DetachedCriteria criteria) {
		if (filtro.getGrupoNaturezaDespesa() != null && filtro.getGrupoNaturezaDespesa().getCodigo() != null) {
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.NTD_GND_CODIGO.toString(), filtro.getGrupoNaturezaDespesa().getCodigo()));
		}
		if (filtro.getNaturezaDespesa() != null && filtro.getNaturezaDespesa().getId() != null && filtro.getNaturezaDespesa().getId().getCodigo() != null) {
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.NTD_CODIGO.toString(), filtro.getNaturezaDespesa().getId().getCodigo()));
		}
		if (filtro.getVerbaGestao() != null && filtro.getVerbaGestao().getSeq() != null) {
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString(), filtro.getVerbaGestao().getSeq()));
		}
		if (filtro.getSolicitacao() != null) {
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtro.getSolicitacao()));
		}
		if (filtro.getMaterial() != null && filtro.getMaterial().getCodigo() != null) {
			criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), filtro.getMaterial().getCodigo()));
		}
		if (filtro.getGrupoMaterial() != null && filtro.getGrupoMaterial().getCodigo() != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), filtro.getGrupoMaterial().getCodigo()));
		}
	}

	private void obterRestrictionDatasListaSolicitacaoDeCompra(FiltroSolicitacaoTituloVO filtro, DetachedCriteria criteria) {
		if (filtro.getDataGeracaoInicial() != null) {
			if (filtro.getDataGeracaoFinal() != null) {
				criteria.add(Restrictions.between("SLC." + ScoSolicitacaoDeCompra.Fields.DATA_DIGITACAO.toString(), DateUtil.truncaData(filtro.getDataGeracaoInicial()), DateUtil.truncaDataFim(filtro.getDataGeracaoFinal())));
			} else {
				criteria.add(Restrictions.ge("SLC." + ScoSolicitacaoDeCompra.Fields.DATA_DIGITACAO.toString(), DateUtil.truncaData(filtro.getDataGeracaoInicial())));
			}
		} else {
			if (filtro.getDataGeracaoFinal() != null) {
				criteria.add(Restrictions.le("SLC." + ScoSolicitacaoDeCompra.Fields.DATA_DIGITACAO.toString(), DateUtil.truncaDataFim(filtro.getDataGeracaoFinal())));
			}
		}
	}
	
	public DetachedCriteria obterSubCriteriaTituloSolicitacao() {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FcpTituloSolicitacoes.class , "TXS");
		subCriteria.setProjection(Projections.property("TXS." + FcpTituloSolicitacoes.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("TXS." + FcpTituloSolicitacoes.Fields.SLC_NUMERO.toString(), "SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		return subCriteria;
	}
	
	public DetachedCriteria obterSubCriteriaFaseSolicitacao() {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FSC");
		subCriteria.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		return subCriteria;
	}
}
