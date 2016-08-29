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
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroAtestadoMedicoAmbulatorioController extends ActionController{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7617716978973058746L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private String declaracao1;
	
	private AacConsultas consultaSelecionada;
	private List<MamAtestados> listAtestadosMedicos;
	private MamAtestados atestado = new MamAtestados();
	private String dadosPaciente;
	private AghAtendimentos atendimento;
 	private Boolean modoEdicao = Boolean.FALSE;
	private MamAtestados itemSelecionado;
	private MamTipoAtestado tipoAtestado;
	private BigDecimal parametro; 
	private AipPacientes paciente;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}

	public void inicio(){
		try {
			paciente = ambulatorioFacade.obterPacienteOriginal(consultaSelecionada.getPaciente().getCodigo());
			atestado.setNroVias(Byte.valueOf("1"));
			parametro = ambulatorioFacade.obterParametroAtestadoAtestadoMedico();
			tipoAtestado = ambulatorioFacade.obterTipoAtestadoOriginal(parametro.shortValue());
			listAtestadosMedicos = ambulatorioFacade.obterAtestadosDaConsultaComCid(getConsultaSelecionada(), parametro.shortValue());
			atendimento = aghuFacade.obterAtendimentoPorConsulta(this.consultaSelecionada.getNumero());
			montarDeclaracao1();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void montarDeclaracao1(){
		StringBuilder retorno = new StringBuilder(100);
		retorno.append(consultaSelecionada.getPaciente().getNome());
		if(atendimento!=null) {
			retorno.append(", prontuário ").append(atendimento.getProntuario());
		}
		declaracao1 = retorno.toString();
	}
	
	//sb1
	public List<AghCid> obterListaAghCid(String filtro) {
		return this.returnSGWithCount(aghuFacade.pesquisarCidPorCodDescricaoPorSexo(filtro, paciente.getSexoBiologico()), 
				aghuFacade.pesquisarCidPorCodDescricaoPorSexoCount(filtro, paciente.getSexoBiologico()));
	}	
	
	public void gravar(){
		
			try {
				ambulatorioFacade.validarCamposPreenchidosAtestadoMedico(atestado);
				ambulatorioFacade.validarValorMinimoNumeroVias(atestado);
				ambulatorioFacade.validarDatas(atestado.getDataInicial(), atestado.getDataFinal());
				
				boolean novo = atestado.getSeq() == null ? true : false; 
				if(atestado.getSeq() == null){
					atestado.setConsulta(consultaSelecionada);
					atestado.setAipPacientes(consultaSelecionada.getPaciente());
					atestado.setServidor(servidorLogadoFacade.obterServidorLogado());
					atestado.setIndPendente(DominioIndPendenteAmbulatorio.P);
					atestado.setIndImpresso(Boolean.FALSE);
					atestado.setDthrCriacao(new Date());
					atestado.setMamTipoAtestado(tipoAtestado);
					atestado.setDthrValida(new Date());
					atestado.setServidorValida(servidorLogadoFacade.obterServidorLogado());
				}
				
				ambulatorioFacade.gravarAtestado(atestado); 
				
				limpar();
				listAtestadosMedicos = ambulatorioFacade.obterAtestadosDaConsultaComCid(getConsultaSelecionada(), parametro.shortValue());
				
				if(novo){
					apresentarMsgNegocio(Severity.INFO, "MSG_ATESTADO_CADASTRADO_SUCESSO");
				} else{
					apresentarMsgNegocio(Severity.INFO, "MSG_ATESTADO_ALTERADO_SUCESSO");
				}
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
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
	}
	
	public void editar(){
		modoEdicao = Boolean.TRUE;
	}
	
	public void excluir(){
		try {
			ambulatorioFacade.excluirAtestadoComparecimento(itemSelecionado);
			apresentarMsgNegocio(Severity.INFO, "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_EXCLUIDO");
			listAtestadosMedicos = ambulatorioFacade.obterAtestadosDaConsultaComCid(getConsultaSelecionada(), parametro.shortValue());
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

	public String getDeclaracao1() {
		return declaracao1;
	}

	public void setDeclaracao1(String declaracao1) {
		this.declaracao1 = declaracao1;
	}

	public List<MamAtestados> getListAtestadosMedicos() {
		return listAtestadosMedicos;
	}

	public void setListAtestadosMedicos(List<MamAtestados> listAtestadosMedicos) {
		this.listAtestadosMedicos = listAtestadosMedicos;
	}

}
