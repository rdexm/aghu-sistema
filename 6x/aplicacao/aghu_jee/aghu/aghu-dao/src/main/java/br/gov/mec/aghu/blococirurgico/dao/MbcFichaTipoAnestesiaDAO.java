package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.blococirurgico.vo.MbcFichaTipoAnestesiaVO;
import br.gov.mec.aghu.model.MbcFichaTipoAnestesia;

public class MbcFichaTipoAnestesiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaTipoAnestesia> {

	private static final long serialVersionUID = -5446552595354877048L;

	public List<MbcFichaTipoAnestesia> pesquisarMbcFichasTipoAnestesias(Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaTipoAnestesia.class);
		criteria.add(Restrictions.eq(MbcFichaTipoAnestesia.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}	

	@SuppressWarnings("unchecked")
	public List<MbcFichaTipoAnestesiaVO> buscarProcedimentoAgendado(Integer atdSeq, 
			Integer pacCodigo, 
			Short seqpGestacao, 
			Integer procedimentoSeq, 
			Integer analgesiaSeq){
		
	StringBuffer query = new StringBuffer()	
		.append(" SELECT ")
		.append(" FIC.SEQ, ") 
		.append(" FIC.PENDENTE, ") 
		.append(" FTN.TAN_SEQ ")
		.append(" FROM ")  	
		.append(" AGH.MBC_FICHA_TIPO_ANESTESIAS FTN ")
		
		.append(" LEFT JOIN AGH.MBC_FICHA_ANESTESIAS FIC ")
		.append(" ON FTN.FIC_SEQ = FIC.SEQ ")
		
		.append(" JOIN	AGH.MBC_FICHA_PROCEDIMENTOS FPO ")
		.append(" ON 	FPO.FIC_SEQ = FIC.SEQ ")
		
		.append(" WHERE FIC.ATD_SEQ = "+atdSeq+" ")
		.append(" AND     FIC.DTHR_MVTO IS NULL ")
		.append(" AND     FIC.PENDENTE IN ('R','P','V') ")
		.append(" AND     FIC.ORIGEM = 'G' ")
		.append(" AND     FIC.PAC_CODIGO = "+pacCodigo+" ")
		.append(" AND      FIC.GSO_PAC_CODIGO = "+pacCodigo+" ")
		.append(" AND      FIC.GSO_SEQP   =  "+seqpGestacao+" ")
		.append(" AND      FIC.CRG_SEQ IS NULL ")
		.append(" AND ")     
		.append(" (case  FPO.PCI_SEQ ")
		.append(" when  "+procedimentoSeq+" THEN 'S' ")
		.append(" WHEN  "+analgesiaSeq+" THEN 'S' ")
		.append(" ELSE 'N' ")
		.append(" END) = 'S' ");
	
		SQLQuery q = createSQLQuery(query.toString());
		
		List<MbcFichaTipoAnestesiaVO> listaVO = q.
				addScalar("ficSeq",LongType.INSTANCE).
				addScalar("pendente",StringType.INSTANCE).
				addScalar("tanSeq",ShortType.INSTANCE).
				setResultTransformer(Transformers.aliasToBean(MbcFichaTipoAnestesiaVO.class)).list();
		
		return listaVO;
		
	}

}
