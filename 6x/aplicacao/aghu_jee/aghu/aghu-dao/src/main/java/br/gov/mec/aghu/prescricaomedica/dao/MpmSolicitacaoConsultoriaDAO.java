package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoriaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultoriasInternacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemRelatorioEstatisticaProdutividadeConsultorVO;
import br.gov.mec.aghu.prescricaomedica.vo.SolicitacaoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.VisualizaDadosSolicitacaoConsultoriaVO;

/**
 * @author rcorvalao
 * 
 */

public class MpmSolicitacaoConsultoriaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmSolicitacaoConsultoria> {

	private static final long serialVersionUID = -4276699663909137644L;

	public List<MpmSolicitacaoConsultoria> buscaSolicitacaoConsultoriaPorPrescricaoMedica(
			MpmPrescricaoMedicaId id, Boolean listarTodas) {
		List<MpmSolicitacaoConsultoria> list;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmSolicitacaoConsultoria.class);
		criteria.add(Restrictions.eq(
				MpmSolicitacaoConsultoria.Fields.PME_ATD_SEQ.toString(), id
						.getAtdSeq()));
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PME_SEQ
				.toString(), id.getSeq()));
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM
				.toString(), DominioOrigemSolicitacaoConsultoria.M));
		if(!listarTodas) {
			criteria.add(Restrictions
					.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM
							.toString()));
		}
		list = super.executeCriteria(criteria);

		return list;
	}
	
	public List<MpmSolicitacaoConsultoria> buscaSolicitacaoConsultoria(
			MpmPrescricaoMedicaId id, Boolean listarTodas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), id.getAtdSeq()));
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.M));
		if(!listarTodas) {
			criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.SEQ.toString(), id.getSeq()));
			criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
		}
		return executeCriteria(criteria);
	}
	
	public MpmSolicitacaoConsultoria obterMpmSolicitacaoConsultoriaPorIdComPaciente(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class, "SCN");
		criteria.createAlias("SCN." + MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("SCN." + MpmSolicitacaoConsultoria.Fields.PRESCRICAO_MEDICA.toString(), "PRM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PRM." + MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);		
		criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.SEQ.toString(), seq));
		return (MpmSolicitacaoConsultoria) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtém solicitacao consultoria pelo seu SEQ.
	 * 
	 * bsoliveira 29/10/2010
	 * 
	 * @param {Integer} seq
	 * 
	 * @return MpmSolicitacaoConsultoria
	 */
	public MpmSolicitacaoConsultoria obterSolicitacaoConsultoriaPeloSeq(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmSolicitacaoConsultoria.class);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.SEQ.toString(),
				seq));
		MpmSolicitacaoConsultoria retorno = (MpmSolicitacaoConsultoria) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;
	}
	
	/**
	 * 
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmSolicitacaoConsultoria> buscaSolicitacaoConsultoriaSumarioAlta(Integer atdSeq) throws ApplicationBusinessException {
		
		List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria = new ArrayList<MpmSolicitacaoConsultoria>();
		listaSolicitacaoConsultoria = this.executeCriteria(this.obterCriteriaConsultoria(atdSeq));
		return listaSolicitacaoConsultoria;
	}
	
	private DetachedCriteria obterCriteriaConsultoria(Integer atdSeq) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNotNull(MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
		criteria.add(Restrictions.or(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString()), Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.M)));
		
		return criteria;
	}
	
	/**
	 * Pesquisa as consultorias para processar o cancelamento das mesmas.
	 * @param atdSeq
	 * @param pmeSeq
	 * @param dthrMovimento
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriasParaCancelar(
			Integer atdSeq, Integer pmeSeq, Date dthrMovimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PME_SEQ.toString(), pmeSeq));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.B);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.Y);
		restricaoIn.add(DominioIndPendenteItemPrescricao.R);
		
		criteria.add(Restrictions.in(MpmSolicitacaoConsultoria.Fields.PENDENCIA
				.toString(), restricaoIn));
		
		Criterion criterionCriadoEmMaiorIgual = Restrictions.ge(MpmSolicitacaoConsultoria.Fields.CRIADO_EM.toString(), dthrMovimento);
		Criterion criterionAlteradoEmMaiorIgual = Restrictions.ge(MpmSolicitacaoConsultoria.Fields.ALTERADO_EM.toString(), dthrMovimento);
		
		criteria.add(Restrictions.or(criterionCriadoEmMaiorIgual, criterionAlteradoEmMaiorIgual));
		
		List<MpmSolicitacaoConsultoria> retorno = this.executeCriteria(criteria);
		
		return retorno;
	}
	
	/**
	 * Método que pesquisa todas as consultorias de uma prescrição médica
	 * @param id
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> pesquisarTodasConsultoriasPrescricaoMedica(
			MpmPrescricaoMedicaId id) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmSolicitacaoConsultoria.class);
		criteria.add(Restrictions.eq(
				MpmSolicitacaoConsultoria.Fields.PRESCRICAO_MEDICA_ID.toString(),
				id));

		List<MpmSolicitacaoConsultoria> list = super.executeCriteria(criteria);

		return list;
	}
	
	public List<MpmSolicitacaoConsultoria> obterListaConsultoriasRespondidas(Integer atdSeq){
	    DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);  
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(),atdSeq));	    
	    criteria.add(Restrictions.isNotNull(MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(),	DominioIndPendenteItemPrescricao.N));	    
	    criteria.add(Restrictions.or(
		    	Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString()),
		        Restrictions.eq( MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.M )
		        ));
	    return executeCriteria(criteria);
	}
	
	public List<MpmSolicitacaoConsultoria> obterListaConsultorias(Integer atdSeq){
	    DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);  
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(),atdSeq));	 
	    criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(),	DominioIndPendenteItemPrescricao.N));	    
	    criteria.add(Restrictions.or(
		    	Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString()),
		        Restrictions.eq( MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.M )
		        ));
	    return executeCriteria(criteria);
	}
	
	/**
	 * Método que pesquisa solicitacoes de consultoria filtrando pelo atendimento, data de início e de fim da prescrição.
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> obterSolicitacoesConsultoriaParaSumarioPrescricao(Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao){
		List<MpmSolicitacaoConsultoria> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), pmeAtdSeq));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(), restricaoIn));

		criteria.add(Restrictions.or(
				Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString()),
				Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.M)));
				
		criteria.add(Restrictions.ge(MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString(), dataInicioPrescricao));
		criteria.add(Restrictions.lt(MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString(), dataFimPrescricao));

		criteria.add(Restrictions.isNotNull(MpmSolicitacaoConsultoria.Fields.SERVIDOR_VALIDACAO.toString()));
		
		criteria.addOrder(Order.asc(MpmSolicitacaoConsultoria.Fields.SOLICITACAO_CONSULTORIA_ORIGINAL.toString()));

		lista = executeCriteria(criteria);
		
		return lista;
	}
	
	/**
	 * Busca as informacoes que estao atualmente salva na base de dados
	 * para o Objeto informado no parametro.<br>
	 * 
	 * <code>MpmSolicitacaoConsultoria</code>
	 * 
	 * @param consultoria
	 * @return
	 */
	public List<Object[]> buscarValoresConsultoriaAnterior(MpmSolicitacaoConsultoria consultoria){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpmSolicitacaoConsultoria.Fields.ESP_SEQ.toString()),"espSeq")
				.add(Projections.property(MpmSolicitacaoConsultoria.Fields.TIPO.toString()), "tipo")
				.add(Projections.property(MpmSolicitacaoConsultoria.Fields.IND_URGENCIA.toString()), "urgente")
				.add(Projections.property(MpmSolicitacaoConsultoria.Fields.MOTIVO.toString()), "motivo")
				);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ID.toString(), consultoria.getId()));
		
		List<Object[]> listaAtributos = executeCriteria(criteria);
		
		return listaAtributos;
	}

	public List<MpmSolicitacaoConsultoria> obterSolicitacoesConsultoriaPai(
			Integer seq, Integer seqAtendimento, Date dataHoraInicio,
			Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.SOLICITACAO_CONSULTORIA_ORIGINAL_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.SOLICITACAO_CONSULTORIA_ORIGINAL_ATD_SEQ.toString(), seqAtendimento));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(), restricaoIn));

		criteria.add(Restrictions.or(
				Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString()),
				Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.M)));
				
		criteria.add(Restrictions.ge(MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString(), dataHoraInicio));
		criteria.add(Restrictions.lt(MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString(), dataHoraFim));

		criteria.add(Restrictions.isNotNull(MpmSolicitacaoConsultoria.Fields.SERVIDOR_VALIDACAO.toString()));
		
		criteria.addOrder(Order.asc(MpmSolicitacaoConsultoria.Fields.SOLICITACAO_CONSULTORIA_ORIGINAL.toString()));

		return executeCriteria(criteria);
	}

	public List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaPorInternacaoOutrasEspecialidades(
			Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		
		criteria.createAlias(MpmSolicitacaoConsultoria.Fields.ATENDIMENTO.toString(), "aliasAtendimento");
		criteria.createAlias("aliasAtendimento"+"."+AghAtendimentos.Fields.INTERNACAO.toString(), "aliasInternacao");
		criteria.createAlias(MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), "aliasEspecialidade");
		
		criteria.add(Restrictions.eq("aliasInternacao"+"."+AghAtendimentos.Fields.SEQ.toString(), intSeq));
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(),DominioIndPendenteItemPrescricao.N));
		criteria.add(Restrictions.isNotNull(MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
		
		criteria.addOrder(Order.asc("aliasInternacao"+"."+AinInternacao.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}

	/**
	 * 
	 * Cria uma criteria que pode ser reaproveitada para pesquisar solicitacoes
	 * de consultoria ativas para um mesmo atendimento.
	 * 
	 * @param idAtendimento
	 * @return
	 */
	private DetachedCriteria obterCriteriaSolicitacaoconsultoriaAtivas(Integer idAtendimento) {
		// Essa query, além de ser mais flexível por usar o detached criteria
		// permite utilizar o count quando necessário
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), idAtendimento));
		return criteria;
	}

	/**
	 * Cria uma criteria que pode ser reaproveitada para pesquisar solicitacoes
	 * de consultoria ativas para uma mesma prescricao em um mesmo atendimento.
	 * 
	 * @param idAtendimento
	 * @param prescricao
	 * @return
	 */
	private DetachedCriteria obterCriteriaSolicitacaoConsultoriaAtivasPorPrescricao(Integer idAtendimento,
			MpmPrescricaoMedica prescricao) {
		DetachedCriteria criteria = obterCriteriaSolicitacaoconsultoriaAtivas(idAtendimento);

		// O Between usa maior ou igual e menor ou igual, como nao eh exatamente
		// o caso fazer explicitamente
		// criteria.add(Restrictions.between("dthrSolicitada",
		// prescricao.getDthrInicio(), prescricao.getDthrFim()));
		criteria.add(Restrictions.ge(MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString(), prescricao.getDthrInicio()));
		criteria.add(Restrictions.lt(MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString(), prescricao.getDthrFim()));

		return criteria;
	}

	/**
	 * Limita a criteria de solicitacoes para trazer apenas aquelas da mesma
	 * especialidade.
	 * 
	 * @param idEspecialidade
	 *            ATENÇÃO: eh bom manter como inteiro para a camada de serviço
	 *            nao ficar tao presa com questoes de modelagem de banco de
	 *            dados.
	 * @param idAtendimento
	 * @param prescricao
	 * @return
	 */
	private DetachedCriteria obterCriteriaSolicitacoesAtivasPorEspecialidadePorPrescricao(Short idEspecialidade,
			Integer idAtendimento, MpmPrescricaoMedica prescricao) {
		DetachedCriteria criteria = obterCriteriaSolicitacaoConsultoriaAtivasPorPrescricao(idAtendimento, prescricao);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ESP_SEQ.toString(), idEspecialidade));
		return criteria;
	}

	/**
	 * Método que verifica se já existe uma solicitação de consultoria para esta
	 * especialidade em uma mesma prescrição de um mesmo atendimento. TODO
	 * alterar nome de metodo para pesquisarSolicitacaoConsultoria.
	 * 
	 * @param especialidade
	 * @param idAtendimento
	 * @return
	 */
	public MpmSolicitacaoConsultoria verificarSolicitacaoConsultoriaAtivaPorEspecialidade(Short idEspecialidade,
			Integer idAtendimento, Integer idPrescricao, MpmPrescricaoMedica prescricao) {

		DetachedCriteria criteria = obterCriteriaSolicitacoesAtivasPorEspecialidadePorPrescricao(idEspecialidade, idAtendimento,
				prescricao);
		// Faz apenas um count, nao precisa fazer o fetch dos objetos nesse
		// momento.
		// criteria.setProjection(Projections.rowCount());
		List<MpmSolicitacaoConsultoria> result = executeCriteria(criteria, 0, 1, null, true);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	/**
	 * Método utilizado para pesquisar as consultorias de uma prescrição médica
	 * 
	 * @param prescricaoMedica
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriasPorPrescricao(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		criteria.setFetchMode(MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PME_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PME_SEQ.toString(), seq));
		criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
		criteria.addOrder(Order.desc(MpmSolicitacaoConsultoria.Fields.SEQ.toString()));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param idEspecialidade
	 * @param idAtendimento
	 * @param prescricao
	 * @return
	 */
	private DetachedCriteria obterCriteriaSolicitacoesAtivasNaoPendentesPorEspecialidade(Short idEspecialidade, Integer idAtendimento,
			MpmPrescricaoMedica prescricao) {

		DetachedCriteria criteria = obterCriteriaSolicitacoesAtivasPorEspecialidadePorPrescricao(idEspecialidade, idAtendimento,
				prescricao);

		criteria.add(Restrictions.ne(MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(), DominioIndPendenteItemPrescricao.P));

		return criteria;
	}

	public Long obterSolicitacoesAtivasNaoPendentesPorEspecialidadeCount(MpmSolicitacaoConsultoria solicitacaoConsultoria,
			MpmPrescricaoMedica precricao, Integer idAtendimento) {

		DetachedCriteria criteria = obterCriteriaSolicitacoesAtivasNaoPendentesPorEspecialidade(solicitacaoConsultoria
				.getEspecialidade().getSeq(), idAtendimento, precricao);

		// Faz apenas um count, nao precisa fazer o fetch dos objetos nesse
		// momento.
		return executeCriteriaCount(criteria);
	}

	public List<MpmSolicitacaoConsultoria> pesquisaSolicitacoesConsultoria(Integer atdSeq) {
		DetachedCriteria criteria = obterCriteriaSolicitacaoconsultoriaAtivas(atdSeq);
		criteria.addOrder(Order.asc(MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisaSolicitacoesConsultoriaCount(Integer atdSeq) {
		DetachedCriteria criteria = obterCriteriaSolicitacaoconsultoriaAtivas(atdSeq);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Método responsável pela geração do Sequencial para o Id de uma
	 * solicitação de consultoria.
	 * 
	 * @param solicitacaoConsultoria
	 */
	public void setValorSequencialId(MpmSolicitacaoConsultoria solicitacaoConsultoria) {
		solicitacaoConsultoria.getId().setSeq(getNextVal(SequenceID.MPM_SCN_SQ1));
	}

	public List<MpmSolicitacaoConsultoria> obterConsultoriaAtiva(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Método de pesquisa de solicitações de consultoria de enfermagem com
	 * resposta não vista.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriaEnfermagemResposta(Integer atdSeq) {
	    return executeCriteria(montarCriteriaPesquisarConsultoriaEnfermagemResposta(atdSeq));
	}
	
	public Boolean verificarExisteConsultoriaEnfermagemResposta(Integer atdSeq) {
	    return executeCriteriaCount(montarCriteriaPesquisarConsultoriaEnfermagemResposta(atdSeq)) > 0;
	}
	
	private DetachedCriteria montarCriteriaPesquisarConsultoriaEnfermagemResposta(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(),atdSeq));
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.E));
	    criteria.add(Restrictions.isNotNull(MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
	    criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_CONHECIMENTO_RESPOSTA.toString()));
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.SITUACAO.toString(), DominioSituacao.A));
	    
	    return criteria;
	}	
	
	/**
	 * Método de pesquisa de solicitações de consultoria de enfermagem.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriaEnfermagemSolicitada(Integer atdSeq) { 
	    return executeCriteria(montarCriteriaPesquisarConsultoriaEnfermagemSolicitada(atdSeq));
	}
	
	public Boolean verificarExisteConsultoriaEnfermagemSolicitada(Integer atdSeq) { 
	    return executeCriteriaCount(montarCriteriaPesquisarConsultoriaEnfermagemSolicitada(atdSeq)) > 0;
	}
	
	private DetachedCriteria montarCriteriaPesquisarConsultoriaEnfermagemSolicitada(Integer atdSeq){
	    
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(),atdSeq));
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.E));
	    criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
	    criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_CONHECIMENTO_RESPOSTA.toString()));
	    criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.SITUACAO.toString(), DominioSituacao.A));
	    
	    return criteria;
	}
	
	/**
	 * Método de pesquisa de solicitações de consultoria ativas e de origem da enfermagem.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaAtivaEnfermagem(Integer atdSeq) {	    
	    return executeCriteria(montarCriteriaSolicitacaoConsultoriaAtivaEnfermagem(atdSeq));
	}
	
	public Boolean verificarExisteSolicitacaoConsultoriaAtivaEnfermagem(Integer atdSeq) {	    
	    return executeCriteriaCount(montarCriteriaSolicitacaoConsultoriaAtivaEnfermagem(atdSeq)) > 0;
	}
	
	private DetachedCriteria montarCriteriaSolicitacaoConsultoriaAtivaEnfermagem(Integer atdSeq) {
	    
		DetachedCriteria criteria = obterCriteriaSolicitacaoconsultoriaAtivas(atdSeq);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.E));
	    
	    return criteria;
	}
	
	/**
	 * #1000 Prescrição: Visualizar Dados da Solicitação de Consultoria
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public VisualizaDadosSolicitacaoConsultoriaVO obterDadosSolicitacaoConsultoria(final Integer atdSeq, final Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);

		/*
		 * Projections
		 */
		ProjectionList list = Projections.projectionList();
		
		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.ID_ATD_SEQ.toString());
		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.SEQ.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.ID_SCN_SEQ.toString());
		
		list.add(Projections.property("ATD2." + AghAtendimentos.Fields.PRONTUARIO.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.PRONTUARIO.toString());
		list.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.NOME_PACIENTE.toString());
		list.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.NOME_PACIENTE.toString());
		list.add(Projections.property("PAC." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.DATA_NASCIMENTO.toString());
		list.add(Projections.property("PAC." + AipPacientes.Fields.SEXO.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.SEXO.toString());
		list.add(Projections.property("CLC." + AghClinicas.Fields.DESCRICAO.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.CLINICA.toString());
		list.add(Projections.property("ESP1." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.ESPECIALIDADE_ATENDIMENTO.toString());
		list.add(Projections.property("ESP2." + AghEspecialidades.Fields.SIGLA.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.SIGLA_ESPECIALIDADE.toString());
		list.add(Projections.property("ESP2." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.ESPECIALIDADE.toString());
		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.DATA_SOLICITACAO.toString());
		list.add(Projections.property("PMD." + MpmPrescricaoMedica.Fields.SEQ.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.PRESCRICAO.toString());
		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.MOTIVO.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.DESCRICAO.toString());
		
		list.add(Projections.property("INT." + AinInternacao.Fields.SEQ.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.INTERNACAO.toString()); // INT_SEQ para MPMC_VER_DT_INI_ATD
		list.add(Projections.property("ATU." + AinAtendimentosUrgencia.Fields.SEQ.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.ATENDIMENTO_URGENCIA.toString()); // ATU_SEQ para MPMC_VER_DT_INI_ATD

		list.add(Projections.property("ATD2_SER." + RapServidores.Fields.MATRICULA.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.VCS_MATRICULA.toString()); // VCS.VIN_CODIGO
		list.add(Projections.property("ATD2_SER." + RapServidores.Fields.VIN_CODIGO.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.VCS_VINCULO.toString()); // VCS.MATRICULA

		list.add(Projections.property("SER_VALID." + RapServidores.Fields.MATRICULA.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.VCS2_MATRICULA.toString()); // SCN.SER_MATRICULA_VALIDA
		list.add(Projections.property("SER_VALID." + RapServidores.Fields.VIN_CODIGO.toString()), VisualizaDadosSolicitacaoConsultoriaVO.Fields.VCS2_VINCULO.toString()); // SCN.SER_VIN_CODIGO_VALIDA
		
		criteria.setProjection(list);

		/*
		 * Alias
		 */
		criteria.createAlias(MpmSolicitacaoConsultoria.Fields.SERVIDOR_VALIDACAO.toString(), "SER_VALID", JoinType.LEFT_OUTER_JOIN); // VCS1
		criteria.createAlias(MpmSolicitacaoConsultoria.Fields.PRESCRICAO_MEDICA.toString(), "PMD");
		criteria.createAlias("PMD." + MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "ATD2");
		criteria.createAlias("ATD2." + AghAtendimentos.Fields.SERVIDOR.toString(), "ATD2_SER", JoinType.LEFT_OUTER_JOIN); // VCS2
		criteria.createAlias("ATD2." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP1");
		criteria.createAlias(MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), "ESP2");
		criteria.createAlias("ATD2." + AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN); // INT_SEQ
		criteria.createAlias("ATD2." + AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString(), "ATU", JoinType.LEFT_OUTER_JOIN); // ATU_SEQ
		criteria.createAlias("ESP1." + AghEspecialidades.Fields.CLINICA.toString(), "CLC", JoinType.RIGHT_OUTER_JOIN); // (+) =
		criteria.createAlias("ATD2." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.RIGHT_OUTER_JOIN); // (+) =

		/*
		 * Critérios view VCS2
		 */
		DetachedCriteria subQueryVcs2 = DetachedCriteria.forClass(VRapServidorConselho.class, "VCS2");
		subQueryVcs2.setProjection(Projections.property("VCS2." + VRapServidorConselho.Fields.ID.toString()));
		subQueryVcs2.add(Property.forName("VCS2." + VRapServidorConselho.Fields.VIN_CODIGO.toString()).eqProperty("SER_VALID." + RapServidores.Fields.VIN_CODIGO.toString()));
		subQueryVcs2.add(Property.forName("VCS2." + VRapServidorConselho.Fields.MATRICULA.toString()).eqProperty("SER_VALID." + RapServidores.Fields.MATRICULA.toString()));
		criteria.add(Subqueries.exists(subQueryVcs2));

		// Critério princiapl
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ID.toString(), new MpmSolicitacaoConsultoriaId(atdSeq, seq)));

		criteria.setResultTransformer(Transformers.aliasToBean(VisualizaDadosSolicitacaoConsultoriaVO.class));
		return (VisualizaDadosSolicitacaoConsultoriaVO) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * #3855 Prescrição: Emitir relatório de estatísica da produtividade do
	 * consultor
	 * 
	 * @param espSeq
	 * @param dtInicio
	 * @param dtFim
	 * @return
	 */
	public List<ItemRelatorioEstatisticaProdutividadeConsultorVO> pesquisarRelatorioEstatisticaProdutividadeConsultor(final Short espSeq, final Date dtInicio, final Date dtFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);

		/*
		 * Projections
		 */
		ProjectionList list = Projections.projectionList();

		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.ID_ATD_SEQ.toString());
		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.SEQ.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.ID_SCN_SEQ.toString());

		list.add(Projections.property("ESP1." + AghEspecialidades.Fields.SIGLA.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.ESP_SOL.toString());

		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.TIPO.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.TIPO.toString());
		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.IND_URGENCIA.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.IND_URGENCIA.toString());

		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.CRIADO_EM.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.DATA_SOLICITACAO.toString());
		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.DTHR_PRIM_CONSULTA.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.DATA_CONHECIMENTO.toString());

		list.add(Projections.property(MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.DTHR_RESPOSTA.toString());
		list.add(Projections.property("ATD." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString()), ItemRelatorioEstatisticaProdutividadeConsultorVO.Fields.IND_PAC_ATENDIMENTO.toString());

		criteria.setProjection(list);

		/*
		 * Alias
		 */
		criteria.createAlias(MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), "ESP"); // <P_ESP_SEQ>
		criteria.createAlias(MpmSolicitacaoConsultoria.Fields.PRESCRICAO_MEDICA.toString(), "PME");
		criteria.createAlias("PME." + MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP1");

		// Critério principal
		criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(ItemPrescricaoMedica.Fields.IND_PENDENTE.toString(), DominioIndPendenteItemPrescricao.N));
		criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));

		/**
		 * Formata datas para pesquisa por intervalo
		 */
		final Date dtInicioFormatada = DateUtil.truncaData(dtInicio);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dtFim);
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		final Date dtFimFormatada = calendar.getTime();

		// Critério do intervalo de datas
		criteria.add(Restrictions.between(MpmSolicitacaoConsultoria.Fields.CRIADO_EM.toString(), dtInicioFormatada, dtFimFormatada));

		/*
		 * Ordenação
		 */
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));
		criteria.addOrder(Order.asc(MpmSolicitacaoConsultoria.Fields.CRIADO_EM.toString()));
		criteria.addOrder(Order.asc(MpmSolicitacaoConsultoria.Fields.DTHR_PRIM_CONSULTA.toString()));
		criteria.addOrder(Order.asc(MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemRelatorioEstatisticaProdutividadeConsultorVO.class));
		return executeCriteria(criteria);
	}
	
	
	public List<ConsultoriasInternacaoVO> listarConsultoriasInternacaoPorAtendimento(Short espSeq, Short unfSeq, DominioTipoSolicitacaoConsultoria tipo,
			DominioSimNao urgencia, DominioSituacaoConsultoria situacao) {
		
		DetachedCriteria criteria = obterCriteriaPrincipalConsultoriasInternacaoPorAtendimentos();
		
		// DTHR_FIM = DTHR_DESATIVACAO
		criteria.add(Restrictions.isNull("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
		criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(), DominioIndPendenteItemPrescricao.N));
		criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.ESP_SEQ.toString(), espSeq));
		if (urgencia != null) {
			criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.IND_URGENCIA.toString(), urgencia));
		}
		if (tipo != null) {
			criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.TIPO.toString(), tipo));
		}
		DetachedCriteria subCriteria = obterSubCriteriaConsultoriasInternacaoPorAtendimento(unfSeq);
		criteria.add(Subqueries.propertyIn("SCN." + MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), subCriteria));
		
		if (situacao != null) {
			switch (situacao) {
			case P:
				criteria.add(Restrictions.isNull("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
				criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.SITUACAO.toString(), DominioSituacao.A));
				break;
			case CO:
				criteria.add(Restrictions.isNotNull("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
				criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.IND_CONCLUIDA.toString(), DominioIndConcluidaSolicitacaoConsultoria.S));
				break;
			case C:
				criteria.add(Restrictions.isNotNull("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
				break;
			case A:
				criteria.add(Restrictions.isNotNull("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
				criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.IND_CONCLUIDA.toString(), DominioIndConcluidaSolicitacaoConsultoria.A));
				break;
			case PA:
				Criterion crit1 = Restrictions.isNull("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString());
				Criterion crit2 = Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.SITUACAO.toString(), DominioSituacao.A);
				Criterion crit3 = Restrictions.isNotNull("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString());
				Criterion crit4 = Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.IND_CONCLUIDA.toString(), DominioIndConcluidaSolicitacaoConsultoria.A);
				
				criteria.add(Restrictions.or(
						Restrictions.and(crit1, crit2), 
						Restrictions.and(crit3, crit4)));
				break;

			default:
				break;
			}
		} else {
			criteria.add(Restrictions.in("SCN." + MpmSolicitacaoConsultoria.Fields.SITUACAO.toString(),
					new DominioSituacao[]{DominioSituacao.A, DominioSituacao.I}));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultoriasInternacaoVO.class));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaPrincipalConsultoriasInternacaoPorAtendimentos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class, "SCN");
		criteria.createAlias("SCN." + MpmSolicitacaoConsultoria.Fields.PRESCRICAO_MEDICA.toString(), "PRC");
		criteria.createAlias("PRC." + MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString())
						, ConsultoriasInternacaoVO.Fields.ATD_SEQ.toString())
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.SEQ.toString())
						, ConsultoriasInternacaoVO.Fields.SEQ.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString())
						, ConsultoriasInternacaoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString())
						, ConsultoriasInternacaoVO.Fields.NOME.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.LTO_LTO_ID.toString())
						, ConsultoriasInternacaoVO.Fields.LEITO_ID.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.QRT_NUMERO.toString())
						, ConsultoriasInternacaoVO.Fields.QRTO_NUMERO.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.UNF_SEQ.toString())
						, ConsultoriasInternacaoVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.IND_CONCLUIDA.toString())
						, ConsultoriasInternacaoVO.Fields.IND_CONCLUIDA.toString())
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.TIPO.toString())
						, ConsultoriasInternacaoVO.Fields.TIPO.toString())
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.IND_URGENCIA.toString())
						, ConsultoriasInternacaoVO.Fields.IND_URGENCIA.toString())
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString())
						, ConsultoriasInternacaoVO.Fields.DTHR_SOLICITADA.toString())
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString())
						, ConsultoriasInternacaoVO.Fields.DTHR_RESPOSTA.toString())
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_PRIM_CONSULTA.toString())
						, ConsultoriasInternacaoVO.Fields.DTHR_PRIM_CONSULTA.toString())
				.add(Projections.property("SCN." + MpmSolicitacaoConsultoria.Fields.CRIADO_EM.toString())
						, ConsultoriasInternacaoVO.Fields.CRIADO_EM.toString()));
		
		return criteria;
	}
	
	private DetachedCriteria obterSubCriteriaConsultoriasInternacaoPorAtendimento(Short unfSeq) {
		DetachedCriteria subCriteria =  DetachedCriteria.forClass(AghAtendimentos.class, "ATDE");
		subCriteria.setProjection(Projections.property("ATDE." + AghAtendimentos.Fields.SEQ.toString()));
		
		subCriteria.add(Restrictions.eqProperty("ATDE." + AghAtendimentos.Fields.SEQ.toString(),
				"SCN." + MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString()));
		subCriteria.add(Restrictions.eq("ATDE." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		if (unfSeq != null) {
			subCriteria.add(Restrictions.eq("ATDE." + AghAtendimentos.Fields.UNF_SEQ.toString(), unfSeq));
		}
		return subCriteria;
	}
	
	public List<MpmSolicitacaoConsultoria> obterSolicitacoesConsultoriaPorEspSeqTipoUrgencia(Short espSeq,
			DominioTipoSolicitacaoConsultoria tipo, DominioSimNao urgencia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class);
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.ESP_SEQ.toString(), espSeq));
		if (tipo != null) {
			criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.TIPO.toString(), tipo));
		}
		if (urgencia != null) {
			criteria.add(Restrictions.eq(MpmSolicitacaoConsultoria.Fields.IND_URGENCIA.toString(), urgencia));
		}
		criteria.add(Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_PRIM_CONSULTA.toString()));
		Criterion crit1 = Restrictions.isNull(MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString());
		Criterion crit2 = Restrictions.eq(MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(), DominioIndPendenteItemPrescricao.N);
		Criterion crit3 = Restrictions.in(MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(),
				new DominioIndPendenteItemPrescricao[]{DominioIndPendenteItemPrescricao.A, DominioIndPendenteItemPrescricao.E});
		criteria.add(Restrictions.or(
				Restrictions.and(crit1, crit2), crit3));
		
		return executeCriteria(criteria);
	}
	
	
	public List<MpmSolicitacaoConsultoria> listarSolicitacaoRetornoConsultoria(final Integer atdSeq, final Integer scnSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class, "SCN");	
		criteria.createAlias("SCN." + MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.createAlias("ESP." + AghEspecialidades.Fields.CLINICA.toString(), "CLC");
		criteria.createAlias("SCN." + MpmSolicitacaoConsultoria.Fields.SERVIDOR_VALIDACAO.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		//criteria.createAlias("SCN." + MpmSolicitacaoConsultoria.Fields.ATENDIMENTO.toString(), "ATD");
		//criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		
		if (atdSeq != null) {
			criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), atdSeq));
		}
		
		if (scnSeq != null) {
			criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.SEQ.toString(), scnSeq));
		}

		return executeCriteria(criteria);
	}
	public List<SolicitacaoConsultoriaVO> pesquisaSolicitacaoConsultoriaPorAtendimentoEDataInicioEDataFim(Integer atdSeq, Date dthrInicio, Date dthrFim) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class, "MSC");
		
		criteria.createAlias("MSC." + MpmSolicitacaoConsultoria.Fields.PRESCRICAO_MEDICA.toString(), "PME", JoinType.INNER_JOIN);
		criteria.createAlias("MSC." + MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);		
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.X);
		
		criteria.add(Restrictions.and(
				Restrictions.not(Restrictions.in("MSC." + MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(), restricaoIn)),
				Restrictions.between("MSC." + MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString(), dthrInicio, dthrFim)));
		
		criteria.add(Restrictions.eq("PME." + MpmPrescricaoMedica.Fields.ATD_SEQ.toString(), atdSeq));
		
 		ProjectionList projectionList = Projections.projectionList()
			.add(Projections.property("PME." + MpmPrescricaoMedica.Fields.ATD_SEQ.toString()).as(SolicitacaoConsultoriaVO.Fields.ATD_SEQ.toString()))
			.add(Projections.property("PME." + MpmPrescricaoMedica.Fields.SEQ.toString()).as(SolicitacaoConsultoriaVO.Fields.SEQ.toString()))
			.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME.toString()).as(SolicitacaoConsultoriaVO.Fields.NOME_ESPECIALIDADE.toString()))
			.add(Projections.property("MSC." + MpmSolicitacaoConsultoria.Fields.DTHRSOLICITADA.toString()).as(SolicitacaoConsultoriaVO.Fields.DTHR_SOLICITADA.toString()))
			.add(Projections.property("MSC." + MpmSolicitacaoConsultoria.Fields.TIPO.toString()).as(SolicitacaoConsultoriaVO.Fields.TIPO.toString()))
			.add(Projections.property("MSC." + MpmSolicitacaoConsultoria.Fields.IND_URGENCIA.toString()).as(SolicitacaoConsultoriaVO.Fields.IND_URGENCIA.toString()))
			.add(Projections.property("MSC." + MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString()).as(SolicitacaoConsultoriaVO.Fields.PENDENCIA.toString()))
			.add(Projections.property("PME." + MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()).as(SolicitacaoConsultoriaVO.Fields.DTHR_INICIO.toString()))
			.add(Projections.property("PME." + MpmPrescricaoMedica.Fields.DTHR_FIM.toString()).as(SolicitacaoConsultoriaVO.Fields.DTHR_FIM.toString()));
 		
 		criteria.setProjection(projectionList);
 		criteria.setResultTransformer(Transformers.aliasToBean(SolicitacaoConsultoriaVO.class));
		return executeCriteria(criteria);
	}

	/**
	 * Realiza a consulta por Solicitações de Consultoria pendentes, utilizando como filtro o número de Atendimento informado.
	 * 
	 * @param atdSeq - Número de Atendimento
	 * @return Lista de Solicitações de Consultoria
	 */
	public List<MpmSolicitacaoConsultoria> listarSolicitacaoConsultoriaPendentesPorCodigoAtendimento(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class, "MSC");

//		criteria.createAlias("MSC." + MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), "ESP");

		criteria.add(Restrictions.isNull("MSC." + MpmSolicitacaoConsultoria.Fields.DTHR_CONHECIMENTO_RESPOSTA.toString()));
		criteria.add(Restrictions.isNotNull("MSC." + MpmSolicitacaoConsultoria.Fields.DTHR_RESPOSTA.toString()));
		criteria.add(Restrictions.eq("MSC." + MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.eq("MSC." + MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), DominioOrigemSolicitacaoConsultoria.M))
				.add(Restrictions.eq("MSC." + MpmSolicitacaoConsultoria.Fields.ORIGEM.toString(), null)));

		criteria.addOrder(Order.asc("MSC." + MpmSolicitacaoConsultoria.Fields.CRIADO_EM.toString()));

		return executeCriteria(criteria);
	}
	
	public List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaPorServidorEspecialidade(final RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class, "SCN");	
		criteria.createAlias("SCN." + MpmSolicitacaoConsultoria.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("ESP." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "PROF", JoinType.INNER_JOIN);
		criteria.createAlias("PROF." + AghProfEspecialidades.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.IND_CONCLUIDA.toString(), DominioIndConcluidaSolicitacaoConsultoria.N));
		criteria.add(Restrictions.isNull("SCN." + MpmSolicitacaoConsultoria.Fields.DTHR_FIM.toString()));
		criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.PENDENCIA.toString(), DominioIndPendenteItemPrescricao.N));
		criteria.add(Restrictions.eq("PROF." + AghProfEspecialidades.Fields.IND_PROF_REALIZA_CONSULTORIA.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("SCN." + MpmSolicitacaoConsultoria.Fields.TIPO.toString(), DominioTipoSolicitacaoConsultoria.C));
		criteria.add(Restrictions.eq("PROF." + AghProfEspecialidades.Fields.SERVIDOR.toString(), servidor));

		DetachedCriteria subCriteria = obterSubCriteriaConsultoriasInternacaoPorAtendimento(null);
		criteria.add(Subqueries.propertyIn("SCN." + MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), subCriteria));
		criteria.addOrder(Order.desc("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()));

		return executeCriteria(criteria);
	}
	
	public Object obterSeqAtendimentoOrigemEnfermagemPorConsultoria(Integer seqConsultoria){
	StringBuilder sql = new StringBuilder(200);
		 sql.append(" SELECT")
		.append(" ATD_SEQ ")
		.append(" FROM ")
		.append(" AGH.MPM_SOLICITACAO_CONSULTORIAS")
		.append(" WHERE")
		.append(" SEQ = :seqConsultoria");
		Query query = this.createNativeQuery(sql.toString());
		query.setParameter("seqConsultoria", seqConsultoria);
		return query.getSingleResult();
	}

}
