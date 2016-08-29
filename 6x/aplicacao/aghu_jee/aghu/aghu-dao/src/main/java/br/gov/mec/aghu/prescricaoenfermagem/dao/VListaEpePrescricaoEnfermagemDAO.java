package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmListaPacCpa;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.MpmListaServEspecialidade;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmServidorUnidFuncional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.view.VListaEpePrescricaoEnfermagem;

public class VListaEpePrescricaoEnfermagemDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VListaEpePrescricaoEnfermagem> implements java.io.Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2407262565226304888L;

	/*
	 * public List<AghAtendimentos> listarAtendimentosEnfermagem(final RapServidores servidor) { if (servidor == null) { throw new IllegalArgumentException("Parâmetro obrigatório"); }
	 * 
	 * final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atendimentos"); criteria.createAlias(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional");
	 * criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "paciente");
	 * 
	 * criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S)); criteria.add(Restrictions.or( Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(),
	 * DominioOrigemAtendimento.I), Restrictions.or( Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.H), Restrictions.or(Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(),
	 * DominioOrigemAtendimento.U), Restrictions.eq(AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.N)))));
	 * 
	 * criteria.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
	 * 
	 * final DetachedCriteria criteriaPacAtendProfissional = DetachedCriteria.forClass(MpmPacAtendProfissional.class, "atendProfissional");
	 * criteriaPacAtendProfissional.setProjection(Projections.property(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString()));
	 * criteriaPacAtendProfissional.add(Restrictions.eq(MpmPacAtendProfissional.Fields.SERVIDOR.toString(), servidor));
	 * criteriaPacAtendProfissional.add(Restrictions.eqProperty(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString(), "atendimentos." + AghAtendimentos.Fields.SEQUENCE.toString()));
	 * 
	 * final DetachedCriteria criteriaServidorUnidadeFuncional = DetachedCriteria.forClass(MpmServidorUnidFuncional.class, "servidorUnidadeFuncional");
	 * criteriaServidorUnidadeFuncional.setProjection(Projections.property(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
	 * criteriaServidorUnidadeFuncional.add(Restrictions.eq(MpmServidorUnidFuncional.Fields.SERVIDOR.toString(), servidor));
	 * criteriaServidorUnidadeFuncional.add(Restrictions.eqProperty(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), "atendimentos." +
	 * AghAtendimentos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));
	 * 
	 * final DetachedCriteria criteriaServidorEspecialidades = DetachedCriteria.forClass(MpmListaServEspecialidade.class, "servidorEspecialidade");
	 * criteriaServidorEspecialidades.setProjection(Projections.property(MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString() + "." + AghEspecialidades.Fields.SEQ.toString()));
	 * criteriaServidorEspecialidades.add(Restrictions.eq(MpmListaServEspecialidade.Fields.SERVIDOR.toString(), servidor)); criteriaServidorEspecialidades.add(Restrictions.or( Restrictions.and(
	 * Restrictions.isNotNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()), Restrictions.leProperty(MpmListaServEspecialidade.Fields.CRIADO_EM.toString(), "atendimentos." +
	 * AghAtendimentos.Fields.DTHR_FIM.toString())), Restrictions.and(Restrictions.isNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()),
	 * Restrictions.le(MpmListaServEspecialidade.Fields.CRIADO_EM.toString(), new Date())))); criteriaServidorEspecialidades.add(Restrictions.eqProperty(MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString() + "." +
	 * AghEspecialidades.Fields.SEQ.toString(), "atendimentos." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()));
	 * 
	 * final DetachedCriteria criteriaServidorEquipes = DetachedCriteria.forClass(MpmListaServEquipe.class, "servidorEquipe"); criteriaServidorEquipes.setProjection(Projections.projectionList()
	 * .add(Projections.property(MpmListaServEquipe.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.CODIGO_VINCULO.toString())) .add(Projections.property(MpmListaServEquipe.Fields.SERVIDOR.toString() + "." +
	 * RapServidores.Fields.MATRICULA.toString()))); criteriaServidorEquipes.createAlias(MpmListaServEquipe.Fields.SERVIDOR.toString(), "servidor");
	 * criteriaServidorEquipes.add(Restrictions.eq(MpmListaServEquipe.Fields.SERVIDOR.toString(), servidor)); criteriaServidorEquipes.add(Restrictions.or( Restrictions.and( Restrictions.isNotNull("atendimentos." +
	 * AghAtendimentos.Fields.DTHR_FIM.toString()), Restrictions.leProperty(MpmListaServEquipe.Fields.CRIADO_EM.toString(), "atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString())),
	 * Restrictions.and(Restrictions.isNull("atendimentos." + AghAtendimentos.Fields.DTHR_FIM.toString()), Restrictions.le(MpmListaServEquipe.Fields.CRIADO_EM.toString(), new Date()))));
	 * criteriaServidorEquipes.add(Restrictions.eqProperty(MpmListaServEquipe.Fields.SERVIDOR.toString(), "atendimentos." + AghAtendimentos.Fields.SERVIDOR.toString()));
	 * 
	 * final DetachedCriteria criteriaPacCpas = DetachedCriteria.forClass(MpmListaPacCpa.class, "pacCpa"); criteriaPacCpas.setProjection(Projections.property(MpmListaPacCpa.Fields.IND_PAC_CPA.toString()));
	 * criteriaPacCpas.add(Restrictions.eq(MpmListaPacCpa.Fields.SERVIDOR.toString(), servidor)); criteriaPacCpas.add(Restrictions.eq(MpmListaPacCpa.Fields.IND_PAC_CPA.toString(), true));
	 * criteriaPacCpas.add(Restrictions.eq("atendimentos." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S)); criteriaPacCpas.add(Restrictions.eq("atendimentos." +
	 * AghAtendimentos.Fields.IND_PAC_CPA.toString(), true));
	 * 
	 * criteria.add(Restrictions.or( Subqueries.exists(criteriaPacAtendProfissional), Restrictions.or( Subqueries.exists(criteriaServidorUnidadeFuncional),
	 * Restrictions.or(Subqueries.exists(criteriaServidorEspecialidades), Restrictions.or(Subqueries.exists(criteriaServidorEquipes), Subqueries.exists(criteriaPacCpas))))));
	 * 
	 * criteria.addOrder(Order.asc("leito")); criteria.addOrder(Order.asc("quarto")); criteria.addOrder(Order.asc("unidadeFuncional"));
	 * 
	 * return executeCriteria(criteria);
	 * 
	 * }
	 */
	public List<VListaEpePrescricaoEnfermagem> listarPacientesEnfermagemView(RapServidores servidor) {
		if (servidor == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		final DetachedCriteria criteria = DetachedCriteria.forClass(VListaEpePrescricaoEnfermagem.class, "VEPE");
		
		// Exists: Paciente Atendimento Profissional
		final DetachedCriteria criteriaPacAtendProfissional = DetachedCriteria.forClass(MpmPacAtendProfissional.class, "atendProfissional");
		criteriaPacAtendProfissional.setProjection(Projections.property(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString()));
		criteriaPacAtendProfissional.add(Restrictions.eq(MpmPacAtendProfissional.Fields.SERVIDOR.toString(), servidor));
		criteriaPacAtendProfissional.add(Restrictions.eqProperty(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString(), "VEPE."+ VListaEpePrescricaoEnfermagem.Fields.ATENDIMENTO.toString()));
		
		// Exists: Servidor Unidade funcional
		final DetachedCriteria criteriaServidorUnidadeFuncional = DetachedCriteria.forClass(MpmServidorUnidFuncional.class, "servidorUnidadeFuncional");
		criteriaServidorUnidadeFuncional.setProjection(Projections.property(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString() + "."
				+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteriaServidorUnidadeFuncional.add(Restrictions.eq(MpmServidorUnidFuncional.Fields.SERVIDOR.toString(), servidor));
		criteriaServidorUnidadeFuncional.add(Restrictions.eqProperty(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString() + "."
				+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), "VEPE." + VListaEpePrescricaoEnfermagem.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));
		
		// Exists: Lista Servidor Especialidade
		final DetachedCriteria criteriaServidorEspecialidades = DetachedCriteria.forClass(MpmListaServEspecialidade.class, "servidorEspecialidade");
		criteriaServidorEspecialidades.setProjection(Projections.property(MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString() + "."
				+ AghEspecialidades.Fields.SEQ.toString()));
		criteriaServidorEspecialidades.add(Restrictions.eq(MpmListaServEspecialidade.Fields.SERVIDOR.toString(), servidor));
		criteriaServidorEspecialidades.add(Restrictions.or(
				Restrictions.and(
						Restrictions.isNotNull("VEPE." + VListaEpePrescricaoEnfermagem.Fields.DTHR_FIM.toString()),
						Restrictions.leProperty(MpmListaServEspecialidade.Fields.CRIADO_EM.toString(), "VEPE." + VListaEpePrescricaoEnfermagem.Fields.DTHR_FIM.toString())),
						Restrictions.and(Restrictions.isNull("VEPE." + VListaEpePrescricaoEnfermagem.Fields.DTHR_FIM.toString()), Restrictions.le(MpmListaServEspecialidade.Fields.CRIADO_EM.toString(), new Date()))));
		criteriaServidorEspecialidades.add(Restrictions.eqProperty(MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString() + "."
				+ AghEspecialidades.Fields.SEQ.toString(), "VEPE." + MpmListaServEspecialidade.Fields.ESPECIALIDADE_SEQ.toString()));
		
		// Exists: Lista Servidor equipe
		final DetachedCriteria criteriaServidorEquipes = DetachedCriteria.forClass(MpmListaServEquipe.class, "servidorEquipe");
		criteriaServidorEquipes.setProjection(Projections.projectionList()
				.add(Projections.property(MpmListaServEquipe.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.CODIGO_VINCULO.toString()))
				.add(Projections.property(MpmListaServEquipe.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.MATRICULA.toString())));
		criteriaServidorEquipes.createAlias(MpmListaServEquipe.Fields.SERVIDOR.toString(), "servidor");
		criteriaServidorEquipes.add(Restrictions.eq(MpmListaServEquipe.Fields.SERVIDOR.toString(), servidor));
		criteriaServidorEquipes.add(Restrictions.or(
				Restrictions.and(Restrictions.isNotNull("VEPE." + VListaEpePrescricaoEnfermagem.Fields.DTHR_FIM.toString()),
						Restrictions.leProperty(MpmListaServEquipe.Fields.CRIADO_EM.toString(), "VEPE." + VListaEpePrescricaoEnfermagem.Fields.DTHR_FIM.toString())),
						Restrictions.and(Restrictions.isNull("VEPE." + VListaEpePrescricaoEnfermagem.Fields.DTHR_FIM.toString()),
								Restrictions.le(MpmListaServEquipe.Fields.CRIADO_EM.toString(), new Date()))));
		criteriaServidorEquipes.add(Restrictions.eqProperty(MpmListaServEquipe.Fields.SERVIDOR.toString(), "VEPE." + VListaEpePrescricaoEnfermagem.Fields.SERVIDOR.toString()));

		// Exists: Lista Paciente Cpa
		final DetachedCriteria criteriaPacCpas = DetachedCriteria.forClass(MpmListaPacCpa.class, "pacCpa");
		criteriaPacCpas.setProjection(Projections.property(MpmListaPacCpa.Fields.IND_PAC_CPA.toString()));
		criteriaPacCpas.add(Restrictions.eq(MpmListaPacCpa.Fields.SERVIDOR.toString(), servidor));
		criteriaPacCpas.add(Restrictions.eq(MpmListaPacCpa.Fields.IND_PAC_CPA.toString(), true));
		criteriaPacCpas.add(Restrictions.eq("VEPE." + VListaEpePrescricaoEnfermagem.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteriaPacCpas.add(Restrictions.eq("VEPE." + VListaEpePrescricaoEnfermagem.Fields.IND_PAC_CPA.toString(), true));

		// Criando os filtro com Exists
		criteria.add(Restrictions.or(
				Subqueries.exists(criteriaPacAtendProfissional),
				Restrictions.or(
						Subqueries.exists(criteriaServidorUnidadeFuncional),
						Restrictions.or(Subqueries.exists(criteriaServidorEspecialidades),
								Restrictions.or(Subqueries.exists(criteriaServidorEquipes), Subqueries.exists(criteriaPacCpas))))));

		
		return executeCriteria(criteria);
	}
}
