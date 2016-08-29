package br.gov.mec.aghu.ambulatorio.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroAtestadoMarcacaoAmbulatorioController extends ActionController{
	


	/**
	 * 
	 */
	private static final long serialVersionUID = -6216046512554398982L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private String declaracao1;
	private String declaracaoGrid;

	private AacConsultas consultaSelecionada;
	private List<MamAtestados> listAtestadosMarcacao;
	private MamAtestados atestado = new MamAtestados();
	private AghAtendimentos atendimento;
 	private Boolean modoEdicao =  Boolean.FALSE;
	private MamAtestados itemSelecionado;
	private MamTipoAtestado tipoAtestado;
	private BigDecimal parametro;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}

	public void inicio(){
		try {
			atestado.setNroVias(Byte.valueOf("1"));
			parametro = ambulatorioFacade.obterParametroAtestadoMarcacao();
			tipoAtestado = ambulatorioFacade.obterTipoAtestadoOriginal(parametro.shortValue());
			listAtestadosMarcacao = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
			atendimento = aghuFacade.obterAtendimentoPorConsulta(this.consultaSelecionada.getNumero());
			montarDeclaracao1();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void montarDeclaracao1(){
		declaracao1 =  "Declaro que ".concat(montarDeclaracaoGrid());
	}
	
	public String montarDeclaracaoGrid(){
	  declaracaoGrid = consultaSelecionada.getPaciente().getNome()
		.concat(", prontuário ")
		.concat(atendimento.getProntuario().toString());
	  return declaracaoGrid;
	}
	
	public void gravar(){
		
			try {				
				ambulatorioFacade.validarValorMinimoNumeroVias(atestado);
				
				boolean novo = atestado.getSeq() == null ? true : false; 
				if(atestado.getSeq() == null){
					atestado.setConsulta(consultaSelecionada);
					atestado.setAipPacientes(consultaSelecionada.getPaciente());
					atestado.setServidor(servidorLogadoFacade.obterServidorLogado());
					atestado.setIndPendente(DominioIndPendenteAmbulatorio.P);
					atestado.setIndImpresso(Boolean.FALSE);
					atestado.setDthrCriacao(new Date());
					atestado.setMamTipoAtestado(tipoAtestado);
					atestado.setServidorValida(servidorLogadoFacade.obterServidorLogado());
					atestado.setObservacao(atestado.getObservacao().trim());
					atestado.setVersion(0);
				}
				atestado.setObservacao(atestado.getObservacao().trim());
				ambulatorioFacade.gravarAtestado(atestado); 
				
				limpar();
				listAtestadosMarcacao = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
				
				if(novo){
					apresentarMsgNegocio(Severity.INFO, "MSG_ATESTADO_CADASTRADO_SUCESSO");
				} else{
					apresentarMsgNegocio(Severity.INFO, "MSG_ATESTADO_ALTERADO_SUCESSO");
				}
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
	}
	
	public void limpar(){
		itemSelecionado = null;
		modoEdicao = Boolean.FALSE;
		atestado = new MamAtestados();
		atestado.setNroVias(Byte.valueOf("1"));
	}
	
	public void editar(){
		modoEdicao = Boolean.TRUE;
	}
	
	public boolean editandoRegistro(MamAtestados item){
		if(atestado.getSeq()!=null && atestado.equals(item)){
			return true;
		}
		return false;
	}
	
	public void excluir(){
		try {
			ambulatorioFacade.excluirAtestadoComparecimento(itemSelecionado);
			apresentarMsgNegocio(Severity.INFO, "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_EXCLUIDO");
			listAtestadosMarcacao = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
			limpar();
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_EXCLUIR_REGISTRO");
		}
		
	}
	
	//Getters e Setters
	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}


	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public MamAtestados getAtestado() {
		return atestado;
	}

	public void setAtestado(MamAtestados atestado) {
		this.atestado = atestado;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public MamAtestados getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MamAtestados itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public String getDeclaracao1() {
		return declaracao1;
	}

	public void setDeclaracao1(String declaracao1) {
		this.declaracao1 = declaracao1;
	}
	
	public String getDeclaracaoGrid() {
		return declaracaoGrid;
	}

	public void setDeclaracaoGrid(String declaracaoGrid) {
		this.declaracaoGrid = declaracaoGrid;
	}

	public List<MamAtestados> getListAtestadosMarcacao() {
		return listAtestadosMarcacao;
	}

	public void setListAtestadosMarcacao(List<MamAtestados> listAtestadosMarcacao) {
		this.listAtestadosMarcacao = listAtestadosMarcacao;
	}
}
