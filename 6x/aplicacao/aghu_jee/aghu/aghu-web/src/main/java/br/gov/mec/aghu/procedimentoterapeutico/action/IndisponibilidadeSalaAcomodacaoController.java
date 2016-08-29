package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.MptBloqueio;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class IndisponibilidadeSalaAcomodacaoController extends ActionController{

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	/**
	 * 
	 */
	private static final long serialVersionUID = 3084446424679457763L;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

	@EJB
	private IServidorLogadoFacade servidorFacade;

	private MptBloqueio bloqueio = new MptBloqueio();
	
	private MptJustificativa justificativa;
	
	private List<MptTipoSessao> listaTipoSessao = null;
	
	private List<MptLocalAtendimento> listaAcomodacoes = null;
	
	private List<MptSalas> listaSalas = null;
	
	private String voltarPara;

	private Boolean modoEdicao;
	
	private static final String INDISPONIBILIDADE_LIST = "indisponibilidadeSalaOuAcomodacaoList";
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@PostConstruct
	public void inicializar(){
		begin(conversation);
	}
			
	/**
	 * Valida se a tela foi acessada para edição ou inclusão de um registro
	 */
	public void iniciar() {
		
		if (!this.modoEdicao){//se inclusão			
			limparInclusao();
			carregarCombos();
		}else{
			carregarCombos();
			selecionarSala();
			selecionarAcomodacao();
		}
	}
	
	/**
	 * Pesquisar
	 */
	private void carregarCombos() {
		listaTipoSessao = procedimentoTerapeuticoFacade.buscarTipoSessao();
	}
	
	public void selecionarSala(){
		if (bloqueio.getTipoSessao() != null && bloqueio.getTipoSessao().getSeq() != null){
			if (!this.modoEdicao){
				bloqueio.setSala(new MptSalas());
				listaSalas = new ArrayList<MptSalas>();
				bloqueio.setLocalAtendimento(new MptLocalAtendimento());
				listaAcomodacoes = null;				
			}
			listaSalas = procedimentoTerapeuticoFacade.buscarSala(bloqueio.getTipoSessao().getSeq());
			if (listaSalas.isEmpty()){
				bloqueio.setSala(new MptSalas());
				listaSalas = null;
				bloqueio.setLocalAtendimento(new MptLocalAtendimento());
				listaAcomodacoes = null;					
			}
		}else{
			bloqueio.setSala(new MptSalas());
			listaSalas = null;
			bloqueio.setLocalAtendimento(new MptLocalAtendimento());
			listaAcomodacoes = null;
		}
	}

	public void selecionarAcomodacao(){
		if (bloqueio.getSala() != null && bloqueio.getSala().getSeq() != null){
			listaAcomodacoes = procedimentoTerapeuticoFacade.buscarLocalAtendimento(bloqueio.getSala().getSeq());
		}else{
			bloqueio.setLocalAtendimento(new MptLocalAtendimento());
			listaAcomodacoes = null;
		}
	}
	
	public void limparInclusao(){
		this.bloqueio = new MptBloqueio();
		limparCamposBloqueio();
	}
	
	private void limparCamposBloqueio(){
		bloqueio.setTipoSessao(new MptTipoSessao());
		bloqueio.setSala(new MptSalas());
		bloqueio.setLocalAtendimento(new MptLocalAtendimento());
		bloqueio.setCriadoEm(null);
		bloqueio.setApartirDe(null);
		bloqueio.setAte(null);
	}
	
	/**
	 * Ação Gravar
	 */
	public String gravar() {
		String retorno = null;
		
		try {
			
			if(!validarCampos()){		
				if (this.bloqueio!=null && !estoqueFacade.dataValida(bloqueio.getApartirDe(), bloqueio.getAte())) {				
					throw new ApplicationBusinessException("ERRO_DATA_INICIAL_MENOR_QUE_FINAL", Severity.ERROR,"LABEL_DATA_E_HORA_INICIO");
				}
				
				this.bloqueio.setServidor(servidorFacade.obterServidorLogado());
				if(!modoEdicao){
					bloqueio.setCriadoEm(new Date());	
					if(bloqueio != null && bloqueio.getLocalAtendimento() == null){
						for (MptLocalAtendimento mptLocalAtendimento : listaAcomodacoes) {
							this.bloqueio.setLocalAtendimento(mptLocalAtendimento);
							MptBloqueio clonBloq = (MptBloqueio) bloqueio.clone();
							this.procedimentoTerapeuticoFacade.persistirMptBloqueio(clonBloq);
						}
					}else{
						this.procedimentoTerapeuticoFacade.persistirMptBloqueio(bloqueio);
					}
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRAVAR_SUCESSO_INDISPONIBILIDADE", bloqueio.getDescricao());
				}else{
					this.procedimentoTerapeuticoFacade.atualizarMptBloqueio(bloqueio); 
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATUALIZAR_SUCESSO_INDISPONIBILIDADE", bloqueio.getDescricao());
				}
				
				limparInclusao();
				
				retorno = INDISPONIBILIDADE_LIST;
			}else{
				return null;
			}
			
		} catch (BaseException e) {
			apresentarMsgNegocio(e.getParameters()[0].toString(), e.getSeverity(), e.getMessage(), e.getParameters()[0].toString());
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(e.getMessage());
		}
		return retorno;
	}

	private boolean validarCampos() {
		boolean campoObrigatorio = Boolean.FALSE;
		
		if(bloqueio != null && bloqueio.getTipoSessao() == null){
			campoObrigatorio = Boolean.TRUE;
			this.apresentarMsgNegocio(Severity.WARN, CAMPO_OBRIGATORIO, "Tipo de Sessão");
		}
		if(bloqueio != null && bloqueio.getSala() == null){
			campoObrigatorio = Boolean.TRUE;
			this.apresentarMsgNegocio(Severity.WARN, CAMPO_OBRIGATORIO, "Sala");
		}
		if(bloqueio != null && bloqueio.getApartirDe() == null){
			campoObrigatorio = Boolean.TRUE;
			this.apresentarMsgNegocio(Severity.WARN, CAMPO_OBRIGATORIO, "Data e Hora Inicial");
		}
		if(bloqueio != null && bloqueio.getAte() == null){
			campoObrigatorio = Boolean.TRUE;
			this.apresentarMsgNegocio(Severity.WARN, CAMPO_OBRIGATORIO, "Data e Hora Final");
		}
		if(bloqueio != null && bloqueio.getJustificativa() == null){
			campoObrigatorio = Boolean.TRUE;
			this.apresentarMsgNegocio(Severity.WARN, CAMPO_OBRIGATORIO, "Motivo");				
		}
		return campoObrigatorio;
	}
	
	//Suggestion Justificativa
	public List<MptJustificativa> pesquisarJustificativa(String filtro){
		return  this.returnSGWithCount(procedimentoTerapeuticoFacade.buscarJustificativa(filtro), pesquisarCCLotacaoCount(filtro));
	}

	public Long pesquisarCCLotacaoCount(String filtro) {
		return procedimentoTerapeuticoFacade.buscarJustificativaCount(filtro);
	}

	/**
	 * Método que realiza a ação do botão voltar
	 */
	public String voltar() {
		limparInclusao();
		return INDISPONIBILIDADE_LIST;
	}

	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}

	public void setProcedimentoTerapeuticoFacade(
			IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}

	public MptBloqueio getBloqueio() {
		return bloqueio;
	}

	public void setBloqueio(MptBloqueio bloqueio) {
		this.bloqueio = bloqueio;
	}

	public MptJustificativa getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(MptJustificativa justificativa) {
		this.justificativa = justificativa;
	}

	public List<MptTipoSessao> getListaTipoSessao() {
		return listaTipoSessao;
	}

	public void setListaTipoSessao(List<MptTipoSessao> listaTipoSessao) {
		this.listaTipoSessao = listaTipoSessao;
	}

	public List<MptLocalAtendimento> getListaAcomodacoes() {
		return listaAcomodacoes;
	}

	public void setListaAcomodacoes(List<MptLocalAtendimento> listaAcomodacoes) {
		this.listaAcomodacoes = listaAcomodacoes;
	}

	public List<MptSalas> getListaSalas() {
		return listaSalas;
	}

	public void setListaSalas(List<MptSalas> listaSalas) {
		this.listaSalas = listaSalas;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

//	public Boolean getSalaAtivo() {
//		return salaAtivo;
//	}
//
//	public void setSalaAtivo(Boolean salaAtivo) {
//		this.salaAtivo = salaAtivo;
//	}
//
//	public Boolean getAcomodacaoAtivo() {
//		return acomodacaoAtivo;
//	}
//
//	public void setAcomodacaoAtivo(Boolean acomodacaoAtivo) {
//		this.acomodacaoAtivo = acomodacaoAtivo;
//	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}	
}
