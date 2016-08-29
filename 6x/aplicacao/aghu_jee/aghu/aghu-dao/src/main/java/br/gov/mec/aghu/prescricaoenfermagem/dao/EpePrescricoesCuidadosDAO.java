package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.RapServidores;

public class EpePrescricoesCuidadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpePrescricoesCuidados> {

	private static final long serialVersionUID = -5035359644647658755L;

	public Integer obterValorSequencialId() {
		return this.getNextVal(SequenceID.EPE_PRC_SQ1);
	}	

	public List<EpePrescricoesCuidados> pesquisarPrescricoesCuidadosPorAtendimentoDataInicioFim(Integer atdSeq, Date dthrInicio, Date dthrFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		String aliasPrescricaoEnf = "pen";
		String separador = ".";
		criteria.createAlias(EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM.toString(), aliasPrescricaoEnf);
		criteria.add(Restrictions.eq(aliasPrescricaoEnf + separador + EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.or(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()), Restrictions.lt(EpePrescricoesCuidados.Fields.DTHR_INICIO.toString(),EpePrescricoesCuidados.Fields.DTHR_FIM)));
		criteria.add(Restrictions.lt(EpePrescricoesCuidados.Fields.DTHR_INICIO.toString(), dthrFim));
		criteria.add(Restrictions.or(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()), Restrictions.or(Restrictions.and(Restrictions.gt(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), new Date()), Restrictions.gt(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dthrInicio)), Restrictions.and(Restrictions.lt(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), new Date()), Restrictions.eq(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dthrFim)))));
		return executeCriteria(criteria);
	}
	
	/**
	 * Método que pesquisa a lista de cuidados da prescrição mais recente
	 *
	 * @return
	 */
	public List<EpePrescricoesCuidados> pesquisarCuidadosUltimaPrescricao(EpePrescricaoEnfermagem prescricaoEnfAnterior) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		String aliasPrescricaoEnf = "pen";
		String separador = ".";
		criteria.createAlias(EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM.toString(), aliasPrescricaoEnf);
		criteria.add(Restrictions.eq(aliasPrescricaoEnf + separador + EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(),
				prescricaoEnfAnterior.getId().getAtdSeq()));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PEN_SEQ
				.toString(), prescricaoEnfAnterior.getId().getSeq()));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.DTHR_FIM
				.toString(), prescricaoEnfAnterior.getDthrFim()));
		
		criteria.addOrder(Order.asc(EpePrescricoesCuidados.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Lista os cuidados prescritos através chave da prescrição
	 * de enfermagem(pen_seq + atd_seq) onde a data de fim do cuidado seja igual a data
	 * da prescrição
	 * 
	 * @param penId
	 * @param dthrFim
	 * @return
	 */
	public List<EpePrescricoesCuidados> pesquisarCuidadosPrescricao(
			EpePrescricaoEnfermagemId penId, Date dthrFim, Boolean listarTodas) {
		return this.pesquisarCuidadosPrescricao(penId, dthrFim, listarTodas, false);
	}

	/**
	 * Lista os cuidados prescritos através chave da prescrição
	 * de enfermagem(pen_seq + atd_seq) onde a data de fim do cuidado seja igual a data
	 * da prescrição
	 * 
	 * @param penId
	 * @param dthrFim
	 * @param excluirHierarquicos
	 * @return
	 */
	public List<EpePrescricoesCuidados> pesquisarCuidadosPrescricao(
			EpePrescricaoEnfermagemId penId, Date dthrFim, Boolean listarTodas, Boolean excluirHierarquicos) {

		if (penId == null || dthrFim == null) {
			throw new IllegalArgumentException("Parametro invalido!!!");
		}
		String separador = ".";
		String aliasCuidado = "cui";
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		
		criteria.createAlias(EpePrescricoesCuidados.Fields.CUIDADO.toString(), aliasCuidado, JoinType.INNER_JOIN);
		criteria.createAlias(EpePrescricoesCuidados.Fields.SERVIDOR_VALIDACAO.toString(), "SERV_VAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_VAL."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_VAL_PES", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PEN_ID.toString(), penId));
		
		//Utilizado para exibição dos itens no momento da edição/inclusão
		if (excluirHierarquicos){
			criteria.add(Restrictions.isNull(EpePrescricoesCuidados.Fields.SERVIDOR_MOVIMENTO_VALIDACAO.toString()));
			criteria.add(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_VALIDA_MVTO.toString()));
		}
		
		if(!listarTodas) {
			criteria.add(Restrictions.or(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),
										Restrictions.eq(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dthrFim)));
		}
		
		criteria.addOrder(Order.asc(aliasCuidado + separador + EpeCuidados.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}	
	
	public void desatacharCuidado(EpePrescricoesCuidados prescricaoCuidado) {
		this.desatachar(prescricaoCuidado);
	}
	
	public List<EpePrescricoesCuidados> pesquisarCuidadosPendentes(Integer atdSeq, Integer penSeq, Date dthrWork, Date dthrFim){
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);

		Object[] listaSituacaoNaoPendente = new Object[]{DominioIndPendentePrescricoesCuidados.N,DominioIndPendentePrescricoesCuidados.A};

		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM.toString()+"."+EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PEN_SEQ.toString(), penSeq));
		criteria.add(Restrictions.lt(EpePrescricoesCuidados.Fields.DTHR_INICIO.toString(), dthrFim));
		criteria.add(Restrictions.not(Restrictions.in(EpePrescricoesCuidados.Fields.IND_PENDENTE.toString(), listaSituacaoNaoPendente)));


		criteria.add(Restrictions.or(Restrictions.ge(EpePrescricoesCuidados.Fields.CRIADO_EM.toString(), dthrWork),
		Restrictions.ge(EpePrescricoesCuidados.Fields.ALTERADO_EM.toString(), dthrWork)));

		criteria.add(Restrictions.or(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),
		Restrictions.le(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dthrFim)));


		return executeCriteria(criteria);
	}
	
	public List<EpePrescricoesCuidados> pesquisarCuidadosParaMarcarPendente(Integer atdSeq, Integer penSeq, Date dthrWork, Date dthrInicio, Date dthrFim){
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);

		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM.toString()+"."+EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PEN_SEQ.toString(), penSeq));
		
		criteria.add(Restrictions.or(Restrictions.ge(EpePrescricoesCuidados.Fields.CRIADO_EM.toString(), dthrWork),
		Restrictions.ge(EpePrescricoesCuidados.Fields.ALTERADO_EM.toString(), dthrWork)));

		criteria.add(Restrictions.ge(EpePrescricoesCuidados.Fields.DTHR_INICIO.toString(), dthrFim));
		
		criteria.add(Restrictions.or(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),
		Restrictions.le(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dthrFim)));

		return executeCriteria(criteria);
	}
	
	public List<EpePrescricoesCuidados> pesquisarPrescricoesCuidadosPorPrescricaoEnfermagemMovimentoDataInicioFim(Integer atdSeq, Integer penSeq, Date dthrMovimento, Date dthrInicio, Date dthrFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM.toString()+"."+EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PEN_SEQ.toString(), penSeq));
		
		criteria.add(Restrictions.or(Restrictions.ge(EpePrescricoesCuidados.Fields.CRIADO_EM.toString(),dthrMovimento),Restrictions.ge(EpePrescricoesCuidados.Fields.ALTERADO_EM.toString(),dthrMovimento)));

		criteria.add(Restrictions.in(EpePrescricoesCuidados.Fields.IND_PENDENTE.toString(), new Object[] {
			DominioIndPendentePrescricoesCuidados.P, DominioIndPendentePrescricoesCuidados.E, DominioIndPendentePrescricoesCuidados.A}));
		
		criteria.addOrder(Order.desc(EpePrescricoesCuidados.Fields.DTHR_INICIO.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	public Boolean verificarCuidadoJaExistePrescricao(Short cuiSeq, EpePrescricaoEnfermagemId penId){		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.CUI_SEQ.toString(), cuiSeq));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PEN_SEQ.toString(), penId.getSeq()));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PEN_ATD_SEQ.toString(), penId.getAtdSeq()));
		
		criteria.add(Restrictions.ge(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), new Date()));
		
		return executeCriteriaExists(criteria);
	}
	
	
	public List<EpePrescricoesCuidados> pesquisarCuidadosAtivosAtribuidos(Integer penSeq, 
			Integer penAtdSeq, Date dthrFim) {
		
		Object[] listaSituacaoNaoExibicao = new Object[]{DominioIndPendentePrescricoesCuidados.E,DominioIndPendentePrescricoesCuidados.A,DominioIndPendentePrescricoesCuidados.X};
		String aliasCuid = "cui";
		String aliasPrescCuid = "pcu";
		String aliasPrescEnf = "pen";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class, aliasPrescCuid);
		criteria.createAlias(EpePrescricoesCuidados.Fields.CUIDADO.toString(), aliasCuid);
		criteria.createAlias(aliasPrescCuid + separador + EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM.toString(), aliasPrescEnf);
		
		criteria.add(Restrictions.not(Restrictions.in(aliasPrescCuid + separador + EpePrescricoesCuidados.Fields.IND_PENDENTE.toString(), listaSituacaoNaoExibicao)));
		
		criteria.add(Restrictions.eq(aliasPrescEnf + separador + EpePrescricaoEnfermagem.Fields.ATD_SEQ.toString(), penAtdSeq));
		criteria.add(Restrictions.eq(aliasPrescEnf + separador + EpePrescricaoEnfermagem.Fields.SEQ.toString(), penSeq));
		
		criteria.add(Restrictions.eq(aliasCuid + separador 
				+ EpeCuidados.Fields.IND_SITUACAO.toString() , DominioSituacao.A));

		criteria.addOrder(Order.asc(EpeCuidados.Fields.DESCRICAO.toString()));
		
		criteria.add(Restrictions.or(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),
                Restrictions.eq(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dthrFim)));

		return executeCriteria(criteria);
	}
	
	public Long obterCountPrescricaoCuidadoAutoRelacionamentoPorAtdSeqESeq(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.isNotNull(EpePrescricoesCuidados.Fields.PRESCRICAO_CUIDADO.toString()));
		return executeCriteriaCount(criteria);
	}
	
	/**
	 *  Método que pesquisa os cuidados de uma prescrição médica filtrando pelo atendimento, data de início e de fim da prescrição.
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<EpePrescricoesCuidados> obterPrescricoesCuidadoParaSumarioCuidado(Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao){
		List<EpePrescricoesCuidados> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.ATD_SEQ.toString(), pmeAtdSeq));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(EpePrescricoesCuidados.Fields.IND_PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(EpePrescricoesCuidados.Fields.DTHR_INICIO.toString(), dataFimPrescricao));
		
		criteria.add(Restrictions.or(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),
				Restrictions.gt(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dataInicioPrescricao)));

		criteria.add(Restrictions.isNotNull(EpePrescricoesCuidados.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(EpePrescricoesCuidados.Fields.CUIDADO.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;
	}
	
	public List<EpePrescricoesCuidados> obterPrescricoesCuidadoPai(Integer seq,
			Integer seqAtendimento, Date dataHoraInicio, Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PRESCRICAO_CUIDADO_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.PRESCRICAO_CUIDADO_ATD_SEQ.toString(), seqAtendimento));

		List<DominioIndPendentePrescricoesCuidados> restricaoIn = new ArrayList<DominioIndPendentePrescricoesCuidados>();
		restricaoIn.add(DominioIndPendentePrescricoesCuidados.N);
		restricaoIn.add(DominioIndPendentePrescricoesCuidados.A);
		restricaoIn.add(DominioIndPendentePrescricoesCuidados.E);

		criteria.add(Restrictions.in(EpePrescricoesCuidados.Fields.IND_PENDENTE.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(EpePrescricoesCuidados.Fields.DTHR_INICIO.toString(), dataHoraFim));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoCuidado.Fields.DTHR_FIM.toString()),
				Restrictions.gt(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), dataHoraInicio)));

		criteria.add(Restrictions.isNotNull(EpePrescricoesCuidados.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(EpePrescricoesCuidados.Fields.PRESCRICAO_CUIDADO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Obter cuidado da prescrição pelo seu ID.
	 * 
	 * @param pmeAtdSeq
	 * @param seq
	 * @return
	 */
	public List<EpePrescricoesCuidados> listPrescricaoCuidadoPorId(Integer atdSeq,Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.SEQ.toString(), seq));
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param seq
	 * @return
	 */
	public List<EpePrescricoesCuidados> obterEpePrescricoesCuidadosPorEpeCuidadoSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		criteria.add(Restrictions.eq(EpePrescricoesCuidados.Fields.CUIDADO.toString() + "." +  EpeCuidados.Fields.SEQ.toString(), seq));
		return executeCriteria(criteria);
	}
}