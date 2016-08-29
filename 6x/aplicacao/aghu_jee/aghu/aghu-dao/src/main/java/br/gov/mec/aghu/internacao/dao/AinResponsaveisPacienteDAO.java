package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioTipoResponsabilidade;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;

public class AinResponsaveisPacienteDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AinResponsaveisPaciente> {

	private static final long serialVersionUID = 4701505342208176544L;

	public AinResponsaveisPaciente buscaResponsaveisPaciente(
			Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinResponsaveisPaciente.class);

		criteria.createAlias(AinResponsaveisPaciente.Fields.INTERNACAO
				.toString(), AinResponsaveisPaciente.Fields.INTERNACAO
				.toString());

		criteria.add(Restrictions
				.in(AinResponsaveisPaciente.Fields.TIPO_RESPONSABILIDADE
						.toString(), new DominioTipoResponsabilidade[] {
						DominioTipoResponsabilidade.C,
						DominioTipoResponsabilidade.CT }));

		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.INTERNACAO
				.toString() + "." + AinInternacao.Fields.SEQ.toString(), intSeq));

		List<AinResponsaveisPaciente> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public Integer obterContaConvenioAnterior(Integer seqResp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinResponsaveisPaciente.class);
		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.SEQ.toString(), seqResp));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AinResponsaveisPaciente.Fields.NRO_CONTA.toString())));
		Integer nroConta = (Integer) this.executeCriteriaUniqueResult(criteria);
		return nroConta;
	}
	
	public Integer pesquisarQtdResponsaveisInternacao(Integer intSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AinResponsaveisPaciente.class);
		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.INTERNACAO_SEQ.toString(), intSeq));
		List<AinResponsaveisPaciente> lista = this.executeCriteria(criteria);
		
		return lista.size();
	}

  	/**
     * Método que obtém o número de responsáveis por um paciente para uma
     * certa internação
     * 
     * @param intSeq
     * @return listaResponsaveisPaciente
     */
	public List<AinResponsaveisPaciente> pesquisarResponsaveisPaciente(Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinResponsaveisPaciente.class, "RESPP");

		criteria.createAlias("RESPP." + AinResponsaveisPaciente.Fields.RESPONSAVEL_CONTA.toString(), "RC" ,JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.INTERNACAO_SEQ.toString(), intSeq));

		List<AinResponsaveisPaciente> listaResponsaveisPaciente = executeCriteria(criteria);
		return listaResponsaveisPaciente;
	}

  	/**
     * Método que obtém o responsável do paciente contratante 
     * 
     * @param intSeq
     * @return AinResponsaveisPaciente
     */
 	public AinResponsaveisPaciente obterResponsaveisPacienteTipoConta(Integer intSeq){
  		DetachedCriteria criteria = DetachedCriteria.forClass(AinResponsaveisPaciente.class);
  		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.INTERNACAO_SEQ.toString(), intSeq));
  		
  		//Estava buscando somente tipo Conta, mas segundo a Milena deveria aparecer Tipo Conta ou Tipo Contato e Conta #17284
//		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.TIPO_RESPONSABILIDADE.toString(), DominioTipoResponsabilidade.C));
  		criteria.add(Restrictions.or(Restrictions.eq(AinResponsaveisPaciente.Fields.TIPO_RESPONSABILIDADE.toString(), DominioTipoResponsabilidade.C), 
  				Restrictions.eq(AinResponsaveisPaciente.Fields.TIPO_RESPONSABILIDADE.toString(), DominioTipoResponsabilidade.CT)));
  		
  		AinResponsaveisPaciente responsavelContratante = null;
  		List<AinResponsaveisPaciente> listaResp = executeCriteria(criteria);
  		if (listaResp != null && !listaResp.isEmpty()) {
  			responsavelContratante = listaResp.get(0);
  		}
  		return responsavelContratante;
  	}

	public List<Object> obterResponsavelPaciente(Integer seqInternacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinResponsaveisPaciente.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AinResponsaveisPaciente.Fields.NOME.toString()), "nome")
				.add(Projections.property(AinResponsaveisPaciente.Fields.LOGRADOURO.toString()), "logradouro")
				.add(Projections.property(AinResponsaveisPaciente.Fields.CIDADE.toString()), "cidade")
				.add(Projections.property(AinResponsaveisPaciente.Fields.UF.toString()), "uf")
				.add(Projections.property(AinResponsaveisPaciente.Fields.CEP.toString()), "cep")
				.add(Projections.property(AinResponsaveisPaciente.Fields.FONE.toString()), "fone"));

		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.INTERNACAO_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.TIPO_RESPONSABILIDADE.toString(), DominioTipoResponsabilidade.C));

		return this.executeCriteria(criteria);
	}
	
	public AinResponsaveisPaciente obterResponsaveisPacientePorNome(String nome) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinResponsaveisPaciente.class, "RESPP");
		criteria.add(Restrictions.eq(AinResponsaveisPaciente.Fields.NOME.toString(), nome));
		criteria.add(Restrictions.or(Restrictions.eq(AinResponsaveisPaciente.Fields.TIPO_RESPONSABILIDADE.toString(), DominioTipoResponsabilidade.C), 
				Restrictions.eq(AinResponsaveisPaciente.Fields.TIPO_RESPONSABILIDADE.toString(), DominioTipoResponsabilidade.CT)));
		
		criteria.createAlias("RESPP." + AinResponsaveisPaciente.Fields.RESPONSAVEL_CONTA.toString(), "RC" ,JoinType.LEFT_OUTER_JOIN);
		
		return (AinResponsaveisPaciente) executeCriteriaUniqueResult(criteria);
		
	}
}
