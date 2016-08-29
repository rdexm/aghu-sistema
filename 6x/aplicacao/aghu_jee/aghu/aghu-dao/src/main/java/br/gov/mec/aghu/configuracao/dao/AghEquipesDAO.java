package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.MamConsultoriaAmbEquipe;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * 
 */
public class AghEquipesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghEquipes> {


	private static final long serialVersionUID = -6748122738133172947L;

	public List<AghEquipes> pesquisarPorNomeOuCodigo(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	public List<AghEquipes> pesquisarPorNomeCodigoAtiva(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(),
				DominioSituacao.A));
		criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	
	
	public List<AghEquipes> pesquisarEquipeAtiva(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(),
				DominioSituacao.A));
		return executeCriteria(criteria, 0, 100, AghEquipes.Fields.NOME.toString(), true);
	}
	
	public Long pesquisarEquipeAtivaCount(String parametro){
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(),
				DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	
	public List<AghEquipes> pesquisarEquipeAtivaCO(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AghEquipes.Fields.PLACAR_RISCO_NEONATAL.toString(), DominioSimNao.S));
		criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, AghEquipes.Fields.NOME.toString(), true);
	}
	
	public Long pesquisarEquipeAtivaCOCount(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaNomeOuCodigo(parametro);
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AghEquipes.Fields.PLACAR_RISCO_NEONATAL.toString(), DominioSimNao.S));
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaParaNomeOuCodigo(String parametro) {
		String nomeOuCodigo = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class);
		if (StringUtils.isNotEmpty(nomeOuCodigo)) {
			int codigo = -1;
			if (CoreUtil.isNumeroInteger(nomeOuCodigo)){
				codigo = Integer.parseInt(nomeOuCodigo);
			}			
			if (codigo != -1) {
				criteria.add(Restrictions.eq(
						AghEquipes.Fields.CODIGO.toString(), codigo));
			} else {
				criteria.add(Restrictions.ilike(
						AghEquipes.Fields.NOME.toString(), nomeOuCodigo,
						MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	public Long pesquisaEquipesCount(Integer codigo, String nome,
			RapServidoresVO profissionalResponsavel, DominioSituacao ativo,
			DominioSimNao placarRiscoNeonatal) {
		return executeCriteriaCount(createPesquisaEquipesCriteria(codigo, nome,
				profissionalResponsavel, ativo, placarRiscoNeonatal));
	}

	public List<AghEquipes> pesquisarEquipes(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String nome,
			RapServidoresVO profissionalResponsavel, DominioSituacao ativo,
			DominioSimNao placarRiscoNeonatal) {
		DetachedCriteria criteria = createPesquisaEquipesCriteria(codigo, nome,
				profissionalResponsavel, ativo, placarRiscoNeonatal);
		criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	private DetachedCriteria createPesquisaEquipesCriteria(Integer codigo,
			String nome, RapServidoresVO profissionalResponsavel,
			DominioSituacao ativo, DominioSimNao placarRiscoNeonatal) {
		
		String aliasSer = "ser";
		String aliasPes = "pes";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class);
		
		criteria.createAlias(AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), aliasSer, JoinType.INNER_JOIN);
		criteria.createAlias(aliasSer + ponto + RapServidores.Fields.PESSOA_FISICA.toString(), aliasPes, JoinType.INNER_JOIN);
		
		if (codigo != null) {
			criteria.add(Restrictions.eq(AghEquipes.Fields.CODIGO.toString(),codigo));
		}

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(AghEquipes.Fields.NOME.toString(),nome, MatchMode.ANYWHERE));
		}

		if (ativo != null) {
			criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(),ativo));
		}

		if (placarRiscoNeonatal != null) {
			criteria.add(Restrictions.eq(AghEquipes.Fields.PLACAR_RISCO_NEONATAL.toString(),placarRiscoNeonatal));
		}

		if (profissionalResponsavel != null) {
			criteria.add(Restrictions.eq(AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString() + "."+ RapServidores.Fields.CODIGO_VINCULO.toString(),
												profissionalResponsavel.getVinculo()));
			
			criteria.add(Restrictions.eq(AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString() + "." + RapServidores.Fields.MATRICULA.toString(),
												profissionalResponsavel.getMatricula()));
		}

		return criteria;
	}
	
	public AghEquipes obterAghEquipesComRapServidores(final AghEquipes equipe){
		return obterPorChavePrimaria(equipe.getSeq(), AghEquipes.Fields.RAP_SERVIDORES);
	}
	

	private DetachedCriteria criarCriteriaEquipes() {
		DetachedCriteria cri = DetachedCriteria.forClass(AghEquipes.class, "eqp");
		cri.createAlias("eqp."+AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL, "pre", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("pre."+RapServidores.Fields.PESSOA_FISICA, "pfi", JoinType.LEFT_OUTER_JOIN);

		cri.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));

		return cri;
	}

	public List<AghEquipes> pesquisarAtivasPorNomeOuCodigo(Object paramPesquisa, int quantidadeParaRetorno) {
		return pesquisarPorNomeOuCodigo((String) paramPesquisa, true, quantidadeParaRetorno);
	}

	public List<AghEquipes> pesquisarPorNomeOuCodigo(Object paramPesquisa, int quantidadeParaRetorno) {
		return pesquisarPorNomeOuCodigo((String) paramPesquisa, false, quantidadeParaRetorno);
	}

	@SuppressWarnings("unchecked")
	private List<AghEquipes> pesquisarPorNomeOuCodigo(String strPesquisa,
			boolean apenasAtivas, int quantidadeParaRetorno) {

		DetachedCriteria cri = null;

		LinkedHashSet<AghEquipes> li = new LinkedHashSet<AghEquipes>();

		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			cri = criarCriteriaEquipes();

			cri.add(Restrictions.eq(AghEquipes.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));

			if (apenasAtivas) {
				cri.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(),
						DominioSituacao.A));
			}

			List<AghEquipes> objList = executeCriteria(cri, 0, quantidadeParaRetorno, null, false);
			li.addAll(objList);

			if (li.size() > 0) {
				return new ArrayList<AghEquipes>(li);
			}
		}

		cri = criarCriteriaEquipes();
		if (StringUtils.isNotBlank(strPesquisa)) {
			cri.add(Restrictions.ilike(AghEquipes.Fields.NOME.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		if (apenasAtivas) {
			cri.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(),
					DominioSituacao.A));
		}

		List<AghEquipes> objList = executeCriteria(cri, 0, quantidadeParaRetorno, null, false);
		li.addAll(objList);

		return new ArrayList<AghEquipes>(li);

	}

	public List<AghEquipes> pesquisarEquipesPorNomeOuDescricao(String seqDesc) {

		DetachedCriteria criteria = createPesquisaCriteria(seqDesc);

		return executeCriteria(criteria);

	}

	/**
	 * Método auxiliar que cria DetachedCriteria a partir de parâmetros.
	 * 
	 * @param codigo
	 *            ou descrição.
	 * @return DetachedCriteria.
	 */
	private DetachedCriteria createPesquisaCriteria(String seqDesc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class);

		if (StringUtils.isNotBlank(seqDesc)) {
			if (CoreUtil.isNumeroInteger(seqDesc)) {
				criteria.add(Restrictions.eq(
						AghEquipes.Fields.CODIGO.toString(),
						Integer.parseInt(seqDesc)));
			} else {
				criteria.add(Restrictions.ilike(
						AghEquipes.Fields.NOME.toString(), seqDesc,
						MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}
	
	public List<AghEquipes> pesquisarEquipeRespLaudoAih(Long seq, RapServidores servidorRespInternacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class, "EQU");
		
		criteria.add(Restrictions.eq("EQU.".concat(AghEquipes.Fields.RAP_SERVIDORES_ID_VIN_CODIGO.toString()), servidorRespInternacao.getId().getVinCodigo()));
		criteria.add(Restrictions.eq("EQU.".concat(AghEquipes.Fields.RAP_SERVIDORES_ID_MATRICULA.toString()), servidorRespInternacao.getId().getMatricula()));
		
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.addOrder(Order.desc(AghEquipes.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}

	/**
	 * Monta a criteria de Equipes ativas do CO
	 * 
	 * Web Service #38731
	 * 
	 * @return
	 */
	private DetachedCriteria montarCriteriaEquipesAtivasDoCO() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class);
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AghEquipes.Fields.PLACAR_RISCO_NEONATAL.toString(), DominioSimNao.S));
		return criteria;
	}

	/**
	 * Buscar as Equipes ativas do CO
	 * 
	 * Web Service #38731
	 * 
	 * @return
	 */
	public List<AghEquipes> pesquisarEquipesAtivasDoCO() {
		DetachedCriteria criteria = this.montarCriteriaEquipesAtivasDoCO();
		criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Buscar a equipe sendo informado matícula e vínculo
	 * 
	 * Web Service #38721
	 * 
	 * @return
	 */
	public List<AghEquipes> pesquisarEquipesPorMatriculaVinculo(final Integer matricula, final Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class, "EQP");
		criteria.createAlias("EQP." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "RAP");

		criteria.add(Restrictions.eq("RAP." + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("RAP." + RapServidores.Fields.VIN_CODIGO.toString(), vinCodigo));

		return executeCriteria(criteria);
	}
	
	public List<EquipeVO> pesquisarEquipesConsultoriaAmbulatorial(final String pesquisa, Short espSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<EquipeVO> lista = null;
		if (pesquisa != null && CoreUtil.isNumeroInteger(pesquisa)) {
			DetachedCriteria criteria = montarCriteriaEquipesConsultoriaAmbulatorial(pesquisa, espSeq, true);
			criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
			lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		if (lista == null || lista.isEmpty()) {
			DetachedCriteria criteria = montarCriteriaEquipesConsultoriaAmbulatorial(pesquisa, espSeq, false);
			criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
			lista = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		return lista; 
	}
	
	public Long pesquisarEquipesConsultoriaAmbulatorialCount(final String pesquisa, Short espSeq) {
		Long retorno = null;
		if (pesquisa != null && CoreUtil.isNumeroInteger(pesquisa)) {
			DetachedCriteria criteria = montarCriteriaEquipesConsultoriaAmbulatorial(pesquisa, espSeq, true);
			retorno = executeCriteriaCount(criteria);
		}
		if (retorno == null || retorno.longValue() == 0) {
			DetachedCriteria criteria = montarCriteriaEquipesConsultoriaAmbulatorial(pesquisa, espSeq, false);
			retorno = executeCriteriaCount(criteria);
		}
		return retorno;
	}
	
	private DetachedCriteria montarCriteriaEquipesConsultoriaAmbulatorial(final String pesquisa, Short espSeq, boolean isSigla) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class, "EQP");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghEquipes.Fields.SEQ.toString()), EquipeVO.Fields.SEQ.toString())
				.add(Projections.property(AghEquipes.Fields.NOME.toString()), EquipeVO.Fields.EQUIPE.toString()));
		
		if (pesquisa != null && !pesquisa.isEmpty()) {
			if (isSigla && CoreUtil.isNumeroInteger(pesquisa)) {
				criteria.add(Restrictions.eq(AghEquipes.Fields.SEQ.toString(), Integer.valueOf(pesquisa)));
			} else {
				criteria.add(Restrictions.ilike(AghEquipes.Fields.NOME.toString(), pesquisa, MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(MamConsultoriaAmbEquipe.class, "MCA");
		subQuery.setProjection(Projections.property(MamConsultoriaAmbEquipe.Fields.EQP_SEQ.toString()));
		subQuery.add(Restrictions.eq(MamConsultoriaAmbEquipe.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (espSeq != null) {
			subQuery.add(Restrictions.eq(MamConsultoriaAmbEquipe.Fields.ESP_SEQ.toString(), espSeq));
		}
		subQuery.add(Property.forName("EQP."+AghEquipes.Fields.SEQ.toString()).eqProperty(
				"MCA."+MamConsultoriaAmbEquipe.Fields.EQP_SEQ.toString()));
		
		criteria.add(Subqueries.exists(subQuery));
		criteria.setResultTransformer(Transformers.aliasToBean(EquipeVO.class));
		
		return criteria;
	}
	
	/**
	 * 42803
	 * C11 
	 * @return
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterEquipePorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class, "EQP");
		criteria.createAlias("EQP." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "VPS");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("EQP." + AghEquipes.Fields.NOME.toString()), LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.NOME_PROFISSIONAL_SOLICITANTE.toString())
				.add(Projections.property("VPS." + RapServidores.Fields.PES_CODIGO.toString()), LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.PES_CODIGO.toString())
				);
		
		criteria.add(Restrictions.eq("EQP." + AghEquipes.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("EQP." + AghEquipes.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(LaudoSolicitacaoAutorizacaoProcedAmbVO.class));
		return (LaudoSolicitacaoAutorizacaoProcedAmbVO) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AghEquipes> pesquisarEquipes(String pesquisa) {
		DetachedCriteria criteria = createPesquisaCriteria(pesquisa);
		criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);

	}
	public Long pesquisarEquipesCount(String pesquisa) {
		DetachedCriteria criteria = createPesquisaCriteria(pesquisa);
		return executeCriteriaCount(criteria);
		
	}
	
	
	
	private DetachedCriteria createPesquisaCriteriaInterconsulta(String pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class);

		criteria.createAlias(AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "SER");
		
		if (StringUtils.isNotBlank(pesquisa)) {
			if (CoreUtil.isNumeroInteger(pesquisa)) {
				criteria.add(Restrictions.or(
						Restrictions.eq(AghEquipes.Fields.CODIGO.toString(),Integer.parseInt(pesquisa)),
						Restrictions.eq("SER."+ RapServidores.Fields.MATRICULA.toString(),Integer.valueOf(pesquisa)),
						Restrictions.eq("SER."+ RapServidores.Fields.VIN_CODIGO.toString(),Short.valueOf(pesquisa))
						));
			} else {
				criteria.add(Restrictions.ilike(AghEquipes.Fields.NOME.toString(), pesquisa,MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(AghEquipes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghEquipes.Fields.CODIGO.toString()), EquipeVO.Fields.SEQ.toString())
				.add(Projections.property(AghEquipes.Fields.NOME.toString()), EquipeVO.Fields.NOME.toString())
				.add(Projections.property("SER."+ RapServidores.Fields.MATRICULA.toString()), EquipeVO.Fields.MATRICULA.toString())
				.add(Projections.property("SER."+ RapServidores.Fields.VIN_CODIGO.toString()), EquipeVO.Fields.VINCULO.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(EquipeVO.class));
		
		return criteria;
	}
	
	public List<EquipeVO> pesquisarEquipeInterconsulta(String pesquisa) {
		DetachedCriteria criteria = createPesquisaCriteriaInterconsulta(pesquisa);
		criteria.addOrder(Order.asc(AghEquipes.Fields.NOME.toString()));
		return executeCriteria(criteria, 0, 100, null, false);

	}
	public Long pesquisarEquipeInterconsultaCount(String pesquisa) {
		DetachedCriteria criteria = createPesquisaCriteriaInterconsulta(pesquisa);
		return executeCriteriaCount(criteria);
		
	}
	
	/***
	 * #6810 SB2
	 */
	public List<AghEquipes> obterEquipesPorSeqOuNome(String parametro){
		DetachedCriteria criteria = obterCriteriaEquipesPorSeqOuNome(parametro);
		return executeCriteria(criteria, 0, 100, AghEquipes.Fields.SEQ.toString(), true);
	}

	/***
	 * #6810 SB2
	 */
	public Long obterEquipesPorSeqOuNomeCount(String parametro){
		DetachedCriteria criteria = obterCriteriaEquipesPorSeqOuNome(parametro);
		return executeCriteriaCount(criteria);
	}

	/***
	 * #6810 SB2
	 */
	private DetachedCriteria obterCriteriaEquipesPorSeqOuNome(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class);
		if(parametro != null && !parametro.isEmpty()){
			if(StringUtils.isNumeric(parametro)){
				criteria.add(Restrictions.eq(AghEquipes.Fields.SEQ.toString(), Integer.valueOf(parametro)));
			}else{
				criteria.add(Restrictions.ilike(AghEquipes.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	
	public String obterDescricaoEquipe(Integer matricula, Short vinCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghEquipes.class, "EQU");
		criteria.createAlias("EQU." + AghEquipes.Fields.PROFISSIONAIS_EQUIPE.toString(), "PRE", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.property(AghEquipes.Fields.NOME.toString()));
		criteria.add(Restrictions.eq("PRE." + AghProfissionaisEquipe.Fields.SERVIDOR.toString() + "." +
				RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("PRE." + AghProfissionaisEquipe.Fields.SERVIDOR.toString() + "." +
				RapServidores.Fields.VIN_CODIGO.toString(), vinCodigo));
		return (String) executeCriteriaUniqueResult(criteria);
	}

}