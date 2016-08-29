package br.gov.mec.aghu.paciente.portalpaciente.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class PacienteInternadoAbaAnamneseController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4217045884952333260L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private Integer paramCategoria;
	private String textAreaAnamnese;
	private AghAtendimentos atendimento;
	private Date data;
	private List<MamRegistro> mamRegistros;
	private Integer indiceAtual;
	private Boolean consultaRealizada = Boolean.FALSE;

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void iniciar() {
		this.textAreaAnamnese = null;
		
		if (this.atendimento != null && !this.consultaRealizada) {
			this.indiceAtual = 0;

			// Executar a consulta C1, usando a atendimento obtido da consulta C3 (fornecido pela tela principal). Para cada registro retornado, executar o procedimento P1. 
			// O retorno deve ser adicionado ao texto a ser apresentado.
			this.mamRegistros = ambulatorioFacade.obterRegistroAnamnesePorAtendSeqCriadoEm(this.atendimento.getSeq(), getData());
			this.consultaRealizada = Boolean.TRUE;
			
			if (!this.mamRegistros.isEmpty()){
				this.carregarTextoAnamnese();
			}
		}
	}

	private void carregarTextoAnamnese() {
		try{

			this.textAreaAnamnese = ambulatorioFacade.obterAnamnesePorMamRegistroSeq(this.mamRegistros.get(indiceAtual).getSeq(), this.paramCategoria);
		
		}catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void moverProximo(){
		this.indiceAtual--;
		this.carregarTextoAnamnese();
	}
	
	public void moverAnterior(){
		this.indiceAtual++;
		this.carregarTextoAnamnese();
	}
	
	public Boolean existeProximo(){
		if (this.mamRegistros != null && !this.mamRegistros.isEmpty()){
			return this.indiceAtual > 0;
		} else {
			return Boolean.FALSE;
		}
	}

	public Boolean existeAnterior(){
		if (this.mamRegistros != null && !this.mamRegistros.isEmpty()){
			return (this.indiceAtual + 1) < this.mamRegistros.size();
		} else {
			return Boolean.FALSE;
		}
	}

	public void limpar(){
		this.consultaRealizada = Boolean.FALSE;
		this.textAreaAnamnese = null;
		this.mamRegistros = null;
		this.indiceAtual = 0;
		this.atendimento = null;
		this.data = null;
		this.paramCategoria = null;
	}

	public Integer getParamCategoria() {
		return paramCategoria;
	}

	public void setParamCategoria(Integer paramCategoria) {
		this.paramCategoria = paramCategoria;
	}

	public String getTextAreaAnamnese() {
		return textAreaAnamnese;
	}

	public void setTextAreaAnamnese(String textAreaAnamnese) {
		this.textAreaAnamnese = textAreaAnamnese;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	public List<MamRegistro> getMamRegistros() {
		return mamRegistros;
	}

	public void setMamRegistros(List<MamRegistro> mamRegistros) {
		this.mamRegistros = mamRegistros;
	}

	public Integer getIndiceAtual() {
		return indiceAtual;
	}

	public void setIndiceAtual(Integer indiceAtual) {
		this.indiceAtual = indiceAtual;
	}

	public Boolean getConsultaRealizada() {
		return consultaRealizada;
	}

	public void setConsultaRealizada(Boolean consultaRealizada) {
		this.consultaRealizada = consultaRealizada;
	}
}
