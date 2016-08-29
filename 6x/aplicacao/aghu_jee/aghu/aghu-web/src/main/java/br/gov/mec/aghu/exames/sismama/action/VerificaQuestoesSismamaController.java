package br.gov.mec.aghu.exames.sismama.action;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSismamaDescargaPapilar;
import br.gov.mec.aghu.dominio.DominioSismamaMama;
import br.gov.mec.aghu.dominio.DominioSismamaMamaExaminada;
import br.gov.mec.aghu.dominio.DominioSismamaSimNaoNaoSabe;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.core.action.ActionController;


public class VerificaQuestoesSismamaController extends ActionController {

	private static final long serialVersionUID = 2149511466024586215L;

	private Integer prontuario;
	private String nomePaciente;
	private Integer solicitacao;
	private Integer item;
	private String descricaoUsualExame;
	private String informacaoClinica;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Map<String, String> questionario = new HashMap<String, String>();
	
	private String voltarPara;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String voltar() {
		return voltarPara;
	}
	
	public void preencherQuestionario(Integer codSolicitacao, Short codItemSolicitacao, Boolean isHist){
		
		questionario = examesLaudosFacade.preencherQuestionarioSisMama(codSolicitacao,codItemSolicitacao, isHist);
		
		this.iseSoeSeq = codSolicitacao;
		this.iseSeqp = codItemSolicitacao;
		//preenche dados da solicitacao
		if(isHist){
			preencheDadosDaSolicitacaoHistorico(codSolicitacao, codItemSolicitacao);
			this.voltarPara  = "/pages/exames/pesquisa/pesquisaExamesHistorico.xhtml";
		}else{
			preencheDadosDaSolicitacao(codSolicitacao, codItemSolicitacao);
			this.voltarPara  = "/pages/exames/pesquisa/pesquisaExames.xhtml";
		}
	}

	private void preencheDadosDaSolicitacaoHistorico(Integer codSolicitacao, Short codItemSolicitacao) {
		//recupera solicitação
		AelItemSolicExameHist itemSolicitacaoExames = examesFacade.buscaItemSolicitacaoExamePorIdHist(codSolicitacao, codItemSolicitacao);
		this.solicitacao = codSolicitacao;
		this.nomePaciente = itemSolicitacaoExames.getSolicitacaoExame().getAtendimento().getPaciente().getNome();
		this.prontuario = itemSolicitacaoExames.getSolicitacaoExame().getAtendimento().getProntuario();
		this.item=codItemSolicitacao.intValue();
		this.descricaoUsualExame = itemSolicitacaoExames.getExame().getDescricaoUsual();
		
	}

	private void preencheDadosDaSolicitacao(Integer codSolicitacao,
			Short codItemSolicitacao) {
		//recupera solicitação	
		AelItemSolicitacaoExames itemSolicitacaoExames = examesFacade.buscaItemSolicitacaoExamePorId(codSolicitacao, codItemSolicitacao);
		this.solicitacao = codSolicitacao;
		this.nomePaciente = itemSolicitacaoExames.getSolicitacaoExame().getAtendimento().getPaciente().getNome();
		this.prontuario = itemSolicitacaoExames.getSolicitacaoExame().getAtendimento().getProntuario();
		this.item=codItemSolicitacao.intValue();
		this.descricaoUsualExame = itemSolicitacaoExames.getExame().getDescricaoUsual();
	}
	
	public Boolean obterBooleanResposta(String valor){
		if(valor.equals("0")){
			return Boolean.FALSE;
		} else{
			return Boolean.TRUE;
		}
	}
	
	/**
	 * 
	 * @author bruno.mourao
	 * @since 08/01/2013
	 * @param valor
	 * @return
	 */
	public Date obterDataResposta(String valor){
		if(valor.equals("0") || StringUtils.isEmpty(valor)){
			return null;
		}
		else{
			//Espera-se datas no formato dd/mm/yyyy 24hh:mm:ss
			String completa[] = valor.split(" ");
			if(completa.length >0){
				String data = completa[0];
				
				String data_[]  = data.split("/");
				
				Calendar cal = Calendar.getInstance();
				
				if(data_.length == 3){
					cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(data_[0]));
					cal.set(Calendar.MONTH, Integer.valueOf(data_[1])-1);
					cal.set(Calendar.YEAR, Integer.valueOf(data_[2]));
				}
				if(completa.length ==2){
					String hora = completa[1];
					String hora_[] = hora.split(":");
					if(hora_.length ==3){
						cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hora_[0]));
						cal.set(Calendar.MINUTE, Integer.valueOf(hora_[1]));
						cal.set(Calendar.SECOND, Integer.valueOf(hora_[2]));
					}
					else{
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
					}
				}
				else{
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
				}
				cal.set(Calendar.MILLISECOND, 0);
				
				return cal.getTime();
			}
			else{
				return null;
			}
		}
	}
	
	public DominioSismamaSimNaoNaoSabe obterDominioSismamaSimNaoResposta(String valor){
		if(StringUtils.isAlpha(valor)){
			return null;
		} else{
			Integer val = Integer.valueOf(valor);
			switch(val){
				case 1:  return DominioSismamaSimNaoNaoSabe.NAO;
				case 2:  return DominioSismamaSimNaoNaoSabe.NAO_SABE;
				case 3:  return DominioSismamaSimNaoNaoSabe.SIM;
				default: return null;
			}
		}
	}
	
	public DominioSismamaMama obterDominioSismamaMamaResposta(String valor){
		if(StringUtils.isAlpha(valor)){
			return null;
		} else{
			Integer val = Integer.valueOf(valor);
			switch(val){
				case 2:  return DominioSismamaMama.NUNCA_FORAM_EXAMINADAS;
				case 3:  return DominioSismamaMama.SIM;
				default: return null;
			}
		}
	}
	
	/**
	 * 
	 * @author bruno.mourao
	 * @since 08/01/2013
	 * @param valor
	 * @return
	 */
	public DominioSismamaDescargaPapilar obterDominioSismamaDescargaPapilarResposta(String valor){
		if(StringUtils.isAlpha(valor) || (valor != null && valor.length() == 0)) {
			return null;
		} 
		else{
			Integer val = Integer.valueOf(valor);
			switch(val){
				case 1:  return DominioSismamaDescargaPapilar.CRISTALINA;
				case 2:  return DominioSismamaDescargaPapilar.HEMORRAGICA;
				default: return null;
			}
		}
	}
	
	/**
	 * 
	 * @author bruno.mourao
	 * @since 08/01/2013
	 */
	public DominioSismamaMamaExaminada obterDominioSismamaMamaExaminadaResposta(String valor){
		if(StringUtils.isAlpha(valor)){
			return null;
		} else{
			Integer val = Integer.valueOf(valor);
			switch(val){
				case 1:  return DominioSismamaMamaExaminada.DIREITA;
				case 2:  return DominioSismamaMamaExaminada.ESQUERDA;
				case 3:  return DominioSismamaMamaExaminada.AMBAS;
				default: return null;
			}
		}
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public String getDescricaoUsualExame() {
		return descricaoUsualExame;
	}

	public void setDescricaoUsualExame(String descricaoUsualExame) {
		this.descricaoUsualExame = descricaoUsualExame;
	}

	public String getInformacaoClinica() {
		return informacaoClinica;
	}

	public void setInformacaoClinica(String informacaoClinica) {
		this.informacaoClinica = informacaoClinica;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Map<String, String> getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Map<String, String> questionario) {
		this.questionario = questionario;
	}
}

