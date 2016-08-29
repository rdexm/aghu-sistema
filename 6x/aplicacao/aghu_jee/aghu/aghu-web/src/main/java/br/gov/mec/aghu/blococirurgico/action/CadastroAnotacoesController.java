package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.model.MbcCirurgiaAnotacao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroAnotacoesController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final String AGENDA_PROCEDIMENTOS = "blococirurgico-agendaProcedimentos";
	
	private static final long serialVersionUID = 3661785096909101631L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	/*Cirurgia*/
	private MbcCirurgias cirurgia;
	
	/*Cirurgia Anotações*/
	private MbcCirurgiaAnotacao anotacao;
	
	/*Localização*/
	private String localizacao;
	
	/*Prontuario Formatado*/
	private String prontuarioFormatado;
	
	/*Anotações*/
	private List<MbcCirurgiaAnotacao> listaAnotacoes;
	
	/**
	 * Codigo da cirurgia, obtido via page parameter.
	 */
	private Integer mbcCirurgiaCodigo;
	
	private String voltarPara;
	
	public void inicio() {
	 

	 

		if (this.mbcCirurgiaCodigo != null) {
			this.cirurgia = this.blocoCirurgicoFacade
					.obterCirurgiaPorSeq(mbcCirurgiaCodigo);
			
			atualizarLista(mbcCirurgiaCodigo);
			this.localizacao = this.blocoCirurgicoFacade.obterQuarto(this.cirurgia.getPaciente().getCodigo());
			this.prontuarioFormatado = getProntuarioFormatado(this.cirurgia.getPaciente().getProntuario());
			
			anotacao = new MbcCirurgiaAnotacao();
		}

	
	}
	
	
	/**
	 * Atualiza a lista de anotações
	 * @param seq
	 */
	private void atualizarLista(Integer seq) {
		this.listaAnotacoes = this.blocoCirurgicoFacade.obterCirurgiaAnotacaoPorSeqCirurgia(seq);
	}
	
	private String getProntuarioFormatado(Integer prontuario) {
		return CoreUtil.formataProntuario(prontuario);
	}
	
	public void confirmar() {
		try {
			this.blocoCirurgicoFacade.persistirCirurgiaAnotacao(anotacao, mbcCirurgiaCodigo);
			
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_CRIACAO_ANOTACAO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		limpar();
	}
	
	public void limpar() {
		this.anotacao = new MbcCirurgiaAnotacao();
		atualizarLista(mbcCirurgiaCodigo);
	}
	
	public String voltar() {
		if(voltarPara != null){
			return  voltarPara;
			
		} else {
		return AGENDA_PROCEDIMENTOS;
	}
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public MbcCirurgiaAnotacao getAnotacao() {
		return anotacao;
	}

	public void setAnotacao(MbcCirurgiaAnotacao anotacao) {
		this.anotacao = anotacao;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public List<MbcCirurgiaAnotacao> getListaAnotacoes() {
		return listaAnotacoes;
	}

	public void setListaAnotacoes(
			List<MbcCirurgiaAnotacao> listaAnotacoes) {
		this.listaAnotacoes = listaAnotacoes;
	}

	public Integer getMbcCirurgiaCodigo() {
		return mbcCirurgiaCodigo;
	}

	public void setMbcCirurgiaCodigo(Integer mbcCirurgiaCodigo) {
		this.mbcCirurgiaCodigo = mbcCirurgiaCodigo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}