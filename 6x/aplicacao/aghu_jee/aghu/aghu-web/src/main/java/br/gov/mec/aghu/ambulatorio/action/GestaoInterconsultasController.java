package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioAvaliacaoInterconsulta;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GestaoInterconsultasController extends ActionController {

    private static final long serialVersionUID = -6296939495016946133L;
    
    private static final String PAGE_PESQUISA_INTERCONSULTAS = "ambulatorio-gestaoInterconsultas";
    
    private MamInterconsultas mamInterconsultas;
    
    private AghEspecialidades agenda;
    
    private String nomePaciente;
    
    private Integer prontuario;
    
    private String prioridade;
    
    private Integer interconsulta;
    
    private Integer consultaRetorno;
    
    private boolean dataDesabilitado;
    
    private boolean agendaDesabilitado;
    
    @EJB
    private IAmbulatorioFacade ambulatorioFacade;
    
    @Inject
    PesquisarInterconsultasPaginatorController pesquisarInterconsultasPaginatorController;
    
    @PostConstruct
    protected void inicializar() {
                   this.begin(conversation);
    }
    
    /**
    * Método chamado na iniciação da tela.
    */
    public void iniciar() {
                   
		iniciarCamposTela();
       
       //Habilitação campo data.
       if (mamInterconsultas.getDigitado().equals(DominioSimNao.S.toString())) {
                       dataDesabilitado = false;
       } else {
                       dataDesabilitado = true;
       }
       
       //Habilitação SuggestionBox agenda.
       if (mamInterconsultas.getDthrAvaliacao() != null || mamInterconsultas.getAvaliacao().equals(DominioAvaliacaoInterconsulta.N)) {
                       agendaDesabilitado = true;
       } else {
                       agendaDesabilitado = false;
       }
                   
    }

	private void iniciarCamposTela() {
		if (mamInterconsultas.getPaciente() != null) {
			nomePaciente = mamInterconsultas.getPaciente().getNome();
			prontuario = mamInterconsultas.getPaciente().getProntuario();
		}

		if (mamInterconsultas.getPrioridadeAprovada() != null) {
			prioridade = mamInterconsultas.getPrioridadeAprovada()
					.getDescricao();
		}

		agenda = mamInterconsultas.getEspecialidadeAdm();

		if (mamInterconsultas.getConsultaMarcada() != null) {
			interconsulta = mamInterconsultas.getConsultaMarcada().getNumero();
		} else {
			interconsulta = null;
		}

		if (mamInterconsultas.getConsultaRetorno() != null) {
			consultaRetorno = mamInterconsultas.getConsultaRetorno()
					.getNumero();
		} else {
			consultaRetorno = null;
		}
	}
    
    /**
    * Ação do botão gravar.
    */
    public void gravar() {

       mamInterconsultas.setEspecialidadeAdm(agenda);
       
       if (interconsulta != null) {
           mamInterconsultas.setConsultaMarcada(new AacConsultas());
           mamInterconsultas.getConsultaMarcada().setNumero(interconsulta);
       } else {
           mamInterconsultas.setConsultaMarcada(null);
       }
       
       if (consultaRetorno != null) {
           mamInterconsultas.setConsultaRetorno(new AacConsultas());
           mamInterconsultas.getConsultaRetorno().setNumero(consultaRetorno);
       } else {
            mamInterconsultas.setConsultaRetorno(null);
       }
       
       try {
                       
	       ambulatorioFacade.gravarInterconsultas(mamInterconsultas);
	       
	       this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_GESTAO_INTERCONSULTA");
                       
       } catch(ApplicationBusinessException e) {
               apresentarExcecaoNegocio(e);
       }
    }
    
    /**
    * Método que realiza a ação do botão voltar.
    */
    public String voltar() {
    	pesquisarInterconsultasPaginatorController.getDataModel().reiniciarPaginator();
        return PAGE_PESQUISA_INTERCONSULTAS;
    }
    
    /**
    * Método usado no suggestionBox de Agenda.
    * @param parametro
    * @return Lista de especialidades.
    */
    public List<AghEspecialidades> pesquisarAgenda(String parametro) {
         return  this.returnSGWithCount(this.ambulatorioFacade.pesquisarPorSiglaOuNomeEspecialidade(parametro),pesquisarAgendaCount(parametro));
    }
    
    /**
    * Método usado no suggestionBox de Agenda.
    * @param parametro
    * @return Quantidade.
    */
    public Long pesquisarAgendaCount(String parametro) {
          return this.ambulatorioFacade.pesquisarPorSiglaOuNomeEspecialidadeCount(parametro);
    }

    
    //GETs e SETs
    
    public MamInterconsultas getMamInterconsultas() {
         return mamInterconsultas;
    }

    public void setMamInterconsultas(MamInterconsultas mamInterconsultas) {
         this.mamInterconsultas = mamInterconsultas;
    }

    public AghEspecialidades getAgenda() {
         return agenda;
    }

    public void setAgenda(AghEspecialidades agenda) {
         this.agenda = agenda;
    }

    public String getNomePaciente() {
         return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
         this.nomePaciente = nomePaciente;
    }

    public Integer getProntuario() {
         return prontuario;
    }

    public void setProntuario(Integer prontuario) {
         this.prontuario = prontuario;
    }

    public String getPrioridade() {
         return prioridade;
    }

    public void setPrioridade(String prioridade) {
         this.prioridade = prioridade;
    }

    public Integer getInterconsulta() {
         return interconsulta;
    }

    public void setInterconsulta(Integer interconsulta) {
         this.interconsulta = interconsulta;
    }

    public Integer getConsultaRetorno() {
         return consultaRetorno;
    }

    public void setConsultaRetorno(Integer consultaRetorno) {
         this.consultaRetorno = consultaRetorno;
    }

    public boolean isDataDesabilitado() {
          return dataDesabilitado;
    }

    public void setDataDesabilitado(boolean dataDesabilitado) {
          this.dataDesabilitado = dataDesabilitado;
    }

    public boolean isAgendaDesabilitado() {
           return agendaDesabilitado;
    }

    public void setAgendaDesabilitado(boolean agendaDesabilitado) {
           this.agendaDesabilitado = agendaDesabilitado;
    }
                
}



