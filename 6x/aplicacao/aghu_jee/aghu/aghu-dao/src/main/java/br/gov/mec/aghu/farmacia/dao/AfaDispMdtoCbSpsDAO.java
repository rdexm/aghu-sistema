package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.SceLoteDocImpressao;


public class AfaDispMdtoCbSpsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaDispMdtoCbSps> {
	
	private static final long serialVersionUID = 1304313741574313893L;
	private static final String ALIAS_AFA_DISP_MDTO_CB_SPS = "DMCS";
	
	/**
	 * Pesquisa a quantidade de etiquetas lidas para o SEQ de AfaDispensacao passado via parametro
	 * @param seqAfaDispMdto
	 * @return
	 */
	public Long getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(Long seqAfaDispMdto, DominioIndExcluidoDispMdtoCbSps indExcluidoDispMdtoCb){		
		DetachedCriteria criteria = getCriteriaMdtoCbSpsIncluidoByDispensacaoMdto(seqAfaDispMdto, indExcluidoDispMdtoCb);
		return executeCriteriaCountDistinct(criteria, AfaDispMdtoCbSps.Fields.SEQ.toString(), Boolean.FALSE);
	}
	
	public List<AfaDispMdtoCbSps> pesquisarDispMdtoCbSpsByDispensacaoMdto(Long seqAfaDispMdto, DominioIndExcluidoDispMdtoCbSps indExcluidoDispMdtoCb){		
		DetachedCriteria criteria = getCriteriaMdtoCbSpsIncluidoByDispensacaoMdto(seqAfaDispMdto, indExcluidoDispMdtoCb);
		return executeCriteria(criteria);
	}
	
	//#5465

	/**
	 * Pesquisa a quantidade de etiquetas lidas de acordo com o numero da etiqueta
	 * @param etiqueta
	 * @param indExcluido
	 * @return qtdeEtiquetas
	 */
	public List<AfaDispMdtoCbSps> obterListaDispMdtoCbSpsPorEtiqueta(
			String etiqueta, DominioIndExcluidoDispMdtoCbSps indExcluido) {		
		DetachedCriteria criteria = getCriteriaDispMdtoCbSpsByEtiqueta(etiqueta,indExcluido);
		return executeCriteria(criteria);
	}
	/**
	 * Pesquisa a quantidade de etiquetas lidas de acordo com o numero da etiqueta
	 * @param etiqueta
	 * @param indExcluido
	 * @return qtdeEtiquetas
	 *
	public Long getQtdeDispMdtoCbSpsByEtiqueta(
			String etiqueta, DominioIndExcluidoDispMdtoCbSps indExcluido) {		
		DetachedCriteria criteria = getCriteriaDispMdtoCbSpsByEtiqueta(etiqueta,indExcluido);
		return executeCriteriaCountDistinct(criteria, AfaDispMdtoCbSps.Fields.NRO_ETIQUETA.toString(), Boolean.FALSE);
	}
	*/
	
	public List<AfaDispMdtoCbSps> getDispMdtoCbSpsIncluidoByDispensacaoMdto(Long seqAfaDispMdto, DominioIndExcluidoDispMdtoCbSps indExcluidoDispMdtoCb){
		DetachedCriteria criteria = getCriteriaMdtoCbSpsIncluidoByDispensacaoMdto(seqAfaDispMdto, indExcluidoDispMdtoCb);
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaMdtoCbSpsIncluidoByDispensacaoMdto(
			Long seqAfaDispMdto, DominioIndExcluidoDispMdtoCbSps indExcluidoDispMdtoCb) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispMdtoCbSps.class, ALIAS_AFA_DISP_MDTO_CB_SPS);
		criteria.add(Restrictions.eq(AfaDispMdtoCbSps.Fields.DISPENSACAO_SEQ.toString(), seqAfaDispMdto));
		
		if(indExcluidoDispMdtoCb != null){
			criteria.add(Restrictions.eq(AfaDispMdtoCbSps.Fields.IND_EXCLUIDO.toString(), indExcluidoDispMdtoCb));
		}
		
		return criteria;
	}
	
	public AfaDispMdtoCbSps getDispMdtoCbSpsByEtiqueta(String etiqueta,
			DominioIndExcluidoDispMdtoCbSps indExcluido) {
		return getDispMdtoCbSpsByEtiqueta(etiqueta, indExcluido, null);
	}
	
	public AfaDispMdtoCbSps getDispMdtoCbSpsByEtiqueta(String etiqueta,
			DominioIndExcluidoDispMdtoCbSps indExcluido, String loteCodigo) {
		DetachedCriteria cri = getCriteriaDispMdtoCbSpsByEtiqueta(etiqueta, indExcluido);
		Object t = executeCriteriaUniqueResult(cri);
			if(t != null){
				return (AfaDispMdtoCbSps)t;
			}else{
				return null;
			}
	}
	
	private DetachedCriteria getCriteriaDispMdtoCbSpsByEtiqueta(
			String etiqueta, DominioIndExcluidoDispMdtoCbSps indExcluido) {
		return getCriteriaDispMdtoCbSpsByEtiqueta(etiqueta, indExcluido, null);
	}
	private DetachedCriteria getCriteriaDispMdtoCbSpsByEtiqueta(
			String etiqueta, DominioIndExcluidoDispMdtoCbSps indExcluido, String loteCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispMdtoCbSps.class, ALIAS_AFA_DISP_MDTO_CB_SPS);
		criteria.add(Restrictions.eq(AfaDispMdtoCbSps.Fields.NRO_ETIQUETA.toString(), etiqueta));
		
		if(indExcluido != null){
			criteria.add(Restrictions.eq(AfaDispMdtoCbSps.Fields.IND_EXCLUIDO.toString(), indExcluido));
		}
		
		if(StringUtils.isNotBlank(loteCodigo)){
			criteria.createAlias(AfaDispMdtoCbSps.Fields.LOTE_DOC_IMPRESSAO.toString(), "lot");
			criteria.add(Restrictions.eq("lot."+SceLoteDocImpressao.Fields.LOTE_CODIGO.toString(), loteCodigo));
		}
		return criteria;
	}
	
	public Long getDispMdtoCbSpsByEtiquetaCount(String etiqueta,
			DominioIndExcluidoDispMdtoCbSps indExcluido) {
		return getDispMdtoCbSpsByEtiquetaCount(etiqueta, indExcluido, null);
	}
	
	public Long getDispMdtoCbSpsByEtiquetaCount(String etiqueta,
			DominioIndExcluidoDispMdtoCbSps indExcluido, String loteCodigo) {
		DetachedCriteria criteria = getCriteriaDispMdtoCbSpsByEtiqueta(etiqueta, indExcluido);
		return executeCriteriaCount(criteria);
	}
}
