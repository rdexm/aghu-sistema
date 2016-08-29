package br.gov.mec.aghu.internacao.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.internacao.business.vo.RegistraExtratoLeitoVO;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.internacao.vo.QuartoDisponibilidadeVO;
import br.gov.mec.aghu.internacao.vo.SituacaoLeitosVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinCaracteristicaLeito;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.FatContasInternacao;
/**
 * 
 * @modulo internacao
 *
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class AinLeitosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinLeitos> {

	private static final long serialVersionUID = -21559964538655712L;
	final static String SEPARADOR = ".";

	protected DetachedCriteria obterCriteriaLeitosAtivosPorListaQuartos(List<Integer> listNroQuarto) {
		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.add(Restrictions.eq(AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(AinLeitos.Fields.QRT_NUMERO.toString(), listNroQuarto));
		return criteria;
	}
	
	public AinLeitos obterLeitoComAla(String leitoID) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.createAlias(AinLeitos.Fields.QUARTO.toString(), "quarto");
		criteria.createAlias(AinLeitos.Fields.ALA.toString(), "ala");
		criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leitoID));
		return (AinLeitos) executeCriteriaUniqueResult(criteria);
	}	

	public List<AinLeitos> obterLeitosAtivosPorUnf(Object pesquisa, Short unfSeq) {
		
		DetachedCriteria criteria = getCriteriaObterLeitosAtivosPorUnf(pesquisa, unfSeq);
		
		return this.executeCriteria(criteria, 0, 100, AinLeitos.Fields.LTO_ID.toString(), true);
	}
	
	private DetachedCriteria getCriteriaObterLeitosAtivosPorUnf(Object pesquisa, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.createAlias(AinLeitos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("INT."+AinInternacao.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (pesquisa != null){
			criteria.add(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), pesquisa.toString(), MatchMode.ANYWHERE));
		}		
		if(unfSeq!=null){
			criteria.add(Restrictions.eq(AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}
		return criteria;
	}
	
	public Long obterLeitosAtivosPorUnfCount(Object pesquisa, Short unfSeq) {
		
		DetachedCriteria criteria = getCriteriaObterLeitosAtivosPorUnf(pesquisa, unfSeq);		
		return this.executeCriteriaCount(criteria);
	}
	
	public int contarLeitosAtivosPorListaQuartos(List<Integer> listNroQuarto) {
		DetachedCriteria criteria = obterCriteriaLeitosAtivosPorListaQuartos(listNroQuarto);
		Long result = this.executeCriteriaCount(criteria);

		return (result != null ? result.intValue() : 0);
	}
	
	/**
	 * Método para reotrnar todos os leitos com situação "ativo"
	 * 
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosAtivos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.add(Restrictions.eq(AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Obter Leitos e Tipos de Movs de Leitos.
	 * 
	 * @return
	 */
	public List<Object[]> obterLeitosTipoMovsLeitos() {
		DetachedCriteria cri = DetachedCriteria.forClass(AinLeitos.class, "leito");

		cri.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "tipoMovLeito");
		cri.setProjection(Projections
				.projectionList()
				.add(Projections.property("leito" + SEPARADOR
						+ AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()))

				.add(Projections.property("leito" + SEPARADOR + AinLeitos.Fields.LTO_ID.toString()))
				.add(Projections.property("leito" + SEPARADOR
						+ AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + SEPARADOR
						+ AinTiposMovimentoLeito.Fields.CODIGO.toString())));

		return executeCriteria(cri);
	}
	
	/**
	 * Cria uma subquery para verificar se a unidade funcional relacionada ao
	 * quarto do leito possui determinada caracteristica
	 * 
	 * @param idLeitoConsultaExterna
	 * @param idLeitoSubquery
	 * @param separadorAtributos
	 * @return
	 */
	public DetachedCriteria obterSubqueryUnidadeFuncionalPossuiCaracteristica(
			final String idLeitoConsultaExterna,
			ConstanteAghCaractUnidFuncionais caracteristica) {

		final String idLeitoSubquery = "leitoSubquery";

		DetachedCriteria subquery2 = DetachedCriteria.forClass(AinLeitos.class,	idLeitoSubquery);

		subquery2.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(),
				AinLeitos.Fields.UNIDADE_FUNCIONAL.toString());

		subquery2.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(),
				AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString());

		subquery2.add(Restrictions.eqProperty(idLeitoSubquery + "." + AinLeitos.Fields.LTO_ID.toString(),
				idLeitoConsultaExterna + "." + AinLeitos.Fields.LTO_ID.toString()));
		
		subquery2.add(Restrictions.eq(
								AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString()	+ "."
										+ AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA,	caracteristica));
		
		subquery2.setProjection(Projections.rowCount());
		return subquery2;
	}
	
	/**
	 * 
	 * Método usado para construir a criteria da view de leitos acomodações sem
	 * as projections.
	 * 
	 * @param id
	 * @param separadorAtributos
	 * @return
	 */
	private DetachedCriteria obterCriteriaViewLeitosAcomodacoesSemProjections(String id, boolean isCount) {
		String idCriteria = "criteriaViewLeitoAcomodacoes";
		if (id != null) {
			idCriteria = id;
		}

		DetachedCriteria criteriaViewLeitoAcomodacoes = DetachedCriteria.forClass(AinLeitos.class, idCriteria);

		criteriaViewLeitoAcomodacoes.createAlias(AinLeitos.Fields.QUARTO.toString(), AinLeitos.Fields.QUARTO.toString());
		criteriaViewLeitoAcomodacoes.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(),AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());
		criteriaViewLeitoAcomodacoes.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(),	AinLeitos.Fields.UNIDADE_FUNCIONAL.toString());
		criteriaViewLeitoAcomodacoes.createAlias(AinLeitos.Fields.QUARTO.toString()+ "." + AinQuartos.Fields.CLINICA.toString(),
				AinQuartos.Fields.CLINICA.toString(), JoinType.LEFT_OUTER_JOIN);
		criteriaViewLeitoAcomodacoes.createAlias(AinLeitos.Fields.QUARTO.toString()	+ "." + AinQuartos.Fields.ACOMODACAO.toString(),
				AinQuartos.Fields.ACOMODACAO.toString());
		criteriaViewLeitoAcomodacoes.createAlias(AinLeitos.Fields.CARACTERISTICAS.toString(),AinLeitos.Fields.CARACTERISTICAS.toString(),
				JoinType.LEFT_OUTER_JOIN);

		criteriaViewLeitoAcomodacoes.createAlias(AinLeitos.Fields.CARACTERISTICAS.toString() + "."
						+ AinCaracteristicaLeito.Fields.TIPO_CARACTERISTICA.toString(),
				AinCaracteristicaLeito.Fields.TIPO_CARACTERISTICA.toString(),JoinType.LEFT_OUTER_JOIN);

		// restrictions da view		
		criteriaViewLeitoAcomodacoes.add(Restrictions.eq(AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteriaViewLeitoAcomodacoes.add(Restrictions.eq(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "." + 
				AghUnidadesFuncionais.Fields.SITUACAO.toString(),DominioSituacao.A));

		criteriaViewLeitoAcomodacoes.add(Restrictions.in(
				AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString()+ "."+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), 
					Arrays.asList(DominioMovimentoLeito.BL, DominioMovimentoLeito.R,DominioMovimentoLeito.L)));

		criteriaViewLeitoAcomodacoes
				.add(Restrictions.or(
						Restrictions.eq(AinLeitos.Fields.CARACTERISTICAS.toString()+"."
								+ AinCaracteristicaLeito.Fields.IND_CARACTERISTICA_PRINCIPAL.toString(),DominioSimNao.S),
						Restrictions.isNull(AinLeitos.Fields.CARACTERISTICAS.toString()+"."
								+ AinCaracteristicaLeito.Fields.IND_CARACTERISTICA_PRINCIPAL.toString())));

		DetachedCriteria subquery2 = obterSubqueryUnidadeFuncionalPossuiCaracteristica(idCriteria, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);

		criteriaViewLeitoAcomodacoes.add(Subqueries.eq(0L, subquery2));
		if (!isCount){
			criteriaViewLeitoAcomodacoes.addOrder(Order.asc(idCriteria + "." + AinLeitos.Fields.LTO_ID.toString()));
		}	

		return criteriaViewLeitoAcomodacoes;
	}
	
	/**
	 * 
	 * @return
	 */
	private DetachedCriteria obterCriteriaViewLeitosAcomodacoes(String id, boolean isCount) {

		DetachedCriteria criteriaViewLeitoAcomodacoes = obterCriteriaViewLeitosAcomodacoesSemProjections(id,isCount);
		criteriaViewLeitoAcomodacoes.setProjection(
			Projections.projectionList().add(Projections.id())						
				.add(// 0
					Projections.property(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "."
							+ AghUnidadesFuncionais.Fields.ANDAR.toString()))
				.add(// 1
					Projections.property(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "."
							+ AghUnidadesFuncionais.Fields.ALA.toString()))
				.add(// 2
					Projections.property(AinQuartos.Fields.CLINICA.toString() + "."
							+ AghClinicas.Fields.CODIGO.toString()))
				.add(// 3
					Projections.property(AinQuartos.Fields.CLINICA.toString() + "."
							+ AghClinicas.Fields.DESCRICAO.toString()))
				.add(// 4
					Projections.property(AinQuartos.Fields.ACOMODACAO.toString() + "."
							+ AinAcomodacoes.Fields.DESCRICAO.toString()))
				.add(// 5
					Projections.property(AinLeitos.Fields.QUARTO.toString() + "."
							+ AinQuartos.Fields.SEXO_OCUPACAO.toString()))
				.add(// 6
					Projections.property(AinLeitos.Fields.QUARTO.toString() + "."
							+ AinQuartos.Fields.NUMERO.toString()))
				.add(// 7
					Projections.property(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + "."
							+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString()))
				.add(// 8
					Projections.property(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "."
							+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()))
				.add(// 9
					Projections.property(AinCaracteristicaLeito.Fields.TIPO_CARACTERISTICA.toString() + "."
							+ AinTipoCaracteristicaLeito.Fields.DESCRICAO.toString()))
				.add(// 10
					Projections.property(AinQuartos.Fields.ACOMODACAO.toString() + "."
							+ AinAcomodacoes.Fields.SEQUENCIAL.toString()))
				.add(// 11
					Projections.property(AinLeitos.Fields.QUARTO.toString() + "."
						+ AinQuartos.Fields.SEXO_DETERMINANTE.toString())));

		return criteriaViewLeitoAcomodacoes;

	}

	/**
	 * Método responsável por criar a criteria da pesquisa de disponibilidade de
	 * leitos.
	 * 
	 * @param idAcomodacao
	 * @param idClinica
	 * @param seqUnidadeFuncional
	 * @param idLeito
	 * @param numeroQuarto
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaDisponibilidadeLeitos(boolean isCount, Integer idAcomodacao, Integer idClinica,
			AghUnidadesFuncionais unidadeFuncional, String idLeito, Short numeroQuarto) {

		final String idCriteriaView = "idCriteriaView";

		DetachedCriteria criteriaViewLeitoAcomodacoes = this.obterCriteriaViewLeitosAcomodacoes(idCriteriaView, isCount);

		// Restrictions da query
		aplicarRestrictionsPesquisaDisponibilidadeLeito(idAcomodacao, idClinica, unidadeFuncional, idLeito, numeroQuarto,
				idCriteriaView, criteriaViewLeitoAcomodacoes);

		return criteriaViewLeitoAcomodacoes;
	}
	

	public void removerComDependencias(AinLeitos leito) {
		removerDependeciasAinCaracteristicaLeito(leito);
		removerDependeciasAinExtratoLeitos(leito);
		
		StringBuffer hql = new StringBuffer(520);
		
		hql.append("delete from ").append(AinLeitos.class.getSimpleName());
		hql.append(" where ").append(AinLeitos.Fields.LTO_ID.toString()).append(" = :idLeito");
		
		javax.persistence.Query query = super.createQuery(hql.toString());
		
		query.setParameter("idLeito", leito.getLeitoID());
		
		query.executeUpdate();
	}
	
	private void removerDependeciasAinExtratoLeitos(AinLeitos leito) {
		StringBuffer hql = new StringBuffer(520);
		
		hql.append("delete from ").append(AinExtratoLeitos.class.getSimpleName());
		hql.append(" where ").append(AinExtratoLeitos.Fields.LEITO_ID.toString()).append(" = :idLeito");
		
		javax.persistence.Query query = super.createQuery(hql.toString());
		
		query.setParameter("idLeito", leito.getLeitoID());
		query.executeUpdate();
	}

	private void removerDependeciasAinCaracteristicaLeito(AinLeitos leito) {
		StringBuffer hql = new StringBuffer(520);
		
		hql.append("delete from ").append(AinCaracteristicaLeito.class.getSimpleName());
		hql.append(" where ").append(AinCaracteristicaLeito.Fields.LTO_LTO_ID.toString()).append(" = :idLeito");
		
		javax.persistence.Query query = super.createQuery(hql.toString());
		
		query.setParameter("idLeito", leito.getLeitoID());
		query.executeUpdate();
		
	}

	
	/**
	 * Método usado para aplicar as restrictions da pesquisa de disponibilidade
	 * de leito a pesquisa sobre a view de leitos acomodações.
	 * 
	 * @param idAcomodacao
	 * @param idClinica
	 * @param seqUnidadeFuncional
	 * @param idLeito
	 * @param numeroQuarto
	 * @param separadorAtributos
	 * @param idCriteriaView
	 * @param criteriaViewLeitoAcomodacoes
	 */
	private void aplicarRestrictionsPesquisaDisponibilidadeLeito(
			Integer idAcomodacao, Integer idClinica, AghUnidadesFuncionais unidadeFuncional,
			String idLeito, Short numeroQuarto, final String idCriteriaView,
			DetachedCriteria criteriaViewLeitoAcomodacoes) {

		if (idAcomodacao != null) {
			criteriaViewLeitoAcomodacoes.add(Restrictions.eq(
					AinQuartos.Fields.ACOMODACAO.toString()	+ "."
							+ AinAcomodacoes.Fields.SEQUENCIAL.toString(), idAcomodacao));
		}

		if (idClinica != null) {
			criteriaViewLeitoAcomodacoes.add(Restrictions.eq(AinQuartos.Fields.CLINICA.toString() + "."
							+ AghClinicas.Fields.CODIGO.toString(), idClinica));
		}

		if (unidadeFuncional != null) {
			if (unidadeFuncional.getIndAla() != null) {
				criteriaViewLeitoAcomodacoes.add(Restrictions.eq(
						AinLeitos.Fields.UNIDADE_FUNCIONAL.toString()+ "." + AghUnidadesFuncionais.Fields.ALA,
						unidadeFuncional.getIndAla()));
			}
			// unidadeFuncional.andar é not null no BD e não precisa de
			// validação para evitar NullPointerException
			criteriaViewLeitoAcomodacoes.add(Restrictions.eq(
					AinLeitos.Fields.UNIDADE_FUNCIONAL.toString()+ "."	+ AghUnidadesFuncionais.Fields.ANDAR, unidadeFuncional.getAndar()));
		}

		if (numeroQuarto != null) {
			criteriaViewLeitoAcomodacoes.add(Restrictions.eq(
					AinLeitos.Fields.QUARTO.toString() + "." + AinQuartos.Fields.NUMERO, numeroQuarto));
		}

		if (idLeito != null) {
			criteriaViewLeitoAcomodacoes.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), idLeito));

		} else {
			DetachedCriteria subquery3 = obterSubqueryUnidadeFuncionalPossuiCaracteristica(
					idCriteriaView,	ConstanteAghCaractUnidFuncionais.UNID_PESQUISA);

			criteriaViewLeitoAcomodacoes.add(Subqueries.eq(0L, subquery3));

		}
	}
	
	/**
	 * Método responsável por realizar a query de consulta de disponibilidades
	 * de leitos.
	 * 
	 * @param idAcomodacao
	 * @param idClinica
	 * @param seqUnidadeFuncional
	 * @param idLeito
	 * @param numeroQuarto
	 * @return
	 */
	public List<Object[]> pesquisarDisponibilidadeLeitos(Integer firstResult, Integer maxResult, Integer idAcomodacao,
			Integer idClinica, AghUnidadesFuncionais unidadeFuncional, String idLeito, Short numeroQuarto) {
		DetachedCriteria criteriaViewLeitoAcomodacoes = obterCriteriaPesquisaDisponibilidadeLeitos(false,idAcomodacao, idClinica,
				unidadeFuncional, idLeito, numeroQuarto);

		return this.executeCriteria(criteriaViewLeitoAcomodacoes, firstResult, maxResult, null, false);
	}
	
	public Long pesquisarDisponibilidadeLeitosCount(Integer idAcomodacao,
			Integer idClinica, AghUnidadesFuncionais unidadeFuncional, String idLeito, Short numeroQuarto) {
		
		DetachedCriteria criteriaViewLeitoAcomodacoes = 
				obterCriteriaPesquisaDisponibilidadeLeitos(true,idAcomodacao, idClinica,	unidadeFuncional, idLeito, numeroQuarto);

		return this.executeCriteriaCount(criteriaViewLeitoAcomodacoes);
	}	
	
	
	public List<Object[]> pesquisarCapacIntQrt(List<QuartoDisponibilidadeVO> quartosList) {
		ArrayList<DominioMovimentoLeito> lista = new ArrayList<DominioMovimentoLeito>();
		lista.add(DominioMovimentoLeito.B);
		lista.add(DominioMovimentoLeito.BI);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.createAlias("quarto", "q");
		criteria.createAlias("tipoMovimentoLeito", "tml");
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.count("q.numero"));
		p.add(Projections.groupProperty("q.capacInternacao"));
		p.add(Projections.groupProperty("q.numero"));
		criteria.setProjection(p);
		
		criteria.add(Restrictions.in("tml.grupoMvtoLeito", lista));
		return executeCriteria(criteria);
	}
	
	
	public List<Object[]> pesquisaLeitosDesocupados(AghClinicas clinica) {
		// Desocupados
		DetachedCriteria cri = DetachedCriteria.forClass(AinLeitos.class, "LTO");
		cri.createAlias("LTO." + AinLeitos.Fields.QUARTO.toString(), "QRT", JoinType.INNER_JOIN);
		cri.createAlias("QRT." + AinQuartos.Fields.CLINICA.toString(), "CLN", JoinType.INNER_JOIN);
		cri.createAlias("LTO." + AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "TML", JoinType.INNER_JOIN);
		
		cri.setProjection(Projections.projectionList()
				.add(Projections.property("CLN." + AghClinicas.Fields.CODIGO.toString()), "codigoClinica")
				.add(Projections.property("TML." + AinTiposMovimentoLeito.Fields.CODIGO.toString()), "codigoTipoMovimentoLeito")
				.add(Projections.property("TML." + AinTiposMovimentoLeito.Fields.DESCRICAO.toString()), "descricaoTipoMovimentoLeito")
				.add(Projections.property("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString()),	"grupoTipoMovimentoLeito")
				.add(Projections.count("CLN." + AghClinicas.Fields.CODIGO.toString()), "quantidade")
				.add(Projections.groupProperty("CLN." + AghClinicas.Fields.CODIGO.toString()))
				.add(Projections.groupProperty("TML." + AinTiposMovimentoLeito.Fields.CODIGO.toString()))
				.add(Projections.groupProperty("TML." + AinTiposMovimentoLeito.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString())));
		cri.add(Restrictions.eq("LTO." + AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		cri.add(Restrictions.not(Restrictions.in("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(),
				new DominioMovimentoLeito[] { DominioMovimentoLeito.I })));
		if (clinica != null) {
			cri.add(Restrictions.eq("CLN." + AghClinicas.Fields.CODIGO, clinica.getCodigo()));
		}
		return executeCriteria(cri);
	}
	
	public Long pesquisarLeitoPorNumeroQuartoInternacao(Short quartoNumero) {
		DetachedCriteria criteriaLeitos = DetachedCriteria.forClass(AinLeitos.class);
		criteriaLeitos.createAlias(AinLeitos.Fields.QUARTO.toString(), AinLeitos.Fields.QUARTO.toString());

		criteriaLeitos.add(Restrictions.eq(AinLeitos.Fields.QRT_NUMERO.toString(), quartoNumero));
		criteriaLeitos.add(Restrictions.isNotNull(AinLeitos.Fields.INTERNACAO.toString()));

		return executeCriteriaCount(criteriaLeitos);
	}
	
	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param leitoID
	 * @return
	 */
	public DominioSituacao obterSituacaoLeito(String leitoID) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinLeitos.class);
		cri.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("indSituacao"))));
		cri.add(Restrictions.eq("leitoID", leitoID));
		Object resultadoPesquisa = this.executeCriteriaUniqueResult(cri);
		
		return (DominioSituacao)resultadoPesquisa;
	}
	
	/**
	 * Retorna a situação do leito
	 * 
	 * @param leito
	 * @return
	 */
	public String obterSituacaoLeitoDescricao(String leito) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());
		criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leito));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + "."
						+ AinTiposMovimentoLeito.Fields.DESCRICAO.toString())));

		return (String) executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion18(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status,
			Short codigoLeitoOcupado, Short codigoLeitoAlta) throws ApplicationBusinessException {

		StringBuffer hql = new StringBuffer(570);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	lto.leitoID, ");
		hql.append(" 	tml.descricao,");
		hql.append(" 	tml.grupoMvtoLeito");
		hql.append(" from AinLeitos lto");
		hql.append(" join lto.tipoMovimentoLeito as tml ");
		hql.append(" join lto.unidadeFuncional as unf ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" where tml.codigo not in (:tipoMovLto1) ");
		hql.append(" and lto.indSituacao = :situacao ");

		hql.append(" and 1 <= (");
		hql.append(" select count(lt1.leitoID)");
		hql.append(" from AinLeitos lt1 ");
		hql.append(" join lt1.unidadeFuncional as unf1 ");
		hql.append(" join unf1.caracteristicas as car ");
		hql.append(" where car.id.caracteristica = :caracr");
		hql.append(" and lt1.leitoID = lto.leitoID");
		hql.append(')');

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Query query = createHibernateQuery(hql.toString());
		query.setParameterList("tipoMovLto1", new Short[] { codigoLeitoOcupado, codigoLeitoAlta });
		query.setParameter("situacao", DominioSituacao.A);
		query.setParameter("caracr", ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO);

		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	public Short obterNumeroQuartoPorLeito(String leitoID) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leitoID));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AinLeitos.Fields.QRT_NUMERO
						.toString())));
		return (Short) this.executeCriteriaUniqueResult(criteria);
	}

	private Query createPesquisaLeitosPorPacienteHql(String pesquisa, AinSolicTransfPacientes solicitacao) {

		StringBuffer hql = new StringBuffer(620);

		hql.append(" select lto ");
		hql.append(" from AinLeitos lto ");
		hql.append("  left outer join fetch lto.tipoMovimentoLeito as tml ");
		hql.append(" where lto.indSituacao = :indSituacao");
		hql.append(" 	and lto.tipoMovimentoLeito.grupoMvtoLeito = :grupoMvtoLeito ");
		hql.append(" 	and ((lto.quarto.sexoOcupacao = null or lto.quarto.sexoOcupacao = :sexoPaciente) ");
		hql.append(" 	and	 (lto.quarto.sexoDeterminante = :sexoQualquer or lto.quarto.sexoDeterminante = :sexoPaciente)) ");
		hql.append(" 	and lto.quarto.clinica.idadeMinPacInternacao <= :idadePaciente");
		hql.append(" 	and lto.quarto.clinica.idadeMaxPacInternacao >= :idadePaciente");
		if (pesquisa != null && !pesquisa.isEmpty()) {
			hql.append(" 	and (lto.leitoID like :pesquisa");
			hql.append(" 		or lto.unidadeFuncional.descricao like :pesquisa");
			hql.append(" 		or str(lto.unidadeFuncional.andar) like :pesquisa)");
		}
		hql.append(" order by lto.leitoID");

		Query query = createHibernateQuery(hql.toString());

		if (pesquisa != null && !pesquisa.isEmpty()) {
			query.setParameter("pesquisa", "%" + pesquisa.toUpperCase() + "%");
		}
		query.setParameter("indSituacao", DominioSituacao.A);
		query.setParameter("grupoMvtoLeito", DominioMovimentoLeito.L);
		query.setParameter("sexoPaciente", solicitacao.getInternacao().getPaciente().getSexo());
		query.setParameter("sexoQualquer", DominioSexoDeterminante.Q);
		query.setParameter("idadePaciente", solicitacao.getInternacao().getPaciente().getIdade());

		return query;
	}
	
	public List<AinLeitos> pesquisarLeitosDisponiveis(String pesquisa, AinSolicTransfPacientes solicitacao) {
		Query query = createPesquisaLeitosPorPacienteHql(pesquisa, solicitacao);
		@SuppressWarnings("unchecked")
		List<AinLeitos> leitos = query.list();
		return leitos;
	}
	
	public Short buscarSeqUnidadeFuncionalSeqDoLeito(String leitoID) {
		if (StringUtils.isNotBlank(leitoID)) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
			criteria.add(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), leitoID, MatchMode.EXACT));
			criteria.setProjection(Projections.property(AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));
			return (Short) executeCriteriaUniqueResult(criteria);
		}
		return null;
	}
	
	/*public List<AinLeitos> pesquisarLeitosAtivosReservados(Object strPesq, AinLeitos leitoAtual) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class, "THIS");
		criteria.createAlias("THIS." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("THIS." + AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		DetachedCriteria subQuery = DetachedCriteria.forClass(AinLeitos.class, "LTO");

		subQuery.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "TML");

		subQuery.setProjection(Projections.projectionList().add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString())));

		if (leitoAtual != null){
			Criterion c1 = Restrictions.eq("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.R);
			Criterion c2 = Restrictions.eq("LTO." + AinLeitos.Fields.LTO_ID.toString(), leitoAtual.getLeitoID());
			subQuery.add(Restrictions.or(c1, c2));
		}else{
			subQuery.add(Restrictions.eq("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.R));
		}

		subQuery.add(Restrictions.eqProperty("LTO." + AinLeitos.Fields.LTO_ID.toString(), "THIS." + AinLeitos.Fields.LTO_ID.toString()));
		criteria.add(Subqueries.exists(subQuery));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SITUACAO, DominioSituacao.A));

		if (strPesq != null && !StringUtils.isEmpty(strPesq.toString())) {
			criteria.add(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), strPesq.toString(), MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc("THIS." + AinLeitos.Fields.LTO_ID.toString()));
		return executeCriteria(criteria);
	}
	
*/
	
	public List<Object[]> pesquisaLeitosOcupados(AghClinicas clinica) {

		// Ocupados
		DetachedCriteria criOcup = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criOcup.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criOcup.createAlias("UNF." + AghUnidadesFuncionais.Fields.CLINICA.toString(), "CLN", JoinType.LEFT_OUTER_JOIN);
		criOcup.setProjection(Projections.projectionList()
				.add(Projections.property("CLN." + AghClinicas.Fields.CODIGO.toString()), "codigoClinica")
				.add(Projections.count("CLN." + AghClinicas.Fields.CODIGO.toString()), "quantidade")
				.add(Projections.groupProperty("CLN." + AghClinicas.Fields.CODIGO.toString())));
		criOcup.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criOcup.add(Restrictions.in("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), new DominioOrigemAtendimento[] {
				DominioOrigemAtendimento.I, DominioOrigemAtendimento.U }));
		if (clinica != null) {
			criOcup.add(Restrictions.eq("CLN." + AghClinicas.Fields.CODIGO, clinica.getCodigo()));
		}

		return executeCriteria(criOcup);
	}


	public List<AinLeitos> pesquisarLeitosAtivosOcupados(Object strPesq, AinLeitos leitoAtual) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class, "THIS");
		criteria.createAlias("THIS." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("QRT." + AinQuartos.Fields.CLINICA.toString(), "CLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "tml2", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("THIS." + AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		DetachedCriteria subQuery = DetachedCriteria.forClass(AinLeitos.class, "LTO");
		subQuery.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "TML");
		subQuery.setProjection(Projections.projectionList().add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString())));

		if (leitoAtual != null){
			Criterion c1 = Restrictions.eq("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.O);
			Criterion c2 = Restrictions.eq("LTO." + AinLeitos.Fields.LTO_ID.toString(), leitoAtual.getLeitoID());
			subQuery.add(Restrictions.or(c1, c2));
		}
		else{

			subQuery.add(Restrictions.eq("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.O));
		}

		subQuery.add(Restrictions.eqProperty("LTO." + AinLeitos.Fields.LTO_ID.toString(), "THIS." + AinLeitos.Fields.LTO_ID.toString()));
		criteria.add(Subqueries.exists(subQuery));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SITUACAO, DominioSituacao.A));
		if (strPesq != null && !StringUtils.isEmpty(strPesq.toString())) {
			criteria.add(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), strPesq.toString(), MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc("THIS." + AinLeitos.Fields.LTO_ID.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AinLeitos> pesquisarLeitosAtivosDesocupados(Object strPesq, AinLeitos leitoAtual) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class, "THIS");

		criteria.createAlias("THIS." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("QRT." + AinQuartos.Fields.CLINICA.toString(), "CLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "tml2", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("THIS." + AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));

		DetachedCriteria subQuery = DetachedCriteria.forClass(AinLeitos.class, "LTO");
		subQuery.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "TML");
		subQuery.setProjection(Projections.projectionList().add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString())));
		
		if (leitoAtual != null){
			Criterion c1 = Restrictions.eq("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.L);
			Criterion c2 = Restrictions.eq("LTO." + AinLeitos.Fields.LTO_ID.toString(), leitoAtual.getLeitoID());
			subQuery.add(Restrictions.or(c1, c2));
		}else{
			subQuery.add(Restrictions.eq("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.L));
		}
		
		if (strPesq != null && !StringUtils.isEmpty(strPesq.toString())) {
			criteria.add(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), strPesq.toString(), MatchMode.ANYWHERE));
		}
		
		subQuery.add(Restrictions.eqProperty("LTO." + AinLeitos.Fields.LTO_ID.toString(), "THIS." + AinLeitos.Fields.LTO_ID.toString()));
		criteria.add(Subqueries.exists(subQuery));
		
		return executeCriteria(criteria);
	}
	
	public List<AinLeitos> pesquisarLeitosAtivosReservados(Object strPesq, AinLeitos leitoAtual) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class, "THIS");
		criteria.createAlias("THIS." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("QRT." + AinQuartos.Fields.CLINICA.toString(), "CLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "tml2", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("THIS." + AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		DetachedCriteria subQuery = DetachedCriteria.forClass(AinLeitos.class, "LTO");
		subQuery.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "TML");
		subQuery.setProjection(Projections.projectionList().add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString())));

		if (leitoAtual != null){
			Criterion c1 = Restrictions.eq("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.R);
			Criterion c2 = Restrictions.eq("LTO." + AinLeitos.Fields.LTO_ID.toString(), leitoAtual.getLeitoID());
			subQuery.add(Restrictions.or(c1, c2));
		}
		else{
			subQuery.add(Restrictions.eq("TML." + AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), DominioMovimentoLeito.R));
		}

		subQuery.add(Restrictions.eqProperty("LTO." + AinLeitos.Fields.LTO_ID.toString(), "THIS." + AinLeitos.Fields.LTO_ID.toString()));
		criteria.add(Subqueries.exists(subQuery));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SITUACAO, DominioSituacao.A));

		if (strPesq != null && !StringUtils.isEmpty(strPesq.toString())) {
			criteria.add(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), strPesq.toString(), MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc("THIS." + AinLeitos.Fields.LTO_ID.toString()));
		return executeCriteria(criteria);
	}

	
	public List<AinLeitos> pesquisarAtivosConcedidos(Object strPesq, Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class, "SOL");

		// PROJECTION
		criteria.setProjection(Projections.projectionList().add(
				Projections.property("SOL." + AinSolicTransfPacientes.Fields.LEITO.toString())));

		criteria.createAlias("SOL." + AinSolicTransfPacientes.Fields.LEITO.toString(), "THIS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("THIS." + AinLeitos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("THIS." + AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SITUACAO, DominioSituacao.A));

		if (strPesq != null && !StringUtils.isEmpty(strPesq.toString())) {
			criteria.add(Restrictions.ilike("THIS." + AinLeitos.Fields.LTO_ID.toString(), strPesq.toString(), MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq("SOL." + AinSolicTransfPacientes.Fields.INTERNACAO_SEQ.toString(), intSeq));
		criteria.add(Restrictions.eq("SOL." + AinSolicTransfPacientes.Fields.IND_SITUACAO.toString(),
				DominioSituacaoSolicitacaoInternacao.A));

		return executeCriteria(criteria);
	}

	public Short buscarMaxSeqUnidadeFuncionalSeqDoLeito(String leitoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.setProjection(Projections.max(AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));
		criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leitoId));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AinLeitos> pesquisarLeitosOcupadosOuReservadosPorNumeroQuartoLeitoId(Short numeroQuarto, String paramLeitoId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		criteria.add(Restrictions.eq(AinLeitos.Fields.QRT_NUMERO.toString(), numeroQuarto));
		if (paramLeitoId != null) {
			criteria.add(Restrictions.ne(AinLeitos.Fields.LTO_ID.toString(), paramLeitoId));
		}
		DominioMovimentoLeito[] gruposMvtoLeito = new DominioMovimentoLeito[] { DominioMovimentoLeito.O, DominioMovimentoLeito.R };
		DetachedCriteria criteriaTML = criteria.createCriteria(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());
		criteriaTML.add(Restrictions.in(AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), gruposMvtoLeito));
		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<RegistraExtratoLeitoVO> pesquisarRegistraExtratoLeitoVO(String leitoID, List<Short> codigosMovimento) {

		StringBuffer hql = new StringBuffer(330);

		hql.append(" select new br.gov.mec.aghu.internacao.business.vo.RegistraExtratoLeitoVO (");
		hql.append(" 	lto2.leitoID, lto2.indBloqLeitoLimpeza ");
		hql.append(" ) ");
		hql.append(" from ");
		hql.append(" 	AinLeitos lto2 join lto2.tipoMovimentoLeito as tml, ");
		hql.append(" 	AinLeitos lto1 ");
		hql.append(" where lto1.leitoID = :leitoID1 ");
		hql.append(" 	and lto2.quarto = lto1.quarto ");
		hql.append(" 	and (lto2.leitoID = :leitoID2 or tml.codigo in (:codigosMovimento)) ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("leitoID1", leitoID);
		query.setParameter("leitoID2", leitoID);
		query.setParameterList("codigosMovimento", codigosMovimento);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AinLeitos> pesquisarLeitosAtivosDesocupados(String strPesquisa) {
		StringBuilder sql = new StringBuilder(300);
		sql.append(" select distinct lt ");
		sql.append(" from AinLeitos lt ");
		sql.append(" 	join lt.unidadeFuncional as uf ");
		sql.append(" 	left join lt.tipoMovimentoLeito as tml ");
		sql.append(" where lt.indSituacao = :situacao ");
		sql.append(" and tml.grupoMvtoLeito = :grupoMvtoLeito ");

		if (StringUtils.isNotBlank(strPesquisa)) {
			sql.append(" and ( ");
			sql.append(" 	lower(lt.leitoID) like lower('%' || :leitoID || '%') ");
			sql.append(" 	OR ");
			sql.append(" 	lower('0' || uf.andar || ' ' || uf.indAla || ' - ' || uf.descricao) like lower('%' || :strPesquisa || '%') ");
			sql.append(" ) ");
		}

		sql.append(" order by lt.leitoID ");

		javax.persistence.Query query = this.createQuery(sql.toString());
		query.setParameter("situacao", DominioSituacao.A);
		query.setParameter("grupoMvtoLeito", DominioMovimentoLeito.L);

		if (StringUtils.isNotBlank(strPesquisa)) {
			query.setParameter("leitoID", strPesquisa);
			query.setParameter("strPesquisa", strPesquisa);
		}

		query.setFirstResult(0);
		query.setMaxResults(25);

		return query.getResultList();
	}

	/**
	 * Monta o Criteria para a consulta de leitos desocupados somente pelo
	 * codigo do Leito
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaLeitosDesocupadosPorLeito(String parametro) {

		DetachedCriteria criteriaLeitos = DetachedCriteria.forClass(AinLeitos.class);

		criteriaLeitos.createAlias(
				AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(),
				AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());

		criteriaLeitos.add(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(),parametro, MatchMode.ANYWHERE));

		criteriaLeitos.add(Restrictions.eq(
				AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString()
						+ "."
						+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO
								.toString(), DominioMovimentoLeito.L));

		criteriaLeitos.addOrder(Order.asc(AinLeitos.Fields.LTO_ID.toString()));

		return criteriaLeitos;
	}
	
	public AinLeitos obterLeitoPorId(String leitoId){
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		
		criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leitoId));
		
		return (AinLeitos) executeCriteriaUniqueResult(criteria);
	}

	public List<AinLeitos> pesquisarLeitosDesocupadosPorLeito(String stParametro) {
		List<AinLeitos> resultadoPesquisa;

		DetachedCriteria criteria = obterCriteriaPesquisaLeitosDesocupadosPorLeito(stParametro);

		resultadoPesquisa = this.executeCriteria(criteria);

		return resultadoPesquisa;
	}

	private DetachedCriteria obterCriteriaPeloLeitoId(String leito) {
		DetachedCriteria criteriaLeitos = DetachedCriteria.forClass(AinLeitos.class);
		
		if(StringUtils.isNotBlank(leito)){
			criteriaLeitos.add(Restrictions.like(AinLeitos.Fields.LTO_ID.toString(), leito.toUpperCase(), MatchMode.ANYWHERE));
		}
		
		return criteriaLeitos;
	}

	public List<AinLeitos> pesquisaLeitoPeloLeitoId(String leito) {
		DetachedCriteria criteria = obterCriteriaPeloLeitoId(leito);

		criteria.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), "TML", JoinType.INNER_JOIN);
		criteria.createAlias(AinLeitos.Fields.QUARTO.toString(), "QRT", JoinType.INNER_JOIN);
		criteria.createAlias("QRT."+AinQuartos.Fields.ACOMODACAO.toString(), "ACO", JoinType.INNER_JOIN);
		criteria.createAlias("QRT."+AinQuartos.Fields.CLINICA.toString(), "CLI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("QRT."+AinQuartos.Fields.ALA.toString(), "ALA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc(AinLeitos.Fields.LTO_ID.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * 
	 * @param paramentro
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaLeitosBloqueados(String paramentro, String andar, AghAla alaParametro) {
		List<DominioMovimentoLeito> listaLeitos = new ArrayList<DominioMovimentoLeito>();
		listaLeitos.add(DominioMovimentoLeito.B);
		listaLeitos.add(DominioMovimentoLeito.BI);
		listaLeitos.add(DominioMovimentoLeito.BL);
		listaLeitos.add(DominioMovimentoLeito.R);
		listaLeitos.add(DominioMovimentoLeito.I);
		listaLeitos.add(DominioMovimentoLeito.D);
		
		DetachedCriteria criteriaLeitos = DetachedCriteria.forClass(AinLeitos.class);

		criteriaLeitos.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), AinLeitos.Fields.UNIDADE_FUNCIONAL.toString());
		criteriaLeitos.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString()+"."+AghUnidadesFuncionais.Fields.ALA.toString(), AghUnidadesFuncionais.Fields.ALA.toString());

		criteriaLeitos.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());

		criteriaLeitos.add(Restrictions.in(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + "."
				+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), listaLeitos));

		criteriaLeitos.add(Restrictions.or(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), paramentro, MatchMode.ANYWHERE),
				Restrictions.or(Restrictions.eq(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "."
						+ AghUnidadesFuncionais.Fields.ANDAR.toString(), andar), Restrictions.or(
						Restrictions.eq(
								AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.ALA.toString(),
								alaParametro), Restrictions.ilike(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "."
								+ AghUnidadesFuncionais.Fields.DESCRICAO.toString(), paramentro, MatchMode.ANYWHERE)))));

		criteriaLeitos.addOrder(Order.asc(AinLeitos.Fields.LTO_ID.toString()));

		return criteriaLeitos;
	}


	/**
	 * Retorna leito desocupado.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public AinLeitos obterLeitoDesocupado(String leito) {

		DetachedCriteria criteriaLeitos = DetachedCriteria
				.forClass(AinLeitos.class);

		criteriaLeitos.createAlias(
				AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(),
				AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());

		criteriaLeitos.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(),
				leito));

		criteriaLeitos.add(Restrictions.eq(
				AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString()
						+ "."
						+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO
								.toString(), DominioMovimentoLeito.L));

		return (AinLeitos) executeCriteriaUniqueResult(criteriaLeitos);
	}

	/**
	 * 
	 * @param paramentro
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaLeitosDesocupados(String paramentro, String andar, AghAla alaParametro, DominioMovimentoLeito[] situacoesLeito) {
		DetachedCriteria criteriaLeitos = DetachedCriteria.forClass(AinLeitos.class);

		criteriaLeitos.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), AinLeitos.Fields.UNIDADE_FUNCIONAL.toString());
		criteriaLeitos.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString()+"."+AghUnidadesFuncionais.Fields.ALA.toString(), AghUnidadesFuncionais.Fields.ALA.toString());
		criteriaLeitos.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());

		
		criteriaLeitos.add(Restrictions.in(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + "."
				+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), situacoesLeito));

		criteriaLeitos.add(Restrictions.or(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), paramentro, MatchMode.ANYWHERE),
				Restrictions.or(Restrictions.eq(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "."
						+ AghUnidadesFuncionais.Fields.ANDAR.toString(), andar), Restrictions.or(
						Restrictions.eq(
								AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.ALA.toString(),
								alaParametro), Restrictions.ilike(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "."
								+ AghUnidadesFuncionais.Fields.DESCRICAO.toString(), paramentro, MatchMode.ANYWHERE)))));

		criteriaLeitos.addOrder(Order.asc(AinLeitos.Fields.LTO_ID.toString()));

		return criteriaLeitos;
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosDesocupados(String paramentro, String andar, AghAla alaParametro) {
		DominioMovimentoLeito[] situacoesLeito = {DominioMovimentoLeito.L};
		DetachedCriteria criteria = obterCriteriaPesquisaLeitosDesocupados(paramentro, andar, alaParametro, situacoesLeito);
		return this.executeCriteria(criteria);
	}
	
	public List<AinLeitos> pesquisarLeitosPorSituacoesDoLeito(String paramentro, String andar, AghAla alaParametro, DominioMovimentoLeito[] situacoesLeito) {
		DetachedCriteria criteria = obterCriteriaPesquisaLeitosDesocupados(paramentro, andar, alaParametro, situacoesLeito);
		return this.executeCriteria(criteria);
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public AinLeitos obterLeitoBloqueado(String leito) {

		DetachedCriteria criteriaLeitos = DetachedCriteria.forClass(AinLeitos.class);

		criteriaLeitos.createAlias(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString(), AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString());

		List<DominioMovimentoLeito> listaLeitos = new ArrayList<DominioMovimentoLeito>();
		listaLeitos.add(DominioMovimentoLeito.B);
		listaLeitos.add(DominioMovimentoLeito.BI);
		listaLeitos.add(DominioMovimentoLeito.BL);
		listaLeitos.add(DominioMovimentoLeito.R);
		listaLeitos.add(DominioMovimentoLeito.I);
		listaLeitos.add(DominioMovimentoLeito.D);

		criteriaLeitos.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leito));

		criteriaLeitos.add(Restrictions.in(AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + "."
				+ AinTiposMovimentoLeito.Fields.GRUPO_MOVIMENTO_LEITO.toString(), listaLeitos));

		return (AinLeitos) executeCriteriaUniqueResult(criteriaLeitos);
	}

	/**
	 * Retorna lista de leitos desocupados.
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosBloqueados(String paramentro, String andar, AghAla alaParametro) {
		List<AinLeitos> resultadoPesquisa;

		DetachedCriteria criteria = obterCriteriaPesquisaLeitosBloqueados(paramentro, andar, alaParametro);

		resultadoPesquisa = this.executeCriteria(criteria);

		return resultadoPesquisa;
	}

	private DetachedCriteria obterCriteriaPesquisaLeitosOrdenado(String parametro) {

		DetachedCriteria criteriaLeitos = DetachedCriteria.forClass(AinLeitos.class);

		if (parametro != null && StringUtils.isNotBlank(parametro)) {
			criteriaLeitos.add(Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), parametro,MatchMode.ANYWHERE));
		}
		criteriaLeitos.addOrder(Order.asc(AinLeitos.Fields.LTO_ID.toString()));

		return criteriaLeitos;
	}

	/**
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param paramentro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosOrdenado(Object parametro) {
		DetachedCriteria criteria = obterCriteriaPesquisaLeitosOrdenado((String) parametro);
		return this.executeCriteria(criteria);
	}
	
	public List<AinLeitos> pesquisarLeitos(String parametro, String andar, AghAla alaPametro) {
		DetachedCriteria criteria = obterCriteriaPesquisaLeitos(parametro, andar, alaPametro);
		return this.executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisaLeitos(String parametro, String andar, AghAla alaPametro) {
		DetachedCriteria criteriaLeitos = DetachedCriteria.forClass(AinLeitos.class);

		criteriaLeitos.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), JoinType.LEFT_OUTER_JOIN);
		criteriaLeitos.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.ALA.toString(),
				 AghUnidadesFuncionais.Fields.ALA.toString(), JoinType.LEFT_OUTER_JOIN);
		
		//Ajuste para retornar apenas leitos ativos e de unidades funcionais também ativas
		criteriaLeitos.add(Restrictions.eq(	AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteriaLeitos.add(Restrictions.eq(AinLeitos.Fields.UNIDADE_FUNCIONAL
				.toString()	+ "." + AghUnidadesFuncionais.Fields.SITUACAO.toString(),DominioSituacao.A));

		criteriaLeitos
				.add(Restrictions.or(
						Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(),parametro, MatchMode.ANYWHERE),
						Restrictions.or(
							Restrictions.eq(
								AinLeitos.Fields.UNIDADE_FUNCIONAL
										.toString()	+ "."
										+ AghUnidadesFuncionais.Fields.ANDAR.toString(), andar),
							Restrictions.or(
								Restrictions
									.eq(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString() 	+ "."
											+ AghUnidadesFuncionais.Fields.ALA.toString(),	alaPametro),
								Restrictions
									.ilike(AinLeitos.Fields.UNIDADE_FUNCIONAL
											.toString()	+ "."+ AghUnidadesFuncionais.Fields.DESCRICAO.toString(),
											parametro, MatchMode.ANYWHERE)))));
		return criteriaLeitos;
	}
	
	/**
	 * ORADB V_AIN_SITUACAO_LEITOS
	 * 
	 * @param clinica
	 * @return
	 */
	public SituacaoLeitosVO pesquisaCapacidadeInstaladaLeitos(AghClinicas clinica) {
		DetachedCriteria cri = DetachedCriteria
				.forClass(AinLeitos.class, "LTO");

		cri.createAlias("LTO." + AinLeitos.Fields.QUARTO.toString(), "QRT",	JoinType.INNER_JOIN);
		cri.createAlias("QRT." + AinQuartos.Fields.CLINICA.toString(), "CLN", JoinType.INNER_JOIN);

		cri.setProjection(
				Projections.projectionList()
				.add(Projections.count("CLN." + AghClinicas.Fields.CODIGO.toString()), "quantidade")
				.add(Projections.groupProperty("CLN." + AghClinicas.Fields.CODIGO.toString())));

		cri.add(Restrictions.eq("LTO." + AinLeitos.Fields.SITUACAO.toString(),DominioSituacao.A));
		cri.add(Restrictions.eq("CLN." + AghClinicas.Fields.CODIGO,	clinica.getCodigo()));

		List<Object[]> ocupados = this.executeCriteria(cri);
		SituacaoLeitosVO situacaoLeito = new SituacaoLeitosVO();
		situacaoLeito.setQuantidade(0);
		
		if(ocupados.size() > 0){
			Iterator<Object[]> it = ocupados.iterator();
			Object[] ocupado = it.next();			
			if (ocupado[0] != null) {
				situacaoLeito.setQuantidade(Integer.valueOf(ocupado[0].toString()));
			}
		}		
		
		return situacaoLeito;
	}

	private static final String LEITO = "LEITO";
	private static final String UNF = "UNF";
	private static final String ESP = "ESP";

	private	static final String SQL_PROJECTION_CONTA_CARACTERISTICAS = montaSQLProjectionContaCaracteristicas();
	
	private static String montaSQLProjectionContaCaracteristicas() {
		return "(select count(*) from "+AinCaracteristicaLeito.class.getAnnotation(Table.class).schema() + '.'+AinCaracteristicaLeito.class.getAnnotation(Table.class).name() + " CTA "+
				" where CTA."+AinCaracteristicaLeito.Fields.LTO_LTO_ID.name() + " = {alias}."+AinLeitos.Fields.LTO_ID.name()+ ") as "+AinLeitosVO.Fields.QT_CARACTERISTICAS.toString();
	}
	
	public List<AinLeitosVO> pesquisaLeitosPorNroQuarto(Short nroQuarto) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class,LEITO);		
		
		criteria.add(Restrictions.eq(LEITO+'.'+AinLeitos.Fields.QRT_NUMERO.toString(), nroQuarto));
		criteria.createAlias(LEITO+'.'+AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), UNF, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(LEITO+'.'+AinLeitos.Fields.ESPECIALIDADE.toString(), ESP, JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
									.add(Projections.property(LEITO+'.'+AinLeitos.Fields.LTO_ID.toString()),AinLeitosVO.Fields.LEITO_ID.toString())
									.add(Projections.property(LEITO+'.'+AinLeitos.Fields.LEITO.toString()),AinLeitosVO.Fields.LEITO.toString())
									.add(Projections.property(UNF+'.'+AghUnidadesFuncionais.Fields.DESCRICAO.toString()),AinLeitosVO.Fields.DESCRICAO_UNIDADE.toString())
									.add(Projections.property(ESP+'.'+AghEspecialidades.Fields.NOME.toString()),AinLeitosVO.Fields.NOME_ESPECIALIDADE.toString())
									.add(Projections.property(LEITO+'.'+AinLeitos.Fields.IND_CONS_CLIN_UNIDADE.toString()),AinLeitosVO.Fields.IND_CONS_CLIN_UNIDADE.toString())
									.add(Projections.property(LEITO+'.'+AinLeitos.Fields.IND_CONS_ESP.toString()),AinLeitosVO.Fields.IND_CONS_ESP.toString())
									.add(Projections.property(LEITO+'.'+AinLeitos.Fields.IND_BLOQ_LEITO_LIMPEZA.toString()),AinLeitosVO.Fields.IND_BLOQ_LEITO_LIMPEZA.toString())
									.add(Projections.property(LEITO+'.'+AinLeitos.Fields.IND_PERTENCE_REFL.toString()),AinLeitosVO.Fields.IND_PERTENCE_REFL.toString())
									.add(Projections.property(LEITO+'.'+AinLeitos.Fields.SITUACAO.toString()),AinLeitosVO.Fields.SITUACAO.toString())
									
									.add(Projections.sqlProjection(SQL_PROJECTION_CONTA_CARACTERISTICAS , new String[]{AinLeitosVO.Fields.QT_CARACTERISTICAS.toString()}, new Type[]{IntegerType.INSTANCE}))
								);
		
		criteria.addOrder(Order.asc(LEITO+'.'+AinLeitos.Fields.LEITO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(AinLeitosVO.class));
		
		return this.executeCriteria(criteria);
	}
	
	public boolean existeLeitoID(String ltoID){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class,LEITO);
		criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), ltoID));
		
		return executeCriteriaCount(criteria) > Long.valueOf(0);
	}

	public List<AinLeitos> pesquisaAinLeitosPorNroQuarto(Short nroQuarto) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class,LEITO);		
		criteria.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UMD_FUNC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinLeitos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinLeitos.Fields.CARACTERISTICAS.toString(), "CARAC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(LEITO+'.'+AinLeitos.Fields.QRT_NUMERO.toString(), nroQuarto));
		criteria.addOrder(Order.asc(LEITO+'.'+AinLeitos.Fields.LEITO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #2199 - F1
	 * @ORADB FATC_BUSCA_LTO_CONTA
	 * @param cthSeq
	 * @return
	 */
	public Short buscarUnidadeFuncional(Integer cthSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "INT");	
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(FatContasInternacao.class, "CONTA");
		
		criteria.createAlias("INT."+AinInternacao.Fields.LEITO.toString(), LEITO);
		criteria.createAlias("INT."+ AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(), "FAT");
		criteria.add(Restrictions.eq("FAT."+FatContasInternacao.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Subqueries.propertyEq("INT." + AinInternacao.Fields.SEQ.toString(), subCriteria));
		
		
		criteria.setProjection(Projections.projectionList().add(Projections.property(LEITO+'.'+ AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString())));
		

		
		subCriteria.add(Restrictions.eq("CONTA."+FatContasInternacao.Fields.CTH_SEQ.toString(), cthSeq));
		
		subCriteria.setProjection(Projections.projectionList().add(Projections.max("CONTA."+FatContasInternacao.Fields.INT_SEQ)));
		
		return (Short) executeCriteriaUniqueResult(criteria) ;
	}
	
	public List<AinLeitos> pesquisarLeitosPorUnidadeFuncional(Object param, Short unfSeq) {
		DetachedCriteria criteria = montarCriteriaPesquisaLeitosPorUnidadeFuncional(param, unfSeq);
		criteria.addOrder(Order.asc(LEITO + '.' + AinLeitos.Fields.LTO_ID.toString()));
		return this.executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarLeitosPorUnidadeFuncionalCount(Object param, Short unfSeq) {
		return executeCriteriaCount(montarCriteriaPesquisaLeitosPorUnidadeFuncional(param, unfSeq));
	}
	
	public DetachedCriteria montarCriteriaPesquisaLeitosPorUnidadeFuncional(Object param, Short unfSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class, LEITO);
		criteria.createAlias(LEITO + "." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), UNF);
		
		if (param != null && StringUtils.isNotBlank(param.toString())) {
			criteria.add(Restrictions.ilike(LEITO + "." + AinLeitos.Fields.LTO_ID.toString(), param.toString(), MatchMode.ANYWHERE));
		}
 		
		if (unfSeq != null) {
			criteria.add(Restrictions.eq(LEITO + "." + AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}
		return criteria;
	}

	public Boolean verificarLeitoExiste(String idLeito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		if(StringUtils.isNotBlank(idLeito)) {
			criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), idLeito));
		}
		return executeCriteriaExists(criteria);
	}
	
	public List<AinLeitos> pesquisarLeitosPorSeqUnf(List<Short> unfs, String leito) {
		DetachedCriteria criteria = criarCriteriaPesquisarLeitosPorSeqUnf(unfs, leito);
		criteria.addOrder(Order.asc(AinLeitos.Fields.LTO_ID.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria criarCriteriaPesquisarLeitosPorSeqUnf(
			List<Short> unfs, String leito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		
		if(unfs!= null && !unfs.isEmpty()) {
			criteria.add(Restrictions.in(AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfs));
		}		
		criteria.add(Restrictions.eq(AinLeitos.Fields.SITUACAO.toString(),DominioSituacao.A));
		if(StringUtils.isNotBlank(leito)) {
			Criterion c1 = Restrictions.ilike(AinLeitos.Fields.LEITO.toString(), leito, MatchMode.ANYWHERE);
			Criterion c2 = Restrictions.ilike(AinLeitos.Fields.LTO_ID.toString(), leito, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(c1, c2));
		}
		return criteria;
	}

	public Long pesquisarLeitosPorSeqUnfCount(List<Short> unfs, String leito) {
		DetachedCriteria criteria = criarCriteriaPesquisarLeitosPorSeqUnf(unfs, leito);
		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * Estoria #5302 SB01
	 * @param param
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitosPorUnidadeFuncionalSalaQuarto(Object param){
		DetachedCriteria criteria = montarPesquisarLeitosPorUnidadeFuncionalSalaQuarto(param);
		criteria.addOrder(Order.asc("QATO."+ AinQuartos.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("LEITO."+ AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()));
		criteria.addOrder(Order.asc(LEITO + "." + AinLeitos.Fields.LTO_ID.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long countPesquisarLeitosPorUnidadeFuncionalSalaQuarto(Object param){
		DetachedCriteria criteria = montarPesquisarLeitosPorUnidadeFuncionalSalaQuarto(param);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarPesquisarLeitosPorUnidadeFuncionalSalaQuarto(
			Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class, "LEITO");
		criteria.createAlias("LEITO."+ AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("LEITO."+AinLeitos.Fields.QUARTO.toString(), "QATO");
		
		Short quarto=null;
		Short unfSeq=null;
		Disjunction or = Restrictions.disjunction();
		if (param != null && StringUtils.isNotBlank(param.toString())) {
			or.add(Restrictions.ilike(LEITO + "." + AinLeitos.Fields.LTO_ID.toString(), param.toString(), MatchMode.ANYWHERE));
		}
 		
		if (CoreUtil.isNumeroShort(param)) {
			quarto = Short.valueOf((String) param);
			unfSeq = Short.valueOf((String) param);
			or.add(Restrictions.or(Restrictions.eq("LEITO."+ AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq),Restrictions.eq("QATO."+ AinQuartos.Fields.NUMERO.toString(), quarto)));
		}
		criteria.add(or);
		return criteria;
	}
	
	public AinLeitos obterLeitoQuartoUnidadeFuncionalPorId(String leitoId){
		DetachedCriteria criteria = DetachedCriteria.forClass(AinLeitos.class);
		
		criteria.add(Restrictions.eq(AinLeitos.Fields.LTO_ID.toString(), leitoId));
		criteria.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias(AinLeitos.Fields.QUARTO.toString(), "QATO");
		
		return (AinLeitos) executeCriteriaUniqueResult(criteria);
	}
	
	
	public List<AinLeitos> getLeitosPorQuarto(AinQuartos quarto) {
		if (quarto == null || quarto.getNumero() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!!");
		}
		DetachedCriteria criteriaLeitosPorQuarto = DetachedCriteria.forClass(AinLeitos.class);
		criteriaLeitosPorQuarto.add(Restrictions.eq(AinLeitos.Fields.QUARTO.toString(), quarto));

		return this.executeCriteria(criteriaLeitosPorQuarto);

	}
		
	public List<AinLeitos> obterListaLeitosPorSeqInternacao(AinInternacao internacao) {
		DetachedCriteria listaLeitosPorSeqInternacao = DetachedCriteria.forClass(AinLeitos.class);	
		listaLeitosPorSeqInternacao.add(Restrictions.eq(AinLeitos.Fields.INTERNACAO.toString(), internacao));
		return this.executeCriteria(listaLeitosPorSeqInternacao);
		}

}
