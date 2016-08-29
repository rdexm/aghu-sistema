package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import br.gov.mec.aghu.blococirurgico.vo.ConsultarHistoricoProcessoOpmeVO;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFHistoricoExecucao;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;

public class AghWFHistoricoExecucaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghWFHistoricoExecucao> {
	
	private static final long serialVersionUID = -3150699039101300002L;

	public List<ConsultarHistoricoProcessoOpmeVO> consultarHistorico(Integer seqFluxo) {

		StringBuilder hql = new StringBuilder(300);
		hql.append("SELECT")
		.append(" WHE." ).append( AghWFHistoricoExecucao.Fields.DT_REGISTRO.toString() ).append( " as " ).append( ConsultarHistoricoProcessoOpmeVO.Fields.DATA_REGISTRO.toString())
		.append(", WTE." ).append( AghWFTemplateEtapa.Fields.DESCRICAO.toString() ).append( " as " ).append( ConsultarHistoricoProcessoOpmeVO.Fields.DESCRICAO_ETAPA.toString())
		.append(", PES." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( " as " ).append( ConsultarHistoricoProcessoOpmeVO.Fields.NOME_PESSOA_FISICA.toString())
		.append(", WHE." ).append( AghWFHistoricoExecucao.Fields.ACAO.toString() ).append(  " as " ).append( ConsultarHistoricoProcessoOpmeVO.Fields.ACAO.toString())
		.append(", WHE." ).append( AghWFHistoricoExecucao.Fields.OBSERVACAO.toString() ).append( " as " ).append( ConsultarHistoricoProcessoOpmeVO.Fields.OBSERVACAO.toString())
		.append(", WHE." ).append( AghWFHistoricoExecucao.Fields.JUSTIFICATIVA.toString() ).append( " as " ).append( ConsultarHistoricoProcessoOpmeVO.Fields.JUSTIFICATIVA.toString())
		
		.append(" FROM ")
		.append(AghWFHistoricoExecucao.class.getSimpleName() ).append( " WHE, ")
		.append(AghWFTemplateEtapa.class.getSimpleName() ).append( " WTE, ")
		.append(AghWFEtapa.class.getSimpleName() ).append( " WET, ")
		.append(AghWFExecutor.class.getSimpleName() ).append( " WEX, ")
		.append(RapServidores.class.getSimpleName() ).append( " SER, ")
		.append(RapPessoasFisicas.class.getSimpleName() ).append( " PES ")
		

		.append(" WHERE " )
//		agh_wf_hist_execucoes.wet_seq = agh_wf_etapas.seq
		.append(" WHE." ).append( AghWFHistoricoExecucao.Fields.WET_SEQ.toString() ).append( " = WET." ).append( AghWFEtapa.Fields.SEQ.toString())
//	       AND agh_wf_etapas.wte_seq = agh_wf_template_etapas.seq 
		.append(" AND WET." ).append( AghWFEtapa.Fields.TEMPLATE_ETAPA.toString() ).append( " = WTE." ).append( AghWFTemplateEtapa.Fields.SEQ.toString())		
//	       AND agh_wf_hist_execucoes.wex_seq = agh_wf_executores.seq
		.append(" AND WHE." ).append( AghWFHistoricoExecucao.Fields.WEX_SEQ.toString() ).append( " = WEX." ).append( AghWFExecutor.Fields.SEQ.toString())
		///AND agh_wf_executores.wet_seq = agh_wf_etapas.seq		
		.append(" AND WEX." ).append( AghWFExecutor.Fields.WET_SEQ.toString() ).append( " = WET." ).append( AghWFEtapa.Fields.SEQ.toString())
		//AND rap_servidores.matricula = agh_wf_executores.ser_matricula
		.append(" AND SER." ).append( RapServidores.Fields.MATRICULA.toString() ).append( " = WEX." ).append( AghWFExecutor.Fields.SERVIDOR_MATRICULA.toString())
		//AND rap_servidores.vin_codigo = agh_wf_executores.ser_vin_codigo
		.append(" AND SER." ).append( RapServidores.Fields.VIN_CODIGO.toString() ).append( " = WEX." ).append( AghWFExecutor.Fields.SERVIDOR_VIN_CODIGO.toString())
		//AND rap_servidores.pes_codigo = rap_pessoas_fisicas.codigo
		.append(" AND SER." ).append( RapServidores.Fields.PES_CODIGO.toString() ).append( " = PES." ).append( RapPessoasFisicas.Fields.CODIGO.toString());
		

		if(seqFluxo != null){
			hql.append(" AND WEX." + AghWFExecutor.Fields.WFL_SEQ_ID.toString() + " = :seqFluxo");
		}
		//order by agh_wf_hist_execucoes.seq
		hql.append(" ORDER BY WHE." ).append( AghWFHistoricoExecucao.Fields.SEQ.toString());
		
		Query query = createHibernateQuery(hql.toString());
		
		if(seqFluxo != null){
			query.setInteger("seqFluxo", seqFluxo);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(ConsultarHistoricoProcessoOpmeVO.class));
		return query.list();
	}
	
	
	public List<AghWFHistoricoExecucao> buscarHistExecutoresAutorizacao(AghWFEtapa etapa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFHistoricoExecucao.class, "WHE");
		criteria.createAlias("WHE."+AghWFHistoricoExecucao.Fields.WEX_SEQ.toString(), "WEX");
		criteria.createAlias("WEX."+AghWFExecutor.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("WEX."+AghWFExecutor.Fields.WTE_SEQ.toString(), "WTE");
		criteria.add(Restrictions.eq("WHE."+AghWFHistoricoExecucao.Fields.FLUXO_SEQ.toString(), etapa.getFluxo().getSeq()));
		criteria.add(Restrictions.eq("WTE."+AghWFTemplateEtapa.Fields.CODIGO.toString(), etapa.getTemplateEtapa().getCodigo()));

		return executeCriteria(criteria);
	}

}
