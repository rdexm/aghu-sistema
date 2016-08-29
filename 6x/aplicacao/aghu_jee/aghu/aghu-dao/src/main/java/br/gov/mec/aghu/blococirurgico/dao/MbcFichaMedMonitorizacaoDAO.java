package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaMedMonitorizacao;
import br.gov.mec.aghu.model.MbcFichaMonitorizacao;
import br.gov.mec.aghu.model.MbcItemMonitorizacao;
import br.gov.mec.aghu.model.MbcMonitorizacao;
import br.gov.mec.aghu.model.MbcTipoItemMonitorizacao;

public class MbcFichaMedMonitorizacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaMedMonitorizacao> {
	
	private static final long serialVersionUID = -3098069525896688657L;

	public List<Object[]> pesquisarTipoItemMonitorizacaoComMedicao(Long seqMbcFichaAnestesia) {	
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaMedMonitorizacao.class, "MED");
		
		criteria.createAlias("MED." + MbcFichaMedMonitorizacao.Fields.MBC_FICHA_MONITORIZACOES.toString(), "FMT");
		criteria.createAlias("FMT." + MbcFichaMonitorizacao.Fields.MBC_ITEM_MONITORIZACOES.toString(), "IMZ");
		criteria.createAlias("IMZ." + MbcItemMonitorizacao.Fields.MBC_TIPO_ITEM_MONITORIZACOES.toString(), "TMZ");
		criteria.createAlias("IMZ." + MbcItemMonitorizacao.Fields.MBC_MONITORIZACOES.toString(), "MOZ");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("TMZ." + MbcTipoItemMonitorizacao.Fields.NOME_REDUZIDO.toString()))
				.add(Projections.groupProperty("IMZ." + MbcItemMonitorizacao.Fields.SEQ.toString()))
				.add(Projections.max("FMT." + MbcFichaMonitorizacao.Fields.MBC_FICHA_ANESTESIAS.toString() + "." + MbcFichaAnestesias.Fields.SEQ.toString())));
		
		criteria.add(Restrictions.eq("FMT." + MbcFichaMonitorizacao.Fields.MBC_FICHA_ANESTESIAS.toString() + "." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		criteria.add(Restrictions.isNotNull("MED." + MbcFichaMedMonitorizacao.Fields.MEDICAO.toString()));
		
		criteria.addOrder(Order.asc("TMZ." + MbcTipoItemMonitorizacao.Fields.NOME_REDUZIDO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcFichaMedMonitorizacao> persquisarFichaMedMononitorizacaoComMedicao(Long seqMbcFichaAnestesia, Short seqMbcItemMonitorizacoes){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaMedMonitorizacao.class, "MED");
		
		criteria.createAlias("MED." + MbcFichaMedMonitorizacao.Fields.MBC_FICHA_MONITORIZACOES.toString(), "FMT");
		criteria.createAlias("FMT." + MbcFichaMonitorizacao.Fields.MBC_ITEM_MONITORIZACOES.toString(), "IMZ");
		criteria.createAlias("IMZ." + MbcItemMonitorizacao.Fields.MBC_TIPO_ITEM_MONITORIZACOES.toString(), "TMZ");
		criteria.createAlias("IMZ." + MbcItemMonitorizacao.Fields.MBC_MONITORIZACOES.toString(), "MOZ");
		
		criteria.add(Restrictions.eq("FMT." + MbcFichaMonitorizacao.Fields.MBC_FICHA_ANESTESIAS.toString() + "." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		criteria.add(Restrictions.eq("FMT." + MbcFichaMonitorizacao.Fields.MBC_ITEM_MONITORIZACOES.toString() + "." + MbcItemMonitorizacao.Fields.SEQ.toString(), seqMbcItemMonitorizacoes));
		criteria.add(Restrictions.isNotNull("MED." + MbcFichaMedMonitorizacao.Fields.MEDICAO.toString()));
		
		criteria.addOrder(Order.asc("TMZ." + MbcTipoItemMonitorizacao.Fields.NOME_REDUZIDO.toString()));
		criteria.addOrder(Order.asc("MED." + MbcFichaMedMonitorizacao.Fields.DTHR_MEDICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcFichaMedMonitorizacao> pesquisarMbcFichaMedMonitorizacaoComMbcTipoItemMonit(
			Long seqMbcFichaAnestesia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaMedMonitorizacao.class);
		criteria.createAlias(MbcFichaMedMonitorizacao.Fields.MBC_FICHA_MONITORIZACOES.toString(), "fmt");
		criteria.createAlias("fmt." + MbcFichaMonitorizacao.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		criteria.createAlias("fmt." + MbcFichaMonitorizacao.Fields.MBC_ITEM_MONITORIZACOES.toString(), "imz");
		criteria.createAlias("imz." + MbcItemMonitorizacao.Fields.MBC_TIPO_ITEM_MONITORIZACOES.toString(), "tmz");
		criteria.createAlias("imz." + MbcItemMonitorizacao.Fields.MBC_MONITORIZACOES.toString(), "moz");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		criteria.add(Restrictions.isNotNull(MbcFichaMedMonitorizacao.Fields.MEDICAO.toString()));
		criteria.add(Restrictions.eq("tmz." + MbcTipoItemMonitorizacao.Fields.GRAFICO.toString(), "GP"));
		
		
		criteria.addOrder(Order.asc("moz." + MbcMonitorizacao.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc("imz." + MbcItemMonitorizacao.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MbcFichaMedMonitorizacao.Fields.DTHR_MEDICAO.toString()));
		
		return executeCriteria(criteria);
		
	}


}
