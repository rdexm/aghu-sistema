package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MbcDestinoPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcDestinoPaciente> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 364130605593548206L;

	@Override
	protected void obterValorSequencialId(final MbcDestinoPaciente elemento) {
		elemento.setSeq(this.getNextVal(SequenceID.MBC_DPA_SQ1).byteValue());
	}

	/**
	 * Monta criteria da pesquisa paginada dos destinos dos pacientes
	 * 
	 * @param elemento
	 * @return
	 */
	private DetachedCriteria montarCriteriaPesquisarDestinoPaciente(MbcDestinoPaciente elemento, Byte param) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDestinoPaciente.class);
		criteria.createAlias(MbcDestinoPaciente.Fields.SERVIDOR.toString(), "RAP");
		
		if (elemento.getSeq() != null) {
			criteria.add(Restrictions.eq(MbcDestinoPaciente.Fields.SEQ.toString(), elemento.getSeq()));
		}

		if (!StringUtils.isEmpty(elemento.getDescricao())) {
			criteria.add(Restrictions.ilike(MbcDestinoPaciente.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
		}

		if (elemento.getSituacao() != null) {
			criteria.add(Restrictions.eq(MbcDestinoPaciente.Fields.SITUACAO.toString(), elemento.getSituacao()));
		}
		
		if(param != null){
			criteria.add(Restrictions.ne(MbcDestinoPaciente.Fields.SEQ.toString(), param));
		}

		return criteria;
	}

	/**
	 * Pesquisa paginada dos destinos dos pacientes
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return
	 */
	public List<MbcDestinoPaciente> pesquisarDestinoPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcDestinoPaciente elemento, Byte param) {

		DetachedCriteria criteria = montarCriteriaPesquisarDestinoPaciente(elemento, param);

		// Ordena pelo sequencial
		criteria.addOrder(Order.asc(MbcDestinoPaciente.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Contabiliza resultados da pesquisa paginada dos destinos dos pacientes
	 * 
	 * @param elemento
	 * @return
	 */
	public Long pesquisarDestinoPacienteCount(MbcDestinoPaciente elemento) {
		DetachedCriteria criteria = montarCriteriaPesquisarDestinoPaciente(elemento, null);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa destino do paciente por descrição
	 * 
	 * @param descricao
	 * @return
	 */
	public MbcDestinoPaciente pesquisarDestinoPacientePorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDestinoPaciente.class);
		criteria.add(Restrictions.ilike(MbcDestinoPaciente.Fields.DESCRICAO.toString(), descricao));
		return (MbcDestinoPaciente) executeCriteriaUniqueResult(criteria);
	}

	public List<MbcDestinoPaciente> pesquisarDestinoPacientePorSeqOuDescricao(Object pesquisa, Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Byte param){
		MbcDestinoPaciente elemento = new MbcDestinoPaciente();
		
		processaPesquisaEmDestinoPaciente(pesquisa, elemento);
		
		return pesquisarDestinoPaciente(firstResult, maxResult, orderProperty, asc, elemento, param);
	}
	
	public Long pesquisarDestinoPacientePorSeqOuDescricaoCount(Object pesquisa){
		
		MbcDestinoPaciente elemento = new MbcDestinoPaciente();
		
		processaPesquisaEmDestinoPaciente(pesquisa, elemento);
		
		return pesquisarDestinoPacienteCount(elemento);
	}
	
	public List<MbcDestinoPaciente> pesquisarDestinoPacienteAtivoPorSeqOuDescricao(Object objPesquisa, Boolean asc, Byte param, MbcDestinoPaciente.Fields ... fieldsOrder){
		MbcDestinoPaciente elemento = new MbcDestinoPaciente();
		processaPesquisaEmDestinoPaciente(objPesquisa, elemento);
		DetachedCriteria criteria = montarCriteriaPesquisarDestinoPaciente(elemento, param);
		criteria.add(Restrictions.eq(MbcDestinoPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		if(Boolean.TRUE.equals(asc)){//pode ser nulo, então testa se equals true
			for(MbcDestinoPaciente.Fields field : fieldsOrder){
				criteria.addOrder(Order.asc(field.toString()));
			}
		}else{
			for(MbcDestinoPaciente.Fields field : fieldsOrder){
				criteria.addOrder(Order.desc(field.toString()));
			}
		}
		
		return this.executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(Object objPesquisa, Byte param){
		MbcDestinoPaciente elemento = new MbcDestinoPaciente();
		processaPesquisaEmDestinoPaciente(objPesquisa, elemento);
		DetachedCriteria criteria = montarCriteriaPesquisarDestinoPaciente(elemento, param);
		criteria.add(Restrictions.eq(MbcDestinoPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteriaCount(criteria);
	}

	private void processaPesquisaEmDestinoPaciente(Object pesquisa,
			MbcDestinoPaciente elemento) {
		if (pesquisa != null && StringUtils.isNotBlank(pesquisa.toString())) {
			if (CoreUtil.isNumeroByte(pesquisa)) {
				elemento.setSeq(Byte.valueOf(pesquisa.toString()));
			} else {
				elemento.setDescricao(pesquisa.toString());
			}
		}
	}

}
