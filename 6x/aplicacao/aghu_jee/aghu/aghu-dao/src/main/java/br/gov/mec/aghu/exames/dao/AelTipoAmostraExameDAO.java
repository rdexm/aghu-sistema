package br.gov.mec.aghu.exames.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelTipoAmostraExameId;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


public class AelTipoAmostraExameDAO extends BaseDao<AelTipoAmostraExame> {

	private static final long serialVersionUID = 2780030763748202011L;

	@Override
	protected void obterValorSequencialId(AelTipoAmostraExame elemento) {
		
		if (elemento == null || elemento.getExamesMaterialAnalise() == null) {
			throw new IllegalArgumentException("Material de Análise de exame não foi informado corretamente.");
		}
		
		if (elemento == null || elemento.getMaterialAnalise() == null) {
			throw new IllegalArgumentException("Material de Análise não foi informado corretamente.");
		}
		
		if (elemento == null || elemento.getOrigemAtendimento() == null) {
			throw new IllegalArgumentException("Origem do atendimento não foi informado corretamente.");
		}		

		AelTipoAmostraExameId id = new AelTipoAmostraExameId();
		id.setEmaExaSigla(elemento.getExamesMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExamesMaterialAnalise().getId().getManSeq());
		id.setManSeq(elemento.getMaterialAnalise().getSeq());
		id.setOrigemAtendimento(elemento.getOrigemAtendimento());
		
		elemento.setId(id);	
		
	}
	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelTipoAmostraExame.class);
		criteria.createAlias(AelTipoAmostraExame.Fields.RECIPIENTE_COLETA.toString(),"REC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelTipoAmostraExame.Fields.ANTI_COAGULANTE.toString(),"ACO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelTipoAmostraExame.Fields.MATERIAL_ANALISE.toString(),"MAA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelTipoAmostraExame.Fields.UNIDADE_FUNCIONAL.toString(),"UNF", JoinType.LEFT_OUTER_JOIN);
		return criteria;
    }
	
	/**
	 * Mátodo que procura nos tipos de amostra de exame se existe algum que utiliza
	 * determinado material de análise.
	 * 
	 * @param codigoMaterialAnalise o código do material de análise a ser buscado
	 * @return true caso encontre um tipo de amostra que use o material parametrizado, false caso contrário
	 */
	public boolean pesquisarPorMaterialAnalise(Integer codigoMaterialAnalise) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.MAN_SEQ.toString(), codigoMaterialAnalise));
		
		return executeCriteriaCount(criteria) != 0;
	}
	
	/**
	 * Mátodo que procura nos tipos de amostra de exame se existe algum que utiliza
	 * determinado anticoagulante.
	 * 
	 * @param codigoAnticoagulante o código do anticoagulante a ser buscado
	 * @return true caso encontre um tipo de amostra que use o anticoagulante parametrizado, false caso contrário
	 */
	public boolean pesquisarPorAnticoagulante(Integer codigoAnticoagulante) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.ATC_SEQ.toString(), codigoAnticoagulante));	
		return executeCriteriaCount(criteria) != 0;
	}
	
	/**
	 * Retorna AelTipoAmostraExame pelo id
	 * @param id
	 * @return
	 */
	public AelTipoAmostraExame obterAelTipoAmostraExameIdPorID(AelTipoAmostraExameId id) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria = this.obterCriterioConsulta(criteria, id.getEmaExaSigla(), 
				id.getEmaManSeq(),id.getManSeq(), id.getOrigemAtendimento());
				
		return (AelTipoAmostraExame) executeCriteriaUniqueResult(criteria);
	}

	public boolean obterTipoAmostraExame(Integer codigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelTipoAmostraExame.class);
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.RCO_SEQ.toString(),codigo));
		
		return executeCriteriaCount(criteria) != 0;
	}
	
	public List<AelTipoAmostraExame> buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(String emaExaSigla, Integer emaManSeq){
		DetachedCriteria criteria =  DetachedCriteria.forClass(AelTipoAmostraExame.class);
		
		criteria.createAlias(AelTipoAmostraExame.Fields.EXAMES_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelTipoAmostraExame.Fields.MATERIAL_ANALISE.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelTipoAmostraExame.Fields.RECIPIENTE_COLETA.toString(), "REC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelTipoAmostraExame.Fields.ANTI_COAGULANTE.toString(), "ANT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelTipoAmostraExame.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelTipoAmostraExame.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),emaManSeq));
		criteria.addOrder(Order.asc(AelTipoAmostraExame.Fields.MAN_SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	public Long countListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(String emaExaSigla, Integer emaManSeq){
		DetachedCriteria criteria =  DetachedCriteria.forClass(AelTipoAmostraExame.class);
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),emaManSeq));
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Método que procura tipos de amostra de exame com a origem do atendimento igual a "todas as origens"
	 * @param emaExaSigla sigla do exame do material de análise do exame
	 * @param emaManSeq valor sequencial do manterial de análise do material de análise do exame
	 * @return lista de tipos de amostra de exame
	 */
	public List<AelTipoAmostraExame> buscarListaAelTipoAmostraExamePorOrigemAtendimentoTodasOrigens(String emaExaSigla, Integer emaManSeq){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),emaManSeq));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(), DominioOrigemAtendimento.T));
		return executeCriteria(criteria);
	}
	
	/**
	 * Método que procura tipos de amostra de exame com a origem do atendimento diferente de "todas as origens"
	 * @param emaExaSigla sigla do exame do material de análise do exame
	 * @param emaManSeq valor sequencial do manterial de análise do material de análise do exame
	 * @return lista de tipos de amostra de exame
	 */
	public List<AelTipoAmostraExame> buscarListaAelTipoAmostraExamePorOrigemAtendimentoDiferenteTodasOrigens(String emaExaSigla, Integer emaManSeq){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),emaManSeq));
		criteria.add(Restrictions.ne(AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(), DominioOrigemAtendimento.T));
		return executeCriteria(criteria);
	}	
	
	
	/**
	 * Método que procura tipos de amostra de Exame, Material analise e Origem Atendimento.<br>
	 * 
	 * @param exa
	 * @param man
	 * @param origem
	 * @return
	 */
	public List<AelTipoAmostraExame> buscarListaAelTipoAmostraExame(AelExames exa, AelMateriaisAnalises man, DominioOrigemAtendimento origem) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(), exa.getSigla()));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(), man.getSeq()));
		
		//and (origem_atendimento = decode(c_origem_atendimento,'N','I',c_origem_atendimento) or origem_atendimento = 'T');
		if (DominioOrigemAtendimento.N == origem) {
			origem = DominioOrigemAtendimento.I;
		}
		
		Collection<DominioOrigemAtendimento> origens = Arrays.asList(DominioOrigemAtendimento.T, origem);
		criteria.add(Restrictions.in(AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(), origens));
		
		return executeCriteria(criteria);
	}	
	
	
	/**
	 * Este método para atualizar está acoplado ao 
	 * comportamento da lista de tipos de amostras de exames em manter Materiais de Análise de Exames
	 * @param elemento
	 * @return
	 */
	public AelTipoAmostraExame atualizarParaListaMaterialAnaliseExame(AelTipoAmostraExame elemento) {
		
		final AelMateriaisAnalises materialAnaliseOld = elemento.getMaterialAnalise();
		final DominioOrigemAtendimento origemAtendimentoOld = elemento.getOrigemAtendimento();
		
		desatachar(elemento);
		AelTipoAmostraExame elementoAntigo = obterPorChavePrimaria(elemento.getId());
		remover(elementoAntigo);
		this.realizarFlush();
		
		elemento.setMaterialAnalise(materialAnaliseOld);
		elemento.setOrigemAtendimento(origemAtendimentoOld);
		
		this.persistir(elemento);
		this.realizarFlush();
		
		return elemento;
	}
	
	/**
	 * Método que procura tipos de amostra de exame cuja responsabilidade seja do Coletador ou Solicitante
	 * @param emaExaSigla sigla do exame do material de análise do exame
	 * @param emaManSeq valor sequencial do manterial de análise do material de análise do exame
	 * @return lista de tipos de amostra de exame
	 */
	public List<AelTipoAmostraExame> buscarListaAelTipoAmostraExameColetadorSolicitante(String emaExaSigla, Integer emaManSeq){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),emaManSeq));
		criteria.add(Restrictions.in(AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), new DominioResponsavelColetaExames[]{DominioResponsavelColetaExames.C, DominioResponsavelColetaExames.S}));
		return executeCriteria(criteria);
	}	
	
	/**
	 * Método que procura tipos de amostra de exame cuja responsabilidade seja do Coletador ou Solicitante
	 * @param emaExaSigla sigla do exame do material de análise do exame
	 * @param emaManSeq valor sequencial do manterial de análise do material de análise do exame
	 * @return lista de tipos de amostra de exame
	 */
	public List<AelTipoAmostraExame> buscarListaAelTipoAmostraExameColetador(String emaExaSigla, Integer emaManSeq){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),emaManSeq));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), DominioResponsavelColetaExames.C));
		return executeCriteria(criteria);
	}

	public List<AelTipoAmostraExame> buscarAelTipoAmostraExamePorAelExamesMaterialAnaliseAelAmostrasRecipienteColetaResponsavelCS(
			AelExamesMaterialAnalise aelExamesMaterialAnalise,
			AelAmostras aelAmostras, AelRecipienteColeta recipienteColeta) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),aelExamesMaterialAnalise.getAelExames().getSigla()));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),aelExamesMaterialAnalise.getAelMateriaisAnalises().getSeq()));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.MAN_SEQ.toString(),aelAmostras.getMateriaisAnalises().getSeq()));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.RECIPIENTE_COLETA.toString(),recipienteColeta));
		criteria.add(Restrictions.in(AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), new DominioResponsavelColetaExames[]{DominioResponsavelColetaExames.C,DominioResponsavelColetaExames.S}));
		return executeCriteria(criteria);
	}	
	
	/**
	 * Método que busca qual o responsável da coleta de um exame que tem origem atendimento igual á origem informada na tela ou 'T'.
	 * cursos c_amostra_exame da pll AELP_VERIFICA_RESPONSAVEL_COLETA.
	 * @param emaExaSigla sigla do exame do material de análise do exame
	 * @param emaManSeq valor sequencial do manterial de análise do material de análise do exame
	 * @param origemAtendimento Origem do atendimento informado na tela de solicitação de exame 
	 * @return amostra de exame com os valores carregados
	 */
	public List<AelTipoAmostraExame> buscarAelTipoAmostraExameResponsavelColeta(String emaExaSigla, Integer emaManSeq, DominioOrigemAtendimento origemAtendimento){
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),emaManSeq));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.MAN_SEQ.toString(),emaManSeq));
		criteria.add(Restrictions.or(
				Restrictions.eq(AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(), origemAtendimento), 
				Restrictions.eq(AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(), DominioOrigemAtendimento.T)));
		
		return executeCriteria(criteria);
	}	
	
	public List<AelTipoAmostraExame> pesquisarResponsavelColeta(String sigla, Integer matExame) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelTipoAmostraExame.Fields.EXAMES_MATERIAL_ANALISE.toString(), AelTipoAmostraExame.Fields.EXAMES_MATERIAL_ANALISE.toString());
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(), matExame));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(), sigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), DominioResponsavelColetaExames.C));
		return executeCriteria(criteria);
	}
	
	
	public AelTipoAmostraExame obterAelTipoAmostraExame(String emaExaSigla,	Integer emaManSeq, Integer manSeq, DominioOrigemAtendimento origemAtendimento) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.createCriteria(AelTipoAmostraExame.Fields.EXAMES_MATERIAL_ANALISE.toString(), "EAN" , JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("EAN." + AelExamesMaterialAnalise.Fields.EXAME.toString(),
				"EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("EAN." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(),
				"MAN", JoinType.LEFT_OUTER_JOIN);
		
		criteria = this.obterCriterioConsulta(criteria, emaExaSigla, emaManSeq, manSeq, origemAtendimento);
		
		return (AelTipoAmostraExame) executeCriteriaUniqueResult(criteria);
		
	}
	
	public AelTipoAmostraExame obterAelTipoAmostraExame(AelTipoAmostraExameId id) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelTipoAmostraExame.Fields.EXAMES_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.ID.toString(), id));
		return (AelTipoAmostraExame) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria obterCriterioConsulta(DetachedCriteria criteria,String emaExaSigla, Integer emaManSeq, Integer manSeq, DominioOrigemAtendimento origemAtendimento) {
		
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.MAN_SEQ.toString(),  manSeq));
		criteria.add(Restrictions.eq(AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(), origemAtendimento));
		
		return criteria;
	}
	
	private void realizarFlush() {
		this.flush();
	}

	/**
	 * Retorna true se existir algum exame na solicitação ou lista fornecida em
	 * que o responsável pela coleta é o coletador.
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param seqps
	 * @return
	 */
	public boolean existemInformacoesColeta(Integer soeSeq, Short seqp, List<Short> seqps) {
		StringBuilder hql = new StringBuilder(300);
		
		hql.append(" select count(*) from ")
				.append(AelTipoAmostraExame.class.getName()).append(" as tae, ") //
				.append(AelItemSolicitacaoExames.class.getName()).append(" as ise ") //
		.append(" 	join ise.").append(AelItemSolicitacaoExames.Fields.AEL_EXAMES).append(" as exa ") //
		.append(" 	join ise.").append(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES).append(" as ma ") //
		.append(" where 	tae.").append(AelTipoAmostraExame.Fields.EMA_EXA_SIGLA) //
		.append(" = exa.").append(AelExames.Fields.SIGLA)
		.append(" 	and tae.").append(AelTipoAmostraExame.Fields.EMA_MAN_SEQ).append(" = ma.").append(AelMateriaisAnalises.Fields.SEQ)
		.append(" 	and ise.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ).append(" = :soeSeq ");
		
		if (seqps != null && !seqps.isEmpty()){
			hql.append(" 	and ise.").append(AelItemSolicitacaoExames.Fields.SEQP).append(" in (:seqp) ");
		} else {
			hql.append(" 	and ise.").append(AelItemSolicitacaoExames.Fields.SEQP).append(" = :seqp ");
		}
		hql.append(" 	and tae.").append(AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA).append(" = :responsavelColeta ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		if (seqps != null && !seqps.isEmpty()){
			query.setParameterList("seqp",  seqps);
		} else {
			query.setParameter("seqp", seqp);
		}
		query.setParameter("responsavelColeta", DominioResponsavelColetaExames.C);
		
		Long count = (Long) query.uniqueResult();
		return count > 0;
	}
	
}