package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @see MpmListaServEquipe
 * 
 * @author cvagheti
 * 
 */
public class MpmListaServEquipeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmListaServEquipe> {

	private static final long serialVersionUID = -120375244139620322L;

	/**
	 * Retorna criteria para pesquisa de equipes configuradas para o servidor
	 * fornecido.
	 * 
	 * @param servidor
	 *            configurado para
	 * @return
	 */
	protected DetachedCriteria criteriaConfiguradaPara(RapServidores servidor) {
		if (servidor == null) {
			throw new IllegalArgumentException("Argumento obrigatório");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmListaServEquipe.class);

		criteria.add(Restrictions.eq(MpmListaServEquipe.Fields.SERVIDOR
				.toString(), servidor));

		return criteria;
	}

	public List<RapServidores> listaProfissionaisEquipAtendimento(
			RapServidores servidor) {

		List<RapServidores> result = new ArrayList<RapServidores>();

		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmListaServEquipe.class);
		
		criteria.createAlias(MpmListaServEquipe.Fields.EQUIPE.toString(), "EQP");
		criteria.createAlias("EQP."+AghEquipes.Fields.RAP_SERVIDORES.toString(), "SERV");
		criteria.createAlias("SERV."+RapServidores.Fields.ATENDIMENTOS.toString(), "ATD");

		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.IND_SIT_SUMARIO_ALTA
				.toString(), DominioSituacaoSumarioAlta.P));

		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO
				.toString(), DominioPacAtendimento.N));

		criteria.add(Restrictions.eq("SERV."+RapServidores.Fields.ID
				.toString(), servidor.getId()));
		
		List<MpmListaServEquipe> list = executeCriteria(criteria);
		Set<RapServidores> set = new HashSet<RapServidores>();
		for (MpmListaServEquipe l : list) {
			set.add(l.getServidor());
		}

		result = new ArrayList<RapServidores>(set);
		return result;
	}
	
	/**
	 * Retorna os profissionais responsaveis pelas equipes configuradas para
	 * serem apresentadas na lista de pacientes do servidor fornecido.
	 * 
	 * @param servidor
	 *            configuração do servidor
	 * @return profissionais responsaveis pela equipe
	 */
	public List<RapServidores> listaProfissionaisRespEquipe(
			RapServidores servidor) {
		List<RapServidores> result = new ArrayList<RapServidores>();

		DetachedCriteria criteriaEquipe = this
				.criteriaConfiguradaPara(servidor);

		// TODO: fazer com projection
		List<MpmListaServEquipe> list = executeCriteria(criteriaEquipe);
		for (MpmListaServEquipe l : list) {
			result.add(l.getEquipe().getProfissionalResponsavel());
		}

		return result;
	}

	

	public List<AghEquipes> pesquisarEquipesPorServidor(RapServidores servidor) {
		DetachedCriteria criteria = criteriaEquipeConfiguradaPara(servidor);
		return executeCriteria(criteria);

	}

	public List<AghEquipes> pesquisarEquipesPorEspecialidade(Object objPesquisa, AghEspecialidades especialidade, DominioSituacao situacao) {

		// Consulta todos os profissionais de uma especialidade através do parâmeto (instância de AghEspecialidades)
		DetachedCriteria criteriaAghProfEspecialidades = DetachedCriteria.forClass(AghProfEspecialidades.class);
		criteriaAghProfEspecialidades.add(Restrictions.eq(AghProfEspecialidades.Fields.ID_ESPECIALIDADE_SEQ.toString(),(especialidade!=null?especialidade.getSeq():null)));
		// Restringe a busca de profissionais cujo atributo indAtuaAmbt seja Sim
		criteriaAghProfEspecialidades.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true));
		// Instância da lista quem armazena profissionais de uma especialidade
		List<AghProfEspecialidades> listAghProfEspecialidades = executeCriteria(criteriaAghProfEspecialidades);

		// Consulta todas as equipes de uma especialidade e separa todos os profissionais
		DetachedCriteria criteriaAghEquipes = DetachedCriteria.forClass(AghEquipes.class);

		// Separa profissionais
		Disjunction disjunction = Restrictions.disjunction();
		Criterion criterionVinCodigo = null;
		Criterion criterionMatricula = null;
		for (AghProfEspecialidades aghProfEspecialidades : listAghProfEspecialidades) {
			
			criterionMatricula = Restrictions.eq(AghEquipes.Fields.RAP_SERVIDORES_ID_MATRICULA.toString(), aghProfEspecialidades.getRapServidor().getId().getMatricula());
			criterionVinCodigo = Restrictions.eq(AghEquipes.Fields.RAP_SERVIDORES_ID_VIN_CODIGO.toString(), aghProfEspecialidades.getRapServidor().getId().getVinCodigo());
			
			disjunction.add(Restrictions.and(criterionVinCodigo, criterionMatricula));
		}
		criteriaAghEquipes.add(disjunction);

		// Considera os parâmetros informados para pesquisa
		String  srtPesquisa = (String)objPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
		    if (CoreUtil.isNumeroInteger(objPesquisa)) {
		    	criteriaAghEquipes.add(Restrictions.eq(AghEquipes.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
		    } else {
		    	criteriaAghEquipes.add(Restrictions.ilike(AghEquipes.Fields.NOME.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		    }
		}
		if(situacao!=null){
			criteriaAghEquipes.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(), situacao));
		}	
		
		// Ordena profissionais por nome
		criteriaAghEquipes.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
		
		// Realiza uma distinção no resultados da consulta de equipes
		criteriaAghEquipes.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteriaAghEquipes);
	}
	
	
	
	/**
	 * 
	 * @param especialidade
	 * @return
	 */
	public List<VRapServidorConselho> pesquisarProfissionaisPorEspecialidade(Object objPesquisa, AghEspecialidades especialidade, Object[] cprCodigos) {

		DetachedCriteria criteriaAghProfEspecialidades = DetachedCriteria.forClass(AghProfEspecialidades.class);
		criteriaAghProfEspecialidades.add(Restrictions.eq(AghProfEspecialidades.Fields.ID_ESPECIALIDADE_SEQ.toString(),especialidade.getSeq()));
		// Restringe a busca de profissionais cujo atributo indAtuaAmbt seja Sim
		criteriaAghProfEspecialidades.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true));
		List<AghProfEspecialidades> listAghProfEspecialidades = executeCriteria(criteriaAghProfEspecialidades);

		DetachedCriteria criteriaVRapServidorConselho = DetachedCriteria.forClass(VRapServidorConselho.class);

		// Separa profissionais
		Disjunction disjunction = Restrictions.disjunction();
		Criterion criterionVinCodigo = null;
		Criterion criterionMatricula = null;
		for (AghProfEspecialidades aghProfEspecialidades : listAghProfEspecialidades) {
			criterionMatricula = Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), aghProfEspecialidades.getRapServidor().getId().getMatricula());
			criterionVinCodigo = Restrictions.eq(VRapServidorConselho.Fields.VIN_CODIGO.toString(), aghProfEspecialidades.getRapServidor().getId().getVinCodigo());
			disjunction.add(Restrictions.and(criterionVinCodigo, criterionMatricula));
		}
		criteriaVRapServidorConselho.add(disjunction);
		
		String  srtPesquisa = (String)objPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
		    if (CoreUtil.isNumeroInteger(objPesquisa)) {
		    	criteriaVRapServidorConselho.add(Restrictions.eq(VRapServidorConselho.Fields.MATRICULA.toString(), Integer.valueOf(srtPesquisa)));
		    } else {
		    	criteriaVRapServidorConselho.add(Restrictions.ilike(VRapServidorConselho.Fields.NOME.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		    }
		}

		criteriaVRapServidorConselho.add(Restrictions.in(VRapServidorConselho.Fields.CPR_CODIGO.toString(), cprCodigos));
		
		// Realiza uma distinção no resultados da consulta de equipes
		criteriaVRapServidorConselho.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);

		// Ordena profissionais por nome, sigla e número no conselho
		criteriaVRapServidorConselho.addOrder(Order.asc(VRapServidorConselho.Fields.NOME.toString()));
		criteriaVRapServidorConselho.addOrder(Order.asc(VRapServidorConselho.Fields.SIGLA.toString()));
		criteriaVRapServidorConselho.addOrder(Order.asc(VRapServidorConselho.Fields.NRO_REG_CONSELHO.toString()));
		
		return executeCriteria(criteriaVRapServidorConselho);
	}

	private DetachedCriteria criteriaEquipeConfiguradaPara(
			RapServidores servidor) {
		return this.criteriaConfiguradaPara(servidor).setProjection(
				Projections.property("equipe"));
	}

	public List<MpmListaServEquipe> pesquisarAssociacoesPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = criteriaConfiguradaPara(servidor);
		return executeCriteria(criteria);
	}

	public Long pesquisarAssociacoesPorServidorCount(final  RapServidores servidor) {
		DetachedCriteria criteria = criteriaConfiguradaPara(servidor);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Método para pesquisar lista de MpmListaServEquipe pelo servidor com
	 * restrições nas datas de final do seu vinculo
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @param dataFim
	 * @return
	 */
	public List<MpmListaServEquipe> pesquisarListaServidorEquipePorServidor(
			Integer matricula, Short vinCodigo, Date dataFim) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmListaServEquipe.class);
		
		criteria.createAlias(
				MpmListaServEquipe.Fields.EQUIPE.toString(),
				"equipe");
		criteria.createAlias(
				"equipe." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(),
				"profissional");
		criteria.createAlias(
				MpmListaServEquipe.Fields.SERVIDOR.toString(),
				"servidor");
		
		criteria.add(Restrictions.eq("profissional."
				+ RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("profissional."
				+ RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		
		Date data = new Date();
		if (dataFim != null) {
			data = dataFim;
		}

		criteria.add(Restrictions.le(
				MpmListaServEquipe.Fields.CRIADO_EM.toString(), data));

		Criterion c1 = Restrictions.gt("servidor."
				+ RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());
		Criterion c2 = Restrictions.isNull("servidor."
				+ RapServidores.Fields.DATA_FIM_VINCULO.toString());

		criteria.add(Restrictions.or(c1, c2));

		return this.executeCriteria(criteria);
	}
}