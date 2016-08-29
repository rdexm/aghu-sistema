package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOrderBy;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceItemRmsId;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class SceItemRmsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemRms> {
	
	private static final long serialVersionUID = -4153174617708498205L;

	/**
	 * Retorna SceItemRms original.<br>
	 * 
	 * Devido a problema na migracao de dados/estrutura o metodo generico nao pode ser usado por enquanto.<br>
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public SceItemRms obterOriginal(SceItemRms elementoModificado) {
		
		SceItemRmsId id = elementoModificado.getId();
//		StringBuilder hql = new StringBuilder();
//		hql.append("select o.").append(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString());
//		hql.append(", o.").append(SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString());
//		hql.append(", o.").append(SceItemRms.Fields.SCO_UNIDADE_MEDIDA.toString());
//		hql.append(", o.").append(SceItemRms.Fields.QTD_REQUISITADA.toString());
//		hql.append(", o.").append(SceItemRms.Fields.IND_TEM_ESTOQUE.toString());
//		hql.append(", o.").append(SceItemRms.Fields.QTDE_ENTREGUE.toString());
//		hql.append(", o.").append(SceItemRms.Fields.OBSERVACAO.toString());
//		//hql.append(", o.").append(SceItemRms.Fields.SCE_ITEM_PEDIDO_MATERIAIS.toString());
//		hql.append(", o.").append(SceItemRms.Fields.QTDE_DEVOLVIDA.toString());
//		hql.append(", o.").append(SceItemRms.Fields.QTDE_TERCEIROS.toString());
//		hql.append(", o.").append(SceItemRms.Fields.QTDE_EM_DEVOLUCAO.toString());
//		hql.append(", o.").append(SceItemRms.Fields.SCE_ITEM_RMPS.toString());
//		
//		hql.append(" from ").append(SceItemRms.class.getSimpleName()).append(" o ");
//		
//		hql.append(" left outer join o.").append(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString());
//		hql.append(" left outer join o.").append(SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString());
//		hql.append(" left outer join o.").append(SceItemRms.Fields.SCO_UNIDADE_MEDIDA.toString());
//		// TODO: tabela não importada pelo pessoal do banco de dados
//		// 	hql.append(" left outer join o.").append(SceItemRms.Fields.SCE_ITEM_PEDIDO_MATERIAIS.toString());
//		 
//		 
//		hql.append(" left outer join o.").append(SceItemRms.Fields.SCE_ITEM_RMPS.toString());
//		
//		hql.append(" where o.").append(SceItemRms.Fields.ID.toString()).append(" = :entityId ");
//		
//		Query query = this.createQuery(hql.toString());
//		query.setParameter("entityId", id);
//		
//		SceItemRms original = null;
//		List<Object[]> camposLst = (List<Object[]>) query.getResultList();
//		
//		if(camposLst != null && camposLst.size()>0) {
//			
//			Object[] campos = camposLst.get(0);
//			original = new SceItemRms();
//			
//			original.setId(id);
//			original.setSceReqMateriais((SceReqMaterial)campos[0]);
//			original.setEstoqueAlmoxarifado((SceEstoqueAlmoxarifado)campos[1]);
//			original.setScoUnidadeMedida((ScoUnidadeMedida)campos[2]);
//			original.setQtdeRequisitada((Integer)campos[3]);
//			original.setIndTemEstoque((Boolean)campos[4]);
//			original.setQtdeEntregue((Integer)campos[5]);
//			original.setObservacao((String)campos[6]);
//			//original.setSceItemPedidoMateriais((SceItemPedidoMateriais)campos[7]);
//			original.setQtdeDevolvida((Integer)campos[7]);
//			original.setQtdeTerceiros((Integer)campos[8]);
//			original.setQtdeEmDevolucao((Integer)campos[9]);
//			original.setSceItemRmps((SceItemRmps)campos[10]);
//		}
//		return original;
		return this.obterPorChavePrimaria(id);
	}
	
	/**
	 * Retorna true ou false;
	 * @param id
	 * @return
	 */
	public boolean verificaExistencia(SceItemRms elementoModificado) {
		SceItemRmsId id = elementoModificado.getId();

		StringBuilder hql = new StringBuilder(100);

		hql.append("select count(*) as counter ");
		hql.append(" from ").append(SceItemRms.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(SceItemRms.Fields.ID.toString()).append(" = :entityId ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);

		Object counter = (Object) query.getSingleResult();
		
		if(Integer.parseInt(counter.toString())>0) {
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Pesquisa SceReqMateriais por quantidade entregue da requisição de material
	 * @param sceReqMateriais
	 * @return
	 */
	public List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriaisQntEntregue(SceReqMaterial sceReqMateriais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
		criteria.add(Restrictions.eq(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString(), sceReqMateriais));
		criteria.add(Restrictions.isNotNull(SceItemRms.Fields.QTDE_ENTREGUE.toString()));
		criteria.add(Restrictions.gt(SceItemRms.Fields.QTDE_ENTREGUE.toString(), 0));
		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa SceReqMateriais
	 * @param sceReqMateriais
	 * @return
	 */
	public List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriais(Integer reqMaterialSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
		criteria.createAlias(SceItemRms.Fields.ESTOQUE_ALMOXARIFADO.toString(), "req_estq_almo" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("req_estq_almo." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mat." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "grupo" ,JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(SceItemRms.Fields.SCE_REQ_MATERIAIS_SEQ.toString(), reqMaterialSeq));
		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa SceReqMateriais
	 * @param sceReqMateriais
	 * @return
	 */
	public List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriaisOrderBy(Integer reqMaterialSeq, DominioOrderBy orderBy, DominioSituacaoRequisicaoMaterial indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
		criteria.createAlias(SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString(), "eal", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("eal."+SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mat."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "gmat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("eal."+SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "forn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceItemRms.Fields.SCO_UNIDADE_MEDIDA.toString(), "sco_um", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString(), "sce_req_mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("sce_req_mat." + SceReqMaterial.Fields.SERVIDOR.toString(), "servidorReq", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("sce_req_mat." + SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), "centroCustos", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("sce_req_mat." + SceReqMaterial.Fields.ALMOXARIFADO.toString(), "req_mat_almo" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("sce_req_mat." + SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), "req_gm" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("req_mat_almo." + SceAlmoxarifado.Fields.CCT_CODIGO.toString(), "almoarifadoCentroCusto" ,JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(SceItemRms.Fields.SCE_REQ_MATERIAIS_SEQ.toString(), reqMaterialSeq));
		
		
		if(orderBy!=null){
			if(orderBy.equals(DominioOrderBy.C)){
				criteria.addOrder(Order.asc("mat."+ScoMaterial.Fields.CODIGO.toString()));
			}else if(orderBy.equals(DominioOrderBy.N)){
				criteria.addOrder(Order.asc("mat."+ScoMaterial.Fields.NOME.toString()));
			}else if(orderBy.equals(DominioOrderBy.E)){
				criteria.addOrder(Order.asc("eal."+SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()));
				criteria.addOrder(Order.asc("mat."+ScoMaterial.Fields.NOME.toString()));
			}
		}else{
			criteria.addOrder(Order.asc("eal."+SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()));
			criteria.addOrder(Order.asc("mat."+ScoMaterial.Fields.NOME.toString()));
		}
		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa SceReqMateriais
	 * @param sceReqMateriais
	 * @return
	 */
	public List<SceItemRms> pesquisarSceItemRmsPorSceReqMateriaisAlmoxarifado(Integer seqReqMateriais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
		criteria.add(Restrictions.eq(SceItemRms.Fields.SCE_REQ_MATERIAIS_SEQ.toString(), seqReqMateriais));
		criteria.createCriteria(SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString(), "eal", JoinType.INNER_JOIN);

	
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisa SceReqMateriais COUNT
	 * @param sceReqMateriais
	 * @return
	 */
	public Long pesquisarListaSceItemRmsPorSceReqMateriaisCount(SceReqMaterial sceReqMateriais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
		criteria.add(Restrictions.eq(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString(), sceReqMateriais));
		return executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa SceReqMateriais por dados de estoque de terceiros
	 * @param sceReqMateriais
	 * @return
	 */
	public List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriaisDadosEstoqueTerceiros(SceReqMaterial sceReqMateriais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
		criteria.createAlias(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString(), "rms");
		criteria.createAlias(SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString(), "eal");
		
		criteria.add(Restrictions.eq(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString(), sceReqMateriais));
		criteria.add(Restrictions.isNotNull("eal."+SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()));
		criteria.add(Restrictions.isNotNull(SceItemRms.Fields.QTDE_ENTREGUE.toString()));
		criteria.add(Restrictions.ltProperty("eal."+SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString(), SceItemRms.Fields.QTDE_ENTREGUE.toString()));
		criteria.add(Restrictions.isNotNull("eal."+SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString()));
		criteria.add(Restrictions.gt("eal."+SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString(), 0));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Busca SceItemRms relacionado a um centro de custo compatível com o tipo de despesa
	 * @param id
	 * @return
	 */
	public SceItemRms buscarSceItemRmsPorMaterialCompativelTipoDespesaCentroCusto(SceItemRms sceItemRms) {
		
		SceItemRmsId id = new SceItemRmsId();
		id.setEalSeq(sceItemRms.getEstoqueAlmoxarifado().getSeq());
		id.setRmsSeq(sceItemRms.getSceReqMateriais().getSeq());
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
		
		criteria.add(Restrictions.eq(SceItemRms.Fields.ID.toString(), id));

		criteria.createAlias(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString(), "rms", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("rms." + SceReqMaterial.Fields.CENTRO_CUSTOS.toString(), "fcc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("rms." + SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), "rm_gm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString(), "eal", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("eal." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mat." + SceReqMaterial.Fields.GRUPO_MATERIAL.toString(), "gmt", JoinType.LEFT_OUTER_JOIN);
		
		return (SceItemRms) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Busca SceItemRms relacionado a um centro de custo compatível com o tipo de despesa
	 * @param id
	 * @return
	 */
	public SceItemRms buscarSceItemRmsPorGrupoMaterialSemGrupo(Integer rmsSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
	
		criteria.createAlias(SceItemRms.Fields.SCE_REQ_MATERIAIS.toString(), "rms");
		criteria.add(Restrictions.eq(SceItemRms.Fields.RMS_SEQ.toString(), rmsSeq));

		List<SceItemRms> resultadoConsulta = executeCriteria(criteria);
		
		if(resultadoConsulta != null && !resultadoConsulta.isEmpty()){
			return resultadoConsulta.get(0);
		} 
		
		return null;
		
	}
	
	/**
	 * Método que retorna uma lista de itens de requisição de materiais
	 * @param seqRequisicaoMateriais
	 * @return
	 */
	public List<SceItemRms> pesquisarItensRequisicaoMateriais(Integer seqRequisicaoMateriais){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class, "IRM");
		
		criteria.createAlias("IRM." + SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "UND", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("MAT."+ScoMaterial.Fields.CODIGO.toString()),SceItemRms.Fields.CODIGO_MATERIAL.toString());
		p.add(Projections.property("MAT."+ScoMaterial.Fields.NOME.toString()),SceItemRms.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("MAT."+ScoMaterial.Fields.DESCRICAO.toString()),SceItemRms.Fields.DESCRICAO_MATERIAL.toString());
		p.add(Projections.property("UND."+ScoUnidadeMedida.Fields.CODIGO.toString()),SceItemRms.Fields.CODIGO_UNIDADE_MEDIDA.toString());
		p.add(Projections.property("FRN."+ScoFornecedor.Fields.NUMERO.toString()),SceItemRms.Fields.NUMERO_FORNECEDOR.toString());
		p.add(Projections.property("FRN."+ScoFornecedor.Fields.NOME_FANTASIA.toString()),SceItemRms.Fields.NOME_FANTASIA_FORNECEDOR.toString());
		p.add(Projections.property("IRM."+SceItemRms.Fields.QTD_REQUISITADA.toString()),SceItemRms.Fields.QTD_REQUISITADA.toString());
		p.add(Projections.property("IRM."+SceItemRms.Fields.QTDE_ENTREGUE.toString()),SceItemRms.Fields.QTDE_ENTREGUE.toString());
		p.add(Projections.property("MAT."+ScoMaterial.Fields.OBSERVACAO.toString()),SceItemRms.Fields.OBSERVACAO.toString());
		p.add(Projections.property("IRM."+SceItemRms.Fields.IND_TEM_ESTOQUE.toString()),SceItemRms.Fields.IND_TEM_ESTOQUE.toString());
		criteria.setProjection(p);	
	
		criteria.add(Restrictions.eq("IRM." + SceItemRms.Fields.RMS_SEQ.toString(), seqRequisicaoMateriais));
		criteria.addOrder(Order.asc("EAL."+SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()));
		criteria.addOrder(Order.asc("MAT."+ScoMaterial.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(SceItemRms.class));
		
		return executeCriteria(criteria);
	}

	//14497
	public List<SceItemRms> pesquisarItemDispensadoDataCorrente(Integer codigoMaterial, Short seqUnidadeFuncional,
			Integer seqRequisicaoMaterial, Integer ealSeq) {
		Date data = new Date();
		Date dataInicioTruncada = DateUtil.truncaData(data);
		Date dataFimTruncada = DateUtil.truncaData(DateUtil.adicionaDias(data, 1));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class, "IRM");

		criteria.createAlias("IRM." + SceItemRms.Fields.SCE_REQ_MATERIAIS.toString(), "RM");
		criteria.createAlias("IRM." + SceItemRms.Fields.SCE_ESTOQUE_ALMOXARIFADO.toString(), "EAL");
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM");
		criteria.createAlias("ALM." + SceAlmoxarifado.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UNF");
		
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.SEQ, ealSeq));
		criteria.add(Restrictions.eq("RM." + SceReqMaterial.Fields.SEQ.toString(), seqRequisicaoMaterial));
		criteria.add(Restrictions.between("RM." + SceReqMaterial.Fields.DATA_GERACAO.toString(), dataInicioTruncada, dataFimTruncada));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnidadeFuncional));
		
		return executeCriteria(criteria);
	}

	

	
	
	public List<SceItemRms> pesquisarRequisicaoMaterial(Integer matCodigo, Short unfSeq, Date dthrDispensacao, DominioSituacaoRequisicaoMaterial situacao, Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRms.class);
		criteria.createAlias(SceItemRms.Fields.REQUISICAO_MATERIAL.toString(), "requisicaoMaterial");
		criteria.createAlias(SceItemRms.Fields.ESTOQUE_ALMOXARIFADO.toString(), "estoqueAlmoxarifado");
		criteria.createAlias("estoqueAlmoxarifado"+"."+SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "almoxarifado");
		criteria.createAlias("almoxarifado"+"."+SceAlmoxarifado.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "unidadeFuncional");
		criteria.add(Restrictions.eq("estoqueAlmoxarifado"+"."+SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq("unidadeFuncional." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		
		/* 
		 * Caso dispensação seja por etiqueta, dthrDispensacao pode ser nulo
		 * enquanto não tiver sido dispensada toda a quantidade através das etiquetas.
		 */
		if (dthrDispensacao != null) {
			Date dthrDispensacaoInicial = DateUtil.truncaData(dthrDispensacao);
			Date dthrDispensacaoFinal = DateUtil.truncaDataFim(dthrDispensacao);
			criteria.add(Restrictions.ge("requisicaoMaterial." + SceReqMaterial.Fields.DATA_GERACAO.toString(), dthrDispensacaoInicial));
			criteria.add(Restrictions.le("requisicaoMaterial." + SceReqMaterial.Fields.DATA_GERACAO.toString(), dthrDispensacaoFinal));			
		}

		criteria.add(Restrictions.eq("requisicaoMaterial." + SceReqMaterial.Fields.SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq("requisicaoMaterial." + SceReqMaterial.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc("requisicaoMaterial." + SceReqMaterial.Fields.DATA_GERACAO.toString()));
		// Ordena trazendo primeiramente os fornecedores com menor qtdeDisponivel em estoque (de acordo com a logica da dispensacao)
		criteria.addOrder(Order.asc("estoqueAlmoxarifado." + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString()));
		return executeCriteria(criteria);
	}
}