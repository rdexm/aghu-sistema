package br.gov.mec.aghu.ambulatorio.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AacAtendimentoApacsHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatAtendimentoApacProcHosp;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProcedimentosAPACVO;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AacAtendimentoApacsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacAtendimentoApacs> {

	private static final long serialVersionUID = -5064766516720684038L;
	private static final String ATM = "ATM.";
	private static final String ATD = "ATD.";

	public List<AacAtendimentoApacs> listarAtendimentoApacPorPtrPacCodigo(Integer ptrPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacAtendimentoApacs.class);

		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_PAC_CODIGO.toString(), ptrPacCodigo));

		return executeCriteria(criteria);
	}

	public List<AacAtendimentoApacs> listarAtendimentosApacsPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacAtendimentoApacs.class);

		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	
	/**
	 * #42011
	 * @param ptrPacCodigo
	 * @param ptrTtrCodigo
	 * @return
	 */
	public String buscarApac (Integer ptrPacCodigo, String ptrTtrCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacAtendimentoApacs.class);
		
		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_PAC_CODIGO.toString(), ptrPacCodigo));
		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_TTR_CODIGO.toString(), ptrTtrCodigo));
		criteria.add(Restrictions.isNull(AacAtendimentoApacs.Fields.DT_FIM.toString()));
		
		if (executeCriteriaExists(criteria)) {
			return "S";
		} else {
			return "N";
		}
	}

	public Long contaApacs(Integer pacienteCodigo, String ttrCodigo, Date dateTransplante) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacAtendimentoApacs.class);
		
		Calendar calendar = Calendar.getInstance();  
	    calendar.setTime(dateTransplante);  
	    calendar.add(Calendar.DATE, 365);  
				
		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_PAC_CODIGO.toString(), pacienteCodigo));
		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_TTR_CODIGO.toString(), ttrCodigo));
		criteria.add(Restrictions.between(AacAtendimentoApacs.Fields.DT_INICIO.toString(), dateTransplante, calendar.getTime()));
		
		
		return executeCriteriaCount(criteria);
	}
	
	//CONSULTA DO SCHEM HIST
	public Long contaApacsHist(Integer pacienteCodigo, String ttrCodigo, Date dateTransplante) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacAtendimentoApacsHist.class);

		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_PAC_CODIGO.toString(), pacienteCodigo));
		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_TTR_CODIGO.toString(), ttrCodigo));
		criteria.add(Restrictions.between(AacAtendimentoApacs.Fields.DT_INICIO.toString(), dateTransplante, DateUtil.adicionaDias(dateTransplante, 365)));
		
		
		return executeCriteriaCount(criteria);
	}

	public Byte obterAtendimentoApacCornea(Integer pacCodigo, Date dtConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacAtendimentoApacs.class);
		criteria.createAlias(AacAtendimentoApacs.Fields.FAT_CONTA_APAC.toString(), "C");
		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_TTR_CODIGO.toString(), "CORNEA"));
		criteria.add(Restrictions.sqlRestriction("'"+ new SimpleDateFormat("dd/MM/YYYY").format(dtConsulta) + "' between dt_inicio_validade and c1_.dt_fim_validade"));

		criteria.setProjection(Projections.property("C." + FatContaApac.Fields.SEQP.toString()));
		
		return (Byte) executeCriteriaUniqueResult(criteria);
	}
	

	/**
	 * #41736
	 * C3 - Consulta procedimentos da APAC. 
	 */
	public List<ProcedimentosAPACVO> obterProcedimentosApac(Long numeroApac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacAtendimentoApacs.class, "ATM");
			
		criteria.createAlias(ATM + AacAtendimentoApacs.Fields.FAT_ATEND_APAC_PROC_HOSP.toString(), "AAP", JoinType.INNER_JOIN);
		criteria.createAlias("AAP." + FatAtendimentoApacProcHosp.Fields.PROCEDIEMNTO_HOSP_INT.toString(), "PHI", JoinType.INNER_JOIN);
			
		StringBuilder projView = new StringBuilder(300);
		projView.append("(select distinct VAPR.COD_TABELA from V_FAT_ASSOCIACAO_PROCEDIMENTOS VAPR where (VAPR.PHI_SEQ = PHI2_.SEQ) AND (VAPR.CPG_GRC_SEQ = THIS_.GRC_SEQ)) As codTabela  ");
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(ATM + AacAtendimentoApacs.Fields.NUMERO.toString()), ProcedimentosAPACVO.Fields.NUMERO.toString())
				.add(Projections.property("AAP." + FatAtendimentoApacProcHosp.Fields.IND_PRIORIDADE.toString()),ProcedimentosAPACVO.Fields.IND_PRIORIDADE.toString())
				.add(Projections.sqlProjection(projView.toString(), new String [] {ProcedimentosAPACVO.Fields.COD_TABELA.toString()}, new Type[] {new StringType()}))));
		
		criteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.NUMERO.toString(), numeroApac));
		criteria.addOrder(Order.asc(("AAP." + FatAtendimentoApacProcHosp.Fields.IND_PRIORIDADE.toString())));
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentosAPACVO.class));
		return executeCriteria(criteria);
	}
	
public List<AacAtendimentoApacs> obterDataInicioAtendimentoExistente(Integer pacCodigo, Short espSeq, DominioTipoTratamentoAtendimento indTipoTratamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacAtendimentoApacs.class, "ATM");
		criteria.createAlias(ATM + AacAtendimentoApacs.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(ATD + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(ATD + AghAtendimentos.Fields.IND_TIPO_TRATAMENTO.toString(), indTipoTratamento));
		criteria.add(Restrictions.eq(ATD + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(ATD + AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.A));
		criteria.add(Restrictions.isNull(ATD + AghAtendimentos.Fields.DTHR_FIM.toString()));
		
		criteria.addOrder(Order.asc(ATM + AacAtendimentoApacs.Fields.DT_INICIO.toString()))
			.addOrder(Order.desc(ATD + AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
		
		return executeCriteria(criteria);
	}
}
