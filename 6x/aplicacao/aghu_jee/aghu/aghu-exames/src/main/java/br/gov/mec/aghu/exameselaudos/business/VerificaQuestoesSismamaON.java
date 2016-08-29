package br.gov.mec.aghu.exameselaudos.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.exames.dao.AelItemSolicExameHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.model.AelSismamaMamoResHist;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Utilizada pela estoria #5868 – Verificar questões do SISMAMA
 *
 */
@Stateless
public class VerificaQuestoesSismamaON extends BaseBusiness {


@EJB
private VerificaQuestoesSismamaRN verificaQuestoesSismamaRN;

private static final Log LOG = LogFactory.getLog(VerificaQuestoesSismamaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;

@EJB
private IExamesLaudosFacade examesLaudosFacade;

	private final static String RESPOSTA_VALOR_TRES = "3";
	private final static String RESPOSTA_VALOR_DOIS = "2";
	private final static String RESPOSTA_VALOR_UM = "1";
	private final static String RESPOSTA_VALOR_ZERO = "0";

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116322235498809689L;	
	
	public Boolean habilitarBotaoQuestaoSismama(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist) {
		Boolean habilitar = Boolean.FALSE;
		if (!solicitacoes.isEmpty() && solicitacoes.size() == 1) {
			Integer iseSoeSeq = solicitacoes.keySet().iterator().next();
			if (solicitacoes.get(iseSoeSeq).size() == 1) {
				Short iseSeqp = solicitacoes.get(iseSoeSeq).iterator().next();
				Short unfSeq = getUnfSeqExecutaExames(iseSoeSeq, iseSeqp, isHist);
				habilitar = getVerificaQuestoesSismamaRN().verificarQuestaoSismama(iseSoeSeq, iseSeqp, unfSeq, isHist);						
			}
		}
		return habilitar;
	}
	
	private Short getUnfSeqExecutaExames(Integer iseSoeSeq, Short iseSeqp, Boolean isHist) {
		if(isHist){
			AelItemSolicExameHist item = getAelItemSolicExameHistDAO().obterPorId(iseSoeSeq, iseSeqp);
			if (item.getAelUnfExecutaExames() != null) {
				return item.getAelUnfExecutaExames().getId().getUnfSeq().getSeq();
			}
		}else{
			AelItemSolicitacaoExames item = getAelItemSolicitacaoExameDAO().obterPorId(iseSoeSeq, iseSeqp);
			if (item.getAelUnfExecutaExames() != null) {
				return item.getAelUnfExecutaExames().getId().getUnfSeq().getSeq();
			}
		}
		return null;
	}

	/*public Boolean habilitarBotaoQuestaoSismamaHist(Map<Integer, Vector<Short>> solicitacoes) {
		Boolean habilitar = Boolean.FALSE;
		if (!solicitacoes.isEmpty() && solicitacoes.size() == 1) {
			Integer iseSoeSeq = solicitacoes.keySet().iterator().next();
			if (solicitacoes.get(iseSoeSeq).size() == 1) {
				Short iseSeqp = solicitacoes.get(iseSoeSeq).iterator().next();
				AelItemSolicExameHist item = getAelItemSolicExameHistDAO().obterPorChavePrimaria(new AelItemSolicExameHistId(iseSoeSeq, iseSeqp));
				Short unfSeq = null;
				if (item.getAelUnfExecutaExames() != null) {
					unfSeq = item.getAelUnfExecutaExames().getId().getUnfSeq().getSeq();
				}
				habilitar = getVerificaQuestoesSismamaRN().verificarQuestaoSismama(iseSoeSeq, iseSeqp, unfSeq, true);						
			}
		}
		return habilitar;
	}*/

	
	private AelItemSolicExameHistDAO getAelItemSolicExameHistDAO() {
		return aelItemSolicExameHistDAO;
	}

	protected VerificaQuestoesSismamaRN getVerificaQuestoesSismamaRN() {
		return verificaQuestoesSismamaRN;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	
	private void obterRespostaNvl(String codigo, Map<String, String> mapRetorno,
			DominioSismamaMamoCadCodigo... codigosNvl) {
		for (int i = 0; i < codigosNvl.length; i++) {
			if (codigosNvl[i].toString().equals(codigo)) {
				mapRetorno.put(codigo,	nvl(mapRetorno.get(codigo)));
			}
		}
	}
	
	public Map<String, String> preencherQuestionarioSisMama(Integer codSolicitacao, Short codItemSolicitacao, Boolean isHist){
		Map<String, String> questionario = new HashMap<String, String>();
		
		if(isHist){
			List<AelSismamaMamoResHist> respostasMamo = getExamesLaudosFacade().obterRespostasMamoHist(codSolicitacao,codItemSolicitacao);
			respostasMamo.addAll(getExamesLaudosFacade().obterRespostasMamografiaHistRespNull(codSolicitacao,codItemSolicitacao));
			for(AelSismamaMamoResHist resposta : respostasMamo){
				questionario.put(DominioSismamaMamoCadCodigo.valueOf(resposta.getAelSismamaMamoCad().getCodigo()).toString(), resposta.getResposta());
			}
		}else{
			List<AelSismamaMamoRes> respostasMamo = getExamesLaudosFacade().obterRespostasMamo(codSolicitacao,codItemSolicitacao);		
			respostasMamo.addAll(getExamesLaudosFacade().obterRespostasMamografiaRespNull(codSolicitacao,codItemSolicitacao));
			
			for(AelSismamaMamoRes resposta : respostasMamo){
				questionario.put(DominioSismamaMamoCadCodigo.valueOf(resposta.getAelSismamaMamoCad().getCodigo()).toString(), resposta.getResposta());
			}
		}
		
		questionario = obterON2VerificaQuestoesSismama(questionario);
		
		return questionario;
	}
	
	public IExamesLaudosFacade getExamesLaudosFacade(){
		return examesLaudosFacade;
	}
	
	public Map<String, String> obterON2VerificaQuestoesSismama(Map<String, String> mapResposta){
		Map<String, String> mapRetorno = new HashMap<String, String>();
		mapRetorno.putAll(mapResposta);
		
		Set<String> keys = mapResposta.keySet();  
		Iterator<String> mapRespostaIterator = keys.iterator();  
		while(mapRespostaIterator.hasNext()){  
		    String codigo = mapRespostaIterator.next();  
		    obterDadosInfClinicas(codigo, mapRetorno);
        	obterDadosAnamnese(codigo, mapRetorno);
		}  			
        return mapRetorno;
	}
	
	/**
	 * P_CARREGA_DADOS_INF_CLINICAS
	 */
	public void obterDadosInfClinicas(String codigo, Map<String, String> mapRetorno){
		
		if (codigo.equals(DominioSismamaMamoCadCodigo.C_CLI_DIAG.toString())){
			String resposta = mapRetorno.get(codigo);			
			if (resposta != null && !resposta.equals(RESPOSTA_VALOR_ZERO)) {
				mapRetorno.put(codigo, resposta);
				mapRetorno.put(DominioSismamaMamoCadCodigo.C_MAMO_DIAG.toString(), RESPOSTA_VALOR_TRES);
			}else{
				mapRetorno.put(DominioSismamaMamoCadCodigo.C_MAMO_DIAG.toString(), RESPOSTA_VALOR_ZERO);
			}	
		}else if (codigo.equals(DominioSismamaMamoCadCodigo.C_CLI_DESC_DIR.toString())
				|| codigo.equals(DominioSismamaMamoCadCodigo.C_CLI_DESC_ESQ.toString())){
			String resposta = mapRetorno.get(codigo);
			
			if (resposta != null){
				if (resposta.equals(RESPOSTA_VALOR_ZERO)){
					mapRetorno.put(codigo, null);				
				}else{
					mapRetorno.put(codigo, resposta);
				}
			}
		}else{
			obterDadosInfClinicasDireita(codigo, mapRetorno);	
			obterDadosInfClinicasEsquerda(codigo, mapRetorno);	
			obterDadosInfClinicasEsp(codigo, mapRetorno);	
			obterDadosInfClinicasRad(codigo, mapRetorno);	
			obterDadosInfClinicasLesao(codigo, mapRetorno);	
		}
		
	}
	
	private void obterDadosInfClinicasDireita(String codigo, Map<String, String> mapRetorno) {

		// --direita
		obterRespostaNvl(codigo, mapRetorno,
				DominioSismamaMamoCadCodigo.C_CLI_PAPIL_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_QSE_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_QIE_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_QSI_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_QII_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_UQEXT_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_UQSUP_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINT_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINF_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_RRA_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_QSE_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_QIE_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_QSI_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_QII_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_UQEXT_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_UQSUP_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINT_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINF_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_RRA_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_PA_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_LINF_AX_DIR,
				DominioSismamaMamoCadCodigo.C_CLI_LINF_SUPRA_DIR);
	}
	
	
	private void obterDadosInfClinicasEsquerda(String codigo, Map<String, String> mapRetorno) {

		// --esquerda
		obterRespostaNvl(codigo, mapRetorno,
				DominioSismamaMamoCadCodigo.C_CLI_PAPIL_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_QSE_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_QIE_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_QSI_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_QII_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_UQEXT_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_UQSUP_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINT_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINF_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_RRA_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_NOD_PA_ESQ);
	}		
	
	private void obterDadosInfClinicasEsp(String codigo, Map<String, String> mapRetorno) {

		obterRespostaNvl(codigo, mapRetorno,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_QSE_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_QIE_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_QSI_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_QII_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_UQEXT_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_UQSUP_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINT_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINF_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_RRA_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_ESP_PA_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_LINF_AX_ESQ,
				DominioSismamaMamoCadCodigo.C_CLI_LINF_SUPRA_ESQ,
				DominioSismamaMamoCadCodigo.C_CONTROLE_RADIOL);
	}
	
	private void obterDadosInfClinicasRad(String codigo, Map<String, String> mapRetorno) {

		obterRespostaNvl(codigo, mapRetorno,
				DominioSismamaMamoCadCodigo.C_RAD_NODULO_DIR,
				DominioSismamaMamoCadCodigo.C_RAD_NODULO_ESQ,
				DominioSismamaMamoCadCodigo.C_RAD_MICROCAL_DIR,
				DominioSismamaMamoCadCodigo.C_RAD_MICROCAL_ESQ,
				DominioSismamaMamoCadCodigo.C_RAD_ASSIM_FOC_DIR,
				DominioSismamaMamoCadCodigo.C_RAD_ASSIM_FOC_ESQ,
				DominioSismamaMamoCadCodigo.C_RAD_ASSIM_DIF_DIR,
				DominioSismamaMamoCadCodigo.C_RAD_ASSIM_DIF_ESQ,
				DominioSismamaMamoCadCodigo.C_RAD_AREA_DENS_DIR,
				DominioSismamaMamoCadCodigo.C_RAD_AREA_DENS_ESQ,
				DominioSismamaMamoCadCodigo.C_RAD_DIST_ARQ_DIR,
				DominioSismamaMamoCadCodigo.C_RAD_DIST_ARQ_ESQ);
	}

	private void obterDadosInfClinicasLesao(String codigo, Map<String, String> mapRetorno) {

		// --lesao
		obterRespostaNvl(codigo, mapRetorno,
				DominioSismamaMamoCadCodigo.C_LESAO_DIAG,
				DominioSismamaMamoCadCodigo.C_DIAG_NODULO_DIR,
				DominioSismamaMamoCadCodigo.C_DIAG_NODULO_ESQ,
				DominioSismamaMamoCadCodigo.C_DIAG_MICROCAL_DIR,
				DominioSismamaMamoCadCodigo.C_DIAG_MICROCAL_ESQ,
				DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_FOC_DIR,
				DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_FOC_ESQ,
				DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_DIF_DIR,
				DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_DIF_ESQ,
				DominioSismamaMamoCadCodigo.C_DIAG_AREA_DENS_DIR,
				DominioSismamaMamoCadCodigo.C_DIAG_AREA_DENS_ESQ,
				DominioSismamaMamoCadCodigo.C_DIAG_DIST_ARQ_DIR,
				DominioSismamaMamoCadCodigo.C_DIAG_DIST_ARQ_ESQ);
	}
	
	
	/**
	 *  P_CARREGA_DADOS_ANAMNESE
	 */
	public void obterDadosAnamnese(String codigo, Map<String, String> mapRetorno){
		
		if (codigo.equals(DominioSismamaMamoCadCodigo.C_ANM_PARENTE_CANCER.toString()) ||
				codigo.equals(DominioSismamaMamoCadCodigo.C_ANM_EXA_PROF.toString())
				|| codigo.equals(DominioSismamaMamoCadCodigo.C_ANM_MAMOGRAF.toString())){
			String resposta = mapRetorno.get(codigo);
			
			if (resposta != null){
				mapRetorno.put(codigo, resposta);
			}else{
				mapRetorno.put(codigo, RESPOSTA_VALOR_DOIS);
			}			
		}
		
		obterDadosAnamneseAna(codigo, mapRetorno);
		obterDadosAnamneseRadioterapia(codigo, mapRetorno);
	}
	
	private String converterDataBanco(String chave){
		Map<String, String> mapMes = new HashMap<String, String>();
		mapMes.put("JAN", "1");
		mapMes.put("FEB", "2");
		mapMes.put("MAR", "3");
		mapMes.put("APR", "4");
		mapMes.put("MAY", "5");
		mapMes.put("JUN", "6");		
		mapMes.put("JUL", "7");
		mapMes.put("AUG", "8");
		mapMes.put("SEP", "9");
		mapMes.put("OCT", "10");
		mapMes.put("NOV", "11");
		mapMes.put("DEC", "12");
		
		return mapMes.get(chave);
	}
	
	public String obterDataResposta(String dataBanco){
		String completa[] = dataBanco.split(" ");
		if(completa.length <= 1){
			String data = completa[0];
			
			// 'yyyymmdd'
			data = data.substring(6, 8).concat("/")
					.concat(data.substring(4, 6)).concat("/")
					.concat(data.substring(0, 4));		
			
			return data;
		}else{	
			String data = completa[0];
			String time = completa[1];
			
			//'dd/mon/yyyy hh24:mi:ss'			
			data = data.substring(0, 2).concat("/")
			.concat(converterDataBanco(data.substring(3, 6))).concat("/")
			.concat(data.substring(7, 11));

			return data + " " + time;			
		}		
	}
	
	private void obterDadosAnamneseAna(String codigo,Map<String, String> mapRetorno) {
		if (codigo.equals(DominioSismamaMamoCadCodigo.D_ANA_MESTRUAC.toString())){
			String resposta = mapRetorno.get(codigo);
			
			if (resposta != null){
				if (resposta.equals(RESPOSTA_VALOR_ZERO) ){
					mapRetorno.put(codigo, null);
				}else if (resposta.length() == 20){
					mapRetorno.put(codigo, obterDataResposta(resposta));
				}else if (resposta.length() == 8){
					mapRetorno.put(codigo, obterDataResposta(resposta));
				}else{
					mapRetorno.put(codigo, resposta);
				}
			}
		}
		else if (codigo.equals(DominioSismamaMamoCadCodigo.C_ANA_MENOP_IDADE.toString())){
			String resposta = mapRetorno.get(codigo);
			
			if (resposta != null){
				if (resposta.equals(RESPOSTA_VALOR_ZERO) ){
					mapRetorno.put(codigo, null);
				}else{
					mapRetorno.put(codigo, resposta);
				}
			}
		}
		else if (codigo.equals(DominioSismamaMamoCadCodigo.C_ANA_USAHORMONIO.toString())){
			String resposta = mapRetorno.get(codigo);
			
			if (resposta != null){
				mapRetorno.put(codigo, resposta);
			}else{
				mapRetorno.put(codigo, RESPOSTA_VALOR_DOIS);
			}	
		}
		else if (codigo.equals(DominioSismamaMamoCadCodigo.C_ANA_GRAVIDA.toString())){
			String resposta = mapRetorno.get(codigo);
			
			if (resposta != null && resposta.equals(RESPOSTA_VALOR_ZERO)){
				mapRetorno.put(codigo, RESPOSTA_VALOR_UM);
			}else if (resposta != null){
				mapRetorno.put(codigo, resposta);
			}else{
				mapRetorno.put(codigo, RESPOSTA_VALOR_DOIS);
			}	
		}else{
			obterRespostaNvl(codigo, mapRetorno,
			DominioSismamaMamoCadCodigo.C_ANM_NOD_MD, DominioSismamaMamoCadCodigo.C_ANM_NOD_ME,
			DominioSismamaMamoCadCodigo.C_ANM_NOD, DominioSismamaMamoCadCodigo.C_ANA_NAOFEZCIRUR);
		}
	}
	
	private void obterDadosAnamneseRadioterapia(String codigo, Map<String, String> mapRetorno) {		
		
		// -- 9. Radioterapia		
		if (codigo.equals(DominioSismamaMamoCadCodigo.C_ANA_RADIO.toString())){
			String resposta = mapRetorno.get(codigo);
			
			if (resposta != null){
				mapRetorno.put(codigo, resposta);
			}else{
				mapRetorno.put(codigo, RESPOSTA_VALOR_DOIS);
			}	
		}else{
			obterRespostaNvl(codigo, mapRetorno,DominioSismamaMamoCadCodigo.C_ANA_RADIO_MDIR,
					DominioSismamaMamoCadCodigo.C_ANA_RADIO_MESQ);
		}		
	}
	
	private String nvl(String valor){
		if (valor == null){
			return RESPOSTA_VALOR_ZERO;
		}
		return valor;
	}
}
