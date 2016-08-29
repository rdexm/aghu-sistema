package br.gov.mec.aghu.estoque.dao;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceItemBoc;
import br.gov.mec.aghu.model.SceNotaRecebimento;

public class SceItemBocDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemBoc> {
	
	private static final long serialVersionUID = -9010639160175295923L;


	/**
	 * Pesquisa os itens de um boletim de ocorrencia pelo numeroNr
	 * @param numeroNr
	 * @return
	 */
	public List<SceItemBoc> pesquisarPendenciasDevolucao(Integer numeroNr) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemBoc.class, "IBO");
		criteria.createAlias("IBO."+SceItemBoc.Fields.BOLETIM_OCORRENCIA.toString(), "BOC", Criteria.INNER_JOIN);
		criteria.createAlias("BOC."+SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO.toString(), "NRS", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("BOC."+SceBoletimOcorrencias.Fields.SITUACAO.toString(), DominioBoletimOcorrencias.G));
		criteria.add(Restrictions.eq("NRS."+SceNotaRecebimento.Fields.NUMERO_NR.toString(), numeroNr));
		
		return executeCriteria(criteria);
	}
	
	public List<SceItemBoc> pesquisarItensBOCPorNotaRecebimentoMaterial(Integer numeroNr, Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemBoc.class, "IBO");
		criteria.createAlias("IBO."+SceItemBoc.Fields.BOLETIM_OCORRENCIA.toString(), "BOC", Criteria.INNER_JOIN);		
		criteria.createAlias("BOC."+SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO.toString(), "NRS", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("BOC."+SceBoletimOcorrencias.Fields.SITUACAO.toString(), DominioBoletimOcorrencias.G));
		criteria.add(Restrictions.eq("NRS."+SceNotaRecebimento.Fields.NUMERO_NR.toString(), numeroNr));
		criteria.add(Restrictions.eq("IBO."+SceItemBoc.Fields.MAT_CODIGO.toString(), matCodigo));		
		
		return executeCriteria(criteria);
	}
	
	public Long contarNrEmBoletimOcorrencia(Integer numeroNr, Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemBoc.class, "IBO");
		criteria.createAlias("IBO."+SceItemBoc.Fields.BOLETIM_OCORRENCIA.toString(), "BOC", Criteria.INNER_JOIN);
		criteria.createAlias("BOC."+SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO.toString(), "NRS", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("NRS."+SceNotaRecebimento.Fields.NUMERO_NR.toString(), numeroNr));
		criteria.add(Restrictions.eq("IBO."+SceItemBoc.Fields.MAT_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.and(Restrictions.ne("BOC."+SceBoletimOcorrencias.Fields.SITUACAO.toString(), DominioBoletimOcorrencias.S), 
				Restrictions.ne("BOC."+SceBoletimOcorrencias.Fields.SITUACAO.toString(), DominioBoletimOcorrencias.H)));
		
		return executeCriteriaCount(criteria);
	}

	public List<SceItemBoc> pesquisarItemBoletimOcorrenciaPorBocSeq(Integer bocSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemBoc.class, "IBO");
		
		criteria.add(Restrictions.eq("IBO."+SceItemBoc.Fields.BOC_SEQ.toString(), bocSeq));
		
		return executeCriteria(criteria);
	}

	public Short obterProximoNroItem(Integer bocSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemBoc.class, "IBO");
		criteria.add(Restrictions.eq("IBO."+SceItemBoc.Fields.BOC_SEQ.toString(), bocSeq));
		criteria.setProjection(Projections.max(SceItemBoc.Fields.NRO_ITEM.toString()));
		
		Short max = (Short) executeCriteriaUniqueResult(criteria);
		
		if (max == null) {
			max = 1;
		} else {
			max++;
		}
		
		return max;
	}
}
