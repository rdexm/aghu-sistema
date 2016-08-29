package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioExibicaoParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelVersaoLaudo;

public class AelParametroCamposLaudoDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelParametroCamposLaudo> {

	private static final long serialVersionUID = -9178172483848737164L;

	@Override
	protected void obterValorSequencialId(AelParametroCamposLaudo elemento) {
		if (elemento == null || elemento.getId() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}

		if(elemento.getId().getSeqp()==null){
			final Integer maxSeqp = this.obterPCLSeqpMax(elemento.getId());
			if (maxSeqp != null) {
				elemento.getId().setSeqp(maxSeqp+1);
			}else{
				elemento.getId().setSeqp(1);
			}
		}
	}

	public List<AelParametroCamposLaudo> buscarAelParametroCamposLaudoComVersaoLaudoAtivaPorAelExamesMaterialAnalise(
			AelExamesMaterialAnalise materialNew) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class);
		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");
		criteria.add(Restrictions.eq("VEL."+AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), materialNew.getId().getExaSigla()));
		criteria.add(Restrictions.eq("VEL."+AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), materialNew.getId().getManSeq()));
		criteria.add(Restrictions.eq("VEL."+AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));
		
		return executeCriteria(criteria);
	}
	
	public Integer obterPCLSeqpMax(final AelParametroCampoLaudoId pclId) {
		DetachedCriteria criteria = criarCriteriaObterParametroCampoLaudo(pclId);		

		criteria.setProjection(Projections.max(AelParametroCamposLaudo.Fields.SEQP.toString()));
		final Object objMax = this.executeCriteriaUniqueResult(criteria);
		return (Integer) objMax;
	}

	private DetachedCriteria criarCriteriaObterParametroCampoLaudo(final AelParametroCampoLaudoId pclId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class);

		
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), pclId.getVelEmaExaSigla()));
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), pclId.getVelEmaManSeq()));
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.VEL_SEQP.toString(), pclId.getVelSeqp()));
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pclId.getCalSeq()));
		return criteria;
	}
	
	
	/**
	 * ORADB aelk_vnc_rn.rn_vncp_ver_update
	 * @param calSeq (seq campo laudo)
	 * @return
	 */
	public List<AelParametroCamposLaudo> pesquisarAelParametroCamposLaudoValorNormalidadeInativo(Integer calSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");
		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.CAL_SEQ.toString(),calSeq));
		criteria.add(Restrictions.not(Restrictions.eq("VEL."+AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.E)));
		
		return executeCriteria(criteria);
	}

	public List<AelParametroCamposLaudo> pesquisarCamposTelaPorVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class);
		
		criteria.createAlias(AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "CAL", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), versaoLaudo));

		criteria.add(Restrictions.or(
				Restrictions.eq(AelParametroCamposLaudo.Fields.EXIBICAO.toString(), DominioExibicaoParametroCamposLaudo.A),
				Restrictions.eq(AelParametroCamposLaudo.Fields.EXIBICAO.toString(), DominioExibicaoParametroCamposLaudo.T)));

		criteria.addOrder(Order.asc(AelParametroCamposLaudo.Fields.POSICAO_LINHA_TELA.toString()));
		criteria.addOrder(Order.asc(AelParametroCamposLaudo.Fields.POSICAO_COLUNA_TELA.toString()));

		return executeCriteria(criteria);
	}
	
	public List<AelParametroCamposLaudo> pesquisarCamposTelaEdicaoMascaraPorVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class);
		criteria.createAlias(AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "campoLaudo");
		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VL");
		criteria.createAlias("VL." + AelVersaoLaudo.Fields.EXAME_MATERIAL_ANALISE.toString(), "EXA");
		
		criteria.createAlias(AelParametroCamposLaudo.Fields.CAMPO_VINCULADOS.toString(), AelParametroCamposLaudo.Fields.CAMPO_VINCULADOS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelParametroCamposLaudo.Fields.CAMPO_RELACIONADO.toString(), AelParametroCamposLaudo.Fields.CAMPO_RELACIONADO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelParametroCamposLaudo.Fields.CAMPO_RELACIONADO_CODIFICADO.toString(), AelParametroCamposLaudo.Fields.CAMPO_RELACIONADO_CODIFICADO.toString(), JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), versaoLaudo));
		criteria.addOrder(Order.asc(AelParametroCamposLaudo.Fields.POSICAO_LINHA_TELA.toString()));
		criteria.addOrder(Order.asc(AelParametroCamposLaudo.Fields.POSICAO_COLUNA_TELA.toString()));

		return executeCriteria(criteria);
	}
	
	public List<AelParametroCamposLaudo> pesquisarCamposRelatorioPorVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class);

		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), versaoLaudo));

		criteria.add(Restrictions.or(
				Restrictions.eq(AelParametroCamposLaudo.Fields.EXIBICAO.toString(), DominioExibicaoParametroCamposLaudo.A),
				Restrictions.eq(AelParametroCamposLaudo.Fields.EXIBICAO.toString(), DominioExibicaoParametroCamposLaudo.R)));

		criteria.addOrder(Order.asc(AelParametroCamposLaudo.Fields.POSICAO_LINHA_IMPRESSAO.toString()));
		criteria.addOrder(Order.asc(AelParametroCamposLaudo.Fields.POSICAO_COLUNA_IMPRESSAO.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Verifica se existe Versão do Parâmetro do Campo Laudo ativa
	 * @param seqCampoLaudo
	 * @return
	 */
	public boolean existeVersaoParametroAtivaCampoLaudo(final Integer seqCampoLaudo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");
		
		criteria.createAlias("PCL." + AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");

		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), seqCampoLaudo));
		criteria.add(Restrictions.eqProperty("VEL." + AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), "PCL." + AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.eqProperty("VEL." + AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), "PCL." + AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("VEL." + AelVersaoLaudo.Fields.SEQP.toString(), "PCL." + AelParametroCamposLaudo.Fields.VEL_SEQP.toString()));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	/**
	 * Pesquisa parâmetros campos laudo com versão ativa
	 * @param exaSigla
	 * @param manSeq
	 * @param calSeq
	 * @return
	 */
	public  List<AelParametroCamposLaudo> pesquisarParametroCamposLaudoComVersaoLaudoAtiva(String exaSigla, Integer manSeq, Integer calSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");
		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.CAL_SEQ.toString(),calSeq));
		criteria.add(Restrictions.eq("VEL."+AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq("VEL."+AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("VEL."+AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Obtem o SEQP da versão ativa da versão do campo laudo para interfaceamento. Vide: cursor c_versao_ativa
	 * 
	 * @param exaSigla
	 * @param manSeq
	 * @param parametroLis
	 * @return
	 */
	public Integer obterSeqpVersaoAtivaInterfaceamento(String exaSigla, Integer manSeq, Integer pParametroLis) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");

		// Seqp da versão do laudo
		criteria.setProjection(Projections.property("VEL." + AelVersaoLaudo.Fields.SEQP.toString()));

		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");

		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pParametroLis));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem o SEQP da versão ativa do parâmetro campo laudo para interfaceamento. Vide: cursor c_ocorrencia_campo_laudo
	 * 
	 * @param exaSigla
	 * @param manSeq
	 * @param parametroLis
	 * @param versaoAtiva
	 * @return
	 */
	public Integer obterSeqpOcorrenciaCampoLaudoInterfaceamento(String exaSigla, Integer manSeq, Integer pParametroLis, Integer vVersaoAtiva) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");

		// Seqp da versão do parâmetro campo laudo
		criteria.setProjection(Projections.property(AelParametroCamposLaudo.Fields.SEQP.toString()));

		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");

		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pParametroLis));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.SEQP.toString(), vVersaoAtiva));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Obtem o SEQP da versão ativa da versão do campo laudo para antibiograma. Vide: cursor v_versao_ativa_antibio
	 * 
	 * @param exaSigla
	 * @param manSeq
	 * @param parametroLis
	 * @return
	 */
	public Integer obterSeqpVersaoAtivaAntibiogramaInterfaceamento(String exaSigla, Integer manSeq, Integer codGermeLis) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");
		
		// DISTINCT Seqp da versão do laudo
		criteria.setProjection(Projections.distinct(Projections.property("VEL." + AelVersaoLaudo.Fields.SEQP.toString())));

		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");

		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), codGermeLis));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.SITUACAO.toString(), DominioSituacaoVersaoLaudo.A));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Verifica a existência do desenho de máscara em um exame material
	 * @param exaSigla
	 * @param manSeq
	 * @param seqp
	 * @param calSeq
	 * @return
	 */
	public Boolean existeParametroCamposLaudoPorExameMaterialDesenhoMascara(String exaSigla, Integer manSeq, Integer seqp, Integer calSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");

		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");
		
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.SEQP.toString(), seqp));
		
		criteria.add(Restrictions.eq("PCL." + AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), calSeq));
	

		return executeCriteriaCount(criteria) > 0;
	}
	
	/**
	 * Pesquisa parâmetos de campo laudo através de uma versão do laudo
	 * @param exaSigla
	 * @param manSeq
	 * @param seqp
	 * @param calSeq
	 * @return
	 */
	public  List<AelParametroCamposLaudo> pesquisarParametroCamposLaudoPorVersaoLaudo(String emaExaSigla, Integer emaManSeq, Integer seqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class, "PCL");

		criteria.createAlias(AelParametroCamposLaudo.Fields.AEL_VERSAO_LAUDO.toString(), "VEL");
		
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq("VEL." + AelVersaoLaudo.Fields.SEQP.toString(), seqp));

		return executeCriteria(criteria);
	}

	public List<AelParametroCamposLaudo> obterCampoLaudo(AelParametroCampoLaudoId id) {
		return executeCriteria(criarCriteriaObterParametroCampoLaudo(id));
	}

	public AelParametroCamposLaudo obterCampoLaudoPorChavePrimaria(AelParametroCampoLaudoId pclId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelParametroCamposLaudo.class);

		
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), pclId.getVelEmaExaSigla()));
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), pclId.getVelEmaManSeq()));
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.VEL_SEQP.toString(), pclId.getVelSeqp()));
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pclId.getCalSeq()));
		criteria.add(Restrictions.eq(AelParametroCamposLaudo.Fields.SEQP.toString(), pclId.getSeqp()));

		return (AelParametroCamposLaudo) executeCriteriaUniqueResult(criteria);
	}
}
