package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamDescritor
 * 
 * @author luismoura
 * 
 */
public class MamDescritorDAO extends BaseDao<MamDescritor> {

	private static final long serialVersionUID = 272883005161812631L;
	
	private static final String GRAVIDADE = "GRAVIDADE.";

	/**
	 * Monta a criteria de MamDescritor por mamFluxograma
	 * 
	 * @param mamFluxograma
	 * @return
	 */
	private DetachedCriteria montarCriteriaDescritorPorFluxograma(MamFluxograma mamFluxograma) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamDescritor.class, "DESCRITOR");
		criteria.add(Restrictions.eq(MamDescritor.Fields.FLUXOGRAMA.toString(), mamFluxograma));
		return criteria;
	}

	/**
	 * Pesquisa MamDescritor por mamFluxograma
	 * 
	 * C2 de #32286 - MANTER CADASTRO DE PERGUNTAS UTILIZADAS NOS PROTOCOLOS DE CLASSIFICAÇÃO DE RISCO
	 * 
	 * @param mamFluxograma
	 * @return
	 */
	public List<MamDescritor> pesquisarDescritorPorFluxograma(MamFluxograma mamFluxograma) {
		DetachedCriteria criteria = this.montarCriteriaDescritorPorFluxograma(mamFluxograma);
		criteria.addOrder(Order.asc(MamFluxograma.Fields.ORDEM.toString()));
		return super.executeCriteria(criteria);
	}

	/**
	 * Consulta utilizada para popular o grid de descritores
	 * 
	 * C2 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param mamFluxograma
	 * @return
	 */
	public List<MamDescritor> pesquisarDescritorAtivoPorFluxogramaGravidadeAtivaTriagem(MamFluxograma mamFluxograma) {
		DetachedCriteria criteria = this.montarCriteriaDescritorPorFluxograma(mamFluxograma);
		criteria.add(Restrictions.eq(MamDescritor.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		criteria.createAlias(MamDescritor.Fields.GRAVIDADE.toString(), "GRAVIDADE");
		criteria.add(Restrictions.eq(GRAVIDADE + MamGravidade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(GRAVIDADE + MamGravidade.Fields.IND_USO_TRIAGEM.toString(), Boolean.TRUE));
		
		criteria.addOrder(Order.asc(GRAVIDADE + MamGravidade.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc("DESCRITOR." + MamDescritor.Fields.ORDEM.toString()));
		return super.executeCriteria(criteria);
	}

	/**
	 * Consulta utilizada para obter o fluxograma para uma classificação
	 * 
	 * C15 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	public MamFluxograma obterFluxogramaDoDescritorPorGravidade(Short grvSeq, Integer dctSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamDescritor.class);
		criteria.add(Restrictions.eq(MamDescritor.Fields.SEQ.toString(), dctSeq));

		criteria.createAlias(MamDescritor.Fields.GRAVIDADE.toString(), "GRAVIDADE");
		criteria.add(Restrictions.eq(GRAVIDADE + MamGravidade.Fields.SEQ.toString(), grvSeq));

		List<MamDescritor> descritores = executeCriteria(criteria);
		if (descritores != null && !descritores.isEmpty()) {
			return descritores.get(0).getFluxograma();
		}
		return null;
	}
}