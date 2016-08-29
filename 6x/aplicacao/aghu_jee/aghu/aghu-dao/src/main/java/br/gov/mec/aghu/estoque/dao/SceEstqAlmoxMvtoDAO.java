package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceEstqAlmoxMvto;
import br.gov.mec.aghu.model.SceEstqAlmoxMvtoId;

/**
 * 
 * @author lalegre
 *
 */
public class SceEstqAlmoxMvtoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceEstqAlmoxMvto> {
	
	private static final long serialVersionUID = 8515751603740519559L;

	@Override
	protected void obterValorSequencialId(SceEstqAlmoxMvto elemento) {
		
		if (elemento == null || elemento.getSceEstoqueAlmoxarifado() == null || elemento.getSceTipoMovimentos() == null) {
			throw new IllegalArgumentException("Estoque Almoxarifado ou Tipo de Movimento nao foi informado corretamente.");
		}
		
		SceEstqAlmoxMvtoId id = new SceEstqAlmoxMvtoId();
		id.setEalSeq(elemento.getSceEstoqueAlmoxarifado().getSeq());
		id.setTmvComplemento(elemento.getSceTipoMovimentos().getId().getComplemento());
		id.setTmvSeq(elemento.getSceTipoMovimentos().getId().getSeq());
		
		elemento.setId(id);
	}
	
	
	
	public SceEstqAlmoxMvto obterSceEstqAlmoxMvto(Integer ealSeq, Short tmvSeq, Byte tmvComplemento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstqAlmoxMvto.class);

		criteria.add(Restrictions.eq(SceEstqAlmoxMvto.Fields.ESTOQUE_ALMOXARIFADO_SEQ.toString(), ealSeq));
		criteria.add(Restrictions.eq(SceEstqAlmoxMvto.Fields.TIPO_MOVIMENTO_SEQ.toString(), tmvSeq));
		criteria.add(Restrictions.eq(SceEstqAlmoxMvto.Fields.TIPO_MOVIMENTO_COMPLEMENTO.toString(), tmvComplemento));
		
		
		
		List<SceEstqAlmoxMvto> listaEstAlm = executeCriteria(criteria);
		
		if (listaEstAlm != null && !listaEstAlm.isEmpty()) {
			
			return listaEstAlm.get(0);

		}
		
		return null;
		
	}
	
	/**
	 * Obtém a quantidade de registros em estoque almoxarifado movimento
	 * @return
	 */
	public Long obterQuantidadeRegistros() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstqAlmoxMvto.class);
		criteria.setProjection(Projections.count(SceEstqAlmoxMvto.Fields.ESTOQUE_ALMOXARIFADO_SEQ.toString()));
		final Long max = (Long) executeCriteriaUniqueResult(criteria);
		if (max == null) {
			return 0l;
		}
		return max;
	}
	
	/**
	 * Limpa tabela SCE_ESTQ_ALMOX_MVTOS para receber movimentos do novo mês
	 * @return
	 */
	public Integer limparEstqAlmoxMvtoFechamentoEstoqueMensal(){
		
		SQLQuery q = createSQLQuery("DELETE FROM AGH.SCE_ESTQ_ALMOX_MVTOS");

		// Executa a limpeza e retorna a quantidade de registros removidos
		Integer retorno = q.executeUpdate();
		this.flush();
		return retorno;
		
	}
	
	

}
