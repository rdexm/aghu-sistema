package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioRepeticaoRetorno;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxPeriodoRetorno;
import br.gov.mec.aghu.model.MtxTipoRetorno;

public class MtxItemPeriodoRetornoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxItemPeriodoRetorno> {

	private static final long serialVersionUID = -5131280037164821709L;
	
    public List<MtxItemPeriodoRetorno> pesquisarItemPeriodoRetorno(DominioTipoRetorno tipoRetorno, Integer seqPeriodoRetorno, String pesquisaSuggestion){
    	DetachedCriteria criteria = DetachedCriteria.forClass(MtxItemPeriodoRetorno.class, "IPR");
    	
    	criteria.createAlias("IPR." + MtxItemPeriodoRetorno.Fields.PERIODO_RETORNO.toString(), "PRE");
    	criteria.createAlias("PRE." + MtxPeriodoRetorno.Fields.TIPO_RETORNO.toString(), "TRE");
    	
    	if (tipoRetorno != null){
    		criteria.add(Restrictions.eq("TRE." + MtxTipoRetorno.Fields.IND_TIPO.toString(), tipoRetorno));
    	}
    	
    	if (seqPeriodoRetorno != null){
    		criteria.add(Restrictions.eq("PRE."+ MtxPeriodoRetorno.Fields.SEQ.toString(), seqPeriodoRetorno));
    	}

    	if (!StringUtils.isEmpty(pesquisaSuggestion)){
    		Junction disjunction = Restrictions.disjunction();
    		
    		if (StringUtils.isNumeric(pesquisaSuggestion)) {
    			disjunction.add(Restrictions.eq("PRE."+ MtxPeriodoRetorno.Fields.SEQ.toString(), Integer.valueOf(pesquisaSuggestion)));
    			disjunction.add(Restrictions.eq("IPR."+ MtxItemPeriodoRetorno.Fields.ORDEM.toString(), Integer.valueOf(pesquisaSuggestion)));
    			disjunction.add(Restrictions.eq("IPR."+ MtxItemPeriodoRetorno.Fields.QUANTIDADE.toString(), Integer.valueOf(pesquisaSuggestion)));
    		}
    		
    		Object[] arrayRepeticao = DominioRepeticaoRetorno.obterListDominioPorDescricao(pesquisaSuggestion);
    		if (arrayRepeticao != null && arrayRepeticao.length >0){
    			disjunction.add(Restrictions.in("IPR." + MtxItemPeriodoRetorno.Fields.IND_REPETICAO.toString(), arrayRepeticao));
    		}
			disjunction.add(Restrictions.ilike("TRE."+ MtxTipoRetorno.Fields.DESCRICAO.toString(), pesquisaSuggestion, MatchMode.ANYWHERE));

    		criteria.add(disjunction);
    	}
    	
    	criteria.add(Restrictions.eq("PRE."+ MtxPeriodoRetorno.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
    	criteria.add(Restrictions.eq("TRE."+ MtxTipoRetorno.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
    	
    	criteria.addOrder(Order.desc("TRE."+ MtxTipoRetorno.Fields.IND_TIPO.toString()))
				.addOrder(Order.asc("TRE."+ MtxTipoRetorno.Fields.DESCRICAO.toString()))
				.addOrder(Order.asc("PRE."+ MtxPeriodoRetorno.Fields.SEQ.toString()))
				.addOrder(Order.asc("IPR."+ MtxItemPeriodoRetorno.Fields.ORDEM.toString()));

        return executeCriteria(criteria);
  }
}
