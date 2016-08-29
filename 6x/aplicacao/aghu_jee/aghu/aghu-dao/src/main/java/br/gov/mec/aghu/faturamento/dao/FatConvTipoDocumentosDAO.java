/**
 * 
 */
package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FatConvTipoDocumentos;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;

/**
 * @author marcelofilho
 *
 */
public class FatConvTipoDocumentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatConvTipoDocumentos> {
	

	private static final long serialVersionUID = -8033918834890180484L;

	
	public List<FatConvTipoDocumentos> pesquisarConvTipoDocumentoConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		return executeCriteria(criarCriteriaFatConvTipoDocumentos(convenioSaudePlano));
	}
	
	/**
	 * Retorna a criteria de recuperação de <code>FatConvTipoDocumentos</code>.
	 * 
	 * @param codigo
	 *            do
	 * @return o DetachedCriteria para ser usado em outros métodos
	 */
	private DetachedCriteria criarCriteriaFatConvTipoDocumentos(FatConvenioSaudePlano convenioSaudePlano) {
		if (convenioSaudePlano == null || convenioSaudePlano.getId() == null) {
			throw new IllegalArgumentException("Convênio Saúde Plano não informado.");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatConvTipoDocumentos.class);

		criteria.createAlias(FatConvTipoDocumentos.Fields.TIPO_DOCUMENTO.toString(), "TPD", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(
				FatConvTipoDocumentos.Fields.CONVENIO_SAUDE_PLANO.toString(),
				convenioSaudePlano));

		return criteria;
	}

	public List<FatConvTipoDocumentos> pesquisarObrigatoriosPorFatConvenioSaudePlano(Short novoCspCnvCodigo, Byte novoCspSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvTipoDocumentos.class);
		criteria.add(Restrictions.eq(FatConvTipoDocumentos.Fields.IND_OBRIGATORIO.toString(), Boolean.TRUE));
		DetachedCriteria criteriaConvenioSaude = criteria.createCriteria(FatConvTipoDocumentos.Fields.CONVENIO_SAUDE_PLANO.toString());
		criteriaConvenioSaude.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SEQ.toString(), novoCspSeq));
		criteriaConvenioSaude.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), novoCspCnvCodigo));
		return executeCriteria(criteria);
	}
}
