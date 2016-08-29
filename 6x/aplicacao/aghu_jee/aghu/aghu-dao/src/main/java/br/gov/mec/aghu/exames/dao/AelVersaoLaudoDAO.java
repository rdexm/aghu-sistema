package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AelVersaoLaudoId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class AelVersaoLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelVersaoLaudo> {

	private static final long serialVersionUID = 5684890878051780197L;

	@Override
	protected void obterValorSequencialId(AelVersaoLaudo elemento) {

		if (elemento.getExameMaterialAnalise() == null || elemento.getExameMaterialAnalise().getId() == null) {
			throw new IllegalArgumentException("Exame material de análise não foi informado corretamente.");
		}

		// Instância um novo id
		AelVersaoLaudoId id = new AelVersaoLaudoId();

		String exaSigla = elemento.getExameMaterialAnalise().getId().getExaSigla();
		Integer manSeq = elemento.getExameMaterialAnalise().getId().getManSeq();

		id.setEmaExaSigla(exaSigla);
		id.setEmaManSeq(manSeq);

		Integer seqp = this.obterMaxSeqp(exaSigla, manSeq);
		if(seqp == null){
			seqp=0;
		}
		id.setSeqp(++seqp); // Incrementa seqp

		// Seta id no novo elemento
		elemento.setId(id);
	}

	/**
	 * Obtém o seqp máximo através do exame material de análise
	 * 
	 * @param exaSigla
	 * @param manSeq
	 * @return
	 */
	public Integer obterMaxSeqp(final String exaSigla, final Integer manSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelVersaoLaudo.class);

		criteria.setProjection(Projections.max(AelVersaoLaudo.Fields.SEQP.toString()));

		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), manSeq));

		return (Integer) executeCriteriaUniqueResult(criteria);

	}

	/**
	 * Método que retorna a versão do laudo ativa baseado no item de solicitação de exame.
	 * 
	 * @HIST AelVersaoLaudoDAO.obterVersaoLaudoPorItemSolicitacaoExameHist
	 * @param itemSolicitacaoExame
	 * @return
	 */
	public AelVersaoLaudo obterVersaoLaudoPorItemSolicitacaoExame(
			AelExames exame, AelMateriaisAnalises materialAnalise,
			Integer velSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelVersaoLaudo.class);

		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), exame.getSigla()));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), materialAnalise.getSeq()));
		criteria.addOrder(Order.desc(AelVersaoLaudo.Fields.CRIADO_EM.toString()));
		
		if(velSeqp != null) {
			criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.SEQP.toString(), velSeqp));
		}else{
			criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		}
		
		List<AelVersaoLaudo> list = executeCriteria(criteria);
		
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}

	}
	

	/**
	 * Método que retorna a versão do laudo ativa por emaExaSigla e emaManSeq
	 * 
	 * @param itemSolicitacaoExame
	 * @return
	 */
	public AelVersaoLaudo obterVersaoLaudoPorEmaExaSiglaEManSeq(final String exaSigla, final Integer manSeq, Integer velSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelVersaoLaudo.class);

		criteria.createAlias(AelVersaoLaudo.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "AELX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "AELM", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), manSeq));
		criteria.addOrder(Order.desc(AelVersaoLaudo.Fields.CRIADO_EM.toString()));
		
		if(velSeqp != null) {
			criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.SEQP.toString(), velSeqp));
		}else{
			criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacao.A));
		}
		
		List<AelVersaoLaudo> list = executeCriteria(criteria);
		
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}

	}
	
	

	/**
	 * Pesquisa Versão do Laudo através da sigla do Exame e seq do Material
	 * 
	 * @param seq
	 * @param ordem
	 * @return
	 */
	public List<AelVersaoLaudo> pesquisarVersaoLaudoPorExameMaterialAnalise(final String exaSigla, final Integer manSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelVersaoLaudo.class);

		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), manSeq));

		return executeCriteria(criteria);

	}

	/**
	 * Obtem a criteria necessária para a consulta da estória #5391 Manter Cadastro de Máscaras de Laudos
	 * 
	 * @param unidadeExecutora
	 * @param versaoLaudo
	 * @return
	 */
	private DetachedCriteria getCriteriaPesquisarVersaoLaudo(AghUnidadesFuncionais unidadeFuncional, AelVersaoLaudo versaoLaudo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelVersaoLaudo.class, "VEL");
		criteria.createAlias("VEL." + AelVersaoLaudo.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA");

		// Situação
		if (versaoLaudo.getSituacao() != null) {
			criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.SITUACAO.toString(), versaoLaudo.getSituacao()));
		}

		// Exame Material de Análise
		if (versaoLaudo.getExameMaterialAnalise() != null) {

			String exaSigla = versaoLaudo.getExameMaterialAnalise().getId().getExaSigla();
			criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), exaSigla));

			Integer manSeq = versaoLaudo.getExameMaterialAnalise().getId().getManSeq();
			criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), manSeq));
		}

		// Máscara
		if (StringUtils.isNotEmpty(versaoLaudo.getNomeDesenho())) {
			criteria.add(Restrictions.ilike("VEL." + AelVersaoLaudo.Fields.NOME_DESENHO.toString(), StringUtils.trim(versaoLaudo.getNomeDesenho()), MatchMode.ANYWHERE));
		}

		// Consulta secundária
		DetachedCriteria subQuery = DetachedCriteria.forClass(AelUnfExecutaExames.class, "UFE");
		subQuery.setProjection(Projections.property("UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString()));

		subQuery.add(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));

		subQuery.add(Property.forName("UFE." + AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString()).eqProperty("VEL." + AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString()));
		subQuery.add(Property.forName("UFE." + AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString()).eqProperty("VEL." + AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString()));

		// Acrescenta consulta secundária (EXISTS)
		criteria.add(Subqueries.exists(subQuery));

		return criteria;
	}

	/**
	 * Obtém a quantidade de resultados da pesquisa na estória #5391 Manter Cadastro de Máscaras de Laudos
	 * 
	 * @param unidadeExecutora
	 * @param versaoLaudo
	 * @return
	 */
	public Long pesquisarVersaoLaudoCount(AghUnidadesFuncionais unidadeFuncional, AelVersaoLaudo versaoLaudo) {
		DetachedCriteria criteria = this.getCriteriaPesquisarVersaoLaudo(unidadeFuncional, versaoLaudo);
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa a da estória #5391 Manter Cadastro de Máscaras de Laudos
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unidadeExecutora
	 * @param versaoLaudo
	 * @return
	 */
	public List<AelVersaoLaudo> pesquisarVersaoLaudo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AghUnidadesFuncionais unidadeFuncional,
			AelVersaoLaudo versaoLaudo) {
		DetachedCriteria criteria = this.getCriteriaPesquisarVersaoLaudo(unidadeFuncional, versaoLaudo);
		

		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "EMA_MA");
		
		criteria.addOrder(Order.asc(AelVersaoLaudo.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc(AelVersaoLaudo.Fields.NOME_DESENHO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, null, false);

	}

	/**
	 * Verifica a ocorrência de versões do laudo duplicadas
	 * 
	 * @param versaoLaudo
	 * @return
	 */
	public Boolean existeVersaoLaudoVerificarSituacaoDuplicada(AelVersaoLaudo versaoLaudo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelVersaoLaudo.class);
		
		String exaSigla = versaoLaudo.getExameMaterialAnalise().getId().getExaSigla();
		Integer manSeq = versaoLaudo.getExameMaterialAnalise().getId().getManSeq();
		
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), manSeq));

		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.NOME_DESENHO.toString(), versaoLaudo.getNomeDesenho()));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.SITUACAO.toString(), versaoLaudo.getSituacao()));
		
		if (versaoLaudo.getId() != null && versaoLaudo.getId().getSeqp() != null) {
			criteria.add(Restrictions.ne(AelVersaoLaudo.Fields.SEQP.toString(), versaoLaudo.getId().getSeqp()));
		}

		return executeCriteriaCount(criteria) > 0;
	}

	/**
	 * Pesquisa versões do laudo para inativação
	 * 
	 * @param versaoLaudo
	 * @return
	 */
	public List<AelVersaoLaudo> pesquisarVersaoLaudoInativarSituacao(AelVersaoLaudo versaoLaudo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelVersaoLaudo.class);

		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), versaoLaudo.getId().getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), versaoLaudo.getId().getEmaManSeq()));

		criteria.add(Restrictions.ne(AelVersaoLaudo.Fields.SEQP.toString(), versaoLaudo.getId().getSeqp()));

		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.NOME_DESENHO.toString(), versaoLaudo.getNomeDesenho()));

		
		return executeCriteria(criteria);

	}
	
	/**
	 * Metodo utilizado na #5978. c_tot
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public Long pesquisarQuantidadeVersoesAtivasLaudo(Integer iseSoeSeq, Short iseSeqp) {
		return executeCriteriaCount(criarCriteriaPesquisarQuantidadeVersoesAtivasLaudo(iseSoeSeq, iseSeqp));
	}
	
	/**
	 * Metodo utilizado na #5978. c_versao
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public AelVersaoLaudo obterVersaoAtivaLaudo(Integer iseSoeSeq, Short iseSeqp) {
		Object obj =  executeCriteriaUniqueResult(criarCriteriaPesquisarQuantidadeVersoesAtivasLaudo(iseSoeSeq, iseSeqp));
		return obj != null ? (AelVersaoLaudo) obj : null;
	}

	private DetachedCriteria criarCriteriaPesquisarQuantidadeVersoesAtivasLaudo(
			Integer iseSoeSeq, Short iseSeqp) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "VEL");
		
		dc.add(Restrictions.eq("VEL.".concat(AelVersaoLaudo.Fields.SITUACAO.toString()), DominioSituacaoVersaoLaudo.A));
		
		//Substitui o join com a tabela ael_item_solicitacao_exames
		DetachedCriteria subQueryItem = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		subQueryItem.add(Restrictions.eq("ISE.".concat(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), iseSoeSeq));
		subQueryItem.add(Restrictions.eq("ISE.".concat(AelItemSolicitacaoExames.Fields.SEQP.toString()), iseSeqp));
		subQueryItem.add(Restrictions.eqProperty("VEL.".concat(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString()), "ISE.".concat(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString())));
		subQueryItem.add(Restrictions.eqProperty("VEL.".concat(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString()), "ISE.".concat(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString())));
		
		subQueryItem.setProjection(Projections.projectionList().add(Projections.property(
				AelItemSolicitacaoExames.Fields.ISE_SOE_SEQ.toString()))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.ISE_SEQP.toString()))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()))
				.add(Projections.property(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString())));
		
		//Substitui o join com a tabela ael_agrp_pesquisa_x_exames
		DetachedCriteria subQueryPesquisa = DetachedCriteria.forClass(AelAgrpPesquisaXExame.class, "APE");
		subQueryPesquisa.add(Restrictions.eqProperty("APE.".concat(AelAgrpPesquisaXExame.Fields.UFE_EMA_EXA_SIGLA.toString()), "VEL.".concat(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString())));
		subQueryPesquisa.add(Restrictions.eqProperty("APE.".concat(AelAgrpPesquisaXExame.Fields.UFE_EMA_MAN_SEQ.toString()), "VEL.".concat(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString())));
		subQueryPesquisa.add(Restrictions.eq("APE.".concat(AelAgrpPesquisaXExame.Fields.IND_SITUACAO.toString()), DominioSituacao.A));

		subQueryPesquisa.setProjection(Projections.projectionList().add(Projections.property(
										 AelAgrpPesquisaXExame.Fields.UFE_EMA_EXA_SIGLA.toString()))
				.add(Projections.property(AelAgrpPesquisaXExame.Fields.UFE_EMA_MAN_SEQ.toString()))
				.add(Projections.property(AelAgrpPesquisaXExame.Fields.IND_SITUACAO.toString()))
				.add(Projections.property(AelAgrpPesquisaXExame.Fields.NOME_LAUDO.toString())));
		
		dc.add(Subqueries.exists(subQueryItem));
		dc.add(Subqueries.exists(subQueryPesquisa));
		
		return dc;
	}

	public AelVersaoLaudo obterVersaoLaudoComDependencias(final AelVersaoLaudoId versaoLaudoPK) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelVersaoLaudo.class);

		criteria.createAlias(AelVersaoLaudo.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "AELX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "AELM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), versaoLaudoPK.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), versaoLaudoPK.getEmaManSeq()));
		criteria.add(Restrictions.eq(AelVersaoLaudo.Fields.SEQP.toString(), versaoLaudoPK.getSeqp()));
		
		return (AelVersaoLaudo)executeCriteriaUniqueResult(criteria);
	}

}
