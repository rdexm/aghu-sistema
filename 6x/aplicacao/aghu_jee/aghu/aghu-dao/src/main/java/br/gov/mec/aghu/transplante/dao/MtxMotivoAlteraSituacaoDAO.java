package br.gov.mec.aghu.transplante.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMotivoAlteraSituacoes;
import br.gov.mec.aghu.model.MtxExtratoTransplantes;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;

public class MtxMotivoAlteraSituacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxMotivoAlteraSituacao> {

	private static final long serialVersionUID = 1308195037096060091L;
	
	private static final String MAS = "MAS.";

	/*46378*/
	public DetachedCriteria obterCriteriaListaMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxMotivoAlteraSituacao.class);
		if(mtxMotivoAlteraSituacao.getTipo() != null){
			criteria.add(Restrictions.eq(MtxMotivoAlteraSituacao.Fields.TIPO.toString(), mtxMotivoAlteraSituacao.getTipo() ));
		}
		if(StringUtils.isNotEmpty(mtxMotivoAlteraSituacao.getDescricao())){
			criteria.add(Restrictions.ilike(MtxMotivoAlteraSituacao.Fields.DESCRICAO.toString(), mtxMotivoAlteraSituacao.getDescricao().toString(), MatchMode.ANYWHERE));
		}
		if(mtxMotivoAlteraSituacao.getIndicadorSituacao() != null){
			criteria.add(Restrictions.eq(MtxMotivoAlteraSituacao.Fields.INDICADOR_SITUACAO.toString(), mtxMotivoAlteraSituacao.getIndicadorSituacao()));
		}
		criteria.addOrder(Order.asc(MtxMotivoAlteraSituacao.Fields.DESCRICAO.toString()));
		
		return criteria;
	}
	
	public List<MtxMotivoAlteraSituacao> obterListaMotivoAlteraSituacao(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao) {
		return executeCriteria(obterCriteriaListaMotivoAlteraSituacao(mtxMotivoAlteraSituacao), firstResult, maxResults, orderProperty, asc);
	}

	public Long obterListaMotivoAlteraSituacaoCount(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao) {
		return executeCriteriaCount(obterCriteriaListaMotivoAlteraSituacao(mtxMotivoAlteraSituacao));
	}
	
	public Boolean consultaExtratoTransplante(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class);
		criteria.add(Restrictions.eq(MtxExtratoTransplantes.Fields.MTX_MOTIVO_ALTERA_SITUACAO.toString() + "." + MtxMotivoAlteraSituacao.Fields.SEQ.toString(),
						mtxMotivoAlteraSituacao.getSeq()));
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * #41787 - C2
	 * @author romario.caldeira
	 */
	public List<MtxMotivoAlteraSituacao> listarMotivosAlteracaoSituacao(){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxMotivoAlteraSituacao.class, "MAS");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MAS+MtxMotivoAlteraSituacao.Fields.SEQ), MtxMotivoAlteraSituacao.Fields.SEQ.toString())
				.add(Projections.property(MAS+MtxMotivoAlteraSituacao.Fields.DESCRICAO), MtxMotivoAlteraSituacao.Fields.DESCRICAO.toString())
				);
		
		criteria.add(Restrictions.eq(MAS + MtxMotivoAlteraSituacao.Fields.INDICADOR_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(MAS + MtxMotivoAlteraSituacao.Fields.TIPO.toString(), Arrays.asList(DominioTipoMotivoAlteraSituacoes.M, DominioTipoMotivoAlteraSituacoes.T)));
		criteria.setResultTransformer(Transformers.aliasToBean(MtxMotivoAlteraSituacao.class));
		return executeCriteria(criteria);
	}
}
