package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamObrigatoriedade
 * 
 * @author luismoura
 * 
 */
public class MamObrigatoriedadeDAO extends BaseDao<MamObrigatoriedade> {
	private static final long serialVersionUID = -6897133681803603205L;

	/**
	 * Lista MamObrigatoriedade filtrando por MamDescritor
	 * 
	 * C1, C4, C6 e C8 de #32658
	 * 
	 * @param mamDescritor
	 * @return
	 */
	public List<MamObrigatoriedade> pesquisarPorMamDescritor(MamDescritor mamDescritor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamObrigatoriedade.class);
		criteria.add(Restrictions.eq(MamObrigatoriedade.Fields.MAM_DESCRITOR.toString(), mamDescritor));
		return super.executeCriteria(criteria);
	}
	
	/**
	 * Verifica se existe determinado item de sinal vital (ICE_SEQ) para o descritor.
	 * 
	 * @param iceSeq
	 * @param mamDescritor
	 * @return
	 */
	public boolean existsItemSinalVitalPorDescritor(Short iceSeq, MamDescritor mamDescritor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamObrigatoriedade.class);
		criteria.add(Restrictions.eq(MamObrigatoriedade.Fields.MAM_DESCRITOR.toString(), mamDescritor));
		criteria.add(Restrictions.eq(MamObrigatoriedade.Fields.ICE_SEQ.toString(), iceSeq));
		return super.executeCriteriaExists(criteria);
	}

	/**
	 * Verifica se existe determinado item para o descritor
	 * 
	 * @param itemField
	 * @param itemSeq
	 * @param mamDescritor
	 * @return
	 */
	private boolean existsItemPorDescritor(String itemField, Integer itemSeq, MamDescritor mamDescritor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamObrigatoriedade.class);
		criteria.add(Restrictions.eq(MamObrigatoriedade.Fields.MAM_DESCRITOR.toString(), mamDescritor));
		criteria.createAlias(itemField, "ITEM");
		criteria.add(Restrictions.eq("ITEM.seq", itemSeq));
		return super.executeCriteriaExists(criteria);
	}

	/**
	 * Verifica se existe determinado item de medicacao (MDM_SEQ) para o descritor.
	 * 
	 * @param mdmSeq
	 * @param mamDescritor
	 * @return
	 */
	public boolean existsItemMedicacaoPorDescritor(Integer mdmSeq, MamDescritor mamDescritor) {
		return this.existsItemPorDescritor(MamObrigatoriedade.Fields.MAM_ITEM_MEDICACAO.toString(), mdmSeq, mamDescritor);
	}

	/**
	 * Verifica se existe determinado item de exame (EMS_SEQ) para o descritor.
	 * 
	 * @param emsSeq
	 * @param mamDescritor
	 * @return
	 */
	public boolean existsItemExamePorDescritor(Integer emsSeq, MamDescritor mamDescritor) {
		return this.existsItemPorDescritor(MamObrigatoriedade.Fields.MAM_ITEM_EXAME.toString(), emsSeq, mamDescritor);
	}

	/**
	 * Verifica se existe determinado item geral (ITG_SEQ) para o descritor.
	 * 
	 * @param itgSeq
	 * @param mamDescritor
	 * @return
	 */
	public boolean existsItemGeralPorDescritor(Integer itgSeq, MamDescritor mamDescritor) {
		return this.existsItemPorDescritor(MamObrigatoriedade.Fields.MAM_ITEM_GERAL.toString(), itgSeq, mamDescritor);
	}
	
	public List<MamObrigatoriedade> pesquisarItensObrigatoriosPorTriagem(Long trgSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamObrigatoriedade.class, "OBG");
		criteria.createAlias("OBG." + MamObrigatoriedade.Fields.MAM_DESCRITOR.toString(), "DCT");
		criteria.createAlias("DCT." + MamDescritor.Fields.MAM_TRG_GRAVIDADE.toString(), "TGV");
		
		criteria.add(Restrictions.eq("TGV." + MamTrgGravidade.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq("OBG." + MamObrigatoriedade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamTrgGravidade.class, "ULT");
		subCriteria.setProjection(Projections.max("ULT." + MamTrgGravidade.Fields.SEQP.toString()));
		subCriteria.add(Restrictions.eq("ULT." + MamTrgGravidade.Fields.TRG_SEQ.toString(), trgSeq));
		
		criteria.add(Subqueries.propertyIn("TGV." + MamTrgGravidade.Fields.SEQP.toString(), subCriteria));
		
		return executeCriteria(criteria);
	}

	/**
	 * Consulta utilizada pra obter os exames, medicações e itens gerais obrigatórios para o descritor informado
	 * 
	 * C5, C6 e C7 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param dctSeq
	 * @return
	 */
	public List<MamObrigatoriedade> pesquisarObrigatoriedadesAtivasPorDescritor(Integer dctSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamObrigatoriedade.class);
		criteria.add(Restrictions.eq(MamObrigatoriedade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.createAlias(MamObrigatoriedade.Fields.MAM_DESCRITOR.toString(), "MAM_DESCRITOR");
		criteria.add(Restrictions.eq("MAM_DESCRITOR." + MamDescritor.Fields.SEQ.toString(), dctSeq));
		return executeCriteria(criteria);
	}
}