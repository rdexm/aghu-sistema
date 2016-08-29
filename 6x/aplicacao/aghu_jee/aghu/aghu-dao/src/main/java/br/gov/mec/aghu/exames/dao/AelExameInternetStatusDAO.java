package br.gov.mec.aghu.exames.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameLiberadoGrupoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameReenvioGrupoVO;
import br.gov.mec.aghu.model.AelExameInternetStatus;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelExameInternetStatusDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameInternetStatus> {
	
	private static final long serialVersionUID = 3257162530007732360L;
	private final String stringSeparator = ".";

	private DetachedCriteria obterCriteria() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameInternetStatus.class);
		return criteria;
    }

    /** 
     * Buscar a lista exames que deverão ser envidos para a fila
     * @param situacao
     * @return
     */
	public List<MensagemSolicitacaoExameLiberadoGrupoVO> buscarExamesAgrupadosPorSolicitacaoArea(DominioStatusExameInternet status, DominioSituacaoExameInternet situacao, Integer maxResults){
		
		final DetachedCriteria criteria = obterCriteria();		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty(AelExameInternetStatus.Fields.SOLICITACAO_EXAME_SEQ.toString()))
				.add(Projections.groupProperty(AelExameInternetStatus.Fields.EXAME_INTERNET_GRUPO_SEQ.toString())));

		if (situacao != null) {
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.SITUACAO.toString(), situacao));
		}

		if (status != null) {
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.STATUS.toString(), status));
		}

		List<MensagemSolicitacaoExameLiberadoGrupoVO> listExameInternetGrupoVO = new ArrayList<MensagemSolicitacaoExameLiberadoGrupoVO>();
		List<Object[]> listaArrayObject = executeCriteria(criteria, 0, maxResults, null, true);

		for (Object[] arrayObject : listaArrayObject) {
			MensagemSolicitacaoExameLiberadoGrupoVO vMensagemSolicitacaoExameGrupoVO = new MensagemSolicitacaoExameLiberadoGrupoVO();
			vMensagemSolicitacaoExameGrupoVO.setSeqSolicitacaoExame((Integer)arrayObject[0]);
			vMensagemSolicitacaoExameGrupoVO.setSeqExameInternetGrupo((Integer)arrayObject[1]);
			listExameInternetGrupoVO.add(vMensagemSolicitacaoExameGrupoVO);
		}

		return listExameInternetGrupoVO;
		
	}
	
	/**
	 * Buscar a data da primeira liberação do exame para um mesmo grupo 
	 * @param soeSeq
	 * @param seqGrupo
	 * @return
	 */
	public Date buscarDataUltimaLiberacaoPorSolicitacaoGrupo(Integer soeSeq, Integer seqGrupo){
		
		final DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.STATUS.toString(), DominioStatusExameInternet.FG));
		criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.SOLICITACAO_EXAME_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.EXAME_INTERNET_GRUPO_SEQ.toString(), seqGrupo));
		criteria.addOrder(Order.desc(AelExameInternetStatus.Fields.DTHR_STATUS.toString()));
		
		List<AelExameInternetStatus> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0).getDataHoraStatus();
		} else {
			return null;
		}
	
	}
	
	public List<AelExameInternetStatus> buscarExameInternetStatus(Integer soeSeq, Integer seqGrupo, DominioStatusExameInternet status, DominioSituacaoExameInternet situacao){
		
		final DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.SOLICITACAO_EXAME_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.EXAME_INTERNET_GRUPO_SEQ.toString(), seqGrupo));
		if (situacao != null) {
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.SITUACAO.toString(), situacao));
		}

		if (status != null) {
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.STATUS.toString(), status));
		}

		return executeCriteria(criteria);
		
	}
	
	public List<AelExameInternetStatus> listarExameInternetStatus(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Date dataHoraInicial, Date dataHoraFinal,
			DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet status, Integer iseSoeSeq,
			Short iseSeqp, String localizador, String sigla, String nroRegConselho, Long cnpjContratante) throws ParseException {
		
		final DetachedCriteria criteria = obterCriteriaFiltroConsultaExameInternet( dataHoraInicial, dataHoraFinal, situacao, status, iseSoeSeq, 
																					iseSeqp,localizador, sigla, nroRegConselho, cnpjContratante);
		if(StringUtils.isBlank(sigla) && StringUtils.isBlank(nroRegConselho) && cnpjContratante == null && StringUtils.isBlank(localizador)){
			criteria.createAlias(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString(), AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString(), JoinType.INNER_JOIN);
		}
		criteria.createAlias(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString()+"."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "SE_ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString()+"."+AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(), "SE_SER", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AelExameInternetStatus.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "ISE_SE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE_SE."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ISE_SE_ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE_SE_ATD."+AghAtendimentos.Fields.PACIENTE.toString(), "ISE_SE_ATD_PAC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("ISE_SE_ATD."+AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString(), "ISE_SE_ATD_EXT", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("ISE_SE_ATD_EXT."+AghAtendimentosPacExtern.Fields.MEDICOEXTERNO.toString(), "ISE_SE_ATD_EXT_MED", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AelExameInternetStatus.Fields.EXAME_INTERNET_GRUPO.toString(), "EIG", JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);		
		
	}
	
	public Long listarExameInternetStatusCount(Date dataHoraInicial,
			Date dataHoraFinal, DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet status, Integer iseSoeSeq,
			Short iseSeqp, String localizador, String sigla, String nroRegConselho, Long cnpjContratante ) throws ParseException {
		
		final DetachedCriteria criteria = this
				.obterCriteriaFiltroConsultaExameInternet(dataHoraInicial,
						dataHoraFinal, situacao, status, iseSoeSeq, iseSeqp,
						localizador, sigla, nroRegConselho, cnpjContratante);
		return executeCriteriaCount(criteria);		
		
	}

	
	private DetachedCriteria obterCriteriaFiltroConsultaExameInternet(Date dataHoraInicial,
			Date dataHoraFinal, DominioSituacaoExameInternet situacao,
			DominioStatusExameInternet status, Integer iseSoeSeq,
			Short iseSeqp, String localizador, String sigla, String nroRegConselho,
			Long cnpjContratante) throws ParseException {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameInternetStatus.class);

		if (dataHoraInicial != null && dataHoraFinal != null) {
			criteria.add(Restrictions.between(AelExameInternetStatus.Fields.DTHR_STATUS.toString(),	dataHoraInicial, dataHoraFinal));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.SITUACAO.toString(), situacao));
		}
		
		if(status != null){
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.STATUS.toString(), status));
		}
		
		if(iseSoeSeq != null){
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.SOLICITACAO_EXAME_SEQ.toString(), iseSoeSeq));
		}
		
		if(iseSeqp != null){
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.ITEM_SOLICITACAO_EXAME_SEQP.toString(), iseSeqp));
		}
		
		if(!StringUtils.isBlank(sigla) || !StringUtils.isBlank(nroRegConselho) || cnpjContratante != null || !StringUtils.isBlank(localizador)){
			this.incluirRestricao(criteria, localizador, sigla, nroRegConselho, cnpjContratante);
		}
				
		return criteria;
	}
	
	/**
	 * Inclusão da restrição do CNPJ Contratante
	 * @param criteria
	 * @param cnpjContratante
	 */
	private void incluirRestricaoCnpjContratante(DetachedCriteria criteria, Long cnpjContratante ){	
		criteria.createAlias(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString() + stringSeparator + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(),
				AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), JoinType.INNER_JOIN);

		criteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString() + stringSeparator + AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString(),
				AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString(), JoinType.INNER_JOIN);

		criteria.createAlias(AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString() + stringSeparator + AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString(),
				AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString(), JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.or(Restrictions.eq(AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString()+stringSeparator+AelLaboratorioExternos.Fields.CGC.toString(),cnpjContratante.toString()), Restrictions.eq(AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString()+stringSeparator+AelLaboratorioExternos.Fields.CGC.toString(),CoreUtil.formatarCNPJ(cnpjContratante))));
		
	}

	/**
	 * Adicionar as restrições na Criteria
	 * @param criteria
	 * @param localizador
	 * @param sigla
	 * @param nroRegConselho
	 * @param cnpjContratante
	 */
	private void incluirRestricao(DetachedCriteria criteria, String localizador, String sigla, String nroRegConselho, Long cnpjContratante ){
		
		criteria.createAlias(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString(),
				AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString(), JoinType.INNER_JOIN);			

		if(!StringUtils.isBlank(localizador)){
			criteria.add(Restrictions.eq(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString() + stringSeparator + AelSolicitacaoExames.Fields.LOCALIZADOR.toString(), localizador));	
		}
		
		if(cnpjContratante != null){
			this.incluirRestricaoCnpjContratante(criteria, cnpjContratante);
		}
		
		if(!StringUtils.isBlank(sigla) || !StringUtils.isBlank(nroRegConselho)){
			//Caso somente a sigla esteja preenchida, faz a busca apenas dos médicos internos (médico externo não tem a informação da sigla)
			if(!StringUtils.isBlank(sigla) && StringUtils.isBlank(nroRegConselho)){ 
				Subqueries.propertyIn(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString() + stringSeparator + AelSolicitacaoExames.Fields.SEQ.toString(), this.subCriteriaMedicoInterno(sigla, nroRegConselho));
			//Caso contrário, faz a busca tanto para médico interno como externo
			}else{
				criteria.add(Restrictions.or(
						Subqueries.propertyIn(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString() + stringSeparator + AelSolicitacaoExames.Fields.SEQ.toString(), this.subCriteriaMedicoExterno(nroRegConselho)), 
						Subqueries.propertyIn(AelExameInternetStatus.Fields.SOLICITACAO_EXAME.toString() + stringSeparator + AelSolicitacaoExames.Fields.SEQ.toString(), this.subCriteriaMedicoInterno(sigla, nroRegConselho))
						));
			}			
		}
		
	}

	/**
	 * Retorna a subquery com os exames de médicos externos
	 * 
	 * Obs.: Para a pesquisa do médico externo existe apenas o número do registro 
	 * @param nroRegConselho
	 * @return
	 */
	private DetachedCriteria subCriteriaMedicoExterno(String nroRegConselho){

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
				
		subCriteria.setProjection(Projections.distinct(Projections.property(AelSolicitacaoExames.Fields.SEQ.toString())));
		
		subCriteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), JoinType.INNER_JOIN);
		
		subCriteria.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString() + stringSeparator + AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString(),
				AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString(), JoinType.INNER_JOIN);
		
		subCriteria.createAlias(AghAtendimentos.Fields.ATENDIMENTO_PACIENTE_EXTERNO.toString() + stringSeparator + AghAtendimentosPacExtern.Fields.MEDICOEXTERNO.toString(),
				AghAtendimentosPacExtern.Fields.MEDICOEXTERNO.toString(), JoinType.INNER_JOIN);
				
		if(!StringUtils.isBlank(nroRegConselho)){
			subCriteria.add(Restrictions.eq(AghAtendimentosPacExtern.Fields.MEDICOEXTERNO.toString() + stringSeparator + AghMedicoExterno.Fields.CRM.toString(), Integer.parseInt(nroRegConselho)));
		}		

		return subCriteria;
		
	}
	

	/**
	 * Retorna a subquery com os exames de médicos internos
	 * @param sigla
	 * @param nroRegConselho
	 * @return
	 */
	private DetachedCriteria subCriteriaMedicoInterno(String sigla, String nroRegConselho){

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelSolicitacaoExames.class);
				
		subCriteria.setProjection(Projections.distinct(Projections.property(AelSolicitacaoExames.Fields.SEQ.toString())));

		subCriteria.createAlias(AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(),
				AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString(), JoinType.INNER_JOIN);
		
		// rap_pessoas_fisicas
		subCriteria.createAlias(AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString() + stringSeparator + RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString(), JoinType.INNER_JOIN);
		
		// rap_qualificacoes
		subCriteria.createAlias(RapServidores.Fields.PESSOA_FISICA.toString() + stringSeparator + RapPessoasFisicas.Fields.QUALIFICACOES.toString(),
				RapPessoasFisicas.Fields.QUALIFICACOES.toString(), JoinType.INNER_JOIN);
		
		// rap_tipos_qualificacao
		subCriteria.createAlias( RapPessoasFisicas.Fields.QUALIFICACOES.toString()	+ stringSeparator + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), JoinType.INNER_JOIN);
		
		// rap_conselhos_profissionais
		subCriteria.createAlias( RapQualificacao.Fields.TIPO_QUALIFICACAO.toString() + stringSeparator + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(),
				RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), JoinType.INNER_JOIN);
		
		//Retirada a restrição de data do servidor, conforme solicitado pela analista
		/*
		subCriteria.add(Restrictions.or(
				 Restrictions.isNull(AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString() + stringSeparator + RapServidores.Fields.DATA_FIM_VINCULO.toString()),
				 Restrictions.gt(AelSolicitacaoExames.Fields.SERVIDOR_EH_RESPONSABILID.toString() + stringSeparator + RapServidores.Fields.DATA_FIM_VINCULO.toString(),
						 	  DateUtil.truncaData(new Date()))));
        */
		if(!StringUtils.isBlank(sigla)){

			subCriteria.add(Restrictions.eq(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString() 
						+ stringSeparator
						+ RapConselhosProfissionais.Fields.SIGLA.toString(), sigla));

			subCriteria.add(Restrictions.isNotNull(RapPessoasFisicas.Fields.QUALIFICACOES.toString()+stringSeparator+RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));			
		}			

		if(!StringUtils.isBlank(nroRegConselho)){
			subCriteria.add(Restrictions.eq(RapPessoasFisicas.Fields.QUALIFICACOES.toString()+stringSeparator+RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString(),nroRegConselho));
		}
		
		return subCriteria;
		
	}
	
	
	public List<MensagemSolicitacaoExameReenvioGrupoVO> buscarExamesComErroParaReenvio(Integer maxResults, Date dataInicio){
		
		final DetachedCriteria criteria = createCriteriaReenvios(dataInicio);
		
		List<MensagemSolicitacaoExameReenvioGrupoVO> listExameInternetGrupoVO = new ArrayList<MensagemSolicitacaoExameReenvioGrupoVO>();
		List<Object[]> listaArrayObject = executeCriteria(criteria, 0, maxResults, null, true);

		for (Object[] arrayObject : listaArrayObject) {
			MensagemSolicitacaoExameReenvioGrupoVO vMensagemSolicitacaoExameGrupoVO = new MensagemSolicitacaoExameReenvioGrupoVO();
			vMensagemSolicitacaoExameGrupoVO.setSeqSolicitacaoExame((Integer)arrayObject[0]);
			vMensagemSolicitacaoExameGrupoVO.setSeqExameInternetGrupo((Integer)arrayObject[1]);
			listExameInternetGrupoVO.add(vMensagemSolicitacaoExameGrupoVO);
		}

		return listExameInternetGrupoVO;
		
	}

	private DetachedCriteria createCriteriaReenvios(Date dataInicio) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameInternetStatus.class, "eis");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(AelExameInternetStatus.Fields.SOLICITACAO_EXAME_SEQ.toString())))
				.add(Projections.property(AelExameInternetStatus.Fields.EXAME_INTERNET_GRUPO_SEQ.toString())));

		
		criteria.createCriteria(AelExameInternetStatus.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise", JoinType.INNER_JOIN);
		
		DominioSituacaoExameInternet[] situacoes = {DominioSituacaoExameInternet.N, DominioSituacaoExameInternet.E};
		
		criteria.add(Restrictions.in(AelExameInternetStatus.Fields.SITUACAO.toString(),situacoes));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(AelExameInternetStatus.class, "eis2");
		
		subCriteria.add(Restrictions.eqProperty("eis2." + AelExameInternetStatus.Fields.ITEM_SOLICITACAO_EXAME_SOE_SEQ.toString(), "eis." + AelExameInternetStatus.Fields.ITEM_SOLICITACAO_EXAME_SOE_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("eis2." + AelExameInternetStatus.Fields.ITEM_SOLICITACAO_EXAME_SEQP.toString(), "eis." + AelExameInternetStatus.Fields.ITEM_SOLICITACAO_EXAME_SEQP.toString()));
		subCriteria.add(Restrictions.eq("eis2." + AelExameInternetStatus.Fields.STATUS.toString(),DominioStatusExameInternet.EC));
		subCriteria.setProjection(Projections.projectionList()
				.add(Projections.property("eis2." + AelExameInternetStatus.Fields.ITEM_SOLICITACAO_EXAME_SOE_SEQ.toString())));
		criteria.add(Subqueries.notExists(subCriteria));

		criteria.add(Restrictions.ge(AelExameInternetStatus.Fields.DTHR_STATUS.toString(), dataInicio));
		criteria.add(Restrictions.ne("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "CA"));
		criteria.add(Restrictions.isNotNull(AelExameInternetStatus.Fields.MENSAGEM.toString()));
		return criteria;
	}

	
}
