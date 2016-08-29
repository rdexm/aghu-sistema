package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.MpmAltaDiagPrincipal;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author bsoliveira
 *
 */
public class MpmAltaDiagPrincipalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaDiagPrincipal> {
    
	private static final long serialVersionUID = 3421405683347286572L;


	@Override
	protected void obterValorSequencialId(MpmAltaDiagPrincipal elemento) {
		elemento.setId(elemento.getAltaSumario().getId());
	}
	
	public MpmAltaDiagPrincipal obterAltaDiagPrincipal(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagPrincipal.class);
		criteria.createAlias(MpmAltaDiagPrincipal.Fields.CID.toString(), "CID");
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.SEQP.toString(), altanAsuSeqp));
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (MpmAltaDiagPrincipal) executeCriteriaUniqueResult(criteria);
	}
	
	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagPrincipal.class, "ADP");
		criteria.createAlias(MpmAltaDiagPrincipal.Fields.CID_ATENDIMENTO.toString(), "CIA");
		criteria.createAlias(MpmAltaDiagPrincipal.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.APA_SEQ.toString(), seq));

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("CIA." + MpmCidAtendimento.Fields.SEQ.toString()), "ciaSeq");
		pList.add(Projections.property("ADP." + MpmAltaDiagPrincipal.Fields.CID.toString()), "cid");
		pList.add(Projections.property("ADP." + MpmAltaDiagPrincipal.Fields.COMPLEMENTO_CID.toString()), "complemento");
		criteria.setProjection(pList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(SumarioAltaDiagnosticosCidVO.class));
		
		return executeCriteria(criteria);
	}
	
	public String obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagPrincipal.class);
		
		criteria.setProjection(Projections.sqlProjection(
				"coalesce(DESC_CID, '')||' '||coalesce(COMPL_CID,'')" +
				" as descricao",
				new String[]{"descricao"},
				new Type[]{StringType.INSTANCE}));
				
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.SEQP.toString(), asuSeqp));
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.IND_SITUACAO.toString(), DominioSituacao.A));		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Método que verifica a validação
	 * do diagnóstico principal da alta 
	 * do paciente. Deve pelo menos ter 
	 * um registro ativo associado ao sumário 
	 * do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public List<Long> listAltaDiagPrincipal(MpmAltaSumarioId altaSumarioId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagPrincipal.class);
		
		criteria.setProjection(Projections.rowCount())
		.add(Restrictions.idEq(altaSumarioId));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * @param pCthSeq
	 * @return
	 */
	public Integer buscarCidAlta(Integer pCthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagPrincipal.class);
		
		criteria.createAlias(MpmAltaDiagPrincipal.Fields.ALTA_SUMARIO.toString(), "ASU");
		criteria.add(Restrictions.eq("ASU." + MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));		
		criteria.createAlias("ASU." + MpmAltaSumario.Fields.ATENDIMENTO.toString(), "ATD");		
		criteria.createAlias("ATD." + AghAtendimentos.Fields.INTERNACAO.toString(), "INT");
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatContasInternacao.class);
		subCriteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "CTH");
		subCriteria.add(Restrictions.eq("CTH." + FatContasHospitalares.Fields.SEQ.toString(), pCthSeq));
		subCriteria.createAlias(FatContasInternacao.Fields.INTERNACAO.toString(), "INT2");
		subCriteria.setProjection(Projections.property("INT2." + AinInternacao.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.propertyIn("INT." + AinInternacao.Fields.SEQ.toString(), subCriteria));
		
		criteria.createAlias(MpmAltaDiagPrincipal.Fields.CID.toString(), "CID");
		criteria.setProjection(Projections.property("CID." + AghCid.Fields.SEQ.toString()));
		
		List<Integer> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * {@link IPrescricaoMedicaFacade#buscaAltaPrincipalPorAtendimento(AghAtendimentos)}
	 */
	public MpmAltaDiagPrincipal buscaAltaPrincipalPorAtendimento(AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaDiagPrincipal.class, "adp");
		criteria.createCriteria("adp." + MpmAltaDiagPrincipal.Fields.ALTA_SUMARIO.toString(), "asu", JoinType.INNER_JOIN);
		criteria.createCriteria("adp." + MpmAltaDiagPrincipal.Fields.CID.toString(), "cid", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("asu." + MpmAltaSumario.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.eq("asu." + MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.eq(MpmAltaDiagPrincipal.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		List<MpmAltaDiagPrincipal> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
}
