package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatDiariaInternacao;
import br.gov.mec.aghu.model.FatValorDiariaInternacao;

public class FatDiariaInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatDiariaInternacao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2219517515424984478L;

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
	
	//  #2146
	public List<FatDiariaInternacao> listarDiariasInternacao(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			final FatDiariaInternacao fatDiariaInternacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatDiariaInternacao.class);
		listarDiariasInternacaoFiltro(criteria, fatDiariaInternacao);
		
		if (StringUtils.isEmpty(orderProperty)) {
			criteria.addOrder(Order.asc(FatDiariaInternacao.Fields.SEQ.toString()));
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	//	#2146
	public Long listarDiariasInternacaoCount(FatDiariaInternacao fatDiariaInternacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatDiariaInternacao.class);
		listarDiariasInternacaoFiltro(criteria, fatDiariaInternacao);
		return this.executeCriteriaCount(criteria);
	}
	
	//	#2146
	public void listarDiariasInternacaoFiltro(final DetachedCriteria criteria ,FatDiariaInternacao fatDiariaInternacao){
		if(fatDiariaInternacao.getSeq()!=null){
			criteria.add(Restrictions.eq(FatDiariaInternacao.Fields.SEQ.toString(), fatDiariaInternacao.getSeq()));
		}
		if(StringUtils.isNotBlank(fatDiariaInternacao.getDescricao())){
			criteria.add(Restrictions.ilike(FatDiariaInternacao.Fields.DESCRICAO.toString(), this.replaceCaracterEspecial(fatDiariaInternacao.getDescricao()), MatchMode.ANYWHERE));
		}
		if(fatDiariaInternacao.getTipoValorConta()!=null){
			criteria.add(Restrictions.eq(FatDiariaInternacao.Fields.TIPO_VALOR_CONTA.toString(), fatDiariaInternacao.getTipoValorConta()));
		}
		if(fatDiariaInternacao.getQuantidadeDias()!=null){
			criteria.add(Restrictions.eq(FatDiariaInternacao.Fields.QUANTIDADE_DIAS.toString(), fatDiariaInternacao.getQuantidadeDias()));
		}
	}
	
	//	#2146
	public void removerDiariaInternacao(FatDiariaInternacao fatDiariaInternacao) {
		FatDiariaInternacao fatDiariaInternacaoExcluir = obterPorChavePrimaria(fatDiariaInternacao.getSeq());		
		this.remover(fatDiariaInternacaoExcluir);
	}
	
	/**
	 * #2146
	 * @param descricao
	 * @return
	 */
	private String replaceCaracterEspecial(String descricao) {
        return descricao.replace("_", "\\_").replace("%", "\\%");
	}
}