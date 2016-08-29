package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.equipamento.TelaEquipamentoVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterEquipamentoUtilCirgController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = 3241608251279698875L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private TelaEquipamentoVO tela;
	
	private Integer crgSeq;
	private Short quantidade;
	private Integer crgSeqParaExclusao;
	private Short euuSeqParaExclusao;
	private String usuarioLogado;

	private String voltarPara;
	
	public List<MbcEquipamentoCirurgico> buscaEquipamentosCirurgicos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoFacade.buscaEquipamentosCirurgicos(objPesquisa),buscaEquipamentosCirurgicosCount(objPesquisa));
	}
	
	public Long buscaEquipamentosCirurgicosCount(String objPesquisa){
		return this.blocoCirurgicoFacade.buscaEquipamentosCirurgicosCount(objPesquisa);
	}
	
	public void iniciar() {
		this.usuarioLogado = this.obterLoginUsuarioLogado();
		MbcCirurgias cirurgia = this.blocoCirurgicoFacade.obterCirurgiaComUnidadePacienteDestinoPorCrgSeq(crgSeq);
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(cirurgia.getPaciente().getCodigo());
		//???? -> Pelo relacionamento, dentro de paciente jah tem um leito
		//...precisaria realmente chamar a procedure como descrito no documento de analise ???
		this.getTela().setCirurgia(cirurgia);
		this.getTela().getCirurgia().setPaciente(paciente);
		this.getTela().setLocalizacao(this.blocoCirurgicoFacade.obterLeitoComoString(cirurgia.getPaciente().getCodigo()));
		
		try {
			List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirg = this.blocoCirurgicoFacade
			.pesquisarMbcEquipamentoUtilCirgPorCirurgia(this.crgSeq);
			this.getTela().setListaMbcEquipamentoUtilCirg(listaMbcEquipamentoUtilCirg);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
		}
	}
	
	
	//@Restrict("#{s:hasPermission('registroEquipamentoCirurgia','executar')}")
	public void adicionarEquipamentoUtilCirgs(){
		try {
			Boolean existeNaLista = this.blocoCirurgicoFacade.atualizarQuantidadeSeExistirMbcEquipamentoCirurgicoNaListaMbcEquipamentoutilCirg(
					this.getTela().getMbcEquipamentoCirurgicoSelecionadoNaSuggestion(), 
					this.getTela().getListaMbcEquipamentoUtilCirg(), this.getQuantidade());
			
			if(!existeNaLista){
				MbcEquipamentoUtilCirg novo = this.blocoCirurgicoFacade.getMbcEquipamentosUtilCirgsPorMbcEquipamentoCirurgico(
						this.getTela().getMbcEquipamentoCirurgicoSelecionadoNaSuggestion(), 
						this.getQuantidade());
				this.getTela().getListaMbcEquipamentoUtilCirg().add(novo);
			}
						
			apresentarMsgNegocio(Severity.INFO,"MBCEQUIPAMENTO_UTIL_CIRG_INSERIDOS_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		this.limpar();
	}
	
	//@Restrict("#{s:hasPermission('registroEquipamentoCirurgia','executar')}")
	public void gravar(){
		MbcCirurgias cirurgia = this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(this.crgSeq);
		try {
			this.blocoCirurgicoFacade.persistirListaMbcEquipamentoUtilCirg(
					this.getTela().getListaMbcEquipamentoUtilCirg(),
					cirurgia);			
			this.limpar();
			this.iniciar();
			apresentarMsgNegocio(Severity.INFO,"MBCEQUIPAMENTO_UTIL_CIRG_GRAVADO_SUCESSO");		
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	//@Restrict("#{s:hasPermission('registroEquipamentoCirurgia','executar')}")
	public void excluirEquipamento(){
		try {
			this.blocoCirurgicoFacade.excluirListaMbcEquipamentoUtilCirgPorMbcCirurgiaEEquipamentoCirurgico(this.crgSeqParaExclusao, this.euuSeqParaExclusao);
			this.iniciar();
			apresentarMsgNegocio(Severity.INFO,"MBCEQUIPAMENTO_UTIL_CIRG_EQUIPAMENTO_EXCLUIDO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	//@Restrict("#{s:hasPermission('registroEquipamentoCirurgia','executar')}")
	public void gerarNovaListaMbcEquipamentosUtilCirg(){
		try {
			this.blocoCirurgicoFacade.excluirListaMbcEquipamentoUtilCirgPorMbcCirurgiaEEquipamentoCirurgico(this.tela.getCirurgia().getSeq(), null);
			
			List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirg = this.blocoCirurgicoFacade
					.pesquisarMbcEquipamentoUtilCirgPorCirurgia(this.tela.getCirurgia().getSeq());
			this.tela.setListaMbcEquipamentoUtilCirg(listaMbcEquipamentoUtilCirg);
			apresentarMsgNegocio(Severity.INFO,"MBCEQUIPAMENTO_UTIL_CIRG_NOVA_LISTA_GERADA_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		this.limpar();
	}
	
	protected void limpar(){
		this.tela.setMbcEquipamentoCirurgicoSelecionadoNaSuggestion(null);
		this.setQuantidade(null);
	}
	
	public String cancelar() {
		if (!StringUtils.isEmpty(voltarPara)) {
			return voltarPara;
		} else {
			return "telaEquipamento";
		}
	}
	//========================================================================
	//====================== GETTERS E SETTERS ===============================
	//========================================================================
	
	public TelaEquipamentoVO getTela() {
		if(tela == null){
			this.tela = new TelaEquipamentoVO();
		}
		return tela;
	}

	public void setTela(TelaEquipamentoVO tela) {
		this.tela = tela;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getCrgSeqParaExclusao() {
		return crgSeqParaExclusao;
	}

	public void setCrgSeqParaExclusao(Integer crgSeqParaExclusao) {
		this.crgSeqParaExclusao = crgSeqParaExclusao;
	}

	public Short getEuuSeqParaExclusao() {
		return euuSeqParaExclusao;
	}

	public void setEuuSeqParaExclusao(Short euuSeqParaExclusao) {
		this.euuSeqParaExclusao = euuSeqParaExclusao;
	}

	public String getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}
