package br.gov.mec.aghu.internacao.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.faturamento.vo.AinMovimentosInternacaoVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapServidores;

@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class AinMovimentoInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinMovimentosInternacao> {

	private static final long serialVersionUID = -4724374759246057878L;

	static final String ALIAS_INTERNACAO = "internacao";

	static final String SEPARADOR = ".";

	private static final int UM_DIA = 1;

	private DetachedCriteria obterDetachedCriteriaAinMovimentoInternacao() {
		return DetachedCriteria.forClass(AinMovimentosInternacao.class);
	}
	
	/**
	 * Método para obter o movimento de internação pelo seu ID
	 * 
	 * @param seq
	 * @return
	 */
	public AinMovimentosInternacao obterMovimentoInternacao(final Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.idEq(seq));
		return (AinMovimentosInternacao) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Primeiro MovimentosInternacao filtrando pelo maior seq de Internacao de uma
	 * ContaInternacao e também pela DthrLancamento.
	 * 
	 * @param cthSeq
	 * @param dthrLancamento
	 * @return List<AinMovimentosInternacao>
	 */
	public AinMovimentosInternacao buscarPrimeiroMovimentosInternacaoPorMaiorInternacaoDeContaInternacaoEDthrLancamento(final Integer cthSeq, final Date dthrLancamento) {
		final DetachedCriteria criteria = obterDetachedCriteriaAinMovimentoInternacao();

		criteria.add(Restrictions.le(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dthrLancamento));

		final DetachedCriteria subquery = DetachedCriteria.forClass(FatContasInternacao.class);
		subquery.setProjection(Projections.max(FatContasInternacao.Fields.INT_SEQ.toString()));
		subquery.add(Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));

		criteria.add(Subqueries.propertyEq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(),subquery));

		criteria.addOrder(Order.desc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));

		final List<AinMovimentosInternacao> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null; 
	}

	/**
	 * @ORADB PACKAGE AINK_IH_UTIL CURSOR data_m
	 * 
	 * @param seqInternacao
	 * @param dataLancamento
	 * @param dataAlta
	 * @return
	 */
	public List<AinMovimentosInternacao> pesquisarMovimentoInternacao(final Integer seqInternacao,
			final Date dataLancamento, final Date dataAlta) {

		final String separador = ".";

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(),
				AinMovimentosInternacao.Fields.INTERNACAO.toString());

		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO.toString()
				+ separador + AinInternacao.Fields.SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.gt(
				AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dataLancamento));

		// TODO inserir como parametros os valores abaixo
		final List<Integer> restricao = new ArrayList<Integer>();
		restricao.add(Integer.valueOf("2"));
		restricao.add(Integer.valueOf("11"));
		restricao.add(Integer.valueOf("12"));
		restricao.add(Integer.valueOf("13"));
		restricao.add(Integer.valueOf("14"));
		restricao.add(Integer.valueOf("15"));
		restricao.add(Integer.valueOf("21"));
		criteria.add(Restrictions.in(
				AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), restricao));

		criteria.addOrder(Order.asc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));

		final List<AinMovimentosInternacao> movimentoInternacaoList = this.executeCriteria(criteria);

		// Código equivalente a
		// "decode(a.tmi_seq, 21, p_dt_alta, a.dthr_lancamento)"
		for (final AinMovimentosInternacao movimento : movimentoInternacaoList) {
			// TODO buscar 21 do de parametro
			if (movimento.getTipoMovimentoInternacao().getCodigo() == Integer.valueOf("21")) {
				movimento.setDthrLancamento(dataLancamento);
			}
		}

		return movimentoInternacaoList;
	}

	public List<Object> pesquisarMovtsInternacao(final List<Integer> tipoMovimentoInternacaoList,
			final Date mesCompetencia, final Date ultimoDiaMes, final Date diaMaxMesMaisUm) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(),
				ALIAS_INTERNACAO);
		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(),
				"especialidade");
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.PACIENTE.toString(), "paciente");

		// Cria projeção de valores que serão selecionados
		criteria.setProjection(this.criarProjectionMovimentosInternacao());

		// Aplica restrições de datas
		criteria.add(this.criarRestricaoDatasMovimentosInternacao(mesCompetencia, ultimoDiaMes,
				diaMaxMesMaisUm));

		criteria.add(Restrictions.lt(
				AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), diaMaxMesMaisUm));
		criteria.add(Restrictions.in(
				AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(),
				tipoMovimentoInternacaoList));

		// @alterado
		// criteria.createAlias("unidadeFuncional", "unidadeFuncional");
		// criteria.add(Restrictions.eq("unidadeFuncional.seq",
		// Short.valueOf("116")));
		// criteria.add(Restrictions.or(Restrictions.eq("unidadeFuncional.seq",
		// Short.valueOf("103")), Restrictions.eq("unidadeFuncional.seq",
		// Short.valueOf("105"))));

		// Cláusula Order by da query
		criteria.addOrder(Order.asc(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString()));
		criteria.addOrder(Order.asc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Método para criar projeção a ser usada na busca de movimentos de
	 * internação
	 * 
	 * @return
	 */
	private Projection criarProjectionMovimentosInternacao() {
		final String aliasEspecialidade = "especialidade";

		final String prontuario = "paciente" + SEPARADOR + AipPacientes.Fields.PRONTUARIO.toString();
		final String sigla = aliasEspecialidade + SEPARADOR + AghEspecialidades.Fields.SIGLA.toString();
		final String intSeq = ALIAS_INTERNACAO + SEPARADOR + AinInternacao.Fields.SEQ.toString();
		final String atuSeq = ALIAS_INTERNACAO + SEPARADOR
				+ AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString() + SEPARADOR
				+ AinAtendimentosUrgencia.Fields.SEQ.toString();
		final String tamCodigo = ALIAS_INTERNACAO + SEPARADOR
				+ AinInternacao.Fields.TIPO_ALTA_MEDICA.toString() + SEPARADOR
				+ AinTiposAltaMedica.Fields.CODIGO.toString();
		final String dthrAltaMedica = ALIAS_INTERNACAO + SEPARADOR
				+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString();
		final String ltoLtoId = ALIAS_INTERNACAO + SEPARADOR + AinInternacao.Fields.LEITO + SEPARADOR
				+ AinLeitos.Fields.LTO_ID.toString();

		final String espSeq = aliasEspecialidade + SEPARADOR + AghEspecialidades.Fields.SEQ.toString();

		final String dthrLancamento = AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString();
		final String movimentoInternacaoSeq = AinMovimentosInternacao.Fields.SEQ.toString();

		final Projection prj = Projections.projectionList().add(Projections.property(prontuario))
				.add(Projections.property(sigla)).add(Projections.property(intSeq))
				.add(Projections.property(atuSeq)).add(Projections.property(tamCodigo))
				.add(Projections.property(dthrAltaMedica)).add(Projections.property(ltoLtoId))
				.add(Projections.property(espSeq)).add(Projections.property(dthrLancamento))
				.add(Projections.property(movimentoInternacaoSeq));

		return prj;
	}

	/**
	 * Método para implementar todas restrições de datas feitas no ínicio da
	 * query do cursor "movimentos"
	 * 
	 * @param ultimoDiaMes
	 * @return
	 */
	private Criterion criarRestricaoDatasMovimentosInternacao(final Date mesCompetencia,
			final Date ultimoDiaMes, final Date diaMaxMesMaisUm) {

		// Restrições da query
		final Criterion c1Restricao1 = Restrictions.ge(ALIAS_INTERNACAO + SEPARADOR
				+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA,
				DateUtils.truncate(mesCompetencia, Calendar.DAY_OF_MONTH));
		final Criterion c2Restricao1 = Restrictions.lt(ALIAS_INTERNACAO + SEPARADOR
				+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA, diaMaxMesMaisUm);
		final Criterion restricao1 = Restrictions.and(c1Restricao1, c2Restricao1);

		final Criterion c1Restricao2 = Restrictions.eq(ALIAS_INTERNACAO + SEPARADOR
				+ AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true);
		final Criterion c2Restricao2 = Restrictions.gt(ALIAS_INTERNACAO + SEPARADOR
				+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), ultimoDiaMes);
		final Criterion c3Restricao2 = Restrictions.or(c1Restricao2, c2Restricao2);
		final Criterion c4Restricao2 = Restrictions.lt(ALIAS_INTERNACAO + SEPARADOR
				+ AinInternacao.Fields.DT_INTERNACAO.toString(), diaMaxMesMaisUm);
		final Criterion restricao2 = Restrictions.and(c3Restricao2, c4Restricao2);
		final Criterion restricao = Restrictions.or(restricao1, restricao2);

		return restricao;
	}
	
	public List<AinMovimentosInternacao> listarMovimentosInternacao(final Integer intSeq, final Date dtIniAtd, final Date dtFimCta,
			final Integer[] codigosTipoMovimentoInternacao) {		
		final Calendar calDtIniAtd = Calendar.getInstance();
		if(dtIniAtd != null){
			calDtIniAtd.setTime(dtIniAtd);
			calDtIniAtd.set(Calendar.HOUR_OF_DAY, 0);
			calDtIniAtd.set(Calendar.MINUTE, 0);
			calDtIniAtd.set(Calendar.SECOND, 0);
			calDtIniAtd.set(Calendar.MILLISECOND, 0);
		}

		final Calendar calDtFimCta = Calendar.getInstance();
		if(dtFimCta != null){
			calDtFimCta.setTime(dtFimCta);
			calDtFimCta.set(Calendar.HOUR_OF_DAY, 23);
			calDtFimCta.set(Calendar.MINUTE, 59);
			calDtFimCta.set(Calendar.SECOND, 59);
			calDtFimCta.set(Calendar.MILLISECOND, 999);
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), intSeq));

		criteria.add(Restrictions.ge(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dtIniAtd != null ? calDtIniAtd.getTime() : null));

		criteria.add(Restrictions.le(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dtFimCta != null ? calDtFimCta.getTime() : null));

		criteria.add(Restrictions.not(Restrictions.in(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(),
				codigosTipoMovimentoInternacao)));

		criteria.addOrder(Order.asc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));

		return executeCriteria(criteria);
	}

	public Date buscarDataUltimoIngresso(final Integer internacaoSeq,	final Short unidadeSeq, final List<Integer> listaSeqMotivos) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unidadeSeq));
		criteria.add(Restrictions.in(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), listaSeqMotivos) );
		criteria.setProjection(Projections.max(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<AinMovimentosInternacaoVO> listarMotivosInternacaoVOOrdenadoPorDthrLancamento(final Integer intSeq, final Date dtInicio, final Date dtFim,
			final Integer[] tiposMovimentoIgnorados) {
		final StringBuffer hql = new StringBuffer(300);
		
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.AinMovimentosInternacaoVO(");
		hql.append(" mvi.").append(AinMovimentosInternacao.Fields.INT_SEQ.toString());
		hql.append(" , mvi.").append(AinMovimentosInternacao.Fields.CRIADO_EM.toString());
		hql.append(" , mvi.").append(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString());
        hql.append(" , mvi.").append(AinMovimentosInternacao.Fields.TMI_SEQ.toString());
        hql.append(" , mvi.").append(AinMovimentosInternacao.Fields.QRT_NUMERO.toString());
        hql.append(" , mvi.").append(AinMovimentosInternacao.Fields.LTO_LTO_ID.toString());
        hql.append(" , mvi.").append(AinMovimentosInternacao.Fields.UNF_SEQ.toString());
        hql.append(" , mvi.").append(AinMovimentosInternacao.Fields.ESP_SEQ.toString());
		hql.append(") from ").append(AinMovimentosInternacao.class.getSimpleName()).append(" as mvi ");
		hql.append(" where mvi.").append(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()).append(" >= :dtInicio ");
		hql.append(" 	and mvi.").append(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()).append(" <= :dtFim ");
		hql.append(" 	and mvi.").append(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString()).append(" = :intSeq ");
		hql.append(" 	and mvi.").append(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString()).append(" not in (:tiposMovimentoIgnorados) ");
		hql.append(" order by mvi.").append(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString());
		
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("intSeq", intSeq);
		query.setParameterList("tiposMovimentoIgnorados", tiposMovimentoIgnorados);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtInicio);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		query.setParameter("dtInicio", cal.getTime());
		
		cal = Calendar.getInstance();
		cal.setTime(dtFim);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		query.setParameter("dtFim", cal.getTime());
		
		return query.list();
	}
	
	/**
	 *  Metodo para listar filtrando pelo id de uma determinada internacao e 
	 *  pelo 'criado em' menor do que o parametro de data informado.
	 *  
	 * @param intSeq
	 * @param criadoEm
	 * @param colunaOrder : coluna que deve ser usada para ordenacao (nao obrigatorio)
	 * @param order : sentido da ordenacao -> true(asc), false(desc) -> (nao obrigatorio)
	 * @return List<AinMovimentosInternacao>
	 */
	public List<AinMovimentosInternacao> listarMotivosInternacaoPorInternacaoECriadoem(final Integer intSeq, final Date criadoEm, final String colunaOrder, final Boolean order){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), ALIAS_INTERNACAO);
		
		criteria.add(Restrictions.eq(ALIAS_INTERNACAO + SEPARADOR + AinInternacao.Fields.SEQ.toString(), intSeq));
		
		//Conferir com Hoffman se o correto é realmente criado_em (conforme a procedure)
		//criteria.add(Restrictions.le(AinMovimentosInternacao.Fields.CRIADO_EM.toString(), criadoEm));
		criteria.add(Restrictions.le(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), criadoEm));
		
		if(StringUtils.isNotBlank(colunaOrder) && order != null){
			if(order){
				criteria.addOrder(Order.asc(colunaOrder));
			}else{
				criteria.addOrder(Order.desc(colunaOrder));
			}
		}

		return executeCriteria(criteria);
		
	}
			
	public Object[] pesquisaAinMovimentosInternacao(final Integer codMvtoInt, final Integer seq) {
		final DetachedCriteria criterio = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		criterio.createAlias(AinMovimentosInternacao.Fields.SERVIDOR_GERADO.toString(),
				AinMovimentosInternacao.Fields.SERVIDOR_GERADO.toString());

		criterio.add(Restrictions.and(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), seq),
				Restrictions.eq(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), codMvtoInt)));

		criterio.setProjection(Projections
				.projectionList()
				.add(// 0
				Projections.property(AinMovimentosInternacao.Fields.SERVIDOR_GERADO.toString() + "."
						+ RapServidores.Fields.MATRICULA.toString()))
				.add(// 0
				Projections.property(AinMovimentosInternacao.Fields.SERVIDOR_GERADO.toString() + "."
						+ RapServidores.Fields.CODIGO_VINCULO.toString())));

		return (Object[]) executeCriteriaUniqueResult(criterio);
	}

	public List<Object[]> obterCensoUnion1(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status,
			final Integer numeroDiasCenso, final Integer codigoLeitoBloqueioLimpeza) throws ApplicationBusinessException {

		final DetachedCriteria cri = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		cri.setProjection(Projections.projectionList()
				.add(Projections.property(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString()))
				.add(Projections.property("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.PRONTUARIO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.CODIGO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.NOME.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.DT_INTERNACAO.toString()))
				.add(Projections.property("ESP" + "." + AghEspecialidades.Fields.SIGLA.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.TAM_CODIGO.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.SEQ.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.QUARTO.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.LEITO.toString()))
				.add(Projections.property("PRF.id.matricula")).add(Projections.property("PRF.id.vinCodigo"))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.CONVENIO_CODIGO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.DATA_NASCIMENTO.toString()))
				.add(Projections.property("CNV" + "." + FatConvenioSaude.Fields.DESCRICAO.toString()))
				.add(Projections.property("TAM" + "." + AinTiposAltaMedica.Fields.DESCRICAO.toString()))
				.add(Projections.property("ESP" + "." + AghEspecialidades.Fields.NOME.toString())));

		cri.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), "INT");
		cri.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "ESP");
		cri.createAlias(AinMovimentosInternacao.Fields.SERVIDOR.toString(), "PRF", Criteria.LEFT_JOIN);
		cri.createAlias("INT." + AinInternacao.Fields.PACIENTE.toString(), "PAC");
		cri.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		cri.createAlias("INT." + AinInternacao.Fields.CONVENIO.toString(), "CNV", Criteria.LEFT_JOIN);
		cri.createAlias("INT." + AinInternacao.Fields.TIPO_ALTA_MEDICA.toString(), "TAM", Criteria.LEFT_JOIN);

		cri.add(Restrictions.ne(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), codigoLeitoBloqueioLimpeza));

		cri.add(Restrictions.gt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), obterData(data, -numeroDiasCenso)));
		cri.add(Restrictions.lt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), obterData(data, +UM_DIA)));

		if (unfSeq != null) {
			cri.add(Restrictions.eq(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}

		if (unfSeqMae != null) {
			cri.add(Restrictions.or(Restrictions.eq("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString(), unfSeqMae),
					Restrictions.eq(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeqMae)));
		}

		return executeCriteria(cri);
	}

	private Date obterData(final Date data, final Integer numDiasCalculo) {
		final Calendar dataCalculo = Calendar.getInstance();

		dataCalculo.setTime(data);
		dataCalculo.add(Calendar.DATE, numDiasCalculo);
		dataCalculo.set(Calendar.HOUR_OF_DAY, 0);
		dataCalculo.set(Calendar.MINUTE, 0);
		dataCalculo.set(Calendar.SECOND, 0);
		dataCalculo.set(Calendar.MILLISECOND, 0);
		
		return dataCalculo.getTime();
	}
	
	public List<Object[]> obterCensoUnion2(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status,
			final Integer numeroDiasCenso) throws ApplicationBusinessException {

		final DetachedCriteria cri = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		cri.setProjection(Projections.projectionList()
				.add(Projections.property(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString()))
				.add(Projections.property("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.PRONTUARIO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.CODIGO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.NOME.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.DT_INTERNACAO.toString()))
				.add(Projections.property("ESP.sigla"))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.TAM_CODIGO.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.SEQ.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.QUARTO.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.LEITO.toString()))
				.add(Projections.property("PRF.id.matricula")).add(Projections.property("PRF.id.vinCodigo"))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.CONVENIO_CODIGO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.DATA_NASCIMENTO.toString()))
				.add(Projections.property("CNV" + "." + FatConvenioSaude.Fields.DESCRICAO.toString()))
				.add(Projections.property("TAM" + "." + AinTiposAltaMedica.Fields.DESCRICAO.toString()))
				.add(Projections.property("ESP" + "." + AghEspecialidades.Fields.NOME.toString())));

		cri.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), "INT");
		cri.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "ESP");
		cri.createAlias(AinMovimentosInternacao.Fields.SERVIDOR.toString(), "PRF", Criteria.LEFT_JOIN);
		cri.createAlias("INT." + AinInternacao.Fields.PACIENTE.toString(), "PAC");
		cri.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		cri.createAlias("INT." + AinInternacao.Fields.CONVENIO.toString(), "CNV", Criteria.LEFT_JOIN);
		cri.createAlias("INT." + AinInternacao.Fields.TIPO_ALTA_MEDICA.toString(), "TAM", Criteria.LEFT_JOIN);
		// TODO remover hardcode do codigo do TipoMovtoInternacao.
		cri.add(Restrictions.eq(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), Integer.valueOf("1")));

		cri.add(Restrictions.gt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), obterData(data, -numeroDiasCenso)));
		cri.add(Restrictions.lt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), obterData(data, +UM_DIA)));

		if (unfSeq != null) {
			cri.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		}

		if (unfSeqMae != null) {
			cri.add(Restrictions.or(Restrictions.eq("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString(), unfSeqMae),
					Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeqMae)));
		}

		return executeCriteria(cri);
	}
	
	public List<Object[]> obterCensoUnion3(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status,
			final Integer numeroDiasCenso, final Integer codigoLeitoBloqueioLimpeza) throws ApplicationBusinessException {

		final DetachedCriteria cri = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		cri.setProjection(Projections.projectionList()
				.add(Projections.property(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString()))
				.add(Projections.property("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.PRONTUARIO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.CODIGO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.NOME.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.DT_INTERNACAO.toString()))
				.add(Projections.property("ESP.sigla"))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.TAM_CODIGO.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.SEQ.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.QUARTO.toString()))
				.add(Projections.property(AinMovimentosInternacao.Fields.LEITO.toString()))
				.add(Projections.property("PRF.id.matricula")).add(Projections.property("PRF.id.vinCodigo"))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.CONVENIO_CODIGO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.DATA_NASCIMENTO.toString()))
				.add(Projections.property("CNV" + "." + FatConvenioSaude.Fields.DESCRICAO.toString()))
				.add(Projections.property("TAM" + "." + AinTiposAltaMedica.Fields.DESCRICAO.toString()))
				.add(Projections.property("ESP" + "." + AghEspecialidades.Fields.NOME.toString())));

		cri.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), "INT");
		cri.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "ESP");
		cri.createAlias(AinMovimentosInternacao.Fields.SERVIDOR.toString(), "PRF", Criteria.LEFT_JOIN);
		cri.createAlias("INT." + AinInternacao.Fields.PACIENTE.toString(), "PAC");
		cri.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		cri.createAlias("INT." + AinInternacao.Fields.CONVENIO.toString(), "CNV", Criteria.LEFT_JOIN);
		cri.createAlias("INT." + AinInternacao.Fields.TIPO_ALTA_MEDICA.toString(), "TAM", Criteria.LEFT_JOIN);

		cri.add(Restrictions.eq(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), codigoLeitoBloqueioLimpeza));

		cri.add(Restrictions.gt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), obterData(data, -numeroDiasCenso)));
		cri.add(Restrictions.lt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), obterData(data, +UM_DIA)));

		if (unfSeq != null) {
			cri.add(Restrictions.eq(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}

		if (unfSeqMae != null) {
			cri.add(Restrictions.or(Restrictions.eq("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString(), unfSeqMae),
					Restrictions.eq(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeqMae)));

		}

		return executeCriteria(cri);
	}
	
	public List<Object[]> obterCensoUnion4(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status,
			final Integer numeroDiasCenso, final Integer codigoLeitoBloqueioLimpeza) throws ApplicationBusinessException {

		final DetachedCriteria cri = DetachedCriteria.forClass(AinMovimentosInternacao.class, "MVT");

		cri.setProjection(Projections.projectionList()
				.add(Projections.property("MVT" + "." + AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString()))
				.add(Projections.property("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.PRONTUARIO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.CODIGO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.NOME.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.DT_INTERNACAO.toString()))
				.add(Projections.property("ESP.sigla"))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.TAM_CODIGO.toString()))
				.add(Projections.property("MVT" + "." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.SEQ.toString()))
				.add(Projections.property("MVT" + "." + AinMovimentosInternacao.Fields.QUARTO.toString()))
				.add(Projections.property("MVT" + "." + AinMovimentosInternacao.Fields.LEITO.toString()))
				.add(Projections.property("PRF.id.matricula")).add(Projections.property("PRF.id.vinCodigo"))
				.add(Projections.property("INT" + "." + AinInternacao.Fields.CONVENIO_CODIGO.toString()))
				.add(Projections.property("PAC" + "." + AipPacientes.Fields.DATA_NASCIMENTO.toString()))
				.add(Projections.property("CNV" + "." + FatConvenioSaude.Fields.DESCRICAO.toString()))
				.add(Projections.property("TAM" + "." + AinTiposAltaMedica.Fields.DESCRICAO.toString()))
				.add(Projections.property("ESP" + "." + AghEspecialidades.Fields.NOME.toString())));

		cri.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), "INT");
		cri.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "ESP");
		cri.createAlias(AinMovimentosInternacao.Fields.SERVIDOR.toString(), "PRF", Criteria.LEFT_JOIN);
		cri.createAlias("INT." + AinInternacao.Fields.PACIENTE.toString(), "PAC");
		cri.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		cri.createAlias("INT." + AinInternacao.Fields.CONVENIO.toString(), "CNV", Criteria.LEFT_JOIN);
		cri.createAlias("INT." + AinInternacao.Fields.TIPO_ALTA_MEDICA.toString(), "TAM", Criteria.LEFT_JOIN);

		cri.add(Restrictions.ne(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), codigoLeitoBloqueioLimpeza));

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class, "MVT_WHERE");
		criteria.setProjection(Projections.projectionList().add(
				Projections.property("MVT_WHERE" + "." + AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString())));
		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF_WHERE");

		final DetachedCriteria subquery = DetachedCriteria.forClass(AinMovimentosInternacao.class, "MVT_WHERE_SUB");
		subquery.setProjection(Projections.max("MVT_WHERE_SUB" + "." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		subquery.add(Restrictions.eqProperty("MVT_WHERE_SUB" + "." + AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), "MVT"
				+ "." + AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString()));
		subquery.add(Restrictions.ltProperty("MVT_WHERE_SUB" + "." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(),
				"MVT" + "." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));

		criteria.add(Restrictions.eqProperty("MVT_WHERE" + "." + AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), "MVT" + "."
				+ AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString()));
		criteria.add(Subqueries.propertyEq(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), subquery));

		cri.add(Subqueries.propertyIn(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), criteria));

		cri.add(Restrictions.gt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), obterData(data, -numeroDiasCenso)));
		cri.add(Restrictions.lt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), obterData(data, +UM_DIA)));

		if (unfSeq != null) {
			cri.add(Restrictions.eq(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}

		if (unfSeqMae != null) {
			cri.add(Restrictions.or(Restrictions.eq("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString(), unfSeqMae),
					Restrictions.eq(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeqMae)));
		}

		return executeCriteria(cri);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion5(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status,
			final Integer numeroDiasCenso) throws ApplicationBusinessException {

		final StringBuffer hql = new StringBuffer(2100);
		hql.append(" select ");
		hql.append(" 	unf.seq,  un.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then (unf.andar ||' '|| ala.codigo) else (qrt.descricao ||'') end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	atu.dtAtendimento, esp.sigla, tpa.codigo, mv.dthrLancamento,");
		hql.append(" 	atu.seq, ser.id.matricula, ser.id.vinCodigo, cnv.codigo,");
		hql.append("	case when ltoAtu.leitoID is null then ( case when qrtAtu.descricao is null then '' else (qrtAtu.descricao) end ) else ltoAtu.leitoID end,");
		hql.append(" 	add_days(mv.dthrLancamento,1), mv.dthrLancamento, pac.dtNascimento, cnv.descricao, tpa.descricao, esp.nomeEspecialidade");
		hql.append(" from AghUnidadesFuncionais un, AinMovimentosInternacao mv ");
		hql.append(" join mv.especialidade as esp ");
		hql.append(" left join mv.servidor as ser ");
		hql.append(" left join mv.tipoMovimentoInternacao as tmv ");
		hql.append(" left join mv.quarto as qrt");
		hql.append(" left join mv.leito as lto");
		hql.append(" left join mv.unidadeFuncional as unf ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join unf.indAla as ala ");
		hql.append(" join mv.internacao as int ");
		hql.append(" join int.paciente as pac ");
		hql.append(" left join int.convenioSaude as cnv ");
		hql.append(" left join int.tipoAltaMedica as tpa");
		hql.append(" left join int.atendimentoUrgencia as atu ");
		hql.append(" left join atu.quarto as qrtAtu");
		hql.append(" left join atu.leito as ltoAtu");
		hql.append(" where tmv.codigo = :tipoMovInt ");
		hql.append(" and mv.dthrLancamento > :dthrLancamentoMaior ");
		hql.append(" and mv.dthrLancamento < :dthrLancamentoMenor ");
		hql.append(" and atu.seq is not null");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unf.unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		hql.append(" and mv.unidadeFuncional.seq <> (");
		hql.append(" select b.seq");
		hql.append(" from AinMovimentosAtendUrgencia a ");
		hql.append(" join a.unidadeFuncional as b ");
		hql.append(" where a.dthrLancamento = (");
		hql.append(" select max(a1.dthrLancamento) ");
		hql.append(" from AinMovimentosAtendUrgencia a1 ");
		hql.append(" where a1.id.seqAtendimentoUrgencia = atu.seq");
		hql.append(" and a1.dthrLancamento < add_days(mv.dthrLancamento,1))");
		hql.append(" and a.id.seqAtendimentoUrgencia = atu.seq");
		hql.append(')');

		hql.append(" and un.seq = (");
		hql.append(" select b.seq ");
		hql.append(" from AinMovimentosAtendUrgencia a ");
		hql.append(" join a.unidadeFuncional as b ");
		hql.append(" where a.dthrLancamento = (");
		hql.append(" select max(a1.dthrLancamento) ");
		hql.append(" from AinMovimentosAtendUrgencia a1 ");
		hql.append(" where id.seqAtendimentoUrgencia = atu.seq");
		hql.append(" and a1.dthrLancamento < add_days(mv.dthrLancamento,1))");
		hql.append(" and a.id.seqAtendimentoUrgencia = atu.seq");
		hql.append(')');

		final Query query = createHibernateQuery(hql.toString());

		query.setParameter("dthrLancamentoMaior", obterData(data, -numeroDiasCenso));
		query.setParameter("dthrLancamentoMenor", obterData(data, +UM_DIA));

		query.setParameter("tipoMovInt", Integer.valueOf("1"));
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion6(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status,
			final Integer numeroDiasCenso) throws ApplicationBusinessException {

		final StringBuffer hql = new StringBuffer(1430);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	int.dthrInternacao, esp.sigla, tpa.codigo, mv.dthrLancamento,");
		hql.append(" 	int.seq, ser.id.matricula, ser.id.vinCodigo, cnv.codigo, atu.seq, add_days(mv.dthrLancamento,1), pac.dtNascimento, cnv.descricao, tpa.descricao, esp.nomeEspecialidade");
		hql.append(" from AinMovimentosInternacao mv");
		hql.append(" join mv.internacao as int ");
		hql.append(" join mv.especialidade as esp ");
		hql.append(" left join mv.servidor as ser ");
		hql.append(" left join mv.tipoMovimentoInternacao as tmv ");
		hql.append(" join int.paciente as pac ");
		hql.append(" left join mv.unidadeFuncional as unf ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join int.atendimentoUrgencia as atu ");
		hql.append(" left join int.convenioSaude as cnv ");
		hql.append(" left join int.tipoAltaMedica as tpa");
		hql.append(" left join mv.quarto as qrt");
		hql.append(" left join mv.leito as lto");

		hql.append(" where tmv.codigo = :tipoMovInt ");
		hql.append(" and mv.dthrLancamento > :dthrLancamentoMaior ");
		hql.append(" and mv.dthrLancamento < :dthrLancamentoMenor ");
		hql.append(" and atu.seq is not null");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		hql.append(" and unf.seq <> (");
		hql.append(" select b.seq");
		hql.append(" from AinMovimentosAtendUrgencia a ");
		hql.append(" join a.unidadeFuncional as b ");
		hql.append(" where a.dthrLancamento = (");
		hql.append(" select max(a1.dthrLancamento) ");
		hql.append(" from AinMovimentosAtendUrgencia a1 ");
		hql.append(" where a1.id.seqAtendimentoUrgencia = atu.seq");
		hql.append(" and a1.dthrLancamento < add_days(mv.dthrLancamento,1))");
		hql.append(" and a.id.seqAtendimentoUrgencia = atu.seq");
		hql.append(')');

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrLancamentoMaior", obterData(data, -numeroDiasCenso));
		query.setParameter("dthrLancamentoMenor", obterData(data, +UM_DIA));
		query.setParameter("tipoMovInt", Integer.valueOf("1"));

		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion7(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status,
			final Integer numeroDiasCenso, final Integer codigoLeitoBloqueioLimpeza) throws ApplicationBusinessException {

		final StringBuffer hql = new StringBuffer(2100);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	int.dthrInternacao,  esp.sigla, tpa.codigo, mv.dthrLancamento,");
		hql.append(" 	int.seq, ser.id.matricula, ser.id.vinCodigo, cnv.codigo, pac.dtNascimento, cnv.descricao, tpa.descricao, esp.nomeEspecialidade");
		hql.append(" from AinMovimentosInternacao mv2, AinMovimentosInternacao mv");
		hql.append(" join mv.internacao as int ");
		hql.append(" join int.paciente as pac ");
		hql.append(" join mv.especialidade as esp ");
		hql.append(" left join mv.servidor as ser ");
		hql.append(" left join mv.tipoMovimentoInternacao as tmv ");
		hql.append(" left join mv.unidadeFuncional as unf ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join int.atendimentoUrgencia as atu ");
		hql.append(" left join int.convenioSaude as cnv ");
		hql.append(" left join int.tipoAltaMedica as tpa");
		hql.append(" left join mv.quarto as qrt");
		hql.append(" left join mv.leito as lto");

		
		hql.append(" where mv2.internacao.seq = int.seq ");
		hql.append(" and tmv.codigo <> :tipoMovInt ");
		hql.append(" and mv.dthrLancamento > :dthrLancamentoMaior ");
		hql.append(" and mv.dthrLancamento < :dthrLancamentoMenor ");
		
		if (unfSeq != null) {
			hql.append(" and mv2.unidadeFuncional.seq = :unidadeFunc ");
		}
		
		hql.append(" and mv2.dthrLancamento = ((select max(mov.dthrLancamento) ");
		hql.append(" from AinMovimentosInternacao mov");
		hql.append(" where mov.internacao.seq = mv.internacao.seq ");
		hql.append(" and mov.dthrLancamento < mv.dthrLancamento ");
		hql.append(" and (mov.unidadeFuncional.seq not in ( ");
		hql.append(" select mov2.unidadeFuncional.seq ");
		hql.append(" from AinMovimentosInternacao mov2 ");
		hql.append(" where mov2.dthrLancamento = ( ");
		hql.append(" select min(mov3.dthrLancamento)  ");
		hql.append(" from AinMovimentosInternacao mov3 ");
		hql.append(" where mov3.internacao.seq = int.seq ");
		hql.append(" and mov3.dthrLancamento > mov.dthrLancamento) ");
		hql.append(" and mov2.internacao.seq = int.seq)) ");
		hql.append(" ))) ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("dthrLancamentoMaior", obterData(data, -UM_DIA));
		query.setParameter("dthrLancamentoMenor", obterData(data, +UM_DIA));

		query.setParameter("tipoMovInt", codigoLeitoBloqueioLimpeza);
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisaAltasDia(final Date dataAlta) {

		final StringBuilder hql = new StringBuilder(900);

		hql.append(" select ");
		hql.append(" CSP.id.cnvCodigo, "); // 0
		hql.append(" CNV.descricao, "); // 1
		hql.append(" INT1.dthrAltaMedica, "); // 2
		hql.append(" PAC.prontuario, PAC.nome, "); // 3 e 4
		hql.append(" INT1.tipoAltaMedica.codigo, "); // 5
		hql.append(" INT1.servidorProfessor.id.vinCodigo, INT1.servidorProfessor.id.matricula, "); // 6
																									// e
																									// //7
		hql.append(" INT1.seq, "); // 8
		hql.append(" INT1.dthrInternacao, "); // 9
		hql.append(" ESP.sigla, "); // 10
		hql.append(" LTO.leitoID, QTO.descricao, UF.andar  "); // 11, 12,
																	// 13

		hql.append(" from ");
		hql.append(" FatConvenioSaudePlano CSP ");
		hql.append(" join CSP.convenioSaude CNV ");
		hql.append(" join CSP.internacoes INT1 ");
		hql.append(" join INT1.paciente PAC ");
		hql.append(" join INT1.atendimento ATD ");
		hql.append(" join ATD.unidadeFuncional UF ");
		hql.append(" join INT1.especialidade ESP ");
		hql.append(" join INT1.movimentosInternacao MVI ");
		hql.append(" left join INT1.leito LTO ");
		hql.append(" left join INT1.quarto QTO ");

		hql.append(" where ");
		hql.append(" MVI.criadoEm >= :dataAlta ");
		hql.append(" AND MVI.criadoEm < :dataAltaMaisUM ");
		hql.append(" AND MVI.tipoMovimentoInternacao.codigo + 0 = 21 ");

		hql.append(" order by ");
		hql.append(" CSP.id.cnvCodigo, CNV.descricao || '                ' || INT1.dthrAltaMedica, PAC.nome, INT1.tipoAltaMedica.codigo ");

		final javax.persistence.Query query = this.createQuery(hql.toString());

		query.setParameter("dataAlta", dataAlta);

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataAlta);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		query.setParameter("dataAltaMaisUM", calendar.getTime());

		return query.getResultList();
	}
	
	private DetachedCriteria createPesquisaBaixasDiaCriteria(final Date dataReferencia, final DominioGrupoConvenio grupoConvenio,
			final AghOrigemEventos origemEvento, final Integer idTipoMovimentacao, final AipEtnia etniaPaciente, boolean exibeEtnia) throws ApplicationBusinessException {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class, "ami");
		criteria.createAlias("internacao", "int");
		
		if(exibeEtnia){
			criteria.createAlias("int." + AinInternacao.Fields.PACIENTE.toString(), "pac");
			criteria.createAlias("pac." + AipPacientes.Fields.ETNIA.toString(), "etn");
			
			if(etniaPaciente != null){
				criteria.add(Restrictions.eq("etn." + AipEtnia.Fields.ID.toString(), etniaPaciente.getId()));
			}
		}

		criteria.add(Restrictions.eq("ami." + AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), idTipoMovimentacao));

		if (origemEvento != null || grupoConvenio != null) {
			
			if (origemEvento != null) {
				criteria.add(Restrictions.eq("int." + AinInternacao.Fields.ORIGEM_EVENTO.toString(), origemEvento));
			}
			if (grupoConvenio != null) {
				criteria.createAlias("int.convenioSaude", "ica");
				criteria.add(Restrictions.eq("ica." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), grupoConvenio));
			}
		}

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataReferencia);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		criteria.add(Restrictions.ge(AinMovimentosInternacao.Fields.CRIADO_EM.toString(), dataReferencia));

		calendar.add(Calendar.DATE, 1);
		criteria.add(Restrictions.lt(AinMovimentosInternacao.Fields.CRIADO_EM.toString(), calendar.getTime()));
		
		if(exibeEtnia){
			criteria.addOrder(Order.asc("etn." + AipEtnia.Fields.DESCRICAO.toString()));
		}

		return criteria;
	}
	
	public List<AinMovimentosInternacao> pesquisaBaixasDia(final Date dataReferencia, final DominioGrupoConvenio grupoConvenio,
			final AghOrigemEventos origemEvento, final Integer idTipoMovimentacao, final AipEtnia etniaPaciente, boolean exibeEtnia) throws ApplicationBusinessException {

		final DetachedCriteria criteria = this.createPesquisaBaixasDiaCriteria(dataReferencia, grupoConvenio, origemEvento,
				idTipoMovimentacao, etniaPaciente, exibeEtnia);
		return executeCriteria(criteria);
	}
	
	public Object[] obterOrigem(final Integer seq, final Date data) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), seq));
		
		final DetachedCriteria criteria2 = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria2.setProjection(Property.forName(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()).max());
		criteria2.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), seq));
		criteria2.add(Restrictions.lt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), data));
		
		criteria.add(Subqueries.propertyEq(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), criteria2));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString())) // 0-AghUnidadesFuncionais
				.add(Projections.property(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString())) // 1-Date
				.add(Projections.property(AinMovimentosInternacao.Fields.LEITO.toString()))// 2-AinLeitos
				.add(Projections.property(AinMovimentosInternacao.Fields.QUARTO.toString()))// 3-AinQuartos
				.add(Projections.property(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString()))// 4-AghEspecialidades

		);
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Cria a pesquisa principal.
	 * 
	 * @param Data
	 *            de referência
	 * @param Grupo
	 *            convênio
	 * @param Código
	 *            da unidade funcional
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private DetachedCriteria createPesquisaAltaPorUnidadeCriteria(final Date dataDeReferencia, final DominioGrupoConvenio grupoConvenio,
			final Integer codigoUnidadesFuncionais) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		// AIN_INTERNACOES
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(),
				AinMovimentosInternacao.Fields.INTERNACAO.toString());

		// AIP_PACIENTES
		criteria.createAlias(
				AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR + AinInternacao.Fields.PACIENTE.toString(),
				AinInternacao.Fields.PACIENTE.toString());

		// RAP_SERVIDORES
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), AinInternacao.Fields.SERVIDOR_PROFESSOR.toString());

		// FAT_CONVENIOS_SAUDE
		criteria.createAlias(
				AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR + AinInternacao.Fields.CONVENIO.toString(),
				AinInternacao.Fields.CONVENIO.toString());

		// AIN_TIPOS_CARATER_INTERNACAO
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
				+ AinInternacao.Fields.CARATER_INTERNACAO.toString(), AinInternacao.Fields.CARATER_INTERNACAO.toString());

		// AGH_ATENDIMENTOS
		criteria.createAlias(
				AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR + AinInternacao.Fields.ATENDIMENTO.toString(),
				AinInternacao.Fields.ATENDIMENTO.toString());

		// AGH_ESPECIALIDADES
		criteria.createAlias(
				AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR + AinInternacao.Fields.ESPECIALIDADE.toString(),
				AinInternacao.Fields.ESPECIALIDADE.toString());

		criteria.setProjection(Projections
				.projectionList()

				// 0-DominioGrupoConvenio
				.add(Projections.property(AinInternacao.Fields.CONVENIO.toString() + SEPARADOR
						+ FatConvenioSaude.Fields.GRUPO_CONVENIO.toString()))
				// 1-Integer
				.add(Projections.property(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString()))
				// 2-Date
				.add(Projections.property(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
						+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()))
				// 3-Date
				.add(Projections.property(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()))

				// 4-Integer
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARADOR
						+ AipPacientes.Fields.PRONTUARIO.toString()))
				// 5-String
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARADOR + AipPacientes.Fields.NOME.toString()))
				// 6-DominioSexo
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARADOR + AipPacientes.Fields.SEXO.toString()))
				// 7-AinLeitos
				.add(Projections.property(AinInternacao.Fields.LEITO.toString()))
				// 8-AinQuartos
				.add(Projections.property(AinInternacao.Fields.QUARTO.toString()))
				// 9-Integer
				.add(Projections.property(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
						+ AinInternacao.Fields.SEQ.toString()))
				// 10-AghClinicas
				.add(Projections.property(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString() + SEPARADOR
						+ AghEspecialidades.Fields.CLINICA.toString()))
				// 11-String
				.add(Projections.property(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString() + SEPARADOR
						+ AghEspecialidades.Fields.SIGLA.toString()))
				// 12-String
				.add(Projections.property(AinInternacao.Fields.CARATER_INTERNACAO.toString() + SEPARADOR
						+ AinTiposCaraterInternacao.Fields.DESCRICAO.toString()))
				// 13-Short
				.add(Projections.property(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString() + SEPARADOR
						+ RapServidores.Fields.CODIGO_VINCULO.toString()))
				// 14-Integer
				.add(Projections.property(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString() + SEPARADOR
						+ RapServidores.Fields.MATRICULA.toString()))
				// 15-Date
				.add(Projections.property(AinInternacao.Fields.ATENDIMENTO.toString() + SEPARADOR
						+ AghAtendimentos.Fields.DTHR_INICIO.toString()))
				// 16-AinTiposAltaMedica
				.add(Projections.property(AinMovimentosInternacao.Fields.INTERNACAO.toString() + SEPARADOR
						+ AinInternacao.Fields.TIPO_ALTA_MEDICA.toString()))
				// 17-Integer
				.add(Projections.property(AinInternacao.Fields.CARATER_INTERNACAO.toString() + SEPARADOR
						+ AinTiposCaraterInternacao.Fields.CODIGO_SUS.toString()))
				// 18-AghUnidadesFuncionais
				.add(Projections.property(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString() + SEPARADOR
						+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()))
				// 19-Short
				.add(Projections.property(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString() + SEPARADOR
						+ AghEspecialidades.Fields.SEQ.toString())));

		criteria.add(Restrictions.ge(AinMovimentosInternacao.Fields.CRIADO_EM.toString(), dataDeReferencia));

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataDeReferencia);
		calendar.add(Calendar.DATE, 1);

		criteria.add(Restrictions.lt(AinMovimentosInternacao.Fields.CRIADO_EM.toString(), calendar.getTime()));

		if (grupoConvenio != null && StringUtils.isNotBlank(grupoConvenio.getDescricao())) {
			criteria.add(Restrictions.eq(
					AinInternacao.Fields.CONVENIO.toString() + SEPARADOR + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(),
					grupoConvenio));
		}

		criteria.addOrder(Order.asc(AinInternacao.Fields.PACIENTE.toString() + SEPARADOR + AipPacientes.Fields.NOME.toString()));

		return criteria;
	}
	
	public List<Object[]> pesquisaAltasPorUnidade(final Date dataDeReferencia, final DominioGrupoConvenio grupoConvenio,
			final Integer codigoUnidadesFuncionais) {
		final DetachedCriteria criteria = createPesquisaAltaPorUnidadeCriteria(dataDeReferencia, grupoConvenio, codigoUnidadesFuncionais);
		return executeCriteria(criteria);
	}

	public List<AinMovimentosInternacao> pesquisarAinMovimentosInternacaoPorInternacaoTipoMvto(final Integer seqInternacao,
			final Integer codigoMovimentoInternacaoAlta) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), codigoMovimentoInternacaoAlta));
		return executeCriteria(criteria);
	}
	
	public AinMovimentosInternacao obterAinMovimentosInternacaoSeqAteDtHrLancamento(final Integer internacaoSeq, final Date dataHora) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		final DetachedCriteria subquery = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		subquery.setProjection(Projections.max(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		subquery.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		subquery.add(Restrictions.lt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));

		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		criteria.add(Subqueries.propertyEq(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), subquery));

		return (AinMovimentosInternacao) executeCriteriaUniqueResult(criteria);
	}

	public AinMovimentosInternacao obterAinMovimentosInternacaoSeqPosDtHrLancamento(final Integer internacaoSeq, final Date dataHora) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		final DetachedCriteria subquery = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		subquery.setProjection(Projections.min(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		subquery.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		subquery.add(Restrictions.gt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));

		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		criteria.add(Subqueries.propertyEq(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), subquery));

		return (AinMovimentosInternacao) executeCriteriaUniqueResult(criteria);
	}

	public Date obtemDthrFinal(final Integer internacaoSeq, final Date dataHora) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		final DetachedCriteria subquery = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		subquery.setProjection(Projections.min(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		subquery.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		subquery.add(Restrictions.gt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));

		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		criteria.add(Subqueries.propertyEq(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), subquery));

		criteria.setProjection(Projections.property(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));

		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public Short obtemLocalOrigemSeq(final Integer internacaoSeq, final Date dataHora) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString())));

		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");

		final DetachedCriteria subquery = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		subquery.setProjection(Projections.max(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		subquery.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		subquery.add(Restrictions.lt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dataHora));

		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		criteria.add(Subqueries.propertyEq(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), subquery));

		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	public List<Object[]> pesquisarOrigemPorInternacao(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);

		criteria.setProjection(Projections.projectionList().add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString()))
				.add(Projections.property("QRT." + AinQuartos.Fields.NUMERO.toString()))
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())));

		criteria.createAlias(AinMovimentosInternacao.Fields.LEITO.toString(), "LTO", Criteria.LEFT_JOIN);
		criteria.createAlias(AinMovimentosInternacao.Fields.QUARTO.toString(), "QRT", Criteria.LEFT_JOIN);
		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", Criteria.LEFT_JOIN);
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), "INT");

		criteria.add(Restrictions.eq("INT." + AinInternacao.Fields.SEQ.toString(), intSeq));
		criteria.addOrder(Order.desc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));

		return executeCriteria(criteria);
	}
	
	public Date buscaDthrFinal(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.setProjection(Projections.max(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), "INT");
		criteria.add(Restrictions.eq("INT." + AinInternacao.Fields.SEQ.toString(), intSeq));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public List<AinMovimentosInternacao> pesquisarPorInternacaoSeqOrderDtHrLanc(final Integer internacaoSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), internacaoSeq));
		criteria.addOrder(Order.desc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		return executeCriteria(criteria);
	}

	public AinMovimentosInternacao obterMovimentoInternacaoPorSeqTipo(final Integer seqInternacao, final Integer idTipoMovimentacao)
			throws ApplicationBusinessException {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.TIPO_MVTO_INTERNACAO.toString(), idTipoMovimentacao));
		criteria.addOrder(Order.asc(AinMovimentosInternacao.Fields.SEQ.toString()));
		List<AinMovimentosInternacao> listaMovimentacaoInternacao = executeCriteria(criteria);
		if (listaMovimentacaoInternacao != null && !listaMovimentacaoInternacao.isEmpty()) {
			return listaMovimentacaoInternacao.get(0);
		} 
		return null;
	}

	/**
	 * {@link IInternacaoFacade#buscarMovimentosInternacao(Integer, Date, Date, Date)}
	 */
	public List<AinMovimentosInternacao> buscarMovimentosInternacao(Integer seqInternacao, Date dtInicioProcessamento, Date DtFimProcessamento,
			Date dtHoraLancamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), "int", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unf."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "esp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinMovimentosInternacao.Fields.SERVIDOR.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pesFis", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INT_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.between(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dtInicioProcessamento, DtFimProcessamento));
		if (dtHoraLancamento != null) {
			criteria.add(Restrictions.lt(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dtHoraLancamento));
			criteria.addOrder(Order.desc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		} else {
			criteria.addOrder(Order.asc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		}
		return executeCriteria(criteria);
	}

	public List<AinMovimentosInternacao> buscarMovimentosInternacao(Integer seqInternacao, Date dtHoraLancamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.createAlias(AinMovimentosInternacao.Fields.INTERNACAO.toString(), "int", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unf."+AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias(AinMovimentosInternacao.Fields.ESPECIALIDADE.toString(), "esp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinMovimentosInternacao.Fields.SERVIDOR.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pesFis", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INT_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.le(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString(), dtHoraLancamento));
		criteria.addOrder(Order.desc(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AinMovimentosInternacao> getListaMovimentoInternacao(AinLeitosVO leito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.LTO_LTO_ID.toString(), leito.getLeitoID()));
		
		return executeCriteria(criteria);
	}

	public List<Object[]> obterCensoUnionAll(Short unfSeq, Short unfSeqMae) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AinLeitos.class);

		cri.setProjection(Projections.projectionList()
				.add(Projections.property(AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString()))
				.add(Projections.property("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString()))
				.add(Projections.property(AinLeitos.Fields.LTO_ID.toString()))
				.add(Projections.property("MOV." + AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO)));
		if (unfSeq != null) {
			cri.add(Restrictions.eq(AinMovimentosInternacao.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), unfSeq));
		}
		if (unfSeqMae != null) {
			cri.add(Restrictions.eq("UNF" + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL_MAE.toString(), unfSeqMae));
		}
		
		cri.add(Restrictions.eq(AinLeitos.Fields.SITUACAO.toString(), DominioSituacao.A));
		cri.createAlias(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		cri.createAlias(AinLeitos.Fields.INTERNACAO.toString(), "INT");
		cri.createAlias("INT." + AinInternacao.Fields.MOVIMENTOS_INTERNACAO.toString(), "MOV");
		
		return executeCriteria(cri);
	}
}
