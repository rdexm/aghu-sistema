package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.dominio.DominioIndSituacaoLaudoAih;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MamLaudoAih;

public class MamLaudoAihDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamLaudoAih> {

	private static final long serialVersionUID = -4463627355805353455L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(MamLaudoAih.class);
    }
	
	public List<MamLaudoAih> obterPorTrgSeq(long trgSeq) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(MamLaudoAih.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq(MamLaudoAih.Fields.IND_PENDENTE.toString(), DominioIndPendenteLaudoAih.V));
		criteria.add(Restrictions.isNull(MamLaudoAih.Fields.DTHR_VALIDA_MVTO.toString()));

		return executeCriteria(criteria);
	}
	
	public List<MamLaudoAih> obterLaudosDoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLaudoAih.class, "LAI");
		criteria.add(Restrictions.eq("LAI.".concat(MamLaudoAih.Fields.PAC_CODIGO.toString()), pacCodigo));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria listarLaudosAIH(AipPacientes paciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLaudoAih.class, "LAI");
	
		criteria.createAlias("LAI.".concat(MamLaudoAih.Fields.ESPECIALIDADE.toString()), "ESP");
		criteria.createAlias("LAI.".concat(MamLaudoAih.Fields.MAM_LAUDO_AIHS.toString()), "MLA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LAI.".concat(MamLaudoAih.Fields.SERVIDOR_RESP_INTERNACAO.toString()), "RAP");
		criteria.add(Restrictions.eq("LAI.".concat(MamLaudoAih.Fields.PACIENTE.toString()), paciente));
		criteria.add(Restrictions.eq("LAI.".concat(MamLaudoAih.Fields.IND_PENDENTE.toString()), DominioIndPendenteLaudoAih.V));			

		return criteria;

	}
	
	public List<MamLaudoAih> listarLaudosAIH(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,	AipPacientes paciente) {
		final DetachedCriteria criteria = this.listarLaudosAIH(paciente);
		criteria.addOrder(Order.desc(MamLaudoAih.Fields.DTHR_CRIACAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarLaudosAIHCount(AipPacientes paciente) {
		return executeCriteriaCount(this.listarLaudosAIH(paciente));
	}
	
	public List<VFatSsmInternacaoVO> pesquisarInternacoesPacienteByLaudo(Long seqMamLaudoAih, Short paramTabelaFaturPadrao){
		
		List<VFatSsmInternacaoVO> result = null;
		StringBuilder hql = new StringBuilder(350);

		hql.append("select distinct lai." ).append( MamLaudoAih.Fields.DTHR_CRIACAO.toString()	).append(" as " ).append( VFatSsmInternacaoVO.Fields.DTHR_CRIACAO.toString() ).append( " , \n")
		.append("lai." ).append( MamLaudoAih.Fields.SINAIS_SINTOMAS.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.SINAIS_SINTOMAS.toString() ).append( " , \n")
		.append("lai." ).append( MamLaudoAih.Fields.CONDICOES.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.CONDICOES.toString() ).append( " , \n")
		.append("lai." ).append( MamLaudoAih.Fields.RESULTADOS_PROVAS.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.RESULTADOS_PROVAS.toString() ).append( " , \n")
		.append("cid." ).append( AghCid.Fields.CODIGO.toString()  ).append(" as " ).append( VFatSsmInternacaoVO.Fields.CID_CODIGO.toString() ).append( " , \n")
		.append("cid." ).append( AghCid.Fields.DESCRICAO.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.CID_DESCRICAO.toString() ).append( " , \n")
		.append("cis." ).append( AghCid.Fields.CODIGO.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.CID_CODIGO_SEC.toString() ).append( " , \n")
		.append("iph." ).append( FatItensProcedHospitalar.Fields.COD_TABELA.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.COD_TABELA.toString() ).append( " , \n")
		.append("iph." ).append( FatItensProcedHospitalar.Fields.DESCRICAO.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.DESCRICAO_ITEM_PROCEDIMENTO.toString() ).append( " , \n")
		.append("lai." ).append( MamLaudoAih.Fields.DESCRICAO_PROCEDIMENTO.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.DESCRICAO_PROCEDIMENTO.toString() ).append( " , \n")
		.append("lai." ).append( MamLaudoAih.Fields.PRIORIDADE.toString() ).append(" as " ).append( VFatSsmInternacaoVO.Fields.PRIORIDADE.toString() ).append( "  \n")
		
		.append("from " ).append( MamLaudoAih.class.getSimpleName()).append( " lai ")
		
		.append("left join lai." ).append( MamLaudoAih.Fields.AGH_CID.toString() ).append( " cid ")
		.append("left join lai." ).append( MamLaudoAih.Fields.AGH_CID_SECUNDARIO.toString() ).append( " cis ")
		.append("left join lai.fatItemProcedHospital iph ")
		.append("with iph.").append( FatItensProcedHospitalar.Fields.PHO_SEQ.toString() ).append( " = :paramTabelaFaturPadrao ")
		.append("and iph.").append( FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString() ).append( " = :indInternacao ")
		.append("left join iph." ).append( FatItensProcedHospitalar.Fields.FAT_VLR_ITEM_PROCED_HOSP_COMPS.toString() ).append( " ipc ")
		.append(" where lai." ).append( MamLaudoAih.Fields.SEQ.toString() ).append( " = :seq ");
		
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setShort("paramTabelaFaturPadrao", paramTabelaFaturPadrao);
		query.setBoolean("indInternacao", Boolean.TRUE);
		query.setLong("seq", seqMamLaudoAih);
		
		query.setResultTransformer(Transformers.aliasToBean(VFatSsmInternacaoVO.class));
		result = query.list();
		
		return result;
	}
	
	/**
	 * Obter laudos aih de uma determinada consulta
	 * 
	 * Web Service #38473
	 * 
	 * @param conNumero
	 * @param pacCodigo
	 * @return
	 */
	public List<MamLaudoAih> pesquisarLaudosAihPorConsultaPaciente(final Integer conNumero, final Integer pacCodigo) {
		DetachedCriteria criteria = obterCriteria();

		criteria.add(Restrictions.eq(MamLaudoAih.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(MamLaudoAih.Fields.PAC_CODIGO.toString(), pacCodigo));

		criteria.add(Restrictions.isNull(MamLaudoAih.Fields.MAM_LAUDO_AIHS.toString()));

		criteria.addOrder(Order.desc(MamLaudoAih.Fields.DTHR_CRIACAO.toString()));

		return executeCriteria(criteria);
	}
	
	public List<MamLaudoAih> obterLaudoPorRgtSeqLaiSeqIndPendente(long rgtSeq, Long seq) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(MamLaudoAih.Fields.RGT_SEQ.toString(), rgtSeq));
		criteria.add(Restrictions.in(MamLaudoAih.Fields.IND_PENDENTE.toString(),
				new DominioIndPendenteLaudoAih[]{DominioIndPendenteLaudoAih.R, DominioIndPendenteLaudoAih.P, DominioIndPendenteLaudoAih.E}));
		if (seq != null) {
			criteria.add(Restrictions.eq(MamLaudoAih.Fields.SEQ.toString(), seq));
		}
		return executeCriteria(criteria);
	}
	
	public MamLaudoAih obterLaudoPorTrgSeqDominioSituacaoPacCodigo(Long trgSeq, Integer pacCodigo) {
		DetachedCriteria criteria = obterCriteria();
		
		if (trgSeq != null) {
			criteria.add(Restrictions.eq(MamLaudoAih.Fields.TRG_SEQ.toString(), trgSeq));
		}
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(MamLaudoAih.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		criteria.add(Restrictions.eq(MamLaudoAih.Fields.IND_PENDENTE.toString(), DominioIndPendenteLaudoAih.V));
		criteria.add(Restrictions.not(Restrictions.in(MamLaudoAih.Fields.IND_SITUACAO.toString(),
				new DominioIndSituacaoLaudoAih[]{DominioIndSituacaoLaudoAih.J, DominioIndSituacaoLaudoAih.C, DominioIndSituacaoLaudoAih.U})));
		
		//criteria.getExecutableCriteria(getSession()).setFirstResult(0);
		//criteria.getExecutableCriteria(getSession()).setMaxResults(1);
		
		List<MamLaudoAih> lista = executeCriteria(criteria, 0, 1, null, false);
		return lista.isEmpty() ? null : lista.get(0);
	}
	
	public List<MamLaudoAih> obterLaudoPorSeqEPaciente(Long seq, Integer pacCodigo) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(MamLaudoAih.Fields.SEQ.toString(), seq));
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(MamLaudoAih.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamLaudoAih> verificarLaudoAihParaImpressao(Integer nroConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLaudoAih.class);		
		criteria.add(Restrictions.eq(MamLaudoAih.Fields.CON_NUMERO.toString(), nroConsulta));
		criteria.add(Restrictions.isNull(MamLaudoAih.Fields.DTHR_VALIDA_MVTO.toString()));		
		criteria.add(Restrictions.or(Restrictions.eq(MamLaudoAih.Fields.IND_PENDENTE.toString(), DominioIndPendenteLaudoAih.V),Restrictions.eq(MamLaudoAih.Fields.IND_PENDENTE.toString(), DominioIndPendenteLaudoAih.P)));
		return executeCriteria(criteria);			
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamLaudoAih buscarLaudoAihPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamLaudoAih.class);

		criteria.add(Restrictions.eq(MamLaudoAih.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamLaudoAih> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
