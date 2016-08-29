package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioVivoMorto;
import br.gov.mec.aghu.model.FatCadCidNascimento;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FatCadCidNascimentoDAO extends BaseDao<FatCadCidNascimento> {

	private static final long serialVersionUID = -4428789941858959998L;

	public List<FatCadCidNascimento> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			DominioVivoMorto vivo, DominioVivoMorto morto, String cid) {
		
		DetachedCriteria criteria = criarPesquisaCriteria(vivo, morto, cid);

		if (orderProperty == null) {
			criteria.addOrder(Order.asc(FatCadCidNascimento.Fields.SEQ.toString()));
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria criarPesquisaCriteria(DominioVivoMorto vivo, DominioVivoMorto morto, String cid) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCadCidNascimento.class);
		
		criteria.createCriteria(FatCadCidNascimento.Fields.SERVIDOR.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(FatCadCidNascimento.Fields.SERVIDOR_ALTERA.toString(), JoinType.LEFT_OUTER_JOIN);

		if (vivo != null) {
			criteria.add(Restrictions.eq(FatCadCidNascimento.Fields.VIVO.toString(), vivo));
		} 

		if (morto != null) {
			criteria.add(Restrictions.eq(FatCadCidNascimento.Fields.MORTO.toString(), morto));
		}
		
		if (StringUtils.isNotBlank(cid)) {
			criteria.add(Restrictions.ilike(FatCadCidNascimento.Fields.CID.toString(), this.replaceCaracterEspecial(cid), MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	/**
	 * Formata as ocorrencias de "%" e "_" para que as mesmas sejam submetidas à pesquisa.
	 * 
	 * @param descricao {@link String}
	 * @return {@link String}
	 */
	private String replaceCaracterEspecial(String descricao) {
		
		return descricao.replace("_", "\\_").replace("%", "\\%");
	}
	
	private DetachedCriteria criarCadastroCriteria(FatCadCidNascimento fatCadCidNascimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCadCidNascimento.class);
		
		criteria.createCriteria(FatCadCidNascimento.Fields.SERVIDOR.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria(FatCadCidNascimento.Fields.SERVIDOR_ALTERA.toString(), JoinType.LEFT_OUTER_JOIN);

		if (fatCadCidNascimento.getVivo() != null) {
			criteria.add(Restrictions.eq(FatCadCidNascimento.Fields.VIVO.toString(), fatCadCidNascimento.getVivo()));
		}

		if (fatCadCidNascimento.getMorto() != null) {
			criteria.add(Restrictions.eq(FatCadCidNascimento.Fields.MORTO.toString(), fatCadCidNascimento.getMorto()));
		}
		
		if (StringUtils.isNotBlank(fatCadCidNascimento.getCid())) {
			criteria.add(Restrictions.eq(FatCadCidNascimento.Fields.CID.toString(), fatCadCidNascimento.getCid()));
		}

		return criteria;
	}

	public Long pesquisarCount(DominioVivoMorto vivo, DominioVivoMorto morto, String cid) {

		return executeCriteriaCount(criarPesquisaCriteria(vivo, morto, cid));
	}

	public FatCadCidNascimento obterFatCadCidNascimento(FatCadCidNascimento fatCadCidNascimento) {

		DetachedCriteria criteria = criarCadastroCriteria(fatCadCidNascimento);
		
		List<FatCadCidNascimento> lista = executeCriteria(criteria);
		
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		} else {
			return null;
		}
	}
}
