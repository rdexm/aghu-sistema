package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioAgruparRelMensal;
import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.core.commons.CoreUtil;
import org.hibernate.criterion.*;

/**
 * @modulo compras
 * @author cvagheti
 *
 */
public class ScoGrupoMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoGrupoMaterial> {

	private static final long serialVersionUID = 4497825107167582866L;

	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorFiltro(Object _input) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoMaterial.class);

		String descricao = (String) _input;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(descricao)){
			codigo = Integer.valueOf(descricao);
			descricao = null;
		}	

		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoGrupoMaterial.Fields.CODIGO.toString(),
					codigo));
		}

		if (descricao != null) {
			criteria.add(Restrictions.ilike(ScoGrupoMaterial.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(ScoGrupoMaterial.Fields.CODIGO.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarGrupoMaterialPorFiltroCount(Object _input) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoMaterial.class);

		String descricao = (String) _input;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(descricao)){
			codigo = Integer.valueOf(descricao);
			descricao = null;
		}	

		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoGrupoMaterial.Fields.CODIGO.toString(),
					codigo));
		}

		if (descricao != null) {
			criteria.add(Restrictions.ilike(ScoGrupoMaterial.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		return executeCriteriaCount(criteria);
	}

	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorFiltroAlmoxarifado(Short almoxSeq, Object _input) {

		String descricao = (String) _input;
		Integer codigo = null;


		if (CoreUtil.isNumeroInteger(descricao)){
			codigo = Integer.valueOf(descricao);
			descricao = null;
		}

		StringBuilder stbQuery  = new StringBuilder(400).append("	select  distinct this_.*");
		stbQuery.append("	from    AGH.sco_grupos_materiais this_,");
		stbQuery.append("       	AGH.SCO_MATERIAIS mat1_,");
		stbQuery.append("        	AGH.sce_almoxarifados alm2_");
		stbQuery.append("	where   this_.CODIGO=mat1_.gmt_codigo");
		stbQuery.append("	and     mat1_.alm_seq=alm2_.seq ");

		if (codigo != null) {
			stbQuery.append("	and   this_.codigo = :codigo ");
		}

		if (descricao != null && !descricao.trim().equals("")) {
			stbQuery.append("	and   this_.descricao like '%"+descricao.trim().toUpperCase() +"%' ");
		}

		if(almoxSeq!=null){
			stbQuery.append("	and   alm2_.seq = :almSeq ");
		}
		stbQuery.append("order by  this_.CODIGO asc");
		javax.persistence.Query query = this.createNativeQuery(stbQuery.toString(), ScoGrupoMaterial.class);

		if (codigo != null) {
			query.setParameter("codigo", codigo);
		}
		if(almoxSeq!=null){
			query.setParameter("almSeq", almoxSeq);
		}
		return (List<ScoGrupoMaterial>)query.getResultList();
	}
	
	/**
	 * Obtem ScoGrupoMaterial por codigo ou descricao
	 * @param param
	 * @return
	 */
	public List<ScoGrupoMaterial> obterGrupoMaterialPorSeqDescricao(Object param) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoMaterial.class);
        
        String strPesquisa = (String) param;
		
        if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroLong(strPesquisa)) {
				if (param != null && !param.equals("")) {
		            criteria.add(Restrictions.eq(ScoGrupoMaterial.Fields.CODIGO.toString(), Integer.parseInt(param.toString())));
		            criteria.addOrder(Order.asc(ScoGrupoMaterial.Fields.CODIGO.toString()));
		        }
			} else {
				criteria.add(Restrictions.ilike(ScoGrupoMaterial.Fields.DESCRICAO.toString(), StringUtils.trim(param.toString()), MatchMode.ANYWHERE));
			}
        }        
        criteria.addOrder(Order.asc(ScoGrupoMaterial.Fields.CODIGO.toString()));
        List<ScoGrupoMaterial> result =executeCriteria(criteria, 0, 100, null, true);
        return result;
	}
	
	public Long obterGrupoMaterialPorSeqDescricaoCount(Object param) {
		DetachedCriteria criteria = montarCriteriaGrupoMaterialPorSeqDescricao(param);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaGrupoMaterialPorSeqDescricao(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoMaterial.class);
		return criteria;
	}
	
	/**
	 * 
	 * @param dataCompetencia
	 * @param codigo
	 * @return
	 */
	public List<ScoGrupoMaterial> pesquisaRelMensalMateriais(Date dataCompetencia, Integer codigo,DominioAgruparRelMensal agrupar) {

		StringBuilder hql = new StringBuilder(400);
		hql.append("select GMT.").append(ScoGrupoMaterial.Fields.CODIGO.toString());
		hql.append(", GMT.").append(ScoGrupoMaterial.Fields.DESCRICAO.toString());
		hql.append(", GMT.").append(ScoGrupoMaterial.Fields.NTD_CODIGO.toString());
		hql.append(" from ").append(ScoGrupoMaterial.class.getSimpleName()).append(" GMT ");
		hql.append(" where GMT.").append(ScoGrupoMaterial.Fields.CODIGO.toString()).append(" IN ( ");
		hql.append(" 			select GM.").append(ScoGrupoMaterial.Fields.CODIGO.toString());
		hql.append(" 			from ").append(ScoGrupoMaterial.class.getSimpleName()).append(" GM, ");
		hql.append(ScoMaterial.class.getSimpleName()).append(" MAT, ");
		hql.append(SceMovimentoMaterial.class.getSimpleName()).append(" MMT ");
		hql.append(" 			where MMT.").append(SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString()).append(" = :dtCompetencia ");
		hql.append(" 			and   MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = ").append("MMT.").append(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString());
		hql.append(" 			and    GM.").append(ScoGrupoMaterial.Fields.CODIGO.toString()).append(" = ").append("MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString());
		hql.append(" 			) OR ");
		hql.append("    GMT.").append(ScoGrupoMaterial.Fields.CODIGO.toString()).append(" IN ( ");
		hql.append(" 			select GM.").append(ScoGrupoMaterial.Fields.CODIGO.toString());
		hql.append(" 			from ").append(ScoGrupoMaterial.class.getSimpleName()).append(" GM, ");
		hql.append(ScoMaterial.class.getSimpleName()).append(" MAT, ");
		hql.append(SceEstoqueGeral.class.getSimpleName()).append(" EGR ");
		hql.append(" 			where EGR.").append(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()).append(" = :dtCompetencia ");
		hql.append(" 			and   EGR.").append(SceEstoqueGeral.Fields.FRN_NUMERO.toString()).append(" = 1");
		hql.append(" 			and   EGR.").append(SceEstoqueGeral.Fields.VALOR.toString()).append("<> 0");
		hql.append(" 			and   MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = ").append("EGR.").append(SceEstoqueGeral.Fields.MAT_CODIGO.toString());
		hql.append(" 			and   GM.").append(ScoGrupoMaterial.Fields.CODIGO.toString()).append(" = ").append("MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString());

		if(agrupar == null || agrupar.equals(DominioAgruparRelMensal.GRUPO_MATERIAL)){
			hql.append(" 			) ORDER BY").append(" GMT.").append(ScoGrupoMaterial.Fields.CODIGO.toString());
		}else{
			hql.append(" 			) ORDER BY").append(" GMT.").append(ScoGrupoMaterial.Fields.NTD_CODIGO.toString());
		}

		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("dtCompetencia", dataCompetencia);
		List<Object[]> lista = query.getResultList();
		List<ScoGrupoMaterial> listGrupoMaterial = null;

		ScoGrupoMaterial grupoMaterial = null;
		if (lista != null && !lista.isEmpty()) {
			listGrupoMaterial = new ArrayList<ScoGrupoMaterial>();
			for (Object[] listFields : lista) {
				grupoMaterial = new ScoGrupoMaterial();

				grupoMaterial.setCodigo((Integer) listFields[0]);
				grupoMaterial.setDescricao((String) listFields[1]);
				grupoMaterial.setNtdCodigo((Integer) listFields[2]);

				listGrupoMaterial.add(grupoMaterial);
			}
		}
		return listGrupoMaterial;
	}

	/**
	 * Obtem natureza de despesa cadastrada no grupo do material.
	 * 
	 * @param materialId ID do material.
	 * @return Natureza de despesa.
	 */
	public FsoNaturezaDespesa obtemNaturezaDespesaPorMaterial(Integer materialId) {
		final String NTD = "ntd", GM = "gm", M = "m";
		
		DetachedCriteria materialInGrupo = DetachedCriteria
				.forClass(ScoMaterial.class, M)
				.setProjection(Projections.id())
				.add(Restrictions.eqProperty(M + "."
						+ ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(),
						GM + "." + ScoGrupoMaterial.Fields.CODIGO.toString()))
				.add(Restrictions.eq(
						M + "." + ScoMaterial.Fields.CODIGO.toString(),
						materialId));

		DetachedCriteria naturezaInGrupo = DetachedCriteria
				.forClass(ScoGrupoMaterial.class, GM)
				.setProjection(Projections.id())
				.add(Restrictions.eqProperty(GM + "."
						+ ScoGrupoMaterial.Fields.NTD_CODIGO.toString(), NTD
						+ "." + FsoNaturezaDespesa.Fields.CODIGO.toString()))
				.add(Subqueries.exists(materialInGrupo));
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FsoNaturezaDespesa.class, NTD)
				.add(Subqueries.exists(naturezaInGrupo))
				.add(Restrictions.eq(
						FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(),
						DominioSituacao.A));

		List<FsoNaturezaDespesa> r = executeCriteria(criteria);
		return r.isEmpty() ? null : r.get(0);
	}
	
	/**
	 * Verifica se existe uma ScoMaterial pelo código do ScoGrupoMaterial
	 * 
	 * C8 de #31584
	 * 
	 * @param gmtCodigo
	 * @return
	 */
	public boolean verificarExistenciaScoMaterialPorScoGrupoMaterial(Integer gmtCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);
		criteria.createAlias(ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
		criteria.add(Restrictions.eq("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), gmtCodigo));
		return super.executeCriteriaExists(criteria);
	}
		


	/**
	 * Add uma restriction se o valor diferente de null ou String vazia
	 * 
	 * @param criteria
	 * @param field
	 * @param value
	 */
	private void addEqFilterRestriction(DetachedCriteria criteria, ScoGrupoMaterial.Fields field, Object value) {
		if (value != null) {
			criteria.add(Restrictions.eq(field.toString(), value));
		}
	}
	
	/**
	 * C1 de #31584
	 * 
	 * @param codigo
	 * @param descricao
	 * @param patrimonio
	 * @param engenhari
	 * @param nutricao
	 * @param exigeForn
	 * @param geraMovEstoque
	 * @param controleValidade
	 * @param dispensario
	 * @param ntdCodigo
	 * @param codMercadoriaBb
	 * @param diaFavEntgMaterial
	 * @param seqAgrupa
	 * @return
	 */
	public List<ScoGrupoMaterial> pesquisarScoGrupoMaterial(Integer codigo, String descricao, Boolean patrimonio, Boolean engenhari, Boolean nutricao,
			Boolean exigeForn, Boolean geraMovEstoque, Boolean controleValidade, Boolean dispensario, Integer ntdCodigo, Integer codMercadoriaBb,
			DominioDiaSemanaMes diaFavEntgMaterial, Short seqAgrupa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoMaterial.class);

		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.CODIGO, codigo);
		
		if (descricao != null) {
			criteria.add(Restrictions.ilike(ScoGrupoMaterial.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.IND_PATRIMONIO, patrimonio);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.IND_ENGENHARI, engenhari);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.IND_NUTRICAO, nutricao);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.IND_EXIGE_FORN, exigeForn);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.IND_GERA_MOV_ESTOQUE, geraMovEstoque);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.IND_CONTROLE_VALIDADE, controleValidade);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.IND_CONTROLE_VALIDADE, controleValidade);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.IND_DISPENSARIO, dispensario);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.NTD_CODIGO, ntdCodigo);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.COD_MERCADORIA_BB, codMercadoriaBb);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.DIA_FAV_ENTG_MATERIAL, diaFavEntgMaterial);
		this.addEqFilterRestriction(criteria, ScoGrupoMaterial.Fields.DIA_FAV_ENTG_MATERIAL, diaFavEntgMaterial);
		
		if (seqAgrupa != null) {
			criteria.createAlias(ScoGrupoMaterial.Fields.AGRUPA_GRUPO_MATERIAL.toString(), "AGRUPA_GRUPO_MATERIAL");
			criteria.add(Restrictions.eq("AGRUPA_GRUPO_MATERIAL." + FcuAgrupaGrupoMaterial.Fields.SEQ.toString(), seqAgrupa));
		}
		
		criteria.addOrder(Order.asc(ScoGrupoMaterial.Fields.CODIGO.toString()));
	
		return super.executeCriteria(criteria);
	}
	
	/**
	 * #46298 - Obtem Lista de Grupo Material por Codigo ou Descrição
	 */
	public List<ScoGrupoMaterial> obterListaGrupoMaterialPorCodigoOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoMaterial.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)){
				criteria.add(Restrictions.eq(ScoGrupoMaterial.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(ScoGrupoMaterial.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		return executeCriteria(criteria, 0, 100, ScoGrupoMaterial.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * #46298 - Obtem Count de Grupo Material por Codigo ou Descrição
	 */
	public Long obterCountGrupoMaterialPorCodigoOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoMaterial.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)){
				criteria.add(Restrictions.eq(ScoGrupoMaterial.Fields.CODIGO.toString(), Integer.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(ScoGrupoMaterial.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		return executeCriteriaCount(criteria);
	}
}