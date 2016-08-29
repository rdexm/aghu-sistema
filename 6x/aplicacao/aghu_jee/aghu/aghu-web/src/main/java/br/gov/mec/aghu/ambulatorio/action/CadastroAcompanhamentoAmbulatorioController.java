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

public class CadastroAcompanhamentoAmbulatorioController extends ActionController{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -528529963219898525L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private AacConsultas consultaSelecionada;
	private List<MamAtestados> listAtestadosComparecimento;
	private MamAtestados atestado = new MamAtestados();
	private String dadosPaciente;
	private AghAtendimentos atendimento;
 	private Boolean modoEdicao =  Boolean.FALSE;;
	private MamAtestados itemSelecionado;
	private MamTipoAtestado tipoAtestado;
	private String nomePaciente;
	private BigDecimal parametro;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}

	public void inicio(){
		try {
			atestado.setNroVias(Byte.valueOf("1"));
			atestado.setDataFinal(new Date());
			atestado.setDataInicial(new Date());
			atestado.setDthrCons(new Date());
			parametro = ambulatorioFacade.obterParametroAtestadoAcompanhamento();
			tipoAtestado = ambulatorioFacade.obterTipoAtestadoOriginal(parametro.shortValue());
			listAtestadosComparecimento = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
			nomePaciente = consultaSelecionada.getPaciente().getNome();
			atendimento = aghuFacade.obterAtendimentoPorConsulta(this.consultaSelecionada.getNumero());
			obterDadosPaciente();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void obterDadosPaciente(){
		StringBuilder retorno = new StringBuilder(100);
		retorno.append(consultaSelecionada.getPaciente().getNome()).append(", prontuário: ");
		if(atendimento!=null) {
			retorno.append(atendimento.getProntuario());
		}
		else {
			retorno.append(consultaSelecionada.getPaciente().getProntuario());
		}
		retorno.append(", para atendimento da consulta");
		dadosPaciente = retorno.toString();
	}
	
	
	public void gravar(){
		List<String> camposObrigatorios = ambulatorioFacade.validarCamposAtestadoAcompanhamento(atestado);
		if(camposObrigatorios.isEmpty()){
			try {
				
				ambulatorioFacade.validarValorMinimoNumeroVias(atestado);
				ambulatorioFacade.validarHoraInicioFimAtestado(atestado.getDataInicial(), atestado.getDataFinal());
				
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
				}
				
				ambulatorioFacade.gravarAtestado(atestado); 
				
				limpar();
				listAtestadosComparecimento = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
				
				if(novo){
					apresentarMsgNegocio(Severity.INFO, "MSG_ATESTADO_CADASTRADO_SUCESSO");
				} else{
					apresentarMsgNegocio(Severity.INFO, "MSG_ATESTADO_ALTERADO_SUCESSO");
				}
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}else{
			for (String erro : camposObrigatorios) {
				apresentarMsgNegocio(Severity.ERROR, erro);
			}
		}
	}
	
	
	public boolean editandoRegistro(MamAtestados item){
		if(atestado.getSeq()!=null && atestado.equals(item)){
			return true;
		}
		return false;
	}
	
	
	public void limpar(){
		itemSelecionado = null;
		modoEdicao = Boolean.FALSE;
		atestado = new MamAtestados();
		atestado.setNroVias(Byte.valueOf("1"));
		atestado.setDataFinal(new Date());
		atestado.setDataInicial(new Date());
		atestado.setDthrCons(new Date());
	}
	
	public void editar(){
		modoEdicao = Boolean.TRUE;
	}
	
	public void selecionarItem(MamAtestados item){
		setItemSelecionado(item);
	}
	
	public void excluir(){
		try {
			ambulatorioFacade.excluirAtestadoComparecimento(itemSelecionado);
			apresentarMsgNegocio(Severity.INFO, "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_EXCLUIDO");
			listAtestadosComparecimento = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
			if(modoEdicao){
				setItemSelecionado(atestado);
			}
			//limpar();
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_EXCLUIR_REGISTRO");
		}
		
	}
	
	public void excluir(MamAtestados item){
		setItemSelecionado(item);
		excluir();
	}
	
	//Getters e Setters
	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}


	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public List<MamAtestados> getListAtestadosComparecimento() {
		return listAtestadosComparecimento;
	}

	public void setListAtestadosComparecimento(
			List<MamAtestados> listAtestadosComparecimento) {
		this.listAtestadosComparecimento = listAtestadosComparecimento;
	}

	public MamAtestados getAtestado() {
		return atestado;
	}

	public void setAtestado(MamAtestados atestado) {
		this.atestado = atestado;
	}

	public String getDadosPaciente() {
		return dadosPaciente;
	}

	public void setDadosPaciente(String dadosPaciente) {
		this.dadosPaciente = dadosPaciente;
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

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

}
