package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.DateType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelJustificativaExclusoes;
import br.gov.mec.aghu.model.AelProjetoEquipe;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AelProjetoPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelProjetoPacientes> {
	
	private static final long serialVersionUID = -6650068409149071112L;

	public List<AelProjetoPacientes> listarProjetosPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class);
		
		criteria.add(Restrictions.eq(AelProjetoPacientes.Fields.PAC_CODIGO.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}

	public List<Integer> pesquisarCProjPac(final Integer codigoPaciente, final Date dataTruncada) {
		return executeCriteria(montarCriteriaPesquisarCProjPac(codigoPaciente, dataTruncada), 0, 1, new HashMap<String, Boolean>());
	}
	
	public Boolean verificarExisteCProjPac(final Integer codigoPaciente, final Date dataTruncada) {
		return executeCriteriaCount(montarCriteriaPesquisarCProjPac(codigoPaciente, dataTruncada)) > 0;
	}	
	
	/**
	 * ORADB: CURSOR: C_PROJ_PAC
	 */
	private DetachedCriteria montarCriteriaPesquisarCProjPac(final Integer codigoPaciente, final Date dataTruncada) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class, "PPJ");
		criteria.setProjection(Projections.property(AelProjetoPacientes.Fields.PJQ_SEQ.toString()));
		
		criteria.add(Restrictions.eq(AelProjetoPacientes.Fields.PAC_CODIGO.toString(), codigoPaciente));

		final Object[] values = {dataTruncada};	final Type[] types = {DateType.INSTANCE};
		
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction("({alias}."+AelProjetoPacientes.Fields.DT_FIM.name()+" IS NULL OR TRUNC({alias}."+AelProjetoPacientes.Fields.DT_FIM.name()+") >= ?)", values, types));
			
		} else {
			criteria.add(Restrictions.sqlRestriction("({alias}."+AelProjetoPacientes.Fields.DT_FIM.name()+" IS NULL OR DATE_TRUNC('DAY', {alias}."+AelProjetoPacientes.Fields.DT_FIM.name()+") >= ?)", values, types));
		}

		final DetachedCriteria subCriteria = DetachedCriteria.forClass(AelJustificativaExclusoes.class, "JEX");
		subCriteria.setProjection(Projections.property(AelJustificativaExclusoes.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("JEX."+AelJustificativaExclusoes.Fields.SEQ.toString(), 
												"PPJ."+AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()
												)
					   );
		subCriteria.add(Restrictions.eq("JEX."+AelJustificativaExclusoes.Fields.IND_MOSTRA_TELAS.toString(), DominioSimNao.S));
//		criteria.add(Subqueries.exists(subCriteria));
		criteria.add(Restrictions.or(Restrictions.isNull("PPJ."+AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()),
									 Subqueries.exists(subCriteria)
								 	)
					);
		
		return criteria;
	}
	

	/**
	 * /**
	 * FUNCTION AIPC_VER_MONITOR_PRJ
	 * c_proj_monitor_2
	 * 
	 * @param codigoPaciente
	 * @param servidorMatricula
	 * @param servidorVinCodigo
	 * @param funcao
	 * @return
	 */
	public List<AelProjetoPacientes> buscarProjetosPesquisaComPaciente(Integer codigoPaciente,
																				  Integer servidorMatricula,
																		          Short servidorVinCodigo,
																		          String funcao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class, "projetoPaciente");
		criteria.createAlias("projetoPaciente." + AelProjetoPacientes.Fields.PROJETO_PESQUISA.toString(), "projetoPesquisa");
		criteria.createAlias("projetoPesquisa." + AelProjetoPesquisas.Fields.PROJETO_EQUIPES.toString(), "projetoEquipe");
		criteria.createAlias("projetoPaciente." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO.toString(), "justificativaExclusao");
		
		criteria.add(Restrictions.eq("projetoPaciente." + AelProjetoPacientes.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.isNotNull("projetoPaciente." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()));
		criteria.add(Restrictions.eq("projetoEquipe." + AelProjetoEquipe.Fields.SERVIDOR_MATRICULA.toString(), servidorMatricula));
		criteria.add(Restrictions.eq("projetoEquipe." + AelProjetoEquipe.Fields.SERVIDOR_VIN_CODIGO.toString(), servidorVinCodigo));
		criteria.add(Restrictions.eq("justificativaExclusao." + AelJustificativaExclusoes.Fields.IND_MOSTRA_TELAS.toString(), DominioSimNao.S));

		if(StringUtils.isNotBlank(funcao)) {
			criteria.add(Restrictions.eq("projetoEquipe." + AelProjetoEquipe.Fields.FUNCAO.toString(), funcao));
		}
		
		return executeCriteria(criteria);
	}
	
	public List<AelProjetoPacientes> pesquisarProjetosPesquisaPacientePOL(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, Integer matricula, Short vinCodigo) {
		 
		DetachedCriteria criteria = getCriteriaPesquisarProjetosPesquisaPacientePOL(
				codigo, matricula, vinCodigo);
		
		criteria.addOrder(Order.desc("projetoPesquisa." + AelProjetoPesquisas.Fields.DATA_INICIO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}


	private DetachedCriteria getCriteriaPesquisarProjetosPesquisaPacientePOL(
			Integer codigo, Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class, "t1");
		
		//Joins com tabelas  		
		criteria.createAlias("t1." + AelProjetoPacientes.Fields.PROJETO_PESQUISA.toString(), "projetoPesquisa");		
		
		criteria.add(Restrictions.eq("t1." + AelProjetoPacientes.Fields.PAC_CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq("t1." + AelProjetoPacientes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.or(Restrictions.isNull("projetoPesquisa." + AelProjetoPesquisas.Fields.DATA_FIM.toString()),
								     Restrictions.gt("projetoPesquisa." + AelProjetoPesquisas.Fields.DATA_FIM.toString(), new Date()))); 
		
		criteria.add(Restrictions.or(Restrictions.isNull("t1." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()),
				                     Subqueries.exists(subCriteriaJustificativaExclusao()))); 
		
		if(matricula != null){
			criteria.add(Subqueries.propertyIn("projetoPesquisa." + AelProjetoPesquisas.Fields.SEQ.toString(), subCriteriaUsuario(matricula, vinCodigo)));
		}
		return criteria;
	}

	private DetachedCriteria subCriteriaJustificativaExclusao() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelJustificativaExclusoes.class,"t2");

		criteria.setProjection(Projections
				.projectionList().add(Projections.property("t2." + AelJustificativaExclusoes.Fields.SEQ.toString())));
		
		criteria.add(Restrictions.eqProperty("t2." + AelJustificativaExclusoes.Fields.SEQ.toString(), "t1.justificativaExclusao.seq"));
		criteria.add(Restrictions.eq("t2." + AelJustificativaExclusoes.Fields.IND_MOSTRA_TELAS.toString(), DominioSimNao.S));
		
		return criteria;
	}
	
	
	private DetachedCriteria subCriteriaUsuario(Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoEquipe.class,"t3");

		criteria.setProjection(Projections
				.projectionList().add(Projections.property("t3." + AelProjetoEquipe.Fields.AEL_PROJETO_PESQUISAS_SEQ.toString())));
		
		criteria.add(Restrictions.eq("t3." + AelProjetoEquipe.Fields.SERVIDOR_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("t3." + AelProjetoEquipe.Fields.SERVIDOR_VIN_CODIGO.toString(), vinCodigo));
		criteria.add(Restrictions.eq("t3." + AelProjetoEquipe.Fields.FUNCAO.toString(), "M"));
		criteria.add(Restrictions.eq("t3." + AelProjetoEquipe.Fields.IND_SITUACAO.toString(), "A"));
		
		return criteria; 
	}

	public Long pesquisarProjetosPesquisaPacientePOLCount(Integer codigo, Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = getCriteriaPesquisarProjetosPesquisaPacientePOL(
				codigo, matricula, vinCodigo); 
		
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * 
	 * FUNCTION AIPC_VER_MONITOR_PRJ
	 * c_proj_monitor_1
	 * 
	 * @param codigoPaciente
	 * @param servidorMatricula
	 * @param servidorVinCodigo
	 * @return ListaProjetoEquipe
	 * 
	 */
	
	public List<AelProjetoPacientes> pesquisarProjetoEquipesComPaciente(Integer pacCodigo, Integer matricula, Short vinCodigo, String funcao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class, "projetoPaciente");
		criteria.createAlias("projetoPaciente." + AelProjetoPacientes.Fields.PROJETO_PESQUISA.toString(), "projetoPesquisa");
		criteria.createAlias("projetoPesquisa." + AelProjetoPesquisas.Fields.PROJETO_EQUIPES.toString(), "projetoEquipe");
				
		criteria.add(Restrictions.eq("projetoPaciente." + AelProjetoPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNull("projetoPaciente." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()));
		criteria.add(Restrictions.eq("projetoEquipe." + AelProjetoEquipe.Fields.SERVIDOR_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("projetoEquipe." + AelProjetoEquipe.Fields.SERVIDOR_VIN_CODIGO.toString(), vinCodigo));
		
		if(funcao != null && funcao.trim().equalsIgnoreCase("M")){
			criteria.add(Restrictions.eq("projetoEquipe." + AelProjetoEquipe.Fields.FUNCAO.toString(), "M"));
		}

		
		return executeCriteria(criteria);
	}
	
	public AelProjetoPacientes obterProjetoPacienteCadastradoDataProjetoPesquisa(Integer pacCodigo, Integer pjqSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class);
		criteria.createAlias(AelProjetoPacientes.Fields.PROJETO_PESQUISA.toString(), "pjq");
		criteria.add(Restrictions.eq(AelProjetoPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("pjq.".concat(AelProjetoPesquisas.Fields.SEQ.toString()), pjqSeq));

		AelProjetoPacientes projetoPaciente = (AelProjetoPacientes)executeCriteriaUniqueResult(criteria);
		AelProjetoPacientes resultado = null;

		if (projetoPaciente != null){
			
			Date dataAtual = DateUtil.truncaData(new Date());

			Date dtInicioProjetoPaciente = DateUtil.truncaData(projetoPaciente.getDtInicio());
			Date dtFimProjetoPaciente = (Date) CoreUtil.nvl(DateUtil.truncaData(projetoPaciente.getDtFim()), dataAtual);
			boolean isDataProjetoPacienteValida = DateUtil.entre(dataAtual, dtInicioProjetoPaciente, dtFimProjetoPaciente);

			Date dtInicioProjetoPesquisa = DateUtil.truncaData(projetoPaciente.getProjetoPesquisa().getDtInicio());
			Date dtFimProjetoPesquisa = (Date) CoreUtil.nvl(DateUtil.truncaData(projetoPaciente.getProjetoPesquisa().getDtFim()), dataAtual);
			boolean isDataProjetoPesquisaValida = DateUtil.entre(dataAtual, dtInicioProjetoPesquisa, dtFimProjetoPesquisa);

			if (isDataProjetoPacienteValida && isDataProjetoPesquisaValida) {
				resultado = projetoPaciente;
			}

		}

		return resultado;
	}
	
	/**
	 * Web Service #39251 utilizado na estória #864
	 * @param pacCodigo
	 * @return Boolean
	 */
	public List<Object[]> verificaPacienteEmProjetoPesquisa(List<Integer> codigosPaciente){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class, "PPJ");
		
		 ProjectionList projectionList = Projections.projectionList();
		    projectionList.add(Projections.groupProperty("PPJ." + AelProjetoPacientes.Fields.PAC_CODIGO.toString()));
		    projectionList.add(Projections.rowCount());
		    criteria.setProjection(projectionList);

		criteria.createAlias("PPJ." + AelProjetoPacientes.Fields.PROJETO_PESQUISA.toString(), "PJQ");
		    
		criteria.add(Restrictions.sqlRestriction(
				this.isOracle() ? " pjq1_.DT_FIM IS NULL OR TRUNC(pjq1_.DT_FIM) >= TRUNC(sysdate) "
						: " pjq1_.DT_FIM IS NULL OR DATE_TRUNC('day', pjq1_.DT_FIM) >= DATE_TRUNC('day', now()) "));
		
		criteria.add(Restrictions.sqlRestriction(
				this.isOracle() ? " {alias}.DT_FIM IS NULL OR TRUNC({alias}.DT_FIM) >= TRUNC(sysdate) "
						: " {alias}.DT_FIM IS NULL OR DATE_TRUNC('day', {alias}.DT_FIM) >= DATE_TRUNC('day', now()) "));

		DetachedCriteria subQuery = DetachedCriteria.forClass(AelJustificativaExclusoes.class, "JEX");
		subQuery.setProjection(Projections.projectionList().add(
				Projections.property("JEX." + AelJustificativaExclusoes.Fields.SEQ.toString())));
		subQuery.add(Restrictions.eqProperty("JEX." + AelJustificativaExclusoes.Fields.SEQ.toString(), "PPJ." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()));
		subQuery.add(Restrictions.eq("JEX." + AelJustificativaExclusoes.Fields.IND_MOSTRA_TELAS.toString(), DominioSimNao.S));

		criteria.add(Restrictions.or(Restrictions.isNull("PPJ." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()) ,Subqueries.exists(subQuery)));

		return executeCriteria(criteria);
	}	

	public Boolean verificaPacienteEmProjetoPesquisa(Integer codigoPaciente){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class, "PPJ");
		criteria.add(Restrictions.eq("PPJ." + AelProjetoPacientes.Fields.PAC_CODIGO.toString(), codigoPaciente));

		DetachedCriteria subQuery = DetachedCriteria.forClass(AelJustificativaExclusoes.class, "JEX");
		subQuery.setProjection(Projections.projectionList().add(
				Projections.property("JEX." + AelJustificativaExclusoes.Fields.SEQ.toString())));
		subQuery.add(Restrictions.eqProperty("JEX." + AelJustificativaExclusoes.Fields.SEQ.toString(), "PPJ." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()));
		subQuery.add(Restrictions.eq("JEX." + AelJustificativaExclusoes.Fields.IND_MOSTRA_TELAS.toString(), DominioSimNao.S));

		criteria.add(Restrictions.or(
				Restrictions.sqlRestriction(
						this.isOracle() ? " {alias}.DT_FIM IS NULL OR TRUNC({alias}.DT_FIM) >= TRUNC(sysdate) AND {alias}.JEX_SEQ IS NULL " : 
							" {alias}.DT_FIM IS NULL OR DATE_TRUNC('day', {alias}.DT_FIM) >= DATE_TRUNC('day', now()) AND {alias}.JEX_SEQ IS NULL "), Subqueries.exists(subQuery))
		);
		
		return executeCriteriaCount(criteria) > 0;
	}	

}
