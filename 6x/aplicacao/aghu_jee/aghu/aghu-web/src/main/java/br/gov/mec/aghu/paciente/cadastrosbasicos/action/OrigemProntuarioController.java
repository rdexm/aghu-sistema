package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class OrigemProntuarioController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(OrigemProntuarioController.class);

	private static final long serialVersionUID = 2798834316260104757L;
	
	private static final String ORIGEM_PRONTUARIO_LIST = "origemProntuarioList";
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject
	private OrigemProntuarioPaginatorController origemProntuarioPaginatorController;
	
	private Short seq;
	private AghSamis samisOrigemProntuario;
	
	public Short getSeq() {
		return seq;
	}


	public void setSeq(Short seq) {
		this.seq = seq;
	}


	public AghSamis getSamisOrigemProntuario() {
		return samisOrigemProntuario;
	}


	public void setSamisOrigemProntuario(AghSamis samisOrigemProntuario) {
		this.samisOrigemProntuario = samisOrigemProntuario;
	}
	
	public String confirmar() {
		try {
			origemProntuarioPaginatorController.getDataModel().reiniciarPaginator();

			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			
			this.cadastrosBasicosPacienteFacade.persistirOrigemProntuario(this.samisOrigemProntuario, servidorLogado);

			if (this.seq == null) {

				// Inclusão
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ORIGEM_PRONTUARIO",
						this.samisOrigemProntuario.getDescricao());
			} else {
				// Alteração
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_ORIGEM_PRONTUARIO",
						this.samisOrigemProntuario.getDescricao());
			}
			limpar();
			return ORIGEM_PRONTUARIO_LIST;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

	}
	
	public void inicio() {
		
		if (this.seq != null) {
			this.samisOrigemProntuario = this.cadastrosBasicosPacienteFacade
					.obterOrigemProntuario(seq);
		}

		if (this.samisOrigemProntuario == null) {
			this.samisOrigemProntuario = new AghSamis();
			this.samisOrigemProntuario.setAtivo(true);
		}
	}

	
	public void iniciarEdicao(AghSamis selecionado){
		this.setSeq(selecionado.getCodigo());
	}
	
	public String cancelar() {
		LOG.info("Cancelado");
		limpar();
		return ORIGEM_PRONTUARIO_LIST;
	}


	private void limpar() {
		this.samisOrigemProntuario = new AghSamis();
		this.seq = null;
	}
	

}