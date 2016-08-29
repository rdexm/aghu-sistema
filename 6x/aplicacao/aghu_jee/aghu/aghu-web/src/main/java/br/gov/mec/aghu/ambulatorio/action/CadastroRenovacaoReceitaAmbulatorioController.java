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

public class CadastroRenovacaoReceitaAmbulatorioController extends ActionController{
	


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
	
	private AacConsultas consultaSelecionada;
	private List<MamAtestados> listAtestadosRenovacaoReceita;
	private MamAtestados atestado = new MamAtestados();
	private AghAtendimentos atendimento;
 	private Boolean modoEdicao =  Boolean.FALSE;
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
			atestado.setPeriodo(Byte.valueOf("1"));
			parametro = ambulatorioFacade.obterParametroRenovacaoReceita();
			tipoAtestado = ambulatorioFacade.obterTipoAtestadoOriginal(parametro.shortValue());
			listAtestadosRenovacaoReceita = ambulatorioFacade.obterAtestadosDaConsultaComCid(getConsultaSelecionada(), parametro.shortValue());
			atendimento = aghuFacade.obterAtendimentoPorConsulta(this.consultaSelecionada.getNumero());
			declaracao1 = montarDeclaracao1();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private String montarDeclaracao1(){
		if(atendimento!=null) {
			return getBundle().getString("LABEL_DECLARACAO_1")
					.concat(" ").concat(consultaSelecionada.getPaciente().getNome())
					.concat(", prontuário ")
					.concat(atendimento.getProntuario().toString()).concat(", ");
		}
		else {
			return getBundle().getString("LABEL_DECLARACAO_1")
					.concat(" ").concat(consultaSelecionada.getPaciente().getNome());
		}
	}
	
	public String montarDescricao(MamAtestados item){
		StringBuilder retorno = new StringBuilder(100); 
				retorno.append(declaracao1)
				.append(getBundle().getString("LABEL_PORTADOR_PATOLOGIA"));
				if(item.getAghCid() != null){
					retorno.append(item.getAghCid().getCodigo());
					retorno.append(" - ");
					retorno.append(item.getAghCid().getDescricao());
				}
				retorno.append(". ")
				.append(getBundle().getString("LABEL_RECOMENDACAO_MEDICAMENTO"));
				if(item.getPeriodo() != null ){
					retorno.append(item.getPeriodo().toString());
					if(item.getPeriodo() != null && item.getPeriodo() == 1){
						retorno.append(" mês. ");
						
					}else if(item.getPeriodo() != null && item.getPeriodo() > 1){
						retorno.append(" meses. ");
					}
				}
				if(item.getObservacao() != null){
					retorno.append(item.getObservacao());
				}
				
		return retorno.toString();
	}
	
	//sb1
	public List<AghCid> obterListaAghCid(String filtro) {
		return this.returnSGWithCount(aghuFacade.pesquisarCidPorCodDescricaoPorSexo(filtro, paciente.getSexoBiologico()), 
				aghuFacade.pesquisarCidPorCodDescricaoPorSexoCount(filtro, paciente.getSexoBiologico()));
	}	
	
	public void gravar(){
		
			try {				
				ambulatorioFacade.validarCamposPreenchidosRenovacaoReceita(atestado);
				
				boolean valorMinimo, valorMinimoPeriodo;
				valorMinimo = ambulatorioFacade.validarValorMinimo(atestado);
				valorMinimoPeriodo = ambulatorioFacade.validarValorMinimoPeriodo(atestado);
				
				
				if(!valorMinimo || !valorMinimoPeriodo){
					if(!valorMinimo){
						apresentarMsgNegocio(Severity.ERROR, "MSG_VALOR_MINIMO_1");
					}
					if(!valorMinimoPeriodo){
						apresentarMsgNegocio(Severity.ERROR, "MSG_VALOR_MINIMO_MESES");
					}
					return;
				}
				
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
					atestado.setServidorValida(servidorLogadoFacade.obterServidorLogado());
				}
				
				ambulatorioFacade.gravarAtestado(atestado); 
				
				limpar();
				listAtestadosRenovacaoReceita = ambulatorioFacade.obterAtestadosDaConsultaComCid(getConsultaSelecionada(), parametro.shortValue());
				
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
		atestado.setPeriodo(Byte.valueOf("1"));
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
			listAtestadosRenovacaoReceita = ambulatorioFacade.obterAtestadosDaConsultaComCid(getConsultaSelecionada(), parametro.shortValue());
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

	public List<MamAtestados> getListAtestadosRenovacaoReceita() {
		return listAtestadosRenovacaoReceita;
	}

	public void setListAtestadosRenovacaoReceita(
			List<MamAtestados> listAtestadosRenovacaoReceita) {
		this.listAtestadosRenovacaoReceita = listAtestadosRenovacaoReceita;
	}

}
