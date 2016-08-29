package br.gov.mec.aghu.prescricaomedica.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class AprovacaoEmLoteController extends ActionController {
	
	private static final long serialVersionUID = 7827355644506948209L;

	private static final String MS01 = "AVALIACAO_MDTO_LOTE_USU_NAO_ENCONTRADO";
	private static final String MS02 = "AVALIACAO_MDTO_LOTE_SUCESSO";
	private static final String MS03 = "AVALIACAO_MDTO_LOTE_ERRO";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private DominioIndRespAvaliacao respAvaliacao;
	private String nomeProfissional;
	private Long solicitacoesParaAprovacao;
	private Long total;
	private Long avaliados;
	private Long paraAprovacao;
	private RapServidores servidorLogado;
	private boolean habilitarBotao;
	
	@PostConstruct
	protected void iniciarConversacao() {
		begin(conversation);
	}
	
	public void iniciar() {
		
		try {
			this.respAvaliacao = DominioIndRespAvaliacao.C;
			this.servidorLogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			if (this.servidorLogado != null && this.servidorLogado.getId() != null) {
				this.nomeProfissional = this.registroColaboradorFacade.obterNomePessoaServidor(
						this.servidorLogado.getId().getVinCodigo(), this.servidorLogado.getId().getMatricula());
			}
			if (this.nomeProfissional != null) {
				obterContagemTotal();
				this.habilitarBotao = true;
			} else {
				// ON01
				apresentarMsgNegocio(Severity.ERROR, MS01);
				this.habilitarBotao = false;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void obterContagemTotal() {
		if (this.servidorLogado != null && this.servidorLogado.getId() != null) {
			Object[] retorno = this.prescricaoMedicaFacade.obterContagemTotais(
					this.servidorLogado.getId().getMatricula(), 
					this.servidorLogado.getId().getVinCodigo(), 
					this.respAvaliacao);
			if (retorno != null) {
				this.solicitacoesParaAprovacao = (Long) retorno[0];
				this.total = (Long) retorno[1];
				this.avaliados = (Long) retorno[2];
				this.paraAprovacao = this.total - this.avaliados;
			}
		}
	}

	public void aprovarLote() {
		try {
			this.prescricaoMedicaFacade.aprovarLote(
					this.servidorLogado.getId().getMatricula(), this.servidorLogado.getId().getVinCodigo(), this.respAvaliacao);
			apresentarMsgNegocio(Severity.INFO, MS02);
			obterContagemTotal();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (PersistenceException e) {
			apresentarMsgNegocio(Severity.ERROR, MS03);
		}
	}

	/**
	 * 
	 * Gets and Sets 
	 * 
	 */
	public DominioIndRespAvaliacao getRespAvaliacao() {
		return respAvaliacao;
	}

	public void setRespAvaliacao(DominioIndRespAvaliacao respAvaliacao) {
		this.respAvaliacao = respAvaliacao;
	}

	public String getNomeProfissional() {
		return nomeProfissional;
	}

	public void setNomeProfissional(String nomeProfissional) {
		this.nomeProfissional = nomeProfissional;
	}

	public Long getSolicitacoesParaAprovacao() {
		return solicitacoesParaAprovacao;
	}

	public void setSolicitacoesParaAprovacao(Long solicitacoesParaAprovacao) {
		this.solicitacoesParaAprovacao = solicitacoesParaAprovacao;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getAvaliados() {
		return avaliados;
	}

	public void setAvaliados(Long avaliados) {
		this.avaliados = avaliados;
	}

	public Long getParaAprovacao() {
		return paraAprovacao;
	}

	public void setParaAprovacao(Long paraAprovacao) {
		this.paraAprovacao = paraAprovacao;
	}

	public boolean isHabilitarBotao() {
		return habilitarBotao;
	}

	public void setHabilitarBotao(boolean habilitarBotao) {
		this.habilitarBotao = habilitarBotao;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
}
