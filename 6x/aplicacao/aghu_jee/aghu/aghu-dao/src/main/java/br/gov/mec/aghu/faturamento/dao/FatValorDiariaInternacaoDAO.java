package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatDiariaInternacao;
import br.gov.mec.aghu.model.FatValorDiariaInternacao;

public class FatValorDiariaInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatValorDiariaInternacao> {

	
	private static final long serialVersionUID = -6319595026489452784L;
	
	public FatValorDiariaInternacao buscarPrimeiraValidaPorTipo(String tipoValorConta){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatValorDiariaInternacao.class);
		criteria.createAlias(FatValorDiariaInternacao.Fields.DIARIA_INTERNACAO.toString(), FatValorDiariaInternacao.Fields.DIARIA_INTERNACAO.toString());
		criteria.add(Restrictions.le(FatValorDiariaInternacao.Fields.DATA_INICIO_VALIDADE.toString(), new Date()));
		criteria.add(Restrictions.or(
						Restrictions.isNull(FatValorDiariaInternacao.Fields.DATA_FIM_VALIDADE.toString()), 
						Restrictions.ge(FatValorDiariaInternacao.Fields.DATA_FIM_VALIDADE.toString(), new Date())
					));
		criteria.add(Restrictions.eq(FatValorDiariaInternacao.Fields.DIARIA_INTERNACAO.toString() + "." + FatDiariaInternacao.Fields.TIPO_VALOR_CONTA.toString(), tipoValorConta));
		List<FatValorDiariaInternacao> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
//	#2146
	public List<FatValorDiariaInternacao> listarValorDiariaInternacao(String orderProperty, final FatValorDiariaInternacao fatValorDiariaInternacao, FatDiariaInternacao fatDiariaInternacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatValorDiariaInternacao.class);
		
		criteria.add(Restrictions.eq(FatValorDiariaInternacao.Fields.DIN_SEQ.toString(), 
				fatDiariaInternacao.getSeq()));
		if (StringUtils.isEmpty(orderProperty)) {
			criteria.addOrder(Order.asc(FatValorDiariaInternacao.Fields.DATA_INICIO_VALIDADE.toString()));
		}
			return executeCriteria(criteria);
	}
	
	/**
	 * Metodo para retornar lista de valores das diarias por seq de acordo com DATA_FIM_VALIDADE
	 * #44376
	 */
	public List<FatValorDiariaInternacao> validarDiariaInternacao(final Integer DinSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatValorDiariaInternacao.class);
		criteria.add(Restrictions.eq(FatValorDiariaInternacao.Fields.DIN_SEQ.toString(), DinSeq));
		criteria.add(Restrictions.isNull(FatValorDiariaInternacao.Fields.DATA_FIM_VALIDADE.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Metodo para retornar lista de valores das diarias por seq
	 * #2146
	 */
	public List<FatValorDiariaInternacao> obterListaValorDiaria(Integer codigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatValorDiariaInternacao.class);
		criteria.add(Restrictions.eq(FatValorDiariaInternacao.Fields.DIN_SEQ.toString(), codigo));
		return executeCriteria(criteria);
	}
	
	//#2146
	public void removerValorDiariaInternacao(FatValorDiariaInternacao fatValorDiariaInternacao) {
		FatValorDiariaInternacao fatValorDiariaInternacaoExcluir = obterPorChavePrimaria(fatValorDiariaInternacao.getId());
		this.remover(fatValorDiariaInternacaoExcluir);
	}
}
