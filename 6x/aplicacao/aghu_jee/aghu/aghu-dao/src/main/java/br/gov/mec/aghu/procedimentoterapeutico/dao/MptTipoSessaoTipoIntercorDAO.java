package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTipoSessaoTipoIntercor;
import br.gov.mec.aghu.procedimentoterapeutico.vo.VincularIntercorrenciaTipoSessaoVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptTipoSessaoTipoIntercorDAO extends
		BaseDao<MptTipoSessaoTipoIntercor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5253677519885318967L;
	private static final String TSTI_PONTO = "TSTI.";
	
	/**#47027 C2 - Carrega vínculos entre Tipo de Intercorrência e Tipo de Sessão**/
	public List<VincularIntercorrenciaTipoSessaoVO> carregarVinculosTipoIntercorreciaTipoSessao(MptTipoSessao tipoSessao, String descricao, Integer firstResult, Integer maxResults){
		DetachedCriteria criteria = criteriaCarregaTipoIntercorreciaTipoSessao(tipoSessao, descricao);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ITC."+MptTipoIntercorrencia.Fields.DESCRICAO.toString()),
						VincularIntercorrenciaTipoSessaoVO.Fields.DESC_TIPO_INTERCOR.toString())
				.add(Projections.property("TPS."+MptTipoSessao.Fields.DESCRICAO.toString()),
						VincularIntercorrenciaTipoSessaoVO.Fields.DESC_TIPO_SESSAO.toString())
				.add(Projections.property(TSTI_PONTO+MptTipoSessaoTipoIntercor.Fields.SEQ.toString()),
						VincularIntercorrenciaTipoSessaoVO.Fields.SEQ_TIPO_INTERCOR.toString()));
		
		criteria.addOrder(Order.asc("TPS."+MptTipoSessao.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(VincularIntercorrenciaTipoSessaoVO.class));
		
		return executeCriteria(criteria, firstResult, maxResults, null, true);
	}
	
	public Long carregarVinculosTipoIntercorreciaTipoSessaoCount(MptTipoSessao tipoSessao, String descricao){
		DetachedCriteria criteria = criteriaCarregaTipoIntercorreciaTipoSessao(tipoSessao, descricao);
		return executeCriteriaCount(criteria);
	}
	
	//#47027 C2
	private DetachedCriteria criteriaCarregaTipoIntercorreciaTipoSessao(MptTipoSessao tipoSessao, String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessaoTipoIntercor.class, "TSTI");
		criteria.createAlias(TSTI_PONTO+MptTipoSessaoTipoIntercor.Fields.TP_INTERCOR.toString(), "ITC");
		criteria.createAlias(TSTI_PONTO+MptTipoSessaoTipoIntercor.Fields.TP_SESSAO.toString(), "TPS");
		
		if(tipoSessao != null && tipoSessao.getSeq() != null){
			criteria.add(Restrictions.eq(TSTI_PONTO+MptTipoSessaoTipoIntercor.Fields.TPS_SEQ.toString(), tipoSessao.getSeq()));
		}
		
		if(descricao != null && StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike("ITC."+MptTipoIntercorrencia.Fields.DESCRICAO.toString(), descricao.trim(), MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/**#47027 C4 - Verifica a existência de vinculo do Tipo de Intercorrencia por Tipo de Sessão. 
	 * Caso não exista vínculo, retornará valor ZERO**/
	public Long verificarExistenciaVinculoIntercorrenciaTipoSessao(Short seqTipoIntercorrencia, Short seqTipoSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessaoTipoIntercor.class);
		criteria.add(Restrictions.eq(MptTipoSessaoTipoIntercor.Fields.TPI_SEQ.toString(), seqTipoIntercorrencia));
		criteria.add(Restrictions.eq(MptTipoSessaoTipoIntercor.Fields.TPS_SEQ.toString(), seqTipoSessao));
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #46469 C3 - Consulta faz a verificação de vínculo com MPT_TIPO_SESSAO.
	 * 
	 * @param seqIntercor
	 * @return
	 */
	public boolean verificarVinculoPorTipoIntercorrente(Short seqIntercor){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessaoTipoIntercor.class);
		criteria.add(Restrictions.eq(MptTipoSessaoTipoIntercor.Fields.TPI_SEQ.toString(), seqIntercor));
		return executeCriteriaExists(criteria);
	}

}
