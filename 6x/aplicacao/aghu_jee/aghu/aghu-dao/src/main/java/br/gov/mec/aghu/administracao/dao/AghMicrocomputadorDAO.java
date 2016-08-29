package br.gov.mec.aghu.administracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghCaractMicrocomputador;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FccCentroCustos;

public class AghMicrocomputadorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghMicrocomputador> {

	private static final String CARACTERISTICA2 = "caracteristica";
	private static final long serialVersionUID = -6200579003736768608L;

	/** Pesquisa registros por nome
	 * 
	 * @param nome
	 * @return
	 */
	public AghMicrocomputador obterComputadorPorNomeComputador(String computador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class);
		criteria.createAlias(AghMicrocomputador.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghMicrocomputador.Fields.UNIDFUNCIONALSALA.toString(), "USL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghMicrocomputador.Fields.SALA_EXECUTORA_EXAMES.toString(), "SEE", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AghMicrocomputador.Fields.NOME.toString(), computador).ignoreCase());
				
		return (AghMicrocomputador) executeCriteriaUniqueResult(criteria);
	}
	
	public AghMicrocomputador obterAghMicroComputadorPorNomeOuIPException(String computadorRede) throws ApplicationBusinessException{		
		AghMicrocomputador micro = obterAghMicroComputadorPorNomeOuIP(computadorRede, null);
		
		if(micro==null){
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.AGH_MICROCOMPUTADOR_NAO_CADASTRADO, computadorRede);
		}
		
		return micro;		
	}

	public AghMicrocomputador obterAghMicroComputadorPorNomeOuIP(String computadorRede, DominioCaracteristicaMicrocomputador caracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class);
		
		criteria.createAlias(AghMicrocomputador.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghMicrocomputador.Fields.UNIDFUNCIONALSALA.toString(), "USL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.or(Restrictions.eq(AghMicrocomputador.Fields.NOME.toString(), computadorRede).ignoreCase(), Restrictions.eq(AghMicrocomputador.Fields.IP.toString(), computadorRede)));
		if(caracteristica != null) {
			criteria.createAlias(AghMicrocomputador.Fields.CARACTERISTICAS.toString(), CARACTERISTICA2);
			criteria.add(Restrictions.eq(CARACTERISTICA2 + "." + AghCaractMicrocomputador.Fields.CARACTERISTICA.toString(), caracteristica));
		}
		return (AghMicrocomputador) executeCriteriaUniqueResult(criteria);
	}
	
	public boolean validaDadosInformadosMicrocomputador(String nome, String ip, String ponto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class);
		criteria.createAlias(AghMicrocomputador.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghMicrocomputador.Fields.UNIDFUNCIONALSALA.toString(), "USL", JoinType.LEFT_OUTER_JOIN);
		
		if (!StringUtils.isEmpty(nome)) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.NOME.toString(), nome).ignoreCase());
		}
		
		if (!StringUtils.isEmpty(ip)) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.IP.toString(), ip).ignoreCase());
		}
		
		if (!StringUtils.isEmpty(ponto)) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.PONTO.toString(), ponto).ignoreCase());
		}
		return executeCriteriaExists(criteria);
	}
	
	public Long verificaQtdComputador(String nome, String ponto, String ip){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class);
		
		if (!StringUtils.isEmpty(nome)) {
			criteria.add(Restrictions.not(Restrictions.eq(AghMicrocomputador.Fields.NOME.toString(), nome).ignoreCase()));
		}
		
		if (!StringUtils.isEmpty(ponto)) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.PONTO.toString(), ponto).ignoreCase());
		}
		
		if (!StringUtils.isEmpty(ip)) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.IP.toString(), ip).ignoreCase());
		}
		return executeCriteriaCount(criteria);
	}
	
	
	/** Pesquisa registros por zona/sala
	 * 
	 * @param unfSeq
	 * @param sala
	 * @return
	 */
	public List<AghMicrocomputador> pesquisarAghMicrocomputadorPorZonaESala(Short unfSeq, 
			Byte sala) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class);
		criteria.add(Restrictions.eq(AghMicrocomputador.Fields.USL_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AghMicrocomputador.Fields.USL_SALA.toString(), sala));
		
		return executeCriteria(criteria);
	}	
	
	/** Pesquisa registros por sala
	 * 
	 * @param sala
	 * @return
	 */
	public List<AghMicrocomputador> pesquisarAghMicrocomputadorPorSala(Byte sala) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class);
		criteria.add(Restrictions.eq(AghMicrocomputador.Fields.USL_SALA.toString(), sala));
		
		return executeCriteria(criteria);
	}
	
	//#5714
	public List<AghMicrocomputador> pesquisarTodosMicroAtivos(Object estacao){
		DetachedCriteria criteria = getCriteriaPesquisarTodosMicroAtivos(estacao);
		criteria.addOrder(Order.asc(AghMicrocomputador.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, "", true);
	}
	
	//#5714
	public Long pesquisarTodosMicroAtivosCount(Object estacao){
		DetachedCriteria criteria = getCriteriaPesquisarTodosMicroAtivos(estacao);
		return executeCriteriaCount(criteria);
	}
	
	//#5714
	private DetachedCriteria getCriteriaPesquisarTodosMicroAtivos(Object estacao) {
		DetachedCriteria criteria  = DetachedCriteria.forClass(AghMicrocomputador.class);
		
		criteria.add(Restrictions.eq(AghMicrocomputador.Fields.ATIVO.toString(), Boolean.TRUE));
		
		if(estacao!=null){
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.NOME.toString(), estacao.toString(), MatchMode.ANYWHERE));
		}
		
		return criteria;
	}

		
	
	/**
	 * Pesquisa de microcomputador por filtros
	 * @return
	 */
	public List<AghMicrocomputador> pesquisarMicrocomputador(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, String nome, Byte sala, Short ramal, 
			DominioSimNao prioridade, DominioSimNao indAtivo, String ponto, String ip, String usuario,
			String impPadrao, String impEtiquetas, String impMatricial, String observacao, AghUnidadesFuncionais unidadeFuncional) {
		
		DetachedCriteria criteria = this.createCriteriaMicrocomputador(nome, sala, ramal, prioridade,
				indAtivo, ponto, ip, usuario, impPadrao, impEtiquetas, impMatricial, observacao, unidadeFuncional);
		
		criteria.addOrder(Order.asc(AghMicrocomputador.Fields.NOME.toString()));
				
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Contador de registros da pesquisa de microcomputador
	 * @return
	 */
	public Long pesquisarMicromputadorCount(String nome, Byte sala, Short ramal, DominioSimNao prioridade, 
			DominioSimNao indAtivo, String ponto, String ip, String usuario, String impPadrao, 
			String impEtiquetas, String impMatricial, String observacao, AghUnidadesFuncionais unidadeFuncional) {
		
		DetachedCriteria criteria = this.createCriteriaMicrocomputador(nome, sala, ramal, prioridade,
				indAtivo, ponto, ip, usuario, impPadrao, impEtiquetas, impMatricial, observacao, unidadeFuncional);
		
		return executeCriteriaCount(criteria);
	}

	/**
	 * Cria a criteria do filtro
	 * @return
	 */
	@SuppressWarnings({"PMD.NPathComplexity"})
	private DetachedCriteria createCriteriaMicrocomputador(String nome, Byte sala, Short ramal, 
			DominioSimNao prioridade, DominioSimNao indAtivo, String ponto, String ip, String usuario, 
			String impPadrao, String impEtiquetas, String impMatricial, String observacao, AghUnidadesFuncionais unidadeFuncional) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class);
				
		criteria.createAlias(AghMicrocomputador.Fields.UNIDADE_FUNCIONAL.toString()
				, AghMicrocomputador.Fields.UNIDADE_FUNCIONAL.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghMicrocomputador.Fields.UNIDFUNCIONALSALA.toString()
				, AghMicrocomputador.Fields.UNIDFUNCIONALSALA.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghMicrocomputador.Fields.SALA_EXECUTORA_EXAMES.toString(), "salaExecutora", JoinType.LEFT_OUTER_JOIN);
		
		if(StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		if(unidadeFuncional!=null) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));
		}
		if(sala!=null) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.USL_SALA.toString(), sala));
		}
		if(ramal!=null) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.RAMAL.toString(), ramal));
		}
		if(prioridade!=null) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.PRIORIDADE.toString(), prioridade.isSim()));
		}
		if(indAtivo!=null) {
			criteria.add(Restrictions.eq(AghMicrocomputador.Fields.ATIVO.toString(), indAtivo.isSim()));
		}
		if(StringUtils.isNotBlank(ponto)) {
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.PONTO.toString(), ponto, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(ip)) {
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.IP.toString(), ip, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(usuario)) {
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.USUARIO.toString(), usuario, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(impPadrao)) {
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.IMPRESSORA_PADRAO.toString(), impPadrao, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(impMatricial)) {
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.IMPRESSORA_MATRICIAL.toString(), impMatricial, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(impEtiquetas)) {
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.IMPRESSORA_ETIQUETAS.toString(), impEtiquetas, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(observacao)) {
			criteria.add(Restrictions.ilike(AghMicrocomputador.Fields.OBSERVACAO.toString(), observacao, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	public Long buscarQtdeMicrocomputadorPorCentroCusto(FccCentroCustos centroCusto){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class, "mc" );
		criteria.createAlias("mc." + AghMicrocomputador.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		
		criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), centroCusto));
		
		return this.executeCriteriaCountDistinct(criteria, AghMicrocomputador.Fields.NOME.toString(), true);
	}

	public Boolean verificarCaracteristicaMicrocomputador(AghMicrocomputador microcomputador,
			DominioCaracteristicaMicrocomputador dominioCaracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMicrocomputador.class);
		
		criteria.add(Restrictions.or(
				Restrictions.eq(AghMicrocomputador.Fields.NOME.toString(), microcomputador.getNome()).ignoreCase()));
		if(dominioCaracteristica != null) {
			criteria.createAlias(AghMicrocomputador.Fields.CARACTERISTICAS.toString(), CARACTERISTICA2);
			criteria.add(Restrictions.eq(CARACTERISTICA2 + "." + AghCaractMicrocomputador.Fields.CARACTERISTICA.toString(), dominioCaracteristica));
		}		
		return executeCriteriaCount(criteria) > 0;
	}
		
}

