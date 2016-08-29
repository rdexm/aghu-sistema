package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import br.gov.mec.aghu.blococirurgico.vo.PendenciaWorkflowVO;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.RapServidores;

public class AghWFExecutorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghWFExecutor> {
	
	private static final long serialVersionUID = 2924156461078059499L;


	public  DetachedCriteria obterCriteriaBasica() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFExecutor.class);		
		return criteria;
	}

	// #37052 - Consulta para Central de Pendencias.
	public List<PendenciaWorkflowVO> buscarPendenciasWorkflowPorUsuarioLogado(RapServidores usuarioLogado) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFExecutor.class, "WEX");
		criteria.createAlias("WEX."+AghWFExecutor.Fields.WTE_SEQ.toString(), "WTE");
		criteria.createAlias("WEX."+AghWFExecutor.Fields.WFL_SEQ.toString(), "WLF");
		criteria.createAlias("WEX."+AghWFExecutor.Fields.WET_SEQ .toString(), "WET");

		criteria.add(Restrictions.eq("WEX."+AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString(), usuarioLogado.getId().getMatricula()));
		criteria.add(Restrictions.eq("WEX."+AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString(), usuarioLogado.getId().getVinCodigo()));
		criteria.add(Restrictions.isNull("WEX."+AghWFExecutor.Fields.DT_EXECUCAO.toString()));
		criteria.add(Restrictions.isNull("WLF."+AghWFFluxo.Fields.DT_FIM.toString()));
		criteria.add(Restrictions.isNull("WET."+AghWFEtapa.Fields.DT_FIM.toString()));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("WEX."+AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString()),"matriculaExecutor")
				.add(Projections.property("WEX."+AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString()),"vinCodigoExecutor")
				.add(Projections.property("WEX."+AghWFExecutor.Fields.IND_RECEBE_NOTIF.toString()),"indRecebeNotificacao")
				.add(Projections.property("WTE."+AghWFTemplateEtapa.Fields.CODIGO.toString()),"codigo")
				.add(Projections.property("WTE."+AghWFTemplateEtapa.Fields.DESCRICAO.toString()),"descricao")
				.add(Projections.property("WTE."+AghWFTemplateEtapa.Fields.URL.toString()),"url"));
		criteria.setResultTransformer(Transformers.aliasToBean(PendenciaWorkflowVO.class));		
		return executeCriteria(criteria);
	}	

	//#37054
	public AghWFExecutor obterExecutorPorRapServidor(RapServidores servidor, Integer seqEtapa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFExecutor.class, "WEX");
		criteria.createAlias("WEX." + AghWFExecutor.Fields.WET_SEQ.toString(), "WET");
		criteria.add(Restrictions.eq("WEX."+AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("WEX."+AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));		
		criteria.add(Restrictions.eq("WET."+AghWFEtapa.Fields.SEQ.toString(), seqEtapa));
		return (AghWFExecutor) executeCriteriaUniqueResult(criteria);
	}
	
	// #31954
	public List<AghWFExecutor> obterExecutorPorCodigoFluxo(Integer fluxoSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghWFExecutor.class, "WEX");
		criteria.createAlias("WEX." + AghWFExecutor.Fields.WFL_SEQ.toString(), "WFL");
		
		if(fluxoSeq != null){
			criteria.add(Restrictions.eq("WFL." + AghWFFluxo.Fields.SEQ.toString(), fluxoSeq));
		}
		
		criteria.add(Restrictions.eq("WEX." + AghWFExecutor.Fields.IND_RECEBE_NOTIF.toString(), Boolean.TRUE));
		
		return executeCriteria(criteria);
	}
	//TODO ADICIONADO, VERIFICAR NA ESTÓRIA DE CANCELAMENTO
	public List<AghWFExecutor> obterExecutoresPorFluxo(RapServidores servidor, Integer seqFluxo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFExecutor.class, "WEX");
		criteria.createAlias("WEX." + AghWFExecutor.Fields.WFL_SEQ.toString(), "WFL");
		criteria.add(Restrictions.eq("WEX."+AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("WEX."+AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));		
		criteria.add(Restrictions.eq("WFL." + AghWFFluxo.Fields.SEQ.toString(), seqFluxo));
		criteria.addOrder(Order.desc("WEX." + AghWFExecutor.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	//Retorna os executores pelo fluxo
	public List<AghWFExecutor> obterExecutoresEnvolvidosNoProcessoDeAutorizacao(Integer seqFluxo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFExecutor.class, "WEX");
		criteria.add(Restrictions.eq("WEX." + AghWFExecutor.Fields.WFL_SEQ_ID, seqFluxo));
		criteria.add(Restrictions.eq("WEX." + AghWFExecutor.Fields.IND_RECEBE_NOTIF, Boolean.TRUE));
		criteria.addOrder(Order.desc("WEX." + AghWFExecutor.Fields.SEQ.toString()));
		List<AghWFExecutor> lista = executeCriteria(criteria);
		for (AghWFExecutor item : lista) {
			super.initialize(item.getRapServidor());
			super.initialize(item.getRapServidor().getPessoaFisica());
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> obterEmailsExecutores(Short requisicaoSeq) {
		StringBuffer sql = new StringBuffer(500);
		sql.append("SELECT RAP_SERVIDORES.EMAIL "
			+ " FROM AGH.AGH_WF_EXECUTORES, AGH.AGH_WF_TEMPLATE_ETAPAS, AGH.AGH_WF_FLUXOS, AGH.MBC_REQUISICAO_OPMES, AGH.RAP_SERVIDORES "
			+ " WHERE  AGH_WF_EXECUTORES.WTE_SEQ = AGH_WF_TEMPLATE_ETAPAS.SEQ "
			+ " AND AGH_WF_FLUXOS.SEQ = MBC_REQUISICAO_OPMES.WFL_SEQ "
			+ " AND AGH_WF_EXECUTORES.IND_RECEBE_NOTIF = 'S' "
			+ " AND AGH_WF_EXECUTORES.SER_MATRICULA = RAP_SERVIDORES.MATRICULA "
			+ " AND AGH_WF_EXECUTORES.SER_VIN_CODIGO = RAP_SERVIDORES.VIN_CODIGO "
			+ " AND MBC_REQUISICAO_OPMES.SEQ = :requisicaoSeq ");
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("requisicaoSeq", requisicaoSeq);
		return query.list();
	}
	
	// #37052 - Consulta para Central de Pendencias.
	public List<AghWFExecutor> buscarExecutoresUnanimes(AghWFEtapa etapa) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFExecutor.class, "WEX");
			criteria.createAlias("WEX."+AghWFExecutor.Fields.SERVIDOR.toString(), "SER");
			criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
			criteria.createAlias("WEX."+AghWFExecutor.Fields.WTE_SEQ.toString(), "WTE");
			criteria.add(Restrictions.eq("WEX."+AghWFExecutor.Fields.WFL_SEQ_ID.toString(), etapa.getFluxo().getSeq()));
			criteria.add(Restrictions.eq("WTE."+AghWFTemplateEtapa.Fields.CODIGO.toString(), etapa.getTemplateEtapa().getCodigo()));
			criteria.add(Restrictions.eq("WEX."+AghWFExecutor.Fields.AUTORIZADO_EXECUTAR.toString(), true));

			return executeCriteria(criteria);
	}
}
