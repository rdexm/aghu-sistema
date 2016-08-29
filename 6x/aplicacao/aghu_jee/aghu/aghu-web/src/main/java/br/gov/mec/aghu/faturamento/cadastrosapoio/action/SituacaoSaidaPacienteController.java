package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPacienteId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controle da tela Cadastrar Situação de Saída de Pacientes
 * 
 * @author rafael.silvestre
 */
public class SituacaoSaidaPacienteController extends ActionController {

	private static final long serialVersionUID = 4587268071784006688L;
	
	private static final String PAGE_DETALHAR_MOTIVO_SAIDA_PACIENTE = "faturamento-situacaoSaidaPacienteList";
	
	private static final String MENSAGEM_SUCESSO_EDICAO = "MENSAGEM_SUCESSO_EDICAO_SITUACAO_SAIDA_PACIENTE";
	
	private static final String MENSAGEM_SUCESSO_INCLUSAO = "MENSAGEM_SUCESSO_INCLUSAO_SITUACAO_SAIDA_PACIENTE";
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	private FatSituacaoSaidaPaciente entity;
	
	private Short seqMotivoSaidaPaciente;

	private Boolean situacao;
	
	private boolean modoEdicao;
	
	/**
	 * Inicializar
	 */
	@PostConstruct
	protected void inicializar() {
		
		begin(conversation);
	}
	
	/**
	 * Valida se a tela foi acessada para edição ou inclusão de um registro
	 */
	public void iniciar() {
	
		if (super.isValidInitMethod()) {
		
			limpar();
		}
	}

	/**
	 * Preenche os valores dos campos conforme caso de inclusão ou alteração
	 */
	private void limpar() {
		
		if (this.modoEdicao) {
			
			this.situacao = this.entity.getSituacao().isAtivo();
			
		} else {
			
			this.situacao = true;
			this.entity = new FatSituacaoSaidaPaciente();
			this.entity.setId(new FatSituacaoSaidaPacienteId());
			this.entity.getId().setMspSeq(this.seqMotivoSaidaPaciente);
		}
	}
	
	/**
	 * Ação do botão Gravar.
	 */
	public void gravar() {
		
		this.entity.setSituacao(DominioSituacao.getInstance(this.situacao));
		
		try {
			
			this.faturamentoApoioFacade.gravarSituacaoSaidaPaciente(this.entity);

			if (this.modoEdicao) {
				
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO);
				
			} else {
			
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_INCLUSAO);
			}
			
			limpar();
			
		} catch (ApplicationBusinessException e) {
			
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método responsável pela navegação para a página anterior.
	 * 
	 * @return
	 */
	public String voltar() {
		
		this.entity = null;
		this.situacao = true;
		this.modoEdicao = false;
		
		return PAGE_DETALHAR_MOTIVO_SAIDA_PACIENTE;
	}

	/**
	 * 
	 * GET's and SET's 
	 * 
	 */
	public FatSituacaoSaidaPaciente getEntity() {
		return entity;
	}

	public void setEntity(FatSituacaoSaidaPaciente entity) {
		this.entity = entity;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Short getSeqMotivoSaidaPaciente() {
		return seqMotivoSaidaPaciente;
}

	public void setSeqMotivoSaidaPaciente(Short seqMotivoSaidaPaciente) {
		this.seqMotivoSaidaPaciente = seqMotivoSaidaPaciente;
	}

}
