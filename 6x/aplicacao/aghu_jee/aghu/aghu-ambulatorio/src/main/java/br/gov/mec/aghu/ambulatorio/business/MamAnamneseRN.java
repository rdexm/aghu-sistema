package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamConcatenacaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamCustomQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamQuestionarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTipoItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.vo.AnamneseItemTipoItemVO;
import br.gov.mec.aghu.ambulatorio.vo.QuestionarioRespostaAnamneseVO;
import br.gov.mec.aghu.ambulatorio.vo.ResponsavelAnamneseVO;
import br.gov.mec.aghu.casca.dao.CseCategoriaProfissionalDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.emergencia.dao.MamEmgServidorDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamConcatenacao;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RarProgramaDAO;
import br.gov.mec.aghu.registrocolaborador.vo.ProgramaEspecialidadeVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Classe responsável por manter as regras de negócio da entidade MamAnamnese.
 *
 */
@Stateless
public class MamAnamneseRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2498132105782677378L;

	private static final Log LOG = LogFactory.getLog(MamAnamneseRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private CseCategoriaProfissionalDAO cseCategoriaProfissionalDAO;

	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject 
	private MamTipoItemAnamnesesDAO mamTipoItemAnamnesesDAO;
	
	@Inject 
	private MamQuestionarioDAO mamQuestionarioDAO;
	
	@Inject 
	private MamConcatenacaoDAO mamConcatenacaoDAO;
	
	@Inject 
	private MamCustomQuestaoDAO mamCustomQuestaoDAO;
	
	@Inject
	private RelatorioAnaEvoInternacaoRN relatorioAnaEvoInternacaoRN;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AtendimentoPacientesAgendadosRN atendimentoPacientesAgendadosRN;
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@Inject
	private RarProgramaDAO rarProgramaDAO;
	
	@Inject 
	private MamEmgServidorDAO mamEmgServidorDAO;
	
	private ResponsavelAnamneseVO responsavelAnamneseVO = null;
	private MamAnamneses mamAnamneses = null;
	private MamEvolucoes mamEvolucoes = null;
	private String usaAssUnificada = "N";
	
	private Object profElaborou [] = null, profValida [] = null;

	private String nomeElaborou = null;
	private String nomeValida = null;

	private ProgramaEspecialidadeVO servidorContratadoElaborou = null, servidorResidenteElaborou = null, servidorContratadoValidou = null, servidorResidenteValidou = null;
	
	private Boolean concatenado = Boolean.FALSE;
	private Boolean primeiraVez = Boolean.TRUE;
	private String caracter = StringUtils.SPACE;
	
	private static final String PONTO = ".";

	@Override
	protected Log getLogger() {

		return LOG;
	}

	/**
	 * @ORADB mamk_int_visualiza.mamp_int_v_reg_ana
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String obterAnamnesePorMamRegistroSeq(Long rgtSeq, Integer categoriaProfSeq) throws ApplicationBusinessException {

		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		CseCategoriaProfissional cseCategoriaProfissional = null;
		List<CseCategoriaProfissional> listCategoriaProfissional = cseCategoriaProfissionalDAO.pesquisarCategoriaProfissional(servidorLogado);
		List<MamAnamneses> listMamAnamneses = null;

		if (listCategoriaProfissional != null
				&& !listCategoriaProfissional.isEmpty()) {
			cseCategoriaProfissional = listCategoriaProfissional.get(0);
		}

		// mamc_get_cat_profis(ser_matricula_valida, ser_vin_codigo_valida) =
		// c_cat_prof or c_cat_prof is null
		if (listCategoriaProfissional != null
				&& (categoriaProfSeq == null || cseCategoriaProfissional
						.getSeq().equals(categoriaProfSeq))) {
			listMamAnamneses = mamAnamnesesDAO.obterAnamnesePorRgtSeqIndPendentePV(rgtSeq);
		}

		String p_chamada = "E";
		String p_documento = "T";
		String p_modo_vis = "CS";

		String anamnese = null; // v_anamnese (Long ou String)
		StringBuffer anamneseSB = new StringBuffer();
		if (p_documento.equals("A") || p_documento.equals("T")) {

			boolean existe = Boolean.FALSE;
			Date dthrValida = null;
			for (MamAnamneses mamAnamneses : listMamAnamneses) {
				if ((p_chamada.equals("P") && mamAnamneses.getPendente().equals(DominioIndPendenteAmbulatorio.V)) || !p_chamada.equals("P")) {

					dthrValida = mamAnamneses.getDthrValida();
					existe = Boolean.TRUE;

					// mamk_int_visualiza.mamp_int_vis_ana_edu
					// (r_ana.seq, 'N', null, 'N', v_anamnese);
					anamnese = montarDescricaoAnamnese(mamAnamneses.getSeq(), false, null, false);
				}
			}

			if (existe) {
				// if p_modo_vis = 'CS' then -- modo de visualização com
				// separador
				if (p_modo_vis.equals("CS")) {
					if (anamnese != null) {
						if (dthrValida != null) {

							// v_anamnese := '---
							// '||to_char(v_dthr_valida,'dd/mm/yyyy hh24:mi')
							// ||'
							// ---'||chr(10)|| v_anamnese || chr(10);
							
							String formatoData = "dd/MM/yyyy HH:mm"; // "dd/mm/yyyy hh24:mi"
							anamneseSB.append("--- ");
							anamneseSB.append(DateUtil.obterDataFormatada(dthrValida, formatoData));
							anamneseSB.append(" ---\n");
							anamneseSB.append(anamnese);
							anamneseSB.append(" \n");
							
						} else {
							anamneseSB.append(anamnese);
							anamneseSB.append(" \n");
						}

					}
				} else {
					// v_anamnese :=
					// 'Anamnese:'||chr(10)||chr(10)||v_anamnese||chr(10);
					anamneseSB.append("Anamnese:\n\n");
					anamneseSB.append(anamnese);
					anamneseSB.append(" \n");
				}
			} else {
				anamneseSB = null;
			}
		}
		
		if (anamneseSB != null) {
			return anamneseSB.toString() + "\n";
		}
		
		return StringUtils.EMPTY;
	}
	
	/**	  
	 * #50973
	 * @ORADB mamp_int_vis_ana
	 * @param anaSeq
	 * @param mostraNaoInfo
	 * @param tinSeq
	 * @param montaCabecalho
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String montarDescricaoAnamnese(Long anaSeq, Boolean mostraNaoInf, Integer tinSeq, Boolean montaCabecalho) throws ApplicationBusinessException{
		String vNegaSimples = StringUtils.EMPTY;
		String vNegaResp = StringUtils.EMPTY;
		String vRespostas = StringUtils.EMPTY;
		String vTextoLivres = StringUtils.EMPTY;
		
		concatenado = Boolean.FALSE;
		primeiraVez = Boolean.TRUE;
		caracter = StringUtils.SPACE;
		
		StringBuffer retornoAnamnese = new StringBuffer();
		DominioIndPendenteAmbulatorio indPendente = null; //TODO

		if(anaSeq != null){

			List<MamTipoItemAnamneses> mamTipoItemAnamnesesLista = mamTipoItemAnamnesesDAO.buscaTipoItemAnamneseAtivoOrdenado(tinSeq);
			
			for(MamTipoItemAnamneses mamTipoItemAnamneses : mamTipoItemAnamnesesLista){
				
				/* Verifica se houve negações simples sem resposta */
				vNegaSimples = obterNegacaoSimplesSemResposta(anaSeq, mamTipoItemAnamneses.getSeq());
				
				/* Verifica se houve negações com resposta */
				vNegaResp = obterNegacaoComResposta(anaSeq, mamTipoItemAnamneses.getSeq());
				
				/* Verifica as respostas sem negação e sem concatenação */
				vRespostas = obterRespostasSemNegacaoConcatenacao(anaSeq, mamTipoItemAnamneses.getSeq(), mostraNaoInf);
				
				/* Textos livres */
				vTextoLivres = obterTextosLivres(anaSeq, mamTipoItemAnamneses.getSeq(), mostraNaoInf, StringUtils.isBlank(vRespostas));
				
				if(StringUtils.isNotBlank(vNegaSimples) || StringUtils.isNotBlank(vNegaResp) || StringUtils.isNotBlank(vRespostas) || StringUtils.isNotBlank(vTextoLivres)){
					if(tinSeq == null){
						retornoAnamnese.append(mamTipoItemAnamneses.getDescricao());
						retornoAnamnese.append(": ");
					}
					
					retornoAnamnese.append(vTextoLivres);
					retornoAnamnese.append(vRespostas);
					retornoAnamnese.append(vNegaResp);
					retornoAnamnese.append(vNegaSimples);
					retornoAnamnese.append(StringUtils.LF);
					retornoAnamnese.append(StringUtils.LF);
				}
				
				if(tinSeq != null){
					if(retornoAnamnese.length() > 0){
						retornoAnamnese = new StringBuffer(StringUtil.leftTrim(retornoAnamnese.toString())); 
						
						while(retornoAnamnese.toString().startsWith("\n")){
							String aux = retornoAnamnese.substring(2);
							retornoAnamnese = new StringBuffer(aux);
						}
					}
				}				
			}
			
			if(tinSeq == null){
				String vRodape = obterIdentificacaoResponsavelAnaEvo(anaSeq, null);
				
				if(StringUtils.isNotBlank(vRodape)){
					retornoAnamnese.append(vRodape);
					retornoAnamnese.append(StringUtils.LF);
				}
				
				String vNotasAdicionais = prontuarioOnlineFacade.visualizarNotaAnamneseEMG(mamAnamnesesDAO.obterRgtSeqPorAnaSeq(anaSeq), null);
				
				if(StringUtils.isNotBlank(vNotasAdicionais)){
					retornoAnamnese.append(StringUtils.LF);
					retornoAnamnese.append(vNotasAdicionais);
				}
			}
			
			if(DominioIndPendenteAmbulatorio.E.equals(indPendente)){
				retornoAnamnese.append(StringUtils.LF);
				retornoAnamnese.append(StringUtils.LF);
				retornoAnamnese.append(" <<< ANAMNESE EXCLUÍDA >>>");
			}
		}
		
		return retornoAnamnese.toString();
	}
	
	/**
	 * #50973
	 * Verifica se houve negações simples sem resposta
	 * @param anaSeq
	 * @param tinSeq
	 * @return
	 */
	private String obterNegacaoSimplesSemResposta(Long anaSeq, Integer tinSeq){
		String valorAnterior = StringUtils.EMPTY;
		
		StringBuffer respostaBuffer = new StringBuffer();
		
		List<QuestionarioRespostaAnamneseVO> questionarioRespostaLista = mamQuestionarioDAO.obterNegaSimplesPorAnaSeqTinSeq(anaSeq, tinSeq);
		
		for(QuestionarioRespostaAnamneseVO resposta : questionarioRespostaLista){
			
			if(!valorAnterior.equalsIgnoreCase(resposta.getValorValidoQuestao())){
				valorAnterior = resposta.getValorValidoQuestao();
				respostaBuffer.append(StringUtils.LF);
				respostaBuffer.append(resposta.getValorValidoQuestao());
				respostaBuffer.append(": ");
				respostaBuffer.append(StringUtils.lowerCase(resposta.getDescricao())); 
			}else{
				respostaBuffer.append(", ");
				respostaBuffer.append(StringUtils.lowerCase(resposta.getDescricao()));
			}
		}
		
		return respostaBuffer.toString();
	}
	
	/**
	 * #50937
	 * Verifica se houve negações com resposta
	 * @param anaSeq
	 * @param tinSeq
	 * @return
	 */
	private String obterNegacaoComResposta(Long anaSeq, Integer tinSeq){
		StringBuffer respostaBuffer = new StringBuffer();
		
		List<QuestionarioRespostaAnamneseVO> questionarioRespostaLista = mamQuestionarioDAO.obterNegaComRespostaPorAnaSeqTinSeq(anaSeq, tinSeq);
		
		for(QuestionarioRespostaAnamneseVO resposta : questionarioRespostaLista){
				respostaBuffer.append(StringUtils.LF);
				respostaBuffer.append(resposta.getValorValidoQuestao() + ": " + StringUtils.lowerCase(resposta.getDescricao())+ ": " + resposta.getResposta() + ".");
		}
		
		return respostaBuffer.toString();
	}
	
	/**
	 * @param anaSeq
	 * @param tinSeq
	 * @param mostraNaoInf
	 * @return
	 */
	private String obterRespostasSemNegacaoConcatenacao(Long anaSeq, Integer tinSeq, Boolean mostraNaoInf){
		StringBuffer respostaBuffer = new StringBuffer();
		
		StringBuffer textoAntesRespostaBuffer = new StringBuffer();
		StringBuffer textoDepoisRespostaBuffer = new StringBuffer();
		
		List<QuestionarioRespostaAnamneseVO> questionarioRespostaLista = mamQuestionarioDAO.obterRespostaPorAnaSeqTinSeq(anaSeq, tinSeq);
		
		for(QuestionarioRespostaAnamneseVO resposta : questionarioRespostaLista){
			
			if(resposta.getTextoAntesResposta() != null){
				textoAntesRespostaBuffer.append(StringUtils.SPACE);
				textoAntesRespostaBuffer.append(resposta.getTextoAntesResposta());
			}else{
				textoAntesRespostaBuffer = new StringBuffer();
			}
			
			if(resposta.getTextoDepoisResposta() != null){
				textoDepoisRespostaBuffer.append(StringUtils.SPACE);
				textoDepoisRespostaBuffer.append(resposta.getTextoDepoisResposta());
			}else{
				textoDepoisRespostaBuffer = new StringBuffer();
			}
			
			if(primeiraVez){
				primeiraVez = Boolean.FALSE;
				
				if(respostaBuffer.length() > 0){
					respostaBuffer.append(StringUtils.LF);
				}
			}
			
			/* verifica se a questão está concatenada */
			respostaBuffer.append(obterQuestaoConcatenada(resposta, textoAntesRespostaBuffer.toString(), textoDepoisRespostaBuffer.toString()));
			
			/* verifica a customização da questão */
			respostaBuffer.append(obterCustomizacaoQuestao(resposta, anaSeq, mostraNaoInf, tinSeq));
		}
		return respostaBuffer.toString();
	}
	
	/**
	 * #50937
	 * @param anaSeq
	 * @param tinSeq
	 * @param mostraNaoInf
	 * @param temResposta
	 * @return
	 */
	private String obterTextosLivres(Long anaSeq, Integer tinSeq, Boolean mostraNaoInf, Boolean temResposta){
		StringBuffer textoLivreBuffer = new StringBuffer();
		
		List<AnamneseItemTipoItemVO> anaTipoItemLista = mamTipoItemAnamnesesDAO.obterTextoLivresPorAnaSeqTinSeq(anaSeq, tinSeq);
		
		for(AnamneseItemTipoItemVO anaTipoItem : anaTipoItemLista){
			
			if(anaTipoItem.getPermiteLivre()){
				
				if(anaTipoItem.getPermiteQuest() || anaTipoItem.getPermiteFigura() || anaTipoItem.getIdentificacao()){
					
					if(anaTipoItem.getDescricao() != null){
						textoLivreBuffer.append(StringUtils.LF);
						textoLivreBuffer.append(anaTipoItem.getDescricao());
					}else{
						if(mostraNaoInf && !temResposta){
							textoLivreBuffer.append(StringUtils.LF);
							textoLivreBuffer.append("Não Informado");
						}
					}
				}
				
				if(!anaTipoItem.getPermiteQuest() && !anaTipoItem.getPermiteFigura() && !anaTipoItem.getIdentificacao()){
					
					if(anaTipoItem.getDescricao() != null){
						textoLivreBuffer.append(StringUtils.LF);
						textoLivreBuffer.append(anaTipoItem.getDescricao());
						textoLivreBuffer.append(PONTO);
					}else{
						if(mostraNaoInf && !temResposta){
							textoLivreBuffer.append(StringUtils.LF);
							textoLivreBuffer.append("Não Informado");
						}
					}
				}else{
					if(anaTipoItem.getIdentificacao()){
						if(anaTipoItem.getDescricao() != null){
							textoLivreBuffer.append(StringUtils.LF);
							textoLivreBuffer.append(anaTipoItem.getDescricao());
							textoLivreBuffer.append(PONTO);
						}						
					}
				}
			}			
		}
		return textoLivreBuffer.toString();
	}
	
	/**
	 * #50937
	 * verifica se a questão está concatenada
	 * @param resposta
	 * @param textoAntesResposta
	 * @param textoDepoisResposta
	 * @return
	 */
	private String obterQuestaoConcatenada(QuestionarioRespostaAnamneseVO resposta, String textoAntesResposta, String textoDepoisResposta){
		StringBuffer respostaBuffer = new StringBuffer();
				
		MamConcatenacao mamConcatenacao = mamConcatenacaoDAO.obterConcatenacaoAtivaPorIdQuestao(resposta.getQutSeq(), resposta.getSeqpQuestao());
		
		if(mamConcatenacao != null){
			concatenado = Boolean.TRUE;
			caracter = mamConcatenacao.getCaracter();
		}
		
		if((resposta.getResposta() != null && resposta.getTextoFormatado() == null) || 
		   (resposta.getResposta() != null && resposta.getTextoFormatado() != null && !StringUtils.equalsIgnoreCase(resposta.getResposta(), resposta.getTextoFormatado()))){
			
			if(!concatenado){
				if(!mamConcatenacaoDAO.existeConcatenacaoPorIdQuestao(resposta.getQutSeq(), resposta.getSeqpQuestao())){
					respostaBuffer.append(StringUtils.LF);
					respostaBuffer.append(resposta.getDescricao());
					respostaBuffer.append(": ");
				}else{
					respostaBuffer.append(StringUtils.LF);
				}
				
				if(resposta.getTextoAntesResposta() != null){
					respostaBuffer.append(resposta.getTextoAntesResposta());
					respostaBuffer.append(StringUtils.SPACE);
				}
				
				respostaBuffer.append(resposta.getResposta());	
				
				if(resposta.getTextoDepoisResposta() != null){
					respostaBuffer.append(StringUtils.SPACE);
					respostaBuffer.append(resposta.getTextoDepoisResposta());
				}
				
			}else{
				respostaBuffer.append(caracter);
				respostaBuffer.append(textoAntesResposta);
				respostaBuffer.append(StringUtils.SPACE);
				respostaBuffer.append(resposta.getResposta());
				respostaBuffer.append(textoDepoisResposta);
				
			}
		}
		return respostaBuffer.toString();
	}
	
	/**
	 * #50937
	 * verifica a customização da questão
	 * @param resposta
	 * @return
	 */
	private String obterCustomizacaoQuestao(QuestionarioRespostaAnamneseVO resposta, Long anaSeq, Boolean mostraNaoInf, Integer tinSeq){
		StringBuffer respostaBuffer = new StringBuffer();
		
		Boolean temCustomizacao = mamCustomQuestaoDAO.temCustomizacaoQuestao(resposta.getQutSeq(), resposta.getSeqpQuestao());
		
		if(temCustomizacao){
			String customizacao = relatorioAnaEvoInternacaoRN.obterCustomAnamnese(anaSeq, mostraNaoInf, tinSeq, resposta.getQutSeq(), resposta.getSeqpQuestao());
			
			if(StringUtils.isNotBlank(customizacao)){
				respostaBuffer.append(StringUtils.LF);
				respostaBuffer.append(customizacao);
			}		
		
			if(mostraNaoInf && ((resposta.getResposta() == null) || 
					(resposta.getResposta() != null && resposta.getTextoFormatado() != null && StringUtils.equalsIgnoreCase(resposta.getResposta(), resposta.getTextoFormatado())) )){
				
				if(!concatenado){
					respostaBuffer.append(StringUtils.LF);
					respostaBuffer.append(resposta.getDescricao());
					respostaBuffer.append(": Não Informado");
				}else{
					respostaBuffer.append(caracter);
					respostaBuffer.append(StringUtils.SPACE);
					respostaBuffer.append(resposta.getDescricao());
					respostaBuffer.append(": Não Informado");
				}				
			}
		}
		return respostaBuffer.toString();
	}
	
	/**
	 * #50937
	 * @ORADB mamk_int_visualiza.mamc_int_ident_resp
	 * @param anaSeq
	 * @param evoSeq
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String obterIdentificacaoResponsavelAnaEvo(Long anaSeq, Long evoSeq) throws ApplicationBusinessException{
		StringBuffer retornoIdentificacao = new StringBuffer();
		
		AghParametros aghParametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_USA_ASS_UNIFICADA);
		
		if(aghParametro != null){
			usaAssUnificada = aghParametro.getVlrTexto();
		}
		
		if(usaAssUnificada.equalsIgnoreCase("S")){
			if(anaSeq != null){
				mamAnamneses = mamAnamnesesDAO.obterPorChavePrimaria(anaSeq);
				retornoIdentificacao.append(atendimentoPacientesAgendadosRN.obterAssinaturaTexto(mamAnamneses, null, null, null));
			}
			
			if(evoSeq != null){
				mamEvolucoes = mamEvolucoesDAO.obterPorChavePrimaria(evoSeq);
				retornoIdentificacao.append(atendimentoPacientesAgendadosRN.obterAssinaturaTexto(null, mamEvolucoes, null, null));
			}
		}else{
			
			if(anaSeq != null){
				responsavelAnamneseVO = mamAnamnesesDAO.obterResponsavelAnamnesePorSeq(anaSeq);
			}else if(evoSeq != null){
				responsavelAnamneseVO = mamEvolucoesDAO.obterEvolucaoResponsavelPorSeq(evoSeq);
			}
			
			if(responsavelAnamneseVO != null && responsavelAnamneseVO.getMatricula() != null && responsavelAnamneseVO.getVinculo() != null){
			    
			    //Busca nome profissional que elaborou
				profElaborou = prescricaoMedicaFacade.buscaConsProf(responsavelAnamneseVO.getMatricula(), responsavelAnamneseVO.getVinculo());
				
				nomeElaborou = nomeProfissional(profElaborou);
				
				//Verifica se é contratado
				servidorContratadoElaborou = mamEmgServidorDAO.obterNomeEspecialidadeServidorContratado(responsavelAnamneseVO.getMatricula(), responsavelAnamneseVO.getVinculo());
				
			    //Se não é contratado verifica se é residente
				if(servidorContratadoElaborou == null){
					servidorResidenteElaborou = rarProgramaDAO.obterNomeProgramaNomeEspecialidade(responsavelAnamneseVO.getMatricula(), responsavelAnamneseVO.getVinculo());
				}
			}
			
			if(responsavelAnamneseVO != null && responsavelAnamneseVO.getMatriculaValida() != null && responsavelAnamneseVO.getVinculoValida() != null){
				
				 //Busca nome profissional que validou
				profValida = prescricaoMedicaFacade.buscaConsProf(responsavelAnamneseVO.getMatricula(), responsavelAnamneseVO.getVinculo());
				
				nomeValida = nomeProfissional(profValida);
				
				//Verifica se é contratado
				servidorContratadoValidou = mamEmgServidorDAO.obterNomeEspecialidadeServidorContratado(responsavelAnamneseVO.getMatricula(), responsavelAnamneseVO.getVinculo());
				
			    //Se não é contratado verifica se é residente
				if(servidorContratadoValidou == null){
					servidorResidenteValidou = rarProgramaDAO.obterNomeProgramaNomeEspecialidade(responsavelAnamneseVO.getMatricula(), responsavelAnamneseVO.getVinculo());
				}
			}

			if(StringUtils.equalsIgnoreCase(nomeElaborou, nomeValida)){
				profissionalElaborouIgualValidou(retornoIdentificacao);
			}else{
				if(nomeElaborou != null){
					nomeElaborouNotNull(retornoIdentificacao);
				}
				
				if(nomeValida != null){
					nomeValidaNotNull(retornoIdentificacao);
				}
			}
		}
		return retornoIdentificacao.toString();
	}
	
	private void profissionalElaborouIgualValidou(StringBuffer retornoIdentificacao){
	
		if(servidorContratadoValidou != null && servidorContratadoValidou.getNomeEspecialidade() != null){
			retornoIdentificacao.append("Elaborado e assinado por médico contratado "); 
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeValida, 2));
			retornoIdentificacao.append(StringUtils.SPACE);
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(servidorContratadoValidou.getNomeEspecialidade(), 2));
		
		}else if(servidorResidenteValidou != null && servidorResidenteValidou.getNomeEspecialidade() != null){
			retornoIdentificacao.append("Elaborado e assinado por médico residente "); 
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeValida, 2));
			retornoIdentificacao.append(StringUtils.SPACE);
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(servidorResidenteValidou.getNomeEspecialidade(), 2));
		
		}else{
			retornoIdentificacao.append("Elaborado e assinado por ");
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeValida, 2));
		}
		
		retornoIdentificacao.append(" em: ");
		retornoIdentificacao.append(DateUtil.obterDataFormatada(responsavelAnamneseVO.getDthrValida(), "dd/MM/yy HH:mm"));
	}
	
	private void nomeElaborouNotNull(StringBuffer retornoIdentificacao){
		
		if(servidorContratadoElaborou != null && servidorContratadoElaborou.getNomeEspecialidade() != null){
			retornoIdentificacao.append("Elaborado e assinado por médico contratado "); 
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeElaborou, 2));
			retornoIdentificacao.append(StringUtils.SPACE);
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(servidorContratadoElaborou.getNomeEspecialidade(), 2));
		
		}else if(servidorResidenteElaborou != null && servidorResidenteElaborou.getNomeEspecialidade() != null){
			retornoIdentificacao.append("Elaborado e assinado por médico residente "); 
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeElaborou, 2));
			retornoIdentificacao.append(StringUtils.SPACE);
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(servidorResidenteElaborou.getNomeEspecialidade(), 2));
		
		}else{
			retornoIdentificacao.append("Elaborado por ");
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeElaborou, 2));
		}
	}
	
	private void nomeValidaNotNull(StringBuffer retornoIdentificacao){
		if(servidorContratadoValidou != null && servidorContratadoValidou.getNomeEspecialidade() != null){
			retornoIdentificacao.append(" - Assinado por médico contratado "); 
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeValida, 2));
			retornoIdentificacao.append(StringUtils.SPACE);
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(servidorContratadoValidou.getNomeEspecialidade(), 2));
		
		}else if(servidorResidenteValidou != null && servidorResidenteValidou.getNomeEspecialidade() != null){
			retornoIdentificacao.append(" - Assinado por médico residente "); 
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeValida, 2));
			retornoIdentificacao.append(StringUtils.SPACE);
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(servidorResidenteValidou.getNomeEspecialidade(), 2));
		
		}else{
			retornoIdentificacao.append(" - Assinado por ");
			retornoIdentificacao.append(relatorioAnaEvoInternacaoRN.mpmcMinusculo(nomeValida, 2));
		}
		
		retornoIdentificacao.append(" em: ");
		retornoIdentificacao.append(DateUtil.obterDataFormatada(responsavelAnamneseVO.getDthrValida(), "dd/MM/yy HH:mm"));
	}
	
	private String nomeProfissional(Object prof []){
		if(prof != null){
			return (String) prof[1];
		}
		
		return null;
	}
}
