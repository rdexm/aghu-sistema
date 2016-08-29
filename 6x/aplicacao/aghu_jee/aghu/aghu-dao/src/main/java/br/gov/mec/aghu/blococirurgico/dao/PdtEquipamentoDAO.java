package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtEquipPorProc;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PdtEquipamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtEquipamento> {

	private static final long serialVersionUID = 2908172385874144604L;
	
	/**
	 * 
	 * Busca as Equipamentos pelo Nome
	 * 
	 * @return Lista de equipamentos
	 */	
	public List<PdtEquipamento> pesquisarEquipamentoPorNome(Object strPesquisa) {
		DetachedCriteria cri = criarCriteriaEquipamentoPorNome(strPesquisa);
	
		cri.addOrder(Order.asc(PdtEquipamento.Fields.DESCRICAO.toString()));

		return executeCriteria(cri);
	}

	public Long pesquisarEquipamentoPorNomeCount(Object strPesquisa) {
		DetachedCriteria cri = criarCriteriaEquipamentoPorNome(strPesquisa);
	
		return executeCriteriaCount(cri);
	}
	
	private DetachedCriteria criarCriteriaEquipamentoPorNome(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(PdtEquipamento.class);

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(PdtEquipamento.Fields.SEQ.toString(), Short.valueOf(strPesquisa.toString())));
		}else if(strPesquisa != null && !strPesquisa.toString().isEmpty()){
			cri.add(Restrictions.ilike(PdtEquipamento.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE));
		}

		cri.add(Restrictions.eq(PdtEquipamento.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		return cri;
	}


	public List<PdtEquipamento> pesquisarEquipamentosDiagnosticoTerapeutico(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String descricao, Short codigo,
			DominioSituacao situacao, String enderecoImagens) {

		DetachedCriteria criteria = criarCriteriaEquipamentoDiagnosticoTerapeutico(
				descricao, codigo, situacao, enderecoImagens);
		
		criteria.addOrder(Order.asc(PdtEquipamento.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria criarCriteriaEquipamentoDiagnosticoTerapeutico(
			String descricao, Short codigo, DominioSituacao situacao,
			String enderecoImagens) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtEquipamento.class);
		
		if (codigo != null){
			criteria.add(Restrictions.eq(PdtEquipamento.Fields.SEQ.toString(), codigo));
		}
		if (descricao != null && !descricao.isEmpty()){
			criteria.add(Restrictions.ilike(PdtEquipamento.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (situacao != null){
			criteria.add(Restrictions.eq(PdtEquipamento.Fields.IND_SITUACAO.toString(), situacao));
		}
		if (enderecoImagens != null && !enderecoImagens.isEmpty()){
			//Corrige problema de busca na base
			enderecoImagens = enderecoImagens.replace("\\", "\\\\");
			criteria.add(Restrictions.ilike(PdtEquipamento.Fields.PATCH_IMAGENS.toString(), enderecoImagens, MatchMode.ANYWHERE));
		}
		return criteria;
	}

	public Long pesquisarEquipamentosDiagnosticoTerapeuticoCount(
			String descricao, Short codigo, DominioSituacao situacao,
			String enderecoImagens) {
		DetachedCriteria criteria = criarCriteriaEquipamentoDiagnosticoTerapeutico(
				descricao, codigo, situacao, enderecoImagens);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<PdtEquipamento> listarPdtEquipamentoAtivoPorDescricao(Object strPesquisa) {
		DetachedCriteria criteria = criarCriteriaListarPdtEquipamentoAtivoPorDescricao(strPesquisa);
		criteria.addOrder(Order.asc(PdtEquipamento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public Long listarPdtEquipamentoAtivoPorDescricaoCount(Object strPesquisa) {
		DetachedCriteria criteria = criarCriteriaListarPdtEquipamentoAtivoPorDescricao(strPesquisa);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaListarPdtEquipamentoAtivoPorDescricao(Object strPesquisa) {
		String seqOuDescricao = StringUtils.trimToNull(strPesquisa.toString());
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtEquipamento.class);
		criteria.add(Restrictions.eq(PdtEquipamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (StringUtils.isNotEmpty(seqOuDescricao)) {
			if (CoreUtil.isNumeroShort(seqOuDescricao)){
				criteria.add(Restrictions.eq(PdtEquipamento.Fields.SEQ.toString(), Short.parseShort(seqOuDescricao)));
			}else {
				criteria.add(Restrictions.ilike(PdtEquipamento.Fields.DESCRICAO.toString(), seqOuDescricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	


	public List<PdtEquipamento> pesquisarEquipamentosDiagnosticoTerapeutico(final Integer dptSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtEquipamento.class, "DEQ");
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(PdtEquipPorProc.class, "EPP");
		subCriteria.setProjection(Projections.property("EPP."+PdtEquipPorProc.Fields.DEQ_SEQ.toString()));
		subCriteria.add(Restrictions.eq("EPP."+PdtEquipPorProc.Fields.DPT_SEQ.toString(), dptSeq));
		
		criteria.add(Property.forName("DEQ."+PdtEquipamento.Fields.SEQ.toString()).in(subCriteria));
		criteria.add(Restrictions.eq("DEQ."+PdtEquipamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("DEQ."+PdtEquipamento.Fields.DESCRICAO.toString()));
		
		
		return executeCriteria(criteria);
	}
}

