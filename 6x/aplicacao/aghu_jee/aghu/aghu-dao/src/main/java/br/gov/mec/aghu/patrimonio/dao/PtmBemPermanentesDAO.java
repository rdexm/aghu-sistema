package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PtmBemPermanentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PtmBemPermanentes> {

	private static final String ALIAS_PBP = "PBP";
	private static final String ALIAS_PBP_PONTO = "PBP.";
	private static final long serialVersionUID = 2722688179512805728L;

	/**
	 * Obtém lista de Bens Permanentes associados ao Item de Recebimento informado.
	 * 
	 * @param seqItemRecebimento - Código do Item de Recebimento
	 * @return Lista de Bens Permanentes
	 */
	public List<PtmBemPermanentes> listarBemPermanentePorItemRecebimento(Long seqItemRecebimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class);

		criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.IRP_SEQ.toString(), seqItemRecebimento));

		return executeCriteria(criteria);
	}

	/**
	 * SB1 44713 Suggestion para o campo Patrimônio.
	 * @param param - NR bem / detalhamento
	 * @return Lista de Bens Permanentes
	 */
	public List<PtmBemPermanentes> listarSugestionPatrimonio(Object param) {
		DetachedCriteria criteria = listarSugestionPatrimonioCriteria(param);
		criteria.addOrder(Order.asc(PtmBemPermanentes.Fields.DETALHAMENTO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	private DetachedCriteria listarSugestionPatrimonioCriteria(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class);
		if(param != null && !StringUtils.isEmpty(param.toString())){
			if(StringUtils.isNumeric(param.toString())){
				criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.NR_BEM.toString(),Long.valueOf(param.toString())));
				if(!executeCriteriaExists(criteria)){
					criteria =  DetachedCriteria.forClass(PtmBemPermanentes.class);
					criteria.add(Restrictions.ilike(PtmBemPermanentes.Fields.DETALHAMENTO.toString(),StringUtils.lowerCase(param.toString()),MatchMode.ANYWHERE));
				}
			}else{
				criteria =  DetachedCriteria.forClass(PtmBemPermanentes.class);
				criteria.add(Restrictions.ilike(PtmBemPermanentes.Fields.DETALHAMENTO.toString(),StringUtils.lowerCase(param.toString()),MatchMode.ANYWHERE));
			}	
		}
		return criteria;
	}
	
	public Long listarSugestionPatrimonioCount(Object param){
		DetachedCriteria criteria = listarSugestionPatrimonioCriteria(param);
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Obtém um Bem Permanente a partir de seu número de patrimônio, caso exista.
	 * 
	 * @param numeroBem - Número de patrimônio
	 * @return Bem Permanente encontrado
	 */
	public PtmBemPermanentes obterBemPermanentePorNumeroBem(Long numeroBem) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class);

		criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.NR_BEM.toString(), numeroBem));
		criteria.add(Restrictions.isNotNull(PtmBemPermanentes.Fields.NR_BEM.toString()));

		List<PtmBemPermanentes> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}

		return retorno.get(0);
	}
	
	/**
	 * C1 #43475
	 * 
	 * @param numRecebimento, vlrNumerico
	 * @return 
	 */
	public List<DevolucaoBemPermanenteVO> carregarListaBemPermanente(Long seqItemPatrimonio, Integer vlrNumerico){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class,ALIAS_PBP);
		criteria.createAlias(ALIAS_PBP_PONTO + PtmBemPermanentes.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PBP_PONTO + PtmBemPermanentes.Fields.PIRP_SEQ.toString(), "PIRP", JoinType.INNER_JOIN);
		criteria.createAlias("PIRP." + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(), "SIRP", JoinType.INNER_JOIN);
		

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.SEQ.toString()), DevolucaoBemPermanenteVO.Fields.PBP_SEQ.toString())
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.NR_SERIE.toString()),	DevolucaoBemPermanenteVO.Fields.PBP_NUMERO_SERIE.toString())
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.NR_BEM.toString()), DevolucaoBemPermanenteVO.Fields.PBP_NUMERO_BEM.toString())
				.add(Projections.property("MAT."+ScoMaterial.Fields.CODIGO.toString()), DevolucaoBemPermanenteVO.Fields.MAT_CODIGO.toString())
				.add(Projections.property("MAT."+ScoMaterial.Fields.NOME.toString()),DevolucaoBemPermanenteVO.Fields.MAT_NOME.toString())
				.add(Projections.property("PIRP."+PtmItemRecebProvisorios.Fields.SEQ.toString()), DevolucaoBemPermanenteVO.Fields.PIRP_SEQ.toString())
				.add(Projections.property("SIRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString()), DevolucaoBemPermanenteVO.Fields.SIRP_NUMERO_SEQ.toString())
				.add(Projections.property("SIRP."+SceItemRecebProvisorio.Fields.ESL_SEQ.toString()), DevolucaoBemPermanenteVO.Fields.SIRP_ESL.toString())
				.add(Projections.property("SIRP."+SceItemRecebProvisorio.Fields.NRO_ITEM.toString()), DevolucaoBemPermanenteVO.Fields.SIRP_NUMERO_ITEM.toString()));
	
		
		criteria.add(Restrictions.ne(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.CC_SEQ.toString(), vlrNumerico));
		
		criteria.add(Restrictions.eq("PIRP."+ PtmItemRecebProvisorios.Fields.SEQ.toString(), seqItemPatrimonio));
		criteria.setResultTransformer(Transformers.aliasToBean(DevolucaoBemPermanenteVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * C5 #43475
	 * 
	 * @param numRecebimento
	 * @return 
	 */
	public Long countQtdDisp(Long numRecebimento, boolean diferenteCentroCusto, Integer vlrNumerico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class, ALIAS_PBP);

		criteria.add(Restrictions.eq(ALIAS_PBP_PONTO + PtmBemPermanentes.Fields.IRP_SEQ.toString(), numRecebimento));
		
		if (diferenteCentroCusto) {
			criteria.add(Restrictions.ne(ALIAS_PBP_PONTO	+ PtmBemPermanentes.Fields.CC_SEQ.toString(), vlrNumerico));
		} else {
			criteria.add(Restrictions.eq(ALIAS_PBP_PONTO	+ PtmBemPermanentes.Fields.CC_SEQ.toString(), vlrNumerico));
		}
		return executeCriteriaCount(criteria);
	}

	public PtmBemPermanentes obterBemPermanentePorSeq(Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class, ALIAS_PBP);
		criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.SEQ.toString(), seq));

		List<PtmBemPermanentes> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}

		return retorno.get(0);
	}
	
	/**
	 * #44799 C8
	 * @param filtro
	 * @return
	 */
	public List<PtmBemPermanentes> obterPtmBemPermanentesPorNumeroDescricao(Object filtro){
		DetachedCriteria criteria = obterCriteriaPtmBemPermanentesPorNumeroDescricao(filtro, false);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PtmBemPermanentes.Fields.SEQ.toString()).as(PtmBemPermanentes.Fields.SEQ.toString()))
				.add(Projections.property(PtmBemPermanentes.Fields.NR_SERIE.toString()).as(PtmBemPermanentes.Fields.NR_SERIE.toString()))
				.add(Projections.property(PtmBemPermanentes.Fields.NR_BEM.toString()).as(PtmBemPermanentes.Fields.NR_BEM.toString()))
				.add(Projections.property(PtmBemPermanentes.Fields.DETALHAMENTO.toString()).as(PtmBemPermanentes.Fields.DETALHAMENTO.toString())
				));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PtmBemPermanentes.class));
		criteria.addOrder(Order.asc(PtmBemPermanentes.Fields.DETALHAMENTO.toString()));
		
		if(CoreUtil.isNumeroLong(filtro.toString())){
			List<PtmBemPermanentes> listaRetorno = executeCriteria(criteria, 0, 100, null, false);
			
			if(listaRetorno != null && !listaRetorno.isEmpty()){
				return listaRetorno;
			}else{
				DetachedCriteria criteriaListaVazia = obterCriteriaPtmBemPermanentesPorNumeroDescricao(filtro, true);
				return executeCriteria(criteriaListaVazia, 0, 100, null, false);
			}
		}
		
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long obterPtmBemPermanentesPorNumeroDescricaoCount(Object filtro){
		DetachedCriteria criteria = obterCriteriaPtmBemPermanentesPorNumeroDescricao(filtro, false);
		
		if(CoreUtil.isNumeroLong(filtro.toString())){
			Long valorRetorno = executeCriteriaCount(criteria);
			
			if(valorRetorno != null && valorRetorno > 0){
				return valorRetorno;
			}else{
				DetachedCriteria criteriaListaVazia = obterCriteriaPtmBemPermanentesPorNumeroDescricao(filtro, true);
				return executeCriteriaCount(criteriaListaVazia);
			}
		}
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaPtmBemPermanentesPorNumeroDescricao(Object filtro, boolean listaVazia){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class);
		
		criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.SITUACAO.toString(), "1"));
		
		String filtroPesquisa = filtro.toString();
		
		if(StringUtils.isNotBlank(filtroPesquisa)){
			if(CoreUtil.isNumeroLong(filtroPesquisa) && !listaVazia){
				criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.NR_BEM.toString(), Long.valueOf(filtroPesquisa)));
			}else{
				criteria.add(Restrictions.ilike(PtmBemPermanentes.Fields.DETALHAMENTO.toString(), filtroPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	public PtmBemPermanentes obterBemPermanentesPorNumeroBem(Long numeroBem){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class);
		criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.NR_BEM.toString(), numeroBem));
		return (PtmBemPermanentes) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #48782 C3 SB Seleção itens de patrimônio
	 * @param param
	 * @return
	 */
	public List<PtmBemPermanentes> pesquisarSugestionPatrimonio(String param) {
		DetachedCriteria criteria = montarCriteriaBemPermanente(param);
		return executeCriteria(criteria, 0, 100, PtmBemPermanentes.Fields.NR_BEM.toString(), true);
	}
	
	public Long pesquisarSugestionPatrimonioCount(String param) {
		List<PtmBemPermanentes>  lista = executeCriteria(montarCriteriaBemPermanente(param));
		return Long.valueOf(lista.size());
	}
	
	private DetachedCriteria montarCriteriaBemPermanente(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class);
		if (StringUtils.isNotBlank((String) param)) {
			if(CoreUtil.isNumeroLong(param)) {
				criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.NR_BEM.toString(),Long.valueOf(param)));
			} else {
				criteria.add(Restrictions.ilike(PtmBemPermanentes.Fields.NR_SERIE.toString(),StringUtils.lowerCase(param),MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	//#48782 C9
	public List<DevolucaoBemPermanenteVO> pesquisarBensPermanentesPorSeqPirp(Long seq, boolean edicao, Integer avtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class, ALIAS_PBP);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.DETALHAMENTO.toString()), DevolucaoBemPermanenteVO.Fields.PBP_DETALHAMENTO.toString())
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.NR_BEM.toString()), DevolucaoBemPermanenteVO.Fields.PBP_NUMERO_BEM.toString())
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.NR_SERIE.toString()), DevolucaoBemPermanenteVO.Fields.PBP_NUMERO_SERIE.toString())
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.SEQ.toString()), DevolucaoBemPermanenteVO.Fields.PBP_SEQ.toString())
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.AVT_SEQ.toString()), DevolucaoBemPermanenteVO.Fields.AVT_SEQ.toString())
				);
		
		if(!edicao){
			criteria.add(Restrictions.isNull(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.AVT_SEQ.toString()));
		}else{
			criteria.add(Restrictions.or(Restrictions.isNull(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.AVT_SEQ.toString()),
					Restrictions.eq(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.AVT_SEQ.toString(), avtSeq)));
		}
		
		criteria.add(Restrictions.eq(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.IRP_SEQ.toString(), seq));
		criteria.addOrder(Order.asc(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.NR_BEM.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(DevolucaoBemPermanenteVO.class));
		return executeCriteria(criteria);
	}
	
	//#48782 C18
	public List<PtmBemPermanentes> verificarStatusItemReceb(Long seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class, ALIAS_PBP);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.STATUS_ACEITE_TECNICO.toString()),
						PtmBemPermanentes.Fields.STATUS_ACEITE_TECNICO.toString())
				);
		
		criteria.add(Restrictions.eq(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.IRP_SEQ.toString(), seq));
		criteria.addOrder(Order.asc(ALIAS_PBP_PONTO+PtmBemPermanentes.Fields.STATUS_ACEITE_TECNICO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PtmBemPermanentes.class));
		return executeCriteria(criteria);
	}
	
	public List<PtmBemPermanentes> obterPorAvtSeq(Integer avtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmBemPermanentes.class);
		criteria.add(Restrictions.eq(PtmBemPermanentes.Fields.AVT_SEQ.toString(), avtSeq));
		return executeCriteria(criteria);
	}
}
