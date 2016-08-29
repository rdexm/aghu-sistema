package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceItemDasId;

public class SceDocumentoValidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceDocumentoValidade> {

	
	private static final long serialVersionUID = 7337303208600225687L;
	//Constantes criadas para buscar uma requisicao de material conforme dados da SceTipoMovimentos, nao foi criado um 
	//dominio devido a chave composta da tabela
	public static final Integer TMV_SEQ_RM = 5;
	public static final Integer TMV_COMPLEMENTO_RM = 2;
	
	/**
	 * 
	 * @param nroDoc
	 * @param tmvSeqDoc
	 * @param tmvComplDoc
	 * @param ealSeq
	 * @return
	 */
	public List<SceDocumentoValidade> pesquisarDocValidadeTransfAutoAlmoxarifado(Integer nroDoc, Short tmvSeqDoc, Byte tmvComplDoc, Integer ealSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoValidade.class,"DCV");
		criteria.createCriteria("DCV."+SceDocumentoValidade.Fields.VALIDADE.toString(), "VAL", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("DCV."+SceDocumentoValidade.Fields.NUMERO_DOCUMENTO.toString(),ealSeq));
		criteria.add(Restrictions.eq("DCV."+SceDocumentoValidade.Fields.TMV_SEQ.toString(),ealSeq));
		criteria.add(Restrictions.eq("DCV."+SceDocumentoValidade.Fields.TMV_COMPLEMENTO.toString(),ealSeq));
		criteria.add(Restrictions.eq("DCV."+SceDocumentoValidade.Fields.EAL_SEQ.toString(),ealSeq));
		
		return executeCriteria(criteria);
		
	}
	
	public List<SceDocumentoValidade> pesquisarDocumentoValidadePorItemDa(SceItemDasId itemDasId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoValidade.class);
		criteria.add(Restrictions.eq(SceDocumentoValidade.Fields.NUMERO_DOCUMENTO.toString(),itemDasId.getDalSeq()));
		criteria.add(Restrictions.eq(SceDocumentoValidade.Fields.EAL_SEQ.toString(),itemDasId.getEalSeq()));
		return executeCriteria(criteria);
	}
	
	public List<SceDocumentoValidade> pesquisarDocumentoValidadePorEalSeqENroDocRequisicaoMaterial(Integer ealSeq, Integer nroDoc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoValidade.class);
		criteria.add(Restrictions.eq(SceDocumentoValidade.Fields.EAL_SEQ.toString(), ealSeq));
		criteria.add(Restrictions.eq(SceDocumentoValidade.Fields.NUMERO_DOCUMENTO.toString(), nroDoc));
		criteria.add(Restrictions.eq(SceDocumentoValidade.Fields.TMV_SEQ.toString(), TMV_SEQ_RM));
		criteria.add(Restrictions.eq(SceDocumentoValidade.Fields.TMV_COMPLEMENTO.toString(), TMV_COMPLEMENTO_RM));
		return executeCriteria(criteria);
	}
	
	
}
