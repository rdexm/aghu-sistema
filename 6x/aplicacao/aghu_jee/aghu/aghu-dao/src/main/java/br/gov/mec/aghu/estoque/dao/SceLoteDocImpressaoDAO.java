package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
/**
 * @modulo estoque
 * @author
 *
 */
public class SceLoteDocImpressaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceLoteDocImpressao> {
	
	private static final long serialVersionUID = 16885112270525731L;

	public enum SceLoteDocImpressaoDAOExceptionCode implements	BusinessExceptionCode {
		FORMATO_ETIQUETA_INVALIDO;
	}
	
	/**
	 * Formata o número da etiqueta, recupera os 10 primeiros digitos
	 * e invoca getLoteDocImpressaoByNroEtiqueta
	 * @param nroEtiqueta
	 * @param intervaloEtiqueta 
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public SceLoteDocImpressao getLoteDocImpressaoByNroEtiquetaFormatada(
			String nroEtiqueta, String intervaloEtiqueta) throws ApplicationBusinessException {
		
//		if(nroEtiqueta== null || nroEtiqueta.length() != 10){
//			getLoteDocImpressaoByNroEtiqueta(nroEtiqueta);
//		}
		DetachedCriteria criteria = obterCriteriaGetLoteDocImpressaoByNroEtiqueta(nroEtiqueta, intervaloEtiqueta);
		
		return (SceLoteDocImpressao) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria obterCriteriaGetLoteDocImpressaoByNroEtiqueta(
			String nroEtiqueta, String intervaloEtiqueta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteDocImpressao.class);
		criteria.add(Restrictions.eq(SceLoteDocImpressao.Fields.SEQUENCIAL.toString(), new BigDecimal(nroEtiqueta).longValue()));
		
		criteria.add(Restrictions.le(SceLoteDocImpressao.Fields.NRO_INICIAL.toString(), new BigDecimal(intervaloEtiqueta).intValue()));
		criteria.add(Restrictions.ge(SceLoteDocImpressao.Fields.NRO_FINAL.toString(), new BigDecimal(intervaloEtiqueta).intValue()));
		return criteria;
	}

	public List<SceLoteDocImpressao> pesquisarLoteDocImpressao(SceLoteDocImpressao entidadePesquisa,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		
		DetachedCriteria cri = getCriteriaPesquisarLoteDocImpressao(entidadePesquisa);
		validadeMaiorQueAtualENroNfEntradaNotNull(cri);
		cri.addOrder(Order.desc(SceLoteDocImpressao.Fields.SEQUENCIAL.toString()));
		return executeCriteria(cri, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarLoteDocImpressaoCount(SceLoteDocImpressao entidadePesquisa) {
		DetachedCriteria cri = getCriteriaPesquisarLoteDocImpressao(entidadePesquisa);
		validadeMaiorQueAtualENroNfEntradaNotNull(cri);
		return executeCriteriaCount(cri);
	}

	private void validadeMaiorQueAtualENroNfEntradaNotNull(DetachedCriteria cri){
		cri.add(Restrictions.isNotNull(SceLoteDocImpressao.Fields.NRO_NF_ENTRADA.toString()));
		cri.add(Restrictions.gt(SceLoteDocImpressao.Fields.DT_VALIDADE.toString(), DateUtil.hojeSeNull(null)));
	}
	private DetachedCriteria getCriteriaPesquisarLoteDocImpressao(
			SceLoteDocImpressao loteDocImp) {
		DetachedCriteria cri = DetachedCriteria.forClass(SceLoteDocImpressao.class);
		
		cri.createAlias(SceLoteDocImpressao.Fields.MATERIAL.toString(), "material", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(SceLoteDocImpressao.Fields.MARCA_COMERCIAL.toString(), "marcaComercial", JoinType.LEFT_OUTER_JOIN);
		
		if(loteDocImp.getNroNfEntrada() != null){
			cri.add(Restrictions.eq(SceLoteDocImpressao.Fields.NRO_NF_ENTRADA.toString(), loteDocImp.getNroNfEntrada()));
		}
		if(loteDocImp.getInrNrsSeq() != null){
			cri.add(Restrictions.eq(SceLoteDocImpressao.Fields.INS_NRS_SEQ.toString(), loteDocImp.getInrNrsSeq()));
		}
		if(loteDocImp.getMaterial() != null){
			cri.add(Restrictions.eq(SceLoteDocImpressao.Fields.MATERIAL.toString(), loteDocImp.getMaterial()));
		}
		if(!StringUtils.isEmpty(loteDocImp.getLoteCodigo())){
			cri.add(Restrictions.eq(SceLoteDocImpressao.Fields.LOTE_CODIGO.toString(), loteDocImp.getLoteCodigo()));
		}
		if(loteDocImp.getDtValidade() != null){
			cri.add(Restrictions.eq(SceLoteDocImpressao.Fields.DT_VALIDADE.toString(), loteDocImp.getDtValidade()));
		}
		if(loteDocImp.getMarcaComercial()!= null){
			cri.add(Restrictions.eq(SceLoteDocImpressao.Fields.MARCA_COMERCIAL.toString(), loteDocImp.getMarcaComercial()));
		}
		return cri;
	}

	public List<SceLoteDocImpressao> pesquisarPorDadosInformados(SceLoteDocImpressao entidade) {
		
		DetachedCriteria criteria = montarCriteria(entidade);
	
		criteria.addOrder(Order.desc(SceLoteDocImpressao.Fields.SEQUENCIAL.toString()));
		criteria.addOrder(Order.desc(SceLoteDocImpressao.Fields.NRO_FINAL.toString()));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarCriteria(SceLoteDocImpressao entidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteDocImpressao.class);
		
		if(entidade.getMaterial() != null){
			criteria.add(Restrictions.eq(SceLoteDocImpressao.Fields.MATERIAL.toString(), entidade.getMaterial()));
		}
		if(entidade.getLoteCodigo()!=null && !entidade.getLoteCodigo().isEmpty()){
			criteria.add(Restrictions.eq(SceLoteDocImpressao.Fields.LOTE_CODIGO.toString(), entidade.getLoteCodigo()));
		}
		if(entidade.getDtValidade() != null){
			criteria.add(Restrictions.eq(SceLoteDocImpressao.Fields.DT_VALIDADE.toString(), entidade.getDtValidade()));
		}
		if(entidade.getMarcaComercial()!= null){
			criteria.add(Restrictions.eq(SceLoteDocImpressao.Fields.MARCA_COMERCIAL.toString(), entidade.getMarcaComercial()));		
		}
		return criteria;
	}
	
	public List<SceLoteDocImpressao> pesquisarLoteDocImpSemLoteDocumento(SceLoteDocImpressao entidade) {
		
		DetachedCriteria criteria = montarCriteria(entidade);
		
		criteria.add(Restrictions.isNull(SceLoteDocImpressao.Fields.LOTE_X_DOCUMENTO.toString()));		
		
		return executeCriteria(criteria);
	}

	public List<SceLoteDocImpressao> pesquisarLoteDocImpressaoPorInrNrsSeq(
			Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteDocImpressao.class);
		criteria.createAlias(SceLoteDocImpressao.Fields.LOTE_X_DOCUMENTO.toString(), "ldc");
		criteria.add(Restrictions.eq(SceLoteDocImpressao.Fields.INS_NRS_SEQ.toString(), seq));
		
		return executeCriteria(criteria);
	}
	
	public List<SceLoteDocImpressao> pesquisarLoteDocImpressaoPorLdcSeq(
			Integer ldcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteDocImpressao.class);
		criteria.add(Restrictions.eq(SceLoteDocImpressao.Fields.LOTE_X_DOCUMENTO_SEQ.toString(), ldcSeq));
		
		return executeCriteria(criteria);
	}
	
}