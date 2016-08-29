package br.gov.mec.aghu.estoque.dao;

import br.gov.mec.aghu.estoque.vo.QuantidadesVO;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisValidadeVencidaVO;
import br.gov.mec.aghu.model.*;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import java.util.Date;
import java.util.List;

public class SceValidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceValidade> {

	private static final long serialVersionUID = -1471240016089461860L;

	/**
	 * Retorna uma lista com os dados para relatorio de material de validade vencida
	 * 
	 * @param seqAlmoxarifado
	 * @param codigoGrupo
	 * @param dataInicial
	 * @param dataFinal
	 * @param numeroFornecedor
	 * @return Lsita de RelatorioMateriaisValidadeVencidaVO com os dados para o relatório.
	 */
	public List<RelatorioMateriaisValidadeVencidaVO> pesquisarDadosRelatorioMaterialValidadeVencida(Short seqAlmoxarifado, 
																									Integer codigoGrupo,
																									Date dataInicial,
																									Date dataFinal,
																									Integer numeroFornecedor) {
		
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");
			
			criteria.createAlias("MAT."	+ ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.INNER_JOIN);			
			criteria.createAlias("MAT."	+ ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);
			criteria.createAlias("EAL."	+ SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.INNER_JOIN);			
			criteria.createAlias("EAL."	+ SceEstoqueAlmoxarifado.Fields.VALIDADES.toString(), "VAL", JoinType.INNER_JOIN);			
			criteria.createAlias("EAL."	+ SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
			
			ProjectionList p = Projections.projectionList();
			
			p.add(Projections.property("GMT."+ ScoGrupoMaterial.Fields.CODIGO.toString()), 
					RelatorioMateriaisValidadeVencidaVO.Fields.GMT_CODIGO.toString());
			p.add(Projections.property("GMT."+ ScoGrupoMaterial.Fields.DESCRICAO.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.GMT_DESCRICAO.toString());
			p.add(Projections.property("MAT."+ ScoMaterial.Fields.NOME.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.NOME_MATERIAL.toString());
			p.add(Projections.property("MAT."+ ScoMaterial.Fields.CODIGO.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.CODIGO_MATERIAL.toString());
			p.add(Projections.property("EAL."+ SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.ALMOX.toString());
			p.add(Projections.property("ALM."+ SceAlmoxarifado.Fields.DESCRICAO.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.ALMOX_DESCRICAO.toString());
			p.add(Projections.property("EAL."+ SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.UNID.toString());
			p.add(Projections.property("EAL."+ SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.ENDE.toString());
			p.add(Projections.property("EAL."+ SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.QTDE_DISP.toString());
			p.add(Projections.property("VAL."+ SceValidade.Fields.DATA.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.VALIDADE.toString());			
			p.add(Projections.property("VAL."+ SceValidade.Fields.QTDE_DISPONIVEL.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.QTDE_VALD.toString());
			p.add(Projections.property("EAL."+ SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.FRN_NUMERO.toString());			
			p.add(Projections.property("FRN."+ ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),
					RelatorioMateriaisValidadeVencidaVO.Fields.RAZAO_SOCIAL.toString());			
			
			criteria.setProjection(p);
			
			/**
			  GMT.CODIGO = EAL.ALM_SEQ
		      AND MAT.GMT_CODIGO = GMT.CODIGO
		      AND EAL.MAT_CODIGO  = MAT.CODIGO
		      AND EAL.ALM_SEQ = EAL.ALM_SEQ
		      AND EAL.FRN_NUMERO = EAL.FRN_NUMERO
		      AND EAL.QTDE_DISPONIVEL  >  0
		      AND VAL.EAL_SEQ = EAL.SEQ
		      AND VAL.DATA BETWEEN date '2011-10-07' and date '2012-02-02'
		      AND VAL.QTDE_DISPONIVEL > 0
		      AND ALM.SEQ = EAL.ALM_SEQ
		      AND FRN.NUMERO = EAL.FRN_NUMERO
			 */

			if(codigoGrupo!=null){
				criteria.add(Restrictions.eq("GMT."+ ScoGrupoMaterial.Fields.CODIGO.toString(), codigoGrupo));
			}
			if(seqAlmoxarifado!=null){
				criteria.add(Restrictions.eq("EAL."+ SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(),seqAlmoxarifado));
			}
			if(numeroFornecedor!=null){
				criteria.add(Restrictions.eq("EAL."+ SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(),numeroFornecedor));
			}
			if(dataInicial!=null && dataFinal!=null){
				criteria.add(Restrictions.between("VAL."+ SceValidade.Fields.DATA.toString(),dataInicial,dataFinal));
			}else{
				criteria.add(Restrictions.lt("VAL."+ SceValidade.Fields.DATA.toString(), new Date()));
			}

			criteria.add(Restrictions.gt("VAL."+ SceValidade.Fields.QTDE_DISPONIVEL.toString(),0));
			criteria.add(Restrictions.gt("EAL."+ SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString(),0));
			
			// ordenacao
			criteria.addOrder(Order.asc("GMT."+ ScoGrupoMaterial.Fields.CODIGO.toString()));
			criteria.addOrder(Order.asc("MAT."+ ScoMaterial.Fields.NOME.toString()));
			criteria.addOrder(Order.asc("VAL."+ SceValidade.Fields.DATA.toString()));
			
			criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMateriaisValidadeVencidaVO.class));
			return executeCriteria(criteria);
		}
	
	/**
	 * Pesquisa Validade através estoque almoxarifado e data de validade
	 * @param ealSeq
	 * @param dataValidade
	 * @return
	 */
	public List<SceValidade> pesquisarValidadePorEalSeqDataValidade(Integer ealSeq, Date dataValidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceValidade.class,"val");
		criteria.add(Restrictions.eq("val."+SceValidade.Fields.EAL_SEQ.toString(),ealSeq));
		criteria.add(Restrictions.eq("val."+SceValidade.Fields.DATA.toString(),dataValidade));
		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa Validade através estoque almoxarifado e data de validade com quantidade disponível
	 * @param ealSeq
	 * @param dataValidade
	 * @return
	 */
	public List<SceValidade> pesquisarValidadeEalSeqDataValidadeComQuantidadeDisponivel(Integer ealSeq, Date dataValidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceValidade.class,"val");
		criteria.add(Restrictions.eq("val."+SceValidade.Fields.EAL_SEQ.toString(),ealSeq));
		if(dataValidade !=null){
			criteria.add(Restrictions.eq("val."+SceValidade.Fields.DATA.toString(),dataValidade));
		}
		criteria.add(Restrictions.gt("val."+SceValidade.Fields.QTDE_DISPONIVEL.toString(),0));
		criteria.addOrder(Order.asc("val."+SceValidade.Fields.DATA.toString()));
		return executeCriteria(criteria);
	}

	public SceValidade obterValidadePorMaterialDtValidadeEstqAlmoxFornecedor(
			ScoMaterial material, Date dtValidade,
			SceEstoqueAlmoxarifado estoqueAlmoxarifado, Integer fornecedorHcpa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceValidade.class,"VAL");
		criteria.add(Restrictions.eq("VAL."+SceValidade.Fields.EAL_SEQ.toString(), estoqueAlmoxarifado.getSeq()));
		criteria.add(Restrictions.eq("VAL."+SceValidade.Fields.DATA.toString(), dtValidade));
		
		return (SceValidade)executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * Obtém uma validade através da data e seq/id do estoque almoxarifado
	 */
	public SceValidade obterValidadePorDataValidadeEstoqueAlmoxarifado(Date dtValidade, Integer seqEstoqueAlmoxarifado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceValidade.class,"VAL");
		
		criteria.add(Restrictions.eq("VAL."+SceValidade.Fields.EAL_SEQ.toString(), seqEstoqueAlmoxarifado));
		criteria.add(Restrictions.eq("VAL."+SceValidade.Fields.DATA.toString(), dtValidade));

		return (SceValidade)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Data de validade do item no almoxarifado
	 * 
	 * C9 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param ealSeq
	 * @return
	 */
	public Date obterDataValidade(Integer ealSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceValidade.class);

		criteria.add(Restrictions.eq(SceValidade.Fields.EAL_SEQ.toString(), ealSeq));
		criteria.add(Restrictions.gt(SceValidade.Fields.QTDE_DISPONIVEL.toString(), Integer.valueOf(0)));

		criteria.setProjection(Projections.min(SceValidade.Fields.DATA.toString()));

		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Quantidade disponível do material em estoque
	 * 
	 * C10 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param matCodigo
	 * @param controleValidade
	 * @param dataReferencia
	 * @param almoxUnico
	 * @return
	 */
	public QuantidadesVO obterQtdDisponivelQtdBloqueada(Integer matCodigo, Boolean controleValidade, Date dataReferencia, Boolean almoxUnico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceValidade.class, "VAL");
		criteria.add(Restrictions.gt("VAL." + SceValidade.Fields.QTDE_DISPONIVEL.toString(), Integer.valueOf(0)));
		if (controleValidade) {
			criteria.add(Restrictions.gt("VAL." + SceValidade.Fields.DATA.toString(), dataReferencia));
		}

		criteria.createAlias("VAL." + SceValidade.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL");
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");

		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), matCodigo));

		if (almoxUnico) {
			criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM1");
			criteria.createAlias("MAT." + ScoMaterial.Fields.ALMOXARIFADO.toString(), "ALM2");
			criteria.add(Restrictions.eqProperty("ALM1." + SceAlmoxarifado.Fields.SEQ.toString(), "ALM2." + SceAlmoxarifado.Fields.SEQ.toString()));
		}

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.sum("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()), QuantidadesVO.Fields.QUANTIDADE_1.toString());
		projections.add(Projections.sum("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()), QuantidadesVO.Fields.QUANTIDADE_2.toString());
		criteria.setProjection(projections);

		criteria.setResultTransformer(Transformers.aliasToBean(QuantidadesVO.class));

		return (QuantidadesVO) super.executeCriteriaUniqueResult(criteria);
	}
}