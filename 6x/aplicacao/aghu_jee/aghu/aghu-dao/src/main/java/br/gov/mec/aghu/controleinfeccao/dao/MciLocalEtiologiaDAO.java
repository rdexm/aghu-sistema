package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.LocaisOrigemInfeccaoVO;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.MciLocalEtiologia;

public class MciLocalEtiologiaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MciLocalEtiologia> {

	private static final long serialVersionUID = -8188294628492811615L;

	
	public List<LocaisOrigemInfeccaoVO> listarLocaisOrigemInfeccoes(String codigoOrigem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciLocalEtiologia.class, "LET");
		criteria.createAlias("LET." + MciLocalEtiologia.Fields.MCI_ETIOLOGIA_INFECCAO.toString(), "EIN");
		
		criteria.setProjection(Projections.projectionList().add(
				Projections.property("EIN." + MciEtiologiaInfeccao.Fields.CODIGO.toString())
					, LocaisOrigemInfeccaoVO.Fields.CODIGO_ORIGEM.toString())
				.add(Projections.property("LET." + MciLocalEtiologia.Fields.UNF_SEQ.toString())
						, LocaisOrigemInfeccaoVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("LET." + MciLocalEtiologia.Fields.IND_FORMA_CONTABILIZACAO.toString())
						, LocaisOrigemInfeccaoVO.Fields.FORMA_CONTABILIZACAO.toString())
				.add(Projections.property("LET." + MciLocalEtiologia.Fields.IND_SITUACAO.toString())
						, LocaisOrigemInfeccaoVO.Fields.SITUACAO.toString()));
		
		criteria.add(Restrictions.eq("LET." + MciLocalEtiologia.Fields.EIN_TIPO.toString(), codigoOrigem));
		
		criteria.setResultTransformer(Transformers.aliasToBean(LocaisOrigemInfeccaoVO.class));
		
		return executeCriteria(criteria);
	}
}