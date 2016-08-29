package br.gov.mec.aghu.blococirurgico.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoItensId;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoCirurgicaAchadosOperatoriosController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2148740019890237700L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;	
	
		
	private DescricaoCirurgicaVO descricaoCirurgicaVO;
	
	// Atributos para campos da tela
	private String achadosOperatorios;
	private String observacao;
	private String intercorrenciaClinica;

	private DominioSimNao indPerdaSangue;
	private DominioSimNao indIntercorrencia;
	private Integer volumePerdaSangue;
	
	
	private Integer dcgCrgSeq;
	private Short dcgSeqp;	
	
	private MbcDescricaoItens descricaoItens;
	private MbcDescricaoCirurgica descricaoCirurgica;
	
	private Boolean exibeModalValidacaoIntercorrencia; 
	
	@Inject
	private	RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	public void iniciar(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
		
		dcgCrgSeq = descricaoCirurgicaVO.getDcgCrgSeq();
		dcgSeqp = descricaoCirurgicaVO.getDcgSeqp();
		
		descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);

		limpar();
		carregarCamposDescricaoItens();
		
		descricaoCirurgica = blocoCirurgicoFacade.buscarMbcDescricaoCirurgica(dcgCrgSeq, dcgSeqp);
		
		exibeModalValidacaoIntercorrencia = Boolean.FALSE;
		
		
	}
	
	public boolean validarCamposObrigatorios(){
		boolean validado = true;
		if(descricaoCirurgicaVO== null){
			return true;
		}
		if(indIntercorrencia==null){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_INTERCORRENCIA");
			validado = false;
        }else if(indIntercorrencia.equals(DominioSimNao.S) && intercorrenciaClinica == null || "".equals(intercorrenciaClinica)){
        	this.apresentarMsgNegocio(Severity.ERROR,"MBC_01835");
        	validado = false;
        }
        if(indPerdaSangue==null){
        	this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_PERDA_SANG_SIGNIF");
        	validado = false;
        }else if(indPerdaSangue.equals(DominioSimNao.S) && volumePerdaSangue == null){
        	this.apresentarMsgNegocio(Severity.ERROR,"TITLE_VOLUME_PERDA_SANG_SUGNIF");
        	validado = false;
        }
        if(achadosOperatorios == null){
        	this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_ACHADOS_OPERATORIOS");
        	validado = false;
        }        
        return validado ;
	}
	
	private void carregarCamposDescricaoItens() {
		if (descricaoItens != null) {
			achadosOperatorios = descricaoItens.getAchadosOperatorios();
			observacao = descricaoItens.getObservacao();
			indIntercorrencia = descricaoItens.getIndIntercorrencia() != null ? DominioSimNao.getInstance(descricaoItens.getIndIntercorrencia()) : null;
			indPerdaSangue = descricaoItens.getIndPerdaSangue() != null ? DominioSimNao.getInstance(descricaoItens.getIndPerdaSangue()) : null;
			intercorrenciaClinica = descricaoItens.getIntercorrenciaClinica();
			volumePerdaSangue = descricaoItens.getVolumePerdaSangue();
		}
	}
	
	private void limpar() {
		/*if (descricaoItens != null) {
			blocoCirurgicoFacade.refresh(descricaoItens);
		}*/
		achadosOperatorios = null;
		observacao = null;
		indIntercorrencia = null;
		indPerdaSangue = null;
		intercorrenciaClinica = null;
	}
	
	public void gravarAchadosOperatorios() {
		
		descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
		
		if(achadosOperatorios == null){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_ACHADOS_OPERATORIOS");
			return;
		}
		
		achadosOperatorios = achadosOperatorios.replaceAll("\\r\\n", "\n");
		
		if (descricaoItens == null) {
			descricaoItens = blocoCirurgicoFacade.montarDescricaoItens(
					descricaoCirurgica, achadosOperatorios, observacao,
					indIntercorrencia, indPerdaSangue, volumePerdaSangue, intercorrenciaClinica);
		} else {
			descricaoItens.setAchadosOperatorios(achadosOperatorios);
		}
		
		try {
			blocoCirurgicoFacade.persistirDescricaoItens(descricaoItens);			
			//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DESCRICAO_CIRURGICA_ACHADOS_OPERATORIOS_SALVO_COM_SUCESSO");
			
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
		descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
		relatorioDescricaoCirurgiaController.inicio();
	}
	
	public void gravarPerdaSangSignif(){
		descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
		if (descricaoItens == null) {
			descricaoItens = blocoCirurgicoFacade.montarDescricaoItens(descricaoCirurgica, achadosOperatorios, observacao,
					indIntercorrencia, indPerdaSangue, volumePerdaSangue, intercorrenciaClinica);
		} else {
			descricaoItens.setIndPerdaSangue(indPerdaSangue.isSim());
		}
		if(!descricaoItens.getIndPerdaSangue()){
			setVolumePerdaSangue(null);
			descricaoItens.setVolumePerdaSangue(null);
		}
		try {
			blocoCirurgicoFacade.persistirDescricaoItens(descricaoItens);			
			//this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DESCRICAO_CIRURGICA_PERDA_SANG_SIGNIF_SALVO_COM_SUCESSO");
			descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void gravarVolumePerdaSangSignif(){
		descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
		if(indPerdaSangue==null){
        	this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_PERDA_SANG_SIGNIF");
        	return;
        }else if(indPerdaSangue.equals(DominioSimNao.S) && volumePerdaSangue == null){
        	this.apresentarMsgNegocio(Severity.ERROR,"TITLE_VOLUME_PERDA_SANG_SUGNIF");
        	return;
        }
		
		if (descricaoItens == null) {
			descricaoItens = blocoCirurgicoFacade.montarDescricaoItens(descricaoCirurgica, achadosOperatorios, observacao,
					indIntercorrencia, indPerdaSangue, volumePerdaSangue, intercorrenciaClinica);
		} else {
			descricaoItens.setVolumePerdaSangue(volumePerdaSangue);
		}
		
		try {
			blocoCirurgicoFacade.persistirDescricaoItens(descricaoItens);			
			//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DESCRICAO_CIRURGICA_VOLUME_PERDA_SANG_SIGNIF_SALVO_COM_SUCESSO");
			descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);	
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void gravarHouveIntercorrencia(){
		descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
		if (descricaoItens == null) {
			descricaoItens = blocoCirurgicoFacade.montarDescricaoItens(descricaoCirurgica, achadosOperatorios, observacao,
					indIntercorrencia, indPerdaSangue, volumePerdaSangue, intercorrenciaClinica);
		} else {
			descricaoItens.setIndIntercorrencia(indIntercorrencia.isSim());
		}
		if(!descricaoItens.getIndIntercorrencia()){
			descricaoItens.setIntercorrenciaClinica(null);
			setIntercorrenciaClinica(null);
		}
		try {
			blocoCirurgicoFacade.persistirDescricaoItens(descricaoItens);			
			//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DESCRICAO_CIRURGICA_HOUVE_INTERCORRENCIA_SALVO_COM_SUCESSO");
			descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	
	public void gravarIntercorrencias() {
		descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);
		if(indIntercorrencia==null){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_INFORME_INTERCORRENCIA");
			exibeModalValidacaoIntercorrencia = Boolean.TRUE;
			return;
        }else if(indIntercorrencia.equals(DominioSimNao.S) && intercorrenciaClinica == null || "".equals(intercorrenciaClinica)){
        	this.apresentarMsgNegocio(Severity.ERROR,"MBC_01835");
        	exibeModalValidacaoIntercorrencia = Boolean.TRUE;
        	return;
        }
		
		exibeModalValidacaoIntercorrencia = Boolean.FALSE;
		
		intercorrenciaClinica = intercorrenciaClinica.replaceAll("\\r\\n", "\n");
		
		if (descricaoItens == null) {
			descricaoItens = blocoCirurgicoFacade.montarDescricaoItens(descricaoCirurgica, achadosOperatorios, observacao,
					indIntercorrencia, indPerdaSangue, volumePerdaSangue, intercorrenciaClinica);
		} else {
			descricaoItens.setIntercorrenciaClinica(intercorrenciaClinica);
		}
		
		try {
			blocoCirurgicoFacade.validarIntercorrenciaAchadosOperatorios(descricaoItens);
		} catch (ApplicationBusinessException e) {
			exibeModalValidacaoIntercorrencia = Boolean.TRUE;
			apresentarExcecaoNegocio(e);			
			return;
		}
		
		MbcDescricaoItens descricaoItensOld = blocoCirurgicoFacade
				.obterMbcDescricaoItensOriginal(new MbcDescricaoItensId(
						dcgCrgSeq, dcgSeqp));
		
		if (descricaoItensOld != null && StringUtils.defaultString(descricaoItensOld.getIntercorrenciaClinica()).equals(
				StringUtils.defaultString(descricaoItens.getIntercorrenciaClinica()))) {
			return; // Retorna pois não houve alteração para o campo "Observação e Intercorrências"
		}
		
		try {
			blocoCirurgicoFacade.persistirDescricaoItens(descricaoItens);			
			//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DESCRICAO_CIRURGICA_OBSERVACOES_INTERCORRENCIA_SALVO_COM_SUCESSO");
			descricaoItens = blocoCirurgicoFacade.buscarDescricaoItens(dcgCrgSeq, dcgSeqp);		
			exibeModalValidacaoIntercorrencia = Boolean.FALSE;
			relatorioDescricaoCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void fecharModalObsIntercorrencia() {
		exibeModalValidacaoIntercorrencia = Boolean.FALSE;
	}
	
//	public void desfazerIntercorrencia() {
//		descricaoItens.setIndIntercorrencia(Boolean.FALSE);
//		descricaoItens.setObservacao(null);
//		
//		try {
//			blocoCirurgicoFacade.persistirDescricaoItens(descricaoItens, servidorLogado);
//			blocoCirurgicoFacade.flush();
//			this.apresentarMsgNegocio(Severity.INFO, 
//					"MENSAGEM_DESCRICAO_CIRURGICA_OBSERVACOES_INTERCORRENCIA_SALVO_COM_SUCESSO");
//			
//			carregarCamposDescricaoItens();
//			
//		} catch (BaseException e) {
//			apresentarExcecaoNegocio(e);
//			logError(e);
//		}
//		
//		exibeModalValidacaoIntercorrencia = Boolean.FALSE;
//	}

	public DescricaoCirurgicaVO getDescricaoCirurgicaVO() {
		return descricaoCirurgicaVO;
	}

	public void setDescricaoCirurgicaVO(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
	}

	public String getAchadosOperatorios() {
		return achadosOperatorios;
	}

	public void setAchadosOperatorios(String achadosOperatorios) {
		this.achadosOperatorios = achadosOperatorios;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public DominioSimNao getIndIntercorrencia() {
		return indIntercorrencia;
	}

	public void setIndIntercorrencia(DominioSimNao indIntercorrencia) {
		this.indIntercorrencia = indIntercorrencia;
	}

	public Integer getDcgCrgSeq() {
		return dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	public Short getDcgSeqp() {
		return dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}

	public Boolean getExibeModalValidacaoIntercorrencia() {
		return exibeModalValidacaoIntercorrencia;
	}

	public void setExibeModalValidacaoIntercorrencia(
			Boolean exibeModalValidacaoIntercorrencia) {
		this.exibeModalValidacaoIntercorrencia = exibeModalValidacaoIntercorrencia;
	}

	public DominioSimNao getIndPerdaSangue() {
		return indPerdaSangue;
	}

	public void setIndPerdaSangue(DominioSimNao indPerdaSangue) {
		this.indPerdaSangue = indPerdaSangue;
	}

	public Integer getVolumePerdaSangue() {
		return volumePerdaSangue;
	}

	public void setVolumePerdaSangue(Integer volumePerdaSangue) {
		this.volumePerdaSangue = volumePerdaSangue;
	}

	public String getIntercorrenciaClinica() {
		return intercorrenciaClinica;
	}

	public void setIntercorrenciaClinica(String intercorrenciaClinica) {
		this.intercorrenciaClinica = intercorrenciaClinica;
	}

}
