package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraItemExamesVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAmostrasId;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAelExameMatAnalise;


public class AelAmostrasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAmostras> {
	
	private static final long serialVersionUID = 3257162530007732360L;

	@Override
	protected void obterValorSequencialId(final AelAmostras elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		if (elemento.getSolicitacaoExame() == null) {
			throw new IllegalArgumentException("Associacao com AelSolicitacaoExame nao esta corretamente informada!!!");
		}
		final Short maxSeqpAmostra = this.obterMaxSeqpPorSolicitacaoExame(elemento.getSolicitacaoExame());
		final Integer newSeqpAmostra = maxSeqpAmostra + 1;
		
		final AelAmostrasId id = new AelAmostrasId();		
		id.setSoeSeq(elemento.getSolicitacaoExame().getSeq());
		id.setSeqp(newSeqpAmostra.shortValue());
		
		elemento.setId(id);
	}
	
	private DetachedCriteria obterCriteria() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
		return criteria;
    }

	
	private DetachedCriteria obterCriteriaAlias(String alias) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(
				AelAmostras.class, StringUtils.isBlank(alias) ? "AMO" : alias.trim());
		return criteria;
    }
	
	

	/**
	 * .
	 * @param aelAmostras
	 * @return
	 */
	public List<AelAmostras> buscarAmostrasOutrasSolicitacoes(
			final AelAmostras aelAmostras) {
		
		final DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelAmostras.Fields.NRO_UNICO.toString(), aelAmostras.getNroUnico()));
		criteria.add(Restrictions.eq(AelAmostras.Fields.DT_NUMERO_UNICO.toString(), aelAmostras.getDtNumeroUnico()));
		criteria.add(Restrictions.or(
			Restrictions.ne(AelAmostras.Fields.SOE_SEQ.toString(), aelAmostras.getId().getSoeSeq()),
			Restrictions.ne(AelAmostras.Fields.SEQP.toString(), aelAmostras.getId().getSeqp())));
		
		return executeCriteria(criteria);
	}
	
	
	
	
	/**
	 * .
	 * @param dthrNumeroUnico
	 * @param soeSeq
	 * @return
	 */
	public List<AelAmostras> buscarAmostrasPorDthrNumeroUnico(
			final Date dthrNumeroUnico, final Integer soeSeq) {
		final DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.isNotNull(AelAmostras.Fields.NRO_UNICO.toString()));
		dc.add(Restrictions.eq(AelAmostras.Fields.DT_NUMERO_UNICO.toString(), dthrNumeroUnico));
		dc.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), soeSeq));
		dc.addOrder(Order.asc(AelAmostras.Fields.SEQP.toString()));
		
		return executeCriteria(dc);
	}
	
	/**
	 * Busca o objeto pelo seu id.
	 * @param {Integer} soeSeq
	 * @param {Short} seqp
	 * @return {AelAmostras}
	 */
	public AelAmostras buscarAmostrasPorId(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria dc = obterCriteria();

		dc.createAlias(AelAmostras.Fields.MATERIAL_ANALISE.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias(AelAmostras.Fields.SOLICITACAO_EXAME.toString(), "SOL", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("SOL."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		dc.createAlias("ATD."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);

		dc.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), soeSeq));
		dc.add(Restrictions.eq(AelAmostras.Fields.SEQP.toString(), seqp));

		return (AelAmostras) executeCriteriaUniqueResult(dc);
	}
	
	public AelAmostras buscarAmostrasComRecepientePorId(final Integer soeSeq, final Short seqp) {
		final DetachedCriteria dc = obterCriteria();

		dc.createAlias(AelAmostras.Fields.MATERIAL_ANALISE.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias(AelAmostras.Fields.SOLICITACAO_EXAME.toString(), "SOL", JoinType.LEFT_OUTER_JOIN);

		dc.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), soeSeq));
		dc.add(Restrictions.eq(AelAmostras.Fields.SEQP.toString(), seqp));
		
		dc.setFetchMode(AelAmostras.Fields.RECIPIENTE_COLETA.toString(), FetchMode.JOIN);
		dc.setFetchMode(AelAmostras.Fields.ANTICOAGULANTE.toString(), FetchMode.JOIN);

		return (AelAmostras) executeCriteriaUniqueResult(dc);
	}
	
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame, final Short amostraSeqp) {
		return buscarAmostrasPorSolicitacaoExame(solicitacaoExame.getSeq(), amostraSeqp);
	}

	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(final Integer soeSeq, final Short amostraSeqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
		criteria.createAlias(AelAmostras.Fields.RECIPIENTE_COLETA.toString(), "REC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), soeSeq));
		
		if(amostraSeqp != null){
			criteria.add(Restrictions.eq(AelAmostras.Fields.SEQP.toString(), amostraSeqp));
		}
		
		return executeCriteria(criteria);	
	}

	public List<AelAmostras> buscarAmostrasPorSolicitacaoExameEItemSolicitacao(final AelSolicitacaoExames solicitacaoExame, final Short amostraSeqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
				
		criteria.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), solicitacaoExame.getSeq()));
		
		if(amostraSeqp != null){
			criteria.add(Restrictions.eq(AelAmostras.Fields.SEQP.toString(), amostraSeqp));
		}
		
		return executeCriteria(criteria);	
	}
	
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
		criteria.createAlias(AelAmostras.Fields.MATERIAL_ANALISE.toString(), "AMA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), solicitacaoExame.getSeq()));
		
		criteria.addOrder(Order.asc(AelAmostras.Fields.DTHR_PREVISTA_COLETA.toString()));
		
		return executeCriteria(criteria);	
	}
	
	public List<AelAmostras> buscarAmostrasPorUnidadeFuncionalSolicitacaoExame(final AghUnidadesFuncionais unidadeExecutora, final AelSolicitacaoExames solicitacaoExame) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
		criteria.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), solicitacaoExame.getSeq()));
		criteria.add(Restrictions.eq(AelAmostras.Fields.UNF_SEQ.toString(), unidadeExecutora.getSeq()));
		criteria.addOrder(Order.asc(AelAmostras.Fields.SEQP.toString()));
		return executeCriteria(criteria);	
	}
	
	public DetachedCriteria obterCriteriaBuscarAmostrasPorSolicitacao(final Integer soeSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
		
		criteria.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), soeSeq));
		
		criteria.addOrder(Order.asc(AelAmostras.Fields.DTHR_PREVISTA_COLETA.toString()));
		
		criteria.setFetchMode(AelAmostras.Fields.RECIPIENTE_COLETA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelAmostras.Fields.ANTICOAGULANTE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelAmostras.Fields.SALA_EXECUTORA_EXAME.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelAmostras.Fields.SOLICITACAO_EXAME.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelAmostras.Fields.MATERIAL_ANALISE.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(
				AelAmostras.Fields.SALA_EXECUTORA_EXAME.toString()
						+ "."
						+ AelSalasExecutorasExames.Fields.UNIDADE_FUNCIONAL
								.toString(), FetchMode.JOIN);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);		
		
		return criteria;
	}
	
	
	public List<AelAmostras> buscarAmostrasPorSolicitacaoExameSeq(final Integer soeSeq) {
		final DetachedCriteria criteria = obterCriteriaBuscarAmostrasPorSolicitacao(soeSeq);
		
		return executeCriteria(criteria);	
	}
	
	public List<AelAmostras> buscarAmostrasPorAmostraESolicitacaoExame(final Short amostraSeq, final Integer soeSeq) {
		final DetachedCriteria criteria = obterCriteriaBuscarAmostrasPorSolicitacao(soeSeq);
		
		criteria.add(Restrictions.eq(AelAmostras.Fields.SEQP.toString(), amostraSeq));
		
		return executeCriteria(criteria);	
	}
	
	/**
	 * Filtra por Solicitacao exame e retorna o maior seqp.<br>
	 * 
	 * @param solicitacaoExame
	 * @return
	 */
	public Short obterMaxSeqpPorSolicitacaoExame(final AelSolicitacaoExames solicitacaoExame) {
		if (solicitacaoExame == null || solicitacaoExame.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!");
		}
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
		
		criteria.setProjection(Projections.max(AelAmostras.Fields.SEQP.toString()));
		
		criteria.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), solicitacaoExame.getSeq()));
		
		Short seqp = 0;
		final Object maxSeqp = this.executeCriteriaUniqueResult(criteria);
		if (maxSeqp != null) {
			seqp = (Short) maxSeqp;
		}
		
		return seqp;
	}
	
	/**
	 * Verifica se existe uma solicitação para o mesmo atendimento que tenha o mesmo exame,
	 * porém apenas para itens com situação diferente de Cancelado (CA).
	 * 
	 * @param {Integer} pacCodigo
	 * @param {Integer} soeSeq
	 * @param {Integer} ufeEmaExaSigla
	 * @param {Integer} ufeEmaManSeq
	 * @param {Date} dataCalculadaAparecimentoSolicitacao
	 * @return {Integer} Sequencial da Solicitação
	 */
	public Integer countAmostras(final Integer pacCodigo,
			final Integer soeSeq, final String ufeEmaExaSigla, final Integer ufeEmaManSeq, final Date dataCalculadaAparecimentoSolicitacao) {
			
		/*
	            select nvl(count(*),0)
			      from  ael_amostras                amo,
			            ael_amostra_item_exames     aie,
			            ael_item_solicitacao_exames ise,
			            ael_solicitacao_exames      soe,
			            agh_atendimentos            atd
			     where  atd.pac_codigo        =  c_pac_codigo
			       and  soe.atd_seq           =  atd.seq
			       and  ise.soe_seq           =  soe.seq
			       and  ise.soe_seq						<> c_soe_seq
			       and  substr(ise.ufe_ema_exa_sigla,1,5) =  c_ufe_ema_exa_sigla
			       and  ise.ufe_ema_man_seq   =  c_ufe_ema_man_seq
			       and  substr(ise.sit_codigo,1,2) <> 'CA'
			       and  ise.dthr_programada+0  >=  c_dthr_calculada
			       and  aie.ise_soe_seq       = ise.soe_seq
			       and  aie.ise_seqp          = ise.seqp
			       and  amo.soe_seq           = aie.amo_soe_seq
			       and  amo.seqp              = aie.amo_seqp
			       and  amo.situacao         <> 'C';
       
		 */
		
		final StringBuffer hql = new StringBuffer(350);
		hql.append("select soe.");
		hql.append(AelSolicitacaoExames.Fields.SEQ.toString());
		hql.append(" from ").append(AelItemSolicitacaoExames.class.getSimpleName()).append(" ise ");
		hql.append(" inner join ise."
				+ AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString()
				+ " aie ");
		hql.append(" inner join aie."
				+ AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString()
				+ " amo ");
		hql.append(" inner join ise."
				+ AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString()
				+ " soe ");
		hql.append(" inner join soe."
				+ AelSolicitacaoExames.Fields.ATENDIMENTO.toString()
				+ " atd ");
		
		hql.append(" where atd.");
		hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		hql.append(" = :pacCodigo ");
		
		if (soeSeq != null) {
			hql.append(" AND ise.");
			hql.append(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString());
			hql.append(" <> :soeSeq ");
		}
		
		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString());
		hql.append(" = :ufeEmaExaSigla ");
		
		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString());
		hql.append(" = :ufeEmaManSeq ");
		
		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		hql.append(" <> :sitCodigo ");
		
		hql.append(" AND ise.");
		hql.append(AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()).append(" >= :dataCalculadaAparecimentoSolicitacao "); 
		
		hql.append(" AND amo.");
		hql.append(AelAmostras.Fields.SITUACAO.toString());
		hql.append(" <> :situacao ");
		
		final javax.persistence.Query query = createQuery(hql.toString());
		query.setParameter("pacCodigo", pacCodigo);
		
		if (soeSeq != null) {
			query.setParameter("soeSeq", soeSeq);
		}
		
		query.setParameter("ufeEmaExaSigla", ufeEmaExaSigla);
		query.setParameter("ufeEmaManSeq", ufeEmaManSeq);
		query.setParameter("sitCodigo", DominioSituacaoItemSolicitacaoExame.CA.toString());
		query.setParameter("situacao", DominioSituacaoAmostra.C);
		query.setParameter("dataCalculadaAparecimentoSolicitacao", dataCalculadaAparecimentoSolicitacao, TemporalType.TIMESTAMP);
		
		return query.getResultList().size();
		
	}

	public AelAmostras buscarAmostrasPor(final AelSolicitacaoExames solicitacaoExame,
			final AelMateriaisAnalises aelMateriaisAnalises,
			final AghUnidadesFuncionais unidadeFuncional,
			final AelRecipienteColeta recipienteColeta,
			final AelAnticoagulante anticoagulante, final Date dthrPrevistaColeta,
			final Short tempo, final DominioSituacaoAmostra situacaoAmostraItem) {
		// TODO Auto-generated method stub
		/*
		
		cursor  c_amostras
	       (c_soe_seq	ael_amostras.soe_seq%type,
	        c_man_seq  ael_amostras.man_seq%type,
	        c_rco_seq  ael_amostras.rco_seq%type,
	        c_atc_seq  ael_amostras.atc_seq%type,
	        c_dthr_prevista_coleta   ael_amostras.dthr_prevista_coleta%type,
	        c_tempo_intervalo_coleta  ael_amostras.tempo_intervalo_coleta%type,
	        c_situacao ael_amostras.situacao%type,
	        c_unf_seq ael_amostras.unf_seq%type)
	is
	      select  SEQP,
	              UNID_TEMPO_INTERVALO_COLETA
	        from  ael_amostras
	     where  soe_seq  =  c_soe_seq  and
	            man_seq  =  c_man_seq  and
	            rco_seq  =  c_rco_seq  and
	            atc_seq  =  c_atc_seq  and
	            dthr_prevista_coleta   = c_dthr_prevista_coleta   and
	            tempo_intervalo_coleta = c_tempo_intervalo_coleta and
	            situacao = c_situacao  and
	            unf_seq  = c_unf_seq;
		*/
		
		
		return null;
	}
	
	public List<AelAmostras> buscarAmostrasPorSolicitacaoColetaSituacao(final Integer soeSeq, final boolean coleta, final Short seqp, String situacaoItemExame) {
		String aliasAmo = "amo";
		String aliasAie = "aie";
		String aliasIse = "ise";
		String ponto = ".";
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class, aliasAmo);
		
		criteria.add(Restrictions.eq(aliasAmo + ponto + AelAmostras.Fields.SOE_SEQ.toString(), soeSeq));
		//criteria.createAlias(aliasAmo + ponto + AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString(), AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString(), JoinType.LEFT_OUTER_JOIN);
		//criteria.createAlias(AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString() + ponto + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasAmo + ponto + AelAmostras.Fields.UNIDADE_FUNCIONAL.toString(), AelAmostras.Fields.UNIDADE_FUNCIONAL.toString(), JoinType.LEFT_OUTER_JOIN);
		// Filtro situacaoItemExame necessario para contemplar o cursor c_amostra migrado 
		// da procedure AELP_GERA_ETIQUETAS_CS do PLL AELF_SOLICITAR_EXAME
		if (situacaoItemExame != null) {
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(AelAmostraItemExames.class, aliasAie);
			subCriteria.setProjection(Projections.property(aliasAie + ponto + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()));
			subCriteria.createAlias(aliasAie + ponto + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), aliasIse);
			subCriteria.add(Restrictions.eq(aliasIse + ponto + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), situacaoItemExame));
			subCriteria.add(Restrictions.eqProperty(aliasAie + ponto + AelAmostraItemExames.Fields.SOE_SEQ.toString() , aliasAmo + ponto + AelAmostras.Fields.SOE_SEQ.toString()));
			subCriteria.add(Restrictions.eqProperty(aliasAie + ponto + AelAmostraItemExames.Fields.AMO_SEQP.toString() , aliasAmo + ponto + AelAmostras.Fields.SEQP.toString()));
			criteria.add(Subqueries.exists(subCriteria));
		} else if (coleta) {
			final DominioSituacaoAmostra[] situacoes = {DominioSituacaoAmostra.G, DominioSituacaoAmostra.C, DominioSituacaoAmostra.U, DominioSituacaoAmostra.M};
			criteria.add(Restrictions.in(aliasAmo + ponto + AelAmostras.Fields.SITUACAO.toString(), situacoes));	
		}
		
		if (seqp != null) {
			criteria.add(Restrictions.eq(aliasAmo + ponto + AelAmostras.Fields.SEQP.toString(), seqp));
		}
		
		return executeCriteria(criteria, true);
	}
	
	public List<AelAmostras> buscaListaAmostrasPorItemExame(final AelItemSolicitacaoExames itemSolicitacaoExame){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class, "AMO");
		
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.SOE_SEQ.toString(), itemSolicitacaoExame.getId().getSoeSeq()));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(AelAmostraItemExames.class, "AIE");
		subCriteria.setProjection(Projections.property("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()));
		subCriteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.SOE_SEQ.toString(), itemSolicitacaoExame.getId().getSoeSeq()));
		subCriteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.SEQP.toString(), itemSolicitacaoExame.getId().getSeqp()));
		//DESCOMENTAR AQUI PRA TESTAR
		//List<AelAmostraItemExames> ael = executeCriteria(subCriteria);
		subCriteria.add(Restrictions.eqProperty("AMO."+AelAmostras.Fields.SEQP.toString(), "AIE."+AelAmostraItemExames.Fields.AMO_SEQP.toString()));

		criteria.add(Subqueries.exists(subCriteria));
		
		return executeCriteria(criteria, true);
	}
	
	public AelAmostras obterAelAmostras(Integer amoSoeSeq, Integer amoSeqP) {
		return this.obterAelAmostras(amoSoeSeq, amoSeqP.shortValue());
	}
	
	/**
	 * View V_AEL_AMOSTRA_MAPA
	 * 
	 * @param amoSoeSeq
	 * @param seqP
	 * @return
	 */
	public AelAmostras obterAelAmostras(Integer amoSoeSeq, Short amoSeqP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
		
		//criteria.createCriteria(AelAmostras.Fields.CONFIG_MAPA.toString(), Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq(AelAmostras.Fields.SEQP.toString(), amoSeqP));
				
		List<AelAmostras> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public AelAmostrasVO obterAelAmostraVO(Integer soeSeq, Short seqp) {
		
		final DetachedCriteria criteria = this.obterCriteriaAlias(null);
		
		criteria.createAlias("AMO.".concat(AelAmostras.Fields.RECIPIENTE_COLETA.toString()), 
				"RCO", Criteria.INNER_JOIN);
		criteria.createAlias("AMO.".concat(AelAmostras.Fields.ANTICOAGULANTE.toString()), 
				"ATC", Criteria.LEFT_JOIN);
		criteria.createAlias("AMO.".concat(AelAmostras.Fields.SOLICITACAO_EXAME.toString()), 
				"SOE", Criteria.LEFT_JOIN);
		criteria.createAlias("SOE.".concat(AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString()), 
				"ADD", Criteria.LEFT_JOIN);
		criteria.createAlias("SOE.".concat(AelSolicitacaoExames.Fields.ATENDIMENTO.toString()), 
				"ATD", Criteria.LEFT_JOIN);
		criteria.createAlias("ATD.".concat(AghAtendimentos.Fields.PACIENTE.toString()), 
				"PAC", Criteria.LEFT_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Property.forName("AMO.".concat(AelAmostras.Fields.SOE_SEQ.toString())), 
				AelAmostrasVO.Fields.SOE_SEQ.toString());
		projection.add(Property.forName("AMO.".concat(AelAmostras.Fields.SEQP.toString())), 
				AelAmostrasVO.Fields.SEQP.toString());
		projection.add(Property.forName("AMO.".concat(AelAmostras.Fields.NRO_UNICO.toString())), 
				AelAmostrasVO.Fields.NUMERO_UNICO.toString());
		projection.add(Property.forName("AMO.".concat(AelAmostras.Fields.DT_NUMERO_UNICO.toString())),
				AelAmostrasVO.Fields.DT_NUMERO_UNICO.toString());
		projection.add(Property.forName("AMO.".concat(AelAmostras.Fields.SITUACAO.toString())),
				AelAmostrasVO.Fields.SITUACAO.toString());
		projection.add(Property.forName("AMO.".concat(AelAmostras.Fields.UNID_TEMPO_INTERVALO_COLETA.toString())),
				AelAmostrasVO.Fields.UNID_TEMPO_INTERVALO_COLETA.toString());
		projection.add(Property.forName("AMO.".concat(AelAmostras.Fields.TEMPO_INTERVALO_COLETA.toString())),
				AelAmostrasVO.Fields.TEMPO_INTERVALO_COLETA.toString());
		
		projection.add(Property.forName("RCO.".concat(AelRecipienteColeta.Fields.DESCRICAO.toString())),
				AelAmostrasVO.Fields.RECIPIENTE_COLETA.toString());
		
		projection.add(Property.forName("ATC.".concat(AelAnticoagulante.Fields.DESCRICAO.toString())),
				AelAmostrasVO.Fields.ANTICOAGULANTE.toString());
		
		projection.add(Property.forName("PAC.".concat(AipPacientes.Fields.CODIGO.toString())),
				AelAmostrasVO.Fields.CODIGO_PAC_ATD.toString());
		projection.add(Property.forName("ADD.".concat(AelAtendimentoDiversos.Fields.PAC_CODIGO.toString())),
				AelAmostrasVO.Fields.CODIGO_PAC_ADD.toString());
		
		projection.add(Property.forName("PAC.".concat(AipPacientes.Fields.NOME.toString())),
				AelAmostrasVO.Fields.NOME_PAC_ATD.toString());
		projection.add(Property.forName("ADD.".concat(AelAtendimentoDiversos.Fields.NOME_PACIENTE.toString())),
				AelAmostrasVO.Fields.NOME_PAC_ADD.toString());
		
		projection.add(Property.forName("ATD.".concat(AghAtendimentos.Fields.QRT_NUMERO.toString())),
				AelAmostrasVO.Fields.QRT_NUMERO.toString());
		
		projection.add(Property.forName("ATD.".concat(AghAtendimentos.Fields.UNF_SEQ.toString())),
				AelAmostrasVO.Fields.UNIDADE.toString());
		
		projection.add(Property.forName("ATD.".concat(AghAtendimentos.Fields.LTO_LTO_ID.toString())),
				AelAmostrasVO.Fields.LEITO.toString());
		
		projection.add(Property.forName("ATD.".concat(AghAtendimentos.Fields.SEQ.toString())),
				AelAmostrasVO.Fields.ATD_SEQ.toString());
		
		projection.add(Property.forName("ATD.".concat(AghAtendimentos.Fields.PRONTUARIO.toString())),
				AelAmostrasVO.Fields.ATD_PRONTUARIO.toString());
		
		projection.add(Property.forName("ADD.".concat(AelAtendimentoDiversos.Fields.PRONTUARIO.toString())),
				AelAmostrasVO.Fields.ATV_PRONTUARIO.toString());
				
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq("AMO.".concat(AelAmostras.Fields.SOE_SEQ.toString()), soeSeq));
		criteria.add(Restrictions.eq("AMO.".concat(AelAmostras.Fields.SEQP.toString()), seqp));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelAmostrasVO.class));
		
		return (AelAmostrasVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Lista todas as amostras por agendamento
	 * 
	 * @param hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda
	 * @return lista de amostras
	 */
	@SuppressWarnings("unchecked")
	public List<AelAmostras> listarAmostrasPorAgendamento(final Short hedGaeUnfSeq, final Integer hedGaeSeqp, final Date hedDthrAgenda) {
		StringBuffer hql = new StringBuffer(300);
		hql.append(" select amo from " + AelAmostras.class.getSimpleName() + " amo ");
		hql.append(" where (amo." + AelAmostras.Fields.SOE_SEQ.toString() + " , amo." + AelAmostras.Fields.SEQP.toString() + ") ");
		hql.append(" 	in ( ");
		hql.append(" select aie." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString() + ", aie."+AelAmostraItemExames.Fields.AMO_SEQP);
		hql.append(" from " + AelAmostraItemExames.class.getSimpleName() + " aie ");
		hql.append(" where (aie." + AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString() + " , aie." + AelAmostraItemExames.Fields.ISE_SEQP.toString() + ") ");
		hql.append(" 	in ( ");
		hql.append(" select iha." + AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString() + ", iha."+AelItemHorarioAgendado.Fields.ISE_SEQP);
		hql.append(" from " + AelItemHorarioAgendado.class.getSimpleName() + " iha ");
		hql.append(" where iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString()).append(" = :hedGaeUnfSeq ");
		hql.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString()).append(" = :hedGaeSeqp ");
		hql.append(" and iha.").append(AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString()).append(" = :hedDthrAgenda ))");
		hql.append(" order by amo.").append(AelAmostras.Fields.SOE_SEQ.toString()).append(", amo.").append(AelAmostras.Fields.SEQP.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("hedGaeUnfSeq", hedGaeUnfSeq);
		query.setParameter("hedGaeSeqp", hedGaeSeqp);
		query.setParameter("hedDthrAgenda", hedDthrAgenda);

		return query.list();
	}
	
	public AelAmostras obterAmostraPorId(Integer amoSoeSeq, Short amoSeqP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class);
		
		criteria.add(Restrictions.eq(AelAmostras.Fields.SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq(AelAmostras.Fields.SEQP.toString(), amoSeqP));
		
		criteria.setFetchMode(AelAmostras.Fields.RECIPIENTE_COLETA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelAmostras.Fields.ANTICOAGULANTE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AelAmostras.Fields.SALA_EXECUTORA_EXAME.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(
				AelAmostras.Fields.SALA_EXECUTORA_EXAME.toString()
						+ "."
						+ AelSalasExecutorasExames.Fields.UNIDADE_FUNCIONAL
								.toString(), FetchMode.JOIN);
				
		List<AelAmostras> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	/**
	 * @ORADB V_AEL_MAT_COLETA_ENF_AMO
	 */
	public DetachedCriteria materiaisColetaEnfermagemAmostra(
			AghUnidadesFuncionais unidadeFuncional, Integer prontuarioPaciente,
			Integer soeSeq, DominioSituacaoAmostra situacao) {

		DetachedCriteria criteriaAie = DetachedCriteria.forClass(AelAmostraItemExames.class, "aie");
		DetachedCriteria criteriaAmo = criteriaAie.createCriteria(AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "amo");
		DetachedCriteria criteriaIse = criteriaAie.createCriteria(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		DetachedCriteria criteriaSoe = criteriaAmo.createCriteria(AelAmostras.Fields.SOLICITACAO_EXAME.toString(), "soe");
		DetachedCriteria criteriaAtd = criteriaSoe.createCriteria(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		DetachedCriteria criteriaTae = DetachedCriteria.forClass(AelTipoAmostraExame.class, "tae");
		
		criteriaAtd.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		
		criteriaAtd.add(Restrictions.eq(AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		
		DominioSituacaoAmostra[] situacoes = {DominioSituacaoAmostra.G, DominioSituacaoAmostra.C, DominioSituacaoAmostra.M};
		
		criteriaAmo.add(Restrictions.in("amo." + AelAmostras.Fields.SITUACAO.toString(), situacoes));
		
		criteriaIse.add(Restrictions.ne(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "PE"));
		
		criteriaSoe.add(Restrictions.eqProperty("soe." + AelSolicitacaoExames.Fields.SEQ.toString(),
				"amo." + AelAmostras.Fields.SOE_SEQ.toString()));
		
		criteriaAie.add(Subqueries.exists(criteriaTae
				.setProjection(Property.forName("tae." + AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString()))
				.add(Restrictions.eqProperty("tae."	+ AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString(),
						"ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()))
				.add(Restrictions.eqProperty("tae."	+ AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString(),
						"ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()))
				.add(Restrictions.eqProperty("tae."	+ AelTipoAmostraExame.Fields.MAN_SEQ.toString(),
						"ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()))
				.add(Restrictions.or(						
								Restrictions.eq("tae." + AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(),
										DominioOrigemAtendimento.T),
								Restrictions.eqProperty("tae." + AelTipoAmostraExame.Fields.ORIGEM_ATENDIMENTO.toString(),
										"atd." + AghAtendimentos.Fields.ORIGEM.toString())))
				.add(Restrictions.eq("tae." + AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(),
						DominioResponsavelColetaExames.P))
				));
		
		criteriaAie.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("atd." + AghAtendimentos.Fields.SEQ.toString()), "atdSeq")
				.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), "nomePaciente")
				.add(Projections.property("amo." + AelAmostras.Fields.SOE_SEQ.toString()), "soeSeq")
				.add(Projections.property("amo." + AelAmostras.Fields.SEQP.toString()), "amoSeqp")
				.add(Projections.property("amo." + AelAmostras.Fields.NRO_UNICO.toString()), "nroUnico")
				.add(Projections.property("amo." + AelAmostras.Fields.DT_NUMERO_UNICO.toString()), "dtNumeroUnico")
				.add(Projections.property("amo." + AelAmostras.Fields.SITUACAO.toString()), "situacao")
				.add(Projections.property("soe." + AelSolicitacaoExames.Fields.UNF_SEQ.toString()), "unfSeq")));
		
		if(unidadeFuncional != null) {
			criteriaSoe.add(Restrictions.eq(AelSolicitacaoExames.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		}
		
		if(prontuarioPaciente != null) {
			criteriaAtd.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), prontuarioPaciente));
		}
		
		if(situacao != null) {
			criteriaAmo.add(Restrictions.eq("amo." + AelAmostras.Fields.SITUACAO.toString(), situacao));
		}
		
		if(soeSeq != null) {
			criteriaAmo.add(Restrictions.eq("amo." + AelAmostras.Fields.SOE_SEQ.toString(), soeSeq));
		}
		
		criteriaAie.setResultTransformer(Transformers.aliasToBean(MateriaisColetarEnfermagemAmostraVO.class));
		
		return criteriaAie;
	}
	
	public List<MateriaisColetarEnfermagemAmostraVO> pesquisarMateriaisColetaEnfermagemAmostra(
			AghUnidadesFuncionais unidadeFuncional, Integer prontuarioPaciente,
			Integer soeSeq, DominioSituacaoAmostra situacao) {

		DetachedCriteria criteriaAie = this.materiaisColetaEnfermagemAmostra(
				unidadeFuncional, prontuarioPaciente, soeSeq, situacao);

		List<MateriaisColetarEnfermagemAmostraVO> result = executeCriteria(criteriaAie);

		return result;
	}
	
	public List<MateriaisColetarEnfermagemAmostraItemExamesVO> pesquisarMateriaisColetarEnfermagemPorAmostra(
			Integer amoSoeSeq, Short amoSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExames.class, "AIE");
		DetachedCriteria criteriaIse = criteria.createCriteria(AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE");

		criteriaIse.createAlias("ISE." + AelItemSolicitacaoExames.Fields.EXAME_MAT_ANALISE.toString(), "VEM");
		criteriaIse.createAlias("ISE." + AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "unfDescricao")
				.add(Projections.property("VEM." + VAelExameMatAnalise.Fields.NOME_USUAL_MATERIAL.toString()), "nomeUsualMaterial")
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString()), "dthrProgramada")
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.NRO_AMOSTRAS.toString()), "nroAmostras")
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.INTERVALO_DIAS.toString()), "intervaloDias")
				.add(Projections.property("ISE." +  AelItemSolicitacaoExames.Fields.INTERVALO_HORAS.toString()), "intervaloHoras")
				.add(Projections.property("AIE." + AelAmostraItemExames.Fields.SITUACAO.toString()), "amoItemSituacao")
				.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()), "iseSeqp"));

		criteria.add(Restrictions.eq("AIE."	+ AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq("AIE."	+ AelAmostraItemExames.Fields.AMO_SEQP.toString(), amoSeqp.intValue()));

		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(MateriaisColetarEnfermagemAmostraItemExamesVO.class));

		List<MateriaisColetarEnfermagemAmostraItemExamesVO> result = executeCriteria(criteria);

		return result;
	}
}
