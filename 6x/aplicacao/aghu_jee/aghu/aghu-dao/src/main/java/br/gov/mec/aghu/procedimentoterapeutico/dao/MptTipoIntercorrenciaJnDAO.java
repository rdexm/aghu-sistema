package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptTipoIntercorrenciaJn;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptTipoIntercorrenciaJnVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptTipoIntercorrenciaJnDAO extends BaseDao<MptTipoIntercorrenciaJn> {


	private static final String MTIJ_PONTO = "MTIJ.";
	/**
	 * 
	 */
	private static final long serialVersionUID = -6268422511120203410L;

	/**
	 * #46469 C2 - Consulta carrega registros da journal de tipo de intercorrência.
	 * 
	 * @param seq
	 * @return
	 */
	public List<MptTipoIntercorrenciaJnVO> carregarRegistrosTiposIntercorrencia(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoIntercorrenciaJn.class, "MTIJ");
		criteria.add(Restrictions.eq(MptTipoIntercorrenciaJn.Fields.SEQ.toString(), seq));
		criteria.addOrder(Order.desc(MptTipoIntercorrenciaJn.Fields.CRIADO_EM.toString()));
	    criteria.setProjection(Projections.projectionList()
                 .add(Projections.property(MTIJ_PONTO+MptTipoIntercorrenciaJn.Fields.CRIADO_EM), MptTipoIntercorrenciaJnVO.Fields.CRIADO_EM.toString())
                 .add(Projections.property(MTIJ_PONTO+MptTipoIntercorrenciaJn.Fields.IND_SITUACAO), MptTipoIntercorrenciaJnVO.Fields.IND_SITUACAO.toString())
                 .add(Projections.property(MTIJ_PONTO+MptTipoIntercorrenciaJn.Fields.USUARIO), MptTipoIntercorrenciaJnVO.Fields.USUARIO.toString())
                 .add(Projections.property(MTIJ_PONTO+MptTipoIntercorrenciaJn.Fields.DESCRICAO), MptTipoIntercorrenciaJnVO.Fields.DESCRICAO.toString())
                 .add(Projections.property(MTIJ_PONTO+MptTipoIntercorrenciaJn.Fields.MATRICULA), MptTipoIntercorrenciaJnVO.Fields.MATRICULA.toString())
                 .add(Projections.property(MTIJ_PONTO+MptTipoIntercorrenciaJn.Fields.VIN_CODIGO), MptTipoIntercorrenciaJnVO.Fields.VIN_CODIGO.toString())
                 );
	     criteria.setResultTransformer(Transformers.aliasToBean(MptTipoIntercorrenciaJnVO.class));
		return executeCriteria(criteria);
	}
	
}