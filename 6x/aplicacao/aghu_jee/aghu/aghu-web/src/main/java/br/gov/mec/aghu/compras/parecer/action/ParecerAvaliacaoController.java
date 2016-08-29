package br.gov.mec.aghu.compras.parecer.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.parecer.business.IParecerFacade;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParecerAvalConsul;
import br.gov.mec.aghu.model.ScoParecerAvalDesemp;
import br.gov.mec.aghu.model.ScoParecerAvalTecnica;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de Parecer
 * 
 */
public class ParecerAvaliacaoController extends ActionController {

	private static final String SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";

	private static final long serialVersionUID = 0L;

	@EJB
	protected IComprasFacade comprasFacade;	
	
	@EJB
	protected IParecerFacade parecerFacade;	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private ScoParecerMaterial  parecerMaterial = new ScoParecerMaterial();
	private ScoParecerAvaliacao  parecerAvaliacao = new ScoParecerAvaliacao();
	private ScoParecerAvalTecnica  parecerAvalTecnica = new ScoParecerAvalTecnica();
	private ScoParecerAvalConsul  parecerAvalConsul = new ScoParecerAvalConsul();
	private ScoParecerAvalDesemp  parecerAvalDesemp = new ScoParecerAvalDesemp();
	
	private Integer codigo;
	private Integer codigoParecer;
	private Boolean modoEdit;
	private String voltarParaUrl;
	
	private static final String  PAGE_CONSULTA_PARECER = "consultarParecer";

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
		this.setParecerAvaliacao(new ScoParecerAvaliacao());		
		this.setParecerAvalTecnica(new ScoParecerAvalTecnica());
		this.setParecerAvalConsul(new ScoParecerAvalConsul()); 
		this.setParecerAvalDesemp(new ScoParecerAvalDesemp());
        this.setParecerMaterial(this.getParecerMaterial() == null ? new ScoParecerMaterial(): this.getParecerMaterial());
        this.getParecerAvaliacao().setParecerGeral(DominioParecer.EA);
        carregarDadosInicio();
	}

    private void carregarDadosInicio() {


        if (this.getCodigo() != null){
            this.setParecerAvaliacao(this.parecerFacade.obterParecerAvaliacaoPorCodigo(this.codigo));

            if (this.getParecerAvaliacao() != null){

                ScoParecerAvalTecnica scoParecerAvalTecnica = this.parecerFacade.obterParecerAvalTecnicaPorAvaliacao(this.getParecerAvaliacao());
                ScoParecerAvalConsul scoParecerAvalConsul = this.parecerFacade.obterParecerAvalConsulPorAvaliacao(this.getParecerAvaliacao());
                ScoParecerAvalDesemp scoParecerAvalDesemp = this.parecerFacade.obterParecerAvalDesempPorAvaliacao(this.getParecerAvaliacao());

                this.setParecerAvalTecnica(scoParecerAvalTecnica !=null ? scoParecerAvalTecnica : new ScoParecerAvalTecnica());
                this.setParecerAvalConsul(scoParecerAvalConsul != null ? scoParecerAvalConsul : new ScoParecerAvalConsul());
                this.setParecerAvalDesemp( scoParecerAvalDesemp != null ? scoParecerAvalDesemp : new ScoParecerAvalDesemp());
                if (this.getParecerAvaliacao().getParecerMaterial() != null) {
                    this.setCodigoParecer(this.getParecerAvaliacao().getParecerMaterial().getCodigo());
                }
            }

        }

        if (this.getCodigoParecer() != null){
            this.setParecerMaterial(this.parecerFacade.obterParecer(this
                    .getCodigoParecer()));
        }
    }


    public boolean desabilitarParecerFinal(){
		if(this.getParecerAvaliacao()!=null && this.getParecerAvaliacao().getParecerGeral()!=null && this.getParecerAvaliacao().getParecerGeral().equals(DominioParecer.PF)){
			return true;
		} else if(this.getParecerAvaliacao()!=null && this.getParecerAvaliacao().getParecerGeral()!=null && this.getParecerAvaliacao().getParecerGeral().equals(DominioParecer.PD)){
			return true;
		} else{
			return false;
		}		
	}

	public String gravar() {

		try {
			this.getParecerAvaliacao().setParecerMaterial(parecerMaterial);
			this.parecerFacade.persistirParecerAvaliacao(
					this.getParecerAvaliacao(), this.getParecerAvalTecnica(),
					this.getParecerAvalConsul(), this.getParecerAvalDesemp());
			this.setCodigo(this.parecerAvaliacao.getCodigo());
			this.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_PARECER_AVALIACAO_SUCESSO_M04");
		if (StringUtils.isNotBlank(cancelar())){
			return cancelar();
		}
		return PAGE_CONSULTA_PARECER;
	}
	
	public String cancelar() {	
		return this.voltarParaUrl;
	}	
	
	public Boolean desabilitarParecerAvalTecnica(){
			if (this.getParecerAvalTecnica().getParecer() == null){
				this.getParecerAvalTecnica().setDtAvaliacao(null);
				this.getParecerAvalTecnica().setServidorAvaliacao(null);
				this.getParecerAvalTecnica().setDescricao(null);
			}
			else {
				if (this.getParecerAvalTecnica().getDtAvaliacao() == null) {
				   this.getParecerAvalTecnica().setDtAvaliacao(new Date());
				}
				
                setPesoaFisicaToServidor();
			}

            Boolean result = Boolean.FALSE;
            if (this.getParecerAvalTecnica() == null  || this.getParecerAvalTecnica().getParecer() == null){
                result = Boolean.TRUE;
            }
			
			return result;
	}

    public Short getVinCodigo() {
        if(parecerAvalTecnica != null && parecerAvalTecnica.getServidorAvaliacao() != null && parecerAvalTecnica.getServidorAvaliacao().getId() != null) {
            return parecerAvalTecnica.getServidorAvaliacao().getId().getVinCodigo();
        }else{
            return null;
        }
    }
	
	
    public Boolean desabilitarParecerAvalConsul(){
		
		if (this.getParecerAvalConsul().getParecer() == null){
			this.getParecerAvalConsul().setDtAvaliacao(null);
			this.getParecerAvalConsul().setServidorAvaliacao(null);
			this.getParecerAvalConsul().setDescricao(null);
		}
		else {
			if (this.getParecerAvalConsul().getDtAvaliacao() == null){
			    this.getParecerAvalConsul().setDtAvaliacao(new Date());
			}
			if (this.getParecerAvalConsul().getServidorAvaliacao() == null){
			    this.getParecerAvalConsul().setServidorAvaliacao(this.servidorLogadoFacade.obterServidorLogado());
			}
		}
		
		return (this.getParecerAvalConsul().getParecer() == null);
		
	}
	
	public Boolean desabilitarParecerAvalDesemp(){
		
		if (this.getParecerAvalDesemp().getParecer() == null){
			this.getParecerAvalDesemp().setDtAvaliacao(null);
			this.getParecerAvalDesemp().setServidorAvaliacao(null);
			this.getParecerAvalDesemp().setDescricao(null);
		}
		else {
			if (this.getParecerAvalDesemp().getDtAvaliacao() == null){
			    this.getParecerAvalDesemp().setDtAvaliacao(new Date());
			}
			if (this.getParecerAvalDesemp().getServidorAvaliacao() == null){
			    this.getParecerAvalDesemp().setServidorAvaliacao(this.servidorLogadoFacade.obterServidorLogado());
			}
		}
		
		return (this.getParecerAvalDesemp().getParecer() == null);
		
	}
	
	// Metódo para Suggestion Box de Servidor
		public List<RapServidores> listaServidores(String objPesquisa) {

			try {
				return this.registroColaboradorFacade
						.pesquisarServidoresVinculados(objPesquisa);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			return new ArrayList<RapServidores>();
		}

    public void onParecerAvaliacaoTecnicaSelected(){
        setPesoaFisicaToServidor();
    }

    public void onParecerAvaliacaoConsultaSelected(){
        if (parecerAvalConsul != null && parecerAvalConsul.getServidorAvaliacao() == null) {
            parecerAvalConsul.setServidorAvaliacao(this.servidorLogadoFacade.obterServidorLogado());
        }
        if (parecerAvalConsul != null && parecerAvalConsul.getServidorAvaliacao() != null) {
            String login = parecerAvalConsul.getServidorAvaliacao().getUsuario();
            RapServidores servidor = registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(login);
            parecerAvalConsul.setServidorAvaliacao(servidor);
        }
    }

    public void onParecerParecerAvalDesempSelected(){
        if (parecerAvalDesemp != null && parecerAvalDesemp.getServidorAvaliacao() == null) {
            parecerAvalDesemp.setServidorAvaliacao(this.servidorLogadoFacade.obterServidorLogado());
        }
        if (parecerAvalDesemp != null && parecerAvalDesemp.getServidorAvaliacao() != null) {
            String login = parecerAvalDesemp.getServidorAvaliacao().getUsuario();
            RapServidores servidor = registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(login);
            parecerAvalDesemp.setServidorAvaliacao(servidor);
        }
    }
	
	
		
	public String redirecionaAnexosAvaliacaoTecnica(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	} 
	
	
	public String redirecionaAnexosAvaliacaoConsultoria(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	} 
	
	public String redirecionaAnexosAvaliacaoDesempenho(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	} 		
		
	
	// ### GETs e SETs ###	
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public ScoParecerMaterial getParecerMaterial() {
		return parecerMaterial;
	}

	public void setParecerMaterial(ScoParecerMaterial parecerMaterial) {
		this.parecerMaterial = parecerMaterial;
	}

	public ScoParecerAvaliacao getParecerAvaliacao() {
		return parecerAvaliacao;
	}



	public void setParecerAvaliacao(ScoParecerAvaliacao parecerAvaliacao) {
		this.parecerAvaliacao = parecerAvaliacao;
	}



	public ScoParecerAvalTecnica getParecerAvalTecnica() {
        return parecerAvalTecnica;
	}

    private void setPesoaFisicaToServidor() {

        if (parecerAvalTecnica != null && parecerAvalTecnica.getServidorAvaliacao() == null) {
            parecerAvalTecnica.setServidorAvaliacao(this.servidorLogadoFacade.obterServidorLogado());
        }

        if (parecerAvalTecnica != null && parecerAvalTecnica.getServidorAvaliacao() != null) {
        	   parecerAvalTecnica.setServidorAvaliacao(registroColaboradorFacade.obterRapServidor(parecerAvalTecnica.getServidorAvaliacao().getId()));
               String login = parecerAvalTecnica.getServidorAvaliacao().getUsuario();
               RapServidores servidor = registroColaboradorFacade.obterServidorComPessoaFisicaByUsuario(login);
               parecerAvalTecnica.setServidorAvaliacao(servidor);
        }
    }


    public void setParecerAvalTecnica(ScoParecerAvalTecnica parecerAvalTecnica) {
		this.parecerAvalTecnica = parecerAvalTecnica;
	}



	public ScoParecerAvalConsul getParecerAvalConsul() {
		return parecerAvalConsul;
	}



	public void setParecerAvalConsul(ScoParecerAvalConsul parecerAvalConsul) {
		this.parecerAvalConsul = parecerAvalConsul;
	}



	public ScoParecerAvalDesemp getParecerAvalDesemp() {
		return parecerAvalDesemp;
	}



	public void setParecerAvalDesemp(ScoParecerAvalDesemp parecerAvalDesemp) {
		this.parecerAvalDesemp = parecerAvalDesemp;
	}



	public Boolean getModoEdit() {
		return modoEdit;
	}



	public void setModoEdit(Boolean modoEdit) {
		this.modoEdit = modoEdit;
	}



	public Integer getCodigoParecer() {
		return codigoParecer;
	}



	public void setCodigoParecer(Integer codigoParecer) {
		this.codigoParecer = codigoParecer;
	}


	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}


	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

}
