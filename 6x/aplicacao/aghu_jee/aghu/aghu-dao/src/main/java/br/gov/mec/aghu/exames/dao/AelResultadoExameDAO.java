package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.ambulatorio.vo.ResultadoExameVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.estoque.vo.VoltarProtocoloUnicoVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.model.AelResultadosExamesHist;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.vo.ResultadoExamesVO;
import br.gov.mec.aghu.transplante.vo.GrupoSanguinioFatorRhVO;
import br.gov.mec.aghu.transplante.vo.ResultadoExameCulturalVO;

public class AelResultadoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelResultadoExame> {


	private static final long serialVersionUID = -5112284446286421181L;

	@SuppressWarnings("unchecked")
	public List<AelResultadoExame> buscarResultadosExames(Integer pacCodigo,
			Date dataLiberacao, String nomeCampo) {
		StringBuffer hql = new StringBuffer(280);
		hql.append(" select re");
		hql.append(" from ");
		hql.append(AelResultadoExame.class.getSimpleName());
		hql.append(" as re ");
		hql.append(" join re.");
		hql.append(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString());
		hql.append(" as pcl ");
		hql.append(" join pcl.");
		hql.append(AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString());
		hql.append(" as cal ");
		hql.append(" join re.");
		hql.append(AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString());
		hql.append(" as ise ");
		hql.append(" join ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString());
		hql.append(" as soe ");
		hql.append(" join soe.");
		hql.append(AelSolicitacaoExames.Fields.ATENDIMENTO.toString());
		hql.append(" as atd ");
		hql.append(" where ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString());
		hql.append(" = :sitCodigo ");
		hql.append(" and re.");
		hql.append(AelResultadoExame.Fields.ANULACAO_LAUDO.toString());
		hql.append(" = :anulacaoLaudo ");
		hql.append(" and atd.");
		hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		hql.append(" = :pacCodigo ");
		hql.append(" and substring(cal.");
		hql.append(AelCampoLaudo.Fields.NOME.toString());
		hql.append(", 1, 100) = :nomeCampo ");
		hql.append(" and ise.");
		hql.append(AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString());
		hql.append(" < :dataLiberacao ");
		hql.append(" order by ise.");
		hql.append(AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString());
		hql.append(" desc ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pacCodigo", pacCodigo);
		query.setParameter("dataLiberacao", dataLiberacao);
		query.setParameter("nomeCampo", nomeCampo);
		query.setParameter("sitCodigo", DominioSituacaoItemSolicitacaoExame.LI.toString());
		query.setParameter("anulacaoLaudo", Boolean.FALSE);

		return query.list();
	}

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelResultadoExame.class);
	}

	public boolean existeResultadosNaoAnulados(Integer iseSoeSeq, Short iseSeqp) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));

		return (executeCriteriaCount(criteria) > 0);
	}

	public boolean existeDependenciaResultadoCodificado(Integer resultadoCodificado, Integer grupo) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "rex", DetachedCriteria.INNER_JOIN);
		criteria.add(Restrictions.eq("rex."+AelResultadoCodificado.Fields.SEQ.toString(), resultadoCodificado));
		criteria.add(Restrictions.eq("rex."+AelResultadoCodificado.Fields.GTC_SEQ.toString(), grupo));
		return (executeCriteriaCount(criteria) > 0);
	}

	/**
	 * @HIST AelResultadoExameDAO.listarResultadosExameHist
	 * @param itemSolicitacaoExames
	 * @return
	 */
	public List<AelResultadoExame> listarResultadosExame(AelItemSolicitacaoExames itemSolicitacaoExames){
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "rcd", DetachedCriteria.LEFT_JOIN);
		criteria.createCriteria(AelResultadoExame.Fields.RESULTADO_CARACTERISTICAS.toString(), "rct", DetachedCriteria.LEFT_JOIN);
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), itemSolicitacaoExames.getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), itemSolicitacaoExames.getId().getSeqp()));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));
		
		return this.executeCriteria(criteria);
	}
	
	public List<AelResultadosExamesHist> listarResultadosExameHist(AelItemSolicExameHist itemSolicitacaoExames){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadosExamesHist.class);
		criteria.createCriteria(AelResultadosExamesHist.Fields.RESULTADO_CODIFICADO.toString(), "rcd", DetachedCriteria.LEFT_JOIN);
		criteria.createCriteria(AelResultadosExamesHist.Fields.RESULTADO_CARACTERISTICAS.toString(), "rct", DetachedCriteria.LEFT_JOIN);
		criteria.add(Restrictions.eq(AelResultadosExamesHist.Fields.ISE_SOE_SEQ.toString(), itemSolicitacaoExames.getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelResultadosExamesHist.Fields.ISE_SEQP.toString(), itemSolicitacaoExames.getId().getSeqp()));
		criteria.add(Restrictions.eq(AelResultadosExamesHist.Fields.ANULACAO_LAUDO.toString(), false));

		return this.executeCriteria(criteria);
	}

	public AelResultadoExame buscaMaximoResultadosExamePorPCLeItemSolicitacaoExame(AelParametroCamposLaudo pcl, Integer iseSoeSeq, Short iseSeqp){
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "pcl", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.ID.toString(), pcl.getId()));
		
		criteria.addOrder(Order.desc(AelResultadoExame.Fields.SEQP.toString()));
		
		List<AelResultadoExame> result = executeCriteria(criteria);
		if(result!=null && result.size() > 0){
			return result.get(0);
		}else{
			return null;
		}
	}
	
	
	public List<AelResultadoExame> pesquisarResultadosExamesAnuladosAntibiograma(Integer iseSoeSeq, Short iseSeqp, String pclVelEmaExaSigla, Integer pclVelEmaManSeq, Integer pclVelSeqp, Integer pclCalSeq){

		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.PCL_VEL_EMA_EXA_SIGLA.toString(), pclVelEmaExaSigla));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.PCL_VEL_EMA_MAN_SEQ.toString(), pclVelEmaManSeq));
		
		if(pclVelSeqp != null){
			criteria.add(Restrictions.eq(AelResultadoExame.Fields.PCL_VEL_SEQP.toString(), pclVelSeqp));
		}
		
		if(pclCalSeq != null){
			criteria.add(Restrictions.eq(AelResultadoExame.Fields.PCL_CAL_SEQ.toString(), pclCalSeq));
		}
	
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		return this.executeCriteria(criteria);
	}


	public List<AelResultadoExame> pesquisarResultadosExameAtualizarAnuladosAntibiograma(Integer iseSoeSeq, Short iseSeqp, String pclVelEmaExaSigla, Integer pclVelEmaManSeq){

		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.PCL_VEL_EMA_EXA_SIGLA.toString(), pclVelEmaExaSigla));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.PCL_VEL_EMA_MAN_SEQ.toString(), pclVelEmaManSeq));
		
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		return this.executeCriteria(criteria);
	}
	

	/**
	 * Utilizado nas procedures AELP_PROC_RES_ANTIB e AELP_GRAVA_RES_OUTR
	 * @param aghSolicitacao
	 * @param iseSeqp
	 * @param voVariaveisPacote
	 * @param versaoAtiva
	 * @param codGermeLis
	 * @return
	 */
	public List<AelResultadoExame> pesquisarResultadosExameNaoAnulados(Integer aghSolicitacao, Short iseSeqp, final VoltarProtocoloUnicoVO voVariaveisPacote, Integer versaoAtiva, Integer codGermeLis ){

		DetachedCriteria criteria = obterCriteria();
		
		this.montaCriteriaResultExameNaoAnulados(aghSolicitacao, iseSeqp,voVariaveisPacote, versaoAtiva, codGermeLis,voVariaveisPacote.getParametroCampoSeqp(),criteria);
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));
		
		
		return this.executeCriteria(criteria);
	}

	private void montaCriteriaResultExameNaoAnulados(Integer aghSolicitacao,
			Short iseSeqp, final VoltarProtocoloUnicoVO voVariaveisPacote,
			Integer versaoAtiva, Integer codGermeLis,Integer campoSeqp,
			DetachedCriteria criteria) {
		criteria.createAlias(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL");

		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), aghSolicitacao));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), voVariaveisPacote.getSiglaExame()));
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), voVariaveisPacote.getMaterialAnalise()));
		if(versaoAtiva !=null){
			criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.VEL_SEQP.toString(), versaoAtiva ));
		}
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), codGermeLis));
		if(campoSeqp!=null){
			criteria.add(Restrictions.eq(AelResultadoExame.Fields.SEQP.toString(), campoSeqp));
		}
		
		

		
	}
	
	public Integer obterMaxSeqpResultadoExame(Integer aghSolicitacao, Short iseSeqp, final VoltarProtocoloUnicoVO voVariaveisPacote, Integer versaoAtivaAntibiograma, Integer codGermeLis ){

		DetachedCriteria criteria = obterCriteria();
		criteria.setProjection(Projections.max(AelResultadoExame.Fields.SEQP.toString()));
		
		this.montaCriteriaResultExameNaoAnulados(aghSolicitacao, iseSeqp,voVariaveisPacote, versaoAtivaAntibiograma, codGermeLis,null,criteria);
		
		Integer seqp = 0;
		final Object maxSeqp = this.executeCriteriaUniqueResult(criteria);
		if (maxSeqp != null) {
			seqp = (Integer) maxSeqp +1;
		}

		return seqp;

	}

	public List<AelResultadoExame> pesquisarResultadosExameOrdemGerme(Integer aghSolicitacao, Short iseSeqp, final VoltarProtocoloUnicoVO voVariaveisPacote, Integer versaoAtivaAntibiograma, Integer campoLaudoGerme, Integer grupoCodificadoGerme, Integer codificadoGerme){
		
		DetachedCriteria criteria = obterCriteria();
		
		this.montaCriteriaResultExameNaoAnulados(aghSolicitacao, iseSeqp,voVariaveisPacote, versaoAtivaAntibiograma, campoLaudoGerme,voVariaveisPacote.getParametroCampoSeqp(),criteria);
		
		criteria.createAlias(AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "RCD");
		
		criteria.add(Restrictions.eq("RCD."+AelResultadoCodificado.Fields.GTC_SEQ.toString(), grupoCodificadoGerme));
		criteria.add(Restrictions.eq("RCD."+AelResultadoCodificado.Fields.SEQ.toString(), codificadoGerme));
		
		return this.executeCriteria(criteria);
	}
	
		
	public Long quantidadeResultadosExame(Integer iseSoeSeq, Short iseSeqp, String ufeEmaExaSigla, Integer ufeEmaManSeq, Integer velSeqp, 
			Integer calSeq, Integer seqp) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "pcl", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));

		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.ID.toString(), new AelParametroCampoLaudoId(ufeEmaExaSigla, ufeEmaManSeq, velSeqp, calSeq, seqp)));

		return executeCriteriaCount(criteria);
	}
	
	public List<AelResultadoExame> listarResultadosExameAnulados(Integer iseSoeSeq, Short iseSeqp, String ufeEmaExaSigla, Integer ufeEmaManSeq) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "pcl", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), ufeEmaExaSigla));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), ufeEmaManSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));

		return executeCriteria(criteria);
	}
	
	/**
	 * cursor: c_versao_laudo
	 * 5945
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return List<AelResultadoExame>
	 */
	public List<AelResultadoExame> listarResultadoVersaoLaudo(Integer iseSoeSeq, Short iseSeqp) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));

		return executeCriteria(criteria);
	}
	
	
	/**
	 * Estoria 5945
	 * CURSOR c_resultado da procedure AELK_POL_SUM_EXAMES
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param pclVelEmaExaSigla
	 * @param pclVelEmaManSeq
	 * @param pclVelSeqp
	 * @param pclCalSeq
	 * @param seqp
	 * @return
	 */
	public List<AelResultadoExame> listarResultadosExames(Integer iseSoeSeq, 
														  Short iseSeqp, 
														  String pclVelEmaExaSigla, 
														  Integer pclVelEmaManSeq,
														  Integer pclVelSeqp,
														  Integer pclCalSeq,
														  Integer seqp) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "pcl", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), pclVelEmaExaSigla));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), pclVelEmaManSeq));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_SEQP.toString(), pclVelSeqp));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pclCalSeq));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.SEQP.toString(), seqp));
		
		return executeCriteria(criteria);
	}
	
	public List<AelResultadosExamesHist> listarResultadosExamesHist(Integer iseSoeSeq, 
			  Short iseSeqp, 
			  String pclVelEmaExaSigla, 
			  Integer pclVelEmaManSeq,
			  Integer pclVelSeqp,
			  Integer pclCalSeq,
			  Integer seqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadosExamesHist.class);
		criteria.createCriteria(AelResultadosExamesHist.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "pcl", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AelResultadosExamesHist.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadosExamesHist.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadosExamesHist.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString(), pclVelEmaExaSigla));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString(), pclVelEmaManSeq));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.VEL_SEQP.toString(), pclVelSeqp));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pclCalSeq));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.SEQP.toString(), seqp));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Estoria 5945
	 * CURSOR c_pcl_ree da procedure AELK_POL_SUM_EXAMES
	 * 
	 * @param iseSoeSeq
	 * @param seqp
	 * @return
	 */
	public List<AelResultadoExame> listarMascaraResultadosExames(Integer iseSoeSeq, Short iseSeqp) {
		
		DetachedCriteria criteria = obterCriteria();
		criteria.createCriteria(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "pcl", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("pcl."+AelParametroCamposLaudo.Fields.SUMARIO_SEM_MASCARA.toString(), Boolean.TRUE));

		return executeCriteria(criteria);
	}
	

	/**
	 * cursor: c_resul_cod
	 * 22585
	 */
	public Boolean isResultadoCodigo(Integer iseSoeSeq, Short iseSeqp, Integer pclCalSeq, Integer rcdGtcSeq, Integer rcdSeqp) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "REC", DetachedCriteria.INNER_JOIN);
		criteria.createAlias(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL", DetachedCriteria.INNER_JOIN);
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pclCalSeq));
		criteria.add(Restrictions.eq("REC."+AelResultadoCodificado.Fields.GTC_SEQ.toString(), rcdGtcSeq));
		criteria.add(Restrictions.eq("REC."+AelResultadoCodificado.Fields.SEQ.toString(), rcdSeqp));

		return executeCriteriaCount(criteria) > 0 ? true : false;
	}
	
	/**
	 * cursor: c_resul_num
	 * 22585
	 */
	public Boolean isResultadoNumerico(Integer iseSoeSeq, Short iseSeqp, Integer pclCalSeq, Long resulNumIni, Long resulNumFim) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL", DetachedCriteria.INNER_JOIN);
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.CAL_SEQ.toString(), pclCalSeq));
		criteria.add(Restrictions.between(AelResultadoExame.Fields.VALOR.toString(), resulNumIni, resulNumFim));
		
		return executeCriteriaCount(criteria) > 0 ? true : false;
	}

	/**
	 * cursor: c_resul_alfa
	 * 22585
	 */
	public  List<String> obterDescricaoResultadoAlfa(Integer iseSoeSeq, Short iseSeqp, Integer pclCalSeq) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.setProjection(Projections.property(AelResultadoExame.Fields.DESCRICAO.toString()));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.PCL_CAL_SEQ.toString(), pclCalSeq));
		
		return executeCriteria(criteria);
	}

	
	
	public AelResultadoExame obterResultadosExamesParaSecretaria(AelResultadoExameId id) {

		DetachedCriteria criteria = obterCriteria();
		
		criteria.createCriteria(AelResultadoExame.Fields.RESULTADO_CARACTERISTICAS.toString(), 
				DetachedCriteria.LEFT_JOIN);
		criteria.createCriteria(AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), 
				DetachedCriteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ID.toString(), id));
		
				
		criteria.add(Restrictions.eq(AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		criteria.addOrder(Order.asc(AelResultadoExame.Fields.RCD_GTC_SEQ.toString()));
		criteria.addOrder(Order.asc(AelResultadoExame.Fields.RCD_SEQP.toString()));
		
		List<AelResultadoExame> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		}
		
		return result.get(0);
}
	
	/**
	 * Consulta C29 do Web Service #39353
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public List<AelResultadoExame> pesquisarPorAelItemSolicitacaoExames(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class, "REE");
		criteria.createAlias("REE." + AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq("REE." + AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), false));
		return super.executeCriteria(criteria);
	}
	
	/**
	 * #41772 - C4 - Union 1
	 * @author marcelo.deus
	 */
	private DetachedCriteria obterResultadosExameCulturalUnion1(Integer codPaciente, Integer codMaterial){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class, "REE");
		
		criteria.createAlias("REE." + AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE")
				.createAlias("REE." + AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL")
				.createAlias("REE." + AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "REC")
				.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE")
				.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.add(Restrictions.eq("REE." + AelResultadoExame.Fields.PCL_VEL_EMA_EXA_SIGLA.toString(), "CECPH"))
				.add(Restrictions.eq("REE." + AelResultadoExame.Fields.PCL_VEL_EMA_MAN_SEQ.toString(), codMaterial))
				.add(Restrictions.eq("REE." + AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), DominioSimNao.N.isSim()))
				.add(Restrictions.eq("REE." + AelResultadoExame.Fields.PCL_CAL_SEQ.toString(), 4899))
				.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("REC." + AelResultadoCodificado.Fields.DESCRICAO.toString()), 
						ResultadoExameCulturalVO.Fields.DESCRICAO.toString())
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()), 
						ResultadoExameCulturalVO.Fields.DATA_LIBERADA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoExameCulturalVO.class));
		
		return criteria;
	}

	/**
	 * #41772 - C4 - Union 2
	 * @author marcelo.deus
	 */
	private DetachedCriteria obterResultadosExameCulturalUnion2(Integer codPaciente, Integer codMaterial){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class, "REE");
		
		criteria.createAlias("REE." + AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE")
				.createAlias("REE." + AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL")
				.createAlias("REE." + AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "REC")
				.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE")
				.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.add(Restrictions.eq("REE." + AelResultadoExame.Fields.PCL_VEL_EMA_EXA_SIGLA.toString(), "\\"))
				.add(Restrictions.eq("REE." + AelResultadoExame.Fields.PCL_VEL_EMA_MAN_SEQ.toString(), codMaterial))
				.add(Restrictions.eq("REE." + AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), DominioSimNao.N.isSim()))
				.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("REC." + AelResultadoCodificado.Fields.DESCRICAO.toString()), 
						ResultadoExameCulturalVO.Fields.DESCRICAO.toString())
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()), 
						ResultadoExameCulturalVO.Fields.DATA_LIBERADA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoExameCulturalVO.class));
		
		return criteria;
	}
	
	/**
	 * #41772 - C4 - Junção das criterias
	 * @author marcelo.deus
	 */
	public List<ResultadoExameCulturalVO> obterListaResultadosExameCultural(Integer codPaciente, Integer codMaterial){
		
		DetachedCriteria criteriaUnion1 = obterResultadosExameCulturalUnion1(codPaciente, codMaterial);
		DetachedCriteria criteriaUnion2 = obterResultadosExameCulturalUnion2(codPaciente, codMaterial);
		
		List<ResultadoExameCulturalVO> listaUnion1 = executeCriteria(criteriaUnion1);
		List<ResultadoExameCulturalVO> listaUnion2 = executeCriteria(criteriaUnion2);
		
		if(!listaUnion1.isEmpty() && !listaUnion2.isEmpty()){
			listaUnion1.addAll(listaUnion2);
			return listaUnion1;
		} else if(!listaUnion1.isEmpty() && listaUnion2.isEmpty()){
			return listaUnion1;
		} else if(listaUnion1.isEmpty() && !listaUnion2.isEmpty()){
			return listaUnion2;
		} else {
			return null;
		}
	}
	
	public List<ResultadoExamesVO> obterResultadoExamesSaps3(Integer atdSeq,String param1, Integer param2, Integer param3){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadosExamesHist.class,"ARE");
		
		criteria.createAlias("ARE."+AelResultadosExamesHist.Fields.ITEM_SOLICITACAO_EXAME.toString(),"ISE", JoinType.INNER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(),"ASE", JoinType.INNER_JOIN);
		criteria.createAlias("ASE."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(),"AAT", JoinType.INNER_JOIN);
		criteria.createAlias("ASE."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(),"UNF", JoinType.INNER_JOIN);
		criteria.createAlias("UNF."+AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(),"CUF", JoinType.INNER_JOIN);

		if (atdSeq != null) {
			criteria.add(Restrictions.eq("AAT."+ AghAtendimentos.Fields.SEQ.toString(),atdSeq));
		}
		criteria.add(Restrictions.eq("CUF."+ AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(),ConstanteAghCaractUnidFuncionais.UNID_CTI));
		criteria.add(Restrictions.eq("ISE."+ AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(),"LI"));
		criteria.add(Restrictions.eq("ARE."+ AelResultadosExamesHist.Fields.ANULACAO_LAUDO.toString(),false));
		criteria.add(Restrictions.geProperty("ISE."+ AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString(),"AAT."+ AghAtendimentos.Fields.DTHR_INGRESSO_UNIDADE.toString()));
		// parametros
		criteria.add(Restrictions.eq("ISE."+ AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(),param1));
		criteria.add(Restrictions.eq("ISE."+ AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(),param2));
		criteria.add(Restrictions.eq("ISE."+ AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString(),Short.valueOf(param3.toString())));
		
		criteria.addOrder(Order.asc("ISE."+ AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()));
		
		ProjectionList pList = Projections.projectionList();
			pList.add(Projections.property("ISE."+ AelItemSolicitacaoExames.Fields.DTHR_LIBERADA.toString()), ResultadoExamesVO.Fields.DTHR_LIBERADA.toString());
			pList.add(Projections.property("ARE."+ AelResultadosExamesHist.Fields.VALOR.toString()),  ResultadoExamesVO.Fields.VALOR.toString());
		
		criteria.setProjection(pList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoExamesVO.class));
		
		return executeCriteria(criteria,0,1,null,false);
	}
	
	/**
	 * #41798 - C7
	 * @param pacCodigo
	 * @return
	 */
	public Boolean verificarExisteResultadoExame(Integer pacCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class, "REE");
		
		criteria.createAlias("REE."+AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("SOE."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		//criteria.createAlias("REE."+AelResultadoExame.Fields.MTX_EXAME_ULT_RESULTS.toString(), "EUR");

		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction(" SUBSTR(ise1_.sit_codigo,1,2) = 'LI' "));
		}else{
			criteria.add(Restrictions.sqlRestriction(" SUBSTR(ise1_.sit_codigo,1,2) = 'LI' "));
		}
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PAC_COD.toString(), pacCodigo));
		
		return (executeCriteriaCount(criteria) > 0);
	}
	
	/**
	 * #50701 - P4 - c_resultado 
	 * @param soeSeq
	 * @param seqp
	 * @return ResultadoExameVO
	 */
	public ResultadoExameVO obterResultadoExamePorItemSolicitacaoExame(Integer soeSeq, Short seqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class, "REE");
		
		criteria.createAlias("REE."+AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL");
		
		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.ISE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.sqlProjection("(this_.valor / power(10,pcl1_.qtde_casas_decimais)) valor", new String[] {ResultadoExameVO.Fields.VALOR.toString()}, new Type[] {BigDecimalType.INSTANCE}));
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.RCD_GTC_SEQ.toString()), ResultadoExameVO.Fields.RCD_GTC_SEQ.toString());
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.RCD_SEQP.toString()), ResultadoExameVO.Fields.RCD_SEQP.toString());
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.CAC_SEQ.toString()), ResultadoExameVO.Fields.CAC_SEQ.toString());
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.PCL_VEL_EMA_EXA_SIGLA.toString()), ResultadoExameVO.Fields.PCL_VEL_EMA_EXA_SIGLA.toString());
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.PCL_VEL_EMA_MAN_SEQ.toString()), ResultadoExameVO.Fields.PCL_VEL_EMA_MAN_SEQ.toString());
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.PCL_VEL_SEQP.toString()), ResultadoExameVO.Fields.PCL_VEL_SEQP.toString());
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.PCL_CAL_SEQ.toString()), ResultadoExameVO.Fields.PCL_CAL_SEQ.toString());
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.PCL_SEQP.toString()), ResultadoExameVO.Fields.PCL_SEQP.toString());
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.SEQP.toString()), ResultadoExameVO.Fields.SEQP.toString());		
		projList.add(Projections.property("REE."+ AelResultadoExame.Fields.DESCRICAO.toString()), ResultadoExameVO.Fields.DESCRICAO.toString());		
		criteria.setProjection(projList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoExameVO.class));
		
		return (ResultadoExameVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #50701 - P4 - Contador
	 * @param soeSeq
	 * @param seqp
	 * @return Long
	 */
	public Long contarResultadoExame(Integer soeSeq, Short seqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class, "REE");
		
		criteria.createAlias("REE."+AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL");
		
		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.ISE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq("REE."+AelResultadoExame.Fields.IND_ANULACAO_LAUDO.toString(), Boolean.FALSE));
		
		return executeCriteriaCount(criteria);
	}
	public String obterFatorRhExamesRealizados(Integer pacCodigo){
		final String P_AGHU_EXAME_FATOR_RH = "P_AGHU_EXAME_FATOR_RH";
		
		String fator = obterDadosSangueExamesRealizadosPorParametro(pacCodigo, P_AGHU_EXAME_FATOR_RH);
		
		if(StringUtils.isNotEmpty(fator)){
			if(fator.equalsIgnoreCase("POSITIVO")){
				return "+";
			}else if(fator.equalsIgnoreCase("NEGATIVO")){
				return "-";
			}
		}
		return StringUtils.EMPTY; 
	}
	
	public String obterFatorFatorSanguinioExamesRealizados(Integer pacCodigo){
		final String P_AGHU_EXAME_GRUPO_ABO = "P_AGHU_EXAME_GRUPO_ABO";
		
		return obterDadosSangueExamesRealizadosPorParametro(pacCodigo, P_AGHU_EXAME_GRUPO_ABO);
	}
	
	/* Usado em TMO e orgãos. */
	private String obterDadosSangueExamesRealizadosPorParametro(Integer pacCodigo, String parametro){
		List<GrupoSanguinioFatorRhVO> retorno = new ArrayList<GrupoSanguinioFatorRhVO>();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoExame.class, "REE");
		
		criteria.createAlias("REE." + AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "REC");
		criteria.createAlias("REE." + AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty("REC." + AelResultadoCodificado.Fields.DESCRICAO.toString()).as(GrupoSanguinioFatorRhVO.Fields.DESCRICAO.toString()))
				.add(Projections.max("REE." + AelResultadoExame.Fields.SEQP.toString()).as(GrupoSanguinioFatorRhVO.Fields.SEQ.toString()))
		);
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_COD.toString(), pacCodigo));
		criteria.add(Restrictions.eq("ISE." + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "LI"));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghParametros.class, "PAR");
		
		subCriteria.setProjection(Projections.property("PAR."+ AghParametros.Fields.VLR_TEXTO.toString()));
		subCriteria.add(Restrictions.eq("PAR." + AghParametros.Fields.NOME.toString(), parametro));
		
		criteria.add(Subqueries.propertyEq("REE." + AelResultadoExame.Fields.PCL_VEL_EMA_EXA_SIGLA.toString(), subCriteria));
		
		criteria.setResultTransformer(Transformers.aliasToBean(GrupoSanguinioFatorRhVO.class)); 

		retorno = executeCriteria(criteria, 0, 1, null, true);
		
		if(!retorno.isEmpty()){
			return retorno.get(0).getDescricao();
		}
		return StringUtils.EMPTY;
	}
	
}