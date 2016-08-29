package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghSamisMovimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuarioVO;

public class AghSamisMovimentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghSamisMovimentos>{

	private static final long serialVersionUID = 4729749129152357679L;

	public Long pesquisaCount(Integer codigoPacientePesquisa, Integer prontuario, String nomePesquisaPaciente) {
		DetachedCriteria criteria = createPesquisaCriteriaAipPacientes(codigoPacientePesquisa, prontuario, nomePesquisaPaciente);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria createPesquisaCriteriaAipPacientes(
			Integer codigoPacientePesquisa, Integer prontuario, String nomePesquisaPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		if (codigoPacientePesquisa != null ) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codigoPacientePesquisa));
		}
		if (prontuario != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		if (nomePesquisaPaciente != null) {
			criteria.add(Restrictions.eq(AipPacientes.Fields.NOME.toString(), nomePesquisaPaciente));
		}
		return criteria;
	}
	
	private DetachedCriteria createPesquisaCriteriaAipMovimentacaoProntuario(Integer codigoPesquisaPaciente,
			Integer prontuarioPesquisa, AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, String nomePesquisaPaciente, DominioSituacaoMovimentoProntuario situacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class, "smov");
		criteria.createAlias("smov."+AipMovimentacaoProntuarios.Fields.PACIENTE.toString(), "paciente", Criteria.INNER_JOIN);
		criteria.createAlias("smov."+AipMovimentacaoProntuarios.Fields.SAMIS.toString(), "samis", Criteria.INNER_JOIN);

		if (codigoPesquisaPaciente != null ) {
			criteria.add(Restrictions.eq("paciente."+AipPacientes.Fields.CODIGO.toString(), codigoPesquisaPaciente));
		}
		if (prontuarioPesquisa != null) {
			criteria.add(Restrictions.eq("paciente."+AipPacientes.Fields.PRONTUARIO.toString(), prontuarioPesquisa));
		}
		if (nomePesquisaPaciente != null) {
			criteria.add(Restrictions.eq("paciente."+AipPacientes.Fields.NOME.toString(), nomePesquisaPaciente));
		}
		if (origemProntuariosPesquisa != null) {
			criteria.add(Restrictions.eq("samis."+AghSamis.Fields.CODIGO.toString(), origemProntuariosPesquisa.getCodigo()));
		}
		if(unidadeSolicitantePesquisa != null) {
			criteria.add(Restrictions.eq("smov."+AghSamisMovimentos.Fields.LOCAL_ATUAL.toString(), unidadeSolicitantePesquisa.getDescricao()));
		}
		if(situacao != null) {
			criteria.add(Restrictions.eq("smov."+AghSamisMovimentos.Fields.SITUACAO.toString(), situacao));
		}
		
		criteria.setProjection(Projections
				.projectionList()
				.add(
				Projections.property("paciente" + "." + AipPacientes.Fields.CODIGO.toString()), "movimentacao.paciente.codigo")
				.add(
				Projections.property("paciente" + "." + AipPacientes.Fields.PRONTUARIO.toString()), "movimentacao.paciente.prontuario")
				.add(
				Projections.property("paciente" + "." + AipPacientes.Fields.NOME.toString()), "movimentacao.paciente.nome")
				.add(
				Projections.property("smov."+AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString()), "movimentacao.dataMovimento")
				.add(
				Projections.property("samis" + "." + AghSamis.Fields.DESCRICAO), "movimentacao.samisOrigem.descricao")
				.add(
				Projections.property("samis" + "." + AghSamis.Fields.CODIGO), "movimentacao.samisOrigem.codigo")
				.add(
				Projections.property("smov."+AipMovimentacaoProntuarios.Fields.LOCAL.toString()), "movimentacao.local")
				.add(
				Projections.property("smov."+AipMovimentacaoProntuarios.Fields.SITUACAO.toString()), "movimentacao.situacao")
				);
		criteria.setResultTransformer(Transformers.aliasToBean(AipMovimentacaoProntuarioVO.class));
		
		return criteria;
	}
	
	public List<AipPacientes> pesquisa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente) {
		DetachedCriteria criteria = createPesquisaCriteriaAipPacientes(codigoPaciente, prontuario, nomePesquisaPaciente);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public List<AipMovimentacaoProntuarioVO> pesquisaMovimentacoesDeProntuarios(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigoPesquisaPaciente,
			Integer prontuarioPesquisa, AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, String nomePesquisaPaciente, DominioSituacaoMovimentoProntuario situacao) {
		DetachedCriteria criteria = createPesquisaCriteriaAipMovimentacaoProntuario(codigoPesquisaPaciente, prontuarioPesquisa, origemProntuariosPesquisa, unidadeSolicitantePesquisa, nomePesquisaPaciente, situacao);
		if(firstResult != null){
			return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		} else{
			return executeCriteria(criteria);
		}
		
	}
	
	public Long pesquisaMovimentacoesDeProntuariosCount(
			Integer codigoPesquisaPaciente,
			Integer prontuarioPesquisa, AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, String nomePesquisaPaciente, DominioSituacaoMovimentoProntuario situacao) {
		DetachedCriteria criteria = createPesquisaCriteriaAipMovimentacaoProntuario(codigoPesquisaPaciente, prontuarioPesquisa, origemProntuariosPesquisa, unidadeSolicitantePesquisa, nomePesquisaPaciente, situacao);
		return executeCriteriaCount(criteria);
	}

	public List<AipMovimentacaoProntuarioVO> pesquisarTodasMovimentacoesParaSelecionarTodas(
			Integer codigoPesquisaPaciente, Integer prontuarioPesquisa,
			AghSamis origemProntuariosPesquisa, AghUnidadesFuncionais unidadeSolicitantePesquisa, String nomePesquisaPaciente, DominioSituacaoMovimentoProntuario situacao) {
		DetachedCriteria criteria = createPesquisaCriteriaAipMovimentacaoProntuario(codigoPesquisaPaciente, prontuarioPesquisa, origemProntuariosPesquisa, unidadeSolicitantePesquisa, nomePesquisaPaciente, situacao);
		return executeCriteria(criteria);
	}
	
}

