package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class DetalhaSolicitacaoHemoterapicaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6602949857598530046L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	private static final String AGENDA_PROCEDIMENTOS = "blococirurgico-agendaProcedimentos";
	
	/*Cirurgia*/
	private MbcCirurgias cirurgia;
	
	/*Localização*/
	private String localizacao;
	
	/*Prontuario Formatado*/
	private String prontuarioFormatado;
	
	/*Solicitações*/
	private List<MbcSolicHemoCirgAgendada> listaSolicitacoes;
	
	/*Controla exibição da mensagem de tipagem sanguínea*/
	private Boolean exibirMensagem = Boolean.FALSE;
	

	/**
	 * Codigo da cirurgia, obtido via page parameter.
	 */
	private Integer mbcCirurgiaCodigo;

	public void inicio(){
	 

	 

		// mbcCirurgiaCodigo está sendo passado como um valor fixo via page parameter
		if (this.mbcCirurgiaCodigo != null) {
			this.cirurgia = this.blocoCirurgicoFacade
				.obterCirurgiaPorChavePrimaria(mbcCirurgiaCodigo, new Enum[] {MbcCirurgias.Fields.UNIDADE_FUNCIONAL, MbcCirurgias.Fields.PACIENTE}, null);
			
			//verifica tipagem sanguínea solicitada
			this.exibirMensagem = this.blocoCirurgicoFacade.obterTipagemSanguinea(this.mbcCirurgiaCodigo);

			//atualizar a lista de solicitações
			this.listaSolicitacoes = this.blocoCirurgicoFacade.pesquisarSolicHemoterapica(mbcCirurgiaCodigo);
			
			this.localizacao = this.blocoCirurgicoFacade.obterQuarto(this.cirurgia.getPaciente().getCodigo());
			this.prontuarioFormatado = getProntuarioFormatado(this.cirurgia.getPaciente().getProntuario());
		}
	
	}
	
	
	public String obterDescricaoSimNao(Boolean valor) {
		if(valor != null) {
			return DominioSimNao.getInstance(valor).getDescricao();
		}
		return null;
	}
	
	private String getProntuarioFormatado(Integer prontuario) {
		return CoreUtil.formataProntuario(prontuario);
	}
	
	public String voltar() {
		return AGENDA_PROCEDIMENTOS;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
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

	public List<MbcSolicHemoCirgAgendada> getListaSolicitacoes() {
		return listaSolicitacoes;
	}

	public void setListaSolicitacoes(
			List<MbcSolicHemoCirgAgendada> listaSolicitacoes) {
		this.listaSolicitacoes = listaSolicitacoes;
	}

	public Integer getMbcCirurgiaCodigo() {
		return mbcCirurgiaCodigo;
	}

	public void setMbcCirurgiaCodigo(Integer mbcCirurgiaCodigo) {
		this.mbcCirurgiaCodigo = mbcCirurgiaCodigo;
	}

	public boolean isExibirMensagem() {
		return exibirMensagem;
	}

	public void setExibirMensagem(boolean exibirMensagem) {
		this.exibirMensagem = exibirMensagem;
	}

}
