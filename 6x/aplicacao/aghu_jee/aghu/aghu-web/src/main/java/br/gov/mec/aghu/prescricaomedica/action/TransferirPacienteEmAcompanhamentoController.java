package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class TransferirPacienteEmAcompanhamentoController extends ActionController {

	private static final long serialVersionUID = -4898846495059037204L;

	public enum TransferirPacienteEmAcompanhamentoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TRANSFERIR_PACIENTES_CONFIRMA,
		MENSAGEM_TRANSFERIR_PACIENTES_TOTAL,
		MENSAGEM_TRANSFERIR_PACIENTES_NENHUM,
		MENSAGEM_TRANSFERIR_PACIENTES_NAO_PERMITIDO;
	}

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;	

	private RapServidoresVO profissionaisDe;
	private RapServidoresVO profissionaisPara;
	private String mensagemConfirmacao;
	private RapServidores servidorLogado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
		servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		if(servidorLogado != null){
			this.profissionaisDe = registroColaboradorFacade.pesquisarProfissionalPorServidor(servidorLogado);
		}
	}

	public void limpar() {
		this.profissionaisPara = null;
	}
	
	public void transferirPaciente() {
		Integer total = 0;
		try {
			total = prescricaoMedicaFacade.transferirPacienteEmAcompanhamento(this.profissionaisDe, this.profissionaisPara);
			if(total > 0){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TRANSFERIR_PACIENTES_TOTAL", total);
			}else{
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_TRANSFERIR_PACIENTES_NENHUM");
			}
			limpar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void modalTransferirPaciente() {
		this.mensagemConfirmacao = super.getBundle()
				.getString("MENSAGEM_TRANSFERIR_PACIENTES_CONFIRMA")
				.replace("{0}", this.profissionaisDe.getNome())
				.replace("{1}", this.profissionaisPara.getNome());
		
		if(prescricaoMedicaFacade.validarMesmoProfissionalPorVinculoEMatricula(this.profissionaisDe, this.profissionaisPara)){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_TRANSFERIR_PACIENTES_NAO_PERMITIDO");
			return;
		}
		openDialog("modalTransferirPacienteWG");
	}
	
	public List<RapServidoresVO> pesquisarProfissionais(String strPesquisa) {
		return this.returnSGWithCount(
			registroColaboradorFacade.pesquisarProfissionaisPorVinculoMatriculaNome(strPesquisa, 100),
			registroColaboradorFacade.pesquisarProfissionaisPorVinculoMatriculaNomeCount(strPesquisa, null)
		);		
	}
	
	public RapServidoresVO getProfissionaisDe() {
		return profissionaisDe;
	}

	public void setProfissionaisDe(RapServidoresVO profissionaisDe) {
		this.profissionaisDe = profissionaisDe;
	}

	public RapServidoresVO getProfissionaisPara() {
		return profissionaisPara;
	}

	public void setProfissionaisPara(RapServidoresVO profissionaisPara) {
		this.profissionaisPara = profissionaisPara;
	}

	public String getMensagemConfirmacao() {
		return mensagemConfirmacao;
	}

	public void setMensagemConfirmacao(String mensagemConfirmacao) {
		this.mensagemConfirmacao = mensagemConfirmacao;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
}
