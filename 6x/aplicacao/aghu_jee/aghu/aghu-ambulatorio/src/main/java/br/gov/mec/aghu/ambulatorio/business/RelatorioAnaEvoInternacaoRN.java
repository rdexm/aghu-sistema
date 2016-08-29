package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamAlergiasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamCustomQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamImpDiagnosticaDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemExameDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemEvolucaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgAlergiasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncAmbulatoriaisDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncExternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgExameDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgMedicacoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgSinalVitalDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamValorValidoQuestaoDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamAlergias;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamConcatenacao;
import br.gov.mec.aghu.model.MamCustomQuestao;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamImpDiagXCid;
import br.gov.mec.aghu.model.MamImpDiagnostica;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamQuestao;
import br.gov.mec.aghu.model.MamRespostaAnamneses;
import br.gov.mec.aghu.model.MamRespostaEvolucoes;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.MamTrgAlergias;
import br.gov.mec.aghu.model.MamTrgEncAmbulatoriais;
import br.gov.mec.aghu.model.MamTrgEncExternos;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamTrgSinalVital;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamValorValidoQuestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class RelatorioAnaEvoInternacaoRN extends BaseBusiness {

	private static final String NLNL = "\n\n";

	private static final String NL_NAO_INFORMADO = "\nNão informado";

	private static final String NL_TAB = "\n          ";

	private static final String NAO_INFORMADO = ": Não Informado";

	private static final Log LOG = LogFactory.getLog(RelatorioAnaEvoInternacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MamAlergiasDAO mamAlergiasDAO;
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@Inject
	private MamTipoItemAnamnesesDAO mamTipoItemAnamnesesDAO;
	
	@Inject
	private MamItemEvolucoesDAO mamItemEvolucoesDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MamItemExameDAO mamItemExameDAO;
	
	@Inject
	private MamTrgAlergiasDAO mamTrgAlergiasDAO;
	
	@Inject
	private MamRespostaEvolucoesDAO mamRespostaEvolucoesDAO;
	
	@Inject
	private MamTrgExameDAO mamTrgExameDAO;
	
	@Inject
	private MamItemAnamnesesDAO mamItemAnamnesesDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private MamCustomQuestaoDAO mamCustomQuestaoDAO;
	
	@Inject
	private MamTrgEncExternoDAO mamTrgEncExternoDAO;
	
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamValorValidoQuestaoDAO mamValorValidoQuestaoDAO;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private MamQuestaoDAO mamQuestaoDAO;
	
	@Inject
	private MamTrgEncAmbulatoriaisDAO mamTrgEncAmbulatoriaisDAO;
	
	@Inject
	private MamImpDiagnosticaDAO mamImpDiagnosticaDAO;
	
	@Inject
	private MamTrgMedicacoesDAO mamTrgMedicacoesDAO;
	
	@Inject
	private MamTrgSinalVitalDAO mamTrgSinalVitalDAO;
	
	@Inject
	private MamRespostaAnamnesesDAO mamRespostaAnamnesesDAO;
	
	@EJB
	private AtendimentoPacientesAgendadosRN atendimentoPacientesAgendadosRN;
	
	@Inject
	private MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO;

	private static final long serialVersionUID = 35747991170638293L;


	/**
	 * ORADB MAMP_ATU_IMP_ANA_INT
	 * Verifica a existencia de dados para serem impressos no Relatório de Anamneses
	 * @param atdSeq - Código do atendimento.
	 * @return
	 */
	public Boolean existeDadosImprimirRelatorioAnamneses(Integer atdSeq) {
		Boolean existeDados = Boolean.FALSE;
		if (atdSeq != null) {
			List<MamAnamneses> result = getMamAnamnesesDAO().pesquisarAnamnesesPorAtendimento(atdSeq);
			existeDados = (result != null && !result.isEmpty());
		}
		return existeDados;
	}
	
	public Integer obterConNumeroAtendimentoEmergencia(Long trgSeq) {
		MamTrgEncInterno tei = getMamTrgEncInternoDAO().pesquisarEncaminhamentoInternoPorSeqTriagem(trgSeq);
		if (tei != null && tei.getConsulta() != null) {
			return tei.getConsulta().getNumero();
		}
		return null;
	}
	
	/**
	 * @ORADB MAMP_GET_CUSTOM_ANA
	 * @param anaSeq
	 * @param mostraNaoInf
	 * @param tinSeq
	 * @param qusQutSeq
	 * @param qusSeqp
	 * @return
	 * @author bruno.mourao
	 * @since 17/05/2012
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String obterCustomAnamnese(Long anaSeq, Boolean mostraNaoInf, Integer tinSeq, Integer qusQutSeq, Short qusSeqp){
		String retorno = null;
		
		/*
		 * --
			-- quando a visualização for chamada pela tela (p_tin_seq=null)
			-- deve-se mostrar todos os registros de mam_questoes e no caso
			-- em que for chamado por um relatório (p_tin_seq com conteúdo)
			-- o registro de mam_questões que tiver ind_mostra_impressao = 'N'
			-- não deverá poderá aparecer.
			--
			-- v_modo: "T" --> mostrar na tela
			--         "I" --> sair em um relatório
			--
		 */ 
		String modo = "";
		
		if(tinSeq == null){
			modo = "T";
		}
		else{
			modo = "I";
		}

		MamQuestao questao = getMamQuestaoDAO().obterQuestaoPorIdModo(qusQutSeq, qusSeqp, modo);// cur_qus
		
		String descricao = "";
		
		if(questao != null){
			descricao = questao.getDescricao();// cur_qus
		}
		
		String valorAnterior = "";
		StringBuffer negaSimples = null;
		
		/*
		 * --
			-- Verifica se houve negações simples, sem resposta
			--
		 */
		
		List<MamValorValidoQuestao> nega = getMamValorValidoQuestaoDAO().pesquisarValorValidoQuestaoPorAnamneseQuestaoNegaSimples(anaSeq, questao.getId().getQutSeq(), questao.getId().getSeqp(), modo, null); //cur_nega
		
		for(MamValorValidoQuestao valorValidoqst : nega){
			if(negaSimples == null){
				negaSimples = new StringBuffer();
			}
			if(!valorValidoqst.getValor().equals(valorAnterior)){
				valorAnterior = valorValidoqst.getValor();
				negaSimples.append(NL_TAB).append(valorValidoqst.getValor()).append(':').append(valorValidoqst.getMamQuestao().getDescricao().toLowerCase());
				
			}
			else{
				negaSimples.append(',').append(valorValidoqst.getMamQuestao().getDescricao().toLowerCase());
			}
		}
		
		valorAnterior = "";
		StringBuffer negaResp = new StringBuffer();
		
		/*
		 * --
			-- Verifica se houve negações com resposta
			--
		 */
		
		List<MamValorValidoQuestao> negaRespList = getMamValorValidoQuestaoDAO().pesquisarValorValidoQuestaoPorAnamneseQuestaoNegaComResposta(anaSeq, qusQutSeq, qusSeqp, modo,DominioSimNao.S.toString(),null); //cur_nega_resp
		
		for (MamValorValidoQuestao negResp : negaRespList) {
			negaResp.append(NL_TAB)
			.append(negResp.getValor()).append(':').append(negResp.getMamQuestao().getDescricao().toLowerCase()).append(':');
			if(negResp.getMamRespostaAnamneseses() != null && negResp.getMamRespostaAnamneseses().size() > 0){
				negaResp.append(negResp.getMamRespostaAnamneseses().iterator().next().getResposta());
			}
		}
		
		Boolean primeiraVes = true;
		
		/*
		 * --
		-- Verifica as respostas sem negação e sua concatenação
		--
		 */
		
		List<MamRespostaAnamneses> respostasList = getMamRespostaAnamnesesDAO().pesquisarRespostaAnamnesesPorAnamneseQuestaoModo(anaSeq, qusQutSeq, qusSeqp, modo, DominioSimNao.S ,null); //cur_resp
		
		String textoAntesResposta = "";
		String textoDepoisResposta = "";
		StringBuffer respostas = new StringBuffer(20);
		String caracter = null;
		Boolean concatenado = false;
		for(MamRespostaAnamneses resposta : respostasList){
			if(resposta.getMamQuestao().getTextoAntesResposta() != null){
				textoAntesResposta = " " + resposta.getMamQuestao().getTextoAntesResposta(); 
			}
			else{
				textoAntesResposta = null;
			}
			
			if(resposta.getMamQuestao().getTextoDepoisResposta() != null){
				textoDepoisResposta = " " + resposta.getMamQuestao().getTextoDepoisResposta(); 
			}
			else{
				textoDepoisResposta = null;
			}
			
			if(primeiraVes){
				primeiraVes = false;
				if(respostas != null){
					respostas.append('\n');
				}
			}
			/*
			 *  --
			  -- verifica se a questão está concatenada
			  --
			 */
			List<MamConcatenacao> concatenacoes = getAmbulatorioFacade().pesquisarConcatenacaoAtivaPorIdQuestao(qusQutSeq, qusSeqp);//cur_concat
			
			if(concatenacoes != null && !concatenacoes.isEmpty()){
				concatenado = true;	
			}
			
			if((resposta.getResposta() != null && resposta.getMamQuestao().getTextoFormatado() == null)
					|| (resposta.getResposta() != null && resposta.getMamQuestao().getTextoFormatado() != null
					&& !resposta.getResposta().equals(resposta.getMamQuestao().getTextoFormatado()))){
				
				if(!concatenado){
					if(concatenacoes == null || concatenacoes.isEmpty()){
						if(respostas != null){
							respostas.append('\n');
						}
						respostas.append("          ");
						respostas.append(resposta.getMamQuestao().getDescricao()).append(':');
					}
					else{
						if(respostas != null){
							respostas.append('\n');
						}
					}
					if(resposta.getMamQuestao().getTextoAntesResposta() != null){
						respostas.append(textoAntesResposta);
					}
					respostas.append(resposta.getResposta());
					
					if(resposta.getMamQuestao().getTextoDepoisResposta() != null){
						respostas.append(textoDepoisResposta);
					}
				}
				else{
					respostas.append(caracter).append(textoAntesResposta).append(' ').append(resposta.getResposta()).append(textoDepoisResposta);
				}
			}
			if(mostraNaoInf && ((resposta.getResposta() == null) || (resposta.getResposta() != null && resposta.getMamQuestao().getTextoFormatado() != null && resposta.getResposta().equals(resposta.getMamQuestao().getTextoFormatado())))){// mostra inclusive os nao inf
				if(!concatenado){
					respostas.append('\n');
				}
				else{
					respostas.append(caracter).append(' ');
				}
				respostas.append(resposta.getMamQuestao().getDescricao()).append(" : Não Informado");
			}
		}
		
		if((negaSimples != null && StringUtils.isNotBlank(negaSimples.toString())) 
				|| (negaResp != null && StringUtils.isNotBlank(negaResp.toString())) 
				|| (respostas != null && StringUtils.isNotBlank(respostas.toString()))){ //-- monta a descrição do tipo
			retorno = descricao + ": \n" + ((respostas != null) ? respostas.toString() : "") + ((negaResp != null) ? negaResp.toString() : "") + ((negaSimples != null) ? negaSimples.toString() : "");
		}
		
		/*
		 * --
		-- se chamado do relatório, não pode iniciar com linha em branco ou espaços
		--
		 */
		if(tinSeq != null){
			if(retorno != null){
				retorno = StringUtil.leftTrim(retorno);
				while(retorno.startsWith("\n")){
					retorno = retorno.substring(2);
				}
			}
		}
		
		return retorno;
	}
	
	
	
	/**
	 * @ORADB MAMP_EMG_VIS_ANA
	 * @param anaSeq
	 * @param tinSeq
	 * @param rgtSeq
	 * @param mostraNaoInf
	 * @param montaCabecalhos
	 * @return
	 * @author fausto.santos
	 * @throws ApplicationBusinessException 
	 *  
	 * @since 22/05/2012
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
		public String obterEmgVisaoAnamnese(Long anaSeq, Integer tinSeq, Long rgtSeq, Boolean mostraNaoInf, String montaCabecalhos) throws ApplicationBusinessException{
		
		StringBuilder retorno = new StringBuilder();
		if (anaSeq != null) {

			//Para cada tipo de item vai verificar se existe algum registro
			List<MamTipoItemAnamneses> tinList = getMamTipoItemAnamnesesDAO().buscaTipoItemAnamneseAtivoOrdenado(tinSeq);
			for (MamTipoItemAnamneses rTin : tinList) {
				String valorAnterior = " ";
				StringBuilder negaSimples = new StringBuilder();

				//cur_nega
				List<MamValorValidoQuestao> negaList =  getMamValorValidoQuestaoDAO().pesquisarValorValidoQuestaoPorAnamneseQuestaoNega(anaSeq, rTin.getSeq(), null, null, null);
				
				//Verifica se houve negações simples, sem resposta
				for (MamValorValidoQuestao rNega : negaList) {
					if (!rNega.getValor().equals(valorAnterior)) {
						valorAnterior = rNega.getValor();
						negaSimples.append('\n').append(rNega.getValor()).append(": ").append(rNega.getMamQuestao().getDescricao().toLowerCase());
					} else {
						negaSimples.append(", ").append(rNega.getMamQuestao().getDescricao().toLowerCase());
					}
				}
				
				valorAnterior = " ";
				StringBuilder negaResp = new StringBuilder();					
				
				//cur_nega_resp
				List<MamValorValidoQuestao> negaRespList = getMamValorValidoQuestaoDAO().pesquisarValorValidoQuestaoPorAnamneseQuestaoNegaComResposta(anaSeq, rTin.getSeq(), null, null, null);

				//Verifica se houve negações com resposta
				for (MamValorValidoQuestao rNegaResp : negaRespList) {
					negaResp.append('\n').append(rNegaResp.getValor()).append(": ")
							.append(rNegaResp.getMamQuestao().getDescricao().toLowerCase()).append(": ");
					if (rNegaResp.getMamRespostaAnamneseses() != null && !rNegaResp.getMamRespostaAnamneseses().isEmpty()) {
						negaResp.append(rNegaResp.getMamRespostaAnamneseses().iterator().next().getResposta()).append('.');
					}
				}
				
				Boolean primeiraVez = Boolean.TRUE;
				
				//cur_resp
				List<MamRespostaAnamneses> negaConcatList = getMamRespostaAnamnesesDAO().pesquisarRespostaAnamnesePorAnamneseQuestaoModo(anaSeq, null, null, null, rTin.getSeq()); 
				
				StringBuilder textoAntesResposta = new StringBuilder();
				StringBuilder textoDepoisResposta =  new StringBuilder();
				StringBuilder respostas =  new StringBuilder();
				StringBuilder caracter =  new StringBuilder();
				
				//Verifica as respostas sem negação e sem concatenação
				for (MamRespostaAnamneses rResp : negaConcatList) {
					if (StringUtils.isNotEmpty(rResp.getMamQuestao().getTextoAntesResposta())) {
						textoAntesResposta.append(' ').append(rResp.getMamQuestao().getTextoAntesResposta()); 
					} else {
						textoAntesResposta = new StringBuilder();
					}
					
					if (StringUtils.isNotEmpty(rResp.getMamQuestao().getTextoDepoisResposta())) {
						textoDepoisResposta.append(' ').append(rResp.getMamQuestao().getTextoDepoisResposta()); 
					} else {
						textoDepoisResposta = new StringBuilder();
					}
					
					if (primeiraVez) {
						primeiraVez = Boolean.FALSE;
						if (!respostas.toString().isEmpty()) {
							respostas.append('\n');
						}
					}
				
					//cur_concat
					List<MamConcatenacao> qConcat = getAmbulatorioFacade().pesquisarConcatenacaoAtivaPorIdQuestao(rResp.getId().getQusQutSeq(), rResp.getId().getQusSeqp());
					
					//verifica se a questão está concatenada
					Boolean concatenado = Boolean.FALSE;

					if(qConcat != null && !qConcat.isEmpty()){
						concatenado = Boolean.TRUE;	
						caracter.append(qConcat.get(0).getCaracter());	
					} 
						
					if ((StringUtils.isNotEmpty(rResp.getResposta()) && StringUtils.isEmpty(rResp.getMamQuestao().getTextoFormatado()))
							|| (StringUtils.isNotEmpty(rResp.getResposta()) && StringUtils.isNotEmpty(rResp.getMamQuestao().getTextoFormatado())
									&& !StringUtils.equals(rResp.getResposta(), rResp.getMamQuestao().getTextoFormatado()))) {
						if (!concatenado) {
							//cur_c1
							List<MamConcatenacao> concatenacoes = getAmbulatorioFacade().pesquisarConcatenacaoAtivaPorIdQuestao(rResp.getMamQuestao().getId().getQutSeq(),rResp.getMamQuestao().getId().getSeqp());
							
							if (concatenacoes == null || concatenacoes.isEmpty()) {
								respostas.append('\n').append(rResp.getMamQuestao().getDescricao()).append(": ");
							} else {
								respostas.append('\n');
							}
							
							if (StringUtils.isNotEmpty(rResp.getMamQuestao().getTextoAntesResposta())) {
								respostas.append(rResp.getMamQuestao().getTextoAntesResposta()).append(' ');
							}
							
							respostas.append(rResp.getResposta());
							
							if (StringUtils.isNotEmpty(rResp.getMamQuestao().getTextoDepoisResposta())) {
								respostas.append(' ').append(rResp.getMamQuestao().getTextoDepoisResposta());
							}
						} else {
							respostas.append(caracter).append(textoAntesResposta).append(' ').append(rResp.getResposta()).append(textoDepoisResposta);
						}
					}
					
					//cur_cqu
					List<MamCustomQuestao> customQuestao = getMamCustomQuestaoDAO().pesquisarCustomQuestao(rResp.getId().getQusQutSeq(), rResp.getId().getQusSeqp());
					
					//verifica a customização da questão
					if (customQuestao != null && !customQuestao.isEmpty()){
						String custom = obterCustomAnamnese(anaSeq, mostraNaoInf, tinSeq, rResp.getId().getQusQutSeq(), rResp.getId().getSeqp());
						if (StringUtils.isNotEmpty(custom)) {
							respostas.append('\n');
							respostas.append(custom);
						}						
					}
						
					//mostra inclusive os nao inf
					if(mostraNaoInf && ((StringUtils.isEmpty(rResp.getResposta()))
							|| (StringUtils.isNotEmpty(rResp.getResposta())
									&& StringUtils.isNotEmpty(rResp.getMamQuestao().getTextoFormatado())
									&&	StringUtils.equals(rResp.getResposta(), rResp.getMamQuestao().getTextoFormatado())))){
						if(!concatenado){
							respostas.append('\n').append(rResp.getMamQuestao().getDescricao()).append(NAO_INFORMADO);
						} else{
							respostas.append(caracter).append(' ').append(rResp.getMamQuestao().getDescricao()).append(NAO_INFORMADO);
						}
					}
				}
				
			    //Textos livres
				StringBuilder textoLivre = new StringBuilder();
				// cur_ian
				List<MamItemAnamneses> mamItemAnamnesesList = getMamItemAnamnesesDAO().pesquisarItemAnamnesesPorAnamnesesTipoItem(anaSeq, rTin.getSeq());

				for (MamItemAnamneses rIan : mamItemAnamnesesList) {
					MamTipoItemAnamneses tipoItem = rIan.getTipoItemAnamneses();
					if (tipoItem.getPermiteLivre()) {
						if (tipoItem.getPermiteQuest() || tipoItem.getPermiteFigura() || tipoItem.getIdentificacao()) {
							if (StringUtils.isNotEmpty(tipoItem.getDescricao())) {
								textoLivre.append('\n').append(rIan.getDescricao());
							} else {
								if (mostraNaoInf && respostas.toString().isEmpty()) {
									textoLivre.append(NL_NAO_INFORMADO); 
								}
							}
						} else {
							if (StringUtils.isNotEmpty(tipoItem.getDescricao())) {
								textoLivre.append('\n').append(rIan.getDescricao()).append('.');
							} else {
								if (mostraNaoInf && respostas.toString().isEmpty()) {
									textoLivre.append(NL_NAO_INFORMADO);
								}
							}
						}
					} else if (tipoItem.getIdentificacao()) {
						if (StringUtils.isNotEmpty(tipoItem.getDescricao())) {
							textoLivre.append('\n').append(rIan.getDescricao()).append('.');
						}
					}
				}
				
				//monta a descrição do tipo
				if (StringUtils.isNotEmpty(negaSimples.toString())
						|| StringUtils.isNotEmpty(negaResp.toString())
						|| StringUtils.isNotEmpty(respostas.toString())
						|| StringUtils.isNotEmpty(textoLivre.toString())) {
					
					if (tinSeq == null) {
						retorno.append(rTin.getDescricao()).append(": ");
					}
					retorno.append(textoLivre).append(respostas).append(negaResp).append(negaSimples).append(NLNL);
				}
				
				if (tinSeq != null) {
					String tmpRet = retorno.toString();
					if (!retorno.toString().isEmpty()) {
						tmpRet = StringUtil.leftTrim(retorno.toString());
					}
					while (tmpRet.startsWith("\n")) {
						tmpRet = tmpRet.substring(2);
					}
					retorno = new StringBuilder();
					retorno.append(tmpRet);
				}
				
				//tela
				if (tinSeq == null) {
					// MAMC_GET_IDENT_RESP
					String rodape = getAmbulatorioFacade().obterIdentificacaoResponsavel(getMamAnamnesesDAO().obterPorChavePrimaria(anaSeq), null);
					retorno.append(rodape).append('\n');
				}

				if (tinSeq == null) {
					//MAMC_EMG_VIS_NAA
					String notasAdicionaisAna = getProntuarioOnlineFacade().visualizarNotaAnamneseEMG(rgtSeq,null);
					if (StringUtils.isNotEmpty(notasAdicionaisAna)) {
						retorno.append('\n').append(notasAdicionaisAna);
					}
				}
			}
		}
		return retorno.toString();
	}
	
	
	/**
	 * ORADB MAMP_ATU_IMP_ANA_EVO
	 * Verifica a existencia de dados para serem impressos no Relatório de Evolução
	 * @param atdSeq - Código do atendimento.
	 * @return
	 */
	public Boolean existeDadosImprimirRelatorioEvolucoes(Integer atdSeq) {
		Boolean existeDados = Boolean.FALSE;
		if (atdSeq != null) {
			List<MamEvolucoes> result = getMamEvolucoesDAO().pesquisarEvolucoesPorAtendimento(atdSeq);
			existeDados = (result != null && !result.isEmpty());
		}
		return existeDados;
	}
	
	/**
	 * @ORADB MAMP_GET_CUSTOM_EVO
	 * @param evoSeq
	 * @param mostraNaoInf
	 * @param tieSeq
	 * @param qusQutSeq
	 * @param qusSeqp
	 * @return
	 * @author bruno.mourao
	 * @since 22/05/2012
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String obterCustomEvolucao(Long evoSeq, Boolean mostraNaoInf, Integer tieSeq, Integer qusQutSeq, Short qusSeqp){
		IAmbulatorioFacade ambulatorioFacade = getAmbulatorioFacade();
		MamQuestaoDAO mamQuestaoDAO = getMamQuestaoDAO();
		MamRespostaEvolucoesDAO mamRespostaEvolucoesDAO = getMamRespostaEvolucoesDAO();
		MamValorValidoQuestaoDAO mamValorValidoQuestaoDAO = getMamValorValidoQuestaoDAO();
		
		String retorno = null;
		
		/*
		 * --
			-- quando a visualização for chamada pela tela (p_tin_seq=null)
			-- deve-se mostrar todos os registros de mam_questoes e no caso
			-- em que for chamado por um relatório (p_tin_seq com conteúdo)
			-- o registro de mam_questões que tiver ind_mostra_impressao = 'N'
			-- não deverá poderá aparecer.
			--
			-- v_modo: "T" --> mostrar na tela
			--         "I" --> sair em um relatório
			--
		 */ 
		String modo = "";
		
		if(tieSeq == null){
			modo = "T";
		}
		else{
			modo = "I";
		}
		
		MamQuestao questao = mamQuestaoDAO.obterQuestaoPorIdModo(qusQutSeq, qusSeqp, modo);// cur_qus
		
		String descricao = "";
		
		if(questao != null){
			descricao = questao.getDescricao();// cur_qus
		}
		
		String valorAnterior = "";
		StringBuffer negaSimples = null;
		
		/*
		 * --
			-- Verifica se houve negações simples, sem resposta
			--
		 */
		
		List<MamValorValidoQuestao> nega = mamValorValidoQuestaoDAO.pesquisarValorValidoQuestaoPorEvolucaoQuestaoNegaSimples(evoSeq, tieSeq, qusQutSeq, qusSeqp, modo); //cur_nega
		
		for(MamValorValidoQuestao valorValidoqst : nega){
			if(negaSimples == null){
				negaSimples = new StringBuffer();
			}
			if(!valorValidoqst.getValor().equals(valorAnterior)){
				valorAnterior = valorValidoqst.getValor();
				negaSimples.append(NL_TAB).append(valorValidoqst.getValor()).append(':').append(valorValidoqst.getMamQuestao().getDescricao().toLowerCase());
				
			}
			else{
				negaSimples.append(',').append(valorValidoqst.getMamQuestao().getDescricao().toLowerCase());
			}
		}
		
		valorAnterior = "";
		StringBuffer negaResp = new StringBuffer();
		
		/*
		 * --
			-- Verifica se houve negações com resposta
			--
		 */
		List<MamValorValidoQuestao> negaRespList = mamValorValidoQuestaoDAO.pesquisarValorValidoQuestaoPorEvolucaoQuestaoNegaComResposta(evoSeq, tieSeq, qusQutSeq, qusSeqp, modo); //cur_nega_resp
		
		for (MamValorValidoQuestao negResp : negaRespList) {
			negaResp.append(NL_TAB)
			.append(negResp.getValor()).append(':').append(negResp.getMamQuestao().getDescricao().toLowerCase()).append(':');
			if(negResp.getMamRespostaEvolucoeses() != null && negResp.getMamRespostaEvolucoeses().size() > 0){
				negaResp.append(negResp.getMamRespostaEvolucoeses().iterator().next().getResposta());
			}
		}
		
		Boolean primeiraVes = true;
		
		/*
		 * --
		-- Verifica as respostas sem negação e sua concatenação
		--
		 */
		
		List<MamRespostaEvolucoes> respostasList = mamRespostaEvolucoesDAO.pesquisarRespostaEvolucoesPorEvolucaoQuestaoModo(evoSeq, qusQutSeq, qusSeqp, modo, DominioSimNao.S,null);//cur_resp
		
		String textoAntesResposta = "";
		String textoDepoisResposta = "";
		StringBuffer respostas = new StringBuffer(20);
		String caracter = null;
		Boolean concatenado = false;
		for(MamRespostaEvolucoes resposta : respostasList){
			if(resposta.getMamQuestao().getTextoAntesResposta() != null){
				textoAntesResposta = " " + resposta.getMamQuestao().getTextoAntesResposta(); 
			}
			else{
				textoAntesResposta = null;
			}
			
			if(resposta.getMamQuestao().getTextoDepoisResposta() != null){
				textoDepoisResposta = " " + resposta.getMamQuestao().getTextoDepoisResposta(); 
			}
			else{
				textoDepoisResposta = null;
			}
			
			if(primeiraVes){
				primeiraVes = false;
				if(respostas != null){
					respostas.append('\n');
				}
			}
			/*
			 *  --
			  -- verifica se a questão está concatenada
			  --
			 */
			
			List<MamConcatenacao> concatenacoes = ambulatorioFacade.pesquisarConcatenacaoAtivaPorIdQuestao(qusQutSeq, qusSeqp);//cur_concat
			
			if(concatenacoes != null && !concatenacoes.isEmpty()){
				concatenado = true;	
			}
			
			if((resposta.getResposta() != null && resposta.getMamQuestao().getTextoFormatado() == null)
					|| (resposta.getResposta() != null && resposta.getMamQuestao().getTextoFormatado() != null
					&& !resposta.getResposta().equals(resposta.getMamQuestao().getTextoFormatado()))){
				
				if(!concatenado){
					if(concatenacoes == null || concatenacoes.isEmpty()){
						if(respostas != null){
							respostas.append('\n');
						}
						respostas.append("          ");
						respostas.append(resposta.getMamQuestao().getDescricao()).append(':');
					}
					else{
						if(respostas != null){
							respostas.append('\n');
						}
					}
					if(resposta.getMamQuestao().getTextoAntesResposta() != null){
						respostas.append(textoAntesResposta);
					}
					respostas.append(resposta.getResposta());
					
					if(resposta.getMamQuestao().getTextoDepoisResposta() != null){
						respostas.append(textoDepoisResposta);
					}
				}
				else{
					respostas.append(caracter).append(textoAntesResposta).append(' ').append(resposta.getResposta()).append(textoDepoisResposta);
				}
			}
			if(mostraNaoInf && ((resposta.getResposta() == null) || (resposta.getResposta() != null && resposta.getMamQuestao().getTextoFormatado() != null && resposta.getResposta().equals(resposta.getMamQuestao().getTextoFormatado())))){// mostra inclusive os nao inf
				if(!concatenado){
					respostas.append('\n');
				}
				else{
					respostas.append(caracter).append(' ');
				}
				respostas.append(resposta.getMamQuestao().getDescricao()).append(" : Não Informado");
			}
		}
		
		if((negaSimples != null && StringUtils.isNotBlank(negaSimples.toString())) 
				|| (negaResp != null && StringUtils.isNotBlank(negaResp.toString())) 
				|| (respostas != null && StringUtils.isNotBlank(respostas.toString()))){ //-- monta a descrição do tipo
			retorno = descricao + ": \n" + ((respostas != null) ? respostas.toString() : "") + ((negaResp != null) ? negaResp.toString() : "") + ((negaSimples != null) ? negaSimples.toString() : "");
		}
		
		/*
		 * --
		-- se chamado do relatório, não pode iniciar com linha em branco ou espaços
		--
		 */
		if(tieSeq != null){
			if(retorno != null){
				retorno = StringUtil.leftTrim(retorno);
				while(retorno.startsWith("\n")){
					retorno = retorno.substring(2);
				}
			}
		}
		
		return retorno;
	}
	
	/**
	 * @ORADB MAMP_EMG_VIS_EVO
	 * @param evoSeq - Sequence da tabela MAM_EVOLUCOES
	 * @param tieSeq - Sequence da tabela MAM_TIPO_ITEM_EVOLUCOES
	 * @param rgtSeq - Sequence da tabela MAM_REGISTROSS
	 * @param mostraNaoInf
	 * @param montaCabecalhos
	 * @return
	 * @author fausto.santos
	 * @throws ApplicationBusinessException 
	 *  
	 * @since 22/05/2012
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String obterEmgVisaoEvolucao(Long evoSeq, Integer tieSeq,
			Long rgtSeq, Boolean mostraNaoInf, String montaCabecalhos,
			CseCategoriaProfissional categoriaProfissional) throws ApplicationBusinessException,
			ApplicationBusinessException {
		IAmbulatorioFacade ambulatorioFacade = getAmbulatorioFacade();
		IProntuarioOnlineFacade prontuarioOnlineFacade = getProntuarioOnlineFacade();
		AtendimentoPacientesAgendadosRN atendimentoPacientesAgendadosRN = getAtendimentoPacientesAgendadosRN();
		MamAlergiasDAO mamAlergiasDAO = getMamAlergiasDAO();
		MamCustomQuestaoDAO mamCustomQuestaoDAO = getMamCustomQuestaoDAO();
		MamEvolucoesDAO mamEvolucoesDAO = getMamEvolucoesDAO();
		MamItemEvolucoesDAO mamItemEvolucaoDAO = getMamItemEvolucaoDAO();
		MamRespostaEvolucoesDAO mamRespostaEvolucoesDAO = getMamRespostaEvolucoesDAO();
		MamValorValidoQuestaoDAO mamValorValidoQuestaoDAO = getMamValorValidoQuestaoDAO();
		MamTipoItemEvolucaoDAO mamTipoItemEvolucaoDAO = getMamTipoItemEvolucaoDAO();
		
		StringBuilder retorno = new StringBuilder();
		if (evoSeq != null) {		
			List<MamTipoItemEvolucao> mamTipoItemEvolucao = mamTipoItemEvolucaoDAO.buscaTipoItemEvolucaoAtivoOrdenado(tieSeq);
			
			//Para cada tipo de item vai verificar se existe algum registro
			for(MamTipoItemEvolucao rTie : mamTipoItemEvolucao){
				
				//boolean v_teve_valor = false;
				StringBuilder modo = new StringBuilder();
				
				if(tieSeq == null){
					modo.append('T');
				}else{
					modo.append('I');
				}
				
				String valorAnterior = "";
				StringBuilder negaSimples = new StringBuilder();
				
				//Verifica se houve negações simples, sem resposta
				List<MamValorValidoQuestao> mamValorValidoQuestaoList = mamValorValidoQuestaoDAO.pesquisarValorValidoQuestaoPorEvolucaoQuestaoNegaSimples(evoSeq, rTie.getSeq(), null, null, null); //cur_nega
				
				for(MamValorValidoQuestao rNega : mamValorValidoQuestaoList){
					if(!StringUtils.equals(rNega.getValor(),valorAnterior)){
						valorAnterior = rNega.getValor();
						negaSimples.append('\n').append(rNega.getValor()).append(": ")
								   .append(rNega.getMamQuestao().getDescricao().toLowerCase());
					}else{
						negaSimples.append(", ").append(rNega.getMamQuestao().getDescricao().toLowerCase());
					}
				}
				
				valorAnterior = "";
				StringBuilder negaResp = new StringBuilder();
				
				List<MamValorValidoQuestao> negaRespList = mamValorValidoQuestaoDAO.pesquisarValorValidoQuestaoPorEvolucaoQuestaoNegaComResposta(evoSeq, rTie.getSeq(), null, null, null); //cur_nega_resp
				
				for (MamValorValidoQuestao rNegaResp : negaRespList) {
					negaResp.append('\n').append(rNegaResp.getValor())
							.append(": ").append(rNegaResp.getMamQuestao().getDescricao().toLowerCase())
							.append(':').append(rNegaResp.getMamRespostaEvolucoeses().iterator().next().getResposta());
				}
				
				//Verifica as respostas sem negação e sem concatenação		
				List<MamRespostaEvolucoes> mamRespostaEvolucoesList = mamRespostaEvolucoesDAO.pesquisarRespostaEvolucoesPorEvolucaoQuestaoModo(evoSeq, null, null, modo.toString(), rTie.getSeq()); //cur_resp
				
				Boolean primeiraVez = Boolean.TRUE;
				StringBuilder textoAntesResposta = new StringBuilder();
				StringBuilder textoDepoisResposta =  new StringBuilder();
				StringBuilder respostas =  new StringBuilder();
				StringBuilder caracter =  new StringBuilder();		
			
				for(MamRespostaEvolucoes rResp : mamRespostaEvolucoesList){					
					if(rResp.getMamQuestao().getTextoAntesResposta() != null){
						textoAntesResposta.append(' ').append(rResp.getMamQuestao().getTextoAntesResposta()); 
					}else{
						textoAntesResposta = null;
					}
					
					if(rResp.getMamQuestao().getTextoDepoisResposta() != null){
						textoDepoisResposta.append(' ').append(rResp.getMamQuestao().getTextoDepoisResposta()); 
					}else{
						textoDepoisResposta = null;
					}
					
					if(primeiraVez){
						primeiraVez = Boolean.FALSE;
						if(respostas != null){
							respostas.append('\n');
						}
					}
					//verifica se a questão está concatenada
					
					List<MamConcatenacao> qConcat = ambulatorioFacade.pesquisarConcatenacaoAtivaPorIdQuestao(rResp.getId().getQusQutSeq(), rResp.getId().getQusSeqp());//cur_concat
					
					Boolean concatenado = Boolean.FALSE;
					
					if(qConcat != null && !qConcat.isEmpty()){
						concatenado = Boolean.TRUE;	
						caracter.append(qConcat.get(0).getCaracter());	
					}
					
					if ((rResp.getResposta() != null && rResp.getMamQuestao().getTextoFormatado() == null)
							|| (rResp.getResposta() != null && rResp.getMamQuestao().getTextoFormatado() != null
									&& !rResp.getResposta().equals(rResp.getMamQuestao().getTextoFormatado()))) {
	
						if (!concatenado) {
							List<MamConcatenacao> curC1 = ambulatorioFacade.pesquisarConcatenacaoAtivaPorIdQuestao(rResp.getMamQuestao().getId().getQutSeq(),rResp.getMamQuestao().getId().getSeqp());
							
							if(curC1 == null || curC1.isEmpty()) {
								respostas.append('\n').append(rResp.getMamQuestao().getDescricao()).append(": ");
							}else {
								respostas.append('\n');
							}
							
							if(rResp.getMamQuestao().getTextoAntesResposta() != null) {
								respostas.append(rResp.getMamQuestao().getTextoAntesResposta()).append(' ');
							}
							
							respostas.append(rResp.getResposta());
							
							if(rResp.getMamQuestao().getTextoDepoisResposta() != null){
								respostas.append(' ').append(rResp.getMamQuestao().getTextoDepoisResposta());
							}
						} else {
							respostas.append(caracter).append(textoAntesResposta).append(' ').append(rResp.getResposta()).append(textoDepoisResposta);
						}
					}
					
					List<MamCustomQuestao> custom = mamCustomQuestaoDAO.pesquisarCustomQuestao(rResp.getId().getQusQutSeq(), rResp.getId().getSeqp());
					
					if(custom != null){
						String vCustom = obterCustomEvolucao(evoSeq, mostraNaoInf, tieSeq, rResp.getId().getQusQutSeq(), rResp.getId().getSeqp());
						if(vCustom != null && !vCustom.isEmpty()){
							respostas.append('\n').append(vCustom);
						}		
					}
				
					// mostra inclusive os nao inf
					if (mostraNaoInf && ((rResp.getResposta() == null) || (rResp.getResposta() != null
									&& rResp.getMamQuestao().getTextoFormatado() != null
									&& rResp.getResposta().equals(rResp.getMamQuestao().getTextoFormatado())))) {
						if (!concatenado) {
							respostas.append('\n').append(rResp.getMamQuestao().getDescricao()).append(NAO_INFORMADO);
						} else {
							respostas.append(caracter).append(' ').append(rResp.getMamQuestao().getDescricao()).append(NAO_INFORMADO);
						}
					}
				}
				
				StringBuilder textoLivre = new StringBuilder();
				
				List<MamItemEvolucoes> mamItenEvolucaoList = mamItemEvolucaoDAO.pesquisarItemEvolucaoPorEvolucaoTipoItem(evoSeq, rTie.getSeq());
				
				for (MamItemEvolucoes rIen: mamItenEvolucaoList) {
					MamTipoItemEvolucao tipoItem = rIen.getTipoItemEvolucao();
					
					if (tipoItem.getPermiteLivre()) {
						if (tipoItem.getPermiteQuest() || tipoItem.getPermiteFigura() || tipoItem.getIdentificacao()) {
							if (StringUtils.isNotEmpty(tipoItem.getDescricao())) {
								textoLivre.append('\n').append(rIen.getDescricao());
							} else {
								//mostra inclusive os nao inf
								if (mostraNaoInf && StringUtils.isNotEmpty(respostas.toString())) {
									textoLivre.append(NL_NAO_INFORMADO); 
								}
							}
						} else {
							if (StringUtils.isNotEmpty(tipoItem.getDescricao())) {
								textoLivre.append('\n').append(rIen.getDescricao()).append('.');
							} else {
								if (mostraNaoInf && StringUtils.isEmpty(respostas.toString())) {
									textoLivre.append(NL_NAO_INFORMADO); 
								}
							}
						}
					} else if(tipoItem.getIdentificacao()) {
						if (StringUtils.isNotEmpty(tipoItem.getDescricao())) {
							textoLivre.append('\n').append(rIen.getDescricao()).append('.');
						}
					}
				}				
			
				//recupera as alergias informadas para sair junto com o subjetivo
				StringBuilder alergia = new StringBuilder();
				Boolean primeiraAlergia = Boolean.FALSE;
				
				if(StringUtils.equals("S",rTie.getSigla()) && tieSeq == null && StringUtils.equals("N",montaCabecalhos)){
					primeiraAlergia = Boolean.TRUE;
					
					List<MamAlergias> alergiaList = mamAlergiasDAO.pesquisarMamAlergiasPorSeqRegistro(rgtSeq);
					
					for(MamAlergias rAlg : alergiaList){
						if(primeiraAlergia){
							primeiraAlergia = Boolean.FALSE;										
							alergia.append("\n\n         Alergia: ").append(pesquisarMamAlergiasPorSeq(rAlg.getSeq()));
						}else{
							alergia.append("\n                  ").append(pesquisarMamAlergiasPorSeq(rAlg.getSeq()));
							
						}
					}
				}
				
				//recupera as impressões diagnósticas para sair junto com a impressão
				StringBuilder impDiag = new StringBuilder(18);
				Boolean primeiroImpDiag = Boolean.FALSE;
				
				if(StringUtils.equals("I", rTie.getSigla())){
					primeiroImpDiag = Boolean.TRUE;
					
					List<RegistroDiagnostico> rImt = pesquisarMamImpDiagnosticaPorSeqRegistro(rgtSeq);
					
					for (RegistroDiagnostico rImtItem : rImt) {
						if (primeiroImpDiag) {
							primeiroImpDiag = Boolean.FALSE;
							impDiag.append("\n\n         Impressão Diagnóstica:");
						}
						impDiag.append("\n              ").append(rImtItem.getDescricao());
						if (StringUtils.equals("E", rImtItem.getIndPendente())) {
							impDiag.append(" <<< EXCLUÍDO >>> ");
						}
					}
				
				}
			
				//monta a descrição do tipo				
				if (StringUtils.isNotEmpty(negaSimples.toString()) || StringUtils.isNotEmpty(negaResp.toString())
						|| StringUtils.isNotEmpty(respostas.toString()) || StringUtils.isNotEmpty(textoLivre.toString())) {
					if (tieSeq == null) {
						retorno.append(rTie.getDescricao()).append(": ");
					}
					
					retorno.append(textoLivre).append(respostas).append(negaResp).append(negaSimples);
					
					if (StringUtils.isNotEmpty(alergia.toString())) {
						retorno.append(alergia.toString());
					}

					if (StringUtils.isNotEmpty(impDiag.toString())) {
						retorno.append(impDiag.toString());
					}

					if (!StringUtils.equals("A", montaCabecalhos)) {
						retorno.append(NLNL);
					}
				} else {
					if (StringUtils.isNotEmpty(alergia.toString()) && rTie.getCategoriaProfissional().equals(categoriaProfissional)) {
						if (tieSeq == null) {
							retorno.append(rTie.getDescricao()).append(": ");
						}
						retorno.append(alergia).append(NLNL);
					}
					//se deve imprimir a impressão diagnostica
					if (StringUtils.isNotEmpty(impDiag.toString()) && rTie.getCategoriaProfissional().equals(categoriaProfissional)) {
						if (tieSeq == null) {
							retorno.append(rTie.getDescricao()).append(": ");
						}
						retorno.append(impDiag).append(NLNL);
					}
				}
		
				//se foi chamado do relatório, não pode iniciar com linha em branco ou espaços
				if (tieSeq != null) {
					String tmpRet = "";
					if (StringUtils.isNotEmpty(retorno.toString())) {
						tmpRet = StringUtil.leftTrim(retorno.toString());
					}
					while(tmpRet.startsWith("\n")){
						tmpRet = tmpRet.substring(2);
					}
					retorno = new StringBuilder();
					retorno.append(tmpRet);
				}
			//fim do loop do r_tie
			}
			
			//tela
			if(tieSeq == null){
				//MAMC_GET_IDENT_RESP
				MamEvolucoes evo = mamEvolucoesDAO.obterPorChavePrimaria(evoSeq);
				
				String rodape = atendimentoPacientesAgendadosRN.obterIdentificacaoResponsavel(null, evo);
				
				retorno.append(rodape).append('\n');
			}

			if(tieSeq == null){
				//MAMC_EMG_VIS_NEV
				String notasAdicionaisEvo = prontuarioOnlineFacade.visualizarNotaEvolucaoEMG(rgtSeq, null);
				
				if(StringUtils.isNotEmpty(notasAdicionaisEvo)){
					retorno.append('\n').append(notasAdicionaisEvo);
				}
			}
		}
		return retorno.toString();
	}
	
	public List<RegistroDiagnostico> pesquisarMamImpDiagnosticaPorSeqRegistro(Long rgtSeq){
		StringBuilder descricao = new StringBuilder();
		StringBuilder indPendente = new StringBuilder();
		List<RegistroDiagnostico> regDiagList = new ArrayList<RegistroDiagnostico>();
		
		List<MamImpDiagnostica> mamImpDiagnosticaList = getMamImpDiagnosticaDAO().pesquisarMamImpDiagnosticaPorSeqRegistro(rgtSeq);
		
		List<MamImpDiagXCid> MamImpDiagXCid = new ArrayList<MamImpDiagXCid>();
		for(MamImpDiagnostica mamImpDiagnosticaItem : mamImpDiagnosticaList){
			MamImpDiagXCid.addAll(mamImpDiagnosticaItem.getMamImpDiagXCides());
		}
		
		for(MamImpDiagXCid tempDiagCid: MamImpDiagXCid){
			descricao.append(mpmcMinusculo(tempDiagCid.getAghCid().getDescricao().toString(), 1));
			descricao.append('(');
			descricao.append(tempDiagCid.getAghCid().getCodigo().toLowerCase());
			descricao.append(')');
			if(tempDiagCid.getComplemento() != null){
				descricao.append(" - ");
				descricao.append(tempDiagCid.getComplemento());
			}
			indPendente.append(tempDiagCid.getMamImpDiagnostica().getIndPendente());
			RegistroDiagnostico regDiag = new RegistroDiagnostico();
			regDiag.setDescricao(descricao.toString());
			regDiag.setIndPendente(indPendente.toString());
			regDiagList.add(regDiag);
		}
		
		return regDiagList;
	}
	
	private class RegistroDiagnostico{
		private String descricao;
		private String indPendente;
		
		public String getDescricao() {
			return descricao;
		}
		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}
		public String getIndPendente() {
			return indPendente;
		}
		public void setIndPendente(String indPendente) {
			this.indPendente = indPendente;
		}
		
	}
	
	/**
	 * @ORADB MPMC_MINUSCULO
	 * @param pString
	 * @param pTipo
	 * @return
	 * @author fausto.santos
	 *  
	 * @since 04/06/2012
	 * 
	 * @see mpmcMinusculo("ABACAXI VERDE", 1) = "Abacaxi verde"
	 * @see mpmcMinusculo("ABACAXI VERDE", 2) = "Abacaxi Verde"
	 * 
	 */
	public String mpmcMinusculo(String pString, Integer pTipo){
		StringBuilder strRetorno = new StringBuilder();
		String[] strParts = pString.toLowerCase().split(" ");
		StringBuilder textLower = new StringBuilder();
		List<String> regex = new ArrayList<String>();
		String[] itens = {"a", "o", "e", "as","os", "é" ,"da", "do", "de", "das", "dos" , "se"};
		
		for(String tmp : itens){
			regex.add(tmp);
		}
		boolean primeiraVez = true;
		for(String tmpTextPart:strParts){
			textLower = new StringBuilder();
			textLower.append(tmpTextPart.toLowerCase());
			if((primeiraVez || pTipo == 2) && !regex.contains(textLower.toString().trim())){
				strRetorno.append(StringUtils.capitalize(textLower.toString()));
				primeiraVez = false;
			}else{
				strRetorno.append(textLower);
			}
			strRetorno.append(' ');
		}

		return strRetorno.toString().trim();
	}	
	
	/**
	 * @ORADB MAMC_EMG_VIS_TRG
	 * @param pTrgSeq
	 * @param pModoVis
	 * @return
	 * @author fausto.santos
	 * @throws ApplicationBusinessException 
	 *  
	 * @since 07/06/2012
	 */
	public StringBuilder obterEmergenciaVisTriagem(Long pTrgSeq, String pModoVis) throws ApplicationBusinessException{
		
		StringBuilder vTexto = new StringBuilder();
		
		if(("CS").equals(pModoVis)){
			vTexto.append("Triagem -----------------------------------\n\n");
		}else{
			vTexto = new StringBuilder();
		}
		//recupera todas as queixas
		List<MamTriagens> mamTriagensList = getMamTriagensDAO().listarTriagensPorSeq(pTrgSeq);
		
		for(MamTriagens rTriagen: mamTriagensList){
			obterQueixaPrincipal(pModoVis, vTexto, rTriagen);
			//recupera todos os sinais vitais
			obterSinaisVitais(pTrgSeq, vTexto);
			//recupera todos os medicamentos
			obterMedicamentos(pTrgSeq, vTexto);
			//recupera todos os exames
			obterExames(pTrgSeq, vTexto);
			
			obterInformacoesComplementares(vTexto, rTriagen);
			//recupera todos os alergias
			obterAlergias(pTrgSeq, vTexto);			
			//recupera todos os encaminhamentos externos
			obterEncaminhamentosExternos(pTrgSeq, vTexto);			
			//recupera todos os encaminhamentos ambulatório
			obterEncaminhamentosAmbulatorio(pTrgSeq, vTexto);	
			
			obterProfissional(pTrgSeq, pModoVis, vTexto, rTriagen);
		}
		return vTexto;
	}

	private void obterInformacoesComplementares(StringBuilder vTexto,
			MamTriagens rTriagen) {
		if(StringUtils.isNotEmpty(rTriagen.getInformacoesComplementares())){
			vTexto.append("Informações Complementares:\n").append(rTriagen.getInformacoesComplementares()).append(NLNL);
		}
	}

	private void obterProfissional(Long pTrgSeq, String pModoVis, StringBuilder vTexto, MamTriagens rTriagen) throws ApplicationBusinessException {
		if(("CS").equals(pModoVis) || ("BA").equals(pModoVis)){
			Object[] profElab = getPrescricaoMedicaFacade().buscaConsProf(recuperarServidorEncaminhamento(pTrgSeq));
			if (("BA").equals(pModoVis)){
				if (StringUtils.isNotEmpty(rTriagen.getQueixaPrincipal()) && !StringUtils.trim(rTriagen.getQueixaPrincipal()).equals(".")){
					vTexto.append(mpmcMinusculo(profElab[2].toString(), 2)).append(' ').append(mpmcMinusculo(profElab[3].toString(), 2));
				}
			} else {
				vTexto.append("\nElaborado por: ")
						.append(montarNomeMinusculo(profElab[1]))
						.append('-')
						.append(montarNomeMinusculo(profElab[2]))
						.append(' ')
						.append(montarNomeMinusculo(profElab[3]))
						.append(" em: ")
						.append(DateUtil
								.obterDataFormatada(
										recuperarDataHoraEncaminhamento(pTrgSeq),
										DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
						.append('\n');
			}
		}
	}

	private String montarNomeMinusculo(Object valor) {
		if (valor != null) {
			return mpmcMinusculo(valor.toString(), 2);
		}

		return "";
	}
	

	private void obterEncaminhamentosAmbulatorio(Long pTrgSeq, StringBuilder vTexto) {
		MamTrgEncAmbulatoriais mamTrgEncAmbulatoriais = getMamTrgEncAmbulatoriaisDAO().pesquisarMamTrgEncAmbulatoriaisComEspecialidade(pTrgSeq);
		if(mamTrgEncAmbulatoriais != null){
			vTexto.append("Encaminhamento ao Ambulatório:\nEspecialidade: ").append(mamTrgEncAmbulatoriais.getEspecialidade()).append('\n');
			if (StringUtils.isNotEmpty(mamTrgEncAmbulatoriais.getObservacao())) {
				vTexto.append("Observação: ").append(mamTrgEncAmbulatoriais.getObservacao()).append('\n');
			}
			vTexto.append('\n');
		}
	}

	private void obterEncaminhamentosExternos(Long pTrgSeq, StringBuilder vTexto) {
		MamTrgEncExternos mamTrgEncExternos = getMamTrgEncExternoDAO().pesquisarEncaminhamentoExternoPorSeqTriagem(pTrgSeq);
		if(mamTrgEncExternos != null){
			vTexto.append("Encaminhamento Externo:\nEspecialidade: ").append(mamTrgEncExternos.getEspecialidade())				
			.append("\nAtendimento: ").append(mamTrgEncExternos.getLocalAtendimento()).append('\n');
			if(StringUtils.isNotEmpty(mamTrgEncExternos.getObservacao())){
				vTexto.append("Observação: ").append(mamTrgEncExternos.getObservacao()).append('\n');
			}
			vTexto.append('\n');
		}
	}

	private void obterAlergias(Long pTrgSeq, StringBuilder vTexto) {
		List<MamTrgAlergias> mamTrgAlergiasList = getMamTrgAlergiasDAO().listarTrgAlergiasPorTrgSeq(pTrgSeq);
		if(mamTrgAlergiasList != null && !mamTrgAlergiasList.isEmpty()){
			vTexto.append("Alergias:\n");
			for(MamTrgAlergias rAlergia : mamTrgAlergiasList ){
				vTexto.append(rAlergia.getDescricao()).append('\n');
			}
			vTexto.append('\n');
		}
	}

	private void obterExames(Long pTrgSeq, StringBuilder vTexto) {
		List<MamTrgExames> MamTrgExamesList = getMamTrgExameDAO().pesquisarMamTrgExamesComItem(pTrgSeq);
		if(MamTrgExamesList != null && !MamTrgExamesList.isEmpty()){
			
			List<String> exames = new ArrayList<String>();				
			for(MamTrgExames rExame : MamTrgExamesList ){
				MamItemExame item = rExame.getItemExame();
				String decode = getDecodeDescricaoComplemento(item.getDescricao(), rExame.getComplemento());
				if (StringUtils.isNotBlank(decode)){
					exames.add(decode);
				}
			}
			if (exames.size() > 0) {
				vTexto.append("Exames:\n");
				ordenarArrayStrings(vTexto, exames);
				vTexto.append('\n');
			}
		}
	}

	private void obterMedicamentos(Long pTrgSeq, StringBuilder vTexto) {
		List<MamTrgMedicacoes> mamTrgMedicacoesList = getMamTrgMedicacoesDAO().pesquisarMamTrgMedicacoesComItem(pTrgSeq);
		if(mamTrgMedicacoesList != null && !mamTrgMedicacoesList.isEmpty()){
			
			List<String> medicamentos = new ArrayList<String>();				
			for(MamTrgMedicacoes rMed : mamTrgMedicacoesList ){
				String decode = getDecodeDescricaoComplemento(rMed.getItemMedicacao().getDescricao(), rMed.getComplemento());
				if (StringUtils.isNotBlank(decode)){
					medicamentos.add(decode);
				}
			}
			if (medicamentos.size() > 0) {
				vTexto.append("Medicamentos:\n");
				ordenarArrayStrings(vTexto, medicamentos);
				vTexto.append('\n');
			}
		}
	}

	private void obterSinaisVitais(Long pTrgSeq, StringBuilder vTexto) {
		List<MamTrgSinalVital> mamTrgSinalVitalList = getMamTrgSinalVitalDAO().pesquisarMamTrgSinalVitalComItem(pTrgSeq);
		if(mamTrgSinalVitalList != null && !mamTrgSinalVitalList.isEmpty()){  

			List<String> sinais = new ArrayList<String>();
			for(MamTrgSinalVital rSinal : mamTrgSinalVitalList ){
				String decode = getDecodeDescricaoComplemento(rSinal.getMamItemSinalVital().getDescricao(), rSinal.getComplemento());
				if (StringUtils.isNotBlank(decode)){
					sinais.add(decode);
				}
			}
			if (sinais.size() > 0) {
				vTexto.append("Sinais Vitais:\n");
				ordenarArrayStrings(vTexto, sinais);
				vTexto.append('\n');
			}
		}
	}

	private void obterQueixaPrincipal(String pModoVis, StringBuilder vTexto,
			MamTriagens rTriagen) {
		if (("BA").equals(pModoVis)) {
			if (StringUtils.isNotEmpty(rTriagen.getQueixaPrincipal()) && !StringUtils.trim(rTriagen.getQueixaPrincipal()).equals(".")) {
				vTexto.append("Queixa Principal:\n").append(rTriagen.getQueixaPrincipal()).append(NLNL);
			}
		} else {
			vTexto.append("Queixa Principal:\n").append(rTriagen.getQueixaPrincipal()).append(NLNL);
		}
	}

	private void ordenarArrayStrings(StringBuilder vTexto, List<String> aStrings) {
		Arrays.sort(aStrings.toArray());
		for (String str : aStrings) {
			vTexto.append(str).append('\n');					
		}
	}
	
	private String getDecodeDescricaoComplemento(String descricao, String complemento){
		StringBuilder retorno = new StringBuilder();
		if(StringUtils.isBlank(descricao) && StringUtils.isNotBlank(complemento)){
			retorno.append(complemento);
		} else if (StringUtils.isNotBlank(descricao) && StringUtils.isNotBlank(complemento)){
			retorno.append(descricao).append(" - ").append(complemento);
		}
		return retorno.toString();
	}

	protected MamQuestaoDAO getMamQuestaoDAO(){
		return mamQuestaoDAO;
	}
	
	protected MamValorValidoQuestaoDAO getMamValorValidoQuestaoDAO(){
		return mamValorValidoQuestaoDAO;
	}
	
	protected MamRespostaAnamnesesDAO getMamRespostaAnamnesesDAO(){
		return mamRespostaAnamnesesDAO;
	}	
	
	protected MamEvolucoesDAO getMamEvolucoesDAO(){
		return mamEvolucoesDAO;
	}
	
	protected MamTipoItemAnamnesesDAO getMamTipoItemAnamnesesDAO(){
		return mamTipoItemAnamnesesDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return this.prontuarioOnlineFacade;
	}
	
	protected MamCustomQuestaoDAO getMamCustomQuestaoDAO(){
		return mamCustomQuestaoDAO;
	}
	
	protected MamItemEvolucoesDAO getMamItemEvolucaoDAO(){
		return mamItemEvolucoesDAO;
	}
	
	protected MamRespostaEvolucoesDAO getMamRespostaEvolucoesDAO(){
		return mamRespostaEvolucoesDAO;
	}
	
	protected MamImpDiagnosticaDAO getMamImpDiagnosticaDAO(){
		return mamImpDiagnosticaDAO;
	}

	protected MamTrgEncAmbulatoriaisDAO getMamTrgEncAmbulatoriaisDAO() {
		return mamTrgEncAmbulatoriaisDAO;
	}

	protected MamTrgEncExternoDAO getMamTrgEncExternoDAO() {
		return mamTrgEncExternoDAO;
	}

	protected MamTrgEncInternoDAO getMamTrgEncInternoDAO() {
		return mamTrgEncInternoDAO;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.prescricaoMedicaFacade;
	}
	
	protected MamTriagensDAO getMamTriagensDAO(){
		return mamTriagensDAO;
	}
	
	protected MamTrgSinalVitalDAO getMamTrgSinalVitalDAO(){
		return mamTrgSinalVitalDAO;
	}
	
	protected MamTrgMedicacoesDAO getMamTrgMedicacoesDAO(){
		return mamTrgMedicacoesDAO;
	}
	
	protected MamTrgAlergiasDAO getMamTrgAlergiasDAO(){
		return mamTrgAlergiasDAO;
	}
	
	protected MamTrgExameDAO getMamTrgExameDAO(){
		return mamTrgExameDAO;
	}	
	
	protected MamItemExameDAO getMamItemExameDAO(){
		return mamItemExameDAO;
	}	
	
	protected MamItemAnamnesesDAO getMamItemAnamnesesDAO(){
		return mamItemAnamnesesDAO;
	}
	
	protected ICascaFacade getICascaFacade(){
		return this.cascaFacade;
	}
	
	protected MamAnamnesesDAO getMamAnamnesesDAO(){
		return mamAnamnesesDAO;
	}
	
	/**
	 * @ORADB MAMP_EMG_GET_DH_ENC
	 * Retorna a data e hora do encaminhamento
	 * @param trgSeq
	 * @return 
	 * @since 06/06/2012
	 */
	public Date recuperarDataHoraEncaminhamento(Long trgSeq) {
		MamTrgEncExternos trgEncExternos = getMamTrgEncExternoDAO().pesquisarEncaminhamentoExternoPorSeqTriagem(trgSeq);
		MamTrgEncInterno trgEncInterno = getMamTrgEncInternoDAO().pesquisarEncaminhamentoInternoPorSeqTriagem(trgSeq);
		MamTrgEncAmbulatoriais trgEncAmbulatoriais = getMamTrgEncAmbulatoriaisDAO().pesquisarEncaminhamentoAmbulatoriaisPorSeqTriagem(trgSeq);
		
		Date criadoEm = null;
		
		if (trgEncInterno != null && trgEncInterno.getCriadoEm() != null && criadoEm == null) {
			criadoEm = trgEncInterno.getCriadoEm();
		} else if (trgEncExternos != null && trgEncExternos.getCriadoEm() != null && criadoEm == null) {
			criadoEm = trgEncExternos.getCriadoEm();
		} else if (trgEncAmbulatoriais != null && trgEncAmbulatoriais.getCriadoEm() != null && criadoEm == null) {
			criadoEm = trgEncAmbulatoriais.getCriadoEm();
		}
		
		return criadoEm;
	}

	public MamAlergiasDAO getMamAlergiasDAO(){
		return mamAlergiasDAO;
	}
	
	
	/**
	 * @ORADB MAMC_EMG_VIS_ALG
	 * @param Seq
	 * @return DESCRICAO ALERGIA EXCLUIDA
	 * @author leandro.martins
	 * @since 06/06/2012
	 */
	public String pesquisarMamAlergiasPorSeq(Long seq) {
		
		StringBuilder alergiaPendente = new StringBuilder(20);
		MamAlergias alergias = getMamAlergiasDAO().obterOriginal(seq);
		
		if(alergias.getIndPendente().equals("E")){
			alergiaPendente.append("\nALERGIA EXCLUÍDA");
		}
		
		
		return alergiaPendente.toString();
	}
	
	/**
	 * @ORADB MAMP_EMG_GET_RES_ENC
	 * Retorna o responsavel do encaminhamento
	 * @param trgSeq
	 * @return 
	 * @since 06/06/2012
	 */
	public RapServidores recuperarServidorEncaminhamento(Long trgSeq) {
		MamTrgEncExternos trgEncExternos = getMamTrgEncExternoDAO().pesquisarEncaminhamentoExternoPorSeqTriagem(trgSeq);
		MamTrgEncInterno trgEncInterno = getMamTrgEncInternoDAO().pesquisarEncaminhamentoInternoPorSeqTriagem(trgSeq);
		MamTrgEncAmbulatoriais trgEncAmbulatoriais = getMamTrgEncAmbulatoriaisDAO().pesquisarEncaminhamentoAmbulatoriaisPorSeqTriagem(trgSeq);
		
		RapServidores servidor = null;
		
		if (trgEncInterno != null && trgEncInterno.getServidor() != null && servidor == null) {
			servidor = trgEncInterno.getServidor();
		} else if (trgEncExternos != null && trgEncExternos.getServidor() != null && servidor == null) {
			servidor = trgEncExternos.getServidor();
		} else if (trgEncAmbulatoriais != null && trgEncAmbulatoriais.getServidor() != null && servidor == null) {
			servidor = trgEncAmbulatoriais.getServidor();
		}
		
		return servidor;
	}
	
	/**
	 * @ORADB mpmc_ida_mes_dia_ref
	 * Retorna a idade
	 * 	Se ANOS > zero,
	 * 		retorna somente anos e meses;
	 * 	senao,
	 * 		meses e dias;
	 * 	fim se;
	 * 
	 * if dtReferencia não informado, considerar data atual.
	 * @param dtNascimento
	 * @param dtReferencia 
	 */
	public String obterIdadeMesDias(Date dtNascimento, Date dtReferencia) {
		StringBuilder sb = new StringBuilder();
		
		if (dtReferencia == null) {
			dtReferencia = new Date(); 
		}
		Integer anos = DateUtil.obterQtdAnosEntreDuasDatas(DateUtil.truncaData(dtNascimento), DateUtil.truncaData(dtReferencia));
		
		if (anos < 0) {
			anos = 0;
		}
		
		Integer meses = (DateUtil.obterQtdMesesEntreDuasDatas(DateUtil.truncaData(dtNascimento), DateUtil.truncaData(dtReferencia)) - (anos * 12));
		Integer dias = (DateUtil.obterQtdDiasEntreDuasDatas(DateUtil.adicionaMeses(dtNascimento, ((anos * 12) + meses)), dtReferencia));
		
		if (anos > 1) {
			sb.append(anos).append(" anos ");
		} else if (anos == 1) {
			sb.append(anos).append(" ano ");
		}
		
		if (meses > 1) {
			sb.append(meses).append(" meses ");
		} else if (meses == 1) {
			sb.append(meses).append(" mês ");
		}
		
		if (anos <= 0) {
			if (dias > 1) {
				sb.append(dias).append(" dias");
			} else if (dias == 1) {
				sb.append(dias).append(" dia");
			}
		}
		
		return sb.toString();		
	}
	
	public String getGrupoProfissional(Long pAnaSeq, AghParametros parametroMed, AghParametros parametroEnf, AghParametros parametroNut)throws ApplicationBusinessException {
		RapServidores servidorValida = null;
		StringBuilder grupo = new StringBuilder("Outros Profissionais de Saúde");
		MamAnamneses mamAnamnese = getMamAnamnesesDAO().obterPorChavePrimaria(pAnaSeq);
		
		if(mamAnamnese != null){
			servidorValida = mamAnamnese.getServidorValida();
		}
		
		if(servidorValida != null){
			Set<String> nomePerfisServidor = getICascaFacade().obterNomePerfisPorUsuario(servidorValida.getUsuario());
			
			String[] aghParamMedList = parametroMed.getVlrTexto().split(",");
			String[] aghParamEnfList = parametroEnf.getVlrTexto().split(",");
			String[] aghParamNutList = parametroNut.getVlrTexto().split(",");
			
			
				for(int i = 0; i < aghParamMedList.length; i++){
					if(	nomePerfisServidor.contains(aghParamMedList[i])){
						grupo = new StringBuilder().append(parametroMed.getDescricao());
						return grupo.toString();
					}
				}
				
				for(int i = 0; i < aghParamEnfList.length; i++){
					if(	nomePerfisServidor.contains(aghParamEnfList[i])){
						grupo = new StringBuilder().append(parametroEnf.getDescricao());
						return grupo.toString();
					}
				}
				
				for(int i = 0; i < aghParamNutList.length; i++){
					if(	nomePerfisServidor.contains(aghParamNutList[i])){
						grupo = new StringBuilder().append(parametroNut.getDescricao());
						return grupo.toString();
					}
				}
				
		}
				
		return	grupo.toString();
		
	}

	protected AtendimentoPacientesAgendadosRN getAtendimentoPacientesAgendadosRN() {
		return atendimentoPacientesAgendadosRN;
	}

	protected MamTipoItemEvolucaoDAO getMamTipoItemEvolucaoDAO() {
		return mamTipoItemEvolucaoDAO;
	}
	
}
