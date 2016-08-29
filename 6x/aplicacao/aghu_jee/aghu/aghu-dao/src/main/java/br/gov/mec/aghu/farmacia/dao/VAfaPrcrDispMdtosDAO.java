package br.gov.mec.aghu.farmacia.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAfaPrcrDispMdtos;

public class VAfaPrcrDispMdtosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAfaPrcrDispMdtos> {
	
	private static final long serialVersionUID = -7532881441203781493L;

	private DetachedCriteria getCriteriaPesquisarPacientesParaDispensacao(Date dataReferencia, AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes paciente, Date dataInicial, Date dataFinal) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VAfaPrcrDispMdtos.class, "apd");
		
//		criteria.createAlias("atendimento"/*VAfaPrcrDispMdtos.Fields.ATENDIMENTO.toString()*/, "atendimento", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("atendimentoLocal"/*VAfaPrcrDispMdtos.Fields.ATENDIMENTO_LOCAL.toString()*/, "atendimentoLocal", JoinType.LEFT_OUTER_JOIN);
		
//		criteria.createAlias("atendimento."+AghAtendimentos.Fields.PACIENTE.toString(), "PACIENTE", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.gt(VAfaPrcrDispMdtos.Fields.DT_REFERENCIA.toString(), dataInicial));
		criteria.add(Restrictions.le(VAfaPrcrDispMdtos.Fields.DT_REFERENCIA.toString(), dataFinal));
		
		if(paciente!=null) {
			criteria.add(Subqueries.propertyIn(VAfaPrcrDispMdtos.Fields.ATD_SEQ.toString(), getCriteriaPaciente(paciente)));
		}
		
		criteria.add(Restrictions.or(
				Restrictions.and(
						Subqueries.propertyIn(VAfaPrcrDispMdtos.Fields.ATD_SEQ.toString(), getCriteriaDispMdto(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ, 		farmacia, false)), 
						Subqueries.propertyIn(VAfaPrcrDispMdtos.Fields.SEQ.toString(), 	 getCriteriaDispMdto(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ, 	farmacia, false)))
				,
				Restrictions.and(
					Subqueries.propertyIn(VAfaPrcrDispMdtos.Fields.ATD_SEQ.toString(), getCriteriaDispMdto(AfaDispensacaoMdtos.Fields.IMO_PMO_PTE_ATD_SEQ, 	farmacia, true)), 
					Subqueries.propertyIn(VAfaPrcrDispMdtos.Fields.SEQ.toString(), 	 getCriteriaDispMdto(AfaDispensacaoMdtos.Fields.IMO_PMO_PTE_SEQ, 		farmacia, true)))
					));
		
		criteria.add(Subqueries.propertyIn(VAfaPrcrDispMdtos.Fields.ATD_SEQ_LOCAL.toString(), getCriteriaLeitoEQuarto(leito, quarto, unidadeFuncionalPrescricao, paciente)));
		
		return criteria;
	}
	
	public List<VAfaPrcrDispMdtos> pesquisarPacientesParaDispensacao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Date dataReferencia, AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes paciente, Date dataInicial, Date dataFinal) {
		
		DetachedCriteria criteria = getCriteriaPesquisarPacientesParaDispensacao(
				dataReferencia, farmacia, unidadeFuncionalPrescricao, leito,
				quarto, paciente, dataInicial, dataFinal);
		
		return executeCriteria(criteria); //, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarPacientesParaDispensacao(Date dataReferencia, AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes paciente, Date dataInicial, Date dataFinal) {
		return executeCriteriaCount(getCriteriaPesquisarPacientesParaDispensacao(
				dataReferencia, farmacia, unidadeFuncionalPrescricao, leito,
				quarto, paciente, dataInicial, dataFinal));
	}
	
	private DetachedCriteria getCriteriaPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "att");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		criteria.setProjection(p);
		
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), paciente.getProntuario()));
		return criteria;
	}

	private DetachedCriteria getCriteriaLeitoEQuarto(AinLeitos leito,
			AinQuartos quarto, AghUnidadesFuncionais unidadeFuncionalPrescricao, AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "att");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		criteria.setProjection(p);
		
		String atdSeqLocalMain = "apd."+VAfaPrcrDispMdtos.Fields.ATD_SEQ_LOCAL.toString();
		criteria.add(Restrictions.eqProperty(AghAtendimentos.Fields.SEQ.toString(), atdSeqLocalMain));
		if(unidadeFuncionalPrescricao !=null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.UNF_SEQ.toString(), unidadeFuncionalPrescricao.getSeq()));
		}
		if(paciente!=null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.PRONTUARIO.toString(), paciente.getProntuario()));
		}
		if(leito !=null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.LEITO.toString(), leito));
		}
		if(quarto !=null){
			criteria.add(Restrictions.eq(AghAtendimentos.Fields.QUARTO.toString(), quarto));
		}
		return criteria;
	}

	private DetachedCriteria getCriteriaDispMdto(AfaDispensacaoMdtos.Fields propertyAfaDispensacao, AghUnidadesFuncionais farmacia, Boolean comImo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(propertyAfaDispensacao.toString()));
		criteria.setProjection(p);
		
		String atdSeqMain = "apd." + VAfaPrcrDispMdtos.Fields.ATD_SEQ.toString(); //apd.id.atdSeq
		String pmeSeqMain = "apd." + VAfaPrcrDispMdtos.Fields.SEQ.toString(); //apd.id.seq
		
		if(!comImo){
			//where dsm1.pme_atd_seq = V_AFA_PRCR_DISP.ATD_SEQ
			criteria.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString(), atdSeqMain));

			//and dsm1.pme_seq = V_AFA_PRCR_DISP.SEQ
			criteria.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString(), pmeSeqMain));
		}else{
			//where dsm1.IMO_PMO_PTE_ATD_SEQ = V_AFA_PRCR_DISP.ATD_SEQ
			criteria.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.IMO_PMO_PTE_ATD_SEQ.toString(), atdSeqMain));
			
			//and dsm1.IMO_PMO_PTE_SEQ = V_AFA_PRCR_DISP.SEQ
			criteria.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.IMO_PMO_PTE_SEQ.toString(), pmeSeqMain));
		}
		
		//and dsm1.unf_seq = nvl(:qms$ctrl.vuf_seq, dsm1.unf_seq)
		if(farmacia!=null){
			criteria.add(Restrictions.eq(AfaDispensacaoMdtos.Fields.FARMACIA_SEQ.toString(), farmacia.getSeq()));
		}
		//and upper(dsm1.ind_situacao) in ('S','D','T','C')
		criteria.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), getSituacaoMedicamento()));
		return criteria;
	}

	private List<DominioSituacaoDispensacaoMdto> getSituacaoMedicamento() {
		List<DominioSituacaoDispensacaoMdto> listSituacoes = new ArrayList<DominioSituacaoDispensacaoMdto>();
		listSituacoes.add(DominioSituacaoDispensacaoMdto.S);
		listSituacoes.add(DominioSituacaoDispensacaoMdto.D);
		listSituacoes.add(DominioSituacaoDispensacaoMdto.T);
		listSituacoes.add(DominioSituacaoDispensacaoMdto.C);
		return listSituacoes;
	}
}