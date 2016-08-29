package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class CabecalhoAnamneseEvolucaoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 14350067867867L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	
	private Integer seqAtendimento;
	private AghAtendimentos atendimento;
	private String nomeProfissional;
	private String prontuarioFormatado;
	
	public void iniciar() {
		if (this.seqAtendimento != null) {
			this.atendimento = this.aghuFacade.obterAghAtendimentosAnamneseEvolucao(this.seqAtendimento);
			this.nomeProfissional = this.atendimento.getServidor().getPessoaFisica().getNome();
			this.prontuarioFormatado = CoreUtil.formataProntuario(this.atendimento.getPaciente().getProntuario());
			if (this.atendimento != null && this.atendimento.getServidor() != null) {
				RapPessoasFisicas rapPessoaFisica = registroColaboradorFacade.obterRapPessoasFisicasPorServidor(this.atendimento.getServidor().getId());
				this.nomeProfissional =  rapPessoaFisica.getNome();
			}
		}
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}
	
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public String getNomeProfissional() {
		return nomeProfissional;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}	
	
}
