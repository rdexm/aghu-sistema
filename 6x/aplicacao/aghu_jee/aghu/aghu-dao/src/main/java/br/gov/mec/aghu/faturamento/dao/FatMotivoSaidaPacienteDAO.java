package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatMotivoSaidaPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMotivoSaidaPaciente> {

	private static final long serialVersionUID = 6818203108832521730L;

	public List<FatMotivoSaidaPaciente> listarMotivoSaidaPaciente(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoSaidaPaciente.class);
		criteria.add(Restrictions.eq(FatMotivoSaidaPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(CoreUtil.isNumeroShort(objPesquisa)) {
			criteria.add(Restrictions.eq(FatMotivoSaidaPaciente.Fields.SEQ.toString(), Short.valueOf((String)objPesquisa)));
		}
		else {
			if(!StringUtils.isEmpty((String)objPesquisa)) {
				criteria.add(Restrictions.ilike(FatMotivoSaidaPaciente.Fields.DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
			}
		}
 
		criteria.addOrder(Order.asc(FatMotivoSaidaPaciente.Fields.SEQ.toString()));
		
		List<FatMotivoSaidaPaciente> lista = executeCriteria(criteria);
		
		return lista;
	}

	public Long listarMotivoSaidaPacienteCount(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoSaidaPaciente.class);
		criteria.add(Restrictions.eq(FatMotivoSaidaPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(CoreUtil.isNumeroShort(objPesquisa)) {
			criteria.add(Restrictions.eq(FatMotivoSaidaPaciente.Fields.SEQ.toString(), Short.valueOf((String)objPesquisa)));
		}
		else {
			if(!StringUtils.isEmpty((String)objPesquisa)) {
				criteria.add(Restrictions.ilike(FatMotivoSaidaPaciente.Fields.DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
			}
		}
 
		return executeCriteriaCount(criteria);
	}

	/**
	 * Recupera lista paginada para a tela de consulta
	 * 
	 * @param firstResult {@link Integer}
	 * @param maxResult {@link Integer}
	 * @param orderProperty {@link String}
	 * @param asc {@link Boolean}
	 * @param entity {@link FatMotivoSaidaPaciente}
	 * @return {@link List} de {@link FatMotivoSaidaPaciente}
	 */
	public List<FatMotivoSaidaPaciente> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatMotivoSaidaPaciente entity) {
		
		DetachedCriteria criteria = criarPesquisaCriteria(entity);

		if (orderProperty == null) {
			criteria.addOrder(Order.asc(FatMotivoSaidaPaciente.Fields.SEQ.toString()));
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Recupera o contador para a tela de consulta
	 * 
	 * @param entity {@link FatMotivoSaidaPaciente}
	 * @return {@link Long}
	 */
	public Long recuperarCount(FatMotivoSaidaPaciente entity) {
		
		return executeCriteriaCount(criarPesquisaCriteria(entity));
	}
	
	/**
	 * Monta o criteria para a consulta passando os filtros selecionados
	 * 
	 * @param filtro {@link FatMotivoSaidaPaciente}
	 * @return {@link DetachedCriteria}
	 */
	private DetachedCriteria criarPesquisaCriteria(FatMotivoSaidaPaciente filtro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoSaidaPaciente.class);
		
		if (filtro != null) {
			
			if (filtro.getSeq() != null) {
				criteria.add(Restrictions.eq(FatMotivoSaidaPaciente.Fields.SEQ.toString(), filtro.getSeq()));
			}
			
			if (filtro.getCodigoSus() != null) {
				criteria.add(Restrictions.eq(FatMotivoSaidaPaciente.Fields.CODIGO_SUS.toString(), filtro.getCodigoSus()));
			}
			
			if (StringUtils.isNotBlank(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike(FatMotivoSaidaPaciente.Fields.DESCRICAO.toString(), 
						replaceCaracterEspecial(filtro.getDescricao()), MatchMode.ANYWHERE));
			}
			
			if (filtro.getSituacao() != null) {
				criteria.add(Restrictions.eq(FatMotivoSaidaPaciente.Fields.SITUACAO.toString(), filtro.getSituacao()));
			}
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
}
