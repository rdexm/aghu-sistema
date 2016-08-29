package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.vo.OrdenacaoPorGravidadesVO;
import br.gov.mec.aghu.model.MamAgrupGravidade;
import br.gov.mec.aghu.model.MamAgrupXGravidade;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTrgPrevAtend;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamTrgGravidadeDAO extends BaseDao<MamTrgGravidade> {

	private static final long serialVersionUID = -1697944899396513190L;

	public Short obterGrvSeqPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGravidade.class);
		
		criteria.setProjection(Projections.property(MamTrgGravidade.Fields.GRV_SEQ.toString()));
		
		criteria.add(Restrictions.eq(MamTrgGravidade.Fields.TRG_SEQ.toString(), trgSeq));
		
		criteria.addOrder(Order.desc(MamTrgGravidade.Fields.SEQP.toString()));
		List<Short> result = super.executeCriteria(criteria, 0, 1, null, false);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		
		return null;
	}
	
	public Boolean verificarExisteGravidadePorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGravidade.class);
		criteria.add(Restrictions.eq(MamTrgGravidade.Fields.TRG_SEQ.toString(), trgSeq));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}
	
	/**
	 * Verifica obrigatoriedade dos campos Data Queixa e Hora Queixa conforme parâmetro recebido.
	 * @param trgSeq
	 * @param isData Indica se deve verificar obrigatoriedade do campo Data ou Hora.
	 * @return
	 */
	public Boolean verificaObrigatoriedadeDataHoraQueixa(Long trgSeq, boolean isData) {
		
		final String TGV = "TGV.";
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGravidade.class, "TGV");
				
		criteria.createAlias(TGV + MamTrgGravidade.Fields.MAM_DESCRITORES.toString(), "DES");
		
		if (isData) {
			criteria.setProjection(Projections.property("DES." + MamDescritor.Fields.IND_DT_QUEIXA_OBGT.toString()));
		} else {
			criteria.setProjection(Projections.property("DES." + MamDescritor.Fields.IND_HR_QUEIXA_OBGT.toString()));
		}
		criteria.add(Restrictions.eq(TGV + MamTrgGravidade.Fields.TRG_SEQ.toString(), trgSeq));
		
		criteria.addOrder(Order.desc(TGV + MamTrgGravidade.Fields.SEQP.toString()));
		
		List<Boolean> result =  executeCriteria(criteria, 0, 1, null);
		
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return false;
	}

	/**
	 * Consulta que retorna um seqp para uma gravidade a ser inserida para um registro em acolhimento
	 * 
	 * C3 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public Short obterProximoSeqPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGravidade.class);
		criteria.add(Restrictions.eq(MamTrgGravidade.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.setProjection(Projections.max(MamTrgGravidade.Fields.SEQP.toString()));
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return ++maxSeqP;
		}
		return 1;
	}

	/**
	 * Consulta utilizada para verificar se já foi feita classificação de risco para o paciente corrente
	 * 
	 * C14 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public MamTrgGravidade obterUltimaGravidadePorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGravidade.class);
		
		criteria.createAlias(MamTrgGravidade.Fields.MAM_TRIAGENS.toString(), "TRIAGEM");
		criteria.add(Restrictions.eq("TRIAGEM." + MamTriagens.Fields.SEQ.toString(), trgSeq));
		criteria.addOrder(Order.desc(MamTrgGravidade.Fields.SEQP.toString()));

		List<MamTrgGravidade> result = super.executeCriteria(criteria, 0, 1, null, false);

		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	public OrdenacaoPorGravidadesVO obterOrdenacaoPorTrgSeq(Long trgSeq) {
		
		final String TGG = "TGG.";
		final String AGR = "AGR.";
		final String TEI = "TEI.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGravidade.class, "TGG");
		
		criteria.createAlias(TGG + MamTrgGravidade.Fields.MAM_TRIAGENS.toString(), "TRG");
		criteria.createAlias("TRG." + MamTriagens.Fields.MAM_TRG_PREV_ATEND.toString(), "TPV");
		criteria.createAlias(TGG + MamTrgGravidade.Fields.MAM_GRAVIDADES.toString(), "GRV");
		criteria.createAlias("GRV." + MamGravidade.Fields.AGRUP_X_GRAVIDADE.toString(), "AXG");
		criteria.createAlias("AXG." + MamAgrupXGravidade.Fields.AGRUPAMENTO_GRAVIDADE.toString(), "AGR");
		criteria.createAlias("TRG." + MamTriagens.Fields.MAM_TRG_ENC_INTERNO.toString(), "TEI");
		
		criteria.add(Restrictions.eq(TGG + MamTrgGravidade.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq("AXG." + MamAgrupXGravidade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AGR + MamAgrupGravidade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull(TEI + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamTrgGravidade.class, "TGGS");
		subCriteria.add(Restrictions.eqProperty("TGGS." + MamTrgGravidade.Fields.TRG_SEQ.toString(), TGG + MamTrgGravidade.Fields.TRG_SEQ.toString()));
		subCriteria.setProjection(Projections.max("TGGS."+ MamTrgGravidade.Fields.SEQP.toString()));
		criteria.add(Subqueries.propertyEq(TGG + MamTrgGravidade.Fields.SEQP.toString(), subCriteria));
		
		final DetachedCriteria subCriteria2 = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEIS");
		subCriteria2.add(Restrictions.eqProperty("TEIS." + MamTrgEncInterno.Fields.TRG_SEQ.toString(), TEI + MamTrgEncInterno.Fields.TRG_SEQ.toString()));
		subCriteria2.add(Restrictions.isNull("TEIS." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		subCriteria2.setProjection(Projections.max("TEIS."+ MamTrgEncInterno.Fields.SEQP.toString()));
		criteria.add(Subqueries.propertyEq(TEI + MamTrgEncInterno.Fields.SEQP.toString(), subCriteria2));
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property(AGR+ MamAgrupGravidade.Fields.SEQ.toString()), OrdenacaoPorGravidadesVO.Fields.AGR_SEQ.toString())
				.add(Projections.property(AGR+ MamAgrupGravidade.Fields.ORDEM.toString()), OrdenacaoPorGravidadesVO.Fields.AGR_ORDEM.toString())
				.add(Projections.property(TGG+ MamTrgGravidade.Fields.TRG_SEQ.toString()), OrdenacaoPorGravidadesVO.Fields.TRG_SEQ.toString())
				.add(Projections.property(TEI+ MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()), OrdenacaoPorGravidadesVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("TPV."+ MamTrgPrevAtend.Fields.DTHR_PREV_ATEND.toString()), OrdenacaoPorGravidadesVO.Fields.DTHR_PREV_ATEND.toString())
				.add(Projections.property(AGR+ MamAgrupGravidade.Fields.IND_PREV_ATEND.toString()), OrdenacaoPorGravidadesVO.Fields.IND_PREV_ATEND.toString());
		criteria.setProjection(projection);
		
		criteria.setResultTransformer(Transformers.aliasToBean(OrdenacaoPorGravidadesVO.class));
		
		return (OrdenacaoPorGravidadesVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Monta a criteria de MamTrgGravidade por mamProtClassifRisco
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	private DetachedCriteria montarCriteriaMamTrgGravidadePorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGravidade.class, "TGV");
		criteria.createAlias("TGV." + MamTrgGravidade.Fields.MAM_GRAVIDADES.toString(), "GRV");
		criteria.add(Restrictions.eq("GRV." + MamGravidade.Fields.PCR_SEQ.toString(), mamProtClassifRisco.getSeq()));
		return criteria;
	}
	
	/**
	 * Pesquisa se existe MamTrgGravidade por mamProtClassifRisco
	 * 
	 * C6 de #32283
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	public Boolean existsMamTrgGravidadePorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		DetachedCriteria criteria = this.montarCriteriaMamTrgGravidadePorProtocolo(mamProtClassifRisco);
		return super.executeCriteriaExists(criteria);
	}
}