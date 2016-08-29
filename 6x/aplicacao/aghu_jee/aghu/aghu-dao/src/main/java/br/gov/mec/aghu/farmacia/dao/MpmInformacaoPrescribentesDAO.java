package br.gov.mec.aghu.farmacia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.farmacia.vo.CodAtendimentoInformacaoPacienteVO;
import br.gov.mec.aghu.farmacia.vo.InformacoesPacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmInformacaoPrescribente;


public class MpmInformacaoPrescribentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmInformacaoPrescribente> {

	private static final long serialVersionUID = 2073246796989194981L;
	
	/**
	 * #5795 C4 UTILIZADO EM ON1
	 * @param seq
	 * @return
	 */
	public InformacoesPacienteAgendamentoPrescribenteVO obterInformacoesPacienteAgendamentoPrescribenteVO(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmInformacaoPrescribente.class, "IFP");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.SEQ.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.IFP_SEQ.toString())
				.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.PME_ATD_SEQ.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.PME_ATD_SEQ.toString())
				.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.PME_SEQ.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.PME_SEQ.toString())
				.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.UNF_SEQ.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.ATENDIMENTO_SEQ.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.ATD_SEQ.toString())
				.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.DESCRICAO.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.DESCRICAO.toString())
				.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.IND_INF_VERIFICADA.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.IND_INF_VERIFICA.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.CODIGO.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.PAC_NOME.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO.toString()), InformacoesPacienteAgendamentoPrescribenteVO.Fields.PRONTUARIO.toString())
				);
		criteria.createAlias("IFP."+MpmInformacaoPrescribente.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD."+AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq("IFP."+MpmInformacaoPrescribente.Fields.SEQ.toString(), seq));
		criteria.setResultTransformer(Transformers.aliasToBean(InformacoesPacienteAgendamentoPrescribenteVO.class));
		
		return (InformacoesPacienteAgendamentoPrescribenteVO) executeCriteriaUniqueResult(criteria);
	}

	@Override
	public MpmInformacaoPrescribente obterPorChavePrimaria(Object pk) {
		MpmInformacaoPrescribente informacaoPrescribente = super.obterPorChavePrimaria(pk);
		return informacaoPrescribente;
	}
	
	public MpmInformacaoPrescribente obterMpmInformacaoPrescribentePorId(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmInformacaoPrescribente.class, "MIP");
		criteria.add(Restrictions.eq("MIP."+MpmInformacaoPrescribente.Fields.SEQ.toString(), seq));
		criteria.createAlias("MIP."+MpmInformacaoPrescribente.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("MIP."+MpmInformacaoPrescribente.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("MIP."+MpmInformacaoPrescribente.Fields.PRESCRICAO_MEDICA.toString(), "PME");
		
		return (MpmInformacaoPrescribente) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #5799
	 * C1 - Consulta para buscar código do atendimento e informações do paciente
	 */
	public CodAtendimentoInformacaoPacienteVO buscarCodigoInformacoesPaciente(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmInformacaoPrescribente.class, "IFP");
		
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.SEQ.toString()), CodAtendimentoInformacaoPacienteVO.Fields.SEQ_IFP.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.PME_ATD_SEQ.toString()), CodAtendimentoInformacaoPacienteVO.Fields.PME_ATD_SEQ.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.PME_SEQ.toString()), CodAtendimentoInformacaoPacienteVO.Fields.PME_SEQ.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.UNF_SEQ.toString()), CodAtendimentoInformacaoPacienteVO.Fields.UNF_SEQ.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.ATENDIMENTO_SEQ.toString()), CodAtendimentoInformacaoPacienteVO.Fields.ATD_SEQ.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.DESCRICAO.toString()), CodAtendimentoInformacaoPacienteVO.Fields.DESCRICAO_IFP.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.IND_INF_VERIFICADA.toString()), CodAtendimentoInformacaoPacienteVO.Fields.IND_INF_VERIFICADA.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.DTHR_INF_VERIFICADA.toString()), CodAtendimentoInformacaoPacienteVO.Fields.DTHR_INF_VERIFICADA.toString())
			.add(Projections.property("PAC."+AipPacientes.Fields.CODIGO.toString()), CodAtendimentoInformacaoPacienteVO.Fields.CODIGO_PAC.toString())
			.add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()), CodAtendimentoInformacaoPacienteVO.Fields.NOME_PAC.toString())
			.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO.toString()), CodAtendimentoInformacaoPacienteVO.Fields.PRONTUARIO.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.SER_MATRICULA.toString()), CodAtendimentoInformacaoPacienteVO.Fields.SER_MATRICULA.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.SER_VIN_CODIGO.toString()), CodAtendimentoInformacaoPacienteVO.Fields.SER_VIN_CODIGO.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.SER_MATRICULA_VERIFICADO.toString()), CodAtendimentoInformacaoPacienteVO.Fields.SER_MATRICULA_VERIF.toString())
			.add(Projections.property("IFP."+MpmInformacaoPrescribente.Fields.SER_VIN_CODIGO_VERIFICADO.toString()), CodAtendimentoInformacaoPacienteVO.Fields.SER_VIN_CODIGO_VERIF.toString())
			);
		
		criteria.createAlias("IFP."+MpmInformacaoPrescribente.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("ATD."+AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("IFP."+MpmInformacaoPrescribente.Fields.SEQ.toString(), seq));
		criteria.setResultTransformer(Transformers.aliasToBean(CodAtendimentoInformacaoPacienteVO.class));
		
		return (CodAtendimentoInformacaoPacienteVO) executeCriteriaUniqueResult(criteria);
	}
}
