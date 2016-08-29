package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaPrincFarmaco;
import br.gov.mec.aghu.model.MpmAltaPrincFarmacoId;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.vo.AfaMedicamentoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author bsoliveira - 29/10/2010
 * 
 */
public class MpmAltaPrincFarmacoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaPrincFarmaco> {

    private static final long serialVersionUID = -6648754749733610676L;


	@Override
    protected void obterValorSequencialId(MpmAltaPrincFarmaco elemento) {

	if (elemento == null) {
	    throw new IllegalArgumentException("Parametro invalido!!!");
	}

	DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmAltaPrincFarmaco.class);
	criteria.add(Restrictions.eq(
		MpmAltaPrincFarmaco.Fields.ASU_APA_ATD_SEQ.toString(), elemento
			.getAltaSumario().getId().getApaAtdSeq()));
	criteria.add(Restrictions.eq(
		MpmAltaPrincFarmaco.Fields.ASU_APA_SEQ.toString(), elemento
			.getAltaSumario().getId().getApaSeq()));
	criteria.add(Restrictions.eq(
		MpmAltaPrincFarmaco.Fields.ASU_SEQP.toString(), elemento
			.getAltaSumario().getId().getSeqp()));
	criteria.setProjection(Projections.max(MpmAltaPrincFarmaco.Fields.SEQP
		.toString()));

	Integer seqp = (Integer) this.executeCriteriaUniqueResult(criteria);
	seqp = seqp == null ? 0 : seqp;

	MpmAltaSumarioId altaSumarioId = elemento.getAltaSumario().getId();
	MpmAltaPrincFarmacoId altaPrincFarmacoId = new MpmAltaPrincFarmacoId();
	altaPrincFarmacoId.setAsuApaAtdSeq(altaSumarioId.getApaAtdSeq());
	altaPrincFarmacoId.setAsuApaSeq(altaSumarioId.getApaSeq());
	altaPrincFarmacoId.setAsuSeqp(altaSumarioId.getSeqp());

	altaPrincFarmacoId.setSeqp(++seqp);

	elemento.setId(altaPrincFarmacoId);

    }

    /**
     * 
     * Busca lista do objeto MpmAltaPrincFarmaco pela FK de MpmAltaSumario.
     * 
     * @param {MpmAltaSumarioId} altaSumarioId
     * @return List<MpmAltaPrincFarmaco>
     * 
     */
    public List<MpmAltaPrincFarmaco> obterListaAltaPrincFarmacoPeloAltaSumarioId(MpmAltaSumarioId altaSumarioId) {
		if (altaSumarioId == null) {
		    throw new IllegalArgumentException("Parametro invalido!!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincFarmaco.class);
		
		criteria.add(Restrictions.eq(MpmAltaPrincFarmaco.Fields.ASU_APA_ATD_SEQ.toString(), altaSumarioId.getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaPrincFarmaco.Fields.ASU_APA_SEQ.toString(), altaSumarioId.getApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaPrincFarmaco.Fields.ASU_SEQP.toString(), altaSumarioId.getSeqp()));
		
		criteria.add(Restrictions.eq(MpmAltaPrincFarmaco.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		List<MpmAltaPrincFarmaco> retorno = this.executeCriteria(criteria);
		
		return retorno;
    }

    /**
     * Busca lista do objeto MpmAltaPrincFarmaco do sumário ativo
     * 
     * @param altanAtdSeq
     * @param altanApaSeq
     * @param altanAsuSeqp
     * @return
     */
	public List<MpmAltaPrincFarmaco> obterMpmAltaPrincFarmaco(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return obterMpmAltaPrincFarmaco(altanAtdSeq, altanApaSeq, altanAsuSeqp, true);
	}
    
    /**
     * Busca lista do objeto MpmAltaPrincFarmaco do sumário.<br>
     * 
     * @param altanAtdSeq
     * @param altanApaSeq
     * @param altanAsuSeqp
     * @param apenasAtivos
     * @return
     */
	public List<MpmAltaPrincFarmaco> obterMpmAltaPrincFarmaco(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincFarmaco.class);
		
		criteria.add(Restrictions.eq(MpmAltaPrincFarmaco.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincFarmaco.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincFarmaco.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(MpmAltaPrincFarmaco.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		
		return this.executeCriteria(criteria);
	}
    
    public List<AfaMedicamentoPrescricaoVO> obterListaAltaPrincFarmacoPorIndCarga(
        	Integer apaAtdSeq, Integer apaSeq, Short seqp, Boolean indCarga) {

    	DetachedCriteria criteria = DetachedCriteria
    		.forClass(MpmAltaPrincFarmaco.class);
    	criteria.createAlias(MpmAltaPrincFarmaco.Fields.MEDICAMENTO.toString(),
    		"MAT");
    	criteria.add(Restrictions.eq(
    		MpmAltaPrincFarmaco.Fields.ASU_APA_ATD_SEQ.toString(),
    		apaAtdSeq));
    	criteria.add(Restrictions.eq(
    			MpmAltaPrincFarmaco.Fields.ASU_APA_SEQ.toString(),
    			apaSeq));
    	criteria.add(Restrictions.eq(
    			MpmAltaPrincFarmaco.Fields.ASU_SEQP.toString(),
    			seqp));
    	criteria.add(Restrictions.eq(
    			MpmAltaPrincFarmaco.Fields.IND_CARGA.toString(),
    			indCarga));
    	criteria.add(Restrictions.eq(
    		MpmAltaPrincFarmaco.Fields.IND_SITUACAO.toString(),
    		DominioSituacao.A));

    	ProjectionList pList = Projections.projectionList();
    	pList.add(Property.forName(MpmAltaPrincFarmaco.Fields.ASU_APA_ATD_SEQ
    		.toString()), "asuApaAtdSeq");
    	pList.add(Property.forName(MpmAltaPrincFarmaco.Fields.ASU_APA_SEQ
    		.toString()), "asuApaSeq");
    	pList.add(Property.forName(MpmAltaPrincFarmaco.Fields.ASU_SEQP
    		.toString()), "asuSeqp");
    	pList.add(Property.forName(MpmAltaPrincFarmaco.Fields.SEQP.toString()),
    		"seqp");
    	pList.add(Property.forName(MpmAltaPrincFarmaco.Fields.MED_MAT_CODIGO
    		.toString()), "medMatCodigo");
    	pList.add(Property
    		.forName(MpmAltaPrincFarmaco.Fields.DESCRICAO_MEDICAMENTO
    			.toString()), "descricao");

    	criteria.setProjection(pList);

    	criteria.setResultTransformer(Transformers
    		.aliasToBean(AfaMedicamentoPrescricaoVO.class));

    	return this.executeCriteria(criteria);
        }
 
    public MpmAltaPrincFarmaco obterMpmAltaPrincFarmacoOriginalDesatachado(
	    MpmAltaPrincFarmaco altaPrincFarmaco) {
	// Obs.: Ao utilizar evict o objeto prescricaoProcedimento torna-se
	// desatachado.
	this.desatachar(altaPrincFarmaco);
	return this.obterPorChavePrimaria(altaPrincFarmaco.getId());
    }
  
    
	public List<LinhaReportVO> obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		String hql="select new br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO(" +
			MpmAltaPrincFarmaco.Fields.DESCRICAO_MEDICAMENTO.toString() +
		   ") from MpmAltaPrincFarmaco " + 
		   " where "+ MpmAltaPrincFarmaco.Fields.ASU_APA_ATD_SEQ.toString() + "=:asuApaAtdSeq " +
		   " and " + MpmAltaPrincFarmaco.Fields.ASU_APA_SEQ.toString() + "=:asuApaSeq " +
		   " and " + MpmAltaPrincFarmaco.Fields.ASU_SEQP.toString() + "=:asuSeqp " +
		   " and " + MpmAltaPrincFarmaco.Fields.IND_SITUACAO.toString() + "=:dominioSituacao";
		Query query = createHibernateQuery(hql);
		query.setParameter("asuApaAtdSeq", asuApaAtdSeq);
		query.setParameter("asuApaSeq", asuApaSeq);
		query.setParameter("asuSeqp", asuSeqp);
		query.setParameter("dominioSituacao", DominioSituacao.A);
		
		return query.list();		
		
		
	}    

}
