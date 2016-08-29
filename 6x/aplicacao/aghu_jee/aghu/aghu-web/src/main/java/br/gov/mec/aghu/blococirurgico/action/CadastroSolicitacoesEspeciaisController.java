package br.gov.mec.aghu.blococirurgico.action;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirgId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroSolicitacoesEspeciaisController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = 3661785096909101631L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	/*Cirurgia*/
	private MbcCirurgias cirurgia;
	
	/*Solicitação Especial*/
	private MbcSolicitacaoEspExecCirg solicitacaoEspecial;
	
	/*Lista de solicitações cadastradas*/
	private List<MbcSolicitacaoEspExecCirg> listaSolicEspeciais;
	
	/*Localização*/
	private String localizacao;
	
	/*Unidade que será comunicada*/
	private String unidadeComunicada;
	
	/*Prontuario Formatado*/
	private String prontuarioFormatado;
	
	/**
	 * Codigo da cirurgia, obtido via page parameter.
	 */
	private Integer mbcCirurgiaCodigo;
	private String voltarPara;
	
	private boolean emEdicao;
	private Boolean descricaoObrigatoria;
	private static final String AGENDA_PROCEDIMENTOS = "blococirurgico-agendaProcedimentos";

	
	public void inicio() {
		unidadeComunicada = "";
		if (this.mbcCirurgiaCodigo != null) {
			this.cirurgia = this.blocoCirurgicoFacade
					.obterCirurgiaPorSeq(mbcCirurgiaCodigo);
			
			this.emEdicao = false;
			atualizarLista(mbcCirurgiaCodigo);
			this.localizacao = this.blocoCirurgicoFacade.obterQuarto(this.cirurgia.getPaciente().getCodigo());
			this.prontuarioFormatado = getProntuarioFormatado(this.cirurgia.getPaciente().getProntuario());
			
			solicitacaoEspecial = new MbcSolicitacaoEspExecCirg();
			solicitacaoEspecial.setMbcCirurgias(cirurgia);
		}
	}
	
	
	/**
	 * Atualiza a lista de anotações
	 * @param seq
	 */
	private void atualizarLista(Integer crgSeq) {
		this.listaSolicEspeciais = this.blocoCirurgicoFacade.pesquisarSolicitacaoEspecialidadePorCrgSeq(crgSeq);
	}
	
	public List<MbcNecessidadeCirurgica> pesquisarNecessidade(String objParam) {
		return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.buscarNecessidadeCirurgicaPorCodigoOuDescricao((String) objParam, true),pesquisarNecessidadeCount(objParam));
	}
	
	public Long pesquisarNecessidadeCount(String objParam) {
		return this.blocoCirurgicoCadastroApoioFacade.countBuscarNecessidadeCirurgicaPorCodigoOuDescricao((String) objParam, true);
	}
	
	public void carregarDescricaoUnf() {
		descricaoObrigatoria = this.solicitacaoEspecial.getMbcNecessidadeCirurgica().getIndExigeDescSolic();
		if(this.solicitacaoEspecial.getMbcNecessidadeCirurgica().getAghUnidadesFuncionais() != null) {
			unidadeComunicada = this.solicitacaoEspecial.getMbcNecessidadeCirurgica().getAghUnidadesFuncionais().getDescricao();
		} else {
			unidadeComunicada = null;
		}
	}
	
	public void carregarDescricaoUnf(boolean existeDescricao) {
		descricaoObrigatoria = this.solicitacaoEspecial.getMbcNecessidadeCirurgica().getIndExigeDescSolic();
		if(existeDescricao) {
			unidadeComunicada = this.solicitacaoEspecial.getMbcNecessidadeCirurgica().getAghUnidadesFuncionais().getDescricao();
		} else {
			unidadeComunicada = null;
		}
	}
	
	public void removerDescricaoUnf() {
		descricaoObrigatoria = false;
		unidadeComunicada = null;
	}
	
	public boolean isSolicEspEmEdicao(MbcSolicitacaoEspExecCirgId id) {
		return this.solicitacaoEspecial != null && this.solicitacaoEspecial.getId() != null
			&& this.solicitacaoEspecial.getId().equals(id);
	}
	
	public void editarSolicEsp(MbcSolicitacaoEspExecCirgId id) {
 		this.emEdicao = true;
 		boolean existeDescricao = true;
		this.solicitacaoEspecial = this.blocoCirurgicoFacade.pesquisarMbcSolicitacaoEspNessidadeCirurgicaEUnidadeFuncional(id);
		if(solicitacaoEspecial == null){
			solicitacaoEspecial = this.blocoCirurgicoFacade.obterMbcSolicitacaoEspExecCirgPorChavePrimaria(id, new  Enum[] {MbcSolicitacaoEspExecCirg.Fields.MBC_NECESSIDADE_CIRURGICAS }, null );
			existeDescricao = false;
		}
		
		carregarDescricaoUnf(existeDescricao);
	}
	
	public void cancelarSolicEsp() {
		this.solicitacaoEspecial = new MbcSolicitacaoEspExecCirg();
		this.solicitacaoEspecial.setMbcCirurgias(cirurgia);
		this.emEdicao = false;
		removerDescricaoUnf();
	}
	
	private String getProntuarioFormatado(Integer prontuario) {
		return CoreUtil.formataProntuario(prontuario);
	}
	
	public void confirmar() {
		try {
			boolean create = this.solicitacaoEspecial.getId() == null;
			
			if (create) {
				Short nciSeq = this.solicitacaoEspecial.getMbcNecessidadeCirurgica().getSeq();
				this.blocoCirurgicoFacade.verificarSolicEspExistente(mbcCirurgiaCodigo, nciSeq);
				this.blocoCirurgicoFacade.persistirSolicitacaoEspecial(this.solicitacaoEspecial);
				
			
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_SOLICITACAO_ESPECIAL");
			} else {
				this.solicitacaoEspecial.setMbcCirurgias(cirurgia);
				this.blocoCirurgicoFacade.atualizarSolicitacaoEspecial(this.solicitacaoEspecial);
				
				
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_SOLICITACAO_ESPECIAL");
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		limpar();
	}
	
	
	public void excluir(Integer crgSeqExc, Short nciSeqExc, Short seqpExc) {
		
		try {
			this.blocoCirurgicoFacade.excluirSolicitacaoEspecial(crgSeqExc, nciSeqExc, seqpExc);
			
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_EXCLUSAO_SOLICITACAO_ESPECIAL");
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		limpar();
	}
	
	public void limpar() {
		removerDescricaoUnf();
		this.emEdicao = false;
		this.solicitacaoEspecial = new MbcSolicitacaoEspExecCirg();
		this.solicitacaoEspecial.setMbcCirurgias(cirurgia);
		atualizarLista(mbcCirurgiaCodigo);
	}
	
	public String voltar() {
		if(this.voltarPara != null){
			return this.voltarPara;	
		}
		return AGENDA_PROCEDIMENTOS; // Retorno padrão
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getUnidadeComunicada() {
		return unidadeComunicada;
	}

	public void setUnidadeComunicada(String unidadeComunicada) {
		this.unidadeComunicada = unidadeComunicada;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public MbcSolicitacaoEspExecCirg getSolicitacaoEspecial() {
		return solicitacaoEspecial;
	}
	
	public void setSolicitacaoEspecial(MbcSolicitacaoEspExecCirg solicitacaoEspecial) {
		this.solicitacaoEspecial = solicitacaoEspecial;
	}
	
	public List<MbcSolicitacaoEspExecCirg> getListaSolicEspeciais() {
		return listaSolicEspeciais;
	}

	public void setListaSolicEspeciais(
			List<MbcSolicitacaoEspExecCirg> listaSolicEspeciais) {
		this.listaSolicEspeciais = listaSolicEspeciais;
	}

	public Integer getMbcCirurgiaCodigo() {
		return mbcCirurgiaCodigo;
	}

	public void setMbcCirurgiaCodigo(Integer mbcCirurgiaCodigo) {
		this.mbcCirurgiaCodigo = mbcCirurgiaCodigo;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Boolean getDescricaoObrigatoria() {
		return descricaoObrigatoria;
	}

	public void setDescricaoObrigatoria(Boolean descricaoObrigatoria) {
		this.descricaoObrigatoria = descricaoObrigatoria;
	}

	public String getVoltarPara() {
		return voltarPara;
	}
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}